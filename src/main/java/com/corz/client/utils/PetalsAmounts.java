package com.corz.client.utils;
import com.corz.client.utils.PetalsSnowType;
import com.corz.client.utils.AmountAtmoUtils;

public final class PetalsAmounts {
   final float[] vectorTable;
   int targetParticleCount;
   private final int nanoTimePetals;
   final float[] petalsTable;
   private boolean petalsActive;
   final double[] petalsSnowTable;
   final float[] table;
   final float[] tumbleFrequency;
   private boolean hasLastCamPos;
   final float[] swayAngleOffset;
   private double lastCamY;
   private float petalsAmount;
   final float[] velocityY;
   private double lastCamX;
   final float[] swayPhase;
   final float[] fadeIn;
   final float[] currentYaw;
   private float viewDirX;
   double petalsCount;
   final float[] tumblePhase;
   final float[] spinAngle;
   public final PetalsSnowType petalsSnowType;
   final float[] flutterPhase;
   final float[] swayFrequency;
   final float[] velocityX;
   final double[] positionZ;
   int activeParticleCount;
   final float[] sizeScale;
   private float turbulence;
   private float windX;
   private double playerX;
   private double playerY;
   final int particleCapacity;
   private float viewDirZ;
   private float windZ;
   private double playerZ;
   final int[] randomSeed;
   private float windGustStrength;
   public static final PetalsAmounts.Ground NO_GROUND = (petalsValue, petalsValue2) -> Float.NEGATIVE_INFINITY;
   final float[] fallSpeed;
   final double[] positionX;
   final float[] ageSeconds;
   final int[] respawnCount;
   private double lastCamZ;
   final float[] driftRadius;
   final float[] velocityZ;
   final float[] baseYaw;
   final float[] swayAmplitude;

   public int getMin() {
      return this.targetParticleCount;
   }

   public void handleAmountFlag(double amount, double amount2, double amount3, float amount4, float amount5, float amount6) {
      this.petalsActive = true;
      this.playerX = amount;
      this.playerY = amount2;
      this.playerZ = amount3;
      this.viewDirX = amount4;
      this.petalsAmount = amount5;
      this.viewDirZ = amount6;
   }

   private void handlePetalsSnow(int index, double petalsSnowValue, double y, double z) {
      double vectorAmount = this.petalsSnowType.vectorAmount;
      double petalsSnowLimit = this.positionX[index] - petalsSnowValue;
      double petalsValue = this.positionZ[index] - z;
      double petalsSnowLimit3 = this.petalsSnowTable[index] - y;
      if (petalsSnowLimit < -vectorAmount || petalsSnowLimit >= vectorAmount) {
         this.positionX[index] = petalsSnowValue + getAmount(petalsSnowLimit, (double)this.petalsSnowType.getAmount());
      }

      if (petalsValue < -vectorAmount || petalsValue >= vectorAmount) {
         this.positionZ[index] = z + getAmount(petalsValue, (double)this.petalsSnowType.getAmount());
      }

      if (petalsSnowLimit3 < -this.petalsSnowType.petalsSnowAmount) {
         this.petalsSnowTable[index] = y - this.petalsSnowType.petalsSnowAmount;
      } else if (petalsSnowLimit3 >= this.petalsSnowType.spawnHeight) {
         this.petalsSnowTable[index] = y + this.petalsSnowType.spawnHeight - 0.001;
      }
   }

   private void handlePetalsSnowForIntDouble(int index, double petalsSnowValue, double y, double z) {
      double petalsFactor = this.petalsSnowType.getAmount();
      double vectorAmount = this.petalsSnowType.vectorAmount;
      double petalsSnowLimit = this.positionX[index] - petalsSnowValue;
      if (petalsSnowLimit < -vectorAmount || petalsSnowLimit >= vectorAmount) {
         this.positionX[index] = petalsSnowValue + getAmount(petalsSnowLimit, petalsFactor);
      }

      double petalsValue = this.positionZ[index] - z;
      if (petalsValue < -vectorAmount || petalsValue >= vectorAmount) {
         this.positionZ[index] = z + getAmount(petalsValue, petalsFactor);
      }

      double petalsSnowLimit3 = this.petalsSnowTable[index] - y;
      if (petalsSnowLimit3 < -this.petalsSnowType.petalsSnowAmount) {
         this.handlePetalsSnowRespawnCount(index, petalsSnowValue, y, z);
      } else if (petalsSnowLimit3 >= this.petalsSnowType.spawnHeight) {
         double petalsSnowFactor = this.petalsSnowType.getAmountPetalsSnowAmount();
         double petalsSnowValue5 = petalsSnowLimit3 + this.petalsSnowType.petalsSnowAmount;
         double petalsSnowValue6 = petalsSnowValue5 - petalsSnowFactor * Math.floor(petalsSnowValue5 / petalsSnowFactor);
         this.petalsSnowTable[index] = y - this.petalsSnowType.petalsSnowAmount + petalsSnowValue6;
      }
   }

