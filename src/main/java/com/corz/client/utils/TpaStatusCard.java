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
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpotifyHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

public final class TpaStatusCard {
   private static final int PANEL_STYLE = TwoDidUtils.getValue(0, 26);
   private long toneChangeStartNanos;
   private static Identifier broadcastIcon;
   public static final String AUTOTPAHERE = "autotpahere";
   private static final String[] DEFAULT_TOPBAR_TAGS = new String[]{"topbar", "perf", "clock"};
   private static final String ELLIPSIS = "...";
   private static final String DEATHS = "Deaths";
   private final Field homeCountField;
   private static final String CAME = " came";
   private final Field sentCountField;
   private final Field statusTextField;
   private static final String SENT = "Sent";
   private static final String AUTO_TPA = "Auto TPA Here";
   private final Pop cardPop;
   private float autoTpaLabelWidth;
   private float sentLabelWidth;
   private final Smooth verticalPositionSmooth;
   private final Field cameCountField;
   private int previousCameCount;
   private final Pulse pulse;
   private final Field deathsCountField;
   private int previousToneColor;
   private int currentToneColor;
   private boolean firstFrame;
   private float homeLabelWidth;
   private long animationTick;
   private final Smooth widthSmooth;
   private VertexTextureFont lastFontRenderer;
   private long lastNanoTime;
   private final MatrixStack matrixStack = new MatrixStack();
   private static final String[] ELYTRA_FINDER_TOPBAR_TAGS = new String[]{"topbar", "perf", "clock", "elytrafinder"};
   private Tone currentTone;
   private String statusText;
   private static final String HOME = "Home";
   private float deathsLabelWidth;

