package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.MsdfFontData;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.utils.CardStatusUtilsFlag;

public class CorzfontAbcdefghijklmnopqrstuvwxyzTexture extends VertexTextureFont {
   private  float abcdefghijklmnopqrstuvwxyzBrownAmount;
   private static float corzfontParsedAmount;
   private static final Identifier identifier = Identifier.of("corzclient", "font/msdf_corzfont");
   private static CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph corzfontParsedGlyph;
   private static NativeImage readCorzfontParsed;
   private static final Map<Integer, CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph> CORZFONT_CORZCLIENT_MAP = new HashMap<>();
   private boolean fontBufferActive;
   private final boolean brownActive;
   private  float brownAmount;
   private static final Map<Long, Float> CORZFONT_PNG_MAP = new HashMap<>();
   private static float parsedCorzfontAmount;
   private static final String QUICK_BROWN_FOX = "The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789";
   private static final CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[] corzfontCorzclientGlyphs = new CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph[256];
   private static boolean corzfontPngFlag;
   private static boolean corzfontJsonFlag = false;
   private BufferBuilder bufferBuilder;
   private static boolean corzfontParsedActive = false;
   private  float metricsFont;
   private static boolean fontMsdfFlag = false;

   private void handleFontBuffer(String[] strings, float[] fontBufferValues, float[] fontBufferValues2, float fontBufferValue, int fontBufferLimit, Color color) {
      MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
      Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
      float redScaled = color.getRed() / 255.0F;
      float greenScaled = color.getGreen() / 255.0F;
      float blueScaled = color.getBlue() / 255.0F;
      float fontBufferValue2 = color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount;
      BufferBuilder bufferBuilder = this.fontBufferActive ? this.bufferBuilder : null;

      for (int i = 0; i < fontBufferLimit; i++) {
         String orDefault = CardStatusUtilsFlag.getOrDefault(strings[i]);
         if (orDefault != null && !orDefault.isEmpty()) {
            updateMsdfTexture();
            if (!fontMsdfFlag) {
               break;
            }

            bufferBuilder = this.getAmountAbcdefghijklmnopqrstuvwxyz(bufferBuilder, positionMatrix, orDefault, fontBufferValues[i], fontBufferValues2 == null ? fontBufferValue : fontBufferValues2[i], redScaled, greenScaled, blueScaled, fontBufferValue2);
         }
      }

      if (this.fontBufferActive) {
         this.bufferBuilder = bufferBuilder;
      } else {
         if (bufferBuilder != null) {
            CorzRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
         }
      }
   }

