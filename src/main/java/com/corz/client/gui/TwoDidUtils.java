package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import java.util.Objects;
import java.util.Random;
import net.minecraft.client.gui.DrawContext;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.ToxicElectricUtils;

public final class TwoDidUtils {
   private static long lastNanoTime;
   private static float randomScale = 30.0F;
   private static boolean compileShaderFlag;
   private static final float[] peekTable = new float[24];
   private static int twoColorValue;
   private static final int[] customTwoValues = new int[5];
   private static long twoDidValue;
   private static int twoColorValue5TwoCustom;
   private static String twoColorText;
   private static boolean twoActive;
   private static float twoAmount = 27.0F;
   private static int twoColorValue7TwoCustom;
   public static final int UNUSED_ONE = 1;
   private static float twoDidAmount = -1.0F;
   private static final TwoDidUtils.Field[] peekFields = new TwoDidUtils.Field[]{
      new TwoDidUtils.Field(3, 2, 1.0F, 1.0F), new TwoDidUtils.Field(1, 4, 0.6F, 1.3F), new TwoDidUtils.Field(2, 2, 0.9F, 1.0F)
   };
   private static boolean peekActive;
   private static final float[] twoDidTable = new float[]{0.0F, 1.0F, 1.0F, 0.0F, 0.5F, 1.0F, 0.5F, 0.0F};
   private static final float[] table = new float[15];
   private static long lastNanoTimeTwo;
   private static boolean twoColorFlag;
   private static int twoColorValue6TwoCustom;
   private static double twoCount;
   private static double animationPhase;
   private static final float[] CORNER_OFFSETS = new float[]{0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.5F, 1.0F, 0.5F};
   private static float twoCustomAmount = 1.0F;
   private static final float[] VERTEX_BUFFER = new float[18];

   public static void handlePeekAmounts(int peekValue, float ambientFramebufferFactor, float matrixStackValue, float peekBound, float hashHuntBound, float lerpUuidFactor, float peekValue5, float peekFactor) {
      if (!peekActive && !(peekBound <= 1.0F) && !(hashHuntBound <= 1.0F)) {
         float ambientFramebufferLimit = Math.min(0.42F, getAmountTime() * getIntensityOpacity() * 0.95F)
            * (CardMatrixUtils.isModernFlag() ? 0.7F : 1.0F)
            * peekFactor
            * CorzRoundedUtils.panelOverlayAmount;
         if (!(ambientFramebufferLimit < 0.004F)) {
            try {
               if (!compileShaderFlag && !isShaderCompile()) {
                  return;
               }

               updateAmountTwo();
               int peekValue7 = peekValue & 7;
               int valueInt = getValueInt(peekValue);
               float peekValue9 = Math.max(40.0F * peekValue5, Math.min(130.0F * peekValue5, 0.9F * Math.max(peekBound, hashHuntBound))) * Math.max(0.6F, Math.min(1.5F, getRadius()));
               handleAmountsAmount(0, peekValue7, valueInt, ambientFramebufferFactor, matrixStackValue, peekBound, hashHuntBound, peekValue9, 1.0F, 0);
               byte ambientFramebufferBound = 1;
               if (peekValue7 < 4) {
                  handleAmountsAmount(1, peekValue7 ^ 2, getValueInt(valueInt), ambientFramebufferFactor, matrixStackValue, peekBound, hashHuntBound, peekValue9 * 0.86F, 0.76F, 4);
                  ambientFramebufferBound = 2;
               }

               CorzRoundedUtils.handleAmbientFramebuffer(
                  MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
                  ambientFramebufferFactor,
                  matrixStackValue,
                  peekBound,
                  hashHuntBound,
                  lerpUuidFactor,
                  MatrixPeekUtilsRender.getAmount(),
                  ambientFramebufferLimit,
                  peekTable,
                  VERTEX_BUFFER,
                  ambientFramebufferBound
               );
            } catch (Throwable e) {
               handleFlag(String.valueOf(e));
            }
         }
      }
   }

