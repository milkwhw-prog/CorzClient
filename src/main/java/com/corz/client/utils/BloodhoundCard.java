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
import com.corz.client.utils.ScissorTgtHandler.Tone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpotifyHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

public final class BloodhoundCard {
   private final Field field;
   private long lastNanoTime;
   private Tone dangerPausedType;
   private long animationTick;
   private VertexTextureFont vertexTexture;
   private final Smooth smooth;
   private long dangerPausedValue;
   private static Identifier identifier;
   private final Smooth bloodhoundBestSmooth;
   private boolean smoothActive;
   private final Field bloodhoundBestField;
   public static final String BLOODHOUND = "bloodhound";
   private int previousSmoothValue;
   private static final String CHUNKS = "Chunks";
   private int bloodhoundCardValue;
   private float basesBestAmount;
   private static final int topbarPerfValue = TwoDidUtils.getValue(0, 47);
   private static final String BEST = "Best";
   private final Pop bloodhoundBestPop;
   private static final String BASES = "Bases";
   private final MatrixStack matrixStack = new MatrixStack();
   private static final String[] bloodhoundElytrafinderTexts = new String[]{"topbar", "perf", "clock", "elytrapilot", "anchorhunter", "elytrafinder"};
   private float bestChunksAmount;
   private boolean dangerPausedActive;
   private float basesBloodhoundAmount;
   private float chunksBestAmount;
   private static final String ELLIPSIS_TEXT = "...";
   private static final String TITLE_TEXT = "Bloodhound";
   private final Field bloodhoundCardField;
   private String dangerPausedText;
   private final Pulse pulse;
   private final Field dangerPausedField;
   private final Field basesCardField;

   private static Identifier getIdentifierTarget() {
      if (identifier == null) {
         identifier = IcoGenUtils.getIdentifierForString("target");
      }

      return identifier;
   }

   private int getDangerPaused(long dangerPausedValue) {
      int releaseRight2;
      if (this.dangerPausedType == null) {
         releaseRight2 = NativeHud.getReleaseRight();
      } else {
         switch (this.dangerPausedType) {
            case ACTIVE:
               releaseRight2 = -8393375;
               break;
            case PAUSED:
               releaseRight2 = -1535700;
               break;
            case DANGER:
               releaseRight2 = -1750963;
               break;
            default:
               releaseRight2 = NativeHud.getReleaseRight();
         }
      }

      int releaseRightValue = releaseRight2;
      float dangerPausedLimit = this.dangerPausedValue == 0L ? 1.0F : NativeHud.getMax((float)(dangerPausedValue - this.dangerPausedValue) / 2.2E8F);
      if (dangerPausedLimit >= 1.0F) {
         this.dangerPausedValue = 0L;
      }

      this.bloodhoundCardValue = dangerPausedLimit >= 1.0F ? releaseRightValue : CardMatrixUtils.getValue(this.previousSmoothValue, releaseRightValue, NativeHud.getAmount5(dangerPausedLimit));
      return this.bloodhoundCardValue;
   }

   private static float getBuildprogress() {
      int spotifySong = SpotifyHUD.getSpotifySong();
      float buildprogress = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      Module iliIiiliiiii = ModuleRegistry.getModule(ShowHUD.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal()) {
         buildprogress += 26.0F * ShowHUD.getAmountForString("buildprogress") + 6.0F;
      }

      return buildprogress;
   }

   private void handleDangerPaused(Tone dangerPausedType, String dangerPausedText, int dangerPausedBound, int rocketsBuyBound, int dangerPausedBound3, String dangerPausedText2, long dangerPausedValue) {
      boolean dangerPausedFlag = !this.smoothActive;
      this.dangerPausedText = dangerPausedText == null ? "" : dangerPausedText;
      String fieldText = dangerPausedText2 == null ? "" : dangerPausedText2;
      if (this.field.canMax(fieldText.hashCode())) {
         this.field.handleAmountTime(fieldText, 0.8F, true, false);
         boolean flag = !fieldText.isEmpty();
         if (flag && !this.dangerPausedActive) {
            this.pulse.updateTime();
         }

         this.dangerPausedActive = flag;
      }

      if (dangerPausedType != this.dangerPausedType) {
         boolean dangerPausedFlag2 = this.dangerPausedType == null || this.smoothActive;
         this.previousSmoothValue = this.bloodhoundCardValue;
         this.dangerPausedType = dangerPausedType;
         this.dangerPausedValue = dangerPausedFlag2 ? 0L : dangerPausedValue;
      }

      if (this.basesCardField.canMax(dangerPausedBound)) {
         this.basesCardField.handleAmountTime(String.valueOf(Math.max(0, dangerPausedBound)), 0.9F, true, dangerPausedFlag);
      }

      if (this.dangerPausedField.canMax(Math.max(0, rocketsBuyBound))) {
         this.dangerPausedField.handleAmountTime(rocketsBuyBound <= 0 ? "-" : rocketsBuyBound + "%", 0.9F, true, dangerPausedFlag);
      }

      if (this.bloodhoundBestField.canMax(dangerPausedBound3)) {
         this.bloodhoundBestField.handleAmountTime(String.valueOf(Math.max(0, dangerPausedBound3)), 0.9F, true, dangerPausedFlag);
      }
   }

