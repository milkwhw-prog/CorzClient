package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.FloorBuildPlanner;

public final class CrouchedBodyUtils {
   public static final double[] FACE_REACH_OFFSETS = new double[]{0.55, 0.67, 0.79};

   private static double findLargestClearMargin(CrouchedBodyUtils.Probe probe, double crouchedBodyValue, double r, double crouched, CrouchedBodyUtils.EyeMode eyeMode, double crouchedBodyValue4) {
      for (double eyeY2 = crouchedBodyValue4; eyeY2 >= 0.049999999999000004; eyeY2 *= 0.5) {
         if (probe.findPlacementHit(crouchedBodyValue - eyeY2, crouched, r - eyeY2, eyeMode) != null
            && probe.findPlacementHit(crouchedBodyValue + eyeY2, crouched, r - eyeY2, eyeMode) != null
            && probe.findPlacementHit(crouchedBodyValue - eyeY2, crouched, r + eyeY2, eyeMode) != null
            && probe.findPlacementHit(crouchedBodyValue + eyeY2, crouched, r + eyeY2, eyeMode) != null) {
            return eyeY2;
         }
      }

      return 0.0;
   }

   static List<CrouchedBodyUtils.EyeMode> getEyeModeOrder(boolean crouchedStandingFlag, boolean crouchedStandingFlag2) {
      ArrayList arrayList = new ArrayList(2);
      if (crouchedStandingFlag2) {
         arrayList.add(CrouchedBodyUtils.EyeMode.CROUCHED);
         return arrayList;
      } else {
         if (crouchedStandingFlag) {
            arrayList.add(CrouchedBodyUtils.EyeMode.CROUCHED);
            arrayList.add(CrouchedBodyUtils.EyeMode.STANDING);
         } else {
            arrayList.add(CrouchedBodyUtils.EyeMode.STANDING);
            arrayList.add(CrouchedBodyUtils.EyeMode.CROUCHED);
         }

         return arrayList;
      }
   }

   public static double getSignedOffsetAlongStep(long crouchedBodyValue, long proofSupportValue, double crouchedBodyValue3, double crouchedBodyValue4) {
      int valueInt = ProofSupportUtils.getValueInt(crouchedBodyValue);
      int valueForLong = ProofSupportUtils.getValueForLong(crouchedBodyValue);
      int signum = Integer.signum(ProofSupportUtils.getValueInt(proofSupportValue) - valueInt);
      int signum2 = Integer.signum(ProofSupportUtils.getValueForLong(proofSupportValue) - valueForLong);
      return signum != 0 ? (crouchedBodyValue3 - (valueInt + 0.5)) * signum : (crouchedBodyValue4 - (valueForLong + 0.5)) * signum2;
   }

   private static CrouchedBodyUtils.Certificate certificateForCurrentPoint(
      CrouchedBodyUtils.Request request, CrouchedBodyUtils.Probe probe, long crouchedBodyValue, long target, double posAutoValue, double r, double crouchedValue
   ) {
      double crouchedBodyValue6 = request.here() ? Math.hypot(request.bodyX() - posAutoValue, request.bodyZ() - r) : 0.0;
      boolean crouchedBodyFlag = false;

      for (CrouchedBodyUtils.EyeMode eyeMode : getEyeModeOrder(request.sneakingNow(), false)) {
         CrouchedBodyUtils.Hit posAuto = probe.findPlacementHit(posAutoValue, getEyeHeight(crouchedValue, eyeMode), r, eyeMode);
         if (posAuto != null) {
            crouchedBodyFlag = true;
            if (!request.here()) {
               return new CrouchedBodyUtils.Certificate(
                  CrouchedBodyUtils.Status.REQUIRES_SURFACE_MOVE,
                  CrouchedBodyUtils.Reason.OK,
                  crouchedBodyValue,
                  target,
                  eyeMode,
                  eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED,
                  posAuto,
                  CrouchedBodyUtils.Interval.createCentered(posAutoValue, r, 0.12),
                  0.0,
                  0.29000000000000004
               );
            }

            if (!(crouchedBodyValue6 < 0.1) && probe.isBodySpaceClear(posAutoValue, crouchedValue, r, eyeMode)) {
               double largestClearMargin = findLargestClearMargin(probe, posAutoValue, r, getEyeHeight(crouchedValue, eyeMode), eyeMode, Math.min(0.12, crouchedBodyValue6 * 0.5));
               if (!(largestClearMargin <= 0.0)) {
                  return new CrouchedBodyUtils.Certificate(
                     CrouchedBodyUtils.Status.EXECUTABLE_WITH_PROVEN_MICRO_ADJUST,
                     CrouchedBodyUtils.Reason.OK,
                     crouchedBodyValue,
                     target,
                     eyeMode,
                     eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED,
                     posAuto,
                     CrouchedBodyUtils.Interval.createCentered(posAutoValue, r, largestClearMargin),
                     Math.max(0.0, crouchedBodyValue6 - largestClearMargin),
                     0.29000000000000004
                  );
               }
            }
         }
      }

      return crouchedBodyFlag ? CrouchedBodyUtils.Certificate.createInvalid(crouchedBodyValue, target, CrouchedBodyUtils.Reason.DYNAMIC) : null;
   }

