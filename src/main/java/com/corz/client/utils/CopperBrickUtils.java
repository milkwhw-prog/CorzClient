package com.corz.client.utils;

import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EmptyBlockView;
import com.corz.client.utils.ConstCopperUtils;

public final class CopperBrickUtils {
   private static final Reference2ByteOpenHashMap<BlockState> REFERENCE2_BYTE_OPEN_HASH_MA = new Reference2ByteOpenHashMap();
   private static final byte NO_REFERENCE = -1;

   private static boolean canChestCopper(String chestCopperText) {
      return chestCopperText.equals("chest")
         || chestCopperText.equals("trapped_chest")
         || chestCopperText.equals("barrel")
         || chestCopperText.equals("ender_chest")
         || chestCopperText.endsWith("shulker_box")
         || chestCopperText.endsWith("copper_chest");
   }

   private CopperBrickUtils() {
   }

   static {
      REFERENCE2_BYTE_OPEN_HASH_MA.defaultReturnValue((byte)-1);
   }

   private static byte getOakRail(BlockState blockState) {
      if (blockState.isAir()) {
         return ConstCopperUtils.apiValue;
      } else {
         Block block = blockState.getBlock();

         String oakRailText;
         try {
            oakRailText = Registries.BLOCK.getId(block).getPath();
         } catch (Throwable e) {
            oakRailText = "";
         }

         VoxelShape voxelShape;
         try {
            voxelShape = blockState.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
         } catch (Throwable e2) {
            voxelShape = null;
         }

         boolean oakRailFlag = voxelShape != null && voxelShape.isEmpty();
         boolean oakRailFlag2 = voxelShape != null && !voxelShape.isEmpty() && voxelShape.getMax(Axis.Y) <= 0.1875;
         if (block == Blocks.COBWEB) {
            return ConstCopperUtils.getValue(1, 6);
         } else if (oakRailText.endsWith("rail")) {
            return ConstCopperUtils.getValue(1, 5);
         } else if (blockState.getFluidState().isEmpty() || !oakRailFlag && !oakRailFlag2 && block != Blocks.WATER && block != Blocks.LAVA) {
            if (canLanternRod(oakRailText)) {
               return ConstCopperUtils.getValue(1, canCopperBulb(oakRailText) ? 7 : 9);
            } else if (oakRailFlag || oakRailFlag2) {
               return ConstCopperUtils.getValue(1, getCarpetBanner(oakRailText));
            } else if (block == Blocks.SPAWNER) {
               return ConstCopperUtils.getValue(2, 2);
            } else if (oakRailText.startsWith("mossy_cobblestone")) {
               return ConstCopperUtils.getValue(2, 1);
            } else if (block == Blocks.OAK_PLANKS || block == Blocks.DARK_OAK_PLANKS) {
               return ConstCopperUtils.getValue(2, 3);
            } else if (block == Blocks.OAK_FENCE || block == Blocks.DARK_OAK_FENCE) {
               return ConstCopperUtils.getValue(2, 4);
            } else if (canBrickDeepslate(oakRailText)) {
               return ConstCopperUtils.getValue(2, 7);
            } else if (canChestCopper(oakRailText)) {
               return ConstCopperUtils.getValue(2, 10);
            } else if (canMossRooted(oakRailText)) {
               return ConstCopperUtils.getValue(2, 8);
            } else if (canDeepslateCobblestone(block, blockState, oakRailText)) {
               return ConstCopperUtils.getValue(2, 11);
            } else {
               return canTableBlock(oakRailText) ? ConstCopperUtils.getValue(2, 9) : ConstCopperUtils.DEFAULT_TYPE_CODE;
            }
         } else {
            return ConstCopperUtils.railValue;
         }
      }
   }

   private static boolean canCopperBulb(String copperBulbText) {
      return !copperBulbText.endsWith("_ore") && !copperBulbText.startsWith("raw_") && copperBulbText.contains("copper")
         ? copperBulbText.contains("cut_copper")
            || copperBulbText.contains("copper_grate")
            || copperBulbText.contains("copper_bulb")
            || copperBulbText.contains("chiseled_copper")
            || copperBulbText.endsWith("copper_block")
            || copperBulbText.endsWith("exposed_copper")
            || copperBulbText.endsWith("weathered_copper")
            || copperBulbText.endsWith("oxidized_copper")
            || copperBulbText.endsWith("copper_door")
            || copperBulbText.endsWith("copper_trapdoor")
         : false;
   }

   private static boolean canLanternRod(String lanternRodText) {
      return lanternRodText.equals("ladder")
         || lanternRodText.equals("scaffolding")
         || lanternRodText.endsWith("door")
         || lanternRodText.endsWith("fence_gate")
         || lanternRodText.equals("lantern")
         || lanternRodText.equals("soul_lantern")
         || lanternRodText.endsWith("copper_lantern")
         || lanternRodText.endsWith("candle")
         || lanternRodText.endsWith("chain")
         || lanternRodText.equals("end_rod")
         || lanternRodText.equals("lightning_rod")
         || lanternRodText.endsWith("torch")
         || lanternRodText.endsWith("flower_pot")
         || lanternRodText.startsWith("potted_");
   }

   private static boolean canDeepslateCobblestone(Block block, BlockState blockState, String deepslateCobblestoneText) {
      if (!deepslateCobblestoneText.equals("cobblestone") && !deepslateCobblestoneText.equals("cobbled_deepslate")) {
         return (block == Blocks.DEEPSLATE || block == Blocks.BASALT) && blockState.contains(Properties.AXIS) ? blockState.get(Properties.AXIS) != Axis.Y : false;
      } else {
         return true;
      }
   }

