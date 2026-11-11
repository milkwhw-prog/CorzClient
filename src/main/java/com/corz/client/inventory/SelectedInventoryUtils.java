package com.corz.client.inventory;

import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class SelectedInventoryUtils {
   private static final MinecraftClient selectedInventoryMinecraftCl = MinecraftClient.getInstance();
   private static int selectedSlot = -1;

   private SelectedInventoryUtils() {
   }

   public static void updateSelectedSlot() {
      if (selectedSlot != -1 && selectedInventoryMinecraftCl.player != null) {
         PlayerInventory inventory = selectedInventoryMinecraftCl.player.getInventory();
         if (inventory.getSelectedSlot() != selectedSlot) {
            inventory.setSelectedSlot(selectedSlot);
         }

         selectedSlot = -1;
      } else {
         selectedSlot = -1;
      }
   }

   public static int getValue(Item requiredItem) {
      return getInstanceInventory(itemx -> itemx.isOf(requiredItem));
   }

   public static boolean isSelectedSlot() {
      return selectedSlot != -1;
   }

   public static int getInstanceInventory(Predicate<ItemStack> predicate) {
      if (selectedInventoryMinecraftCl.player == null) {
         return -1;
      } else {
         PlayerInventory inventory = selectedInventoryMinecraftCl.player.getInventory();

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && predicate.test(stack)) {
               return i;
            }
         }

         return -1;
      }
   }

   public static void handleSelectedSlot(int selectedSlot2) {
      if (selectedInventoryMinecraftCl.player != null && selectedSlot2 >= 0 && selectedSlot2 <= 8) {
         PlayerInventory inventory = selectedInventoryMinecraftCl.player.getInventory();
         if (selectedSlot == -1) {
            selectedSlot = inventory.getSelectedSlot();
         }

         if (inventory.getSelectedSlot() != selectedSlot2) {
            inventory.setSelectedSlot(selectedSlot2);
         }
      }
   }

   public static void updateSelectedSlot2() {
      selectedSlot = -1;
   }
}
