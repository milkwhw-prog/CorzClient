package com.corz.client.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import com.corz.client.utils.DensityBreachType;

public final class InventoryStackUtilsEmpty {
   private static final MinecraftClient inventoryStackMinecraftClien = MinecraftClient.getInstance();

   public static int getSelectedSlot(int selectedSlot) {
      if (inventoryStackMinecraftClien.player == null) {
         return -1;
      } else {
         PlayerInventory inventory = inventoryStackMinecraftClien.player.getInventory();
         int selectedSlot2 = inventory.getSelectedSlot();
         if (canSwordsEmpty(inventory.getStack(selectedSlot2))) {
            return selectedSlot2;
         } else if (PlayerInventory.isValidHotbarIndex(selectedSlot) && canSwordsEmpty(inventory.getStack(selectedSlot))) {
            return selectedSlot;
         } else {
            boolean[] booleans = new boolean[9];

            for (int i = 0; i < 9; i++) {
               booleans[i] = canSwordsEmpty(inventory.getStack(i));
            }

            return FlagInventoryUtils.getValue(booleans, selectedSlot2);
         }
      }
   }

   public static ItemStack getEmpty(int emptyValue) {
      return inventoryStackMinecraftClien.player != null && PlayerInventory.isValidHotbarIndex(emptyValue) ? inventoryStackMinecraftClien.player.getInventory().getStack(emptyValue) : ItemStack.EMPTY;
   }

   public static int getEnchantmentsDefault(ItemStack itemStack, RegistryKey<Enchantment> registryKey) {
      if (itemStack != null && !itemStack.isEmpty() && registryKey != null) {
         ItemEnchantmentsComponent orDefault = itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

         for (RegistryEntry registryEntry : orDefault.getEnchantments()) {
            if (registryEntry.matchesKey(registryKey)) {
               return orDefault.getLevel(registryEntry);
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public static boolean canSwordsEmpty(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && itemStack.isIn(ItemTags.SWORDS);
   }

   public static boolean canSecondsDisable(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         WeaponComponent stack3 = itemStack.get(DataComponentTypes.WEAPON);
         return stack3 != null && stack3.disableBlockingForSeconds() > 0.0F;
      } else {
         return false;
      }
   }

   private InventoryStackUtilsEmpty() {
   }

   public static int getInstanceSelected() {
      if (inventoryStackMinecraftClien.player == null) {
         return -1;
      } else {
         PlayerInventory inventory = inventoryStackMinecraftClien.player.getInventory();
         boolean[] booleans = new boolean[9];

         for (int i = 0; i < 9; i++) {
            booleans[i] = canSecondsDisable(inventory.getStack(i));
         }

         return FlagInventoryUtils.getValue(booleans, inventory.getSelectedSlot());
      }
   }

   public static boolean canEmpty(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof MaceItem;
   }

   public static boolean canAxesEmpty(ItemStack itemStack) {
      return canSwordsEmpty(itemStack) || canEmpty(itemStack) || canSecondsDisable(itemStack) || itemStack != null && !itemStack.isEmpty() && itemStack.isIn(ItemTags.AXES);
   }

   public static int getDensityInstance(DensityBreachType densityBreachType) {
      if (inventoryStackMinecraftClien.player == null) {
         return -1;
      } else {
         PlayerInventory inventory = inventoryStackMinecraftClien.player.getInventory();

         RegistryKey registryKey = switch (densityBreachType == null ? DensityBreachType.ANY : densityBreachType) {
            case DENSITY -> Enchantments.DENSITY;
            case BREACH -> Enchantments.BREACH;
            case ANY -> null;
         };
         boolean[] booleans = new boolean[9];
         int[] ints = new int[9];

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            booleans[i] = canEmpty(stack);
            ints[i] = booleans[i] && registryKey != null ? getEnchantmentsDefault(stack, registryKey) : 0;
         }

         return FlagInventoryUtils.getValue(booleans, ints, inventory.getSelectedSlot());
      }
   }
}
