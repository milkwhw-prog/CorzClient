package com.corz.client.utils;

import java.util.Arrays;

public final class LandingSpotFinder {
   private static final int[] RING_OFFSETS_X;
   public static final int PROBE_WATER = 1;
   private static final int[] RING_OFFSETS_Z;
   private static final int[] CIRCLE_OFFSETS_X;
   private static final int[] NEIGHBOR_OFFSETS_X = new int[]{0, 1, -1, 0, 0, 1, 1, -1, -1};
   private static final int[] CIRCLE_OFFSETS_Z;
   private static final int[] NEIGHBOR_OFFSETS_Z = new int[]{0, 0, 0, 1, -1, 1, -1, 1, -1};

   private LandingSpotFinder() {
   }

   public static LandingSpotFinder.Spot findLandingSpot(
      LandingSpotFinder.Probe probe, double spotValue, double z, double x, double spotValue4, int spotBound, boolean spotFlag, boolean spotFlag2
   ) {
      if (probe != null && Double.isFinite(spotValue) && Double.isFinite(z) && !(Math.abs(spotValue) > 3.0E7) && !(Math.abs(z) > 3.0E7)) {
         boolean spotFlag3 = Double.isFinite(x) && Double.isFinite(spotValue4);
         int max5 = Math.max(0, Math.min(512, spotBound));
         return new LandingSpotFinder.Search(probe, spotValue, z, spotFlag3 ? x : spotValue, spotFlag3 ? spotValue4 : z, max5, spotFlag, spotFlag2).findBestSpot();
      } else {
         return null;
      }
   }

   public static boolean isFlatWaterArea(LandingSpotFinder.Probe probe, int landingSpotValue, int probePilot, int landingSpotValue3) {
      return isFlatAreaOfType(probe, landingSpotValue, probePilot, landingSpotValue3, 1);
   }

   public static boolean isFlatClearArea(LandingSpotFinder.Probe probe, int landingSpotValue, int probePilot, int landingSpotValue3) {
      return isFlatAreaOfType(probe, landingSpotValue, probePilot, landingSpotValue3, 0);
   }

