package com.corz.client.utils;

import com.corz.client.render.MatrixRotateUtilsHandle;

public final class AmountConstStrain {
   public float displayBlend;
   public float strainAmount;
   float constStrainAmount;
   public float armRadiansAmount;
   public float flashAlpha;
   public float windupAmount;
   public float afterglowAmount;
   float elapsedTime;
   public float vectorAmount;
   public float armRaiseBlend;
   public static final float UNUSED_SCALE = 1.08F;
   public float windupSquared;
   public float shakePitch;
   public float burstExpand;
   public float vectorTranslateAmount;
   private static final float[] constStrainTable = new float[]{0.0F, 0.55F, 1.35F, 1.65F, 2.15F, 3.75F, 4.0F};
   public float swayRoll;
   public float jitterX;
   public float vectorQuaternionfAmount;
   public final float[] strainTable;
   public float shakeYaw;
   public float flashPulse;
   public float rightArmBlend;
   public float jitterZ;
   public float leanAmount;
   private static final float[] table = new float[]{0.08F, 0.42F, 0.8F, 1.0F, 0.5F, 0.08F, 0.08F};
   public float itemScale;
   public float swayY;
   public float sparkleWindow;
   public float jitterY;
   public float flashPulseLate;
   public float headBlend;
   AmountConstStrain.Phase strainDriftType = AmountConstStrain.Phase.DRIFT;
   public float itemProgress;
   public float burstAlpha;
   public float glowStrength;
   public final float[] sparkleAlphas;
   public float liftAmount;

   static float getAmount(float constStrainFactor, float constStrainLimit, float constStrainFactor2) {
      float constStrainFactor3 = constStrainLimit > 0.0F ? getAmount4(1.35F, 1.65F, constStrainFactor) : 0.0F;
      float constStrainValue = (float)((Math.PI * 2) * constStrainFactor / 4.0);
      float constStrainValue2 = constStrainValue + ((float) (Math.PI * 2) - constStrainValue) * constStrainFactor3;
      float constStrainLimit2 = constStrainValue2 + (float) Math.PI * Math.max(0.0F, constStrainFactor - 1.75F) * constStrainFactor2 * constStrainFactor3;
      return constStrainLimit2 > (float) (Math.PI * 2) ? constStrainLimit2 - (float) (Math.PI * 2) * (float)Math.floor(constStrainLimit2 / (Math.PI * 2)) : constStrainLimit2;
   }

   static float getAmountForFloat(float constStrainLimit, float constStrainLimit2, float constStrainDivisor) {
      if (!(constStrainLimit < constStrainLimit2) && !(constStrainLimit >= constStrainLimit2 + constStrainDivisor)) {
         float constStrainFactor = (constStrainLimit - constStrainLimit2) / constStrainDivisor;
         return MatrixRotateUtilsHandle.getAmountFloat((float)Math.sin(Math.PI * constStrainFactor) * (1.0F - constStrainFactor) / 0.5792F);
      } else {
         return 0.0F;
      }
   }

   public static float getAmount(float constStrainValue) {
      if (!Float.isFinite(constStrainValue)) {
         return 0.0F;
      } else {
         float constStrainTotal = constStrainValue % 4.0F;
         if (constStrainTotal < 0.0F) {
            constStrainTotal += 4.0F;
         }

         return constStrainTotal >= 4.0F ? 0.0F : constStrainTotal;
      }
   }

   static float getAmountFloat(float constStrainLimit, float constStrainLimit2, float constStrainDivisor) {
      return !(constStrainLimit < constStrainLimit2) && !(constStrainLimit >= constStrainLimit2 + constStrainDivisor) ? (constStrainLimit - constStrainLimit2) / constStrainDivisor : 0.0F;
   }

   public static float getAmount2(float constStrainValue) {
      if (constStrainValue < 3.2F) {
         return 1.0F;
      } else if (constStrainValue < 3.28F) {
         return 1.0F - getAmount4(3.2F, 3.28F, constStrainValue);
      } else if (constStrainValue < 3.45F) {
         return 0.0F;
      } else if (constStrainValue < 3.85F) {
         float constStrainFactor = (constStrainValue - 3.45F) / 0.39999986F;
         float constStrainFactor2 = 1.0F - constStrainFactor;
         float sin = (float)Math.sin(Math.PI * constStrainFactor);
         return (1.0F - constStrainFactor2 * constStrainFactor2 * constStrainFactor2) * (1.0F + 0.05F * sin * sin);
      } else {
         return 1.0F;
      }
   }

