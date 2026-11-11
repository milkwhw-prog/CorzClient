package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;

public final class CountsLateUtils {
   public static boolean isWithinLateBound(int lateBound, int lateValue, int lateValue2) {
      return lateValue - lateValue2 >= Math.max(0, lateBound);
   }

   private static <K> boolean canSpareCounts(
      CountsLateUtils.Load<K> load2, CountsLateUtils.Layer<K> layer, Map<K, Integer> map, int spareValue, ToIntFunction<K> toIntFunction
   ) {
      HashMap<K, Integer> hashMap = new HashMap<>(load2.spareStoreMap);

      for (Entry<K, Integer> entry : layer.counts.entrySet()) {
         if ((Integer)entry.getValue() > 0) {
            hashMap.merge(entry.getKey(), (Integer)entry.getValue(), Integer::sum);
         }
      }

      int value = 0;

      for (Entry<K, Integer> entryItem : hashMap.entrySet()) {
         value += clampLateValue((Integer)entryItem.getValue(), getCountOrZero(map, entryItem.getKey()), toIntFunction.applyAsInt(entryItem.getKey()));
      }

      if (value > spareValue) {
         return false;
      } else {
         for (Entry entry3 : layer.counts.entrySet()) {
            if ((Integer)entry3.getValue() > 0) {
               load2.spareStoreMap.merge((K)entry3.getKey(), (Integer)entry3.getValue(), Integer::sum);
            }
         }

         load2.spareStoreValue = value;
         return true;
      }
   }

   private CountsLateUtils() {
   }

   public static <K> int sumMappedCounts(Map<K, Integer> map, ToIntFunction<K> toIntFunction) {
      int lateTotal = 0;

      for (Entry<K, Integer> entry : map.entrySet()) {
         lateTotal += minLateValue((Integer)entry.getValue(), toIntFunction.applyAsInt(entry.getKey()));
      }

      return lateTotal;
   }

   public static <K> CountsLateUtils.Load<K> getCounts(
      List<CountsLateUtils.Layer<K>> list, Map<K, Integer> map, int lateBound, ToIntFunction<K> toIntFunction, Map<K, Integer> map2
   ) {
      CountsLateUtils.Load<K> load = new CountsLateUtils.Load<>();
      ArrayList<CountsLateUtils.Layer<K>> arrayList = new ArrayList<>(list);
      arrayList.sort(Comparator.comparingInt(layer -> ((CountsLateUtils.Layer) layer).y()));

      for (CountsLateUtils.Layer<K> arrayItem : arrayList) {
         load.accumulatedLayerCounts = load.accumulatedLayerCounts + arrayItem.getCounts();
      }

      int maxFreeSlots = Math.max(0, lateBound);
      if (map2 != null) {
         for (Entry<K, Integer> entry : map2.entrySet()) {
            getSpareStore(load, entry.getKey(), (Integer)entry.getValue(), map, maxFreeSlots, toIntFunction);
         }
      }

      for (CountsLateUtils.Layer<K> layer3 : arrayList) {
         if (layer3.getCounts() == 0) {
            load.layerCount++;
         } else {
            if (!canSpareCounts(load, layer3, map, maxFreeSlots, toIntFunction)) {
               ArrayList<Entry<K, Integer>> arrayList2 = new ArrayList<>(layer3.counts.entrySet());
               arrayList2.sort((entry2, entry3) -> {
                  int compare = Integer.compare((Integer)((java.util.Map.Entry) entry3).getValue(), (Integer)((java.util.Map.Entry) entry2).getValue());
                  return compare != 0 ? compare : String.valueOf(((java.util.Map.Entry) entry2).getKey()).compareTo(String.valueOf(((java.util.Map.Entry) entry3).getKey()));
               });

               for (Entry<K, Integer> entry4 : arrayList2) {
                  load.accumulatedLoadCount = load.accumulatedLoadCount + getSpareStore(load, entry4.getKey(), (Integer)entry4.getValue(), map, maxFreeSlots, toIntFunction);
               }
               break;
            }

            load.layerCount++;
            load.accumulatedLoadCount = load.accumulatedLoadCount + layer3.getCounts();
         }
      }

      return load;
   }

   public static int clampLateValue(int lateValue, int lateBound, int lateValue2) {
      return minLateValue(Math.max(0, lateValue - Math.max(0, lateBound)), lateValue2);
   }

