package com.corz.client.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class LoadingModsUtils {
   static final String CORZ = "corz";
   private static final String LOADING = "Loading ";
   private static final String MODS = " mods:";

   static boolean isContains(String containsText) {
      return containsText.toLowerCase(Locale.ROOT).contains("corz");
   }

   public static boolean isLatestLogs() {
      try {
         Path resolve = FabricLoader.getInstance().getGameDir().resolve("logs").resolve("latest.log");
         if (!Files.exists(resolve)) {
            return false;
         } else {
            List list = getItems(Files.readAllLines(resolve, StandardCharsets.UTF_8));
            Files.write(resolve, list, StandardCharsets.UTF_8);
            return true;
         }
      } catch (Exception e) {
         return false;
      }
   }

   private LoadingModsUtils() {
   }

   static String formatLoadingMods(String loadingModsText) {
      int indexOf = loadingModsText.indexOf("Loading ");
      if (indexOf < 0) {
         return null;
      } else {
         int start = indexOf + "Loading ".length();
         int end = loadingModsText.indexOf(" mods:", start);
         if (end < 0) {
            return null;
         } else {
            String trim = loadingModsText.substring(start, end).trim();

            int loadingModsValue;
            try {
               loadingModsValue = Integer.parseInt(trim);
            } catch (NumberFormatException e) {
               return null;
            }

            int max2 = Math.max(0, loadingModsValue - 1);
            return loadingModsText.substring(0, start) + max2 + loadingModsText.substring(end);
         }
      }
   }

   public static List<String> getItems(List<String> list) {
      ArrayList arrayList = new ArrayList(list.size());

      for (String loadingModsText : list) {
         String totalPriceData = formatLoadingMods(loadingModsText);
         if (totalPriceData != null) {
            arrayList.add(totalPriceData);
         } else if (!isContains(loadingModsText)) {
            arrayList.add(loadingModsText);
         }
      }

      return arrayList;
   }
}
