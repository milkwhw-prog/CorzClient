package com.corz.client.utils;

public final class ApproachPlan {

   public static boolean isWithinHorizontalRange(int approachPlanValue, int z2, int approachPlanValue3, int approachPlanValue4) {
      double approachPlanFactor = approachPlanValue - approachPlanValue3;
      double approachPlanFactor2 = z2 - approachPlanValue4;
      return approachPlanFactor * approachPlanFactor + approachPlanFactor2 * approachPlanFactor2 <= 36.0;
   }

   public static boolean isTimeoutReached(long approachPlanLimit) {
      return approachPlanLimit > 360000L;
   }

   public static double getHorizontalDistance(double approachPlanValue, double approachPlanValue2, int approachPlanValue3, int approachPlanValue4) {
      return Math.hypot(approachPlanValue3 + 0.5 - approachPlanValue, approachPlanValue4 + 0.5 - approachPlanValue2);
   }

   public static ApproachPlan.ApproachRange classifyDistance(double farNearValue, double farNearValue2, int farNearValue3, int farNearValue4) {
      return getHorizontalDistance(farNearValue, farNearValue2, farNearValue3, farNearValue4) > 28.0 ? ApproachPlan.ApproachRange.FAR : ApproachPlan.ApproachRange.NEAR;
   }

   public static int[] computeSearchBounds(int approachPlanBound, int approachPlanValue, int intersectsCenterBound, int spotBound, int approachPlanValue2, int approachPlanBound4) {
      int aggroPreemptBound = Math.min(approachPlanBound, spotBound) - 8;
      int approachPlanBound6 = Math.max(approachPlanBound, spotBound) + 8;
      int approachPlanBound7 = Math.min(intersectsCenterBound, approachPlanBound4) - 8;
      int approachPlanBound8 = Math.max(intersectsCenterBound, approachPlanBound4) + 8;
      int max3 = Math.max(aggroPreemptBound, approachPlanBound - 40);
      int min4 = Math.min(approachPlanBound6, approachPlanBound + 40);
      int max5 = Math.max(approachPlanBound7, intersectsCenterBound - 40);
      int min6 = Math.min(approachPlanBound8, intersectsCenterBound + 40);
      int min7 = Math.min(approachPlanValue2 - 3, approachPlanValue - 14);
      int max8 = Math.max(approachPlanValue2 + 4, approachPlanValue + 10);
      return new int[]{max3, min7, max5, min4, max8, min6};
   }

   private ApproachPlan() {
   }

   public static boolean shouldAbortApproach(boolean approachPlanFlag, long approachPlanValue, long approachPlanLimit, long approachPlanValue2, double throwAnchor, long approachPlanLimit3) {
      return approachPlanLimit3 > 8000L ? true : approachPlanFlag && approachPlanLimit >= approachPlanValue2 && approachPlanValue - approachPlanLimit >= 1000L && throwAnchor >= 0.95;
   }

   public static boolean hasExceededAttemptLimits(int approachPlanLimit, int approachPlanLimit2, long approachPlanLimit3) {
      return approachPlanLimit >= 3 || approachPlanLimit2 > 40 || approachPlanLimit3 > 120000L;
   }

   public static enum ApproachRange {
      FAR,
      NEAR;
   }
}
