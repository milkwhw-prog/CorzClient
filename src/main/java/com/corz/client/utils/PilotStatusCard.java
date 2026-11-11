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
import com.corz.client.utils.PilotStatus;

public final class PilotStatusCard {
   private float smoothedDurability;
   private final Smooth etaSmoother;
   private float etaValueMinWidth;
   private final Pop cardPop;
   private static final String ELYTRA = "Elytra";
   private int cachedDurabilityColorArgb;
   private static final String SAMPLE_PERCENT = "100%";
   private final Field xpBottlesField;
   private boolean renderFailed;
   private static final String ROCKETS = "Rockets";
   private final Smooth durabilityWarnSmoother;
   private static final String SAMPLE_ETA = "00m 00s";
   public static final String ELYTRAPILOT = "elytrapilot";
   private final Smooth durabilityBarSmoother;
   private final Field activityField;
   private float durabilityWarnBlend;
   private static final String SAMPLE_ROCKET_COUNT = "000";
   private static final int topbarPerfValue = TwoDidUtils.getValue(0, 27);
   private static Identifier wingIdentifier;
   private static final String[] topbarPerfTexts = new String[]{"topbar", "perf", "clock"};
   private long lastRemainingKiloBlocks;
   private int currentToneColor;
   private final Smooth durabilityCriticalSmoother;
   private long toneTransitionStartNanos;
   private long renderCallCount;
   private final Smooth durabilitySmoother;
   private float durabilityCriticalBlend;
   private final Smooth rocketCountSmooth;
   private final Smooth rocketsWarnSmoother;
   private static final String SPEED = "Speed";
   private Color durabilityColor;
   private float percentTextWidth;
   private float titleWidth;
   private final Field etaField;
   private final Field remainingDistanceField;
   private static final String ETA = "ETA";
   private float rocketsValueMinWidth;
   private static final String THREE_DIGIT_SAMPLE = "000";
   private final Field destinationField;
   private static final String[] perfTopbarTexts = new String[]{"topbar", "perf", "clock", "elytrafinder"};
   private final Smooth remainingDistanceSmoother;
   private final Smooth routeBarSmoother;
   private final Smooth widthSmoother;
   private final Field speedValueField;
   private final Pulse pulse;
   private static final String[] autotpahereClockTexts = new String[]{"topbar", "perf", "clock", "autotpahere"};
   private final Smooth rocketsCriticalSmoother;
   private int transitionStartColor;
   private float rocketsWarnBlend;
   private float durabilityBarAlpha;
   private int statusModeIndex;
   private static final String LABEL_Y = "Y";
   private Color rocketsColor;
   private float altitudeLabelWidth;
   private long lastNanoTime;
   private long lastRemainingUpdateNanos;
   private static final String ELYTRA_PILOT = "Elytra Pilot";
   private float elytraLabelWidth;
   private float rocketsCriticalBlend;
   private float speedLabelWidth;
   private int fontGeneration;
   private static final String DASH_PLACEHOLDER = "-";
   private float routeProgress;
   private long criticalFlashStartNanos;
   private float speedValueMinWidth;
   private final Field elytraPercentField;
   private final Smooth speedSmoother;
   private float xpLabelWidth;
   private MatrixStack matrixStack = new MatrixStack();
   private static final String PERCENT_SUFFIX = "%";
   private float lastDurabilityFraction;
   private final Field altitudeField;
   private int cachedRocketsColorArgb;
   private static final String SPEED_SAMPLE_TEXT = "00 b/s";
   private VertexTextureFont vertexTexture;
   private boolean skipAnimation;
   private float routeBarAlpha;
   private float altitudeValueMinWidth;
   private final Smooth xpBottlesSmoother;
   private final Smooth routeProgressSmoother;
   private static final String ELLIPSIS = "...";
   private float etaLabelWidth;
   private String activityText;
   private static final String[] autotpahereElytrafinderTexts = new String[]{"topbar", "perf", "clock", "elytrafinder", "autotpahere"};
   private String destinationText;
   private final Smooth layoutSmoother;
   private static final String SPEED_UNIT_SUFFIX = " b/s";
   private final Field rocketsCountField;
   private final Smooth altitudeSmoother;
   private float rocketsLabelWidth;
   private static final String XP_LABEL = "XP";
   private static Identifier pinIdentifier;

