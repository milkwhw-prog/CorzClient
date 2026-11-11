package com.corz.client.utils;
import com.corz.client.utils.TierBuilderUtils;

public final class AimDeltaSmoother {
   private float yawOffset;
   private float pitchOffset;

   public float[] computeSmoothedAim(float builderValue, float builderValue2, float yawModuleValue, float pitchLimit, float builderBound, float builderValue5, float builderValue6) {
      builderBound = Math.max(1.0F, Math.min(30.0F, builderBound));
      float min7 = Math.min(8.0F, builderBound / 3.0F);
      this.yawOffset = this.slewOffset(TierBuilderUtils.getAmount(builderValue, yawModuleValue + builderValue5), this.yawOffset, builderBound, min7);
      this.pitchOffset = this.slewOffset(pitchLimit + builderValue6 - builderValue2, this.pitchOffset, builderBound, min7);
      return new float[]{builderValue + this.yawOffset, Math.max(-90.0F, Math.min(90.0F, builderValue2 + this.pitchOffset))};
   }

   public void resetOffsets() {
      this.yawOffset = this.pitchOffset = 0.0F;
   }

   private float slewOffset(float builderValue, float builderValue2, float builderBound, float builderValue3) {
      float hashHuntBound = Math.max(-builderBound, Math.min(builderBound, builderValue * 0.65F));
      return Math.max(builderValue2 - builderValue3, Math.min(builderValue2 + builderValue3, hashHuntBound));
   }
}
