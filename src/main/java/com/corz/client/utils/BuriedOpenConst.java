package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.EspHoleApi;
import com.corz.client.utils.BuriedOpenLoad;
import com.corz.client.utils.LinesQuads;
import com.corz.client.utils.ConstCopperUtils;
import com.corz.client.utils.FacesEdgesUtils;
import com.corz.client.utils.MaxUtils;

public final class BuriedOpenConst {
   public static final int SINGLE_CELL = 1;
   private final byte[][] apiBytes;
   private static final String[] buriedScoreTexts = new String[]{
      "accepted",
      "notOpen",
      "tooSmall",
      "openSky",
      "unresolved",
      "tooLarge",
      "claimed",
      "dungeon",
      "mineshaft",
      "structure",
      "cave",
      "fluid",
      "shape",
      "short",
      "score",
      "buried"
   };
   private int[] buriedOpenTable;
   private double holeScore;
   private int claimStatusCode;
   private final BuriedOpenConst.SegmentStats buriedOpenSegmentStats;
   private EspHoleApi apiEspHoleApi;
   private boolean[] buriedOpenFlags;
   private double claimedAmount;
   private int[] table;
   private int[] cellTypes;
   private int[] cellDepths;
   private int claimedValue;
   private static final int[] claimedTable = new int[]{1, -1, 0, 0, 0, 0};
   private int[] facesEdgesTable;
   private int[] cellDistances;
   private int[] regionMaxHeights;
   private int[] regionExtents;
   private final BuriedOpenLoad cellIndexLookup;
   private final int[] heightSamples;
   private int[] regionSizes;
   private int bottomApi;
   private final int[] sizeHistogram;
   private final int[] apiTable = new int[16];
   private final int[] cachedApiY = new int[16];
   private int[] cellZs;
   private int[] cellXs;
   private int buriedOpenValue;
   private int claimedOriginZ;
   private final int[] cachedApiZ = new int[16];
   private static final int[] NEIGHBOR_DY = new int[]{0, 0, 1, -1, 0, 0};
   private int claimedCount;
   private int[] floodQueue;
   private int[] visitedMarks;
   private int[] backwardRunLength;
   private final long[] heightSampleKeys;
   private final int[] claimedTypeCounts;
   private int claimedOriginX;
   private LinesQuads facesEdges;
   private final boolean[] apiFlags;
   private static final int[] NEIGHBOR_DZ = new int[]{0, 0, 0, 0, 1, -1};
   private int[] regionMinHeights;
   private int cellCapacity;
   private long claimedChunkKey;
   private int[] componentOfCell;
   private boolean[] candidateCells;
   private int facesEdgesCount;
   private int[] forwardRunLength;
   private final boolean[] apiCacheValid;
   private int claimedOriginY;
   private int[] regionMinWidths;
   private int apiValue;
   private int[] sortedCellOrder;

   private int getValue(int buriedOpenValue, int j, int buriedOpenValue3) {
      return Math.abs(buriedOpenValue - this.claimedOriginX) < 512 && Math.abs(j - this.claimedOriginY) < 512 && Math.abs(buriedOpenValue3 - this.claimedOriginZ) < 512
         ? this.cellIndexLookup.getValue(this.getValueClaimedOriginX(buriedOpenValue, j, buriedOpenValue3))
         : -1;
   }

   public int getFlagsTimes(int buriedOpenValue, int buriedOpenValue2) {
      long buriedOpenValue3 = (long)buriedOpenValue << 32 | buriedOpenValue2 & 4294967295L;
      int index = (int)(buriedOpenValue3 * -7046029254386353131L >>> 58);
      if (this.apiFlags[index] && this.heightSampleKeys[index] == buriedOpenValue3) {
         return this.heightSamples[index];
      } else {
         int heightmapSample = this.apiEspHoleApi.getHeightmapSample(buriedOpenValue, buriedOpenValue2);
         this.heightSampleKeys[index] = buriedOpenValue3;
         this.heightSamples[index] = heightmapSample;
         this.apiFlags[index] = true;
         return heightmapSample;
      }
   }

   private void handle(int buriedOpenValue, int buriedOpenLimit, int buriedOpenValue3, int flagsTimes, int constCopperValue) {
      this.cellXs[this.facesEdgesCount] = buriedOpenValue;
      this.facesEdgesTable[this.facesEdgesCount] = buriedOpenLimit;
      this.cellZs[this.facesEdgesCount] = buriedOpenValue3;
      this.cellDepths[this.facesEdgesCount] = flagsTimes - 1 - buriedOpenLimit;
      this.cellIndexLookup.handle(this.getValueClaimedOriginX(buriedOpenValue, buriedOpenLimit, buriedOpenValue3), this.facesEdgesCount);
      this.claimedTypeCounts[ConstCopperUtils.getValueForInt(constCopperValue)]++;
      this.facesEdgesCount++;
   }

   public int getBuriedOpenValue() {
      return this.buriedOpenValue;
   }

   public int getFacesEdgesCount() {
      return this.facesEdgesCount;
   }

