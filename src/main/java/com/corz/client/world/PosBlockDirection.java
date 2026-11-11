package com.corz.client.world;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;

public final class PosBlockDirection {
   private int wallSliceUpdateCount;
   private PosBlockDirection.State advanceSelectType;
   private final Map<Long, Integer> wallSliceIntegerMap;
   private int sliceWallValue;
   private int stateDirectionValue;
   private final Map<Long, Set<String>> wallSliceSetMap;
   private int directionBandCount;
   private final Set<Long> possAdvancePositionSet;
   private int compare;
   private DirectionOffsetUtilsUseful.CellWorld residueSegCellWorld;
   private int residueSegValue;
   private int wallSliceValue;
   private PosBlockDirection.Target residueSeg;
   private final Set<Long> directionAxisPositionSet;
   private final ArrayDeque<PosBlockDirection.Pending> arrayDeque;
   private final DirectionAxis directionAxis;
   private final PosBlockDirection.Stats directionBandStats = new PosBlockDirection.Stats();
   private boolean directionBandActive;
   private int advanceDirectionCount;
   private int directionSliceCount;
   private final Map<Long, Integer> possAdvanceIntegerMap;
   private PosBlockDirection.Target directionSliceTarget;
   private boolean sliceWallFlag;
   private int directionBandValue;
   private final Set<Long> advanceDirectionPositionSet;

