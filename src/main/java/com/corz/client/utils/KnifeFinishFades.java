package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.KnifeFinishKit;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishReference;

final class KnifeFinishFades {
   static final KnifeFinishKit.Ramp ACCENT_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.28F, 0.52F, 0.76F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(10487832),
         KnifeFinishKit.getValue(14881822),
         KnifeFinishKit.getValue(16734738),
         KnifeFinishKit.getValue(16756736),
         KnifeFinishKit.getValue(16767032)
      }
   );
   static final KnifeFinishKit.Ramp BODY_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.3F, 0.62F, 0.86F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(1455280),
         KnifeFinishKit.getValue(1854694),
         KnifeFinishKit.getValue(4032255),
         KnifeFinishKit.getValue(6795007),
         KnifeFinishKit.getValue(10144767)
      }
   );
   static final KnifeFinishKit.Ramp GLOW_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.26F, 0.5F, 0.64F, 0.74F, 0.83F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(16726546),
         KnifeFinishKit.getValue(16741396),
         KnifeFinishKit.getValue(16756258),
         KnifeFinishKit.getValue(16766042),
         KnifeFinishKit.getValue(16772536),
         KnifeFinishKit.getValue(10277631),
         KnifeFinishKit.getValue(3438847)
      }
   );
   static final int HIGHLIGHT_WARM_WHITE = KnifeFinishKit.getValue(16775918);
   static final int WHITE_COLOR = KnifeFinishKit.getValue(16777215);
   static final int GLOW_GOLD_COLOR = KnifeFinishKit.getValue(16770720);
   static final KnifeFinishKit.Ramp SAPPHIRE_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.14F, 0.3F, 0.46F, 0.6F, 0.74F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(16765514),
         KnifeFinishKit.getValue(16758830),
         KnifeFinishKit.getValue(16751134),
         KnifeFinishKit.getValue(16743958),
         KnifeFinishKit.getValue(16738834),
         KnifeFinishKit.getValue(16733706),
         KnifeFinishKit.getValue(15614982)
      }
   );
   static final KnifeFinishKit.Ramp GOLD_GLOW_RAMP = KnifeFinishKit.getRamp(
      KnifeFinishKit.getValue(16734730), KnifeFinishKit.getValue(16747036), KnifeFinishKit.getValue(16758832)
   );
   static final int AMBER_COLOR = KnifeFinishKit.getValue(16761960);
   static final KnifeFinishKit.Ramp RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.18F, 0.35F, 0.51F, 0.64F, 0.75F, 0.9F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(5436132),
         KnifeFinishKit.getValue(1101024),
         KnifeFinishKit.getValue(1349360),
         KnifeFinishKit.getValue(2064383),
         KnifeFinishKit.getValue(4018930),
         KnifeFinishKit.getValue(6961390),
         KnifeFinishKit.getValue(9189610),
         KnifeFinishKit.getValue(10369764)
      }
   );
   static final KnifeFinishKit.Ramp CYAN_GLOW_RAMP = KnifeFinishKit.getRamp(
      KnifeFinishKit.getValue(4873471), KnifeFinishKit.getValue(2006015), KnifeFinishKit.getValue(1369056)
   );

   static KnifeFinish.Finish getBuild() {
      int knifeFinishValue = KnifeFinishKit.getValue(2500397);
      int knifeFinishValue2 = KnifeFinishKit.getValue(3421500);
      KnifeFinish.Builder glow = KnifeFinish.getBuilder("Marble")
         .seed(18)
         .paint(KnifeFinish.fadeSapphireValue, KnifeFinishFades::getAmount)
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), texel -> KnifeFinishKit.getValueForTexelInt(texel, knifeFinishValue, 0.7F))
         .paint(KnifeFinish.getValue(5), texel2 -> KnifeFinishKit.getValue(texel2, knifeFinishValue2, 0.5F, 0.3F))
         .recolor(KnifeFinish.getValue(6), 3092534)
         .recolor(KnifeFinish.getValue(7), 9343386)
         .glow(KnifeFinish.fadeSapphireValue, texel3 -> KnifeFinishKit.getValueForTexelIntFloat(texel3, getAmountInt(texel3), 0.2F));
      return getGlow(glow, KnifeFinishFades::getRampAmountForTexel, GLOW_GOLD_COLOR, 0.3F).build();
   }

   private KnifeFinishFades() {
   }

   static float getToFloat(long floatValue) {
      return Float.intBitsToFloat((int)(floatValue >>> 32));
   }

   static float getAmount(float knifeFinishValue) {
      return KnifeFinishKit.getAmountForFloat(-0.03F, 0.03F, knifeFinishValue);
   }

   static int getAmount(KnifeFinish.Texel texel) {
      long knifeFinishValue = getTime(texel.rampAmount, texel.texelY, texel.texelSeed);
      float toFloat = getToFloat(knifeFinishValue);
      float knifeFinishValue2 = getAmount(texel, toFloat);
      float amountForTexelFloat = getAmountForTexelFloat(texel, toFloat);
      int ramp = getRamp(toFloat, getToFloatForLong(knifeFinishValue), knifeFinishValue2, amountForTexelFloat);
      int value3 = KnifeFinishKit.getValue3(ramp, 1.0F + 0.05F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.9F, texel.texelY * 0.35F, 2, texel.texelSeed + 71));
      if (texel.sheenValue == 2) {
         value3 = KnifeFinishKit.getValue4(value3, 0.12F);
      }

      int knifeFinishValue5 = KnifeFinishKit.getValue(texel, value3, 0.95F, KnifeFinishKit.getValue(value3, WHITE_COLOR, 0.45F));
      int valueInt = KnifeFinishKit.getValueInt(knifeFinishValue5, value3, 0.12F + 0.16F * getAmount(toFloat));
      float knifeFinishValue7 = KnifeFinishKit.getAmount(KnifeFinishKit.getAmount3(texel));
      float knifeFinishFactor = 0.32F * KnifeFinishKit.getAmountFloat(knifeFinishValue7, 0.205F, 0.03F) + 0.15F * KnifeFinishKit.getAmountFloat(knifeFinishValue7, 0.6F, 0.016F);
      int valueInt9 = KnifeFinishKit.getValueInt(valueInt, WHITE_COLOR, knifeFinishFactor);
      return KnifeFinishKit.getValue(valueInt9, HIGHLIGHT_WARM_WHITE, Math.max(0.4F * knifeFinishValue2, 0.5F * amountForTexelFloat));
   }

   static float getAmountForTexel(KnifeFinish.Texel texel) {
      return getAmount(texel, -0.1F, 0.03F, 29);
   }

   static long getTime(float knifeFinishValue, float knifeFinishValue2, int knifeFinishValue3) {
      float knifeFinishValue4 = knifeFinishValue * 0.25F;
      float seedScaled = knifeFinishValue2 * 0.1F;
      float amount3 = KnifeFinishKit.getAmount3(knifeFinishValue4, seedScaled, 2, knifeFinishValue3 + 11);
      float knifeFinishValue6 = KnifeFinishKit.getAmount3(knifeFinishValue4 + 5.2F, seedScaled + 1.3F, 2, knifeFinishValue3 + 23);
      float knifeFinishValue7 = KnifeFinishKit.getAmount3(knifeFinishValue4 + 1.5F * amount3 + 1.7F, seedScaled + 1.5F * knifeFinishValue6 + 9.2F, 2, knifeFinishValue3 + 37);
      float knifeFinishValue8 = KnifeFinishKit.getAmount3(knifeFinishValue4 + 1.5F * amount3 + 8.3F, seedScaled + 1.5F * knifeFinishValue6 + 2.8F, 2, knifeFinishValue3 + 41);
      float knifeFinishValue9 = knifeFinishValue4 + 1.6F * knifeFinishValue7;
      float hashRegistryValue = seedScaled + 1.6F * knifeFinishValue8;
      float quadUnfinishedValue = KnifeFinishKit.getAmount(knifeFinishValue9, hashRegistryValue, 3, 2.03F, 0.42F, knifeFinishValue3);
      float knifeFinishValue12 = KnifeFinishKit.getAmount3(knifeFinishValue9 * 1.7F + 3.3F, hashRegistryValue * 1.7F - 4.1F, 2, knifeFinishValue3 + 59);
      return (long)Float.floatToRawIntBits(quadUnfinishedValue) << 32 | Float.floatToRawIntBits(knifeFinishValue12) & 4294967295L;
   }

   static KnifeFinish.Finish getBuildSAPPHIRE_RAMP() {
      int knifeFinishValue = KnifeFinishKit.getValue(2828326);
      int knifeFinishValue2 = KnifeFinishKit.getValue(3551790);
      KnifeFinish.Builder glow = KnifeFinish.getBuilder("Amber Fade")
         .paint(KnifeFinish.fadeSapphireValue, texel -> getAmount(texel, SAPPHIRE_RAMP.getStopsRamp(getAmountFloat(texel)), 0.7F, 0.22F, AMBER_COLOR))
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), grip -> KnifeFinishKit.getValueForTexelInt(grip, knifeFinishValue, 0.65F))
         .paint(KnifeFinish.getValue(5), rim -> KnifeFinishKit.getValue(rim, knifeFinishValue2, 0.5F, 0.35F))
         .recolor(KnifeFinish.getValue(6), 3420203)
         .recolor(KnifeFinish.getValue(7), 11044432)
         .glow(KnifeFinish.fadeSapphireValue, texel2 -> KnifeFinishKit.getValueForTexelIntFloat(texel2, SAPPHIRE_RAMP.getStopsRamp(getAmountFloat(texel2)), 0.2F));
      return getGlow(glow, texel3 -> GOLD_GLOW_RAMP.getStopsRamp(texel3.glowRecolorAmount), KnifeFinishKit.getValue(16769162), 1.0F).build();
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, float knifeFinishFactor2, int knifeFinishValue) {
      float knifeFinishLimit = 1.06F * texel.glowRecolorAmount
         - 0.05F
         + knifeFinishFactor * (0.5F - texel.edgePosition)
         + knifeFinishFactor2 * KnifeFinishKit.getAmount3(texel.rampAmount * 0.7F, texel.texelY * 0.25F, 3, texel.texelSeed + knifeFinishValue)
         + 0.05F * texel.fieldX;
      return KnifeFinishKit.getMax(knifeFinishLimit);
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishValue) {
      return (1.0F - KnifeFinishKit.getAmountForFloat(0.007F, 0.018F, Math.abs(knifeFinishValue)))
         * KnifeFinishKit.getAmountForFloat(-0.06F, 0.1F, KnifeFinishKit.getAmount3(texel.rampAmount * 0.9F, texel.texelY * 0.4F, 2, texel.texelSeed + 77));
   }

   static int getRamp(float rampValue, float rampValue2, float rampValue3, float amountForTexelFloat) {
      int stopsRamp = ACCENT_RAMP.getStopsRamp(KnifeFinishKit.getMax(0.4F + 1.35F * rampValue2 + 0.5F * rampValue));
      int stopsRamp2 = BODY_RAMP.getStopsRamp(KnifeFinishKit.getMax(0.4F + 1.3F * rampValue2 + 1.1F * rampValue));
      int knifeFinishValue = KnifeFinishKit.getValue(KnifeFinishKit.getValue(stopsRamp2, stopsRamp, getAmount(rampValue)), HIGHLIGHT_WARM_WHITE, 0.85F * rampValue3);
      return KnifeFinishKit.getValue(knifeFinishValue, HIGHLIGHT_WARM_WHITE, 0.6F * amountForTexelFloat);
   }

   static int getAmount(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishValue2, float knifeFinishFactor, int stopsRamp) {
      knifeFinishValue = KnifeFinishKit.getValue3(knifeFinishValue, 1.0F + 0.06F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.9F, texel.texelY * 0.35F, 2, texel.texelSeed + 71));
      if (texel.sheenValue == 2) {
         knifeFinishValue = KnifeFinishKit.getValue(knifeFinishValue, stopsRamp, 0.3F);
      }

      int knifeFinishValue5 = KnifeFinishKit.getValue(knifeFinishValue, stopsRamp, 0.55F);
      float knifeFinishValue6 = KnifeFinishKit.getAmount(texel);
      float amountForTexel = KnifeFinishKit.getAmountForTexel(texel);
      int value3 = KnifeFinishKit.getValue3(knifeFinishValue, (1.0F + knifeFinishValue2 * 0.06F * KnifeFinishKit.getAmount(texel, 1.0F, texel.texelSeed)) * (0.9F + 0.558F * (knifeFinishValue6 - 0.62F)));
      int valueInt = KnifeFinishKit.getValueInt(value3, knifeFinishValue5, 0.54F * amountForTexel);
      int valueInt10 = KnifeFinishKit.getValueInt(valueInt, knifeFinishValue5, 0.3F * KnifeFinishKit.getAmount4(texel, 0.08F));
      return KnifeFinishKit.getValueInt(valueInt10, knifeFinishValue, knifeFinishFactor);
   }

   static int getAmountInt(KnifeFinish.Texel texel) {
      long knifeFinishValue = getTime(texel.rampAmount, texel.texelY, texel.texelSeed);
      float toFloat = getToFloat(knifeFinishValue);
      return getRamp(toFloat, getToFloatForLong(knifeFinishValue), getAmount(texel, toFloat), getAmountForTexelFloat(texel, toFloat));
   }

   static float getAmountFloat(KnifeFinish.Texel texel) {
      return getAmount(texel, 0.1F, 0.03F, 13);
   }

   static float getToFloatForLong(long floatValue) {
      return Float.intBitsToFloat((int)floatValue);
   }

   static int getAmount(KnifeFinish.Texel texel, int knifeFinishValue, float knifeFinishValue2, float knifeFinishFactor) {
      knifeFinishValue = KnifeFinishKit.getValue3(knifeFinishValue, 1.0F + 0.06F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.9F, texel.texelY * 0.35F, 2, texel.texelSeed + 71));
      if (texel.sheenValue == 2) {
         knifeFinishValue = KnifeFinishKit.getValue4(knifeFinishValue, 0.22F);
      }

      return KnifeFinishKit.getValueInt(KnifeFinishKit.getValue(texel, knifeFinishValue, 0.9F, knifeFinishValue2), knifeFinishValue, knifeFinishFactor);
   }

   static int getRampAmountForTexel(KnifeFinish.Texel texel) {
      return GLOW_RAMP.getStopsRamp(texel.glowRecolorAmount);
   }

   static KnifeFinish.Builder getGlow(KnifeFinish.Builder builder, KnifeFinish.Painter painter, int glowValue, float glowValue2) {
      return builder.glow(
            KnifeFinish.getValue(5),
            glowx -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(painter.paint(glowx), glowValue, glowValue2 * 0.3F * KnifeFinishKit.getAmountForTexelFloat(glowx, 0.05F)), KnifeFinishReference.getAmountForTexel(glowx)
            )
         )
         .glow(
            KnifeFinish.getValue(4),
            hotx -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(painter.paint(hotx), glowValue, glowValue2 * 0.35F * KnifeFinishKit.getAmountForTexelFloat(hotx, 0.12F)),
               0.07F + 0.93F * KnifeFinishKit.getAmountForTexelFloat(hotx, 0.16F)
            )
         )
         .glow(KnifeFinish.getValue(3), glowx -> KnifeFinishKit.getValue6(painter.paint(glowx), KnifeFinishReference.getRigKnife(glowx, false)));
   }

   static List<KnifeFinish.Finish> getItems() {
      return List.of(getBuild(), getBuildSAPPHIRE_RAMP(), getBuildRAMP());
   }

   static float getAmountForTexelFloat(KnifeFinish.Texel texel, float knifeFinishValue) {
      float minAbs = Math.min(Math.abs(knifeFinishValue - 0.06F), Math.abs(knifeFinishValue + 0.08F));
      return (1.0F - KnifeFinishKit.getAmountForFloat(0.004F, 0.011F, minAbs))
         * KnifeFinishKit.getAmountForFloat(0.04F, 0.2F, KnifeFinishKit.getAmount3(texel.rampAmount * 0.55F, texel.texelY * 0.22F, 2, texel.texelSeed + 91));
   }

   static KnifeFinish.Finish getBuildRAMP() {
      int knifeFinishValue = KnifeFinishKit.getValue(2303791);
      int knifeFinishValue2 = KnifeFinishKit.getValue(2830393);
      KnifeFinish.Builder glow = KnifeFinish.getBuilder("Ocean Fade")
         .paint(KnifeFinish.fadeSapphireValue, texel -> getAmount(texel, RAMP.getStopsRamp(getAmountForTexel(texel)), 0.7F, 0.14F))
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), grip -> KnifeFinishKit.getValueForTexelInt(grip, knifeFinishValue, 0.65F))
         .paint(KnifeFinish.getValue(5), texel2 -> KnifeFinishKit.getValue(texel2, knifeFinishValue2, 0.5F, 0.35F))
         .recolor(KnifeFinish.getValue(6), 2895928)
         .recolor(KnifeFinish.getValue(7), 9213348)
         .glow(KnifeFinish.fadeSapphireValue, texel3 -> KnifeFinishKit.getValueForTexelIntFloat(texel3, RAMP.getStopsRamp(getAmountForTexel(texel3)), 0.2F));
      return getGlow(glow, texel4 -> CYAN_GLOW_RAMP.getStopsRamp(texel4.glowRecolorAmount), KnifeFinishKit.getValue(11075572), 1.0F).build();
   }
}
