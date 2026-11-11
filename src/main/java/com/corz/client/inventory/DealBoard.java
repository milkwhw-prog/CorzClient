package com.corz.client.inventory;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ButtonMin;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackHelper;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.gui.PreviewText;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.utils.ComparingUtils.Rules;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import com.corz.client.categories.donutsmp.SpecialItemBuyer;
import com.corz.client.utils.BuyerAppendUtils;
import com.corz.client.utils.ComparingUtils;
import com.corz.client.utils.DaysHoursUtils;
import com.corz.client.utils.DealBoardUtils;
import com.corz.client.utils.ElseMaxData;
import com.corz.client.utils.ElseMaxUtils;
import com.corz.client.utils.FormatFakesUtils;
import com.corz.client.utils.MaxMax2;
import com.corz.client.utils.MaxDataOver;
import com.corz.client.utils.ScannedSkipped;
import com.corz.client.utils.ShardAxeType;

public final class DealBoard extends GuiClickScreen {
   private long cachedTimeKey;
   private boolean thumbsActive;
   private final Runnable more;
   private final Runnable scan;
   private static final ShardAxeType[] inspectsShardAxeTypes = ShardAxeType.values();
   private String limitMaxText;
   private static final int skippedNothingValue = CardMatrixUtils.getValue(-7630437, -8393375, 0.45);
   private DealBoard.View maxFakes;
   private boolean spendsRankedFlag;
   private float column1X;
   private boolean skippedLimActive;
   private String rankedDestructText;
   private static final String GENERAL = "General";
   private String limitsWordingText;
   private static final String DEAL_BOARD = "Deal Board";
   private float durabilityPageAmount;
   private int limitsValue;
   private final String[] dealBoardTexts;
   private ElseMaxData elseMaxData;
   private static final int HIGHLIGHT_COLOR = CardMatrixUtils.getValue(-920587, -8393375, 0.7);
   private float skippedLimAmount;
   private boolean limitsWordingFlag;
   private final SpecialItemBuyer specialItemBuyer;
   private final IntegerSetting integerHelper;
   private float dealBoardAmount;
   private int lastThumbsValue;
   private long timeMillis;
   private String workingCartText;
   private final IntegerSetting nextLimit;
   private final IntegerSetting nextInteger;
   private boolean dealBoardActive;
   private final IntConsumer intConsumer;
   private String listing;
   private float maxContentWidth;
   private static final String PRESS_CONFIRM_BUY = "Press Confirm to buy it. This spends your coins.";
   private static final int fakesReadValue = CardMatrixUtils.getValue(-7630437, -1750963, 0.65);
   private static final String SCAN = "Scan again first";
   private final ItemStack[] dealBoardItemStacks = new ItemStack[inspectsShardAxeTypes.length];
   private float column2X;
   private final IntConsumer skippedLimIntConsumer;
   private int limitValue;
   private float column3X;
   private int dealBoardValue;
   private boolean matrixStackActive;
   private long limitsWordingTime;
   private final Runnable action;
   private float column4X;
   private final IntConsumer dealBoardIntConsumer;
   private String limitText;
   private float cubeAmount;
   private static Screen currentScreenMatrixStack;
   private final float[] spendAmounts;
   private MatrixStackHelper matrixStackHelper;
   private float detailPanelWidth;
   private int limitMaxValue;
   private float elseAmount;
   private final List<String> dealBoardStringList;
   private final MaxMax2 limitsPanelAnim;
   private String elseText;
   private ElseMaxData elseMaxDataElse;
   private boolean durabilityPageActive;
   private float maxColumnWidth;
   private MaxDataOver maxData;
   private static final String SPENDS_COINS_TEXT = "This scan is over " + DaysHoursUtils.formatText(600L) + " old. Press Scan again to buy.";
   private final IntConsumer maxPriceStepConsumer;
   private long currentMillis;
   private static final String RANKED_PRICE_HOUR = "Ranked by price per hour of self-destruct time left.";
   private float lastAvailableWidth;
   private int noneLittleValue;
   private String cubeText;
   private String joinFakeFakes;
   private String workingScannedText;
   private Rules maxDataThumbs;
   private float column5X;
   private static final String SHIFT_CLICK_SMALL = "Shift-click for small steps.";
   private int fakeValue;
   private final Runnable back;
   private final MaxMax2 pagePanelAnim;
   private final DealBoard.View[] dealBoardMaxFakess = new DealBoard.View[inspectsShardAxeTypes.length];
   private final IntConsumer minTimeStepConsumer;
   private String littleTimeText;
   private final float[] dealBoardTable;
   private final IntConsumer maxSpendStepConsumer;
   private boolean scanCartFlag;
   private boolean extraColumnVisible;
   private boolean scanSettingsFlag;
   private float pageWidth;
   private String littleText;
   private boolean schematicsActive;
   private static final String SETTINGS_LIMITS_SPEED = "More settings  \u00b7  limits, speed and server wording";
   private static final String[] pageDurabilityTexts = new String[]{"#", "Price", "Time left", "Per hour", "Durability", "Page"};
   private final String[] formattedTextCache;
   private static int floorMod;

   private IntegerSetting getNext(String dealBoardText) {
      for (ConfigSetting autoEspBase : this.specialItemBuyer.getSettings()) {
         if (autoEspBase instanceof IntegerSetting next && dealBoardText.equals(autoEspBase.getSettingName())) {
            return next;
         }
      }

      return null;
   }

   private void handle(float dealBoardValue, float matrixStackValue, float dealBoardValue3, String dealBoardText, String matrixStackText, int dealBoardValue4) {
      float dealBoardValue5 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
      MatrixStackUtilsRelease.handle(this.formatTextsAmounts(dealBoardText, Math.max(10.0F, dealBoardValue3 - dealBoardValue5 - 8.0F), 0.82F), dealBoardValue, matrixStackValue, 13.0F, 0.82F, -7630437);
      MatrixStackUtilsRelease.handle(matrixStackText, MatrixStackUtilsAmount.getAmount(dealBoardValue + dealBoardValue3 - dealBoardValue5), matrixStackValue, 13.0F, 0.82F, dealBoardValue4);
   }

   private static boolean isPressedWindow() {
      Window window = MinecraftClient.getInstance().getWindow();
      return window != null && (InputUtil.isKeyPressed(window, 340) || InputUtil.isKeyPressed(window, 344));
   }

   @Override
   public void handleInspectsSchematics(int floorMod2) {
      if (floorMod2 >= 0 && floorMod2 < inspectsShardAxeTypes.length) {
         floorMod = floorMod2;
         this.updateElseMax();
      }
   }

   private void handle(IntegerSetting integerHelper, int dealBoardValue) {
      if (integerHelper != null) {
         integerHelper.handleMax(DealBoardUtils.getValue(integerHelper.getValue(), dealBoardValue, isPressedWindow(), integerHelper.getMaxValue()));
         this.updateElseMax();
      }
   }

   private float getAmountCube(DrawContext context, ButtonMin buttonMin, DealBoard.View view, ElseMaxData elseMaxData, float cubeValue, boolean cubeFlag) {
      boolean guidePackageFlag = buttonMin.guidePackageFlag;
      float matrixPeekValue = buttonMin.cubeKnifeAmount + cubeValue;
      float cubeValue3 = buttonMin.cubeSchematicAmount2Cube - cubeValue * 2.0F;
      float matrixStackValue = buttonMin.cubeSchematicAmount + cubeValue;
      float cubeValue5 = matrixPeekValue + 9.0F;
      float didnScanValue = Math.max(0.0F, cubeValue3 - 18.0F);
      String cubeText = view.didnOpenTexts[0];
      float didnScan = getDidnScan(view, didnScanValue, cubeFlag);
      float materialFactor = cubeFlag ? 16.0F : 22.0F;
      boolean canDidnScan2 = canDidnScan(view, didnScanValue, didnScan);
      float cubeValue8 = this.getAmount(view, didnScanValue, cubeFlag);
      MatrixPeekUtilsRender.renderForDrawContextFloat(
         context, matrixPeekValue, matrixStackValue, cubeValue3, cubeValue8, 8.0F, MatrixStackUtilsRelease.getValue(-8393375, 0.075F), MatrixStackUtilsRelease.getValue(-8393375, 0.32F)
      );
      float cubeValue9 = matrixStackValue + 9.0F - 1.0F;
      MatrixStackUtilsRelease.handle(
         this.formatTextsAmounts(view.fitsNothingText, didnScanValue, 0.78F), cubeValue5, cubeValue9, 9.0F, 0.78F, MatrixStackUtilsRelease.getValue(-8393375, 0.85F)
      );
      float cubeValue10 = cubeValue9 + 9.0F + 2.0F;
      String formatTextsAmounts = this.formatTextsAmounts(cubeText, canDidnScan2 ? didnScanValue : didnScanValue - view.scanAmount - 12.0F, didnScan);
      MatrixStackUtilsRelease.handle(formatTextsAmounts, cubeValue5, cubeValue10, materialFactor, didnScan, -8393375);
      MatrixStackUtilsRelease.handle(formatTextsAmounts, cubeValue5 + MatrixPeekUtilsRender.getAmount(), cubeValue10, materialFactor, didnScan, MatrixStackUtilsRelease.getValue(-8393375, 0.55F));
      float cubeValue11 = cubeValue10 + materialFactor;
      long secondsLeft = FormatFakesUtils.getSecondsLeft(elseMaxData, view.scanData.scannedAtMs(), this.currentMillis);
      if (!cubeFlag) {
         float cubeValue12 = cubeValue11 + 2.0F;
         String matrixStackText = this.formatTextsAmounts(this.formatTextElse(view, elseMaxData, secondsLeft), didnScanValue, 0.82F);
         MatrixStackUtilsRelease.handle(matrixStackText, cubeValue5, cubeValue12, 11.0F, 0.82F, -5854026);
         if (secondsLeft < 10800L && matrixStackText.equals(this.cubeText)) {
            MatrixStackUtilsRelease.handle(this.elseText, cubeValue5 + this.elseAmount, cubeValue12, 11.0F, 0.82F, -1535700);
         }

         cubeValue11 = cubeValue12 + 11.0F;
      }

      if (canDidnScan2) {
         this.renderBuyScan(context, buttonMin, guidePackageFlag, view, elseMaxData, secondsLeft, cubeValue5, cubeValue11 + 6.0F, didnScanValue);
      } else {
         this.renderBuyScan(
            context, buttonMin, guidePackageFlag, view, elseMaxData, secondsLeft, cubeValue5 + didnScanValue - view.scanAmount, MatrixStackUtilsAmount.getAmount(cubeValue10 + (materialFactor - 16.0F) / 2.0F), view.scanAmount
         );
      }

      return matrixStackValue + cubeValue8;
   }