   private static void handleAmountsAmount(TwoDidUtils.Field field, float twoDidValue, float matrixStackValue, float twoDidBound, float hashHuntBound, float twoDidValue3) {
      float twoDidFactor = Math.min(twoDidBound, hashHuntBound) * 0.58F * field.fieldAmount * getRadius() * (0.88F + 0.12F * twoDidValue3);
      float twoDidFactor2 = twoDidBound / field.gridSize;
      float twoDidFactor3 = hashHuntBound / field.fieldValue;
      float twoDidFactor4 = 0.88F;
      double animationPhase52 = animationPhase;

      for (int i = 0; i < field.dotCount; i++) {
         int index = i * 5;
         float twoDidFactor5 = (float)(
            0.62 * Math.sin(animationPhase52 * field.wobbleFrequencies[index] + field.fieldTable[index])
               + 0.38 * Math.sin(animationPhase52 * field.wobbleFrequencies[index + 1] * 1.618 + field.fieldTable[index + 1])
         );
         float twoDidFactor6 = (float)(
            0.62 * Math.cos(animationPhase52 * field.wobbleFrequencies[index + 2] + field.fieldTable[index + 2])
               + 0.38 * Math.sin(animationPhase52 * field.wobbleFrequencies[index + 3] * 1.618 + field.fieldTable[index + 3])
         );
         float twoDidFactor7 = 1.0F + 0.12F * (float)Math.sin(animationPhase52 * field.wobbleFrequencies[index + 4] + field.fieldTable[index + 4]);
         field.dotVertexData[i * 4] = twoDidValue + twoDidBound * (0.06F + twoDidFactor4 * field.dotOffsetsX[i]) + twoDidFactor5 * twoDidFactor2 * 0.28F;
         field.dotVertexData[i * 4 + 1] = matrixStackValue + hashHuntBound * (0.06F + twoDidFactor4 * field.dotOffsetsY[i]) + twoDidFactor6 * twoDidFactor3 * 0.28F;
         field.dotVertexData[i * 4 + 2] = twoDidFactor * field.dotSizes[i];
         int index2 = field.table[i] * 3;
         float pilotStatusBound = 0.2126F * table[index2] + 0.7152F * table[index2 + 1] + 0.0722F * table[index2 + 2];
         field.dotVertexData[i * 4 + 3] = Math.min(1.0F, field.dotAlphas[i] * twoDidFactor7) * Math.min(1.0F, 0.3F / Math.max(pilotStatusBound, 0.001F));
         field.peekTable[i * 3] = table[index2];
         field.peekTable[i * 3 + 1] = table[index2 + 1];
         field.peekTable[i * 3 + 2] = table[index2 + 2];
      }
   }

   public static int getValue(int twoDidValue, int twoDidValue2) {
      return twoDidValue2 << 3 | twoDidValue & 7;
   }

   public static void updateTimeFlag() {
      handleAmountTwo(new Random());
      if (!peekActive) {
         isShaderCompile();
      }

      twoDidValue = System.nanoTime();
   }

   private static void handleFlag(String twoDidText) {
      peekActive = true;
   }

   private static float getTime() {
      if (twoDidValue == 0L) {
         return 1.0F;
      } else {
         float twoDidLimit = (float)(System.nanoTime() - twoDidValue) / 6.5E8F;
         if (twoDidLimit >= 1.0F) {
            return 1.0F;
         } else {
            float twoDidFactor = 1.0F - Math.max(0.0F, twoDidLimit);
            return 1.0F - twoDidFactor * twoDidFactor * twoDidFactor;
         }
      }
   }

   private static float getIntensityOpacity() {
      ThemeCustomizer instance = getInstance();
      double intensityOpacityBound = instance == null ? 0.402 : instance.intensity.getValue() * instance.opacity.getValue();
      float intensityOpacityFactor = (float)Math.max(0.0, Math.min(1.0, intensityOpacityBound));
      return 0.52F * (1.0F - (float)Math.exp(-2.9F * intensityOpacityFactor));
   }

