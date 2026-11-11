package com.corz.client.utils;

public final class AmountLoft {
   public float translateZ;
   public float loftAmount;
   public float translateY;
   public float matrixMFloat;
   public float matrixM20;
   public float matrixM22;
   public float matrixM11;
   public float translateX;
   public float matrixM10;
   public float matrixM12;
   public float matrixM02;
   public float matrixM0F = 1.0F;

   public AmountLoft getAmount(AmountLoft amountLoft) {
      this.matrixM0F = amountLoft.matrixM0F;
      this.matrixMFloat = amountLoft.matrixMFloat;
      this.matrixM02 = amountLoft.matrixM02;
      this.translateX = amountLoft.translateX;
      this.matrixM10 = amountLoft.matrixM10;
      this.matrixM11 = amountLoft.matrixM11;
      this.matrixM12 = amountLoft.matrixM12;
      this.translateY = amountLoft.translateY;
      this.matrixM20 = amountLoft.matrixM20;
      this.loftAmount = amountLoft.loftAmount;
      this.matrixM22 = amountLoft.matrixM22;
      this.translateZ = amountLoft.translateZ;
      return this;
   }

   public void handleAmount(float[] loftValues, int index) {
      loftValues[index] = this.matrixM0F;
      loftValues[index + 1] = this.matrixMFloat;
      loftValues[index + 2] = this.matrixM02;
      loftValues[index + 3] = this.translateX;
      loftValues[index + 4] = this.matrixM10;
      loftValues[index + 5] = this.matrixM11;
      loftValues[index + 6] = this.matrixM12;
      loftValues[index + 7] = this.translateY;
      loftValues[index + 8] = this.matrixM20;
      loftValues[index + 9] = this.loftAmount;
      loftValues[index + 10] = this.matrixM22;
      loftValues[index + 11] = this.translateZ;
   }

   public float getAmount(float loftFactor, float loftFactor2, float minSide) {
      return this.matrixM20 * loftFactor + this.loftAmount * loftFactor2 + this.matrixM22 * minSide + this.translateZ;
   }

   public AmountLoft getAmount() {
      this.matrixM0F = 1.0F;
      this.matrixMFloat = 0.0F;
      this.matrixM02 = 0.0F;
      this.translateX = 0.0F;
      this.matrixM10 = 0.0F;
      this.matrixM11 = 1.0F;
      this.matrixM12 = 0.0F;
      this.translateY = 0.0F;
      this.matrixM20 = 0.0F;
      this.loftAmount = 0.0F;
      this.matrixM22 = 1.0F;
      this.translateZ = 0.0F;
      return this;
   }

   public AmountLoft() {
      this.matrixM11 = 1.0F;
      this.matrixM22 = 1.0F;
   }

