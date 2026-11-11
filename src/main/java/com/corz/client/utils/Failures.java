package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Failures {
   private long currentGroupTime;
   private final LinkedHashMap<Long, Failures.Group> failuresGroupMap;
   private final HashMap<Long, Long> failuresPositionMap = new HashMap<>();
   private int failures;
   private static final int[][] failuresInts = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

   public long getCurrentGroupTime() {
      return this.currentGroupTime;
   }

   public int getFailures() {
      return this.failures;
   }

   public Failures.Family getFamilyForPosition(long failuresValue) {
      Failures.Group group = this.failuresGroupMap.get(failuresValue);
      return group == null ? null : new Failures.Family(failuresValue, Collections.unmodifiableSet(group.failuresPositionSet), group.failuresCount, group.failureTime, group.failureHitCount);
   }

   public Failures() {
      this.failuresGroupMap = new LinkedHashMap<>();
      this.currentGroupTime = Long.MIN_VALUE;
   }

   public int getGroupCount() {
      return this.failuresGroupMap.size();
   }

   public long getPositionValueOrDefault(long orDefaultValue) {
      return this.failuresPositionMap.getOrDefault(orDefaultValue, Long.MIN_VALUE);
   }

   public static Map<Long, Long> getPositionMapBounded(Collection<Long> collection, int failuresBound) {
      return getPositionMapFlagged(collection, Math.max(0, failuresBound), false);
   }

   public boolean canFlagPositionFailure(long failuresValue, int failuresValue2) {
      Failures.Group group = this.failuresGroupMap.get(failuresValue);
      return group != null && group.failuresCount >= failuresValue2;
   }

   public static Map<Long, Long> getMap(Collection<Long> collection) {
      return getPositionMapFlagged(collection, -1, false);
   }

   public Set<Long> getUnmodifiableSet(long unmodifiableValue) {
      Failures.Group group = this.failuresGroupMap.get(unmodifiableValue);
      return group == null ? Set.of() : Collections.unmodifiableSet(group.failuresPositionSet);
   }

   public void updateGroupTime(long failuresValue) {
      Failures.Group group = this.failuresGroupMap.remove(failuresValue);
      if (group != null) {
         for (Long long2 : group.failuresPositionSet) {
            this.failuresPositionMap.remove(long2);
         }

         if (this.currentGroupTime == failuresValue) {
            this.currentGroupTime = Long.MIN_VALUE;
         }
      }
   }

   private static Map<Long, Long> getPositionMapFlagged(Collection<Long> collection, int failuresValue, boolean failuresFlag) {
      HashSet hashSet = new HashSet(collection);
      HashMap hashMap = new HashMap(Math.max(16, hashSet.size() * 2));
      ArrayDeque arrayDeque = new ArrayDeque();
      ArrayList arrayList = new ArrayList();

      for (Long long2 : collection) {
         if (hashSet.remove(long2)) {
            arrayList.clear();
            arrayDeque.add(long2);
            long failuresLimit = long2;

            while (!arrayDeque.isEmpty()) {
               long totalPriceData = (Long)arrayDeque.poll();
               arrayList.add(totalPriceData);
               if (totalPriceData < failuresLimit) {
                  failuresLimit = totalPriceData;
               }

               collectNeighborPositions(totalPriceData, failuresValue, failuresFlag, hashSet, arrayDeque);
            }

            for (Long long3 : (Iterable<java.lang.Long>) (arrayList)) {
               hashMap.put(long3, failuresLimit);
            }
         }
      }

      return hashMap;
   }

   private static void collectNeighborPositions(long failuresValue, int failuresLimit, boolean failuresFlag, HashSet<Long> hashSet, ArrayDeque<Long> arrayDeque) {
      int valueInt = ProofSupportUtils.getValueInt(failuresValue);
      int failuresValue3 = ProofSupportUtils.getValue(failuresValue);
      int valueForLong = ProofSupportUtils.getValueForLong(failuresValue);
      if (failuresLimit < 0) {
         for (int[] failuresValues : failuresInts) {
            long time5 = ProofSupportUtils.getTime(valueInt + failuresValues[0], failuresValue3 + failuresValues[1], valueForLong + failuresValues[2]);
            if (hashSet.remove(time5)) {
               arrayDeque.add(time5);
            }
         }
      } else {
         for (int i = -failuresLimit; i <= failuresLimit; i++) {
            for (int j = failuresFlag ? 0 : -failuresLimit; j <= (failuresFlag ? 0 : failuresLimit); j++) {
               for (int k = -failuresLimit; k <= failuresLimit; k++) {
                  if (i != 0 || j != 0 || k != 0) {
                     long immutable = ProofSupportUtils.getTime(valueInt + i, failuresValue3 + j, valueForLong + k);
                     if (hashSet.remove(immutable)) {
                        arrayDeque.add(immutable);
                     }
                  }
               }
            }
         }
      }
   }

   public int getTimeBetweenPositions(long failuresValue, long failuresValue2) {
      Failures.Group group = this.failuresGroupMap.get(failuresValue);
      return group != null && group.failureTime == failuresValue2 ? group.failuresCount : 0;
   }

   public int getFailureValueForLimit(int failuresValue) {
      int failuresCounter = 0;

      for (Failures.Group group : this.failuresGroupMap.values()) {
         if (group.failuresCount >= failuresValue) {
            failuresCounter++;
         }
      }

      return failuresCounter;
   }

   public int getTotalFailureValue() {
      return this.failuresPositionMap.size();
   }

   private void linkPositions(long failuresValue, long lowerCase) {
      Failures.Group group = this.failuresGroupMap.get(lowerCase);
      if (group != null) {
         group.failuresPositionSet.remove(failuresValue);
         if (group.failuresPositionSet.isEmpty()) {
            this.failuresGroupMap.remove(lowerCase);
         }
      }
   }

   public static Map<Long, Long> getPositionMapWithBound(Collection<Long> collection, int failuresBound) {
      return getPositionMapFlagged(collection, Math.max(0, failuresBound), true);
   }

   public int getFailuresTime(long failuresValue, long time, long l2) {
      Failures.Group group = this.failuresGroupMap.remove(time);
      if (group == null) {
         group = new Failures.Group();
      }

      this.failuresGroupMap.put(time, group);

      while (this.failuresGroupMap.size() > 512) {
         this.updateFailureCounters();
      }

      if (group.failureTime != l2) {
         group.failureTime = l2;
         group.failuresCount = 0;
      }

      group.failuresCount++;
      group.failureHitCount++;
      this.failures++;
      this.currentGroupTime = time;
      Long long2 = this.failuresPositionMap.get(failuresValue);
      if (long2 != null && long2 != time) {
         this.linkPositions(failuresValue, long2);
      }

      if (group.failuresPositionSet.size() < 4096 && group.failuresPositionSet.add(failuresValue)) {
         this.failuresPositionMap.put(failuresValue, time);
      }

      return group.failuresCount;
   }

   public void updateFailuresTime() {
      this.failuresGroupMap.clear();
      this.failuresPositionMap.clear();
      this.failures = 0;
      this.currentGroupTime = Long.MIN_VALUE;
   }

   public String formatTimeFailures() {
      Failures.Group group = this.failuresGroupMap.get(this.currentGroupTime);
      String failuresText = group == null
         ? "-"
         : ProofSupportUtils.formatText(this.currentGroupTime) + " n=" + group.failuresPositionSet.size() + " a=" + group.failuresCount + "@" + group.failureTime + " f=" + group.failureHitCount;
      return "access families=" + this.failuresGroupMap.size() + " members=" + this.failuresPositionMap.size() + " failures=" + this.failures + " last=" + failuresText;
   }

   private void updateFailureCounters() {
      Iterator iterator = this.failuresGroupMap.entrySet().iterator();

      for (Long long2 : ((Failures.Group)((Entry)iterator.next()).getValue()).failuresPositionSet) {
         this.failuresPositionMap.remove(long2);
      }

      iterator.remove();
   }

   public record Family(long root, Set<Long> members, int attemptsThisEpoch, long epoch, int failuresTotal) {
   }

   private static final class Group {
      int failureHitCount;
      int failuresCount;
      long failureTime;
      final HashSet<Long> failuresPositionSet = new HashSet<>();

      private Group() {
         this.failureTime = Long.MIN_VALUE;
      }
   }
}
