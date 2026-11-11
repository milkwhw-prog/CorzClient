package com.corz.client.utils;

import java.util.Locale;

public enum RoiPoseType {
   GEOMETRY_FAILURE(false, true),
   ROUTE_PHYSICAL_FAILURE(false, false),
   MOVEMENT_SERVER_CORRECTION(false, false),
   EXECUTOR_FAILURE(false, true),
   ROI_REFUSED(true, false),
   LOW_VALUE_NOW(true, false),
   BETTER_ALTERNATIVE_EXISTS(true, false),
   SELF_POSE_FAILURE(true, false);
   public final boolean memoryReplanningFlag;
   public final boolean memoryReplanningActive;

   private RoiPoseType(boolean flag2, boolean memoryReplanningFlag) {
      this.memoryReplanningActive = flag2;
      this.memoryReplanningFlag = memoryReplanningFlag;
   }

   public boolean isPhysicalCorrection() {
      return this == ROUTE_PHYSICAL_FAILURE || this == MOVEMENT_SERVER_CORRECTION;
   }

   public static RoiPoseType getRoiPose(String roiPoseText) {
      String roiPoseText2 = roiPoseText == null ? "" : roiPoseText.toLowerCase(Locale.ROOT);
      if (roiPoseText2.contains("server_correction") || roiPoseText2.contains("server correction")) {
         return MOVEMENT_SERVER_CORRECTION;
      } else if (roiPoseText2.contains("unstable_body")
         || roiPoseText2.contains("recenter")
         || roiPoseText2.contains("recentre")
         || roiPoseText2.contains("self-pose")
         || roiPoseText2.contains("self_pose")) {
         return SELF_POSE_FAILURE;
      } else if (roiPoseText2.contains("relocation roi")
         || roiPoseText2.contains("roi too low")
         || roiPoseText2.contains("roi refused")
         || roiPoseText2.contains("roi_redirect")
         || roiPoseText2.contains("roi redirect")) {
         return ROI_REFUSED;
      } else if (roiPoseText2.contains("better alternative") || roiPoseText2.contains("superseded") || roiPoseText2.contains("scheduler")) {
         return BETTER_ALTERNATIVE_EXISTS;
      } else if (roiPoseText2.contains("moratorium")
         || roiPoseText2.contains("low value")
         || roiPoseText2.contains("deferring")
         || roiPoseText2.contains("workspace_return")
         || roiPoseText2.contains("workspace return")
         || roiPoseText2.contains("cooldown")
         || roiPoseText2.contains("not worth")) {
         return LOW_VALUE_NOW;
      } else if (roiPoseText2.contains("source pose left") || roiPoseText2.contains("jump demand refused")) {
         return ROUTE_PHYSICAL_FAILURE;
      } else if (roiPoseText2.contains("no path")
         || roiPoseText2.contains("route")
         || roiPoseText2.contains("stalled")
         || roiPoseText2.contains("no progress")
         || roiPoseText2.contains("could not settle")
         || roiPoseText2.contains("could not mount")
         || roiPoseText2.contains("dig-down refused")
         || roiPoseText2.contains("unreachable")
         || roiPoseText2.contains("blocked")
         || roiPoseText2.contains("paths failed")
         || roiPoseText2.contains("repath")
         || roiPoseText2.contains("no access")
         || roiPoseText2.contains("access route")) {
         return ROUTE_PHYSICAL_FAILURE;
      } else {
         return !roiPoseText2.contains("support face")
               && !roiPoseText2.contains("no legal")
               && !roiPoseText2.contains("no stand")
               && !roiPoseText2.contains("stand exists")
               && !roiPoseText2.contains("not visible")
               && !roiPoseText2.contains("no visible")
               && !roiPoseText2.contains("invalid_pose")
               && !roiPoseText2.contains("no aim")
            ? EXECUTOR_FAILURE
            : GEOMETRY_FAILURE;
      }
   }
}
