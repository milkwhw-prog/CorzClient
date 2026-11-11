package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import com.corz.client.utils.SpawnerTimeUtils;

public final class PriorityQueueHelper {
   private int x;
   private final PriorityQueue<Long> priorityQueue = new PriorityQueue<>(this::getValue);
   private int z;
   private final Set<Long> priorityQueuePositionSet = new HashSet<>();

   public Long getPoll() {
      Long priorityqueue = this.priorityQueue.poll();
      if (priorityqueue != null) {
         this.priorityQueuePositionSet.remove(priorityqueue);
      }

      return priorityqueue;
   }

   public void updatePriorityQueue() {
      this.priorityQueue.clear();
      this.priorityQueuePositionSet.clear();
   }

   public boolean canPriorityQueue(long priorityQueueValue) {
      if (!this.priorityQueuePositionSet.add(priorityQueueValue)) {
         return false;
      } else {
         this.priorityQueue.add(priorityQueueValue);
         return true;
      }
   }

   public boolean isContains(long containsValue) {
      return this.priorityQueuePositionSet.contains(containsValue);
   }

   private int getValue(Long long2, Long long3) {
      return Long.compare(SpawnerTimeUtils.getTime(long2, this.x, this.z), SpawnerTimeUtils.getTime(long3, this.x, this.z));
   }

   public boolean isEmpty() {
      return this.priorityQueue.isEmpty();
   }

   public int getPriorityQueue() {
      return this.priorityQueue.size();
   }

   public boolean canPriorityQueueForLong(long priorityQueueValue) {
      if (!this.priorityQueuePositionSet.remove(priorityQueueValue)) {
         return false;
      } else {
         this.priorityQueue.remove(priorityQueueValue);
         return true;
      }
   }

   public void handlePriorityQueue(int priorityQueueValue, int priorityQueueValue2) {
      if (priorityQueueValue != this.x || priorityQueueValue2 != this.z) {
         this.x = priorityQueueValue;
         this.z = priorityQueueValue2;
         if (this.priorityQueue.size() > 1) {
            ArrayList arrayList = new ArrayList<>(this.priorityQueue);
            this.priorityQueue.clear();
            this.priorityQueue.addAll(arrayList);
         }
      }
   }
}
