package com.corz.client.utils;

import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ButtonMin;
import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.gui.PreviewText;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.TextureImageUtils;
import java.util.List;
import java.util.Locale;
import java.util.function.IntConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import com.corz.client.categories.misc.CustomCrosshair;

public class CrosshairPreview extends GuiClickScreen {
   private int previewCrosshairValue;
   private final Runnable dynamicZoomAction;
   private float currentZoomScale;
   private final Runnable dynamicGlowAction;
   private final Runnable roundedOutlineAction;
   private int previewCrosshairValueThumbs = Integer.MIN_VALUE;
   private float thumbsAmount;
   private final float[] cubeSchematicTable;
   private float targetZoomScale;
   private float thumbsCount;
   private final IntConsumer intConsumer;
   private final IntConsumer folderDoneIntConsumer;
   private final IntConsumer crosshairPreviewIntConsumer;
   private int previewCrosshairValuePreview;
   private final List<String> enumSchematics;
   private final Runnable glowRoundedAction;
   private final IntConsumer backdrop;
   private final CustomCrosshair dynamic;
   private String format;
   private final int baseScaleValue;
   private int backdropDynamicValuePreviewC = -1;
   private long lastNanoTime;
   private final Runnable zoom;
   private static int backdropDynamicValue = 0;
   private String schematicsText;

   @Override
   public void handleFolderDone(PreviewText previewText) {
      previewText.getClassForStringInt("size", "Size", this.dynamic.getSizeInteger().getValue(), this.crosshairPreviewIntConsumer)
         .getClassForStringInt("gap", "Gap", this.dynamic.getIntegerHelper().getValue(), this.folderDoneIntConsumer)
         .getClassForStringInt("thickness", "Thickness", this.dynamic.getThicknessInteger().getValue(), this.intConsumer)
         .getClassForStringFlag("outline", "Outline", this.dynamic.getOutlineColor().isEnabled(), this.roundedOutlineAction)
         .getClassForStringFlag("rounded", "Rounded", this.dynamic.getRoundedColor().isEnabled(), this.glowRoundedAction)
         .getClassForStringFlag("glow", "Glow", this.dynamic.getGlowColor().isEnabled(), this.dynamicGlowAction)
         .getClassForStringFlag("dynamic", "Dynamic", this.dynamic.getBooleanBase().isEnabled(), this.dynamicZoomAction)
         .getFlag()
         .getClassForStringRunnable("zoom", this.formatPreviewCrosshair(), this.zoom)
         .getClassForStringIntArray("backdrop", GrassDarkUtils.backdropZoomValues, backdropDynamicValue, this.backdrop);
   }

   private float getFactorScale() {
      int maxScaleFactor = Math.max(1, MinecraftClient.getInstance().getWindow().getScaleFactor());
      return (float)this.baseScaleValue / maxScaleFactor;
   }

   @Override
   public float getAmount() {
      return 308.0F;
   }

   @Override
   public void handleThumbsAmount(float thumbsValue) {
      this.thumbsCount += thumbsValue;
      int enumHelper = this.dynamic.getEnumHelper();
      if (enumHelper != this.previewRail.getFadeValue() && enumHelper >= 0 && enumHelper < this.enumSchematics.size()) {
         this.previewRail.handleCrossfadeFlag(enumHelper, true);
      }

      int previewCrosshairValue = this.dynamic.getIntegerBase();
      if (previewCrosshairValue != this.previewCrosshairValueThumbs) {
         this.previewCrosshairValueThumbs = previewCrosshairValue;

         for (int i = 0; i < this.enumSchematics.size(); i++) {
            this.cubeSchematicTable[i] = this.dynamic.getMax(this.enumSchematics.get(i));
         }
      }

      this.thumbsAmount = AmountPreviewUtils.getAmount(this.thumbsAmount, this.dynamic.getBooleanBase().isEnabled() ? 1.0F : 0.0F, 8.0F, thumbsValue);
   }

   private static void handle(BooleanSetting booleanBase) {
      booleanBase.toggle();
   }

   @Override
   public void renderCubeSchematic(DrawContext context, ButtonMin buttonMin) {
      int index = buttonMin.selectedIndex;
      if (index >= 0 && index < this.enumSchematics.size()) {
         float cubeSchematicFactor = Math.max(0.05F, this.currentZoomScale) * this.getFactorScale();
         float cubeSchematicValue = this.thumbsAmount > 0.001F ? this.dynamic.getInteger(this.getAmountThumbsCount() * this.thumbsAmount) * cubeSchematicFactor : 0.0F;
         this.handleDynamic(this.enumSchematics.get(index), buttonMin.getCubeAmount(), buttonMin.getCubeSchematic(), cubeSchematicFactor, cubeSchematicValue);
      }
   }

   @Override
   public String formatSchematicsText() {
      int previewCrosshairValue = this.previewRail.getFadeValue();
      if (previewCrosshairValue != this.previewCrosshairValue) {
         this.previewCrosshairValue = previewCrosshairValue;
         this.schematicsText = previewCrosshairValue >= 0 && previewCrosshairValue < this.enumSchematics.size() ? this.enumSchematics.get(previewCrosshairValue) + "  \u00b7  " + (previewCrosshairValue + 1) + " of " + this.enumSchematics.size() : "";
      }

      return this.schematicsText;
   }

