package com.corz.client.utils;
import com.corz.client.utils.CrouchedBodyUtils;

public final class SlidingPulseUtils {

   static boolean passesClosestPoint(CrouchedBodyUtils.Interval interval, double slidingPulseValue, double d6, double x, double z) {
      if (interval.containsPoint(x, z)) {
         return false;
      } else {
         double[] axisLateral = clampToInterval(interval, slidingPulseValue, d6);
         double slidingPulseFactor = axisLateral[0] - slidingPulseValue;
         double slidingPulseFactor2 = axisLateral[1] - d6;
         double slidingPulseFactor3 = axisLateral[0] - x;
         double slidingPulseFactor4 = axisLateral[1] - z;
         return slidingPulseFactor * slidingPulseFactor3 + slidingPulseFactor2 * slidingPulseFactor4 < 0.0;
      }
   }

   static double[] clampToInterval(CrouchedBodyUtils.Interval interval, double axisLateralValue, double d6) {
      double axisLateralBound = interval.axis() == 2 ? d6 : axisLateralValue;
      double goalBound = interval.axis() == 2 ? axisLateralValue : d6;
      double max3 = Math.max(interval.lo(), Math.min(interval.hi(), axisLateralBound));
      double maxLateralLo = Math.max(interval.lateralLo(), Math.min(interval.lateralHi(), goalBound));
      return interval.axis() == 2 ? new double[]{maxLateralLo, max3} : new double[]{max3, maxLateralLo};
   }

   public static double getDriftDistance(double slidingPulseFactor, double slidingPulseValue) {
      double dampedFactor = getDampedFactor(slidingPulseValue);
      return slidingPulseFactor * dampedFactor / (1.0 - dampedFactor);
   }

   public static double getPulseImpulse(double slidingPulseValue) {
      double dampedFactor = getDampedFactor(0.6);
      double slidingPulseDivisor = getDampedFactor(slidingPulseValue);
      double slidingPulseFactor = dampedFactor / slidingPulseDivisor;
      return 0.03 * slidingPulseFactor * slidingPulseFactor * slidingPulseFactor;
   }

   static double getMaxSettleProjection(CrouchedBodyUtils.Interval interval, double lateralSettleValue, double d6) {
      double deltaX = interval.settleX() - lateralSettleValue;
      double deltaZ = interval.settleZ() - d6;
      double hypot = Math.hypot(deltaX, deltaZ);
      if (hypot < 1.0E-9) {
         return 0.0;
      } else {
         double lateralSettleFactor = deltaX / hypot;
         double lateralSettleFactor2 = deltaZ / hypot;
         double[][] doubles = interval.axis() == 2
            ? new double[][]{{interval.lateralLo(), interval.lo()}, {interval.lateralLo(), interval.hi()}, {interval.lateralHi(), interval.lo()}, {interval.lateralHi(), interval.hi()}}
            : new double[][]{{interval.lo(), interval.lateralLo()}, {interval.lo(), interval.lateralHi()}, {interval.hi(), interval.lateralLo()}, {interval.hi(), interval.lateralHi()}};
         double lateralSettle = 0.0;

         for (double[] lateralSettleValues : doubles) {
            lateralSettle = Math.max(lateralSettle, (lateralSettleValues[0] - lateralSettleValue) * lateralSettleFactor + (lateralSettleValues[1] - d6) * lateralSettleFactor2);
         }

         return lateralSettle;
      }
   }

   private SlidingPulseUtils() {
   }

   public static double getDampedFactor(double slidingPulseValue) {
      return Math.min(0.995, slidingPulseValue * 0.91);
   }

   public static boolean isHighFriction(double slidingPulseLimit) {
      return slidingPulseLimit > 0.7;
   }

   public static int getFrictionStepValue(double slidingPulseValue) {
      return isHighFriction(slidingPulseValue) ? 70 : 40;
   }

