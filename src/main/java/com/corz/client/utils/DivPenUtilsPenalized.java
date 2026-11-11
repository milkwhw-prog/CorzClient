package com.corz.client.utils;

public final class DivPenUtilsPenalized {

   public static DivPenUtilsPenalized.Result getIgnoredPenalized(int sampleCount, boolean forcedTrial, int penaltyScore, boolean hasPenaltyCandidate) {
      boolean isTrial = forcedTrial || sampleCount >= 40;
      if (!isTrial) {
         return DivPenUtilsPenalized.Result.NOT_TRIAL;
      } else if (!hasPenaltyCandidate) {
         return DivPenUtilsPenalized.Result.IGNORED;
      } else {
         return penaltyScore >= 60 ? DivPenUtilsPenalized.Result.PENALIZED : DivPenUtilsPenalized.Result.IGNORED;
      }
   }

   private DivPenUtilsPenalized() {
   }

   public static enum Result {
      IGNORED,
      PENALIZED,
      NOT_TRIAL;
   }
}
