package com.corz.client.utils;

import com.corz.client.inventory.StackChestplateType;
import com.corz.client.inventory.StackEnchantmentsUtils;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;

public enum HotbarItemCategory {
   NONE("None", null),
   SWORD("Sword", StackChestplateType.SWORD),
   AXE("Axe", StackChestplateType.AXE),
   MACE("Mace", StackChestplateType.MACE),
   SPEAR("Spear", StackChestplateType.SPEAR),
   TRIDENT("Trident", StackChestplateType.TRIDENT),
   PICKAXE("Pickaxe", StackChestplateType.PICKAXE),
   SHOVEL("Shovel", StackChestplateType.SHOVEL),
   BOW("Bow", StackChestplateType.BOW),
   CROSSBOW("Crossbow", StackChestplateType.CROSSBOW),
   BLOCKS("Blocks", null),
   OBSIDIAN("Obsidian", null),
   END_CRYSTAL("End Crystal", null),
   RESPAWN_ANCHOR("Respawn Anchor", null),
   GLOWSTONE("Glowstone", null),
   COBWEB("Cobweb", null),
   GOLDEN_APPLE("Golden Apple", null),
   FOOD("Food", null),
   ENDER_PEARL("Ender Pearl", null),
   WIND_CHARGE("Wind Charge", null),
   WATER_BUCKET("Water Bucket", null),
   LAVA_BUCKET("Lava Bucket", null),
   HEALING_POTION("Healing Potion", null),
   FIREWORK("Firework", null),
   FISHING_ROD("Fishing Rod", null),
   SNOWBALL("Snowball", null),
   FLINT_AND_STEEL("Flint and Steel", null),
   XP_BOTTLE("XP Bottle", null);
   private final StackChestplateType toolType;
   private final String displayName;
   private static final Set<String> EXCLUDED_BLOCK_IDS = Set.of(
      "obsidian",
      "crying_obsidian",
      "respawn_anchor",
      "glowstone",
      "tnt",
      "slime_block",
      "magma_block",
      "diamond_block",
      "emerald_block",
      "netherite_block",
      "gold_block",
      "iron_block",
      "lapis_block",
      "redstone_block",
      "coal_block",
      "copper_block",
      "raw_gold_block",
      "raw_iron_block",
      "raw_copper_block",
      "ancient_debris",
      "budding_amethyst",
      "sponge",
      "wet_sponge",
      "bedrock",
      "reinforced_deepslate"
   );

   private HotbarItemCategory(String text, StackChestplateType stackChestplateType) {
      this.displayName = text;
      this.toolType = stackChestplateType;
   }

   public static HotbarItemCategory fromDisplayName(String noneText) {
      for (HotbarItemCategory noneText2 : values()) {
         if (noneText2.displayName.equals(noneText)) {
            return noneText2;
         }
      }

      return NONE;
   }

   public StackChestplateType getToolType() {
      return this.toolType;
   }

   static boolean isPlainBuildingBlock(ItemStack stack) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         if (block instanceof FallingBlock) {
            return false;
         } else {
            String path = Registries.ITEM.getId(stack.getItem()).getPath();
            if (!path.endsWith("_ore") && !EXCLUDED_BLOCK_IDS.contains(path)) {
               BlockState defaultState = block.getDefaultState();
               return !defaultState.hasBlockEntity() && !(block.getSlipperiness() > 0.6F) ? defaultState.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN) : false;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public double getItemScore(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return 0.0;
      } else if (this.toolType != null) {
         return StackEnchantmentsUtils.getUnbreakingMending(stack, this.toolType);
      } else {
         return switch (this) {
            case GOLDEN_APPLE -> stack.isOf(Items.ENCHANTED_GOLDEN_APPLE) ? 2.0 : 1.0;
            case FOOD -> StackEnchantmentsUtils.getSaturationNutrition(stack);
            default -> 0.0;
            case HEALING_POTION -> StackEnchantmentsUtils.getPotionEffects(stack);
            case FISHING_ROD, FLINT_AND_STEEL -> StackEnchantmentsUtils.getAmount(stack);
         };
      }
   }

   public boolean matchesStack(ItemStack stack) {
      if (stack == null || stack.isEmpty() || this == NONE) {
         return false;
      } else if (this.toolType != null) {
         return StackChestplateType.getArmorCrossbow(stack) == this.toolType;
      } else {
         return switch (this) {
            case BLOCKS -> isPlainBuildingBlock(stack);
            case OBSIDIAN -> stack.isOf(Items.OBSIDIAN);
            case END_CRYSTAL -> stack.isOf(Items.END_CRYSTAL);
            case RESPAWN_ANCHOR -> stack.isOf(Items.RESPAWN_ANCHOR);
            case GLOWSTONE -> stack.isOf(Items.GLOWSTONE);
            case COBWEB -> stack.isOf(Items.COBWEB);
            case GOLDEN_APPLE -> stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
            case FOOD -> isSafeFood(stack);
            case ENDER_PEARL -> stack.isOf(Items.ENDER_PEARL);
            case WIND_CHARGE -> stack.isOf(Items.WIND_CHARGE);
            case WATER_BUCKET -> stack.isOf(Items.WATER_BUCKET);
            case LAVA_BUCKET -> stack.isOf(Items.LAVA_BUCKET);
            case HEALING_POTION -> (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION)) && StackEnchantmentsUtils.canFlag(stack);
            case FIREWORK -> stack.isOf(Items.FIREWORK_ROCKET);
            case FISHING_ROD -> stack.isOf(Items.FISHING_ROD);
            case SNOWBALL -> stack.isOf(Items.SNOWBALL) || stack.isOf(Items.EGG) || stack.isOf(Items.BLUE_EGG) || stack.isOf(Items.BROWN_EGG);
            case FLINT_AND_STEEL -> stack.isOf(Items.FLINT_AND_STEEL);
            case XP_BOTTLE -> stack.isOf(Items.EXPERIENCE_BOTTLE);
            default -> false;
         };
      }
   }

   static boolean isSafeFood(ItemStack stack) {
      return !stack.contains(DataComponentTypes.FOOD)
         ? false
         : !stack.isOf(Items.GOLDEN_APPLE)
            && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)
            && !stack.isOf(Items.ROTTEN_FLESH)
            && !stack.isOf(Items.SPIDER_EYE)
            && !stack.isOf(Items.POISONOUS_POTATO)
            && !stack.isOf(Items.PUFFERFISH)
            && !stack.isOf(Items.CHICKEN)
            && !stack.isOf(Items.CHORUS_FRUIT)
            && !stack.isOf(Items.SUSPICIOUS_STEW);
   }

   public static String[] getDisplayNames() {
      HotbarItemCategory[] blockBucketTypes = values();
      String[] strings = new String[blockBucketTypes.length];

      for (int i = 0; i < blockBucketTypes.length; i++) {
         strings[i] = blockBucketTypes[i].displayName;
      }

      return strings;
   }
}
