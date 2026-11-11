package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.utils.AmountPreviewUtils;

public final class PreviewText {
   static final int UNIT_STEP = 1;
   private float cachedAmount;
   private String consumerActionText;
   private int revealedItemCount;
   private final List<PreviewText.Item> consumerActionItemList = new ArrayList<>();
   private static final String[] previewTexts = new String[256];
   private String previewText;
   private String cachedFormattedText;
   private boolean consumerActionActive;
   private final Map<String, PreviewText.State> previewStateMap = new HashMap<>();

   private String formatTextAmount(String formatText, float amount) {
      if (formatText.equals(this.previewText) && amount == this.cachedAmount && this.cachedFormattedText != null) {
         return this.cachedFormattedText;
      } else {
         this.previewText = formatText;
         this.cachedAmount = amount;
         this.cachedFormattedText = MatrixStackUtilsAmount.getFontRenderer().formatText(formatText, amount / 0.82F);
         return this.cachedFormattedText;
      }
   }

   public PreviewText getClassForStringIntArray(String classText, int[] classValues, int classValue, IntConsumer intConsumer) {
      return classValues != null && classValues.length != 0 ? this.getTextFlag(4, classText, "", null, false, classValues, classValue, null, intConsumer) : this;
   }

   void updateConsumerAction() {
      for (int i = 0; i < this.revealedItemCount; i++) {
         PreviewText.Item item = this.consumerActionItemList.get(i);
         item.action = null;
         item.intConsumer = null;
         item.consumerActionTable = null;
      }

      this.revealedItemCount = 0;
      this.consumerActionText = null;
      this.consumerActionActive = false;
   }

   public PreviewText getClassForStringRunnable(String classText, String classText2, Runnable runnable) {
      return this.getTextFlag(1, classText, classText2, null, false, null, 0, runnable, null);
   }

   private static float getAmount(float previewValue) {
      return (float)Math.ceil(previewValue);
   }

   public PreviewText getClassPreviewText(String classText, String classText2, Runnable runnable) {
      return this.getTextFlag(5, classText, classText2, null, false, null, 0, runnable, null);
   }

   private PreviewText getTextFlag(int value2, String previewText, String previewText2, String text, boolean flag, int[] values, int classValue, Runnable action, IntConsumer intConsumer) {
      PreviewText.Item item;
      if (this.revealedItemCount < this.consumerActionItemList.size()) {
         item = this.consumerActionItemList.get(this.revealedItemCount);
      } else {
         item = new PreviewText.Item();
         this.consumerActionItemList.add(item);
      }

      this.revealedItemCount++;
      item.displayMode = value2;
      item.stateKey = previewText != null ? previewText : (previewText2 != null ? previewText2 : "item" + this.revealedItemCount);
      item.labelText = previewText2 == null ? "" : previewText2;
      item.valueText = text;
      item.toggleActive = flag;
      item.consumerActionTable = values;
      item.selectedIndex = classValue;
      item.action = action;
      item.intConsumer = intConsumer;
      item.actionEnabled = this.consumerActionActive;
      item.isVisible = false;
      return this;
   }

