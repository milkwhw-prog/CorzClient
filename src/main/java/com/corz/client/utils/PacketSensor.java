package com.corz.client.utils;

import com.corz.client.config.FriendNameStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import com.corz.client.utils.ChunkLightSnapshot;
import com.corz.client.utils.LightPacketCache;
import com.corz.client.utils.SectionStatus;
import com.corz.client.categories.donutsmp.StaffList;
import com.corz.client.utils.Sounds;
import com.corz.client.utils.Clue;
import com.corz.client.utils.SiteBook;

public final class PacketSensor {
   private final ConcurrentLinkedQueue<PacketSensor.Event> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private final Map<UUID, Long> predicatePositionMap;
   private final Set<UUID> predicateUuidSet;
   private final Map<UUID, Long> idsBlockedPositionMap;
   private ClientWorld world;
   static final BiPredicate<UUID, String> BI_PREDICATE = PacketSensor::canDaveCrypto;
   private final Map<UUID, PacketSensor.Seen> idsBlockedSeenMap;
   private int profileCheckTicks;
   private final Map<Integer, PacketSensor.Seen> predicateSeenMap;
   private final Map<String, Long> rootPositionMap;
   private final List<PacketSensor.Arrival> predicateArrivalList;
   private final AtomicInteger atomicInteger = new AtomicInteger();

   private void handleAtomicConcurrent(PacketSensor.Event event) {
      this.atomicInteger.incrementAndGet();
      this.concurrentLinkedQueue.offer(event);
   }

   static String formatMultiplayerLeft(Text text2) {
      if (!(text2 != null && text2.getContent() instanceof TranslatableTextContent translatableTextContent)) {
         return null;
      } else if ("multiplayer.player.left".equals(translatableTextContent.getKey()) && translatableTextContent.getArgs().length != 0) {
         Object obj = translatableTextContent.getArgs()[0];
         return obj instanceof Text text3 ? text3.getString() : String.valueOf(obj);
      } else {
         return null;
      }
   }

   public void handleEventIds(Packet<?> packet) {
      if (this.atomicInteger.get() < 4096) {
         long currentMillis = System.currentTimeMillis();
         if (packet instanceof PlaySoundS2CPacket playSoundS2CPacket) {
            String eventIdsText;
            try {
               eventIdsText = playSoundS2CPacket.getSound().value().id().getPath();
            } catch (Throwable e) {
               return;
            }

            boolean eventIdsFlag = playSoundS2CPacket.getCategory() == SoundCategory.BLOCKS;
            if (Sounds.getFenceTrapdoor(eventIdsText, eventIdsFlag) == null) {
               return;
            }

            this.handleAtomicConcurrent(
               new PacketSensor.Event(PacketSensor.Kind.SOUND, eventIdsText, eventIdsFlag ? 1 : 0, 0, playSoundS2CPacket.getX(), playSoundS2CPacket.getY(), playSoundS2CPacket.getZ(), null, null, currentMillis)
            );
         } else if (packet instanceof WorldEventS2CPacket worldEventS2CPacket) {
            if (Sounds.getWorkstationMining(worldEventS2CPacket.getEventId()) == null) {
               return;
            }

            if (worldEventS2CPacket.getEventId() == 2001 && canGrassRaw(worldEventS2CPacket.getData())) {
               return;
            }

            BlockPos pos = worldEventS2CPacket.getPos();
            this.handleAtomicConcurrent(
               new PacketSensor.Event(
                  PacketSensor.Kind.WORLD_EVENT,
                  null,
                  worldEventS2CPacket.getEventId(),
                  worldEventS2CPacket.getData(),
                  pos.getX() + 0.5,
                  pos.getY() + 0.5,
                  pos.getZ() + 0.5,
                  null,
                  null,
                  currentMillis
               )
            );
         } else if (packet instanceof BlockEventS2CPacket blockEventS2CPacket) {
            Clue chestPiston = getChestPiston(blockEventS2CPacket.getBlock(), blockEventS2CPacket.getType(), blockEventS2CPacket.getData());
            if (chestPiston == null) {
               return;
            }

            BlockPos pos2 = blockEventS2CPacket.getPos();
            this.handleAtomicConcurrent(
               new PacketSensor.Event(
                  PacketSensor.Kind.BLOCK_EVENT, chestPiston.name(), 0, 0, pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5, null, null, currentMillis
               )
            );
         } else if (packet instanceof EntitiesDestroyS2CPacket entitiesDestroyS2CPacket) {
            this.handleAtomicConcurrent(
               new PacketSensor.Event(PacketSensor.Kind.DESTROY, null, 0, 0, 0.0, 0.0, 0.0, null, entitiesDestroyS2CPacket.getEntityIds().toIntArray(), currentMillis)
            );
         } else if (packet instanceof PlayerRemoveS2CPacket playerRemoveS2CPacket) {
            for (UUID uuid : playerRemoveS2CPacket.profileIds()) {
               this.handleAtomicConcurrent(new PacketSensor.Event(PacketSensor.Kind.REMOVE, null, 0, 0, 0.0, 0.0, 0.0, uuid, null, currentMillis));
            }
         } else if (packet instanceof PlayerListS2CPacket playerListS2CPacket && playerListS2CPacket.getActions().contains(Action.ADD_PLAYER)) {
            for (Entry entry : playerListS2CPacket.getEntries()) {
               this.handleAtomicConcurrent(new PacketSensor.Event(PacketSensor.Kind.JOIN, null, 0, 0, 0.0, 0.0, 0.0, entry.profileId(), null, currentMillis));
            }
         } else if (packet instanceof GameMessageS2CPacket gameMessageS2CPacket && !gameMessageS2CPacket.overlay()) {
            String formatMultiplayerLeft2 = formatMultiplayerLeft(gameMessageS2CPacket.content());
            if (formatMultiplayerLeft2 != null) {
               this.handleAtomicConcurrent(new PacketSensor.Event(PacketSensor.Kind.LEFT, formatMultiplayerLeft2, 0, 0, 0.0, 0.0, 0.0, null, null, currentMillis));
            }
         }
      }
   }

