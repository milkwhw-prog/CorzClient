package com.corz.client.gui;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.HalloweenMatrixUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.ScissorMax;
import com.corz.client.render.SnowVertexUtils;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.HalloweenChristmasType;
import com.corz.client.utils.ToxicElectricUtils;

public class ResetLock {
   private final List<ResetLock.Row> musicHalloweenItems;
   private double scrollProgress;
   private float christmasAmount;
   private String[] clone;
   private final ScissorMax scissorMax;
   private static final String[] tabsMotionTexts = new String[]{"All", "Presets", "Appearance", "Effects", "Ambient", "Motion"};
   private static final Identifier identifier = Identifier.of("corzclient", "textures/gui/icons/lock.png");
   private float decemberRevealsAmount;
   private final ThemeCustomizer musicvol;
   private HalloweenChristmasType halloweenChristmasType;
   private DoubleSetting doubleHelper;
   private final MatrixStackUtilsAmount.Hits tabsResetHits;
   private static final String LOCK_SEASON = "lock:season";
   private long resetThemeTime;
   private int armValue;
   private static final String[] texturesIconsTexts = new String[ToxicElectricUtils.motionTexturesValue];
   private final List<ResetLock.Row> musicSongItems;
   private int remainingResetTicks;
   private static final int UNUSED_ONE = 1;
   private double scissorTabsAmount;
   private static final String[] motionAmbientTexts = new String[]{"Presets", "Appearance", "Effects", "Ambient", "Motion"};
   private final List<List<ResetLock.Row>> musicSongListList = new ArrayList<>();
   private static final String RESET_ALL = "Reset all?";
   private static final String LOCK_TIP = "lock:tip";
   private int armResetValue;
   private static final String RESET = "reset";
   private int tabs;
   private int christmasValue;
   private int lockValue;
   private final MatrixStackUtilsAmount.Entrance tabsResetEntrance;
   private final MatrixStackUtilsAmount.Anims christmasAnims;
   private static final String[] lockTexts = new String[ToxicElectricUtils.motionTexturesValue];
   private long lastNanoTime;
   private float lockSeasonAmount;
   private boolean lockTipFlag;
   private static final String TABS = "tabs";
   private static int controlsTexturesValue;

   public void handleScissorMusic(double scissorMusicValue, double mouseReleasedValue, int button) {
      for (ResetLock.Row row : this.musicSongItems) {
         if (row.scissorMax != null) {
            row.scissorMax.handleFlagForDoubleInt(scissorMusicValue, mouseReleasedValue, button);
         }
      }

      this.doubleHelper = null;
   }