   private static CrouchedBodyUtils.Certificate certificateForEdgeStep(
      CrouchedBodyUtils.Request request, CrouchedBodyUtils.Probe probe, long crouchedBodyValue, long target, double crouchedBodyValue3, double r, double crouchedValue, int crouchedBodyFactor, int crouchedBodyFactor2
   ) {
      double crouched = getEyeHeight(crouchedValue, CrouchedBodyUtils.EyeMode.CROUCHED);
      double crouchedBodyValue6 = -1.0;
      CrouchedBodyUtils.Hit hit = null;

      for (int i = FACE_REACH_OFFSETS.length - 1; i >= 0; i--) {
         double crouchedBodyFactor3 = FACE_REACH_OFFSETS[i];
         CrouchedBodyUtils.Hit posAuto = probe.findPlacementHit(crouchedBodyValue3 + crouchedBodyFactor * crouchedBodyFactor3, crouched, r + crouchedBodyFactor2 * crouchedBodyFactor3, CrouchedBodyUtils.EyeMode.CROUCHED);
         if (posAuto == null) {
            if (i == FACE_REACH_OFFSETS.length - 1) {
               return null;
            }
            break;
         }

         if (hit == null) {
            hit = posAuto;
         }

         crouchedBodyValue6 = crouchedBodyFactor3;
      }

      double crouchedBodyFactor4 = crouchedBodyValue6;
      double crouchedBodyValue7 = 0.8;
      double crouchedBodyFactor5 = 0.79;
      double crouchedBodyLimit = request.here() ? (crouchedBodyFactor != 0 ? (request.bodyX() - crouchedBodyValue3) * crouchedBodyFactor : (request.bodyZ() - r) * crouchedBodyFactor2) : 0.0;
      double spotValue = request.here() ? (crouchedBodyFactor != 0 ? request.bodyZ() - r : request.bodyX() - crouchedBodyValue3) : 0.0;
      double crouchedBodyValue9 = -1.0;

      for (double crouchedBodyValue10 : new double[]{0.35, 0.1}) {
         double crouchedBodyValue11 = crouchedBodyValue3 + crouchedBodyFactor * crouchedBodyFactor4;
         double crouchedBodyValue12 = r + crouchedBodyFactor2 * crouchedBodyFactor4;
         double crouchedBodyValue13 = crouchedBodyFactor != 0 ? 0.0 : crouchedBodyValue10;
         double crouchedBodyValue14 = crouchedBodyFactor != 0 ? crouchedBodyValue10 : 0.0;
         if (probe.findPlacementHit(crouchedBodyValue11 + crouchedBodyValue13, crouched, crouchedBodyValue12 + crouchedBodyValue14, CrouchedBodyUtils.EyeMode.CROUCHED) != null
            && probe.findPlacementHit(crouchedBodyValue11 - crouchedBodyValue13, crouched, crouchedBodyValue12 - crouchedBodyValue14, CrouchedBodyUtils.EyeMode.CROUCHED) != null) {
            crouchedBodyValue9 = crouchedBodyValue10;
            break;
         }
      }

      if (crouchedBodyValue9 < 0.0) {
         return null;
      } else {
         if (request.here() && crouchedBodyLimit >= crouchedBodyFactor4 - 1.0E-9 && Math.abs(spotValue) <= crouchedBodyValue9 + 1.0E-9) {
            crouchedBodyFactor4 = crouchedBodyLimit + 0.05;
            if (crouchedBodyFactor4 > crouchedBodyFactor5 + 1.0E-9) {
               return CrouchedBodyUtils.Certificate.createInvalid(crouchedBodyValue, target, CrouchedBodyUtils.Reason.DYNAMIC);
            }
         }

         if (!probe.isBodySpaceClear(crouchedBodyValue3 + crouchedBodyFactor * crouchedBodyFactor5, crouchedValue, r + crouchedBodyFactor2 * crouchedBodyFactor5, CrouchedBodyUtils.EyeMode.CROUCHED)) {
            return CrouchedBodyUtils.Certificate.createInvalid(crouchedBodyValue, target, CrouchedBodyUtils.Reason.COLLISION);
         } else {
            if (request.here()) {
               double bodyX = request.bodyX();
               double bodyZ = request.bodyZ();
               double crouchedBodyValue15 = crouchedBodyValue3 + crouchedBodyFactor * crouchedBodyFactor5;
               double crouchedBodyValue16 = r + crouchedBodyFactor2 * crouchedBodyFactor5;
               int ceil2 = (int)Math.ceil(Math.hypot(crouchedBodyValue15 - bodyX, crouchedBodyValue16 - bodyZ) / 0.1);

               for (int j = 1; j < ceil2; j++) {
                  double crouchedBodyFactor6 = (double)j / ceil2;
                  if (!probe.isBodySpaceClear(bodyX + (crouchedBodyValue15 - bodyX) * crouchedBodyFactor6, crouchedValue, bodyZ + (crouchedBodyValue16 - bodyZ) * crouchedBodyFactor6, CrouchedBodyUtils.EyeMode.CROUCHED)) {
                     return CrouchedBodyUtils.Certificate.createInvalid(crouchedBodyValue, target, CrouchedBodyUtils.Reason.COLLISION);
                  }
               }
            }

            CrouchedBodyUtils.Interval interval = buildEdgeInterval(crouchedBodyValue3, r, crouchedBodyFactor, crouchedBodyFactor2, crouchedBodyFactor4, crouchedBodyValue7, crouchedBodyFactor5);
            CrouchedBodyUtils.Interval interval2 = crouchedBodyFactor != 0
               ? new CrouchedBodyUtils.Interval(0, interval.lo(), interval.hi(), r - crouchedBodyValue9, r + crouchedBodyValue9, interval.settleX(), interval.settleZ())
               : new CrouchedBodyUtils.Interval(2, interval.lo(), interval.hi(), crouchedBodyValue3 - crouchedBodyValue9, crouchedBodyValue3 + crouchedBodyValue9, interval.settleX(), interval.settleZ());
            double max17 = Math.max(0.0, crouchedBodyFactor4 - crouchedBodyLimit);
            return new CrouchedBodyUtils.Certificate(
               CrouchedBodyUtils.Status.EXECUTABLE_WITH_PROVEN_MICRO_ADJUST,
               CrouchedBodyUtils.Reason.OK,
               crouchedBodyValue,
               target,
               CrouchedBodyUtils.EyeMode.CROUCHED,
               true,
               hit,
               interval2,
               max17,
               0.29000000000000004
            );
         }
      }
   }

