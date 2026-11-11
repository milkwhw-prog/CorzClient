package com.corz.client.utils;

import java.util.Optional;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AssetInfo.TextureAsset;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.categories.misc.Capes;
import com.corz.client.utils.EmitVertex;
import com.corz.client.utils.SubmitRadians;
import com.corz.client.utils.SubmitMatrixApi;
import com.corz.client.utils.SubmitEmitUtils;
import com.corz.client.utils.RadiansAge;

public final class MatrixSubmitUtils {
   private static float tickAccumulatorSeconds;
   private static final RenderStateDataKey<EmitVertex> dataKey = RenderStateDataKey.create(() -> "corz:cape_3d");
   private static final Matrix4f MATRIX4_FMATRIX4F = new Matrix4f();
   private static long lastNanoTime;
   private static final Matrix4f MATRIX4F2 = new Matrix4f();
   private static final Matrix4f MATRIX4F3 = new Matrix4f();
   private static float matrixSubmitAmount;
   private static float currentCapeYaw;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static EmitVertex emitVertex;
   private static boolean captureCapeActive;
   private static boolean submitCustomActive;
   private static final Vector3f VECTOR3F = new Vector3f();
   private static final float[] matrixVectorTable = new float[8];
   private static final float[] matrixSubmitTable = new float[8];
   private static float lastCapeYaw;

   public static void handleAmountFlag(float matrixSubmitValue) {
      captureCapeActive = true;
      currentCapeYaw = Float.isFinite(matrixSubmitValue) ? matrixSubmitValue : 0.0F;
   }

   private static boolean canEmpty(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty();
   }

   static float getPitchGliding(PlayerEntityRenderState playerEntityRenderState) {
      float pitchGlidingFactor = 0.0F;
      if (playerEntityRenderState.isGliding) {
         if (!playerEntityRenderState.usingRiptide) {
            pitchGlidingFactor = playerEntityRenderState.getGlidingProgress() * (90.0F + playerEntityRenderState.pitch);
         }
      } else if (playerEntityRenderState.leaningPitch > 0.0F) {
         pitchGlidingFactor = playerEntityRenderState.leaningPitch * (playerEntityRenderState.touchingWater ? 90.0F + playerEntityRenderState.pitch : 90.0F);
      }

      float pitchGliding = pitchGlidingFactor * (float) (Math.PI / 180.0);
      if (playerEntityRenderState.isInSneakingPose) {
         pitchGliding += 0.5F;
      }

      return pitchGliding;
   }

   static void handleFlag(String matrixSubmitText, Throwable throwable) {
      if (!submitCustomActive) {
         submitCustomActive = true;
      }
   }

   private static void handleAngles(Model model, Object angles) {
      model.setAngles(angles);
   }

   private static void handleMatrixVector(SubmitRadians submitRadians, float matrixVectorValue, float matrixVectorValue2, float matrixVectorValue3, float matrixVectorValue4, float matrixVectorValue5, float matrixVectorValue6, float matrixIdentityValue, float submitRadiansBound) {
      MATRIX4F2.mul(MATRIX4F3, MATRIX4F);

      for (int i = 0; i < 8; i++) {
         float matrixVectorValue9 = (i & 1) == 0 ? matrixVectorValue - matrixIdentityValue : matrixVectorValue4 + matrixIdentityValue;
         float matrixVectorValue10 = (i & 2) == 0 ? matrixVectorValue2 - matrixIdentityValue : matrixVectorValue5 + matrixIdentityValue;
         float matrixVectorValue11 = (i & 4) == 0 ? matrixVectorValue3 - matrixIdentityValue : matrixVectorValue6 + matrixIdentityValue;
         MATRIX4F.transformPosition(matrixVectorValue9, matrixVectorValue10, matrixVectorValue11, VECTOR3F);
         matrixVectorTable[i] = VECTOR3F.y;
         matrixSubmitTable[i] = VECTOR3F.z;
      }

      submitRadians.handleAmounts(matrixVectorTable, matrixSubmitTable, 8, submitRadiansBound);
   }

   public static boolean canFlag(PlayerEntityRenderState playerEntityRenderState) {
      return !submitCustomActive && playerEntityRenderState.getData(dataKey) != null;
   }

   private static void handleScaleOrigin(Matrix4f matrix, ModelPart modelPart) {
      matrix.translate(modelPart.originX, modelPart.originY, modelPart.originZ);
      if (modelPart.pitch != 0.0F || modelPart.yaw != 0.0F || modelPart.roll != 0.0F) {
         matrix.rotateZYX(modelPart.roll, modelPart.yaw, modelPart.pitch);
      }

      if (modelPart.xScale != 1.0F || modelPart.yScale != 1.0F || modelPart.zScale != 1.0F) {
         matrix.scale(modelPart.xScale, modelPart.yScale, modelPart.zScale);
      }
   }

