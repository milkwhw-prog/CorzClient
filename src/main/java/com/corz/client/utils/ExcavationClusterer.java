package com.corz.client.utils;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.corz.client.utils.ExcavationCluster;
import com.corz.client.utils.CraftedExcavation;

public final class ExcavationClusterer {

   private ExcavationClusterer() {
   }

   private static long packCellKey(int hashArrayValue, int hashArrayValue2, int hashArrayValue3) {
      return hashArrayValue & 67108863L | (long)(hashArrayValue3 & 67108863) << 26 | (long)(hashArrayValue2 & 4095) << 52;
   }

   private static int enqueueUnvisitedNeighbors(int[] hashArrayValues, int hashArrayLimit, int[] hashArrayValues2, int[] hashArrayValues3, int hashArrayValue, int[] hashArrayValues4, int hashArrayCounter) {
      for (int i = 0; i < hashArrayLimit; i++) {
         int index = hashArrayValues[i];
         if (hashArrayValues2[index] <= 0 && hashArrayValues3[index] != hashArrayValue) {
            hashArrayValues3[index] = hashArrayValue;
            hashArrayValues4[hashArrayCounter++] = index;
         }
      }

      return hashArrayCounter;
   }

   public static List<ExcavationCluster> clusterCells(List<CraftedExcavation> list, int hashArrayBound, int hashArrayLimit) {
      int cellsSize = list.size();
      ArrayList arrayList = new ArrayList();
      if (cellsSize == 0) {
         return arrayList;
      } else {
         int maxEps = Math.max(1, hashArrayBound);
         int[] ints = new int[cellsSize];
         int[] ints2 = new int[cellsSize];
         int[] ints3 = new int[cellsSize];
         Long2ObjectOpenHashMap long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();

         for (int i = 0; i < cellsSize; i++) {
            CraftedExcavation cell = (CraftedExcavation)list.get(i);
            ints[i] = cell.posX;
            ints2[i] = cell.posY;
            ints3[i] = cell.posZ;
            long hashArrayValue = packCellKey(Math.floorDiv(cell.posX, maxEps), Math.floorDiv(cell.posY, maxEps), Math.floorDiv(cell.posZ, maxEps));
            IntArrayList intArrayList = (IntArrayList)long2ObjectOpenHashMap.get(hashArrayValue);
            if (intArrayList == null) {
               intArrayList = new IntArrayList();
               long2ObjectOpenHashMap.put(hashArrayValue, intArrayList);
            }

            intArrayList.add(i);
         }

         int[] ints4 = new int[cellsSize];
         int hashArrayCounter = 0;
         int[] hashArrayValues = new int[cellsSize];
         int[] ints6 = new int[cellsSize];
         int[] ints7 = new int[cellsSize];

         for (int j = 0; j < cellsSize; j++) {
            if (ints4[j] == 0) {
               int elements = findNeighborsInRange(ints, ints2, ints3, long2ObjectOpenHashMap, j, maxEps, hashArrayBound, hashArrayValues);
               if (elements < hashArrayLimit) {
                  ints4[j] = -1;
               } else {
                  ints4[j] = ++hashArrayCounter;
                  int hashArrayCounter2 = 0;
                  int hashArrayLimit2 = enqueueUnvisitedNeighbors(hashArrayValues, elements, ints4, ints7, hashArrayCounter, ints6, 0);

                  while (hashArrayCounter2 < hashArrayLimit2) {
                     int index = ints6[hashArrayCounter2++];
                     if (ints4[index] == -1) {
                        ints4[index] = hashArrayCounter;
                     }

                     if (ints4[index] == 0) {
                        ints4[index] = hashArrayCounter;
                        int neighborsInRange = findNeighborsInRange(ints, ints2, ints3, long2ObjectOpenHashMap, index, maxEps, hashArrayBound, hashArrayValues);
                        if (neighborsInRange >= hashArrayLimit) {
                           hashArrayLimit2 = enqueueUnvisitedNeighbors(hashArrayValues, neighborsInRange, ints4, ints7, hashArrayCounter, ints6, hashArrayLimit2);
                        }
                     }
                  }
               }
            }
         }

         HashMap hashMap = new HashMap();

         for (int m = 0; m < cellsSize; m++) {
            if (ints4[m] > 0) {
               ((com.corz.client.utils.ExcavationCluster) hashMap.computeIfAbsent(ints4[m], obj -> new ExcavationCluster())).addCell((CraftedExcavation)list.get(m));
            }
         }

         arrayList.addAll(hashMap.values());
         return arrayList;
      }
   }

   private static int findNeighborsInRange(int[] elementsValues, int[] elementsValues2, int[] elementsValues3, Long2ObjectOpenHashMap<IntArrayList> long2ObjectOpenHashMap, int index, int elementsValue, int elementsLimit, int[] hashArrayValues) {
      int floorDivValue = elementsValues[index];
      int elementsValue3 = elementsValues2[index];
      int elementsValue4 = elementsValues3[index];
      int floorDiv = Math.floorDiv(floorDivValue, elementsValue);
      int floorDiv2 = Math.floorDiv(elementsValue3, elementsValue);
      int floorDiv3 = Math.floorDiv(elementsValue4, elementsValue);
      int elements = 0;

      for (int j = -1; j <= 1; j++) {
         for (int k = -1; k <= 1; k++) {
            for (int m = -1; m <= 1; m++) {
               IntArrayList intArrayList = (IntArrayList)long2ObjectOpenHashMap.get(packCellKey(floorDiv + j, floorDiv2 + k, floorDiv3 + m));
               if (intArrayList != null) {
                  int[] elements2 = intArrayList.elements();
                  int index2 = 0;

                  for (int p = intArrayList.size(); index2 < p; index2++) {
                     int index3 = elements2[index2];
                     if (index3 != index) {
                        int elementsBound = Math.abs(floorDivValue - elementsValues[index3]);
                        int aggroPreemptBound = Math.abs(elementsValue3 - elementsValues2[index3]);
                        int rocketsBuyBound = Math.abs(elementsValue4 - elementsValues3[index3]);
                        if (Math.max(elementsBound, Math.max(aggroPreemptBound, rocketsBuyBound)) <= elementsLimit) {
                           hashArrayValues[elements++] = index3;
                        }
                     }
                  }
               }
            }
         }
      }

      return elements;
   }
}
