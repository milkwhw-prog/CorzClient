package com.corz.client.utils;

public final class CompoundCompleteUtils {

   public static boolean isCompoundLimitReached(int currentCount, int limit) {
      return currentCount >= limit;
   }

   public static boolean isCompoundFlagSet(boolean flag) {
      return flag;
   }

   public static boolean areBothCompoundFlagsSet(boolean firstFlag, boolean secondFlag) {
      return firstFlag && secondFlag;
   }

   public static CompoundCompleteUtils.Release getCompleteCompound(
      boolean isComplete, boolean isActive, boolean isCompound, int elapsedTime, int idleTime, int timeoutLimit, int noProgressLimit
   ) {
      if (!isActive || isComplete && !isCompound) {
         if (elapsedTime > timeoutLimit) {
            return CompoundCompleteUtils.Release.TIMEOUT;
         } else {
            return idleTime > noProgressLimit ? CompoundCompleteUtils.Release.NO_PROGRESS : CompoundCompleteUtils.Release.NONE;
         }
      } else {
         return isComplete ? CompoundCompleteUtils.Release.COMPOUND_COMPLETE : CompoundCompleteUtils.Release.COMPLETE;
      }
   }

   private CompoundCompleteUtils() {
   }

   public static enum Release {
      NONE,
      COMPLETE,
      COMPOUND_COMPLETE,
      TIMEOUT,
      NO_PROGRESS;
   }
}
