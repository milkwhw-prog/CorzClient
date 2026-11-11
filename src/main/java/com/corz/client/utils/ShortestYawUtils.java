package com.corz.client.utils;

import java.util.Locale;

public final class ShortestYawUtils {
   public static float getAmount(float shortestYawValue, float clampPlayerValue) {
      return shortestYawValue + getAmount(clampPlayerValue - shortestYawValue);
   }

   public static float getAmount(float shortestYawValue) {
      float shortestYawTotal = shortestYawValue % 360.0F;
      if (shortestYawTotal >= 180.0F) {
         shortestYawTotal -= 360.0F;
      }

      if (shortestYawTotal < -180.0F) {
         shortestYawTotal += 360.0F;
      }

      return shortestYawTotal;
   }

   public static String formatRoot(String rootText, float rootValue, float clampPlayerValue, float rootValue3) {
      return String.format(
         Locale.ROOT,
         "owner=%s currentYaw=%.1f desiredWrapped=%.1f chosenContinuous=%.1f shortestDelta=%.1f rawRequestedDelta=%.1f preventedFullTurns=%d",
         rootText,
         rootValue,
         clampPlayerValue,
         rootValue3,
         getAmountForFloat(rootValue, clampPlayerValue),
         rootValue3 - rootValue,
         getValue(rootValue, clampPlayerValue)
      );
   }

   public static float getAmountForFloat(float shortestYawValue, float clampPlayerValue) {
      return getAmount(clampPlayerValue - shortestYawValue);
   }

   public static int getValue(float shortestYawValue, float clampPlayerValue) {
      return (int)Math.floor(Math.abs(clampPlayerValue - getAmount(shortestYawValue, clampPlayerValue)) / 360.0F);
   }

   public static boolean canFlag(float shortestYawValue, float shortestYawValue2) {
      return Math.abs(shortestYawValue2 - shortestYawValue) <= 180.001F;
   }

   private ShortestYawUtils() {
   }

   public static final class Counters {
      public double maxShortestYawDelta;
      public double maxAbs;
      public long yawShortestTime;
      public long yawShortestCount;

      public void recordMaxAbs(double countersValue) {
         this.maxAbs = Math.max(this.maxAbs, Math.abs(countersValue));
      }

      public void handleShortestYaw(float shortestYawValue, float clampPlayerValue, float shortestYawValue3) {
         this.yawShortestTime = this.yawShortestTime + ShortestYawUtils.getValue(shortestYawValue, clampPlayerValue);
         if (!ShortestYawUtils.canFlag(shortestYawValue, shortestYawValue3)) {
            this.yawShortestCount++;
         }

         this.maxShortestYawDelta = Math.max(this.maxShortestYawDelta, (double)Math.abs(ShortestYawUtils.getAmountForFloat(shortestYawValue, clampPlayerValue)));
      }

      public void updateShortestYaw() {
         this.yawShortestTime = this.yawShortestCount = 0L;
         this.maxAbs = this.maxShortestYawDelta = 0.0;
      }

      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "yawFullTurnsPrevented=%d yawNonShortestRequests=%d maxRawYawDriftBetweenActions=%.1f maxShortestYawDelta=%.1f",
            this.yawShortestTime,
            this.yawShortestCount,
            this.maxAbs,
            this.maxShortestYawDelta
         );
      }
   }
}
