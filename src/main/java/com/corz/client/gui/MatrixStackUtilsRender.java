package com.corz.client.gui;

import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexConsumerUtilsAmounts;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public final class MatrixStackUtilsRender {

   private MatrixStackUtilsRender() {
   }

   public static void renderTranslatePop(DrawContext context, String translatePopText, float x, float y, float translatePopFactor, float translatePopValue, boolean translatePopFlag, float x2) {
      if (!(translatePopValue <= 0.01F) && translatePopText != null && !translatePopText.isEmpty()) {
         float translatePopValue2 = MatrixStackUtilsAmount.getAmount(translatePopText, translatePopFactor);
         float translatePopValue3 = MatrixStackUtilsAmount.getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown() * translatePopFactor;
         float translatePopValue4 = 5.5F;
         float translatePopValue5 = 3.2F;
         float translatePopValue6 = 5.0F;
         float materialFactor = translatePopValue2 + translatePopValue4 * 2.0F;
         float translatePopValue8 = translatePopValue3 + translatePopValue5 * 2.0F;
         float matrixPeekValue = translatePopFlag ? x - 7.0F : x - materialFactor + 7.0F;
         float matrixStackValue = y - translatePopValue8 - translatePopValue6;
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(x, y, 0.0F);
         matrixStack.scale(x2, x2, 1.0F);
         matrixStack.translate(-x, -y, 0.0F);
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(translatePopValue);

         try {
            VertexConsumerUtilsAmounts.updatePositionMatrix();
            int partNeckValue = CorzRoundedUtils.getPanelOverlay(-150996754);
            int panelOverlay3 = CorzRoundedUtils.getPanelOverlay(859843098);
            float translatePopValue11 = translatePopFlag ? matrixPeekValue + 7.0F : matrixPeekValue + materialFactor - 13.0F;
            VertexConsumerUtilsAmounts.handleVertexColor(
               translatePopValue11 - 0.6F, matrixStackValue + translatePopValue8 - 0.8F, panelOverlay3, translatePopValue11 + 6.6F, matrixStackValue + translatePopValue8 - 0.8F, panelOverlay3, x, y + 0.6F, panelOverlay3
            );
            VertexConsumerUtilsAmounts.updateConsumerVertex();
            MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, matrixStackValue, materialFactor, translatePopValue8, Math.min(translatePopValue8 / 2.0F, 6.0F), -150996754, 859843098);
            VertexConsumerUtilsAmounts.updatePositionMatrix();
            VertexConsumerUtilsAmounts.handleVertexColor(
               translatePopValue11 + 0.4F, matrixStackValue + translatePopValue8 - 0.45F, partNeckValue, translatePopValue11 + 5.6F, matrixStackValue + translatePopValue8 - 0.45F, partNeckValue, x, y - 0.6F, partNeckValue
            );
            VertexConsumerUtilsAmounts.updateConsumerVertex();
            MatrixStackUtilsAmount.handlePushScale(translatePopText, matrixPeekValue + translatePopValue4, MatrixStackUtilsAmount.getAmount(matrixStackValue, translatePopValue8, translatePopFactor), translatePopFactor, -12966890);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            matrixStack.pop();
         }
      }
   }
}
