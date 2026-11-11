package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.VertexGuiUtils;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Random;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.HalloweenChristmasType;

public final class HalloweenMatrixUtils {
   private static double animationTime;
   public static final String BATS_COBWEBS_TEXT = HalloweenChristmasType.formatText("Halloween");
   private static Matrix4f positionMatrix;
   private static float batScaleLimit;
   private static final Random RANDOM = new Random();
   private static final HalloweenMatrixUtils.Bat[] trianglesBats = new HalloweenMatrixUtils.Bat[5];
   private static float halloweenAmount;
   private static final float[] halloweenAmounts = new float[8];
   private static final float[] halloweenTable = new float[8];

   private static void handle(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float halloweenMatrixValue3, float halloweenMatrixValue4, float halloweenMatrixFactor, float halloweenMatrixFactor2, float halloweenMatrixFactor3, float halloweenMatrixFactor4, int halloweenMatrixValue5, int panelOverlay) {
      float vertexColorValue = halloweenMatrixValue + halloweenMatrixFactor * halloweenMatrixFactor3;
      float halloweenMatrixValue8 = halloweenMatrixValue2 + halloweenMatrixFactor2 * halloweenMatrixFactor3;
      float halloweenMatrixValue9 = halloweenMatrixValue3 + halloweenMatrixFactor * halloweenMatrixFactor3;
      float halloweenMatrixValue10 = halloweenMatrixValue4 + halloweenMatrixFactor2 * halloweenMatrixFactor3;
      float halloweenMatrixValue11 = halloweenMatrixValue3 + halloweenMatrixFactor * halloweenMatrixFactor4;
      float halloweenMatrixValue12 = halloweenMatrixValue4 + halloweenMatrixFactor2 * halloweenMatrixFactor4;
      float halloweenMatrixValue13 = halloweenMatrixValue + halloweenMatrixFactor * halloweenMatrixFactor4;
      float halloweenMatrixValue14 = halloweenMatrixValue2 + halloweenMatrixFactor2 * halloweenMatrixFactor4;
      handleVertexColor(vertices, vertexColorValue, halloweenMatrixValue8, halloweenMatrixValue5);
      handleVertexColor(vertices, halloweenMatrixValue9, halloweenMatrixValue10, halloweenMatrixValue5);
      handleVertexColor(vertices, halloweenMatrixValue11, halloweenMatrixValue12, panelOverlay);
      handleVertexColor(vertices, vertexColorValue, halloweenMatrixValue8, halloweenMatrixValue5);
      handleVertexColor(vertices, halloweenMatrixValue11, halloweenMatrixValue12, panelOverlay);
      handleVertexColor(vertices, halloweenMatrixValue13, halloweenMatrixValue14, panelOverlay);
   }