   private String formatPreviewCrosshair() {
      int previewCrosshairValue = Math.round(Math.max(0.0F, this.currentZoomScale) * 10.0F);
      if (previewCrosshairValue != this.previewCrosshairValuePreview) {
         this.previewCrosshairValuePreview = previewCrosshairValue;
         this.format = previewCrosshairValue <= 10 ? "1:1" : String.format(Locale.ROOT, "%.1f\u00d7", previewCrosshairValue / 10.0F);
      }

      return this.format;
   }

   @Override
   public void renderThumbsInspects(DrawContext context, int i, float thumbsInspectsValue, float configsLikeValue, float backdropZoomDivisor) {
      this.renderPreviewCrosshair(context, thumbsInspectsValue, configsLikeValue, backdropZoomDivisor, backdropZoomDivisor, 5.0F);
      float dynamicFactor = Math.min(1.5F, backdropZoomDivisor * 0.36F / Math.max(1.0F, this.cubeSchematicTable[i]));
      this.handleDynamic(this.enumSchematics.get(i), thumbsInspectsValue + backdropZoomDivisor / 2.0F, configsLikeValue + backdropZoomDivisor / 2.0F, dynamicFactor, 0.0F);
   }

   private void renderPreviewCrosshair(DrawContext context, float previewCrosshairValue, float configsLikeValue, float materialFactor, float backdropZoomDivisor, float corzLicenseLimit) {
      float previewCrosshairLimit = this.backdropDynamicValuePreviewC < 0 ? 1.0F : (float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F / 240.0F;
      if (previewCrosshairLimit >= 1.0F) {
         this.backdropDynamicValuePreviewC = -1;
      }

      if (this.backdropDynamicValuePreviewC < 0) {
         TextureImageUtils.renderBackdropZoom(context, backdropDynamicValue, previewCrosshairValue, configsLikeValue, materialFactor, backdropZoomDivisor, corzLicenseLimit);
      } else {
         TextureImageUtils.renderBackdropZoom(context, this.backdropDynamicValuePreviewC, previewCrosshairValue, configsLikeValue, materialFactor, backdropZoomDivisor, corzLicenseLimit);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(AmountPreviewUtils.getAmount8(previewCrosshairLimit));

         try {
            TextureImageUtils.renderBackdropZoom(context, backdropDynamicValue, previewCrosshairValue, configsLikeValue, materialFactor, backdropZoomDivisor, corzLicenseLimit);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   @Override
   public void renderCubeAmountForDrawContextButtonMin(DrawContext context, ButtonMin buttonMin) {
      int cubeValue = Math.max(0, this.previewRail.getFadeValue());
      float cubeSchematic = this.getCubeSchematic(cubeValue, buttonMin);
      if (this.targetZoomScale < 0.0F) {
         this.targetZoomScale = Math.min(4.0F, cubeSchematic);
      }

      float thumbsLimit = Math.max(1.0F, Math.min(this.targetZoomScale, cubeSchematic));
      this.currentZoomScale = this.currentZoomScale < 0.0F ? thumbsLimit : AmountPreviewUtils.getAmount(this.currentZoomScale, thumbsLimit, 14.0F, buttonMin.frameDelta, 0.002F);
      this.renderPreviewCrosshair(context, buttonMin.cubeKnifeAmount, buttonMin.cubeSchematicAmount, buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube, buttonMin.renderAmount);
   }

   @Override
   public void handleInspectsSchematics(int inspectsSchematicsValue) {
      this.dynamic.handleEnum(inspectsSchematicsValue);
   }

   @Override
   public boolean canAmountMatrix(ButtonMin buttonMin, double matrixValue, double matrixValue2, double exponent) {
      if (this.targetZoomScale < 0.0F) {
         return true;
      } else {
         float cubeSchematic = this.getCubeSchematic(Math.max(0, this.previewRail.getFadeValue()), buttonMin);
         float matrixFactor = Math.min(this.targetZoomScale, cubeSchematic);
         this.targetZoomScale = Math.max(1.0F, Math.min(8.0F, matrixFactor * (float)Math.pow(1.2F, exponent)));
         return true;
      }
   }

   @Override
   public int getShowSchematics() {
      return this.enumSchematics.size();
   }

   private void handleBackdropDynamic(int backdropDynamicValue2) {
      if (backdropDynamicValue2 >= 0 && backdropDynamicValue2 < 4 && backdropDynamicValue2 != backdropDynamicValue) {
         this.backdropDynamicValuePreviewC = backdropDynamicValue;
         backdropDynamicValue = backdropDynamicValue2;
         this.lastNanoTime = System.nanoTime();
      }
   }

   private float getCubeSchematic(int cubeValue, ButtonMin buttonMin) {
      if (cubeValue >= 0 && cubeValue < this.cubeSchematicTable.length && !(buttonMin.cubeSchematicAmount2Cube <= 0.0F) && !(buttonMin.cubeSchematicAmount3Cube <= 0.0F)) {
         float cubeSchematicValue = Math.min(buttonMin.cubeSchematicAmount2Cube, buttonMin.cubeSchematicAmount3Cube) * 0.4F;
         float cubeSchematicBound = cubeSchematicValue / (Math.max(1.0F, this.cubeSchematicTable[cubeValue]) * this.getFactorScale());
         return Math.max(1.0F, Math.min(8.0F, cubeSchematicBound));
      } else {
         return 1.0F;
      }
   }

   @Override
   public float getSchematicMaterialsAmount() {
      return 460.0F;
   }

   private static void handle(IntegerSetting integerHelper, int crosshairPreviewFactor) {
      int crosshairPreviewBound = integerHelper.getValue() + crosshairPreviewFactor * Math.max(1, integerHelper.getStepSize());
      integerHelper.handleMax(Math.max(integerHelper.getMinValue(), Math.min(integerHelper.getMaxValue(), crosshairPreviewBound)));
   }

   private float getAmountThumbsCount() {
      float crosshairPreviewValue = this.thumbsCount % 3.2F;
      if (crosshairPreviewValue < 0.28F) {
         return AmountPreviewUtils.getAmount6(crosshairPreviewValue / 0.28F);
      } else if (crosshairPreviewValue < 1.5F) {
         return 1.0F;
      } else {
         return crosshairPreviewValue < 2.1F ? 1.0F - AmountPreviewUtils.getAmount8((crosshairPreviewValue - 1.5F) / 0.6F) : 0.0F;
      }
   }

   @Override
   public String formatTextsInspects(int i) {
      return this.enumSchematics.get(i);
   }

   private void updateMin() {
      if (!(this.targetZoomScale < 0.0F)) {
         ButtonMin buttonMin = this.previewRail.getButtonMin();
         float cubeSchematic = this.getCubeSchematic(Math.max(0, this.previewRail.getFadeValue()), buttonMin);
         this.targetZoomScale = Math.min(this.targetZoomScale, cubeSchematic) > 1.05F ? 1.0F : Math.min(4.0F, cubeSchematic);
      }
   }

   @Override
   public float getMax() {
      return 122.0F;
   }

   public CrosshairPreview(Screen screen, CustomCrosshair dynamic) {
      super(screen, "Crosshair Preview");
      this.previewCrosshairValue = -1;
      this.previewCrosshairValuePreview = Integer.MIN_VALUE;
      this.targetZoomScale = -1.0F;
      this.currentZoomScale = -1.0F;
      this.format = "";
      this.dynamic = dynamic;
      this.enumSchematics = dynamic.getEnum();
      this.cubeSchematicTable = new float[this.enumSchematics.size()];
      MinecraftClient instance = MinecraftClient.getInstance();
      this.baseScaleValue = MaxFlag.getValue(
         instance.options.getGuiScale().getValue(), instance.getWindow().getFramebufferWidth(), instance.getWindow().getFramebufferHeight(), instance.forcesUnicodeFont()
      );
      this.crosshairPreviewIntConsumer = crosshairPreviewValue -> handle(dynamic.getSizeInteger(), crosshairPreviewValue);
      this.folderDoneIntConsumer = crosshairPreviewFactor -> handle(dynamic.getIntegerHelper(), crosshairPreviewFactor);
      this.intConsumer = modulex -> handle(dynamic.getThicknessInteger(), modulex);
      this.roundedOutlineAction = () -> handle(dynamic.getOutlineColor());
      this.glowRoundedAction = () -> handle(dynamic.getRoundedColor());
      this.dynamicGlowAction = () -> handle(dynamic.getGlowColor());
      this.dynamicZoomAction = () -> handle(dynamic.getBooleanBase());
      this.zoom = this::updateMin;
      this.backdrop = this::handleBackdropDynamic;
      this.previewRail.handleCrossfadeFlag(dynamic.getEnumHelper(), false);
      TextureImageUtils.updateFuturesCompletable();
   }

   private void handleDynamic(String dynamicText, float dynamicValue, float peekPositionFactor, float dynamicFactor, float cubeSchematicValue) {
      float dynamicDivisor = MatrixPeekUtilsRender.getAmount();
      float mFloat = MatrixStackUtilsRelease.getMFloat();
      float colorValue = Math.round(MatrixStackUtilsRelease.getPeekPositionForFloat(dynamicValue) / dynamicDivisor) * dynamicDivisor;
      float scaledHeightScaled = Math.round(MatrixStackUtilsRelease.getPeekPosition(peekPositionFactor) / dynamicDivisor) * dynamicDivisor;
      this.dynamic.handleBaseColor(dynamicText, colorValue, scaledHeightScaled, dynamicFactor * mFloat, cubeSchematicValue * mFloat);
   }

   @Override
   public boolean canMaxSubstring(int substringLimit, int substringValue) {
      if (substringLimit >= 49 && substringLimit < 53) {
         this.handleBackdropDynamic(substringLimit - 49);
         return true;
      } else if (substringLimit == 48) {
         this.updateMin();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isFlag() {
      return true;
   }
}
