package com.corz.client.mixins;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.NameTags;
import ai.auth.ModuleRegistry;

@Mixin({PlayerEntityRenderer.class})
public class MixinPlayerEntityRenderer {
   @Inject(
      method = {"method_4213(Lnet/minecraft/class_10055;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz_cancelPlayerLabel(
      PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo
   ) {
      NameTags nameTags = (NameTags)ModuleRegistry.getModule(NameTags.class);
      if (nameTags != null && nameTags.isFlag()) {
         callbackInfo.cancel();
      }
   }
}
