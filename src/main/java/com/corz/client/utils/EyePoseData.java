package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.CrouchedBodyUtils;

public record EyePoseData(
   long id,
   long target,
   long stand,
   CrouchedBodyUtils.EyeMode eyeMode,
   double eyeX,
   double eyeY,
   double eyeZ,
   CrouchedBodyUtils.Hit hit,
   double reach,
   double plannerDistance,
   long geometryFingerprint,
   EyePoseData.PoseContract pose
) {

   public static EyePoseData getModeEye(
      long id,
      long mode,
      long eyeY,
      CrouchedBodyUtils.EyeMode stand,
      double eyeX,
      double reach,
      double eyeZ,
      CrouchedBodyUtils.Hit hit,
      double target,
      long pose,
      EyePoseData.PoseContract geometryFingerprint
   ) {
      if (hit != null && stand != null && geometryFingerprint != null && target > 0.0) {
         if (geometryFingerprint.eyeMode() != stand || !geometryFingerprint.isEyeMode()) {
            return null;
         } else if (!canHeightEye(eyeY, reach, geometryFingerprint)) {
            return null;
         } else {
            double modeEyeLimit = getEyeHitDistance(eyeX, reach, eyeZ, hit);
            return modeEyeLimit > target + 1.0E-9 ? null : new EyePoseData(id, mode, eyeY, stand, eyeX, reach, eyeZ, hit, target, modeEyeLimit, pose, geometryFingerprint);
         }
      } else {
         return null;
      }
   }

   public static double getEyeHitDistance(double ex, double ey, double ez, CrouchedBodyUtils.Hit h) {
      double eyePoseFactor = h.x() - ex;
      double eyePoseFactor2 = h.y() - ey;
      double eyePoseFactor3 = h.z() - ez;
      return Math.sqrt(eyePoseFactor * eyePoseFactor + eyePoseFactor2 * eyePoseFactor2 + eyePoseFactor3 * eyePoseFactor3);
   }

   public boolean canEyePose(double x, double z) {
      return Math.hypot(x - this.eyeX, z - this.eyeZ) <= this.pose.centreRadius() + 1.0E-9;
   }

   public double getHit(double ez, double ey, double ex) {
      return getEyeHitDistance(ez, ey, ex, this.hit);
   }

   public boolean canReach(double ey, double ez, double ex) {
      return this.getHit(ey, ez, ex) <= this.reach + 1.0E-9;
   }

   public static EyePoseData getFace(
      long geometryFingerprint,
      long eyeX,
      long id,
      CrouchedBodyUtils.EyeMode hit,
      double stand,
      double eyeZ,
      double eyeY,
      CrouchedBodyUtils.Hit reach,
      double target,
      long mode
   ) {
      return getModeEye(
         geometryFingerprint,
         eyeX,
         id,
         hit,
         stand,
         eyeZ,
         eyeY,
         reach,
         target,
         mode,
         hit == null ? null : EyePoseData.PoseContract.getCrouched(hit, false, -1, reach == null ? -1 : reach.face())
      );
   }

   public boolean canPose(double eyeAboveFeet, boolean sneaking) {
      return this.pose.canHeightSneak(eyeAboveFeet, sneaking);
   }

   public boolean canTolerancePose(double x, double z) {
      return Math.abs(x - this.eyeX) <= this.pose.bodyTolerance() + 1.0E-9 && Math.abs(z - this.eyeZ) <= this.pose.bodyTolerance() + 1.0E-9;
   }

   public String formatEyeHitSummary(double ex, double ez, double result, String ey) {
      return String.format(
         Locale.ROOT,
         "plannerEye=%.2f,%.2f,%.2f executorEye=%.2f,%.2f,%.2f hit=%.2f,%.2f,%.2f plannerDistance=%.2f executorDistance=%.2f limit=%.2f eye=%s certificateId=%d geometryFingerprint=%s pose=%s result=%s",
         this.eyeX,
         this.eyeY,
         this.eyeZ,
         ex,
         ez,
         result,
         this.hit.x(),
         this.hit.y(),
         this.hit.z(),
         this.plannerDistance,
         this.getHit(ex, ez, result),
         this.reach,
         this.eyeMode,
         this.id,
         Long.toUnsignedString(this.geometryFingerprint),
         this.pose.formatPoseContract(),
         ey
      );
   }

   public static boolean isEyeWithinReach(double tz, double ez, double ey, int ex, int ty, int reach, double tx) {
      double eyePoseFactor = ex + 0.5 - tz;
      double eyePoseFactor2 = ty + 0.5 - ez;
      double eyePoseFactor3 = reach + 0.5 - ey;
      return eyePoseFactor * eyePoseFactor + eyePoseFactor2 * eyePoseFactor2 + eyePoseFactor3 * eyePoseFactor3 > tx * tx;
   }

   public static boolean canHeightEye(long pose, double stand, EyePoseData.PoseContract eyeY) {
      return Math.abs(stand - (ProofSupportUtils.getValue(pose) + eyeY.eyeHeight())) <= 1.0E-6;
   }

   public record PoseContract(
      CrouchedBodyUtils.EyeMode eyeMode,
      boolean sneak,
      boolean guiSupport,
      double eyeHeight,
      int lookDirection,
      int supportFace,
      double bodyTolerance,
      double centreRadius,
      int nudgeTicks,
      int settleTicks
   ) {

      public static EyePoseData.PoseContract getCrouched(CrouchedBodyUtils.EyeMode supportFace, boolean mode, int guiSupport, int lookDirection) {
         boolean crouchedFlag = supportFace == CrouchedBodyUtils.EyeMode.CROUCHED;
         return new EyePoseData.PoseContract(supportFace, crouchedFlag || mode, mode, getEyeHeightForMode(supportFace), guiSupport, lookDirection, 0.3, 0.07, 30, 6);
      }

      public String formatPoseContract() {
         return String.format(
            Locale.ROOT,
            "%s/sneak=%s/gui=%s/eyeHeight=%.2f/look=%d/face=%d/nudge=%d/settle=%d",
            this.eyeMode,
            this.sneak,
            this.guiSupport,
            this.eyeHeight,
            this.lookDirection,
            this.supportFace,
            this.nudgeTicks,
            this.settleTicks
         );
      }

      public boolean canHeightSneak(double sneaking, boolean eyeAboveFeet) {
         return eyeAboveFeet == this.sneak && Math.abs(sneaking - this.eyeHeight) <= 0.02;
      }

      public boolean isEyeMode() {
         return Math.abs(this.eyeHeight - getEyeHeightForMode(this.eyeMode)) <= 1.0E-9
            && this.sneak == (this.eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED || this.guiSupport)
            && (!this.guiSupport || this.eyeMode == CrouchedBodyUtils.EyeMode.CROUCHED);
      }

      public static double getEyeHeightForMode(CrouchedBodyUtils.EyeMode mode) {
         return mode == CrouchedBodyUtils.EyeMode.CROUCHED ? 1.27 : 1.62;
      }
   }
}
