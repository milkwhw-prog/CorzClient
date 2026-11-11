package com.corz.client.utils;

import com.corz.client.world.StateBlockUtilsCenter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.BlockStateUtilsFluid;
import com.corz.client.utils.Drill;
import com.corz.client.utils.HotbarFoodEater;
import com.corz.client.utils.AreaLayerCursor;
import com.corz.client.utils.BlockRegionSelection;
import com.corz.client.utils.FluidSafetyController;
import com.corz.client.utils.BlockAimController;
import com.corz.client.utils.ScriptedMovementInput;
import com.corz.client.utils.MinerPathNavigator;
import com.corz.client.utils.PillarUpPlacer;
import com.corz.client.utils.SmoothAimRotator;

public final class AreaMinerController {
   private boolean finalScanActive;
   private int finalScanX;
   private final ArrayDeque<BlockPos> straightCutQueue;
   private int finalCheckRetryCount;
   private final Drill drill;
   private final Set<BlockPos> plugCandidatePositions;
   private final Map<BlockPos, Integer> settleCooldownTicks;
   private double lastPlayerZ;
   private final Map<BlockPos, Integer> skippedBlockCooldowns;
   private int restoreCountResetTicks;
   private int xpOrbWaitTicks;
   private BlockPos downCutTarget;
   private int savedHotbarSlot;
   private BlockPos shaftAlignPos;
   private int minedBlockCount;
   private int inventorySyncWaitTicks;
   private boolean layerRemineTriggered;
   private final Map<BlockPos, Integer> breakAttemptCounts;
   private int unsafeSpotFailCount;
   private double lastDistanceToStandSpot;
   private final Set<BlockPos> unreachableBlockSet;
   private Input savedPlayerInput;
   private long lastSkipMessageMs;
   private boolean noHotbarBlocksWarned;
   private BlockPos sweepTargetBlock;
   private final HotbarFoodEater inventoryFood;
   private BlockPos currentStraightCut;
   private final Map<BlockPos, Integer> serverRestoreCounts;
   private int idleStuckTicks;
   private int repairNoProgressTicks;
   private int trackedSwapSlot;
   private final MinecraftClient minecraftClient;
   private int skippedBlockCount;
   private BlockPos currentBreakTarget;
   private int lastToolDamage;
   private final Set<BlockPos> lavaPlugPositions;
   private int pillarBackCooldownTicks;
   private boolean finalCheckPhaseActive;
   private int straightCutsDone;
   private boolean fellBelowLayer;
   private AreaLayerCursor layerCursor;
   private final BlockRegionSelection areaBounds;
   private boolean boringIntoFace;
   private final FluidSafetyController playerEntityHelper;
   private boolean repairGivenUp;
   private final Set<BlockPos> minedAirPositions;
   private int stuckEscalationCount;
   private final List<BlockPos> finalCheckBlocks;
   private int layerRetryCount;
   private int currentRowId;
   private final Set<BlockPos> placedPlugPositions;
   private int leftoverStuckTicks;
   private boolean collectingXpOrbs;
   private Item pendingRestoreItem;
   private boolean repairingTool;
   private boolean inCentreColumn;
   private AreaMinerController.State state;
   private ClientPlayerEntity swapPlayer;
   private int bottleThrowTicks;
   private final BlockAimController blockAimer;
   private int breakAttemptTicks;
   private int shaftAlignTicks;
   private int shaftCutsDone;
   private int shaftLevelTicks;
   private BlockPos shaftApproachTarget;
   private Item offhandItemBeforeSwap;
   private AreaMinerController.MiningTask task;
   private int pillarBackTicks;
   private boolean drillSweepWarned;
   private boolean autoPaused;
   private BlockPos standSpotCandidate;
   private BlockPos progressTrackedSpot;
   private String statusText;
   private final ScriptedMovementInput syntheticInput = new ScriptedMovementInput();
   private int crosshairMissTicks;
   private int finalScanZ;
   private MinerPathNavigator navigator;
   private int taskTicks;
   private int shaftPhaseTicks;
   private double lastPlayerY;
   private int straightCutTicks;
   private boolean playerMoving;
   private int shaftRouteRetries;
   private int noProgressTicks;
   private int shaftTargetY;
   private boolean alignedWithShaft;
   private BlockPos lastCentreCell;
   private int queuedSwapSlot;
   private int initialScanDelayTicks;
   private BlockPos sweepStandSpot;
   private boolean pillaringUp;
   private int finalScanY;
   private int boreRowsRemaining;
   private boolean preferDrill;
   private double x;
   private int dropWaitTicks;
   private BlockPos straightCutStandSpot;
   private BlockPos sweepBreakTarget;
   private final Set<BlockPos> rejectedStandSpots;

   private void resetNavigationTargets() {
      this.abortBreaking();
      if (this.navigator != null) {
         this.navigator.resetNavigation();
      }
   }

   public BlockPos getAimTarget() {
      return this.blockAimer.getCurrentTarget();
   }

   private boolean tickStraightCuts(World world, ClientPlayerEntity player) {
      while (!this.straightCutQueue.isEmpty() && !this.isBreakableTarget(world, this.straightCutQueue.peek())) {
         BlockPos arraydeque = this.straightCutQueue.poll();
         if (arraydeque.equals(this.currentStraightCut)) {
            this.straightCutsDone++;
            this.currentStraightCut = null;
         }

         this.straightCutTicks = 0;
      }

      if (this.straightCutsDone >= 3) {
         this.straightCutQueue.clear();
         this.straightCutStandSpot = null;
         this.currentStraightCut = null;
         return false;
      } else if (this.straightCutQueue.isEmpty()) {
         return false;
      } else if (this.straightCutTicks > 600) {
         this.straightCutQueue.clear();
         this.straightCutStandSpot = null;
         this.sendChatMessage("\u00a7estraight cuts at the row start took too long \u2014 back to the normal route");
         return false;
      } else {
         BlockPos peek = this.straightCutQueue.peek();
         if (this.skippedBlockCooldowns.containsKey(peek)) {
            this.straightCutQueue.clear();
            this.straightCutStandSpot = null;
            this.currentStraightCut = null;
            this.sendChatMessage("\u00a7ea straight cut won't break \u2014 back to the normal route");
            return false;
         } else {
            double maxSpotStandAmount = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
            int wonCutValueOffset = this.straightCutsDone + 1;
            if (this.blockAimer.canReachBlock(peek, maxSpotStandAmount)) {
               this.currentStraightCut = peek;
               this.aimAndBreakBlock(peek, maxSpotStandAmount, true);
               this.statusText = "straight cut " + wonCutValueOffset + "/3";
               return true;
            } else if (this.tryClearObstacle(world, player, peek, maxSpotStandAmount)) {
               return true;
            } else {
               BlockPos cornerNear = this.findStandSpotFor(world, player, peek);
               if (cornerNear == null) {
                  cornerNear = new BlockPos(peek.getX(), this.layerCursor.getSliceBottomY(), peek.getZ());
               }

               this.statusText = "walking to straight cut " + wonCutValueOffset + "/3";
               if (!cornerNear.equals(this.straightCutStandSpot)) {
                  this.straightCutStandSpot = cornerNear.toImmutable();
                  this.navigator.resetNavigation();
               }

               if (this.tickClearPlugAtStandSpot(world, player, cornerNear)) {
                  if (this.task == AreaMinerController.MiningTask.REPOSITION) {
                     this.straightCutQueue.clear();
                     this.straightCutStandSpot = null;
                     return false;
                  } else {
                     return true;
                  }
               } else {
                  if (this.navigator.moveToward(cornerNear) == MinerPathNavigator.Result.BLOCKED) {
                     this.rejectedStandSpots.add(cornerNear.toImmutable());
                     this.straightCutStandSpot = null;
                     this.navigator.resetNavigation();
                  }

                  return true;
               }
            }
         }
      }
   }

   public List<BlockPos> getDigCellBlocks() {
      return this.state == AreaMinerController.State.RUNNING && this.layerCursor != null && !this.layerCursor.isCheckingLeftovers()
         ? this.layerCursor.getSurroundingPositions()
         : List.of();
   }

   private boolean isBreakableTarget(World world, BlockPos pos2) {
      if (!this.areaBounds.canFlag(pos2) || !this.isInsideMineableArea(pos2) || world.getBlockState(pos2).isAir() || this.isPlugHoldingFluid(world, pos2)) {
         return false;
      } else {
         return this.minedAirPositions.contains(pos2) && !(world.getBlockState(pos2).getBlock() instanceof FallingBlock) ? false : StateBlockUtilsCenter.canHardnessState(world, pos2);
      }
   }

