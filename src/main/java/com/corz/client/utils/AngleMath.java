package com.corz.client.utils;

public final class AngleMath {
   public static double wrapDegrees(double signumValue) {
      double signumTotal = signumValue % 360.0;
      if (signumTotal >= 180.0) {
         signumTotal -= 360.0;
      }

      if (signumTotal < -180.0) {
         signumTotal += 360.0;
      }

      return signumTotal;
   }

   public static double pitchFromDelta(double signumValue, double x) {
      return Math.toDegrees(Math.atan2(-signumValue, x));
   }

   public static double lerp(double signumFactor, double signumValue, double signumValue2) {
      return signumValue + signumFactor * (signumValue2 - signumValue);
   }

   public static double clamp(double signumLimit, double boxMinYOffset, double signumValue) {
      return signumLimit < boxMinYOffset ? boxMinYOffset : (signumLimit > signumValue ? signumValue : signumLimit);
   }

   private AngleMath() {
   }

   public static double smoothStep(double signumValue, double signumValue2, double distance) {
      double signumFactor = clamp((distance - signumValue) / (signumValue2 - signumValue), 0.0, 1.0);
      return signumFactor * signumFactor * (3.0 - 2.0 * signumFactor);
   }

   public static double safePitchFromDelta(double y, double signumBound) {
      return -Math.toDegrees(Math.atan2(y, Math.max(signumBound, 1.0E-4)));
   }

   public static double pitchDeltaToward(double signumValue, double signumValue2, double x) {
      return wrapDegrees(pitchFromDelta(signumValue2, x) - signumValue);
   }
}
