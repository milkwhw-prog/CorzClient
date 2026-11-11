package com.corz.client.render;

import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.VertexGuiUtils;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.time.LocalDate;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.HalloweenChristmasType;

public final class SnowVertexUtils {
   public static final String SNOW_WARM_LIGHTS = "Snow, warm lights and frost, ends 2 January";
   private static final float[] snowVertexTable = new float[16];
   private static float lastMouseX;
   private static final float[] table = new float[64];
   private static final float[] LIGHT_FLASH = new float[22];
   private static final float[] LIGHT_REST_ANGLE = new float[22];
   public static final String JANUARY_SNOW_TEXT = HalloweenChristmasType.formatText("Christmas");
   private static final float[] DROP_X = new float[16];
   private static float snowVertexAmount = 6.0F;
   private static final Random RANDOM = new Random();
   private static float lastMouseY;
   private static final float[] randomTable = new float[48];
   private static int activeSparkCount;
   private static float flashTimer = 1.5F;
   private static boolean snowVertexActive = true;
   private static float fadeNearTop = 1.0F;
   private static float mouseVelocityX;
   private static boolean trianglesActive;
   private static final float[] FLAKE_FALL_SPEED = new float[48];
   private static final float[] SPARK_START_ALPHA = new float[64];
   private static boolean matrixUnscaled;
   private static float snowHeightScale;
   private static boolean interactionActive;
   private static final float[] FLAKE_SIZE = new float[48];
   private static float fadeNearEdge = 1.0F;
   private static double brushSnowAmount;
   private static final float[] LIGHT_DIR_Y = new float[22];
   private static final float[] LIGHT_Y = new float[22];
   private static double animTime;
   private static final float[] SPARK_REVEAL_THRESHOLD = new float[64];
   private static final float[] CIRCLE_SIN = new float[17];
   private static final float[] FLAKE_ALPHA = new float[48];
   private static final float[] FLAKE_WIND_X = new float[48];
   private static final float[] FLAKE_X = new float[48];
   private static final float[] LIGHT_POSITION_RATIO = new float[22];
   private static final float[] SPARK_DRIFT_X = new float[64];
   private static Matrix4f positionMatrix;
   private static final float[] LIGHT_BLINK_PERIOD = new float[22];
   private static boolean trianglesRendered;
   private static boolean hoverRequested;
   private static final boolean[] snowVertexFlags = new boolean[64];
   private static final boolean[] SPARK_ACTIVE = new boolean[64];
   private static final float[] FLAKE_SWAY_PHASE = new float[48];
   private static final float[] SPARK_END_ALPHA = new float[64];
   private static final float[] DROP_VELOCITY_X = new float[16];
   private static final float[] DROP_Y = new float[16];
   private static int snowVertexValue;
   private static final int[] LIGHT_COLORS = new int[22];
   private static float frameDeltaSeconds;
   private static float revealProgress;
   private static float trianglesAmount = 1.0F;
   private static final float[] LIGHT_DIR_X = new float[22];
   private static final float[] LIGHT_BLINK_PHASE = new float[22];
   private static final float[] PILE_HEIGHTS = new float[100];
   private static final float[] DROP_LIFETIME = new float[16];
   private static final float[] SPARK_RISE_Y = new float[64];
   private static final float[] PILE_NOISE = new float[100];
   private static final float[] FLAKE_SWAY_AMPLITUDE = new float[48];
   private static boolean brushSnowActive;
   private static long lastNanoTime;
   private static boolean randomActive;
   private static final float[] LIGHT_SWING_VELOCITY = new float[22];
   private static final int[] LIGHT_PALETTE = new int[]{-45747, -12723068, -15811, -11688193, -3626};
   private static final float[] SPARK_BASE_OFFSET = new float[64];
   private static final float[] DROP_VELOCITY_Y = new float[16];
   private static final float[] FLAKE_SWAY_PERIOD = new float[48];
   private static int nextDropIndex;
   private static float snowVertexCount;
   private static float dropSpawnBudget;
   private static final float[] LIGHT_X = new float[22];
   private static final float[] PILE_SCRATCH = new float[100];
   private static float mouseVelocityY;
   private static float lightSweepPosition = -1.0F;
   private static final float[] DROP_AGE = new float[16];
   private static final float[] CIRCLE_COS = new float[17];
   private static int lastRandomWidth;
   private static final float[] LIGHT_SWING_ANGLE = new float[22];
   private static final float[] FLAKE_WIND_Y = new float[48];

   private static void handleVertexColor(VertexConsumer vertices, float vertexColorValue, float vertexColorValue2, int vertexColorValue3, float vertexColorValue4, float deltaVar, int vertexColorValue6, float snowVertexLimit, float vertexColorValue8, int panelOverlay) {
      vertices.vertex(positionMatrix, vertexColorValue, vertexColorValue2, 0.0F).color(vertexColorValue3);
      vertices.vertex(positionMatrix, vertexColorValue4, deltaVar, 0.0F).color(vertexColorValue6);
      vertices.vertex(positionMatrix, snowVertexLimit, vertexColorValue8, 0.0F).color(panelOverlay);
   }

   private static float getAmount(float snowVertexFactor) {
      snowVertexFactor = getMin(snowVertexFactor);
      return snowVertexFactor * snowVertexFactor * (3.0F - 2.0F * snowVertexFactor);
   }

