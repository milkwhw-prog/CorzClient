package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;

public final class BuilderAutoUtilsConst {

   public static <K> boolean isDeficitEmpty(Map<K, Integer> map, Map<K, Integer> map2) {
      return getPositiveDifference(map, map2).isEmpty();
   }

   public static <K> int mergeMaxCounts(Map<K, Integer> map, Map<K, Integer> map2) {
      int builderAutoCounter = 0;

      for (Entry<K, Integer> entry : map2.entrySet()) {
         Integer integer = (Integer)map.get(entry.getKey());
         if (integer == null || integer < (Integer)entry.getValue()) {
            map.put(entry.getKey(), (Integer)entry.getValue());
            builderAutoCounter++;
         }
      }

      return builderAutoCounter;
   }

   private BuilderAutoUtilsConst() {
   }

   public static <K> int fillCountsWithinStackBudget(Map<K, Integer> map, Map<K, Integer> map2, int builderAutoBound, ToIntFunction<K> toIntFunction) {
      int builderAutoCounter = 0;
      int maxMaxStack = Math.max(0, builderAutoBound);

      for (Entry<K, Integer> entry : map2.entrySet()) {
         int orDefault = map.getOrDefault(entry.getKey(), 0);
         int builderAutoLimit = (Integer)entry.getValue();
         if (orDefault < builderAutoLimit) {
            int maxApplyAsInt = Math.max(1, toIntFunction.applyAsInt(entry.getKey()));
            int builderAutoValue = (orDefault + maxApplyAsInt - 1) / maxApplyAsInt;
            int builderAutoValue2 = (builderAutoLimit + maxApplyAsInt - 1) / maxApplyAsInt;
            int deltaVar = builderAutoValue2 - builderAutoValue;
            if (deltaVar <= maxMaxStack) {
               map.put(entry.getKey(), builderAutoLimit);
               maxMaxStack -= deltaVar;
               builderAutoCounter++;
            } else {
               int spotBound = (builderAutoValue + maxMaxStack) * maxApplyAsInt;
               if (spotBound > orDefault) {
                  map.put(entry.getKey(), Math.min(builderAutoLimit, spotBound));
                  builderAutoCounter++;
               }

               maxMaxStack = 0;
            }

            if (maxMaxStack == 0 && deltaVar > 0 && map.getOrDefault(entry.getKey(), 0) < builderAutoLimit) {
               break;
            }
         }
      }

      return builderAutoCounter;
   }

   public static <K> LinkedHashMap<K, Integer> countFirstElements(List<K> list, int builderAutoBound) {
      var linkedHashMap = new LinkedHashMap();
      int builderAutoLimit = Math.max(0, Math.min(builderAutoBound, list == null ? 0 : list.size()));

      for (int i = 0; i < builderAutoLimit; i++) {
         Object ownerKey = list.get(i);
         if (ownerKey != null) {
            linkedHashMap.merge(ownerKey, 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
         }
      }

      return linkedHashMap;
   }

   public static <K> LinkedHashMap<K, Integer> getPositiveDifference(Map<K, Integer> map, Map<K, Integer> map2) {
      var linkedHashMap = new LinkedHashMap();

      for (Entry entry : map.entrySet()) {
         int builderAutoLimit = (Integer)entry.getValue() - map2.getOrDefault(entry.getKey(), 0);
         if (builderAutoLimit > 0) {
            linkedHashMap.put(entry.getKey(), builderAutoLimit);
         }
      }

      return linkedHashMap;
   }
}
