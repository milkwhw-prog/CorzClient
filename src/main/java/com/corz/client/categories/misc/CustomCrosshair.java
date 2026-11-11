package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.TickProgress;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.movement.Freecam;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.CrosshairPreview;
import com.corz.client.utils.AmountHandle;
import com.corz.client.utils.IconsPngType;

public class CustomCrosshair extends Module {
   private final EnumSetting enumHelper;
   private static final String[] styleCrossTexts = new String[]{
      "Cross", "Dot", "Cross + Dot", "Ring", "Ring + Dot", "Halo", "X", "T", "Brackets", "Arcs", "Tri", "Diamond", "Box", "Scope", "Chevron"
   };
   private final BooleanSetting booleanBase;
   private final BooleanSetting outlineColor;
   private final BooleanSetting glowColor;
   private final IntegerSetting integerHelper;
   private final ColorSetting colorBase;
   private final IntegerSetting thicknessInteger;
   private final IntegerSetting sizeInteger;
   private float smoothedOffset;
   private final ButtonBase buttonBase;
   private final BooleanSetting roundedColor;
   private long lastNanoTime;

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (mc.player != null) {
         if (mc.currentScreen == null) {
            if (!mc.options.hudHidden) {
               if (mc.options.getPerspective().isFirstPerson() || Freecam.isBaseInstance()) {
                  if (!mc.getDebugHud().shouldShowDebugHud()) {
                     float scaledWidthScaled = mc.getWindow().getScaledWidth() / 2.0F;
                     float scaledHeightScaled = mc.getWindow().getScaledHeight() / 2.0F;
                     this.handleEnum(scaledWidthScaled, scaledHeightScaled, 1.0F, this.getAmountTime());
                  }
               }
            }
         }
      }
   }

   public IntegerSetting getSizeInteger() {
      return this.sizeInteger;
   }

   public void handleBaseColor(String colorText, float colorValue, float scaledHeightScaled, float dotRingFactor, float dotRingValue) {
      AmountHandle amountHandle = new AmountHandle(
         colorValue,
         scaledHeightScaled,
         this.colorBase.getColor(),
         this.outlineColor.isEnabled(),
         this.glowColor.isEnabled(),
         this.roundedColor.isEnabled(),
         false
      );
      this.handleDotRing(amountHandle, colorText, dotRingFactor, dotRingValue);
      amountHandle.updateAmountPanel();
   }

   private void handleDotRing(AmountHandle amountHandle, String dotRingText, float dotRingFactor, float dotRingValue) {
      float dotRingValue2 = this.sizeInteger.getValue() * dotRingFactor;
      float customCrosshairValue = this.integerHelper.getValue() * dotRingFactor + dotRingValue;
      float dotRingBound = this.thicknessInteger.getValue() * dotRingFactor;
      float strokeWidth = Math.max(1.0F, dotRingBound * 0.5F);
      float max5 = Math.max(1.0F, dotRingBound * 0.6F);
      boolean booleanValue = this.roundedColor.isEnabled();
      String dotRingText2 = dotRingText == null ? "" : dotRingText;
      switch (dotRingText2) {
         case "Dot":
            amountHandle.handleAmount(Math.max(1.0F, dotRingBound * 0.8F));
            break;
         case "Cross + Dot":
            handle(amountHandle, customCrosshairValue, dotRingValue2, dotRingBound);
            amountHandle.handleAmount(Math.max(1.0F, dotRingBound * 0.7F));
            break;
         case "Ring":
            amountHandle.handle(customCrosshairValue + dotRingValue2 * 0.7F, strokeWidth);
            break;
         case "Ring + Dot":
            amountHandle.handle(customCrosshairValue + dotRingValue2 * 0.7F, strokeWidth);
            amountHandle.handleAmount(max5);
            break;
         case "Halo":
            float dotRingValue6 = customCrosshairValue + dotRingValue2 * 0.6F;
            amountHandle.handle(dotRingValue6, strokeWidth);
            float dotRingValue7 = dotRingValue6 + Math.max(1.0F, dotRingBound * 0.75F);

            for (int i = 0; i < 4; i++) {
               amountHandle.handleAmountFlag(i * 90.0F, dotRingValue7, dotRingValue7 + dotRingValue2 * 0.6F, dotRingBound);
            }
            break;
         case "X":
            for (int j = 0; j < 4; j++) {
               amountHandle.handleAmountFlag(45.0F + j * 90.0F, customCrosshairValue, customCrosshairValue + dotRingValue2, dotRingBound);
            }
            break;
         case "T":
            amountHandle.handleAmountFlag(0.0F, customCrosshairValue, customCrosshairValue + dotRingValue2, dotRingBound);
            amountHandle.handleAmountFlag(90.0F, customCrosshairValue, customCrosshairValue + dotRingValue2, dotRingBound);
            amountHandle.handleAmountFlag(180.0F, customCrosshairValue, customCrosshairValue + dotRingValue2, dotRingBound);
            break;
         case "Brackets":
            handleForAmountHandleFloat(amountHandle, customCrosshairValue + dotRingValue2, Math.max(dotRingBound, dotRingValue2 * 0.7F), dotRingBound);
            amountHandle.handleAmount(max5);
            break;
         case "Arcs":
            float dotRingValue8 = customCrosshairValue + dotRingValue2 * 0.7F;

            for (int k = 0; k < 4; k++) {
               amountHandle.handleAmountForFloat(dotRingValue8, strokeWidth, 45.0F + k * 90.0F, 50.0F);
            }

            amountHandle.handleAmount(max5);
            break;
         case "Tri":
            float handleFactor = customCrosshairValue + dotRingValue2 * 0.6F;
            float handleValue = Math.max(1.5F, dotRingValue2 * 0.4F);
            amountHandle.handleAmount(-90.0F, handleFactor, handleValue);
            amountHandle.handleAmount(30.0F, handleFactor, handleValue);
            amountHandle.handleAmount(150.0F, handleFactor, handleValue);
            break;
         case "Diamond":
            amountHandle.handleAmount(customCrosshairValue + dotRingValue2 * 0.55F, strokeWidth, 45.0F, booleanValue ? strokeWidth : 0.0F);
            amountHandle.handleAmount(max5);
            break;
         case "Box":
            amountHandle.handleAmount(customCrosshairValue + dotRingValue2 * 0.5F, strokeWidth, 0.0F, booleanValue ? strokeWidth * 1.5F : 0.0F);
            amountHandle.handleAmount(max5);
            break;
         case "Scope":
            float dotRingValue11 = customCrosshairValue + dotRingValue2 * 0.8F;
            amountHandle.handle(dotRingValue11, Math.max(1.0F, dotRingBound * 0.4F));
            float dotRingValue12 = dotRingValue11 + Math.max(1.0F, dotRingBound * 0.5F);
            float max13 = Math.max(0.5F, dotRingBound * 0.35F);

            for (int m = 0; m < 4; m++) {
               amountHandle.handleAmountFlag(m * 90.0F, dotRingValue12, dotRingValue12 + dotRingValue2 * 1.4F, max13);
            }

            amountHandle.handleAmount(Math.max(1.0F, dotRingBound * 0.5F));
            break;
         case "Chevron":
            float dotRingValue14 = customCrosshairValue + dotRingBound * 0.5F;
            float dotRingValue15 = dotRingValue2 * 0.9F;
            amountHandle.handleAmountFlag(0.0F, dotRingValue14, -dotRingValue15, dotRingValue14 + dotRingValue15, dotRingBound);
            amountHandle.handleAmountFlag(0.0F, dotRingValue14, dotRingValue15, dotRingValue14 + dotRingValue15, dotRingBound);
            break;
         default:
            handle(amountHandle, customCrosshairValue, dotRingValue2, dotRingBound);
      }
   }

   public BooleanSetting getRoundedColor() {
      return this.roundedColor;
   }

   private float getAmountTime() {
      long nanoTime = System.nanoTime();
      float customCrosshairValue = this.lastNanoTime == 0L ? 0.016F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
      this.lastNanoTime = nanoTime;
      if (!this.booleanBase.isEnabled()) {
         this.smoothedOffset = 0.0F;
         return 0.0F;
      } else {
         Vec3d velocity = mc.player.getVelocity();
         double hypot = Math.hypot(velocity.x, velocity.z);
         float customCrosshairBound = hypot > 0.03 ? (float)Math.min(1.0, hypot / 0.26) : 0.0F;
         if (!mc.player.isOnGround()) {
            customCrosshairBound = Math.max(customCrosshairBound, 0.7F);
         }

         this.smoothedOffset = this.smoothedOffset + (customCrosshairBound - this.smoothedOffset) * (1.0F - (float)Math.exp(-customCrosshairValue * 14.0F));
         if (Math.abs(this.smoothedOffset) < 0.002F) {
            this.smoothedOffset = 0.0F;
         }

         return this.getInteger(this.smoothedOffset);
      }
   }

   public BooleanSetting getGlowColor() {
      return this.glowColor;
   }

   public List<String> getEnum() {
      return this.enumHelper.getOptions();
   }

   public void handleEnum(float enumValue, float scaledHeightScaled, float dotRingFactor, float dotRingValue) {
      this.handleBaseColor(this.enumHelper.getCurrentOption(), enumValue, scaledHeightScaled, dotRingFactor, dotRingValue);
   }

   public BooleanSetting getBooleanBase() {
      return this.booleanBase;
   }

   public static boolean isFlag() {
      CustomCrosshair customCrosshair = (CustomCrosshair)ModuleRegistry.getModule(CustomCrosshair.class);
      return customCrosshair != null && customCrosshair.isEnabled();
   }

   public IntegerSetting getThicknessInteger() {
      return this.thicknessInteger;
   }

   public float getInteger(float integerFactor) {
      return integerFactor * (2.0F + this.sizeInteger.getValue() * 0.5F);
   }

   public IntegerSetting getIntegerHelper() {
      return this.integerHelper;
   }

   public CustomCrosshair() {
      super("Custom Crosshair", IconsPngType.MISC);
      this.enumHelper = new EnumSetting("Style", "Crosshair style", "Cross", styleCrossTexts);
      this.sizeInteger = new IntegerSetting("Size", "Length of each arm / reach of the reticle", 5, 1, 20);
      this.integerHelper = new IntegerSetting("Gap", "Gap from the centre", 2, 0, 10);
      this.thicknessInteger = new IntegerSetting("Thickness", "Line thickness", 2, 1, 6);
      this.colorBase = new ColorSetting("Color", "Crosshair colour", -1);
      this.outlineColor = new BooleanSetting("Outline", "Hairline contrast outline (auto black or white) so the reticle reads on any background", true);
      this.roundedColor = new BooleanSetting("Rounded", "Round the ends of the arms and the corners of frames", false);
      this.glowColor = new BooleanSetting("Glow", "Soft halo in the reticle colour", false);
      this.booleanBase = new BooleanSetting("Dynamic", "Spread the reticle while moving or airborne, like a native shooter crosshair", false);
      this.buttonBase = new ButtonBase(
         "Preview", "Open the live preview: pick a style and check it on dark, light, sky and grass backdrops", "Preview", this::updateScreenInstance
      );
      this.enumHelper.hideFromList();
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.sizeInteger,
            this.integerHelper,
            this.thicknessInteger,
            this.colorBase,
            this.outlineColor,
            this.roundedColor,
            this.glowColor,
            this.booleanBase,
            this.buttonBase
         }
      );
   }

   public BooleanSetting getOutlineColor() {
      return this.outlineColor;
   }

   private static void handle(AmountHandle amountHandle, float customCrosshairValue, float customCrosshairValue2, float dotRingBound) {
      for (int i = 0; i < 4; i++) {
         amountHandle.handleAmountFlag(i * 90.0F, customCrosshairValue, customCrosshairValue + customCrosshairValue2, dotRingBound);
      }
   }

   public void handleEnum(int enumLimit) {
      if (enumLimit >= 0 && enumLimit < this.enumHelper.getOptions().size()) {
         this.enumHelper.handle(enumLimit);
      }
   }

   public int getEnumHelper() {
      return this.enumHelper.getCurrentIndex();
   }

   private static void handleForAmountHandleFloat(AmountHandle amountHandle, float customCrosshairFactor, float customCrosshairValue, float dotRingBound) {
      float customCrosshairValue3 = dotRingBound / 2.0F;

      for (int i = -1; i <= 1; i += 2) {
         for (int j = -1; j <= 1; j += 2) {
            amountHandle.handleAmountFlagForFloat(i * (customCrosshairFactor - customCrosshairValue), j * customCrosshairFactor - customCrosshairValue3, i * customCrosshairFactor, j * customCrosshairFactor + customCrosshairValue3);
            amountHandle.handleAmountFlagForFloat(i * customCrosshairFactor - customCrosshairValue3, j * (customCrosshairFactor - customCrosshairValue), i * customCrosshairFactor + customCrosshairValue3, j * (customCrosshairFactor - customCrosshairValue3));
         }
      }
   }

   public float getMax(String customCrosshairText) {
      AmountHandle amountHandle = new AmountHandle(
         0.0F,
         0.0F,
         this.colorBase.getColor(),
         this.outlineColor.isEnabled(),
         this.glowColor.isEnabled(),
         this.roundedColor.isEnabled(),
         true
      );
      this.handleDotRing(amountHandle, customCrosshairText, 1.0F, 0.0F);
      return Math.max(1.0F, amountHandle.getMax());
   }

   private void updateScreenInstance() {
      MinecraftClient instance = MinecraftClient.getInstance();
      instance.setScreen(new CrosshairPreview(instance.currentScreen, this));
   }

   public int getIntegerBase() {
      int integerValue = (this.sizeInteger.getValue() * 31 + this.integerHelper.getValue()) * 31 + this.thicknessInteger.getValue();
      return integerValue * 8
         + (this.outlineColor.isEnabled() ? 4 : 0)
         + (this.glowColor.isEnabled() ? 2 : 0)
         + (this.roundedColor.isEnabled() ? 1 : 0);
   }
}
