package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.AmountLoft;
import com.corz.client.utils.ForeShinScale;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ForeEar;

public final class CorzclientWarm {
   public final float boundsMinZ;
   public final float scaleAmount;
   public final int[] corzclientWarmTable;
   public final float corzclientWarmAmount;
   public final int partIndex;
   public final float boundsMaxY;
   public final int[] corzclientValues;
   public final float[] personCustomTable;
   public final String corzclientWarmText;
   public final String[] corzclientWarmTexts;
   public final float boundsMaxX;
   public final int corzclientValue;
   public final int corzclientTextureValue;
   public final int corzclientWarmValue;
   public final int listSize;
   public final float[][] vectorEmitFloats;
   public final int personCustomValue;
   public final float boundsMaxZ;

   public boolean canFlag(int corzclientWarmValue, int vectorEmitValue) {
      int corzclientWarmValue2 = corzclientWarmValue;

      while (corzclientWarmValue2 >= 0) {
         if (corzclientWarmValue2 == vectorEmitValue) {
            return true;
         }

         corzclientWarmValue2 = this.corzclientWarmTable[corzclientWarmValue2];
      }

      return false;
   }

   private static AmountLoft getAmount(float[] corzclientWarmValues, int index, AmountLoft amountLoft) {
      amountLoft.matrixM0F = corzclientWarmValues[index];
      amountLoft.matrixMFloat = corzclientWarmValues[index + 1];
      amountLoft.matrixM02 = corzclientWarmValues[index + 2];
      amountLoft.translateX = corzclientWarmValues[index + 3];
      amountLoft.matrixM10 = corzclientWarmValues[index + 4];
      amountLoft.matrixM11 = corzclientWarmValues[index + 5];
      amountLoft.matrixM12 = corzclientWarmValues[index + 6];
      amountLoft.translateY = corzclientWarmValues[index + 7];
      amountLoft.matrixM20 = corzclientWarmValues[index + 8];
      amountLoft.loftAmount = corzclientWarmValues[index + 9];
      amountLoft.matrixM22 = corzclientWarmValues[index + 10];
      amountLoft.translateZ = corzclientWarmValues[index + 11];
      return amountLoft;
   }

   public float getMax() {
      if (this.personCustomValue < 0) {
         return 0.0F;
      } else {
         float corzclientWarmValue = this.personCustomTable[3 * this.personCustomValue + 1];
         float corzclientWarmBound = 0.0F;

         for (int i = 0; i < this.listSize; i++) {
            if (this.canFlag(i, this.personCustomValue)) {
               float[] corzclientWarmValues = this.vectorEmitFloats[i];

               for (int j = 0; j < corzclientWarmValues.length; j += 9) {
                  corzclientWarmBound = Math.max(corzclientWarmBound, corzclientWarmValue - corzclientWarmValues[j + 1]);
               }
            }
         }

         return corzclientWarmBound;
      }
   }

   public void handleAmounts(ForeShinScale foreShinScale, float[] corzclientWarmValues) {
      AmountLoft amountLoft = new AmountLoft();
      AmountLoft amountLoft2 = new AmountLoft();

      for (int i = 0; i < this.listSize; i++) {
         float corzclientWarmValue = this.personCustomTable[3 * i];
         float corzclientWarmValue2 = this.personCustomTable[3 * i + 1];
         float corzclientWarmValue3 = this.personCustomTable[3 * i + 2];
         amountLoft2.getAmount()
            .getAmountTranslateX(corzclientWarmValue + foreShinScale.offsetX[i], corzclientWarmValue2 + foreShinScale.foreShinTable[i], corzclientWarmValue3 + foreShinScale.offsetZ[i])
            .getAmount3(foreShinScale.table[i])
            .getAmount(foreShinScale.rotationZ[i])
            .getAmountForFloat(foreShinScale.rotationX[i])
            .getAmountAmountLoft(foreShinScale.scaleX[i], foreShinScale.scaleY[i], foreShinScale.scaleZ[i])
            .getAmountTranslateX(-corzclientWarmValue, -corzclientWarmValue2, -corzclientWarmValue3);
         int corzclientWarmValue4 = this.corzclientWarmTable[i];
         if (corzclientWarmValue4 < 0) {
            amountLoft.getAmount(amountLoft2);
         } else {
            getAmount(corzclientWarmValues, corzclientWarmValue4 * 21, amountLoft).getAmountForAmountLoft(amountLoft2);
         }

         amountLoft.handleAmount(corzclientWarmValues, i * 21);
         amountLoft.handleAmountForFloatArrayInt(corzclientWarmValues, i * 21 + 12);
      }
   }

