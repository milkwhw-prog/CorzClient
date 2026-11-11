package com.corz.client.utils;

public final class SwordCombatMath {

   public static int getTicksUntilCharge(long constLimit, double constValue, float constFactor) {
      if (constLimit >= 0L && constLimit < 2147483647L) {
         double constValue2 = constFactor * getAttackCooldownTicks(constValue) - 0.5 - constLimit;
         return constValue2 <= 0.0 ? 0 : (int)Math.ceil(constValue2 - 1.0E-9);
      } else {
         return 0;
      }
   }

   public static double subtractReachMargin(double constLimit) {
      return constLimit > 0.0 ? Math.max(0.0, constLimit - 0.020000000000000018) : 0.0;
   }

   public static int getDeltaTrend(double constValue, double groundY, double constBound, int constLimit) {
      if (Double.isFinite(constValue) && Double.isFinite(groundY)) {
         double maxDesired = Math.max(0.0, constBound);
         double constLimit2 = constValue - groundY;
         if (constLimit > 0) {
            return constLimit2 > -maxDesired ? 1 : (constLimit2 < -2.0 * maxDesired ? -1 : 0);
         } else if (constLimit < 0) {
            return constLimit2 < maxDesired ? -1 : (constLimit2 > 2.0 * maxDesired ? 1 : 0);
         } else {
            return constLimit2 > 2.0 * maxDesired ? 1 : (constLimit2 < -2.0 * maxDesired ? -1 : 0);
         }
      } else {
         return 0;
      }
   }

   public static double lerpFinite(double constValue, double groundY, double constBound) {
      if (!Double.isFinite(groundY)) {
         return constValue;
      } else if (!Double.isFinite(constValue)) {
         return groundY;
      } else {
         double constFactor = Math.max(0.0, Math.min(1.0, constBound));
         return constValue + constFactor * (groundY - constValue);
      }
   }

   public static boolean isWithinCloseRange(double constValue) {
      return Double.isFinite(constValue) && constValue < 2.3;
   }

   private SwordCombatMath() {
   }

   public static boolean isAtLeastFour(int constLimit) {
      return constLimit >= 4;
   }

   public static double getAngleToVector(float degrees, float routedPartialValue, double constFactor, double constFactor2, double constFactor3) {
      double distance = Math.sqrt(constFactor * constFactor + constFactor2 * constFactor2 + constFactor3 * constFactor3);
      if (distance > 1.0E-6 && Float.isFinite(degrees) && Float.isFinite(routedPartialValue)) {
         double angle = Math.toRadians(degrees);
         double radians = Math.toRadians(routedPartialValue);
         double constFactor4 = -Math.sin(angle) * Math.cos(radians);
         double sin = -Math.sin(radians);
         double constFactor5 = Math.cos(angle) * Math.cos(radians);
         double constBound = (constFactor4 * constFactor + sin * constFactor2 + constFactor5 * constFactor3) / distance;
         return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, constBound))));
      } else {
         return 180.0;
      }
   }

   public static double wrapDegrees(double constValue) {
      return (constValue % 360.0 + 540.0) % 360.0 - 180.0;
   }

   public static double getEngageRangeFromPing(int constBound, double groundY) {
      int constValue = Math.max(0, Math.min(3, (int)Math.round(Math.max(0, constBound) / 50.0)));
      double constValue2 = Double.isFinite(groundY) ? Math.max(0.0, Math.min(0.35, groundY)) : 0.0;
      return 3.35 + constValue * 0.12 + constValue2 * 2.5;
   }

   public static double getAttackCooldownTicks(double constLimit) {
      double constDivisor = Double.isFinite(constLimit) && constLimit > 0.0 ? constLimit : 4.0;
      return 20.0 / constDivisor;
   }

   public static double clampInteractionRange(double constLimit) {
      return constLimit > 0.0 ? Math.min(3.0, constLimit) : 0.0;
   }

   public static boolean isAttackReady(boolean constFlag, boolean underFortyThreshold, boolean constFlag3, float constLimit, int constValue) {
      return constFlag && underFortyThreshold && constFlag3 && constLimit >= 0.6F && constValue <= 0;
   }

   public static boolean isUnderFortyThreshold(double constValue) {
      return constValue <= 40.0;
   }

   public static boolean isSafelyBeyondRange(double constLimit, double groundY) {
      return Double.isFinite(groundY) && !(groundY > 4.5) ? !Double.isFinite(constLimit) || constLimit > groundY + 2.0 : false;
   }

   public static int getStepCountForFlag(double constValue, boolean constFlag) {
      return !constFlag && !isWithinCloseRange(constValue) ? 1 : 2;
   }

   public static double getRangeThreshold(int constValue, double gapChargeBound, double constValue3) {
      if (constValue <= 0) {
         return 2.9;
      } else {
         return constValue <= 2 ? 3.08 + 0.1 * (constValue - 1) + constValue3 : Math.max(gapChargeBound + constValue3, 2.9 + 0.26 * constValue);
      }
   }

   public static boolean exceedsBothThresholds(double constLimit, double constLimit2) {
      return constLimit >= 100.0 && constLimit2 > 0.05;
   }

   public static int compareWithMargin(double constLimit, double constValue, double constBound) {
      if (Double.isFinite(constLimit) && Double.isFinite(constValue)) {
         double maxGap = Math.max(0.0, constBound);
         if (constLimit > constValue + maxGap) {
            return 1;
         } else {
            return constLimit < constValue - maxGap ? -1 : 0;
         }
      } else {
         return 0;
      }
   }

   public static float getChargeFraction(long constLimit, double constValue) {
      if (constLimit >= 0L && constLimit < 2147483647L) {
         double constBound = (constLimit + 0.5) / getAttackCooldownTicks(constValue);
         return (float)Math.max(0.0, Math.min(1.0, constBound));
      } else {
         return 1.0F;
      }
   }

   public static int getTicksToReachThreshold(float constBound, float constLimit, float constLimit2) {
      if (!(constBound < constLimit2)) {
         return 0;
      } else {
         float constFactor = Float.isFinite(constLimit) && constLimit > 0.0F ? constLimit : 12.5F;
         return Math.max(1, (int)Math.ceil((constLimit2 - Math.max(0.0F, constBound)) * constFactor - 1.0E-4F));
      }
   }
}
