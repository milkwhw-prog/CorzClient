package com.corz.client.render;

public final class MatrixClearUtils {
   private static long lastNanoTime = 0L;
   private static double trackedProgress = 0.0;
   private static int updateCount = 0;

   public static void updateAmountTime() {
      lastNanoTime = 0L;
      trackedProgress = 0.0;
      updateCount = 0;
   }

   public static double getAmount() {
      return trackedProgress;
   }

   public static int getValue() {
      return updateCount;
   }

   private MatrixClearUtils() {
   }

   public static void updateTimeAmount() {
      long nanoTime = System.nanoTime();
      if (lastNanoTime == 0L) {
         trackedProgress = 0.0;
      } else {
         double amount2 = (nanoTime - lastNanoTime) / 1.0E9;
         if (amount2 < 0.0) {
            amount2 = 0.0;
         }

         if (amount2 > 0.05) {
            amount2 = 0.05;
         }

         trackedProgress = amount2;
      }

      lastNanoTime = nanoTime;
      updateCount = 0;
   }

   public static void update() {
      updateCount++;
   }
}
