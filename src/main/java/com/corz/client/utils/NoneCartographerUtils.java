package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.VillagerProfession;

public final class NoneCartographerUtils {
   private static final Map<String, RegistryKey<VillagerProfession>> CARTOGRAPHER_MASON_MAP = new LinkedHashMap<>();
   public static final String ANY_LABEL = "Any";
   private static final String[] masterExpertTexts = new String[]{"", "Novice", "Apprentice", "Journeyman", "Expert", "Master"};

   public static boolean canMatchesMason(RegistryEntry<VillagerProfession> registryEntry, String matchesMasonText) {
      if (matchesMasonText != null && !matchesMasonText.equals("Any")) {
         RegistryKey cartographer_mason_map = CARTOGRAPHER_MASON_MAP.get(matchesMasonText);
         return cartographer_mason_map != null && registryEntry != null && registryEntry.matchesKey(cartographer_mason_map);
      } else {
         return true;
      }
   }

   public static String formatExpertMaster(int index) {
      return index > 0 && index < masterExpertTexts.length ? masterExpertTexts[index] : "Level " + index;
   }

   private NoneCartographerUtils() {
   }

   static {
      CARTOGRAPHER_MASON_MAP.put("Librarian", VillagerProfession.LIBRARIAN);
      CARTOGRAPHER_MASON_MAP.put("Farmer", VillagerProfession.FARMER);
      CARTOGRAPHER_MASON_MAP.put("Fletcher", VillagerProfession.FLETCHER);
      CARTOGRAPHER_MASON_MAP.put("Cleric", VillagerProfession.CLERIC);
      CARTOGRAPHER_MASON_MAP.put("Armorer", VillagerProfession.ARMORER);
      CARTOGRAPHER_MASON_MAP.put("Toolsmith", VillagerProfession.TOOLSMITH);
      CARTOGRAPHER_MASON_MAP.put("Weaponsmith", VillagerProfession.WEAPONSMITH);
      CARTOGRAPHER_MASON_MAP.put("Cartographer", VillagerProfession.CARTOGRAPHER);
      CARTOGRAPHER_MASON_MAP.put("Mason", VillagerProfession.MASON);
      CARTOGRAPHER_MASON_MAP.put("Shepherd", VillagerProfession.SHEPHERD);
      CARTOGRAPHER_MASON_MAP.put("Leatherworker", VillagerProfession.LEATHERWORKER);
      CARTOGRAPHER_MASON_MAP.put("Butcher", VillagerProfession.BUTCHER);
      CARTOGRAPHER_MASON_MAP.put("Fisherman", VillagerProfession.FISHERMAN);
   }

   public static String getKey(RegistryEntry<VillagerProfession> registryEntry) {
      if (registryEntry == null) {
         return "None";
      } else {
         String orElse = registryEntry.getKey().map(registryKey -> registryKey.getValue().getPath()).orElse("none");

         for (Entry entryItem : CARTOGRAPHER_MASON_MAP.entrySet()) {
            if (((RegistryKey)entryItem.getValue()).getValue().getPath().equals(orElse)) {
               return (String)entryItem.getKey();
            }
         }

         return formatNone(orElse);
      }
   }

   public static String[] getMasonCartographer() {
      String[] strings = new String[CARTOGRAPHER_MASON_MAP.size() + 1];
      strings[0] = "Any";
      int masonCartographerCounter = 1;

      for (String masonCartographerText : CARTOGRAPHER_MASON_MAP.keySet()) {
         strings[masonCartographerCounter++] = masonCartographerText;
      }

      return strings;
   }

   public static RegistryKey<VillagerProfession> getMasonCartographer(String masonCartographerText) {
      return masonCartographerText == null ? null : CARTOGRAPHER_MASON_MAP.get(masonCartographerText);
   }

   private static String formatNone(String noneText) {
      if (noneText != null && !noneText.isEmpty()) {
         String replace = noneText.replace('_', ' ');
         return Character.toUpperCase(replace.charAt(0)) + replace.substring(1);
      } else {
         return "None";
      }
   }
}
