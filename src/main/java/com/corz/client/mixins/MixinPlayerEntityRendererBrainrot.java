package com.corz.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.utils.RenderMatrixUtilsWarm;

@Mixin({PlayerEntityRenderer.class})
public class MixinPlayerEntityRendererBrainrot {
   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("TAIL")}
   )
   private void corz$captureBrainrot(PlayerLikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float corzcaptureBrainrotValue, CallbackInfo callbackInfo) {
      RenderMatrixUtilsWarm.handleProgressPose(playerLikeEntity, playerEntityRenderState);
   }

   @Inject(
      method = {"method_62606(Lnet/minecraft/class_10055;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$brainrotHidesFeatures(PlayerEntityRenderState playerEntityRenderState, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (RenderMatrixUtilsWarm.canInvisible(playerEntityRenderState)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @WrapOperation(
      method = {"method_23205(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Lnet/minecraft/class_630;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11659;method_73491(Lnet/minecraft/class_630;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IILnet/minecraft/class_1058;)V"
      )}
   )
   private void corz$brainrotArm(
      OrderedRenderCommandQueue orderedRenderCommandQueue, ModelPart modelPart, MatrixStack matrixStack, RenderLayer renderLayer, int corzbrainrotArmValue, int corzbrainrotArmValue2, Sprite sprite, Operation<Void> operation
   ) {
      PlayerEntityModel model = ((net.minecraft.client.render.entity.model.PlayerEntityModel) (Object) (((PlayerEntityRenderer)((net.minecraft.client.render.entity.PlayerEntityRenderer) (Object) (this))).getModel()));
      if (!RenderMatrixUtilsWarm.canPersonCustom(orderedRenderCommandQueue, modelPart, matrixStack, corzbrainrotArmValue, modelPart == model.rightArm)) {
         operation.call(new Object[]{orderedRenderCommandQueue, modelPart, matrixStack, renderLayer, corzbrainrotArmValue, corzbrainrotArmValue2, sprite});
      }
   }
}
