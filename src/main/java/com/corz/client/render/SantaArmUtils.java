package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackUtilsRender;
import java.util.Arrays;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

final class SantaArmUtils {
   private static ModelPart modelPart;
   static final String[][] buddyTrapTexts = new String[][]{
      {"Santa has a secret house.", "Under the GUI corner? No way!"},
      {"Have you seen Santa?", "He's late. Again."},
      {"Who nibbled my arm?!", "...not me. *crumbs*"},
      {"Hot cocoa later?", "Only with marshmallows!"},
      {"Don't look down.", "We're cookies. We crumble anyway."},
      {"Is it snowing again?", "It's always snowing here."},
      {"Poke me three times...", "Shh! You'll wake Santa!"},
      {"Snow on the edge again!", "Swipe it off, it's fun!"},
      {"Psst... that corner is loose.", "Don't lift it! ...okay, lift it."},
      {"So many modules...", "And not one for frosting."},
      {"Is that a creeper down there?", "It's a snowman. Probably."},
      {"Merry Christmas!", "Merry Crispmas!"},
      {"I asked Santa for a cookie jar.", "That's a trap, buddy."}
   };
   static final String[] boopHeheTexts = new String[]{"Hey! That tickles!", "Ouch, my gumdrop!", "Careful, I crumble!", "Hehe!", "Boop!"};
   private static float boopHeheAmount = -0.8F;
   private static final float[] boopHeheAmounts = new float[2];
   private static final float[] boopHeheTable = new float[]{-1.0F, -1.0F};
   private static final String[] wokeSantaTexts = new String[2];
   private static final long[] wokeSantaTimes = new long[3];
   private static float boopHeheFade;
   private static ModelPart modelBoopHehe;
   private static float wokeSantaAmount;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static int textSetIndex;
   private static RenderMatrix renderMatrix;
   static final float[][] psstSantaAmounts = new float[][]{{0.35F, 1.55F, 0.0F}, {1.6F, 2.8F, 1.0F}, {3.2F, 4.4F, 0.0F}, {4.5F, 5.8F, 1.0F}};
   private static boolean boopHeheActive;
   private static int wokeSantaValue;
   private static boolean wokeSantaActive;
   private static float santaArmAmount;
   static final String[] hisSecretTexts = new String[]{"Psst... is that Santa?", "Shh! He's sneaking in!", "He lifted the GUI!", "His secret house!"};
   private static RenderLayer noCull;

   private SantaArmUtils() {
   }

   private static int getPsstSanta() {
      if (!PartNeckUtils.isRunningFlag()) {
         return -1;
      } else {
         float xmasPeek = PartNeckUtils.getXmasPeek();

         for (int i = 0; i < psstSantaAmounts.length; i++) {
            if (xmasPeek >= psstSantaAmounts[i][0] && xmasPeek < psstSantaAmounts[i][1]) {
               return i;
            }
         }

         return -1;
      }
   }

   private static int getBoopHehe() {
      if (PartNeckUtils.isRunningFlag()) {
         int psstSanta = getPsstSanta();
         return psstSanta < 0 ? -1 : (int)psstSantaAmounts[psstSanta][2];
      } else if (boopHeheAmount < 0.0F) {
         return -1;
      } else if (boopHeheAmount < 2.9F) {
         return 0;
      } else if (boopHeheAmount < 3.3500001F) {
         return -1;
      } else {
         return boopHeheAmount < 6.25F ? 1 : -1;
      }
   }

