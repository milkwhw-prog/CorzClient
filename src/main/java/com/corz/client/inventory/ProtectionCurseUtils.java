package com.corz.client.inventory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ProtectionCurseUtils {
   private static final Map<String, RegistryKey<Enchantment>> PROTECTION_FIRE_MAP = new LinkedHashMap<>();
   public static final String ANY_LABEL = "Any";

   public static RegistryKey<Enchantment> getProtectionFire(String protectionFireText) {
      return protectionFireText == null ? null : PROTECTION_FIRE_MAP.get(protectionFireText);
   }

   public static String[] getProtectionFire() {
      String[] strings = new String[PROTECTION_FIRE_MAP.size() + 1];
      strings[0] = "Any";
      int protectionFireCounter = 1;

      for (String protectionFireText : PROTECTION_FIRE_MAP.keySet()) {
         strings[protectionFireCounter++] = protectionFireText;
      }

      return strings;
   }

   private ProtectionCurseUtils() {
   }

   private static String formatText(String protectionCurseText) {
      if (protectionCurseText != null && !protectionCurseText.isEmpty()) {
         String[] split = protectionCurseText.replace('_', ' ').split(" ");
         StringBuilder stringBuilder = new StringBuilder();

         for (String protectionCurseText2 : split) {
            if (!protectionCurseText2.isEmpty()) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append(' ');
               }

               stringBuilder.append(Character.toUpperCase(protectionCurseText2.charAt(0))).append(protectionCurseText2.substring(1));
            }
         }

         return stringBuilder.toString();
      } else {
         return "";
      }
   }

   public static boolean canMatchesProtection(RegistryEntry<Enchantment> registryEntry, String matchesProtectionText) {
      if (matchesProtectionText == null || matchesProtectionText.equals("Any")) {
         return true;
      } else if (registryEntry == null) {
         return false;
      } else {
         RegistryKey protection_fire_map = PROTECTION_FIRE_MAP.get(matchesProtectionText);
         if (protection_fire_map == null) {
            return false;
         } else if (registryEntry.matchesKey(protection_fire_map)) {
            return true;
         } else {
            Identifier orElse = registryEntry.getKey().map(RegistryKey::getValue).orElse(null);
            return orElse != null && orElse.equals(protection_fire_map.getValue());
         }
      }
   }

   public static String getKey(RegistryEntry<Enchantment> registryEntry) {
      if (registryEntry == null) {
         return "";
      } else {
         String orElse = getOrElse(registryEntry);

         for (Entry entryItem : PROTECTION_FIRE_MAP.entrySet()) {
            if (((RegistryKey)entryItem.getValue()).getValue().getPath().equals(orElse)) {
               return (String)entryItem.getKey();
            }
         }

         return formatText(orElse);
      }
   }

   public static String getMax(int protectionCurseValue) {
      return switch (protectionCurseValue) {
         case 1 -> "I";
         case 2 -> "II";
         case 3 -> "III";
         case 4 -> "IV";
         case 5 -> "V";
         case 6 -> "VI";
         case 7 -> "VII";
         case 8 -> "VIII";
         case 9 -> "IX";
         case 10 -> "X";
         default -> Integer.toString(protectionCurseValue);
      };
   }

   public static String getOrElse(RegistryEntry<Enchantment> registryEntry) {
      return registryEntry == null ? "" : registryEntry.getKey().map(registryKey -> registryKey.getValue().getPath()).orElse("");
   }

   static {
      PROTECTION_FIRE_MAP.put("Mending", Enchantments.MENDING);
      PROTECTION_FIRE_MAP.put("Unbreaking", Enchantments.UNBREAKING);
      PROTECTION_FIRE_MAP.put("Efficiency", Enchantments.EFFICIENCY);
      PROTECTION_FIRE_MAP.put("Fortune", Enchantments.FORTUNE);
      PROTECTION_FIRE_MAP.put("Silk Touch", Enchantments.SILK_TOUCH);
      PROTECTION_FIRE_MAP.put("Sharpness", Enchantments.SHARPNESS);
      PROTECTION_FIRE_MAP.put("Smite", Enchantments.SMITE);
      PROTECTION_FIRE_MAP.put("Bane of Arthropods", Enchantments.BANE_OF_ARTHROPODS);
      PROTECTION_FIRE_MAP.put("Looting", Enchantments.LOOTING);
      PROTECTION_FIRE_MAP.put("Sweeping Edge", Enchantments.SWEEPING_EDGE);
      PROTECTION_FIRE_MAP.put("Fire Aspect", Enchantments.FIRE_ASPECT);
      PROTECTION_FIRE_MAP.put("Knockback", Enchantments.KNOCKBACK);
      PROTECTION_FIRE_MAP.put("Protection", Enchantments.PROTECTION);
      PROTECTION_FIRE_MAP.put("Fire Protection", Enchantments.FIRE_PROTECTION);
      PROTECTION_FIRE_MAP.put("Blast Protection", Enchantments.BLAST_PROTECTION);
      PROTECTION_FIRE_MAP.put("Projectile Protection", Enchantments.PROJECTILE_PROTECTION);
      PROTECTION_FIRE_MAP.put("Feather Falling", Enchantments.FEATHER_FALLING);
      PROTECTION_FIRE_MAP.put("Thorns", Enchantments.THORNS);
      PROTECTION_FIRE_MAP.put("Respiration", Enchantments.RESPIRATION);
      PROTECTION_FIRE_MAP.put("Aqua Affinity", Enchantments.AQUA_AFFINITY);
      PROTECTION_FIRE_MAP.put("Depth Strider", Enchantments.DEPTH_STRIDER);
      PROTECTION_FIRE_MAP.put("Frost Walker", Enchantments.FROST_WALKER);
      PROTECTION_FIRE_MAP.put("Soul Speed", Enchantments.SOUL_SPEED);
      PROTECTION_FIRE_MAP.put("Swift Sneak", Enchantments.SWIFT_SNEAK);
      PROTECTION_FIRE_MAP.put("Power", Enchantments.POWER);
      PROTECTION_FIRE_MAP.put("Punch", Enchantments.PUNCH);
      PROTECTION_FIRE_MAP.put("Flame", Enchantments.FLAME);
      PROTECTION_FIRE_MAP.put("Infinity", Enchantments.INFINITY);
      PROTECTION_FIRE_MAP.put("Multishot", Enchantments.MULTISHOT);
      PROTECTION_FIRE_MAP.put("Piercing", Enchantments.PIERCING);
      PROTECTION_FIRE_MAP.put("Quick Charge", Enchantments.QUICK_CHARGE);
      PROTECTION_FIRE_MAP.put("Loyalty", Enchantments.LOYALTY);
      PROTECTION_FIRE_MAP.put("Impaling", Enchantments.IMPALING);
      PROTECTION_FIRE_MAP.put("Riptide", Enchantments.RIPTIDE);
      PROTECTION_FIRE_MAP.put("Channeling", Enchantments.CHANNELING);
      PROTECTION_FIRE_MAP.put("Luck of the Sea", Enchantments.LUCK_OF_THE_SEA);
      PROTECTION_FIRE_MAP.put("Lure", Enchantments.LURE);
      PROTECTION_FIRE_MAP.put("Density", Enchantments.DENSITY);
      PROTECTION_FIRE_MAP.put("Breach", Enchantments.BREACH);
      PROTECTION_FIRE_MAP.put("Wind Burst", Enchantments.WIND_BURST);
      PROTECTION_FIRE_MAP.put("Curse of Vanishing", Enchantments.VANISHING_CURSE);
      PROTECTION_FIRE_MAP.put("Curse of Binding", Enchantments.BINDING_CURSE);
   }
}
