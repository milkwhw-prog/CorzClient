package com.corz.client.utils;

import com.corz.client.utils.AnchorBreaker.BreakStatus;
import com.corz.client.utils.HunterPather.Grid;
import com.corz.client.utils.HunterPather.Move;
import com.corz.client.utils.HunterPather.Options;
import com.corz.client.utils.HunterPather.Result;
import com.corz.client.utils.HunterPather.Search;
import com.corz.client.utils.HunterPather.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BlockStateUtilsFluid;
import com.corz.client.utils.AnchorBreaker;
import com.corz.client.utils.HunterPather;
import com.corz.client.utils.HunterGrid;
import com.corz.client.utils.HunterBody;
import com.corz.client.utils.McCells;
import com.corz.client.utils.AirPlan;
import com.corz.client.utils.Cells;
import com.corz.client.utils.ApproachPlan;
import com.corz.client.utils.StandFinder;

public final class DigMover {
   private int pathStepIndex;
   private boolean pathReachesGoal;
   private int breakAttemptCount;
   private long stepStartTick;
   private boolean lastPathEmpty;
   private final HunterGrid hunterGrid;
   private Vec3d lastProgressPos;
   private Search search;
   private BlockPos digTarget;
   private BlockPos searchGoal;
   private boolean surfacingForAir;
   private List<Step> currentPath;
   private BlockPos pendingDigBlock;
   private final AnchorBreaker anchorBreaker;
   private List<BlockPos> remainingFeetCache;
   private String currentTaskKey;
   private BlockPos searchStart;
   private long lastSearchTick;
   private int blocksDugCount;
   private int collisionStuckTicks;
   private static final double[] FLOOR_PROBE_OFFSETS = new double[]{-0.3, 0.3};
   private long currentTick;
   private int noProgressTicks;
   private int failureCount;
   private final HunterBody hunterBody;
   private final McCells mcCells;
   private double closestDistance;
   private long lastTickSeen;
   private final MinecraftClient client = MinecraftClient.getInstance();
   private String lastFailureReason;

   private double getDistanceToCell(ClientPlayerEntity player, BlockPos pos) {
      return this.hunterBody.getHorizontalDistance(Vec3d.ofBottomCenter(pos)) + 0.5 * Math.abs(player.getY() - pos.getY());
   }

   public int getBlocksDugCount() {
      return this.blocksDugCount;
   }

