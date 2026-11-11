package com.corz.client.utils;

import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.EmptyLvl;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsWorld;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.PathFollower;
import com.corz.client.utils.PathGoals;
import com.corz.client.utils.AStarPathfinder;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.CrystalDodgePlanner;
import com.corz.client.utils.StandGoalPlanner;
import com.corz.client.utils.FinishingPressUtils;

public final class CombatMovementController {
   private CombatMovementController.MoveStatus lastMoveStatus;
   private double stuckCheckZ;
   private boolean meleeCellActive;
   private boolean knockbackPending;
   private boolean fireEscapeActive;
   private boolean steeredLastTick;
   private long lastInterceptSearchTick;
   private double knockbackX;
   private int strafeDirectionSign;
   private double lastGoalDistance;
   private long nextStrafeRollTick;
   private AmountConstTime.Spot threatSpot;
   private double[] cachedMeleeCell;
   private final CombatMovementController.Env worldEnv;
   private boolean launchPhaseActive;
   private boolean steeringThisTick;
   private double fireEscapeDirZ;
   private double targetVelX;
   private Vec3d latchedGoal;
   private final Random random;
   private long lastSetbackAgeMs;
   private PathFollower.Cmd pathCommand;
   private boolean stuckDetected;
   private boolean lastIntentActive;
   public static final double[] ANCHOR_RING_RADII = new double[]{2.0, 2.4, 2.8, 3.2};
   private long pearlThrownTick;
   private long sidestepUntilTick;
   private long fireEscapeLastSampleTick;
   private double fireEscapeDirX;
   private int stuckCount;
   private int leadTargetCount;
   private long pearlLandingSeenTick;
   private Vec3d lastTargetPos;
   private long targetBaseTick;
   private int groundedTicks;
   private boolean walkerJumpAdvised;
   private boolean edgeGuardUsedThisTick;
   private long lastRepositionSearchTick;
   private int repositionCount;
   private CrystalDodgePlanner.Dodge activeDodge;
   private double lastMoveDirZ;
   private boolean repositionCellActive;
   private int stuckSampleCount;
   private long dodgeHoldUntilTick;
   private final AnchorRoutePlanner pathfinder;
   private double[] cachedInterceptCell;
   private double lastIntentDirZ;
   private double sidestepDirX;
   private boolean critJumpActive;
   private double sidestepDirZ;
   private long lastStuckRecoveryTick;
   private boolean interceptCellActive;
   private boolean arrivedLatched;
   private long lastMeleeCellSearchTick;
   private boolean jumpOnLandingPending;
   private final WalkerSurface walkerAdvisor = new WalkerSurface();
   private double goalDistance;
   private long critJumpStartTick;
   private double[] cachedRepositionCell;
   private int lastPearlCount;
   private float previousSwordCharge;
   private boolean leadingTargetActive;
   private CombatMovementController.MoveStatus pendingMoveStatus;
   private boolean strafePauseActive;
   private double lastMoveDirX;
   private long launchPhaseTick;
   public static final double[] TARGET_RING_RADII = new double[]{1.2, 1.6, 2.0, 2.4, 2.8, 3.2};
   private long fireEscapeBlockedUntilTick;
   private long lastFireCheckTick;
   private int interceptCellCount;
   private double targetVelZ;
   private int meleeCellCount;
   private boolean strafeHopRolled;
   private boolean closeRangeMode;
   private boolean waterApproachActive;
   private static final double[] ZERO_DIRECTION = new double[]{0.0, 0.0};
   private double knockbackZ;
   private long lastTargetUpdateTick;
   private double lastIntentDirX;
   private double stuckCheckX;
   private boolean pearlSetbackExpected;

   public boolean isWaterApproachActive() {
      return this.waterApproachActive;
   }

   private void applyWalkerAdvice(long vecHashValue, Vec3d vec, WalkerSurface.Advice advice) {
      if (advice != null) {
         switch (advice) {
            case JUMP:
               this.walkerJumpAdvised = true;
               break;
            case REPATH:
               this.markBlockedCellAhead(vecHashValue, vec);
               break;
            case STRAFE:
               this.startSidestep(vecHashValue, vec);
               break;
            case UNREACHABLE:
               this.pathfinder.markUnreachable(vecHashValue);
         }
      }
   }

   static double getVisibleCellHeightLevel(CombatMovementController.Env env, int posBlockValue, int z2, Vec3d vec, AmountConstTime.Spot spot) {
      return getVisibleCellHeight(env, posBlockValue, z2, vec, spot, 0.5, -0.5);
   }

   static boolean isRepositionCellStillValid(CombatMovementController.Env env, double[] posBlockValues, Vec3d vec, Vec3d vec2, AmountConstTime.Spot spot) {
      return canFlag(env, posBlockValues, vec, vec2, spot, false);
   }

   static boolean hasNoSpecialComboAction(CenterVecData centerVecData, SurroundKitType surroundKitType) {
      return centerVecData != null && centerVecData.crystal() == null && centerVecData.anchor() == null && centerVecData.pearl() == null
         ? surroundKitType != SurroundKitType.PEARL && surroundKitType != SurroundKitType.BREACH && centerVecData.lane() != SurroundKitType.PEARL && centerVecData.lane() != SurroundKitType.BREACH
         : false;
   }

   double[] getTargetVelocity() {
      return new double[]{this.targetVelX, this.targetVelZ};
   }

   int getMeleeCellCount() {
      return this.meleeCellCount;
   }

   private boolean isPearlTeleportImminent(long time, ComboTradeData comboTradeData, int value) {
      if (this.lastPearlCount >= 0 && value >= 0 && value < this.lastPearlCount) {
         this.pearlThrownTick = time;
      }

      this.lastPearlCount = value;
      if (this.worldEnv.isTouchingWater()) {
         return false;
      } else {
         int expandEntities = this.worldEnv.getPearlLandingTicks();
         boolean apiFlag = this.pearlThrownTick != Long.MIN_VALUE && time >= this.pearlThrownTick && time - this.pearlThrownTick < 5L;
         boolean apiFlag2 = expandEntities >= 0 && expandEntities <= 7;
         boolean apiFlag3 = apiFlag2 || expandEntities < 0 && apiFlag;
         boolean apiFlag4 = comboTradeData != null && comboTradeData.lane() == SurroundKitType.PEARL && comboTradeData.stance() == DanceChaseType.HOLD;
         if (apiFlag2) {
            this.pearlLandingSeenTick = time;
         }

         return apiFlag3 || apiFlag4;
      }
   }

   static double traceStraightWalk(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, boolean flooredFlag, double flooredBound, int flooredValue) {
      if (env != null && vec != null && vec2 != null && isFiniteVec(vec) && isFiniteVec(vec2)) {
         double deltaX = vec2.x - vec.x;
         double deltaZ = vec2.z - vec.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         if (hypot < 1.0E-6) {
            double groundY = getAmount(env, vec.x, vec.z, vec.y, flooredValue);
            return Double.isFinite(groundY) ? groundY : vec.y;
         } else {
            double minAllowJump = Math.min(hypot, flooredBound);
            int flooredLimit = Math.max(1, (int)Math.ceil(minAllowJump / 0.4));
            double floored = vec.y;
            double x = vec.x;
            double z = vec.z;
            long long2 = BlockPos.ofFloored(vec.x, vec.y + 0.001, vec.z).asLong();

            for (int i = 1; i <= flooredLimit; i++) {
               double flooredFactor = minAllowJump * i / flooredLimit / hypot;
               double posBlockValue = vec.x + deltaX * flooredFactor;
               double flooredValue4 = vec.z + deltaZ * flooredFactor;
               double flooredValue5 = getAmount(env, posBlockValue, flooredValue4, floored, flooredValue);
               if (!Double.isFinite(flooredValue5)) {
                  return Double.NaN;
               }

               double deltaVar = flooredValue5 - floored;
               if (deltaVar > 1.001) {
                  return Double.NaN;
               }

               if (deltaVar > 0.601) {
                  if (!flooredFlag) {
                     return Double.NaN;
                  }

                  if (env.isSolidAt((int)Math.floor(x), (int)Math.floor(floored + 0.001) + 2, (int)Math.floor(z))) {
                     return Double.NaN;
                  }
               }

               BlockPos ofFloored = BlockPos.ofFloored(posBlockValue, flooredValue5 + 0.001, flooredValue4);
               long long3 = ofFloored.asLong();
               if (long3 != long2) {
                  long2 = long3;
                  if ((env.getHazardFlags(ofFloored) & 1373) != 0) {
                     return Double.NaN;
                  }
               }

               floored = flooredValue5;
               x = posBlockValue;
               z = flooredValue4;
            }

            return floored;
         }
      } else {
         return Double.NaN;
      }
   }

   private void storeMoveDirection(double[] posBlockValues) {
      if (posBlockValues != null) {
         this.lastMoveDirX = posBlockValues[0];
         this.lastMoveDirZ = posBlockValues[1];
      }
   }

   private boolean isCellUnsafe(Vec3d vec, double apiVecValue, double apiVecValue2) {
      BlockPos floored = BlockPos.ofFloored(apiVecValue, vec.y + 0.001, apiVecValue2);
      if (this.pathfinder.isGapOrDropCell(floored.getX(), floored.getZ())) {
         return false;
      } else {
         int maxDrop = this.pathfinder.getMaxDrop();
         return this.worldEnv.getDropDepth(floored, maxDrop + 1) > maxDrop ? true : (this.worldEnv.getHazardFlags(floored) & 337) != 0;
      }
   }

   CombatMovementController(Random random, CombatMovementController.Env env) {
      this(
         random,
         env,
         env instanceof AStarPathfinder.World world ? new AnchorRoutePlanner(world, PathGoals.Los.INSTANCE, AStarPathfinder.Danger.INSTANCE) : AnchorRoutePlanner.createDirectOnly()
      );
   }

   private boolean hasCeilingAbove(Vec3d vec) {
      return this.worldEnv.isSolidAt((int)Math.floor(vec.x), (int)Math.floor(vec.y + 0.001) + 2, (int)Math.floor(vec.z));
   }

