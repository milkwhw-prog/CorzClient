package com.corz.client.utils;

public final class AmountsWaterUtils {
   static final int[] waterTable = new int[]{11, -7, 27, 9, -64, -109, 77, 150, -74, 364};
   static final int[] table = new int[]{5, 18, -14, 47, -38, 40, -132, 150, -277, 64};
   static final float[] WAVE_AMPLITUDES = new float[]{0.16F, 0.14F, 0.12F, 0.1F, 0.085F, 0.07F, 0.06F, 0.05F, 0.04F, 0.032F};
   private static final double[] WAVE_FREQUENCIES = new double[10];

   public static double getAmounts(int index) {
      return WAVE_FREQUENCIES[index];
   }

   public static int getFloorMod(int floorModValue) {
      return Math.floorMod(floorModValue, 256);
   }

   static {
      for (int i = 0; i < 10; i++) {
         WAVE_FREQUENCIES[i] = Math.sqrt(4.905 * getAmount(i));
      }
   }

   public static void handleAmounts(double waterFactor, float[] waterValues) {
      for (int i = 0; i < 10; i++) {
         double waterTotal = WAVE_FREQUENCIES[i] * waterFactor % (Math.PI * 2);
         if (waterTotal < 0.0) {
            waterTotal += Math.PI * 2;
         }

         waterValues[i] = (float)waterTotal;
      }
   }

   public static double getAmount(int index) {
      return 0.02454369260617026 * Math.hypot(waterTable[index], table[index]);
   }

   private AmountsWaterUtils() {
   }

   public static double getAmountForInt(int waterValue) {
      return (Math.PI * 2) / getAmount(waterValue);
   }

   public static float getAmounts(double waterFactor, double waterFactor2, double waterFactor3) {
      double waterTotal = 0.0;

      for (int i = 0; i < 10; i++) {
         double waterFactor4 = 0.02454369260617026 * waterTable[i];
         double waterFactor5 = 0.02454369260617026 * table[i];
         double angle = waterFactor4 * waterFactor + waterFactor5 * waterFactor2 - WAVE_FREQUENCIES[i] * waterFactor3;
         waterTotal += WAVE_AMPLITUDES[i] / getAmount(i) * Math.exp(Math.sin(angle) - 1.0);
      }

      return (float)waterTotal;
   }

   public static float getAmount(double waterValue) {
      double waterCounter = waterValue / 2.5 % 1.0;
      if (waterCounter < 0.0) {
         waterCounter++;
      }

      return (float)waterCounter;
   }
}
