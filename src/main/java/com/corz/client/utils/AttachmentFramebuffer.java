package com.corz.client.utils;

import com.corz.client.render.KawaseGpu;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import com.corz.client.utils.FireSampler;
import com.corz.client.utils.TexturePost;
import com.corz.client.utils.OutlineSampler;
import com.corz.client.utils.TrailSampler;
import com.corz.client.utils.SamplerMask;

public class AttachmentFramebuffer {
   private float postIntensity;
   private KawaseGpu kawaseGpu;
   private FireSampler fireSampler;
   private final TexturePost texturePost;
   private float postAmount;
   private float attachmentFramebufferAmount;
   private float fireAlpha;
   private float fireBrightness;
   private float fireThreshold;
   private float fireSaturation;
   private final MinecraftClient instancePost = MinecraftClient.getInstance();
   private float fireContrast;
   private float trailBlurRadius;
   private float trailGlowStrength;
   private float outlineColorGreen;
   private float outlineColorRed;
   private final float outlineAlpha;
   private float trailSamplerAmount;
   private float glowColorBlue;
   private boolean samplerKawaseActive;
   private float glowColorGreen;
   private OutlineSampler outlineSampler;
   private int postMode;
   private float trailIntensity;
   private static AttachmentFramebuffer attachmentFramebufferAttachm;
   private boolean trailSamplerActive;
   private float trailWidth;
   private float glowColorRed;
   private TrailSampler trailSampler;
   private float trailDecay;
   private boolean postActive;
   private float glowFalloff;
   private float trailBrightness;
   private float intensityDecay;
   private long nanoTimeSamplerKawase;
   private boolean attachmentFramebufferActive;
   private SamplerMask samplerMask;
   private float outlineColorBlue;

   public AttachmentFramebuffer() {
      this.texturePost = new TexturePost();
      this.samplerKawaseActive = false;
      this.trailSamplerActive = false;
      this.postActive = true;
      this.trailGlowStrength = 1.0F;
      this.glowFalloff = 8.0F;
      this.fireSaturation = 1.0F;
      this.fireBrightness = 1.0F;
      this.fireContrast = 1.55F;
      this.trailBrightness = 1.35F;
      this.trailDecay = 0.84F;
      this.trailBlurRadius = 1.45F;
      this.postAmount = 1.25F;
      this.intensityDecay = 0.68F;
      this.fireThreshold = 0.55F;
      this.postIntensity = 0.6F;
      this.outlineColorRed = 0.2F;
      this.outlineColorGreen = 1.0F;
      this.outlineColorBlue = 1.0F;
      this.trailIntensity = 0.0F;
      this.trailSamplerAmount = 0.0F;
      this.outlineAlpha = 1.0F;
      this.attachmentFramebufferActive = false;
      this.trailWidth = 1.5F;
      this.fireAlpha = 1.0F;
      this.postMode = 0;
      this.glowColorRed = 1.0F;
      this.glowColorGreen = 1.0F;
      this.glowColorBlue = 1.0F;
      this.attachmentFramebufferAmount = 1.0F;
      this.nanoTimeSamplerKawase = System.nanoTime();
   }

   public void setAmountForFloat(float amount) {
      this.trailBlurRadius = amount;
   }

   public void updatePostTexture() {
      this.updateSamplerKawase();
      this.texturePost.updateTextureWidth();
      this.samplerKawaseActive = false;
   }

   public void setAmountTrailDecay(float amount) {
      this.trailDecay = amount;
   }

   public void setAmountTrailGlowStrength(float amount) {
      this.trailGlowStrength = amount;
   }

   public void updatePostTextureTrailSamplerActive() {
      if (this.trailSamplerActive) {
         this.updateSamplerKawaseSamplerKawaseActive();
         this.texturePost.isTextureAttachment();
      }
   }

   public void setAmountFireBrightness(float amount) {
      this.fireBrightness = amount;
   }

   public void setAmountGlowFalloff(float amount) {
      this.glowFalloff = amount;
   }

   public void setAmountFireAlpha(float amount) {
      this.fireAlpha = amount;
   }

   public void handleAmount(float amount, float amount2, float amount3, float amount4) {
      this.postIntensity = amount;
      this.outlineColorRed = amount2;
      this.outlineColorGreen = amount3;
      this.outlineColorBlue = amount4;
   }

   public void setAmountPostAmount(float amount) {
      this.postAmount = amount;
   }

   public void setAmountIntensityDecay(float amount) {
      this.intensityDecay = amount;
   }

   public static AttachmentFramebuffer getInstance() {
      if (attachmentFramebufferAttachm == null) {
         attachmentFramebufferAttachm = new AttachmentFramebuffer();
      }

      return attachmentFramebufferAttachm;
   }

   public void updatePostTexture3() {
      if (this.trailSamplerActive) {
         this.updateSamplerKawaseSamplerKawaseActive();
         this.texturePost.isTextureAttachmentTextureAttachmentActive();
      }
   }

   public void setAmountFireContrast(float amount) {
      this.fireContrast = amount;
   }

   public void handleAmount(float amount) {
      this.fireContrast = amount;
      this.trailBlurRadius = amount;
   }

