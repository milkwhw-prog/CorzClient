package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.corz.client.utils.Cells;

public final class StandFinder {
   private static final int[] NEIGHBOR_X_OFFSETS = new int[]{1, -1, 0, 0};
   private static final int[] NEIGHBOR_Z_OFFSETS = new int[]{0, 0, 1, -1};

   public static double distanceToBlockCenter(double standFinderValue, double standFinderValue2, double standFinderValue3, int seesValue, int standFinderValue5, int standFinderValue6) {
      double standFinderFactor = seesValue + 0.5 - standFinderValue;
      double standFinderFactor2 = standFinderValue5 + 0.5 - standFinderValue2;
      double standFinderFactor3 = standFinderValue6 + 0.5 - standFinderValue3;
      return Math.sqrt(standFinderFactor * standFinderFactor + standFinderFactor2 * standFinderFactor2 + standFinderFactor3 * standFinderFactor3);
   }

   public static double pitchToBlockCenter(double standFinderValue, double standFinderValue2, double standFinderValue3, int seesValue, int standFinderValue5, int standFinderValue6) {
      double deltaX = seesValue + 0.5 - standFinderValue;
      double standFinderValue8 = standFinderValue5 + 0.5 - standFinderValue2;
      double deltaZ = standFinderValue6 + 0.5 - standFinderValue3;
      return Math.toDegrees(Math.atan2(-standFinderValue8, Math.hypot(deltaX, deltaZ)));
   }

   static boolean hasLineOfSight(Cells cells, double standFinderValue, double standFinderValue2, double standFinderValue3, double standFinderValue4, double standFinderValue5, double standFinderValue6, int standFinderValue7, int standFinderValue8, int standFinderValue9) {
      double standFinderFactor = standFinderValue4 - standFinderValue;
      double standFinderFactor2 = standFinderValue5 - standFinderValue2;
      double standFinderFactor3 = standFinderValue6 - standFinderValue3;
      double distance = Math.sqrt(standFinderFactor * standFinderFactor + standFinderFactor2 * standFinderFactor2 + standFinderFactor3 * standFinderFactor3);
      int standFinderLimit = Math.max(1, (int)Math.ceil(distance / 0.05));
      int minValue = Integer.MIN_VALUE;
      int minValue11 = Integer.MIN_VALUE;
      int minValue12 = Integer.MIN_VALUE;

      for (int i = 1; i <= standFinderLimit; i++) {
         double standFinderFactor4 = (double)i / standFinderLimit;
         int hashPutValue = (int)Math.floor(standFinderValue + standFinderFactor * standFinderFactor4);
         int floor14 = (int)Math.floor(standFinderValue2 + standFinderFactor2 * standFinderFactor4);
         int unpackLongZ = (int)Math.floor(standFinderValue3 + standFinderFactor3 * standFinderFactor4);
         if (hashPutValue != minValue || floor14 != minValue11 || unpackLongZ != minValue12) {
            minValue = hashPutValue;
            minValue11 = floor14;
            minValue12 = unpackLongZ;
            if (hashPutValue == standFinderValue7 && floor14 == standFinderValue8 && unpackLongZ == standFinderValue9) {
               return true;
            }

            if (Cells.hasAnyFlag(cells.getCellFlags(hashPutValue, floor14, unpackLongZ), 1)) {
               return false;
            }
         }
      }

      return false;
   }

   public static boolean hasDeepDropNeighbor(Cells cells, int standFinderValue, int topY, int unpackLongZ) {
      for (int i = 0; i < 4; i++) {
         int hashPutValue = standFinderValue + NEIGHBOR_X_OFFSETS[i];
         int standFinderValue5 = unpackLongZ + NEIGHBOR_Z_OFFSETS[i];
         if (Cells.isFreeOfSolidFlags(cells.getCellFlags(hashPutValue, topY, standFinderValue5)) && Cells.isFreeOfSolidFlags(cells.getCellFlags(hashPutValue, topY + 1, standFinderValue5)) && measureFallDepth(cells, hashPutValue, topY, standFinderValue5, 4) > 3) {
            return true;
         }
      }

      return false;
   }

