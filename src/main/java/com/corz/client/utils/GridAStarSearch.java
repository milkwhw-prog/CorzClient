package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;

public final class GridAStarSearch {
   private static List<Long> reconstructPath(Map<Long, Long> map, long blockPosValue, long totalPriceData) {
      var arrayList = new ArrayList();
      long blockPosValue2 = blockPosValue;

      while (blockPosValue2 != totalPriceData) {
         arrayList.add(blockPosValue2);
         Long start2 = (Long)map.get(blockPosValue2);
         if (start2 == null) {
            break;
         }

         blockPosValue2 = start2;
      }

      arrayList.add(totalPriceData);
      Collections.reverse(arrayList);
      return arrayList;
   }

   private GridAStarSearch() {
   }

   public static GridAStarSearch.Result searchPath(
      long costValue,
      Set<Long> set,
      int costLimit,
      double costFactor,
      LongFunction<long[]> longFunction,
      GridAStarSearch.StepCost stepCost,
      LongToDoubleFunction longToDoubleFunction
   ) {
      if (set.isEmpty()) {
         return new GridAStarSearch.Result(0L, null, 0, false, 0);
      } else if (set.contains(costValue)) {
         return new GridAStarSearch.Result(costValue, List.of(costValue), 0, false, 0);
      } else {
         var hashMap = new HashMap();
         HashMap<Long, Double> hashMap2 = new HashMap<>();
         var hashSet = new HashSet();
         PriorityQueue priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(obj -> Double.longBitsToDouble(((long[]) obj)[1])));
         hashMap2.put(costValue, 0.0);
         priorityQueue.add(new long[]{costValue, Double.doubleToLongBits(costFactor * longToDoubleFunction.applyAsDouble(costValue))});
         int costCounter = 0;

         while (!priorityQueue.isEmpty() && costCounter++ < costLimit) {
            long blockPosValue = ((long[])priorityQueue.poll())[0];
            if (set.contains(blockPosValue)) {
               return new GridAStarSearch.Result(blockPosValue, reconstructPath(hashMap, blockPosValue, costValue), costCounter, false, priorityQueue.size());
            }

            if (hashSet.add(blockPosValue)) {
               double orDefault = ((double) (Double) (hashMap2.getOrDefault(blockPosValue, Double.MAX_VALUE)));

               for (long tickx : (long[])longFunction.apply(blockPosValue)) {
                  if (!hashSet.contains(tickx)) {
                     double finish = orDefault + stepCost.cost(blockPosValue, tickx);
                     if (finish < hashMap2.getOrDefault(tickx, Double.MAX_VALUE)) {
                        hashMap.put(tickx, blockPosValue);
                        hashMap2.put(tickx, finish);
                        priorityQueue.add(new long[]{tickx, Double.doubleToLongBits(finish + costFactor * longToDoubleFunction.applyAsDouble(tickx))});
                     }
                  }
               }
            }
         }

         long[] longs = new long[hashSet.size() + priorityQueue.size()];
         int costCounter2 = 0;

         for (long costValue5 : (Iterable<Long>) (hashSet)) {
            longs[costCounter2++] = costValue5;
         }

         for (long[] costValues : (Iterable<long[]>) (priorityQueue)) {
            longs[costCounter2++] = costValues[0];
         }

         return new GridAStarSearch.Result(0L, null, Math.min(costCounter, costLimit), !priorityQueue.isEmpty(), priorityQueue.size(), longs);
      }
   }

   public record Result(long goal, List<Long> path, int expanded, boolean budgetExhausted, int openRemaining, long[] footprint) {
      public boolean isPath() {
         return this.path != null;
      }

      public Result(long openRemaining, List<Long> goal, int budgetExhausted, boolean path, int expanded) {
         this(openRemaining, goal, budgetExhausted, path, expanded, null);
      }
   }

   public interface StepCost {
      double cost(long costValue, long tickx);
   }
}
