package com.corz.client.utils;

public final class ForestCavesUtils {
   public static final int UNUSED_ONE = 1;

   private ForestCavesUtils() {
   }

   public static String formatForestCaves(int forestCavesValue) {
      switch (forestCavesValue) {
         case 1:
            return "overworld";
         case 2:
            return "deep_dark";
         case 3:
            return "lush_caves";
         case 4:
            return "dripstone_caves";
         case 5:
            return "ocean";
         case 6:
            return "river";
         case 7:
            return "beach";
         case 8:
            return "desert";
         case 9:
            return "badlands";
         case 10:
            return "jungle";
         case 11:
            return "taiga";
         case 12:
            return "snowy";
         case 13:
            return "swamp";
         case 14:
            return "dark_forest";
         case 15:
            return "pale_garden";
         case 16:
            return "plains";
         case 17:
            return "savanna";
         case 18:
            return "mushroom";
         case 19:
            return "mountain";
         case 20:
            return "forest";
         case 21:
            return "nether_wastes";
         case 22:
            return "crimson_forest";
         case 23:
            return "warped_forest";
         case 24:
            return "soul_sand_valley";
         case 25:
            return "basalt_deltas";
         case 26:
            return "end";
         default:
            return "unknown";
      }
   }
}
