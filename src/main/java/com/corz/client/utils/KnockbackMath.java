package com.corz.client.utils;

public final class KnockbackMath {

   public static boolean isFacingWithinDegrees(double knockbackMathValue, double knockbackMathValue2, double knockbackMathValue3, double knockbackMathValue4, double knockbackMathValue5, double knockbackMathLimit) {
      return getAngleDifferenceToTarget(knockbackMathValue, knockbackMathValue2, knockbackMathValue3, knockbackMathValue4, knockbackMathValue5) < knockbackMathLimit;
   }

   public static boolean hasMinGroundTicks(int knockbackMathLimit) {
      return knockbackMathLimit >= 3;
   }

   public static boolean isWithinWindowSince(long knockbackMathValue, long knockbackMathLimit, long knockbackMathLimit2) {
      return knockbackMathLimit >= 0L && knockbackMathValue - knockbackMathLimit <= knockbackMathLimit2;
   }

   static double normalizeDegrees(double knockbackMathValue) {
      double knockbackMathTotal = knockbackMathValue % 360.0;
      if (knockbackMathTotal >= 180.0) {
         knockbackMathTotal -= 360.0;
      }

      if (knockbackMathTotal < -180.0) {
         knockbackMathTotal += 360.0;
      }

      return knockbackMathTotal;
   }

   public static boolean isSprintHitReady(boolean knockbackMathFlag, float knockbackMathLimit) {
      return knockbackMathFlag && knockbackMathLimit > 0.9F;
   }

   public static boolean isHurtTimeActiveOrRecent(int knockbackMathLimit, int knockbackMathValue, int knockbackMathLimit2) {
      return knockbackMathLimit > 0 || knockbackMathValue <= knockbackMathLimit2;
   }

   public static boolean isKnockbackVelocity(double knockbackMathFactor, double knockbackMathLimit, double knockbackMathFactor2) {
      return knockbackMathFactor * knockbackMathFactor + knockbackMathFactor2 * knockbackMathFactor2 > 1.0E-8 && knockbackMathLimit >= 0.0;
   }

   public static boolean isAtMostBound(int knockbackMathValue, int knockbackMathBound) {
      return knockbackMathValue <= Math.max(0, knockbackMathBound);
   }

   static double getAngleDifferenceToTarget(double knockbackMathValue, double knockbackMathValue2, double knockbackMathValue3, double knockbackMathValue4, double knockbackMathValue5) {
      double knockbackMathFactor = knockbackMathValue4 - knockbackMathValue2;
      double x = knockbackMathValue5 - knockbackMathValue3;
      if (knockbackMathFactor * knockbackMathFactor + x * x < 1.0E-12) {
         return 0.0;
      } else {
         double degrees = Math.toDegrees(Math.atan2(-knockbackMathFactor, x));
         return Math.abs(normalizeDegrees(degrees - knockbackMathValue));
      }
   }

   public static long rollKnockbackDelayMs(boolean knockbackMathFlag, double knockbackMathValue, double knockbackMathValue2, double knockbackMathValue3, double knockbackMathValue4, double knockbackMathValue5, long knockbackMathBound) {
      double knockbackMathLimit = knockbackMathFlag ? lerpClamped(knockbackMathValue3, knockbackMathValue4, knockbackMathValue5) : lerpClamped(knockbackMathValue, knockbackMathValue2, knockbackMathValue5);
      return !(knockbackMathLimit > 0.0) ? 0L : Math.min(knockbackMathBound, Math.round(knockbackMathLimit));
   }

   public static boolean isKnockbackDelayExpired(boolean knockbackMathFlag, int knockbackMathValue, long knockbackMathLimit, long knockbackMathValue2) {
      return knockbackMathFlag && knockbackMathValue <= 0 || knockbackMathLimit >= knockbackMathValue2;
   }

   public static boolean hasCooldownElapsed(long knockbackMathValue, long knockbackMathValue2, long knockbackMathValue3) {
      return knockbackMathValue2 < 0L || knockbackMathValue - knockbackMathValue2 > knockbackMathValue3;
   }

   public static boolean rollPercentChance(double knockbackMathLimit, double knockbackMathValue) {
      if (knockbackMathLimit >= 100.0) {
         return true;
      } else {
         return !(knockbackMathLimit > 0.0) ? false : knockbackMathValue * 100.0 < knockbackMathLimit;
      }
   }

   public static int msToTicks(double knockbackMathLimit) {
      return !(knockbackMathLimit > 0.0) ? 0 : (int)Math.min(2147483647L, Math.round(knockbackMathLimit / 50.0));
   }

   public static double lerpClamped(double knockbackMathValue, double knockbackMathLimit, double knockbackMathBound) {
      return !(knockbackMathLimit > knockbackMathValue) ? knockbackMathValue : knockbackMathValue + (knockbackMathLimit - knockbackMathValue) * Math.max(0.0, Math.min(1.0, knockbackMathBound));
   }

   public static KnockbackMath.JumpStep getJumpResetStep(int pressDropValue, boolean pressDropFlag, boolean pressDropFlag2) {
      if (pressDropFlag && pressDropValue < 3) {
         return pressDropFlag2 ? KnockbackMath.JumpStep.PRESS : KnockbackMath.JumpStep.WAIT;
      } else {
         return KnockbackMath.JumpStep.DROP;
      }
   }

   private KnockbackMath() {
   }

   public static enum JumpStep {
      WAIT,
      PRESS,
      DROP;
   }
}