   private CrouchedBodyUtils() {
   }

   static CrouchedBodyUtils.Interval buildEdgeInterval(double crouchedBodyValue, double r, int crouchedBodyLimit, int crouchedBodyLimit2, double crouchedBodyValue3, double crouchedBodyValue4, double crouchedBodyFactor) {
      if (crouchedBodyLimit != 0) {
         double crouchedBodyValue5 = crouchedBodyLimit > 0 ? crouchedBodyValue + crouchedBodyValue3 : crouchedBodyValue - crouchedBodyValue4;
         double crouchedBodyValue6 = crouchedBodyLimit > 0 ? crouchedBodyValue + crouchedBodyValue4 : crouchedBodyValue - crouchedBodyValue3;
         return new CrouchedBodyUtils.Interval(0, crouchedBodyValue5, crouchedBodyValue6, r - 0.35, r + 0.35, crouchedBodyValue + crouchedBodyLimit * crouchedBodyFactor, r);
      } else {
         double crouchedBodyValue7 = crouchedBodyLimit2 > 0 ? r + crouchedBodyValue3 : r - crouchedBodyValue4;
         double crouchedBodyValue8 = crouchedBodyLimit2 > 0 ? r + crouchedBodyValue4 : r - crouchedBodyValue3;
         return new CrouchedBodyUtils.Interval(2, crouchedBodyValue7, crouchedBodyValue8, crouchedBodyValue - 0.35, crouchedBodyValue + 0.35, crouchedBodyValue, r + crouchedBodyLimit2 * crouchedBodyFactor);
      }
   }

