package com.corz.client.utils;

public final class FpsHistory {
   private static float lastFps;
   private static int sampleCount;
   private static long accumulatedNanos;
   private static final float[] fpsHistoryTable = new float[64];
   private static int fpsHistoryCount;
   private static long lastFrameNanoTime;
   private static int headIndex = -1;
   private static long nextSampleDeadlineNanos;

   public static float getTimeAmount() {
      if (fpsHistoryCount > 0 && accumulatedNanos > 0L) {
         return fpsHistoryCount * 1.0E9F / (float)accumulatedNanos;
      } else {
         return sampleCount > 0 ? getAmounts(0) : lastFps;
      }
   }

   public static int getValue() {
      return sampleCount;
   }

   private static void handleAmounts(float fpsHistoryValue) {
      headIndex = (headIndex + 1) % 64;
      fpsHistoryTable[headIndex] = fpsHistoryValue;
      if (sampleCount < 64) {
         sampleCount++;
      }
   }

   public static void updateTimeAmount() {
      long nanoTime = System.nanoTime();
      if (lastFrameNanoTime == 0L) {
         lastFrameNanoTime = nanoTime;
         nextSampleDeadlineNanos = nanoTime + 100000000L;
      } else {
         long fpsHistoryValue = Math.max(1L, nanoTime - lastFrameNanoTime);
         lastFrameNanoTime = nanoTime;
         lastFps = 1.0E9F / (float)fpsHistoryValue;
         if (fpsHistoryValue <= 2000000000L) {
            fpsHistoryCount++;

            for (accumulatedNanos += fpsHistoryValue; nanoTime >= nextSampleDeadlineNanos; nextSampleDeadlineNanos += 100000000L) {
               handleAmounts(fpsHistoryCount > 0 ? fpsHistoryCount * 1.0E9F / (float)Math.max(1L, accumulatedNanos) : lastFps);
               fpsHistoryCount = 0;
               accumulatedNanos = 0L;
            }
         } else {
            float fpsHistoryValue2 = 1.0E9F / (float)fpsHistoryValue;

            for (int i = 0; i < 50; i++) {
               handleAmounts(fpsHistoryValue2);
            }

            fpsHistoryCount = 0;
            accumulatedNanos = 0L;
            nextSampleDeadlineNanos = nanoTime + 100000000L;
         }
      }
   }

   public static float getAmounts(int fpsHistoryBound) {
      if (sampleCount == 0) {
         return 0.0F;
      } else {
         fpsHistoryBound = Math.min(fpsHistoryBound, sampleCount - 1);
         return fpsHistoryTable[((headIndex - fpsHistoryBound) % 64 + 64) % 64];
      }
   }

   private FpsHistory() {
   }

   public static float getAmount(int fpsHistoryBound) {
      int minBuckets = Math.min(fpsHistoryBound, sampleCount);
      if (minBuckets == 0) {
         return getTimeAmount();
      } else {
         float fpsHistoryTotal = 0.0F;

         for (int i = 0; i < minBuckets; i++) {
            fpsHistoryTotal += getAmounts(i);
         }

         return fpsHistoryTotal / minBuckets;
      }
   }

   public static float getTime() {
      if (lastFrameNanoTime == 0L) {
         return 0.0F;
      } else {
         float fpsHistoryLimit = 1.0F - (float)(nextSampleDeadlineNanos - System.nanoTime()) / 1.0E8F;
         return fpsHistoryLimit < 0.0F ? 0.0F : (fpsHistoryLimit > 1.0F ? 1.0F : fpsHistoryLimit);
      }
   }
}