   public static AmountConstStrain.Phase getStrainPull(float strainPullValue) {
      if (strainPullValue < 0.55F) {
         return AmountConstStrain.Phase.DRIFT;
      } else if (strainPullValue < 1.35F) {
         return AmountConstStrain.Phase.STRAIN;
      } else if (strainPullValue < 1.65F) {
         return AmountConstStrain.Phase.PULL;
      } else {
         return strainPullValue < 3.2F ? AmountConstStrain.Phase.CATCH : AmountConstStrain.Phase.RESET;
      }
   }

   private static float getAmounts(float constStrainValue) {
      for (int i = 1; i < constStrainTable.length; i++) {
         if (constStrainValue < constStrainTable[i]) {
            return table[i - 1] + (table[i] - table[i - 1]) * getAmount4(constStrainTable[i - 1], constStrainTable[i], constStrainValue);
         }
      }

      return table[table.length - 1];
   }

   static float getAmount3(float constStrainLimit) {
      if (constStrainLimit <= 1.35F) {
         return 0.0F;
      } else if (constStrainLimit >= 1.65F) {
         return 1.0F;
      } else {
         float constStrainFactor = (constStrainLimit - 1.35F) / 0.29999995F;
         return constStrainFactor * constStrainFactor * constStrainFactor;
      }
   }

   public static float getAmount4(float constStrainValue) {
      if (constStrainValue < 1.35F) {
         return 0.0F;
      } else {
         return constStrainValue < 3.28F ? getAmount3(constStrainValue) : 0.0F;
      }
   }

   public AmountConstStrain() {
      this.itemScale = 1.0F;
      this.strainTable = new float[6];
      this.sparkleAlphas = new float[6];
   }

   public AmountConstStrain.Phase getStrainDriftType() {
      return this.strainDriftType;
   }

   public float getElapsedTime() {
      return this.elapsedTime;
   }

   static float getAmount5(float constStrainLimit) {
      if (constStrainLimit <= 0.1F) {
         return 0.0F;
      } else if (constStrainLimit >= 0.55F) {
         return 1.0F;
      } else {
         float constStrainFactor = (constStrainLimit - 0.1F) / 0.45000002F - 1.0F;
         return 1.0F + 2.0700002F * constStrainFactor * constStrainFactor * constStrainFactor + 1.07F * constStrainFactor * constStrainFactor;
      }
   }

   static float getAmount4(float constStrainLimit, float constStrainValue, float constStrainLimit2) {
      if (constStrainLimit2 <= constStrainLimit) {
         return 0.0F;
      } else if (constStrainLimit2 >= constStrainValue) {
         return 1.0F;
      } else {
         float constStrainFactor = (constStrainLimit2 - constStrainLimit) / (constStrainValue - constStrainLimit);
         return constStrainFactor * constStrainFactor * (3.0F - 2.0F * constStrainFactor);
      }
   }

   private static float getAmount5(float constStrainFactor, float constStrainFactor2, float constStrainValue) {
      return (float)Math.sin(constStrainFactor * constStrainFactor2 * (Math.PI * 2) + constStrainValue);
   }

   static float getAmount(float constStrainLimit, float constStrainLimit2, float constStrainDivisor, float constStrainFactor) {
      return !(constStrainLimit < constStrainLimit2) && !(constStrainLimit >= constStrainLimit2 + constStrainDivisor) ? constStrainFactor * (1.0F - (constStrainLimit - constStrainLimit2) / constStrainDivisor) : 0.0F;
   }

