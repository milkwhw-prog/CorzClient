package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.ClampMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import ai.auth.Module;
import com.corz.client.utils.MotionBlurTexture;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.IconsPngType;
public class MotionBlur extends Module {
   private double smoothedBlur;
   private static boolean motionBlurActive;
   private final IntegerSetting integerHelper;
   private final IntegerSetting strength = new IntegerSetting("Strength", "Blur strength", 30, 5, 100, 5) {
      @Override
      public String getHintLabel() {
         return "\u00b7 " + MotionBlur.formatCinematicSubtle(this.getValue());
      }
   };
   private boolean blurMotionActive;
   private static MotionBlur singleton;
   private final BooleanSetting booleanBase;
   private long lastNanoTime;
   private final BooleanSetting pinkTrails = new BooleanSetting("Pink Trails", "Tint the trails with your theme accent", true);
   private MotionBlurTexture blurPass;

   @Override
   protected void onDisable() {
      if (RenderSystem.isOnRenderThread()) {
         this.updateBlurMotionBlurPass();
      } else {
         this.updateBlurMotionLastNanoTime();
      }
   }

   static double getAmount(double motionBlurTotal, double motionBlurBound) {
      double motionBlurFactor = 1.0 - Math.exp(-12.0 * Math.max(0.0, motionBlurBound));
      motionBlurTotal += (1.0 - motionBlurTotal) * motionBlurFactor;
      return motionBlurTotal > 0.999 ? 1.0 : motionBlurTotal;
   }

   public static void updateFlagInstance() {
      boolean motionBlurFlag = motionBlurActive;
      motionBlurActive = false;
      MotionBlur singleton2 = singleton;
      if (singleton2 != null) {
         if (!singleton2.isEnabled()) {
            singleton2.updateBlurMotionBlurPass();
         } else if (motionBlurFlag && mc.world != null && !DirExtractionUtils.isFlag() && !ClampMatrixUtils.isFrameCamera()) {
            singleton2.updateBlurMotion();
         } else {
            singleton2.updateBlurMotionLastNanoTime();
         }
      }
   }

   static int getValue(int motionBlurValue, boolean motionBlurFlag, int motionBlurValue2) {
      float motionBlurFactor = motionBlurFlag ? (float)Math.clamp(motionBlurValue2 / 100.0, 0.0, 1.0) : 0.0F;
      if (motionBlurFactor <= 0.0F) {
         return -1;
      } else {
         int motionBlurValue3 = (int)(255.0F + ((motionBlurValue >> 16 & 0xFF) - 255) * motionBlurFactor);
         int motionBlurValue4 = (int)(255.0F + ((motionBlurValue >> 8 & 0xFF) - 255) * motionBlurFactor);
         int motionBlurValue5 = (int)(255.0F + ((motionBlurValue & 0xFF) - 255) * motionBlurFactor);
         return 0xFF000000 | motionBlurValue3 << 16 | motionBlurValue4 << 8 | motionBlurValue5;
      }
   }

   static String formatCinematicSubtle(int cinematicSubtleValue) {
      if (cinematicSubtleValue <= 20) {
         return "Subtle";
      } else if (cinematicSubtleValue <= 45) {
         return "Balanced";
      } else if (cinematicSubtleValue <= 70) {
         return "Smooth";
      } else {
         return cinematicSubtleValue <= 90 ? "Heavy" : "Cinematic";
      }
   }

   @Override
   protected void onEnable() {
      this.blurMotionActive = false;
      this.updateBlurMotionLastNanoTime();
   }

   public static void updateFlag() {
      motionBlurActive = true;
   }

   static double getAmount(int motionBlurValue, boolean motionBlurFlag, double motionBlurLimit, double motionBlurFactor) {
      double clamp = getClamp(motionBlurValue);
      if (motionBlurFlag && motionBlurLimit > 0.0) {
         clamp = Math.pow(clamp, motionBlurLimit * 60.0);
      }

      return Math.clamp(clamp * motionBlurFactor, 0.0, 0.97);
   }

   private void updateBlurMotion() {
      if (!this.blurMotionActive) {
         long nanoTime = System.nanoTime();
         double blurMotionValue = this.lastNanoTime == 0L ? 0.0 : (nanoTime - this.lastNanoTime) / 1.0E9;
         this.lastNanoTime = nanoTime;
         this.smoothedBlur = getAmount(this.smoothedBlur, blurMotionValue);
         float textureAllocValue = (float)getAmount(this.strength.getValue(), this.booleanBase.isEnabled(), blurMotionValue, this.smoothedBlur);
         int blurMotionValue3 = getValue(MatrixEspUtils.releaseRightValue, this.pinkTrails.isEnabled(), this.integerHelper.getValue());
         if (this.blurPass == null) {
            this.blurPass = new MotionBlurTexture();
         }

         try {
            this.blurPass
               .canTextureAlloc(mc.getFramebuffer(), textureAllocValue, (blurMotionValue3 >> 16 & 0xFF) / 255.0F, (blurMotionValue3 >> 8 & 0xFF) / 255.0F, (blurMotionValue3 & 0xFF) / 255.0F);
         } catch (Throwable e) {
            this.blurMotionActive = true;
            this.blurPass.updateFlag();
         }
      }
   }

   private void updateBlurMotionLastNanoTime() {
      this.lastNanoTime = 0L;
      this.smoothedBlur = 0.0;
      if (this.blurPass != null) {
         this.blurPass.updateFlag();
      }
   }

   public MotionBlur() {
      super("Motion Blur", IconsPngType.VISUAL);
      this.integerHelper = new IntegerSetting("Tint", "How strongly trails take the theme color", 30, 0, 100, 5);
      this.booleanBase = new BooleanSetting("FPS Compensated", "Keep the blur consistent across framerates", true);
      this.addSettings(new ConfigSetting[]{this.strength, this.pinkTrails, this.integerHelper, this.booleanBase});
      singleton = this;
   }

   private void updateBlurMotionBlurPass() {
      this.updateBlurMotionLastNanoTime();
      if (this.blurPass != null) {
         this.blurPass.updateMemAlloc();
         this.blurPass = null;
      }
   }

   static double getClamp(int clampValue) {
      return Math.clamp(clampValue / 100.0, 0.05, 0.95);
   }
}
