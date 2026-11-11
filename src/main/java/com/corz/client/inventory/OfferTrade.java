package com.corz.client.inventory;

import java.util.Iterator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.TradeOffer;

public final class OfferTrade {
   public final int merchantExperience;
   public final String levelText;
   public final int specialPrice;
   public final int primaryCostCount;
   public final int recipeIndex;
   public final int demandBonus;
   public final boolean itemStack;
   public final RegistryEntry<Enchantment> next;
   private final String append;
   public final Item secondaryCostItem;
   public final int count2UnconfirmedFull;
   public final int level;
   public final int maxUses;
   public final ItemStack copy;
   public final Item emeraldStackItem;
   public final boolean emeraldStackActive;
   public final int uses2EnchantJson;
   public final boolean disabled;
   public final Item primaryCostItem;
   public final float priceMultiplier;
   public final int count3EnchantJson;
   public final int emeraldAutomateValue;

   public String formatLevelMax() {
      if (this.emeraldStackActive && !this.levelText.isEmpty()) {
         String levelText = ProtectionCurseUtils.getKey(this.next);
         int levelLimit = 1;

         try {
            if (this.next != null) {
               levelLimit = this.next.value().getMaxLevel();
            }
         } catch (Exception e) {
            levelLimit = this.level;
         }

         return levelLimit > 1 ? levelText + " " + ProtectionCurseUtils.getMax(this.level) : levelText;
      } else {
         return IngotBallUtils.getKey(this.emeraldStackItem);
      }
   }

   public static OfferTrade getDisplayedUses(TradeOffer tradeOffer, int displayedUsesValue) {
      ItemStack displayedFirstBuyItem = tradeOffer.getDisplayedFirstBuyItem();
      ItemStack displayedSecondBuyItem = tradeOffer.getDisplayedSecondBuyItem();
      ItemStack copy = tradeOffer.getSellItem().copy();
      Item item = displayedFirstBuyItem.isEmpty() ? null : displayedFirstBuyItem.getItem();
      int displayedUsesValue2 = displayedFirstBuyItem.getCount();
      Item item2 = displayedSecondBuyItem.isEmpty() ? null : displayedSecondBuyItem.getItem();
      int displayedUsesValue3 = displayedSecondBuyItem.isEmpty() ? 0 : displayedSecondBuyItem.getCount();
      RegistryEntry registryEntry = null;
      String displayedUsesText = "";
      int level4 = 0;
      if (copy.isOf(Items.ENCHANTED_BOOK)) {
         ItemEnchantmentsComponent orDefault = copy.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
         Iterator iterator = orDefault.getEnchantments().iterator();
         if (iterator.hasNext()) {
            RegistryEntry next2 = (RegistryEntry)iterator.next();
            registryEntry = next2;
            level4 = orDefault.getLevel(next2);
            displayedUsesText = ProtectionCurseUtils.getOrElse(next2);
         }
      }

      return new OfferTrade(
         displayedUsesValue,
         item,
         displayedUsesValue2,
         item2,
         displayedUsesValue3,
         copy,
         tradeOffer.getUses(),
         tradeOffer.getMaxUses(),
         tradeOffer.isDisabled(),
         tradeOffer.getSpecialPrice(),
         tradeOffer.getPriceMultiplier(),
         tradeOffer.getDemandBonus(),
         tradeOffer.getMerchantExperience(),
         registryEntry,
         displayedUsesText,
         level4
      );
   }

   public boolean isFlag() {
      return this.secondaryCostItem != null && this.count2UnconfirmedFull > 0;
   }

   public String getAppend() {
      return this.append;
   }

   private OfferTrade(
      int fullInventoryValue,
      Item item,
      int count,
      Item item2,
      int count2,
      ItemStack copy,
      int uses2,
      int maxUses2,
      boolean disabled2,
      int specialPrice2,
      float priceMultiplier2,
      int demandBonus2,
      int merchantExperience,
      RegistryEntry<Enchantment> next,
      String text,
      int level
   ) {
      this.recipeIndex = fullInventoryValue;
      this.primaryCostItem = item;
      this.primaryCostCount = count;
      this.secondaryCostItem = item2;
      this.count2UnconfirmedFull = count2;
      this.copy = copy;
      this.emeraldStackItem = copy.getItem();
      this.emeraldAutomateValue = copy.getCount();
      this.uses2EnchantJson = uses2;
      this.maxUses = maxUses2;
      this.disabled = disabled2;
      this.specialPrice = specialPrice2;
      this.priceMultiplier = priceMultiplier2;
      this.demandBonus = demandBonus2;
      this.merchantExperience = merchantExperience;
      this.itemStack = copy.isOf(Items.EMERALD);
      this.emeraldStackActive = copy.isOf(Items.ENCHANTED_BOOK);
      this.next = next;
      this.levelText = text;
      this.level = level;
      int count3 = 0;
      if (item == Items.EMERALD) {
         count3 = count;
      } else if (item2 == Items.EMERALD) {
         count3 = count2;
      }

      this.count3EnchantJson = count3;
      this.append = getTranslationKey(item)
         + "x"
         + count
         + "+"
         + getTranslationKey(item2)
         + "x"
         + count2
         + ">"
         + getTranslationKey(this.emeraldStackItem)
         + "x"
         + this.emeraldAutomateValue
         + (this.emeraldStackActive ? "#" + text + level : "");
   }

   public boolean isDisabled() {
      return !this.disabled && this.getMax() > 0;
   }

   private static String getTranslationKey(Item item2) {
      return item2 == null ? "-" : item2.getTranslationKey();
   }

   public int getMax() {
      return Math.max(0, this.maxUses - this.uses2EnchantJson);
   }

   public String formatText() {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.primaryCostItem != null) {
         stringBuilder.append(this.primaryCostCount).append(" ").append(IngotBallUtils.getKey(this.primaryCostItem));
      }

      if (this.isFlag()) {
         stringBuilder.append(" + ").append(this.count2UnconfirmedFull).append(" ").append(IngotBallUtils.getKey(this.secondaryCostItem));
      }

      return stringBuilder.length() == 0 ? "?" : stringBuilder.toString();
   }
}
