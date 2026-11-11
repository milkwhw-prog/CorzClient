package com.corz.client.utils;

import com.corz.client.world.PosBlockUtilsWorld;
import com.corz.client.world.StateBlockUtilsCenter;
import java.util.List;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import com.corz.client.utils.Drill;
import com.corz.client.utils.ScriptedMovementInput;
import com.corz.client.utils.PillarUpPlacer;
import com.corz.client.utils.BlockRegionSelection;
import com.corz.client.utils.BlockAimController;
import com.corz.client.utils.SmoothAimRotator;

public final class MinerPathNavigator {
   private int ceilingBreakAttempts;
   private int bridgePlaceCooldown;
   private int alignTicks;
   private int pathAgeTicks;
   private int pathIndex;
   private int ceilingBreakTicks;
   private boolean breakingCeiling;
   private final MinecraftClient minecraftClient;
   private boolean pillaring;
   private int noProgressTicks;
   private int centreAdjustTicks;
   private BlockPos goalPos;
   private final Drill drill;
   private BlockPos ceilingTarget;
   private int bridgeFailTicks;
   private final ScriptedMovementInput stateFluidInput;
   private BlockPos arrivedGoal;
   private double maxReach;
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private Direction bridgeDirection;
   private int lavaMoveCooldownTicks;
   private boolean turningToward;
   private static final int[] PATH_Y_OFFSETS = new int[]{0, 1, -1};
   private int breakHeldTicks;
   private BlockPos bridgeStartPos;
   private int bridgeStuckTicks;
   private List<BlockPos> currentPath;
   private int overrideY;
   private Axis pathAxis;
   private BlockPos pathGoal;
   private long lastMoveCallNanos;
   private final PillarUpPlacer blockPlacer;
   private BlockPos lastAimTarget;
   private double bestDistanceSq = Double.MAX_VALUE;
   private final BlockRegionSelection areaBounds;
   private final BlockAimController blockAimer;

   public Set<BlockPos> getPlacedBlocks() {
      return this.blockPlacer.getRecentPlacedPositions();
   }

   public long getLastMoveNanos() {
      return this.lastMoveCallNanos;
   }

   private boolean isMakingNoProgress(ClientPlayerEntity player, BlockPos pos) {
      double amount = horizontalDistanceSq(player, pos) + Math.abs(player.getBlockY() - pos.getY());
      BlockPos blockPos = this.blockAimer.getCurrentTarget();
      if (blockPos != null && !blockPos.equals(this.lastAimTarget)) {
         this.breakHeldTicks = 0;
      }

      this.lastAimTarget = blockPos;
      if (this.bestDistanceSq - amount > 0.0015) {
         this.noProgressTicks = 0;
         this.breakHeldTicks = 0;
      } else if (blockPos != null && ++this.breakHeldTicks <= 600) {
         this.noProgressTicks = 0;
      } else {
         this.noProgressTicks++;
      }

      this.bestDistanceSq = amount;
      return this.noProgressTicks > 50;
   }

   private MinerPathNavigator.Result breakBlockingBlock(BlockPos pos2) {
      ClientWorld world = this.minecraftClient.world;
      if (world == null || !this.areaBounds.isInsideRegion(pos2, 1)) {
         return MinerPathNavigator.Result.BLOCKED;
      } else if (!StateBlockUtilsCenter.canHardnessState(world, pos2)) {
         return MinerPathNavigator.Result.BLOCKED;
      } else if (this.isCrosshairOnBlock(pos2)) {
         return this.blockAimer.aimAndSelectTool(pos2, this.getMaxReach(), true)
            ? MinerPathNavigator.Result.MOVING
            : MinerPathNavigator.Result.BLOCKED;
      } else {
         return this.blockAimer.aimAtBlock(pos2, this.getMaxReach()) ? MinerPathNavigator.Result.MOVING : MinerPathNavigator.Result.BLOCKED;
      }
   }

   public boolean isBreakingCeiling() {
      return this.breakingCeiling;
   }

