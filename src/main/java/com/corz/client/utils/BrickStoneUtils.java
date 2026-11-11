package com.corz.client.utils;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BambooShootBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BulbBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.CaveVinesBodyBlock;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.CopperChestBlock;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.KelpBlock;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.PlayerSkullBlock;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.ShelfBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction.Axis;
import com.corz.client.utils.ExcavationCraftedType;

public final class BrickStoneUtils {
   private static final ConcurrentHashMap<Block, Optional<IntProperty>> AGE_PROPERTY_CACHE = new ConcurrentHashMap<>();
   static final BrickStoneUtils.Info FARM_SENSOR_INFO_OF = infoOf(ExcavationCraftedType.FARM, 1, 109);
   static final BrickStoneUtils.Info CRAFTED_SENSOR_INFO_OF = infoOf(ExcavationCraftedType.CRAFTED, 1, 99);
   static final BrickStoneUtils.Info UTILITY_SENSOR_INFO_OF = infoOf(ExcavationCraftedType.UTILITY, 2, 92);
   static final BrickStoneUtils.Info REDSTONE_SENSOR_INFO_OF = infoOf(ExcavationCraftedType.REDSTONE, 2, 13);
   static final BrickStoneUtils.Info FARM_SENSOR_INFO = infoOf(ExcavationCraftedType.FARM, 1, 105);
   static final BrickStoneUtils.Info CRAFTED_SENSOR_INFO = infoOf(ExcavationCraftedType.CRAFTED, 1, 65);
   static final BrickStoneUtils.Info CRAFTED_SENSOR_82 = infoOf(ExcavationCraftedType.CRAFTED, 1, 82);
   static final BrickStoneUtils.Info UTILITY_SENSOR_INFO = new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 3, 0, 29, 48, 0);
   private static final ConcurrentHashMap<Block, BrickStoneUtils.Info> BLOCK_INFO_CACHE = new ConcurrentHashMap<>();
   static final BrickStoneUtils.Info CRAFTED_SENSOR_98 = infoOf(ExcavationCraftedType.CRAFTED, 1, 98);
   static final BrickStoneUtils.Info CRAFTED_SENSOR_97 = infoOf(ExcavationCraftedType.CRAFTED, 1, 97);
   static final BrickStoneUtils.Info CRAFTED_SENSOR_91 = infoOf(ExcavationCraftedType.CRAFTED, 1, 91);
   private static final boolean[] STRUCTURE_SENSOR_FLAGS = new boolean[148];
   static final BrickStoneUtils.Info CRAFTED_SENSOR_75 = infoOf(ExcavationCraftedType.CRAFTED, 1, 75);
   static final BrickStoneUtils.Info FARM_SENSOR_110 = infoOf(ExcavationCraftedType.FARM, 1, 110);
   static final BrickStoneUtils.Info FARM_SENSOR_111 = infoOf(ExcavationCraftedType.FARM, 1, 111);
   static final BrickStoneUtils.Info FARM_SENSOR_106 = infoOf(ExcavationCraftedType.FARM, 1, 106);
   public static final int UNUSED_ONE = 1;
   static final BrickStoneUtils.Info FARM_SENSOR_107 = infoOf(ExcavationCraftedType.FARM, 1, 107);
   private static final String[] WOOD_TYPE_PREFIXES;
   static final BrickStoneUtils.Info CRAFTED_SENSOR_72 = infoOf(ExcavationCraftedType.CRAFTED, 1, 72);
   static final BrickStoneUtils.Info EMPTY_INFO = new BrickStoneUtils.Info(null, 0, 0, 0, 0, 0);
   static final BrickStoneUtils.Info FARM_SENSOR_112 = infoOf(ExcavationCraftedType.FARM, 2, 112);

   private static BrickStoneUtils.Info craftedInfo(int craftedValue, int brickDeepslateValue) {
      return new BrickStoneUtils.Info(ExcavationCraftedType.CRAFTED, 1, brickDeepslateValue, 0, craftedValue, 0);
   }

   public static int classifyBlockState(BlockState state) {
      if (state.isAir()) {
         return 0;
      } else if (state.isLiquid()) {
         return 1;
      } else {
         BrickStoneUtils.Info ifAbsent = getBlockInfo(state.getBlock());
         if (isPlayerCrafted(state, ifAbsent)) {
            return 3;
         } else {
            return isSolidWithInfo(state, ifAbsent) ? 2 : 4;
         }
      }
   }

   public static boolean isSolidForScan(BlockState state) {
      return isSolidWithInfo(state, getBlockInfo(state.getBlock()));
   }

   public static IntProperty getAgeProperty(Block block) {
      return AGE_PROPERTY_CACHE.computeIfAbsent(block, blk -> {
         for (Property property : blk.getDefaultState().getProperties()) {
            if (property instanceof IntProperty intProperty && "age".equals(intProperty.getName())) {
               return Optional.of(intProperty);
            }
         }

         return Optional.empty();
      }).orElse(null);
   }

   static boolean isPlayerCrafted(BlockState state, BrickStoneUtils.Info info) {
      return info.excavationCraftedType != null;
   }

   private static int axisToIndex(Axis axis) {
      switch (axis) {
         case X:
            return 0;
         case Y:
            return 1;
         default:
            return 2;
      }
   }

   private BrickStoneUtils() {
   }

   public static int getAxisIndex(BlockState state) {
      if (state.contains(Properties.AXIS)) {
         return axisToIndex(state.get(Properties.AXIS));
      } else {
         return state.contains(Properties.HORIZONTAL_AXIS) ? axisToIndex(state.get(Properties.HORIZONTAL_AXIS)) : -1;
      }
   }

   private static BrickStoneUtils.Info getUtilityBlockInfo(Block block, String utilityTableText) {
      if (block == Blocks.CRAFTING_TABLE) {
         return infoOf(ExcavationCraftedType.UTILITY, 4, 23);
      } else if (block == Blocks.STONECUTTER
         || block == Blocks.SMITHING_TABLE
         || block == Blocks.LOOM
         || block == Blocks.CARTOGRAPHY_TABLE
         || block == Blocks.FLETCHING_TABLE
         || block == Blocks.GRINDSTONE
         || block == Blocks.COMPOSTER) {
         return infoOf(ExcavationCraftedType.UTILITY, 4, 24);
      } else if (block == Blocks.ANVIL || block == Blocks.CHIPPED_ANVIL || block == Blocks.DAMAGED_ANVIL) {
         return infoOf(ExcavationCraftedType.UTILITY, 4, 25);
      } else if (block == Blocks.ENCHANTING_TABLE) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 6, 0, 10, 26, 0);
      } else if (block == Blocks.BREWING_STAND) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 5, 0, 9, 27, 0);
      } else if (block == Blocks.BEACON) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 10, 0, 11, 28, 0);
      } else if (block == Blocks.CONDUIT) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 8, 0, 19, 29, 0);
      } else if (block instanceof BedBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 8, 0, 12, 30, 0);
      } else if (block == Blocks.RESPAWN_ANCHOR || block == Blocks.LODESTONE) {
         return infoOf(ExcavationCraftedType.UTILITY, 8, 31);
      } else if (block == Blocks.LECTERN) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 15, 32, 0);
      } else if (block == Blocks.BOOKSHELF) {
         return infoOf(ExcavationCraftedType.UTILITY, 1, 32);
      } else if (block == Blocks.CHISELED_BOOKSHELF) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 21, 33, 0);
      } else if (block instanceof ShelfBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 24, 45, 0);
      } else if (block instanceof CampfireBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 18, block == Blocks.SOUL_CAMPFIRE ? 36 : 35, 0);
      } else if (block instanceof AbstractSignBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 13, 37, 0);
      } else if (block instanceof AbstractBannerBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 14, 38, 0);
      } else if (block == Blocks.DECORATED_POT) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 1, 0, 23, 39, 0);
      } else if (block instanceof AbstractCauldronBlock) {
         return infoOf(ExcavationCraftedType.UTILITY, 2, 40);
      } else if (block instanceof AbstractSkullBlock) {
         return !(block instanceof PlayerSkullBlock) && !utilityTableText.startsWith("player_")
            ? new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 1, 0, 28, 41, 0)
            : new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 3, 0, 28, 42, 0);
      } else if (block == Blocks.FLOWER_POT || utilityTableText.startsWith("potted_")) {
         return infoOf(ExcavationCraftedType.UTILITY, 1, 43);
      } else if (block == Blocks.BEEHIVE) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 3, 0, 27, 44, 0);
      } else if (block == Blocks.BEE_NEST) {
         return EMPTY_INFO;
      } else if (block instanceof CopperGolemStatueBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 3, 0, 26, 46, 0);
      } else if (block == Blocks.CAKE) {
         return infoOf(ExcavationCraftedType.UTILITY, 1, 47);
      } else if (block == Blocks.CREAKING_HEART) {
         return biomeOnlyInfo(16);
      } else {
         return block == Blocks.DRIED_GHAST ? biomeOnlyInfo(17) : null;
      }
   }

   private static BrickStoneUtils.Info getCraftedMaterialInfo(Block block, String brickDeepslateText) {
      int brickDeepslateValue = getShapeVariantIndex(block);
      if (block instanceof LadderBlock) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 95);
      } else if (block instanceof ScaffoldingBlock) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 96);
      } else if (block == Blocks.HAY_BLOCK) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 93);
      } else if (block == Blocks.DIRT_PATH) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 94);
      } else if (block instanceof CarpetBlock || brickDeepslateText.endsWith("_wool")) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 87);
      } else if (block instanceof ChainBlock) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 89);
      } else if (block == Blocks.IRON_BARS || block == Blocks.IRON_DOOR || block == Blocks.IRON_TRAPDOOR) {
         return craftedInfo(88, brickDeepslateValue);
      } else if (block == Blocks.OBSIDIAN) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.CRAFTED, 1, 0, 0, 91, 14);
      } else if (brickDeepslateText.contains("copper") && !brickDeepslateText.endsWith("_ore") && block != Blocks.RAW_COPPER_BLOCK) {
         return craftedInfo(74, brickDeepslateValue);
      } else if (brickDeepslateText.contains("tuff") && !brickDeepslateText.equals("tuff")) {
         return craftedInfo(73, brickDeepslateValue);
      } else if (brickDeepslateText.contains("deepslate")) {
         if (brickDeepslateText.contains("cobbled_deepslate")) {
            return craftedInfo(71, brickDeepslateValue);
         } else {
            return !brickDeepslateText.contains("deepslate_brick") && !brickDeepslateText.contains("deepslate_tile") && !brickDeepslateText.contains("polished_deepslate") && !brickDeepslateText.contains("chiseled_deepslate")
               ? EMPTY_INFO
               : craftedInfo(70, brickDeepslateValue);
         }
      } else if (brickDeepslateText.contains("blackstone")) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.CRAFTED, 1, brickDeepslateValue, 0, 75, brickDeepslateText.equals("blackstone") ? 15 : 0);
      } else if (brickDeepslateText.contains("nether_brick")) {
         return craftedInfo(76, brickDeepslateValue);
      } else if (brickDeepslateText.contains("purpur") || brickDeepslateText.contains("end_stone_brick")) {
         return craftedInfo(79, brickDeepslateValue);
      } else if (brickDeepslateText.contains("prismarine")) {
         return craftedInfo(80, brickDeepslateValue);
      } else if (brickDeepslateText.contains("quartz") && !brickDeepslateText.endsWith("_ore")) {
         return craftedInfo(78, brickDeepslateValue);
      } else if (brickDeepslateText.contains("sandstone")) {
         return !brickDeepslateText.equals("sandstone") && !brickDeepslateText.equals("red_sandstone") ? craftedInfo(81, brickDeepslateValue) : EMPTY_INFO;
      } else if (brickDeepslateText.contains("mud_brick") || brickDeepslateText.equals("packed_mud")) {
         return craftedInfo(69, brickDeepslateValue);
      } else if (brickDeepslateText.equals("bricks") || brickDeepslateText.startsWith("brick_")) {
         return craftedInfo(68, brickDeepslateValue);
      } else if (brickDeepslateText.contains("stone_brick")) {
         return craftedInfo(66, brickDeepslateValue);
      } else if (brickDeepslateText.contains("cobblestone")) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.CRAFTED, 1, brickDeepslateValue, 0, 65, brickDeepslateText.equals("mossy_cobblestone") ? 18 : 0);
      } else if (brickDeepslateText.contains("polished_basalt")) {
         return craftedInfo(77, brickDeepslateValue);
      } else if (brickDeepslateText.contains("glazed_terracotta")) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 83);
      } else if (brickDeepslateText.contains("terracotta")) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.CRAFTED, 1, 0, 0, 82, 13);
      } else if (brickDeepslateText.contains("concrete")) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 84);
      } else if (brickDeepslateText.equals("tinted_glass")) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 86);
      } else if (brickDeepslateText.contains("glass")) {
         return craftedInfo(85, brickDeepslateValue);
      } else if (brickDeepslateText.contains("resin") && !brickDeepslateText.equals("resin_clump")) {
         return craftedInfo(92, brickDeepslateValue);
      } else if (!brickDeepslateText.equals("honeycomb_block") && !brickDeepslateText.equals("dried_kelp_block")) {
         String brickDeepslateText2 = brickDeepslateText.startsWith("stripped_") ? brickDeepslateText.substring("stripped_".length()) : brickDeepslateText;

         for (String woodTypePrefixesItem : WOOD_TYPE_PREFIXES) {
            if (brickDeepslateText2.startsWith(woodTypePrefixesItem)) {
               boolean brickDeepslateFlag = brickDeepslateText2.endsWith("_planks") || brickDeepslateValue != 0 || brickDeepslateText.startsWith("stripped_") || brickDeepslateText2.contains("_mosaic") || brickDeepslateText2.equals("bamboo_block");
               if (brickDeepslateFlag) {
                  return craftedInfo(brickDeepslateText2.startsWith("dark_oak_") ? 64 : 63, brickDeepslateValue);
               }
               break;
            }
         }

         if (!brickDeepslateText.equals("smooth_stone")
            && !brickDeepslateText.equals("smooth_stone_slab")
            && !brickDeepslateText.equals("stone_slab")
            && !brickDeepslateText.equals("stone_stairs")
            && (!brickDeepslateText.contains("andesite") && !brickDeepslateText.contains("diorite") && !brickDeepslateText.contains("granite") || !brickDeepslateText.startsWith("polished_") && brickDeepslateValue == 0)) {
            if (block == Blocks.IRON_BLOCK
               || block == Blocks.GOLD_BLOCK
               || block == Blocks.DIAMOND_BLOCK
               || block == Blocks.EMERALD_BLOCK
               || block == Blocks.NETHERITE_BLOCK
               || block == Blocks.LAPIS_BLOCK
               || block == Blocks.COAL_BLOCK) {
               return infoOf(ExcavationCraftedType.CRAFTED, 1, 90);
            } else {
               return brickDeepslateValue != 0 ? craftedInfo(67, brickDeepslateValue) : EMPTY_INFO;
            }
         } else {
            return craftedInfo(67, brickDeepslateValue);
         }
      } else {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 92);
      }
   }

   private static BrickStoneUtils.Info infoOf(ExcavationCraftedType excavationCraftedType, int classValue, int classValue2) {
      return new BrickStoneUtils.Info(excavationCraftedType, classValue, 0, 0, classValue2, 0);
   }

   private static BrickStoneUtils.Info sensorOnlyInfo(int classValue) {
      return new BrickStoneUtils.Info(null, 0, 0, 0, classValue, 0);
   }

   public static boolean isFullyGrown(BlockState state) {
      IntProperty orElse = getAgeProperty(state.getBlock());
      if (orElse == null) {
         return false;
      } else {
         int blockLimit = state.get(orElse);
         return blockLimit >= Collections.max(orElse.getValues());
      }
   }

   private static BrickStoneUtils.Info biomeOnlyInfo(int classValue) {
      return new BrickStoneUtils.Info(null, 0, 0, 0, 0, classValue);
   }

   public static boolean isLit(BlockState state) {
      return state.contains(Properties.LIT) && state.get(Properties.LIT);
   }

   static boolean isSolidWithInfo(BlockState state, BrickStoneUtils.Info info) {
      if (!state.isOpaqueFullCube()) {
         return false;
      } else if (info == EMPTY_INFO) {
         return true;
      } else {
         switch (info.sensorId) {
            case 114:
            case 115:
            case 125:
            case 126:
            case 129:
            case 130:
            case 145:
               return true;
            default:
               return info.biomeCraftedValue == 19 && (!state.contains(Properties.AXIS) || state.get(Properties.AXIS) == Axis.Y);
         }
      }
   }

   public static BrickStoneUtils.Info getBlockInfo(Block block) {
      return BLOCK_INFO_CACHE.computeIfAbsent(block, BrickStoneUtils::computeBlockInfo);
   }

   static {
      int[] ints = new int[]{113, 116, 117, 119, 120, 121, 122, 123, 124, 127, 128};

      for (int index : ints) {
         STRUCTURE_SENSOR_FLAGS[index] = true;
      }

      WOOD_TYPE_PREFIXES = new String[]{
         "oak_", "spruce_", "birch_", "jungle_", "acacia_", "dark_oak_", "mangrove_", "cherry_", "pale_oak_", "bamboo_", "crimson_", "warped_"
      };
   }

   public static int getFacingIndex(BlockState state) {
      if (state.contains(Properties.FACING)) {
         return state.get(Properties.FACING).ordinal();
      } else if (state.contains(Properties.HORIZONTAL_FACING)) {
         return state.get(Properties.HORIZONTAL_FACING).ordinal();
      } else {
         return state.contains(Properties.HOPPER_FACING) ? state.get(Properties.HOPPER_FACING).ordinal() : -1;
      }
   }

   private static int getShapeVariantIndex(Block block) {
      if (block instanceof StairsBlock) {
         return 1;
      } else if (block instanceof SlabBlock) {
         return 2;
      } else if (block instanceof WallBlock) {
         return 3;
      } else if (block instanceof DoorBlock) {
         return 4;
      } else if (block instanceof TrapdoorBlock) {
         return 5;
      } else if (block instanceof PaneBlock) {
         return 6;
      } else {
         return !(block instanceof FenceBlock) && !(block instanceof FenceGateBlock) ? 0 : 7;
      }
   }

   public static boolean isPlacedStructureBlock(BlockState blockState) {
      BrickStoneUtils.Info ifAbsent = getBlockInfo(blockState.getBlock());
      if (ifAbsent.excavationCraftedType != null) {
         return true;
      } else if (ifAbsent.biomeCraftedValue == 19) {
         return blockState.contains(Properties.AXIS) && blockState.get(Properties.AXIS) != Axis.Y;
      } else {
         return ifAbsent.biomeCraftedValue != 0 ? true : ifAbsent.sensorId != 0 && STRUCTURE_SENSOR_FLAGS[ifAbsent.sensorId];
      }
   }

   private static BrickStoneUtils.Info computeBlockInfo(Block block) {
      String path = Registries.BLOCK.getId(block).getPath();
      if (path.endsWith("_ore") || block == Blocks.ANCIENT_DEBRIS) {
         return sensorOnlyInfo(145);
      } else if (block == Blocks.TRIAL_SPAWNER || block == Blocks.VAULT) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.TRIAL, 12, 0, 0, 120, 0);
      } else if (block == Blocks.HEAVY_CORE) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.TRIAL, 4, 0, 0, 121, 0);
      } else if (block == Blocks.REINFORCED_DEEPSLATE) {
         return sensorOnlyInfo(116);
      } else if (block == Blocks.END_PORTAL_FRAME) {
         return sensorOnlyInfo(119);
      } else if (block == Blocks.BUDDING_AMETHYST) {
         return sensorOnlyInfo(113);
      } else if (block == Blocks.CALCITE) {
         return sensorOnlyInfo(114);
      } else if (block == Blocks.SMOOTH_BASALT) {
         return sensorOnlyInfo(115);
      } else if (block == Blocks.COBWEB) {
         return sensorOnlyInfo(122);
      } else if (block == Blocks.GILDED_BLACKSTONE) {
         return sensorOnlyInfo(123);
      } else if (block == Blocks.CRYING_OBSIDIAN) {
         return sensorOnlyInfo(124);
      } else if (block == Blocks.SUSPICIOUS_SAND || block == Blocks.SUSPICIOUS_GRAVEL) {
         return sensorOnlyInfo(127);
      } else if (block instanceof InfestedBlock) {
         return path.contains("brick") ? sensorOnlyInfo(128) : EMPTY_INFO;
      } else if (block == Blocks.NETHERRACK) {
         return sensorOnlyInfo(125);
      } else if (block == Blocks.MAGMA_BLOCK) {
         return sensorOnlyInfo(126);
      } else if (block == Blocks.SNOW_BLOCK) {
         return sensorOnlyInfo(129);
      } else if (block == Blocks.POINTED_DRIPSTONE || block == Blocks.DRIPSTONE_BLOCK) {
         return sensorOnlyInfo(130);
      } else if (block == Blocks.SPAWNER) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.SPAWNER, 0, 0, 30, 49, 20);
      } else if (block == Blocks.SPONGE || block == Blocks.WET_SPONGE) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 100);
      } else if (block == Blocks.BELL) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 17, 34, 0);
      } else if (block == Blocks.DEEPSLATE) {
         return biomeOnlyInfo(19);
      } else if (block == Blocks.SCULK || block == Blocks.SCULK_VEIN) {
         return biomeOnlyInfo(2);
      } else if (block == Blocks.CALIBRATED_SCULK_SENSOR) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 3, 0, 31, 14, 0);
      } else if (block == Blocks.SCULK_SENSOR) {
         return biomeOnlyInfo(3);
      } else if (block == Blocks.SCULK_SHRIEKER || block == Blocks.SCULK_CATALYST) {
         return biomeOnlyInfo(4);
      } else if (path.endsWith("shulker_box")) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 8, 0, 3, 3, 0);
      } else if (block instanceof CopperChestBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 5, 11, 25, 5, 0);
      } else if (block instanceof ChestBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 5, 11, 1, 1, 0);
      } else if (block == Blocks.ENDER_CHEST) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 4, 11, 4, 4, 0);
      } else if (block == Blocks.BARREL) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 4, 0, 2, 2, 0);
      } else if (block instanceof AbstractFurnaceBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.STORAGE, 3, 0, 5, 6, 22);
      } else if (block instanceof HopperBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 3, 8, 6, 7, 0);
      } else if (block == Blocks.DISPENSER) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 3, 0, 7, 8, 0);
      } else if (block == Blocks.DROPPER) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 3, 0, 8, 9, 0);
      } else if (block == Blocks.CRAFTER) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 3, 0, 22, 15, 0);
      } else if (block == Blocks.PISTON_HEAD || block == Blocks.MOVING_PISTON) {
         return EMPTY_INFO;
      } else if (block instanceof PistonBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 2, 10, 0, 10, 0);
      } else if (block instanceof ObserverBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 2, 9, 0, 11, 0);
      } else if (block instanceof RepeaterBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 2, 12, 0, 11, 0);
      } else if (block instanceof ComparatorBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.REDSTONE, 2, 0, 0, 11, 0);
      } else if (block instanceof RedstoneWireBlock || block instanceof RedstoneTorchBlock || block == Blocks.REDSTONE_BLOCK || block == Blocks.DAYLIGHT_DETECTOR) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 11);
      } else if (block == Blocks.TARGET) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 19);
      } else if (block instanceof LeverBlock
         || block instanceof ButtonBlock
         || block instanceof AbstractPressurePlateBlock
         || block == Blocks.TRIPWIRE_HOOK
         || block == Blocks.TRIPWIRE) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 12);
      } else if (block == Blocks.TNT) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 20);
      } else if (block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 21);
      } else if (block == Blocks.NOTE_BLOCK) {
         return infoOf(ExcavationCraftedType.REDSTONE, 1, 22);
      } else if (block == Blocks.JUKEBOX) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.UTILITY, 2, 0, 16, 22, 0);
      } else if (block instanceof LightningRodBlock) {
         return infoOf(ExcavationCraftedType.REDSTONE, 2, 16);
      } else if (block instanceof PoweredRailBlock || block instanceof DetectorRailBlock) {
         return infoOf(ExcavationCraftedType.REDSTONE, 2, 17);
      } else if (block instanceof AbstractRailBlock) {
         return infoOf(ExcavationCraftedType.CRAFTED, 1, 18);
      } else if (block == Blocks.GLOWSTONE) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 3, 0, 0, 54, 1);
      } else if (block == Blocks.SHROOMLIGHT) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 3, 0, 0, 56, 1);
      } else if (path.endsWith("froglight")) {
         return infoOf(ExcavationCraftedType.LIGHTING, 3, 57);
      } else if (block == Blocks.END_ROD) {
         return infoOf(ExcavationCraftedType.LIGHTING, 3, 58);
      } else if (block == Blocks.REDSTONE_LAMP) {
         return infoOf(ExcavationCraftedType.LIGHTING, 3, 61);
      } else if (block == Blocks.SEA_LANTERN) {
         return infoOf(ExcavationCraftedType.LIGHTING, 3, 55);
      } else if (block instanceof BulbBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 3, 0, 0, 60, 23);
      } else if (block instanceof LanternBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 3, 13, 0, block == Blocks.SOUL_LANTERN ? 52 : 53, 0);
      } else if (block instanceof TorchBlock || path.endsWith("torch") && !path.contains("redstone")) {
         int redstoneLightingValue = path.startsWith("soul_") ? 52 : (path.startsWith("copper_") ? 51 : 50);
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 2, 14, 0, redstoneLightingValue, 0);
      } else if (block instanceof CandleCakeBlock) {
         return infoOf(ExcavationCraftedType.UTILITY, 2, 47);
      } else if (block instanceof AbstractCandleBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.LIGHTING, 1, 0, 0, 59, 24);
      } else if (block == Blocks.JACK_O_LANTERN) {
         return infoOf(ExcavationCraftedType.LIGHTING, 2, 62);
      } else {
         BrickStoneUtils.Info utilityTable = getUtilityBlockInfo(block, path);
         if (utilityTable != null) {
            return utilityTable;
         } else {
            BrickStoneUtils.Info farm = getFarmBlockInfo(block);
            if (farm != null) {
               return farm;
            } else if (block == Blocks.MOSS_BLOCK
               || block == Blocks.MOSS_CARPET
               || block == Blocks.SPORE_BLOSSOM
               || block == Blocks.BIG_DRIPLEAF
               || block == Blocks.BIG_DRIPLEAF_STEM
               || block == Blocks.SMALL_DRIPLEAF) {
               return biomeOnlyInfo(5);
            } else if (block == Blocks.PALE_MOSS_BLOCK || block == Blocks.PALE_MOSS_CARPET || block == Blocks.PALE_HANGING_MOSS) {
               return EMPTY_INFO;
            } else if (block == Blocks.AMETHYST_CLUSTER || block == Blocks.LARGE_AMETHYST_BUD || block == Blocks.MEDIUM_AMETHYST_BUD || block == Blocks.SMALL_AMETHYST_BUD) {
               return biomeOnlyInfo(6);
            } else {
               return block == Blocks.AMETHYST_BLOCK ? infoOf(ExcavationCraftedType.CRAFTED, 1, 98) : getCraftedMaterialInfo(block, path);
            }
         }
      }
   }

   private static BrickStoneUtils.Info getFarmBlockInfo(Block block) {
      if (block instanceof FarmlandBlock) {
         return infoOf(ExcavationCraftedType.FARM, 1, 101);
      } else if (block instanceof CropBlock || block instanceof PitcherCropBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.FARM, 1, 0, 0, 102, 21);
      } else if (block instanceof NetherWartBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.FARM, 1, 0, 0, 108, 21);
      } else if (block instanceof AttachedStemBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.FARM, 1, 0, 0, 103, 25);
      } else if (block instanceof StemBlock) {
         return new BrickStoneUtils.Info(ExcavationCraftedType.FARM, 1, 0, 0, 103, 21);
      } else if (block instanceof SugarCaneBlock) {
         return infoOf(ExcavationCraftedType.FARM, 1, 104);
      } else if (block instanceof CactusBlock) {
         return biomeOnlyInfo(7);
      } else if (block instanceof BambooBlock || block instanceof BambooShootBlock) {
         return biomeOnlyInfo(8);
      } else if (block instanceof KelpBlock || block instanceof KelpPlantBlock) {
         return biomeOnlyInfo(9);
      } else if (block instanceof CocoaBlock) {
         return biomeOnlyInfo(10);
      } else if (block instanceof SweetBerryBushBlock) {
         return biomeOnlyInfo(11);
      } else {
         return !(block instanceof CaveVinesHeadBlock) && !(block instanceof CaveVinesBodyBlock) ? null : biomeOnlyInfo(12);
      }
   }

   public static final class Info {
      public final ExcavationCraftedType excavationCraftedType;
      public final int sensorId;
      public final int craftedExcavationValue;
      public final int biomeCraftedValue;
      public final int variantIndex;
      public final int priorityWeight;

      Info(ExcavationCraftedType excavationCraftedType, int value, int value2, int value3, int value4, int value5) {
         this.excavationCraftedType = excavationCraftedType;
         this.priorityWeight = value;
         this.variantIndex = value2;
         this.craftedExcavationValue = value3;
         this.sensorId = value4;
         this.biomeCraftedValue = value5;
      }
   }
}
