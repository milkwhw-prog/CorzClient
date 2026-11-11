package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.DeltaHeadroomUtils;

public record GapSupportData(
   DeltaHeadroomUtils.Kind type,
   long fromFeet,
   long toFeet,
   double supportTop,
   double landingY,
   double landingMinX,
   double landingMaxX,
   double landingMinZ,
   double landingMaxZ,
   boolean headroomClear,
   int gapCells,
   int dropBlocks,
   boolean sprintRequired,
   boolean jumpRequired,
   boolean sneakForbidden,
   double takeoffNear,
   double takeoffFar,
   boolean narrowLanding,
   double brakeFrom,
   double maxTakeoffSpeed,
   int maxAirborneTicks,
   int completionWindowTicks,
   String reject
) {

   public int getToY() {
      return ProofSupportUtils.getValue(this.toFeet);
   }

   public boolean isAtLandingHeight(double y) {
      return Math.abs(y - this.landingY) <= 0.02;
   }

   public static GapSupportData evaluateGapSupport(
      DeltaHeadroomUtils.Kind supportTop, long headClear, long lowCoverTop, double to, double bodyClear, boolean type, boolean narrowLanding, boolean from
   ) {
      int gapSupportValue = ProofSupportUtils.getValueInt(headClear);
      int gapSupportValue2 = ProofSupportUtils.getValue(headClear);
      int valueForLong = ProofSupportUtils.getValueForLong(headClear);
      int valueInt = ProofSupportUtils.getValueInt(lowCoverTop);
      int gapSupportLimit = ProofSupportUtils.getValue(lowCoverTop);
      int valueForLong5 = ProofSupportUtils.getValueForLong(lowCoverTop);
      int abs6 = Math.abs(valueInt - gapSupportValue);
      int abs7 = Math.abs(valueForLong5 - valueForLong);
      boolean gapSupportFlag = supportTop == DeltaHeadroomUtils.Kind.GAP_JUMP;
      String gapSupportText = null;
      double gapSupportValue8;
      if (bodyClear > 0.0 && bodyClear <= 0.26) {
         gapSupportValue8 = gapSupportLimit + bodyClear;
      } else if (to >= 0.8) {
         gapSupportValue8 = gapSupportLimit - 1 + to;
      } else {
         gapSupportValue8 = gapSupportLimit;
         gapSupportText = to <= 0.0 ? "NO_SUPPORT" : "SUPPORT_TOO_LOW";
      }

      if (gapSupportText == null && !type) {
         gapSupportText = "BODY_BLOCKED";
      }

      if (gapSupportText == null && !narrowLanding) {
         gapSupportText = "NO_HEADROOM";
      }

      int max2 = Math.max(0, gapSupportValue2 - gapSupportLimit);
      if (gapSupportText == null && gapSupportFlag) {
         if (abs6 == 2 && abs7 == 0 || abs7 == 2 && abs6 == 0) {
            if (gapSupportLimit > gapSupportValue2) {
               gapSupportText = "GAP_RISES";
            } else if (max2 > 1) {
               gapSupportText = "GAP_DROPS_TOO_FAR";
            }
         } else {
            gapSupportText = "GAP_NOT_CARDINAL";
         }
      }

      int gapSupportValue9 = gapSupportFlag ? 12 + 2 * max2 + 4 : 0;
      return new GapSupportData(
         supportTop,
         headClear,
         lowCoverTop,
         to,
         gapSupportValue8,
         valueInt,
         valueInt + 1.0,
         valueForLong5,
         valueForLong5 + 1.0,
         narrowLanding,
         gapSupportFlag ? 1 : 0,
         max2,
         false,
         gapSupportFlag,
         gapSupportFlag,
         gapSupportFlag ? 1.5 : 0.0,
         gapSupportFlag ? 1.9 : 0.0,
         from,
         gapSupportFlag ? 2.5 : 0.0,
         gapSupportFlag ? (from ? 0.12 : 0.24) : Double.POSITIVE_INFINITY,
         gapSupportValue9,
         gapSupportFlag ? 40 : 0,
         gapSupportText
      );
   }

   public int getFromX() {
      return ProofSupportUtils.getValueInt(this.fromFeet);
   }

   public boolean isInsideLandingArea(double z, double x) {
      return z >= this.landingMinX && z < this.landingMaxX && x >= this.landingMinZ && x < this.landingMaxZ;
   }

   public boolean isLandedOnTarget(double y, double onGround, double z, boolean x) {
      return x && this.isInsideLandingArea(y, z) && this.isAtLandingHeight(onGround);
   }

   public int getToX() {
      return ProofSupportUtils.getValueInt(this.toFeet);
   }

   public String describe() {
      return String.format(
         Locale.ROOT,
         "type=%s from=%d,%d,%d to=%d,%d,%d supportTop=%.3f landingY=%.3f gap=%d drop=%d sprint=%s narrow=%s takeoff=%.2f..%.2f airborne<=%d window=%d certified=%s%s",
         this.type,
         this.getFromX(),
         this.getFromY(),
         this.getFromZ(),
         this.getToX(),
         this.getToY(),
         this.getToZ(),
         this.supportTop,
         this.landingY,
         this.gapCells,
         this.dropBlocks,
         this.sprintRequired,
         this.narrowLanding,
         this.takeoffNear,
         this.takeoffFar,
         this.maxAirborneTicks,
         this.completionWindowTicks,
         this.isCertified(),
         this.reject == null ? "" : " reject=" + this.reject
      );
   }

   public GapSupportData withReject(String why) {
      return new GapSupportData(
         this.type,
         this.fromFeet,
         this.toFeet,
         this.supportTop,
         this.landingY,
         this.landingMinX,
         this.landingMaxX,
         this.landingMinZ,
         this.landingMaxZ,
         this.headroomClear,
         this.gapCells,
         this.dropBlocks,
         this.sprintRequired,
         this.jumpRequired,
         this.sneakForbidden,
         this.takeoffNear,
         this.takeoffFar,
         this.narrowLanding,
         this.brakeFrom,
         this.maxTakeoffSpeed,
         this.maxAirborneTicks,
         this.completionWindowTicks,
         why
      );
   }

   public int getFromZ() {
      return ProofSupportUtils.getValueForLong(this.fromFeet);
   }

   public int getFromY() {
      return ProofSupportUtils.getValue(this.fromFeet);
   }

   public int getToZ() {
      return ProofSupportUtils.getValueForLong(this.toFeet);
   }

   public boolean isCertified() {
      return this.reject == null;
   }
}