   private boolean canRoot(String rootText, long rootValue) {
      Long long2 = this.rootPositionMap.get(rootText.toLowerCase(Locale.ROOT));
      return long2 != null && Math.abs(rootValue - long2) <= 2000L;
   }

   private void handleLogoutPredicate(PacketSensor.Seen seen, SiteBook siteBook, long logoutPredicateValue) {
      if (!seen.blocked && !this.predicateUuidSet.contains(seen.id) && BI_PREDICATE.test(seen.id, seen.name)) {
         siteBook.canTimeJoin(
            Clue.LOGOUT,
            (int)Math.floor(seen.x),
            (int)Math.floor(seen.y),
            (int)Math.floor(seen.z),
            "logout:" + seen.id + ":" + logoutPredicateValue / 60000L,
            Clue.LOGOUT.clueWeight,
            seen.name + " logged out here",
            logoutPredicateValue
         );
      }
   }

   static boolean canGrassRaw(int grassRawValue) {
      try {
         BlockState stateFromRawId = Block.getStateFromRawId(grassRawValue);
         return stateFromRawId.getBlock() instanceof PlantBlock
            || stateFromRawId.isIn(BlockTags.LEAVES)
            || stateFromRawId.isIn(BlockTags.FLOWERS)
            || stateFromRawId.isIn(BlockTags.CROPS)
            || stateFromRawId.isOf(Blocks.SHORT_GRASS)
            || stateFromRawId.isOf(Blocks.TALL_GRASS)
            || stateFromRawId.isOf(Blocks.GRASS_BLOCK)
            || stateFromRawId.isOf(Blocks.SNOW)
            || stateFromRawId.isOf(Blocks.TURTLE_EGG)
            || stateFromRawId.isOf(Blocks.SUGAR_CANE)
            || stateFromRawId.isOf(Blocks.FARMLAND);
      } catch (Throwable e) {
         return false;
      }
   }

