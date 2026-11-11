package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;

public final class AutobuyShortfallCalculator {
   public static final String NOT_ENOUGH_CONTAINERS_MESSAGE = "Not enough restock containers marked before autobuy";

   public static <K> List<AutobuyShortfallCalculator.Row<K>> buildRows(
      Map<K, Integer> map, Map<K, Integer> map2, Map<K, Integer> map3, Map<K, Integer> map4, ToIntFunction<K> toIntFunction
   ) {
      var arrayList = new ArrayList<AutobuyShortfallCalculator.Row<K>>();

      for (Entry<K, Integer> entry : map.entrySet()) {
         K k = entry.getKey();
         arrayList.add(
            AutobuyShortfallCalculator.Row.computeFromCounts(
               k,
               (Integer)entry.getValue(),
               map2.getOrDefault(k, 0),
               map3.getOrDefault(k, 0),
               map4.getOrDefault(k, 0),
               toIntFunction.applyAsInt(k)
            )
         );
      }

      return arrayList;
   }

   public static <K> String formatAutobuySummary(List<AutobuyShortfallCalculator.Row<K>> list, AutobuyShortfallCalculator.Capacity capacity, int storageSlotsValue) {
      return "materials="
         + list.size()
         + " totalItems="
         + sumRemainingRequired(list)
         + " deficitItems="
         + sumTrueDeficit(list)
         + " requiredSlots="
         + capacity.requiredStorageSlots()
         + " availableSlots="
         + capacity.availableStorageSlots()
         + " containers="
         + storageSlotsValue
         + " accessibleContainers="
         + capacity.usableContainers();
   }

   public static <K> int sumTrueDeficit(List<AutobuyShortfallCalculator.Row<K>> list) {
      int deficit = 0;

      for (AutobuyShortfallCalculator.Row row : list) {
         deficit += row.trueDeficit();
      }

      return deficit;
   }

   public static <K> AutobuyShortfallCalculator.Capacity computeCapacity(
      List<AutobuyShortfallCalculator.Row<K>> list, int additionalRequiredBound, int n4, int additionalRequiredValue, int n2
   ) {
      int additionalRequiredTotal = 0;

      for (AutobuyShortfallCalculator.Row row : list) {
         additionalRequiredTotal += row.requiredAdditionalSlots();
      }

      int additionalRequiredValue3 = Math.max(0, additionalRequiredBound) + Math.max(0, n4);
      return new AutobuyShortfallCalculator.Capacity(additionalRequiredTotal, additionalRequiredValue3, Math.max(0, additionalRequiredTotal - additionalRequiredValue3), additionalRequiredValue, n2);
   }

   public static <K> int sumRemainingRequired(List<AutobuyShortfallCalculator.Row<K>> list) {
      int requiredRemaining = 0;

      for (AutobuyShortfallCalculator.Row row : list) {
         requiredRemaining += row.remainingRequired();
      }

      return requiredRemaining;
   }

   private AutobuyShortfallCalculator() {
   }

   public static <K> LinkedHashMap<K, Integer> getDeficitByMaterial(List<AutobuyShortfallCalculator.Row<K>> list) {
      var linkedHashMap = new LinkedHashMap();

      for (AutobuyShortfallCalculator.Row row : list) {
         if (row.trueDeficit() > 0) {
            linkedHashMap.put(row.material(), row.trueDeficit());
         }
      }

      return linkedHashMap;
   }

   public record Capacity(int requiredStorageSlots, int availableStorageSlots, int missingStorageSlots, int usableContainers, int inaccessibleContainers) {
      public String formatMissingContainersMessage() {
         return "Not enough restock containers marked before autobuy requiredStorageSlots="
            + this.requiredStorageSlots
            + " availableStorageSlots="
            + this.availableStorageSlots
            + " missingStorageSlots="
            + this.missingStorageSlots
            + " usableContainers="
            + this.usableContainers
            + " inaccessibleContainers="
            + this.inaccessibleContainers;
      }

      public boolean hasEnoughStorage() {
         return this.missingStorageSlots <= 0;
      }
   }

   public record Row<K>(
      K material,
      int requiredTotal,
      int alreadyPlaced,
      int remainingRequired,
      int playerInventoryCount,
      int restockStoredCount,
      int trueDeficit,
      int maxStackSize,
      int requiredAdditionalSlots
   ) {
      public String formatRow() {
         return "material="
            + this.material
            + " requiredTotal="
            + this.requiredTotal
            + " alreadyPlaced="
            + this.alreadyPlaced
            + " remainingRequired="
            + this.remainingRequired
            + " playerInventoryCount="
            + this.playerInventoryCount
            + " restockStoredCount="
            + this.restockStoredCount
            + " trueDeficit="
            + this.trueDeficit
            + " maxStackSize="
            + this.maxStackSize
            + " requiredAdditionalSlots="
            + this.requiredAdditionalSlots;
      }

      public static <K> AutobuyShortfallCalculator.Row<K> computeFromCounts(
         K inventory, int maxStack, int stored, int material, int alreadyPlaced, int requiredTotal
      ) {
         int requiredDeficitValue = Math.max(0, maxStack - stored);
         int max2 = Math.max(0, requiredDeficitValue - Math.max(0, material) - Math.max(0, alreadyPlaced));
         int maxRequiredTotal = Math.max(1, requiredTotal);
         int requiredDeficitValue3 = (max2 + maxRequiredTotal - 1) / maxRequiredTotal;
         return new AutobuyShortfallCalculator.Row<>((K)inventory, maxStack, stored, requiredDeficitValue, Math.max(0, material), Math.max(0, alreadyPlaced), max2, maxRequiredTotal, requiredDeficitValue3);
      }
   }
}
