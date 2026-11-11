package com.corz.client.utils;

public enum PetalsSnowType {
   SNOW(5000, 16.0F, 6.0F, 12.0F, 0.55F, 1.0F, 1.1F, 0.96F, 0.05F),
   PETALS(1600, 15.0F, 6.0F, 12.0F, 0.85F, 0.85F, 2.2F, 0.95F, 0.025F);
   public final float sizeMultiplier;
   public final float petalsAmount;
   public final int baseParticleCount;
   public final float petalsSnowAmount;
   public final float lifetimeSeconds;
   public final float vectorAmount;
   public final float smoothingTime;
   public final float spawnHeight;
   public final float windInfluence;

   public int getValue(float petalsSnowLimit) {
      float petalsSnowFactor = petalsSnowLimit < 0.0F ? 0.0F : (petalsSnowLimit > 1.0F ? 1.0F : petalsSnowLimit);
      return Math.round(this.baseParticleCount * petalsSnowFactor);
   }

   public float getAmount() {
      return this.vectorAmount * 2.0F;
   }

   public float getAmountPetalsSnowAmount() {
      return this.petalsSnowAmount + this.spawnHeight;
   }

   private PetalsSnowType(int value, float amount, float amount2, float amount3, float amount4, float amount5, float amount6, float amount7, float amount8) {
      this.baseParticleCount = value;
      this.vectorAmount = amount;
      this.petalsSnowAmount = amount2;
      this.spawnHeight = amount3;
      this.smoothingTime = amount4;
      this.petalsAmount = amount5;
      this.lifetimeSeconds = amount6;
      this.sizeMultiplier = amount7;
      this.windInfluence = amount8;
   }
}
