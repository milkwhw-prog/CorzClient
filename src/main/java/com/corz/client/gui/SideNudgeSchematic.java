package com.corz.client.gui;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.world.ElseBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import com.corz.client.categories.bots.AutoBuilder;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.AmountPreviewUtils;
import com.corz.client.utils.CardStatusUtilsActive;
import com.corz.client.utils.DoneMaxUtils;
import com.corz.client.utils.MaxMaterialsUtils;
import com.corz.client.utils.ScannedSkipped;

public final class SideNudgeSchematic extends PreviewSchematic {
   private final Runnable sideBuyAction;
   private final Runnable back;
   private float scrollOffset;
   private static final int folderMaterialsValue = CardMatrixUtils.getValue(-7630437, -1750963, 0.65);
   private final Runnable refillSideAction;
   private final IntConsumer intConsumer;
   private final ButtonBase buttonBase;
   private final ButtonBase autoBuilderButtonReady;
   private final BooleanSetting booleanBase;
   private String checkingPlacedText;
   private static final int placedAllValue = CardMatrixUtils.getValue(-7630437, -8393375, 0.45);
   private String blocksText;
   private final Runnable stop;
   private final ButtonBase buttonReadyButtonBase;
   private final IntConsumer sideLookIntConsumer;
   private long lastNanoTime;
   private final ButtonBase sideNudgeButtonBase;
   private final Runnable crosshair;
   private final ButtonBase nudgeYMinusButton;
   private final Runnable viewGiveAction;
   private float contentHeight;
   private final ButtonBase buyMaterialsButton;
   private final Runnable cancel;
   private final ButtonBase autoBuilderBuilding;
   private static final String BUILDING = "Building";
   private final Runnable start;
   private long thumbsValue;
   private static final Set<String> CANCEL_PREVIEW_SET = Set.of(
      "Open Folder",
      "Refresh",
      "Show Materials",
      "Creative Materials Get",
      "Paste Schematic",
      "Preview Schematic",
      "Capture Snapshot",
      "Build Mode",
      "Auto Move",
      "Origin: Player",
      "Origin: Looking",
      "Nudge X+",
      "Nudge X-",
      "Nudge Y+",
      "Nudge Y-",
      "Nudge Z+",
      "Nudge Z-",
      "Rotation",
      "Mirror",
      "Restock When Empty",
      "Buy Materials",
      "Cancel Preview"
   );
   private final ButtonBase nudgeXMinusButton;
   private final AutoBuilder autoBuilder;
   private final Runnable paste;
   private float skippedLimAmount;
   private final ButtonBase autoBuilderFolderDone;
   private String showText;
   private static final String[] sideCancelTexts = new String[]{"X", "Y", "Z"};
   private final Runnable mapPin;
   private final BooleanSetting autoBuilderSideRefill;
   private int showOriginValue;
   private Object thumbsObject;
   private MaxMaterialsUtils.Summary typesTotal;
   private float thumbsAmount;
   private MatrixStackHelper matrixStackHelper;
   private final EnumSetting enumHelper;
   private boolean settingsMode;
   private static boolean showFlag;
   private final ButtonBase autoBuilderBuilderButton;
   private boolean schematicBuilderFlag;
   private String sideNudgeText;
   private String checkingCheckText;
   private final Runnable sideRefillAction;
   private final EnumSetting autoBuilderEnumReady;
   private SideNudgeSchematic.State buildState;
   private int checkingPlacedValue;
   private final Runnable folder;
   private final ButtonBase nudgeYPlusButton;
   private String blocksPercentText;
   private final ButtonBase autoBuilderViewGive;
   private final Runnable giveViewAction;
   private final Runnable more = this::updateMatrixStackMatrixStackHelper;
   private String schematicsPickText;
   private String enumText;
   private static final String[] sideTexts = new String[]{"side:nx-", "side:nx+", "side:ny-", "side:ny+", "side:nz-", "side:nz+"};
   private String degreesText;
   private final Runnable[] actions;
   private String schematicsPickTextSchematics;
   private final ButtonBase nudgeZPlusButton;
   private String format;

   private static boolean isSingleplayerAbilities() {
      MinecraftClient instance = MinecraftClient.getInstance();
      return instance.isInSingleplayer() && instance.player != null && instance.player.getAbilities().creativeMode;
   }

   @Override
   public float getAmount() {
      return 372.0F;
   }

   private void updateStateBuilding() {
      if (this.buildState == SideNudgeSchematic.State.READY || this.buildState == SideNudgeSchematic.State.BUILDING) {
         handle(this.autoBuilderViewGive);
      }
   }

