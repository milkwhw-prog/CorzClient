package com.corz.client.utils;

import java.util.List;

public final class InventorySlotSelector {
   public static int chooseSlotIndex(List<InventorySlotSelector.Slot> list) {
      for (InventorySlotSelector.Slot slot : list) {
         if (slot.slotActive && !slot.isExcludedSlot) {
            return slot.slotIndex;
         }
      }

      int inventoryStackValue = -1;
      long inventoryStackLimit = Long.MAX_VALUE;

      for (InventorySlotSelector.Slot slot2 : list) {
         if (isSlotCandidate(slot2)) {
            long slotScore = computeSlotScore(slot2);
            if (slotScore < inventoryStackLimit) {
               inventoryStackLimit = slotScore;
               inventoryStackValue = slot2.slotIndex;
            }
         }
      }

      return inventoryStackValue;
   }

   static long computeSlotScore(InventorySlotSelector.Slot slot) {
      long inventoryStackTotal = 0L;
      if (slot.isPreferredSlot) {
         inventoryStackTotal += 1000000L;
      }

      long inventoryStackValue = inventoryStackTotal + Math.max(0, slot.primaryRank) * 10000L;
      long inventoryStackValue2 = inventoryStackValue + Math.max(0, slot.secondaryRank) * 100L;
      return inventoryStackValue2 + Math.max(0, slot.tertiaryRank);
   }

   static boolean isSlotCandidate(InventorySlotSelector.Slot slot) {
      return !slot.slotActive && !slot.isExcludedSlot && !slot.isHotbarSlot && !slot.isPartialStack && !slot.isReservedSlot;
   }

   private InventorySlotSelector() {
   }

   public static final class Slot {
      public final boolean slotActive;
      public final boolean isPartialStack;
      public final int slotIndex;
      public final boolean isReservedSlot;
      public final int secondaryRank;
      public final boolean isHotbarSlot;
      public final boolean isPreferredSlot;
      public final int tertiaryRank;
      public final boolean isExcludedSlot;
      public final int primaryRank;

      public Slot(
         int value,
         boolean flag,
         boolean flag2,
         boolean flag3,
         boolean flag4,
         boolean flag5,
         boolean flag6,
         int value2,
         int value3,
         int value4
      ) {
         this.slotIndex = value;
         this.slotActive = flag;
         this.isExcludedSlot = flag2;
         this.isHotbarSlot = flag3;
         this.isPreferredSlot = flag4;
         this.isReservedSlot = flag5;
         this.isPartialStack = flag6;
         this.tertiaryRank = value2;
         this.primaryRank = value3;
         this.secondaryRank = value4;
      }
   }
}