   private void steerWhileAirborne(ClientPlayerEntity player) {
      if (this.currentPath != null && this.pathStepIndex < this.currentPath.size()) {
         Step pathpartialitem = this.currentPath.get(this.pathStepIndex);
         Vec3d bottomCenter = Vec3d.ofBottomCenter(pathpartialitem.feet());
         double hypot = this.hunterBody.getHorizontalDistance(bottomCenter);
         if (pathpartialitem.move() != Move.DESCEND && !(hypot < 0.6)) {
            float degrees = this.hunterBody.getYawToward(bottomCenter);
            this.hunterBody.rotateTowardYaw(degrees, 10.0F, 0);
            if (Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw())) < 50.0F) {
               this.hunterBody.pressKeys(1);
            }
         } else {
            if (hypot > 0.35) {
               this.hunterBody.rotateTowardYaw(this.hunterBody.getYawToward(bottomCenter), 10.0F, 0);
            }

            this.hunterBody.moveTowardPoint(bottomCenter, false);
         }
      }
   }

   private boolean isLowOnAir(ClientPlayerEntity player) {
      int[] effectStatus = getAirStatus(player);
      return effectStatus[1] < 1000000 && effectStatus[0] < AirPlan.getLowAirThreshold(effectStatus[1]);
   }

   private void steerInWater(ClientPlayerEntity player, Step step, Vec3d vec) {
      BlockPos feet = step.feet();
      double y = feet.getY();
      Move move = step.move();
      boolean hunterBodyFlag = step.wet() && !step.submerged() && Cells.hasAnyFlag(this.mcCells.getCellFlags(feet.getX(), feet.getY() - 1, feet.getZ()), 1);
      boolean hunterBodyFlag2 = move == Move.SWIM_UP || move == Move.ASCEND || !step.wet() || player.getY() < y - 0.15 || !step.submerged() && !hunterBodyFlag;
      boolean hunterBodyFlag3 = !hunterBodyFlag2 && (move == Move.SWIM_DOWN || player.getY() > y + 0.75);
      double hypot = this.hunterBody.getHorizontalDistance(vec);
      if (hypot > 0.25) {
         float degrees = this.hunterBody.getYawToward(vec);
         this.hunterBody.rotateTowardYaw(degrees, 10.0F, 0);
         float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw()));
         if (absWrapDegrees < 50.0F) {
            this.hunterBody.pressKeys(1);
         } else if (hypot < 0.9) {
            this.hunterBody.moveTowardPoint(vec, false);
         }
      }

      if (hunterBodyFlag2) {
         this.hunterBody.pressKeys(16);
      } else if (hunterBodyFlag3) {
         this.hunterBody.pressKeys(32);
      }
   }

   private void advancePathIndex(ClientPlayerEntity player, BlockPos pos) {
      int pathPartialLimit = Math.min(this.currentPath.size(), this.pathStepIndex + 6 + 1);
      int pathPartialLimit2 = -1;

      for (int i = this.pathStepIndex; i < pathPartialLimit; i++) {
         if (this.hasReachedStep(player, pos, this.currentPath.get(i))) {
            pathPartialLimit2 = i;
         }

         if (this.isEnteringSubmergedStep(i) && i > pathPartialLimit2) {
            break;
         }
      }

      if (pathPartialLimit2 >= 0) {
         this.pathStepIndex = pathPartialLimit2 + 1;
         this.stepStartTick = this.currentTick;
         this.remainingFeetCache = null;
         this.resetStuckTracking();
      }
   }

   public int getFailureCount() {
      return this.failureCount;
   }

   private DigMover.PathMoveResult searchForPath(ClientPlayerEntity player, BlockPos blockPos, Predicate<BlockPos> predicate, BlockPos pos, BlockPos pos2, boolean pathPartialFlag) {
      if (this.search == null) {
         if (this.lastPathEmpty && this.currentTick - this.lastSearchTick < 10L) {
            this.lookTowardAndSurface(player, pos);
            return DigMover.PathMoveResult.FAILED;
         }

         this.lastSearchTick = this.currentTick;
         this.searchStart = blockPos;
         BlockPos blockPos2 = pos2 == null ? blockPos : pos2;
         int[] pathPartialValues = ApproachPlan.computeSearchBounds(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         Object obj = pathPartialFlag ? this.hunterGrid : new DigMover.WalkOnly(this.hunterGrid);
         int[] effectStatus = getAirStatus(player);
         double pathPartialLimit = pos == null ? 0.0 : Math.hypot(pos.getX() - blockPos.getX(), pos.getZ() - blockPos.getZ());
         BlockPos blockPos3 = pos;
         Predicate<BlockPos> predicate2 = predicate;
         if (pathPartialLimit > 32.0) {
            int x = blockPos.getX() + (int)Math.round((pos.getX() - blockPos.getX()) / pathPartialLimit * 32.0);
            int z = blockPos.getZ() + (int)Math.round((pos.getZ() - blockPos.getZ()) / pathPartialLimit * 32.0);
            blockPos3 = new BlockPos(x, blockPos.getY(), z);
            predicate2 = pos3 -> predicate.test(((net.minecraft.util.math.BlockPos) (pos3))) || Math.hypot(pos3.getX() - x, pos3.getZ() - z) <= 4.0;
         }

         this.search = HunterPather.createSearch(
            (Grid)obj,
            blockPos,
            predicate2,
            blockPos3,
            new BlockPos(pathPartialValues[0], pathPartialValues[1], pathPartialValues[2]),
            new BlockPos(pathPartialValues[3], pathPartialValues[4], pathPartialValues[5]),
            40000,
            new Options(effectStatus[0], effectStatus[1], pathPartialLimit > 24.0 ? 1.6 : 1.0, true)
         );
         this.searchGoal = blockPos3;
      }

      long nanoTime = System.nanoTime();

      Result result;
      while ((result = this.search.advanceSearch(200)) == null && System.nanoTime() - nanoTime <= 2000000L) {
      }

      if (result == null) {
         this.lookTowardAndSurface(player, pos);
         return DigMover.PathMoveResult.MOVING;
      } else {
         this.search = null;
         this.lastPathEmpty = result.steps().isEmpty();
         if (this.lastPathEmpty) {
            this.clearPath();
            this.recordFailure(player, null, result.reached() ? "already there" : "no path");
            return DigMover.PathMoveResult.FAILED;
         } else {
            if (!result.reached()) {
               BlockPos feet = result.steps().get(result.steps().size() - 1).feet();
               double pathPartialValue = this.searchGoal == null ? 0.0 : getBlockDistance(this.searchStart, this.searchGoal) - getBlockDistance(feet, this.searchGoal);
               if (pathPartialValue < 3.0) {
                  this.recordFailure(player, null, "partial path");
               }
            }

            this.currentPath = result.steps();
            this.pathReachesGoal = result.reached();
            this.pathStepIndex = 0;
            this.remainingFeetCache = null;
            this.stepStartTick = this.currentTick;
            this.resetStuckTracking();
            this.advancePathIndex(player, blockPos);
            if (this.pathStepIndex >= this.currentPath.size()) {
               this.clearPath();
               return DigMover.PathMoveResult.MOVING;
            } else {
               return null;
            }
         }
      }
   }

   private static double getBlockDistance(BlockPos pos, BlockPos pos2) {
      double deltaX = pos.getX() - pos2.getX();
      double deltaY = pos.getY() - pos2.getY();
      double deltaZ = pos.getZ() - pos2.getZ();
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public boolean wasUpdatedRecently(long digMoverValue) {
      return digMoverValue - this.lastTickSeen <= 2L;
   }

   private boolean shouldSprintToward(ClientPlayerEntity player, Vec3d vec) {
      return player.isOnGround() && !player.isTouchingWater() && this.hunterBody.getHorizontalDistance(vec) >= 3.0;
   }

   private void startTask(String text, ClientPlayerEntity player) {
      this.currentTaskKey = text;
      this.hunterGrid.clearForbiddenCells();
      this.failureCount = 0;
      this.blocksDugCount = 0;
      this.clearPath();
      this.search = null;
      this.lastPathEmpty = false;
      this.lastSearchTick = -2305843009213693952L;
      this.lastProgressPos = player.getEntityPos();
      this.lastFailureReason = "";
   }

   public void resetAll() {
      this.currentTaskKey = null;
      this.clearPath();
      this.search = null;
      this.searchGoal = null;
      this.searchStart = null;
      this.lastSearchTick = -2305843009213693952L;
      this.lastPathEmpty = false;
      this.failureCount = 0;
      this.blocksDugCount = 0;
      this.digTarget = null;
      this.pendingDigBlock = null;
      this.breakAttemptCount = 0;
      this.lastProgressPos = null;
      this.surfacingForAir = false;
      this.hunterGrid.clearForbiddenCells();
      this.hunterGrid.setNoDigBlock(null);
      this.lastFailureReason = "";
   }

   public String getLastFailureReason() {
      return this.lastFailureReason;
   }

   private void lookTowardAndSurface(ClientPlayerEntity player, BlockPos pos) {
      if (pos != null) {
         this.hunterBody.rotateTowardYaw(this.hunterBody.getYawToward(Vec3d.ofCenter(pos)), 10.0F, 0);
      }

      if (player.isSubmergedInWater()) {
         this.hunterBody.pressKeys(16);
      }
   }

   public boolean isSearching() {
      return this.search != null;
   }

   public static int[] getAirStatus(ClientPlayerEntity player) {
      if (!player.hasStatusEffect(StatusEffects.WATER_BREATHING) && !player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
         double attributeValue = player.getAttributeValue(EntityAttributes.OXYGEN_BONUS);
         return new int[]{AirPlan.scaleAirByOxygenBonus(Math.max(0, player.getAir()), attributeValue), AirPlan.scaleAirByOxygenBonus(player.getMaxAir(), attributeValue)};
      } else {
         return new int[]{1000000, 1000000};
      }
   }

   public boolean isSurfacingForAir() {
      return this.surfacingForAir;
   }

   public DigMover.PathMoveResult moveAlongPath(long pathStepTime, String pathStepText, Predicate<BlockPos> predicate, BlockPos pos, BlockPos pos2, boolean pathStepFlag) {
      this.currentTick = pathStepTime;
      this.digTarget = null;
      this.surfacingForAir = false;
      ClientPlayerEntity player = this.client.player;
      boolean touchingWater = player.isTouchingWater();
      BlockPos floored = this.getFeetBlock(player);
      if (this.currentTick - this.lastTickSeen > 1L) {
         this.stepStartTick = this.currentTick;
         this.resetStuckTracking();
      }

      this.lastTickSeen = this.currentTick;
      if (!pathStepText.equals(this.currentTaskKey)) {
         this.startTask(pathStepText, player);
      }

      if (!predicate.test(floored) || !player.isOnGround() && !touchingWater) {
         if (!player.isOnGround() && !touchingWater) {
            this.steerWhileAirborne(player);
            return DigMover.PathMoveResult.MOVING;
         } else {
            this.relaxFailureCount(player);
            boolean pathStepFlag2 = this.currentPath == null || this.pathStepIndex >= this.currentPath.size();
            if (!pathStepFlag2) {
               this.advancePathIndex(player, floored);
               if (this.pathStepIndex >= this.currentPath.size()) {
                  this.clearPath();
                  if (predicate.test(floored)) {
                     return DigMover.PathMoveResult.MOVING;
                  }

                  pathStepFlag2 = true;
               }
            }

            if (!pathStepFlag2) {
               Step pathpartialitem = this.currentPath.get(this.pathStepIndex);
               int pathStepValue = !pathpartialitem.mine().isEmpty() ? 220 : (pathpartialitem.wet() ? 140 : 80);
               if (this.currentTick - this.stepStartTick > pathStepValue) {
                  this.recordFailure(player, pathpartialitem.feet(), "step timeout");
                  this.clearPath();
                  pathStepFlag2 = true;
               } else if (this.isOffPath(player)) {
                  this.lastFailureReason = "off path";
                  this.clearPath();
                  pathStepFlag2 = true;
               }
            }

            if (pathStepFlag2) {
               DigMover.PathMoveResult pathPartial = this.searchForPath(player, floored, predicate, pos, pos2, pathStepFlag);
               if (pathPartial != null) {
                  return pathPartial;
               }
            }

            Step step = this.currentPath.get(this.pathStepIndex);

            for (BlockPos blockPos : step.mine()) {
               BlockState blockState = this.client.world.getBlockState(blockPos);
               if (!blockState.isAir() && (!blockState.getCollisionShape(this.client.world, blockPos).isEmpty() || !blockState.isReplaceable())) {
                  if (pathStepFlag && this.hunterGrid.canDigCell(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
                     if (step.move() == Move.DIG_DOWN && blockPos.equals(floored.down())) {
                        if (!this.hunterBody.isNearCell(floored, 0.22)) {
                           this.hunterBody.moveTowardPoint(Vec3d.ofBottomCenter(floored), true);
                           return DigMover.PathMoveResult.MOVING;
                        }

                        if (BlockStateUtilsFluid.hasLavaBelow(this.client.world, blockPos)
                           || BlockStateUtilsFluid.isStandingOverLava(this.client.world, player.getBoundingBox(), blockPos, false)) {
                           this.recordFailure(player, step.feet(), "lava under");
                           this.clearPath();
                           return DigMover.PathMoveResult.MOVING;
                        }
                     }

                     this.digBlock(player, blockPos, step.feet());
                     return DigMover.PathMoveResult.MOVING;
                  } else {
                     this.recordFailure(player, step.feet(), "cannot dig " + blockPos.toShortString());
                     this.clearPath();
                     return DigMover.PathMoveResult.MOVING;
                  }
               }

               if (blockPos.equals(this.pendingDigBlock)) {
                  this.blocksDugCount++;
                  this.pendingDigBlock = null;
               }
            }

            if (step.submerged() && !player.isSubmergedInWater() && this.isLowOnAir(player)) {
               this.surfacingForAir = true;
               this.stepStartTick = this.currentTick;
               this.resetStuckTracking();
               this.hunterBody.rotateTowardYaw(this.hunterBody.getYawToward(Vec3d.ofBottomCenter(step.feet())), 10.0F, 0);
               if (touchingWater) {
                  this.hunterBody.pressKeys(16);
               }

               return DigMover.PathMoveResult.MOVING;
            } else {
               this.steerForStep(player, floored, step);
               this.trackStuck(player, step);
               return DigMover.PathMoveResult.MOVING;
            }
         }
      } else {
         this.clearPath();
         this.search = null;
         if (!touchingWater && !this.hunterBody.isNearCell(floored, 0.3)) {
            this.hunterBody.moveTowardPoint(Vec3d.ofBottomCenter(floored), StandFinder.hasDeepDropNeighbor(this.mcCells, floored.getX(), floored.getY(), floored.getZ()));
            return DigMover.PathMoveResult.MOVING;
         } else {
            return DigMover.PathMoveResult.ARRIVED;
         }
      }
   }

   private BlockPos getFeetBlock(ClientPlayerEntity player) {
      if (player.isTouchingWater()) {
         BlockPos floored = BlockPos.ofFloored(player.getX(), player.getY() + 0.2, player.getZ());
         int hashPut = this.mcCells.getCellFlags(floored.getX(), floored.getY(), floored.getZ());
         if (Cells.hasAnyFlag(hashPut, 4) && Cells.isFreeOfSolidFlags(hashPut)) {
            return floored;
         }
      }

      return this.hunterBody.getFeetBlock();
   }

   public List<BlockPos> getRemainingPathFeet() {
      if (this.currentPath != null && this.pathStepIndex < this.currentPath.size()) {
         if (this.remainingFeetCache == null) {
            var partialPathItems = new ArrayList(this.currentPath.size() - this.pathStepIndex);

            for (int i = this.pathStepIndex; i < this.currentPath.size(); i++) {
               partialPathItems.add(this.currentPath.get(i).feet());
            }

            this.remainingFeetCache = partialPathItems;
         }

         return this.remainingFeetCache;
      } else {
         return null;
      }
   }

   public BlockPos getPathEnd() {
      return this.currentPath != null && !this.currentPath.isEmpty() ? this.currentPath.get(this.currentPath.size() - 1).feet() : null;
   }

   public void setNoDigBlock(BlockPos pos2) {
      this.hunterGrid.setNoDigBlock(pos2);
   }

   private int findLookaheadIndex(ClientPlayerEntity player, BlockPos pos) {
      int pathPartialValue = this.pathStepIndex;
      Vec3d entityPos = player.getEntityPos();
      int partialPathLimit = Math.min(this.currentPath.size(), this.pathStepIndex + 6);

      for (int i = this.pathStepIndex; i < partialPathLimit; pathPartialValue = i++) {
         Step pathpartialitem = this.currentPath.get(i);
         if (pathpartialitem.move() != Move.WALK && pathpartialitem.move() != Move.DIAGONAL
            || !pathpartialitem.mine().isEmpty()
            || pathpartialitem.wet()
            || pathpartialitem.feet().getY() != pos.getY()
            || !this.isStraightWalkClear(entityPos, Vec3d.ofBottomCenter(pathpartialitem.feet()), pos.getY())) {
            break;
         }
      }

      return pathPartialValue;
   }

   public boolean isDigging() {
      return this.digTarget != null;
   }

   public void cancelPath() {
      this.clearPath();
      this.search = null;
   }

   private void recordFailure(ClientPlayerEntity player, BlockPos pos, String pathStepText) {
      if (pos != null) {
         this.hunterGrid.markCellForbidden(pos);
      }

      this.failureCount++;
      this.lastFailureReason = pathStepText;
      this.lastProgressPos = player.getEntityPos();
   }

   public DigMover(HunterBody hunterBody, AnchorBreaker anchorBreaker, McCells mcCells) {
      this.lastSearchTick = -2305843009213693952L;
      this.lastTickSeen = -2305843009213693952L;
      this.closestDistance = Double.MAX_VALUE;
      this.lastFailureReason = "";
      this.hunterBody = hunterBody;
      this.anchorBreaker = anchorBreaker;
      this.mcCells = mcCells;
      this.hunterGrid = new HunterGrid(mcCells);
   }

   private boolean hasReachedStep(ClientPlayerEntity player, BlockPos pos, Step step) {
      if (!step.wet()) {
         return pos.equals(step.feet());
      } else {
         double deltaY = player.getY() - step.feet().getY();
         return this.hunterBody.getHorizontalDistance(Vec3d.ofBottomCenter(step.feet())) <= 0.55 && deltaY > -0.6 && deltaY < 1.1;
      }
   }

   private boolean isOffPath(ClientPlayerEntity player) {
      int partialPathValue = Math.max(0, this.pathStepIndex - 1);
      int partialPathLimit = Math.min(this.currentPath.size(), this.pathStepIndex + 6);
      double partialPathBound = Double.MAX_VALUE;

      for (int i = partialPathValue; i < partialPathLimit; i++) {
         partialPathBound = Math.min(partialPathBound, this.getDistanceToCell(player, this.currentPath.get(i).feet()));
      }

      if (this.pathStepIndex == 0 && this.searchStart != null) {
         partialPathBound = Math.min(partialPathBound, this.getDistanceToCell(player, this.searchStart));
      }

      return partialPathBound > 2.6;
   }

   private void steerWalking(ClientPlayerEntity player, Vec3d vec, boolean hunterBodyFlag, boolean hunterBodyFlag2, boolean swimHunterFlag) {
      float degrees = this.hunterBody.getYawToward(vec);
      this.hunterBody.rotateTowardYaw(degrees, 10.0F, 0);
      float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(degrees - player.getYaw()));
      double hypot = this.hunterBody.getHorizontalDistance(vec);
      if (absWrapDegrees < 50.0F && !swimHunterFlag) {
         this.hunterBody.pressKeys(1);
         if (hunterBodyFlag && absWrapDegrees < 18.0F && player.getHungerManager().getFoodLevel() > 6) {
            this.hunterBody.pressKeys(64);
         }
      } else if (!swimHunterFlag && hypot < 0.9) {
         this.hunterBody.moveTowardPoint(vec, false);
      }

      if (hunterBodyFlag2 && absWrapDegrees < 35.0F) {
         this.hunterBody.pressKeys(16);
      }
   }

   private void clearPath() {
      this.currentPath = null;
      this.pathStepIndex = 0;
      this.remainingFeetCache = null;
      this.resetStuckTracking();
   }

   private void trackStuck(ClientPlayerEntity player, Step step) {
      double amount = this.getDistanceToCell(player, step.feet());
      if (amount < this.closestDistance - 0.06) {
         this.closestDistance = amount;
         this.noProgressTicks = 0;
      } else {
         this.noProgressTicks++;
      }

      boolean stuckGroundFlag = player.horizontalCollision && player.isOnGround() && !player.isTouchingWater() && step.feet().getY() == this.hunterBody.getFeetBlock().getY();
      this.collisionStuckTicks = stuckGroundFlag ? this.collisionStuckTicks + 1 : 0;
      if ((this.noProgressTicks == 10 || this.collisionStuckTicks == 6) && player.isOnGround()) {
         this.hunterBody.pressKeys(16);
      }

      if (this.noProgressTicks >= 32) {
         this.recordFailure(player, step.feet(), "stuck");
         this.clearPath();
      }
   }

   private double getTurnAngle(int index) {
      if (index + 1 >= this.currentPath.size()) {
         return 0.0;
      } else {
         BlockPos blockPos = index > 0 ? this.currentPath.get(index - 1).feet() : this.searchStart;
         BlockPos feet = this.currentPath.get(index).feet();
         BlockPos feet2 = this.currentPath.get(index + 1).feet();
         if (blockPos == null) {
            return 0.0;
         } else {
            double degrees = Math.toDegrees(Math.atan2(feet.getZ() - blockPos.getZ(), feet.getX() - blockPos.getX()));
            double degrees2 = Math.toDegrees(Math.atan2(feet2.getZ() - feet.getZ(), feet2.getX() - feet.getX()));
            return Math.abs(MathHelper.wrapDegrees(degrees2 - degrees));
         }
      }
   }

   private boolean isStraightWalkClear(Vec3d vec, Vec3d vec2, int floorGridValue) {
      double deltaX = vec2.x - vec.x;
      double deltaZ = vec2.z - vec.z;
      double hypot = Math.hypot(deltaX, deltaZ);
      int floorGridLimit = Math.max(1, (int)Math.ceil(hypot / 0.3));

      for (int i = 0; i <= floorGridLimit; i++) {
         double floorGridFactor = (double)i / floorGridLimit;
         double floorGridValue2 = vec.x + deltaX * floorGridFactor;
         double floorGridValue3 = vec.z + deltaZ * floorGridFactor;
         if (!Cells.isSolidWithoutHazard(this.mcCells.getCellFlags(MathHelper.floor(floorGridValue2), floorGridValue - 1, MathHelper.floor(floorGridValue3)))) {
            return false;
         }

         for (double floorProbeOffsetsItem : FLOOR_PROBE_OFFSETS) {
            for (double floorGridValue5 : FLOOR_PROBE_OFFSETS) {
               int floor = MathHelper.floor(floorGridValue2 + floorProbeOffsetsItem);
               int floor7 = MathHelper.floor(floorGridValue3 + floorGridValue5);
               if (this.hunterGrid.isSolidCell(floor, floorGridValue, floor7)
                  || this.hunterGrid.isSolidCell(floor, floorGridValue + 1, floor7)
                  || this.hunterGrid.isForbiddenCell(floor, floorGridValue, floor7)
                  || this.hunterGrid.isForbiddenCell(floor, floorGridValue + 1, floor7)) {
                  return false;
               }

               int hashPut = this.mcCells.getCellFlags(floor, floorGridValue, floor7);
               int cellFlags = this.mcCells.getCellFlags(floor, floorGridValue + 1, floor7);
               int cellsValue = this.mcCells.getCellFlags(floor, floorGridValue - 1, floor7);
               if (Cells.hasAnyFlag(hashPut | cellFlags, 286)) {
                  return false;
               }

               if (Cells.hasAnyFlag(cellsValue, 26)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean isEnteringSubmergedStep(int index) {
      Step pathpartialitem = this.currentPath.get(index);
      return pathpartialitem.wet() && !pathpartialitem.submerged() && index + 1 < this.currentPath.size() && this.currentPath.get(index + 1).submerged();
   }

   public BlockPos getCurrentFeetBlock(ClientPlayerEntity player) {
      return this.getFeetBlock(player);
   }

   private void resetStuckTracking() {
      this.closestDistance = Double.MAX_VALUE;
      this.noProgressTicks = 0;
      this.collisionStuckTicks = 0;
   }

   private void digBlock(ClientPlayerEntity player, BlockPos next, BlockPos pos) {
      this.digTarget = next;
      this.pendingDigBlock = next;
      BreakStatus hunterBody = this.anchorBreaker.tickBreakTarget(next, false);
      if (hunterBody == BreakStatus.GONE) {
         this.blocksDugCount++;
         this.pendingDigBlock = null;
         this.digTarget = null;
      } else if (hunterBody == BreakStatus.NO_SIGHT) {
         this.recordFailure(player, pos, "no sight " + next.toShortString());
         this.clearPath();
         this.anchorBreaker.cancelBreaking();
         this.digTarget = null;
      } else if (hunterBody == BreakStatus.WORKING) {
         this.anchorBreaker.setBreakRequested(true);
         if (this.anchorBreaker.isCrosshairOnTarget()) {
            this.breakAttemptCount = 0;
         } else {
            Vec3d eyePos = this.hunterBody.getVisibleAimPoint(next, 4.5);
            if (eyePos != null && this.hunterBody.isLookingToward(eyePos, 2.5F)) {
               BlockHitResult missed = this.hunterBody.raycastForward(4.5);
               if (missed.getType() == Type.BLOCK && missed.getBlockPos().equals(next)) {
                  this.breakAttemptCount = 0;
               } else {
                  if (++this.breakAttemptCount > 8) {
                     this.breakAttemptCount = 0;
                     this.recordFailure(player, pos, "crosshair blocked at " + next.toShortString());
                     this.clearPath();
                     this.anchorBreaker.cancelBreaking();
                     this.digTarget = null;
                  }
               }
            }
         }
      }
   }

   private void steerForStep(ClientPlayerEntity player, BlockPos pos, Step step) {
      Vec3d bottomCenter = Vec3d.ofBottomCenter(step.feet());
      Move move = step.move();
      if (!player.isTouchingWater() && !step.wet() && move != Move.SWIM && move != Move.SWIM_UP && move != Move.SWIM_DOWN) {
         switch (move) {
            case WALK:
            case DIAGONAL:
               int partialPath = this.findLookaheadIndex(player, pos);
               Vec3d ofBottomCenter = Vec3d.ofBottomCenter(this.currentPath.get(partialPath).feet());
               boolean touchingWater = this.shouldSprintToward(player, ofBottomCenter);
               boolean swimHunterFlag = player.isSprinting() && this.hunterBody.getHorizontalDistance(ofBottomCenter) < 1.2 && this.getTurnAngle(partialPath) > 60.0;
               this.steerWalking(player, ofBottomCenter, touchingWater, false, swimHunterFlag);
               break;
            case ASCEND:
               this.steerWalking(player, bottomCenter, false, player.isOnGround() && this.hunterBody.getHorizontalDistance(bottomCenter) < 1.25, false);
               break;
            case DIG_DOWN:
               this.hunterBody.moveTowardPoint(bottomCenter, true);
               break;
            default:
               this.steerWalking(player, bottomCenter, false, false, false);
         }
      } else {
         this.steerInWater(player, step, bottomCenter);
      }
   }

   private void relaxFailureCount(ClientPlayerEntity player) {
      if (this.failureCount > 0 && this.lastProgressPos != null) {
         if (!(player.getEntityPos().distanceTo(this.lastProgressPos) < 8.0)) {
            this.failureCount--;
            this.lastProgressPos = player.getEntityPos();
         }
      }
   }

   public static enum PathMoveResult {
      FAILED,
      MOVING,
      ARRIVED;
   }

   private record WalkOnly(Grid g) implements Grid {
      @Override
      public boolean isForbiddenCell(int z, int y, int x) {
         return this.g.isForbiddenCell(z, y, x);
      }

      @Override
      public boolean isWaterCell(int y, int z, int x) {
         return this.g.isWaterCell(y, z, x);
      }

      @Override
      public boolean isSolidCell(int x, int y, int z) {
         return this.g.isSolidCell(x, y, z);
      }

      @Override
      public float getDigCost(int x, int y, int z) {
         return 1.0F;
      }

      @Override
      public boolean canDigCell(int y, int z, int x) {
         return false;
      }

      @Override
      public boolean isNextToLavaCell(int x, int y, int z) {
         return this.g.isNextToLavaCell(x, y, z);
      }

      @Override
      public float getLavaPenalty(int z, int x, int y) {
         return this.g.getLavaPenalty(z, x, y);
      }
   }
}
