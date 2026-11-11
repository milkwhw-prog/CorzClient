package com.corz.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import com.corz.client.utils.DirectionOffsetUtils2;

public final class DirectionOffsetUtilsUseful {

   static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, Axis axis, int directionOffsetValue, int cover, int breaksPattern, int axisValue, int directionOffsetValue5, int directionOffsetValue6) {
      Axis[] axiss = AxisPosUtils.getAxiss(axis);
      int directionOffsetValue7 = getValue(directionOffsetValue, cover, breaksPattern, axiss[0]);
      int directionOffsetValue8 = getValue(directionOffsetValue, cover, breaksPattern, axiss[1]);
      int directionOffsetValue9 = bounds.getValue(axiss[0]);
      int valueForAxis = bounds.getValueForAxis(axiss[0]);
      int directionOffsetValue11 = bounds.getValue(axiss[1]);
      int valueForAxis12 = bounds.getValueForAxis(axiss[1]);

      for (int i = -1; i <= 1; i++) {
         if (DirectionOffsetUtils2.canFlag(directionOffsetValue7 + i, directionOffsetValue9, valueForAxis)) {
            for (int j = -1; j <= 1; j++) {
               if (DirectionOffsetUtils2.canFlag(directionOffsetValue8 + j, directionOffsetValue11, valueForAxis12)) {
                  int directionOffsetValue2 = directionOffsetValue;
                  int cover2 = cover;
                  int breaksPattern2 = breaksPattern;
                  switch (axiss[0]) {
                     case X:
                        directionOffsetValue2 = directionOffsetValue7 + i;
                        break;
                     case Y:
                        cover2 = directionOffsetValue7 + i;
                        break;
                     case Z:
                        breaksPattern2 = directionOffsetValue7 + i;
                  }

                  switch (axiss[1]) {
                     case X:
                        directionOffsetValue2 = directionOffsetValue8 + j;
                        break;
                     case Y:
                        cover2 = directionOffsetValue8 + j;
                        break;
                     case Z:
                        breaksPattern2 = directionOffsetValue8 + j;
                  }

                  if ((directionOffsetValue2 != axisValue || cover2 != directionOffsetValue5 || breaksPattern2 != directionOffsetValue6) && cellWorld.canFlag(directionOffsetValue2, cover2, breaksPattern2) && canFlagForCellWorldBounds(cellWorld, bounds, axis, directionOffsetValue2, cover2, breaksPattern2)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static DirectionOffsetUtilsUseful.Candidate getSpillGrid(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, int spillGridValue, int directionOffsetValue, int ey2, Axis axis
   ) {
      return getOffsetPositive(cellWorld, bounds, spillGridValue, directionOffsetValue, ey2, axis, null);
   }

   private static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Request request) {
      return canOffset(cellWorld, request.usefulSpillValue, request.rankAxisValue, request.cellZ, getDirection(request));
   }

   static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, Axis axis, int directionOffsetValue, int cover, int ey2) {
      Axis[] axiss = AxisPosUtils.getAxiss(axis);
      if (!DirectionOffsetUtils2.canFlag(getValue(directionOffsetValue, cover, ey2, axiss[0]), bounds.getValue(axiss[0]), bounds.getValueForAxis(axiss[0]))) {
         return false;
      } else {
         return !DirectionOffsetUtils2.canFlag(getValue(directionOffsetValue, cover, ey2, axiss[1]), bounds.getValue(axiss[1]), bounds.getValueForAxis(axiss[1]))
            ? false
            : cellWorld.canFlag(directionOffsetValue, cover, ey2) && canFlagForCellWorldBounds(cellWorld, bounds, axis, directionOffsetValue, cover, ey2);
      }
   }

   static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, Axis axis, int directionOffsetValue, int cover, int breaksPattern, int axisPosValue, int cxx, int bestD, Axis axis2) {
      boolean[] booleans = new boolean[]{false};
      AxisPosUtils.handleVisit(directionOffsetValue, cover, breaksPattern, axis, (fxx, directionOffsetValue7, outside) -> {
         if (!booleans[0] && cellWorld.canFlag(fxx, directionOffsetValue7, outside) && !AxisPosUtils.canFlag(axisPosValue, cxx, bestD, axis2, fxx, directionOffsetValue7, outside)) {
            booleans[0] = true;
         }
      });
      return booleans[0];
   }

   private static DirectionOffsetUtilsUseful.Candidate getAxis(
      DirectionOffsetUtilsUseful.CellWorld cellWorld,
      DirectionOffsetUtilsUseful.Bounds bounds,
      int axisValue,
      int directionOffsetValue,
      int ey2,
      Direction direction,
      Axis axis,
      int axisValue4,
      boolean axisFlag
   ) {
      Axis axis2 = direction.getAxis();
      Axis axis3 = axis != null ? axis : axis2;
      boolean axisFlag2 = axis2 != axis3;
      Axis[] axiss = AxisPosUtils.getAxiss(axis2);
      boolean canFlag3 = DirectionOffsetUtils2.canFlag(getValue(axisValue, directionOffsetValue, ey2, axiss[0]), bounds.getValue(axiss[0]), bounds.getValueForAxis(axiss[0]));
      boolean canFlag4 = DirectionOffsetUtils2.canFlag(getValue(axisValue, directionOffsetValue, ey2, axiss[1]), bounds.getValue(axiss[1]), bounds.getValueForAxis(axiss[1]));
      boolean axisFlag5 = !axisFlag2 && canFlag3 && canFlag4;
      int[] ints = new int[3];
      boolean[] booleans = new boolean[]{false};
      AxisPosUtils.handleVisit(
         axisValue,
         directionOffsetValue,
         ey2,
         axis2,
         (cellWorldValue, cover, breaksPattern) -> {
            if (cellWorld.canFlag(cellWorldValue, cover, breaksPattern)) {
               if (!axisFlag5 && canFlag(cellWorld, bounds, axis3, cellWorldValue, cover, breaksPattern, axisValue, directionOffsetValue, ey2)) {
                  ints[2]++;
               } else {
                  ints[0]++;
               }

               if (axisFlag2
                  && (cellWorldValue != axisValue || cover != directionOffsetValue || breaksPattern != ey2)
                  && canFlag(cellWorld, bounds, axis3, cellWorldValue, cover, breaksPattern)
                  && canFlag(cellWorld, axis3, cellWorldValue, cover, breaksPattern, axisValue, directionOffsetValue, ey2, axis2)) {
                  booleans[0] = true;
               }
            } else if (!cellWorld.canFlagForInt(cellWorldValue, cover, breaksPattern) && cellWorld.canFlag3(cellWorldValue, cover, breaksPattern)) {
               ints[1]++;
            }
         }
      );
      boolean axisFlag6 = ints[0] == 0;
      if (!axisFlag6 || axisFlag && ints[2] != 0) {
         if (axisFlag2 && !axisFlag6 && booleans[0]) {
            return null;
         } else {
            int axisValue6 = axisFlag6 ? ints[2] * 25 : ints[0] * 100;
            int axisValue7 = axisValue6 + (canFlag3 ? 350 : 0) + (canFlag4 ? 350 : 0) - ints[1] * 150 - (axisFlag2 ? 350 : 0) - axisValue4;
            return new DirectionOffsetUtilsUseful.Candidate(axisValue, directionOffsetValue, ey2, direction, ints[0], ints[2], ints[1], canFlag3, canFlag4, axisValue7, axisFlag6, axisFlag2);
         }
      } else {
         return null;
      }
   }

   public static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, int directionOffsetValue, int directionOffsetValue2, int directionOffsetValue3, Axis axis) {
      if (cellWorld.canFlag(directionOffsetValue, directionOffsetValue2, directionOffsetValue3)) {
         return true;
      } else {
         boolean[] booleans = new boolean[]{false};
         AxisPosUtils.handleVisit(directionOffsetValue, directionOffsetValue2, directionOffsetValue3, axis, (cyx, cover, ey2) -> {
            if (!booleans[0] && cellWorld.canFlag(cyx, cover, ey2) && !canFlag(cellWorld, bounds, axis, cyx, cover, ey2, directionOffsetValue, directionOffsetValue2, directionOffsetValue3)) {
               booleans[0] = true;
            }
         });
         return booleans[0];
      }
   }

   public static boolean canOffset(DirectionOffsetUtilsUseful.CellWorld cellWorld, int offsetValue, int cxx, int bestD, Direction direction) {
      return !cellWorld.canFlag3(offsetValue + direction.getOffsetX(), cxx + direction.getOffsetY(), bestD + direction.getOffsetZ());
   }

   public static DirectionOffsetUtilsUseful.Candidate getSpillGrid(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, DirectionOffsetUtilsUseful.Request request
   ) {
      return getAmountAxis(cellWorld, bounds, request, false);
   }

   public static DirectionOffsetUtilsUseful.Candidate getRankAxis(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, DirectionOffsetUtilsUseful.Request request
   ) {
      DirectionOffsetUtilsUseful.Candidate amountAxis = getAmountAxis(cellWorld, bounds, request, false);
      boolean rankAxisFlag = !request.rankAxisActive || request.rankAxisAxis == null || !canFlag(cellWorld, request);
      if (amountAxis == null && rankAxisFlag) {
         amountAxis = getAmountAxis(cellWorld, bounds, request, true);
      }

      if (amountAxis == null) {
         return null;
      } else {
         boolean rankAxisFlag2 = request.rankAxisActive && amountAxis.canUsefulSpill(request.usefulSpillValue, request.rankAxisValue, request.cellZ);
         if (rankAxisFlag2 && request.direction != null && amountAxis.opposite == request.direction) {
            return amountAxis;
         } else {
            boolean rankAxisFlag3 = request.rankAxisAxis == null || amountAxis.getAxis() == request.rankAxisAxis;
            boolean rankAxisFlag4 = request.rankAxisCellRank != null
               && request.rankAxisActive
               && request.rankAxisCellRank.rank(amountAxis.usefulValue, amountAxis.usefulSpillValue, amountAxis.usefulSpillValue2UsefulSpill) > request.rankAxisCellRank.rank(request.usefulSpillValue, request.rankAxisValue, request.cellZ);
            if (amountAxis.isGridRotated() && rankAxisFlag3 && !rankAxisFlag4) {
               return amountAxis;
            } else {
               boolean rankAxisFlag5 = request.rankAxisActive && getAmount(request.rankAxisAmount, request.requestAmount, request.targetZ, request.usefulSpillValue, request.rankAxisValue, request.cellZ) <= request.costLimit * request.costLimit;
               return rankAxisFlag5 && request.rankAxisAxis != null && canAmount(cellWorld, request) ? null : amountAxis;
            }
         }
      }
   }

   public static DirectionOffsetUtilsUseful.Candidate getOffsetPositive(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, int offsetPositiveValue, int directionOffsetValue, int ey2, Axis axis, Direction direction
   ) {
      Direction from = Direction.from(axis, AxisDirection.POSITIVE);
      if (cellWorld.canFlag(offsetPositiveValue, directionOffsetValue, ey2) && canOffset(cellWorld, offsetPositiveValue, directionOffsetValue, ey2) && canFlagForCellWorldBounds(cellWorld, bounds, axis, offsetPositiveValue, directionOffsetValue, ey2)) {
         DirectionOffsetUtilsUseful.Candidate spillGrid = getSpillGrid(cellWorld, bounds, offsetPositiveValue, directionOffsetValue, ey2, from, axis);
         if (spillGrid != null) {
            return getUsefulSpill(cellWorld, spillGrid);
         }
      }

      DirectionOffsetUtilsUseful.Candidate[] candidates = new DirectionOffsetUtilsUseful.Candidate[]{null};
      int[] ints = new int[]{Integer.MAX_VALUE};
      AxisPosUtils.handleVisit(offsetPositiveValue, directionOffsetValue, ey2, axis, (normalx, cyx, czx) -> {
         if (canOffset(cellWorld, normalx, cyx, czx)) {
            DirectionOffsetUtilsUseful.Candidate var12x = getSpillGrid(cellWorld, bounds, normalx, cyx, czx, from, axis);
            if (var12x != null && var12x.spill <= 0) {
               int var13x = (normalx - offsetPositiveValue) * (normalx - offsetPositiveValue) + (cyx - directionOffsetValue) * (cyx - directionOffsetValue) + (czx - ey2) * (czx - ey2);
               if (candidates[0] == null || var12x.score > candidates[0].score || var12x.score == candidates[0].score && var13x < ints[0]) {
                  candidates[0] = var12x;
                  ints[0] = var13x;
               }
            }
         }
      });
      if (candidates[0] != null) {
         return getUsefulSpill(cellWorld, candidates[0]);
      } else {
         for (int i = 0; i <= (direction == null ? 0 : 1); i++) {
            int offsetPositiveValue4 = direction == null ? 0 : direction.getOffsetX() * i;
            int offsetPositiveValue5 = direction == null ? 0 : direction.getOffsetY() * i;
            int offsetPositiveValue6 = direction == null ? 0 : direction.getOffsetZ() * i;
            AxisPosUtils.handleVisit(
               offsetPositiveValue + offsetPositiveValue4,
               directionOffsetValue + offsetPositiveValue5,
               ey2 + offsetPositiveValue6,
               axis,
               (cyx, cxx, bestD) -> {
                  if (bounds.canFlag(cyx, cxx, bestD) && canOffset(cellWorld, cyx, cxx, bestD)) {
                     for (Direction var14x : Direction.values()) {
                        if (var14x.getAxis() != axis && canOffset(cellWorld, cyx, cxx, bestD, var14x)) {
                           DirectionOffsetUtilsUseful.Candidate spillGrid2 = getSpillGrid(cellWorld, bounds, cyx, cxx, bestD, var14x, axis);
                           if (spillGrid2 != null
                              && spillGrid2.spill <= 0
                              && (
                                 AxisPosUtils.canFlag(cyx, cxx, bestD, var14x.getAxis(), offsetPositiveValue, directionOffsetValue, ey2)
                                    || canFlag(cellWorld, offsetPositiveValue, directionOffsetValue, ey2, axis, cyx, cxx, bestD, var14x.getAxis())
                              )) {
                              int offsetPositiveValue7 = (cyx - offsetPositiveValue) * (cyx - offsetPositiveValue) + (cxx - directionOffsetValue) * (cxx - directionOffsetValue) + (bestD - ey2) * (bestD - ey2);
                              if (candidates[0] == null
                                 || spillGrid2.useful > candidates[0].useful
                                 || spillGrid2.useful == candidates[0].useful && (spillGrid2.score > candidates[0].score || spillGrid2.score == candidates[0].score && offsetPositiveValue7 < ints[0])) {
                                 candidates[0] = spillGrid2;
                                 ints[0] = offsetPositiveValue7;
                              }
                           }
                        }
                     }
                  }
               }
            );
         }

         return candidates[0];
      }
   }

   private static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, int directionOffsetValue, int cover, int ey2, Axis axis, int axisPosValue, int cxx, int bestD, Axis axis2) {
      boolean[] booleans = new boolean[]{false};
      AxisPosUtils.handleVisit(directionOffsetValue, cover, ey2, axis, (cellWorldValue, fxx, hit) -> {
         if (!booleans[0] && cellWorld.canFlag(cellWorldValue, fxx, hit) && AxisPosUtils.canFlag(axisPosValue, cxx, bestD, axis2, cellWorldValue, fxx, hit)) {
            booleans[0] = true;
         }
      });
      return booleans[0];
   }

   public static boolean canOffset(DirectionOffsetUtilsUseful.CellWorld cellWorld, double offsetValue, double offsetValue2, double offsetValue3, int offsetValue4, int cxx, int bestD, Direction direction, double offsetValue7) {
      if (!canOffset(cellWorld, offsetValue4, cxx, bestD, direction)) {
         return false;
      } else {
         double offsetValue8 = offsetValue4 + 0.5 + direction.getOffsetX() * 0.5;
         double offsetValue9 = cxx + 0.5 + direction.getOffsetY() * 0.5;
         double offsetValue10 = bestD + 0.5 + direction.getOffsetZ() * 0.5;
         double offsetFactor = offsetValue - offsetValue8;
         double offsetFactor2 = offsetValue2 - offsetValue9;
         double offsetFactor3 = offsetValue3 - offsetValue10;
         double offsetValue11 = offsetFactor * direction.getOffsetX() + offsetFactor2 * direction.getOffsetY() + offsetFactor3 * direction.getOffsetZ();
         if (offsetValue11 < 0.3) {
            return false;
         } else {
            double distance = Math.sqrt(offsetFactor * offsetFactor + offsetFactor2 * offsetFactor2 + offsetFactor3 * offsetFactor3);
            return distance > offsetValue7 ? false : offsetValue11 / distance >= 0.4;
         }
      }
   }

   private static DirectionOffsetUtilsUseful.Candidate getUsefulSpill(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Candidate usefulSpill) {
      if (canOffset(cellWorld, usefulSpill.usefulValue, usefulSpill.usefulSpillValue, usefulSpill.usefulSpillValue2UsefulSpill, usefulSpill.opposite)) {
         return usefulSpill;
      } else {
         Direction opposite = usefulSpill.opposite.getOpposite();
         return !canOffset(cellWorld, usefulSpill.usefulValue, usefulSpill.usefulSpillValue, usefulSpill.usefulSpillValue2UsefulSpill, opposite)
            ? usefulSpill
            : new DirectionOffsetUtilsUseful.Candidate(
               usefulSpill.usefulValue,
               usefulSpill.usefulSpillValue,
               usefulSpill.usefulSpillValue2UsefulSpill,
               opposite,
               usefulSpill.useful,
               usefulSpill.covered,
               usefulSpill.spill,
               usefulSpill.gridUsefulSpill,
               usefulSpill.rotatedGridFlag,
               usefulSpill.score,
               usefulSpill.usefulSpillFlag,
               usefulSpill.rotatedScoreFlag
            );
      }
   }

   public static boolean canFlagForCellWorldBounds(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, Axis axis, int directionOffsetValue, int directionOffsetValue2, int ey2) {
      boolean[] booleans = new boolean[]{false};
      AxisPosUtils.handleVisit(directionOffsetValue, directionOffsetValue2, ey2, axis, (spill, cover, breaksPattern) -> {
         if (!booleans[0] && !cellWorld.canFlagForInt(spill, cover, breaksPattern) && cellWorld.canFlag3(spill, cover, breaksPattern)) {
            booleans[0] = true;
         }
      });
      return !booleans[0];
   }

   public static boolean canOffset(DirectionOffsetUtilsUseful.CellWorld cellWorld, int offsetValue, int directionOffsetValue, int ey2) {
      if (cellWorld.canFlag(offsetValue, directionOffsetValue, ey2) && !cellWorld.canFlag5(offsetValue, directionOffsetValue, ey2)) {
         for (Direction direction : Direction.values()) {
            if (!cellWorld.canFlag3(offsetValue + direction.getOffsetX(), directionOffsetValue + direction.getOffsetY(), ey2 + direction.getOffsetZ())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static double getAmount(double directionOffsetLimit, double j, double directionOffsetValue) {
      return directionOffsetLimit < j ? j : (directionOffsetLimit > directionOffsetValue ? directionOffsetValue : directionOffsetLimit);
   }

   private static boolean canUsefulSpill(
      DirectionOffsetUtilsUseful.Candidate candidate,
      int usefulSpillValue,
      double usefulSpillValue2,
      DirectionOffsetUtilsUseful.Candidate amountAxis,
      int usefulSpillLimit,
      double usefulSpillLimit2,
      DirectionOffsetUtilsUseful.Request request
   ) {
      if (usefulSpillValue != usefulSpillLimit) {
         return usefulSpillValue < usefulSpillLimit;
      } else if (candidate.score != amountAxis.score) {
         return candidate.score > amountAxis.score;
      } else {
         boolean usefulSpill = request.rankAxisActive && candidate.canUsefulSpill(request.usefulSpillValue, request.rankAxisValue, request.cellZ);
         boolean usefulSpillFlag = request.rankAxisActive && amountAxis.canUsefulSpill(request.usefulSpillValue, request.rankAxisValue, request.cellZ);
         if (usefulSpill != usefulSpillFlag) {
            return usefulSpill;
         } else if (usefulSpill && request.direction != null && candidate.opposite == request.direction != (amountAxis.opposite == request.direction)) {
            return candidate.opposite == request.direction;
         } else if (candidate.usefulSpillValue != amountAxis.usefulSpillValue) {
            return candidate.usefulSpillValue > amountAxis.usefulSpillValue;
         } else if (Math.abs(usefulSpillValue2 - usefulSpillLimit2) > 1.0E-9) {
            return usefulSpillValue2 < usefulSpillLimit2;
         } else if (candidate.usefulValue != amountAxis.usefulValue) {
            return candidate.usefulValue < amountAxis.usefulValue;
         } else {
            return candidate.usefulSpillValue2UsefulSpill != amountAxis.usefulSpillValue2UsefulSpill
               ? candidate.usefulSpillValue2UsefulSpill < amountAxis.usefulSpillValue2UsefulSpill
               : candidate.opposite.ordinal() < amountAxis.opposite.ordinal();
         }
      }
   }

   public static boolean canSpillRank(
      DirectionOffsetUtilsUseful.CellWorld cellWorld,
      DirectionOffsetUtilsUseful.Bounds bounds,
      int spillRankValue,
      int y2,
      int z2,
      Axis axis,
      DirectionOffsetUtilsUseful.CellRank cellRank,
      long spillRankValue4,
      long spillRankValue5
   ) {
      for (Axis axis2 : Axis.values()) {
         long spillRankLimit = axis != null && axis2 != axis ? spillRankValue5 : spillRankValue4;
         Axis[] axiss = AxisPosUtils.getAxiss(axis2);

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               int spillRankValue2 = spillRankValue;
               int cover = y2;
               int ey2 = z2;
               switch (axiss[0]) {
                  case X:
                     spillRankValue2 = spillRankValue + i;
                     break;
                  case Y:
                     cover = y2 + i;
                     break;
                  case Z:
                     ey2 = z2 + i;
               }

               switch (axiss[1]) {
                  case X:
                     spillRankValue2 += j;
                     break;
                  case Y:
                     cover += j;
                     break;
                  case Z:
                     ey2 += j;
               }

               if (cellWorld.canFlag(spillRankValue2, cover, ey2) && (cellRank == null || cellRank.rank(spillRankValue2, cover, ey2) <= spillRankLimit)) {
                  for (AxisDirection axisDirection : AxisDirection.values()) {
                     Direction from = Direction.from(axis2, axisDirection);
                     if (canOffset(cellWorld, spillRankValue2, cover, ey2, from)) {
                        DirectionOffsetUtilsUseful.Candidate spillGrid = getSpillGrid(cellWorld, bounds, spillRankValue2, cover, ey2, from, axis);
                        if (spillGrid != null && spillGrid.spill == 0 && !spillGrid.usefulSpillFlag) {
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private DirectionOffsetUtilsUseful() {
   }

   public static DirectionOffsetUtilsUseful.Candidate getSpillGrid(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, int spillGridValue, int directionOffsetValue, int ey2, Direction direction, Axis axis
   ) {
      return getAxis(cellWorld, bounds, spillGridValue, directionOffsetValue, ey2, direction, axis, 0, false);
   }

   private static Direction getDirection(DirectionOffsetUtilsUseful.Request request) {
      Direction direction = request.direction;
      if (direction == null) {
         Axis axis = request.rankAxisAxis;

         double directionLimit = switch (axis) {
            case X -> request.rankAxisAmount;
            case Y -> request.requestAmount;
            case Z -> request.targetZ;
         };
         double directionValue = getValue(request.usefulSpillValue, request.rankAxisValue, request.cellZ, axis) + 0.5;
         direction = Direction.from(axis, directionLimit >= directionValue ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE);
      }

      return direction;
   }

   public static boolean canFlag(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, int directionOffsetValue, int y2, int z2) {
      return canSpillRank(cellWorld, bounds, directionOffsetValue, y2, z2, null, null, Long.MAX_VALUE, Long.MAX_VALUE);
   }

   private static boolean canAmount(DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Request request) {
      int usefulSpillValue = request.usefulSpillValue;
      int rankAxisValue = request.rankAxisValue;
      int directionOffsetValue = request.cellZ;
      if (cellWorld.canFlag(usefulSpillValue, rankAxisValue, directionOffsetValue) && !cellWorld.canFlag5(usefulSpillValue, rankAxisValue, directionOffsetValue)) {
         Direction direction = getDirection(request);
         return !canFlag(cellWorld, request) ? false : !canOffset(cellWorld, request.rankAxisAmount, request.requestAmount, request.targetZ, usefulSpillValue, rankAxisValue, directionOffsetValue, direction, request.axisAmount);
      } else {
         return false;
      }
   }

   public static DirectionOffsetUtilsUseful.Candidate getAmountAxis(
      DirectionOffsetUtilsUseful.CellWorld cellWorld, DirectionOffsetUtilsUseful.Bounds bounds, DirectionOffsetUtilsUseful.Request request, boolean axisFlag
   ) {
      int axisValue = (int)Math.floor(request.rankAxisAmount);
      int floor2 = (int)Math.floor(request.requestAmount);
      int floor3 = (int)Math.floor(request.targetZ);
      int axisValue4 = (int)Math.ceil(request.axisAmount) + 1;
      int max5 = Math.max(bounds.minX, axisValue - axisValue4);
      int axisLimit = Math.min(bounds.directionAxisValue, axisValue + axisValue4);
      int max6 = Math.max(bounds.negativePositiveValue, floor2 - axisValue4);
      int min7 = Math.min(bounds.maxY, floor2 + axisValue4);
      int max8 = Math.max(bounds.minZ, floor3 - axisValue4);
      int min2 = Math.min(bounds.boundsValue, floor3 + axisValue4);
      double axisValue9 = request.axisAmount * request.axisAmount;
      boolean axisFlag2 = request.rankAxisCellRank != null && request.rankAxisActive;
      long axisValue10 = axisFlag2 ? request.rankAxisCellRank.rank(request.usefulSpillValue, request.rankAxisValue, request.cellZ) : 0L;
      DirectionOffsetUtilsUseful.Candidate amountAxis = null;
      int usefulSpillLimit = 0;
      double axisValue12 = 0.0;

      for (int i = min7; i >= max6; i--) {
         for (int j = max5; j <= axisLimit; j++) {
            for (int k = max8; k <= min2; k++) {
               if (cellWorld.canFlag(j, i, k) && !cellWorld.canFlag5(j, i, k)) {
                  double axisLimit3 = getAmount(request.rankAxisAmount, request.requestAmount, request.targetZ, j, i, k);
                  if (!(axisLimit3 > axisValue9)) {
                     long axisLimit4 = axisFlag2 ? request.rankAxisCellRank.rank(j, i, k) : 0L;
                     if (!axisFlag2 || request.timeLimitMs < 0L || axisLimit4 <= axisValue10 + request.timeLimitMs) {
                        int axisValue13 = axisFlag2 && axisLimit4 > axisValue10 ? 700 : 0;

                        for (Direction direction : Direction.values()) {
                           if (canOffset(cellWorld, request.rankAxisAmount, request.requestAmount, request.targetZ, j, i, k, direction, request.axisAmount)) {
                              boolean axisFlag3 = request.rankAxisAxis != null && direction.getAxis() != request.rankAxisAxis;
                              DirectionOffsetUtilsUseful.Candidate axis = getAxis(cellWorld, bounds, j, i, k, direction, request.rankAxisAxis, axisValue13, axisFlag);
                              if (axis != null
                                 && (axis.spill <= 0 || !request.secondAxisActive && (!axisFlag3 || !request.axisActive))
                                 && (request.requestActive || !AxisPosUtils.canFlag(j, i, k, direction.getAxis(), request.axisValue, request.axisDirectionValue - 1, request.requestValue))) {
                                 int usefulSpillValue = i >= request.axisDirectionValue ? 0 : 1;
                                 if (amountAxis == null || canUsefulSpill(axis, usefulSpillValue, axisLimit3, amountAxis, usefulSpillLimit, axisValue12, request)) {
                                    amountAxis = axis;
                                    usefulSpillLimit = usefulSpillValue;
                                    axisValue12 = axisLimit3;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return amountAxis;
   }

   public static double getAmount(double directionOffsetValue, double directionOffsetLimit, double directionOffsetValue3, int j, int i, int k) {
      double directionOffsetValue7 = getAmount(directionOffsetValue, j, j + 1.0);
      double directionOffsetValue8 = getAmount(directionOffsetLimit, i, i + 1.0);
      double directionOffsetValue9 = getAmount(directionOffsetValue3, k, k + 1.0);
      double directionOffsetFactor = directionOffsetValue - directionOffsetValue7;
      double directionOffsetFactor2 = directionOffsetLimit - directionOffsetValue8;
      double directionOffsetFactor3 = directionOffsetValue3 - directionOffsetValue9;
      return directionOffsetFactor * directionOffsetFactor + directionOffsetFactor2 * directionOffsetFactor2 + directionOffsetFactor3 * directionOffsetFactor3;
   }

   static int getValue(int directionOffsetValue, int cover, int breaksPattern, Axis axis) {
      return switch (axis) {
         case X -> directionOffsetValue;
         case Y -> cover;
         case Z -> breaksPattern;
      };
   }

   public static final class Bounds {
      public final int minX;
      public final int directionAxisValue;
      public final int negativePositiveValue;
      public final int boundsValue;
      public final int minZ;
      public final int maxY;

      int getValue(Axis axis) {
         return switch (axis) {
            case X -> this.minX;
            case Y -> this.negativePositiveValue;
            case Z -> this.minZ;
         };
      }

      int getValueForAxis(Axis axis) {
         return switch (axis) {
            case X -> this.directionAxisValue;
            case Y -> this.maxY;
            case Z -> this.boundsValue;
         };
      }

      public Bounds(int value, int value2, int value3, int value4, int value5, int value6) {
         this.minX = value;
         this.negativePositiveValue = value2;
         this.minZ = value3;
         this.directionAxisValue = value4;
         this.maxY = value5;
         this.boundsValue = value6;
      }

      public boolean canFlag(int boundsLimit, int cxx, int bestD) {
         return boundsLimit >= this.minX && boundsLimit <= this.directionAxisValue && cxx >= this.negativePositiveValue && cxx <= this.maxY && bestD >= this.minZ && bestD <= this.boundsValue;
      }
   }

   public static final class Candidate {
      public final int covered;
      public final boolean rotatedGridFlag;
      public final int spill;
      public final int usefulSpillValue;
      public final boolean rotatedScoreFlag;
      public final int usefulSpillValue2UsefulSpill;
      public final boolean usefulSpillFlag;
      public final Direction opposite;
      public final boolean gridUsefulSpill;
      public final int useful;
      public final int score;
      public final int usefulValue;

      public Axis getAxis() {
         return this.opposite.getAxis();
      }

      public boolean isGridRotated() {
         return this.gridUsefulSpill && this.rotatedGridFlag;
      }

      public BlockPos getUsefulSpill() {
         return new BlockPos(this.usefulValue, this.usefulSpillValue, this.usefulSpillValue2UsefulSpill);
      }

      Candidate(
         int usefulValue, int usefulSpillValue, int usefulSpillValue2, Direction opposite, int useful2, int covered2, int spill2, boolean grid, boolean rotatedGridFlag, int score2, boolean usefulSpillFlag, boolean rotatedScoreFlag
      ) {
         this.usefulValue = usefulValue;
         this.usefulSpillValue = usefulSpillValue;
         this.usefulSpillValue2UsefulSpill = usefulSpillValue2;
         this.opposite = opposite;
         this.useful = useful2;
         this.covered = covered2;
         this.spill = spill2;
         this.gridUsefulSpill = grid;
         this.rotatedGridFlag = rotatedGridFlag;
         this.score = score2;
         this.usefulSpillFlag = usefulSpillFlag;
         this.rotatedScoreFlag = rotatedScoreFlag;
      }

      public boolean canUsefulSpill(int usefulSpillValue, int usefulSpillValue2, int usefulSpillValue3) {
         return this.usefulValue == usefulSpillValue && this.usefulSpillValue == usefulSpillValue2 && this.usefulSpillValue2UsefulSpill == usefulSpillValue3;
      }

      @Override
      public String toString() {
         return "("
            + this.usefulValue
            + ","
            + this.usefulSpillValue
            + ","
            + this.usefulSpillValue2UsefulSpill
            + ") "
            + this.opposite
            + " useful="
            + this.useful
            + (this.usefulSpillFlag ? " BOOTSTRAP covered=" + this.covered : "")
            + " spill="
            + this.spill
            + " grid="
            + (this.gridUsefulSpill ? 1 : 0)
            + (this.rotatedGridFlag ? 1 : 0)
            + (this.rotatedScoreFlag ? " ROTATED" : "")
            + " score="
            + this.score;
      }
   }

   @FunctionalInterface
   public interface CellRank {
      long rank(int rankValue, int cover, int ey2);
   }

   public interface CellWorld {
      boolean canFlag(int cellWorldValue, int cover, int ey2);

      boolean canFlagForInt(int cellWorldValue, int cover, int breaksPattern);

      boolean canFlag3(int cellWorldValue, int cover, int breaksPattern);

      default boolean canFlag4(int cellWorldValue, int j, int k) {
         return true;
      }

      boolean canFlag5(int cellWorldValue, int directionOffsetValue, int ey2);
   }

   public static final class Request {
      public final double costLimit = Double.MAX_VALUE;
      public boolean axisActive;
      public int cellZ;
      public int rankAxisValue;
      public int axisDirectionValue;
      public Axis rankAxisAxis;
      public double axisAmount;
      public int axisValue;
      public boolean requestActive;
      public boolean secondAxisActive;
      public double rankAxisAmount;
      public final long timeLimitMs = -1L;
      public int requestValue;
      public Direction direction;
      public int usefulSpillValue;
      public DirectionOffsetUtilsUseful.CellRank rankAxisCellRank;
      public boolean rankAxisActive;
      public double requestAmount;
      public double targetZ;

      public DirectionOffsetUtilsUseful.Request getAmount(double amount, double amount2, double amount3) {
         this.rankAxisAmount = amount;
         this.requestAmount = amount2;
         this.targetZ = amount3;
         return this;
      }

      public DirectionOffsetUtilsUseful.Request getAxisDirectionHelper(int value, int value2, int value3) {
         this.axisValue = value;
         this.axisDirectionValue = value2;
         this.requestValue = value3;
         return this;
      }

      public DirectionOffsetUtilsUseful.Request getFlag(int value, int value2, int value3) {
         this.rankAxisActive = true;
         this.usefulSpillValue = value;
         this.rankAxisValue = value2;
         this.cellZ = value3;
         return this;
      }
   }
}