   private BlockPos getDirectionAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionAxisValue, int directionAxisLimit) {
      int planes = this.directionAxis.getPlanes(this.directionSliceCount, this.sliceWallValue);
      int directionAxisValue2 = this.directionAxis.getAxisHelper().getValue(this.directionAxis.getSweep());
      int valueForAxis = this.directionAxis.getAxisHelper().getValueForAxis(this.directionAxis.getSweep());

      for (int i = directionAxisValue; i <= directionAxisLimit; i++) {
         for (int j = directionAxisValue2; j <= valueForAxis; j++) {
            BlockPos directionAxis = this.getDirectionAxisForInt(planes, i, j);
            if (!cellWorld.canFlag4(directionAxis.getX(), directionAxis.getY(), directionAxis.getZ())) {
               return directionAxis;
            }
         }
      }

      return null;
   }

   public void handlePossAdvance(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos, Direction direction, OutsideUseful outsideUseful, int possAdvanceValue, int possAdvanceBound) {
      long[][] longs = new long[1][];
      boolean[][] booleans = new boolean[1][];
      this.handleAxis(cellWorld, pos, direction, longs, booleans);
      this.arrayDeque.addLast(new PosBlockDirection.Pending(pos, direction, outsideUseful.useful, possAdvanceValue + Math.max(1, possAdvanceBound), outsideUseful.blockPoss, longs[0], booleans[0]));
      this.possAdvancePositionSet.add(pos.asLong());

      for (BlockPos blockPos : outsideUseful.blockPoss) {
         this.possAdvanceIntegerMap.put(blockPos.asLong(), possAdvanceValue);
      }

      this.possAdvanceIntegerMap.entrySet().removeIf(nowx -> possAdvanceValue - (Integer)nowx.getValue() > 200);
      this.directionBandStats.possAdvanceCount++;
      this.advanceSelectType = PosBlockDirection.State.DRILL;
   }

   private static String formatText(BlockPos pos, Direction direction, BlockPos pos2, PosBlockDirection.Failure failure) {
      return pos.asLong()
         + "/"
         + (direction == null ? "?" : direction.getId())
         + "/"
         + (pos2 == null ? "?" : Long.toString(pos2.asLong()))
         + (failure == null ? "" : "/" + failure);
   }

   public PosBlockDirection.Target getResidueSeg(DirectionOffsetUtilsUseful.CellWorld cellWorld, int residueSegValue) {
      return this.getResidueSeg(cellWorld, cellWorld, residueSegValue);
   }

   private boolean canDirectionAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionAxisValue, int directionAxisLimit) {
      int planes = this.directionAxis.getPlanes(this.directionSliceCount, this.sliceWallValue);
      int directionAxisValue2 = this.directionAxis.getAxisHelper().getValue(this.directionAxis.getSweep());
      int valueForAxis = this.directionAxis.getAxisHelper().getValueForAxis(this.directionAxis.getSweep());

      for (int i = directionAxisValue; i <= directionAxisLimit; i++) {
         for (int j = directionAxisValue2; j <= valueForAxis; j++) {
            BlockPos directionAxis = this.getDirectionAxisForInt(planes, i, j);
            if (!this.directionAxisPositionSet.contains(directionAxis.asLong()) && cellWorld.canFlag(directionAxis.getX(), directionAxis.getY(), directionAxis.getZ())) {
               return true;
            }
         }
      }

      return false;
   }

   private void handleAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos, Direction direction, long[][] longs, boolean[][] flags) {
      Axis axis = direction.getAxis();
      Axis[] axiss = AxisPosUtils.getAxiss(axis);
      long[] longs2 = new long[34];
      boolean[] booleans = new boolean[longs2.length];
      int index = 0;

      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            if (Math.abs(i) > 1 || Math.abs(j) > 1) {
               BlockPos blockPos = getBlockPos(pos, axiss[0], i, axiss[1], j, axis, 0);
               longs2[index] = blockPos.asLong();
               booleans[index++] = cellWorld.canFlag3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            }
         }
      }

      for (int k = -1; k <= 1; k += 2) {
         for (int m = -1; m <= 1; m++) {
            for (int p = -1; p <= 1; p++) {
               BlockPos blockPos2 = getBlockPos(pos, axiss[0], m, axiss[1], p, axis, k);
               longs2[index] = blockPos2.asLong();
               booleans[index++] = cellWorld.canFlag3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
         }
      }

      longs[0] = longs2;
      flags[0] = booleans;
   }

   private int getSliceWall() {
      return this.directionAxis.getMin(this.directionSliceCount);
   }

   public String getAppend(PosBlockDirection.Target target) {
      return !this.sliceWallFlag && this.directionSliceCount < this.directionAxis.getLifts() && this.directionAxis.getBands() != 0
         ? "[WallSlice] plane="
            + this.sliceWallValue
            + "/"
            + this.directionAxis.getPlanes()
            + " depth="
            + this.directionAxis.getPlanes(this.directionSliceCount, this.sliceWallValue)
            + " lift="
            + this.directionSliceCount
            + "/"
            + this.directionAxis.getLifts()
            + " verticalOrder="
            + this.directionAxis.getOrder()
            + " bandY="
            + this.directionAxis.getBandsMax(this.directionSliceCount, this.directionBandValue)
            + " direction="
            + this.directionAxis.getAdvance()
            + " state="
            + this.advanceSelectType
            + " seg="
            + this.stateDirectionValue
            + "/"
            + this.directionAxis.getSweeps()
            + (target == null ? "" : " " + target)
         : "[WallSlice] DONE planes="
            + this.directionAxis.getPlanes()
            + " levels="
            + this.directionAxis.getLifts()
            + " state="
            + this.advanceSelectType
            + " abandoned="
            + this.directionBandStats.sliceWallValue
            + (target == null ? "" : " " + target);
   }

   public long getMax() {
      return Math.max(0L, this.directionAxis.getSweeps() - 1L - this.stateDirectionValue);
   }

   public PosBlockDirection(DirectionAxis directionAxis) {
      this.advanceSelectType = PosBlockDirection.State.SELECT_WALL_PLANE;
      this.wallSliceValue = Integer.MIN_VALUE;
      this.wallSliceIntegerMap = new HashMap<>();
      this.advanceDirectionPositionSet = new HashSet<>();
      this.directionAxisPositionSet = new HashSet<>();
      this.wallSliceSetMap = new HashMap<>();
      this.arrayDeque = new ArrayDeque<>();
      this.possAdvancePositionSet = new HashSet<>();
      this.possAdvanceIntegerMap = new HashMap<>();
      this.residueSegValue = Integer.MIN_VALUE;
      this.directionAxis = directionAxis;
      this.sliceWallFlag = directionAxis.getBandsSweeps() == 0L;
      if (this.sliceWallFlag) {
         this.advanceSelectType = PosBlockDirection.State.DONE;
      }
   }

   private int getMaxDirectionAxis() {
      return Math.max(
         this.directionAxis.getAxisHelper().getValue(this.directionAxis.getBand()),
         this.directionAxis.getBandsMax(this.directionSliceCount, this.directionBandValue) - 1
      );
   }

   static String formatText(BlockPos pos) {
      return pos == null ? "-" : pos.getX() + "," + pos.getY() + "," + pos.getZ();
   }

   private static Direction getAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos, Direction axis) {
      if (axis.getAxis() != Axis.Y && DirectionOffsetUtilsUseful.canOffset(cellWorld, pos.getX(), pos.getY(), pos.getZ(), axis)) {
         return axis;
      } else {
         for (Direction axis2 : Direction.values()) {
            if (axis2.getAxis() != Axis.Y && DirectionOffsetUtilsUseful.canOffset(cellWorld, pos.getX(), pos.getY(), pos.getZ(), axis2)) {
               return axis2;
            }
         }

         for (Direction direction : Direction.values()) {
            if (DirectionOffsetUtilsUseful.canOffset(cellWorld, pos.getX(), pos.getY(), pos.getZ(), direction)) {
               return direction;
            }
         }

         return axis;
      }
   }

   public void updateDequeArray() {
      this.arrayDeque.clear();
      this.possAdvancePositionSet.clear();
   }

   public boolean canSliceWall(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      if (this.sliceWallFlag && this.wallSliceUpdateCount < 2) {
         this.directionAxisPositionSet.clear();
         if (!this.canDirectionAxisForCellWorld(cellWorld)) {
            return false;
         } else {
            this.sliceWallFlag = false;
            this.updateWallSlice();
            return true;
         }
      } else {
         return false;
      }
   }

   public void handleAxis(BlockPos pos, Direction direction) {
      if (pos != null && direction != null) {
         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               Axis[] axiss = AxisPosUtils.getAxiss(direction.getAxis());
               this.handle(getBlockPos(pos, axiss[0], i, axiss[1], j, direction.getAxis(), 0));
            }
         }
      }
   }

   public String formatCentreExpected(DirectionOffsetUtilsUseful.CellWorld cellWorld, int centreExpectedLimit) {
      StringBuilder stringBuilder = null;

      while (!this.arrayDeque.isEmpty() && this.arrayDeque.peekFirst().expireAtLimit <= centreExpectedLimit) {
         PosBlockDirection.Pending pollFirst = this.arrayDeque.pollFirst();
         this.possAdvancePositionSet.remove(pollFirst.blockPos.asLong());
         int centreExpectedCounter = 0;

         for (BlockPos blockPos : pollFirst.blockPoss) {
            if (cellWorld.canFlag(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
               centreExpectedCounter++;
            }
         }

         int centreExpectedValue = Math.max(0, pollFirst.removedExpectedValue - centreExpectedCounter);
         this.directionBandStats.centreExpectedCount += centreExpectedValue;
         this.directionBandStats.centreExpectedTotal += centreExpectedCounter;
         int centreExpectedCounter2 = 0;
         int centreExpectedCounter3 = 0;

         for (int i = 0; i < pollFirst.timesCentreExpected.length; i++) {
            if (pollFirst.centreExpectedFlags[i]) {
               BlockPos long2 = BlockPos.fromLong(pollFirst.timesCentreExpected[i]);
               if (!cellWorld.canFlag3(long2.getX(), long2.getY(), long2.getZ())) {
                  Integer integer = this.possAdvanceIntegerMap.get(pollFirst.timesCentreExpected[i]);
                  if (integer == null) {
                     if (cellWorld.canFlagForInt(long2.getX(), long2.getY(), long2.getZ())) {
                        centreExpectedCounter3++;
                     } else if (!canPossDown(pollFirst, long2)) {
                        centreExpectedCounter2++;
                     }
                  }
               }
            }
         }

         this.directionBandStats.centreExpectedMisses += centreExpectedCounter3;
         this.directionBandStats.centreValue += centreExpectedCounter2;
         if (centreExpectedCounter > 0 || centreExpectedCounter3 > 0 || centreExpectedCounter2 > 0) {
            if (stringBuilder == null) {
               stringBuilder = new StringBuilder();
            }

            stringBuilder.append(stringBuilder.length() == 0 ? "" : " | ")
               .append("centre=")
               .append(formatText(pollFirst.blockPos))
               .append(" face=")
               .append(pollFirst.direction)
               .append(" expected=")
               .append(pollFirst.removedExpectedValue)
               .append(" removed=")
               .append(centreExpectedValue)
               .append(" stillPresent=")
               .append(centreExpectedCounter)
               .append(" beyondFootprint=")
               .append(centreExpectedCounter3)
               .append(" outsideArea=")
               .append(centreExpectedCounter2);
         }

         if (centreExpectedCounter > 0) {
            this.handleForBlockPos(pollFirst.blockPos);
         }
      }

      if (this.advanceSelectType == PosBlockDirection.State.DRILL && this.arrayDeque.isEmpty()) {
         this.advanceSelectType = PosBlockDirection.State.ADVANCE_HORIZONTAL;
      }

      return stringBuilder == null ? null : stringBuilder.toString();
   }

   public PosBlockDirection.Stats getDirectionBandStats() {
      return this.directionBandStats;
   }

   private long getDirectionAxis() {
      return this.sliceWallFlag ? Long.MAX_VALUE : this.getSliceWallSliceWallFlag() + (long)this.directionAxis.getBandsSweepsValue() * this.directionAxis.getSweeps();
   }

   private PosBlockDirection.Target getDirectionSlice(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionSliceValue) {
      if (this.sliceWallFlag) {
         this.advanceSelectType = PosBlockDirection.State.DONE;
         return null;
      } else {
         this.directionSliceTarget = null;

         for (int i = 0; i < 8192; i++) {
            BlockPos blockPos = this.directionAxis.getBlockPos(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue);
            long long2 = blockPos.asLong();
            if (!this.getApi(cellWorld).canFlag4(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
               this.advanceSelectType = PosBlockDirection.State.MOVE_TO_WORK_LANE;
               return new PosBlockDirection.Target(
                  this.directionSliceCount,
                  this.sliceWallValue,
                  this.directionBandValue,
                  this.stateDirectionValue,
                  blockPos,
                  blockPos,
                  this.directionAxis.getAdvanceDown(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue),
                  this.directionAxis.getDirection(this.directionSliceCount),
                  this.directionAxis.getSweepsLong(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue),
                  true,
                  this.directionBandActive
               );
            }

            if (!this.directionAxisPositionSet.contains(long2)
               && !this.advanceDirectionPositionSet.contains(long2)
               && DirectionOffsetUtilsUseful.canFlag(
                  cellWorld, this.directionAxis.getAxisHelper(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.directionAxis.getDepth2()
               )) {
               Direction direction = this.directionAxis.getDirection(this.directionSliceCount);
               BlockPos advanceDown = this.directionAxis.getAdvanceDown(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue);
               long sweeps = this.directionAxis.getSweepsLong(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue);
               DirectionOffsetUtilsUseful.Candidate offsetPositive = DirectionOffsetUtilsUseful.getOffsetPositive(
                  cellWorld, this.directionAxis.getAxisHelper(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.directionAxis.getDepth2(), this.getDirection()
               );
               if (offsetPositive != null) {
                  this.advanceSelectType = PosBlockDirection.State.ALIGN_DRILL;
                  Direction direction2 = offsetPositive.rotatedScoreFlag ? offsetPositive.opposite : direction;
                  BlockPos blockPos2 = offsetPositive.rotatedScoreFlag ? this.directionAxis.getBlockPos(offsetPositive.getUsefulSpill(), offsetPositive.opposite, this.directionSliceCount) : advanceDown;
                  return new PosBlockDirection.Target(
                     this.directionSliceCount,
                     this.sliceWallValue,
                     this.directionBandValue,
                     this.stateDirectionValue,
                     blockPos,
                     offsetPositive.getUsefulSpill(),
                     blockPos2,
                     direction2,
                     sweeps,
                     false,
                     this.directionBandActive
                  );
               }

               BlockPos positiveDirection = this.getPositiveDirection(cellWorld, blockPos);
               if (positiveDirection != null) {
                  this.advanceSelectType = PosBlockDirection.State.ALIGN_DRILL;
                  Direction axis = getAxis(cellWorld, positiveDirection, direction);
                  return new PosBlockDirection.Target(
                     this.directionSliceCount,
                     this.sliceWallValue,
                     this.directionBandValue,
                     this.stateDirectionValue,
                     blockPos,
                     positiveDirection,
                     this.directionAxis.getOffsetDown(positiveDirection, axis, this.directionSliceCount, true),
                     axis,
                     sweeps,
                     false,
                     this.directionBandActive,
                     true
                  );
               }

               BlockPos positiveDirection2 = this.getPositiveDirectionForCellWorldBlockPos(cellWorld, blockPos);
               if (positiveDirection2 != null) {
                  this.advanceSelectType = PosBlockDirection.State.MOVE_TO_WORK_LANE;
                  return new PosBlockDirection.Target(
                     this.directionSliceCount,
                     this.sliceWallValue,
                     this.directionBandValue,
                     this.stateDirectionValue,
                     blockPos,
                     positiveDirection2,
                     advanceDown,
                     direction,
                     sweeps,
                     true,
                     this.directionBandActive
                  );
               }
            }

            if (!this.canAdvanceDirection(cellWorld)) {
               return this.directionSliceTarget;
            }
         }

         return null;
      }
   }

   private Direction getDirection() {
      return Direction.from(
         this.directionAxis.getDepth(), this.directionAxis.getDirectionAdvance(this.directionSliceCount) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE
      );
   }

   public boolean isDequeArray() {
      return !this.arrayDeque.isEmpty();
   }

   private boolean canDirectionSlice(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionSliceValue, int directionAxisLimit) {
      BlockPos directionAxis = this.getDirectionAxis(this.getApi(cellWorld), directionSliceValue, directionAxisLimit);
      if (directionAxis == null) {
         return false;
      } else {
         this.advanceSelectType = PosBlockDirection.State.MOVE_TO_WORK_LANE;
         this.directionSliceTarget = new PosBlockDirection.Target(
            this.directionSliceCount,
            this.sliceWallValue,
            this.directionBandValue,
            this.stateDirectionValue,
            directionAxis,
            directionAxis,
            this.directionAxis.getAdvanceDown(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue),
            this.directionAxis.getDirection(this.directionSliceCount),
            this.directionAxis.getSweepsLong(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue),
            true,
            this.directionBandActive
         );
         return true;
      }
   }

   private void handleDirectionAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionAxisValue, int directionAxisLimit) {
      int planes = this.directionAxis.getPlanes(this.directionSliceCount, this.sliceWallValue);
      int directionAxisValue2 = this.directionAxis.getAxisHelper().getValue(this.directionAxis.getSweep());
      int valueForAxis = this.directionAxis.getAxisHelper().getValueForAxis(this.directionAxis.getSweep());
      Direction from = Direction.from(this.directionAxis.getDepth2(), AxisDirection.POSITIVE);

      for (int i = directionAxisValue; i <= directionAxisLimit; i++) {
         for (int j = directionAxisValue2; j <= valueForAxis; j++) {
            BlockPos directionAxis = this.getDirectionAxisForInt(planes, i, j);
            if (cellWorld.canFlag(directionAxis.getX(), directionAxis.getY(), directionAxis.getZ()) && this.canFlag(directionAxis, from)) {
               this.directionAxisPositionSet.add(directionAxis.asLong());
            }
         }
      }

      this.directionBandStats.sliceWallValue = this.directionAxisPositionSet.size();
   }

   public Axis getDirectionAxis2() {
      return this.directionAxis.getDepth2();
   }

   public void handle(int posBlockValue, int j) {
      this.directionBandStats.statsValue = this.directionBandStats.statsValue + Math.abs(posBlockValue);
      this.directionBandStats.directionBandValue = this.directionBandStats.directionBandValue + Math.abs(j);
   }

   public void update() {
      this.directionBandStats.updateCallCount++;
   }

   public DirectionAxis getDirectionAxis3() {
      return this.directionAxis;
   }

   public boolean canDirectionAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      DirectionOffsetUtilsUseful.Bounds axisHelper = this.directionAxis.getAxisHelper();

      for (int i = axisHelper.minX; i <= axisHelper.directionAxisValue; i++) {
         for (int j = axisHelper.negativePositiveValue; j <= axisHelper.maxY; j++) {
            for (int k = axisHelper.minZ; k <= axisHelper.boundsValue; k++) {
               if (cellWorld.canFlag4(i, j, k) && cellWorld.canFlag(i, j, k)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      return this.canDirectionAxis(this.getApi(cellWorld), this.getSliceWallDirectionAxis(), this.getSliceWall());
   }

   private BlockPos getPositiveDirection(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos) {
      BlockPos[] blockPos = new BlockPos[]{null};
      AxisPosUtils.handleAxis(
         pos,
         Direction.from(this.directionAxis.getDepth2(), AxisDirection.POSITIVE),
         (x2, y2, z2) -> {
            if (blockPos[0] == null && DirectionOffsetUtilsUseful.canOffset(cellWorld, x2, y2, z2)) {
               if (!DirectionOffsetUtilsUseful.canFlag(
                  cellWorld, this.directionAxis.getAxisHelper(), this.directionAxis.getDepth2(), x2, y2, z2, pos.getX(), pos.getY(), pos.getZ()
               )) {
                  if (!DirectionOffsetUtilsUseful.canSpillRank(
                     cellWorld,
                     this.directionAxis.getAxisHelper(),
                     x2,
                     y2,
                     z2,
                     this.directionAxis.getDepth2(),
                     this.directionAxis::getBandsSweeps,
                     this.getSliceWallSliceWallFlag(),
                     this.getDirectionAxis()
                  )) {
                     blockPos[0] = new BlockPos(x2, y2, z2);
                  }
               }
            }
         }
      );
      return blockPos[0];
   }

   public long getDirectionAxis(int directionAxisValue, int directionAxisValue2, int directionAxisValue3) {
      return this.directionAxis.getBandsSweeps(directionAxisValue, directionAxisValue2, directionAxisValue3);
   }

   public void handle(BlockPos pos) {
      if (pos != null) {
         long long2 = pos.asLong();
         this.wallSliceIntegerMap.remove(long2);
         this.advanceDirectionPositionSet.remove(long2);
         this.wallSliceSetMap.remove(long2);
      }
   }

   private void handleForBlockPos(BlockPos pos) {
      long long2 = pos.asLong();
      if (this.wallSliceIntegerMap.merge(long2, 1, Integer::sum) >= this.getValue()) {
         this.advanceDirectionPositionSet.add(long2);
         this.wallSliceIntegerMap.remove(long2);
      }
   }

   public boolean canFlagForCellWorld(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      return this.canDirectionAxis(this.getApi(cellWorld), this.getMaxDirectionAxis(), this.getMin());
   }

   private boolean canFlag(BlockPos pos, Direction direction) {
      if (this.advanceDirectionPositionSet.isEmpty()) {
         return false;
      } else {
         boolean[] booleans = new boolean[]{false};
         AxisPosUtils.handleAxis(pos, direction, (hit, posBlockValue, posBlockValue2) -> {
            if (!booleans[0] && this.advanceDirectionPositionSet.contains(BlockPos.asLong(hit, posBlockValue, posBlockValue2))) {
               booleans[0] = true;
            }
         });
         return booleans[0];
      }
   }

   private long getSliceWallSliceWallFlag() {
      return this.sliceWallFlag
         ? Long.MAX_VALUE
         : this.directionAxis.getSweepsLong(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue) + this.getMax();
   }

   private int getSliceWallDirectionAxis() {
      return this.directionAxis.getMaxForInt(this.directionSliceCount);
   }

   private boolean canWallSlice(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      this.advanceSelectType = PosBlockDirection.State.ADVANCE_DEPTH;
      if (++this.sliceWallValue < this.directionAxis.getPlanes()) {
         this.directionBandStats.statsCount++;
         return true;
      } else {
         this.sliceWallValue = 0;
         this.directionBandStats.wallSliceChecks++;
         if (++this.directionSliceCount < this.directionAxis.getLifts()) {
            this.advanceSelectType = PosBlockDirection.State.SELECT_WALL_PLANE;
            this.wallSliceValue = Integer.MIN_VALUE;
            return true;
         } else if (this.wallSliceUpdateCount < 2 && !this.arrayDeque.isEmpty()) {
            this.directionSliceCount = this.directionAxis.getLifts() - 1;
            this.sliceWallValue = this.directionAxis.getPlanes() - 1;
            this.directionBandValue = this.directionAxis.getMinForInt(this.directionSliceCount) - 1;
            this.stateDirectionValue = this.directionAxis.getSweeps() - 1;
            this.advanceSelectType = PosBlockDirection.State.WAIT_WORLD_CONFIRMATION;
            return false;
         } else {
            if (this.wallSliceUpdateCount < 2) {
               this.directionAxisPositionSet.clear();
               if (this.canDirectionAxisForCellWorld(cellWorld)) {
                  this.updateWallSlice();
                  return true;
               }
            }

            this.sliceWallFlag = true;
            this.advanceSelectType = PosBlockDirection.State.DONE;
            return false;
         }
      }
   }

   private int getValue() {
      return this.wallSliceUpdateCount > 0 ? 1 : 3;
   }

   private BlockPos getDirectionAxisForInt(int directionAxisValue, int i, int j) {
      int x = 0;
      int y = 0;
      int z = 0;
      switch (this.directionAxis.getDepth()) {
         case X:
            x = directionAxisValue;
            break;
         case Y:
            y = directionAxisValue;
            break;
         case Z:
            z = directionAxisValue;
      }

      switch (this.directionAxis.getBand()) {
         case X:
            x = i;
            break;
         case Y:
            y = i;
            break;
         case Z:
            z = i;
      }

      switch (this.directionAxis.getSweep()) {
         case X:
            x = j;
            break;
         case Y:
            y = j;
            break;
         case Z:
            z = j;
      }

      return new BlockPos(x, y, z);
   }

   private boolean canDirectionAxisForCellWorld(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      DirectionOffsetUtilsUseful.Bounds axisHelper = this.directionAxis.getAxisHelper();

      for (int i = axisHelper.minX; i <= axisHelper.directionAxisValue; i++) {
         for (int j = axisHelper.negativePositiveValue; j <= axisHelper.maxY; j++) {
            for (int k = axisHelper.minZ; k <= axisHelper.boundsValue; k++) {
               if (cellWorld.canFlag4(i, j, k) && !this.directionAxisPositionSet.contains(BlockPos.asLong(i, j, k)) && cellWorld.canFlag(i, j, k)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public PosBlockDirection.State getAdvanceSelectType() {
      return this.advanceSelectType;
   }

   private int getMin() {
      return Math.min(
         this.directionAxis.getAxisHelper().getValueForAxis(this.directionAxis.getBand()),
         this.directionAxis.getBandsMax(this.directionSliceCount, this.directionBandValue) + 1
      );
   }

   private void updateDirectionBand() {
      int value = this.directionAxis.getBandsMax(this.directionSliceCount, this.directionBandValue);
      if (this.wallSliceValue != Integer.MIN_VALUE) {
         int compare = Integer.compare(value, this.wallSliceValue);
         if (compare != 0) {
            this.directionBandStats.directionBandValue = this.directionBandStats.directionBandValue + Math.abs(value - this.wallSliceValue);
            if (this.compare != 0 && compare != this.compare) {
               this.directionBandStats.bandUpdateCount++;
            }

            this.compare = compare;
         }
      }

      this.wallSliceValue = value;
   }

   private static boolean canPossDown(PosBlockDirection.Pending pending, BlockPos pos) {
      BlockPos down = pos.down();

      for (BlockPos blockPos : pending.blockPoss) {
         if (blockPos.equals(down)) {
            return true;
         }
      }

      return false;
   }

   public void handle(BlockPos pos, Direction direction, BlockPos pos2, PosBlockDirection.Failure failure) {
      if (pos != null) {
         this.directionBandStats.handleCallCount++;
         long long2 = pos.asLong();
         Set ifAbsent = this.wallSliceSetMap.computeIfAbsent(long2, long3 -> new HashSet<>());
         if (!ifAbsent.add(formatText(pos, direction, pos2, failure))) {
            this.directionBandStats.handleSkipCount++;
         }

         ifAbsent.add(formatText(pos, direction, pos2, null));
         int merge = this.wallSliceIntegerMap.merge(long2, 1, Integer::sum);
         if (merge >= this.getValue()) {
            this.advanceDirectionPositionSet.add(long2);
            this.wallSliceIntegerMap.remove(long2);
         }
      }
   }

   public boolean canDirectionSlice(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      if (this.sliceWallFlag) {
         return false;
      } else {
         BlockPos blockPos = this.directionAxis.getBlockPos(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue);
         boolean[] booleans = new boolean[]{false};
         AxisPosUtils.handleAxis(blockPos, Direction.from(this.directionAxis.getDepth2(), AxisDirection.POSITIVE), (x2, y2, open) -> {
            if (!booleans[0] && cellWorld.canFlag(x2, y2, open) && canOffset(cellWorld, new BlockPos(x2, y2, open), this.directionAxis.getDepth2())) {
               booleans[0] = true;
            }
         });
         return !booleans[0];
      }
   }

   private static boolean canOffset(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos, Axis axis) {
      for (AxisDirection axisDirection : AxisDirection.values()) {
         Direction from = Direction.from(axis, axisDirection);
         if (!cellWorld.canFlag3(pos.getX() + from.getOffsetX(), pos.getY() + from.getOffsetY(), pos.getZ() + from.getOffsetZ())) {
            return true;
         }
      }

      return false;
   }

   public boolean canDirectionSlice(BlockPos pos) {
      if (this.sliceWallFlag) {
         return false;
      } else {
         BlockPos blockPos = this.directionAxis.getBlockPos(this.directionSliceCount, this.sliceWallValue, this.directionBandValue, this.stateDirectionValue);
         return AxisPosUtils.canFlag(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.directionAxis.getDepth2(), pos.getX(), pos.getY(), pos.getZ());
      }
   }

   private boolean canDirectionBand(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      this.updateDirectionBand();
      if (++this.directionBandValue < this.directionAxis.getMinForInt(this.directionSliceCount)) {
         this.advanceSelectType = PosBlockDirection.State.SELECT_VERTICAL_BAND;
         return true;
      } else {
         this.directionBandValue = this.directionAxis.getMinForInt(this.directionSliceCount) - 1;
         this.stateDirectionValue = this.directionAxis.getSweeps() - 1;
         if (!this.arrayDeque.isEmpty()) {
            this.advanceSelectType = PosBlockDirection.State.WAIT_WORLD_CONFIRMATION;
            return false;
         } else if (this.canDirectionSlice(cellWorld, this.getSliceWallDirectionAxis(), this.getSliceWall())) {
            return false;
         } else {
            this.directionBandValue = 0;
            this.stateDirectionValue = 0;
            this.advanceSelectType = PosBlockDirection.State.VERIFY_WALL_PLANE;
            if (this.canFlag(cellWorld)) {
               if (this.directionBandCount++ < 2) {
                  this.advanceDirectionPositionSet.clear();
                  this.directionBandActive = true;
                  this.directionBandStats.directionBandCount++;
                  this.advanceSelectType = PosBlockDirection.State.SELECT_VERTICAL_BAND;
                  return true;
               }

               this.handleDirectionAxis(this.getApi(cellWorld), this.getSliceWallDirectionAxis(), this.getSliceWall());
            }

            this.directionBandStats.directionBandChecks++;
            this.directionBandCount = 0;
            this.directionBandActive = false;
            return this.canWallSlice(cellWorld);
         }
      }
   }

   public boolean canFlag(BlockPos pos, Direction direction, BlockPos pos2) {
      Set set = this.wallSliceSetMap.get(pos.asLong());
      return set != null && set.contains(formatText(pos, direction, pos2, null));
   }

   public PosBlockDirection.Target getResidueSeg(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.CellWorld api, int value) {
      if (this.residueSegValue == value) {
         return this.residueSeg;
      } else {
         this.residueSegValue = value;
         this.residueSegCellWorld = api;
         this.residueSeg = this.getDirectionSlice(cellWorld, value);
         return this.residueSeg;
      }
   }

   public boolean isSliceWallFlag() {
      return this.sliceWallFlag;
   }

   private void updateWallSlice() {
      this.wallSliceUpdateCount++;
      this.directionBandStats.wallSliceCount++;
      this.directionSliceCount = 0;
      this.sliceWallValue = 0;
      this.directionBandValue = 0;
      this.stateDirectionValue = 0;
      this.advanceDirectionPositionSet.clear();
      this.wallSliceIntegerMap.clear();
      this.wallSliceSetMap.clear();
      this.advanceDirectionCount = 0;
      this.directionBandCount = 0;
      this.directionBandActive = true;
      this.wallSliceValue = Integer.MIN_VALUE;
      this.residueSegValue = Integer.MIN_VALUE;
      this.advanceSelectType = PosBlockDirection.State.SELECT_WALL_PLANE;
   }

   public boolean canFlag(BlockPos pos) {
      return pos != null && this.possAdvancePositionSet.contains(pos.asLong());
   }

   public boolean canDirectionAxis(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionAxisValue, int y2, int z2) {
      return DirectionOffsetUtilsUseful.canSpillRank(
         cellWorld,
         this.directionAxis.getAxisHelper(),
         directionAxisValue,
         y2,
         z2,
         this.directionAxis.getDepth2(),
         this.directionAxis::getBandsSweeps,
         this.getSliceWallSliceWallFlag(),
         this.getDirectionAxis()
      );
   }

   private static BlockPos getBlockPos(BlockPos pos, Axis axis, int blockPosValue, Axis axis2, int j, Axis axis3, int k) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      switch (axis) {
         case X:
            x += blockPosValue;
            break;
         case Y:
            y += blockPosValue;
            break;
         case Z:
            z += blockPosValue;
      }

      switch (axis2) {
         case X:
            x += j;
            break;
         case Y:
            y += j;
            break;
         case Z:
            z += j;
      }

      switch (axis3) {
         case X:
            x += k;
            break;
         case Y:
            y += k;
            break;
         case Z:
            z += k;
      }

      return new BlockPos(x, y, z);
   }

   private boolean canAdvanceDirection(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      if (++this.stateDirectionValue < this.directionAxis.getSweeps()) {
         this.advanceSelectType = PosBlockDirection.State.SELECT_HORIZONTAL_SEGMENT;
         return true;
      } else {
         this.stateDirectionValue = this.directionAxis.getSweeps() - 1;
         if (!this.arrayDeque.isEmpty()) {
            this.advanceSelectType = PosBlockDirection.State.WAIT_WORLD_CONFIRMATION;
            return false;
         } else if (this.canDirectionSlice(cellWorld, this.getMaxDirectionAxis(), this.getMin())) {
            return false;
         } else {
            this.stateDirectionValue = 0;
            this.advanceSelectType = PosBlockDirection.State.ADVANCE_HORIZONTAL;
            if (this.canFlagForCellWorld(cellWorld)) {
               if (this.advanceDirectionCount++ < 2) {
                  this.advanceDirectionPositionSet.clear();
                  this.directionBandActive = true;
                  this.directionBandStats.directionBandCount++;
                  this.advanceSelectType = PosBlockDirection.State.SELECT_HORIZONTAL_SEGMENT;
                  return true;
               }

               this.handleDirectionAxis(this.getApi(cellWorld), this.getMaxDirectionAxis(), this.getMin());
            }

            this.directionBandStats.advanceDirectionCount++;
            this.advanceDirectionCount = 0;
            this.directionBandActive = false;
            this.advanceDirectionPositionSet.clear();
            return this.canDirectionBand(cellWorld);
         }
      }
   }

   private DirectionOffsetUtilsUseful.CellWorld getApi(DirectionOffsetUtilsUseful.CellWorld cellWorld) {
      return this.residueSegCellWorld != null ? this.residueSegCellWorld : cellWorld;
   }

   private BlockPos getPositiveDirectionForCellWorldBlockPos(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos) {
      if (cellWorld.canFlag(pos.getX(), pos.getY(), pos.getZ())) {
         return cellWorld.canFlag5(pos.getX(), pos.getY(), pos.getZ()) ? null : pos;
      } else {
         BlockPos[] blockPos = new BlockPos[]{null};
         AxisPosUtils.handleAxis(pos, Direction.from(this.directionAxis.getDepth2(), AxisDirection.POSITIVE), (out, y, z2) -> {
            if (blockPos[0] == null && cellWorld.canFlag(out, y, z2) && !cellWorld.canFlag5(out, y, z2)) {
               blockPos[0] = new BlockPos(out, y, z2);
            }
         });
         return blockPos[0];
      }
   }

   public static enum Failure {
      UNREACHABLE,
      OCCLUDED,
      WRONG_FACE,
      WOULD_SPILL,
      NO_STAND,
      NO_PROGRESS,
      NO_TOOL;
   }

   private static final class Pending {
      final int removedExpectedValue;
      final int expireAtLimit;
      final Direction direction;
      final BlockPos blockPos;
      final boolean[] centreExpectedFlags;
      final BlockPos[] blockPoss;
      final long[] timesCentreExpected;

      Pending(BlockPos blockPos, Direction direction, int removedExpectedValue, int value, BlockPos[] blockPoss, long[] times, boolean[] flags) {
         this.blockPos = blockPos;
         this.direction = direction;
         this.removedExpectedValue = removedExpectedValue;
         this.expireAtLimit = value;
         this.blockPoss = blockPoss;
         this.timesCentreExpected = times;
         this.centreExpectedFlags = flags;
      }
   }

   public static enum State {
      SELECT_WALL_PLANE,
      SELECT_VERTICAL_BAND,
      SELECT_HORIZONTAL_SEGMENT,
      MOVE_TO_WORK_LANE,
      ALIGN_DRILL,
      DRILL,
      WAIT_WORLD_CONFIRMATION,
      VERIFY_LOCAL_FOOTPRINT,
      ADVANCE_HORIZONTAL,
      ADVANCE_VERTICAL_BAND,
      VERIFY_WALL_PLANE,
      ADVANCE_DEPTH,
      DONE;
   }

   public static final class Stats {
      public int bandUpdateCount;
      public int possAdvanceCount;
      public int wallSliceCount;
      public int directionBandValue;
      public int statsCount;
      public int centreExpectedCount;
      public int wallSliceChecks;
      public int sliceWallValue;
      public int directionBandCount;
      public int directionBandChecks;
      public int centreExpectedTotal;
      public int handleCallCount;
      public int centreExpectedMisses;
      public int updateCallCount;
      public int centreValue;
      public int statsValue;
      public int advanceDirectionCount;
      public int handleSkipCount;

      public double getAmount() {
         return this.possAdvanceCount == 0 ? 0.0 : (double)this.centreExpectedCount / this.possAdvanceCount;
      }
   }

   public static final class Target {
      public final boolean repairProbeFlag;
      public final boolean residueRepairFlag;
      public final int lift;
      public final int stringValue;
      public final int plane;
      public final BlockPos centre;
      public final BlockPos stand;
      public final boolean probeFaceFlag;
      public final int band2String;
      public final BlockPos aim;
      public final long targetTimeMs;
      public final Direction face;

      @Override
      public String toString() {
         return "lift="
            + this.lift
            + " plane="
            + this.plane
            + " band="
            + this.band2String
            + " seg="
            + this.stringValue
            + " centre="
            + PosBlockDirection.formatText(this.centre)
            + " aim="
            + PosBlockDirection.formatText(this.aim)
            + " stand="
            + PosBlockDirection.formatText(this.stand)
            + " face="
            + this.face
            + (this.probeFaceFlag ? " PROBE" : "")
            + (this.repairProbeFlag ? " REPAIR" : "")
            + (this.residueRepairFlag ? " RESIDUE-1x1" : "");
      }

      Target(
         int targetValue, int targetValue2, int targetValue3, int seg, BlockPos pos, BlockPos pos2, BlockPos pos3, Direction direction, long targetValue5, boolean targetFlag, boolean targetFlag2
      ) {
         this(targetValue, targetValue2, targetValue3, seg, pos, pos2, pos3, direction, targetValue5, targetFlag, targetFlag2, false);
      }

      Target(
         int targetValue,
         int plane2,
         int band2,
         int seg,
         BlockPos centre,
         BlockPos pos2,
         BlockPos pos3,
         Direction direction,
         long time,
         boolean probeFaceFlag,
         boolean repairProbeFlag,
         boolean residueRepairFlag
      ) {
         this.lift = targetValue;
         this.plane = plane2;
         this.band2String = band2;
         this.stringValue = seg;
         this.centre = centre;
         this.aim = pos2;
         this.stand = pos3;
         this.face = direction;
         this.targetTimeMs = time;
         this.probeFaceFlag = probeFaceFlag;
         this.repairProbeFlag = repairProbeFlag;
         this.residueRepairFlag = residueRepairFlag;
      }
   }
}
