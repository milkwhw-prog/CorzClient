package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class MaxMaterialsUtils {
   public static <K> MaxMaterialsUtils.Plan<K> getAvailableLow(
      Map<K, Integer> map, Map<K, Integer> map2, Map<K, Integer> map3, Map<K, Integer> hashMap, boolean availableLowFlag
   ) {
      var linkedHashSet = new LinkedHashSet<K>(map.keySet());
      var arrayList = new ArrayList(linkedHashSet.size());
      int availableLowTotal = 0;
      int availableLowTotal2 = 0;
      int availableLowTotal3 = 0;
      int availableLowCounter = 0;

      for (K k : linkedHashSet) {
         int maxOrDefault = Math.max(0, map.getOrDefault(k, 0));
         if (maxOrDefault != 0) {
            int availableLowValue = Math.min(maxOrDefault, Math.max(0, getValue(map2, k)));
            int deltaVar = maxOrDefault - availableLowValue;
            int max2 = Math.max(0, getValue(map3, k));
            int max3 = Math.max(0, getValue(hashMap, k));
            int availableLowLimit = max2 + max3;
            int availableLowLimit2 = availableLowFlag ? (deltaVar > 0 && availableLowLimit == 0 ? deltaVar : 0) : Math.max(0, deltaVar - availableLowLimit);
            MaxMaterialsUtils.Status status;
            if (deltaVar == 0) {
               status = MaxMaterialsUtils.Status.COMPLETE;
            } else if (availableLowLimit == 0) {
               status = MaxMaterialsUtils.Status.MISSING;
            } else if (availableLowFlag) {
               status = MaxMaterialsUtils.Status.AVAILABLE;
            } else if (availableLowLimit >= deltaVar) {
               status = MaxMaterialsUtils.Status.AVAILABLE;
            } else {
               status = MaxMaterialsUtils.Status.LOW;
            }

            arrayList.add(new MaxMaterialsUtils.Row<>(k, maxOrDefault, availableLowValue, deltaVar, max2, max3, availableLowLimit, availableLowLimit2, status));
            availableLowTotal += maxOrDefault;
            availableLowTotal2 += availableLowValue;
            availableLowTotal3 += deltaVar;
            if (availableLowLimit2 > 0) {
               availableLowCounter++;
            }
         }
      }

      arrayList.sort(
         Comparator.<MaxMaterialsUtils.Row>comparingInt(row -> row.pricingMaterialValue)
            .reversed()
            .thenComparing(Comparator.<MaxMaterialsUtils.Row>comparingInt(row2 -> row2.maxCount).reversed())
            .thenComparing(Comparator.<MaxMaterialsUtils.Row>comparingInt(row3 -> row3.surveyedPlanValue).reversed())
      );
      double availableLowValue4 = availableLowTotal == 0 ? 1.0 : (double)availableLowTotal2 / availableLowTotal;
      return new MaxMaterialsUtils.Plan<>(arrayList, new MaxMaterialsUtils.Summary(availableLowTotal, availableLowTotal2, availableLowTotal3, arrayList.size(), availableLowCounter, availableLowValue4));
   }

   public static <K> MaxMaterialsUtils.Plan<K> getClassForMap(Map<K, Integer> map, Map<K, Integer> map2, Map<K, Integer> map3, Map<K, Integer> hashMap) {
      return getAvailableLow(map, map2, map3, hashMap, false);
   }

   private static <K> int getValue(Map<K, Integer> map, K k2) {
      Integer integer = (Integer)map.get(k2);
      return integer == null ? 0 : integer;
   }

   private MaxMaterialsUtils() {
   }

   public static final class Plan<K> {
      public final List<MaxMaterialsUtils.Row<K>> readSelectedItems;
      public final MaxMaterialsUtils.Summary typesTotal;

      Plan(List<MaxMaterialsUtils.Row<K>> readSelectedItems, MaxMaterialsUtils.Summary typesTotal) {
         this.readSelectedItems = readSelectedItems;
         this.typesTotal = typesTotal;
      }
   }

   public static final class Row<K> {
      public final int minCount;
      public final int preferredCount;
      public final MaxMaterialsUtils.Status availableCompleteType;
      public final int pricingMaterialValue;
      public final int maxCount;
      public final K next;
      public final int typesNoneValue;
      public final int surveyedPlanValue;
      public final int rowValue;

      Row(K k, int max, int min, int value, int max2, int max3, int value2, int max4, MaxMaterialsUtils.Status availableCompleteType) {
         this.next = (K)k;
         this.surveyedPlanValue = max;
         this.minCount = min;
         this.pricingMaterialValue = value;
         this.typesNoneValue = max2;
         this.rowValue = max3;
         this.preferredCount = value2;
         this.maxCount = max4;
         this.availableCompleteType = availableCompleteType;
      }
   }

   public static enum Status {
      COMPLETE,
      AVAILABLE,
      LOW,
      MISSING;
   }

   public static final class Summary {
      public final int missingTypes;
      public final int types;
      public final int blocksValue;
      public final int total;
      public final double fillRatio;
      public final int remaining;

      Summary(int total, int blocksValue, int remaining, int types2, int missingTypes2, double amount) {
         this.total = total;
         this.blocksValue = blocksValue;
         this.remaining = remaining;
         this.types = types2;
         this.missingTypes = missingTypes2;
         this.fillRatio = amount;
      }
   }
}
