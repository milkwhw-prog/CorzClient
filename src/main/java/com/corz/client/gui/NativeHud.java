package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.VertexTextureFont;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public final class NativeHud {
   public static final int topbarCloudValue = TwoDidUtils.getValue(3, 1);
   public static final int TPS_SIGNAL_BAR_COUNT = TwoDidUtils.getValue(0, 3);
   public static final int keyboardKeybindsValue = TwoDidUtils.getValue(3, 6);
   public static final int backpackResourcesValue = TwoDidUtils.getValue(2, 7);
   private static long lastNanoTime;
   private static float reportedAmount;
   public static final int staffValue = TwoDidUtils.getValue(6, 8);
   public static final int flaskPotionsValue = TwoDidUtils.getValue(1, 9);
   public static final int timerCooldownsValue = TwoDidUtils.getValue(3, 10);
   public static final int gridLayoutValue = TwoDidUtils.getValue(1, 11);
   private static int whiteAlpha40Color;
   public static final int personStaffValue = TwoDidUtils.getValue(0, 12);
   private static int nativeHudValue;
   public static final int smoothValue = TwoDidUtils.getValue(0, 13);
   public static final int fieldsTotemsValue = TwoDidUtils.getValue(6, 14);
   public static final int lerpUuidValue = TwoDidUtils.getValue(2, 15);
   private static int whiteBaseRgb;
   private static int whiteAlpha22Color;
   public static final int pinValue = TwoDidUtils.getValue(0, 16);
   public static final int shieldValue = TwoDidUtils.getValue(1, 17);
   public static final int corzValue = TwoDidUtils.getValue(2, 18);
   public static final int cubeValue = TwoDidUtils.getValue(3, 19);
   private static int whiteAlpha15Color;
   public static final int resizeScrollValue = TwoDidUtils.getValue(3, 20);
   public static final int scrollValue = TwoDidUtils.getValue(3, 21);
   public static final int SPOTIFY_HUD_ID = TwoDidUtils.getValue(0, 22);
   public static final int COLOR_PANEL_ID = TwoDidUtils.getValue(1, 23);
   public static final int barInventoryValue = TwoDidUtils.getValue(2, 24);
   public static final int performanceValue = TwoDidUtils.getValue(0, 25);
   public static final Color OFF_WHITE = new Color(-920587, true);
   private static boolean whiteActive;
   public static final Color GRAY = new Color(-7630437, true);
   public static final Color DARK_BLUE = new Color(-11117467, true);
   private static float smoothedAmount;
   public static final Color RED = new Color(-1750963, true);
   public static final Color WHITE = new Color(-1, true);
   private static final int[] nativeHudTable = new int[65];
   private static final boolean[] whiteFlags = new boolean[65];
   private static long lastAmountTimeNanos;
   private static final Identifier[] identifiers = new Identifier[33];
   private static Color white = Color.WHITE;
   private static Color whiteColor = Color.WHITE;

   public static void handle(int nativeHudValue, float peekPositionValue, float matrixStackValue, float panelOverlayBound, float hashHuntBound, float wrapDegreesBound, float panelOverlayFactor) {
      handlePanelOverlay(nativeHudValue, peekPositionValue, matrixStackValue, panelOverlayBound, hashHuntBound, wrapDegreesBound, 1.0F, panelOverlayFactor);
   }

   private static void handleMaxText(NativeHud.Field field, float nativeHudValue, float scaleValue, float f13, float x2, Color color) {
      float nativeHudFactor = field.getTime();
      if (nativeHudFactor >= 1.0F) {
         handlePushScale(field.bloodhoundBestText, scaleValue, f13, x2, color);
      } else {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(1.0F - nativeHudFactor * nativeHudFactor);

         try {
            handlePushScale(field.fieldText, nativeHudValue, f13, x2, color);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }

         float panelOverlay2 = CorzRoundedUtils.getPanelOverlay(getAmount5(nativeHudFactor));

         try {
            handlePushScale(field.bloodhoundBestText, scaleValue, f13, x2, color);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay2);
         }
      }
   }

   public static void handle(Identifier id, float nativeHudValue, float roundOffset, float peekPositionFactor, Color color) {
      if (id != null) {
         MatrixPeekUtilsIdentifier.handle(nativeHudValue, roundOffset, peekPositionFactor, peekPositionFactor, id, color, MatrixPeekUtilsRender.getMatrixStack());
      }
   }

   private static Identifier getIdentifier(int index) {
      Identifier identifier = identifiers[index];
      if (identifier != null) {
         return identifier;
      } else {
         int identifierValue = index;
         int identifierLimit = (int)Math.ceil(index / 2.0F + 0.5F);
         int identifierLimit2 = identifierLimit * 2 + 2;
         int identifierLimit3 = index + 2;

         Identifier of2;
         try {
            NativeImage nativeImage = new NativeImage(identifierLimit2, identifierLimit3, false);
            float identifierValue2 = index / 2.0F;

            for (int i = 0; i < identifierLimit3; i++) {
               float identifierFactor = i + 0.5F - identifierLimit3 / 2.0F;

               for (int j = 0; j < identifierLimit2; j++) {
                  float identifierFactor2 = j < identifierLimit ? j - identifierLimit : (j > identifierLimit2 - 1 - identifierLimit ? j - (identifierLimit2 - 1 - identifierLimit) : 0.0F);
                  int round = Math.round(getMax(identifierValue2 + 0.5F - (float)Math.sqrt(identifierFactor2 * identifierFactor2 + identifierFactor * identifierFactor)) * 255.0F);
                  nativeImage.setColorArgb(j, i, round << 24 | 16777215);
               }
            }

            of2 = Identifier.of("corzclient", "gen/hud_ring_" + index);
            MinecraftClient.getInstance().getTextureManager().registerTexture(of2, new NativeImageBackedTexture(() -> "corz_hud_ring_" + identifierValue, nativeImage));
         } catch (Throwable e) {
            return null;
         }

         identifiers[index] = of2;
         return of2;
      }
   }

   private static void handleForIntFloat(int nativeHudValue, float peekPositionValue, float glfwLimit, float panelOverlayBound, float regionmapRegionTotal, float nativeHudValue6, float panelOverlayFactor) {
      handlePanelOverlay(nativeHudValue, peekPositionValue, glfwLimit, panelOverlayBound, regionmapRegionTotal, 8.0F * nativeHudValue6, nativeHudValue6, panelOverlayFactor);
   }

   public static void render(DrawContext context, float nativeHudValue, float y, float crossLimit, int nativeHudValue4) {
      handlePeekPosition(nativeHudValue - crossLimit / 2.0F, y - crossLimit / 2.0F, crossLimit, crossLimit, crossLimit / 2.0F, 0.0F, nativeHudValue4);
   }

   public static void handlePushScale(String scaleText, float scaleValue, float f13, float x2, Color color) {
      if (scaleText != null && !scaleText.isEmpty()) {
         VertexTextureFont fontRenderer = getFontRenderer();
         if (Math.abs(x2 - 1.0F) < 5.0E-4F) {
            fontRenderer.handleForStringFloat(scaleText, scaleValue, f13, color);
         } else {
            MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
            matrixStack.push();
            matrixStack.scale(x2, x2, 1.0F);
            fontRenderer.handleForStringFloat(scaleText, scaleValue / x2, f13 / x2, color);
            matrixStack.pop();
         }
      }
   }

   public static boolean canTranslateMatrices(DrawContext context, float translateMatricesValue, float y, float x2) {
      if (Math.abs(x2 - 1.0F) <= 5.0E-4F) {
         return false;
      } else {
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
         handleTranslatePush(matrixStack, translateMatricesValue, y, x2);
         if (matrixStack2 != matrixStack) {
            handleTranslatePush(matrixStack2, translateMatricesValue, y, x2);
         }

         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();
         matrices.translate(translateMatricesValue, y);
         matrices.scale(x2, x2);
         matrices.translate(-translateMatricesValue, -y);
         return true;
      }
   }

   public static float getMax(float nativeHudLimit) {
      return nativeHudLimit < 0.0F ? 0.0F : (nativeHudLimit > 1.0F ? 1.0F : nativeHudLimit);
   }

   public static void handle(NativeHud.Field field, float nativeHudValue, float f13, float x2, Color color) {
      handleMaxText(field, nativeHudValue, nativeHudValue, f13, x2, color);
   }

   public static void handleMaxTime(float nativeHudBound) {
      reportedAmount = Math.max(0.0F, nativeHudBound);
      lastNanoTime = System.nanoTime();
   }

   public static VertexTextureFont getFontRenderer() {
      return CorzClient.fontRenderer();
   }

   public static void handleAmount(NativeHud.Field field, float nativeHudValue, float roundOffset, float x2, Color color) {
      handleMaxText(field, nativeHudValue - field.fieldAmount, nativeHudValue - field.textWidth, roundOffset, x2, color);
   }

   private static void handleVertexTexture(BufferBuilder buffer, Matrix4f matrix, float vertexTextureValue, float positionBeginBound, float vertexTextureValue3, float vertexTextureValue4, float vertexTextureValue5, float vertexTextureValue6, int vertexTextureValue7, int cardMatrixValue) {
      float panelOverlayAmount = CorzRoundedUtils.panelOverlayAmount;
      float vertexTextureValue9 = (vertexTextureValue7 >> 16 & 0xFF) / 255.0F;
      float vertexTextureValue10 = (vertexTextureValue7 >> 8 & 0xFF) / 255.0F;
      float vertexTextureValue11 = (vertexTextureValue7 & 0xFF) / 255.0F;
      float vertexTextureValue12 = (vertexTextureValue7 >>> 24 & 0xFF) / 255.0F * panelOverlayAmount;
      float vertexTextureValue13 = (cardMatrixValue >> 16 & 0xFF) / 255.0F;
      float vertexTextureValue14 = (cardMatrixValue >> 8 & 0xFF) / 255.0F;
      float vertexTextureValue15 = (cardMatrixValue & 0xFF) / 255.0F;
      float vertexTextureValue16 = (cardMatrixValue >>> 24 & 0xFF) / 255.0F * panelOverlayAmount;
      buffer.vertex(matrix, vertexTextureValue, vertexTextureValue4, 0.0F).texture(vertexTextureValue5, 1.0F).color(vertexTextureValue9, vertexTextureValue10, vertexTextureValue11, vertexTextureValue12);
      buffer.vertex(matrix, positionBeginBound, vertexTextureValue4, 0.0F).texture(vertexTextureValue6, 1.0F).color(vertexTextureValue13, vertexTextureValue14, vertexTextureValue15, vertexTextureValue16);
      buffer.vertex(matrix, positionBeginBound, vertexTextureValue3, 0.0F).texture(vertexTextureValue6, 0.0F).color(vertexTextureValue13, vertexTextureValue14, vertexTextureValue15, vertexTextureValue16);
      buffer.vertex(matrix, vertexTextureValue, vertexTextureValue3, 0.0F).texture(vertexTextureValue5, 0.0F).color(vertexTextureValue9, vertexTextureValue10, vertexTextureValue11, vertexTextureValue12);
   }

   public static void renderMatrixMatrices(DrawContext context, ItemStack itemStack, float matrixMatricesValue, float matrixMatricesValue2, float topbarBackpackAmount) {
      if (itemStack != null && !itemStack.isEmpty()) {
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();
         matrices.translate(matrixMatricesValue, matrixMatricesValue2);
         matrices.scale(topbarBackpackAmount / 16.0F, topbarBackpackAmount / 16.0F);
         DrawMatricesUtils.renderMatricesMatrix(context, itemStack, 0, 0, 16);
         matrices.popMatrix();
      }
   }

   public static void handle(float nativeHudValue, float matrixStackValue, float nativeHudValue3, float crossLimit, float lerpUuidFactor, float nativeHudLimit) {
      handlePeekPosition(nativeHudValue, matrixStackValue, nativeHudValue3, crossLimit, lerpUuidFactor, 0.0F, getValue(201326591, nativeHudLimit));
      handlePeekPosition(nativeHudValue, matrixStackValue, nativeHudValue3, crossLimit, lerpUuidFactor, MatrixPeekUtilsRender.getAmount(), getValue(318767103, nativeHudLimit));
   }

   public static boolean canFlag(SkinTextures skinTextures, float nativeHudValue, float randomValue, float crossLimit) {
      return canPeekBody(skinTextures, nativeHudValue, randomValue, crossLimit, 2.0F * MatrixPeekUtilsRender.getAmount());
   }

   public static float getAmount(
      DrawContext context, float nativeHudValue, float hudScaleValue, float bottomFactor, Identifier id, String nativeHudText, NativeHud.Field field, NativeHud.Pulse pulse, float colorLimit
   ) {
      return getColorAmount(context, nativeHudValue, hudScaleValue, bottomFactor, id, null, nativeHudText, field, pulse, colorLimit);
   }

   public static float getMaxForFloat(float nativeHudValue) {
      return (float)Math.ceil(Math.max(14.0F, nativeHudValue + 9.0F));
   }

   public static float getAmountTime() {
      long nanoTime = System.nanoTime();
      float amount2 = lastNanoTime != 0L && nanoTime - lastNanoTime < 250000000L ? reportedAmount : 0.0F;
      long nativeHudLimit = nanoTime - lastAmountTimeNanos;
      lastAmountTimeNanos = nanoTime;
      if (nativeHudLimit > 250000000L) {
         smoothedAmount = amount2;
         return smoothedAmount;
      } else {
         smoothedAmount = smoothedAmount + (amount2 - smoothedAmount) * (1.0F - (float)Math.exp(-((float)nativeHudLimit / 1.0E9F) / 0.08F));
         if (Math.abs(amount2 - smoothedAmount) < 0.05F) {
            smoothedAmount = amount2;
         }

         return smoothedAmount;
      }
   }

   private static void handleTranslatePush(MatrixStack matrices, float x, float y, float x2) {
      matrices.push();
      matrices.translate(x, y, 0.0F);
      matrices.scale(x2, x2, 1.0F);
      matrices.translate(-x, -y, 0.0F);
   }

   public static int getReleaseRight() {
      return 0xFF000000 | MatrixEspUtils.releaseRightValue & 16777215;
   }

   public static float getColorAmount(
      DrawContext context, float colorValue, float hudScaleValue, float bottomFactor, Identifier id, Color color, String colorText, NativeHud.Field field, NativeHud.Pulse pulse, float colorLimit
   ) {
      float matrixStackValue = hudScaleValue + 6.0F;
      handleColor(colorValue + 6.0F, matrixStackValue, 14.0F, 4.5F, 8.0F, id, color, 1.0F);
      handlePushScale(colorText, colorValue + 6.0F + 14.0F + 5.5F, getAmount2(matrixStackValue, 14.0F, 0.95F), 0.95F, OFF_WHITE);
      if (field != null && !field.isEmpty()) {
         float colorValue5 = colorValue + bottomFactor - 7.0F;
         boolean colorFlag = pulse != null && canTranslateMatrices(context, colorValue5 - field.textWidth / 2.0F, matrixStackValue + 7.0F, pulse.getTime());

         try {
            handleAmount(field, colorValue5, getAmount2(matrixStackValue, 14.0F, 0.8F), 0.8F, GRAY);
         } finally {
            renderPopMatrices(context, colorFlag);
         }
      }

      if (colorLimit > 0.002F) {
         renderForDrawContextFloatInt(context, colorValue + 6.0F, hudScaleValue + 26.0F, bottomFactor - 12.0F, getValue(268435455, colorLimit));
      }

      return hudScaleValue + 30.0F;
   }

   public static Color getWhite() {
      updateWhiteFlags();
      return white;
   }

   public static void handlePeekPosition(float peekPositionValue, float matrixStackValue, float crossLimit, float peekPositionValue4, float lerpUuidFactor, float hotbarMatrixBound, int peekPositionValue7) {
      if (!(crossLimit <= 0.0F) && !(peekPositionValue4 <= 0.0F) && peekPositionValue7 >>> 24 != 0) {
         float peekPositionDivisor = MatrixPeekUtilsRender.getAmount();
         CorzRoundedUtils.handleRectRounded(
            MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
            peekPositionValue - 1.0F,
            matrixStackValue - 1.0F,
            peekPositionValue + crossLimit + 1.0F,
            matrixStackValue + peekPositionValue4 + 1.0F,
            peekPositionValue / peekPositionDivisor,
            matrixStackValue / peekPositionDivisor,
            crossLimit / peekPositionDivisor,
            peekPositionValue4 / peekPositionDivisor,
            lerpUuidFactor / peekPositionDivisor,
            hotbarMatrixBound / peekPositionDivisor,
            (peekPositionValue7 >> 16 & 0xFF) / 255.0F,
            (peekPositionValue7 >> 8 & 0xFF) / 255.0F,
            (peekPositionValue7 & 0xFF) / 255.0F,
            (peekPositionValue7 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   public static int getValue(int nativeHudValue, float nativeHudLimit) {
      int round = Math.round((nativeHudValue >>> 24 & 0xFF) * getMax(nativeHudLimit));
      return round << 24 | nativeHudValue & 16777215;
   }

   public static void handleTextureVertex(float textureVertexValue, float textureVertexValue2, float lerpUuidFactor, float hotbarMatrixBound, float textureVertexLimit, int textureVertexValue5) {
      textureVertexLimit = getMax(textureVertexLimit);
      if (!(textureVertexLimit <= 0.001F) && !(lerpUuidFactor <= 0.0F) && !(hotbarMatrixBound <= 0.0F) && textureVertexValue5 >>> 24 != 0) {
         if (textureVertexLimit >= 0.999F) {
            handlePeekPosition(textureVertexValue - lerpUuidFactor, textureVertexValue2 - lerpUuidFactor, lerpUuidFactor * 2.0F, lerpUuidFactor * 2.0F, lerpUuidFactor, hotbarMatrixBound, textureVertexValue5);
         } else {
            float textureVertexFactor = MatrixPeekUtilsRender.getAmount();
            int max2 = Math.max(1, Math.min(32, Math.round(hotbarMatrixBound / textureVertexFactor)));
            Identifier identifier = getIdentifier(max2);
            if (identifier != null) {
               float textureVertexValue6 = max2 * textureVertexFactor / 2.0F;
               float textureVertexDivisor = lerpUuidFactor - textureVertexValue6;
               float textureVertexFactor3 = textureVertexDivisor - textureVertexValue6 - textureVertexFactor;
               float textureVertexFactor4 = textureVertexDivisor + textureVertexValue6 + textureVertexFactor;
               if (!(textureVertexFactor3 <= 0.0F)) {
                  int ceil5 = (int)Math.ceil(max2 / 2.0F + 0.5F);
                  float textureVertexDivisor2 = ceil5 * 2.0F + 2.0F;
                  float textureVertexFactor6 = ceil5 * textureVertexFactor;
                  float textureVertexValue7 = 0.5F / textureVertexDivisor2;
                  float textureVertexValue8 = (ceil5 + 0.5F) / textureVertexDivisor2;
                  float textureVertexValue9 = (ceil5 + 1.5F) / textureVertexDivisor2;
                  float textureVertexValue10 = (textureVertexDivisor2 - 0.5F) / textureVertexDivisor2;
                  float textureVertexValue11 = textureVertexLimit * (float) (Math.PI * 2);
                  float textureVertexValue12 = textureVertexValue6 / textureVertexDivisor;
                  float angle = (float) (-Math.PI / 2) + textureVertexValue12;
                  float textureVertexLimit2 = (float) (-Math.PI / 2) + textureVertexValue11 - textureVertexValue12;
                  if (textureVertexLimit2 < angle) {
                     angle = (float) (-Math.PI / 2) + textureVertexValue11 / 2.0F;
                     textureVertexLimit2 = angle;
                  }

                  int textureVertexLimit3 = textureVertexLimit2 > angle ? Math.max(1, Math.min(512, (int)Math.ceil((textureVertexLimit2 - angle) * textureVertexFactor4 / (3.0F * textureVertexFactor)))) : 0;
                  float textureVertexValue13 = (textureVertexValue5 >> 16 & 0xFF) / 255.0F;
                  float textureVertexValue14 = (textureVertexValue5 >> 8 & 0xFF) / 255.0F;
                  float textureVertexValue15 = (textureVertexValue5 & 0xFF) / 255.0F;
                  float textureVertexValue16 = (textureVertexValue5 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount;
                  Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
                  BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                  float cos = (float)Math.cos(angle);
                  float sin = (float)Math.sin(angle);
                  float textureVertexValue17 = sin * textureVertexFactor6;
                  float textureVertexValue18 = -cos * textureVertexFactor6;
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor3 + textureVertexValue17, textureVertexValue2 + sin * textureVertexFactor3 + textureVertexValue18, 0.0F)
                     .texture(textureVertexValue7, 1.0F)
                     .color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor3, textureVertexValue2 + sin * textureVertexFactor3, 0.0F).texture(textureVertexValue8, 1.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor4, textureVertexValue2 + sin * textureVertexFactor4, 0.0F).texture(textureVertexValue8, 0.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor4 + textureVertexValue17, textureVertexValue2 + sin * textureVertexFactor4 + textureVertexValue18, 0.0F)
                     .texture(textureVertexValue7, 0.0F)
                     .color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);

                  for (int i = 1; i <= textureVertexLimit3; i++) {
                     float angle2 = angle + (textureVertexLimit2 - angle) * i / textureVertexLimit3;
                     float cos2 = (float)Math.cos(angle2);
                     float sin2 = (float)Math.sin(angle2);
                     begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor3, textureVertexValue2 + sin * textureVertexFactor3, 0.0F).texture(0.5F, 1.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                     begin.vertex(positionMatrix, textureVertexValue + cos2 * textureVertexFactor3, textureVertexValue2 + sin2 * textureVertexFactor3, 0.0F).texture(0.5F, 1.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                     begin.vertex(positionMatrix, textureVertexValue + cos2 * textureVertexFactor4, textureVertexValue2 + sin2 * textureVertexFactor4, 0.0F).texture(0.5F, 0.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                     begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor4, textureVertexValue2 + sin * textureVertexFactor4, 0.0F).texture(0.5F, 0.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                     cos = cos2;
                     sin = sin2;
                  }

                  float textureVertexValue19 = -sin * textureVertexFactor6;
                  float textureVertexValue20 = cos * textureVertexFactor6;
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor3, textureVertexValue2 + sin * textureVertexFactor3, 0.0F).texture(textureVertexValue9, 1.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor3 + textureVertexValue19, textureVertexValue2 + sin * textureVertexFactor3 + textureVertexValue20, 0.0F)
                     .texture(textureVertexValue10, 1.0F)
                     .color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor4 + textureVertexValue19, textureVertexValue2 + sin * textureVertexFactor4 + textureVertexValue20, 0.0F)
                     .texture(textureVertexValue10, 0.0F)
                     .color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  begin.vertex(positionMatrix, textureVertexValue + cos * textureVertexFactor4, textureVertexValue2 + sin * textureVertexFactor4, 0.0F).texture(textureVertexValue9, 0.0F).color(textureVertexValue13, textureVertexValue14, textureVertexValue15, textureVertexValue16);
                  CorzRoundedUtils.handleTextureTextured(begin.end(), identifier);
               }
            }
         }
      }
   }

   private static float getAmount(float nativeHudValue, float styleHudValue, float nativeHudDivisor) {
      if (nativeHudDivisor <= 0.0F) {
         return 1.0F;
      } else {
         float nativeHudFactor = getMax((nativeHudValue - styleHudValue) / nativeHudDivisor);
         return nativeHudFactor * nativeHudFactor * (3.0F - 2.0F * nativeHudFactor);
      }
   }

   private NativeHud() {
   }

   public static float getAmount(float nativeHudValue, float nativeHudFactor) {
      float nativeHudFactor2 = getMax(nativeHudValue) - 1.0F;
      return 1.0F + (nativeHudFactor + 1.0F) * nativeHudFactor2 * nativeHudFactor2 * nativeHudFactor2 + nativeHudFactor * nativeHudFactor2 * nativeHudFactor2;
   }

   public static void handleColor(float colorValue, float glfwLimit, float panelOverlayBound, float regionmapRegionTotal, Identifier id, String colorText, float colorValue5) {
      handleForIntFloat(COLOR_PANEL_ID, colorValue, glfwLimit, panelOverlayBound, regionmapRegionTotal, colorValue5, 1.0F);
      float colorValue6 = 3.0F * colorValue5;
      float crossLimit = 12.0F * colorValue5;
      handleColor(colorValue + colorValue6, glfwLimit + colorValue6, crossLimit, 4.0F * colorValue5, 8.0F * colorValue5, id, null, 1.0F);
      handlePushScale(colorText, colorValue + colorValue6 + crossLimit + 5.0F * colorValue5, getAmount2(glfwLimit, 18.0F * colorValue5, 0.8F * colorValue5), 0.8F * colorValue5, OFF_WHITE);
   }

   public static float getAmount(float nativeHudValue) {
      float nativeHudDivisor = MatrixPeekUtilsRender.getAmount();
      return (float)Math.ceil(nativeHudValue / nativeHudDivisor - 0.001F) * nativeHudDivisor;
   }

   public static void handle(float nativeHudValue, float matrixStackValue, Identifier id, float panelOverlayLimit) {
      handleColor(nativeHudValue, matrixStackValue, 12.0F, 4.0F, 8.0F, id, null, panelOverlayLimit);
   }

   public static void handlePeekPosition(float peekPositionValue, float matrixStackValue, float peekPositionValue3, float pilotStatusBound, float peekPositionValue5, int peekPositionValue6, float peekPositionValue7, boolean peekPositionFlag) {
      if (!(peekPositionValue3 <= 0.0F) && !(pilotStatusBound <= 0.0F) && peekPositionValue6 >>> 24 != 0) {
         float peekPositionDivisor = MatrixPeekUtilsRender.getAmount();
         float argbOffset = peekPositionValue7 + 2.0F;
         CorzRoundedUtils.handle(
            MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
            peekPositionValue - argbOffset,
            matrixStackValue - argbOffset,
            peekPositionValue + peekPositionValue3 + argbOffset,
            matrixStackValue + pilotStatusBound + argbOffset,
            peekPositionValue / peekPositionDivisor,
            matrixStackValue / peekPositionDivisor,
            peekPositionValue3 / peekPositionDivisor,
            pilotStatusBound / peekPositionDivisor,
            peekPositionValue5 / peekPositionDivisor,
            (peekPositionValue6 >> 16 & 0xFF) / 255.0F,
            (peekPositionValue6 >> 8 & 0xFF) / 255.0F,
            (peekPositionValue6 & 0xFF) / 255.0F,
            (peekPositionValue6 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount,
            peekPositionValue7 / peekPositionDivisor,
            1.0F,
            peekPositionFlag
         );
      }
   }

   public static void handle(String nativeHudText, float nativeHudValue, float nativeHudValue2, float maxRadarSize, Color color) {
      handlePushScale(nativeHudText, nativeHudValue, getAmount2(nativeHudValue2, maxRadarSize, 0.9F), 0.9F, color);
   }

   private static void handlePanelOverlay(int panelOverlayValue, float peekPositionValue, float matrixStackValue, float panelOverlayBound, float hashHuntBound, float wrapDegreesBound, float panelOverlayValue4, float panelOverlayFactor) {
      if (!(panelOverlayBound <= 0.0F) && !(hashHuntBound <= 0.0F) && !(panelOverlayFactor <= 0.002F)) {
         boolean modernFlag = CardMatrixUtils.isModernFlag();
         float lerpUuidFactor = Math.min(wrapDegreesBound, Math.min(panelOverlayBound, hashHuntBound) / 2.0F);
         if (!modernFlag) {
            float min6 = Math.min(3.0F * panelOverlayValue4, Math.min(panelOverlayBound, hashHuntBound) / 4.0F);
            handlePeekPosition(
               peekPositionValue + min6,
               matrixStackValue + min6 + 3.0F * panelOverlayValue4,
               panelOverlayBound - 2.0F * min6,
               hashHuntBound - 2.0F * min6,
               Math.max(0.0F, lerpUuidFactor - min6),
               getValue(1291845632, panelOverlayFactor),
               7.0F * panelOverlayValue4 + min6,
               true
            );
         }

         handlePeekPosition(peekPositionValue, matrixStackValue, panelOverlayBound, hashHuntBound, lerpUuidFactor, 0.0F, getValue(modernFlag ? -1945564402 : -318174450, panelOverlayFactor));
         if (panelOverlayFactor * CorzRoundedUtils.panelOverlayAmount >= 0.02F) {
            TwoDidUtils.handlePeekAmounts(panelOverlayValue, peekPositionValue, matrixStackValue, panelOverlayBound, hashHuntBound, lerpUuidFactor, panelOverlayValue4, panelOverlayFactor);
         }

         handlePeekPosition(peekPositionValue, matrixStackValue, panelOverlayBound, hashHuntBound, lerpUuidFactor, MatrixPeekUtilsRender.getAmount(), getValue(385875967, panelOverlayFactor));
      }
   }

   public static void handleForFieldFloat(NativeHud.Field field, float nativeHudValue, float nativeHudValue2, float peekPositionFactor, Color color) {
      handleAmount(field, nativeHudValue, getAmount2(nativeHudValue2, peekPositionFactor, 0.9F), 0.9F, color);
   }

   public static void renderMatrixMatrices(DrawContext context, String text, float matrixMatricesValue, float matrixMatricesValue2, float matrixMatricesValue3) {
      if (text != null && !text.isEmpty() && !(CorzRoundedUtils.getPanelOverlay() < 0.55F)) {
         int panelOverlay = CorzRoundedUtils.getPanelOverlay(-1);
         TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();
         matrices.translate(matrixMatricesValue, matrixMatricesValue2);
         matrices.scale(matrixMatricesValue3 / 16.0F, matrixMatricesValue3 / 16.0F);
         context.drawText(textRenderer, text, 17 - textRenderer.getWidth(text), 9, panelOverlay, true);
         matrices.popMatrix();
      }
   }

   public static void handleColor(String colorText, float colorValue, float colorValue2, float peekPositionFactor) {
      handlePushScale(colorText, colorValue, getAmount2(colorValue2, peekPositionFactor, 0.8F), 0.8F, GRAY);
   }

   public static void handle(float nativeHudValue, float nativeHudValue2, float nativeHudFactor, float nativeHudBound, float nativeHudValue3, int cardMatrixValue) {
      if (!(nativeHudFactor <= 0.0F) && !(nativeHudBound <= 0.0F)) {
         nativeHudValue2 = getAmount4(nativeHudValue2);
         handlePeekPosition(nativeHudValue, nativeHudValue2, nativeHudFactor, nativeHudBound, nativeHudBound / 2.0F, 0.0F, 318767103);
         float nativeHudLimit = getAmount4(nativeHudFactor * getMax(nativeHudValue3));
         if (nativeHudLimit > 0.0F) {
            handlePositionBegin(nativeHudValue, nativeHudValue2, Math.max(nativeHudLimit, Math.min(nativeHudFactor, nativeHudBound)), nativeHudBound, cardMatrixValue);
         }
      }
   }

   public static void handle(float nativeHudValue, float matrixStackValue, float nativeHudBound, float hashHuntBound, float cardMatrixFactor) {
      updateWhiteFlags();
      float lerpUuidFactor = Math.min(4.5F, Math.min(nativeHudBound, hashHuntBound) / 2.0F);
      handlePeekPosition(nativeHudValue, matrixStackValue, nativeHudBound, hashHuntBound, lerpUuidFactor, 0.0F, CardMatrixUtils.getValue(201326591, whiteAlpha22Color, cardMatrixFactor));
      handlePeekPosition(nativeHudValue, matrixStackValue, nativeHudBound, hashHuntBound, lerpUuidFactor, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValue(318767103, whiteAlpha40Color, cardMatrixFactor));
   }

   private static void handlePositionBegin(float positionBeginValue, float matrixStackValue, float crossLimit, float nativeHudBound, int cardMatrixValue) {
      if (cardMatrixValue >>> 24 != 0) {
         float positionBeginFactor = MatrixPeekUtilsRender.getAmount();
         int max2 = Math.max(1, Math.min(32, Math.round(nativeHudBound / positionBeginFactor)));
         Identifier identifier = getIdentifier(max2);
         if (identifier == null) {
            handlePeekPosition(positionBeginValue, matrixStackValue, crossLimit, nativeHudBound, nativeHudBound / 2.0F, 0.0F, cardMatrixValue);
         } else {
            int positionBeginValue6 = CardMatrixUtils.getValue(cardMatrixValue, cardMatrixValue | 16777215, 0.15F);
            float positionBeginValue7 = max2 * positionBeginFactor / 2.0F;
            float positionBeginValue8 = matrixStackValue + nativeHudBound / 2.0F;
            float positionBeginValue9 = positionBeginValue8 - positionBeginValue7 - positionBeginFactor;
            float positionBeginValue10 = positionBeginValue8 + positionBeginValue7 + positionBeginFactor;
            int ceil3 = (int)Math.ceil(max2 / 2.0F + 0.5F);
            float positionBeginDivisor = ceil3 * 2.0F + 2.0F;
            float positionBeginValue11 = ceil3 * positionBeginFactor;
            float positionBeginBound = positionBeginValue + positionBeginValue7;
            float positionBeginLimit = Math.max(positionBeginBound, positionBeginValue + crossLimit - positionBeginValue7);
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            handleVertexTexture(begin, positionMatrix, positionBeginBound - positionBeginValue11, positionBeginBound, positionBeginValue9, positionBeginValue10, 0.5F / positionBeginDivisor, (ceil3 + 0.5F) / positionBeginDivisor, positionBeginValue6, positionBeginValue6);
            if (positionBeginLimit > positionBeginBound) {
               handleVertexTexture(begin, positionMatrix, positionBeginBound, positionBeginLimit, positionBeginValue9, positionBeginValue10, 0.5F, 0.5F, positionBeginValue6, cardMatrixValue);
            }

            handleVertexTexture(begin, positionMatrix, positionBeginLimit, positionBeginLimit + positionBeginValue11, positionBeginValue9, positionBeginValue10, (ceil3 + 1.5F) / positionBeginDivisor, (positionBeginDivisor - 0.5F) / positionBeginDivisor, cardMatrixValue, cardMatrixValue);
            CorzRoundedUtils.handleTextureTextured(begin.end(), identifier);
         }
      }
   }

   public static int getFlags(float nativeHudValue) {
      updateWhiteFlags();
      int index = Math.round(getMax(nativeHudValue) * 64.0F);
      if (!whiteFlags[index]) {
         nativeHudTable[index] = MatrixAmountUtils.getValueForIntFloat(-1750963, 0xFF000000 | whiteBaseRgb, MatrixAmountUtils.getAmountForFloat((index / 64.0F - 0.2F) / 0.3F));
         whiteFlags[index] = true;
      }

      return nativeHudTable[index];
   }

   public static void renderForDrawContextFloat(DrawContext context, float nativeHudValue, float nativeHudValue2, float nativeHudValue3, int nativeHudValue4) {
      handlePeekPosition(getAmount4(nativeHudValue), getAmount4(nativeHudValue2), MatrixPeekUtilsRender.getAmount(), getAmount4(nativeHudValue3), 0.0F, 0.0F, nativeHudValue4);
   }

   private static void updateWhiteFlags() {
      int value = MatrixEspUtils.releaseRightValue & 16777215;
      if (!whiteActive || value != whiteBaseRgb) {
         whiteActive = true;
         whiteBaseRgb = value;
         int whiteValue = 0xFF000000 | value;
         white = new Color(whiteValue, true);
         whiteColor = new Color(CardMatrixUtils.getValue(whiteValue, -1, 0.2F), true);
         whiteAlpha15Color = Math.round(38.25F) << 24 | value;
         nativeHudValue = Math.round(56.1F) << 24 | value;
         whiteAlpha22Color = Math.round(56.1F) << 24 | value;
         whiteAlpha40Color = Math.round(102.0F) << 24 | value;
         Arrays.fill(whiteFlags, false);
      }
   }

   public static void renderPopMatrices(DrawContext context, boolean matricesFlag) {
      if (matricesFlag) {
         context.getMatrices().popMatrix();
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         MatrixStack matrixStack2 = MatrixPeekUtilsRender.getMatrixStack();
         if (matrixStack2 != matrixStack) {
            matrixStack2.pop();
         }

         matrixStack.pop();
      }
   }

   public static float getAmountForFloat(float nativeHudValue) {
      return nativeHudValue < 0.3F ? getAmount5(nativeHudValue / 0.3F) : 1.0F - getAmount((nativeHudValue - 0.3F) / 0.7F, 2.0F);
   }

   public static void handleAmountForFieldFloat(NativeHud.Field field, float nativeHudValue, float f13, float x2, Color color) {
      handleMaxText(field, nativeHudValue - field.fieldAmount / 2.0F, nativeHudValue - field.textWidth / 2.0F, f13, x2, color);
   }

   public static float getAmountFloat(float nativeHudValue) {
      return 18.0F * nativeHudValue;
   }

   public static void handleColor(float colorValue, float randomValue, float crossLimit, float lerpUuidFactor, Identifier id, float peekPositionFactor) {
      handle(colorValue, randomValue, crossLimit, crossLimit, lerpUuidFactor, 1.0F);
      if (id != null) {
         float colorValue6 = (crossLimit - peekPositionFactor) / 2.0F;
         MatrixPeekUtilsIdentifier.handle(colorValue + colorValue6, randomValue + colorValue6, peekPositionFactor, peekPositionFactor, id, GRAY, MatrixPeekUtilsRender.getMatrixStack());
      }
   }

   public static void handleForFloatIdentifier(float nativeHudValue, float matrixStackValue, Identifier id, float panelOverlayLimit) {
      handleColor(nativeHudValue, matrixStackValue, 14.0F, 4.5F, 8.0F, id, null, panelOverlayLimit);
   }

   public static boolean canPeekBody(SkinTextures skinTextures, float peekBodyValue, float randomValue, float crossLimit, float corzLicenseLimit) {
      if (skinTextures != null && !(crossLimit <= 0.0F)) {
         Identifier identifier;
         try {
            identifier = skinTextures.body().texturePath();
         } catch (Throwable e) {
            return false;
         }

         if (identifier == null) {
            return false;
         } else {
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            CorzRoundedUtils.handleTextureRounded(positionMatrix, peekBodyValue, randomValue, peekBodyValue + crossLimit, randomValue + crossLimit, 0.125F, 0.125F, 0.25F, 0.25F, identifier, corzLicenseLimit);
            CorzRoundedUtils.handleTextureRounded(positionMatrix, peekBodyValue, randomValue, peekBodyValue + crossLimit, randomValue + crossLimit, 0.625F, 0.125F, 0.75F, 0.25F, identifier, corzLicenseLimit);
            return true;
         }
      } else {
         return false;
      }
   }

   public static void handle(float nativeHudValue, float matrixStackValue, Identifier id, Color color, float panelOverlayLimit) {
      handleColor(nativeHudValue, matrixStackValue, 12.0F, 4.0F, 8.0F, id, color, panelOverlayLimit);
   }

   public static float getAmount2(float nativeHudValue, float maxRadarSize, float nativeHudFactor) {
      float nativeHudFactor2 = getFontRenderer().getAbcdefghijklmnopqrstuvwxyzBrown(0, 100) - 50.0F;
      return getAmount4(nativeHudValue + maxRadarSize / 2.0F + nativeHudFactor2 * nativeHudFactor);
   }

   public static float getAmount(String nativeHudText, float nativeHudValue) {
      return 27.0F * nativeHudValue + getAmount3(nativeHudText, 0.8F * nativeHudValue);
   }

   public static float getAmount2(float nativeHudValue, float nativeHudLimit) {
      return 25.5F + nativeHudValue + (nativeHudLimit > 0.0F ? 10.0F + nativeHudLimit : 0.0F) + 7.0F;
   }

   public static float getAmount4(float nativeHudValue) {
      float nativeHudDivisor = MatrixPeekUtilsRender.getAmount();
      return Math.round(nativeHudValue / nativeHudDivisor) * nativeHudDivisor;
   }

   private static void handleColor(float colorValue, float matrixStackValue, float crossLimit, float lerpUuidFactor, float peekPositionFactor, Identifier id, Color color, float panelOverlayLimit) {
      if (!(panelOverlayLimit <= 0.002F)) {
         handle(colorValue, matrixStackValue, crossLimit, crossLimit, lerpUuidFactor, panelOverlayLimit);
         if (id != null) {
            float colorValue7 = (crossLimit - peekPositionFactor) / 2.0F;
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

            try {
               MatrixPeekUtilsIdentifier.handle(colorValue + colorValue7, matrixStackValue + colorValue7, peekPositionFactor, peekPositionFactor, id, color != null ? color : WHITE, MatrixPeekUtilsRender.getMatrixStack());
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   public static float getAmount5(float nativeHudValue) {
      float nativeHudFactor = 1.0F - getMax(nativeHudValue);
      return 1.0F - nativeHudFactor * nativeHudFactor * nativeHudFactor;
   }

   public static void renderGuiMatrix(DrawContext context, Identifier id, float guiMatrixValue, float guiMatrixValue2, float guiMatrixValue3) {
      if (id != null) {
         int panelOverlay = CorzRoundedUtils.getPanelOverlay(-1);
         if (panelOverlay >>> 24 >= 8) {
            Matrix3x2fStack matrices = context.getMatrices();
            matrices.pushMatrix();
            matrices.translate(guiMatrixValue, guiMatrixValue2);
            matrices.scale(guiMatrixValue3 / 18.0F, guiMatrixValue3 / 18.0F);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, id, 0, 0, 18, 18, panelOverlay);
            matrices.popMatrix();
         }
      }
   }

   public static void handleTextureColor(float[] textureColorValues, float[] textureColorValues2, int textureColorLimit, float textureColorLimit2, float textureColorBound, int textureColorValue, float textureColorFactor, float styleHudValue, float nativeHudDivisor) {
      if (textureColorLimit >= 2 && !(textureColorBound <= textureColorLimit2) && textureColorValue >>> 24 != 0) {
         Identifier identifier = getIdentifier(2);
         if (identifier != null) {
            float textureColorFactor2 = CorzRoundedUtils.panelOverlayAmount * (textureColorValue >>> 24 & 0xFF) / 255.0F;
            float textureColorValue4 = (textureColorValue >> 16 & 0xFF) / 255.0F;
            float textureColorValue5 = (textureColorValue >> 8 & 0xFF) / 255.0F;
            float textureColorValue6 = (textureColorValue & 0xFF) / 255.0F;
            float textureColorDivisor = textureColorBound - textureColorLimit2;
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            for (int i = 0; i + 1 < textureColorLimit; i++) {
               float textureColorValue7 = textureColorValues[i];
               float textureColorValue8 = textureColorValues[i + 1];
               float minBase = Math.min(textureColorBound, textureColorValues2[i]);
               float min2 = Math.min(textureColorBound, textureColorValues2[i + 1]);
               float textureColorValue9 = textureColorFactor2 * textureColorFactor * getMax((textureColorBound - minBase) / textureColorDivisor) * getAmount(textureColorValue7, styleHudValue, nativeHudDivisor);
               float textureColorValue10 = textureColorFactor2 * textureColorFactor * getMax((textureColorBound - min2) / textureColorDivisor) * getAmount(textureColorValue8, styleHudValue, nativeHudDivisor);
               begin.vertex(positionMatrix, textureColorValue7, textureColorBound, 0.0F).texture(0.5F, 0.5F).color(textureColorValue4, textureColorValue5, textureColorValue6, 0.0F);
               begin.vertex(positionMatrix, textureColorValue8, textureColorBound, 0.0F).texture(0.5F, 0.5F).color(textureColorValue4, textureColorValue5, textureColorValue6, 0.0F);
               begin.vertex(positionMatrix, textureColorValue8, min2, 0.0F).texture(0.5F, 0.5F).color(textureColorValue4, textureColorValue5, textureColorValue6, textureColorValue10);
               begin.vertex(positionMatrix, textureColorValue7, minBase, 0.0F).texture(0.5F, 0.5F).color(textureColorValue4, textureColorValue5, textureColorValue6, textureColorValue9);
            }

            CorzRoundedUtils.handleTextureTextured(begin.end(), identifier);
         }
      }
   }

   public static float getAmountForStringFloat(String nativeHudText, float nativeHudFactor) {
      if (nativeHudText != null && !nativeHudText.isEmpty()) {
         VertexTextureFont fontRenderer = getFontRenderer();
         float nativeHudValue = fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("0") * nativeHudFactor;
         float nativeHudTotal = 0.0F;

         for (int i = 0; i < nativeHudText.length(); i++) {
            char charAt = nativeHudText.charAt(i);
            nativeHudTotal += charAt >= '0' && charAt <= '9' ? nativeHudValue : fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(String.valueOf(charAt)) * nativeHudFactor;
         }

         return nativeHudTotal;
      } else {
         return 0.0F;
      }
   }

   public static void handle(String nativeHudText, float nativeHudValue, float nativeHudValue2, float maxRadarSize, float nativeHudFactor, Color color) {
      handlePushScale(nativeHudText, nativeHudValue, getAmount2(nativeHudValue2, maxRadarSize, nativeHudFactor), nativeHudFactor, color);
   }

   public static void handleTextureColor(float[] textureColorValues, float[] textureColorValues2, int textureColorLimit, float textureColorValue, int releaseRight, float styleHudValue, float nativeHudDivisor) {
      if (textureColorLimit >= 2 && !(textureColorValue <= 0.0F) && releaseRight >>> 24 != 0) {
         float textureColorDivisor = MatrixPeekUtilsRender.getAmount();
         int textureColorFactor = Math.max(1, Math.min(32, Math.round(textureColorValue / textureColorDivisor)));
         Identifier identifier = getIdentifier(textureColorFactor);
         if (identifier != null) {
            float textureColorValue5 = textureColorFactor * textureColorDivisor / 2.0F + textureColorDivisor;
            float textureColorFactor2 = CorzRoundedUtils.panelOverlayAmount * (releaseRight >>> 24 & 0xFF) / 255.0F;
            float textureColorValue6 = (releaseRight >> 16 & 0xFF) / 255.0F;
            float textureColorValue7 = (releaseRight >> 8 & 0xFF) / 255.0F;
            float textureColorValue8 = (releaseRight & 0xFF) / 255.0F;
            Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
            BufferBuilder begin = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            float textureColorValue9 = 0.0F;
            float textureColorValue10 = 0.0F;
            float textureColorValue11 = 0.0F;

            for (int i = 0; i < textureColorLimit; i++) {
               float textureColorFactor3 = i > 0 ? textureColorValues[i] - textureColorValues[i - 1] : 0.0F;
               float textureColorFactor4 = i > 0 ? textureColorValues2[i] - textureColorValues2[i - 1] : 0.0F;
               float textureColorFactor5 = i + 1 < textureColorLimit ? textureColorValues[i + 1] - textureColorValues[i] : 0.0F;
               float textureColorFactor6 = i + 1 < textureColorLimit ? textureColorValues2[i + 1] - textureColorValues2[i] : 0.0F;
               float distance = (float)Math.sqrt(textureColorFactor3 * textureColorFactor3 + textureColorFactor4 * textureColorFactor4);
               float sqrt2 = (float)Math.sqrt(textureColorFactor5 * textureColorFactor5 + textureColorFactor6 * textureColorFactor6);
               float textureColorFactor7 = distance > 1.0E-5F ? -textureColorFactor4 / distance : 0.0F;
               float textureColorFactor8 = distance > 1.0E-5F ? textureColorFactor3 / distance : 0.0F;
               float textureColorFactor9 = sqrt2 > 1.0E-5F ? -textureColorFactor6 / sqrt2 : 0.0F;
               float textureColorFactor10 = sqrt2 > 1.0E-5F ? textureColorFactor5 / sqrt2 : 0.0F;
               float textureColorFactor11 = textureColorFactor7 + textureColorFactor9;
               float textureColorFactor12 = textureColorFactor8 + textureColorFactor10;
               float sqrt3 = (float)Math.sqrt(textureColorFactor11 * textureColorFactor11 + textureColorFactor12 * textureColorFactor12);
               if (sqrt3 < 1.0E-5F) {
                  textureColorFactor11 = distance > 1.0E-5F ? textureColorFactor7 : textureColorFactor9;
                  textureColorFactor12 = distance > 1.0E-5F ? textureColorFactor8 : textureColorFactor10;
                  sqrt3 = 1.0F;
               }

               float textureColorFactor13 = textureColorFactor11 / sqrt3;
               float textureColorFactor14 = textureColorFactor12 / sqrt3;
               float textureColorBound = distance > 1.0E-5F ? textureColorFactor7 * textureColorFactor13 + textureColorFactor8 * textureColorFactor14 : textureColorFactor9 * textureColorFactor13 + textureColorFactor10 * textureColorFactor14;
               float textureColorFactor15 = textureColorValue5 / Math.max(0.5F, textureColorBound);
               float textureColorValue12 = textureColorFactor2 * getAmount(textureColorValues[i], styleHudValue, nativeHudDivisor);
               float textureColorValue13 = textureColorFactor13 * textureColorFactor15;
               float textureColorValue14 = textureColorFactor14 * textureColorFactor15;
               if (i > 0) {
                  begin.vertex(positionMatrix, textureColorValues[i - 1] - textureColorValue10, textureColorValues2[i - 1] - textureColorValue11, 0.0F).texture(0.5F, 1.0F).color(textureColorValue6, textureColorValue7, textureColorValue8, textureColorValue9);
                  begin.vertex(positionMatrix, textureColorValues[i] - textureColorValue13, textureColorValues2[i] - textureColorValue14, 0.0F).texture(0.5F, 1.0F).color(textureColorValue6, textureColorValue7, textureColorValue8, textureColorValue12);
                  begin.vertex(positionMatrix, textureColorValues[i] + textureColorValue13, textureColorValues2[i] + textureColorValue14, 0.0F).texture(0.5F, 0.0F).color(textureColorValue6, textureColorValue7, textureColorValue8, textureColorValue12);
                  begin.vertex(positionMatrix, textureColorValues[i - 1] + textureColorValue10, textureColorValues2[i - 1] + textureColorValue11, 0.0F).texture(0.5F, 0.0F).color(textureColorValue6, textureColorValue7, textureColorValue8, textureColorValue9);
               }

               textureColorValue10 = textureColorValue13;
               textureColorValue11 = textureColorValue14;
               textureColorValue9 = textureColorValue12;
            }

            CorzRoundedUtils.handleTextureTextured(begin.end(), identifier);
         }
      }
   }

   public static void handleWhite(float whiteValue, float whiteValue2, float maxRadarSize, String whiteText, float whiteValue4, float crossLimit) {
      updateWhiteFlags();
      float peekPositionValue = whiteValue - crossLimit;
      float matrixStackValue = getAmount4(whiteValue2 + (maxRadarSize - 11.0F) / 2.0F);
      handlePeekPosition(peekPositionValue, matrixStackValue, crossLimit, 11.0F, 5.5F, 0.0F, whiteAlpha15Color);
      handlePeekPosition(peekPositionValue, matrixStackValue, crossLimit, 11.0F, 5.5F, MatrixPeekUtilsRender.getAmount(), nativeHudValue);
      handlePushScale(whiteText, peekPositionValue + (crossLimit - whiteValue4) / 2.0F, getAmount2(matrixStackValue, 11.0F, 0.72F), 0.72F, whiteColor);
   }

   public static void handleTextsAmounts(NativeHud.Caps caps, float nativeHudValue, float nativeHudValue2, float x2, Color color) {
      if (caps != null && caps.capsTexts.length != 0) {
         VertexTextureFont fontRenderer = getFontRenderer();
         MatrixStack matrixStack = CorzClient.fontManager.getMatrixStack();
         matrixStack.push();
         matrixStack.scale(x2, x2, 1.0F);

         for (int i = 0; i < caps.capsTexts.length; i++) {
            caps.capsTable[i] = (nativeHudValue + caps.table[i]) / x2;
         }

         fontRenderer.handleFlag(caps.capsTexts, caps.capsTable, caps.capsTexts.length, nativeHudValue2 / x2, color);
         matrixStack.pop();
      }
   }

   public static void handle(int nativeHudValue, float peekPositionValue, float matrixStackValue, float panelOverlayBound, float hashHuntBound, float panelOverlayFactor) {
      handlePanelOverlay(nativeHudValue, peekPositionValue, matrixStackValue, panelOverlayBound, hashHuntBound, 8.0F, 1.0F, panelOverlayFactor);
   }

   public static float getAmount3(String nativeHudText, float nativeHudFactor) {
      return nativeHudText != null && !nativeHudText.isEmpty() ? getFontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont(nativeHudText) * nativeHudFactor : 0.0F;
   }

   public static void renderForDrawContextFloatInt(DrawContext context, float nativeHudValue, float nativeHudValue2, float nativeHudValue3, int nativeHudValue4) {
      handlePeekPosition(getAmount4(nativeHudValue), getAmount4(nativeHudValue2), getAmount4(nativeHudValue3), MatrixPeekUtilsRender.getAmount(), 0.0F, 0.0F, nativeHudValue4);
   }

   public static final class Caps {
      private final float[] capsTable;
      private final float[] table;
      private final String[] capsTexts;

      public Caps(String capsText, float capsValue, float capsValue2) {
         int capsSize = capsText == null ? 0 : capsText.length();
         this.capsTexts = new String[capsSize];
         this.table = new float[capsSize];
         this.capsTable = new float[capsSize];
         float capsTotal = 0.0F;

         for (int i = 0; i < capsSize; i++) {
            this.capsTexts[i] = String.valueOf(capsText.charAt(i));
            this.table[i] = capsTotal;
            capsTotal += NativeHud.getAmount3(this.capsTexts[i], capsValue) + capsValue2;
         }
      }
   }

   public static final class Field {
      private long lastNanoTime;
      public String fieldText;
      private long fieldValue;
      private long lastMeasuredValue;
      public String bloodhoundBestText = "";
      public float checkedRocketsAmount;
      public float textWidth;
      public float fieldAmount;

      public boolean canMax(long max) {
         if (max == this.lastMeasuredValue) {
            return false;
         } else {
            this.lastMeasuredValue = max;
            return true;
         }
      }

      public void handleAmountTime(String fieldText, float fieldValue, boolean fieldFlag, boolean smoothFlag) {
         long nanoTime = System.nanoTime();
         boolean fieldFlag3 = this.fieldValue != 0L && (float)(nanoTime - this.fieldValue) < 1.2E8F;
         boolean fieldFlag4 = this.lastNanoTime != 0L && nanoTime - this.lastNanoTime < 300000000L;
         this.lastNanoTime = nanoTime;
         this.fieldText = this.bloodhoundBestText;
         this.fieldAmount = this.textWidth;
         this.bloodhoundBestText = fieldText == null ? "" : fieldText;
         this.textWidth = NativeHud.getAmount3(this.bloodhoundBestText, fieldValue);
         this.checkedRocketsAmount = (float)Math.ceil(fieldFlag ? NativeHud.getAmountForStringFloat(this.bloodhoundBestText, fieldValue) : this.textWidth);
         if (smoothFlag && !fieldFlag3 && !fieldFlag4 && !this.fieldText.isEmpty() && !this.fieldText.equals(this.bloodhoundBestText)) {
            this.fieldValue = nanoTime;
         } else {
            this.fieldValue = 0L;
            this.fieldText = this.bloodhoundBestText;
            this.fieldAmount = this.textWidth;
         }
      }

      public boolean isEmpty() {
         return this.bloodhoundBestText.isEmpty();
      }

      public Field() {
         this.fieldText = "";
         this.lastMeasuredValue = Long.MIN_VALUE;
      }

      public float getTime() {
         if (this.fieldValue == 0L) {
            return 1.0F;
         } else {
            float fieldLimit = (float)(System.nanoTime() - this.fieldValue) / 1.2E8F;
            if (fieldLimit >= 1.0F) {
               this.fieldValue = 0L;
               return 1.0F;
            } else {
               return fieldLimit;
            }
         }
      }
   }

   public static final class ListRow {
      public boolean rowActive;
      public float badgeWidth;
      public float rowAmount;
      public float targetY;
      public final float fullHeight;
      public int lastShownValue;
      public float slideOffsetY;
      public final float textWidth;
      public float targetVisibility;
      public String rowText = "";
      public float keybindsKeyboardAmount;
      public boolean keybindsKeyboardActive;
      public final String keybindsKeyboardText;
      public float textAlpha;

      public void updateAmountFlag() {
         this.keybindsKeyboardActive = true;
         this.targetVisibility = 0.0F;
      }

      public void updateAmountFlagKeybindsKeyboardActive() {
         this.keybindsKeyboardActive = false;
         this.targetVisibility = 1.0F;
      }

      public float getAmount() {
         return this.fullHeight * this.rowAmount;
      }

      public ListRow(String text, float amount, float amount2) {
         this.targetVisibility = 1.0F;
         this.lastShownValue = Integer.MIN_VALUE;
         this.keybindsKeyboardText = text;
         this.textWidth = amount;
         this.fullHeight = amount2;
         this.keybindsKeyboardAmount = (float)Math.ceil(amount);
      }

      public void handleAmountFlag(float rowValue) {
         if (this.rowActive) {
            this.slideOffsetY = this.slideOffsetY + (this.targetY - rowValue);
         }
      }

      public void handleAmountText(String rowText, float amount) {
         this.rowText = rowText == null ? "" : rowText;
         this.textAlpha = amount;
         this.badgeWidth = this.rowText.isEmpty() ? 0.0F : NativeHud.getMaxForFloat(amount);
         this.keybindsKeyboardAmount = (float)Math.ceil(this.textWidth + (this.badgeWidth > 0.0F ? 10.0F + this.badgeWidth : 0.0F));
      }

      public boolean canAmountFlag(float amount, float rowFactor) {
         this.rowAmount = this.rowAmount + (this.targetVisibility - this.rowAmount) * rowFactor;
         if (Math.abs(this.targetVisibility - this.rowAmount) < 0.002F) {
            this.rowAmount = this.targetVisibility;
         }

         if (this.keybindsKeyboardActive && this.rowAmount < 0.01F) {
            return true;
         } else {
            this.targetY = amount;
            if (!this.rowActive) {
               this.rowActive = true;
               return false;
            } else {
               this.slideOffsetY *= 1.0F - rowFactor;
               if (Math.abs(this.slideOffsetY) < 0.01F) {
                  this.slideOffsetY = 0.0F;
               }

               return false;
            }
         }
      }
   }

   public static final class Pop {
      private long lastUpdateNanos;
      private float popScale;
      private final QuintScissor popAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 200, 0.0);
      private float popProgress;
      private long lastTriggerFrame;

      public float getPopProgress() {
         return this.popProgress;
      }

      public Pop() {
         this.popScale = 1.0F;
      }

      public boolean canTimeAmount(boolean nativeHudFlag, long time) {
         double nativeHudValue = nativeHudFlag ? 1.0 : 0.0;
         if (Math.abs(this.popAnimation.getTargetAmount() - nativeHudValue) > 0.01) {
            this.popAnimation.handleAmountTime(nativeHudValue);
         }

         float amount = (float)this.popAnimation.getAmount();
         if (amount < 0.004F) {
            this.lastTriggerFrame = 0L;
            this.popProgress = 0.0F;
            this.popScale = 1.0F;
            return false;
         } else {
            long nanoTime = System.nanoTime();
            if (this.lastTriggerFrame == 0L || time - this.lastTriggerFrame > 1L) {
               this.lastUpdateNanos = nanoTime;
            }

            this.lastTriggerFrame = time;
            float nativeHudLimit = (float)(nanoTime - this.lastUpdateNanos) / 2.2E8F;
            if (nativeHudLimit >= 1.0F) {
               this.popScale = 1.0F;
               this.popProgress = amount;
            } else {
               this.popScale = 0.94F + 0.060000002F * NativeHud.getAmount(nativeHudLimit, 1.7F);
               this.popProgress = amount * NativeHud.getAmount5(nativeHudLimit / 0.6F);
            }

            return true;
         }
      }

      public void updateTime() {
         this.lastUpdateNanos = System.nanoTime();
      }

      public float getAmount() {
         return this.popScale;
      }
   }

   public static final class Pulse {
      private long lastNanoTime;

      public void updateTime() {
         this.lastNanoTime = System.nanoTime();
      }

      public float getTime() {
         if (this.lastNanoTime == 0L) {
            return 1.0F;
         } else {
            float pulseLimit = (float)(System.nanoTime() - this.lastNanoTime) / 2.4E8F;
            if (pulseLimit >= 1.0F) {
               this.lastNanoTime = 0L;
               return 1.0F;
            } else {
               return 1.0F + 0.25F * NativeHud.getAmountForFloat(pulseLimit);
            }
         }
      }
   }

   public static class Row {
      public float rowHeight;
      public float rowAmount;
      public float onlineNoneAmount;
      public boolean durationActive;
      public float personOnlineAmount;

      public void handleAmount(float amount) {
         this.rowHeight = amount;
         this.onlineNoneAmount = amount;
      }

      public boolean canAmountFlag(float rowFactor) {
         this.rowHeight = this.rowHeight + (this.onlineNoneAmount - this.rowHeight) * rowFactor;
         if (Math.abs(this.onlineNoneAmount - this.rowHeight) < 0.01F) {
            this.rowHeight = this.onlineNoneAmount;
         }

         this.personOnlineAmount = this.personOnlineAmount + (this.rowAmount - this.personOnlineAmount) * rowFactor;
         if (Math.abs(this.rowAmount - this.personOnlineAmount) < 0.002F) {
            this.personOnlineAmount = this.rowAmount;
         }

         return this.durationActive && this.personOnlineAmount < 0.01F;
      }

      public void updateAmountFlag() {
         this.durationActive = true;
         this.rowAmount = 0.0F;
      }

      public void updateAmountFlagDurationActive() {
         this.durationActive = false;
         this.rowAmount = 1.0F;
      }
   }

   public static final class Smooth {
      private float smoothedValue = Float.NaN;

      public float getMax() {
         return Float.isNaN(this.smoothedValue) ? 0.0F : this.smoothedValue;
      }

      public float getMax(float max, float smoothValue, float smoothDivisor, float smoothLimit) {
         if (Float.isNaN(this.smoothedValue)) {
            this.smoothedValue = max;
            return this.smoothedValue;
         } else {
            this.smoothedValue = this.smoothedValue + (max - this.smoothedValue) * (1.0F - (float)Math.exp(-smoothValue / smoothDivisor));
            if (Math.abs(max - this.smoothedValue) < smoothLimit) {
               this.smoothedValue = max;
            }

            return this.smoothedValue;
         }
      }

      public float getAmount(float smoothBound, float smoothValue, float smoothDivisor) {
         return Math.max(smoothBound, this.getAmountForFloat(smoothBound, smoothValue, smoothDivisor));
      }

      public void updateMax() {
         this.smoothedValue = Float.NaN;
      }

      public float getAmountForFloat(float smoothValue, float smoothValue2, float smoothDivisor) {
         return this.getMax(smoothValue, smoothValue2, smoothDivisor, 0.05F);
      }
   }
}