   private boolean canEntitiesWorld(PacketSensor.Event event) {
      if (this.world == null) {
         return false;
      } else {
         try {
            return !this.world
               .getEntitiesByClass(VillagerEntity.class, new Box(event.x - 4.0, event.y - 3.0, event.z - 4.0, event.x + 4.0, event.y + 3.0, event.z + 4.0), villagerEntity -> true)
               .isEmpty();
         } catch (Throwable e2) {
            return false;
         }
      }
   }

   void handlePredicate(int predicateValue, UUID uuid, String predicateText, double predicateValue2, double hashRegistryValue, double predicateValue4, long profilePlayersValue) {
      boolean predicateFlag = this.predicateUuidSet.contains(uuid) || !BI_PREDICATE.test(uuid, predicateText);
      if (predicateFlag && this.predicateUuidSet.size() < 4096) {
         this.predicateUuidSet.add(uuid);
      }

      Long long2 = this.predicatePositionMap.remove(uuid);
      if (long2 != null && profilePlayersValue - long2 <= 3000L && !predicateFlag) {
         this.predicateArrivalList.add(new PacketSensor.Arrival(uuid, predicateText, (int)Math.floor(predicateValue2), (int)Math.floor(hashRegistryValue), (int)Math.floor(predicateValue4), profilePlayersValue + 2000L));
      }

      this.predicateSeenMap.put(predicateValue, new PacketSensor.Seen(uuid, predicateText, predicateValue2, hashRegistryValue, predicateValue4, profilePlayersValue, predicateFlag));
   }

   static boolean canDaveCrypto(UUID uuid, String daveCryptoText) {
      try {
         if (daveCryptoText != null) {
            for (String daveCryptoText2 : StaffList.cryptoDaveTexts) {
               if (daveCryptoText2 != null && daveCryptoText2.trim().equalsIgnoreCase(daveCryptoText)) {
                  return false;
               }
            }
         }

         return StaffList.isStaffPlayer(uuid, daveCryptoText) ? false : daveCryptoText == null || !FriendNameStore.containsFriendIgnoreCase(daveCryptoText);
      } catch (Throwable e) {
         return false;
      }
   }

   void handleIdsBlocked(PacketSensor.Event event, double idsBlockedValue, double idsBlockedValue2, double idsBlockedValue3, SiteBook siteBook, long logoutPredicateValue) {
      switch (event.kind) {
         case SOUND:
            handleSpawner(Sounds.getFenceTrapdoor(event.name, event.a == 1), event, idsBlockedValue, idsBlockedValue2, idsBlockedValue3, siteBook, logoutPredicateValue);
            break;
         case WORLD_EVENT:
            if (event.a == 1500 && this.canEntitiesWorld(event)) {
               return;
            }

            handleSpawner(Sounds.getWorkstationMining(event.a), event, idsBlockedValue, idsBlockedValue2, idsBlockedValue3, siteBook, logoutPredicateValue);
            break;
         case BLOCK_EVENT:
            handleSpawner(Clue.valueOf(event.name), event, idsBlockedValue, idsBlockedValue2, idsBlockedValue3, siteBook, logoutPredicateValue);
            break;
         case DESTROY:
            for (int immutablePosValue : event.ids) {
               PacketSensor.Seen seen = this.predicateSeenMap.remove(immutablePosValue);
               if (seen != null) {
                  Long long2 = this.idsBlockedPositionMap.remove(seen.id);
                  if (long2 != null && Math.abs(event.ms - long2) <= 2000L) {
                     this.handleLogoutPredicate(seen, siteBook, logoutPredicateValue);
                  } else if (this.canRoot(seen.name, event.ms)) {
                     this.handleLogoutPredicate(seen, siteBook, logoutPredicateValue);
                  } else {
                     this.idsBlockedSeenMap.put(seen.id, new PacketSensor.Seen(seen.id, seen.name, seen.x, seen.y, seen.z, event.ms, seen.blocked));
                  }
               }
            }
            break;
         case REMOVE:
            PacketSensor.Seen seen2 = this.idsBlockedSeenMap.remove(event.id);
            if (seen2 != null && Math.abs(event.ms - seen2.ms) <= 2000L) {
               this.handleLogoutPredicate(seen2, siteBook, logoutPredicateValue);
            } else {
               this.idsBlockedPositionMap.put(event.id, event.ms);
            }

            this.predicateArrivalList.removeIf(arrival -> arrival.id.equals(event.id));
            break;
         case LEFT:
            this.rootPositionMap.put(event.name.toLowerCase(Locale.ROOT), event.ms);
            Iterator iterator = this.idsBlockedSeenMap.values().iterator();

            while (iterator.hasNext()) {
               PacketSensor.Seen packetSensorSeen = (PacketSensor.Seen)iterator.next();
               if (packetSensorSeen.name.equalsIgnoreCase(event.name) && Math.abs(event.ms - packetSensorSeen.ms) <= 2000L) {
                  iterator.remove();
                  this.handleLogoutPredicate(packetSensorSeen, siteBook, logoutPredicateValue);
               }
            }
            break;
         case JOIN:
            this.predicatePositionMap.put(event.id, event.ms);
      }
   }

