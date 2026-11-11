package com.corz.client.render;

import com.corz.client.CorzClient;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.utils.AbcdefghijklmnopqrstuvwxyzBrownApi;
import com.corz.client.utils.CardStatusUtilsFlag;

public class VertexTextureFont implements AbcdefghijklmnopqrstuvwxyzBrownApi {
   private Graphics2D graphics;
   private final float[] fontMetricsTable;
   private NativeImage nativeImage;
   private final float corzclientAmount;
   private FontMetrics fontMetrics;
   private boolean nativeImageActive;
   private final int baseFontSize = 32;
   private final float[] vertexTextureTable;
   private Font deriveFont;
   private final int corzclient;
   private final Identifier identifier;
   private BufferedImage bufferedImage;
   private final int vertexTextureValue = 255;
   private  boolean corzclientFlag;
   public static int corzclientCount = 0;

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzBrown() {
      return (float)this.getStringBounds("W").getHeight() / 2.0F;
   }

   private void handlePopPush(MatrixStack matrices, String vertexTextureText, float vertexTextureValue, float abcdefghijklmnopqrstuvwxyzBrown, Color color, Color color4, boolean vertexTextureFlag) {
      matrices.push();

      try {
         matrices.scale(0.5F, 0.5F, 0.5F);
         if (vertexTextureFlag) {
            this.handlePositionBegin(matrices, vertexTextureText, vertexTextureValue + 0.5F, abcdefghijklmnopqrstuvwxyzBrown + 0.5F, color4);
         }

         this.handlePositionBegin(matrices, vertexTextureText, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, color);
      } finally {
         matrices.pop();
      }
   }

   public int getAbcdefghijklmnopqrstuvwxyzBrown(int brownValue, int boxHeight) {
      return brownValue + (boxHeight - (int)this.getAbcdefghijklmnopqrstuvwxyzBrown()) / 2 - 2;
   }

