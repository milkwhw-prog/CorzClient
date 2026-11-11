package com.corz.client.utils;
import com.corz.client.utils.IncompleteStand;

public final class ReplanArrivalUtils {

   public static ReplanArrivalUtils.Action getArrivalReplan(
      int arrivalReplanValue, boolean arrivalReplanFlag, double incompleteStandValue, long arrivalReplanValue3, long arrivalReplanValue4, boolean arrivalReplanFlag2, boolean arrivalReplanFlag3
   ) {
      boolean groundedAndSettled = IncompleteStand.isGroundedAndSettled(arrivalReplanFlag, incompleteStandValue);
      if (groundedAndSettled) {
         if (arrivalReplanValue4 == Long.MIN_VALUE) {
            return ReplanArrivalUtils.Action.REPLAN_STAND;
         } else if (!arrivalReplanFlag2) {
            return arrivalReplanValue3 == arrivalReplanValue4 ? ReplanArrivalUtils.Action.ARRIVAL_FAILED : ReplanArrivalUtils.Action.REPLAN_STAND;
         } else {
            return arrivalReplanValue3 != arrivalReplanValue4 ? ReplanArrivalUtils.Action.RETURN_TO_STAND : ReplanArrivalUtils.Action.ARRIVAL_FAILED;
         }
      } else {
         return arrivalReplanValue < 20 && arrivalReplanFlag3
            ? ReplanArrivalUtils.Action.SETTLE
            : (arrivalReplanValue < 20 ? ReplanArrivalUtils.Action.SETTLE : ReplanArrivalUtils.Action.ARRIVAL_FAILED);
      }
   }

   private ReplanArrivalUtils() {
   }

   public static enum Action {
      SETTLE,
      RETURN_TO_STAND,
      REPLAN_STAND,
      ARRIVAL_FAILED;
   }
}
