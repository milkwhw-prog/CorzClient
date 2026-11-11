package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import com.corz.client.utils.Drill;

public final class HotbarFoodEater {
   private int trackedFoodLevel = 20;
   private int foodLevelBeforeEating;
   private final MinecraftClient minecraftClient;
   private boolean eating;
   private final Drill drillSettings;
   private int eatCooldownTicks;
   private int previousSlot = -1;

   public boolean tickAutoEat() {
      ClientPlayerEntity player = this.minecraftClient.player;
      if (player == null || this.minecraftClient.interactionManager == null) {
         return false;
      } else if (!this.drillSettings.autoEatEnabled) {
         if (this.eating) {
            this.restorePreviousSlot(player);
         }

         return false;
      } else {
         int foodLevel = player.getHungerManager().getFoodLevel();
         if (this.eating) {
            if (player.isUsingItem()) {
               this.minecraftClient.options.useKey.setPressed(true);
               return true;
            } else {
               if (foodLevel <= this.foodLevelBeforeEating) {
                  this.eatCooldownTicks = 20;
               }

               this.restorePreviousSlot(player);
               this.trackedFoodLevel = foodLevel;
               return false;
            }
         } else if (this.eatCooldownTicks > 0) {
            this.eatCooldownTicks--;
            return false;
         } else {
            if (foodLevel > this.trackedFoodLevel) {
               this.trackedFoodLevel = foodLevel;
            }

            int selectedSlot = foodLevel <= 10 ? findBestFoodSlot(player) : -1;
            if (selectedSlot < 0) {
               if (this.trackedFoodLevel - foodLevel < this.drillSettings.hungerDropToEat * 2) {
                  return false;
               }

               selectedSlot = findGoldenAppleSlot(player);
            }

            if (selectedSlot < 0) {
               return false;
            } else {
               this.previousSlot = player.getInventory().getSelectedSlot();
               player.getInventory().setSelectedSlot(selectedSlot);
               this.minecraftClient.interactionManager.interactItem(player, Hand.MAIN_HAND);
               this.foodLevelBeforeEating = foodLevel;
               this.eating = true;
               this.minecraftClient.options.useKey.setPressed(player.isUsingItem());
               return true;
            }
         }
      }
   }

   private static int findBestFoodSlot(ClientPlayerEntity player) {
      int nutritionApple = -1;
      int nutritionAppleValue = Integer.MIN_VALUE;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = player.getInventory().getStack(i);
         FoodComponent foodComponent = stack.isEmpty() ? null : stack.get(DataComponentTypes.FOOD);
         if (foodComponent != null) {
            Item item = stack.getItem();
            if (item != Items.GOLDEN_APPLE
               && item != Items.ENCHANTED_GOLDEN_APPLE
               && item != Items.CHORUS_FRUIT
               && item != Items.PUFFERFISH
               && item != Items.SPIDER_EYE
               && item != Items.POISONOUS_POTATO
               && item != Items.SUSPICIOUS_STEW) {
               int nutritionAppleLimit = item == Items.ROTTEN_FLESH ? foodComponent.nutrition() - 100 : foodComponent.nutrition();
               if (nutritionAppleLimit > nutritionAppleValue) {
                  nutritionAppleValue = nutritionAppleLimit;
                  nutritionApple = i;
               }
            }
         }
      }

      return nutritionApple;
   }

   public HotbarFoodEater(MinecraftClient minecraftClient, Drill drill) {
      this.foodLevelBeforeEating = 20;
      this.minecraftClient = minecraftClient;
      this.drillSettings = drill;
   }

   private static int findGoldenAppleSlot(ClientPlayerEntity player) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = player.getInventory().getStack(i);
         if (stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            return i;
         }
      }

      return -1;
   }

   public void resetEating() {
      if (this.eating) {
         ClientPlayerEntity player = this.minecraftClient.player;
         if (player != null) {
            this.restorePreviousSlot(player);
         } else {
            this.minecraftClient.options.useKey.setPressed(false);
         }
      }

      this.eating = false;
      this.previousSlot = -1;
      this.trackedFoodLevel = 20;
      this.foodLevelBeforeEating = 20;
      this.eatCooldownTicks = 0;
   }

   private void restorePreviousSlot(ClientPlayerEntity player) {
      if (this.previousSlot >= 0 && this.previousSlot < 9) {
         player.getInventory().setSelectedSlot(this.previousSlot);
      }

      this.eating = false;
      this.previousSlot = -1;
      this.minecraftClient.options.useKey.setPressed(false);
   }
}
