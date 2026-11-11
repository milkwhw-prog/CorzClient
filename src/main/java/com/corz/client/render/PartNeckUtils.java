package com.corz.client.render;

import com.corz.client.gui.MatrixStackUtilsRender;
import com.corz.client.mixins.AccessorGameRenderer;
import com.corz.client.world.StateDefault;
import java.time.LocalDate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.AmountsAmountFlag;
import com.corz.client.utils.HalloweenChristmasType;

final class PartNeckUtils {
   static final int UNUSED_ONE = 1;
   private static float boxX;
   private static boolean matrixActive;
   static final float[] limbTable = new float[]{6.3F, 6.85F, 7.4F};
   private static final float degrees = (float)Math.toDegrees(Math.atan2(0.39999962F, -1.0));
   private static final float[][] neckMerryFloats = new float[][]{{1.75F, -0.42F}, {1.75F, 0.42F}, {2.95F, -0.42F}, {2.95F, 0.42F}, {4.2F, 0.0F}};
   private static int activeLimbCount;
   private static RenderLayer eyes;
   private static ModelPart modelPart;
   private static float peekVisitAmount = 1.0F;
   private static boolean peekActive;
   private static RenderLayer noCull;
   private static RenderLayer entityCutoutNoCull;
   private static float parseFloat = -1.0F;
   private static float xmasAmount;
   private static float welcomeHouseAmount;
   private static boolean neckFlag;
   private static RenderLayer entityCutoutNoCullCullNeck;
   private static float peekAmount = Float.NaN;
   private static float partNeckAmount = Float.NaN;
   private static long lastNanoTime;
   private static float merryAmount = 620.0F;
   private static float merryChristmasAmount = 360.0F;
   private static float merryChristmasAmount3Merry = 12.0F;
   private static ModelPart child;
   private static float peekAngleDegrees = 30.0F;
   private static boolean visitPeekFlag;
   private static int partNeckValue;
   private static float firstMerryAmount = Float.NaN;
   private static int merryChristmasValue;
   private static boolean running;
   private static float xmasPeekAmount = 0.5F;
   private static int lastPeekWidth;
   private static boolean merryActive;
   private static long nanoTime3Peek;
   private static ModelPart childCullNeck;
   private static PartNeckUtils.SantaModel anglesPlayerModel;
   private static float peek;
   private static float peekXmasAmount;
   private static final PartNeckUtils.SantaPose MAX_RENDER_STATE = new PartNeckUtils.SantaPose();
   private static final PartNeckUtils.SantaPose MIN_RENDER_STATE = new PartNeckUtils.SantaPose();
   private static final ModelPart[] neckHeadModelParts = new ModelPart[PartNeckUtils.neckMerryFloats.length];
   private static String merryChristmasText = "Merry Christmas!";
   private static int lastPeekHeight;
   private static final AmountsAmountFlag RENDER_AMOUNTS = new AmountsAmountFlag();
   private static RenderMatrix renderMatrix;
   private static RenderLayer entityCutoutNoCullLegNeck;
   private static final AmountsAmountFlag CHECK_AMOUNTS = new AmountsAmountFlag();
   private static float merryBoxY;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static final float[] merryChristmasAmounts = new float[3];
   private static final float[] merryChristmasTable = new float[3];
   private static final float[] merryAmounts = new float[3];

   static float getAmount(float partNeckValue) {
      if (partNeckValue < 3.0F) {
         return 4.0F;
      } else if (partNeckValue < 4.0F) {
         return getAmountFloat(4.0F, -14.0F, getAmount3((partNeckValue - 3.0F) / 0.3F));
      } else {
         return partNeckValue < 4.35F ? getAmountFloat(-14.0F, 35.0F, getAmount3((partNeckValue - 4.0F) / 0.3499999F)) : getAmountFloat(35.0F, 2.0F, getAmount3((partNeckValue - 4.35F) / 0.4F));
      }
   }

   static float getDegreesXmas() {
      if (!running) {
         return 15.0F;
      } else {
         float peekXmasAmount2 = peekXmasAmount;
         if (peekXmasAmount2 < 5.9F) {
            return degrees;
         } else {
            return peekXmasAmount2 < 7.9999995F
               ? getAmountFloat(degrees, 20.0F, getAmount3((peekXmasAmount2 - 5.9F) / 0.35F))
               : getAmountFloat(20.0F, 0.0F, getAmount3((peekXmasAmount2 - 8.4F + 0.4F) / 0.4F));
         }
      }
   }

   static String formatPeekXmas(String peekXmasText) {
      String[] split = peekXmasText.trim().split("\\s+");
      String peekXmasText2 = split[0];
      switch (peekXmasText2) {
         case "visit":
            visitPeekFlag = false;
            boolean merryChristmas = isMerryChristmas();
            return "xmas visit start=" + merryChristmas;
         case "peek":
            boolean welcomeWool = isWelcomeWool();
            return "xmas peek open=" + welcomeWool;
         case "unpeek":
            updateWoolFall();
            return "xmas peek closed";
         case "freeze":
            parseFloat = Float.parseFloat(split[1]);
            if (parseFloat >= 0.0F) {
               peekXmasAmount = parseFloat;
            }

            return "xmas freeze " + parseFloat + " running=" + running;
         case "gifts":
            handleMax(Integer.parseInt(split[1]));
            merryChristmasValue = getPartNeckValue();
            return "xmas gifts " + getPartNeckValue();
         case "state":
            return "xmas state running=" + running + " t=" + peekXmasAmount + " lift=" + getPeekMerry() + " peek=" + peek + " gifts=" + getMin();
         default:
            return "xmas: unknown " + peekXmasText;
      }
   }

   private static void handleModelAngles(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, PartNeckUtils.SantaPose santaPose) {
      orderedRenderCommandQueue.submitModel(anglesPlayerModel, santaPose, matrices, noCull, 15728880, OverlayTexture.DEFAULT_UV, -3874, null, 0, null);
      anglesPlayerModel.setAngles((PlayerEntityRenderState)santaPose);
      matrices.push();
      anglesPlayerModel.getHead().applyTransform(matrices);
      matrices.translate(0.0F, 0.125F, 0.0F);
      orderedRenderCommandQueue.submitModelPart(childCullNeck, matrices, entityCutoutNoCull, 15728880, OverlayTexture.DEFAULT_UV, null, -3874, null);
      matrices.pop();
   }

