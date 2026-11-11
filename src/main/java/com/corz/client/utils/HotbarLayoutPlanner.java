package com.corz.client.utils;

public final class HotbarLayoutPlanner {

   public static int getMoveKey(int layoutHotbarValue, int target) {
      return layoutHotbarValue * 36 + target;
   }

   static boolean isBetterReplacement(HotbarLayoutPlanner.View view, int layoutHotbarValue, int stacksValue, int viewValue) {
      if (view.getBucketScore(stacksValue, viewValue) <= view.getBucketScore(stacksValue, layoutHotbarValue) + 0.005) {
         return false;
      } else {
         int maxCount = view.getMaxCount(layoutHotbarValue);
         return maxCount <= 1 ? true : view.getStackCount(viewValue) >= Math.min(view.getStackCount(layoutHotbarValue), getLowStackThreshold(maxCount));
      }
   }

   static int findRefillSource(HotbarLayoutPlanner.View view, int layoutHotbarValue) {
      int viewValue = -1;

      for (int i = 9; i < 36; i++) {
         if (!view.isEmpty(i)
            && view.isSlotMovable(i)
            && view.isSlotLocked(i)
            && view.rememberedStackMatches(layoutHotbarValue, i)
            && !view.isMoveAllowed(i, layoutHotbarValue)
            && (viewValue < 0 || view.getStackCount(i) > view.getStackCount(viewValue))) {
            viewValue = i;
         }
      }

      return viewValue;
   }

   static int findTopUpSource(HotbarLayoutPlanner.View view, int layoutHotbarValue, int stacksValue) {
      int maxCount = view.getMaxCount(layoutHotbarValue);
      if (maxCount > 1 && view.getStackCount(layoutHotbarValue) <= getLowStackThreshold(maxCount)) {
         double amountsStacks = view.getBucketScore(stacksValue, layoutHotbarValue);
         int viewValue = -1;
         boolean layoutHotbarFlag = false;

         for (int i = 0; i < 36; i++) {
            int searchOrderSlot = getSearchOrderSlot(i);
            if (isValidSourceSlot(view, layoutHotbarValue, stacksValue, searchOrderSlot) && !(Math.abs(view.getBucketScore(stacksValue, searchOrderSlot) - amountsStacks) > 1.0E-6) && view.getStackCount(searchOrderSlot) > view.getStackCount(layoutHotbarValue)) {
               boolean stacks = view.canMergeStacks(layoutHotbarValue, searchOrderSlot);
               if (viewValue < 0 || stacks && !layoutHotbarFlag || stacks == layoutHotbarFlag && view.getStackCount(searchOrderSlot) > view.getStackCount(viewValue)) {
                  viewValue = searchOrderSlot;
                  layoutHotbarFlag = stacks;
               }
            }
         }

         return viewValue;
      } else {
         return -1;
      }
   }

   static int findBestSourceSlot(HotbarLayoutPlanner.View view, int layoutHotbarValue, int stacksValue) {
      int searchOrderSlot3 = -1;

      for (int i = 0; i < 36; i++) {
         int searchOrderSlot = getSearchOrderSlot(i);
         if (isValidSourceSlot(view, layoutHotbarValue, stacksValue, searchOrderSlot) && (searchOrderSlot3 < 0 || isBetterCandidate(view, stacksValue, searchOrderSlot, searchOrderSlot3))) {
            searchOrderSlot3 = searchOrderSlot;
         }
      }

      return searchOrderSlot3;
   }