   public static void handleScreenInstance(AutoBuilder autoBuilder) {
      if (autoBuilder != null) {
         try {
            autoBuilder.schematicBuilder.refreshFolderList();
         } catch (Throwable e) {
         }

         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new SideNudgeSchematic(instance.currentScreen, autoBuilder));
      }
   }

   @Override
   public void handleMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseReleasedValue, int button) {
      if (this.settingsMode) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.handle(matrixStackValue, mouseReleasedValue, button);
         }
      } else {
         super.handleMatrixStack(buttonMin, matrixStackValue, mouseReleasedValue, button);
      }
   }

   private float getBlocksText(DrawContext context, ScannedSkipped scannedSkipped, float blocksValue) {
      float sideBuilding = getSideBuilding(context, scannedSkipped, blocksValue, "Building");
      float sideBuildingAmount = scannedSkipped.sideBuildingAmount;
      float sideWalkAmount = scannedSkipped.sideWalkAmount;
      int placed = CardStatusUtilsActive.getPlaced();
      int total = CardStatusUtilsActive.getTotal();
      int blocksValue2 = CardStatusUtilsActive.getValue();
      long blocksLimit = DoneMaxUtils.isAmount() ? Math.round(DoneMaxUtils.getAmount()) : -1L;
      String blocksText = placed + "|" + total + "|" + blocksValue2 + "|" + blocksLimit;
      if (!blocksText.equals(this.blocksText)) {
         this.blocksText = blocksText;
         this.blocksPercentText = blocksValue2 + "%";
         this.sideNudgeText = blocksLimit > 0L ? "about " + DoneMaxUtils.formatDone(blocksLimit) + " left" : "";
         this.format = String.format(Locale.ROOT, "%,d of %,d blocks", placed, total);
      }

      MatrixStackUtilsRelease.handle(this.blocksPercentText, sideBuildingAmount, sideBuilding, 12.0F, 1.0F, -920587);
      if (!this.sideNudgeText.isEmpty()) {
         float blocksValue3 = MatrixStackUtilsRelease.getAmount(this.sideNudgeText, 0.82F);
         MatrixStackUtilsRelease.handle(this.sideNudgeText, sideBuildingAmount + sideWalkAmount - blocksValue3, sideBuilding, 12.0F, 0.82F, -7630437);
      }

      float configsLikeValue = MatrixStackUtilsAmount.getAmount(sideBuilding + 12.0F + 4.0F);
      MatrixPeekUtilsRender.renderMatrixPeek(context, sideBuildingAmount, configsLikeValue, sideWalkAmount, 2.0F, 1.0F, MatrixStackUtilsAmount.getValue(-1, 0.06F));
      float materialFactor = Math.max(2.0F, sideWalkAmount * CardStatusUtilsActive.getAmount());
      MatrixPeekUtilsRender.renderMatrixPeek(context, sideBuildingAmount, configsLikeValue, materialFactor, 2.0F, 1.0F, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.9F));
      MatrixStackUtilsRelease.handle(this.format, sideBuildingAmount, configsLikeValue + 2.0F + 4.0F, 12.0F, 0.82F, -7630437);
      return configsLikeValue + 2.0F + 4.0F + 12.0F;
   }

   @Override
   public void handleFolderDone(PreviewText previewText) {
      if (this.settingsMode) {
         previewText.getClassPreviewText("back", "Back", this.back).getFlag().getClass073("done", "Done", this.back);
      } else if (this.buildState == SideNudgeSchematic.State.EMPTY) {
         previewText.getText("corzclient/schematics").getFlag().getClass073("folder", "Open folder", this.folder);
      } else {
         previewText.getClassPreviewText("folder", "Open folder", this.folder).getClassPreviewText("more", "More settings", this.more).getFlag();
         if (this.buildState == SideNudgeSchematic.State.BUILDING) {
            previewText.getClassPreviewText("stop", "Stop building", this.stop);
         } else {
            if (this.buildState != SideNudgeSchematic.State.NONE && this.autoBuilderBuilderButton != null) {
               previewText.getClassPreviewText("cancel", "Cancel preview", this.cancel);
            }

            if (this.buildState == SideNudgeSchematic.State.READY && isInSingleplayer() && this.autoBuilderFolderDone != null) {
               previewText.getClassPreviewText("paste", "Paste", this.paste);
            }

            if (this.buildState == SideNudgeSchematic.State.READY) {
               previewText.getClass073("start", "Start building", this.start);
            } else {
               previewText.getClassPreviewText("start", "Start building", this.start);
            }
         }
      }
   }

   private float getSideLook(DrawContext context, ScannedSkipped scannedSkipped, float sideLookValue, boolean sideLookFlag) {
      float sideBuilding = getSideBuilding(context, scannedSkipped, sideLookValue, "Place it");
      float sideBuildingAmount = scannedSkipped.sideBuildingAmount;
      float sideWalkAmount = scannedSkipped.sideWalkAmount;
      float sideLookValue2 = (sideWalkAmount - 4.0F) / 2.0F;
      this.render(context, scannedSkipped, "side:feet", sideBuildingAmount, sideBuilding, sideLookValue2, "map-pin", "At my feet", sideLookFlag, this.mapPin);
      this.render(context, scannedSkipped, "side:look", sideBuildingAmount + sideLookValue2 + 4.0F, sideBuilding, sideLookValue2, "crosshair", "Where I look", sideLookFlag, this.crosshair);
      float matrixStackValue = sideBuilding + 16.0F + 4.0F;
      this.renderReleaseRight(context, scannedSkipped, "side:rot", sideBuildingAmount, matrixStackValue, sideLookValue2, "Rotate", this.formatTextEnum(), sideLookFlag, this.sideLookIntConsumer);
      this.renderReleaseRight(context, scannedSkipped, "side:mir", sideBuildingAmount + sideLookValue2 + 4.0F, matrixStackValue, sideLookValue2, "Mirror", this.formatNoneEnum(), sideLookFlag, this.intConsumer);
      float sideLookValue4 = matrixStackValue + 16.0F + 4.0F;
      this.renderSideTexts(context, scannedSkipped, sideBuildingAmount, sideLookValue4, sideWalkAmount, sideLookFlag);
      return sideLookValue4 + 16.0F;
   }

   private void updateBase() {
      if (this.autoBuilderSideRefill != null) {
         this.autoBuilderSideRefill.handle(!this.autoBuilderSideRefill.isEnabled());
      }
   }

   private String formatCheckingPlaced(boolean checkingPlacedFlag) {
      String checkingCheckText;
      if (!checkingPlacedFlag) {
         checkingCheckText = this.buildState == SideNudgeSchematic.State.LOADING ? "load" : "none";
      } else if (this.typesTotal == null) {
         checkingCheckText = "check";
      } else {
         checkingCheckText = this.typesTotal.remaining + "|" + this.typesTotal.missingTypes + "|" + this.typesTotal.types;
      }

      if (checkingCheckText.equals(this.checkingCheckText)) {
         return this.checkingPlacedText;
      } else {
         this.checkingCheckText = checkingCheckText;
         if (!checkingPlacedFlag) {
            this.checkingPlacedText = this.buildState == SideNudgeSchematic.State.LOADING ? "Checking..." : "-";
            this.checkingPlacedValue = -7630437;
         } else if (this.typesTotal == null) {
            this.checkingPlacedText = "Checking...";
            this.checkingPlacedValue = -7630437;
         } else if (this.typesTotal.remaining == 0) {
            this.checkingPlacedText = "All placed";
            this.checkingPlacedValue = placedAllValue;
         } else if (this.typesTotal.missingTypes == 0) {
            this.checkingPlacedText = "None";
            this.checkingPlacedValue = placedAllValue;
         } else {
            this.checkingPlacedText = this.typesTotal.missingTypes + " of " + this.typesTotal.types + " types";
            this.checkingPlacedValue = folderMaterialsValue;
         }

         return this.checkingPlacedText;
      }
   }

   private void updateButtonReady() {
      if (this.buildState == SideNudgeSchematic.State.READY) {
         handle(this.autoBuilderButtonReady);
      }
   }

   @Override
   protected Screen getJoinFlag() {
      return this.schematicBuilderFlag ? null : this.currentScreen;
   }

   public static String getAppend(String appendText) {
      MinecraftClient instance = MinecraftClient.getInstance();
      String appendText2 = appendText == null ? "" : appendText.trim().toLowerCase(Locale.ROOT);
      if (appendText2.equals("open")) {
         handleScreenInstance((AutoBuilder)ModuleRegistry.getModule(AutoBuilder.class));
         return "builder: opened";
      } else if (instance.currentScreen instanceof SideNudgeSchematic sideNudgeSchematic) {
         if (!appendText2.startsWith("fold ")) {
            switch (appendText2) {
               case "settings":
                  sideNudgeSchematic.updateMatrixStackMatrixStackHelper();
                  break;
               case "build":
                  sideNudgeSchematic.updateMatrixStack();
                  break;
               case "stop":
                  sideNudgeSchematic.updateAutoBuilder();
                  break;
               case "start":
                  sideNudgeSchematic.updateSchematicBuilder();
                  break;
               case "more":
                  sideNudgeSchematic.updateShowFlag();
                  break;
               case "cancel":
                  sideNudgeSchematic.updateBuilderButton();
                  break;
               default:
                  return "builder: unknown " + appendText;
            }

            return "builder: " + appendText2 + " -> state=" + sideNudgeSchematic.buildState + " settingsMode=" + sideNudgeSchematic.settingsMode;
         } else {
            return "builder: fold "
               + (
                  sideNudgeSchematic.settingsMode && sideNudgeSchematic.matrixStackHelper != null
                     ? sideNudgeSchematic.matrixStackHelper.formatAmount(Integer.parseInt(appendText2.substring(5).trim()))
                     : "not on More settings"
               );
         }
      } else {
         return "builder: not open";
      }
   }

   private static void handle(ButtonBase buttonBase) {
      if (buttonBase != null) {
         buttonBase.updateAction();
      }
   }

   private void renderReleaseRight(
      DrawContext context, ScannedSkipped scannedSkipped, String releaseRightText, float releaseRightValue, float matrixStackValue, float materialFactor, String matrixStackText, String releaseRightText3, boolean releaseRightFlag, IntConsumer intConsumer
   ) {
      float cardMatrixFactor = releaseRightFlag ? scannedSkipped.getMin(releaseRightText, releaseRightValue, matrixStackValue, materialFactor, 16.0F) : 0.0F;
      MatrixStackUtilsRelease.render(context, releaseRightValue, matrixStackValue, materialFactor, 16.0F, cardMatrixFactor, 0.0F);
      float releaseRightValue5 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
      float releaseRightValue6 = MatrixStackUtilsRelease.getAmount(releaseRightText3, 0.82F);
      float releaseRightValue7 = 3.5F;
      float releaseRightValue8 = MatrixStackUtilsAmount.getAmount(releaseRightValue + (materialFactor - releaseRightValue5 - releaseRightValue7 - releaseRightValue6) / 2.0F);
      int releaseRightValue9 = releaseRightFlag ? CardMatrixUtils.getValue(-7630437, -5854026, cardMatrixFactor) : -11117467;
      int releaseRightValue10 = releaseRightFlag ? CardMatrixUtils.getValue(-920587, CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.22), cardMatrixFactor) : -11117467;
      MatrixStackUtilsRelease.handle(matrixStackText, releaseRightValue8, matrixStackValue, 16.0F, 0.82F, releaseRightValue9);
      MatrixStackUtilsRelease.handle(releaseRightText3, releaseRightValue8 + releaseRightValue5 + releaseRightValue7, matrixStackValue, 16.0F, 0.82F, releaseRightValue10);
      if (releaseRightFlag) {
         scannedSkipped.handle(releaseRightText, releaseRightValue, matrixStackValue, materialFactor, 16.0F, intConsumer);
      }
   }

   @Override
   public boolean canMaxAmount(ScannedSkipped scannedSkipped, double sideNudgeValue, double sideNudgeValue2, double sideNudgeValue3) {
      this.scrollOffset = Math.max(0.0F, Math.min(Math.max(0.0F, this.contentHeight - this.skippedLimAmount), this.scrollOffset - (float)sideNudgeValue3 * 24.0F));
      return true;
   }

   private void updateMatrixStack() {
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }

      this.settingsMode = false;
      this.updateSchematicsSelected();
      this.previewRail.updateFadeCrossfade();
   }

   private void updateBuilderButton() {
      if (this.autoBuilderBuilderButton != null
         && (this.buildState == SideNudgeSchematic.State.READY || this.buildState == SideNudgeSchematic.State.LOADING || this.buildState == SideNudgeSchematic.State.FAILED)) {
         if (this.autoBuilder.schematicBuilder.getLoadedSchematic() != null) {
            handle(this.autoBuilderBuilderButton);
         }

         this.autoBuilder.schematicBuilder.clearLoadedSchematic();
         this.updateMeshText();
         this.previewRail.updateFadeCrossfade();
      }
   }

   private static float getSideBuilding(DrawContext context, ScannedSkipped scannedSkipped, float sideBuildingValue, String sideBuildingText) {
      MatrixStackUtilsAmount.renderRoot(context, sideBuildingText, scannedSkipped.sideBuildingAmount, sideBuildingValue, scannedSkipped.sideWalkAmount);
      return sideBuildingValue + 12.0F + 5.0F;
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double matrixStackValue) {
      if (this.settingsMode) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.handleMax(matrixStackValue);
         }

         return true;
      } else {
         return super.canAmountMatrix(buttonMin, matrixValue, matrixValue2, matrixStackValue);
      }
   }

   private void updateButtonReadyBuildState() {
      if (this.buildState == SideNudgeSchematic.State.READY) {
         handle(this.buttonReadyButtonBase);
      }
   }

   private String formatTextEnum() {
      String text = this.autoBuilderEnumReady == null ? "0" : this.autoBuilderEnumReady.getCurrentOption();
      if (!text.equals(this.enumText)) {
         this.enumText = text;
         this.degreesText = text + "\u00b0";
      }

      return this.degreesText;
   }

   private String formatNoneEnum() {
      String noneEnumText = this.enumHelper == null ? "None" : this.enumHelper.getCurrentOption();
      return "None".equals(noneEnumText) ? "Off" : noneEnumText;
   }

   @Override
   public void renderCubeAmount(DrawContext context, ButtonMin buttonMin) {
      if (!this.settingsMode) {
         super.renderCubeAmount(context, buttonMin);
      }
   }

   private void updateAutoBuilder() {
      if (this.autoBuilder.isEnabled()) {
         this.autoBuilder.setEnabled(false);
      }
   }

   private void renderSideTexts(DrawContext context, ScannedSkipped scannedSkipped, float sideValue, float matrixStackValue, float sideWalkAmount, boolean sideFlag) {
      MatrixPeekUtilsRender.renderForDrawContextFloat(context, sideValue, matrixStackValue, sideWalkAmount, 16.0F, 5.0F, CardMatrixUtils.getValue7(), CardMatrixUtils.getValueInt());
      float ctxScaled = sideWalkAmount / 3.0F;
      float releaseRightBound = MatrixPeekUtilsRender.getAmount();

      for (int i = 1; i < 3; i++) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, MatrixStackUtilsAmount.getAmount(sideValue + ctxScaled * i), matrixStackValue + 4.0F, releaseRightBound, 8.0F, CardMatrixUtils.getValueInt());
      }

      String[] sideCancelTexts2 = sideCancelTexts;

      for (int j = 0; j < 3; j++) {
         float minusPlusValue = sideValue + ctxScaled * j;
         float sideValue6 = ctxScaled / 2.0F;
         this.renderMinusPlus(context, scannedSkipped, sideTexts[j * 2], minusPlusValue, matrixStackValue, sideValue6, true, sideFlag, this.actions[j * 2]);
         this.renderMinusPlus(context, scannedSkipped, sideTexts[j * 2 + 1], minusPlusValue + sideValue6, matrixStackValue, sideValue6, false, sideFlag, this.actions[j * 2 + 1]);
         float sideValue7 = MatrixStackUtilsRelease.getAmount(sideCancelTexts2[j], 0.82F);
         MatrixStackUtilsRelease.handle(sideCancelTexts2[j], MatrixStackUtilsAmount.getAmount(minusPlusValue + (ctxScaled - sideValue7) / 2.0F), matrixStackValue, 16.0F, 0.82F, sideFlag ? -920587 : -11117467);
      }
   }

   @Override
   public float getSettingsCleric() {
      return !this.settingsMode && this.buildState != SideNudgeSchematic.State.EMPTY ? 176.0F : 0.0F;
   }

   private void updateMatrixStackMatrixStackHelper() {
      if (this.matrixStackHelper == null) {
         this.matrixStackHelper = new MatrixStackHelper(this.getBuildingSchematic());
      }

      this.settingsMode = true;
      this.previewRail.updateFadeCrossfade();
   }

   private void handleEnumReady(int enumReadyLimit) {
      if (this.autoBuilderEnumReady != null && this.buildState == SideNudgeSchematic.State.READY) {
         if (enumReadyLimit >= 0) {
            this.autoBuilderEnumReady.selectNext();
         } else {
            this.autoBuilderEnumReady.selectPrevious();
         }
      }
   }

   private static void handle(float sideNudgeValue, float matrixStackValue, String sideNudgeText, float cardMatrixFactor) {
      MatrixStackUtilsRelease.handle(sideNudgeText, sideNudgeValue, matrixStackValue, 20.0F, 0.82F, CardMatrixUtils.getValue(-5854026, -920587, cardMatrixFactor));
   }

   private void render(
      DrawContext context, ScannedSkipped scannedSkipped, String sideNudgeText, String scannedSkippedText, float sideNudgeValue, float matrixStackValue, float sideWalkAmount, String sideNudgeText3, BooleanSetting booleanBase, Runnable runnable
   ) {
      if (booleanBase != null) {
         float cardMatrixFactor = scannedSkipped.getMin(sideNudgeText, sideNudgeValue, matrixStackValue, sideWalkAmount, 20.0F);
         float pillLimit = scannedSkipped.getMin(scannedSkippedText, booleanBase.isEnabled() ? 1.0F : 0.0F, 16.0F);
         handle(sideNudgeValue, matrixStackValue, sideNudgeText3, cardMatrixFactor);
         RenderPillUtils.render(context, Math.round(sideNudgeValue + sideWalkAmount - 22.0F), Math.round(matrixStackValue + 5.0F), pillLimit, cardMatrixFactor);
         scannedSkipped.handle(sideNudgeText, sideNudgeValue, matrixStackValue, sideWalkAmount, 20.0F, runnable);
      }
   }

   @Override
   public boolean canMatrixStack(int matrixStackValue, int rendererValue) {
      return this.settingsMode && this.matrixStackHelper != null && this.matrixStackHelper.canFlag(matrixStackValue, rendererValue);
   }

   @Override
   public boolean canMatrixStack(ButtonMin buttonMin, double matrixStackValue, double mouseY, int glfwValue) {
      return !this.settingsMode
         ? super.canMatrixStack(buttonMin, matrixStackValue, mouseY, glfwValue)
         : this.matrixStackHelper != null && this.matrixStackHelper.canAmountGlfw(matrixStackValue, mouseY, glfwValue);
   }

   private float getViewGive(DrawContext context, ScannedSkipped scannedSkipped, float viewGiveValue) {
      float sideBuilding = getSideBuilding(context, scannedSkipped, viewGiveValue, "Materials");
      float sideBuildingAmount = scannedSkipped.sideBuildingAmount;
      float sideWalkAmount = scannedSkipped.sideWalkAmount;
      boolean viewGiveFlag = this.buildState == SideNudgeSchematic.State.READY || this.buildState == SideNudgeSchematic.State.BUILDING;
      MatrixStackUtilsRelease.handle("Missing", sideBuildingAmount, sideBuilding, 12.0F, 0.82F, -7630437);
      String formatCheckingPlaced = this.formatCheckingPlaced(viewGiveFlag);
      float viewGiveValue2 = MatrixStackUtilsRelease.getAmount(formatCheckingPlaced, 0.82F);
      MatrixStackUtilsRelease.handle(formatCheckingPlaced, sideBuildingAmount + sideWalkAmount - viewGiveValue2, sideBuilding, 12.0F, 0.82F, this.checkingPlacedValue);
      float viewGiveValue3 = sideBuilding + 12.0F + 4.0F;
      if (viewGiveFlag && isSingleplayerAbilities() && this.autoBuilderViewGive != null) {
         float viewGiveValue4 = (sideWalkAmount - 4.0F) / 2.0F;
         this.render(context, scannedSkipped, "side:view", sideBuildingAmount, viewGiveValue3, viewGiveValue4, null, "View list", true, this.viewGiveAction);
         this.render(context, scannedSkipped, "side:give", sideBuildingAmount + viewGiveValue4 + 4.0F, viewGiveValue3, viewGiveValue4, null, "Give me all", true, this.giveViewAction);
      } else {
         this.render(context, scannedSkipped, "side:view", sideBuildingAmount, viewGiveValue3, sideWalkAmount, null, "View list", viewGiveFlag, this.viewGiveAction);
      }

      return viewGiveValue3 + 16.0F;
   }

   private void updateButtonReady3() {
      if (this.buildState == SideNudgeSchematic.State.READY && !this.autoBuilder.isBuyMaterialsFlag()) {
         handle(this.buyMaterialsButton);
      }
   }

   private static boolean isInSingleplayer() {
      return MinecraftClient.getInstance().isInSingleplayer();
   }

   private void updateBaseBooleanBase() {
      if (this.booleanBase != null) {
         this.booleanBase.handle(!this.booleanBase.isEnabled());
      }
   }

   private void handleEnumReadyForInt(int enumReadyLimit) {
      if (this.enumHelper != null && this.buildState == SideNudgeSchematic.State.READY) {
         if (enumReadyLimit >= 0) {
            this.enumHelper.selectNext();
         } else {
            this.enumHelper.selectPrevious();
         }
      }
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      if (this.settingsMode) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.renderAmountCube(context, buttonMin);
         }
      } else {
         super.renderCubeSchematic(context, buttonMin);
      }
   }

   private float getSideRefill(DrawContext context, ScannedSkipped scannedSkipped, float sideRefillValue) {
      float matrixStackValue = getSideBuilding(context, scannedSkipped, sideRefillValue, "While building") - 2.0F;
      float sideBuildingAmount = scannedSkipped.sideBuildingAmount;
      float sideWalkAmount = scannedSkipped.sideWalkAmount;
      this.render(context, scannedSkipped, "side:walk", "side:walk:on", sideBuildingAmount, matrixStackValue, sideWalkAmount, "Walk around by itself", this.autoBuilderSideRefill, this.sideRefillAction);
      this.render(context, scannedSkipped, "side:refill", "side:refill:on", sideBuildingAmount, matrixStackValue + 20.0F, sideWalkAmount, "Refill from my chests", this.booleanBase, this.refillSideAction);
      float sideBuilding = matrixStackValue + 40.0F;
      boolean buyMaterialsFlag = this.autoBuilder.isBuyMaterialsFlag();
      boolean sideRefillFlag = this.buildState == SideNudgeSchematic.State.READY && !buyMaterialsFlag;
      float cardMatrixFactor = sideRefillFlag ? scannedSkipped.getMin("side:buy", sideBuildingAmount, sideBuilding, sideWalkAmount, 20.0F) : 0.0F;
      handle(sideBuildingAmount, sideBuilding, "Buy missing on /ah", cardMatrixFactor);
      String sideRefillText = buyMaterialsFlag ? "Buying" : "Buy";
      float materialFactor = Math.max(34.0F, (float)Math.ceil(MatrixStackUtilsRelease.getAmount(sideRefillText, 0.82F)) + 14.0F);
      float sideRefillValue6 = sideBuildingAmount + sideWalkAmount - materialFactor;
      float configsLikeValue = MatrixStackUtilsAmount.getAmount(sideBuilding + 2.0F);
      MatrixStackUtilsRelease.render(context, sideRefillValue6, configsLikeValue, materialFactor, 16.0F, cardMatrixFactor, buyMaterialsFlag ? 1.0F : 0.0F);
      float sideRefillValue8 = MatrixStackUtilsRelease.getAmount(sideRefillText, 0.82F);
      int sideRefillValue9 = !sideRefillFlag && !buyMaterialsFlag ? -11117467 : MatrixStackUtilsRelease.getReleaseRight(cardMatrixFactor, buyMaterialsFlag ? 1.0F : 0.0F);
      MatrixStackUtilsRelease.handle(sideRefillText, MatrixStackUtilsAmount.getAmount(sideRefillValue6 + (materialFactor - sideRefillValue8) / 2.0F), configsLikeValue, 16.0F, 0.82F, sideRefillValue9);
      if (sideRefillFlag) {
         scannedSkipped.handle("side:buy", sideBuildingAmount, sideBuilding, sideWalkAmount, 20.0F, this.sideBuyAction);
      }

      return sideBuilding + 20.0F;
   }

   private boolean canFlag(int sideNudgeLimit) {
      return sideNudgeLimit > 10 && this.getSelectedSchematicsValue() < 8;
   }

   @Override
   public int getShowSchematics() {
      if (this.settingsMode) {
         return 0;
      } else {
         int schematicsTexts = this.getSchematicsTexts();
         if (schematicsTexts > 10 && this.getSelectedSchematicsValue() >= 8) {
            showFlag = true;
         }

         return this.canFlag(schematicsTexts) && !showFlag ? 8 : schematicsTexts;
      }
   }

   @Override
   protected void updateMatrixStack3() {
      super.updateMatrixStack3();
      if (this.matrixStackHelper != null) {
         this.matrixStackHelper.updateGlfwListening();
      }
   }

   private void render(DrawContext context, ScannedSkipped scannedSkipped, String sideNudgeText, float sideNudgeValue, float sideBuilding, float sideWalkAmount, String identifierText, String matrixStackText, boolean sideNudgeFlag, Runnable runnable) {
      float cardMatrixFactor = sideNudgeFlag ? scannedSkipped.getMin(sideNudgeText, sideNudgeValue, sideBuilding, sideWalkAmount, 16.0F) : 0.0F;
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, sideNudgeValue + sideWalkAmount / 2.0F, sideBuilding + 8.0F, scannedSkipped.getMin(sideNudgeText, 0.05F), 0.0F);

      try {
         MatrixStackUtilsRelease.render(context, sideNudgeValue, sideBuilding, sideWalkAmount, 16.0F, cardMatrixFactor, 0.0F);
         int matrixStackValue = sideNudgeFlag ? MatrixStackUtilsRelease.getReleaseRight(cardMatrixFactor, 0.0F) : -11117467;
         float sideNudgeValue6 = MatrixStackUtilsRelease.getAmount(matrixStackText, 0.82F);
         float sideNudgeLimit = identifierText == null ? 0.0F : 12.0F;
         if (sideNudgeLimit + sideNudgeValue6 > sideWalkAmount - 10.0F) {
            sideNudgeLimit = 0.0F;
         }

         float sideNudgeValue7 = MatrixStackUtilsAmount.getAmount(sideNudgeValue + (sideWalkAmount - sideNudgeLimit - sideNudgeValue6) / 2.0F);
         if (sideNudgeLimit > 0.0F) {
            MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier(identifierText), sideNudgeValue7, MatrixStackUtilsAmount.getAmount(sideBuilding + 4.0F), 8.0F, matrixStackValue);
         }

         MatrixStackUtilsRelease.handle(matrixStackText, sideNudgeValue7 + sideNudgeLimit, sideBuilding, 16.0F, 0.82F, matrixStackValue);
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (sideNudgeFlag) {
         scannedSkipped.handle(sideNudgeText, sideNudgeValue, sideBuilding, sideWalkAmount, 16.0F, runnable);
      }
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 620.0F;
   }

   @Override
   public boolean isClericLimits() {
      return this.buildState == SideNudgeSchematic.State.BUILDING || this.autoBuilder.isEnabled();
   }

   private void renderMinusPlus(DrawContext context, ScannedSkipped scannedSkipped, String minusPlusText, float minusPlusValue, float sideBuilding, float sideWalkAmount, boolean minusPlusFlag, boolean sideFlag, Runnable runnable) {
      float minusPlusLimit = sideFlag ? scannedSkipped.getMin(minusPlusText, minusPlusValue, sideBuilding, sideWalkAmount, 16.0F) : 0.0F;
      if (minusPlusLimit > 0.01F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, minusPlusValue + 1.5F, sideBuilding + 1.5F, sideWalkAmount - 3.0F, 13.0F, 3.5F, MatrixStackUtilsAmount.getValue(-1, 0.06F * minusPlusLimit));
      }

      float minusPlusValue4 = minusPlusFlag ? minusPlusValue + 6.0F : minusPlusValue + sideWalkAmount - 6.0F - 7.0F;
      int matrixStackValue = sideFlag ? CardMatrixUtils.getValue(-7630437, -920587, minusPlusLimit) : -11117467;
      MatrixStackUtilsRelease.handle(
         IcoGenUtils.getIdentifier(minusPlusFlag ? "minus" : "plus"), MatrixStackUtilsAmount.getAmount(minusPlusValue4), MatrixStackUtilsAmount.getAmount(sideBuilding + 4.5F), 7.0F, matrixStackValue
      );
      if (sideFlag) {
         scannedSkipped.handle(minusPlusText, minusPlusValue, sideBuilding, sideWalkAmount, 16.0F, runnable);
      }
   }

   private SideNudgeSchematic(Screen screen, AutoBuilder autoBuilder) {
      super(screen, autoBuilder.schematicBuilder, "Auto Builder");
      this.back = this::updateMatrixStack;
      this.start = this::updateSchematicBuilder;
      this.stop = this::updateAutoBuilder;
      this.paste = this::updateButtonReady4;
      this.folder = () -> this.update();
      this.sideRefillAction = this::updateBase;
      this.refillSideAction = this::updateBaseBooleanBase;
      this.sideBuyAction = this::updateButtonReady3;
      this.mapPin = this::updateButtonReadyBuildState;
      this.crosshair = this::updateButtonReady;
      this.viewGiveAction = this::updateStateBuildingBuildState;
      this.giveViewAction = this::updateStateBuilding;
      this.cancel = this::updateBuilderButton;
      this.sideLookIntConsumer = this::handleEnumReady;
      this.intConsumer = this::handleEnumReadyForInt;
      this.buildState = SideNudgeSchematic.State.NONE;
      this.thumbsValue = 1000000000L;
      this.showOriginValue = -1;
      this.autoBuilder = autoBuilder;
      this.buttonReadyButtonBase = this.getAutoBuilder(ButtonBase.class, "Origin: Player");
      this.autoBuilderButtonReady = this.getAutoBuilder(ButtonBase.class, "Origin: Looking");
      this.autoBuilderBuilding = this.getAutoBuilder(ButtonBase.class, "Show Materials");
      this.autoBuilderViewGive = this.getAutoBuilder(ButtonBase.class, "Creative Materials Get");
      this.autoBuilderFolderDone = this.getAutoBuilder(ButtonBase.class, "Paste Schematic");
      this.buyMaterialsButton = this.getAutoBuilder(ButtonBase.class, "Buy Materials");
      this.autoBuilderBuilderButton = this.getAutoBuilder(ButtonBase.class, "Cancel Preview");
      this.sideNudgeButtonBase = this.getAutoBuilder(ButtonBase.class, "Nudge X+");
      this.nudgeXMinusButton = this.getAutoBuilder(ButtonBase.class, "Nudge X-");
      this.nudgeYPlusButton = this.getAutoBuilder(ButtonBase.class, "Nudge Y+");
      this.nudgeYMinusButton = this.getAutoBuilder(ButtonBase.class, "Nudge Y-");
      this.nudgeZPlusButton = this.getAutoBuilder(ButtonBase.class, "Nudge Z+");
      this.buttonBase = this.getAutoBuilder(ButtonBase.class, "Nudge Z-");
      this.autoBuilderEnumReady = this.getAutoBuilder(EnumSetting.class, "Rotation");
      this.enumHelper = this.getAutoBuilder(EnumSetting.class, "Mirror");
      this.autoBuilderSideRefill = this.getAutoBuilder(BooleanSetting.class, "Auto Move");
      this.booleanBase = this.getAutoBuilder(BooleanSetting.class, "Restock When Empty");
      this.actions = new Runnable[]{
         () -> handle(this.nudgeXMinusButton),
         () -> handle(this.sideNudgeButtonBase),
         () -> handle(this.nudgeYMinusButton),
         () -> handle(this.nudgeYPlusButton),
         () -> handle(this.buttonBase),
         () -> handle(this.nudgeZPlusButton)
      };
   }

   private void updateSchematicBuilder() {
      if (this.buildState != SideNudgeSchematic.State.READY) {
         String schematicBuilderText = this.buildState == SideNudgeSchematic.State.LOADING ? "Wait for the schematic to load" : "Pick a schematic first";
         DisabledEnabled.getInstance().handleDisabledEnabled("Auto Builder", schematicBuilderText, false);
      } else {
         if (!this.autoBuilder.isEnabled()) {
            this.autoBuilder.setEnabled(true);
         }

         this.schematicBuilderFlag = true;
         this.updateRailPreview();
      }
   }

   private Map<String, List<ConfigSetting>> getBuildingSchematic() {
      var linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("Building", new ArrayList());

      for (ConfigSetting autoEspBase : this.autoBuilder.getSettings()) {
         if (!autoEspBase.isHiddenFromList() && !CANCEL_PREVIEW_SET.contains(autoEspBase.getSettingName())) {
            String buildingSchematicText = autoEspBase.getGroupName();
            ((java.util.List) linkedHashMap.computeIfAbsent(buildingSchematicText != null && !"Schematic".equals(buildingSchematicText) ? buildingSchematicText : "Building", obj -> new ArrayList<>())).add(autoEspBase);
         }
      }

      linkedHashMap.values().removeIf(obj2 -> ((List) obj2).isEmpty());
      return linkedHashMap;
   }

   @Override
   public String formatShowOrigin() {
      int showOriginValue = this.getSchematicsTexts();
      if (this.settingsMode || !this.canFlag(showOriginValue)) {
         return null;
      } else if (showFlag) {
         return "Show less";
      } else {
         if (showOriginValue != this.showOriginValue) {
            this.showOriginValue = showOriginValue;
            this.showText = "Show all " + showOriginValue;
         }

         return this.showText;
      }
   }

   @Override
   public void handleMatrixParse(ButtonMin buttonMin, double matrixParseValue, double minmaxValue, double orbitValue, double rotateQuaternionfValue, int button) {
      if (this.settingsMode) {
         if (this.matrixStackHelper != null) {
            this.matrixStackHelper.handleForDoubleInt(matrixParseValue, minmaxValue, button);
         }
      } else {
         super.handleMatrixParse(buttonMin, matrixParseValue, minmaxValue, orbitValue, rotateQuaternionfValue, button);
      }
   }

   @Override
   public String formatSchematicsText() {
      if (this.settingsMode) {
         return "More settings  \u00b7  restock, buying, preview, safety and team";
      } else {
         String gpuMeshText = this.getGpuMeshText();
         int schematicsValue = this.buildState == SideNudgeSchematic.State.BUILDING ? CardStatusUtilsActive.getValue() : -1;
         String schematicsPickText = this.buildState + "|" + gpuMeshText + "|" + schematicsValue;
         if (!schematicsPickText.equals(this.schematicsPickTextSchematics)) {
            this.schematicsPickTextSchematics = schematicsPickText;
            String formatText = formatTextForString(gpuMeshText);

            this.schematicsPickText = switch (this.buildState) {
               case EMPTY -> "No schematics yet";
               case NONE -> "Pick a schematic on the left";
               case LOADING -> "Loading " + formatText + "...";
               case FAILED -> formatText + " couldn't be loaded";
               case READY -> formatText + "  \u00b7  place it, then press Start";
               case BUILDING -> "Building " + formatText + "  \u00b7  " + schematicsValue + "%";
            };
         }

         return this.schematicsPickText;
      }
   }

   @Override
   public void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
      if (!this.settingsMode) {
         super.renderCubeAmountForDrawContextButtonMin(context, buttonMin);
      }
   }

   @Override
   public void updateShowFlag() {
      showFlag = !showFlag;
      this.updateSchematicsSelected();
   }

   private void updateButtonReady4() {
      if (this.buildState == SideNudgeSchematic.State.READY) {
         handle(this.autoBuilderFolderDone);
      }
   }

   @Override
   public void handleThumbsAmount(float thumbsValue) {
      super.handleThumbsAmount(thumbsValue);
      this.thumbsAmount = AmountPreviewUtils.getAmount(this.thumbsAmount, this.scrollOffset, 18.0F, thumbsValue, 0.05F);
      if (!this.isSchematicsTexts()) {
         this.buildState = SideNudgeSchematic.State.EMPTY;
      } else if (this.autoBuilder.isEnabled()) {
         this.buildState = SideNudgeSchematic.State.BUILDING;
      } else if (this.getGpuMeshText().isEmpty()) {
         this.buildState = SideNudgeSchematic.State.NONE;
      } else if (this.isMeshGpu()) {
         this.buildState = SideNudgeSchematic.State.FAILED;
      } else if (this.isMeshGpuSchematicBuilder()) {
         this.buildState = SideNudgeSchematic.State.READY;
      } else {
         this.buildState = SideNudgeSchematic.State.LOADING;
      }

      boolean thumbsFlag = this.buildState == SideNudgeSchematic.State.READY || this.buildState == SideNudgeSchematic.State.BUILDING;
      ElseBuilder object = thumbsFlag ? this.autoBuilder.schematicBuilder.getLoadedSchematic() : null;
      long nanoTime = System.nanoTime();
      if (object != this.thumbsObject || thumbsFlag && nanoTime - this.lastNanoTime > this.thumbsValue) {
         this.thumbsObject = object;

         try {
            this.typesTotal = thumbsFlag ? this.autoBuilder.getMaterialTypesSummary() : null;
         } catch (Throwable e) {
            this.typesTotal = null;
         }

         this.lastNanoTime = System.nanoTime();
         this.thumbsValue = Math.max(1000000000L, (this.lastNanoTime - nanoTime) * 50L);
      }
   }

   @Override
   public void renderSkippedLim(DrawContext context, ScannedSkipped scannedSkipped) {
      this.skippedLimAmount = scannedSkipped.skippedLimAmount;
      float max = Math.max(0.0F, this.contentHeight - scannedSkipped.skippedLimAmount);
      if (this.scrollOffset > max) {
         this.scrollOffset = max;
      }

      float skippedLimValue = scannedSkipped.skippedScannedAmount - MatrixStackUtilsAmount.getAmount(this.thumbsAmount);
      MatrixStackUtilsRelease.renderScissorEnable(
         context, scannedSkipped.sideBuildingAmount - 2.0F, scannedSkipped.skippedScannedAmount, scannedSkipped.sideWalkAmount + 4.0F, scannedSkipped.skippedLimAmount
      );

      float sideRefill2;
      try {
         float skippedLimValue3 = this.buildState == SideNudgeSchematic.State.BUILDING
            ? this.getBlocksText(context, scannedSkipped, skippedLimValue)
            : this.getSideLook(context, scannedSkipped, skippedLimValue, this.buildState == SideNudgeSchematic.State.READY);
         float viewGive = this.getViewGive(context, scannedSkipped, skippedLimValue3 + 12.0F);
         sideRefill2 = this.getSideRefill(context, scannedSkipped, viewGive + 12.0F);
      } finally {
         context.disableScissor();
      }

      this.contentHeight = sideRefill2 + MatrixStackUtilsAmount.getAmount(this.thumbsAmount) - scannedSkipped.skippedScannedAmount;
   }

   private <T extends ConfigSetting> T getAutoBuilder(Class<T> class2, String autoBuilderText) {
      for (ConfigSetting autoEspBase : this.autoBuilder.getSettings()) {
         if (class2.isInstance(autoEspBase) && autoBuilderText.equals(autoEspBase.getSettingName())) {
            return (T)class2.cast(autoEspBase);
         }
      }

      return null;
   }

   private void updateStateBuildingBuildState() {
      if (this.buildState == SideNudgeSchematic.State.READY || this.buildState == SideNudgeSchematic.State.BUILDING) {
         handle(this.autoBuilderBuilding);
      }
   }

   @Override
   public boolean isShowFlag() {
      return showFlag;
   }

   @Override
   public float getMax() {
      return 134.0F;
   }

   @Override
   public boolean canMaxSubstring(int substringValue, int modifiers) {
      if (this.settingsMode) {
         return this.matrixStackHelper != null && this.matrixStackHelper.canGlfwListening(substringValue, modifiers);
      } else {
         int schematicsTexts = this.getSchematicsTexts();
         if (schematicsTexts >= 2 && (substringValue == 262 || substringValue == 263 || substringValue == 264 || substringValue == 265 || substringValue == 268 || substringValue == 269)) {
            if (this.isClericLimits()) {
               return true;
            } else {
               int max3 = Math.max(0, this.getSelectedSchematicsValue());

               int substringLimit = switch (substringValue) {
                  case 262, 264 -> Math.floorMod(max3 + 1, schematicsTexts);
                  case 263, 265 -> Math.floorMod(max3 - 1, schematicsTexts);
                  default -> schematicsTexts - 1;
                  case 268 -> 0;
               };
               if (substringLimit >= 8) {
                  showFlag = true;
               }

               this.handleSchematicsSelected(substringLimit);
               return true;
            }
         } else {
            return super.canMaxSubstring(substringValue, modifiers);
         }
      }
   }

   private static enum State {
      EMPTY,
      NONE,
      LOADING,
      FAILED,
      READY,
      BUILDING;
   }
}
