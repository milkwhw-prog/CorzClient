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
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.corz.client.categories.bots.AutoTpaHere;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.categories.visuals.SpotifyHUD;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

public final class HunterStatusCard {
   private long animationTicks;
   private Tone currentTone;
   private final Field perHourField;
   private static final String NEXT_RTP_LABEL = "Next RTP";
   private float anchorsLabelWidth;
   private final Field warningField;
   private static final int CARD_STYLE_ID = TwoDidUtils.getValue(0, 31);
   private final Field statusTextField;
   private static final String ELLIPSIS = "...";
   private final Smooth yPositionSmooth;
   private static final String ANCHOR_HUNTER = "Anchor Hunter";
   private boolean skipAnimation;
   private int currentAccentColor;
   private final Field anchorsCountField;
   private final Pulse pulse;
   private final Smooth widthSmooth;
   private long toneChangeNanos;
   private static final String ANCHORS = "Anchors";
   private String statusText;
   private static final String PER_HOUR_LABEL = "Per hour";
   private final Field nextRtpField;
   private VertexTextureFont cachedFont;
   private final MatrixStack matrixStack = new MatrixStack();
   private static final Color RED = new Color(-1750963, true);
   private boolean warningActive;
   private final Pop fadePop = new Pop();
   private long lastNanoTime;
   private int previousAccentColor;
   public static final String HUD_ELEMENT_ID = "anchorhunter";
   private static Identifier broadcastIcon;
   private float perHourLabelWidth;
   private float nextRtpLabelWidth;
   private float titleWidth;

   private int getAccentColor(long dangerPausedValue) {
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
      float dangerPausedLimit = this.toneChangeNanos == 0L ? 1.0F : NativeHud.getMax((float)(dangerPausedValue - this.toneChangeNanos) / 2.2E8F);
      if (dangerPausedLimit >= 1.0F) {
         this.toneChangeNanos = 0L;
      }

      this.currentAccentColor = dangerPausedLimit >= 1.0F ? releaseRightValue : CardMatrixUtils.getValue(this.previousAccentColor, releaseRightValue, NativeHud.getAmount5(dangerPausedLimit));
      return this.currentAccentColor;
   }

   private void updateFields(Snapshot snapshot, int dangerPausedBound, int rocketsBuyBound, String dangerPausedText, String dangerPausedText2, long dangerPausedValue) {
      boolean dangerPausedFlag = !this.skipAnimation;
      String state3 = snapshot.state();
      this.statusText = state3 == null ? "" : state3;
      String dangerPausedText4 = dangerPausedText2 == null ? "" : dangerPausedText2;
      if (this.warningField.canMax(dangerPausedText4.hashCode())) {
         this.warningField.handleAmountTime(dangerPausedText4, 0.8F, true, false);
         boolean flag = !dangerPausedText4.isEmpty();
         if (flag && !this.warningActive) {
            this.pulse.updateTime();
         }

         this.warningActive = flag;
      }

      if (snapshot.tone() != this.currentTone) {
         boolean dangerPausedFlag2 = this.currentTone == null || this.skipAnimation;
         this.previousAccentColor = this.currentAccentColor;
         this.currentTone = snapshot.tone();
         this.toneChangeNanos = dangerPausedFlag2 ? 0L : dangerPausedValue;
      }

      if (this.anchorsCountField.canMax(dangerPausedBound)) {
         this.anchorsCountField.handleAmountTime(String.valueOf(Math.max(0, dangerPausedBound)), 0.9F, true, dangerPausedFlag);
      }

      if (this.perHourField.canMax(rocketsBuyBound)) {
         this.perHourField.handleAmountTime(String.valueOf(Math.max(0, rocketsBuyBound)), 0.9F, true, dangerPausedFlag);
      }

      String fieldText = dangerPausedText == null ? "-" : dangerPausedText;
      if (this.nextRtpField.canMax(fieldText.hashCode())) {
         this.nextRtpField.handleAmountTime(fieldText, 0.9F, true, dangerPausedFlag);
      }
   }

   public HunterStatusCard() {
      this.pulse = new Pulse();
      this.widthSmooth = new Smooth();
      this.yPositionSmooth = new Smooth();
      this.warningField = new Field();
      this.statusTextField = new Field();
      this.anchorsCountField = new Field();
      this.perHourField = new Field();
      this.nextRtpField = new Field();
      this.skipAnimation = true;
      this.statusText = "";
   }

