package com.corz.client.utils;

import net.minecraft.util.math.MathHelper;
import com.corz.client.utils.GlidePhysics;

public final class FlightPlanner {
   private boolean directPlanUsed;
   public boolean planInadequate;
   public double lastPlanCost;
   private final FlightPlanner.Plan candidatePlan;
   private final GlidePhysics.Body simBody = new GlidePhysics.Body();
   private static final float[] COARSE_YAW_OFFSETS = new float[]{0.0F, -12.0F, 12.0F};
   private static final float[] WIDE_YAW_OFFSETS = new float[]{-30.0F, 30.0F, -55.0F, 55.0F, -90.0F, 90.0F, -140.0F, 140.0F};
   private static final float[] PITCH_CANDIDATES = new float[]{-62.0F, -46.0F, -33.0F, -23.0F, -15.0F, -9.0F, -4.0F, 1.0F, 6.0F, 12.0F, 19.0F, 27.0F, 36.0F};
   private int floorCheckCount;
   private boolean climbPhaseActive;
   private double safeFloorAltitude;
   private boolean floorFallbackUsed;

   private double getAltitudeUpperBound(FlightPlanner.Goal goal, double flightPlannerValue, double amountFlag) {
      double targetAltitude = goal.getTargetAltitude(flightPlannerValue);
      boolean flightPlannerFlag = !Double.isNaN(goal.goalY);
      return Math.max(targetAltitude + (flightPlannerFlag ? 6.0 : goal.cruiseBandAbove), amountFlag + 10.0);
   }

   private double capAltitudeToCeiling(FlightPlanner.Goal goal, double flightPlannerBound, double flightPlannerValue) {
      return Math.min(flightPlannerBound, this.getAltitudeUpperBound(goal, flightPlannerValue, this.getAltitudeLowerBound(goal, flightPlannerValue)));
   }

