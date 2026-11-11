package com.corz.client.utils;

import java.util.Locale;

public final class MinecraftSpiderUtils {
   public static MinecraftSpiderUtils.Bucket getMinecraftSpider(String minecraftSpiderText) {
      String lowerCase = getLowerCase(minecraftSpiderText);
      if (lowerCase == null) {
         return MinecraftSpiderUtils.Bucket.UNKNOWN;
      } else {
         return switch (lowerCase) {
            case "minecraft:zombie" -> MinecraftSpiderUtils.Bucket.ZOMBIE;
            case "minecraft:skeleton" -> MinecraftSpiderUtils.Bucket.SKELETON;
            case "minecraft:spider" -> MinecraftSpiderUtils.Bucket.SPIDER;
            case "minecraft:cave_spider" -> MinecraftSpiderUtils.Bucket.CAVE_SPIDER;
            case "minecraft:blaze" -> MinecraftSpiderUtils.Bucket.BLAZE;
            default -> MinecraftSpiderUtils.Bucket.OTHER;
         };
      }
   }

   private MinecraftSpiderUtils() {
   }

   public static String formatUnknown(String unknownText) {
      String lowerCase = getLowerCase(unknownText);
      if (lowerCase == null) {
         return "Unknown";
      } else {
         String substring = lowerCase.substring(lowerCase.indexOf(58) + 1);
         StringBuilder stringBuilder = new StringBuilder(substring.length());
         boolean unknownFlag = true;

         for (int i = 0; i < substring.length(); i++) {
            char charAt = substring.charAt(i);
            if (charAt != '_' && charAt != '/' && charAt != '.') {
               stringBuilder.append(unknownFlag ? Character.toUpperCase(charAt) : charAt);
               unknownFlag = false;
            } else {
               stringBuilder.append(' ');
               unknownFlag = true;
            }
         }

         return stringBuilder.toString();
      }
   }

   public static String getLowerCase(String lowerCaseText) {
      if (lowerCaseText == null) {
         return null;
      } else {
         String lowerCase = lowerCaseText.trim().toLowerCase(Locale.ROOT);
         if (lowerCase.isEmpty()) {
            return null;
         } else {
            return lowerCase.indexOf(58) < 0 ? "minecraft:" + lowerCase : lowerCase;
         }
      }
   }

   public static enum Bucket {
      ZOMBIE,
      SKELETON,
      SPIDER,
      CAVE_SPIDER,
      BLAZE,
      OTHER,
      UNKNOWN;
   }
}
