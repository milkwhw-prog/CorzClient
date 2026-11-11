package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.LongPredicate;

public final class FlagPoseUtils {
   public static long getPoseTime(long poseValue) {
      return ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(poseValue), ProofSupportUtils.getValue(poseValue) + 1, ProofSupportUtils.getValueForLong(poseValue));
   }

   public static boolean canFlagPosePredicate(long poseValue, LongPredicate longPredicate) {
      return getPoseValueWithPredicate(poseValue, longPredicate) == 0;
   }

   private FlagPoseUtils() {
   }

   public static boolean canFlagPoseTwoPredicates(long poseValue, LongPredicate longPredicate, LongPredicate longPredicate2) {
      for (long linkedHashValue : getPoseTimes(poseValue)) {
         if (longPredicate.test(linkedHashValue) && longPredicate2.test(linkedHashValue)) {
            return true;
         }
      }

      return false;
   }

   public static boolean canFlagPoseInt(int poseValue) {
      return poseValue == 0;
   }

   public static boolean canFlagPosePair(long poseValue, long proofSupportValue) {
      int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
      int poseValue4 = ProofSupportUtils.getValue(proofSupportValue);
      int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
      return poseValue == proofSupportValue || poseValue == ProofSupportUtils.getTime(valueInt, poseValue4 - 1, valueForLong) || poseValue == ProofSupportUtils.getTime(valueInt, poseValue4 + 1, valueForLong);
   }

   public static int getPoseValueWithPredicate(long poseValue, LongPredicate longPredicate) {
      int poseCounter = 0;

      for (long linkedHashValue : getPoseTimes(poseValue)) {
         if (!longPredicate.test(linkedHashValue)) {
            poseCounter++;
         }
      }

      return poseCounter;
   }

   public static List<Long> filterPosesWithTwoPredicates(Iterable<Long> iterable, LongPredicate longPredicate, LongPredicate longPredicate2) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (long poseValue : iterable) {
         linkedHashSet.add(poseValue);
      }

      ArrayList arrayList = new ArrayList(linkedHashSet.size());

      while (!linkedHashSet.isEmpty()) {
         Long long2 = null;
         int poseValue2 = -1;
         int minValue = Integer.MIN_VALUE;

         for (long poseValue4 : (Iterable<Long>) (linkedHashSet)) {
            if (!linkedHashSet.contains(getPoseTime(poseValue4))) {
               int poseLimit = getPoseValueWithPredicate(poseValue4, longPredicate);
               int poseLimit2 = ProofSupportUtils.getValue(poseValue4);
               if (long2 == null || poseLimit > poseValue2 || poseLimit == poseValue2 && poseLimit2 > minValue || poseLimit == poseValue2 && poseLimit2 == minValue && poseValue4 < long2) {
                  long2 = poseValue4;
                  poseValue2 = poseLimit;
                  minValue = poseLimit2;
               }
            }
         }

         if (long2 == null) {
            for (long proofSupportValue : (Iterable<Long>) (linkedHashSet)) {
               if (long2 == null || ProofSupportUtils.getValue(proofSupportValue) > ProofSupportUtils.getValue(long2)) {
                  long2 = proofSupportValue;
               }
            }
         }

         arrayList.add(long2);
         linkedHashSet.remove(long2);
      }

      return arrayList;
   }

   public static boolean canFlagPosePredicateAlternate(long poseValue, LongPredicate longPredicate) {
      return longPredicate.test(getPoseTime(poseValue));
   }

   public static FlagPoseUtils.Action getPoseBreak(long poseBreakValue, long proofSupportValue, boolean poseBreakFlag, boolean poseBreakFlag2, boolean poseBreakFlag3) {
      if (canFlagPosePair(poseBreakValue, proofSupportValue)) {
         return FlagPoseUtils.Action.UNSAFE_WOULD_STRAND;
      } else if (poseBreakFlag) {
         return FlagPoseUtils.Action.BREAK_FROM_HERE;
      } else if (poseBreakFlag2) {
         return FlagPoseUtils.Action.ROUTE_TO_BREAK_POSE;
      } else {
         return poseBreakFlag3 ? FlagPoseUtils.Action.DEFER_BLOCKED_BY_NEIGHBOUR : FlagPoseUtils.Action.IMPOSSIBLE_NO_POSE;
      }
   }

   public static long[] getPoseTimes(long poseValue) {
      int valueInt = ProofSupportUtils.getValueInt(poseValue);
      int builderAutoValue = ProofSupportUtils.getValue(poseValue);
      int valueForLong = ProofSupportUtils.getValueForLong(poseValue);
      return new long[]{
         ProofSupportUtils.getTime(valueInt + 1, builderAutoValue, valueForLong),
         ProofSupportUtils.getTime(valueInt - 1, builderAutoValue, valueForLong),
         ProofSupportUtils.getTime(valueInt, builderAutoValue + 1, valueForLong),
         ProofSupportUtils.getTime(valueInt, builderAutoValue - 1, valueForLong),
         ProofSupportUtils.getTime(valueInt, builderAutoValue, valueForLong + 1),
         ProofSupportUtils.getTime(valueInt, builderAutoValue, valueForLong - 1)
      };
   }

   public static List<Long> filterPosesWithPredicate(Iterable<Long> iterable, LongPredicate longPredicate) {
      HashSet hashSet = new HashSet();

      for (long poseValue : iterable) {
         hashSet.add(poseValue);
      }

      return filterPosesWithTwoPredicates(iterable, longPredicate, hashSet::contains);
   }

   public static enum Action {
      BREAK_FROM_HERE,
      ROUTE_TO_BREAK_POSE,
      DEFER_BLOCKED_BY_NEIGHBOUR,
      UNSAFE_WOULD_STRAND,
      IMPOSSIBLE_NO_POSE;
   }
}
