package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexConsumerUtilsAmount;
import com.corz.client.render.VertexTexturePeek;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.utils.QuintBounceType;

public class MatrixCorz {
   private float corzAmount;
   private boolean introsoundActive;
   private final MatrixStack matrixStack;
   private boolean corzVertexActive;
   private float paletteWeightE;
   private final float[] introsoundTable;
   private static boolean scissorMatrixActive = false;
   private final float[] corzMatrixTable;
   private long lastNanoTime;
   private int lastPaletteColorRgb;
   private float matrixAmount;
   private float corzVertexAmount;
   private static final String[] corzMatrixTexts = new String[]{"C", "O", "R", "Z"};
   private float matrixCorzAmount;
   private float paletteWeightB;
   private static final String CORZ = "CORZ";
   private boolean corzActive = false;
   private VertexTexturePeek vertexTexture;
   private static final float[] matrixCorzTable = new float[]{0.06F, 0.95F, 0.425F, 0.575F};
   private static final float[] table = new float[]{0.04F, 0.97F, 0.58F, 0.58F};
   private static final float[] BLOB_SPREAD_FACTORS = new float[]{0.46F, 0.52F, 0.28F, 0.28F};
   private final float[] blobColorRgb;
   private static final float[] BLOB_RADIUS_FACTORS = new float[]{0.045F, 0.038F, 0.065F, 0.095F};
   private static final float[] BLOB_DRIFT_AMPLITUDES = new float[]{0.034F, 0.034F, 0.026F, 0.026F};
   private final float[] blobWeights;
   private float paletteWeightC;
   private static final float[] BLOB_DRIFT_PERIOD_SECONDS = new float[]{16.0F, 18.0F, 14.0F, 15.0F};
   private boolean matrixCorzActive;
   private float paletteWeightD;
   private static final float[] introsoundAmounts = new float[]{19.0F, 15.0F, 17.0F, 13.0F};
   private static final float[] introsoundCorzclientAmounts = new float[]{0.0F, 2.1F, 0.6F, 3.7F};
   private boolean corzFlag;
   private boolean scissorRendered = false;
   private boolean corzMatrixActive;
   private boolean paletteInitialized;
   private static final Color OFF_WHITE = new Color(-920587, true);
   private static final SoundEvent soundEvent = SoundEvent.of(Identifier.of("corzclient", "introsound"));
   private boolean introsoundInitialized;

