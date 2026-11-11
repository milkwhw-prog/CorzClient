package com.corz.client.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.ConnectionTimeUtils;

public final class ServerPacketEventQueue {
   private static final byte TOTEM_STATUS_BYTE = 35;
   public static final String UNKNOWN_DAMAGE_TYPE = "?";
   private final AtomicInteger queuedEventCount;
   private final ConcurrentLinkedQueue<Object> pendingEvents = new ConcurrentLinkedQueue<>();

   private void enqueueEvent(Object obj) {
      if (this.queuedEventCount.incrementAndGet() > 2048) {
         this.queuedEventCount.decrementAndGet();
      } else {
         this.pendingEvents.add(obj);
      }
   }

   public void clearPendingEvents() {
      while (this.pendingEvents.poll() != null) {
         this.queuedEventCount.decrementAndGet();
      }
   }

   static boolean isRelevantBlockState(BlockState blockState) {
      return blockState == null
         ? false
         : blockState.isAir()
            || blockState.isOf(Blocks.RESPAWN_ANCHOR)
            || blockState.isOf(Blocks.GLOWSTONE)
            || blockState.isOf(Blocks.OBSIDIAN)
            || blockState.isOf(Blocks.CRYING_OBSIDIAN);
   }

   public ServerPacketEventQueue() {
      this.queuedEventCount = new AtomicInteger();
   }

   private void queueEventFromPacket(Packet<?> packet) {
      if (packet != null) {
         if (packet instanceof EntityStatusS2CPacket entityStatusS2CPacket) {
            if (entityStatusS2CPacket.getStatus() == 35) {
               this.enqueueEvent(entityStatusS2CPacket);
            }
         } else if (packet instanceof EntityDamageS2CPacket entityDamageS2CPacket) {
            this.enqueueEvent(
               new ServerPacketEventQueue.EntityDamage(
                  entityDamageS2CPacket.entityId(), entityDamageS2CPacket.sourceCauseId(), entityDamageS2CPacket.sourceDirectId(), entityDamageS2CPacket.sourcePosition().orElse(null), getDamageTypeName(entityDamageS2CPacket.sourceType())
               )
            );
         } else if (packet instanceof EntitySpawnS2CPacket entitySpawnS2CPacket) {
            EntityType entityType = entitySpawnS2CPacket.getEntityType();
            if (entityType == EntityType.END_CRYSTAL || entityType == EntityType.ENDER_PEARL) {
               this.enqueueEvent(new ServerPacketEventQueue.Spawn(entitySpawnS2CPacket.getEntityId(), entityType, entitySpawnS2CPacket.getX(), entitySpawnS2CPacket.getY(), entitySpawnS2CPacket.getZ(), entitySpawnS2CPacket.getEntityData()));
            }
         } else if (packet instanceof EntitiesDestroyS2CPacket entitiesDestroyS2CPacket) {
            this.enqueueEvent(new ServerPacketEventQueue.Destroy(entitiesDestroyS2CPacket.getEntityIds().toIntArray()));
         } else if (packet instanceof ExplosionS2CPacket explosionS2CPacket) {
            this.enqueueEvent(new ServerPacketEventQueue.Boom(explosionS2CPacket.center(), explosionS2CPacket.radius()));
         } else if (packet instanceof EntityAnimationS2CPacket entityAnimationS2CPacket) {
            if (entityAnimationS2CPacket.getAnimationId() == 0) {
               this.enqueueEvent(new ServerPacketEventQueue.Swing(entityAnimationS2CPacket.getEntityId()));
            }
         } else if (packet instanceof WorldTimeUpdateS2CPacket) {
            this.enqueueEvent(new ServerPacketEventQueue.TimeUpdate(ConnectionTimeUtils.getTimeTimeMillis()));
         } else if (packet instanceof BlockUpdateS2CPacket blockUpdateS2CPacket) {
            this.queueBlockChange(blockUpdateS2CPacket.getPos(), blockUpdateS2CPacket.getState());
         } else if (packet instanceof ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket) {
            chunkDeltaUpdateS2CPacket.visitUpdates(this::queueBlockChange);
         } else if (packet instanceof BlockBreakingProgressS2CPacket blockBreakingProgressS2CPack && blockBreakingProgressS2CPack.getPos() != null) {
            this.enqueueEvent(new ServerPacketEventQueue.Dig(blockBreakingProgressS2CPack.getEntityId(), blockBreakingProgressS2CPack.getPos().toImmutable(), blockBreakingProgressS2CPack.getProgress()));
         }
      }
   }