   private int getValue(int index, int buriedOpenValue) {
      int buriedOpenValue2 = this.cellTypes[index];
      int buriedOpenCounter = 0;
      int buriedOpenCounter2 = 0;
      this.floodQueue[buriedOpenCounter2++] = index;
      this.componentOfCell[index] = buriedOpenValue;

      while (buriedOpenCounter < buriedOpenCounter2) {
         int index2 = this.floodQueue[buriedOpenCounter++];
         int buriedOpenValue3 = this.cellXs[index2];
         int buriedOpenValue4 = this.facesEdgesTable[index2];
         int buriedOpenValue5 = this.cellZs[index2];

         for (int i = 0; i < 6; i++) {
            int index3 = this.getValue(buriedOpenValue3 + claimedTable[i], buriedOpenValue4 + NEIGHBOR_DY[i], buriedOpenValue5 + NEIGHBOR_DZ[i]);
            if (index3 >= 0 && this.componentOfCell[index3] < 0 && this.cellTypes[index3] == buriedOpenValue2) {
               this.componentOfCell[index3] = buriedOpenValue;
               this.floodQueue[buriedOpenCounter2++] = index3;
            }
         }
      }

      return buriedOpenCounter2;
   }

   public double getAmount() {
      return this.holeScore;
   }

   private int getFlags(int buriedOpenLimit) {
      Arrays.fill(this.candidateCells, 0, buriedOpenLimit, false);

      for (int i = 0; i < this.facesEdgesCount; i++) {
         int index = this.componentOfCell[i];
         if (!this.buriedOpenFlags[index] && !this.candidateCells[index] && this.regionExtents[index] <= 6) {
            int buriedOpenValue = this.cellXs[i];
            int buriedOpenValue2 = this.facesEdgesTable[i];
            int buriedOpenValue3 = this.cellZs[i];

            for (int j = 0; j < 6; j++) {
               int index2 = this.getValue(buriedOpenValue + claimedTable[j], buriedOpenValue2 + NEIGHBOR_DY[j], buriedOpenValue3 + NEIGHBOR_DZ[j]);
               if (index2 >= 0 && this.buriedOpenFlags[this.componentOfCell[index2]]) {
                  this.candidateCells[index] = true;
                  break;
               }
            }
         }
      }

      int flags = 0;

      for (int k = 0; k < buriedOpenLimit; k++) {
         if (this.candidateCells[k]) {
            flags += this.regionExtents[k];
         }
      }

      return flags;
   }

   public LinesQuads getClassFacesEdges() {
      return this.facesEdges;
   }

   private void handleFlags(int value) {
      if (value > this.cellCapacity) {
         this.cellCapacity = value;
         this.cellXs = new int[value];
         this.facesEdgesTable = new int[value];
         this.cellZs = new int[value];
         this.cellDepths = new int[value];
         this.visitedMarks = new int[value];
         this.forwardRunLength = new int[value];
         this.backwardRunLength = new int[value];
         this.buriedOpenTable = new int[value];
         this.cellDistances = new int[value];
         this.cellTypes = new int[value];
         this.componentOfCell = new int[value];
         this.sortedCellOrder = new int[value];
         this.floodQueue = new int[value];
         this.regionExtents = new int[value];
         this.buriedOpenFlags = new boolean[value];
         this.candidateCells = new boolean[value];
      }
   }

   public BuriedOpenConst() {
      this.apiBytes = new byte[16][];
      this.apiCacheValid = new boolean[16];
      this.heightSampleKeys = new long[64];
      this.heightSamples = new int[64];
      this.apiFlags = new boolean[64];
      this.cellIndexLookup = new BuriedOpenLoad(256);
      this.claimedTypeCounts = new int[12];
      this.regionSizes = new int[0];
      this.regionMinWidths = new int[0];
      this.table = new int[0];
      this.regionMinHeights = new int[0];
      this.regionMaxHeights = new int[0];
      this.sizeHistogram = new int[65];
      this.buriedOpenSegmentStats = new BuriedOpenConst.SegmentStats();
      this.handleFlags(256);
   }

   public void handleFlagsApi(EspHoleApi api) {
      this.apiEspHoleApi = api;
      this.bottomApi = api.getBottom();
      this.apiValue = api.getValue();
      Arrays.fill(this.apiCacheValid, false);
      Arrays.fill(this.apiFlags, false);
   }

   private int getValueForInt(int buriedOpenValue, int index) {
      return buriedOpenValue == 1 ? this.cellZs[index] : this.facesEdgesTable[index];
   }

   public double getClaimedAmount() {
      return this.claimedAmount;
   }

   public static String formatBuriedScore(int index) {
      return index >= 0 && index < buriedScoreTexts.length ? buriedScoreTexts[index] : "?";
   }

   private void update() {
      Arrays.fill(this.visitedMarks, 0, this.facesEdgesCount, 0);

      for (int i = 0; i < this.facesEdgesCount; i++) {
         if (this.visitedMarks[i] == 0) {
            int buriedOpenValue = this.cellXs[i];
            int buriedOpenValue2 = this.cellZs[i];
            int buriedOpenCounter = this.facesEdgesTable[i];

            while (this.getValue(buriedOpenValue, buriedOpenCounter - 1, buriedOpenValue2) >= 0) {
               buriedOpenCounter--;
            }

            int buriedOpenCounter2 = 0;

            int buriedOpenCounter3;
            for (buriedOpenCounter3 = buriedOpenCounter; this.getValue(buriedOpenValue, buriedOpenCounter3, buriedOpenValue2) >= 0; buriedOpenCounter3++) {
               buriedOpenCounter2++;
            }

            for (int j = buriedOpenCounter; j < buriedOpenCounter3; j++) {
               this.visitedMarks[this.getValue(buriedOpenValue, j, buriedOpenValue2)] = buriedOpenCounter2;
            }
         }
      }
   }