   public static boolean isNearlyFlatClearArea(LandingSpotFinder.Probe probe, int landingSpotValue, int landingSpotValue2, int j) {
      if (probe == null || landingSpotValue2 == Integer.MIN_VALUE) {
         return false;
      } else if (probe.getBlockClassAt(landingSpotValue, landingSpotValue2, j) != 0) {
         return false;
      } else {
         for (int i = 1; i < 9; i++) {
            int probePilot = probe.getHeightAt(landingSpotValue + NEIGHBOR_OFFSETS_X[i], j + NEIGHBOR_OFFSETS_Z[i]);
            if (probePilot == Integer.MIN_VALUE || probePilot > landingSpotValue2 + 1 || probePilot < landingSpotValue2 - 1) {
               return false;
            }

            if (probe.getBlockClassAt(landingSpotValue + NEIGHBOR_OFFSETS_X[i], probePilot, j + NEIGHBOR_OFFSETS_Z[i]) != 0) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isFlatAreaOfType(LandingSpotFinder.Probe probe, int landingSpotValue, int probePilot, int landingSpotValue3, int blockClassAt) {
      if (probe != null && probePilot != Integer.MIN_VALUE) {
         for (int i = 0; i < 9; i++) {
            if (probe.getHeightAt(landingSpotValue + NEIGHBOR_OFFSETS_X[i], landingSpotValue3 + NEIGHBOR_OFFSETS_Z[i]) != probePilot) {
               return false;
            }
         }

         for (int j = 0; j < 9; j++) {
            if (probe.getBlockClassAt(landingSpotValue + NEIGHBOR_OFFSETS_X[j], probePilot, landingSpotValue3 + NEIGHBOR_OFFSETS_Z[j]) != blockClassAt) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static LandingSpotFinder.Spot getSpot(LandingSpotFinder.Probe probe, double spotValue, double z, double x, double spotValue4, int spotBound, boolean spotFlag) {
      return findLandingSpot(probe, spotValue, z, x, spotValue4, spotBound, spotFlag, false);
   }

   static {
      int[] ints = new int[121];
      int[] ints2 = new int[121];
      int index = 0;

      for (int i = 4; i <= 30; i++) {
         for (int j = -5; j <= 5; j++) {
            for (int k = -5; k <= 5; k++) {
               if (j * j + k * k == i) {
                  ints[index] = j;
                  ints2[index] = k;
                  index++;
               }
            }
         }
      }

      CIRCLE_OFFSETS_X = Arrays.copyOf(ints, index);
      CIRCLE_OFFSETS_Z = Arrays.copyOf(ints2, index);
      int[] values3 = new int[16];
      int[] values4 = new int[16];
      int index2 = 0;

      for (int m = -2; m <= 2; m++) {
         for (int p = -2; p <= 2; p++) {
            if (Math.max(Math.abs(m), Math.abs(p)) == 2) {
               values3[index2] = m;
               values4[index2] = p;
               index2++;
            }
         }
      }

      RING_OFFSETS_X = values3;
      RING_OFFSETS_Z = values4;
   }

   private static final class Cache implements LandingSpotFinder.Probe {
      private final int baseZ;
      private int entryCount;
      private final byte[] blockClassCache;
      private final LandingSpotFinder.Probe probe;
      int probeCallCount;
      private final boolean[] visitedColumns;
      private static final byte UNKNOWN_PROBE = -1;
      private final int[] hashKeys = new int[32768];
      private final int baseX;
      private final int[] groundHeights = new int[32768];

      Cache(LandingSpotFinder.Probe probe2, int value, int value2) {
         this.blockClassCache = new byte[32768];
         this.visitedColumns = new boolean[32768];
         this.probe = probe2;
         this.baseX = value;
         this.baseZ = value2;
      }

      @Override
      public int getBlockClassAt(int probePilotValue, int probePilotValue2, int j) {
         int probe = this.findOrInsertSlot(probePilotValue, j);
         if (probe >= 0 && this.groundHeights[probe] == probePilotValue2) {
            if (this.blockClassCache[probe] == -1) {
               this.probeCallCount++;
               int probePilot = this.probe.getBlockClassAt(probePilotValue, probePilotValue2, j);
               this.blockClassCache[probe] = (byte)(probePilot >= 0 && probePilot <= 5 ? probePilot : 5);
            }

            return this.blockClassCache[probe];
         } else {
            this.probeCallCount++;
            return this.probe.getBlockClassAt(probePilotValue, probePilotValue2, j);
         }
      }

      @Override
      public int getHeightAt(int probePilotValue, int j) {
         int probe = this.findOrInsertSlot(probePilotValue, j);
         if (probe >= 0) {
            return this.groundHeights[probe];
         } else {
            this.probeCallCount++;
            return this.probe.getHeightAt(probePilotValue, j);
         }
      }

      private int findOrInsertSlot(int probeValue, int j) {
         int probeLimit = probeValue - this.baseX + 2048;
         int probeLimit2 = j - this.baseZ + 2048;
         if (probeLimit >= 0 && probeLimit2 >= 0 && probeLimit < 4096 && probeLimit2 < 4096) {
            int probeValue3 = (probeLimit << 12 | probeLimit2) + 1;

            int index;
            int probeValue4;
            for (index = probeValue3 * -1640531527 >>> 17; (probeValue4 = this.hashKeys[index]) != 0; index = index + 1 & 32767) {
               if (probeValue4 == probeValue3) {
                  return index;
               }
            }

            if (this.entryCount >= 24576) {
               return -1;
            } else {
               int probePilot = this.probe.getHeightAt(probeValue, j);
               this.probeCallCount++;
               this.hashKeys[index] = probeValue3;
               this.groundHeights[index] = probePilot;
               this.blockClassCache[index] = -1;
               this.entryCount++;
               return index;
            }
         } else {
            return -1;
         }
      }

      boolean tryMarkVisited(int landingSpotValue, int j) {
         int probe = this.findOrInsertSlot(landingSpotValue, j);
         if (probe < 0) {
            return true;
         } else if (this.visitedColumns[probe]) {
            return false;
         } else {
            this.visitedColumns[probe] = true;
            return true;
         }
      }
   }

   public interface Probe {
      int getHeightAt(int probePilotValue, int j);

      int getBlockClassAt(int probePilotValue, int probePilot, int j);
   }

   private static final class Search {
      private final int searchRadius;
      private final LandingSpotFinder.Cache cache;
      private LandingSpotFinder.Spot spot;
      private int flatNeighborCount;
      private final double targetZ;
      private final boolean allowRoughGround;
      private boolean probeBudgetExceeded;
      private double lastRoughness;
      private final double originX;
      private final double directionZ;
      private final double directionX;
      private final double originZ;
      private boolean goodSpotFound;
      private final double targetX;
      private final boolean allowSoftLanding;

      LandingSpotFinder.Spot findBestSpot() {
         double spotValue = this.directionX == 0.0 && this.directionZ == 0.0 ? 0.0 : StrictMath.atan2(this.directionZ, this.directionX);
         int spotLimit = -1;

         for (int i = 0; this.spot == null || !(i - 2.2 - 9.5 >= this.spot.score()); i = Math.min(this.searchRadius, i + 3)) {
            this.scanRing(i, spotValue);
            if (this.probeBudgetExceeded) {
               break;
            }

            if (spotLimit >= 0) {
               spotLimit++;
            } else if (this.goodSpotFound) {
               spotLimit = 0;
            }

            if (spotLimit >= 2 || i >= this.searchRadius) {
               break;
            }
         }

         return this.spot;
      }

      private boolean isAcceptableBlockClass(int landingSpotValue) {
         return landingSpotValue == 0 || this.allowSoftLanding && landingSpotValue == 1 || this.allowSoftLanding && this.allowRoughGround && landingSpotValue == 3;
      }

      private double getRingRoughness(int landingSpotValue, int landingSpotValue2, int landingSpotValue3, int landingSpotFactor, int landingSpotFactor2, int landingSpotFactor3, int landingSpotValue4, boolean landingSpotFlag, double landingSpotFactor4, double landingSpotFactor5) {
         int landingSpotValue5 = landingSpotFactor2 * landingSpotFactor2;
         int landingSpotLimit = landingSpotFactor3 * landingSpotFactor3;
         int landingSpotCounter = 0;
         int landingSpotValue6 = Math.floorDiv(landingSpotValue - landingSpotFactor3 + landingSpotFactor - 1, landingSpotFactor) * landingSpotFactor;
         int landingSpotValue7 = Math.floorDiv(landingSpotValue3 - landingSpotFactor3 + landingSpotFactor - 1, landingSpotFactor) * landingSpotFactor;
         double landingSpotTotal = 0.0;

         for (int i = landingSpotValue6; i <= landingSpotValue + landingSpotFactor3; i += landingSpotFactor) {
            int landingSpotFactor6 = i - landingSpotValue;

            for (int j = landingSpotValue7; j <= landingSpotValue3 + landingSpotFactor3; j += landingSpotFactor) {
               int landingSpotFactor7 = j - landingSpotValue3;
               int landingSpotLimit2 = landingSpotFactor6 * landingSpotFactor6 + landingSpotFactor7 * landingSpotFactor7;
               if (landingSpotLimit2 >= landingSpotValue5 && landingSpotLimit2 <= landingSpotLimit) {
                  landingSpotCounter++;
                  int probePilot = this.cache.getHeightAt(i, j);
                  if (probePilot == Integer.MIN_VALUE) {
                     landingSpotTotal += 0.5;
                  } else {
                     int landingSpotLimit3 = probePilot - landingSpotValue2 - landingSpotValue4;
                     if (landingSpotLimit3 > 0) {
                        double landingSpotFactor8 = landingSpotFlag ? 0.7 + 0.3 * (landingSpotFactor6 * landingSpotFactor4 + landingSpotFactor7 * landingSpotFactor5) / Math.sqrt(landingSpotLimit2) : 0.7;
                        landingSpotTotal += landingSpotFactor8 * Math.min(landingSpotLimit3, 30);
                     }
                  }
               }
            }
         }

         return landingSpotCounter == 0 ? 0.0 : landingSpotTotal / landingSpotCounter;
      }

      private void evaluateColumn(int spotValue, int spotValue2) {
         if (this.cache.probeCallCount + 400 > 12000) {
            this.probeBudgetExceeded = true;
         } else {
            int probePilot = this.cache.getHeightAt(spotValue, spotValue2);
            if (probePilot != Integer.MIN_VALUE) {
               int blockClassAt = this.cache.getBlockClassAt(spotValue, probePilot, spotValue2);
               if (this.isAcceptableBlockClass(blockClassAt)) {
                  if (this.spot != null) {
                     double spotFactor = spotValue + 0.5 - this.originX;
                     double spotFactor2 = spotValue2 + 0.5 - this.originZ;
                     if (Math.sqrt(spotFactor * spotFactor + spotFactor2 * spotFactor2) - 1.5 - 9.5 >= this.spot.score()) {
                        return;
                     }
                  }

                  double cacheSpot = this.scoreCandidate(spotValue, spotValue2);
                  if (!(this.lastRoughness <= 0.6) && !this.probeBudgetExceeded) {
                     if (blockClassAt == 0 || this.spot == null) {
                        if (Double.isNaN(cacheSpot)) {
                           int spotCounter = 0;

                           for (int i = 1; i <= 4; i++) {
                              if (this.cache.getHeightAt(spotValue + LandingSpotFinder.NEIGHBOR_OFFSETS_X[i], spotValue2 + LandingSpotFinder.NEIGHBOR_OFFSETS_Z[i]) == probePilot) {
                                 spotCounter++;
                              }
                           }

                           if (spotCounter < 2) {
                              return;
                           }
                        }

                        int sx2 = spotValue;
                        int sz2 = spotValue2;

                        for (int j = 1; j < 9 && !this.probeBudgetExceeded; j++) {
                           double scoreCandidate2 = this.scoreCandidate(spotValue + LandingSpotFinder.NEIGHBOR_OFFSETS_X[j], spotValue2 + LandingSpotFinder.NEIGHBOR_OFFSETS_Z[j]);
                           if (!Double.isNaN(scoreCandidate2)) {
                              if (this.lastRoughness <= 0.6) {
                                 return;
                              }

                              if (Double.isNaN(cacheSpot) || scoreCandidate2 < cacheSpot) {
                                 cacheSpot = scoreCandidate2;
                                 sx2 = spotValue + LandingSpotFinder.NEIGHBOR_OFFSETS_X[j];
                                 sz2 = spotValue2 + LandingSpotFinder.NEIGHBOR_OFFSETS_Z[j];
                              }
                           }
                        }

                        if (!Double.isNaN(cacheSpot)) {
                           for (int k = 0; k < 4 && !this.probeBudgetExceeded; k++) {
                              int spotValue3 = sx2;
                              int spotValue4 = sz2;
                              boolean spotFlag = false;

                              for (int m = 1; m < 9 && !this.probeBudgetExceeded; m++) {
                                 double vecHashValue = this.scoreCandidate(sx2 + LandingSpotFinder.NEIGHBOR_OFFSETS_X[m], sz2 + LandingSpotFinder.NEIGHBOR_OFFSETS_Z[m]);
                                 if (!Double.isNaN(vecHashValue) && vecHashValue < cacheSpot) {
                                    cacheSpot = vecHashValue;
                                    spotValue3 = sx2 + LandingSpotFinder.NEIGHBOR_OFFSETS_X[m];
                                    spotValue4 = sz2 + LandingSpotFinder.NEIGHBOR_OFFSETS_Z[m];
                                    spotFlag = this.lastRoughness <= 0.6;
                                 }
                              }

                              if (spotFlag || spotValue3 == sx2 && spotValue4 == sz2) {
                                 return;
                              }

                              sx2 = spotValue3;
                              sz2 = spotValue4;
                           }

                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      Search(LandingSpotFinder.Probe probe, double amount, double hashRegistryValue, double amount3, double amount4, int value, boolean flag, boolean flag2) {
         this.allowRoughGround = flag2;
         this.cache = new LandingSpotFinder.Cache(probe, (int)Math.floor(amount), (int)Math.floor(hashRegistryValue));
         this.originX = amount;
         this.originZ = hashRegistryValue;
         this.targetX = amount3;
         this.targetZ = amount4;
         this.searchRadius = value;
         this.allowSoftLanding = flag;
         double landingSpotFactor = amount3 - amount;
         double landingSpotFactor2 = amount4 - hashRegistryValue;
         double distance = Math.sqrt(landingSpotFactor * landingSpotFactor + landingSpotFactor2 * landingSpotFactor2);
         this.directionX = distance > 1.0 ? landingSpotFactor / distance : 0.0;
         this.directionZ = distance > 1.0 ? landingSpotFactor2 / distance : 0.0;
      }

      private void scanRing(int landingSpotFactor, double landingSpotValue) {
         int landingSpotLimit = landingSpotFactor == 0 ? 1 : Math.max(6, (int)Math.ceil((Math.PI * 2) * landingSpotFactor / 3.5));
         int minValue = Integer.MIN_VALUE;
         int minValue3 = Integer.MIN_VALUE;

         for (int i = 0; i < landingSpotLimit && !this.probeBudgetExceeded; i++) {
            double landingSpotValue4 = landingSpotValue + (Math.PI * 2) * i / landingSpotLimit;
            int spotValue = (int)Math.floor(this.originX + landingSpotFactor * StrictMath.cos(landingSpotValue4));
            int j = (int)Math.floor(this.originZ + landingSpotFactor * StrictMath.sin(landingSpotValue4));
            if (spotValue != minValue || j != minValue3) {
               minValue = spotValue;
               minValue3 = j;
               this.evaluateColumn(spotValue, j);
            }
         }
      }

      private double scoreCandidate(int spotValue, int j) {
         this.lastRoughness = Double.NaN;
         if (this.cache.probeCallCount + 400 > 12000) {
            this.probeBudgetExceeded = true;
            return Double.NaN;
         } else {
            double spotFactor = spotValue + 0.5 - this.originX;
            double spotFactor2 = j + 0.5 - this.originZ;
            double distance = Math.sqrt(spotFactor * spotFactor + spotFactor2 * spotFactor2);
            if (!(distance > this.searchRadius + 0.75) && this.cache.tryMarkVisited(spotValue, j)) {
               int probePilot = this.cache.getHeightAt(spotValue, j);
               if (probePilot == Integer.MIN_VALUE) {
                  return Double.NaN;
               } else {
                  int blockClassAt = this.cache.getBlockClassAt(spotValue, probePilot, j);
                  if (!this.isAcceptableBlockClass(blockClassAt)) {
                     return Double.NaN;
                  } else {
                     boolean spotFlag = blockClassAt == 1;
                     double spotValue3 = distance + (spotFlag ? 400.0 : 0.0) + (blockClassAt == 3 ? 240.0 : 0.0);
                     if (this.spot != null && spotValue3 - 9.5 >= this.spot.score()) {
                        return Double.NaN;
                     } else {
                        boolean spotFlag2 = false;
                        if (!LandingSpotFinder.isFlatAreaOfType(this.cache, spotValue, probePilot, j, blockClassAt)) {
                           if (!this.allowRoughGround || blockClassAt != 0 || !LandingSpotFinder.isNearlyFlatClearArea(this.cache, spotValue, probePilot, j)) {
                              return Double.NaN;
                           }

                           spotFlag2 = true;
                        }

                        double amount = this.getSurroundingRoughness(spotValue, probePilot, j, spotFlag);
                        if (Double.isNaN(amount)) {
                           return Double.NaN;
                        } else {
                           double spotValue4 = distance > 0.5 ? 0.35 * Math.min(distance, 10.0) * (spotFactor * this.directionX + spotFactor2 * this.directionZ) / distance : 0.0;
                           double cacheSpot = spotValue3 + 5.0 * amount - 6.0 * this.flatNeighborCount / 16.0 - spotValue4 + (spotFlag2 ? 25.0 : 0.0);
                           if (this.spot == null || cacheSpot < this.spot.score()) {
                              this.spot = new LandingSpotFinder.Spot(spotValue, probePilot, j, cacheSpot, spotFlag);
                              this.goodSpotFound = !spotFlag && cacheSpot - distance <= 15.0;
                           }

                           this.lastRoughness = amount;
                           return cacheSpot;
                        }
                     }
                  }
               }
            } else {
               return Double.NaN;
            }
         }
      }

      private double getSurroundingRoughness(int landingSpotValue, int landingSpotValue2, int landingSpotValue3, boolean landingSpotFlag) {
         double landingSpotTotal = 0.0;
         int landingSpotCounter = 0;

         for (int i = 0; i < LandingSpotFinder.CIRCLE_OFFSETS_X.length; i++) {
            int probePilot = this.cache.getHeightAt(landingSpotValue + LandingSpotFinder.CIRCLE_OFFSETS_X[i], landingSpotValue3 + LandingSpotFinder.CIRCLE_OFFSETS_Z[i]);
            if (probePilot == Integer.MIN_VALUE) {
               landingSpotTotal += 0.5;
            } else {
               int landingSpotLimit = probePilot - landingSpotValue2;
               if (landingSpotLimit > 3) {
                  if (LandingSpotFinder.CIRCLE_OFFSETS_X[i] * LandingSpotFinder.CIRCLE_OFFSETS_X[i] + LandingSpotFinder.CIRCLE_OFFSETS_Z[i] * LandingSpotFinder.CIRCLE_OFFSETS_Z[i] <= 9) {
                     return Double.NaN;
                  }

                  landingSpotCounter++;
                  landingSpotTotal += 6.0;
               } else {
                  landingSpotTotal += landingSpotLimit == 3 ? 3.0 : (landingSpotLimit == 2 ? 1.2 : (landingSpotLimit == 1 ? 0.4 : (landingSpotLimit < -4 ? 0.25 : 0.0)));
               }
            }
         }

         if (landingSpotCounter * 3 > LandingSpotFinder.CIRCLE_OFFSETS_X.length) {
            return Double.NaN;
         } else {
            double landingSpotCounter2 = 10.0 * landingSpotTotal / LandingSpotFinder.CIRCLE_OFFSETS_X.length;
            int landingSpotValue4 = landingSpotFlag ? 1 : 0;
            this.flatNeighborCount = 0;

            for (int j = 0; j < LandingSpotFinder.RING_OFFSETS_X.length; j++) {
               int probePilotValue = landingSpotValue + LandingSpotFinder.RING_OFFSETS_X[j];
               int landingSpotValue6 = landingSpotValue3 + LandingSpotFinder.RING_OFFSETS_Z[j];
               int heightAt = this.cache.getHeightAt(probePilotValue, landingSpotValue6);
               if (heightAt != Integer.MIN_VALUE) {
                  int blockClassAt = this.cache.getBlockClassAt(probePilotValue, heightAt, landingSpotValue6);
                  if (blockClassAt == 2) {
                     landingSpotCounter2++;
                  } else if (blockClassAt == 4) {
                     landingSpotCounter2++;
                  } else if (heightAt == landingSpotValue2 && blockClassAt == landingSpotValue4) {
                     this.flatNeighborCount++;
                  }
               }
            }

            double landingSpotFactor = this.targetX - (landingSpotValue + 0.5);
            double landingSpotFactor2 = this.targetZ - (landingSpotValue3 + 0.5);
            double distance = Math.sqrt(landingSpotFactor * landingSpotFactor + landingSpotFactor2 * landingSpotFactor2);
            boolean landingSpotFlag2 = distance >= 4.0;
            if (landingSpotFlag2) {
               landingSpotFactor /= distance;
               landingSpotFactor2 /= distance;
            }

            double landingSpotValue7 = landingSpotCounter2 + 2.0 * this.getRingRoughness(landingSpotValue, landingSpotValue2, landingSpotValue3, 2, 6, 14, 6, landingSpotFlag2, landingSpotFactor, landingSpotFactor2);
            return landingSpotValue7 + 1.0 * this.getRingRoughness(landingSpotValue, landingSpotValue2, landingSpotValue3, 4, 15, 28, 14, landingSpotFlag2, landingSpotFactor, landingSpotFactor2);
         }
      }
   }

   public record Spot(int x, int y, int z, double score, boolean water) {
   }
}
