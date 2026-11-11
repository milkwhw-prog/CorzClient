package com.corz.client.utils;

public final class GrassDarkUtils {
   public static final String[] grassLightTexts = new String[]{"Dark", "Light", "Sky", "Grass"};
   public static final int UNUSED_ONE = 1;
   public static final int[] backdropZoomValues = new int[]{-15724009, -1841430, -8541460, -10773956};

   static float getAmount(float grassDarkValue, float hashRegistryValue, int grassDarkLimit, int grassDarkValue3) {
      float grassDarkTotal = 0.0F;
      float grassDarkFactor = 0.5F;
      float grassDarkTotal2 = 0.0F;

      for (int i = 0; i < grassDarkLimit; i++) {
         grassDarkTotal += grassDarkFactor * getAmount(grassDarkValue, hashRegistryValue, grassDarkValue3 + i * 17);
         grassDarkTotal2 += grassDarkFactor;
         grassDarkValue *= 2.03F;
         hashRegistryValue *= 2.03F;
         grassDarkFactor *= 0.5F;
      }

      return grassDarkTotal / grassDarkTotal2;
   }

   public static float getMax(float grassDarkLimit) {
      float grassDarkDivisor = 1.6F;
      return grassDarkLimit >= grassDarkDivisor ? 1.0F : Math.max(0.05F, grassDarkLimit / grassDarkDivisor);
   }

   private static float getAmount(float grassDarkValue, float grassDarkValue2, float grassDarkFactor) {
      return grassDarkValue + (grassDarkValue2 - grassDarkValue) * grassDarkFactor;
   }

   public static int getValue(int grassDarkValue, int j, int i, int grassDarkDivisor, int grassDarkDivisor2) {
      float grassDarkValue4 = (j + 0.5F) / grassDarkDivisor;
      float base = (i + 0.5F) / grassDarkDivisor2;
      float grassDarkValue5;
      float grassDarkValue6;
      float grassDarkValue7;
      switch (grassDarkValue) {
         case 1:
            float grassDarkValue8 = getAmount(j * 0.018F, i * 0.018F, 3, 71) - 0.5F;
            grassDarkValue5 = getAmount(238.0F, 216.0F, base) + grassDarkValue8 * 7.0F;
            grassDarkValue6 = getAmount(240.0F, 220.0F, base) + grassDarkValue8 * 7.0F;
            grassDarkValue7 = getAmount(243.0F, 226.0F, base) + grassDarkValue8 * 6.0F;
            break;
         case 2:
            float pow = (float)Math.pow(base, 0.85);
            float grassDarkValue9 = getAmount(76.0F, 169.0F, pow);
            float grassDarkValue10 = getAmount(132.0F, 203.0F, pow);
            float grassDarkValue11 = getAmount(223.0F, 244.0F, pow);
            float grassDarkValue12 = getAmount(j * 0.011F, i * 0.026F, 4, 13);
            float grassDarkValue13 = getAmount((grassDarkValue12 - 0.5F) / 0.2F) * (0.92F - 0.45F * base);
            grassDarkValue5 = getAmount(grassDarkValue9, 246.0F, grassDarkValue13);
            grassDarkValue6 = getAmount(grassDarkValue10, 248.0F, grassDarkValue13);
            grassDarkValue7 = getAmount(grassDarkValue11, 252.0F, grassDarkValue13);
            break;
         case 3:
            float grassDarkValue14 = getAmount(j * 0.022F, i * 0.022F, 4, 29);
            float grassDarkValue15 = getAmount(j * 0.21F, i * 0.045F, 2, 47) - 0.5F;
            float grassDarkValue16 = getAmount(j, i, 5) - 0.5F;
            float grassDarkFactor = 1.06F - 0.14F * base;
            grassDarkValue5 = (getAmount(71.0F, 116.0F, grassDarkValue14) + grassDarkValue15 * 18.0F + grassDarkValue16 * 6.0F) * grassDarkFactor;
            grassDarkValue6 = (getAmount(127.0F, 178.0F, grassDarkValue14) + grassDarkValue15 * 26.0F + grassDarkValue16 * 8.0F) * grassDarkFactor;
            grassDarkValue7 = (getAmount(46.0F, 74.0F, grassDarkValue14) + grassDarkValue15 * 12.0F + grassDarkValue16 * 4.0F) * grassDarkFactor;
            break;
         default:
            float grassDarkFactor2 = grassDarkValue4 - 0.5F;
            float grassDarkFactor3 = base - 0.48F;
            float grassDarkFactor4 = 1.0F - 0.32F * Math.min(1.0F, (grassDarkFactor2 * grassDarkFactor2 + grassDarkFactor3 * grassDarkFactor3) * 2.4F);
            grassDarkValue5 = getAmount(21.0F, 10.0F, base) * grassDarkFactor4;
            grassDarkValue6 = getAmount(23.0F, 11.0F, base) * grassDarkFactor4;
            grassDarkValue7 = getAmount(29.0F, 14.0F, base) * grassDarkFactor4;
      }

      float grassDarkValue17 = getAmount(j, i, 101 + grassDarkValue) + getAmount(j, i, 211 + grassDarkValue) - 1.0F;
      return 0xFF000000 | getValue(grassDarkValue5 + grassDarkValue17) << 16 | getValue(grassDarkValue6 + grassDarkValue17) << 8 | getValue(grassDarkValue7 + grassDarkValue17);
   }