   private static void handle(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float halloweenMatrixFactor, float halloweenMatrixFactor2, float halloweenMatrixBound, float halloweenMatrixFactor3, int panelOverlay) {
      float tipXScaled = halloweenMatrixFactor * 0.12F;
      float wrapDegreesBound = -halloweenMatrixFactor * 0.1F;
      float tipXScaled2 = halloweenMatrixFactor * 0.12F;
      float tipXScaled3 = halloweenMatrixFactor * 0.24F;
      float halloweenMatrixFactor4 = halloweenMatrixFactor2 * 0.48F;
      float halloweenMatrixFactor5 = Math.min(wrapDegreesBound, halloweenMatrixBound) - halloweenMatrixFactor * 0.2F;
      float halloweenMatrixValue4 = 0.0F;
      float halloweenMatrixValue5 = 0.0F;
      float vertexColorValue6 = 0.0F;
      float halloweenMatrixValue7 = 0.0F;

      for (int i = 0; i <= 12; i++) {
         float halloweenMatrixFactor6 = i / 12.0F;
         float halloweenMatrixFactor7 = 1.0F - halloweenMatrixFactor6;
         float halloweenMatrixFactor8 = halloweenMatrixFactor7 * halloweenMatrixFactor7 * tipXScaled + 2.0F * halloweenMatrixFactor7 * halloweenMatrixFactor6 * halloweenMatrixFactor4 + halloweenMatrixFactor6 * halloweenMatrixFactor6 * halloweenMatrixFactor2;
         float halloweenMatrixValue8 = halloweenMatrixFactor7 * halloweenMatrixFactor7 * wrapDegreesBound + 2.0F * halloweenMatrixFactor7 * halloweenMatrixFactor6 * halloweenMatrixFactor5 + halloweenMatrixFactor6 * halloweenMatrixFactor6 * halloweenMatrixBound;
         float halloweenMatrixFactor9 = tipXScaled2 + (halloweenMatrixFactor2 - tipXScaled2) * halloweenMatrixFactor6;
         float halloweenMatrixValue9 = 1.0F - Math.abs((float)Math.sin((Math.PI * 3) * halloweenMatrixFactor6));
         float halloweenMatrixValue10 = tipXScaled3 + (halloweenMatrixBound - tipXScaled3) * halloweenMatrixFactor6 + halloweenMatrixFactor * (0.1F * (float)Math.sin(Math.PI * halloweenMatrixFactor6) + 0.15F * halloweenMatrixValue9 * halloweenMatrixFactor7);
         float halloweenMatrixValue11 = halloweenMatrixValue + halloweenMatrixFactor3 * halloweenMatrixFactor8;
         float halloweenMatrixValue12 = halloweenMatrixValue2 + halloweenMatrixValue8;
         float vertexColorValue = halloweenMatrixValue + halloweenMatrixFactor3 * halloweenMatrixFactor9;
         float halloweenMatrixValue14 = halloweenMatrixValue2 + halloweenMatrixValue10;
         if (i > 0) {
            handleForVertexConsumerFloat(vertices, halloweenMatrixValue4, halloweenMatrixValue5, halloweenMatrixValue11, halloweenMatrixValue12, vertexColorValue, halloweenMatrixValue14, panelOverlay);
            handleForVertexConsumerFloat(vertices, halloweenMatrixValue4, halloweenMatrixValue5, vertexColorValue, halloweenMatrixValue14, vertexColorValue6, halloweenMatrixValue7, panelOverlay);
         }

         halloweenMatrixValue4 = halloweenMatrixValue11;
         halloweenMatrixValue5 = halloweenMatrixValue12;
         vertexColorValue6 = vertexColorValue;
         halloweenMatrixValue7 = halloweenMatrixValue14;
      }
   }

