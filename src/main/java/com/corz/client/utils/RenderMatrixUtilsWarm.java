package com.corz.client.utils;

import com.corz.client.gui.DisabledEnabled;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.categories.misc.CustomSkin;
import com.corz.client.utils.CorzclientWarm;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.WarmBrainrotBase;
import com.corz.client.utils.PoseProgress;
import com.corz.client.utils.TextureCorzclientUtils;
import com.corz.client.utils.LoftLatheCopy;

public final class RenderMatrixUtilsWarm {
   private static final RenderStateDataKey<RenderMatrixUtilsWarm.Frame> dataKey = RenderStateDataKey.create(() -> "corz:brainrot");
   private static final Set<String> SKIN_CUSTOM_SET = new HashSet<>();
   private static final Map<CorzclientWarm, ForeShinScale> FORE_SHIN_CACHE = new IdentityHashMap<>();
   private static final Map<CorzclientWarm, float[]> PERSON_CUSTOM_CACHE = new IdentityHashMap<>();
   private static final Map<CorzclientWarm, float[]> MAX_AMOUNTS_CACHE = new IdentityHashMap<>();
   private static final Vector3f VECTOR3_FVECTOR3F = new Vector3f();
   private static final Vector3f VECTOR3F = new Vector3f();

   private static float[] getAmounts(WarmBrainrotBase warmBrainrotBase, CorzclientWarm corzclientWarm, PoseProgress poseProgress) {
      ForeShinScale ifAbsent = FORE_SHIN_CACHE.computeIfAbsent(corzclientWarm, CorzclientWarm::getListSize);
      ifAbsent.getAmounts();
      warmBrainrotBase.handleShinProgress(poseProgress, ifAbsent);
      float[] matrixWarmValues = corzclientWarm.getAmounts();
      corzclientWarm.handleAmounts(ifAbsent, matrixWarmValues);
      return matrixWarmValues;
   }

   private static void handleCustomSkin(WarmBrainrotBase warmBrainrotBase, String customSkinText, Throwable throwable) {
      if (SKIN_CUSTOM_SET.add(warmBrainrotBase.formatTungDin())) {
         try {
            DisabledEnabled.getInstance().handleDisabledEnabled("Custom Skin", warmBrainrotBase.formatTungDinString() + " failed to render and was switched off", false);
         } catch (Throwable e) {
         }
      }
   }