   public void renderScissorMatrix(DrawContext context, int scissorMatrixValue, int randomValue, int introsoundFactor, int hashHuntBound, int wrapDegreesBound) {
      if (this.corzActive) {
         CorzClient.fontManager.setMatrixStack(this.matrixStack);
         MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
         float millis = (float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F;
         if (millis >= 3000.0F) {
            this.corzActive = false;
            this.scissorRendered = true;
            scissorMatrixActive = true;
            this.handleMatrixPeek(scissorMatrixValue, randomValue, introsoundFactor, hashHuntBound, wrapDegreesBound, -16448249);
         } else {
            if (!this.matrixCorzActive) {
               MinecraftClient instance = MinecraftClient.getInstance();
               if (instance.player != null) {
                  instance.player.playSound(soundEvent, 1.0F, 1.0F);
               }

               this.matrixCorzActive = true;
            }

            float matrixCorzLimit;
            if (millis < 700.0F) {
               matrixCorzLimit = (float)QuintBounceType.QUART_OUT.getUnaryOperator(millis / 700.0);
            } else if (millis < 2300.0F) {
               matrixCorzLimit = 1.0F;
            } else {
               matrixCorzLimit = 1.0F - (float)QuintBounceType.QUART_IN.getUnaryOperator((millis - 2300.0F) / 700.0);
            }

            float scissorMatrixValue7 = getAmount(matrixCorzLimit);
            this.handleMatrixPeek(scissorMatrixValue, randomValue, introsoundFactor, hashHuntBound, wrapDegreesBound, -16448249);
            context.enableScissor(scissorMatrixValue, randomValue, scissorMatrixValue + introsoundFactor, randomValue + hashHuntBound);

            try {
               this.handleAmountsIntrosound(scissorMatrixValue, randomValue, introsoundFactor, hashHuntBound, wrapDegreesBound, millis);
               this.handleMatrixStack(scissorMatrixValue, randomValue, introsoundFactor, hashHuntBound, millis);
            } finally {
               context.disableScissor();
            }

            float vertexConsumerFactor = getAmount(millis / 2300.0F);
            VertexConsumerUtilsAmount.handle(
               this.matrixStack.peek().getPositionMatrix(), scissorMatrixValue, randomValue, introsoundFactor, hashHuntBound, wrapDegreesBound, vertexConsumerFactor, scissorMatrixValue7 * 0.95F, MatrixEspUtils.releaseRightValue
            );
         }
      }
   }

   private void handleAmountMatrix(float matrixValue, float randomValue, float matrixValue3) {
      float wrapDegreesBound = 1.25F;
      this.handleMatrixPeek(matrixValue, randomValue, 210.0F, 2.5F, wrapDegreesBound, 318767103);
      float matrixBound = 210.0F * matrixValue3;
      if (!(matrixBound <= 0.01F)) {
         float matrixFactor = Math.max(matrixBound, 2.5F);
         float min2 = Math.min(1.0F, matrixBound / 2.5F);
         float matrixDivisor = MatrixPeekUtilsRender.getAmount();
         float matrixValue5 = 9.0F;
         float matrixValue6 = min2 * CorzRoundedUtils.panelOverlayAmount;
         Matrix4f positionMatrix = this.matrixStack.peek().getPositionMatrix();
         CorzRoundedUtils.handle(
            positionMatrix,
            matrixValue - matrixValue5,
            randomValue - matrixValue5,
            matrixValue + matrixFactor + matrixValue5,
            randomValue + 2.5F + matrixValue5,
            matrixValue / matrixDivisor,
            randomValue / matrixDivisor,
            matrixFactor / matrixDivisor,
            2.5F / matrixDivisor,
            wrapDegreesBound / matrixDivisor,
            this.paletteWeightB,
            this.matrixCorzAmount,
            this.paletteWeightE,
            0.28F * matrixValue6,
            7.0F / matrixDivisor,
            1.0F,
            false
         );
         int max2 = Math.max(1, Math.min(24, (int)Math.ceil(matrixFactor / 1.5F)));

         for (int i = 0; i < max2; i++) {
            float matrixFactor3 = (i + 0.5F) / max2;
            float rectRoundedValue = i == 0 ? matrixValue - 1.0F : matrixValue + matrixFactor * i / max2;
            float peekPositionLimit = i == max2 - 1 ? matrixValue + matrixFactor + 1.0F : matrixValue + matrixFactor * (i + 1) / max2;
            CorzRoundedUtils.handleRectRounded(
               positionMatrix,
               rectRoundedValue,
               randomValue - 1.0F,
               peekPositionLimit,
               randomValue + 2.5F + 1.0F,
               matrixValue / matrixDivisor,
               randomValue / matrixDivisor,
               matrixFactor / matrixDivisor,
               2.5F / matrixDivisor,
               wrapDegreesBound / matrixDivisor,
               0.0F,
               this.matrixAmount + (this.paletteWeightB - this.matrixAmount) * matrixFactor3,
               this.paletteWeightD + (this.matrixCorzAmount - this.paletteWeightD) * matrixFactor3,
               this.paletteWeightC + (this.paletteWeightE - this.paletteWeightC) * matrixFactor3,
               matrixValue6
            );
         }
      }
   }

   private void updateAmountAmounts() {
      int value = MatrixEspUtils.releaseRightValue & 16777215;
      if (!this.paletteInitialized || value != this.lastPaletteColorRgb) {
         this.paletteInitialized = true;
         this.lastPaletteColorRgb = value;
         int matrixCorzValue = 0xFF000000 | value;
         int matrixValue = MatrixAmountUtils.getValue(matrixCorzValue, -38.0F, 0.9F, 1.0F);
         this.paletteWeightB = MatrixAmountUtils.getAmount(matrixCorzValue);
         this.matrixCorzAmount = MatrixAmountUtils.getAmount4(matrixCorzValue);
         this.paletteWeightE = MatrixAmountUtils.getAmount6(matrixCorzValue);
         this.matrixAmount = MatrixAmountUtils.getAmount(matrixValue);
         this.paletteWeightD = MatrixAmountUtils.getAmount4(matrixValue);
         this.paletteWeightC = MatrixAmountUtils.getAmount6(matrixValue);

         for (int i = 0; i < 6; i++) {
            this.blobColorRgb[i] = 1.0F;
         }

         this.blobWeights[2] = this.getAmounts(2, this.matrixAmount, this.paletteWeightD, this.paletteWeightC);
         this.blobWeights[3] = this.getAmounts(3, this.paletteWeightB, this.matrixCorzAmount, this.paletteWeightE);
      }
   }

   private float getAmounts(int matrixCorzValue, float base, float base2, float base3) {
      float pow = (float)Math.pow(base, 2.2F);
      float pow2 = (float)Math.pow(base2, 2.2F);
      float pow3 = (float)Math.pow(base3, 2.2F);
      this.blobColorRgb[matrixCorzValue * 3] = pow;
      this.blobColorRgb[matrixCorzValue * 3 + 1] = pow2;
      this.blobColorRgb[matrixCorzValue * 3 + 2] = pow3;
      float matrixCorzBound = 0.2126F * pow + 0.7152F * pow2 + 0.0722F * pow3;
      return (float)Math.sqrt(Math.min(1.0F, 0.3F / Math.max(matrixCorzBound, 0.001F)));
   }

   private void updateFlagCorz() {
      this.corzActive = true;
      this.scissorRendered = false;
      this.matrixCorzActive = false;
      this.corzMatrixActive = false;
      this.lastNanoTime = System.nanoTime();
   }

   public boolean isFlag() {
      if (scissorMatrixActive) {
         this.scissorRendered = true;
         return false;
      } else if (!this.corzActive && !this.scissorRendered) {
         this.updateFlagCorz();
         return true;
      } else {
         return this.corzActive;
      }
   }

   private void handleCorzMatrix(float corzMatrixValue, float corzMatrixValue2) {
      if (this.corzVertexActive) {
         try {
            for (int i = 0; i < corzMatrixTexts.length; i++) {
               this.vertexTexture.handlePeekEnd(corzMatrixTexts[i], corzMatrixValue + this.corzMatrixTable[i], corzMatrixValue2, -920587, 56.716415F);
            }
         } catch (Throwable e) {
            this.corzFlag = true;
            this.corzMatrixActive = false;
         }
      } else {
         this.matrixStack.push();

         try {
            this.matrixStack.scale(2.4F, 2.4F, 1.0F);
            CorzClient.fontRenderer().handleForStringFloat("CORZ", corzMatrixValue / 2.4F, corzMatrixValue2 / 2.4F, OFF_WHITE);
         } finally {
            this.matrixStack.pop();
         }
      }
   }

   private void handleMatrixStack(int matrixStackValue, int randomValue, int introsoundFactor, int hashHuntBound, float matrixStackLimit) {
      float panelOverlayLimit;
      float x;
      if (matrixStackLimit >= 2300.0F) {
         float matrixStackFactor = getAmount((matrixStackLimit - 2300.0F) / 320.0F);
         panelOverlayLimit = 1.0F - matrixStackFactor * matrixStackFactor;
         x = 1.0F - 0.029999971F * matrixStackFactor * matrixStackFactor;
      } else if (matrixStackLimit < 220.0F) {
         float panelXScaled = matrixStackLimit / 220.0F;
         panelOverlayLimit = MatrixStackUtilsAmount.getAmountForFloat(panelXScaled / 0.6F);
         x = 0.94F + 0.060000002F * MatrixStackUtilsAmount.getAmount(panelXScaled, 1.6F);
      } else {
         panelOverlayLimit = 1.0F;
         x = 1.0F;
      }

      if (!(panelOverlayLimit <= 0.002F)) {
         if (!this.corzMatrixActive) {
            this.updateCorzVertex();
         }

         this.updateAmountAmounts();
         float matrixStackValue6 = 43.5F;
         float x2 = matrixStackValue + introsoundFactor / 2.0F;
         float matrixStackValue7 = MatrixStackUtilsAmount.getAmount(randomValue + (hashHuntBound - matrixStackValue6) / 2.0F);
         float matrixStackValue8 = matrixStackValue7 + 19.0F;
         boolean matrixStackFlag = Math.abs(x - 1.0F) > 5.0E-4F;
         if (matrixStackFlag) {
            float y = matrixStackValue7 + matrixStackValue6 / 2.0F;
            this.matrixStack.push();
            this.matrixStack.translate(x2, y, 0.0F);
            this.matrixStack.scale(x, x, 1.0F);
            this.matrixStack.translate(-x2, -y, 0.0F);
         }

         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            this.handleCorzMatrix(MatrixStackUtilsAmount.getAmount(x2 - this.corzAmount / 2.0F), matrixStackValue8 - this.corzVertexAmount);
            this.handleAmountMatrix(MatrixStackUtilsAmount.getAmount(x2 - 105.0F), matrixStackValue8 + 22.0F, MatrixStackUtilsAmount.getAmount3(matrixStackLimit / 2300.0F));
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            if (matrixStackFlag) {
               this.matrixStack.pop();
            }
         }
      }
   }

