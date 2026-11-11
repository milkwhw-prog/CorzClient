package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.RenderMatrixUtilsRelease;
import com.corz.client.render.VertexTextureFont;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.categories.settings.ClientSounds;
import com.corz.client.utils.AmountPreviewUtils;
import com.corz.client.utils.MaxMax2;
import com.corz.client.utils.MaxFlag;
import com.corz.client.utils.ScannedSkipped;

public final class PreviewRail {
   private float opacityFactor;
   private int button;
   private float nonAmount;
   private final PreviewText nonPreviewText;
   private String nonText;
   private float[] scissorTable;
   private final MaxMax2 railScrollState;
   private boolean crossfadeActive;
   private long nanoTimeFadeCrossfade;
   private int itemCount;
   private final ButtonMin buttonMin;
   private final MaxFlag nonMaxFlag = new MaxFlag();
   private float baseOpacity;
   private static final String PREVIEW_CLOSE = "preview:close";
   private static final String ELLIPSIS = "...";
   private final ButtonInspectsApi nonNull;
   private float gridStepPx;
   private static final String PREVIEW_RAIL = "preview:rail-more";
   private float previewRailAmount;
   private final float[] previewRailTable;
   private long nanoTimeNon;
   private float baseScale;
   private boolean previewRailActive;
   private float translateOffset;
   private boolean nonActive;
   private float previewRailCount;
   private final ConsumerButton buttonConsumerButton;
   private float currentScale;
   private boolean crossfadePending;
   private String[] nonTexts;
   private boolean fadeActive;
   private boolean closeFlag;
   private final IntConsumer intConsumer;
   private boolean fadeCrossfadeActive;
   private boolean crossfadeSuppressed;
   private static final Set<String> keySet = ConcurrentHashMap.newKeySet();
   private int previousIndex;
   private final MatrixIdentifierUtils.Spring previewRailSpring;
   private float lastSnapAmount;
   private float[] table;
   private float closePreviewAmount;
   private PreviewRail.Transition crossfadeFadeType;
   private final Runnable closePreviewAction;
   public static final int UNIT_STEP = 1;
   private long lastNanoTime;
   private int fadeValue;
   private String previewRailText;
   private boolean buttonActive;
   private float backdropAlpha;
   private final ScannedSkipped previewRailScannedSkipped;
   private float baseTranslateOffset;
   private float shrinkScale;
   private long lastNanoTimeNon;
   private long nonValue;
   private final Runnable railPreviewAction;
   private final MinButton buttonMinButton = new MinButton();
   private String outgoingText;
   private PreviewRail.Transition fadeCrossfadeTransition;
   private String[] previewRailTexts;
   private String currentLabelText;

   private void handleAmountFlag(double previewRailValue, float previewRailBound, float previewFactor) {
      if (!this.previewRailActive) {
         this.previewRailSpring.handleAmount(previewRailValue);
         this.previewRailActive = true;
      } else if (this.previewRailSpring.springPosition != previewRailValue || this.previewRailSpring.anywayEnableCount != 0.0) {
         int previewRailDivisor = Math.max(1, (int)Math.ceil(previewFactor / 0.004166666666666667));
         double previewRailValue3 = (double)previewFactor / previewRailDivisor;

         for (int i = 0; i < previewRailDivisor; i++) {
            this.previewRailSpring.handleAmount(previewRailValue, 784.0, 42.0, previewRailValue3);
         }

         if (Math.abs(this.previewRailSpring.springPosition - previewRailValue) < 0.04 && Math.abs(this.previewRailSpring.anywayEnableCount) < 1.5) {
            this.previewRailSpring.handleAmount(previewRailValue);
         }
      }

      float min4 = AmountPreviewUtils.getMin((float)Math.abs(this.previewRailSpring.springPosition - previewRailValue) / Math.max(1.0F, previewRailBound));
      this.shrinkScale = AmountPreviewUtils.getAmount(this.shrinkScale, 1.0F - 0.6F * min4, 22.0F, previewFactor, 0.004F);
   }

