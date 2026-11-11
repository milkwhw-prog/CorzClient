package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.NativeHud;
import com.corz.client.gui.NativeHud.Field;
import com.corz.client.gui.NativeHud.Pop;
import com.corz.client.gui.NativeHud.Pulse;
import com.corz.client.gui.NativeHud.Smooth;
import com.corz.client.gui.TwoDidUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.VertexTextureFont;
import com.corz.client.render.VertexTexturePeek;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpotifyHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.ElytraStatus;

public final class ElytraStatusCard {
   private final MatrixStack matrixStack = new MatrixStack();
   private static final String DECIMAL_SEPARATOR = ".";
   private static final String CHECKED = "Checked";
   private long nanoTimeSmooth;
   private float rocketsFinderAmount;
   private final Smooth smooth;
   private static final String KM_SUFFIX = " km";
   private static final String PERCENT_SUFFIX = "%";
   private float checkedPanelAnimation;
   private int statusCode;
   private int colorValue;
   private VertexTextureFont vertexTexture;
   private long smoothValue;
   private int elytraStatusValue;
   private final Smooth smoothSmooth;
   private final Smooth elytraStatusSmooth;
   private static final String ELYTRA_FINDER = "Elytra Finder";
   private int checkedRocketsValue;
   private static Identifier identifier;
   private final Field field;
   private final Field checkedRocketsField;
   private float smoothAmount;
   private static final String ELLIPSIS = "...";
   private static final String FOUND = " found";
   private float elytraStatusAmount;
   private final Field smoothField;
   private final Field elytraStatusField;
   private float checkedRocketsAmount;
   private static final String ELYTRA = "Elytra";
   private static final String TARGET = "Target";
   private long lastNanoTime;
   private final Smooth amountSmoother;
   private float displayProgress;
   private static final int topbarPerfValue = TwoDidUtils.getValue(0, 24);
   private static final String ROCKETS = "Rockets";
   private long animationTick;
   private final Smooth checkedSmoother;
   private static final String[] elytrafinderClockTexts = new String[]{"topbar", "perf", "clock"};
   private static final int UNUSED_CODE_ONE = 1;
   private float rocketsLabelWidth;
   private final Smooth statusSmoother;
   private int peakCheckedValue;
   private boolean checkedFlag;
   private final Smooth smoothAmountSmoother;
   private final Pop checkedRocketsPop = new Pop();
   private float targetProgress;
   private float colorAmount;
   private float revealAmount;
   private final Pulse pulse = new Pulse();
   private float checkedAmount;
   private final Field maxRocketsField;
   private final Smooth revealSmoother;
   private Color color;
   private long lastNanoTimeSmooth;
   public static final String ELYTRAFINDER = "elytrafinder";
   private String checkedText;
   private final Smooth checkedRocketsSmooth;
   private final Field statusDistanceField;
   private int blendedStatusColor;
   private static final String METERS_SUFFIX = " m";
   private final Field checkedTextField;

   private static long computeDurationNanos(double elytraStatusValue) {
      return elytraStatusValue < 999.5 ? Math.round(elytraStatusValue) : 1000000L + Math.round(elytraStatusValue / 100.0);
   }

