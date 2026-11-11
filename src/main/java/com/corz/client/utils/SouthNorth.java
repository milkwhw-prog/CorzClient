package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.IntPredicate;

public final class SouthNorth {
   private int lastHandledIndex;
   private SouthNorth.TxStatus timedPendingType;
   private int timedPendingCount;
   private static final int[] southNorthTable = new int[]{1, 0, 3, 2, 5, 4};
   private int southNorthCount;
   private int consecutiveFailCount;
   private final Set<Integer> unmodifiableSet;
   private int timedPendingValue;
   private int handledPendingCount;
   private static final int[] northDownTable = new int[]{0, 0, 0, 0, -1, 1};
   private int pendingTimedValue;
   private static final int[] table = new int[]{-1, 1, 0, 0, 0, 0};
   private int checkedPendingCount;
   private static final int[] downNorthValues = new int[]{0, 0, -1, 1, 0, 0};
   private int southNorthValue;
   private static final String[] eastWestTexts = new String[]{"down", "up", "north", "south", "west", "east"};
   private int lastSouthNorthIndex;
   private final Set<Integer> unmodifiableIntegerSet = new TreeSet<>();
   private final long clickBaseTime;

   public void handlePendingTimed(int value, int value2) {
      this.pendingTimedValue = value;
      this.southNorthValue = value2;
      this.timedPendingType = SouthNorth.TxStatus.PENDING;
      this.handle(value, value2);
   }

   public void handle(Set<Integer> set) {
      this.unmodifiableIntegerSet.clear();
      if (set != null) {
         for (Integer integer : set) {
            if (integer != null && integer >= 0 && integer < 6) {
               this.unmodifiableIntegerSet.add(integer);
            }
         }
      }
   }

   public int getHandledPendingCount() {
      return this.handledPendingCount;
   }

   public SouthNorth(long time, Set<Integer> set) {
      this.lastSouthNorthIndex = -1;
      this.lastHandledIndex = -1;
      this.pendingTimedValue = -1;
      this.southNorthValue = -1;
      this.timedPendingType = SouthNorth.TxStatus.NONE;
      this.timedPendingValue = -1;
      this.clickBaseTime = time;
      TreeSet treeSet = new TreeSet();
      if (set != null) {
         for (Integer integer : set) {
            if (integer != null && integer >= 0 && integer < 6) {
               treeSet.add(integer);
            }
         }
      }

      this.unmodifiableSet = Collections.unmodifiableSet(treeSet);
   }

   public int getTimedPendingValue() {
      return this.timedPendingValue;
   }

   public int getSouthNorthCount() {
      return this.southNorthCount;
   }

   public void handleTimedPending(Set<Integer> set, Set<Integer> set2, int timedPendingValue) {
      this.handle(set);
      if (this.pendingTimedValue >= 0 && set2 != null && set2.contains(this.pendingTimedValue)) {
         this.timedPendingType = SouthNorth.TxStatus.CONFIRMED;
         this.handledPendingCount++;
         this.timedPendingValue = this.southNorthValue < 0 ? -1 : timedPendingValue - this.southNorthValue;
         this.pendingTimedValue = -1;
         this.southNorthValue = -1;
         this.consecutiveFailCount = 0;
      }
   }

   public void handle(int value, int value2) {
      this.southNorthCount++;
      this.lastHandledIndex = value;
      this.lastSouthNorthIndex = value2;
   }

   public boolean isFlag() {
      return this.consecutiveFailCount > 2;
   }

   public SouthNorth.TxStatus getTimedPendingType() {
      return this.timedPendingType;
   }

   public Set<Integer> getUnmodifiableSet() {
      return Collections.unmodifiableSet(this.unmodifiableIntegerSet);
   }

   public boolean isPendingTimedValue() {
      return this.pendingTimedValue >= 0;
   }

   public List<Integer> getUnmodifiable() {
      ArrayList arrayList = new ArrayList();

      for (Integer integer : this.unmodifiableSet) {
         if (!this.unmodifiableIntegerSet.contains(integer)) {
            arrayList.add(integer);
         }
      }

      return arrayList;
   }

   public static Set<Integer> getWestEast(Map<String, String> map) {
      TreeSet treeSet = new TreeSet();
      if (map == null) {
         return treeSet;
      } else {
         for (int i = 0; i < 6; i++) {
            String prop = (String)map.get(eastWestTexts[i]);
            if (prop != null && prop.trim().equalsIgnoreCase("true")) {
               treeSet.add(i);
            }
         }

         return treeSet;
      }
   }

