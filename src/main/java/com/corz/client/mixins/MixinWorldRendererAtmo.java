package com.corz.client.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.utils.RenderAtmoUtils;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererAtmo {
   @Inject(
      method = {"method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At(value = "INVOKE", target = "Lnet/minecraft/class_9909;method_61910(Lnet/minecraft/class_9922;Lnet/minecraft/class_9909$class_9912;)V", shift = At.Shift.AFTER)},
      require = 0
   )
   private void corz$atmoAfterFrameGraph(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline,
         Camera camera, Matrix4f positionMatrix, Matrix4f basicProjection, Matrix4f projection,
         GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
      RenderAtmoUtils.handleRenderLate(fogBuffer);
   }
}