   public static boolean canCustomSubmit(
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      PlayerEntityRenderState playerEntityRenderState,
      MatrixStack matrixStack,
      RenderLayer renderLayer,
      int customSubmitValue,
      int customSubmitValue2,
      int customSubmitValue3,
      int vectorOutlineValue
   ) {
      RenderMatrixUtilsWarm.Frame frame = (RenderMatrixUtilsWarm.Frame)playerEntityRenderState.getData(dataKey);
      if (frame == null) {
         return false;
      } else {
         WarmBrainrotBase warmBrainrotBase = frame.warmBrainrotBase;
         if (SKIN_CUSTOM_SET.contains(warmBrainrotBase.formatTungDin())) {
            return false;
         } else {
            try {
               CorzclientWarm classCorzclientWarm = warmBrainrotBase.getClassCorzclientWarm();
               if (classCorzclientWarm == null) {
                  return false;
               } else {
                  CorzclientWarm cameraFlag = getCameraFlag(warmBrainrotBase, classCorzclientWarm, frame);
                  frame.poseProgress.headYawRadians = -playerEntityRenderState.relativeHeadYaw * (float) (Math.PI / 180.0);
                  frame.poseProgress.shinProgressAmount = -playerEntityRenderState.pitch * (float) (Math.PI / 180.0);
                  Identifier corzclientTexture = TextureCorzclientUtils.getCorzclientTexture(warmBrainrotBase, classCorzclientWarm);
                  float[] customSubmitValues = getAmounts(warmBrainrotBase, cameraFlag, frame.poseProgress);
                  boolean customSubmitFlag = renderLayer != null && renderLayer.isOutline();
                  boolean customSubmitFlag2 = customSubmitValue3 >>> 24 < 255;
                  RenderLayer renderLayer2 = customSubmitFlag
                     ? RenderLayers.outlineNoCull(corzclientTexture)
                     : (customSubmitFlag2 ? RenderLayers.entityTranslucent(corzclientTexture) : RenderLayers.entityCutoutNoCull(corzclientTexture));
                  matrixStack.push();

                  boolean customSubmit;
                  try {
                     matrixStack.translate(0.0F, 1.501F, 0.0F);
                     matrixStack.scale(-0.0625F, -0.0625F, 0.0625F);
                     if (!playerEntityRenderState.flipUpsideDown) {
                        if (playerEntityRenderState.isInSneakingPose) {
                           matrixStack.translate(0.0F, 2.1333334F, 0.0F);
                        }

                        if (playerEntityRenderState.hasVehicle) {
                           matrixStack.translate(0.0F, -Math.max(0.0F, warmBrainrotBase.getAmount(classCorzclientWarm) - 10.240001F), 0.0F);
                        }
                     }

                     if (!customSubmitFlag) {
                        orderedRenderCommandQueue.submitCustom(matrixStack, renderLayer2, (entry, colorx) -> handleVectorEmit(warmBrainrotBase, cameraFlag, customSubmitValues, -1, entry, colorx, customSubmitValue3, customSubmitValue, customSubmitValue2));
                        if (vectorOutlineValue == 0) {
                           return true;
                        }

                        Optional affectedOutline = renderLayer2.getAffectedOutline();
                        if (affectedOutline.isPresent()) {
                           orderedRenderCommandQueue.submitCustom(matrixStack, (RenderLayer)affectedOutline.get(), (entry2, vertices) -> handleVectorOutline(warmBrainrotBase, cameraFlag, customSubmitValues, entry2, vertices, vectorOutlineValue));
                        }

                        return true;
                     }

                     int customSubmitValue5 = vectorOutlineValue != 0 ? vectorOutlineValue : customSubmitValue3;
                     orderedRenderCommandQueue.submitCustom(matrixStack, renderLayer2, (mats, vertices2) -> handleVectorOutline(warmBrainrotBase, cameraFlag, customSubmitValues, mats, vertices2, customSubmitValue5));
                     customSubmit = true;
                  } finally {
                     matrixStack.pop();
                  }

                  return customSubmit;
               }
            } catch (Throwable e) {
               handleCustomSkin(warmBrainrotBase, "render", e);
               return false;
            }
         }
      }
   }

   static float[] getAmounts(float matrixWarmLimit) {
      if (!(matrixWarmLimit <= 0.0F) && !(matrixWarmLimit >= 10.0F)) {
         float matrixWarmFactor = Math.min(2.0F, 10.0F / matrixWarmLimit);
         return new float[]{matrixWarmFactor, 12.0F - matrixWarmLimit * matrixWarmFactor};
      } else {
         return new float[]{1.0F, 0.0F};
      }
   }

   public static boolean canInvisible(PlayerEntityRenderState playerEntityRenderState) {
      return !playerEntityRenderState.invisible && playerEntityRenderState.getData(dataKey) != null;
   }

   public static void updateCustomSkin() {
      SKIN_CUSTOM_SET.clear();
      FORE_SHIN_CACHE.clear();
      PERSON_CUSTOM_CACHE.clear();
      MAX_AMOUNTS_CACHE.clear();
   }

   private static CorzclientWarm getCameraFlag(WarmBrainrotBase warmBrainrotBase, CorzclientWarm cameraFlag, RenderMatrixUtilsWarm.Frame frame) {
      if (!frame.progressPoseActive && !(frame.toCamera <= 64.0)) {
         CorzclientWarm classCorzclient = warmBrainrotBase.getClassCorzclient();
         if (classCorzclient == null) {
            return cameraFlag;
         } else if (frame.toCamera <= 576.0) {
            return classCorzclient;
         } else {
            CorzclientWarm classThrowableThread = warmBrainrotBase.getClassThrowableThread();
            return classThrowableThread != null ? classThrowableThread : classCorzclient;
         }
      } else {
         return cameraFlag;
      }
   }