   private void renderAmountText(
      DrawContext context, PreviewText.Item item, PreviewText.State state, float previewValue, float previewLimit, float previewLimit2, boolean previewFlag, MinButton minButton, ConsumerButton consumerButton, float previewFactor
   ) {
      float materialFactor = 16.0F;
      if (item.displayMode == 4) {
         this.renderAmountsAmount(context, item, state, previewValue, previewLimit, previewLimit2, previewFlag, consumerButton, previewFactor);
      } else {
         boolean previewFlag2 = item.displayMode != 0;
         boolean previewFlag3 = previewFlag && previewFlag2 && previewLimit >= item.itemX && previewLimit <= item.itemX + item.amountText && previewLimit2 >= previewValue - 1.0F && previewLimit2 <= previewValue + materialFactor + 1.0F;
         state.hoverProgress = AmountPreviewUtils.getAmount(state.hoverProgress, previewFlag3 ? 1.0F : 0.0F, previewFlag3 ? 16.0F : 11.0F, previewFactor, 0.002F);
         state.highlightProgress = AmountPreviewUtils.getAmount(state.highlightProgress, item.displayMode == 2 && item.toggleActive ? 1.0F : 0.0F, 18.0F, previewFactor, 0.002F);
         float hoverProgress = state.hoverProgress;
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, item.itemX + item.amountText / 2.0F, previewValue + materialFactor / 2.0F, minButton.getAmount(item.stateKey, 0.05F), 0.0F);

         try {
            switch (item.displayMode) {
               case 0:
                  MatrixStackUtilsRelease.render(context, item.itemX, previewValue, item.amountText, materialFactor, 0.0F, 0.0F);
                  MatrixStackUtilsRelease.handle(item.labelText, item.itemX + 7.0F, previewValue, materialFactor, 0.82F, -7630437);
                  break;
               case 1:
               case 2:
               case 4:
               default:
                  MatrixStackUtilsRelease.render(context, item.itemX, previewValue, item.amountText, materialFactor, hoverProgress, state.highlightProgress);
                  handle(item.labelText, item.itemX, previewValue, item.amountText, materialFactor, MatrixStackUtilsRelease.getReleaseRight(hoverProgress, state.highlightProgress));
                  break;
               case 3:
                  this.renderTextAmount(context, item, state, previewValue, materialFactor, hoverProgress);
                  break;
               case 5:
                  MatrixStackUtilsRelease.render(context, item.itemX, previewValue, item.amountText, materialFactor, hoverProgress, 0.0F);
                  handle(item.labelText, item.itemX, previewValue, item.amountText, materialFactor, MatrixStackUtilsRelease.getReleaseRight(hoverProgress, 0.0F));
                  break;
               case 6:
                  MatrixStackUtilsRelease.renderReleaseRight(context, item.itemX, previewValue, item.amountText, materialFactor, hoverProgress);
                  handle(item.labelText, item.itemX, previewValue, item.amountText, materialFactor, MatrixStackUtilsAmount.getReleaseRight());
            }
         } finally {
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
         }

         if (previewFlag) {
            if (item.displayMode == 3) {
               consumerButton.handleMaxConsumer(item.itemX, previewValue - 1.0F, item.amountText, materialFactor + 2.0F, 1, item.stateKey, null, item.intConsumer, 0);
            } else if (previewFlag2) {
               consumerButton.handleMaxConsumer(item.itemX, previewValue - 1.0F, item.amountText, materialFactor + 2.0F, 0, item.stateKey, item.action, null, 0);
            }
         }
      }
   }

   private static void handle(String previewText, float previewValue, float previewValue2, float previewValue3, float materialFactor, int matrixStackValue) {
      float previewValue6 = MatrixStackUtilsRelease.getAmount(previewText, 0.82F);
      MatrixStackUtilsRelease.handle(previewText, MatrixStackUtilsAmount.getAmount(previewValue + (previewValue3 - previewValue6) / 2.0F), previewValue2, materialFactor, 0.82F, matrixStackValue);
   }

   public PreviewText() {
      this.cachedAmount = -1.0F;
   }

   public PreviewText getFlag() {
      this.consumerActionActive = true;
      return this;
   }

   public PreviewText getClassForString(String classText, String classText2) {
      return this.getTextFlag(0, classText, classText2, null, false, null, 0, null, null);
   }

   public PreviewText getClass073(String classText, String classText2, Runnable runnable) {
      return this.getTextFlag(6, classText, classText2, null, false, null, 0, runnable, null);
   }

   public PreviewText getClassForStringIntConsumer(String classText, String classText2, String folderDoneText, IntConsumer intConsumer) {
      return this.getTextFlag(3, classText, classText2, folderDoneText == null ? "" : folderDoneText, false, null, 0, null, intConsumer);
   }

   static String formatTexts(int index) {
      if (index >= 0 && index < previewTexts.length) {
         String formatTexts = previewTexts[index];
         if (formatTexts == null) {
            previewTexts[index] = formatTexts = Integer.toString(index);
         }

         return formatTexts;
      } else {
         return Integer.toString(index);
      }
   }

   private void renderAmountsAmount(
      DrawContext context, PreviewText.Item item, PreviewText.State state, float previewValue, float previewLimit, float previewValue3, boolean previewFlag, ConsumerButton consumerButton, float previewFactor
   ) {
      int previewSize = item.consumerActionTable.length;
      if (state.table.length != previewSize) {
         state.table = new float[previewSize];
      }

      float translatePushValue = previewValue + 8.0F;
      float previewValue6 = item.itemX + 2.5F + 5.5F;
      int max7 = Math.max(0, Math.min(previewSize - 1, item.selectedIndex));
      float thumbsLimit = previewValue6 + max7 * 17.0F;
      state.smoothedOffset = Float.isNaN(state.smoothedOffset) ? thumbsLimit : AmountPreviewUtils.getAmount(state.smoothedOffset, thumbsLimit, 20.0F, previewFactor, 0.02F);
      float previewValue9 = 5.5F;

      for (int i = 0; i < previewSize; i++) {
         float x = previewValue6 + i * 17.0F;
         boolean previewFlag2 = previewFlag && Math.abs(previewLimit - x) <= previewValue9 + 2.5F && Math.abs(previewValue3 - translatePushValue) <= previewValue9 + 2.5F;
         state.table[i] = AmountPreviewUtils.getAmount(state.table[i], previewFlag2 && i != max7 ? 1.0F : 0.0F, previewFlag2 ? 16.0F : 11.0F, previewFactor, 0.002F);
         float x2 = 1.0F + 0.12F * state.table[i];
         boolean translatePush = MatrixStackUtilsRelease.canTranslatePush(context, x, translatePushValue, x2, 0.0F);

         try {
            int matrixValue = 0xFF000000 | item.consumerActionTable[i];
            int matrixStackValue = MatrixAmountUtils.getAmountForInt(matrixValue) > 0.6F ? 771751936 : 788529151;
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, x - previewValue9, translatePushValue - previewValue9, 11.0F, 11.0F, previewValue9, matrixValue, matrixStackValue);
         } finally {
            MatrixStackUtilsRelease.renderPopMatrices(context, translatePush);
         }

         if (previewFlag) {
            consumerButton.handleMaxConsumer(x - previewValue9 - 2.5F, translatePushValue - previewValue9 - 2.5F, 16.0F, 16.0F, 2, item.stateKey, null, item.intConsumer, i);
         }
      }

      float corzLicenseLimit = previewValue9 + 2.5F;
      MatrixPeekUtilsRender.renderMatrixPeek(
         context,
         state.smoothedOffset - corzLicenseLimit,
         translatePushValue - corzLicenseLimit,
         corzLicenseLimit * 2.0F,
         corzLicenseLimit * 2.0F,
         corzLicenseLimit,
         MatrixPeekUtilsRender.getAmount() * 1.5F,
         MatrixStackUtilsAmount.getValue(MatrixEspUtils.releaseRightValue, 0.95F)
      );
   }

   public PreviewText getClassForStringInt(String classText, String classText2, int classValue, IntConsumer intConsumer) {
      return this.getClassForStringIntConsumer(classText, classText2, formatTexts(classValue), intConsumer);
   }

   private PreviewText.State getTimeForString(String previewText) {
      PreviewText.State state = this.previewStateMap.get(previewText);
      if (state == null) {
         state = new PreviewText.State();
         this.previewStateMap.put(previewText, state);
      }

      return state;
   }

   void renderAmountText(DrawContext context, float previewValue, float matrixStackValue, float previewValue3, float materialFactor, float previewValue5, float previewValue6, boolean previewFlag, MinButton minButton, ConsumerButton consumerButton) {
      float min7 = minButton.getMin();
      float previewValue8 = previewValue + previewValue3;
      boolean previewFlag2 = false;

      for (int i = this.revealedItemCount - 1; i >= 0; i--) {
         PreviewText.Item item = this.consumerActionItemList.get(i);
         if (item.actionEnabled) {
            PreviewText.State state = this.getTimeForString(item.stateKey);
            item.amountText = this.getAmountText(item, state, min7);
            float amount = previewValue8 - item.amountText;
            item.itemX = amount;
            item.isVisible = amount >= previewValue;
            previewValue8 = amount - 4.0F;
            previewFlag2 = true;
         }
      }

      float previewValue9 = previewFlag2 ? previewValue8 + 4.0F - 10.0F : previewValue + previewValue3;
      float previewTotal = 0.0F;

      for (int j = 0; j < this.revealedItemCount; j++) {
         PreviewText.Item item2 = this.consumerActionItemList.get(j);
         if (!item2.actionEnabled) {
            item2.amountText = this.getAmountText(item2, this.getTimeForString(item2.stateKey), min7);
            previewTotal += item2.amountText + 4.0F;
         }
      }

      float previewValue2 = previewValue;
      if (this.consumerActionText != null) {
         float previewLimit = previewValue9 - previewValue - previewTotal - (previewTotal > 0.0F ? 10.0F : 0.0F);
         if (previewLimit >= 28.0F) {
            String formatTextAmount = this.formatTextAmount(this.consumerActionText, previewLimit);
            MatrixStackUtilsRelease.handle(formatTextAmount, previewValue + 1.0F, matrixStackValue, materialFactor, 0.82F, -7630437);
            previewValue2 = previewValue + getAmount(MatrixStackUtilsRelease.getAmount(formatTextAmount, 0.82F)) + 10.0F;
         }
      }

      for (int k = 0; k < this.revealedItemCount; k++) {
         PreviewText.Item item3 = this.consumerActionItemList.get(k);
         if (!item3.actionEnabled) {
            item3.itemX = previewValue2;
            item3.isVisible = previewValue2 + item3.amountText <= previewValue9 + 0.5F;
            previewValue2 += item3.amountText + 4.0F;
         }
      }

      float previewValue10 = matrixStackValue + (materialFactor - 16.0F) / 2.0F;

      for (int m = 0; m < this.revealedItemCount; m++) {
         PreviewText.Item item4 = this.consumerActionItemList.get(m);
         if (item4.isVisible) {
            this.renderAmountText(context, item4, this.getTimeForString(item4.stateKey), previewValue10, previewValue5, previewValue6, previewFlag, minButton, consumerButton, min7);
         }
      }
   }

   private void renderTextAmount(DrawContext context, PreviewText.Item item, PreviewText.State state, float previewValue, float materialFactor, float cardMatrixFactor) {
      MatrixStackUtilsRelease.render(context, item.itemX, previewValue, item.amountText, materialFactor, cardMatrixFactor, 0.0F);
      float previewValue4 = item.itemX + 7.0F;
      MatrixStackUtilsRelease.handle(item.labelText, previewValue4, previewValue, materialFactor, 0.82F, CardMatrixUtils.getValue(-7630437, -5854026, cardMatrixFactor));
      float matrixStackValue = previewValue4 + getAmount(MatrixStackUtilsRelease.getAmount(item.labelText, 0.82F)) + 3.5F;
      long nanoTime = System.nanoTime();
      if (state.displayedText == null) {
         state.displayedText = item.valueText;
      } else if (!Objects.equals(state.displayedText, item.valueText)) {
         state.previousText = state.displayedText;
         state.displayedText = item.valueText;
         state.lastNanoTime = nanoTime;
      }

      int previewValue6 = CardMatrixUtils.getValue(-920587, CardMatrixUtils.getValue(MatrixEspUtils.releaseRightValue, -1, 0.22), cardMatrixFactor);
      float previewLimit = state.previousText == null ? 1.0F : (float)(nanoTime - state.lastNanoTime) / 1000000.0F / 160.0F;
      if (previewLimit >= 1.0F) {
         state.previousText = null;
         MatrixStackUtilsRelease.handle(state.displayedText, matrixStackValue, previewValue, materialFactor, 0.82F, previewValue6);
      } else {
         float min7 = AmountPreviewUtils.getMin(previewLimit / 0.55F);
         float panelOverlayLimit = AmountPreviewUtils.getAmount6((previewLimit - 0.25F) / 0.75F);
         if (min7 < 1.0F) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - min7);

            try {
               MatrixStackUtilsRelease.handle(state.previousText, matrixStackValue, previewValue - 3.0F * min7, materialFactor, 0.82F, previewValue6);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }

         if (panelOverlayLimit > 0.0F) {
            float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixStackUtilsRelease.handle(state.displayedText, matrixStackValue, previewValue + 3.0F * (1.0F - panelOverlayLimit), materialFactor, 0.82F, previewValue6);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
            }
         }
      }
   }

   boolean isText() {
      return this.revealedItemCount == 0 && this.consumerActionText == null;
   }

   public PreviewText getClassForStringFlag(String classText, String classText2, boolean classFlag, Runnable runnable) {
      return this.getTextFlag(2, classText, classText2, null, classFlag, null, 0, runnable, null);
   }

   public PreviewText getText(String previewText) {
      this.consumerActionText = previewText != null && !previewText.isEmpty() ? previewText : null;
      return this;
   }

   private float getAmountText(PreviewText.Item item, PreviewText.State state, float previewValue) {
      float amount = switch (item.displayMode) {
         case 3 -> getAmount(MatrixStackUtilsRelease.getAmount(item.labelText, 0.82F)) + 3.5F + getAmount(MatrixStackUtilsRelease.getAmount(item.valueText, 0.82F)) + 14.0F;
         case 4 -> item.consumerActionTable.length * 11.0F + (item.consumerActionTable.length - 1) * 6.0F + 5.0F;
         case 5, 6 -> getAmount(MatrixStackUtilsRelease.getAmount(item.labelText, 0.82F)) + 18.0F;
         default -> getAmount(MatrixStackUtilsRelease.getAmount(item.labelText, 0.82F)) + 14.0F;
      };
      if (state.animatedWidth < 0.0F) {
         state.animatedWidth = amount;
      } else {
         state.animatedWidth = AmountPreviewUtils.getAmount(state.animatedWidth, amount, 18.0F, previewValue, 0.05F);
      }

      return state.animatedWidth;
   }

   private static final class Item {
      String stateKey;
      boolean toggleActive;
      int displayMode;
      float itemX;
      String valueText;
      int selectedIndex;
      boolean isVisible;
      boolean actionEnabled;
      int[] consumerActionTable;
      String labelText;
      Runnable action;
      IntConsumer intConsumer;
      float amountText;
   }

   private static final class State {
      float smoothedOffset;
      float[] table;
      float highlightProgress;
      String displayedText;
      float animatedWidth = -1.0F;
      long lastNanoTime;
      String previousText;
      float hoverProgress;

      private State() {
         this.smoothedOffset = Float.NaN;
         this.table = new float[0];
      }
   }
}
