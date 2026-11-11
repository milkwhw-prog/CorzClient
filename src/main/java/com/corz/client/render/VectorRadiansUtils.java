package com.corz.client.render;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.corz.client.utils.AmountConstStrain;

final class VectorRadiansUtils {
   static final float radians = (float)Math.toRadians(5.0);
   static final float radiansRadiansXyz = (float)Math.toRadians(2.5);

   static Vector3f getAmount(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, AmountConstStrain amountConstStrain) {
      float deltaX = vector3f2.x - vector3f3.x;
      float deltaY = vector3f2.y - vector3f3.y;
      float deltaZ = vector3f2.z - vector3f3.z;
      float distance = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      float vectorRadiansFactor = distance > 1.0E-6F ? amountConstStrain.windupSquared * 1.2F * 0.0625F / distance : 0.0F;
      return vector3f.set(
         vector3f2.x + deltaX * vectorRadiansFactor + amountConstStrain.jitterX * 0.0625F, vector3f2.y + deltaY * vectorRadiansFactor + (amountConstStrain.jitterY - amountConstStrain.swayY) * 0.0625F, vector3f2.z + deltaZ * vectorRadiansFactor + amountConstStrain.jitterZ * 0.0625F
      );
   }

   static Vector3f getRotate(Vector3f vector3f, float rotateValue) {
      return vector3f.set(-0.71875F, -0.8125F, -0.3125F).rotateY(rotateValue);
   }

   static Vector3f getPositionIdentity(RadiansVectorModel radiansVectorModel, PlayerEntityRenderState playerEntityRenderState, Quaternionf quaternionf, MatrixStack matrices, Vector3f vector3f) {
      matrices.loadIdentity();
      handleTranslateMultiply(matrices, quaternionf);
      radiansVectorModel.setArmAngle(playerEntityRenderState, Arm.RIGHT, matrices);
      matrices.translate(-0.0625F, 0.84375F, 0.0F);
      return matrices.peek().getPositionMatrix().transformPosition(vector3f.set(0.0F, 0.0F, 0.0F));
   }

   static float getAmount(float vectorRadiansValue, float vectorRadiansFactor) {
      return (0.5F + 0.3F * vectorRadiansValue) * vectorRadiansFactor;
   }

   static Vector3f getLerp(Vector3f lerp, Vector3f vector3f, Vector3f vector3f2, float lerpFactor, float lerpValue) {
      lerp.set(vector3f).lerp(vector3f2, lerpFactor);
      lerp.y = lerp.y - (0.09375F * (float)Math.sin(Math.PI * lerpFactor) + lerpValue * 0.0625F);
      return lerp;
   }

   static float getAmount(float vectorRadiansValue) {
      float vectorRadiansLimit = 30.0F;
      float vectorRadiansTotal = (vectorRadiansValue - -30.0F) % 360.0F;
      if (vectorRadiansTotal < 0.0F) {
         vectorRadiansTotal += 360.0F;
      }

      float vectorRadiansValue2;
      if (vectorRadiansTotal <= vectorRadiansLimit) {
         vectorRadiansValue2 = -30.0F + vectorRadiansTotal;
      } else if (vectorRadiansTotal <= vectorRadiansLimit + 45.0F) {
         vectorRadiansValue2 = 0.0F;
      } else if (vectorRadiansTotal < 315.0F) {
         vectorRadiansValue2 = 0.0F + -30.0F * (vectorRadiansTotal - vectorRadiansLimit - 45.0F) / (360.0F - vectorRadiansLimit - 90.0F);
      } else {
         vectorRadiansValue2 = -30.0F;
      }

      return vectorRadiansValue2 - -30.0F - vectorRadiansTotal;
   }

   static void handle(Vector3f vector3f, Vector3f vector3f2, Vector3f vector3f3, float vectorRadiansFactor, float vectorRadiansFactor2) {
      float exp = (float)Math.exp(-vectorRadiansFactor * vectorRadiansFactor2);
      float vectorRadiansFactor3 = (vector3f2.x + vectorRadiansFactor * (vector3f.x - vector3f3.x)) * vectorRadiansFactor2;
      float vectorRadiansFactor4 = (vector3f2.y + vectorRadiansFactor * (vector3f.y - vector3f3.y)) * vectorRadiansFactor2;
      float vectorRadiansFactor5 = (vector3f2.z + vectorRadiansFactor * (vector3f.z - vector3f3.z)) * vectorRadiansFactor2;
      vector3f2.set((vector3f2.x - vectorRadiansFactor * vectorRadiansFactor3) * exp, (vector3f2.y - vectorRadiansFactor * vectorRadiansFactor4) * exp, (vector3f2.z - vectorRadiansFactor * vectorRadiansFactor5) * exp);
      vector3f.set(vector3f3.x + (vector3f.x - vector3f3.x + vectorRadiansFactor3) * exp, vector3f3.y + (vector3f.y - vector3f3.y + vectorRadiansFactor4) * exp, vector3f3.z + (vector3f.z - vector3f3.z + vectorRadiansFactor5) * exp);
   }

   static Quaternionf getRadiansXyz(Quaternionf quaternionf, float radiansXyzFactor) {
      return quaternionf.rotationXYZ(-radians * radiansXyzFactor, 0.0F, radiansRadiansXyz * radiansXyzFactor);
   }

   static Vector3f getInverseTransform(Vector3f vector3f, Vector3f vector3f2, Quaternionf quaternionf) {
      vector3f.set(vector3f2.x, vector3f2.y - 1.5F, vector3f2.z);
      quaternionf.transformInverse(vector3f);
      return vector3f.set(vector3f.x / 0.0625F, (vector3f.y + 1.5F) / 0.0625F, vector3f.z / 0.0625F);
   }

   static void handleTranslateMultiply(MatrixStack matrices, Quaternionf quaternionf) {
      matrices.translate(0.0F, 1.5F, 0.0F);
      matrices.multiply(quaternionf);
      matrices.translate(0.0F, -1.5F, 0.0F);
   }

   private VectorRadiansUtils() {
   }
}
