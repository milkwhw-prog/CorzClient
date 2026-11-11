package com.corz.client.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RtpRegions {
   public static final String RANDOM = "Random";
   private final Map<String, Integer> regionUseCounts = new HashMap<>();
   public static final List<String> RANDOM_REGION_ITEMS = List.of("Random", "Asia", "East", "EU Central", "EU West", "Oceania", "West");
   private int rotationIndex;
   public static final List<String> REGION_NAMES = List.of("asia", "east", "eu central", "eu west", "oceania", "west");

   public void clearRegionCount(String rtpRegionsText) {
      if (rtpRegionsText != null) {
         this.regionUseCounts.remove(rtpRegionsText);
      }
   }

   public int getRegionCount(String orDefaultText) {
      return this.regionUseCounts.getOrDefault(orDefaultText, 0);
   }

   public void incrementRegionCount(String rtpRegionsText) {
      if (isKnownRegion(rtpRegionsText)) {
         this.regionUseCounts.merge(rtpRegionsText, 1, Integer::sum);
      }
   }

   public void reset() {
      this.regionUseCounts.clear();
      this.rotationIndex = 0;
   }

   public static String normalizeRegion(String rootText) {
      if (rootText == null) {
         return null;
      } else {
         String lowerCase = rootText.trim().toLowerCase(Locale.ROOT);
         return isKnownRegion(lowerCase) ? lowerCase : null;
      }
   }

   public String pickRegion(String modFloorText) {
      String normalizeRegion2 = normalizeRegion(modFloorText);
      if (normalizeRegion2 != null) {
         return normalizeRegion2;
      } else {
         int west_oceania_itemsSize = REGION_NAMES.size();

         for (int i = 0; i < west_oceania_itemsSize; i++) {
            String west_oceania_item = REGION_NAMES.get(Math.floorMod(this.rotationIndex + i, west_oceania_itemsSize));
            if (this.regionUseCounts.getOrDefault(west_oceania_item, 0) < 2) {
               this.rotationIndex = Math.floorMod(this.rotationIndex + i + 1, west_oceania_itemsSize);
               return west_oceania_item;
            }
         }

         this.regionUseCounts.clear();
         String west_oceania_item2 = REGION_NAMES.get(Math.floorMod(this.rotationIndex, west_oceania_itemsSize));
         this.rotationIndex = Math.floorMod(this.rotationIndex + 1, west_oceania_itemsSize);
         return west_oceania_item2;
      }
   }

   public static boolean isKnownRegion(String oceaniaWestText) {
      return oceaniaWestText != null && REGION_NAMES.contains(oceaniaWestText);
   }
}
