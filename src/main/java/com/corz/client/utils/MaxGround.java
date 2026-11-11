package com.corz.client.utils;
import com.corz.client.utils.DeltaHeadroomUtils;

public final class MaxGround {
   private boolean transitionStepActive;
   private int maxGroundStep;

   private static double getThinChest(MaxGround.Surface surface) {
      return surface != MaxGround.Surface.CHEST && surface != MaxGround.Surface.THIN ? 0.15 : 0.26;
   }

   public static boolean canTransitionStep(MaxGround.Node node, MaxGround.Pose pose) {
      if (pose.onGround() && canFeetSurface(node, pose)) {
         return false;
      } else if (pose.onGround() && pose.y() >= node.y() && pose.y() < node.y() + 1.3) {
         return false;
      } else {
         boolean transitionStepFlag = node.transition() == DeltaHeadroomUtils.Kind.STEP_UP
            || node.transition() == DeltaHeadroomUtils.Kind.JUMP_MOUNT
            || node.transition() == DeltaHeadroomUtils.Kind.OWNED_DIRT_STEP
            || node.transition() == DeltaHeadroomUtils.Kind.CONSTRUCTIVE_ACCESS_STEP;
         return transitionStepFlag || node.y() - pose.y() > 0.35;
      }
   }

   public void handleMaxFlag(int max) {
      if (max > this.maxGroundStep) {
         this.maxGroundStep = max;
      }

      this.transitionStepActive = false;
   }

   public boolean isFlag() {
      return this.transitionStepActive;
   }

   public int getMax() {
      return this.maxGroundStep;
   }

   public static boolean canFeetSurface(MaxGround.Node node, MaxGround.Pose pose) {
      boolean feetSurfaceFlag = pose.feetX() == node.x() && pose.feetZ() == node.z();
      boolean feetSurfaceFlag2 = node.surface() == MaxGround.Surface.CHEST || node.surface() == MaxGround.Surface.THIN;
      double thinChest = getThinChest(node.surface());
      boolean feetSurfaceFlag3 = pose.y() > node.y() - thinChest && pose.y() < node.y() + 1.3;
      boolean feetSurfaceFlag4 = pose.feetY() == node.y();
      boolean feetSurfaceFlag5 = feetSurfaceFlag2 && pose.feetY() == node.y() - 1 && pose.y() >= node.y() - thinChest && pose.y() < node.y();
      return feetSurfaceFlag && feetSurfaceFlag3 && (feetSurfaceFlag4 || feetSurfaceFlag5);
   }

   public MaxGround.Decision getTransitionStep(int transitionStepLimit, MaxGround.Node node, MaxGround.Pose pose) {
      if (transitionStepLimit > this.maxGroundStep) {
         this.handleMaxFlag(transitionStepLimit);
      }

      int transitionStepValue = this.maxGroundStep;
      double[] transitionStepValues = getAmounts(node);
      double transitionStepFactor = transitionStepValues[0] - pose.x();
      double transitionStepFactor2 = transitionStepValues[1] - pose.z();
      double distance = Math.sqrt(transitionStepFactor * transitionStepFactor + transitionStepFactor2 * transitionStepFactor2);
      double abs2 = Math.abs(node.y() - pose.y());
      if (this.transitionStepActive) {
         if (distance > 0.84) {
            this.transitionStepActive = false;
         }
      } else if (distance <= 0.62) {
         this.transitionStepActive = true;
      }

      boolean transitionStepFlag = pose.feetX() == node.x() && pose.feetZ() == node.z();
      double thinChest = getThinChest(node.surface());
      boolean transitionStepFlag2 = pose.y() > node.y() - thinChest && pose.y() < node.y() + 1.3;
      boolean feetSurface = canFeetSurface(node, pose);
      boolean transitionStepFlag3 = feetSurface && pose.onGround();
      boolean transitionStepFlag4 = node.transition() == DeltaHeadroomUtils.Kind.STEP_UP
         || node.transition() == DeltaHeadroomUtils.Kind.JUMP_MOUNT
         || node.transition() == DeltaHeadroomUtils.Kind.OWNED_DIRT_STEP
         || node.transition() == DeltaHeadroomUtils.Kind.CONSTRUCTIVE_ACCESS_STEP
         || node.transition() == DeltaHeadroomUtils.Kind.DROP
         || node.transition() == DeltaHeadroomUtils.Kind.GAP_JUMP;
      boolean transitionStepFlag5 = !transitionStepFlag4 || transitionStepFlag3 && pose.y() < node.y() + 0.6;
      return new MaxGround.Decision(transitionStepValue, this.transitionStepActive && transitionStepFlag && transitionStepFlag2 && transitionStepFlag5, transitionStepValues[0], transitionStepValues[1], distance, abs2, feetSurface, transitionStepFlag3);
   }

   public void handleMaxFlagForInt(int groundBound) {
      this.maxGroundStep = Math.max(0, groundBound);
      this.transitionStepActive = false;
   }

   public static double[] getAmounts(MaxGround.Node node) {
      return new double[]{node.x() + 0.5, node.z() + 0.5};
   }

   public record Decision(
      int routeIndex,
      boolean arrived,
      double targetX,
      double targetZ,
      double horizontalDistance,
      double verticalDistance,
      boolean occupiesSurface,
      boolean settled
   ) {
   }

   public record Node(int x, int y, int z, MaxGround.Surface surface, DeltaHeadroomUtils.Kind transition) {
   }

   public record Pose(double x, double y, double z, int feetX, int feetY, int feetZ, boolean onGround) {
   }

   public static enum Surface {
      FULL,
      CHEST,
      THIN,
      OWNED_DIRT,
      EDGE;
   }
}