   public AmountConstStrain getAmountStrain(float strainValue) {
      float amount = getAmount(strainValue);
      this.elapsedTime = amount;
      this.strainDriftType = getStrainPull(amount);
      this.armRaiseBlend = amount < 2.15F ? getAmount5(amount) : 0.0F;
      this.rightArmBlend = amount < 3.25F ? getAmount4(1.75F, 2.15F, amount) : 1.0F - getAmount4(3.25F, 3.75F, amount);
      this.windupAmount = getAmount4(0.55F, 1.3000001F, amount) * (1.0F - getAmount4(1.37F, 1.53F, amount));
      this.windupSquared = this.windupAmount * this.windupAmount;
      this.leanAmount = amount < 2.15F ? getAmount4(1.55F, 1.65F, amount) : 0.0F;
      this.armRadiansAmount = getAmountForFloat(amount, 1.65F, 0.25F);
      this.vectorAmount = 0.5F * getAmount4(0.55F, 1.35F, amount) * (1.0F - getAmount4(1.65F, 2.15F, amount)) + 0.5F * getAmountForFloat(amount, 1.65F, 0.3F);
      this.headBlend = getAmount4(0.0F, 0.45F, amount) * (1.0F - getAmount4(3.25F, 3.7F, amount));
      this.itemProgress = getAmount4(amount);
      float strainFactor = 1.0F - this.itemProgress;
      this.swayY = 0.6F * getAmount5(amount, 0.5F, 0.0F) * strainFactor;
      this.swayRoll = 0.45F * getAmount5(amount, 0.25F, 0.0F) * strainFactor;
      this.liftAmount = this.itemProgress * this.rightArmBlend * (1.5F + 0.5F * getAmount5(amount, 1.5F, 0.0F));
      this.vectorTranslateAmount = getAmount(amount, this.itemProgress, this.rightArmBlend);
      this.itemScale = getAmount2(amount) * (1.0F + 0.08F * this.windupAmount * getAmount5(amount, 9.0F, 0.0F));
      float strainFactor2 = this.windupSquared * 0.09F;
      this.shakePitch = strainFactor2 * (getAmount5(amount, 13.0F, 0.0F) + 0.5F * getAmount5(amount, 21.0F, 1.3F)) / 1.5F;
      this.shakeYaw = strainFactor2 * (getAmount5(amount, 21.0F, 0.4F) + 0.5F * getAmount5(amount, 13.0F, 2.1F)) / 1.5F;
      float strainFactor3 = this.windupSquared * 1.4F;
      this.jitterX = strainFactor3 * getAmount5(amount, 17.0F, 0.0F);
      this.jitterY = strainFactor3 * getAmount5(amount, 23.0F, 0.7F);
      this.jitterZ = strainFactor3 * getAmount5(amount, 19.0F, 1.9F);
      this.glowStrength = MatrixRotateUtilsHandle.getAmountFloat(getAmounts(amount) + 0.08F * this.windupAmount * getAmount5(amount, 9.0F, 0.0F));
      this.constStrainAmount = getAmount4(1.63F, 1.65F, amount) * (1.0F - getAmount4(1.6999999F, 1.75F, amount));
      float strainValue2 = 0.8F * this.glowStrength * Math.min(1.0F, Math.max(this.armRaiseBlend, this.rightArmBlend));
      this.strainAmount = strainValue2 + (1.0F - strainValue2) * this.constStrainAmount;
      boolean strainFlag = amount >= 1.35F && amount < 1.7099999F;
      this.vectorQuaternionfAmount = strainFlag ? getAmount3(amount - 0.06F) : this.itemProgress;
      this.afterglowAmount = strainFlag ? getAmount4(0.2F, 0.3F, (amount - 1.35F) / 0.29999995F) : 0.0F;
      this.burstExpand = getAmountFloat(amount, 1.65F, 0.22F);
      this.burstAlpha = getAmount(amount, 1.65F, 0.22F, 0.6F);
      this.flashPulse = getAmountFloat(amount, 3.2F, 0.25F);
      this.flashAlpha = getAmount(amount, 3.2F, 0.25F, 0.8F);
      this.flashPulseLate = getAmountFloat(amount, 3.45F, 0.25F);
      this.displayBlend = getAmount(amount, 3.45F, 0.25F, 0.8F);
      this.sparkleWindow = getAmount4(0.55F, 0.75F, amount) * (1.0F - getAmount4(1.55F, 1.65F, amount));

      for (int i = 0; i < 6; i++) {
         float hashRegistryValue = (amount - 0.55F) / 0.35F + i / 6.0F;
         float strainValue4 = hashRegistryValue - (float)Math.floor(hashRegistryValue);
         this.strainTable[i] = strainValue4;
         this.sparkleAlphas[i] = 0.7F * this.sparkleWindow * Math.min(1.0F, strainValue4 / 0.15F) * (1.0F - strainValue4);
      }

      return this;
   }

   public static enum Phase {
      DRIFT,
      STRAIN,
      PULL,
      CATCH,
      RESET;
   }
}