   private float getDestinationReservedWidth() {
      return this.destinationField.isEmpty() ? 0.0F : 10.0F + this.destinationField.textWidth + 10.0F;
   }

   private void updateRemainingDistance(double smoothLimit, long nanoTime, float smoothValue) {
      if (smoothLimit >= 0.0 && !Double.isInfinite(smoothLimit)) {
         float blocks = this.remainingDistanceSmoother.getMax((float)smoothLimit, smoothValue, 0.15F, 0.5F);
         long time = (long)Math.floor(smoothLimit / 1000.0);
         if (this.lastRemainingKiloBlocks >= 0L && time < this.lastRemainingKiloBlocks) {
            this.pulse.updateTime();
         }

         this.lastRemainingKiloBlocks = time;
         if (this.lastRemainingUpdateNanos == 0L || nanoTime - this.lastRemainingUpdateNanos >= 100000000L) {
            this.lastRemainingUpdateNanos = nanoTime;
            if (this.remainingDistanceField.canMax(this.combineFieldKey(quantizeRemaining((double)blocks)))) {
               this.remainingDistanceField.handleAmountTime(Destination.getFormat(blocks), 0.8F, true, false);
            }
         }
      } else {
         this.remainingDistanceSmoother.updateMax();
         this.lastRemainingKiloBlocks = -1L;
         this.lastRemainingUpdateNanos = 0L;
         if (this.remainingDistanceField.canMax(this.combineFieldKey(-1L))) {
            this.remainingDistanceField.handleAmountTime("", 0.8F, true, false);
         }
      }
   }

   private static long quantizeRemaining(double pilotStatusValue) {
      if (pilotStatusValue < 1000.0) {
         return Math.round(pilotStatusValue);
      } else {
         return pilotStatusValue < 100000.0 ? 1000000L + Math.round(pilotStatusValue / 100.0) : 2000000L + Math.round(pilotStatusValue / 1000.0);
      }
   }

