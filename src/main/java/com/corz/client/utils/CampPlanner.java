package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BlockPosApi;
import com.corz.client.utils.PocketAnalyzer;
import com.corz.client.utils.CrystalTargetPlanner;

public final class CampPlanner {

   public static CampPlanner.Side findBuildSide(BlockPosApi blockPosApi, PocketAnalyzer.Result result, Direction direction, boolean downOffsetFlag) {
      if (blockPosApi != null && result != null && result.feet() != null && direction != null) {
         BlockPos offset = result.feet().offset(direction);
         if (blockPosApi.isPassable(offset)) {
            return null;
         } else {
            BlockPos offsetOffset = offset.offset(direction);
            if (!blockPosApi.isFreeToPlaceAt(offsetOffset)) {
               return null;
            } else {
               BlockPos down = offsetOffset.down();
               if (blockPosApi.isBedrockOrObsidian(down)) {
                  return new CampPlanner.Side(direction, offset, offsetOffset, down, false);
               } else {
                  return downOffsetFlag && blockPosApi.isFreeToPlaceAt(down) && blockPosApi.isSolidSupportBlock(down.down()) ? new CampPlanner.Side(direction, offset, offsetOffset, down, true) : null;
               }
            }
         }
      } else {
         return null;
      }
   }

   public static CampPlanner.PunishPlan getPunishPlan(
      BlockPosApi blockPosApi, PocketAnalyzer.Result result, BlockPos pos, long cityDownLimit, CampPlanner.Side side, boolean cityDownFlag
   ) {
      if (blockPosApi != null && result != null && result.feet() != null && pos != null) {
         BlockPos feet = result.feet();
         if (pos.getX() == feet.getX() && pos.getZ() == feet.getZ()) {
            if (pos.getY() < feet.getY()) {
               return new CampPlanner.PunishPlan(CampPlanner.PunishAction.FOLLOW, null, pos);
            }

            if (pos.getY() >= feet.getY() + 2) {
               return new CampPlanner.PunishPlan(CampPlanner.PunishAction.ROOF_ANCHOR, feet.up(2), pos);
            }
         }

         if (cityDownLimit <= 0L && cityDownFlag && side != null && side.wall().equals(pos)) {
            return new CampPlanner.PunishPlan(CampPlanner.PunishAction.BREAK_PRE, side.base(), pos);
         } else if (cityDownLimit > 5L) {
            return CampPlanner.PunishPlan.INSTANCE;
         } else {
            boolean cityDownFlag2 = pos.getY() == feet.getY();
            return cityDownFlag2 && blockPosApi.isBedrockOrObsidian(pos.down())
               ? new CampPlanner.PunishPlan(CampPlanner.PunishAction.CITY_CRYSTAL, pos.down(), pos)
               : new CampPlanner.PunishPlan(CampPlanner.PunishAction.CITY_ANCHOR, pos, pos);
         }
      } else {
         return CampPlanner.PunishPlan.INSTANCE;
      }
   }

   static boolean canReachSideBase(BlockPosApi blockPosApi, Vec3d vec, CampPlanner.Side side) {
      BlockPos base = side.base();
      if (side.buildBase()) {
         BlockPos down = base.down();
         Vec3d vec3d = new Vec3d(down.getX() + 0.5, down.getY() + 1.0 - 0.02, down.getZ() + 0.5);
         if (vec.distanceTo(vec3d) > 4.4 || !PocketAnalyzer.isRayClearTo(blockPosApi, vec, vec3d, down, null)) {
            return false;
         }
      } else {
         Vec3d vec3d2 = new Vec3d(base.getX() + 0.5, base.getY() + 1.0 - 0.02, base.getZ() + 0.5);
         if (vec.distanceTo(vec3d2) > 4.4 || !PocketAnalyzer.isRayClearTo(blockPosApi, vec, vec3d2, base, null)) {
            return false;
         }
      }

      return CrystalTargetPlanner.distanceToBox(vec, CrystalTargetPlanner.getCrystalHitbox(base)) <= 3.0;
   }

   public static CampPlanner.Camp findCampSpot(BlockPosApi blockPosApi, PocketAnalyzer.Result result, Vec3d vec, BlockPos pos, boolean immutableFlag) {
      if (blockPosApi != null && result != null && result.feet() != null) {
         BlockPos feet = result.feet();
         Direction sideFeet = getApproachDirection(result, pos, vec);
         ArrayList arrayList = new ArrayList();
         CampPlanner.Side downOffset = findBuildSide(blockPosApi, result, sideFeet, immutableFlag);
         if (downOffset != null) {
            arrayList.add(downOffset);
         }

         for (Direction direction : getDirectionsSortedToward(sideFeet, vec, feet)) {
            if (direction != sideFeet) {
               CampPlanner.Side buildSide = findBuildSide(blockPosApi, result, direction, immutableFlag);
               if (buildSide != null) {
                  arrayList.add(buildSide);
               }
            }
         }

         BlockPos offset = feet.offset(sideFeet);
         CampPlanner.Camp baseImmutable = null;
         double immutableValue = Double.NEGATIVE_INFINITY;

         for (int i = -5; i <= 5; i++) {
            for (int j = -5; j <= 5; j++) {
               for (int k = -2; k <= 2; k++) {
                  BlockPos add = feet.add(i, k, j);
                  double hypot = Math.hypot(add.getX() - offset.getX(), add.getZ() - offset.getZ());
                  if (!(hypot < 2.5) && !(hypot > 4.0) && isValidCampCell(blockPosApi, result, add, arrayList)) {
                     Vec3d vec3d = CrystalTargetPlanner.getEyePosAt(add);
                     ArrayList arrayList2 = new ArrayList();
                     boolean immutableFlag2 = false;

                     for (CampPlanner.Side side : (Iterable<com.corz.client.utils.CampPlanner.Side>) (arrayList)) {
                        if (arrayList2.size() >= 2) {
                           break;
                        }

                        if (canReachSideBase(blockPosApi, vec3d, side)) {
                           arrayList2.add(side);
                           Vec3d vec3d2 = new Vec3d(side.base().getX() + 0.5, side.base().getY() + 1.0, side.base().getZ() + 0.5);
                           if (PocketAnalyzer.getBlockPos(blockPosApi, vec3d2, vec3d) != null) {
                              immutableFlag2 = true;
                           }
                        }
                     }

                     boolean immutableFlag3 = downOffset != null && arrayList2.contains(downOffset);
                     double immutableValue2 = vec == null ? 0.0 : Math.sqrt(vec.squaredDistanceTo(add.getX() + 0.5, add.getY(), add.getZ() + 0.5));
                     double immutableLimit = 10.0 * arrayList2.size() + (immutableFlag3 ? 10.0 : 0.0) + (immutableFlag2 ? 2.0 : 0.0) - 0.1 * immutableValue2;
                     if (immutableLimit > immutableValue) {
                        immutableValue = immutableLimit;
                        baseImmutable = new CampPlanner.Camp(add.toImmutable(), List.copyOf(arrayList2), immutableFlag2);
                     }
                  }
               }
            }
         }

         return baseImmutable;
      } else {
         return null;
      }
   }

