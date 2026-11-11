package com.corz.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import com.corz.client.utils.DirectionOffsetUtils2;

public final class DirectionAxis {
   private final boolean depthAxisActive;
   private final int lifts;
   private final int maxStepCount;
   private final DirectionOffsetUtilsUseful.Bounds axisHelper;
   private final Direction advance;
   private final Axis bandAxis;
   private final int bandsSweepsValue;
   private final boolean directionAxisActive;
   private final Axis depth;
   private final Axis sweep;
   private final int[] sweeps;
   private final int[] bands;
   private final DirectionAxis.VerticalOrder order;
   private final int[] planes;

   public DirectionAxis.VerticalOrder getOrder() {
      return this.order;
   }

   public Axis getDepth() {
      return this.depth;
   }

   public long getBandsSweeps() {
      return (long)this.planes.length * this.bands.length * this.sweeps.length;
   }

   public Axis getDepth2() {
      return this.depth;
   }

   public BlockPos getBlockPos(int blockPosValue, int depthAxis, int bandsValue, int j) {
      return this.getSweepBand(this.getPlanes(blockPosValue, depthAxis), this.getBandsMax(blockPosValue, bandsValue), this.getSweeps(blockPosValue, depthAxis, bandsValue, j));
   }

   public boolean canSweepAxis(double sweepAxisValue, double sweepAxisValue2) {
      double sweepAxisValue3 = this.sweep == Axis.X ? sweepAxisValue : sweepAxisValue2;
      double sweepAxisLimit = (this.axisHelper.getValue(this.sweep) + this.axisHelper.getValueForAxis(this.sweep) + 1) / 2.0;
      return sweepAxisValue3 <= sweepAxisLimit;
   }

   public boolean canFlag(int directionAxisValue, int depthAxis, int bandsValue) {
      return (this.getPlanesMax(directionAxisValue, depthAxis, bandsValue) & 1L) == 0L == this.directionAxisActive;
   }

   public int getBandsSweepsValue() {
      return this.bandsSweepsValue;
   }

   public int getMax() {
      return this.maxStepCount;
   }

   public Axis getBand() {
      return this.bandAxis;
   }

   public int getDepthAxis(int depthAxisValue, int depthAxisLimit) {
      int depthAxisValue2 = this.axisHelper.getValue(this.depth);
      int valueForAxis = this.axisHelper.getValueForAxis(this.depth);
      if (depthAxisLimit >= depthAxisValue2 && depthAxisLimit <= valueForAxis) {
         int depthAxisValue3 = this.advance.getDirection() == AxisDirection.POSITIVE ? depthAxisLimit - depthAxisValue2 : valueForAxis - depthAxisLimit;
         return this.depthAxisActive && (depthAxisValue & 1) == 1 ? this.planes.length - 1 - depthAxisValue3 : depthAxisValue3;
      } else {
         return -1;
      }
   }

   public BlockPos getAdvanceDown(int advanceDownValue, int depthAxis, int bandsValue, int j) {
      int sweepBandValue = this.getPlanes(advanceDownValue, depthAxis) - this.getDirectionAdvance(advanceDownValue) * this.maxStepCount;
      int sweeps = this.getSweeps(advanceDownValue, depthAxis, bandsValue, j);
      if (this.bandAxis == Axis.Y) {
         return this.getSweepBand(sweepBandValue, this.getMaxForInt(advanceDownValue), sweeps);
      } else {
         int advanceDownValue6 = this.getBandsMax(advanceDownValue, bandsValue) + (this.advance == Direction.DOWN ? this.maxStepCount : 0);
         int advanceDownValue7 = this.advance == Direction.DOWN ? this.getPlanes(advanceDownValue, depthAxis) + 1 : this.getPlanes(advanceDownValue, depthAxis) - 2;
         return this.getSweepBand(advanceDownValue7, advanceDownValue6, sweeps);
      }
   }

   private int getPlanesFlag(int planesValue, int depthAxis) {
      return this.depthAxisActive && (planesValue & 1) == 1 ? this.planes.length - 1 - depthAxis : depthAxis;
   }

