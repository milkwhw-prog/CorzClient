package com.corz.client.utils;

import com.corz.client.render.SamplerBuilderUtils;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.OptionalInt;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.RadiansFramebuffer;

public class MemRender {
   private final GpuBuffer[] gpuBuffers = new GpuBuffer[4];
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static final Vector3f VECTOR3F = new Vector3f(0.0F, 0.0F, 0.0F);
   private int bufferIndex;
   private boolean fogDummyFlag;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private GpuBuffer gpuBuffer;
   private final Map<RenderPipeline, Boolean> precompilePipelineBooleanMap = new IdentityHashMap<>();
   private ByteBuffer memAlloc;

   public void handleMemAlloc(GpuTextureView gpuTextureView, RenderPipeline renderPipeline, RadiansFramebuffer radiansFramebuffer) {
      if (gpuTextureView != null && !(radiansFramebuffer.framebufferWidth <= 0.0F) && !(radiansFramebuffer.framebufferHeight <= 0.0F)) {
         this.updateFogDummy();
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         GpuBuffer buffer = this.getBuffer();
         this.memAlloc.clear();
         MemoryUtil.memSet(this.memAlloc, 0);
         radiansFramebuffer.handleAmountRadians(this.memAlloc);
         this.memAlloc.position(0).limit(144);
         commandEncoder.writeToBuffer(buffer.slice(), this.memAlloc);
         GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), VECTOR4F, VECTOR3F, MATRIX4F);

         try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:sky", gpuTextureView, OptionalInt.empty())) {
            renderPass.setPipeline(renderPipeline);
            renderPass.setVertexBuffer(0, this.gpuBuffer);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", write);
            renderPass.setUniform("ShaderFogData", buffer);
            renderPass.draw(0, 6);
         }
      }
   }

   public void updateGpuMem() {
      for (int i = 0; i < 4; i++) {
         if (this.gpuBuffers[i] != null) {
            this.gpuBuffers[i].close();
            this.gpuBuffers[i] = null;
         }
      }

      if (this.gpuBuffer != null) {
         this.gpuBuffer.close();
         this.gpuBuffer = null;
      }

      if (this.memAlloc != null) {
         MemoryUtil.memFree(this.memAlloc);
         this.memAlloc = null;
      }

      this.fogDummyFlag = false;
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

   private GpuBuffer getBuffer() {
      this.bufferIndex = (this.bufferIndex + 1) % 4;
      GpuBuffer buffer = this.gpuBuffers[this.bufferIndex];
      if (buffer == null || buffer.isClosed()) {
         int bufferValue = this.bufferIndex;
         buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:shader_fog_uniform_" + bufferValue, 136, 144L);
         this.gpuBuffers[this.bufferIndex] = buffer;
      }

      return buffer;
   }

   public boolean canPrecompilePipeline(RenderPipeline renderPipeline) {
      Boolean precompilePipeline = this.precompilePipelineBooleanMap.get(renderPipeline);
      if (precompilePipeline == null) {
         try {
            precompilePipeline = RenderSystem.getDevice().precompilePipeline(renderPipeline).isValid();
         } catch (Throwable e) {
            precompilePipeline = false;
         }

         this.precompilePipelineBooleanMap.put(renderPipeline, precompilePipeline);
      }

      return precompilePipeline;
   }

   public static RenderPipeline getPipelineRender(String pipelineRenderText) {
      return switch (pipelineRenderText) {
         case "Drain" -> SamplerBuilderUtils.SHADER_FOG;
         case "Nebula" -> SamplerBuilderUtils.NEBULA_PIPELINE;
         case "Plasma" -> SamplerBuilderUtils.PLASMA_PIPELINE;
         case "Saturn" -> SamplerBuilderUtils.SATURN_PIPELINE;
         case "Northern Lights" -> SamplerBuilderUtils.RENDER_PIPELINE;
         case "Black Hole" -> SamplerBuilderUtils.BLACK_HOLE_PIPELINE;
         default -> SamplerBuilderUtils.BLOOM_PIPELINE;
      };
   }

   private void updateFogDummy() {
      if (!this.fogDummyFlag) {
         this.memAlloc = MemoryUtil.memAlloc(144);
         this.gpuBuffer = getBuffer("corzclient:shader_fog_dummy_vertex");
         this.fogDummyFlag = true;
      }
   }

   public void handle(RenderPipeline renderPipeline) {
      this.precompilePipelineBooleanMap.put(renderPipeline, Boolean.FALSE);
   }
}
