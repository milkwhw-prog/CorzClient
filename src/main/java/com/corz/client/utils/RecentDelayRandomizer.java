package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Random;

public final class RecentDelayRandomizer {
   private int lastDelayTicks = -1;
   private final Random random;
   private final Deque<Integer> recentDelays = new ArrayDeque<>();

   public int pickVariedDelay(int randomBound, int rocketsBuyBound) {
      int maxMaxTicks = Math.max(1, randomBound);
      int maxMinTicks = Math.max(maxMaxTicks, rocketsBuyBound);
      if (this.lastDelayTicks < 0) {
         return maxMaxTicks + this.random.nextInt(maxMinTicks - maxMaxTicks + 1);
      } else {
         ArrayList arrayList = new ArrayList(maxMinTicks - maxMaxTicks + 1);

         for (int i = maxMaxTicks; i <= maxMinTicks; i++) {
            if (Math.abs(i - this.lastDelayTicks) >= 2) {
               arrayList.add(i);
            }
         }

         if (arrayList.isEmpty()) {
            return Math.abs(maxMaxTicks - this.lastDelayTicks) >= Math.abs(maxMinTicks - this.lastDelayTicks) ? maxMaxTicks : maxMinTicks;
         } else {
            int deque = this.getSecondLastDelay();
            if (arrayList.size() > 1 && arrayList.contains(deque) && this.random.nextDouble() < 0.85) {
               arrayList.remove(Integer.valueOf(deque));
            }

            int maxValue = Integer.MAX_VALUE;

            for (int randomValue : (Iterable<Integer>) (arrayList)) {
               maxValue = Math.min(maxValue, this.countRecentOccurrences(randomValue));
            }

            ArrayList arrayList2 = new ArrayList(arrayList.size());

            for (int dequeValue : (Iterable<Integer>) (arrayList)) {
               if (this.countRecentOccurrences(dequeValue) == maxValue) {
                  arrayList2.add(dequeValue);
               }
            }

            return (Integer)arrayList2.get(this.random.nextInt(arrayList2.size()));
         }
      }
   }

   private int countRecentOccurrences(int dequeValue) {
      int deque = 0;

      for (int dequeValue2 : this.recentDelays) {
         if (dequeValue2 == dequeValue) {
            deque++;
         }
      }

      return deque;
   }

   public int getLastDelayTicks() {
      return this.lastDelayTicks;
   }

   public void reset() {
      this.lastDelayTicks = -1;
      this.recentDelays.clear();
   }

   public void recordDelay(int dequeBound) {
      this.lastDelayTicks = Math.max(0, dequeBound);
      this.recentDelays.addLast(this.lastDelayTicks);

      while (this.recentDelays.size() > 10) {
         this.recentDelays.pollFirst();
      }
   }

   public RecentDelayRandomizer(Random random) {
      this.random = random;
   }

   private int getSecondLastDelay() {
      if (this.recentDelays.size() < 2) {
         return -1;
      } else {
         Integer[] array = this.recentDelays.toArray(new Integer[0]);
         return array[array.length - 2];
      }
   }

   public int getMinimumExtraTicks() {
      return this.lastDelayTicks == 0 ? 1 : 0;
   }
}
