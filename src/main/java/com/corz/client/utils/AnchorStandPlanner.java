package com.corz.client.utils;

import com.corz.client.entity.WorldCollisionHelper;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.LivingEntityHelperVisible;
import com.corz.client.inventory.LivingEntityHelperVisible.Seed;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.PosVecUtils.Sample;
import com.corz.client.world.VecPosUtilsAmount;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.EntityHeightSamples;

public final class AnchorStandPlanner {
   private int raycastBudget;
   private static final Direction[] ALL_DIRECTIONS = Direction.values();
   private static final double[] FACE_SAMPLE_OFFSETS = new double[]{0.5, 0.2, 0.8};
   private Predicate<Vec3d> blockedPosPredicate = vec -> false;

   static Vec3d getFacePoint(BlockPos pos2, Direction direction, double vec3dValue, double vec3dValue2) {
      double x = pos2.getX();
      double y = pos2.getY();
      double z = pos2.getZ();

      return switch (direction) {
         case UP -> new Vec3d(x + vec3dValue, y + 1.0, z + vec3dValue2);
         case DOWN -> new Vec3d(x + vec3dValue, y, z + vec3dValue2);
         case NORTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z);
         case SOUTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z + 1.0);
         case WEST -> new Vec3d(x, y + vec3dValue, z + vec3dValue2);
         case EAST -> new Vec3d(x + 1.0, y + vec3dValue, z + vec3dValue2);
      };
   }

   private static boolean hasArrivedAt(Vec3d vec, boolean blockPosFlag, Vec3d vec2) {
      return WorldCollisionHelper.horizontalDistance(vec, vec2) <= 0.3 && blockPosFlag && Math.abs(vec.y - vec2.y) < 0.5;
   }

   static List<BlockPos> getNeighborCells(BlockPos pos) {
      var arrayList = new ArrayList(12);
      BlockPos up = pos.up();
      arrayList.add(pos.north());
      arrayList.add(pos.south());
      arrayList.add(pos.west());
      arrayList.add(pos.east());
      arrayList.add(pos.north().west());
      arrayList.add(pos.north().east());
      arrayList.add(pos.south().west());
      arrayList.add(pos.south().east());
      arrayList.add(up.north());
      arrayList.add(up.south());
      arrayList.add(up.west());
      arrayList.add(up.east());
      return arrayList;
   }

   private boolean hasSupportFaceInReach(WorldCollisionHelper blockPosHelperSolid, BlockPos pos, BlockPos pos2, Vec3d vec, double oppositeOffsetValue, Box box) {
      BlockPos up = pos2.up();

      for (Direction direction : ALL_DIRECTIONS) {
         BlockPos offset = pos.offset(direction);
         if (!offset.equals(pos2) && !offset.equals(up)) {
            Direction opposite = direction.getOpposite();
            if (isOnFaceSide(vec, offset, opposite)
               && blockPosHelperSolid.isSolidAt(offset)
               && BlockEntityUtils.canBlockSquare(offset, opposite)
               && this.canRaycastToFace(offset, opposite, vec, oppositeOffsetValue, box)) {
               return true;
            }
         }
      }

      return false;
   }

   public AnchorStandPlanner.Plan planAnchorStand(
      long glowstoneFlooredValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      PearlsPlaystyleData pearlsPlaystyleData,
      int glowstoneFlooredValue2,
      Predicate<Vec3d> predicate
   ) {
      if (pearlsPlaystyleData == null || !pearlsPlaystyleData.anchors() || blockPosHelperSolid == null || blockPosHelperSolid.getWorld() == null || livingEntityHelperVisible == null || absorptionPosData == null || vecBlockingData == null) {
         return null;
      } else if (!BlockPosUtilsRespawn.isRegistryWorld()) {
         return null;
      } else {
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (player == null) {
            return null;
         } else if (SelectedInventoryUtils.getValue(Items.RESPAWN_ANCHOR) >= 0 && SelectedInventoryUtils.getValue(Items.GLOWSTONE) >= 0) {
            if (player.getOffHandStack().isOf(Items.GLOWSTONE)) {
               return null;
            } else {
               Seed seed = livingEntityHelperVisible.getDataGroundMax();
               if (seed != null && seed.grounded()) {
                  Predicate predicate2 = predicate == null ? obj -> false : predicate;
                  this.blockedPosPredicate = predicate2;
                  World world = blockPosHelperSolid.getWorld();
                  Sample sample = seed.sample();
                  Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
                  int floor3 = (int)Math.floor(sample.y() + 1.0E-6);
                  BlockPos floored = BlockPos.ofFloored(vec3d.x, floor3 + 0.01, vec3d.z);
                  Box box = WorldCollisionHelper.getPlayerBoxAt(sample.x(), sample.y(), sample.z(), 0.2);
                  Vec3d pos = absorptionPosData.pos();
                  List<AnchorStandPlanner.StandCell> arrayList = new ArrayList<>();

                  for (int i = -4; i <= 4; i++) {
                     for (int j = -4; j <= 4; j++) {
                        double x = floored.getX() + i + 0.5;
                        double z = floored.getZ() + j + 0.5;
                        double glowstoneFlooredLimit = WorldCollisionHelper.getAmount(x, z, vec3d.x, vec3d.z);
                        if (!(glowstoneFlooredLimit < 1.5) && !(glowstoneFlooredLimit > 4.0)) {
                           for (int k = -1; k >= -2; k--) {
                              BlockPos blockPos = new BlockPos(floored.getX() + i, floor3 + k, floored.getZ() + j);
                              if (!blockPosHelperSolid.isSolidAt(blockPos) && !blockPosHelperSolid.isSolidAt(blockPos.up()) && blockPosHelperSolid.isSolidAt(blockPos.down()) && BlockHazardFlags.isSafeToStand(world, blockPos)) {
                                 Vec3d vec3d2 = new Vec3d(x, blockPos.getY(), z);
                                 double distanceTo = pos.distanceTo(vec3d2);
                                 int ceil4 = (int)Math.ceil(WorldCollisionHelper.horizontalDistance(pos, vec3d2) / 0.26);
                                 if (ceil4 <= 30 && blockPosHelperSolid.canWalkBetween(pos, vec3d2, 1)) {
                                    double glowstoneFlooredValue5 = (k == -1 ? 0.0 : -1.0) - 0.05 * ceil4 - 0.01 * distanceTo;
                                    ((java.util.List) arrayList).add(new AnchorStandPlanner.StandCell(vec3d2, k, ceil4, glowstoneFlooredValue5));
                                    break;
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (((java.util.List) arrayList).isEmpty()) {
                     return null;
                  } else {
                     ((java.util.List) arrayList).sort(Comparator.comparingDouble(AnchorStandPlanner.StandCell::score).reversed());
                     if (((java.util.List) arrayList).size() > 6) {
                        arrayList = ((java.util.List) arrayList).subList(0, 6);
                     }

                     var southNorth = getNeighborCells(floored);
                     double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
                     this.raycastBudget = 240;
                     AnchorStandPlanner.Plan glowstoneFloored = null;

                     for (AnchorStandPlanner.StandCell standCell : arrayList) {
                        int glowstoneFlooredCounter = 0;

                        for (BlockPos southNorthItem : southNorth) {
                           if (glowstoneFlooredCounter >= 3) {
                              break;
                           }

                           AnchorStandPlanner.Plan intersectsCenter = this.evaluateAnchorCell(
                              blockPosHelperSolid, southNorthItem, standCell.feet(), standCell.level(), standCell.pathTicks(), floored, vec3d, box, seed.rExpl(), pos, absorptionPosData.onGround(), predicate2, rangeInteraction
                           );
                           if (intersectsCenter != null) {
                              glowstoneFlooredCounter++;
                              if (glowstoneFloored == null || intersectsCenter.value() > glowstoneFloored.value()) {
                                 glowstoneFloored = intersectsCenter;
                              }
                           }
                        }
                     }

                     return glowstoneFloored;
                  }
               } else {
                  return null;
               }
            }
         } else {
            return null;
         }
      }
   }

   private AnchorStandPlanner.Plan evaluateAnchorCell(
      WorldCollisionHelper blockPosHelperSolid,
      BlockPos pos,
      Vec3d vec,
      int intersectsCenterValue,
      int intersectsCenterValue2,
      BlockPos pos2,
      Vec3d vec2,
      Box box,
      double standPredicateValue,
      Vec3d vec3,
      boolean intersectsCenterFlag,
      Predicate<Vec3d> predicate,
      double oppositeOffsetValue
   ) {
      World world = blockPosHelperSolid.getWorld();
      boolean of = world.getBlockState(pos).isOf(Blocks.RESPAWN_ANCHOR);
      if (!of && !blockPosHelperSolid.isReplaceableAt(pos)) {
         return null;
      } else {
         Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(pos);
         Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(vec.x, vec.y, vec.z, 0.0);
         if (boxForBlockPos.intersects(box) || boxForBlockPos.intersects(playerBoxAt)) {
            return null;
         } else if (isCrystalBlocking(blockPosHelperSolid, pos, boxForBlockPos)) {
            return null;
         } else {
            Vec3d center = Vec3d.ofCenter(pos);
            if (predicate != null && predicate.test(center)) {
               return null;
            } else {
               Vec3d add = vec.add(0.0, 1.62, 0.0);
               if (!of && !this.hasSupportFaceInReach(blockPosHelperSolid, pos, pos2, add, oppositeOffsetValue, box)) {
                  return null;
               } else if (!this.canSeeAnyFace(pos, add, oppositeOffsetValue, box)) {
                  return null;
               } else {
                  double reachingFraction = EntityHeightSamples.getReachingFraction(vec.y, pos.getY() + 0.5);
                  double[] intersectsCenterValues = VecPosUtilsAmount.getAmounts(vec2.x, vec2.y, vec2.z, 1.62, center.x, center.y, center.z, 5.0, 1.0, standPredicateValue);
                  double posVecValue = -0.0784 + intersectsCenterValues[1];
                  int intersectsCenterBound = WorldCollisionHelper.computeStepCount(vec2.y, posVecValue, vec2.y, 60);
                  double intersectsCenterValue7 = 14.0 * (1.0 - reachingFraction) + 0.25 * Math.min(intersectsCenterBound, 24) - 0.15 * intersectsCenterValue2 + (intersectsCenterValue == -1 ? 0.0 : -1.0);
                  return new AnchorStandPlanner.Plan(vec, pos, intersectsCenterValue7, (float)reachingFraction, intersectsCenterValue2 + 8, hasArrivedAt(vec3, intersectsCenterFlag, vec));
               }
            }
         }
      }
   }

   private boolean canRaycastToFace(BlockPos pos2, Direction direction, Vec3d vec, double raycastFactor, Box box) {
      double raycastValue = raycastFactor * raycastFactor;

      for (double faceSampleOffsetsItem : FACE_SAMPLE_OFFSETS) {
         for (double vec3dValue2 : FACE_SAMPLE_OFFSETS) {
            Vec3d vec3d = getFacePoint(pos2, direction, faceSampleOffsetsItem, vec3dValue2);
            if (!(vec.squaredDistanceTo(vec3d) > raycastValue) && (box == null || !box.raycast(vec, vec3d).isPresent())) {
               if (this.raycastBudget <= 0) {
                  return false;
               }

               this.raycastBudget--;
               if (BlockEntityUtils.canSubtractRaycast(vec, vec3d)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean canSeeAnyFace(BlockPos pos, Vec3d vec, double blockPosValue, Box box) {
      for (Direction direction : ALL_DIRECTIONS) {
         if (isOnFaceSide(vec, pos, direction) && this.canRaycastToFace(pos, direction, vec, blockPosValue, box)) {
            return true;
         }
      }

      return false;
   }

   static boolean isOnFaceSide(Vec3d vec, BlockPos pos2, Direction direction) {
      return switch (direction) {
         case UP -> vec.y > pos2.getY() + 1.0;
         case DOWN -> vec.y < pos2.getY();
         case NORTH -> vec.z < pos2.getZ();
         case SOUTH -> vec.z > pos2.getZ() + 1.0;
         case WEST -> vec.x < pos2.getX();
         case EAST -> vec.x > pos2.getX() + 1.0;
      };
   }

   private AnchorStandPlanner.Plan replanFromCurrentPos(
      AnchorStandPlanner.Plan plan, WorldCollisionHelper blockPosHelperSolid, BlockPos pos, Vec3d vec, Box box, double standPredicateValue, int intersectsCenterValue, Vec3d vec2, boolean standPredicateFlag
   ) {
      double standPredicateLimit = WorldCollisionHelper.getAmount(plan.stand().x, plan.stand().z, vec.x, vec.z);
      if (!(standPredicateLimit < 1.5) && !(standPredicateLimit > 4.0)) {
         int ceil3 = (int)Math.ceil(WorldCollisionHelper.horizontalDistance(vec2, plan.stand()) / 0.26);
         double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
         this.raycastBudget = 80;
         AnchorStandPlanner.Plan standPredicate = null;

         for (BlockPos blockPos : getNeighborCells(pos)) {
            AnchorStandPlanner.Plan intersectsCenter = this.evaluateAnchorCell(
               blockPosHelperSolid, blockPos, plan.stand(), intersectsCenterValue, ceil3, pos, vec, box, standPredicateValue, vec2, standPredicateFlag, this.blockedPosPredicate, rangeInteraction
            );
            if (intersectsCenter != null && (standPredicate == null || intersectsCenter.value() > standPredicate.value())) {
               standPredicate = intersectsCenter;
            }
         }

         return standPredicate;
      } else {
         return null;
      }
   }

   static int getStepDownLevel(double blockPosValue, int standFlooredValue) {
      int blockPosValue3 = (int)Math.floor(blockPosValue + 1.0E-6) - standFlooredValue;
      return blockPosValue3 != -1 && blockPosValue3 != -2 ? 0 : blockPosValue3;
   }

   private static boolean isCrystalBlocking(WorldCollisionHelper blockPosHelperSolid, BlockPos pos, Box box) {
      for (EndCrystalEntity endCrystalEntity : blockPosHelperSolid.getCrystalsInRadius(Vec3d.ofCenter(pos), 3.0)) {
         if (endCrystalEntity.getBoundingBox().intersects(box)) {
            return true;
         }
      }

      return false;
   }

   static boolean isAdjacentCell(BlockPos pos, BlockPos pos2) {
      if (pos != null && pos2 != null) {
         int blockPosValue = Math.abs(pos.getX() - pos2.getX());
         int abs2 = Math.abs(pos.getZ() - pos2.getZ());
         int deltaY = pos.getY() - pos2.getY();
         if (deltaY == 0) {
            return blockPosValue <= 1 && abs2 <= 1 && blockPosValue + abs2 > 0;
         } else {
            return deltaY == 1 ? blockPosValue + abs2 == 1 : false;
         }
      } else {
         return false;
      }
   }

   static boolean isAnchorUnsuitable(BlockPos pos, Vec3d vec, double flooredValue, double flooredValue2, double flooredValue3, boolean flooredFlag) {
      if (pos == null) {
         return true;
      } else {
         if (flooredFlag) {
            int floor4 = (int)Math.floor(flooredValue2 + 1.0E-6);
            if (!isAdjacentCell(pos, BlockPos.ofFloored(flooredValue, floor4 + 0.01, flooredValue3))) {
               return true;
            }
         } else if (WorldCollisionHelper.getAmount(pos.getX() + 0.5, pos.getZ() + 0.5, flooredValue, flooredValue3) > 2.5) {
            return true;
         }

         return vec != null && WorldCollisionHelper.getAmount(vec.x, vec.z, flooredValue, flooredValue3) > 5.5;
      }
   }

   public AnchorStandPlanner.Plan revalidatePlan(
      AnchorStandPlanner.Plan plan, WorldCollisionHelper blockPosHelperSolid, AbsorptionPosData absorptionPosData, LivingEntityHelperVisible livingEntityHelperVisible
   ) {
      if (plan != null && blockPosHelperSolid != null && blockPosHelperSolid.getWorld() != null && absorptionPosData != null) {
         World world = blockPosHelperSolid.getWorld();
         BlockPos anchor = plan.anchor();
         boolean of = world.getBlockState(anchor).isOf(Blocks.RESPAWN_ANCHOR);
         if (!of && !blockPosHelperSolid.isReplaceableAt(anchor)) {
            return null;
         } else {
            BlockPos floored = BlockPos.ofFloored(plan.stand().x, plan.stand().y + 0.01, plan.stand().z);
            if (!BlockHazardFlags.isSafeToStand(world, floored)) {
               return null;
            } else {
               Vec3d pos = absorptionPosData.pos();
               Seed seed = livingEntityHelperVisible == null ? null : livingEntityHelperVisible.getDataGroundMax();
               if (seed != null && of) {
                  Sample sample = seed.sample();
                  if (isAnchorUnsuitable(anchor, plan.stand(), sample.x(), sample.y(), sample.z(), seed.grounded())) {
                     return null;
                  }
               }

               if (seed != null && !of) {
                  Sample sample2 = seed.sample();
                  Box box = WorldCollisionHelper.getPlayerBoxAt(sample2.x(), sample2.y(), sample2.z(), 0.2);
                  boolean intersects = BlockEntityUtils.getBoxForBlockPos(anchor).intersects(box);
                  if (seed.grounded()) {
                     int standFlooredValue = (int)Math.floor(sample2.y() + 1.0E-6);
                     BlockPos ofFloored = BlockPos.ofFloored(sample2.x(), standFlooredValue + 0.01, sample2.z());
                     int stepDownLevel = getStepDownLevel(plan.stand().y, standFlooredValue);
                     if (stepDownLevel == 0) {
                        return null;
                     }

                     if (intersects || !isAdjacentCell(anchor, ofFloored)) {
                        return this.replanFromCurrentPos(
                           plan, blockPosHelperSolid, ofFloored, new Vec3d(sample2.x(), sample2.y(), sample2.z()), box, seed.rExpl(), stepDownLevel, pos, absorptionPosData.onGround()
                        );
                     }
                  } else if (intersects) {
                     return null;
                  }
               }

               boolean standFlooredFlag = hasArrivedAt(pos, absorptionPosData.onGround(), plan.stand());
               return standFlooredFlag == plan.arrived() ? plan : new AnchorStandPlanner.Plan(plan.stand(), plan.anchor(), plan.value(), plan.selfBound(), plan.prepTicks(), standFlooredFlag);
            }
         }
      } else {
         return null;
      }
   }

   public record Plan(Vec3d stand, BlockPos anchor, double value, float selfBound, int prepTicks, boolean arrived) {
   }

   private record StandCell(Vec3d feet, int level, int pathTicks, double score) {
   }
}
