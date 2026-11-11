package com.corz.client.utils;

import java.util.Locale;
import java.util.Optional;
import com.corz.client.utils.KmbtSellerUtils;

public enum ShardAxeType {
   DRILL("Drill", "Shard Pickaxe", "Breaks 9 Blocks at Once", ShardAxeType.Tool.PICKAXE, "ah drill"),
   SELL_WAND("Sell Wand", "Shard Sell Axe", "Instantly Sells All Items in A Chest", ShardAxeType.Tool.AXE, "ah sellwand"),
   SHARD_AXE("Shard Axe", "Shard Axe", "Breaks Trees Instantly", ShardAxeType.Tool.AXE, "ah shard axe"),
   SHARD_SHOVEL("Shard Shovel", "Shard Shovel", "Breaks 9 Blocks at Once", ShardAxeType.Tool.SHOVEL, "ah shard shovel");

   private final String shardAxeText;
   private final String displayLabel;
   private final String commandName;
   private final ShardAxeType.Tool shovelPickaxeType;
   private final String shortLabel;
   private final String emptyText;

   public static String formatRoot(String rootText) {
      StringBuilder stringBuilder = new StringBuilder();

      for (char ch : KmbtSellerUtils.formatText(rootText).toLowerCase(Locale.ROOT).toCharArray()) {
         if (Character.isLetterOrDigit(ch)) {
            stringBuilder.append(ch);
         }
      }

      return stringBuilder.toString();
   }

   public String getDisplayLabel() {
      return this.displayLabel;
   }

   public String getText() {
      return this.shardAxeText;
   }

   public static Optional<ShardAxeType> getEmpty(String emptyText) {
      String moneyEnoughText = formatRoot(emptyText);
      if (moneyEnoughText.isEmpty()) {
         return Optional.empty();
      } else {
         for (ShardAxeType shardAxeType : values()) {
            if (shardAxeType.emptyText.equals(moneyEnoughText)) {
               return Optional.of(shardAxeType);
            }
         }

         return Optional.empty();
      }
   }

   public ShardAxeType.Tool getShovelPickaxeType() {
      return this.shovelPickaxeType;
   }

   public String getShortLabel() {
      return this.shortLabel;
   }

   public String getCommandName() {
      return this.commandName;
   }

   private ShardAxeType(String text, String rootText, String text3, ShardAxeType.Tool shovelPickaxeType, String text4) {
      this.commandName = text;
      this.shardAxeText = rootText;
      this.shortLabel = text3;
      this.shovelPickaxeType = shovelPickaxeType;
      this.displayLabel = text4;
      this.emptyText = formatRoot(rootText);
   }

   public static enum Tool {
      PICKAXE,
      AXE,
      SHOVEL,
      OTHER;
   }
}