   private static boolean canMatricesTranslate(DrawContext context, float y) {
      if (y < 0.001F) {
         return false;
      } else {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, y, 0.0F);
         context.getMatrices().pushMatrix();
         context.getMatrices().translate(0.0F, y);
         return true;
      }
   }

   private float getTexturesIcons(DrawContext context, int texturesIconsValue, float matrixStackValue, int w, int matrixStackLimit, int scaledY) {
      ToxicElectricUtils.Palette[] crimson = ToxicElectricUtils.getCrimson();
      int instance = ToxicElectricUtils.getInstance();
      float texturesIconsValue6 = MatrixStackUtilsAmount.getAmount(matrixStackValue);
      int floor7 = (int)Math.floor(texturesIconsValue6 + 0.001F);
      boolean matricesTranslate = canMatricesTranslate(context, texturesIconsValue6 - floor7);

      try {
         for (int i = 0; i < crimson.length; i++) {
            int matrixStackFactor = i % 4;
            int texturesIconsValue9 = i;
            boolean texturesIconsFlag = matrixStackFactor == 0 && i == crimson.length - 1;
            int valueForInt = MatrixStackUtilsAmount.getValueForInt(texturesIconsValue, w, 4, 4, matrixStackFactor);
            int materialFactor = texturesIconsFlag ? w : MatrixStackUtilsAmount.getValue(texturesIconsValue, w, 4, 4, matrixStackFactor);
            int vectorTransformValue = floor7 + i / 4 * 38;
            boolean texturesIconsFlag2 = i == instance;
            this.renderArmAmount(
               context,
               lockTexts[i],
               valueForInt,
               vectorTransformValue,
               materialFactor,
               34.0F,
               matrixStackLimit,
               (myx, active) -> this.renderTexturesIcons(context, myx, texturesIconsValue9, texturesIconsFlag2, valueForInt, vectorTransformValue, materialFactor, active, scaledY)
            );
         }
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, matricesTranslate);
      }

      int texturesIconsValue13 = (crimson.length + 4 - 1) / 4;
      return texturesIconsValue13 == 0 ? 0.0F : texturesIconsValue13 * 38 - 4;
   }

   private void updateThemeReset() {
      long currentMillis = System.currentTimeMillis();
      if (currentMillis < this.resetThemeTime) {
         if (currentMillis < this.resetThemeTime - 2500L + 300L) {
            return;
         }

         this.resetThemeTime = 0L;
         MatrixEspUtils.updateFlag();

         for (ResetLock.Row row : this.musicSongItems) {
            handleScissorBase(row);
         }

         MatrixEspUtils.updateFlagTime();
         DisabledEnabled.getInstance().handleDisabledEnabled("Theme Reset", "All theme settings restored", true);
      } else {
         this.resetThemeTime = currentMillis + 2500L;
      }
   }

   private void handleControlsTabs(int tabs) {
      this.tabs = tabs;
      controlsTexturesValue = tabs;
      this.resetThemeTime = 0L;
   }

   private void renderTexturesIcons(DrawContext context, MatrixStackUtilsAmount.Hits hits, int index2, boolean texturesIconsFlag, int texturesIconsValue, int matrixStackValue, int identifierText, int matrixStackLimit, int scaledY) {
      ToxicElectricUtils.Palette palette = ToxicElectricUtils.getCrimson()[index2];
      String texturesIconsText = lockTexts[index2];
      float lockFactor = this.christmasAnims.getAmount(texturesIconsText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, texturesIconsValue, matrixStackValue, identifierText, 34.0F));
      float texturesIconsLimit = this.christmasAnims.getAmount(texturesIconsTexts[index2], texturesIconsFlag ? 1.0F : 0.0F, 9.0F);
      MatrixStackUtilsAmount.render(context, texturesIconsValue, matrixStackValue, identifierText, 34.0F, lockFactor, 0.0F, texturesIconsLimit);
      float texturesIconsValue7 = texturesIconsValue + 7;
      float texturesIconsFactor = identifierText - 14.0F;
      int texturesIconsValue8 = CardMatrixUtils.getValue(CardMatrixUtils.getValue(-7630437, -920587, 0.55 * lockFactor), -920587, texturesIconsLimit);
      MatrixStackUtilsAmount.handle(
         MatrixStackUtilsAmount.getFontRenderer().formatText(palette.name(), texturesIconsFactor - 10.0F - 4.0F),
         texturesIconsValue7,
         MatrixStackUtilsAmount.getAmount(matrixStackValue + 4.0F, 11.0F, 1.0F),
         texturesIconsValue8
      );
      if (texturesIconsLimit > 0.01F) {
         float matrixPeekValue = texturesIconsValue7 + texturesIconsFactor - 10.0F;
         float configsLikeValue = matrixStackValue + 4.0F + 0.5F;
         float texturesIconsValue11 = 1.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(texturesIconsLimit);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixPeekValue + 5.0F, configsLikeValue + 5.0F, 0.6F + 0.4F * texturesIconsLimit);

         try {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, matrixPeekValue, configsLikeValue, 10.0F, 10.0F, 5.0F, MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.92F)
            );
            MatrixPeekUtilsIdentifier.handle(
               matrixPeekValue + texturesIconsValue11,
               configsLikeValue + texturesIconsValue11,
               8.0F,
               8.0F,
               IcoGenUtils.getIcoCheck(),
               new Color(MatrixStackUtilsAmount.getReleaseRight(), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      float texturesIconsValue12 = matrixStackValue + 19.0F;
      int[] colors = palette.colors();
      int texturesIconsDivisor = colors.length;

      for (int i = 0; i < texturesIconsDivisor; i++) {
         float texturesIconsValue13 = MatrixStackUtilsAmount.getAmount(texturesIconsValue7 + texturesIconsFactor * i / texturesIconsDivisor);
         float texturesIconsValue14 = MatrixStackUtilsAmount.getAmount(texturesIconsValue7 + texturesIconsFactor * (i + 1) / texturesIconsDivisor);
         float corzRoundedValue = i == 0 ? texturesIconsValue13 - 1.0F : texturesIconsValue13;
         float texturesIconsValue16 = i == texturesIconsDivisor - 1 ? texturesIconsValue14 + 1.0F : texturesIconsValue14;
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, texturesIconsValue7, texturesIconsValue12, texturesIconsFactor, 9.0F, 3.0F, CardMatrixUtils.getValue(0xFF000000 | colors[i], -1, 0.1 * lockFactor), corzRoundedValue, texturesIconsValue12 - 1.0F, texturesIconsValue16 - corzRoundedValue, 11.0F
         );
      }

      MatrixPeekUtilsRender.renderMatrixPeek(
         context, texturesIconsValue7, texturesIconsValue12, texturesIconsFactor, 9.0F, 3.0F, MatrixPeekUtilsRender.getAmount(), MatrixStackUtilsAmount.getValue(-1, 0.1F + 0.06F * lockFactor)
      );
      hits.handleFlag(texturesIconsValue, matrixStackValue, identifierText, 34.0F, () -> {
         ToxicElectricUtils.handleBaseInstance(palette);
         this.scissorMax.update();
         DisabledEnabled.getInstance().handleDisabledEnabled("Theme", palette.name() + " applied", true);
      }, this.christmasAnims, texturesIconsText);
   }

   private void handleMusicSong(int index, ResetLock.Row row) {
      this.musicSongListList.get(index).add(row);
      this.musicSongItems.add(row);
   }

   private void render(DrawContext context, float lockValue, float configsLikeValue, int matrixStackValue, float maxThemeTabsAmount, float x2) {
      float lockValue6 = MatrixPeekUtilsRender.getAmount();
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, lockValue + 11.0F, configsLikeValue + 7.0F, x2);
      MatrixPeekUtilsRender.renderMatrixPeek(context, lockValue, configsLikeValue, 22.0F, 14.0F, 4.0F, MatrixStackUtilsAmount.getValue(-1, 0.22F + 0.3F * maxThemeTabsAmount));
      MatrixPeekUtilsRender.renderMatrixPeek(context, lockValue + lockValue6, configsLikeValue + lockValue6, 22.0F - lockValue6 * 2.0F, 14.0F - lockValue6 * 2.0F, 4.0F - lockValue6, -16118769);
      MatrixPeekUtilsRender.renderMatrixPeek(context, lockValue + lockValue6, configsLikeValue + lockValue6, 22.0F - lockValue6 * 2.0F, 14.0F - lockValue6 * 2.0F, 4.0F - lockValue6, matrixStackValue);
      MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
   }

   private float getText(DrawContext context, List<ResetLock.Row> list2, int lockValue, float tabsResetTotal, int w, int matrixStackLimit, int scaledY) {
      float lockTotal = tabsResetTotal;

      for (int i = 0; i < list2.size(); i += 2) {
         int lockLimit = Math.min(2, list2.size() - i);
         float lockBound = 36.0F;

         for (int j = 0; j < lockLimit; j++) {
            lockBound = Math.max(lockBound, getSegmentSlider((ResetLock.Row)list2.get(i + j)));
         }

         float lockValue6 = MatrixStackUtilsAmount.getAmount(lockTotal);
         int vectorTransformValue = (int)Math.floor(lockValue6 + 0.001F);
         boolean matricesTranslate = canMatricesTranslate(context, lockValue6 - vectorTransformValue);

         try {
            for (int k = 0; k < lockLimit; k++) {
               ResetLock.Row row = (ResetLock.Row)list2.get(i + k);
               int valueForInt = MatrixStackUtilsAmount.getValueForInt(lockValue, w, 2, 4, k);
               int materialFactor = MatrixStackUtilsAmount.getValue(lockValue, w, 2, 4, k);
               float segmentSlider = getSegmentSlider(row);
               this.renderArmAmount(
                  context,
                  row.cardAnimKey,
                  valueForInt,
                  vectorTransformValue,
                  materialFactor,
                  segmentSlider,
                  matrixStackLimit,
                  (hits, target) -> this.renderChristmasText(context, row, hits, valueForInt, vectorTransformValue, materialFactor, segmentSlider, target, scaledY)
               );
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, matricesTranslate);
         }

         lockTotal += lockBound + 4.0F;
      }

      return list2.isEmpty() ? 0.0F : lockTotal - 4.0F - tabsResetTotal;
   }

   private void handle(double lockValue) {
      if (this.doubleHelper != null && this.christmasValue > 0) {
         double lockFactor = Math.max(0.0, Math.min(1.0, (lockValue - this.lockValue) / this.christmasValue));
         this.doubleHelper.handleMaxAmount(this.doubleHelper.getMinValue() + lockFactor * (this.doubleHelper.getMaxValue() - this.doubleHelper.getMinValue()));
         ModuleConfigAutosave.updateFlag2();
         MatrixEspUtils.updateFlagTime();
      }
   }

   private void renderTextChristmas(DrawContext context, MatrixStackUtilsAmount.Hits hits, ResetLock.Row row, int christmasValue, int vectorTransformValue, int christmasValue3, float abcdefghijklmnopqrstuvwxyzBrown, int matrixStackLimit, int scaledY) {
      DoubleSetting doubleHelper = (DoubleSetting)row.baseChristmas;
      int value = christmasValue + 8;
      int releaseRightFactor = christmasValue3 - 16;
      int matrixStackValue = value - 6;
      int plusDismissValue = vectorTransformValue + 16;
      int identifierText = releaseRightFactor + 12;
      byte segmentSlider = 20;
      boolean christmasFlag = this.doubleHelper == doubleHelper;
      float cardMatrixFactor = this.christmasAnims.getAmount(row.christmasText, christmasFlag || MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, matrixStackValue, plusDismissValue, identifierText, segmentSlider));
      double deltaAmount = doubleHelper.getMaxValue() - doubleHelper.getMinValue();
      float animsValue = (float)doubleHelper.getValue();
      float christmasValue13;
      if (christmasFlag) {
         float christmasLimit = MatrixStackUtilsAmount.getAmountForFloat((float)(System.nanoTime() - this.lastNanoTime) / 1.1E8F);
         christmasValue13 = christmasLimit >= 1.0F ? animsValue : this.christmasAmount + (animsValue - this.christmasAmount) * christmasLimit;
         this.christmasAnims.handle(row.valueAnimKey, christmasValue13);
      } else {
         christmasValue13 = this.christmasAnims.getAmount(row.valueAnimKey, animsValue, 12.0F);
      }

      float christmasValue14 = deltaAmount <= 0.0 ? 0.0F : (float)((christmasValue13 - doubleHelper.getMinValue()) / deltaAmount);
      MatrixStackUtilsAmount.renderReleaseRight(context, value, vectorTransformValue + 24, releaseRightFactor, christmasValue14, cardMatrixFactor);
      String christmasText = row.christmasActive ? Math.round(christmasValue13 * 100.0F) + "%" : String.format(Locale.ROOT, "%.2fx", christmasValue13);
      MatrixStackUtilsAmount.handle(
         christmasText,
         MatrixStackUtilsAmount.getAmount(christmasValue + christmasValue3 - 8 - MatrixStackUtilsAmount.getAmount3(christmasText)),
         abcdefghijklmnopqrstuvwxyzBrown,
         CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor)
      );
      hits.handle(matrixStackValue, plusDismissValue, identifierText, segmentSlider, () -> {
         this.doubleHelper = doubleHelper;
         this.lockValue = value;
         this.christmasValue = releaseRightFactor;
         this.christmasAmount = this.christmasAnims.getAmount(row.valueAnimKey);
         this.lastNanoTime = System.nanoTime();
         this.handle(this.scrollProgress);
      });
   }

   private void renderChristmasText(DrawContext context, ResetLock.Row row, MatrixStackUtilsAmount.Hits hits, int christmasValue, int vectorTransformValue, int identifierText, float segmentSlider, int matrixStackLimit, int scaledY) {
      VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
      float lockFactor = this.christmasAnims.getAmount(row.cardAnimKey, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, christmasValue, vectorTransformValue, identifierText, segmentSlider));
      float christmasValue8 = christmasValue + identifierText - 8;
      float brownValue = vectorTransformValue + 10.0F;
      float matrixStackValue = christmasValue + 8;
      float abcdefghijklmnopqrstuvwxyzBrown = MatrixStackUtilsAmount.getAmount(vectorTransformValue + 7.0F, 11.0F, 1.0F);

      float christmasValue12 = switch (row.segmentSliderType) {
         case TOGGLE -> {
            BooleanSetting base01 = (BooleanSetting)row.baseChristmas;
            float releaseRightLimit = this.christmasAnims.getAmount(row.onAnimKey, base01.isEnabled() ? 1.0F : 0.0F, 16.0F);
            MatrixStackUtilsAmount.render(context, christmasValue, vectorTransformValue, identifierText, segmentSlider, lockFactor, this.christmasAnims.getAmount(row.pressAnimKey, 0.0F, 10.0F), releaseRightLimit);
            RenderPillUtils.render(context, christmasValue + identifierText, vectorTransformValue, 36, 8, releaseRightLimit, lockFactor);
            hits.handleFlag(christmasValue, vectorTransformValue, identifierText, 36.0F, () -> {
               this.christmasAnims.handle(row.pressAnimKey, 1.0F);
               base01.toggle();
               MatrixEspUtils.updateFlagTime();
            }, this.christmasAnims, row.cardAnimKey);
            yield christmasValue8 - 22.0F;
         }
         case SEGMENT -> {
            EnumSetting base012 = (EnumSetting)row.baseChristmas;
            MatrixStackUtilsAmount.render(context, christmasValue, vectorTransformValue, identifierText, segmentSlider, lockFactor * 0.5F, 0.0F, 0.0F);
            int christmasLimit = base012 == this.musicvol.enumHelper && !HalloweenChristmasType.isTimeFlag() ? base012.getOptions().indexOf("Christmas") : -1;
            String[] texts = christmasLimit < 0 ? row.christmasTexts : this.getTexts(row.christmasTexts, christmasLimit);
            float amountForDrawContextHits = MatrixStackUtilsAmount.getAmountForDrawContextHits(
               context, hits, this.christmasAnims, row.christmasText, texts, base012.getCurrentIndex(), christmasValue8, brownValue, 16.0F, matrixStackLimit, scaledY, index -> {
                  if (index != christmasLimit) {
                     base012.handle(index);
                     MatrixEspUtils.updateFlagTime();
                  }
               }
            );
            if (christmasLimit >= 0) {
               this.handleLockSeason(texts, christmasLimit, amountForDrawContextHits, brownValue, matrixStackLimit, scaledY);
            }

            yield amountForDrawContextHits;
         }
         case SLIDER -> {
            MatrixStackUtilsAmount.render(context, christmasValue, vectorTransformValue, identifierText, segmentSlider, lockFactor * 0.5F, 0.0F, 0.0F);
            this.renderTextChristmas(context, hits, row, christmasValue, vectorTransformValue, identifierText, abcdefghijklmnopqrstuvwxyzBrown, matrixStackLimit, scaledY);
            yield christmasValue8 - 24.0F;
         }
         case COLOR -> {
            MatrixStackUtilsAmount.render(context, christmasValue, vectorTransformValue, identifierText, segmentSlider, lockFactor * 0.5F, 0.0F, 0.0F);
            float christmasValue15 = christmasValue8 - 22.0F;
            float configsLikeValue = vectorTransformValue + 11.0F;
            float maxThemeTabsAmount = Math.max(this.christmasAnims.getAmount(row.christmasText, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, christmasValue, vectorTransformValue, identifierText, 36.0F)), row.themeTabsAmount);
            this.render(context, christmasValue15, configsLikeValue, row.scissorMax.getSupplierColor(), maxThemeTabsAmount, this.christmasAnims.getTime(row.christmasText, 0.07F));
            hits.handleFlag(christmasValue, vectorTransformValue, identifierText, 36.0F, () -> {
               row.themeTabsFlag = !row.themeTabsFlag;
               if (row.themeTabsFlag) {
                  row.scissorMax.update();
               }
            }, this.christmasAnims, row.christmasText);
            if (row.themeTabsAmount > 0.004F) {
               this.renderScissorTabs(context, hits, row, christmasValue, vectorTransformValue, identifierText, segmentSlider);
            }

            yield christmasValue15;
         }
      };
      float christmasValue17 = christmasValue12 - 5.0F - 14.0F;
      float christmasValue18 = row.segmentSliderType == ResetLock.Kind.SLIDER ? vectorTransformValue + 7.0F + -1.5F : vectorTransformValue + 11.0F;
      this.renderText(context, hits, row, christmasValue17, christmasValue18, lockFactor, matrixStackLimit, scaledY);
      float vertexTextureLimit = christmasValue17 - 4.0F - matrixStackValue;
      MatrixStackUtilsAmount.handle(fontRenderer.formatText(row.labelText, vertexTextureLimit), matrixStackValue, abcdefghijklmnopqrstuvwxyzBrown, -920587);
      if (row.rowText != null) {
         MatrixStackUtilsAmount.handlePushScale(
            fontRenderer.formatText(row.rowText, vertexTextureLimit / 0.82F), matrixStackValue, MatrixStackUtilsAmount.getAmount(vectorTransformValue + 18.0F, 11.0F, 0.82F), 0.82F, -7630437
         );
      }
   }

   public boolean isFlag() {
      return false;
   }

   private static void handleScissorBase(ResetLock.Row row) {
      ConfigSetting base01 = row.baseChristmas;
      if (base01 instanceof BooleanSetting booleanBase) {
         booleanBase.handle(booleanBase.isFlag3());
      } else if (base01 instanceof EnumSetting enumHelper) {
         enumHelper.handle(enumHelper.getValue());
      } else if (base01 instanceof DoubleSetting doubleHelper) {
         doubleHelper.handleMaxAmount(doubleHelper.getAmount3());
         ModuleConfigAutosave.updateFlag2();
      } else if (base01 instanceof ColorSetting colorBase) {
         colorBase.setColor(colorBase.getDefaultColor());
         row.scissorMax.update();
      }
   }

   private void renderArmAmount(DrawContext context, String armText, int armValue, int vectorTransformValue, int materialFactor, float armBound, int matrixStackLimit, ResetLock.Body body) {
      MatrixStackUtilsAmount.Entrance amountFlag = this.tabsResetEntrance.getAmountFlag(this.armValue++);
      if (!amountFlag.isAmount()) {
         float tabsSettingsAmount = amountFlag.tabsSettingsAmount;
         float armLimit = amountFlag.entranceProgress;
         MatrixStackUtilsAmount.Hits hits = amountFlag.matrixScissorActive ? this.tabsResetHits : MatrixStackUtilsAmount.Hits.INSTANCE;
         int bodyValue = amountFlag.activeFlag ? matrixStackLimit : armValue - 4000;
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, armValue + materialFactor / 2.0F, vectorTransformValue + Math.min(armBound, 36.0F) / 2.0F, amountFlag.matrixScissorAmount * this.christmasAnims.getTime(armText, 0.025F)
         );

         try {
            if (tabsSettingsAmount > 0.004F) {
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(tabsSettingsAmount);

               try {
                  MatrixStackUtilsAmount.render(context, armValue, vectorTransformValue, materialFactor, armBound, 0.0F, 0.0F, 0.0F);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }
            }

            if (armLimit > 0.002F) {
               float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(armLimit);

               try {
                  body.draw(hits, bodyValue);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
               }
            }
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }
      }
   }

   private void handleLockSeason(String[] texts, int index, float lockSeasonValue, float decemberRevealsAmount, int matrixStackLimit, int scaledY) {
      float lockSeasonTotal = lockSeasonValue + 1.0F;

      for (int i = 0; i < index; i++) {
         lockSeasonTotal += (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(texts[i])) + 12.0F + 1.0F;
      }

      float identifierText = (float)Math.ceil(MatrixStackUtilsAmount.getAmount3(texts[index])) + 12.0F;
      float matrixPeekValue = lockSeasonTotal + 6.0F + MatrixStackUtilsAmount.getAmount3(texts[index].trim()) + 2.0F;
      float idxOffset = decemberRevealsAmount + 4.0F;
      boolean lockSeasonFlag = MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, lockSeasonTotal, decemberRevealsAmount, identifierText, 16.0F);
      float cardMatrixFactor = this.christmasAnims.getAmount("lock:season", lockSeasonFlag);
      MatrixPeekUtilsIdentifier.handle(
         matrixPeekValue, idxOffset, 8.0F, 8.0F, identifier, new Color(CardMatrixUtils.getValue(-7630437, -920587, cardMatrixFactor), true), MatrixPeekUtilsRender.getMatrixStack()
      );
      if (lockSeasonFlag) {
         this.lockTipFlag = true;
         this.lockSeasonAmount = lockSeasonTotal + identifierText / 2.0F;
         this.decemberRevealsAmount = decemberRevealsAmount;
      }
   }

   public void renderRevealsDecember(DrawContext context) {
      float revealsDecemberValue = this.christmasAnims.getAmount("lock:tip", this.lockTipFlag ? 1.0F : 0.0F, 14.0F);
      if (!(revealsDecemberValue < 0.01F)) {
         String revealsDecemberText = "Reveals itself on 1 December";
         float revealsDecemberValue2 = MatrixStackUtilsAmount.getAmount3(revealsDecemberText) + 14.0F;
         float materialFactor = 21.0F;
         float matrixPeekValue = MatrixStackUtilsAmount.getAmount(this.lockSeasonAmount - revealsDecemberValue2 / 2.0F);
         float matrixStackValue = MatrixStackUtilsAmount.getAmount(this.decemberRevealsAmount - materialFactor - 5.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(revealsDecemberValue);

         try {
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, revealsDecemberValue2, materialFactor, 8.0F, -65990120, MatrixStackUtilsAmount.getValue(-1, 0.09F));
            MatrixStackUtilsAmount.handle(revealsDecemberText, matrixPeekValue + 7.0F, MatrixStackUtilsAmount.getAmount(matrixStackValue + 5.0F, 11.0F, 1.0F), -920587);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private void renderText(DrawContext context, MatrixStackUtilsAmount.Hits hits, ResetLock.Row row, float lockValue, float matrixStackValue, float lockFactor, int matrixStackLimit, int scaledY) {
      float lockLimit = this.christmasAnims.getAmount(row.resetAnimKey, MatrixStackUtilsAmount.canFlag(matrixStackLimit, scaledY, lockValue, matrixStackValue, 14.0F, 14.0F));
      float panelOverlayLimit = lockFactor * this.christmasAnims.getAmount(row.liveAnimKey, canBase(row) ? 1.0F : 0.45F, 12.0F);
      if (panelOverlayLimit > 0.01F) {
         float lockValue5 = 2.0F;
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, lockValue + 7.0F, matrixStackValue + 7.0F, this.christmasAnims.getTime(row.resetAnimKey, 0.07F));

         try {
            if (lockLimit > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeek(context, lockValue, matrixStackValue, 14.0F, 14.0F, 4.0F, MatrixStackUtilsAmount.getValue(-1, 0.09F * lockLimit));
            }

            MatrixPeekUtilsIdentifier.handle(
               lockValue + lockValue5,
               matrixStackValue + lockValue5,
               10.0F,
               10.0F,
               IcoGenUtils.getIcoReset(),
               new Color(CardMatrixUtils.getValue(-7630437, -920587, lockLimit), true),
               MatrixPeekUtilsRender.getMatrixStack()
            );
         } finally {
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }

      hits.handleFlag(lockValue - 1.0F, matrixStackValue - 1.0F, 16.0F, 16.0F, () -> {
         MatrixEspUtils.updateFlag();
         handleScissorBase(row);
         MatrixEspUtils.updateFlagTime();
      }, this.christmasAnims, row.resetAnimKey);
   }

   public void renderTabsReset(DrawContext context, int tabsResetValue, int ctxOffset, int sideWalkAmount, int matrixStackLimit, int mouseY) {
      if (HalloweenChristmasType.getTimeInstance() != this.halloweenChristmasType) {
         this.updateMusicSong();
      }

      this.tabsResetHits.update();
      this.lockTipFlag = false;
      boolean timeFlag = this.tabsResetEntrance.isTimeFlag();
      if (timeFlag) {
         this.doubleHelper = null;
         this.resetThemeTime = 0L;
         this.christmasAnims.handle("reset:arm", 0.0F);
      }

      if (timeFlag || this.tabs != this.armResetValue) {
         this.tabsResetEntrance.updateTimeFlag();
      }

      this.armResetValue = this.tabs;
      this.armValue = 0;

      for (ResetLock.Row row : this.musicSongItems) {
         if (row.segmentSliderType == ResetLock.Kind.COLOR) {
            row.themeTabsAmount = this.christmasAnims.getAmount(row.themeTabsText, row.themeTabsFlag ? 1.0F : 0.0F, 16.0F);
         }
      }

      float amountForDrawContextString = MatrixStackUtilsAmount.getAmountForDrawContextString(context, "Theme", tabsResetValue, ctxOffset, 22.0F);
      MatrixStackUtilsAmount.getAmount(
         context, this.tabsResetHits, this.christmasAnims, "tabs", tabsMotionTexts, this.tabs, amountForDrawContextString + 10.0F, ctxOffset, 22.0F, matrixStackLimit, mouseY, this::handleControlsTabs
      );
      MatrixStackUtilsAmount.getAmount(
         context,
         this.tabsResetHits,
         this.christmasAnims,
         "reset",
         IcoGenUtils.getIcoReset(),
         "Reset all?",
         (float)(tabsResetValue + sideWalkAmount),
         (float)ctxOffset,
         22.0F,
         matrixStackLimit,
         mouseY,
         System.currentTimeMillis() < this.resetThemeTime,
         this::updateThemeReset
      );
      float tabsResetTotal = ctxOffset + 22 + 6;
      boolean tabsResetFlag = this.tabs == 0;
      boolean tabsResetFlag2 = true;

      for (int i = 0; i < motionAmbientTexts.length; i++) {
         if (tabsResetFlag || i == this.tabs - 1) {
            if (tabsResetFlag) {
               if (!tabsResetFlag2) {
                  tabsResetTotal += 9.0F;
               }

               tabsResetFlag2 = false;
               float panelOverlay = CorzRoundedUtils.getPanelOverlay(this.tabsResetEntrance.getAmount(this.armValue));

               try {
                  MatrixStackUtilsAmount.renderRoot(context, motionAmbientTexts[i], tabsResetValue, MatrixStackUtilsAmount.getAmount(tabsResetTotal), sideWalkAmount);
               } finally {
                  CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               }

               tabsResetTotal += 12.0F;
            }

            tabsResetTotal += i == 0 ? this.getTexturesIcons(context, tabsResetValue, tabsResetTotal, sideWalkAmount, matrixStackLimit, mouseY) : this.getText(context, this.musicSongListList.get(i), tabsResetValue, tabsResetTotal, sideWalkAmount, matrixStackLimit, mouseY);
         }
      }

      this.remainingResetTicks = (int)Math.ceil(tabsResetTotal - ctxOffset);
   }

   private void renderScissorTabs(DrawContext context, MatrixStackUtilsAmount.Hits hits, ResetLock.Row row, int scissorTabsValue, int matrixStackValue, int identifierText, float segmentSlider) {
      int ctxOffset = scissorTabsValue + 8;
      int plusDismissValue = matrixStackValue + 36;
      int materialFactor = identifierText - 16;
      int scissorTabsValue7 = plusDismissValue - 6;
      int scissorTabsLimit = (int)Math.floor(matrixStackValue + segmentSlider - 2.0F);
      row.scissorMax.handleFlag(ctxOffset, plusDismissValue, materialFactor);
      if (scissorTabsLimit > scissorTabsValue7) {
         context.enableScissor(scissorTabsValue, scissorTabsValue7, scissorTabsValue + identifierText, scissorTabsLimit);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getMax(row.themeTabsAmount * 1.5F));

         try {
            row.scissorMax.render(context);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            context.disableScissor();
         }
      }

      if (row.themeTabsFlag && row.themeTabsAmount > 0.3F) {
         hits.handle(ctxOffset, plusDismissValue, materialFactor, 84.0F, () -> row.scissorMax.canFlag(this.scrollProgress, this.scissorTabsAmount));
      }
   }

   private void handleSlider(int sliderValue, String sliderText, String sliderText2, DoubleSetting doubleHelper, boolean sliderFlag) {
      this.handleMusicSong(sliderValue, new ResetLock.Row(sliderText, ResetLock.Kind.SLIDER, sliderText2, null, doubleHelper, null, sliderFlag));
   }

   private String[] getTexts(String[] texts, int index) {
      if (this.clone == null) {
         String lockText = " ";

         while (MatrixStackUtilsAmount.getAmount3(lockText) < 10.0F && lockText.length() < 8) {
            lockText = lockText + " ";
         }

         this.clone = (String[])texts.clone();
         this.clone[index] = texts[index] + lockText;
      }

      return this.clone;
   }

   private void handleColor(int colorValue, String colorText, String colorText2, String colorText3, ColorSetting colorBase, ScissorMax scissorMax) {
      this.handleMusicSong(colorValue, new ResetLock.Row(colorText, ResetLock.Kind.COLOR, colorText2, colorText3, colorBase, scissorMax, false));
   }

   public void handleScissorMusicForDoubleInt(double scissorMusicValue, double minmaxValue, int button) {
      for (ResetLock.Row row : this.musicSongItems) {
         if (row.scissorMax != null) {
            row.scissorMax.handleFlag(scissorMusicValue, minmaxValue, button);
         }
      }

      if (this.doubleHelper != null) {
         this.handle(scissorMusicValue);
      }
   }

   public ResetLock(ThemeCustomizer musicvol) {
      this.musicSongItems = new ArrayList<>();
      this.musicHalloweenItems = new ArrayList<>();
      this.tabsResetHits = new MatrixStackUtilsAmount.Hits();
      this.christmasAnims = new MatrixStackUtilsAmount.Anims();
      this.tabsResetEntrance = new MatrixStackUtilsAmount.Entrance();
      this.armResetValue = -1;
      this.armValue = 0;
      this.doubleHelper = null;
      this.resetThemeTime = 0L;
      this.remainingResetTicks = 200;
      this.musicvol = musicvol;

      for (int i = 0; i < motionAmbientTexts.length; i++) {
         this.musicSongListList.add(new ArrayList<>());
      }

      this.tabs = Math.max(0, Math.min(tabsMotionTexts.length - 1, controlsTexturesValue));
      this.scissorMax = new ScissorMax(musicvol.main, 0, 0, 100, 0);
      this.scissorMax.setIntSupplier(() -> musicvol.main.getColor() & 0xFF000000 | MatrixEspUtils.releaseRightValue & 16777215);
      this.handleColor(1, "main", "Main Color", "Accent for highlights and controls", musicvol.main, this.scissorMax);
      this.handle(1, "toggle", ResetLock.Kind.SEGMENT, "Toggle Style", "Look of on/off switches", musicvol.toggleStyle);
      this.handle(2, "glass", ResetLock.Kind.TOGGLE, "See-Through GUI", "Translucent window and cards", musicvol.seeThroughGui);
      this.handle(2, "frost", ResetLock.Kind.TOGGLE, "Frosted Blur", "Blur the game behind the glass", musicvol.frostedBlur);
      this.handle(2, "season", ResetLock.Kind.SEGMENT, "Season", "Auto = by date", musicvol.enumHelper);
      this.updateMusicSong();
      this.handle(3, "ambient", ResetLock.Kind.TOGGLE, "Ambient Background", "Soft glow behind the window", musicvol.ambientBackground);
      this.handle(3, "mode", ResetLock.Kind.SEGMENT, "Color Mode", "Glow source", musicvol.ambientColorMode);
      this.handleColor(3, "primary", "Primary Color", "Used by Custom and Two Color", musicvol.primary, new ScissorMax(musicvol.primary, 0, 0, 100, 0));
      this.handleColor(3, "secondary", "Secondary Color", "Used by Two Color", musicvol.secondary, new ScissorMax(musicvol.secondary, 0, 0, 100, 0));
      this.handleSlider(3, "intensity", "Intensity", musicvol.intensity, true);
      this.handleSlider(3, "opacity", "Opacity", musicvol.opacity, true);
      this.handleSlider(3, "radius", "Radius", musicvol.radius, false);
      this.handle(4, "drift", ResetLock.Kind.TOGGLE, "Ambient Animation", "Slow drift of the glow", musicvol.ambientAnimation);
      this.handleSlider(4, "speed", "Animation Speed", musicvol.speed, false);
   }

   public boolean canAmount(double amount, double iconsClearValue, int lockValue) {
      if (lockValue != 0) {
         return false;
      } else {
         this.scrollProgress = amount;
         this.scissorTabsAmount = iconsClearValue;
         return this.tabsResetHits.canAnimsPress(amount, iconsClearValue);
      }
   }

   private void handle(int lockValue, String lockText, ResetLock.Kind kind, String lockText2, String lockText3, ConfigSetting autoEspBase) {
      this.handleMusicSong(lockValue, new ResetLock.Row(lockText, kind, lockText2, lockText3, autoEspBase, null, false));
   }

   public int getValue() {
      return this.remainingResetTicks;
   }

   private static boolean canBase(ResetLock.Row row) {
      ConfigSetting base01 = row.baseChristmas;
      if (base01 instanceof BooleanSetting booleanBase) {
         return booleanBase.isEnabled() != booleanBase.isFlag3();
      } else if (base01 instanceof EnumSetting enumHelper) {
         return enumHelper.getCurrentIndex() != enumHelper.getValue();
      } else if (base01 instanceof DoubleSetting doubleHelper) {
         return Math.abs(doubleHelper.getValue() - doubleHelper.getAmount3()) > 1.0E-6;
      } else {
         return base01 instanceof ColorSetting colorBase ? colorBase.getColor() != colorBase.getDefaultColor() : false;
      }
   }

   static {
      for (int i = 0; i < lockTexts.length; i++) {
         lockTexts[i] = "preset:" + i;
         texturesIconsTexts[i] = "preset:sel:" + i;
      }

      controlsTexturesValue = 0;
   }

   private static float getSegmentSlider(ResetLock.Row row) {
      return row.segmentSliderType == ResetLock.Kind.COLOR ? MatrixStackUtilsAmount.getAmount(36.0F + 92.0F * row.themeTabsAmount) : 36.0F;
   }

   private void updateMusicSong() {
      this.musicSongListList.get(2).removeAll(this.musicHalloweenItems);
      this.musicSongItems.removeAll(this.musicHalloweenItems);
      this.musicHalloweenItems.clear();
      HalloweenChristmasType halloweenChristmasType = HalloweenChristmasType.getTimeInstance();
      this.halloweenChristmasType = halloweenChristmasType;
      int musicsongitemsSize = this.musicSongItems.size();
      if (halloweenChristmasType == HalloweenChristmasType.HALLOWEEN) {
         this.handle(
            2, "halloween", ResetLock.Kind.TOGGLE, HalloweenMatrixUtils.BATS_COBWEBS_TEXT, "Bats and cobwebs, ends 2 November", this.musicvol.musicSongBooleanBase
         );
      } else if (halloweenChristmasType == HalloweenChristmasType.CHRISTMAS) {
         this.handle(
            2, "christmas", ResetLock.Kind.TOGGLE, SnowVertexUtils.JANUARY_SNOW_TEXT, "Snow, warm lights and frost, ends 2 January", this.musicvol.themeCustomizerBooleanBase
         );
      }

      if (halloweenChristmasType != HalloweenChristmasType.NONE) {
         this.handle(
            2,
            "music",
            ResetLock.Kind.TOGGLE,
            "Theme Music",
            halloweenChristmasType == HalloweenChristmasType.HALLOWEEN ? "Spooky song while the GUI is open" : "Cozy song while the GUI is open",
            this.musicvol.booleanBase
         );
         this.handleSlider(2, "musicvol", "Music Volume", this.musicvol.doubleHelper, true);
      }

      this.musicHalloweenItems.addAll(this.musicSongItems.subList(musicsongitemsSize, this.musicSongItems.size()));
   }

   private interface Body {
      void draw(MatrixStackUtilsAmount.Hits hits, int bodyValue);
   }

   private static enum Kind {
      TOGGLE,
      SEGMENT,
      SLIDER,
      COLOR;
   }

   private static final class Row {
      final String themeTabsText;
      final ScissorMax scissorMax;
      final String rowText;
      final String onAnimKey;
      final ResetLock.Kind segmentSliderType;
      final String resetAnimKey;
      final ConfigSetting baseChristmas;
      final String[] christmasTexts;
      final String labelText;
      final String pressAnimKey;
      final String christmasText;
      final boolean christmasActive;
      float themeTabsAmount;
      final String cardAnimKey;
      final String liveAnimKey;
      boolean themeTabsFlag;
      final String valueAnimKey;

      Row(String rowText, ResetLock.Kind segmentSliderType, String text, String text2, ConfigSetting base, ScissorMax scissorMax, boolean flag) {
         this.segmentSliderType = segmentSliderType;
         this.labelText = text;
         this.rowText = text2;
         this.baseChristmas = base;
         this.scissorMax = scissorMax;
         this.christmasActive = flag;
         this.christmasTexts = base instanceof EnumSetting enumHelper ? enumHelper.getOptions().toArray(new String[0]) : null;
         this.cardAnimKey = "card:" + rowText;
         this.christmasText = "ctl:" + rowText;
         this.onAnimKey = "on:" + rowText;
         this.pressAnimKey = "press:" + rowText;
         this.valueAnimKey = "val:" + rowText;
         this.themeTabsText = "open:" + rowText;
         this.resetAnimKey = "reset:" + rowText;
         this.liveAnimKey = "live:" + rowText;
      }
   }
}
