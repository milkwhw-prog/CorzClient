package com.corz.client.gui;

import com.corz.client.render.BetaListening;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixRender;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.MatrixStackUtilsAmount.Entrance;
import com.corz.client.render.MatrixStackUtilsAmount.Hits;
import com.corz.client.render.MatrixStackUtilsAmount.Swap;
import com.corz.client.render.RenderMatrixUtilsRelease;
import com.corz.client.utils.ConstAmountUtils.Columns;
import com.corz.client.utils.ConstAmountUtils.Rect;
import com.corz.client.utils.PopsTaken.Measure;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.categories.settings.ClientSounds;
import ai.auth.Module;
import com.corz.client.utils.ConstAmountUtils;
import com.corz.client.utils.CpxVillagerData;
import com.corz.client.utils.MinecraftBotUtils;
import com.corz.client.utils.PngIcons;
import com.corz.client.utils.PopsTaken;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.ScissorTgtUtils;
import com.corz.client.utils.StandbyDanger;

public final class BotTgt {
   private final MatrixScissor matrixScissor;
   private long scissorStartNanos;
   private static final String SOON_TEXT_UPPER = "SOON";
   private static final String COMING_FUTURE_UPDATE = "Coming in a future update.";
   private static final int DANGER_TONE_COLOR = CardMatrixUtils.getValue(-1750963, -920587, 0.3);
   private static final String BOTS_SHOW = "Bots will show up here.";
   private final Swap timeMax;
   private final Map<String, Double> utilityDoubleMap;
   private int matrixScissorValue;
   private String pvpBotText;
   private float tgtTextWidth;
   private final Swap tgtSwap;
   private final Swap botTgtSwap;
   private boolean utilityActive;
   private int utilityInspectorValue;
   private String tgtWidthText;
   private final PopsTaken popsTaken;
   private long botTgtValue;
   private static final String EMPTY = "empty";
   private final Anims pvpBotAnims;
   private int panelY;
   private final Swap tgtWidthSwap;
   private long lastResetNanos;
   private final Map<Module, BetaListening> botUtilityMap = new IdentityHashMap<>();
   private static final Measure TEXT_MEASURE = botTgtText -> MatrixStackUtilsAmount.getAmount(botTgtText, 0.8F, 1.0F);
   private float panelFadeProgress;
   private float botTgtAmount;
   private float tgtAmount;
   private boolean matrixScissorActive;
   private final Entrance matrixScissorEntrance;
   private float tgtWidthAmount;
   private boolean utilityInspectorActive;
   private float width;
   private float savedDisableAmount;
   private Columns matrixScissorColumns;
   private final List<Module> copyOf;
   private int pvpBotValue;
   private long showDeadlineNanos;
   private long tgtValue;
   private final List<BetaListening> betaListeningBetaListeningLi = new ArrayList<>(1);
   private float botsAmount;
   private static final String UTILITY_BOT = "UTILITY BOT";
   private String tgtText;
   private int utilityValue;
   private float widthTgtWidth;
   private long nanoTimeBots;
   private int inspectorHeight;
   private static final String BOT = "bot";
   private static final String DEVELOPMENT = "In development";
   private long nanoTimeUtility;
   private float fadeOutAlpha;
   private final MatrixRender matrixRender;
   private String botTgtText;
   private float scissorDisableAmount;
   private static final String PVP_BOT = "PVP BOT";
   private int matrixValue;
   private MinecraftBotUtils.Rail pvpKind;
   private static final String BOTS = "No bots yet";
   private String pvpBotFormattedText;
   private boolean matrixActive;
   private static final String SOON_TEXT_LOWER = "soon";
   private float animDurationMs;
   private float fadeStartAmount;
   private static final int UNUSED_ONE = 1;

   private void renderPvpBot(DrawContext context, MinecraftBotUtils.Slot slot2, float pvpBotValue, float downloadCommunityValue, float w, float segmentSlider) {
      CpxVillagerData look = slot2.look();
      byte pvpBotValue5 = 8;
      int constValue = (int)w;
      float pvpBotValue6 = MatrixStackUtilsAmount.getAmount("SOON");
      float matrixStackValue = pvpBotValue + ConstAmountUtils.getValue(constValue) - pvpBotValue6;
      if (this.pvpBotValue != this.utilityValue) {
         this.pvpBotValue = this.utilityValue;
         String pvpBotText = look.name() == null ? look.id() : look.name();
         this.pvpBotFormattedText = MatrixStackUtilsAmount.getFontRenderer().formatText(pvpBotText, Math.max(0.0F, matrixStackValue - pvpBotValue - pvpBotValue5 * 2.0F) / 1.22F);
         this.pvpBotText = MatrixStackUtilsAmount.getFontRenderer().formatText(look.tagline(), (w - pvpBotValue5 * 2.0F) / 0.82F);
      }

      MatrixStackUtilsAmount.getPopPush("PVP BOT", pvpBotValue + pvpBotValue5, MatrixStackUtilsAmount.getAmount(downloadCommunityValue + 7.0F, 10.0F, 0.8F), 0.8F, 1.0F, -11117467);
      float priceValue = MatrixStackUtilsAmount.getAmount(downloadCommunityValue + 18.0F, 18.0F, 1.22F);
      MatrixStackUtilsAmount.handlePushScale(this.pvpBotFormattedText, pvpBotValue + pvpBotValue5, priceValue, 1.22F, MatrixStackUtilsAmount.getValue(-920587, 0.75F));
      MatrixStackUtilsAmount.handlePushScale(
         this.pvpBotFormattedText, pvpBotValue + pvpBotValue5 + MatrixPeekUtilsRender.getAmount(), priceValue, 1.22F, MatrixStackUtilsAmount.getValue(-920587, 0.41250002F)
      );
      MatrixStackUtilsAmount.getAmount(context, "SOON", matrixStackValue, downloadCommunityValue + 18.0F + 3.5F, MatrixEspUtils.releaseRightValue);
      MatrixStackUtilsAmount.handlePushScale(this.pvpBotText, pvpBotValue + pvpBotValue5, MatrixStackUtilsAmount.getAmount(downloadCommunityValue + 37.0F, 10.0F, 0.82F), 0.82F, -7630437);
      RenderMatrixUtilsRelease.renderForDrawContextInt(context, (int)pvpBotValue + pvpBotValue5, (int)downloadCommunityValue + 54, constValue - pvpBotValue5 * 2, 1.0);
      MatrixStackUtilsAmount.renderMatricesPop(
         context,
         Hits.INSTANCE,
         this.pvpBotAnims,
         "soon",
         pvpBotValue,
         downloadCommunityValue + 58.0F,
         w,
         ConstAmountUtils.getMaxInt((int)segmentSlider),
         IcoGenUtils.getIcoClockIcoClockActive(),
         "In development",
         "Coming in a future update.",
         null,
         (int)pvpBotValue - 4000,
         (int)downloadCommunityValue,
         null
      );
   }

