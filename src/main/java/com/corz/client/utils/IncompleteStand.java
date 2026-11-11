package com.corz.client.utils;

public final class IncompleteStand {
   private final IncompleteStand.Reason reason;
   private static final IncompleteStand ARRIVED_INSTANCE = new IncompleteStand(IncompleteStand.Reason.ARRIVED);

   public IncompleteStand.Reason getReason() {
      return this.reason;
   }

   public static boolean isGroundedAndSettled(boolean incompleteStandFlag, double incompleteStandValue) {
      return incompleteStandFlag && incompleteStandValue <= 0.08;
   }

   public static IncompleteStand classifyArrival(long standIncompleteValue, long l2, boolean standIncompleteFlag, double incompleteStandValue, int standIncompleteLimit) {
      if (l2 == Long.MIN_VALUE) {
         return new IncompleteStand(IncompleteStand.Reason.NO_STAND);
      } else if (!isGroundedAndSettled(standIncompleteFlag, incompleteStandValue)) {
         return new IncompleteStand(IncompleteStand.Reason.AIRBORNE);
      } else if (standIncompleteLimit > 0) {
         return new IncompleteStand(IncompleteStand.Reason.ROUTE_INCOMPLETE);
      } else {
         return standIncompleteValue != l2 ? new IncompleteStand(IncompleteStand.Reason.NOT_AT_STAND) : ARRIVED_INSTANCE;
      }
   }

   @Override
   public String toString() {
      return "Arrival{" + this.reason + "}";
   }

   public boolean isAirborneOrRouteIncomplete() {
      return this.reason == IncompleteStand.Reason.AIRBORNE || this.reason == IncompleteStand.Reason.ROUTE_INCOMPLETE;
   }

   private IncompleteStand(IncompleteStand.Reason standIncompleteType) {
      this.reason = standIncompleteType;
   }

   public boolean hasArrived() {
      return this.reason == IncompleteStand.Reason.ARRIVED;
   }

   public static boolean isNearlyStill(double incompleteStandValue) {
      return incompleteStandValue <= 1.0E-4;
   }

   public static enum Reason {
      ARRIVED,
      AIRBORNE,
      NOT_AT_STAND,
      ROUTE_INCOMPLETE,
      NO_STAND;
   }
}
