package com.corz.client.utils;

public final class WeakFullUtils {

   public static WeakFullUtils.Lift getWeakFull(double weakFullValue) {
      if (weakFullValue < 0.4) {
         return WeakFullUtils.Lift.FULL;
      } else {
         return weakFullValue < 0.8 ? WeakFullUtils.Lift.WEAK : WeakFullUtils.Lift.NONE;
      }
   }

   private WeakFullUtils() {
   }

   public static double getAmount(WeakFullUtils.Lift lift) {
      if (lift == null) {
         return 0.0;
      } else {
         return switch (lift) {
            case FULL -> 1.0;
            case WEAK -> 0.25;
            case NONE -> 0.0;
         };
      }
   }

   public static enum Lift {
      FULL,
      WEAK,
      NONE;
   }
}
