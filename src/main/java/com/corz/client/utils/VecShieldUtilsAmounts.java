package com.corz.client.utils;

public final class VecShieldUtilsAmounts {

   public static double getDegrees(double degreesLimit) {
      return !(degreesLimit > 0.0) ? 90.0 : Math.toDegrees(Math.atan(0.3 / degreesLimit));
   }

   public static float getAmount(double vecShieldValue, double x) {
      return (float)Math.toDegrees(Math.atan2(-vecShieldValue, x));
   }

   public static double[] getAmounts(double vecShieldValue, double vecShieldValue2, double vecShieldFactor, double vecShieldFactor2, double vecShieldFactor3) {
      return new double[]{vecShieldValue - vecShieldFactor * vecShieldFactor3, vecShieldValue2 - vecShieldFactor2 * vecShieldFactor3};
   }

   public static double getMin(double vecShieldValue) {
      return Math.min(5.0, 0.6 * getDegrees(vecShieldValue));
   }

   public static double[] getAmountsForDouble(double vecShieldValue, double vecShieldValue2, double vecShieldFactor, double vecShieldFactor2, double vecPosValue) {
      return new double[]{vecShieldValue + vecShieldFactor * vecPosValue, vecShieldValue2 + vecShieldFactor2 * vecPosValue};
   }

   private VecShieldUtilsAmounts() {
   }
}