   void steerToward(ClientPlayerEntity player, double yawInputFactor, double spotValue, boolean yawInputFlag) {
      double angle = Math.toRadians(player.getYaw());
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      double yawInputValue = spotValue * cos - yawInputFactor * sin;
      double blockPosLimit = spotValue * sin + yawInputFactor * cos;
      this.stateFluidInput.setMovementInputWithSneak(toGraduatedInputAxis(yawInputValue), toGraduatedInputAxis(blockPosLimit), false, false, yawInputFlag);
   }

   private boolean startCeilingBreak(ClientPlayerEntity player) {
      ClientWorld world = this.minecraftClient.world;
      if (world != null && this.ceilingBreakAttempts < 6) {
         BlockPos up = player.getBlockPos().up(2);
         if (StateBlockUtilsCenter.canCollisionShape(world, up) || !StateBlockUtilsCenter.canHardnessState(world, up)) {
            return false;
         } else if (!this.areaBounds.isInsideRegion(up, 1)) {
            return false;
         } else {
            this.breakingCeiling = true;
            this.ceilingTarget = up.toImmutable();
            this.ceilingBreakTicks = 0;
            this.ceilingBreakAttempts++;
            return true;
         }
      } else {
         return false;
      }
   }

   public PillarUpPlacer getBlockPlacer() {
      return this.blockPlacer;
   }

   private MinerPathNavigator.Result stepTowardGoal(ClientPlayerEntity player, World world, BlockPos pos, BlockPos pos2) {
      boolean inputMovingFlag = pos.getX() == pos2.getX() && pos.getZ() == pos2.getZ();
      if (inputMovingFlag && pos.getY() < pos2.getY() && this.tickPillarUp(player)) {
         return MinerPathNavigator.Result.MOVING;
      } else if (inputMovingFlag && pos.getY() > pos2.getY()) {
         this.stateFluidInput.clearInput();
         BlockPos down = pos.down();
         return StateBlockUtilsCenter.canCollisionShape(world, down) ? MinerPathNavigator.Result.MOVING : this.breakBlockingBlock(down);
      } else if (!inputMovingFlag) {
         Direction southNorth = getStepDirection(this.chooseTravelAxis(pos, pos2), pos, pos2);
         BlockPos offset = pos.offset(southNorth);
         int y = this.overrideY != Integer.MIN_VALUE ? this.overrideY : pos.getY() + 1;
         BlockPos blockPos = new BlockPos(offset.getX(), y, offset.getZ());
         BlockPos blockPos2 = new BlockPos(offset.getX(), pos.getY() + 1, offset.getZ());
         boolean canCollisionShape2 = !StateBlockUtilsCenter.canCollisionShape(world, blockPos);
         boolean canCollisionShape3 = !StateBlockUtilsCenter.canCollisionShape(world, blockPos2);
         if (!canCollisionShape2 && !canCollisionShape3) {
            this.blockAimer.clearTargetAndCancelBreaking();
            if (this.getYawDifferenceToward(player, southNorth.getOffsetX(), southNorth.getOffsetZ()) > 50.0F) {
               this.stateFluidInput.clearInput();
               return MinerPathNavigator.Result.MOVING;
            } else if (!StateBlockUtilsCenter.canSquareState(world, offset.down())) {
               if (this.drill.placeBlocks && this.lavaMoveCooldownTicks <= 0 && PillarUpPlacer.hasOpaqueBlockInHotbar(player)) {
                  this.startBridging(southNorth, pos);
                  MinerPathNavigator.Result offsetInput = this.stepBridgeAcross(player, world, pos, pos2);
                  if (offsetInput != null) {
                     return offsetInput;
                  }
               }

               this.stateFluidInput.clearInput();
               return MinerPathNavigator.Result.BLOCKED;
            } else {
               boolean inputMovingFlag4 = this.drill.sprintToTarget && horizontalDistanceSq(player, pos2) > 2.5;
               this.stateFluidInput.setMovementInput(1.0F, 0.0F, false, inputMovingFlag4);
               return MinerPathNavigator.Result.MOVING;
            }
         } else {
            boolean inputMovingFlag5 = Math.abs(MathHelper.wrapDegrees(SmoothAimRotator.computeYawToward(southNorth.getOffsetX(), southNorth.getOffsetZ()) - player.getYaw())) <= 40.0F;
            boolean inputMovingFlag6 = inputMovingFlag5 && this.drill.sprintToTarget && horizontalDistanceSq(player, pos2) > 2.5;
            this.stateFluidInput.setMovementInput(inputMovingFlag5 ? 1.0F : 0.0F, 0.0F, false, inputMovingFlag6);
            MinerPathNavigator.Result inputMoving = canCollisionShape2 && this.blockAimer.canReachBlock(blockPos, this.getMaxReach())
               ? this.breakBlockingBlock(blockPos)
               : (canCollisionShape3 ? this.breakBlockingBlock(blockPos2) : this.breakBlockingBlock(blockPos));
            if (inputMoving == MinerPathNavigator.Result.BLOCKED) {
               this.stateFluidInput.clearInput();
            }

            return inputMoving;
         }
      } else {
         this.pathAxis = null;
         if (pos.getY() < pos2.getY()) {
            if (this.blockAimer.canReachBlock(pos2, this.drill.reachDistance)) {
               this.stateFluidInput.clearInput();
               return MinerPathNavigator.Result.ARRIVED;
            } else {
               double inputMovingFactor = pos2.getX() + 0.5 - player.getX();
               double spotValue = pos2.getZ() + 0.5 - player.getZ();
               if (inputMovingFactor * inputMovingFactor + spotValue * spotValue > 0.64) {
                  this.steerToward(player, inputMovingFactor, spotValue, false);
                  return MinerPathNavigator.Result.MOVING;
               } else if (pos2.getY() - pos.getY() > 4 && this.tickPillarUp(player)) {
                  return MinerPathNavigator.Result.MOVING;
               } else {
                  this.pillaring = false;
                  return MinerPathNavigator.Result.BLOCKED;
               }
            }
         } else {
            this.stateFluidInput.clearInput();
            return MinerPathNavigator.Result.MOVING;
         }
      }
   }