   public static void handleAmountTriangles(int trianglesValue, int randomValue, int trianglesValue3, int randomFactor, int trianglesValue5, int trianglesValue6, int halloweenMatrixValue, int halloweenMatrixLimit) {
      if (isBaseInstance()) {
         float halloweenMatrixFactor = (float)Math.min(0.05, MatrixClearUtils.getAmount());
         animationTime += halloweenMatrixFactor;
         CorzRoundedUtils.updateSamplerView();
         positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
         batScaleLimit = Math.max(0.35F, CorzRoundedUtils.getFramebufferHeight() * 1.2F);
         VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
         float trianglesValue10 = trianglesValue5 * 0.29F;
         float animationTime11 = (float)animationTime;
         handleHalloweenAmounts(beginBuffer, trianglesValue + trianglesValue3 - trianglesValue10, randomValue + trianglesValue10, -1.0F, 1.0F, 50.0F, 7, 4, 11, animationTime11);
         handleHalloweenAmounts(beginBuffer, trianglesValue + trianglesValue10, randomValue + trianglesValue10, 1.0F, 1.0F, 30.0F, 5, 3, 23, animationTime11 + 1.7F);
         handleHalloweenAmounts(beginBuffer, trianglesValue + trianglesValue3 - trianglesValue10, randomValue + randomFactor - trianglesValue10, -1.0F, -1.0F, 24.0F, 5, 3, 37, animationTime11 + 3.1F);
         handleAmount(beginBuffer, trianglesValue + trianglesValue3 - 31.0F, randomValue + trianglesValue10 + 2.0F, halloweenMatrixValue, halloweenMatrixLimit, halloweenMatrixFactor, animationTime11);
         VertexGuiUtils.updateBufferClose();
         VertexConsumer vertices = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
         int releaseRight = MatrixAmountUtils.getValue(-8620404, 0xFF000000 | MatrixEspUtils.releaseRightValue & 16777215, 0.16F);

         for (int i = 0; i < 5; i++) {
            HalloweenMatrixUtils.Bat bat = trianglesBats[i];
            if (bat == null) {
               bat = trianglesBats[i] = new HalloweenMatrixUtils.Bat();
               handleRandomAmount(bat, randomValue, randomFactor, trianglesValue6, true);
            }

            if (!bat.trianglesActive) {
               bat.trianglesCount -= halloweenMatrixFactor;
               if (!(bat.trianglesCount <= 0.0F)) {
                  continue;
               }

               bat.trianglesActive = true;
            }

            handleAmount(bat, halloweenMatrixFactor, halloweenMatrixValue, halloweenMatrixLimit);
            if ((!(bat.trianglesAmount > 0.0F) || !(bat.posX > trianglesValue6 + 24.0F)) && (!(bat.trianglesAmount < 0.0F) || !(bat.posX < -24.0F))) {
               int panelOverlayValue = MatrixAmountUtils.getValue(-16184562, releaseRight, 0.55F + 0.45F * bat.nextFloat);
               handle(
                  vertices,
                  bat.posX,
                  bat.currentY + bat.avoidOffsetY,
                  bat.batScale,
                  bat.trianglesAmount,
                  (float)Math.sin(bat.angle),
                  CorzRoundedUtils.getPanelOverlay(panelOverlayValue)
               );
            } else {
               handleRandomAmount(bat, randomValue, randomFactor, trianglesValue6, false);
            }
         }

         VertexGuiUtils.updateBufferClose();
      }
   }

