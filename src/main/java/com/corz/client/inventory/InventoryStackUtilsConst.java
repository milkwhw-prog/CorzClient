package com.corz.client.inventory;
import com.corz.client.categories.combat.AutoInventoryTotem;

public final class InventoryStackUtilsConst {

   public static boolean canFlag(int inventoryStackLimit) {
      if (inventoryStackLimit >= 0 && inventoryStackLimit <= 8) {
         int base = AutoInventoryTotem.getReserveSlotIfEnabled();
         return base >= 0 && (inventoryStackLimit == base || AutoInventoryTotem.wasTotemSlot(inventoryStackLimit));
      } else {
         return false;
      }
   }

   public static boolean isFlag() {
      for (int i = 0; i <= 40; i++) {
         if (AutoInventoryTotem.isSlotInPendingSwap(i)) {
            return true;
         }
      }

      return false;
   }

   public static boolean canFlagForInt(int inventoryStackValue) {
      return AutoInventoryTotem.isSlotInPendingSwap(inventoryStackValue);
   }

   private InventoryStackUtilsConst() {
   }
}
