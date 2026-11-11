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

public class SamplerMask {
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private GpuSampler sampler;
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private boolean diffDummyFlag;
   private GpuBuffer buffer;
   private GpuBuffer gpuBuffer;
   private ByteBuffer memAlloc;

   private void updateDiffDummy() {
      if (!this.diffDummyFlag) {
         this.memAlloc = MemoryUtil.memAlloc(16);
         this.gpuBuffer = getBuffer("corzclient:mask_diff_dummy_vertex");
         this.sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         this.diffDummyFlag = true;
      }
   }

   private void handleBufferAlloc(CommandEncoder commandEncoder) {
      int remaining = this.memAlloc.remaining();
      if (this.buffer == null || this.buffer.size() < remaining) {
         if (this.buffer != null) {
            this.buffer.close();
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:mask_diff_uniform", 136, remaining);
      }

      commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);
   }

   private void handleAllocMem(int allocMemValue, int allocMemValue2) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(allocMemValue);
      this.memAlloc.putFloat(allocMemValue2);
      this.memAlloc.putFloat(0.0F);
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

      this.diffDummyFlag = false;
   }

   public void handleSamplerBind(
      GpuTextureView gpuTextureView, GpuTextureView gpuTextureView2, GpuTextureView gpuTextureView3, GpuTextureView gpuTextureView4, GpuTextureView gpuTextureView5, int samplerBindLimit, int textureHeight
   ) {
      if (gpuTextureView != null && gpuTextureView2 != null && gpuTextureView3 != null && gpuTextureView4 != null && gpuTextureView5 != null) {
         if (samplerBindLimit > 0 && textureHeight > 0) {
            this.updateDiffDummy();
            this.handleAllocMem(samplerBindLimit, textureHeight);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            this.handleBufferAlloc(commandEncoder);
            GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:mask_diff", gpuTextureView, OptionalInt.of(0))) {
               renderPass.setPipeline(SamplerBuilderUtils.SAMPLER_BIND_PIPELINE);
               renderPass.setVertexBuffer(0, this.gpuBuffer);
               renderPass.bindTexture("BeforeSampler", gpuTextureView2, this.sampler);
               renderPass.bindTexture("AfterSampler", gpuTextureView3, this.sampler);
               renderPass.bindTexture("DepthBeforeSampler", gpuTextureView4, this.sampler);
               renderPass.bindTexture("DepthAfterSampler", gpuTextureView5, this.sampler);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("DynamicTransforms", write);
               renderPass.setUniform("MaskData", this.buffer);
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
}
