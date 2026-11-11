package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public final class WalkableRegionAnalyzer {

   public static WalkableRegionAnalyzer.Terrain withStrictDiagonals(WalkableRegionAnalyzer.Terrain terrain) {
      return new WalkableRegionAnalyzer.Terrain() {
         @Override
         public boolean isThinCollision(int mutableValue, int mutableValue2, int z) {
            return terrain.isThinCollision(mutableValue, mutableValue2, z);
         }

         @Override
         public boolean canStandOn(int mutableValue, int y, int mutableValue6) {
            return terrain.canStandOn(mutableValue, y, mutableValue6);
         }

         @Override
         public boolean isHoneyBlock(int mutableValue, int mutableValue8, int mutableValue9) {
            return terrain.isHoneyBlock(mutableValue, mutableValue8, mutableValue9);
         }

         @Override
         public boolean solid(int solidValue, int candidateY, int candidateZ) {
            return terrain.solid(solidValue, candidateY, candidateZ);
         }

         @Override
         public boolean isStrictDiagonals() {
            return true;
         }
      };
   }

   public static int countReachableCells(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int n2, int n3, int reachabilityThreshold) {
      return getValue(terrain, allowedValue, n2, n3, reachabilityThreshold, new HashSet<>());
   }

   private static long packPosKey(int allowedValue, int builderAutoValue, int j) {
      return ProofSupportUtils.getTime(allowedValue, builderAutoValue, j);
   }

   static boolean hasStandableWithinTwoBelow(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int y, int z) {
      for (int i = 0; i <= 2; i++) {
         if (isWalkableCell(terrain, allowedValue, y - i, z)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isLowerPositionConfined(WalkableRegionAnalyzer.Terrain terrain, int solidValue, int n2, int candidateZ, int allowedValue, int solidLimit, int n3) {
      if (solidLimit >= n2) {
         return false;
      } else {
         int countReachableCells2 = countReachableCells(terrain, solidValue, n2, candidateZ, 40);
         if (countReachableCells2 == 0 && terrain.solid(solidValue, n2 - 1, candidateZ) && !terrain.canStandOn(solidValue, n2 - 1, candidateZ)) {
            countReachableCells2 = countBestNeighborRegion(terrain, solidValue, n2, candidateZ);
         }

         int countReachableCells3 = countReachableCells(terrain, allowedValue, solidLimit, n3, 40);
         if (countReachableCells2 == 0 && countReachableCells3 > 0 && !terrain.solid(solidValue, n2 - 1, candidateZ)) {
            countReachableCells2 = countBestNeighborRegion(terrain, solidValue, n2, candidateZ);
         }

         return countReachableCells2 >= 40 ? countReachableCells3 < 40 : countReachableCells3 < countReachableCells2 && countReachableCells3 <= 12;
      }
   }

   public static WalkableRegionAnalyzer.Result evaluatePocketSealing(
      WalkableRegionAnalyzer.Terrain terrain, int sealsPocketValue, int y, int z, int allowedValue, int candidateY, int candidateZ, boolean sealsPocketFlag
   ) {
      if (!isWalkableCell(terrain, sealsPocketValue, y, z)) {
         return new WalkableRegionAnalyzer.Result(WalkableRegionAnalyzer.Verdict.UNKNOWN, -1, -1);
      } else {
         int sealsPocketLimit = countReachableCells(terrain, sealsPocketValue, y, z, 40);
         int countReachableCells2 = countReachableCells(getHandlerForTerrainInt(terrain, allowedValue, candidateY, candidateZ, sealsPocketFlag), sealsPocketValue, y, z, 40);
         int sealsPocketLimit3 = isWalkableCell(terrain, allowedValue, candidateY, candidateZ) ? 1 : 0;
         return sealsPocketLimit >= 40
            ? new WalkableRegionAnalyzer.Result(countReachableCells2 >= 40 ? WalkableRegionAnalyzer.Verdict.SAFE : WalkableRegionAnalyzer.Verdict.SEALS, sealsPocketLimit, countReachableCells2)
            : new WalkableRegionAnalyzer.Result(
               sealsPocketLimit - countReachableCells2 <= sealsPocketLimit3 && countReachableCells2 > 1 ? WalkableRegionAnalyzer.Verdict.ALREADY_POCKET : WalkableRegionAnalyzer.Verdict.SEALS, sealsPocketLimit, countReachableCells2
            );
      }
   }

   private WalkableRegionAnalyzer() {
   }

   static int countBestNeighborRegion(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int n2, int n3) {
      int allowedBound = 0;
      int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      for (int[] allowedValues : ints) {
         int solidValue = allowedValue + allowedValues[0];
         int z = n3 + allowedValues[1];
         if (isPassable(terrain, solidValue, n2 + 1, z)) {
            for (int i = 0; i <= 2; i++) {
               if (isWalkableCell(terrain, solidValue, n2 - i, z)) {
                  allowedBound = Math.max(allowedBound, countReachableCells(terrain, solidValue, n2 - i, z, 40));
                  break;
               }

               if (!isPassable(terrain, solidValue, n2 - i, z)) {
                  break;
               }
            }
         }
      }

      return allowedBound;
   }

   public static WalkableRegionAnalyzer.Result evaluatePocketSealingStandable(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int y, int z, int allowedValue4, int candidateY, int candidateZ) {
      return evaluatePocketSealing(terrain, allowedValue, y, z, allowedValue4, candidateY, candidateZ, true);
   }

   public static boolean canStandAt(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int y, int z) {
      return isWalkableCell(terrain, allowedValue, y, z);
   }

   public static WalkableRegionAnalyzer.Terrain withBlockOverlay(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int candidateY, int candidateZ) {
      return getHandlerForTerrainInt(terrain, allowedValue, candidateY, candidateZ, true);
   }

   public static int getPillarHeightToEscape(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int y, int z, int allowedLimit, WalkableRegionAnalyzer.PillarPlacementCheck pillarBase) {
      if (!isWalkableCell(terrain, allowedValue, y, z)) {
         return 0;
      } else {
         WalkableRegionAnalyzer.Terrain terrain2 = terrain;

         for (int i = 1; i <= allowedLimit; i++) {
            if (!isPassable(terrain, allowedValue, y + i + 1, z) || !pillarBase.isPlacementAllowed(allowedValue, y + i - 1, z)) {
               return 0;
            }

            terrain2 = withBlockOverlay(terrain2, allowedValue, y + i - 1, z);
            if (countReachableCells(terrain2, allowedValue, y + i, z, 40) >= 40) {
               return i;
            }
         }

         return 0;
      }
   }

   public static boolean doesOverlayShrinkRegion(WalkableRegionAnalyzer.Terrain terrain, WalkableRegionAnalyzer.Terrain terrainAfterRemoval, int allowedValue, int n2, int n3) {
      return countReachableCells(terrainAfterRemoval, allowedValue, n2, n3, 160) < 158 && countReachableCells(terrain, allowedValue, n2, n3, 160) >= 160;
   }

   private static void enqueueIfNew(WalkableRegionAnalyzer.Terrain terrain, Set<Long> set, ArrayDeque<long[]> arrayDeque, int allowedValue, int builderAutoValue, int j) {
      if (set.add(packPosKey(allowedValue, builderAutoValue, j))) {
         arrayDeque.add(new long[]{allowedValue, builderAutoValue, j});
      }
   }

   private static int getValue(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int n2, int n3, int reachabilityThreshold, Set<Long> set) {
      if (!isWalkableCell(terrain, allowedValue, n2, n3)) {
         return 0;
      } else {
         ArrayDeque arrayDeque = new ArrayDeque();
         set.add(packPosKey(allowedValue, n2, n3));
         arrayDeque.add(new long[]{allowedValue, n2, n3});
         int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
         int[][] ints2 = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

         while (!arrayDeque.isEmpty()) {
            if (set.size() >= reachabilityThreshold) {
               return set.size();
            }

            long[] allowedValues = (long[])arrayDeque.poll();
            int solidValue = (int)allowedValues[0];
            int y = (int)allowedValues[1];
            int allowedValue7 = (int)allowedValues[2];

            for (int[] allowedValues2 : ints2) {
               if (isPassable(terrain, solidValue + allowedValues2[0], y, allowedValue7)
                  && isPassable(terrain, solidValue + allowedValues2[0], y + 1, allowedValue7)
                  && isPassable(terrain, solidValue, y, allowedValue7 + allowedValues2[1])
                  && isPassable(terrain, solidValue, y + 1, allowedValue7 + allowedValues2[1])
                  && isWalkableCell(terrain, solidValue + allowedValues2[0], y, allowedValue7 + allowedValues2[1])
                  && (!terrain.isStrictDiagonals() || hasStandableWithinTwoBelow(terrain, solidValue + allowedValues2[0], y, allowedValue7) && hasStandableWithinTwoBelow(terrain, solidValue, y, allowedValue7 + allowedValues2[1]))) {
                  enqueueIfNew(terrain, set, arrayDeque, solidValue + allowedValues2[0], y, allowedValue7 + allowedValues2[1]);
               }
            }

            for (int[] intsItem : ints) {
               int allowedValue8 = solidValue + intsItem[0];
               int z = allowedValue7 + intsItem[1];
               if (isWalkableCell(terrain, allowedValue8, y, z)) {
                  enqueueIfNew(terrain, set, arrayDeque, allowedValue8, y, z);
               } else if (!isPassable(terrain, solidValue, y + 2, allowedValue7)
                  || !isWalkableCell(terrain, allowedValue8, y + 1, z)
                  || terrain.isThinCollision(solidValue, y - 1, allowedValue7) && !terrain.isThinCollision(allowedValue8, y, z)
                  || terrain.isHoneyBlock(solidValue, y - 1, allowedValue7) && !terrain.isThinCollision(allowedValue8, y, z)) {
                  boolean allowedFlag = false;
                  boolean solid = isPassable(terrain, allowedValue8, y + 1, z);

                  for (int i = 1;
                     solid
                        && i <= 3
                        && isPassable(terrain, allowedValue8, y - i + 1, z)
                        && (
                           i != 2
                              || terrain.isThinCollision(solidValue, y - 1, allowedValue7)
                              || !terrain.isThinCollision(allowedValue8, y - i - 1, z)
                              || !isWalkableCell(terrain, allowedValue8, y - i, z)
                        );
                     i++
                  ) {
                     if (isWalkableCell(terrain, allowedValue8, y - i, z)) {
                        if (i <= 2 || !terrain.isThinCollision(allowedValue8, y - i - 1, z)) {
                           enqueueIfNew(terrain, set, arrayDeque, allowedValue8, y - i, z);
                           allowedFlag = true;
                        }
                        break;
                     }

                     if (i == 2 && !terrain.isThinCollision(solidValue, y - 1, allowedValue7)) {
                        break;
                     }
                  }

                  if (!allowedFlag && isPassable(terrain, allowedValue8, y, z) && isPassable(terrain, allowedValue8, y + 1, z) && isPassable(terrain, solidValue, y + 2, allowedValue7)) {
                     boolean allowedFlag2 = false;

                     for (int j = 1; j <= 2; j++) {
                        if (isWalkableCell(terrain, allowedValue8, y - j, z)) {
                           allowedFlag2 = true;
                           break;
                        }
                     }

                     if (allowedFlag2) {
                        int allowedValue10 = solidValue + 2 * intsItem[0];
                        int allowedValue11 = allowedValue7 + 2 * intsItem[1];
                        if (isWalkableCell(terrain, allowedValue10, y, allowedValue11)) {
                           enqueueIfNew(terrain, set, arrayDeque, allowedValue10, y, allowedValue11);
                        } else if (isPassable(terrain, allowedValue10, y, allowedValue11) && isWalkableCell(terrain, allowedValue10, y - 1, allowedValue11)) {
                           enqueueIfNew(terrain, set, arrayDeque, allowedValue10, y - 1, allowedValue11);
                        }
                     }
                  }
               } else {
                  enqueueIfNew(terrain, set, arrayDeque, allowedValue8, y + 1, z);
               }
            }
         }

         return set.size();
      }
   }

   public static boolean isWalkableCell(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int y, int z) {
      return terrain.canStandOn(allowedValue, y - 1, z) && isPassable(terrain, allowedValue, y, z) && isPassable(terrain, allowedValue, y + 1, z);
   }

   static boolean isPassable(WalkableRegionAnalyzer.Terrain terrain, int solidValue, int y, int z) {
      return !terrain.solid(solidValue, y, z);
   }

   public static boolean isLowerRegionEnclosed(
      WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int allowedLimit, int n3, int allowedValue3, int n2, int allowedValue5, WalkableRegionAnalyzer.PillarPlacementCheck pillarBase
   ) {
      if (n2 < allowedLimit && countReachableCells(terrain, allowedValue, allowedLimit, n3, 40) >= 40) {
         HashSet hashSet = new HashSet();
         int allowedLimit2 = getValue(terrain, allowedValue3, n2, allowedValue5, 160, hashSet);
         if (allowedLimit2 >= 40 && allowedLimit2 < 160 && !hashSet.contains(packPosKey(allowedValue, allowedLimit, n3))) {
            for (long proofSupportValue : (Iterable<Long>) (hashSet)) {
               int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
               int allowedValue8 = ProofSupportUtils.getValue(proofSupportValue);
               int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
               if (isPassable(terrain, valueInt, allowedValue8 + 2, valueForLong) && isPassable(terrain, valueInt, allowedValue8 + 3, valueForLong) && pillarBase.isPlacementAllowed(valueInt, allowedValue8, valueForLong)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean canEscapeToOpenArea(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int n2, int n3, WalkableRegionAnalyzer.PillarPlacementCheck pillarBase) {
      HashSet hashSet = new HashSet();
      if (getValue(terrain, allowedValue, n2, n3, 40, hashSet) >= 40) {
         return true;
      } else {
         for (long proofSupportValue : (Iterable<Long>) (hashSet)) {
            int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
            int allowedValue6 = ProofSupportUtils.getValue(proofSupportValue);
            int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
            if (isPassable(terrain, valueInt, allowedValue6 + 2, valueForLong) && isPassable(terrain, valueInt, allowedValue6 + 3, valueForLong) && pillarBase.isPlacementAllowed(valueInt, allowedValue6, valueForLong)) {
               return true;
            }
         }

         return false;
      }
   }

   public static WalkableRegionAnalyzer.Terrain getHandlerForTerrainInt(WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int allowedValue2, int candidateZ, boolean allowedFlag) {
      return new WalkableRegionAnalyzer.Terrain() {
         @Override
         public boolean isHoneyBlock(int mutableValue, int mutableValue2, int z) {
            return (mutableValue != allowedValue || mutableValue2 != allowedValue2 || z != candidateZ) && terrain.isHoneyBlock(mutableValue, mutableValue2, z);
         }

         @Override
         public boolean isThinCollision(int mutableValue, int y, int mutableValue6) {
            return (mutableValue != allowedValue || y != allowedValue2 || mutableValue6 != candidateZ) && terrain.isThinCollision(mutableValue, y, mutableValue6);
         }

         @Override
         public boolean solid(int solidValue, int candidateY, int solidValue3) {
            return solidValue == allowedValue && candidateY == allowedValue2 && solidValue3 == candidateZ || terrain.solid(solidValue, candidateY, solidValue3);
         }

         @Override
         public boolean canStandOn(int mutableValue, int mutableValue8, int mutableValue9) {
            return mutableValue == allowedValue && mutableValue8 == allowedValue2 && mutableValue9 == candidateZ ? allowedFlag : terrain.canStandOn(mutableValue, mutableValue8, mutableValue9);
         }
      };
   }

   public static boolean isRegionEnclosed(
      WalkableRegionAnalyzer.Terrain terrain, int allowedValue, int builderAutoValue, int j, int n2, int allowedLimit, int n3, WalkableRegionAnalyzer.PillarPlacementCheck pillarBase
   ) {
      if (allowedLimit >= builderAutoValue) {
         return false;
      } else {
         HashSet hashSet = new HashSet();
         int allowedValue6 = getValue(terrain, n2, allowedLimit, n3, 160, hashSet);
         if (allowedValue6 != 0 && allowedValue6 < 160 && !hashSet.contains(packPosKey(allowedValue, builderAutoValue, j))) {
            for (long proofSupportValue : (Iterable<Long>) (hashSet)) {
               int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
               int allowedValue9 = ProofSupportUtils.getValue(proofSupportValue);
               int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
               if (isPassable(terrain, valueInt, allowedValue9 + 2, valueForLong) && isPassable(terrain, valueInt, allowedValue9 + 3, valueForLong) && pillarBase.isPlacementAllowed(valueInt, allowedValue9, valueForLong)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public interface PillarPlacementCheck {
      boolean isPlacementAllowed(int allowedValue, int allowedValue2, int z);
   }

   public record Result(WalkableRegionAnalyzer.Verdict verdict, int componentBefore, int componentAfter) {
      public boolean isVerdictUnknown() {
         return this.verdict == WalkableRegionAnalyzer.Verdict.UNKNOWN;
      }

      public boolean isSealsVerdict() {
         return this.verdict == WalkableRegionAnalyzer.Verdict.SEALS;
      }
   }

   public interface Terrain {
      default boolean isStrictDiagonals() {
         return false;
      }

      default boolean isHoneyBlock(int mutableValue, int mutableValue2, int z) {
         return false;
      }

      default boolean canStandOn(int mutableValue, int y, int candidateZ) {
         return this.solid(mutableValue, y, candidateZ);
      }

      default boolean isThinCollision(int mutableValue, int y, int z) {
         return false;
      }

      boolean solid(int solidValue, int candidateY, int candidateZ);
   }

   public static enum Verdict {
      SAFE,
      SEALS,
      ALREADY_POCKET,
      UNKNOWN;
   }
}
