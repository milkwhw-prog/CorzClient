package com.corz.client.inventory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class IngotBallUtils {
   public static final String BOTTLE = "XP Bottle";
   private static final Map<String, Item> BOTTLE_BOOK_MAP = new LinkedHashMap<>();
   private static final Map<String, Item> INGOT_BOOK_MAP = new LinkedHashMap<>();

   public static boolean canExperienceBottle(Item item2) {
      return item2 == Items.EXPERIENCE_BOTTLE;
   }

   public static String getId(Item item2) {
      if (item2 == null) {
         return null;
      } else {
         for (Entry entry : INGOT_BOOK_MAP.entrySet()) {
            if (entry.getValue() == item2) {
               return (String)entry.getKey();
            }
         }

         return Registries.ITEM.getId(item2).toString();
      }
   }

   public static int getValue(Item item2) {
      return canExperienceBottle(item2) ? 5 : 0;
   }

   public static String formatBookIngot(String formatBookIngot) {
      if (formatBookIngot == null) {
         return "?";
      } else if (!INGOT_BOOK_MAP.containsKey(formatBookIngot) && !BOTTLE_BOOK_MAP.containsKey(formatBookIngot) && formatBookIngot.indexOf(58) >= 0) {
         Item defaultedRegistry = getDefaultedRegistry(formatBookIngot);
         return defaultedRegistry == null ? formatBookIngot : getKey(defaultedRegistry);
      } else {
         return formatBookIngot;
      }
   }

   private IngotBallUtils() {
   }

   public static String[] getIngotLeather() {
      return new String[]{
         "Pumpkin",
         "Melon Slice",
         "Wheat",
         "Carrot",
         "Potato",
         "Beetroot",
         "Sweet Berries",
         "Stick",
         "Coal",
         "Iron Ingot",
         "Gold Ingot",
         "Rotten Flesh",
         "String",
         "Leather",
         "Glass Pane",
         "Clay Ball",
         "Dried Kelp Block",
         "Paper"
      };
   }

   public static Item getDefaultedRegistry(String defaultedRegistryText) {
      if (defaultedRegistryText == null) {
         return null;
      } else {
         Item ingot_book_map = INGOT_BOOK_MAP.get(defaultedRegistryText);
         if (ingot_book_map != null) {
            return ingot_book_map;
         } else {
            Item bottle_book_map = BOTTLE_BOOK_MAP.get(defaultedRegistryText);
            if (bottle_book_map == null && defaultedRegistryText.indexOf(58) >= 0) {
               Identifier tryParse = Identifier.tryParse(defaultedRegistryText);
               if (tryParse == null) {
                  return null;
               } else {
                  Item item = Registries.ITEM.get(tryParse);
                  return item != null && item != Items.AIR ? item : null;
               }
            } else {
               return bottle_book_map;
            }
         }
      }
   }

   public static String getKey(Item emerald) {
      if (emerald == null) {
         return "?";
      } else {
         for (Entry entry : INGOT_BOOK_MAP.entrySet()) {
            if (entry.getValue() == emerald) {
               return (String)entry.getKey();
            }
         }

         for (Entry entryItem : BOTTLE_BOOK_MAP.entrySet()) {
            if (entryItem.getValue() == emerald) {
               return (String)entryItem.getKey();
            }
         }

         String translationKey = emerald.getTranslationKey();
         int lastIndexOf = translationKey.lastIndexOf(46);
         String ingotBallText = lastIndexOf >= 0 ? translationKey.substring(lastIndexOf + 1) : translationKey;
         String[] split = ingotBallText.replace('_', ' ').split(" ");
         StringBuilder stringBuilder = new StringBuilder();

         for (String ingotBallText2 : split) {
            if (!ingotBallText2.isEmpty()) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append(' ');
               }

               stringBuilder.append(Character.toUpperCase(ingotBallText2.charAt(0))).append(ingotBallText2.substring(1));
            }
         }

         return stringBuilder.toString();
      }
   }

   public static String[] getBookIngot() {
      String[] strings = new String[INGOT_BOOK_MAP.size() + BOTTLE_BOOK_MAP.size()];
      int bookIngotCounter = 0;

      for (String bookIngotText : INGOT_BOOK_MAP.keySet()) {
         strings[bookIngotCounter++] = bookIngotText;
      }

      for (String keyItem : BOTTLE_BOOK_MAP.keySet()) {
         strings[bookIngotCounter++] = keyItem;
      }

      return strings;
   }

   static {
      INGOT_BOOK_MAP.put("Emerald", Items.EMERALD);
      INGOT_BOOK_MAP.put("Enchanted Book", Items.ENCHANTED_BOOK);
      INGOT_BOOK_MAP.put("Book", Items.BOOK);
      INGOT_BOOK_MAP.put("Paper", Items.PAPER);
      INGOT_BOOK_MAP.put("Pumpkin", Items.PUMPKIN);
      INGOT_BOOK_MAP.put("Melon Slice", Items.MELON_SLICE);
      INGOT_BOOK_MAP.put("Wheat", Items.WHEAT);
      INGOT_BOOK_MAP.put("Carrot", Items.CARROT);
      INGOT_BOOK_MAP.put("Potato", Items.POTATO);
      INGOT_BOOK_MAP.put("Beetroot", Items.BEETROOT);
      INGOT_BOOK_MAP.put("Sweet Berries", Items.SWEET_BERRIES);
      INGOT_BOOK_MAP.put("Stick", Items.STICK);
      INGOT_BOOK_MAP.put("Coal", Items.COAL);
      INGOT_BOOK_MAP.put("Iron Ingot", Items.IRON_INGOT);
      INGOT_BOOK_MAP.put("Gold Ingot", Items.GOLD_INGOT);
      INGOT_BOOK_MAP.put("Rotten Flesh", Items.ROTTEN_FLESH);
      INGOT_BOOK_MAP.put("String", Items.STRING);
      INGOT_BOOK_MAP.put("Leather", Items.LEATHER);
      INGOT_BOOK_MAP.put("Glass Pane", Items.GLASS_PANE);
      INGOT_BOOK_MAP.put("Clay Ball", Items.CLAY_BALL);
      INGOT_BOOK_MAP.put("Dried Kelp Block", Items.DRIED_KELP_BLOCK);
      BOTTLE_BOOK_MAP.put("XP Bottle", Items.EXPERIENCE_BOTTLE);
   }
}
