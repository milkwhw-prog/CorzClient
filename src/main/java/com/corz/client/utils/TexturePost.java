package com.corz.client.utils;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public final class TexturePost {
   private GpuTexture gpuTexture;
   private int textureHeight;
   private GpuTexture deviceCreate;
   private GpuTextureView textureView;
   private GpuTexture deviceRgba;
   private GpuTextureView textureViewTextureGpu;
   private final GpuTextureView[] gpuTextureViews;
   private boolean textureAttachmentActive;
   private GpuTexture deviceRgbaTexturePost;
   private boolean texturePostActive;
   private final GpuTexture[] postMaskGpuTextures;
   private final MinecraftClient instanceTextureAttachment = MinecraftClient.getInstance();
   private GpuTextureView textureViewTexturePost;
   private int textureWidth;
   private GpuTextureView textureGpuGpuTextureView;
   private GpuTexture deviceRgbaTextureGpu;
   private GpuTextureView texturePostGpuTextureView;

   private static GpuTexture getDeviceCreate(String deviceCreateText, int deviceCreateValue, int deviceCreateValue2) {
      return RenderSystem.getDevice().createTexture(() -> deviceCreateText, 13, TextureFormat.DEPTH32, deviceCreateValue, deviceCreateValue2, 1, 1);
   }

   private void handleTexturePost(int textureWidth, int textureHeight) {
      if (textureWidth != this.textureWidth || textureHeight != this.textureHeight || this.deviceRgbaTextureGpu == null) {
         this.updateTextureGpu();
         this.deviceRgbaTextureGpu = getDeviceRgba("corzclient:post_scene_before", textureWidth, textureHeight);
         this.textureView = RenderSystem.getDevice().createTextureView(this.deviceRgbaTextureGpu);
         this.deviceRgba = getDeviceRgba("corzclient:post_scene_after", textureWidth, textureHeight);
         this.texturePostGpuTextureView = RenderSystem.getDevice().createTextureView(this.deviceRgba);
         this.deviceCreate = getDeviceCreate("corzclient:post_depth_before", textureWidth, textureHeight);
         this.textureViewTextureGpu = RenderSystem.getDevice().createTextureView(this.deviceCreate);
         this.gpuTexture = getDeviceCreate("corzclient:post_depth_after", textureWidth, textureHeight);
         this.textureViewTexturePost = RenderSystem.getDevice().createTextureView(this.gpuTexture);
         this.deviceRgbaTexturePost = getDeviceRgba("corzclient:post_mask", textureWidth, textureHeight);
         this.textureGpuGpuTextureView = RenderSystem.getDevice().createTextureView(this.deviceRgbaTexturePost);

         for (int i = 0; i < 4; i++) {
            this.postMaskGpuTextures[i] = getDeviceRgba("corzclient:post_pingpong_" + i, textureWidth, textureHeight);
            this.gpuTextureViews[i] = RenderSystem.getDevice().createTextureView(this.postMaskGpuTextures[i]);
         }

         this.textureWidth = textureWidth;
         this.textureHeight = textureHeight;
      }
   }

   public GpuTextureView getTextureView() {
      return this.textureView;
   }

   private void updateTextureGpu() {
      this.textureView = getClose(this.textureView);
      this.deviceRgbaTextureGpu = getClose(this.deviceRgbaTextureGpu);
      this.texturePostGpuTextureView = getClose(this.texturePostGpuTextureView);
      this.deviceRgba = getClose(this.deviceRgba);
      this.textureViewTextureGpu = getClose(this.textureViewTextureGpu);
      this.deviceCreate = getClose(this.deviceCreate);
      this.textureViewTexturePost = getClose(this.textureViewTexturePost);
      this.gpuTexture = getClose(this.gpuTexture);
      this.textureGpuGpuTextureView = getClose(this.textureGpuGpuTextureView);
      this.deviceRgbaTexturePost = getClose(this.deviceRgbaTexturePost);

      for (int i = 0; i < 4; i++) {
         this.gpuTextureViews[i] = getClose(this.gpuTextureViews[i]);
         this.postMaskGpuTextures[i] = getClose(this.postMaskGpuTextures[i]);
      }
   }

   public boolean isTexturePostActive() {
      return this.texturePostActive;
   }

   public GpuTextureView getTextureGpuGpuTextureView() {
      return this.textureGpuGpuTextureView;
   }

   public void updateFlag() {
      this.textureAttachmentActive = false;
      this.texturePostActive = false;
   }

   public GpuTextureView getTexturePostGpuTextureView() {
      return this.texturePostGpuTextureView;
   }

   public boolean isTextureAttachment() {
      if (this.textureAttachmentActive) {
         return true;
      } else {
         Framebuffer framebuffer = this.instanceTextureAttachment.getFramebuffer();
         if (framebuffer != null && framebuffer.getColorAttachment() != null) {
            this.handleTexturePost(framebuffer.textureWidth, framebuffer.textureHeight);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            commandEncoder.copyTextureToTexture(framebuffer.getColorAttachment(), this.deviceRgbaTextureGpu, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
            if (framebuffer.getDepthAttachment() != null) {
               commandEncoder.copyTextureToTexture(framebuffer.getDepthAttachment(), this.deviceCreate, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
            }

            this.textureAttachmentActive = true;
            this.texturePostActive = false;
            return true;
         } else {
            return false;
         }
      }
   }

   public TexturePost() {
      this.postMaskGpuTextures = new GpuTexture[4];
      this.gpuTextureViews = new GpuTextureView[4];
      this.textureWidth = 0;
      this.textureHeight = 0;
      this.textureAttachmentActive = false;
      this.texturePostActive = false;
   }

   private static GpuTexture getDeviceRgba(String deviceRgbaText, int deviceRgbaValue, int deviceRgbaValue2) {
      return RenderSystem.getDevice().createTexture(() -> deviceRgbaText, 13, TextureFormat.RGBA8, deviceRgbaValue, deviceRgbaValue2, 1, 1);
   }

   private static GpuTexture getClose(GpuTexture gpuTexture) {
      if (gpuTexture != null) {
         gpuTexture.close();
      }

      return null;
   }

   public void updateTextureWidth() {
      this.updateTextureGpu();
      this.textureWidth = 0;
      this.textureHeight = 0;
   }

   public int getTextureWidth() {
      return this.textureWidth;
   }

   public int getTextureHeight() {
      return this.textureHeight;
   }

   private static GpuTextureView getClose(GpuTextureView gpuTextureView) {
      if (gpuTextureView != null) {
         gpuTextureView.close();
      }

      return null;
   }

   public GpuTextureView getTextureViewTexturePost() {
      return this.textureViewTexturePost;
   }

   public GpuTextureView getViewsGpu(int index) {
      return this.gpuTextureViews[index];
   }

   public boolean isTextureAttachmentTextureAttachmentActive() {
      if (!this.textureAttachmentActive) {
         return false;
      } else if (this.texturePostActive) {
         return true;
      } else {
         Framebuffer framebuffer = this.instanceTextureAttachment.getFramebuffer();
         if (framebuffer != null && framebuffer.getColorAttachment() != null) {
            this.handleTexturePost(framebuffer.textureWidth, framebuffer.textureHeight);
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            commandEncoder.copyTextureToTexture(framebuffer.getColorAttachment(), this.deviceRgba, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
            if (framebuffer.getDepthAttachment() != null) {
               commandEncoder.copyTextureToTexture(framebuffer.getDepthAttachment(), this.gpuTexture, 0, 0, 0, 0, 0, this.textureWidth, this.textureHeight);
            }

            this.texturePostActive = true;
            return true;
         } else {
            return false;
         }
      }
   }

   public GpuTextureView getTextureViewTextureGpu() {
      return this.textureViewTextureGpu;
   }
}
