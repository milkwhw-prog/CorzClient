package com.corz.client.utils;

public final class ContinueGenericUtils {

   public static boolean isWithinContinueLimit(int continueGenericLimit) {
      return continueGenericLimit > -9000;
   }

   public static ContinueGenericUtils.Verdict getContinueGeneric(int continueGenericValue, int continueGenericLimit, boolean continueGenericFlag, boolean continueGenericFlag2) {
      if (isWithinContinueLimit(continueGenericValue)) {
         return continueGenericFlag && continueGenericLimit >= continueGenericValue ? ContinueGenericUtils.Verdict.MISSION_TOP_REACHED : ContinueGenericUtils.Verdict.CONTINUE;
      } else {
         return continueGenericFlag && continueGenericFlag2 ? ContinueGenericUtils.Verdict.GENERIC_EXIT_ALLOWED : ContinueGenericUtils.Verdict.CONTINUE;
      }
   }

   private ContinueGenericUtils() {
   }

   public static enum Verdict {
      CONTINUE,
      MISSION_TOP_REACHED,
      GENERIC_EXIT_ALLOWED;
   }
}
