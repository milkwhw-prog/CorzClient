package com.corz.client.mixins;

import com.corz.client.render.VectorDotUtils;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererTracers {
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;

   @Inject(
      method = {"method_62199(Lnet/minecraft/class_9909;Lnet/minecraft/class_12075;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void corz$tracerPass(FrameGraphBuilder frameGraphBuilder, CameraRenderState cameraRenderState, GpuBufferSlice shaderFog, Matrix4f matrix4f, CallbackInfo callbackInfo) {
      FramePass pass = frameGraphBuilder.createPass("corz_tracers");
      this.framebufferSet.mainFramebuffer = pass.transfer(this.framebufferSet.mainFramebuffer);
      pass.setRenderer(() -> {
         RenderSystem.setShaderFog(shaderFog);
         VectorDotUtils.updateDrawImmediate();
      });
   }
}