   public int getValue(int index) {
      return this.cellXs[index];
   }

   private int getValueCellXs(int buriedOpenValue, int index) {
      return buriedOpenValue == 0 ? this.cellXs[index] : (buriedOpenValue == 1 ? this.facesEdgesTable[index] : this.cellZs[index]);
   }

   private int getValue(int buriedOpenValue, int j, int buriedOpenValue3, int buriedOpenValue4, int buriedOpenValue5) {
      int buriedOpenLimit = this.getValue(buriedOpenValue + buriedOpenValue4, j, buriedOpenValue3 + buriedOpenValue5);
      if (buriedOpenLimit >= 0) {
         return buriedOpenLimit;
      } else {
         int buriedOpenLimit2 = this.getValue(buriedOpenValue + buriedOpenValue4, j - 1, buriedOpenValue3 + buriedOpenValue5);
         return buriedOpenLimit2 >= 0 ? buriedOpenLimit2 : this.getValue(buriedOpenValue + buriedOpenValue4, j + 1, buriedOpenValue3 + buriedOpenValue5);
      }
   }

   public int getValue() {
      return this.claimStatusCode;
   }

   private LinesQuads getFacesEdges(int facesEdgesValue, int facesEdgesValue2, int facesEdgesValue3, int facesEdgesLimit) {
      int maxValue = Integer.MAX_VALUE;
      int maxValue3 = Integer.MAX_VALUE;
      int maxValue4 = Integer.MAX_VALUE;
      int minValue = Integer.MIN_VALUE;
      int minValue5 = Integer.MIN_VALUE;
      int minValue6 = Integer.MIN_VALUE;
      int espHoleValue = 0;
      int buriedOpenLimit = 0;
      int facesEdgesLimit7 = 0;
      boolean facesEdgesFlag = true;
      long[] longs = new long[this.facesEdgesCount];
      long facesEdgesTotal = 0L;
      long facesEdgesValue7 = 0L;

      for (int i = 0; i < this.facesEdgesCount; i++) {
         int facesEdgesLimit8 = this.cellXs[i];
         int facesEdgesLimit9 = this.facesEdgesTable[i];
         int facesEdgesLimit10 = this.cellZs[i];
         if (facesEdgesLimit8 < maxValue) {
            maxValue = facesEdgesLimit8;
         }

         if (facesEdgesLimit9 < maxValue3) {
            maxValue3 = facesEdgesLimit9;
         }

         if (facesEdgesLimit10 < maxValue4) {
            maxValue4 = facesEdgesLimit10;
         }

         if (facesEdgesLimit8 > minValue) {
            minValue = facesEdgesLimit8;
         }

         if (facesEdgesLimit9 > minValue5) {
            minValue5 = facesEdgesLimit9;
         }

         if (facesEdgesLimit10 > minValue6) {
            minValue6 = facesEdgesLimit10;
         }

         if (facesEdgesFlag || facesEdgesLimit8 < espHoleValue || facesEdgesLimit8 == espHoleValue && (facesEdgesLimit10 < facesEdgesLimit7 || facesEdgesLimit10 == facesEdgesLimit7 && facesEdgesLimit9 < buriedOpenLimit)) {
            espHoleValue = facesEdgesLimit8;
            buriedOpenLimit = facesEdgesLimit9;
            facesEdgesLimit7 = facesEdgesLimit10;
            facesEdgesFlag = false;
         }

         long time8 = EspHoleApi.getTime(facesEdgesLimit8, facesEdgesLimit9, facesEdgesLimit10);
         longs[i] = time8;
         long facesEdgesValue9 = time8 * -7046029254386353131L;
         long facesEdgesValue10 = facesEdgesValue9 ^ facesEdgesValue9 >>> 31;
         facesEdgesTotal += facesEdgesValue10;
         facesEdgesValue7 ^= facesEdgesValue10 * -4658895280553007687L;
      }

      Arrays.sort(longs);
      long facesEdgesValue11 = facesEdgesTotal * 31L + facesEdgesValue7 + this.facesEdgesCount;
      int facesEdgesValue12 = this.cellXs[this.sortedCellOrder[0]];
      int facesEdgesValue13 = this.facesEdgesTable[this.sortedCellOrder[0]];
      int facesEdgesValue14 = this.cellZs[this.sortedCellOrder[0]];
      if (facesEdgesLimit > 0) {
         int maxValue11 = Integer.MAX_VALUE;
         int minValue15 = Integer.MIN_VALUE;

         for (int j = 0; j < facesEdgesLimit; j++) {
            int valueCellXs = this.getValueCellXs(facesEdgesValue3, this.sortedCellOrder[j]);
            if (valueCellXs < maxValue11) {
               maxValue11 = valueCellXs;
            }

            if (valueCellXs > minValue15) {
               minValue15 = valueCellXs;
            }
         }

         int facesEdgesValue16 = maxValue11 + minValue15 >> 1;
         int maxValue13 = Integer.MAX_VALUE;

         for (int k = 0; k < facesEdgesLimit; k++) {
            int index = this.sortedCellOrder[k];
            if (this.getValueCellXs(facesEdgesValue3, index) == facesEdgesValue16) {
               int facesEdgesValue17 = facesEdgesValue3 == 1 ? this.cellXs[index] + this.cellZs[index] : this.facesEdgesTable[index];
               if (facesEdgesValue17 < maxValue13) {
                  maxValue13 = facesEdgesValue17;
                  facesEdgesValue12 = this.cellXs[index];
                  facesEdgesValue13 = this.facesEdgesTable[index];
                  facesEdgesValue14 = this.cellZs[index];
               }
            }
         }
      }

      long[] ls = new long[8];
      int facesEdgesCounter = 0;

      for (int m = 0; m < this.facesEdgesCount; m++) {
         long time18 = EspHoleApi.getTime(this.cellXs[m] >> 4, this.cellZs[m] >> 4);
         boolean facesEdgesFlag2 = false;

         for (int p = 0; p < facesEdgesCounter; p++) {
            if (ls[p] == time18) {
               facesEdgesFlag2 = true;
               break;
            }
         }

         if (!facesEdgesFlag2) {
            if (facesEdgesCounter == ls.length) {
               ls = Arrays.copyOf(ls, facesEdgesCounter * 2);
            }

            ls[facesEdgesCounter++] = time18;
         }
      }

      int[] ints = new int[this.facesEdgesCount];
      int[] ints2 = new int[this.facesEdgesCount];
      int[] ints3 = new int[this.facesEdgesCount];

      for (int q = 0; q < this.facesEdgesCount; q++) {
         ints[q] = this.cellXs[q] - maxValue;
         ints2[q] = this.facesEdgesTable[q] - maxValue3;
         ints3[q] = this.cellZs[q] - maxValue4;
      }

      int maxValue19 = maxValue;
      int facesEdgesValue20 = maxValue3;
      int facesEdgesValue21 = maxValue4;
      FacesEdgesUtils facesEdges = FacesEdgesUtils.getFacesEdges(
         this.facesEdgesCount, ints, ints2, ints3, (facesEdgesValue22, facesEdgesValue23, facesEdgesValue24) -> this.getValue(facesEdgesValue22 + maxValue19, facesEdgesValue23 + facesEdgesValue20, facesEdgesValue24 + facesEdgesValue21) >= 0
      );
      return new LinesQuads(
         EspHoleApi.getTime(espHoleValue, buriedOpenLimit, facesEdgesLimit7),
         facesEdgesValue11,
         facesEdgesValue,
         maxValue,
         maxValue3,
         maxValue4,
         minValue,
         minValue5,
         minValue6,
         this.facesEdgesCount,
         facesEdgesValue2,
         (float)this.claimedAmount,
         facesEdgesValue12,
         facesEdgesValue13,
         facesEdgesValue14,
         facesEdges.edges,
         facesEdges.faces,
         Arrays.copyOf(ls, facesEdgesCounter),
         longs
      );
   }

