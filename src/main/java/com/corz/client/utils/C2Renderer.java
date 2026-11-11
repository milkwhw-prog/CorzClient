package com.corz.client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import com.corz.client.utils.C2Model;
import com.corz.client.utils.KnifeFinishTextures;
import com.corz.client.utils.C2Frame;
import com.corz.client.utils.KnifeMesh;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.C2Animator;
import com.corz.client.utils.C2Assets;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.C2Butterfly;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Surface;

public final class C2Renderer {
   private static Matrix4f[] invertMul;
   private static C2Model c2Model;

   private C2Renderer() {
   }

   private static boolean canCustomSubmit(
      C2Model c2Model, Matrix4f[] matrix4fs, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int customSubmitValue, KnifeFinishTextures.Skin skin2, boolean customSubmitFlag
   ) {
      int customSubmitSize = C2Frame.downBladeTexts.length;
      KnifeMesh[] knifeMeshes = new KnifeMesh[customSubmitSize];
      KnifeFinish.Skin[] skins = new KnifeFinish.Skin[customSubmitSize];

      for (int i = 0; i < customSubmitSize; i++) {
         knifeMeshes[i] = KnifeMesh.getTexturesKnife(C2Frame.downBladeTexts[i]);
         skins[i] = skin2.getSkins(i);
         if (knifeMeshes[i] == null || !knifeMeshes[i].canAmounts(skins[i]) || C2Frame.getBladeTexts(c2Model, i) < 0) {
            return false;
         }
      }

      Matrix4f[] matrixModel = getMatrixModel(c2Model);

      for (int j = 0; j < customSubmitSize; j++) {
         int bladeTexts = C2Frame.getBladeTexts(c2Model, j);
         if (matrixModel[j] != null && !(Math.abs(matrix4fs[bladeTexts].determinant3x3()) < 1.0E-10F)) {
            KnifeMesh knifeMesh = knifeMeshes[j];
            KnifeFinish.Skin skin3 = skins[j];
            matrixStack.push();

            try {
               matrixStack.multiplyPositionMatrix(new Matrix4f(matrix4fs[bladeTexts]).mul(matrixModel[j]));
               orderedRenderCommandQueue.submitCustom(
                  matrixStack, skin2.renderLayerCorzrenderKnife, (lightx, entry) -> knifeMesh.handleAmountsLight(lightx, entry, customSubmitValue, OverlayTexture.DEFAULT_UV, skin3)
               );
               if (skin2.canLayerRender(j)) {
                  orderedRenderCommandQueue.getBatchingQueue(1).submitCustom(matrixStack, skin2.renderLayer, (mesh, skin) -> knifeMesh.handleAmounts(mesh, skin, skin3));
               }

               if (customSubmitFlag) {
                  orderedRenderCommandQueue.submitCustom(matrixStack, RenderLayers.entityGlint(), (entry2, lightx) -> knifeMesh.handleAmountsLight(entry2, lightx, customSubmitValue, OverlayTexture.DEFAULT_UV));
               }
            } finally {
               matrixStack.pop();
            }
         }
      }

      return true;
   }

