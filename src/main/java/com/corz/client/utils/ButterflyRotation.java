package com.corz.client.utils;

import com.corz.client.render.StageKnife;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import com.corz.client.categories.visuals.SwordInspect;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeInspectClip;
import com.corz.client.utils.KnifeFinishTextures;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.C2Preview;
import com.corz.client.utils.KnifePreviewRig;
import com.corz.client.utils.C2Knife;

public final class ButterflyRotation {
   private ItemStack itemStack;
   private String stilettoText;
   static final Matrix4f MATRIX4F = new Matrix4f(0.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
   private float rotationXDegrees;
   private final ItemRenderState itemRenderState = new ItemRenderState();
   private final Matrix4f stilettoMatrix4f = new Matrix4f();
   private final StageKnife.Scene stilettoScene = this::handleAmountMultiply;
   private static final String[] butterflyBayonetTexts = new String[]{"Stiletto", "M9 Bayonet", "Butterfly"};
   private float stilettoAmount;
   private float multiplyAmount;
   private float butterflyRotationAmount;
   private static final float[] butterflyBayonetAmounts = new float[]{-34.0F, 0.0F, 34.0F};
   private KnifeFinish.Finish finish;
   private String multiplyText;
   private static final float[] randomAmounts = new float[]{-0.26F, 0.0F, 0.26F};
   private final StageKnife.Scene randomStilettoScene = this::handleMultiplyDegrees;
   private String randomStilettoText;
   private KnifeInspectClip knifeInspectClip;
   private float randomStilettoAmount;

   private void handleMultiplyDegrees(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      String randomStilettoText = this.randomStilettoText;
      matrices.translate(0.0F, 0.0F, -this.randomStilettoAmount);
      if (!"Random".equals(randomStilettoText)) {
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.0F));
         switch (randomStilettoText) {
            case "Spin":
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(58.0F));
               break;
            case "Flip":
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-52.0F));
               break;
            case "Barrel Roll":
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0F));
               break;
            case "Twirl":
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(28.0F));
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30.0F));
               break;
            case "Toss":
               matrices.translate(0.0F, 0.12F, 0.0F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
               break;
            default:
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(26.0F));
         }

         this.handleMultiplyPush(matrices, orderedRenderCommandQueue, randomStilettoText, null, null, 0.0F, 0.0F, false);
      } else {
         for (int i = 0; i < butterflyBayonetTexts.length; i++) {
            matrices.push();
            matrices.translate(randomAmounts[i], 0.0F, -0.1F * Math.abs(randomAmounts[i]));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(butterflyBayonetAmounts[i] - -38.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(18.0F));
            matrices.scale(0.62F, 0.62F, 0.62F);
            this.handleMultiplyPush(matrices, orderedRenderCommandQueue, butterflyBayonetTexts[i], null, null, 0.0F, 0.0F, false);
            matrices.pop();
         }
      }
   }

   public static void handleButterflyInstance(String butterflyText, KnifeFinish.Finish finish) {
      if (finish != null && !finish.isCompactFlag() && butterflyText != null) {
         if ("Butterfly".equals(butterflyText)) {
            KnifeFinishTextures.handleCorzclientService(KnifeRig.INSTANCE, finish);
         } else if (KnifeInspectClip.canBayonetStiletto(butterflyText)) {
            KnifeFinishTextures.handleCorzclientService(KnifeRig.getBayonetStiletto(butterflyText), finish);
         }
      }
   }

   public static float getAmount(float butterflyRotationFactor, float butterflyRotationDivisor, float butterflyRotationBound) {
      double angle = Math.toRadians(28.0) / 2.0;
      double hashRegistryBound = 1.0 / Math.sin(angle) / butterflyRotationDivisor;
      if (butterflyRotationFactor < 1.0F) {
         double atan = Math.atan(Math.tan(angle) * butterflyRotationFactor);
         hashRegistryBound = Math.max(hashRegistryBound, 1.0 / Math.sin(atan) / butterflyRotationDivisor);
      }

      return (float)(hashRegistryBound / Math.max(0.05F, butterflyRotationBound));
   }

   public static boolean canButterflyInstance(String butterflyText, KnifeFinish.Finish finish) {
      if (finish == null || finish.isCompactFlag() || butterflyText == null) {
         return true;
      } else if ("Butterfly".equals(butterflyText)) {
         return C2Preview.canInstance(finish) || KnifeFinishTextures.canEntrysCorzclient(KnifeRig.INSTANCE, finish);
      } else {
         return !KnifeInspectClip.canBayonetStiletto(butterflyText)
            ? true
            : KnifePreviewRig.canFlag(butterflyText, finish) || KnifeFinishTextures.canEntrysCorzclient(KnifeRig.getBayonetStiletto(butterflyText), finish);
      }
   }

   private void handleAmountMultiply(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      matrices.translate(0.0F, 0.05F, -this.randomStilettoAmount);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.rotationXDegrees));
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(this.multiplyAmount));
      float multiplyValue = this.stilettoAmount > 0.0F ? Math.max(0.0F, Math.min(1.0F, this.butterflyRotationAmount / this.stilettoAmount)) : 0.0F;
      this.handleMultiplyPush(matrices, orderedRenderCommandQueue, this.multiplyText, this.stilettoText, this.knifeInspectClip, this.butterflyRotationAmount, multiplyValue, true);
   }

   public void handleAmountStiletto(
      StageKnife stageKnife,
      int stilettoValue,
      int stilettoBound,
      String text,
      String text2,
      KnifeInspectClip knifeInspectClip,
      float amount,
      float amount2,
      float cubeSchematicValue,
      float amount4,
      float butterflyRotationBound,
      KnifeFinish.Finish finish
   ) {
      float butterflyRotationFactor = (float)stilettoValue / Math.max(1, stilettoBound);
      this.multiplyText = text;
      this.stilettoText = text2;
      this.knifeInspectClip = knifeInspectClip;
      this.butterflyRotationAmount = amount;
      this.stilettoAmount = amount2;
      this.multiplyAmount = cubeSchematicValue;
      this.rotationXDegrees = amount4;
      this.finish = finish;
      this.randomStilettoAmount = getAmount(butterflyRotationFactor, 0.9F, butterflyRotationBound);
      this.handlePerspectiveMatrix(butterflyRotationFactor, this.randomStilettoAmount);
      stageKnife.handleShaderProjection(0, 0, stilettoValue, stilettoBound, this.stilettoMatrix4f, this.stilettoScene);
   }

   public static boolean canBayonetStiletto(KnifeFinish.Finish finish) {
      boolean instance = C2Preview.canInstance(finish);
      boolean bayonetStilettoFlag = KnifePreviewRig.canFlag("M9 Bayonet", finish);
      boolean canFlag = KnifePreviewRig.canFlag("Stiletto", finish);
      return instance && bayonetStilettoFlag && canFlag;
   }

   private void handlePerspectiveMatrix(float perspectiveMatrixValue, float perspectiveMatrixValue2) {
      float max3 = Math.max(0.05F, perspectiveMatrixValue2 - 2.0F);
      float perspectiveMatrixValue4 = perspectiveMatrixValue2 + 2.0F;
      this.stilettoMatrix4f.setPerspective((float)Math.toRadians(28.0), perspectiveMatrixValue, max3, perspectiveMatrixValue4);
   }

   public boolean canButterfly(String butterflyText) {
      C2Knife c2Knife = C2Knife.getC2Knife(butterflyText);
      return c2Knife != null ? C2Preview.canNone(c2Knife) : "Butterfly".equals(butterflyText) && C2Preview.isButterfly();
   }

   public boolean canRandomStiletto(StageKnife stageKnife, int randomStilettoValue, int shaderProjectionLimit, String randomStilettoText, KnifeFinish.Finish finish) {
      boolean randomStilettoFlag = "Random".equals(randomStilettoText)
         ? this.canButterflyEmpty("Butterfly") && this.canButterflyEmpty("M9 Bayonet") && this.canButterflyEmpty("Stiletto")
         : this.canButterflyEmpty(randomStilettoText);
      if (randomStilettoFlag) {
         boolean randomStiletto = "Random".equals(randomStilettoText)
            ? canButterflyInstance("Butterfly", finish) & canButterflyInstance("M9 Bayonet", finish) & canButterflyInstance("Stiletto", finish)
            : canButterflyInstance(randomStilettoText, finish);
         this.randomStilettoText = randomStilettoText;
         this.finish = finish;
         this.randomStilettoAmount = getAmount(1.0F, 0.97F, 1.0F);
         this.handlePerspectiveMatrix(1.0F, this.randomStilettoAmount);
         stageKnife.handleShaderProjection(randomStilettoValue, 0, shaderProjectionLimit, shaderProjectionLimit, this.stilettoMatrix4f, this.randomStilettoScene);
         return randomStiletto;
      } else {
         return ("Random".equals(randomStilettoText) || "Butterfly".equals(randomStilettoText)) && C2Preview.isButterfly() || this.canButterfly(randomStilettoText);
      }
   }

   private void handleMultiplyPush(
      MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, String multiplyPushText, String multiplyPushText2, KnifeInspectClip knifeInspectClip, float multiplyPushValue, float channelsMultiplyBound, boolean multiplyPushFlag
   ) {
      C2Knife c2Knife = C2Knife.getC2Knife(multiplyPushText);
      if (c2Knife != null) {
         matrices.push();
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-38.0F));
         matrices.multiplyPositionMatrix(MATRIX4F);
         float x = 1.0F / C2Preview.getMax(c2Knife);
         matrices.scale(x, x, x);
         C2Preview.canMatrixPop(c2Knife, matrices, orderedRenderCommandQueue, this.finish, multiplyPushFlag ? multiplyPushText2 : null, multiplyPushValue, multiplyPushFlag, 15728880);
         matrices.pop();
      } else if ("Butterfly".equals(multiplyPushText)) {
         matrices.push();
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-38.0F));
         matrices.multiplyPositionMatrix(MATRIX4F);
         float x2 = 1.0F / C2Preview.getButterflyFloat();
         matrices.scale(x2, x2, x2);
         C2Preview.canButterfly(matrices, orderedRenderCommandQueue, this.finish, multiplyPushFlag ? multiplyPushText2 : null, multiplyPushValue, multiplyPushFlag, 15728880);
         matrices.pop();
      } else if (KnifeInspectClip.canBayonetStiletto(multiplyPushText)) {
         matrices.push();
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-38.0F));
         float x3 = 1.0F / KnifePreviewRig.getRadius(multiplyPushText);
         matrices.scale(x3, x3, x3);
         KnifePreviewRig.canChannelsMultiply(matrices, orderedRenderCommandQueue, multiplyPushText, this.finish, multiplyPushFlag ? knifeInspectClip : null, channelsMultiplyBound, multiplyPushFlag, 15728880);
         matrices.pop();
      } else {
         matrices.push();
         boolean multiplyPushFlag2 = multiplyPushFlag && "Toss".equals(multiplyPushText);
         float x4 = 1.0F / (0.74F + (multiplyPushFlag2 ? 0.31F : 0.0F));
         matrices.scale(x4, x4, x4);
         if (multiplyPushFlag2) {
            matrices.translate(0.0F, -0.31F, 0.0F);
         }

         if (multiplyPushFlag) {
            SwordInspect.handleSpins(matrices, multiplyPushText, channelsMultiplyBound);
         }

         this.itemRenderState.render(matrices, orderedRenderCommandQueue, 15728880, OverlayTexture.DEFAULT_UV, 0);
         matrices.pop();
      }
   }

   public void handleStackUpdate(SwordInspect swordInspect) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ItemStack itemStack = instance.player != null ? instance.player.getMainHandStack() : ItemStack.EMPTY;
      if (itemStack == null || itemStack.isEmpty() || !itemStack.isIn(ItemTags.SWORDS)) {
         if (this.itemStack == null) {
            this.itemStack = new ItemStack(Items.NETHERITE_SWORD);
         }

         itemStack = this.itemStack;
      }

      instance.getItemModelManager().clearAndUpdate(this.itemRenderState, itemStack, ItemDisplayContext.NONE, instance.world, instance.player, 0);
   }

   public boolean canButterflyEmpty(String butterflyEmptyText) {
      if ("Butterfly".equals(butterflyEmptyText)) {
         return C2Preview.isButterflyFlag();
      } else {
         C2Knife c2Knife = C2Knife.getC2Knife(butterflyEmptyText);
         if (c2Knife != null) {
            return C2Preview.canFlag(c2Knife);
         } else {
            return KnifeInspectClip.canBayonetStiletto(butterflyEmptyText) ? KnifePreviewRig.canInstance(butterflyEmptyText) : !this.itemRenderState.isEmpty();
         }
      }
   }
}