   private static void handleVectorEmit(
      WarmBrainrotBase warmBrainrotBase, CorzclientWarm corzclientWarm, float[] vectorEmitValues, int vectorEmitValue, Entry entry, VertexConsumer vertices, int vectorEmitValue2, int customSubmitValue, int vectorEmitValue4
   ) {
      try {
         Matrix4f positionMatrix = entry.getPositionMatrix();
         boolean vectorEmitFlag = vectorEmitValue2 != -1;

         for (int i = 0; i < corzclientWarm.listSize; i++) {
            if (vectorEmitValue < 0 || corzclientWarm.canFlag(i, vectorEmitValue)) {
               float[] loftLatheValues = corzclientWarm.vectorEmitFloats[i];
               if (loftLatheValues.length != 0) {
                  int index = i * 21;
                  float vectorEmitFactor = vectorEmitValues[index];
                  float vectorEmitFactor2 = vectorEmitValues[index + 1];
                  float vectorEmitFactor3 = vectorEmitValues[index + 2];
                  float vectorEmitValue5 = vectorEmitValues[index + 3];
                  float vectorEmitFactor4 = vectorEmitValues[index + 4];
                  float vectorEmitFactor5 = vectorEmitValues[index + 5];
                  float vectorEmitFactor6 = vectorEmitValues[index + 6];
                  float vectorEmitValue6 = vectorEmitValues[index + 7];
                  float vectorEmitFactor7 = vectorEmitValues[index + 8];
                  float vectorEmitFactor8 = vectorEmitValues[index + 9];
                  float vectorEmitFactor9 = vectorEmitValues[index + 10];
                  float vectorEmitValue7 = vectorEmitValues[index + 11];
                  float vectorEmitFactor10 = vectorEmitValues[index + 12];
                  float vectorEmitFactor11 = vectorEmitValues[index + 13];
                  float vectorEmitFactor12 = vectorEmitValues[index + 14];
                  float vectorEmitFactor13 = vectorEmitValues[index + 15];
                  float vectorEmitFactor14 = vectorEmitValues[index + 16];
                  float vectorEmitFactor15 = vectorEmitValues[index + 17];
                  float vectorEmitFactor16 = vectorEmitValues[index + 18];
                  float vectorEmitFactor17 = vectorEmitValues[index + 19];
                  float vectorEmitFactor18 = vectorEmitValues[index + 20];

                  for (int j = 0; j < loftLatheValues.length; j += 9) {
                     float vectorEmitFactor19 = loftLatheValues[j];
                     float vectorEmitFactor20 = loftLatheValues[j + 1];
                     float vectorEmitFactor21 = loftLatheValues[j + 2];
                     positionMatrix.transformPosition(
                        vectorEmitFactor * vectorEmitFactor19 + vectorEmitFactor2 * vectorEmitFactor20 + vectorEmitFactor3 * vectorEmitFactor21 + vectorEmitValue5,
                        vectorEmitFactor4 * vectorEmitFactor19 + vectorEmitFactor5 * vectorEmitFactor20 + vectorEmitFactor6 * vectorEmitFactor21 + vectorEmitValue6,
                        vectorEmitFactor7 * vectorEmitFactor19 + vectorEmitFactor8 * vectorEmitFactor20 + vectorEmitFactor9 * vectorEmitFactor21 + vectorEmitValue7,
                        VECTOR3_FVECTOR3F
                     );
                     float vectorEmitFactor22 = loftLatheValues[j + 5];
                     float vectorEmitFactor23 = loftLatheValues[j + 6];
                     float vectorEmitFactor24 = loftLatheValues[j + 7];
                     entry.transformNormal(
                        vectorEmitFactor10 * vectorEmitFactor22 + vectorEmitFactor11 * vectorEmitFactor23 + vectorEmitFactor12 * vectorEmitFactor24,
                        vectorEmitFactor13 * vectorEmitFactor22 + vectorEmitFactor14 * vectorEmitFactor23 + vectorEmitFactor15 * vectorEmitFactor24,
                        vectorEmitFactor16 * vectorEmitFactor22 + vectorEmitFactor17 * vectorEmitFactor23 + vectorEmitFactor18 * vectorEmitFactor24,
                        VECTOR3F
                     );
                     int matrixWarmValue = LoftLatheCopy.getValue(loftLatheValues, j);
                     if (vectorEmitFlag) {
                        matrixWarmValue = getValue(matrixWarmValue, vectorEmitValue2);
                     }

                     vertices.vertex(
                        VECTOR3_FVECTOR3F.x, VECTOR3_FVECTOR3F.y, VECTOR3_FVECTOR3F.z, matrixWarmValue, loftLatheValues[j + 3], loftLatheValues[j + 4], vectorEmitValue4, customSubmitValue, VECTOR3F.x, VECTOR3F.y, VECTOR3F.z
                     );
                  }
               }
            }
         }
      } catch (Throwable e) {
         handleCustomSkin(warmBrainrotBase, "emit", e);
      }
   }