   public static void handleCaptureCape(PlayerLikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float captureCapeValue) {
      if (isFlag() && !playerEntityRenderState.invisible && playerEntityRenderState.capeVisible) {
         try {
            SkinTextures skinTextures = playerEntityRenderState.skinTextures;
            if (skinTextures == null) {
               return;
            }

            TextureAsset cape = skinTextures.cape();
            if (cape == null || !Capes.canCapesTextures(cape.texturePath())) {
               return;
            }

            if (!(playerLikeEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity)) {
               return;
            }

            boolean inPose = playerEntityRenderState.isInPose(EntityPose.SLEEPING);
            float captureCapeValue2 = 1.0F;
            EmitVertex emitVertex2;
            float submitEmitValue;
            if (captureCapeActive && abstractClientPlayerEntity == MinecraftClient.getInstance().player) {
               if (emitVertex == null) {
                  emitVertex = new EmitVertex();
               }

               emitVertex2 = emitVertex;
               submitEmitValue = getAmountTime(emitVertex2.radiansAge);
               captureCapeValue2 = 1.8F;
               inPose = false;
            } else {
               if (!(abstractClientPlayerEntity instanceof SubmitMatrixApi submitMatrixApi)) {
                  return;
               }

               emitVertex2 = submitMatrixApi.corz$capeFrame();
               if (emitVertex2 == null) {
                  emitVertex2 = new EmitVertex();
                  submitMatrixApi.corz$capeFrame(emitVertex2);
               }

               emitVertex2.radiansAge.handleAmountAge(abstractClientPlayerEntity.age, abstractClientPlayerEntity.getX(), abstractClientPlayerEntity.getY(), abstractClientPlayerEntity.getZ(), abstractClientPlayerEntity.bodyYaw, getPitchGliding(playerEntityRenderState), inPose);
               submitEmitValue = captureCapeValue;
            }

            emitVertex2.captureCapeActive = inPose;
            if (inPose) {
               SubmitEmitUtils.handle(emitVertex2.captureAmounts, emitVertex2.captureTable);
            } else {
               SubmitEmitUtils.handleAmountsAmount(
                  emitVertex2.radiansAge.minAmountsUpdate,
                  submitEmitValue,
                  playerEntityRenderState.limbSwingAnimationProgress,
                  playerEntityRenderState.limbSwingAmplitude,
                  captureCapeValue2,
                  emitVertex2.captureAmounts,
                  emitVertex2.captureTable
               );
            }

            playerEntityRenderState.setData(dataKey, emitVertex2);
         } catch (Throwable e) {
            handleFlag("capture", e);
         }
      }
   }

   public static boolean canSubmitCustom(
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      Model<?> model,
      PlayerEntityRenderState playerEntityRenderState,
      MatrixStack matrixStack,
      RenderLayer renderLayer,
      int emitValue,
      int corzsubmitCape3dValue2,
      int value,
      boolean submitCustomFlag
   ) {
      if (submitCustomActive) {
         return false;
      } else {
         EmitVertex emitVertex = (EmitVertex)playerEntityRenderState.getData(dataKey);
         if (emitVertex != null && model instanceof BipedEntityModel bipedEntityModel) {
            boolean submitCustomFlag2 = false;

            boolean submitCustom;
            try {
               handleAngles(model, playerEntityRenderState);
               handleMatrixIdentity(bipedEntityModel, playerEntityRenderState, submitCustomFlag, emitVertex.submitCustomSubmitRadians);
               emitVertex.radiansAge.cosSubmitRadians.handleAmountsAmount(emitVertex.submitCustomSubmitRadians);
               emitVertex.updateCaptureAmounts();
               emitVertex.emitValue = emitValue;
               emitVertex.emitValue2SubmitCustom = corzsubmitCape3dValue2;
               emitVertex.vertexColor = value;
               matrixStack.push();
               submitCustomFlag2 = true;
               bipedEntityModel.getRootPart().applyTransform(matrixStack);
               bipedEntityModel.body.applyTransform(matrixStack);
               matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
               orderedRenderCommandQueue.submitCustom(matrixStack, renderLayer, emitVertex.custom);
               if (value != 0) {
                  Optional affectedOutline = renderLayer.getAffectedOutline();
                  if (affectedOutline.isPresent()) {
                     orderedRenderCommandQueue.submitCustom(matrixStack, (RenderLayer)affectedOutline.get(), emitVertex.submitCustomCustom);
                  }
               }

               return true;
            } catch (Throwable e) {
               handleFlag("submit", e);
               submitCustom = false;
            } finally {
               if (submitCustomFlag2) {
                  matrixStack.pop();
               }
            }

            return submitCustom;
         } else {
            return false;
         }
      }
   }