   public int getValueFacesEdgesTable(int index) {
      return this.facesEdgesTable[index];
   }

   private int getValueClaimedOriginX(int buriedOpenValue, int claimedLimit, int buriedOpenValue3) {
      return buriedOpenValue - this.claimedOriginX + 512 | claimedLimit - this.claimedOriginY + 512 << 10 | buriedOpenValue3 - this.claimedOriginZ + 512 << 20;
   }

   public int getTimeClaimed(int value, int buriedOpenLimit, int value3, MaxUtils maxUtils, BuriedOpenConst.Claims claims) {
      this.facesEdgesCount = 0;
      this.cellIndexLookup.updateLoadCounter();
      Arrays.fill(this.claimedTypeCounts, 0);
      this.claimedCount = 0;
      this.claimedChunkKey = Long.MIN_VALUE;
      this.facesEdges = null;
      this.claimedAmount = 0.0;
      this.buriedOpenValue = 0;
      this.claimedValue = 0;
      this.holeScore = 0.0;
      this.claimedOriginX = value;
      this.claimedOriginY = buriedOpenLimit;
      this.claimedOriginZ = value3;
      this.handleFlags(maxUtils.maxBoundClaimed);
      int flagsApi = this.getFlagsApi(value, buriedOpenLimit, value3);
      if (ConstCopperUtils.getValue(flagsApi) == 0) {
         this.claimedChunkKey = EspHoleApi.getTime(value >> 4, value3 >> 4);
         return this.claimStatusCode = 4;
      } else if (ConstCopperUtils.getValue(flagsApi) != 1) {
         return this.claimStatusCode = 1;
      } else {
         int flagsTimes = this.getFlagsTimes(value, value3);
         if (flagsTimes == Integer.MIN_VALUE) {
            this.claimedChunkKey = EspHoleApi.getTime(value >> 4, value3 >> 4);
            return this.claimStatusCode = 4;
         } else if (buriedOpenLimit >= flagsTimes) {
            return this.claimStatusCode = 3;
         } else {
            this.handle(value, buriedOpenLimit, value3, flagsTimes, flagsApi);
            int claimedValue = maxUtils.cellLimit;
            int index = 0;

            while (index < this.facesEdgesCount) {
               int claimedValue2 = this.cellXs[index];
               int claimedValue3 = this.facesEdgesTable[index];
               int claimedValue4 = this.cellZs[index];
               index++;

               for (int i = 0; i < 6; i++) {
                  int claimedValue5 = claimedValue2 + claimedTable[i];
                  int claimedLimit = claimedValue3 + NEIGHBOR_DY[i];
                  int claimedValue6 = claimedValue4 + NEIGHBOR_DZ[i];
                  if (claimedLimit >= this.bottomApi) {
                     if (claimedLimit >= this.apiValue) {
                        return this.claimStatusCode = 3;
                     }

                     int constCopperValue = this.getFlagsApi(claimedValue5, claimedLimit, claimedValue6);
                     switch (ConstCopperUtils.getValue(constCopperValue)) {
                        case 0:
                           this.claimedChunkKey = EspHoleApi.getTime(claimedValue5 >> 4, claimedValue6 >> 4);
                           return this.claimStatusCode = 4;
                        case 1:
                        default:
                           if (Math.abs(claimedValue5 - this.claimedOriginX) > claimedValue || Math.abs(claimedLimit - this.claimedOriginY) > claimedValue || Math.abs(claimedValue6 - this.claimedOriginZ) > claimedValue) {
                              return this.claimStatusCode = 5;
                           }

                           if (this.cellIndexLookup.getValue(this.getValueClaimedOriginX(claimedValue5, claimedLimit, claimedValue6)) < 0) {
                              if (claims != null && claims.claimed(claimedValue5, claimedLimit, claimedValue6)) {
                                 return this.claimStatusCode = 6;
                              }

                              int flagsTimes2 = this.getFlagsTimes(claimedValue5, claimedValue6);
                              if (flagsTimes2 == Integer.MIN_VALUE) {
                                 this.claimedChunkKey = EspHoleApi.getTime(claimedValue5 >> 4, claimedValue6 >> 4);
                                 return this.claimStatusCode = 4;
                              }

                              if (claimedLimit >= flagsTimes2) {
                                 return this.claimStatusCode = 3;
                              }

                              if (this.facesEdgesCount >= maxUtils.maxBoundClaimed) {
                                 return this.claimStatusCode = 5;
                              }

                              this.handle(claimedValue5, claimedLimit, claimedValue6, flagsTimes2, constCopperValue);
                           }
                           break;
                        case 2:
                           this.claimedTypeCounts[ConstCopperUtils.getValueForInt(constCopperValue)]++;
                           break;
                        case 3:
                           this.claimedCount++;
                     }
                  }
               }
            }

            return this.claimStatusCode = this.getAmountFlags(maxUtils);
         }
      }
   }

