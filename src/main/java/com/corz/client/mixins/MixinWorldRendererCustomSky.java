package com.corz.client.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.CustomSky;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererCustomSky {
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;

   @Inject(
      method = {"method_62200(Lnet/minecraft/class_9909;Lnet/minecraft/class_4184;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$customSky(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null) {
         float vis = sf.getAmount();
         if (!(vis < 1.0F)) {
            this.corz$addSkyPass(frameGraphBuilder, gpuBufferSlice, sf, 1.0F);
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"method_62200(Lnet/minecraft/class_9909;Lnet/minecraft/class_4184;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("RETURN")}
   )
   private void corz$customSkyFade(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null) {
         float vis = sf.getEasedFade();
         if (!(vis <= 0.0F) && !(vis >= 1.0F)) {
            this.corz$addSkyPass(frameGraphBuilder, gpuBufferSlice, sf, vis);
         }
      }
   }

   @Unique
   private void corz$addSkyPass(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice shaderFog, CustomSky customSky, float corzaddSkyValue) {
      FramePass framePass = frameGraphBuilder.createPass("corz_custom_sky");
      this.framebufferSet.mainFramebuffer = framePass.transfer(this.framebufferSet.mainFramebuffer);
      framePass.setRenderer(() -> {
         RenderSystem.setShaderFog(shaderFog);
         customSky.handleFramebufferWindow(corzaddSkyValue);
      });
   }

   @Inject(
      method = {"method_62204(Lnet/minecraft/class_9909;Lnet/minecraft/class_4063;Lnet/minecraft/class_243;JFIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$customSkyClouds(
      FrameGraphBuilder frameGraphBuilder, CloudRenderMode cloudRenderMode, Vec3d vec, long corzcustomSkyValue, float corzcustomSkyValue2, int corzcustomSkyValue3, float corzcustomSkyValue4, CallbackInfo callbackInfo
   ) {
      CustomSky sf = CustomSky.getCustomSky();
      if (sf != null && sf.isMax()) {
         callbackInfo.cancel();
      }
   }
}
