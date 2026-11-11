package com.corz.client.utils;

public final class PoseProgress {
   public boolean usingItem;
   public float age;
   public boolean gliding;
   public int hashCode;
   public boolean vehicle;
   public float limbSwingStrength;
   public boolean shinProgressActive;
   public float animationProgress;
   public float leaningPitch;
   public boolean sneakingPose;
   public boolean inPose;
   public boolean swimming;
   public boolean poseProgressActive;
   public float swingProgress;
   public float headYawRadians;
   public float shinProgressAmount;

   public float getSin() {
      return this.swingProgress <= 0.0F ? 0.0F : (float)Math.sin(this.swingProgress * Math.PI);
   }

   public float getRadians(float radiansFactor) {
      return (float)Math.toRadians((this.age + (this.hashCode & 0xFF)) * radiansFactor % 360.0F);
   }

   public PoseProgress getPoseProgress(PoseProgress poseProgress) {
      this.age = poseProgress.age;
      this.animationProgress = poseProgress.animationProgress;
      this.limbSwingStrength = poseProgress.limbSwingStrength;
      this.headYawRadians = poseProgress.headYawRadians;
      this.shinProgressAmount = poseProgress.shinProgressAmount;
      this.swingProgress = poseProgress.swingProgress;
      this.leaningPitch = poseProgress.leaningPitch;
      this.sneakingPose = poseProgress.sneakingPose;
      this.gliding = poseProgress.gliding;
      this.swimming = poseProgress.swimming;
      this.vehicle = poseProgress.vehicle;
      this.inPose = poseProgress.inPose;
      this.usingItem = poseProgress.usingItem;
      this.shinProgressActive = poseProgress.shinProgressActive;
      this.poseProgressActive = poseProgress.poseProgressActive;
      this.hashCode = poseProgress.hashCode;
      return this;
   }

   public PoseProgress getPoseProgress() {
      this.age = 0.0F;
      this.animationProgress = 0.0F;
      this.limbSwingStrength = 0.0F;
      this.headYawRadians = 0.0F;
      this.shinProgressAmount = 0.0F;
      this.swingProgress = 0.0F;
      this.leaningPitch = 0.0F;
      this.sneakingPose = false;
      this.gliding = false;
      this.swimming = false;
      this.vehicle = false;
      this.inPose = false;
      this.usingItem = false;
      this.shinProgressActive = false;
      this.poseProgressActive = false;
      this.hashCode = 0;
      return this;
   }

   public float getSwingProgress() {
      return this.swingProgress <= 0.0F ? 0.0F : (float)Math.sin(Math.sqrt(this.swingProgress) * Math.PI);
   }

   public float getAnimationProgress(float animationProgressValue) {
      return Math.max(0.0F, (float)Math.sin(this.animationProgress * 0.6662F + animationProgressValue)) * this.limbSwingStrength;
   }

   public float getAgeCode(float ageCodeDivisor, float ageCodeValue) {
      return (float)Math.sin((this.age + (this.hashCode & 0xFF)) / ageCodeDivisor * 2.0F * Math.PI + ageCodeValue);
   }

   public float getAnimationProgressForFloat(float animationProgressValue) {
      return (float)Math.cos(this.animationProgress * 0.6662F + animationProgressValue) * this.limbSwingStrength;
   }
}
