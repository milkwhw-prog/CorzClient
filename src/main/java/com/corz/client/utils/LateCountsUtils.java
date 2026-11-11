package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.ToIntFunction;
import com.corz.client.utils.CountsLateUtils;

public final class LateCountsUtils {

   public static int getValue(int lateBound, int lateBound2, int rocketsBuyBound) {
      int lateValue = Math.max(0, Math.min(lateBound, 16));
      long lateValue2 = (long)Math.max(1, lateBound2) << Math.max(0, lateValue - 1);
      return (int)Math.min((long)Math.max(lateBound2, rocketsBuyBound), lateValue == 0 ? 0L : lateValue2);
   }

   public static <K> int getCounts(List<CountsLateUtils.Layer<K>> list, Map<K, Integer> map, ToIntFunction<K> toIntFunction, Set<K> set, Set<K> set2) {
      if (list != null && set != null && !set.isEmpty()) {
         int lateLimit = Integer.MAX_VALUE;
         int maxValue = Integer.MAX_VALUE;
         var hashMap = new HashMap();

         for (CountsLateUtils.Layer layer : list) {
            for (Entry entry : (Iterable<java.util.Map.Entry>) (layer.counts().entrySet())) {
               if (entry.getValue() != null && (Integer)entry.getValue() > 0 && (set2 == null || !set2.contains(entry.getKey()))) {
                  if (layer.y() < maxValue) {
                     maxValue = layer.y();
                  }

                  if (set.contains(entry.getKey())) {
                     if (layer.y() < lateLimit) {
                        lateLimit = layer.y();
                        hashMap.clear();
                     }

                     if (layer.y() == lateLimit) {
                        hashMap.merge(entry.getKey(), (Integer)entry.getValue(), (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
                     }
                  }
               }
            }
         }

         if (lateLimit != maxValue) {
            return 0;
         } else {
            int counts = 0;

            for (Entry entry2 : (Iterable<java.util.Map.Entry>) (hashMap.entrySet())) {
               counts += getValueForInt((Integer)entry2.getValue(), getValue(map, (K)entry2.getKey()), Math.max(1, toIntFunction.applyAsInt((K)entry2.getKey())));
            }

            return counts;
         }
      } else {
         return 0;
      }
   }

   private static <K> Comparator<Entry<K, Integer>> getComparator() {
      return (entry, entry2) -> {
         int compare = Integer.compare(entry.getValue(), entry2.getValue());
         return compare != 0 ? compare : String.valueOf(entry.getKey()).compareTo(String.valueOf(entry2.getKey()));
      };
   }

   public static <K> LateCountsUtils.Cycle<K> getLateLayers(
      List<CountsLateUtils.Layer<K>> list,
      Map<K, Integer> map,
      int lateLayersBound,
      ToIntFunction<K> toIntFunction,
      Map<K, Integer> map2,
      Set<K> set,
      Set<K> set2,
      Map<K, Integer> map3
   ) {
      var cycle = new LateCountsUtils.Cycle();
      var planner = new LateCountsUtils.Planner(map, Math.max(0, lateLayersBound), toIntFunction);
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      var hashMap = new HashMap<Object, Integer>();
      if (list != null) {
         var arrayList3 = new ArrayList(list);
         arrayList3.sort(Comparator.comparingInt(obj -> ((CountsLateUtils.Layer) obj).y()));

         for (CountsLateUtils.Layer layer : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList3)) {
            var linkedHashMap = new LinkedHashMap();
            var linkedHashMap2 = new LinkedHashMap();

            for (Entry entry : (Iterable<java.util.Map.Entry>) (layer.counts().entrySet())) {
               if (entry.getValue() != null && (Integer)entry.getValue() > 0) {
                  Object goodsSlot = entry.getKey();
                  if (set == null || !set.contains(goodsSlot)) {
                     hashMap.merge(goodsSlot, (Integer)entry.getValue(), (integer, integer2) -> Integer.sum((Integer) integer, (Integer) integer2));
                     if (set2 != null && set2.contains(goodsSlot)) {
                        linkedHashMap2.put(goodsSlot, (Integer)entry.getValue());
                     } else {
                        linkedHashMap.put(goodsSlot, (Integer)entry.getValue());
                     }
                  } else if (!cycle.lateCellsItems.contains(goodsSlot)) {
                     cycle.lateCellsItems.add((K)goodsSlot);
                  }
               }
            }

            if (!linkedHashMap.isEmpty()) {
               arrayList.add(new CountsLateUtils.Layer<>(layer.y(), linkedHashMap));
            }

            if (!linkedHashMap2.isEmpty()) {
               arrayList2.add(new CountsLateUtils.Layer<>(layer.y(), linkedHashMap2));
            }

            if (!linkedHashMap.isEmpty() || !linkedHashMap2.isEmpty()) {
               cycle.layersSlotsCount++;
            }
         }
      }

      for (CountsLateUtils.Layer layer2 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList)) {
         cycle.lateLayersValue = cycle.lateLayersValue + layer2.getCounts();
      }

      for (CountsLateUtils.Layer layer3 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList2)) {
         cycle.lateLayersValue = cycle.lateLayersValue + layer3.getCounts();
      }

