package com.corz.client.utils;

public final class AmountUtils {
   public static double getAmount(double d, double d2, double d3) {
      return d + (d2 - d) * getAmount(d3);
   }

   private AmountUtils() {
   }

   public static double getAmount(long longValue, long divisor) {
      if (divisor <= 0L) {
         return 1.0;
      } else {
         double limit = (double)longValue / divisor;
         return limit < 0.0 ? 0.0 : (limit > 1.0 ? 1.0 : limit);
      }
   }

   public static long getTime(double d, double d2, long bound, long etaSeconds) {
      double divisor = d2 <= 0.0 ? 1.0 : d2;
      long round = Math.round(d / divisor * 6.0);
      return Math.max(bound, Math.min(etaSeconds, round));
   }

   public static double getAmountForDouble(double d, double d2, double d3) {
      return d + (d2 - d) * getAmount(d3);
   }

   public static double getAmount(double factor) {
      factor = factor < 0.0 ? 0.0 : (factor > 1.0 ? 1.0 : factor);
      return factor * factor * (3.0 - 2.0 * factor);
   }
}
