package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class InventoryStack {
   private int stableStreakCount;
   private final Map<Object, Integer> inventoryStackIntegerMap;
   private int inventoryStackCount;
   private final Map<Object, Long> inventoryStackPositionMap = new HashMap<>();

   public void reset() {
      this.inventoryStackPositionMap.clear();
      this.inventoryStackIntegerMap.clear();
      this.inventoryStackCount = 0;
      this.stableStreakCount = 0;
   }

   public boolean updateStableStreak(boolean inventoryStackFlag, boolean inventoryStackFlag2, boolean inventoryStackFlag3) {
      this.stableStreakCount = inventoryStackFlag && inventoryStackFlag2 && inventoryStackFlag3 ? this.stableStreakCount + 1 : 0;
      return this.stableStreakCount >= 6;
   }

   public boolean isUnchanged(Object obj, long inventoryStackValue) {
      if (!Objects.equals(this.inventoryStackPositionMap.get(obj), inventoryStackValue)) {
         return false;
      } else {
         Integer integer = this.inventoryStackIntegerMap.get(obj);
         return integer != null && integer == this.inventoryStackCount;
      }
   }

   public InventoryStack() {
      this.inventoryStackIntegerMap = new HashMap<>();
   }

   public boolean markIfChanged(Object obj, long inventoryStackValue) {
      if (this.isUnchanged(obj, inventoryStackValue)) {
         return false;
      } else {
         this.inventoryStackPositionMap.put(obj, inventoryStackValue);
         this.inventoryStackIntegerMap.put(obj, ++this.inventoryStackCount);
         this.stableStreakCount = 0;
         return true;
      }
   }
}