   public void renderCard(DrawContext context, ElytraStatus elytraStatus) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (context != null && instance.player != null) {
         if (elytraStatus == null) {
            elytraStatus = ElytraStatus.INSTANCE;
         }

         CorzClient.fontManager.setMatrixStack(this.matrixStack);
         MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
         double scaleFactor = instance.getWindow().getScaleFactor();
         int calculateScaleFactor = instance.getWindow().calculateScaleFactor(ShowHUD.getMax(instance), instance.forcesUnicodeFont());
         float scaledWidthDivisor = (float)(calculateScaleFactor / scaleFactor);
         boolean scaledWidthFlag = Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F;
         int scaledWidth = scaledWidthFlag ? Math.round(instance.getWindow().getScaledWidth() / scaledWidthDivisor) : instance.getWindow().getScaledWidth();
         if (scaledWidthFlag) {
            CorzRoundedUtils.scaledWidth = scaledWidth;
            CorzRoundedUtils.scaledHeight = Math.round(instance.getWindow().getScaledHeight() / scaledWidthDivisor);
         }

         try {
            float scaledWidthValue = ShowHUD.getTime();
            if (scaledWidthValue < 0.004F) {
               return;
            }

            long nanoTime = System.nanoTime();
            float scaledWidthValue2 = this.lastNanoTime == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
            this.lastNanoTime = nanoTime;
            this.updateAnimations(elytraStatus, nanoTime, scaledWidthValue2);
            if (this.checkedRocketsPop.canTimeAmount(true, ++this.animationTick)) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledWidthValue);

               try {
                  this.renderCheckedRockets(context, scaledWidth, scaledWidthValue2, nanoTime);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               this.checkedFlag = false;
               return;
            }
         } finally {
            if (scaledWidthFlag) {
               CorzRoundedUtils.scaledWidth = 0.0F;
               CorzRoundedUtils.scaledHeight = 0.0F;
            }
         }
      }
   }

   private static String formatDistance(double elytraStatusValue) {
      if (elytraStatusValue < 999.5) {
         return Math.round(elytraStatusValue) + " m";
      } else {
         long round = Math.round(elytraStatusValue / 100.0);
         return round / 10L + "." + round % 10L + " km";
      }
   }

   private static float clampUnit(float elytraStatusBound) {
      return Math.max(150.0F, Math.min(220.0F, elytraStatusBound));
   }

   private static float measureTextWidth(String elytraStatusText, VertexTexturePeek vertexTexturePeek) {
      return vertexTexturePeek == null ? NativeHud.getAmount3(elytraStatusText, 0.9F) : vertexTexturePeek.getCorzclientAmounts(elytraStatusText, 12.0F);
   }

   private void renderCheckedRockets(DrawContext context, int checkedRocketsValue, float checkedRocketsValue2, long nanoTime) {
      VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
      if (vertexTexture != this.vertexTexture) {
         this.vertexTexture = vertexTexture;
         this.rocketsFinderAmount = NativeHud.getAmount3("Elytra Finder", 0.95F);
         this.rocketsLabelWidth = NativeHud.getAmount3("Rockets", 0.78F);
         this.checkedRocketsAmount = NativeHud.getAmount3("Checked", 0.78F);
      }

      if (this.checkedTextField.canMax(this.getCheckedText(vertexTexture))) {
         float elytraStatusLimit = 196.0F;
         this.checkedTextField.handleAmountTime(fitTextToWidth(CardStatusUtilsFlag.getOrDefault(this.checkedText), elytraStatusLimit, null), 0.9F, false, !this.checkedFlag);
      }

      float checkedRocketsValue5 = 7.0F;
      float checkedRocketsValue6 = 4.0F;
      float maxRadarSize = 15.0F;
      boolean checkedRocketsFlag = this.checkedRocketsValue != 0;
      float checkedRocketsBound = checkedRocketsValue5
         + this.rocketsLabelWidth
         + checkedRocketsValue6
         + this.maxRocketsField.checkedRocketsAmount
         + 10.0F
         + this.checkedRocketsAmount
         + checkedRocketsValue6
         + this.field.checkedRocketsAmount
         + (checkedRocketsFlag ? 10.0F + this.checkedRocketsField.textWidth + checkedRocketsValue6 + this.elytraStatusField.checkedRocketsAmount : 0.0F)
         + checkedRocketsValue5;
      float coverageValue = Math.max(
         NativeHud.getAmount2(this.rocketsFinderAmount, this.smoothField.checkedRocketsAmount), Math.max(checkedRocketsValue5 + 4.0F + 6.0F + this.checkedTextField.textWidth + checkedRocketsValue5, checkedRocketsBound)
      );
      float bottomFactor = this.smooth.getAmount(clampUnit((float)Math.ceil(coverageValue)), checkedRocketsValue2, 0.08F);
      float checkedRocketsValue10 = NativeHud.getAmount(30.0F + 2.0F * maxRadarSize + this.checkedPanelAnimation * 3.5F + 5.0F);
      float hudLimit = (checkedRocketsValue - bottomFactor) / 2.0F;
      float amountForFloat = this.checkedRocketsSmooth.getAmountForFloat(ShowHUD.getAmounts(hudLimit, getCompletionProgress(true), bottomFactor, checkedRocketsValue10, elytrafinderClockTexts), checkedRocketsValue2, 0.08F);
      float amount4 = ShowHUD.getAmount4("elytrafinder");
      float amountFloat = ShowHUD.getAmountFloat("elytrafinder");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("elytrafinder", hudLimit + amount4, amountForFloat + amountFloat, bottomFactor, checkedRocketsValue10);
      float peekPositionValue = NativeHud.getAmount4(hudLimit + amount4);
      float hudScaleValue = NativeHud.getAmount4(amountForFloat + amountFloat);
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.checkedRocketsPop.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + bottomFactor / 2.0F, hudScaleValue + checkedRocketsValue10 / 2.0F, this.checkedRocketsPop.getAmount());

      try {
         NativeHud.handle(topbarPerfValue, peekPositionValue, hudScaleValue, bottomFactor, checkedRocketsValue10, 1.0F);
         float checkedRocketsValue17 = NativeHud.getAmount(context, peekPositionValue, hudScaleValue, bottomFactor, getWingIdentifier(), "Elytra Finder", this.smoothField, this.pulse, 1.0F);
         float checkedRocketsValue18 = peekPositionValue + checkedRocketsValue5;
         float checkedRocketsValue19 = peekPositionValue + bottomFactor - checkedRocketsValue5;
         this.updateAnimationStep(checkedRocketsValue18, checkedRocketsValue17 + NativeHud.getAmount4((maxRadarSize - 4.0F) / 2.0F), this.computeStatusColor(nanoTime, false), nanoTime);
         NativeHud.handle(this.checkedTextField, checkedRocketsValue18 + 4.0F + 6.0F, NativeHud.getAmount2(checkedRocketsValue17, maxRadarSize, 0.9F), 0.9F, NativeHud.OFF_WHITE);
         float nativeHudValue = checkedRocketsValue17 + maxRadarSize;
         float f13 = NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.78F);
         float roundOffset = NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.9F);
         NativeHud.handlePushScale("Rockets", checkedRocketsValue18, f13, 0.78F, NativeHud.GRAY);
         float checkedRocketsValue23 = checkedRocketsValue18 + (this.rocketsLabelWidth + checkedRocketsValue6 + this.maxRocketsField.checkedRocketsAmount);
         NativeHud.handleAmount(this.maxRocketsField, checkedRocketsValue23, roundOffset, 0.9F, this.getBlendedColor());
         float scaleValue = checkedRocketsValue23 + 10.0F;
         NativeHud.handlePushScale("Checked", scaleValue, f13, 0.78F, NativeHud.GRAY);
         NativeHud.handleAmount(this.field, scaleValue + this.checkedRocketsAmount + checkedRocketsValue6 + this.field.checkedRocketsAmount, roundOffset, 0.9F, NativeHud.OFF_WHITE);
         if (this.checkedAmount > 0.002F && !this.elytraStatusField.isEmpty()) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(this.checkedAmount * this.checkedAmount);

            try {
               NativeHud.handleAmount(this.elytraStatusField, checkedRocketsValue19, roundOffset, 0.9F, NativeHud.OFF_WHITE);
               NativeHud.handleAmount(this.checkedRocketsField, checkedRocketsValue19 - this.elytraStatusField.checkedRocketsAmount - checkedRocketsValue6, f13, 0.78F, NativeHud.GRAY);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }

         if (this.checkedPanelAnimation > 0.002F) {
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(this.checkedPanelAnimation);

            try {
               NativeHud.handle(checkedRocketsValue18, nativeHudValue + maxRadarSize + 1.0F, checkedRocketsValue19 - checkedRocketsValue18, 2.5F, this.displayProgress, NativeHud.getFlags(this.displayProgress));
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
            }
         }
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (hudHotbar != null) {
            this.matrixStack.pop();
         }
      }
   }

   private long getCheckedText(Object obj) {
      return this.checkedText.hashCode() * 31L + System.identityHashCode(obj);
   }

   private void updateAnimations(ElytraStatus elytraStatus, long nanoTime, float smoothValue) {
      boolean smoothFlag = !this.checkedFlag;
      String activity = elytraStatus.activity();
      this.checkedText = activity == null ? "" : activity;
      int max = Math.max(0, elytraStatus.found());
      if (this.smoothField.canMax(max)) {
         this.smoothField.handleAmountTime(max + " found", 0.8F, true, false);
         if (this.peakCheckedValue >= 0 && max > this.peakCheckedValue) {
            this.pulse.updateTime();
         }
      }

      this.peakCheckedValue = max;
      int value = elytraStatus.tone();
      if (value != this.statusCode) {
         boolean smoothFlag2 = this.statusCode < 0 || this.checkedFlag;
         this.elytraStatusValue = this.blendedStatusColor;
         this.statusCode = value;
         this.smoothValue = smoothFlag2 ? 0L : nanoTime;
         if (value == 3 && !smoothFlag2) {
            this.nanoTimeSmooth = nanoTime;
         }
      }

      int maxRockets = Math.max(0, elytraStatus.rockets());
      if (this.maxRocketsField.canMax(maxRockets)) {
         this.maxRocketsField.handleAmountTime(String.valueOf(maxRockets), 0.9F, true, smoothFlag);
      }

      int maxChecked = Math.max(0, elytraStatus.checked());
      if (this.field.canMax(maxChecked)) {
         this.field.handleAmountTime(String.valueOf(maxChecked), 0.9F, true, smoothFlag);
      }

      boolean smoothFlag3 = maxRockets <= 0;
      boolean smoothFlag4 = !smoothFlag3 && elytraStatus.rocketMin() > 0 && maxRockets < elytraStatus.rocketMin();
      this.colorAmount = this.smoothSmooth.getMax(smoothFlag4 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      this.smoothAmount = this.smoothAmountSmoother.getMax(smoothFlag3 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      float amount = elytraStatus.getDurabilityFraction();
      boolean smoothFlag5 = amount >= 0.0F;
      if (smoothFlag5) {
         this.targetProgress = amount;
      }

      this.checkedPanelAnimation = this.amountSmoother.getMax(smoothFlag5 ? 1.0F : 0.0F, smoothValue, 0.08F, 0.002F);
      this.revealAmount = this.revealSmoother.getMax(!smoothFlag5 && !(this.elytraStatusAmount > 0.03F) ? 0.0F : 1.0F, smoothValue, 0.08F, 0.002F);
      this.elytraStatusAmount = this.statusSmoother.getMax(smoothFlag5 && this.revealAmount > 0.95F ? 1.0F : 0.0F, smoothValue, 0.06F, 0.002F);
      this.displayProgress = this.elytraStatusSmooth.getMax(this.targetProgress, smoothValue, 0.12F, 0.001F);
      double distance = elytraStatus.distance();
      int value2 = distance >= 0.0 ? 1 : (amount >= 0.0F ? 2 : 0);
      if (value2 != this.checkedRocketsValue || this.lastNanoTimeSmooth == 0L || nanoTime - this.lastNanoTimeSmooth >= 100000000L) {
         this.lastNanoTimeSmooth = nanoTime;
         this.checkedRocketsValue = value2;
         if (value2 == 1) {
            if (this.elytraStatusField.canMax(computeDurationNanos(distance))) {
               this.elytraStatusField.handleAmountTime(formatDistance(distance), 0.9F, true, smoothFlag);
            }
         } else if (value2 == 2) {
            int round = Math.round(amount * 100.0F);
            if (this.elytraStatusField.canMax(-1L - round)) {
               this.elytraStatusField.handleAmountTime(round + "%", 0.9F, true, smoothFlag);
            }
         }

         if (value2 != 0 && this.checkedRocketsField.canMax(value2)) {
            this.checkedRocketsField.handleAmountTime(value2 == 1 ? "Target" : "Elytra", 0.78F, false, smoothFlag);
         }
      }

      this.checkedAmount = this.checkedSmoother.getMax(value2 != 0 ? 1.0F : 0.0F, smoothValue, 0.08F, 0.002F);
   }

   public ElytraStatusCard() {
      this.smooth = new Smooth();
      this.checkedRocketsSmooth = new Smooth();
      this.amountSmoother = new Smooth();
      this.elytraStatusSmooth = new Smooth();
      this.checkedSmoother = new Smooth();
      this.smoothSmooth = new Smooth();
      this.smoothAmountSmoother = new Smooth();
      this.revealSmoother = new Smooth();
      this.statusSmoother = new Smooth();
      this.smoothField = new Field();
      this.checkedTextField = new Field();
      this.statusDistanceField = new Field();
      this.maxRocketsField = new Field();
      this.field = new Field();
      this.checkedRocketsField = new Field();
      this.elytraStatusField = new Field();
      this.checkedFlag = true;
      this.peakCheckedValue = -1;
      this.statusCode = -1;
      this.checkedRocketsValue = 0;
      this.targetProgress = 1.0F;
      this.checkedText = "";
   }

   private Color getBlendedColor() {
      int value = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-920587, -1535700, this.colorAmount), -1750963, this.smoothAmount);
      if (this.color == null || value != this.colorValue) {
         this.colorValue = value;
         this.color = new Color(value, true);
      }

      return this.color;
   }

   private void updateAnimationStep(float elytraStatusValue, float nativeHudValue, int elytraStatusValue3, long nanoTime) {
      float peekPositionValue = NativeHud.getAmount4(elytraStatusValue);
      float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
      float elytraStatusValue7 = 0.5F + 0.5F * (float)Math.cos((float)(nanoTime % 1800000000L) * 3.4906587E-9F);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(elytraStatusValue3, 0.22F * (0.35F + 0.65F * elytraStatusValue7)), 2.5F, true);
      float elytraStatusLimit = this.computeElapsedProgress(nanoTime);
      if (elytraStatusLimit > 0.002F) {
         NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(elytraStatusValue3, 0.55F * elytraStatusLimit * elytraStatusLimit), 2.5F + 4.0F * (1.0F - elytraStatusLimit), true);
      }

      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, 0.0F, elytraStatusValue3);
   }

   private static String fitTextToWidth(String elytraStatusText, float elytraStatusLimit, VertexTexturePeek vertexTexturePeek) {
      if (elytraStatusText != null && !elytraStatusText.isEmpty() && !(measureTextWidth(elytraStatusText, vertexTexturePeek) <= elytraStatusLimit)) {
         int end = 0;
         int availLength = elytraStatusText.length();

         while (end < availLength) {
            int end2 = end + availLength + 1 >>> 1;
            if (measureTextWidth(elytraStatusText.substring(0, end2) + "...", vertexTexturePeek) <= elytraStatusLimit) {
               end = end2;
            } else {
               availLength = end2 - 1;
            }
         }

         return elytraStatusText.substring(0, end).stripTrailing() + "...";
      } else {
         return elytraStatusText == null ? "" : elytraStatusText;
      }
   }

   private float computeElapsedProgress(long elytraStatusValue) {
      if (this.nanoTimeSmooth == 0L) {
         return 0.0F;
      } else {
         float elytraStatusLimit = (float)(elytraStatusValue - this.nanoTimeSmooth) / 7.0E8F;
         if (elytraStatusLimit >= 1.0F) {
            this.nanoTimeSmooth = 0L;
            return 0.0F;
         } else {
            return 1.0F - elytraStatusLimit;
         }
      }
   }

   private static Identifier getWingIdentifier() {
      if (identifier == null) {
         identifier = IcoGenUtils.getIdentifierForString("wing");
      }

      return identifier;
   }

   private static float getCompletionProgress(boolean buildprogressFlag) {
      int spotifySong = SpotifyHUD.getSpotifySong();
      float buildprogress = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      Module iliIiiliiiii = ModuleRegistry.getModule(ShowHUD.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal()) {
         float buildprogressFactor = buildprogressFlag ? 26.0F : ShowHUD.getAmount4(1.0F) + 11.0F;
         buildprogress += buildprogressFactor * ShowHUD.getAmountForString("buildprogress") + 6.0F;
      }

      return buildprogress;
   }

   public void updateSmoothTime() {
      this.animationTick += 2L;
      this.checkedFlag = true;
      this.lastNanoTime = 0L;
      this.lastNanoTimeSmooth = 0L;
      this.smoothValue = 0L;
      this.nanoTimeSmooth = 0L;
      this.peakCheckedValue = -1;
      this.statusCode = -1;
      this.smooth.updateMax();
      this.checkedRocketsSmooth.updateMax();
      this.amountSmoother.updateMax();
      this.elytraStatusSmooth.updateMax();
      this.checkedSmoother.updateMax();
      this.smoothSmooth.updateMax();
      this.smoothAmountSmoother.updateMax();
      this.revealSmoother.updateMax();
      this.statusSmoother.updateMax();
      this.elytraStatusAmount = 0.0F;
   }

   private int computeStatusColor(long elytraStatusValue, boolean elytraStatusFlag) {
      int releaseRightValue = switch (this.statusCode) {
         case 1 -> elytraStatusFlag ? -667076 : -1535700;
         case 2 -> elytraStatusFlag ? -700356 : -1750963;
         case 3 -> elytraStatusFlag ? -12788616 : -8393375;
         default -> elytraStatusFlag ? -1 : NativeHud.getReleaseRight();
      };
      float elytraStatusLimit = this.smoothValue == 0L ? 1.0F : NativeHud.getMax((float)(elytraStatusValue - this.smoothValue) / 2.2E8F);
      if (elytraStatusLimit >= 1.0F) {
         this.smoothValue = 0L;
      }

      this.blendedStatusColor = elytraStatusLimit >= 1.0F ? releaseRightValue : CardMatrixUtils.getValue(this.elytraStatusValue, releaseRightValue, NativeHud.getAmount5(elytraStatusLimit));
      return this.blendedStatusColor;
   }
}
