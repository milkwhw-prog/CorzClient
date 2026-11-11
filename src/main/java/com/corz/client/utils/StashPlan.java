package com.corz.client.utils;

public final class StashPlan {
   private StashPlan() {
   }

   public static StashPlan.Place getKitPlacement(boolean alreadyOpen, boolean hasKit, boolean hasSpot) {
      if (alreadyOpen) {
         return StashPlan.Place.OPEN;
      } else if (!hasKit) {
         return StashPlan.Place.NO_KIT;
      } else {
         return hasSpot ? StashPlan.Place.PLACE : StashPlan.Place.NO_SPOT;
      }
   }

   public static boolean isStashAllowed(boolean stashPlanFlag, boolean stashPlanFlag2, boolean stashPlanFlag3, int stashPlanValue) {
      return stashPlanFlag && (stashPlanFlag2 || stashPlanFlag3 && stashPlanValue == 1);
   }

   public static enum Place {
      OPEN,
      PLACE,
      NO_KIT,
      NO_SPOT;
   }
}
