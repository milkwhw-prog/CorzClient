package com.corz.client.inventory;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.VillagerTradeRule;
import com.corz.client.config.VillagerTradeRule.Action;
import com.corz.client.config.VillagerTradeRule.OutputKind;
import com.corz.client.categories.bots.VillagerTradeBot.TradeFacts;
import com.corz.client.gui.ButtonMin;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackHelper;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.gui.PreviewText;
import com.corz.client.gui.RenderPillUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.ChestStorageUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.world.RoleDimension;
import com.corz.client.world.RoleDimension.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.joml.Matrix3x2fStack;
import com.corz.client.categories.bots.VillagerTradeBot;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.AmountPreviewUtils;
import com.corz.client.utils.MaxMax2;
import com.corz.client.utils.NoneCartographerUtils;
import com.corz.client.utils.ScannedSkipped;

public final class VillagerTrader extends GuiClickScreen {
   private final BooleanSetting booleanBase;
   private ScannedSkipped buttonScannedSkipped;
   private final Runnable chevronEarlierAction;
   private float smoothScrollAmount;
   private boolean autoActive;
   private final Runnable dropSideAction;
   private final Runnable overlaySideAction;
   private List<OfferTrade> clericBottlesItems;
   private String tradeClericText;
   private static final String TRADE = "Use this trade";
   private static final int soldFullValue = CardMatrixUtils.getValue(-7630437, -1750963, 0.65);
   private final Runnable start;
   private final Runnable[] actions;
   private final VillagerTradeBot villagerTradeBot;
   private TradeFacts tradeFacts;
   private final Runnable again;
   private int tradingTradeValue;
   private final float[] startAmounts;
   private final Runnable stop;
   private MatrixStackHelper matrixStackHelper;
   private boolean skippedLimActive;
   private final Runnable add;
   private String villagerBuyText;
   private static final String LEVEL_LEAST = "Level at least";
   private final Map<String, ItemStack> enchantLevelItemStackMap;
   private float cachedLeastMax;
   private final String[] villagerTraderTexts;
   private final Runnable xp;
   private final Runnable action;
   private int tradeVillagerValue;
   private static final int tradedRunValue = CardMatrixUtils.getValue(-7630437, -8393375, 0.45);
   private int lastThumbsValue;
   private float villagerTraderAmount;
   private final Runnable sideGenAction;
   private static final String STOP = "Stop after";
   private final IntConsumer intConsumer;
   private final Runnable cancel;
   private static final int[] chestTable = new int[]{0, 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
   private final String[] formattedTextCache;
   private final IntConsumer villagersSoldIntConsumer;
   private final List<Runnable> bottlesClericsItems;
   private final BooleanSetting nextSideLabels;
   private String schematicsText;
   private static final String BOTTLES = "Add XP bottles";
   private static final String START = "Or start with";
   private static final int[] chestValues = new int[]{0, 16, 32, 64, 128, 256, 512, 1024, 1728, 3456};
   private final float[] villagerTraderTable;
   private final Runnable back;
   private float cubeAmount;
   private final ItemStack itemStack;
   private static final Role[] tradingChangeOutputsEmeraldsTypes = new Role[]{Role.EMERALDS, Role.MATERIALS, Role.OUTPUTS, Role.DROP};
   private int villagerTraderValue;
   private static final String[] sideMarkTexts = new String[]{"Emerald chest", "Material chest", "Output chest", "Drop spot"};
   private final BooleanSetting sideLabelsBooleanBase;
   private static final String[] markTradingTexts = new String[]{"side:mark:emerald", "side:mark:material", "side:mark:output", "side:mark:drop"};
   private long timeMillis;
   private static final String GENERAL = "General";
   private static final String[] sideClearTexts = new String[]{"side:clear:emerald", "side:clear:material", "side:clear:output", "side:clear:drop"};
   private float thumbsAmount;
   private boolean buyVillagerActive;
   private float earlierLaterAmount;
   private float skippedLimAmount;
   private String append;
   private final Runnable[] markTradingRunnables;
   private final IntConsumer villagerTraderIntConsumer;
   private float autoAmount;
   private VillagerTrader.State tradesRunningType;
   private static final String MASTER_CLERICS_EMERALDS = "From Master Clerics  \u00b7  about 3 emeralds each";
   private VillagerTradeRule enchantJson;
   private final Runnable confirmAction;
   private static final String MAX_PRICE = "Max price";
   private float matrixAmount;
   private final Runnable laterDownAction;
   private long timeMillisThumbs;
   private float scrollOffset;
   private final MaxMax2 schematicScroller;
   private static final String YOUR_TRADER = "Set up your trader";
   private boolean settingsBackFlag;
   private long timeMillisTradeTrading;
   private float emeraldAutomateAmount;
   private boolean joinWorldFlag;
   private final IntConsumer targetAmountConsumer;
   private final IntConsumer emeraldCostConsumer;
   private static final String[] pressTradeTexts = new String[]{"Add a trade", "Mark your chests", "Press Start"};
   private static final String[] comesWalksTexts = new String[]{
      "Press Add trade, then right-click a villager and pick what to buy.",
      "An emerald chest to take from, an output chest or drop spot to fill.",
      "It walks, trades and comes back after villagers restock."
   };
   private final BooleanSetting villagerTraderBooleanBase;
   private final Map<String, Integer> levelIntegerMap;
   private final IntConsumer enchantLevelConsumer;
   private VillagerTradeRule enchantJsonBuyVillager;
   private static final String XP_BOTTLES_LABEL = "XP bottles";
   private float settingsClericAmount;
   private final Runnable sideLabelsAction;
   private final Runnable more = this::updateMatrixStackClericBottlesItems;
   private ButtonMin buttonMin;
   private final BooleanSetting navigationToggle;
   private int parseInt;
   private float viewportHeight;
   private static final String[] villagerAutoTexts = new String[]{"", "Novice ", "Apprentice ", "Journeyman ", "Expert ", "Master "};
   private final Runnable sideWalkAction;
   private static final String STOP_TRADING_CHANGE = "Stop trading to change chests";
   private final Map<String, String[]> villagerTraderStringArrayMap;
   private float outputAutoAmount;
   private static final Set<String> OVERLAY_STATUS_SET = Set.of("Auto Drop Output", "Auto Acquire Emeralds", "Navigation", "Villager ESP", "Status Overlay");

   private void renderSureSold(DrawContext context, String sureSoldText, float sureSoldValue, float matrixStackValue, float cubeSchematicValue, boolean sureSoldFlag, boolean villagerTraderFlag, Runnable runnable) {
      float cardMatrixFactor = villagerTraderFlag ? this.getMinButton(sureSoldText, sureSoldValue, matrixStackValue, cubeSchematicValue, 16.0F) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, sureSoldValue + cubeSchematicValue / 2.0F, matrixStackValue + 8.0F, this.previewRail.getButtonMinButton().getAmount(sureSoldText, 0.05F), 0.0F);

      try {
         MatrixStackUtilsRelease.render(context, sureSoldValue, matrixStackValue, cubeSchematicValue, 16.0F, cardMatrixFactor, 0.0F);
         if (sureSoldFlag) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, sureSoldValue + 1.0F, matrixStackValue + 1.0F, cubeSchematicValue - 2.0F, 14.0F, 4.0F, MatrixStackUtilsRelease.getValue(-1750963, 0.14F));
            String matrixStackText = "Sure?";
            float sureSoldValue5 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
            MatrixStackUtilsRelease.handle(matrixStackText, MatrixStackUtilsAmount.getAmount(sureSoldValue + (cubeSchematicValue - sureSoldValue5) / 2.0F), matrixStackValue, 16.0F, 0.82F, soldFullValue);
         } else {
            int sureSoldValue6 = villagerTraderFlag ? CardMatrixUtils.getValue(-7630437, soldFullValue, cardMatrixFactor) : -11117467;
            MatrixStackUtilsRelease.handle(
               IcoGenUtils.getIdentifier("x"),
               MatrixStackUtilsAmount.getAmount(sureSoldValue + (cubeSchematicValue - 8.0F) / 2.0F),
               MatrixStackUtilsAmount.getAmount(matrixStackValue + 4.0F),
               8.0F,
               sureSoldValue6
            );
         }
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (villagerTraderFlag) {
         this.handleMinButton(sureSoldText, sureSoldValue, matrixStackValue, cubeSchematicValue, 16.0F, runnable);
      }
   }

   private void updateBottlesCleric() {
      if (this.clericBottlesItems != null) {
         this.clericBottlesItems = null;
         this.bottlesClericsItems.clear();
         this.previewRail.updateFadeCrossfade();
      }
   }

   private void handleTraderVillager(String traderVillagerText, boolean traderVillagerFlag) {
      DisabledEnabled.getInstance().handleDisabledEnabled("Villager Trader", traderVillagerText, traderVillagerFlag);
   }

   private int getLevelMax(String levelText) {
      if (levelText != null && !"Any".equals(levelText)) {
         Integer levelMax = this.levelIntegerMap.get(levelText);
         if (levelMax != null) {
            return levelMax;
         } else {
            int botCrystalValue;
            try {
               var empty = getEmpty(levelText);
               botCrystalValue = empty.isPresent() ? ((Enchantment)((Reference)empty.get()).value()).getMaxLevel() : -1;
            } catch (Exception e) {
               botCrystalValue = -1;
            }

            if (botCrystalValue < 1) {
               return 1;
            } else {
               this.levelIntegerMap.put(levelText, botCrystalValue);
               return botCrystalValue;
            }
         }
      } else {
         return 5;
      }
   }

   @Override
   public void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
   }

   private void handle(float villagerTraderValue, float villagerTraderValue2, float villagerTraderBound, String villagerTraderText, String villagerTraderText2, int matrixStackValue) {
      MatrixStackUtilsRelease.handle(villagerTraderText, villagerTraderValue, villagerTraderValue2, 13.0F, 0.82F, -7630437);
      float villagerTraderValue5 = MatrixStackUtilsRelease.getAmount(villagerTraderText, 0.82F) + 10.0F;
      String formatTextsAmounts = this.formatTextsAmounts(villagerTraderText2, Math.max(10.0F, villagerTraderBound - villagerTraderValue5), 0.82F);
      float villagerTraderValue6 = MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F);
      MatrixStackUtilsRelease.handle(formatTextsAmounts, villagerTraderValue + villagerTraderBound - villagerTraderValue6, villagerTraderValue2, 13.0F, 0.82F, matrixStackValue);
   }

   private String formatBuyVillager(VillagerTradeRule enchantJson, boolean flag) {
      if (enchantJson == this.enchantJsonBuyVillager && flag == this.buyVillagerActive && this.villagerBuyText != null) {
         return this.villagerBuyText;
      } else {
         this.enchantJsonBuyVillager = enchantJson;
         this.buyVillagerActive = flag;
         int buyVillagerLimit = enchantJson.action == Action.BUY ? enchantJson.getBuyItemValue() : 0;
         String buyVillagerText = buyVillagerLimit > 0 ? NoneCartographerUtils.formatExpertMaster(buyVillagerLimit) + " " + enchantJson.profession : enchantJson.profession;
         String buyVillagerText2 = enchantJson.action == Action.SELL ? "Sell to a " + enchantJson.profession : ("Any".equals(enchantJson.profession) ? "Buy from any villager" : "Buy from a " + buyVillagerText);
         this.villagerBuyText = flag ? buyVillagerText2 + "  \u00b7  first choice" : buyVillagerText2;
         return this.villagerBuyText;
      }
   }

   private static String formatFarEye(List<RoleDimension> traderVillager) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null && !traderVillager.isEmpty()) {
         double farEyeBound = Double.MAX_VALUE;

         for (RoleDimension roleDimension : traderVillager) {
            farEyeBound = Math.min(farEyeBound, roleDimension.getVec3d().distanceTo(instance.player.getEyePos()));
         }

         return farEyeBound > 999.0 ? "Far" : Math.round(farEyeBound) + "m";
      } else {
         return "Set";
      }
   }

   @Override
   public int getShowSchematics() {
      return !this.settingsBackFlag && this.clericBottlesItems == null ? this.villagerTradeBot.getTargets().size() : 0;
   }

   private String formatTextsAmounts(String villagerTraderText, float villagerTraderBound, float villagerTraderDivisor) {
      if (villagerTraderText != null && !villagerTraderText.isEmpty()) {
         float villagerTraderValue = Math.max(0.0F, villagerTraderBound) / villagerTraderDivisor;

         for (int i = 0; i < 64; i++) {
            if (this.villagerTraderTable[i] == villagerTraderValue && villagerTraderText.equals(this.villagerTraderTexts[i])) {
               return this.formattedTextCache[i];
            }
         }

         int villagerTraderValue2 = this.villagerTraderValue;
         this.villagerTraderValue = (this.villagerTraderValue + 1) % 64;
         this.villagerTraderTexts[villagerTraderValue2] = villagerTraderText;
         this.villagerTraderTable[villagerTraderValue2] = villagerTraderValue;
         this.formattedTextCache[villagerTraderValue2] = MatrixStackUtilsAmount.getFontRenderer().formatText(villagerTraderText, villagerTraderValue);
         return this.formattedTextCache[villagerTraderValue2];
      } else {
         return "";
      }
   }

   private float getLeastMax() {
      if (this.cachedLeastMax < 0.0F) {
         this.cachedLeastMax = Math.max(
            MatrixStackUtilsRelease.getAmount("Max price", 0.82F),
            Math.max(MatrixStackUtilsRelease.getAmount("Stop after", 0.82F), MatrixStackUtilsRelease.getAmount("Level at least", 0.82F))
         );
      }

      return this.cachedLeastMax;
   }

   private void renderEmeraldAutomate(DrawContext context, ButtonMin buttonMin, boolean emeraldAutomateFlag, int index, OfferTrade offerTrade, float emeraldAutomateValue, float emeraldAutomateBound, float cubeSchematicValue, float rocketsElytraBound) {
      VillagerTradeRule enchantJson = VillagerTradeBot.createTargetFromOffer(offerTrade, this.tradeClericText);
      boolean hasVar = enchantJson != null;
      boolean emeraldAutomateFlag2 = hasVar && this.villagerTradeBot.indexOfTarget(enchantJson) >= 0;
      String emeraldAutomateText = "offer:" + index;
      float emeraldAutomateLimit = emeraldAutomateFlag && hasVar ? buttonMin.getMin(emeraldAutomateText, emeraldAutomateValue, Math.max(emeraldAutomateBound, rocketsElytraBound), cubeSchematicValue, 30.0F) : 0.0F;
      MatrixStackUtilsRelease.render(context, emeraldAutomateValue, emeraldAutomateBound, cubeSchematicValue, 30.0F, index, emeraldAutomateLimit);
      ItemStack itemStack = offerTrade.itemStack ? getEmerald(offerTrade.primaryCostItem != Items.EMERALD ? offerTrade.primaryCostItem : offerTrade.secondaryCostItem) : offerTrade.copy;
      renderMatrixPop(context, itemStack, emeraldAutomateValue + 8.0F, emeraldAutomateBound + 7.0F, 1.0F);
      float matrixStackValue = emeraldAutomateValue + 32.0F;
      String emeraldAutomateText2;
      String emeraldAutomateText3;
      if (offerTrade.itemStack) {
         Item item = offerTrade.primaryCostItem != Items.EMERALD ? offerTrade.primaryCostItem : offerTrade.secondaryCostItem;
         int emeraldAutomateValue4 = offerTrade.primaryCostItem != Items.EMERALD ? offerTrade.primaryCostCount : offerTrade.count2UnconfirmedFull;
         emeraldAutomateText2 = "Sell " + IngotBallUtils.getKey(item);
         emeraldAutomateText3 = "Gives " + offerTrade.emeraldAutomateValue + (offerTrade.emeraldAutomateValue == 1 ? " emerald" : " emeralds") + " for " + emeraldAutomateValue4;
      } else {
         emeraldAutomateText2 = offerTrade.formatLevelMax() + (offerTrade.emeraldAutomateValue > 1 ? "  \u00d7" + offerTrade.emeraldAutomateValue : "");
         emeraldAutomateText3 = "Costs " + formatNothing(offerTrade);
      }

      String emeraldAutomateText4;
      int emeraldAutomateValue5;
      if (!hasVar) {
         emeraldAutomateText4 = "Can't automate";
         emeraldAutomateValue5 = -11117467;
      } else if (emeraldAutomateFlag2) {
         emeraldAutomateText4 = "In your list";
         emeraldAutomateValue5 = tradedRunValue;
      } else if (!offerTrade.isDisabled()) {
         emeraldAutomateText4 = "Sold out now";
         emeraldAutomateValue5 = soldFullValue;
      } else {
         emeraldAutomateText4 = offerTrade.getMax() + " left";
         emeraldAutomateValue5 = -7630437;
      }

      float emeraldAutomateValue6 = MatrixStackUtilsRelease.getAmount(emeraldAutomateText4, 0.82F);
      float emeraldAutomateValue7 = cubeSchematicValue - 32.0F - emeraldAutomateValue6 - 18.0F;
      int emeraldAutomateValue8 = hasVar ? CardMatrixUtils.getValue(-920587, -1, 0.2 * emeraldAutomateLimit) : -7630437;
      MatrixStackUtilsRelease.handle(MatrixStackUtilsAmount.getFontRenderer().formatText(emeraldAutomateText2, emeraldAutomateValue7 / 0.95F), matrixStackValue, emeraldAutomateBound + 4.0F, 11.0F, 0.95F, emeraldAutomateValue8);
      MatrixStackUtilsRelease.handle(MatrixStackUtilsAmount.getFontRenderer().formatText(emeraldAutomateText3, emeraldAutomateValue7 / 0.82F), matrixStackValue, emeraldAutomateBound + 15.0F, 10.0F, 0.82F, -7630437);
      float emeraldAutomateTotal = emeraldAutomateValue + cubeSchematicValue - 10.0F - emeraldAutomateValue6;
      if (hasVar && emeraldAutomateLimit > 0.01F && !emeraldAutomateFlag2) {
         String matrixStackText = "Add";
         float emeraldAutomateFactor = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F) + 14.0F;
         float releaseRightValue = emeraldAutomateValue + cubeSchematicValue - 8.0F - emeraldAutomateFactor;
         float amountMax = MatrixStackUtilsAmount.getAmount(emeraldAutomateBound + 7.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(emeraldAutomateLimit);

         try {
            MatrixStackUtilsRelease.renderReleaseRight(context, releaseRightValue, amountMax, emeraldAutomateFactor, 16.0F, 1.0F);
            MatrixStackUtilsRelease.handle(matrixStackText, MatrixStackUtilsAmount.getAmount(releaseRightValue + 7.0F), amountMax, 16.0F, 0.82F, MatrixStackUtilsAmount.getReleaseRight());
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         emeraldAutomateTotal -= emeraldAutomateFactor * emeraldAutomateLimit;
      }

      MatrixStackUtilsRelease.handle(emeraldAutomateText4, emeraldAutomateTotal, emeraldAutomateBound, 30.0F, 0.82F, emeraldAutomateValue5);
      if (emeraldAutomateFlag && hasVar) {
         float max11 = Math.max(emeraldAutomateBound, rocketsElytraBound);
         float materialFactor = Math.min(emeraldAutomateBound + 30.0F, rocketsElytraBound + this.emeraldAutomateAmount) - max11;
         if (materialFactor > 1.0F && index < this.bottlesClericsItems.size()) {
            buttonMin.handle(emeraldAutomateText, emeraldAutomateValue, max11, cubeSchematicValue, materialFactor, this.bottlesClericsItems.get(index));
         }
      }
   }

   private static String formatEmeraldEmeralds(Item emerald, int emeraldEmeraldsValue) {
      return emerald == Items.EMERALD ? emeraldEmeraldsValue + (emeraldEmeraldsValue == 1 ? " emerald" : " emeralds") : emeraldEmeraldsValue + " " + IngotBallUtils.getKey(emerald);
   }

   private float getVillagersSold(DrawContext context, ButtonMin buttonMin, VillagerTradeRule enchantJsonProfession, int villagersSoldValue, float cubeSchematicValue, float configsLikeValue) {
      boolean guidePackageFlag = buttonMin.guidePackageFlag;
      float villagersSoldValue4 = buttonMin.cubeKnifeAmount + cubeSchematicValue;
      float villagersSoldValue5 = buttonMin.cubeSchematicAmount2Cube - cubeSchematicValue * 2.0F;
      MatrixStackUtilsRelease.render(context, villagersSoldValue4, configsLikeValue, 30.0F);
      renderMatrixPop(context, this.getEnchantLevel(enchantJsonProfession), villagersSoldValue4 + 3.0F, configsLikeValue + 3.0F, 1.5F);
      float scaleValue = villagersSoldValue4 + 30.0F + 10.0F;
      float villagerTraderBound = villagersSoldValue5 - 30.0F - 10.0F;
      MatrixStackUtilsAmount.handlePushScale(this.formatTextsAmounts(enchantJsonProfession.formatTargetName(), villagerTraderBound, 1.1F), scaleValue, configsLikeValue + 3.0F, 1.1F, -920587);
      MatrixStackUtilsRelease.handle(
         this.formatTextsAmounts(this.formatBuyVillager(enchantJsonProfession, villagersSoldValue == 0 && this.villagerTradeBot.getTargets().size() > 1), villagerTraderBound, 0.82F),
         scaleValue,
         configsLikeValue + 17.0F,
         11.0F,
         0.82F,
         -7630437
      );
      float min8 = Math.min(104.0F, villagersSoldValue5 - this.getLeastMax() - 8.0F);
      boolean villagersSoldFlag = min8 < 84.0F;
      if (villagersSoldFlag) {
         min8 = villagersSoldValue5;
      }

      float minusPlusValue = configsLikeValue + 30.0F + 12.0F;
      if (enchantJsonProfession.action == Action.BUY) {
         minusPlusValue = this.getMinusPlus(
            context,
            buttonMin,
            guidePackageFlag,
            "ed:price",
            "ed:price:m",
            minusPlusValue,
            villagersSoldValue4,
            villagersSoldValue5,
            min8,
            villagersSoldFlag,
            "Max price",
            enchantJsonProfession.maxEmeraldCost > 0 ? enchantJsonProfession.maxEmeraldCost + " emeralds" : "Any price",
            this.emeraldCostConsumer,
            this.intConsumer
         );
      }

      String villagersSoldText = enchantJsonProfession.targetAmount <= 0 ? "Never" : enchantJsonProfession.targetAmount + (enchantJsonProfession.action == Action.SELL ? " sold" : " bought");
      float minusPlus = this.getMinusPlus(
         context, buttonMin, guidePackageFlag, "ed:stop", "ed:stop:m", minusPlusValue, villagersSoldValue4, villagersSoldValue5, min8, villagersSoldFlag, "Stop after", villagersSoldText, this.targetAmountConsumer, this.villagersSoldIntConsumer
      );
      if (enchantJsonProfession.kind == OutputKind.ENCHANTED_BOOK && this.getLevelMax(enchantJsonProfession.enchant) > 1) {
         minusPlus = this.getMinusPlus(
            context,
            buttonMin,
            guidePackageFlag,
            "ed:level",
            "ed:level:m",
            minusPlus,
            villagersSoldValue4,
            villagersSoldValue5,
            min8,
            villagersSoldFlag,
            "Level at least",
            ProtectionCurseUtils.getMax(Math.max(1, enchantJsonProfession.enchantLevel)),
            this.enchantLevelConsumer,
            this.villagerTraderIntConsumer
         );
      }

      float cardMatrixFactor = guidePackageFlag ? buttonMin.getMin("ed:on", villagersSoldValue4, minusPlus, villagersSoldValue5, 20.0F) : 0.0F;
      float pillLimit = this.previewRail.getButtonMinButton().getAmount("ed:on:k", enchantJsonProfession.enabled ? 1.0F : 0.0F, 16.0F);
      handle(villagersSoldValue4, minusPlus, this.formatTextsAmounts("Use this trade", villagersSoldValue5 - 22.0F - 6.0F, 0.82F), cardMatrixFactor);
      RenderPillUtils.render(context, Math.round(villagersSoldValue4 + villagersSoldValue5 - 22.0F), Math.round(minusPlus + 5.0F), pillLimit, cardMatrixFactor);
      if (guidePackageFlag) {
         buttonMin.handle("ed:on", villagersSoldValue4, minusPlus, villagersSoldValue5, 20.0F, this.action);
      }

      float villagersSoldValue12 = minusPlus + 28.0F;
      float chevronEarlier = this.getChevronEarlier(context, buttonMin, guidePackageFlag, villagersSoldValue4, villagersSoldValue12, villagersSoldValue5, villagersSoldValue);
      float villagersSoldValue13 = chevronEarlier + 12.0F;
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, villagersSoldValue4, MatrixStackUtilsAmount.getAmount(villagersSoldValue13), villagersSoldValue5, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt()
      );
      float villagersSoldValue14 = villagersSoldValue13 + 8.0F;
      boolean iliIlllilIi = this.villagerTradeBot.isEnabled();
      String villagersSoldText2 = enchantJsonProfession.action == Action.SELL ? "Sold this run" : "Bought this run";
      this.handle(
         villagersSoldValue4,
         villagersSoldValue14,
         villagersSoldValue5,
         villagersSoldText2,
         enchantJsonProfession.targetAmount > 0 ? enchantJsonProfession.tradedAmount + " of " + enchantJsonProfession.targetAmount : Integer.toString(enchantJsonProfession.tradedAmount),
         enchantJsonProfession.tradedAmount > 0 ? tradedRunValue : -7630437
      );
      float villagersSoldTotal = villagersSoldValue14 + 13.0F;
      int villagersSoldLimit = enchantJsonProfession.action == Action.BUY ? enchantJsonProfession.getBuyItemValue() : 0;
      if (villagersSoldLimit > 0) {
         String villagersSoldText3 = "Any".equals(enchantJsonProfession.profession) ? "villagers" : enchantJsonProfession.profession + "s";
         this.handle(villagersSoldValue4, villagersSoldTotal, villagersSoldValue5, "Sold by", NoneCartographerUtils.formatExpertMaster(villagersSoldLimit) + " " + villagersSoldText3, -7630437);
         villagersSoldTotal += 13.0F;
      }

      TradeFacts tradeFacts = this.enchantJson == enchantJsonProfession ? this.tradeFacts : null;
      float villagersSold;
      if (iliIlllilIi && tradeFacts != null) {
         String villagersSoldText4 = tradeFacts.bestCost() >= 0
            ? tradeFacts.bestCost() + "e  \u00b7  " + tradeFacts.bestWhere()
            : (tradeFacts.unchecked() > 0 ? "Checking..." : "None in stock");
         this.handle(villagersSoldValue4, villagersSoldTotal, villagersSoldValue5, enchantJsonProfession.action == Action.SELL ? "Buyer" : "Cheapest now", villagersSoldText4, tradeFacts.bestCost() >= 0 ? -920587 : -7630437);
         float matrixStackValue = villagersSoldTotal + 13.0F;
         String villagerTraderText = tradeFacts.inStock()
            + " in stock  \u00b7  "
            + tradeFacts.restocking()
            + " restocking"
            + (tradeFacts.unchecked() > 0 ? "  \u00b7  " + tradeFacts.unchecked() + " unchecked" : "");
         this.handle(villagersSoldValue4, matrixStackValue, villagersSoldValue5, "Villagers", villagerTraderText, -7630437);
         villagersSold = matrixStackValue + 13.0F;
         if (villagersSoldLimit > 0 && tradeFacts.belowLevel() > 0) {
            int belowLevel = tradeFacts.belowLevel();
            this.handle(
               villagersSoldValue4,
               villagersSold,
               villagersSoldValue5,
               "Not " + NoneCartographerUtils.formatExpertMaster(villagersSoldLimit) + " yet",
               belowLevel + (belowLevel == 1 ? " villager" : " villagers"),
               -7630437
            );
            villagersSold += 13.0F;
         }
      } else {
         this.handle(villagersSoldValue4, villagersSoldTotal, villagersSoldValue5, "Villagers", "Shown while trading", -11117467);
         villagersSold = villagersSoldTotal + 13.0F;
      }

      String formatEmeraldsRight = this.villagerTradeBot.getPauseReason(enchantJsonProfession);
      if (!formatEmeraldsRight.isEmpty()) {
         String[] texts = this.getTexts("Paused: " + formatEmeraldsRight, villagersSoldValue5, 3, 0.82F);

         for (String textsItem : texts) {
            MatrixStackUtilsRelease.handle(textsItem, villagersSoldValue4, villagersSold, 13.0F, 0.82F, soldFullValue);
            villagersSold += 11.0F;
         }
      }

      return villagersSold;
   }

   private static int[] getChestMax(VillagerTradeRule enchantJsonProfession) {
      Item buyAction = enchantJsonProfession.getBuyItem();
      return buyAction != null && buyAction.getMaxCount() >= 64 ? chestValues : chestTable;
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseY, int glfwValue) {
      return !this.settingsBackFlag ? false : this.matrixStackHelper != null && this.matrixStackHelper.canAmountGlfw(matrixStackValue, mouseY, glfwValue);
   }

   private float getCubeSchematic(DrawContext context, ButtonMin buttonMin, float cubeSchematicBound, float cubeSchematicValue, boolean cubeSchematicFlag) {
      float villagerTraderBound = Math.min(300.0F, buttonMin.cubeSchematicAmount2Cube - cubeSchematicBound * 2.0F);
      float tradedReleaseValue = buttonMin.cubeKnifeAmount + (buttonMin.cubeSchematicAmount2Cube - villagerTraderBound) / 2.0F;
      float outputAutoAmount = villagerTraderBound - 28.0F;
      if (outputAutoAmount != this.outputAutoAmount) {
         this.outputAutoAmount = outputAutoAmount;

         for (int i = 0; i < comesWalksTexts.length; i++) {
            this.startAmounts[i] = Math.max(36.0F, 17.0F + this.getTexts(comesWalksTexts[i], outputAutoAmount, 4, 0.82F).length * 9.5F);
         }
      }

      float cubeSchematicTotal = 78.0F;

      for (float cubeSchematicValue4 : this.startAmounts) {
         cubeSchematicTotal += cubeSchematicValue4;
      }

      float priceValue = !cubeSchematicFlag && cubeSchematicTotal + cubeSchematicBound * 2.0F <= buttonMin.cubeSchematicAmount3Cube
         ? buttonMin.cubeSchematicAmount + Math.max(cubeSchematicBound, (buttonMin.cubeSchematicAmount3Cube - cubeSchematicTotal) / 2.0F)
         : buttonMin.cubeSchematicAmount + cubeSchematicBound - cubeSchematicValue;
      String formatTextsAmounts = this.formatTextsAmounts("Set up your trader", buttonMin.cubeSchematicAmount2Cube - cubeSchematicBound * 2.0F, 1.1F);
      MatrixStackUtilsAmount.handlePushScale(
         formatTextsAmounts,
         MatrixStackUtilsAmount.getAmount(buttonMin.cubeKnifeAmount + (buttonMin.cubeSchematicAmount2Cube - MatrixStackUtilsAmount.getAmount(formatTextsAmounts, 1.1F)) / 2.0F),
         priceValue,
         1.1F,
         -920587
      );
      float cubeSchematicTotal2 = priceValue + 22.0F;
      boolean cubeSchematicFlag2 = !this.villagerTradeBot.getStoragesForRole(Role.EMERALDS).isEmpty()
         || !this.villagerTradeBot.getStoragesForRole(Role.OUTPUTS).isEmpty()
         || !this.villagerTradeBot.getStoragesForRole(Role.DROP).isEmpty();

      for (int j = 0; j < pressTradeTexts.length; j++) {
         this.renderTradedRelease(context, tradedReleaseValue, cubeSchematicTotal2, villagerTraderBound, j + 1, pressTradeTexts[j], comesWalksTexts[j], j == 1 && cubeSchematicFlag2);
         cubeSchematicTotal2 += this.startAmounts[j];
      }

      float matrixStackValue = cubeSchematicTotal2 + 12.0F;
      MatrixStackUtilsRelease.handle(this.formatTextsAmounts("Or start with", villagerTraderBound, 0.82F), tradedReleaseValue, matrixStackValue, 12.0F, 0.82F, -7630437);
      float cubeSchematicValue7 = matrixStackValue + 14.0F;
      this.renderGuideMaster(context, buttonMin, tradedReleaseValue, cubeSchematicValue7, villagerTraderBound);
      return cubeSchematicValue7 + 30.0F;
   }

   @Override
   public boolean canMatrixStack(int matrixStackValue, int rendererValue) {
      return this.settingsBackFlag && this.matrixStackHelper != null && this.matrixStackHelper.canFlag(matrixStackValue, rendererValue);
   }

   private VillagerTradeRule getEnchantJson() {
      return this.getBotVillager(this.previewRail.getFadeValue());
   }

   private static void renderMatrixPop(DrawContext context, ItemStack stack2, float matrixPopValue, float matrixPopValue2, float matrixPopValue3) {
      if (stack2 != null && !stack2.isEmpty()) {
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();

         try {
            matrices.translate(matrixPopValue, matrixPopValue2);
            if (matrixPopValue3 != 1.0F) {
               matrices.scale(matrixPopValue3, matrixPopValue3);
            }

            DrawMatricesUtils.renderMatricesMatrix(context, stack2, 0, 0, 16);
         } finally {
            matrices.popMatrix();
         }
      }
   }

   private void updateTradeTrading() {
      int tradingTradeValue = this.previewRail.getFadeValue();
      VillagerTradeRule botVillager = this.getBotVillager(tradingTradeValue);
      if (botVillager != null) {
         if (this.villagerTradeBot.isEnabled()) {
            this.handleTraderVillager("Stop trading to remove a trade", false);
         } else {
            long timeMillis = System.currentTimeMillis();
            if (this.tradingTradeValue == tradingTradeValue && timeMillis - this.timeMillisTradeTrading <= 2500L) {
               this.tradingTradeValue = -1;
               this.villagerTradeBot.removeTarget(tradingTradeValue);
               this.handleTraderVillager("Removed " + botVillager.formatTargetName(), true);
               int unmodifiablelistSize = this.villagerTradeBot.getTargets().size();
               if (unmodifiablelistSize > 0) {
                  this.previewRail.handleCrossfadeFlag(Math.min(tradingTradeValue, unmodifiablelistSize - 1), false);
               }

               this.previewRail.updateFadeCrossfade();
            } else {
               this.tradingTradeValue = tradingTradeValue;
               this.timeMillisTradeTrading = timeMillis;
            }
         }
      }
   }

   private static ItemStack getEmerald(Item item2) {
      return item2 == null ? new ItemStack(Items.EMERALD) : new ItemStack(item2);
   }

   private void updateEnabledBot() {
      VillagerTradeRule enchantJson = this.getEnchantJson();
      if (enchantJson != null) {
         enchantJson.enabled = !enchantJson.enabled;
         this.villagerTradeBot.clearPause(enchantJson);
      }
   }

   private void render(DrawContext context, ButtonMin buttonMin, boolean villagerTraderFlag, String villagerTraderText, float villagerTraderValue, float villagerTraderValue2, float cubeSchematicValue, String identifierText, String villagerTraderText3, boolean villagerTraderFlag2, Runnable runnable) {
      float cardMatrixFactor = villagerTraderFlag && villagerTraderFlag2 ? buttonMin.getMin(villagerTraderText, villagerTraderValue, villagerTraderValue2, cubeSchematicValue, 16.0F) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, villagerTraderValue + cubeSchematicValue / 2.0F, villagerTraderValue2 + 8.0F, buttonMin.getMin(villagerTraderText, 0.05F), 0.0F);

      try {
         MatrixStackUtilsRelease.render(context, villagerTraderValue, villagerTraderValue2, cubeSchematicValue, 16.0F, cardMatrixFactor, 0.0F);
         int villagerTraderValue5 = villagerTraderFlag2 ? MatrixStackUtilsRelease.getReleaseRight(cardMatrixFactor, 0.0F) : -11117467;
         String formatTextsAmounts = this.formatTextsAmounts(villagerTraderText3, cubeSchematicValue - 8.0F, 0.82F);
         float villagerTraderValue6 = MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F);
         float villagerTraderLimit = identifierText == null ? 0.0F : 12.0F;
         if (villagerTraderLimit + villagerTraderValue6 > cubeSchematicValue - 10.0F) {
            villagerTraderLimit = 0.0F;
         }

         float matrixStackValue = MatrixStackUtilsAmount.getAmount(villagerTraderValue + (cubeSchematicValue - villagerTraderLimit - villagerTraderValue6) / 2.0F);
         if (villagerTraderLimit > 0.0F) {
            MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier(identifierText), matrixStackValue, MatrixStackUtilsAmount.getAmount(villagerTraderValue2 + 4.0F), 8.0F, villagerTraderValue5);
         }

         MatrixStackUtilsRelease.handle(formatTextsAmounts, matrixStackValue + villagerTraderLimit, villagerTraderValue2, 16.0F, 0.82F, villagerTraderValue5);
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (villagerTraderFlag && villagerTraderFlag2) {
         buttonMin.handle(villagerTraderText, villagerTraderValue, villagerTraderValue2, cubeSchematicValue, 16.0F, runnable);
      }
   }

   @Override
   public void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double minmaxValue, double matrixParseValue3, double matrixParseValue4, int button) {
      if (this.settingsBackFlag && this.matrixStackHelper != null) {
         this.matrixStackHelper.handleForDoubleInt(matrixParseValue, minmaxValue, button);
      }
   }

   @Override
   public boolean canMaxAmount(ScannedSkipped scannedSkipped, double villagerTraderValue, double villagerTraderValue2, double villagerTraderValue3) {
      this.scrollOffset = Math.max(0.0F, Math.min(Math.max(0.0F, this.skippedLimAmount - this.viewportHeight), this.scrollOffset - (float)villagerTraderValue3 * 24.0F));
      return true;
   }

   @Override
   protected Screen getJoinFlag() {
      return this.joinWorldFlag ? null : this.currentScreen;
   }

   @Override
   public float getAmount() {
      return 360.0F;
   }

   private static void handle(float villagerTraderValue, float minusPlus, String villagerTraderText, float cardMatrixFactor) {
      MatrixStackUtilsRelease.handle(villagerTraderText, villagerTraderValue, minusPlus, 20.0F, 0.82F, CardMatrixUtils.getValue(-5854026, -920587, cardMatrixFactor));
   }

   private Map<String, List<ConfigSetting>> getGeneralOverlay() {
      var linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("General", new ArrayList());

      for (ConfigSetting autoEspBase : this.villagerTradeBot.getSettings()) {
         if (!autoEspBase.isHiddenFromList() && !OVERLAY_STATUS_SET.contains(autoEspBase.getSettingName())) {
            String generalOverlayText = autoEspBase.getGroupName();
            ((java.util.List) linkedHashMap.computeIfAbsent(generalOverlayText == null ? "General" : generalOverlayText, obj -> new ArrayList<>())).add(autoEspBase);
         }
      }

      linkedHashMap.values().removeIf(obj2 -> ((List) obj2).isEmpty());
      return linkedHashMap;
   }

   private float getDropTrading(DrawContext context, float dropTradingValue, float villagerTraderBound, float skippedLimValue) {
      float dropTradingValue4 = getAmount(context, dropTradingValue, villagerTraderBound, skippedLimValue, "Storage");
      boolean dropTradingFlag = this.villagerTradeBot.isEnabled();
      boolean dropTradingFlag2 = false;
      float dropTrading = dropTradingValue4;

      for (int i = 0; i < tradingChangeOutputsEmeraldsTypes.length; i++) {
         var traderVillager = this.villagerTradeBot.getStoragesForRole(tradingChangeOutputsEmeraldsTypes[i]);
         if (tradingChangeOutputsEmeraldsTypes[i] == Role.DROP && !traderVillager.isEmpty()) {
            dropTradingFlag2 = true;
         }

         this.renderMarkTrading(context, i, traderVillager, dropTradingValue, dropTrading, villagerTraderBound, dropTradingFlag);
         dropTrading += 20.0F;
      }

      if (this.booleanBase != null) {
         String dropTradingText = dropTradingFlag2 ? "Drop items at the spot" : "Throw bought items away";
         this.render(context, "side:drop", "side:drop:on", dropTradingValue, dropTrading, villagerTraderBound, dropTradingText, this.booleanBase, this.dropSideAction);
         dropTrading += 20.0F;
      }

      if (dropTradingFlag) {
         MatrixStackUtilsRelease.handle(this.formatTextsAmounts("Stop trading to change chests", villagerTraderBound, 0.82F), dropTradingValue, dropTrading, 13.0F, 0.82F, -11117467);
         dropTrading += 13.0F;
      }

      return dropTrading;
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      if (this.settingsBackFlag) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.renderAmountCube(context, buttonMin);
         }
      } else if (this.clericBottlesItems != null) {
         this.renderCubeAmountClericBottlesItems(context, buttonMin);
      } else {
         float cubeSchematicValue = buttonMin.cubeSchematicAmount2Cube < 180.0F ? 10.0F : 14.0F;
         float cubeSchematicValue2 = MatrixStackUtilsAmount.getAmount(this.schematicScroller.getDisplayedScroll());
         boolean cubeSchematicFlag = !this.previewRail.getNonMaxFlag().isAmount();
         float cubeSchematic3;
         if (this.villagerTradeBot.getTargets().isEmpty()) {
            cubeSchematic3 = this.getCubeSchematic(context, buttonMin, cubeSchematicValue, cubeSchematicValue2, cubeSchematicFlag);
         } else {
            VillagerTradeRule botVillager = this.getBotVillager(buttonMin.selectedIndex);
            cubeSchematic3 = botVillager == null ? buttonMin.cubeSchematicAmount + cubeSchematicValue - cubeSchematicValue2 : this.getVillagersSold(context, buttonMin, botVillager, buttonMin.selectedIndex, cubeSchematicValue, buttonMin.cubeSchematicAmount + cubeSchematicValue - cubeSchematicValue2);
         }

         if (cubeSchematicFlag) {
            cubeSchematic3 = this.getCubeMin(context, buttonMin, cubeSchematicValue, cubeSchematic3);
         }

         if (buttonMin.guidePackageFlag) {
            this.schematicScroller.handleMaxAmount(cubeSchematic3 + cubeSchematicValue2 + cubeSchematicValue - buttonMin.cubeSchematicAmount, buttonMin.cubeSchematicAmount3Cube);
         }
      }
   }

   @Override
   public void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseReleasedValue, int button) {
      if (this.settingsBackFlag && this.matrixStackHelper != null) {
         this.matrixStackHelper.handle(matrixStackValue, mouseReleasedValue, button);
      }
   }

   private void handleTradeTrading(int tradeTradingValue) {
      int tradeTradingLimit = this.previewRail.getFadeValue();
      if (tradeTradingLimit >= 0) {
         this.tradingTradeValue = -1;
         int tradesTrade = this.villagerTradeBot.moveTarget(tradeTradingLimit, tradeTradingValue);
         if (tradesTrade != tradeTradingLimit) {
            this.previewRail.handleCrossfadeFlag(tradesTrade, false);
         }
      }
   }

   private void renderTradedRelease(DrawContext context, float tradedReleaseValue, float tradedReleaseValue2, float tradedReleaseValue3, int logoProfileCounter, String tradedReleaseText, String villagerTraderText, boolean tradedReleaseFlag) {
      float corzLicenseLimit = 9.0F;
      float tradedReleaseValue6 = tradedReleaseValue + corzLicenseLimit;
      float tradedReleaseValue7 = tradedReleaseValue2 + corzLicenseLimit;
      int n7 = tradedReleaseFlag ? tradedRunValue : MatrixStackUtilsRelease.getValue(MatrixEspUtils.releaseRightValue, 0.85F);
      MatrixPeekUtilsRender.renderMatrixPeek(
         context,
         tradedReleaseValue6 - corzLicenseLimit,
         tradedReleaseValue7 - corzLicenseLimit,
         corzLicenseLimit * 2.0F,
         corzLicenseLimit * 2.0F,
         corzLicenseLimit,
         MatrixStackUtilsRelease.getValue(tradedReleaseFlag ? -8393375 : MatrixEspUtils.releaseRightValue, 0.12F)
      );
      MatrixPeekUtilsRender.renderMatrixPeek(context, tradedReleaseValue6 - corzLicenseLimit, tradedReleaseValue7 - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, MatrixPeekUtilsRender.getAmount() * 1.5F, n7);
      String matrixStackText = Integer.toString(logoProfileCounter);
      float tradedReleaseValue9 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
      MatrixStackUtilsRelease.handle(
         matrixStackText, MatrixStackUtilsAmount.getAmount(tradedReleaseValue6 - tradedReleaseValue9 / 2.0F), tradedReleaseValue7 - corzLicenseLimit, corzLicenseLimit * 2.0F, 0.82F, tradedReleaseFlag ? tradedRunValue : -920587
      );
      float matrixStackValue = tradedReleaseValue + corzLicenseLimit * 2.0F + 10.0F;
      float villagerTraderBound = tradedReleaseValue3 - (matrixStackValue - tradedReleaseValue);
      MatrixStackUtilsRelease.handle(this.formatTextsAmounts(tradedReleaseText, villagerTraderBound, 0.95F), matrixStackValue, tradedReleaseValue2, 11.0F, 0.95F, -920587);
      String[] texts = this.getTexts(villagerTraderText, villagerTraderBound, 4, 0.82F);

      for (int i = 0; i < texts.length; i++) {
         MatrixStackUtilsRelease.handle(texts[i], matrixStackValue, tradedReleaseValue2 + 11.0F + i * 9.5F, 10.0F, 0.82F, -7630437);
      }
   }

   public static String getAppend(String appendText) {
      MinecraftClient instance = MinecraftClient.getInstance();
      String appendText2 = appendText == null ? "" : appendText.trim().toLowerCase(Locale.ROOT);
      VillagerTradeBot villagerTradeBot = ModuleRegistry.getModule(VillagerTradeBot.class) instanceof VillagerTradeBot villagerTradeBot2 ? villagerTradeBot2 : null;
      if (villagerTradeBot == null) {
         return "trader: module missing";
      } else if (appendText2.equals("open")) {
         handleScreenInstance(villagerTradeBot);
         return "trader: opened";
      } else if (appendText2.equals("demo")) {
         handleScreenInstance(villagerTradeBot, getEmeraldUnbreaking(), "Librarian", 4);
         return "trader: demo offers opened";
      } else if (appendText2.startsWith("demo cleric")) {
         int appendValue = 5;

         try {
            if (appendText2.length() > 11) {
               appendValue = Math.max(1, Math.min(5, Integer.parseInt(appendText2.substring(11).trim())));
            }
         } catch (NumberFormatException e) {
         }

         handleScreenInstance(villagerTradeBot, getEmeraldBottle(appendValue), "Cleric", appendValue);
         return "trader: cleric demo (level " + appendValue + ") opened";
      } else if (instance.currentScreen instanceof VillagerTrader villagerTrader) {
         String[] split = appendText2.split("\\s+");

         try {
            String appendText3 = split[0];
            switch (appendText3) {
               case "settings":
                  villagerTrader.updateMatrixStackClericBottlesItems();
                  break;
               case "back":
                  villagerTrader.updateMatrixStack();
                  break;
               case "add":
                  villagerTrader.updateTradeJoin();
                  break;
               case "start":
                  villagerTrader.updateTradeBot();
                  break;
               case "stop":
                  villagerTrader.updateBotVillager();
                  break;
               case "cancel":
                  villagerTrader.updateBottlesCleric();
                  break;
               case "pick":
                  villagerTrader.handleClericBottles(Integer.parseInt(split[1]));
                  break;
               case "sel":
                  villagerTrader.previewRail.handleCrossfadeFlag(Integer.parseInt(split[1]), true);
                  break;
               case "mark":
                  villagerTrader.handleTradingChangeForInt(Integer.parseInt(split[1]));
                  break;
               case "clear":
                  villagerTrader.handleTradingChange(Integer.parseInt(split[1]));
                  break;
               case "up":
                  villagerTrader.handleTradeTrading(-1);
                  break;
               case "down":
                  villagerTrader.handleTradeTrading(1);
                  break;
               case "remove":
                  villagerTrader.updateTradeTrading();
                  break;
               case "price":
                  villagerTrader.handleEmeraldCost(Integer.parseInt(split[1]));
                  break;
               case "xp":
                  villagerTrader.updateBottlesTrade();
                  break;
               case "stopafter":
                  villagerTrader.handleTargetAmount(Integer.parseInt(split[1]));
                  break;
               default:
                  return "trader: unknown " + appendText;
            }
         } catch (RuntimeException e2) {
            return "trader: bad args " + appendText;
         }

         return "trader: "
            + appendText2
            + " -> state="
            + villagerTrader.tradesRunningType
            + " settingsMode="
            + villagerTrader.settingsBackFlag
            + " picking="
            + (villagerTrader.clericBottlesItems != null)
            + " trades="
            + villagerTradeBot.getTargets().size()
            + " selected="
            + villagerTrader.previewRail.getFadeValue();
      } else {
         return "trader: not open";
      }
   }

   private void updateTradeJoin() {
      if (this.villagerTradeBot.isEnabled()) {
         this.handleTraderVillager("Stop trading first, then add the trade", false);
      } else if (!this.villagerTradeBot.beginTradePick()) {
         this.handleTraderVillager("Join a world first", false);
      } else {
         this.joinWorldFlag = true;
         this.updateRailPreview();
      }
   }

   private VillagerTradeRule getBotVillager(int index) {
      var unmodifiableList = this.villagerTradeBot.getTargets();
      return index >= 0 && index < unmodifiableList.size() ? (VillagerTradeRule)unmodifiableList.get(index) : null;
   }

   private float getChevronEarlier(DrawContext context, ButtonMin buttonMin, boolean chevronEarlierFlag, float chevronEarlierValue, float matrixStackValue, float villagerTraderBound, int chevronEarlierLimit) {
      float chevronEarlierBound = 62.0F;
      float chevronEarlierValue4 = chevronEarlierValue + 62.0F + 4.0F;
      float chevronEarlierValue5 = chevronEarlierValue + villagerTraderBound - 70.0F;
      float cubeSchematicValue = 70.0F;
      float matrixStackValue2 = matrixStackValue;
      boolean chevronEarlierFlag2 = true;
      if (villagerTraderBound < 206.0F) {
         float chevronEarlierLimit2 = (villagerTraderBound - 8.0F) / 3.0F;
         if (chevronEarlierLimit2 >= 40.0F) {
            cubeSchematicValue = chevronEarlierLimit2;
            chevronEarlierBound = chevronEarlierLimit2;
            chevronEarlierValue4 = chevronEarlierValue + chevronEarlierLimit2 + 4.0F;
            chevronEarlierValue5 = chevronEarlierValue4 + chevronEarlierLimit2 + 4.0F;
         } else {
            chevronEarlierBound = (villagerTraderBound - 4.0F) / 2.0F;
            chevronEarlierValue4 = chevronEarlierValue + chevronEarlierBound + 4.0F;
            chevronEarlierValue5 = chevronEarlierValue;
            cubeSchematicValue = villagerTraderBound;
            matrixStackValue2 = matrixStackValue + 16.0F + 4.0F;
         }

         chevronEarlierFlag2 = 12.0F + this.getEarlierLater() <= Math.min(chevronEarlierBound, cubeSchematicValue) - 10.0F;
      }

      int unmodifiablelistSize = this.villagerTradeBot.getTargets().size();
      this.render(context, buttonMin, chevronEarlierFlag, "ed:up", chevronEarlierValue, matrixStackValue, chevronEarlierBound, chevronEarlierFlag2 ? "chevron-up" : null, "Earlier", chevronEarlierLimit > 0, this.chevronEarlierAction);
      this.render(context, buttonMin, chevronEarlierFlag, "ed:down", chevronEarlierValue4, matrixStackValue, chevronEarlierBound, chevronEarlierFlag2 ? "chevron-down" : null, "Later", chevronEarlierLimit < unmodifiablelistSize - 1, this.laterDownAction);
      boolean chevronEarlierFlag3 = this.tradingTradeValue == chevronEarlierLimit;
      if (chevronEarlierFlag3) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, chevronEarlierValue5 - 1.0F, matrixStackValue2 - 1.0F, cubeSchematicValue + 2.0F, 18.0F, 6.0F, MatrixStackUtilsRelease.getValue(-1750963, 0.16F));
      }

      this.render(context, buttonMin, chevronEarlierFlag, "ed:remove", chevronEarlierValue5, matrixStackValue2, cubeSchematicValue, chevronEarlierFlag2 ? "x" : null, chevronEarlierFlag3 ? "Confirm" : "Remove", true, this.confirmAction);
      return matrixStackValue2 + 16.0F;
   }

   private void handleTargetAmount(int targetLimit) {
      VillagerTradeRule enchantJson = this.getEnchantJson();
      if (enchantJson != null) {
         int[] chestMax = getChestMax(enchantJson);
         int index = 0;

         for (int i = 0; i < chestMax.length; i++) {
            if (chestMax[i] <= enchantJson.targetAmount) {
               index = i;
            }
         }

         int max2 = Math.max(0, Math.min(chestMax.length - 1, index + (targetLimit > 0 ? 1 : -1)));
         if (targetLimit < 0 && chestMax[index] < enchantJson.targetAmount) {
            max2 = index;
         }

         enchantJson.targetAmount = chestMax[max2];
         this.villagerTradeBot.clearPause(enchantJson);
      }
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 600.0F;
   }

   @Override
   public boolean isClericLimits() {
      return this.settingsBackFlag || this.clericBottlesItems != null;
   }

   private static void handle(BooleanSetting booleanBase) {
      if (booleanBase != null) {
         booleanBase.handle(!booleanBase.isEnabled());
      }
   }

   private void renderGuideMaster(DrawContext context, ButtonMin buttonMin, float guideMasterValue, float matrixStackValue, float cubeSchematicValue) {
      boolean guidePackageFlag = buttonMin.guidePackageFlag;
      float matrixStackLimit = guidePackageFlag ? buttonMin.getMin("guide:xp", guideMasterValue, matrixStackValue, cubeSchematicValue, 30.0F) : 0.0F;
      MatrixStackUtilsRelease.render(context, guideMasterValue, matrixStackValue, cubeSchematicValue, 30.0F, 0, matrixStackLimit);
      renderMatrixPop(context, this.itemStack, guideMasterValue + 8.0F, matrixStackValue + 7.0F, 1.0F);
      String guideMasterText = "Add";
      float materialFactor = MatrixStackUtilsRelease.getAmount(guideMasterText, 0.82F) + 14.0F;
      float releaseRightValue = guideMasterValue + cubeSchematicValue - 8.0F - materialFactor;
      float amountMax = MatrixStackUtilsAmount.getAmount(matrixStackValue + 7.0F);
      MatrixStackUtilsRelease.renderReleaseRight(context, releaseRightValue, amountMax, materialFactor, 16.0F, matrixStackLimit);
      MatrixStackUtilsRelease.handle(guideMasterText, MatrixStackUtilsAmount.getAmount(releaseRightValue + 7.0F), amountMax, 16.0F, 0.82F, MatrixStackUtilsAmount.getReleaseRight());
      float guideMasterValue8 = guideMasterValue + 32.0F;
      float villagerTraderBound = Math.max(0.0F, releaseRightValue - 8.0F - guideMasterValue8);
      int guideMasterValue10 = CardMatrixUtils.getValue(-920587, -1, 0.2 * matrixStackLimit);
      MatrixStackUtilsRelease.handle(this.formatTextsAmounts("XP bottles", villagerTraderBound, 0.95F), guideMasterValue8, matrixStackValue + 4.0F, 11.0F, 0.95F, guideMasterValue10);
      MatrixStackUtilsRelease.handle(
         this.formatTextsAmounts("From Master Clerics  \u00b7  about 3 emeralds each", villagerTraderBound, 0.82F), guideMasterValue8, matrixStackValue + 15.0F, 10.0F, 0.82F, -7630437
      );
      if (guidePackageFlag) {
         buttonMin.handle("guide:xp", guideMasterValue, matrixStackValue, cubeSchematicValue, 30.0F, this.xp);
      }
   }

   @Override
   protected void updateMatrixStack3() {
      super.updateMatrixStack3();
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }
   }

   public static void handleScreenInstance(VillagerTradeBot villagerTradeBot) {
      if (villagerTradeBot != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new VillagerTrader(instance.currentScreen, villagerTradeBot, null, "", 0));
      }
   }

   @Override
   public boolean isFlag() {
      return true;
   }

   @Override
   public float getMax() {
      this.updateMaxAuto();
      return this.villagerTraderAmount;
   }

   private void handleMinButton(String buttonText, float buttonValue, float matrixStackValue, float cubeSchematicValue, float materialFactor, Runnable runnable) {
      if (this.skippedLimActive) {
         if (this.buttonScannedSkipped != null) {
            this.buttonScannedSkipped.handle(buttonText, buttonValue, matrixStackValue, cubeSchematicValue, materialFactor, runnable);
         } else {
            this.buttonMin.handle(buttonText, buttonValue, matrixStackValue, cubeSchematicValue, materialFactor, runnable);
         }
      }
   }

   private float getMinButton(String buttonText, float buttonValue, float sideBuilding, float cubeSchematicValue, float materialFactor) {
      if (!this.skippedLimActive) {
         return 0.0F;
      } else {
         return this.buttonScannedSkipped != null ? this.buttonScannedSkipped.getMin(buttonText, buttonValue, sideBuilding, cubeSchematicValue, materialFactor) : this.buttonMin.getMin(buttonText, buttonValue, sideBuilding, cubeSchematicValue, materialFactor);
      }
   }

   @Override
   public void renderThumbsInspects(DrawContext context, int thumbsInspectsValue, float thumbsInspectsValue2, float thumbsInspectsValue3, float thumbsInspectsValue4) {
      VillagerTradeRule botVillager = this.getBotVillager(thumbsInspectsValue);
      if (botVillager != null) {
         renderMatrixPop(context, this.getEnchantLevel(botVillager), thumbsInspectsValue2 + (thumbsInspectsValue4 - 16.0F) / 2.0F, thumbsInspectsValue3 + (thumbsInspectsValue4 - 16.0F) / 2.0F, 1.0F);
      }
   }

   public static void handleScreenInstance(VillagerTradeBot villagerTradeBot, List<OfferTrade> list, String screenText, int screenValue) {
      if (villagerTradeBot != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new VillagerTrader(instance.currentScreen, villagerTradeBot, list == null ? List.of() : list, screenText, screenValue));
      }
   }

   private float getMinusPlus(
      DrawContext context,
      ButtonMin buttonMin,
      boolean minusPlusFlag,
      String minusPlusText,
      String buttonText,
      float minusPlusValue,
      float matrixStackValue,
      float villagerTraderBound,
      float minusPlusValue4,
      boolean villagersSoldFlag,
      String villagerTraderText,
      String villagersSoldText,
      IntConsumer intConsumer,
      IntConsumer intConsumer2
   ) {
      float buttonValue;
      float minusPlusValue6;
      float minusPlus;
      if (villagersSoldFlag) {
         buttonValue = matrixStackValue;
         minusPlusValue6 = MatrixStackUtilsAmount.getAmount(minusPlusValue + 13.0F);
         minusPlus = minusPlusValue6 + 16.0F + 4.0F;
      } else {
         buttonValue = matrixStackValue + villagerTraderBound - minusPlusValue4;
         minusPlusValue6 = MatrixStackUtilsAmount.getAmount(minusPlusValue + 2.0F);
         minusPlus = minusPlusValue + 20.0F;
      }

      float cardMatrixFactor = minusPlusFlag ? buttonMin.getMin(minusPlusText, buttonValue, minusPlusValue6, minusPlusValue4, 16.0F) : 0.0F;
      if (villagersSoldFlag) {
         MatrixStackUtilsRelease.handle(this.formatTextsAmounts(villagerTraderText, villagerTraderBound, 0.82F), matrixStackValue, minusPlusValue, 13.0F, 0.82F, -5854026);
      } else {
         handle(matrixStackValue, minusPlusValue, this.formatTextsAmounts(villagerTraderText, villagerTraderBound - minusPlusValue4 - 8.0F, 0.82F), 0.0F);
      }

      MatrixStackUtilsRelease.render(context, buttonValue, minusPlusValue6, minusPlusValue4, 16.0F, cardMatrixFactor, 0.0F);
      float peekPositionFactor = 7.0F;
      int minusPlusValue9 = CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor);
      MatrixStackUtilsRelease.handle(
         IcoGenUtils.getIdentifier("minus"),
         MatrixStackUtilsAmount.getAmount(buttonValue + 6.0F),
         MatrixStackUtilsAmount.getAmount(minusPlusValue6 + (16.0F - peekPositionFactor) / 2.0F),
         peekPositionFactor,
         minusPlusValue9
      );
      MatrixStackUtilsRelease.handle(
         IcoGenUtils.getIdentifier("plus"),
         MatrixStackUtilsAmount.getAmount(buttonValue + minusPlusValue4 - 6.0F - peekPositionFactor),
         MatrixStackUtilsAmount.getAmount(minusPlusValue6 + (16.0F - peekPositionFactor) / 2.0F),
         peekPositionFactor,
         minusPlusValue9
      );
      String formatTextsAmounts = this.formatTextsAmounts(villagersSoldText, minusPlusValue4 - (6.0F + peekPositionFactor + 3.0F) * 2.0F, 0.82F);
      float minusPlusValue10 = MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F);
      int minusPlusValue11 = CardMatrixUtils.getValue(-920587, CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.22), cardMatrixFactor);
      MatrixStackUtilsRelease.handle(formatTextsAmounts, MatrixStackUtilsAmount.getAmount(buttonValue + (minusPlusValue4 - minusPlusValue10) / 2.0F), minusPlusValue6, 16.0F, 0.82F, minusPlusValue11);
      if (!minusPlusFlag) {
         return minusPlus;
      } else {
         float cubeSchematicValue = minusPlusValue4 / 2.0F;
         buttonMin.handle(minusPlusText, buttonValue + cubeSchematicValue, minusPlusValue6, cubeSchematicValue, 16.0F, intConsumer);
         buttonMin.handle(buttonText, buttonValue, minusPlusValue6, cubeSchematicValue, 16.0F, intConsumer2);
         return minusPlus;
      }
   }

   private void handleTradingChange(int parseInt) {
      if (parseInt >= 0 && parseInt < tradingChangeOutputsEmeraldsTypes.length) {
         if (this.villagerTradeBot.isEnabled()) {
            this.handleTraderVillager("Stop trading to change your chests", false);
         } else {
            long timeMillis = System.currentTimeMillis();
            if (this.parseInt == parseInt && timeMillis - this.timeMillis <= 2500L) {
               this.parseInt = -1;
               int traderVillager = this.villagerTradeBot.removeStorages(tradingChangeOutputsEmeraldsTypes[parseInt]);
               this.handleTraderVillager(
                  traderVillager == 0 ? "Nothing to clear" : "Cleared the " + sideMarkTexts[parseInt].toLowerCase(Locale.ROOT) + (traderVillager > 1 ? "s" : ""), traderVillager > 0
               );
            } else {
               this.parseInt = parseInt;
               this.timeMillis = timeMillis;
            }
         }
      }
   }

   private void render(DrawContext context, String villagerTraderText, float villagerTraderValue, float sideBuilding, float cubeSchematicValue, String matrixStackText, boolean villagerTraderFlag, boolean villagerTraderFlag2, Runnable runnable) {
      float cardMatrixFactor = villagerTraderFlag ? this.getMinButton(villagerTraderText, villagerTraderValue, sideBuilding, cubeSchematicValue, 16.0F) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, villagerTraderValue + cubeSchematicValue / 2.0F, sideBuilding + 8.0F, this.previewRail.getButtonMinButton().getAmount(villagerTraderText, 0.05F), 0.0F);

      try {
         MatrixStackUtilsRelease.render(context, villagerTraderValue, sideBuilding, cubeSchematicValue, 16.0F, cardMatrixFactor, villagerTraderFlag2 && villagerTraderFlag ? 0.55F : 0.0F);
         int matrixStackValue = villagerTraderFlag ? MatrixStackUtilsRelease.getReleaseRight(cardMatrixFactor, villagerTraderFlag2 ? 0.55F : 0.0F) : -11117467;
         float villagerTraderValue6 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
         MatrixStackUtilsRelease.handle(matrixStackText, MatrixStackUtilsAmount.getAmount(villagerTraderValue + (cubeSchematicValue - villagerTraderValue6) / 2.0F), sideBuilding, 16.0F, 0.82F, matrixStackValue);
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (villagerTraderFlag) {
         this.handleMinButton(villagerTraderText, villagerTraderValue, sideBuilding, cubeSchematicValue, 16.0F, runnable);
      }
   }

   @Override
   public void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
      if (!this.settingsBackFlag && this.clericBottlesItems == null) {
         MatrixStackUtilsRelease.render(
            context, buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 5.0F, buttonMin.cubeSchematicAmount + 4.0F, buttonMin.cubeSchematicAmount3Cube - 8.0F, this.schematicScroller
         );
      }
   }

   private float getEarlierLater() {
      if (this.earlierLaterAmount < 0.0F) {
         this.earlierLaterAmount = Math.max(
            Math.max(MatrixStackUtilsRelease.getAmount("Earlier", 0.82F), MatrixStackUtilsRelease.getAmount("Later", 0.82F)),
            Math.max(MatrixStackUtilsRelease.getAmount("Remove", 0.82F), MatrixStackUtilsRelease.getAmount("Confirm", 0.82F))
         );
      }

      return this.earlierLaterAmount;
   }

   @Override
   public void renderSkippedLim(DrawContext context, ScannedSkipped min) {
      this.viewportHeight = min.skippedLimAmount;
      float max = Math.max(0.0F, this.skippedLimAmount - min.skippedLimAmount);
      if (this.scrollOffset > max) {
         this.scrollOffset = max;
      }

      float skippedLimValue = min.skippedScannedAmount - MatrixStackUtilsAmount.getAmount(this.thumbsAmount);
      this.buttonScannedSkipped = min;
      this.buttonMin = null;
      this.skippedLimActive = true;
      MatrixStackUtilsRelease.renderScissorEnable(
         context, min.sideBuildingAmount - 2.0F, min.skippedScannedAmount, min.sideWalkAmount + 4.0F, min.skippedLimAmount
      );

      float sideLabels2;
      try {
         float tradeBot = this.getTradeBot(context, min.sideBuildingAmount, min.sideWalkAmount, skippedLimValue);
         float dropTrading = this.getDropTrading(context, min.sideBuildingAmount, min.sideWalkAmount, tradeBot + 12.0F);
         sideLabels2 = this.getSideLabels(context, min.sideBuildingAmount, min.sideWalkAmount, dropTrading + 12.0F);
      } finally {
         context.disableScissor();
      }

      this.skippedLimAmount = sideLabels2 + MatrixStackUtilsAmount.getAmount(this.thumbsAmount) - min.skippedScannedAmount;
   }

   @Override
   public String formatTextsInspects(int inspectsValue) {
      VillagerTradeRule botVillager = this.getBotVillager(inspectsValue);
      return botVillager == null ? "" : botVillager.formatTargetName();
   }

   private boolean isClericBottles() {
      if (this.clericBottlesItems != null && "Cleric".equals(this.tradeClericText)) {
         for (OfferTrade offerTrade : this.clericBottlesItems) {
            if (IngotBallUtils.canExperienceBottle(offerTrade.emeraldStackItem)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static List<OfferTrade> getEmeraldUnbreaking() {
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      arrayList2.add(new TradeOffer(new TradedItem(Items.PAPER, 24), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
      arrayList2.add(new TradeOffer(new TradedItem(Items.EMERALD, 9), new ItemStack(Items.BOOKSHELF), 12, 1, 0.05F));
      arrayList2.add(new TradeOffer(new TradedItem(Items.EMERALD, 14), Optional.of(new TradedItem(Items.BOOK, 1)), getBookEnchanted("Mending", 1), 12, 5, 0.2F));
      arrayList2.add(
         new TradeOffer(new TradedItem(Items.EMERALD, 11), Optional.of(new TradedItem(Items.BOOK, 1)), getBookEnchanted("Unbreaking", 3), 12, 12, 10, 0.2F)
      );
      arrayList2.add(new TradeOffer(new TradedItem(Items.EMERALD, 1), new ItemStack(Items.LANTERN), 12, 5, 0.05F));
      arrayList2.add(new TradeOffer(new TradedItem(Items.BOOK, 4), new ItemStack(Items.EMERALD), 12, 10, 0.05F));

      for (int i = 0; i < arrayList2.size(); i++) {
         arrayList.add(OfferTrade.getDisplayedUses((TradeOffer)arrayList2.get(i), i));
      }

      return arrayList;
   }

   @Override
   public String formatSchematicsText() {
      if (this.settingsBackFlag) {
         return "More settings  \u00b7  villagers, emeralds, walking and timing";
      } else {
         String text;
         if (this.clericBottlesItems != null) {
            text = "o|" + this.tradeClericText + "|" + this.tradeVillagerValue;
         } else if (this.tradesRunningType == VillagerTrader.State.RUNNING) {
            text = "r|" + this.villagerTradeBot.getCachedStatusText();
         } else {
            text = this.tradesRunningType + "|" + this.villagerTradeBot.getTargets().size() + "|" + this.villagerTradeBot.getEmeraldCount();
         }

         if (!text.equals(this.schematicsText)) {
            this.schematicsText = text;
            if (this.clericBottlesItems != null) {
               boolean schematicsFlag = this.tradeClericText.isEmpty() || "Any".equals(this.tradeClericText);
               String schematicsText = this.tradeVillagerValue > 0 && this.tradeVillagerValue < villagerAutoTexts.length
                  ? villagerAutoTexts[this.tradeVillagerValue]
                  : "";
               this.append = schematicsFlag ? "Pick a trade from this villager" : "Pick a trade from this " + schematicsText + this.tradeClericText;
            } else if (this.tradesRunningType == VillagerTrader.State.RUNNING) {
               this.append = this.villagerTradeBot.getCachedStatusText();
            } else if (this.tradesRunningType == VillagerTrader.State.NO_TRADES) {
               this.append = "Add a trade, mark your chests, then press Start";
            } else {
               int unmodifiablelistSize = this.villagerTradeBot.getTargets().size();
               this.append = unmodifiablelistSize + (unmodifiablelistSize == 1 ? " trade" : " trades") + "  \u00b7  " + this.villagerTradeBot.getEmeraldCount() + " emeralds on you";
            }
         }

         return this.append;
      }
   }

   private String[] getTexts(String villagerTraderText, float villagerTraderLimit, int villagerTraderValue, float villagerTraderDivisor) {
      if (villagerTraderText != null && !villagerTraderText.isEmpty()) {
         String botCrystalText = villagerTraderText + '\u0000' + Math.round(villagerTraderLimit) + ':' + villagerTraderValue + ':' + villagerTraderDivisor;
         String[] texts = this.villagerTraderStringArrayMap.get(botCrystalText);
         if (texts != null) {
            return texts;
         } else {
            if (this.villagerTraderStringArrayMap.size() > 96) {
               this.villagerTraderStringArrayMap.clear();
            }

            var arrayList = new ArrayList<String>();
            String[] split = villagerTraderText.split(" ");
            StringBuilder stringBuilder = new StringBuilder();
            int index = 0;

            while (index < split.length) {
               String villagerTraderText3 = split[index];
               String matrixStackText = stringBuilder.length() == 0 ? villagerTraderText3 : stringBuilder + " " + villagerTraderText3;
               if (!(MatrixStackUtilsRelease.getAmount(matrixStackText, villagerTraderDivisor) <= villagerTraderLimit) && stringBuilder.length() != 0) {
                  arrayList.add(stringBuilder.toString());
                  stringBuilder.setLength(0);
                  if (arrayList.size() == villagerTraderValue - 1) {
                     StringBuilder stringBuilder2 = new StringBuilder();

                     for (int i = index; i < split.length; i++) {
                        stringBuilder2.append(i > index ? " " : "").append(split[i]);
                     }

                     stringBuilder.append((CharSequence)stringBuilder2);
                     break;
                  }
               } else {
                  stringBuilder.setLength(0);
                  stringBuilder.append(matrixStackText);
                  index++;
               }
            }

            if (stringBuilder.length() > 0) {
               arrayList.add(stringBuilder.toString());
            }

            for (int j = 0; j < arrayList.size(); j++) {
               if (MatrixStackUtilsRelease.getAmount((String)arrayList.get(j), villagerTraderDivisor) > villagerTraderLimit) {
                  arrayList.set(j, MatrixStackUtilsAmount.getFontRenderer().formatText((String)arrayList.get(j), villagerTraderLimit / villagerTraderDivisor));
               }
            }

            String[] array = arrayList.toArray(new String[0]);
            this.villagerTraderStringArrayMap.put(botCrystalText, array);
            return array;
         }
      } else {
         return new String[0];
      }
   }

   private float getSideLabels(DrawContext context, float sideLabelsValue, float cubeSchematicValue, float skippedLimValue) {
      float dropTrading = getAmount(context, sideLabelsValue, cubeSchematicValue, skippedLimValue, "Options") - 2.0F;
      float sideLabels = dropTrading;
      if (this.villagerTraderBooleanBase != null) {
         this.render(context, "side:gen", "side:gen:on", sideLabelsValue, dropTrading, cubeSchematicValue, "Make emeralds when short", this.villagerTraderBooleanBase, this.sideGenAction);
         sideLabels = dropTrading + 20.0F;
      }

      if (this.navigationToggle != null) {
         this.render(context, "side:walk", "side:walk:on", sideLabelsValue, sideLabels, cubeSchematicValue, "Walk around by itself", this.navigationToggle, this.sideWalkAction);
         sideLabels += 20.0F;
      }

      if (this.nextSideLabels != null) {
         this.render(context, "side:labels", "side:labels:on", sideLabelsValue, sideLabels, cubeSchematicValue, "Show villager labels", this.nextSideLabels, this.sideLabelsAction);
         sideLabels += 20.0F;
      }

      if (this.sideLabelsBooleanBase != null) {
         this.render(context, "side:overlay", "side:overlay:on", sideLabelsValue, sideLabels, cubeSchematicValue, "Status card on screen", this.sideLabelsBooleanBase, this.overlaySideAction);
         sideLabels += 20.0F;
      }

      return sideLabels;
   }

   private void updateTradeBot() {
      if (this.villagerTradeBot.getTargets().isEmpty()) {
         this.handleTraderVillager("Add a trade first", false);
      } else {
         boolean tradeBotFlag = false;

         for (VillagerTradeRule enchantJsonProfession : this.villagerTradeBot.getTargets()) {
            tradeBotFlag |= enchantJsonProfession.enabled;
         }

         if (!tradeBotFlag) {
            this.handleTraderVillager("Turn on at least one trade", false);
         } else {
            if (!this.villagerTradeBot.isEnabled()) {
               this.villagerTradeBot.setEnabled(true);
            }

            if (this.villagerTradeBot.isEnabled()) {
               this.joinWorldFlag = true;
               this.updateRailPreview();
            }
         }
      }
   }

   private float getTradeBot(DrawContext context, float tradeBotValue, float villagerTraderLimit, float skippedLimValue) {
      float tradeBotValue4 = getAmount(context, tradeBotValue, villagerTraderLimit, skippedLimValue, "Status");
      boolean tradeBotFlag = this.villagerTradeBot.isEnabled();
      int tradeBotValue5 = !tradeBotFlag ? -11117467 : (this.villagerTradeBot.isPaused() ? -1535700 : -8393375);
      String tradeBotText = tradeBotFlag
         ? this.villagerTradeBot.getCachedStatusText()
         : (this.tradesRunningType == VillagerTrader.State.NO_TRADES ? "Add a trade to begin" : "Stopped  \u00b7  ready to start");
      String[] texts = this.getTexts(tradeBotText, villagerTraderLimit - 5.0F - 6.0F, 2, 0.82F);
      float configsLikeValue = MatrixStackUtilsAmount.getAmount(tradeBotValue4 + 4.0F);
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, tradeBotValue, configsLikeValue, 5.0F, 5.0F, 2.5F, tradeBotFlag ? MatrixStackUtilsRelease.getValue(tradeBotValue5, 0.75F + 0.25F * ChestStorageUtils.getAmountFloat()) : tradeBotValue5
      );
      float tradeBotTotal = tradeBotValue4;

      for (String textsItem : texts) {
         MatrixStackUtilsRelease.handle(textsItem, tradeBotValue + 5.0F + 6.0F, tradeBotTotal, 13.0F, 0.82F, -920587);
         tradeBotTotal += 12.0F;
      }

      float tradeBotValue7 = tradeBotTotal + 4.0F;
      this.handle(tradeBotValue, tradeBotValue7, villagerTraderLimit, "Emeralds on you", Integer.toString(this.villagerTradeBot.getEmeraldCount()), -920587);
      float matrixStackValue = tradeBotValue7 + 13.0F;
      int soldTrades = this.villagerTradeBot.getTotalTradedCount();
      this.handle(tradeBotValue, matrixStackValue, villagerTraderLimit, "Traded this run", Integer.toString(soldTrades), soldTrades > 0 ? tradedRunValue : -7630437);
      float tradeBot = matrixStackValue + 13.0F;
      String villagerTraderText = tradeBotFlag ? this.villagerTradeBot.getStatusMessage() : "";
      if (!villagerTraderText.isEmpty()) {
         tradeBot += 2.0F;

         for (String getTextsItem : this.getTexts(villagerTraderText, villagerTraderLimit, 4, 0.82F)) {
            MatrixStackUtilsRelease.handle(getTextsItem, tradeBotValue, tradeBot, 13.0F, 0.82F, soldFullValue);
            tradeBot += 11.0F;
         }
      }

      return tradeBot;
   }

   private float getCubeMin(DrawContext context, ButtonMin buttonMin, float cubeValue, float tradeBot2) {
      float tradeBotValue = buttonMin.cubeKnifeAmount + cubeValue;
      float villagerTraderLimit = buttonMin.cubeSchematicAmount2Cube - cubeValue * 2.0F;
      this.buttonScannedSkipped = null;
      this.buttonMin = buttonMin;
      this.skippedLimActive = buttonMin.guidePackageFlag;
      tradeBot2 = this.getTradeBot(context, tradeBotValue, villagerTraderLimit, tradeBot2 + 12.0F);
      tradeBot2 = this.getDropTrading(context, tradeBotValue, villagerTraderLimit, tradeBot2 + 12.0F);
      return this.getSideLabels(context, tradeBotValue, villagerTraderLimit, tradeBot2 + 12.0F);
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.settingsBackFlag) {
         previewText.getClassPreviewText("back", "Back", this.back).getFlag().getClass073("done", "Done", this.back);
      } else if (this.clericBottlesItems == null) {
         previewText.getClassPreviewText("more", "More settings", this.more);
         switch (this.tradesRunningType) {
            case NO_TRADES:
               previewText.getFlag().getClass073("add", "Add trade", this.add);
               break;
            case READY:
               previewText.getClassPreviewText("add", "Add trade", this.add);
               if (!this.isBotVillager()) {
                  previewText.getClassPreviewText("xp", "Add XP bottles", this.xp);
               }

               previewText.getFlag().getClass073("start", "Start trading", this.start);
               break;
            case RUNNING:
               previewText.getFlag().getClassPreviewText("stop", "Stop trading", this.stop);
         }
      } else {
         boolean folderDoneFlag = this.isClericBottles() && !this.isBotVillager();
         previewText.getText(folderDoneFlag ? "XP bottles need a Master Cleric" : "Click a trade to add it").getFlag();
         if (folderDoneFlag) {
            previewText.getClassPreviewText("xp", "Add XP bottles", this.xp);
         }

         previewText.getClassPreviewText("again", "Other villager", this.again).getClassPreviewText("cancel", "Cancel", this.cancel);
      }
   }

   @Override
   public void handleThumbsAmount(float thumbsValue) {
      this.thumbsAmount = AmountPreviewUtils.getAmount(this.thumbsAmount, this.scrollOffset, 18.0F, thumbsValue, 0.05F);
      this.smoothScrollAmount = AmountPreviewUtils.getAmount(this.smoothScrollAmount, this.matrixAmount, 18.0F, thumbsValue, 0.05F);
      int value = this.previewRail.getFadeValue();
      if (value != this.lastThumbsValue) {
         this.lastThumbsValue = value;
         this.schematicScroller.handleAmountMax(0.0F);
      }

      this.schematicScroller.handleAmountMax(thumbsValue, this.previewRail.getButtonMin().thumbsActive);
      if (this.villagerTradeBot.isEnabled()) {
         this.tradesRunningType = VillagerTrader.State.RUNNING;
      } else {
         this.tradesRunningType = this.villagerTradeBot.getTargets().isEmpty() ? VillagerTrader.State.NO_TRADES : VillagerTrader.State.READY;
      }

      VillagerTradeRule enchantJson = this.getEnchantJson();
      long timeMillis = System.currentTimeMillis();
      if (enchantJson != this.enchantJson || timeMillis - this.timeMillisThumbs > 250L) {
         this.enchantJson = enchantJson;
         this.timeMillisThumbs = timeMillis;
         this.tradeFacts = enchantJson == null ? null : this.villagerTradeBot.getTradeFacts(enchantJson);
      }

      if (this.tradingTradeValue >= 0 && timeMillis - this.timeMillisTradeTrading > 2500L) {
         this.tradingTradeValue = -1;
      }

      if (this.parseInt >= 0 && timeMillis - this.timeMillis > 2500L) {
         this.parseInt = -1;
      }
   }

   private static ItemStack getBookEnchanted(String bookEnchantedText, int bookEnchantedBound) {
      try {
         var empty = getEmpty(bookEnchantedText);
         if (empty.isPresent()) {
            return EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry((RegistryEntry<Enchantment>)empty.get(), Math.max(1, bookEnchantedBound)));
         }
      } catch (Exception e) {
      }

      return new ItemStack(Items.ENCHANTED_BOOK);
   }

   @Override
   public boolean mouseScrolled(double mouseScrolledValue, double amountMaxForDouble, double mouseScrolledValue3, double consumerLimit) {
      if (!this.settingsBackFlag
         && this.clericBottlesItems == null
         && this.schematicScroller.isFlag()
         && this.previewRail.isFlag()
         && this.previewRail.getNonMaxFlag().canAmountMax(mouseScrolledValue, amountMaxForDouble)) {
         this.schematicScroller.handleAmount(consumerLimit, 24.0F);
         return true;
      } else {
         return super.mouseScrolled(mouseScrolledValue, amountMaxForDouble, mouseScrolledValue3, consumerLimit);
      }
   }

   private static Optional<Reference<Enchantment>> getEmpty(String emptyText) {
      MinecraftClient instance = MinecraftClient.getInstance();
      var protectionFire = ProtectionCurseUtils.getProtectionFire(emptyText);
      if (protectionFire != null && instance.world != null) {
         try {
            var optional = instance.world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
            return optional.isEmpty() ? Optional.empty() : ((Registry)optional.get()).getOptional(protectionFire);
         } catch (Exception e) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   @Override
   public String formatSoldBought(int soldBoughtValue) {
      VillagerTradeRule botVillager = this.getBotVillager(soldBoughtValue);
      if (botVillager == null) {
         return "";
      } else if (!botVillager.enabled) {
         return "Turned off";
      } else {
         String soldBoughtText = botVillager.targetAmount > 0
            ? botVillager.tradedAmount + "/" + botVillager.targetAmount
            : (botVillager.tradedAmount > 0 ? botVillager.tradedAmount + (botVillager.action == Action.SELL ? " sold" : " bought") : null);
         String soldBoughtText2 = botVillager.action == Action.SELL
            ? "Sell to " + botVillager.profession
            : (botVillager.maxEmeraldCost > 0 ? "Up to " + botVillager.maxEmeraldCost + "e" : "Any price");
         return soldBoughtText == null ? soldBoughtText2 : soldBoughtText2 + "  \u00b7  " + soldBoughtText;
      }
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int modifiers) {
      if (this.settingsBackFlag) {
         if (this.matrixStackHelper != null && this.matrixStackHelper.canGlfwListening(substringValue, modifiers)) {
            return true;
         } else if (substringValue == 256) {
            this.updateMatrixStack();
            return true;
         } else {
            return false;
         }
      } else if (this.clericBottlesItems != null && substringValue == 256) {
         this.updateBottlesCleric();
         return true;
      } else {
         return false;
      }
   }

   private ItemStack getEnchantLevel(VillagerTradeRule enchantJsonProfession) {
      String enchantLevelText = enchantJsonProfession.kind + "|" + enchantJsonProfession.enchant + "|" + enchantJsonProfession.enchantLevel + "|" + enchantJsonProfession.item;
      ItemStack enchantLevel = this.enchantLevelItemStackMap.get(enchantLevelText);
      if (enchantLevel != null) {
         return enchantLevel;
      } else {
         ItemStack botCrystalValue = switch (enchantJsonProfession.kind) {
            case ENCHANTED_BOOK -> getBookEnchanted(enchantJsonProfession.enchant, enchantJsonProfession.enchantLevel);
            case ITEM, EMERALD -> getEmerald(IngotBallUtils.getDefaultedRegistry(enchantJsonProfession.item));
         };
         this.enchantLevelItemStackMap.put(enchantLevelText, botCrystalValue);
         return botCrystalValue;
      }
   }

   @Override
   public float getSettingsCleric() {
      if (!this.settingsBackFlag && this.clericBottlesItems == null) {
         this.updateMaxAuto();
         return this.settingsClericAmount;
      } else {
         return 0.0F;
      }
   }

   private void handleTradingChangeForInt(int index) {
      if (index >= 0 && index < tradingChangeOutputsEmeraldsTypes.length) {
         if (this.villagerTradeBot.isEnabled()) {
            this.handleTraderVillager("Stop trading to change your chests", false);
         } else if (!this.villagerTradeBot.beginStoragePick(tradingChangeOutputsEmeraldsTypes[index])) {
            this.handleTraderVillager("Join a world first", false);
         } else {
            this.joinWorldFlag = true;
            this.updateRailPreview();
         }
      }
   }

   private static float getAmount(DrawContext context, float villagerTraderValue, float villagerTraderBound, float skippedLimValue, String villagerTraderText) {
      MatrixStackUtilsAmount.renderRoot(context, villagerTraderText, villagerTraderValue, skippedLimValue, villagerTraderBound);
      return skippedLimValue + 12.0F + 5.0F;
   }

   private void updateMatrixStack() {
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }

      this.settingsBackFlag = false;
      this.previewRail.updateFadeCrossfade();
   }

   private BooleanSetting getNext(String villagerTraderText) {
      for (ConfigSetting autoEspBase : this.villagerTradeBot.getSettings()) {
         if (autoEspBase instanceof BooleanSetting next && villagerTraderText.equals(autoEspBase.getSettingName())) {
            return next;
         }
      }

      return null;
   }

   private void renderCubeAmountClericBottlesItems(DrawContext context, ButtonMin buttonMin) {
      boolean guidePackageFlag = buttonMin.guidePackageFlag;
      if (this.clericBottlesItems.isEmpty()) {
         MatrixStackUtilsRelease.render(
            context,
            buttonMin.cubeKnifeAmount,
            buttonMin.cubeSchematicAmount,
            buttonMin.cubeSchematicAmount2Cube,
            buttonMin.cubeSchematicAmount3Cube,
            IcoGenUtils.getIdentifier("package"),
            "No trades here",
            "This villager has nothing to trade yet. Try another one."
         );
      } else {
         float cubeValue = Math.min(460.0F, buttonMin.cubeSchematicAmount2Cube - 28.0F);
         float emeraldAutomateValue = buttonMin.cubeKnifeAmount + (buttonMin.cubeSchematicAmount2Cube - cubeValue) / 2.0F;
         float cubeLimit = buttonMin.cubeSchematicAmount + 8.0F;
         this.emeraldAutomateAmount = buttonMin.cubeSchematicAmount3Cube - 16.0F;
         this.cubeAmount = this.clericBottlesItems.size() * 32.0F - 2.0F;
         float max = Math.max(0.0F, this.cubeAmount - this.emeraldAutomateAmount);
         if (this.matrixAmount > max) {
            this.matrixAmount = max;
         }

         float cubeTotal = cubeLimit - MatrixStackUtilsAmount.getAmount(this.smoothScrollAmount);
         MatrixStackUtilsRelease.renderScissorEnable(context, buttonMin.cubeKnifeAmount, cubeLimit, buttonMin.cubeSchematicAmount2Cube, this.emeraldAutomateAmount);

         try {
            for (int i = 0; i < this.clericBottlesItems.size(); cubeTotal += 32.0F) {
               if (!(cubeTotal + 30.0F < cubeLimit) && !(cubeTotal > cubeLimit + this.emeraldAutomateAmount)) {
                  this.renderEmeraldAutomate(context, buttonMin, guidePackageFlag, i, this.clericBottlesItems.get(i), emeraldAutomateValue, cubeTotal, cubeValue, cubeLimit);
               }

               i++;
            }
         } finally {
            context.disableScissor();
         }
      }
   }

   private VillagerTrader(Screen screen, VillagerTradeBot villagerTradeBot, List<OfferTrade> list, String villagerTraderText, int villagerTraderValue) {
      super(screen, "Villager Trader");
      this.back = this::updateMatrixStack;
      this.start = this::updateTradeBot;
      this.stop = this::updateBotVillager;
      this.add = this::updateTradeJoin;
      this.cancel = this::updateBottlesCleric;
      this.again = this::updateTradeJoin;
      this.chevronEarlierAction = () -> this.handleTradeTrading(-1);
      this.laterDownAction = () -> this.handleTradeTrading(1);
      this.confirmAction = this::updateTradeTrading;
      this.action = this::updateEnabledBot;
      this.xp = this::updateBottlesTrade;
      this.emeraldCostConsumer = this::handleEmeraldCost;
      this.targetAmountConsumer = this::handleTargetAmount;
      this.enchantLevelConsumer = this::handleEnchantLevel;
      this.intConsumer = villagerTraderValue2 -> this.handleEmeraldCost(-villagerTraderValue2);
      this.villagersSoldIntConsumer = villagerTraderValue3 -> this.handleTargetAmount(-villagerTraderValue3);
      this.villagerTraderIntConsumer = villagerTraderValue4 -> this.handleEnchantLevel(-villagerTraderValue4);
      this.actions = new Runnable[tradingChangeOutputsEmeraldsTypes.length];
      this.markTradingRunnables = new Runnable[tradingChangeOutputsEmeraldsTypes.length];
      this.bottlesClericsItems = new ArrayList<>();
      this.enchantLevelItemStackMap = new HashMap<>();
      this.itemStack = new ItemStack(Items.EXPERIENCE_BOTTLE);
      this.villagerTraderStringArrayMap = new HashMap<>();
      this.levelIntegerMap = new HashMap<>();
      this.schematicScroller = new MaxMax2();
      this.villagerTraderTexts = new String[64];
      this.formattedTextCache = new String[64];
      this.villagerTraderTable = new float[64];
      this.startAmounts = new float[pressTradeTexts.length];
      this.tradesRunningType = VillagerTrader.State.NO_TRADES;
      this.tradeClericText = "";
      this.tradingTradeValue = -1;
      this.parseInt = -1;
      this.lastThumbsValue = -1;
      this.autoAmount = -1.0F;
      this.villagerTraderAmount = 150.0F;
      this.settingsClericAmount = 176.0F;
      this.cachedLeastMax = -1.0F;
      this.earlierLaterAmount = -1.0F;
      this.outputAutoAmount = -1.0F;
      this.villagerTradeBot = villagerTradeBot;
      this.booleanBase = this.getNext("Auto Drop Output");
      this.villagerTraderBooleanBase = this.getNext("Auto Acquire Emeralds");
      this.navigationToggle = this.getNext("Navigation");
      this.nextSideLabels = this.getNext("Villager ESP");
      this.sideLabelsBooleanBase = this.getNext("Status Overlay");
      this.dropSideAction = () -> handle(this.booleanBase);
      this.sideGenAction = () -> handle(this.villagerTraderBooleanBase);
      this.sideWalkAction = () -> handle(this.navigationToggle);
      this.sideLabelsAction = () -> handle(this.nextSideLabels);
      this.overlaySideAction = () -> handle(this.sideLabelsBooleanBase);

      for (int i = 0; i < tradingChangeOutputsEmeraldsTypes.length; i++) {
         int villagerTraderValue5 = i;
         this.actions[i] = () -> this.handleTradingChangeForInt(villagerTraderValue5);
         this.markTradingRunnables[i] = () -> this.handleTradingChange(villagerTraderValue5);
      }

      if (list != null) {
         this.handleBottlesCleric(list, villagerTraderText, villagerTraderValue);
      }
   }

   private void updateMatrixStackClericBottlesItems() {
      if (this.clericBottlesItems == null) {
         if (this.matrixStackHelper == null) {
            this.matrixStackHelper = new MatrixStackHelper(this.getGeneralOverlay());
         }

         this.settingsBackFlag = true;
         this.previewRail.updateFadeCrossfade();
      }
   }

   private void handleBottlesCleric(List<OfferTrade> list2, String bottlesClericText, int tradeVillagerValue) {
      this.clericBottlesItems = new ArrayList<>(list2);
      this.tradeClericText = bottlesClericText == null ? "" : bottlesClericText;
      this.tradeVillagerValue = tradeVillagerValue;
      this.smoothScrollAmount = this.matrixAmount = 0.0F;
      this.bottlesClericsItems.clear();

      for (int i = 0; i < this.clericBottlesItems.size(); i++) {
         int bottlesClericValue = i;
         this.bottlesClericsItems.add(() -> this.handleClericBottles(bottlesClericValue));
      }
   }

   private void handleEmeraldCost(int emeraldCostValue) {
      VillagerTradeRule enchantJson = this.getEnchantJson();
      if (enchantJson != null) {
         enchantJson.maxEmeraldCost = Math.max(0, Math.min(64, enchantJson.maxEmeraldCost + emeraldCostValue));
         this.villagerTradeBot.clearPause(enchantJson);
      }
   }

   private void handleClericBottles(int index) {
      if (this.clericBottlesItems != null && index >= 0 && index < this.clericBottlesItems.size()) {
         VillagerTradeRule enchantJson = VillagerTradeBot.createTargetFromOffer(this.clericBottlesItems.get(index), this.tradeClericText);
         if (enchantJson == null) {
            this.handleTraderVillager("The bot can't do this trade", false);
         } else {
            int tradesTrade = this.villagerTradeBot.indexOfTarget(enchantJson);
            if (tradesTrade >= 0) {
               this.handleTraderVillager(enchantJson.formatTargetName() + " is already in your list", false);
            } else {
               tradesTrade = this.villagerTradeBot.addTargetIfAbsent(enchantJson);
               this.handleTraderVillager("Added " + enchantJson.formatTargetName(), true);
            }

            this.clericBottlesItems = null;
            this.bottlesClericsItems.clear();
            this.previewRail.updateFadeCrossfade();
            this.previewRail.handleCrossfadeFlag(Math.max(0, tradesTrade), false);
         }
      }
   }

   private void renderMarkTrading(DrawContext context, int index, List<RoleDimension> traderVillager, float markTradingValue, float dropTrading, float villagerTraderBound, boolean markTradingFlag) {
      boolean empty2 = !traderVillager.isEmpty();
      int storageChest = ChestStorageUtils.getStorageChest(tradingChangeOutputsEmeraldsTypes[index]);
      float configsLikeValue = MatrixStackUtilsAmount.getAmount(dropTrading + 7.5F);
      MatrixPeekUtilsRender.renderMatrixPeek(context, markTradingValue, configsLikeValue, 5.0F, 5.0F, 2.5F, empty2 ? storageChest : MatrixStackUtilsRelease.getValue(storageChest, 0.35F));
      float markTradingValue5 = markTradingValue + villagerTraderBound;
      boolean villagerTraderFlag = !markTradingFlag;
      String markTradingText = empty2 ? (tradingChangeOutputsEmeraldsTypes[index] == Role.DROP ? "Move" : "Add") : "Mark";
      float cubeSchematicValue = Math.max(34.0F, (float)Math.ceil(MatrixStackUtilsRelease.getAmount(markTradingText, 0.82F)) + 14.0F);
      float sideBuilding = MatrixStackUtilsAmount.getAmount(dropTrading + 2.0F);
      this.render(context, markTradingTexts[index], markTradingValue5 - cubeSchematicValue, sideBuilding, cubeSchematicValue, markTradingText, villagerTraderFlag, !empty2, this.actions[index]);
      float markTradingTotal = markTradingValue5 - (cubeSchematicValue + 4.0F);
      boolean sureSoldFlag = this.parseInt == index;
      if (empty2) {
         float markTradingValue8 = sureSoldFlag ? 44.0F : 16.0F;
         this.renderSureSold(context, sideClearTexts[index], markTradingTotal - markTradingValue8, sideBuilding, markTradingValue8, sureSoldFlag, villagerTraderFlag, this.markTradingRunnables[index]);
         markTradingTotal -= markTradingValue8 + 4.0F;
      }

      float matrixStackValue = markTradingValue + 5.0F + 6.0F;
      String formatTextsAmounts = this.formatTextsAmounts(sideMarkTexts[index], markTradingTotal - 4.0F - matrixStackValue, 0.82F);
      MatrixStackUtilsRelease.handle(formatTextsAmounts, matrixStackValue, dropTrading, 20.0F, 0.82F, empty2 ? -5854026 : -7630437);
      String matrixStackText;
      int markTradingValue10;
      if (!empty2) {
         matrixStackText = "Not set";
         markTradingValue10 = -11117467;
      } else {
         boolean markTradingFlag5 = false;

         for (RoleDimension roleDimension : traderVillager) {
            markTradingFlag5 |= this.villagerTradeBot.isStorageFullWhileRunning(roleDimension);
         }

         if (markTradingFlag5) {
            matrixStackText = "Full";
            markTradingValue10 = soldFullValue;
         } else if (traderVillager.size() > 1 && tradingChangeOutputsEmeraldsTypes[index] != Role.DROP) {
            matrixStackText = traderVillager.size() + " chests";
            markTradingValue10 = -7630437;
         } else {
            matrixStackText = formatFarEye(traderVillager);
            markTradingValue10 = -7630437;
         }
      }

      float markTradingValue11 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
      float markTradingValue12 = matrixStackValue + MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F) + 4.0F;
      if (markTradingTotal - 4.0F - markTradingValue11 >= markTradingValue12) {
         MatrixStackUtilsRelease.handle(matrixStackText, markTradingTotal - 4.0F - markTradingValue11, dropTrading, 20.0F, 0.82F, markTradingValue10);
      }
   }

   private void updateBottlesTrade() {
      if (this.villagerTradeBot.isEnabled()) {
         this.handleTraderVillager("Stop trading first, then add the trade", false);
      } else {
         VillagerTradeRule clericExperience = VillagerTradeRule.createBuyExperienceBottleRule();
         int tradesTrade = this.villagerTradeBot.indexOfTarget(clericExperience);
         if (tradesTrade >= 0) {
            this.handleTraderVillager("XP bottles are already in your list", false);
         } else {
            tradesTrade = this.villagerTradeBot.addTargetIfAbsent(clericExperience);
            this.handleTraderVillager("Added XP bottles  \u00b7  bought from Master Clerics", true);
         }

         this.clericBottlesItems = null;
         this.bottlesClericsItems.clear();
         this.previewRail.updateFadeCrossfade();
         this.previewRail.handleCrossfadeFlag(Math.max(0, tradesTrade), false);
      }
   }

   private static List<OfferTrade> getEmeraldBottle(int emeraldBottleLimit) {
      var arrayList = new ArrayList();
      arrayList.add(new TradeOffer(new TradedItem(Items.ROTTEN_FLESH, 32), new ItemStack(Items.EMERALD), 16, 2, 0.05F));
      arrayList.add(new TradeOffer(new TradedItem(Items.EMERALD, 1), new ItemStack(Items.REDSTONE, 2), 12, 1, 0.05F));
      if (emeraldBottleLimit >= 2) {
         arrayList.add(new TradeOffer(new TradedItem(Items.GOLD_INGOT, 3), new ItemStack(Items.EMERALD), 12, 10, 0.05F));
         arrayList.add(new TradeOffer(new TradedItem(Items.EMERALD, 1), new ItemStack(Items.LAPIS_LAZULI), 12, 5, 0.05F));
      }

      if (emeraldBottleLimit >= 3) {
         arrayList.add(new TradeOffer(new TradedItem(Items.RABBIT_FOOT, 2), new ItemStack(Items.EMERALD), 12, 20, 0.05F));
         arrayList.add(new TradeOffer(new TradedItem(Items.EMERALD, 4), new ItemStack(Items.GLOWSTONE), 12, 10, 0.05F));
      }

      if (emeraldBottleLimit >= 4) {
         arrayList.add(new TradeOffer(new TradedItem(Items.GLASS_BOTTLE, 9), new ItemStack(Items.EMERALD), 12, 30, 0.05F));
         arrayList.add(new TradeOffer(new TradedItem(Items.EMERALD, 5), new ItemStack(Items.ENDER_PEARL), 12, 15, 0.05F));
      }

      if (emeraldBottleLimit >= 5) {
         arrayList.add(new TradeOffer(new TradedItem(Items.NETHER_WART, 22), new ItemStack(Items.EMERALD), 12, 30, 0.05F));
         arrayList.add(new TradeOffer(new TradedItem(Items.EMERALD, 3), new ItemStack(Items.EXPERIENCE_BOTTLE), 12, 30, 0.05F));
      }

      var arrayList2 = new ArrayList();

      for (int i = 0; i < arrayList.size(); i++) {
         arrayList2.add(OfferTrade.getDisplayedUses((TradeOffer)arrayList.get(i), i));
      }

      return arrayList2;
   }

   private void handleEnchantLevel(int enchantLevelValue) {
      VillagerTradeRule enchantJson = this.getEnchantJson();
      if (enchantJson != null) {
         enchantJson.enchantLevel = Math.max(1, Math.min(this.getLevelMax(enchantJson.enchant), enchantJson.enchantLevel + enchantLevelValue));
         this.villagerTradeBot.clearPause(enchantJson);
      }
   }

   private void updateBotVillager() {
      if (this.villagerTradeBot.isEnabled()) {
         this.villagerTradeBot.setEnabled(false);
      }
   }

   private void updateMaxAuto() {
      boolean flag = !this.villagerTradeBot.getTargets().isEmpty();
      float autoAmount = Math.min(600.0F, Math.max(220.0F, this.width - 24.0F)) - 24.0F;
      if (autoAmount != this.autoAmount || flag != this.autoActive) {
         this.autoAmount = autoAmount;
         this.autoActive = flag;
         float autoValue = flag ? 16.0F : 8.0F;
         float min2 = Math.min(1.0F, (autoAmount - autoValue) / ((flag ? 150.0F : 0.0F) + 176.0F + 234.0F));
         float max = (float)Math.floor(Math.max(96.0F, 150.0F * min2));
         float floor2 = (float)Math.floor(Math.max(124.0F, 176.0F * min2));
         if (autoAmount - autoValue - (flag ? max : 0.0F) - floor2 >= 132.0F) {
            this.villagerTraderAmount = max;
            this.settingsClericAmount = floor2;
         } else {
            this.villagerTraderAmount = (float)Math.floor(Math.max(96.0F, (autoAmount - 8.0F) * 150.0F / 384.0F));
            this.settingsClericAmount = 0.0F;
         }
      }
   }

   private static String formatNothing(OfferTrade offerTrade) {
      StringBuilder stringBuilder = new StringBuilder();
      if (offerTrade.primaryCostItem != null) {
         stringBuilder.append(formatEmeraldEmeralds(offerTrade.primaryCostItem, offerTrade.primaryCostCount));
      }

      if (offerTrade.isFlag()) {
         stringBuilder.append(" + ").append(formatEmeraldEmeralds(offerTrade.secondaryCostItem, offerTrade.count2UnconfirmedFull));
      }

      return stringBuilder.length() == 0 ? "nothing" : stringBuilder.toString();
   }

   private boolean isBotVillager() {
      for (VillagerTradeRule enchantJsonProfession : this.villagerTradeBot.getTargets()) {
         if (enchantJsonProfession.isExperienceBottleRule()) {
            return true;
         }
      }

      return false;
   }

   private void render(DrawContext context, String villagerTraderText, String buttonText, float villagerTraderValue, float dropTrading, float cubeSchematicValue, String dropTradingText, BooleanSetting booleanBase, Runnable runnable) {
      float minButton = this.getMinButton(villagerTraderText, villagerTraderValue, dropTrading, cubeSchematicValue, 20.0F);
      float pillLimit = this.previewRail.getButtonMinButton().getAmount(buttonText, booleanBase.isEnabled() ? 1.0F : 0.0F, 16.0F);
      handle(villagerTraderValue, dropTrading, this.formatTextsAmounts(dropTradingText, cubeSchematicValue - 22.0F - 6.0F, 0.82F), minButton);
      RenderPillUtils.render(context, Math.round(villagerTraderValue + cubeSchematicValue - 22.0F), Math.round(dropTrading + 5.0F), pillLimit, minButton);
      this.handleMinButton(villagerTraderText, villagerTraderValue, dropTrading, cubeSchematicValue, 20.0F, runnable);
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double matrixStackValue) {
      if (this.settingsBackFlag) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.handleMax(matrixStackValue);
         }

         return true;
      } else if (this.clericBottlesItems != null) {
         float matrixBound = Math.max(0.0F, this.cubeAmount - this.emeraldAutomateAmount);
         this.matrixAmount = Math.max(0.0F, Math.min(matrixBound, this.matrixAmount - (float)matrixStackValue * 24.0F));
         return true;
      } else {
         return false;
      }
   }

   private static enum State {
      NO_TRADES,
      READY,
      RUNNING;
   }
}
