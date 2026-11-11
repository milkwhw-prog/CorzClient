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

public class OutlineSampler {
   private GpuSampler sampler;
   private ByteBuffer memAlloc;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private GpuBuffer gpuBuffer;
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private boolean dummyVertexFlag;
   private GpuBuffer buffer;

   private void handleBufferAlloc(CommandEncoder commandEncoder) {
      int remaining = this.memAlloc.remaining();
      if (this.buffer == null || this.buffer.size() < remaining) {
         if (this.buffer != null) {
            this.buffer.close();
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:hand_outline_uniform", 136, remaining);
      }

      commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);
   }

   private void updateDummyVertex() {
      if (!this.dummyVertexFlag) {
         this.memAlloc = MemoryUtil.memAlloc(48);
         this.gpuBuffer = getBuffer("corzclient:hand_outline_dummy_vertex");
         this.sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         this.dummyVertexFlag = true;
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

   public void handleSamplerDynamic(
      GpuTextureView gpuTextureView,
      GpuTextureView gpuTextureView2,
      GpuTextureView gpuTextureView3,
      int samplerDynamicLimit,
      int textureHeight,
      float samplerDynamicValue,
      float samplerDynamicValue2,
      float samplerDynamicValue3,
      int samplerDynamicValue4,
      float samplerDynamicValue5,
      float samplerDynamicValue6,
      float samplerDynamicValue7,
      float samplerDynamicValue8
   ) {
      if (gpuTextureView != null && gpuTextureView2 != null && gpuTextureView3 != null && samplerDynamicLimit > 0 && textureHeight > 0) {
         this.updateDummyVertex();
         this.handleAllocMem(samplerDynamicLimit, textureHeight, samplerDynamicValue, samplerDynamicValue2, samplerDynamicValue3, samplerDynamicValue4, samplerDynamicValue5, samplerDynamicValue6, samplerDynamicValue7, samplerDynamicValue8);
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         this.handleBufferAlloc(commandEncoder);
         GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

         try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:hand_outline", gpuTextureView, OptionalInt.empty())) {
            renderPass.setPipeline(SamplerBuilderUtils.SAMPLER_DYNAMIC_PIPELINE);
            renderPass.setVertexBuffer(0, this.gpuBuffer);
            renderPass.bindTexture("SceneSampler", gpuTextureView2, this.sampler);
            renderPass.bindTexture("MaskSampler", gpuTextureView3, this.sampler);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", write);
            renderPass.setUniform("HandOutlineData", this.buffer);
            renderPass.draw(0, 6);
         }
      }
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

   private void handleAllocMem(int allocMemValue, int textureHeight, float samplerDynamicValue, float allocMemValue4, float allocMemValue5, int allocMemValue6, float allocMemValue7, float allocMemValue8, float allocMemValue9, float allocMemValue10) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(allocMemValue);
      this.memAlloc.putFloat(textureHeight);
      this.memAlloc.putFloat(samplerDynamicValue);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.putFloat(allocMemValue7);
      this.memAlloc.putFloat(allocMemValue8);
      this.memAlloc.putFloat(allocMemValue9);
      this.memAlloc.putFloat(allocMemValue10);
      this.memAlloc.putFloat(allocMemValue4);
      this.memAlloc.putFloat(allocMemValue5);
      this.memAlloc.putFloat(allocMemValue6);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.flip();
   }
}