   private void renderStatusIndicator(float tpaStatusValue, float nativeHudValue, int tpaStatusValue3, long dangerPausedValue) {
      float peekPositionValue = NativeHud.getAmount4(tpaStatusValue);
      float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
      float tpaStatusValue7 = 0.5F + 0.5F * (float)Math.cos((float)(dangerPausedValue % 1800000000L) * 3.4906587E-9F);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(tpaStatusValue3, 0.22F * (0.35F + 0.65F * tpaStatusValue7)), 2.5F, true);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, 0.0F, tpaStatusValue3);
   }

   public TpaStatusCard() {
      this.cardPop = new Pop();
      this.pulse = new Pulse();
      this.widthSmooth = new Smooth();
      this.verticalPositionSmooth = new Smooth();
      this.cameCountField = new Field();
      this.statusTextField = new Field();
      this.sentCountField = new Field();
      this.deathsCountField = new Field();
      this.homeCountField = new Field();
      this.firstFrame = true;
      this.previousCameCount = -1;
      this.statusText = "";
   }

   private void renderCardContent(DrawContext context, int deathsHomeValue, float smoothValue, long dangerPausedValue) {
      VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
      if (vertexTexture != this.lastFontRenderer) {
         this.lastFontRenderer = vertexTexture;
         this.autoTpaLabelWidth = NativeHud.getAmount3("Auto TPA Here", 0.95F);
         this.sentLabelWidth = NativeHud.getAmount3("Sent", 0.78F);
         this.deathsLabelWidth = NativeHud.getAmount3("Deaths", 0.78F);
         this.homeLabelWidth = NativeHud.getAmount3("Home", 0.78F);
      }

      if (this.statusTextField.canMax(this.statusText.hashCode() * 31L + System.identityHashCode(vertexTexture))) {
         float tpaStatusLimit = 196.0F;
         this.statusTextField.handleAmountTime(truncateWithEllipsis(CardStatusUtilsFlag.getOrDefault(this.statusText), tpaStatusLimit), 0.9F, false, !this.firstFrame);
      }

      float deathsHomeValue5 = 7.0F;
      float deathsHomeValue6 = 4.0F;
      float deathsHomeValue7 = 15.0F;
      float deathsHomeBound = deathsHomeValue5
         + this.sentLabelWidth
         + deathsHomeValue6
         + this.sentCountField.checkedRocketsAmount
         + 10.0F
         + this.deathsLabelWidth
         + deathsHomeValue6
         + this.deathsCountField.checkedRocketsAmount
         + 10.0F
         + this.homeLabelWidth
         + deathsHomeValue6
         + this.homeCountField.checkedRocketsAmount
         + deathsHomeValue5;
      float max8 = Math.max(
         NativeHud.getAmount2(this.autoTpaLabelWidth, this.cameCountField.checkedRocketsAmount), Math.max(deathsHomeValue5 + 4.0F + 6.0F + this.statusTextField.textWidth + deathsHomeValue5, deathsHomeBound)
      );
      float bottomFactor = this.widthSmooth.getAmount(Math.max(150.0F, Math.min(220.0F, (float)Math.ceil(max8))), smoothValue, 0.08F);
      float maxRadarSize = NativeHud.getAmount(30.0F + 2.0F * deathsHomeValue7 + 5.0F);
      float hudLimit = (deathsHomeValue - bottomFactor) / 2.0F;
      float amountForFloat = this.verticalPositionSmooth.getAmountForFloat(ShowHUD.getAmounts(hudLimit, getCardTopOffset(), bottomFactor, maxRadarSize, getTopbarTags()), smoothValue, 0.08F);
      float amount4 = ShowHUD.getAmount4("autotpahere");
      float amountFloat = ShowHUD.getAmountFloat("autotpahere");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("autotpahere", hudLimit + amount4, amountForFloat + amountFloat, bottomFactor, maxRadarSize);
      float peekPositionValue = NativeHud.getAmount4(hudLimit + amount4);
      float hudScaleValue = NativeHud.getAmount4(amountForFloat + amountFloat);
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.cardPop.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + bottomFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.cardPop.getAmount());

      try {
         NativeHud.handle(PANEL_STYLE, peekPositionValue, hudScaleValue, bottomFactor, maxRadarSize, 1.0F);
         float deathsHomeValue17 = NativeHud.getAmount(context, peekPositionValue, hudScaleValue, bottomFactor, getBroadcastIcon(), "Auto TPA Here", this.cameCountField, this.pulse, 1.0F);
         float tpaStatusValue = peekPositionValue + deathsHomeValue5;
         this.renderStatusIndicator(tpaStatusValue, deathsHomeValue17 + NativeHud.getAmount4((deathsHomeValue7 - 4.0F) / 2.0F), this.getToneColor(dangerPausedValue), dangerPausedValue);
         NativeHud.handle(this.statusTextField, tpaStatusValue + 4.0F + 6.0F, NativeHud.getAmount2(deathsHomeValue17, deathsHomeValue7, 0.9F), 0.9F, NativeHud.OFF_WHITE);
         float nativeHudValue = deathsHomeValue17 + deathsHomeValue7;
         float f13 = NativeHud.getAmount2(nativeHudValue, deathsHomeValue7, 0.78F);
         float roundOffset = NativeHud.getAmount2(nativeHudValue, deathsHomeValue7, 0.9F);
         float colorCheckedOffset = renderLabelAndCount("Sent", this.sentLabelWidth, this.sentCountField, tpaStatusValue, f13, roundOffset) + 10.0F;
         float scaleValue = renderLabelAndCount("Deaths", this.deathsLabelWidth, this.deathsCountField, colorCheckedOffset, f13, roundOffset) + 10.0F;
         renderLabelAndCount("Home", this.homeLabelWidth, this.homeCountField, scaleValue, f13, roundOffset);
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (hudHotbar != null) {
            this.matrixStack.pop();
         }
      }
   }

   private static float getCardTopOffset() {
      int spotifySong = SpotifyHUD.getSpotifySong();
      float buildprogress = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      Module iliIiiliiiii = ModuleRegistry.getModule(ShowHUD.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal()) {
         buildprogress += 26.0F * ShowHUD.getAmountForString("buildprogress") + 6.0F;
      }

      return buildprogress;
   }

   private static Identifier getBroadcastIcon() {
      if (broadcastIcon == null) {
         broadcastIcon = IcoGenUtils.getIdentifierForString("broadcast");
      }

      return broadcastIcon;
   }

   private static String truncateWithEllipsis(String formatText, float tpaStatusLimit) {
      if (formatText != null && !formatText.isEmpty()) {
         if (NativeHud.getAmount3(formatText, 0.9F) <= tpaStatusLimit) {
            return formatText;
         } else {
            int end = 0;
            int availLength = formatText.length();

            while (end < availLength) {
               int end2 = end + availLength + 1 >>> 1;
               if (NativeHud.getAmount3(formatText.substring(0, end2) + "...", 0.9F) <= tpaStatusLimit) {
                  end = end2;
               } else {
                  availLength = end2 - 1;
               }
            }

            return formatText.substring(0, end).stripTrailing() + "...";
         }
      } else {
         return "";
      }
   }

   public void resetAnimationState() {
      this.animationTick += 2L;
      this.firstFrame = true;
      this.lastNanoTime = 0L;
      this.toneChangeStartNanos = 0L;
      this.previousCameCount = -1;
      this.currentTone = null;
      this.widthSmooth.updateMax();
      this.verticalPositionSmooth.updateMax();
   }

   private static String[] getTopbarTags() {
      Module iliIiiliiiii = ModuleRegistry.getModule(AutoElytraFinder.class);
      return iliIiiliiiii != null && iliIiiliiiii.isEnabled() ? ELYTRA_FINDER_TOPBAR_TAGS : DEFAULT_TOPBAR_TAGS;
   }

   private void updateFromSnapshot(Snapshot snapshot, int dangerPausedBound, int rocketsBuyBound, long dangerPausedValue) {
      boolean dangerPausedFlag = !this.firstFrame;
      String dangerPausedText = snapshot.hint() != null ? snapshot.hint() : snapshot.state();
      this.statusText = dangerPausedText == null ? "" : dangerPausedText;
      int value = snapshot.popsTaken();
      if (this.cameCountField.canMax(value)) {
         this.cameCountField.handleAmountTime(value > 0 ? value + " came" : "", 0.8F, true, false);
         if (this.previousCameCount >= 0 && value > this.previousCameCount) {
            this.pulse.updateTime();
         }
      }

      this.previousCameCount = value;
      if (snapshot.tone() != this.currentTone) {
         boolean dangerPausedFlag2 = this.currentTone == null || this.firstFrame;
         this.previousToneColor = this.currentToneColor;
         this.currentTone = snapshot.tone();
         this.toneChangeStartNanos = dangerPausedFlag2 ? 0L : dangerPausedValue;
      }

      if (this.sentCountField.canMax(snapshot.pops())) {
         this.sentCountField.handleAmountTime(String.valueOf(snapshot.pops()), 0.9F, true, dangerPausedFlag);
      }

      if (this.deathsCountField.canMax(dangerPausedBound)) {
         this.deathsCountField.handleAmountTime(String.valueOf(Math.max(0, dangerPausedBound)), 0.9F, true, dangerPausedFlag);
      }

      if (this.homeCountField.canMax(rocketsBuyBound)) {
         this.homeCountField.handleAmountTime(String.valueOf(Math.max(0, rocketsBuyBound)), 0.9F, true, dangerPausedFlag);
      }
   }

   public void renderStatusCard(DrawContext context, Snapshot snapshot, int scaledWidthValue, int rocketsBuyBound) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (context != null && instance.player != null && snapshot != null) {
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
            this.updateFromSnapshot(snapshot, scaledWidthValue, rocketsBuyBound, nanoTime);
            if (this.cardPop.canTimeAmount(true, ++this.animationTick)) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.renderCardContent(context, scaledWidth, smoothValue, nanoTime);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               this.firstFrame = false;
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

   private int getToneColor(long dangerPausedValue) {
      int releaseRight2;
      if (this.currentTone == null) {
         releaseRight2 = NativeHud.getReleaseRight();
      } else {
         switch (this.currentTone) {
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
      float dangerPausedLimit = this.toneChangeStartNanos == 0L ? 1.0F : NativeHud.getMax((float)(dangerPausedValue - this.toneChangeStartNanos) / 2.2E8F);
      if (dangerPausedLimit >= 1.0F) {
         this.toneChangeStartNanos = 0L;
      }

      this.currentToneColor = dangerPausedLimit >= 1.0F ? releaseRightValue : CardMatrixUtils.getValue(this.previousToneColor, releaseRightValue, NativeHud.getAmount5(dangerPausedLimit));
      return this.currentToneColor;
   }

   private static float renderLabelAndCount(String colorCheckedText, float colorCheckedValue, Field field, float scaleValue, float f13, float roundOffset) {
      NativeHud.handlePushScale(colorCheckedText, scaleValue, f13, 0.78F, NativeHud.GRAY);
      float colorChecked = scaleValue + colorCheckedValue + 4.0F + field.checkedRocketsAmount;
      NativeHud.handleAmount(field, colorChecked, roundOffset, 0.9F, NativeHud.OFF_WHITE);
      return colorChecked;
   }
}