   public static StandFinder.Result scanStands(Cells cells, int seesValue, int seesValue2, int seesValue3, StandFinder.Sight sight) {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      int seesCounter = 0;
      int seesCounter2 = 0;
      int seesCounter3 = 0;
      int seesCounter4 = 0;
      int ceil4 = (int)Math.ceil(4.3);
      int floor5 = (int)Math.floor(seesValue2 + 0.5 - 4.3 - 1.62);
      int seesLimit = (int)Math.ceil(seesValue2 + 0.5 + 4.3 - 1.62);

      for (int i = floor5; i <= seesLimit; i++) {
         for (int j = seesValue - ceil4; j <= seesValue + ceil4; j++) {
            for (int k = seesValue3 - ceil4; k <= seesValue3 + ceil4; k++) {
               if (j != seesValue || k != seesValue3 || i < seesValue2 - 1 || i > seesValue2 + 1) {
                  double standFinderValue = j + 0.5;
                  double seesValue7 = i + 1.62;
                  double seesValue8 = k + 0.5;
                  if (!(distanceToBlockCenter(standFinderValue, seesValue7, seesValue8, seesValue, seesValue2, seesValue3) > 4.3)) {
                     int hashPut = cells.getCellFlags(j, i - 1, k);
                     int cellFlags = cells.getCellFlags(j, i, k);
                     int cellsValue = cells.getCellFlags(j, i + 1, k);
                     if (!Cells.hasAnyFlag(hashPut | cellFlags | cellsValue, 256)) {
                        if (!Cells.hasAnyFlag(hashPut, 1)) {
                           if (Cells.hasAnyFlag(hashPut, 2) && Cells.isFreeOfSolidFlags(cellFlags) && Cells.isFreeOfSolidFlags(cellsValue)) {
                              seesCounter2++;
                           }
                        } else if (Cells.isFreeOfSolidFlags(cellFlags) && Cells.isFreeOfSolidFlags(cellsValue)) {
                           seesCounter++;
                           if (Cells.isSolidWithoutHazard(hashPut) && !Cells.hasAnyFlag(cellFlags | cellsValue, 10)) {
                              if (Cells.hasAnyFlag(cellFlags | cellsValue, 4)) {
                                 seesCounter3++;
                              } else if (Cells.isNextToLava(cells, j, i, k)) {
                                 seesCounter2++;
                              } else if (sight != null && sight.sees(standFinderValue, seesValue7, seesValue8, seesValue, seesValue2, seesValue3)) {
                                 arrayList.add(new StandFinder.Stand(j, i, k, true, 0, computeStandCost(cells, j, i, k, seesValue, seesValue2, seesValue3)));
                              }
                           } else if (Cells.hasAnyFlag(hashPut | cellFlags | cellsValue, 10)) {
                              seesCounter2++;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      for (int m = 0; m >= -1; m--) {
         for (int p = 0; p < 4; p++) {
            int hashPutValue = seesValue + NEIGHBOR_X_OFFSETS[p];
            int seesValue10 = seesValue2 + m;
            int unpackLongZ = seesValue3 + NEIGHBOR_Z_OFFSETS[p];
            int cellFlags4 = cells.getCellFlags(hashPutValue, seesValue10 - 1, unpackLongZ);
            int cellFlags5 = cells.getCellFlags(hashPutValue, seesValue10, unpackLongZ);
            int cellFlags6 = cells.getCellFlags(hashPutValue, seesValue10 + 1, unpackLongZ);
            if (!Cells.hasAnyFlag(cellFlags4 | cellFlags5 | cellFlags6, 256)) {
               if (Cells.hasAnyFlag(cellFlags5 | cellFlags6, 4)) {
                  seesCounter3++;
               } else if (Cells.isSolidWithoutHazard(cellFlags4) && !Cells.hasAnyFlag(cellFlags5 | cellFlags6, 10)) {
                  boolean seesFlag = Cells.isFreeOfSolidFlags(cellFlags5) || Cells.isSafeSolidBlock(cells, hashPutValue, seesValue10, unpackLongZ);
                  boolean seesFlag2 = Cells.isFreeOfSolidFlags(cellFlags6) || Cells.isSafeSolidBlock(cells, hashPutValue, seesValue10 + 1, unpackLongZ);
                  if (!seesFlag || !seesFlag2) {
                     if (Cells.hasAnyFlag(cellFlags5 | cellFlags6, 96)) {
                        seesCounter4++;
                     }
                  } else if (Cells.isNextToLava(cells, hashPutValue, seesValue10, unpackLongZ)) {
                     seesCounter2++;
                  } else {
                     int seesValue12 = (Cells.isFreeOfSolidFlags(cellFlags5) ? 0 : 1) + (Cells.isFreeOfSolidFlags(cellFlags6) ? 0 : 1);
                     arrayList2.add(new StandFinder.Stand(hashPutValue, seesValue10, unpackLongZ, false, seesValue12, 2.0 + seesValue12 + computeStandCost(cells, hashPutValue, seesValue10, unpackLongZ, seesValue, seesValue2, seesValue3)));
                  }
               } else if (Cells.hasAnyFlag(cellFlags4 | cellFlags5 | cellFlags6, 2)) {
                  seesCounter2++;
               }
            }
         }
      }

      Comparator thenComparingInt = Comparator.comparingDouble(StandFinder.Stand::cost)
         .thenComparingInt(StandFinder.Stand::y)
         .thenComparingInt(StandFinder.Stand::x)
         .thenComparingInt(StandFinder.Stand::z);
      arrayList.sort(thenComparingInt);
      arrayList2.sort(thenComparingInt);
      return new StandFinder.Result(List.copyOf(arrayList), List.copyOf(arrayList2), seesCounter, seesCounter2, seesCounter3, seesCounter4);
   }

   public static StandFinder.Sight getSight(Cells cells) {
      return (sightValue, sightValue2, sightValue3, sightValue4, sightValue5, sightValue6) -> {
         double[][] doubles = new double[][]{
            {sightValue4 + 0.5, sightValue5 + 0.99, sightValue6 + 0.5},
            {sightValue4 + 0.5, sightValue5 + 0.01, sightValue6 + 0.5},
            {sightValue4 + 0.01, sightValue5 + 0.5, sightValue6 + 0.5},
            {sightValue4 + 0.99, sightValue5 + 0.5, sightValue6 + 0.5},
            {sightValue4 + 0.5, sightValue5 + 0.5, sightValue6 + 0.01},
            {sightValue4 + 0.5, sightValue5 + 0.5, sightValue6 + 0.99}
         };

         for (double[] sightValues : doubles) {
            if (hasLineOfSight(cells, sightValue, sightValue2, sightValue3, sightValues[0], sightValues[1], sightValues[2], sightValue4, sightValue5, sightValue6)) {
               return true;
            }
         }

         return false;
      };
   }

   public static int measureFallDepth(Cells cells, int standFinderValue, int i, int unpackLongZ, int standFinderLimit) {
      int standFinderCounter;
      for (standFinderCounter = 0; standFinderCounter < standFinderLimit; standFinderCounter++) {
         int hashPut = cells.getCellFlags(standFinderValue, i - 1 - standFinderCounter, unpackLongZ);
         if (Cells.hasAnyFlag(hashPut, 261)) {
            break;
         }
      }

      return standFinderCounter;
   }

   static double computeStandCost(Cells cells, int standFinderValue, int i, int k, int seesValue, int standFinderValue5, int standFinderValue6) {
      double standFinderValue7 = standFinderValue + 0.5;
      double standFinderValue8 = i + 1.62;
      double standFinderValue9 = k + 0.5;
      double standFinderLimit = distanceToBlockCenter(standFinderValue7, standFinderValue8, standFinderValue9, seesValue, standFinderValue5, standFinderValue6);
      double pitchToBlockCenter2 = pitchToBlockCenter(standFinderValue7, standFinderValue8, standFinderValue9, seesValue, standFinderValue5, standFinderValue6);
      double standFinderTotal = standFinderLimit;
      if (standFinderLimit < 1.5) {
         standFinderTotal = standFinderLimit + (1.5 - standFinderLimit) * 2.0;
      }

      if (standFinderLimit > 3.5) {
         standFinderTotal += (standFinderLimit - 3.5) * 2.0;
      }

      if (pitchToBlockCenter2 < -20.0) {
         standFinderTotal += (-20.0 - pitchToBlockCenter2) / 10.0;
      }

      if (pitchToBlockCenter2 > 60.0) {
         standFinderTotal += (pitchToBlockCenter2 - 60.0) / 10.0;
      }

      if (hasDeepDropNeighbor(cells, standFinderValue, i, k)) {
         standFinderTotal += 2.0;
      }

      return standFinderTotal;
   }

   private StandFinder() {
   }

   public record Result(List<StandFinder.Stand> exposed, List<StandFinder.Stand> dig, int reachable, int lavaFloor, int wet, int hard) {
      public boolean hasCandidates(boolean allowDig) {
         return !this.exposed.isEmpty() || allowDig && !this.dig.isEmpty();
      }

      public List<StandFinder.Stand> getCandidates(boolean allowDig) {
         return this.exposed.isEmpty() && allowDig ? this.dig : this.exposed;
      }

      public StandFinder.Stand getBestCandidate(boolean allowDig) {
         if (!this.exposed.isEmpty()) {
            return this.exposed.get(0);
         } else {
            return allowDig && !this.dig.isEmpty() ? this.dig.get(0) : null;
         }
      }
   }

   public interface Sight {
      boolean sees(double seesValue, double seesValue2, double seesValue3, int seesValue4, int seesValue5, int seesValue6);
   }

   public record Stand(int x, int y, int z, boolean exposed, int digs, double cost) {
   }
}