   private static void handleHalloweenAmounts(VertexConsumer vertices, float halloweenValue, float halloweenValue2, float halloweenFactor, float halloweenFactor2, float halloweenFactor3, int halloweenLimit, int halloweenLimit2, int halloweenMatrixValue, float halloweenValue4) {
      float[] halloweenAmounts3 = halloweenAmounts;
      float[] halloweenTable22 = halloweenTable;

      for (int i = 0; i < halloweenLimit; i++) {
         double angle = (Math.PI / 2) * i / (halloweenLimit - 1);
         float halloweenFactor4 = halloweenFactor3 * (i != 0 && i != halloweenLimit - 1 ? 0.86F + 0.18F * getAmount(halloweenMatrixValue, i) : 1.08F);
         float halloweenFactor5 = i != 0 && i != halloweenLimit - 1 ? 0.7F * (float)Math.sin(halloweenValue4 * 0.8F + i * 1.3F) : 0.0F;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         halloweenAmounts3[i] = halloweenValue + halloweenFactor * (cos * halloweenFactor4 - sin * halloweenFactor5);
         halloweenTable22[i] = halloweenValue2 + halloweenFactor2 * (sin * halloweenFactor4 + cos * halloweenFactor5);
      }

      int halloweenValue5 = getValue(1.0F);
      float halloweenValue6 = 0.32F;

      for (int j = 0; j < halloweenLimit; j++) {
         handleMax(vertices, halloweenValue, halloweenValue2, halloweenAmounts3[j], halloweenTable22[j], halloweenValue6, halloweenValue5);
      }

      for (int k = 1; k <= halloweenLimit2; k++) {
         float pow = (float)Math.pow(k / (halloweenLimit2 + 0.55F), 1.12F);
         int panelOverlay = getValue(0.75F + 0.25F * (1.0F - pow));

         for (int m = 0; m + 1 < halloweenLimit; m++) {
            float halloweenFactor6 = halloweenValue + (halloweenAmounts3[m] - halloweenValue) * pow;
            float halloweenFactor7 = halloweenValue2 + (halloweenTable22[m] - halloweenValue2) * pow;
            float halloweenFactor8 = halloweenValue + (halloweenAmounts3[m + 1] - halloweenValue) * pow;
            float halloweenFactor9 = halloweenValue2 + (halloweenTable22[m + 1] - halloweenValue2) * pow;
            float halloweenValue8 = (halloweenFactor6 + halloweenFactor8) * 0.5F;
            float halloweenValue9 = (halloweenFactor7 + halloweenFactor9) * 0.5F;
            float halloweenFactor10 = halloweenValue8 + (halloweenValue - halloweenValue8) * 0.16F;
            float halloweenFactor11 = halloweenValue9 + (halloweenValue2 - halloweenValue9) * 0.16F;
            float halloweenValue10 = halloweenFactor6;
            float halloweenValue11 = halloweenFactor7;

            for (int p = 1; p <= 4; p++) {
               float halloweenFactor12 = p / 4.0F;
               float halloweenFactor13 = 1.0F - halloweenFactor12;
               float halloweenValue12 = halloweenFactor13 * halloweenFactor13 * halloweenFactor6 + 2.0F * halloweenFactor13 * halloweenFactor12 * halloweenFactor10 + halloweenFactor12 * halloweenFactor12 * halloweenFactor8;
               float halloweenValue13 = halloweenFactor13 * halloweenFactor13 * halloweenFactor7 + 2.0F * halloweenFactor13 * halloweenFactor12 * halloweenFactor11 + halloweenFactor12 * halloweenFactor12 * halloweenFactor9;
               handleMax(vertices, halloweenValue10, halloweenValue11, halloweenValue12, halloweenValue13, halloweenValue6 * 0.85F, panelOverlay);
               halloweenValue10 = halloweenValue12;
               halloweenValue11 = halloweenValue13;
            }
         }
      }
   }

   private static int getValue(float halloweenMatrixValue) {
      int halloweenMatrixBound = Math.round(33.149998F * halloweenMatrixValue);
      return CorzRoundedUtils.getPanelOverlay(Math.min(255, halloweenMatrixBound) << 24 | 14472938);
   }

   private HalloweenMatrixUtils() {
   }

   private static void handle(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float halloweenMatrixFactor, float halloweenMatrixFactor2, int halloweenMatrixLimit, int panelOverlay) {
      float vertexColorValue = halloweenMatrixValue + halloweenMatrixFactor;
      float col2 = halloweenMatrixValue2;

      for (int i = 1; i <= halloweenMatrixLimit; i++) {
         double angle = (Math.PI * 2) * i / halloweenMatrixLimit;
         float halloweenMatrixValue5 = halloweenMatrixValue + halloweenMatrixFactor * (float)Math.cos(angle);
         float halloweenMatrixValue6 = halloweenMatrixValue2 + halloweenMatrixFactor2 * (float)Math.sin(angle);
         handleForVertexConsumerFloat(vertices, halloweenMatrixValue, halloweenMatrixValue2, vertexColorValue, col2, halloweenMatrixValue5, halloweenMatrixValue6, panelOverlay);
         vertexColorValue = halloweenMatrixValue5;
         col2 = halloweenMatrixValue6;
      }
   }

   private static void handleForVertexConsumerFloat(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float vertexColorValue, float halloweenMatrixValue4, float halloweenMatrixValue5, float halloweenMatrixValue6, int panelOverlay) {
      handleVertexColor(vertices, halloweenMatrixValue, halloweenMatrixValue2, panelOverlay);
      handleVertexColor(vertices, vertexColorValue, halloweenMatrixValue4, panelOverlay);
      handleVertexColor(vertices, halloweenMatrixValue5, halloweenMatrixValue6, panelOverlay);
   }

