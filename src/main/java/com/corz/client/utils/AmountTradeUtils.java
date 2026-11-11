package com.corz.client.utils;

public final class AmountTradeUtils {

   public static float getAmount(double tradeValue, double playerEyeValue) {
      if (tradeValue <= 0.25) {
         return 89.0F;
      } else {
         double max3 = getMax(tradeValue);
         float tradeTotal4 = 89.0F;
         double tradeLimit = Double.MAX_VALUE;

         for (float tradeTotal = 89.0F; tradeTotal >= -15.0F; tradeTotal -= 0.5F) {
            double amountForFloatDouble = getAmountForFloatDouble(tradeTotal, max3, playerEyeValue);
            if (amountForFloatDouble < tradeLimit) {
               tradeLimit = amountForFloatDouble;
               tradeTotal4 = tradeTotal;
            }
         }

         return tradeTotal4;
      }
   }

   public static double getAmountForDouble(double tradeValue, double tradeLimit) {
      double amountForFloatDouble = getAmountForFloatDouble(getAmount(tradeValue, tradeLimit), getMax(tradeValue), tradeLimit);
      return amountForFloatDouble == Double.MAX_VALUE ? -1.0 : 0.35 - amountForFloatDouble;
   }

   public static double getMax(double tradeValue) {
      return Math.max(0.25, tradeValue - 0.15);
   }

   public static double getAmount(float tradeValue, double tradeLimit) {
      return getAmount(tradeValue, tradeLimit, 0.0);
   }

   private AmountTradeUtils() {
   }

   public static double getAmount(float degrees, double tradeLimit, double tradeValue) {
      double angle = Math.toRadians(degrees);
      double cosScaled = Math.cos(angle) * 0.3;
      double tradeValue2 = -Math.sin(angle) * 0.3 + 0.1 + tradeValue;
      double tradeValue3 = 0.0;
      double tradeLimit2 = 0.0;

      for (int i = 0; i < 200; i++) {
         double tradeValue4 = tradeValue2 - 0.04;
         double tradeValue5 = tradeValue3 + cosScaled;
         double tradeValue6 = tradeLimit2 + tradeValue4;
         if (tradeLimit2 > tradeLimit && tradeValue6 <= tradeLimit) {
            double tradeFactor = (tradeLimit2 - tradeLimit) / (tradeLimit2 - tradeValue6);
            return tradeValue3 + (tradeValue5 - tradeValue3) * tradeFactor;
         }

         tradeValue3 = tradeValue5;
         tradeLimit2 = tradeValue6;
         cosScaled *= 0.98;
         tradeValue2 = tradeValue4 * 0.98;
      }

      return Double.NaN;
   }

   public static double getAmountForFloatDouble(float tradeValue, double tradeValue2, double tradeLimit) {
      double vecHashValue = getAmount(tradeValue, tradeLimit, -0.07);
      double tradeValue5 = getAmount(tradeValue, tradeLimit, 0.07);
      return !Double.isNaN(vecHashValue) && !Double.isNaN(tradeValue5) ? Math.max(Math.abs(vecHashValue - tradeValue2), Math.abs(tradeValue5 - tradeValue2)) : Double.MAX_VALUE;
   }
}