   public static void handleInstance(SpecialItemBuyer specialItemBuyer, int dealBoardValue) {
      if (specialItemBuyer != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         handleExecuteThread(
            instance,
            () -> {
               floorMod = Math.floorMod(dealBoardValue, inspectsShardAxeTypes.length);
               Screen currentScreen = instance.currentScreen;
               if (currentScreen instanceof DealBoard dealBoard) {
                  dealBoard.handleLimitsWording(floorMod);
               } else {
                  if (currentScreen instanceof MatrixMatricesScreen) {
                     currentScreenMatrixStack = currentScreen;
                  } else if (currentScreen instanceof HandledScreen
                     && !(currentScreen instanceof InventoryScreen)
                     && !(currentScreen instanceof CreativeInventoryScreen)
                     && instance.player != null) {
                     instance.player.closeHandledScreen();
                  } else if (currentScreen != null) {
                     return;
                  }

                  instance.setScreen(new DealBoard(currentScreenMatrixStack, specialItemBuyer));
               }
            }
         );
      }
   }

   private float getMinusPlus(
      DrawContext context,
      ScannedSkipped scannedSkipped,
      String minusPlusText,
      String scannedSkippedText,
      float minusPlusValue,
      float matrixStackValue,
      float dealBoardBound,
      String matrixStackText,
      String dealBoardText,
      IntConsumer intConsumer,
      IntConsumer intConsumer2
   ) {
      float minusPlusValue4 = 16.0F;
      float sideWalkAmount = Math.min(96.0F, dealBoardBound - MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F) - 8.0F);
      float scannedSkippedValue;
      float sideBuilding;
      float minusPlus;
      if (sideWalkAmount < 70.0F) {
         sideWalkAmount = dealBoardBound;
         MatrixStackUtilsRelease.handle(this.formatTextsAmounts(matrixStackText, dealBoardBound, 0.82F), minusPlusValue, matrixStackValue, 13.0F, 0.82F, -7630437);
         scannedSkippedValue = minusPlusValue;
         sideBuilding = MatrixStackUtilsAmount.getAmount(matrixStackValue + 13.0F);
         minusPlus = sideBuilding + minusPlusValue4 + 4.0F;
      } else {
         MatrixStackUtilsRelease.handle(matrixStackText, minusPlusValue, matrixStackValue, 20.0F, 0.82F, -7630437);
         scannedSkippedValue = minusPlusValue + dealBoardBound - sideWalkAmount;
         sideBuilding = MatrixStackUtilsAmount.getAmount(matrixStackValue + (20.0F - minusPlusValue4) / 2.0F);
         minusPlus = matrixStackValue + 20.0F;
      }