   static boolean canWokeSanta(double wokeSantaLimit, double heightAmount) {
      if (wokeSantaActive && !(boopHeheFade < 0.5F)) {
         for (int i = 0; i < 2; i++) {
            float wokeSantaValue2 = santaArmAmount - 96.0F + i * 31.0F;
            if (wokeSantaLimit >= wokeSantaValue2 - 10.0F && wokeSantaLimit <= wokeSantaValue2 + 10.0F && heightAmount >= wokeSantaAmount - 30.0F && heightAmount <= wokeSantaAmount + 10.0F) {
               boopHeheAmounts[i] = 1.0F;
               boopHeheTable[i] = 0.0F;
               wokeSantaTexts[i] = boopHeheTexts[(int)(
                     RenderMatrixUtilsAmount.getAmount((int)(RenderMatrixUtilsAmount.legNeckAmount * 7.0), i) * boopHeheTexts.length
                  )
                  % boopHeheTexts.length];
               long nanoTime = System.nanoTime();
               wokeSantaTimes[wokeSantaValue] = nanoTime;
               wokeSantaValue = (wokeSantaValue + 1) % wokeSantaTimes.length;
               long wokeSantaValue3 = wokeSantaTimes[wokeSantaValue];
               if (wokeSantaValue3 != 0L && nanoTime - wokeSantaValue3 <= 2500000000L && PartNeckUtils.isMerryChristmas()) {
                  Arrays.fill(wokeSantaTimes, 0L);
                  wokeSantaTexts[i] = "You woke up Santa!";
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static void handleChildLeg(ModelPart modelPart, int childLegValue, boolean childLegFlag, float angle, float legNeckAmount) {
      ModelPart child = modelPart.getChild("head");
      ModelPart child2 = modelPart.getChild("arm_l");
      ModelPart child3 = modelPart.getChild("arm_r");
      ModelPart child4 = modelPart.getChild("leg_l");
      ModelPart child5 = modelPart.getChild("leg_r");
      float sin = (float)Math.sin(angle * 1.6F);
      child4.pitch = -1.2F + 0.28F * sin;
      child5.pitch = -1.2F - 0.28F * sin;
      float childLegFactor = childLegValue == 0 ? 1.0F : -1.0F;
      child.roll = childLegFactor * (childLegFlag ? 0.16F + 0.05F * (float)Math.sin(legNeckAmount * 9.0F) : 0.05F);
      child.pitch = childLegFlag ? 0.08F * (float)Math.sin(legNeckAmount * 7.3F) : 0.04F * (float)Math.sin(angle);
      child2.pitch = childLegFlag && childLegValue == 1 ? -1.2F - 0.35F * (float)Math.sin(legNeckAmount * 6.0F) : -0.25F;
      child3.pitch = childLegFlag && childLegValue == 0 ? -1.2F - 0.35F * (float)Math.sin(legNeckAmount * 6.0F) : -0.25F;
      child2.roll = -0.35F;
      child3.roll = 0.35F;
   }

   private static void renderBoopHehe(DrawContext context, int boopHeheValue, int boopHeheLimit, int boopHeheValue2, float boopHeheFactor) {
      if (boopHeheLimit >= 46) {
         int boopHehe = getBoopHehe();

         for (int i = 0; i < 2; i++) {
            String boopHeheText = null;
            float boopHeheFactor2 = 0.0F;
            float x2 = 1.0F;
            if (boopHeheTable[i] >= 0.0F && boopHeheTable[i] < 1.6F) {
               boopHeheText = wokeSantaTexts[i];
               boopHeheFactor2 = PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheTable[i] / 0.12F))
                  * PartNeckUtils.getAmount3(Math.min(1.0F, (1.6F - boopHeheTable[i]) / 0.25F));
               x2 = 0.85F + 0.15F * PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheTable[i] / 0.15F));
            } else if (boopHehe == i && getPsstSanta() >= 0) {
               int psstSanta = getPsstSanta();
               float boopHeheValue4 = PartNeckUtils.getXmasPeek() - psstSantaAmounts[psstSanta][0];
               float boopHeheValue5 = psstSantaAmounts[psstSanta][1] - psstSantaAmounts[psstSanta][0];
               boopHeheText = hisSecretTexts[psstSanta];
               boopHeheFactor2 = PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheValue4 / 0.14F)) * PartNeckUtils.getAmount3(Math.min(1.0F, (boopHeheValue5 - boopHeheValue4) / 0.18F));
               x2 = 0.86F + 0.14F * PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheValue4 / 0.16F));
            } else if (boopHehe == i) {
               float boopHeheValue6 = boopHeheAmount - (i == 0 ? 0.0F : 3.3500001F);
               boopHeheText = buddyTrapTexts[textSetIndex][i];
               boopHeheFactor2 = PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheValue6 / 0.16F)) * PartNeckUtils.getAmount3(Math.min(1.0F, (2.9F - boopHeheValue6) / 0.22F));
               x2 = 0.86F + 0.14F * PartNeckUtils.getAmount3(Math.min(1.0F, boopHeheValue6 / 0.18F));
            }

            if (boopHeheText != null && !(boopHeheFactor2 <= 0.01F)) {
               float x = getAmount(boopHeheValue, boopHeheValue2, i) + (i == 0 ? -2.0F : 2.0F);
               float y = boopHeheLimit - 2.2F - 22.8F - 4.0F - 9.0F * (float)Math.sin(Math.min(1.0F, boopHeheAmounts[i]) * Math.PI);
               MatrixStackUtilsRender.renderTranslatePop(context, boopHeheText, x, y, 0.72F, boopHeheFactor2 * boopHeheFactor, i == 1, x2);
            }
         }
      }
   }

   static float getAmount(int santaArmValue, int santaArmValue2, int i) {
      return santaArmValue + santaArmValue2 - 96.0F + i * 31.0F;
   }

   static void updateBoopHehe() {
      if (renderMatrix != null) {
         renderMatrix.updateProjectionRaw();
      }

      renderMatrix = null;
      boopHeheFade = 0.0F;
      boopHeheAmount = -0.8F;
      Arrays.fill(wokeSantaTimes, 0L);
      boopHeheTable[0] = boopHeheTable[1] = -1.0F;
      boopHeheAmounts[0] = boopHeheAmounts[1] = 0.0F;
   }

   static void renderBoopHehe(DrawContext context, int boopHeheValue, int santaArmLimit, int boopHeheValue2, int boopHeheBound, int boopHeheValue3) {
      float boopHeheFactor = RenderMatrixUtilsAmount.frameDeltaSeconds;
      boolean boopHeheFlag = RenderMatrixUtilsAmount.isPipelineFlag() && canFlag(santaArmLimit) && RenderMatrixUtilsAmount.getAmountFlag() > 0.5F;
      float boopHeheValue4 = boopHeheFlag ? 1.0F : 0.0F;
      boopHeheFade = boopHeheFade + (boopHeheValue4 - boopHeheFade) * (1.0F - (float)Math.exp(-5.0F * boopHeheFactor));
      if (!boopHeheFlag && boopHeheFade < 0.01F) {
         boopHeheFade = 0.0F;
      }

      wokeSantaActive = false;
      if (!(boopHeheFade <= 0.01F) && !boopHeheActive) {
         float matrixRenderValue = boopHeheFade * CorzRoundedUtils.getPanelOverlay();
         if (!(matrixRenderValue <= 0.01F)) {
            if (!PartNeckUtils.isRunningFlag()) {
               boopHeheAmount += boopHeheFactor;
            }

            float boopHeheValue6 = 11.05F;
            if (boopHeheAmount > boopHeheValue6) {
               boopHeheAmount -= boopHeheValue6;
               textSetIndex = (textSetIndex + 1) % buddyTrapTexts.length;
            }

            for (int i = 0; i < 2; i++) {
               boopHeheAmounts[i] = Math.max(0.0F, boopHeheAmounts[i] - boopHeheFactor * 2.6F);
               if (boopHeheTable[i] >= 0.0F) {
                  boopHeheTable[i] = boopHeheTable[i] + boopHeheFactor;
                  if (boopHeheTable[i] > 2.0F) {
                     boopHeheTable[i] = -1.0F;
                  }
               }
            }

            float orderViewFactor = boopHeheValue + boopHeheValue2 - 150.0F;
            float matrixRenderFactor = boopHeheValue + boopHeheValue2 - 16.0F;
            float pilotStatusBound = santaArmLimit - 56.0F;
            float ctxOffset = santaArmLimit + 22.0F;
            santaArmAmount = boopHeheValue + boopHeheValue2;
            wokeSantaAmount = santaArmLimit;
            wokeSantaActive = true;

            try {
               MinecraftClient instance = MinecraftClient.getInstance();
               if (modelPart == null) {
                  ModelPartUtils.handleIdentifierIdentifiers(instance);
                  modelPart = ModelPartUtils.getModel9();
                  modelBoopHehe = ModelPartUtils.getModel9();
                  noCull = RenderLayers.entityCutoutNoCull(ModelPartUtils.GINGERBREAD_TEXTURE);
               }

               float boopHeheFactor2 = (float)instance.getWindow().getFramebufferWidth() / Math.max(1, boopHeheBound);
               int maxRound = Math.max(8, Math.round((matrixRenderFactor - orderViewFactor) * boopHeheFactor2));
               int rocketsBuyBound = Math.max(8, Math.round((ctxOffset - pilotStatusBound) * boopHeheFactor2));
               float boopHeheFactor3 = 55.0F;
               MATRIX4F.setFrustum(
                  (orderViewFactor - boopHeheBound / 2.0F) * boopHeheFactor3 / 1100.0F,
                  (matrixRenderFactor - boopHeheBound / 2.0F) * boopHeheFactor3 / 1100.0F,
                  -(ctxOffset - boopHeheValue3 / 2.0F) * boopHeheFactor3 / 1100.0F,
                  -(pilotStatusBound - boopHeheValue3 / 2.0F) * boopHeheFactor3 / 1100.0F,
                  boopHeheFactor3,
                  22000.0F
               );
               CorzRoundedUtils.updateSamplerView();
               if (renderMatrix == null) {
                  renderMatrix = new RenderMatrix("ginger", RenderMatrixUtilsAmount.matrixRenderTable, RenderMatrixUtilsAmount.slimTexturesAmounts);
               }

               renderMatrix.handleTextureCreate(maxRound, rocketsBuyBound);
               renderMatrix.handleShaderProjection(0, 0, maxRound, rocketsBuyBound, MATRIX4F, (matrices, swx) -> handlePartBoop(matrices, swx, boopHeheValue, santaArmLimit, boopHeheValue2, boopHeheBound, boopHeheValue3));
               Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
               renderMatrix.handleOrderView(
                  positionMatrix,
                  orderViewFactor,
                  pilotStatusBound,
                  matrixRenderFactor - orderViewFactor,
                  ctxOffset - pilotStatusBound,
                  renderMatrix.getMinForFloat(0.0F),
                  renderMatrix.getMin(0.0F),
                  renderMatrix.getMinForFloat(maxRound),
                  renderMatrix.getMin(rocketsBuyBound),
                  orderViewFactor,
                  pilotStatusBound,
                  matrixRenderFactor,
                  ctxOffset,
                  0.0F,
                  matrixRenderValue
               );
               renderBoopHehe(context, boopHeheValue, santaArmLimit, boopHeheValue2, boopHeheFade);
            } catch (Throwable e) {
               boopHeheActive = true;

               try {
                  MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher().getQueue().clear();
               } catch (Throwable e2) {
               }
            }
         }
      }
   }

   static boolean canFlag(int santaArmLimit) {
      return santaArmLimit >= 26;
   }

   private static void handlePartBoop(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int partBoopValue, int santaArmLimit, int partBoopValue3, int boopHeheBound, int partBoopValue5) {
      matrices.translate(0.0F, 0.0F, -1100.0F);
      matrices.scale(1.0F, -1.0F, 1.0F);
      matrices.translate(-boopHeheBound / 2.0F, -partBoopValue5 / 2.0F, 0.0F);
      int boopHehe = getBoopHehe();
      float legNeckAmount = (float)RenderMatrixUtilsAmount.legNeckAmount;

      for (int i = 0; i < 2; i++) {
         ModelPart modelPart3 = i == 0 ? modelPart : modelBoopHehe;
         boolean partBoopFlag = boopHehe == i || boopHeheTable[i] >= 0.0F && boopHeheTable[i] < 1.4F;
         float angle = legNeckAmount * 1.7F + i * 1.9F;
         float partBoopValue7 = -9.0F * (float)Math.sin(Math.min(1.0F, boopHeheAmounts[i]) * Math.PI);
         handleChildLeg(modelPart3, i, partBoopFlag, angle, legNeckAmount);
         matrices.push();
         matrices.translate(getAmount(partBoopValue, partBoopValue3, i), santaArmLimit - 2.2F + partBoopValue7, 6.0F);
         matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(i == 0 ? -24.0 : 24.0)));
         matrices.multiply(new Quaternionf().rotateX(-0.12F));
         float x = 30.4F;
         matrices.scale(x, x, -x);
         orderedRenderCommandQueue.submitModelPart(modelPart3, matrices, noCull, 15728880, OverlayTexture.DEFAULT_UV, null, -5678, null);
         matrices.pop();
      }
   }
}