   public DirectionAxis(
      DirectionOffsetUtilsUseful.Bounds bounds2,
      Direction direction,
      DirectionAxis.VerticalOrder verticalOrder,
      int directionAxisValue,
      double directionAxisValue2,
      int rocketsBuyBound,
      boolean directionAxisFlag
   ) {
      this(bounds2, direction, verticalOrder, directionAxisValue, directionAxisValue2, rocketsBuyBound, directionAxisFlag, true);
   }

   public DirectionAxis(
      DirectionOffsetUtilsUseful.Bounds axisHelper,
      Direction direction,
      DirectionAxis.VerticalOrder verticalOrder,
      int directionAxisBound,
      double directionAxisValue,
      int rocketsBuyBound,
      boolean directionAxisFlag,
      boolean flag
   ) {
      this.axisHelper = axisHelper;
      this.advance = direction;
      this.order = verticalOrder;
      this.directionAxisActive = flag;
      this.depth = direction.getAxis();
      this.depthAxisActive = directionAxisFlag && this.depth == Axis.Y;
      if (this.depth == Axis.Y) {
         this.bandAxis = Axis.X;
         this.sweep = Axis.Z;
      } else {
         this.bandAxis = Axis.Y;
         this.sweep = this.depth == Axis.X ? Axis.Z : Axis.X;
      }

      int directionAxisValue2 = axisHelper.getValue(this.depth);
      int valueForAxis = axisHelper.getValueForAxis(this.depth);
      boolean directionAxisFlag2 = direction.getDirection() == AxisDirection.POSITIVE;
      int directionAxisSize = Math.max(0, valueForAxis - directionAxisValue2 + 1);
      this.planes = new int[directionAxisSize];

      for (int i = 0; i < directionAxisSize; i++) {
         this.planes[i] = directionAxisFlag2 ? directionAxisValue2 + i : valueForAxis - i;
      }

      int[] directionAxisValues = DirectionOffsetUtils2.getValues(axisHelper.getValue(this.bandAxis), axisHelper.getValueForAxis(this.bandAxis));
      boolean directionAxisFlag3 = directionAxisValues.length >= 2 && directionAxisValues[directionAxisValues.length - 1] + 1 > axisHelper.getValueForAxis(this.bandAxis);
      boolean directionAxisFlag4 = this.bandAxis != Axis.Y || verticalOrder == DirectionAxis.VerticalOrder.BOTTOM_UP || directionAxisFlag3;
      this.bands = new int[directionAxisValues.length];

      for (int j = 0; j < directionAxisValues.length; j++) {
         this.bands[j] = directionAxisFlag4 ? directionAxisValues[j] : directionAxisValues[directionAxisValues.length - 1 - j];
      }

      this.sweeps = DirectionOffsetUtils2.getValues(axisHelper.getValue(this.sweep), axisHelper.getValueForAxis(this.sweep));
      int intersectsCenterBound = this.bandAxis != Axis.Y
         ? Math.max(1, this.bands.length)
         : (directionAxisBound == 0 ? getValue(directionAxisValue) : (directionAxisBound == Integer.MAX_VALUE ? Math.max(1, this.bands.length) : Math.max(1, directionAxisBound)));
      this.bandsSweepsValue = Math.max(1, Math.min(intersectsCenterBound, Math.max(1, this.bands.length)));
      this.lifts = this.bands.length == 0 ? 0 : (this.bands.length + this.bandsSweepsValue - 1) / this.bandsSweepsValue;
      this.maxStepCount = this.bandAxis == Axis.Y ? Math.max(Math.max(1, rocketsBuyBound), getMax(this.bandsSweepsValue)) : Math.max(1, rocketsBuyBound);
   }

   public BlockPos getBlockPos(BlockPos pos, Direction direction, int blockPosValue) {
      return this.getOffsetDown(pos, direction, blockPosValue, false);
   }

   public static int getMax(int directionAxisBound) {
      double directionAxisValue = 3.0 * (Math.max(1, directionAxisBound) - 1) - 0.12;
      return Math.max(2, (int)Math.ceil(directionAxisValue) + 1);
   }

   private long getPlanesMax(int planesValue, int depthAxis, int bandsValue) {
      return ((long)planesValue * this.planes.length + depthAxis) * this.bandsSweepsValue + bandsValue;
   }

   public int getSweeps(int sweepsValue, int depthAxis, int bandsValue, int j) {
      return this.sweeps[this.getSweepsForInt(sweepsValue, depthAxis, bandsValue, j)];
   }

