package com.corz.client.utils;

public final class FlagVictimUtils {

   public static FlagVictimUtils.CycleChoice getPlaceVictimChoice(
      boolean victimFlag, int victimLimit, int n2
   ) {
      if (!victimFlag) {
         return FlagVictimUtils.CycleChoice.DEADLOCKED_NEEDS_ESCAPE;
      } else {
         boolean victimFlag2 = victimLimit > 0;
         boolean victimFlag3 = n2 > 0;
         if (victimFlag2 && !victimFlag3) {
            return FlagVictimUtils.CycleChoice.PLACE_CANDIDATE;
         } else if (victimFlag3 && !victimFlag2) {
            return FlagVictimUtils.CycleChoice.PLACE_VICTIM;
         } else if (victimFlag2) {
            return victimLimit >= n2
               ? FlagVictimUtils.CycleChoice.PLACE_CANDIDATE
               : FlagVictimUtils.CycleChoice.PLACE_VICTIM;
         } else {
            return FlagVictimUtils.CycleChoice.DEADLOCKED_NEEDS_ESCAPE;
         }
      }
   }

   public static boolean canFlagVictimThree(int victimValue, int n2, boolean victimFlag) {
      return !victimFlag && canFlagVictimPair(victimValue, n2);
   }

   public static boolean canFlagVictimLimit(int victimLimit) {
      return victimLimit >= 0 && victimLimit <= 2;
   }

   public static boolean canFlagVictimPair(int victimLimit, int victimValue) {
      return victimLimit > 0 && victimLimit <= 2 && victimValue <= 0;
   }

   public static boolean canFlagVictimFlag(boolean victimFlag) {
      return victimFlag;
   }

   public static double getVictimAmount(int victimBound, boolean victimFlag) {
      if (victimFlag && canFlagVictimLimit(victimBound)) {
         int victimFactor = 2 - Math.max(0, Math.min(2, victimBound)) + 1;
         double victimFactor2 = 300000.0;
         return -Math.min(900000.0, victimFactor * victimFactor2);
      } else {
         return 0.0;
      }
   }

   private FlagVictimUtils() {
   }

   public static enum CycleChoice {
      PLACE_CANDIDATE,
      PLACE_VICTIM,
      DEADLOCKED_NEEDS_ESCAPE;
   }
}
