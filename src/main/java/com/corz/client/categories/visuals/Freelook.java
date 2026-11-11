package com.corz.client.categories.visuals;

import net.minecraft.util.math.MathHelper;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;

public class Freelook extends Module {
   private float lastPitch;
   private float startClamp;
   private float clamp;
   private float endClamp;

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      float tickDelta = deltaAngle.getTickDelta();
      deltaAngle.setAngleDegrees(MathHelper.lerp(tickDelta, this.startClamp, this.endClamp));
      deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.lastPitch, this.clamp));
      deltaAngle.updateFlag();
   }

   public static boolean isFlag() {
      Freelook freelook = (Freelook)ModuleRegistry.getModule(Freelook.class);
      return freelook != null && freelook.isEnabled();
   }

   @Override
   public void onEnable() {
      if (mc.player != null) {
         this.endClamp = this.startClamp = mc.player.getYaw();
         this.clamp = this.lastPitch = mc.player.getPitch();
      }
   }

   public void handleClampAmount(double clampValue, double clampValue2) {
      this.startClamp = this.endClamp;
      this.lastPitch = this.clamp;
      this.endClamp += (float)(clampValue * 0.15);
      this.clamp = MathHelper.clamp(this.clamp + (float)(clampValue2 * 0.15), -90.0F, 90.0F);
   }

   public Freelook() {
      super("Freelook", IconsPngType.VISUAL);
   }
}
