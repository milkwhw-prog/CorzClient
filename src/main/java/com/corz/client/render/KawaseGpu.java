package com.corz.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
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

public class KawaseGpu {
   private GpuBuffer gpuBuffer;
   private int[] kawaseDummyTable;
   private int[] kawaseFinalTable;
   private int[] kawaseFinalValues;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private ByteBuffer memAlloc;
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private boolean kawaseDummyFlag;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private GpuTexture[] kawaseDummyGpuTextures;
   private GpuSampler sampler;
   private int framebufferWidth;
   private GpuTextureView[] kawaseCorzclientGpuTextureViews;
   private GpuBuffer buffer;
   private GpuTextureView[] kawaseDummyGpuTextureViews;
   private int[] kawaseGpuTable;
   private GpuTexture texture;
   private int framebufferHeight;
   private GpuTextureView textureView;
   private GpuTexture[] kawaseGpuGpuTextures;

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

   private void handleDynamicSampler(
      CommandEncoder commandEncoder, RenderPipeline renderPipeline, GpuTextureView gpuTextureView, GpuTextureView gpuTextureView2, int dynamicSamplerValue, int dynamicSamplerValue2, float kawaseFinalValue, String dynamicSamplerText
   ) {
      if (gpuTextureView != null && gpuTextureView2 != null) {
         this.handleAllocMem(dynamicSamplerValue, dynamicSamplerValue2, kawaseFinalValue);
         this.handleBufferAlloc(commandEncoder);
         GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

         try (RenderPass renderPass = commandEncoder.createRenderPass(() -> dynamicSamplerText, gpuTextureView2, OptionalInt.of(0))) {
            renderPass.setPipeline(renderPipeline);
            renderPass.setVertexBuffer(0, this.gpuBuffer);
            renderPass.bindTexture("Sampler0", gpuTextureView, this.sampler);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", write);
            renderPass.setUniform("KawaseData", this.buffer);
            renderPass.draw(0, 6);
         }
      }
   }

   public GpuTextureView getKawaseFinal(GpuTexture gpuTexture, GpuTextureView gpuTextureView, int kawaseFinalLimit, int kawaseFinalLimit2, int kawaseFinalBound, float kawaseFinalValue) {
      if (gpuTexture != null && gpuTextureView != null && kawaseFinalLimit > 0 && kawaseFinalLimit2 > 0) {
         this.updateKawaseDummy();
         this.handleTextureKawase(kawaseFinalLimit, kawaseFinalLimit2);
         kawaseFinalBound = Math.min(Math.max(kawaseFinalBound, 1), 8);
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         GpuTextureView gpuTextureView2 = gpuTextureView;
         int dynamicSamplerValue = kawaseFinalLimit;
         int sourceTexture2 = kawaseFinalLimit2;

         for (int i = 0; i < kawaseFinalBound; i++) {
            this.handleDynamicSampler(
               commandEncoder,
               SamplerBuilderUtils.REGISTER,
               gpuTextureView2,
               this.kawaseDummyGpuTextureViews[i],
               dynamicSamplerValue,
               sourceTexture2,
               kawaseFinalValue,
               "corzclient:kawase_down_" + i
            );
            gpuTextureView2 = this.kawaseDummyGpuTextureViews[i];
            dynamicSamplerValue = this.kawaseFinalTable[i];
            sourceTexture2 = this.kawaseDummyTable[i];
         }

         for (int j = kawaseFinalBound - 1; j >= 0; j--) {
            this.handleDynamicSampler(
               commandEncoder,
               SamplerBuilderUtils.KAWASE_FINAL_PIPELINE,
               gpuTextureView2,
               this.kawaseCorzclientGpuTextureViews[j],
               dynamicSamplerValue,
               sourceTexture2,
               kawaseFinalValue,
               "corzclient:kawase_up_" + j
            );
            gpuTextureView2 = this.kawaseCorzclientGpuTextureViews[j];
            dynamicSamplerValue = this.kawaseFinalValues[j];
            sourceTexture2 = this.kawaseGpuTable[j];
         }

         this.handleDynamicSampler(commandEncoder, SamplerBuilderUtils.KAWASE_FINAL_PIPELINE, gpuTextureView2, this.textureView, dynamicSamplerValue, sourceTexture2, kawaseFinalValue, "corzclient:kawase_final");
         return this.textureView;
      } else {
         return null;
      }
   }

