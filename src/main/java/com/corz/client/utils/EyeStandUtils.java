package com.corz.client.utils;

public final class EyeStandUtils {
   public static EyeStandUtils.Verdict getEyeStand(
      boolean eyeStandFlag, boolean canPose, boolean eyeStandFlag3, boolean eyeStandFlag4, boolean eyeStandFlag5, boolean eyeStandFlag6, boolean eyeStandFlag7
   ) {
      if (!eyeStandFlag) {
         return EyeStandUtils.Verdict.STAND_MISMATCH;
      } else if (!canPose) {
         return eyeStandFlag3 ? EyeStandUtils.Verdict.SETTLE_EYE : EyeStandUtils.Verdict.EYE_UNAVAILABLE;
      } else if (eyeStandFlag4) {
         return EyeStandUtils.Verdict.ACCEPT;
      } else {
         return (!eyeStandFlag5 || !eyeStandFlag6) && eyeStandFlag7 ? EyeStandUtils.Verdict.NUDGE_TO_STAND_CENTRE : EyeStandUtils.Verdict.PARITY_FAILURE;
      }
   }

   private EyeStandUtils() {
   }

   public static EyeStandUtils.Verdict getStandEyeType(boolean standEyeFlag, boolean standEyeFlag2, boolean standEyeFlag3, boolean standEyeFlag4, boolean standEyeFlag5) {
      return getEyeStand(standEyeFlag, true, true, standEyeFlag2, standEyeFlag3, standEyeFlag4, standEyeFlag5);
   }

   public static enum Verdict {
      ACCEPT,
      SETTLE_EYE,
      EYE_UNAVAILABLE,
      NUDGE_TO_STAND_CENTRE,
      STAND_MISMATCH,
      PARITY_FAILURE;
   }
}