   public int getTexts(String corzclientWarmText) {
      for (int i = 0; i < this.listSize; i++) {
         if (this.corzclientWarmTexts[i].equals(corzclientWarmText)) {
            return i;
         }
      }

      return -1;
   }

   CorzclientWarm(String text, LoftLatheFlag loftLatheFlag, int[] corzclientWarmValues, List<ForeEar> list, ForeEar foreEar) {
      this.corzclientWarmText = text;
      this.corzclientValue = loftLatheFlag.atlasWidth;
      this.corzclientTextureValue = loftLatheFlag.atlasHeight;
      this.corzclientValues = corzclientWarmValues != null ? corzclientWarmValues : loftLatheFlag.loftLatheTable;
      this.corzclientWarmValue = loftLatheFlag.getMax();
      this.listSize = list.size();
      this.corzclientWarmTexts = new String[this.listSize];
      this.corzclientWarmTable = new int[this.listSize];
      this.personCustomTable = new float[3 * this.listSize];
      this.vectorEmitFloats = new float[this.listSize][];
      int value = 0;
      float amount = Float.MAX_VALUE;
      float wrapDegreesBound = Float.MAX_VALUE;
      float maxValue3 = Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue6 = -Float.MAX_VALUE;

      for (int i = 0; i < this.listSize; i++) {
         ForeEar atla = (ForeEar)list.get(i);
         this.corzclientWarmTexts[i] = atla.foreEarText;
         this.corzclientWarmTable[i] = atla.foreEar == null ? -1 : atla.foreEar.boneIndex;
         if (this.corzclientWarmTable[i] >= i) {
            throw new IllegalStateException("bone " + atla.foreEarText + " declared before its parent");
         }

         this.personCustomTable[3 * i] = atla.offsetX;
         this.personCustomTable[3 * i + 1] = atla.foreEarAmount;
         this.personCustomTable[3 * i + 2] = atla.shinFootAmount;
         this.vectorEmitFloats[i] = atla.rootTransform.getCopyOf();
         value += atla.rootTransform.getValue();
         float[] corzclientWarmValues2 = this.vectorEmitFloats[i];

         for (int j = 0; j < corzclientWarmValues2.length; j += 9) {
            amount = Math.min(amount, corzclientWarmValues2[j]);
            maxValue4 = Math.max(maxValue4, corzclientWarmValues2[j]);
            wrapDegreesBound = Math.min(wrapDegreesBound, corzclientWarmValues2[j + 1]);
            pilotStatusBound = Math.max(pilotStatusBound, corzclientWarmValues2[j + 1]);
            maxValue3 = Math.min(maxValue3, corzclientWarmValues2[j + 2]);
            maxValue6 = Math.max(maxValue6, corzclientWarmValues2[j + 2]);
         }
      }

      this.partIndex = value;
      this.personCustomValue = foreEar == null ? -1 : foreEar.boneIndex;
      if (value == 0) {
         maxValue6 = 0.0F;
         pilotStatusBound = 0.0F;
         maxValue4 = 0.0F;
         maxValue3 = 0.0F;
         wrapDegreesBound = 0.0F;
         amount = 0.0F;
      }

      this.boundsMaxX = amount;
      this.corzclientWarmAmount = wrapDegreesBound;
      this.boundsMinZ = maxValue3;
      this.scaleAmount = maxValue4;
      this.boundsMaxY = pilotStatusBound;
      this.boundsMaxZ = maxValue6;
   }

   public ForeShinScale getListSize() {
      return new ForeShinScale(this.listSize);
   }

   public float[] getAmounts() {
      return new float[this.listSize * 21];
   }
}