   public static int minLateValue(int lateValue, int lateBound) {
      if (lateValue <= 0) {
         return 0;
      } else {
         int maxMaxStack = Math.max(1, lateBound);
         return (lateValue + maxMaxStack - 1) / maxMaxStack;
      }
   }

   private static <K> int getCountOrZero(Map<K, Integer> map, K k2) {
      if (map == null) {
         return 0;
      } else {
         Integer integer = (Integer)map.get(k2);
         return integer == null ? 0 : Math.max(0, integer);
      }
   }

   public static <K> LinkedHashMap<K, Integer> getCounts(List<CountsLateUtils.Layer<K>> list, Map<K, Integer> map, Map<K, Integer> map2) {
      HashMap<K, Integer> hashMap = new HashMap<>();

      for (CountsLateUtils.Layer<K> layer : list) {
         for (Entry<K, Integer> entry : layer.counts.entrySet()) {
            if ((Integer)entry.getValue() > 0) {
               hashMap.merge(entry.getKey(), layer.y(), Math::min);
            }
         }
      }

      ArrayList<K> arrayList = new ArrayList<>(map.keySet());
      arrayList.sort((k, k2) -> {
         int compare = Integer.compare(((int) (Integer) (hashMap.getOrDefault(k, Integer.MAX_VALUE))), ((int) (Integer) (hashMap.getOrDefault(k2, Integer.MAX_VALUE))));
         if (compare != 0) {
            return compare;
         } else {
            int var5x = Integer.compare(getCountOrZero(map, k2), getCountOrZero(map, k));
            return var5x != 0 ? var5x : String.valueOf(k).compareTo(String.valueOf(k2));
         }
      });
      LinkedHashMap<K, Integer> linkedHashMap = new LinkedHashMap<>();

      for (K k3 : arrayList) {
         int lateLimit = Math.max(0, getCountOrZero(map, k3) - getCountOrZero(map2, k3));
         if (lateLimit > 0) {
            linkedHashMap.put(k3, lateLimit);
         }
      }

      return linkedHashMap;
   }

   private static <K> int getSpareStore(CountsLateUtils.Load<K> load2, K k2, int spareStoreValue, Map<K, Integer> map, int maxFreeSlots, ToIntFunction<K> toIntFunction) {
      if (spareStoreValue <= 0) {
         return 0;
      } else {
         int orDefault = load2.spareStoreMap.getOrDefault(k2, 0);
         int countOrZero = getCountOrZero(map, k2);
         int maxApplyAsInt = Math.max(1, toIntFunction.applyAsInt(k2));
         int clampLateValue4 = clampLateValue(orDefault, countOrZero, maxApplyAsInt);
         int spareStoreFactor = Math.max(0, maxFreeSlots - (load2.spareStoreValue - clampLateValue4));
         int spareStoreBound = countOrZero + spareStoreFactor * maxApplyAsInt;
         int finish = Math.min(orDefault + spareStoreValue, spareStoreBound);
         int deltaVar = finish - orDefault;
         if (deltaVar <= 0) {
            return 0;
         } else {
            load2.spareStoreMap.put((K)k2, finish);
            load2.spareStoreValue = load2.spareStoreValue + (clampLateValue(finish, countOrZero, maxApplyAsInt) - clampLateValue4);
            return deltaVar;
         }
      }
   }

   public record Layer<K>(int y, Map<K, Integer> counts) {
      public int getCounts() {
         int counts = 0;

         for (int layerBound : this.counts.values()) {
            counts += Math.max(0, layerBound);
         }

         return counts;
      }
   }

   public static final class Load<K> {
      public int accumulatedLoadCount;
      public int layerCount;
      public int spareStoreValue;
      public int accumulatedLayerCounts;
      public final LinkedHashMap<K, Integer> spareStoreMap = new LinkedHashMap<>();

      public int getSpareStore(K k2, int spareStoreValue) {
         Integer sparestoremap = this.spareStoreMap.get(k2);
         return sparestoremap == null ? 0 : Math.max(0, sparestoremap - spareStoreValue);
      }

      public boolean isEmpty() {
         return this.spareStoreMap.isEmpty();
      }

      public boolean isFlag() {
         return this.accumulatedLoadCount >= this.accumulatedLayerCounts;
      }

      public boolean canSpareStore(Map<K, Integer> map) {
         for (Entry<K, Integer> entry : this.spareStoreMap.entrySet()) {
            if (CountsLateUtils.getCountOrZero(map, entry.getKey()) < (Integer)entry.getValue()) {
               return false;
            }
         }

         return true;
      }
   }
}
