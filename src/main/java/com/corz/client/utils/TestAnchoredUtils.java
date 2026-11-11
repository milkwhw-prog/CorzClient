package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.function.LongConsumer;

public final class TestAnchoredUtils {
   private static final int[][] anchoredInts = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

   private TestAnchoredUtils() {
   }

   public static TestAnchoredUtils.Result getTestAnchored(
      int anchoredValue, int builderAutoValue, int anchoredValue3, TestAnchoredUtils.Cell cell, TestAnchoredUtils.Cell cell2, int anchoredValue4, LongConsumer longConsumer
   ) {
      if (!cell.test(anchoredValue, builderAutoValue, anchoredValue3)) {
         return new TestAnchoredUtils.Result(TestAnchoredUtils.Verdict.ANCHORED, builderAutoValue, 0);
      } else {
         ArrayDeque arrayDeque = new ArrayDeque();
         HashSet hashSet = new HashSet();
         long time5 = ProofSupportUtils.getTime(anchoredValue, builderAutoValue, anchoredValue3);
         arrayDeque.add(time5);
         hashSet.add(time5);
         if (longConsumer != null) {
            longConsumer.accept(time5);
         }

         int builderAutoValue2 = builderAutoValue;

         while (!arrayDeque.isEmpty()) {
            long proofSupportValue = (Long)arrayDeque.poll();
            int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
            int anchoredValue8 = ProofSupportUtils.getValue(proofSupportValue);
            int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
            if (anchoredValue8 < builderAutoValue2) {
               builderAutoValue2 = anchoredValue8;
            }

            for (int[] anchoredValues : anchoredInts) {
               int cellValue = valueInt + anchoredValues[0];
               int anchoredValue11 = anchoredValue8 + anchoredValues[1];
               int j = valueForLong + anchoredValues[2];
               if (cell.test(cellValue, anchoredValue11, j)) {
                  long blockPos = ProofSupportUtils.getTime(cellValue, anchoredValue11, j);
                  if (hashSet.add(blockPos)) {
                     if (longConsumer != null) {
                        longConsumer.accept(blockPos);
                     }

                     if (hashSet.size() > anchoredValue4) {
                        return new TestAnchoredUtils.Result(TestAnchoredUtils.Verdict.UNKNOWN, builderAutoValue2, hashSet.size());
                     }

                     arrayDeque.add(blockPos);
                  }
               } else if (cell2.test(cellValue, anchoredValue11, j)) {
                  return new TestAnchoredUtils.Result(TestAnchoredUtils.Verdict.ANCHORED, builderAutoValue2, hashSet.size());
               }
            }
         }

         return new TestAnchoredUtils.Result(TestAnchoredUtils.Verdict.FLOATING, builderAutoValue2, hashSet.size());
      }
   }

   public interface Cell {
      boolean test(int cellValue, int builderAutoValue, int j);
   }

   public record Result(TestAnchoredUtils.Verdict verdict, int minY, int size) {
   }

   public static enum Verdict {
      ANCHORED,
      FLOATING,
      UNKNOWN;
   }
}
