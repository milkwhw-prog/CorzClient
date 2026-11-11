package com.corz.client.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.joml.Vector3f;
import com.corz.client.utils.AmountConstStrain;

final class RadiansVectorModel extends PlayerEntityModel {
   final Vector3f armRadiansVector3f;
   boolean entityActive;
   float armPitchScale = 1.0F;
   AmountConstStrain entityAmountConstStrain;
   final Vector3f handTargetPos;
   static final float radiansArmRadians = (float)Math.toRadians(20.0);
   static final float radians = (float)Math.toRadians(12.0);

   RadiansVectorModel(ModelPart modelPart, boolean radiansVectorFlag) {
      super(modelPart, radiansVectorFlag);
      this.armRadiansVector3f = new Vector3f();
      this.handTargetPos = new Vector3f();
   }

   private static float getAmount(float radiansVectorValue, float amountForFloat, float radiansVectorFactor) {
      return radiansVectorValue + (amountForFloat - radiansVectorValue) * radiansVectorFactor;
   }

   private void handleArmRadians(AmountConstStrain amountConstStrain) {
      float pitch = this.rightArm.pitch;
      float yaw = this.rightArm.yaw;
      float roll = this.rightArm.roll;
      float armRadiansFactor = this.armRadiansVector3f.x - this.rightArm.originX;
      float armRadiansFactor2 = this.armRadiansVector3f.y + 5.0F - this.rightArm.originY;
      float armRadiansFactor3 = this.armRadiansVector3f.z - this.rightArm.originZ;
      float distance = (float)Math.sqrt(armRadiansFactor * armRadiansFactor + armRadiansFactor2 * armRadiansFactor2 + armRadiansFactor3 * armRadiansFactor3);
      float armRadiansFactor4 = amountConstStrain.leanAmount + 0.5F * amountConstStrain.armRadiansAmount;
      float armRadiansValue = distance > 1.0E-4F ? -((float)Math.acos(getAmountForFloat(armRadiansFactor2 / distance, -1.0F, 1.0F))) : pitch;
      float armRadiansValue2 = distance > 1.0E-4F ? (float)Math.atan2(-armRadiansFactor, -armRadiansFactor3) : yaw;
      float armRadiansValue3 = armRadiansValue + (0.3F * armRadiansFactor4 + 0.12F * amountConstStrain.windupAmount + amountConstStrain.shakePitch);
      float armRadiansValue4 = armRadiansValue2 + (-0.4F * armRadiansFactor4 + amountConstStrain.shakeYaw);
      float armRadiansValue5 = getAmount(pitch + 0.5F, armRadiansValue3, amountConstStrain.armRaiseBlend);
      float armRadiansValue6 = getAmount(yaw, armRadiansValue4, amountConstStrain.armRaiseBlend);
      float radiansVectorValue = getAmount(roll - 0.12F, 0.0F, amountConstStrain.armRaiseBlend);
      this.rightArm.pitch = getAmount(armRadiansValue5, -1.45F, amountConstStrain.rightArmBlend);
      this.rightArm.yaw = getAmount(armRadiansValue6, -0.08F, amountConstStrain.rightArmBlend);
      this.rightArm.roll = getAmount(radiansVectorValue, 0.0F, amountConstStrain.rightArmBlend);
      float armRadiansFactor5 = this.handTargetPos.x - this.head.originX;
      float y = this.handTargetPos.y - this.head.originY - -4.0F;
      float armRadiansFactor6 = this.handTargetPos.z - this.head.originZ;
      float amountForFloat = getAmountForFloat((float)Math.atan2(-armRadiansFactor5, -armRadiansFactor6), -radiansArmRadians, radiansArmRadians);
      float amountForFloat9 = getAmountForFloat((float)Math.atan2(y, Math.sqrt(armRadiansFactor5 * armRadiansFactor5 + armRadiansFactor6 * armRadiansFactor6)), -radians, radians);
      this.head.yaw = getAmount(this.head.yaw, amountForFloat, amountConstStrain.headBlend);
      this.head.pitch = getAmountForFloat(getAmount(this.head.pitch, amountForFloat9, amountConstStrain.headBlend) + 0.12F * amountConstStrain.windupAmount, -radians, radians);
      this.head.roll = this.head.roll - (0.12F * amountConstStrain.headBlend + 0.06F * amountConstStrain.windupAmount) * (amountForFloat / radiansArmRadians);
   }

   static float getAmount(float radiansVectorValue) {
      float amountFloat = MatrixRotateUtilsHandle.getAmountFloat(radiansVectorValue);
      return 0.35F + 0.65F * amountFloat;
   }

   private static float getAmountForFloat(float radiansVectorLimit, float radiansVectorLimit2, float radiansVectorValue) {
      return radiansVectorLimit < radiansVectorLimit2 ? radiansVectorLimit2 : (radiansVectorLimit > radiansVectorValue ? radiansVectorValue : radiansVectorLimit);
   }

   @Override
   public void setAngles(PlayerEntityRenderState angles) {
      super.setAngles(angles);
      this.rightLeg.yaw = 0.07F;
      this.leftLeg.yaw = -0.07F;
      if (!this.entityActive) {
         this.rightArm.pitch = this.rightArm.pitch - 0.32F * this.armPitchScale;
         this.rightArm.roll += 0.22F;
         this.leftArm.pitch += 0.06F;
         if (this.entityAmountConstStrain != null) {
            this.handleArmRadians(this.entityAmountConstStrain);
         }
      }
   }
}