   public void handleProfilePlayers(ClientWorld world, ClientPlayerEntity player, SiteBook siteBook, long profilePlayersValue) {
      this.world = world;
      double x = player.getX();
      double z = player.getZ();

      for (PlayerEntity playerEntity : world.getPlayers()) {
         if (playerEntity != player) {
            double profilePlayersFactor = playerEntity.getX() - x;
            double profilePlayersFactor2 = playerEntity.getZ() - z;
            if (!(profilePlayersFactor * profilePlayersFactor + profilePlayersFactor2 * profilePlayersFactor2 > 25600.0)) {
               this.handlePredicate(playerEntity.getId(), playerEntity.getUuid(), playerEntity.getGameProfile().name(), playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), profilePlayersValue);
            }
         }
      }

      this.handleLoginDue(siteBook, profilePlayersValue);
      int profilePlayersCounter = 512;

      PacketSensor.Event event;
      while (profilePlayersCounter-- > 0 && (event = this.concurrentLinkedQueue.poll()) != null) {
         this.atomicInteger.decrementAndGet();
         this.handleIdsBlocked(event, player.getX(), player.getY(), player.getZ(), siteBook, profilePlayersValue);
      }

      this.predicateSeenMap.values().removeIf(seen -> profilePlayersValue - seen.ms > 5000L);
      this.idsBlockedSeenMap.values().removeIf(seen2 -> profilePlayersValue - seen2.ms > 2000L);
      this.idsBlockedPositionMap.values().removeIf(nowx -> profilePlayersValue - nowx > 2000L);
      this.predicatePositionMap.values().removeIf(long2 -> profilePlayersValue - long2 > 3000L);
      this.rootPositionMap.values().removeIf(long3 -> profilePlayersValue - long3 > 2000L);
      if (++this.profileCheckTicks % 40 == 0) {
         this.handleEspSection(player, siteBook, profilePlayersValue);
      }
   }

   void handleLoginDue(SiteBook siteBook, long loginDueLimit) {
      Iterator iterator = this.predicateArrivalList.iterator();

      while (iterator.hasNext()) {
         PacketSensor.Arrival arrival = (PacketSensor.Arrival)iterator.next();
         if (loginDueLimit >= arrival.due) {
            iterator.remove();
            if (!this.predicateUuidSet.contains(arrival.id) && BI_PREDICATE.test(arrival.id, arrival.name)) {
               siteBook.canTimeJoin(
                  Clue.LOGIN, arrival.x, arrival.y, arrival.z, "login:" + arrival.id + ":" + arrival.due / 60000L, Clue.LOGIN.clueWeight, arrival.name + " logged in here", loginDueLimit
               );
            } else {
               this.predicateUuidSet.add(arrival.id);
            }
         }
      }
   }

   private static void handleSpawner(Clue clue, PacketSensor.Event event, double spawnerValue, double spawnerValue2, double spawnerValue3, SiteBook siteBook, long logoutPredicateValue) {
      if (clue != null) {
         double spawnerFactor = event.x - spawnerValue;
         double spawnerFactor2 = event.y - spawnerValue2;
         double spawnerFactor3 = event.z - spawnerValue3;
         double spawnerFactor4 = clue == Clue.SPAWNER ? 17.0 : 8.0;
         if (!(spawnerFactor * spawnerFactor + spawnerFactor2 * spawnerFactor2 + spawnerFactor3 * spawnerFactor3 <= spawnerFactor4 * spawnerFactor4)) {
            int siteBookValue = (int)Math.floor(event.x);
            int blockY = (int)Math.floor(event.y);
            int blockZ = (int)Math.floor(event.z);
            siteBook.canFlag(clue, siteBookValue, blockY, blockZ, "act:" + clue.ordinal() + ":" + siteBookValue + "," + blockY + "," + blockZ + ":" + event.ms / 60000L, logoutPredicateValue);
         }
      }
   }

   static Clue getChestPiston(Block block, int chestPistonValue, int chestPistonLimit) {
      if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
         return Clue.MACHINE;
      } else if (block == Blocks.NOTE_BLOCK) {
         return Clue.MACHINE;
      } else {
         boolean chestPistonFlag = block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.ENDER_CHEST || block instanceof ShulkerBoxBlock;
         return chestPistonFlag && chestPistonValue == 1 && chestPistonLimit > 0 ? Clue.CHEST_USE : null;
      }
   }

   private void handleEspSection(ClientPlayerEntity player, SiteBook siteBook, long espSectionValue) {
      int espSectionValue2 = player.getBlockX() >> 4;
      int espSectionValue3 = player.getBlockZ() >> 4;

      try {
         for (ChunkLightSnapshot chunkLightSnapshot : LightPacketCache.INSTANCE.sectionsCoordChunkLightSnaps.values()) {
            if (Math.abs(chunkLightSnapshot.chunkX - espSectionValue2) <= 12 && Math.abs(chunkLightSnapshot.chunkZ - espSectionValue3) <= 12 && (Math.abs(chunkLightSnapshot.chunkX - espSectionValue2) > 1 || Math.abs(chunkLightSnapshot.chunkZ - espSectionValue3) > 1)) {
               for (int i = 1; i <= chunkLightSnapshot.verticalSections; i++) {
                  int espSectionValue4 = (chunkLightSnapshot.sectionCoord + (i - 1)) * 16;
                  if (espSectionValue4 + 15 >= 16) {
                     break;
                  }

                  if (chunkLightSnapshot.blockLightStatuses[i] == SectionStatus.ZEROED_PRESENT) {
                     siteBook.canFlag(Clue.HIDDEN_PLAYER, chunkLightSnapshot.chunkX * 16 + 8, espSectionValue4 + 8, chunkLightSnapshot.chunkZ * 16 + 8, "esp:" + chunkLightSnapshot.chunkX + "," + chunkLightSnapshot.chunkZ, espSectionValue);
                     break;
                  }
               }
            }
         }
      } catch (Throwable e) {
      }
   }

   public PacketSensor() {
      this.predicateSeenMap = new HashMap<>();
      this.idsBlockedSeenMap = new HashMap<>();
      this.idsBlockedPositionMap = new HashMap<>();
      this.predicatePositionMap = new HashMap<>();
      this.rootPositionMap = new HashMap<>();
      this.predicateUuidSet = new HashSet<>();
      this.predicateArrivalList = new ArrayList<>();
   }

   public void updateAtomicConcurrent() {
      this.concurrentLinkedQueue.clear();
      this.atomicInteger.set(0);
      this.predicateSeenMap.clear();
      this.idsBlockedSeenMap.clear();
      this.idsBlockedPositionMap.clear();
      this.predicatePositionMap.clear();
      this.rootPositionMap.clear();
      this.predicateArrivalList.clear();
   }

   private record Arrival(UUID id, String name, int x, int y, int z, long due) {
   }

   record Event(PacketSensor.Kind kind, String name, int a, int b, double x, double y, double z, UUID id, int[] ids, long ms) {
   }

   static enum Kind {
      SOUND,
      WORLD_EVENT,
      BLOCK_EVENT,
      DESTROY,
      REMOVE,
      LEFT,
      JOIN;
   }

   private record Seen(UUID id, String name, double x, double y, double z, long ms, boolean blocked) {
   }
}