   private static void handleLimbRelative(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int limbRelativeValue, int ctxOffset) {
      matrices.translate(0.0F, 0.0F, -1300.0F);
      matrices.scale(1.0F, -1.0F, 1.0F);
      matrices.translate(-limbRelativeValue / 2.0F, -ctxOffset / 2.0F, 0.0F);
      float peekXmasAmount2 = peekXmasAmount;
      PartNeckUtils.SantaPose santaPose = MAX_RENDER_STATE;
      santaPose.age = (float)RenderMatrixUtilsAmount.legNeckAmount * 20.0F;
      santaPose.leftArmBlend = 0.0F;
      santaPose.limbAmount = 0.0F;
      santaPose.isInSneakingPose = false;
      santaPose.santaPoseAmount = getAmountForFloat(peekXmasAmount);
      santaPose.pitch = getAmount(peekXmasAmount2);
      float degrees2;
      if (peekXmasAmount2 < 3.0F) {
         santaPose.poseIndex = 0;
         santaPose.limbSwingAnimationProgress = (getMinBoxX() - boxX) * 0.09F;
         santaPose.limbSwingAmplitude = 0.55F * (1.0F - getAmount3((peekXmasAmount2 - 2.8F) / 0.2F));
         santaPose.relativeHeadYaw = 38.0F * (float)Math.sin(peekXmasAmount2 * 2.3F);
         santaPose.rightArmBlend = peekXmasAmount2 >= 1.9F ? getAmount3(Math.min(1.0F, (peekXmasAmount2 - 1.9F) / 0.25F)) : 0.0F;
         if (santaPose.rightArmBlend > 0.0F) {
            santaPose.relativeHeadYaw = santaPose.relativeHeadYaw * (1.0F - santaPose.rightArmBlend);
         }

         degrees2 = 62.0F;
      } else if (peekXmasAmount2 < 4.35F) {
         santaPose.limbSwingAmplitude = 0.0F;
         santaPose.rightArmBlend = peekXmasAmount2 < 3.2F ? 1.0F - getAmount3((peekXmasAmount2 - 3.0F) / 0.20000005F) : 0.0F;
         santaPose.relativeHeadYaw = 0.0F;
         degrees2 = getAmountFloat(62.0F, 125.0F, getAmount3(Math.min(1.0F, (peekXmasAmount2 - 3.0F) / 0.3499999F)));
         santaPose.poseIndex = peekXmasAmount2 >= 3.25F ? 1 : 0;
         santaPose.limbRelativeAmount = getAmount3((peekXmasAmount2 - 3.3F) / 0.70000005F);
      } else {
         santaPose.poseIndex = 2;
         santaPose.limbRelativeAmount = 1.0F - getAmount3((peekXmasAmount2 - 4.35F) / 0.3F);
         santaPose.limbSwingAnimationProgress = (peekXmasAmount2 - 4.35F) * 9.0F;
         santaPose.limbSwingAmplitude = 0.6F * (1.0F - santaPose.limbRelativeAmount) * (1.0F - getAmount3((peekXmasAmount2 - 4.8F) / 0.2F));
         santaPose.relativeHeadYaw = 0.0F;
         degrees2 = getAmountFloat(125.0F, degrees, getAmount3((peekXmasAmount2 - 4.35F) / 0.35F));
      }

      float x = 58.125F * getXmasPeekPeekXmasAmount();
      matrices.push();
      matrices.translate(getMinBoxX(), getAmountXmas(), 0.0F);
      matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(degrees2)));
      matrices.scale(x, x, -x);
      matrices.translate(0.0F, -1.501F, 0.0F);
      handleModelAngles(matrices, orderedRenderCommandQueue, santaPose);
      matrices.pop();
   }

   static float getAmountForFloat(float partNeckValue) {
      if (partNeckValue < 3.0F) {
         return 1.0F;
      } else if (partNeckValue < 4.0F) {
         return 1.0F - getAmount3((partNeckValue - 3.0F) / 0.54999995F);
      } else {
         return partNeckValue < 4.35F ? 0.55F * getAmount3((partNeckValue - 4.0F) / 0.3499999F) : 0.55F * (1.0F - getAmount3((partNeckValue - 4.35F) / 0.6500001F));
      }
   }

   static int getInstance() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton == null) {
         return 0;
      } else {
         long partNeckValue = Math.round(singleton.christmasGifts.getValue());
         int partNeckValue2 = (int)(partNeckValue / 16L);
         int partNeckBound = (int)(partNeckValue % 16L);
         return partNeckValue2 == HalloweenChristmasType.getYear(LocalDate.now()) ? Math.min(9, partNeckBound) : 0;
      }
   }

   static int getMin() {
      if (!running) {
         return getPartNeckValue();
      } else {
         int partNeckCounter = 0;

         for (int i = 0; i < activeLimbCount; i++) {
            if (peekXmasAmount >= limbTable[i] + 0.8F) {
               partNeckCounter++;
            }
         }

         return Math.min(9, merryChristmasValue + partNeckCounter);
      }
   }

   private static boolean canFlag(float partNeckValue, float partNeckLimit, float partNeckLimit2) {
      return partNeckValue < partNeckLimit2 && partNeckLimit >= partNeckLimit2;
   }

   private static void renderWelcomeHouse(DrawContext context, float welcomeHouseFactor) {
      float peekXmasAmount2 = peekXmasAmount;
      if (running && peekXmasAmount2 >= 1.9F && peekXmasAmount2 < 3.3F) {
         float welcomeHouseFactor2 = getAmount(peekXmasAmount2, 1.9F, 3.3F);
         float welcomeHouseValue = getAmountXmas() - getAmountFloat(2.8F, 2.55F, getAmountForFloat(peekXmasAmount)) * 62.0F * 0.9375F;
         MatrixStackUtilsRender.renderTranslatePop(
            context, "Shh...", getMinBoxX() - 30.0F, welcomeHouseValue + 6.0F, 0.8F, welcomeHouseFactor2 * welcomeHouseFactor, false, 0.85F + 0.15F * getAmount3(Math.min(1.0F, (peekXmasAmount2 - 1.9F) / 0.18F))
         );
      }

      String welcomeHouseText = null;
      float welcomeHouseFactor3 = 0.0F;
      float x2 = 1.0F;
      if (running && peekXmasAmount2 >= 8.4F && peekXmasAmount2 < 10.6F) {
         welcomeHouseText = peekXmasAmount2 < 9.299999F ? "Ho ho ho!" : merryChristmasText;
         welcomeHouseFactor3 = getAmount(peekXmasAmount2, 8.4F, 10.6F);
         x2 = 0.86F + 0.14F * getAmount3(Math.min(1.0F, (peekXmasAmount2 - 8.4F) / 0.18F));
      } else if (!running && peek > 0.85F) {
         welcomeHouseText = welcomeHouseAmount < 3.2F ? "Welcome to my house!" : "Ho ho ho!";
         welcomeHouseFactor3 = getAmount3((peek - 0.85F) / 0.15F);
      }

      if (welcomeHouseText != null && !(welcomeHouseFactor3 <= 0.01F)) {
         float[] merryAmounts = getMerryAmounts();
         if (merryAmounts != null && RenderMatrixUtilsAmount.canCabinCam(merryAmounts[0] + 0.3F, 2.65F, merryAmounts[2], merryChristmasAmounts)) {
            MatrixStackUtilsRender.renderTranslatePop(context, welcomeHouseText, merryChristmasAmounts[0] + 4.0F, merryChristmasAmounts[1] - 2.0F, 0.8F, welcomeHouseFactor3 * welcomeHouseFactor, true, x2);
         }
      }
   }

   private static void updatePeekVisit() {
      int peekVisitValue = getMin();
      if (peekXmasAmount < 3.3F) {
         visitPeekFlag = false;
      }

      running = false;
      peekVisitAmount = 1.0F;
      handleMax(peekVisitValue);
      merryChristmasValue = getPartNeckValue();
   }

   static void handleNeckChild(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      if (neckFlag) {
         int index = neckHeadModelParts.length - 1;
         matrices.push();
         handleNeckMerry(matrices, index);
         neckHeadModelParts[index].getChild("neck").applyTransform(matrices);
         neckHeadModelParts[index].getChild("neck").getChild("head").applyTransform(matrices);
         orderedRenderCommandQueue.submitModelPart(child, matrices, eyes, 15728880, OverlayTexture.DEFAULT_UV, null, -54742, null);
         matrices.pop();
      }
   }

   static float getXmasPeek() {
      return peekXmasAmount;
   }

   static float getAmountMerry() {
      return -(boxX + 2.0F * merryAmount) * AmountsAmountFlag.merryAmount - (merryBoxY + 2.0F * merryChristmasAmount) * AmountsAmountFlag.SHADOW_DIR_Y + 8.0F;
   }

   static void updateRenderMatrix() {
      if (renderMatrix != null) {
         renderMatrix.updateProjectionRaw();
      }

      renderMatrix = null;
      if (running) {
         updatePeekVisit();
      }

      peekActive = false;
      peek = 0.0F;
      matrixActive = false;
      peekAngleDegrees = 30.0F;
      lastNanoTime = 0L;
   }

   private static boolean isChristmas() {
      return HalloweenChristmasType.getHalloweenChristmas(LocalDate.now()) == HalloweenChristmasType.CHRISTMAS;
   }

   static float getPeekMerry() {
      if (peek > 0.0F) {
         return getAmountFloat(peekAngleDegrees, getAmountMerry(), getAmount3(peek));
      } else {
         return running ? getMerryChristmas(peekXmasAmount) : peekAngleDegrees;
      }
   }

   static void updateWoolFall() {
      if (peekActive) {
         peekActive = false;
         handlePlaySound(SoundEvents.BLOCK_WOOL_FALL, 0.9F, 0.45F);
      }
   }

   static void renderMerryAmount(DrawContext context, int merryValue, int ctxOffset, float merryFactor, float merryFactor2) {
      if (!merryActive && !(merryFactor <= 0.003F) && RenderMatrixUtilsAmount.isPipelineFlag() && !(RenderMatrixUtilsAmount.getAmountFlag() < 0.5F)) {
         float boxX2 = boxX;
         float merryBoxY3 = merryBoxY;
         float merryAmount2 = merryAmount;
         float merryChristmasAmount2 = merryChristmasAmount;
         float merryChristmasAmount3 = merryChristmasAmount3Merry;
         float ctxScaled = merryValue * 0.5F;
         float alphaScaled = ctxOffset * 0.5F;
         boxX = ctxScaled + (boxX2 - ctxScaled) * merryFactor2;
         merryBoxY = alphaScaled + (merryBoxY3 - alphaScaled) * merryFactor2;
         merryAmount = merryAmount2 * merryFactor2;
         merryChristmasAmount = merryChristmasAmount2 * merryFactor2;
         merryChristmasAmount3Merry = merryChristmasAmount3 * merryFactor2;

         try {
            MinecraftClient instance = MinecraftClient.getInstance();
            handleCullNeck(instance);
            float partNeckFactor = merryFactor * RenderMatrixUtilsAmount.getAmountFlag();
            float peekMerry = getPeekMerry();
            boolean merryFlag = running && peekXmasAmount >= 0.2F && peekXmasAmount < 5.0F;
            boolean merryFlag2 = merryFlag && peekXmasAmount >= 4.35F;
            boolean merryFlag3 = peekMerry > 40.0F;
            if (merryFlag3 || merryFlag) {
               handleFogRenderer(instance);
            }

            RENDER_AMOUNTS.handleAmountsAmount(boxX, merryBoxY, merryAmount, merryChristmasAmount, merryChristmasAmount3Merry, peekMerry);
            if (peekMerry < 100.0F) {
               handleMaxAmounts(partNeckFactor);
            }

            if (merryFlag3) {
               float matrixRenderValue = partNeckFactor * getAmount3((peekMerry - 40.0F) / 60.0F);
               if (RenderMatrixUtilsAmount.canCabinMatrix(
                  RENDER_AMOUNTS.merryAverage,
                  RENDER_AMOUNTS.matrixRenderAmount,
                  RENDER_AMOUNTS.merryTable,
                  RENDER_AMOUNTS.matrixRenderTable,
                  RENDER_AMOUNTS.pointCount,
                  boxX,
                  merryBoxY,
                  merryAmount,
                  merryChristmasAmount,
                  peek <= 0.0F,
                  matrixRenderValue
               )) {
                  updateMerryChristmas();
               }
            }

            if (merryFlag2) {
               handleMatrixRender(instance, merryValue, ctxOffset, partNeckFactor, true);
            }

            RENDER_AMOUNTS.handleAmountsMax(partNeckFactor);
            RENDER_AMOUNTS.handleAmountsAmount(partNeckFactor);
            if (merryFlag && !merryFlag2) {
               handleMatrixRender(instance, merryValue, ctxOffset, partNeckFactor, false);
            }

            if (matrixActive) {
               context.setCursor(StandardCursors.POINTING_HAND);
            }

            renderWelcomeHouse(context, partNeckFactor);
         } catch (Throwable e) {
            handleRenderMatrix(String.valueOf(e));

            try {
               MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher().getQueue().clear();
            } catch (Throwable e2) {
            }
         } finally {
            boxX = boxX2;
            merryBoxY = merryBoxY3;
            merryAmount = merryAmount2;
            merryChristmasAmount = merryChristmasAmount2;
            merryChristmasAmount3Merry = merryChristmasAmount3;
         }
      }
   }

   static int getMax() {
      return activeLimbCount;
   }

   static boolean isPeekFlag() {
      return peekActive || peek > 0.01F;
   }

   static boolean isWelcomeWool() {
      if (!running && !isPeekFlag() && !merryActive && RenderMatrixUtilsAmount.isPipelineFlag() && !(RenderMatrixUtilsAmount.getAmountFlag() < 0.98F)) {
         peekActive = true;
         welcomeHouseAmount = 0.0F;
         handlePlaySound(SoundEvents.BLOCK_WOOL_BREAK, 0.6F, 0.4F);
         return true;
      } else {
         return false;
      }
   }

   private static void handleNeckMerry(MatrixStack matrices, int index) {
      float neckMerryValue = 0.06F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 2.1);
      matrices.translate(
         -1.9F + neckMerryFloats[index][0],
         0.85F + neckMerryValue + 0.12F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 2.4 + index * 0.8),
         -7.6F + neckMerryFloats[index][1]
      );
      matrices.scale(0.95F, -0.95F, -0.95F);
   }

   private static float getAmount(float partNeckValue, float partNeckValue2, float partNeckValue3) {
      return getAmount3(Math.min(1.0F, (partNeckValue - partNeckValue2) / 0.15F)) * getAmount3(Math.min(1.0F, (partNeckValue3 - partNeckValue) / 0.2F));
   }

   private static void handleInstance(int partNeckBound) {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton != null) {
         singleton.christmasGifts.handleMaxAmount(HalloweenChristmasType.getYear(LocalDate.now()) * 16L + Math.max(0, Math.min(9, partNeckBound)));
      }
   }

   private PartNeckUtils() {
   }

   static boolean isRunning() {
      return running || isPeekFlag();
   }

   private static void handleMatrixRender(MinecraftClient client, int matrixRenderFactor, int ctxOffset, float matrixRenderValue, boolean matrixRenderFlag) {
      float matrixRenderFactor3 = RenderMatrix.getAmount(matrixRenderFactor, ctxOffset, client.getWindow().getScaleFactor());
      int maxRound = Math.max(8, Math.round(matrixRenderFactor * matrixRenderFactor3));
      int matrixValue = Math.max(8, Math.round(ctxOffset * matrixRenderFactor3));
      CorzRoundedUtils.updateSamplerView();
      if (renderMatrix == null) {
         renderMatrix = new RenderMatrix("front", RenderMatrixUtilsAmount.matrixRenderTable, RenderMatrixUtilsAmount.slimTexturesAmounts);
      }

      renderMatrix.handleTextureCreate(maxRound, matrixValue);
      float matrixRenderValue2 = 2.0F * (float)Math.atan(ctxOffset / 2.0F / 1300.0F);
      MATRIX4F.setPerspective(matrixRenderValue2, (float)matrixRenderFactor / ctxOffset, 65.0F, 26000.0F);
      renderMatrix.handleShaderProjection(0, 0, maxRound, matrixValue, MATRIX4F, (shx, swx) -> handleLimbRelative(shx, swx, matrixRenderFactor, ctxOffset));
      if (matrixRenderFlag) {
         renderMatrix.handleOrderWindow(
            new Matrix4f(), RENDER_AMOUNTS.merryAverage, RENDER_AMOUNTS.matrixRenderAmount, RENDER_AMOUNTS.merryTable, RENDER_AMOUNTS.matrixRenderTable, RENDER_AMOUNTS.pointCount, 0.0F, 0.0F, matrixRenderFactor, ctxOffset, maxRound, matrixValue, matrixRenderValue
         );
      } else {
         renderMatrix.handleOrderView(
            new Matrix4f(),
            0.0F,
            0.0F,
            matrixRenderFactor,
            ctxOffset,
            renderMatrix.getMinForFloat(0.0F),
            renderMatrix.getMin(0.0F),
            renderMatrix.getMinForFloat(maxRound),
            renderMatrix.getMin(matrixValue),
            0.0F,
            0.0F,
            matrixRenderFactor,
            ctxOffset,
            0.0F,
            matrixRenderValue
         );
      }
   }

   static float getAmountXmas() {
      float amountXmas = merryBoxY + merryChristmasAmount - 3.0F;
      if (peekXmasAmount < 4.35F) {
         return amountXmas;
      } else {
         float xmasValue = getAmount3(Math.min(1.0F, (peekXmasAmount - 4.35F) / 0.6500001F));
         return getAmountFloat(amountXmas, getMerryChristmas(amountXmas - 30.0F, xmasAmount), xmasValue);
      }
   }

   private static void handleRenderMatrix(String matrixText) {
      merryActive = true;
      running = false;
      peekActive = false;
      peek = 0.0F;
      matrixActive = false;
      if (renderMatrix != null) {
         renderMatrix.updateProjectionRaw();
      }

      renderMatrix = null;
   }

   static float getMinBoxX() {
      float partNeckValue = Math.min(boxX - 80.0F, -40.0F);
      float partNeckValue2 = boxX + merryAmount - 60.0F;
      if (peekXmasAmount < 0.2F) {
         return partNeckValue;
      } else if (peekXmasAmount < 3.0F) {
         return partNeckValue + (partNeckValue2 - partNeckValue) * ((peekXmasAmount - 0.2F) / 2.8F);
      } else if (peekXmasAmount < 4.35F) {
         return partNeckValue2;
      } else {
         float partNeckFactor = getAmount3(Math.min(1.0F, (peekXmasAmount - 4.35F) / 0.6500001F));
         return getAmountFloat(partNeckValue2, getMerryChristmas(partNeckValue2 - 20.0F, firstMerryAmount), partNeckFactor);
      }
   }

   static float getMerryChristmas(float merryChristmasValue) {
      float peekAngleDegrees32 = peekAngleDegrees;
      if (merryChristmasValue < 3.3F) {
         return peekAngleDegrees32;
      } else if (merryChristmasValue < 4.0F) {
         return getAmountFloat(peekAngleDegrees32, 280.0F, getAmount3((merryChristmasValue - 3.3F) / 0.70000005F));
      } else if (merryChristmasValue < 4.35F) {
         return getAmountFloat(280.0F, 330.0F, getAmount3((merryChristmasValue - 4.0F) / 0.3499999F));
      } else if (merryChristmasValue < 5.7F) {
         return getAmountFloat(330.0F, 420.0F, getAmount3((merryChristmasValue - 4.35F) / 1.3499999F));
      } else if (merryChristmasValue < 10.7F) {
         return 420.0F;
      } else if (merryChristmasValue < 11.5F) {
         float merryChristmasValue2 = (merryChristmasValue - 10.7F) / 0.8000002F;
         float partNeckFactor = merryChristmasValue2 < 0.75F ? merryChristmasValue2 / 0.75F * (merryChristmasValue2 / 0.75F) : 1.0F - 0.12F * (float)Math.sin(Math.PI * (merryChristmasValue2 - 0.75F) / 0.25);
         return getAmountFloat(420.0F, peekAngleDegrees32, partNeckFactor);
      } else {
         return peekAngleDegrees32;
      }
   }

   private static void handleWoolBlock(float woolBlockValue, float partNeckLimit) {
      if (!(peekVisitAmount > 1.5F)) {
         for (int i = 0; i < 6; i++) {
            if (canFlag(woolBlockValue, partNeckLimit, 0.5F + 0.42F * i)) {
               handlePlaySound(SoundEvents.BLOCK_SNOW_STEP, 1.3F, 0.12F);
            }
         }

         if (canFlag(woolBlockValue, partNeckLimit, 3.3999999F)) {
            handlePlaySound(SoundEvents.BLOCK_WOOL_BREAK, 0.6F, 0.45F);
         }

         if (canFlag(woolBlockValue, partNeckLimit, 4.45F)) {
            handlePlaySound(SoundEvents.BLOCK_WOOL_STEP, 0.8F, 0.35F);
         }

         for (int j = 0; j < activeLimbCount; j++) {
            if (canFlag(woolBlockValue, partNeckLimit, limbTable[j] + 0.8F)) {
               handlePlaySound(SoundEvents.BLOCK_WOOL_PLACE, 1.1F, 0.5F);
            }
         }

         for (int k = 0; k < 3; k++) {
            if (canFlag(woolBlockValue, partNeckLimit, 8.4F + 0.16F * k)) {
               handlePlaySound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1.19F, 0.28F);
            }
         }

         if (canFlag(woolBlockValue, partNeckLimit, 11.35F)) {
            handlePlaySound(SoundEvents.BLOCK_WOOL_FALL, 0.8F, 0.55F);
         }
      }
   }

   private static void handleFogRenderer(MinecraftClient client) {
      try {
         CorzRoundedUtils.updateSamplerView();
         AccessorGameRenderer gameRenderer = (AccessorGameRenderer)client.gameRenderer;
         gameRenderer.corzGuiRenderer().render(gameRenderer.corzFogRenderer().getFogBuffer(FogType.NONE));
      } catch (Throwable e) {
      }
   }

   static float getAmountFloat(float partNeckValue, float partNeckValue2, float partNeckFactor) {
      return partNeckValue + (partNeckValue2 - partNeckValue) * partNeckFactor;
   }

   static void handleMerryAmount(float scissorMatrixValue, float randomValue, float merryAmount2, float merryChristmasAmount2, float merryChristmasAmount3) {
      boxX = scissorMatrixValue;
      merryBoxY = randomValue;
      merryAmount = merryAmount2;
      merryChristmasAmount = merryChristmasAmount2;
      merryChristmasAmount3Merry = merryChristmasAmount3;
   }

   private static void handleLegNeck(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      float legNeckValue = 0.06F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 2.1);
      int legNeckValue2 = -4668184;
      matrices.push();
      matrices.translate(-1.9F, 0.85F + legNeckValue, -7.6F);
      matrices.scale(1.0F, -1.0F, -1.0F);
      orderedRenderCommandQueue.submitModelPart(modelPart, matrices, entityCutoutNoCullCullNeck, 15728880, OverlayTexture.DEFAULT_UV, null, legNeckValue2, null);
      matrices.pop();

      for (int i = 0; i < neckHeadModelParts.length; i++) {
         ModelPart modelPart2 = neckHeadModelParts[i];
         float roll = 0.15F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 1.7 + i);
         modelPart2.getChild("leg_fl").roll = roll;
         modelPart2.getChild("leg_fr").roll = roll;
         modelPart2.getChild("leg_bl").roll = -roll;
         modelPart2.getChild("leg_br").roll = -roll;
         modelPart2.getChild("neck").roll = -0.45F + 0.12F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 1.1 + i * 1.3);
         matrices.push();
         handleNeckMerry(matrices, i);
         orderedRenderCommandQueue.submitModelPart(modelPart2, matrices, entityCutoutNoCullLegNeck, 15728880, OverlayTexture.DEFAULT_UV, null, legNeckValue2, null);
         matrices.pop();
      }
   }

   static void updatePeekFlag() {
      peekActive = false;
      peek = 0.0F;
   }

   static int getPartNeckValue() {
      return isChristmas() ? getInstance() : Math.min(9, partNeckValue);
   }

   private static void updateMerryChristmas() {
      if (running
         && RenderMatrixUtilsAmount.canCabinCam(6.3F, 0.0F, 6.7F, merryChristmasAmounts)
         && RenderMatrixUtilsAmount.canCabinCam(6.3F, 1.7578125F, 6.7F, merryChristmasTable)) {
         firstMerryAmount = merryChristmasAmounts[0];
         xmasAmount = merryChristmasAmounts[1];
         xmasPeekAmount = Math.max(0.1F, (merryChristmasAmounts[1] - merryChristmasTable[1]) / 108.984375F);
      }
   }

   static float[] getMerryAmounts() {
      if (!neckFlag) {
         return null;
      } else if (!running) {
         if (peek <= 0.0F) {
            return null;
         } else {
            merryAmounts[0] = -0.4F;
            merryAmounts[1] = 0.0F;
            merryAmounts[2] = 3.6F;
            return merryAmounts;
         }
      } else if (peekXmasAmount < 5.0F) {
         return null;
      } else {
         float merryValue;
         float merryValue2;
         if (peekXmasAmount < 5.25F) {
            merryValue = 6.3F;
            merryValue2 = 6.7F;
         } else {
            float partNeckFactor = getAmount3(Math.min(1.0F, (peekXmasAmount - 5.25F) / 0.6500001F));
            merryValue = getAmountFloat(6.3F, 6.7F, partNeckFactor);
            merryValue2 = getAmountFloat(6.7F, 5.7F, partNeckFactor);
         }

         merryAmounts[0] = merryValue;
         merryAmounts[1] = 0.0F;
         merryAmounts[2] = merryValue2;
         return merryAmounts;
      }
   }

   static void handlePeekAmount(float peekFactor, int amount, int merryValue, int pipelineCabinBound, int rocketsBuyBound, boolean peekFlag, boolean peekFlag2) {
      long nanoTime = System.nanoTime();
      if (lastNanoTime == 0L) {
         lastNanoTime = nanoTime;
         nanoTime3Peek = nanoTime;
      }

      if (!Float.isNaN(peekAmount) && (Math.abs(amount - peekAmount) > 0.5F || Math.abs(merryValue - partNeckAmount) > 0.5F)) {
         nanoTime3Peek = nanoTime;
      }

      peekAmount = amount;
      partNeckAmount = merryValue;
      if (isPeekFlag() && !RenderMatrixUtilsAmount.isPipelineFlag()) {
         updatePeekFlag();
      }

      matrixActive = peekFlag2 && !running && !isPeekFlag() && RenderMatrixUtilsAmount.getAmountFlag() > 0.9F && canMerryAmount(amount, merryValue);
      float peekValue = matrixActive ? 58.0F : 30.0F;
      peekAngleDegrees = peekAngleDegrees + (peekValue - peekAngleDegrees) * (1.0F - (float)Math.exp(-10.0F * peekFactor));
      if (peekActive) {
         welcomeHouseAmount += peekFactor;
         if (welcomeHouseAmount > 15.0F) {
            updateWoolFall();
         }
      }

      float peekValue2 = peekFactor / (peekActive ? 1.15F : 0.9F);
      peek = peekActive ? Math.min(1.0F, peek + peekValue2) : Math.max(0.0F, peek - peekValue2);
      if (running) {
         if ((lastPeekWidth == 0 || pipelineCabinBound == lastPeekWidth && rocketsBuyBound == lastPeekHeight) && RenderMatrixUtilsAmount.isPipelineFlag()) {
            if (parseFloat >= 0.0F) {
               peekXmasAmount = parseFloat;
            } else {
               float woolBlockValue = peekXmasAmount;
               peekXmasAmount = peekXmasAmount + peekFactor * peekVisitAmount;
               handleWoolBlock(woolBlockValue, peekXmasAmount);
               if (peekXmasAmount >= 11.8F) {
                  updatePeekVisit();
               }
            }
         } else {
            updatePeekVisit();
         }
      } else if (!visitPeekFlag && !isPeekFlag() && peekFlag && nanoTime - lastNanoTime > 5000000000L && nanoTime - nanoTime3Peek > 3000000000L) {
         isMerryChristmas();
      }

      lastPeekWidth = pipelineCabinBound;
      lastPeekHeight = rocketsBuyBound;
   }

   static float getAmount3(float partNeckFactor) {
      partNeckFactor = Math.max(0.0F, Math.min(1.0F, partNeckFactor));
      return partNeckFactor * partNeckFactor * (3.0F - 2.0F * partNeckFactor);
   }

   static void updateTime() {
      nanoTime3Peek = System.nanoTime();
   }

   static boolean isMerryChristmas() {
      if (!running && !isPeekFlag() && !merryActive && RenderMatrixUtilsAmount.isPipelineFlag() && !(RenderMatrixUtilsAmount.getAmountFlag() < 0.98F)) {
         running = true;
         visitPeekFlag = true;
         peekXmasAmount = 0.0F;
         peekVisitAmount = 1.0F;
         merryChristmasValue = getPartNeckValue();
         activeLimbCount = Math.max(0, Math.min(limbTable.length, 9 - merryChristmasValue));
         firstMerryAmount = Float.NaN;
         String username = MinecraftClient.getInstance().getSession().getUsername();
         merryChristmasText = username != null && !username.isBlank() && username.length() <= 16 ? "Merry Christmas, " + username + "!" : "Merry Christmas!";
         return true;
      } else {
         return false;
      }
   }

   private static void handlePlaySound(SoundEvent soundEvent, float playSoundValue, float playSoundValue2) {
      try {
         MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(soundEvent, playSoundValue, playSoundValue2));
      } catch (Throwable e) {
      }
   }

   static boolean isRunningFlag() {
      return running;
   }

   static float getXmasPeekPeekXmasAmount() {
      if (peekXmasAmount < 4.35F) {
         return 1.0F;
      } else {
         float xmasPeekValue = Math.min(1.0F, (peekXmasAmount - 4.35F) / 0.6500001F);
         return getAmountFloat(1.0F, getMerryChristmas(0.8F, xmasPeekAmount), xmasPeekValue);
      }
   }

   static boolean canMerryAmount(float merryLimit, float merryValue) {
      float merryValue2 = 6.0F;
      if (merryLimit > boxX + merryAmount - merryValue2 && merryValue <= merryBoxY + merryChristmasAmount - 2.0F * merryValue2) {
         return false;
      } else {
         CHECK_AMOUNTS.handleAmountsAmount(boxX, merryBoxY, merryAmount, merryChristmasAmount, merryChristmasAmount3Merry, Math.max(30.0F, peekAngleDegrees));
         return CHECK_AMOUNTS.canAmounts(merryLimit, merryValue) || CHECK_AMOUNTS.canAmountsMax(merryLimit, merryValue);
      }
   }

   private static void handleCullNeck(MinecraftClient client) {
      if (!neckFlag) {
         neckFlag = true;
         ModelPartUtils.handleIdentifierIdentifiers(client);
         anglesPlayerModel = new PartNeckUtils.SantaModel(client.getLoadedEntityModels().getModelPart(EntityModelLayers.PLAYER));
         noCull = RenderLayers.entityCutoutNoCull(ModelPartUtils.SANTA_SKIN_TEXTURE);
         childCullNeck = HatModelUtils.getChild();
         entityCutoutNoCull = RenderLayers.entityCutoutNoCull(HatModelUtils.getTextureIdentifier(client));
         modelPart = ModelPartUtils.getModelModelPart();

         for (int i = 0; i < neckHeadModelParts.length; i++) {
            neckHeadModelParts[i] = ModelPartUtils.getModel();
         }

         child = neckHeadModelParts[neckHeadModelParts.length - 1].getChild("neck").getChild("head").getChild("nose");
         entityCutoutNoCullCullNeck = RenderLayers.entityCutoutNoCull(ModelPartUtils.IDENTIFIER);
         entityCutoutNoCullLegNeck = RenderLayers.entityCutoutNoCull(ModelPartUtils.REINDEER_TEXTURE);
         eyes = RenderLayers.eyes(ModelPartUtils.BULB_TEXTURE);

         for (PartNeckUtils.SantaPose santaPose : new PartNeckUtils.SantaPose[]{MAX_RENDER_STATE, MIN_RENDER_STATE}) {
            santaPose.hatVisible = true;
            santaPose.jacketVisible = true;
            santaPose.leftSleeveVisible = true;
            santaPose.rightSleeveVisible = true;
            santaPose.leftPantsLegVisible = true;
            santaPose.rightPantsLegVisible = true;
            santaPose.capeVisible = false;
         }
      }
   }

   private static float getMerryChristmas(float merryChristmasValue, float merryChristmasValue2) {
      return Float.isNaN(firstMerryAmount) ? merryChristmasValue : merryChristmasValue2;
   }

   static void updateRunningAmount() {
      if (running) {
         peekVisitAmount = 4.0F;
      }
   }

   private static void handleMaxAmounts(float partNeckFactor) {
      if (RENDER_AMOUNTS.pointCount >= 3) {
         VertexConsumerUtilsAmounts.updatePositionMatrix();

         try {
            int partNeckValue = VertexConsumerUtilsAmounts.getValue(1314313, 0.96F * partNeckFactor);
            int partNeckValue2 = VertexConsumerUtilsAmounts.getValue(16751178, (0.35F + 0.25F * (peekAngleDegrees - 30.0F) / 28.0F) * partNeckFactor);

            for (int i = 1; i < RENDER_AMOUNTS.pointCount - 1; i++) {
               VertexConsumerUtilsAmounts.handleVertexColor(
                  RENDER_AMOUNTS.merryTable[0],
                  RENDER_AMOUNTS.matrixRenderTable[0],
                  partNeckValue,
                  RENDER_AMOUNTS.merryTable[i],
                  RENDER_AMOUNTS.matrixRenderTable[i],
                  partNeckValue,
                  RENDER_AMOUNTS.merryTable[i + 1],
                  RENDER_AMOUNTS.matrixRenderTable[i + 1],
                  partNeckValue
               );
            }

            float partNeckValue3 = 0.85F + 0.15F * (float)Math.sin(RenderMatrixUtilsAmount.legNeckAmount * 9.0);
            VertexConsumerUtilsAmounts.handleAmounts(
               boxX + merryAmount - 6.0F,
               merryBoxY + merryChristmasAmount - 6.0F,
               26.0F * partNeckValue3,
               22.0F * partNeckValue3,
               partNeckValue2,
               VertexConsumerUtilsAmounts.getValue(16742960, 0.12F * partNeckFactor)
            );
         } finally {
            VertexConsumerUtilsAmounts.updateConsumerVertex();
         }
      }
   }

   static void handleLimbAmount(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      if (neckFlag) {
         handleLegNeck(matrices, orderedRenderCommandQueue);
         float[] merryAmounts = getMerryAmounts();
         if (merryAmounts != null) {
            float peekXmasAmount2 = peekXmasAmount;
            PartNeckUtils.SantaPose santaPose = MIN_RENDER_STATE;
            santaPose.age = (float)RenderMatrixUtilsAmount.legNeckAmount * 20.0F;
            santaPose.isInSneakingPose = false;
            santaPose.santaPoseAmount = 0.0F;
            santaPose.leftArmBlend = 0.0F;
            santaPose.relativeHeadYaw = 0.0F;
            santaPose.pitch = 2.0F;
            if (!running) {
               santaPose.poseIndex = 4;
               santaPose.limbSwingAmplitude = 0.0F;
               santaPose.limbAmount = getAmount3(Math.min(1.0F, welcomeHouseAmount / 0.4F))
                  * (0.5F + 0.5F * Math.max(0.0F, (float)Math.sin(welcomeHouseAmount * 1.4F)));
            } else {
               float limbLimit = getAmount3((peekXmasAmount2 - 5.25F) / 0.15F) * (1.0F - getAmount3((peekXmasAmount2 - 5.75F) / 0.15F));
               santaPose.poseIndex = limbLimit > 0.0F ? 3 : 4;
               santaPose.limbSwingAnimationProgress = (peekXmasAmount2 - 5.25F) * 9.0F;
               santaPose.limbSwingAmplitude = 0.8F * limbLimit;
               float limbTotal = 0.0F;
               float limbTotal2 = 0.0F;

               for (int i = 0; i < activeLimbCount; i++) {
                  float limbLimit2 = peekXmasAmount2 - (limbTable[i] - 0.4F - 0.15F);
                  if (!(limbLimit2 < 0.0F) && !(limbLimit2 > 0.85F)) {
                     float limbFactor;
                     float amountFloat2;
                     if (limbLimit2 < 0.4F) {
                        float limbValue = limbLimit2 / 0.4F;
                        limbFactor = getAmount3(Math.min(1.0F, limbValue * 2.5F));
                        amountFloat2 = getAmountFloat(-1.1F, -0.55F, limbValue);
                     } else {
                        float partNeckFactor = (limbLimit2 - 0.4F) / 0.45F;
                        limbFactor = 1.0F - getAmount3(partNeckFactor);
                        amountFloat2 = getAmountFloat(-0.55F, -3.55F, getAmount3(Math.min(1.0F, partNeckFactor * 3.0F)));
                     }

                     limbTotal += limbFactor;
                     limbTotal2 += limbFactor * amountFloat2;
                  }
               }

               if (limbTotal > 0.0F) {
                  santaPose.leftArmBlend = Math.min(1.0F, limbTotal);
                  santaPose.anglesAmount = limbTotal2 / limbTotal;
               }

               santaPose.limbAmount = peekXmasAmount2 >= 8.15F ? getAmount3(Math.min(1.0F, (peekXmasAmount2 - 8.4F + 0.25F) / 0.3F)) : 0.0F;
            }

            float degrees = getDegreesXmas();
            matrices.push();
            matrices.translate(merryAmounts[0], 0.0F, merryAmounts[2]);
            matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(degrees)));
            matrices.scale(0.9375F, -0.9375F, -0.9375F);
            matrices.translate(0.0F, -1.501F, 0.0F);
            handleModelAngles(matrices, orderedRenderCommandQueue, santaPose);
            matrices.pop();
            if (running) {
               float[][] floats = StateDefault.getAmounts();

               for (int j = 0; j < activeLimbCount; j++) {
                  float limbLimit3 = (peekXmasAmount2 - limbTable[j]) / 0.8F;
                  if (!(limbLimit3 < 0.0F) && !(limbLimit3 >= 1.0F)) {
                     int index = merryChristmasValue + j;
                     if (index < floats.length) {
                        float[] limbValues = floats[index];
                        float deltaX = limbValues[0] - merryAmounts[0];
                        float deltaZ = limbValues[1] - merryAmounts[2];
                        float limbDivisor = Math.max(0.01F, (float)Math.hypot(deltaX, deltaZ));
                        float limbValue5 = merryAmounts[0] + deltaX / limbDivisor * 0.75F;
                        float limbValue6 = 1.85F;
                        float limbValue7 = merryAmounts[2] + deltaZ / limbDivisor * 0.75F;
                        float amountFloat = getAmountFloat(limbValue5, limbValues[0], limbLimit3);
                        float z2 = getAmountFloat(limbValue7, limbValues[1], limbLimit3);
                        float y2 = getAmountFloat(limbValue6, 0.0F, limbLimit3) + 3.6F * limbLimit3 * (1.0F - limbLimit3);
                        RenderMatrixUtilsAmount.handlePartModel(matrices, orderedRenderCommandQueue, amountFloat, y2, z2, limbValues[2], limbValues[3] + 360.0F * getAmount3(limbLimit3), (index + 1) % 4);
                     }
                  }
               }
            }
         }
      }
   }

   static void updateTimeMerry() {
      lastNanoTime = System.nanoTime();
      nanoTime3Peek = lastNanoTime;
      peekAmount = Float.NaN;
      peekActive = false;
      peek = 0.0F;
      matrixActive = false;
      peekAngleDegrees = 30.0F;
      if (running) {
         updatePeekVisit();
      }
   }

   private static void handleMax(int partNeckBound) {
      if (isChristmas()) {
         handleInstance(partNeckBound);
      } else {
         partNeckValue = Math.max(0, Math.min(9, partNeckBound));
      }
   }

   static final class SantaModel extends PlayerEntityModel {
      @Override
      public void setAngles(PlayerEntityRenderState angles) {
         super.setAngles(angles);
         if (angles instanceof PartNeckUtils.SantaPose santaPose) {
            float santaPoseAmount = santaPose.santaPoseAmount;
            if (santaPoseAmount > 0.0F) {
               this.body.pitch += 0.5F * santaPoseAmount;
               this.body.originY += 3.2F * santaPoseAmount;
               this.head.originY += 4.2F * santaPoseAmount;
               this.rightArm.originY += 3.2F * santaPoseAmount;
               this.leftArm.originY += 3.2F * santaPoseAmount;
               this.rightArm.pitch += 0.4F * santaPoseAmount;
               this.leftArm.pitch += 0.4F * santaPoseAmount;
               this.rightLeg.originZ += 4.0F * santaPoseAmount;
               this.leftLeg.originZ += 4.0F * santaPoseAmount;
               this.rightLeg.originY += 0.2F * santaPoseAmount;
               this.leftLeg.originY += 0.2F * santaPoseAmount;
            }

            switch (santaPose.poseIndex) {
               case 0:
                  if (santaPose.rightArmBlend > 0.0F) {
                     this.rightArm.pitch = PartNeckUtils.getAmountFloat(this.rightArm.pitch, -2.05F, santaPose.rightArmBlend);
                     this.rightArm.yaw = PartNeckUtils.getAmountFloat(this.rightArm.yaw, -0.55F, santaPose.rightArmBlend);
                     this.rightArm.roll = PartNeckUtils.getAmountFloat(this.rightArm.roll, 0.1F, santaPose.rightArmBlend);
                  }
                  break;
               case 1:
                  float limbRelativeAmount = santaPose.limbRelativeAmount;
                  this.rightArm.pitch = PartNeckUtils.getAmountFloat(this.rightArm.pitch, -2.9F, limbRelativeAmount);
                  this.leftArm.pitch = PartNeckUtils.getAmountFloat(this.leftArm.pitch, -2.9F, limbRelativeAmount);
                  this.rightArm.roll = PartNeckUtils.getAmountFloat(this.rightArm.roll, 0.18F, limbRelativeAmount);
                  this.leftArm.roll = PartNeckUtils.getAmountFloat(this.leftArm.roll, -0.18F, limbRelativeAmount);
                  this.rightArm.yaw = PartNeckUtils.getAmountFloat(this.rightArm.yaw, 0.0F, limbRelativeAmount);
                  this.leftArm.yaw = PartNeckUtils.getAmountFloat(this.leftArm.yaw, 0.0F, limbRelativeAmount);
                  break;
               case 2:
                  float partNeckFactor = santaPose.limbRelativeAmount;
                  this.rightArm.pitch = PartNeckUtils.getAmountFloat(this.rightArm.pitch, -2.9F, partNeckFactor);
                  this.leftArm.pitch = PartNeckUtils.getAmountFloat(this.leftArm.pitch, -2.9F, partNeckFactor);
                  this.rightArm.roll = PartNeckUtils.getAmountFloat(this.rightArm.roll, 0.18F, partNeckFactor);
                  this.leftArm.roll = PartNeckUtils.getAmountFloat(this.leftArm.roll, -0.18F, partNeckFactor);
                  break;
               default:
                  if (santaPose.leftArmBlend > 0.0F) {
                     this.leftArm.pitch = PartNeckUtils.getAmountFloat(this.leftArm.pitch, santaPose.anglesAmount, santaPose.leftArmBlend);
                     this.leftArm.roll = PartNeckUtils.getAmountFloat(this.leftArm.roll, -0.35F, santaPose.leftArmBlend);
                  }

                  if (santaPose.limbAmount > 0.0F) {
                     this.leftArm.pitch = PartNeckUtils.getAmountFloat(this.leftArm.pitch, -2.55F, santaPose.limbAmount);
                     this.leftArm.roll = PartNeckUtils.getAmountFloat(this.leftArm.roll, 0.55F + 0.38F * (float)Math.sin(santaPose.age * 0.55F), santaPose.limbAmount);
                  }
            }
         }
      }

      SantaModel(ModelPart modelPart) {
         super(modelPart, false);
      }
   }

   static final class SantaPose extends PlayerEntityRenderState {
      int poseIndex;
      float rightArmBlend;
      float limbAmount;
      float limbRelativeAmount;
      float leftArmBlend;
      float anglesAmount;
      float santaPoseAmount;
   }
}