   public static boolean isScissorMatrixActive() {
      return scissorMatrixActive;
   }

   private void updateCorzVertex() {
      this.corzMatrixActive = true;
      this.vertexTexture = this.corzFlag ? null : VertexTexturePeek.getWave();
      this.corzVertexActive = this.vertexTexture != null && this.vertexTexture.isCorzclientFlag();
      if (this.corzVertexActive) {
         float corzVertexTotal = 0.0F;

         for (int i = 0; i < corzMatrixTexts.length; i++) {
            this.corzMatrixTable[i] = corzVertexTotal;
            corzVertexTotal += this.vertexTexture.getCorzclientAmounts(corzMatrixTexts[i], 56.716415F) + 3.0F;
         }

         this.corzAmount = corzVertexTotal - 3.0F;
         this.corzVertexAmount = this.vertexTexture.getAmountParsedCorzfontAmount(56.716415F);
      } else {
         this.corzAmount = CorzClient.fontRenderer().getAbcdefghijklmnopqrstuvwxyzCorzfont("CORZ") * 2.4F;
         this.corzVertexAmount = 27.6F;
      }
   }

   public MatrixCorz() {
      this.matrixCorzActive = false;
      this.matrixStack = new MatrixStack();
      this.corzMatrixTable = new float[corzMatrixTexts.length];
      this.introsoundTable = new float[24];
      this.blobColorRgb = new float[18];
      this.blobWeights = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   }