   private void handleAmountTime(long nanoTime, float amount, float amount2, float botTgtValue) {
      this.botTgtValue = nanoTime;
      this.animDurationMs = amount;
      this.botTgtAmount = amount2;
      this.showDeadlineNanos = nanoTime + getTime(amount + botTgtValue);
      this.matrixRender.updateMatricesCpxStandbyDanger();
   }

   private static void renderPopMatrices(DrawContext context, float matricesValue) {
      if (matricesValue != 0.0F) {
         context.getMatrices().popMatrix();
         MatrixPeekUtilsRender.getMatrixStack().pop();
      }
   }

   public void handleScissorInspector(double scissorInspectorValue, double heightAmount, double keybindBindValue) {
      BetaListening betaListening = this.getBetaListening();
      if (betaListening != null && betaListening.isFlagTime()) {
         betaListening.canKeybindBind(scissorInspectorValue, heightAmount, keybindBindValue);
      } else {
         double py = scissorInspectorValue - this.matrixScissorValue;
         double px = heightAmount - this.panelY;
         if (this.matrixScissorColumns.rail().canFlag(py, px)) {
            this.matrixScissor.canAmountMax(keybindBindValue);
         } else if (betaListening != null && this.matrixScissorColumns.inspector().canFlag(py, px)) {
            betaListening.canKeybindBind(scissorInspectorValue, heightAmount, keybindBindValue);
         }
      }
   }

   private void handleToneHint(BetaListening betaListening, MinecraftBotUtils.Slot slot2) {
      String tagline = slot2.look().tagline();
      int toneHintValue = -7630437;
      Module module = slot2.module();
      if (module != null && module.isEnabled() && module instanceof ScissorTgtHandler scissorTgtHandler) {
         Snapshot instance = ScissorTgtUtils.getInstance(scissorTgtHandler);
         if (instance.hint() != null && (instance.tone() != Tone.ACTIVE || !slot2.look().isPvpKind())) {
            tagline = instance.hint();
            toneHintValue = instance.tone() == Tone.DANGER ? DANGER_TONE_COLOR : -5854026;
         }
      }

      betaListening.handleTextAmount(tagline, toneHintValue);
   }

   private void renderScissorDisable(DrawContext context, float scissorDisableValue, float peekPositionFactor, float matricesCpxBound, float hashHuntBound, CpxVillagerData cpxVillagerData, boolean scissorDisableFlag, Snapshot snapshot) {
      boolean scissorDisableFlag2 = this.scissorDisableAmount >= 0.0F;
      if (scissorDisableFlag2) {
         context.enableScissor(
            (int)Math.floor(scissorDisableValue), (int)Math.floor(peekPositionFactor + ConstAmountUtils.getAmount(this.scissorDisableAmount)), (int)Math.ceil(scissorDisableValue + matricesCpxBound), (int)Math.ceil(peekPositionFactor + hashHuntBound)
         );
      }

      float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.panelFadeProgress);

