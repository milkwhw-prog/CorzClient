package com.corz.client.utils;

public final class FlagConstUtils {
   public static final int UNUSED_ONE = 1;

   public static boolean canFlag(boolean constFlag) {
      return !constFlag;
   }

   public static boolean canFlag(boolean constFlag, int constValue, double constLimit) {
      return canFlag(constValue, constLimit) == constFlag;
   }

   private FlagConstUtils() {
   }

   public static boolean canFlag(int constValue, double constLimit) {
      if (constValue == 0) {
         return true;
      } else {
         return constValue == 1 ? false : constLimit > 0.5;
      }
   }

   public static double getAmount(boolean constFlag) {
      return constFlag ? 0.78 : 0.22;
   }

   public static int getValue(float constValue) {
      int constValue2 = (int)Math.floor(constValue / 90.0 + 0.5) & 3;

      return switch (constValue2) {
         case 0 -> 3;
         case 1 -> 4;
         case 2 -> 2;
         default -> 5;
      };
   }

   public static float getAmount(int constValue) {
      return switch (constValue) {
         case 2 -> 180.0F;
         case 3 -> 0.0F;
         case 4 -> 90.0F;
         case 5 -> -90.0F;
         default -> 0.0F;
      };
   }
}