   public void simulateCandidate(
      FlightPlanner.Terrain terrain,
      GlidePhysics.Body body,
      int flightPlannerBound,
      boolean flag,
      FlightPlanner.Goal goal,
      float amount,
      float planLimit,
      FlightPlanner.Plan plan
   ) {
      GlidePhysics.Body pitchYaw = this.simBody.copyFrom(body);
      int flightPlannerCounter = flag ? GlidePhysics.getRocketBoostTicks(goal.rocketFlightDuration) : Math.max(0, flightPlannerBound);
      double flightPlannerValue = goal.getDistanceToGoal(pitchYaw.x, pitchYaw.positionZ);
      double flightPlannerLimit = Double.MAX_VALUE;
      double flightPlannerTotal = 0.0;
      double flightPlannerTotal2 = 0.0;
      double apiPutBound = 0.0;
      double flightPlannerTotal4 = 0.0;
      double flightPlannerTotal5 = 0.0;
      double flightPlannerTotal6 = 0.0;
      boolean flag2 = false;
      int flightPlannerValue2 = 60;

      for (int i = 0; i < 60; i++) {
         pitchYaw.yaw = GlidePhysics.rotateToward(pitchYaw.yaw, amount, 300.0F);
         pitchYaw.pitch = GlidePhysics.stepPitchToward(pitchYaw.pitch, planLimit);
         GlidePhysics.applyGlideTick(pitchYaw, goal.gravity);
         GlidePhysics.applyVelocity(pitchYaw);
         if (flightPlannerCounter > 0) {
            GlidePhysics.applyRocketBoost(pitchYaw);
            flightPlannerCounter--;
         }

         double distanceToGoal = goal.getDistanceToGoal(pitchYaw.x, pitchYaw.positionZ);
         int floorSurface = this.getFloorSurface(terrain, pitchYaw.x, pitchYaw.positionZ, goal);
         double flightPlannerValue4 = pitchYaw.y - floorSurface;
         if (!this.floorFallbackUsed && flightPlannerValue4 < 0.05) {
            flag2 = true;
            flightPlannerValue2 = i;
            break;
         }

         if (!this.floorFallbackUsed && flightPlannerValue4 < 24.0 && i > 0) {
            double floorSurfaceValue = pitchYaw.x - pitchYaw.velocityX * 0.5;
            double flightPlannerValue6 = pitchYaw.positionZ - pitchYaw.velocityZ * 0.5;
            int floorSurface2 = this.getFloorSurface(terrain, floorSurfaceValue, flightPlannerValue6, goal);
            if (!this.floorFallbackUsed && pitchYaw.y - pitchYaw.velocityY * 0.5 - floorSurface2 < 0.05) {
               flag2 = true;
               flightPlannerValue2 = i;
               break;
            }
         }

         if (flightPlannerValue4 < flightPlannerLimit) {
            flightPlannerLimit = flightPlannerValue4;
         }

         double max2 = Math.max(Math.max(goal.getRequiredClearance(distanceToGoal), pitchYaw.getHorizontalSpeed() * 5.0), -pitchYaw.velocityY * 25.0);
         if (goal.steerToLandingSpot && max2 < 10.0) {
            max2 = 10.0;
         }

         if (pitchYaw.velocityY < -0.5) {
            flightPlannerTotal6 += (-pitchYaw.velocityY - 0.5) * 25.0;
         }

         if (flightPlannerValue4 < max2) {
            double deltaVar = max2 - flightPlannerValue4;
            flightPlannerTotal += deltaVar * deltaVar * 0.5 + deltaVar * 2.0;
         }

         if (flightPlannerValue4 < 8.0 && pitchYaw.velocityY < -0.5) {
            flightPlannerTotal5 += (-pitchYaw.velocityY - 0.5) * 30.0;
         }

         if (pitchYaw.y < goal.goalMinY) {
            flightPlannerTotal4 += (goal.goalMinY - pitchYaw.y) * 4.0;
         }

         double flightPlannerFactor = i < 20 ? 0.4 : 1.0;
         flightPlannerTotal2 += Math.abs(this.getAltitudeBandError(goal, pitchYaw.y, distanceToGoal)) * flightPlannerFactor;
         apiPutBound += flightPlannerFactor;
      }

      double distanceToGoal3 = goal.getDistanceToGoal(pitchYaw.x, pitchYaw.positionZ);
      double horizontalSpeed = pitchYaw.getHorizontalSpeed();
      double amount4 = flightPlannerValue - distanceToGoal3;
      double amount5;
      if (flag2) {
         amount5 = 1000000.0 + (60 - flightPlannerValue2) * 1000.0;
      } else {
         double altitudeBandError = this.getAltitudeBandError(goal, pitchYaw.y, distanceToGoal3);
         boolean flightPlannerFlag = !Double.isNaN(goal.goalY);
         double flightPlannerFactor3 = goal.landingGoalActive ? 4.0 : 2.2;
         double flightPlannerFactor4 = flightPlannerFlag ? 1.6 : 0.6;
         double flightPlannerFactor5 = goal.steerToLandingSpot ? 0.0 : (goal.landingGoalActive ? 0.35 : 1.0);
         amount5 = (altitudeBandError < 0.0 ? -altitudeBandError * flightPlannerFactor3 : altitudeBandError * flightPlannerFactor4)
            + flightPlannerTotal2 / Math.max(1.0E-6, apiPutBound) * 0.4
            - amount4 * flightPlannerFactor5
            - (
               !flightPlannerFlag && !goal.landingGoalActive
                  ? (this.capAltitudeToCeiling(goal, pitchYaw.y, distanceToGoal3) - this.capAltitudeToCeiling(goal, body.y, flightPlannerValue) + getKineticEnergy(horizontalSpeed) - getKineticEnergy(body.getHorizontalSpeed())) * goal.altitudeWeight
                  : 0.0
            )
            + flightPlannerTotal / 60.0 * 10.0
            + flightPlannerTotal4 / 60.0
            + flightPlannerTotal5
            + flightPlannerTotal6 / 60.0 * 10.0
            + (flag ? goal.rocketBoostPenalty : 0.0);
         if (horizontalSpeed < 0.55) {
            amount5 += (0.55 - horizontalSpeed) * 60.0;
         }

         if (!Double.isNaN(goal.goalY) && distanceToGoal3 < 120.0 && horizontalSpeed > goal.landingSpeedLimit) {
            amount5 += (horizontalSpeed - goal.landingSpeedLimit) * 40.0;
         }

         if (goal.steerToLandingSpot) {
            double hypot = Math.hypot(pitchYaw.x - goal.landingGoalX, pitchYaw.positionZ - goal.landingGoalZ);
            if (hypot > goal.landingDistanceLimit * 1.5) {
               amount5 += (hypot - goal.landingDistanceLimit * 1.5) * 1.5;
            }
         } else {
            double angle = Math.atan2(-pitchYaw.velocityX, pitchYaw.velocityZ);
            double atan2 = Math.atan2(-(goal.goalX - pitchYaw.x), goal.goalZ - pitchYaw.positionZ);
            double absWrapDegrees = Math.abs(MathHelper.wrapDegrees(Math.toDegrees(angle - atan2)));
            if (distanceToGoal3 > 8.0) {
               amount5 += absWrapDegrees * 0.06;
            }
         }
      }

      plan.targetYaw = amount;
      plan.planPitch = planLimit;
      plan.usesRocketBoost = flag;
      plan.planCost = amount5;
      plan.minFloorClearance = flag2 ? -1.0 : flightPlannerLimit;
      plan.hitsGround = flag2;
      plan.endY = pitchYaw.y;
      plan.endSpeed = horizontalSpeed;
      plan.distanceClosed = amount4;
   }

