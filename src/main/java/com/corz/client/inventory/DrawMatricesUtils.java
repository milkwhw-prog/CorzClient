package com.corz.client.inventory;

import com.corz.client.render.CorzRoundedUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public final class DrawMatricesUtils {

   public static void renderMatricesMatrix(DrawContext context, ItemStack itemStack, int matricesMatrixValue, int floorCtx, int matricesMatrixValue3) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay();
      if (panelOverlay >= 0.999F) {
         context.drawItem(itemStack, matricesMatrixValue, floorCtx);
      } else if (!(panelOverlay < 0.55F)) {
         float matricesMatrixValue4 = (panelOverlay - 0.55F) / 0.45F;
         float matricesMatrixValue5 = 0.8F + 0.19999999F * matricesMatrixValue4;
         float matricesMatrixValue6 = matricesMatrixValue + matricesMatrixValue3 / 2.0F;
         float matricesMatrixValue7 = floorCtx + matricesMatrixValue3 / 2.0F;
         context.getMatrices().pushMatrix();

         try {
            context.getMatrices().translate(matricesMatrixValue6, matricesMatrixValue7);
            context.getMatrices().scale(matricesMatrixValue5, matricesMatrixValue5);
            context.getMatrices().translate(-matricesMatrixValue6, -matricesMatrixValue7);
            context.drawItem(itemStack, matricesMatrixValue, floorCtx);
         } finally {
            context.getMatrices().popMatrix();
         }
      }
   }

   public static void renderFill(DrawContext context, int x12, int y12, int x22, int y22, int matricesValue) {
      int panelOverlay = CorzRoundedUtils.getPanelOverlay(matricesValue);
      if (panelOverlay >>> 24 != 0) {
         context.fill(x12, y12, x22, y22, panelOverlay);
      }
   }

   public static void renderGradientFill(DrawContext context, int x12, int y12, int x22, int y22, int gradientFillValue, int panelOverlayValue) {
      int panelOverlay = CorzRoundedUtils.getPanelOverlay(gradientFillValue);
      int panelOverlay2 = CorzRoundedUtils.getPanelOverlay(panelOverlayValue);
      if (panelOverlay >>> 24 != 0 || panelOverlay2 >>> 24 != 0) {
         context.fillGradient(x12, y12, x22, y22, panelOverlay, panelOverlay2);
      }
   }

   public static void render(DrawContext context, ItemStack itemStack, int matricesValue, int floorCtx) {
      renderMatricesMatrix(context, itemStack, matricesValue, floorCtx, 16);
   }

   private DrawMatricesUtils() {
   }
}