   private static void updateAmountTwo() {
      long nanoTime = System.nanoTime();
      long twoLimit = nanoTime - lastNanoTimeTwo;
      if (!twoActive || twoLimit < 0L || twoLimit >= 1000000L) {
         boolean twoFlag = !twoActive || twoLimit < 0L || twoLimit > 250000000L;
         lastNanoTimeTwo = nanoTime;
         float twoFactor = twoFlag ? 0.0F : (float)twoLimit / 1.0E9F;
         twoCount += twoFactor;
         ThemeCustomizer instance = getInstance();
         if (instance != null && instance.ambientAnimation.isEnabled()) {
            animationPhase = animationPhase + twoFactor * instance.speed.getValue();
         }

         updateTwoCustom();
         float twoValue = twoFlag ? 1.0F : 1.0F - (float)Math.exp(-8.0F * twoFactor);

         for (int i = 0; i <= 4; i++) {
            int twoValue2 = customTwoValues[i];
            handleAmounts(i * 3, getPow(twoValue2 >> 16 & 0xFF), twoValue);
            handleAmounts(i * 3 + 1, getPow(twoValue2 >> 8 & 0xFF), twoValue);
            handleAmounts(i * 3 + 2, getPow(twoValue2 & 0xFF), twoValue);
         }

         twoActive = true;
      }
   }

   private static int getValueForInt(int twoDidValue, int releaseRightValue) {
      twoDidValue |= -16777216;
      float max3 = Math.max(MatrixAmountUtils.getAmount(twoDidValue), Math.max(MatrixAmountUtils.getAmount4(twoDidValue), MatrixAmountUtils.getAmount6(twoDidValue)));
      float max4 = MatrixAmountUtils.getMax((0.5F - max3) / 0.5F);
      return max4 <= 0.0F ? twoDidValue : MatrixAmountUtils.getValueForIntFloat(twoDidValue, 0xFF000000 | releaseRightValue, 0.85F * max4);
   }

   public static int getValue(int twoDidValue) {
      if (CardMatrixUtils.isModernFlag()) {
         return twoDidValue;
      } else {
         float twoDidValue2 = 0.16F * getAmountTime() * Math.min(1.0F, getIntensityOpacity() * 8.0F);
         if (twoDidValue2 <= 0.002F) {
            return twoDidValue;
         } else {
            int round = Math.round((twoDidValue >>> 24 & 0xFF) * (1.0F - twoDidValue2));
            return round << 24 | twoDidValue & 16777215;
         }
      }
   }

   private static ThemeCustomizer getInstance() {
      return ThemeCustomizer.singleton;
   }

   private static void updateTwoCustom() {
      ThemeCustomizer instance = getInstance();
      String pos = instance == null ? "Theme" : instance.ambientColorMode.getCurrentOption();
      int twoColorValue5 = instance == null ? 0 : instance.primary.getColor();
      int twoColorValue6 = instance == null ? 0 : instance.secondary.getColor();
      int twoColorValue7 = instance == null ? 0 : instance.main.getColor();
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      if (!twoColorFlag
         || !Objects.equals(pos, twoColorText)
         || twoColorValue5 != twoColorValue5TwoCustom
         || twoColorValue6 != twoColorValue6TwoCustom
         || twoColorValue7 != twoColorValue7TwoCustom
         || releaseRightValue != twoColorValue) {
         twoColorFlag = true;
         twoColorText = pos;
         twoColorValue5TwoCustom = twoColorValue5;
         twoColorValue6TwoCustom = twoColorValue6;
         twoColorValue7TwoCustom = twoColorValue7;
         twoColorValue = releaseRightValue;
         if ("Two Color".equals(pos)) {
            int twoCustomValue = 0xFF000000 | twoColorValue5;
            int twoCustomValue2 = 0xFF000000 | twoColorValue6;
            customTwoValues[0] = twoCustomValue;
            customTwoValues[1] = twoCustomValue2;
            customTwoValues[2] = MatrixAmountUtils.getValueForIntFloat(twoCustomValue, twoCustomValue2, 0.34F);
            customTwoValues[3] = MatrixAmountUtils.getValueForIntFloat(twoCustomValue, twoCustomValue2, 0.67F);
            customTwoValues[4] = twoCustomValue2;
         } else if ("Custom".equals(pos)) {
            handleTwoCustom(twoColorValue5);
         } else {
            int[] colors = ToxicElectricUtils.getColors();
            if (colors != null && colors.length >= 4) {
               for (int i = 0; i < 4; i++) {
                  customTwoValues[i] = getValueForInt(colors[i], releaseRightValue);
               }

               customTwoValues[4] = customTwoValues[1];
            } else {
               handleTwoCustom(releaseRightValue);
            }
         }
      }
   }

