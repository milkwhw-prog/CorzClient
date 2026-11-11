package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VoxelSurfaceMesher {
   public static final int UNUSED_ONE = 1;

   private static Set<Long> toPosKeySet(long[] blockPosValues, int blockPosLimit) {
      var hashSet = new HashSet(Math.max(16, blockPosLimit * 2));

      for (int i = 0; i < blockPosLimit; i++) {
         hashSet.add(blockPosValues[i]);
      }

      return hashSet;
   }

   public static long packPosKey(int blockPosValue, int j, int k) {
      return (long)(blockPosValue & 67108863) << 38 | (long)(k & 67108863) << 12 | j & 4095L;
   }

   private static boolean hasAdjacentEdgeOnOtherAxes(List<Set<Long>> list, int blockPosValue, int blockPosValue2, int j, int k) {
      for (int i = 0; i < 3; i++) {
         if (i != blockPosValue) {
            var set = (Set)list.get(i);
            if (set.contains(packPosKey(blockPosValue2, j, k))) {
               return true;
            }

            if (set.contains(packPosKey(blockPosValue2 - (i == 0 ? 1 : 0), j - (i == 1 ? 1 : 0), k - (i == 2 ? 1 : 0)))) {
               return true;
            }
         }
      }

      return false;
   }

   public static int unpackX(long blockPosValue) {
      return (int)(blockPosValue >> 38);
   }

   public static boolean isCornerVertex(Set<Long> set2, int blockPosValue, int blockPosValue2, int j, int k) {
      boolean blockPosFlag;
      boolean contains2;
      boolean contains3;
      boolean contains4;
      switch (blockPosValue) {
         case 0:
            blockPosFlag = set2.contains(packPosKey(blockPosValue2, j - 1, k - 1));
            contains2 = set2.contains(packPosKey(blockPosValue2, j, k - 1));
            contains3 = set2.contains(packPosKey(blockPosValue2, j - 1, k));
            contains4 = set2.contains(packPosKey(blockPosValue2, j, k));
            break;
         case 1:
            blockPosFlag = set2.contains(packPosKey(blockPosValue2 - 1, j, k - 1));
            contains2 = set2.contains(packPosKey(blockPosValue2, j, k - 1));
            contains3 = set2.contains(packPosKey(blockPosValue2 - 1, j, k));
            contains4 = set2.contains(packPosKey(blockPosValue2, j, k));
            break;
         default:
            blockPosFlag = set2.contains(packPosKey(blockPosValue2 - 1, j - 1, k));
            contains2 = set2.contains(packPosKey(blockPosValue2, j - 1, k));
            contains3 = set2.contains(packPosKey(blockPosValue2 - 1, j, k));
            contains4 = set2.contains(packPosKey(blockPosValue2, j, k));
      }

      int blockPosValue5 = (blockPosFlag ? 1 : 0) + (contains2 ? 1 : 0) + (contains3 ? 1 : 0) + (contains4 ? 1 : 0);
      return blockPosValue5 == 1 || blockPosValue5 == 3 || blockPosValue5 == 2 && blockPosFlag == contains4;
   }

   public static int[] buildBoundarySegments(long[] blockPosValues, int blockPosValue) {
      return getValues(blockPosValues, blockPosValue, null, 0);
   }

   public static int[] buildExposedFaces(long[] blockPosValues, int blockPosValue) {
      var set = toPosKeySet(blockPosValues, blockPosValue);
      int[] ints = new int[set.size() * 6 * 4];
      int blockPosCounter = 0;

      for (long blockPosValue2 : set) {
         int unpackX3 = unpackX(blockPosValue2);
         int unpackY4 = unpackY(blockPosValue2);
         int unpackZ5 = unpackZ(blockPosValue2);

         for (int i = 0; i < 6; i++) {
            int blockPosValue6 = unpackX3;
            int j = unpackY4;
            int k = unpackZ5;
            switch (i) {
               case 0:
                  blockPosValue6 = unpackX3 - 1;
                  break;
               case 1:
                  blockPosValue6 = unpackX3 + 1;
                  break;
               case 2:
                  j = unpackY4 - 1;
                  break;
               case 3:
                  j = unpackY4 + 1;
                  break;
               case 4:
                  k = unpackZ5 - 1;
                  break;
               default:
                  k = unpackZ5 + 1;
            }

            if (!set.contains(packPosKey(blockPosValue6, j, k))) {
               ints[blockPosCounter++] = unpackX3;
               ints[blockPosCounter++] = unpackY4;
               ints[blockPosCounter++] = unpackZ5;
               ints[blockPosCounter++] = i;
            }
         }
      }

      return Arrays.copyOf(ints, blockPosCounter);
   }

   public static int[] getValues(long[] blockPosValues, int blockPosValue, long[] longs, int blockPosLimit) {
      var set = toPosKeySet(blockPosValues, blockPosValue);
      var set2 = longs != null && blockPosLimit > 0 ? toPosKeySet(longs, blockPosLimit) : null;
      List<Set<Long>> of = List.of(new HashSet<>(), new HashSet<>(), new HashSet<>());
      var of2 = List.of(new HashSet(), new HashSet(), new HashSet());

      for (long blockPosValue2 : set) {
         int unpackX3 = unpackX(blockPosValue2);
         int unpackY4 = unpackY(blockPosValue2);
         int unpackZ5 = unpackZ(blockPosValue2);

         for (int i = 0; i < 3; i++) {
            for (int j = 0; j <= 1; j++) {
               for (int k = 0; k <= 1; k++) {
                  int blockPosValue6 = unpackX3;
                  int blockPosValue7 = unpackY4;
                  int blockPosValue8 = unpackZ5;
                  switch (i) {
                     case 0:
                        blockPosValue7 = unpackY4 + j;
                        blockPosValue8 = unpackZ5 + k;
                        break;
                     case 1:
                        blockPosValue6 = unpackX3 + j;
                        blockPosValue8 = unpackZ5 + k;
                        break;
                     default:
                        blockPosValue6 = unpackX3 + j;
                        blockPosValue7 = unpackY4 + k;
                  }

                  long pos = packPosKey(blockPosValue6, blockPosValue7, blockPosValue8);
                  if (((Set)of2.get(i)).add(pos)
                     && isCornerVertex(set, i, blockPosValue6, blockPosValue7, blockPosValue8)
                     && (set2 == null || !isCornerVertex(set2, i, blockPosValue6, blockPosValue7, blockPosValue8))) {
                     ((Set)of.get(i)).add(pos);
                  }
               }
            }
         }
      }

      return encodeSegmentRuns(of);
   }

   private VoxelSurfaceMesher() {
   }

   public static int unpackZ(long blockPosValue) {
      return (int)(blockPosValue << 26 >> 38);
   }

   private static int[] encodeSegmentRuns(List<Set<Long>> list) {
      var arrayList = new ArrayList();

      for (int i = 0; i < 3; i++) {
         var arrayList2 = new ArrayList<int[]>(((Set)list.get(i)).size());

         for (long blockPosValue : (Iterable<Long>) ((Set)list.get(i))) {
            int unpackX2 = unpackX(blockPosValue);
            int unpackY3 = unpackY(blockPosValue);
            int unpackZ4 = unpackZ(blockPosValue);
            int blockPosValue5 = i == 0 ? unpackX2 : (i == 1 ? unpackY3 : unpackZ4);
            int blockPosValue6 = i == 0 ? unpackY3 : unpackX2;
            int blockPosValue7 = i == 2 ? unpackY3 : unpackZ4;
            arrayList2.add(new int[]{blockPosValue6, blockPosValue7, blockPosValue5, unpackX2, unpackY3, unpackZ4});
         }

         arrayList2.sort((blockPosValues, blockPosValues2) -> blockPosValues[0] != blockPosValues2[0] ? Integer.compare(((int[]) blockPosValues)[0], ((int[]) blockPosValues2)[0]) : (blockPosValues[1] != blockPosValues2[1] ? Integer.compare(((int[]) blockPosValues)[1], ((int[]) blockPosValues2)[1]) : Integer.compare(((int[]) blockPosValues)[2], ((int[]) blockPosValues2)[2])));
         int blockPosTotal = 0;

         while (blockPosTotal < arrayList2.size()) {
            int[] blockPosValues3 = (int[])arrayList2.get(blockPosTotal);

            int blockPosCounter;
            for (blockPosCounter = 1; blockPosTotal + blockPosCounter < arrayList2.size(); blockPosCounter++) {
               int[] blockPosValues4 = (int[])arrayList2.get(blockPosTotal + blockPosCounter);
               if (blockPosValues4[0] != blockPosValues3[0] || blockPosValues4[1] != blockPosValues3[1] || blockPosValues4[2] != blockPosValues3[2] + blockPosCounter) {
                  break;
               }
            }

            arrayList.add(new int[]{i, blockPosValues3[3], blockPosValues3[4], blockPosValues3[5], blockPosCounter});
            blockPosTotal += blockPosCounter;
         }
      }

      int[] ints = new int[arrayList.size() * 7];
      int blockPosCounter2 = 0;

      for (int[] blockPosValues5 : (Iterable<int[]>) (arrayList)) {
         int blockPosValue8 = blockPosValues5[0];
         int blockPosValue9 = blockPosValues5[1];
         int j = blockPosValues5[2];
         int k = blockPosValues5[3];
         int blockPosValue12 = blockPosValues5[4];
         int blockPosValue13 = blockPosValue9 + (blockPosValue8 == 0 ? blockPosValue12 : 0);
         int blockPosValue14 = j + (blockPosValue8 == 1 ? blockPosValue12 : 0);
         int blockPosValue15 = k + (blockPosValue8 == 2 ? blockPosValue12 : 0);
         ints[blockPosCounter2++] = blockPosValue8;
         ints[blockPosCounter2++] = blockPosValue9;
         ints[blockPosCounter2++] = j;
         ints[blockPosCounter2++] = k;
         ints[blockPosCounter2++] = blockPosValue12;
         ints[blockPosCounter2++] = hasAdjacentEdgeOnOtherAxes(list, blockPosValue8, blockPosValue9, j, k) ? 1 : 0;
         ints[blockPosCounter2++] = hasAdjacentEdgeOnOtherAxes(list, blockPosValue8, blockPosValue13, blockPosValue14, blockPosValue15) ? 1 : 0;
      }

      return ints;
   }

   public static int unpackY(long blockPosValue) {
      return (int)(blockPosValue << 52 >> 52);
   }
}
