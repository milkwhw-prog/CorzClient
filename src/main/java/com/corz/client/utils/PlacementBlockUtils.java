package com.corz.client.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public final class PlacementBlockUtils {
   public static BlockState getPlacementState(PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult, Block block) {
      if (playerEntity == null || itemStack == null || blockHitResult == null || block == null) {
         return null;
      } else if (!(itemStack.getItem() instanceof BlockItem)) {
         return null;
      } else {
         ItemPlacementContext itemPlacementContext = new ItemPlacementContext(playerEntity, hand, itemStack, blockHitResult);
         return block.getPlacementState(itemPlacementContext);
      }
   }

   public static BlockState getPlacementState(PlayerEntity playerEntity, Hand hand, ItemStack itemStack, BlockHitResult blockHitResult) {
      if (playerEntity != null && itemStack != null && blockHitResult != null) {
         if (itemStack.getItem() instanceof BlockItem blockItem) {
            ItemPlacementContext itemPlacementContext = new ItemPlacementContext(playerEntity, hand, itemStack, blockHitResult);
            Block block = blockItem.getBlock();
            return block.getPlacementState(itemPlacementContext);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private PlacementBlockUtils() {
   }
}