   void handlePetalsSnowRespawnCount(int index, double petalsSnowValue, double y, double z) {
      this.respawnCount[index]++;
      this.handleAmountsPetals(index);
      int atmoValue = this.randomSeed[index];
      this.positionX[index] = petalsSnowValue + (AmountAtmoUtils.getAmount(atmoValue, 40) - 0.5F) * this.petalsSnowType.getAmount();
      this.positionZ[index] = z + (AmountAtmoUtils.getAmount(atmoValue, 41) - 0.5F) * this.petalsSnowType.getAmount();
      this.petalsSnowTable[index] = y + this.petalsSnowType.spawnHeight - 0.001 - AmountAtmoUtils.getAmount(atmoValue, 42) * 0.6;
      this.handlePetalsSnow(index, petalsSnowValue, y, z);
      this.handleAmountsAmount(index);
      this.table[index] = 0.0F;
   }

   public void updateMaxFlag() {
      this.activeParticleCount = 0;
      this.hasLastCamPos = false;
   }

   private void handleAmountsPetals(int index) {
      int petalsValue = AmountAtmoUtils.getValue(AmountAtmoUtils.getValue(index * -1640531535 + this.nanoTimePetals) ^ this.respawnCount[index] * -2048144777);
      this.randomSeed[index] = petalsValue;
      float petalsFactor = AmountAtmoUtils.getAmount(petalsValue, 0);
      float petalsValue2 = AmountAtmoUtils.getAmount(petalsValue, 1);
      float petalsValue3 = AmountAtmoUtils.getAmount(petalsValue, 2);
      float petalsValue4 = AmountAtmoUtils.getAmount(petalsValue, 3);
      float petalsValue5 = AmountAtmoUtils.getAmount(petalsValue, 4);
      float petalsValue6 = AmountAtmoUtils.getAmount(petalsValue, 5);
      float petalsValue7 = AmountAtmoUtils.getAmount(petalsValue, 6);
      float petalsValue8 = AmountAtmoUtils.getAmount(petalsValue, 7);
      if (this.petalsSnowType == PetalsSnowType.PETALS) {
         float petalsValue9 = AmountAtmoUtils.getAmount(petalsValue, 8);
         float petalsValue10 = AmountAtmoUtils.getAmount(petalsValue, 9);
         float petalsValue11 = AmountAtmoUtils.getAmount(petalsValue, 10);
         float petalsValue12 = AmountAtmoUtils.getAmount(petalsValue, 11);
         float petalsFactor2 = AmountAtmoUtils.getAmount(petalsValue, 12);
         float petalsValue13 = AmountAtmoUtils.getAmount(petalsValue, 13);
         float petalsFactor3 = AmountAtmoUtils.getAmount(petalsValue, 14);
         this.vectorTable[index] = 0.055F + 0.05F * petalsFactor * (float)Math.sqrt(petalsFactor);
         this.sizeScale[index] = 0.82F + 0.24F * petalsValue2;
         this.fallSpeed[index] = 0.48F + 0.3F * petalsValue3;
         this.driftRadius[index] = 0.55F + 0.6F * petalsValue4;
         this.swayFrequency[index] = 0.26F + 0.24F * petalsValue5;
         this.tumbleFrequency[index] = petalsValue6;
         this.swayPhase[index] = petalsValue7;
         this.tumblePhase[index] = petalsValue8;
         float petalsValue14 = petalsValue9 < 0.25F ? 2.2F + 2.2F * petalsValue10 : 0.25F + 0.7F * petalsValue10;
         this.petalsTable[index] = petalsValue11 < 0.5F ? -petalsValue14 : petalsValue14;
         this.swayAmplitude[index] = 0.45F + 0.6F * petalsValue12;
         this.baseYaw[index] = petalsFactor2 * (float) (Math.PI * 2);
         this.flutterPhase[index] = petalsValue13;
         this.spinAngle[index] = petalsFactor3 * (float) (Math.PI * 2);
         this.currentYaw[index] = this.baseYaw[index];
      } else {
         float petalsValue15 = 0.042F + 0.075F * petalsFactor * petalsFactor;
         this.vectorTable[index] = petalsValue15;
         this.sizeScale[index] = 1.0F;
         this.fallSpeed[index] = 0.62F + 0.42F * petalsValue2 + 2.6F * (petalsValue15 - 0.036F);
         this.driftRadius[index] = 0.1F + 0.22F * petalsValue3;
         this.swayFrequency[index] = 0.22F + 0.5F * petalsValue4;
         this.tumbleFrequency[index] = this.swayFrequency[index] * (0.7F + 0.6F * petalsValue5);
         this.swayPhase[index] = petalsValue6;
         this.tumblePhase[index] = petalsValue7;
         this.flutterPhase[index] = petalsValue8;
         this.petalsTable[index] = 0.0F;
         this.swayAmplitude[index] = 0.0F;
         this.baseYaw[index] = 0.0F;
         this.spinAngle[index] = 0.0F;
         this.currentYaw[index] = 0.0F;
      }
   }

