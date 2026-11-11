package com.corz.client.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import com.corz.client.utils.ConnectionTimeUtils;

public final class EntityDamageUtils {
   private static final MinecraftClient entityDamageMinecraftClient = MinecraftClient.getInstance();
   private static final EntityDamageUtils.Ring HURT_TIME_RING = new EntityDamageUtils.Ring(20);
   private static final Map<Integer, EntityDamageUtils.Hurt> HURT_BY_ENTITY_ID = new ConcurrentHashMap<>();
   private static final Object HURT_LOCK = new Object();
   private static long lastHurtTimeMs;
   private static int hurtValue = -1;
   private static long entityDamageValue;
   private static volatile long sourceEntityCount;
   private static volatile int localPlayerId = -1;
   private static volatile int cachedHurtTimeLimit;

   public static long getTime() {
      return HURT_TIME_RING.getValue() > 0 ? HURT_TIME_RING.getTimes() : getCachedHurtTimeLimit();
   }

   public static int getHurtTime(Entity entity) {
      return entity instanceof LivingEntity livingEntity ? livingEntity.hurtTime : 0;
   }

   public static void handleTimeHurt(Entity entity) {
      ClientPlayerEntity player = entityDamageMinecraftClient.player;
      if (player != null) {
         localPlayerId = player.getId();
      }

      if (entity instanceof LivingEntity livingEntity && livingEntity.hurtTime <= 0) {
         long timeTimeMillis = ConnectionTimeUtils.getTimeTimeMillis();
         synchronized (HURT_LOCK) {
            if (timeTimeMillis - entityDamageValue > 400L) {
               hurtValue = entity.getId();
               lastHurtTimeMs = timeTimeMillis;
               entityDamageValue = timeTimeMillis;
            }
         }
      }
   }

   static boolean canFlag(long entityDamageLimit) {
      return entityDamageLimit >= 0L && entityDamageLimit < 500L;
   }

   public static void updateTime() {
      HURT_TIME_RING.update();
      HURT_BY_ENTITY_ID.clear();
      cachedHurtTimeLimit = 0;
      synchronized (HURT_LOCK) {
         hurtValue = -1;
         entityDamageValue = 0L;
      }
   }

   private EntityDamageUtils() {
   }

   public static void handleSourceEntity(Packet<?> packet, long sourceEntityValue) {
      if (packet instanceof BundlePacket bundlePacket) {
         for (Packet packet3 : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
            handleSourceEntity(packet3, sourceEntityValue);
         }
      } else if (packet instanceof EntityDamageS2CPacket entityDamageS2CPacket) {
         HURT_BY_ENTITY_ID.put(entityDamageS2CPacket.entityId(), new EntityDamageUtils.Hurt(sourceEntityValue, sourceEntityCount));
         int localPlayerId3 = localPlayerId;
         if (localPlayerId3 != -1 && (entityDamageS2CPacket.sourceCauseId() == localPlayerId3 || entityDamageS2CPacket.sourceDirectId() == localPlayerId3)) {
            synchronized (HURT_LOCK) {
               if (hurtValue == entityDamageS2CPacket.entityId()) {
                  long entityDamageLimit = sourceEntityValue - lastHurtTimeMs;
                  hurtValue = -1;
                  if (canFlag(entityDamageLimit)) {
                     HURT_TIME_RING.handleTimes(entityDamageLimit);
                  }
               }
            }
         }
      }
   }

   public static void updateTimeInstance() {
      sourceEntityCount++;
      ClientPlayerEntity player = entityDamageMinecraftClient.player;
      localPlayerId = player != null ? player.getId() : -1;
      cachedHurtTimeLimit = getMax(player);
      long entityDamageValue = ConnectionTimeUtils.getTimeTimeMillis();
      synchronized (HURT_LOCK) {
         if (hurtValue != -1 && entityDamageValue - lastHurtTimeMs >= 500L) {
            hurtValue = -1;
         }
      }

      if (sourceEntityCount % 100L == 0L && !HURT_BY_ENTITY_ID.isEmpty()) {
         HURT_BY_ENTITY_ID.values().removeIf(hurt -> entityDamageValue - hurt.ms() > 10000L);
      }
   }

   public static int getMin(int entityDamageValue) {
      EntityDamageUtils.Hurt hurt = HURT_BY_ENTITY_ID.get(entityDamageValue);
      return hurt == null ? Integer.MAX_VALUE : (int)Math.min(2147483647L, Math.max(0L, sourceEntityCount - hurt.tick()));
   }

   public static int getValue() {
      return HURT_TIME_RING.getValue();
   }

   public static boolean isFlag() {
      return HURT_TIME_RING.getValue() > 0;
   }

   private static int getMax(ClientPlayerEntity player) {
      ClientPlayNetworkHandler networkHandler = entityDamageMinecraftClient.getNetworkHandler();
      if (networkHandler != null && player != null) {
         PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(player.getUuid());
         return playerListEntry == null ? 0 : Math.max(0, playerListEntry.getLatency());
      } else {
         return 0;
      }
   }

   static int getValue(long entityDamageBound) {
      return (int)Math.round(Math.max(0L, entityDamageBound) / 50.0);
   }

   public static int getMax(LivingEntity livingEntity) {
      return livingEntity == null ? 0 : Math.max(0, livingEntity.timeUntilRegen - 10);
   }

   public static int getCachedHurtTimeLimit() {
      return cachedHurtTimeLimit;
   }

   public static int getValueInt() {
      return getValue(getTime());
   }

   public static long getTime(int entityDamageValue) {
      EntityDamageUtils.Hurt hurt = HURT_BY_ENTITY_ID.get(entityDamageValue);
      return hurt == null ? -1L : hurt.ms();
   }

   private record Hurt(long ms, long tick) {
   }

   static final class Ring {
      private final long[] ringTable;
      private int ringSampleCount;
      private int ringValue;

      synchronized void handleTimes(long ringValue) {
         this.ringTable[this.ringValue] = ringValue;
         this.ringValue = (this.ringValue + 1) % this.ringTable.length;
         if (this.ringSampleCount < this.ringTable.length) {
            this.ringSampleCount++;
         }
      }

      Ring(int ringSize) {
         this.ringTable = new long[ringSize];
      }

      synchronized long getTimes() {
         if (this.ringSampleCount == 0) {
            return 0L;
         } else {
            long ringTotal = 0L;

            for (int i = 0; i < this.ringSampleCount; i++) {
               ringTotal += this.ringTable[i];
            }

            return Math.round((double)ringTotal / this.ringSampleCount);
         }
      }

      synchronized int getValue() {
         return this.ringSampleCount;
      }

      synchronized void update() {
         this.ringSampleCount = 0;
         this.ringValue = 0;
      }
   }
}