      var hashMap2 = new HashMap();
      if (map3 != null) {
         var arrayList4 = new ArrayList();

         for (Entry entryItem : map3.entrySet()) {
            if (entryItem.getValue() != null && (Integer)entryItem.getValue() > 0 && (set == null || !set.contains(entryItem.getKey()))) {
               arrayList4.add(entryItem);
            }
         }

         arrayList4.sort(getComparator());

         for (Entry entry3 : (Iterable<java.util.Map.Entry>) (arrayList4)) {
            int lateLayersLimit = planner.getMax(entry3.getKey(), (Integer)entry3.getValue());
            if (lateLayersLimit > 0) {
               hashMap2.merge(entry3.getKey(), lateLayersLimit, (obj3, obj4) -> Integer.sum((Integer) obj3, (Integer) obj4));
            }
         }
      }

      var hashMap3 = new HashMap<Object, Integer>();
      if (map2 != null) {
         for (Entry entry4 : map2.entrySet()) {
            if (entry4.getValue() != null && (Integer)entry4.getValue() > 0 && (set == null || !set.contains(entry4.getKey()))) {
               int cooldownValue = planner.getMax(entry4.getKey(), (Integer)entry4.getValue());
               if (cooldownValue > 0) {
                  hashMap3.merge(entry4.getKey(), cooldownValue, (integer3, integer4) -> Integer.sum((Integer) integer3, (Integer) integer4));
               }
            }
         }
      }

      boolean lateLayersFlag = false;
      int minValue = Integer.MIN_VALUE;

      for (int i = 0; i < 2 && !lateLayersFlag; i++) {
         for (CountsLateUtils.Layer layer4 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (i == 0 ? arrayList : arrayList2)) {
            if (i == 0) {
               minValue = layer4.y();
            }

            var linkedHashMap3 = new LinkedHashMap();

            for (Entry entry5 : (Iterable<java.util.Map.Entry>) (layer4.counts().entrySet())) {
               int lateLayersTotal = (Integer)entry5.getValue();
               Integer intersectsCenterBound = (Integer)hashMap2.get(entry5.getKey());
               if (intersectsCenterBound != null && intersectsCenterBound > 0) {
                  int lateLayersValue = Math.min(intersectsCenterBound, lateLayersTotal);
                  lateLayersTotal -= lateLayersValue;
                  hashMap2.put(entry5.getKey(), intersectsCenterBound - lateLayersValue);
               }

               if (lateLayersTotal > 0) {
                  linkedHashMap3.put(entry5.getKey(), lateLayersTotal);
               }
            }

            if (!linkedHashMap3.isEmpty()) {
               if (!planner.canMax(linkedHashMap3)) {
                  var arrayList5 = new ArrayList(linkedHashMap3.entrySet());
                  arrayList5.sort(getComparator());

                  for (Entry entry6 : (Iterable<java.util.Map.Entry>) (arrayList5)) {
                     planner.getMax(entry6.getKey(), (Integer)entry6.getValue());
                  }

                  lateLayersFlag = true;
                  break;
               }

               planner.handle(linkedHashMap3);
            }
         }
      }

      cycle.lateLayersIntegerMap.putAll(planner.lateLayersIntegerMap);