   private CampPlanner() {
   }

   static boolean isValidCampCell(BlockPosApi blockPosApi, PocketAnalyzer.Result result, BlockPos pos, List<CampPlanner.Side> list) {
      if (!blockPosApi.canStandAt(pos)) {
         return false;
      } else {
         BlockPos up = pos.up();

         for (BlockPos blockPos : result.pocket()) {
            if (blockPos.equals(pos) || blockPos.equals(up)) {
               return false;
            }
         }

         BlockPos feet = result.feet();
         if (pos.getX() == feet.getX() && pos.getZ() == feet.getZ()) {
            return false;
         } else {
            for (Direction direction : PocketAnalyzer.HORIZONTAL_DIRECTIONS) {
               BlockPos offset = feet.offset(direction);

               for (int i = 1; i <= 3; i++) {
                  BlockPos offsetOffset = offset.offset(direction, i);
                  if (offsetOffset.getX() == pos.getX() && offsetOffset.getZ() == pos.getZ() && Math.abs(offsetOffset.getY() - pos.getY()) <= 1) {
                     return false;
                  }
               }
            }

            for (CampPlanner.Side side : list) {
               if (side.outside().equals(pos) || side.outside().equals(up) || side.base().equals(pos.down())) {
                  return false;
               }
            }

            int offsetDirectionsCounter = 0;

            for (Direction direction2 : PocketAnalyzer.HORIZONTAL_DIRECTIONS) {
               if (blockPosApi.isPassable(pos.offset(direction2)) && blockPosApi.isPassable(up.offset(direction2))) {
                  offsetDirectionsCounter++;
               }
            }

            return offsetDirectionsCounter >= 2;
         }
      }
   }

   static List<Direction> getDirectionsSortedToward(Direction direction, Vec3d vec, BlockPos pos) {
      ArrayList arrayList = new ArrayList<>(List.of(PocketAnalyzer.HORIZONTAL_DIRECTIONS));
      double directionsFactor = vec == null ? 0.0 : vec.x - (pos.getX() + 0.5);
      double directionsFactor2 = vec == null ? 0.0 : vec.z - (pos.getZ() + 0.5);
      arrayList.sort((obj, obj2) -> Double.compare(((net.minecraft.util.math.Direction) obj2).getOffsetX() * directionsFactor + ((net.minecraft.util.math.Direction) obj2).getOffsetZ() * directionsFactor2, ((net.minecraft.util.math.Direction) obj).getOffsetX() * directionsFactor + ((net.minecraft.util.math.Direction) obj).getOffsetZ() * directionsFactor2));
      arrayList.remove(direction);
      arrayList.add(0, direction);
      return arrayList;
   }

   public static Direction getApproachDirection(PocketAnalyzer.Result result, BlockPos pos, Vec3d vec) {
      if (result != null && result.feet() != null) {
         if (pos != null) {
            for (PocketAnalyzer.Wall wall : result.walls()) {
               if (wall.level() <= 1 && wall.side() != null && wall.pos().equals(pos)) {
                  return wall.side();
               }
            }
         }

         BlockPos feet = result.feet();
         double sideFeetLimit = vec == null ? 1.0 : vec.x - (feet.getX() + 0.5);
         double spotValue = vec == null ? 0.0 : vec.z - (feet.getZ() + 0.5);
         if (Math.abs(sideFeetLimit) >= Math.abs(spotValue)) {
            return sideFeetLimit >= 0.0 ? Direction.EAST : Direction.WEST;
         } else {
            return spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH;
         }
      } else {
         return null;
      }
   }

   public static enum PunishAction {
      NONE,
      BREAK_PRE,
      CITY_CRYSTAL,
      CITY_ANCHOR,
      ROOF_ANCHOR,
      FOLLOW;
   }

   public record Camp(BlockPos stand, List<CampPlanner.Side> sides, boolean cover) {
   }

   public record PunishPlan(CampPlanner.PunishAction act, BlockPos cell, BlockPos opened) {
      public static final CampPlanner.PunishPlan INSTANCE = new CampPlanner.PunishPlan(CampPlanner.PunishAction.NONE, null, null);
   }

   public record Side(Direction dir, BlockPos wall, BlockPos outside, BlockPos base, boolean buildBase) {
   }
}
