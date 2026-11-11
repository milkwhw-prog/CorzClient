package com.corz.client.world;

import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public final class ClampMath {
   private float clamp;
   private static final MinecraftClient clampMathMinecraftClient = MinecraftClient.getInstance();
   private final Random random = new Random();
   private boolean clampRandomActive;
   private float currentYaw;

   public static float getSensitivityMouse() {
      double sensitivityMouseFactor = clampMathMinecraftClient.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      return (float)(sensitivityMouseFactor * sensitivityMouseFactor * sensitivityMouseFactor * 8.0 * 0.15);
   }

   public void updateFlag() {
      this.clampRandomActive = false;
   }

   public float[] getClampRandom(float clampRandomValue, float value) {
      if (!this.clampRandomActive) {
         this.currentYaw = MathClampUtils.getYaw();
         this.clamp = MathClampUtils.getClamp();
         this.clampRandomActive = true;
      }

      float sensitivityMouse = getSensitivityMouse();
      float wrapDegrees = MathHelper.wrapDegrees(clampRandomValue - this.currentYaw);
      float deltaClamp = MathHelper.clamp(value, -90.0F, 90.0F) - this.clamp;
      float clampRandomTotal = wrapDegrees * (0.6F + this.random.nextFloat() * 0.3F);
      float planValuesItem = deltaClamp * (0.5F + this.random.nextFloat() * 0.3F);
      if (Math.abs(clampRandomTotal) > sensitivityMouse) {
         clampRandomTotal += (this.random.nextFloat() - 0.5F) * 2.0F * sensitivityMouse;
      }

      if (Math.abs(planValuesItem) > sensitivityMouse) {
         planValuesItem += (this.random.nextFloat() - 0.5F) * 2.0F * sensitivityMouse;
      }

      float clampRandomValue2 = Math.round(clampRandomTotal / sensitivityMouse) * sensitivityMouse;
      float clampRandomValue3 = Math.round(planValuesItem / sensitivityMouse) * sensitivityMouse;
      this.currentYaw += clampRandomValue2;
      this.clamp = MathHelper.clamp(this.clamp + clampRandomValue3, -90.0F, 90.0F);
      return new float[]{this.currentYaw, this.clamp};
   }
}