   public AmountLoft getAmount(float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float amount = this.matrixM0F * cos - this.matrixM02 * sin;
         this.matrixM02 = this.matrixM0F * sin + this.matrixM02 * cos;
         this.matrixM0F = amount;
         float amount2 = this.matrixM10 * cos - this.matrixM12 * sin;
         this.matrixM12 = this.matrixM10 * sin + this.matrixM12 * cos;
         this.matrixM10 = amount2;
         float amount3 = this.matrixM20 * cos - this.matrixM22 * sin;
         this.matrixM22 = this.matrixM20 * sin + this.matrixM22 * cos;
         this.matrixM20 = amount3;
         return this;
      }
   }

   public void handleAmountForFloatArrayInt(float[] loftValues, int index) {
      float loftFactor = this.matrixM11 * this.matrixM22 - this.matrixM12 * this.loftAmount;
      float loftFactor2 = this.matrixM12 * this.matrixM20 - this.matrixM10 * this.matrixM22;
      float loftFactor3 = this.matrixM10 * this.loftAmount - this.matrixM11 * this.matrixM20;
      float loftFactor4 = this.matrixM02 * this.loftAmount - this.matrixMFloat * this.matrixM22;
      float loftFactor5 = this.matrixM0F * this.matrixM22 - this.matrixM02 * this.matrixM20;
      float loftFactor6 = this.matrixMFloat * this.matrixM20 - this.matrixM0F * this.loftAmount;
      float loftFactor7 = this.matrixMFloat * this.matrixM12 - this.matrixM02 * this.matrixM11;
      float loftFactor8 = this.matrixM02 * this.matrixM10 - this.matrixM0F * this.matrixM12;
      float loftFactor9 = this.matrixM0F * this.matrixM11 - this.matrixMFloat * this.matrixM10;
      float loftValue = this.matrixM0F * loftFactor + this.matrixMFloat * loftFactor2 + this.matrixM02 * loftFactor3;
      float loftFactor10 = loftValue < 0.0F ? -1.0F : 1.0F;
      loftValues[index] = loftFactor * loftFactor10;
      loftValues[index + 1] = loftFactor2 * loftFactor10;
      loftValues[index + 2] = loftFactor3 * loftFactor10;
      loftValues[index + 3] = loftFactor4 * loftFactor10;
      loftValues[index + 4] = loftFactor5 * loftFactor10;
      loftValues[index + 5] = loftFactor6 * loftFactor10;
      loftValues[index + 6] = loftFactor7 * loftFactor10;
      loftValues[index + 7] = loftFactor8 * loftFactor10;
      loftValues[index + 8] = loftFactor9 * loftFactor10;
   }

   public AmountLoft getAmountForFloat(float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float amount = this.matrixMFloat * cos + this.matrixM02 * sin;
         this.matrixM02 = this.matrixM02 * cos - this.matrixMFloat * sin;
         this.matrixMFloat = amount;
         float amount2 = this.matrixM11 * cos + this.matrixM12 * sin;
         this.matrixM12 = this.matrixM12 * cos - this.matrixM11 * sin;
         this.matrixM11 = amount2;
         float amount3 = this.loftAmount * cos + this.matrixM22 * sin;
         this.matrixM22 = this.matrixM22 * cos - this.loftAmount * sin;
         this.loftAmount = amount3;
         return this;
      }
   }

   public float getAmountMatrixM10(float loftFactor, float loftFactor2, float minSide) {
      return this.matrixM10 * loftFactor + this.matrixM11 * loftFactor2 + this.matrixM12 * minSide + this.translateY;
   }

   public AmountLoft getAmountForAmountLoft(AmountLoft amountLoft) {
      float amount = this.matrixM0F * amountLoft.matrixM0F + this.matrixMFloat * amountLoft.matrixM10 + this.matrixM02 * amountLoft.matrixM20;
      float amount2 = this.matrixM0F * amountLoft.matrixMFloat + this.matrixMFloat * amountLoft.matrixM11 + this.matrixM02 * amountLoft.loftAmount;
      float amount3 = this.matrixM0F * amountLoft.matrixM02 + this.matrixMFloat * amountLoft.matrixM12 + this.matrixM02 * amountLoft.matrixM22;
      float amount4 = this.matrixM0F * amountLoft.translateX + this.matrixMFloat * amountLoft.translateY + this.matrixM02 * amountLoft.translateZ + this.translateX;
      float amount5 = this.matrixM10 * amountLoft.matrixM0F + this.matrixM11 * amountLoft.matrixM10 + this.matrixM12 * amountLoft.matrixM20;
      float amount6 = this.matrixM10 * amountLoft.matrixMFloat + this.matrixM11 * amountLoft.matrixM11 + this.matrixM12 * amountLoft.loftAmount;
      float amount7 = this.matrixM10 * amountLoft.matrixM02 + this.matrixM11 * amountLoft.matrixM12 + this.matrixM12 * amountLoft.matrixM22;
      float amount8 = this.matrixM10 * amountLoft.translateX + this.matrixM11 * amountLoft.translateY + this.matrixM12 * amountLoft.translateZ + this.translateY;
      float amount9 = this.matrixM20 * amountLoft.matrixM0F + this.loftAmount * amountLoft.matrixM10 + this.matrixM22 * amountLoft.matrixM20;
      float amount10 = this.matrixM20 * amountLoft.matrixMFloat + this.loftAmount * amountLoft.matrixM11 + this.matrixM22 * amountLoft.loftAmount;
      float amount11 = this.matrixM20 * amountLoft.matrixM02 + this.loftAmount * amountLoft.matrixM12 + this.matrixM22 * amountLoft.matrixM22;
      float amount12 = this.matrixM20 * amountLoft.translateX + this.loftAmount * amountLoft.translateY + this.matrixM22 * amountLoft.translateZ + this.translateZ;
      this.matrixM0F = amount;
      this.matrixMFloat = amount2;
      this.matrixM02 = amount3;
      this.translateX = amount4;
      this.matrixM10 = amount5;
      this.matrixM11 = amount6;
      this.matrixM12 = amount7;
      this.translateY = amount8;
      this.matrixM20 = amount9;
      this.loftAmount = amount10;
      this.matrixM22 = amount11;
      this.translateZ = amount12;
      return this;
   }

   public float getAmountMatrixM00(float loftFactor, float loftFactor2, float minSide) {
      return this.matrixM0F * loftFactor + this.matrixMFloat * loftFactor2 + this.matrixM02 * minSide + this.translateX;
   }

   public AmountLoft getAmountAmountLoft(float loftValue, float loftValue2, float latheLoftValue) {
      this.matrixM0F *= loftValue;
      this.matrixM10 *= loftValue;
      this.matrixM20 *= loftValue;
      this.matrixMFloat *= loftValue2;
      this.matrixM11 *= loftValue2;
      this.loftAmount *= loftValue2;
      this.matrixM02 *= latheLoftValue;
      this.matrixM12 *= latheLoftValue;
      this.matrixM22 *= latheLoftValue;
      return this;
   }

   public AmountLoft getAmount3(float angle) {
      if (angle == 0.0F) {
         return this;
      } else {
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float amount = this.matrixM0F * cos + this.matrixMFloat * sin;
         this.matrixMFloat = this.matrixMFloat * cos - this.matrixM0F * sin;
         this.matrixM0F = amount;
         float amount2 = this.matrixM10 * cos + this.matrixM11 * sin;
         this.matrixM11 = this.matrixM11 * cos - this.matrixM10 * sin;
         this.matrixM10 = amount2;
         float amount3 = this.matrixM20 * cos + this.loftAmount * sin;
         this.loftAmount = this.loftAmount * cos - this.matrixM20 * sin;
         this.matrixM20 = amount3;
         return this;
      }
   }

   public AmountLoft getAmountTranslateX(float loftFactor, float armTrunkValue, float loftFactor3) {
      this.translateX = this.translateX + (this.matrixM0F * loftFactor + this.matrixMFloat * armTrunkValue + this.matrixM02 * loftFactor3);
      this.translateY = this.translateY + (this.matrixM10 * loftFactor + this.matrixM11 * armTrunkValue + this.matrixM12 * loftFactor3);
      this.translateZ = this.translateZ + (this.matrixM20 * loftFactor + this.loftAmount * armTrunkValue + this.matrixM22 * loftFactor3);
      return this;
   }
}