   private static double getAmount(double buriedOpenLimit) {
      return buriedOpenLimit < 0.0 ? 0.0 : (buriedOpenLimit > 1.0 ? 1.0 : buriedOpenLimit);
   }

   private int getAmountFlags(MaxUtils maxUtils) {
      if (this.facesEdgesCount < 4) {
         return 2;
      } else if (this.claimedTypeCounts[2] <= 0 && this.claimedTypeCounts[1] < 2) {
         int buriedOpenValue = this.claimedTypeCounts[4];
         int buriedOpenValue2 = this.claimedTypeCounts[6];
         int buriedOpenValue3 = this.claimedTypeCounts[5];
         if (buriedOpenValue2 < 2 && buriedOpenValue < 2 && (buriedOpenValue3 + buriedOpenValue2 + buriedOpenValue <= 0 || this.claimedTypeCounts[3] <= 0)) {
            if (this.claimedTypeCounts[7] >= 3) {
               return 9;
            } else if (this.claimedCount >= 3) {
               return 11;
            } else if (this.claimedTypeCounts[8] > Math.max(2, this.facesEdgesCount / 8)) {
               return 10;
            } else {
               this.update();
               this.handle(0, this.buriedOpenTable);
               this.handle(2, this.cellDistances);

               for (int i = 0; i < this.facesEdgesCount; i++) {
                  int buriedOpenValue4 = this.buriedOpenTable[i];
                  byte buriedOpenValue5 = 0;
                  if (this.cellDistances[i] > buriedOpenValue4) {
                     buriedOpenValue4 = this.cellDistances[i];
                     buriedOpenValue5 = 2;
                  }

                  if (this.visitedMarks[i] > buriedOpenValue4) {
                     buriedOpenValue5 = 1;
                  }

                  this.cellTypes[i] = buriedOpenValue5;
                  this.componentOfCell[i] = -1;
               }

               int buriedOpenTotal = 0;
               int value = 0;
               int buriedOpenLimit = 0;
               int value2 = 0;
               int buriedOpenTotal2 = 0;
               int buriedOpenTotal3 = 0;
               double buriedOpenTotal4 = 0.0;
               int facesEdgesLimit = 0;
               int buriedOpenValue7 = 0;
               int buriedOpenCounter = 0;

               for (int j = 0; j < this.facesEdgesCount; j++) {
                  if (this.componentOfCell[j] < 0) {
                     int index = buriedOpenCounter++;
                     int buriedOpenFactor = this.getValue(j, index);
                     int buriedOpenValue8 = this.cellTypes[j];
                     BuriedOpenConst.SegmentStats amountFlag = this.getAmountFlag(buriedOpenValue8, buriedOpenFactor);
                     this.regionExtents[index] = buriedOpenFactor;
                     this.buriedOpenFlags[index] = amountFlag.segmentStatsActive;
                     if (amountFlag.segmentStatsActive) {
                        value2++;
                        buriedOpenTotal += buriedOpenFactor;
                        value += amountFlag.segmentSize;
                        buriedOpenTotal4 += amountFlag.segmentFillRatio * buriedOpenFactor;
                        if (buriedOpenValue8 == 1) {
                           buriedOpenTotal2 += buriedOpenFactor;
                        } else {
                           buriedOpenTotal3 += Math.max(0, amountFlag.segmentStatsValue - 1);
                        }

                        if (amountFlag.segmentSize > buriedOpenLimit) {
                           buriedOpenLimit = amountFlag.segmentSize;
                           buriedOpenValue7 = buriedOpenValue8;
                           facesEdgesLimit = buriedOpenFactor;
                           System.arraycopy(this.floodQueue, 0, this.sortedCellOrder, 0, buriedOpenFactor);
                        }
                     }
                  }
               }

               this.buriedOpenValue = value;
               this.claimedValue = value2;
               int buriedOpenValue9 = buriedOpenTotal > 0 ? Math.min(this.getFlags(buriedOpenCounter), buriedOpenTotal / 2) : 0;
               double amount = (double)(buriedOpenTotal + buriedOpenValue9) / this.facesEdgesCount;
               this.holeScore = amount;
               if (!(amount < 0.7) && value2 != 0) {
                  int maxBound = maxUtils.maxBound;
                  if (value >= maxBound && buriedOpenLimit >= Math.max(4, maxBound / 2)) {
                     double buriedOpenFactor2 = buriedOpenTotal4 / Math.max(1, buriedOpenTotal);
                     double buriedOpenValue10 = (double)value / value2;
                     double buriedOpenValue11 = getAmount((buriedOpenValue10 - 2.0) / 4.0);
                     double buriedOpenValue12 = amount * buriedOpenFactor2 * (0.5 + 0.5 * buriedOpenValue11);
                     double min13 = Math.min(1.0, value / (2.5 * maxBound));
                     long buriedOpenTotal5 = 0L;
                     int maxValue = Integer.MAX_VALUE;

                     for (int k = 0; k < this.facesEdgesCount; k++) {
                        buriedOpenTotal5 += Math.max(0, this.cellDepths[k]);
                        if (this.cellDepths[k] < maxValue) {
                           maxValue = this.cellDepths[k];
                        }
                     }

                     int buriedOpenValue14 = this.claimedTypeCounts[9] + this.claimedTypeCounts[10] + this.claimedTypeCounts[11];
                     if (buriedOpenTotal2 == buriedOpenTotal && buriedOpenValue14 == 0 && value < 2 * maxBound && maxValue > 4) {
                        return 15;
                     } else {
                        double min15 = Math.min(1.0, (double)buriedOpenTotal5 / this.facesEdgesCount / 8.0);
                        double min16 = Math.min(1.0, 0.25 * this.claimedTypeCounts[9] + 0.5 * this.claimedTypeCounts[10] + 0.3 * this.claimedTypeCounts[11]);
                        double buriedOpenValue17 = 0.04 * this.claimedTypeCounts[8] + 0.12 * this.claimedCount + 0.15 * this.claimedTypeCounts[7] + 0.06 * buriedOpenTotal3;
                        this.claimedAmount = 0.45 * buriedOpenValue12 + 0.2 * min13 + 0.15 * min15 + 0.2 * min16 - buriedOpenValue17;
                        if (this.claimedAmount < 0.5) {
                           return 14;
                        } else {
                           int facesEdgesValue = buriedOpenTotal2 * 2 >= buriedOpenTotal ? 1 : 0;
                           this.facesEdges = this.getFacesEdges(facesEdgesValue, value, buriedOpenValue7, facesEdgesLimit);
                           return 0;
                        }
                     }
                  } else {
                     return 13;
                  }
               } else {
                  return 12;
               }
            }
         } else {
            return 8;
         }
      } else {
         return 7;
      }
   }