      float cardMatrixFactor = scannedSkipped.getMin(minusPlusText, scannedSkippedValue, sideBuilding, sideWalkAmount, minusPlusValue4);
      MatrixStackUtilsRelease.render(context, scannedSkippedValue, sideBuilding, sideWalkAmount, minusPlusValue4, cardMatrixFactor, 0.0F);
      int minusPlusValue9 = CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor);
      float roundOffset = MatrixStackUtilsAmount.getAmount(sideBuilding + (minusPlusValue4 - 7.0F) / 2.0F);
      MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier("minus"), MatrixStackUtilsAmount.getAmount(scannedSkippedValue + 6.0F), roundOffset, 7.0F, minusPlusValue9);
      MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier("plus"), MatrixStackUtilsAmount.getAmount(scannedSkippedValue + sideWalkAmount - 6.0F - 7.0F), roundOffset, 7.0F, minusPlusValue9);
      String formatTextsAmounts = this.formatTextsAmounts(dealBoardText, sideWalkAmount - 32.0F, 0.82F);
      float minusPlusValue11 = MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F);
      MatrixStackUtilsRelease.handle(
         formatTextsAmounts, MatrixStackUtilsAmount.getAmount(scannedSkippedValue + (sideWalkAmount - minusPlusValue11) / 2.0F), sideBuilding, minusPlusValue4, 0.82F, CardMatrixUtils.getValue(-920587, -1, 0.2 * cardMatrixFactor)
      );
      float materialFactor = sideWalkAmount / 2.0F;
      scannedSkipped.handle(minusPlusText, scannedSkippedValue + materialFactor, sideBuilding, materialFactor, minusPlusValue4, intConsumer);
      scannedSkipped.handle(scannedSkippedText, scannedSkippedValue, sideBuilding, materialFactor, minusPlusValue4, intConsumer2);
      return minusPlus;
   }

   private float getFlag(float dealBoardValue, float dealBoardValue2, float dealBoardValue3, float dealBoardValue4, float dealBoardValue5) {
      return dealBoardValue - dealBoardValue2 - 8.0F - dealBoardValue3 - (this.durabilityPageActive ? 8.0F + dealBoardValue4 : 0.0F) - (this.extraColumnVisible ? 8.0F + dealBoardValue5 : 0.0F);
   }

   @Override
   public String formatTextsInspects(int index2) {
      return index2 >= 0 && index2 < inspectsShardAxeTypes.length ? inspectsShardAxeTypes[index2].getCommandName() : "";
   }

   private void renderBuyScan(DrawContext context, ButtonMin buttonMin, boolean buyScanFlag, DealBoard.View view, ElseMaxData elseMaxData, long buyScanLimit, float buyScanValue, float buyScanValue2, float cubeSchematicValue) {
      float materialFactor = 16.0F;
      boolean scanned = ElseMaxUtils.canScanned(view.scanData, this.currentMillis);
      boolean buyScanFlag2 = !this.scanCartFlag && buyScanLimit > 0L && !scanned;
      boolean buyScanFlag3 = buyScanFlag2 && elseMaxData.equals(this.elseMaxData);
      float releaseRightLimit = buyScanFlag && buyScanFlag2 ? buttonMin.getMin("buy", buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, buyScanValue + cubeSchematicValue / 2.0F, buyScanValue2 + materialFactor / 2.0F, buttonMin.getMin("buy", 0.05F), 0.0F);

      try {
         int matrixStackValue;
         if (!buyScanFlag2) {
            MatrixStackUtilsRelease.render(context, buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor, 0.0F, 0.0F);
            matrixStackValue = -11117467;
         } else if (buyScanFlag3) {
            if (releaseRightLimit > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor, 5.0F, MatrixStackUtilsRelease.getValue(-1535700, 0.22F * releaseRightLimit), 4.0F, 0.5F);
            }

            MatrixPeekUtilsRender.renderMatrixPeek(context, buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor, 5.0F, CardMatrixUtils.getValue(-1535700, -1, 0.08 * releaseRightLimit));
            matrixStackValue = -15002616;
         } else {
            MatrixStackUtilsRelease.renderReleaseRight(context, buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor, releaseRightLimit);
            matrixStackValue = MatrixStackUtilsAmount.getReleaseRight();
         }

         String formatTextsAmounts = this.formatTextsAmounts(scanned ? "Scan again first" : (buyScanFlag3 ? view.scanText : view.scanFitsText), cubeSchematicValue - 8.0F, 0.82F);
         float buyScanValue6 = MatrixStackUtilsRelease.getAmount(formatTextsAmounts, 0.82F);
         MatrixStackUtilsRelease.handle(formatTextsAmounts, MatrixStackUtilsAmount.getAmount(buyScanValue + (cubeSchematicValue - buyScanValue6) / 2.0F), buyScanValue2, materialFactor, 0.82F, matrixStackValue);
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (buyScanFlag && buyScanFlag2) {
         buttonMin.handle("buy", buyScanValue, buyScanValue2, cubeSchematicValue, materialFactor, this.action);
      }
   }

   private String formatLittleText() {
      this.formatLittleTime();
      return this.littleTimeText;
   }

   private void renderAmountTexts(
      DrawContext context, ButtonMin buttonMin, boolean dealBoardFlag, DealBoard.View view, int index, float dealBoardBound, float dealBoardValue, float dealBoardValue2, float rocketsElytraBound, float hashHuntBound
   ) {
      ElseMaxData ranked = view.scanData.ranked().get(index);
      float buttonValue = dealBoardValue - 4.0F;
      float cubeSchematicValue = dealBoardValue2 + 8.0F;
      float matrixStackValue = Math.max(dealBoardBound, rocketsElytraBound);
      float dealBoardLimit = Math.min(dealBoardBound + 16.0F, hashHuntBound) - matrixStackValue;
      float matrixStackLimit = dealBoardFlag && dealBoardLimit > 1.0F ? buttonMin.getMin(this.formatText(index), buttonValue, matrixStackValue, cubeSchematicValue, dealBoardLimit) : 0.0F;
      MatrixStackUtilsRelease.render(context, buttonValue, dealBoardBound, cubeSchematicValue, 16.0F, index, matrixStackLimit);
      boolean dealBoardFlag2 = index == 0;
      if (dealBoardFlag2) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, buttonValue, dealBoardBound, cubeSchematicValue, 16.0F, 5.0F, MatrixStackUtilsRelease.getValue(-8393375, 0.07F));
      }

      long secondsLeft = FormatFakesUtils.getSecondsLeft(ranked, view.scanData.scannedAtMs(), this.currentMillis);
      boolean dealBoardFlag3 = secondsLeft < 10800L;
      int dealBoardValue7 = dealBoardFlag2 ? HIGHLIGHT_COLOR : CardMatrixUtils.getValue(-920587, -1, 0.15 * matrixStackLimit);
      int dealBoardValue8 = dealBoardFlag2 ? HIGHLIGHT_COLOR : CardMatrixUtils.getValue(-5854026, -920587, matrixStackLimit);
      float matrixStackFactor = 0.82F;
      MatrixStackUtilsRelease.handle(view.viewTexts[index], dealBoardValue, dealBoardBound, 16.0F, matrixStackFactor, dealBoardFlag2 ? skippedNothingValue : -7630437);
      handle(view.fitsNothingTexts[index], this.column1X, dealBoardBound, 16.0F, matrixStackFactor, dealBoardValue7);
      MatrixStackUtilsRelease.handle(view.formatTextsTimes(index, secondsLeft), this.column2X, dealBoardBound, 16.0F, matrixStackFactor, dealBoardFlag3 ? -1535700 : dealBoardValue8);
      if (this.dealBoardActive && this.detailPanelWidth > 1.0F) {
         float configsLikeValue = MatrixStackUtilsAmount.getAmount(dealBoardBound + 6.5F);
         MatrixPeekUtilsRender.renderMatrixPeek(context, this.dealBoardAmount, configsLikeValue, this.detailPanelWidth, 3.0F, 1.5F, MatrixStackUtilsRelease.getValue(-1, 0.08F));
         float materialFactor = Math.max(3.0F, this.detailPanelWidth * FormatFakesUtils.getMax(secondsLeft));
         int dealBoardValue12 = dealBoardFlag3
            ? -1535700
            : (dealBoardFlag2 ? MatrixStackUtilsRelease.getValue(-8393375, 0.85F) : MatrixStackUtilsRelease.getValue(CardMatrixUtils.getReleaseRight(), 0.85F));
         if (secondsLeft > 0L) {
            MatrixPeekUtilsRender.renderMatrixPeek(context, this.dealBoardAmount, configsLikeValue, materialFactor, 3.0F, 1.5F, dealBoardValue12);
         }
      }

      handle(view.didnOpenTexts[index], this.column3X, dealBoardBound, 16.0F, matrixStackFactor, dealBoardValue7);
      if (this.durabilityPageActive) {
         handle(view.elseTexts[index], this.column4X, dealBoardBound, 16.0F, matrixStackFactor, dealBoardValue8);
      }

      if (this.extraColumnVisible) {
         handle(view.pageNumberTexts[index], this.column5X, dealBoardBound, 16.0F, matrixStackFactor, -7630437);
      }
   }

   private DealBoard(Screen screen, SpecialItemBuyer specialItemBuyer) {
      super(screen, "Deal Board");
      this.pagePanelAnim = new MaxMax2();
      this.limitsPanelAnim = new MaxMax2();
      this.scan = this::updateSpecialBuyer;
      this.action = this::updateDestructedDeal;
      this.more = this::updateMatrixStack;
      this.back = this::updateMatrixStackMatrixStackHelper;
      this.intConsumer = this::handleIntegerNextLimit;
      this.maxPriceStepConsumer = dealBoardValue -> this.handleIntegerNextLimit(-dealBoardValue);
      this.minTimeStepConsumer = this::handleIntegerForInt;
      this.dealBoardIntConsumer = dealBoardValue2 -> this.handleIntegerForInt(-dealBoardValue2);
      this.skippedLimIntConsumer = this::handleInteger;
      this.maxSpendStepConsumer = dealBoardValue3 -> this.handleInteger(-dealBoardValue3);
      this.dealBoardStringList = new ArrayList<>();
      this.spendAmounts = new float[pageDurabilityTexts.length];
      this.dealBoardTexts = new String[48];
      this.formattedTextCache = new String[48];
      this.dealBoardTable = new float[48];
      this.lastThumbsValue = -1;
      this.lastAvailableWidth = -1.0F;
      this.maxColumnWidth = 128.0F;
      this.maxContentWidth = 140.0F;
      this.workingCartText = "";
      this.workingScannedText = "";
      this.limitsWordingText = "";
      this.limitsValue = -2;
      this.cachedTimeKey = -1L;
      this.elseText = "";
      this.fakeValue = -1;
      this.joinFakeFakes = "";
      this.rankedDestructText = "";
      this.noneLittleValue = -1;
      this.limitValue = -1;
      this.limitMaxValue = -1;
      this.littleText = "";
      this.littleTimeText = "";
      this.limitMaxText = "";
      this.limitText = "";
      this.specialItemBuyer = specialItemBuyer;
      this.nextLimit = this.getNext("Max Price");
      this.nextInteger = this.getNext("Min Time Left");
      this.integerHelper = this.getNext("Max Total Spend");

      for (int i = 0; i < inspectsShardAxeTypes.length; i++) {
         this.dealBoardItemStacks[i] = getNetheriteIngot(inspectsShardAxeTypes[i]);
      }

      Arrays.fill(this.spendAmounts, -1.0F);
      floorMod = Math.floorMod(floorMod, inspectsShardAxeTypes.length);
      this.previewRail.handleCrossfadeFlag(floorMod, false);
   }

   @Override
   public void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double minmaxValue, double matrixParseValue3, double matrixParseValue4, int button) {
      if (this.matrixStackActive && this.matrixStackHelper != null) {
         this.matrixStackHelper.handleForDoubleInt(matrixParseValue, minmaxValue, button);
      }
   }

   private Map<String, List<ConfigSetting>> getGeneralSpecial() {
      var linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("General", new ArrayList());

      for (ConfigSetting autoEspBase : this.specialItemBuyer.getSettings()) {
         if (!autoEspBase.isHiddenFromList()) {
            String generalSpecialText = autoEspBase.getGroupName();
            ((java.util.List) linkedHashMap.computeIfAbsent(generalSpecialText == null ? "General" : generalSpecialText, obj -> new ArrayList<>())).add(autoEspBase);
         }
      }

      linkedHashMap.values().removeIf(obj2 -> ((List) obj2).isEmpty());
      return linkedHashMap;
   }

   private void handleLimitsWording(int limitsWordingValue) {
      this.updateElseMax();
      if (!this.matrixStackActive) {
         this.previewRail.handleCrossfadeFlag(Math.floorMod(limitsWordingValue, inspectsShardAxeTypes.length), true);
      }

      this.previewRail.updateFadeCrossfade();
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int modifiers) {
      if (!this.matrixStackActive) {
         return false;
      } else if (this.matrixStackHelper != null && this.matrixStackHelper.canGlfwListening(substringValue, modifiers)) {
         return true;
      } else if (substringValue == 256) {
         this.updateMatrixStackMatrixStackHelper();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
      if (!this.matrixStackActive && this.thumbsActive) {
         MatrixStackUtilsRelease.render(context, this.cubeAmount, this.durabilityPageAmount + 2.0F, Math.max(0.0F, this.pageWidth - 4.0F), this.pagePanelAnim);
      }
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.matrixStackActive) {
         previewText.getClassPreviewText("back", "Back", this.back).getFlag().getClass073("done", "Done", this.back);
      } else {
         boolean folderDoneFlag = !this.scanCartFlag && this.isModMax();
         String folderDoneText;
         if (this.scanCartFlag) {
            folderDoneText = this.workingCartText.isEmpty() ? "Working..." : this.workingCartText;
         } else if (this.elseMaxData != null) {
            folderDoneText = "Press Confirm to buy it. This spends your coins.";
         } else if (folderDoneFlag) {
            folderDoneText = SPENDS_COINS_TEXT;
         } else if (this.spendsRankedFlag && !this.rankedDestructText.isEmpty()) {
            folderDoneText = this.rankedDestructText;
         } else {
            folderDoneText = "Ranked by price per hour of self-destruct time left.";
         }

         previewText.getText(folderDoneText);
         previewText.getFlag().getClassPreviewText("more", "More settings", this.more);
         String folderDoneText2 = this.scanSettingsFlag ? "Scan again" : "Scan";
         if (this.scanCartFlag) {
            previewText.getClassForString("scan", folderDoneText2);
         } else if (this.scanSettingsFlag && !folderDoneFlag) {
            previewText.getClassPreviewText("scan", folderDoneText2, this.scan);
         } else {
            previewText.getClass073("scan", folderDoneText2, this.scan);
         }
      }
   }

   private int getNextInteger() {
      return this.nextInteger == null ? 0 : Math.max(0, this.nextInteger.getValue());
   }

   private String formatLimitMax() {
      int limitMaxValue = this.integerHelper.getValue();
      if (limitMaxValue != this.limitMaxValue) {
         this.limitMaxValue = limitMaxValue;
         this.limitText = limitMaxValue <= 0 ? "No limit" : formatText((long)limitMaxValue);
      }

      return this.limitText;
   }

   @Override
   public boolean canMatrixStack(int matrixStackValue, int rendererValue) {
      return this.matrixStackActive && this.matrixStackHelper != null && this.matrixStackHelper.canFlag(matrixStackValue, rendererValue);
   }

   private static String formatText(long dealBoardValue) {
      return BuyerAppendUtils.formatText(dealBoardValue);
   }

   private String formatTextElse(DealBoard.View view, ElseMaxData elseMaxData, long time) {
      if (elseMaxData == this.elseMaxDataElse && time == this.cachedTimeKey && this.cubeText != null) {
         return this.cubeText;
      } else {
         this.elseMaxDataElse = elseMaxData;
         this.cachedTimeKey = time;
         String elseText = view.fitsNothingTexts[0] + "  \u00b7  ";
         this.elseText = DaysHoursUtils.formatText(time) + " left";
         this.elseAmount = MatrixStackUtilsRelease.getAmount(elseText, 0.82F);
         this.cubeText = elseText + this.elseText + "  \u00b7  " + view.elseTexts[0] + " durability  \u00b7  page " + elseMaxData.page();
         return this.cubeText;
      }
   }

   private String formatTextsAmounts(String dealBoardText, float dealBoardBound, float dealBoardDivisor) {
      if (dealBoardText != null && !dealBoardText.isEmpty()) {
         float dealBoardValue = Math.max(0.0F, dealBoardBound) / dealBoardDivisor;

         for (int i = 0; i < 48; i++) {
            if (this.dealBoardTable[i] == dealBoardValue && dealBoardText.equals(this.dealBoardTexts[i])) {
               return this.formattedTextCache[i];
            }
         }

         int dealBoardValue2 = this.dealBoardValue;
         this.dealBoardValue = (this.dealBoardValue + 1) % 48;
         this.dealBoardTexts[dealBoardValue2] = dealBoardText;
         this.dealBoardTable[dealBoardValue2] = dealBoardValue;
         this.formattedTextCache[dealBoardValue2] = MatrixStackUtilsAmount.getFontRenderer().formatText(dealBoardText, dealBoardValue);
         return this.formattedTextCache[dealBoardValue2];
      } else {
         return "";
      }
   }

   private void handleInteger(int integerValue) {
      this.handle(this.integerHelper, integerValue);
   }

   public static void handleInstance(SpecialItemBuyer specialItemBuyer) {
      if (specialItemBuyer != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         handleExecuteThread(instance, () -> {
            Screen currentScreen = instance.currentScreen;
            if (currentScreen instanceof DealBoard dealBoard) {
               dealBoard.handleLimitsWording(floorMod);
            } else {
               currentScreenMatrixStack = currentScreen instanceof MatrixMatricesScreen ? currentScreen : null;
               instance.setScreen(new DealBoard(currentScreenMatrixStack, specialItemBuyer));
            }
         });
      }
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      if (this.matrixStackActive) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.renderAmountCube(context, buttonMin);
         }
      } else {
         int cubeSchematicLimit = buttonMin.selectedIndex;
         if (cubeSchematicLimit >= 0 && cubeSchematicLimit < inspectsShardAxeTypes.length) {
            DealBoard.View fakessMax = this.getFakessMax(cubeSchematicLimit);
            float cubeSchematicValue = buttonMin.cubeSchematicAmount2Cube < 200.0F ? 7.0F : 10.0F;
            float cubeSchematicValue2 = buttonMin.cubeSchematicAmount + buttonMin.cubeSchematicAmount3Cube - cubeSchematicValue;
            ElseMaxData elseMaxData = fakessMax == null ? null : fakessMax.scanData.getRanked();
            if (elseMaxData == null) {
               if (buttonMin.guidePackageFlag) {
                  this.spendsRankedFlag = false;
               }

               this.renderScanPress(context, buttonMin, cubeSchematicLimit, fakessMax);
            } else {
               boolean cubeSchematicFlag = !this.previewRail.getNonMaxFlag().isAmount() && !this.formatFakeFakes(fakessMax).isEmpty();
               float max3 = Math.max(0.0F, buttonMin.cubeSchematicAmount2Cube - cubeSchematicValue * 2.0F - 18.0F);
               float cubeSchematicValue4 = cubeSchematicValue2 - (buttonMin.cubeSchematicAmount + cubeSchematicValue) - (cubeSchematicFlag ? 15.0F : 0.0F) - this.getAmount(fakessMax, max3, false) - 8.0F - 13.0F - 3.0F;
               boolean cubeFlag = cubeSchematicValue4 < 32.0F;
               if (buttonMin.guidePackageFlag) {
                  this.spendsRankedFlag = cubeFlag && cubeSchematicFlag;
               }

               if (cubeSchematicFlag && !cubeFlag) {
                  float matrixStackValue = cubeSchematicValue2 - 11.0F;
                  String formatTextsAmounts = this.formatTextsAmounts(this.rankedDestructText, buttonMin.cubeSchematicAmount2Cube - cubeSchematicValue * 2.0F, 0.82F);
                  MatrixStackUtilsRelease.handle(formatTextsAmounts, buttonMin.cubeKnifeAmount + cubeSchematicValue, matrixStackValue, 11.0F, 0.82F, -7630437);
                  cubeSchematicValue2 = matrixStackValue - 4.0F;
               }

               float amountCube = this.getAmountCube(context, buttonMin, fakessMax, elseMaxData, cubeSchematicValue, cubeFlag);
               this.renderDurabilityPage(context, buttonMin, fakessMax, cubeSchematicValue, amountCube + 8.0F, cubeSchematicValue2);
            }
         }
      }
   }

   private DealBoard.View getFakessMax(int index) {
      if (index >= 0 && index < inspectsShardAxeTypes.length) {
         MaxDataOver dealScan = this.specialItemBuyer.getDealScan(inspectsShardAxeTypes[index]);
         if (dealScan == null) {
            this.dealBoardMaxFakess[index] = null;
            return null;
         } else {
            Rules rules = this.maxDataThumbs != null ? this.maxDataThumbs : this.specialItemBuyer.getCurrentRules();
            long fakessValue = Math.max(0L, this.currentMillis - dealScan.scannedAtMs()) / 1000L;
            DealBoard.View fakessMax = this.dealBoardMaxFakess[index];
            if (fakessMax == null || fakessMax.maxData != dealScan || fakessMax.snapshotTime != fakessValue || !fakessMax.rules.equals(rules)) {
               fakessMax = new DealBoard.View(dealScan, ElseMaxUtils.getLeftSeconds(dealScan, rules, this.currentMillis), rules, fakessValue, this.currentMillis);
               this.dealBoardMaxFakess[index] = fakessMax;
            }

            return fakessMax;
         }
      } else {
         return null;
      }
   }

   @Override
   public void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseReleasedValue, int button) {
      if (this.matrixStackActive && this.matrixStackHelper != null) {
         this.matrixStackHelper.handle(matrixStackValue, mouseReleasedValue, button);
      }
   }

   private static void handleExecuteThread(MinecraftClient client, Runnable runnable) {
      if (client.isOnThread()) {
         runnable.run();
      } else {
         client.execute(runnable);
      }
   }

   private static void handle(String dealBoardText, float dealBoardValue, float dealBoardBound, float materialFactor, float matrixStackValue, int dealBoardValue5) {
      MatrixStackUtilsRelease.handle(dealBoardText, MatrixStackUtilsAmount.getAmount(dealBoardValue - MatrixStackUtilsRelease.getAmount(dealBoardText, matrixStackValue)), dealBoardBound, materialFactor, matrixStackValue, dealBoardValue5);
   }

   private void render(DrawContext context, float dealBoardValue, float dealBoardValue2, float sideWalkAmount, int matrixStackValue, String dealBoardText, String matrixStackText, int dealBoardValue5) {
      float configsLikeValue = MatrixStackUtilsAmount.getAmount(dealBoardValue2 + 4.0F);
      MatrixPeekUtilsRender.renderMatrixPeek(context, dealBoardValue, configsLikeValue, 5.0F, 5.0F, 2.5F, matrixStackValue);
      this.handle(dealBoardValue + 5.0F + 6.0F, dealBoardValue2, sideWalkAmount - 5.0F - 6.0F, dealBoardText, matrixStackText, dealBoardValue5);
   }

   private void updateElseMax() {
      this.elseMaxData = null;
   }

   private void updateMatrixStack() {
      if (this.matrixStackHelper == null) {
         this.matrixStackHelper = new MatrixStackHelper(this.getGeneralSpecial());
      }

      this.updateElseMax();
      this.matrixStackActive = true;
      this.previewRail.updateFadeCrossfade();
   }

   @Override
   public float getAmount() {
      return 340.0F;
   }

   @Override
   protected void updateMatrixStack3() {
      super.updateMatrixStack3();
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }

      currentScreenMatrixStack = null;
   }

   private static void renderMatrixPop(DrawContext context, ItemStack itemStack, float matrixPopValue, float matrixPopValue2) {
      if (itemStack != null && !itemStack.isEmpty()) {
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();

         try {
            matrices.translate(matrixPopValue, matrixPopValue2);
            DrawMatricesUtils.renderMatricesMatrix(context, itemStack, 0, 0, 16);
         } finally {
            matrices.popMatrix();
         }
      }
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double matrixStackValue) {
      if (this.matrixStackActive) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.handleMax(matrixStackValue);
         }

         return true;
      } else {
         this.pagePanelAnim.handleAmount(matrixStackValue, 24.0F);
         return true;
      }
   }

   private float getAmount(DealBoard.View view, float dealBoardValue, boolean dealBoardFlag) {
      float dealBoardTotal = 19.0F + (dealBoardFlag ? 16.0F : 22.0F) + 9.0F - 2.0F;
      if (!dealBoardFlag) {
         dealBoardTotal += 13.0F;
      }

      if (canDidnScan(view, dealBoardValue, getDidnScan(view, dealBoardValue, dealBoardFlag))) {
         dealBoardTotal += 22.0F;
      }

      return dealBoardTotal;
   }

   private String formatText(int index) {
      while (this.dealBoardStringList.size() <= index) {
         this.dealBoardStringList.add("row:" + this.dealBoardStringList.size());
      }

      return this.dealBoardStringList.get(index);
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 560.0F;
   }

   @Override
   public boolean isClericLimits() {
      return this.matrixStackActive;
   }

   private void updateDestructedDeal() {
      if (!this.matrixStackActive) {
         DealBoard.View fakessMax = this.getFakessMax(floorMod);
         ElseMaxData elseMaxData = fakessMax == null ? null : fakessMax.scanData.getRanked();
         if (elseMaxData != null && !this.specialItemBuyer.isRunning()) {
            long timeMillis = System.currentTimeMillis();
            if (ElseMaxUtils.canScanned(fakessMax.scanData, timeMillis)) {
               this.updateElseMax();
            } else if (FormatFakesUtils.getSecondsLeft(elseMaxData, fakessMax.scanData.scannedAtMs(), timeMillis) <= 0L) {
               this.updateElseMax();
               DisabledEnabled.getInstance().handleDisabledEnabled("Deal Board", "That one self-destructed. Press Scan again.", false);
            } else if (!elseMaxData.equals(this.elseMaxData) || timeMillis - this.timeMillis > 2500L) {
               this.elseMaxData = elseMaxData;
               this.timeMillis = timeMillis;
            } else if (timeMillis - this.timeMillis >= 400L) {
               this.updateElseMax();
               SpecialItemBuyer specialItemBuyer = this.specialItemBuyer;
               int floorMod2 = floorMod;
               specialItemBuyer.onBoardBuyPressed(elseMaxData, () -> handleInstance(specialItemBuyer, floorMod2));
            }
         } else {
            this.updateElseMax();
         }
      }
   }

   private String formatFakeFakes(DealBoard.View maxFakes) {
      int fakeValue = this.getNextInteger();
      if (maxFakes == this.maxFakes && fakeValue == this.fakeValue) {
         return this.joinFakeFakes;
      } else {
         this.maxFakes = maxFakes;
         this.fakeValue = fakeValue;
         MaxDataOver scanData = maxFakes.scanData;
         var arrayList = new ArrayList(4);
         if (scanData.fakes() > 0) {
            arrayList.add(formatText(scanData.fakes(), "fake"));
         }

         if (scanData.unread() > 0) {
            arrayList.add(scanData.unread() + " couldn't be read");
         }

         if (scanData.tooShort() > 0) {
            arrayList.add(scanData.tooShort() + " under " + (fakeValue <= 0 ? "your Min Time Left" : DaysHoursUtils.formatText(fakeValue * 60L)));
         }

         if (scanData.overPrice() > 0) {
            arrayList.add(scanData.overPrice() + " over Max Price");
         }

         if (scanData.overBudget() > 0) {
            arrayList.add(scanData.overBudget() + " over budget");
         }

         this.joinFakeFakes = String.join("  \u00b7  ", arrayList);
         this.rankedDestructText = "Skipped " + this.joinFakeFakes;
         return this.joinFakeFakes;
      }
   }

   private static String formatText(ElseMaxData elseMaxData) {
      return FormatFakesUtils.formatSecondsPrice(elseMaxData);
   }

   @Override
   public float getSettingsCleric() {
      if (this.matrixStackActive) {
         return 0.0F;
      } else {
         this.updateMaxAmount();
         return this.maxContentWidth;
      }
   }

   private static ItemStack getNetheriteIngot(ShardAxeType shardAxeType) {
      Item item = switch (shardAxeType.getShovelPickaxeType()) {
         case PICKAXE -> Items.NETHERITE_PICKAXE;
         case AXE -> Items.NETHERITE_AXE;
         case SHOVEL -> Items.NETHERITE_SHOVEL;
         case OTHER -> Items.NETHERITE_INGOT;
      };
      ItemStack itemStack = new ItemStack(item);
      itemStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
      return itemStack;
   }

   @Override
   public boolean canMaxAmount(ScannedSkipped scannedSkipped, double dealBoardValue, double dealBoardValue2, double d) {
      this.limitsPanelAnim.handleAmount(d, 24.0F);
      return true;
   }

   private String formatLittleTime() {
      int noneLittleValue = this.getNextInteger();
      if (noneLittleValue != this.noneLittleValue) {
         this.noneLittleValue = noneLittleValue;
         this.littleText = noneLittleValue <= 0 ? "None" : DaysHoursUtils.formatText(noneLittleValue * 60L);
         this.littleTimeText = noneLittleValue <= 0 ? "Too little time" : "Under " + this.littleText;
      }

      return this.littleText;
   }

   private boolean isModMax() {
      DealBoard.View fakessMax = this.getFakessMax(floorMod);
      return fakessMax != null && fakessMax.scanData.getRanked() != null && ElseMaxUtils.canScanned(fakessMax.scanData, this.currentMillis);
   }

   @Override
   public float getMax() {
      this.updateMaxAmount();
      return this.maxColumnWidth;
   }

   private static String formatText(int dealBoardValue, String dealBoardText) {
      return dealBoardValue + " " + dealBoardText + (dealBoardValue == 1 ? "" : "s");
   }

   @Override
   public int getShowSchematics() {
      return this.matrixStackActive ? 0 : inspectsShardAxeTypes.length;
   }

   private void renderScanPress(DrawContext context, ButtonMin buttonMin, int pressValue, DealBoard.View view) {
      Identifier identifier;
      String pressText;
      String dealBoardText;
      if (view == null) {
         if (this.scanCartFlag) {
            identifier = IcoGenUtils.getIdentifier("shopping-cart");
            pressText = "Scanning /ah";
            dealBoardText = this.workingCartText.isEmpty() ? null : this.workingCartText;
         } else {
            identifier = IcoGenUtils.getIdentifier("shopping-cart");
            pressText = "No scan yet";
            dealBoardText = "Press Scan to check /ah";
         }
      } else if (!view.scanData.opened()) {
         identifier = IcoGenUtils.getIdentifier("x");
         pressText = "The auction house didn't open";
         dealBoardText = "Wait a moment, then press Scan again";
      } else if (view.scanData.listings() == 0 && view.scanData.fakes() == 0 && view.scanData.unread() == 0) {
         identifier = IcoGenUtils.getIdentifier("package");
         pressText = "No listings on /ah right now";
         dealBoardText = "Press Scan again later";
      } else if (view.scanData.listings() == 0 && view.scanData.unread() > 0) {
         identifier = IcoGenUtils.getIdentifierForString("cross");
         pressText = "Couldn't read the listings";
         dealBoardText = "Check Self Destruct Word and Price Keyword in More settings";
      } else if (view.scanData.listings() == 0) {
         identifier = IcoGenUtils.getIdentifierForString("shield");
         pressText = "Only fakes on /ah right now";
         dealBoardText = "Fakes are never bought. Press Scan again later";
      } else {
         identifier = IcoGenUtils.getIdentifierForString("shield");
         pressText = "Nothing fits your limits";
         dealBoardText = this.formatFakeFakes(view).isEmpty() ? "Change your limits in More settings" : this.rankedDestructText;
      }

      float dealBoardBound = Math.max(0.0F, buttonMin.cubeSchematicAmount2Cube - 16.0F);
      MatrixStackUtilsRelease.render(
         context,
         buttonMin.cubeKnifeAmount,
         buttonMin.cubeSchematicAmount,
         buttonMin.cubeSchematicAmount2Cube,
         buttonMin.cubeSchematicAmount3Cube,
         identifier,
         this.formatTextsAmounts(pressText, dealBoardBound, 1.0F),
         dealBoardText == null ? null : this.formatTextsAmounts(dealBoardText, dealBoardBound, 0.82F)
      );
   }

   @Override
   public boolean isFlag() {
      return true;
   }

   @Override
   public String formatSchematicsText() {
      int limitsValue = this.previewRail.getFadeValue();
      MaxDataOver maxData = !this.matrixStackActive && limitsValue >= 0 && limitsValue < inspectsShardAxeTypes.length ? this.specialItemBuyer.getDealScan(inspectsShardAxeTypes[limitsValue]) : null;
      if (this.listing != null
         && limitsValue == this.limitsValue
         && this.scanCartFlag == this.schematicsActive
         && this.matrixStackActive == this.limitsWordingFlag
         && maxData == this.maxData
         && this.workingScannedText.equals(this.limitsWordingText)
         && this.currentMillis - this.limitsWordingTime < 400L) {
         return this.listing;
      } else {
         this.limitsValue = limitsValue;
         this.schematicsActive = this.scanCartFlag;
         this.limitsWordingFlag = this.matrixStackActive;
         this.maxData = maxData;
         this.limitsWordingText = this.workingScannedText;
         this.limitsWordingTime = this.currentMillis;
         if (this.matrixStackActive) {
            this.listing = "More settings  \u00b7  limits, speed and server wording";
         } else if (this.scanCartFlag) {
            this.listing = this.workingCartText.isEmpty() ? "Working..." : this.workingCartText;
         } else if (!this.workingScannedText.isEmpty()) {
            this.listing = this.workingScannedText;
         } else if (maxData == null) {
            this.listing = "Not scanned yet. Press Scan.";
         } else {
            this.listing = "Scanned "
               + FormatFakesUtils.formatTextForLong(maxData.scannedAtMs(), this.currentMillis)
               + "  \u00b7  "
               + formatText(maxData.pages(), "page")
               + "  \u00b7  "
               + formatText(maxData.listings(), "listing");
         }

         return this.listing;
      }
   }

   private void updateMaxAmount() {
      float amount = Math.min(560.0F, Math.max(220.0F, this.width - 24.0F)) - 24.0F;
      if (amount != this.lastAvailableWidth) {
         this.lastAvailableWidth = amount;
         float dealBoardValue = 16.0F;
         float min2 = Math.min(1.0F, (amount - dealBoardValue) / 520.0F);
         float max = (float)Math.floor(Math.max(96.0F, 128.0F * min2));
         float floor2 = (float)Math.floor(Math.max(120.0F, 140.0F * min2));
         if (amount - dealBoardValue - max - floor2 >= 210.0F) {
            this.maxColumnWidth = max;
            this.maxContentWidth = floor2;
         } else {
            this.maxColumnWidth = (float)Math.floor(Math.max(96.0F, (amount - 8.0F) * 128.0F / 380.0F));
            this.maxContentWidth = 0.0F;
         }
      }
   }

   private static boolean canDidnScan(DealBoard.View view, float didnScanValue, float matrixStackValue) {
      return didnScanValue < MatrixStackUtilsRelease.getAmount(view.didnOpenTexts[0], matrixStackValue) + 12.0F + view.scanAmount;
   }

   private String formatLimitText() {
      int limitValue = this.nextLimit.getValue();
      if (limitValue != this.limitValue) {
         this.limitValue = limitValue;
         this.limitMaxText = limitValue <= 0 ? "No limit" : formatText((long)limitValue);
      }

      return this.limitMaxText;
   }

   @Override
   public void renderThumbsInspects(DrawContext context, int index2, float thumbsInspectsValue, float thumbsInspectsValue2, float thumbsInspectsValue3) {
      if (index2 >= 0 && index2 < inspectsShardAxeTypes.length) {
         renderMatrixPop(context, this.dealBoardItemStacks[index2], thumbsInspectsValue + (thumbsInspectsValue3 - 16.0F) / 2.0F, thumbsInspectsValue2 + (thumbsInspectsValue3 - 16.0F) / 2.0F);
      }
   }

   private void handleIntegerForInt(int integerValue) {
      if (this.nextInteger != null) {
         this.nextInteger.handleMax(DealBoardUtils.getValueForIntFlag(this.nextInteger.getValue(), integerValue, isPressedWindow(), this.nextInteger.getMaxValue()));
         this.updateElseMax();
      }
   }

   private void handleFlagMax(DealBoard.View view, float dealBoardValue, float dealBoardValue2) {
      float dealBoardValue3 = 8.0F;
      float maxSpendAmounts = Math.max(view.viewMaxWidth, this.getSpendAmounts(0));
      float max2 = Math.max(view.ceilViewBound, this.getSpendAmounts(1));
      float max3 = Math.max(view.column3Width, this.getSpendAmounts(3));
      float max4 = Math.max(view.column4Width, this.getSpendAmounts(4));
      float max5 = Math.max(view.viewAmount, this.getSpendAmounts(5));
      float dealBoardValue4 = maxSpendAmounts + dealBoardValue3 + max2 + dealBoardValue3;
      float pilotStatusBound = Math.max(view.column2Width, this.getSpendAmounts(2));
      float dealBoardBound = view.column2Width + 5.0F + 14.0F;
      float dealBoardLimit = Math.max(pilotStatusBound, dealBoardBound);
      this.durabilityPageActive = true;
      this.extraColumnVisible = true;
      this.dealBoardActive = true;
      if (this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5) < dealBoardLimit) {
         this.extraColumnVisible = false;
      }

      if (this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5) < dealBoardLimit) {
         this.dealBoardActive = false;
      }

      if (!this.dealBoardActive) {
         this.extraColumnVisible = true;
         if (this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5) < pilotStatusBound) {
            this.extraColumnVisible = false;
         }
      }

      if (this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5) < pilotStatusBound) {
         this.durabilityPageActive = false;
      }

      if (!this.durabilityPageActive && !this.dealBoardActive && !this.extraColumnVisible && this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5) >= dealBoardLimit) {
         this.dealBoardActive = true;
      }

      float dealBoardValue5 = this.getFlag(dealBoardValue2, dealBoardValue4, max3, max4, max5);
      this.column1X = dealBoardValue + maxSpendAmounts + dealBoardValue3 + max2;
      this.column2X = this.column1X + dealBoardValue3;
      this.dealBoardAmount = this.column2X + view.column2Width + 5.0F;
      this.detailPanelWidth = this.dealBoardActive ? Math.min(64.0F, dealBoardValue5 - view.column2Width - 5.0F) : 0.0F;
      float amount = dealBoardValue + dealBoardValue2;
      this.column5X = amount;
      if (this.extraColumnVisible) {
         amount -= max5 + dealBoardValue3;
      }

      this.column4X = amount;
      if (this.durabilityPageActive) {
         amount -= max4 + dealBoardValue3;
      }

      this.column3X = amount;
   }

   private void updateSpecialBuyer() {
      if (!this.matrixStackActive && !this.specialItemBuyer.isRunning()) {
         this.updateElseMax();
         SpecialItemBuyer specialItemBuyer = this.specialItemBuyer;
         int floorMod2 = floorMod;
         specialItemBuyer.onBoardScanPressed(() -> handleInstance(specialItemBuyer, floorMod2));
         this.previewRail.updateFadeCrossfade();
      }
   }

   private void updateMatrixStackMatrixStackHelper() {
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }

      this.matrixStackActive = false;
      this.previewRail.handleCrossfadeFlag(floorMod, false);
      this.previewRail.updateFadeCrossfade();
   }

   @Override
   public void renderSkippedLim(DrawContext context, ScannedSkipped scannedSkipped) {
      float sideBuildingAmount = scannedSkipped.sideBuildingAmount;
      float sideWalkAmount = scannedSkipped.sideWalkAmount;
      int skippedLimValue = this.previewRail.getFadeValue();
      DealBoard.View fakessMax = this.getFakessMax(skippedLimValue);
      this.skippedLimActive = scannedSkipped.skippedLimActive && scannedSkipped.canScannedSkipped(scannedSkipped.hoverX, scannedSkipped.scannedSkippedAmount);
      this.limitsPanelAnim.handleMaxAmount(this.skippedLimAmount, scannedSkipped.skippedLimAmount);
      float skippedLimValue2 = MatrixStackUtilsAmount.getAmount(this.limitsPanelAnim.getDisplayedScroll());
      float sideBuildingValue = scannedSkipped.skippedScannedAmount - skippedLimValue2;
      MatrixStackUtilsRelease.renderScissorEnable(context, sideBuildingAmount - 2.0F, scannedSkipped.skippedScannedAmount, sideWalkAmount + 4.0F, scannedSkipped.skippedLimAmount);

      float skippedLimValue4;
      try {
         float skippedLimTotal = getAmount(context, sideBuildingAmount, sideWalkAmount, sideBuildingValue, "Skipped");
         if (fakessMax == null) {
            MatrixStackUtilsRelease.handle("Not scanned yet", sideBuildingAmount, skippedLimTotal, 13.0F, 0.82F, -11117467);
            skippedLimTotal += 13.0F;
         } else {
            int skippedLimCounter = 0;
            MaxDataOver scanData = fakessMax.scanData;
            if (scanData.fakes() > 0) {
               this.render(context, sideBuildingAmount, skippedLimTotal, sideWalkAmount, -1750963, "Fakes", fakessMax.fakesReadText, fakesReadValue);
               skippedLimTotal += 13.0F;
               skippedLimCounter++;
            }

            if (scanData.unread() > 0) {
               this.render(context, sideBuildingAmount, skippedLimTotal, sideWalkAmount, -1535700, "Couldn't read", fakessMax.readCouldnText, -1535700);
               skippedLimTotal += 13.0F;
               skippedLimCounter++;
            }

            if (scanData.tooShort() > 0) {
               this.render(context, sideBuildingAmount, skippedLimTotal, sideWalkAmount, -1535700, this.formatLittleText(), fakessMax.priceReadText, -1535700);
               skippedLimTotal += 13.0F;
               skippedLimCounter++;
            }

            if (scanData.overPrice() > 0) {
               this.render(context, sideBuildingAmount, skippedLimTotal, sideWalkAmount, -7630437, "Over Max Price", fakessMax.priceMaxText, -5854026);
               skippedLimTotal += 13.0F;
               skippedLimCounter++;
            }

            if (scanData.overBudget() > 0) {
               this.render(context, sideBuildingAmount, skippedLimTotal, sideWalkAmount, -7630437, "Over budget", fakessMax.budgetSkippedText, -5854026);
               skippedLimTotal += 13.0F;
               skippedLimCounter++;
            }

            if (skippedLimCounter == 0) {
               MatrixStackUtilsRelease.handle("Nothing skipped", sideBuildingAmount, skippedLimTotal, 13.0F, 0.82F, skippedNothingValue);
               skippedLimTotal += 13.0F;
            }

            if (scanData.fakes() > 0) {
               MatrixStackUtilsRelease.handle(this.formatTextsAmounts("Fakes are never bought.", sideWalkAmount, 0.82F), sideBuildingAmount, skippedLimTotal, 13.0F, 0.82F, -11117467);
               skippedLimTotal += 13.0F;
            }

            if (scanData.unread() > 0) {
               MatrixStackUtilsRelease.handle(this.formatTextsAmounts("Check Server Wording.", sideWalkAmount, 0.82F), sideBuildingAmount, skippedLimTotal, 13.0F, 0.82F, -11117467);
               skippedLimTotal += 13.0F;
            }
         }

         float matrixStackValue = getAmount(context, sideBuildingAmount, sideWalkAmount, skippedLimTotal + 12.0F, "Your limits");
         if (this.nextLimit != null) {
            matrixStackValue = this.getMinusPlus(
               context, scannedSkipped, "lim:price", "lim:price-", sideBuildingAmount, matrixStackValue, sideWalkAmount, "Max Price", this.formatLimitText(), this.intConsumer, this.maxPriceStepConsumer
            );
         }

         if (this.nextInteger != null) {
            matrixStackValue = this.getMinusPlus(
               context, scannedSkipped, "lim:min", "lim:min-", sideBuildingAmount, matrixStackValue, sideWalkAmount, "Min Time Left", this.formatLittleTime(), this.minTimeStepConsumer, this.dealBoardIntConsumer
            );
         }

         if (this.integerHelper != null) {
            matrixStackValue = this.getMinusPlus(
               context, scannedSkipped, "lim:spend", "lim:spend-", sideBuildingAmount, matrixStackValue, sideWalkAmount, "Max Total Spend", this.formatLimitMax(), this.skippedLimIntConsumer, this.maxSpendStepConsumer
            );
         }

         MatrixStackUtilsRelease.handle(this.formatTextsAmounts("Shift-click for small steps.", sideWalkAmount, 0.82F), sideBuildingAmount, matrixStackValue, 13.0F, 0.82F, -11117467);
         skippedLimValue4 = matrixStackValue + 13.0F;
      } finally {
         context.disableScissor();
      }

      this.skippedLimAmount = skippedLimValue4 + skippedLimValue2 - scannedSkipped.skippedScannedAmount;
      MatrixStackUtilsRelease.render(context, sideBuildingAmount + sideWalkAmount, scannedSkipped.skippedScannedAmount + 2.0F, Math.max(0.0F, scannedSkipped.skippedLimAmount - 4.0F), this.limitsPanelAnim);
   }

   private void handleIntegerNextLimit(int integerValue) {
      this.handle(this.nextLimit, integerValue);
   }

   private void handleModLimits(int modLimitsValue) {
      if (!this.matrixStackActive) {
         int floorMod2 = Math.floorMod(modLimitsValue, inspectsShardAxeTypes.length);
         this.previewRail.handleCrossfadeFlag(floorMod2, true);
         floorMod = floorMod2;
         this.updateElseMax();
      }
   }

   @Override
   public void handleThumbsAmount(float thumbsValue) {
      this.currentMillis = System.currentTimeMillis();
      this.maxDataThumbs = this.specialItemBuyer.getCurrentRules();
      this.scanCartFlag = this.specialItemBuyer.isRunning();
      String thumbsText = this.scanCartFlag ? this.specialItemBuyer.getStatusText() : "";
      this.workingCartText = thumbsText == null ? "" : thumbsText;
      this.workingScannedText = this.specialItemBuyer.getRecentResultMessage(this.currentMillis);
      this.scanSettingsFlag = false;

      for (int i = 0; i < inspectsShardAxeTypes.length; i++) {
         this.scanSettingsFlag = this.scanSettingsFlag | this.getFakessMax(i) != null;
      }

      int value = this.previewRail.getFadeValue();
      if (value != this.lastThumbsValue) {
         this.lastThumbsValue = value;
         this.pagePanelAnim.handleAmountMax(0.0F);
      }

      this.pagePanelAnim.handleAmountMax(thumbsValue, this.previewRail.getButtonMin().thumbsActive);
      this.limitsPanelAnim.handleAmountMax(thumbsValue, this.skippedLimActive);
      this.thumbsActive = false;
      if (this.elseMaxData != null) {
         DealBoard.View fakessMax = this.getFakessMax(floorMod);
         if (this.scanCartFlag
            || this.currentMillis - this.timeMillis > 2500L
            || fakessMax == null
            || !this.elseMaxData.equals(fakessMax.scanData.getRanked())
            || ElseMaxUtils.canScanned(fakessMax.scanData, this.currentMillis)) {
            this.updateElseMax();
         }
      }
   }

   private static float getAmount(DrawContext context, float dealBoardValue, float sideWalkAmount, float sideBuildingValue, String dealBoardText) {
      MatrixStackUtilsAmount.renderRoot(context, dealBoardText, dealBoardValue, sideBuildingValue, sideWalkAmount);
      return sideBuildingValue + 12.0F + 5.0F;
   }

   private static float getDidnScan(DealBoard.View view, float didnScanValue, boolean didnScanFlag) {
      if (!didnScanFlag) {
         return 2.0F;
      } else {
         float didnScanDivisor = MatrixStackUtilsRelease.getAmount(view.didnOpenTexts[0], 1.0F);
         if (didnScanDivisor <= 0.0F) {
            return 1.4F;
         } else {
            float didnScanLimit = (didnScanValue - 12.0F - view.scanAmount) / didnScanDivisor;
            return didnScanLimit >= 1.0F ? Math.min(1.4F, didnScanLimit) : Math.max(1.0F, Math.min(1.4F, didnScanValue / didnScanDivisor));
         }
      }
   }

   private void renderDurabilityPage(DrawContext context, ButtonMin buttonMin, DealBoard.View view, float durabilityPageValue, float matrixStackValue, float durabilityPageValue3) {
      boolean guidePackageFlag = buttonMin.guidePackageFlag;
      float durabilityPageValue4 = buttonMin.cubeKnifeAmount + durabilityPageValue;
      float durabilityPageValue5 = buttonMin.cubeSchematicAmount2Cube - durabilityPageValue * 2.0F;
      float amount = matrixStackValue + 13.0F + 3.0F;
      float max = Math.max(0.0F, durabilityPageValue3 - amount);
      if (max < 16.0F) {
         if (guidePackageFlag) {
            this.pagePanelAnim.handleMaxAmount(0.0F, 0.0F);
         }
      } else {
         this.handleFlagMax(view, durabilityPageValue4, durabilityPageValue5);
         int durabilityPageValue6 = -11117467;
         MatrixStackUtilsRelease.handle(pageDurabilityTexts[0], durabilityPageValue4, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         handle(pageDurabilityTexts[1], this.column1X, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         MatrixStackUtilsRelease.handle(pageDurabilityTexts[2], this.column2X, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         handle(pageDurabilityTexts[3], this.column3X, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         if (this.durabilityPageActive) {
            handle(pageDurabilityTexts[4], this.column4X, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         }

         if (this.extraColumnVisible) {
            handle(pageDurabilityTexts[5], this.column5X, matrixStackValue, 13.0F, 0.75F, durabilityPageValue6);
         }

         float rootDivisor = MatrixPeekUtilsRender.getAmount();
         MatrixPeekUtilsRender.renderMatrixPeek(context, durabilityPageValue4 - 4.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue + 13.0F), durabilityPageValue5 + 8.0F, rootDivisor, CardMatrixUtils.getValueInt());
         float durabilityPageValue8 = 0.0F;
         if (guidePackageFlag) {
            this.pagePanelAnim.handleMaxAmount(view.rankedCount * 16.0F, max);
            this.thumbsActive = true;
            this.cubeAmount = buttonMin.cubeKnifeAmount + buttonMin.cubeSchematicAmount2Cube - 5.0F;
            this.durabilityPageAmount = amount;
            this.pageWidth = max;
            durabilityPageValue8 = MatrixStackUtilsAmount.getAmount(this.pagePanelAnim.getDisplayedScroll());
         }

         if (!(max <= 1.0F) && view.rankedCount != 0) {
            int max9 = Math.max(0, (int)Math.floor(durabilityPageValue8 / 16.0F));
            int durabilityPageLimit = Math.min(view.rankedCount - 1, (int)Math.floor((durabilityPageValue8 + max) / 16.0F));
            MatrixStackUtilsRelease.renderScissorEnable(context, buttonMin.cubeKnifeAmount, amount, buttonMin.cubeSchematicAmount2Cube, max);

            try {
               for (int i = max9; i <= durabilityPageLimit; i++) {
                  this.renderAmountTexts(context, buttonMin, guidePackageFlag, view, i, amount + i * 16.0F - durabilityPageValue8, durabilityPageValue4, durabilityPageValue5, amount, amount + max);
               }
            } finally {
               context.disableScissor();
            }
         }
      }
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseY, int glfwValue) {
      return this.matrixStackActive && this.matrixStackHelper != null && this.matrixStackHelper.canAmountGlfw(matrixStackValue, mouseY, glfwValue);
   }

   @Override
   public String formatSoldBought(int soldBoughtLimit) {
      if (soldBoughtLimit >= 0 && soldBoughtLimit < inspectsShardAxeTypes.length) {
         DealBoard.View fakessMax = this.getFakessMax(soldBoughtLimit);
         return fakessMax != null ? fakessMax.fakesListingsText : "Not scanned";
      } else {
         return "";
      }
   }

   private float getSpendAmounts(int index) {
      if (this.spendAmounts[index] < 0.0F) {
         this.spendAmounts[index] = MatrixStackUtilsRelease.getAmount(pageDurabilityTexts[index], 0.75F);
      }

      return this.spendAmounts[index];
   }

   private static final class View {
      final float scanAmount;
      final String scanText;
      final String[] viewTexts;
      final float viewMaxWidth;
      final String readCouldnText;
      final String fakesListingsText;
      final String fitsNothingText;
      final float ceilViewBound;
      final String[] didnOpenTexts;
      final MaxDataOver scanData;
      final String priceMaxText;
      final long snapshotTime;
      final MaxDataOver maxData;
      final long[] viewTable;
      final String[] timeTexts;
      final String priceReadText;
      final String scanFitsText;
      final Rules rules;
      final float viewAmount;
      final int rankedCount;
      final String[] elseTexts;
      final float column3Width;
      final float column2Width;
      final String budgetSkippedText;
      final float column4Width;
      final String[] pageNumberTexts;
      final String fakesReadText;
      final String[] fitsNothingTexts;

      String formatTextsTimes(int index, long viewValue) {
         if (this.viewTable[index] != viewValue || this.timeTexts[index] == null) {
            this.viewTable[index] = viewValue;
            this.timeTexts[index] = DaysHoursUtils.formatText(viewValue);
         }

         return this.timeTexts[index];
      }

      View(MaxDataOver maxData, MaxDataOver maxData2, Rules maxData3, long time, long viewValue) {
         this.maxData = maxData;
         this.scanData = maxData2;
         this.rules = maxData3;
         this.snapshotTime = time;
         var ranked = maxData2.ranked();
         this.rankedCount = ranked.size();
         this.viewTexts = new String[this.rankedCount];
         this.fitsNothingTexts = new String[this.rankedCount];
         this.didnOpenTexts = new String[this.rankedCount];
         this.elseTexts = new String[this.rankedCount];
         this.pageNumberTexts = new String[this.rankedCount];
         this.timeTexts = new String[this.rankedCount];
         this.viewTable = new long[this.rankedCount];
         Arrays.fill(this.viewTable, -1L);
         float matrixStackValue = 0.82F;
         float viewBound = 0.0F;
         float max2 = 0.0F;
         float max3 = 0.0F;
         float max4 = 0.0F;
         float pilotStatusBound = 0.0F;
         float max6 = Math.max(MatrixStackUtilsRelease.getAmount("59m 59s", matrixStackValue), MatrixStackUtilsRelease.getAmount("23h 59m", matrixStackValue));

         for (int i = 0; i < this.rankedCount; i++) {
            ElseMaxData elseMaxData = (ElseMaxData)ranked.get(i);
            this.viewTexts[i] = Integer.toString(i + 1);
            this.fitsNothingTexts[i] = DealBoard.formatText(elseMaxData.price());
            this.didnOpenTexts[i] = FormatFakesUtils.formatPrice(elseMaxData, maxData2.scannedAtMs(), viewValue);
            this.elseTexts[i] = Math.round(Math.max(0.0, Math.min(1.0, elseMaxData.durability())) * 100.0) + "%";
            this.pageNumberTexts[i] = Integer.toString(elseMaxData.page());
            viewBound = Math.max(viewBound, MatrixStackUtilsRelease.getAmount(this.viewTexts[i], matrixStackValue));
            max2 = Math.max(max2, MatrixStackUtilsRelease.getAmount(this.fitsNothingTexts[i], matrixStackValue));
            max3 = Math.max(max3, MatrixStackUtilsRelease.getAmount(this.didnOpenTexts[i], matrixStackValue));
            max4 = Math.max(max4, MatrixStackUtilsRelease.getAmount(this.elseTexts[i], matrixStackValue));
            pilotStatusBound = Math.max(pilotStatusBound, MatrixStackUtilsRelease.getAmount(this.pageNumberTexts[i], matrixStackValue));
            max6 = Math.max(max6, MatrixStackUtilsRelease.getAmount(DaysHoursUtils.formatText(elseMaxData.secondsLeft()), matrixStackValue));
         }

         this.viewMaxWidth = (float)Math.ceil(viewBound);
         this.ceilViewBound = (float)Math.ceil(max2);
         this.column3Width = (float)Math.ceil(max3);
         this.column4Width = (float)Math.ceil(max4);
         this.viewAmount = (float)Math.ceil(pilotStatusBound);
         this.column2Width = (float)Math.ceil(max6);
         ElseMaxData ranked2 = maxData2.getRanked();
         if (ranked2 != null) {
            this.fakesListingsText = "Best " + this.didnOpenTexts[0];
         } else if (!maxData2.opened()) {
            this.fakesListingsText = "Didn't open";
         } else if (maxData2.listings() == 0 && maxData2.unread() > 0) {
            this.fakesListingsText = "Couldn't read";
         } else if (maxData2.listings() == 0 && maxData2.fakes() > 0) {
            this.fakesListingsText = "Only fakes";
         } else if (maxData2.listings() == 0) {
            this.fakesListingsText = "No listings";
         } else {
            this.fakesListingsText = "Nothing fits";
         }

         this.fitsNothingText = "BEST " + maxData2.kind().getCommandName().toUpperCase(Locale.ROOT);
         this.scanFitsText = ranked2 == null ? "" : "Buy for " + this.fitsNothingTexts[0];
         this.scanText = ranked2 == null ? "" : "Confirm " + this.fitsNothingTexts[0];
         this.scanAmount = ranked2 == null
            ? 0.0F
            : (float)Math.ceil(
                  Math.max(
                     Math.max(MatrixStackUtilsRelease.getAmount(this.scanFitsText, matrixStackValue), MatrixStackUtilsRelease.getAmount(this.scanText, matrixStackValue)),
                     MatrixStackUtilsRelease.getAmount("Scan again first", matrixStackValue)
                  )
               )
               + 18.0F;
         this.fakesReadText = Integer.toString(maxData2.fakes());
         this.readCouldnText = Integer.toString(maxData2.unread());
         this.priceReadText = Integer.toString(maxData2.tooShort());
         this.priceMaxText = Integer.toString(maxData2.overPrice());
         this.budgetSkippedText = Integer.toString(maxData2.overBudget());
      }
   }
}
