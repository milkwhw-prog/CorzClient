package com.corz.client.utils;

public final class AmountsUtils {
   private AmountsUtils() {
   }

   static double getAmount(double[] doubles, double[] doubles2) {
      double hypot = Math.hypot(doubles[0], doubles[1]);
      double hypot2 = Math.hypot(doubles2[0], doubles2[1]);
      return !(hypot < 1.0E-9) && !(hypot2 < 1.0E-9) ? (doubles[0] * doubles2[0] + doubles[1] * doubles2[1]) / (hypot * hypot2) : -Double.MAX_VALUE;
   }

   public static int[] getValues(int n, int n2, float f, float f2) {
      if (n == 0 && n2 == 0) {
         return new int[]{0, 0};
      } else {
         double[] doubles = getAmounts(n, n2, f);
         int n3 = 0;
         int n4 = 0;
         double d = -Double.MAX_VALUE;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (i != 0 || j != 0) {
                  double limit = getAmount(doubles, getAmounts(j, i, f2));
                  if (limit > d) {
                     d = limit;
                     n3 = j;
                     n4 = i;
                  }
               }
            }
         }

         return new int[]{n3, n4};
      }
   }

   static double[] getAmounts(double factor, double n2, float degrees) {
      double angle = Math.toRadians(degrees);
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      return new double[]{factor * cos - n2 * sin, n2 * cos + factor * sin};
   }
}
