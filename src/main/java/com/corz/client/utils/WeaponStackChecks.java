package com.corz.client.utils;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.registry.tag.ItemTags;

public final class WeaponStackChecks {
   private WeaponStackChecks() {
   }

   public static boolean canShovelsPickaxes(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && (itemStack.isIn(ItemTags.PICKAXES) || itemStack.isIn(ItemTags.SHOVELS));
   }

   public static boolean isMace(ItemStack itemStack) {
      return itemStack != null && itemStack.getItem() instanceof MaceItem;
   }

   public static boolean isMeleeWeapon(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty()
         ? itemStack.isIn(ItemTags.SWORDS)
            || itemStack.isIn(ItemTags.AXES)
            || itemStack.isIn(ItemTags.SPEARS)
            || itemStack.getItem() instanceof MaceItem
            || itemStack.isOf(Items.TRIDENT)
         : false;
   }

   public static boolean canDisableShield(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         WeaponComponent stack3 = itemStack.get(DataComponentTypes.WEAPON);
         return stack3 != null && stack3.disableBlockingForSeconds() > 0.0F;
      } else {
         return false;
      }
   }
}
