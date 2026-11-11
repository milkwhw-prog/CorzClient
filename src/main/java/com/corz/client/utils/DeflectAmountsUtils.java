package com.corz.client.utils;

public final class DeflectAmountsUtils {
   private static final double[] deflectTable = new double[]{6.0, 10.0, 14.0, 20.0, 30.0};
   private static final int[] table = new int[]{5, 7, 10, 15, 23};

   public static double[] getAmounts(double deflectValue, double deflectValue2, double deflectValue3, boolean deflectFlag, int deflectLimit) {
      double my2 = deflectValue;
      double mx2 = deflectValue2;
      double onGround2 = deflectValue3;

      for (int i = 0; i < deflectLimit; i++) {
         if (deflectFlag) {
            my2 *= 0.546;
            onGround2 *= 0.546;
            mx2 = 0.0;
         } else {
            my2 *= 0.91;
            onGround2 *= 0.91;
            mx2 = (mx2 - 0.08) * 0.98;
         }
      }

      return new double[]{my2, mx2, onGround2};
   }

   public static double[] getAmounts(float degrees, float degrees2, double deflectValue, double deflectValue2, double deflectValue3) {
      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(degrees2);
      double deflectFactor = -Math.sin(angle) * Math.cos(radians);
      double sin = -Math.sin(radians);
      double deflectFactor2 = Math.cos(angle) * Math.cos(radians);
      double distance = Math.sqrt(deflectFactor * deflectFactor + sin * sin + deflectFactor2 * deflectFactor2);
      if (distance > 0.0) {
         deflectFactor /= distance;
         sin /= distance;
         deflectFactor2 /= distance;
      }

      return new double[]{deflectFactor * 1.5 + deflectValue, sin * 1.5 + deflectValue2, deflectFactor2 * 1.5 + deflectValue3};
   }

   public static double getAmount(double degrees, double deflectValue) {
      if (Double.isFinite(degrees) && Double.isFinite(deflectValue)) {
         double angle = Math.toRadians(degrees);
         double cosScaled = Math.cos(angle) * 1.5;
         double deflectValue2 = -Math.sin(angle) * 1.5;
         double deflectValue3 = 0.0;
         double pitchDegrees2 = deflectValue;

         for (int i = 1; i <= 400; i++) {
            double deflectValue4 = deflectValue2 - 0.03;
            cosScaled *= 0.99;
            deflectValue2 = deflectValue4 * 0.99;
            double deflectValue5 = deflectValue3 + cosScaled;
            double deflectValue6 = pitchDegrees2 + deflectValue2;
            if (deflectValue6 <= 0.0) {
               double deflectBound = pitchDegrees2 - deflectValue6 > 0.0 ? pitchDegrees2 / (pitchDegrees2 - deflectValue6) : 0.0;
               return deflectValue3 + (deflectValue5 - deflectValue3) * Math.max(0.0, deflectBound);
            }

            deflectValue3 = deflectValue5;
            pitchDegrees2 = deflectValue6;
         }

         return Double.POSITIVE_INFINITY;
      } else {
         return Double.POSITIVE_INFINITY;
      }
   }

   public static double getAmount(double deflectValue, double deltaZ, double degrees) {
      return Math.hypot(deflectValue, deltaZ) * Math.sin(Math.toRadians(degrees));
   }

   private DeflectAmountsUtils() {
   }

   public static int getAmounts(double deflectLimit) {
      int index = deflectTable.length - 1;
      if (!Double.isNaN(deflectLimit) && !(deflectLimit >= deflectTable[index])) {
         if (deflectLimit <= deflectTable[0]) {
            return table[0];
         } else {
            for (int i = 0; i < index; i++) {
               double deflectValue = deflectTable[i];
               double deflectLimit2 = deflectTable[i + 1];
               if (deflectLimit <= deflectLimit2) {
                  double deflectFactor = (deflectLimit - deflectValue) / (deflectLimit2 - deflectValue);
                  double deflectValue2 = table[i] + deflectFactor * (table[i + 1] - table[i]);
                  return (int)Math.ceil(deflectValue2 - 1.0E-9);
               }
            }

            return table[index];
         }
      } else {
         return table[index];
      }
   }

   static double getAmount(double deflectValue) {
      return (deflectValue % 360.0 + 540.0) % 360.0 - 180.0;
   }

   static double getAmountForDouble(double deflectValue, double x) {
      return Math.toDegrees(Math.atan2(-deflectValue, x));
   }

   public static double getAmount(double deflectValue, double deltaZ, double deflectValue3, double x) {
      return getAmount(getAmountForDouble(deflectValue, deltaZ) - getAmountForDouble(deflectValue3, x));
   }
}
