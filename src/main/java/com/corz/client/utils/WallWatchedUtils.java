package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.HashSet;

public final class WallWatchedUtils {
   private static final int[][] WALL_OFFSETS = new int[][]{{0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
   private static final int[][] FLUID_NEIGHBOR_OFFSETS = new int[][]{{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};

   public static boolean canEscapeToVoid(WallWatchedUtils.PlanGrid planGrid, int wallValue, int wallValue2, int j, int wallValue4) {
      ArrayDeque arrayDeque = new ArrayDeque();
      HashSet hashSet = new HashSet();
      arrayDeque.add(new int[]{wallValue, wallValue2, j});
      hashSet.add(ProofSupportUtils.getTime(wallValue, wallValue2, j));

      while (!arrayDeque.isEmpty()) {
         int[] wallValues = (int[])arrayDeque.poll();

         for (int[] wallOffsetsItem : WALL_OFFSETS) {
            int planGridValue = wallValues[0] + wallOffsetsItem[0];
            int builderAutoValue = wallValues[1] + wallOffsetsItem[1];
            int wallValue7 = wallValues[2] + wallOffsetsItem[2];
            WallWatchedUtils.PlanCell at = planGrid.at(planGridValue, builderAutoValue, wallValue7);
            if (at != WallWatchedUtils.PlanCell.WALL && hashSet.add(ProofSupportUtils.getTime(planGridValue, builderAutoValue, wallValue7))) {
               if (at == WallWatchedUtils.PlanCell.VOID || hashSet.size() > wallValue4) {
                  return true;
               }

               arrayDeque.add(new int[]{planGridValue, builderAutoValue, wallValue7});
            }
         }
      }

      return false;
   }

   public static boolean isSealedByWalls(WallWatchedUtils.PlanGrid planGrid, int wallValue, int builderAutoValue, int j, int wallValue4) {
      int[][] ints = new int[][]{{0, -1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
      ArrayDeque arrayDeque = new ArrayDeque();
      HashSet hashSet = new HashSet();
      arrayDeque.add(new int[]{wallValue, builderAutoValue, j});
      hashSet.add(ProofSupportUtils.getTime(wallValue, builderAutoValue, j));

      while (!arrayDeque.isEmpty()) {
         int[] wallValues = (int[])arrayDeque.poll();

         for (int[] intsItem : ints) {
            int proofSupportValue = wallValues[0] + intsItem[0];
            int wallValue6 = wallValues[1] + intsItem[1];
            int wallValue7 = wallValues[2] + intsItem[2];
            if (hashSet.add(ProofSupportUtils.getTime(proofSupportValue, wallValue6, wallValue7))) {
               WallWatchedUtils.PlanCell at = planGrid.at(proofSupportValue, wallValue6, wallValue7);
               if (at != WallWatchedUtils.PlanCell.WALL) {
                  if (at == WallWatchedUtils.PlanCell.VOID || hashSet.size() > wallValue4) {
                     return false;
                  }

                  arrayDeque.add(new int[]{proofSupportValue, wallValue6, wallValue7});
               }
            }
         }
      }

      return true;
   }

   public static boolean isFluidRegionContained(WallWatchedUtils.LiveGrid liveGrid, WallWatchedUtils.Occupied occupied, int watchedSettledValue, int watchedSettledValue2, int watchedSettledValue3, int watchedSettledValue4) {
      if (occupied.at(watchedSettledValue, watchedSettledValue2, watchedSettledValue3)) {
         return false;
      } else {
         ArrayDeque arrayDeque = new ArrayDeque();
         HashSet hashSet = new HashSet();
         arrayDeque.add(new int[]{watchedSettledValue, watchedSettledValue2, watchedSettledValue3});
         hashSet.add(ProofSupportUtils.getTime(watchedSettledValue, watchedSettledValue2, watchedSettledValue3));

         while (!arrayDeque.isEmpty()) {
            int[] watchedSettledValues = (int[])arrayDeque.poll();

            for (int[] fluidNeighborOffsetsItem : FLUID_NEIGHBOR_OFFSETS) {
               int proofSupportValue = watchedSettledValues[0] + fluidNeighborOffsetsItem[0];
               int builderAutoValue = watchedSettledValues[1] + fluidNeighborOffsetsItem[1];
               int j = watchedSettledValues[2] + fluidNeighborOffsetsItem[2];
               WallWatchedUtils.LiveCell at = liveGrid.at(watchedSettledValues[0], watchedSettledValues[1], watchedSettledValues[2], proofSupportValue, builderAutoValue, j);
               if (at == WallWatchedUtils.LiveCell.WATCHED) {
                  return false;
               }

               if (fluidNeighborOffsetsItem[1] <= 0 && at != WallWatchedUtils.LiveCell.WALL && at != WallWatchedUtils.LiveCell.SETTLED) {
                  if (at == WallWatchedUtils.LiveCell.BLOCKED || at == WallWatchedUtils.LiveCell.SOURCE && fluidNeighborOffsetsItem[1] < 0) {
                     return false;
                  }

                  if (hashSet.add(ProofSupportUtils.getTime(proofSupportValue, builderAutoValue, j))) {
                     if (occupied.at(proofSupportValue, builderAutoValue, j) || hashSet.size() > watchedSettledValue4) {
                        return false;
                     }

                     arrayDeque.add(new int[]{proofSupportValue, builderAutoValue, j});
                  }
               }
            }
         }

         return true;
      }
   }

   public static boolean isFirstTwoSetAndThirdClear(boolean wallWatchedFlag, boolean wallWatchedFlag2, boolean wallWatchedFlag3) {
      return wallWatchedFlag && wallWatchedFlag2 && !wallWatchedFlag3;
   }

   private WallWatchedUtils() {
   }

   public static enum LiveCell {
      WALL,
      SETTLED,
      SOURCE,
      FLOW,
      BLOCKED,
      WATCHED;
   }

   public interface LiveGrid {
      WallWatchedUtils.LiveCell at(int liveGridValue, int liveGridValue2, int liveGridValue3, int proofSupportValue, int builderAutoValue, int j);
   }

   public interface Occupied {
      boolean at(int occupiedValue, int builderAutoValue, int j);
   }

   public static enum PlanCell {
      WALL,
      SOURCE,
      FLOW,
      VOID;
   }

   public interface PlanGrid {
      WallWatchedUtils.PlanCell at(int planGridValue, int builderAutoValue, int j);
   }
}