   private void handleMaxMin(double petalsValue, double y, double z) {
      this.hasLastCamPos = true;
      int max = Math.max(this.activeParticleCount, this.targetParticleCount);

      for (int i = 0; i < max; i++) {
         this.handlePetalsSnow4(i, petalsValue, y, z);
      }

      this.activeParticleCount = max;
   }

   private boolean canPetalsSnow(int index, double petalsSnowValue, double y, double z) {
      double vectorAmount = this.petalsSnowType.vectorAmount;
      double petalsSnowLimit = this.positionX[index] - petalsSnowValue;
      double petalsSnowLimit2 = this.petalsSnowTable[index] - y;
      double petalsSnowLimit3 = this.positionZ[index] - z;
      return petalsSnowLimit < -vectorAmount || petalsSnowLimit >= vectorAmount || petalsSnowLimit3 < -vectorAmount || petalsSnowLimit3 >= vectorAmount || petalsSnowLimit2 < -this.petalsSnowType.petalsSnowAmount || petalsSnowLimit2 >= this.petalsSnowType.spawnHeight;
   }

   private void handlePetalsSnow4(int index, double petalsSnowValue, double y, double z) {
      this.respawnCount[index]++;
      this.handleAmountsPetals(index);
      int atmoValue = this.randomSeed[index];
      this.positionX[index] = petalsSnowValue + (AmountAtmoUtils.getAmount(atmoValue, 40) - 0.5F) * this.petalsSnowType.getAmount();
      this.positionZ[index] = z + (AmountAtmoUtils.getAmount(atmoValue, 41) - 0.5F) * this.petalsSnowType.getAmount();
      this.petalsSnowTable[index] = y - this.petalsSnowType.petalsSnowAmount + AmountAtmoUtils.getAmount(atmoValue, 42) * this.petalsSnowType.getAmountPetalsSnowAmount() * 0.999F;
      this.handlePetalsSnow(index, petalsSnowValue, y, z);
      this.handleAmountsAmount(index);
      this.table[index] = 0.0F;
   }

   public void updateFlag() {
      this.petalsActive = false;
   }

   public void handleMin(int petalsBound) {
      this.targetParticleCount = petalsBound < 0 ? 0 : Math.min(petalsBound, this.particleCapacity);
   }

