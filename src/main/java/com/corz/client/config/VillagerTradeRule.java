package com.corz.client.config;

import com.corz.client.inventory.IngotBallUtils;
import com.corz.client.inventory.OfferTrade;
import com.corz.client.inventory.ProtectionCurseUtils;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public final class VillagerTradeRule {
   public String enchant;
   public String item;
   public int maxEmeraldCost;
   public String profession;
   public VillagerTradeRule.Action action;
   public int enchantLevel;
   public int targetAmount;
   public boolean enabled = true;
   public boolean autoDrop;
   public VillagerTradeRule.OutputKind kind;
   public boolean buyContinuous;
   public int priority;
   public transient int tradedAmount;

   public static VillagerTradeRule createSellItemRule(String profession2, String id2, int priority2) {
      VillagerTradeRule enchantJsonProfession = new VillagerTradeRule();
      enchantJsonProfession.action = VillagerTradeRule.Action.SELL;
      enchantJsonProfession.kind = VillagerTradeRule.OutputKind.EMERALD;
      enchantJsonProfession.profession = profession2;
      enchantJsonProfession.item = id2;
      enchantJsonProfession.priority = priority2;
      return enchantJsonProfession;
   }

   public int getBuyItemValue() {
      return IngotBallUtils.getValue(this.getBuyItem());
   }

   public static VillagerTradeRule createFromOffer(OfferTrade offerTrade, String emeraldStackText) {
      if (offerTrade == null) {
         return null;
      } else {
         String profession2 = emeraldStackText != null && !emeraldStackText.isEmpty() ? emeraldStackText : "Any";
         if (offerTrade.emeraldStackActive && offerTrade.next != null) {
            String protectionFireText = ProtectionCurseUtils.getKey(offerTrade.next);
            if (ProtectionCurseUtils.getProtectionFire(protectionFireText) == null) {
               return null;
            } else {
               VillagerTradeRule librarianEnchant = createBuyEnchantedBookRule(protectionFireText, Math.max(1, offerTrade.level), 80, false);
               librarianEnchant.profession = profession2;
               return librarianEnchant;
            }
         } else if (!offerTrade.itemStack) {
            String id = IngotBallUtils.getId(offerTrade.emeraldStackItem);
            return id == null ? null : createBuyItemRule(profession2, id, 80);
         } else {
            Item item = offerTrade.primaryCostItem != null && offerTrade.primaryCostItem != Items.EMERALD ? offerTrade.primaryCostItem : (offerTrade.secondaryCostItem != null && offerTrade.secondaryCostItem != Items.EMERALD ? offerTrade.secondaryCostItem : null);
            String id2 = IngotBallUtils.getId(item);
            return id2 == null ? null : createSellItemRule(profession2, id2, 60);
         }
      }
   }

   public static VillagerTradeRule fromJson(JsonObject json) {
      VillagerTradeRule enchantJsonProfession = new VillagerTradeRule();

      try {
         if (json.has("enabled")) {
            enchantJsonProfession.enabled = json.get("enabled").getAsBoolean();
         }

         if (json.has("action")) {
            enchantJsonProfession.action = VillagerTradeRule.Action.valueOf(json.get("action").getAsString());
         }

         if (json.has("kind")) {
            enchantJsonProfession.kind = VillagerTradeRule.OutputKind.valueOf(json.get("kind").getAsString());
         }

         if (json.has("profession")) {
            enchantJsonProfession.profession = json.get("profession").getAsString();
         }

         if (json.has("enchant")) {
            enchantJsonProfession.enchant = json.get("enchant").getAsString();
         }

         if (json.has("enchantLevel")) {
            enchantJsonProfession.enchantLevel = json.get("enchantLevel").getAsInt();
         }

         if (json.has("item")) {
            enchantJsonProfession.item = json.get("item").getAsString();
         }

         if (json.has("maxEmeraldCost")) {
            enchantJsonProfession.maxEmeraldCost = json.get("maxEmeraldCost").getAsInt();
         }

         if (json.has("priority")) {
            enchantJsonProfession.priority = json.get("priority").getAsInt();
         }

         if (json.has("targetAmount")) {
            enchantJsonProfession.targetAmount = json.get("targetAmount").getAsInt();
         }

         if (json.has("buyContinuous")) {
            enchantJsonProfession.buyContinuous = json.get("buyContinuous").getAsBoolean();
         }

         if (json.has("autoDrop")) {
            enchantJsonProfession.autoDrop = json.get("autoDrop").getAsBoolean();
         }
      } catch (Exception e) {
      }

      return enchantJsonProfession;
   }

   public boolean isExperienceBottleRule() {
      return IngotBallUtils.canExperienceBottle(this.getBuyItem());
   }

   public boolean isTargetSold() {
      return this.targetAmount > 0 && this.tradedAmount >= this.targetAmount;
   }

   public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", this.enabled);
      jsonObject.addProperty("action", this.action.name());
      jsonObject.addProperty("kind", this.kind.name());
      jsonObject.addProperty("profession", this.profession);
      jsonObject.addProperty("enchant", this.enchant);
      jsonObject.addProperty("enchantLevel", this.enchantLevel);
      jsonObject.addProperty("item", this.item);
      jsonObject.addProperty("maxEmeraldCost", this.maxEmeraldCost);
      jsonObject.addProperty("priority", this.priority);
      jsonObject.addProperty("targetAmount", this.targetAmount);
      jsonObject.addProperty("buyContinuous", this.buyContinuous);
      jsonObject.addProperty("autoDrop", this.autoDrop);
      return jsonObject;
   }

   public static VillagerTradeRule createBuyExperienceBottleRule() {
      return createBuyItemRule("Cleric", IngotBallUtils.getId(Items.EXPERIENCE_BOTTLE), 80);
   }

   public VillagerTradeRule() {
      this.action = VillagerTradeRule.Action.BUY;
      this.kind = VillagerTradeRule.OutputKind.ENCHANTED_BOOK;
      this.profession = "Any";
      this.enchant = "Mending";
      this.enchantLevel = 1;
      this.item = "Pumpkin";
      this.maxEmeraldCost = 0;
      this.priority = 50;
      this.targetAmount = 0;
      this.buyContinuous = true;
      this.autoDrop = false;
      this.tradedAmount = 0;
   }

   public static VillagerTradeRule createBuyEnchantedBookRule(String protectionFireText, int enchantLevel, int priority2, boolean autoDrop2) {
      VillagerTradeRule enchantJsonProfession = new VillagerTradeRule();
      enchantJsonProfession.action = VillagerTradeRule.Action.BUY;
      enchantJsonProfession.kind = VillagerTradeRule.OutputKind.ENCHANTED_BOOK;
      enchantJsonProfession.profession = "Librarian";
      enchantJsonProfession.enchant = protectionFireText;
      enchantJsonProfession.enchantLevel = enchantLevel;
      enchantJsonProfession.priority = priority2;
      enchantJsonProfession.autoDrop = autoDrop2;
      return enchantJsonProfession;
   }

   public String formatTargetName() {
      return switch (this.kind) {
         case ENCHANTED_BOOK -> this.enchantLevel > 1 ? this.enchant + " " + ProtectionCurseUtils.getMax(this.enchantLevel) : this.enchant;
         case EMERALD -> "Sell " + IngotBallUtils.formatBookIngot(this.item);
         case ITEM -> IngotBallUtils.formatBookIngot(this.item);
      };
   }

   public String formatDescription() {
      String professionEmeraldText = this.kind == VillagerTradeRule.OutputKind.EMERALD
         ? IngotBallUtils.formatBookIngot(this.item) + " \u2192 Emerald"
         : this.formatTargetName();
      return this.action + " " + professionEmeraldText + " (" + this.profession + ")";
   }

   public Item getBuyItem() {
      return this.action == VillagerTradeRule.Action.BUY && this.kind == VillagerTradeRule.OutputKind.ITEM
         ? IngotBallUtils.getDefaultedRegistry(this.item)
         : null;
   }

   public static VillagerTradeRule createBuyItemRule(String profession2, String item2, int priority2) {
      VillagerTradeRule enchantJsonProfession = new VillagerTradeRule();
      enchantJsonProfession.action = VillagerTradeRule.Action.BUY;
      enchantJsonProfession.kind = VillagerTradeRule.OutputKind.ITEM;
      enchantJsonProfession.profession = profession2;
      enchantJsonProfession.item = item2;
      enchantJsonProfession.priority = priority2;
      return enchantJsonProfession;
   }

   public boolean isEnabled() {
      return this.enabled && !this.isTargetSold();
   }

   public static enum Action {
      BUY,
      SELL;
   }

   public static enum OutputKind {
      ENCHANTED_BOOK,
      EMERALD,
      ITEM;
   }
}