   public static CrouchedBodyUtils.Certificate computeCertificate(CrouchedBodyUtils.Request request, CrouchedBodyUtils.Probe probe) {
      long target = request.target();
      long stand = request.stand();
      int crouchedBodyValue = ProofSupportUtils.getValueInt(stand);
      int crouchedBodyValue2 = ProofSupportUtils.getValue(stand);
      int valueForLong = ProofSupportUtils.getValueForLong(stand);
      int valueInt = ProofSupportUtils.getValueInt(target);
      int crouchedBodyValue5 = ProofSupportUtils.getValue(target);
      int valueForLong6 = ProofSupportUtils.getValueForLong(target);
      double posAutoValue = crouchedBodyValue + 0.5;
      double r = valueForLong + 0.5;
      double crouchedValue = request.here() ? request.bodyY() : crouchedBodyValue2;
      int signum = Integer.signum(valueInt - crouchedBodyValue);
      int signum2 = Integer.signum(valueForLong6 - valueForLong);
      boolean crouchedBodyFlag = Math.abs(valueInt - crouchedBodyValue) + Math.abs(valueForLong6 - valueForLong) == 1 && crouchedBodyValue5 == crouchedBodyValue2 - 1;
      if (request.here()) {
         for (CrouchedBodyUtils.EyeMode eyeMode : getEyeModeOrder(request.sneakingNow(), request.sneakRequiredForSafety())) {
            CrouchedBodyUtils.Hit posAuto = probe.findPlacementHit(request.bodyX(), getEyeHeight(crouchedValue, eyeMode), request.bodyZ(), eyeMode);
            if (posAuto != null) {
               return new CrouchedBodyUtils.Certificate(
                  CrouchedBodyUtils.Status.EXECUTABLE_CURRENT_POSE,
                  CrouchedBodyUtils.Reason.OK,
                  target,
                  stand,
                  eyeMode,
                  eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED,
                  posAuto,
                  CrouchedBodyUtils.Interval.createCentered(request.bodyX(), request.bodyZ(), 0.12),
                  0.0,
                  0.29000000000000004
               );
            }
         }
      }

      CrouchedBodyUtils.Certificate crouchedBody = certificateForCurrentPoint(request, probe, target, stand, posAutoValue, r, crouchedValue);
      if (crouchedBody != null && crouchedBody.isStatusValid()) {
         return request.here() ? crouchedBody : crouchedBody.asRequiresSurfaceMove();
      } else {
         if (crouchedBodyFlag) {
            CrouchedBodyUtils.Certificate certificateForEdgeStep2 = certificateForEdgeStep(request, probe, target, stand, posAutoValue, r, crouchedValue, signum, signum2);
            if (certificateForEdgeStep2 != null && !certificateForEdgeStep2.isStatusValid()) {
               return certificateForEdgeStep2;
            }

            if (certificateForEdgeStep2 != null) {
               return request.here() ? certificateForEdgeStep2 : certificateForEdgeStep2.asRequiresSurfaceMove();
            }
         }

         if (crouchedBody != null) {
            return crouchedBody;
         } else {
            boolean crouchedBodyFlag2 = probe.isNearSolidNeighbour(posAutoValue, getEyeHeight(crouchedValue, CrouchedBodyUtils.EyeMode.CROUCHED), r)
               || crouchedBodyFlag && probe.isNearSolidNeighbour(posAutoValue + signum * 0.79, getEyeHeight(crouchedValue, CrouchedBodyUtils.EyeMode.CROUCHED), r + signum2 * 0.79);
            return CrouchedBodyUtils.Certificate.createInvalid(
               target, stand, crouchedBodyFlag2 ? CrouchedBodyUtils.Reason.NO_FACE : CrouchedBodyUtils.Reason.OUT_OF_REACH
            );
         }
      }
   }

   public static double getEyeHeight(double crouchedValue, CrouchedBodyUtils.EyeMode eyeMode) {
      return crouchedValue + (eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED ? 1.27 : 1.62);
   }