   private void handleAllocMem(int allocMemValue, int allocMemValue2, float kawaseFinalValue) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(allocMemValue);
      this.memAlloc.putFloat(allocMemValue2);
      this.memAlloc.putFloat(kawaseFinalValue);
      this.memAlloc.putFloat(0.0F);
      this.memAlloc.flip();
   }

   private void updateKawaseDummy() {
      if (!this.kawaseDummyFlag) {
         this.memAlloc = MemoryUtil.memAlloc(16);
         this.gpuBuffer = getBuffer("corzclient:kawase_dummy_vertex");
         this.sampler = RenderSystem.getDevice()
            .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
         this.kawaseDummyGpuTextures = new GpuTexture[8];
         this.kawaseDummyGpuTextureViews = new GpuTextureView[8];
         this.kawaseGpuGpuTextures = new GpuTexture[8];
         this.kawaseCorzclientGpuTextureViews = new GpuTextureView[8];
         this.kawaseFinalTable = new int[8];
         this.kawaseDummyTable = new int[8];
         this.kawaseFinalValues = new int[8];
         this.kawaseGpuTable = new int[8];
         this.kawaseDummyFlag = true;
      }
   }

   private void handleBufferAlloc(CommandEncoder commandEncoder) {
      int remaining = this.memAlloc.remaining();
      if (this.buffer == null || this.buffer.size() < remaining) {
         if (this.buffer != null) {
            this.buffer.close();
         }

         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:kawase_uniform", 136, remaining);
      }

      commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);
   }

   public void updateBufferMem() {
      this.updateKawaseGpu();
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

      this.kawaseDummyFlag = false;
   }

   private void handleTextureKawase(int framebufferWidth, int framebufferHeight) {
      if (framebufferWidth != this.framebufferWidth || framebufferHeight != this.framebufferHeight || this.texture == null) {
         this.updateKawaseGpu();
         this.texture = RenderSystem.getDevice().createTexture(() -> "corzclient:kawase_final", 13, TextureFormat.RGBA8, framebufferWidth, framebufferHeight, 1, 1);
         this.textureView = RenderSystem.getDevice().createTextureView(this.texture);
         int textureKawaseValue = framebufferWidth;
         int framebufferHeight2 = framebufferHeight;

         for (int i = 0; i < 8; i++) {
            textureKawaseValue = Math.max(1, textureKawaseValue / 2);
            framebufferHeight2 = Math.max(1, framebufferHeight2 / 2);
            int textureKawaseValue2 = i;
            this.kawaseFinalTable[i] = textureKawaseValue;
            this.kawaseDummyTable[i] = framebufferHeight2;
            this.kawaseFinalValues[i] = textureKawaseValue;
            this.kawaseGpuTable[i] = framebufferHeight2;
            this.kawaseDummyGpuTextures[i] = RenderSystem.getDevice()
               .createTexture(() -> "corzclient:kawase_down_" + textureKawaseValue2, 13, TextureFormat.RGBA8, textureKawaseValue, framebufferHeight2, 1, 1);
            this.kawaseDummyGpuTextureViews[i] = RenderSystem.getDevice().createTextureView(this.kawaseDummyGpuTextures[i]);
            this.kawaseGpuGpuTextures[i] = RenderSystem.getDevice()
               .createTexture(() -> "corzclient:kawase_up_" + textureKawaseValue2, 13, TextureFormat.RGBA8, textureKawaseValue, framebufferHeight2, 1, 1);
            this.kawaseCorzclientGpuTextureViews[i] = RenderSystem.getDevice().createTextureView(this.kawaseGpuGpuTextures[i]);
         }

         this.framebufferWidth = framebufferWidth;
         this.framebufferHeight = framebufferHeight;
      }
   }

   private void updateKawaseGpu() {
      if (this.textureView != null) {
         this.textureView.close();
         this.textureView = null;
      }

      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }

      if (this.kawaseDummyGpuTextureViews != null) {
         for (int i = 0; i < 8; i++) {
            if (this.kawaseDummyGpuTextureViews[i] != null) {
               this.kawaseDummyGpuTextureViews[i].close();
               this.kawaseDummyGpuTextureViews[i] = null;
            }

            if (this.kawaseDummyGpuTextures[i] != null) {
               this.kawaseDummyGpuTextures[i].close();
               this.kawaseDummyGpuTextures[i] = null;
            }

            if (this.kawaseCorzclientGpuTextureViews[i] != null) {
               this.kawaseCorzclientGpuTextureViews[i].close();
               this.kawaseCorzclientGpuTextureViews[i] = null;
            }

            if (this.kawaseGpuGpuTextures[i] != null) {
               this.kawaseGpuGpuTextures[i].close();
               this.kawaseGpuGpuTextures[i] = null;
            }
         }
      }

      this.framebufferWidth = 0;
      this.framebufferHeight = 0;
   }
}
