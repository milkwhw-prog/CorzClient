package com.corz.client.utils;

import com.corz.client.render.SamplerBuilderUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.GpuSampler;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class FireSampler {
   private ByteBuffer memAlloc;
   private GpuBuffer gpuBuffer;
   private GpuSampler sampler;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private boolean dummyVertexFlag;
   private GpuBuffer buffer;

   public void handleSamplerBind(
      GpuTextureView gpuTextureView,
      GpuTextureView gpuTextureView2,
      GpuTextureView kawaseFinal,
      GpuTextureView gpuTextureView4,
      int samplerBindLimit,
      int textureHeight,
      float samplerBindValue,
      float samplerBindValue2,
      float redScaled,
      float greenScaled,
      float blueScaled,
      float alphaScaled,
      float samplerBindValue7,
      float samplerBindValue8,
      float samplerBindValue9,
      float samplerBindValue10,
      float samplerBindValue11,
      float samplerBindValue12,
      float postTextureValue
   ) {
      if (gpuTextureView != null && gpuTextureView2 != null && kawaseFinal != null && gpuTextureView4 != null) {
         if (samplerBindLimit > 0 && textureHeight > 0) {
            this.updateDummyVertex();
            this.handleAllocMem(samplerBindLimit, textureHeight, samplerBindValue, samplerBindValue2, redScaled, greenScaled, blueScaled, alphaScaled, samplerBindValue7, samplerBindValue8, samplerBindValue9, samplerBindValue10, samplerBindValue11, samplerBindValue12, postTextureValue);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            this.handleBufferAlloc(commandEncoder);
            GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:hand_fire", gpuTextureView, OptionalInt.empty())) {
               renderPass.setPipeline(SamplerBuilderUtils.PIPELINE);
               renderPass.setVertexBuffer(0, this.gpuBuffer);
               renderPass.bindTexture("SceneSampler", gpuTextureView2, this.sampler);
               renderPass.bindTexture("BlurSampler", kawaseFinal, this.sampler);
               renderPass.bindTexture("MaskSampler", gpuTextureView4, this.sampler);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("DynamicTransforms", write);
               renderPass.setUniform("HandFireData", this.buffer);
               renderPass.draw(0, 6);
            }
         }
      }
   }

   private static GpuBuffer getBuffer(String bufferText) {
      ByteBuffer memAlloc = MemoryUtil.memAlloc(4);

      GpuBuffer buffer;
      try {
         memAlloc.putInt(0);
         memAlloc.flip();
         buffer = RenderSystem.getDevice().createBuffer(() -> bufferText, 32, memAlloc);
      } finally {
         MemoryUtil.memFree(memAlloc);
      }

      return buffer;
   }

   private void updateDummyVertex() {
      if (!this.dummyVertexFlag) {
         this.memAlloc = MemoryUtil.memAlloc(64);
         this.gpuBuffer = getBuffer("corzclient:hand_fire_dummy_vertex");
         this.sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         this.dummyVertexFlag = true;
      }
   }

   private void handleAllocMem(
      int allocMemValue,
      int textureHeight,
      float samplerBindValue,
      float allocMemValue4,
      float redScaled,
      float greenScaled,
      float blueScaled,
      float alphaScaled,
      float allocMemValue9,
      float allocMemValue10,
      float allocMemValue11,
      float allocMemValue12,
      float allocMemValue13,
      float allocMemValue14,
      float postTextureValue
   ) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(allocMemValue);
      this.memAlloc.putFloat(textureHeight);
      this.memAlloc.putFloat(samplerBindValue);
      this.memAlloc.putFloat(allocMemValue4);
      this.memAlloc.putFloat(redScaled);
      this.memAlloc.putFloat(greenScaled);
      this.memAlloc.putFloat(blueScaled);
      this.memAlloc.putFloat(alphaScaled);
      this.memAlloc.putFloat(allocMemValue9);
      this.memAlloc.putFloat(allocMemValue10);
      this.memAlloc.putFloat(allocMemValue11);
      this.memAlloc.putFloat(allocMemValue12);
      this.memAlloc.putFloat(allocMemValue13);
      this.memAlloc.putFloat(allocMemValue14);
      this.memAlloc.putFloat(postTextureValue);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.flip();
   }

   public void updateBufferMem() {
      if (this.sampler != null) {
         this.sampler.close();
         this.sampler = null;
      }

      if (this.buffer != null) {
         this.buffer.close();
         this.buffer = null;
      }

      if (this.gpuBuffer != null) {
         this.gpuBuffer.close();
         this.gpuBuffer = null;
      }

      if (this.memAlloc != null) {
         MemoryUtil.memFree(this.memAlloc);
         this.memAlloc = null;
      }

      this.dummyVertexFlag = false;
   }

   private void handleBufferAlloc(CommandEncoder commandEncoder) {
      int remaining = this.memAlloc.remaining();
      if (this.buffer == null || this.buffer.size() < remaining) {
         if (this.buffer != null) {
            this.buffer.close();
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:hand_fire_uniform", 136, remaining);
      }

      commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);
   }
}
