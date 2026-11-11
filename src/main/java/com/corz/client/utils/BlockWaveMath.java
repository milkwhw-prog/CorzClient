package com.corz.client.utils;

import java.util.Arrays;

public final class BlockWaveMath {
   public static final int UNUSED_ONE = 1;

   public static float getAmount(double blockWaveValue, boolean blockWaveFlag, double blockWaveLimit) {
      return blockWaveFlag ? (float)(1.0 + getAmount3(blockWaveLimit, 0.0, 0.5)) : (float)getAmount3(blockWaveValue, 0.0, 1.0);
   }

   public static boolean canFlag(double blockWaveLimit) {
      return blockWaveLimit >= 0.08;
   }

   public static double getAmount(double blockWaveLimit) {
      return !(blockWaveLimit > 0.0) ? 1.0 : 1.0 - getAmount4(0.0, 0.08, blockWaveLimit);
   }

   public static boolean canFlagForDouble(double blockWaveLimit) {
      return blockWaveLimit >= 0.03;
   }

   public static double getAmount(double blockWaveValue, double blockWaveValue2, double blockWaveValue3, double blockWaveLimit, double blockWaveValue5) {
      double blockWaveFactor = getAmount4(getAmount(blockWaveValue, blockWaveValue2, blockWaveValue3));
      if (blockWaveFactor <= 0.0) {
         return 0.0;
      } else {
         double blockWaveFactor2 = getAmount(blockWaveValue2, blockWaveLimit);
         return blockWaveFactor2 <= 0.0 ? 0.0 : blockWaveFactor * blockWaveFactor2 * getAmountDouble(blockWaveValue) * getAmount5(blockWaveValue, blockWaveValue5);
      }
   }

   public static double getAmount(double blockWaveValue, double blockWaveValue2, double blockWaveBound, double blockWaveValue3, double blockWaveLimit, double blockWaveValue5) {
      return Math.max(0.0, blockWaveBound) * getAmount(blockWaveValue, blockWaveValue2, blockWaveValue3, blockWaveLimit, blockWaveValue5);
   }

   public static double getAmount(double blockWaveValue, double blockWaveLimit, double blockWaveValue3) {
      return (blockWaveLimit - getAmount3(blockWaveValue, blockWaveValue3)) / getAmount5(blockWaveValue3);
   }

   public static boolean canFlag(
      boolean blockWaveFlag,
      boolean blockWaveFlag2,
      boolean blockWaveFlag3,
      boolean blockWaveFlag4,
      boolean blockWaveFlag5,
      boolean varEmpty,
      double blockWaveLimit,
      boolean mutableFlag,
      boolean blockWaveFlag8,
      boolean blockWaveFlag9
   ) {
      if (blockWaveFlag || !blockWaveFlag2 || blockWaveFlag3 || blockWaveFlag4 || blockWaveFlag5) {
         return false;
      } else if (varEmpty || !mutableFlag) {
         return false;
      } else {
         return !(blockWaveLimit < 0.5) && !(blockWaveLimit > 1.0) ? blockWaveFlag8 && !blockWaveFlag9 : false;
      }
   }

   public static double getAmount(double blockWaveValue, double blockWaveLimit) {
      if (!(blockWaveLimit > 0.0)) {
         return 0.0;
      } else {
         double blockWaveLimit2 = blockWaveValue / blockWaveLimit;
         if (blockWaveLimit2 <= 0.0) {
            return 1.0;
         } else {
            return blockWaveLimit2 >= 1.0 ? 0.0 : Math.exp(-1.1 * blockWaveLimit2) * (1.0 - getAmount4(0.7, 1.0, blockWaveLimit2));
         }
      }
   }

   public static double getAmountForDouble(double blockWaveValue) {
      return getAmount3(blockWaveValue * 0.35, 1.0, 2.5);
   }

   public static int getValue(float[] blockWaveValues, int blockWaveBound, int rocketsBuyBound, int[] blockWaveValues2) {
      int blockWaveSize = Math.max(0, Math.min(blockWaveBound, blockWaveValues.length));
      long[] longs = new long[blockWaveSize];
      int blockWaveCounter = 0;

      for (int i = 0; i < blockWaveSize; i++) {
         float blockWaveLimit = blockWaveValues[i];
         if (blockWaveLimit >= 0.0F) {
            longs[blockWaveCounter++] = (long)Float.floatToIntBits(blockWaveLimit) << 32 | i & 4294967295L;
         }
      }

      Arrays.sort(longs, 0, blockWaveCounter);
      int min2 = Math.min(blockWaveCounter, Math.min(Math.max(0, rocketsBuyBound), blockWaveValues2.length));

      for (int j = 0; j < min2; j++) {
         blockWaveValues2[j] = (int)longs[j];
      }

      return min2;
   }

   public static double getAmountDouble(double blockWaveValue) {
      return getAmount4(0.35, 1.5, blockWaveValue);
   }

   public static int getValue(int blockWaveValue, float blockWaveFactor) {
      if (!(blockWaveFactor > 0.0F)) {
         return -16777216;
      } else {
         if (blockWaveFactor > 1.0F) {
            blockWaveFactor = 1.0F;
         }

         int round = Math.round((blockWaveValue >> 16 & 0xFF) * blockWaveFactor);
         int round3 = Math.round((blockWaveValue >> 8 & 0xFF) * blockWaveFactor);
         int round4 = Math.round((blockWaveValue & 0xFF) * blockWaveFactor);
         return 0xFF000000 | round << 16 | round3 << 8 | round4;
      }
   }

   public static double getAmount2(double blockWaveValue, double blockWaveValue2, double blockWaveBound) {
      return Math.max(0.0, Math.min(blockWaveBound, getAmount3(blockWaveValue, blockWaveValue2) + getAmount5(blockWaveValue2)));
   }