   private static float getRadius() {
      ThemeCustomizer instance = getInstance();
      return (float)((instance == null ? 1.34 : instance.radius.getValue()) / 1.34);
   }

   static {
      handleAmountTwo(new Random());
   }

   private static float getPow(int powValue) {
      return (float)Math.pow(powValue / 255.0, 2.2F);
   }

   private TwoDidUtils() {
   }

   public static void renderMinPeek(DrawContext context, int index, float peekValue, float matrixStackValue, float peekBound, float materialFactor, float wrapDegreesBound, float peekFactor) {
      if (!peekActive && index >= 0 && index < peekFields.length && !(peekBound <= 1.0F) && !(materialFactor <= 1.0F)) {
         float peekFactor2 = getAmountTime() * getIntensityOpacity() * peekFactor * CorzRoundedUtils.panelOverlayAmount;
         if (!(peekFactor2 < 0.003F)) {
            try {
               if (!compileShaderFlag && !isShaderCompile()) {
                  return;
               }

               updateAmountTwo();
               TwoDidUtils.Field field = peekFields[index];
               float time4 = getTime();
               handleAmountsAmount(field, peekValue, matrixStackValue, peekBound, materialFactor, time4);
               float max5 = Math.max(10.0F, Math.min(44.0F, Math.min(peekBound, materialFactor) * 0.2F));
               CorzRoundedUtils.handleAmbientFramebuffer(
                  MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix(),
                  peekValue,
                  matrixStackValue,
                  peekBound,
                  materialFactor,
                  wrapDegreesBound,
                  max5,
                  peekFactor2 * field.opacityScale * (0.35F + 0.65F * time4),
                  field.dotVertexData,
                  field.peekTable,
                  field.dotCount
               );
            } catch (Throwable e) {
               handleFlag(String.valueOf(e));
            }
         }
      }
   }

   public static float getAmountTime() {
      if (peekActive) {
         return 0.0F;
      } else {
         float amount = isBase() ? 1.0F : 0.0F;
         long nanoTime = System.nanoTime();
         long twoDidValue = nanoTime - lastNanoTime;
         lastNanoTime = nanoTime;
         if (!(twoDidAmount < 0.0F) && twoDidValue <= 250000000L) {
            twoDidAmount = twoDidAmount + (amount - twoDidAmount) * (1.0F - (float)Math.exp(-10.0F * ((float)twoDidValue / 1.0E9F)));
            if (Math.abs(amount - twoDidAmount) < 0.002F) {
               twoDidAmount = amount;
            }
         } else {
            twoDidAmount = amount;
         }

         return twoDidAmount;
      }
   }

   public static boolean isBase() {
      ThemeCustomizer instance = getInstance();
      return instance == null ? true : instance.ambientBackground.isEnabled();
   }