      for (Entry entry7 : (Iterable<java.util.Map.Entry>) (cycle.lateLayersIntegerMap.entrySet())) {
         Object unknownValue = entry7.getKey();
         int lateValue = getValue(map, (K)unknownValue);
         if ((Integer)entry7.getValue() > lateValue) {
            int lateLayersLimit4 = hashMap.getOrDefault(unknownValue, 0) + hashMap3.getOrDefault(unknownValue, 0);
            if (lateLayersLimit4 > (Integer)entry7.getValue()) {
               int maxApplyAsInt = Math.max(1, toIntFunction.applyAsInt((K)unknownValue));
               int spotBound = Math.max(CountsLateUtils.minLateValue((Integer)entry7.getValue(), maxApplyAsInt), CountsLateUtils.minLateValue(lateValue, maxApplyAsInt)) * maxApplyAsInt;
               if (spotBound > (Integer)entry7.getValue()) {
                  entry7.setValue(Math.min(lateLayersLimit4, spotBound));
               }
            }
         }
      }

      for (Entry entry8 : (Iterable<java.util.Map.Entry>) (cycle.lateLayersIntegerMap.entrySet())) {
         Object obj6 = entry8.getKey();
         int lateBound = getValue(map, (K)obj6);
         int finish = (Integer)entry8.getValue() - lateBound;
         if (finish > 0) {
            cycle.buyMaterialsMap.put((K)obj6, finish);
            cycle.slotsLayersValue += finish;
            cycle.layersSlotsValue = cycle.layersSlotsValue + getValueForInt((Integer)entry8.getValue(), lateBound, Math.max(1, toIntFunction.applyAsInt((K)obj6)));
         }
      }

      var hashMap4 = new HashMap<Object, Integer>(cycle.lateLayersIntegerMap);

      for (Entry<Object, Integer> entry9 : hashMap3.entrySet()) {
         hashMap4.merge(entry9.getKey(), -(Integer)entry9.getValue(), (integer6, integer7) -> Integer.sum((Integer) integer6, (Integer) integer7));
      }

      var hashMap5 = new HashMap<Integer, Integer>();