   public void setAmountFireSaturation(float amount) {
      this.fireSaturation = amount;
   }

   public boolean isAmountPost() {
      if (this.trailSamplerActive && this.texturePost.isTexturePostActive()) {
         this.updateSamplerKawaseSamplerKawaseActive();

         boolean amountPost;
         try {
            Framebuffer framebuffer = this.instancePost.getFramebuffer();
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
            if (this.postActive) {
               float postBound = 0.0F;
               if (this.instancePost.player != null && this.instancePost.player.handSwinging) {
                  postBound = 1.0F - Math.min(1.0F, this.instancePost.player.handSwingProgress);
               }

               float postValue = Math.max(this.trailIntensity, postBound);
               float trailSamplerAmount14 = this.trailSamplerAmount;
               this.trailIntensity = this.trailIntensity * this.intensityDecay;
               this.trailSamplerAmount = this.trailSamplerAmount * this.trailDecay;
               if (this.trailIntensity < 0.01F) {
                  this.trailIntensity = 0.0F;
               }

               if (this.trailSamplerAmount < 0.01F) {
                  this.trailSamplerAmount = 0.0F;
               }

               GpuTextureView samplerTrail = this.trailSampler
                  .getSamplerTrail(
                     this.texturePost.getTexturePostGpuTextureView(),
                     this.texturePost.getTextureGpuGpuTextureView(),
                     textureWidth,
                     textureHeight,
                     seconds,
                     this.trailGlowStrength,
                     this.postIntensity,
                     this.outlineColorRed,
                     this.outlineColorGreen,
                     this.outlineColorBlue,
                     this.glowFalloff,
                     this.fireSaturation,
                     this.fireBrightness,
                     this.trailBrightness,
                     this.fireContrast,
                     this.fireThreshold,
                     postValue,
                     trailSamplerAmount14,
                     this.outlineAlpha,
                     this.trailDecay
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
                        this.trailGlowStrength,
                        this.postIntensity,
                        this.outlineColorRed,
                        this.outlineColorGreen,
                        this.outlineColorBlue,
                        this.glowFalloff,
                        this.fireSaturation,
                        this.fireBrightness,
                        this.postAmount,
                        this.trailBlurRadius,
                        this.fireThreshold,
                        postValue
                     );
               }
            }

            if (this.attachmentFramebufferActive) {
               this.outlineSampler
                  .handleSamplerDynamic(
                     framebuffer.getColorAttachmentView(),
                     this.texturePost.getTexturePostGpuTextureView(),
                     this.texturePost.getTextureGpuGpuTextureView(),
                     textureWidth,
                     textureHeight,
                     seconds,
                     this.trailWidth,
                     this.fireAlpha,
                     this.postMode,
                     this.glowColorRed,
                     this.glowColorGreen,
                     this.glowColorBlue,
                     this.attachmentFramebufferAmount
                  );
            }

            amountPost = true;
         } finally {
            this.texturePost.updateFlag();
         }

         return amountPost;
      } else {
         return false;
      }
   }

   private void updateSamplerKawase() {
      if (this.fireSampler != null) {
         this.fireSampler.updateBufferMem();
         this.fireSampler = null;
      }

      if (this.outlineSampler != null) {
         this.outlineSampler.updateBufferMem();
         this.outlineSampler = null;
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

   public void setValue(int value) {
      this.postMode = value;
   }

   public void setAmountTrailBrightness(float amount) {
      this.trailBrightness = amount;
   }

   public void handleTrailSampler(boolean flag) {
      this.trailSamplerActive = flag;
      if (flag) {
         this.updateSamplerKawaseSamplerKawaseActive();
      } else {
         this.trailIntensity = 0.0F;
         this.trailSamplerAmount = 0.0F;
         this.texturePost.updateFlag();
         if (this.trailSampler != null) {
            this.trailSampler.updateViewsTrail();
         }
      }
   }

   public void setAmountTrailWidth(float amount) {
      this.trailWidth = amount;
   }

   public boolean isTrailSamplerActive() {
      return this.trailSamplerActive;
   }

   public void setAmountFireThreshold(float amount) {
      this.fireThreshold = amount;
   }

   private void updateSamplerKawaseSamplerKawaseActive() {
      if (!this.samplerKawaseActive) {
         this.updateSamplerKawase();
         this.fireSampler = new FireSampler();
         this.outlineSampler = new OutlineSampler();
         this.trailSampler = new TrailSampler();
         this.kawaseGpu = new KawaseGpu();
         this.samplerMask = new SamplerMask();
         this.nanoTimeSamplerKawase = System.nanoTime();
         this.samplerKawaseActive = true;
      }
   }

   public void setFlagForFlag(boolean flag) {
      this.attachmentFramebufferActive = flag;
   }

   public void setFlagPostActive(boolean flag) {
      this.postActive = flag;
   }

   public void handleAmountForFloat(float amount, float amount2, float amount3, float amount4) {
      this.glowColorRed = amount;
      this.glowColorGreen = amount2;
      this.glowColorBlue = amount3;
      this.attachmentFramebufferAmount = amount4;
   }
}
