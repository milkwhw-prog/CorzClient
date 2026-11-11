package com.corz.client.utils;

import com.corz.client.utils.WalkableRegionAnalyzer.Terrain;
import com.corz.client.utils.TakeoffCorridorCheck.Proof;
import com.corz.client.utils.DeltaHeadroomUtils.Edge;
import com.corz.client.utils.DeltaHeadroomUtils.Hint;
import com.corz.client.utils.DeltaHeadroomUtils.Kind;
import com.corz.client.utils.DeltaHeadroomUtils.Primitive;
import com.corz.client.utils.DeltaHeadroomUtils.Result;
import com.corz.client.utils.SolidUnknownUtils.Verdict;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import com.corz.client.utils.WalkableRegionAnalyzer;
import com.corz.client.utils.TakeoffCorridorCheck;
import com.corz.client.utils.DeltaHeadroomUtils;
import com.corz.client.utils.SolidUnknownUtils;
import com.corz.client.utils.SlidingPulseUtils;
import com.corz.client.utils.GridAStarSearch;
import com.corz.client.utils.GapSupportData;

public final class WalkRouteFinder {
   public static long gapJumpOkCount;
   public static long vetoedEdgeCount;
   public static long gapJumpRejectedCount;
   private static final MinecraftClient MC_CLIENT = MinecraftClient.getInstance();
   private static volatile WalkRouteFinder.PathDiagnostics lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(0, 10000, false, false, 0, 0);
   private static volatile long[] lastFailedFootprint;
   private static final HashSet<Long> VETOED_POS_KEYS = new HashSet<>();
   public static final int[] STAND_Y_OFFSETS_NARROW = new int[]{-2, -1, 0, 1, 2};
   public static final int[] STAND_Y_OFFSETS_WIDE = new int[]{-3, -2, -1, 0, 1, 2};
   private static volatile WalkRouteFinder.EdgeVeto edgeVeto;
   private static volatile long searchStartPosKey = Long.MIN_VALUE;

   static boolean isShapeFlushToSide(Box box, Direction direction) {
      return switch (direction) {
         case NORTH -> box.minZ <= 0.01 && box.maxZ < 0.5;
         case SOUTH -> box.maxZ >= 0.99 && box.minZ > 0.5;
         case WEST -> box.minX <= 0.01 && box.maxX < 0.5;
         case EAST -> box.maxX >= 0.99 && box.minX > 0.5;
         default -> false;
      };
   }

   private static double getHeuristicDistance(BlockPos pos, BlockPos pos2) {
      double maxAbs = Math.max(Math.abs(pos.getX() - pos2.getX()), Math.abs(pos.getZ() - pos2.getZ()));
      return maxAbs + 0.4 * Math.abs(pos.getY() - pos2.getY());
   }

