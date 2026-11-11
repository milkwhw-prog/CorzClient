package com.corz.client.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import com.corz.client.utils.FireSampler;
import com.corz.client.utils.SamplerMask;
import com.corz.client.utils.TexturePost;
import com.corz.client.utils.TrailSampler;

public class FramebufferAttachment {
   private long nanoTimeSamplerKawase;
   private float effectStrength;
   private float postTextureAmount;
   private float framebufferAttachmentAmount;
   private boolean postTextureActive;
   private float trailThreshold;
   private final TexturePost texturePost;
   private FireSampler fireSampler;
   private static FramebufferAttachment framebufferAttachmentFramebu;
   private float trailSoftness;
   private float trailIntensity;
   private float trailBloom;
   private SamplerMask samplerMask;
   private float trailBlurRadius;
   private boolean samplerKawaseActive;
   private float trailSaturation;
   private float trailContrast;
   private KawaseGpu kawaseGpu;
   private final MinecraftClient instancePostTexture = MinecraftClient.getInstance();
   private TrailSampler trailSampler;

   public boolean canPostTexture(Color color) {
      if (this.postTextureActive && this.texturePost.isTexturePostActive()) {
         this.updateSamplerKawase();

         boolean postTexture;
         try {
            Framebuffer framebuffer = this.instancePostTexture.getFramebuffer();
            if (framebuffer == null || framebuffer.getColorAttachment() == null) {
               return false;
            }

            int textureWidth = this.texturePost.getTextureWidth();
            int textureHeight = this.texturePost.getTextureHeight();
            this.samplerMask
               .handleSamplerBind(
                  this.texturePost.getTextureGpuGpuTextureView(),
                  this.texturePost.getTextureView(),
                  this.texturePost.getTexturePostGpuTextureView(),
                  this.texturePost.getTextureViewTextureGpu(),
                  this.texturePost.getTextureViewTexturePost(),
                  textureWidth,
                  textureHeight
               );
            float seconds = (float)(System.nanoTime() - this.nanoTimeSamplerKawase) / 1.0E9F;
            float redScaled = color.getRed() / 255.0F;
            float greenScaled = color.getGreen() / 255.0F;
            float blueScaled = color.getBlue() / 255.0F;
            float alphaScaled = color.getAlpha() / 255.0F;
            float postTextureValue = 0.6F;
            float postTextureValue2 = 0.0F;
            float postTextureValue3 = 1.0F;
            GpuTextureView samplerTrail = this.trailSampler
               .getSamplerTrail(
                  this.texturePost.getTexturePostGpuTextureView(),
                  this.texturePost.getTextureGpuGpuTextureView(),
                  textureWidth,
                  textureHeight,
                  seconds,
                  this.framebufferAttachmentAmount,
                  redScaled,
                  greenScaled,
                  blueScaled,
                  alphaScaled,
                  this.trailThreshold,
                  this.effectStrength,
                  this.trailSaturation,
                  this.trailBloom,
                  this.trailIntensity,
                  this.postTextureAmount,
                  postTextureValue,
                  postTextureValue2,
                  postTextureValue3,
                  this.trailSoftness
               );
            if (samplerTrail != null) {
               int kawaseFinalBound = this.trailBlurRadius >= 2.15F ? 3 : (this.trailBlurRadius >= 1.05F ? 2 : 1);
               GpuTextureView kawaseFinal = this.kawaseGpu
                  .getKawaseFinal(this.trailSampler.getDummyTextures(), samplerTrail, textureWidth, textureHeight, kawaseFinalBound, Math.max(1.05F, this.trailBlurRadius * 1.22F));
               if (kawaseFinal == null) {
                  kawaseFinal = samplerTrail;
               }

               this.fireSampler
                  .handleSamplerBind(
                     framebuffer.getColorAttachmentView(),
                     this.texturePost.getTexturePostGpuTextureView(),
                     kawaseFinal,
                     this.texturePost.getTextureGpuGpuTextureView(),
                     textureWidth,
                     textureHeight,
                     seconds,
                     this.framebufferAttachmentAmount,
                     redScaled,
                     greenScaled,
                     blueScaled,
                     alphaScaled,
                     this.trailThreshold,
                     this.effectStrength,
                     this.trailSaturation,
                     this.trailContrast,
                     this.trailBlurRadius,
                     this.postTextureAmount,
                     postTextureValue
                  );
               return true;
            }

            postTexture = false;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         } finally {
            this.texturePost.updateFlag();
         }

         return postTexture;
      } else {
         return false;
      }
   }