   private static void handleTwoCustom(int twoCustomValue) {
      twoCustomValue |= -16777216;
      float twoCustomLimit = MatrixAmountUtils.getAmount5(twoCustomValue);
      if (twoCustomLimit > 0.02F && twoCustomLimit < 0.64F) {
         twoCustomValue = MatrixAmountUtils.getValue(twoCustomValue, 0.0F, 0.64F / twoCustomLimit, 1.0F);
      }

      customTwoValues[0] = twoCustomValue;
      customTwoValues[1] = MatrixAmountUtils.getValue(twoCustomValue, twoCustomAmount * randomScale, 1.0F, 1.0F);
      customTwoValues[2] = MatrixAmountUtils.getValue(twoCustomValue, -twoCustomAmount * twoAmount * 0.4F, 1.14F, 0.82F);
      customTwoValues[3] = MatrixAmountUtils.getValue(twoCustomValue, -twoCustomAmount * twoAmount, 0.82F, 1.0F);
      customTwoValues[4] = MatrixAmountUtils.getValue(twoCustomValue, -30.0F, 1.0F, 1.0F);
   }

   private static void handleAmounts(int index2, float twoDidValue, float twoDidFactor) {
      table[index2] = table[index2] + (twoDidValue - table[index2]) * twoDidFactor;
   }

   private static void handleAmountTwo(Random random) {
      twoCustomAmount = random.nextBoolean() ? 1.0F : -1.0F;
      randomScale = 25.0F + 10.0F * random.nextFloat();
      twoAmount = 22.0F + 10.0F * random.nextFloat();
      twoColorFlag = false;

      for (TwoDidUtils.Field field : peekFields) {
         field.handleAmountsMin(random);
      }
   }

   private static void handleAmountsAmount(int twoDidValue, int index, int valueInt, float ambientFramebufferFactor, float matrixStackValue, float twoDidFactor, float hashHuntBound, float twoDidFactor3, float twoDidFactor4, int twoDidValue5) {
      float twoDidFactor5 = twoDidTable[index];
      float twoDidFactor6 = CORNER_OFFSETS[index];
      float twoDidFactor7 = (twoDidFactor5 - 0.5F) * 2.0F;
      float twoDidFactor8 = (twoDidFactor6 - 0.5F) * 2.0F;
      float twoDidFactor9 = twoDidFactor3 * 0.12F;
      float twoDidFactor10 = (valueInt & 0xFF) / 255.0F;
      float twoDidFactor11 = (valueInt >>> 8 & 0xFF) / 255.0F;
      float twoDidDivisor = 12.0F + 8.0F * ((valueInt >>> 16 & 0xFF) / 255.0F);
      float twoDidDivisor2 = 12.0F + 8.0F * ((valueInt >>> 24) / 255.0F);
      float sin = (float)Math.sin(twoCount * ((Math.PI * 2) / twoDidDivisor) + twoDidFactor10 * (float) (Math.PI * 2));
      float cos = (float)Math.cos(twoCount * ((Math.PI * 2) / twoDidDivisor2) + twoDidFactor11 * (float) (Math.PI * 2));
      peekTable[twoDidValue * 4] = ambientFramebufferFactor + twoDidFactor * twoDidFactor5 + twoDidFactor7 * twoDidFactor9 + (twoDidFactor7 == 0.0F ? twoDidFactor10 - 0.5F : -twoDidFactor7 * twoDidFactor10) * 0.16F * twoDidFactor + sin * twoDidFactor3 * 0.07F;
      peekTable[twoDidValue * 4 + 1] = matrixStackValue + hashHuntBound * twoDidFactor6 + twoDidFactor8 * twoDidFactor9 + (twoDidFactor8 == 0.0F ? (twoDidFactor11 - 0.5F) * 0.16F * hashHuntBound : 0.0F) + cos * twoDidFactor3 * 0.07F;
      peekTable[twoDidValue * 4 + 2] = twoDidFactor3 * 0.4F;
      int index2 = twoDidValue5 * 3;
      float twoDidBound = 0.2126F * table[index2] + 0.7152F * table[index2 + 1] + 0.0722F * table[index2 + 2];
      peekTable[twoDidValue * 4 + 3] = twoDidFactor4 * Math.min(1.0F, 0.3F / Math.max(twoDidBound, 0.001F));
      VERTEX_BUFFER[twoDidValue * 3] = table[index2];
      VERTEX_BUFFER[twoDidValue * 3 + 1] = table[index2 + 1];
      VERTEX_BUFFER[twoDidValue * 3 + 2] = table[index2 + 2];
   }

