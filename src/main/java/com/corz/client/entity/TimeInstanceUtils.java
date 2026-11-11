package com.corz.client.entity;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public final class TimeInstanceUtils {
   private static int repeatThreshold;
   private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   private static final ArrayDeque<Long> ARRAY_DEQUE = new ArrayDeque<>();
   private static long minElapsedMs;
   private static int lastEntityId = -1;
   private static long requiredGapMs;
   private static long nextAllowedTimeMs;
   private static long timeMillis;
   private static long timeMillisInteractionBreakin;
   private static int repeatCounter;

   private static long getMax() {
      double bound = 150.0 + ThreadLocalRandom.current().nextGaussian() * 45.0;
      return (long)Math.max(90.0, Math.min(280.0, bound));
   }

   public static void updateDequeArray() {
      long currentMillis = System.currentTimeMillis();
      handleDequeArray(currentMillis);
      ARRAY_DEQUE.addLast(currentMillis);
      handleTime(currentMillis);
   }

   public static boolean canTimeMin(Entity entity) {
      long currentTimeMillis = System.currentTimeMillis();
      if (entity.getId() != lastEntityId || currentTimeMillis - timeMillis > 1200L) {
         lastEntityId = entity.getId();
         minElapsedMs = currentTimeMillis;
         requiredGapMs = getMax();
      }

      timeMillis = currentTimeMillis;
      if (isInteractionBreaking()) {
         return false;
      } else if (currentTimeMillis < nextAllowedTimeMs) {
         return false;
      } else if (currentTimeMillis - minElapsedMs < requiredGapMs) {
         return false;
      } else {
         handleDequeArray(currentTimeMillis);
         return ARRAY_DEQUE.size() >= 8 ? false : ThreadLocalRandom.current().nextDouble() >= 0.12;
      }
   }

   public static boolean canFlag(Entity entity) {
      if (!canTimeMin(entity)) {
         return false;
      } else {
         updateDequeArray();
         return true;
      }
   }

   public static void handleMinTime(Entity entity) {
      long currentTimeMillis = System.currentTimeMillis();
      lastEntityId = entity.getId();
      timeMillis = currentTimeMillis;
      minElapsedMs = Math.min(minElapsedMs, currentTimeMillis - requiredGapMs);
      handleDequeArray(currentTimeMillis);
      ARRAY_DEQUE.addLast(currentTimeMillis);
      handleTime(currentTimeMillis);
   }

   private TimeInstanceUtils() {
   }

   private static void handleDequeArray(long dequeArrayValue) {
      while (!ARRAY_DEQUE.isEmpty() && dequeArrayValue - ARRAY_DEQUE.peekFirst() > 1000L) {
         ARRAY_DEQUE.pollFirst();
      }
   }

   private static void handleTime(long longValue) {
      if (repeatThreshold == 0) {
         repeatThreshold = 6 + ThreadLocalRandom.current().nextInt(7);
      }

      if (++repeatCounter >= repeatThreshold) {
         repeatCounter = 0;
         repeatThreshold = 6 + ThreadLocalRandom.current().nextInt(7);
         nextAllowedTimeMs = longValue + 300L + ThreadLocalRandom.current().nextInt(300);
      }
   }

   public static boolean isInteractionBreaking() {
      long timeMillis = System.currentTimeMillis();
      if (minecraftClient.interactionManager != null && minecraftClient.interactionManager.isBreakingBlock()) {
         timeMillisInteractionBreakin = timeMillis;
         return true;
      } else {
         return timeMillis - timeMillisInteractionBreakin < 75L;
      }
   }
}