   private void renderBloodhoundBest(DrawContext context, int bloodhoundBestValue, float smoothValue, long bloodhoundBestValue3) {
      VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
      if (vertexTexture != this.vertexTexture) {
         this.vertexTexture = vertexTexture;
         this.basesBloodhoundAmount = NativeHud.getAmount3("Bloodhound", 0.95F);
         this.basesBestAmount = NativeHud.getAmount3("Bases", 0.78F);
         this.bestChunksAmount = NativeHud.getAmount3("Best", 0.78F);
         this.chunksBestAmount = NativeHud.getAmount3("Chunks", 0.78F);
         this.basesCardField.handleAmountTime(this.basesCardField.bloodhoundBestText, 0.9F, true, false);
         this.dangerPausedField.handleAmountTime(this.dangerPausedField.bloodhoundBestText, 0.9F, true, false);
         this.bloodhoundBestField.handleAmountTime(this.bloodhoundBestField.bloodhoundBestText, 0.9F, true, false);
      }

      if (this.bloodhoundCardField.canMax(this.dangerPausedText.hashCode() * 31L + System.identityHashCode(vertexTexture))) {
         float bloodhoundCardLimit = 212.0F;
         this.bloodhoundCardField.handleAmountTime(formatText(CardStatusUtilsFlag.getOrDefault(this.dangerPausedText), bloodhoundCardLimit), 0.9F, false, !this.smoothActive);
      }

      float bloodhoundBestValue5 = 7.0F;
      float bloodhoundBestValue6 = 4.0F;
      float maxRadarSize = 15.0F;
      float bloodhoundBestBound = bloodhoundBestValue5
         + this.basesBestAmount
         + bloodhoundBestValue6
         + this.basesCardField.checkedRocketsAmount
         + 10.0F
         + this.bestChunksAmount
         + bloodhoundBestValue6
         + this.dangerPausedField.checkedRocketsAmount
         + 10.0F
         + this.chunksBestAmount
         + bloodhoundBestValue6
         + this.bloodhoundBestField.checkedRocketsAmount
         + bloodhoundBestValue5;
      float coverageValue = Math.max(
         NativeHud.getAmount2(this.basesBloodhoundAmount, this.field.checkedRocketsAmount), Math.max(bloodhoundBestValue5 + 4.0F + 6.0F + this.bloodhoundCardField.textWidth + bloodhoundBestValue5, bloodhoundBestBound)
      );
      float bottomFactor = this.bloodhoundBestSmooth.getAmount(Math.max(168.0F, Math.min(236.0F, (float)Math.ceil(coverageValue))), smoothValue, 0.08F);
      float bloodhoundBestValue10 = NativeHud.getAmount(30.0F + 2.0F * maxRadarSize + 5.0F);
      float hudLimit = (bloodhoundBestValue - bottomFactor) / 2.0F;
      float amountForFloat = this.smooth.getAmountForFloat(ShowHUD.getAmounts(hudLimit, getBuildprogress(), bottomFactor, bloodhoundBestValue10, bloodhoundElytrafinderTexts), smoothValue, 0.08F);
      float amount4 = ShowHUD.getAmount4("bloodhound");
      float amountFloat = ShowHUD.getAmountFloat("bloodhound");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("bloodhound", hudLimit + amount4, amountForFloat + amountFloat, bottomFactor, bloodhoundBestValue10);
      float peekPositionValue = NativeHud.getAmount4(hudLimit + amount4);
      float hudScaleValue = NativeHud.getAmount4(amountForFloat + amountFloat);
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.bloodhoundBestPop.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + bottomFactor / 2.0F, hudScaleValue + bloodhoundBestValue10 / 2.0F, this.bloodhoundBestPop.getAmount());

