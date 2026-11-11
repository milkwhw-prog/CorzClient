package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationCraftedType;

public final class ExcavationScanner {

   private static void enqueueIfUnvisited(boolean[][] flags, boolean[][] booleans, ArrayDeque<int[]> arrayDeque, int index, int index2) {
      if (index >= 0 && index <= 15 && index2 >= 0 && index2 <= 15 && flags[index2][index] && !booleans[index2][index]) {
         booleans[index2][index] = true;
         arrayDeque.add(new int[]{index, index2});
      }
   }

   private static boolean isColumnMostlyFlagged(boolean[][] flags, int index, int excavationConstValue, int excavationConstLimit) {
      int excavationConstCounter = 0;

      for (int i = excavationConstValue; i < excavationConstLimit; i++) {
         if (flags[i][index]) {
            excavationConstCounter++;
         }
      }

      return excavationConstCounter * 10 >= (excavationConstLimit - excavationConstValue) * 8;
   }

   private static void addBlobExcavations(ExcavationScanner.SectionMask sectionMask, boolean[][] flags, int excavationValue, int excavationConstValue, int excavationValue3, int startZ, List<CraftedExcavation> list) {
      boolean[][] booleans = new boolean[16][16];
      ArrayDeque arrayDeque = new ArrayDeque();
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            if (flags[i][j] && !booleans[i][j]) {
               arrayList.clear();
               booleans[i][j] = true;
               arrayDeque.add(new int[]{j, i});
               int excavationLimit = j;
               int excavationValue5 = j;
               int excavationLimit2 = i;
               int excavationValue6 = i;

               while (!arrayDeque.isEmpty()) {
                  int[] excavationValues = (int[])arrayDeque.poll();
                  arrayList.add(excavationValues);
                  int excavationLimit3 = excavationValues[0];
                  int excavationLimit4 = excavationValues[1];
                  if (excavationLimit3 < excavationLimit) {
                     excavationLimit = excavationLimit3;
                  }

                  if (excavationLimit3 > excavationValue5) {
                     excavationValue5 = excavationLimit3;
                  }

                  if (excavationLimit4 < excavationLimit2) {
                     excavationLimit2 = excavationLimit4;
                  }

                  if (excavationLimit4 > excavationValue6) {
                     excavationValue6 = excavationLimit4;
                  }

                  enqueueIfUnvisited(flags, booleans, arrayDeque, excavationLimit3 + 1, excavationLimit4);
                  enqueueIfUnvisited(flags, booleans, arrayDeque, excavationLimit3 - 1, excavationLimit4);
                  enqueueIfUnvisited(flags, booleans, arrayDeque, excavationLimit3, excavationLimit4 + 1);
                  enqueueIfUnvisited(flags, booleans, arrayDeque, excavationLimit3, excavationLimit4 - 1);
               }

               int excavationLimit5 = excavationValue5 - excavationLimit + 1;
               int excavationLimit6 = excavationValue6 - excavationLimit2 + 1;
               int varSize = arrayList.size();
               boolean excavationFlag = varSize >= 12 && excavationLimit5 >= 4 && excavationLimit6 >= 4 && varSize >= 0.8 * excavationLimit5 * excavationLimit6;
               boolean excavationFlag2 = varSize >= 9 && excavationLimit5 >= 3 && excavationLimit6 >= 3 && varSize >= 0.85 * excavationLimit5 * excavationLimit6;
               if ((excavationFlag || excavationFlag2) && isBlobMostlyEnclosedAbove(sectionMask, arrayList, excavationValue) && isBlobBorderMostlyMasked(sectionMask, flags, arrayList, excavationValue)) {
                  for (int[] excavationValues2 : (Iterable<int[]>) (arrayList)) {
                     if (((excavationConstValue + excavationValues2[0]) % 3 + 3) % 3 == 0 && ((startZ + excavationValues2[1]) % 3 + 3) % 3 == 0) {
                        list.add(CraftedExcavation.createTypedNoGroup(excavationConstValue + excavationValues2[0], excavationValue3 + excavationValue, startZ + excavationValues2[1], ExcavationCraftedType.EXCAVATION, 1, 139, 0));
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean hasTwoStackedMaskBits(ExcavationScanner.SectionMask sectionMask, int excavationConstValue, int excavationValue, int q) {
      return sectionMask.isMaskBitSet(excavationConstValue, excavationValue, q) && sectionMask.isMaskBitSet(excavationConstValue, excavationValue + 1, q);
   }

   private static void addDiagonalStairExcavations(boolean[][][] flags, int excavationValue, int excavationValue2, int startZ, List<CraftedExcavation> list) {
      int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      for (int i = 1; i <= 14; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               if (flags[i][j][k]) {
                  for (int[] excavationValues : ints) {
                     int index = k - excavationValues[0];
                     int index2 = j - excavationValues[1];
                     if (i + 1 > 14 || index < 0 || index >= 16 || index2 < 0 || index2 >= 16 || !flags[i + 1][index2][index]) {
                        int excavationLimit = 1;
                        int excavationValue4 = k;
                        int excavationValue5 = i;
                        int excavationValue6 = j;

                        while (true) {
                           int index3 = excavationValue4 + excavationValues[0];
                           int index4 = excavationValue5 - 1;
                           int index5 = excavationValue6 + excavationValues[1];
                           if (index4 < 0 || index3 < 0 || index3 > 15 || index5 < 0 || index5 > 15 || !flags[index4][index5][index3]) {
                              if (excavationLimit >= 5) {
                                 int excavationTotal = k;
                                 int excavationCounter = i;
                                 int excavationTotal2 = j;

                                 for (int m = 0; m < excavationLimit; m++) {
                                    if (m % 2 == 0) {
                                       list.add(
                                          CraftedExcavation.createTypedNoGroup(excavationValue + excavationTotal, excavationValue2 + excavationCounter, startZ + excavationTotal2, ExcavationCraftedType.EXCAVATION, 1, 147, 0)
                                       );
                                    }

                                    excavationTotal += excavationValues[0];
                                    excavationCounter--;
                                    excavationTotal2 += excavationValues[1];
                                 }
                              }
                              break;
                           }

                           excavationLimit++;
                           excavationValue4 = index3;
                           excavationValue5 = index4;
                           excavationValue6 = index5;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private ExcavationScanner() {
   }

   public static List<CraftedExcavation> findExcavations(ExcavationScanner.SectionMask sectionMask, int excavationConstValue, int excavationConstValue2, int startZ, int[] excavationConstValues) {
      ArrayList arrayList = new ArrayList();
      if (sectionMask != null && sectionMask.hasBitsetBits()) {
         boolean[][][] booleans = new boolean[16][16][16];
         boolean excavationConstFlag = false;

         for (int i = 0; i <= 14; i++) {
            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 16; k++) {
                  if (sectionMask.isBitsetBitSet(k, i, j)
                     && sectionMask.isBitsetBitSet(k, i + 1, j)
                     && sectionMask.isBitSetOrBelowLayer(k, i, j)
                     && excavationConstValue2 + i <= excavationConstValues[j * 16 + k] - 2) {
                     booleans[i][j][k] = true;
                     excavationConstFlag = true;
                  }
               }
            }
         }

         if (excavationConstFlag) {
            for (int p = 0; p <= 14; p++) {
               addBlobExcavations(sectionMask, booleans[p], p, excavationConstValue, excavationConstValue2, startZ, arrayList);
               addTunnelExcavations(sectionMask, booleans[p], p, excavationConstValue, excavationConstValue2, startZ, arrayList);
            }

            addDiagonalStairExcavations(booleans, excavationConstValue, excavationConstValue2, startZ, arrayList);
         }

         addVerticalShaftExcavations(sectionMask, excavationConstValue, excavationConstValue2, startZ, excavationConstValues, arrayList);
         return arrayList;
      } else {
         return arrayList;
      }
   }

   private static void addVerticalShaftExcavations(ExcavationScanner.SectionMask sectionMask, int excavationValue, int excavationValue2, int startZ, int[] excavationValues, List<CraftedExcavation> list) {
      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int excavationCounter = 0;

            for (int k = 0; k <= 16; k++) {
               boolean excavationFlag = k < 16
                  && sectionMask.isBitsetBitSet(j, k, i)
                  && sectionMask.isMaskBitSet(j + 1, k, i)
                  && sectionMask.isMaskBitSet(j - 1, k, i)
                  && sectionMask.isMaskBitSet(j, k, i + 1)
                  && sectionMask.isMaskBitSet(j, k, i - 1)
                  && excavationValue2 + k <= excavationValues[i * 16 + j] - 2;
               if (excavationFlag) {
                  excavationCounter++;
               } else {
                  if (excavationCounter >= 8) {
                     for (int p = k - excavationCounter; p < k; p++) {
                        if (((excavationValue2 + p) % 4 + 4) % 4 == 0) {
                           list.add(CraftedExcavation.createTypedNoGroup(excavationValue + j, excavationValue2 + p, startZ + i, ExcavationCraftedType.EXCAVATION, 1, 141, 0));
                        }
                     }
                  }

                  excavationCounter = 0;
               }
            }
         }
      }
   }

   private static boolean isRowMostlyFlagged(boolean[] excavationConstFlags, int excavationConstValue, int excavationConstLimit) {
      int excavationConstCounter = 0;

      for (int i = excavationConstValue; i < excavationConstLimit; i++) {
         if (excavationConstFlags[i]) {
            excavationConstCounter++;
         }
      }

      return excavationConstCounter * 10 >= (excavationConstLimit - excavationConstValue) * 8;
   }

   private static boolean isBlobMostlyEnclosedAbove(ExcavationScanner.SectionMask sectionMask, List<int[]> list, int excavationConstValue) {
      int[] ints = new int[7];
      int excavationConstCounter = 0;

      for (int[] excavationConstValues : list) {
         int index = 0;

         while (index < 6 && excavationConstValue + index < 16 && sectionMask.isBitsetBitSet(excavationConstValues[0], excavationConstValue + index, excavationConstValues[1])) {
            index++;
         }

         boolean excavationConstFlag = excavationConstValue + index >= 16 && index < 6;
         if (!excavationConstFlag && index < 6) {
            ints[index]++;
            excavationConstCounter++;
         }
      }

      if (excavationConstCounter * 2 < list.size()) {
         return true;
      } else {
         int excavationConstBound = 0;

         for (int i = 2; i <= 6; i++) {
            excavationConstBound = Math.max(excavationConstBound, ints[i]);
         }

         return excavationConstBound >= 0.6 * excavationConstCounter;
      }
   }

   private static void addTunnelExcavations(ExcavationScanner.SectionMask sectionMask, boolean[][] flags, int excavationValue, int excavationValue2, int excavationValue3, int startZ, List<CraftedExcavation> list) {
      boolean[][] booleans = new boolean[16][16];

      for (int i = 0; i < 16; i++) {
         int index = 0;

         while (index < 16) {
            if (flags[i][index] && !booleans[i][index]) {
               int index5 = index;

               while (index < 16 && flags[i][index]) {
                  index++;
               }

               int deltaVar = index - index5;
               if (deltaVar >= 8) {
                  boolean excavationFlag = i + 1 < 16 && isRowMostlyFlagged(flags[i + 1], index5, index);
                  int excavationCounter = 0;
                  int excavationCounter2 = 0;
                  int excavationCounter3 = 0;

                  for (int j = index5; j < index; j++) {
                     boolean twoStackedMaskBits2 = hasTwoStackedMaskBits(sectionMask, j, excavationValue, i - 1);
                     boolean excavationFlag3 = excavationFlag ? hasTwoStackedMaskBits(sectionMask, j, excavationValue, i + 2) : hasTwoStackedMaskBits(sectionMask, j, excavationValue, i + 1);
                     if (twoStackedMaskBits2 && excavationFlag3) {
                        excavationCounter++;
                     }

                     if (excavationValue + 2 < 16) {
                        excavationCounter2++;
                        if (sectionMask.isMaskBitSet(j, excavationValue + 2, i)) {
                           excavationCounter3++;
                        }
                     }
                  }

                  if (meetsTunnelCoverage(deltaVar, excavationCounter, excavationCounter2, excavationCounter3)) {
                     for (int k = index5; k < index; k++) {
                        booleans[i][k] = true;
                        if (excavationFlag) {
                           booleans[i + 1][k] = true;
                        }

                        if (((excavationValue2 + k) % 4 + 4) % 4 == 0) {
                           list.add(CraftedExcavation.createTypedNoGroup(excavationValue2 + k, excavationValue3 + excavationValue, startZ + i, ExcavationCraftedType.EXCAVATION, 1, 140, 0));
                        }
                     }
                  }
               }
            } else {
               index++;
            }
         }
      }

      for (int p = 0; p < 16; p++) {
         int excavationConstLimit = 0;

         while (excavationConstLimit < 16) {
            if (flags[excavationConstLimit][p] && !booleans[excavationConstLimit][p]) {
               int excavationConstValue = excavationConstLimit;

               while (excavationConstLimit < 16 && flags[excavationConstLimit][p]) {
                  excavationConstLimit++;
               }

               int deltaVar2 = excavationConstLimit - excavationConstValue;
               if (deltaVar2 >= 8) {
                  boolean excavationFlag4 = p + 1 < 16 && isColumnMostlyFlagged(flags, p + 1, excavationConstValue, excavationConstLimit);
                  int excavationCounter4 = 0;
                  int excavationCounter5 = 0;
                  int excavationCounter6 = 0;

                  for (int q = excavationConstValue; q < excavationConstLimit; q++) {
                     boolean twoStackedMaskBits5 = hasTwoStackedMaskBits(sectionMask, p - 1, excavationValue, q);
                     boolean excavationFlag6 = excavationFlag4 ? hasTwoStackedMaskBits(sectionMask, p + 2, excavationValue, q) : hasTwoStackedMaskBits(sectionMask, p + 1, excavationValue, q);
                     if (twoStackedMaskBits5 && excavationFlag6) {
                        excavationCounter4++;
                     }

                     if (excavationValue + 2 < 16) {
                        excavationCounter5++;
                        if (sectionMask.isMaskBitSet(p, excavationValue + 2, q)) {
                           excavationCounter6++;
                        }
                     }
                  }

                  if (meetsTunnelCoverage(deltaVar2, excavationCounter4, excavationCounter5, excavationCounter6)) {
                     for (int r = excavationConstValue; r < excavationConstLimit; r++) {
                        booleans[r][p] = true;
                        if (excavationFlag4) {
                           booleans[r][p + 1] = true;
                        }

                        if (((startZ + r) % 4 + 4) % 4 == 0) {
                           list.add(CraftedExcavation.createTypedNoGroup(excavationValue2 + p, excavationValue3 + excavationValue, startZ + r, ExcavationCraftedType.EXCAVATION, 1, 140, 0));
                        }
                     }
                  }
               }
            } else {
               excavationConstLimit++;
            }
         }
      }
   }

   private static boolean isBlobBorderMostlyMasked(ExcavationScanner.SectionMask sectionMask, boolean[][] flags, List<int[]> list, int excavationConstValue) {
      int excavationConstCounter = 0;
      int excavationConstCounter2 = 0;
      int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      for (int[] excavationConstValues : list) {
         for (int[] intsItem : ints) {
            int index = excavationConstValues[0] + intsItem[0];
            int i = excavationConstValues[1] + intsItem[1];
            if (index >= 0 && index <= 15 && i >= 0 && i <= 15 && !flags[i][index]) {
               if (sectionMask.isMaskBitSet(index, excavationConstValue, i)) {
                  excavationConstCounter++;
               } else {
                  excavationConstCounter2++;
               }
            }
         }
      }

      return excavationConstCounter + excavationConstCounter2 < 4 ? true : excavationConstCounter >= 0.6 * (excavationConstCounter + excavationConstCounter2);
   }

   private static boolean meetsTunnelCoverage(int excavationConstLimit, int excavationConstValue, int excavationConstLimit2, int excavationConstLimit3) {
      if (excavationConstValue < 0.7 * excavationConstLimit) {
         return false;
      } else {
         return excavationConstLimit >= 10 ? true : excavationConstLimit2 > 0 && excavationConstLimit3 >= 0.6 * excavationConstLimit2;
      }
   }

   public static final class SectionMask {
      boolean sectionMaskActive;
      final long[] sectionMaskTable;
      long[] belowLayerBits;
      final long[] sectionMaskBitsets = new long[64];

      public boolean isMaskBitSet(int sectionMaskValue, int k, int i) {
         return isInChunkBounds(sectionMaskValue, k, i) && testBit(this.sectionMaskTable, sectionMaskValue, k, i);
      }

      public long[] getTopLayerBits() {
         long[] longs = new long[4];

         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               if (testBit(this.sectionMaskTable, j, 15, i)) {
                  int sectionMaskValue = i << 4 | j;
                  longs[sectionMaskValue >>> 6] = longs[sectionMaskValue >>> 6] | 1L << (sectionMaskValue & 63);
               }
            }
         }

         return longs;
      }

      private static boolean isInChunkBounds(int sectionMaskValue, int k, int i) {
         return (sectionMaskValue | k | i) >= 0 && sectionMaskValue < 16 && k < 16 && i < 16;
      }

      public void setBitsetBit(int sectionMaskValue, int m, int q) {
         setBit(this.sectionMaskBitsets, sectionMaskValue, m, q);
         this.sectionMaskActive = true;
      }

      public void setBelowLayerBits(long[] times) {
         this.belowLayerBits = times;
      }

      public boolean hasBitsetBits() {
         return this.sectionMaskActive;
      }

      public SectionMask() {
         this.sectionMaskTable = new long[64];
         this.belowLayerBits = null;
         this.sectionMaskActive = false;
      }

      public void setMaskBit(int sectionMaskValue, int m, int q) {
         setBit(this.sectionMaskTable, sectionMaskValue, m, q);
      }

      private static void setBit(long[] sectionMaskValues, int sectionMaskValue, int m, int q) {
         int sectionMaskValue4 = m << 8 | q << 4 | sectionMaskValue;
         sectionMaskValues[sectionMaskValue4 >>> 6] = sectionMaskValues[sectionMaskValue4 >>> 6] | 1L << (sectionMaskValue4 & 63);
      }

      private static boolean testBit(long[] sectionMaskValues, int sectionMaskValue, int k, int i) {
         int sectionMaskValue4 = k << 8 | i << 4 | sectionMaskValue;
         return (sectionMaskValues[sectionMaskValue4 >>> 6] & 1L << (sectionMaskValue4 & 63)) != 0L;
      }

      public boolean isBitsetBitSet(int sectionMaskValue, int i, int j) {
         return isInChunkBounds(sectionMaskValue, i, j) && testBit(this.sectionMaskBitsets, sectionMaskValue, i, j);
      }

      public boolean isBitSetOrBelowLayer(int sectionMaskValue, int sectionMaskLimit, int j) {
         if (sectionMaskLimit > 0) {
            return this.isMaskBitSet(sectionMaskValue, sectionMaskLimit - 1, j);
         } else if (this.belowLayerBits != null && isInChunkBounds(sectionMaskValue, 0, j)) {
            int sectionMaskValue3 = j << 4 | sectionMaskValue;
            return (this.belowLayerBits[sectionMaskValue3 >>> 6] & 1L << (sectionMaskValue3 & 63)) != 0L;
         } else {
            return false;
         }
      }
   }
}