   private static void handleRandomAmount(HalloweenMatrixUtils.Bat bat, int randomValue, int randomFactor, int randomValue2, boolean randomFlag) {
      bat.nextFloat = RANDOM.nextFloat();
      bat.batScale = 3.4F + 2.8F * bat.nextFloat;
      bat.trianglesAmount = RANDOM.nextBoolean() ? 1.0F : -1.0F;
      bat.flySpeed = 58.0F + 54.0F * bat.nextFloat + RANDOM.nextFloat() * 14.0F;
      bat.posX = bat.trianglesAmount > 0.0F ? -20.0F : randomValue2 + 20.0F;
      bat.randomAmount = randomValue + randomFactor * (0.06F + 0.88F * RANDOM.nextFloat());
      bat.driftSpeed = (RANDOM.nextFloat() - 0.5F) * 18.0F;
      bat.bobAmplitude = 3.0F + RANDOM.nextFloat() * 8.0F;
      bat.bobFrequency = 1.4F + RANDOM.nextFloat() * 1.2F;
      bat.angle = RANDOM.nextFloat() * 6.283F;
      bat.batAmount = (float) (Math.PI * 2) * (8.5F - 2.5F * bat.nextFloat + RANDOM.nextFloat());
      bat.avoidOffsetY = 0.0F;
      bat.trianglesActive = false;
      bat.trianglesCount = randomFlag ? RANDOM.nextFloat() * 5.0F : 2.5F + RANDOM.nextFloat() * 7.0F;
      bat.currentY = bat.randomAmount;
   }