   public long getTime() {
      return this.claimedChunkKey;
   }

   private BuriedOpenConst.SegmentStats getAmountFlag(int buriedOpenValue, int buriedOpenLimit) {
      BuriedOpenConst.SegmentStats buriedOpenSegmentStats164 = this.buriedOpenSegmentStats;
      buriedOpenSegmentStats164.segmentStatsActive = false;
      buriedOpenSegmentStats164.segmentSize = 0;
      buriedOpenSegmentStats164.segmentFillRatio = 0.0;
      buriedOpenSegmentStats164.segmentStatsValue = 0;
      int maxValue = Integer.MAX_VALUE;
      int minValue = Integer.MIN_VALUE;
      int maxValue3 = Integer.MAX_VALUE;
      int minValue3 = Integer.MIN_VALUE;
      int maxValue4 = Integer.MAX_VALUE;
      int minValue4 = Integer.MIN_VALUE;

      for (int i = 0; i < buriedOpenLimit; i++) {
         int buriedOpenValue5 = this.floodQueue[i];
         int valueCellXs = this.getValueCellXs(buriedOpenValue, buriedOpenValue5);
         int valueCellZs = this.getValueCellZs(buriedOpenValue, buriedOpenValue5);
         int valueForInt = this.getValueForInt(buriedOpenValue, buriedOpenValue5);
         if (valueCellXs < maxValue) {
            maxValue = valueCellXs;
         }

         if (valueCellXs > minValue) {
            minValue = valueCellXs;
         }

         if (valueCellZs < maxValue3) {
            maxValue3 = valueCellZs;
         }

         if (valueCellZs > minValue3) {
            minValue3 = valueCellZs;
         }

         if (valueForInt < maxValue4) {
            maxValue4 = valueForInt;
         }

         if (valueForInt > minValue4) {
            minValue4 = valueForInt;
         }
      }

      int value = minValue - maxValue + 1;
      buriedOpenSegmentStats164.segmentSize = value;
      if (value < 3) {
         return buriedOpenSegmentStats164;
      } else {
         int buriedOpenLimit8 = minValue3 - maxValue3 + 1;
         int buriedOpenLimit9 = minValue4 - maxValue4 + 1;
         if (buriedOpenLimit8 > 3) {
            return buriedOpenSegmentStats164;
         } else if (buriedOpenValue == 1 && buriedOpenLimit9 > 3) {
            return buriedOpenSegmentStats164;
         } else {
            if (this.regionSizes.length < value) {
               int buriedOpenSize = Math.max(value, this.regionSizes.length * 2);
               this.regionSizes = new int[buriedOpenSize];
               this.regionMinWidths = new int[buriedOpenSize];
               this.table = new int[buriedOpenSize];
               this.regionMinHeights = new int[buriedOpenSize];
               this.regionMaxHeights = new int[buriedOpenSize];
            }

            for (int j = 0; j < value; j++) {
               this.regionSizes[j] = 0;
               this.regionMinWidths[j] = Integer.MAX_VALUE;
               this.table[j] = Integer.MIN_VALUE;
               this.regionMinHeights[j] = Integer.MAX_VALUE;
               this.regionMaxHeights[j] = Integer.MIN_VALUE;
            }

            for (int k = 0; k < buriedOpenLimit; k++) {
               int buriedOpenValue6 = this.floodQueue[k];
               int index = this.getValueCellXs(buriedOpenValue, buriedOpenValue6) - maxValue;
               int valueCellZs10 = this.getValueCellZs(buriedOpenValue, buriedOpenValue6);
               int valueForInt11 = this.getValueForInt(buriedOpenValue, buriedOpenValue6);
               this.regionSizes[index]++;
               if (valueCellZs10 < this.regionMinWidths[index]) {
                  this.regionMinWidths[index] = valueCellZs10;
               }

               if (valueCellZs10 > this.table[index]) {
                  this.table[index] = valueCellZs10;
               }

               if (valueForInt11 < this.regionMinHeights[index]) {
                  this.regionMinHeights[index] = valueForInt11;
               }

               if (valueForInt11 > this.regionMaxHeights[index]) {
                  this.regionMaxHeights[index] = valueForInt11;
               }
            }

            Arrays.fill(this.sizeHistogram, 0);

            for (int m = 0; m < value; m++) {
               int index2 = this.regionSizes[m];
               if (index2 > 0 && index2 < this.sizeHistogram.length) {
                  this.sizeHistogram[index2]++;
               }
            }

            int index3 = 0;

            for (int p = 1; p < this.sizeHistogram.length; p++) {
               if (this.sizeHistogram[p] > this.sizeHistogram[index3]) {
                  index3 = p;
               }
            }

            if (index3 == 0) {
               return buriedOpenSegmentStats164;
            } else {
               int buriedOpenCounter = 0;
               int buriedOpenFactor12 = 0;
               int buriedOpenLimit13 = 0;
               int minValue7 = Integer.MIN_VALUE;
               int signum8 = 0;
               int value2 = 0;

               for (int q = 0; q < value; q++) {
                  int buriedOpenValue9 = this.regionSizes[q];
                  if (buriedOpenValue9 != 0) {
                     int buriedOpenFactor = this.table[q] - this.regionMinWidths[q] + 1;
                     int buriedOpenFactor2 = this.regionMaxHeights[q] - this.regionMinHeights[q] + 1;
                     if (buriedOpenValue9 == index3 && buriedOpenValue9 == buriedOpenFactor * buriedOpenFactor2) {
                        buriedOpenCounter++;
                        if (buriedOpenFactor12 == 0) {
                           buriedOpenFactor12 = buriedOpenFactor;
                           buriedOpenLimit13 = buriedOpenFactor2;
                        }
                     }

                     if (buriedOpenValue != 1) {
                        int buriedOpenValue10 = this.regionMinHeights[q];
                        if (minValue7 != Integer.MIN_VALUE) {
                           int deltaVar = buriedOpenValue10 - minValue7;
                           if (Math.abs(deltaVar) >= 2) {
                              value2++;
                           }

                           int signum = Integer.signum(deltaVar);
                           if (signum != 0) {
                              if (signum8 != 0 && signum != signum8) {
                                 value2++;
                              }

                              signum8 = signum;
                           }
                        }

                        minValue7 = buriedOpenValue10;
                     }
                  }
               }

               if (buriedOpenFactor12 == 0) {
                  return buriedOpenSegmentStats164;
               } else {
                  if (buriedOpenValue == 1) {
                     if (buriedOpenFactor12 > 3 || buriedOpenLimit13 > 3) {
                        return buriedOpenSegmentStats164;
                     }
                  } else if (buriedOpenFactor12 > 3 || buriedOpenLimit13 > 4) {
                     return buriedOpenSegmentStats164;
                  }

                  buriedOpenSegmentStats164.segmentFillRatio = (double)buriedOpenCounter / value;
                  buriedOpenSegmentStats164.segmentStatsValue = value2;
                  buriedOpenSegmentStats164.segmentStatsActive = buriedOpenSegmentStats164.segmentFillRatio >= 0.6;
                  return buriedOpenSegmentStats164;
               }
            }
         }
      }
   }