      for (CountsLateUtils.Layer layer5 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList)) {
         hashMap5.merge(layer5.y(), layer5.getCounts(), (integer8, integer9) -> Integer.sum((Integer) integer8, (Integer) integer9));
      }

      for (CountsLateUtils.Layer layer6 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList2)) {
         hashMap5.merge(layer6.y(), layer6.getCounts(), (integer10, integer11) -> Integer.sum((Integer) integer10, (Integer) integer11));
      }

      for (int j = 0; j < 2; j++) {
         for (CountsLateUtils.Layer layer7 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (j == 0 ? arrayList : arrayList2)) {
            int lateLayersTotal2 = 0;

            for (Entry entry10 : (Iterable<java.util.Map.Entry>) (layer7.counts().entrySet())) {
               int maxOrDefault = Math.max(0, hashMap4.getOrDefault(entry10.getKey(), 0));
               int min7 = Math.min(maxOrDefault, (Integer)entry10.getValue());
               if (min7 > 0) {
                  hashMap4.put(entry10.getKey(), maxOrDefault - min7);
                  lateLayersTotal2 += min7;
               }
            }

            if (lateLayersTotal2 > 0) {
               cycle.cellsLateValue += lateLayersTotal2;
               if (hashMap5.merge(layer7.y(), -lateLayersTotal2, (integer12, integer13) -> Integer.sum((Integer) integer12, (Integer) integer13)) == 0) {
                  cycle.lateLayersCount++;
               }

               if (j == 1) {
                  cycle.lateFlag = true;
               } else if (layer7.y() <= minValue) {
                  cycle.minLayerY = Math.min(cycle.minLayerY, layer7.y());
                  cycle.maxLayerY = Math.max(cycle.maxLayerY, layer7.y());
               }
            }
         }
      }

      return cycle;
   }

   public static <K> LinkedHashMap<K, Integer> getCounts(List<CountsLateUtils.Layer<K>> list, Map<K, Integer> map, Set<K> set, int lateBound) {
      var linkedHashMap = new LinkedHashMap();
      if (list == null) {
         return linkedHashMap;
      } else {
         var arrayList = new ArrayList(list);
         arrayList.sort(Comparator.comparingInt(obj -> ((CountsLateUtils.Layer) obj).y()));
         boolean lateFlag = true;

         for (CountsLateUtils.Layer layer : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList)) {
            for (Entry entry : (Iterable<java.util.Map.Entry>) (layer.counts().entrySet())) {
               if (entry.getValue() != null && (Integer)entry.getValue() > 0 && (set == null || !set.contains(entry.getKey()))) {
                  lateFlag = false;
               }
            }
         }

         int lateCounter = 0;

         for (CountsLateUtils.Layer layer2 : (Iterable<com.corz.client.utils.CountsLateUtils.Layer>) (arrayList)) {
            boolean lateFlag2 = false;

            for (Entry entry2 : (Iterable<java.util.Map.Entry>) (layer2.counts().entrySet())) {
               if (entry2.getValue() != null && (Integer)entry2.getValue() > 0) {
                  Object goodsSlot = entry2.getKey();
                  if (lateFlag || set == null || !set.contains(goodsSlot)) {
                     lateFlag2 = true;
                     if (getValue(map, (K)goodsSlot) <= 0) {
                        linkedHashMap.merge(goodsSlot, (Integer)entry2.getValue(), (obj3, obj4) -> Integer.sum((Integer) obj3, (Integer) obj4));
                     }
                  }
               }
            }

            if (lateFlag2) {
               if (++lateCounter >= Math.max(1, lateBound)) {
                  break;
               }
            }
         }

         return linkedHashMap;
      }
   }

   private static <K> int getValue(Map<K, Integer> map, K k2) {
      if (map == null) {
         return 0;
      } else {
         Integer integer = (Integer)map.get(k2);
         return integer == null ? 0 : Math.max(0, integer);
      }
   }

   private LateCountsUtils() {
   }

   public static <K> LateCountsUtils.Cycle<K> getLateMaterials(
      List<CountsLateUtils.Layer<K>> list,
      Map<K, Integer> map,
      int lateMaterialsValue,
      ToIntFunction<K> toIntFunction,
      Map<K, Integer> map2,
      Set<K> set,
      Set<K> set2
   ) {
      return getLateLayers(list, map, lateMaterialsValue, toIntFunction, map2, set, set2, null);
   }

   public static <K> int getValue(Map<K, Integer> map, Map<K, Integer> map2, Map<K, Integer> map3, ToIntFunction<K> toIntFunction, Set<K> set) {
      var hashMap = new HashMap();

      for (int i = 0; i < 2; i++) {
         var map4 = i == 0 ? map : map2;
         if (map4 != null) {
            for (Entry entry : map4.entrySet()) {
               if (entry.getValue() != null && (Integer)entry.getValue() > 0 && (set == null || !set.contains(entry.getKey()))) {
                  hashMap.merge(entry.getKey(), (Integer)entry.getValue(), (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               }
            }
         }
      }

      int lateTotal = 0;

      for (Entry entry2 : (Iterable<java.util.Map.Entry>) (hashMap.entrySet())) {
         lateTotal += getValueForInt((Integer)entry2.getValue(), getValue(map3, (K)entry2.getKey()), Math.max(1, toIntFunction.applyAsInt((K)entry2.getKey())));
      }

      return lateTotal;
   }

   public static int getValueForInt(int lateValue, int lateBound, int lateValue2) {
      return Math.max(0, CountsLateUtils.minLateValue(lateValue, lateValue2) - CountsLateUtils.minLateValue(Math.max(0, lateBound), lateValue2));
   }

   public static int getValue(int lateBound, int rocketsBuyBound) {
      int maxEmptySlots = Math.max(0, lateBound);
      int maxMargin = Math.max(0, rocketsBuyBound);
      return maxEmptySlots > maxMargin ? maxEmptySlots - maxMargin : maxEmptySlots;
   }

   public static final class Cycle<K> {
      public final LinkedHashMap<K, Integer> buyMaterialsMap;
      public int slotsLayersValue;
      public final List<K> lateCellsItems;
      public int maxLayerY;
      public int layersSlotsCount;
      public int cellsLateValue;
      public int layersSlotsValue;
      public int lateLayersCount;
      public int minLayerY;
      public final LinkedHashMap<K, Integer> lateLayersIntegerMap = new LinkedHashMap<>();
      public boolean lateFlag;
      public int lateLayersValue;

      public String getAppend() {
         return "materials="
            + this.buyMaterialsMap.size()
            + " items="
            + this.slotsLayersValue
            + " slots="
            + this.layersSlotsValue
            + " layers="
            + this.lateLayersCount
            + "/"
            + this.layersSlotsCount
            + (this.minLayerY <= this.maxLayerY ? " y=" + this.minLayerY + ".." + this.maxLayerY : "")
            + " cells="
            + this.cellsLateValue
            + "/"
            + this.lateLayersValue
            + (this.lateCellsItems.isEmpty() ? "" : " skipped=" + this.lateCellsItems.size())
            + (this.lateFlag ? " late=true" : "");
      }

      public Cycle() {
         this.buyMaterialsMap = new LinkedHashMap<>();
         this.lateCellsItems = new ArrayList<>();
         this.minLayerY = Integer.MAX_VALUE;
         this.maxLayerY = Integer.MIN_VALUE;
      }

      public boolean isEmpty() {
         return this.buyMaterialsMap.isEmpty();
      }

      public int getMaterialsBuy(ToIntFunction<K> toIntFunction) {
         int materialsBuy = 0;

         for (Entry entry : this.buyMaterialsMap.entrySet()) {
            materialsBuy += CountsLateUtils.minLateValue((Integer)entry.getValue(), toIntFunction.applyAsInt((K)entry.getKey()));
         }

         return materialsBuy;
      }

      public boolean isLateCells() {
         return this.lateCellsItems.isEmpty() && this.cellsLateValue >= this.lateLayersValue;
      }
   }

   private static final class Planner<K> {
      int plannedTotal;
      final Map<K, Integer> plannerIntegerMap;
      final LinkedHashMap<K, Integer> lateLayersIntegerMap = new LinkedHashMap<>();
      final int totalLimit;
      final ToIntFunction<K> toIntFunction;

      boolean canMax(Map<K, Integer> map) {
         int plannerTotal = this.plannedTotal;

         for (Entry entry : map.entrySet()) {
            Object obj = entry.getKey();
            int orDefault = this.lateLayersIntegerMap.getOrDefault(obj, 0);
            int plannerValue = LateCountsUtils.getValue(this.plannerIntegerMap, (K)obj);
            int lateBound = this.getMax((K)obj);
            plannerTotal += LateCountsUtils.getValueForInt(orDefault + (Integer)entry.getValue(), plannerValue, lateBound) - LateCountsUtils.getValueForInt(orDefault, plannerValue, lateBound);
         }

         return plannerTotal <= this.totalLimit;
      }

      int getMax(K k2, int plannerValue) {
         if (plannerValue <= 0) {
            return 0;
         } else {
            int orDefault = this.lateLayersIntegerMap.getOrDefault(k2, 0);
            int lateBound = LateCountsUtils.getValue(this.plannerIntegerMap, (K)k2);
            int plannerFactor = this.getMax((K)k2);
            int valueForInt = LateCountsUtils.getValueForInt(orDefault, lateBound, plannerFactor);
            int max4 = Math.max(0, this.totalLimit - (this.plannedTotal - valueForInt));
            long plannerBound = (long)(CountsLateUtils.minLateValue(lateBound, plannerFactor) + max4) * plannerFactor;
            int finish = (int)Math.min((long)orDefault + plannerValue, Math.max(plannerBound, (long)orDefault));
            int deltaVar = finish - orDefault;
            if (deltaVar <= 0) {
               return 0;
            } else {
               this.lateLayersIntegerMap.put((K)k2, finish);
               this.plannedTotal = this.plannedTotal + (LateCountsUtils.getValueForInt(finish, lateBound, plannerFactor) - valueForInt);
               return deltaVar;
            }
         }
      }

      Planner(Map<K, Integer> map, int max, ToIntFunction<K> toIntFunction) {
         this.plannerIntegerMap = map;
         this.totalLimit = max;
         this.toIntFunction = toIntFunction;
      }

      int getMax(K k2) {
         return Math.max(1, this.toIntFunction.applyAsInt((K)k2));
      }

      void handle(Map<K, Integer> map) {
         for (Entry entry : map.entrySet()) {
            Object obj = entry.getKey();
            int orDefault = this.lateLayersIntegerMap.getOrDefault(obj, 0);
            int plannerValue = LateCountsUtils.getValue(this.plannerIntegerMap, (K)obj);
            int lateBound = this.getMax((K)obj);
            this.plannedTotal = this.plannedTotal + (LateCountsUtils.getValueForInt(orDefault + (Integer)entry.getValue(), plannerValue, lateBound) - LateCountsUtils.getValueForInt(orDefault, plannerValue, lateBound));
            this.lateLayersIntegerMap.put((K)obj, orDefault + (Integer)entry.getValue());
         }
      }
   }
}
