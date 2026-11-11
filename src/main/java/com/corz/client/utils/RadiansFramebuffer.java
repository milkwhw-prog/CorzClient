package com.corz.client.utils;

import java.nio.ByteBuffer;
import org.joml.Matrix4f;

public final class RadiansFramebuffer {
   public boolean radiansFramebufferActive;
   public float timeUniform;
   public float radiansFramebufferAmount;
   public float radians;
   public float radiansAmount;
   public float framebufferHeight;
   public final Matrix4f radiansFramebufferMatrix4f = new Matrix4f();
   public float radiansRadiansFramebuffer;
   public float colorGreen;
   public float saturnSizeScale;
   public float timeFactor;
   public float colorRed;
   public float orbitSpeed;
   public float ringTiltAngle;
   public float glowIntensity;
   public float colorBlue;
   public boolean radiansActive;
   public float framebufferWidth;
   public float bloomAmount;

   public void handleAmount(int radiansFramebufferValue) {
      this.colorRed = (radiansFramebufferValue >> 16 & 0xFF) / 255.0F;
      this.colorGreen = (radiansFramebufferValue >> 8 & 0xFF) / 255.0F;
      this.colorBlue = (radiansFramebufferValue & 0xFF) / 255.0F;
   }

   public void handleAmountRadians(ByteBuffer byteBuffer) {
      byteBuffer.putFloat(0, this.framebufferWidth);
      byteBuffer.putFloat(4, this.framebufferHeight);
      byteBuffer.putFloat(8, this.radiansRadiansFramebuffer);
      byteBuffer.putFloat(12, this.radians);
      byteBuffer.putFloat(16, this.colorRed);
      byteBuffer.putFloat(20, this.colorGreen);
      byteBuffer.putFloat(24, this.colorBlue);
      byteBuffer.putFloat(28, this.timeFactor);
      byteBuffer.putFloat(32, this.bloomAmount);
      byteBuffer.putFloat(36, this.orbitSpeed);
      byteBuffer.putFloat(40, this.radiansFramebufferAmount);
      byteBuffer.putFloat(44, this.glowIntensity);
      byteBuffer.putFloat(48, this.timeUniform);
      byteBuffer.putFloat(52, this.saturnSizeScale);
      byteBuffer.putFloat(56, this.ringTiltAngle);
      byteBuffer.putFloat(60, this.radiansAmount);
      byteBuffer.putFloat(64, this.radiansActive ? 1.0F : 0.0F);
      byteBuffer.putFloat(68, this.radiansFramebufferActive && !this.radiansActive ? 1.0F : 0.0F);
      byteBuffer.putFloat(72, 0.0F);
      byteBuffer.putFloat(76, 0.0F);
      this.radiansFramebufferMatrix4f.get(80, byteBuffer);
   }
}
