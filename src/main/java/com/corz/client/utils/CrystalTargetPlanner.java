package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BlockPosApi;
import com.corz.client.utils.PocketAnalyzer;
import com.corz.client.utils.PickaxeToolbox;
import com.corz.client.utils.BlockBreakMath;

public final class CrystalTargetPlanner {
   private static final double[][] FACE_AIM_OFFSETS = new double[][]{{0.5, 0.5}, {0.25, 0.25}, {0.75, 0.25}, {0.25, 0.75}, {0.75, 0.75}};
   private static final Direction[] ALL_DIRECTIONS = Direction.values();
   public static final int BLOCK_UNIT = 1;

   static boolean canReachFollowTarget(BlockPosApi blockPosApi, Vec3d vec, CrystalTargetPlanner.FollowUpKind followUpKind, BlockPos pos, BlockPos pos2) {
      if (followUpKind == CrystalTargetPlanner.FollowUpKind.NONE || pos == null) {
         return true;
      } else if (followUpKind == CrystalTargetPlanner.FollowUpKind.CRYSTAL) {
         Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0 - 0.02, pos.getZ() + 0.5);
         if (vec.distanceTo(vec3d) > 4.4) {
            return false;
         } else {
            return !PocketAnalyzer.isRayClearTo(blockPosApi, vec, vec3d, pos, pos2) ? false : distanceToBox(vec, getCrystalHitbox(pos)) <= 3.0;
         }
      } else {
         Vec3d center = Vec3d.ofCenter(pos);
         return vec.distanceTo(center) > 4.4 ? false : PocketAnalyzer.isRayClearTo(blockPosApi, vec, center, pos, pos2);
      }
   }

   public static Box getCrystalHitbox(BlockPos pos) {
      return new Box(pos.getX() - 0.5, pos.getY() + 1.0, pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 3.0, pos.getZ() + 1.5);
   }

   private static void addPlanIfPresent(List<CrystalTargetPlanner.Plan> list, CrystalTargetPlanner.Plan plan) {
      if (plan != null) {
         list.add(plan);
      }
   }

   static float getDefaultDamage(CrystalTargetPlanner.PlanKind entry) {
      return switch (entry) {
         case CITY_CRYSTAL -> 10.0F;
         case CITY_ANCHOR -> 5.7F;
         case ROOF_ANCHOR -> 5.4F;
         case OPEN_TOP_ANCHOR -> 5.4F;
         case SOFT_MINE -> 6.0F;
         case LOS_MINE -> 4.0F;
         case CAMP -> 0.0F;
      };
   }

   public static CrystalTargetPlanner.Plan getBestPlan(CrystalTargetPlanner.PlanInput in2, Predicate<BlockPos> predicate) {
      List losCls = collectPlans(in2);
      CrystalTargetPlanner.Plan minedBox = null;

      for (CrystalTargetPlanner.Plan plan : (Iterable<com.corz.client.utils.CrystalTargetPlanner.Plan>) (losCls)) {
         if ((predicate == null || plan.mined() == null || !predicate.test(plan.mined())) && (minedBox == null || plan.value() > minedBox.value() + 1.0E-9)) {
            minedBox = plan;
         }
      }

      if (minedBox != null) {
         return minedBox;
      } else {
         return in2 != null && in2.box() != null && in2.box().isSealedVerdict()
            ? new CrystalTargetPlanner.Plan(
               CrystalTargetPlanner.PlanKind.CAMP, null, null, null, null, CrystalTargetPlanner.FollowUpKind.NONE, null, null, -1, 0, 0, 0.0, 0.0, 1.0
            )
            : null;
      }
   }

   private CrystalTargetPlanner() {
   }

   static int getPathTicksTo(CrystalTargetPlanner.PlanInput in2, BlockPos pos) {
      if (in2.path() != null) {
         return in2.path().getPathTicks(pos);
      } else {
         Vec3d selfFeet = in2.selfFeet();
         if (selfFeet == null) {
            return 0;
         } else {
            double sqrt = Math.sqrt(selfFeet.squaredDistanceTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
            return (int)Math.ceil(sqrt / 0.25);
         }
      }
   }

   static boolean hasSolidNeighbor(BlockPosApi blockPosApi, BlockPos pos, BlockPos pos2) {
      for (Direction direction : ALL_DIRECTIONS) {
         BlockPos offset = pos.offset(direction);
         if (!offset.equals(pos2) && !blockPosApi.isPassable(offset) && blockPosApi.hasSolidTopFace(offset)) {
            return true;
         }
      }

      return false;
   }

   public static List<CrystalTargetPlanner.Plan> collectPlans(CrystalTargetPlanner.PlanInput in2) {
      ArrayList arrayList = new ArrayList();
      if (in2 != null && in2.view() != null && in2.box() != null && in2.box().feet() != null) {
         PocketAnalyzer.Result box = in2.box();
         if (!box.isEnclosed()) {
            return arrayList;
         } else {
            BlockPos feet = box.feet();
            HashMap hashMap = new HashMap();
            int losClsValue = box.isSealedVerdict() ? 200 : 60;

            for (PocketAnalyzer.Wall wall : box.getWallsLevel(0)) {
               if (wall.cls() == PocketAnalyzer.WallClass.HARD && wall.canBreakWithinTicks(losClsValue)) {
                  if (in2.crystals() && wall.crystalFloor()) {
                     addPlanIfPresent(arrayList, planForWall(in2, hashMap, CrystalTargetPlanner.PlanKind.CITY_CRYSTAL, wall, CrystalTargetPlanner.FollowUpKind.CRYSTAL, wall.pos().down()));
                  }

                  if (in2.anchors() && wall.solidFloor()) {
                     addPlanIfPresent(arrayList, planForWall(in2, hashMap, CrystalTargetPlanner.PlanKind.CITY_ANCHOR, wall, CrystalTargetPlanner.FollowUpKind.ANCHOR, wall.pos()));
                  }
               }
            }

            PocketAnalyzer.Wall roof = box.roof();
            boolean losClsFlag = box.isSealedVerdict() || box.verdict() == PocketAnalyzer.Verdict.OPEN_TOP;
            if (in2.anchors() && roof != null && losClsFlag && roof.cls() != PocketAnalyzer.WallClass.NEVER) {
               int maxTicks = roof.cls() == PocketAnalyzer.WallClass.HARD ? losClsValue : 160;
               BlockPos up = feet.up(2);
               if (roof.canBreakWithinTicks(maxTicks) && (roof.pos().equals(up) || in2.view().isPassable(up)) && hasSolidNeighbor(in2.view(), up, roof.pos())) {
                  addPlanIfPresent(arrayList, planForWall(in2, hashMap, CrystalTargetPlanner.PlanKind.ROOF_ANCHOR, roof, CrystalTargetPlanner.FollowUpKind.ANCHOR, up));
               }
            }

            if (in2.anchors() && box.verdict() == PocketAnalyzer.Verdict.OPEN_TOP && in2.view().isFreeToPlaceAt(feet.up(2))) {
               addPlanIfPresent(arrayList, planOpenTopAnchor(in2, hashMap, feet));
            }

            for (int i = 0; i <= 1; i++) {
               for (PocketAnalyzer.Wall getWallsLevelItem : box.getWallsLevel(i)) {
                  if (getWallsLevelItem.cls() == PocketAnalyzer.WallClass.SOFT && getWallsLevelItem.canBreakWithinTicks(160)) {
                     addPlanIfPresent(arrayList, planForWall(in2, hashMap, CrystalTargetPlanner.PlanKind.SOFT_MINE, getWallsLevelItem, CrystalTargetPlanner.FollowUpKind.NONE, null));
                  }
               }
            }

            if (box.verdict() == PocketAnalyzer.Verdict.WALLED && box.los() != null && box.los().canBreakWithinTicks(45)) {
               addPlanIfPresent(arrayList, planForWall(in2, hashMap, CrystalTargetPlanner.PlanKind.LOS_MINE, box.los(), CrystalTargetPlanner.FollowUpKind.NONE, null));
            }

            arrayList.sort(Comparator.comparingInt(obj -> ((com.corz.client.utils.CrystalTargetPlanner.Plan) obj).entry().ordinal()));
            return arrayList;
         }
      } else {
         return arrayList;
      }
   }

   static boolean isFirstBlockerBlastProof(BlockPosApi blockPosApi, Vec3d vec, Vec3d vec2, BlockPos pos) {
      if (vec == null) {
         return false;
      } else {
         BlockPos blockPos = PocketAnalyzer.getFirstBlockerOnRay(blockPosApi, vec, vec2, pos);
         return blockPos != null && blockPosApi.getBlastResistance(blockPos) >= 600.0F;
      }
   }

   static boolean isValidStandCell(
      CrystalTargetPlanner.PlanInput in2,
      Map<Long, Boolean> map,
      BlockPos pos,
      BlockPos pos2,
      BlockPos pos3,
      BlockPos pos4,
      BlockPos pos5,
      CrystalTargetPlanner.PlanKind planKind
   ) {
      BlockPosApi view = in2.view();
      if (!map.computeIfAbsent(pos.asLong(), long2 -> view.canStandAt(pos))) {
         return false;
      } else {
         BlockPos up = pos.up();

         for (BlockPos blockPos : in2.box().pocket()) {
            if (blockPos.equals(pos) || blockPos.equals(up)) {
               return false;
            }
         }

         if (pos2 != null) {
            if (pos.equals(pos2) || up.equals(pos2)) {
               return false;
            }

            if (pos.down().equals(pos2)) {
               return false;
            }

            if (pos.getX() == pos2.getX() && pos.getZ() == pos2.getZ() && pos2.getY() < pos.getY()) {
               return false;
            }
         }

         if (pos3 != null) {
            if (pos.equals(pos3) || up.equals(pos3)) {
               return false;
            }

            if (pos.getX() == pos3.getX() && pos.getZ() == pos3.getZ()) {
               return false;
            }
         }

         if (pos4 == null || !pos.equals(pos4) && !up.equals(pos4)) {
            if (planKind == CrystalTargetPlanner.PlanKind.CITY_CRYSTAL && pos2 != null) {
               Box box = new Box(pos2.getX(), pos2.getY(), pos2.getZ(), pos2.getX() + 1.0, pos2.getY() + 2.0, pos2.getZ() + 1.0);
               if (getPlayerBoxAt(pos).intersects(box)) {
                  return false;
               }
            }

            return planKind != CrystalTargetPlanner.PlanKind.ROOF_ANCHOR && planKind != CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR
               || pos.getX() != pos5.getX()
               || pos.getZ() != pos5.getZ();
         } else {
            return false;
         }
      }
   }

   static Vec3d getFacePoint(BlockPos pos2, Direction direction, double vec3dValue, double vec3dValue2) {
      double x = pos2.getX();
      double y = pos2.getY();
      double z = pos2.getZ();
      double vec3dValue3 = 0.02;

      return switch (direction) {
         case UP -> new Vec3d(x + vec3dValue, y + 1.0 - vec3dValue3, z + vec3dValue2);
         case DOWN -> new Vec3d(x + vec3dValue, y + vec3dValue3, z + vec3dValue2);
         case NORTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z + vec3dValue3);
         case SOUTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z + 1.0 - vec3dValue3);
         case WEST -> new Vec3d(x + vec3dValue3, y + vec3dValue2, z + vec3dValue);
         case EAST -> new Vec3d(x + 1.0 - vec3dValue3, y + vec3dValue2, z + vec3dValue);
      };
   }

   public static double computePlanValue(double posBlockValue, int posBlockBound, double anchorTopLimit) {
      return posBlockValue / Math.max(1, posBlockBound) - 0.25 * anchorTopLimit;
   }

   private static CrystalTargetPlanner.Plan planOpenTopAnchor(CrystalTargetPlanner.PlanInput in2, Map<Long, Boolean> map, BlockPos pos) {
      BlockPosApi view = in2.view();
      BlockPos up = pos.up(2);
      BlockPos blockPos = null;
      if (!hasSolidNeighbor(view, up, null)) {
         if (!in2.obsidian()) {
            return null;
         }

         for (Direction direction : PocketAnalyzer.HORIZONTAL_DIRECTIONS) {
            BlockPos offset = up.offset(direction);
            if (view.isFreeToPlaceAt(offset) && view.hasSolidTopFace(offset.down())) {
               blockPos = offset;
               break;
            }
         }

         if (blockPos == null) {
            return null;
         }
      }

      List centerImmutable = findStandCandidates(in2, map, CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR, null, CrystalTargetPlanner.FollowUpKind.ANCHOR, up, null, pos);
      float anchorTopValue = in2.damage() == null
         ? getDefaultDamage(CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR)
         : in2.damage().getTargetDamage(CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR, null, up);
      CrystalTargetPlanner.Plan anchorTop = null;
      int anchorTopCounter = 0;

      for (CrystalTargetPlanner.Stand stand : (Iterable<com.corz.client.utils.CrystalTargetPlanner.Stand>) (centerImmutable)) {
         if (in2.damage() != null && anchorTopCounter++ >= 3) {
            break;
         }

         Vec3d vec3d = getEyePosAt(stand.feet());
         if (blockPos != null) {
            Vec3d vec3d2 = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() - 0.02, blockPos.getZ() + 0.5);
            if (vec3d.distanceTo(vec3d2) > 4.4 || !PocketAnalyzer.isRayClearTo(view, vec3d, vec3d2, blockPos.down(), null)) {
               continue;
            }
         }

         int posBlockBound = stand.pathTicks() + 3 + (blockPos != null ? 1 : 0);
         float anchorTopLimit = in2.damage() == null ? 0.0F : in2.damage().getSelfDamage(CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR, stand.feet(), null, up);
         if (!(in2.ehp() > 0.0F) || !(anchorTopLimit >= in2.ehp() - 1.0F)) {
            double planValue = computePlanValue(anchorTopValue, posBlockBound, anchorTopLimit);
            if (anchorTop == null || planValue > anchorTop.value()) {
               anchorTop = new CrystalTargetPlanner.Plan(
                  CrystalTargetPlanner.PlanKind.OPEN_TOP_ANCHOR,
                  null,
                  null,
                  Vec3d.ofCenter(up),
                  stand.feet(),
                  CrystalTargetPlanner.FollowUpKind.ANCHOR,
                  up,
                  blockPos,
                  -1,
                  0,
                  posBlockBound,
                  anchorTopValue,
                  anchorTopLimit,
                  planValue
               );
            }
         }
      }

      return anchorTop;
   }

   public static CrystalTargetPlanner.Aim findVisibleFace(BlockPosApi blockPosApi, Vec3d vec, BlockPos pos, double distanceValue) {
      CrystalTargetPlanner.Aim distanceAmounts = null;
      double distanceLimit = Double.MAX_VALUE;

      for (Direction direction : ALL_DIRECTIONS) {
         if (isEyeOutsideFace(vec, pos, direction)) {
            for (double[] distanceValues : FACE_AIM_OFFSETS) {
               Vec3d vec3d = getFacePoint(pos, direction, distanceValues[0], distanceValues[1]);
               double distanceTo = vec.distanceTo(vec3d);
               if (!(distanceTo > distanceValue) && PocketAnalyzer.isRayClearTo(blockPosApi, vec, vec3d, pos, null)) {
                  if (distanceTo < distanceLimit) {
                     distanceLimit = distanceTo;
                     distanceAmounts = new CrystalTargetPlanner.Aim(direction, vec3d);
                  }
                  break;
               }
            }
         }
      }

      return distanceAmounts;
   }

   private static CrystalTargetPlanner.Plan planForWall(
      CrystalTargetPlanner.PlanInput in2,
      Map<Long, Boolean> map,
      CrystalTargetPlanner.PlanKind entry,
      PocketAnalyzer.Wall wall,
      CrystalTargetPlanner.FollowUpKind follow,
      BlockPos pos
   ) {
      BlockPosApi view = in2.view();
      BlockPos pos2 = wall.pos();
      BlockPos feet = in2.box().feet();
      BlockPos blockPos = null;
      if (wall.side() != null && wall.level() <= 1) {
         blockPos = pos2.offset(wall.side());
      }

      List centerImmutable = findStandCandidates(in2, map, entry, pos2, follow, pos, blockPos, feet);
      if (centerImmutable.isEmpty()) {
         return null;
      } else {
         boolean ticksBestFlag = wall.ticksHotbar() != Integer.MAX_VALUE && wall.ticksHotbar() <= wall.ticksBest();
         int ticksBestValue = !ticksBestFlag && wall.bestSlot() >= 0 ? 2 : 0;
         int ticksBestValue2 = ticksBestFlag ? wall.ticksHotbar() : wall.ticksBest();
         int bestSlot = wall.bestSlot();
         if (ticksBestFlag && in2.tools() != null) {
            PickaxeToolbox.Pick suitableMultiplier = in2.tools().findBestPick(view, pos2, true, true);
            bestSlot = suitableMultiplier.slot();
         }

         float posBlockValue = in2.damage() == null ? getDefaultDamage(entry) : in2.damage().getTargetDamage(entry, pos2, pos);
         CrystalTargetPlanner.Plan ticksBest = null;
         int ticksBestCounter = 0;

         for (CrystalTargetPlanner.Stand stand : (Iterable<com.corz.client.utils.CrystalTargetPlanner.Stand>) (centerImmutable)) {
            if (in2.damage() != null && follow != CrystalTargetPlanner.FollowUpKind.NONE && ticksBestCounter++ >= 3) {
               break;
            }

            int addTicksSaturating = BlockBreakMath.addTicksSaturating(BlockBreakMath.addTicksSaturating(stand.pathTicks(), ticksBestValue + getFollowTickCost(follow)), ticksBestValue2);
            if (addTicksSaturating != Integer.MAX_VALUE) {
               float ticksBestLimit = in2.damage() != null && follow != CrystalTargetPlanner.FollowUpKind.NONE
                  ? in2.damage().getSelfDamage(entry, stand.feet(), pos2, pos)
                  : 0.0F;
               if (follow == CrystalTargetPlanner.FollowUpKind.NONE || !(in2.ehp() > 0.0F) || !(ticksBestLimit >= in2.ehp() - 1.0F)) {
                  double ticksBestLimit2 = computePlanValue(posBlockValue, addTicksSaturating, ticksBestLimit) + (stand.cover() ? 1.0E-6 : 0.0);
                  if (ticksBest == null || ticksBestLimit2 > ticksBest.value()) {
                     ticksBest = new CrystalTargetPlanner.Plan(
                        entry, pos2, stand.aim().face(), stand.aim().point(), stand.feet(), follow, pos, null, bestSlot, ticksBestValue2, addTicksSaturating, posBlockValue, ticksBestLimit, ticksBestLimit2
                     );
                  }
               }
            }
         }

         return ticksBest;
      }
   }

   public static Vec3d getEyePosAt(BlockPos pos) {
      return new Vec3d(pos.getX() + 0.5, pos.getY() + 1.62, pos.getZ() + 0.5);
   }

   static List<CrystalTargetPlanner.Stand> findStandCandidates(
      CrystalTargetPlanner.PlanInput in2,
      Map<Long, Boolean> map,
      CrystalTargetPlanner.PlanKind planKind,
      BlockPos pos,
      CrystalTargetPlanner.FollowUpKind followUpKind,
      BlockPos pos2,
      BlockPos pos3,
      BlockPos pos4
   ) {
      BlockPosApi view = in2.view();
      BlockPos blockPos = pos != null ? pos : pos2;
      ArrayList arrayList = new ArrayList();

      for (int i = -4; i <= 4; i++) {
         for (int j = -4; j <= 4; j++) {
            for (int k = -4; k <= 2; k++) {
               BlockPos add = blockPos.add(i, k, j);
               Vec3d vec3d = getEyePosAt(add);
               if (!(vec3d.squaredDistanceTo(Vec3d.ofCenter(blockPos)) > 28.090000000000007)
                  && isValidStandCell(in2, map, add, pos, pos2, pos3, pos4, planKind)) {
                  arrayList.add(add);
               }
            }
         }
      }

      Vec3d selfFeet = in2.selfFeet();
      arrayList.sort(Comparator.comparingDouble(self -> selfFeet == null ? 0.0 : selfFeet.squaredDistanceTo(((net.minecraft.util.math.BlockPos) self).getX() + 0.5, ((net.minecraft.util.math.BlockPos) self).getY(), ((net.minecraft.util.math.BlockPos) self).getZ() + 0.5)));
      ArrayList arrayList2 = new ArrayList();
      int centerImmutableCounter = 0;

      for (BlockPos blockPos2 : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
         if (centerImmutableCounter++ >= 40) {
            break;
         }

         Vec3d vec3d2 = getEyePosAt(blockPos2);
         CrystalTargetPlanner.Aim aim = null;
         if (pos != null) {
            aim = findVisibleFace(view, vec3d2, pos, 4.4);
            if (aim == null) {
               continue;
            }
         }

         if (canReachFollowTarget(view, vec3d2, followUpKind, pos2, pos)) {
            int pathSquared = getPathTicksTo(in2, blockPos2);
            if (pathSquared >= 0) {
               boolean centerImmutableFlag = followUpKind != CrystalTargetPlanner.FollowUpKind.NONE && isFirstBlockerBlastProof(view, getFollowTargetPoint(followUpKind, pos2), vec3d2, pos);
               double centerImmutableValue = -0.05 * pathSquared + (centerImmutableFlag ? 2.0 : 0.0);
               arrayList2.add(
                  new CrystalTargetPlanner.Stand(
                     blockPos2.toImmutable(),
                     aim == null ? new CrystalTargetPlanner.Aim(null, pos2 == null ? null : Vec3d.ofCenter(pos2)) : aim,
                     pathSquared,
                     centerImmutableFlag,
                     centerImmutableValue
                  )
               );
            }
         }
      }

      arrayList2.sort(Comparator.comparingDouble(CrystalTargetPlanner.Stand::score).reversed());
      return (List<CrystalTargetPlanner.Stand>)(arrayList2.size() > 6 ? arrayList2.subList(0, 6) : arrayList2);
   }

   public static double distanceToBox(Vec3d vec, Box box) {
      double posBlockFactor = Math.max(0.0, Math.max(box.minX - vec.x, vec.x - box.maxX));
      double max2 = Math.max(0.0, Math.max(box.minY - vec.y, vec.y - box.maxY));
      double max3 = Math.max(0.0, Math.max(box.minZ - vec.z, vec.z - box.maxZ));
      return Math.sqrt(posBlockFactor * posBlockFactor + max2 * max2 + max3 * max3);
   }

   static int getFollowTickCost(CrystalTargetPlanner.FollowUpKind follow) {
      return switch (follow) {
         case NONE -> 0;
         case CRYSTAL -> 2;
         case ANCHOR -> 3;
      };
   }

   static Vec3d getFollowTargetPoint(CrystalTargetPlanner.FollowUpKind followUpKind, BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         return followUpKind == CrystalTargetPlanner.FollowUpKind.CRYSTAL ? new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5) : Vec3d.ofCenter(pos);
      }
   }

   static Box getPlayerBoxAt(BlockPos pos) {
      return new Box(pos.getX() + 0.2, pos.getY(), pos.getZ() + 0.2, pos.getX() + 0.8, pos.getY() + 1.8, pos.getZ() + 0.8);
   }

   public static CrystalTargetPlanner.Plan getBestPlanUnfiltered(CrystalTargetPlanner.PlanInput in2) {
      return getBestPlan(in2, null);
   }

   static boolean isEyeOutsideFace(Vec3d vec, BlockPos pos2, Direction direction) {
      return switch (direction) {
         case UP -> vec.y > pos2.getY() + 1.0;
         case DOWN -> vec.y < pos2.getY();
         case NORTH -> vec.z < pos2.getZ();
         case SOUTH -> vec.z > pos2.getZ() + 1.0;
         case WEST -> vec.x < pos2.getX();
         case EAST -> vec.x > pos2.getX() + 1.0;
      };
   }

   public record Aim(Direction face, Vec3d point) {
   }

   public interface DamageFn {
      float getTargetDamage(CrystalTargetPlanner.PlanKind entry, BlockPos pos, BlockPos pos2);

      float getSelfDamage(CrystalTargetPlanner.PlanKind entry, BlockPos pos, BlockPos pos2, BlockPos pos3);
   }

   public static enum PlanKind {
      CITY_CRYSTAL,
      CITY_ANCHOR,
      ROOF_ANCHOR,
      OPEN_TOP_ANCHOR,
      SOFT_MINE,
      LOS_MINE,
      CAMP;
   }

   public static enum FollowUpKind {
      NONE,
      CRYSTAL,
      ANCHOR;
   }

   public record PlanInput(
      BlockPosApi view,
      PocketAnalyzer.Result box,
      PickaxeToolbox tools,
      Vec3d selfFeet,
      boolean crystals,
      boolean anchors,
      boolean obsidian,
      float ehp,
      CrystalTargetPlanner.DamageFn damage,
      CrystalTargetPlanner.PathFn path
   ) {
   }

   public interface PathFn {
      int getPathTicks(BlockPos pos);
   }

   public record Plan(
      CrystalTargetPlanner.PlanKind entry,
      BlockPos mined,
      Direction face,
      Vec3d aim,
      BlockPos stand,
      CrystalTargetPlanner.FollowUpKind follow,
      BlockPos followCell,
      BlockPos build,
      int toolSlot,
      int mineTicks,
      int eta,
      double damage,
      double selfCost,
      double value
   ) {
      public boolean hasMineTarget() {
         return this.mined != null;
      }

      public boolean isCamp() {
         return this.entry == CrystalTargetPlanner.PlanKind.CAMP;
      }
   }

   record Stand(BlockPos feet, CrystalTargetPlanner.Aim aim, int pathTicks, boolean cover, double score) {
   }
}