   public static void handleTrianglesMax(int trianglesValue, int randomValue, int matrixMatricesValue, int heightValue, float panelOverlayLimit) {
      if (!(trianglesAmount <= 0.004F) && (trianglesActive || isBaseInstance())) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(panelOverlayLimit);

         try {
            updatePositionMatrix();
            VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
            handleAmountsMin(beginBuffer, trianglesValue, randomValue, matrixMatricesValue, heightValue);
            VertexGuiUtils.updateBufferClose();
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
         }
      }
   }

   private static float getAmount(float snowVertexValue, float snowVertexValue2, float snowVertexValue3) {
      return getAmount((snowVertexValue3 - snowVertexValue) / (snowVertexValue2 - snowVertexValue));
   }

   private static void handleAmountsRandom(int randomValue, int randomValue2, int snowVertexFactor, int value, int randomFactor) {
      randomActive = true;

      for (int i = 0; i < 100; i++) {
         float randomBound = 3.1F + 0.65F * (float)Math.sin(0.13 * i + 0.4) + 0.4F * (float)Math.sin(0.37 * i + 2.1) + 0.2F * getAmount(i, 3);
         PILE_HEIGHTS[i] = Math.max(1.4F, Math.min(4.2F, randomBound));
      }

      for (int j = 0; j < 48; j++) {
         handleRandomAmounts(j, value, randomFactor, true);
         if (j >= 30 && FLAKE_X[j] > randomValue && FLAKE_X[j] < randomValue + snowVertexFactor && randomTable[j] > randomValue2 - 4) {
            randomTable[j] = RANDOM.nextFloat() * Math.max(0.0F, (float)(randomValue2 - 8));
         }
      }

      lastRandomWidth = value;
      snowVertexValue = randomFactor;
   }

   private static float getAmount(float snowVertexValue, int snowVertexValue2, int snowVertexFactor) {
      float snowVertexLimit = (snowVertexValue - (snowVertexValue2 + 6.0F)) / ((snowVertexFactor - 12.0F) / 99.0F);
      if (!(snowVertexLimit <= 0.0F) && !(snowVertexLimit >= 99.0F)) {
         int snowVertexLimit4 = (int)snowVertexLimit;
         float deltaVar = snowVertexLimit - snowVertexLimit4;
         return getAmounts(snowVertexLimit4, snowVertexValue2, snowVertexFactor) * (1.0F - deltaVar) + getAmounts(snowVertexLimit4 + 1, snowVertexValue2, snowVertexFactor) * deltaVar;
      } else {
         return 0.0F;
      }
   }

   private static float getMin(float snowVertexValue, boolean snowVertexFlag) {
      return snowVertexFlag ? snowVertexValue + (0.25F - snowVertexValue) * Math.min(1.0F, 6.0F * frameDeltaSeconds) : Math.min(1.0F, snowVertexValue + 0.25F * frameDeltaSeconds);
   }

   private static int getAmounts() {
      for (int i = 0; i < 16; i++) {
         int index = (nextDropIndex + i) % 16;
         if (DROP_LIFETIME[index] <= 0.0F) {
            nextDropIndex = (index + 1) % 16;
            return index;
         }
      }

      return -1;
   }

   public static boolean isChristmas() {
      return HalloweenChristmasType.getTimeInstance() == HalloweenChristmasType.CHRISTMAS;
   }

   private static void handleAmounts(int snowVertexValue, float snowVertexFactor) {
      for (int i = -2; i <= 2; i++) {
         int index = snowVertexValue + i;
         if (index >= 0 && index < 100) {
            float snowVertexFactor2 = i == 0 ? 0.4F : (Math.abs(i) == 1 ? 0.2F : 0.1F);
            float snowVertexFactor3 = PILE_HEIGHTS[index] / 4.4F;
            float max4 = Math.max(0.0F, 1.0F - snowVertexFactor3 * snowVertexFactor3 * snowVertexFactor3);
            PILE_HEIGHTS[index] = Math.min(4.4F, PILE_HEIGHTS[index] + snowVertexFactor * snowVertexFactor2 * max4);
         }
      }
   }

   private static float getAmount(float snowVertexValue, float snowVertexValue2, int snowVertexValue3, int randomValue, int snowVertexValue5, int snowVertexValue6, int snowVertexValue7) {
      float snowVertexValue8 = snowVertexValue5 / 2.0F - snowVertexValue7;
      float snowVertexValue9 = snowVertexValue6 / 2.0F - snowVertexValue7;
      float snowVertexBound = Math.abs(snowVertexValue - (snowVertexValue3 + snowVertexValue5 / 2.0F)) - snowVertexValue8;
      float pilotStatusBound = Math.abs(snowVertexValue2 - (randomValue + snowVertexValue6 / 2.0F)) - snowVertexValue9;
      float snowVertexFactor = Math.max(snowVertexBound, 0.0F);
      float max2 = Math.max(pilotStatusBound, 0.0F);
      return (float)Math.sqrt(snowVertexFactor * snowVertexFactor + max2 * max2) + Math.min(Math.max(snowVertexBound, pilotStatusBound), 0.0F) - snowVertexValue7;
   }

   public static void handleTrianglesFlag(int trianglesValue, int trianglesValue2, int snowVertexFactor, int trianglesValue4, int trianglesValue5, int randomValue, int randomFactor, int matrixMatricesValue, int heightValue) {
      if (canMinMax(trianglesValue, trianglesValue2, snowVertexFactor, trianglesValue4, randomValue, randomFactor, matrixMatricesValue, heightValue)) {
         trianglesRendered = true;
         updatePositionMatrix();
         VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
         handleAmountsMax(beginBuffer, trianglesValue, trianglesValue2, snowVertexFactor, trianglesValue4, trianglesValue5);
         VertexGuiUtils.updateBufferClose();
      }
   }

   private static void handleAmountsMin(VertexConsumer vertices, int snowVertexValue, int randomValue, int matrixMatricesValue, int heightValue) {
      float snowVertexValue5 = 1.0F - (1.0F - revealProgress) * (1.0F - revealProgress) * (1.0F - revealProgress);

      for (int i = 0; i < activeSparkCount; i++) {
         boolean snowVertexFlag = SPARK_ACTIVE[i];
         float snowVertexFactor = trianglesAmount * (snowVertexFlag ? fadeNearTop : fadeNearEdge);
         float snowVertexFactor2 = snowVertexFlags[i] ? getMin(snowVertexValue5 * 1.15F) : getAmount((snowVertexValue5 - SPARK_REVEAL_THRESHOLD[i]) / 0.2F);
         if (!(snowVertexFactor <= 0.01F) && !(snowVertexFactor2 <= 0.01F)) {
            float snowVertexValue6 = snowVertexFlag ? snowVertexValue + matrixMatricesValue - 3.5F : snowVertexValue + 3.5F;
            float snowVertexValue7 = randomValue + heightValue - 3.5F;
            float snowVertexFactor3 = snowVertexFlag ? -1.0F : 1.0F;
            float snowVertexValue8 = snowVertexValue6 + snowVertexFactor3 * SPARK_BASE_OFFSET[i];
            float snowVertexValue9 = snowVertexValue7 - table[i];
            float snowVertexValue10 = snowVertexValue8 + snowVertexFactor3 * SPARK_DRIFT_X[i] * snowVertexFactor2;
            float snowVertexValue11 = snowVertexValue9 - SPARK_RISE_Y[i] * snowVertexFactor2;
            float snowVertexFactor4 = SPARK_START_ALPHA[i] + (SPARK_END_ALPHA[i] - SPARK_START_ALPHA[i]) * snowVertexFactor2;
            handleMax(
               vertices,
               snowVertexValue8,
               snowVertexValue9,
               snowVertexValue10,
               snowVertexValue11,
               0.28F,
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-2692873, SPARK_START_ALPHA[i] * snowVertexFactor)),
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-2692873, snowVertexFactor4 * snowVertexFactor))
            );
         }
      }
   }

   private static void handleMax(VertexConsumer vertices, int snowVertexValue, int snowVertexValue2, int snowVertexFactor) {
      int releaseRight = getReleaseRight();
      int snowVertexValue4 = MatrixAmountUtils.getValue(-3943708, releaseRight, 0.3F);
      float trianglesAmount22 = trianglesAmount;
      float snowVertexValue5 = canFlag(snowVertexValue2) ? 0.0F : 0.3F;

      for (int i = 0; i < 99; i++) {
         float snowVertexValue6 = getAmount(i, snowVertexValue, snowVertexFactor);
         float snowVertexLimit = getAmount(i + 1, snowVertexValue, snowVertexFactor);
         float snowVertexBound = getAmounts(i, snowVertexValue, snowVertexFactor);
         float hashHuntBound = getAmounts(i + 1, snowVertexValue, snowVertexFactor);
         if (!(snowVertexBound < 0.05F) || !(hashHuntBound < 0.05F)) {
            float amountForFloatInt = getAmountForFloatInt(snowVertexValue6, snowVertexValue, snowVertexValue2, snowVertexFactor);
            float amountForFloatInt9 = getAmountForFloatInt(snowVertexLimit, snowVertexValue, snowVertexValue2, snowVertexFactor);
            float deltaVar = amountForFloatInt - snowVertexBound;
            float deltaVar2 = amountForFloatInt9 - hashHuntBound;
            int amounts10 = getAmounts(snowVertexValue4, snowVertexValue6, snowVertexValue, snowVertexFactor, 0.62F * trianglesAmount22 * Math.min(1.0F, snowVertexBound));
            int amounts11 = getAmounts(snowVertexValue4, snowVertexLimit, snowVertexValue, snowVertexFactor, 0.62F * trianglesAmount22 * Math.min(1.0F, hashHuntBound));
            int panelOverlay = CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-854277, 0.88F * trianglesAmount22 * Math.min(1.0F, snowVertexBound * 1.5F)));
            int panelOverlay2 = CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-854277, 0.88F * trianglesAmount22 * Math.min(1.0F, hashHuntBound * 1.5F)));
            handleVertexColor(vertices, snowVertexValue6, amountForFloatInt + snowVertexValue5, amounts10, snowVertexLimit, amountForFloatInt9 + snowVertexValue5, amounts11, snowVertexLimit, deltaVar2, panelOverlay2);
            handleVertexColor(vertices, snowVertexValue6, amountForFloatInt + snowVertexValue5, amounts10, snowVertexLimit, deltaVar2, panelOverlay2, snowVertexValue6, deltaVar, panelOverlay);
            handleVertexColor(vertices, snowVertexValue6, deltaVar, panelOverlay, snowVertexLimit, deltaVar2, panelOverlay2, snowVertexLimit, deltaVar2 - 1.1F, panelOverlay2 & 16777215);
            handleVertexColor(vertices, snowVertexValue6, deltaVar, panelOverlay, snowVertexLimit, deltaVar2 - 1.1F, panelOverlay2 & 16777215, snowVertexValue6, deltaVar - 1.1F, panelOverlay & 16777215);
         }
      }
   }

   private static boolean canMinMax(int snowVertexValue, int snowVertexValue2, int snowVertexFactor, int snowVertexValue4, int randomValue, int randomFactor, int matrixMatricesValue, int heightValue) {
      boolean baseInstance = isBaseInstance();
      if (!baseInstance && !trianglesActive) {
         lastNanoTime = 0L;
         return false;
      } else {
         long nanoTime = System.nanoTime();
         long snowVertexLimit = lastNanoTime == 0L ? Long.MAX_VALUE : nanoTime - lastNanoTime;
         lastNanoTime = nanoTime;
         boolean snowVertexFlag = snowVertexActive;
         boolean snowVertexFlag2 = snowVertexFlag || snowVertexLimit > 250000000L;
         snowVertexActive = false;
         frameDeltaSeconds = snowVertexFlag2 ? 0.0F : Math.min(0.05F, (float)snowVertexLimit / 1.0E9F);
         animTime = animTime + frameDeltaSeconds;
         if (!baseInstance && snowVertexFlag2) {
            trianglesActive = false;
            trianglesAmount = 1.0F;
            return false;
         } else {
            if (baseInstance && !randomActive) {
               handleAmountsRandom(snowVertexValue, snowVertexValue2, snowVertexFactor, randomValue, randomFactor);
            }

            boolean snowVertexFlag3 = baseInstance && !trianglesActive;
            if (baseInstance) {
               trianglesActive = true;
            }

            if (snowVertexFlag2) {
               hoverRequested = false;
            }

            if (baseInstance && (snowVertexFlag || snowVertexFlag3)) {
               trianglesAmount = snowVertexFlag2 && !(CorzRoundedUtils.getPanelOverlay() > 0.9F) ? 1.0F : 0.0F;
            }

            if (snowVertexFlag3) {
               brushSnowAmount = 0.0;
            }

            matrixUnscaled = Math.abs(MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix().m00() - 1.0F) < 0.001F && !MatrixMatricesScreen.isMatrixMatricesActive();
            handleAmounts(randomValue, randomFactor);
            if (baseInstance) {
               trianglesAmount = trianglesAmount + (1.0F - trianglesAmount) * (1.0F - (float)Math.exp(-5.0F * frameDeltaSeconds));
               brushSnowAmount = brushSnowAmount + frameDeltaSeconds;
               revealProgress = Math.min(1.0F, revealProgress + frameDeltaSeconds / 6.0F);
            } else {
               trianglesAmount = Math.max(0.0F, trianglesAmount - 3.2F * frameDeltaSeconds);
               if (trianglesAmount <= 0.004F) {
                  trianglesActive = false;
                  trianglesAmount = 1.0F;
                  return false;
               }
            }

            handleAmountMin(matrixMatricesValue, heightValue);
            handleAmountsAmount(snowVertexValue, snowVertexValue2, snowVertexFactor);
            handleAmountsAmount(snowVertexValue, snowVertexValue2, snowVertexFactor, snowVertexValue4, randomValue, randomFactor);
            handleAmountsMin(snowVertexValue, snowVertexValue2, snowVertexFactor, matrixMatricesValue, heightValue);
            handleAmountsMin(snowVertexValue, snowVertexValue2, snowVertexFactor);
            handleAmountFlag(snowVertexValue, snowVertexValue2, snowVertexFactor, snowVertexValue4, matrixMatricesValue, heightValue);
            lastMouseX = matrixMatricesValue;
            lastMouseY = heightValue;
            return true;
         }
      }
   }

   private static float getAmountsAmount(int index) {
      float snowVertexTotal = 0.8F + 0.14F * (float)Math.sin(6.2831855 * animTime / LIGHT_BLINK_PERIOD[index] + LIGHT_BLINK_PHASE[index]);
      if (lightSweepPosition >= -0.1F) {
         float snowVertexFactor = (LIGHT_POSITION_RATIO[index] - lightSweepPosition) / 0.07F;
         snowVertexTotal += 0.45F * (float)Math.exp(-snowVertexFactor * snowVertexFactor);
      }

      return snowVertexTotal + 0.55F * LIGHT_FLASH[index];
   }

   private static int getValue(float snowVertexValue, int snowVertexValue2, int snowVertexFactor) {
      float glfwLimit = (snowVertexValue - (snowVertexValue2 + 6.0F)) / ((snowVertexFactor - 12.0F) / 99.0F);
      return Math.max(0, Math.min(99, Math.round(glfwLimit)));
   }

   private static void handleAmountsFlags(boolean snowVertexFlag, boolean snowVertexFlag2, float snowVertexValue, float snowVertexValue2, float snowVertexValue3, float snowVertexValue4, float snowVertexValue5, float snowVertexValue6, float snowVertexValue7) {
      int index = activeSparkCount++;
      SPARK_ACTIVE[index] = snowVertexFlag;
      snowVertexFlags[index] = snowVertexFlag2;
      SPARK_REVEAL_THRESHOLD[index] = snowVertexValue;
      SPARK_BASE_OFFSET[index] = snowVertexValue2;
      table[index] = snowVertexValue3;
      SPARK_DRIFT_X[index] = snowVertexValue4;
      SPARK_RISE_Y[index] = snowVertexValue5;
      SPARK_START_ALPHA[index] = snowVertexValue6;
      SPARK_END_ALPHA[index] = snowVertexValue7;
   }

   private static void handleAmountsMax(VertexConsumer vertices, int snowVertexValue, int randomValue, int snowVertexValue3, int snowVertexValue4, int snowVertexValue5) {
      boolean modernFlag = CardMatrixUtils.isModernFlag();
      int releaseRight = getReleaseRight();
      int snowVertexValue6 = MatrixAmountUtils.getValue(-16184562, -854277, 0.78F);

      for (int i = 0; i < 48; i++) {
         boolean snowVertexFlag = i >= 30;
         float amountsAmount = getAmountsAmountForInt(i);
         float snowVertexValue7 = randomTable[i];
         float snowVertexLimit = getAmount(amountsAmount, snowVertexValue7, snowVertexValue, randomValue, snowVertexValue3, snowVertexValue4, snowVertexValue5);
         float snowVertexLimit2 = getAmount(-snowVertexLimit / 2.2F);
         if (!(snowVertexLimit2 >= 0.999F) || !snowVertexFlag && modernFlag) {
            float snowVertexValue8 = FLAKE_SIZE[i];
            float snowVertexFactor = FLAKE_ALPHA[i] * trianglesAmount;
            float snowHeightScale9 = snowHeightScale;
            int matrixValue = snowVertexFlag ? -854277 : snowVertexValue6;
            if (snowVertexLimit > 0.0F && snowVertexLimit < 36.0F) {
               float matrixFactor = 0.5F * (1.0F - snowVertexLimit / 36.0F) * (1.0F - snowVertexLimit / 36.0F);
               matrixValue = MatrixAmountUtils.getValue(matrixValue, releaseRight, matrixFactor);
               snowVertexFactor *= 1.0F + 0.3F * matrixFactor;
            }

            float snowVertexBound;
            if (modernFlag && !snowVertexFlag) {
               snowVertexValue8 *= 1.0F + 0.8F * snowVertexLimit2;
               snowHeightScale9 *= 1.0F + 1.5F * snowVertexLimit2;
               snowVertexBound = snowVertexFactor * (1.0F - 0.65F * snowVertexLimit2);
            } else {
               snowVertexBound = snowVertexFactor * (1.0F - snowVertexLimit2);
            }

            if (!(snowVertexBound <= 0.004F)) {
               handleAmounts(vertices, amountsAmount, snowVertexValue7, snowVertexValue8, snowHeightScale9, CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(matrixValue, Math.min(1.0F, snowVertexBound))));
            }
         }
      }
   }

   private static float getAmount(float snowVertexValue, int snowVertexValue2, int snowVertexValue3, int snowVertexFactor) {
      float snowVertexValue5 = canFlag(snowVertexValue3) ? snowVertexValue3 + 1.2F : snowVertexValue3 - 0.2F;
      float snowVertexDivisor = (snowVertexFactor - 24.0F) / 11.0F;
      float hashRegistryValue = (snowVertexValue - (snowVertexValue2 + 12.0F)) / snowVertexDivisor;
      float snowVertexLimit = hashRegistryValue - (float)Math.floor(hashRegistryValue);
      return snowVertexValue5 + 2.0F * (float)Math.sin(Math.PI * getMin(snowVertexLimit));
   }

   private static void handleAmountsMin(int snowVertexValue, int snowVertexValue2, int snowVertexValue3, int snowVertexValue4, int snowVertexValue5) {
      float snowVertexLimit = 1.0F - (float)Math.exp(-1.2F * frameDeltaSeconds);
      if (snowVertexLimit > 0.0F) {
         for (int i = 0; i < 100; i++) {
            float snowVertexValue6 = PILE_HEIGHTS[Math.max(0, i - 1)];
            float snowVertexValue7 = PILE_HEIGHTS[Math.min(99, i + 1)];
            PILE_SCRATCH[i] = PILE_HEIGHTS[i] + (0.5F * (snowVertexValue6 + snowVertexValue7) - PILE_HEIGHTS[i]) * snowVertexLimit;
         }

         System.arraycopy(PILE_SCRATCH, 0, PILE_HEIGHTS, 0, 100);
      }

      if (interactionActive && !(frameDeltaSeconds <= 0.0F) && !(trianglesAmount * CorzRoundedUtils.getPanelOverlay() < 0.6F)) {
         float snowVertexValue8 = canFlag(snowVertexValue2) ? snowVertexValue2 + 6.0F : snowVertexValue2;
         float snowVertexLimit2 = snowVertexValue8 - 7.2F;
         float snowVertexValue9 = snowVertexValue8 + 3.0F;
         float lastMouseX10 = lastMouseX;
         float lastMouseY32 = lastMouseY;
         float px2 = snowVertexValue4;
         float mx2 = snowVertexValue5;
         float distance = (float)Math.sqrt(mouseVelocityX * mouseVelocityX + mouseVelocityY * mouseVelocityY);
         if (!(distance < 25.0F)) {
            if ((!(lastMouseY32 < snowVertexLimit2) || !(mx2 < snowVertexLimit2)) && (!(lastMouseY32 > snowVertexValue9) || !(mx2 > snowVertexValue9))) {
               float max3 = 0.0F;
               float snowVertexFactor = 1.0F;
               if (Math.abs(mx2 - lastMouseY32) > 1.0E-4F) {
                  float snowVertexBound = (snowVertexLimit2 - lastMouseY32) / (mx2 - lastMouseY32);
                  float snowVertexBound2 = (snowVertexValue9 - lastMouseY32) / (mx2 - lastMouseY32);
                  max3 = Math.max(0.0F, Math.min(snowVertexBound, snowVertexBound2));
                  snowVertexFactor = Math.min(1.0F, Math.max(snowVertexBound, snowVertexBound2));
                  if (max3 > snowVertexFactor) {
                     return;
                  }
               }

               float wrapDegreesBound = lastMouseX10 + (px2 - lastMouseX10) * max3;
               float hashHuntBound = lastMouseX10 + (px2 - lastMouseX10) * snowVertexFactor;
               float max4 = Math.max(snowVertexValue + 6.0F, Math.min(wrapDegreesBound, hashHuntBound));
               float min11 = Math.min(snowVertexValue + snowVertexValue3 - 6.0F, Math.max(wrapDegreesBound, hashHuntBound));
               if (!(max4 > min11 + 8.0F)) {
                  float max2 = Math.max(0.25F, Math.min(0.92F, distance / 90.0F));
                  float exponent = frameDeltaSeconds * 60.0F;
                  float snowVertexTotal = 0.0F;

                  for (int j = 0; j < 100; j++) {
                     float snowVertexLimit5 = getAmount(j, snowVertexValue, snowVertexValue3);
                     float snowVertexLimit6 = snowVertexLimit5 < max4 ? max4 - snowVertexLimit5 : (snowVertexLimit5 > min11 ? snowVertexLimit5 - min11 : 0.0F);
                     if (!(snowVertexLimit6 >= 8.0F)) {
                        float snowVertexFactor3 = 1.0F - snowVertexLimit6 / 8.0F * (snowVertexLimit6 / 8.0F);
                        float snowVertexFactor4 = 1.0F - (float)Math.pow(1.0F - max2 * snowVertexFactor3 * snowVertexFactor3, exponent);
                        float snowVertexValue12 = PILE_HEIGHTS[j] * snowVertexFactor4;
                        PILE_HEIGHTS[j] = PILE_HEIGHTS[j] - snowVertexValue12;
                        snowVertexTotal += snowVertexValue12;
                     }
                  }

                  if (!(snowVertexTotal <= 0.0F)) {
                     dropSpawnBudget = Math.min(3.0F, dropSpawnBudget + snowVertexTotal);
                     snowVertexCount = Math.min(3.0F, snowVertexCount + 30.0F * frameDeltaSeconds);

                     while (dropSpawnBudget >= 0.3F && snowVertexCount >= 1.0F) {
                        int index = getAmounts();
                        if (index < 0) {
                           break;
                        }

                        dropSpawnBudget -= 0.3F;
                        snowVertexCount--;
                        float snowVertexValue13 = max4 + Math.max(0.0F, min11 - max4) * RANDOM.nextFloat();
                        DROP_X[index] = snowVertexValue13;
                        DROP_Y[index] = getAmountForFloatInt(snowVertexValue13, snowVertexValue, snowVertexValue2, snowVertexValue3) - getAmount(snowVertexValue13, snowVertexValue, snowVertexValue3);
                        DROP_VELOCITY_X[index] = Math.max(-110.0F, Math.min(110.0F, 0.45F * mouseVelocityX)) + (RANDOM.nextFloat() - 0.5F) * 36.0F;
                        DROP_VELOCITY_Y[index] = Math.max(-85.0F, -(16.0F + 34.0F * RANDOM.nextFloat()) - 0.07F * Math.abs(mouseVelocityX));
                        DROP_AGE[index] = 0.0F;
                        DROP_LIFETIME[index] = (0.55F + 0.35F * RANDOM.nextFloat()) * (canFlag(snowVertexValue2) ? 0.6F : 1.0F);
                        snowVertexTable[index] = 0.5F + 0.5F * RANDOM.nextFloat();
                     }
                  }
               }
            }
         }
      }
   }

   private static int getReleaseRight() {
      return MatrixAmountUtils.getValue(-13942, 0xFF000000 | MatrixEspUtils.releaseRightValue & 16777215, 0.12F);
   }

   private static void updateBrushSnow() {
      if (!brushSnowActive && !(brushSnowAmount < 2.2) && !(trianglesAmount < 0.99F) && !(CorzRoundedUtils.getPanelOverlay() < 0.99F) && isBaseInstance()) {
         ThemeCustomizer singleton = ThemeCustomizer.singleton;
         if (MinecraftClient.getInstance().currentScreen instanceof MatrixMatricesScreen) {
            brushSnowActive = true;
            LocalDate now = LocalDate.now();
            boolean brushSnowFlag = HalloweenChristmasType.getHalloweenChristmas(now) != HalloweenChristmasType.CHRISTMAS;
            int year = HalloweenChristmasType.getYear(now);
            if (brushSnowFlag || Math.round(singleton.seasonToastYear.getValue()) != year) {
               if (!brushSnowFlag) {
                  singleton.seasonToastYear.handleMaxAmount(year);
               }

               DisabledEnabled.getInstance().handleDisabledEnabled(JANUARY_SNOW_TEXT, "Brush the snow off the top edge", true);
            }
         }
      }
   }

   private static float getAmountForFloatInt(float snowVertexLimit, int snowVertexValue, int snowVertexValue2, int snowVertexValue3) {
      if (canFlag(snowVertexValue2)) {
         return snowVertexValue2 + 6.0F;
      } else {
         float snowVertexFactor = 12.0F;
         if (snowVertexLimit < snowVertexValue + snowVertexFactor) {
            float snowVertexFactor2 = snowVertexValue + snowVertexFactor - snowVertexLimit;
            return snowVertexValue2 + snowVertexFactor - (float)Math.sqrt(Math.max(0.0F, snowVertexFactor * snowVertexFactor - snowVertexFactor2 * snowVertexFactor2));
         } else if (snowVertexLimit > snowVertexValue + snowVertexValue3 - snowVertexFactor) {
            float snowVertexFactor3 = snowVertexLimit - (snowVertexValue + snowVertexValue3 - snowVertexFactor);
            return snowVertexValue2 + snowVertexFactor - (float)Math.sqrt(Math.max(0.0F, snowVertexFactor * snowVertexFactor - snowVertexFactor3 * snowVertexFactor3));
         } else {
            return snowVertexValue2;
         }
      }
   }

   private static void handleAmountsMax(VertexConsumer vertices) {
      for (int i = 0; i < 16; i++) {
         if (!(DROP_LIFETIME[i] <= 0.0F)) {
            float snowVertexBound = 1.0F - DROP_AGE[i] / DROP_LIFETIME[i];
            float snowVertexValue = 0.75F * (float)Math.pow(Math.max(0.0F, snowVertexBound), 1.4) * trianglesAmount;
            handleAmounts(vertices, DROP_X[i], DROP_Y[i], snowVertexTable[i], Math.max(snowHeightScale, 0.8F), CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-854277, snowVertexValue)));
         }
      }
   }

   private static float getAmount(int snowVertexValue, int snowVertexValue2) {
      double sinScaled = Math.sin(snowVertexValue * 12.9898 + snowVertexValue2 * 78.233) * 43758.5453;
      return (float)(sinScaled - Math.floor(sinScaled));
   }

   private static void handleAmountsMax(VertexConsumer vertices, int snowVertexValue, int snowVertexValue2, int snowVertexValue3) {
      float snowVertexBound = trianglesAmount;
      int panelOverlay = CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-15325666, 0.92F * snowVertexBound));
      float snowVertexValue4 = snowVertexValue + 12.0F;
      float snowVertexValue5 = snowVertexValue + snowVertexValue3 - 12.0F;
      byte snowVertexLimit = 120;
      float snowVertexValue6 = snowVertexValue4;
      float snowVertexValue7 = getAmount(snowVertexValue4, snowVertexValue, snowVertexValue2, snowVertexValue3);

      for (int i = 1; i <= snowVertexLimit; i++) {
         float snowVertexValue8 = snowVertexValue4 + (snowVertexValue5 - snowVertexValue4) * i / snowVertexLimit;
         float snowVertexValue9 = getAmount(snowVertexValue8, snowVertexValue, snowVertexValue2, snowVertexValue3);
         handleMax(vertices, snowVertexValue6, snowVertexValue7, snowVertexValue8, snowVertexValue9, 0.32F, panelOverlay, panelOverlay);
         snowVertexValue6 = snowVertexValue8;
         snowVertexValue7 = snowVertexValue9;
      }

      int panelOverlay2 = CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-13878735, snowVertexBound));
      float hashHuntBound = CorzRoundedUtils.getPanelOverlay();

      for (int j = 0; j < 22; j++) {
         float snowVertexValue10 = LIGHT_DIR_X[j];
         float snowVertexValue11 = LIGHT_DIR_Y[j];
         handleMax(vertices, LIGHT_X[j], LIGHT_Y[j], LIGHT_X[j] + snowVertexValue10 * 1.1F, LIGHT_Y[j] + snowVertexValue11 * 1.1F, 0.62F, panelOverlay2, panelOverlay2);
         float snowVertexFactor = getAmount(j);
         float matrixFactor = getMin(getAmountsAmount(j) * snowVertexFactor);
         int releaseRight = getValue(LIGHT_COLORS[j], Math.min(snowVertexFactor, hashHuntBound));
         int matrixValue = MatrixAmountUtils.getValue(LIGHT_COLORS[j], -15723496, 0.62F);
         int snowVertexValue14 = MatrixAmountUtils.getValue(matrixValue, releaseRight, matrixFactor);
         int snowVertexValue15 = MatrixAmountUtils.getValue(snowVertexValue14, -1, (0.35F + 0.3F * LIGHT_FLASH[j]) * matrixFactor);
         handleAmountsMax(
            vertices,
            getAmountsForInt(j),
            getAmounts(j),
            snowVertexValue10,
            snowVertexValue11,
            CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue15, snowVertexBound)),
            CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue14, snowVertexBound))
         );
         float snowVertexValue16 = getAmountsForInt(j) - snowVertexValue11 * 0.42F - snowVertexValue10 * 0.55F;
         float snowVertexValue17 = getAmounts(j) + snowVertexValue10 * 0.42F - snowVertexValue11 * 0.55F;
         handleAmounts(vertices, snowVertexValue16, snowVertexValue17, 0.32F, 0.35F, CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(-1, (0.25F + 0.35F * matrixFactor) * snowVertexBound)));
      }
   }

   private static void handleAmountsMin(int snowVertexValue, int randomValue, int snowVertexFactor) {
      float exp = (float)Math.exp(-2.4F * frameDeltaSeconds);
      float snowVertexValue4 = canFlag(randomValue) ? randomValue + 6.0F : randomValue;

      for (int i = 0; i < 16; i++) {
         if (!(DROP_LIFETIME[i] <= 0.0F)) {
            DROP_AGE[i] = DROP_AGE[i] + frameDeltaSeconds;
            DROP_VELOCITY_Y[i] = DROP_VELOCITY_Y[i] + 120.0F * frameDeltaSeconds;
            DROP_VELOCITY_X[i] = DROP_VELOCITY_X[i] * exp;
            DROP_X[i] = DROP_X[i] + DROP_VELOCITY_X[i] * frameDeltaSeconds;
            DROP_Y[i] = DROP_Y[i] + DROP_VELOCITY_Y[i] * frameDeltaSeconds;
            boolean snowVertexFlag = DROP_X[i] > snowVertexValue + 6.0F && DROP_X[i] < snowVertexValue + snowVertexFactor - 6.0F;
            if (snowVertexFlag && DROP_VELOCITY_Y[i] > 0.0F && DROP_Y[i] >= getAmountForFloatInt(DROP_X[i], snowVertexValue, randomValue, snowVertexFactor) - getAmount(DROP_X[i], snowVertexValue, snowVertexFactor)) {
               handleAmounts(getValue(DROP_X[i], snowVertexValue, snowVertexFactor), 0.15F);
               DROP_LIFETIME[i] = 0.0F;
            } else {
               boolean snowVertexFlag2 = DROP_X[i] > snowVertexValue && DROP_X[i] < snowVertexValue + snowVertexFactor;
               if (DROP_AGE[i] >= DROP_LIFETIME[i] || snowVertexFlag2 && DROP_Y[i] > snowVertexValue4) {
                  DROP_LIFETIME[i] = 0.0F;
               }
            }
         }
      }
   }

   private static void handle(boolean snowVertexFlag, float snowVertexFactor, float[] snowVertexValues, float[] snowVertexValues2, float[] snowVertexValues3) {
      for (int i = 0; i < snowVertexValues.length && activeSparkCount < 64; i++) {
         double angle = Math.toRadians(snowVertexValues[i]);
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float snowVertexFactor2 = snowVertexFactor * snowVertexValues2[i];
         handleAmountsFlags(snowVertexFlag, true, 0.0F, 0.0F, 0.0F, cos * snowVertexFactor2, sin * snowVertexFactor2, 0.16F, 0.04F);

         for (int j = 0; j < snowVertexValues3.length && activeSparkCount < 64; j++) {
            float snowVertexFactor3 = snowVertexValues3[j];
            double angle2 = angle + (j % 2 == 0 ? 1 : -1) * Math.toRadians(58.0);
            float snowVertexFactor4 = 0.3F * snowVertexFactor2 * (1.0F - snowVertexFactor3) + 1.0F;
            handleAmountsFlags(
               snowVertexFlag, false, snowVertexFactor3, cos * snowVertexFactor2 * snowVertexFactor3, sin * snowVertexFactor2 * snowVertexFactor3, (float)Math.cos(angle2) * snowVertexFactor4, (float)Math.sin(angle2) * snowVertexFactor4, 0.11F, 0.0F
            );
         }
      }
   }

   private static float getAmount(float snowVertexValue, float snowVertexValue2, float snowVertexValue3, float snowVertexValue4) {
      float snowVertexFactor = snowVertexValue - snowVertexValue3;
      float snowVertexFactor2 = snowVertexValue2 - snowVertexValue4;
      return (float)Math.sqrt(snowVertexFactor * snowVertexFactor + snowVertexFactor2 * snowVertexFactor2);
   }

   private static void handleAmounts(int value, int randomFactor) {
      if (lastRandomWidth > 0 && snowVertexValue > 0 && (value != lastRandomWidth || randomFactor != snowVertexValue)) {
         float snowVertexFactor = (float)value / lastRandomWidth;
         float snowVertexFactor2 = (float)randomFactor / snowVertexValue;

         for (int i = 0; i < 48; i++) {
            FLAKE_X[i] = FLAKE_X[i] * snowVertexFactor;
            randomTable[i] = randomTable[i] * snowVertexFactor2;
         }
      }

      lastRandomWidth = value;
      snowVertexValue = randomFactor;
   }

   private static float getAmounts(int index) {
      return LIGHT_Y[index] + LIGHT_DIR_Y[index] * 2.63F;
   }

   private static void handleAmountsMaxForVertexConsumer(VertexConsumer vertices) {
      float panelOverlay = CorzRoundedUtils.getPanelOverlay();

      for (int i = 0; i < 22; i++) {
         float snowVertexFactor = getAmount(i);
         if (!(snowVertexFactor <= 0.002F)) {
            float snowVertexValue = getAmountsAmount(i) * snowVertexFactor * trianglesAmount;
            int snowVertexValue2 = getValue(LIGHT_COLORS[i], Math.min(snowVertexFactor, panelOverlay));
            float amountsForInt = getAmountsForInt(i);
            float amounts4 = getAmounts(i);
            float snowVertexValue5 = 0.07F * snowVertexValue;
            handleAmounts(
               vertices,
               amountsForInt,
               amounts4 + 1.5F,
               16.0F,
               10.0F,
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue2, snowVertexValue5)),
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue2, snowVertexValue5 * 0.5F))
            );
            float snowVertexBound = Math.min(0.55F, 0.3F * snowVertexValue);
            float snowVertexValue7 = 1.0F + 0.25F * LIGHT_FLASH[i];
            handleAmounts(
               vertices,
               amountsForInt,
               amounts4,
               8.5F * snowVertexValue7,
               7.0F * snowVertexValue7,
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue2, snowVertexBound)),
               CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(snowVertexValue2, snowVertexBound * 0.45F))
            );
         }
      }
   }

   static {
      for (int i = 0; i <= 16; i++) {
         double angle = (Math.PI * 2) * i / 16.0;
         CIRCLE_COS[i] = (float)Math.cos(angle);
         CIRCLE_SIN[i] = (float)Math.sin(angle);
      }

      for (int j = 0; j < 22; j++) {
         float snowVertexValue = 20.0F + j * 27.619047F + (getAmount(j, 5) - 0.5F) * 3.0F;
         LIGHT_POSITION_RATIO[j] = snowVertexValue / 620.0F;
         LIGHT_COLORS[j] = LIGHT_PALETTE[j % LIGHT_PALETTE.length];
         LIGHT_BLINK_PERIOD[j] = 4.5F + 3.0F * getAmount(j, 11);
         LIGHT_BLINK_PHASE[j] = (float) (Math.PI * 2) * getAmount(j, 13);
         LIGHT_REST_ANGLE[j] = (j % 2 == 0 ? 1.0F : -1.0F) * (0.08F + 0.08F * getAmount(j, 17));
      }

      for (int k = 0; k < 100; k++) {
         PILE_NOISE[k] = 0.8F + 0.14F * (float)Math.sin(0.21 * k + 1.7) + 0.06F * (float)Math.sin(0.63 * k + 0.4);
      }

      handle(false, 18.0F, new float[]{8.0F, 32.0F, 58.0F, 82.0F}, new float[]{0.6F, 1.0F, 0.85F, 0.55F}, new float[]{0.3F, 0.5F, 0.7F});
      handle(
         true, 30.0F, new float[]{4.0F, 24.0F, 45.0F, 66.0F, 86.0F}, new float[]{0.55F, 0.85F, 1.0F, 0.8F, 0.5F}, new float[]{0.22F, 0.38F, 0.54F, 0.7F, 0.84F}
      );
   }

   private SnowVertexUtils() {
   }

   private static float getMin(float snowVertexLimit) {
      return snowVertexLimit < 0.0F ? 0.0F : (snowVertexLimit > 1.0F ? 1.0F : snowVertexLimit);
   }

   private static void handleRandomAmounts(int index, int randomValue, int randomFactor, boolean randomFlag) {
      boolean randomFlag2 = index >= 30;
      FLAKE_X[index] = -20.0F + RANDOM.nextFloat() * (randomValue + 40.0F);
      randomTable[index] = randomFlag ? RANDOM.nextFloat() * randomFactor : -6.0F - 40.0F * RANDOM.nextFloat();
      FLAKE_SIZE[index] = randomFlag2 ? 0.95F + 0.4F * RANDOM.nextFloat() : 0.55F + 0.3F * RANDOM.nextFloat();
      FLAKE_ALPHA[index] = randomFlag2 ? 0.42F + 0.18F * RANDOM.nextFloat() : 0.2F + 0.12F * RANDOM.nextFloat();
      FLAKE_FALL_SPEED[index] = randomFlag2 ? 13.0F + 7.0F * RANDOM.nextFloat() : 6.0F + 4.0F * RANDOM.nextFloat();
      FLAKE_SWAY_AMPLITUDE[index] = randomFlag2 ? 4.0F + 4.0F * RANDOM.nextFloat() : 2.5F + 2.0F * RANDOM.nextFloat();
      FLAKE_SWAY_PERIOD[index] = randomFlag2 ? 4.5F + 2.5F * RANDOM.nextFloat() : 6.0F + 4.0F * RANDOM.nextFloat();
      FLAKE_SWAY_PHASE[index] = RANDOM.nextFloat() * (float) (Math.PI * 2);
      FLAKE_WIND_X[index] = FLAKE_WIND_Y[index] = 0.0F;
   }

   private static int getValue(int snowVertexValue, float snowVertexLimit) {
      return MatrixAmountUtils.getValue(-42466, snowVertexValue, (float)Math.pow(getMin(snowVertexLimit), 1.5));
   }

   private static float getAmounts(int index, int snowVertexValue, int snowVertexFactor) {
      return Math.min(4.4F, PILE_HEIGHTS[index]) * getAmountFloat(getAmount(index, snowVertexValue, snowVertexFactor), snowVertexValue, snowVertexFactor) * PILE_NOISE[index];
   }

   private static void handleAmountMin(int matrixMatricesValue, int heightValue) {
      boolean flag = matrixMatricesValue > -1000 && heightValue > -1000;
      boolean snowVertexFlag = Math.abs(matrixMatricesValue - lastMouseX) + Math.abs(heightValue - lastMouseY) >= Math.max(96.0F, 3200.0F * frameDeltaSeconds);
      interactionActive = flag && hoverRequested && frameDeltaSeconds > 0.0F && !snowVertexFlag && matrixUnscaled;
      hoverRequested = flag;
      if (!interactionActive) {
         lastMouseX = matrixMatricesValue;
         lastMouseY = heightValue;
         mouseVelocityY = 0.0F;
         mouseVelocityX = 0.0F;
      } else {
         float snowVertexFactor = 1.0F - (float)Math.exp(-12.0F * frameDeltaSeconds);
         mouseVelocityX = mouseVelocityX + ((matrixMatricesValue - lastMouseX) / frameDeltaSeconds - mouseVelocityX) * snowVertexFactor;
         mouseVelocityY = mouseVelocityY + ((heightValue - lastMouseY) / frameDeltaSeconds - mouseVelocityY) * snowVertexFactor;
      }
   }

   private static void handleAmountsAmount(int snowVertexValue, int randomValue, int snowVertexFactor) {
      snowVertexAmount = snowVertexAmount - frameDeltaSeconds;
      if (snowVertexAmount <= 0.0F) {
         snowVertexAmount = 16.0F + 8.0F * RANDOM.nextFloat();
         lightSweepPosition = -0.1F;
      }

      if (lightSweepPosition >= -0.1F) {
         lightSweepPosition = lightSweepPosition + frameDeltaSeconds / 2.2F > 1.15F ? -1.0F : lightSweepPosition + frameDeltaSeconds / 2.2F;
      }

      flashTimer = flashTimer - frameDeltaSeconds;
      if (flashTimer <= 0.0F) {
         flashTimer = 0.7F + 1.4F * RANDOM.nextFloat();
         LIGHT_FLASH[RANDOM.nextInt(22)] = 1.0F;
      }

      float exp = (float)Math.exp(-frameDeltaSeconds / 0.25F);
      int snowVertexDivisor = frameDeltaSeconds <= 0.0F ? 0 : Math.max(1, (int)Math.ceil(frameDeltaSeconds / 0.004166667F));
      float snowVertexFactor2 = snowVertexDivisor == 0 ? 0.0F : frameDeltaSeconds / snowVertexDivisor;

      for (int i = 0; i < 22; i++) {
         LIGHT_FLASH[i] = LIGHT_FLASH[i] * exp;
         float snowVertexValue3 = snowVertexValue + LIGHT_POSITION_RATIO[i] * snowVertexFactor;
         float snowVertexValue4 = getAmount(snowVertexValue3, snowVertexValue, randomValue, snowVertexFactor);
         if (interactionActive && frameDeltaSeconds > 0.0F) {
            float snowVertexFactor3 = lastMouseX - snowVertexValue3;
            float snowVertexFactor4 = lastMouseY - (snowVertexValue4 + 3.0F);
            if (snowVertexFactor3 * snowVertexFactor3 + snowVertexFactor4 * snowVertexFactor4 < 64.0F) {
               LIGHT_SWING_VELOCITY[i] = LIGHT_SWING_VELOCITY[i] + Math.max(-2.5F, Math.min(2.5F, mouseVelocityX * 0.006F)) * (frameDeltaSeconds * 60.0F);
            }
         }

         for (int j = 0; j < snowVertexDivisor; j++) {
            LIGHT_SWING_VELOCITY[i] = LIGHT_SWING_VELOCITY[i] + (-55.0F * LIGHT_SWING_ANGLE[i] - 3.2F * LIGHT_SWING_VELOCITY[i]) * snowVertexFactor2;
            LIGHT_SWING_ANGLE[i] = Math.max(-0.9F, Math.min(0.9F, LIGHT_SWING_ANGLE[i] + LIGHT_SWING_VELOCITY[i] * snowVertexFactor2));
         }

         float snowVertexValue5 = 0.03F * (float)Math.sin(animTime * 0.9 + i * 0.7);
         float angle = LIGHT_REST_ANGLE[i] + LIGHT_SWING_ANGLE[i] + snowVertexValue5;
         LIGHT_DIR_X[i] = -((float)Math.sin(angle));
         LIGHT_DIR_Y[i] = (float)Math.cos(angle);
         LIGHT_X[i] = snowVertexValue3;
         LIGHT_Y[i] = snowVertexValue4;
      }
   }

   private static float getAmount(int snowVertexValue) {
      float snowVertexLimit = (float)((brushSnowAmount - (0.35 + 0.045 * snowVertexValue)) / 0.45);
      float min3 = getMin(snowVertexLimit);
      float snowVertexFactor = 1.0F - min3;
      return 1.0F - snowVertexFactor * snowVertexFactor * snowVertexFactor;
   }

   private static void handleAmountsAmount(int snowVertexValue, int snowVertexValue2, int snowVertexValue3, int snowVertexValue4, int randomValue, int randomFactor) {
      float snowVertexFactor = 2.4F * (float)Math.sin(6.2831855 * animTime / 41.0) + 1.2F * (float)Math.sin(6.2831855 * animTime / 17.0 + 1.3);
      boolean snowVertexFlag = interactionActive && (!(lastMouseX >= snowVertexValue) || !(lastMouseX <= snowVertexValue + snowVertexValue3) || !(lastMouseY >= snowVertexValue2) || !(lastMouseY <= snowVertexValue2 + snowVertexValue4));
      float exp = (float)Math.exp(-1.2F * frameDeltaSeconds);
      float min2 = Math.min(1.0F, 5.0F * frameDeltaSeconds);
      boolean canFlag2 = canFlag(snowVertexValue2);

      for (int i = 0; i < 48; i++) {
         boolean snowVertexFlag3 = i >= 30;
         float amountsAmount = getAmountsAmountForInt(i);
         if (snowVertexFlag) {
            float snowVertexFactor3 = amountsAmount - lastMouseX;
            float snowVertexFactor4 = randomTable[i] - lastMouseY;
            float distance = (float)Math.sqrt(snowVertexFactor3 * snowVertexFactor3 + snowVertexFactor4 * snowVertexFactor4);
            if (distance < 30.0F) {
               float snowVertexFactor5 = (1.0F - distance / 30.0F) * min2;
               FLAKE_WIND_X[i] = FLAKE_WIND_X[i] + (0.25F * mouseVelocityX - FLAKE_WIND_X[i]) * snowVertexFactor5;
               FLAKE_WIND_Y[i] = FLAKE_WIND_Y[i] + (0.25F * mouseVelocityY - FLAKE_WIND_Y[i]) * snowVertexFactor5;
            }
         }

         FLAKE_WIND_X[i] = FLAKE_WIND_X[i] * exp;
         FLAKE_WIND_Y[i] = FLAKE_WIND_Y[i] * exp;
         float snowVertexValue7 = randomTable[i];
         FLAKE_X[i] = FLAKE_X[i] + (snowVertexFactor * (snowVertexFlag3 ? 1.0F : 0.6F) + FLAKE_WIND_X[i]) * frameDeltaSeconds;
         randomTable[i] = randomTable[i] + (FLAKE_FALL_SPEED[i] + FLAKE_WIND_Y[i]) * frameDeltaSeconds;
         float amountsAmountForInt = getAmountsAmountForInt(i);
         if (snowVertexFlag3 && amountsAmountForInt > snowVertexValue && amountsAmountForInt < snowVertexValue + snowVertexValue3) {
            float snowVertexValue8 = canFlag2 ? snowVertexValue2 : getAmountForFloatInt(amountsAmountForInt, snowVertexValue, snowVertexValue2, snowVertexValue3) - getAmount(amountsAmountForInt, snowVertexValue, snowVertexValue3);
            if (snowVertexValue7 < snowVertexValue8 - 0.4F && randomTable[i] >= snowVertexValue8 - 0.4F) {
               if (randomTable[i] < snowVertexValue8 + 4.0F && amountsAmountForInt > snowVertexValue + 6.0F && amountsAmountForInt < snowVertexValue + snowVertexValue3 - 6.0F) {
                  handleAmounts(getValue(amountsAmountForInt, snowVertexValue, snowVertexValue3), 4.2F * Math.max(0.35F, Math.min(1.5F, (snowVertexValue2 + 20.0F) / 110.0F)));
               }

               handleRandomAmounts(i, randomValue, randomFactor, false);
               continue;
            }
         }

         if (randomTable[i] > randomFactor + 4.0F || FLAKE_X[i] < -60.0F || FLAKE_X[i] > randomValue + 60.0F) {
            handleRandomAmounts(i, randomValue, randomFactor, false);
         }
      }
   }

   private static int getValueForIntFloat(int snowVertexValue, float snowVertexBound) {
      int round = Math.round(255.0F * Math.max(0.0F, Math.min(1.0F, snowVertexBound)));
      return round << 24 | snowVertexValue & 16777215;
   }

   private static float getAmountsForInt(int index) {
      return LIGHT_X[index] + LIGHT_DIR_X[index] * 2.63F;
   }

   private static void handleAmounts(VertexConsumer vertices, float snowVertexValue, float snowVertexValue2, float snowVertexFactor, float snowVertexValue3, int snowVertexValue4) {
      int panelOverlay = snowVertexValue4 & 16777215;
      float snowVertexFactor2 = snowVertexFactor + snowVertexValue3;

      for (int i = 0; i < 16; i += 2) {
         int index = i + 2;
         float vertexColorValue = snowVertexValue + CIRCLE_COS[i] * snowVertexFactor;
         float snowVertexValue7 = snowVertexValue2 + CIRCLE_SIN[i] * snowVertexFactor;
         float snowVertexLimit = snowVertexValue + CIRCLE_COS[index] * snowVertexFactor;
         float deltaVar = snowVertexValue2 + CIRCLE_SIN[index] * snowVertexFactor;
         handleVertexColor(vertices, snowVertexValue, snowVertexValue2, snowVertexValue4, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexLimit, deltaVar, snowVertexValue4);
         float snowVertexValue10 = snowVertexValue + CIRCLE_COS[i] * snowVertexFactor2;
         float snowVertexValue11 = snowVertexValue2 + CIRCLE_SIN[i] * snowVertexFactor2;
         float snowVertexValue12 = snowVertexValue + CIRCLE_COS[index] * snowVertexFactor2;
         float snowVertexValue13 = snowVertexValue2 + CIRCLE_SIN[index] * snowVertexFactor2;
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue10, snowVertexValue11, panelOverlay, snowVertexValue12, snowVertexValue13, panelOverlay);
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue12, snowVertexValue13, panelOverlay, snowVertexLimit, deltaVar, snowVertexValue4);
      }
   }

   private static void handleAmountsMax(VertexConsumer vertices, float snowVertexValue, float deltaVar, float snowVertexFactor, float snowVertexFactor2, int panelOverlay, int snowVertexValue4) {
      int snowVertexValue5 = snowVertexValue4 & 16777215;
      float core2 = snowVertexFactor2;
      float cy2 = -snowVertexFactor;

      for (int i = 0; i < 16; i++) {
         float snowVertexFactor3 = CIRCLE_COS[i];
         float snowVertexFactor4 = CIRCLE_SIN[i];
         float snowVertexFactor5 = CIRCLE_COS[i + 1];
         float snowVertexFactor6 = CIRCLE_SIN[i + 1];
         float snowVertexFactor7 = snowVertexFactor4 > 0.0F ? 1.0F + 0.18F * snowVertexFactor4 : 1.0F;
         float snowVertexFactor8 = snowVertexFactor6 > 0.0F ? 1.0F + 0.18F * snowVertexFactor6 : 1.0F;
         float vertexColorValue = snowVertexValue + core2 * snowVertexFactor3 * 1.2F + snowVertexFactor * snowVertexFactor4 * 1.7F * snowVertexFactor7;
         float snowVertexValue7 = deltaVar + cy2 * snowVertexFactor3 * 1.2F + snowVertexFactor2 * snowVertexFactor4 * 1.7F * snowVertexFactor7;
         float snowVertexLimit = snowVertexValue + core2 * snowVertexFactor5 * 1.2F + snowVertexFactor * snowVertexFactor6 * 1.7F * snowVertexFactor8;
         float snowVertexValue9 = deltaVar + cy2 * snowVertexFactor5 * 1.2F + snowVertexFactor2 * snowVertexFactor6 * 1.7F * snowVertexFactor8;
         handleVertexColor(vertices, snowVertexValue, deltaVar, panelOverlay, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexLimit, snowVertexValue9, snowVertexValue4);
         float snowVertexFactor9 = 1.0F + snowHeightScale / 1.2F;
         float snowVertexFactor10 = 1.0F + snowHeightScale / 1.2F;
         float snowVertexValue10 = snowVertexValue + (vertexColorValue - snowVertexValue) * snowVertexFactor9;
         float snowVertexValue11 = deltaVar + (snowVertexValue7 - deltaVar) * snowVertexFactor9;
         float snowVertexValue12 = snowVertexValue + (snowVertexLimit - snowVertexValue) * snowVertexFactor10;
         float snowVertexValue13 = deltaVar + (snowVertexValue9 - deltaVar) * snowVertexFactor10;
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue10, snowVertexValue11, snowVertexValue5, snowVertexValue12, snowVertexValue13, snowVertexValue5);
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue12, snowVertexValue13, snowVertexValue5, snowVertexLimit, snowVertexValue9, snowVertexValue4);
      }
   }

   private static void handle(VertexConsumer vertices, float snowVertexValue, float snowVertexValue2, float snowVertexValue3, float snowVertexValue4, float snowVertexFactor, float snowVertexFactor2, float snowVertexFactor3, float snowVertexFactor4, int snowVertexValue5, int snowVertexValue6, int snowVertexValue7, int panelOverlay) {
      float vertexColorValue = snowVertexValue + snowVertexFactor * snowVertexFactor3;
      float deltaVar = snowVertexValue2 + snowVertexFactor2 * snowVertexFactor3;
      float snowVertexLimit = snowVertexValue3 + snowVertexFactor * snowVertexFactor3;
      float snowVertexValue12 = snowVertexValue4 + snowVertexFactor2 * snowVertexFactor3;
      float snowVertexValue13 = snowVertexValue3 + snowVertexFactor * snowVertexFactor4;
      float snowVertexValue14 = snowVertexValue4 + snowVertexFactor2 * snowVertexFactor4;
      float snowVertexValue15 = snowVertexValue + snowVertexFactor * snowVertexFactor4;
      float snowVertexValue16 = snowVertexValue2 + snowVertexFactor2 * snowVertexFactor4;
      handleVertexColor(vertices, vertexColorValue, deltaVar, snowVertexValue5, snowVertexLimit, snowVertexValue12, snowVertexValue6, snowVertexValue13, snowVertexValue14, panelOverlay);
      handleVertexColor(vertices, vertexColorValue, deltaVar, snowVertexValue5, snowVertexValue13, snowVertexValue14, panelOverlay, snowVertexValue15, snowVertexValue16, snowVertexValue7);
   }

   private static void handleAmounts(VertexConsumer vertices, float snowVertexValue, float snowVertexValue2, float snowVertexFactor, float snowVertexFactor2, int snowVertexValue3, int snowVertexValue4) {
      int panelOverlay = snowVertexValue3 & 16777215;

      for (int i = 0; i < 16; i++) {
         float vertexColorValue = snowVertexValue + CIRCLE_COS[i] * snowVertexFactor * 0.32F;
         float snowVertexValue7 = snowVertexValue2 + CIRCLE_SIN[i] * snowVertexFactor2 * 0.32F;
         float snowVertexValue8 = snowVertexValue + CIRCLE_COS[i + 1] * snowVertexFactor * 0.32F;
         float snowVertexValue9 = snowVertexValue2 + CIRCLE_SIN[i + 1] * snowVertexFactor2 * 0.32F;
         handleVertexColor(vertices, snowVertexValue, snowVertexValue2, snowVertexValue3, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue8, snowVertexValue9, snowVertexValue4);
         float snowVertexValue10 = snowVertexValue + CIRCLE_COS[i] * snowVertexFactor;
         float deltaVar = snowVertexValue2 + CIRCLE_SIN[i] * snowVertexFactor2;
         float snowVertexLimit = snowVertexValue + CIRCLE_COS[i + 1] * snowVertexFactor;
         float snowVertexValue13 = snowVertexValue2 + CIRCLE_SIN[i + 1] * snowVertexFactor2;
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexValue10, deltaVar, panelOverlay, snowVertexLimit, snowVertexValue13, panelOverlay);
         handleVertexColor(vertices, vertexColorValue, snowVertexValue7, snowVertexValue4, snowVertexLimit, snowVertexValue13, panelOverlay, snowVertexValue8, snowVertexValue9, snowVertexValue4);
      }
   }

   public static void updateFlag() {
      snowVertexActive = true;
   }

   private static int getAmounts(int snowVertexValue, float snowVertexValue2, int snowVertexValue3, int snowVertexValue4, float snowVertexBound) {
      float matrixFactor = 0.0F;
      int releaseRight = -13942;
      float panelOverlay = CorzRoundedUtils.getPanelOverlay();

      for (int i = 0; i < 22; i++) {
         float snowVertexLimit = Math.abs(snowVertexValue2 - LIGHT_X[i]);
         if (!(snowVertexLimit >= 9.0F)) {
            float snowVertexFactor = 1.0F - snowVertexLimit / 9.0F;
            float wrapDegreesBound = getAmount(i);
            float snowVertexLimit2 = 0.5F * getMin(getAmountsAmount(i) * wrapDegreesBound) * Math.min(1.0F, panelOverlay * 1.2F) * snowVertexFactor * snowVertexFactor;
            if (snowVertexLimit2 > matrixFactor) {
               matrixFactor = snowVertexLimit2;
               releaseRight = getValue(LIGHT_COLORS[i], Math.min(wrapDegreesBound, panelOverlay));
            }
         }
      }

      return CorzRoundedUtils.getPanelOverlay(getValueForIntFloat(MatrixAmountUtils.getValue(snowVertexValue, releaseRight, matrixFactor), snowVertexBound));
   }

   private static float getAmountsAmountForInt(int index) {
      return FLAKE_X[index] + FLAKE_SWAY_AMPLITUDE[index] * (float)Math.sin(6.2831855 * animTime / FLAKE_SWAY_PERIOD[index] + FLAKE_SWAY_PHASE[index]);
   }

   private static void handleMax(VertexConsumer vertices, float snowVertexValue, float snowVertexValue2, float snowVertexValue3, float snowVertexValue4, float snowVertexValue5, int snowVertexValue6, int snowVertexValue7) {
      float snowVertexFactor = snowVertexValue3 - snowVertexValue;
      float snowVertexFactor2 = snowVertexValue4 - snowVertexValue2;
      float distance = (float)Math.sqrt(snowVertexFactor * snowVertexFactor + snowVertexFactor2 * snowVertexFactor2);
      if (!(distance < 0.001F)) {
         float snowVertexValue8 = -snowVertexFactor2 / distance;
         float snowVertexValue9 = snowVertexFactor / distance;
         int snowVertexValue10 = snowVertexValue6 & 16777215;
         int panelOverlay = snowVertexValue7 & 16777215;
         handle(vertices, snowVertexValue, snowVertexValue2, snowVertexValue3, snowVertexValue4, snowVertexValue8, snowVertexValue9, -snowVertexValue5 - snowHeightScale, -snowVertexValue5, snowVertexValue10, panelOverlay, snowVertexValue6, snowVertexValue7);
         handle(vertices, snowVertexValue, snowVertexValue2, snowVertexValue3, snowVertexValue4, snowVertexValue8, snowVertexValue9, -snowVertexValue5, snowVertexValue5, snowVertexValue6, snowVertexValue7, snowVertexValue6, snowVertexValue7);
         handle(vertices, snowVertexValue, snowVertexValue2, snowVertexValue3, snowVertexValue4, snowVertexValue8, snowVertexValue9, snowVertexValue5, snowVertexValue5 + snowHeightScale, snowVertexValue6, snowVertexValue7, snowVertexValue10, panelOverlay);
      }
   }

   public static boolean isBaseInstance() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      return singleton != null && singleton.themeCustomizerBooleanBase.isEnabled() && isChristmas();
   }

   private static void handleAmountFlag(int snowVertexValue, int randomValue, int snowVertexFactor, int snowVertexValue4, int matrixMatricesValue, int heightValue) {
      float snowVertexValue7 = getAmount((float)matrixMatricesValue, (float)heightValue, snowVertexValue + 3.5F, randomValue + snowVertexValue4 - 3.5F);
      float snowVertexValue8 = getAmount((float)matrixMatricesValue, (float)heightValue, snowVertexValue + snowVertexFactor - 3.5F, randomValue + snowVertexValue4 - 3.5F);
      fadeNearEdge = getMin(fadeNearEdge, interactionActive && snowVertexValue7 < 34.0F);
      fadeNearTop = getMin(fadeNearTop, interactionActive && snowVertexValue8 < 46.0F);
   }

   public static void handleTrianglesFlagForInt(int trianglesValue, int trianglesValue2, int snowVertexFactor, int trianglesValue4, int trianglesValue5, int randomValue, int randomFactor, int matrixMatricesValue, int heightValue) {
      boolean trianglesFlag = trianglesRendered || canMinMax(trianglesValue, trianglesValue2, snowVertexFactor, trianglesValue4, randomValue, randomFactor, matrixMatricesValue, heightValue);
      trianglesRendered = false;
      if (trianglesFlag) {
         updatePositionMatrix();
         VertexConsumer beginBuffer = VertexGuiUtils.getBeginBuffer(DrawMode.TRIANGLES);
         handleAmountsMaxForVertexConsumer(beginBuffer);
         handleMax(beginBuffer, trianglesValue, trianglesValue2, snowVertexFactor);
         handleAmountsMax(beginBuffer, trianglesValue, trianglesValue2, snowVertexFactor);
         handleAmountsMax(beginBuffer);
         VertexGuiUtils.updateBufferClose();
         updateBrushSnow();
      }
   }

   private static void updatePositionMatrix() {
      CorzRoundedUtils.updateSamplerView();
      positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      snowHeightScale = Math.max(0.35F, CorzRoundedUtils.getFramebufferHeight() * 1.2F);
   }

   private static float getAmountFloat(float snowVertexValue, int snowVertexValue2, int snowVertexFactor) {
      return getAmount(snowVertexValue2 + 6.0F, snowVertexValue2 + 20.0F, snowVertexValue) * getAmount(snowVertexValue2 + snowVertexFactor - 6.0F, snowVertexValue2 + snowVertexFactor - 20.0F, snowVertexValue);
   }

   private static float getAmount(int snowVertexFactor, int snowVertexValue, int snowVertexValue2) {
      return snowVertexValue + 6.0F + snowVertexFactor * ((snowVertexValue2 - 12.0F) / 99.0F);
   }

   private static boolean canFlag(int snowVertexValue) {
      return snowVertexValue < 8.0F;
   }
}
