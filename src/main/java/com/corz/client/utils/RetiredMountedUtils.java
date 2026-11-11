package com.corz.client.utils;

public final class RetiredMountedUtils {
   public static RetiredMountedUtils.Step getSkipPresent(boolean skipPresentFlag, boolean skipPresentFlag2) {
      if (skipPresentFlag) {
         return RetiredMountedUtils.Step.SKIP_PRESENT;
      } else {
         return skipPresentFlag2 ? RetiredMountedUtils.Step.PLACE : RetiredMountedUtils.Step.BLOCKED;
      }
   }

   public static boolean canBuildingPlanned(RetiredMountedUtils.Phase phase, boolean buildingPlannedFlag) {
      return buildingPlannedFlag
         ? true
         : phase == RetiredMountedUtils.Phase.PLANNED || phase == RetiredMountedUtils.Phase.APPROACH || phase == RetiredMountedUtils.Phase.BUILDING;
   }

   public static RetiredMountedUtils.Phase getRetiredMounted(RetiredMountedUtils.Phase phase, RetiredMountedUtils.Event event) {
      return switch (event) {
         case ARRIVED_AT_BASE -> phase == RetiredMountedUtils.Phase.PLANNED ? RetiredMountedUtils.Phase.APPROACH : phase;
         case CELL_PLACED -> phase.ordinal() <= RetiredMountedUtils.Phase.BUILDING.ordinal() ? RetiredMountedUtils.Phase.BUILDING : phase;
         case MOUNTED -> phase == RetiredMountedUtils.Phase.RETIRED ? RetiredMountedUtils.Phase.RETIRED : RetiredMountedUtils.Phase.MOUNTED;
         case PLACED_FROM_PLATFORM -> phase != RetiredMountedUtils.Phase.MOUNTED && phase != RetiredMountedUtils.Phase.USING ? phase : RetiredMountedUtils.Phase.USING;
         case COLUMN_RESET -> RetiredMountedUtils.Phase.PLANNED;
         case RETIRED -> RetiredMountedUtils.Phase.RETIRED;
         case REEVALUATION -> phase;
      };
   }

   private RetiredMountedUtils() {
   }

   public static enum Event {
      ARRIVED_AT_BASE,
      CELL_PLACED,
      MOUNTED,
      PLACED_FROM_PLATFORM,
      COLUMN_RESET,
      RETIRED,
      REEVALUATION;
   }

   public static enum Phase {
      PLANNED,
      APPROACH,
      BUILDING,
      MOUNTED,
      USING,
      RETIRED;
   }

   public static enum Step {
      SKIP_PRESENT,
      PLACE,
      BLOCKED;
   }
}
