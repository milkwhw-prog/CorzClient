package com.corz.client.utils;

public final class ShiftedBudgetMath {

   public static int getShiftedCapped(int builderAutoValue, int builderAutoValue2, int builderAutoValue3) {
      int min4 = Math.min(Math.max(0, builderAutoValue - 1), 4);
      long builderAutoBound = (long)builderAutoValue2 << min4;
      return (int)Math.min(builderAutoBound, (long)builderAutoValue3);
   }

   public static boolean isOnlyFirstSet(boolean builderAutoFlag, boolean builderAutoFlag2, boolean builderAutoFlag3) {
      if (!builderAutoFlag) {
         return false;
      } else {
         return builderAutoFlag2 ? false : !builderAutoFlag3;
      }
   }

   private ShiftedBudgetMath() {
   }
}
