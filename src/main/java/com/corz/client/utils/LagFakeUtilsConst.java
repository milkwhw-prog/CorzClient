package com.corz.client.utils;

public final class LagFakeUtilsConst {

   public static boolean canFlag(double currentValue, double thresholdValue) {
      return currentValue < thresholdValue - 0.03;
   }

   private LagFakeUtilsConst() {
   }

   public static long getTime(long baseTime, long extraDelay, long minTime) {
      long maxLastPositionUntil = Math.max(baseTime, minTime);
      return Math.min(maxLastPositionUntil + 90L, baseTime + Math.max(0L, extraDelay));
   }
}
