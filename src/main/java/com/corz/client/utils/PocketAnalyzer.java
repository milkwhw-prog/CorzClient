package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BlockPosApi;
import com.corz.client.utils.PickaxeToolbox;

public final class PocketAnalyzer {
   public static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private static final Direction[] ALL_DIRECTIONS = new Direction[]{
      Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
   };

   public static BlockPos getFirstBlockerOnRay(BlockPosApi blockPosApi, Vec3d vec, Vec3d vec2, BlockPos pos) {
      if (blockPosApi != null && vec != null && vec2 != null) {
         List list = getBlocksAlongRay(vec, vec2);

         for (int i = 0; i < list.size() - 1; i++) {
            BlockPos blockPos = (BlockPos)list.get(i);
            if (!blockPos.equals(pos) && !blockPosApi.isPassable(blockPos)) {
               return blockPos;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static BlockPos getBlockPos(BlockPosApi blockPosApi, Vec3d vec, Vec3d vec2) {
      return getFirstBlockerOnRay(blockPosApi, vec, vec2, null);
   }

   static boolean areAllSidesSolid(BlockPosApi blockPosApi, BlockPos pos) {
      for (Direction direction : HORIZONTAL_DIRECTIONS) {
         if (blockPosApi.isPassable(pos.offset(direction))) {
            return false;
         }
      }

      return true;
   }

   public static int getChebyshevDistance(BlockPos pos, BlockPos pos2) {
      return Math.max(Math.abs(pos.getX() - pos2.getX()), Math.max(Math.abs(pos.getY() - pos2.getY()), Math.abs(pos.getZ() - pos2.getZ())));
   }

   public static PocketAnalyzer.Result analyzePocket(
      BlockPosApi blockPosApi, BlockPos pos, PickaxeToolbox suitableMultiplier, boolean offsetDirectionsFlag, Vec3d vec, Vec3d vec2
   ) {
      if (blockPosApi != null && pos != null) {
         PickaxeToolbox suitableMultiplier2 = suitableMultiplier == null ? PickaxeToolbox.EMPTY : suitableMultiplier;
         BlockPos immutable = pos.toImmutable();
         HashSet hashSet = new HashSet();
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();
         ArrayDeque arrayDeque = new ArrayDeque();

         for (BlockPos blockPos : new BlockPos[]{immutable, immutable.up()}) {
            if (blockPosApi.isPassable(blockPos) && hashSet.add(blockPos)) {
               arrayDeque.add(blockPos);
            }
         }

         boolean offsetDirectionsFlag2 = false;

         while (!arrayDeque.isEmpty()) {
            BlockPos totalPriceData = (BlockPos)arrayDeque.poll();
            if (arrayList.size() >= 27) {
               offsetDirectionsFlag2 = true;
               arrayList2.add(totalPriceData);
               break;
            }

            arrayList.add(totalPriceData);

            for (Direction direction : ALL_DIRECTIONS) {
               BlockPos offset = totalPriceData.offset(direction);
               if (hashSet.add(offset) && blockPosApi.isPassable(offset)) {
                  if (getChebyshevDistance(offset, immutable) >= 2) {
                     arrayList2.add(offset);
                  } else {
                     arrayDeque.add(offset);
                  }
               }
            }
         }

         if (!arrayDeque.isEmpty()) {
            offsetDirectionsFlag2 = true;
         }

         ArrayList arrayList3 = new ArrayList();

         for (Direction horizontalDirectionsItem : HORIZONTAL_DIRECTIONS) {
            BlockPos immutableOffset = immutable.offset(horizontalDirectionsItem);
            if (!blockPosApi.isPassable(immutableOffset)) {
               arrayList3.add(analyzeWall(blockPosApi, suitableMultiplier2, immutableOffset, 0, horizontalDirectionsItem));
            }
         }

         for (Direction direction3 : HORIZONTAL_DIRECTIONS) {
            BlockPos offset3 = immutable.up().offset(direction3);
            if (!blockPosApi.isPassable(offset3)) {
               arrayList3.add(analyzeWall(blockPosApi, suitableMultiplier2, offset3, 1, direction3));
            }
         }

         PocketAnalyzer.Wall wall = null;

         for (int i = 2; i <= 4; i++) {
            BlockPos up = immutable.up(i);
            if (!blockPosApi.isPassable(up)) {
               wall = analyzeWall(blockPosApi, suitableMultiplier2, up, 2, Direction.UP);
               break;
            }
         }

         PocketAnalyzer.Verdict verdict;
         if (arrayList2.isEmpty() && !offsetDirectionsFlag2) {
            verdict = PocketAnalyzer.Verdict.SEALED;
         } else if (!offsetDirectionsFlag2 && areEscapesAboveHead(arrayList2, immutable)) {
            verdict = PocketAnalyzer.Verdict.OPEN_TOP;
         } else if (areAllSidesSolid(blockPosApi, immutable)) {
            verdict = PocketAnalyzer.Verdict.HOLED;
         } else if (offsetDirectionsFlag) {
            verdict = PocketAnalyzer.Verdict.WALLED;
         } else {
            verdict = PocketAnalyzer.Verdict.OPEN;
         }

         PocketAnalyzer.Wall analyzeWall2 = null;
         if (vec != null && vec2 != null) {
            BlockPos blockPos3 = getBlockPos(blockPosApi, vec, vec2);
            if (blockPos3 != null) {
               analyzeWall2 = analyzeWall(blockPosApi, suitableMultiplier2, blockPos3, 3, null);
            }
         }

         return new PocketAnalyzer.Result(verdict, immutable, arrayList.size(), List.copyOf(arrayList), List.copyOf(arrayList2), List.copyOf(arrayList3), wall, analyzeWall2);
      } else {
         return PocketAnalyzer.Result.INSTANCE;
      }
   }

   public static boolean isRayClearTo(BlockPosApi blockPosApi, Vec3d vec, Vec3d vec2, BlockPos pos, BlockPos pos2) {
      if (blockPosApi != null && vec != null && vec2 != null) {
         for (BlockPos blockPos : getBlocksAlongRay(vec, vec2)) {
            if (pos != null && blockPos.equals(pos)) {
               return true;
            }

            if (!blockPos.equals(pos2) && !blockPosApi.isPassable(blockPos)) {
               return false;
            }
         }

         return pos == null;
      } else {
         return false;
      }
   }

   static PocketAnalyzer.Wall analyzeWall(BlockPosApi blockPosApi, PickaxeToolbox suitableMultiplier, BlockPos pos, int immutableTicksValue, Direction direction) {
      float hardnessWorld = blockPosApi.getHardnessWorld(pos);
      PocketAnalyzer.WallClass softHard = classifyWall(hardnessWorld, blockPosApi.getBlastResistance(pos));
      int immutableTicksValue2 = softHard == PocketAnalyzer.WallClass.NEVER ? Integer.MAX_VALUE : suitableMultiplier.findBestPick(blockPosApi, pos, true, true).ticks();
      PickaxeToolbox.Pick pick = softHard == PocketAnalyzer.WallClass.NEVER
         ? PickaxeToolbox.Pick.INSTANCE
         : suitableMultiplier.findBestPick(blockPosApi, pos, false, true);
      BlockPos down = pos.down();
      return new PocketAnalyzer.Wall(pos.toImmutable(), immutableTicksValue, direction, softHard, immutableTicksValue2, pick.ticks(), pick.slot(), blockPosApi.isBedrockOrObsidian(down), blockPosApi.hasSolidTopFace(down));
   }

   static boolean areEscapesAboveHead(List<BlockPos> list, BlockPos pos) {
      if (list.isEmpty()) {
         return false;
      } else {
         for (BlockPos blockPos : list) {
            if (blockPos.getY() < pos.getY() + 2) {
               return false;
            }

            if (Math.abs(blockPos.getX() - pos.getX()) > 1 || Math.abs(blockPos.getZ() - pos.getZ()) > 1) {
               return false;
            }
         }

         return true;
      }
   }

   public static List<BlockPos> getBlocksAlongRay(Vec3d vec, Vec3d vec2) {
      ArrayList arrayList = new ArrayList();
      int x = floorToInt(vec.x);
      int y = floorToInt(vec.y);
      int z = floorToInt(vec.z);
      double deltaX = vec2.x - vec.x;
      double deltaY = vec2.y - vec.y;
      double deltaZ = vec2.z - vec.z;
      int signum = (int)Math.signum(deltaX);
      int signum2 = (int)Math.signum(deltaY);
      int signum3 = (int)Math.signum(deltaZ);
      double posBlockFactor = deltaX == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaX);
      double posBlockFactor2 = deltaY == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaY);
      double posBlockFactor3 = deltaZ == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / deltaZ);
      double posBlockTotal = deltaX == 0.0 ? Double.POSITIVE_INFINITY : (deltaX > 0.0 ? x + 1 - vec.x : vec.x - x) * posBlockFactor;
      double hypot = deltaY == 0.0 ? Double.POSITIVE_INFINITY : (deltaY > 0.0 ? y + 1 - vec.y : vec.y - y) * posBlockFactor2;
      double posBlockLimit = deltaZ == 0.0 ? Double.POSITIVE_INFINITY : (deltaZ > 0.0 ? z + 1 - vec.z : vec.z - z) * posBlockFactor3;

      for (int i = 0; i < 64; i++) {
         double min2 = Math.min(posBlockTotal, Math.min(hypot, posBlockLimit));
         if (min2 > 1.0) {
            break;
         }

         if (posBlockTotal <= hypot && posBlockTotal <= posBlockLimit) {
            x += signum;
            posBlockTotal += posBlockFactor;
         } else if (hypot <= posBlockLimit) {
            y += signum2;
            hypot += posBlockFactor2;
         } else {
            z += signum3;
            posBlockLimit += posBlockFactor3;
         }

         arrayList.add(new BlockPos(x, y, z));
      }

      return arrayList;
   }

   private PocketAnalyzer() {
   }

   public static PocketAnalyzer.WallClass classifyWall(float softHardValue, float softHardLimit) {
      if (softHardValue < 0.0F) {
         return PocketAnalyzer.WallClass.NEVER;
      } else {
         return softHardLimit >= 600.0F ? PocketAnalyzer.WallClass.HARD : PocketAnalyzer.WallClass.SOFT;
      }
   }

   private static int floorToInt(double posBlockValue) {
      return (int)Math.floor(posBlockValue);
   }

   public record Result(
      PocketAnalyzer.Verdict verdict,
      BlockPos feet,
      int cells,
      List<BlockPos> pocket,
      List<BlockPos> escapes,
      List<PocketAnalyzer.Wall> walls,
      PocketAnalyzer.Wall roof,
      PocketAnalyzer.Wall los
   ) {
      public static final PocketAnalyzer.Result INSTANCE = new PocketAnalyzer.Result(
         PocketAnalyzer.Verdict.OPEN, null, 0, List.of(), List.of(), List.of(), null, null
      );

      public PocketAnalyzer.Wall getWallAt(BlockPos p) {
         if (p == null) {
            return null;
         } else {
            for (PocketAnalyzer.Wall next : this.walls) {
               if (next.pos().equals(p)) {
                  return next;
               }
            }

            if (this.roof != null && this.roof.pos().equals(p)) {
               return this.roof;
            } else {
               return this.los != null && this.los.pos().equals(p) ? this.los : null;
            }
         }
      }

      public List<PocketAnalyzer.Wall> getWallsLevel(int level) {
         ArrayList arrayList = new ArrayList();

         for (PocketAnalyzer.Wall wall : this.walls) {
            if (wall.level() == level) {
               arrayList.add(wall);
            }
         }

         return arrayList;
      }

      public boolean isNearPocket(BlockPos within, int p) {
         if (within != null && this.feet != null) {
            for (BlockPos blockPos : this.pocket) {
               if (PocketAnalyzer.getChebyshevDistance(blockPos, within) <= p) {
                  return true;
               }
            }

            return PocketAnalyzer.getChebyshevDistance(this.feet, within) <= p + 1;
         } else {
            return false;
         }
      }

      public boolean isSealedVerdict() {
         return this.verdict == PocketAnalyzer.Verdict.SEALED;
      }

      public boolean isEnclosed() {
         return this.verdict != PocketAnalyzer.Verdict.OPEN;
      }
   }

   public static enum Verdict {
      OPEN,
      WALLED,
      HOLED,
      OPEN_TOP,
      SEALED;
   }

   public record Wall(
      BlockPos pos,
      int level,
      Direction side,
      PocketAnalyzer.WallClass cls,
      int ticksHotbar,
      int ticksBest,
      int bestSlot,
      boolean crystalFloor,
      boolean solidFloor
   ) {
      public boolean canBreakWithinTicks(int maxTicks) {
         return this.cls != PocketAnalyzer.WallClass.NEVER && this.ticksBest != Integer.MAX_VALUE && this.ticksBest <= maxTicks;
      }
   }

   public static enum WallClass {
      NEVER,
      HARD,
      SOFT;
   }
}