   public void handleAmountMax(double amount, double y, double z, float petalsLimit, PetalsAmounts.Ground ground) {
      if (ground == null) {
         ground = NO_GROUND;
      }

      if (!(petalsLimit >= 0.0F)) {
         petalsLimit = 0.0F;
      }

      if (petalsLimit > 0.2F) {
         petalsLimit = 0.2F;
      }

      float petalsValue = this.petalsSnowType.getAmount();
      float amountPetalsSnowAmount = this.petalsSnowType.getAmountPetalsSnowAmount();
      if (!this.hasLastCamPos || Math.abs(amount - this.lastCamX) > petalsValue || Math.abs(z - this.lastCamZ) > petalsValue || Math.abs(y - this.lastCamY) > amountPetalsSnowAmount) {
         this.handleMaxMin(amount, y, z);
      }

      this.lastCamX = amount;
      this.lastCamY = y;
      this.lastCamZ = z;
      this.petalsCount += petalsLimit;
      if (this.targetParticleCount > this.activeParticleCount) {
         for (int i = this.activeParticleCount; i < this.targetParticleCount; i++) {
            this.handlePetalsSnow4(i, amount, y, z);
         }

         this.activeParticleCount = this.targetParticleCount;
      }

      int petalsDivisor = petalsLimit > 0.05F ? (int)Math.ceil(petalsLimit / 0.05F) : 1;
      float petalsFactor = petalsLimit / petalsDivisor;
      float petalsValue4 = 1.0F - (float)Math.exp(-petalsFactor / this.petalsSnowType.smoothingTime);
      float petalsValue5 = 1.0F - (float)Math.exp(-petalsLimit * 2.4F);
      float petalsValue6 = 1.0F - (float)Math.exp(-petalsLimit * 24.0F);

      for (int j = 0; j < this.activeParticleCount; j++) {
         this.handleAmountsPetals(j, petalsFactor, petalsDivisor, petalsLimit, petalsValue4, petalsValue5, petalsValue6, amount, y, z, ground);
      }

      while (this.activeParticleCount > this.targetParticleCount && this.table[this.activeParticleCount - 1] <= 0.002F) {
         this.activeParticleCount--;
      }
   }

   private void handleAmountAmounts(int index, float petalsFactor) {
      double petalsFactor2 = this.positionX[index] - this.playerX;
      double petalsFactor3 = this.positionZ[index] - this.playerZ;
      double petalsLimit = this.petalsSnowTable[index] - (this.playerY + 0.9);
      if (!(petalsFactor2 > 2.0) && !(petalsFactor2 < -2.0) && !(petalsFactor3 > 2.0) && !(petalsFactor3 < -2.0)) {
         if (!(petalsLimit < -1.5) && !(petalsLimit > 1.7)) {
            float squaredDistanceTo = (float)(petalsFactor2 * petalsFactor2 + petalsFactor3 * petalsFactor3);
            if (!(squaredDistanceTo >= 4.0F) && !(squaredDistanceTo < 1.0E-6F)) {
               float sqrt = (float)Math.sqrt(this.viewDirX * this.viewDirX + this.viewDirZ * this.viewDirZ + this.petalsAmount * this.petalsAmount * 0.25F);
               if (!(sqrt < 0.15F)) {
                  float sqrt2 = (float)Math.sqrt(squaredDistanceTo);
                  float petalsFactor4 = 1.0F - sqrt2 / 2.0F;
                  float petalsFactor5 = petalsFactor4 * petalsFactor4;
                  float petalsFactor6 = petalsFactor5 * (1.0F - AmountAtmoUtils.getAmount(0.9F, 1.6F, (float)Math.abs(petalsLimit)));
                  if (!(petalsFactor6 <= 0.0F)) {
                     float petalsFactor7 = (float)petalsFactor2 / sqrt2;
                     float petalsFactor8 = (float)petalsFactor3 / sqrt2;
                     float petalsValue = petalsFactor7 * sqrt * 0.95F;
                     float petalsValue2 = petalsFactor8 * sqrt * 0.95F;
                     float petalsFactor9 = this.viewDirX * petalsFactor8 - this.viewDirZ * petalsFactor7 >= 0.0F ? 1.0F : -1.0F;
                     float petalsFactor10 = petalsValue + -petalsFactor8 * petalsFactor9 * sqrt * 0.5F;
                     float petalsFactor11 = petalsValue2 + petalsFactor7 * petalsFactor9 * sqrt * 0.5F;
                     float distance = (float)Math.sqrt(this.viewDirX * this.viewDirX + this.viewDirZ * this.viewDirZ);
                     if (distance > 0.001F) {
                        float petalsValue3 = (petalsFactor7 * this.viewDirX + petalsFactor8 * this.viewDirZ) / distance;
                        if (petalsValue3 < 0.0F) {
                           petalsFactor10 += this.viewDirX * -petalsValue3 * 0.55F;
                           petalsFactor11 += this.viewDirZ * -petalsValue3 * 0.55F;
                        }
                     }

                     float petalsFactor12 = this.petalsAmount * 0.3F;
                     float distance2 = (float)Math.sqrt(petalsFactor10 * petalsFactor10 + petalsFactor11 * petalsFactor11 + petalsFactor12 * petalsFactor12);
                     if (distance2 > 9.0F) {
                        float petalsValue4 = 9.0F / distance2;
                        petalsFactor10 *= petalsValue4;
                        petalsFactor11 *= petalsValue4;
                        petalsFactor12 *= petalsValue4;
                     }

                     this.velocityX[index] = this.velocityX[index] + petalsFactor10 * petalsFactor6 * petalsFactor;
                     this.velocityY[index] = this.velocityY[index] + petalsFactor12 * petalsFactor6 * petalsFactor;
                     this.velocityZ[index] = this.velocityZ[index] + petalsFactor11 * petalsFactor6 * petalsFactor;
                  }
               }
            }
         }
      }
   }