      try {
         NativeHud.handle(topbarPerfValue, peekPositionValue, hudScaleValue, bottomFactor, bloodhoundBestValue10, 1.0F);
         float nativeHudValue = NativeHud.getAmount(context, peekPositionValue, hudScaleValue, bottomFactor, getIdentifierTarget(), "Bloodhound", this.field, this.pulse, 1.0F);
         float bloodhoundBestValue18 = peekPositionValue + bloodhoundBestValue5;
         this.handle(bloodhoundBestValue18, nativeHudValue + NativeHud.getAmount4((maxRadarSize - 4.0F) / 2.0F), this.getDangerPaused(bloodhoundBestValue3), bloodhoundBestValue3);
         NativeHud.handle(this.bloodhoundCardField, bloodhoundBestValue18 + 4.0F + 6.0F, NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.9F), 0.9F, NativeHud.OFF_WHITE);
         float bloodhoundBestValue19 = nativeHudValue + maxRadarSize;
         float f13 = NativeHud.getAmount2(bloodhoundBestValue19, maxRadarSize, 0.78F);
         float roundOffset = NativeHud.getAmount2(bloodhoundBestValue19, maxRadarSize, 0.9F);
         float colorCheckedOffset = getColorChecked("Bases", this.basesBestAmount, this.basesCardField, bloodhoundBestValue18, f13, roundOffset) + 10.0F;
         float scaleValue = getColorChecked("Best", this.bestChunksAmount, this.dangerPausedField, colorCheckedOffset, f13, roundOffset) + 10.0F;
         getColorChecked("Chunks", this.chunksBestAmount, this.bloodhoundBestField, scaleValue, f13, roundOffset);
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (hudHotbar != null) {
            this.matrixStack.pop();
         }
      }
   }

   public void updateSmoothTime() {
      this.animationTick += 2L;
      this.smoothActive = true;
      this.lastNanoTime = 0L;
      this.dangerPausedValue = 0L;
      this.dangerPausedActive = false;
      this.dangerPausedType = null;
      this.bloodhoundBestSmooth.updateMax();
      this.smooth.updateMax();
   }

   private static String formatText(String formatText, float bloodhoundCardLimit) {
      if (formatText != null && !formatText.isEmpty()) {
         if (NativeHud.getAmount3(formatText, 0.9F) <= bloodhoundCardLimit) {
            return formatText;
         } else {
            int end = 0;
            int length2 = formatText.length();

            while (end < length2) {
               int end2 = end + length2 + 1 >>> 1;
               if (NativeHud.getAmount3(formatText.substring(0, end2) + "...", 0.9F) <= bloodhoundCardLimit) {
                  end = end2;
               } else {
                  length2 = end2 - 1;
               }
            }

            return formatText.substring(0, end).stripTrailing() + "...";
         }
      } else {
         return "";
      }
   }

   private void handle(float bloodhoundCardValue, float nativeHudValue, int bloodhoundCardValue3, long bloodhoundCardValue4) {
      float peekPositionValue = NativeHud.getAmount4(bloodhoundCardValue);
      float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
      float bloodhoundCardValue7 = 0.5F + 0.5F * (float)Math.cos((float)(bloodhoundCardValue4 % 1800000000L) * 3.4906587E-9F);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(bloodhoundCardValue3, 0.22F * (0.35F + 0.65F * bloodhoundCardValue7)), 2.5F, true);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, 0.0F, bloodhoundCardValue3);
   }

   public void renderScaledWidth(DrawContext context, Tone tone, String scaledWidthText, int scaledWidthValue, int rocketsBuyBound, int scaledWidthValue3, String scaledWidthText2) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (context != null && instance.player != null) {
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
            float panelOverlayLimit = ShowHUD.getTime();
            if (panelOverlayLimit < 0.004F) {
               return;
            }

            long nanoTime = System.nanoTime();
            float smoothValue = this.lastNanoTime == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
            this.lastNanoTime = nanoTime;
            this.handleDangerPaused(tone, scaledWidthText, scaledWidthValue, rocketsBuyBound, scaledWidthValue3, scaledWidthText2, nanoTime);
            if (this.bloodhoundBestPop.canTimeAmount(true, ++this.animationTick)) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.renderBloodhoundBest(context, scaledWidth, smoothValue, nanoTime);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               this.smoothActive = false;
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

   private static float getColorChecked(String colorCheckedText, float colorCheckedValue, Field field, float scaleValue, float f13, float roundOffset) {
      NativeHud.handlePushScale(colorCheckedText, scaleValue, f13, 0.78F, NativeHud.GRAY);
      float colorChecked = scaleValue + colorCheckedValue + 4.0F + field.checkedRocketsAmount;
      NativeHud.handleAmount(field, colorChecked, roundOffset, 0.9F, NativeHud.OFF_WHITE);
      return colorChecked;
   }

   public BloodhoundCard() {
      this.bloodhoundBestPop = new Pop();
      this.pulse = new Pulse();
      this.bloodhoundBestSmooth = new Smooth();
      this.smooth = new Smooth();
      this.field = new Field();
      this.bloodhoundCardField = new Field();
      this.basesCardField = new Field();
      this.dangerPausedField = new Field();
      this.bloodhoundBestField = new Field();
      this.smoothActive = true;
      this.dangerPausedText = "";
   }
}
