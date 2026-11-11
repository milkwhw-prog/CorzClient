package com.corz.client.utils;

public final class BuilderAutoUtils {
   public static int clampToOneToFour(int builderAutoBound) {
      return Math.max(1, Math.min(4, builderAutoBound));
   }

   public static int getMismatchBit(boolean builderAutoFlag, boolean builderAutoFlag2) {
      return builderAutoFlag == builderAutoFlag2 ? 0 : 1;
   }

   public static int getInequalityBit(boolean builderAutoFlag, boolean builderAutoFlag2) {
      return builderAutoFlag == builderAutoFlag2 ? 0 : 1;
   }

   private BuilderAutoUtils() {
   }

   public static int getDifferenceBit(boolean builderAutoFlag, boolean builderAutoFlag2) {
      return builderAutoFlag == builderAutoFlag2 ? 0 : 1;
   }

   public static int getCyclicDifferenceModForInt(int builderAutoValue, int builderAutoBound) {
      int clampToOneToFour3 = clampToOneToFour(builderAutoValue);
      int clampToOneToFour4 = clampToOneToFour(builderAutoBound);
      return ((clampToOneToFour4 - clampToOneToFour3) % 4 + 4) % 4;
   }

   public static boolean isOpenOrAboveHalf(int builderAutoValue, double builderAutoLimit) {
      if (builderAutoValue == 0) {
         return true;
      } else {
         return builderAutoValue == 1 ? false : builderAutoLimit > 0.5;
      }
   }
}