   public CorzfontAbcdefghijklmnopqrstuvwxyzTexture(float corzfontTextureValue) {
      super(getAsStream(), corzfontTextureValue);

      boolean flag;
      try {
         updateCorzfontParsed();
         float corzfontTextureValue2 = super.getAbcdefghijklmnopqrstuvwxyzCorzfont("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         float corzfontCorzclient = getCorzfontCorzclient("The quick brown fox ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789");
         this.brownAmount = (
               corzfontCorzclient > 0.0F ? corzfontTextureValue2 / corzfontCorzclient : super.getAbcdefghijklmnopqrstuvwxyzBrown() / corzfontParsedAmount
            )
            * 1.0F;
         this.abcdefghijklmnopqrstuvwxyzBrownAmount = super.getAbcdefghijklmnopqrstuvwxyzBrown();
         this.metricsFont = this.getMetricsFont();
         flag = !corzfontParsedActive;
      } catch (Throwable e) {
         e.printStackTrace();
         flag = false;
      }

      this.brownActive = flag;
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrownBrownActive() {
      return this.brownActive ? 0.716F * this.brownAmount : super.getAbcdefghijklmnopqrstuvwxyzBrownBrownActive();
   }

   @Override
   public void handleFlag(String[] texts, float[] corzfontTextureValues, int corzfontTextureValue, float vertexTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(texts, corzfontTextureValues, corzfontTextureValue, vertexTextureValue, color);
      } else {
         this.handleFontBuffer(texts, corzfontTextureValues, null, vertexTextureValue, corzfontTextureValue, color);
      }
   }

   @Override
   public void handleForStringFloat(String corzfontTextureText, float corzfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(corzfontTextureText, corzfontTextureValue, f13, color);
   }

   public void updateBufferBuilder() {
      BufferBuilder bufferBuilder = this.bufferBuilder;
      this.fontBufferActive = false;
      this.bufferBuilder = null;
      if (bufferBuilder != null) {
         CorzRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
      }
   }

   @Override
   public void handleFlag(String[] strings, float[] corzfontTextureValues, float[] corzfontTextureValues2, int corzfontTextureValue, Color color) {
      if (!this.brownActive) {
         super.handleFlag(strings, corzfontTextureValues, corzfontTextureValues2, corzfontTextureValue, color);
      } else {
         this.handleFontBuffer(strings, corzfontTextureValues, corzfontTextureValues2, 0.0F, corzfontTextureValue, color);
      }
   }

   public boolean isFlagBuffer() {
      if (this.brownActive && !this.fontBufferActive) {
         this.fontBufferActive = true;
         this.bufferBuilder = null;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getAbcdefghijklmnopqrstuvwxyzBrown(int brownValue, int boxHeight) {
      return !this.brownActive
         ? super.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, boxHeight)
         : Math.round(brownValue + boxHeight / 2.0F - (this.metricsFont - 0.716F * this.brownAmount / 2.0F));
   }

   private static long getTime(int corzfontTextureValue, int charAt) {
      return (long)corzfontTextureValue << 32 | charAt & 4294967295L;
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzCorzfont(String corzfontText) {
      corzfontText = CardStatusUtilsFlag.getOrDefault(corzfontText);
      if (!this.brownActive) {
         return super.getAbcdefghijklmnopqrstuvwxyzCorzfont(corzfontText);
      } else if (corzfontText != null && !corzfontText.isEmpty()) {
         float corzfontTotal = 0.0F;
         int corzfontValue = -1;

         for (int i = 0; i < corzfontText.length(); i++) {
            char charAt = corzfontText.charAt(i);
            CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph corzfontCorzclient = getCorzfontCorzclient(charAt);
            if (corzfontCorzclient == null) {
               corzfontValue = charAt;
            } else {
               if (corzfontPngFlag && corzfontValue != -1) {
                  Float corzfont_png_map = CORZFONT_PNG_MAP.get(getTime(corzfontValue, charAt));
                  if (corzfont_png_map != null) {
                     corzfontTotal += corzfont_png_map * this.brownAmount;
                  }
               }

               corzfontTotal += corzfontCorzclient.corzfontAmount * this.brownAmount;
               corzfontValue = charAt;
            }
         }

         return corzfontTotal;
      } else {
         return 0.0F;
      }
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrown() {
      return this.brownActive ? this.abcdefghijklmnopqrstuvwxyzBrownAmount : super.getAbcdefghijklmnopqrstuvwxyzBrown();
   }

   private static CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph getCorzfontCorzclient(int index) {
      CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph corzfontCorzclient = index < 256 ? corzfontCorzclientGlyphs[index] : CORZFONT_CORZCLIENT_MAP.get(index);
      if (corzfontCorzclient == null && index != 32) {
         corzfontCorzclient = corzfontParsedGlyph;
      }

      return corzfontCorzclient;
   }

   private static synchronized void updateCorzfontParsed()  throws java.io.IOException {
      if (!corzfontJsonFlag && !corzfontParsedActive) {
         try {
            MsdfFontData fromJson4;
            try (InputStream resourceAsStream = CorzClient.class.getClassLoader().getResourceAsStream("assets/corzclient/fonts/corzfont.json")) {
               if (resourceAsStream == null) {
                  throw new IllegalStateException("corzfont.json missing from resources");
               }

               fromJson4 = (MsdfFontData)new Gson().fromJson(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8), MsdfFontData.class);
            }

            if (fromJson4 != null && fromJson4.class172 != null && fromJson4.class245 != null && fromJson4.parsedCorzfontItems != null) {
               corzfontParsedAmount = fromJson4.class245.parsedCorzfontAmount;
               parsedCorzfontAmount = fromJson4.class245.parsedCorzfontAmount2;
               float parsedCorzfontAmount2 = fromJson4.class172.parsedCorzfontAmount;
               float parsedCorzfontAmount22 = fromJson4.class172.parsedCorzfontAmount2;

               for (MsdfFontData.Glyph glyph : fromJson4.parsedCorzfontItems) {
                  CORZFONT_CORZCLIENT_MAP.put(glyph.value, new CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph(glyph, parsedCorzfontAmount2, parsedCorzfontAmount22));
               }

               if (fromJson4.items != null) {
                  for (MsdfFontData.Kerning kerning : fromJson4.items) {
                     CORZFONT_PNG_MAP.put(getTime(kerning.value, kerning.value2), kerning.amount);
                  }
               }

               for (int i = 0; i < corzfontCorzclientGlyphs.length; i++) {
                  corzfontCorzclientGlyphs[i] = CORZFONT_CORZCLIENT_MAP.get(i);
               }

               corzfontParsedGlyph = CORZFONT_CORZCLIENT_MAP.get(63);
               corzfontPngFlag = !CORZFONT_PNG_MAP.isEmpty();

               try (InputStream resourceAsStream2 = CorzClient.class.getClassLoader().getResourceAsStream("assets/corzclient/fonts/corzfont.png")) {
                  if (resourceAsStream2 == null) {
                     throw new IllegalStateException("corzfont.png missing from resources");
                  }

                  readCorzfontParsed = NativeImage.read(resourceAsStream2);
               }

               corzfontJsonFlag = true;
            } else {
               throw new IllegalStateException("corzfont.json parsed empty");
            }
         } catch (Throwable e) {
            corzfontParsedActive = true;
            throw e;
         }
      }
   }

   private void handleFontBuffer(String fontBufferText, float fontBufferValue, float f13, Color color) {
      fontBufferText = CardStatusUtilsFlag.getOrDefault(fontBufferText);
      if (!this.brownActive) {
         super.handleForStringFloat(fontBufferText, fontBufferValue, f13, color);
      } else if (fontBufferText != null && !fontBufferText.isEmpty()) {
         updateMsdfTexture();
         if (fontMsdfFlag) {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            float redScaled = color.getRed() / 255.0F;
            float greenScaled = color.getGreen() / 255.0F;
            float blueScaled = color.getBlue() / 255.0F;
            float fontBufferValue3 = color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount;
            BufferBuilder bufferBuilder = this.getAmountAbcdefghijklmnopqrstuvwxyz(this.fontBufferActive ? this.bufferBuilder : null, positionMatrix, fontBufferText, fontBufferValue, f13, redScaled, greenScaled, blueScaled, fontBufferValue3);
            if (this.fontBufferActive) {
               this.bufferBuilder = bufferBuilder;
            } else {
               if (bufferBuilder != null) {
                  CorzRoundedUtils.handleTextureView(bufferBuilder.end(), identifier);
               }
            }
         }
      }
   }

   private BufferBuilder getAmountAbcdefghijklmnopqrstuvwxyz(
      BufferBuilder begin2, Matrix4f matrix4f, String corzfontTextureText, float corzfontTextureValue, float f13, float redScaled, float greenScaled, float blueScaled, float corzfontTextureValue6
   ) {
      float corzfontTextureValue7 = f13 + this.metricsFont;
      float corzfontTextureTotal = corzfontTextureValue;
      int charAt8 = -1;

      for (int i = 0; i < corzfontTextureText.length(); i++) {
         char charAt = corzfontTextureText.charAt(i);
         CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph corzfontCorzclient = getCorzfontCorzclient(charAt);
         if (corzfontCorzclient == null) {
            charAt8 = charAt;
         } else {
            if (corzfontPngFlag && charAt8 != -1) {
               Float corzfont_png_map = CORZFONT_PNG_MAP.get(getTime(charAt8, charAt));
               if (corzfont_png_map != null) {
                  corzfontTextureTotal += corzfont_png_map * this.brownAmount;
               }
            }

            if (corzfontCorzclient.isAmount()) {
               if (begin2 == null) {
                  begin2 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               }

               float corzfontTextureValue9 = corzfontTextureValue7 - corzfontCorzclient.planeTop * this.brownAmount;
               float corzfontTextureValue10 = corzfontCorzclient.planeWidth * this.brownAmount;
               float corzfontTextureValue11 = corzfontCorzclient.planeHeight * this.brownAmount;
               begin2.vertex(matrix4f, corzfontTextureTotal, corzfontTextureValue9 + corzfontTextureValue11, 0.0F).texture(corzfontCorzclient.textureULeft, corzfontCorzclient.glyphAmount).color(redScaled, greenScaled, blueScaled, corzfontTextureValue6);
               begin2.vertex(matrix4f, corzfontTextureTotal + corzfontTextureValue10, corzfontTextureValue9 + corzfontTextureValue11, 0.0F).texture(corzfontCorzclient.textureURight, corzfontCorzclient.glyphAmount).color(redScaled, greenScaled, blueScaled, corzfontTextureValue6);
               begin2.vertex(matrix4f, corzfontTextureTotal + corzfontTextureValue10, corzfontTextureValue9, 0.0F).texture(corzfontCorzclient.textureURight, corzfontCorzclient.textureVTop).color(redScaled, greenScaled, blueScaled, corzfontTextureValue6);
               begin2.vertex(matrix4f, corzfontTextureTotal, corzfontTextureValue9, 0.0F).texture(corzfontCorzclient.textureULeft, corzfontCorzclient.textureVTop).color(redScaled, greenScaled, blueScaled, corzfontTextureValue6);
            }

            corzfontTextureTotal += corzfontCorzclient.corzfontAmount * this.brownAmount;
            charAt8 = charAt;
         }
      }

      return begin2;
   }

   private static InputStream getAsStream() {
      return CorzClient.class.getClassLoader().getResourceAsStream("assets/corzclient/font/font.ttf");
   }

   @Override
   public void handle(String corzfontTextureText, float corzfontTextureValue, float f13, Color color) {
      this.handleFontBuffer(corzfontTextureText, corzfontTextureValue, f13, color);
   }

   private static float getCorzfontCorzclient(String corzfontCorzclientText) {
      float corzfontCorzclient = 0.0F;
      int corzfontCorzclientValue = -1;

      for (int i = 0; i < corzfontCorzclientText.length(); i++) {
         char charAt = corzfontCorzclientText.charAt(i);
         CorzfontAbcdefghijklmnopqrstuvwxyzTexture.Glyph corzfont_corzclient_map = CORZFONT_CORZCLIENT_MAP.get(Integer.valueOf(charAt));
         if (corzfont_corzclient_map == null && charAt != ' ') {
            corzfont_corzclient_map = CORZFONT_CORZCLIENT_MAP.get(63);
         }

         if (corzfont_corzclient_map == null) {
            corzfontCorzclientValue = charAt;
         } else {
            if (corzfontCorzclientValue != -1) {
               Float corzfont_png_map = CORZFONT_PNG_MAP.get(getTime(corzfontCorzclientValue, charAt));
               if (corzfont_png_map != null) {
                  corzfontCorzclient += corzfont_png_map;
               }
            }

            corzfontCorzclient += corzfont_corzclient_map.corzfontAmount;
            corzfontCorzclientValue = charAt;
         }
      }

      return corzfontCorzclient;
   }

   private static void updateMsdfTexture() {
      if (!fontMsdfFlag && readCorzfontParsed != null) {
         NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "corz_msdf_corzfont", readCorzfontParsed);
         MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, nativeImageBackedTexture);
         fontMsdfFlag = true;
      }
   }

   private static final class Glyph {
      final float textureVTop;
      final float corzfontAmount;
      final float glyphAmount;
      final float textureULeft;
      final float planeHeight;
      final float textureURight;
      final float planeWidth;
      final float planeTop;

      boolean isAmount() {
         return this.planeWidth > 0.0F && this.planeHeight > 0.0F && (this.textureURight != this.textureULeft || this.glyphAmount != this.textureVTop);
      }

      Glyph(MsdfFontData.Glyph glyph, float glyphDivisor, float glyphDivisor2) {
         this.corzfontAmount = glyph.amount;
         MsdfFontData.Bounds class1912 = glyph.class1912;
         if (class1912 != null) {
            this.textureULeft = class1912.amount4 / glyphDivisor;
            this.textureURight = class1912.amount3 / glyphDivisor;
            this.textureVTop = 1.0F - class1912.amount2 / glyphDivisor2;
            this.glyphAmount = 1.0F - class1912.amount / glyphDivisor2;
         } else {
            this.textureULeft = this.textureURight = this.textureVTop = this.glyphAmount = 0.0F;
         }

         MsdfFontData.Bounds class191 = glyph.class191;
         if (class191 != null) {
            this.planeWidth = class191.amount3 - class191.amount4;
            this.planeHeight = class191.amount2 - class191.amount;
            this.planeTop = class191.amount2;
         } else {
            this.planeWidth = this.planeHeight = this.planeTop = 0.0F;
         }
      }
   }
}
