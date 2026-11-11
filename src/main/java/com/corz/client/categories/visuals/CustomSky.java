package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.render.Camera;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.RadiansFramebuffer;
import com.corz.client.utils.MemRender;
import com.corz.client.utils.VectorMatrixUtils;
import com.corz.client.categories.visuals.NameTags;
import com.corz.client.utils.FovOptionsUtils;
import com.corz.client.utils.IconsPngType;

public class CustomSky extends Module {
   private String customSkyText;
   private long customSkyValue;
   private final ColorSetting colorBase;
   private String previousSkyText;
   private final DoubleSetting doubleHelper;
   private final RadiansFramebuffer radiansFramebuffer;
   private final DoubleSetting intensityRadiansFramebuffer;
   private final DoubleSetting scaleRadiansFramebuffer;
   private final MemRender memRender;
   private final DoubleSetting ringTilt;
   private float fadeProgress;
   private final DoubleSetting speedRadiansFramebuffer;
   public static final String[] drainSkyTexts = new String[]{"Drain", "Nebula", "Plasma", "Bloom", "Saturn", "Northern Lights", "Black Hole"};
   private long lastNanoTime;
   private float easedFade;
   private final DoubleSetting saturnSize;
   private long skyStartNanoTime;
   private final EnumSetting enumHelper = new EnumSetting("Mode", "Sky shader style", "Drain", drainSkyTexts);
   private final DoubleSetting galaxyIntensity;

   public float getMaxTime() {
      long nanoTime = System.nanoTime();
      if (this.customSkyValue < 0L) {
         this.customSkyValue = nanoTime;
      }

      long customSkyValue = Math.max(0L, Math.min(nanoTime - this.customSkyValue, 250000000L));
      this.customSkyValue = nanoTime;
      boolean customSkyFlag = this.isEnabled() && mc.world != null && mc.player != null;
      float customSkyValue2 = (float)customSkyValue * 1.8181818E-9F;
      this.fadeProgress = customSkyFlag ? Math.min(1.0F, this.fadeProgress + customSkyValue2) : Math.max(0.0F, this.fadeProgress - customSkyValue2);
      return getAmount(this.fadeProgress);
   }