   private int getValueCellZs(int buriedOpenValue, int index) {
      return buriedOpenValue == 0 ? this.cellZs[index] : this.cellXs[index];
   }

   public int getFlagsApi(int apiValue, int apiLimit, int apiValue2) {
      if (apiLimit < this.bottomApi) {
         return ConstCopperUtils.DEFAULT_TYPE_CODE;
      } else if (apiLimit >= this.apiValue) {
         return ConstCopperUtils.apiValue;
      } else {
         int sectionHashValue = apiValue >> 4;
         int k = apiLimit >> 4;
         int apiValue5 = apiValue2 >> 4;
         int index = (sectionHashValue * -1640531535 ^ k * -2048144789 ^ apiValue5 * -1028477387) >>> 28;
         if (!this.apiCacheValid[index] || this.apiTable[index] != sectionHashValue || this.cachedApiY[index] != k || this.cachedApiZ[index] != apiValue5) {
            this.apiBytes[index] = this.apiEspHoleApi.getSectionHash(sectionHashValue, k, apiValue5);
            this.apiTable[index] = sectionHashValue;
            this.cachedApiY[index] = k;
            this.cachedApiZ[index] = apiValue5;
            this.apiCacheValid[index] = true;
         }

         byte[] apiBytes = this.apiBytes[index];
         return apiBytes == null ? 0 : apiBytes[(apiLimit & 15) << 8 | (apiValue2 & 15) << 4 | apiValue & 15];
      }
   }