   private void handleMatrixPeek(float matrixPeekValue, float randomValue, float introsoundFactor, float hashHuntBound, float wrapDegreesBound, int matrixPeekValue6) {
      if (!(introsoundFactor <= 0.0F) && !(hashHuntBound <= 0.0F)) {
         float matrixPeekDivisor = MatrixPeekUtilsRender.getAmount();
         CorzRoundedUtils.handleRectRounded(
            this.matrixStack.peek().getPositionMatrix(),
            matrixPeekValue - 1.0F,
            randomValue - 1.0F,
            matrixPeekValue + introsoundFactor + 1.0F,
            randomValue + hashHuntBound + 1.0F,
            matrixPeekValue / matrixPeekDivisor,
            randomValue / matrixPeekDivisor,
            introsoundFactor / matrixPeekDivisor,
            hashHuntBound / matrixPeekDivisor,
            wrapDegreesBound / matrixPeekDivisor,
            0.0F,
            (matrixPeekValue6 >> 16 & 0xFF) / 255.0F,
            (matrixPeekValue6 >> 8 & 0xFF) / 255.0F,
            (matrixPeekValue6 & 0xFF) / 255.0F,
            (matrixPeekValue6 >>> 24 & 0xFF) / 255.0F * CorzRoundedUtils.panelOverlayAmount
         );
      }
   }

   private void handleAmountsIntrosound(float introsoundValue, float matrixStackValue, float introsoundFactor, float hashHuntBound, float wrapDegreesBound, float millis) {
      if (!this.introsoundActive) {
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat(millis / 700.0F);
         float introsoundFactor4 = millis < 2300.0F ? 1.0F : 1.0F - MatrixStackUtilsAmount.getAmount3((millis - 2300.0F) / 700.0F);
         float ambientFramebufferLimit = amountForFloat * introsoundFactor4 * CorzRoundedUtils.panelOverlayAmount;
         if (!(ambientFramebufferLimit <= 0.003F)) {
            try {
               if (!this.introsoundInitialized) {
                  this.introsoundInitialized = true;
                  if (!CorzRoundedUtils.isValid()) {
                     this.introsoundActive = true;
                     return;
                  }
               }

               this.updateAmountAmounts();
               float amountTime = TwoDidUtils.getAmountTime();
               float seconds = millis / 1000.0F;
               float introsoundFactor5 = 0.86F + 0.13999999F * amountForFloat;

               for (int i = 0; i < 4; i++) {
                  float introsoundFactor6 = hashHuntBound * BLOB_DRIFT_AMPLITUDES[i];
                  this.introsoundTable[i * 4] = introsoundValue
                     + introsoundFactor * matrixCorzTable[i]
                     + introsoundFactor6 * (float)Math.sin((float) (Math.PI * 2) * seconds / BLOB_DRIFT_PERIOD_SECONDS[i] + introsoundCorzclientAmounts[i]);
                  this.introsoundTable[i * 4 + 1] = matrixStackValue
                     + hashHuntBound * table[i]
                     + introsoundFactor6 * (float)Math.cos((float) (Math.PI * 2) * seconds / introsoundAmounts[i] + introsoundCorzclientAmounts[i]);
                  this.introsoundTable[i * 4 + 2] = hashHuntBound * BLOB_SPREAD_FACTORS[i] * introsoundFactor5;
                  this.introsoundTable[i * 4 + 3] = BLOB_RADIUS_FACTORS[i] * this.blobWeights[i] * (i < 2 ? 1.0F : amountTime);
               }

               CorzRoundedUtils.handleAmbientFramebuffer(
                  this.matrixStack.peek().getPositionMatrix(), introsoundValue, matrixStackValue, introsoundFactor, hashHuntBound, wrapDegreesBound, 1.0F, ambientFramebufferLimit, this.introsoundTable, this.blobColorRgb, 4
               );
            } catch (Throwable e) {
               this.introsoundActive = true;
            }
         }
      }
   }

   private static float getAmount(float matrixCorzLimit) {
      return matrixCorzLimit < 0.0F ? 0.0F : (matrixCorzLimit > 1.0F ? 1.0F : matrixCorzLimit);
   }
}
