package com.corz.client.utils;

import com.corz.client.render.SamplerBuilderUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.GpuSampler;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class TrailSampler {
   private GpuTexture[] dummyVertexGpuTextures;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private int samplerTrailValue;
   private int dummyTextureHeight;
   private GpuBuffer buffer;
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private GpuTextureView[] samplerTrailGpuTextureViews;
   private boolean dummyVertexFlag;
   private GpuBuffer gpuBuffer;
   private ByteBuffer memAlloc;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private GpuSampler sampler;
   private int gpuDummyValue;

   private void handleBufferAlloc(CommandEncoder commandEncoder) {
      int remaining = this.memAlloc.remaining();
      if (this.buffer == null || this.buffer.size() < remaining) {
         if (this.buffer != null) {
            this.buffer.close();
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:hand_trail_uniform", 136, remaining);
      }

      commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);
   }

   private void updateCreateEncoder() {
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

      for (int i = 0; i < 2; i++) {
         int encoderValue = i;
         RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:hand_trail_clear_" + encoderValue, this.samplerTrailGpuTextureViews[i], OptionalInt.of(0));
         if (renderPass != null) {
            renderPass.close();
         }
      }
   }

   private void handleGpuDummy(int value, int textureHeight) {
      if (this.gpuDummyValue != value || this.dummyTextureHeight != textureHeight || this.dummyVertexGpuTextures[0] == null || this.dummyVertexGpuTextures[1] == null) {
         this.updateGpuViews();

         for (int i = 0; i < 2; i++) {
            int gpuDummyValue = i;
            this.dummyVertexGpuTextures[i] = RenderSystem.getDevice()
               .createTexture(() -> "corzclient:hand_trail_" + gpuDummyValue, 13, TextureFormat.RGBA8, value, textureHeight, 1, 1);
            this.samplerTrailGpuTextureViews[i] = RenderSystem.getDevice().createTextureView(this.dummyVertexGpuTextures[i]);
         }

         this.gpuDummyValue = value;
         this.dummyTextureHeight = textureHeight;
         this.samplerTrailValue = 0;
         this.updateCreateEncoder();
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

   public void updateBufferMem() {
      this.updateGpuViews();
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

   private void handleAllocMem(
      int allocMemValue,
      int allocMemValue2,
      float allocMemValue3,
      float allocMemValue4,
      float allocMemValue5,
      float allocMemValue6,
      float allocMemValue7,
      float allocMemValue8,
      float allocMemValue9,
      float allocMemValue10,
      float allocMemValue11,
      float allocMemValue12,
      float allocMemValue13,
      float allocMemValue14,
      float allocMemValue15,
      float allocMemValue16,
      float allocMemValue17,
      float allocMemValue18
   ) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(allocMemValue);
      this.memAlloc.putFloat(allocMemValue2);
      this.memAlloc.putFloat(allocMemValue3);
      this.memAlloc.putFloat(allocMemValue4);
      this.memAlloc.putFloat(allocMemValue5);
      this.memAlloc.putFloat(allocMemValue6);
      this.memAlloc.putFloat(allocMemValue7);
      this.memAlloc.putFloat(allocMemValue8);
      this.memAlloc.putFloat(allocMemValue9);
      this.memAlloc.putFloat(allocMemValue10);
      this.memAlloc.putFloat(allocMemValue11);
      this.memAlloc.putFloat(allocMemValue12);
      this.memAlloc.putFloat(allocMemValue13);
      this.memAlloc.putFloat(allocMemValue14);
      this.memAlloc.putFloat(allocMemValue15);
      this.memAlloc.putFloat(allocMemValue18);
      this.memAlloc.putFloat(allocMemValue16);
      this.memAlloc.putFloat(allocMemValue17);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.flip();
   }

   public GpuTexture getDummyTextures() {
      return this.dummyVertexGpuTextures == null ? null : this.dummyVertexGpuTextures[this.samplerTrailValue];
   }

   private void updateGpuViews() {
      if (this.samplerTrailGpuTextureViews != null) {
         for (int i = 0; i < this.samplerTrailGpuTextureViews.length; i++) {
            if (this.samplerTrailGpuTextureViews[i] != null) {
               this.samplerTrailGpuTextureViews[i].close();
               this.samplerTrailGpuTextureViews[i] = null;
            }

            if (this.dummyVertexGpuTextures[i] != null) {
               this.dummyVertexGpuTextures[i].close();
               this.dummyVertexGpuTextures[i] = null;
            }
         }
      }

      this.gpuDummyValue = 0;
      this.dummyTextureHeight = 0;
   }

   public void updateViewsTrail() {
      if (this.samplerTrailGpuTextureViews != null && this.samplerTrailGpuTextureViews[0] != null && this.samplerTrailGpuTextureViews[1] != null) {
         this.updateCreateEncoder();
      }
   }

   private void updateDummyVertex() {
      if (!this.dummyVertexFlag) {
         this.memAlloc = MemoryUtil.memAlloc(80);
         this.gpuBuffer = getBuffer("corzclient:hand_trail_dummy_vertex");
         this.sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         this.dummyVertexGpuTextures = new GpuTexture[2];
         this.samplerTrailGpuTextureViews = new GpuTextureView[2];
         this.dummyVertexFlag = true;
      }
   }

   public GpuTextureView getSamplerTrail(
      GpuTextureView gpuTextureView,
      GpuTextureView gpuTextureView2,
      int samplerTrailLimit,
      int textureHeight,
      float samplerTrailValue,
      float samplerTrailValue2,
      float redScaled,
      float greenScaled,
      float blueScaled,
      float alphaScaled,
      float samplerTrailValue7,
      float samplerTrailValue8,
      float samplerTrailValue9,
      float allocMemValue,
      float samplerTrailValue11,
      float samplerTrailValue12,
      float postTextureValue,
      float samplerTrailValue14,
      float samplerTrailValue15,
      float samplerTrailValue16
   ) {
      if (gpuTextureView != null && gpuTextureView2 != null && samplerTrailLimit > 0 && textureHeight > 0) {
         this.updateDummyVertex();
         this.handleGpuDummy(samplerTrailLimit, textureHeight);
         int samplerTrailValue17 = 1 - this.samplerTrailValue;
         this.handleAllocMem(
            samplerTrailLimit, textureHeight, samplerTrailValue, samplerTrailValue2, redScaled, greenScaled, blueScaled, alphaScaled, samplerTrailValue7, samplerTrailValue8, samplerTrailValue9, allocMemValue, samplerTrailValue11, samplerTrailValue12, postTextureValue, samplerTrailValue14, samplerTrailValue15, samplerTrailValue16
         );
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         this.handleBufferAlloc(commandEncoder);
         GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

         try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:hand_trail", this.samplerTrailGpuTextureViews[samplerTrailValue17], OptionalInt.of(0))) {
            renderPass.setPipeline(SamplerBuilderUtils.SAMPLER_TRAIL_PIPELINE);
            renderPass.setVertexBuffer(0, this.gpuBuffer);
            renderPass.bindTexture("PrevTrailSampler", this.samplerTrailGpuTextureViews[this.samplerTrailValue], this.sampler);
            renderPass.bindTexture("SceneSampler", gpuTextureView, this.sampler);
            renderPass.bindTexture("MaskSampler", gpuTextureView2, this.sampler);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", write);
            renderPass.setUniform("HandTrailData", this.buffer);
            renderPass.draw(0, 6);
         }

         this.samplerTrailValue = samplerTrailValue17;
         return this.samplerTrailGpuTextureViews[this.samplerTrailValue];
      } else {
         return null;
      }
   }
}