   public PetalsAmounts(PetalsSnowType petalsSnowType, int nanoTime) {
      this.petalsSnowType = petalsSnowType;
      this.particleCapacity = petalsSnowType.baseParticleCount;
      this.nanoTimePetals = nanoTime;
      int petalsSize = this.particleCapacity;
      this.positionX = new double[petalsSize];
      this.petalsSnowTable = new double[petalsSize];
      this.positionZ = new double[petalsSize];
      this.velocityX = new float[petalsSize];
      this.velocityY = new float[petalsSize];
      this.velocityZ = new float[petalsSize];
      this.table = new float[petalsSize];
      this.ageSeconds = new float[petalsSize];
      this.respawnCount = new int[petalsSize];
      this.randomSeed = new int[petalsSize];
      this.vectorTable = new float[petalsSize];
      this.sizeScale = new float[petalsSize];
      this.fallSpeed = new float[petalsSize];
      this.driftRadius = new float[petalsSize];
      this.swayFrequency = new float[petalsSize];
      this.tumbleFrequency = new float[petalsSize];
      this.swayPhase = new float[petalsSize];
      this.tumblePhase = new float[petalsSize];
      this.petalsTable = new float[petalsSize];
      this.swayAmplitude = new float[petalsSize];
      this.baseYaw = new float[petalsSize];
      this.flutterPhase = new float[petalsSize];
      this.currentYaw = new float[petalsSize];
      this.spinAngle = new float[petalsSize];
      this.swayAngleOffset = new float[petalsSize];
      this.fadeIn = new float[petalsSize];
   }

   public static double getAmount(double petalsValue, double petalsFactor) {
      double petalsValue2 = petalsFactor * 0.5;
      return petalsValue - petalsFactor * Math.floor((petalsValue + petalsValue2) / petalsFactor);
   }

   public void handleAmount(float amount, float amount2, float amount3, float amount4) {
      this.windX = amount;
      this.windZ = amount2;
      this.turbulence = amount3;
      this.windGustStrength = amount4;
   }

   public int getMax() {
      return this.activeParticleCount;
   }

   public static void handle(double petalsValue, float petalsValue2, float[] petalsValues) {
      float petalsValue3 = (AmountAtmoUtils.getAmount(petalsValue * 0.011 + 7.31) - 0.5F) * 2.8F;
      float angle = 0.6F + petalsValue3;
      float petalsValue4 = petalsValue2 * 2.2F * (0.75F + 0.5F * AmountAtmoUtils.getAmount(petalsValue * 0.043 + 1.17));
      float petalsFactor = AmountAtmoUtils.getAmount(0.58F, 0.92F, AmountAtmoUtils.getAmount(petalsValue * 0.16 + 3.71));
      float petalsFactor2 = petalsValue4 + petalsValue2 * 1.7F * petalsFactor;
      petalsValues[0] = (float)Math.cos(angle) * petalsFactor2;
      petalsValues[1] = (float)Math.sin(angle) * petalsFactor2;
      petalsValues[2] = petalsFactor;
   }

   private static double getAmount(double petalsValue) {
      return petalsValue - Math.floor(petalsValue);
   }

