package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.KnifeFinishKit;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeRig;

final class KnifeFinishSteels {
   static final int INK_BLACK_COLOR = KnifeFinishKit.getValue(1842465);
   static final int DARK_STEEL_COLOR = KnifeFinishKit.getValue(2763825);
   static final int LIGHT_STEEL_COLOR = KnifeFinishKit.getValue(13817822);
   static final int GUNMETAL_COLOR = KnifeFinishKit.getValue(3817288);
   static final int MID_STEEL_COLOR = KnifeFinishKit.getValue(5593957);
   static final int BLACK_STEEL_COLOR = KnifeFinishKit.getValue(1711394);
   static final int knifeFinishValue = KnifeFinishKit.getValue3(LIGHT_STEEL_COLOR, 0.7F);
   static final KnifeFinishKit.Ramp RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.13F, 0.25F, 0.37F, 0.49F, 0.61F, 0.75F, 0.88F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(11120568),
         KnifeFinishKit.getValue(12889718),
         KnifeFinishKit.getValue(14458930),
         KnifeFinishKit.getValue(10772006),
         KnifeFinishKit.getValue(7809387),
         KnifeFinishKit.getValue(2241438),
         KnifeFinishKit.getValue(2055366),
         KnifeFinishKit.getValue(2847956),
         KnifeFinishKit.getValue(3704024)
      }
   );
   static final int SILVER_COLOR = KnifeFinishKit.getValue(10725810);
   static final KnifeFinishKit.Ramp STEEL_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.35F, 0.7F, 1.0F},
      new int[]{KnifeFinishKit.getValue(14254362), KnifeFinishKit.getValue(15771690), KnifeFinishKit.getValue(16763717), KnifeFinishKit.getValue(16769930)}
   );
   static final int DARK_BRONZE_COLOR = KnifeFinishKit.getValue(4068356);
   static final int RUST_COLOR = KnifeFinishKit.getValue(8006663);
   static final int BURNT_ORANGE_COLOR = KnifeFinishKit.getValue(12736014);
   static final int PALE_GOLD_COLOR = KnifeFinishKit.getValue(16770982);
   static final int AMBER_ORANGE_COLOR = KnifeFinishKit.getValue(14712876);
   static final int stopsRamp = KnifeFinishSteels.STEEL_RAMP.getStopsRamp(0.9F);
   static final int GOLD_GLOW_COLOR = KnifeFinishKit.getValue(16765562);
   static final KnifeFinishKit.Ramp TEMPER_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.3F, 0.62F, 1.0F},
      new int[]{KnifeFinishKit.getValue(923716), KnifeFinishKit.getValue(1716857), KnifeFinishKit.getValue(2839978), KnifeFinishKit.getValue(6064328)}
   );
   static final KnifeFinishKit.Ramp BLEND_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.28F, 0.55F, 0.8F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(14201956),
         KnifeFinishKit.getValue(12088890),
         KnifeFinishKit.getValue(8141452),
         KnifeFinishKit.getValue(3816614),
         KnifeFinishKit.getValue(2310298)
      }
   );
   static final int PURPLE_TINT_COLOR = KnifeFinishKit.getValue(3812490);
   static final int BLUE_TINT_COLOR = KnifeFinishKit.getValue(2915000);
   static final KnifeFinishKit.Ramp ANODIZED_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.08F, 0.2F, 0.32F, 0.43F, 0.53F, 0.63F, 0.74F, 0.86F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(12896204),
         KnifeFinishKit.getValue(15260064),
         KnifeFinishKit.getValue(15118410),
         KnifeFinishKit.getValue(13204016),
         KnifeFinishKit.getValue(11030590),
         KnifeFinishKit.getValue(9190542),
         KnifeFinishKit.getValue(5913784),
         KnifeFinishKit.getValue(2776784),
         KnifeFinishKit.getValue(3838682),
         KnifeFinishKit.getValue(9224412)
      }
   );
   static final int SOFT_GRAY_COLOR = KnifeFinishKit.getValue(10133674);

   static KnifeFinish.Finish getBuild() {
      return KnifeFinish.getBuilder("Damascus")
         .paint(KnifeFinish.fadeSapphireValue, KnifeFinishSteels::getAmount)
         .paint(KnifeFinish.getValue(3), KnifeFinishSteels::getValueINK_BLACK_COLOR)
         .paint(KnifeFinish.getValue(4), texel -> getValueForTexelInt(texel, KnifeFinishKit.getValue(2237738), 0.4F, 0.6F))
         .paint(
            KnifeFinish.getValue(5),
            texel2 -> canRigKnife(texel2) ? KnifeFinishKit.getValueForTexelInt(texel2, DARK_STEEL_COLOR, 0.5F) : getValueForTexelInt(texel2, KnifeFinishKit.getValue(11120569), 0.9F, 0.6F)
         )
         .paint(
            KnifeFinish.getValue(6),
            texel3 -> getValueForTexelInt(texel3, KnifeFinishKit.getValue(BLACK_STEEL_COLOR, knifeFinishValue, KnifeFinishKit.getAmount(getAmountForTexelFloat(texel3, 5.2F), 0.5F, getAmountFloat(texel3))), 0.8F, 0.3F)
         )
         .recolor(KnifeFinish.getValue(7), 13225428)
         .glow(KnifeFinish.fadeSapphireValue, texel4 -> KnifeFinishKit.getValueForTexelIntFloat(texel4, KnifeFinishKit.getValue(15265010), 0.1F))
         .build();
   }

   static int getAmount(KnifeFinish.Texel texel) {
      float knifeFinishValue = getAmountForTexelFloat(texel, 5.2F);
      float knifeFinishFactor = 1.0F + 0.07F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.5F, texel.texelY * 0.25F, 2, texel.texelSeed + 77);
      if (texel.sheenValue == 2) {
         knifeFinishValue = KnifeFinishKit.getAmount(0.82F, knifeFinishValue, 0.4F);
      }

      int value3 = KnifeFinishKit.getValue3(KnifeFinishKit.getValue(GUNMETAL_COLOR, LIGHT_STEEL_COLOR, knifeFinishValue), knifeFinishFactor);
      return getValueForTexelInt(texel, value3, 0.4F + 0.6F * knifeFinishValue, 0.6F);
   }

   static float getAmount(KnifeFinish.Texel texel, int knifeFinishValue) {
      float knifeFinishValue2 = 0.5F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 22.0F, texel.texelY * 0.6F, knifeFinishValue + 31);
      float knifeFinishValue3 = knifeFinishValue2 + 0.32F * KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 57.0F, texel.texelY * 1.5F, knifeFinishValue + 32);
      return knifeFinishValue3 + 0.18F * (2.0F * KnifeFinishKit.getAmount((int)Math.floor(texel.rampAmount * 128.0F), (int)Math.floor(texel.texelY * 5.0F), knifeFinishValue + 33) - 1.0F);
   }

   static int getAmountRamp(KnifeFinish.Texel texel, boolean rampFlag) {
      float rampValue = KnifeFinishKit.getAmount3(texel.rampAmount * 0.55F, texel.texelY * 0.3F, 3, texel.texelSeed);
      float amount3 = KnifeFinishKit.getAmount3(texel.rampAmount * 1.7F, texel.texelY * 0.9F, 3, texel.texelSeed + 4);
      float rampValue3 = KnifeFinishKit.getAmount3(texel.rampAmount * 5.0F, texel.texelY * 1.6F, 2, texel.texelSeed + 5);
      float planValuesItem = KnifeFinishKit.getAmount3(texel.rampAmount * 0.25F + 3.0F, texel.texelY * 0.18F, 2, texel.texelSeed + 6);
      int stopsRamp = TEMPER_RAMP.getStopsRamp(KnifeFinishKit.getMax(0.5F + 0.5F * rampValue + 0.2F * amount3 + 0.08F * rampValue3));
      int amountRamp = KnifeFinishKit.getValue(stopsRamp, planValuesItem < 0.0F ? PURPLE_TINT_COLOR : BLUE_TINT_COLOR, 0.45F * KnifeFinishKit.getAmountForFloat(0.05F, 0.4F, Math.abs(planValuesItem)));
      if (rampFlag) {
         float stopsRampLimit = KnifeFinishKit.getMax(texel.glowRecolorAmount / 0.3F + 0.1F * rampValue + 0.05F * amount3);
         amountRamp = KnifeFinishKit.getValue(BLEND_RAMP.getStopsRamp(stopsRampLimit), amountRamp, 0.3F + 0.7F * KnifeFinishKit.getAmountForFloat(0.45F, 1.0F, stopsRampLimit));
      }

      return amountRamp;
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue) {
      float knifeFinishValue2 = texel.rampAmount - 8.0F;
      float knifeFinishValue3 = knifeFinishValue2 * 0.95F + 0.6F * KnifeFinishKit.getAmount3(knifeFinishValue2 * 0.6F, texel.texelY * 0.75F, 2, texel.texelSeed + 7);
      float amount3 = KnifeFinishKit.getAmount3(knifeFinishValue3, texel.texelY * 0.45F, 2, texel.texelSeed + 3);
      float knifeFinishValue5 = KnifeFinishKit.getAmount3(texel.rampAmount * 1.6F, texel.texelY * 1.2F, 2, texel.texelSeed + 5);
      float knifeFinishValue6 = knifeFinishValue + 0.1F * amount3 + 0.025F * knifeFinishValue5;
      return KnifeFinishKit.getMax(knifeFinishValue6 + 0.012F * (float)Math.sin(knifeFinishValue6 * 75.0F));
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue, float knifeFinishFactor, int knifeFinishValue2) {
      float knifeFinishValue3 = texel.rampAmount - 8.0F;
      float floorSeed = (float)Math.floor(knifeFinishValue);
      float amountForFloatInt = KnifeFinishKit.getAmountForFloatInt(knifeFinishValue3 * 0.85F + floorSeed * 3.7F, floorSeed * 1.9F + texel.texelY * 0.05F, knifeFinishValue2 + 7);
      float amountForFloatInt5 = KnifeFinishKit.getAmountForFloatInt(knifeFinishValue3 * 2.7F + floorSeed * 1.3F, floorSeed * 2.7F, knifeFinishValue2 + 8);
      return knifeFinishFactor * (0.32F + 0.9F * amountForFloatInt + 0.22F * amountForFloatInt5);
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, int knifeFinishValue) {
      float knifeFinishValue2 = texel.rampAmount - 8.0F;
      return (texel.texelY + 0.42F * knifeFinishValue2 + 1.5F * KnifeFinishKit.getAmount3(knifeFinishValue2 * 0.45F, texel.texelY * 0.2F, 3, knifeFinishValue + 3)) * knifeFinishFactor;
   }

   static float getAmountForTexel(KnifeFinish.Texel texel) {
      return getAmount(texel, 0.12F + 0.8F * (1.0F - texel.glowRecolorAmount));
   }

   static KnifeFinish.Finish getBuildANODIZED_RAMP() {
      int knifeFinishValue = KnifeFinishKit.getValue(8159884);
      return KnifeFinish.getBuilder("Heat Stained")
         .paint(KnifeFinish.fadeSapphireValue, texel -> {
            int stopsRamp = ANODIZED_RAMP.getStopsRamp(getAmount4(texel));
            if (texel.sheenValue == 2) {
               stopsRamp = KnifeFinishKit.getValue4(stopsRamp, 0.18F);
            }

            return getValueForTexelInt(texel, stopsRamp, 0.95F, 0.7F);
         })
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), texel2 -> {
            if (canRigKnife(texel2)) {
               return KnifeFinishKit.getValueForTexelInt(texel2, INK_BLACK_COLOR, 0.55F);
            } else {
               int knifeFinishValue2 = KnifeFinishKit.getValue(knifeFinishValue, ANODIZED_RAMP.getStopsRamp(getAmountForTexel(texel2)), 0.85F);
               return getValueForTexelInt(texel2, texel2.rigKnifeValue == 4 ? KnifeFinishKit.getValue3(knifeFinishValue2, 0.5F) : knifeFinishValue2, 0.8F, 0.8F);
            }
         })
         .paint(
            KnifeFinish.getValue(5),
            texel3 -> canRigKnife(texel3)
               ? KnifeFinishKit.getValueForTexelInt(texel3, DARK_STEEL_COLOR, 0.5F)
               : getValueForTexelInt(texel3, KnifeFinishKit.getValue(KnifeFinishKit.getValue(10923190), ANODIZED_RAMP.getStopsRamp(getAmountForTexel(texel3)), 0.35F), 0.9F, 0.6F)
         )
         .paint(
            KnifeFinish.getValue(6),
            texel4 -> getValue(texel4, KnifeFinishKit.getValue(SOFT_GRAY_COLOR, ANODIZED_RAMP.getStopsRamp(getAmount(texel4, 0.62F + 0.14F * texel4.glowRecolorAmount)), 0.18F), 0.95F, 0.5F)
         )
         .recolor(KnifeFinish.getValue(7), 13225428)
         .glow(KnifeFinish.fadeSapphireValue, texel5 -> KnifeFinishKit.getValueForTexelIntFloat(texel5, ANODIZED_RAMP.getStopsRamp(getAmount4(texel5)), 0.14F))
         .build();
   }

   static int getAmountRamp(KnifeFinish.Texel texel) {
      float rampValue = KnifeFinishKit.getAmount3(texel.rampAmount * 0.6F, texel.texelY * 0.25F, 2, texel.texelSeed + 21);
      int stopsRamp = STEEL_RAMP.getStopsRamp(KnifeFinishKit.getMax(0.55F + 0.55F * rampValue));
      float knifeFinishDivisor = Math.max(1.0E-4F, 0.8F * texel.texelSize * 1.4F);
      float max3 = Math.max(1.0E-4F, 1.45F * texel.texelSize * 1.4F);
      float rampValue4 = getAmount(texel, 0.8F, texel.texelSeed);
      float rampValue5 = getAmount(texel, rampValue4, 0.3F, texel.texelSeed);
      float rampValue6 = rampValue5 - getAmount(rampValue4);
      float rampBound = getAmount(rampValue6, 0.05F * KnifeFinishKit.getAmountForFloat(0.0F, 0.12F, rampValue5), knifeFinishDivisor);
      float pilotStatusBound = getAmount(rampValue6, 0.0F, knifeFinishDivisor);
      float rampValue7 = getAmount(rampValue6, -0.07F, knifeFinishDivisor);
      float amountForFloat = KnifeFinishKit.getAmountForFloat(-0.1F, 0.35F, KnifeFinishKit.getAmountForFloatInt(texel.rampAmount * 0.5F, texel.texelY * 0.35F, texel.texelSeed + 61));
      float rampValue9 = getAmount(texel, 1.45F, texel.texelSeed + 50);
      float deltaAmount = getAmount(texel, rampValue9, 0.3F, texel.texelSeed + 50) - getAmount(rampValue9);
      float rampValue10 = -0.05F - 0.22F * (1.0F - amountForFloat);
      float rocketsElytraBound = getAmount(deltaAmount, rampValue10, max3);
      float rampBound4 = getAmount(deltaAmount, rampValue10 - 0.03F, max3);
      int rampValue11 = KnifeFinishKit.getValue(stopsRamp, BURNT_ORANGE_COLOR, 0.75F * Math.max(rampBound, rocketsElytraBound));
      int rampValue12 = KnifeFinishKit.getValue(rampValue11, RUST_COLOR, 0.9F * Math.max(pilotStatusBound, rampBound4));
      return KnifeFinishKit.getValue(rampValue12, DARK_BRONZE_COLOR, 0.65F * rampValue7);
   }

   static boolean canRigKnife(KnifeFinish.Texel texel) {
      return texel.knifeRig == KnifeRig.M9_BAYONET_TP;
   }

   static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue3) {
      return getValue(texel, knifeFinishValue, knifeFinishFactor, knifeFinishValue3, KnifeFinishKit.getValue4(knifeFinishValue, 0.6F));
   }

   static KnifeFinish.Finish getBuildINK_BLACK_COLOR() {
      return KnifeFinish.getBuilder("Case Hardened")
         .paint(KnifeFinish.fadeSapphireValue, texel -> {
            int amountRamp = getAmountRampForTexel(texel);
            if (texel.sheenValue == 2) {
               amountRamp = KnifeFinishKit.getValue4(amountRamp, 0.2F);
            }

            return getValueForTexelIntFloat(texel, amountRamp, 0.9F, 0.6F, KnifeFinishKit.getValue4(amountRamp, 0.3F));
         })
         .paint(KnifeFinish.getValue(3), texel2 -> canRigKnife(texel2) ? KnifeFinishKit.getValueForTexelInt(texel2, INK_BLACK_COLOR, 0.55F) : getValue(texel2, 0.75F, 0.6F))
         .paint(KnifeFinish.getValue(4), texel3 -> getValueForTexelInt(texel3, KnifeFinishKit.getValue(1909296), 0.4F, 0.5F))
         .paint(KnifeFinish.getValue(5), texel4 -> canRigKnife(texel4) ? KnifeFinishKit.getValueForTexelInt(texel4, DARK_STEEL_COLOR, 0.5F) : getValue(texel4, 0.8F, 0.5F))
         .paint(KnifeFinish.getValue(6), texel5 -> getValueForTexelInt(texel5, getValueForTexel(texel5), 0.8F, 0.5F))
         .recolor(KnifeFinish.getValue(7), 12172998)
         .glow(KnifeFinish.fadeSapphireValue, texel6 -> KnifeFinishKit.getValueForTexelIntFloat(texel6, KnifeFinishKit.getValue(13163263), 0.1F))
         .build();
   }

   static int getValue(KnifeFinish.Texel texel, float knifeFinishValue, float knifeFinishValue2) {
      int valueForTexel = getValueForTexel(texel);
      return getValueForTexelIntFloat(texel, valueForTexel, knifeFinishValue, knifeFinishValue2, KnifeFinishKit.getValue4(valueForTexel, 0.45F));
   }

   static float getAmountFloat(KnifeFinish.Texel texel) {
      return texel.rigAmount <= -1.0E9F ? 0.0F : 1.0F - KnifeFinishKit.getAmountForFloat(0.02F, 0.05F, texel.rigAmount);
   }

   static KnifeFinish.Finish getBuildPALE_GOLD_COLOR() {
      int knifeFinishValue = KnifeFinishKit.getValue(1841429);
      int knifeFinishValue2 = KnifeFinishKit.getValue(15445820);
      return KnifeFinish.getBuilder("Tiger")
         .paint(KnifeFinish.fadeSapphireValue, KnifeFinishSteels::getValueStopsRamp)
         .paint(
            KnifeFinish.getValue(3) | KnifeFinish.getValue(4),
            dark -> canRigKnife(dark) ? KnifeFinishKit.getValueForTexelInt(dark, knifeFinishValue, 0.55F) : getValueForTexelInt(dark, knifeFinishValue, 0.5F, 0.6F)
         )
         .paint(KnifeFinish.getValue(5), texel -> getValueForTexelIntFloat(texel, knifeFinishValue2, 0.95F, 0.8F, PALE_GOLD_COLOR))
         .paint(KnifeFinish.getValue(6), gold -> getValue(gold, knifeFinishValue2, 0.95F, 0.35F, PALE_GOLD_COLOR))
         .recolor(KnifeFinish.getValue(7), 15779952)
         .glow(KnifeFinish.fadeSapphireValue, texel2 -> KnifeFinishKit.getValueForTexelIntFloat(texel2, GOLD_GLOW_COLOR, 0.15F * getAmount(getAmountRamp(texel2))))
         .build();
   }

   static int getValueForTexel(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getValue(getAmountRampForTexel(texel), SILVER_COLOR, getAmountFloat(texel));
   }

   static List<KnifeFinish.Finish> getItems() {
      return List.of(getBuild(), getBuildINK_BLACK_COLOR(), getBuildPALE_GOLD_COLOR(), getBuildFinish(), getBuildANODIZED_RAMP());
   }

   static int getValueStopsRamp(KnifeFinish.Texel texel) {
      int amountRamp = getAmountRamp(texel);
      if (texel.sheenValue == 2) {
         amountRamp = KnifeFinishKit.getValue(amountRamp, stopsRamp, 0.35F);
      }

      float knifeFinishValue = getAmount(amountRamp);
      int knifeFinishValue2 = KnifeFinishKit.getValue(
         KnifeFinishKit.getValueInt(amountRamp, AMBER_ORANGE_COLOR, 0.6F), KnifeFinishKit.getValue(PALE_GOLD_COLOR, KnifeFinishKit.getValue4(amountRamp, 0.25F), 0.5F), knifeFinishValue
      );
      return getValueForTexelIntFloat(texel, amountRamp, 0.95F * (0.45F + 0.55F * knifeFinishValue), 1.0F, knifeFinishValue2);
   }

   static int getValueForTexelInt(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue3) {
      return getValueForTexelIntFloat(texel, knifeFinishValue, knifeFinishFactor, knifeFinishValue3, KnifeFinishKit.getValue4(knifeFinishValue, 0.6F));
   }

   static int getValueINK_BLACK_COLOR(KnifeFinish.Texel texel) {
      if (canRigKnife(texel)) {
         return KnifeFinishKit.getValueForTexelInt(texel, INK_BLACK_COLOR, 0.55F);
      } else {
         float knifeFinishValue = getAmountForTexelFloat(texel, 5.2F);
         return getValueForTexelInt(texel, KnifeFinishKit.getValue(BLACK_STEEL_COLOR, MID_STEEL_COLOR, knifeFinishValue), 0.35F + 0.4F * knifeFinishValue, 0.5F);
      }
   }

   static int getValue(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = KnifeFinishKit.getAmount(KnifeFinishKit.getAmount3(texel) * 3.2F + 0.37F);
      float knifeFinishValue5 = knifeFinishFactor * (1.0F - getAmountFloat(texel));
      int valueForTexelIntFloat = getValueForTexelIntFloat(texel, knifeFinishValue, knifeFinishFactor, knifeFinishValue2, knifeFinishValue3);
      int value3 = KnifeFinishKit.getValue3(
         valueForTexelIntFloat, 1.0F - 0.28F * knifeFinishValue5 * (KnifeFinishKit.getAmountFloat(knifeFinishValue4, 0.72F, 0.16F) + KnifeFinishKit.getAmountFloat(knifeFinishValue4, -0.28F, 0.16F))
      );
      return KnifeFinishKit.getValueInt(value3, knifeFinishValue3, 0.55F * knifeFinishValue5 * KnifeFinishKit.getAmountFloat(knifeFinishValue4, 0.3F, 0.09F));
   }

   static float getAmount(int knifeFinishValue) {
      return KnifeFinishKit.getAmountForFloat(0.05F, 0.4F, KnifeFinishKit.getAmount(knifeFinishValue));
   }

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = knifeFinishValue - 8.0F;
      float amount3 = KnifeFinishKit.getAmount3(knifeFinishValue4 * 0.42F, knifeFinishValue2 * 0.15F, 3, knifeFinishValue3 + 3);
      float knifeFinishValue6 = KnifeFinishKit.getAmount3(knifeFinishValue4 * 1.25F + 2.2F * amount3, knifeFinishValue2 * 0.46F, 2, knifeFinishValue3 + 5);
      return 1.3F * knifeFinishValue4 + 0.2F * knifeFinishValue2 + 1.9F * amount3 + 0.4F * knifeFinishValue6 + 0.07F * KnifeFinishKit.getAmountForFloatInt(knifeFinishValue4 * 5.5F, knifeFinishValue2 * 2.2F, knifeFinishValue3 + 9);
   }

   private KnifeFinishSteels() {
   }

   static KnifeFinish.Finish getBuildFinish() {
      int knifeFinishValue = KnifeFinishKit.getValue(13226202);
      return KnifeFinish.getBuilder("Blue Steel")
         .paint(
            KnifeFinish.fadeSapphireValue,
            texel -> texel.sheenValue == 2
               ? getValueForTexelInt(texel, KnifeFinishKit.getValue(knifeFinishValue, getAmountRamp(texel, true), 0.25F), 1.0F, 0.6F)
               : getValueForTexelInt(texel, getAmountRamp(texel, true), 1.0F, 0.6F)
         )
         .paint(
            KnifeFinish.getValue(3),
            texel2 -> canRigKnife(texel2)
               ? KnifeFinishKit.getValueForTexelInt(texel2, KnifeFinishKit.getValue(1382173), 0.55F)
               : getValueForTexelInt(texel2, KnifeFinishKit.getValue3(getAmountRamp(texel2, false), 0.85F), 0.85F, 0.6F)
         )
         .paint(KnifeFinish.getValue(4), texel3 -> getValueForTexelInt(texel3, KnifeFinishKit.getValue(1120304), 0.5F, 0.5F))
         .paint(
            KnifeFinish.getValue(5),
            texel4 -> canRigKnife(texel4)
               ? KnifeFinishKit.getValueForTexelInt(texel4, KnifeFinishKit.getValue(1712960), 0.5F)
               : getValueForTexelInt(texel4, KnifeFinishKit.getValue(10135228), 0.95F, 0.6F)
         )
         .paint(KnifeFinish.getValue(6), texel5 -> getValueForTexelInt(texel5, KnifeFinishKit.getValue3(getAmountRamp(texel5, false), 0.8F), 0.9F, 0.3F))
         .recolor(KnifeFinish.getValue(7), 12107980)
         .glow(KnifeFinish.fadeSapphireValue, texel6 -> KnifeFinishKit.getValueForTexelIntFloat(texel6, KnifeFinishKit.getValue(10273023), 0.16F))
         .build();
   }

   static float getAmountForTexelFloat(KnifeFinish.Texel texel, float knifeFinishFactor) {
      float knifeFinishFactor2 = getAmount(texel.rampAmount, texel.texelY, texel.texelSeed);
      float texelSize = texel.texelSize;
      float knifeFinishFactor3 = getAmount(texel.rampAmount + texelSize, texel.texelY, texel.texelSeed) - knifeFinishFactor2;
      float knifeFinishFactor4 = getAmount(texel.rampAmount, texel.texelY + texelSize, texel.texelSeed) - knifeFinishFactor2;
      float knifeFinishDivisor = Math.max(1.0E-4F, knifeFinishFactor * (float)Math.sqrt(knifeFinishFactor3 * knifeFinishFactor3 + knifeFinishFactor4 * knifeFinishFactor4));
      float knifeFinishValue = knifeFinishFactor * knifeFinishFactor2;
      float floor2 = (float)Math.floor(knifeFinishValue);
      float deltaVar = knifeFinishValue - floor2;
      int knifeFinishValue3 = (int)floor2;
      float knifeFinishLimit = 0.22F + 0.4F * KnifeFinishKit.getAmount(knifeFinishValue3, 7, texel.texelSeed + 11);
      float knifeFinishValue4 = deltaVar < knifeFinishLimit ? Math.min(deltaVar, knifeFinishLimit - deltaVar) : -Math.min(deltaVar - knifeFinishLimit, 1.0F - deltaVar);
      float max5 = KnifeFinishKit.getMax(0.5F + knifeFinishValue4 / knifeFinishDivisor);
      float knifeFinishValue6 = 0.72F + 0.28F * KnifeFinishKit.getAmount(knifeFinishValue3, 3, texel.texelSeed + 13);
      float knifeFinishValue7 = 0.08F + 0.32F * KnifeFinishKit.getAmount(knifeFinishValue3, 5, texel.texelSeed + 17);
      float knifeFinishLimit2 = KnifeFinishKit.getAmount(knifeFinishValue3, 9, texel.texelSeed + 19);
      if (knifeFinishLimit2 > 0.45F) {
         float knifeFinishValue8 = KnifeFinishKit.getMax(0.5F + (0.035F - Math.abs(deltaVar - knifeFinishLimit * 0.5F)) / knifeFinishDivisor) * (1.0F - KnifeFinishKit.getAmountForFloat(0.08F, 0.2F, knifeFinishDivisor));
         knifeFinishValue6 = KnifeFinishKit.getAmount(knifeFinishValue6, knifeFinishValue7 + 0.25F, 0.55F * knifeFinishValue8 * (knifeFinishLimit2 - 0.45F) / 0.55F + 0.25F * knifeFinishValue8);
      }

      float knifeFinishValue9 = KnifeFinishKit.getAmount(knifeFinishValue7, knifeFinishValue6, max5);
      return KnifeFinishKit.getAmount(0.5F * (knifeFinishValue7 + knifeFinishValue6) * 0.92F, knifeFinishValue9, 1.0F - KnifeFinishKit.getAmountForFloat(0.25F, 0.55F, knifeFinishDivisor));
   }

   static float getAmount(float knifeFinishValue, float knifeFinishValue2, float knifeFinishDivisor) {
      return KnifeFinishKit.getMax(0.5F + (knifeFinishValue + knifeFinishValue2) / knifeFinishDivisor);
   }

   static int getValueForTexelIntFloat(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishFactor, float knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = KnifeFinishKit.getAmount(texel);
      float amountForTexel = KnifeFinishKit.getAmountForTexel(texel);
      float knifeFinishFactor3 = 1.0F + knifeFinishValue2 * 0.12F * getAmount(texel, texel.texelSeed);
      int value3 = KnifeFinishKit.getValue3(knifeFinishValue, knifeFinishFactor3 * (0.84F + 0.95F * (knifeFinishValue4 - 0.62F) * knifeFinishFactor));
      int valueInt = KnifeFinishKit.getValueInt(value3, knifeFinishValue3, knifeFinishFactor * 0.62F * amountForTexel);
      int valueInt7 = KnifeFinishKit.getValueInt(valueInt, -1, knifeFinishFactor * 0.08F * amountForTexel * amountForTexel);
      return KnifeFinishKit.getValueInt(valueInt7, knifeFinishValue3, 0.34F * KnifeFinishKit.getAmount4(texel, 0.08F));
   }

   static float getAmount(float knifeFinishValue) {
      return Math.abs(knifeFinishValue - (float)Math.floor(knifeFinishValue) - 0.5F);
   }

   static float getAmount4(KnifeFinish.Texel texel) {
      return getAmount(texel, 0.03F + 0.9F * texel.glowRecolorAmount + 0.12F * KnifeFinishKit.getAmount3(texel, 0.8F) - 0.04F * KnifeFinishKit.getAmount(texel, 0.5F));
   }

   static int getAmountRampForTexel(KnifeFinish.Texel texel) {
      float rampValue = KnifeFinishKit.getAmount(texel.rampAmount * 0.38F, texel.texelY * 0.38F, 1.5F, 3, texel.texelSeed);
      float amount3 = KnifeFinishKit.getAmount3(texel.rampAmount * 3.4F, texel.texelY * 3.4F, 3, texel.texelSeed + 9);
      float stopsRampLimit = KnifeFinishKit.getMax(0.55F + 1.05F * rampValue + 0.16F * amount3);
      int stopsRamp = RAMP.getStopsRamp(stopsRampLimit);
      float amountForFloat = KnifeFinishKit.getAmountForFloat(
         0.1F, 0.15F, KnifeFinishKit.getAmount3(texel.rampAmount * 0.3F + 7.0F + 0.7F * rampValue, texel.texelY * 0.3F + 0.5F * rampValue, 2, texel.texelSeed + 13)
      );
      int rampValue5 = KnifeFinishKit.getValue(stopsRamp, KnifeFinishKit.getValue(SILVER_COLOR, stopsRamp, 0.12F), 0.85F * amountForFloat);
      return KnifeFinishKit.getValue3(rampValue5, 1.0F + 0.08F * amount3);
   }
}