   public void setOverrideY(int blockPosValue) {
      this.overrideY = blockPosValue;
   }

   private boolean isCrosshairOnBlock(BlockPos pos2) {
      return this.minecraftClient.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK && blockHitResult.getBlockPos().equals(pos2);
   }

   public void setMaxReach(double drillSpotBound) {
      this.maxReach = Math.max(this.drill.reachDistance, drillSpotBound);
   }

   public void resetBridging() {
      this.bridgeDirection = null;
      this.bridgeStuckTicks = 0;
      this.bridgeFailTicks = 0;
      this.bridgePlaceCooldown = 0;
      this.lavaMoveCooldownTicks = 25;
      this.stateFluidInput.jumpBlocked = false;
      this.stateFluidInput.sneakHoldTicks = 0;
      this.stateFluidInput.clearInput();
   }

   static double horizontalDistanceSq(ClientPlayerEntity player, BlockPos pos) {
      double blockPosFactor = pos.getX() + 0.5 - player.getX();
      double blockPosFactor2 = pos.getZ() + 0.5 - player.getZ();
      return blockPosFactor * blockPosFactor + blockPosFactor2 * blockPosFactor2;
   }

   private float getYawDifferenceToward(ClientPlayerEntity player, double wrapDegreesValue, double y) {
      float yawToward = SmoothAimRotator.computeYawToward(wrapDegreesValue, y);
      float absWrapDegrees = Math.abs(MathHelper.wrapDegrees(yawToward - player.getYaw()));
      if (!this.turningToward) {
         if (absWrapDegrees < 8.0F) {
            return 0.0F;
         }

         this.turningToward = true;
      }

      this.turningToward = SmoothAimRotator.stepYawToward(player, yawToward, 12.0F);
      return absWrapDegrees;
   }

   public BlockPos getPathGoal() {
      return this.pathGoal;
   }