   private static String[] getBlockingHudElementIds() {
      Module iliIiiliiiii = ModuleRegistry.getModule(AutoElytraFinder.class);
      Module module3 = ModuleRegistry.getModule(AutoTpaHere.class);
      boolean perfTopbarFlag = iliIiiliiiii != null && iliIiiliiiii.isEnabled();
      boolean perfTopbarFlag2 = module3 != null && module3.isEnabled();
      if (perfTopbarFlag && perfTopbarFlag2) {
         return new String[]{"topbar", "perf", "clock", "elytrafinder", "autotpahere"};
      } else if (perfTopbarFlag) {
         return new String[]{"topbar", "perf", "clock", "elytrafinder"};
      } else {
         return perfTopbarFlag2 ? new String[]{"topbar", "perf", "clock", "autotpahere"} : new String[]{"topbar", "perf", "clock"};
      }
   }

   public void renderCard(DrawContext context, Snapshot snapshot, int scaledWidthValue, int rocketsBuyBound, String scaledWidthText, String scaledWidthText2) {
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
            this.updateFields(snapshot, scaledWidthValue, rocketsBuyBound, scaledWidthText, scaledWidthText2, nanoTime);
            if (this.fadePop.canTimeAmount(true, ++this.animationTicks)) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.renderCardContent(context, scaledWidth, smoothValue, nanoTime);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               this.skipAnimation = false;
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

   private static String truncateToWidth(String formatText, float hunterStatusLimit) {
      if (formatText != null && !formatText.isEmpty()) {
         if (NativeHud.getAmount3(formatText, 0.9F) <= hunterStatusLimit) {
            return formatText;
         } else {
            int end = 0;
            int availLength = formatText.length();

            while (end < availLength) {
               int end2 = end + availLength + 1 >>> 1;
               if (NativeHud.getAmount3(formatText.substring(0, end2) + "...", 0.9F) <= hunterStatusLimit) {
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

   public void resetAnimation() {
      this.animationTicks += 2L;
      this.skipAnimation = true;
      this.lastNanoTime = 0L;
      this.toneChangeNanos = 0L;
      this.warningActive = false;
      this.currentTone = null;
      this.widthSmooth.updateMax();
      this.yPositionSmooth.updateMax();
   }

   private void renderAccentGlow(float hunterStatusValue, float nativeHudValue, int hunterStatusValue3, long dangerPausedValue) {
      float peekPositionValue = NativeHud.getAmount4(hunterStatusValue);
      float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
      float hunterStatusValue7 = 0.5F + 0.5F * (float)Math.cos((float)(dangerPausedValue % 1800000000L) * 3.4906587E-9F);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(hunterStatusValue3, 0.22F * (0.35F + 0.65F * hunterStatusValue7)), 2.5F, true);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, 0.0F, hunterStatusValue3);
   }

   private void renderCardContent(DrawContext context, int hourRtpValue, float smoothValue, long dangerPausedValue) {
      VertexTextureFont vertexTexture = NativeHud.getFontRenderer();
      if (vertexTexture != this.cachedFont) {
         this.cachedFont = vertexTexture;
         this.titleWidth = NativeHud.getAmount3("Anchor Hunter", 0.95F);
         this.anchorsLabelWidth = NativeHud.getAmount3("Anchors", 0.78F);
         this.perHourLabelWidth = NativeHud.getAmount3("Per hour", 0.78F);
         this.nextRtpLabelWidth = NativeHud.getAmount3("Next RTP", 0.78F);
      }

      if (this.statusTextField.canMax(this.statusText.hashCode() * 31L + System.identityHashCode(vertexTexture))) {
         float hunterStatusLimit = 206.0F;
         this.statusTextField.handleAmountTime(truncateToWidth(CardStatusUtilsFlag.getOrDefault(this.statusText), hunterStatusLimit), 0.9F, false, !this.skipAnimation);
      }

      float hourRtpValue5 = 7.0F;
      float hourRtpValue6 = 4.0F;
      float maxRadarSize = 15.0F;
      float hourRtpBound = hourRtpValue5
         + this.anchorsLabelWidth
         + hourRtpValue6
         + this.anchorsCountField.checkedRocketsAmount
         + 10.0F
         + this.perHourLabelWidth
         + hourRtpValue6
         + this.perHourField.checkedRocketsAmount
         + 10.0F
         + this.nextRtpLabelWidth
         + hourRtpValue6
         + this.nextRtpField.checkedRocketsAmount
         + hourRtpValue5;
      float max8 = Math.max(
         NativeHud.getAmount2(this.titleWidth, this.warningField.checkedRocketsAmount), Math.max(hourRtpValue5 + 4.0F + 6.0F + this.statusTextField.textWidth + hourRtpValue5, hourRtpBound)
      );
      float bottomFactor = this.widthSmooth.getAmount(Math.max(160.0F, Math.min(230.0F, (float)Math.ceil(max8))), smoothValue, 0.08F);
      float hourRtpValue10 = NativeHud.getAmount(30.0F + 2.0F * maxRadarSize + 5.0F);
      float hudLimit = (hourRtpValue - bottomFactor) / 2.0F;
      float amountForFloat = this.yPositionSmooth.getAmountForFloat(ShowHUD.getAmounts(hudLimit, getCardTopY(), bottomFactor, hourRtpValue10, getBlockingHudElementIds()), smoothValue, 0.08F);
      float amount4 = ShowHUD.getAmount4("anchorhunter");
      float amountFloat = ShowHUD.getAmountFloat("anchorhunter");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("anchorhunter", hudLimit + amount4, amountForFloat + amountFloat, bottomFactor, hourRtpValue10);
      float peekPositionValue = NativeHud.getAmount4(hudLimit + amount4);
      float hudScaleValue = NativeHud.getAmount4(amountForFloat + amountFloat);
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.fadePop.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + bottomFactor / 2.0F, hudScaleValue + hourRtpValue10 / 2.0F, this.fadePop.getAmount());

      try {
         NativeHud.handle(CARD_STYLE_ID, peekPositionValue, hudScaleValue, bottomFactor, hourRtpValue10, 1.0F);
         float hourRtpValue17 = NativeHud.getAmount(context, peekPositionValue, hudScaleValue, bottomFactor, getBroadcastIcon(), "Anchor Hunter", this.warningField, this.pulse, 1.0F);
         float hunterStatusValue = peekPositionValue + hourRtpValue5;
         this.renderAccentGlow(hunterStatusValue, hourRtpValue17 + NativeHud.getAmount4((maxRadarSize - 4.0F) / 2.0F), this.getAccentColor(dangerPausedValue), dangerPausedValue);
         NativeHud.handle(
            this.statusTextField, hunterStatusValue + 4.0F + 6.0F, NativeHud.getAmount2(hourRtpValue17, maxRadarSize, 0.9F), 0.9F, this.currentTone == Tone.DANGER ? RED : NativeHud.OFF_WHITE
         );
         float nativeHudValue = hourRtpValue17 + maxRadarSize;
         float f13 = NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.78F);
         float roundOffset = NativeHud.getAmount2(nativeHudValue, maxRadarSize, 0.9F);
         float colorCheckedOffset = renderLabeledValue("Anchors", this.anchorsLabelWidth, this.anchorsCountField, hunterStatusValue, f13, roundOffset) + 10.0F;
         float scaleValue = renderLabeledValue("Per hour", this.perHourLabelWidth, this.perHourField, colorCheckedOffset, f13, roundOffset) + 10.0F;
         renderLabeledValue("Next RTP", this.nextRtpLabelWidth, this.nextRtpField, scaleValue, f13, roundOffset);
      } finally {
         NativeHud.renderPopMatrices(context, translateMatrices);
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (hudHotbar != null) {
            this.matrixStack.pop();
         }
      }
   }

   private static Identifier getBroadcastIcon() {
      if (broadcastIcon == null) {
         broadcastIcon = IcoGenUtils.getIdentifierForString("broadcast");
      }

      return broadcastIcon;
   }

   private static float getCardTopY() {
      int spotifySong = SpotifyHUD.getSpotifySong();
      float buildprogress = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      Module iliIiiliiiii = ModuleRegistry.getModule(ShowHUD.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal()) {
         buildprogress += 26.0F * ShowHUD.getAmountForString("buildprogress") + 6.0F;
      }

      return buildprogress;
   }

   private static float renderLabeledValue(String colorCheckedText, float colorCheckedValue, Field field, float scaleValue, float f13, float roundOffset) {
      NativeHud.handlePushScale(colorCheckedText, scaleValue, f13, 0.78F, NativeHud.GRAY);
      float colorChecked = scaleValue + colorCheckedValue + 4.0F + field.checkedRocketsAmount;
      NativeHud.handleAmount(field, colorChecked, roundOffset, 0.9F, NativeHud.OFF_WHITE);
      return colorChecked;
   }
}
