package com.corz.client.utils;

public final class FluidAmountUtils {

   public static void handle(float[] fluidValues, int fluidValue, int index2, int[] fluidValues2) {
      float fluidLimit = fluidValues[index2];
      float fluidValue3 = fluidValues[index2 - fluidValue];
      float fluidValue4 = fluidValues[index2 + fluidValue];
      float fluidValue5 = fluidValues[index2 + 1];
      float fluidValue6 = fluidValues[index2 - 1];
      fluidValues2[0] = getMin(getAmount(fluidLimit, fluidValue3, fluidValue6, fluidValues[index2 - fluidValue - 1]));
      fluidValues2[1] = getMin(getAmount(fluidLimit, fluidValue4, fluidValue6, fluidValues[index2 + fluidValue - 1]));
      fluidValues2[2] = getMin(getAmount(fluidLimit, fluidValue4, fluidValue5, fluidValues[index2 + fluidValue + 1]));
      fluidValues2[3] = getMin(getAmount(fluidLimit, fluidValue3, fluidValue5, fluidValues[index2 - fluidValue + 1]));
   }

   public static float getAmount(int fluidValue) {
      return (fluidValue + 0.5F) / 127.0F;
   }

   public static float getAmount(float fluidBound, int rocketsBuyBound) {
      return Math.min(8.0F, Math.max(0.0F, fluidBound) + Math.max(0, rocketsBuyBound));
   }

   public static float getAmount(float fluidLimit, float fluidLimit2, float fluidLimit3, float fluidLimit4) {
      float fluidTotal = 0.0F;
      int fluidCounter = 0;
      if (fluidLimit >= 0.0F) {
         fluidTotal += fluidLimit;
         fluidCounter++;
      }

      if (fluidLimit2 >= 0.0F) {
         fluidTotal += fluidLimit2;
         fluidCounter++;
      }

      if (fluidLimit3 >= 0.0F) {
         fluidTotal += fluidLimit3;
         fluidCounter++;
      }

      if (fluidLimit4 >= 0.0F) {
         fluidTotal += fluidLimit4;
         fluidCounter++;
      }

      return fluidCounter == 0 ? 0.0F : fluidTotal / fluidCounter;
   }

   public static float getAmountForInt(int fluidValue) {
      return fluidValue * 8.0F / 127.0F;
   }

   private FluidAmountUtils() {
   }

   public static boolean canFlag(int[] fluidValues) {
      return fluidValues[0] == fluidValues[1] && fluidValues[1] == fluidValues[2] && fluidValues[2] == fluidValues[3];
   }

   public static int getMin(float fluidLimit) {
      return !(fluidLimit > 0.0F) ? 0 : Math.min(127, Math.round(fluidLimit / 8.0F * 127.0F));
   }
}
