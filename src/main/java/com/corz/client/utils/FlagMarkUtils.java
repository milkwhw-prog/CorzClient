package com.corz.client.utils;

public final class FlagMarkUtils {

   public static boolean canFlagMarkPair(int markValue, int markValue2) {
      return markValue - markValue2 >= 80;
   }

   public static boolean canFlagMarkLongs(long markValue, boolean markFlag, long l) {
      return l == markValue || markFlag;
   }

   public static boolean canFlagMarkWithLimit(int markValue, int markLimit, int markValue2) {
      return markLimit >= 0 && (markValue - markLimit > 80 || markValue - markValue2 > 20);
   }

   private FlagMarkUtils() {
   }

   public static boolean canFlagMarkLimits(int markLimit, int markLimit2, boolean markFlag) {
      return markLimit > -9000 && markFlag && markLimit2 >= markLimit;
   }

   public static boolean canFlagMarkLimit(int markLimit) {
      return markLimit > 24;
   }

   public static boolean canFlagMarkLimitAlternate(int markValue, int markValue2, int markLimit) {
      return markValue - markValue2 > 120 || markLimit > 2;
   }

   public static FlagMarkUtils.ClimbProgress getMarkStall(FlagMarkUtils.ClimbProgress markStall, double markStallLimit, boolean markStallFlag) {
      if (!markStallFlag) {
         return markStall;
      } else if (markStall.mark() == Double.NEGATIVE_INFINITY) {
         return new FlagMarkUtils.ClimbProgress(markStallLimit, 0);
      } else {
         return markStallLimit > markStall.mark() + 0.05
            ? new FlagMarkUtils.ClimbProgress(markStallLimit, 0)
            : new FlagMarkUtils.ClimbProgress(markStall.mark(), markStall.stallTicks() + 1);
      }
   }

   public record ClimbProgress(double mark, int stallTicks) {

      public boolean isStalled() {
         return FlagMarkUtils.canFlagMarkLimit(this.stallTicks);
      }

      public static FlagMarkUtils.ClimbProgress getInitialProgress() {
         return new FlagMarkUtils.ClimbProgress(Double.NEGATIVE_INFINITY, 0);
      }
   }
}