   public void setAmountForFloat(float amount) {
      this.framebufferAttachmentAmount = amount;
   }

   public static FramebufferAttachment getInstance() {
      if (framebufferAttachmentFramebu == null) {
         framebufferAttachmentFramebu = new FramebufferAttachment();
      }

      return framebufferAttachmentFramebu;
   }

   public void updatePostTexture() {
      if (this.postTextureActive) {
         this.updateSamplerKawase();
         this.texturePost.isTextureAttachment();
      }
   }

   public void updateFlag() {
      this.updateSamplerKawaseFireSampler();
      this.samplerKawaseActive = false;
   }

   public void updatePostTextureTexturePost() {
      this.updateSamplerKawaseFireSampler();
      this.texturePost.updateTextureWidth();
      this.samplerKawaseActive = false;
   }

   public void setAmountTrailBlurRadius(float amount) {
      this.trailBlurRadius = amount;
   }

   public void updatePostTexturePostTextureActive() {
      if (this.postTextureActive) {
         this.updateSamplerKawase();
         this.texturePost.isTextureAttachmentTextureAttachmentActive();
      }
   }

   private void updateSamplerKawase() {
      if (!this.samplerKawaseActive) {
         this.updateSamplerKawaseFireSampler();
         this.fireSampler = new FireSampler();
         this.trailSampler = new TrailSampler();
         this.kawaseGpu = new KawaseGpu();
         this.samplerMask = new SamplerMask();
         this.nanoTimeSamplerKawase = System.nanoTime();
         this.samplerKawaseActive = true;
      }
   }

   public void setAmountTrailSoftness(float amount) {
      this.trailSoftness = amount;
   }

   public void setAmount(float amount) {
      this.effectStrength = amount;
   }

   public void setAmountPostTextureAmount(float amount) {
      this.postTextureAmount = amount;
   }

   public void setAmountTrailSaturation(float amount) {
      this.trailSaturation = amount;
   }

   public void setAmountTrailContrast(float amount) {
      this.trailContrast = amount;
   }

   public boolean isFlag() {
      return this.postTextureActive;
   }

   public void setAmountTrailIntensity(float amount) {
      this.trailIntensity = amount;
   }

   public void setAmountTrailThreshold(float amount) {
      this.trailThreshold = amount;
   }

   private void updateSamplerKawaseFireSampler() {
      if (this.fireSampler != null) {
         this.fireSampler.updateBufferMem();
         this.fireSampler = null;
      }

      if (this.trailSampler != null) {
         this.trailSampler.updateBufferMem();
         this.trailSampler = null;
      }

      if (this.kawaseGpu != null) {
         this.kawaseGpu.updateBufferMem();
         this.kawaseGpu = null;
      }

      if (this.samplerMask != null) {
         this.samplerMask.updateBufferMem();
         this.samplerMask = null;
      }
   }

   public void setAmountTrailBloom(float amount) {
      this.trailBloom = amount;
   }

   public FramebufferAttachment() {
      this.texturePost = new TexturePost();
      this.samplerKawaseActive = false;
      this.postTextureActive = false;
      this.framebufferAttachmentAmount = 1.0F;
      this.trailThreshold = 0.35F;
      this.effectStrength = 1.0F;
      this.trailSaturation = 1.0F;
      this.trailIntensity = 1.55F;
      this.trailBloom = 1.35F;
      this.trailSoftness = 0.84F;
      this.trailBlurRadius = 1.45F;
      this.trailContrast = 1.25F;
      this.postTextureAmount = 0.55F;
      this.nanoTimeSamplerKawase = System.nanoTime();
   }

   public void handleTrailSampler(boolean flag) {
      this.postTextureActive = flag;
      if (flag) {
         this.updateSamplerKawase();
      } else {
         this.texturePost.updateFlag();
         if (this.trailSampler != null) {
            this.trailSampler.updateViewsTrail();
         }
      }
   }
}
