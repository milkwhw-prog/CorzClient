package com.corz.client.utils;

public final class AmountsUtilsFlag {
   private AmountsUtilsFlag() {
   }

   public static float[] getAmounts(int n) {
      return switch (n) {
         case 0 -> new float[]{0.0F, 90.0F};
         case 1 -> new float[]{0.0F, -90.0F};
         case 2 -> new float[]{180.0F, 0.0F};
         case 3 -> new float[]{0.0F, 0.0F};
         case 4 -> new float[]{90.0F, 0.0F};
         case 5 -> new float[]{-90.0F, 0.0F};
         default -> throw new IllegalArgumentException("bad look index " + n);
      };
   }

   private static float getAmount(float total) {
      total %= 360.0F;
      if (total >= 180.0F) {
         total -= 360.0F;
      }

      if (total < -180.0F) {
         total += 360.0F;
      }

      return total;
   }

   public static boolean canFlag(int n, float f, float f2, float limit) {
      float[] floats = getAmounts(n);
      float abs3 = Math.abs(getAmount(f - floats[0]));
      float abs4 = Math.abs(f2 - floats[1]);
      return n != 1 && n != 0 ? abs3 <= limit && abs4 <= limit : abs4 <= limit;
   }
}
