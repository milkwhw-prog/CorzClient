package com.corz.client.gui;

import com.corz.client.render.RenderMatrixUtilsRelease;
import net.minecraft.client.gui.DrawContext;

public final class RenderUtils {

   public static void render(DrawContext context, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
      render(context, n, n2, n3, n4, n5, 1.0F, 18.0F, 0.05F, 2303536);
      RenderMatrixUtilsRelease.render(context, n, n2, n3, n4, n5, n6, n7);
   }

   public static void render(DrawContext context, int n, int n2, int n3, int n4, int n5, float f, float f2, float f3, int n6) {
      if (!(f3 <= 0.002F)) {
         int n7 = (int)(getAmount(f3) * 255.0F);
         MatrixPeekUtilsRender.renderMatrixStack(context, n, Math.round(n2 + f), n3, n4, n5, n7 << 24 | n6 & 16777215, f2, 1.0F);
      }
   }

   private static float getAmount(float limit) {
      return limit < 0.0F ? 0.0F : (limit > 1.0F ? 1.0F : limit);
   }

   public static void render(DrawContext context, int n, int n2, int n3, int n4, float f) {
      render(context, n + n4, n2 + 1.0F, n3 - 2.0F * n4, getValueForFloat(f));
   }

   public static void render(DrawContext context, int n, int n2, int n3, int n4, int n5, float f, float f2, float f3) {
      render(context, n, n2, n3, n4, n5, f, f2, f3, 2632499);
   }

   public static void render(DrawContext context, int n, int n2, int n3, int n4, int n5, float f) {
      render(context, n + n5, n2 + n4 - 2.0F, n3 - 2.0F * n5, getValueForFloat(f));
   }

   public static void renderForDrawContextInt(DrawContext context, int n, int n2, int n3, int n4, float f) {
      render(context, n + n4, n2 + 1.0F, n3 - 2.0F * n4, getValue(f));
   }

   public static void renderForDrawContextIntFloat(DrawContext context, int n, int n2, int n3, int n4, int n5, float f, float f2, float f3) {
      if (!(f3 <= 0.002F)) {
         MatrixPeekUtilsRender.renderMatrixStack(context, n, Math.round(n2 + f), n3, n4, n5, -16777216, f2, f3);
      }
   }

   private RenderUtils() {
   }

   public static void render2(DrawContext context, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
      MatrixPeekUtilsRender.render(context, n, n2, n3, n4, n5, n6, n7);
      render(context, n + n5, n2 + MatrixPeekUtilsRender.getAmount(), n3 - 2.0F * n5, getValueForFloat(0.08F));
   }

   private static void render(DrawContext context, float f, float f2, float f3, int n) {
      if (!(f3 <= 0.0F)) {
         MatrixPeekUtilsRender.renderMatrixPeek(context, f, f2, f3, 1.0F, n);
      }
   }

   public static void render(DrawContext context, int n, int n2, int n3, int n4, int n5, int n6, int n7, float f, float limit) {
      f = getAmount(f);
      limit = getAmount(limit);
      float f2 = (0.06F + 0.04F * f) * (1.0F - 0.85F * limit);
      render(context, n, n2, n3, n4, n5, 0.7F + 0.4F * f, 4.0F + f * 2.0F, f2);
      MatrixPeekUtilsRender.render(context, n, n2, n3, n4, n5, n6, n7);
      if (limit > 0.02F) {
         float f3 = MatrixPeekUtilsRender.getAmount();
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, n + f3, n2 + f3, n3 - f3 * 2.0F, n4 - f3 * 2.0F, Math.max(0.0F, n5 - f3), getValueForFloat(0.12F * limit)
         );
      }
   }

   private static int getValue(float f) {
      return (int)(getAmount(f) * 255.0F) << 24 | 16777215;
   }

   private static int getValueForFloat(float f) {
      return (int)(getAmount(f) * 255.0F) << 24;
   }
}
