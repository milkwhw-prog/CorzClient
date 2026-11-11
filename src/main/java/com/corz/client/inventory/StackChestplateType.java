package com.corz.client.inventory;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

public enum StackChestplateType {
   SWORD(null),
   AXE(null),
   MACE(null),
   TRIDENT(null),
   SPEAR(null),
   PICKAXE(null),
   SHOVEL(null),
   BOW(null),
   CROSSBOW(null),
   HELMET(EquipmentSlot.HEAD),
   CHESTPLATE(EquipmentSlot.CHEST),
   LEGGINGS(EquipmentSlot.LEGS),
   BOOTS(EquipmentSlot.FEET);
   private final EquipmentSlot chest;

   public boolean isChest() {
      return this.chest != null;
   }

   private StackChestplateType(EquipmentSlot chest) {
      this.chest = chest;
   }

   public EquipmentSlot getChest() {
      return this.chest;
   }

   public static StackChestplateType getArmorCrossbow(ItemStack stack) {
      if (stack == null || stack.isEmpty() || stack.getMaxCount() != 1) {
         return null;
      } else if (stack.isIn(ItemTags.SWORDS)) {
         return SWORD;
      } else if (stack.isIn(ItemTags.AXES)) {
         return AXE;
      } else if (stack.isOf(Items.MACE)) {
         return MACE;
      } else if (stack.isOf(Items.TRIDENT)) {
         return TRIDENT;
      } else if (stack.isIn(ItemTags.SPEARS)) {
         return SPEAR;
      } else if (stack.isIn(ItemTags.PICKAXES)) {
         return PICKAXE;
      } else if (stack.isIn(ItemTags.SHOVELS)) {
         return SHOVEL;
      } else if (stack.isOf(Items.BOW)) {
         return BOW;
      } else if (stack.isOf(Items.CROSSBOW)) {
         return CROSSBOW;
      } else if (stack.isIn(ItemTags.HEAD_ARMOR)) {
         return HELMET;
      } else if (stack.isIn(ItemTags.CHEST_ARMOR)) {
         return CHESTPLATE;
      } else if (stack.isIn(ItemTags.LEG_ARMOR)) {
         return LEGGINGS;
      } else {
         return stack.isIn(ItemTags.FOOT_ARMOR) ? BOOTS : null;
      }
   }
}
