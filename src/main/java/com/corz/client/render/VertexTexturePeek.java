package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.MsdfFontData;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public final class VertexTexturePeek {
   private boolean textureReadActive;
   private float fontScale;
   private final String descriptor;
   private boolean parsedCorzfontActive;
   private final Identifier corzclient;
   private VertexTexturePeek.Glyph[] corzclientClass137sParsedCor;
   private  boolean corzclientFlag;
   private static final Map<String, VertexTexturePeek> INSTANCES = new HashMap<>();
   private final Map<Integer, VertexTexturePeek.Glyph> parsedCorzfontGlyphMap = new HashMap<>();
   private float parsedCorzfontAmount;
   private final Map<Long, Float> parsedCorzfontFloatMap = new HashMap<>();
   private int hashShift;
   private long[] corzclientTimes;
   private NativeImage readParsedCorzfont;
   private int parsedCorzfontValue;
   private float[] corzclientAmounts;

   public static VertexTexturePeek getIfAbsent(String ifAbsentText) {
      return INSTANCES.computeIfAbsent(ifAbsentText, VertexTexturePeek::new);
   }

   public static VertexTexturePeek getWave() {
      return getIfAbsent("wave");
   }

   private void updateParsedCorzfont()  throws java.io.IOException {
      String parsedCorzfontText = "assets/corzclient/fonts/hud/" + this.descriptor + "/";

      MsdfFontData fromJson4;
      try (InputStream asStream = getAsStream(parsedCorzfontText + "font.json")) {
         if (asStream == null) {
            throw new IllegalStateException("missing " + parsedCorzfontText + "font.json");
         }

         fromJson4 = (MsdfFontData)new Gson().fromJson(new InputStreamReader(asStream, StandardCharsets.UTF_8), MsdfFontData.class);
      }

      if (fromJson4 != null && fromJson4.class172 != null && fromJson4.class245 != null && fromJson4.parsedCorzfontItems != null) {
         this.fontScale = fromJson4.class245.parsedCorzfontAmount;
         this.parsedCorzfontAmount = fromJson4.class245.parsedCorzfontAmount2;
         float parsedCorzfontAmount = fromJson4.class172.parsedCorzfontAmount;
         float parsedCorzfontAmount2 = fromJson4.class172.parsedCorzfontAmount2;

         for (MsdfFontData.Glyph glyph : fromJson4.parsedCorzfontItems) {
            this.parsedCorzfontGlyphMap.put(glyph.value, new VertexTexturePeek.Glyph(glyph, parsedCorzfontAmount, parsedCorzfontAmount2));
         }

         if (fromJson4.items != null) {
            for (MsdfFontData.Kerning kerning : fromJson4.items) {
               this.parsedCorzfontFloatMap.put(getTime(kerning.value, kerning.value2), kerning.amount);
            }
         }

         try (InputStream asStream2 = getAsStream(parsedCorzfontText + "font.png")) {
            if (asStream2 == null) {
               throw new IllegalStateException("missing " + parsedCorzfontText + "font.png");
            }

            this.readParsedCorzfont = NativeImage.read(asStream2);
         }

         int parsedCorzfontValue = -1;

         for (int parsedCorzfontLimit : this.parsedCorzfontGlyphMap.keySet()) {
            if (parsedCorzfontLimit >= 0 && parsedCorzfontLimit <= 65535 && parsedCorzfontLimit > parsedCorzfontValue) {
               parsedCorzfontValue = parsedCorzfontLimit;
            }
         }

         VertexTexturePeek.Glyph[] corzclientClass137s = new VertexTexturePeek.Glyph[parsedCorzfontValue + 1];

         for (Entry entry : this.parsedCorzfontGlyphMap.entrySet()) {
            int index = (Integer)entry.getKey();
            if (index >= 0 && index <= 65535) {
               corzclientClass137s[index] = (VertexTexturePeek.Glyph)entry.getValue();
            }
         }

         this.corzclientClass137sParsedCor = corzclientClass137s;
         int value = 0;

         for (long keyItem : this.parsedCorzfontFloatMap.keySet()) {
            if (keyItem >>> 32 <= 65535L && (keyItem & 4294967295L) <= 65535L) {
               value++;
            }
         }

         int parsedCorzfontSize = 2;

         while (parsedCorzfontSize < value * 2) {
            parsedCorzfontSize <<= 1;
         }

         long[] corzclientTimes = new long[parsedCorzfontSize];
         float[] corzclientAmounts = new float[parsedCorzfontSize];
         Arrays.fill(corzclientTimes, Long.MIN_VALUE);
         int value2 = 64 - Integer.numberOfTrailingZeros(parsedCorzfontSize);

         for (Entry entryItem : this.parsedCorzfontFloatMap.entrySet()) {
            long parsedCorzfontValue3 = (Long)entryItem.getKey();
            if (parsedCorzfontValue3 >>> 32 <= 65535L && (parsedCorzfontValue3 & 4294967295L) <= 65535L) {
               int index2 = (int)(parsedCorzfontValue3 * -7046029254386353131L >>> value2);

               while (corzclientTimes[index2] != Long.MIN_VALUE) {
                  index2 = index2 + 1 & parsedCorzfontSize - 1;
               }

               corzclientTimes[index2] = parsedCorzfontValue3;
               corzclientAmounts[index2] = (Float)entryItem.getValue();
            }
         }

         this.corzclientTimes = corzclientTimes;
         this.corzclientAmounts = corzclientAmounts;
         this.hashShift = value2;
         this.parsedCorzfontValue = value;
         this.parsedCorzfontActive = true;
      } else {
         throw new IllegalStateException("empty descriptor: " + this.descriptor);
      }
   }

   public static VertexTexturePeek getSuisse() {
      return getIfAbsent("suisse");
   }

   public float getCorzclientAmounts(String corzclientText, float corzclientValue) {
      if (this.parsedCorzfontActive && corzclientText != null && !corzclientText.isEmpty()) {
         float corzclientFactor = getAmount(corzclientValue);
         float corzclientAmounts = 0.0F;
         int vertexTextureValue = -1;

         for (int i = 0; i < corzclientText.length(); i++) {
            char charAt = corzclientText.charAt(i);
            VertexTexturePeek.Glyph corzclient = this.getCorzclient(charAt);
            if (corzclient == null) {
               vertexTextureValue = charAt;
            } else {
               if (vertexTextureValue != -1) {
                  int corzclientTimes = this.getCorzclientTimes(getTime(vertexTextureValue, charAt));
                  if (corzclientTimes >= 0) {
                     corzclientAmounts += this.corzclientAmounts[corzclientTimes] * corzclientFactor;
                  }
               }

               corzclientAmounts += corzclient.corzclientAmount * corzclientFactor;
               vertexTextureValue = charAt;
            }
         }

         return corzclientAmounts;
      } else {
         return 0.0F;
      }
   }

   public void handleAmountPosition(String positionText, float positionValue, float positionValue2, int vertexTextureValue, int positionValue4, float corzclientValue) {
      if (this.isCorzclientFlag() && positionText != null && !positionText.isEmpty()) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            float corzclientAmounts = this.getCorzclientAmounts(positionText, corzclientValue);
            if (corzclientAmounts <= 0.0F) {
               this.handlePeekEnd(positionText, positionValue, positionValue2, vertexTextureValue, corzclientValue);
            } else {
               MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
               Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
               float positionFactor = getAmount(corzclientValue);
               float positionValue6 = positionValue2 + this.parsedCorzfontAmount * positionFactor;
               float positionTotal = positionValue;
               int charAt7 = -1;
               BufferBuilder bufferBuilder = null;

               for (int i = 0; i < positionText.length(); i++) {
                  char charAt = positionText.charAt(i);
                  VertexTexturePeek.Glyph corzclient = this.getCorzclient(charAt);
                  if (corzclient == null) {
                     charAt7 = charAt;
                  } else {
                     if (charAt7 != -1) {
                        int corzclientTimes = this.getCorzclientTimes(getTime(charAt7, charAt));
                        if (corzclientTimes >= 0) {
                           positionTotal += this.corzclientAmounts[corzclientTimes] * positionFactor;
                        }
                     }

                     if (corzclient.isAmount()) {
                        if (bufferBuilder == null) {
                           bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                        }

                        float positionValue8 = positionValue6 - corzclient.glyphOffsetY * positionFactor;
                        float positionValue9 = corzclient.glyphAmount * positionFactor;
                        float positionValue10 = corzclient.positionAmount * positionFactor;
                        int positionValue11 = getValue(vertexTextureValue, positionValue4, (positionTotal - positionValue) / corzclientAmounts);
                        int positionValue12 = getValue(vertexTextureValue, positionValue4, (positionTotal + positionValue9 - positionValue) / corzclientAmounts);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal, positionValue8 + positionValue10, corzclient.uvLeft, corzclient.textureAmount, positionValue11);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal + positionValue9, positionValue8 + positionValue10, corzclient.uvRight, corzclient.textureAmount, positionValue12);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal + positionValue9, positionValue8, corzclient.uvRight, corzclient.uvTop, positionValue12);
                        handleVertexTexture(bufferBuilder, positionMatrix, positionTotal, positionValue8, corzclient.uvLeft, corzclient.uvTop, positionValue11);
                     }

                     positionTotal += corzclient.corzclientAmount * positionFactor;
                     charAt7 = charAt;
                  }
               }

               if (bufferBuilder != null) {
                  CorzRoundedUtils.handleTextureView(bufferBuilder.end(), this.corzclient);
               }
            }
         }
      }
   }

   private static void handleVertexTexture(BufferBuilder buffer, Matrix4f matrix, float vertexTextureValue, float vertexTextureValue2, float vertexTextureValue3, float vertexTextureValue4, int vertexTextureValue5) {
      float vertexTextureValue6 = (vertexTextureValue5 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
      buffer.vertex(matrix, vertexTextureValue, vertexTextureValue2, 0.0F).texture(vertexTextureValue3, vertexTextureValue4).color((vertexTextureValue5 >> 16 & 0xFF) / 255.0F, (vertexTextureValue5 >> 8 & 0xFF) / 255.0F, (vertexTextureValue5 & 0xFF) / 255.0F, vertexTextureValue6);
   }

   public float getAmountFlag(char ch, float vertexTextureValue) {
      if (!this.parsedCorzfontActive) {
         return 0.0F;
      } else {
         VertexTexturePeek.Glyph corzclient = this.getCorzclient(ch);
         if (corzclient == null) {
            return 0.0F;
         } else {
            float vertexTextureValue2 = 0.0F;
            return vertexTextureValue2 + corzclient.corzclientAmount * getAmount(vertexTextureValue);
         }
      }
   }

   public static VertexTexturePeek getRegular() {
      return getIfAbsent("sf_regular");
   }

   private BufferBuilder getAmountTexture(
      BufferBuilder amountTexture, Matrix4f matrix4f, String textureText, int textureValue, int textureLimit, float peekEndValue, float textureValue3, float textureFactor, float textureValue4, float textureValue5, float textureValue6, float textureValue7
   ) {
      float x2 = peekEndValue;
      int vertexTextureValue = -1;

      for (int i = textureValue; i < textureLimit; i++) {
         char charAt = textureText.charAt(i);
         VertexTexturePeek.Glyph corzclient = this.getCorzclient(charAt);
         if (corzclient == null) {
            vertexTextureValue = charAt;
         } else {
            if (vertexTextureValue != -1) {
               int corzclientTimes = this.getCorzclientTimes(getTime(vertexTextureValue, charAt));
               if (corzclientTimes >= 0) {
                  x2 += this.corzclientAmounts[corzclientTimes] * textureFactor;
               }
            }

            if (corzclient.isAmount()) {
               if (amountTexture == null) {
                  amountTexture = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               }

               float textureValue9 = textureValue3 - corzclient.glyphOffsetY * textureFactor;
               float textureValue10 = corzclient.glyphAmount * textureFactor;
               float textureValue11 = corzclient.positionAmount * textureFactor;
               amountTexture.vertex(matrix4f, x2, textureValue9 + textureValue11, 0.0F).texture(corzclient.uvLeft, corzclient.textureAmount).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2 + textureValue10, textureValue9 + textureValue11, 0.0F).texture(corzclient.uvRight, corzclient.textureAmount).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2 + textureValue10, textureValue9, 0.0F).texture(corzclient.uvRight, corzclient.uvTop).color(textureValue4, textureValue5, textureValue6, textureValue7);
               amountTexture.vertex(matrix4f, x2, textureValue9, 0.0F).texture(corzclient.uvLeft, corzclient.uvTop).color(textureValue4, textureValue5, textureValue6, textureValue7);
            }

            x2 += corzclient.corzclientAmount * textureFactor;
            vertexTextureValue = charAt;
         }
      }

      return amountTexture;
   }

   private int getCorzclientTimes(long corzclientValue) {
      if (this.parsedCorzfontValue == 0) {
         return -1;
      } else {
         long[] corzclientTimes = this.corzclientTimes;
         int corzclientValue2 = corzclientTimes.length - 1;
         int index = (int)(corzclientValue * -7046029254386353131L >>> this.hashShift);

         while (true) {
            long corzclientValue3 = corzclientTimes[index];
            if (corzclientValue3 == corzclientValue) {
               return index;
            }

            if (corzclientValue3 == Long.MIN_VALUE) {
               return -1;
            }

            index = index + 1 & corzclientValue2;
         }
      }
   }

   private VertexTexturePeek.Glyph getCorzclient(int index) {
      return index < this.corzclientClass137sParsedCor.length ? this.corzclientClass137sParsedCor[index] : null;
   }

   public static VertexTexturePeek getIcon() {
      return getIfAbsent("icon");
   }

   public void handlePeekEnd(String peekEndText, float[] peekEndValues, int peekEndLimit, float peekEndValue, int peekEndValue2, float vertexTextureValue) {
      if (this.isCorzclientFlag() && peekEndText != null && peekEndLimit > 0) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float peekEndFactor = getAmount(vertexTextureValue);
            float peekEndValue4 = (peekEndValue2 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
            float peekEndValue5 = (peekEndValue2 >> 16 & 0xFF) / 255.0F;
            float peekEndValue6 = (peekEndValue2 >> 8 & 0xFF) / 255.0F;
            float peekEndValue7 = (peekEndValue2 & 0xFF) / 255.0F;
            float peekEndValue8 = peekEndValue + this.parsedCorzfontAmount * peekEndFactor;
            BufferBuilder bufferBuilder = null;

            for (int i = 0; i < peekEndLimit; i++) {
               bufferBuilder = this.getAmountTexture(bufferBuilder, positionMatrix, peekEndText, i, i + 1, peekEndValues[i], peekEndValue8, peekEndFactor, peekEndValue5, peekEndValue6, peekEndValue7, peekEndValue4);
            }

            if (bufferBuilder != null) {
               CorzRoundedUtils.handleTextureView(bufferBuilder.end(), this.corzclient);
            }
         }
      }
   }

   private static float getAmount(float vertexTextureValue) {
      return vertexTextureValue * 0.5F;
   }

   private static long getTime(int vertexTextureValue, int charAt) {
      return (long)vertexTextureValue << 32 | charAt & 4294967295L;
   }

   private VertexTexturePeek(String descriptor) {
      this.corzclientClass137sParsedCor = new VertexTexturePeek.Glyph[0];
      this.corzclientTimes = new long[0];
      this.corzclientAmounts = new float[0];
      this.descriptor = descriptor;
      this.corzclient = Identifier.of("corzclient", "font/hud_" + descriptor);

      try {
         this.updateParsedCorzfont();
      } catch (Throwable e) {
         this.corzclientFlag = true;
         e.printStackTrace();
      }
   }

   private static int getValue(int vertexTextureValue, int vertexTextureValue2, float vertexTextureFactor) {
      vertexTextureFactor = vertexTextureFactor < 0.0F ? 0.0F : (vertexTextureFactor > 1.0F ? 1.0F : vertexTextureFactor);
      int vertexTextureValue3 = vertexTextureValue >>> 24 & 0xFF;
      int vertexTextureValue4 = vertexTextureValue >> 16 & 0xFF;
      int vertexTextureValue5 = vertexTextureValue >> 8 & 0xFF;
      int vertexTextureValue6 = vertexTextureValue & 0xFF;
      int vertexTextureValue7 = vertexTextureValue2 >>> 24 & 0xFF;
      int vertexTextureValue8 = vertexTextureValue2 >> 16 & 0xFF;
      int vertexTextureValue9 = vertexTextureValue2 >> 8 & 0xFF;
      int vertexTextureValue10 = vertexTextureValue2 & 0xFF;
      return (int)(vertexTextureValue3 + (vertexTextureValue7 - vertexTextureValue3) * vertexTextureFactor) << 24
         | (int)(vertexTextureValue4 + (vertexTextureValue8 - vertexTextureValue4) * vertexTextureFactor) << 16
         | (int)(vertexTextureValue5 + (vertexTextureValue9 - vertexTextureValue5) * vertexTextureFactor) << 8
         | (int)(vertexTextureValue6 + (vertexTextureValue10 - vertexTextureValue6) * vertexTextureFactor);
   }

   private void updateTextureRead() {
      if (!this.textureReadActive && this.readParsedCorzfont != null) {
         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "hud_font_" + this.descriptor, this.readParsedCorzfont);
         MinecraftClient.getInstance().getTextureManager().registerTexture(this.corzclient, nativeImageBackedTexture);
         this.textureReadActive = true;
      }
   }

   public void handlePeekEnd(String peekEndText, float peekEndValue, float peekEndValue2, int peekEndValue3, float vertexTextureValue) {
      if (this.isCorzclientFlag() && peekEndText != null && !peekEndText.isEmpty()) {
         this.updateTextureRead();
         if (this.textureReadActive) {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float peekEndFactor = getAmount(vertexTextureValue);
            float peekEndValue5 = (peekEndValue3 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
            float peekEndValue6 = (peekEndValue3 >> 16 & 0xFF) / 255.0F;
            float peekEndValue7 = (peekEndValue3 >> 8 & 0xFF) / 255.0F;
            float peekEndValue8 = (peekEndValue3 & 0xFF) / 255.0F;
            float peekEndValue9 = peekEndValue2 + this.parsedCorzfontAmount * peekEndFactor;
            BufferBuilder amountTexture = this.getAmountTexture(null, positionMatrix, peekEndText, 0, peekEndText.length(), peekEndValue, peekEndValue9, peekEndFactor, peekEndValue6, peekEndValue7, peekEndValue8, peekEndValue5);
            if (amountTexture != null) {
               CorzRoundedUtils.handleTextureView(amountTexture.end(), this.corzclient);
            }
         }
      }
   }

   public void handlePeekEnd(String[] strings, float[] peekEndValues, float[] peekEndValues2, int[] peekEndValues3, int peekEndLimit, float peekEndValue) {
      if (this.isCorzclientFlag()) {
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
         float peekEndFactor = getAmount(peekEndValue);
         BufferBuilder bufferBuilder = null;

         for (int i = 0; i < peekEndLimit; i++) {
            String peekEndText = strings[i];
            if (peekEndText != null && !peekEndText.isEmpty()) {
               this.updateTextureRead();
               if (!this.textureReadActive) {
                  break;
               }

               int peekEndValue2 = peekEndValues3[i];
               float peekEndValue3 = (peekEndValue2 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
               float peekEndValue4 = (peekEndValue2 >> 16 & 0xFF) / 255.0F;
               float peekEndValue5 = (peekEndValue2 >> 8 & 0xFF) / 255.0F;
               float peekEndValue6 = (peekEndValue2 & 0xFF) / 255.0F;
               float peekEndValue7 = peekEndValues2[i] + this.parsedCorzfontAmount * peekEndFactor;
               bufferBuilder = this.getAmountTexture(bufferBuilder, positionMatrix, peekEndText, 0, peekEndText.length(), peekEndValues[i], peekEndValue7, peekEndFactor, peekEndValue4, peekEndValue5, peekEndValue6, peekEndValue3);
            }
         }

         if (bufferBuilder != null) {
            CorzRoundedUtils.handleTextureView(bufferBuilder.end(), this.corzclient);
         }
      }
   }

   public boolean isCorzclientFlag() {
      return this.parsedCorzfontActive && !this.corzclientFlag;
   }

   public float getAmountForFloat(float vertexTextureValue) {
      return this.fontScale * getAmount(vertexTextureValue);
   }

   private static InputStream getAsStream(String streamText) {
      return CorzClient.class.getClassLoader().getResourceAsStream(streamText);
   }

   public static VertexTexturePeek getIconsminced() {
      return getIfAbsent("iconsminced");
   }

   public float getAmountParsedCorzfontAmount(float vertexTextureValue) {
      return this.parsedCorzfontAmount * getAmount(vertexTextureValue);
   }

   private static final class Glyph {
      final float uvRight;
      final float positionAmount;
      final float textureAmount;
      final float corzclientAmount;
      final float glyphAmount;
      final float uvTop;
      final float uvLeft;
      final float glyphOffsetY;

      Glyph(MsdfFontData.Glyph glyph, float glyphDivisor, float glyphDivisor2) {
         this.corzclientAmount = glyph.amount;
         MsdfFontData.Bounds class1912 = glyph.class1912;
         if (class1912 != null) {
            this.uvLeft = class1912.amount4 / glyphDivisor;
            this.uvRight = class1912.amount3 / glyphDivisor;
            this.uvTop = 1.0F - class1912.amount2 / glyphDivisor2;
            this.textureAmount = 1.0F - class1912.amount / glyphDivisor2;
         } else {
            this.uvLeft = this.uvRight = this.uvTop = this.textureAmount = 0.0F;
         }

         MsdfFontData.Bounds class191 = glyph.class191;
         if (class191 != null) {
            this.glyphAmount = class191.amount3 - class191.amount4;
            this.positionAmount = class191.amount2 - class191.amount;
            this.glyphOffsetY = class191.amount2;
         } else {
            this.glyphAmount = this.positionAmount = this.glyphOffsetY = 0.0F;
         }
      }

      boolean isAmount() {
         return this.glyphAmount > 0.0F && this.positionAmount > 0.0F && (this.uvRight != this.uvLeft || this.textureAmount != this.uvTop);
      }
   }
}