   private void renderDisableScissor(
      DrawContext context, int index, float scissorValue, float matrixScissorLimit, float maxItemWidth, float materialFactor, float corzLicenseLimit, float matrixStackValue, float scissorBound, boolean scissorFlag, float panelOverlayLimit
   ) {
      float scissorBound2 = this.scissorTable[index];
      float cardMatrixFactor = this.table[index];
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

      try {
         RenderMatrixUtilsRelease.renderForDrawContextFloatDouble(context, scissorValue, matrixScissorLimit, maxItemWidth, materialFactor, corzLicenseLimit, scissorBound2);
         if (scissorFlag) {
            float thumbsOffset = scissorValue + 7.0F;
            float configsLikeValue = matrixScissorLimit + (materialFactor - 22.0F) / 2.0F;
            MatrixStackUtilsRelease.render(context, thumbsOffset, configsLikeValue, 22.0F);
            float scissorValue9 = MatrixPeekUtilsRender.getAmount();
            MatrixStackUtilsRelease.renderScissorEnable(context, thumbsOffset + scissorValue9, configsLikeValue + scissorValue9, 22.0F - scissorValue9 * 2.0F, 22.0F - scissorValue9 * 2.0F);

            try {
               this.nonNull.renderThumbsInspects(context, index, thumbsOffset, configsLikeValue, 22.0F);
            } finally {
               context.disableScissor();
            }

            MatrixStackUtilsRelease.render(context, thumbsOffset, configsLikeValue, 22.0F, 22.0F, 5.0F);
         }

         int scissorValue10 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-8090988, -920587, scissorBound2 * 0.6), MatrixEspUtils.releaseRightValue, cardMatrixFactor);
         String formatTextsNon = this.formatTextsNon(index, scissorBound);
         String formatSoldBought = this.nonNull.formatSoldBought(index);
         if (formatSoldBought == null) {
            MatrixStackUtilsAmount.handle(formatTextsNon, matrixStackValue, MatrixStackUtilsAmount.getAmount(matrixScissorLimit + materialFactor / 2.0F - 8.28F), scissorValue10);
         } else {
            float scissorValue11 = matrixScissorLimit + materialFactor / 2.0F - 0.5F;
            MatrixStackUtilsAmount.handle(formatTextsNon, matrixStackValue, MatrixStackUtilsAmount.getAmount(scissorValue11 - 11.5F), scissorValue10);
            String formatText = MatrixStackUtilsAmount.getFontRenderer().formatText(formatSoldBought, Math.max(0.0F, scissorBound) / 0.78F);
            MatrixStackUtilsAmount.handlePushScale(
               formatText, matrixStackValue, scissorValue11 + 9.5F - 8.969999F, 0.78F, CardMatrixUtils.getValue(-11117467, -7630437, Math.max(scissorBound2, cardMatrixFactor))
            );
         }
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
      }
   }

   private void handleMaxAmounts(int max) {
      this.itemCount = max;
      this.scissorTable = new float[max];
      this.table = new float[max];
      this.nonTexts = new String[max];
      this.previewRailTexts = new String[max];
      if (this.fadeValue >= max) {
         this.fadeValue = max - 1;
      }

      if (this.fadeValue < 0 && max > 0 && !this.crossfadeSuppressed) {
         this.fadeValue = 0;
      }

      if (this.previousIndex >= max) {
         this.previousIndex = -1;
      }

      if (this.fadeValue >= 0) {
         this.table[this.fadeValue] = 1.0F;
      }

      this.previewRailActive = false;
      this.crossfadePending = true;
   }

   public boolean canNonFlag(int nonValue, int nonValue2) {
      if (this.buttonActive) {
         return true;
      } else if (!this.nonActive && this.nonNull.canMaxSubstring(nonValue, nonValue2)) {
         return true;
      } else if (nonValue == 256) {
         this.updateFlagAmount();
         return true;
      } else if (this.nonActive) {
         return true;
      } else {
         if (this.itemCount > 0 && !this.nonNull.isClericLimits()) {
            switch (nonValue) {
               case 262:
               case 264:
                  this.handleMaxForInt(1);
                  return true;
               case 263:
               case 265:
                  this.handleMaxForInt(-1);
                  return true;
               case 266:
               case 267:
               default:
                  break;
               case 268:
                  this.handleNonMax(0);
                  return true;
               case 269:
                  this.handleNonMax(this.itemCount - 1);
                  return true;
            }
         }

         return false;
      }
   }

   public void updateMaxFlag() {
      this.previousIndex = this.fadeValue;
      this.fadeValue = -1;
      this.fadeActive = false;
      this.crossfadeSuppressed = true;
   }

   private void renderMaxAmount(DrawContext context, float previewRailValue, boolean previewRailFlag, float limit) {
      int previewRailLimit = this.itemCount;
      if (previewRailLimit > 0) {
         float maxItemX = this.nonMaxFlag.maxItemX;
         float maxItemY = this.nonMaxFlag.maxItemY;
         float maxItemWidth = this.nonMaxFlag.maxItemWidth;
         boolean scissorFlag = this.nonNull.isFlag();
         boolean previewRailFlag3 = scissorFlag || this.nonNull.formatSoldBought(0) != null;
         float hashHuntBound = previewRailFlag3 ? 30.0F : 22.0F;
         float amount = hashHuntBound + 2.0F;
         if (amount != this.gridStepPx) {
            this.gridStepPx = amount;
            this.previewRailActive = false;
         }

         float cubeLimit = getAmount(this.nonMaxFlag.maxItemHeight, amount, hashHuntBound);
         String formatShowOrigin = this.nonNull.formatShowOrigin();
         boolean clericLimits = this.nonNull.isClericLimits();
         this.railScrollState.handleMaxAmount(previewRailLimit * amount - 2.0F + (formatShowOrigin != null ? 24.0F : 0.0F), cubeLimit);
         boolean previewRailFlag4 = previewRailFlag && !this.closeFlag && this.nonMaxFlag.canAmount(this.closePreviewAmount, this.previewRailAmount);
         boolean railPreviewFlag = previewRailFlag4 && !clericLimits;
         if (this.crossfadePending && this.fadeValue >= 0) {
            float f = this.fadeValue * amount;
            if (this.crossfadeActive) {
               if (f < this.railScrollState.getTargetScroll()) {
                  this.railScrollState.handleAmount(f);
               } else if (f + hashHuntBound > this.railScrollState.getTargetScroll() + cubeLimit) {
                  this.railScrollState.handleAmount(f + hashHuntBound - cubeLimit);
               }
            } else {
               this.railScrollState.handleAmountMax(Math.round((f + hashHuntBound / 2.0F - cubeLimit / 2.0F) / amount) * amount);
            }

            this.crossfadePending = false;
         }

         this.railScrollState.handleAmountMax(limit, previewRailFlag4);
         float previewRailValue7 = MatrixStackUtilsAmount.getAmount(this.railScrollState.getDisplayedScroll());
         int max8 = Math.max(0, (int)Math.floor(previewRailValue7 / amount));
         int min2 = Math.min(previewRailLimit - 1, (int)Math.floor((previewRailValue7 + cubeLimit) / amount));
         if (!(this.getAmountsFlag(previewRailValue, 0) <= 0.002F) || !(this.getAmountsFlag(previewRailValue, min2 - max8) <= 0.002F) || this.nonActive) {
            float min5 = Math.min(7.0F, Math.max(4.0F, hashHuntBound / 2.0F - 2.0F));
            float matrixStackValue = maxItemX + (scissorFlag ? 36.0F : 11.0F);
            float scissorBound = maxItemX + maxItemWidth - 6.0F - matrixStackValue;
            if (scissorBound != this.lastSnapAmount) {
               this.lastSnapAmount = scissorBound;
               Arrays.fill(this.nonTexts, null);
            }

            MatrixStackUtilsRelease.renderScissorEnable(context, maxItemX - 6.0F, maxItemY, maxItemWidth + 12.0F, cubeLimit);

            try {
               this.handleAmountFlag(this.fadeValue < 0 ? 0.0 : (double)this.fadeValue * amount, amount, limit);
               if (this.fadeValue >= 0 && this.fadeValue < previewRailLimit) {
                  float previewRailValue11 = MatrixStackUtilsAmount.getAmount(maxItemY + (float)this.previewRailSpring.springPosition - previewRailValue7);
                  float previewRailLimit3 = this.getAmountsFlag(previewRailValue, Math.max(0, this.fadeValue - max8)) * this.railScrollState.getMax(previewRailValue11, previewRailValue11 + hashHuntBound, maxItemY, hashHuntBound * 0.3F);
                  if (previewRailLimit3 > 0.002F) {
                     float panelOverlay = CorzRoundedUtils.getPanelOverlay(previewRailLimit3);

                     try {
                        RenderMatrixUtilsRelease.renderReleaseRight(context, maxItemX, previewRailValue11, maxItemWidth, hashHuntBound, min5);
                        float round = Math.round(hashHuntBound / 2.0F * this.shrinkScale);
                        RenderMatrixUtilsRelease.render(context, maxItemX + 3.0F, previewRailValue11 + (hashHuntBound - round) / 2.0F, round, 1.0);
                     } finally {
                        CorzRoundedUtils.handlePanelOverlay(panelOverlay);
                     }
                  }
               }

               for (int i = max8; i <= min2; i++) {
                  float previewRailBound = maxItemY + i * amount - previewRailValue7;
                  float panelOverlayLimit = this.getAmountsFlag(previewRailValue, i - max8) * this.railScrollState.getMax(previewRailBound, previewRailBound + hashHuntBound, maxItemY, hashHuntBound * 0.3F) * (clericLimits ? 0.45F : 1.0F);
                  boolean previewRailFlag6 = railPreviewFlag && this.previewRailAmount >= Math.max(previewRailBound, maxItemY) && this.previewRailAmount < Math.min(previewRailBound + hashHuntBound, maxItemY + cubeLimit);
                  this.scissorTable[i] = AmountPreviewUtils.getAmount(
                     this.scissorTable[i], previewRailFlag6 && i != this.fadeValue ? 1.0F : 0.0F, previewRailFlag6 ? 16.0F : 11.0F, limit, 0.002F
                  );
                  this.table[i] = AmountPreviewUtils.getAmount(this.table[i], i == this.fadeValue ? 1.0F : 0.0F, 14.0F, limit, 0.002F);
                  if (!(panelOverlayLimit <= 0.002F)) {
                     this.renderDisableScissor(context, i, maxItemX, previewRailBound, maxItemWidth, hashHuntBound, min5, matrixStackValue, scissorBound, scissorFlag, panelOverlayLimit);
                     float maxCubeSchematicAmount = Math.max(previewRailBound, maxItemY);
                     float previewRailLimit5 = Math.min(previewRailBound + hashHuntBound, maxItemY + cubeLimit) - maxCubeSchematicAmount;
                     if (previewRailFlag && !clericLimits && panelOverlayLimit > 0.5F && previewRailLimit5 > 2.0F) {
                        this.buttonConsumerButton.handleMaxConsumer(maxItemX, maxCubeSchematicAmount, maxItemWidth, previewRailLimit5, 2, null, null, this.intConsumer, i);
                     }
                  }
               }

               if (formatShowOrigin != null) {
                  this.renderRailPreview(
                     context,
                     formatShowOrigin,
                     maxItemX,
                     maxItemY + previewRailLimit * amount - 2.0F + 4.0F - previewRailValue7,
                     maxItemWidth,
                     maxItemY,
                     cubeLimit,
                     min5,
                     matrixStackValue,
                     this.getAmountsFlag(previewRailValue, Math.max(0, min2 - max8 + 1)),
                     railPreviewFlag,
                     previewRailFlag
                  );
               }
            } finally {
               context.disableScissor();
            }

            float amountsFlag = this.getAmountsFlagForFloatInt(previewRailValue, 2);
            if (amountsFlag > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(amountsFlag);

               try {
                  MatrixStackUtilsRelease.render(context, maxItemX + maxItemWidth + 3.0F, maxItemY + 2.0F, cubeLimit - 4.0F, this.railScrollState);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         }
      }
   }

   private void handleButtonMin(int value, float amount, boolean guidePackageFlag) {
      this.buttonMin.selectedIndex = value;
      this.buttonMin.buttonAlpha = amount;
      this.buttonMin.guidePackageFlag = guidePackageFlag;
   }

   public void updateFadeCrossfade() {
      if (this.fadeCrossfadeActive) {
         this.nanoTimeFadeCrossfade = System.nanoTime();
         this.fadeActive = true;
         this.fadeCrossfadeTransition = PreviewRail.Transition.FADE_IN;
      }
   }

   public ButtonMin getButtonMin() {
      return this.buttonMin;
   }

   public void renderAmountNon(DrawContext context, float nonValue, float rootDivisor, double previewRailValue, double nonValue4) {
      long nanoTime = System.nanoTime();
      float buttonBound = this.lastNanoTimeNon == 0L ? 0.0F : Math.min(0.05F, (float)(nanoTime - this.lastNanoTimeNon) / 1.0E9F);
      this.lastNanoTimeNon = nanoTime;
      this.buttonMinButton.handleMin(buttonBound);
      if (!this.buttonActive) {
         this.nonNull.handleThumbsAmount(buttonBound);
      }

      this.buttonConsumerButton.updateConsumerAction();
      this.nonPreviewText.updateConsumerAction();
      this.nonNull.handleFolderDone(this.nonPreviewText);
      String formatSchematicsText = this.nonNull.formatSchematicsText();
      int maxShowSchematics = Math.max(0, this.nonNull.getShowSchematics());
      if (maxShowSchematics != this.itemCount) {
         this.handleMaxAmounts(maxShowSchematics);
      }

      this.nonMaxFlag
         .canAmountFlag(
            nonValue,
            rootDivisor,
            this.nonNull.getSchematicMaterialsAmount(),
            this.nonNull.getAmount(),
            formatSchematicsText != null,
            maxShowSchematics > 0 ? this.nonNull.getMax() : 0.0F,
            Math.max(0.0F, this.nonNull.getSettingsCleric()),
            !this.nonPreviewText.isText()
         );
      float millis = (float)(nanoTime - (this.nonActive ? this.nanoTimeNon : this.nonValue)) / 1000000.0F;
      float nonLimit;
      float nonLimit2;
      if (this.nonActive) {
         nonLimit = this.opacityFactor * AmountPreviewUtils.getAmount4(millis, true);
         this.currentScale = this.baseScale * AmountPreviewUtils.getAmountForFloatFlag(millis, true);
         this.translateOffset = this.baseTranslateOffset + AmountPreviewUtils.getAmountFloat(millis, true);
         nonLimit2 = this.baseOpacity * AmountPreviewUtils.getAmount(millis, true);
         if (AmountPreviewUtils.canFlag(millis)) {
            this.buttonActive = true;
         }
      } else {
         nonLimit = AmountPreviewUtils.getAmount4(millis, false);
         this.currentScale = AmountPreviewUtils.getAmountForFloatFlag(millis, false);
         this.translateOffset = AmountPreviewUtils.getAmountFloat(millis, false);
         nonLimit2 = AmountPreviewUtils.getAmount(millis, false);
      }

      float x = this.nonMaxFlag.panelX + this.nonMaxFlag.nonAmount / 2.0F;
      float translatePushValue = this.nonMaxFlag.panelY + this.nonMaxFlag.panelHeight / 2.0F;
      this.closePreviewAmount = this.getAmountMax(previewRailValue);
      this.previewRailAmount = this.getAmountMaxForDouble(nonValue4);
      boolean nonFlag = !this.nonActive && this.getAmountsFlagForFloatInt(millis, 0) > 0.5F;
      if (nonLimit2 > 0.002F) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, 0.0F, 0.0F, nonValue, rootDivisor, MatrixStackUtilsRelease.getValue(-16777216, this.backdropAlpha * nonLimit2));
      }

      if (!this.buttonActive) {
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, x, translatePushValue, this.currentScale, this.translateOffset);

         try {
            if (nonLimit > 0.002F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(nonLimit);

               try {
                  MatrixStackUtilsRelease.renderForDrawContextFloat(context, this.nonMaxFlag.panelX, this.nonMaxFlag.panelY, this.nonMaxFlag.nonAmount, this.nonMaxFlag.panelHeight, nonLimit);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }

            this.renderMaxAmount(context, formatSchematicsText, this.getAmountsFlagForFloatInt(millis, 0), nonFlag, nanoTime);
            this.renderButtonMin(context, this.getAmountsFlagForFloatInt(millis, 1), nonFlag, nanoTime, buttonBound);
            if (this.nonMaxFlag.isAmount()) {
               this.renderMinAmount(context, this.getAmountsFlagForFloatInt(millis, 1), nonFlag, buttonBound);
            }

            if (this.nonMaxFlag.isMaxItemWidth()) {
               this.renderMaxAmount(context, millis, nonFlag, buttonBound);
            }

            if (this.nonMaxFlag.previewAmount > 0.0F) {
               this.renderAmountPreview(context, this.getAmountsFlagForFloatInt(millis, 3), nonFlag);
            }
         } finally {
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
         }
      }
   }

   private void renderButtonMin(DrawContext context, float amount, boolean buttonFlag, long buttonValue, float min) {
      if (!(amount <= 0.002F)) {
         float cubeKnifeAmount = this.nonMaxFlag.buttonX;
         float cubeSchematicAmount = this.nonMaxFlag.buttonAmount;
         float buttonWidth = this.nonMaxFlag.buttonWidth;
         float buttonHeight = this.nonMaxFlag.buttonHeight;
         if (!(buttonWidth <= 1.0F) && !(buttonHeight <= 1.0F)) {
            boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, 0.0F, 0.0F, 1.0F, this.nonActive ? 0.0F : AmountPreviewUtils.getAmount4(amount));
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(amount);

            try {
               MatrixStackUtilsRelease.render3(context, cubeKnifeAmount, cubeSchematicAmount, buttonWidth, buttonHeight, 7.0F);
               this.buttonMin.cubeKnifeAmount = cubeKnifeAmount;
               this.buttonMin.cubeSchematicAmount = cubeSchematicAmount;
               this.buttonMin.cubeSchematicAmount2Cube = buttonWidth;
               this.buttonMin.cubeSchematicAmount3Cube = buttonHeight;
               this.buttonMin.renderAmount = 7.0F;
               this.buttonMin.materialAmount = this.closePreviewAmount;
               this.buttonMin.cursorY = this.previewRailAmount;
               this.buttonMin.thumbsActive = buttonFlag && (this.closeFlag || this.nonMaxFlag.canAmountMax(this.closePreviewAmount, this.previewRailAmount));
               this.buttonMin.buttonCloseActive = this.closeFlag;
               this.buttonMin.button = this.button;
               this.buttonMin.buttonAmount = amount;
               this.buttonMin.frameDelta = min;
               this.buttonMin.buttonActive = buttonFlag && !this.closeFlag && amount > 0.5F;
               this.buttonMin.buttonMinButton = this.buttonMinButton;
               this.buttonMin.buttonConsumerButton = this.buttonConsumerButton;
               boolean contains = keySet.contains(this.nonNull.getClass().getName());
               if (!contains) {
                  MatrixStackUtilsRelease.renderScissorEnable(context, cubeKnifeAmount, cubeSchematicAmount, buttonWidth, buttonHeight);

                  try {
                     this.handleButtonMin(this.fadeValue, 1.0F, true);
                     this.nonNull.renderCubeAmountForDrawContextButtonMin(context, this.buttonMin);
                     this.renderMaxFade(context, buttonValue);
                     this.handleButtonMin(this.fadeValue, 1.0F, true);
                     this.nonNull.renderCubeAmount(context, this.buttonMin);
                  } catch (Throwable e) {
                     contains = true;
                     keySet.add(this.nonNull.getClass().getName());
                  } finally {
                     context.disableScissor();
                  }
               }

               if (contains) {
                  MatrixStackUtilsRelease.render(
                     context,
                     cubeKnifeAmount,
                     cubeSchematicAmount,
                     buttonWidth,
                     buttonHeight,
                     MatrixStackUtilsRelease.getIdentifier("cube"),
                     "Preview unavailable",
                     "This preview hit an error and was turned off."
                  );
               }

               MatrixStackUtilsRelease.render(context, cubeKnifeAmount, cubeSchematicAmount, buttonWidth, buttonHeight, 7.0F);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   public MaxFlag getNonMaxFlag() {
      return this.nonMaxFlag;
   }

   private void renderButtonMin(DrawContext context, int buttonValue, float panelOverlayLimit, float x2, boolean buttonFlag) {
      this.handleButtonMin(buttonValue, panelOverlayLimit, buttonFlag);
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, this.buttonMin.getCubeAmount(), this.buttonMin.getCubeSchematic(), x2, 0.0F);
      float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

      try {
         this.nonNull.renderCubeSchematic(context, this.buttonMin);
      } finally {
         CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }
   }

   private void renderPreviewClose(DrawContext context, boolean previewCloseFlag) {
      float closeButtonX = this.nonMaxFlag.closeButtonX;
      float closeButtonY = this.nonMaxFlag.closeButtonY;
      float previewCloseAmount = this.nonMaxFlag.previewCloseAmount;
      boolean buttonFlag = previewCloseFlag && !this.closeFlag && this.nonMaxFlag.canAmountForDouble(this.closePreviewAmount, this.previewRailAmount);
      float previewCloseLimit = this.buttonMinButton.getAmount("preview:close", buttonFlag);
      boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, closeButtonX + previewCloseAmount / 2.0F, closeButtonY + previewCloseAmount / 2.0F, this.buttonMinButton.getAmount("preview:close", 0.08F), 0.0F);

      try {
         if (previewCloseLimit > 0.004F) {
            MatrixPeekUtilsRender.render(
               context, closeButtonX, closeButtonY, previewCloseAmount, previewCloseAmount, 5.0F, MatrixStackUtilsRelease.getValue(-1, 0.06F * previewCloseLimit), MatrixStackUtilsRelease.getValue(1040187391, previewCloseLimit)
            );
         }

         float previewCloseValue = (previewCloseAmount - 10.0F) / 2.0F;
         MatrixStackUtilsRelease.handle(
            MatrixStackUtilsRelease.getIdentifier("cross"), closeButtonX + previewCloseValue, closeButtonY + previewCloseValue, 10.0F, CardMatrixUtils.getValue(-7630437, -920587, previewCloseLimit)
         );
      } finally {
         MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
      }

      if (previewCloseFlag) {
         this.buttonConsumerButton.handleMaxConsumer(closeButtonX - 2.0F, closeButtonY - 2.0F, previewCloseAmount + 4.0F, previewCloseAmount + 4.0F, 0, "preview:close", this.closePreviewAction, null, 0);
      }
   }

   private void handleMaxAmount(String previewRailText, float previewRailBound, float previewRailValue) {
      String formatText = MatrixStackUtilsAmount.getFontRenderer().formatText(previewRailText, Math.max(0.0F, previewRailBound) / 0.82F);
      MatrixStackUtilsRelease.handle(formatText, this.nonMaxFlag.titleX, this.nonMaxFlag.subtitleY + previewRailValue, 10.0F, 0.82F, -7630437);
   }

   public boolean isFlag() {
      return this.fadeCrossfadeActive && !this.nonActive && !this.buttonActive;
   }

   public boolean canPreviewClose(double previewCloseValue, double previewRailValue, int previewCloseValue3) {
      if (this.closeFlag && previewCloseValue3 == this.button) {
         this.closePreviewAmount = this.getAmountMax(previewCloseValue);
         this.previewRailAmount = this.getAmountMaxForDouble(previewRailValue);
         this.updateButtonClose();
         return true;
      } else {
         return false;
      }
   }

   private void renderMaxAmount(DrawContext context, String previewRailText, float previewRailValue, boolean previewRailFlag, long nanoTime) {
      if (!(previewRailValue <= 0.002F)) {
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, 0.0F, 0.0F, 1.0F, this.nonActive ? 0.0F : AmountPreviewUtils.getAmount4(previewRailValue));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(previewRailValue);

         try {
            float previewRailValue3 = this.nonMaxFlag.closeButtonX - 8.0F - this.nonMaxFlag.titleX;
            MatrixStackUtilsAmount.getAmountForDrawContextString(context, this.formatTextNon(previewRailValue3), this.nonMaxFlag.titleX, this.nonMaxFlag.titleY, this.nonMaxFlag.titleHeight);
            this.handleTextMax(previewRailText, nanoTime);
            this.renderPreviewClose(context, previewRailFlag);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
         }
      }
   }

   private void handleMax(int previewRailValue) {
      if (previewRailValue != this.fadeValue) {
         ClientSounds.updateCategoryClick();
      }

      this.handleNonMax(previewRailValue);
   }

   public void handleCrossfadeFlag(int max, boolean crossfadeFlag) {
      int maxShowSchematics = Math.max(0, this.nonNull.getShowSchematics());
      if (maxShowSchematics > 0) {
         max = Math.max(0, Math.min(maxShowSchematics - 1, max));
      }

      if (max != this.fadeValue) {
         this.crossfadeSuppressed = false;
         this.previousIndex = this.fadeValue;
         this.fadeValue = max;
         boolean flag = crossfadeFlag && this.fadeCrossfadeActive && this.previousIndex >= 0;
         if (flag) {
            this.nanoTimeFadeCrossfade = System.nanoTime();
            this.fadeActive = true;
            this.fadeCrossfadeTransition = this.crossfadeFadeType;
         } else {
            this.fadeActive = false;
         }

         this.crossfadePending = true;
         this.crossfadeActive = flag;
      }
   }

   public void updateFlagAmount() {
      if (!this.buttonActive && !this.nonActive) {
         if (!this.fadeCrossfadeActive) {
            this.buttonActive = true;
         } else {
            long nanoTime = System.nanoTime();
            float millis = (float)(nanoTime - this.nonValue) / 1000000.0F;
            this.opacityFactor = AmountPreviewUtils.getAmount4(millis, false);
            this.baseScale = AmountPreviewUtils.getAmountForFloatFlag(millis, false);
            this.baseTranslateOffset = AmountPreviewUtils.getAmountFloat(millis, false);
            this.baseOpacity = AmountPreviewUtils.getAmount(millis, false);

            for (int i = 0; i < 4; i++) {
               this.previewRailTable[i] = AmountPreviewUtils.getAmount(millis, i, false);
            }

            this.updateButtonClose();
            this.nonActive = true;
            this.nanoTimeNon = nanoTime;
         }
      }
   }

   private void renderAmountPreview(DrawContext context, float previewLimit, boolean previewFlag) {
      if (!(previewLimit <= 0.002F)) {
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, 0.0F, 0.0F, 1.0F, this.nonActive ? 0.0F : AmountPreviewUtils.getAmount4(previewLimit));
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(previewLimit);

         try {
            this.nonPreviewText
               .renderAmountText(
                  context,
                  this.nonMaxFlag.previewX,
                  this.nonMaxFlag.previewY,
                  this.nonMaxFlag.previewWidth,
                  this.nonMaxFlag.previewAmount,
                  this.closePreviewAmount,
                  this.previewRailAmount,
                  previewFlag && !this.closeFlag && previewLimit > 0.5F,
                  this.buttonMinButton,
                  this.buttonConsumerButton
               );
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
         }
      }
   }

   public void handleCrossfadeFade(PreviewRail.Transition transition) {
      this.crossfadeFadeType = transition == null ? PreviewRail.Transition.CROSSFADE : transition;
   }

   private String formatTextNon(float amount) {
      String text = this.nonNull.formatText();
      if (text == null) {
         text = "";
      }

      if (!text.equals(this.previewRailText) || amount != this.nonAmount) {
         this.previewRailText = text;
         this.nonAmount = amount;
         this.nonText = MatrixStackUtilsAmount.getFontRenderer().formatText(text, Math.max(0.0F, amount) / 1.22F);
      }

      return this.nonText;
   }

   public boolean isNonActive() {
      return this.nonActive;
   }

   public MinButton getButtonMinButton() {
      return this.buttonMinButton;
   }

   private void handleMaxForInt(int previewRailValue) {
      if (this.itemCount > 0) {
         this.handleNonMax(Math.floorMod((this.fadeValue < 0 ? 0 : this.fadeValue) + previewRailValue, this.itemCount));
      }
   }

   public int getFadeValue() {
      return this.fadeValue;
   }

   public boolean isButtonActive() {
      return this.buttonActive;
   }

   private float getAmountsFlag(float previewRailValue, int previewBound) {
      return this.nonActive ? this.previewRailTable[2] * AmountPreviewUtils.getAmount(previewRailValue, 2, true) : AmountPreviewUtils.getAmount(previewRailValue, 2, previewBound, false);
   }

   private static float getAmount(float previewRailBound, float previewRailDivisor, float hashHuntBound) {
      int previewRailFactor = (int)Math.floor((previewRailBound + 2.0F) / previewRailDivisor);
      return previewRailFactor < 1 ? Math.min(previewRailBound, hashHuntBound) : previewRailFactor * previewRailDivisor - 2.0F;
   }

   static String formatText(String formatText, float previewRailLimit) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      if (fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) <= previewRailLimit) {
         return formatText;
      } else {
         float previewRailValue = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("...");
         int end = -1;

         for (int i = formatText.indexOf(32);
            i > 0 && !(fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText.substring(0, i)) + previewRailValue > previewRailLimit);
            i = formatText.indexOf(32, i + 1)
         ) {
            end = i;
         }

         if (end >= Math.min(6, formatText.length() / 3)) {
            String substring = formatText.substring(0, end);
            int varLength = substring.length();

            while (varLength > 0 && " -_,.:;([{".indexOf(substring.charAt(varLength - 1)) >= 0) {
               varLength--;
            }

            if (varLength > 0) {
               return substring.substring(0, varLength) + "...";
            }
         }

         return fontRenderer.formatText(formatText, previewRailLimit);
      }
   }

   public boolean canMaxAmount(double previewRailValue, double previewRailValue2, double consumerLimit) {
      if (this.fadeCrossfadeActive && !this.nonActive && !this.buttonActive) {
         float amountMax = this.getAmountMax(previewRailValue);
         float amountMaxForDouble = this.getAmountMaxForDouble(previewRailValue2);
         if (!this.nonMaxFlag.canAmountMinItemWidth(amountMax, amountMaxForDouble) && !this.nonMaxFlag.canAmount(amountMax, amountMaxForDouble) && this.buttonConsumerButton.canConsumer(amountMax, amountMaxForDouble, consumerLimit)) {
            return true;
         } else if (this.nonMaxFlag.canAmount(amountMax, amountMaxForDouble)) {
            this.previewRailCount = (float)(this.previewRailCount + consumerLimit);
            int previewRailCount7 = (int)this.previewRailCount;
            if (previewRailCount7 != 0) {
               this.previewRailCount -= previewRailCount7;
               float f = Math.round(this.railScrollState.getTargetScroll() / this.gridStepPx - previewRailCount7) * this.gridStepPx;
               this.railScrollState.handleAmount(f);
            }

            return true;
         } else if (this.nonMaxFlag.canAmountMinItemWidth(amountMax, amountMaxForDouble)) {
            this.nonNull.canMaxAmount(this.previewRailScannedSkipped, amountMax, amountMaxForDouble, consumerLimit);
            return true;
         } else {
            if (this.nonMaxFlag.canAmountMax(amountMax, amountMaxForDouble) && !keySet.contains(this.nonNull.getClass().getName())) {
               this.nonNull.canAmountMatrix(this.buttonMin, amountMax, amountMaxForDouble, consumerLimit);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public boolean canCloseFlag(double previewRailValue, double previewRailValue2, int previewRailValue3, double mouseDraggedValue, double previewRailValue5) {
      if (this.closeFlag && !this.nonActive) {
         float previewRailDivisor = Math.max(0.05F, this.currentScale);
         this.nonNull.handleMatrixParse(this.buttonMin, this.getAmountMax(previewRailValue), this.getAmountMaxForDouble(previewRailValue2), mouseDraggedValue / previewRailDivisor, previewRailValue5 / previewRailDivisor, previewRailValue3);
         return true;
      } else {
         return this.closeFlag;
      }
   }

   private float getAmountMax(double previewRailValue) {
      float previewRailValue2 = this.nonMaxFlag.panelX + this.nonMaxFlag.nonAmount / 2.0F;
      return (float)(previewRailValue2 + (previewRailValue - previewRailValue2) / Math.max(0.05F, this.currentScale));
   }

   private float getAmountMaxForDouble(double previewRailValue) {
      float previewRailValue2 = this.nonMaxFlag.panelY + this.nonMaxFlag.panelHeight / 2.0F;
      return (float)(previewRailValue2 + (previewRailValue - this.translateOffset - previewRailValue2) / Math.max(0.05F, this.currentScale));
   }

   public boolean canButtonMin(double buttonValue, double previewRailValue, int button2) {
      if (this.fadeCrossfadeActive && !this.nonActive && !this.buttonActive) {
         float amountMax = this.getAmountMax(buttonValue);
         float amountMaxForDouble = this.getAmountMaxForDouble(previewRailValue);
         if (!this.nonMaxFlag.canAmountPanelX(amountMax, amountMaxForDouble)) {
            if (button2 == 0) {
               this.updateFlagAmount();
            }

            return true;
         } else if (this.buttonConsumerButton.canConsumerText(amountMax, amountMaxForDouble, button2, this.buttonMinButton)) {
            return true;
         } else {
            if (this.nonMaxFlag.canAmountMax(amountMax, amountMaxForDouble) && !keySet.contains(this.nonNull.getClass().getName())) {
               if (this.closeFlag) {
                  this.updateButtonClose();
               }

               this.closeFlag = true;
               this.button = button2;
               this.buttonMin.buttonCloseActive = true;
               this.buttonMin.button = button2;
               this.nonNull.canMatrixStack(this.buttonMin, amountMax, amountMaxForDouble, button2);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void renderRailPreview(
      DrawContext context, String railPreviewText, float railPreviewValue, float railPreviewBound, float maxItemWidth, float maxItemY, float cubeLimit, float corzLicenseLimit, float matrixStackValue, float railPreviewFactor, boolean railPreviewFlag, boolean previewRailFlag
   ) {
      if (!(railPreviewBound >= maxItemY + cubeLimit) && !(railPreviewBound + 20.0F <= maxItemY)) {
         float railPreviewLimit = railPreviewFactor * this.railScrollState.getMax(railPreviewBound, railPreviewBound + 20.0F, maxItemY, 6.0F);
         boolean buttonFlag = railPreviewFlag && this.previewRailAmount >= Math.max(railPreviewBound, maxItemY) && this.previewRailAmount < Math.min(railPreviewBound + 20.0F, maxItemY + cubeLimit);
         float cardMatrixFactor = this.buttonMinButton.getAmount("preview:rail-more", buttonFlag);
         if (!(railPreviewLimit <= 0.002F)) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(railPreviewLimit);

            try {
               RenderMatrixUtilsRelease.renderForDrawContextFloatDouble(context, railPreviewValue, railPreviewBound, maxItemWidth, 20.0F, corzLicenseLimit, cardMatrixFactor * 0.6F);
               int railPreviewValue7 = CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor);
               float roundOffset = MatrixStackUtilsAmount.getAmount(railPreviewBound + 6.5F);
               MatrixStackUtilsRelease.handle(IcoGenUtils.getIdentifier(this.nonNull.isShowFlag() ? "chevron-up" : "chevron-down"), matrixStackValue, roundOffset, 7.0F, railPreviewValue7);
               MatrixStackUtilsRelease.handle(railPreviewText, matrixStackValue + 7.0F + 5.0F, railPreviewBound, 20.0F, 0.82F, railPreviewValue7);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }

            float maxOverRail = Math.max(railPreviewBound, maxItemY);
            float railPreviewLimit2 = Math.min(railPreviewBound + 20.0F, maxItemY + cubeLimit) - maxOverRail;
            if (previewRailFlag && railPreviewLimit > 0.5F && railPreviewLimit2 > 2.0F) {
               this.buttonConsumerButton.handleMaxConsumer(railPreviewValue, maxOverRail, maxItemWidth, railPreviewLimit2, 0, "preview:rail-more", this.railPreviewAction, null, 0);
            }
         }
      }
   }

   public PreviewRail(ButtonInspectsApi buttonInspectsApi) {
      this.buttonConsumerButton = new ConsumerButton();
      this.nonPreviewText = new PreviewText();
      this.buttonMin = new ButtonMin();
      this.previewRailScannedSkipped = new ScannedSkipped();
      this.railScrollState = new MaxMax2();
      this.previewRailSpring = new MatrixIdentifierUtils.Spring(0.0);
      this.previewRailTable = new float[4];
      this.closePreviewAction = this::updateFlagAmount;
      this.intConsumer = this::handleMax;
      this.railPreviewAction = this::updateNonFlag;
      this.crossfadePending = true;
      this.opacityFactor = 1.0F;
      this.baseScale = 1.0F;
      this.baseOpacity = 1.0F;
      this.backdropAlpha = 0.56F;
      this.currentScale = 1.0F;
      this.closePreviewAmount = -10000.0F;
      this.previewRailAmount = -10000.0F;
      this.shrinkScale = 1.0F;
      this.gridStepPx = 24.0F;
      this.fadeValue = -1;
      this.previousIndex = -1;
      this.button = -1;
      this.crossfadeFadeType = PreviewRail.Transition.CROSSFADE;
      this.fadeCrossfadeTransition = PreviewRail.Transition.CROSSFADE;
      this.scissorTable = new float[0];
      this.table = new float[0];
      this.nonTexts = new String[0];
      this.previewRailTexts = new String[0];
      this.lastSnapAmount = -1.0F;
      this.nonAmount = -1.0F;
      this.nonNull = Objects.requireNonNull(buttonInspectsApi);
   }

   public void handleFlagTime(boolean previewRailFlag) {
      this.nonValue = System.nanoTime();
      this.lastNanoTimeNon = 0L;
      this.fadeCrossfadeActive = true;
      this.nonActive = false;
      this.buttonActive = false;
      this.backdropAlpha = previewRailFlag ? 0.56F : 0.56F;
   }

   private void handleNonMax(int nonValue) {
      if (!this.nonNull.isClericLimits()) {
         int fadeValue2 = this.fadeValue;
         this.handleCrossfadeFlag(nonValue, true);
         if (this.fadeValue != fadeValue2) {
            this.nonNull.handleInspectsSchematics(this.fadeValue);
         }
      }
   }

   private void renderMinAmount(DrawContext context, float previewRailLimit, boolean previewRailFlag, float min) {
      if (!(previewRailLimit <= 0.002F) && !(this.nonMaxFlag.minItemWidth <= 1.0F) && !(this.nonMaxFlag.minItemHeight <= 1.0F)) {
         String nameOffset = this.nonNull.getClass().getName() + "#side";
         if (!keySet.contains(nameOffset)) {
            boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, 0.0F, 0.0F, 1.0F, this.nonActive ? 0.0F : AmountPreviewUtils.getAmount4(previewRailLimit));
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(previewRailLimit);

            try {
               this.previewRailScannedSkipped.sideBuildingAmount = this.nonMaxFlag.minItemX;
               this.previewRailScannedSkipped.skippedScannedAmount = this.nonMaxFlag.minItemY;
               this.previewRailScannedSkipped.sideWalkAmount = this.nonMaxFlag.minItemWidth;
               this.previewRailScannedSkipped.skippedLimAmount = this.nonMaxFlag.minItemHeight;
               this.previewRailScannedSkipped.hoverX = this.closePreviewAmount;
               this.previewRailScannedSkipped.scannedSkippedAmount = this.previewRailAmount;
               this.previewRailScannedSkipped.hoverY = min;
               this.previewRailScannedSkipped.skippedLimActive = previewRailFlag && !this.closeFlag && previewRailLimit > 0.5F;
               this.previewRailScannedSkipped.scannedSkippedMinButton = this.buttonMinButton;
               this.previewRailScannedSkipped.scannedSkippedConsumerButton = this.buttonConsumerButton;
               this.nonNull.renderSkippedLim(context, this.previewRailScannedSkipped);
            } catch (Throwable e) {
               keySet.add(nameOffset);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   private float getAmountsFlagForFloatInt(float previewRailValue, int index) {
      return this.nonActive ? this.previewRailTable[index] * AmountPreviewUtils.getAmount(previewRailValue, index, true) : AmountPreviewUtils.getAmount(previewRailValue, index, false);
   }

   private String formatTextsNon(int index, float nonBound) {
      String formatTextsInspects = this.nonNull.formatTextsInspects(index);
      if (formatTextsInspects == null) {
         formatTextsInspects = "";
      }

      if (!formatTextsInspects.equals(this.nonTexts[index])) {
         this.nonTexts[index] = formatTextsInspects;
         this.previewRailTexts[index] = formatText(formatTextsInspects, Math.max(0.0F, nonBound));
      }

      return this.previewRailTexts[index];
   }

   public void updateMax() {
      this.nonMaxFlag.updateAmount();
   }

   private void handleTextMax(String text, long nanoTime) {
      if (!Objects.equals(text, this.currentLabelText)) {
         this.outgoingText = this.currentLabelText != null && this.fadeCrossfadeActive && !this.nonActive ? this.currentLabelText : null;
         this.currentLabelText = text;
         this.lastNanoTime = nanoTime;
      }

      if (this.currentLabelText != null || this.outgoingText != null) {
         float previewRailValue = this.nonMaxFlag.panelX + this.nonMaxFlag.nonAmount - 12.0F - this.nonMaxFlag.titleX;
         float previewRailLimit = this.outgoingText == null ? 1.0F : (float)(nanoTime - this.lastNanoTime) / 1000000.0F / 170.0F;
         if (previewRailLimit >= 1.0F) {
            this.outgoingText = null;
            if (this.currentLabelText != null) {
               this.handleMaxAmount(this.currentLabelText, previewRailValue, 0.0F);
            }
         } else {
            float previewRailFactor = AmountPreviewUtils.getMin(previewRailLimit / 0.5F);
            float panelOverlayLimit = AmountPreviewUtils.getAmount6((previewRailLimit - 0.25F) / 0.75F);
            if (previewRailFactor < 1.0F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - previewRailFactor);

               try {
                  this.handleMaxAmount(this.outgoingText, previewRailValue, -2.5F * previewRailFactor);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }

            if (panelOverlayLimit > 0.0F && this.currentLabelText != null) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

               try {
                  this.handleMaxAmount(this.currentLabelText, previewRailValue, 2.5F * (1.0F - panelOverlayLimit));
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         }
      }
   }

   private void updateButtonClose() {
      if (this.closeFlag) {
         int button = this.button;
         this.closeFlag = false;
         this.button = -1;
         this.buttonMin.buttonCloseActive = false;
         this.buttonMin.button = -1;
         this.nonNull.handleMatrixStack(this.buttonMin, this.closePreviewAmount, this.previewRailAmount, button);
      }
   }

   private void updateNonFlag() {
      ClientSounds.updateCategoryClick();
      this.nonNull.updateShowFlag();
      this.crossfadePending = true;
      this.crossfadeActive = true;
   }

   private void renderMaxFade(DrawContext context, long fadeValue) {
      if (!this.fadeActive) {
         this.renderButtonMin(context, this.fadeValue, 1.0F, 1.0F, true);
      } else {
         float millis = (float)(fadeValue - this.nanoTimeFadeCrossfade) / 1000000.0F;
         if (this.fadeCrossfadeTransition != PreviewRail.Transition.FADE_IN && this.previousIndex >= 0 && this.previousIndex != this.fadeValue) {
            if (millis >= 220.0F) {
               this.fadeActive = false;
               this.renderButtonMin(context, this.fadeValue, 1.0F, 1.0F, true);
            } else {
               float fadeLimit = AmountPreviewUtils.getAmount3(millis);
               if (fadeLimit > 0.002F) {
                  this.renderButtonMin(context, this.previousIndex, fadeLimit, AmountPreviewUtils.getAmount7(millis), false);
               }

               float panelOverlayLimit = AmountPreviewUtils.getAmount5(millis);
               if (panelOverlayLimit > 0.002F) {
                  this.renderButtonMin(context, this.fadeValue, panelOverlayLimit, AmountPreviewUtils.getAmount11(millis), true);
               }
            }
         } else if (millis >= 260.0F) {
            this.fadeActive = false;
            this.renderButtonMin(context, this.fadeValue, 1.0F, 1.0F, true);
         } else {
            this.renderButtonMin(context, this.fadeValue, AmountPreviewUtils.getAmount10(millis), AmountPreviewUtils.getAmountForFloat(millis), true);
         }
      }
   }

   public static enum Transition {
      CROSSFADE,
      FADE_IN;
   }
}