   public static double getMax(double blockWaveBound) {
      return Math.max(0.5, blockWaveBound);
   }

   public static boolean canFlag(double blockWaveValue, double blockWaveLimit, double blockWaveValue2) {
      return blockWaveLimit >= getAmount3(blockWaveValue, blockWaveValue2) - 0.05 && getAmount(blockWaveValue, blockWaveLimit, blockWaveValue2) < 1.0;
   }

   private static double getAmount3(double blockWaveLimit, double blockWaveLimit2, double blockWaveValue) {
      return blockWaveLimit < blockWaveLimit2 ? blockWaveLimit2 : (blockWaveLimit > blockWaveValue ? blockWaveValue : blockWaveLimit);
   }

   public static double getAmount4(double blockWaveLimit) {
      if (blockWaveLimit > 0.0 && !(blockWaveLimit >= 1.0)) {
         double sin = Math.sin(Math.PI * blockWaveLimit);
         return sin * sin;
      } else {
         return 0.0;
      }
   }

   public static double getAmount(double blockWaveBound, boolean blockWaveFlag, boolean empty, double blockWaveValue) {
      return blockWaveFlag && !empty ? Math.max(blockWaveBound, 1.0 + blockWaveValue) : blockWaveBound;
   }

   public static int getValue(int blockWaveValue, int blockWaveValue2, float blockWaveLimit) {
      if (!(blockWaveLimit > 0.0F)) {
         return blockWaveValue;
      } else {
         if (blockWaveLimit > 1.0F) {
            blockWaveLimit = 1.0F;
         }

         int blockWaveValue3 = blockWaveValue >>> 24;
         int valueForIntFloat = getValueForIntFloat(blockWaveValue >> 16 & 0xFF, blockWaveValue2 >> 16 & 0xFF, blockWaveLimit);
         int valueForIntFloat5 = getValueForIntFloat(blockWaveValue >> 8 & 0xFF, blockWaveValue2 >> 8 & 0xFF, blockWaveLimit);
         int valueForIntFloat6 = getValueForIntFloat(blockWaveValue & 0xFF, blockWaveValue2 & 0xFF, blockWaveLimit);
         return blockWaveValue3 << 24 | valueForIntFloat << 16 | valueForIntFloat5 << 8 | valueForIntFloat6;
      }
   }

   private BlockWaveMath() {
   }

   public static boolean canFlag(
      boolean blockWaveFlag,
      boolean blockWaveFlag2,
      boolean blockWaveFlag3,
      boolean blockWaveFlag4,
      boolean blockWaveFlag5,
      boolean varEmpty,
      double blockWaveLimit,
      double blockWaveLimit2,
      boolean blockWaveFlag7,
      boolean blockWaveFlag8,
      double blockWaveValue
   ) {
      if (blockWaveFlag || !blockWaveFlag2 || blockWaveFlag3 || blockWaveFlag4 || blockWaveFlag5) {
         return false;
      } else if (varEmpty || !blockWaveFlag7) {
         return false;
      } else {
         return !(blockWaveLimit > 1.0E-4) && !(blockWaveLimit2 > 0.5001) ? blockWaveFlag8 || blockWaveValue < 0.5 : false;
      }
   }

   public static double getAmount2(double blockWaveValue, double blockWaveValue2) {
      return getAmount3(blockWaveValue, blockWaveValue2) + 0.5 * getAmount5(blockWaveValue2);
   }

   public static boolean canFlag2(double blockWaveValue, double blockWaveLimit, double blockWaveValue2) {
      if (Double.isNaN(blockWaveLimit)) {
         return true;
      } else {
         double abs3 = Math.abs(blockWaveValue - blockWaveValue2);
         double abs4 = Math.abs(blockWaveLimit - blockWaveValue2);
         return abs3 < abs4 - 1.0E-9 ? true : Math.abs(abs3 - abs4) <= 1.0E-9 && blockWaveValue < blockWaveLimit;
      }
   }

   public static double getAmount4(double blockWaveLimit, double blockWaveValue, double blockWaveValue2) {
      if (blockWaveValue <= blockWaveLimit) {
         return blockWaveValue2 < blockWaveLimit ? 0.0 : 1.0;
      } else {
         double blockWaveFactor = getAmount3((blockWaveValue2 - blockWaveLimit) / (blockWaveValue - blockWaveLimit), 0.0, 1.0);
         return blockWaveFactor * blockWaveFactor * (3.0 - 2.0 * blockWaveFactor);
      }
   }

   private static int getValueForIntFloat(int blockWaveValue, int blockWaveValue2, float blockWaveFactor) {
      return Math.round(blockWaveValue + (blockWaveValue2 - blockWaveValue) * blockWaveFactor);
   }

   public static boolean canFlag3(double blockWaveFactor, double blockWaveFactor2, double coverageValue) {
      return blockWaveFactor * blockWaveFactor + blockWaveFactor2 * blockWaveFactor2 <= coverageValue * coverageValue;
   }

   public static double getAmount3(double blockWaveBound, double blockWaveValue) {
      return Math.max(0.0, blockWaveBound) / getMax(blockWaveValue);
   }

   public static double getAmount5(double blockWaveValue) {
      return 2.6 / getMax(blockWaveValue);
   }

   public static double getAmount4(double blockWaveFactor, double blockWaveValue) {
      return getMax(blockWaveValue) * blockWaveFactor - 1.3;
   }

   public static double getAmount5(double blockWaveLimit, double blockWaveValue) {
      return blockWaveLimit >= blockWaveValue ? 0.0 : 1.0 - getAmount4(blockWaveValue - getAmountForDouble(blockWaveValue), blockWaveValue, blockWaveLimit);
   }
}