   public static HotbarLayoutPlanner.Move planNextMove(HotbarLayoutPlanner.View view, boolean layoutHotbarFlag) {
      for (int i = 0; i < 9; i++) {
         if (!view.isHotbarSlotRestricted(i) && view.isEmpty(i)) {
            int layoutHotbarValue = view.getSlotBucketOrdinal(i);
            if (layoutHotbarValue != 0) {
               if (view.rememberedStackFitsSlot(i)) {
                  int layoutHotbarLimit = findBestSourceSlot(view, i, layoutHotbarValue);
                  if (layoutHotbarLimit >= 0) {
                     return new HotbarLayoutPlanner.Move(layoutHotbarLimit, i, true);
                  }
               }
            } else if (view.shouldRefillSlot(i)) {
               int refillSource = findRefillSource(view, i);
               if (refillSource >= 0) {
                  return new HotbarLayoutPlanner.Move(refillSource, i, true);
               }
            }
         }
      }

      if (!layoutHotbarFlag) {
         return null;
      } else {
         for (int j = 0; j < 9; j++) {
            int slotBucketOrdinal = view.getSlotBucketOrdinal(j);
            if (slotBucketOrdinal != 0 && !view.isHotbarSlotRestricted(j) && view.isEmpty(j)) {
               int bestSourceSlot = findBestSourceSlot(view, j, slotBucketOrdinal);
               if (bestSourceSlot >= 0) {
                  return new HotbarLayoutPlanner.Move(bestSourceSlot, j, false);
               }
            }
         }

         for (int k = 0; k < 9; k++) {
            int stacksValue = view.getSlotBucketOrdinal(k);
            if (stacksValue != 0 && !view.isHotbarSlotRestricted(k) && !view.isEmpty(k) && view.isSlotLocked(k)) {
               int viewValue = findBestSourceSlot(view, k, stacksValue);
               if (viewValue >= 0) {
                  if (!view.doesStackFitBucket(stacksValue, k)) {
                     if (!view.isHotbarSlotInUse(k)) {
                        return new HotbarLayoutPlanner.Move(viewValue, k, false);
                     }
                  } else {
                     if (isBetterReplacement(view, k, stacksValue, viewValue)) {
                        return new HotbarLayoutPlanner.Move(viewValue, k, false);
                     }

                     int topUpSource = findTopUpSource(view, k, stacksValue);
                     if (topUpSource >= 0) {
                        return new HotbarLayoutPlanner.Move(topUpSource, k, false);
                     }
                  }
               }
            }
         }

         return null;
      }
   }

   public static int getLowStackThreshold(int layoutHotbarValue) {
      return layoutHotbarValue <= 1 ? 0 : Math.max(1, layoutHotbarValue / 4);
   }

   private static boolean isBetterCandidate(HotbarLayoutPlanner.View view, int layoutHotbarValue, int viewValue, int layoutHotbarValue3) {
      double amountsStacks = view.getBucketScore(layoutHotbarValue, viewValue);
      double bucketScore = view.getBucketScore(layoutHotbarValue, layoutHotbarValue3);
      if (amountsStacks > bucketScore + 1.0E-6) {
         return true;
      } else {
         return amountsStacks < bucketScore - 1.0E-6 ? false : view.getStackCount(viewValue) > view.getStackCount(layoutHotbarValue3);
      }
   }

   static boolean isValidSourceSlot(HotbarLayoutPlanner.View view, int layoutHotbarValue, int stacksValue, int layoutHotbarLimit) {
      if (layoutHotbarLimit == layoutHotbarValue || view.isEmpty(layoutHotbarLimit) || !view.isSlotMovable(layoutHotbarLimit) || !view.isSlotLocked(layoutHotbarLimit) || !view.doesStackFitBucket(stacksValue, layoutHotbarLimit)) {
         return false;
      } else if (view.isMoveAllowed(layoutHotbarLimit, layoutHotbarValue)) {
         return false;
      } else {
         return layoutHotbarLimit >= 9 ? true : !view.isHotbarSlotRestricted(layoutHotbarLimit) && !view.isHotbarSlotInUse(layoutHotbarLimit) && !isSlotCorrectlyFilled(view, layoutHotbarLimit);
      }
   }

   private HotbarLayoutPlanner() {
   }

   static boolean isSlotCorrectlyFilled(HotbarLayoutPlanner.View view, int layoutHotbarValue) {
      int slotBucketOrdinal = view.getSlotBucketOrdinal(layoutHotbarValue);
      return slotBucketOrdinal != 0 && !view.isHotbarSlotRestricted(layoutHotbarValue) && !view.isEmpty(layoutHotbarValue) && view.doesStackFitBucket(slotBucketOrdinal, layoutHotbarValue);
   }

   static int getSearchOrderSlot(int layoutHotbarValue) {
      return layoutHotbarValue < 27 ? layoutHotbarValue + 9 : layoutHotbarValue - 27;
   }

   public record Move(int source, int target, boolean urgent) {
   }

   public interface View {
      boolean shouldRefillSlot(int hotbarAutoValue);

      boolean canMergeStacks(int stacksValue, int searchOrderSlot);

      boolean isMoveAllowed(int stacksCursorValue, int layoutHotbarValue);

      double getBucketScore(int stacksValue, int viewValue);

      int getMaxCount(int viewValue);

      boolean doesStackFitBucket(int stacksValue, int k);

      boolean isSlotMovable(int viewValue);

      boolean rememberedStackMatches(int stacksComeValue, int i);

      boolean isSlotLocked(int viewValue);

      boolean isEmpty(int emptyValue);

      int getSlotBucketOrdinal(int viewValue);

      boolean isHotbarSlotRestricted(int viewValue);

      int getStackCount(int viewValue);

      boolean rememberedStackFitsSlot(int comeStacksValue);

      boolean isHotbarSlotInUse(int cursorHotbarValue);
   }
}