   public static void handleModelPop(
      AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int modelPopValue, KnifeFinish.Finish finish, boolean modelPopFlag
   ) {
      float[] staticIdle = C2Animator.getStaticIdle();
      C2Assets.Loaded loaded = C2Animator.getLoaded();
      if (loaded != null && staticIdle != null) {
         C2Model model2 = loaded.model();
         Matrix4f[] bonesMesh = model2.getBonesMesh();
         model2.handlePivotParent(staticIdle, bonesMesh);
         matrixStack.push();

         try {
            boolean modelPopFlag2 = abstractClientPlayerEntity.getMainArm() == Arm.LEFT;
            if (modelPopFlag2) {
               matrixStack.scale(-1.0F, 1.0F, 1.0F);
            }

            matrixStack.multiplyPositionMatrix(model2.mulTranslationMatrix4f);
            KnifeFinishTextures.Skin skin = loaded.knife().isClassics() ? KnifeFinishTextures.getTimeRigs(KnifeRig.INSTANCE, finish) : null;
            if (skin == null || !canCustomSubmit(model2, bonesMesh, matrixStack, orderedRenderCommandQueue, modelPopValue, skin, modelPopFlag)) {
               RenderLayer noCull = getNoCull(loaded);
               orderedRenderCommandQueue.submitCustom(matrixStack, noCull, (entry2, entry) -> handlePeekDeterminant(model2, bonesMesh, entry2, entry, modelPopValue, -1, OverlayTexture.DEFAULT_UV));
               if (modelPopFlag) {
                  orderedRenderCommandQueue.submitCustom(
                     matrixStack,
                     RenderLayers.entityGlint(),
                     (lightx, model) -> handlePeekDeterminant(model2, bonesMesh, lightx, model, modelPopValue, -1, OverlayTexture.DEFAULT_UV)
                  );
               }
            }

            if (abstractClientPlayerEntity.isInvisible()) {
               return;
            }

            PlayerEntityRenderer playerRenderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getPlayerRenderer(abstractClientPlayerEntity);
            Identifier texturePath = abstractClientPlayerEntity.getSkin().body().texturePath();
            boolean modelPopFlag3 = abstractClientPlayerEntity.getSkin().model() == PlayerSkinType.SLIM;

            for (int i = 0; i < model2.meshModelBones.length; i++) {
               int arm = model2.meshModelBones[i].arm();
               if (arm != 0 && !(Math.abs(bonesMesh[i].determinant3x3()) < 1.0E-10F) && (arm >= 0 || abstractClientPlayerEntity.getOffHandStack().isEmpty())) {
                  matrixStack.push();

                  try {
                     matrixStack.multiplyPositionMatrix(bonesMesh[i]);
                     float[] grip = model2.meshModelBones[i].grip();
                     if (grip != null && !loaded.knife().isClassics()) {
                        matrixStack.translate(grip[0] - arm * (modelPopFlag3 ? 5.5F : 6.0F) / 16.0F, grip[1], grip[2]);
                     }

                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
                     if (modelPopFlag2) {
                        matrixStack.scale(-1.0F, 1.0F, 1.0F);
                        arm = -arm;
                     }

                     if (arm > 0) {
                        playerRenderer.renderRightArm(matrixStack, orderedRenderCommandQueue, modelPopValue, texturePath, abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE));
                     } else {
                        playerRenderer.renderLeftArm(matrixStack, orderedRenderCommandQueue, modelPopValue, texturePath, abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE));
                     }
                  } finally {
                     matrixStack.pop();
                  }
               }
            }
         } finally {
            matrixStack.pop();
         }
      }
   }

   public static void handle(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int c2RendererValue) {
      handleModelPop(abstractClientPlayerEntity, matrixStack, orderedRenderCommandQueue, c2RendererValue, null, false);
   }

   public static boolean canRiptidePerspective(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand, ItemStack itemStack) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (abstractClientPlayerEntity != null
         && abstractClientPlayerEntity == instance.player
         && instance.getCameraEntity() == abstractClientPlayerEntity
         && instance.options.getPerspective().isFirstPerson()
         && C2Butterfly.isC2ButterflyActive()
         && hand == Hand.MAIN_HAND
         && itemStack != null
         && itemStack.isIn(ItemTags.SWORDS)
         && abstractClientPlayerEntity.isAlive()
         && !abstractClientPlayerEntity.isSpectator()
         && !abstractClientPlayerEntity.isUsingItem()
         && !abstractClientPlayerEntity.isUsingRiptide()) {
         C2Knife function = C2Butterfly.getFunction(abstractClientPlayerEntity, itemStack);
         if (function == null) {
            return false;
         } else {
            C2Assets.handleSupplyAsync(function);
            return C2Assets.getSupplyAsync(function) != null;
         }
      } else {
         return false;
      }
   }

   static void handlePeekDeterminant(C2Model c2Model, Matrix4f[] matrix4fs, Entry entry, VertexConsumer vertices, int peekDeterminantValue, int peekDeterminantValue2, int peekDeterminantValue3) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.peek().copy(entry);

      for (int i = 0; i < matrix4fs.length; i++) {
         float[] vertices2 = c2Model.meshModelBones[i].vertices();
         if (vertices2.length != 0 && !(Math.abs(matrix4fs[i].determinant3x3()) < 1.0E-10F)) {
            matrixStack.push();
            matrixStack.multiplyPositionMatrix(matrix4fs[i]);
            Entry peek2 = matrixStack.peek();

            for (int j = 0; j < vertices2.length; j += 8) {
               vertices.vertex(peek2, vertices2[j], vertices2[j + 1], vertices2[j + 2])
                  .color(peekDeterminantValue2)
                  .texture(vertices2[j + 3], vertices2[j + 4])
                  .overlay(peekDeterminantValue3)
                  .light(peekDeterminantValue)
                  .normal(peek2, vertices2[j + 5], vertices2[j + 6], vertices2[j + 7]);
            }

            matrixStack.pop();
         }
      }
   }

   static Matrix4f[] getMatrixModel(C2Model c2Model2) {
      if (c2Model != c2Model2 || invertMul == null) {
         invertMul = C2Frame.getInvertMul(c2Model2);
         c2Model = c2Model2;
      }

      return invertMul;
   }

   static RenderLayer getNoCull(C2Assets.Loaded loaded) {
      return loaded.specular() != null ? C2Surface.getCutoutCull(loaded.texture(), loaded.specular()) : RenderLayers.entityCutoutNoCull(loaded.texture());
   }
}
