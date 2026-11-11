package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class BiomeCategoryMapper {
   private final Map<RegistryKey<Biome>, Integer> categoryCache = new HashMap<>();

   public int getBiomeCategory(RegistryEntry<Biome> registryEntry, int forestDarkValue) {
      if (registryEntry == null) {
         return getFallbackCategory(forestDarkValue);
      } else {
         Optional optional = registryEntry.getKey();
         if (optional.isEmpty()) {
            return getFallbackCategory(forestDarkValue);
         } else {
            RegistryKey registryKey = (RegistryKey)optional.get();
            Integer integer = this.categoryCache.get(registryKey);
            if (integer == null) {
               integer = computeBiomeCategory(registryKey, forestDarkValue);
               this.categoryCache.put(registryKey, integer);
            }

            return integer;
         }
      }
   }

   private static boolean isSameBiome(RegistryKey<Biome> registryKey, RegistryKey<Biome> registryKey2) {
      return registryKey == registryKey2 || registryKey.equals(registryKey2);
   }

   private static int getFallbackCategory(int forestDarkValue) {
      if (forestDarkValue == 1) {
         return 21;
      } else {
         return forestDarkValue == 2 ? 26 : 1;
      }
   }

   private static int computeBiomeCategory(RegistryKey<Biome> registryKey, int forestOceanValue) {
      if (isSameBiome(registryKey, BiomeKeys.DEEP_DARK)) {
         return 2;
      } else if (isSameBiome(registryKey, BiomeKeys.LUSH_CAVES)) {
         return 3;
      } else if (isSameBiome(registryKey, BiomeKeys.DRIPSTONE_CAVES)) {
         return 4;
      } else if (isSameBiome(registryKey, BiomeKeys.OCEAN)
         || isSameBiome(registryKey, BiomeKeys.DEEP_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.COLD_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.DEEP_COLD_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.LUKEWARM_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.DEEP_LUKEWARM_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.WARM_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.FROZEN_OCEAN)
         || isSameBiome(registryKey, BiomeKeys.DEEP_FROZEN_OCEAN)) {
         return 5;
      } else if (isSameBiome(registryKey, BiomeKeys.RIVER) || isSameBiome(registryKey, BiomeKeys.FROZEN_RIVER)) {
         return 6;
      } else if (isSameBiome(registryKey, BiomeKeys.BEACH) || isSameBiome(registryKey, BiomeKeys.SNOWY_BEACH) || isSameBiome(registryKey, BiomeKeys.STONY_SHORE)) {
         return 7;
      } else if (isSameBiome(registryKey, BiomeKeys.DESERT)) {
         return 8;
      } else if (isSameBiome(registryKey, BiomeKeys.BADLANDS) || isSameBiome(registryKey, BiomeKeys.ERODED_BADLANDS) || isSameBiome(registryKey, BiomeKeys.WOODED_BADLANDS)) {
         return 9;
      } else if (isSameBiome(registryKey, BiomeKeys.JUNGLE) || isSameBiome(registryKey, BiomeKeys.SPARSE_JUNGLE) || isSameBiome(registryKey, BiomeKeys.BAMBOO_JUNGLE)) {
         return 10;
      } else if (isSameBiome(registryKey, BiomeKeys.TAIGA)
         || isSameBiome(registryKey, BiomeKeys.SNOWY_TAIGA)
         || isSameBiome(registryKey, BiomeKeys.OLD_GROWTH_PINE_TAIGA)
         || isSameBiome(registryKey, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)) {
         return 11;
      } else if (isSameBiome(registryKey, BiomeKeys.SNOWY_PLAINS)
         || isSameBiome(registryKey, BiomeKeys.ICE_SPIKES)
         || isSameBiome(registryKey, BiomeKeys.SNOWY_SLOPES)
         || isSameBiome(registryKey, BiomeKeys.FROZEN_PEAKS)
         || isSameBiome(registryKey, BiomeKeys.JAGGED_PEAKS)
         || isSameBiome(registryKey, BiomeKeys.GROVE)) {
         return 12;
      } else if (isSameBiome(registryKey, BiomeKeys.SWAMP) || isSameBiome(registryKey, BiomeKeys.MANGROVE_SWAMP)) {
         return 13;
      } else if (isSameBiome(registryKey, BiomeKeys.DARK_FOREST)) {
         return 14;
      } else if (isSameBiome(registryKey, BiomeKeys.PALE_GARDEN)) {
         return 15;
      } else if (isSameBiome(registryKey, BiomeKeys.PLAINS) || isSameBiome(registryKey, BiomeKeys.SUNFLOWER_PLAINS) || isSameBiome(registryKey, BiomeKeys.MEADOW)) {
         return 16;
      } else if (isSameBiome(registryKey, BiomeKeys.SAVANNA) || isSameBiome(registryKey, BiomeKeys.SAVANNA_PLATEAU) || isSameBiome(registryKey, BiomeKeys.WINDSWEPT_SAVANNA)) {
         return 17;
      } else if (isSameBiome(registryKey, BiomeKeys.MUSHROOM_FIELDS)) {
         return 18;
      } else if (isSameBiome(registryKey, BiomeKeys.FOREST)
         || isSameBiome(registryKey, BiomeKeys.FLOWER_FOREST)
         || isSameBiome(registryKey, BiomeKeys.BIRCH_FOREST)
         || isSameBiome(registryKey, BiomeKeys.OLD_GROWTH_BIRCH_FOREST)
         || isSameBiome(registryKey, BiomeKeys.CHERRY_GROVE)
         || isSameBiome(registryKey, BiomeKeys.WINDSWEPT_FOREST)) {
         return 20;
      } else if (isSameBiome(registryKey, BiomeKeys.WINDSWEPT_HILLS) || isSameBiome(registryKey, BiomeKeys.WINDSWEPT_GRAVELLY_HILLS) || isSameBiome(registryKey, BiomeKeys.STONY_PEAKS)) {
         return 19;
      } else if (isSameBiome(registryKey, BiomeKeys.NETHER_WASTES)) {
         return 21;
      } else if (isSameBiome(registryKey, BiomeKeys.CRIMSON_FOREST)) {
         return 22;
      } else if (isSameBiome(registryKey, BiomeKeys.WARPED_FOREST)) {
         return 23;
      } else if (isSameBiome(registryKey, BiomeKeys.SOUL_SAND_VALLEY)) {
         return 24;
      } else if (isSameBiome(registryKey, BiomeKeys.BASALT_DELTAS)) {
         return 25;
      } else if (!isSameBiome(registryKey, BiomeKeys.THE_END)
         && !isSameBiome(registryKey, BiomeKeys.END_HIGHLANDS)
         && !isSameBiome(registryKey, BiomeKeys.END_MIDLANDS)
         && !isSameBiome(registryKey, BiomeKeys.SMALL_END_ISLANDS)
         && !isSameBiome(registryKey, BiomeKeys.END_BARRENS)) {
         String path = registryKey.getValue().getPath();
         if (path.contains("deep_dark")) {
            return 2;
         } else if (path.contains("lush")) {
            return 3;
         } else if (path.contains("dripstone")) {
            return 4;
         } else if (path.contains("ocean")) {
            return 5;
         } else if (path.contains("river")) {
            return 6;
         } else if (path.contains("beach") || path.contains("shore")) {
            return 7;
         } else if (path.contains("desert")) {
            return 8;
         } else if (path.contains("badlands") || path.contains("mesa")) {
            return 9;
         } else if (path.contains("jungle")) {
            return 10;
         } else if (path.contains("taiga")) {
            return 11;
         } else if (path.contains("snow") || path.contains("frozen") || path.contains("ice")) {
            return 12;
         } else if (path.contains("swamp") || path.contains("mangrove")) {
            return 13;
         } else if (path.contains("dark_forest")) {
            return 14;
         } else if (path.contains("pale")) {
            return 15;
         } else if (path.contains("plains") || path.contains("meadow")) {
            return 16;
         } else if (path.contains("savanna")) {
            return 17;
         } else if (path.contains("mushroom")) {
            return 18;
         } else if (path.contains("crimson")) {
            return 22;
         } else if (path.contains("warped")) {
            return 23;
         } else if (path.contains("soul")) {
            return 24;
         } else if (path.contains("basalt")) {
            return 25;
         } else if (path.contains("nether")) {
            return 21;
         } else if (path.contains("end")) {
            return 26;
         } else {
            return path.contains("forest") ? 20 : getFallbackCategory(forestOceanValue);
         }
      } else {
         return 26;
      }
   }
}
