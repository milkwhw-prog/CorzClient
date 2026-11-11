package com.corz.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public final class AxisPosUtils {
   public static final int UNIT_STEP = 1;

   public static boolean canFlag(int axisPosValue, int cxx, int bestD, Axis axis, int fxx, int directionOffsetValue, int outside) {
      return switch (axis) {
         case X -> fxx == axisPosValue && Math.abs(directionOffsetValue - cxx) <= 1 && Math.abs(outside - bestD) <= 1;
         case Y -> directionOffsetValue == cxx && Math.abs(fxx - axisPosValue) <= 1 && Math.abs(outside - bestD) <= 1;
         case Z -> outside == bestD && Math.abs(fxx - axisPosValue) <= 1 && Math.abs(directionOffsetValue - cxx) <= 1;
      };
   }

   public static void handleVisit(int visitValue, int cover, int ey2, Axis axis, AxisPosUtils.CellVisitor cellVisitor) {
      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            switch (axis) {
               case X:
                  cellVisitor.visit(visitValue, cover + i, ey2 + j);
                  break;
               case Y:
                  cellVisitor.visit(visitValue + i, cover, ey2 + j);
                  break;
               case Z:
                  cellVisitor.visit(visitValue + i, cover + j, ey2);
            }
         }
      }
   }

   public static BlockPos[] getBlockPoss(BlockPos pos, Direction direction) {
      BlockPos[] blockPos = new BlockPos[9];
      int[] ints = new int[]{0};
      handleAxis(pos, direction, (out, y, z2) -> blockPos[ints[0]++] = new BlockPos(out, y, z2));
      return blockPos;
   }

   private AxisPosUtils() {
   }

   public static Direction getDirection(Vec3d vec, BlockPos pos) {
      return getSouthNorth(vec.x, vec.y, vec.z, pos.getX(), pos.getY(), pos.getZ());
   }

   public static void handleAxis(BlockPos pos, Direction direction, AxisPosUtils.CellVisitor cellVisitor) {
      handleVisit(pos.getX(), pos.getY(), pos.getZ(), direction.getAxis(), cellVisitor);
   }

   public static Direction getSouthNorth(double southNorthValue, double southNorthValue2, double southNorthValue3, int southNorthValue4, int southNorthValue5, int southNorthValue6) {
      double southNorthLimit = southNorthValue - (southNorthValue4 + 0.5);
      double southNorthLimit2 = southNorthValue2 - (southNorthValue5 + 0.5);
      double spotValue = southNorthValue3 - (southNorthValue6 + 0.5);
      double abs4 = Math.abs(southNorthLimit);
      double abs5 = Math.abs(southNorthLimit2);
      double abs7 = Math.abs(spotValue);
      if (abs4 >= abs5 && abs4 >= abs7) {
         return southNorthLimit >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (abs5 >= abs7) {
         return southNorthLimit2 >= 0.0 ? Direction.UP : Direction.DOWN;
      } else {
         return spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public static Axis[] getAxiss(Axis axis) {
      return switch (axis) {
         case X -> new Axis[]{Axis.Y, Axis.Z};
         case Y -> new Axis[]{Axis.X, Axis.Z};
         case Z -> new Axis[]{Axis.X, Axis.Y};
      };
   }

   @FunctionalInterface
   public interface CellVisitor {
      void visit(int visitValue, int cover, int ey2);
   }
}