   private RenderMatrixUtilsWarm() {
   }

   public static boolean canPersonCustom(OrderedRenderCommandQueue orderedRenderCommandQueue, ModelPart modelPart, MatrixStack matrixStack, int personCustomValue, boolean personCustomFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player == null) {
         return false;
      } else {
         WarmBrainrotBase brainrotTurn = CustomSkin.getBrainrotTurn(instance.player);
         if (brainrotTurn != null && !SKIN_CUSTOM_SET.contains(brainrotTurn.formatTungDin())) {
            try {
               CorzclientWarm classCorzclientWarm = brainrotTurn.getClassCorzclientWarm();
               if (classCorzclientWarm == null) {
                  brainrotTurn.updateThrowableThread();
                  return false;
               } else {
                  int vectorEmitValue = classCorzclientWarm.personCustomValue;
                  if (vectorEmitValue < 0) {
                     return true;
                  } else {
                     Identifier corzclientTexture = TextureCorzclientUtils.getCorzclientTexture(brainrotTurn, classCorzclientWarm);
                     float[] ifAbsent = PERSON_CUSTOM_CACHE.computeIfAbsent(classCorzclientWarm, corzclientWarm -> {
                        float[] personCustomValues = corzclientWarm.getAmounts();
                        corzclientWarm.handleAmounts(corzclientWarm.getListSize(), personCustomValues);
                        return personCustomValues;
                     });
                     float[] ifAbsent2 = MAX_AMOUNTS_CACHE.computeIfAbsent(classCorzclientWarm, corzclientWarm2 -> getAmounts(corzclientWarm2.getMax()));
                     matrixStack.push();

                     try {
                        modelPart.applyTransform(matrixStack);
                        matrixStack.scale(personCustomFlag ? -0.0625F : 0.0625F, -0.0625F, 0.0625F);
                        matrixStack.translate(0.0F, -ifAbsent2[1], 0.0F);
                        matrixStack.scale(ifAbsent2[0], ifAbsent2[0], ifAbsent2[0]);
                        matrixStack.translate(-classCorzclientWarm.personCustomTable[3 * vectorEmitValue], -classCorzclientWarm.personCustomTable[3 * vectorEmitValue + 1], -classCorzclientWarm.personCustomTable[3 * vectorEmitValue + 2]);
                        orderedRenderCommandQueue.submitCustom(
                           matrixStack,
                           RenderLayers.entityCutoutNoCull(corzclientTexture),
                           (entry, arm) -> handleVectorEmit(brainrotTurn, classCorzclientWarm, ifAbsent, vectorEmitValue, entry, arm, -1, personCustomValue, OverlayTexture.DEFAULT_UV)
                        );
                     } finally {
                        matrixStack.pop();
                     }

                     return true;
                  }
               }
            } catch (Throwable e) {
               handleCustomSkin(brainrotTurn, "first person", e);
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private static int getValue(int matrixWarmValue, int matrixWarmValue2) {
      int matrixWarmValue3 = (matrixWarmValue >>> 24) * (matrixWarmValue2 >>> 24) / 255;
      int matrixWarmValue4 = (matrixWarmValue >> 16 & 0xFF) * (matrixWarmValue2 >> 16 & 0xFF) / 255;
      int matrixWarmValue5 = (matrixWarmValue >> 8 & 0xFF) * (matrixWarmValue2 >> 8 & 0xFF) / 255;
      int matrixWarmValue6 = (matrixWarmValue & 0xFF) * (matrixWarmValue2 & 0xFF) / 255;
      return matrixWarmValue3 << 24 | matrixWarmValue4 << 16 | matrixWarmValue5 << 8 | matrixWarmValue6;
   }

   public static void handleProgressPose(PlayerLikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState) {
      try {
         if (playerEntityRenderState.getData(dataKey) != null) {
            playerEntityRenderState.setData(dataKey, null);
         }

         if (!(playerLikeEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity)) {
            return;
         }

         WarmBrainrotBase brainrotTurn = CustomSkin.getBrainrotTurn(abstractClientPlayerEntity);
         if (brainrotTurn == null || SKIN_CUSTOM_SET.contains(brainrotTurn.formatTungDin())) {
            return;
         }

         if (brainrotTurn.getClassCorzclientWarm() == null) {
            brainrotTurn.updateThrowableThread();
            return;
         }

         RenderMatrixUtilsWarm.Frame frame = new RenderMatrixUtilsWarm.Frame(brainrotTurn);
         PoseProgress poseProgress = frame.poseProgress;
         poseProgress.age = playerEntityRenderState.age;
         poseProgress.animationProgress = playerEntityRenderState.limbSwingAnimationProgress;
         poseProgress.limbSwingStrength = Math.min(1.0F, Math.max(0.0F, playerEntityRenderState.limbSwingAmplitude));
         poseProgress.swingProgress = playerEntityRenderState.handSwingProgress;
         poseProgress.leaningPitch = playerEntityRenderState.leaningPitch;
         poseProgress.sneakingPose = playerEntityRenderState.isInSneakingPose;
         poseProgress.gliding = playerEntityRenderState.isGliding;
         poseProgress.swimming = playerEntityRenderState.isSwimming;
         poseProgress.vehicle = playerEntityRenderState.hasVehicle;
         poseProgress.inPose = playerEntityRenderState.isInPose(EntityPose.SLEEPING);
         poseProgress.usingItem = playerEntityRenderState.isUsingItem;
         poseProgress.shinProgressActive = playerEntityRenderState.hurt;
         poseProgress.hashCode = abstractClientPlayerEntity.getUuid().hashCode();
         frame.progressPoseActive = abstractClientPlayerEntity == MinecraftClient.getInstance().player;
         if (frame.progressPoseActive && CustomSkin.scaleValue >= 0) {
            poseProgress.poseProgressActive = true;
            poseProgress.limbSwingStrength = 0.0F;
            poseProgress.swingProgress = 0.0F;
         }

         frame.toCamera = playerEntityRenderState.squaredDistanceToCamera;
         playerEntityRenderState.setData(dataKey, frame);
      } catch (Throwable e) {
      }
   }

   private static void handleVectorOutline(WarmBrainrotBase warmBrainrotBase, CorzclientWarm corzclientWarm, float[] vectorOutlineValues, Entry entry, VertexConsumer vertices, int vectorOutlineValue) {
      try {
         Matrix4f positionMatrix = entry.getPositionMatrix();

         for (int i = 0; i < corzclientWarm.listSize; i++) {
            float[] vectorOutlineValues2 = corzclientWarm.vectorEmitFloats[i];
            int index = i * 21;

            for (int j = 0; j < vectorOutlineValues2.length; j += 9) {
               float vectorOutlineFactor = vectorOutlineValues2[j];
               float vectorOutlineFactor2 = vectorOutlineValues2[j + 1];
               float vectorOutlineFactor3 = vectorOutlineValues2[j + 2];
               positionMatrix.transformPosition(
                  vectorOutlineValues[index] * vectorOutlineFactor + vectorOutlineValues[index + 1] * vectorOutlineFactor2 + vectorOutlineValues[index + 2] * vectorOutlineFactor3 + vectorOutlineValues[index + 3],
                  vectorOutlineValues[index + 4] * vectorOutlineFactor + vectorOutlineValues[index + 5] * vectorOutlineFactor2 + vectorOutlineValues[index + 6] * vectorOutlineFactor3 + vectorOutlineValues[index + 7],
                  vectorOutlineValues[index + 8] * vectorOutlineFactor + vectorOutlineValues[index + 9] * vectorOutlineFactor2 + vectorOutlineValues[index + 10] * vectorOutlineFactor3 + vectorOutlineValues[index + 11],
                  VECTOR3_FVECTOR3F
               );
               vertices.vertex(VECTOR3_FVECTOR3F.x, VECTOR3_FVECTOR3F.y, VECTOR3_FVECTOR3F.z).color(vectorOutlineValue).texture(vectorOutlineValues2[j + 3], vectorOutlineValues2[j + 4]);
            }
         }
      } catch (Throwable e) {
         handleCustomSkin(warmBrainrotBase, "outline", e);
      }
   }

   private static final class Frame {
      boolean progressPoseActive;
      final PoseProgress poseProgress = new PoseProgress();
      final WarmBrainrotBase warmBrainrotBase;
      double toCamera;

      Frame(WarmBrainrotBase warmBrainrotBase) {
         this.warmBrainrotBase = warmBrainrotBase;
      }
   }
}