   public int getPlanes(int planesValue, int depthAxis) {
      return this.planes[this.getPlanesFlag(planesValue, depthAxis)];
   }

   public int getMaxForInt(int directionAxisValue) {
      int directionAxisBound = Integer.MAX_VALUE;

      for (int i = 0; i < this.getMinForInt(directionAxisValue); i++) {
         directionAxisBound = Math.min(directionAxisBound, this.getBandsMax(directionAxisValue, i) - 1);
      }

      return Math.max(this.axisHelper.getValue(this.bandAxis), directionAxisBound);
   }

   public static int getValue(double directionAxisValue) {
      int directionAxisValue2 = 1;

      for (int i = 2; i <= 2; i++) {
         int max3 = getMax(i);
         double deltaX = max3 - 0.5;
         double deltaZ = Math.max(0.0, 3.0 * (i - 1) - 0.62);
         if (Math.hypot(deltaX, deltaZ) <= directionAxisValue - 0.3) {
            directionAxisValue2 = i;
         }
      }

      return directionAxisValue2;
   }

   public Direction getAdvance() {
      return this.advance;
   }

   private BlockPos getSweepBand(int sweepBandValue, int sweepBandValue2, int sweepBandValue3) {
      int x = 0;
      int y = 0;
      int z = 0;
      switch (this.depth) {
         case X:
            x = sweepBandValue;
            break;
         case Y:
            y = sweepBandValue;
            break;
         case Z:
            z = sweepBandValue;
      }

      switch (this.bandAxis) {
         case X:
            x = sweepBandValue2;
            break;
         case Y:
            y = sweepBandValue2;
            break;
         case Z:
            z = sweepBandValue2;
      }

      switch (this.sweep) {
         case X:
            x = sweepBandValue3;
            break;
         case Y:
            y = sweepBandValue3;
            break;
         case Z:
            z = sweepBandValue3;
      }

      return new BlockPos(x, y, z);
   }

   public Axis getSweep() {
      return this.sweep;
   }

   public Direction getDirection(int directionValue) {
      return Direction.from(this.depth, this.getDirectionAdvance(directionValue) > 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE);
   }

   public long getBandsSweeps(int bandsSweepsValue, int bandsSweepsValue2, int bandsSweepsValue3) {
      int depthAxisLimit = getValue(bandsSweepsValue, bandsSweepsValue2, bandsSweepsValue3, this.depth);
      int bandsSweepsValue5 = getValue(bandsSweepsValue, bandsSweepsValue2, bandsSweepsValue3, this.bandAxis);
      int bandsSweepsValue6 = getValue(bandsSweepsValue, bandsSweepsValue2, bandsSweepsValue3, this.sweep);
      long bandsSweeps = Long.MAX_VALUE;

      for (int i = 0; i < this.bands.length; i++) {
         if (Math.abs(this.bands[i] - bandsSweepsValue5) <= 1) {
            int depthAxisValue = i / this.bandsSweepsValue;
            int bandsValue = i % this.bandsSweepsValue;
            int depthAxis = this.getDepthAxis(depthAxisValue, depthAxisLimit);
            if (depthAxis >= 0) {
               for (int j = 0; j < this.sweeps.length; j++) {
                  if (Math.abs(this.sweeps[j] - bandsSweepsValue6) <= 1) {
                     int sweeps = this.getSweepsForInt(depthAxisValue, depthAxis, bandsValue, j);
                     bandsSweeps = Math.min(bandsSweeps, this.getSweepsLong(depthAxisValue, depthAxis, bandsValue, sweeps));
                  }
               }
            }
         }
      }

      return bandsSweeps;
   }

   public int getBands() {
      return this.bands.length;
   }

   private int getSweepsForInt(int sweepsValue, int depthAxis, int bandsValue, int j) {
      return this.canFlag(sweepsValue, depthAxis, bandsValue) ? j : this.sweeps.length - 1 - j;
   }