   private boolean followPath(ClientPlayerEntity player, World world, BlockPos pos, BlockPos blockPos) {
      boolean posOffsetFlag = this.currentPath == null || !blockPos.equals(this.pathGoal) || this.pathIndex >= this.currentPath.size() || ++this.pathAgeTicks > 40;
      if (posOffsetFlag) {
         PosBlockUtilsWorld.Search search = new PosBlockUtilsWorld.Search(world, pos, 3);
         this.currentPath = search.getFifoqueueDequeue(blockPos);
         if (this.currentPath == null) {
            List items = null;

            for (Direction direction : HORIZONTAL_DIRECTIONS) {
               BlockPos up2 = null;

               for (int posOffsetValue : PATH_Y_OFFSETS) {
                  BlockPos up = blockPos.offset(direction).up(posOffsetValue);
                  if (StateBlockUtilsCenter.canDown(world, up)) {
                     up2 = up;
                     break;
                  }
               }

               List list;
               if (up2 != null && (list = search.getFifoqueueDequeue(up2)) != null && !list.isEmpty() && (items == null || list.size() < items.size())) {
                  items = list;
               }
            }

            this.currentPath = items;
         }

         if (this.currentPath != null && !this.currentPath.isEmpty()) {
            int posOffsetValue2 = Math.abs(blockPos.getX() - pos.getX()) + Math.abs(blockPos.getY() - pos.getY()) + Math.abs(blockPos.getZ() - pos.getZ());
            if (this.currentPath.size() > posOffsetValue2 * 4 + 20) {
               this.currentPath = null;
            }
         }

         this.pathGoal = blockPos;
         this.pathIndex = 0;
         this.pathAgeTicks = 0;
      }

      if (this.currentPath != null && !this.currentPath.isEmpty()) {
         while (this.pathIndex < this.currentPath.size() && this.currentPath.get(this.pathIndex).equals(pos)) {
            this.pathIndex++;
         }

         if (this.pathIndex >= this.currentPath.size()) {
            this.currentPath = null;
            return false;
         } else {
            BlockPos blockPos3 = this.currentPath.get(this.pathIndex);
            if (Math.abs(blockPos3.getX() - pos.getX()) <= 1 && Math.abs(blockPos3.getZ() - pos.getZ()) <= 1) {
               boolean drillInputFlag = this.pathIndex == this.currentPath.size() - 1;
               boolean posOffsetFlag3 = blockPos3.getY() > pos.getY();
               boolean posOffsetFlag4 = horizontalDistanceSq(player, blockPos) > 2.5;
               this.steerAlongPath(player, blockPos3, drillInputFlag, posOffsetFlag3, posOffsetFlag4);
               return true;
            } else {
               this.currentPath = null;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static float toGraduatedInputAxis(double blockPosLimit) {
      double blockPosValue = Math.abs(blockPosLimit);
      if (blockPosValue < 0.08) {
         return 0.0F;
      } else if (blockPosValue < 0.3) {
         return blockPosLimit > 0.0 ? 0.35F : -0.35F;
      } else {
         return blockPosLimit > 0.0 ? 1.0F : -1.0F;
      }
   }

   private MinerPathNavigator.Result stepBridgeAcross(ClientPlayerEntity player, World world, BlockPos pos, BlockPos pos2) {
      Direction direction = this.bridgeDirection;
      if (direction == null) {
         return null;
      } else {
         if (this.bridgePlaceCooldown > 0) {
            this.bridgePlaceCooldown--;
         }

         if (pos.equals(pos2)) {
            this.resetBridging();
            return MinerPathNavigator.Result.ARRIVED;
         } else {
            double offsetInputFactor = pos2.getX() + 0.5 - player.getX();
            double offsetInputFactor2 = pos2.getZ() + 0.5 - player.getZ();
            if (offsetInputFactor * offsetInputFactor + offsetInputFactor2 * offsetInputFactor2 <= 0.49 && Math.abs(player.getY() - pos2.getY()) < 0.6) {
               this.resetBridging();
               return MinerPathNavigator.Result.ARRIVED;
            } else {
               BlockPos down = pos.down();
               BlockPos offset = pos.offset(direction);
               BlockPos offsetBelow = offset.down();
               boolean squareState = StateBlockUtilsCenter.canSquareState(world, down);
               boolean canSquareState = StateBlockUtilsCenter.canSquareState(world, offsetBelow);
               if (squareState && canSquareState && !this.blockPlacer.getRecentPlacedPositions().contains(offsetBelow)) {
                  this.resetBridging();
                  SmoothAimRotator.snapYawTo(player, SmoothAimRotator.computeYawToward(direction.getOffsetX(), direction.getOffsetZ()));
                  player.setPitch(15.0F);
                  return MinerPathNavigator.Result.MOVING;
               } else {
                  if (!pos.equals(this.bridgeStartPos)) {
                     this.bridgeStartPos = pos.toImmutable();
                     this.bridgeStuckTicks = 0;
                  }

                  if (++this.bridgeStuckTicks > 140) {
                     this.resetBridging();
                     return MinerPathNavigator.Result.BLOCKED;
                  } else {
                     int opaqueCube = PillarUpPlacer.findOpaqueBlockHotbarSlot(player);
                     if (opaqueCube < 0) {
                        this.resetBridging();
                        return MinerPathNavigator.Result.BLOCKED;
                     } else {
                        if (player.getInventory().getSelectedSlot() != opaqueCube) {
                           player.getInventory().setSelectedSlot(opaqueCube);
                        }

                        if (!squareState) {
                           this.blockAimer.clearTargetAndCancelBreaking();
                           this.stateFluidInput.setMovementInputWithSneak(0.0F, 0.0F, false, false, true);
                           this.stateFluidInput.jumpBlocked = true;
                           this.stateFluidInput.sneakHoldTicks = 6;
                           if (this.bridgePlaceCooldown <= 0 && this.blockPlacer.selectBlockAndPlaceAt(player, down, this.getMaxReach())) {
                              this.bridgeFailTicks = 0;
                              this.bridgePlaceCooldown = 4;
                           } else if (++this.bridgeFailTicks > 40) {
                              this.resetBridging();
                              return MinerPathNavigator.Result.BLOCKED;
                           }

                           return MinerPathNavigator.Result.MOVING;
                        } else if (StateBlockUtilsCenter.canCollisionShape(world, offset) && StateBlockUtilsCenter.canCollisionShape(world, offset.up())) {
                           double offsetInputValue = down.getX() + 0.5;
                           double offsetInputValue2 = down.getZ() + 0.5;
                           float yawToward = SmoothAimRotator.computeYawToward(-direction.getOffsetX(), -direction.getOffsetZ());
                           double offsetInputValue4 = direction.getOffsetX() * (player.getZ() - offsetInputValue2) - direction.getOffsetZ() * (player.getX() - offsetInputValue);
                           float clamp = MathHelper.clamp((float)(offsetInputValue4 * 25.0), -6.0F, 6.0F);
                           SmoothAimRotator.stepYawToward(player, yawToward - clamp, 25.0F);
                           float pitch = player.getPitch();
                           player.setPitch(pitch + MathHelper.clamp(78.0F - pitch, -15.0F, 15.0F));
                           this.stateFluidInput.jumpBlocked = true;
                           this.stateFluidInput.sneakHoldTicks = Math.max(this.stateFluidInput.sneakHoldTicks, 4);
                           this.stateFluidInput.setMovementInputWithSneak(-1.0F, 0.0F, false, false, true);
                           if (canSquareState) {
                              this.bridgeFailTicks = 0;
                              return MinerPathNavigator.Result.MOVING;
                           } else {
                              double offsetInputValue5 = (player.getX() - offsetInputValue) * direction.getOffsetX() + (player.getZ() - offsetInputValue2) * direction.getOffsetZ();
                              if (!(offsetInputValue5 < 0.2) && this.bridgePlaceCooldown <= 0) {
                                 boolean offsetInputFlag = false;
                                 HitResult crosshairTarget = this.minecraftClient.crosshairTarget;
                                 if (crosshairTarget instanceof BlockHitResult blockHitResult && crosshairTarget.getType() == Type.BLOCK) {
                                    BlockPos blockPos = blockHitResult.getBlockPos();
                                    Direction side = blockHitResult.getSide();
                                    if (blockPos.offset(side).equals(offsetBelow) || blockPos.equals(down) && side == direction) {
                                       ActionResult interactBlock = this.minecraftClient.interactionManager.interactBlock(player, Hand.MAIN_HAND, blockHitResult);
                                       if (interactBlock.isAccepted()) {
                                          player.swingHand(Hand.MAIN_HAND);
                                          offsetInputFlag = true;
                                          this.bridgePlaceCooldown = 4;
                                          this.blockPlacer.recordPlacedPosition(offsetBelow);
                                       }
                                    }
                                 }

                                 if (!offsetInputFlag && this.blockPlacer.selectBlockAndPlaceAt(player, offsetBelow, this.getMaxReach())) {
                                    offsetInputFlag = true;
                                    this.bridgePlaceCooldown = 4;
                                 }

                                 if (offsetInputFlag) {
                                    this.bridgeFailTicks = 0;
                                    this.bridgeStuckTicks = 0;
                                 } else if (++this.bridgeFailTicks > 60) {
                                    this.resetBridging();
                                    return MinerPathNavigator.Result.BLOCKED;
                                 }

                                 return MinerPathNavigator.Result.MOVING;
                              } else {
                                 return MinerPathNavigator.Result.MOVING;
                              }
                           }
                        } else {
                           this.resetBridging();
                           SmoothAimRotator.snapYawTo(player, SmoothAimRotator.computeYawToward(direction.getOffsetX(), direction.getOffsetZ()));
                           player.setPitch(15.0F);
                           return MinerPathNavigator.Result.MOVING;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public List<BlockPos> getCurrentPath() {
      return this.currentPath;
   }

   private boolean tickCeilingBreak(ClientPlayerEntity player) {
      ClientWorld world = this.minecraftClient.world;
      if (world != null && this.ceilingTarget != null) {
         this.pillaring = true;
         this.stateFluidInput.clearInput();
         if (StateBlockUtilsCenter.canCollisionShape(world, this.ceilingTarget)) {
            this.blockPlacer.resetRiseState();
            return this.finishCeilingBreak(true);
         } else {
            return ++this.ceilingBreakTicks <= 200 && this.blockAimer.aimAndSelectTool(this.ceilingTarget, this.getMaxReach(), true)
               ? true
               : this.finishCeilingBreak(false);
         }
      } else {
         return this.finishCeilingBreak(false);
      }
   }

   public boolean canPillarUp(ClientPlayerEntity player) {
      return this.tickPillarUp(player);
   }

   public void resetNavigation() {
      this.noProgressTicks = 0;
      this.breakHeldTicks = 0;
      this.lastAimTarget = null;
      this.alignTicks = 0;
      this.arrivedGoal = null;
      this.bestDistanceSq = Double.MAX_VALUE;
      this.currentPath = null;
      this.pathGoal = null;
      this.pathIndex = 0;
      this.pathAgeTicks = 0;
      this.pillaring = false;
      this.breakingCeiling = false;
      this.ceilingTarget = null;
      this.ceilingBreakTicks = 0;
      this.ceilingBreakAttempts = 0;
      this.pathAxis = null;
      this.bridgeDirection = null;
      this.bridgeStuckTicks = 0;
      this.bridgeFailTicks = 0;
      this.bridgeStartPos = null;
      this.bridgePlaceCooldown = 0;
      this.turningToward = false;
      this.blockPlacer.resetRiseState();
   }

   private boolean centreOnBlock(ClientPlayerEntity player) {
      if (!player.isOnGround()) {
         return false;
      } else {
         BlockPos blockPos = player.getBlockPos();
         double groundPosValue = blockPos.getX() + 0.5 - player.getX();
         double spotValue = blockPos.getZ() + 0.5 - player.getZ();
         if ((Math.abs(groundPosValue) > 0.15 || Math.abs(spotValue) > 0.15) && this.centreAdjustTicks <= 40) {
            this.centreAdjustTicks++;
            this.pillaring = true;
            this.steerToward(player, groundPosValue, spotValue, false);
            return true;
         } else {
            this.centreAdjustTicks = 0;
            return false;
         }
      }
   }

   private boolean finishCeilingBreak(boolean immutableFlag) {
      this.breakingCeiling = false;
      this.ceilingTarget = null;
      this.ceilingBreakTicks = 0;
      this.blockAimer.clearTargetAndCancelBreaking();
      if (!immutableFlag) {
         this.pillaring = false;
      }

      return immutableFlag;
   }

   public void clearPlacedBlocks() {
      this.blockPlacer.clearRecentPlacements();
   }

   public MinerPathNavigator.Result moveToward(BlockPos blockPos) {
      this.goalPos = blockPos;
      this.lastMoveCallNanos = System.nanoTime();
      ClientPlayerEntity player = this.minecraftClient.player;
      ClientWorld world = this.minecraftClient.world;
      if (player == null || world == null) {
         return MinerPathNavigator.Result.BLOCKED;
      } else if (this.breakingCeiling) {
         return this.tickCeilingBreak(player) ? MinerPathNavigator.Result.MOVING : MinerPathNavigator.Result.BLOCKED;
      } else {
         BlockPos pos = player.getBlockPos();
         if (this.pillaring && pos.getY() >= blockPos.getY() && player.isOnGround()) {
            this.pillaring = false;
         }

         if (this.pillaring) {
            return this.tickPillarUp(player) ? MinerPathNavigator.Result.MOVING : MinerPathNavigator.Result.BLOCKED;
         } else if (pos.equals(blockPos)) {
            this.resetBridging();
            if (!StateBlockUtilsCenter.canSquareState(world, blockPos.down()) && this.drill.placeBlocks) {
               this.stateFluidInput.setMovementInputWithSneak(0.0F, 0.0F, false, false, true);
               this.blockPlacer.selectBlockAndPlaceAt(player, blockPos.down(), this.getMaxReach());
               return MinerPathNavigator.Result.MOVING;
            } else {
               if (this.arrivedGoal != null && this.arrivedGoal.equals(blockPos)) {
                  if (horizontalDistanceSq(player, blockPos) <= 0.2025) {
                     this.stateFluidInput.clearInput();
                     return MinerPathNavigator.Result.ARRIVED;
                  }

                  this.arrivedGoal = null;
               }

               double movingImmutableLimit = horizontalDistanceSq(player, blockPos);
               if (movingImmutableLimit > 0.16 && ++this.alignTicks <= 6) {
                  this.blockAimer.clearTargetAndCancelBreaking();
                  this.steerToward(player, blockPos.getX() + 0.5 - player.getX(), blockPos.getZ() + 0.5 - player.getZ(), false);
                  return MinerPathNavigator.Result.MOVING;
               } else {
                  this.stateFluidInput.clearInput();
                  this.resetNavigation();
                  this.arrivedGoal = blockPos.toImmutable();
                  return MinerPathNavigator.Result.ARRIVED;
               }
            }
         } else {
            if (this.bridgeDirection != null) {
               MinerPathNavigator.Result offsetInput = this.stepBridgeAcross(player, world, pos, blockPos);
               if (offsetInput != null) {
                  return offsetInput;
               }
            }

            this.alignTicks = 0;
            this.arrivedGoal = null;
            if (this.isMakingNoProgress(player, blockPos)) {
               this.stateFluidInput.clearInput();
               this.resetNavigation();
               return MinerPathNavigator.Result.BLOCKED;
            } else if (this.followPath(player, world, pos, blockPos)) {
               return MinerPathNavigator.Result.MOVING;
            } else {
               if (this.lavaMoveCooldownTicks > 0) {
                  this.lavaMoveCooldownTicks--;
               }

               if (this.drill.placeBlocks && this.lavaMoveCooldownTicks <= 0 && PillarUpPlacer.hasOpaqueBlockInHotbar(player) && Math.abs(pos.getY() - blockPos.getY()) <= 1) {
                  int deltaX = pos.getX() - blockPos.getX();
                  int deltaZ = pos.getZ() - blockPos.getZ();
                  if (deltaX * deltaX + deltaZ * deltaZ <= 49) {
                     Direction southNorth = getStepDirection(this.chooseTravelAxis(pos, blockPos), pos, blockPos);
                     BlockPos offset = pos.offset(southNorth);
                     if (StateBlockUtilsCenter.canCollisionShape(world, offset) && !StateBlockUtilsCenter.canSquareState(world, offset.down())) {
                        this.startBridging(southNorth, pos);
                        MinerPathNavigator.Result stepBridgeAcross2 = this.stepBridgeAcross(player, world, pos, blockPos);
                        if (stepBridgeAcross2 != null) {
                           return stepBridgeAcross2;
                        }
                     }
                  }
               }

               return this.stepTowardGoal(player, world, pos, blockPos);
            }
         }
      }
   }

   private void startBridging(Direction direction, BlockPos pos) {
      this.bridgeDirection = direction;
      this.bridgeStuckTicks = 0;
      this.bridgeFailTicks = 0;
      this.bridgeStartPos = pos.toImmutable();
   }

   private boolean tickPillarUp(ClientPlayerEntity player) {
      if (!this.drill.placeBlocks) {
         return false;
      } else if (this.breakingCeiling) {
         return this.tickCeilingBreak(player);
      } else if (this.blockPlacer.getSuccessfulRiseCount() > 3) {
         if (this.startCeilingBreak(player)) {
            return this.tickCeilingBreak(player);
         } else {
            this.pillaring = false;
            return false;
         }
      } else {
         this.blockAimer.clearTargetAndCancelBreaking();
         if (this.centreOnBlock(player)) {
            return true;
         } else {
            boolean[] booleans = new boolean[]{false};
            if (!this.blockPlacer.tickPillarRise(player, booleans)) {
               this.pillaring = false;
               return false;
            } else {
               if (this.blockPlacer.getSuccessfulRiseCount() == 0) {
                  this.ceilingBreakAttempts = 0;
               }

               this.pillaring = true;
               this.stateFluidInput.setMovementInput(0.0F, 0.0F, booleans[0], false);
               return true;
            }
         }
      }
   }

   private void steerAlongPath(ClientPlayerEntity player, BlockPos pos, boolean drillInputFlag, boolean drillInputFlag2, boolean drillInputFlag3) {
      double drillInputFactor = pos.getX() + 0.5 - player.getX();
      double spotValue = pos.getZ() + 0.5 - player.getZ();
      double drillInputLimit = drillInputFactor * drillInputFactor + spotValue * spotValue;
      this.blockAimer.clearTargetAndCancelBreaking();
      if (drillInputFlag && drillInputLimit <= 0.49) {
         this.turningToward = false;
         this.steerToward(player, drillInputFactor, spotValue, false);
      } else if ((this.turningToward || drillInputLimit >= 0.5625) && this.getYawDifferenceToward(player, drillInputFactor, spotValue) > 50.0F) {
         this.stateFluidInput.clearInput();
      } else {
         boolean drillInputFlag4 = drillInputFlag && drillInputLimit <= 1.44;
         boolean drillInputFlag5 = this.drill.sprintToTarget && drillInputFlag3 && !drillInputFlag4 && !drillInputFlag2;
         this.stateFluidInput.setMovementInput(1.0F, 0.0F, drillInputFlag2 && player.isOnGround(), drillInputFlag5);
      }
   }

   private static int getAxisDelta(Axis axis, BlockPos pos, BlockPos pos2) {
      return axis == Axis.X ? pos2.getX() - pos.getX() : pos2.getZ() - pos.getZ();
   }

   private Axis chooseTravelAxis(BlockPos pos, BlockPos pos2) {
      if (this.pathAxis != null && getAxisDelta(this.pathAxis, pos, pos2) != 0) {
         return this.pathAxis;
      } else {
         this.pathAxis = Math.abs(pos2.getX() - pos.getX()) >= Math.abs(pos2.getZ() - pos.getZ()) ? Axis.X : Axis.Z;
         return this.pathAxis;
      }
   }

   public boolean isBridging() {
      return this.bridgeDirection != null;
   }

   public boolean isPillaring() {
      return this.pillaring;
   }

   public BlockPos getGoalPos() {
      return this.goalPos;
   }

   private static Direction getStepDirection(Axis axis, BlockPos pos, BlockPos pos2) {
      int southNorthLimit = getAxisDelta(axis, pos, pos2);
      if (axis == Axis.X) {
         return southNorthLimit >= 0 ? Direction.EAST : Direction.WEST;
      } else {
         return southNorthLimit >= 0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   double getMaxReach() {
      return this.maxReach > 0.0 ? this.maxReach : this.drill.reachDistance;
   }

   public MinerPathNavigator(MinecraftClient minecraftClient, Drill drill, ScriptedMovementInput stateFluidInput, BlockAimController blockPosHelper, BlockRegionSelection posBlock) {
      this.overrideY = Integer.MIN_VALUE;
      this.minecraftClient = minecraftClient;
      this.drill = drill;
      this.stateFluidInput = stateFluidInput;
      this.blockAimer = blockPosHelper;
      this.blockPlacer = new PillarUpPlacer(minecraftClient, stateFluidInput);
      this.areaBounds = posBlock;
   }

   public static enum Result {
      MOVING,
      ARRIVED,
      BLOCKED;
   }
}