   public int getFloorCheckCount() {
      return this.floorCheckCount;
   }

   public boolean isDirectPlanUsed() {
      return this.directPlanUsed;
   }

   public boolean isClimbPhaseActive() {
      return this.climbPhaseActive;
   }

   public double getAltitudeBandError(FlightPlanner.Goal goal, double flightPlannerLimit, double flightPlannerValue) {
      double amountFlag = this.getAltitudeLowerBound(goal, flightPlannerValue);
      double altitudeUpperBound = this.getAltitudeUpperBound(goal, flightPlannerValue, amountFlag);
      if (flightPlannerLimit < amountFlag) {
         return flightPlannerLimit - amountFlag;
      } else {
         return flightPlannerLimit > altitudeUpperBound ? flightPlannerLimit - altitudeUpperBound : 0.0;
      }
   }

   public FlightPlanner() {
      this.candidatePlan = new FlightPlanner.Plan();
      this.safeFloorAltitude = Double.NEGATIVE_INFINITY;
   }

   private boolean canUseDirectPlan(
      FlightPlanner.Terrain terrain, GlidePhysics.Body body, int flightPlannerLimit, boolean flightPlannerFlag, FlightPlanner.Goal goal, float flightPlannerValue, FlightPlanner.Plan plan
   ) {
      double distanceToGoal = goal.getDistanceToGoal(body.x, body.positionZ);
      if (distanceToGoal < 80.0) {
         return false;
      } else {
         double amountFlag = this.getAltitudeLowerBound(goal, distanceToGoal);
         double altitudeUpperBound = this.getAltitudeUpperBound(goal, distanceToGoal, amountFlag);
         if (goal.landingGoalActive) {
            amountFlag = Math.max(amountFlag, goal.altitudeCeiling - 6.0);
         }

         boolean flightPlannerFlag2 = flightPlannerFlag && goal.rocketsAllowed && flightPlannerLimit <= 0;
         if (!this.climbPhaseActive && body.y < amountFlag && (flightPlannerFlag2 || flightPlannerLimit > 0)) {
            this.climbPhaseActive = true;
         }

         if (this.climbPhaseActive && body.y >= altitudeUpperBound - 8.0) {
            this.climbPhaseActive = false;
         }

         boolean flightPlannerFlag3 = false;
         float planLimit;
         if (this.climbPhaseActive) {
            planLimit = -47.0F;
            flightPlannerFlag3 = flightPlannerFlag2 && body.velocityY < 0.45;
            if (!flightPlannerFlag2 && flightPlannerLimit <= 0 && body.velocityY < 0.05 && body.pitch < -37.0F) {
               this.climbPhaseActive = false;
               return false;
            }
         } else {
            double horizontalSpeed = body.getHorizontalSpeed();
            planLimit = horizontalSpeed < 1.0 ? 14.0F : (horizontalSpeed < 1.4 ? 7.0F : 2.0F);
         }

         this.simulateCandidate(terrain, body, flightPlannerLimit, flightPlannerFlag3, goal, flightPlannerValue, planLimit, plan);
         return !plan.hitsGround && plan.minFloorClearance >= goal.getRequiredClearance(distanceToGoal) && plan.endSpeed > 0.4;
      }
   }