   public int getClaimedValue() {
      return this.claimedValue;
   }

   public int getValueInt(int index) {
      return this.cellZs[index];
   }

   private void handle(int buriedOpenValue, int[] buriedOpenValues) {
      int buriedOpenLimit = Integer.MAX_VALUE;
      int minValue = Integer.MIN_VALUE;

      for (int i = 0; i < this.facesEdgesCount; i++) {
         int buriedOpenLimit2 = buriedOpenValue == 0 ? this.cellXs[i] : this.cellZs[i];
         if (buriedOpenLimit2 < buriedOpenLimit) {
            buriedOpenLimit = buriedOpenLimit2;
         }

         if (buriedOpenLimit2 > minValue) {
            minValue = buriedOpenLimit2;
         }
      }

      int hashRegistryValue = minValue - buriedOpenLimit + 1;
      int[] ints = new int[hashRegistryValue + 1];

      for (int j = 0; j < this.facesEdgesCount; j++) {
         ints[(buriedOpenValue == 0 ? this.cellXs[j] : this.cellZs[j]) - buriedOpenLimit + 1]++;
      }

      for (int k = 1; k <= hashRegistryValue; k++) {
         ints[k] += ints[k - 1];
      }

      int[] copyOf = Arrays.copyOf(ints, hashRegistryValue);
      int index = 0;

      while (index < this.facesEdgesCount) {
         this.sortedCellOrder[copyOf[(buriedOpenValue == 0 ? this.cellXs[index] : this.cellZs[index]) - buriedOpenLimit]++] = index++;
      }

      int buriedOpenValue3 = buriedOpenValue == 0 ? 1 : 0;
      int buriedOpenValue4 = buriedOpenValue == 2 ? 1 : 0;

      for (int m = this.facesEdgesCount - 1; m >= 0; m--) {
         int index2 = this.sortedCellOrder[m];
         int index3 = this.getValue(this.cellXs[index2], this.facesEdgesTable[index2], this.cellZs[index2], buriedOpenValue3, buriedOpenValue4);
         this.forwardRunLength[index2] = index3 >= 0 ? this.forwardRunLength[index3] + 1 : 0;
      }

      for (int p = 0; p < this.facesEdgesCount; p++) {
         int index4 = this.sortedCellOrder[p];
         int index5 = this.getValue(this.cellXs[index4], this.facesEdgesTable[index4], this.cellZs[index4], -buriedOpenValue3, -buriedOpenValue4);
         this.backwardRunLength[index4] = index5 >= 0 ? this.backwardRunLength[index5] + 1 : 0;
      }

      for (int q = 0; q < this.facesEdgesCount; q++) {
         buriedOpenValues[q] = this.forwardRunLength[q] + this.backwardRunLength[q] + 1;
      }
   }

   @FunctionalInterface
   public interface Claims {
      boolean claimed(int claimedValue, int claimedLimit, int claimedValue3);
   }

   private static final class SegmentStats {
      int segmentSize;
      boolean segmentStatsActive;
      double segmentFillRatio;
      int segmentStatsValue;
   }
}
