package com.corz.client.utils;

public final class SpawnerSculkUtils {
   public static final int UNIT_STEP = 1;
   private static final String[] KIND_NAMES = new String[148];
   private static final boolean[] BARREL_FLAGS = new boolean[148];
   private static final boolean[] CHEST_FLAGS = new boolean[148];

   public static void setBit(long[] spawnerSculkValues, int spawnerSculkLimit) {
      if (spawnerSculkLimit > 0 && spawnerSculkLimit < 148) {
         spawnerSculkValues[spawnerSculkLimit >>> 6] = spawnerSculkValues[spawnerSculkLimit >>> 6] | 1L << (spawnerSculkLimit & 63);
      }
   }

   public static long[] toBitSet(int... spawnerSculkValues) {
      long[] longs = new long[3];

      for (int spawnerSculkValue : spawnerSculkValues) {
         setBit(longs, spawnerSculkValue);
      }

      return longs;
   }

   public static boolean isTargetId(int spawnerSculkValue) {
      return spawnerSculkValue == 139 || spawnerSculkValue == 140 || spawnerSculkValue == 141 || spawnerSculkValue == 147;
   }

   public static boolean isChestId(int index) {
      return index > 0 && index < 148 && CHEST_FLAGS[index];
   }

   static {
      String[] strings = new String[]{
         "none",
         "chest",
         "barrel",
         "shulker",
         "ender_chest",
         "copper_chest",
         "furnace",
         "hopper",
         "dispenser",
         "dropper",
         "piston",
         "logic",
         "switch",
         "sculk_sensor",
         "calibrated_sensor",
         "crafter",
         "lightning_rod",
         "powered_rail",
         "rail",
         "target",
         "tnt",
         "slime_honey",
         "note_jukebox",
         "crafting_table",
         "jobsite",
         "anvil",
         "enchanting",
         "brewing",
         "beacon",
         "conduit",
         "bed",
         "respawn_anchor",
         "lectern_bookshelf",
         "chiseled_bookshelf",
         "bell",
         "campfire",
         "soul_campfire",
         "sign",
         "banner",
         "decorated_pot",
         "cauldron",
         "skull",
         "player_head",
         "flower_pot",
         "beehive",
         "shelf",
         "golem_statue",
         "cake",
         "sculk_placed",
         "spawner",
         "torch",
         "copper_torch",
         "soul_light",
         "lantern",
         "glowstone",
         "sea_lantern",
         "shroomlight",
         "froglight",
         "end_rod",
         "candle",
         "copper_bulb",
         "rs_lamp",
         "jack_o_lantern",
         "wood",
         "dark_oak",
         "cobble",
         "stone_brick",
         "stone_crafted",
         "bricks",
         "mud_brick",
         "deepslate_crafted",
         "cobbled_deepslate",
         "rotated_deepslate",
         "tuff",
         "copper",
         "blackstone",
         "nether_brick",
         "polished_basalt",
         "quartz",
         "purpur_end",
         "prismarine",
         "sandstone_crafted",
         "terracotta",
         "glazed_terracotta",
         "concrete",
         "glass",
         "tinted_glass",
         "wool_carpet",
         "iron_bars",
         "chain",
         "metal_block",
         "obsidian",
         "misc_crafted",
         "hay",
         "dirt_path",
         "ladder",
         "scaffolding",
         "moss",
         "amethyst_placed",
         "sculk",
         "sponge",
         "farmland",
         "crop",
         "stem",
         "sugar_cane",
         "cactus",
         "bamboo",
         "kelp",
         "nether_wart",
         "cocoa",
         "berry_bush",
         "cave_vines",
         "dried_ghast",
         "m_budding_amethyst",
         "m_calcite",
         "m_smooth_basalt",
         "m_reinforced_deepslate",
         "m_sculk_active",
         "m_sculk",
         "m_end_portal_frame",
         "m_trial",
         "m_heavy_core",
         "m_cobweb",
         "m_gilded_blackstone",
         "m_crying_obsidian",
         "m_netherrack",
         "m_magma",
         "m_suspicious",
         "m_infested_brick",
         "m_snow",
         "m_dripstone",
         "m_amethyst_natural",
         "m_spawner_dungeon",
         "m_spawner_cave_spider",
         "m_spawner_blaze",
         "m_spawner_magma",
         "m_spawner_silverfish",
         "m_mossy_natural",
         "m_spawner_other",
         "excavation_room",
         "excavation_tunnel",
         "excavation_shaft",
         "decor_entity",
         "storage_minecart",
         "pet",
         "m_ore",
         "hidden_light",
         "excavation_stair"
      };
      System.arraycopy(strings, 0, KIND_NAMES, 0, Math.min(strings.length, 148));

      for (int i = 0; i < 148; i++) {
         if (KIND_NAMES[i] == null) {
            KIND_NAMES[i] = "f" + i;
         }
      }

      int[] ints = new int[]{
         3, 4, 5, 7, 9, 14, 15, 16, 17, 21, 22, 26, 28, 29, 31, 33, 36, 37, 42, 44, 45, 46, 47, 51, 57, 83, 84, 86, 78, 96, 92, 103, 144, 146
      };

      for (int index : ints) {
         BARREL_FLAGS[index] = true;
      }

      for (int j = 113; j <= 138; j++) {
         CHEST_FLAGS[j] = true;
      }

      CHEST_FLAGS[145] = true;
   }

   public static void mergeBits(long[] spawnerSculkValues, long[] cityMonumentValues) {
      for (int i = 0; i < spawnerSculkValues.length && i < cityMonumentValues.length; i++) {
         spawnerSculkValues[i] |= cityMonumentValues[i];
      }
   }

   public static boolean isExtendedTargetId(int spawnerSculkValue) {
      return isTargetId(spawnerSculkValue) || spawnerSculkValue == 146 || spawnerSculkValue == 142 || spawnerSculkValue == 143 || spawnerSculkValue == 144;
   }

   public static boolean isBitSetEmpty(long[] spawnerSculkValues) {
      if (spawnerSculkValues == null) {
         return true;
      } else {
         for (long spawnerSculkValue : spawnerSculkValues) {
            if (spawnerSculkValue != 0L) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isBitSet(long[] spawnerSculkValues, int spawnerSculkLimit) {
      return spawnerSculkValues != null && spawnerSculkLimit > 0 && spawnerSculkLimit < 148 ? (spawnerSculkValues[spawnerSculkLimit >>> 6] & 1L << (spawnerSculkLimit & 63)) != 0L : false;
   }

   public static boolean isBarrelId(int index) {
      return index > 0 && index < 148 && BARREL_FLAGS[index];
   }

   private SpawnerSculkUtils() {
   }

   public static String getIdName(int index) {
      return index >= 0 && index < 148 ? KIND_NAMES[index] : "f" + index;
   }
}
