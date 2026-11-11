package com.corz.client.utils;

import java.util.Locale;
import java.util.Set;

public final class MinecraftSculkUtils {
   private static final Set<String> MINECRAFT_BEE_SET = Set.of(
      "minecraft:repeater",
      "minecraft:comparator",
      "minecraft:lever",
      "minecraft:note_block",
      "minecraft:jukebox",
      "minecraft:daylight_detector",
      "minecraft:cake",
      "minecraft:bell",
      "minecraft:composter",
      "minecraft:lectern",
      "minecraft:beehive",
      "minecraft:bee_nest"
   );
   private static final Set<String> MINECRAFT_CAULDRON_FENCE_SET = Set.of(
      "minecraft:chest",
      "minecraft:trapped_chest",
      "minecraft:barrel",
      "minecraft:dispenser",
      "minecraft:dropper",
      "minecraft:hopper",
      "minecraft:furnace",
      "minecraft:blast_furnace",
      "minecraft:smoker",
      "minecraft:crafting_table",
      "minecraft:anvil",
      "minecraft:chipped_anvil",
      "minecraft:damaged_anvil",
      "minecraft:enchanting_table",
      "minecraft:grindstone",
      "minecraft:stonecutter",
      "minecraft:loom",
      "minecraft:cartography_table",
      "minecraft:smithing_table",
      "minecraft:ender_chest"
   );
   private static final Set<String> MINECRAFT_SCULK_SET = Set.of(
      "minecraft:sculk_catalyst", "minecraft:sculk_sensor", "minecraft:calibrated_sculk_sensor", "minecraft:sculk_shrieker"
   );

   public static boolean canSafe(MinecraftSculkUtils.Interaction interaction) {
      return interaction != MinecraftSculkUtils.Interaction.SAFE;
   }

   private MinecraftSculkUtils() {
   }

   public static int getMutatingSafe(MinecraftSculkUtils.Interaction interaction, boolean mutatingSafeFlag, boolean temporaryDirt) {
      if (interaction != MinecraftSculkUtils.Interaction.SAFE) {
         return 1000 + (interaction == MinecraftSculkUtils.Interaction.STATE_MUTATING ? 100 : 0);
      } else if (mutatingSafeFlag && !temporaryDirt) {
         return 0;
      } else {
         return temporaryDirt ? 100 : 200;
      }
   }

   public static MinecraftSculkUtils.Interaction getCauldronFence(String cauldronFenceText, boolean cauldronFenceFlag, boolean blockFlag) {
      String goodsSlot = cauldronFenceText == null ? "" : cauldronFenceText.toLowerCase(Locale.ROOT);
      if (blockFlag
         || MINECRAFT_BEE_SET.contains(goodsSlot)
         || goodsSlot.endsWith("_button")
         || goodsSlot.endsWith("_door")
         || goodsSlot.endsWith("_trapdoor")
         || goodsSlot.endsWith("_fence_gate")
         || goodsSlot.endsWith("_cauldron")
         || goodsSlot.endsWith("_campfire")) {
         return MinecraftSculkUtils.Interaction.STATE_MUTATING;
      } else if (MINECRAFT_SCULK_SET.contains(goodsSlot)) {
         return MinecraftSculkUtils.Interaction.SAFE;
      } else {
         return !cauldronFenceFlag && !MINECRAFT_CAULDRON_FENCE_SET.contains(goodsSlot) ? MinecraftSculkUtils.Interaction.SAFE : MinecraftSculkUtils.Interaction.GUI;
      }
   }

   public static enum Interaction {
      SAFE,
      GUI,
      STATE_MUTATING;
   }
}