   public static boolean isUnsafeSlipperyStand(BlockView blockView, BlockPos pos) {
      if (blockView != null && SlidingPulseUtils.isHighFriction(blockView.getBlockState(pos.down()).getBlock().getSlipperiness())) {
         for (Direction direction : Type.HORIZONTAL) {
            BlockState blockState = blockView.getBlockState(pos.offset(direction));
            if (!blockState.getFluidState().isEmpty() && blockState.getCollisionShape(blockView, pos.offset(direction)).isEmpty()) {
               return true;
            }

            if (isAirBesideFlowingWater(blockView, pos.offset(direction))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static WalkRouteFinder.RoutePreflight preflightRoute(
      BlockView blockView, List<BlockPos> list, Set<Long> set, Set<Long> set2, Hint hint
   ) {
      if (blockView != null && list != null && !list.isEmpty()) {
         var arrayList = new ArrayList();

         for (int i = 1; i < list.size(); i++) {
            WalkRouteFinder.RouteStep collisionShape = classifyStep(
               blockView, (BlockPos)list.get(i - 1), (BlockPos)list.get(i), set, set2, hint
            );
            arrayList.add(collisionShape);
            if (collisionShape.result() != Result.OK || !DeltaHeadroomUtils.hasPrimitive(collisionShape.kind())) {
               return new WalkRouteFinder.RoutePreflight(false, List.copyOf(arrayList), i, collisionShape.result());
            }
         }

         return new WalkRouteFinder.RoutePreflight(true, List.copyOf(arrayList), -1, Result.OK);
      } else {
         return new WalkRouteFinder.RoutePreflight(false, List.of(), 0, Result.UNSUPPORTED_GEOMETRY);
      }
   }

   private static boolean hasStandableBlockBelow(BlockView blockView, BlockPos pos) {
      for (int i = 0; i <= 2; i++) {
         if (isStandable(blockView, pos.down(i))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isPartialHeightBlock(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof FenceGateBlock && state.contains(Properties.OPEN) && state.get(Properties.OPEN)) {
         return false;
      } else {
         return block instanceof TrapdoorBlock
               && state.contains(Properties.OPEN)
               && !state.get(Properties.OPEN)
               && state.contains(Properties.BLOCK_HALF)
               && state.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM
            ? false
            : block instanceof HopperBlock
               || block instanceof FenceBlock
               || block instanceof WallBlock
               || block instanceof FenceGateBlock
               || block instanceof TrapdoorBlock
               || block instanceof AbstractCauldronBlock
               || block instanceof ComposterBlock;
      }
   }

   public static List<BlockPos> findPath(BlockPos pos, BlockPos pos2) {
      return findPathWithBudget(pos, pos2, Integer.MAX_VALUE);
   }

   private static List<BlockPos> reconstructPath(Map<Long, Long> map, long blockPosValue, BlockPos pos) {
      var arrayList = new ArrayList();
      long botCrystalText = blockPosValue;

      while (botCrystalText != pos.asLong()) {
         arrayList.add(BlockPos.fromLong(botCrystalText));
         Long camefrom = (Long)map.get(botCrystalText);
         if (camefrom == null) {
            break;
         }

         botCrystalText = camefrom;
      }

      arrayList.add(pos);
      Collections.reverse(arrayList);
      return arrayList;
   }

   public static List<BlockPos> findPathToAny(BlockPos pos, Collection<BlockPos> collection) {
      ClientWorld world = MC_CLIENT.world;
      if (world != null && !collection.isEmpty()) {
         var hashSet = new HashSet();
         int pathFootprintBound = 10000;
         int maxValue = Integer.MAX_VALUE;

         for (BlockPos blockPos : collection) {
            hashSet.add(blockPos.asLong());
            pathFootprintBound = Math.max(pathFootprintBound, getSearchBudget(pos, blockPos));
            int spotBound = Math.max(Math.abs(pos.getX() - blockPos.getX()), Math.abs(pos.getZ() - blockPos.getZ())) + Math.abs(pos.getY() - blockPos.getY()) * 2;
            maxValue = Math.min(maxValue, spotBound);
         }

         var arrayList = new ArrayList(collection);
         VETOED_POS_KEYS.clear();
         GridAStarSearch.Result cost = GridAStarSearch.searchPath(pos.asLong(), hashSet, pathFootprintBound, 1.5, key -> {
            var var3x = getNeighborMoves(world, BlockPos.fromLong(key));
            long[] var4x = new long[var3x.size()];

            for (int var5x = 0; var5x < var4x.length; var5x++) {
               var4x[var5x] = ((BlockPos)var3x.get(var5x)).asLong();
            }

            return var4x;
         }, (pathFootprintValue, pathFootprintValue2) -> 1.0 + 0.4 * Math.abs(BlockPos.unpackLongY(pathFootprintValue2) - BlockPos.unpackLongY(pathFootprintValue)), goalList -> {
            BlockPos var3x = BlockPos.fromLong(goalList);
            double var4x = Double.MAX_VALUE;

            for (BlockPos var7x : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
               var4x = Math.min(var4x, getHeuristicDistance(var3x, var7x));
            }

            return var4x;
         });
         lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(cost.expanded(), pathFootprintBound, cost.isPath(), cost.budgetExhausted(), cost.openRemaining(), maxValue);
         lastFailedFootprint = cost.isPath() ? null : appendVetoedKeys(cost.footprint());
         if (!cost.isPath()) {
            return null;
         } else {
            var arrayList2 = new ArrayList(cost.path().size());

            for (long pathItem : cost.path()) {
               arrayList2.add(BlockPos.fromLong(pathItem));
            }

            return arrayList2;
         }
      } else {
         return null;
      }
   }

   public static boolean hasOpenNeighborInWorld(BlockPos pos) {
      ClientWorld world = MC_CLIENT.world;
      return hasOpenHorizontalNeighbor(world, pos);
   }

   public static GapSupportData getGapSupport(BlockView blockView, WalkRouteFinder.RouteStep routeStep) {
      BlockPos to = routeStep.to();
      BlockPos from = routeStep.from();
      BlockState blockState = blockView.getBlockState(to.down());
      VoxelShape collisionShape = blockState.getCollisionShape(blockView, to.down());
      double blockCollisionValue = !isHazardBlock(blockState) && (!isPartialHeightBlock(blockState) || blockState.getBlock() instanceof HopperBlock) && !collisionShape.isEmpty() ? collisionShape.getMax(Axis.Y) : 0.0;
      BlockState blockState2 = blockView.getBlockState(to);
      VoxelShape collisionShape2 = blockState2.getCollisionShape(blockView, to);
      double bodyClear = collisionShape2.isEmpty() ? 0.0 : collisionShape2.getMax(Axis.Y);
      boolean blockCollisionFlag = !isHazardBlock(blockState2) && !isPartialHeightBlock(blockState2) && bodyClear <= 0.26;
      BlockState blockState3 = blockView.getBlockState(to.up());
      boolean narrowLanding = !isHazardBlock(blockState3) && blockState3.getCollisionShape(blockView, to.up()).isEmpty();
      boolean standable3 = false;
      if (routeStep.kind() == Kind.GAP_JUMP) {
         BlockPos add = to.add(Integer.signum(to.getX() - from.getX()), 0, Integer.signum(to.getZ() - from.getZ()));
         standable3 = !isStandable(blockView, add);
      }

      GapSupportData gapSupport = GapSupportData.evaluateGapSupport(routeStep.kind(), from.asLong(), to.asLong(), blockCollisionValue, bodyClear, blockCollisionFlag, narrowLanding, standable3);
      return gapSupport.isCertified() && routeStep.kind() == Kind.GAP_JUMP && blockState.getBlock() instanceof HopperBlock ? gapSupport.withReject("HOPPER_LANDING") : gapSupport;
   }

   public static void setEdgeVeto(WalkRouteFinder.EdgeVeto handler) {
      edgeVeto = handler;
   }

   public static BlockPos findNearestStandPosition(BlockPos pos, double centerSquaredValue, Predicate<BlockPos> predicate) {
      ClientWorld world = MC_CLIENT.world;
      if (world != null && MC_CLIENT.player != null) {
         int ceilFeetValid = (int)Math.ceil(centerSquaredValue);
         Vec3d entityPos = MC_CLIENT.player.getEntityPos();
         double centerSquaredFactor = Math.max(1.0, centerSquaredValue - 0.05);
         BlockPos centerSquared = null;
         double centerSquaredLimit = Double.MAX_VALUE;

         for (int i = -ceilFeetValid; i <= ceilFeetValid; i++) {
            for (int j = -ceilFeetValid; j <= ceilFeetValid; j++) {
               for (int standYOffsetsWideItem : STAND_Y_OFFSETS_WIDE) {
                  BlockPos add = pos.add(i, standYOffsetsWideItem, j);
                  if (!add.equals(pos) && !add.equals(pos.down()) && !add.down().equals(pos) && isStandable(world, add)) {
                     double squaredDistanceTo = Vec3d.ofCenter(add).add(0.0, 1.12, 0.0).squaredDistanceTo(Vec3d.ofCenter(pos));
                     if (!(squaredDistanceTo > centerSquaredFactor * centerSquaredFactor)
                        && (predicate != null ? predicate.test(add) : hasLineOfSight(Vec3d.ofCenter(add).add(0.0, 1.12, 0.0), Vec3d.ofCenter(pos)))) {
                        double centerSquaredValue3 = Vec3d.ofCenter(add).squaredDistanceTo(entityPos) + squaredDistanceTo * 2.0 + (hasOpenNeighborInWorld(add) ? 6.0 : 0.0);
                        if (centerSquaredValue3 < centerSquaredLimit) {
                           centerSquaredLimit = centerSquaredValue3;
                           centerSquared = add;
                        }
                     }
                  }
               }
            }
         }

         return centerSquared;
      } else {
         return null;
      }
   }

   public static boolean isUnreachableFromBelow(BlockPos pos, BlockPos pos2, int belowUnreachableValue) {
      ClientWorld world = MC_CLIENT.world;
      return world != null && pos2.getY() >= pos.getY() + 2
         ? SolidUnknownUtils.analyzePlateau(createTerrainView(world), pos.getY(), pos2.getX(), pos2.getY(), pos2.getZ(), belowUnreachableValue).verdict()
            == Verdict.UNREACHABLE_FROM_BELOW
         : false;
   }

   public static BlockPos findStandPosition(BlockPos pos, double blockPosValue, Set<Long> set, Predicate<BlockPos> predicate, WalkRouteFinder.StandStats standStats) {
      BlockPos losCenter = findStandPositionWithOffsets(pos, blockPosValue, set, predicate, standStats, ProofSupportUtils.blockPosTable);
      return losCenter != null ? losCenter : findStandPositionWithOffsets(pos, blockPosValue, set, predicate, standStats, ProofSupportUtils.proofSupportTable);
   }

   public static boolean isDisconnectedFrom(BlockPos pos, BlockPos pos2, int worldValue) {
      ClientWorld world = MC_CLIENT.world;
      if (world == null) {
         return false;
      } else if (pos2.equals(pos)) {
         return false;
      } else if (!isStandable(world, pos2)) {
         return true;
      } else {
         var hashSet = new HashSet();
         var arrayDeque = new ArrayDeque();
         var hashMap = new HashMap();
         hashSet.add(pos2.asLong());
         arrayDeque.add(pos2);

         while (!arrayDeque.isEmpty()) {
            if (hashSet.size() >= worldValue) {
               return false;
            }

            BlockPos blockPos = (BlockPos)arrayDeque.poll();

            for (int i = -2; i <= 2; i++) {
               for (int j = -2; j <= 2; j++) {
                  if (i != 0 || j != 0) {
                     boolean worldFlag = Math.abs(i) == 2 || Math.abs(j) == 2;
                     if (!worldFlag || Math.abs(i) == 2 && j == 0 || Math.abs(j) == 2 && i == 0) {
                        for (int m = -1; m <= 2; m++) {
                           BlockPos add = blockPos.add(i, m, j);
                           if (!hashSet.contains(add.asLong()) && isStandable(world, add)) {
                              List ifAbsent = ((java.util.List) (hashMap.computeIfAbsent(add.asLong(), obj -> getNeighborMoves(world, add))));
                              if (ifAbsent.contains(blockPos)) {
                                 if (add.equals(pos)) {
                                    return false;
                                 }

                                 hashSet.add(add.asLong());
                                 arrayDeque.add(add);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return !hashSet.contains(pos.asLong());
      }
   }

   private static boolean hasLandingAtOffset(BlockView blockView, BlockPos pos, int[] downValues) {
      BlockPos add = pos.add(downValues[0], 0, downValues[1]);
      if (!isPassableBlock(blockView, add)) {
         return true;
      } else if (isStandable(blockView, add)) {
         return true;
      } else {
         for (int i = 1; i <= 2; i++) {
            if (isStandable(blockView, add.down(i))) {
               return true;
            }
         }

         return false;
      }
   }

   public static BlockPos findStandPositionWithOffsets(
      BlockPos pos, double losCenterValue, Set<Long> set, Predicate<BlockPos> predicate, WalkRouteFinder.StandStats standStats, int[] losCenterValues
   ) {
      ClientWorld world = MC_CLIENT.world;
      if (world != null && MC_CLIENT.player != null) {
         int ceilReach = (int)Math.ceil(losCenterValue);
         Vec3d entityPos = MC_CLIENT.player.getEntityPos();
         BlockPos sel = null;
         double losCenterLimit = Double.MAX_VALUE;

         for (int i = -ceilReach; i <= ceilReach; i++) {
            for (int j = -ceilReach; j <= ceilReach; j++) {
               for (int losCenterValuesItem : losCenterValues) {
                  BlockPos add = pos.add(i, losCenterValuesItem, j);
                  if (!add.equals(pos) && !add.equals(pos.down())) {
                     if (standStats != null) {
                        standStats.scanned++;
                     }

                     if (set != null && set.contains(add.asLong())) {
                        if (standStats != null) {
                           standStats.rejectedPlanned++;
                        }
                     } else if (!isStandable(world, add)) {
                        if (standStats != null) {
                           standStats.rejectedUnstandable++;
                        }
                     } else {
                        Vec3d vec = Vec3d.ofCenter(add).add(0.0, 1.12, 0.0);
                        double losCenterFactor = Math.max(1.0, losCenterValue - 0.6);
                        if (vec.squaredDistanceTo(Vec3d.ofCenter(pos)) > losCenterFactor * losCenterFactor) {
                           if (standStats != null) {
                              standStats.rejectedOutOfReach++;
                           }
                        } else {
                           if (predicate != null) {
                              if (!predicate.test(add)) {
                                 if (standStats != null) {
                                    standStats.rejectedNoLos++;
                                 }
                                 continue;
                              }
                           } else if (!hasLineOfSight(vec, Vec3d.ofCenter(pos))) {
                              if (standStats != null) {
                                 standStats.rejectedNoLos++;
                              }
                              continue;
                           }

                           if (standStats != null) {
                              standStats.accepted++;
                           }

                           double losCenterValue3 = Vec3d.ofCenter(add).squaredDistanceTo(entityPos)
                              + (hasOpenNeighborInWorld(add) ? 24.0 : 0.0)
                              + Math.abs(losCenterValuesItem) * 2.0
                              + (add.getY() + 0.5 < entityPos.y - 0.6 ? 12.0 : 0.0);
                           if (losCenterValue3 < losCenterLimit) {
                              losCenterLimit = losCenterValue3;
                              sel = add;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (standStats != null) {
            standStats.selected = sel;
         }

         return sel;
      } else {
         return null;
      }
   }

   public static void resetCounters() {
      gapJumpRejectedCount = 0L;
      gapJumpOkCount = 0L;
      vetoedEdgeCount = 0L;
   }

   public static WalkRouteFinder.PathDiagnostics getLastPathDiagnostics() {
      return lastPathDiagnostics;
   }

   public static boolean isBambooOrDoor(BlockState state) {
      return state.getBlock() instanceof BambooBlock ? true : state.getBlock() instanceof DoorBlock;
   }

   private static boolean isSolidFloorBlock(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      if (!isHazardBlock(blockState) && !isPartialHeightBlock(blockState)) {
         VoxelShape collisionShape = blockState.getCollisionShape(blockView, pos);
         return !collisionShape.isEmpty()
            && collisionShape.getMax(Axis.Y) >= 0.8
            && !isBambooOrDoor(blockState)
            && !(blockState.getBlock() instanceof LadderBlock)
            && (!blockState.isOf(Blocks.PISTON_HEAD) || !blockState.contains(Properties.FACING) || blockState.get(Properties.FACING).getAxis() == Axis.Y);
      } else {
         return false;
      }
   }

   private static void addNeighborIfValid(BlockView blockView, BlockPos pos, BlockPos pos2, List<BlockPos> list) {
      WalkRouteFinder.RouteStep collisionShape = classifyStep(blockView, pos, pos2, Set.of(), Set.of(), Hint.NONE);
      if (collisionShape.kind() == Kind.GAP_JUMP) {
         if (collisionShape.result() == Result.OK) {
            gapJumpOkCount++;
         } else {
            gapJumpRejectedCount++;
         }
      }

      if (collisionShape.result() == Result.OK && DeltaHeadroomUtils.hasPrimitive(collisionShape.kind())) {
         if (collisionShape.kind() != Kind.DROP && collisionShape.kind() != Kind.GAP_JUMP || !isUnsafeSlipperyStand(blockView, pos2)) {
            WalkRouteFinder.EdgeVeto edgeVeto542 = edgeVeto;
            if (edgeVeto542 != null && edgeVeto542.vetoed(pos, pos2, collisionShape.kind())) {
               vetoedEdgeCount++;
               VETOED_POS_KEYS.add(pos2.asLong());
            } else {
               list.add(pos2);
            }
         }
      }
   }

   public static WalkRouteFinder.RouteStep classifyStep(BlockView blockView, BlockPos pos, BlockPos pos2, Set<Long> set, Set<Long> set2, Hint hint) {
      int deltaX = pos2.getX() - pos.getX();
      int deltaY = pos2.getY() - pos.getY();
      int deltaZ = pos2.getZ() - pos.getZ();
      int collisionShapeValue = Math.abs(deltaX);
      int abs2 = Math.abs(deltaZ);
      boolean collisionShapeFlag = collisionShapeValue == 1 && abs2 == 1;
      boolean collisionShapeFlag2 = true;
      if (collisionShapeFlag) {
         BlockPos add = pos.add(Integer.signum(deltaX), 0, 0);
         BlockPos add2 = pos.add(0, 0, Integer.signum(deltaZ));
         collisionShapeFlag2 = isPassableBlock(blockView, add)
            && isPassableBlock(blockView, add.up())
            && isPassableBlock(blockView, add2)
            && isPassableBlock(blockView, add2.up())
            && hasStandableBlockBelow(blockView, add)
            && hasStandableBlockBelow(blockView, add2);
      }

      boolean collisionShapeFlag3 = collisionShapeValue + abs2 == 2 && (collisionShapeValue == 2 || abs2 == 2);
      Proof proof = collisionShapeFlag3
         ? TakeoffCorridorCheck.checkTakeoffAndCorridor(
            pos.asLong(), pos2.asLong(), collisionShapeValue3 -> isPassableBlock(blockView, BlockPos.fromLong(collisionShapeValue3)), cell -> isStandable(blockView, BlockPos.fromLong(cell))
         )
         : null;
      boolean collisionShapeFlag4 = !collisionShapeFlag3 || proof.corridorClear();
      BlockState blockState = blockView.getBlockState(pos2);
      BlockState blockState2 = blockView.getBlockState(pos2.up());
      BlockState blockState3 = blockView.getBlockState(pos2.down());
      boolean collisionShapeFlag5 = !isHazardBlock(blockState)
         && !isPartialHeightBlock(blockState)
         && (blockState.getCollisionShape(blockView, pos2).isEmpty() || blockState.getCollisionShape(blockView, pos2).getMax(Axis.Y) <= 0.26);
      boolean collisionShapeFlag6 = !isHazardBlock(blockState2) && blockState2.getCollisionShape(blockView, pos2.up()).isEmpty();
      boolean facingBlock = isSolidFloorBlock(blockView, pos2.down());
      boolean collisionShapeFlag7 = collisionShapeFlag3
         ? proof.takeoffClear()
         : deltaY <= 0 || (isPassableBlock(blockView, pos.up()) || isDoorPassableFrom(blockView, pos, deltaX, deltaZ)) && isPassableBlock(blockView, pos.up(2));
      boolean collisionShapeFlag8 = set2 != null && set2.contains(pos2.asLong());
      boolean collisionShapeFlag9 = set != null && set.contains(pos2.down().asLong());
      boolean isChestBlock = blockState3.getBlock() instanceof ChestBlock;
      boolean collisionShapeFlag10 = true;
      if (deltaY < 0 && (deltaX != 0 || deltaZ != 0)) {
         BlockPos blockPos = new BlockPos(pos2.getX(), pos.getY(), pos2.getZ());
         collisionShapeFlag10 = isPassableBlock(blockView, blockPos) && isPassableBlock(blockView, blockPos.up());
      }

      Edge edge = new Edge(
         deltaX, deltaY, deltaZ, isStandable(blockView, pos2), collisionShapeFlag5, collisionShapeFlag6, facingBlock, collisionShapeFlag8, collisionShapeFlag2, collisionShapeFlag4, collisionShapeFlag9, isChestBlock, hasOpenHorizontalNeighbor(blockView, pos2), collisionShapeFlag7, collisionShapeFlag10, hint
      );
      Kind deltaHint = DeltaHeadroomUtils.classifyEdge(edge);
      Result deltaHeadroom = DeltaHeadroomUtils.evaluateEdge(edge);
      return new WalkRouteFinder.RouteStep(pos, pos2, deltaHint, DeltaHeadroomUtils.getPrimitiveForKind(deltaHint), deltaHeadroom);
   }

   private WalkRouteFinder() {
   }

   public static void setDistanceDiagnostics(BlockPos pos, BlockPos pos2) {
      int blockPosValue = Math.max(Math.abs(pos.getX() - pos2.getX()), Math.abs(pos.getZ() - pos2.getZ())) + Math.abs(pos.getY() - pos2.getY()) * 2;
      lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(0, 0, false, false, 0, blockPosValue);
   }

   private static boolean hasLineOfSight(Vec3d vec, Vec3d vec2) {
      BlockHitResult raycast = MC_CLIENT.world.raycast(new RaycastContext(vec, vec2, ShapeType.COLLIDER, FluidHandling.NONE, MC_CLIENT.player));
      return raycast.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK || raycast.getPos().squaredDistanceTo(vec2) < 2.0;
   }

   public static List<BlockPos> findStandPositions(BlockPos pos, double blockPosValue, Set<Long> set, Predicate<BlockPos> predicate, int blockPosLimit) {
      var centerSquared = findSortedStandPositions(pos, blockPosValue, set, predicate, blockPosLimit, ProofSupportUtils.blockPosTable);
      if (centerSquared.size() < blockPosLimit) {
         centerSquared.addAll(findSortedStandPositions(pos, blockPosValue, set, predicate, blockPosLimit - centerSquared.size(), ProofSupportUtils.proofSupportTable));
      }

      return centerSquared;
   }

   public static boolean hasOpenHorizontalNeighbor(BlockView blockView, BlockPos pos) {
      if (blockView == null) {
         return false;
      } else {
         for (Direction direction : Type.HORIZONTAL) {
            BlockPos offset = pos.offset(direction);
            BlockState blockState = blockView.getBlockState(offset);
            if (isPartialHeightBlock(blockState) || isPartialHeightBlock(blockView.getBlockState(offset.down()))) {
               return true;
            }

            if (blockView.getBlockState(offset).getCollisionShape(blockView, offset).isEmpty()
               && blockView.getBlockState(offset.down()).getCollisionShape(blockView, offset.down()).isEmpty()) {
               return true;
            }
         }

         return false;
      }
   }

   private static List<BlockPos> getNeighborMoves(BlockView blockView, BlockPos pos) {
      var arrayList = new ArrayList(8);
      BlockState blockState = blockView.getBlockState(pos);
      if ((blockState.getBlock() instanceof ComposterBlock || blockState.getBlock() instanceof AbstractCauldronBlock) && !isStandable(blockView, pos)) {
         if (isPassableBlock(blockView, pos.up())) {
            for (Direction direction : Type.HORIZONTAL) {
               BlockPos up = pos.offset(direction).up();
               if (isStandable(blockView, up)) {
                  addNeighborIfValid(blockView, pos, up, arrayList);
               }
            }
         }

         return arrayList;
      } else if (blockState.getBlock() instanceof DoorBlock && isSolidFloorBlock(blockView, pos.down()) && !isStandable(blockView, pos)) {
         VoxelShape collisionShape = blockState.getCollisionShape(blockView, pos);
         Box box = collisionShape.isEmpty() ? null : collisionShape.getBoundingBox();

         for (Direction direction2 : Type.HORIZONTAL) {
            if (box == null || !isShapeFlushToSide(box, direction2)) {
               BlockPos offset = pos.offset(direction2);
               if (isStandable(blockView, offset)) {
                  addNeighborIfValid(blockView, pos, offset, arrayList);
               } else {
                  BlockPos up2 = offset.up();
                  if (isStandable(blockView, up2)) {
                     addNeighborIfValid(blockView, pos, up2, arrayList);
                  } else {
                     for (int i = 1; i <= 2; i++) {
                        BlockPos down = offset.down(i);
                        if (isStandable(blockView, down)) {
                           addNeighborIfValid(blockView, pos, down, arrayList);
                           break;
                        }

                        if (!isPassableBlock(blockView, down.up())) {
                           break;
                        }
                     }
                  }
               }
            }
         }

         return arrayList;
      } else {
         int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

         for (int[] downBlockValues : ints) {
            boolean downBlockFlag = downBlockValues[0] != 0 && downBlockValues[1] != 0;
            if (downBlockFlag) {
               BlockPos add = pos.add(downBlockValues[0], 0, 0);
               BlockPos add2 = pos.add(0, 0, downBlockValues[1]);
               if (isPassableBlock(blockView, add) && isPassableBlock(blockView, add.up()) && isPassableBlock(blockView, add2) && isPassableBlock(blockView, add2.up())) {
                  BlockPos add3 = pos.add(downBlockValues[0], 0, downBlockValues[1]);
                  if (isStandable(blockView, add3)) {
                     addNeighborIfValid(blockView, pos, add3, arrayList);
                  }
               }
            } else {
               BlockPos add4 = pos.add(downBlockValues[0], 0, downBlockValues[1]);
               if (isStandable(blockView, add4)) {
                  addNeighborIfValid(blockView, pos, add4, arrayList);
               } else {
                  BlockPos add5 = pos.add(downBlockValues[0], 1, downBlockValues[1]);
                  if (!isPassableBlock(blockView, pos.up()) || !isStandable(blockView, add5) || !isStandable(blockView, pos) && !isSafeLowBlock(blockView, pos)) {
                     if (isPassableBlock(blockView, pos.up()) && isStandable(blockView, add5) && isWaterOverSolidFloor(blockView, pos)) {
                        addNeighborIfValid(blockView, pos, add5, arrayList);
                     } else {
                        for (int j = 1; j <= 2; j++) {
                           BlockPos add6 = pos.add(downBlockValues[0], -j, downBlockValues[1]);
                           if (isStandable(blockView, add6)) {
                              addNeighborIfValid(blockView, pos, add6, arrayList);
                              break;
                           }

                           if (!isPassableBlock(blockView, add6.up())) {
                              break;
                           }
                        }
                     }
                  } else {
                     addNeighborIfValid(blockView, pos, add5, arrayList);
                  }
               }
            }
         }

         int[][] ints2 = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

         for (int[] downValues : ints2) {
            BlockPos add7 = pos.add(downValues[0], 0, downValues[1]);
            if (!isStandable(blockView, add7) && isPassableBlock(blockView, add7) && isPassableBlock(blockView, add7.up()) && isPassableBlock(blockView, pos.up())) {
               boolean downBlockFlag2 = false;

               for (int k = 1; k <= 2; k++) {
                  if (isStandable(blockView, add7.down(k))) {
                     downBlockFlag2 = true;
                     break;
                  }
               }

               if (downBlockFlag2) {
                  BlockPos add8 = pos.add(downValues[0] * 2, 0, downValues[1] * 2);
                  if (isStandable(blockView, add8)) {
                     if (hasLandingAtOffset(blockView, add8, downValues)) {
                        addNeighborIfValid(blockView, pos, add8, arrayList);
                     }
                  } else {
                     BlockPos down2 = add8.down();
                     if (isPassableBlock(blockView, add8) && isStandable(blockView, down2) && hasLandingAtOffset(blockView, down2, downValues)) {
                        addNeighborIfValid(blockView, pos, down2, arrayList);
                     }
                  }
               }
            }
         }

         return arrayList;
      }
   }

   public static String getStandRejectReason(BlockView blockView, BlockPos pos) {
      if (blockView == null) {
         return "no_world";
      } else {
         BlockState blockState = blockView.getBlockState(pos.down());
         if (isHazardBlock(blockState)) {
            return "hazard";
         } else if (isPartialHeightBlock(blockState) && !(blockState.getBlock() instanceof HopperBlock)) {
            return "hopper_trap";
         } else {
            VoxelShape collisionShape = blockState.getCollisionShape(blockView, pos.down());
            if (collisionShape.isEmpty() || collisionShape.getMax(Axis.Y) < 0.8 || isBambooOrDoor(blockState) || blockState.getBlock() instanceof LadderBlock) {
               return "no_floor";
            } else if (blockState.isOf(Blocks.PISTON_HEAD) && blockState.contains(Properties.FACING) && blockState.get(Properties.FACING).getAxis() != Axis.Y) {
               return "no_floor";
            } else {
               BlockState blockState2 = blockView.getBlockState(pos);
               if (isHazardBlock(blockState2)) {
                  return "hazard";
               } else if (isPartialHeightBlock(blockState2)) {
                  return "hopper_trap";
               } else {
                  VoxelShape collisionShape2 = blockState2.getCollisionShape(blockView, pos);
                  if (!collisionShape2.isEmpty() && collisionShape2.getMax(Axis.Y) > 0.26) {
                     return "bad_collision";
                  } else {
                     BlockState blockState3 = blockView.getBlockState(pos.up());
                     if (isHazardBlock(blockState3)) {
                        return "hazard";
                     } else {
                        return !blockState3.getCollisionShape(blockView, pos.up()).isEmpty() ? "bad_headroom" : null;
                     }
                  }
               }
            }
         }
      }
   }

   public static Terrain createTerrainView(BlockView blockView) {
      final Mutable mutable = new Mutable();
      return new Terrain() {
         @Override
         public boolean solid(int solidValue, int solidValue2, int solidValue3) {
            mutable.set(solidValue, solidValue2, solidValue3);
            BlockState blockState = blockView.getBlockState(mutable);
            return WalkRouteFinder.isHazardBlock(blockState) || WalkRouteFinder.isPartialHeightBlock(blockState) || !blockState.getCollisionShape(blockView, mutable).isEmpty();
         }

         @Override
         public boolean canStandOn(int mutableValue, int mutableValue2, int mutableValue3) {
            mutable.set(mutableValue, mutableValue2, mutableValue3);
            BlockState blockState2 = blockView.getBlockState(mutable);
            if (WalkRouteFinder.isBambooOrDoor(blockState2)) {
               return false;
            } else if (!blockState2.getFluidState().isEmpty() && blockState2.getCollisionShape(blockView, mutable).isEmpty()) {
               return false;
            } else if (WalkRouteFinder.isHazardBlock(blockState2) && blockState2.getFluidState().isEmpty()) {
               return false;
            } else {
               VoxelShape collisionShape = blockState2.getCollisionShape(blockView, mutable);
               return !collisionShape.isEmpty() && collisionShape.getMax(Axis.Y) <= 0.26 && !this.canStandOn(mutableValue, mutableValue2 - 1, mutableValue3)
                  ? false
                  : (!WalkRouteFinder.isPartialHeightBlock(blockState2) || blockState2.getBlock() instanceof HopperBlock) && this.solid(mutableValue, mutableValue2, mutableValue3);
            }
         }
      };
   }

   public static List<BlockPos> findSortedStandPositions(BlockPos pos, double centerSquaredValue, Set<Long> set, Predicate<BlockPos> predicate, int centerSquaredLimit, int[] centerSquaredValues) {
      ClientWorld world = MC_CLIENT.world;
      var arrayList = new ArrayList();
      if (world != null && MC_CLIENT.player != null && centerSquaredLimit > 0) {
         int ceilReach = (int)Math.ceil(centerSquaredValue);
         Vec3d entityPos = MC_CLIENT.player.getEntityPos();
         ArrayList<double[]> arrayList2 = new ArrayList<>();

         for (int i = -ceilReach; i <= ceilReach; i++) {
            for (int j = -ceilReach; j <= ceilReach; j++) {
               for (int centerSquaredValuesItem : centerSquaredValues) {
                  BlockPos add = pos.add(i, centerSquaredValuesItem, j);
                  if (!add.equals(pos) && !add.equals(pos.down()) && (set == null || !set.contains(add.asLong())) && isStandable(world, add)) {
                     Vec3d vec = Vec3d.ofCenter(add).add(0.0, 1.12, 0.0);
                     double centerSquaredFactor = Math.max(1.0, centerSquaredValue - 0.6);
                     if (!(vec.squaredDistanceTo(Vec3d.ofCenter(pos)) > centerSquaredFactor * centerSquaredFactor)
                        && (predicate != null ? predicate.test(add) : hasLineOfSight(vec, Vec3d.ofCenter(pos)))) {
                        double centerSquaredValue3 = Vec3d.ofCenter(add).squaredDistanceTo(entityPos)
                           + (hasOpenNeighborInWorld(add) ? 24.0 : 0.0)
                           + Math.abs(centerSquaredValuesItem) * 2.0
                           + (add.getY() + 0.5 < entityPos.y - 0.6 ? 12.0 : 0.0);
                        arrayList2.add(new double[]{centerSquaredValue3, add.asLong()});
                     }
                  }
               }
            }
         }

         arrayList2.sort(Comparator.comparingDouble(centerSquaredValues2 -> centerSquaredValues2[0]));

         for (double[] centerSquaredValues3 : (Iterable<double[]>) (arrayList2)) {
            arrayList.add(BlockPos.fromLong((long)centerSquaredValues3[1]));
            if (arrayList.size() >= centerSquaredLimit) {
               break;
            }
         }

         return arrayList;
      } else {
         return arrayList;
      }
   }

   public static long getSearchStartPosKey() {
      return searchStartPosKey;
   }

   private static long[] appendVetoedKeys(long[] times) {
      if (VETOED_POS_KEYS.isEmpty()) {
         return times;
      } else {
         long[] longs = new long[(times == null ? 0 : times.length) + VETOED_POS_KEYS.size()];
         int blockPosCounter = 0;
         if (times != null) {
            for (long blockPosValue : times) {
               longs[blockPosCounter++] = blockPosValue;
            }
         }

         for (long vetoedPosKeysItem : VETOED_POS_KEYS) {
            longs[blockPosCounter++] = vetoedPosKeysItem;
         }

         return longs;
      }
   }

   public static boolean isHazardBlock(BlockState state) {
      if (!state.getFluidState().isEmpty()) {
         return true;
      } else {
         Block block = state.getBlock();
         return block instanceof CactusBlock
            || block instanceof MagmaBlock
            || block instanceof PowderSnowBlock
            || block instanceof SweetBerryBushBlock
            || block instanceof CampfireBlock
            || block == Blocks.FIRE
            || block == Blocks.SOUL_FIRE
            || block == Blocks.WITHER_ROSE
            || block == Blocks.LAVA
            || block == Blocks.COBWEB;
      }
   }

   private static boolean isPassableBlock(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      return !isHazardBlock(blockState) && !isPartialHeightBlock(blockState) ? blockState.getCollisionShape(blockView, pos).isEmpty() : false;
   }

   public static boolean isWaterOverSolidFloor(BlockView blockView, BlockPos pos) {
      return blockView.getBlockState(pos).isOf(Blocks.WATER) && isSolidFloorBlock(blockView, pos.down());
   }

   static boolean isAirBesideFlowingWater(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      if (blockState.isAir() && !blockView.getBlockState(pos.down()).getCollisionShape(blockView, pos.down()).isEmpty()) {
         for (Direction direction : Type.HORIZONTAL) {
            FluidState fluidState = blockView.getFluidState(pos.offset(direction));
            if (!fluidState.isEmpty() && !fluidState.isStill() && fluidState.getLevel() > 1) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static int getSearchBudget(BlockPos pos, BlockPos pos2) {
      int maxAbs = Math.max(Math.abs(pos.getX() - pos2.getX()), Math.abs(pos.getZ() - pos2.getZ()));
      int blockPosValue = maxAbs + Math.abs(pos.getY() - pos2.getY()) * 2;
      return Math.max(10000, Math.min(24000, 2000 + blockPosValue * 160));
   }

   public static boolean isSafeLowBlock(BlockView blockView, BlockPos pos) {
      BlockState blockState = blockView.getBlockState(pos);
      VoxelShape collisionShape = blockState.getCollisionShape(blockView, pos);
      if (!collisionShape.isEmpty() && !(collisionShape.getMax(Axis.Y) > 0.26) && !isPartialHeightBlock(blockState)) {
         return blockState.getFluidState().isEmpty() ? !isHazardBlock(blockState) : blockState.contains(Properties.WATERLOGGED);
      } else {
         return false;
      }
   }

   public static long[] getLastFailedFootprint() {
      return lastFailedFootprint;
   }

   public static Terrain createFluidFilteredTerrain(BlockView blockView, Terrain terrain, BlockPos pos) {
      final Mutable mutable = new Mutable();
      return new Terrain() {
         @Override
         public boolean canStandOn(int mutableValue, int mutableValue2, int mutableValue3) {
            return blockView.getFluidState(mutable.set(mutableValue, mutableValue2, mutableValue3)).isEmpty() || mutableValue == pos.getX() && mutableValue2 == pos.getY() - 1 && mutableValue3 == pos.getZ()
               ? terrain.canStandOn(mutableValue, mutableValue2, mutableValue3)
               : false;
         }

         @Override
         public boolean isThinCollision(int mutableValue, int y, int z) {
            return terrain.isThinCollision(mutableValue, y, z);
         }

         @Override
         public boolean solid(int solidValue, int candidateY, int candidateZ) {
            return terrain.solid(solidValue, candidateY, candidateZ);
         }
      };
   }

   public static BlockPos getBlockPos(BlockPos pos, double blockPosValue) {
      return getBlockPos(pos, blockPosValue, null);
   }

   public static boolean isStandable(BlockView blockView, BlockPos pos) {
      return getStandRejectReason(blockView, pos) == null;
   }

   public static BlockPos getBlockPos(BlockPos pos, double blockPosValue, Set<Long> set) {
      return findStandPosition(pos, blockPosValue, set, null, null);
   }

   private static boolean isDoorPassableFrom(BlockView blockView, BlockPos pos, int blockLimit, int deltaZ) {
      BlockState blockState = blockView.getBlockState(pos);
      BlockState blockState2 = blockView.getBlockState(pos.up());
      if (blockState.getBlock() instanceof DoorBlock && blockState2.getBlock() instanceof DoorBlock) {
         VoxelShape collisionShape = blockState2.getCollisionShape(blockView, pos.up());
         if (collisionShape.isEmpty()) {
            return true;
         } else {
            Box boundingBox = collisionShape.getBoundingBox();
            return (blockLimit <= 0 || !isShapeFlushToSide(boundingBox, Direction.EAST))
               && (blockLimit >= 0 || !isShapeFlushToSide(boundingBox, Direction.WEST))
               && (deltaZ <= 0 || !isShapeFlushToSide(boundingBox, Direction.SOUTH))
               && (deltaZ >= 0 || !isShapeFlushToSide(boundingBox, Direction.NORTH));
         }
      } else {
         return false;
      }
   }

   public static List<BlockPos> findPathWithBudget(BlockPos pos, BlockPos pos2, int blockPosBound) {
      ClientWorld world = MC_CLIENT.world;
      if (world == null) {
         return null;
      } else {
         int blockPosLimit = Math.min(getSearchBudget(pos, pos2), Math.max(1, blockPosBound));
         int blockPosValue = Math.max(Math.abs(pos.getX() - pos2.getX()), Math.abs(pos.getZ() - pos2.getZ())) + Math.abs(pos.getY() - pos2.getY()) * 2;
         searchStartPosKey = pos.asLong();
         if (pos.equals(pos2)) {
            lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(0, blockPosLimit, true, false, 0, blockPosValue);
            lastFailedFootprint = null;
            return new ArrayList<>(List.of(pos));
         } else {
            var hashMap = new HashMap();
            HashMap<Long, Double> hashMap2 = new HashMap<>();
            var hashSet = new HashSet();
            VETOED_POS_KEYS.clear();
            PriorityQueue<long[]> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(blockPosValues -> Double.longBitsToDouble(blockPosValues[1])));
            hashMap2.put(pos.asLong(), 0.0);
            priorityQueue.add(new long[]{pos.asLong(), Double.doubleToLongBits(1.5 * getHeuristicDistance(pos, pos2))});
            int blockPosCounter = 0;

            while (!priorityQueue.isEmpty() && blockPosCounter++ < blockPosLimit) {
               long blockPosValue2 = ((long[])priorityQueue.poll())[0];
               if (blockPosValue2 == pos2.asLong()) {
                  lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(blockPosCounter, blockPosLimit, true, false, priorityQueue.size(), blockPosValue);
                  lastFailedFootprint = null;
                  return reconstructPath(hashMap, blockPosValue2, pos);
               }

               if (hashSet.add(blockPosValue2)) {
                  BlockPos long2 = BlockPos.fromLong(blockPosValue2);
                  double orDefault = ((double) (Double) (hashMap2.getOrDefault(blockPosValue2, Double.MAX_VALUE)));

                  for (BlockPos blockPos : getNeighborMoves(world, long2)) {
                     long long3 = blockPos.asLong();
                     if (!hashSet.contains(long3)) {
                        double blockPosValue3 = 1.0 + 0.4 * Math.abs(blockPos.getY() - long2.getY());
                        double finish = orDefault + blockPosValue3;
                        if (finish < hashMap2.getOrDefault(long3, Double.MAX_VALUE)) {
                           hashMap.put(long3, blockPosValue2);
                           hashMap2.put(long3, finish);
                           priorityQueue.add(new long[]{long3, Double.doubleToLongBits(finish + 1.5 * getHeuristicDistance(blockPos, pos2))});
                        }
                     }
                  }
               }
            }

            lastPathDiagnostics = new WalkRouteFinder.PathDiagnostics(Math.min(blockPosCounter, blockPosLimit), blockPosLimit, false, !priorityQueue.isEmpty(), priorityQueue.size(), blockPosValue);
            long[] times2 = new long[hashSet.size() + priorityQueue.size() + VETOED_POS_KEYS.size()];
            int blockPosCounter2 = 0;

            for (long blockPosValue5 : (Iterable<Long>) (hashSet)) {
               times2[blockPosCounter2++] = blockPosValue5;
            }

            for (long[] blockPosValues2 : (Iterable<long[]>) (priorityQueue)) {
               times2[blockPosCounter2++] = blockPosValues2[0];
            }

            for (long vetoedPosKeysItem : VETOED_POS_KEYS) {
               times2[blockPosCounter2++] = vetoedPosKeysItem;
            }

            lastFailedFootprint = times2;
            return null;
         }
      }
   }

   public static Terrain createPartialHeightTerrain(BlockView blockView, Terrain terrain) {
      final Mutable mutable = new Mutable();
      return new Terrain() {
         @Override
         public boolean canStandOn(int mutableValue, int y, int mutableValue3) {
            mutable.set(mutableValue, y, mutableValue3);
            VoxelShape collisionShape = blockView.getBlockState(mutable).getCollisionShape(blockView, mutable);
            return !collisionShape.isEmpty() && collisionShape.getMax(Axis.Y) > 0.26 && collisionShape.getMax(Axis.Y) < 0.8 ? false : terrain.canStandOn(mutableValue, y, mutableValue3);
         }

         @Override
         public boolean isHoneyBlock(int mutableValue4, int mutableValue5, int mutableValue6) {
            mutable.set(mutableValue4, mutableValue5, mutableValue6);
            return blockView.getBlockState(mutable).isOf(Blocks.HONEY_BLOCK);
         }

         @Override
         public boolean isThinCollision(int mutableValue7, int mutableValue8, int mutableValue9) {
            mutable.set(mutableValue7, mutableValue8, mutableValue9);
            VoxelShape collisionShape2 = blockView.getBlockState(mutable).getCollisionShape(blockView, mutable);
            return !collisionShape2.isEmpty() && collisionShape2.getMax(Axis.Y) <= 0.26;
         }

         @Override
         public boolean solid(int solidValue, int candidateY, int candidateZ) {
            return terrain.solid(solidValue, candidateY, candidateZ);
         }
      };
   }

   @FunctionalInterface
   public interface EdgeVeto {
      boolean vetoed(BlockPos pos, BlockPos pos2, Kind kind);
   }

   public record PathDiagnostics(int expanded, int budget, boolean success, boolean budgetExhausted, int openRemaining, int distance) {
   }

   public record RoutePreflight(boolean executable, List<WalkRouteFinder.RouteStep> steps, int failedIndex, Result failure) {
   }

   public record RouteStep(BlockPos from, BlockPos to, Kind kind, Primitive primitive, Result result) {
   }

   public static final class StandStats {
      public int rejectedUnstandable;
      public int rejectedPlanned;
      public BlockPos selected;
      public int accepted;
      public int rejectedNoLos;
      public int scanned;
      public int rejectedOutOfReach;

      @Override
      public String toString() {
         return "scanned="
            + this.scanned
            + " accepted="
            + this.accepted
            + " rej[future="
            + this.rejectedPlanned
            + " stand="
            + this.rejectedUnstandable
            + " reach="
            + this.rejectedOutOfReach
            + " no_los="
            + this.rejectedNoLos
            + "] sel="
            + (this.selected == null ? "none" : this.selected.toShortString());
      }
   }
}
