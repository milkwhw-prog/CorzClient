package com.corz.client.inventory;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.corz.client.utils.CpxVillagerData;

public final class StackRegistryUtils {
   private StackRegistryUtils() {
   }

   public static ItemStack getPotionEmpty(CpxVillagerData cpxVillagerData) {
      return cpxVillagerData == null ? ItemStack.EMPTY : getItemStack(cpxVillagerData.offHand(), cpxVillagerData.potion());
   }

   public static ItemStack getItemStack(Identifier id, Identifier id2) {
      return getPotionGlint(id, id2, false);
   }

   public static ItemStack getPadGlint(CpxVillagerData cpxVillagerData) {
      return cpxVillagerData == null ? ItemStack.EMPTY : getPotionGlint(cpxVillagerData.padItem(), cpxVillagerData.potion(), cpxVillagerData.glint());
   }

   public static ItemStack getIconGlint(CpxVillagerData cpxVillagerData) {
      return cpxVillagerData == null ? ItemStack.EMPTY : getPotionGlint(cpxVillagerData.icon(), cpxVillagerData.potion(), cpxVillagerData.glint());
   }

   public static ItemStack getGlintPotion(CpxVillagerData cpxVillagerData) {
      return cpxVillagerData == null ? ItemStack.EMPTY : getPotionGlint(cpxVillagerData.mainHand(), cpxVillagerData.potion(), cpxVillagerData.glint());
   }

   public static ItemStack getPotionGlint(Identifier id, Identifier id2, boolean potionGlintFlag) {
      if (id == null) {
         return ItemStack.EMPTY;
      } else {
         Item item2 = Registries.ITEM.get(id);
         if (item2 != null && item2 != Items.AIR) {
            ItemStack defaultStack = item2.getDefaultStack();
            if (id2 != null && defaultStack.contains(DataComponentTypes.POTION_CONTENTS)) {
               Registries.POTION.getEntry(id2).ifPresent(reference -> defaultStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(reference)));
            }

            if (potionGlintFlag) {
               defaultStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            return defaultStack;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }
}