   public record Certificate(
      CrouchedBodyUtils.Status status,
      CrouchedBodyUtils.Reason reason,
      long target,
      long stand,
      CrouchedBodyUtils.EyeMode eye,
      boolean sneak,
      CrouchedBodyUtils.Hit hit,
      CrouchedBodyUtils.Interval interval,
      double requiredOffset,
      double safeLimit
   ) {
      public boolean isStatusValid() {
         return this.status != CrouchedBodyUtils.Status.INVALID_POSE;
      }

      public String formatStatusLabel() {
         return switch (this.status) {
            case EXECUTABLE_CURRENT_POSE -> "NOW";
            case EXECUTABLE_WITH_PROVEN_MICRO_ADJUST -> "ADJUST";
            case REQUIRES_SURFACE_MOVE -> "MOVE";
            case INVALID_POSE -> "INVALID:" + this.reason;
         };
      }

      public static CrouchedBodyUtils.Certificate createInvalid(long reason, long target, CrouchedBodyUtils.Reason stand) {
         return new CrouchedBodyUtils.Certificate(
            CrouchedBodyUtils.Status.INVALID_POSE, stand, reason, target, CrouchedBodyUtils.EyeMode.STANDING, false, null, null, 0.0, 0.29000000000000004
         );
      }

      public boolean isExecutableWithAdjust() {
         return this.status == CrouchedBodyUtils.Status.EXECUTABLE_WITH_PROVEN_MICRO_ADJUST;
      }

      public boolean isExecutable() {
         return this.status == CrouchedBodyUtils.Status.EXECUTABLE_CURRENT_POSE || this.status == CrouchedBodyUtils.Status.EXECUTABLE_WITH_PROVEN_MICRO_ADJUST;
      }

      public boolean isSafetyReason() {
         return this.status == CrouchedBodyUtils.Status.INVALID_POSE
            && (
               this.reason == CrouchedBodyUtils.Reason.NO_FACE
                  || this.reason == CrouchedBodyUtils.Reason.OUT_OF_REACH
                  || this.reason == CrouchedBodyUtils.Reason.SAFETY
            );
      }

      CrouchedBodyUtils.Certificate asRequiresSurfaceMove() {
         return new CrouchedBodyUtils.Certificate(
            CrouchedBodyUtils.Status.REQUIRES_SURFACE_MOVE,
            this.reason,
            this.target,
            this.stand,
            this.eye,
            this.sneak,
            this.hit,
            this.interval,
            this.requiredOffset,
            this.safeLimit
         );
      }
   }

   public static enum EyeMode {
      STANDING,
      CROUCHED;
   }

   public record Hit(long support, int face, double x, double y, double z) {
   }

   public record Interval(int axis, double lo, double hi, double lateralLo, double lateralHi, double settleX, double settleZ) {
      public double getLength() {
         return this.hi - this.lo;
      }

      public boolean containsPoint(double x, double z) {
         double lateralAxisLimit = this.axis == 2 ? z : x;
         double lateralAxisLimit2 = this.axis == 2 ? x : z;
         return lateralAxisLimit >= this.lo - 1.0E-9 && lateralAxisLimit <= this.hi + 1.0E-9 && lateralAxisLimit2 >= this.lateralLo - 1.0E-9 && lateralAxisLimit2 <= this.lateralHi + 1.0E-9;
      }

      public double distanceToPoint(double x, double z) {
         double lateralAxisLimit = this.axis == 2 ? z : x;
         double lateralAxisLimit2 = this.axis == 2 ? x : z;
         double lateralAxisValue = lateralAxisLimit < this.lo ? this.lo - lateralAxisLimit : (lateralAxisLimit > this.hi ? lateralAxisLimit - this.hi : 0.0);
         double deltaZ = lateralAxisLimit2 < this.lateralLo ? this.lateralLo - lateralAxisLimit2 : (lateralAxisLimit2 > this.lateralHi ? lateralAxisLimit2 - this.lateralHi : 0.0);
         return Math.hypot(lateralAxisValue, deltaZ);
      }

      public static CrouchedBodyUtils.Interval createCentered(double cz, double r, double cx) {
         return new CrouchedBodyUtils.Interval(-1, cz - cx, cz + cx, r - cx, r + cx, cz, r);
      }
   }

   public interface Probe {
      default boolean isBodySpaceClear(double spaceCrouchedValue, double crouchedValue, double r, CrouchedBodyUtils.EyeMode eyeMode) {
         return true;
      }

      CrouchedBodyUtils.Hit findPlacementHit(double posAutoValue, double crouched, double r, CrouchedBodyUtils.EyeMode eyeMode);

      default boolean isNearSolidNeighbour(double replaceableCollisionValue, double replaceableCollisionValue2, double r) {
         return true;
      }
   }

   public static enum Reason {
      OK,
      NO_FACE,
      OUT_OF_REACH,
      COLLISION,
      SAFETY,
      BODY_OVERLAPS_TARGET,
      NOT_STANDABLE,
      GATE,
      DYNAMIC;
   }

   public record Request(
      long target,
      long stand,
      FloorBuildPlanner.Pose pose,
      boolean here,
      double bodyX,
      double bodyY,
      double bodyZ,
      boolean sneakingNow,
      boolean sneakRequiredForSafety,
      double reach
   ) {
   }

   public static enum Status {
      EXECUTABLE_CURRENT_POSE,
      EXECUTABLE_WITH_PROVEN_MICRO_ADJUST,
      REQUIRES_SURFACE_MOVE,
      INVALID_POSE;
   }
}
