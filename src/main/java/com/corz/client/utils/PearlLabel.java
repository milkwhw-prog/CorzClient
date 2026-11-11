package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.VertexTextureFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import com.corz.client.utils.GhostPlayerGeometry;
import com.corz.client.utils.PearlMath;

public final class PearlLabel {
   private static final Color OFF_WHITE = new Color(-920587, true);
   private static final Color GRAY = new Color(-7630437, true);
   private static final String[] pearlLabelTexts = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
   private static final Int2ObjectOpenHashMap<Color> INT2_OBJECT_OPEN_HASH_MAP = new Int2ObjectOpenHashMap();

   private static void handlePushScale(VertexTextureFont vertexTextureFont, String scaleText, float textX, float textY, float scale, Color color) {
      if (!scaleText.isEmpty()) {
         if (Math.abs(scale - 1.0F) < 5.0E-4F) {
            vertexTextureFont.handleForStringFloat(scaleText, textX, textY, color);
         } else {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            matrixStack.push();
            matrixStack.scale(scale, scale, 1.0F);
            vertexTextureFont.handleForStringFloat(scaleText, textX / scale, textY / scale, color);
            matrixStack.pop();
         }
      }
   }

   private PearlLabel() {
   }

   private static float getAmount(VertexTextureFont vertexTextureFont, String text, float scale) {
      float digitWidth = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont("0") * scale;
      float total = 0.0F;

      for (int i = 0; i < text.length(); i++) {
         char charAt = text.charAt(i);
         total += charAt >= '0' && charAt <= '9' ? digitWidth : vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(charAt == '.' ? "." : String.valueOf(charAt)) * scale;
      }

      return total;
   }

   public static void renderColorRenderer(DrawContext context, float x, float y, float scale, String label, String valueText, float progress, int color) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      float textScale = 0.9F * scale;
      float sScale = 0.8F * scale;
      float boxHeight = getAmount(16.0F * scale);
      float iconPad = 6.0F * scale;
      float iconSize = 8.0F * scale;
      boolean hasLabel = label != null && !label.isEmpty();
      float labelWidth = hasLabel ? fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(label) * textScale + 6.0F * scale : 0.0F;
      float sWidth = getAmount(fontRenderer, valueText, textScale);
      float boxWidth = getRendererFont(scale, label, valueText);
      float boxX = getAmount(x - boxWidth / 2.0F);
      float boxY = getAmount(y + 5.0F * scale);
      int bgColor = (CardMatrixUtils.isModernFlag() ? 102 : 184) << 24 | 1053465;
      MatrixPeekUtilsRender.render(context, boxX, boxY, boxWidth, boxHeight, Math.min(6.0F * scale, boxHeight / 2.0F), bgColor, 788529151);
      float iconX = boxX + iconPad;
      handle(iconX + iconSize / 2.0F, boxY + boxHeight / 2.0F, iconSize, progress, color);
      float textX = iconX + (iconSize + 5.0F * scale);
      float textY = getAmount(fontRenderer, boxY, boxHeight, textScale);
      if (hasLabel) {
         handlePushScale(fontRenderer, label, textX, textY, textScale, OFF_WHITE);
         textX += labelWidth;
      }

      handleTexts(fontRenderer, valueText, textX, textY, textScale, getHashOpen(color));
      float sX = textX + (sWidth + 2.0F * scale);
      handlePushScale(fontRenderer, "s", sX, getAmount(fontRenderer, boxY, boxHeight, sScale), sScale, GRAY);
   }

   private static float getAmount(VertexTextureFont vertexTextureFont, float y, float height, float scale) {
      float jitterOffset = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
      return getAmount(y + height / 2.0F + jitterOffset * scale);
   }

   public static float getRendererFont(float scale, String label, String valueText) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      float textScale = 0.9F * scale;
      float pad = 6.0F * scale;
      float labelWidth = label != null && !label.isEmpty() ? fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(label) * textScale + 6.0F * scale : 0.0F;
      return (float)Math.ceil(
         pad
            + 8.0F * scale
            + 5.0F * scale
            + labelWidth
            + getAmount(fontRenderer, valueText, textScale)
            + 2.0F * scale
            + fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("s") * 0.8F * scale
            + pad
      );
   }

   private static Color getHashOpen(int hashOpenValue) {
      Color color = (Color)INT2_OBJECT_OPEN_HASH_MAP.get(hashOpenValue);
      if (color == null) {
         if (INT2_OBJECT_OPEN_HASH_MAP.size() > 64) {
            INT2_OBJECT_OPEN_HASH_MAP.clear();
         }

         color = new Color(hashOpenValue, true);
         INT2_OBJECT_OPEN_HASH_MAP.put(hashOpenValue, color);
      }

      return color;
   }

   private static void handle(float centerX, float centerY, float diameter, float progress, int color) {
      float pixelSize = MatrixPeekUtilsRender.getAmount();
      float strokeWidth = Math.max(2.0F, (float)Math.round(1.5F * diameter / 8.0F / pixelSize)) * pixelSize;
      float radius = diameter / 2.0F - strokeWidth / 2.0F;
      float snappedX = getAmount(centerX);
      float snappedY = getAmount(centerY);
      AmountHandle amountHandle = new AmountHandle(snappedX, snappedY, 654311423, false, false, true, false);
      amountHandle.handle(radius, strokeWidth);
      amountHandle.updateAmountPanel();
      float progressAmount = GhostPlayerGeometry.getAmount(progress);
      if (!(progressAmount <= 0.003F)) {
         AmountHandle amountHandle2 = new AmountHandle(snappedX, snappedY, color, false, false, true, false);
         if (progressAmount >= 0.997F) {
            amountHandle2.handle(radius, strokeWidth);
         } else {
            amountHandle2.handleAmountForFloat(radius, strokeWidth, PearlMath.getAmount(progressAmount), PearlMath.getAmountForFloat(progressAmount));
         }

         amountHandle2.updateAmountPanel();
      }
   }

   private static float getAmount(float value) {
      float pixelSize = MatrixPeekUtilsRender.getAmount();
      return Math.round(value / pixelSize) * pixelSize;
   }

   private static void handleTexts(VertexTextureFont vertexTextureFont, String text, float startX, float y, float scale, Color color) {
      float digitWidth = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont("0") * scale;

      for (int i = 0; i < text.length(); i++) {
         char charAt = text.charAt(i);
         if (charAt >= '0' && charAt <= '9') {
            String digitStr = pearlLabelTexts[charAt - '0'];
            handlePushScale(vertexTextureFont, digitStr, startX + (digitWidth - vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(digitStr) * scale) / 2.0F, y, scale, color);
            startX += digitWidth;
         } else {
            String charStr = charAt == '.' ? "." : String.valueOf(charAt);
            handlePushScale(vertexTextureFont, charStr, startX, y, scale, color);
            startX += vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(charStr) * scale;
         }
      }
   }
}
