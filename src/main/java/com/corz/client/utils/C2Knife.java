package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum C2Knife {
   BUTTERFLY(null, "butterfly", "Butterfly"),
   BAYONET("classics", "bayonet", "Bayonet"),
   BAYONET_AUTOTRONIC("classics", "bayoauto", "Bayonet Autotronic"),
   BUTTERFLY_EMERALD("classics", "buemerald", "Butterfly Emerald"),
   BUTTERFLY_SNOWFALL("classics", "busnowfall", "Butterfly Snowfall"),
   CLASSIC("classics", "css", "Classic Knife"),
   KARAMBIT("classics", "karambit", "Karambit"),
   KARAMBIT_OBSIDIAN("classics", "karobsidian", "Karambit Obsidian"),
   M9("classics", "m9", "M9"),
   M9_SAPPHIRE("classics", "m9sapphire", "M9 Sapphire"),
   M9_AMETHYST("classics", "m9amethyst", "M9 Amethyst"),
   M9_AUTOTRONIC("classics", "m9auto", "M9 Autotronic"),
   M9_EMERALD("classics", "m9emerald", "M9 Emerald"),
   SHADOW_DAGGERS("classics", "push", "Shadow Daggers"),
   SKELETON("classics", "skeleton", "Skeleton"),
   STILETTO("classics", "stiletto", "C2 Stiletto"),
   STILETTO_TIGER_TOOTH("classics", "stilettott", "Stiletto Tiger Tooth"),
   STILETTO_SAPPHIRE("classics", "stilettosapphire", "Stiletto Sapphire"),
   TACTICAL("classics", "tactical", "Tactical"),
   TALON("classics", "talon", "Talon"),
   FIELD_DAGGER("operative", "dagger", "Field Dagger"),
   FIELD_KARAMBIT("operative", "karambit", "Field Karambit"),
   FIELD_BAT("operative", "baseball_bat", "Field Bat"),
   ANNIHILATOR_RED("premier", "annihil", "Annihilator Red"),
   ANNIHILATOR_YELLOW("premier", "annihil", "Annihilator Yellow"),
   COBRA_VANGUARD("premier", "cobra_vanguard", "Cobra Vanguard"),
   EXECUTOR("premier", "executor", "Executor"),
   MERCY("premier", "mercy", "Mercy"),
   DARK_STAR("premier", "null_annihil", "Dark Star Replica"),
   POLARIS("premier", "polaris", "Polaris"),
   QUENCHING("premier", "quenching", "Quenching"),
   SHADOWKILLER("premier", "shadowkiller", "Shadowkiller"),
   SILENCE("premier", "silencefd", "Silence"),
   SEA_GLEAM("premier", "thieve_sea", "Sea Gleam"),
   TREASURE("premier", "treasure_kn", "Treasure Knife");
   public final String treasureText;
   public final String classics;
   public static final String CORZCLIENT = "corzclient";
   public final String classicsText;
   public static final String VISUALS = "c2visuals";
   public static final String ANGEL = "_angel";
   private static final List<String> unmodifiableList;
   public static final String ASSETS_CORZCLIENT_KNIVES = "/assets/corzclient/knives/";

   public boolean isAnnihilatorYellow() {
      return this == ANNIHILATOR_YELLOW;
   }

   public String formatVisualsPack(String visualsPackText, String visualsPackText2, String visualsPackText3) {
      if (this.classics != null && visualsPackText != null) {
         int indexOf = visualsPackText.indexOf(58);
         String moneyEnoughText = indexOf < 0 ? "c2visuals" : visualsPackText.substring(0, indexOf);
         String visualsPackText5 = indexOf < 0 ? visualsPackText : visualsPackText.substring(indexOf + 1);
         if ("c2visuals".equals(moneyEnoughText) && visualsPackText5.matches("[a-z0-9_/-]+") && !visualsPackText5.contains("..") && !visualsPackText5.startsWith("/")) {
            return "knives/" + this.classics + "/" + visualsPackText2 + "/" + visualsPackText5 + visualsPackText3;
         } else {
            throw new IllegalArgumentException("Invalid knife asset reference: " + visualsPackText);
         }
      } else {
         throw new IllegalArgumentException("Not a pack knife");
      }
   }

   public boolean isButterflySnowfall() {
      return this == BUTTERFLY || this == BUTTERFLY_EMERALD || this == BUTTERFLY_SNOWFALL;
   }

   public String formatClassicsText() {
      return this.classics == null ? null : "/assets/corzclient/knives/" + this.classics + "/display/melee/" + this.classicsText + ".json";
   }

   public String formatStaticIdle() {
      return this.isAnnihilatorYellow() ? "static_idle_angel" : "static_idle";
   }

   public boolean isEndsWith(String endsWithText) {
      if (endsWithText == null) {
         return false;
      } else {
         boolean endsWith = endsWithText.endsWith("_angel");
         if (this == ANNIHILATOR_RED) {
            return !endsWith;
         } else {
            return this == ANNIHILATOR_YELLOW ? endsWith : true;
         }
      }
   }

   public static C2Knife getTreasureText(String treasureText) {
      if (treasureText == null) {
         return null;
      } else {
         for (C2Knife treasureText2 : values()) {
            if (treasureText2.treasureText.equals(treasureText)) {
               return treasureText2;
            }
         }

         return null;
      }
   }

   public static List<String> getUnmodifiable() {
      return unmodifiableList;
   }

   public static C2Knife getC2Knife(String c2KnifeText) {
      C2Knife treasureText = getTreasureText(c2KnifeText);
      return treasureText != null && !treasureText.isClassics() ? treasureText : null;
   }

   public boolean isAnnihilatorYellowFlag() {
      return this == ANNIHILATOR_RED || this == ANNIHILATOR_YELLOW;
   }

   public String formatPremierOperative() {
      if (this.classics == null) {
         return "Corz";
      } else {
         String classics = this.classics;

         return switch (classics) {
            case "operative" -> "C2 Operative";
            case "premier" -> "C2 Premier";
            default -> "C2 Classics";
         };
      }
   }

   public boolean isClassics() {
      return this.classics == null;
   }

   private C2Knife(String classics, String text, String treasureText) {
      this.classics = classics;
      this.classicsText = text;
      this.treasureText = treasureText;
   }

   static {
      ArrayList arrayList = new ArrayList();

      for (C2Knife c2Knife : values()) {
         if (c2Knife.classics != null) {
            arrayList.add(c2Knife.treasureText);
         }
      }

      unmodifiableList = Collections.unmodifiableList(arrayList);
   }

   public String formatDrawAngel() {
      return this.isAnnihilatorYellow() ? "draw_angel" : "draw";
   }
}
