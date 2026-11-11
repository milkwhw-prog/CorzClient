package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.render.CorzfontAbcdefghijklmnopqrstuvwxyzTexture;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;

public final class HashPopUtils {
   private static float cachedEightWidth = -1.0F;
   private static float cachedMaxDigitWidth = -1.0F;
   private static final float[] DIGIT_WIDTH_TABLE = new float[10];
   private static final Int2ObjectOpenHashMap<Color> COLOR_CACHE = new Int2ObjectOpenHashMap();
   private static final String[] ASCII_CHAR_STRINGS = new String[128];

   public static float drawTextRightAligned(String hashPopText, float hashPopValue, float priceValue, float hashPopFactor, int hashOpenValue) {
      float measureTextWidth5 = measureTextWidth(hashPopText, hashPopFactor);
      drawTabularText(hashPopText, MatrixStackUtilsAmount.getAmount(hashPopValue - measureTextWidth5), priceValue, hashPopFactor, hashOpenValue, hashOpenValue);
      return measureTextWidth5;
   }

   public static float drawTextSingleColor(String hashPopText, float hashPopValue, float priceValue, float x2, int hashOpenValue) {
      return drawTabularText(hashPopText, hashPopValue, priceValue, x2, hashOpenValue, hashOpenValue);
   }

   static {
      for (int i = 32; i < 128; i++) {
         ASCII_CHAR_STRINGS[i] = String.valueOf((char)i);
      }
   }

   private static float getMaxDigitWidth(VertexTextureFont vertexTextureFont) {
      float amount2 = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont("8");
      if (amount2 != cachedEightWidth || cachedMaxDigitWidth <= 0.0F) {
         cachedEightWidth = amount2;
         float pilotStatusBound = 0.0F;

         for (int i = 0; i < 10; i++) {
            DIGIT_WIDTH_TABLE[i] = vertexTextureFont.getAbcdefghijklmnopqrstuvwxyzCorzfont(ASCII_CHAR_STRINGS[48 + i]);
            if (i >= 2) {
               pilotStatusBound = Math.max(pilotStatusBound, DIGIT_WIDTH_TABLE[i]);
            }
         }

         cachedMaxDigitWidth = pilotStatusBound;
      }

      return cachedMaxDigitWidth;
   }

   private static boolean isAsciiDigit(char hashPopLimit) {
      return hashPopLimit >= '0' && hashPopLimit <= '9';
   }

   private static Color getCachedColor(int hashOpenValue) {
      Color color = (Color)COLOR_CACHE.get(hashOpenValue);
      if (color == null) {
         if (COLOR_CACHE.size() > 96) {
            COLOR_CACHE.clear();
         }

         color = new Color(hashOpenValue, true);
         COLOR_CACHE.put(hashOpenValue, color);
      }

      return color;
   }

   private HashPopUtils() {
   }

   public static float measureTextWidth(String hashPopText, float hashPopFactor) {
      if (hashPopText != null && !hashPopText.isEmpty()) {
         VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
         float maxAmounts = getMaxDigitWidth(fontRenderer);
         float hashPopTotal = 0.0F;
         int hashPopLimit = hashPopText.length();

         for (int i = 0; i < hashPopLimit; i++) {
            char charAt = hashPopText.charAt(i);
            hashPopTotal += isAsciiDigit(charAt) ? maxAmounts : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(getCharString(charAt));
         }

         return hashPopTotal * hashPopFactor;
      } else {
         return 0.0F;
      }
   }

   public static float drawTabularText(String hashPopText, float hashPopValue, float priceValue, float x2, int hashOpenValue, int hashPopValue4) {
      if (hashPopText != null && !hashPopText.isEmpty()) {
         VertexTextureFont fontRenderer = MatrixStackUtilsAmount.getFontRenderer();
         float maxAmounts = getMaxDigitWidth(fontRenderer);
         Color hashOpen = getCachedColor(hashOpenValue);
         Color cachedColor = getCachedColor(hashPopValue4);
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         matrixStack.push();
         CorzfontAbcdefghijklmnopqrstuvwxyzTexture corzfontAbcdefghijklmnopqrst = null;

         float popPush;
         try {
            matrixStack.scale(x2, x2, 1.0F);
            corzfontAbcdefghijklmnopqrst = fontRenderer instanceof CorzfontAbcdefghijklmnopqrstuvwxyzTexture corzfontAbcdefghijklmnopqrst2 && corzfontAbcdefghijklmnopqrst2.isFlagBuffer() ? corzfontAbcdefghijklmnopqrst2 : null;
            float hashPopTotal = hashPopValue / x2;
            float f13 = priceValue / x2;
            int hashPopLimit = hashPopText.length();

            for (int i = 0; i < hashPopLimit; i++) {
               char charAt = hashPopText.charAt(i);
               String charString = getCharString(charAt);
               if (isAsciiDigit(charAt)) {
                  fontRenderer.handleForStringFloat(charString, hashPopTotal + (maxAmounts - DIGIT_WIDTH_TABLE[charAt - '0']) / 2.0F, f13, hashOpen);
                  hashPopTotal += maxAmounts;
               } else {
                  fontRenderer.handleForStringFloat(charString, hashPopTotal, f13, charAt != '$' && charAt != '+' && charAt != '-' ? hashOpen : cachedColor);
                  hashPopTotal += fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(charString);
               }
            }

            popPush = hashPopTotal * x2 - hashPopValue;
         } finally {
            try {
               if (corzfontAbcdefghijklmnopqrst != null) {
                  corzfontAbcdefghijklmnopqrst.updateBufferBuilder();
               }
            } finally {
               matrixStack.pop();
            }
         }

         return popPush;
      } else {
         return 0.0F;
      }
   }

   private static String getCharString(char index) {
      return index < 128 && ASCII_CHAR_STRINGS[index] != null ? ASCII_CHAR_STRINGS[index] : String.valueOf(index);
   }
}