   public static boolean isFlag() {
      return !submitCustomActive && Capes.isBaseInstance();
   }

   private static void handleMatrixIdentity(BipedEntityModel<?> bipedEntityModel, PlayerEntityRenderState playerEntityRenderState, boolean matrixIdentityFlag, SubmitRadians submitRadians) {
      submitRadians.updateAmountsAmount();
      ModelPart rootPart = bipedEntityModel.getRootPart();
      MATRIX4_FMATRIX4F.identity();
      if (matrixIdentityFlag) {
         MATRIX4_FMATRIX4F.translate(0.0F, -0.85F, 1.1F);
      }

      handleScaleOrigin(MATRIX4_FMATRIX4F, rootPart);
      handleScaleOrigin(MATRIX4_FMATRIX4F, bipedEntityModel.body);
      MATRIX4_FMATRIX4F.invertAffine(MATRIX4F2);
      MATRIX4F3.identity();
      handleScaleOrigin(MATRIX4F3, rootPart);
      handleScaleOrigin(MATRIX4F3, bipedEntityModel.body);
      handleMatrixVector(submitRadians, -4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 2.0F, 0.25F, Float.POSITIVE_INFINITY);
      float matrixIdentityValue = canEmpty(playerEntityRenderState.equippedFeetStack) ? 1.0F : (canEmpty(playerEntityRenderState.equippedLegsStack) ? 0.5F : 0.25F);
      MATRIX4F3.identity();
      handleScaleOrigin(MATRIX4F3, rootPart);
      handleScaleOrigin(MATRIX4F3, bipedEntityModel.rightLeg);
      handleMatrixVector(submitRadians, -2.0F, 0.0F, -2.0F, 2.0F, 12.0F, 2.0F, matrixIdentityValue, Float.POSITIVE_INFINITY);
      MATRIX4F3.identity();
      handleScaleOrigin(MATRIX4F3, rootPart);
      handleScaleOrigin(MATRIX4F3, bipedEntityModel.leftLeg);
      handleMatrixVector(submitRadians, -2.0F, 0.0F, -2.0F, 2.0F, 12.0F, 2.0F, matrixIdentityValue, Float.POSITIVE_INFINITY);
      float matrixIdentityValue2 = !canEmpty(playerEntityRenderState.equippedHeadStack) && playerEntityRenderState.wearingSkullType == null ? 0.5F : 1.25F;
      MATRIX4F3.identity();
      handleScaleOrigin(MATRIX4F3, rootPart);
      handleScaleOrigin(MATRIX4F3, bipedEntityModel.head);
      handleMatrixVector(submitRadians, -4.0F, -8.0F, -4.0F, 4.0F, 0.0F, 4.0F, matrixIdentityValue2, 0.0F);
      if (playerEntityRenderState.hasVehicle) {
         submitRadians.handleAmount(13.0F);
      }
   }

   private static float getAmountTime(RadiansAge radiansAge) {
      long nanoTime = System.nanoTime();
      if (lastNanoTime != 0L && nanoTime - lastNanoTime <= 1000000000L) {
         tickAccumulatorSeconds = tickAccumulatorSeconds + (float)Math.min(nanoTime - lastNanoTime, 100000000L) / 1.0E9F;
      } else {
         radiansAge.updateCosMin();
         tickAccumulatorSeconds = 0.0F;
         lastCapeYaw = currentCapeYaw;
      }

      lastNanoTime = nanoTime;

      for (int i = 0; tickAccumulatorSeconds >= 0.05F && i++ < 4; tickAccumulatorSeconds -= 0.05F) {
         float matrixSubmitValue = RadiansAge.getAmount(currentCapeYaw - lastCapeYaw) * (float) (Math.PI / 180.0);
         lastCapeYaw = currentCapeYaw;
         matrixSubmitAmount = (matrixSubmitAmount + 0.05F) % 3600.0F;
         float radiansAgeLimit = 0.1F + 0.05F * (float)Math.sin(matrixSubmitAmount * 0.9F) + 0.03F * (float)Math.sin(matrixSubmitAmount * 2.3F + 1.1F);
         radiansAge.handleRadiansCos(0.0F, matrixSubmitValue, radiansAgeLimit);
      }

      if (tickAccumulatorSeconds >= 0.05F) {
         tickAccumulatorSeconds = 0.0F;
      }

      return tickAccumulatorSeconds / 0.05F;
   }

   public static void updateFlag() {
      captureCapeActive = false;
   }

   private MatrixSubmitUtils() {
   }
}
