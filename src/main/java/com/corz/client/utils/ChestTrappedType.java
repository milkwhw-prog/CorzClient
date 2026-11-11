package com.corz.client.utils;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.TrappedChestBlock;

public enum ChestTrappedType {
   CHEST("Chest"),
   TRAPPED_CHEST("Trapped Chest"),
   BARREL("Barrel"),
   SHULKER("Shulker Box");
   public final String displayName;

   public static ChestTrappedType getChestTrapped(Block block) {
      if (block instanceof TrappedChestBlock) {
         return TRAPPED_CHEST;
      } else if (block instanceof ChestBlock) {
         return CHEST;
      } else if (block instanceof BarrelBlock) {
         return BARREL;
      } else {
         return block instanceof ShulkerBoxBlock ? SHULKER : null;
      }
   }

   private ChestTrappedType(String chestText) {
      this.displayName = chestText;
   }
}
