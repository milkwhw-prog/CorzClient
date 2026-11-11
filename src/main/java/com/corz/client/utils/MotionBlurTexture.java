package com.corz.client.utils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

public final class MotionBlurTexture {
   private static final BlendFunction BLEND_FUNCTION = new BlendFunction(
      SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA
   );
   private Boolean booleanValue;
   private ByteBuffer memAlloc;
   private boolean textureAllocActive;
   public static final RenderPipeline register = RenderPipelines.register(
      RenderPipeline.builder()
         .withLocation(Identifier.of("corzclient", "pipeline/motion_blur"))
         .withVertexShader(Identifier.of("corzclient", "core/motion_blur"))
         .withFragmentShader(Identifier.of("corzclient", "core/motion_blur"))
         .withUniform("MotionBlurData", UniformType.UNIFORM_BUFFER)
         .withSampler("HistorySampler")
         .withBlend(MotionBlurTexture.BLEND_FUNCTION)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .build()
   );
   private int textureHeight;
   private GpuBuffer buffer;
   private GpuTexture texture;
   private int textureWidth;
   private GpuTextureView textureView;

   public boolean canTextureAlloc(Framebuffer framebuffer, float textureAllocValue, float allocMemValue, float textureAllocValue3, float textureAllocValue4) {
      if (framebuffer == null) {
         return false;
      } else {
         GpuTexture colorAttachment = framebuffer.getColorAttachment();
         GpuTextureView colorAttachmentView = framebuffer.getColorAttachmentView();
         if (colorAttachment == null || colorAttachmentView == null || colorAttachment.isClosed()) {
            return false;
         } else if (!this.isPrecompilePipeline()) {
            return false;
         } else {
            int textureWidth = framebuffer.textureWidth;
            int textureHeight = framebuffer.textureHeight;
            if (textureWidth > 0 && textureHeight > 0) {
               this.handleTextureDevice(textureWidth, textureHeight);
               CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
               if (!this.textureAllocActive) {
                  commandEncoder.copyTextureToTexture(colorAttachment, this.texture, 0, 0, 0, 0, 0, textureWidth, textureHeight);
                  this.textureAllocActive = true;
                  return true;
               } else {
                  this.memAlloc.clear();
                  this.memAlloc.putFloat(allocMemValue);
                  this.memAlloc.putFloat(textureAllocValue3);
                  this.memAlloc.putFloat(textureAllocValue4);
                  this.memAlloc.putFloat(textureAllocValue);
                  this.memAlloc.flip();
                  commandEncoder.writeToBuffer(this.buffer.slice(), this.memAlloc);

                  try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "corzclient:motion_blur", colorAttachmentView, OptionalInt.empty())) {
                     renderPass.setPipeline(register);
                     RenderSystem.bindDefaultUniforms(renderPass);
                     renderPass.bindTexture("HistorySampler", this.textureView, RenderSystem.getSamplerCache().get(FilterMode.NEAREST));
                     renderPass.setUniform("MotionBlurData", this.buffer);
                     renderPass.draw(0, 3);
                  }

                  commandEncoder.copyTextureToTexture(colorAttachment, this.texture, 0, 0, 0, 0, 0, textureWidth, textureHeight);
                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   private void updateTextureView() {
      if (this.textureView != null) {
         this.textureView.close();
         this.textureView = null;
      }

      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }

      this.textureWidth = 0;
      this.textureHeight = 0;
      this.textureAllocActive = false;
   }

   public void updateMemAlloc() {
      this.updateTextureView();
      if (this.buffer != null) {
         this.buffer.close();
         this.buffer = null;
      }

      if (this.memAlloc != null) {
         MemoryUtil.memFree(this.memAlloc);
         this.memAlloc = null;
      }
   }

   public void updateFlag() {
      this.textureAllocActive = false;
   }

   private boolean isPrecompilePipeline() {
      if (this.booleanValue == null) {
         boolean booleanValue;
         try {
            booleanValue = RenderSystem.getDevice().precompilePipeline(register).isValid();
         } catch (Throwable e) {
            booleanValue = false;
         }

         this.booleanValue = booleanValue;
      }

      return this.booleanValue;
   }

   private void handleTextureDevice(int textureWidth, int textureHeight) {
      if (this.memAlloc == null) {
         this.memAlloc = MemoryUtil.memAlloc(16);
         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corzclient:motion_blur_uniform", 136, 16L);
      }

      if (textureWidth != this.textureWidth || textureHeight != this.textureHeight || this.texture == null) {
         this.updateTextureView();
         this.texture = RenderSystem.getDevice().createTexture(() -> "corzclient:motion_blur_history", 5, TextureFormat.RGBA8, textureWidth, textureHeight, 1, 1);
         this.textureView = RenderSystem.getDevice().createTextureView(this.texture);
         this.textureWidth = textureWidth;
         this.textureHeight = textureHeight;
         this.textureAllocActive = false;
      }
   }
}