   public boolean canWorldPlayer(GpuTextureView gpuTextureView, int worldPlayerLimit) {
      if (mc.player != null && mc.world != null && gpuTextureView != null && worldPlayerLimit > 0) {
         long nanoTime = System.nanoTime();
         this.handleRadiansFramebuffer(nanoTime, worldPlayerLimit, worldPlayerLimit, true);
         this.handleText(gpuTextureView, nanoTime, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private float getTextTime(long nanoTime) {
      String text = this.enumHelper.getCurrentOption();
      if (this.previousSkyText == null) {
         this.previousSkyText = text;
      }

      if (!text.equals(this.previousSkyText)) {
         this.customSkyText = this.previousSkyText;
         this.previousSkyText = text;
         this.lastNanoTime = nanoTime;
      }

      if (this.customSkyText == null) {
         return 1.0F;
      } else {
         float customSkyLimit = (float)(nanoTime - this.lastNanoTime) / 6.5E8F;
         if (customSkyLimit >= 1.0F) {
            this.customSkyText = null;
            return 1.0F;
         } else {
            return getAmount(Math.max(0.0F, customSkyLimit));
         }
      }
   }

   @Override
   public void onEnable() {
      this.customSkyValue = System.nanoTime();
   }

   public void handleFramebufferWindow(float framebufferWindowFactor) {
      if (mc.player != null && mc.world != null && !(framebufferWindowFactor <= 0.0F)) {
         GpuTextureView colorAttachmentView = mc.getFramebuffer().getColorAttachmentView();
         if (colorAttachmentView != null) {
            long nanoTime = System.nanoTime();
            this.handleRadiansFramebuffer(nanoTime, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), false);
            this.handleText(colorAttachmentView, nanoTime, (float)this.doubleHelper.getValue() * framebufferWindowFactor);
         }
      }
   }

   private float getTime(long nanoTime) {
      if (this.skyStartNanoTime < 0L) {
         this.skyStartNanoTime = nanoTime;
      }

      return (float)((nanoTime - this.skyStartNanoTime) / 1.0E9 % 7200.0);
   }

   private void handleText(GpuTextureView gpuTextureView, long customSkyValue, float customSkyFactor) {
      float textTime = this.getTextTime(customSkyValue);
      if (this.customSkyText != null) {
         this.canBloomMem(gpuTextureView, this.customSkyText, customSkyFactor);
      }

      this.canBloomMem(gpuTextureView, this.previousSkyText, customSkyFactor * textTime);
   }

   public float getAmount() {
      this.easedFade = this.getMaxTime();
      return this.easedFade;
   }

   public void update() {
      this.handleFramebufferWindow(1.0F);
   }

   private boolean canBloomMem(GpuTextureView gpuTextureView, String bloomMemText, float bloomAmount) {
      RenderPipeline pipelineRender = MemRender.getPipelineRender(bloomMemText);
      if (!this.memRender.canPrecompilePipeline(pipelineRender)) {
         pipelineRender = MemRender.getPipelineRender("Bloom");
      }

      if (!this.memRender.canPrecompilePipeline(pipelineRender)) {
         return false;
      } else {
         this.radiansFramebuffer.bloomAmount = bloomAmount;

         try {
            this.memRender.handleMemAlloc(gpuTextureView, pipelineRender, this.radiansFramebuffer);
            return true;
         } catch (Throwable e) {
            this.memRender.handle(pipelineRender);
            return false;
         }
      }
   }

   private void handleRadiansFramebuffer(long radiansFramebufferValue, int framebufferWidth, int framebufferHeight, boolean flag) {
      Camera camera = mc.gameRenderer.getCamera();
      this.radiansFramebuffer.framebufferWidth = framebufferWidth;
      this.radiansFramebuffer.framebufferHeight = framebufferHeight;
      this.radiansFramebuffer.radiansRadiansFramebuffer = (float)Math.toRadians(-camera.getYaw());
      this.radiansFramebuffer.radians = (float)Math.toRadians(camera.getPitch());
      this.radiansFramebuffer.handleAmount(this.colorBase.getColor() & 16777215);
      this.radiansFramebuffer.timeFactor = this.getTime(radiansFramebufferValue);
      this.radiansFramebuffer.orbitSpeed = (float)this.speedRadiansFramebuffer.getValue();
      this.radiansFramebuffer.radiansFramebufferAmount = (float)this.scaleRadiansFramebuffer.getValue();
      this.radiansFramebuffer.glowIntensity = (float)this.intensityRadiansFramebuffer.getValue();
      this.radiansFramebuffer.timeUniform = FovOptionsUtils.getValue();
      this.radiansFramebuffer.saturnSizeScale = (float)this.saturnSize.getValue();
      this.radiansFramebuffer.ringTiltAngle = (float)this.ringTilt.getValue();
      this.radiansFramebuffer.radiansAmount = (float)this.galaxyIntensity.getValue();
      this.radiansFramebuffer.radiansActive = flag;
      this.radiansFramebuffer.radiansFramebufferActive = !flag && VectorMatrixUtils.canDeterminantTranspose(NameTags.MATRIX4F, camera.getRotation(), this.radiansFramebuffer.radiansFramebufferMatrix4f);
   }

   public float getEasedFade() {
      return this.easedFade;
   }

   public boolean isMax() {
      return this.isEnabled() || this.fadeProgress > 0.5F;
   }

   public CustomSky() {
      super("Custom Sky", IconsPngType.VISUAL);
      this.speedRadiansFramebuffer = new DoubleSetting("Speed", "Animation speed", 1.0, 0.1, 5.0, 0.1);
      this.scaleRadiansFramebuffer = new DoubleSetting("Scale", "Pattern scale. Northern Lights: curtain density. Black Hole: size.", 5.0, 1.0, 20.0, 0.5);
      this.intensityRadiansFramebuffer = new DoubleSetting("Intensity", "Effect intensity", 0.01, 0.001, 0.05, 0.001);
      this.doubleHelper = new DoubleSetting("Alpha", "Opacity", 1.0, 0.3, 1.0, 0.05);
      this.colorBase = new ColorSetting("Color", "Base sky colour", -16738561);
      this.saturnSize = new DoubleSetting("Saturn Size", "Saturn angular size (Saturn mode)", 1.0, 0.4, 2.5, 0.05);
      this.ringTilt = new DoubleSetting("Ring Tilt", "Ring opening: 0 = edge-on, 1 = wide open (Saturn mode)", 0.3, 0.0, 1.0, 0.05);
      this.galaxyIntensity = new DoubleSetting("Galaxy Intensity", "Deep-space background brightness (Saturn mode)", 1.0, 0.0, 2.0, 0.05);
      this.memRender = new MemRender();
      this.radiansFramebuffer = new RadiansFramebuffer();
      this.skyStartNanoTime = -1L;
      this.customSkyValue = -1L;
      this.saturnSize.withGroupName("Saturn");
      this.ringTilt.withGroupName("Saturn");
      this.galaxyIntensity.withGroupName("Saturn");
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper,
            this.speedRadiansFramebuffer,
            this.scaleRadiansFramebuffer,
            this.intensityRadiansFramebuffer,
            this.doubleHelper,
            this.colorBase,
            this.saturnSize,
            this.ringTilt,
            this.galaxyIntensity
         }
      );
   }

   public static CustomSky getCustomSky() {
      return (CustomSky)ModuleRegistry.getModule(CustomSky.class);
   }

   @Override
   public void onDisable() {
      this.customSkyValue = System.nanoTime();
   }

   private static float getAmount(float customSkyFactor) {
      return customSkyFactor * customSkyFactor * (3.0F - 2.0F * customSkyFactor);
   }
}