   public static float getMaxForFloat(float grassDarkDivisor) {
      float grassDarkLimit = 1.6F;
      return grassDarkDivisor <= grassDarkLimit ? 1.0F : Math.max(0.05F, grassDarkLimit / grassDarkDivisor);
   }

   private static float getAmount(float grassDarkFactor) {
      grassDarkFactor = grassDarkFactor < 0.0F ? 0.0F : (grassDarkFactor > 1.0F ? 1.0F : grassDarkFactor);
      return grassDarkFactor * grassDarkFactor * (3.0F - 2.0F * grassDarkFactor);
   }

   static int getValue(float grassDarkValue) {
      int grassDarkLimit = Math.round(grassDarkValue);
      return grassDarkLimit < 0 ? 0 : (grassDarkLimit > 255 ? 255 : grassDarkLimit);
   }

   private static float getAmount(float grassDarkValue, float hashRegistryValue, int grassDarkValue3) {
      int floor4 = (int)Math.floor(grassDarkValue);
      int i = (int)Math.floor(hashRegistryValue);
      float grassDarkValue6 = getAmount(grassDarkValue - floor4);
      float grassDarkFactor = getAmount(hashRegistryValue - i);
      float grassDarkValue8 = getAmount(floor4, i, grassDarkValue3);
      float grassDarkValue9 = getAmount(floor4 + 1, i, grassDarkValue3);
      float grassDarkValue10 = getAmount(floor4, i + 1, grassDarkValue3);
      float grassDarkValue11 = getAmount(floor4 + 1, i + 1, grassDarkValue3);
      return getAmount(getAmount(grassDarkValue8, grassDarkValue9, grassDarkValue6), getAmount(grassDarkValue10, grassDarkValue11, grassDarkValue6), grassDarkFactor);
   }

   private GrassDarkUtils() {
   }

   static float getAmount(int grassDarkValue, int i, int grassDarkValue3) {
      int grassDarkValue4 = grassDarkValue * 668265261 ^ i * 374761393 ^ grassDarkValue3 * 1540483477;
      int grassDarkValue5 = grassDarkValue4 ^ grassDarkValue4 >>> 15;
      int grassDarkValue6 = grassDarkValue5 * 739982445;
      int grassDarkValue7 = grassDarkValue6 ^ grassDarkValue6 >>> 12;
      int grassDarkValue8 = grassDarkValue7 * 695872825;
      int grassDarkValue9 = grassDarkValue8 ^ grassDarkValue8 >>> 15;
      return (grassDarkValue9 & 16777215) / 1.6777216E7F;
   }
}
