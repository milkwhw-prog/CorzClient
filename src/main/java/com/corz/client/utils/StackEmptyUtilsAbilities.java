package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.inventory.InventoryStackUtilsConst;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.inventory.SelectedInventoryUtils;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.categories.bots.AutoAreaMiner;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.movement.Scaffold;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.InventoryPlayerUtils;

public final class StackEmptyUtilsAbilities {
   public static boolean isFlag() {
      return !Scaffold.isGodBridge();
   }

   public static boolean canEmptyEqual(ItemStack stack, ItemStack itemStack) {
      return !stack.isEmpty() && !itemStack.isEmpty() ? stack.getCount() == itemStack.getCount() && ItemStack.areItemsAndComponentsEqual(stack, itemStack) : stack.isEmpty() && itemStack.isEmpty();
   }

   private StackEmptyUtilsAbilities() {
   }

   public static boolean isFlag2() {
      return canFlag(AutoBuilder.class) || canFlag(AutoAreaMiner.class) || canFlag(AutoElytraFinder.class) || canFlag(AutoElytraPilot.class);
   }

   public static boolean canAbilitiesInteraction(MinecraftClient client, Object obj) {
      ClientPlayerEntity player = client.player;
      if (player == null || client.world == null || client.interactionManager == null) {
         return false;
      } else if (!player.isAlive() || player.isSpectator() || player.getAbilities().creativeMode || player.hasVehicle()) {
         return false;
      } else if (client.currentScreen != null && !(client.currentScreen instanceof InventoryScreen)) {
         return false;
      } else if (client.interactionManager.isBreakingBlock() || !canHandlerScreen(player)) {
         return false;
      } else {
         return InventoryStackUtilsHandle.canFlag(obj) && !isFlag3() && !InventoryStackUtilsConst.isFlag() ? !isFlag2() : false;
      }
   }

   public static void handleNetworkHandler(MinecraftClient client, int networkValue) {
      if (!(client.currentScreen instanceof HandledScreen)) {
         if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(networkValue));
         }
      }
   }

   private static boolean canAbilitiesInteractionForMinecraftClientObject(MinecraftClient client, Object obj) {
      ClientPlayerEntity player = client.player;
      if (player == null || client.world == null || client.interactionManager == null) {
         return false;
      } else if (!player.isAlive() || player.isSpectator() || player.getAbilities().creativeMode || player.hasVehicle()) {
         return false;
      } else if (client.currentScreen != null && !(client.currentScreen instanceof InventoryScreen)) {
         return false;
      } else if (client.interactionManager.isBreakingBlock() || !canHandlerScreen(player)) {
         return false;
      } else {
         return InventoryStackUtilsHandle.canFlag(obj) && !canFlag(obj) && !InventoryStackUtilsConst.isFlag() ? !isFlag2() : false;
      }
   }

   public static boolean canPotionEffects(ItemStack stack) {
      if (!stack.isOf(Items.POTION) && !stack.isOf(Items.SPLASH_POTION) && !stack.isOf(Items.LINGERING_POTION)) {
         return false;
      } else {
         PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (potionContentsComponent == null) {
            return false;
         } else {
            boolean potionEffects = false;

            for (StatusEffectInstance statusEffectInstance : potionContentsComponent.getEffects()) {
               StatusEffectCategory category = statusEffectInstance.getEffectType().value().getCategory();
               if (category == StatusEffectCategory.BENEFICIAL) {
                  return false;
               }

               if (category == StatusEffectCategory.HARMFUL) {
                  potionEffects = true;
               }
            }

            return potionEffects;
         }
      }
   }

   public static boolean canFlag(MinecraftClient client, Object obj) {
      return !ExtUtils.isAction() && canAbilitiesInteractionForMinecraftClientObject(client, obj);
   }

   public static boolean canFlag(Object obj) {
      return SelectedInventoryUtils.isSelectedSlot()
         || AutoInventoryTotem.isHandSwapSlotActive()
         || InventoryPlayerUtils.isLivingSelected()
         || ClientThreadUtils.canFlag(obj);
   }

   public static boolean canEmptyStream(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (stack.isOf(Items.TOTEM_OF_UNDYING) || stack.contains(DataComponentTypes.DEATH_PROTECTION)) {
         return true;
      } else if (stack.isOf(Items.ELYTRA) || stack.contains(DataComponentTypes.GLIDER)) {
         return true;
      } else if (stack.isOf(Items.ENCHANTED_BOOK) || stack.isOf(Items.ENDER_CHEST)) {
         return true;
      } else if (!stack.contains(DataComponentTypes.CUSTOM_NAME) && !stack.contains(DataComponentTypes.LODESTONE_TRACKER)) {
         if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock) {
            return true;
         } else {
            ContainerComponent containerComponent = stack.get(DataComponentTypes.CONTAINER);
            if (containerComponent != null && containerComponent.streamNonEmpty().findAny().isPresent()) {
               return true;
            } else {
               BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
               return bundleContentsComponent != null && !bundleContentsComponent.isEmpty();
            }
         }
      } else {
         return true;
      }
   }

   public static boolean canHandlerScreen(ClientPlayerEntity player) {
      return player.currentScreenHandler == player.playerScreenHandler && player.currentScreenHandler.getCursorStack().isEmpty() && !player.isUsingItem();
   }

   public static boolean isFlag3() {
      return SelectedInventoryUtils.isSelectedSlot() || AutoInventoryTotem.isHandSwapSlotActive() || InventoryPlayerUtils.isLivingSelected() || ClientThreadUtils.isFlag6();
   }

   private static boolean canFlag(Class<? extends Module> class2) {
      Module iliIiiliiiii = ModuleRegistry.getModule(class2);
      return iliIiiliiiii != null && iliIiiliiiii.isEnabled();
   }

   public static int getValue(int stackEmptyValue) {
      return stackEmptyValue < 9 ? 36 + stackEmptyValue : stackEmptyValue;
   }

   public static boolean canAppleGolden(ItemStack stack) {
      return stack.contains(DataComponentTypes.FOOD) && !stack.isOf(Items.GOLDEN_APPLE) && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
   }

   public static boolean canFlagForMinecraftClientObject(MinecraftClient client, Object obj) {
      return !ExtUtils.isAction() && canAbilitiesInteraction(client, obj);
   }

   public static int getEqualComponents(ClientPlayerEntity player, ItemStack stack) {
      int equalComponents = 0;

      for (int i = 0; i <= 40; i++) {
         if (i < 36 || i >= 40) {
            ItemStack stack2 = player.getInventory().getStack(i);
            if (!stack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack2, stack)) {
               equalComponents += stack2.getCount();
            }
         }
      }

      return equalComponents;
   }

   public static boolean canSprinting(ClientPlayerEntity player) {
      return ExtUtils.getActionValue() >= 3 && !player.isSprinting();
   }

   public static boolean isModifyWorld(ClientPlayerEntity player) {
      return player.getAbilities().allowModifyWorld;
   }
}