      try {
         this.matrixRender.renderCrystalsAmount(context, scissorDisableValue, peekPositionFactor, matricesCpxBound, hashHuntBound, 7.0F, cpxVillagerData, scissorDisableFlag, snapshot);
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         if (scissorDisableFlag2) {
            context.disableScissor();
         }
      }
   }

   public void updateTimeMax() {
      long nanoTime = System.nanoTime();
      this.matrixScissorEntrance.updateTimeFlag();
      this.lastResetNanos = nanoTime;
      this.scissorStartNanos = nanoTime + getTime(120.0F);
      this.tgtValue = nanoTime + getTime(300.0F);
      this.nanoTimeBots = nanoTime + getTime(80.0F);
      this.matrixScissorActive = true;
      this.botTgtValue = 0L;
      this.nanoTimeUtility = 0L;
      this.showDeadlineNanos = 0L;
      this.panelFadeProgress = 0.0F;
      this.tgtAmount = 0.0F;
      this.matrixRender.updateMatricesCpxStandbyDanger();
      this.popsTaken.updateCpxMenu();
      this.botTgtSwap.updateTextMax();
      this.tgtWidthSwap.updateTextMax();
      this.timeMax.updateTextMax();
      this.tgtSwap.updateTextMax();
      this.matrixScissor.handleAmountsMax(this.utilityValue);
      if (this.utilityValue >= 0) {
         this.handleRenderMatrix(this.utilityValue);
      }
   }

   public void renderMatrixScissor(DrawContext context) {
      BetaListening betaListening = this.getBetaListening();
      boolean matrixScissorFlag = betaListening != null && betaListening.isFlagTime();
      if (matrixScissorFlag) {
         this.matrixRender.updateTotemsAmount();
         this.matrixScissor.update();
      }

      this.matrixScissor.renderTextsLook(context, this.matrixScissorValue, !matrixScissorFlag && !this.matrixRender.isTotemsActive());
   }

   public List<BetaListening> getItems() {
      return this.betaListeningBetaListeningLi;
   }

   private void renderAmountTgt(DrawContext context, float tgtValue, float peekPositionFactor, CpxVillagerData cpxVillagerData, long nanoTime) {
      float tgtLimit = getAmount(this.tgtValue, nanoTime, 150.0F);
      if (tgtLimit >= 1.0F) {
         this.tgtValue = 0L;
      }

      this.tgtAmount = (float)MatrixIdentifierUtils.getAmount(this.tgtAmount, this.popsTaken.isText() ? 1.0 : 0.0, 12.0, MatrixClearUtils.getAmount());
      if (this.tgtAmount < 0.002F) {
         this.tgtAmount = 0.0F;
      }

      if (!(tgtLimit <= 0.002F)) {
         float matrixStackFactor = 0.8F;
         float tgtValue5 = 1.0F;
         float tgtValue6 = tgtValue + 8.0F;
         float tgtValue7 = tgtValue + 158.0F;
         float tgtValue8 = tgtValue + 83.0F;
         float priceValue = MatrixStackUtilsAmount.getAmount(peekPositionFactor + 8.0F, 8.0F, matrixStackFactor);
         float tgtValue10 = MatrixStackUtilsAmount.getAmount(peekPositionFactor + 298.0F, 8.0F, matrixStackFactor);
         float tgtValue11 = this.matrixRender.isTotemsActive() ? 0.0F : this.matrixScissor.getTime();
         float amountMax = this.matrixScissor.getAmountMax();
         float tgtLimit2 = this.matrixScissorValue + 38 + this.matrixScissor.getAmounts();
         float tgtValue12 = getAmount(tgtValue11, amountMax, peekPositionFactor + 8.0F);
         float tgtValue13 = getAmount(tgtValue11, amountMax, peekPositionFactor + 298.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(tgtLimit);

         try {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(1.0F - tgtValue12);

            try {
               this.botTgtSwap.getTextAmount(this.popsTaken.getVillagerCode(), tgtValue6, priceValue, matrixStackFactor, tgtValue5, -11117467, tgtValue8, false);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }

            float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(tgtLimit2 > tgtValue7 - this.widthTgtWidth ? 1.0F - tgtValue12 : 1.0F);

            try {
               this.tgtWidthSwap.getTextAmount(this.tgtWidthText, tgtValue7 - 72.0F, priceValue, matrixStackFactor, tgtValue5, this.getRenderMatrix(cpxVillagerData), tgtValue7, true);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
            }

            if (!(this.tgtAmount <= 0.002F)) {
               float panelOverlay4 = CorzRoundedUtils.getPanelOverlay(this.tgtAmount * (1.0F - tgtValue13));

               try {
                  if (!this.tgtText.isEmpty()) {
                     float swapValue = tgtValue6 + MatrixStackUtilsAmount.getPopPush("TGT ", tgtValue6, tgtValue10, matrixStackFactor, tgtValue5, -11117467) + tgtValue5;
                     this.timeMax.getTextAmount(this.tgtText, swapValue, tgtValue10, matrixStackFactor, tgtValue5, -7630437, swapValue + this.tgtTextWidth, false);
                  }
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay4);
               }

               float panelOverlay5 = CorzRoundedUtils.getPanelOverlay(this.tgtAmount * (tgtLimit2 > tgtValue7 - this.width ? 1.0F - tgtValue13 : 1.0F));

               try {
                  this.tgtSwap.getTextAmount(this.botTgtText, tgtValue7 - this.tgtWidthAmount, tgtValue10, matrixStackFactor, tgtValue5, -11117467, tgtValue7, true);
                  return;
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay5);
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void handleRenderMatrix(int max) {
      this.matrixValue = max;
      this.matrixActive = false;
      MinecraftBotUtils.Slot slots = this.pvpKind.getSlots(max);
      if (slots != null) {
         this.matrixRender.handleCpxEmpty(slots.look());
      }
   }

   public void handle(Module Module) {
      int slotsModule = this.pvpKind.getSlotsModule(Module);
      if (slotsModule >= 0) {
         this.handleMaxTime(slotsModule, false);
      }
   }

   private void handleMaxTime(int max, boolean botTgtFlag) {
      int slots = this.pvpKind.getSlots();
      if (slots != 0) {
         max = Math.max(0, Math.min(slots - 1, max));
         if (max != this.utilityValue) {
            BetaListening betaListening = this.getBetaListening();
            MinecraftBotUtils.Slot slots2 = this.pvpKind.getSlots(this.utilityValue);
            if (betaListening != null) {
               if (slots2 != null) {
                  this.utilityDoubleMap.put(slots2.formatLook(), betaListening.getMaxBetaListeningBound());
               }

               betaListening.updateKeybindScissorSettingCompone();
               betaListening.handleFlagTimeForFlag(false);
            }

            this.utilityValue = max;
            MinecraftBotUtils.Slot slots3 = this.pvpKind.getSlots(max);
            PngIcons.getInstance().setText(slots3.formatLook());
            BetaListening utilityModule = this.getUtilityModule(max);
            this.betaListeningBetaListeningLi.clear();
            if (utilityModule != null) {
               Double double2 = this.utilityDoubleMap.get(slots3.formatLook());
               utilityModule.handleFlagTimeForFlag(true);
               utilityModule.handleMaxAmount(double2 == null ? 0.0 : double2);
               this.betaListeningBetaListeningLi.add(utilityModule);
            }

            this.pvpBotValue = -1;
            this.matrixScissor.handleAmountMax(max);
            long nanoTime = System.nanoTime();
            if (!botTgtFlag) {
               this.matrixScissor.handleAmountsMax(max);
               if (this.matrixValue != max) {
                  this.handleRenderMatrix(max);
               }
            } else {
               ClientSounds.updateCardOpen();
               this.nanoTimeBots = nanoTime;
               if (this.matrixScissorActive || this.matrixValue < 0) {
                  this.handleRenderMatrix(max);
               } else if (this.nanoTimeUtility == 0L && this.matrixValue != max) {
                  this.nanoTimeUtility = nanoTime;
                  this.fadeStartAmount = this.panelFadeProgress;
                  this.savedDisableAmount = this.scissorDisableAmount;
                  this.botTgtValue = 0L;
                  this.showDeadlineNanos = 0L;
               }
            }
         }
      }
   }

   public void renderMaxTime(DrawContext context, int botTgtValue, int botTgtValue2, int constValue, int botTgtValue4, int botTgtValue5, int heightValue, float modulesClearValue) {
      long nanoTime = System.nanoTime();
      boolean timeFlag = this.matrixScissorEntrance.isTimeFlag();
      this.updateMaxUtility();
      this.handleUtilityInspector(botTgtValue, botTgtValue2, constValue, botTgtValue4);
      if (timeFlag && !this.matrixScissorEntrance.isTime() && !this.matrixScissorActive && this.nanoTimeUtility == 0L && this.botTgtValue == 0L && this.matrixValue >= 0) {
         this.handleRenderMatrix(this.matrixValue);
         this.handleAmountTime(nanoTime, 260.0F, 0.0F, 90.0F);
      }

      if (this.matrixActive && this.matrixValue >= 0) {
         this.handleRenderMatrix(this.matrixValue);
      }

      this.handleTimeAmount(nanoTime);
      this.renderMatrixAmount(context, botTgtValue5, heightValue);
      this.renderMatrixScissor(context, botTgtValue5, heightValue, nanoTime);
      this.renderBotsAmount(context, botTgtValue5, heightValue, modulesClearValue, nanoTime);
   }

   private void handleTimeAmount(long botTgtLimit) {
      float max = 1.0F;
      this.scissorDisableAmount = -1.0F;
      this.fadeOutAlpha = 0.0F;
      if (this.nanoTimeUtility != 0L) {
         float botTgtValue = (float)(botTgtLimit - this.nanoTimeUtility) / 1000000.0F / 90.0F;
         if (botTgtValue < 1.0F) {
            max = this.fadeStartAmount * (1.0F - MatrixStackUtilsAmount.getMax(botTgtValue));
            this.scissorDisableAmount = this.savedDisableAmount;
         } else {
            this.nanoTimeUtility = 0L;
            this.handleRenderMatrix(this.utilityValue);
            this.handleAmountTime(botTgtLimit, 260.0F, 0.0F, 60.0F);
         }
      }

      if (this.matrixScissorActive) {
         float millis = (float)(botTgtLimit - this.lastResetNanos) / 1000000.0F;
         if (millis >= 260.0F && CorzRoundedUtils.getPanelOverlay() >= 0.95F) {
            this.matrixScissorActive = false;
            this.handleAmountTime(botTgtLimit, 380.0F, 1.0F, 90.0F);
         } else {
            max = millis >= 120.0F ? 1.0F : 0.0F;
         }
      }

      if (this.botTgtValue != 0L) {
         float matrixStackFactor = MatrixStackUtilsAmount.getMax((float)(botTgtLimit - this.botTgtValue) / 1000000.0F / this.animDurationMs);
         this.scissorDisableAmount = MatrixStackUtilsAmount.getAmount3(matrixStackFactor);
         max = Math.max(this.botTgtAmount, MatrixStackUtilsAmount.getAmountForFloat(matrixStackFactor / 0.4F));
         this.fadeOutAlpha = matrixStackFactor < 0.75F ? 1.0F : (1.0F - matrixStackFactor) / 0.25F;
         if (matrixStackFactor >= 1.0F) {
            this.botTgtValue = 0L;
            this.scissorDisableAmount = -1.0F;
            this.fadeOutAlpha = 0.0F;
         }
      }

      boolean botTgtFlag = this.showDeadlineNanos != 0L && botTgtLimit >= this.showDeadlineNanos && this.nanoTimeUtility == 0L;
      boolean botTgtFlag2 = this.showDeadlineNanos == 0L && this.nanoTimeUtility == 0L && !this.matrixScissorActive && this.matrixRender.getMatricesCpxStandbyDanger().isFlag();
      if (botTgtFlag || botTgtFlag2) {
         this.showDeadlineNanos = 0L;
         this.matrixRender.update();
      }

      this.panelFadeProgress = max;
   }

   private void renderMatrix(DrawContext context, float matrixValue, float peekPositionFactor) {
      double releaseRightBound = 0.025F + 0.06F * this.matrixRender.getCrystalsAmount();
      int valueForIntFloat = CardMatrixUtils.getValue(
         CardMatrixUtils.getReleaseRight(releaseRightBound), MatrixStackUtilsAmount.getValue(-1750963, (float)releaseRightBound), 0.5 * this.matrixRender.getMatricesCpxStandbyDanger().getAmount()
      );
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixValue + 43.0F, peekPositionFactor + 84.0F, 80.0F, 152.0F, 40.0F, valueForIntFloat, 32.0F, 1.0F);
   }

   private void renderAmount(DrawContext context, float botTgtValue, float peekPositionFactor, float w) {
      if (!(this.fadeOutAlpha <= 0.01F) && !(this.scissorDisableAmount < 0.0F)) {
         float fontOffset = MatrixPeekUtilsRender.getAmount();
         float amountMax = MatrixStackUtilsAmount.getAmount(peekPositionFactor + ConstAmountUtils.getAmount(this.scissorDisableAmount) - 1.0F);
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context, botTgtValue + 3.0F, amountMax, w - 6.0F, fontOffset, fontOffset / 2.0F, CardMatrixUtils.getReleaseRight(0.18 * this.fadeOutAlpha), 3.0F, 1.0F
         );
         MatrixPeekUtilsRender.renderMatrixPeek(context, botTgtValue, amountMax, w, fontOffset, CardMatrixUtils.getReleaseRight(0.55 * this.fadeOutAlpha));
      }
   }

   private static float getAmount(float botTgtValue, float botTgtLimit, float botTgtLimit2) {
      if (!(botTgtValue <= 0.0F) && !Float.isNaN(botTgtLimit)) {
         return !(botTgtLimit >= botTgtLimit2 + 8.0F) && !(botTgtLimit + 16.0F <= botTgtLimit2) ? botTgtValue : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public void handle(int botTgtValue) {
      this.handleMaxTime(botTgtValue, true);
   }

   private int getLook(String lookText) {
      int slots = this.pvpKind.getSlots(lookText);
      if (slots >= 0) {
         return slots;
      } else {
         if (lookText == null) {
            for (int i = 0; i < this.pvpKind.getSlots(); i++) {
               MinecraftBotUtils.Slot slots2 = this.pvpKind.getSlots(i);
               if (!slots2.isModule() && slots2.look().isPvpKind()) {
                  return i;
               }
            }
         }

         return this.pvpKind.getSlots() == 0 ? -1 : 0;
      }
   }

   private static void renderMatricesTranslate(DrawContext context, float y) {
      if (y != 0.0F) {
         MatrixPeekUtilsRender.getMatrixStack().push();
         MatrixPeekUtilsRender.getMatrixStack().translate(0.0F, y, 0.0F);
         context.getMatrices().pushMatrix();
         context.getMatrices().translate(0.0F, y);
      }
   }

   public void updateRenderMatrix() {
      this.matrixRender.updateTotemsFlag();
      this.matrixActive = true;
   }

   public BotTgt(List<Module> list) {
      this.utilityDoubleMap = new HashMap<>();
      this.matrixScissor = new MatrixScissor();
      this.matrixRender = new MatrixRender();
      this.popsTaken = new PopsTaken();
      this.botTgtSwap = new Swap();
      this.tgtWidthSwap = new Swap();
      this.timeMax = new Swap();
      this.tgtSwap = new Swap();
      this.matrixScissorEntrance = new Entrance();
      this.pvpBotAnims = new Anims();
      this.utilityValue = -1;
      this.matrixValue = -1;
      this.pvpBotValue = -1;
      this.utilityInspectorValue = 474;
      this.inspectorHeight = 316;
      this.matrixScissorColumns = ConstAmountUtils.getDataForInt(474, 316);
      this.fadeStartAmount = 1.0F;
      this.savedDisableAmount = -1.0F;
      this.panelFadeProgress = 1.0F;
      this.scissorDisableAmount = -1.0F;
      this.tgtWidthText = "";
      this.tgtText = "";
      this.botTgtText = "";
      this.pvpBotFormattedText = "";
      this.pvpBotText = "";
      this.copyOf = List.copyOf(list);

      for (Module Module : this.copyOf) {
         BetaListening betaListening = new BetaListening(0, 0, 474, Module);
         betaListening.handleTimeFlag(MinecraftBotUtils.getMinecraftRespawn(Module).isPvpKind() ? "PVP BOT" : "UTILITY BOT");
         betaListening.handleFlagTimeForFlag(false);
         this.botUtilityMap.put(Module, betaListening);
      }

      this.updateMaxUtility();
      MinecraftBotUtils.Slot slots = this.pvpKind.getSlots(this.utilityValue);
      this.botsAmount = slots != null && slots.module() != null && slots.module().isEnabled() ? 1.0F : 0.0F;
   }

   public boolean canMaxScissor(int scissorValue, int modifiers) {
      int slots = this.pvpKind.getSlots();
      if (slots == 0) {
         return false;
      } else {
         BetaListening betaListening = this.getBetaListening();
         if (betaListening == null || !betaListening.isFlagTime() && !betaListening.isListeningGlfw() && !betaListening.isFlagKeybindScissorSettingCompone()) {
            int scissorLimit = switch (scissorValue) {
               case 264 -> ConstAmountUtils.getValue(this.utilityValue, 1, slots);
               case 265 -> ConstAmountUtils.getValue(this.utilityValue, -1, slots);
               default -> -1;
               case 268 -> 0;
               case 269 -> slots - 1;
            };
            if (scissorLimit >= 0) {
               if (scissorLimit != this.utilityValue) {
                  this.handle(scissorLimit);
               }

               this.matrixScissor.handleAmountMax(scissorLimit);
               return true;
            } else if (scissorValue != 257 && scissorValue != 335) {
               return false;
            } else if (betaListening == null) {
               return false;
            } else {
               betaListening.updateLockedModule();
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private void renderMatrixScissor(DrawContext context, int matrixScissorValue, int heightValue, long nanoTime) {
      Rect stage = this.matrixScissorColumns.stage();
      float totemsValue = this.matrixScissorValue + stage.x();
      float peekPositionFactor = this.panelY + stage.y();
      float w = stage.w();
      float n4 = stage.h();
      Entrance amountFlag = this.matrixScissorEntrance.getAmountFlag(1);
      float matrixScissorLimit = amountFlag.entranceProgress;
      float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
      float matrixScissorAmount = amountFlag.matrixScissorAmount;
      MinecraftBotUtils.Slot slots = this.pvpKind.getSlots(this.matrixValue);
      CpxVillagerData cpxVillagerData = slots == null ? null : slots.look();
      Module Module = slots == null ? null : slots.module();
      boolean matrixScissorFlag = Module != null && Module.isEnabled();
      Snapshot menuTurnData = ScissorTgtUtils.getMenuTurnData(Module);
      if (this.popsTaken.canCpxText(cpxVillagerData, matrixScissorFlag, menuTurnData)) {
         this.updateTgtWidth();
      }

      float matrixRenderBound = 1.0F;
      if (this.scissorStartNanos != 0L) {
         float matrixStackValue = (float)(nanoTime - this.scissorStartNanos) / 1000000.0F / 420.0F;
         if (matrixStackValue >= 1.0F) {
            this.scissorStartNanos = 0L;
         } else {
            matrixRenderBound = matrixStackValue <= 0.0F ? 0.0F : MatrixStackUtilsAmount.getAmountForFloat(matrixStackValue);
         }
      }

      this.matrixRender.handleMin(matrixRenderBound);
      this.matrixRender.setFlagForFlag(this.matrixScissorActive);
      float matrixScissorValue8 = this.matrixRender.isCrystalsActive() && this.matrixScissor.isTime() ? this.matrixScissor.getAmountMax() : Float.NaN;
      this.matrixRender.handleAmount(matrixScissorValue8, matrixScissorValue8 + 16.0F);
      if (amountFlag.activeFlag && stage.canFlag(matrixScissorValue - this.matrixScissorValue, heightValue - this.panelY)) {
         this.matrixRender.handleTotemsFlag(matrixScissorValue, heightValue, totemsValue, peekPositionFactor, w, n4);
      } else {
         this.matrixRender.updateTotemsAmount();
      }

      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, totemsValue + w / 2.0F, peekPositionFactor + n4 / 2.0F, matrixScissorAmount);

      try {
         if (tabsSettingsAmount > 0.004F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

            try {
               RenderMatrixUtilsRelease.render(context, totemsValue, peekPositionFactor, w, n4, 7.0F, CardMatrixUtils.getValue7(), 234881023);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(matrixScissorLimit);

         try {
            if (matrixScissorLimit > 0.002F) {
               RenderMatrixUtilsRelease.render(context, totemsValue, peekPositionFactor, w, n4, 7.0F, CardMatrixUtils.getValue7(), 234881023);
               this.renderMatrix(context, totemsValue, peekPositionFactor);
            }

            this.renderScissorDisable(context, totemsValue, peekPositionFactor, w, n4, cpxVillagerData, matrixScissorFlag, menuTurnData);
            if (matrixScissorLimit > 0.002F) {
               this.renderAmount(context, totemsValue, peekPositionFactor, w);
               this.renderAmountTgt(context, totemsValue, peekPositionFactor, cpxVillagerData, nanoTime);
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
         }
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   public boolean canMatrixScissor(double matrixScissorValue, double matrixScissorValue2, int button) {
      double py = matrixScissorValue - this.matrixScissorValue;
      double px = matrixScissorValue2 - this.panelY;
      Rect stage = this.matrixScissorColumns.stage();
      if (this.matrixScissorColumns.rail().canFlag(py, px)) {
         if (!this.matrixScissorEntrance.getAmountFlag(0).matrixScissorActive) {
            return true;
         } else {
            int amountMax = this.matrixScissor.getAmountMax(matrixScissorValue, matrixScissorValue2);
            if (amountMax >= 0 && this.matrixScissorEntrance.getAmountFlag(3 + amountMax).matrixScissorActive) {
               this.update();
               this.matrixScissor.handleTexts(amountMax);
               this.handle(amountMax);
            }

            return true;
         }
      } else if (stage.canFlag(py, px)) {
         if (!this.matrixScissorEntrance.getAmountFlag(1).matrixScissorActive) {
            return true;
         } else {
            this.update();
            this.matrixRender.canMinMax(matrixScissorValue, matrixScissorValue2, this.matrixScissorValue + stage.x(), this.panelY + stage.y(), stage.w(), stage.h(), button);
            return true;
         }
      } else if (this.matrixScissorColumns.inspector().canFlag(py, px)) {
         if (!this.matrixScissorEntrance.getAmountFlag(2).matrixScissorActive) {
            return true;
         } else {
            BetaListening betaListening = this.getBetaListening();
            if (betaListening != null) {
               betaListening.canFlagListening(matrixScissorValue, matrixScissorValue2, button);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private BetaListening getUtilityModule(int utilityModuleValue) {
      MinecraftBotUtils.Slot slot = this.pvpKind == null ? null : this.pvpKind.getSlots(utilityModuleValue);
      return slot != null && slot.module() != null ? this.botUtilityMap.get(slot.module()) : null;
   }

   public void updateMatrixUtility() {
      for (BetaListening betaListening : this.botUtilityMap.values()) {
         betaListening.updateKeybindScissorSettingCompone();
      }

      this.matrixScissor.update();
      this.matrixRender.updateTotemsAmount();
   }

   private static long getTime(float botTgtValue) {
      return (long)(botTgtValue * 1000000.0);
   }

   private void renderMatrixAmount(DrawContext context, int matrixValue, int heightValue) {
      Rect rail = this.matrixScissorColumns.rail();
      float matrixReleaseValue = this.matrixScissorValue + rail.x();
      float peekPositionFactor = this.panelY + rail.y();
      Entrance amountFlag = this.matrixScissorEntrance.getAmountFlag(0);
      float matrixLimit = amountFlag.entranceProgress;
      float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
      float matrixScissorAmount = amountFlag.matrixScissorAmount;
      boolean activeFlag = amountFlag.activeFlag;
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixReleaseValue + rail.w() / 2.0F, peekPositionFactor + rail.h() / 2.0F, matrixScissorAmount);

      try {
         if (tabsSettingsAmount > 0.004F) {
            render(context, tabsSettingsAmount, matrixReleaseValue, peekPositionFactor, rail.w(), rail.h());
         }

         if (matrixLimit > 0.002F) {
            render(context, matrixLimit, matrixReleaseValue, peekPositionFactor, rail.w(), rail.h());
         }
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }

      this.matrixScissor.renderAmountScissor(context, matrixReleaseValue, peekPositionFactor, rail.h(), matrixValue, heightValue, this.utilityValue, this.matrixScissorEntrance, 3, activeFlag && !this.matrixRender.isTotemsActive());
   }

   private BetaListening getBetaListening() {
      return this.betaListeningBetaListeningLi.isEmpty() ? null : this.betaListeningBetaListeningLi.get(0);
   }

   public void handleRenderMatrix(double matrixValue, double heightAmount, int button) {
      if (this.matrixRender.isTotemsActive()) {
         this.matrixRender.handleAmountMin(matrixValue, heightAmount);
      } else {
         BetaListening betaListening = this.getBetaListening();
         if (betaListening != null) {
            betaListening.handleFlag(matrixValue, heightAmount, button);
         }
      }
   }

   private void update() {
      BetaListening betaListening = this.getBetaListening();
      if (betaListening != null) {
         betaListening.updateKeybindScissorSettingCompone();
      }
   }

   private void updateMaxUtility() {
      boolean flag = MinecraftBotUtils.isFlag();
      if (this.pvpKind == null || flag != this.utilityActive) {
         String utilityText = this.pvpKind != null && this.pvpKind.getSlots(this.utilityValue) != null
            ? this.pvpKind.getSlots(this.utilityValue).formatLook()
            : PngIcons.getInstance().getText();
         String utilityText2 = this.pvpKind != null && this.pvpKind.getSlots(this.matrixValue) != null ? this.pvpKind.getSlots(this.matrixValue).formatLook() : null;
         BetaListening betaListening = this.getBetaListening();
         if (betaListening != null && this.pvpKind != null && this.pvpKind.getSlots(this.utilityValue) != null) {
            this.utilityDoubleMap.put(this.pvpKind.getSlots(this.utilityValue).formatLook(), betaListening.getMaxBetaListeningBound());
         }

         this.utilityActive = flag;
         this.pvpKind = MinecraftBotUtils.getPvpKind(this.copyOf, flag);
         this.matrixScissor.handleTextsAmounts(this.pvpKind);

         for (BetaListening betaListening2 : this.botUtilityMap.values()) {
            betaListening2.handleFlagTimeForFlag(false);
         }

         this.betaListeningBetaListeningLi.clear();
         this.utilityValue = -1;
         this.matrixValue = utilityText2 == null ? -1 : this.pvpKind.getSlots(utilityText2);
         this.nanoTimeUtility = 0L;
         int look = this.getLook(utilityText);
         if (look >= 0) {
            this.handleMaxTime(look, false);
         }
      }
   }

   private static float getAmount(long botTgtValue, long nanoTime, float botTgtDivisor) {
      if (botTgtValue == 0L) {
         return 1.0F;
      } else {
         float botTgtLimit = (float)(nanoTime - botTgtValue) / 1000000.0F / botTgtDivisor;
         return botTgtLimit <= 0.0F ? 0.0F : (botTgtLimit >= 1.0F ? 1.0F : MatrixStackUtilsAmount.getAmountForFloat(botTgtLimit));
      }
   }

   private void renderBotsAmount(DrawContext context, int botsValue, int heightValue, float modulesClearValue, long nanoTime) {
      Rect inspector = this.matrixScissorColumns.inspector();
      float matrixReleaseValue = this.matrixScissorValue + inspector.x();
      float downloadCommunityValue = this.panelY + inspector.y();
      float w = inspector.w();
      float segmentSlider = inspector.h();
      Entrance amountFlag = this.matrixScissorEntrance.getAmountFlag(2);
      float entranceProgress = amountFlag.entranceProgress;
      float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
      float matrixScissorAmount = amountFlag.matrixScissorAmount;
      boolean activeFlag = amountFlag.activeFlag;
      MinecraftBotUtils.Slot slots = this.pvpKind.getSlots(this.utilityValue);
      Module ilIllIiIll2 = slots == null ? null : slots.module();
      BetaListening betaListening = this.getBetaListening();
      this.botsAmount = (float)MatrixIdentifierUtils.getAmount(this.botsAmount, ilIllIiIll2 != null && ilIllIiIll2.isEnabled() ? 1.0 : 0.0, 10.0, MatrixClearUtils.getAmount());
      if (betaListening != null && slots != null) {
         this.handleToneHint(betaListening, slots);
      }

      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixReleaseValue + w / 2.0F, downloadCommunityValue + segmentSlider / 2.0F, matrixScissorAmount);

      try {
         if (tabsSettingsAmount > 0.004F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

            try {
               RenderMatrixUtilsRelease.render(context, matrixReleaseValue, downloadCommunityValue, w, segmentSlider, 7.0F, 0.0, 0.0, 0.0);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         if (entranceProgress <= 0.002F) {
            return;
         }

         float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(entranceProgress);

         try {
            RenderMatrixUtilsRelease.render(context, matrixReleaseValue, downloadCommunityValue, w, segmentSlider, 7.0F, this.botsAmount, 0.0, 0.0);
            float botsLimit = getAmount(this.nanoTimeBots, nanoTime, 140.0F);
            if (botsLimit >= 1.0F) {
               this.nanoTimeBots = 0L;
            }

            if (!(botsLimit <= 0.002F)) {
               float matricesValue = (1.0F - botsLimit) * 3.0F;
               float panelOverlay3 = CorzRoundedUtils.getPanelOverlay(botsLimit);
               renderMatricesTranslate(context, matricesValue);

               try {
                  if (betaListening != null) {
                     betaListening.handleMax(this.botsAmount);
                     int keybindBindValue = activeFlag && !this.matrixRender.isTotemsActive() ? botsValue : (int)matrixReleaseValue - 4000;
                     betaListening.renderKeybindBind(context, keybindBindValue, (int)Math.floor(heightValue - matricesValue), modulesClearValue);
                     return;
                  } else {
                     if (slots != null) {
                        this.renderPvpBot(context, slots, matrixReleaseValue, downloadCommunityValue, w, segmentSlider);
                     } else {
                        MatrixStackUtilsAmount.renderMatricesPop(
                           context,
                           Hits.INSTANCE,
                           this.pvpBotAnims,
                           "empty",
                           matrixReleaseValue,
                           downloadCommunityValue,
                           w,
                           segmentSlider,
                           IcoGenUtils.getIdentifier("bot"),
                           "No bots yet",
                           "Bots will show up here.",
                           null,
                           (int)matrixReleaseValue - 4000,
                           (int)downloadCommunityValue,
                           null
                        );
                     }

                     return;
                  }
               } finally {
                  renderPopMatrices(context, matricesValue);
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay3);
               }
            }
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
         }
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   private int getRenderMatrix(CpxVillagerData cpxVillagerData) {
      if (cpxVillagerData == null || cpxVillagerData.planned()) {
         return -11117467;
      } else if (cpxVillagerData.isSkin() && cpxVillagerData.eyes() != null && !cpxVillagerData.isPullAct()) {
         StandbyDanger matricesCpxStandbyDanger = this.matrixRender.getMatricesCpxStandbyDanger();
         return CardMatrixUtils.getValue(-11117467, -54742, 0.45 * matricesCpxStandbyDanger.getBlendedDanger());
      } else {
         return CardMatrixUtils.getValue(-11117467, -10761846, 0.35 * this.matrixRender.getCrystalsAmount());
      }
   }

   public void handleRenderMatrixForDoubleInt(double matrixValue, double heightAmount, int button) {
      this.matrixRender.updateFlagMin();
      BetaListening betaListening = this.getBetaListening();
      if (betaListening != null) {
         betaListening.handleFlagForDoubleInt(matrixValue, heightAmount, button);
      }
   }

   private static void render(DrawContext context, float botTgtValue, float matrixReleaseValue, float peekPositionFactor, float w, float n4) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(botTgtValue);

      try {
         RenderMatrixUtilsRelease.render(context, matrixReleaseValue, peekPositionFactor, w, n4, 7.0F, CardMatrixUtils.getValue4(), 234881023);
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void updateTgtWidth() {
      this.tgtWidthText = PopsTaken.formatWidth(this.popsTaken.getOnlineOfflineText(), 72.0F, TEXT_MEASURE);
      this.widthTgtWidth = TEXT_MEASURE.width(this.tgtWidthText);
      if (this.popsTaken.isText()) {
         float tgtWidthValue = 150.0F;
         float widthOffset = TEXT_MEASURE.width("TGT ") + 1.0F;
         this.tgtWidthAmount = PopsTaken.getAmount(tgtWidthValue, widthOffset);
         this.botTgtText = PopsTaken.formatWidth(this.popsTaken.getPopsTakenText(), this.tgtWidthAmount, TEXT_MEASURE);
         this.width = TEXT_MEASURE.width(this.botTgtText);
         this.tgtTextWidth = PopsTaken.getAmount(tgtWidthValue, widthOffset, this.width);
         String tgtWidthText = this.popsTaken.getText();
         this.tgtText = tgtWidthText.startsWith("TGT ") ? PopsTaken.formatWidth(tgtWidthText.substring("TGT ".length()), this.tgtTextWidth, TEXT_MEASURE) : "";
      }
   }

   public void handleUtilityInspector(int value, int value2, int constValue, int value4) {
      if (!this.utilityInspectorActive || value != this.matrixScissorValue || value2 != this.panelY || constValue != this.utilityInspectorValue || value4 != this.inspectorHeight) {
         this.utilityInspectorActive = true;
         this.matrixScissorValue = value;
         this.panelY = value2;
         this.utilityInspectorValue = constValue;
         this.inspectorHeight = value4;
         this.matrixScissorColumns = ConstAmountUtils.getDataForInt(constValue, value4);
         Rect inspector = this.matrixScissorColumns.inspector();

         for (BetaListening betaListening : this.botUtilityMap.values()) {
            betaListening.handleKeybindListening(value + inspector.x(), value2 + inspector.y(), inspector.w(), inspector.h());
         }
      }
   }
}