   public static boolean isBaseInstance() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      return singleton != null && singleton.musicSongBooleanBase.isEnabled() && isHalloween();
   }

   public static void updateHalloweenAmount() {
      for (int i = 0; i < 5; i++) {
         trianglesBats[i] = null;
      }

      halloweenAmount = 0.0F;
   }

   private static void handleAmount(HalloweenMatrixUtils.Bat bat, float halloweenMatrixFactor, int halloweenMatrixValue, int halloweenMatrixLimit) {
      bat.posX = bat.posX + bat.trianglesAmount * bat.flySpeed * halloweenMatrixFactor;
      bat.randomAmount = bat.randomAmount + bat.driftSpeed * halloweenMatrixFactor;
      bat.angle = bat.angle + bat.batAmount * halloweenMatrixFactor;
      bat.currentY = bat.randomAmount + bat.bobAmplitude * (float)Math.sin(animationTime * bat.bobFrequency + bat.angle * 0.05F);
      float halloweenMatrixFactor2 = bat.posX - halloweenMatrixValue;
      float halloweenMatrixFactor3 = bat.currentY + bat.avoidOffsetY - halloweenMatrixLimit;
      float distance = (float)Math.sqrt(halloweenMatrixFactor2 * halloweenMatrixFactor2 + halloweenMatrixFactor3 * halloweenMatrixFactor3);
      float halloweenMatrixBound = 0.0F;
      if (distance < 30.0F) {
         halloweenMatrixBound = bat.avoidOffsetY + (halloweenMatrixFactor3 >= 0.0F ? 1.0F : -1.0F) * (30.0F - distance) * 1.4F;
      }

      float max3 = Math.max(-28.0F, Math.min(28.0F, halloweenMatrixBound));
      bat.avoidOffsetY = bat.avoidOffsetY + (max3 - bat.avoidOffsetY) * Math.min(1.0F, halloweenMatrixFactor * (distance < 30.0F ? 9.0F : 1.6F));
   }

   public static boolean isHalloween() {
      return HalloweenChristmasType.getTimeInstance() == HalloweenChristmasType.HALLOWEEN;
   }

   private static void handleVertexColor(VertexConsumer vertices, float vertexColorValue, float vertexColorValue2, int panelOverlay) {
      vertices.vertex(positionMatrix, vertexColorValue, vertexColorValue2, 0.0F).color(panelOverlay);
   }

   private static void handleMax(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float halloweenMatrixValue3, float halloweenMatrixValue4, float halloweenMatrixValue5, int panelOverlay) {
      float halloweenMatrixFactor = halloweenMatrixValue3 - halloweenMatrixValue;
      float halloweenMatrixFactor2 = halloweenMatrixValue4 - halloweenMatrixValue2;
      float distance = (float)Math.sqrt(halloweenMatrixFactor * halloweenMatrixFactor + halloweenMatrixFactor2 * halloweenMatrixFactor2);
      if (!(distance < 0.001F)) {
         float halloweenMatrixValue7 = -halloweenMatrixFactor2 / distance;
         float halloweenMatrixValue8 = halloweenMatrixFactor / distance;
         int halloweenMatrixValue9 = panelOverlay & 16777215;
         handle(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixValue3, halloweenMatrixValue4, halloweenMatrixValue7, halloweenMatrixValue8, -halloweenMatrixValue5 - batScaleLimit, -halloweenMatrixValue5, halloweenMatrixValue9, panelOverlay);
         handle(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixValue3, halloweenMatrixValue4, halloweenMatrixValue7, halloweenMatrixValue8, -halloweenMatrixValue5, halloweenMatrixValue5, panelOverlay, panelOverlay);
         handle(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixValue3, halloweenMatrixValue4, halloweenMatrixValue7, halloweenMatrixValue8, halloweenMatrixValue5, halloweenMatrixValue5 + batScaleLimit, panelOverlay, halloweenMatrixValue9);
      }
   }

   private static float getAmount(int halloweenMatrixValue, int i) {
      double sinScaled = Math.sin(halloweenMatrixValue * 12.9898 + i * 78.233) * 43758.5453;
      return (float)(sinScaled - Math.floor(sinScaled));
   }

   private static void handleAmount(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, int halloweenMatrixValue3, int halloweenMatrixLimit, float halloweenMatrixFactor, float halloweenMatrixValue4) {
      float halloweenMatrixFactor2 = 22.0F + 6.0F * (float)Math.sin(halloweenMatrixValue4 * 0.55F);
      float halloweenMatrixValue5 = halloweenMatrixValue2 + halloweenMatrixFactor2 * (1.0F - halloweenAmount);
      boolean halloweenMatrixFlag = Math.abs(halloweenMatrixValue3 - halloweenMatrixValue) < 18.0F && halloweenMatrixLimit > halloweenMatrixValue2 - 4.0F && halloweenMatrixLimit < halloweenMatrixValue5 + 14.0F;
      halloweenAmount = halloweenAmount + ((halloweenMatrixFlag ? 0.82F : 0.0F) - halloweenAmount) * Math.min(1.0F, halloweenMatrixFactor * (halloweenMatrixFlag ? 7.0F : 1.2F));
      float halloweenMatrixValue6 = halloweenMatrixValue2 + halloweenMatrixFactor2 * (1.0F - halloweenAmount);
      float halloweenMatrixValue7 = 0.8F * (float)Math.sin(halloweenMatrixValue4 * 1.1F);
      float halloweenMatrixValue8 = halloweenMatrixValue + halloweenMatrixValue7;
      handleMax(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixValue8, halloweenMatrixValue6 - 2.0F, 0.28F, getValue(1.1F));
      int panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixAmountUtils.getValue(-16184562, -8620404, 0.92F));
      float halloweenMatrixValue9 = 0.35F * (float)Math.sin(halloweenMatrixValue4 * 6.0F);

      for (int i = -1; i <= 1; i += 2) {
         for (int j = 0; j < 4; j++) {
            float halloweenMatrixValue10 = halloweenMatrixValue6 - 0.8F + j * 0.9F;
            float halloweenMatrixValue11 = halloweenMatrixValue8 + i * (2.6F + (j != 1 && j != 2 ? 0.0F : 0.6F));
            float halloweenMatrixValue12 = halloweenMatrixValue10 + (j - 1.5F) * 1.1F - 1.0F + (j % 2 == 0 ? halloweenMatrixValue9 : -halloweenMatrixValue9);
            float halloweenMatrixValue13 = halloweenMatrixValue11 + i * 1.4F;
            float halloweenMatrixValue14 = halloweenMatrixValue12 + 2.2F + (j - 1.5F) * 0.6F;
            handleMax(vertices, halloweenMatrixValue8, halloweenMatrixValue6 + j * 0.5F - 0.6F, halloweenMatrixValue11, halloweenMatrixValue12, 0.3F, panelOverlay);
            handleMax(vertices, halloweenMatrixValue11, halloweenMatrixValue12, halloweenMatrixValue13, halloweenMatrixValue14, 0.26F, panelOverlay);
         }
      }

      handle(vertices, halloweenMatrixValue8, halloweenMatrixValue6 + 1.4F, 2.1F, 2.4F, 12, panelOverlay);
      handle(vertices, halloweenMatrixValue8, halloweenMatrixValue6 - 1.3F, 1.3F, 1.2F, 10, panelOverlay);
   }

   private static void handle(VertexConsumer vertices, float halloweenMatrixValue, float halloweenMatrixValue2, float halloweenMatrixFactor, float halloweenMatrixFactor2, float halloweenMatrixValue3, int panelOverlay) {
      float halloweenMatrixValue5 = halloweenMatrixFactor * (0.94F + 0.08F * halloweenMatrixValue3);
      float halloweenMatrixBound = -halloweenMatrixFactor * (0.05F + 0.58F * halloweenMatrixValue3);
      handle(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixFactor, halloweenMatrixValue5, halloweenMatrixBound, 1.0F, panelOverlay);
      handle(vertices, halloweenMatrixValue, halloweenMatrixValue2, halloweenMatrixFactor, halloweenMatrixValue5, halloweenMatrixBound, -1.0F, panelOverlay);
      handle(vertices, halloweenMatrixValue, halloweenMatrixValue2 + halloweenMatrixFactor * 0.06F, halloweenMatrixFactor * 0.19F, halloweenMatrixFactor * 0.32F, 10, panelOverlay);
      float halloweenMatrixValue7 = halloweenMatrixValue + halloweenMatrixFactor2 * halloweenMatrixFactor * 0.03F;
      float halloweenMatrixValue8 = halloweenMatrixValue2 - halloweenMatrixFactor * 0.27F;
      handle(vertices, halloweenMatrixValue7, halloweenMatrixValue8, halloweenMatrixFactor * 0.15F, halloweenMatrixFactor * 0.14F, 8, panelOverlay);
      handleForVertexConsumerFloat(vertices, halloweenMatrixValue7 - halloweenMatrixFactor * 0.13F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.02F, halloweenMatrixValue7 - halloweenMatrixFactor * 0.03F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.08F, halloweenMatrixValue7 - halloweenMatrixFactor * 0.12F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.3F, panelOverlay);
      handleForVertexConsumerFloat(vertices, halloweenMatrixValue7 + halloweenMatrixFactor * 0.13F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.02F, halloweenMatrixValue7 + halloweenMatrixFactor * 0.03F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.08F, halloweenMatrixValue7 + halloweenMatrixFactor * 0.12F, halloweenMatrixValue8 - halloweenMatrixFactor * 0.3F, panelOverlay);
   }

   private static final class Bat {
      float driftSpeed;
      float randomAmount;
      float batAmount;
      float bobAmplitude;
      boolean trianglesActive;
      float trianglesAmount;
      float posX;
      float trianglesCount;
      float batScale;
      float nextFloat;
      float bobFrequency;
      float angle;
      float currentY;
      float avoidOffsetY;
      float flySpeed;
   }
}