   private void handleAmountsAmount(int index) {
      float petalsAmount2 = this.petalsSnowType.petalsAmount;
      this.velocityX[index] = this.windX * petalsAmount2;
      this.velocityZ[index] = this.windZ * petalsAmount2;
      this.velocityY[index] = -this.fallSpeed[index];
      this.ageSeconds[index] = -1.0F;
      this.fadeIn[index] = 0.0F;
      this.swayAngleOffset[index] = 0.0F;
   }

   static int getValue(double petalsValue) {
      int petalsLimit = (int)petalsValue;
      return petalsValue < petalsLimit ? petalsLimit - 1 : petalsLimit;
   }

   private void handleAmountsPetals(
      int index, float petalsFactor, int petalsLimit, float petalsFactor2, float petalsFactor3, float petalsValue, float petalsValue2, double petalsSnowValue, double y, double z, PetalsAmounts.Ground ground
   ) {
      float windInfluence = this.petalsSnowType.windInfluence;
      if (this.ageSeconds[index] >= 0.0F) {
         this.ageSeconds[index] = this.ageSeconds[index] + petalsFactor2;
         this.fadeIn[index] = Math.min(1.0F, this.fadeIn[index] + petalsFactor2 * 4.0F);
         this.velocityX[index] = 0.0F;
         this.velocityY[index] = 0.0F;
         this.velocityZ[index] = 0.0F;
         if (this.ageSeconds[index] >= this.petalsSnowType.lifetimeSeconds) {
            this.handlePetalsSnowRespawnCount(index, petalsSnowValue, y, z);
         } else if (this.canPetalsSnow(index, petalsSnowValue, y, z)) {
            this.handlePetalsSnowRespawnCount(index, petalsSnowValue, y, z);
         }
      } else {
         float petalsFactor4 = 0.25F + 0.9F * this.turbulence;
         double petalsCount5 = this.petalsCount;
         float petalsValue6 = (AmountAtmoUtils.getAmount(this.positionX[index] * 0.045 + petalsCount5 * 0.07, this.positionZ[index] * 0.045 - petalsCount5 * 0.05) - 0.5F) * petalsFactor4;
         float petalsValue7 = (
               AmountAtmoUtils.getAmount(this.positionX[index] * 0.045 - petalsCount5 * 0.06 + 31.7, this.positionZ[index] * 0.045 + petalsCount5 * 0.04 + 11.3) - 0.5F
            )
            * petalsFactor4;
         float petalsAmount2 = this.petalsSnowType.petalsAmount;
         float petalsValue8;
         float petalsValue9;
         float petalsValue10;
         if (this.petalsSnowType == PetalsSnowType.PETALS) {
            float angle = (float)getAmount(petalsCount5 * this.swayFrequency[index] + this.swayPhase[index]) * (float) (Math.PI * 2);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float angle2 = this.baseYaw[index] + (float)(petalsCount5 * 0.09 * (this.tumbleFrequency[index] - 0.5));
            this.currentYaw[index] = angle2;
            float petalsFactor5 = this.driftRadius[index] * cos;
            float petalsFactor6 = AmountAtmoUtils.getAmount(0.64F, 1.0F, AmountAtmoUtils.getAmount(petalsCount5 * 0.08 + this.tumblePhase[index] * 61.0));
            float petalsValue11 = petalsFactor6 * (0.35F + 0.9F * this.turbulence) * 1.15F + this.windGustStrength * this.turbulence * 0.45F;
            petalsValue8 = (float)Math.cos(angle2) * petalsFactor5 + this.windX * petalsAmount2 + petalsValue6;
            petalsValue9 = (float)Math.sin(angle2) * petalsFactor5 + this.windZ * petalsAmount2 + petalsValue7;
            petalsValue10 = -this.fallSpeed[index] * (0.42F + 0.9F * cos * cos) + petalsValue11;
            this.swayAngleOffset[index] = this.swayAmplitude[index] * sin;
            this.spinAngle[index] = this.spinAngle[index] + this.petalsTable[index] * petalsFactor2;
            if (this.spinAngle[index] > (float) (Math.PI * 2)) {
               this.spinAngle[index] = this.spinAngle[index] - (float) (Math.PI * 2);
            } else if (this.spinAngle[index] < (float) (-Math.PI * 2)) {
               this.spinAngle[index] = this.spinAngle[index] + (float) (Math.PI * 2);
            }
         } else {
            float angle3 = (float)getAmount(petalsCount5 * this.swayFrequency[index] + this.swayPhase[index]) * (float) (Math.PI * 2);
            float angle4 = (float)getAmount(petalsCount5 * this.tumbleFrequency[index] + this.tumblePhase[index]) * (float) (Math.PI * 2);
            float sin2 = (float)Math.sin(angle3);
            petalsValue8 = this.driftRadius[index] * sin2 + this.windX * petalsAmount2 + petalsValue6;
            petalsValue9 = this.driftRadius[index] * (float)Math.cos(angle4) + this.windZ * petalsAmount2 + petalsValue7;
            petalsValue10 = -this.fallSpeed[index] * (1.0F + 0.1F * (float)Math.sin(angle3 * 1.7F)) + (petalsValue6 - petalsValue7) * 0.15F;
         }

         for (int j = 0; j < petalsLimit; j++) {
            this.velocityX[index] = this.velocityX[index] + (petalsValue8 - this.velocityX[index]) * petalsFactor3;
            this.velocityY[index] = this.velocityY[index] + (petalsValue10 - this.velocityY[index]) * petalsFactor3;
            this.velocityZ[index] = this.velocityZ[index] + (petalsValue9 - this.velocityZ[index]) * petalsFactor3;
            if (this.petalsActive) {
               this.handleAmountAmounts(index, petalsFactor);
            }

            double petalsLimit2 = this.petalsSnowTable[index];
            this.positionX[index] = this.positionX[index] + this.velocityX[index] * petalsFactor;
            this.petalsSnowTable[index] = this.petalsSnowTable[index] + this.velocityY[index] * petalsFactor;
            this.positionZ[index] = this.positionZ[index] + this.velocityZ[index] * petalsFactor;
            float top = ground.top(getValue(this.positionX[index]), getValue(this.positionZ[index]));
            if (this.petalsSnowTable[index] < top + windInfluence && petalsLimit2 >= top + windInfluence) {
               this.petalsSnowTable[index] = top + windInfluence;
               this.ageSeconds[index] = 0.0F;
               this.fadeIn[index] = 0.0F;
               this.velocityX[index] = 0.0F;
               this.velocityY[index] = 0.0F;
               this.velocityZ[index] = 0.0F;
               break;
            }
         }

         if (this.ageSeconds[index] < 0.0F) {
            this.handlePetalsSnowForIntDouble(index, petalsSnowValue, y, z);
         } else if (this.canPetalsSnow(index, petalsSnowValue, y, z)) {
            this.handlePetalsSnowRespawnCount(index, petalsSnowValue, y, z);
         }
      }

      boolean petalsFlag = index < this.targetParticleCount;
      boolean petalsFlag2 = false;
      if (this.ageSeconds[index] < 0.0F) {
         float top2 = ground.top(getValue(this.positionX[index]), getValue(this.positionZ[index]));
         petalsFlag2 = this.petalsSnowTable[index] < top2 + windInfluence - 0.001F;
      }

      float petalsLimit3 = petalsFlag && !petalsFlag2 ? 1.0F : 0.0F;
      float petalsFactor7 = petalsLimit3 > this.table[index] ? petalsValue : (petalsFlag2 ? petalsValue2 : petalsValue);
      this.table[index] = this.table[index] + (petalsLimit3 - this.table[index]) * petalsFactor7;
      if (petalsLimit3 == 0.0F && this.table[index] < 0.002F) {
         this.table[index] = 0.0F;
      }
   }

   public int getAmountsMax() {
      int amountsMax = 0;

      for (int i = 0; i < this.activeParticleCount; i++) {
         if (this.table[i] > 0.002F) {
            amountsMax++;
         }
      }

      return amountsMax;
   }

   float getPetalsSnow(int index) {
      float petalsSnowValue = this.ageSeconds[index];
      return petalsSnowValue < 0.0F ? 1.0F : 1.0F - AmountAtmoUtils.getAmount(this.petalsSnowType.lifetimeSeconds * 0.2F, this.petalsSnowType.lifetimeSeconds, petalsSnowValue);
   }

   public interface Ground {
      float top(int topValue, int topValue2);
   }
}
