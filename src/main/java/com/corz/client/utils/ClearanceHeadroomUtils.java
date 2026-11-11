package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ClearanceHeadroomUtils {
   public static final int CLEARANCE_HEADROOM_STEP = 1;
   public static final int[][] ledgeBlockedInts = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   private static final int[][] clearanceHeadroomInts = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

   public static int measureHeadroom(ClearanceHeadroomUtils.World world, int clearanceHeadroomValue, int builderAutoValue, int j, int clearanceHeadroomLimit) {
      int clearanceHeadroomCounter;
      for (clearanceHeadroomCounter = 0; builderAutoValue + clearanceHeadroomCounter < clearanceHeadroomLimit; clearanceHeadroomCounter++) {
         int clearanceHeadroomValue4 = builderAutoValue + clearanceHeadroomCounter;
         int clearanceHeadroomValue5 = clearanceHeadroomValue4 + 1;
         int clearanceHeadroomValue6 = clearanceHeadroomValue4 + 2;
         if (world.isWrongBlockAtSchematicCell(clearanceHeadroomValue, clearanceHeadroomValue4, j)
            || world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomValue5, j)
            || world.isWrongBlockAtSchematicCell(clearanceHeadroomValue, clearanceHeadroomValue5, j)
            || world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomValue6, j)
            || world.isWrongBlockAtSchematicCell(clearanceHeadroomValue, clearanceHeadroomValue6, j)
            || world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomValue6 + 1, j)) {
            break;
         }
      }

      return clearanceHeadroomCounter;
   }

   public static boolean canClearHeadroom(ClearanceHeadroomUtils.World world, int clearanceHeadroomValue, int clearanceHeadroomValue2, int valueForLong, double targetsOffset, double i, double feetYOffset, double clearanceHeadroomFactor) {
      double clearanceHeadroomValue7 = i + 1.62;
      double clearanceHeadroomValue8 = i + 1.27;
      double clearanceHeadroomFactor2 = clearanceHeadroomValue + 0.5 - targetsOffset;
      double clearanceHeadroomFactor3 = clearanceHeadroomValue2 + 0.5 - clearanceHeadroomValue7;
      double clearanceHeadroomFactor4 = valueForLong + 0.5 - feetYOffset;
      if (clearanceHeadroomFactor2 * clearanceHeadroomFactor2 + clearanceHeadroomFactor3 * clearanceHeadroomFactor3 + clearanceHeadroomFactor4 * clearanceHeadroomFactor4 > clearanceHeadroomFactor * clearanceHeadroomFactor) {
         return false;
      } else {
         for (int[] clearanceHeadroomValues : clearanceHeadroomInts) {
            int autoBuilderValue = clearanceHeadroomValue + clearanceHeadroomValues[0];
            int clearanceHeadroomValue10 = clearanceHeadroomValue2 + clearanceHeadroomValues[1];
            int clearanceHeadroomValue11 = valueForLong + clearanceHeadroomValues[2];
            if (world.isCollisionReplaceableAt(autoBuilderValue, clearanceHeadroomValue10, clearanceHeadroomValue11)) {
               double clearanceHeadroomValue12;
               double clearanceHeadroomLimit;
               if (clearanceHeadroomValues[0] != 0) {
                  clearanceHeadroomValue12 = clearanceHeadroomValues[0] > 0 ? clearanceHeadroomValue + 1 : clearanceHeadroomValue;
                  clearanceHeadroomLimit = targetsOffset;
               } else if (clearanceHeadroomValues[1] != 0) {
                  clearanceHeadroomValue12 = clearanceHeadroomValues[1] > 0 ? clearanceHeadroomValue2 + 1 : clearanceHeadroomValue2;
                  clearanceHeadroomLimit = clearanceHeadroomValue7;
               } else {
                  clearanceHeadroomValue12 = clearanceHeadroomValues[2] > 0 ? valueForLong + 1 : valueForLong;
                  clearanceHeadroomLimit = feetYOffset;
               }

               boolean clearanceHeadroomFlag = clearanceHeadroomValues[0] + clearanceHeadroomValues[1] + clearanceHeadroomValues[2] > 0;
               if (clearanceHeadroomFlag ? !(clearanceHeadroomLimit >= clearanceHeadroomValue12 - 0.05) : !(clearanceHeadroomLimit <= clearanceHeadroomValue12 + 0.05)) {
                  double clearanceHeadroomValue13 = clearanceHeadroomValue + 0.5 + clearanceHeadroomValues[0] * 0.5;
                  double clearanceHeadroomValue14 = clearanceHeadroomValue2 + 0.5 + clearanceHeadroomValues[1] * 0.5;
                  double clearanceHeadroomValue15 = valueForLong + 0.5 + clearanceHeadroomValues[2] * 0.5;
                  double clearanceHeadroomFactor5 = clearanceHeadroomValue13 - targetsOffset;
                  double deltaVar = clearanceHeadroomValue14 - clearanceHeadroomValue8;
                  double clearanceHeadroomFactor6 = clearanceHeadroomValue15 - feetYOffset;
                  if (!(clearanceHeadroomFactor5 * clearanceHeadroomFactor5 + deltaVar * deltaVar + clearanceHeadroomFactor6 * clearanceHeadroomFactor6 > clearanceHeadroomFactor * clearanceHeadroomFactor)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private ClearanceHeadroomUtils() {
   }

   public static int getMaxHeadroom(int clearanceHeadroomValue) {
      return Math.max(0, clearanceHeadroomValue - 3);
   }

   public static ClearanceHeadroomUtils.Plan planLedgeBlockedRoute(
      ClearanceHeadroomUtils.World world, long ledgeBlockedValue, int clearanceHeadroomLimit, long[] ledgeBlockedValues, Set<Long> set, double clearanceHeadroomFactor
   ) {
      int valueInt = ProofSupportUtils.getValueInt(ledgeBlockedValue);
      int builderAutoValue = ProofSupportUtils.getValue(ledgeBlockedValue);
      int valueForLong = ProofSupportUtils.getValueForLong(ledgeBlockedValue);
      ArrayList arrayList = new ArrayList(ledgeBlockedInts.length);
      ClearanceHeadroomUtils.Candidate candidate = null;
      boolean autoBuilder = world.isCollisionReplaceableAt(valueInt, builderAutoValue - 1, valueForLong);

      for (int[] ledgeBlockedIntsItem : ledgeBlockedInts) {
         int proofSupportValue = valueInt + ledgeBlockedIntsItem[0];
         int j = valueForLong + ledgeBlockedIntsItem[1];
         long time9 = ProofSupportUtils.getTime(proofSupportValue, builderAutoValue - 1, j);
         long time10 = ProofSupportUtils.getTime(proofSupportValue, builderAutoValue, j);
         boolean ledgeBlockedFlag = !world.isBlockedByCollisionOrFluid(proofSupportValue, builderAutoValue - 1, j);
         ClearanceHeadroomUtils.Reject reject = null;
         if (set != null && set.contains(packHeadroomKey(proofSupportValue, j))) {
            reject = ClearanceHeadroomUtils.Reject.COLUMN_ALREADY_USED;
         } else if (ledgeBlockedFlag && world.isWrongBlockAtSchematicCell(proofSupportValue, builderAutoValue - 1, j)) {
            reject = ClearanceHeadroomUtils.Reject.LEDGE_IS_SCHEMATIC;
         } else if (ledgeBlockedFlag && world.isSolidNonReplaceableAt(proofSupportValue, builderAutoValue - 1, j)) {
            reject = ClearanceHeadroomUtils.Reject.LEDGE_BLOCKED;
         } else if (ledgeBlockedFlag && !autoBuilder) {
            reject = ClearanceHeadroomUtils.Reject.LEDGE_UNSUPPORTED;
         } else if (world.isWrongBlockAtSchematicCell(proofSupportValue, builderAutoValue, j)) {
            reject = ClearanceHeadroomUtils.Reject.FEET_IS_SCHEMATIC;
         } else if (world.isSolidNonReplaceableAt(proofSupportValue, builderAutoValue, j)) {
            reject = ClearanceHeadroomUtils.Reject.FEET_BLOCKED;
         } else if (world.isWrongBlockAtSchematicCell(proofSupportValue, builderAutoValue + 1, j)) {
            reject = ClearanceHeadroomUtils.Reject.HEAD_IS_SCHEMATIC;
         } else if (world.isSolidNonReplaceableAt(proofSupportValue, builderAutoValue + 1, j)) {
            reject = ClearanceHeadroomUtils.Reject.HEAD_BLOCKED;
         }

         int ledgeBlockedValue11 = reject == null ? measureHeadroom(world, proofSupportValue, builderAutoValue, j, clearanceHeadroomLimit) : 0;
         int ledgeBlockedLimit = reject == null ? measureHeadroomOnPath(proofSupportValue, builderAutoValue, j, ledgeBlockedValue11, ledgeBlockedValues) : 0;
         int ledgeBlockedValue12 = reject == null && ledgeBlockedLimit > 0 ? measureHeadroomWithPath(world, proofSupportValue, builderAutoValue, j, ledgeBlockedValue11, ledgeBlockedValues, clearanceHeadroomFactor) : 0;
         if (reject == null && ledgeBlockedLimit == 0) {
            reject = ClearanceHeadroomUtils.Reject.NO_WORK_FROM_COLUMN;
         } else if (reject == null && ledgeBlockedValue12 == 0) {
            reject = ClearanceHeadroomUtils.Reject.NO_FACED_WORK;
         }

         ClearanceHeadroomUtils.Candidate totalPriceData = new ClearanceHeadroomUtils.Candidate(ledgeBlockedIntsItem[0], ledgeBlockedIntsItem[1], time9, time10, ledgeBlockedFlag, ledgeBlockedValue11, ledgeBlockedLimit, ledgeBlockedValue12, reject);
         arrayList.add(totalPriceData);
         if (reject == null && (candidate == null || isBetterFacedFree(totalPriceData, candidate))) {
            candidate = totalPriceData;
         }
      }

      return new ClearanceHeadroomUtils.Plan(candidate, arrayList);
   }

   static boolean isBetterFacedFree(ClearanceHeadroomUtils.Candidate candidate, ClearanceHeadroomUtils.Candidate candidate2) {
      if (candidate.faced() != candidate2.faced()) {
         return candidate.faced() > candidate2.faced();
      } else if (candidate.work() != candidate2.work()) {
         return candidate.work() > candidate2.work();
      } else if (candidate.freeRun() != candidate2.freeRun()) {
         return candidate.freeRun() > candidate2.freeRun();
      } else {
         return candidate.needsLedge() != candidate2.needsLedge() ? !candidate.needsLedge() : false;
      }
   }

   public static String formatClearanceHeadroom(ClearanceHeadroomUtils.World world, int clearanceHeadroomValue, int clearanceHeadroomLimit, int j, int upperSearchY) {
      if (clearanceHeadroomLimit >= upperSearchY) {
         return "at_owner_row";
      } else if (world.isWrongBlockAtSchematicCell(clearanceHeadroomValue, clearanceHeadroomLimit, j)) {
         return "feet_is_schematic";
      } else if (world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomLimit + 1, j) || world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomLimit + 2, j)) {
         return "no_headroom";
      } else {
         return world.isSolidNonReplaceableAt(clearanceHeadroomValue, clearanceHeadroomLimit + 3, j) ? "no_jump_clearance" : null;
      }
   }

   public static long packHeadroomKey(int clearanceHeadroomValue, int z) {
      return ProofSupportUtils.getTime(clearanceHeadroomValue, 0, z);
   }

   public static int measureHeadroomLimited(ClearanceHeadroomUtils.World world, int clearanceHeadroomValue, int clearanceHeadroomValue2, int valueForLong, int replaceableAirValue, int j, int builderAutoValue, int clearanceHeadroomBound, double clearanceHeadroomFactor) {
      if (replaceableAirValue == clearanceHeadroomValue && j == valueForLong) {
         return Integer.MIN_VALUE;
      } else if (!world.isSolidNonReplaceableAt(replaceableAirValue, builderAutoValue, j) && !world.isSolidNonReplaceableAt(replaceableAirValue, builderAutoValue + 1, j)) {
         int measureHeadroom8 = measureHeadroom(world, replaceableAirValue, builderAutoValue, j, clearanceHeadroomBound);

         for (int i = builderAutoValue; i <= Math.min(clearanceHeadroomBound, builderAutoValue + measureHeadroom8); i++) {
            if (isWithinHeadroomBounds(replaceableAirValue, i, j, clearanceHeadroomValue, clearanceHeadroomValue2, valueForLong) && canClearHeadroom(world, clearanceHeadroomValue, clearanceHeadroomValue2, valueForLong, replaceableAirValue + 0.5, i, j + 0.5, clearanceHeadroomFactor)) {
               return i;
            }
         }

         return Integer.MIN_VALUE;
      } else {
         return Integer.MIN_VALUE;
      }
   }

   public static int measureHeadroomWithPath(ClearanceHeadroomUtils.World world, int clearanceHeadroomValue, int builderAutoValue, int j, int clearanceHeadroomLimit, long[] clearanceHeadroomValues, double clearanceHeadroomFactor) {
      int clearanceHeadroomBound = 0;

      for (int i = 0; i <= clearanceHeadroomLimit; i++) {
         double targetsOffset = clearanceHeadroomValue + 0.5;
         double feetYOffset = j + 0.5;
         int clearanceHeadroomCounter = 0;

         for (long clearanceHeadroomValuesItem : clearanceHeadroomValues) {
            int valueInt = ProofSupportUtils.getValueInt(clearanceHeadroomValuesItem);
            int clearanceHeadroomValue7 = ProofSupportUtils.getValue(clearanceHeadroomValuesItem);
            int valueForLong = ProofSupportUtils.getValueForLong(clearanceHeadroomValuesItem);
            if (isWithinHeadroomBoundsStrict(clearanceHeadroomValue, builderAutoValue + i, j, valueInt, clearanceHeadroomValue7, valueForLong, i < clearanceHeadroomLimit) && canClearHeadroom(world, valueInt, clearanceHeadroomValue7, valueForLong, targetsOffset, builderAutoValue + i, feetYOffset, clearanceHeadroomFactor)) {
               clearanceHeadroomCounter++;
            }
         }

         clearanceHeadroomBound = Math.max(clearanceHeadroomBound, clearanceHeadroomCounter);
      }

      return clearanceHeadroomBound;
   }

   public static boolean isWithinHeadroomBounds(int clearanceHeadroomValue, int clearanceHeadroomLimit, int j, int valueInt, int clearanceHeadroomValue5, int valueForLong) {
      return isWithinHeadroomBoundsStrict(clearanceHeadroomValue, clearanceHeadroomLimit, j, valueInt, clearanceHeadroomValue5, valueForLong, true);
   }

   public static boolean isWithinHeadroomBoundsStrict(int clearanceHeadroomValue, int clearanceHeadroomLimit, int j, int valueInt, int clearanceHeadroomLimit2, int valueForLong, boolean clearanceHeadroomFlag) {
      if (Math.abs(valueInt - clearanceHeadroomValue) > 3 || Math.abs(valueForLong - j) > 3) {
         return false;
      } else if (clearanceHeadroomLimit2 < clearanceHeadroomLimit - 1 || clearanceHeadroomLimit2 > clearanceHeadroomLimit + 3) {
         return false;
      } else if (valueInt != clearanceHeadroomValue || valueForLong != j) {
         return true;
      } else {
         return clearanceHeadroomLimit2 != clearanceHeadroomLimit && clearanceHeadroomLimit2 != clearanceHeadroomLimit + 1 ? clearanceHeadroomLimit2 < clearanceHeadroomLimit || !clearanceHeadroomFlag : false;
      }
   }

   public static int measureHeadroomOnPath(int clearanceHeadroomValue, int builderAutoValue, int j, int clearanceHeadroomLimit, long[] clearanceHeadroomValues) {
      int clearanceHeadroomBound = 0;

      for (int i = 0; i <= clearanceHeadroomLimit; i++) {
         int clearanceHeadroomCounter = 0;

         for (long clearanceHeadroomValuesItem : clearanceHeadroomValues) {
            if (isWithinHeadroomBoundsStrict(
               clearanceHeadroomValue, builderAutoValue + i, j, ProofSupportUtils.getValueInt(clearanceHeadroomValuesItem), ProofSupportUtils.getValue(clearanceHeadroomValuesItem), ProofSupportUtils.getValueForLong(clearanceHeadroomValuesItem), i < clearanceHeadroomLimit
            )) {
               clearanceHeadroomCounter++;
            }
         }

         clearanceHeadroomBound = Math.max(clearanceHeadroomBound, clearanceHeadroomCounter);
      }

      return clearanceHeadroomBound;
   }

   public static boolean canClearanceRaise(String clearanceRaiseText) {
      return "feet_is_schematic".equals(clearanceRaiseText)
         || "no_headroom".equals(clearanceRaiseText)
         || "no_jump_clearance".equals(clearanceRaiseText)
         || "column_banned".equals(clearanceRaiseText)
         || "raise_did_not_rise".equals(clearanceRaiseText);
   }

   public static boolean exceedsHeadroomThreshold(int clearanceHeadroomValue, double d4) {
      return getMaxHeadroom(clearanceHeadroomValue) == 0 ? true : clearanceHeadroomValue <= 8 && d4 - getMaxHeadroom(clearanceHeadroomValue) >= 8.0;
   }

   public record Candidate(int dx, int dz, long ledge, long feet, boolean needsLedge, int freeRun, int work, int faced, ClearanceHeadroomUtils.Reject reject) {
      public int getFeetPos() {
         return ProofSupportUtils.getValueForLong(this.feet);
      }

      public boolean isReject() {
         return this.reject == null;
      }

      public int getFeetCellKey() {
         return ProofSupportUtils.getValueInt(this.feet);
      }
   }

   public record Plan(ClearanceHeadroomUtils.Candidate chosen, List<ClearanceHeadroomUtils.Candidate> candidates) {
      public boolean isChosen() {
         return this.chosen != null;
      }
   }

   public static enum Reject {
      COLUMN_ALREADY_USED,
      LEDGE_IS_SCHEMATIC,
      LEDGE_BLOCKED,
      LEDGE_UNSUPPORTED,
      FEET_IS_SCHEMATIC,
      FEET_BLOCKED,
      HEAD_IS_SCHEMATIC,
      HEAD_BLOCKED,
      NO_WORK_FROM_COLUMN,
      NO_FACED_WORK;
   }

   public interface World {
      default boolean isBlockedByCollisionOrFluid(int fluidValue, int fluidValue2, int j) {
         return this.isCollisionReplaceableAt(fluidValue, fluidValue2, j);
      }

      boolean isWrongBlockAtSchematicCell(int blockAutoValue, int builderAutoValue, int j);

      boolean isCollisionReplaceableAt(int autoBuilderValue, int autoBuilderValue2, int valueForLong);

      boolean isSolidNonReplaceableAt(int replaceableAirValue, int builderAutoValue, int j);
   }
}
