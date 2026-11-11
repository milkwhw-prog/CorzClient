package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.ForeEar;

public final class ForeShinScale {
   final float[] foreShinTable;
   public final int boneCount;
   final float[] table;
   final float[] scaleY;
   final float[] rotationX;
   final float[] scaleZ;
   final float[] scaleX;
   final float[] offsetX;
   final float[] offsetZ;
   final float[] rotationZ;

   public ForeShinScale(int value) {
      this.boneCount = value;
      this.rotationX = new float[value];
      this.rotationZ = new float[value];
      this.table = new float[value];
      this.offsetX = new float[value];
      this.foreShinTable = new float[value];
      this.offsetZ = new float[value];
      this.scaleX = new float[value];
      this.scaleY = new float[value];
      this.scaleZ = new float[value];
      this.getAmounts();
   }

   public ForeShinScale getAmounts() {
      Arrays.fill(this.rotationX, 0.0F);
      Arrays.fill(this.rotationZ, 0.0F);
      Arrays.fill(this.table, 0.0F);
      Arrays.fill(this.offsetX, 0.0F);
      Arrays.fill(this.foreShinTable, 0.0F);
      Arrays.fill(this.offsetZ, 0.0F);
      Arrays.fill(this.scaleX, 1.0F);
      Arrays.fill(this.scaleY, 1.0F);
      Arrays.fill(this.scaleZ, 1.0F);
      return this;
   }

   public ForeShinScale getAmounts(ForeEar foreEar, float foreShinValue, float dinForeValue, float angle) {
      if (foreEar != null && foreEar.boneIndex < this.boneCount) {
         this.rotationX[foreEar.boneIndex] = foreShinValue;
         this.rotationZ[foreEar.boneIndex] = dinForeValue;
         this.table[foreEar.boneIndex] = angle;
         return this;
      } else {
         return this;
      }
   }

   public ForeShinScale getAmountsForForeEarFloat(ForeEar foreEar, float foreShinFactor, float shinProgressDivisor, float classValue) {
      if (foreEar != null && foreEar.boneIndex < this.boneCount) {
         this.scaleX[foreEar.boneIndex] = this.scaleX[foreEar.boneIndex] * foreShinFactor;
         this.scaleY[foreEar.boneIndex] = this.scaleY[foreEar.boneIndex] * shinProgressDivisor;
         this.scaleZ[foreEar.boneIndex] = this.scaleZ[foreEar.boneIndex] * classValue;
         return this;
      } else {
         return this;
      }
   }

   public ForeShinScale getAmountsBoneCount(ForeEar foreEar, float foreShinValue, float angle, float foreShinValue3) {
      if (foreEar != null && foreEar.boneIndex < this.boneCount) {
         this.rotationX[foreEar.boneIndex] = this.rotationX[foreEar.boneIndex] + foreShinValue;
         this.rotationZ[foreEar.boneIndex] = this.rotationZ[foreEar.boneIndex] + angle;
         this.table[foreEar.boneIndex] = this.table[foreEar.boneIndex] + foreShinValue3;
         return this;
      } else {
         return this;
      }
   }

   public ForeShinScale getAmountsForeShin2(ForeEar foreEar, float foreShinValue, float shinProgressTotal, float shinProgressValue) {
      if (foreEar != null && foreEar.boneIndex < this.boneCount) {
         this.offsetX[foreEar.boneIndex] = this.offsetX[foreEar.boneIndex] + foreShinValue;
         this.foreShinTable[foreEar.boneIndex] = this.foreShinTable[foreEar.boneIndex] + shinProgressTotal;
         this.offsetZ[foreEar.boneIndex] = this.offsetZ[foreEar.boneIndex] + shinProgressValue;
         return this;
      } else {
         return this;
      }
   }

   public ForeShinScale getClassForForeEarFloat(ForeEar foreEar, float classValue) {
      return this.getAmountsForForeEarFloat(foreEar, classValue, classValue, classValue);
   }
}