   private double getAltitudeLowerBound(FlightPlanner.Goal goal, double flightPlannerValue) {
      double targetAltitude = goal.getTargetAltitude(flightPlannerValue);
      boolean flightPlannerFlag = !Double.isNaN(goal.goalY);
      double amountFlag = targetAltitude - (flightPlannerFlag ? 6.0 : (goal.landingGoalActive ? 8.0 : goal.cruiseBandBelow));
      if (!flightPlannerFlag || !(flightPlannerValue < goal.approachRadius * 1.5)) {
         amountFlag = Math.max(amountFlag, Math.min(this.safeFloorAltitude, targetAltitude + 400.0));
      }

      return amountFlag;
   }

   public double getMaxFloorAhead(FlightPlanner.Terrain terrain, double floorValue, double floorValue2, float cosSinValue, FlightPlanner.Goal goal) {
      double distanceToGoal = goal.getDistanceToGoal(floorValue, floorValue2);
      double floorLimit = Math.min(320.0, Double.isNaN(goal.goalY) ? distanceToGoal + 24.0 : Math.max(0.0, distanceToGoal - goal.approachRadius * 0.6));
      double cosSin = GlidePhysics.getLookX(cosSinValue, 0.0F);
      double cos = GlidePhysics.getLookZ(cosSinValue, 0.0F);
      double floorBound = Double.NEGATIVE_INFINITY;

      for (double floorTotal = 0.0; floorTotal <= floorLimit; floorTotal += 4.0) {
         for (int i = -8; i <= 8; i += 8) {
            int floor = MathHelper.floor(floorValue + cosSin * floorTotal - cos * i);
            int j = MathHelper.floor(floorValue2 + cos * floorTotal + cosSin * i);
            int surface = terrain.surface(floor, j);
            this.floorCheckCount++;
            if (surface == Integer.MIN_VALUE) {
               if (floorTotal <= 200.0) {
                  floorBound = Math.max(floorBound, goal.unknownTerrainHeight - goal.floorClearanceHeight);
               }
            } else if (surface > floorBound) {
               floorBound = surface;
            }
         }
      }

      return floorBound == Double.NEGATIVE_INFINITY ? -100000.0 : floorBound;
   }

   private boolean isPlanInadequate(FlightPlanner.Plan plan, FlightPlanner.Goal goal, GlidePhysics.Body body) {
      return plan.hitsGround || plan.minFloorClearance < goal.getRequiredClearance(goal.getDistanceToGoal(body.x, body.positionZ)) * 0.6 || plan.planCost > 400.0 || plan.endSpeed < 0.45 && plan.minFloorClearance < 40.0;
   }

   public double getSafeFloorAltitude() {
      return this.safeFloorAltitude;
   }