   private boolean isNextStepGapJump() { com.corz.client.utils.AStarPathfinder.Move var50_u = null;  boolean var40_u = false; 
      PathFollower.Cmd pathCommand37 = this.pathCommand;
      if (pathCommand37 instanceof PathFollower.Cmd) {
         PathFollower.Cmd cmd = pathCommand37;

         try {
            var40_u = cmd.move();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         boolean var40_u2 = var40_u;
         cmd = pathCommand37;

         try {
            cmd.dx();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         cmd = pathCommand37;

         try {
            cmd.dz();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         cmd = pathCommand37;

         try {
            cmd.jump();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         cmd = pathCommand37;

         try {
            cmd.sprint();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         cmd = pathCommand37;

         try {
            cmd.done();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         cmd = pathCommand37;

         try {
            cmd.offPath();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         cmd = pathCommand37;

         try {
            cmd.index();
         } catch (Throwable e8) {
            throw new MatchException(e8.toString(), e8);
         }

         cmd = pathCommand37;

         try {
            cmd.remaining();
         } catch (Throwable e9) {
            throw new MatchException(e9.toString(), e9);
         }

         cmd = pathCommand37;

         try {
            var50_u = cmd.next();
         } catch (Throwable e10) {
            throw new MatchException(e10.toString(), e10);
         }

         AStarPathfinder.Move var50_u2 = var50_u;
         if (var40_u2 && (var50_u2 == AStarPathfinder.Move.GAP1 || var50_u2 == AStarPathfinder.Move.GAP2)) {
            return true;
         }
      }

      return false;
   }

   private PlayerInputOverride.Intent getDirectApproachIntent(AbsorptionPosData absorptionPosData, Vec3d vec, double maskedDodgeValue) {
      Vec3d pos = absorptionPosData.pos();
      double deltaX = vec.x - pos.x;
      double deltaZ = vec.z - pos.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      this.goalDistance = hypot;
      if (!(hypot > maskedDodgeValue)) {
         this.pendingMoveStatus = CombatMovementController.MoveStatus.ARRIVED;
         return this.applyLedgeSafety(absorptionPosData, buildIntent(false, 0.0, 0.0, true, false, false));
      } else {
         this.steeringThisTick = true;
         double[] maskedDodgeValues = normalizeXZ(deltaX, deltaZ);
         this.storeMoveDirection(maskedDodgeValues);
         return maskedDodgeValues == null
            ? this.applyLedgeSafety(absorptionPosData, buildIntent(false, 0.0, 0.0, true, false, false))
            : this.applyLedgeSafety(absorptionPosData, buildIntent(true, maskedDodgeValues[0], maskedDodgeValues[1], true, false, false));
      }
   }

   double getTargetSpeed() {
      return Math.hypot(this.targetVelX, this.targetVelZ);
   }

   private void clearRepositionCache() {
      this.cachedRepositionCell = null;
      this.lastRepositionSearchTick = Long.MIN_VALUE;
   }

   private boolean shouldJumpForStep(AbsorptionPosData absorptionPosData, double apiPosValue, double apiPosValue2, boolean apiPosFlag) {
      Vec3d pos = absorptionPosData.pos();
      if (this.hasCeilingAbove(pos)) {
         return false;
      } else if (this.worldEnv.hasPendingBlockAboveBody()) {
         return false;
      } else {
         return apiPosFlag ? !this.worldEnv.hasPendingBlockBesideBody() : this.isStepUpAhead(pos, apiPosValue, apiPosValue2, 0.35) || this.isStepUpAhead(pos, apiPosValue, apiPosValue2, 0.6);
      }
   }

   public boolean isRepositionCellActive() {
      return this.repositionCellActive;
   }

   private boolean canPressWtap(long posBlockValue) {
      return !this.launchPhaseActive && this.launchPhaseTick != Long.MIN_VALUE && posBlockValue - this.launchPhaseTick <= 6L;
   }

   int getStuckCount() {
      return this.stuckCount;
   }

   public void setThreatSpot(AmountConstTime.Spot data) {
      this.threatSpot = data;
   }

   public PlayerInputOverride.Intent computeMovementIntent(
      long maskedValue,
      AbsorptionPosData absorptionPosData,
      StandGoalPlanner.Goal goal,
      Vec3d vec,
      CrystalDodgePlanner.Dodge dodge,
      ComboTradeData comboTradeData,
      boolean maskedFlag
   ) {
      this.pathCommand = null;
      this.pendingMoveStatus = null;
      this.stuckDetected = false;
      this.leadingTargetActive = false;
      this.repositionCellActive = false;
      this.meleeCellActive = false;
      this.interceptCellActive = false;
      this.waterApproachActive = false;
      boolean knockbackPending2 = this.knockbackPending;
      this.knockbackPending = false;
      this.updateTargetVelocity(maskedValue, vec);
      if (absorptionPosData != null && absorptionPosData.pos() != null && isFiniteVec(absorptionPosData.pos())) {
         if (isTerminatorStatus(comboTradeData)) {
            dodge = null;
            this.activeDodge = null;
            this.dodgeHoldUntilTick = Long.MIN_VALUE;
         }

         Vec3d pos = absorptionPosData.pos();
         boolean horizontalCollision = this.worldEnv.isHorizontalCollision();
         this.applyWalkerAdvice(maskedValue, pos, this.walkerAdvisor.getNoneFlag(this.steeredLastTick, pos.x, pos.z, this.lastGoalDistance, horizontalCollision, absorptionPosData.onGround()));
         boolean maskedFlag2 = this.walkerJumpAdvised && (!horizontalCollision || !this.worldEnv.hasPendingBlockBesideBody());
         this.walkerJumpAdvised = false;
         float amount = absorptionPosData.swordCharge();
         boolean juggleLaunchFlag = !Float.isNaN(this.previousSwordCharge) && !Float.isNaN(amount) && amount + 0.3F < this.previousSwordCharge;
         this.previousSwordCharge = amount;
         Vec3d velocity = absorptionPosData.velocity();
         boolean maskedFlag4 = absorptionPosData.onGround() && (velocity == null || Math.abs(velocity.y) < 0.08);
         this.groundedTicks = maskedFlag4 ? Math.min(this.groundedTicks + 1, 10) : 0;
         boolean stanceFeetFlag = this.jumpOnLandingPending && this.groundedTicks >= 2;
         if (stanceFeetFlag) {
            this.jumpOnLandingPending = false;
         }

         boolean maskedFlag6 = this.worldEnv.isActionRunning() || absorptionPosData.inputMasked();
         boolean timeApi = this.isPearlTeleportImminent(maskedValue, comboTradeData, absorptionPosData.pearls());
         long msSinceSetback = this.worldEnv.getMsSinceSetback();
         boolean maskedFlag7 = msSinceSetback < this.getSetbackWindowMs(maskedValue, msSinceSetback);
         if (maskedFlag6 || maskedFlag7) {
            this.critJumpActive = false;
            this.resetStuckTracking();
            this.pendingMoveStatus = maskedFlag6 ? CombatMovementController.MoveStatus.MASKED : CombatMovementController.MoveStatus.SETBACK;
            return this.finalizeIntent(PlayerInputOverride.Intent.idle());
         } else if (timeApi && !this.isDodgeActive(maskedValue, dodge)) {
            this.critJumpActive = false;
            this.resetStuckTracking();
            this.pendingMoveStatus = CombatMovementController.MoveStatus.SETTLE;
            return this.finalizeIntent(this.applyLedgeSafety(absorptionPosData, PlayerInputOverride.Intent.idle().asUrgent()));
         } else {
            CenterVecData.Stand comboYield = getActiveComboStand(comboTradeData);
            if (comboYield == null) {
               this.resetStuckTracking();
            }

            PlayerInputOverride.Intent intent = comboYield != null
               ? this.getComboStandIntent(maskedValue, absorptionPosData, comboYield, vec, dodge, comboTradeData, maskedFlag, stanceFeetFlag, juggleLaunchFlag, horizontalCollision, maskedFlag2)
               : this.getGoalMovementIntent(maskedValue, absorptionPosData, goal, vec, dodge, comboTradeData, stanceFeetFlag, horizontalCollision, maskedFlag2);
            if (knockbackPending2 && this.pendingMoveStatus != CombatMovementController.MoveStatus.FIRE && isMovingAlongDirection(intent, this.knockbackX, this.knockbackZ)) {
               this.pendingMoveStatus = CombatMovementController.MoveStatus.HOLD;
               this.steeringThisTick = false;
               return this.finalizeIntent(intent.withoutMovement());
            } else {
               return this.finalizeIntent(intent);
            }
         }
      } else {
         this.critJumpActive = false;
         this.walkerJumpAdvised = false;
         this.resetStuckTracking();
         return this.finalizeIntent(PlayerInputOverride.Intent.idle());
      }
   }

   private CrystalDodgePlanner.Dodge resolveActiveDodge(long posBlockValue, CrystalDodgePlanner.Dodge maxData) {
      if (maxData != null) {
         this.activeDodge = maxData;
         this.dodgeHoldUntilTick = posBlockValue + Math.max(1, maxData.holdTicks());
         return maxData;
      } else if (this.activeDodge != null && posBlockValue < this.dodgeHoldUntilTick) {
         return this.activeDodge;
      } else {
         this.activeDodge = null;
         return null;
      }
   }

   static double findGroundHeight(CombatMovementController.Env env, double posBlockValue, double z, double floored) {
      return getAmount(env, posBlockValue, z, floored, 3);
   }

   private void clearMeleeCellCache() {
      this.cachedMeleeCell = null;
      this.lastMeleeCellSearchTick = Long.MIN_VALUE;
   }

   static double getStoppingDistance(Vec3d vec) {
      if (vec == null) {
         return 0.0;
      } else {
         double hypot = Math.hypot(vec.x, vec.z);
         return Double.isFinite(hypot) ? hypot / 0.45399999999999996 : 0.0;
      }
   }

   private double[] findFireEscapeDirection(Vec3d vec, Vec3d vec2) {
      int apiFlooredValue = (int)Math.floor(vec.x);
      int floor2 = (int)Math.floor(vec.z);
      double apiFlooredLimit = Double.POSITIVE_INFINITY;
      double[] apiFloored = null;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
               double x = apiFlooredValue + i + 0.5;
               double z = floor2 + j + 0.5;
               double groundY = getAmount(this.worldEnv, x, z, vec.y, 1);
               if (Double.isFinite(groundY)
                  && !(Math.abs(groundY - vec.y) > 0.601)
                  && (this.worldEnv.getHazardFlags(BlockPos.ofFloored(x, groundY + 0.001, z)) & 351) == 0
                  && canWalkStraight(this.worldEnv, vec, new Vec3d(x, vec.y, z), false, 1.5, 1)) {
                  double apiFlooredValue4 = (i != 0 && j != 0 ? 0.3 : 0.0) + (vec2 != null && isFiniteVec(vec2) ? Math.hypot(x - vec2.x, z - vec2.z) : 0.0);
                  if (apiFlooredValue4 < apiFlooredLimit) {
                     double[] apiFlooredValues = normalizeXZ(x - vec.x, z - vec.z);
                     if (apiFlooredValues != null) {
                        apiFlooredLimit = apiFlooredValue4;
                        apiFloored = apiFlooredValues;
                     }
                  }
               }
            }
         }
      }

      return apiFloored;
   }

   static double getAmount(CombatMovementController.Env env, double posBlockValue, double z, double floored, int posBlockBound) {
      int floorFeetY = (int)Math.floor(posBlockValue);
      int floor4 = (int)Math.floor(z);
      int maxMaxDrop = Math.max(0, posBlockBound);
      int floor5 = (int)Math.floor(floored + 1.0);
      int posBlockValue6 = (int)Math.floor(floored + 0.001) - maxMaxDrop - 1;

      for (int i = floor5; i >= posBlockValue6; i--) {
         if (env.isSolidAt(floorFeetY, i, floor4)) {
            double collisionShape = env.getSurfaceHeight(floorFeetY, i, floor4);
            if (!Double.isNaN(collisionShape)) {
               if (collisionShape > floored + 1.0 + 0.001) {
                  return Double.POSITIVE_INFINITY;
               }

               if (floored - collisionShape > maxMaxDrop + 0.001) {
                  return Double.NaN;
               }

               int max7 = Math.max(i + 1, (int)Math.floor(collisionShape + 0.001));
               int posBlockLimit = (int)Math.floor(collisionShape + 1.8 - 0.001);

               for (int j = max7; j <= posBlockLimit; j++) {
                  if (env.isSolidAt(floorFeetY, j, floor4)) {
                     return Double.POSITIVE_INFINITY;
                  }
               }

               return collisionShape;
            }
         }
      }

      return Double.NaN;
   }

   private Vec3d findMeleeCell(long time, Vec3d vec, Vec3d vec2, Vec3d vec3, boolean posBlockFlag) {
      double[] posBlockValues = this.cachedMeleeCell;
      if (posBlockValues != null && isMeleeCellStillValid(this.worldEnv, posBlockValues, vec, vec2, this.threatSpot, posBlockFlag)) {
         return new Vec3d(posBlockValues[0], posBlockValues[1], posBlockValues[2]);
      } else {
         this.cachedMeleeCell = null;
         if (this.lastMeleeCellSearchTick != Long.MIN_VALUE && time >= this.lastMeleeCellSearchTick && time - this.lastMeleeCellSearchTick < 4L) {
            return null;
         } else {
            this.lastMeleeCellSearchTick = time;
            double[] amounts = searchMeleeCell(this.worldEnv, vec, vec2, vec3, this.threatSpot, this.pathfinder.getMaxDrop(), posBlockFlag);
            this.cachedMeleeCell = amounts;
            return amounts == null ? null : new Vec3d(amounts[0], amounts[1], amounts[2]);
         }
      }
   }

   static boolean isMovingAlongDirection(PlayerInputOverride.Intent intent, double dirUrgentFactor, double dirUrgentFactor2) {
      return intent != null && intent.move() && !intent.urgent() && intent.dirX() * dirUrgentFactor + intent.dirZ() * dirUrgentFactor2 > 0.001;
   }

   public boolean isMeleeCellActive() {
      return this.meleeCellActive;
   }

   static boolean isInterceptCellStillValid(CombatMovementController.Env env, double[] posBlockValues, Vec3d vec, boolean posBlockFlag) {
      if (env != null && posBlockValues != null && posBlockValues.length >= 3 && vec != null && isFiniteVec(vec)) {
         if (posBlockFlag && posBlockValues[1] > getWaterSurfaceHeight(env, vec) + 1.2 + 0.001) {
            return false;
         } else if (Math.hypot(posBlockValues[0] - vec.x, posBlockValues[2] - vec.z) > 25.0) {
            return false;
         } else {
            double posBlockValue = getSafeCellHeight(env, (int)Math.floor(posBlockValues[0]), (int)Math.floor(posBlockValues[2]), posBlockValues[1]);
            return Double.isFinite(posBlockValue) && Math.abs(posBlockValue - posBlockValues[1]) < 1.0E-6;
         }
      } else {
         return false;
      }
   }

   int getRepositionCount() {
      return this.repositionCount;
   }

   private void trackGoalPosition(Vec3d vec3d) {
      if (this.latchedGoal == null || Math.hypot(this.latchedGoal.x - vec3d.x, this.latchedGoal.z - vec3d.z) > 0.75 || Math.abs(this.latchedGoal.y - vec3d.y) > 1.0) {
         this.latchedGoal = vec3d;
         this.arrivedLatched = false;
      }
   }

   private void updateTargetVelocity(long time, Vec3d vec3d) {
      if (vec3d == null || !isFiniteVec(vec3d)) {
         this.lastTargetPos = null;
         this.targetBaseTick = Long.MIN_VALUE;
         this.lastTargetUpdateTick = Long.MIN_VALUE;
         this.targetVelX = 0.0;
         this.targetVelZ = 0.0;
      } else if (time != this.lastTargetUpdateTick || this.lastTargetPos == null) {
         boolean vecFlag = this.lastTargetPos != null && this.targetBaseTick != Long.MIN_VALUE && this.lastTargetUpdateTick != Long.MIN_VALUE && time == this.lastTargetUpdateTick + 1L;
         this.lastTargetUpdateTick = time;
         if (!vecFlag) {
            this.lastTargetPos = vec3d;
            this.targetBaseTick = time;
            this.targetVelX = 0.0;
            this.targetVelZ = 0.0;
         } else {
            double deltaX = vec3d.x - this.lastTargetPos.x;
            double deltaZ = vec3d.z - this.lastTargetPos.z;
            double hypot = Math.hypot(deltaX, deltaZ);
            long vecLimit = Math.max(1L, time - this.targetBaseTick);
            if (!(hypot > 1.0E-4)) {
               if (vecLimit >= 3L) {
                  this.targetVelX *= 0.5;
                  this.targetVelZ *= 0.5;
               }
            } else {
               int vecDivisor = (int)Math.min(vecLimit, 2L);
               if (hypot > 1.5 * vecDivisor) {
                  this.targetVelX = 0.0;
                  this.targetVelZ = 0.0;
               } else {
                  this.targetVelX = this.targetVelX + 0.5 * (deltaX / vecDivisor - this.targetVelX);
                  this.targetVelZ = this.targetVelZ + 0.5 * (deltaZ / vecDivisor - this.targetVelZ);
               }

               this.lastTargetPos = vec3d;
               this.targetBaseTick = time;
            }
         }
      }
   }

   static boolean isVerticalDeltaInReach(Vec3d vec, Vec3d vec2) {
      double deltaY = vec.y - vec2.y;
      return deltaY >= -0.25 && deltaY <= 1.5;
   }

   static boolean isWithinLowMeleeHeight(PreferAnchorData preferAnchorData, double lowMeleeValue, double lowMeleeValue2) {
      return preferAnchorData != null && preferAnchorData.kind() == PreferAnchorData.Kind.MELEE_LOW && Double.isFinite(lowMeleeValue) ? lowMeleeValue2 <= lowMeleeValue + 0.6 + 0.001 : true;
   }

   private PlayerInputOverride.Intent getFireEscapeIntent(long time, AbsorptionPosData absorptionPosData, Vec3d vec, ComboTradeData comboTradeData) {
      boolean posBlockFlag = this.lastFireCheckTick == Long.MIN_VALUE || time < this.lastFireCheckTick || time - this.lastFireCheckTick > 1L;
      this.lastFireCheckTick = time;
      if (posBlockFlag) {
         this.fireEscapeActive = false;
         this.fireEscapeBlockedUntilTick = Long.MIN_VALUE;
      }

      if (comboTradeData != null && comboTradeData.lane() == SurroundKitType.PEARL) {
         return null;
      } else {
         Vec3d pos = absorptionPosData.pos();
         if (!this.isStandingInFire(pos)) {
            this.fireEscapeActive = false;
            return null;
         } else {
            if (this.fireEscapeActive && (time < this.fireEscapeLastSampleTick || time - this.fireEscapeLastSampleTick >= 6L)) {
               this.fireEscapeActive = false;
               this.fireEscapeBlockedUntilTick = time + 6L;
            }

            if (!this.fireEscapeActive) {
               if (this.fireEscapeBlockedUntilTick != Long.MIN_VALUE && time < this.fireEscapeBlockedUntilTick) {
                  return null;
               }

               double[] apiFloored = this.findFireEscapeDirection(pos, vec);
               if (apiFloored == null) {
                  return null;
               }

               this.fireEscapeActive = true;
               this.fireEscapeLastSampleTick = time;
               this.fireEscapeDirX = apiFloored[0];
               this.fireEscapeDirZ = apiFloored[1];
            }

            this.pendingMoveStatus = CombatMovementController.MoveStatus.FIRE;
            return this.applyLedgeSafety(absorptionPosData, buildIntent(true, this.fireEscapeDirX, this.fireEscapeDirZ, false, false, false));
         }
      }
   }

   private static PlayerInputOverride.Intent buildIntent(boolean posBlockFlag, double posBlockValue, double deltaZ, boolean posBlockFlag2, boolean swimmingSprinting, boolean posBlockFlag4) {
      double[] posBlockValues = posBlockFlag ? normalizeXZ(posBlockValue, deltaZ) : null;
      boolean hasVar = posBlockValues != null;
      return new PlayerInputOverride.Intent(
         hasVar,
         hasVar ? posBlockValues[0] : 0.0,
         hasVar ? posBlockValues[1] : 0.0,
         posBlockFlag2 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
         PlayerInputOverride.Tri.OFF,
         hasVar && swimmingSprinting ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
         posBlockFlag4
      );
   }

   int getInterceptCellCount() {
      return this.interceptCellCount;
   }

   static double[] searchRepositionCell(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, Vec3d vec3, AmountConstTime.Spot spot, int posBlockValue) {
      return getAmounts(env, vec, vec2, vec3, spot, posBlockValue, false);
   }

   private boolean isStepUpAhead(Vec3d vec, double apiValue, double apiValue2, double apiValue3) {
      return canFlag(this.worldEnv, vec, apiValue, apiValue2, apiValue3);
   }

   private PlayerInputOverride.Intent applyLedgeSafety(AbsorptionPosData absorptionPosData, PlayerInputOverride.Intent intent) { boolean var56_u = false;  com.corz.client.utils.PlayerInputOverride.Tri var54_u = null;  com.corz.client.utils.PlayerInputOverride.Tri var51_u = null;  double var49_u = (double) 0;  double var47_u = (double) 0;  boolean var45_u = false; 
      if (intent instanceof PlayerInputOverride.Intent) {
         PlayerInputOverride.Intent intent2 = intent;

         try {
            var45_u = intent2.move();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         boolean var45_u2 = var45_u;
         intent2 = intent;

         try {
            var47_u = intent2.dirX();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         double var47_u2 = var47_u;
         intent2 = intent;

         try {
            var49_u = intent2.dirZ();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         double var49_u2 = var49_u;
         intent2 = intent;

         try {
            var51_u = intent2.jump();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         PlayerInputOverride.Tri var51_u2 = var51_u;
         intent2 = intent;

         try {
            intent2.sneak();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         intent2 = intent;

         try {
            var54_u = intent2.sprint();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         PlayerInputOverride.Tri var54_u2 = var54_u;
         intent2 = intent;

         try {
            var56_u = intent2.urgent();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         boolean var56_u2 = var56_u;
         if (!var45_u2 || !this.isNextStepGapJump() || var51_u2 != PlayerInputOverride.Tri.ON && absorptionPosData.onGround()) {
            CombatMovementController.Edge velocityPos = this.detectLedgeAhead(absorptionPosData, var45_u2 ? var47_u2 : 0.0, var45_u2 ? var49_u2 : 0.0, var45_u2);
            if (!velocityPos.unsafe()) {
               return new PlayerInputOverride.Intent(var45_u2, var47_u2, var49_u2, var51_u2, PlayerInputOverride.Tri.OFF, var54_u2, var56_u2);
            } else {
               this.pendingMoveStatus = CombatMovementController.MoveStatus.EDGE;
               if (var45_u2 && this.steeringThisTick) {
                  this.edgeGuardUsedThisTick = true;
               }

               boolean dirMaskedFlag = var45_u2;
               double dirMaskedValue = var47_u2;
               double var49u = var49_u2;
               double dirMaskedFactor = var47_u2 * velocityPos.ux() + var49_u2 * velocityPos.uz();
               if (var45_u2 && dirMaskedFactor > 0.0) {
                  double deltaX = var47_u2 - dirMaskedFactor * velocityPos.ux();
                  double deltaZ = var49_u2 - dirMaskedFactor * velocityPos.uz();
                  double hypot = Math.hypot(deltaX, deltaZ);
                  if (hypot < 0.15) {
                     dirMaskedFlag = false;
                     dirMaskedValue = 0.0;
                     var49u = 0.0;
                  } else {
                     dirMaskedValue = deltaX / hypot;
                     var49u = deltaZ / hypot;
                  }
               }

               Vec3d velocity = absorptionPosData.velocity();
               return !dirMaskedFlag
                     && velocity != null
                     && Double.isFinite(velocity.x)
                     && Double.isFinite(velocity.z)
                     && Math.hypot(velocityPos.ux(), velocityPos.uz()) > 0.5
                     && velocity.x * velocityPos.ux() + velocity.z * velocityPos.uz() > 0.05
                  ? new PlayerInputOverride.Intent(
                     true, -velocityPos.ux(), -velocityPos.uz(), PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, true
                  )
                  : new PlayerInputOverride.Intent(
                     dirMaskedFlag,
                     dirMaskedValue,
                     var49u,
                     var51_u2 == PlayerInputOverride.Tri.ON ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
                     PlayerInputOverride.Tri.OFF,
                     dirMaskedFlag && var54_u2 == PlayerInputOverride.Tri.ON ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
                     true
                  );
            }
         } else {
            return new PlayerInputOverride.Intent(var45_u2, var47_u2, var49_u2, var51_u2, PlayerInputOverride.Tri.OFF, var54_u2, var56_u2);
         }
      } else {
         return PlayerInputOverride.Intent.idle();
      }
   }

   private boolean canSprintJumpAhead(AbsorptionPosData absorptionPosData, double posBlockValue, double posBlockValue2, double apiVelocityLimit) {
      return this.canApiVelocity(absorptionPosData, posBlockValue, posBlockValue2, apiVelocityLimit, false);
   }

   private static boolean isMeleeOrNoLane(SurroundKitType surroundKitType) {
      return surroundKitType == null || surroundKitType == SurroundKitType.NONE || surroundKitType == SurroundKitType.MELEE;
   }

   private PlayerInputOverride.Intent finalizeIntent(PlayerInputOverride.Intent intent) {
      this.steeredLastTick = this.steeringThisTick || this.edgeGuardUsedThisTick;
      this.lastGoalDistance = this.goalDistance;
      this.steeringThisTick = false;
      this.edgeGuardUsedThisTick = false;
      this.goalDistance = Double.NaN;
      PlayerInputOverride.Intent dirAmount = this.recordLastIntent(intent);
      this.lastMoveStatus = this.stuckDetected
         ? CombatMovementController.MoveStatus.STALL
         : (this.pendingMoveStatus != null ? this.pendingMoveStatus : (dirAmount.move() ? CombatMovementController.MoveStatus.MOVE : CombatMovementController.MoveStatus.IDLE));
      return dirAmount;
   }

   private CombatMovementController.Approach computeApproach(long maskedValue, AbsorptionPosData absorptionPosData, Vec3d vec, double maskedBound, double hashRegistryBound, PreferAnchorData preferAnchorData) { double var86_u = (double) 0;  boolean var82_u = false;  boolean var80_u = false;  boolean var78_u = false;  double var76_u = (double) 0;  double var74_u = (double) 0;  boolean var72_u = false; 
      Vec3d pos = absorptionPosData.pos();
      double deltaX = vec.x - pos.x;
      double deltaZ = vec.z - pos.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      boolean maskedFlag = (!Double.isFinite(vec.y) || Math.abs(vec.y - pos.y) <= 1.1) && isWithinLowMeleeHeight(preferAnchorData, vec.y, pos.y);
      boolean maskedFlag2 = this.isStandingInFire(vec) && !this.isStandingInFire(pos);
      double maskedLimit = maskedFlag2 ? Math.max(maskedBound, 1.6) : maskedBound;
      double maskedValue2 = maskedFlag2 ? Math.max(hashRegistryBound, 2.0) : hashRegistryBound;
      boolean anchor = isWithinAnchorRange(preferAnchorData, pos, vec, maskedBound);
      this.trackGoalPosition(vec);
      if (this.arrivedLatched && (hypot > maskedValue2 || !maskedFlag && absorptionPosData.onGround() || !anchor)) {
         this.arrivedLatched = false;
      }

      if (!this.arrivedLatched && hypot <= maskedLimit && maskedFlag && anchor) {
         this.arrivedLatched = true;
         this.nextStrafeRollTick = Long.MIN_VALUE;
      }

      if (this.arrivedLatched) {
         this.pathCommand = null;
         this.goalDistance = hypot;
         this.pendingMoveStatus = CombatMovementController.MoveStatus.ARRIVED;
         return new CombatMovementController.Approach(false, 0.0, 0.0, true, hypot, false, false);
      } else {
         this.steeringThisTick = true;
         PreferAnchorData preferAnchorData2 = preferAnchorData != null ? preferAnchorData : PreferAnchorData.getCell(vec);
         if (this.pathfinder.canReachDirectly(maskedValue, pos, preferAnchorData2)) {
            this.pathCommand = null;
            this.goalDistance = hypot;
            double[] maskedValues = normalizeXZ(deltaX, deltaZ);
            this.storeMoveDirection(maskedValues);
            Vec3d velocity = absorptionPosData.velocity();
            boolean maskedFlag3 = maskedValue < this.sidestepUntilTick;
            boolean maskedFlag4 = isMeleeGoal(preferAnchorData2) && this.getTargetSpeed() >= 0.08;
            if (!maskedFlag3 && !this.leadingTargetActive && !maskedFlag4 && absorptionPosData.onGround() && velocity != null && velocity.x * deltaX + velocity.z * deltaZ > 0.0 && hypot <= getStoppingDistance(velocity)) {
               this.pendingMoveStatus = CombatMovementController.MoveStatus.BRAKE;
               return new CombatMovementController.Approach(false, 0.0, 0.0, false, hypot, false, false);
            } else {
               double[] maskedValues2 = maskedFlag3 ? new double[]{this.sidestepDirX, this.sidestepDirZ} : (maskedValues == null ? ZERO_DIRECTION : maskedValues);
               return new CombatMovementController.Approach(true, maskedValues2[0], maskedValues2[1], false, hypot, false, false);
            }
         } else {
            PathFollower.Cmd data = this.pathfinder
               .computeWalkCommand(maskedValue, pos, absorptionPosData.velocity(), absorptionPosData.onGround(), this.worldEnv.isHorizontalCollision(), this.worldEnv.isTouchingWater(), preferAnchorData2);
            this.pathCommand = data;
            boolean maskedFlag5 = maskedValue < this.sidestepUntilTick;
            if (data instanceof PathFollower.Cmd) {
               PathFollower.Cmd cmd = data;

               try {
                  var72_u = cmd.move();
               } catch (Throwable e) {
                  throw new MatchException(e.toString(), e);
               }

               boolean var72_u2 = var72_u;
               cmd = data;

               try {
                  var74_u = cmd.dx();
               } catch (Throwable e2) {
                  throw new MatchException(e2.toString(), e2);
               }

               double var74_u2 = var74_u;
               cmd = data;

               try {
                  var76_u = cmd.dz();
               } catch (Throwable e3) {
                  throw new MatchException(e3.toString(), e3);
               }

               double var76_u2 = var76_u;
               cmd = data;

               try {
                  var78_u = cmd.jump();
               } catch (Throwable e4) {
                  throw new MatchException(e4.toString(), e4);
               }

               boolean var78_u2 = var78_u;
               cmd = data;

               try {
                  var80_u = cmd.sprint();
               } catch (Throwable e5) {
                  throw new MatchException(e5.toString(), e5);
               }

               boolean var80_u2 = var80_u;
               cmd = data;

               try {
                  var82_u = cmd.done();
               } catch (Throwable e6) {
                  throw new MatchException(e6.toString(), e6);
               }

               boolean var82_u2 = var82_u;
               cmd = data;

               try {
                  cmd.offPath();
               } catch (Throwable e7) {
                  throw new MatchException(e7.toString(), e7);
               }

               cmd = data;

               try {
                  cmd.index();
               } catch (Throwable e8) {
                  throw new MatchException(e8.toString(), e8);
               }

               cmd = data;

               try {
                  var86_u = cmd.remaining();
               } catch (Throwable e9) {
                  throw new MatchException(e9.toString(), e9);
               }

               double var86_u2 = var86_u;
               cmd = data;

               try {
                  cmd.next();
               } catch (Throwable e10) {
                  throw new MatchException(e10.toString(), e10);
               }

               this.goalDistance = Double.isFinite(var86_u2) ? var86_u2 : hypot;
               if (var82_u2) {
                  this.steeringThisTick = false;
                  this.pendingMoveStatus = CombatMovementController.MoveStatus.ARRIVED;
                  return new CombatMovementController.Approach(false, 0.0, 0.0, true, this.goalDistance, false, false);
               } else if (!var72_u2) {
                  return maskedFlag5
                     ? new CombatMovementController.Approach(true, this.sidestepDirX, this.sidestepDirZ, false, this.goalDistance, false, false)
                     : new CombatMovementController.Approach(false, 0.0, 0.0, false, this.goalDistance, false, false);
               } else {
                  double[] normalizeXz = normalizeXZ(var74_u2, var76_u2);
                  this.storeMoveDirection(normalizeXz);
                  double[] maskedValues4 = maskedFlag5 ? new double[]{this.sidestepDirX, this.sidestepDirZ} : (normalizeXz == null ? ZERO_DIRECTION : normalizeXz);
                  return new CombatMovementController.Approach(true, maskedValues4[0], maskedValues4[1], false, this.goalDistance, var78_u2, var80_u2);
               }
            } else {
               this.goalDistance = hypot;
               return maskedFlag5
                  ? new CombatMovementController.Approach(true, this.sidestepDirX, this.sidestepDirZ, false, hypot, false, false)
                  : new CombatMovementController.Approach(false, 0.0, 0.0, false, hypot, false, false);
            }
         }
      }
   }

   static boolean canFlag(CombatMovementController.Env env, double[] posBlockValues, Vec3d vec, Vec3d vec2, AmountConstTime.Spot spot, boolean posBlockFlag) {
      if (posBlockValues != null && posBlockValues.length >= 3) {
         boolean posBlockFlag2 = posBlockValues.length > 3 && posBlockValues[3] > 0.5;
         boolean posBlockFlag3 = posBlockValues.length > 4 && posBlockValues[4] > 0.5;
         if ((posBlockFlag2 || posBlockFlag3) && !posBlockFlag) {
            return false;
         } else {
            double hypot = Math.hypot(posBlockValues[0] - vec.x, posBlockValues[2] - vec.z);
            if (!(hypot < 1.7) && !(hypot > 3.5)) {
               if (!posBlockFlag3 && Math.hypot(posBlockValues[0] - vec2.x, posBlockValues[2] - vec2.z) > 2.8) {
                  return false;
               } else {
                  int posBlockValue = (int)Math.floor(posBlockValues[0]);
                  int z2 = (int)Math.floor(posBlockValues[2]);
                  double groundY = posBlockFlag2 ? getVisibleCellHeightLevel(env, posBlockValue, z2, vec, spot) : getVisibleCellHeightBelow(env, posBlockValue, z2, vec, spot);
                  return Double.isFinite(groundY) && Math.abs(groundY - posBlockValues[1]) < 1.0E-6;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   static double getWaterSurfaceHeight(CombatMovementController.Env env, Vec3d vec) {
      int x = (int)Math.floor(vec.x);
      int z = (int)Math.floor(vec.z);
      int posBlockValue = (int)Math.floor(vec.y + 0.001);
      int minValue = Integer.MIN_VALUE;

      for (int i = 0; i < 12 && (env.getHazardFlags(new BlockPos(x, posBlockValue + i, z)) & 32) != 0; i++) {
         minValue = posBlockValue + i;
      }

      return minValue == Integer.MIN_VALUE ? vec.y : Math.max(vec.y, (double)minValue);
   }

   private CombatMovementController.Edge buildLedgeEdge(Vec3d vec, double posBlockValue, double spotValue, boolean posBlockFlag) {
      boolean posBlockFlag2 = Math.abs(posBlockValue) > 1.0E-6 && this.isCellUnsafe(vec, vec.x + posBlockValue, vec.z);
      boolean posBlockFlag3 = Math.abs(spotValue) > 1.0E-6 && this.isCellUnsafe(vec, vec.x, vec.z + spotValue);
      double posBlockValue3;
      double signum4;
      if (posBlockFlag3 && !posBlockFlag2) {
         posBlockValue3 = 0.0;
         signum4 = Math.signum(spotValue);
      } else if (posBlockFlag2 && !posBlockFlag3) {
         posBlockValue3 = Math.signum(posBlockValue);
         signum4 = 0.0;
      } else {
         double[] posBlockValues = normalizeXZ(posBlockValue, spotValue);
         posBlockValue3 = posBlockValues == null ? 0.0 : posBlockValues[0];
         signum4 = posBlockValues == null ? 0.0 : posBlockValues[1];
      }

      return new CombatMovementController.Edge(posBlockFlag, !posBlockFlag, true, posBlockValue3, signum4);
   }

   static int getSideRelativeToSpot(AmountConstTime.Spot spot, Vec3d vec, double posBlockFactor, double posBlockFactor2) {
      if (spot != null && vec != null && isFiniteVec(vec)) {
         double deltaX = vec.x - spot.getX();
         double deltaZ = vec.z - spot.getZ();
         if (!(Math.hypot(deltaX, deltaZ) <= 6.0)) {
            return 0;
         } else {
            double posBlockLimit = deltaX * posBlockFactor + deltaZ * posBlockFactor2;
            return posBlockLimit > 0.001 ? 1 : (posBlockLimit < -0.001 ? -1 : 0);
         }
      } else {
         return 0;
      }
   }

   static double[] getAmounts(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, Vec3d vec3, AmountConstTime.Spot spot, int posBlockValue, boolean posBlockFlag) {
      double[] posBlockValues = getAmounts(env, vec, vec2, vec3, spot, posBlockValue, false, false);
      if (posBlockValues == null && posBlockFlag) {
         double[] amounts2 = getAmounts(env, vec, vec2, vec3, spot, posBlockValue, false, true);
         return amounts2 != null ? amounts2 : getAmounts(env, vec, vec2, vec3, spot, posBlockValue, true, true);
      } else {
         return posBlockValues;
      }
   }

   public float[] getRouteLookAngles(float vecHashValue) {
      return this.pathCommand == null ? null : this.pathfinder.getRouteLookTarget(vecHashValue);
   }

   static boolean isTerminatorStatus(ComboTradeData comboTradeData) {
      return comboTradeData != null && "Terminator".equals(comboTradeData.status());
   }

   private void updateLaunchPhase(long juggleLaunchLimit, long time, CenterVecData.State state2, boolean juggleLaunchFlag) {
      if (time != this.launchPhaseTick) {
         this.launchPhaseTick = time;
         this.launchPhaseActive = false;
      }

      if (time != Long.MIN_VALUE && juggleLaunchLimit > time) {
         if (juggleLaunchFlag || state2 == CenterVecData.State.LAUNCH || state2 == CenterVecData.State.JUGGLE) {
            this.launchPhaseActive = true;
         }
      }
   }

   public String getMoveStatusName() {
      CombatMovementController.MoveStatus dodgeMaskedType = this.lastMoveStatus;
      return dodgeMaskedType == null ? "idle" : dodgeMaskedType.name().toLowerCase(Locale.ROOT);
   }

   static double[] getAmounts(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, Vec3d vec3, AmountConstTime.Spot spot, int posBlockBound, boolean posBlockFlag, boolean posBlockFlag2) {
      if (env != null && vec != null && vec2 != null && vec3 != null && isFiniteVec(vec) && isFiniteVec(vec2) && isFiniteVec(vec3)) {
         HashSet hashSet = new HashSet();
         double posBlockValue = Double.POSITIVE_INFINITY;
         double[] posBlockValues = null;

         for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2) * i / 24.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (double posBlockFactor : ANCHOR_RING_RADII) {
               int floor2 = (int)Math.floor(vec2.x + cos * posBlockFactor);
               int z2 = (int)Math.floor(vec2.z + sin * posBlockFactor);
               if (hashSet.add((long)floor2 << 32 ^ z2 & 4294967295L)) {
                  double x = floor2 + 0.5;
                  double z = z2 + 0.5;
                  double hypot = Math.hypot(x - vec2.x, z - vec2.z);
                  if (!(hypot < 1.999) && !(hypot > 3.201)) {
                     double hypot2 = Math.hypot(x - vec3.x, z - vec3.z);
                     if (posBlockFlag2 || !(hypot2 > 2.501)) {
                        double posBlockLimit = posBlockFlag2 ? Math.hypot(x - vec.x, z - vec.z) + 0.5 * hypot2 : hypot2;
                        if (!(posBlockLimit >= posBlockValue)) {
                           double y = posBlockFlag ? getVisibleCellHeightLevel(env, floor2, z2, vec2, spot) : getVisibleCellHeightBelow(env, floor2, z2, vec2, spot);
                           if (Double.isFinite(y) && canFlagForEnvVec3d(env, vec, new Vec3d(x, y, z), true, 10.0, Math.max(0, posBlockBound))) {
                              posBlockValue = posBlockLimit;
                              posBlockValues = new double[]{x, y, z, posBlockFlag ? 1.0 : 0.0, posBlockFlag2 ? 1.0 : 0.0};
                           }
                        }
                     }
                  }
               }
            }
         }

         return posBlockValues;
      } else {
         return null;
      }
   }

   private Vec3d findRepositionCell(long time, Vec3d vec, PreferAnchorData preferAnchorData, Vec3d vec2, Vec3d vec3, boolean apiFlag) {
      Vec3d anchor = preferAnchorData.anchor();
      if (anchor != null
         && vec2 != null
         && vec != null
         && vec3 != null
         && isFiniteVec(anchor)
         && isFiniteVec(vec2)
         && isFiniteVec(vec3)
         && isVerticalDeltaInReach(vec2, anchor)
         && !(Math.hypot(vec2.x - vec.x, vec2.z - vec.z) > 6.0)) {
         boolean higherThanAnchor = isHigherThanAnchor(this.worldEnv, vec, anchor, vec3, apiFlag);
         double[] posBlockValues = this.cachedRepositionCell;
         if (posBlockValues != null && canFlag(this.worldEnv, posBlockValues, anchor, vec3, this.threatSpot, higherThanAnchor)) {
            return new Vec3d(posBlockValues[0], posBlockValues[1], posBlockValues[2]);
         } else {
            this.cachedRepositionCell = null;
            if (this.lastRepositionSearchTick != Long.MIN_VALUE && time >= this.lastRepositionSearchTick && time - this.lastRepositionSearchTick < 4L) {
               return null;
            } else {
               this.lastRepositionSearchTick = time;
               double[] amounts = getAmounts(this.worldEnv, vec, anchor, vec3, this.threatSpot, this.pathfinder.getMaxDrop(), higherThanAnchor);
               this.cachedRepositionCell = amounts;
               return amounts == null ? null : new Vec3d(amounts[0], amounts[1], amounts[2]);
            }
         }
      } else {
         this.clearRepositionCache();
         return null;
      }
   }

   static boolean isMeleeGoal(PreferAnchorData preferAnchorData) {
      return preferAnchorData != null && (preferAnchorData.kind() == PreferAnchorData.Kind.MELEE || preferAnchorData.kind() == PreferAnchorData.Kind.MELEE_LOW);
   }

   static boolean canWalkStraight(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, boolean posBlockFlag, double posBlockValue, int flooredValue) {
      return !Double.isNaN(traceStraightWalk(env, vec, vec2, posBlockFlag, posBlockValue, flooredValue));
   }

   static boolean isHigherThanAnchor(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, Vec3d vec3, boolean posBlockFlag) {
      if (env != null && vec != null && vec2 != null && vec3 != null && isFiniteVec(vec) && isFiniteVec(vec2) && isFiniteVec(vec3)) {
         if (posBlockFlag && vec.y - vec2.y > 0.501) {
            return true;
         } else {
            double posBlockValue = getAmount(env, vec3.x, vec3.z, Math.max(vec.y, vec2.y), 3);
            return Double.isFinite(posBlockValue) && posBlockValue - vec2.y > 0.501;
         }
      } else {
         return false;
      }
   }

   static boolean canFlag(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, boolean posBlockFlag, double posBlockValue) {
      return canWalkStraight(env, vec, vec2, posBlockFlag, posBlockValue, 3);
   }

   private boolean isDodgeActive(long posBlockValue, CrystalDodgePlanner.Dodge dodge) {
      return dodge != null || this.activeDodge != null && posBlockValue < this.dodgeHoldUntilTick;
   }

   static boolean isInWater(CombatMovementController.Env env, Vec3d vec) {
      return env != null && vec != null && isFiniteVec(vec) ? (env.getHazardFlags(BlockPos.ofFloored(vec.x, vec.y + 0.001, vec.z)) & 32) != 0 : false;
   }

   static double getMeleeCellHeight(CombatMovementController.Env env, int x2, int z2, Vec3d vec, AmountConstTime.Spot spot) {
      double posBlockBound = getAmount(env, x2 + 0.5, z2 + 0.5, vec.y, 2);
      if (!Double.isFinite(posBlockBound)) {
         return Double.NaN;
      } else {
         int y = (int)Math.floor(posBlockBound + 0.001);
         if ((env.getHazardFlags(new BlockPos(x2, y, z2)) & 1407) != 0) {
            return Double.NaN;
         } else if (!env.hasPendingBlockUpdate(x2, y - 1, z2) && !env.hasPendingBlockUpdate(x2, y, z2) && !env.hasPendingBlockUpdate(x2, y + 1, z2)) {
            double spotOffset = x2 + 0.5;
            double posBlockValue = z2 + 0.5;
            if (!EmptyLvl.canFlag2(spotOffset, posBlockBound + 1.62, posBlockValue, vec.x, vec.y, vec.z)) {
               return Double.NaN;
            } else {
               int floor2 = (int)Math.floor(Math.max(posBlockBound, vec.y) + 1.0 + 0.001);

               for (int i = 1; i <= 3; i++) {
                  double posBlockFactor = i / 4.0;
                  if (env.isSolidAt((int)Math.floor(spotOffset + (vec.x - spotOffset) * posBlockFactor), floor2, (int)Math.floor(posBlockValue + (vec.z - posBlockValue) * posBlockFactor))) {
                     return Double.NaN;
                  }
               }

               if (spot != null) {
                  double posBlockFactor2 = spotOffset - spot.getX();
                  double posBlockFactor3 = posBlockBound - spot.getY();
                  double posBlockFactor4 = posBlockValue - spot.getZ();
                  if (HeightDeltaMath.getDistanceWeightedReachFraction(posBlockBound, Math.sqrt(posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3 + posBlockFactor4 * posBlockFactor4), spot.getAmount()) >= 0.25) {
                     return Double.NaN;
                  }
               }

               return posBlockBound;
            }
         } else {
            return Double.NaN;
         }
      }
   }

   static boolean isNearAnchor(Vec3d vec, PreferAnchorData preferAnchorData) {
      if (vec != null && preferAnchorData != null && preferAnchorData.anchor() != null && isFiniteVec(vec) && isFiniteVec(preferAnchorData.anchor())) {
         Vec3d anchor = preferAnchorData.anchor();
         return Math.hypot(vec.x - anchor.x, vec.z - anchor.z) < 1.7 && vec.y - anchor.y <= 0.501;
      } else {
         return false;
      }
   }

   static boolean isIncompletePathMode(AnchorRoutePlanner.Mode mode) {
      return mode == AnchorRoutePlanner.Mode.UNREACHABLE || mode == AnchorRoutePlanner.Mode.SEARCHING || mode == AnchorRoutePlanner.Mode.PARTIAL;
   }

   static double[] searchInterceptCell(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, double posBlockFactor, double groundY, int posBlockBound, boolean posBlockFlag) {
      if (env != null && vec != null && vec2 != null && isFiniteVec(vec) && isFiniteVec(vec2)) {
         double posBlockFactor3 = posBlockFlag ? 1.0 : 0.25;
         double posBlockFactor4 = posBlockFlag ? 0.5 : 1.0;
         double posBlockLimit = Double.isFinite(posBlockFactor) && Double.isFinite(groundY) ? Math.hypot(posBlockFactor, groundY) : 0.0;
         boolean posBlockFlag2 = posBlockLimit >= 0.05;
         double posBlockValue = posBlockFlag ? getWaterSurfaceHeight(env, vec) : vec.y;
         HashSet hashSet = new HashSet();
         double positiveInfinity = Double.POSITIVE_INFINITY;
         double[] posBlockValues = null;

         for (double posBlockFactor5 = 1.0; posBlockFactor5 <= 24.001 && !(posBlockFactor3 * (posBlockFactor5 - 1.0) - 3.0 >= positiveInfinity); posBlockFactor5++) {
            int max2 = Math.max(8, Math.min(48, (int)Math.ceil((Math.PI * 2) * posBlockFactor5 / 0.9)));

            for (int i = 0; i < max2; i++) {
               double angle = (Math.PI * 2) * i / max2;
               int x2 = (int)Math.floor(vec.x + Math.cos(angle) * posBlockFactor5);
               int z2 = (int)Math.floor(vec.z + Math.sin(angle) * posBlockFactor5);
               if (hashSet.add((long)x2 << 32 ^ z2 & 4294967295L)) {
                  double x = x2 + 0.5;
                  double z = z2 + 0.5;
                  double hypot = Math.hypot(x - vec2.x, z - vec2.z);
                  double posBlockValue5 = posBlockFlag2 && hypot > 1.0E-6 ? Math.max(0.0, (posBlockFactor * (x - vec2.x) + groundY * (z - vec2.z)) / (posBlockLimit * hypot)) : 0.0;
                  double posBlockLimit3 = posBlockFactor3 * Math.hypot(x - vec.x, z - vec.z) + posBlockFactor4 * hypot - 3.0 * posBlockValue5;
                  if (!(posBlockLimit3 >= positiveInfinity)) {
                     double y = getSafeCellHeight(env, x2, z2, posBlockValue);
                     if (Double.isFinite(y)) {
                        Vec3d vec3d = new Vec3d(x, y, z);
                        if (posBlockFlag ? !(y > posBlockValue + 1.2 + 0.001) : canWalkStraight(env, vec, vec3d, true, 8.0, Math.max(0, posBlockBound))) {
                           positiveInfinity = posBlockLimit3;
                           posBlockValues = new double[]{x, y, z};
                        }
                     }
                  }
               }
            }
         }

         return posBlockValues;
      } else {
         return null;
      }
   }

   private void startSidestep(long apiValue, Vec3d vec) {
      double[] apiValues = normalizeXZ(this.lastMoveDirX, this.lastMoveDirZ);
      if (apiValues == null) {
         apiValues = normalizeXZ(this.lastIntentDirX, this.lastIntentDirZ);
      }

      if (apiValues != null) {
         double apiFactor = -apiValues[1];
         double apiFactor2 = apiValues[0];
         int maxDrop = this.pathfinder.getMaxDrop();
         boolean apiFlag = canWalkStraight(this.worldEnv, vec, new Vec3d(vec.x + apiFactor * 2.0, vec.y, vec.z + apiFactor2 * 2.0), true, 2.0, maxDrop);
         boolean canWalkStraight2 = canWalkStraight(this.worldEnv, vec, new Vec3d(vec.x - apiFactor * 2.0, vec.y, vec.z - apiFactor2 * 2.0), true, 2.0, maxDrop);
         int apiFactor3 = apiFlag == canWalkStraight2 ? (this.random.nextBoolean() ? 1 : -1) : (apiFlag ? 1 : -1);
         this.sidestepDirX = apiFactor * apiFactor3;
         this.sidestepDirZ = apiFactor2 * apiFactor3;
         this.sidestepUntilTick = apiValue + 8L;
      }
   }

   CombatMovementController(Random random, CombatMovementController.Env env, AnchorRoutePlanner vecHash) {
      this.lastGoalDistance = Double.NaN;
      this.goalDistance = Double.NaN;
      this.sidestepUntilTick = Long.MIN_VALUE;
      this.strafeDirectionSign = 1;
      this.nextStrafeRollTick = Long.MIN_VALUE;
      this.pearlLandingSeenTick = Long.MIN_VALUE;
      this.lastSetbackAgeMs = Long.MAX_VALUE;
      this.pearlThrownTick = Long.MIN_VALUE;
      this.lastPearlCount = -1;
      this.dodgeHoldUntilTick = Long.MIN_VALUE;
      this.launchPhaseTick = Long.MIN_VALUE;
      this.previousSwordCharge = Float.NaN;
      this.critJumpStartTick = Long.MIN_VALUE;
      this.lastFireCheckTick = Long.MIN_VALUE;
      this.fireEscapeLastSampleTick = Long.MIN_VALUE;
      this.fireEscapeBlockedUntilTick = Long.MIN_VALUE;
      this.lastMoveStatus = CombatMovementController.MoveStatus.IDLE;
      this.lastStuckRecoveryTick = Long.MIN_VALUE;
      this.targetBaseTick = Long.MIN_VALUE;
      this.lastTargetUpdateTick = Long.MIN_VALUE;
      this.lastRepositionSearchTick = Long.MIN_VALUE;
      this.lastMeleeCellSearchTick = Long.MIN_VALUE;
      this.lastInterceptSearchTick = Long.MIN_VALUE;
      this.random = random == null ? new Random() : random;
      this.worldEnv = (CombatMovementController.Env)(env == null ? new CombatMovementController.McEnv() : env);
      this.pathfinder = vecHash == null ? AnchorRoutePlanner.createDirectOnly() : vecHash;
   }

   private PlayerInputOverride.Intent recordLastIntent(PlayerInputOverride.Intent intent) { com.corz.client.utils.PlayerInputOverride.Tri var36_u = null;  double var34_u = (double) 0;  double var32_u = (double) 0;  boolean var30_u = false; 
      PlayerInputOverride.Intent dirAmount = intent == null ? PlayerInputOverride.Intent.idle() : intent;
      if (dirAmount instanceof PlayerInputOverride.Intent) {
         PlayerInputOverride.Intent dirAmount2 = dirAmount;

         try {
            var30_u = dirAmount2.move();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         boolean var30_u2 = var30_u;
         dirAmount2 = dirAmount;

         try {
            var32_u = dirAmount2.dirX();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         double amount = var32_u;
         dirAmount2 = dirAmount;

         try {
            var34_u = dirAmount2.dirZ();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         double var34u = var34_u;
         dirAmount2 = dirAmount;

         try {
            var36_u = dirAmount2.jump();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         PlayerInputOverride.Tri var36_u2 = var36_u;
         dirAmount2 = dirAmount;

         try {
            dirAmount2.sneak();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         dirAmount2 = dirAmount;

         try {
            dirAmount2.sprint();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         dirAmount2 = dirAmount;

         try {
            dirAmount2.urgent();
         } catch (Throwable e7) {
            throw new MatchException(e7.toString(), e7);
         }

         this.lastIntentActive = var30_u2 || var36_u2 == PlayerInputOverride.Tri.ON;
         if (var30_u2) {
            this.lastIntentDirX = amount;
            this.lastIntentDirZ = var34u;
         }

         return dirAmount;
      } else {
         this.lastIntentActive = false;
         return PlayerInputOverride.Intent.idle();
      }
   }

   private long getSetbackWindowMs(long posBlockLimit, long time) {
      if (time < this.lastSetbackAgeMs) {
         this.pearlSetbackExpected = this.pearlLandingSeenTick != Long.MIN_VALUE && posBlockLimit >= this.pearlLandingSeenTick && posBlockLimit - this.pearlLandingSeenTick <= 4L;
         if (this.pearlSetbackExpected) {
            this.pearlLandingSeenTick = Long.MIN_VALUE;
         }
      }

      this.lastSetbackAgeMs = time;
      return this.pearlSetbackExpected ? 150L : 300L;
   }

   static boolean isPureMeleeEngagement(Vec3d vec, PreferAnchorData preferAnchorData, CenterVecData centerVecData, ComboTradeData comboTradeData, boolean meleeLaneFlag) {
      if (!meleeLaneFlag && vec != null && isFiniteVec(vec) && preferAnchorData != null && centerVecData != null && comboTradeData != null) {
         if (preferAnchorData.kind() != PreferAnchorData.Kind.MELEE && preferAnchorData.kind() != PreferAnchorData.Kind.MELEE_LOW) {
            return false;
         } else {
            return centerVecData.crystal() == null && centerVecData.anchor() == null && centerVecData.pearl() == null ? isMeleeOrNoLane(comboTradeData.lane()) && isMeleeOrNoLane(centerVecData.lane()) : false;
         }
      } else {
         return false;
      }
   }

   private int getValue(Vec3d vec, double posBlockValue, double posBlockValue2) {
      return getSideRelativeToSpot(this.threatSpot, vec, posBlockValue, posBlockValue2);
   }

   static double getVisibleCellHeightBelow(CombatMovementController.Env env, int posBlockValue, int z2, Vec3d vec, AmountConstTime.Spot spot) {
      return getVisibleCellHeight(env, posBlockValue, z2, vec, spot, -0.5, -1.5);
   }

   private PlayerInputOverride.Intent getStandStillIntent(AbsorptionPosData absorptionPosData, boolean posBlockFlag) {
      return this.applyLedgeSafety(absorptionPosData, buildIntent(false, 0.0, 0.0, posBlockFlag, false, false));
   }

   private boolean canApiVelocity(AbsorptionPosData absorptionPosData, double apiVelocityFactor, double apiVelocityFactor2, double apiVelocityLimit, boolean apiVelocityFlag) {
      if (!this.closeRangeMode && absorptionPosData.onGround() && apiVelocityLimit > (apiVelocityFlag ? 2.5 : 4.0)) {
         Vec3d velocity = absorptionPosData.velocity();
         if (velocity == null || !(Math.hypot(velocity.x, velocity.z) > (apiVelocityFlag ? 0.15 : 0.2))) {
            return false;
         } else if (this.worldEnv.hasPendingBlockAboveBody()) {
            return false;
         } else {
            Vec3d pos = absorptionPosData.pos();
            if (this.hasCeilingAbove(pos)) {
               return false;
            } else {
               int y = (int)Math.floor(pos.y + 0.001);
               double apiVelocityLimit2 = apiVelocityFlag ? -1.001 : -0.01;
               byte apiVelocityLimit3 = 3;

               for (int i = 1; i <= apiVelocityLimit3; i++) {
                  double apiVelocityValue = pos.x + apiVelocityFactor * i;
                  double hashRegistryValue = pos.z + apiVelocityFactor2 * i;
                  double groundHeight = findGroundHeight(this.worldEnv, apiVelocityValue, hashRegistryValue, pos.y);
                  if (!Double.isFinite(groundHeight)) {
                     return false;
                  }

                  double apiVelocityLimit4 = groundHeight - pos.y;
                  if (apiVelocityLimit4 > 0.01 || apiVelocityLimit4 < apiVelocityLimit2) {
                     return false;
                  }

                  if (apiVelocityLimit4 < -0.01) {
                     apiVelocityLimit3 = 5;
                  }

                  int x = (int)Math.floor(apiVelocityValue);
                  int z = (int)Math.floor(hashRegistryValue);
                  int y2 = (int)Math.floor(groundHeight + 0.001);
                  if (this.worldEnv.isSolidAt(x, y + 2, z)) {
                     return false;
                  }

                  if ((this.worldEnv.getHazardFlags(new BlockPos(x, y2, z)) & 1375) != 0) {
                     return false;
                  }

                  if (y2 != y && (this.worldEnv.getHazardFlags(new BlockPos(x, y, z)) & 1375) != 0) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void resetStuckTracking() {
      this.stuckSampleCount = 0;
      this.stuckCheckX = 0.0;
      this.stuckCheckZ = 0.0;
   }

   private void updateStuckDetection(long time, Vec3d vec, Vec3d vec2, double vecBound) {
      boolean vecFlag = this.activeDodge != null && time < this.dodgeHoldUntilTick;
      double vecValue = this.isStandingInFire(vec2) && !this.isStandingInFire(vec) ? Math.max(vecBound, 1.6) : vecBound;
      double hypot = Math.hypot(vec2.x - vec.x, vec2.z - vec.z);
      if (!vecFlag && !isIncompletePathMode(this.pathfinder.getRouteMode()) && hypot > vecValue + 0.3) {
         if (this.stuckSampleCount != 0 && Math.hypot(vec.x - this.stuckCheckX, vec.z - this.stuckCheckZ) < 0.05) {
            this.stuckSampleCount++;
            if (this.stuckSampleCount >= 10) {
               if (this.lastStuckRecoveryTick == Long.MIN_VALUE || time < this.lastStuckRecoveryTick || time - this.lastStuckRecoveryTick >= 20L) {
                  this.lastStuckRecoveryTick = time;
                  this.stuckSampleCount = 0;
                  this.stuckCount++;
                  this.stuckDetected = true;
                  this.arrivedLatched = false;
                  this.activeDodge = null;
                  this.dodgeHoldUntilTick = Long.MIN_VALUE;
                  this.sidestepUntilTick = Long.MIN_VALUE;
                  this.pathfinder.reset();
               }
            }
         } else {
            this.stuckCheckX = vec.x;
            this.stuckCheckZ = vec.z;
            this.stuckSampleCount = 1;
         }
      } else {
         this.resetStuckTracking();
      }
   }

   static boolean canWalkStraightToHeight(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, boolean posBlockFlag, double posBlockValue) {
      return canFlagForEnvVec3d(env, vec, vec2, posBlockFlag, posBlockValue, 3);
   }

   public boolean isInterceptCellActive() {
      return this.interceptCellActive;
   }

   public RoutedBreachingApi getPathfinder() {
      return this.pathfinder;
   }

   static boolean isDodgeAllowed(CenterVecData.StandKind standKind, boolean posBlockFlag) {
      return switch (standKind) {
         case LINE_UP, HOLD -> posBlockFlag;
         case UNDER_TARGET, BELOW_LAYER, FOLLOW -> true;
      };
   }

   static boolean canFlag(CombatMovementController.Env env, Vec3d vec, double posBlockFactor, double posBlockFactor2, double posBlockFactor3) {
      double posBlockValue = vec.x + posBlockFactor * posBlockFactor3;
      double hashRegistryValue = vec.z + posBlockFactor2 * posBlockFactor3;
      double groundHeight = findGroundHeight(env, posBlockValue, hashRegistryValue, vec.y);
      if (!Double.isFinite(groundHeight)) {
         return false;
      } else {
         double posBlockLimit = groundHeight - vec.y;
         return posBlockLimit > 0.601 && posBlockLimit <= 1.001 ? !env.hasPendingBlockUpdate((int)Math.floor(posBlockValue), (int)Math.floor(groundHeight - 0.001), (int)Math.floor(hashRegistryValue)) : false;
      }
   }

   static double getSafeCellHeight(CombatMovementController.Env env, int x2, int z2, double posBlockValue) {
      double groundY = getAmount(env, x2 + 0.5, z2 + 0.5, posBlockValue, 2);
      if (!Double.isFinite(groundY)) {
         return Double.NaN;
      } else {
         int y = (int)Math.floor(groundY + 0.001);
         if ((env.getHazardFlags(new BlockPos(x2, y, z2)) & 1407) != 0) {
            return Double.NaN;
         } else if ((env.getHazardFlags(new BlockPos(x2, y + 1, z2)) & 32) != 0) {
            return Double.NaN;
         } else {
            return !env.hasPendingBlockUpdate(x2, y - 1, z2) && !env.hasPendingBlockUpdate(x2, y, z2) && !env.hasPendingBlockUpdate(x2, y + 1, z2) ? groundY : Double.NaN;
         }
      }
   }

   private static double getVisibleCellHeight(CombatMovementController.Env env, int x2, int z2, Vec3d vec, AmountConstTime.Spot spot, double posBlockValue, double posBlockValue2) {
      int floor3 = (int)Math.floor(vec.y + 0.001);
      double groundY = getAmount(env, x2 + 0.5, z2 + 0.5, floor3 - 1.0, 0);
      if (!Double.isFinite(groundY)) {
         return Double.NaN;
      } else {
         double posBlockLimit = groundY - vec.y;
         if (!(posBlockLimit > posBlockValue + 0.001) && !(posBlockLimit < posBlockValue2 - 0.001)) {
            int y = (int)Math.floor(groundY + 0.001);
            if ((env.getHazardFlags(new BlockPos(x2, y, z2)) & 1407) != 0) {
               return Double.NaN;
            } else if (!env.hasPendingBlockUpdate(x2, y - 1, z2) && !env.hasPendingBlockUpdate(x2, y, z2) && !env.hasPendingBlockUpdate(x2, y + 1, z2)) {
               int posBlockCounter = 0;

               for (int i = 0; i < 4; i++) {
                  int collisionShapeValue = x2 + (i == 0 ? 1 : (i == 1 ? -1 : 0));
                  int z = z2 + (i == 2 ? 1 : (i == 3 ? -1 : 0));
                  if (!env.isSolidAt(collisionShapeValue, y, z) && !env.isSolidAt(collisionShapeValue, y + 1, z)) {
                     posBlockCounter++;
                  }
               }

               if (posBlockCounter < 2) {
                  return Double.NaN;
               } else {
                  double envOffset = x2 + 0.5;
                  double spotOffset = z2 + 0.5;
                  if (!EmptyLvl.canFlag2(envOffset, groundY + 1.62, spotOffset, vec.x, vec.y, vec.z)) {
                     return Double.NaN;
                  } else {
                     for (int j = 1; j <= 3; j++) {
                        double posBlockFactor = j / 4.0;
                        if (env.isSolidAt((int)Math.floor(envOffset + (vec.x - envOffset) * posBlockFactor), floor3, (int)Math.floor(spotOffset + (vec.z - spotOffset) * posBlockFactor))) {
                           return Double.NaN;
                        }
                     }

                     if (spot != null) {
                        double posBlockFactor2 = envOffset - spot.getX();
                        double posBlockFactor3 = groundY - spot.getY();
                        double posBlockFactor4 = spotOffset - spot.getZ();
                        if (HeightDeltaMath.getDistanceWeightedReachFraction(groundY, Math.sqrt(posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3 + posBlockFactor4 * posBlockFactor4), spot.getAmount()) >= 0.25) {
                           return Double.NaN;
                        }
                     }

                     return groundY;
                  }
               }
            } else {
               return Double.NaN;
            }
         } else {
            return Double.NaN;
         }
      }
   }

   static double[] computeTargetLeadOffset(Vec3d vec, PreferAnchorData preferAnchorData, Vec3d vec2, double meleeKindFactor, double groundY) {
      if (vec != null && preferAnchorData != null && vec2 != null && preferAnchorData.anchor() != null) {
         if (preferAnchorData.kind() != PreferAnchorData.Kind.MELEE && preferAnchorData.kind() != PreferAnchorData.Kind.MELEE_LOW) {
            return null;
         } else {
            Vec3d anchor = preferAnchorData.anchor();
            if (isFiniteVec(anchor) && isFiniteVec(vec2) && isFiniteVec(vec) && Double.isFinite(meleeKindFactor) && Double.isFinite(groundY)) {
               if (Math.hypot(anchor.x - vec2.x, anchor.z - vec2.z) > 3.0) {
                  return null;
               } else {
                  double hypot = Math.hypot(meleeKindFactor, groundY);
                  if (!(hypot >= 0.08)) {
                     return null;
                  } else {
                     double deltaX = anchor.x - vec.x;
                     double deltaZ = anchor.z - vec.z;
                     double hypot2 = Math.hypot(deltaX, deltaZ);
                     if (!(hypot2 > 1.0E-6)) {
                        return null;
                     } else {
                        double meleeKindValue = (meleeKindFactor * deltaX + groundY * deltaZ) / (hypot * hypot2);
                        if (meleeKindValue < 0.4 && hypot2 <= 3.2) {
                           return null;
                        } else {
                           double meleeKindFactor3 = Math.max(3.0, Math.min(12.0, hypot2 / 0.28)) * Math.min(1.0, 1.0 + meleeKindValue);
                           double max2 = Math.max(0.0, ((anchor.x - vec2.x) * meleeKindFactor + (anchor.z - vec2.z) * groundY) / hypot);
                           double meleeKindFactor4 = hypot * meleeKindFactor3 - max2;
                           return !(meleeKindFactor4 > 0.001) ? null : new double[]{meleeKindFactor / hypot * meleeKindFactor4, groundY / hypot * meleeKindFactor4};
                        }
                     }
                  }
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   static boolean isBelowAnchor(Vec3d vec, PreferAnchorData preferAnchorData) {
      return vec != null && preferAnchorData != null && preferAnchorData.anchor() != null && Double.isFinite(vec.y) && Double.isFinite(preferAnchorData.anchor().y)
         ? vec.y - preferAnchorData.anchor().y <= -0.499
         : false;
   }

   private CombatMovementController.Edge detectLedgeAhead(AbsorptionPosData absorptionPosData, double velocityPosValue, double velocityPosValue2, boolean velocityPosFlag) {
      Vec3d pos = absorptionPosData.pos();
      Vec3d velocity = absorptionPosData.velocity();
      double velocityPosFactor = velocity != null && Double.isFinite(velocity.x) ? velocity.x : 0.0;
      double deltaZ = velocity != null && Double.isFinite(velocity.z) ? velocity.z : 0.0;
      if (!velocityPosFlag && Math.hypot(velocityPosFactor, deltaZ) < 1.0E-4) {
         return new CombatMovementController.Edge(false, false, false, 0.0, 0.0);
      } else {
         if (velocityPosFlag) {
            double velocityPosValue3 = velocityPosValue * 0.35;
            double moveScaled = velocityPosValue2 * 0.35;
            if (this.isCellUnsafe(pos, pos.x + velocityPosValue3, pos.z + moveScaled)) {
               return this.buildLedgeEdge(pos, velocityPosValue3, moveScaled, false);
            }
         }

         for (int i = 1; i <= 4; i++) {
            double posBlockValue = velocityPosFactor * i + velocityPosValue * 0.25 * i;
            double spotValue = deltaZ * i + velocityPosValue2 * 0.25 * i;
            if (this.isCellUnsafe(pos, pos.x + posBlockValue, pos.z + spotValue)) {
               return this.buildLedgeEdge(pos, posBlockValue, spotValue, true);
            }
         }

         return new CombatMovementController.Edge(false, false, false, 0.0, 0.0);
      }
   }

   static double[] searchMeleeCell(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, Vec3d vec3, AmountConstTime.Spot spot, int posBlockBound, boolean posBlockFlag) {
      if (env != null && vec != null && vec2 != null && vec3 != null && isFiniteVec(vec) && isFiniteVec(vec2) && isFiniteVec(vec3)) {
         HashSet hashSet = new HashSet();
         double posBlockValue = Double.POSITIVE_INFINITY;
         double[] posBlockValues = null;

         for (int i = 0; i < 24; i++) {
            double angle = (Math.PI * 2) * i / 24.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (double posBlockFactor : TARGET_RING_RADII) {
               int x2 = (int)Math.floor(vec2.x + cos * posBlockFactor);
               int z2 = (int)Math.floor(vec2.z + sin * posBlockFactor);
               if (hashSet.add((long)x2 << 32 ^ z2 & 4294967295L)) {
                  double x = x2 + 0.5;
                  double z = z2 + 0.5;
                  double hypot = Math.hypot(x - vec2.x, z - vec2.z);
                  if (!(hypot < 0.999) && !(hypot > 3.201)) {
                     double posBlockLimit = Math.hypot(x - vec3.x, z - vec3.z) + 0.5 * Math.hypot(x - vec.x, z - vec.z);
                     if (!(posBlockLimit >= posBlockValue)) {
                        double y = getMeleeCellHeight(env, x2, z2, vec2, spot);
                        if (Double.isFinite(y)) {
                           double posBlockLimit2 = posBlockLimit + 1.0 * Math.max(0.0, y - vec2.y);
                           if (!(posBlockLimit2 >= posBlockValue)) {
                              Vec3d vec3d = new Vec3d(x, y, z);
                              if (posBlockFlag ? !(y > vec.y + 1.2 + 0.001) : canWalkStraight(env, vec, vec3d, true, 8.0, Math.max(0, posBlockBound))) {
                                 posBlockValue = posBlockLimit2;
                                 posBlockValues = new double[]{x, y, z};
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return posBlockValues;
      } else {
         return null;
      }
   }

   private PlayerInputOverride.Intent getGoalMovementIntent(
      long time,
      AbsorptionPosData absorptionPosData,
      StandGoalPlanner.Goal goal,
      Vec3d vec,
      CrystalDodgePlanner.Dodge maxData,
      ComboTradeData comboTradeData,
      boolean stanceFeetFlag,
      boolean apiPosFlag,
      boolean stanceFeetFlag3
   ) {
      boolean usingItem = absorptionPosData.usingItem();
      CrystalDodgePlanner.Dodge maxTime = this.resolveActiveDodge(time, maxData);
      if (maxTime != null) {
         this.critJumpActive = false;
         return this.getDodgeIntent(absorptionPosData, maxTime, true);
      } else {
         if (this.critJumpActive) {
            if ((time <= this.critJumpStartTick + 1L || !absorptionPosData.onGround()) && time - this.critJumpStartTick <= 14L) {
               return PlayerInputOverride.Intent.idle();
            }

            this.critJumpActive = false;
         }

         boolean usingItem4 = !usingItem;
         boolean touchingWater = this.worldEnv.isTouchingWater();
         if (goal != null && goal.feet() != null && isFiniteVec(goal.feet())) {
            DanceChaseType danceChaseType = comboTradeData != null && comboTradeData.stance() != null ? comboTradeData.stance() : (goal.stance() != null ? goal.stance() : DanceChaseType.STILL);
            if (this.canStartCritJump(time, absorptionPosData, vec, comboTradeData, danceChaseType)) {
               this.critJumpStartTick = time;
               this.critJumpActive = true;
               return PlayerInputOverride.Intent.idle().withJump(PlayerInputOverride.Tri.ON);
            } else {
               double stanceFeetValue = 0.0;
               boolean stanceFeetFlag5 = false;
               boolean stanceFeetFlag6 = false;
               boolean stanceFeetFlag7 = false;
               double maskedBound;
               switch (danceChaseType) {
                  case DANCE:
                     maskedBound = 0.6;
                     stanceFeetValue = 0.9;
                     stanceFeetFlag5 = true;
                     stanceFeetFlag6 = usingItem4 && !isInsideEnclosedHole(absorptionPosData);
                     break;
                  case HOLD:
                     maskedBound = 0.6;
                     break;
                  case CHASE:
                     maskedBound = 0.6;
                     stanceFeetFlag7 = true;
                     break;
                  case FLEE:
                     maskedBound = 0.6;
                     stanceFeetFlag7 = true;
                     stanceFeetValue = 0.9;
                     stanceFeetFlag5 = true;
                     break;
                  case COVER:
                     if ("hole".equals(goal.why())) {
                        maskedBound = 0.2;
                     } else {
                        maskedBound = 0.6;
                        stanceFeetValue = 0.9;
                        stanceFeetFlag5 = true;
                     }
                     break;
                  default:
                     maskedBound = 0.2;
               }

               CombatMovementController.Approach amountMasked = this.computeApproach(time, absorptionPosData, goal.feet(), maskedBound, maskedBound + stanceFeetValue + 0.4, PreferAnchorData.getCell(goal.feet()));
               if (amountMasked.arrived()) {
                  PlayerInputOverride.Intent timeFlag = this.getFireEscapeIntent(time, absorptionPosData, goal.feet(), comboTradeData);
                  if (timeFlag != null) {
                     return timeFlag;
                  } else if (stanceFeetFlag5) {
                     CombatMovementController.DanceStep danceStep = this.computeStrafeStepNoPause(time, absorptionPosData, goal.feet(), vec, stanceFeetValue, stanceFeetFlag6);
                     boolean posBlockFlag = usingItem4 && (danceStep.hop() || stanceFeetFlag || touchingWater);
                     return this.applyLedgeSafety(absorptionPosData, buildIntent(danceStep.move(), danceStep.dx(), danceStep.dz(), posBlockFlag, false, false));
                  } else {
                     return this.getStandStillIntent(absorptionPosData, usingItem4 && (stanceFeetFlag || touchingWater));
                  }
               } else if (!amountMasked.move()) {
                  return this.getStandStillIntent(absorptionPosData, usingItem4 && (stanceFeetFlag || touchingWater));
               } else {
                  boolean stanceFeetFlag9 = usingItem4
                     && (
                        this.shouldJumpForStep(absorptionPosData, amountMasked.dx(), amountMasked.dz(), apiPosFlag)
                           || touchingWater
                           || stanceFeetFlag3
                           || stanceFeetFlag
                           || amountMasked.hop()
                           || stanceFeetFlag7 && this.canSprintJumpAhead(absorptionPosData, amountMasked.dx(), amountMasked.dz(), amountMasked.dist())
                     );
                  return this.applyLedgeSafety(absorptionPosData, buildIntent(true, amountMasked.dx(), amountMasked.dz(), stanceFeetFlag9, (amountMasked.sprint() || amountMasked.dist() > 2.0) && !usingItem, false));
               }
            }
         } else {
            PlayerInputOverride.Intent fireEscapeIntent = this.getFireEscapeIntent(time, absorptionPosData, null, comboTradeData);
            return fireEscapeIntent != null ? fireEscapeIntent : this.getStandStillIntent(absorptionPosData, usingItem4 && (stanceFeetFlag || touchingWater));
         }
      }
   }

   public void applyFireImmunity(PlayerCombatState immuneFireData) {
      this.pathfinder.setImmuneFireData(immuneFireData == null ? PlayerCombatState.INSTANCE : immuneFireData);
   }

   private static boolean isInsideEnclosedHole(AbsorptionPosData absorptionPosData) {
      BlockStateUtilsDirections.Hole hole = absorptionPosData.hole();
      return hole != null && hole.type() != BlockStateUtilsDirections.Type.NONE && hole.openSides() == 0;
   }

   int getLeadTargetCount() {
      return this.leadTargetCount;
   }

   private PlayerInputOverride.Intent getComboStandIntent(
      long apiPosValue,
      AbsorptionPosData absorptionPosData,
      CenterVecData.Stand stand,
      Vec3d vec,
      CrystalDodgePlanner.Dodge maxData,
      ComboTradeData comboTradeData,
      boolean apiPosFlag,
      boolean stanceFeetFlag,
      boolean juggleLaunchFlag,
      boolean horizontalCollision,
      boolean apiPosFlag5
   ) {
      this.critJumpActive = false;
      CenterVecData combo = comboTradeData.combo();
      CenterVecData.StandKind standKind = stand.kind() == null ? CenterVecData.StandKind.HOLD : stand.kind();
      Vec3d vec3d = stand.feet() != null && isFiniteVec(stand.feet()) ? stand.feet() : absorptionPosData.pos();
      double vecBound = stand.tolerance() > 0.0 && Double.isFinite(stand.tolerance()) ? stand.tolerance() : 0.6;
      boolean noJump = stand.noJump();
      boolean usingItem = absorptionPosData.usingItem();
      boolean touchingWater = this.worldEnv.isTouchingWater();
      this.updateLaunchPhase(apiPosValue, stand.wtapTick(), combo.state(), juggleLaunchFlag);
      if (isDodgeAllowed(standKind, comboTradeData.dodge()) && !apiPosFlag) {
         CrystalDodgePlanner.Dodge maxTime = this.resolveActiveDodge(apiPosValue, maxData);
         if (maxTime != null) {
            return this.getDodgeIntent(absorptionPosData, maxTime, !noJump);
         }
      }

      if (standKind == CenterVecData.StandKind.HOLD) {
         this.resetStuckTracking();
         PlayerInputOverride.Intent timeFlag = this.getFireEscapeIntent(apiPosValue, absorptionPosData, vec3d, comboTradeData);
         if (timeFlag == null
            && !absorptionPosData.onGround()
            && hasNoSpecialComboAction(combo, comboTradeData.lane())
            && vec != null
            && isFiniteVec(vec)
            && isHigherThanAnchor(this.worldEnv, absorptionPosData.pos(), vec, absorptionPosData.pos(), false)) {
            Vec3d apiTime = this.findRepositionCell(apiPosValue, absorptionPosData.pos(), new PreferAnchorData(vec, PreferAnchorData.Kind.MELEE_LOW, absorptionPosData.pos()), vec, absorptionPosData.pos(), false);
            if (apiTime != null) {
               this.repositionCount++;
               return this.applyLedgeSafety(
                  absorptionPosData,
                  buildIntent(
                     true,
                     apiTime.x - absorptionPosData.pos().x,
                     apiTime.z - absorptionPosData.pos().z,
                     false,
                     this.worldEnv.isSprintingOutsideWater() && isDirectionInFrontOfYaw(this.worldEnv.getYaw(), apiTime.x - absorptionPosData.pos().x, apiTime.z - absorptionPosData.pos().z),
                     false
                  )
               );
            }
         }

         if (timeFlag == null) {
            this.pendingMoveStatus = CombatMovementController.MoveStatus.HOLD;
         }

         return timeFlag != null ? timeFlag : PlayerInputOverride.Intent.idle();
      } else {
         PreferAnchorData attackGoal = getAttackGoal(stand, vec3d);
         if (standKind == CenterVecData.StandKind.FOLLOW && isPureMeleeEngagement(vec, attackGoal, comboTradeData == null ? null : comboTradeData.combo(), comboTradeData, absorptionPosData.usingItem())) {
            boolean anchor = isBelowAnchor(vec3d, attackGoal);
            boolean nearAnchor = isNearAnchor(vec3d, attackGoal);
            double[] meleeKind = computeTargetLeadOffset(absorptionPosData.pos(), attackGoal, vec, this.targetVelX, this.targetVelZ);
            if (meleeKind != null) {
               Vec3d anchor3 = attackGoal.anchor();
               vec3d = new Vec3d(vec3d.x + meleeKind[0], vec3d.y, vec3d.z + meleeKind[1]);
               attackGoal = new PreferAnchorData(new Vec3d(anchor3.x + meleeKind[0], anchor3.y, anchor3.z + meleeKind[1]), attackGoal.kind(), vec3d);
               this.leadingTargetActive = true;
               this.leadTargetCount++;
            }

            if (!nearAnchor && (this.leadingTargetActive || !anchor)) {
               Vec3d repositionCell = this.findRepositionCell(apiPosValue, absorptionPosData.pos(), attackGoal, vec, vec3d, absorptionPosData.onGround());
               if (repositionCell != null) {
                  vec3d = repositionCell;
                  attackGoal = new PreferAnchorData(attackGoal.anchor(), attackGoal.kind(), repositionCell);
                  this.repositionCellActive = true;
                  this.repositionCount++;
               }
            }
         } else if (standKind == CenterVecData.StandKind.FOLLOW
            && !absorptionPosData.onGround()
            && !this.worldEnv.isTouchingWater()
            && !absorptionPosData.usingItem()
            && isMeleeGoal(attackGoal)
            && hasNoSpecialComboAction(combo, comboTradeData.lane())
            && vec != null
            && isFiniteVec(vec)
            && isHigherThanAnchor(this.worldEnv, absorptionPosData.pos(), vec, absorptionPosData.pos(), false)) {
            Vec3d repositionCell3 = this.findRepositionCell(apiPosValue, absorptionPosData.pos(), new PreferAnchorData(vec, PreferAnchorData.Kind.MELEE_LOW, absorptionPosData.pos()), vec, absorptionPosData.pos(), false);
            boolean swimmingSprinting = this.worldEnv.isSprintingOutsideWater();
            if (repositionCell3 != null && (!swimmingSprinting || isDirectionInFrontOfYaw(this.worldEnv.getYaw(), repositionCell3.x - absorptionPosData.pos().x, repositionCell3.z - absorptionPosData.pos().z))) {
               this.repositionCount++;
               return this.applyLedgeSafety(absorptionPosData, buildIntent(true, repositionCell3.x - absorptionPosData.pos().x, repositionCell3.z - absorptionPosData.pos().z, false, swimmingSprinting, false));
            }
         } else if (standKind == CenterVecData.StandKind.FOLLOW && isMeleeGoal(attackGoal)) {
            this.lastRepositionSearchTick = Long.MIN_VALUE;
         } else {
            this.clearRepositionCache();
         }

         boolean apiPosFlag6 = false;
         boolean apiPosFlag7 = this.worldEnv.isTouchingWater() && !absorptionPosData.onGround();
         if (standKind == CenterVecData.StandKind.FOLLOW && vec != null && isFiniteVec(vec) && isMeleeGoal(attackGoal) && (apiPosFlag7 || isInWater(this.worldEnv, vec3d))) {
            Vec3d timeAmounts = this.findMeleeCell(apiPosValue, absorptionPosData.pos(), vec, vec3d, apiPosFlag7);
            if (timeAmounts != null) {
               vec3d = timeAmounts;
               attackGoal = PreferAnchorData.getCell(timeAmounts);
               this.meleeCellActive = true;
               this.meleeCellCount++;
               apiPosFlag6 = true;
            }

            boolean floored = isInWater(this.worldEnv, vec);
            Vec3d vec3d2 = timeAmounts != null || !apiPosFlag7 && !floored ? null : this.findInterceptCell(apiPosValue, absorptionPosData.pos(), vec, apiPosFlag7);
            if (vec3d2 != null) {
               vec3d = vec3d2;
               attackGoal = PreferAnchorData.getCell(vec3d2);
               this.interceptCellActive = true;
               this.interceptCellCount++;
               apiPosFlag6 = true;
            } else if (timeAmounts != null) {
               this.clearInterceptCellCache();
            }

            this.waterApproachActive = apiPosFlag7 && (timeAmounts != null || vec3d2 != null || floored);
         } else {
            this.clearMeleeCellCache();
            this.clearInterceptCellCache();
         }

         this.updateStuckDetection(apiPosValue, absorptionPosData.pos(), vec3d, vecBound);
         if (standKind == CenterVecData.StandKind.LINE_UP || standKind == CenterVecData.StandKind.FOLLOW) {
            FinishingPressUtils.Phase nonePress = FinishingPressUtils.getPressPhase(apiPosValue, stand.wtapTick());
            if (nonePress == FinishingPressUtils.Phase.RELEASE) {
               this.pendingMoveStatus = CombatMovementController.MoveStatus.WTAP;
               return new PlayerInputOverride.Intent(false, 0.0, 0.0, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, PlayerInputOverride.Tri.OFF, true);
            }

            if (nonePress == FinishingPressUtils.Phase.PRESS && this.canPressWtap(apiPosValue)) {
               this.pendingMoveStatus = CombatMovementController.MoveStatus.WTAP;
               double[] strikeLaunch = getStrikeDirection(absorptionPosData.pos(), vec, combo, vec3d);
               PlayerInputOverride.Intent dirSprint = PlayerInputOverride.Intent.moveToward(strikeLaunch[0], strikeLaunch[1], true)
                  .withJump(PlayerInputOverride.Tri.OFF)
                  .withSneak(PlayerInputOverride.Tri.OFF)
                  .asUrgent();
               return this.applyLedgeSafety(absorptionPosData, usingItem ? dirSprint.withSprint(PlayerInputOverride.Tri.OFF) : dirSprint);
            }
         }

         switch (standKind) {
            case LINE_UP:
               CombatMovementController.Approach amountMasked = this.computeApproach(apiPosValue, absorptionPosData, vec3d, vecBound, vecBound + 0.4, attackGoal);
               if (!amountMasked.move()) {
                  PlayerInputOverride.Intent intent = amountMasked.arrived() ? this.getFireEscapeIntent(apiPosValue, absorptionPosData, vec3d, comboTradeData) : null;
                  return intent != null ? intent : this.getStandStillIntent(absorptionPosData, false);
               }

               return this.applyLedgeSafety(absorptionPosData, buildIntent(true, amountMasked.dx(), amountMasked.dz(), false, false, false));
            case UNDER_TARGET:
               boolean apiPosFlag8 = !noJump && !usingItem;
               CombatMovementController.Approach approach = this.computeApproach(apiPosValue, absorptionPosData, vec3d, vecBound, vecBound + 0.4 + 0.4, attackGoal);
               if (approach.arrived()) {
                  PlayerInputOverride.Intent fireEscapeIntent = this.getFireEscapeIntent(apiPosValue, absorptionPosData, vec3d, comboTradeData);
                  if (fireEscapeIntent != null) {
                     return fireEscapeIntent;
                  }

                  CombatMovementController.DanceStep danceStep = this.computeStrafeStepNoPause(apiPosValue, absorptionPosData, vec3d, vec, 0.4, false);
                  return this.applyLedgeSafety(absorptionPosData, buildIntent(danceStep.move(), danceStep.dx(), danceStep.dz(), apiPosFlag8 && (stanceFeetFlag || touchingWater), false, false));
               } else {
                  if (!approach.move()) {
                     return this.getStandStillIntent(absorptionPosData, apiPosFlag8 && (stanceFeetFlag || touchingWater));
                  }

                  boolean posBlockFlag = apiPosFlag8 && (this.shouldJumpForStep(absorptionPosData, approach.dx(), approach.dz(), horizontalCollision) || touchingWater || apiPosFlag5 || stanceFeetFlag || approach.hop());
                  return this.applyLedgeSafety(absorptionPosData, buildIntent(true, approach.dx(), approach.dz(), posBlockFlag, (approach.sprint() || approach.dist() > 2.0) && !usingItem, false));
               }
            case BELOW_LAYER:
               CombatMovementController.Approach approach3 = this.computeApproach(apiPosValue, absorptionPosData, vec3d, vecBound, vecBound + 0.4, attackGoal);
               if (!approach3.move()) {
                  PlayerInputOverride.Intent intent2 = approach3.arrived() ? this.getFireEscapeIntent(apiPosValue, absorptionPosData, vec3d, comboTradeData) : null;
                  return intent2 != null ? intent2 : PlayerInputOverride.Intent.idle();
               }

               return this.applyLedgeSafety(absorptionPosData, buildIntent(true, approach3.dx(), approach3.dz(), false, (approach3.sprint() || approach3.dist() > 2.0) && !usingItem, false));
            case FOLLOW:
               if (this.waterApproachActive) {
                  return this.getDirectApproachIntent(absorptionPosData, vec3d, vecBound);
               } else {
                  boolean apiPosFlag10 = !apiPosFlag6 && isPureMeleeEngagement(vec, attackGoal, combo, comboTradeData, usingItem);
                  CombatMovementController.Approach approach4 = this.computeApproach(apiPosValue, absorptionPosData, vec3d, vecBound, vecBound + (apiPosFlag10 ? 0.7 : 0.0) + 0.4, attackGoal);
                  boolean apiPosFlag11 = !noJump && !usingItem;
                  if (!approach4.move()) {
                     PlayerInputOverride.Intent apiPos = approach4.arrived() ? this.getFireEscapeIntent(apiPosValue, absorptionPosData, vec3d, comboTradeData) : null;
                     if (apiPos != null) {
                        return apiPos;
                     }

                     if (approach4.arrived() && apiPosFlag10) {
                        CombatMovementController.DanceStep randomFlag = this.computeStrafeStep(apiPosValue, absorptionPosData, vec3d, vec, 0.7, false, 0.0);
                        return this.applyLedgeSafety(absorptionPosData, buildIntent(randomFlag.move(), randomFlag.dx(), randomFlag.dz(), apiPosFlag11 && (stanceFeetFlag || touchingWater), false, false));
                     }

                     return this.getStandStillIntent(absorptionPosData, apiPosFlag11 && (stanceFeetFlag || touchingWater));
                  }

                  boolean apiPosFlag12 = !usingItem && (this.shouldJumpForStep(absorptionPosData, approach4.dx(), approach4.dz(), horizontalCollision) || approach4.hop() || apiPosFlag5);
                  boolean apiPosFlag13 = apiPosFlag11 && (touchingWater || stanceFeetFlag || this.canApiVelocity(absorptionPosData, approach4.dx(), approach4.dz(), approach4.dist(), this.leadingTargetActive));
                  boolean apiPosFlag14 = (this.leadingTargetActive || stand.sprint() || approach4.sprint() || approach4.dist() > 2.0) && !usingItem;
                  return this.applyLedgeSafety(absorptionPosData, buildIntent(true, approach4.dx(), approach4.dz(), apiPosFlag12 || apiPosFlag13, apiPosFlag14, false));
               }
            default:
               return PlayerInputOverride.Intent.idle();
         }
      }
   }

   static boolean isWithinAnchorRange(PreferAnchorData preferAnchorData, Vec3d vec, Vec3d vec2, double anchorBound) {
      double kind = getAnchorMaxRange(preferAnchorData);
      if (Double.isFinite(kind) && preferAnchorData.anchor() != null && isFiniteVec(preferAnchorData.anchor()) && vec != null && isFiniteVec(vec)) {
         Vec3d anchor = preferAnchorData.anchor();
         if (vec2 != null && isFiniteVec(vec2) && Double.isFinite(anchorBound)) {
            kind = Math.max(kind, Math.hypot(vec2.x - anchor.x, vec2.z - anchor.z) + Math.max(0.0, anchorBound));
         }

         return Math.hypot(vec.x - anchor.x, vec.z - anchor.z) <= kind + 0.001;
      } else {
         return true;
      }
   }

   public boolean wasIntentActive() {
      return this.lastIntentActive;
   }

   static boolean isOutsideThreatDamage(double[] posBlockValues, AmountConstTime.Spot spot) {
      if (spot != null && posBlockValues != null && posBlockValues.length >= 3) {
         double posBlockFactor = posBlockValues[0] - spot.getX();
         double posBlockFactor2 = posBlockValues[1] - spot.getY();
         double posBlockFactor3 = posBlockValues[2] - spot.getZ();
         return HeightDeltaMath.getDistanceWeightedReachFraction(posBlockValues[1], Math.sqrt(posBlockFactor * posBlockFactor + posBlockFactor2 * posBlockFactor2 + posBlockFactor3 * posBlockFactor3), spot.getAmount()) < 0.25;
      } else {
         return true;
      }
   }

   private static double[] getStrikeDirection(Vec3d vec, Vec3d vec2, CenterVecData centerVecData, Vec3d vec3) {
      if (vec2 != null && isFiniteVec(vec2)) {
         double[] strikeLaunch = normalizeXZ(vec2.x - vec.x, vec2.z - vec.z);
         if (strikeLaunch != null) {
            return strikeLaunch;
         }
      }

      if (centerVecData != null && centerVecData.strike() != null && Float.isFinite(centerVecData.strike().launchYaw())) {
         double angle = Math.toRadians(centerVecData.strike().launchYaw());
         return new double[]{-Math.sin(angle), Math.cos(angle)};
      } else {
         double[] strikeLaunchValues = normalizeXZ(vec3.x - vec.x, vec3.z - vec.z);
         return strikeLaunchValues != null ? strikeLaunchValues : new double[]{0.0, 0.0};
      }
   }

   private CombatMovementController.DanceStep computeStrafeStepNoPause(long posBlockValue, AbsorptionPosData absorptionPosData, Vec3d vec, Vec3d vec2, double stanceFeetValue, boolean posBlockFlag) {
      return this.computeStrafeStep(posBlockValue, absorptionPosData, vec, vec2, stanceFeetValue, posBlockFlag, 0.0);
   }

   static boolean isMeleeCellStillValid(CombatMovementController.Env env, double[] posBlockValues, Vec3d vec, Vec3d vec2, AmountConstTime.Spot spot, boolean posBlockFlag) {
      if (posBlockValues != null && posBlockValues.length >= 3 && vec != null && vec2 != null) {
         double hypot = Math.hypot(posBlockValues[0] - vec2.x, posBlockValues[2] - vec2.z);
         if (!(hypot < 0.7) && !(hypot > 3.5)) {
            if (posBlockFlag && posBlockValues[1] > vec.y + 1.2 + 0.001) {
               return false;
            } else {
               double posBlockValue = getMeleeCellHeight(env, (int)Math.floor(posBlockValues[0]), (int)Math.floor(posBlockValues[2]), vec2, spot);
               return Double.isFinite(posBlockValue) && Math.abs(posBlockValue - posBlockValues[1]) < 1.0E-6;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void clearInterceptCellCache() {
      this.cachedInterceptCell = null;
      this.lastInterceptSearchTick = Long.MIN_VALUE;
   }

   private void markBlockedCellAhead(long vecHashValue, Vec3d vec) {
      int[] vecFlag = this.pathfinder.getNextNodeCoords();
      if (vecFlag != null && vecFlag.length >= 3) {
         this.pathfinder.markCellBlocked(vecFlag[0], vecFlag[1], vecFlag[2], vecHashValue);
      } else {
         double[] vecHashValues = normalizeXZ(this.lastMoveDirX, this.lastMoveDirZ);
         if (vecHashValues != null) {
            BlockPos floored = BlockPos.ofFloored(vec.x + vecHashValues[0] * 0.8, vec.y + 0.001, vec.z + vecHashValues[1] * 0.8);
            this.pathfinder.markCellBlocked(floored.getX(), floored.getY(), floored.getZ(), vecHashValue);
         }
      }
   }

   public void onKnockbackReceived(Vec3d vec, boolean posBlockFlag) {
      this.jumpOnLandingPending = false;
      if (!posBlockFlag && vec != null && Math.hypot(vec.x, vec.z) >= 0.3) {
         this.knockbackPending = true;
         this.knockbackX = vec.x;
         this.knockbackZ = vec.z;
      }
   }

   private Vec3d findInterceptCell(long time, Vec3d vec, Vec3d vec2, boolean apiFlag) {
      boolean apiFlag2 = this.lastInterceptSearchTick == Long.MIN_VALUE || time < this.lastInterceptSearchTick || time - this.lastInterceptSearchTick >= 10L;
      double[] amounts = this.cachedInterceptCell;
      if (amounts != null && !isInterceptCellStillValid(this.worldEnv, amounts, vec, apiFlag)) {
         amounts = null;
         apiFlag2 = true;
      }

      if (apiFlag2) {
         this.lastInterceptSearchTick = time;
         double[] apiValues = searchInterceptCell(this.worldEnv, vec, vec2, this.targetVelX, this.targetVelZ, this.pathfinder.getMaxDrop(), apiFlag);
         if (apiValues != null) {
            amounts = apiValues;
         }
      }

      if (amounts != null && !isOutsideThreatDamage(amounts, this.threatSpot)) {
         amounts = null;
      }

      this.cachedInterceptCell = amounts;
      return amounts == null ? null : new Vec3d(amounts[0], amounts[1], amounts[2]);
   }

   public void resetState() {
      this.walkerAdvisor.updateFlagWalkerSurfaceActive();
      this.pathfinder.reset();
      this.pathCommand = null;
      this.steeringThisTick = false;
      this.steeredLastTick = false;
      this.edgeGuardUsedThisTick = false;
      this.lastGoalDistance = Double.NaN;
      this.goalDistance = Double.NaN;
      this.sidestepUntilTick = Long.MIN_VALUE;
      this.sidestepDirX = 0.0;
      this.sidestepDirZ = 0.0;
      this.lastMoveDirX = 0.0;
      this.lastMoveDirZ = 0.0;
      this.lastIntentActive = false;
      this.lastIntentDirX = 0.0;
      this.lastIntentDirZ = 0.0;
      this.latchedGoal = null;
      this.arrivedLatched = false;
      this.strafeDirectionSign = 1;
      this.nextStrafeRollTick = Long.MIN_VALUE;
      this.strafePauseActive = false;
      this.strafeHopRolled = false;
      this.walkerJumpAdvised = false;
      this.pearlLandingSeenTick = Long.MIN_VALUE;
      this.lastSetbackAgeMs = Long.MAX_VALUE;
      this.pearlSetbackExpected = false;
      this.pearlThrownTick = Long.MIN_VALUE;
      this.lastPearlCount = -1;
      this.jumpOnLandingPending = false;
      this.knockbackPending = false;
      this.groundedTicks = 0;
      this.activeDodge = null;
      this.dodgeHoldUntilTick = Long.MIN_VALUE;
      this.launchPhaseTick = Long.MIN_VALUE;
      this.launchPhaseActive = false;
      this.previousSwordCharge = Float.NaN;
      this.critJumpStartTick = Long.MIN_VALUE;
      this.critJumpActive = false;
      this.fireEscapeActive = false;
      this.lastFireCheckTick = Long.MIN_VALUE;
      this.fireEscapeLastSampleTick = Long.MIN_VALUE;
      this.fireEscapeBlockedUntilTick = Long.MIN_VALUE;
      this.fireEscapeDirX = 0.0;
      this.fireEscapeDirZ = 0.0;
      this.pendingMoveStatus = null;
      this.lastMoveStatus = CombatMovementController.MoveStatus.IDLE;
      this.stuckDetected = false;
      this.resetStuckTracking();
      this.lastStuckRecoveryTick = Long.MIN_VALUE;
      this.lastTargetPos = null;
      this.targetBaseTick = Long.MIN_VALUE;
      this.lastTargetUpdateTick = Long.MIN_VALUE;
      this.targetVelX = 0.0;
      this.targetVelZ = 0.0;
      this.leadingTargetActive = false;
      this.repositionCellActive = false;
      this.threatSpot = null;
      this.clearRepositionCache();
      this.meleeCellActive = false;
      this.waterApproachActive = false;
      this.clearMeleeCellCache();
      this.interceptCellActive = false;
      this.clearInterceptCellCache();
   }

   public void setCloseRangeMode(boolean flag) {
      this.closeRangeMode = flag;
   }

   static double getAnchorMaxRange(PreferAnchorData preferAnchorData) {
      if (preferAnchorData != null && preferAnchorData.kind() != null) {
         return switch (preferAnchorData.kind()) {
            case MELEE, MELEE_LOW -> 3.2;
            case CRYSTAL -> 3.8;
            case CELL -> Double.POSITIVE_INFINITY;
         };
      } else {
         return Double.POSITIVE_INFINITY;
      }
   }

   public boolean isCritJumpActive() {
      return this.critJumpActive;
   }

   static boolean isDirectionInFrontOfYaw(float degrees, double posBlockValue, double deltaZ) {
      double[] posBlockValues = normalizeXZ(posBlockValue, deltaZ);
      if (posBlockValues != null && Float.isFinite(degrees)) {
         double angle = Math.toRadians(degrees);
         return -posBlockValues[0] * Math.sin(angle) + posBlockValues[1] * Math.cos(angle) >= Math.cos(Math.toRadians(60.0)) - 0.001;
      } else {
         return false;
      }
   }

   static double[] normalizeXZ(double posBlockValue, double deltaZ) {
      double hypot = Math.hypot(posBlockValue, deltaZ);
      return hypot > 1.0E-6 && Double.isFinite(hypot) ? new double[]{posBlockValue / hypot, deltaZ / hypot} : null;
   }

   public boolean isLeadingTarget() {
      return this.leadingTargetActive;
   }

   private PlayerInputOverride.Intent getDodgeIntent(AbsorptionPosData absorptionPosData, CrystalDodgePlanner.Dodge dodge, boolean dodgeDirFlag) { boolean var36_u = false;  double var34_u = (double) 0;  double var32_u = (double) 0; 
      if (dodge instanceof CrystalDodgePlanner.Dodge) {
         CrystalDodgePlanner.Dodge dodge2 = dodge;

         try {
            var32_u = dodge2.dirX();
         } catch (Throwable e) {
            throw new MatchException(e.toString(), e);
         }

         double var32_u2 = var32_u;
         dodge2 = dodge;

         try {
            var34_u = dodge2.dirZ();
         } catch (Throwable e2) {
            throw new MatchException(e2.toString(), e2);
         }

         double var34_u2 = var34_u;
         dodge2 = dodge;

         try {
            var36_u = dodge2.jump();
         } catch (Throwable e3) {
            throw new MatchException(e3.toString(), e3);
         }

         boolean var36_u2 = var36_u;
         dodge2 = dodge;

         try {
            dodge2.holdTicks();
         } catch (Throwable e4) {
            throw new MatchException(e4.toString(), e4);
         }

         dodge2 = dodge;

         try {
            dodge2.expected();
         } catch (Throwable e5) {
            throw new MatchException(e5.toString(), e5);
         }

         dodge2 = dodge;

         try {
            dodge2.stay();
         } catch (Throwable e6) {
            throw new MatchException(e6.toString(), e6);
         }

         this.pendingMoveStatus = CombatMovementController.MoveStatus.DODGE;
         double[] dodgeDirValues = normalizeXZ(var32_u2, var34_u2);
         boolean hasVar = dodgeDirValues != null;
         boolean dodgeDirFlag2 = var36_u2 && dodgeDirFlag && absorptionPosData.onGround() && !this.closeRangeMode;
         PlayerInputOverride.Intent intent = new PlayerInputOverride.Intent(
            hasVar,
            hasVar ? dodgeDirValues[0] : 0.0,
            hasVar ? dodgeDirValues[1] : 0.0,
            dodgeDirFlag2 ? PlayerInputOverride.Tri.ON : PlayerInputOverride.Tri.OFF,
            PlayerInputOverride.Tri.OFF,
            PlayerInputOverride.Tri.OFF,
            true
         );
         return this.applyLedgeSafety(absorptionPosData, intent);
      } else {
         return PlayerInputOverride.Intent.idle();
      }
   }

   static CenterVecData.Stand getActiveComboStand(ComboTradeData comboTradeData) {
      return comboTradeData != null && comboTradeData.combo() != null && !comboTradeData.comboYield() ? comboTradeData.combo().stand() : null;
   }

   private CombatMovementController.DanceStep computeStrafeStep(long randomLimit, AbsorptionPosData absorptionPosData, Vec3d vec, Vec3d vec2, double stanceFeetValue, boolean randomFlag, double randomLimit3) {
      Vec3d pos = absorptionPosData.pos();
      double[] randomValues = vec2 != null && isFiniteVec(vec2) ? normalizeXZ(vec2.x - pos.x, vec2.z - pos.z) : null;
      if (randomValues == null) {
         randomValues = normalizeXZ(vec.x - pos.x, vec.z - pos.z);
      }

      if (randomValues == null) {
         randomValues = new double[]{0.0, 1.0};
      }

      double randomFactor = -randomValues[1];
      double randomFactor2 = randomValues[0];
      double deltaX = pos.x - vec.x;
      double deltaZ = pos.z - vec.z;
      double spotValue = deltaX * randomFactor + deltaZ * randomFactor2;
      double randomValue = deltaX * randomValues[0] + deltaZ * randomValues[1];
      if (this.nextStrafeRollTick == Long.MIN_VALUE || randomLimit >= this.nextStrafeRollTick) {
         if (this.random.nextDouble() < randomLimit3) {
            this.strafePauseActive = true;
            this.nextStrafeRollTick = randomLimit + 3L + this.random.nextInt(4);
            this.strafeHopRolled = false;
         } else {
            this.strafePauseActive = false;
            this.nextStrafeRollTick = randomLimit + 5L + this.random.nextInt(8);
            int value = this.getValue(pos, randomFactor, randomFactor2);
            if (Math.abs(spotValue) > 0.5 * stanceFeetValue) {
               this.strafeDirectionSign = spotValue > 0.0 ? -1 : 1;
            } else if (value != 0) {
               this.strafeDirectionSign = value;
            } else {
               this.strafeDirectionSign = this.random.nextBoolean() ? 1 : -1;
            }

            this.strafeHopRolled = randomFlag && this.random.nextDouble() < 0.08 && !this.closeRangeMode;
         }
      }

      if (this.strafePauseActive) {
         return new CombatMovementController.DanceStep(false, 0.0, 0.0, false);
      } else {
         if (spotValue * this.strafeDirectionSign >= stanceFeetValue) {
            this.strafeDirectionSign = -this.strafeDirectionSign;
         }

         double randomFactor3 = stanceFeetValue > 0.0 ? Math.max(-1.0, Math.min(1.0, randomValue / stanceFeetValue)) * 0.6 : 0.0;
         double posBlockValue = randomFactor * this.strafeDirectionSign - randomValues[0] * randomFactor3;
         double randomValue3 = randomFactor2 * this.strafeDirectionSign - randomValues[1] * randomFactor3;
         boolean randomFlag2 = this.strafeHopRolled && randomFlag;
         this.strafeHopRolled = false;
         double[] normalizeXz = normalizeXZ(posBlockValue, randomValue3);
         return normalizeXz == null ? new CombatMovementController.DanceStep(false, 0.0, 0.0, randomFlag2) : new CombatMovementController.DanceStep(true, normalizeXz[0], normalizeXz[1], randomFlag2);
      }
   }

   private boolean canStartCritJump(long comboApiValue, AbsorptionPosData absorptionPosData, Vec3d vec, ComboTradeData comboTradeData, DanceChaseType danceChaseType) {
      if (this.closeRangeMode || comboTradeData == null || comboTradeData.lane() != SurroundKitType.MELEE || danceChaseType != DanceChaseType.HOLD || comboTradeData.mode() == DefendEngageType.COMBO) {
         return false;
      } else if (comboTradeData.combo() != null && !comboTradeData.comboYield()) {
         return false;
      } else if (vec == null || !isFiniteVec(vec) || !absorptionPosData.onGround() || absorptionPosData.usingItem() || this.worldEnv.isTouchingWater()) {
         return false;
      } else if (!(absorptionPosData.swordCharge() >= 0.9F) || !(this.worldEnv.getCooldownProgress() >= 0.9F)) {
         return false;
      } else if (this.critJumpStartTick != Long.MIN_VALUE && comboApiValue - this.critJumpStartTick < 12L) {
         return false;
      } else {
         Vec3d pos = absorptionPosData.pos();
         return Math.hypot(vec.x - pos.x, vec.z - pos.z) > 2.8 ? false : !this.hasCeilingAbove(pos);
      }
   }

   private static boolean isFiniteVec(Vec3d vec) {
      return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
   }

   public CombatMovementController(Random random) {
      this(random, AnchorRoutePlanner.create(null, null));
   }

   public CombatMovementController(Random random, AnchorRoutePlanner vecHash) {
      this(random, new CombatMovementController.McEnv(), vecHash);
   }

   static boolean canFlagForEnvVec3d(CombatMovementController.Env env, Vec3d vec, Vec3d vec2, boolean posBlockFlag, double posBlockValue, int flooredValue) {
      double floored = traceStraightWalk(env, vec, vec2, posBlockFlag, posBlockValue, flooredValue);
      if (Double.isNaN(floored)) {
         return false;
      } else {
         return Math.hypot(vec2.x - vec.x, vec2.z - vec.z) > posBlockValue ? true : Math.abs(floored - vec2.y) <= 0.601;
      }
   }

   static PreferAnchorData getAttackGoal(CenterVecData.Stand stand, Vec3d vec) {
      Vec3d vec3d = vec != null ? vec : (stand == null ? null : stand.feet());
      return stand != null && stand.attackAt() != null && stand.goal() != null && isFiniteVec(stand.attackAt())
         ? new PreferAnchorData(stand.attackAt(), stand.goal(), vec3d)
         : PreferAnchorData.getCell(vec3d);
   }

   private boolean isStandingInFire(Vec3d vec) {
      return vec != null && isFiniteVec(vec) && this.worldEnv.hasFireAt(BlockPos.ofFloored(vec.x, vec.y + 0.001, vec.z));
   }

   private record Approach(boolean move, double dx, double dz, boolean arrived, double dist, boolean hop, boolean sprint) {
   }

   private record DanceStep(boolean move, double dx, double dz, boolean hop) {
   }

   private record Edge(boolean projected, boolean resting, boolean unsafe, double ux, double uz) {
   }

   interface Env {
      default boolean hasFireAt(BlockPos pos) {
         return (this.getHazardFlags(pos) & 2) != 0;
      }

      int getDropDepth(BlockPos pos, int envValue);

      boolean isActionRunning();

      int getHazardFlags(BlockPos pos);

      default int getPearlLandingTicks() {
         return -1;
      }

      boolean isHorizontalCollision();

      default float getYaw() {
         return Float.NaN;
      }

      long getMsSinceSetback();

      List<BlockPos> findWalkPath(BlockPos pos, BlockPos pos2);

      boolean isSolidAt(int collisionShapeValue, int i, int z);

      default boolean isSprintingOutsideWater() {
         return false;
      }

      default boolean hasPendingBlockAboveBody() {
         return false;
      }

      double getSurfaceHeight(int collisionShapeValue, int i, int z);

      boolean isTouchingWater();

      default boolean hasPendingBlockBesideBody() {
         return false;
      }

      default boolean hasPendingBlockUpdate(int envValue, int y, int z2) {
         return false;
      }

      float getCooldownProgress();
   }

   public static enum MoveStatus {
      MASKED,
      SETBACK,
      SETTLE,
      DODGE,
      HOLD,
      WTAP,
      FIRE,
      ARRIVED,
      BRAKE,
      EDGE,
      STALL,
      MOVE,
      IDLE;
   }

   static final class McEnv implements CombatMovementController.Env {
      @Override
      public boolean hasPendingBlockAboveBody() {
         ClientPlayerEntity player = getPlayer();
         return player != null && FloorMathUtilsBlock.canFloorMin(getWorld(), player.getBoundingBox().offset(0.0, 1.0, 0.0).expand(0.01));
      }

      @Override
      public boolean hasPendingBlockUpdate(int x2, int y2, int z2) {
         return FloorMathUtilsBlock.canEmpty(getWorld(), new BlockPos(x2, y2, z2));
      }

      @Override
      public boolean hasFireAt(BlockPos pos) {
         return (BlockHazardFlags.getHazardFlags(getWorld(), pos) & 2) != 0;
      }

      private static World getWorld() {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance == null ? null : instance.world;
      }

      @Override
      public boolean isSprintingOutsideWater() {
         ClientPlayerEntity player = getPlayer();
         return player != null && player.isSprinting() && !player.isSwimming();
      }

      @Override
      public int getPearlLandingTicks() {
         ClientPlayerEntity player = getPlayer();
         World world = getWorld();
         if (player != null && world != null) {
            int expandEntities = -1;

            for (EnderPearlEntity enderPearlEntity : world.getEntitiesByClass(
               EnderPearlEntity.class, player.getBoundingBox().expand(96.0), enderPearlEntity2 -> !enderPearlEntity2.isRemoved() && enderPearlEntity2.getOwner() == player
            )) {
               FloorMathUtilsDirection.Result touchingOwner = FloorMathUtilsDirection.getTouchingOwner(enderPearlEntity, true, 80);
               int expandEntitiesValue = touchingOwner != null && touchingOwner.directionVecActive && touchingOwner.flightTicks >= 0 ? touchingOwner.flightTicks : 80;
               if (expandEntities < 0 || expandEntitiesValue < expandEntities) {
                  expandEntities = expandEntitiesValue;
               }
            }

            return expandEntities;
         } else {
            return -1;
         }
      }

      @Override
      public boolean isHorizontalCollision() {
         ClientPlayerEntity player = getPlayer();
         return player != null && player.horizontalCollision;
      }

      private static ClientPlayerEntity getPlayer() {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance == null ? null : instance.player;
      }

      @Override
      public int getDropDepth(BlockPos pos, int mcEnvValue) {
         return BlockHazardFlags.countOpenBlocksBelow(getWorld(), pos, mcEnvValue);
      }

      @Override
      public boolean isTouchingWater() {
         ClientPlayerEntity player = getPlayer();
         return player != null && player.isTouchingWater();
      }

      @Override
      public boolean hasPendingBlockBesideBody() {
         ClientPlayerEntity player = getPlayer();
         return player != null && FloorMathUtilsBlock.canFloorMin(getWorld(), player.getBoundingBox().expand(0.1, 0.0, 0.1));
      }

      @Override
      public float getCooldownProgress() {
         ClientPlayerEntity player = getPlayer();
         return player == null ? Float.NaN : player.getAttackCooldownProgress(0.5F);
      }

      @Override
      public int getHazardFlags(BlockPos pos) {
         return BlockHazardFlags.getExtendedHazardFlags(getWorld(), pos);
      }

      @Override
      public boolean isActionRunning() {
         return ExtUtils.isActionFlag2();
      }

      @Override
      public double getSurfaceHeight(int x2, int y2, int z2) {
         World world = getWorld();
         if (world == null) {
            return Double.NaN;
         } else {
            BlockPos blockPos = new BlockPos(x2, y2, z2);
            BlockState blockState = world.getBlockState(blockPos);
            VoxelShape collisionShape = blockState.getCollisionShape(world, blockPos);
            return collisionShape.isEmpty() ? Double.NaN : y2 + collisionShape.getMax(Axis.Y);
         }
      }

      @Override
      public List<BlockPos> findWalkPath(BlockPos pos, BlockPos pos2) {
         World world = getWorld();
         return world != null && pos != null && pos2 != null ? PosBlockUtilsWorld.getItems(world, pos, pos2, 3) : null;
      }

      @Override
      public boolean isSolidAt(int x2, int y2, int z2) {
         World world = getWorld();
         if (world == null) {
            return false;
         } else {
            BlockPos blockPos = new BlockPos(x2, y2, z2);
            return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();
         }
      }

      @Override
      public long getMsSinceSetback() {
         long mcEnvValue = ConnectionTimeUtils.getTime();
         return mcEnvValue < 0L ? Long.MAX_VALUE : ConnectionTimeUtils.getTimeTimeMillis() - mcEnvValue;
      }

      @Override
      public float getYaw() {
         ClientPlayerEntity player = getPlayer();
         if (player == null) {
            return Float.NaN;
         } else {
            return MathClampUtils.isClampSettled() ? MathClampUtils.getYaw() : player.getYaw();
         }
      }
   }
}