   public static SlidingPulseUtils.Decision decideSlidingAction(
      CrouchedBodyUtils.Interval interval, double slidingSettledValue, double d6, double deltaX, double deltaZ, double slidingPulseValue
   ) {
      double hypot = Math.hypot(deltaX, deltaZ);
      boolean lateralAxis = interval.containsPoint(slidingSettledValue, d6);
      double slidingSettledFactor = hypot > 1.0E-9 ? getDriftDistance(hypot, slidingPulseValue) : 0.0;
      double x = hypot > 1.0E-9 ? slidingSettledValue + deltaX / hypot * slidingSettledFactor : slidingSettledValue;
      double z = hypot > 1.0E-9 ? d6 + deltaZ / hypot * slidingSettledFactor : d6;
      boolean slidingSettledFlag = hypot > 0.001 && interval.containsPoint(x, z);
      if (!isHighFriction(slidingPulseValue)) {
         if (lateralAxis) {
            return hypot > 0.01
               ? new SlidingPulseUtils.Decision(
                  SlidingPulseUtils.Action.WAIT_STOP, SlidingPulseUtils.Phase.SETTLE, true, slidingSettledFactor, slidingSettledFlag, false, "stopping inside"
               )
               : new SlidingPulseUtils.Decision(SlidingPulseUtils.Action.SETTLED, SlidingPulseUtils.Phase.EXECUTE, true, slidingSettledFactor, slidingSettledFlag, false, "settled");
         } else {
            return slidingSettledFlag
               ? new SlidingPulseUtils.Decision(SlidingPulseUtils.Action.COAST, SlidingPulseUtils.Phase.STABILIZE, false, slidingSettledFactor, true, false, "sliding in")
               : new SlidingPulseUtils.Decision(SlidingPulseUtils.Action.DRIVE, SlidingPulseUtils.Phase.APPROACH, false, slidingSettledFactor, false, false, "approach");
         }
      } else {
         boolean slidingSettledFlag2 = hypot > 0.001 && !slidingSettledFlag && passesClosestPoint(interval, slidingSettledValue, d6, x, z);
         if (lateralAxis) {
            return hypot <= 0.004
               ? new SlidingPulseUtils.Decision(SlidingPulseUtils.Action.SETTLED, SlidingPulseUtils.Phase.EXECUTE, true, slidingSettledFactor, slidingSettledFlag, false, "settled")
               : new SlidingPulseUtils.Decision(
                  SlidingPulseUtils.Action.BRAKE, SlidingPulseUtils.Phase.BRAKE, true, slidingSettledFactor, slidingSettledFlag, !slidingSettledFlag, "braking inside the interval"
               );
         } else {
            double lateralSettle = getMaxSettleProjection(interval, slidingSettledValue, d6);
            double slidingPulseFactor = hypot + getPulseImpulse(slidingPulseValue);
            double slidingSettledLimit = slidingPulseFactor + getDriftDistance(slidingPulseFactor, slidingPulseValue);
            if (slidingSettledFlag) {
               double slidingSettledLimit2 = (interval.settleX() - x) * (hypot > 1.0E-9 ? deltaX / hypot : 0.0) + (interval.settleZ() - z) * (hypot > 1.0E-9 ? deltaZ / hypot : 0.0);
               return slidingSettledLimit2 > 0.05 && slidingSettledLimit <= lateralSettle
                  ? new SlidingPulseUtils.Decision(
                     SlidingPulseUtils.Action.DRIVE,
                     SlidingPulseUtils.Phase.MICRO_ADJUST,
                     false,
                     slidingSettledFactor,
                     true,
                     false,
                     "stop short of the settle point: one more pulse"
                  )
                  : new SlidingPulseUtils.Decision(SlidingPulseUtils.Action.COAST, SlidingPulseUtils.Phase.STABILIZE, false, slidingSettledFactor, true, false, "sliding in");
            } else if (slidingSettledFlag2) {
               return new SlidingPulseUtils.Decision(
                  SlidingPulseUtils.Action.BRAKE, SlidingPulseUtils.Phase.BRAKE, false, slidingSettledFactor, false, true, "braking: momentum carries past the interval"
               );
            } else {
               double distanceToPoint = interval.distanceToPoint(slidingSettledValue, d6);
               return slidingSettledLimit > lateralSettle
                  ? new SlidingPulseUtils.Decision(
                     SlidingPulseUtils.Action.COAST, SlidingPulseUtils.Phase.MICRO_ADJUST, false, slidingSettledFactor, false, false, "pulse would overshoot: coasting"
                  )
                  : new SlidingPulseUtils.Decision(
                     SlidingPulseUtils.Action.DRIVE,
                     distanceToPoint <= 0.5 ? SlidingPulseUtils.Phase.MICRO_ADJUST : SlidingPulseUtils.Phase.APPROACH,
                     false,
                     slidingSettledFactor,
                     false,
                     false,
                     "approach"
                  );
            }
         }
      }
   }

   public static enum Action {
      DRIVE,
      COAST,
      BRAKE,
      WAIT_STOP,
      SETTLED;
   }

   public record Decision(
      SlidingPulseUtils.Action action, SlidingPulseUtils.Phase phase, boolean inside, double slide, boolean projectedInside, boolean overshoot, String note
   ) {
   }

   public static enum Phase {
      APPROACH,
      BRAKE,
      STABILIZE,
      MICRO_ADJUST,
      SETTLE,
      EXECUTE;
   }
}
