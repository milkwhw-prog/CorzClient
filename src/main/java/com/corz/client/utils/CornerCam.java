package com.corz.client.utils;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.corz.client.utils.CornerCamUtils;

public final class CornerCam {
   static final float[] camParyAmounts = new float[]{6.6F, 2.9F, 12.4F};
   public float camCornerAmount;
   private float viewportWidth;
   public final Matrix4f camCornerMatrix4f = new Matrix4f();
   public float cornerCamAmount;
   private final Vector4f vectorTransformVector4f;
   public final Matrix4f viewMatrix = new Matrix4f();
   private float maxCamCornerBound;
   public float camParyAmount;
   public final Matrix3f camCornerMatrix3f = new Matrix3f();
   private final Matrix4f combinedMatrix = new Matrix4f();

   public void handleCamCorner(float camCornerBound, float rocketsElytraBound, float camCornerFactor, float camCornerFactor2, boolean camCornerFlag) {
      this.viewportWidth = Math.max(1.0F, camCornerBound);
      this.maxCamCornerBound = Math.max(1.0F, rocketsElytraBound);
      float camCornerValue = this.viewportWidth / this.maxCamCornerBound;
      String camCornerText = camCornerFlag ? "corner." : "cam.";
      String camCornerText2 = camCornerFlag ? "corner.l" : "look.";
      float camCornerFactor3 = CornerCamUtils.getAmount(camCornerText + "fov", 62.0F);
      this.camCornerAmount = CornerCamUtils.getAmount(camCornerText + "x", camCornerFlag ? camParyAmounts[0] : 0.0F) + camCornerFactor * CornerCamUtils.getAmount("cam.parx", 0.35F);
      this.camParyAmount = CornerCamUtils.getAmount(camCornerText + "y", camCornerFlag ? camParyAmounts[1] : 2.9F) + camCornerFactor2 * CornerCamUtils.getAmount("cam.pary", 0.18F);
      this.cornerCamAmount = CornerCamUtils.getAmount(camCornerText + "z", camCornerFlag ? camParyAmounts[2] : 12.4F);
      float camCornerValue2 = CornerCamUtils.getAmount(camCornerText2 + "x", camCornerFlag ? 5.4F : 0.0F) + camCornerFactor * 0.08F;
      float camCornerValue3 = CornerCamUtils.getAmount(camCornerText2 + "y", camCornerFlag ? 1.0F : 2.5F);
      float camCornerValue4 = CornerCamUtils.getAmount(camCornerText2 + "z", camCornerFlag ? 3.8F : 0.0F);
      this.camCornerMatrix4f.setPerspective(camCornerFactor3 * (float) (Math.PI / 180.0), camCornerValue, 0.1F, 80.0F);
      if (camCornerFlag) {
         this.camCornerMatrix4f.translateLocal(CornerCamUtils.getAmount("corner.sx", 0.677F), CornerCamUtils.getAmount("corner.sy", -0.333F), 0.0F);
      }

      this.viewMatrix.setLookAt(this.camCornerAmount, this.camParyAmount, this.cornerCamAmount, camCornerValue2, camCornerValue3, camCornerValue4, 0.0F, 1.0F, 0.0F);
      this.viewMatrix.get3x3(this.camCornerMatrix3f);
      this.camCornerMatrix4f.mul(this.viewMatrix, this.combinedMatrix);
   }

   public CornerCam() {
      this.vectorTransformVector4f = new Vector4f();
      this.viewportWidth = 1.0F;
      this.maxCamCornerBound = 1.0F;
   }

   float getMatrixMax(float matrixLimit) {
      return !(matrixLimit > 0.1F) ? 0.0F : this.maxCamCornerBound * this.camCornerMatrix4f.m11() / (2.0F * matrixLimit);
   }

   public boolean canVectorTransform(float vectorTransformValue, float vectorTransformValue2, float vectorTransformValue3, float[] vectorTransformValues) {
      this.vectorTransformVector4f.set(vectorTransformValue, vectorTransformValue2, vectorTransformValue3, 1.0F);
      this.combinedMatrix.transform(this.vectorTransformVector4f);
      if (this.vectorTransformVector4f.w <= 0.1F) {
         return false;
      } else {
         float vectorTransformValue4 = this.vectorTransformVector4f.x / this.vectorTransformVector4f.w;
         float vectorTransformValue5 = this.vectorTransformVector4f.y / this.vectorTransformVector4f.w;
         vectorTransformValues[0] = (vectorTransformValue4 * 0.5F + 0.5F) * this.viewportWidth;
         vectorTransformValues[1] = (0.5F - vectorTransformValue5 * 0.5F) * this.maxCamCornerBound;
         vectorTransformValues[2] = this.vectorTransformVector4f.w;
         return true;
      }
   }

   void handle(float cornerCamValue, float rocketsElytraBound, float camCornerFactor, float cornerCamValue4) {
      this.handleCamCorner(cornerCamValue, rocketsElytraBound, camCornerFactor, cornerCamValue4, false);
   }

   float getCamCorner(float camCornerValue, float camCornerValue2, float camCornerValue3) {
      float camCornerFactor = camCornerValue - this.camCornerAmount;
      float camCornerFactor2 = camCornerValue2 - this.camParyAmount;
      float camCornerFactor3 = camCornerValue3 - this.cornerCamAmount;
      return (float)Math.sqrt(camCornerFactor * camCornerFactor + camCornerFactor2 * camCornerFactor2 + camCornerFactor3 * camCornerFactor3);
   }
}