   public void updateTimedPending() {
      this.pendingTimedValue = -1;
      this.southNorthValue = -1;
      this.consecutiveFailCount = 0;
      this.timedPendingType = SouthNorth.TxStatus.NONE;
   }

   public int getPendingTimedValue() {
      return this.pendingTimedValue;
   }

   public int getLastSouthNorthIndex() {
      return this.lastSouthNorthIndex;
   }

   public void handle(Set<Integer> set, int southNorthValue) {
      this.handleTimedPending(set, set, southNorthValue);
   }

   public static long getNorthDown(long northDownValue, int index) {
      return ProofSupportUtils.getTime(
         ProofSupportUtils.getValueInt(northDownValue) + northDownTable[index],
         ProofSupportUtils.getValue(northDownValue) + table[index],
         ProofSupportUtils.getValueForLong(northDownValue) + downNorthValues[index]
      );
   }

   public int getConsecutiveFailCount() {
      return this.consecutiveFailCount;
   }

   public boolean isEquals() {
      return this.unmodifiableIntegerSet.equals(this.unmodifiableSet);
   }

   public SouthNorth.Click getTime(IntPredicate intPredicate) {
      for (Integer integer : this.getUnmodifiable()) {
         if (intPredicate == null || intPredicate.test(integer)) {
            return new SouthNorth.Click(integer, getNorthDown(this.clickBaseTime, integer), getValue(integer));
         }
      }

      return null;
   }

   public boolean isUnmodifiable() {
      for (Integer integer : this.unmodifiableIntegerSet) {
         if (!this.unmodifiableSet.contains(integer)) {
            return true;
         }
      }

      return false;
   }

   public Set<Integer> getUnmodifiableSet2() {
      return this.unmodifiableSet;
   }

   public int getTimedPendingCount() {
      return this.timedPendingCount;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "target=%d wanted=%s present=%s missing=%s complete=%b clicks=%d tx=%s pendingFace=%d confirmed=%d timeouts=%d dupSuppressed=%d retries=%d latency=%d",
         this.clickBaseTime,
         this.unmodifiableSet,
         this.unmodifiableIntegerSet,
         this.getUnmodifiable(),
         this.isEquals(),
         this.southNorthCount,
         this.timedPendingType,
         this.pendingTimedValue,
         this.handledPendingCount,
         this.checkedPendingCount,
         this.timedPendingCount,
         this.consecutiveFailCount,
         this.timedPendingValue
      );
   }

   public Set<Long> getTimeForIntFilter(IntPredicate intPredicate) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (Integer integer : this.getItems(intPredicate)) {
         linkedHashSet.add(getNorthDown(this.clickBaseTime, integer));
      }

      return linkedHashSet;
   }

   public static int getValue(int index) {
      return southNorthTable[index];
   }

   public List<Integer> getItems(IntPredicate intPredicate) {
      ArrayList arrayList = new ArrayList();
      if (intPredicate == null) {
         return arrayList;
      } else {
         for (Integer integer : this.getUnmodifiable()) {
            if (!intPredicate.test(integer)) {
               arrayList.add(integer);
            }
         }

         return arrayList;
      }
   }

   public long getTime() {
      return this.clickBaseTime;
   }

   public int getValue() {
      return this.lastHandledIndex;
   }

   public boolean canTimedPending(int timedPendingLimit, int timedPendingValue, int timedPendingBound) {
      if (timedPendingLimit >= 0 && timedPendingLimit < 6 && this.unmodifiableIntegerSet.contains(timedPendingLimit)) {
         this.timedPendingCount++;
         return false;
      } else {
         if (this.pendingTimedValue >= 0) {
            if (timedPendingValue - this.southNorthValue < Math.max(1, timedPendingBound)) {
               this.timedPendingCount++;
               return false;
            }

            this.timedPendingType = SouthNorth.TxStatus.TIMED_OUT;
            this.checkedPendingCount++;
            this.consecutiveFailCount++;
            this.pendingTimedValue = -1;
            this.southNorthValue = -1;
         }

         return this.consecutiveFailCount <= 2;
      }
   }

   public int getCheckedPendingCount() {
      return this.checkedPendingCount;
   }

   public record Click(int face, long supportCell, int clickedFace) {
   }

   public static enum TxStatus {
      NONE,
      PENDING,
      CONFIRMED,
      TIMED_OUT;
   }
}
