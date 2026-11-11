package com.corz.client.utils;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Falling;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;

public final class MinecraftOreUtils {
   public static final Set<String> MINECRAFT_TABLE_SET = Set.of(
      "minecraft:crafting_table",
      "minecraft:cartography_table",
      "minecraft:fletching_table",
      "minecraft:smithing_table",
      "minecraft:loom",
      "minecraft:note_block",
      "minecraft:respawn_anchor",
      "minecraft:redstone_ore",
      "minecraft:deepslate_redstone_ore",
      "minecraft:tnt"
   );
   public static final Set<String> MINECRAFT_ORE_SET = Set.of(
      "minecraft:slime_block",
      "minecraft:honey_block",
      "minecraft:ice",
      "minecraft:packed_ice",
      "minecraft:blue_ice",
      "minecraft:magma_block",
      "minecraft:redstone_block",
      "minecraft:observer",
      "minecraft:piston",
      "minecraft:sticky_piston",
      "minecraft:melon",
      "minecraft:chorus_flower",
      "minecraft:powder_snow_bucket",
      "minecraft:beacon",
      "minecraft:spawner",
      "minecraft:enchanting_table",
      "minecraft:jukebox",
      "minecraft:command_block",
      "minecraft:chain_command_block",
      "minecraft:repeating_command_block",
      "minecraft:coal_ore",
      "minecraft:deepslate_coal_ore",
      "minecraft:iron_ore",
      "minecraft:deepslate_iron_ore",
      "minecraft:copper_ore",
      "minecraft:deepslate_copper_ore",
      "minecraft:gold_ore",
      "minecraft:deepslate_gold_ore",
      "minecraft:lapis_ore",
      "minecraft:deepslate_lapis_ore",
      "minecraft:diamond_ore",
      "minecraft:deepslate_diamond_ore",
      "minecraft:emerald_ore",
      "minecraft:deepslate_emerald_ore",
      "minecraft:nether_gold_ore",
      "minecraft:nether_quartz_ore",
      "minecraft:ancient_debris"
   );

   private MinecraftOreUtils() {
   }

   public static boolean canMinecraftTable(String minecraftTableText) {
      return minecraftTableText != null && (MINECRAFT_ORE_SET.contains(minecraftTableText) || MINECRAFT_TABLE_SET.contains(minecraftTableText));
   }

   public static boolean canEmpty(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() && itemStack.getItem() instanceof BlockItem;
   }

   public static boolean isFullCube(ItemStack itemStack) {
      if (!canEmpty(itemStack)) {
         return false;
      } else if (canMinecraftTable(Registries.ITEM.getId(itemStack.getItem()).toString())) {
         return false;
      } else {
         Block block = ((BlockItem)itemStack.getItem()).getBlock();
         return !(block instanceof Falling) && !(block instanceof BlockEntityProvider)
            ? block.getDefaultState().isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)
            : false;
      }
   }

   public static boolean canCubeTable(BlockState blockState, BlockView blockView, BlockPos pos2) {
      if (blockState != null && !blockState.isAir()) {
         Block block = blockState.getBlock();
         if (block instanceof BlockEntityProvider) {
            return false;
         } else {
            return MINECRAFT_TABLE_SET.contains(Registries.BLOCK.getId(block).toString()) ? false : blockState.isFullCube(blockView, pos2);
         }
      } else {
         return false;
      }
   }
}
