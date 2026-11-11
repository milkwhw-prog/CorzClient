package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;

public final class EffectStackUtils {
   public static int getSplashPotion(int splashPotionValue, int splashPotionLimit, RegistryEntry<StatusEffect> registryEntry) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player == null) {
         return -1;
      } else {
         for (int i = splashPotionValue; i < splashPotionLimit; i++) {
            ItemStack stack = instance.player.getInventory().getStack(i);
            if (stack.getItem() == Items.SPLASH_POTION && (registryEntry == null || canEffectsEffect(stack, registryEntry))) {
               return i;
            }
         }

         return -1;
      }
   }

   private static boolean canEffectsEffect(ItemStack itemStack, RegistryEntry<StatusEffect> registryEntry) {
      PotionContentsComponent effect2 = itemStack.get(DataComponentTypes.POTION_CONTENTS);
      if (effect2 == null) {
         return false;
      } else {
         for (StatusEffectInstance statusEffectInstance : effect2.getEffects()) {
            if (statusEffectInstance.getEffectType().equals(registryEntry)) {
               return true;
            }
         }

         return false;
      }
   }

   private EffectStackUtils() {
   }
}