   private boolean isSupportBlockBelowPlayer(BlockPos pos2) {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player == null) {
         return false;
      } else {
         BlockPos blockPos = player.getBlockPos();
         return pos2.equals(blockPos.down())
            ? true
            : pos2.getY() < blockPos.getY() && pos2.getSquaredDistance(blockPos) <= 4.0 && this.navigator.getPlacedBlocks().contains(pos2);
      }
   }

   private int countBreakableBlocksUpTo(World world, int cornerNearValue) {
      int cornerNear = 0;

      for (int i = this.layerCursor.getCurrentLayerTopY(); i >= this.layerCursor.getSliceBottomY(); i--) {
         for (int j = this.layerCursor.getFaceAxisStart(); j <= this.layerCursor.getFaceAxisEnd(); j++) {
            for (int k = this.layerCursor.getRowAxisMin(); k <= this.layerCursor.getRowAxisMax(); k++) {
               if (this.isBreakableTarget(world, this.layerCursor.toBlockPos(j, k, i))) {
                  if (++cornerNear >= cornerNearValue) {
                     return cornerNear;
                  }
               }
            }
         }
      }

      return cornerNear;
   }

   private boolean tickPillarBack(ClientPlayerEntity player) {
      if (!this.drill.placeBlocks) {
         this.fellBelowLayer = false;
         return false;
      } else if (this.pillarBackCooldownTicks > 0) {
         this.pillarBackCooldownTicks--;
         this.fellBelowLayer = false;
         return false;
      } else {
         boolean fellBackFlag = false;
         if (!this.layerCursor.isCheckingLeftovers()) {
            BlockPos blockPos = this.layerCursor.getSliceCenterPos();
            double fellBackFactor = blockPos.getX() + 0.5 - player.getX();
            double fellBackFactor2 = blockPos.getZ() + 0.5 - player.getZ();
            fellBackFlag = fellBackFactor * fellBackFactor + fellBackFactor2 * fellBackFactor2 <= 6.25;
         }

         boolean fellBackFlag2 = this.layerCursor.isCheckingLeftovers()
            || this.task == AreaMinerController.MiningTask.APPROACH && !fellBackFlag
            || this.task == AreaMinerController.MiningTask.REPOSITION
            || this.sweepStandSpot != null;
         if (fellBackFlag2) {
            this.fellBelowLayer = false;
            return false;
         } else {
            boolean fellBackFlag3 = player.getBlockY() < this.layerCursor.getSliceBottomY();
            if (!this.fellBelowLayer && !fellBackFlag3) {
               return false;
            } else if (this.fellBelowLayer && !fellBackFlag3 && player.isOnGround()) {
               this.fellBelowLayer = false;
               this.pillarBackTicks = 0;
               this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
               return false;
            } else {
               this.fellBelowLayer = true;
               this.pillarBackTicks++;
               this.currentBreakTarget = null;
               if (this.pillarBackTicks > 300) {
                  this.fellBelowLayer = false;
                  this.pillarBackTicks = 0;
                  this.pillarBackCooldownTicks = 200;
                  this.syntheticInput.clearInput();
                  this.navigator.resetNavigation();
                  this.setTask(AreaMinerController.MiningTask.REPOSITION);
                  this.sendChatMessage("\u00a7ecould not pillar back up in 15 s \u2014 finding another way back to the layer");
                  return false;
               } else if (!this.navigator.canPillarUp(player)) {
                  if (!PillarUpPlacer.hasOpaqueBlockInHotbar(player)) {
                     this.syntheticInput.clearInput();
                     this.warnIfNoHotbarBlocks(
                        "\u00a7efell below the layer and there is no solid block in your hotbar to climb back \u2014 put cobblestone in your hotbar."
                     );
                     this.statusText = "fell down \u2014 waiting for blocks to climb up";
                     return true;
                  } else {
                     this.fellBelowLayer = false;
                     return false;
                  }
               } else {
                  this.statusText = this.navigator.isBreakingCeiling()
                     ? "fell down \u2014 breaking the ceiling above to keep climbing"
                     : "fell down \u2014 pillaring back up (" + this.pillarBackTicks / 20 + "s)";
                  return true;
               }
            }
         }
      }
   }

   private void markNeighboursSettling(BlockPos pos2) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
               this.settleCooldownTicks.put(pos2.add(i, j, k), 8);
            }
         }
      }
   }

   private void resumeIfAutoPaused() {
      if (this.state == AreaMinerController.State.PAUSED && this.autoPaused) {
         this.state = AreaMinerController.State.RUNNING;
         this.autoPaused = false;
         this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
      }
   }

   private boolean tickBreakTarget(World world, ClientPlayerEntity player) {
      BlockPos immutable = this.blockAimer.getCurrentTarget();
      if (immutable != null && this.currentBreakTarget != null && immutable.equals(this.currentBreakTarget)) {
         double maxSpotStandAmount = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
         if (this.isBreakableTarget(world, immutable) && this.blockAimer.canReachBlock(immutable, maxSpotStandAmount)) {
            this.syntheticInput.jumpBlocked = true;
            this.crosshairMissTicks = this.isCrosshairOnBlock(immutable) ? 0 : this.crosshairMissTicks + 1;
            boolean immutableInputFlag = this.drill.drillActive && this.crosshairMissTicks > 60;
            if (++this.breakAttemptTicks <= 1200 && !immutableInputFlag) {
               this.aimAndBreakBlock(immutable, maxSpotStandAmount, this.preferDrill);
               return true;
            } else {
               this.breakAttemptTicks = 0;
               this.crosshairMissTicks = 0;
               this.skippedBlockCooldowns.put(immutable.toImmutable(), 600);
               this.blockAimer.clearTargetAndCancelBreaking();
               this.currentBreakTarget = null;
               this.stuckEscalationCount++;
               this.syntheticInput.jumpBlocked = false;
               int merge = this.breakAttemptCounts.merge(immutable.toImmutable(), 1, Integer::sum);
               if (immutableInputFlag) {
                  this.blockAimer.invalidateAimPoint(immutable);
               }

               if (this.drill.drillActive && merge >= 3) {
                  this.unreachableBlockSet.add(immutable.toImmutable());
                  this.sendChatMessage(
                     "\u00a7egave up on the block at " + immutable.toShortString() + " after " + merge + " tries \u2014 it stays as a leftover"
                  );
                  this.setTask(AreaMinerController.MiningTask.REPOSITION);
                  return false;
               } else {
                  if (immutableInputFlag) {
                     this.sendChatMessage("\u00a7ecan't get the crosshair onto the block at " + immutable.toShortString() + " \u2014 trying another angle");
                  } else {
                     this.sendChatMessage(
                        "\u00a7ethe block at " + immutable.toShortString() + " didn't break after 60s \u2014 leaving it for now, trying another angle"
                     );
                  }

                  this.setTask(AreaMinerController.MiningTask.REPOSITION);
                  return false;
               }
            }
         } else {
            this.breakAttemptTicks = 0;
            this.crosshairMissTicks = 0;
            this.syntheticInput.jumpBlocked = false;
            return false;
         }
      } else {
         this.breakAttemptTicks = 0;
         this.crosshairMissTicks = 0;
         this.syntheticInput.jumpBlocked = false;
         return false;
      }
   }

   private boolean isPlugHoldingFluid(World world, BlockPos pos2) {
      return !this.plugCandidatePositions.contains(pos2) && !this.placedPlugPositions.contains(pos2)
         ? false
         : !world.getBlockState(pos2).isAir() && BlockStateUtilsFluid.hasAdjacentFluid(world, pos2, this.lavaPlugPositions.contains(pos2), this.areaBounds::canFlag);
   }

   private boolean tickLayerRetry(ClientPlayerEntity player, World world) {
      if (!this.drill.scanLeftovers) {
         return false;
      } else if (this.tickLeftoverSweep(player, world, this.layerCursor.getSliceBottomY(), this.layerCursor.getCurrentLayerTopY(), false)) {
         return true;
      } else {
         int cornerNear = this.countRemainingBlocksInLayers(world, this.layerCursor.getSliceBottomY(), this.layerCursor.getCurrentLayerTopY());
         if (cornerNear > 0 && !this.settleCooldownTicks.isEmpty()) {
            this.syntheticInput.clearInput();
            this.statusText = "waiting for the last blocks to settle";
            return true;
         } else if (cornerNear > 0 && this.layerRetryCount < 2) {
            this.layerRetryCount++;
            this.skippedBlockCooldowns.clear();
            this.rejectedStandSpots.clear();
            this.sendChatMessage(
               "layer "
                  + (this.layerCursor.getLayerIndex() + 1)
                  + ": "
                  + cornerNear
                  + " skipped block"
                  + (cornerNear == 1 ? "" : "s")
                  + " still standing \u2014 trying again ("
                  + this.layerRetryCount
                  + "/"
                  + 2
                  + ")"
            );
            return true;
         } else {
            this.layerRetryCount = 0;
            this.skippedBlockCooldowns.clear();
            return false;
         }
      }
   }

   private void findStandSpotSeeingCentre(World world, BlockPos pos2) {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player != null) {
         if (this.blockAimer.canReachBlock(pos2, this.drill.reachDistance)) {
            if (this.navigator.isBridging()) {
               this.navigator.resetBridging();
            }

            this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
         } else {
            if (this.standSpotCandidate == null) {
               this.standSpotCandidate = this.findStandSpotFor(world, player, pos2);
               if (this.standSpotCandidate == null) {
                  this.statusText = "no stand spot can see the centre \u2014 boring into the centre column";
                  this.rejectedStandSpots.clear();
                  this.boreRowsRemaining = 3;
                  this.boringIntoFace = true;
                  this.navigator.resetNavigation();
                  this.setTask(AreaMinerController.MiningTask.APPROACH);
                  return;
               }

               this.navigator.resetNavigation();
            }

            this.statusText = "looking for a stand spot that can see the centre";
            MinerPathNavigator.Result movingImmutable = this.navigator.moveToward(this.standSpotCandidate);
            if (movingImmutable == MinerPathNavigator.Result.ARRIVED) {
               if (this.navigator.isBridging()) {
                  this.navigator.resetBridging();
               }

               this.rejectedStandSpots.add(this.standSpotCandidate);
               this.standSpotCandidate = null;
               this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
            } else if (movingImmutable == MinerPathNavigator.Result.BLOCKED) {
               if (this.navigator.isBridging()) {
                  this.navigator.resetBridging();
               }

               this.rejectedStandSpots.add(this.standSpotCandidate);
               this.standSpotCandidate = null;
            }
         }
      }
   }

   private boolean allowsBreakingBlock(BlockPos pos2, boolean lavaBehindFlag) {
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player == null || world == null) {
         return true;
      } else if (this.isPlugHoldingFluid(world, pos2)) {
         return false;
      } else if (BlockStateUtilsFluid.isStandingOverLava(world, player.getBoundingBox(), pos2, lavaBehindFlag)) {
         this.skipBlockWithReason(pos2, "there is lava under it");
         return false;
      } else if (BlockStateUtilsFluid.hasLavaNearby(world, pos2, lavaBehindFlag) && (!this.drill.placeBlocks || !this.playerEntityHelper.hasPlugItem(player, true))) {
         this.skipBlockWithReason(
            pos2, this.drill.placeBlocks ? "lava is behind it and there are no blocks to plug it" : "lava is behind it and Place Blocks is off"
         );
         return false;
      } else {
         return true;
      }
   }

   private boolean tickCentreAlignment(ClientPlayerEntity player) {
      if (player.isOnGround() && this.layerCursor != null) {
         double cornerNearValue = this.layerCursor.getTargetRowCoord() + 0.5;
         double cornerNearValue2 = this.layerCursor.faceAxis == Axis.X ? player.getZ() : player.getX();
         double deltaVar = cornerNearValue2 - cornerNearValue;
         double abs3 = Math.abs(deltaVar);
         if (abs3 <= 0.15) {
            return false;
         } else {
            double cornerNearFactor = this.layerCursor.faceAxis == Axis.X ? 0.0 : -deltaVar;
            double spotValue = this.layerCursor.faceAxis == Axis.X ? -deltaVar : 0.0;
            ClientWorld world = this.minecraftClient.world;
            if (world != null) {
               int x = (int)Math.floor(player.getX() + Math.signum(cornerNearFactor) * 0.35);
               int z = (int)Math.floor(player.getZ() + Math.signum(spotValue) * 0.35);
               int y = (int)Math.floor(player.getY() - 0.1);
               if (!StateBlockUtilsCenter.canSquareState(world, new BlockPos(x, y, z))) {
                  return false;
               }

               int y2 = (int)Math.floor(player.getY() + 0.01);
               if (!StateBlockUtilsCenter.canCollisionShape(world, new BlockPos(x, y2, z))
                  || !StateBlockUtilsCenter.canCollisionShape(world, new BlockPos(x, y2 + 1, z))) {
                  return false;
               }
            }

            double angle = Math.toRadians(player.getYaw());
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double cornerNearValue4 = spotValue * cos - cornerNearFactor * sin;
            double blockPosLimit = spotValue * sin + cornerNearFactor * cos;
            this.syntheticInput.setMovementInputWithSneak(toInputAxisFine(cornerNearValue4), toInputAxisFine(blockPosLimit), false, false, false);
            this.statusText = "centring on the 9-cell middle (off by " + String.format("%.2f", abs3) + ")";
            return true;
         }
      } else {
         return false;
      }
   }

   private void aimAndBreakBlock(BlockPos pos2, double immutablePosValue, boolean flag) {
      if (this.areaBounds.canFlag(pos2)) {
         if (this.drill.drillActive) {
            flag = false;
         }

         ClientPlayerEntity player = this.minecraftClient.player;
         if (player != null) {
            this.nudgeTowardBlock(player, pos2);
         }

         if (!pos2.equals(this.currentBreakTarget)) {
            this.currentBreakTarget = pos2.toImmutable();
         }

         this.preferDrill = flag;
         if (!this.blockAimer.aimAndSelectTool(pos2, immutablePosValue, flag)) {
            this.blockAimer.clearTarget();
            this.currentBreakTarget = null;
         }
      }
   }

   private void tickDigLocked(World world, BlockPos pos2) {
      this.syntheticInput.clearInput();
      if (!this.isBreakableTarget(world, pos2)) {
         this.blockAimer.clearTarget();
         this.currentBreakTarget = null;
      } else if (!this.isCrosshairOnBlock(pos2)) {
         this.blockAimer.clearTarget();
         this.currentBreakTarget = null;
         this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
      } else {
         ClientPlayerEntity player = this.minecraftClient.player;
         this.aimAndBreakBlock(pos2, Math.max(this.drill.reachDistance, player != null ? player.getBlockInteractionRange() : this.drill.reachDistance), true);
         if (this.currentBreakTarget == null) {
            this.statusText = "lost line of sight \u2014 finding another stand spot";
            this.setTask(AreaMinerController.MiningTask.REPOSITION);
         } else {
            this.statusText = this.taskTicks > 200
               ? "long dig (" + this.taskTicks / 20 + "s) \u2014 still digging"
               : "digging the " + this.layerCursor.getSliceWidth() + "x" + this.layerCursor.getSliceHeight() + " face";
         }
      }
   }

   private boolean isInsideMineableArea(BlockPos pos2) {
      return !this.unreachableBlockSet.contains(pos2) && (this.areaBounds.isPositionAllowed(pos2) || this.navigator != null && this.navigator.getPlacedBlocks().contains(pos2));
   }

   private BlockPos findLeftoverStandSpot(World world, ClientPlayerEntity player, BlockPos pos2) {
      BlockPos blockPos = player.getBlockPos();

      for (int i = 1; i <= 3; i++) {
         BlockPos posBlock = null;
         double posBlockLimit = Double.MAX_VALUE;

         for (int j = -i; j <= i; j++) {
            for (int k = -i; k <= i; k++) {
               if (Math.max(Math.abs(j), Math.abs(k)) == i) {
                  for (int m = -3; m <= 1; m++) {
                     BlockPos add = pos2.add(j, m, k);
                     if (this.areaBounds.canFlag(add)
                        && !this.rejectedStandSpots.contains(add)
                        && StateBlockUtilsCenter.canDown(world, add)
                        && this.blockAimer.canSeeBlockFromStandSpot(add, pos2, this.drill.reachDistance)) {
                        double posBlockValue = add.getSquaredDistance(blockPos) + Math.abs(add.getY() - blockPos.getY()) * 25;
                        if (posBlockValue < posBlockLimit) {
                           posBlockLimit = posBlockValue;
                           posBlock = add;
                        }
                     }
                  }
               }
            }
         }

         if (posBlock != null) {
            return posBlock;
         }
      }

      for (int q = -4; q <= -2; q++) {
         BlockPos pos2Above = pos2.up(q);
         if (this.areaBounds.canFlag(pos2Above)
            && !this.rejectedStandSpots.contains(pos2Above)
            && StateBlockUtilsCenter.canDown(world, pos2Above)
            && this.blockAimer.canSeeBlockFromStandSpot(pos2Above, pos2, this.drill.reachDistance)) {
            return pos2Above;
         }
      }

      return null;
   }

   private void planShaftToCentreColumn(ClientPlayerEntity player) {
      this.resetShaftState();
      if (player != null) {
         BlockPos blockPos = this.layerCursor.getSliceCenterPos();
         int face = this.layerCursor.getFaceCoord();
         if (this.layerCursor.getFaceAxisEnd() - this.layerCursor.getFaceAxisStart() >= 2) {
            face = Math.max(this.layerCursor.getFaceAxisStart() + 1, Math.min(this.layerCursor.getFaceAxisEnd() - 1, face));
         }

         BlockPos blockPos2 = this.layerCursor.toBlockPos(face, this.layerCursor.getTargetRowCoord(), blockPos.getY());
         ClientWorld world = this.minecraftClient.world;
         BlockPos pos3 = null;
         if (world != null) {
            for (int i = this.layerCursor.getCurrentLayerTopY() + 3; i >= this.layerCursor.getCurrentLayerTopY(); i--) {
               BlockPos pos = new BlockPos(blockPos2.getX(), i, blockPos2.getZ());
               if (StateBlockUtilsCenter.canDown(world, pos)) {
                  pos3 = pos;
                  break;
               }
            }
         }

         if (pos3 == null) {
            pos3 = new BlockPos(blockPos2.getX(), this.layerCursor.getCurrentLayerTopY() + 1, blockPos2.getZ());
         }

         this.shaftAlignPos = pos3.toImmutable();
         this.shaftApproachTarget = this.shaftAlignPos;
         this.shaftTargetY = pos3.getY();
      }
   }

   ScriptedMovementInput getSyntheticInput() {
      return this.syntheticInput;
   }

   private boolean tickToolRepair(ClientPlayerEntity player) {
      if (!this.drill.replaceDamagedTool) {
         this.repairingTool = false;
         return false;
      } else if (this.collectingXpOrbs) {
         boolean erepairDamageFlag = this.minecraftClient.world != null
            && !this.minecraftClient.world.getEntitiesByClass(ExperienceOrbEntity.class, player.getBoundingBox().expand(4.0), experienceOrbEntity -> true).isEmpty();
         if (erepairDamageFlag && ++this.xpOrbWaitTicks <= 200) {
            this.syntheticInput.clearInput();
            this.blockAimer.clearTargetAndCancelBreaking();
            this.statusText = "waiting to pick up the XP orbs\u2026";
            return true;
         } else {
            this.collectingXpOrbs = false;
            this.xpOrbWaitTicks = 0;
            return false;
         }
      } else {
         PlayerInventory inventory = player.getInventory();
         if (!this.repairingTool) {
            if (this.navigator.isPillaring() || this.fellBelowLayer) {
               return false;
            }

            int selectedSlot = inventory.getSelectedSlot();
            ItemStack stack = inventory.getStack(selectedSlot);
            if (!stack.isDamageable() || stack.getMaxDamage() - stack.getDamage() > this.drill.minToolDurability) {
               return false;
            }

            if (this.repairGivenUp) {
               if (findItemInHotbar(inventory, Items.EXPERIENCE_BOTTLE) < 0 && findItemInMainInventory(inventory, Items.EXPERIENCE_BOTTLE) < 0) {
                  return false;
               }

               this.repairGivenUp = false;
            }

            this.repairingTool = true;
            this.savedHotbarSlot = selectedSlot;
            this.bottleThrowTicks = 0;
            this.repairNoProgressTicks = 0;
            this.lastToolDamage = Integer.MAX_VALUE;
            this.sendChatMessage(
               "tool has " + (stack.getMaxDamage() - stack.getDamage()) + " durability left \u2014 pausing to throw XP bottles and repair it"
            );
         }

         ItemStack stack2 = inventory.getStack(this.savedHotbarSlot);
         int erepairDamageLimit = stack2.isEmpty() ? -1 : stack2.getDamage();
         if (erepairDamageLimit >= 0 && erepairDamageLimit < this.lastToolDamage) {
            this.repairNoProgressTicks = 0;
         } else if (++this.repairNoProgressTicks > 1200) {
            this.sendChatMessage("\u00a7erepair made no progress for 60s (server blocking it?) \u2014 mining on, will retry later.");
            this.repairGivenUp = true;
            this.restorePendingSwap(player);
            this.repairingTool = false;
            this.repairNoProgressTicks = 0;
            this.lastToolDamage = Integer.MAX_VALUE;
            return false;
         }

         this.lastToolDamage = erepairDamageLimit < 0 ? Integer.MAX_VALUE : erepairDamageLimit;
         if (!stack2.isEmpty() && stack2.isDamageable() && stack2.getDamage() != 0) {
            this.syntheticInput.clearInput();
            this.blockAimer.clearTargetAndCancelBreaking();
            this.currentBreakTarget = null;
            if (inventory.getSelectedSlot() != this.savedHotbarSlot) {
               inventory.setSelectedSlot(this.savedHotbarSlot);
            }

            if (player.getOffHandStack().getItem() != Items.EXPERIENCE_BOTTLE) {
               int itemInHotbar = findItemInHotbar(inventory, Items.EXPERIENCE_BOTTLE);
               int erepairDamageLimit2 = itemInHotbar >= 0 ? 36 + itemInHotbar : findItemInMainInventory(inventory, Items.EXPERIENCE_BOTTLE);
               if (erepairDamageLimit2 >= 0) {
                  this.inventorySyncWaitTicks = 0;
                  this.swapSlotToOffhand(erepairDamageLimit2);
                  this.statusText = "moving a stack of XP bottles to your off hand";
                  return true;
               } else if (++this.inventorySyncWaitTicks <= 20) {
                  this.statusText = "waiting for the inventory to sync\u2026";
                  return true;
               } else {
                  this.inventorySyncWaitTicks = 0;
                  this.sendChatMessage("\u00a7eout of XP bottles \u2014 tool has " + (stack2.getMaxDamage() - stack2.getDamage()) + " durability left.");
                  this.repairGivenUp = true;
                  this.restorePendingSwap(player);
                  this.repairingTool = false;
                  this.collectingXpOrbs = true;
                  this.xpOrbWaitTicks = 0;
                  return true;
               }
            } else {
               player.setPitch(Math.min(90.0F, player.getPitch() + 30.0F));
               if (++this.bottleThrowTicks >= 4) {
                  this.bottleThrowTicks = 0;
                  ActionResult interactItem = this.minecraftClient.interactionManager.interactItem(player, Hand.OFF_HAND);
                  if (interactItem.isAccepted()) {
                     player.swingHand(Hand.OFF_HAND);
                  }
               }

               this.statusText = "throwing XP to repair \u2014 durability " + (stack2.getMaxDamage() - stack2.getDamage()) + "/" + stack2.getMaxDamage();
               return true;
            }
         } else {
            inventory.setSelectedSlot(this.savedHotbarSlot);
            this.restorePendingSwap(player);
            this.repairingTool = false;
            this.inventorySyncWaitTicks = 0;
            if (!stack2.isEmpty() && stack2.isDamageable() && stack2.getDamage() == 0) {
               this.sendChatMessage("tool fully repaired \u2014 collecting the last XP, then mining on");
            }

            this.collectingXpOrbs = true;
            this.xpOrbWaitTicks = 0;
            return true;
         }
      }
   }

   public BlockPos getCurrentCutPos() {
      return this.state == AreaMinerController.State.RUNNING && this.layerCursor != null && !this.layerCursor.isCheckingLeftovers()
         ? this.layerCursor.getSliceCenterPos()
         : null;
   }

   private BlockPos findBestBreakableBlock(World world, ClientPlayerEntity player) {
      BlockPos blockPos = player.getBlockPos();
      int y = this.layerCursor.getSliceCenterPos().getY();
      BlockPos cornerNear = null;
      int cornerNearValue = -1;
      double cornerNearLimit = Double.MAX_VALUE;

      for (int i = this.layerCursor.getCurrentLayerTopY(); i >= this.layerCursor.getSliceBottomY(); i--) {
         for (int j = this.layerCursor.getFaceAxisStart(); j <= this.layerCursor.getFaceAxisEnd(); j++) {
            for (int k = this.layerCursor.getRowAxisMin(); k <= this.layerCursor.getRowAxisMax(); k++) {
               BlockPos pos2 = this.layerCursor.toBlockPos(j, k, i);
               if (this.isRemainingBlock(world, pos2) && !this.skippedBlockCooldowns.containsKey(pos2) && !this.settleCooldownTicks.containsKey(pos2)) {
                  int cornerNearLimit2 = this.countBreakableNeighbours(world, pos2) + (i == y ? 100 : 0);
                  double squaredDistance = pos2.getSquaredDistance(blockPos);
                  if (cornerNearLimit2 > cornerNearValue || cornerNearLimit2 == cornerNearValue && squaredDistance < cornerNearLimit) {
                     cornerNearValue = cornerNearLimit2;
                     cornerNearLimit = squaredDistance;
                     cornerNear = pos2.toImmutable();
                  }
               }
            }
         }
      }

      return cornerNear;
   }

   private boolean isBreakableNonSupport(World world, BlockPos pos2) {
      return this.isRemainingBlock(world, pos2) && !this.isSupportBlockBelowPlayer(pos2);
   }

   private void tickAimLock(World world, BlockPos pos2) {
      this.syntheticInput.clearInput();
      this.currentBreakTarget = null;
      if (this.isBreakableTarget(world, pos2)) {
         ClientPlayerEntity player = this.minecraftClient.player;
         double aimingDigValue = player != null ? Math.max(this.drill.reachDistance, player.getBlockInteractionRange()) : this.drill.reachDistance;
         if (!this.blockAimer.aimAtBlock(pos2, aimingDigValue)) {
            this.setTask(AreaMinerController.MiningTask.REPOSITION);
         } else if (player == null || this.isAlignedAndFarFromCell(player, pos2) || !this.tickCentreAlignment(player)) {
            if (this.isCrosshairOnBlock(pos2)) {
               this.setTask(AreaMinerController.MiningTask.DIG_LOCKED);
            } else {
               this.statusText = this.taskTicks > 200 ? "still aiming (" + this.taskTicks / 20 + "s)" : "aiming at the dig cell";
            }
         }
      }
   }

   public BlockPos getCurrentBreakTarget() {
      return this.currentBreakTarget;
   }

   void clearBreakTarget() {
      this.currentBreakTarget = null;
      this.blockAimer.clearTarget();
   }

   public MinerPathNavigator getNavigator() {
      return this.navigator;
   }

   public boolean isCrosshairOnAimTarget() {
      if (this.state != AreaMinerController.State.RUNNING) {
         return false;
      } else {
         BlockPos immutable = this.blockAimer.getCurrentTarget();
         return immutable != null && this.isCrosshairOnBlock(immutable);
      }
   }

   void releaseAimTargets() {
      this.currentBreakTarget = null;
      this.blockAimer.clearTargetAndCancelBreaking();
   }

   private void resetCentreApproach() {
      this.task = AreaMinerController.MiningTask.APPROACH;
      this.taskTicks = 0;
      this.idleStuckTicks = 0;
      this.standSpotCandidate = null;
      this.boringIntoFace = this.boreRowsRemaining > 0;
      this.rejectedStandSpots.clear();
   }

   private boolean isAlignedAndFarFromCell(ClientPlayerEntity player, BlockPos pos2) {
      int cornerNearValue = this.layerCursor.getTargetRowCoord();
      double spotValue = this.layerCursor.faceAxis == Axis.X ? player.getZ() - (cornerNearValue + 0.5) : player.getX() - (cornerNearValue + 0.5);
      return Math.abs(spotValue) > 0.22 ? false : Math.abs(this.getCentreAxisOffset(player, pos2)) >= 0.7;
   }

   private void restorePlayerInput() {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player != null && this.savedPlayerInput != null && player.input == this.syntheticInput) {
         player.input = this.savedPlayerInput;
      }

      this.savedPlayerInput = null;
      this.syntheticInput.clearInput();
      this.syntheticInput.clearHoldState();
   }

   public String startMining() {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player == null || this.minecraftClient.world == null) {
         return "not in a world";
      } else if (this.state == AreaMinerController.State.RUNNING) {
         return "already mining";
      } else if (this.state == AreaMinerController.State.PAUSED) {
         this.resumeMining();
         this.sendChatMessage("resuming from where it paused");
         return null;
      } else if (!this.areaBounds.hasSelection()) {
         return "set both corners first";
      } else {
         boolean posBlockFlag = this.layerCursor != null && !this.layerCursor.isFinished() && this.layerCursor.matchesSelection(this.areaBounds);
         if (posBlockFlag) {
            this.sendChatMessage("continuing the previous run \u2014 " + this.layerCursor.getProgressDescription());
            this.lastCentreCell = null;
            this.boreRowsRemaining = 0;
            this.resetShaftState();
            this.straightCutQueue.clear();
            this.straightCutStandSpot = null;
            this.currentStraightCut = null;
            this.straightCutsDone = 0;
            this.resetCentreApproach();
            this.resetNavigationTargets();
         } else {
            this.stopMining();
            boolean posBlockFlag2 = this.areaBounds.getSizeX() >= this.areaBounds.getSizeZ();
            BlockPos blockPos = player.getBlockPos();
            int posBlockValue = posBlockFlag2 ? blockPos.getX() : blockPos.getZ();
            int posBlockValue2 = posBlockFlag2 ? blockPos.getZ() : blockPos.getX();
            int posBlockValue3 = posBlockFlag2 ? this.areaBounds.getMinX() : this.areaBounds.getMinZ();
            int posBlockValue4 = posBlockFlag2 ? this.areaBounds.getMaxX() : this.areaBounds.getMaxZ();
            int posBlockValue5 = posBlockFlag2 ? this.areaBounds.getMinZ() : this.areaBounds.getMinX();
            int posBlockValue6 = posBlockFlag2 ? this.areaBounds.getMaxZ() : this.areaBounds.getMaxX();
            this.layerCursor = new AreaLayerCursor(
               this.areaBounds,
               this.drill.layerHeight,
               this.drill.passWidth,
               Math.abs(posBlockValue2 - posBlockValue6) < Math.abs(posBlockValue2 - posBlockValue5),
               Math.abs(posBlockValue - posBlockValue4) < Math.abs(posBlockValue - posBlockValue3)
            );
            this.navigator = new MinerPathNavigator(this.minecraftClient, this.drill, this.syntheticInput, this.blockAimer, this.areaBounds);
            this.minedBlockCount = 0;
            this.skippedBlockCount = 0;
            this.currentRowId = -1;
            this.pillaringUp = false;
            this.task = AreaMinerController.MiningTask.APPROACH;
            this.lastCentreCell = null;
            this.taskTicks = 0;
            this.standSpotCandidate = null;
            this.boreRowsRemaining = 0;
            this.rejectedStandSpots.clear();
            this.pillarBackTicks = 0;
            this.pillarBackCooldownTicks = 0;
            this.unreachableBlockSet.clear();
            this.breakAttemptCounts.clear();
            this.blockAimer.clearOutlineAimBlocks();
            this.fellBelowLayer = false;
            this.noHotbarBlocksWarned = false;
            this.statusText = "";
            this.plugCandidatePositions.clear();
            this.placedPlugPositions.clear();
            this.lavaPlugPositions.clear();
            this.minedAirPositions.clear();
            this.straightCutQueue.clear();
            this.straightCutStandSpot = null;
            this.resetShaftState();
            this.resetNavigationTargets();
         }

         this.resetRepairAndSweepState();
         this.layerRetryCount = 0;
         this.drillSweepWarned = false;
         this.finalCheckPhaseActive = false;
         this.finalScanActive = false;
         this.finalCheckBlocks.clear();
         this.initialScanDelayTicks = 20;
         this.savedPlayerInput = player.input;
         player.input = this.syntheticInput;
         this.state = AreaMinerController.State.RUNNING;
         if (!this.drill.drillActive && this.blockAimer.findDrillSlot(player) < 0) {
            this.sendChatMessage(
               "\u00a7eno hotbar item matches Drill Name \""
                  + this.drill.drillToolName
                  + "\" \u2014 using your fastest tool for everything. Set Drill Name (Mining group) to part of your 3x3 tool's name, e.g. Shard."
            );
         }

         return null;
      }
   }

   public void resumeMining() {
      if (this.state == AreaMinerController.State.PAUSED) {
         this.state = AreaMinerController.State.RUNNING;
         this.autoPaused = false;
         this.plugCandidatePositions.clear();
         this.minedAirPositions.clear();
         this.straightCutQueue.clear();
         this.straightCutStandSpot = null;
         this.resetShaftState();
         this.lastCentreCell = null;
         this.boreRowsRemaining = 0;
         this.resetCentreApproach();
         this.resetNavigationTargets();
      } else if (this.state == AreaMinerController.State.IDLE && this.layerCursor != null && !this.layerCursor.isFinished()) {
         String formatPosBlock = this.startMining();
         if (formatPosBlock != null) {
            this.sendChatMessage("\u00a7c" + formatPosBlock);
         }
      }
   }

   private boolean isCrosshairOnBlock(BlockPos pos2) {
      return this.minecraftClient.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(pos2);
   }

   private void tickApproach(World world, BlockPos pos) {
      ClientPlayerEntity player = this.minecraftClient.player;
      boolean spotStandFlag = player != null && StateBlockUtilsCenter.canSquareState(world, player.getBlockPos().down());
      double spotStandValue = 0.0;
      if (player != null && this.layerCursor != null) {
         double spotStandValue2 = this.layerCursor.getTargetRowCoord() + 0.5;
         spotStandValue = (this.layerCursor.faceAxis == Axis.X ? player.getZ() : player.getX()) - spotStandValue2;
      }

      boolean spotStandFlag2 = player != null
         && spotStandFlag
         && !this.navigator.isPillaring()
         && this.blockAimer.canReachBlock(pos, this.drill.reachDistance)
         && (this.boreRowsRemaining > 0 || this.canSeeCellFromHere(player, pos));
      if (spotStandFlag2 && Math.abs(spotStandValue) <= 0.22) {
         if (this.navigator.isBridging()) {
            this.navigator.resetBridging();
         }

         this.syntheticInput.clearInput();
         this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
      } else {
         if (spotStandFlag2 && Math.abs(spotStandValue) <= 0.35) {
            if (this.navigator.isBridging()) {
               this.navigator.resetBridging();
            }

            if (this.tickCentreAlignment(player)) {
               this.currentBreakTarget = null;
               return;
            }

            if (Math.abs(spotStandValue) <= 0.22) {
               this.syntheticInput.clearInput();
               this.boreRowsRemaining = 0;
               this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
               return;
            }
         }

         BlockPos blockPos;
         if (this.boringIntoFace) {
            blockPos = this.layerCursor.getCurrentTargetPos();
         } else {
            int faceAxisStart = this.layerCursor.getFaceAxisStart();
            int faceAxisEnd = this.layerCursor.getFaceAxisEnd();
            if (faceAxisEnd - faceAxisStart >= 2 && (this.layerCursor.getFaceCoord() == faceAxisStart || this.layerCursor.getFaceCoord() == faceAxisEnd)) {
               blockPos = this.layerCursor.toBlockPos(this.getRowCutColumn(), this.layerCursor.getTargetRowCoord(), this.layerCursor.getSliceBottomY());
            } else {
               blockPos = this.layerCursor.getPreviousFacePos();
            }
         }

         if (player == null || !this.tickClearPlugAtStandSpot(world, player, blockPos)) {
            if (player == null || !StateBlockUtilsCenter.canStateBlock(world, blockPos.down()) && !this.isStuckApproaching(player, blockPos)) {
               MinerPathNavigator.Result movingImmutable = this.navigator.moveToward(blockPos);
               if (movingImmutable == MinerPathNavigator.Result.MOVING) {
                  this.statusText = this.navigator.isPillaring()
                     ? "climbing up"
                     : (this.navigator.isBridging() ? "bridging" : (this.boringIntoFace ? "boring into the face" : "walking to the stand spot"));
                  this.currentBreakTarget = null;
               } else if (movingImmutable == MinerPathNavigator.Result.BLOCKED) {
                  if (player == null || !this.tryClearObstacle(world, player, pos, Math.max(this.drill.reachDistance, player.getBlockInteractionRange()))) {
                     if (this.boreRowsRemaining == 0 && !this.boringIntoFace) {
                        this.boreRowsRemaining = 3;
                        this.boringIntoFace = true;
                        this.navigator.resetNavigation();
                        this.statusText = "mining the 9 cells in front";
                     } else {
                        this.statusText = "can't reach the stand spot \u2014 trying another";
                        this.setTask(AreaMinerController.MiningTask.REPOSITION);
                     }
                  }
               } else {
                  this.syntheticInput.clearInput();
                  this.boreRowsRemaining = 0;
                  this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
               }
            } else {
               this.rejectedStandSpots.add(blockPos.toImmutable());
               this.navigator.resetNavigation();
               this.progressTrackedSpot = null;
               if (++this.unsafeSpotFailCount >= 3) {
                  this.unsafeSpotFailCount = 0;
                  this.skippedBlockCooldowns.put(pos.toImmutable(), 1200);
                  this.statusText = "no safe spot to mine this cut from \u2014 leaving it for the leftover sweep";
               } else {
                  this.setTask(AreaMinerController.MiningTask.REPOSITION);
                  this.statusText = StateBlockUtilsCenter.canStateBlock(world, blockPos.down())
                     ? "the stand spot is over lava \u2014 mining from the side instead"
                     : "can't get to the stand spot \u2014 trying another";
               }
            }
         }
      }
   }

   private void tickLayerPhase(ClientPlayerEntity player, World world) {
      if (this.finalCheckPhaseActive) {
         this.tickFinalCheck(player, world);
      } else if (!this.layerCursor.isCheckingLeftovers()) {
         this.tickCentreColumnPhase(world);
      } else if (!this.tickLayerRetry(player, world)) {
         int layerCountOffset = this.layerCursor.getLayerIndex() + 1;
         this.resetNavigationTargets();
         this.plugCandidatePositions.clear();
         this.minedAirPositions.clear();
         this.navigator.clearPlacedBlocks();
         this.straightCutQueue.clear();
         this.layerRemineTriggered = false;
         if (this.layerCursor.advanceToNextLayer()) {
            this.sendChatMessage("layer " + layerCountOffset + " clear \u2014 moving down to layer " + (this.layerCursor.getLayerIndex() + 1));
            this.statusText = "";
            this.shaftRouteRetries = 0;
            this.planShaftToCentreColumn(player);
         } else if (this.drill.scanLeftovers) {
            this.beginFinalScan();
         } else {
            this.finishMining(-1);
         }
      }
   }

   public void pauseMining() {
      this.autoPaused = false;
      if (this.state == AreaMinerController.State.RUNNING) {
         this.state = AreaMinerController.State.PAUSED;
         this.syntheticInput.clearInput();
         this.blockAimer.clearTargetAndCancelBreaking();
      }
   }

   private void abortBreaking() {
      this.currentBreakTarget = null;
      this.blockAimer.clearTargetAndCancelBreaking();
   }

   public void stopMining() {
      this.restorePlayerInput();
      this.blockAimer.clearTargetAndCancelBreaking();
      this.currentBreakTarget = null;
      this.autoPaused = false;
      this.inventoryFood.resetEating();
      this.playerEntityHelper.resetState();
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player != null) {
         this.restorePendingSwap(player);
      }

      this.repairingTool = false;
      this.state = this.layerCursor != null && this.layerCursor.isFinished() ? AreaMinerController.State.DONE : AreaMinerController.State.IDLE;
   }

   public AreaMinerController(MinecraftClient minecraftClient, Drill drill, BlockRegionSelection posBlock) {
      this.state = AreaMinerController.State.IDLE;
      this.plugCandidatePositions = new HashSet<>();
      this.placedPlugPositions = new HashSet<>();
      this.lavaPlugPositions = new HashSet<>();
      this.finalCheckBlocks = new ArrayList<>();
      this.minedAirPositions = new HashSet<>();
      this.task = AreaMinerController.MiningTask.APPROACH;
      this.currentRowId = -1;
      this.straightCutQueue = new ArrayDeque<>();
      this.unreachableBlockSet = new HashSet<>();
      this.breakAttemptCounts = new HashMap<>();
      this.skippedBlockCooldowns = new HashMap<>();
      this.settleCooldownTicks = new HashMap<>();
      this.serverRestoreCounts = new HashMap<>();
      this.rejectedStandSpots = new HashSet<>();
      this.statusText = "";
      this.savedHotbarSlot = -1;
      this.lastToolDamage = Integer.MAX_VALUE;
      this.queuedSwapSlot = -1;
      this.trackedSwapSlot = -1;
      this.minecraftClient = minecraftClient;
      this.drill = drill;
      this.areaBounds = posBlock;
      this.blockAimer = new BlockAimController(minecraftClient, drill);
      this.playerEntityHelper = new FluidSafetyController(this, minecraftClient);
      this.inventoryFood = new HotbarFoodEater(minecraftClient, drill);
      this.navigator = new MinerPathNavigator(minecraftClient, drill, this.syntheticInput, this.blockAimer, posBlock);
      this.blockAimer.setBreakGuard(this::allowsBreakingBlock);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> this.restoreSwappedItem());
   }

   private BlockPos getNextFinalCheckBlock(ClientPlayerEntity player) {
      BlockPos blockPos = player.getBlockPos();
      BlockPos next = null;
      double blockPosLimit = Double.MAX_VALUE;

      for (BlockPos obsidianIdleValue : this.finalCheckBlocks) {
         if (!this.skippedBlockCooldowns.containsKey(obsidianIdleValue) && !this.settleCooldownTicks.containsKey(obsidianIdleValue)) {
            double blockPosValue = obsidianIdleValue.getSquaredDistance(blockPos) - obsidianIdleValue.getY() * 0.5;
            if (blockPosValue < blockPosLimit) {
               blockPosLimit = blockPosValue;
               next = obsidianIdleValue;
            }
         }
      }

      return next;
   }

   public AreaMinerController.State getState() {
      return this.state;
   }

   private double getCentreAxisOffset(ClientPlayerEntity player, BlockPos pos2) {
      return this.layerCursor.faceAxis == Axis.X ? player.getX() - (pos2.getX() + 0.5) : player.getZ() - (pos2.getZ() + 0.5);
   }

   private static float toInputAxisFine(double blockPosLimit) {
      if (Math.abs(blockPosLimit) < 0.04) {
         return 0.0F;
      } else {
         return blockPosLimit > 0.0 ? 1.0F : -1.0F;
      }
   }

   private static boolean isWithinPitchLimit(ClientPlayerEntity player, BlockPos pos2, double eyePosLimit) {
      Vec3d eyePos = player.getEyePos();
      double y = pos2.getY() + 0.5 - eyePos.y;
      double hypot = Math.hypot(pos2.getX() + 0.5 - eyePos.x, pos2.getZ() + 0.5 - eyePos.z);
      return Math.abs(Math.toDegrees(Math.atan2(y, hypot))) <= eyePosLimit;
   }

   public boolean isActive() {
      return this.state == AreaMinerController.State.RUNNING || this.state == AreaMinerController.State.PAUSED;
   }

   private static int findItemInMainInventory(PlayerInventory playerInventory, Item item2) {
      for (int i = 9; i < 36; i++) {
         if (playerInventory.getStack(i).getItem() == item2) {
            return i;
         }
      }

      return -1;
   }

   private Direction getDigDirection() {
      if (this.layerCursor.faceAxis == Axis.X) {
         return this.layerCursor.getFaceDirection() > 0 ? Direction.EAST : Direction.WEST;
      } else {
         return this.layerCursor.getFaceDirection() > 0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   void setStatusText(String spotStandText) {
      this.statusText = spotStandText;
   }

   private BlockPos findStandSpotFor(World world, ClientPlayerEntity player, BlockPos pos2) {
      if (this.layerCursor != null) {
         BlockPos cornerNear = !this.straightCutQueue.isEmpty()
            ? this.layerCursor.toBlockPos(this.getRowCutColumn(), this.layerCursor.getTargetRowCoord(), this.layerCursor.getSliceBottomY())
            : this.layerCursor.getPreviousFacePos();
         if (!this.rejectedStandSpots.contains(cornerNear)
            && StateBlockUtilsCenter.canDown(world, cornerNear)
            && this.blockAimer.canSeeBlockFromStandSpot(cornerNear, pos2, this.drill.reachDistance)) {
            return cornerNear;
         }
      }

      int cornerNearValue = this.layerCursor != null ? this.layerCursor.getTargetRowCoord() : -1;
      BlockPos cornerNear2 = null;
      double cornerNearLimit = Double.MAX_VALUE;

      for (int i = 0; i <= 2; i++) {
         if (i == 0) {
            for (int j = -4; j <= -1; j++) {
               BlockPos up = pos2.up(j);
               if (this.areaBounds.canFlag(up)
                  && !this.rejectedStandSpots.contains(up)
                  && StateBlockUtilsCenter.canDown(world, up)
                  && this.blockAimer.canSeeBlockFromStandSpot(up, pos2, this.drill.reachDistance)) {
                  double cornerNearValue2 = up.getSquaredDistance(player.getBlockPos()) + Math.abs(up.getY() - player.getBlockY()) * 25;
                  if (cornerNearValue2 < cornerNearLimit) {
                     cornerNearLimit = cornerNearValue2;
                     cornerNear2 = up;
                  }
               }
            }
         } else {
            for (Direction direction : net.minecraft.util.math.Direction.Type.HORIZONTAL) {
               for (int k = -4; k <= 1; k++) {
                  BlockPos up2 = pos2.offset(direction, i).up(k);
                  if (this.areaBounds.canFlag(up2)
                     && !this.rejectedStandSpots.contains(up2)
                     && StateBlockUtilsCenter.canDown(world, up2)
                     && this.blockAimer.canSeeBlockFromStandSpot(up2, pos2, this.drill.reachDistance)) {
                     int cornerNearValue3 = this.layerCursor != null && this.layerCursor.faceAxis == Axis.X ? up2.getZ() : up2.getX();
                     int cornerNearValue4 = cornerNearValue != -1 ? Math.abs(cornerNearValue3 - cornerNearValue) : 0;
                     if (cornerNearValue4 <= 1) {
                        double cornerNearValue5 = up2.getSquaredDistance(player.getBlockPos()) + Math.abs(up2.getY() - player.getBlockY()) * 25 + cornerNearValue4 * 500;
                        if (cornerNearValue5 < cornerNearLimit) {
                           cornerNearLimit = cornerNearValue5;
                           cornerNear2 = up2;
                        }
                     }
                  }
               }
            }
         }

         if (cornerNear2 != null) {
            return cornerNear2;
         }
      }

      BlockPos up3 = pos2.up();
      return !this.rejectedStandSpots.contains(up3) && StateBlockUtilsCenter.canDown(world, up3) ? up3 : null;
   }

   private void tickMining() {
      if (this.state == AreaMinerController.State.RUNNING) {
         ClientPlayerEntity player = this.minecraftClient.player;
         ClientWorld world = this.minecraftClient.world;
         if (player != null && world != null) {
            if (player.input != this.syntheticInput) {
               this.savedPlayerInput = player.input;
               player.input = this.syntheticInput;
            }

            this.syntheticInput.clearInput();
            if (!this.playerEntityHelper.runSafetyTick()) {
               if (this.currentBreakTarget != null && world.getBlockState(this.currentBreakTarget).isAir()) {
                  this.minedBlockCount++;
                  this.markNeighboursSettling(this.currentBreakTarget);
                  BlockPos immutable = this.currentBreakTarget.toImmutable();
                  int merge = this.serverRestoreCounts.merge(immutable, 1, Integer::sum);
                  if (merge >= 3) {
                     this.skippedBlockCooldowns.put(immutable, 2400);
                     this.serverRestoreCounts.remove(immutable);
                     this.sendChatMessage("\u00a7e" + immutable.toShortString() + " \u2014 the server keeps putting it back, skipping it for now");
                  }

                  this.currentBreakTarget = null;
                  this.blockAimer.clearTarget();
               }

               if (!this.skippedBlockCooldowns.isEmpty()) {
                  this.skippedBlockCooldowns.replaceAll((pos, integer) -> integer - 1);
                  this.skippedBlockCooldowns.values().removeIf(integer2 -> integer2 <= 0);
               }

               if (!this.settleCooldownTicks.isEmpty()) {
                  this.settleCooldownTicks.replaceAll((pos2, integer3) -> integer3 - 1);
                  this.settleCooldownTicks.values().removeIf(integer4 -> integer4 <= 0);
               }

               if (++this.restoreCountResetTicks >= 600) {
                  this.restoreCountResetTicks = 0;
                  this.serverRestoreCounts.clear();
               }

               if (this.shaftApproachTarget != null || this.inCentreColumn) {
                  this.shaftPhaseTicks++;
               }

               if (!this.straightCutQueue.isEmpty()) {
                  this.straightCutTicks++;
               }

               double deltaX = player.getX() - this.x;
               double deltaY = player.getY() - this.lastPlayerY;
               double deltaZ = player.getZ() - this.lastPlayerZ;
               this.playerMoving = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 0.0025;
               this.x = player.getX();
               this.lastPlayerY = player.getY();
               this.lastPlayerZ = player.getZ();
               if (this.initialScanDelayTicks <= 0) {
                  this.navigator.setOverrideY(this.layerCursor.getSliceCenterPos().getY());
                  this.navigator.setMaxReach(player.getBlockInteractionRange());
                  if (!this.tickToolRepair(player)) {
                     if (!this.tickPillarBack(player)) {
                        if (this.pillaringUp && !this.navigator.isPillaring()) {
                           this.setTask(AreaMinerController.MiningTask.AIM_LOCK);
                        }

                        this.pillaringUp = this.navigator.isPillaring();
                        if (this.drill.placeBlocks && !this.noHotbarBlocksWarned && this.navigator.isPillaring() && !PillarUpPlacer.hasOpaqueBlockInHotbar(player)) {
                           this.noHotbarBlocksWarned = true;
                           this.sendChatMessage("\u00a7eno solid block in your hotbar to pillar up with \u2014 put cobblestone/deepslate in your hotbar.");
                        }

                        if (!this.tickBreakTarget(world, player)) {
                           if (!this.tickShaftDescent(world, player)) {
                              this.tickLayerPhase(player, world);
                           }
                        }
                     }
                  }
               } else {
                  this.initialScanDelayTicks--;
                  this.syntheticInput.clearInput();
                  this.blockAimer.clearTargetAndCancelBreaking();
                  int cornerNearCounter = 0;

                  while (this.countBreakableBlocksUpTo(world, 1) == 0 && cornerNearCounter++ < this.layerCursor.getTotalLayers()) {
                     int layerCountOffset = this.layerCursor.getLayerIndex() + 1;
                     if (!this.layerCursor.advanceToNextLayer()) {
                        this.sendChatMessage("the whole area is already clear \u2014 nothing to mine");
                        this.finishMining(0);
                        return;
                     }

                     this.sendChatMessage("layer " + layerCountOffset + " is already clear \u2014 skipping to layer " + (this.layerCursor.getLayerIndex() + 1));
                     this.resetNavigationTargets();
                     this.straightCutQueue.clear();
                     this.resetShaftState();
                  }

                  if (this.initialScanDelayTicks == 0 && this.layerCursor.isCheckingLeftovers() && !this.layerRemineTriggered && this.countBreakableBlocksUpTo(world, 20) >= 20) {
                     this.layerRemineTriggered = true;
                     this.layerCursor.resetLayerProgress();
                     this.lastCentreCell = null;
                     this.boreRowsRemaining = 0;
                     this.resetCentreApproach();
                     this.resetNavigationTargets();
                     this.sendChatMessage(
                        "layer " + (this.layerCursor.getLayerIndex() + 1) + " still has lots of blocks \u2014 re-mining it by 9-cell centres"
                     );
                  }

                  if (!this.layerCursor.isCheckingLeftovers()) {
                     int cornerNearCounter2 = 0;

                     while (!this.hasCentreBlockToMine(world) && !this.layerCursor.isCheckingLeftovers() && cornerNearCounter2++ < 4096) {
                        this.layerCursor.advanceFaceStep();
                     }
                  }

                  if (this.initialScanDelayTicks == 0 && !this.layerCursor.isCheckingLeftovers() && player.getBlockY() > this.layerCursor.getSliceCenterPos().getY()) {
                     int cornerNearCounter3 = 0;

                     while (!this.layerCursor.isCheckingLeftovers() && !this.isCellFullyBreakable(world) && cornerNearCounter3++ < 4096) {
                        this.layerCursor.advanceFaceStep();
                     }

                     if (!this.layerCursor.isCheckingLeftovers() && !this.isCentreColumnClear(world)) {
                        this.shaftRouteRetries = 0;
                        this.planShaftToCentreColumn(player);
                        this.sendChatMessage(
                           "standing above layer "
                              + (this.layerCursor.getLayerIndex() + 1)
                              + " \u2014 heading to the centre column to open a shaft, then mining on"
                        );
                     }
                  }

                  this.statusText = "scanning the area from corner 1 \u2192 corner 2\u2026";
               }
            }
         } else {
            this.stopMining();
         }
      }
   }

   private void recordAirAround(BlockPos pos) {
      ClientWorld world = this.minecraftClient.world;
      if (world != null) {
         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 1; k++) {
                  BlockPos add = pos.add(i, j, k);
                  if (world.getBlockState(add).isAir()) {
                     this.minedAirPositions.add(add);
                  }
               }
            }
         }
      }
   }

   private static int findItemInHotbar(PlayerInventory playerInventory, Item item2) {
      for (int i = 0; i < 9; i++) {
         if (playerInventory.getStack(i).getItem() == item2) {
            return i;
         }
      }

      return -1;
   }

   public String describeFluidAndPlugStats() {
      ClientWorld world = this.minecraftClient.world;
      if (world != null && this.areaBounds.hasSelection()) {
         int posBlockCounter = 0;
         int posBlockCounter2 = 0;
         int posBlockCounter3 = 0;
         int posBlockCounter4 = 0;
         int posBlockCounter5 = 0;
         int posBlockCounter6 = 0;
         int posBlockCounter7 = 0;
         Mutable mutable = new Mutable();

         for (int i = this.areaBounds.getMinX() - 1; i <= this.areaBounds.getMaxX() + 1; i++) {
            for (int j = this.areaBounds.getMinY() - 1; j <= this.areaBounds.getMaxY() + 1; j++) {
               for (int k = this.areaBounds.getMinZ() - 1; k <= this.areaBounds.getMaxZ() + 1; k++) {
                  mutable.set(i, j, k);
                  FluidState fluidState = world.getFluidState(mutable);
                  boolean fluid = BlockStateUtilsFluid.isLava(fluidState);
                  if (!fluid && !BlockStateUtilsFluid.isWater(fluidState)) {
                     if (this.areaBounds.canFlag(mutable) && this.isRemainingBlock(world, mutable)) {
                        posBlockCounter7++;
                     }
                  } else {
                     boolean posBlockFlag = fluidState.isStill() && BlockStateUtilsFluid.hasReplaceableNeighbor(world, mutable, fluidState);
                     if (fluid) {
                        if (fluidState.isStill()) {
                           posBlockCounter++;
                        } else {
                           posBlockCounter2++;
                        }

                        if (posBlockFlag) {
                           posBlockCounter5++;
                        }
                     } else {
                        if (fluidState.isStill()) {
                           posBlockCounter3++;
                        } else {
                           posBlockCounter4++;
                        }

                        if (posBlockFlag) {
                           posBlockCounter6++;
                        }
                     }
                  }
               }
            }
         }

         return "lavaSrc="
            + posBlockCounter
            + " lavaFlow="
            + posBlockCounter2
            + " openLavaSrc="
            + posBlockCounter5
            + " waterSrc="
            + posBlockCounter3
            + " waterFlow="
            + posBlockCounter4
            + " openWaterSrc="
            + posBlockCounter6
            + " minableLeft="
            + posBlockCounter7
            + " plugsHeld="
            + this.countHeldPlugs(world)
            + " plugsPlaced="
            + this.placedPlugPositions.size();
      } else {
         return "no area";
      }
   }

   private void nudgeTowardBlock(ClientPlayerEntity player, BlockPos pos2) {
      if (this.layerCursor != null && !this.layerCursor.isCheckingLeftovers() && player.isOnGround()) {
         Vec3d eyePos = player.getEyePos();
         double drillCornerFactor = pos2.getX() + 0.5 - player.getX();
         double deltaZ = pos2.getZ() + 0.5 - player.getZ();
         double hypot = Math.hypot(drillCornerFactor, deltaZ);
         double drillCornerValue = pos2.getY() + 0.5 - eyePos.y;
         if (!(Math.abs(drillCornerValue) > 1.6)) {
            boolean drillCornerFlag = pos2.getY() >= player.getBlockY();
            if (drillCornerFlag || !(hypot < 1.1)) {
               ClientWorld world = this.minecraftClient.world;
               if (world != null) {
                  int drillCornerValue2 = drillCornerFactor > 0.2 ? 1 : (drillCornerFactor < -0.2 ? -1 : 0);
                  int drillCornerValue3 = deltaZ > 0.2 ? 1 : (deltaZ < -0.2 ? -1 : 0);
                  BlockPos blockPos = player.getBlockPos();
                  if (drillCornerValue2 != 0 && !StateBlockUtilsCenter.canSquareState(world, blockPos.add(drillCornerValue2, -1, 0))
                     || drillCornerValue3 != 0 && !StateBlockUtilsCenter.canSquareState(world, blockPos.add(0, -1, drillCornerValue3))
                     || drillCornerValue2 != 0 && drillCornerValue3 != 0 && !StateBlockUtilsCenter.canSquareState(world, blockPos.add(drillCornerValue2, -1, drillCornerValue3))) {
                     return;
                  }
               }

               double angle = Math.toRadians(player.getYaw());
               double sin = Math.sin(angle);
               double cos = Math.cos(angle);
               double blockPosLimit = deltaZ * cos - drillCornerFactor * sin;
               double spotValue = deltaZ * sin + drillCornerFactor * cos;
               boolean drillCornerFlag2 = Math.abs(spotValue) > Math.abs(blockPosLimit);
               boolean drillCornerFlag3 = this.drill.sprintToTarget && hypot > 2.2;
               this.syntheticInput.setMovementInput(toInputAxisCoarse(blockPosLimit), drillCornerFlag2 ? toInputAxisCoarse(spotValue) : 0.0F, false, drillCornerFlag3);
            }
         }
      }
   }

   private boolean tryClearObstacle(World world, ClientPlayerEntity player, BlockPos pos2, double obstacleClearingValue) {
      BlockHitResult raycast = world.raycast(new RaycastContext(player.getEyePos(), Vec3d.ofCenter(pos2), ShapeType.OUTLINE, FluidHandling.NONE, player));
      if (raycast.getType() == Type.BLOCK && !raycast.getBlockPos().equals(pos2)) {
         BlockPos blockPos = raycast.getBlockPos();
         if (this.areaBounds.canFlag(blockPos)
            && StateBlockUtilsCenter.canHardnessState(world, blockPos)
            && this.blockAimer.canReachBlock(blockPos, obstacleClearingValue)
            && !this.isSupportBlockBelowPlayer(blockPos)
            && !this.isPlugHoldingFluid(world, blockPos)
            && !this.skippedBlockCooldowns.containsKey(blockPos)) {
            this.syntheticInput.clearInput();
            this.aimAndBreakBlock(blockPos, obstacleClearingValue, true);
            this.statusText = "clearing an obstacle to reach the dig cell";
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean canSeeCellFromHere(ClientPlayerEntity player, BlockPos pos2) {
      return player.getBlockY() == this.layerCursor.getSliceBottomY() || isWithinPitchLimit(player, pos2, 30.0);
   }

   private void restorePendingSwap(ClientPlayerEntity player) {
      if (this.queuedSwapSlot >= 0) {
         this.trackedSwapSlot = this.queuedSwapSlot;
         this.pendingRestoreItem = this.offhandItemBeforeSwap;
         this.swapPlayer = player;
         this.queuedSwapSlot = -1;
         this.offhandItemBeforeSwap = null;
         this.restoreSwappedItem();
      }
   }

   void registerPlugBlock(BlockPos pos2, boolean posBlockFlag) {
      this.plugCandidatePositions.add(pos2);
      this.placedPlugPositions.add(pos2);
      if (posBlockFlag) {
         this.lavaPlugPositions.add(pos2);
      } else {
         this.lavaPlugPositions.remove(pos2);
      }

      this.navigator.getBlockPlacer().recordPlacedPosition(pos2);
   }

   private void finishMining(int plugBlockBound) {
      if (!this.unreachableBlockSet.isEmpty()) {
         plugBlockBound = Math.max(plugBlockBound, 0) + this.unreachableBlockSet.size();
      }

      this.finalCheckPhaseActive = false;
      this.finalScanActive = false;
      this.finalCheckBlocks.clear();
      String plugBlockText;
      if (plugBlockBound > 0) {
         plugBlockText = " \u2014 \u00a7e" + plugBlockBound + " block" + (plugBlockBound == 1 ? "" : "s") + " could not be reached\u00a7r (no spot to stand on that can see them)";
      } else if (plugBlockBound == 0) {
         plugBlockText = " \u2014 the area is clear";
      } else {
         plugBlockText = "";
      }

      int plugBlockLimit = this.minecraftClient.world == null ? 0 : this.countHeldPlugs(this.minecraftClient.world);
      if (plugBlockLimit > 0) {
         plugBlockText = plugBlockText + " \u2014 \u00a7e" + plugBlockLimit + " plug block" + (plugBlockLimit == 1 ? " was" : "s were") + " left in place to hold back water/lava\u00a7r";
      }

      this.sendChatMessage("done! Mined " + this.minedBlockCount + " blocks" + plugBlockText + ".");
      this.restorePlayerInput();
      this.blockAimer.clearTargetAndCancelBreaking();
      this.currentBreakTarget = null;
      this.state = AreaMinerController.State.DONE;
   }

   private boolean isRemainingBlock(World world, BlockPos pos2) {
      return this.areaBounds.canFlag(pos2) && this.isInsideMineableArea(pos2) && !world.getBlockState(pos2).isAir() && !this.isPlugHoldingFluid(world, pos2)
         ? StateBlockUtilsCenter.canHardnessState(world, pos2)
         : false;
   }

   private boolean isStuckApproaching(ClientPlayerEntity player, BlockPos pos) {
      double distanceTo = player.squaredDistanceTo(Vec3d.ofBottomCenter(pos));
      if (pos.equals(this.progressTrackedSpot) && !(distanceTo < this.lastDistanceToStandSpot - 1.0)) {
         return ++this.noProgressTicks > 200;
      } else {
         this.progressTrackedSpot = pos.toImmutable();
         this.lastDistanceToStandSpot = distanceTo;
         this.noProgressTicks = 0;
         return false;
      }
   }

   private BlockPos getCentreBlockIfBreakable(World world) {
      BlockPos blockPos = this.layerCursor.getSliceCenterPos();
      return this.isBreakableTarget(world, blockPos) && !this.skippedBlockCooldowns.containsKey(blockPos) ? blockPos : null;
   }

   public AreaLayerCursor getLayerCursor() {
      return this.layerCursor;
   }

   private void setTask(AreaMinerController.MiningTask repositionLockType) {
      if (this.task != repositionLockType) {
         this.task = repositionLockType;
         this.taskTicks = 0;
      }
   }

   private int countHeldPlugs(World world) {
      int posBlock = 0;

      for (BlockPos blockPos : this.placedPlugPositions) {
         if (this.areaBounds.canFlag(blockPos) && this.isPlugHoldingFluid(world, blockPos)) {
            posBlock++;
         }
      }

      return posBlock;
   }

   public String formatStatusLine() {
      return switch (this.state) {
         case IDLE -> "not running";
         case RUNNING -> this.layerCursor.getProgressDescription()
            + " \u00b7 "
            + Math.round(this.layerCursor.getProgressFraction() * 100.0F)
            + "% \u00b7 mined "
            + this.minedBlockCount
            + (this.skippedBlockCount > 0 ? " \u00b7 skipped " + this.skippedBlockCount : "")
            + (this.statusText.isEmpty() ? "" : " \u00b7 " + this.statusText);
         case PAUSED -> "paused \u00b7 " + (this.layerCursor != null ? this.layerCursor.getProgressDescription() : "");
         case DONE -> "done \u00b7 mined " + this.minedBlockCount + " blocks";
      };
   }

   public void resetIfInactive() {
      if (!this.isActive()) {
         this.layerCursor = null;
         this.state = AreaMinerController.State.IDLE;
      }
   }

   private boolean resetShaftState() {
      this.inCentreColumn = false;
      this.downCutTarget = null;
      this.shaftApproachTarget = null;
      this.shaftAlignPos = null;
      this.shaftPhaseTicks = 0;
      this.dropWaitTicks = 0;
      this.shaftLevelTicks = 0;
      this.shaftAlignTicks = 0;
      this.alignedWithShaft = false;
      return false;
   }

   private boolean tickLeftoverSweep(ClientPlayerEntity player, World world, int leftoverBlockValue, int leftoverBlockValue2, boolean leftoverBlockFlag) {
      double maxSpotStandAmount = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
      if (this.sweepBreakTarget != null
         && (!this.isBreakableNonSupport(world, this.sweepBreakTarget) || !this.areaBounds.canFlag(this.sweepBreakTarget) || !this.blockAimer.canReachBlock(this.sweepBreakTarget, maxSpotStandAmount))) {
         this.sweepBreakTarget = null;
         this.leftoverStuckTicks = 0;
      }

      if (this.sweepBreakTarget == null && !this.navigator.isPillaring()) {
         this.sweepBreakTarget = this.findVisibleBlockNearby(world, player, maxSpotStandAmount, leftoverBlockValue, leftoverBlockValue2, leftoverBlockFlag);
         if (this.sweepBreakTarget != null) {
            this.leftoverStuckTicks = 0;
         }
      }

      if (this.sweepBreakTarget != null) {
         if (++this.leftoverStuckTicks > 70) {
            this.skippedBlockCooldowns.put(this.sweepBreakTarget.toImmutable(), 1200);
            this.sweepBreakTarget = null;
            this.leftoverStuckTicks = 0;
            this.statusText = "skipping a stuck leftover";
            return true;
         } else {
            this.syntheticInput.clearInput();
            this.aimAndBreakBlock(this.sweepBreakTarget, maxSpotStandAmount, false);
            if (this.blockAimer.consumeDrillFallbackFlag() && !this.drillSweepWarned) {
               this.drillSweepWarned = true;
               this.sendChatMessage(
                  "\u00a7eno plain pickaxe in your hotbar \u2014 cleaning leftovers with the drill (its 3x3 can break blocks outside the area). Put a normal pickaxe in your hotbar."
               );
            }

            this.statusText = leftoverBlockFlag ? "final check \u2014 mining a leftover" : "sweeping leftovers around you";
            return true;
         }
      } else {
         BlockPos blockPos = leftoverBlockFlag ? this.getNextFinalCheckBlock(player) : this.findBestBreakableBlock(world, player);
         if (blockPos == null) {
            this.sweepStandSpot = null;
            this.sweepTargetBlock = null;
            return false;
         } else {
            if (this.sweepStandSpot == null || !blockPos.equals(this.sweepTargetBlock)) {
               this.sweepTargetBlock = blockPos;
               this.sweepStandSpot = this.findLeftoverStandSpot(world, player, blockPos);
            }

            if (this.sweepStandSpot == null) {
               this.skippedBlockCooldowns.put(blockPos.toImmutable(), 2400);
               this.sweepTargetBlock = null;
               return true;
            } else {
               this.statusText = leftoverBlockFlag ? "final check \u2014 walking to a leftover block" : "sweeping leftovers \u2014 walking to a leftover block";
               MinerPathNavigator.Result movingImmutable = this.navigator.moveToward(this.sweepStandSpot);
               if (movingImmutable == MinerPathNavigator.Result.BLOCKED) {
                  this.skippedBlockCooldowns.put(blockPos.toImmutable(), 1200);
                  this.rejectedStandSpots.add(this.sweepStandSpot);
                  this.sweepStandSpot = null;
                  this.sweepTargetBlock = null;
               } else if (movingImmutable == MinerPathNavigator.Result.ARRIVED) {
                  this.syntheticInput.clearInput();
                  this.rejectedStandSpots.add(this.sweepStandSpot);
                  this.sweepStandSpot = null;
                  this.sweepTargetBlock = null;
               }

               return true;
            }
         }
      }
   }

   private BlockPos findVisibleBlockNearby(World world, ClientPlayerEntity player, double cornerNearValue, int leftoverBlockValue, int cornerNearValue3, boolean cornerNearFlag) {
      BlockPos blockPos = player.getBlockPos();
      Vec3d eyePos = player.getEyePos();
      int cornerNearValue4 = cornerNearFlag ? Integer.MIN_VALUE : this.layerCursor.getSliceCenterPos().getY();
      int cornerNearValue5 = (int)Math.sqrt(100.0) + 1;
      boolean cornerNearFlag2 = this.layerCursor.faceAxis == Axis.X;
      int cornerNearValue6 = cornerNearFlag2 ? blockPos.getX() : blockPos.getZ();
      int cornerNearValue7 = cornerNearFlag2 ? blockPos.getZ() : blockPos.getX();
      int max8 = Math.max(this.layerCursor.getFaceAxisStart(), cornerNearValue6 - cornerNearValue5);
      int cornerNearLimit = Math.min(this.layerCursor.getFaceAxisEnd(), cornerNearValue6 + cornerNearValue5);
      int max9 = Math.max(this.layerCursor.getRowAxisMin(), cornerNearValue7 - cornerNearValue5);
      int min2 = Math.min(this.layerCursor.getRowAxisMax(), cornerNearValue7 + cornerNearValue5);
      BlockPos cornerNear = null;
      int cornerNearValue10 = -1;
      double maxValue = Double.MAX_VALUE;

      for (int i = cornerNearValue3; i >= leftoverBlockValue; i--) {
         for (int j = max8; j <= cornerNearLimit; j++) {
            for (int k = max9; k <= min2; k++) {
               BlockPos pos = this.layerCursor.toBlockPos(j, k, i);
               if (this.areaBounds.canFlag(pos)) {
                  double squaredDistance = pos.getSquaredDistance(blockPos);
                  if (!(squaredDistance > 100.0)
                     && this.isBreakableNonSupport(world, pos)
                     && !this.skippedBlockCooldowns.containsKey(pos)
                     && !this.settleCooldownTicks.containsKey(pos)
                     && this.blockAimer.canReachBlock(pos, cornerNearValue)) {
                     BlockHitResult raycast = world.raycast(new RaycastContext(eyePos, Vec3d.ofCenter(pos), ShapeType.OUTLINE, FluidHandling.NONE, player));
                     if (raycast.getType() != Type.BLOCK || raycast.getBlockPos().equals(pos)) {
                        int cornerNearLimit4 = this.countBreakableNeighbours(world, pos) + (i == cornerNearValue4 ? 100 : 0);
                        if (cornerNearLimit4 > cornerNearValue10 || cornerNearLimit4 == cornerNearValue10 && squaredDistance < maxValue) {
                           cornerNearValue10 = cornerNearLimit4;
                           maxValue = squaredDistance;
                           cornerNear = pos.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return cornerNear;
   }

   private void tickFinalCheck(ClientPlayerEntity player, World world) {
      if (!this.finalScanActive) {
         this.finalCheckBlocks.removeIf(pos -> !this.isRemainingBlock(world, pos));
         if (!this.tickLeftoverSweep(player, world, this.areaBounds.getMinY(), this.areaBounds.getMaxY(), true)) {
            if (!this.finalCheckBlocks.isEmpty() && !this.settleCooldownTicks.isEmpty()) {
               this.syntheticInput.clearInput();
               this.statusText = "final check \u2014 waiting for the last blocks to settle";
            } else if (!this.finalCheckBlocks.isEmpty() && this.finalCheckRetryCount < 2) {
               this.finalCheckRetryCount++;
               this.skippedBlockCooldowns.clear();
               this.rejectedStandSpots.clear();
               this.sendChatMessage(
                  "final check: "
                     + this.finalCheckBlocks.size()
                     + " skipped block"
                     + (this.finalCheckBlocks.size() == 1 ? "" : "s")
                     + " \u2014 trying again ("
                     + this.finalCheckRetryCount
                     + "/"
                     + 2
                     + ")"
               );
            } else {
               this.finishMining(this.finalCheckBlocks.size());
            }
         }
      } else {
         this.syntheticInput.clearInput();
         int finalCheckCounter = 60000;

         while (finalCheckCounter-- > 0 && this.finalScanY >= this.areaBounds.getMinY()) {
            BlockPos blockPos = new BlockPos(this.finalScanX, this.finalScanY, this.finalScanZ);
            if (this.isRemainingBlock(world, blockPos)) {
               this.finalCheckBlocks.add(blockPos);
            }

            if (++this.finalScanZ > this.areaBounds.getMaxZ()) {
               this.finalScanZ = this.areaBounds.getMinZ();
               if (++this.finalScanX > this.areaBounds.getMaxX()) {
                  this.finalScanX = this.areaBounds.getMinX();
                  this.finalScanY--;
               }
            }
         }

         if (this.finalScanY < this.areaBounds.getMinY()) {
            this.finalScanActive = false;
            if (this.finalCheckBlocks.isEmpty()) {
               this.finishMining(0);
            } else {
               this.sendChatMessage(
                  "final check: "
                     + this.finalCheckBlocks.size()
                     + " block"
                     + (this.finalCheckBlocks.size() == 1 ? "" : "s")
                     + " still in the area \u2014 cleaning them up"
               );
            }
         }
      }
   }

   private boolean tickShaftDescent(World world, ClientPlayerEntity player) {
      if (this.shaftApproachTarget != null) {
         if (this.shaftPhaseTicks > 600) {
            if (++this.shaftRouteRetries <= 2) {
               this.planShaftToCentreColumn(player);
               this.sendChatMessage(
                  "\u00a7emoving down \u2014 the old way is blocked, trying another route to the centre column (try " + this.shaftRouteRetries + "/3)"
               );
               return true;
            } else {
               this.shaftApproachTarget = null;
               this.sendChatMessage("\u00a7emoving down \u2014 couldn't reach the centre column, the main routine takes over");
               return false;
            }
         } else {
            MinerPathNavigator.Result movingImmutable = this.navigator.moveToward(this.shaftApproachTarget);
            if (movingImmutable == MinerPathNavigator.Result.ARRIVED) {
               this.shaftApproachTarget = null;
               this.inCentreColumn = true;
               this.shaftCutsDone = 0;
               this.downCutTarget = null;
               this.shaftPhaseTicks = 0;
               return true;
            } else if (movingImmutable == MinerPathNavigator.Result.BLOCKED) {
               double maxSpotStandAmount = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
               if (this.tryClearObstacle(world, player, this.shaftApproachTarget, maxSpotStandAmount)) {
                  return true;
               } else if (++this.shaftRouteRetries <= 2) {
                  this.planShaftToCentreColumn(player);
                  this.sendChatMessage(
                     "\u00a7emoving down \u2014 stuck on the way, trying another route to the centre column (try " + this.shaftRouteRetries + "/3)"
                  );
                  return true;
               } else {
                  this.shaftApproachTarget = null;
                  this.sendChatMessage("\u00a7emoving down \u2014 couldn't reach the centre column, the main routine takes over");
                  return false;
               }
            } else {
               this.statusText = "moving down \u2014 walking above the centre column";
               return true;
            }
         }
      } else if (!this.inCentreColumn) {
         return false;
      } else {
         if (this.downCutTarget != null && !this.isBreakableTarget(world, this.downCutTarget)) {
            this.shaftCutsDone++;
            this.downCutTarget = null;
            this.shaftPhaseTicks = 0;
         }

         if (this.shaftAlignPos == null || !player.isOnGround() || player.getBlockX() == this.shaftAlignPos.getX() && player.getBlockZ() == this.shaftAlignPos.getZ()) {
            boolean movingDownFlag = this.shaftCutsDone >= 3 || this.shaftTargetY - player.getBlockY() >= 4 || player.getBlockY() <= this.layerCursor.getSliceBottomY();
            if (movingDownFlag) {
               if (this.shaftPhaseTicks > 600) {
                  return this.resetShaftState();
               } else if (!player.isOnGround()) {
                  this.syntheticInput.clearInput();
                  this.statusText = "moving down \u2014 waiting to land at the bottom of the shaft";
                  return true;
               } else if (player.getBlockY() < this.layerCursor.getSliceBottomY()) {
                  if (this.navigator.canPillarUp(player)) {
                     this.statusText = "moving down \u2014 placing one block to stand level with the face";
                     return true;
                  } else {
                     return this.resetShaftState();
                  }
               } else if (++this.shaftLevelTicks <= 5) {
                  this.syntheticInput.clearInput();
                  this.statusText = "moving down \u2014 level reached, starting work";
                  return true;
               } else {
                  return this.resetShaftState();
               }
            } else if (this.shaftPhaseTicks > 600) {
               return this.resetShaftState();
            } else if (!player.isOnGround()) {
               this.syntheticInput.clearInput();
               this.statusText = "moving down \u2014 waiting to land, then cut " + (this.shaftCutsDone + 1) + "/3";
               return true;
            } else {
               if (this.shaftAlignPos != null) {
                  double movingDownValue = player.getX() - (this.shaftAlignPos.getX() + 0.5);
                  double spotValue = player.getZ() - (this.shaftAlignPos.getZ() + 0.5);
                  double maxAbs = Math.max(Math.abs(movingDownValue), Math.abs(spotValue));
                  if (this.alignedWithShaft && maxAbs > 0.45) {
                     this.alignedWithShaft = false;
                  }

                  if (!this.alignedWithShaft) {
                     if (maxAbs <= 0.15) {
                        this.alignedWithShaft = true;
                     } else if (++this.shaftAlignTicks <= 60) {
                        double angle = Math.toRadians(player.getYaw());
                        double sin = Math.sin(angle);
                        double cos = Math.cos(angle);
                        this.syntheticInput.setMovementInput(toInputAxisFine(-spotValue * cos - -movingDownValue * sin), toInputAxisFine(-spotValue * sin + -movingDownValue * cos), false, false);
                        this.statusText = "moving down \u2014 lining up with the shaft";
                        return true;
                     }
                  }
               }

               if (this.shaftCutsDone == 0 && this.downCutTarget == null) {
                  Direction north = this.getDigDirection();
                  if (SmoothAimRotator.stepYawToward(player, SmoothAimRotator.computeYawToward(north.getOffsetX(), north.getOffsetZ()), 20.0F)) {
                     this.syntheticInput.clearInput();
                     this.statusText = "moving down \u2014 turning to the dig direction";
                     return true;
                  }
               }

               BlockPos down = player.getBlockPos().down();
               double minecraftClientValue = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
               if (!this.areaBounds.canFlag(down)) {
                  return this.resetShaftState();
               } else if (!world.getBlockState(down).isAir()) {
                  this.dropWaitTicks = 0;
                  if (this.skippedBlockCooldowns.containsKey(down)) {
                     return this.resetShaftState();
                  } else if (this.navigator.getPlacedBlocks().contains(down)) {
                     return this.resetShaftState();
                  } else if (this.isBreakableTarget(world, down) && this.blockAimer.canReachBlock(down, minecraftClientValue)) {
                     this.downCutTarget = down.toImmutable();
                     this.syntheticInput.clearInput();
                     this.aimAndBreakBlock(down, minecraftClientValue, true);
                     this.statusText = "moving down \u2014 cutting down " + (this.shaftCutsDone + 1) + "/3";
                     return true;
                  } else {
                     return this.resetShaftState();
                  }
               } else if (++this.dropWaitTicks > 30) {
                  return this.resetShaftState();
               } else {
                  this.syntheticInput.clearInput();
                  this.statusText = "moving down \u2014 waiting to drop, then cut " + (this.shaftCutsDone + 1) + "/3";
                  return true;
               }
            }
         } else {
            int shaftPhaseTicks3 = this.shaftPhaseTicks;
            this.planShaftToCentreColumn(player);
            this.shaftPhaseTicks = shaftPhaseTicks3;
            this.statusText = "moving down \u2014 off the column, heading back to the centre";
            return true;
         }
      }
   }

   private boolean isCentreColumnClear(World world) {
      BlockPos blockPos = this.layerCursor.getSliceCenterPos();

      for (int i = 0; i < 3; i++) {
         BlockPos pos = new BlockPos(blockPos.getX(), this.layerCursor.getCurrentLayerTopY() - i, blockPos.getZ());
         if (this.areaBounds.canFlag(pos) && !world.getBlockState(pos).isAir()) {
            return false;
         }
      }

      return true;
   }

   public int getMinedBlockCount() {
      return this.minedBlockCount;
   }

   private void swapSlotToOffhand(int value) {
      if (!ExtUtils.isAction() && this.trackedSwapSlot < 0) {
         ExtUtils.handleExt(
            () -> {
               ClientPlayerEntity player = this.minecraftClient.player;
               if (player != null
                  && this.minecraftClient.interactionManager != null
                  && this.state == AreaMinerController.State.RUNNING
                  && this.repairingTool) {
                  if (player.currentScreenHandler == player.playerScreenHandler) {
                     if (player.getOffHandStack().getItem() != Items.EXPERIENCE_BOTTLE) {
                        int blockPosValue = value >= 36 ? value - 36 : value;
                        if (player.getInventory().getStack(blockPosValue).getItem() == Items.EXPERIENCE_BOTTLE) {
                           Item item = player.getOffHandStack().getItem();
                           this.minecraftClient.interactionManager.clickSlot(player.playerScreenHandler.syncId, value, 40, SlotActionType.SWAP, player);
                           if (this.queuedSwapSlot < 0) {
                              this.queuedSwapSlot = value;
                              this.offhandItemBeforeSwap = item;
                           }
                        }
                     }
                  }
               }
            },
            true
         );
      }
   }

   public String buildDebugSummary() {
      BlockPos blockPos = this.layerCursor != null && !this.layerCursor.isCheckingLeftovers() ? this.layerCursor.getSliceCenterPos() : null;
      BlockPos blockPos2 = this.navigator == null ? null : this.navigator.getGoalPos();
      StringBuilder stringBuilder = new StringBuilder();
      ClientWorld world = this.minecraftClient.world;
      ClientPlayerEntity player = this.minecraftClient.player;
      if (world != null && player != null) {
         for (BlockPos pos2 : this.placedPlugPositions) {
            if (!(pos2.getSquaredDistance(player.getBlockPos()) > 16.0)) {
               stringBuilder.append(pos2.toShortString().replace(" ", ""))
                  .append(world.getBlockState(pos2).isAir() ? "(air)" : (this.isPlugHoldingFluid(world, pos2) ? "(held)" : "(free)"))
                  .append(';');
            }
         }
      }

      return "task="
         + this.task
         + "/"
         + this.taskTicks
         + " idle="
         + this.idleStuckTicks
         + " stuck="
         + this.stuckEscalationCount
         + " bore="
         + this.boringIntoFace
         + " fc="
         + (blockPos == null ? "-" : blockPos.toShortString())
         + " stand="
         + (this.layerCursor == null ? "-" : this.layerCursor.getPreviousFacePos().toShortString())
         + " goal="
         + (blockPos2 == null ? "-" : blockPos2.toShortString())
         + " target="
         + (this.currentBreakTarget == null ? "-" : this.currentBreakTarget.toShortString())
         + " aim="
         + (this.blockAimer.getCurrentTarget() == null ? "-" : this.blockAimer.getCurrentTarget().toShortString())
         + " well="
         + this.inCentreColumn
         + "/"
         + (this.shaftApproachTarget != null)
         + " open="
         + this.straightCutQueue.size()
         + " sweep="
         + (this.sweepStandSpot == null ? "-" : this.sweepStandSpot.toShortString())
         + " plugsNear="
         + stringBuilder;
   }

   private boolean hasCentreBlockToMine(World world) {
      return !this.layerCursor.isCheckingLeftovers() && this.getCentreBlockIfBreakable(world) != null;
   }

   private void queueStraightCuts(BlockPos pos) {
      this.straightCutQueue.clear();
      this.straightCutTicks = 0;
      this.straightCutsDone = 0;
      this.currentStraightCut = null;
      this.straightCutStandSpot = null;
      ClientPlayerEntity player = this.minecraftClient.player;
      int cornerNear = this.getRowCutColumn();
      int cornerNearValue = this.layerCursor.getTargetRowCoord();
      int y = this.layerCursor.getSliceCenterPos().getY();
      if (player != null) {
         boolean cornerNearFlag = this.layerCursor.faceAxis == Axis.X;
         int cornerNearValue2 = cornerNearFlag ? player.getBlockZ() : player.getBlockX();
         int signum = Integer.signum(cornerNearValue - cornerNearValue2);
         if (signum == 0) {
            boolean cornerNearFlag2 = this.layerCursor.getLayerIndex() % 2 == 0 != this.layerCursor.startAtRowMax;
            signum = cornerNearFlag2 ? 1 : -1;
         }

         BlockPos blockPos = this.layerCursor.toBlockPos(cornerNear, cornerNearValue - signum, y);
         BlockPos blockPos2 = this.layerCursor.toBlockPos(cornerNear, cornerNearValue, y);
         BlockPos blockPos3 = this.layerCursor.toBlockPos(cornerNear, cornerNearValue + signum, y);
         if (this.areaBounds.canFlag(blockPos)) {
            this.straightCutQueue.add(blockPos.toImmutable());
         }

         if (this.areaBounds.canFlag(blockPos2)) {
            this.straightCutQueue.add(blockPos2.toImmutable());
         }

         if (this.areaBounds.canFlag(blockPos3)) {
            this.straightCutQueue.add(blockPos3.toImmutable());
         }
      } else {
         Direction north = this.getDigDirection();

         for (int i = 0; i < 3; i++) {
            BlockPos offset = pos.offset(north, i);
            if (this.areaBounds.canFlag(offset)) {
               this.straightCutQueue.add(offset.toImmutable());
            }
         }
      }
   }

   Drill getDrillSettings() {
      return this.drill;
   }

   private void warnIfNoHotbarBlocks(String enoHotbarText) {
      if (PillarUpPlacer.hasOpaqueBlockInHotbar(this.minecraftClient.player)) {
         this.noHotbarBlocksWarned = false;
      } else if (!this.noHotbarBlocksWarned) {
         this.noHotbarBlocksWarned = true;
         this.sendChatMessage(enoHotbarText);
      }
   }

   public void neutralizeInput() {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (this.state == AreaMinerController.State.RUNNING && player != null && player.input != this.syntheticInput) {
         this.savedPlayerInput = player.input;
         player.input = this.syntheticInput;
      }

      this.syntheticInput.clearInput();
      this.blockAimer.clearTargetAndCancelBreaking();
      this.currentBreakTarget = null;
   }

   private void tickCentreColumnPhase(World world) {
      int estuckCentreCounter = 0;

      while (!this.hasCentreBlockToMine(world) && !this.layerCursor.isCheckingLeftovers() && estuckCentreCounter++ < 4096) {
         this.layerCursor.advanceFaceStep();
      }

      if (!this.hasCentreBlockToMine(world)) {
         this.statusText = "checking the layer";
      } else {
         BlockPos blockPos = this.layerCursor.getSliceCenterPos();
         if (!blockPos.equals(this.lastCentreCell)) {
            if (this.boreRowsRemaining > 0) {
               this.boreRowsRemaining--;
            }

            if (this.lastCentreCell != null) {
               this.recordAirAround(this.lastCentreCell);
            }

            this.resetCentreApproach();
            this.resetNavigationTargets();
            this.lastCentreCell = blockPos.toImmutable();
            this.stuckEscalationCount = 0;
            this.unsafeSpotFailCount = 0;
            this.progressTrackedSpot = null;
         }

         if (this.layerCursor.getRowIndex() != this.currentRowId) {
            this.currentRowId = this.layerCursor.getRowIndex();
            this.boreRowsRemaining = 0;
            this.boringIntoFace = false;
            this.queueStraightCuts(blockPos);
            this.navigator.resetNavigation();
            this.setTask(AreaMinerController.MiningTask.APPROACH);
            this.statusText = "new row \u2014 three straight cuts to open it";
         }

         ClientPlayerEntity player = this.minecraftClient.player;
         if (player == null || !this.tickStraightCuts(world, player)) {
            this.taskTicks++;
            boolean estuckCentreFlag = this.currentBreakTarget != null
               || this.blockAimer.getCurrentTarget() != null
               || this.navigator.isPillaring()
               || this.navigator.isBridging()
               || this.playerMoving;
            this.idleStuckTicks = estuckCentreFlag ? 0 : this.idleStuckTicks + 1;
            if (this.navigator.isBridging()) {
               this.idleStuckTicks = 0;
            }

            if (this.idleStuckTicks > 160 && !this.navigator.isBridging()) {
               this.idleStuckTicks = 0;
               this.stuckEscalationCount++;
               this.abortBreaking();
               switch (this.stuckEscalationCount % 3) {
                  case 1:
                     this.boreRowsRemaining = 3;
                     this.boringIntoFace = true;
                     this.navigator.resetNavigation();
                     this.setTask(AreaMinerController.MiningTask.APPROACH);
                     this.sendChatMessage("\u00a7estuck 8s \u2014 boring straight into the centre column");
                     break;
                  case 2:
                     this.rejectedStandSpots.clear();
                     this.navigator.resetNavigation();
                     this.setTask(AreaMinerController.MiningTask.REPOSITION);
                     this.sendChatMessage("\u00a7estuck \u2014 looking for another spot to reach the centre");
                     break;
                  default:
                     this.rejectedStandSpots.clear();
                     this.boreRowsRemaining = 0;
                     this.boringIntoFace = false;
                     this.navigator.resetNavigation();
                     this.setTask(AreaMinerController.MiningTask.APPROACH);
                     this.sendChatMessage("\u00a7estuck \u2014 recalculating the route to the face");
               }
            } else {
               boolean estuckCentreFlag2 = player != null && blockPos.getSquaredDistance(player.getBlockPos()) <= 36.0;
               if (estuckCentreFlag2 && this.idleStuckTicks > 60 && this.straightCutQueue.isEmpty() && !this.navigator.isBridging()) {
                  this.queueStraightCuts(blockPos);
                  this.straightCutQueue.removeIf(pos -> !this.isBreakableTarget(world, pos));
                  if (!this.straightCutQueue.isEmpty()) {
                     this.boreRowsRemaining = 0;
                     this.boringIntoFace = false;
                     this.navigator.resetNavigation();
                     this.idleStuckTicks = 0;
                     this.setTask(AreaMinerController.MiningTask.APPROACH);
                     this.sendChatMessage("\u00a7estuck 3s \u2014 three straight cuts, then on");
                  }
               }

               BlockPos leftoverFinal = this.getCentreBlockIfBreakable(world);
               if (leftoverFinal != null) {
                  switch (this.task) {
                     case APPROACH:
                        this.tickApproach(world, blockPos);
                        break;
                     case REPOSITION:
                        this.findStandSpotSeeingCentre(world, leftoverFinal);
                        break;
                     case AIM_LOCK:
                        this.tickAimLock(world, leftoverFinal);
                        break;
                     case DIG_LOCKED:
                        this.tickDigLocked(world, leftoverFinal);
                  }
               }
            }
         }
      }
   }

   private int countBreakableNeighbours(World world, BlockPos pos2) {
      int blockPosCounter = 0;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
               if ((i | j | k) != 0 && this.isBreakableTarget(world, pos2.add(i, j, k))) {
                  blockPosCounter++;
               }
            }
         }
      }

      return blockPosCounter;
   }

   private void autoPause() {
      if (this.state == AreaMinerController.State.RUNNING) {
         this.pauseMining();
         this.autoPaused = true;
      }
   }

   private boolean tickClearPlugAtStandSpot(World world, ClientPlayerEntity player, BlockPos pos) {
      for (BlockPos blockPos : new BlockPos[]{pos, pos.up()}) {
         if (this.placedPlugPositions.contains(blockPos) && !world.getBlockState(blockPos).isAir()) {
            if (!this.isPlugHoldingFluid(world, blockPos) && this.areaBounds.canFlag(blockPos)) {
               double maxSpotStandAmount = Math.max(this.drill.reachDistance, player.getBlockInteractionRange());
               if (!this.blockAimer.canReachBlock(blockPos, maxSpotStandAmount)) {
                  return false;
               }

               this.syntheticInput.clearInput();
               this.aimAndBreakBlock(blockPos, maxSpotStandAmount, false);
               this.statusText = "clearing an old plug block from the stand spot";
               return true;
            }

            this.rejectedStandSpots.add(pos.toImmutable());
            this.navigator.resetNavigation();
            this.setTask(AreaMinerController.MiningTask.REPOSITION);
            this.statusText = "a plug holding back water/lava is in the stand spot \u2014 finding another";
            return true;
         }
      }

      return false;
   }

   private boolean isCellFullyBreakable(World world) {
      int cornerNearCounter = 0;

      for (BlockPos blockPos : this.layerCursor.getSurroundingPositions()) {
         if (!this.areaBounds.canFlag(blockPos) || !this.isBreakableTarget(world, blockPos)) {
            return false;
         }

         cornerNearCounter++;
      }

      return cornerNearCounter == 9;
   }

   private static float toInputAxisCoarse(double blockPosLimit) {
      if (Math.abs(blockPosLimit) < 0.18) {
         return 0.0F;
      } else {
         return blockPosLimit > 0.0 ? 1.0F : -1.0F;
      }
   }

   void sendChatMessage(String minecraftClientText) {
      if (this.minecraftClient.player != null) {
         this.minecraftClient.player.sendMessage(Text.literal("\u00a7b[Miner] \u00a7r" + minecraftClientText), false);
      }
   }

   private void skipBlockWithReason(BlockPos pos2, String leftoverShortText) {
      BlockPos immutable = pos2.toImmutable();
      if (!this.skippedBlockCooldowns.containsKey(immutable)) {
         long timeMillis = System.currentTimeMillis();
         if (timeMillis - this.lastSkipMessageMs > 10000L) {
            this.lastSkipMessageMs = timeMillis;
            this.sendChatMessage("\u00a7enot mining " + immutable.toShortString() + " \u2014 " + leftoverShortText);
         }
      }

      this.skippedBlockCooldowns.put(immutable, 600);
      this.statusText = "not mining " + immutable.toShortString() + " \u2014 " + leftoverShortText;
   }

   private void beginFinalScan() {
      this.finalCheckPhaseActive = true;
      this.finalScanActive = true;
      this.finalCheckBlocks.clear();
      this.finalScanY = this.areaBounds.getMaxY();
      this.finalScanX = this.areaBounds.getMinX();
      this.finalScanZ = this.areaBounds.getMinZ();
      this.finalCheckRetryCount = 0;
      this.skippedBlockCooldowns.clear();
      this.rejectedStandSpots.clear();
      this.resetNavigationTargets();
      this.statusText = "final check \u2014 scanning the whole area";
   }

   public void onTick() {
      boolean airSubmerged = this.playerEntityHelper.isInLavaOrDrowning(this.minecraftClient.player, this.minecraftClient.world);
      if (airSubmerged && (this.state == AreaMinerController.State.RUNNING || this.autoPaused)) {
         this.inventoryFood.resetEating();
         this.resumeIfAutoPaused();
      }

      if (this.state == AreaMinerController.State.RUNNING) {
         if (!airSubmerged && this.inventoryFood.tickAutoEat()) {
            this.autoPause();
         }
      } else if (this.state == AreaMinerController.State.PAUSED && this.autoPaused && !this.inventoryFood.tickAutoEat()) {
         this.resumeIfAutoPaused();
      }

      this.tickMining();
   }

   private void resetRepairAndSweepState() {
      this.sweepBreakTarget = null;
      this.sweepStandSpot = null;
      this.sweepTargetBlock = null;
      this.leftoverStuckTicks = 0;
      this.layerRemineTriggered = false;
      this.repairingTool = false;
      this.repairGivenUp = false;
      this.inventorySyncWaitTicks = 0;
      this.collectingXpOrbs = false;
      this.xpOrbWaitTicks = 0;
      this.bottleThrowTicks = 0;
      this.queuedSwapSlot = -1;
      this.offhandItemBeforeSwap = null;
   }

   private int getRowCutColumn() {
      if (this.layerCursor == null) {
         return 0;
      } else {
         int cornerNearValue = this.layerCursor.getFaceAxisStart();
         int faceAxisEnd = this.layerCursor.getFaceAxisEnd();
         if (faceAxisEnd - cornerNearValue >= 2) {
            return this.layerCursor.getFaceDirection() > 0 ? cornerNearValue + 1 : faceAxisEnd - 1;
         } else {
            return this.layerCursor.getFaceCoord();
         }
      }
   }

   private int countRemainingBlocksInLayers(World world, int cornerNearValue, int cornerNearValue2) {
      int cornerNear = 0;

      for (int i = cornerNearValue2; i >= cornerNearValue; i--) {
         for (int j = this.layerCursor.getFaceAxisStart(); j <= this.layerCursor.getFaceAxisEnd(); j++) {
            for (int k = this.layerCursor.getRowAxisMin(); k <= this.layerCursor.getRowAxisMax(); k++) {
               if (this.isRemainingBlock(world, this.layerCursor.toBlockPos(j, k, i))) {
                  cornerNear++;
               }
            }
         }
      }

      return cornerNear;
   }

   private void restoreSwappedItem() {
      if (this.trackedSwapSlot >= 0) {
         ClientPlayerEntity player = this.minecraftClient.player;
         if (player == null || player != this.swapPlayer || this.minecraftClient.interactionManager == null) {
            this.trackedSwapSlot = -1;
            this.pendingRestoreItem = null;
            this.swapPlayer = null;
         } else if (!ExtUtils.isAction()) {
            int trackedSwapSlot2 = this.trackedSwapSlot;
            Item item = this.pendingRestoreItem;
            ExtUtils.handleExt(
               () -> {
                  ClientPlayerEntity var3x = this.minecraftClient.player;
                  if (var3x != null && this.minecraftClient.interactionManager != null && this.trackedSwapSlot == trackedSwapSlot2) {
                     if (var3x.currentScreenHandler == var3x.playerScreenHandler) {
                        int minecraftClientValue = trackedSwapSlot2 >= 36 ? trackedSwapSlot2 - 36 : trackedSwapSlot2;
                        ItemStack stack = var3x.getInventory().getStack(minecraftClientValue);
                        boolean minecraftClientFlag = item != null && item != Items.AIR
                           ? stack.getItem() == item && var3x.getOffHandStack().getItem() != item
                           : var3x.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE && stack.isEmpty();
                        if (minecraftClientFlag) {
                           this.minecraftClient.interactionManager.clickSlot(var3x.playerScreenHandler.syncId, trackedSwapSlot2, 40, SlotActionType.SWAP, var3x);
                        }

                        this.trackedSwapSlot = -1;
                        this.pendingRestoreItem = null;
                        this.swapPlayer = null;
                     }
                  }
               },
               true
            );
         }
      }
   }

   public static enum State {
      IDLE,
      RUNNING,
      PAUSED,
      DONE;
   }

   private static enum MiningTask {
      APPROACH,
      REPOSITION,
      AIM_LOCK,
      DIG_LOCKED;
   }
}
