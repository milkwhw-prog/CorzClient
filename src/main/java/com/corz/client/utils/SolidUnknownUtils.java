package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import com.corz.client.utils.WalkableRegionAnalyzer;

public final class SolidUnknownUtils {

   public static SolidUnknownUtils.Report analyzePlateau(WalkableRegionAnalyzer.Terrain terrain, int solidUnknownValue, int allowedValue, int y, int z, int belowUnreachableValue) {
      if (y < solidUnknownValue + 2) {
         return new SolidUnknownUtils.Report(SolidUnknownUtils.Verdict.NOT_ELEVATED, 0, false, 0);
      } else if (!WalkableRegionAnalyzer.isWalkableCell(terrain, allowedValue, y, z)) {
         return new SolidUnknownUtils.Report(SolidUnknownUtils.Verdict.UNREACHABLE_FROM_BELOW, 0, true, 0);
      } else {
         int solidUnknownValue6 = solidUnknownValue + 1;
         HashSet hashSet = new HashSet();
         ArrayDeque arrayDeque = new ArrayDeque();
         hashSet.add(ProofSupportUtils.getTime(allowedValue, y, z));
         arrayDeque.add(new int[]{allowedValue, y, z});
         int solidUnknownCounter = 0;
         int[][] ints = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
         int[][] ints2 = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

         while (!arrayDeque.isEmpty()) {
            if (hashSet.size() >= belowUnreachableValue) {
               return new SolidUnknownUtils.Report(SolidUnknownUtils.Verdict.UNKNOWN, hashSet.size(), false, solidUnknownCounter);
            }

            int[] solidUnknownValues = (int[])arrayDeque.poll();
            int solidUnknownValue7 = solidUnknownValues[0];
            int solidUnknownLimit = solidUnknownValues[1];
            int solidUnknownValue9 = solidUnknownValues[2];
            if (solidUnknownLimit == solidUnknownValue6) {
               for (int[] intsItem : ints) {
                  int solidUnknownValue10 = solidUnknownValue7 - intsItem[0];
                  int candidateZ = solidUnknownValue9 - intsItem[1];
                  if (WalkableRegionAnalyzer.isWalkableCell(terrain, solidUnknownValue10, solidUnknownValue, candidateZ) && !terrain.solid(solidUnknownValue10, solidUnknownValue + 2, candidateZ)) {
                     solidUnknownCounter++;
                  }
               }

               if (solidUnknownCounter > 0) {
                  return new SolidUnknownUtils.Report(SolidUnknownUtils.Verdict.UNKNOWN, hashSet.size(), false, solidUnknownCounter);
               }
            }

            for (int[] solidUnknownValues3 : ints) {
               enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + solidUnknownValues3[0], solidUnknownLimit, solidUnknownValue9 + solidUnknownValues3[1], solidUnknownValue6);
               if (solidUnknownLimit - 1 >= solidUnknownValue6 && !terrain.solid(solidUnknownValue7 + solidUnknownValues3[0], solidUnknownLimit + 1, solidUnknownValue9 + solidUnknownValues3[1])) {
                  enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + solidUnknownValues3[0], solidUnknownLimit - 1, solidUnknownValue9 + solidUnknownValues3[1], solidUnknownValue6);
               }

               for (int i = 1; i <= 2; i++) {
                  enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + solidUnknownValues3[0], solidUnknownLimit + i, solidUnknownValue9 + solidUnknownValues3[1], solidUnknownValue6);
               }

               enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + 2 * solidUnknownValues3[0], solidUnknownLimit, solidUnknownValue9 + 2 * solidUnknownValues3[1], solidUnknownValue6);
               enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + 2 * solidUnknownValues3[0], solidUnknownLimit + 1, solidUnknownValue9 + 2 * solidUnknownValues3[1], solidUnknownValue6);
            }

            for (int[] solidUnknownValues4 : ints2) {
               enqueueIfReachable(terrain, hashSet, arrayDeque, solidUnknownValue7 + solidUnknownValues4[0], solidUnknownLimit, solidUnknownValue9 + solidUnknownValues4[1], solidUnknownValue6);
            }
         }

         return new SolidUnknownUtils.Report(
            solidUnknownCounter == 0 ? SolidUnknownUtils.Verdict.UNREACHABLE_FROM_BELOW : SolidUnknownUtils.Verdict.UNKNOWN, hashSet.size(), true, solidUnknownCounter
         );
      }
   }

   private SolidUnknownUtils() {
   }

   private static void enqueueIfReachable(WalkableRegionAnalyzer.Terrain terrain, Set<Long> set, ArrayDeque<int[]> arrayDeque, int solidUnknownValue, int solidUnknownLimit, int z, int solidUnknownValue3) {
      if (solidUnknownLimit >= solidUnknownValue3 && WalkableRegionAnalyzer.isWalkableCell(terrain, solidUnknownValue, solidUnknownLimit, z)) {
         if (set.add(ProofSupportUtils.getTime(solidUnknownValue, solidUnknownLimit, z))) {
            arrayDeque.add(new int[]{solidUnknownValue, solidUnknownLimit, z});
         }
      }
   }

   public record Report(SolidUnknownUtils.Verdict verdict, int plateauCells, boolean closed, int entries) {
   }

   public static enum Verdict {
      UNREACHABLE_FROM_BELOW,
      UNKNOWN,
      NOT_ELEVATED;
   }
}