   private void updateGraphicsBuffered() {
      this.bufferedImage = new BufferedImage(256, 256, 2);
      this.graphics = this.bufferedImage.createGraphics();
      this.graphics
         .setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, this.corzclientFlag ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF
         );
      this.graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
   }

   private Rectangle2D getStringBounds(String stringBoundsText) {
      return this.fontMetrics.getStringBounds(stringBoundsText, this.graphics);
   }

   public VertexTextureFont(InputStream inputStream, float vertexTextureValue, boolean vertexTextureFlag) {
      this(inputStream, vertexTextureValue, null, vertexTextureFlag);
   }

   public void handleFlag(String[] texts, float[] vertexTextureValues, int vertexTextureLimit, float vertexTextureValue, Color color) {
      for (int i = 0; i < vertexTextureLimit; i++) {
         this.handleForStringFloat(texts[i], vertexTextureValues[i], vertexTextureValue, color);
      }
   }

   private void updateNativeImage() {
      if (!this.nativeImageActive) {
         if (this.nativeImage != null) {
            NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(() -> "corz_font_" + this.corzclient, this.nativeImage);
            MinecraftClient.getInstance().getTextureManager().registerTexture(this.identifier, nativeImageBackedTexture);
            this.nativeImageActive = true;
         }
      }
   }

   @Override
   public float getAbcdefghijklmnopqrstuvwxyzCorzfont(String corzfontText) {
      return (float)this.getStringBounds(CardStatusUtilsFlag.getOrDefault(corzfontText)).getWidth() / 2.0F;
   }

   public String formatText(String formatText, float vertexTextureLimit) {
      if (formatText != null && !formatText.isEmpty()) {
         if (this.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText) <= vertexTextureLimit) {
            return formatText;
         } else {
            String vertexTextureText = formatText;

            while (vertexTextureText.length() > 1 && this.getAbcdefghijklmnopqrstuvwxyzCorzfont(vertexTextureText + "...") > vertexTextureLimit) {
               vertexTextureText = vertexTextureText.substring(0, vertexTextureText.length() - 1);
            }

            return vertexTextureText.stripTrailing() + "...";
         }
      } else {
         return "";
      }
   }

   private VertexTextureFont(InputStream inputStream, float corzclientAmount, Integer integer, boolean corzclientFlag) {
      this.vertexTextureTable = new float[223];
      this.fontMetricsTable = new float[223];
      this.nativeImageActive = false;
      this.corzclientFlag = true;
      this.corzclientAmount = corzclientAmount;
      this.corzclientFlag = corzclientFlag;
      this.corzclient = corzclientCount++;
      this.identifier = Identifier.of("corzclient", "font/atlas_" + this.corzclient);
      this.updateGraphicsBuffered();
      this.handleFontMetrics(inputStream, corzclientAmount, integer);
   }

   private void handleFontMetrics(InputStream inputStream, float fontMetricsValue, Integer integer) {
      try {
         this.deriveFont = Font.createFont(0, inputStream);
         if (integer != null) {
            this.deriveFont = this.deriveFont.deriveFont(integer, fontMetricsValue);
         } else {
            this.deriveFont = this.deriveFont.deriveFont(fontMetricsValue);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.graphics.setFont(this.deriveFont);
      this.graphics.setColor(new Color(255, 255, 255, 0));
      this.graphics.fillRect(0, 0, 256, 256);
      this.graphics.setColor(Color.white);
      this.fontMetrics = this.graphics.getFontMetrics();
      if (this.fontMetrics != null) {
         float fontMetricsTotal = 5.0F;
         float fontMetricsTotal2 = 5.0F;

         for (int i = 32; i < 255; i++) {
            this.graphics.drawString(String.valueOf((char)i), fontMetricsTotal, fontMetricsTotal2 + this.fontMetrics.getAscent());
            this.vertexTextureTable[i - 32] = fontMetricsTotal;
            this.fontMetricsTable[i - 32] = fontMetricsTotal2 - this.fontMetrics.getMaxDescent();
            fontMetricsTotal += this.fontMetrics.stringWidth(String.valueOf((char)i)) + 2.0F;
            if (fontMetricsTotal >= 250 - this.fontMetrics.getMaxAdvance()) {
               fontMetricsTotal = 5.0F;
               fontMetricsTotal2 += this.fontMetrics.getMaxAscent() + this.fontMetrics.getMaxDescent() + Math.round(this.corzclientAmount / 2.0F);
            }
         }
      }

      this.handleArgbNative(this.bufferedImage);
   }

   private float getVertexTexture(BufferBuilder bufferBuilder, Matrix4f matrix4f, char ch, float vertexTextureValue, float abcdefghijklmnopqrstuvwxyzBrown, float redScaled, float greenScaled, float blueScaled, float vertexTextureValue6) {
      Rectangle2D stringBounds = this.fontMetrics.getStringBounds(String.valueOf(ch), this.graphics);
      float vertexTextureFactor = this.vertexTextureTable[ch - ' '];
      float vertexTextureFactor2 = this.fontMetricsTable[ch - ' '];
      float width = (float)stringBounds.getWidth();
      float vertexTextureValue7 = (float)stringBounds.getHeight() + this.fontMetrics.getMaxDescent() + 1.0F;
      float vertexTextureFactor3 = 0.0039063F;
      bufferBuilder.vertex(matrix4f, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown + vertexTextureValue7, 0.0F).texture(vertexTextureFactor * vertexTextureFactor3, (vertexTextureFactor2 + vertexTextureValue7) * vertexTextureFactor3).color(redScaled, greenScaled, blueScaled, vertexTextureValue6);
      bufferBuilder.vertex(matrix4f, vertexTextureValue + width, abcdefghijklmnopqrstuvwxyzBrown + vertexTextureValue7, 0.0F).texture((vertexTextureFactor + width) * vertexTextureFactor3, (vertexTextureFactor2 + vertexTextureValue7) * vertexTextureFactor3).color(redScaled, greenScaled, blueScaled, vertexTextureValue6);
      bufferBuilder.vertex(matrix4f, vertexTextureValue + width, abcdefghijklmnopqrstuvwxyzBrown, 0.0F).texture((vertexTextureFactor + width) * vertexTextureFactor3, vertexTextureFactor2 * vertexTextureFactor3).color(redScaled, greenScaled, blueScaled, vertexTextureValue6);
      bufferBuilder.vertex(matrix4f, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, 0.0F).texture(vertexTextureFactor * vertexTextureFactor3, vertexTextureFactor2 * vertexTextureFactor3).color(redScaled, greenScaled, blueScaled, vertexTextureValue6);
      return vertexTextureValue + (float)this.getStringBounds(String.valueOf(ch)).getWidth();
   }

   public VertexTextureFont(InputStream inputStream, float vertexTextureValue, int integer) {
      this(inputStream, vertexTextureValue, integer, true);
   }

   public float getAbcdefghijklmnopqrstuvwxyzBrownBrownActive() {
      return this.getAbcdefghijklmnopqrstuvwxyzBrown() * 0.5F;
   }

   private void handleArgbNative(BufferedImage bufferedImage) {
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int[] ints = new int[width * height];
      bufferedImage.getRGB(0, 0, width, height, ints, 0, width);
      NativeImage nativeImage = new NativeImage(width, height, false);

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            nativeImage.setColorArgb(j, i, ints[i * width + j]);
         }
      }

      this.nativeImage = nativeImage;
   }

   protected float getMetricsFont() {
      return this.fontMetrics == null ? this.getAbcdefghijklmnopqrstuvwxyzBrown() : (this.fontMetrics.getAscent() + this.fontMetrics.getMaxDescent()) * 0.5F;
   }

   public VertexTextureFont(InputStream inputStream, float vertexTextureValue) {
      this(inputStream, vertexTextureValue, null, true);
   }

   private void handleFont(String fontText, float fontValue, float abcdefghijklmnopqrstuvwxyzBrown, Color color, boolean fontFlag) {
      fontText = CardStatusUtilsFlag.getOrDefault(fontText);
      int minAlpha = Math.min(187, color.getAlpha());
      this.handlePopPush(CorzClient.fontManager.getMatrixStack(), fontText, fontValue, abcdefghijklmnopqrstuvwxyzBrown, color, new Color(0, 0, 0, minAlpha == -1 ? color.getAlpha() : minAlpha), fontFlag);
   }

   @Override
   public void handle(String vertexTextureText, float vertexTextureValue, float abcdefghijklmnopqrstuvwxyzBrown, Color color) {
      this.handleFont(vertexTextureText, vertexTextureValue, abcdefghijklmnopqrstuvwxyzBrown, color, false);
   }

   @Override
   public void handleForStringFloat(String vertexTextureText, float vertexTextureValue, float f13, Color color) {
      this.handleFont(vertexTextureText, vertexTextureValue, f13, color, true);
   }

   public void handleFlag(String[] strings, float[] vertexTextureValues, float[] vertexTextureValues2, int vertexTextureLimit, Color color) {
      for (int i = 0; i < vertexTextureLimit; i++) {
         this.handle(strings[i], vertexTextureValues[i], vertexTextureValues2[i], color);
      }
   }

   private void handlePositionBegin(MatrixStack matrices, String positionBeginText, float positionBeginValue, float abcdefghijklmnopqrstuvwxyzBrown, Color color) {
      StringBuilder stringBuilder = new StringBuilder();

      for (char positionBeginLimit : positionBeginText.toCharArray()) {
         if (positionBeginLimit >= ' ' && positionBeginLimit < 255) {
            stringBuilder.append(positionBeginLimit);
         } else {
            stringBuilder.append("?");
         }
      }

      positionBeginText = stringBuilder.toString();
      if (!positionBeginText.isEmpty()) {
         positionBeginValue *= 2.0F;
         abcdefghijklmnopqrstuvwxyzBrown *= 2.0F;
         this.updateNativeImage();
         if (this.nativeImageActive) {
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
            float redScaled = color.getRed() / 255.0F;
            float greenScaled = color.getGreen() / 255.0F;
            float blueScaled = color.getBlue() / 255.0F;
            float positionBeginValue3 = color.getAlpha() / 255.0F * CorzRoundedUtils.panelOverlayAmount;
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            float x2 = positionBeginValue;

            for (int i = 0; i < positionBeginText.length(); i++) {
               try {
                  x2 = this.getVertexTexture(begin, positionMatrix, positionBeginText.charAt(i), x2, abcdefghijklmnopqrstuvwxyzBrown, redScaled, greenScaled, blueScaled, positionBeginValue3);
               } catch (ArrayIndexOutOfBoundsException e) {
               }
            }

            CorzRoundedUtils.handleTextureTextured(begin.end(), this.identifier);
         }
      }
   }
}
