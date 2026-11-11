package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import net.minecraft.util.math.Vec3d;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class FOVSlider extends Module {
   private final BooleanSetting booleanBase;
   private double smoothedFov;
   private final DoubleSetting doubleHelper;
   private final DoubleSetting fovDoubleHelper = new DoubleSetting("FOV", "Target field of view", 95.0, 30.0, 140.0, 1.0);
   private final BooleanSetting noSprintZoom;
   private final BooleanSetting smooth = new BooleanSetting("Smooth", "Ease FOV changes in and out", true);
   private long lastNanoTime;

   private double getVelocityPlayer() {
      if (this.booleanBase.isEnabled() && mc.player != null) {
         Vec3d velocity = mc.player.getVelocity();
         double distance = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
         double velocityPlayerFactor = Math.min(1.0, distance / 0.2825);
         return this.doubleHelper.getValue() * velocityPlayerFactor;
      } else {
         return 0.0;
      }
   }

   public FOVSlider() {
      super("FOVSlider", IconsPngType.MISC);
      this.noSprintZoom = new BooleanSetting("No Sprint Zoom", "Cancel the vanilla sprint / speed FOV punch", true);
      this.booleanBase = new BooleanSetting("Speed FOV", "Widen the view with your movement speed", false);
      this.doubleHelper = new DoubleSetting("Speed Strength", "Extra degrees at full sprint", 12.0, 0.0, 30.0, 1.0);
      this.smoothedFov = -1.0;
      this.lastNanoTime = 0L;
      this.addSettings(new ConfigSetting[]{this.fovDoubleHelper, this.smooth, this.noSprintZoom, this.booleanBase, this.doubleHelper});
   }

   public float getFovTime(float fovTime) {
      int value = mc.options.getFov().getValue();
      if (value <= 0) {
         return fovTime;
      } else {
         double value2 = this.fovDoubleHelper.getValue() + this.getVelocityPlayer();
         long nanoTime = System.nanoTime();
         double fovBound = this.lastNanoTime == 0L ? 0.0 : (nanoTime - this.lastNanoTime) / 1.0E9;
         this.lastNanoTime = nanoTime;
         if (this.smoothedFov < 0.0) {
            this.smoothedFov = value;
         }

         if (!this.smooth.isEnabled()) {
            this.smoothedFov = value2;
         } else {
            double fovFactor = 1.0 - Math.exp(-12.0 * Math.max(0.0, fovBound));
            this.smoothedFov = this.smoothedFov + (value2 - this.smoothedFov) * fovFactor;
            if (Math.abs(this.smoothedFov - value2) < 0.05) {
               this.smoothedFov = value2;
            }
         }

         if (this.noSprintZoom.isEnabled()) {
            return (float)this.smoothedFov;
         } else {
            double fovFactor2 = (double)fovTime / value;
            return (float)(this.smoothedFov * fovFactor2);
         }
      }
   }
}