   private static int getValueInt(int twoDidValue) {
      twoDidValue ^= twoDidValue >>> 16;
      twoDidValue *= 2146121005;
      twoDidValue ^= twoDidValue >>> 15;
      twoDidValue *= -2073254261;
      return twoDidValue ^ twoDidValue >>> 16;
   }

   private static boolean isShaderCompile() {
      compileShaderFlag = true;

      try {
         if (CorzRoundedUtils.isValid()) {
            return true;
         }

         handleFlag("shader did not compile");
      } catch (Throwable e) {
         handleFlag(String.valueOf(e));
      }

      return false;
   }

   private static final class Field {
      final float[] fieldTable;
      final float opacityScale;
      final int[] table;
      final float[] peekTable;
      final int gridSize;
      final int dotCount;
      final float[] dotSizes;
      final int fieldValue;
      final float fieldAmount;
      final float[] dotVertexData;
      final float[] dotOffsetsY;
      final float[] wobbleFrequencies;
      final float[] dotAlphas;
      final float[] dotOffsetsX = new float[6];

      Field(int value, int value2, float amount, float amount2) {
         this.dotOffsetsY = new float[6];
         this.dotSizes = new float[6];
         this.dotAlphas = new float[6];
         this.wobbleFrequencies = new float[30];
         this.fieldTable = new float[30];
         this.table = new int[6];
         this.dotVertexData = new float[24];
         this.peekTable = new float[18];
         this.gridSize = value;
         this.fieldValue = value2;
         this.dotCount = Math.min(6, value * value2);
         this.opacityScale = amount;
         this.fieldAmount = amount2;
      }

      void handleAmountsMin(Random random) {
         int[] ints = new int[4];
         int index = 0;

         while (index < 4) {
            ints[index] = index++;
         }

         for (int i = 3; i > 0; i--) {
            int randomInt = random.nextInt(i + 1);
            int fieldValue = ints[i];
            ints[i] = ints[randomInt];
            ints[randomInt] = fieldValue;
         }

         int fieldFactor = this.gridSize == 1 ? 1 : 2;
         int nextInt = random.nextInt(4);

         for (int j = 0; j < this.dotCount; j++) {
            int fieldValue2 = j % this.gridSize;
            int fieldFactor2 = j / this.gridSize;
            this.dotOffsetsX[j] = (fieldValue2 + 0.5F + (random.nextFloat() - 0.5F) * 0.64F) / this.gridSize;
            this.dotOffsetsY[j] = (fieldFactor2 + 0.5F + (random.nextFloat() - 0.5F) * 0.64F) / this.fieldValue;
            this.dotSizes[j] = 0.35F + 0.4F * random.nextFloat();
            this.dotAlphas[j] = 0.58F + 0.42F * random.nextFloat();
            this.table[j] = ints[(fieldValue2 + fieldFactor2 * fieldFactor + nextInt) % 4];
            int fieldValue3 = j * 5;

            for (int k = 0; k < 4; k++) {
               this.wobbleFrequencies[fieldValue3 + k] = 0.2F + 0.34F * random.nextFloat();
               this.fieldTable[fieldValue3 + k] = random.nextFloat() * (float) (Math.PI * 2);
            }

            this.wobbleFrequencies[fieldValue3 + 4] = 0.1F + 0.16F * random.nextFloat();
            this.fieldTable[fieldValue3 + 4] = random.nextFloat() * (float) (Math.PI * 2);
         }

         int nextInt3 = random.nextInt(this.dotCount);
         this.dotAlphas[nextInt3] = 1.0F;
         this.dotSizes[nextInt3] = Math.max(this.dotSizes[nextInt3], 0.64F);
      }
   }
}
