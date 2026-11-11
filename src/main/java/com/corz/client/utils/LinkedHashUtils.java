package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class LinkedHashUtils {
   private LinkedHashUtils() {
   }

   public static LinkedHashUtils.Assignment getInstance(Map<Long, ? extends List<Long>> map, LinkedHashUtils.Metrics metrics, int linkedHashLimit) {
      if (map != null && !map.isEmpty() && linkedHashLimit > 0) {
         var linkedHashMap = new LinkedHashMap();
         var arrayList = new ArrayList<Long>(map.keySet());
         arrayList.sort(Long::compare);

         for (long linkedHashValue : (Iterable<Long>) (arrayList)) {
            var standsbytarget = (List)map.get(linkedHashValue);
            if (standsbytarget != null) {
               var arrayList2 = new ArrayList<Long>(standsbytarget);
               arrayList2.sort(Long::compare);

               for (long finisherOpenerType : (Iterable<Long>) (arrayList2)) {
                  ((LinkedHashSet<Long>) linkedHashMap.computeIfAbsent(finisherOpenerType, obj -> new LinkedHashSet<>())).add(linkedHashValue);
               }
            }
         }

         if (linkedHashMap.isEmpty()) {
            return LinkedHashUtils.Assignment.INSTANCE;
         } else {
            var linkedHashSet = new LinkedHashSet(arrayList);
            var linkedHashMap2 = new LinkedHashMap();
            var arrayList3 = new ArrayList();
            var linkedHashMap3 = new LinkedHashMap();
            long metricsValue = 0L;

            for (boolean linkedHashFlag = false; !linkedHashSet.isEmpty() && arrayList3.size() < linkedHashLimit; linkedHashFlag = true) {
               long totalPriceData = 0L;
               int finish = 0;
               boolean linkedHashFlag2 = false;
               double maxValue = Double.MAX_VALUE;

               for (Entry entry : (Iterable<java.util.Map.Entry>) (linkedHashMap.entrySet())) {
                  long squaredDistanceValue = (Long)entry.getKey();
                  int linkedHashCounter = 0;

                  for (long tickx : (Iterable<Long>) ((LinkedHashSet)entry.getValue())) {
                     if (linkedHashSet.contains(tickx)) {
                        linkedHashCounter++;
                     }
                  }

                  if (linkedHashCounter != 0) {
                     boolean linkedHashFlag3 = linkedHashFlag && metrics.isWithinAnchorSpacing(metricsValue, squaredDistanceValue);
                     double squaredDistance = metrics.getSquaredDistanceToBuilder(squaredDistanceValue);
                     boolean linkedHashFlag4 = linkedHashCounter > finish
                        || linkedHashCounter == finish && linkedHashFlag3 && !linkedHashFlag2
                        || linkedHashCounter == finish && linkedHashFlag3 == linkedHashFlag2 && squaredDistance < maxValue
                        || linkedHashCounter == finish && linkedHashFlag3 == linkedHashFlag2 && squaredDistance == maxValue && Long.compare(squaredDistanceValue, totalPriceData) < 0;
                     if (linkedHashFlag4) {
                        totalPriceData = squaredDistanceValue;
                        finish = linkedHashCounter;
                        linkedHashFlag2 = linkedHashFlag3;
                        maxValue = squaredDistance;
                     }
                  }
               }

               if (finish == 0) {
                  break;
               }

               arrayList3.add(totalPriceData);
               linkedHashMap3.put(totalPriceData, finish);

               for (long immutable : (Iterable<Long>) ((LinkedHashSet)linkedHashMap.get(totalPriceData))) {
                  if (linkedHashSet.remove(immutable)) {
                     linkedHashMap2.put(immutable, totalPriceData);
                  }
               }

               metricsValue = totalPriceData;
            }

            return new LinkedHashUtils.Assignment(linkedHashMap2, arrayList3, linkedHashMap3);
         }
      } else {
         return LinkedHashUtils.Assignment.INSTANCE;
      }
   }

   public record Assignment(Map<Long, Long> anchorByTarget, List<Long> anchors, Map<Long, Integer> coverage) {
      public static final LinkedHashUtils.Assignment INSTANCE = new LinkedHashUtils.Assignment(Map.of(), List.of(), Map.of());

      public Assignment(Map<Long, Long> anchorByTarget, List<Long> anchors, Map<Long, Integer> coverage) {
         anchorByTarget = Map.copyOf(anchorByTarget == null ? Map.of() : anchorByTarget);
         anchors = List.copyOf(anchors == null ? List.of() : anchors);
         coverage = Map.copyOf(coverage == null ? Map.of() : coverage);
         this.anchorByTarget = anchorByTarget;
         this.anchors = anchors;
         this.coverage = coverage;
      }
   }

   public interface Metrics {
      boolean isWithinAnchorSpacing(long metricsValue, long squaredDistanceValue);

      double getSquaredDistanceToBuilder(long squaredDistanceValue);
   }
}