   public BlockPos getOffsetDown(BlockPos pos, Direction direction, int offsetDownValue, boolean offsetDownFlag) {
      if (direction.getAxis() == Axis.Y) {
         Direction direction2 = this.getDirection(offsetDownValue);
         if (direction2.getAxis() == Axis.Y) {
            direction2 = Direction.SOUTH;
         }

         Direction rotateYClockwise = direction2.rotateYClockwise();
         if (offsetDownFlag) {
            return direction == Direction.DOWN ? pos.down(2) : pos.offset(direction2, 1).offset(rotateYClockwise, 1);
         } else {
            return direction == Direction.DOWN ? pos.offset(direction2, 1).down(3) : pos.offset(direction2, 2).up(1);
         }
      } else {
         int y = this.depth != Axis.Y ? pos.getY() - 1 : (this.advance == Direction.DOWN ? pos.getY() : pos.getY() - 2);
         BlockPos offset = pos.offset(direction, offsetDownFlag ? 1 : this.maxStepCount);
         return new BlockPos(offset.getX(), y, offset.getZ());
      }
   }

   public long getSweepsLong(int sweepsValue, int depthAxis, int bandsValue, int sweepsValue4) {
      return this.getPlanesMax(sweepsValue, depthAxis, bandsValue) * this.sweeps.length + sweepsValue4;
   }

   public static Direction getNegativePositive(DirectionOffsetUtilsUseful.Bounds bounds, double negativePositiveValue, double negativePositiveLimit, double negativePositiveValue2) {
      int negativePositiveValue3 = bounds.maxY - bounds.negativePositiveValue + 1;
      if (negativePositiveValue3 < 3) {
         return negativePositiveLimit >= bounds.maxY + 1 ? Direction.DOWN : Direction.UP;
      } else {
         int negativePositiveValue4 = bounds.directionAxisValue - bounds.minX + 1;
         int negativePositiveLimit2 = bounds.boundsValue - bounds.minZ + 1;
         Axis axis = negativePositiveValue4 <= negativePositiveLimit2 ? Axis.X : Axis.Z;
         double negativePositiveValue5 = axis == Axis.X ? negativePositiveValue : negativePositiveValue2;
         double negativePositiveLimit3 = (bounds.getValue(axis) + bounds.getValueForAxis(axis) + 1) / 2.0;
         return Direction.from(axis, negativePositiveValue5 <= negativePositiveLimit3 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE);
      }
   }

   public int getDirectionAdvance(int directionAdvanceValue) {
      int directionAdvanceValue2 = this.advance.getDirection() == AxisDirection.POSITIVE ? 1 : -1;
      return this.depthAxisActive && (directionAdvanceValue & 1) == 1 ? -directionAdvanceValue2 : directionAdvanceValue2;
   }

   @Override
   public String toString() {
      return "WallSlicePlan[advance="
         + this.advance
         + " depth="
         + this.depth
         + " band="
         + this.bandAxis
         + " sweep="
         + this.sweep
         + " planes="
         + this.planes.length
         + " bands="
         + this.bands.length
         + "/lift "
         + this.bandsSweepsValue
         + " lifts="
         + this.lifts
         + " sweeps="
         + this.sweeps.length
         + " order="
         + this.order
         + "]";
   }

   public int getMin(int directionAxisValue) {
      int directionAxisBound = Integer.MIN_VALUE;

      for (int i = 0; i < this.getMinForInt(directionAxisValue); i++) {
         directionAxisBound = Math.max(directionAxisBound, this.getBandsMax(directionAxisValue, i) + 1);
      }

      return Math.min(this.axisHelper.getValueForAxis(this.bandAxis), directionAxisBound);
   }

   public int getSweeps() {
      return this.sweeps.length;
   }

   public DirectionOffsetUtilsUseful.Bounds getAxisHelper() {
      return this.axisHelper;
   }

   public int getPlanes() {
      return this.planes.length;
   }

   public int getLifts() {
      return this.lifts;
   }

   public int getBandsMax(int bandsFactor, int bandsValue) {
      return this.bands[bandsFactor * this.bandsSweepsValue + bandsValue];
   }

   static int getValue(int directionAxisValue, int directionAxisValue2, int directionAxisValue3, Axis axis) {
      return switch (axis) {
         case X -> directionAxisValue;
         case Y -> directionAxisValue2;
         case Z -> directionAxisValue3;
      };
   }

   public int getMinForInt(int directionAxisFactor) {
      return Math.min(this.bandsSweepsValue, this.bands.length - directionAxisFactor * this.bandsSweepsValue);
   }

   public static enum VerticalOrder {
      TOP_DOWN,
      BOTTOM_UP;
   }
}