   public int drainEvents(World world, Consumer<ServerPacketEventQueue.Event> consumer, int atomicConcurrentLimit) {
      if (consumer == null) {
         return 0;
      } else {
         int atomicConcurrent = 0;

         while (atomicConcurrent < atomicConcurrentLimit) {
            Object concurrentlinkedqueue = this.pendingEvents.poll();
            if (concurrentlinkedqueue == null) {
               break;
            }

            this.queuedEventCount.decrementAndGet();
            ServerPacketEventQueue.Event entity = toEvent(world, concurrentlinkedqueue);
            if (entity != null) {
               consumer.accept(entity);
               atomicConcurrent++;
            }
         }

         return atomicConcurrent;
      }
   }

   static String getDamageTypeName(RegistryEntry<DamageType> registryEntry) {
      return registryEntry == null ? "?" : registryEntry.getKey().map(registryKey -> registryKey.getValue().getPath()).orElse("?");
   }

   public static boolean isOutsideZeroToNine(int cpacketEntityLimit) {
      return cpacketEntityLimit < 0 || cpacketEntityLimit > 9;
   }

   public void onPacketReceived(Packet<?> packet) {
      if (!(packet instanceof BundlePacket bundlePacket)) {
         this.queueEventFromPacket(packet);
      } else {
         for (Packet packet3 : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
            this.queueEventFromPacket(packet3);
         }
      }
   }

   private void queueBlockChange(BlockPos pos2, BlockState blockState) {
      if (pos2 != null && isRelevantBlockState(blockState)) {
         this.enqueueEvent(new ServerPacketEventQueue.BlockChange(pos2.toImmutable(), blockState));
      }
   }

   private static ServerPacketEventQueue.Event toEvent(World world, Object obj) {
      if (obj instanceof ServerPacketEventQueue.Event entity) {
         return entity;
      } else if (obj instanceof EntityStatusS2CPacket entityStatusS2CPacket) {
         if (world == null) {
            return null;
         } else {
            Entity entity2 = entityStatusS2CPacket.getEntity(world);
            return entity2 == null ? null : new ServerPacketEventQueue.Pop(entity2.getId());
         }
      } else {
         return null;
      }
   }

   public record BlockChange(BlockPos pos, BlockState state) implements ServerPacketEventQueue.Event {
   }

   public record Boom(Vec3d center, float radius) implements ServerPacketEventQueue.Event {
   }

   public record Destroy(int[] ids) implements ServerPacketEventQueue.Event {
   }

   public record Dig(int breakerId, BlockPos pos, int stage) implements ServerPacketEventQueue.Event {
   }

   public sealed interface Event
      permits ServerPacketEventQueue.Pop,
      ServerPacketEventQueue.EntityDamage,
      ServerPacketEventQueue.Spawn,
      ServerPacketEventQueue.Destroy,
      ServerPacketEventQueue.Boom,
      ServerPacketEventQueue.Swing,
      ServerPacketEventQueue.TimeUpdate,
      ServerPacketEventQueue.BlockChange,
      ServerPacketEventQueue.Dig {
   }

   public record EntityDamage(int entityId, int causeId, int directId, Vec3d source, String type) implements ServerPacketEventQueue.Event {
      public EntityDamage(int causeId, int entityId, int directId, Vec3d source) {
         this(causeId, entityId, directId, source, "?");
      }
   }

   public record Pop(int entityId) implements ServerPacketEventQueue.Event {
   }

   public record Spawn(int entityId, EntityType<?> type, double x, double y, double z, int data) implements ServerPacketEventQueue.Event {
   }

   public record Swing(int entityId) implements ServerPacketEventQueue.Event {
   }

   public record TimeUpdate(long atMs) implements ServerPacketEventQueue.Event {
   }
}