   public void renderStatusCard(DrawContext context, PilotStatus pilotStatus) {
      if (!this.renderFailed && context != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance != null && instance.player != null && instance.getWindow() != null) {
            try {
               this.renderWithHudScale(context, instance, pilotStatus == null ? PilotStatus.INSTANCE : pilotStatus);
            } catch (Throwable e) {
               this.renderFailed = true;
               this.matrixStack = new MatrixStack();
            }
         }
      }
   }

   public PilotStatusCard() {
      this.cardPop = new Pop();
      this.pulse = new Pulse();
      this.widthSmoother = new Smooth();
      this.layoutSmoother = new Smooth();
      this.remainingDistanceSmoother = new Smooth();
      this.speedSmoother = new Smooth();
      this.altitudeSmoother = new Smooth();
      this.etaSmoother = new Smooth();
      this.rocketCountSmooth = new Smooth();
      this.xpBottlesSmoother = new Smooth();
      this.durabilitySmoother = new Smooth();
      this.durabilityBarSmoother = new Smooth();
      this.routeProgressSmoother = new Smooth();
      this.routeBarSmoother = new Smooth();
      this.rocketsWarnSmoother = new Smooth();
      this.rocketsCriticalSmoother = new Smooth();
      this.durabilityWarnSmoother = new Smooth();
      this.durabilityCriticalSmoother = new Smooth();
      this.remainingDistanceField = new Field();
      this.activityField = new Field();
      this.destinationField = new Field();
      this.speedValueField = new Field();
      this.altitudeField = new Field();
      this.etaField = new Field();
      this.rocketsCountField = new Field();
      this.xpBottlesField = new Field();
      this.elytraPercentField = new Field();
      this.lastRemainingKiloBlocks = -1L;
      this.skipAnimation = true;
      this.statusModeIndex = -1;
      this.lastDurabilityFraction = 1.0F;
      this.activityText = "";
      this.destinationText = "";
   }

   private void renderWithHudScale(DrawContext context, MinecraftClient client, PilotStatus pilotStatus) {
      CorzClient.fontManager.setMatrixStack(this.matrixStack);
      MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
      double scaleFactor = client.getWindow().getScaleFactor();
      int calculateScaleFactor = client.getWindow().calculateScaleFactor(ShowHUD.getMax(client), client.forcesUnicodeFont());
      float scaledWidthDivisor = (float)(calculateScaleFactor / scaleFactor);
      boolean scaledWidthFlag = Math.abs(scaledWidthDivisor - 1.0F) > 1.0E-4F;
      int scaledWidth = scaledWidthFlag ? Math.round(client.getWindow().getScaledWidth() / scaledWidthDivisor) : client.getWindow().getScaledWidth();
      if (scaledWidthFlag) {
         CorzRoundedUtils.scaledWidth = scaledWidth;
         CorzRoundedUtils.scaledHeight = Math.round(client.getWindow().getScaledHeight() / scaledWidthDivisor);
      }

      try {
         float scaledWidthValue = ShowHUD.getTime();
         if (scaledWidthValue < 0.004F) {
            return;
         }

         long nanoTime = System.nanoTime();
         float smoothValue = this.lastNanoTime == 0L ? 0.016F : Math.min(0.05F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
         this.lastNanoTime = nanoTime;
         this.updateStatus(pilotStatus, nanoTime, smoothValue);
         if (this.cardPop.canTimeAmount(true, ++this.renderCallCount)) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(scaledWidthValue);

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

   private static float getBaseCardY() {
      int spotifySong = SpotifyHUD.getSpotifySong();
      float buildprogress = spotifySong > 0 ? spotifySong + 5.0F : 6.0F;
      Module iliIiiliiiii = ModuleRegistry.getModule(ShowHUD.class);
      if (iliIiiliiiii != null && iliIiiliiiii.isEnabled() && CardStatusUtilsActive.getCardStatusSnapshot().isLoadedTotal()) {
         buildprogress += 26.0F * ShowHUD.getAmountForString("buildprogress") + 6.0F;
      }

      return buildprogress;
   }

   private int getToneColor(long pilotStatusValue) {
      int releaseRightValue = switch (this.statusModeIndex) {
         case 1 -> -1535700;
         case 2 -> -1750963;
         case 3 -> -8393375;
         default -> NativeHud.getReleaseRight();
      };
      float pilotStatusLimit = this.toneTransitionStartNanos == 0L ? 1.0F : NativeHud.getMax((float)(pilotStatusValue - this.toneTransitionStartNanos) / 2.2E8F);
      if (pilotStatusLimit >= 1.0F) {
         this.toneTransitionStartNanos = 0L;
      }

      this.currentToneColor = pilotStatusLimit >= 1.0F ? releaseRightValue : CardMatrixUtils.getValue(this.transitionStartColor, releaseRightValue, NativeHud.getAmount5(pilotStatusLimit));
      return this.currentToneColor;
   }

   private static Identifier getWingIdentifier() {
      if (wingIdentifier == null) {
         wingIdentifier = IcoGenUtils.getIdentifierForString("wing");
      }

      return wingIdentifier;
   }

   private void renderStatusDot(float pilotStatusValue, float nativeHudValue, int pilotStatusValue3, long pilotStatusValue4) {
      float peekPositionValue = NativeHud.getAmount4(pilotStatusValue);
      float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
      float pilotStatusValue7 = 0.5F + 0.5F * (float)Math.cos((float)(pilotStatusValue4 % 1800000000L) * 3.4906587E-9F);
      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(pilotStatusValue3, 0.22F * (0.35F + 0.65F * pilotStatusValue7)), 2.5F, true);
      float pilotStatusLimit = this.getCriticalFlashFactor(pilotStatusValue4);
      if (pilotStatusLimit > 0.002F) {
         NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, NativeHud.getValue(pilotStatusValue3, 0.55F * pilotStatusLimit * pilotStatusLimit), 2.5F + 4.0F * (1.0F - pilotStatusLimit), true);
      }

      NativeHud.handlePeekPosition(peekPositionValue, matrixStackValue, 4.0F, 4.0F, 2.0F, 0.0F, pilotStatusValue3);
   }

   private static String truncateToWidth(String formatText, float pilotStatusLimit, float pilotStatusValue) {
      if (formatText != null && !formatText.isEmpty()) {
         if (NativeHud.getAmount3(formatText, pilotStatusValue) <= pilotStatusLimit) {
            return formatText;
         } else {
            int end = 0;
            int scaleLength = formatText.length();

            while (end < scaleLength) {
               int end2 = end + scaleLength + 1 >>> 1;
               if (NativeHud.getAmount3(formatText.substring(0, end2) + "...", pilotStatusValue) <= pilotStatusLimit) {
                  end = end2;
               } else {
                  scaleLength = end2 - 1;
               }
            }

            return formatText.substring(0, end).stripTrailing() + "...";
         }
      } else {
         return "";
      }
   }

   private static Identifier getPinIdentifier() {
      if (pinIdentifier == null) {
         pinIdentifier = IcoGenUtils.getIdentifierForString("pin");
      }

      return pinIdentifier;
   }

   private void renderRouteProgressBar(float pilotStatusValue, float nativeHudValue, float pilotStatusFactor) {
      if (!(pilotStatusFactor <= 0.0F)) {
         int releaseRight = NativeHud.getReleaseRight();
         float pilotStatusBound = 2.5F;
         float matrixStackValue = NativeHud.getAmount4(nativeHudValue);
         float pilotStatusValue4 = pilotStatusValue + Math.max(pilotStatusBound, pilotStatusFactor * NativeHud.getMax(this.routeProgress));
         if (this.routeProgress > 0.002F) {
            NativeHud.handlePeekPosition(NativeHud.getAmount4(pilotStatusValue4 - pilotStatusBound), matrixStackValue, pilotStatusBound, pilotStatusBound, pilotStatusBound / 2.0F, NativeHud.getValue(releaseRight, 0.35F), 2.0F, true);
         }

         NativeHud.handle(pilotStatusValue, nativeHudValue, pilotStatusFactor, pilotStatusBound, this.routeProgress, releaseRight);
      }
   }

   private float getCriticalFlashFactor(long pilotStatusValue) {
      if (this.criticalFlashStartNanos == 0L) {
         return 0.0F;
      } else {
         float pilotStatusLimit = (float)(pilotStatusValue - this.criticalFlashStartNanos) / 7.0E8F;
         if (pilotStatusLimit >= 1.0F) {
            this.criticalFlashStartNanos = 0L;
            return 0.0F;
         } else {
            return 1.0F - pilotStatusLimit;
         }
      }
   }

   public void resetState() {
      this.renderCallCount += 2L;
      this.skipAnimation = true;
      this.renderFailed = false;
      this.lastNanoTime = 0L;
      this.lastRemainingUpdateNanos = 0L;
      this.toneTransitionStartNanos = 0L;
      this.criticalFlashStartNanos = 0L;
      this.lastRemainingKiloBlocks = -1L;
      this.statusModeIndex = -1;
      this.lastDurabilityFraction = 1.0F;
      this.widthSmoother.updateMax();
      this.layoutSmoother.updateMax();
      this.remainingDistanceSmoother.updateMax();
      this.speedSmoother.updateMax();
      this.altitudeSmoother.updateMax();
      this.etaSmoother.updateMax();
      this.rocketCountSmooth.updateMax();
      this.xpBottlesSmoother.updateMax();
      this.durabilitySmoother.updateMax();
      this.durabilityBarSmoother.updateMax();
      this.routeProgressSmoother.updateMax();
      this.routeBarSmoother.updateMax();
      this.rocketsWarnSmoother.updateMax();
      this.rocketsCriticalSmoother.updateMax();
      this.durabilityWarnSmoother.updateMax();
      this.durabilityCriticalSmoother.updateMax();
   }

   private static String[] getNeighborHudElementIds() {
      boolean autotpahereFlag = isModuleEnabled(AutoElytraFinder.class);
      boolean moduleEnabled = isModuleEnabled(AutoTpaHere.class);
      return autotpahereFlag ? (moduleEnabled ? autotpahereElytrafinderTexts : perfTopbarTexts) : (moduleEnabled ? autotpahereClockTexts : topbarPerfTexts);
   }

   private long combineFieldKey(long pilotElytraValue) {
      return pilotElytraValue << 6 | this.fontGeneration & 63;
   }

   private void updateStatus(PilotStatus pilotStatus, long nanoTime, float smoothValue) {
      VertexTextureFont fontRenderer = NativeHud.getFontRenderer();
      if (fontRenderer != this.vertexTexture) {
         this.cacheLabelWidths(fontRenderer);
      }

      boolean smoothFlag = !this.skipAnimation;
      String activity = pilotStatus.activity();
      String destination = pilotStatus.destination();
      this.activityText = activity == null ? "" : activity;
      this.destinationText = destination == null ? "" : destination;
      int value = pilotStatus.tone();
      if (value != this.statusModeIndex) {
         boolean smoothFlag2 = this.statusModeIndex < 0 || this.skipAnimation;
         this.transitionStartColor = this.currentToneColor;
         this.statusModeIndex = value;
         this.toneTransitionStartNanos = smoothFlag2 ? 0L : nanoTime;
         if (value == 3 && !smoothFlag2) {
            this.criticalFlashStartNanos = nanoTime;
         }
      }

      if (this.destinationField.canMax(this.destinationText.hashCode() * 64L + (this.fontGeneration & 63))) {
         this.destinationField.handleAmountTime(truncateToWidth(CardStatusUtilsFlag.getOrDefault(this.destinationText), 92.0F, 0.78F), 0.78F, false, smoothFlag);
      }

      float destinationReservedWidth = this.getDestinationReservedWidth();
      if (this.activityField.canMax((this.activityText.hashCode() * 31L + Float.floatToIntBits(destinationReservedWidth)) * 64L + (this.fontGeneration & 63))) {
         float pilotStatusLimit = 216.0F - destinationReservedWidth;
         this.activityField.handleAmountTime(truncateToWidth(CardStatusUtilsFlag.getOrDefault(this.activityText), pilotStatusLimit, 0.9F), 0.9F, false, smoothFlag);
      }

      this.updateRemainingDistance(pilotStatus.remaining(), nanoTime, smoothValue);
      double speedBps = pilotStatus.speedBps();
      int round = Math.round(this.speedSmoother.getMax((float)(speedBps > 0.0 && speedBps < 1000000.0 ? speedBps : 0.0), smoothValue, 0.25F, 0.05F));
      if (this.speedValueField.canMax(this.combineFieldKey(round))) {
         this.speedValueField.handleAmountTime(round + " b/s", 0.9F, true, smoothFlag);
      }

      int round5 = Math.round(this.altitudeSmoother.getMax(pilotStatus.altitude(), smoothValue, 0.12F, 0.05F));
      if (this.altitudeField.canMax(this.combineFieldKey(round5))) {
         this.altitudeField.handleAmountTime(String.valueOf(round5), 0.9F, true, smoothFlag);
      }

      long etaSeconds = pilotStatus.etaSeconds();
      if (etaSeconds < 0L) {
         this.etaSmoother.updateMax();
         if (this.etaField.canMax(this.combineFieldKey(-1L))) {
            this.etaField.handleAmountTime("-", 0.9F, true, smoothFlag);
         }
      } else {
         long round6 = Math.round(this.etaSmoother.getMax((float)Math.min(etaSeconds, 360000000L), smoothValue, 0.6F, 0.3F));
         if (this.etaField.canMax(this.combineFieldKey(round6))) {
            this.etaField.handleAmountTime(Destination.formatText(round6), 0.9F, true, smoothFlag);
         }
      }

      int maxRockets = Math.max(0, pilotStatus.rockets());
      int round7 = Math.round(this.rocketCountSmooth.getMax(maxRockets, smoothValue, 0.1F, 0.3F));
      if (this.rocketsCountField.canMax(this.combineFieldKey(round7))) {
         this.rocketsCountField.handleAmountTime(String.valueOf(round7), 0.9F, true, smoothFlag);
      }

      int round8 = Math.round(this.xpBottlesSmoother.getMax(Math.max(0, pilotStatus.xpBottles()), smoothValue, 0.1F, 0.3F));
      if (this.xpBottlesField.canMax(this.combineFieldKey(round8))) {
         this.xpBottlesField.handleAmountTime(String.valueOf(round8), 0.9F, true, smoothFlag);
      }

      boolean smoothFlag3 = pilotStatus != PilotStatus.INSTANCE && maxRockets <= 8.0F;
      boolean smoothFlag4 = !smoothFlag3 && pilotStatus.rocketTarget() > 0 && maxRockets < pilotStatus.rocketTarget() / 4.0F;
      this.rocketsWarnBlend = this.rocketsWarnSmoother.getMax(smoothFlag4 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      this.rocketsCriticalBlend = this.rocketsCriticalSmoother.getMax(smoothFlag3 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      float amount = pilotStatus.getDurabilityFraction();
      boolean smoothFlag5 = amount >= 0.0F;
      if (smoothFlag5) {
         this.lastDurabilityFraction = amount;
      }

      this.durabilityBarAlpha = this.durabilityBarSmoother.getMax(smoothFlag5 ? 1.0F : 0.0F, smoothValue, 0.08F, 0.002F);
      this.smoothedDurability = this.durabilitySmoother.getMax(this.lastDurabilityFraction, smoothValue, 0.12F, 0.001F);
      int pilotElytraValue = smoothFlag5 ? Math.round(this.smoothedDurability * 100.0F) : -1;
      if (this.elytraPercentField.canMax(this.combineFieldKey(pilotElytraValue))) {
         this.elytraPercentField.handleAmountTime(pilotElytraValue < 0 ? "-" : pilotElytraValue + "%", 0.9F, true, smoothFlag);
      }

      boolean smoothFlag6 = smoothFlag5 && amount <= 0.1F;
      boolean smoothFlag7 = smoothFlag5 && !smoothFlag6 && amount < 0.25F;
      this.durabilityWarnBlend = this.durabilityWarnSmoother.getMax(smoothFlag7 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      this.durabilityCriticalBlend = this.durabilityCriticalSmoother.getMax(smoothFlag6 ? 1.0F : 0.0F, smoothValue, 0.1F, 0.002F);
      boolean smoothFlag8 = pilotStatus.total() > 0.0 && pilotStatus.remaining() >= 0.0;
      this.routeBarAlpha = this.routeBarSmoother.getMax(smoothFlag8 ? 1.0F : 0.0F, smoothValue, 0.08F, 0.002F);
      if (smoothFlag8) {
         this.routeProgress = this.routeProgressSmoother.getMax(pilotStatus.getRouteProgress(), smoothValue, 0.15F, 5.0E-4F);
      }
   }

   private void cacheLabelWidths(VertexTextureFont vertexTexture) {
      this.vertexTexture = vertexTexture;
      this.fontGeneration++;
      this.titleWidth = NativeHud.getAmount3("Elytra Pilot", 0.95F);
      this.speedLabelWidth = NativeHud.getAmount3("Speed", 0.78F);
      this.altitudeLabelWidth = NativeHud.getAmount3("Y", 0.78F);
      this.etaLabelWidth = NativeHud.getAmount3("ETA", 0.78F);
      this.rocketsLabelWidth = NativeHud.getAmount3("Rockets", 0.78F);
      this.xpLabelWidth = NativeHud.getAmount3("XP", 0.78F);
      this.elytraLabelWidth = NativeHud.getAmount3("Elytra", 0.78F);
      this.speedValueMinWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("00 b/s", 0.9F));
      this.altitudeValueMinWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("000", 0.9F));
      this.etaValueMinWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("00m 00s", 0.9F));
      this.rocketsValueMinWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("000", 0.9F));
      this.percentTextWidth = (float)Math.ceil(NativeHud.getAmountForStringFloat("100%", 0.9F));
   }

   private Color getDurabilityColor() {
      int value = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-920587, -1535700, this.durabilityWarnBlend), -1750963, this.durabilityCriticalBlend);
      if (this.durabilityColor == null || value != this.cachedDurabilityColorArgb) {
         this.cachedDurabilityColorArgb = value;
         this.durabilityColor = new Color(value, true);
      }

      return this.durabilityColor;
   }

   private Color getRocketsColor() {
      int value = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-920587, -1535700, this.rocketsWarnBlend), -1750963, this.rocketsCriticalBlend);
      if (this.rocketsColor == null || value != this.cachedRocketsColorArgb) {
         this.cachedRocketsColorArgb = value;
         this.rocketsColor = new Color(value, true);
      }

      return this.rocketsColor;
   }

   private static void drawLabeledField(String colorText, float colorValue, Field field, float nativeHudValue, float f13, float roundOffset, Color color) {
      NativeHud.handlePushScale(colorText, colorValue, f13, 0.78F, NativeHud.GRAY);
      NativeHud.handleAmount(field, nativeHudValue, roundOffset, 0.9F, color);
   }

   private static boolean isModuleEnabled(Class<? extends Module> class2) {
      Module iliIiiliiiii = ModuleRegistry.getModule(class2);
      return iliIiiliiiii != null && iliIiiliiiii.isEnabled();
   }

   private void renderCardContent(DrawContext context, int rocketsElytraValue, float smoothValue, long pilotStatusValue) {
      float rocketsElytraValue4 = 7.0F;
      float rocketsElytraValue5 = 4.0F;
      float rocketsElytraValue6 = 15.0F;
      float max7 = Math.max(
         this.speedLabelWidth + rocketsElytraValue5 + Math.max(this.speedValueField.checkedRocketsAmount, this.speedValueMinWidth),
         this.rocketsLabelWidth + rocketsElytraValue5 + Math.max(this.rocketsCountField.checkedRocketsAmount, this.rocketsValueMinWidth)
      );
      float max8 = Math.max(
         this.altitudeLabelWidth + rocketsElytraValue5 + Math.max(this.altitudeField.checkedRocketsAmount, this.altitudeValueMinWidth),
         this.xpLabelWidth + rocketsElytraValue5 + Math.max(this.xpBottlesField.checkedRocketsAmount, this.rocketsValueMinWidth)
      );
      float max9 = Math.max(
         this.etaLabelWidth + rocketsElytraValue5 + Math.max(this.etaField.checkedRocketsAmount, this.etaValueMinWidth),
         this.elytraLabelWidth + rocketsElytraValue5 + Math.max(this.elytraPercentField.checkedRocketsAmount, this.percentTextWidth)
      );
      float rocketsElytraBound = rocketsElytraValue4 + max7 + max8 + max9 + 20.0F + rocketsElytraValue4;
      float max10 = Math.max(
         NativeHud.getAmount2(this.titleWidth, this.remainingDistanceField.checkedRocketsAmount),
         Math.max(rocketsElytraValue4 + 4.0F + 6.0F + this.activityField.textWidth + this.getDestinationReservedWidth() + rocketsElytraValue4, rocketsElytraBound)
      );
      float bottomFactor = this.widthSmoother.getAmount(Math.max(170.0F, Math.min(240.0F, (float)Math.ceil(max10))), smoothValue, 0.08F);
      float maxElytraAmount = Math.max(this.routeBarAlpha, this.durabilityBarAlpha);
      float maxRadarSize = NativeHud.getAmount(30.0F + 3.0F * rocketsElytraValue6 + maxElytraAmount * 3.5F + 5.0F);
      float hudLimit = (rocketsElytraValue - bottomFactor) / 2.0F;
      float amountForFloat = this.layoutSmoother.getAmountForFloat(ShowHUD.getAmounts(hudLimit, getBaseCardY(), bottomFactor, maxRadarSize, getNeighborHudElementIds()), smoothValue, 0.08F);
      float amount4 = ShowHUD.getAmount4("elytrapilot");
      float amountFloat = ShowHUD.getAmountFloat("elytrapilot");
      ShowHUD.HudScale hudHotbar = ShowHUD.getHudHotbar("elytrapilot", hudLimit + amount4, amountForFloat + amountFloat, bottomFactor, maxRadarSize);
      float peekPositionValue = NativeHud.getAmount4(hudLimit + amount4);
      float hudScaleValue = NativeHud.getAmount4(amountForFloat + amountFloat);
      if (hudHotbar != null) {
         hudHotbar.handleAmountTranslate(this.matrixStack);
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.cardPop.getPopProgress());
      boolean translateMatrices = NativeHud.canTranslateMatrices(context, peekPositionValue + bottomFactor / 2.0F, hudScaleValue + maxRadarSize / 2.0F, this.cardPop.getAmount());

      try {
         NativeHud.handle(topbarPerfValue, peekPositionValue, hudScaleValue, bottomFactor, maxRadarSize, 1.0F);
         float rocketsElytraValue19 = NativeHud.getAmount(context, peekPositionValue, hudScaleValue, bottomFactor, getWingIdentifier(), "Elytra Pilot", this.remainingDistanceField, this.pulse, 1.0F);
         float rocketsElytraValue20 = peekPositionValue + rocketsElytraValue4;
         float rocketsElytraValue21 = peekPositionValue + bottomFactor - rocketsElytraValue4;
         this.renderStatusDot(rocketsElytraValue20, rocketsElytraValue19 + NativeHud.getAmount4((rocketsElytraValue6 - 4.0F) / 2.0F), this.getToneColor(pilotStatusValue), pilotStatusValue);
         NativeHud.handle(this.activityField, rocketsElytraValue20 + 4.0F + 6.0F, NativeHud.getAmount2(rocketsElytraValue19, rocketsElytraValue6, 0.9F), 0.9F, NativeHud.OFF_WHITE);
         if (!this.destinationField.isEmpty()) {
            NativeHud.handleAmount(this.destinationField, rocketsElytraValue21, NativeHud.getAmount2(rocketsElytraValue19, rocketsElytraValue6, 0.78F), 0.78F, NativeHud.GRAY);
            NativeHud.handle(
               getPinIdentifier(),
               NativeHud.getAmount4(rocketsElytraValue21 - this.destinationField.textWidth - 2.0F - 8.0F),
               NativeHud.getAmount4(rocketsElytraValue19 + (rocketsElytraValue6 - 8.0F) / 2.0F),
               8.0F,
               NativeHud.GRAY
            );
         }

         float max22 = Math.max(10.0F, (rocketsElytraValue21 - rocketsElytraValue20 - max7 - max8 - max9) / 2.0F);
         float rocketsElytraValue23 = rocketsElytraValue20 + max7 + max22;
         float colorValue = rocketsElytraValue23 + max8 + max22;
         float rocketsElytraValue25 = rocketsElytraValue20 + max7;
         float rocketsElytraValue26 = rocketsElytraValue23 + max8;
         float rocketsElytraValue27 = colorValue + max9;
         float nativeHudValue = rocketsElytraValue19 + rocketsElytraValue6;
         float f13 = NativeHud.getAmount2(nativeHudValue, rocketsElytraValue6, 0.78F);
         float roundOffset = NativeHud.getAmount2(nativeHudValue, rocketsElytraValue6, 0.9F);
         drawLabeledField("Speed", rocketsElytraValue20, this.speedValueField, rocketsElytraValue25, f13, roundOffset, NativeHud.OFF_WHITE);
         drawLabeledField("Y", rocketsElytraValue23, this.altitudeField, rocketsElytraValue26, f13, roundOffset, NativeHud.OFF_WHITE);
         drawLabeledField("ETA", colorValue, this.etaField, rocketsElytraValue27, f13, roundOffset, NativeHud.OFF_WHITE);
         float rocketsElytraValue31 = nativeHudValue + rocketsElytraValue6;
         float rocketsElytraValue32 = NativeHud.getAmount2(rocketsElytraValue31, rocketsElytraValue6, 0.78F);
         float rocketsElytraValue33 = NativeHud.getAmount2(rocketsElytraValue31, rocketsElytraValue6, 0.9F);
         drawLabeledField("Rockets", rocketsElytraValue20, this.rocketsCountField, rocketsElytraValue25, rocketsElytraValue32, rocketsElytraValue33, this.getRocketsColor());
         drawLabeledField("XP", rocketsElytraValue23, this.xpBottlesField, rocketsElytraValue26, rocketsElytraValue32, rocketsElytraValue33, NativeHud.OFF_WHITE);
         drawLabeledField("Elytra", colorValue, this.elytraPercentField, rocketsElytraValue27, rocketsElytraValue32, rocketsElytraValue33, this.getDurabilityColor());
         float rocketsElytraValue34 = rocketsElytraValue31 + rocketsElytraValue6 + 1.0F;
         if (this.routeBarAlpha > 0.002F) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(this.routeBarAlpha);

            try {
               this.renderRouteProgressBar(rocketsElytraValue20, rocketsElytraValue34, rocketsElytraValue26 - rocketsElytraValue20);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }

         if (this.durabilityBarAlpha > 0.002F) {
            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(this.durabilityBarAlpha);

            try {
               NativeHud.handle(colorValue, rocketsElytraValue34, rocketsElytraValue27 - colorValue, 2.5F, this.smoothedDurability, NativeHud.getFlags(this.smoothedDurability));
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
}
