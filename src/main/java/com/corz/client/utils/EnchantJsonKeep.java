package com.corz.client.utils;

import com.corz.client.inventory.IngotBallUtils;
import com.corz.client.inventory.ProtectionCurseUtils;
import com.google.gson.JsonObject;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;

public final class EnchantJsonKeep {
   public EnchantJsonKeep.Kind kind;
   public String item;
   public EnchantJsonKeep.Action action;
   public boolean enabled = true;
   public int enchantLevel;
   public String enchant;
   public int keepAmount;

   public boolean matchesStack(ItemStack itemStack) {
      if (this.enabled && itemStack != null && !itemStack.isEmpty()) {
         return switch (this.kind) {
            case ITEM -> {
               Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(this.item);
               yield defaultedRegistry != null && itemStack.isOf(defaultedRegistry);
            }
            case BOOK_ENCHANT -> hasStoredEnchantmentAtLeast(itemStack, this.enchant, Math.max(1, this.enchantLevel));
            case ANY_ENCHANTED_BOOK -> itemStack.isOf(Items.ENCHANTED_BOOK);
         };
      } else {
         return false;
      }
   }

   public EnchantJsonKeep() {
      this.kind = EnchantJsonKeep.Kind.ITEM;
      this.action = EnchantJsonKeep.Action.KEEP;
      this.item = "Emerald";
      this.enchant = "Mending";
      this.enchantLevel = 1;
      this.keepAmount = 0;
   }

   public String formatRuleDescription() {
      String keepBookText = switch (this.kind) {
         case ITEM -> this.item;
         case BOOK_ENCHANT -> this.enchant + " " + ProtectionCurseUtils.getMax(this.enchantLevel) + " book";
         case ANY_ENCHANTED_BOOK -> "Any Enchanted Book";
      };
      String keepBookText2 = (this.action == EnchantJsonKeep.Action.KEEP || this.action == EnchantJsonKeep.Action.RESERVE) && this.keepAmount > 0
         ? " (min " + this.keepAmount + ")"
         : "";
      return keepBookText + " \u2192 " + this.action + keepBookText2;
   }

   public static EnchantJsonKeep createItemRule(String item2, EnchantJsonKeep.Action action2, int keepAmount2) {
      EnchantJsonKeep enchantJsonKeep = new EnchantJsonKeep();
      enchantJsonKeep.kind = EnchantJsonKeep.Kind.ITEM;
      enchantJsonKeep.item = item2;
      enchantJsonKeep.action = action2;
      enchantJsonKeep.keepAmount = keepAmount2;
      return enchantJsonKeep;
   }

   public static boolean hasStoredEnchantmentAtLeast(ItemStack itemStack, String enchantmentsDefaultText, int enchantmentsDefaultValue) {
      if (itemStack != null && !itemStack.isEmpty() && itemStack.isOf(Items.ENCHANTED_BOOK)) {
         ItemEnchantmentsComponent orDefault = itemStack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

         for (RegistryEntry registryEntry : orDefault.getEnchantments()) {
            if (ProtectionCurseUtils.canMatchesProtection(registryEntry, enchantmentsDefaultText) && orDefault.getLevel(registryEntry) >= enchantmentsDefaultValue) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", this.enabled);
      jsonObject.addProperty("kind", this.kind.name());
      jsonObject.addProperty("action", this.action.name());
      jsonObject.addProperty("item", this.item);
      jsonObject.addProperty("enchant", this.enchant);
      jsonObject.addProperty("enchantLevel", this.enchantLevel);
      jsonObject.addProperty("keepAmount", this.keepAmount);
      return jsonObject;
   }

   public static EnchantJsonKeep fromJson(JsonObject json) {
      EnchantJsonKeep enchantJsonKeep = new EnchantJsonKeep();

      try {
         if (json.has("enabled")) {
            enchantJsonKeep.enabled = json.get("enabled").getAsBoolean();
         }

         if (json.has("kind")) {
            enchantJsonKeep.kind = EnchantJsonKeep.Kind.valueOf(json.get("kind").getAsString());
         }

         if (json.has("action")) {
            enchantJsonKeep.action = EnchantJsonKeep.Action.valueOf(json.get("action").getAsString());
         }

         if (json.has("item")) {
            enchantJsonKeep.item = json.get("item").getAsString();
         }

         if (json.has("enchant")) {
            enchantJsonKeep.enchant = json.get("enchant").getAsString();
         }

         if (json.has("enchantLevel")) {
            enchantJsonKeep.enchantLevel = json.get("enchantLevel").getAsInt();
         }

         if (json.has("keepAmount")) {
            enchantJsonKeep.keepAmount = json.get("keepAmount").getAsInt();
         }
      } catch (Exception e) {
      }

      return enchantJsonKeep;
   }

   public static enum Action {
      KEEP,
      DROP,
      DROP_IMMEDIATELY,
      STORE,
      SELL,
      RESERVE,
      IGNORE;
   }

   public static enum Kind {
      ITEM,
      BOOK_ENCHANT,
      ANY_ENCHANTED_BOOK;
   }
}