   private int getFloorSurface(FlightPlanner.Terrain terrain, double floorSurfaceValue, double floorSurfaceValue2, FlightPlanner.Goal goal) {
      this.floorFallbackUsed = false;
      int minValue = Integer.MIN_VALUE;
      boolean floorSurfaceFlag = false;
      int floor = MathHelper.floor(floorSurfaceValue - 0.3);
      int floorSurfaceLimit = MathHelper.floor(floorSurfaceValue + 0.3);
      int floor5 = MathHelper.floor(floorSurfaceValue2 - 0.3);
      int floor2 = MathHelper.floor(floorSurfaceValue2 + 0.3);

      for (int i = floor; i <= floorSurfaceLimit; i++) {
         for (int j = floor5; j <= floor2; j++) {
            int surface = terrain.surface(i, j);
            this.floorCheckCount++;
            if (surface == Integer.MIN_VALUE) {
               floorSurfaceFlag = true;
            } else if (surface > minValue) {
               minValue = surface;
            }
         }
      }

      if (floorSurfaceFlag) {
         int floorSurface = (int)Math.ceil(goal.unknownTerrainHeight - goal.floorClearanceHeight);
         if (minValue == Integer.MIN_VALUE || floorSurface > minValue) {
            this.floorFallbackUsed = true;
            return floorSurface;
         }
      }

      return minValue == Integer.MIN_VALUE ? -100000 : minValue;
   }

   public FlightPlanner.Plan computeBestPlan(FlightPlanner.Terrain terrain, GlidePhysics.Body body, int flightPlannerValue, boolean flightPlannerFlag, FlightPlanner.Goal goal) {
      FlightPlanner.Plan plan = new FlightPlanner.Plan();
      this.floorCheckCount = 0;
      this.directPlanUsed = false;
      float yawToward = GlidePhysics.getYawToward(body.x, body.positionZ, goal.goalX, goal.goalZ);
      float flightPlannerValue3 = body.yaw + MathHelper.wrapDegrees(yawToward - body.yaw);
      this.safeFloorAltitude = this.getMaxFloorAhead(terrain, body.x, body.positionZ, yawToward, goal) + goal.floorClearanceHeight + 4.0;
      if (Double.isNaN(goal.goalY) && this.canUseDirectPlan(terrain, body, flightPlannerValue, flightPlannerFlag, goal, flightPlannerValue3, plan)) {
         this.directPlanUsed = true;
         return plan;
      } else {
         this.searchBestPlan(terrain, body, flightPlannerValue, flightPlannerFlag, goal, flightPlannerValue3, COARSE_YAW_OFFSETS, plan, false, true);
         this.planInadequate = this.isPlanInadequate(plan, goal, body);
         this.lastPlanCost = plan.planCost;
         if (this.planInadequate) {
            this.searchBestPlan(terrain, body, flightPlannerValue, flightPlannerFlag, goal, flightPlannerValue3, COARSE_YAW_OFFSETS, plan, false, false);
            if (this.isPlanInadequate(plan, goal, body)) {
               this.searchBestPlan(terrain, body, flightPlannerValue, flightPlannerFlag, goal, flightPlannerValue3, WIDE_YAW_OFFSETS, plan, true, false);
            }
         }

         return plan;
      }
   }

   private void searchBestPlan(
      FlightPlanner.Terrain terrain,
      GlidePhysics.Body body,
      int planValue,
      boolean planFlag,
      FlightPlanner.Goal goal,
      float flightPlannerValue,
      float[] planValues,
      FlightPlanner.Plan plan,
      boolean flag,
      boolean planFlag2
   ) {
      boolean planFlag3 = planFlag && goal.rocketsAllowed && planValue <= 0;

      for (float planValuesItem : planValues) {
         for (float planLimit : PITCH_CANDIDATES) {
            for (int i = 0; i < (planFlag3 ? 2 : 1); i++) {
               if (i != 1 || !planFlag2 || !(planLimit > -30.0F) || goal.landingGoalActive) {
                  this.simulateCandidate(terrain, body, planValue, i == 1, goal, flightPlannerValue + planValuesItem, planLimit, this.candidatePlan);
                  this.candidatePlan.planCost = this.candidatePlan.planCost + Math.abs(planValuesItem) * 0.08;
                  this.candidatePlan.fromWideSearch = flag;
                  if (this.candidatePlan.planCost < plan.planCost) {
                     plan.copyFrom(this.candidatePlan);
                  }
               }
            }
         }
      }
   }