   private static boolean canTableBlock(String tableBlockText) {
      return tableBlockText.equals("crafting_table")
         || tableBlockText.endsWith("furnace")
         || tableBlockText.equals("smoker")
         || tableBlockText.contains("anvil")
         || tableBlockText.equals("enchanting_table")
         || tableBlockText.endsWith("obsidian")
         || tableBlockText.endsWith("_slab")
         || tableBlockText.endsWith("_stairs")
         || tableBlockText.contains("glass")
         || tableBlockText.endsWith("wool")
         || tableBlockText.endsWith("concrete")
         || tableBlockText.endsWith("_bed")
         || tableBlockText.equals("hopper")
         || tableBlockText.equals("dispenser")
         || tableBlockText.equals("dropper")
         || tableBlockText.equals("observer")
         || tableBlockText.endsWith("piston")
         || tableBlockText.equals("note_block")
         || tableBlockText.equals("jukebox")
         || tableBlockText.equals("beacon")
         || tableBlockText.endsWith("_planks")
         || tableBlockText.equals("lectern")
         || tableBlockText.endsWith("_table")
         || tableBlockText.equals("loom")
         || tableBlockText.equals("grindstone")
         || tableBlockText.equals("stonecutter")
         || tableBlockText.equals("brewing_stand")
         || tableBlockText.contains("cauldron")
         || tableBlockText.equals("composter")
         || tableBlockText.equals("respawn_anchor")
         || tableBlockText.equals("lodestone")
         || tableBlockText.equals("bricks")
         || tableBlockText.equals("bookshelf")
         || tableBlockText.equals("iron_block")
         || tableBlockText.equals("gold_block")
         || tableBlockText.equals("diamond_block")
         || tableBlockText.equals("emerald_block")
         || tableBlockText.equals("netherite_block")
         || tableBlockText.endsWith("_wall")
         || tableBlockText.endsWith("fence");
   }

   private static boolean canMossRooted(String mossRootedText) {
      return mossRootedText.contains("dripstone")
         || mossRootedText.equals("moss_block")
         || mossRootedText.equals("pale_moss_block")
         || mossRootedText.equals("rooted_dirt")
         || mossRootedText.equals("clay")
         || mossRootedText.contains("azalea")
         || mossRootedText.contains("dripleaf")
         || mossRootedText.equals("magma_block");
   }

   private static boolean canBrickDeepslate(String brickDeepslateText) {
      return brickDeepslateText.contains("stone_brick")
         || brickDeepslateText.contains("deepslate_brick")
         || brickDeepslateText.contains("deepslate_tile")
         || brickDeepslateText.contains("tuff_brick")
         || brickDeepslateText.startsWith("polished_tuff")
         || brickDeepslateText.startsWith("chiseled_tuff")
         || brickDeepslateText.equals("chiseled_deepslate")
         || brickDeepslateText.equals("reinforced_deepslate")
         || brickDeepslateText.contains("sculk")
         || brickDeepslateText.equals("end_portal_frame")
         || brickDeepslateText.contains("amethyst")
         || brickDeepslateText.equals("calcite")
         || brickDeepslateText.equals("smooth_basalt")
         || brickDeepslateText.startsWith("infested_")
         || brickDeepslateText.equals("iron_bars")
         || brickDeepslateText.equals("trial_spawner")
         || brickDeepslateText.equals("vault")
         || brickDeepslateText.contains("prismarine")
         || brickDeepslateText.contains("purpur")
         || brickDeepslateText.contains("nether_brick") && !brickDeepslateText.startsWith("red_")
         || brickDeepslateText.startsWith("polished_blackstone_brick")
         || brickDeepslateText.equals("gilded_blackstone")
         || brickDeepslateText.startsWith("suspicious_")
         || brickDeepslateText.equals("chiseled_sandstone")
         || brickDeepslateText.equals("blue_terracotta")
         || brickDeepslateText.equals("tnt")
         || canCopperBulb(brickDeepslateText);
   }

   private static int getCarpetBanner(String carpetBannerText) {
      if (carpetBannerText.contains("sculk")) {
         return 7;
      } else if (carpetBannerText.contains("lichen")
         || carpetBannerText.contains("vine")
         || carpetBannerText.equals("hanging_roots")
         || carpetBannerText.equals("spore_blossom")
         || carpetBannerText.contains("moss_carpet")
         || carpetBannerText.contains("dripleaf")
         || carpetBannerText.contains("dripstone")) {
         return 8;
      } else {
         return !carpetBannerText.endsWith("torch")
               && !carpetBannerText.contains("sign")
               && !carpetBannerText.contains("banner")
               && !carpetBannerText.endsWith("button")
               && !carpetBannerText.equals("lever")
               && !carpetBannerText.endsWith("pressure_plate")
               && !carpetBannerText.endsWith("carpet")
               && !carpetBannerText.equals("redstone_wire")
               && !carpetBannerText.equals("repeater")
               && !carpetBannerText.equals("comparator")
               && !carpetBannerText.startsWith("tripwire")
               && !carpetBannerText.endsWith("_bed")
            ? 0
            : 9;
      }
   }

   public static byte getByteReference(BlockState blockState) {
      byte byte2 = REFERENCE2_BYTE_OPEN_HASH_MA.getByte(blockState);
      if (byte2 == -1) {
         byte2 = getOakRail(blockState);
         REFERENCE2_BYTE_OPEN_HASH_MA.put(blockState, byte2);
      }

      return byte2;
   }
}
