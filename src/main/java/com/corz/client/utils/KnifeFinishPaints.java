package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.KnifeFinishKit;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeFinishBake;

final class KnifeFinishPaints {
   static final int SINGLE_STEP = 1;
   static final int PALE_BLUE_GRAY_COLOR = KnifeFinishKit.getValue(11712706);
   static final int knifeFinishValue = KnifeFinishKit.getValue(13883615);
   static final int DARK_GRAY_COLOR = KnifeFinishKit.getValue(2763568);
   static final KnifeFinishKit.Ramp RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.5F, 1.0F}, new int[]{KnifeFinishKit.getValue(8128530), KnifeFinishKit.getValue(11079706), KnifeFinishKit.getValue(13115946)}
   );
   static final int NEAR_BLACK_COLOR = KnifeFinishKit.getValue(1049606);
   static final int CORAL_COLOR = KnifeFinishKit.getValue(16747640);
   static final KnifeFinishKit.Ramp BASE_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.5F, 1.0F}, new int[]{KnifeFinishKit.getValue(9046293), KnifeFinishKit.getValue(12325150), KnifeFinishKit.getValue(14428203)}
   );
   static final int CREAM_COLOR = KnifeFinishKit.getValue(15982806);
   static final int WARM_WHITE_COLOR = KnifeFinishKit.getValue(16774382);
   static final int DARK_RED_COLOR = KnifeFinishKit.getValue(5899792);
   static final int PEACH_COLOR = KnifeFinishKit.getValue(16757914);
   static final int RAMP_MID_SHADE = KnifeFinishKit.getValue3(KnifeFinishPaints.BASE_RAMP.getStopsRamp(0.5F), 0.9F);
   private static final float[][] rigKnifeFloats = new float[][]{
      {6.3F, 12.0F, 12.5F, 13.0F, 13.5F, 13.75F, 14.0F, 14.25F, 14.5F, 14.75F, 15.0F, 15.25F, 15.5F, 15.75F, 16.0F, 16.5F, 17.0F, 17.5F, 17.75F},
      {6.64F, 6.64F, 6.64F, 6.64F, 6.74F, 6.82F, 6.98F, 7.17F, 7.38F, 7.57F, 7.74F, 7.87F, 7.95F, 8.01F, 8.07F, 8.1F, 8.03F, 7.92F, 7.9F},
      {9.61F, 9.61F, 9.58F, 9.55F, 9.51F, 9.49F, 9.46F, 9.43F, 9.39F, 9.35F, 9.3F, 9.24F, 9.17F, 9.09F, 9.01F, 8.81F, 8.56F, 8.21F, 8.01F}
   };
   private static final float[][] knifeFinishFloats = new float[][]{
      {7.05F, 11.5F, 12.0F, 12.5F, 13.0F, 13.5F, 14.0F, 14.5F, 15.0F, 15.5F, 16.0F, 16.25F},
      {7.43F, 7.43F, 7.45F, 7.47F, 7.5F, 7.54F, 7.59F, 7.65F, 7.72F, 7.82F, 7.92F, 7.93F},
      {8.69F, 8.69F, 8.68F, 8.65F, 8.63F, 8.59F, 8.54F, 8.46F, 8.37F, 8.26F, 8.12F, 8.09F}
   };
   private static final float[][] floats = new float[][]{
      {6.95F, 11.0F, 11.5F, 12.0F, 12.5F, 13.0F, 13.5F, 14.0F, 14.5F, 15.0F, 15.5F, 16.0F, 16.25F},
      {7.05F, 7.05F, 7.06F, 7.12F, 7.21F, 7.28F, 7.32F, 7.33F, 7.33F, 7.32F, 7.29F, 7.29F, 7.29F},
      {8.94F, 8.94F, 8.93F, 8.91F, 8.88F, 8.82F, 8.74F, 8.62F, 8.47F, 8.26F, 7.98F, 7.58F, 7.55F}
   };
   private static final float[][] FIRST_PERSON_RIG_BOUNDS = new float[][]{{-3.05F, 3.3F}, {7.03F, 7.03F}, {8.97F, 8.97F}};
   static final KnifeFinishKit.Ramp SPECULAR_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.45F, 1.0F}, new int[]{KnifeFinishKit.getValue(1579293), KnifeFinishKit.getValue(3158842), KnifeFinishKit.getValue(4541010)}
   );
   static final KnifeFinishKit.Ramp HIGHLIGHT_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.5F, 1.0F}, new int[]{KnifeFinishKit.getValue(2429754), KnifeFinishKit.getValue(3809880), KnifeFinishKit.getValue(5321334)}
   );
   static final int PURPLE_COLOR = KnifeFinishKit.getValue(9202384);
   static final KnifeFinishKit.Ramp RIG_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.25F, 0.5F, 0.75F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(1838343),
         KnifeFinishKit.getValue(3808013),
         KnifeFinishKit.getValue(6040338),
         KnifeFinishKit.getValue(8535833),
         KnifeFinishKit.getValue(11034154)
      }
   );
   static final int SLATE_COLOR = KnifeFinishKit.getValue(5398638);
   static final int STEEL_COLOR = KnifeFinishKit.getValue(9082530);
   static final int BROWN_COLOR = KnifeFinishKit.getValue(6961696);
   static final int GRAY_TEAL_COLOR = KnifeFinishKit.getValue(8817295);
   static final int DARK_BROWN_COLOR = KnifeFinishKit.getValue(1313028);
   static final int COPPER_COLOR = KnifeFinishKit.getValue(12084524);
   static final int OLIVE_COLOR = KnifeFinishKit.getValue(6779978);
   static final int TAN_COLOR = KnifeFinishKit.getValue(10524268);
   static final int LEATHER_BROWN_COLOR = KnifeFinishKit.getValue(5980977);
   static final int CHARCOAL_COLOR = KnifeFinishKit.getValue(2237214);
   static final int DARK_OLIVE_COLOR = KnifeFinishKit.getValue(5921860);

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = knifeFinishValue + 0.9F * KnifeFinishKit.getAmount3(knifeFinishValue * 0.5F, knifeFinishValue2 * 0.5F, 2, knifeFinishValue3 + 3);
      float knifeFinishValue5 = knifeFinishValue2 + 0.9F * KnifeFinishKit.getAmount3(knifeFinishValue * 0.5F + 4.3F, knifeFinishValue2 * 0.5F - 2.1F, 2, knifeFinishValue3 + 4);
      return KnifeFinishKit.getAmount3(knifeFinishValue4 * 0.62F, knifeFinishValue5 * 0.34F - 0.18F * knifeFinishValue4, 3, knifeFinishValue3);
   }

   static float getAmount(KnifeFinish.Texel texel) {
      float knifeFinishFactor = (texel.rampAmount - 7.5F) / 0.24F;
      float knifeFinishFactor2 = (texel.texelY - 10.5F) / 0.58F;
      float knifeFinishFactor3 = knifeFinishFactor / 0.24F;
      float knifeFinishFactor4 = knifeFinishFactor2 / 0.58F;
      float distance = (float)Math.sqrt(knifeFinishFactor3 * knifeFinishFactor3 + knifeFinishFactor4 * knifeFinishFactor4);
      return distance < 0.001F ? -0.24F : (knifeFinishFactor * knifeFinishFactor + knifeFinishFactor2 * knifeFinishFactor2 - 1.0F) / (2.0F * distance);
   }

   static List<KnifeFinish.Finish> getItems() {
      return List.of(getBuild4(), getBuild3(), getBuild5(), getBuild(), getBuild6(), getBuildFinish());
   }

   private static float getAmount(int knifeFinishValue, int i, int rowsSlotsLimit, float knifeFinishFactor, int knifeFinishValue4) {
      return knifeFinishFactor * (knifeFinishValue + 0.7F + 0.42F * (KnifeFinishKit.getAmount(knifeFinishValue, Math.floorMod(i, rowsSlotsLimit), knifeFinishValue4 + 3) - 0.5F)) * (1.0F + 0.025F * knifeFinishValue);
   }

   static KnifeFinish.Finish getBuild() {
      return getPaintSapphire(KnifeFinish.getBuilder("Ultraviolet"), KnifeFinishPaints::getAmountRamp, 0.04F).recolor(KnifeFinish.getValue(7), 8159368).build();
   }

   static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishValue2, float rigFactor) {
      float knifeFinishFactor = 1.0F + 0.035F * rigFactor * KnifeFinishKit.getAmountForTexelFloatInt(texel, 1.6F, texel.texelSeed + 5);
      int value3 = KnifeFinishKit.getValue3(knifeFinishValue, knifeFinishFactor * (0.95F + 0.3F * (KnifeFinishKit.getAmount(texel) - 0.62F) * (1.0F - knifeFinishValue2)));
      return KnifeFinishKit.getValueInt(value3, KnifeFinishKit.getValue4(knifeFinishValue, 0.35F), (1.0F - knifeFinishValue2) * 0.18F * KnifeFinishKit.getAmountForTexel(texel));
   }

   static KnifeFinish.Finish getBuildFinish() {
      return getPaintSapphire(KnifeFinish.getBuilder("Forest Camo"), KnifeFinishPaints::getValue, 0.05F).recolor(KnifeFinish.getValue(7), 8159368).build();
   }

   static float getAmount(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue3, float knifeFinishValue4, int knifeFinishValue5) {
      return knifeFinishValue == 3 ? -1.0F : Math.max(getAmountForTexelFloat(texel, knifeFinishFactor, knifeFinishValue5), getAmount(texel, knifeFinishValue3, knifeFinishValue4, knifeFinishValue5)) - getRigKnife(texel);
   }

   static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, int knifeFinishValue2, float knifeFinishFactor) {
      float knifeFinishValue4 = knifeFinishValue2 == 2 ? 0.0F : 1.0F - getAmountFloat(texel);
      boolean knifeFinishFlag = knifeFinishValue2 == 1;
      int knifeFinishValue5 = getValue(texel, getAmount(texel, knifeFinishValue4, knifeFinishFlag), 0.78F, knifeFinishFlag ? 0.0F : 1.0F);
      if (knifeFinishValue2 == 1) {
         return KnifeFinishKit.getValue3(knifeFinishValue5, 0.55F);
      } else if (knifeFinishValue2 == 2) {
         return knifeFinishValue5;
      } else {
         int knifeFinishValue6 = KnifeFinishKit.getValue(knifeFinishValue5, KnifeFinishKit.getValue4(knifeFinishValue5, 0.3F), 0.6F * getAmountForTexelFloatInt(texel, 1.0F, 0.3F, texel.texelSeed + 61));
         return getAmount(texel, knifeFinishValue6, getAmount(texel, knifeFinishValue2, knifeFinishFactor, 0.45F, 0.1F, texel.texelSeed), getAmount(texel, 0.075F, texel.texelSeed));
      }
   }

   static float getInstance(KnifeRig knifeRig, int knifeFinishValue) {
      if (knifeFinishValue == 0) {
         return knifeRig == KnifeRig.M9_BAYONET_TP ? 12.3F : (knifeRig == KnifeRig.STILETTO_TP ? 10.4F : 10.6F);
      } else {
         return knifeRig == KnifeRig.M9_BAYONET_TP ? 2.3F : (knifeRig == KnifeRig.STILETTO_TP ? 0.9F : -0.3F);
      }
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, int knifeFinishValue) {
      if (texel.rigKnifeValue != 1) {
         return 0.0F;
      } else {
         float min2 = Math.min(Math.max(0.0F, texel.edgeDistance), texel.rigAmount <= -1.0E9F ? 1000.0F : Math.max(0.0F, texel.rigAmount));
         float knifeFinishValue3 = knifeFinishFactor * (0.8F + 0.4F * KnifeFinishKit.getAmount4(texel.rampAmount * 3.0F, texel.texelY * 3.0F, knifeFinishValue + 71));
         return 1.0F - KnifeFinishKit.getAmountForFloat(knifeFinishValue3 - texel.texelSize, knifeFinishValue3 + texel.texelSize, min2);
      }
   }

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3) {
      return 1.0F - KnifeFinishKit.getAmountForFloat(knifeFinishValue2 * 0.5F - knifeFinishValue3 * 0.6F, knifeFinishValue2 * 0.5F + knifeFinishValue3 * 0.6F, knifeFinishValue);
   }

   static int getAmountRig(KnifeFinish.Texel texel, int rigValue, int rigValue2, float knifeFinishFactor) {
      boolean rigFlag = rigValue2 == 2 && texel.knifeRig == KnifeRig.M9_BAYONET_TP;
      boolean rigFlag2 = rigValue2 == 2 && !rigFlag;
      boolean rigFlag3 = rigFlag2 || rigValue2 == 1;
      float rigValue4 = rigValue2 != 2 && rigValue2 != 1 ? getAmountFloat(texel) : 1.0F;
      float rigFactor = 1.0F - rigValue4;
      float rigFactor2 = rigFlag ? 1.0F - getAmountFloat(texel) : rigFactor;
      float rigValue5 = texel.rigAmount <= -1.0E9F ? 2.0F : Math.max(0.0F, texel.rigAmount);
      float amount3 = KnifeFinishKit.getAmount3(texel.rampAmount * 0.6F, texel.texelY * 0.42F, 4, texel.texelSeed);
      float rigFactor3 = KnifeFinishKit.getAmount3(texel.rampAmount * 2.8F, texel.texelY * 2.8F, 3, texel.texelSeed + 7);
      float amountForFloatInt = KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 26.0F, texel.texelY * 26.0F, texel.texelSeed + 11);
      float rigValue7 = rigFactor2 * rigFactor3;
      float rigValue8 = rigFactor * amountForFloatInt;
      float rigValue9 = rigFactor * (KnifeFinishKit.getAmount((int)Math.floor(texel.rampAmount * 90.0F), (int)Math.floor(texel.texelY * 90.0F), texel.texelSeed + 13) - 0.5F);
      float rigFactor5 = rigValue == 2
         ? 0.3F
         : (
            rigValue2 != 3 && !rigFlag
               ? 0.28F * KnifeFinishKit.getAmountForFloat(-0.1F, 0.15F, KnifeFinishKit.getAmount3(texel.rampAmount * 1.3F, texel.texelY * 1.3F, 2, texel.texelSeed + 27))
               : 0.12F
         );
      float rigValue10 = amount3
         + 0.22F * rigValue7
         + 0.04F * (rigFactor2 * amountForFloatInt)
         + rigFactor5 * (1.0F - KnifeFinishKit.getAmountForFloat(0.0F, 0.32F, rigValue5))
         - (rigValue2 == 1 ? 0.22F : (rigValue == 2 ? 0.01F : 0.1F));
      float knifeFinishLimit = rigFlag3 ? (rigFlag2 ? 1.0F : 0.65F) * KnifeFinishKit.getAmountForFloat(-0.05F, 0.05F, rigValue10) : KnifeFinishKit.getAmountForFloat(-0.012F, 0.012F, rigValue10);
      float rigValue12 = KnifeFinishKit.getAmountForFloat(0.05F, 0.45F, rigValue10) * (rigFlag3 ? 0.4F : 1.0F);
      float stopsRampLimit = KnifeFinishKit.getMax(
         0.6F + 0.42F * rigValue7 + 0.22F * rigValue8 + 0.16F * rigValue9 - 0.34F * rigValue12 + 0.2F * KnifeFinishKit.getAmountFloat(rigValue10, 0.03F, 0.05F)
      );
      int stopsRamp = RIG_RAMP.getStopsRamp(stopsRampLimit);
      float rigLimit = rigFactor
         * KnifeFinishKit.getAmountForFloat(0.52F, 0.6F, KnifeFinishKit.getAmount4(texel.rampAmount * 6.0F + 0.5F, texel.texelY * 6.0F + 0.5F, texel.texelSeed + 23))
         * KnifeFinishKit.getAmountForFloat(0.0F, 0.15F, rigValue10);
      if (rigLimit > 0.0F) {
         float rigValue14 = KnifeFinishKit.getAmount(texel.rampAmount * 6.0F, texel.texelY * 6.0F, texel.texelSeed + 19);
         float rigValue15 = KnifeFinishKit.getAmount3(texel.rampAmount * 6.0F, texel.texelY * 6.0F, texel.texelSeed + 19);
         float rigValue16 = 0.07F + 0.08F * rigValue15;
         float rigFactor6 = KnifeFinishKit.getAmount(rigValue15 * 7.31F) < 0.5F ? 0.45F : 0.0F;
         int rigValue17 = KnifeFinishKit.getValue(stopsRamp, COPPER_COLOR, rigFactor6 * rigLimit * KnifeFinishKit.getAmountFloat(rigValue14, rigValue16 + 0.07F, 0.04F + 0.02F * rigValue15));
         stopsRamp = KnifeFinishKit.getValue(rigValue17, DARK_BROWN_COLOR, 0.85F * rigLimit * (1.0F - KnifeFinishKit.getAmountForFloat(rigValue16 - 0.03F, rigValue16 + 0.03F, rigValue14)));
      }

      int value3 = KnifeFinishKit.getValue3(SLATE_COLOR, 0.93F + 0.14F * rigValue7);
      float rigValue19 = rigFactor * (1.0F - knifeFinishLimit) * KnifeFinishKit.getAmountFloat(rigValue10, -0.02F, 0.035F);
      int rigValue20 = KnifeFinishKit.getValue(value3, STEEL_COLOR, 0.45F * KnifeFinishKit.getAmountFloat(rigValue10, -0.012F, 0.008F) * rigFactor);
      int rigValue21 = KnifeFinishKit.getValue(rigValue20, BROWN_COLOR, 0.4F * rigValue19);
      int amountRig = KnifeFinishKit.getValue(
         getValue(texel, rigValue21, 0.5F, rigFactor), getValue(texel, rigFlag2 ? KnifeFinishKit.getValue(BROWN_COLOR, stopsRamp, 0.65F) : stopsRamp, 0.97F, rigFactor), knifeFinishLimit
      );
      if (rigValue2 == 1) {
         return KnifeFinishKit.getValue3(amountRig, 0.55F);
      } else if (rigValue2 == 2) {
         return amountRig;
      } else {
         float rigValue22 = (rigValue2 == 3 ? -1.0F : getAmountForTexelFloat(texel, knifeFinishFactor, texel.texelSeed) - getRigKnife(texel)) - 0.3F * knifeFinishLimit;
         float max23 = Math.max(getAmount(texel, rigValue22), getAmount(texel, 0.06F, texel.texelSeed) * (1.0F - 0.6F * knifeFinishLimit));
         if (max23 <= 0.0F) {
            return amountRig;
         } else {
            int rigValue24 = KnifeFinishKit.getValue(
               getValueForTexel(texel), KnifeFinishKit.getValue(texel, GRAY_TEAL_COLOR, 0.5F, 1.0F), 0.5F * KnifeFinishKit.getAmountForFloat(-0.3F, 0.2F, rigValue7)
            );
            return KnifeFinishKit.getValue(amountRig, rigValue24, max23);
         }
      }
   }

   static float getRigKnife(KnifeFinish.Texel texel, float rigKnifeValue, float rigKnifeValue2, float knifeFinishFactor) {
      boolean rigKnifeFlag = texel.rigKnifeValue == 3;
      if (rigKnifeFlag && texel.knifeRig != KnifeRig.STILETTO_TP) {
         return 0.0F;
      } else {
         float rigKnifeBound = getAmount(
            rigKnifeFlag ? FIRST_PERSON_RIG_BOUNDS : (texel.knifeRig == KnifeRig.M9_BAYONET_TP ? rigKnifeFloats : (texel.knifeRig == KnifeRig.STILETTO_TP ? knifeFinishFloats : floats)), rigKnifeValue, rigKnifeValue2, knifeFinishFactor
         );
         if (!rigKnifeFlag && texel.knifeRig == KnifeRig.M9_BAYONET_TP) {
            rigKnifeBound = Math.min(rigKnifeBound, Math.min(getAmount(rigKnifeValue, rigKnifeValue2, 7.02F, 7.54F, 7.5F, 9.71F), getAmount(rigKnifeValue, rigKnifeValue2, 7.37F, 7.63F, 10.11F, 10.89F)) - knifeFinishFactor);
         }

         if (!rigKnifeFlag && texel.knifeRig == KnifeRig.BUTTERFLY_TP) {
            rigKnifeBound = Math.min(rigKnifeBound, getAmount(rigKnifeValue, rigKnifeValue2, 7.4F, 7.87F, 0.14F) - knifeFinishFactor);
         }

         if (rigKnifeFlag) {
            rigKnifeBound = Math.min(rigKnifeBound, Math.min(getAmount(rigKnifeValue, rigKnifeValue2, 8.0F, -2.12F, 0.12F), getAmount(rigKnifeValue, rigKnifeValue2, 8.0F, 1.21F, 0.12F)) - knifeFinishFactor);
         }

         return KnifeFinishKit.getAmountForFloat(0.03F, 0.07F, rigKnifeBound);
      }
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue, float knifeFinishValue2, int knifeFinishDivisor, float knifeFinishFactor, float knifeFinishFactor2, float knifeFinishFactor3, int knifeFinishValue3) {
      float knifeFinishValue4 = texel.rampAmount + 0.1F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 0.45F, texel.texelY * 0.45F, knifeFinishValue3 + 1);
      float knifeFinishValue5 = texel.texelY + 0.1F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 0.45F + 7.1F, texel.texelY * 0.45F, knifeFinishValue3 + 2);
      float x = knifeFinishValue4 - knifeFinishValue;
      float y = knifeFinishValue5 - knifeFinishValue2;
      float distance = (float)Math.sqrt(x * x + y * y);
      if (distance < 1.0E-4F) {
         return 1.0F;
      } else {
         float angle = (float)Math.atan2(y, x);
         float knifeFinishLimit = (angle / (float) (Math.PI * 2) + 0.5F) * knifeFinishDivisor;
         int floor6 = (int)Math.floor(knifeFinishLimit);
         int knifeFinishValue7 = getAmount(floor6, knifeFinishDivisor, knifeFinishValue3) <= knifeFinishLimit ? floor6 : floor6 - 1;
         float knifeFinishFactor4 = getAmount(knifeFinishValue7, knifeFinishDivisor, knifeFinishValue3);
         float knifeFinishFactor5 = getAmount(knifeFinishValue7 + 1, knifeFinishDivisor, knifeFinishValue3);
         float knifeFinishValue8 = knifeFinishFactor4 * (float) (Math.PI * 2) / knifeFinishDivisor - (float) Math.PI;
         float knifeFinishValue9 = knifeFinishFactor5 * (float) (Math.PI * 2) / knifeFinishDivisor - (float) Math.PI;
         float sin = (float)Math.sin(angle - knifeFinishValue8);
         float planValuesItem = (float)Math.sin(knifeFinishValue9 - angle);
         float sin3 = (float)Math.sin(knifeFinishValue9 - knifeFinishValue8);
         float knifeFinishValue10 = Math.min(Math.abs(sin), Math.abs(planValuesItem)) * distance;
         float knifeFinishFactor6 = 0.78F + 0.44F * KnifeFinishKit.getAmount4(texel.rampAmount * 2.3F, texel.texelY * 2.3F, knifeFinishValue3 + 5);
         float knifeFinishBound = getAmount(knifeFinishValue10, knifeFinishFactor2 * knifeFinishFactor6, texel.texelSize);
         float max11 = KnifeFinishKit.getMax((knifeFinishLimit - knifeFinishFactor4) / Math.max(1.0E-4F, knifeFinishFactor5 - knifeFinishFactor4));
         float knifeFinishFactor7 = 1.0F - 0.34F * max11 * (1.0F - max11);
         float knifeFinishFactor8 = (float)Math.cos((knifeFinishValue9 - knifeFinishValue8) * 0.5F) / Math.max(0.3F, (float)Math.cos(angle - 0.5F * (knifeFinishValue8 + knifeFinishValue9)));
         float hashRegistryValue = distance / (knifeFinishFactor * knifeFinishFactor8 * knifeFinishFactor7) - 0.7F;
         int floor13 = (int)Math.floor(hashRegistryValue);
         float wrapDegreesBound = 1.0E9F;

         for (int i = Math.max(0, floor13 - 1); i <= floor13 + 2; i++) {
            float knifeFinishFactor9 = getAmount(i, knifeFinishValue7, knifeFinishDivisor, knifeFinishFactor, knifeFinishValue3);
            float knifeFinishFactor10 = getAmount(i, knifeFinishValue7 + 1, knifeFinishDivisor, knifeFinishFactor, knifeFinishValue3);
            float knifeFinishDivisor2 = knifeFinishFactor9 * sin + knifeFinishFactor10 * planValuesItem;
            if (!(knifeFinishDivisor2 <= 1.0E-5F)) {
               float knifeFinishValue14 = knifeFinishFactor9 * knifeFinishFactor10 * sin3 / knifeFinishDivisor2 * knifeFinishFactor7;
               wrapDegreesBound = Math.min(wrapDegreesBound, Math.abs(distance - knifeFinishValue14));
            }
         }

         return Math.max(knifeFinishBound, getAmount(wrapDegreesBound, knifeFinishFactor3 * knifeFinishFactor6, texel.texelSize));
      }
   }

   static int getRigKnife(KnifeFinish.Texel texel, int rigKnifeValue, int rigKnifeValue2, float knifeFinishFactor) {
      if (rigKnifeValue == 2 && texel.knifeRig == KnifeRig.BUTTERFLY_TP) {
         return getValueDARK_GRAY_COLOR(texel);
      } else {
         boolean rigKnifeFlag = rigKnifeValue == 0 || rigKnifeValue == 1 && texel.knifeRig == KnifeRig.STILETTO_TP;
         int amountRamp = getAmountRamp(
            texel,
            (rigKnifeValue2 == 0 || rigKnifeValue2 == 3) && getAmountFloat(texel) < 0.5F,
            rigKnifeFlag,
            texel.knifeRig == KnifeRig.M9_BAYONET_TP && rigKnifeValue == 0,
            rigKnifeValue == 1 && texel.knifeRig == KnifeRig.BUTTERFLY_TP ? 0.06F : 0.12F
         );
         int rigKnifeValue4 = KnifeFinishKit.getValue(texel, amountRamp, 0.85F, KnifeFinishKit.getValue(amountRamp, PEACH_COLOR, 0.6F));
         if (rigKnifeValue2 == 1) {
            return KnifeFinishKit.getValue3(rigKnifeValue4, 0.55F);
         } else {
            return rigKnifeValue2 == 2 ? rigKnifeValue4 : getAmount(texel, rigKnifeValue4, getAmount(texel, rigKnifeValue2, knifeFinishFactor, 0.25F, 0.0F, texel.texelSeed), getAmount(texel, 0.065F, texel.texelSeed));
         }
      }
   }

   static int getAmount(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishValue2, float knifeFinishBound) {
      float pilotStatusBound = getAmount(texel, knifeFinishValue2);
      int value3 = KnifeFinishKit.getValue3(knifeFinishValue, 1.0F - 0.3F * KnifeFinishKit.getAmountFloat(knifeFinishValue2, -texel.texelSize * 1.6F, texel.texelSize * 1.6F) * (1.0F - pilotStatusBound));
      float maxField = Math.max(pilotStatusBound, knifeFinishBound);
      return maxField <= 0.0F ? value3 : KnifeFinishKit.getValue(value3, getValueForTexel(texel), maxField);
   }

   static float getInstanceForKnifeRigInt(KnifeRig knifeRig, int knifeFinishValue) {
      if (knifeFinishValue == 0) {
         return knifeRig == KnifeRig.M9_BAYONET_TP ? 7.75F : 8.0F;
      } else {
         return knifeRig == KnifeRig.BUTTERFLY_TP ? 8.62F : 8.05F;
      }
   }

   static float getAmountForTexel(KnifeFinish.Texel texel) {
      float knifeFinishFactor = Math.max(0.0F, Math.abs(texel.rampAmount - 7.5F) - 0.13F);
      float max2 = Math.max(0.0F, Math.abs(texel.texelY - 10.5F) - 0.39F);
      return KnifeFinishKit.getAmountForFloat(0.02F, 0.07F, (float)Math.sqrt(knifeFinishFactor * knifeFinishFactor + max2 * max2));
   }

   static float getAmountFloat(KnifeFinish.Texel texel) {
      return texel.rigAmount <= -1.0E9F ? 0.0F : (texel.rigAmount < 0.0F ? 1.0F : 1.0F - KnifeFinishKit.getAmountForFloat(1.5F * texel.texelSize, 4.0F * texel.texelSize, texel.rigAmount));
   }

   static int getValueForTexel(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getValue(texel, texel.rigKnifeValue == 1 ? knifeFinishValue : PALE_BLUE_GRAY_COLOR, 0.85F, 1.0F);
   }

   static KnifeFinish.Finish getBuild3() {
      return getPaintSapphire(KnifeFinish.getBuilder("Crimson Lace"), KnifeFinishPaints::getRigKnife, 0.035F)
         .recolor(KnifeFinish.getValue(7), 9212056)
         .glow(KnifeFinish.fadeSapphireValue, texel -> KnifeFinishKit.getValueForTexelIntFloat(texel, KnifeFinishKit.getValue(16743018), 0.12F))
         .build();
   }

   static int getValueDARK_GRAY_COLOR(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getValue(texel, DARK_GRAY_COLOR, 0.6F, 0.4F);
   }

   private static float getAmount(int knifeFinishValue, int knifeFinishDivisor, int knifeFinishValue3) {
      return knifeFinishValue + 0.5F + 0.5F * (KnifeFinishKit.getAmount(Math.floorMod(knifeFinishValue, knifeFinishDivisor), 0, knifeFinishValue3) - 0.5F);
   }

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3, float knifeFinishValue4, float knifeFinishValue5) {
      float knifeFinishFactor = knifeFinishValue - knifeFinishValue3;
      float knifeFinishFactor2 = knifeFinishValue2 - knifeFinishValue4;
      return (float)Math.sqrt(knifeFinishFactor * knifeFinishFactor + knifeFinishFactor2 * knifeFinishFactor2) - knifeFinishValue5;
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue, float knifeFinishValue2, int knifeFinishValue3) {
      if (!(texel.rigAmount <= -1.0E9F) && !(knifeFinishValue <= 0.0F)) {
         float max4 = Math.max(0.0F, texel.rigAmount);
         float knifeFinishValue5 = KnifeFinishKit.getAmount3(texel.rampAmount * 5.5F, texel.texelY * 5.5F, 3, knifeFinishValue3 + 43)
            + 0.1F
               * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 31.0F, texel.texelY * 31.0F, knifeFinishValue3 + 47)
               * KnifeFinishKit.getAmountForFloat(2.0F * texel.texelSize, 5.0F * texel.texelSize, max4);
         float knifeFinishValue6 = 0.6F - 0.22F * knifeFinishValue + (0.95F - 0.45F * knifeFinishValue2) * KnifeFinishKit.getAmountForFloat(0.04F, 0.5F, max4);
         return (knifeFinishValue5 - knifeFinishValue6) * 0.3F;
      } else {
         return -1.0F;
      }
   }

   private KnifeFinishPaints() {
   }

   static int getAmount(KnifeFinish.Texel texel, float knifeFinishValue, boolean knifeFinishFlag) {
      float knifeFinishValue2 = texel.rampAmount * 0.85F;
      float seedScaled = texel.texelY * 0.42F;
      float knifeFinishValue4 = knifeFinishValue * 0.022F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 40.0F, texel.texelY * 40.0F, texel.texelSeed + 3);
      float knifeFinishValue5 = KnifeFinishKit.getAmount3(knifeFinishValue2, seedScaled, 4, texel.texelSeed + 1) + knifeFinishValue4;
      float knifeFinishValue6 = KnifeFinishKit.getAmount3(knifeFinishValue2 + 3.7F, seedScaled - 1.9F, 4, texel.texelSeed + 2) + knifeFinishValue4;
      float knifeFinishValue7 = KnifeFinishKit.getAmount3(knifeFinishValue2 * 1.3F + 9.1F, seedScaled * 0.95F, 3, texel.texelSeed + 4) + knifeFinishValue4;
      float knifeFinishValue8 = knifeFinishFlag ? 0.1F : 0.012F + 0.008F * (1.0F - knifeFinishValue);
      int oliveColor = OLIVE_COLOR;
      int knifeFinishValue9 = KnifeFinishKit.getValue(oliveColor, TAN_COLOR, KnifeFinishKit.getAmountForFloat(0.18F - knifeFinishValue8, 0.18F + knifeFinishValue8, knifeFinishValue5));
      int knifeFinishValue10 = KnifeFinishKit.getValue(knifeFinishValue9, LEATHER_BROWN_COLOR, KnifeFinishKit.getAmountForFloat(0.1F - knifeFinishValue8, 0.1F + knifeFinishValue8, knifeFinishValue6));
      int knifeFinishValue11 = KnifeFinishKit.getValue(knifeFinishValue10, CHARCOAL_COLOR, KnifeFinishKit.getAmountForFloat(0.27F - knifeFinishValue8, 0.27F + knifeFinishValue8, knifeFinishValue7));
      int value3 = KnifeFinishKit.getValue3(knifeFinishValue11, 0.94F + 0.12F * KnifeFinishKit.getAmount3(texel.rampAmount * 4.0F, texel.texelY * 4.0F, 2, texel.texelSeed + 6));
      if (!knifeFinishFlag) {
         return value3;
      } else {
         int knifeFinishValue13 = KnifeFinishBake.getValue(KnifeFinishKit.getAmount(value3));
         return KnifeFinishKit.getValue(KnifeFinishKit.getValue(value3, 0xFF000000 | knifeFinishValue13 << 16 | knifeFinishValue13 << 8 | knifeFinishValue13, 0.22F), DARK_OLIVE_COLOR, 0.3F);
      }
   }

   static float getRigKnife(KnifeFinish.Texel texel) {
      return texel.knifeRig == KnifeRig.STILETTO_TP && texel.rigKnifeValue == 3 ? 0.08F * (1.0F - KnifeFinishKit.getAmountForFloat(0.45F, 0.85F, texel.rigKnifeAmount)) : 0.0F;
   }

   static KnifeFinish.Finish getBuild4() {
      return getPaintSapphire(KnifeFinish.getBuilder("Red Web"), KnifeFinishPaints::getRigKnifeForTexelInt, 0.045F).recolor(KnifeFinish.getValue(7), 9212056).build();
   }

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3, float knifeFinishValue4, float knifeFinishValue5, float knifeFinishValue6) {
      float knifeFinishFactor = Math.max(0.0F, Math.max(knifeFinishValue3 - knifeFinishValue, knifeFinishValue - knifeFinishValue4));
      float max2 = Math.max(0.0F, Math.max(knifeFinishValue5 - knifeFinishValue2, knifeFinishValue2 - knifeFinishValue6));
      return (float)Math.sqrt(knifeFinishFactor * knifeFinishFactor + max2 * max2);
   }

   static float getAmountForTexelFloat(KnifeFinish.Texel texel, float knifeFinishFactor, int knifeFinishValue) {
      if (!(texel.rigAmount <= -1.0E9F) && !(knifeFinishFactor <= 0.0F)) {
         if (texel.rigEdgeDistance < 1.0E9F && texel.rigEdgeDistance <= texel.rigAmount + 0.01F) {
            knifeFinishFactor *= 0.4F;
         }

         float max2 = Math.max(0.0F, texel.rigAmount);
         float amountForFloat = KnifeFinishKit.getAmountForFloat(-0.02F, 0.12F, KnifeFinishKit.getAmount3(texel.rampAmount * 0.8F, texel.texelY * 0.8F, 2, knifeFinishValue + 53));
         float knifeFinishValue4 = 0.06F * (1.0F - amountForFloat) + max2;
         if (amountForFloat <= 0.0F) {
            return -knifeFinishValue4;
         } else {
            knifeFinishFactor *= amountForFloat;
            float amount3 = KnifeFinishKit.getAmount3(texel.rampAmount * 2.6F, texel.texelY * 2.6F, 3, knifeFinishValue + 31);
            float knifeFinishFactor2 = KnifeFinishKit.getAmount3(texel.rampAmount * 9.0F, texel.texelY * 9.0F, 2, knifeFinishValue + 37);
            float knifeFinishValue6 = knifeFinishFactor * KnifeFinishKit.getAmount4(0.1F + 2.6F * (amount3 + 0.04F), 0.0F, 1.7F);
            return knifeFinishValue6 + 0.3F * knifeFinishFactor * knifeFinishFactor2 * KnifeFinishKit.getAmountForFloat(2.0F * texel.texelSize, 5.0F * texel.texelSize, max2) - knifeFinishValue4;
         }
      } else {
         return -1.0F;
      }
   }

   static KnifeFinish.Finish getBuild5() {
      return getPaintSapphire(KnifeFinish.getBuilder("Night"), KnifeFinishPaints::getAmountRampForTexelInt, 0.06F).recolor(KnifeFinish.getValue(7), 8159368).build();
   }

   static float getAmountForFloat(float knifeFinishValue, float y, float knifeFinishDivisor) {
      float knifeFinishValue3 = knifeFinishValue / knifeFinishDivisor;
      float configsLikeValue = y / (knifeFinishDivisor * 2.0F * 0.8660254F);
      float knifeFinishFactor = knifeFinishValue3 - Math.round(knifeFinishValue3);
      float knifeFinishFactor2 = (configsLikeValue - Math.round(configsLikeValue)) * 2.0F * 0.8660254F;
      float knifeFinishValue5 = knifeFinishValue3 - 0.5F;
      float glfwLimit = configsLikeValue - 0.5F;
      float knifeFinishFactor3 = knifeFinishValue5 - Math.round(knifeFinishValue5);
      float knifeFinishFactor4 = (glfwLimit - Math.round(glfwLimit)) * 2.0F * 0.8660254F;
      return (float)Math.sqrt(Math.min(knifeFinishFactor * knifeFinishFactor + knifeFinishFactor2 * knifeFinishFactor2, knifeFinishFactor3 * knifeFinishFactor3 + knifeFinishFactor4 * knifeFinishFactor4)) * knifeFinishDivisor;
   }

   static int getAmount(KnifeFinish.Texel texel, int knifeFinishValue, int knifeFinishValue2, float knifeFinishFactor, float knifeFinishFactor2, int knifeFinishValue3) {
      int floor4 = (int)Math.floor((texel.rampAmount - 8.0F) / knifeFinishFactor + 0.5F);
      int floor5 = (int)Math.floor(texel.texelY / knifeFinishFactor);

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue6 = floor4 + j;
            int floorSeed = floor5 + i;
            if (!(KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3) > 0.75F)) {
               float rigKnifeValue = 8.0F + (knifeFinishValue6 - 0.28F + 0.56F * KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3 + 1)) * knifeFinishFactor;
               float knifeFinishValue9 = (floorSeed + 0.22F + 0.56F * KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3 + 2)) * knifeFinishFactor;
               float knifeFinishValue10 = knifeFinishFactor * (0.18F + 0.13F * KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3 + 3));
               float x = texel.rampAmount - rigKnifeValue;
               float y = texel.texelY - knifeFinishValue9;
               float distance = (float)Math.sqrt(x * x + y * y);
               if (!(distance > knifeFinishValue10 * 1.2F)) {
                  float rigKnife = getRigKnife(texel, rigKnifeValue, knifeFinishValue9, knifeFinishValue10);

                  for (int k = 0; k < 2 && rigKnife < 1.0F; k++) {
                     knifeFinishValue10 *= 0.8F;
                     rigKnife = getRigKnife(texel, rigKnifeValue, knifeFinishValue9, knifeFinishValue10);
                  }

                  float knifeFinishFactor3 = knifeFinishFactor2 * rigKnife;
                  if (!(knifeFinishFactor3 <= 0.0F) && !(distance > knifeFinishValue10 * 1.2F)) {
                     int knifeFinishFactor4 = 5 + (int)(KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3 + 4) * 3.0F);
                     float knifeFinishValue11 = KnifeFinishKit.getAmount(knifeFinishValue6, floorSeed, knifeFinishValue3 + 5) * (float) (Math.PI * 2);
                     float knifeFinishFactor5 = (float)Math.atan2(y, x) + knifeFinishValue11;
                     float sin = (float)Math.sin(knifeFinishFactor4 * knifeFinishFactor5 * 0.5F);
                     float knifeFinishValue12 = getAmount(x, y, knifeFinishValue10, knifeFinishFactor4, knifeFinishValue11);
                     float knifeFinishLimit = KnifeFinishKit.getAmountForFloat(-texel.texelSize, texel.texelSize, knifeFinishValue12) * knifeFinishFactor3;
                     float amountForFloat = KnifeFinishKit.getAmountForFloat(0.0323F, 0.039100002F, getAmountForFloat(x, y, 0.085F));
                     float max15 = Math.max(0.62F + 0.38F * amountForFloat, 1.0F - KnifeFinishKit.getAmountForFloat(0.035F, 0.06F, knifeFinishValue12));
                     int knifeFinishValue16 = KnifeFinishKit.getValue(
                        knifeFinishValue2, KnifeFinishKit.getValue3(CREAM_COLOR, 0.86F + 0.14F * KnifeFinishKit.getAmountForFloat(0.0F, 0.12F, knifeFinishValue12)), max15
                     );
                     float knifeFinishValue17 = distance > knifeFinishValue10 * 0.32F
                        ? getAmount(distance * Math.abs(sin) * 2.0F / knifeFinishFactor4, 0.016F, texel.texelSize) * KnifeFinishKit.getAmountForFloat(0.02F, 0.06F, knifeFinishValue12)
                        : 0.0F;
                     int knifeFinishValue18 = KnifeFinishKit.getValue(knifeFinishValue16, KnifeFinishKit.getValue3(CREAM_COLOR, 0.9F), 0.9F * knifeFinishValue17);
                     float knifeFinishValue19 = knifeFinishValue10 * 0.2F;
                     int knifeFinishValue20 = KnifeFinishKit.getValue(knifeFinishValue18, RAMP_MID_SHADE, 1.0F - KnifeFinishKit.getAmountForFloat(knifeFinishValue19 - texel.texelSize, knifeFinishValue19 + texel.texelSize, distance));
                     int stopsRamp = KnifeFinishKit.getValue(
                        knifeFinishValue20,
                        WARM_WHITE_COLOR,
                        KnifeFinishKit.getMax(
                           getAmount(Math.abs(distance - knifeFinishValue19), 0.022F, texel.texelSize)
                              + 1.0F
                              - KnifeFinishKit.getAmountForFloat(knifeFinishValue19 * 0.35F - texel.texelSize, knifeFinishValue19 * 0.35F + texel.texelSize, distance)
                        )
                     );
                     float amountForFloat6 = KnifeFinishKit.getAmountForFloat(-texel.texelSize, texel.texelSize, getAmount(x - 0.022F, y - -0.016F, knifeFinishValue10, knifeFinishFactor4, knifeFinishValue11));
                     knifeFinishValue = KnifeFinishKit.getValue(knifeFinishValue, DARK_RED_COLOR, 0.38F * knifeFinishFactor3 * amountForFloat6 * (1.0F - knifeFinishLimit));
                     knifeFinishValue = KnifeFinishKit.getValue(knifeFinishValue, stopsRamp, knifeFinishLimit);
                     return KnifeFinishKit.getValue(knifeFinishValue, WARM_WHITE_COLOR, knifeFinishFactor3 * getAmount(Math.abs(knifeFinishValue12), 0.03F, texel.texelSize));
                  }
               }
            }
         }
      }

      return knifeFinishValue;
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue) {
      return KnifeFinishKit.getAmountForFloat(-texel.texelSize * 0.8F, texel.texelSize * 0.8F, knifeFinishValue);
   }

   static float getAmount(float[][] floats, float knifeFinishValue, float knifeFinishValue2, float knifeFinishFactor) {
      float[] knifeFinishValues = floats[0];
      float knifeFinishBound = 1.0E9F;

      for (int i = -2; i <= 2; i++) {
         float knifeFinishFactor2 = i * 0.5F;
         float knifeFinishLimit = knifeFinishValue2 + knifeFinishFactor * knifeFinishFactor2;
         if (knifeFinishLimit < knifeFinishValues[0] || knifeFinishLimit > knifeFinishValues[knifeFinishValues.length - 1]) {
            return -1.0F;
         }

         int index = 0;

         while (index < knifeFinishValues.length - 2 && knifeFinishLimit > knifeFinishValues[index + 1]) {
            index++;
         }

         float knifeFinishValue3 = (knifeFinishLimit - knifeFinishValues[index]) / (knifeFinishValues[index + 1] - knifeFinishValues[index]);
         float knifeFinishValue4 = knifeFinishFactor * (float)Math.sqrt(1.0F - knifeFinishFactor2 * knifeFinishFactor2);
         knifeFinishBound = Math.min(
            knifeFinishBound,
            Math.min(KnifeFinishKit.getAmount(floats[2][index], floats[2][index + 1], knifeFinishValue3) - knifeFinishValue, knifeFinishValue - KnifeFinishKit.getAmount(floats[1][index], floats[1][index + 1], knifeFinishValue3))
               - knifeFinishValue4
         );
      }

      return knifeFinishBound;
   }

   static int getAmountRamp(KnifeFinish.Texel texel, int rampValue, int rampValue2, float knifeFinishFactor) {
      float stopsRampLimit = 0.5F + 0.5F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.7F, texel.texelY * 0.5F, 3, texel.texelSeed);
      float rampFactor = rampValue2 == 2 ? 0.0F : 1.0F - getAmountFloat(texel);
      float rampValue5 = rampFactor
         * (
            0.75F * KnifeFinishKit.getAmount3(texel.rampAmount * 16.0F, texel.texelY * 16.0F, 2, texel.texelSeed + 17)
               + 0.25F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 40.0F, texel.texelY * 40.0F, texel.texelSeed + 19)
         );
      int valueForTexelInt = KnifeFinishKit.getValueForTexelInt(texel, KnifeFinishKit.getValue3(HIGHLIGHT_RAMP.getStopsRamp(stopsRampLimit), 1.0F + 0.15F * rampValue5), 0.7F);
      int amountRamp = KnifeFinishKit.getValueInt(valueForTexelInt, PURPLE_COLOR, 0.12F * KnifeFinishKit.getAmountForTexel(texel));
      if (rampValue2 == 1) {
         return KnifeFinishKit.getValue3(amountRamp, 0.55F);
      } else if (rampValue2 == 2) {
         return amountRamp;
      } else {
         int rampValue7 = KnifeFinishKit.getValue(amountRamp, KnifeFinishKit.getValue4(amountRamp, 0.28F), 0.65F * getAmountForTexelFloatInt(texel, 1.1F, 0.3F, texel.texelSeed + 61));
         return getAmount(texel, rampValue7, getAmount(texel, rampValue2, knifeFinishFactor, 0.3F, 0.0F, texel.texelSeed), getAmount(texel, 0.07F, texel.texelSeed));
      }
   }

   static int getAmountRampForTexelInt(KnifeFinish.Texel texel, int rampValue, int rampValue2, float knifeFinishFactor) {
      float rampValue4 = rampValue2 == 2 ? 1.0F : getAmountFloat(texel);
      float amount3 = KnifeFinishKit.getAmount3(
         texel.rampAmount * 0.55F + 0.3F * KnifeFinishKit.getAmount3(texel.rampAmount * 1.4F, texel.texelY * 1.4F, 2, texel.texelSeed + 5), texel.texelY * 0.4F, 4, texel.texelSeed
      );
      float rampValue6 = (1.0F - rampValue4) * KnifeFinishKit.getAmount3(texel.rampAmount * 3.1F, texel.texelY * 2.2F, 3, texel.texelSeed + 3);
      float amountForFloat = KnifeFinishKit.getAmountForFloat(0.03F, 0.11F, amount3 + 0.11F * rampValue6);
      int amountRamp = KnifeFinishKit.getValue(texel, SPECULAR_RAMP.getStopsRamp(0.92F - 0.8F * amountForFloat + 0.08F * rampValue6), 0.5F - 0.15F * amountForFloat, 0.9F * (1.0F - rampValue4) + 0.1F);
      if (rampValue2 == 1) {
         return KnifeFinishKit.getValue3(amountRamp, 0.55F);
      } else if (rampValue2 == 2) {
         return amountRamp;
      } else {
         int rampValue8 = KnifeFinishKit.getValue(amountRamp, KnifeFinishKit.getValue4(amountRamp, 0.3F), 0.7F * getAmountForTexelFloatInt(texel, 0.9F, 0.35F, texel.texelSeed + 61));
         return getAmount(texel, rampValue8, getAmount(texel, rampValue2, knifeFinishFactor, 0.6F, 0.25F, texel.texelSeed), getAmount(texel, 0.075F, texel.texelSeed));
      }
   }

   static float getAmountForTexelFloatInt(KnifeFinish.Texel texel, float knifeFinishFactor, float knifeFinishValue, int knifeFinishValue2) {
      float knifeFinishValue3 = texel.rampAmount / knifeFinishFactor;
      float hashRegistryValue = texel.texelY / knifeFinishFactor;
      int floor5 = (int)Math.floor(knifeFinishValue3);
      int floor6 = (int)Math.floor(hashRegistryValue);
      float knifeFinishBound = 0.0F;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue7 = floor5 + j;
            int floorSeed = floor6 + i;
            if (!(KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2) > knifeFinishValue)) {
               float knifeFinishValue9 = (knifeFinishValue7 + KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 1)) * knifeFinishFactor;
               float knifeFinishValue10 = (floorSeed + KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 2)) * knifeFinishFactor;
               float knifeFinishValue11 = KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 3);
               float knifeFinishFactor2 = knifeFinishValue11 < 0.7F ? (knifeFinishValue11 / 0.7F - 0.5F) * 0.9F : (KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 7) * 2.0F - 1.0F) * 4.0F;
               float knifeFinishFactor3 = 1.0F / (float)Math.sqrt(knifeFinishFactor2 * knifeFinishFactor2 + 1.0F);
               float knifeFinishFactor4 = knifeFinishFactor2 * knifeFinishFactor3;
               float knifeFinishDivisor = knifeFinishFactor * (0.2F + 0.45F * KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 4));
               float knifeFinishFactor5 = texel.rampAmount - knifeFinishValue9;
               float knifeFinishFactor6 = texel.texelY - knifeFinishValue10;
               float planValuesItem = knifeFinishFactor5 * knifeFinishFactor4 + knifeFinishFactor6 * knifeFinishFactor3;
               if (!(Math.abs(planValuesItem) >= knifeFinishDivisor)) {
                  float abs13 = Math.abs(-knifeFinishFactor5 * knifeFinishFactor3 + knifeFinishFactor6 * knifeFinishFactor4);
                  float knifeFinishFactor7 = planValuesItem / knifeFinishDivisor;
                  float knifeFinishFactor8 = 1.0F - knifeFinishFactor7 * knifeFinishFactor7;
                  float knifeFinishValue14 = (0.008F + 0.014F * KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 5)) * knifeFinishFactor8;
                  knifeFinishBound = Math.max(knifeFinishBound, getAmount(abs13, knifeFinishValue14, texel.texelSize) * knifeFinishFactor8 * (0.45F + 0.55F * KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, knifeFinishValue2 + 6)));
               }
            }
         }
      }

      return knifeFinishBound;
   }

   static float getAmount(float x, float y, float knifeFinishFactor, int knifeFinishFactor2, float knifeFinishValue) {
      float knifeFinishFactor3 = (float)Math.atan2(y, x) + knifeFinishValue;
      float sqrt = (float)Math.sqrt(Math.abs((float)Math.cos(knifeFinishFactor2 * knifeFinishFactor3 * 0.5F)));
      return knifeFinishFactor * (0.56F + 0.44F * sqrt) - (float)Math.sqrt(x * x + y * y);
   }

   static int getRigKnifeForTexelInt(KnifeFinish.Texel texel, int rigKnifeValue, int rigKnifeValue2, float knifeFinishFactor) {
      if (rigKnifeValue == 2 && texel.knifeRig == KnifeRig.BUTTERFLY_TP) {
         return getValueDARK_GRAY_COLOR(texel);
      } else {
         int stopsRamp = RAMP.getStopsRamp(0.5F + 0.55F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.6F, texel.texelY * 0.35F, 2, texel.texelSeed + 9));
         if (rigKnifeValue2 == 0 || rigKnifeValue2 == 3) {
            int rigKnifeValue4 = rigKnifeValue == 0 ? 0 : 1;
            float rigKnifeValue5 = getAmount(
               texel, getInstanceForKnifeRigInt(texel.knifeRig, rigKnifeValue4), getInstance(texel.knifeRig, rigKnifeValue4), 13, rigKnifeValue4 == 0 ? 0.86F : 0.92F, 0.058F, 0.052F, texel.texelSeed + rigKnifeValue4 * 97
            );
            if (texel.knifeRig == KnifeRig.M9_BAYONET_TP && rigKnifeValue == 0) {
               rigKnifeValue5 *= getAmountForTexel(texel);
            }

            stopsRamp = KnifeFinishKit.getValue(stopsRamp, NEAR_BLACK_COLOR, 0.95F * rigKnifeValue5);
         }

         int rigKnifeValue6 = KnifeFinishKit.getValue(texel, stopsRamp, 0.55F, CORAL_COLOR);
         if (rigKnifeValue2 == 1) {
            return KnifeFinishKit.getValue3(rigKnifeValue6, 0.55F);
         } else {
            return rigKnifeValue2 == 2 ? rigKnifeValue6 : getAmount(texel, rigKnifeValue6, getAmount(texel, rigKnifeValue2, knifeFinishFactor, 0.35F, 0.0F, texel.texelSeed), getAmount(texel, 0.07F, texel.texelSeed));
         }
      }
   }

   static KnifeFinish.Finish getBuild6() {
      return getPaintSapphire(KnifeFinish.getBuilder("Rust"), KnifeFinishPaints::getAmountRig, 0.05F).recolor(KnifeFinish.getValue(7), 7234136).build();
   }

   static KnifeFinish.Builder getPaintSapphire(KnifeFinish.Builder builder, KnifeFinishPaints.Look look, float paintSapphireFactor) {
      return builder.paint(KnifeFinish.fadeSapphireValue, rimx -> look.paint(rimx, 0, 0, paintSapphireFactor))
         .paint(
            KnifeFinish.getValue(3),
            lookx -> look.paint(lookx, 1, lookx.knifeRig == KnifeRig.M9_BAYONET_TP ? 3 : 0, paintSapphireFactor * (lookx.knifeRig == KnifeRig.STILETTO_TP ? 0.69F : 1.15F))
         )
         .paint(KnifeFinish.getValue(4), texel -> look.paint(texel, 1, 1, paintSapphireFactor))
         .paint(KnifeFinish.getValue(5), lookx -> look.paint(lookx, 1, 2, paintSapphireFactor))
         .paint(KnifeFinish.getValue(6), rimx -> look.paint(rimx, 2, 3, paintSapphireFactor));
   }

   static int getAmountRamp(KnifeFinish.Texel texel, boolean rampFlag, boolean rigKnifeFlag, boolean rampFlag3, float rampValue) {
      float rampValue2 = getAmount(texel.rampAmount, texel.texelY, texel.texelSeed);
      int stopsRamp = BASE_RAMP.getStopsRamp(0.55F + 0.6F * rampValue2);
      if (!rampFlag) {
         return stopsRamp;
      } else {
         float rampDivisor = 0.02F;
         float rampFactor = (getAmount(texel.rampAmount + rampDivisor, texel.texelY, texel.texelSeed) - rampValue2) / rampDivisor;
         float rampFactor2 = (getAmount(texel.rampAmount, texel.texelY + rampDivisor, texel.texelSeed) - rampValue2) / rampDivisor;
         float max2 = Math.max(0.08F, (float)Math.sqrt(rampFactor * rampFactor + rampFactor2 * rampFactor2));
         float rampBound = (rampValue2 - rampValue) / max2;
         float amountForFloat3 = 1.0F;
         if (rampFlag3) {
            float hashHuntBound = getAmount(texel);
            float rampFactor3 = Math.max(0.12F - Math.abs(rampBound - hashHuntBound), 0.0F) / 0.12F;
            rampBound = Math.min(rampBound, hashHuntBound) - rampFactor3 * rampFactor3 * 0.03F;
            amountForFloat3 = KnifeFinishKit.getAmountForFloat(0.012F, 0.03F, hashHuntBound);
         }

         float amountForFloat = KnifeFinishKit.getAmountForFloat(-texel.texelSize, texel.texelSize, rampBound);
         float amountForFloat4 = KnifeFinishKit.getAmountForFloat(0.048F, 0.0552F, getAmountForFloat(texel.rampAmount, texel.texelY, 0.12F));
         int rampValue5 = KnifeFinishKit.getValue(stopsRamp, CREAM_COLOR, amountForFloat * (0.08F + 0.55F * amountForFloat4));
         int rampValue6 = KnifeFinishKit.getValue(rampValue5, WARM_WHITE_COLOR, 0.9F * getAmount(Math.abs(rampBound), 0.024F, texel.texelSize));
         float rampValue7 = Math.abs(rampValue2 - 0.24F) / max2;
         float rampValue8 = Math.abs(rampValue2 - 0.285F) / max2;
         int amountRamp = KnifeFinishKit.getValue(
            rampValue6, WARM_WHITE_COLOR, 0.85F * amountForFloat3 * Math.max(getAmount(rampValue7, 0.016F, texel.texelSize), getAmount(rampValue8, 0.016F, texel.texelSize))
         );
         if (!rigKnifeFlag) {
            return amountRamp;
         } else {
            float amountForFloat9 = KnifeFinishKit.getAmountForFloat(0.01F, 0.04F, Math.min(Math.max(0.0F, texel.rigAmount), texel.rigEdgeDistance));
            return amountForFloat9 <= 0.0F ? amountRamp : getAmount(texel, amountRamp, stopsRamp, 1.4F, amountForFloat9, texel.texelSeed + 29);
         }
      }
   }

   @FunctionalInterface
   interface Look {
      int paint(KnifeFinish.Texel texel, int paintValue, int paintValue2, float paintSapphireFactor);
   }
}
