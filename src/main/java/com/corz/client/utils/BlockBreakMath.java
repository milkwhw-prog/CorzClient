package com.corz.client.utils;

public final class BlockBreakMath {
   public static final float[] MINING_FATIGUE_MULTIPLIERS = new float[]{0.3F, 0.09F, 0.0027F, 8.1E-4F};

   public static float getBlockBreakSpeed(float immutablePosLimit, int immutablePosValue, int immutablePosLimit2, int intersectsCenterBound, boolean immutablePosFlag, boolean suitableMultiplierFlag) {
      float immutablePosTotal = immutablePosLimit > 0.0F ? immutablePosLimit : 1.0F;
      if (immutablePosTotal > 1.0F) {
         immutablePosTotal += getEfficiencyBonus(immutablePosValue);
      }

      if (immutablePosLimit2 >= 0) {
         immutablePosTotal *= 1.0F + (immutablePosLimit2 + 1) * 0.2F;
      }

      if (intersectsCenterBound >= 0) {
         immutablePosTotal *= MINING_FATIGUE_MULTIPLIERS[Math.min(intersectsCenterBound, MINING_FATIGUE_MULTIPLIERS.length - 1)];
      }

      if (immutablePosFlag) {
         immutablePosTotal *= 0.2F;
      }

      if (!suitableMultiplierFlag) {
         immutablePosTotal /= 5.0F;
      }

      return immutablePosTotal;
   }

   public static int getTicksUntilBroken(float immutablePosLimit) {
      if (!(immutablePosLimit > 0.0F)) {
         return Integer.MAX_VALUE;
      } else if (immutablePosLimit >= 1.0F) {
         return 0;
      } else {
         float immutablePosTotal = 0.0F;

         for (int i = 1; i <= 100000; i++) {
            immutablePosTotal += immutablePosLimit;
            if (immutablePosTotal >= 1.0F) {
               return i;
            }
         }

         return Integer.MAX_VALUE;
      }
   }

   public static int getObsidianBreakTicks(float immutablePosValue, int enchantmentsDefault, boolean immutablePosFlag) {
      return getBreakTicks(immutablePosValue, enchantmentsDefault, 50.0F, immutablePosFlag, true);
   }

   public static float getEfficiencyBonus(int immutablePosFactor) {
      return immutablePosFactor > 0 ? immutablePosFactor * immutablePosFactor + 1 : 0.0F;
   }

   private BlockBreakMath() {
   }

   public static int getBreakTicks(float immutablePosValue, int enchantmentsDefault, float immutablePosDivisor, boolean immutablePosFlag, boolean suitableMultiplierFlag) {
      return getTicksUntilBroken(getBreakProgressPerTick(getBlockBreakSpeed(immutablePosValue, enchantmentsDefault, -1, -1, false, suitableMultiplierFlag), immutablePosDivisor, immutablePosFlag));
   }

   public static boolean isBreakProgressComplete(float immutablePosLimit) {
      return immutablePosLimit >= 1.0F;
   }

   public static int addTicksSaturating(int immutablePosValue, int immutablePosValue2) {
      if (immutablePosValue != Integer.MAX_VALUE && immutablePosValue2 != Integer.MAX_VALUE) {
         long immutablePosLimit = (long)immutablePosValue + immutablePosValue2;
         return immutablePosLimit >= 2147483647L ? Integer.MAX_VALUE : (int)immutablePosLimit;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static int reduceTicksByTenths(int immutablePosFactor, int immutablePosBound) {
      if (immutablePosFactor == Integer.MAX_VALUE) {
         return Integer.MAX_VALUE;
      } else if (immutablePosFactor <= 0) {
         return 0;
      } else {
         int immutablePosValue = Math.max(0, Math.min(10, immutablePosBound));
         return (int)Math.ceil(immutablePosFactor * (1.0 - immutablePosValue / 10.0));
      }
   }

   public static float getBreakProgressPerTick(float immutablePosValue, float immutablePosDivisor, boolean immutablePosFlag) {
      if (immutablePosDivisor < 0.0F) {
         return 0.0F;
      } else {
         return immutablePosDivisor == 0.0F ? Float.POSITIVE_INFINITY : immutablePosValue / immutablePosDivisor / (immutablePosFlag ? 30 : 100);
      }
   }
}