   public void resetClimbPhase() {
      this.climbPhaseActive = false;
   }

   private static double getKineticEnergy(double flightPlannerFactor) {
      return flightPlannerFactor * flightPlannerFactor / 0.16 * 0.8;
   }

   public static final class Goal {
      public int rocketFlightDuration;
      public double gravity;
      public final double descentSlope;
      public double rocketBoostPenalty;
      public final double approachAltitudeMargin;
      public double landingGoalX;
      public final double approachRadius;
      public final double cruiseBandAbove;
      public double goalZ;
      public double unknownTerrainHeight;
      public boolean landingGoalActive;
      public double landingSpeedLimit;
      public double landingDistanceLimit;
      public double goalX;
      public double goalMinY;
      public boolean rocketsAllowed;
      public boolean steerToLandingSpot;
      public double goalY;
      public double floorClearanceHeight;
      public double altitudeCeiling = 290.0;
      public final double altitudeWeight;
      public final double cruiseBandBelow;
      public double landingGoalZ;

      public double getRequiredClearance(double goalValue) {
         return Double.isNaN(this.goalY) ? this.floorClearanceHeight : Math.max(3.0, Math.min(this.floorClearanceHeight, (goalValue - this.approachRadius) * 0.25 + 4.0));
      }

      public double getTargetAltitude(double goalValue) {
         if (Double.isNaN(this.goalY)) {
            return this.altitudeCeiling;
         } else {
            double goalBound = this.goalY + this.approachAltitudeMargin + Math.max(0.0, goalValue - this.approachRadius) * this.descentSlope;
            return Math.min(this.altitudeCeiling, goalBound);
         }
      }

      public double getDistanceToGoal(double goalValue, double goalValue2) {
         return Math.hypot(this.goalX - goalValue, this.goalZ - goalValue2);
      }

      public Goal() {
         this.goalY = Double.NaN;
         this.approachRadius = 45.0;
         this.approachAltitudeMargin = 14.0;
         this.descentSlope = 0.095;
         this.floorClearanceHeight = 14.0;
         this.unknownTerrainHeight = 270.0;
         this.goalMinY = Double.NEGATIVE_INFINITY;
         this.rocketsAllowed = true;
         this.rocketBoostPenalty = 70.0;
         this.gravity = 0.08;
         this.rocketFlightDuration = 3;
         this.landingSpeedLimit = 1.45;
         this.cruiseBandBelow = 45.0;
         this.cruiseBandAbove = 15.0;
         this.altitudeWeight = 9.0;
         this.landingDistanceLimit = 42.0;
      }
   }

   public static final class Plan {
      public double distanceClosed;
      public double endSpeed;
      public float targetYaw;
      public boolean fromWideSearch;
      public boolean usesRocketBoost;
      public double endY;
      public double minFloorClearance;
      public double planCost = Double.MAX_VALUE;
      public float planPitch;
      public boolean hitsGround;

      void copyFrom(FlightPlanner.Plan plan) {
         this.targetYaw = plan.targetYaw;
         this.planPitch = plan.planPitch;
         this.usesRocketBoost = plan.usesRocketBoost;
         this.planCost = plan.planCost;
         this.minFloorClearance = plan.minFloorClearance;
         this.hitsGround = plan.hitsGround;
         this.endY = plan.endY;
         this.endSpeed = plan.endSpeed;
         this.distanceClosed = plan.distanceClosed;
         this.fromWideSearch = plan.fromWideSearch;
      }
   }

   public interface Terrain {

      int surface(int surfaceValue, int j);
   }
}
