package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.KnifeFinishKit;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeFinishBake;

final class KnifeFinishReference {
   static final KnifeFinishKit.Ramp SAPPHIRE_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.26F, 0.36F, 0.45F, 0.64F, 0.84F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(16769126),
         KnifeFinishKit.getValue(16763194),
         KnifeFinishKit.getValue(16742960),
         KnifeFinishKit.getValue(16730776),
         KnifeFinishKit.getValue(15611064),
         KnifeFinishKit.getValue(10632422),
         KnifeFinishKit.getValue(7221472)
      }
   );
   static final KnifeFinishKit.Ramp RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.22F, 0.46F, 0.68F, 0.86F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(3801611),
         KnifeFinishKit.getValue(8127760),
         KnifeFinishKit.getValue(12192537),
         KnifeFinishKit.getValue(14881822),
         KnifeFinishKit.getValue(16726566),
         KnifeFinishKit.getValue(16743482)
      }
   );
   static final int VIVID_ORANGE_COLOR = KnifeFinishKit.getValue(16742930);
   static final int knifeFinishValue = KnifeFinishKit.getValue(16757276);
   static final int DARK_RED_COLOR = KnifeFinishKit.getValue(5899794);
   static final int ORANGE_COLOR = KnifeFinishKit.getValue(16751706);
   static final int LIGHT_ORANGE_COLOR = KnifeFinishKit.getValue(16765600);
   static final int BRIGHT_ORANGE_COLOR = KnifeFinishKit.getValue(16747050);
   static final KnifeFinishKit.Ramp GLOW_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.3F, 0.55F, 0.8F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(16727733),
         KnifeFinishKit.getValue(13777919),
         KnifeFinishKit.getValue(9063935),
         KnifeFinishKit.getValue(3841023),
         KnifeFinishKit.getValue(1697535)
      }
   );

   static float getRigKnife(KnifeFinish.Texel texel, boolean rigKnifeFlag) {
      if (texel.rigEdgeDistance < 1.0E9F) {
         return 0.4F * KnifeFinishKit.getAmountForTexelFloat(texel, 0.14F);
      } else {
         return rigKnifeFlag && texel.knifeRig == KnifeRig.STILETTO_TP ? KnifeFinishKit.getAmount(texel, 0.2F, 0.045F) : 0.0F;
      }
   }

   static KnifeFinish.Finish getBuild() {
      int knifeFinishValue = KnifeFinishKit.getValue(1052693);
      return KnifeFinish.getBuilder("Neon")
         .paint(KnifeFinish.getValue(0) | KnifeFinish.getValue(2), KnifeFinishReference::getValueInt)
         .paint(KnifeFinish.getValue(1), texel -> KnifeFinishKit.getValue(getValueInt(texel), getRampAmountForTexel(texel), getRigAmount(texel)))
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), texel2 -> KnifeFinishKit.getValueForTexelInt(texel2, KnifeFinishKit.getValue(1118486), 0.6F))
         .paint(KnifeFinish.getValue(5), texel3 -> KnifeFinishKit.getValue(KnifeFinishKit.getValue(texel3, knifeFinishValue, 0.6F), getRampAmount(texel3), 0.6F * getAmountForTexel(texel3)))
         .recolor(KnifeFinish.getValue(6), 1447707)
         .recolor(KnifeFinish.getValue(7), 3816516)
         .glow(KnifeFinish.getValue(1), texel4 -> KnifeFinishKit.getValue6(KnifeFinishKit.getValue4(getRampAmountForTexel(texel4), 0.12F), getRigAmount(texel4)))
         .glow(KnifeFinish.getValue(0), texel5 -> KnifeFinishKit.getValue6(getRampAmountForTexel(texel5), 0.85F * getAmount4(texel5)))
         .glow(KnifeFinish.getValue(5), texel6 -> KnifeFinishKit.getValue6(getRampAmount(texel6), getAmountForTexel(texel6)))
         .glow(KnifeFinish.getValue(4), texel7 -> KnifeFinishKit.getValue6(getRampAmount(texel7), 0.1F + 0.85F * KnifeFinishKit.getAmountForTexelFloat(texel7, 0.16F)))
         .glow(KnifeFinish.getValue(3), texel8 -> KnifeFinishKit.getValue6(getRampAmount(texel8), getRigKnife(texel8, true)))
         .build();
   }

   private KnifeFinishReference() {
   }

   static int getValueForTexel(KnifeFinish.Texel texel) {
      float knifeFinishValue = KnifeFinishKit.getMax(0.2F * KnifeFinishKit.getAmountForTexel(texel));
      float knifeFinishFactor = 0.1F * getAmountFloat(texel);
      float knifeFinishDivisor = Math.min(0.2F, knifeFinishValue + knifeFinishFactor * (1.0F - knifeFinishValue));
      int value4 = KnifeFinishKit.getValue4(KnifeFinishKit.getValue(16734778), 0.55F);
      return KnifeFinishKit.getValue6(knifeFinishDivisor <= 1.0E-4F ? value4 : KnifeFinishKit.getValue(value4, BRIGHT_ORANGE_COLOR, knifeFinishFactor * (1.0F - knifeFinishValue) / knifeFinishDivisor), knifeFinishDivisor);
   }

   static int getRampAmount(KnifeFinish.Texel texel) {
      return GLOW_RAMP.getStopsRamp(KnifeFinishKit.getMax((texel.glowRecolorAmount - 0.06F) / 0.8F));
   }

   static int getAmountRamp(KnifeFinish.Texel texel) {
      float rampValue = KnifeFinishKit.getAmount(texel.rampAmount * 0.33F, texel.texelY * 0.14F, 0.9F, 2, texel.texelSeed);
      float rampFactor = getAmountFloat(texel);
      float knifeFinishLimit = 0.66F + 0.44F * rampValue * (1.0F - 0.5F * rampFactor) + 0.06F * (texel.glowRecolorAmount - 0.5F) + 0.3F * rampFactor;
      int stopsRamp = RAMP.getStopsRamp(KnifeFinishKit.getMax(knifeFinishLimit));
      int valueInt = KnifeFinishKit.getValueInt(stopsRamp, VIVID_ORANGE_COLOR, 0.5F * rampFactor);
      int valueInt4 = KnifeFinishKit.getValueInt(valueInt, knifeFinishValue, 0.45F * rampFactor * rampFactor);
      int amountRamp = KnifeFinishKit.getValue(valueInt4, DARK_RED_COLOR, 0.55F * KnifeFinishKit.getAmount3(texel, 0.25F));
      if (texel.rigAmount > -1.0E9F) {
         amountRamp = KnifeFinishKit.getValue(amountRamp, KnifeFinishKit.getValue(4719371), 0.3F * (1.0F - KnifeFinishKit.getAmountForFloat(0.0F, 0.2F, texel.rigAmount)));
      }

      return amountRamp;
   }

   static int getValueInt(KnifeFinish.Texel texel) {
      int rampAmount = getRampAmountForTexel(texel);
      int knifeFinishValue = KnifeFinishKit.getValueInt(KnifeFinishKit.getValue(texel, KnifeFinishKit.getValue(855314), 0.7F), rampAmount, 0.3F * KnifeFinishKit.getAmountForTexel(texel));
      return KnifeFinishKit.getValue(knifeFinishValue, rampAmount, 0.5F * getAmount4(texel));
   }

   static KnifeFinish.Finish getBuildORANGE_COLOR() {
      int knifeFinishValue = KnifeFinishKit.getValue(4869461);
      int knifeFinishValue2 = KnifeFinishKit.getValue(15212558);
      int stopsRamp = KnifeFinishKit.getValue(16734740);
      int knifeFinishValue4 = KnifeFinishKit.getValue(16747556);
      return KnifeFinish.getBuilder("Ruby")
         .paint(KnifeFinish.fadeSapphireValue, texel -> {
            int var1x = getAmountRamp(texel);
            if (texel.sheenValue == 2) {
               var1x = KnifeFinishKit.getValue4(var1x, 0.16F);
            }

            return KnifeFinishKit.getValue(texel, var1x, 0.95F, KnifeFinishKit.getValue(var1x, ORANGE_COLOR, 0.7F), LIGHT_ORANGE_COLOR);
         })
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), texel2 -> KnifeFinishKit.getValue(texel2, knifeFinishValue, 0.4F, 0.8F))
         .paint(KnifeFinish.getValue(5), texel3 -> KnifeFinishKit.getValue(texel3, KnifeFinishKit.getValue(5461598), 0.5F, 0.6F))
         .recolor(KnifeFinish.getValue(6), 3816514)
         .recolor(KnifeFinish.getValue(7), 10133160)
         .glow(KnifeFinish.fadeSapphireValue, KnifeFinishReference::getValueForTexel)
         .glow(
            KnifeFinish.getValue(5),
            texel4 -> KnifeFinishKit.getValue6(KnifeFinishKit.getValue(knifeFinishValue2, stopsRamp, 0.6F * KnifeFinishKit.getMax(texel4.glowRecolorAmount)), getAmountForTexel(texel4))
         )
         .glow(
            KnifeFinish.getValue(4),
            texel5 -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(stopsRamp, knifeFinishValue4, 0.5F * KnifeFinishKit.getAmountForTexelFloat(texel5, 0.12F) + 0.3F * KnifeFinishKit.getMax(texel5.glowRecolorAmount)),
               0.07F + 0.93F * KnifeFinishKit.getAmountForTexelFloat(texel5, 0.16F)
            )
         )
         .glow(KnifeFinish.getValue(3), ember -> KnifeFinishKit.getValue6(knifeFinishValue2, getRigKnife(ember, false)))
         .build();
   }

   static List<KnifeFinish.Finish> getItems() {
      return List.of(getBuildSAPPHIRE_RAMP(), getBuildORANGE_COLOR(), getBuild());
   }

   static KnifeFinish.Finish getBuildSAPPHIRE_RAMP() {
      int knifeFinishValue = KnifeFinishKit.getValue(2895155);
      int knifeFinishValue2 = KnifeFinishKit.getValue(16727982);
      int stopsRamp = KnifeFinishKit.getValue(16747222);
      return KnifeFinish.getBuilder("Fade")
         .paint(KnifeFinish.fadeSapphireValue, texel -> {
            int var1x = SAPPHIRE_RAMP.getStopsRamp(getAmount(texel));
            if (texel.sheenValue == 2) {
               var1x = KnifeFinishKit.getValue4(var1x, 0.22F);
            }

            return KnifeFinishKit.getValue(texel, var1x, 0.9F, 0.5F);
         })
         .paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), grip -> KnifeFinishKit.getValueForTexelInt(grip, knifeFinishValue, 0.7F))
         .paint(KnifeFinish.getValue(5), texel2 -> KnifeFinishKit.getValue(texel2, KnifeFinishKit.getValue(3487292), 0.5F, 0.3F))
         .recolor(KnifeFinish.getValue(6), 3224120)
         .recolor(KnifeFinish.getValue(7), 9080214)
         .glow(KnifeFinish.fadeSapphireValue, texel3 -> KnifeFinishKit.getValueForTexelIntFloat(texel3, SAPPHIRE_RAMP.getStopsRamp(getAmount(texel3)), 0.2F))
         .glow(
            KnifeFinish.getValue(5),
            hot -> KnifeFinishKit.getValue6(KnifeFinishKit.getValue(knifeFinishValue2, stopsRamp, 0.3F * KnifeFinishKit.getAmountForTexelFloat(hot, 0.05F)), getAmountForTexel(hot))
         )
         .glow(
            KnifeFinish.getValue(4),
            pink -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(knifeFinishValue2, stopsRamp, 0.35F * KnifeFinishKit.getAmountForTexelFloat(pink, 0.12F)), 0.07F + 0.93F * KnifeFinishKit.getAmountForTexelFloat(pink, 0.16F)
            )
         )
         .glow(KnifeFinish.getValue(3), pink -> KnifeFinishKit.getValue6(knifeFinishValue2, getRigKnife(pink, false)))
         .build();
   }

   static float getAmount(KnifeFinish.Texel texel) {
      float knifeFinishValue = 1.06F * texel.glowRecolorAmount - 0.05F + 0.1F * (0.5F - texel.edgePosition) + 0.025F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.7F, texel.texelY * 0.25F, 3, texel.texelSeed);
      return KnifeFinishKit.getMax(knifeFinishValue);
   }

   static float getAmountForTexel(KnifeFinish.Texel texel) {
      return texel.rigEdgeDistance >= 1.0E9F ? 1.0F : KnifeFinishKit.getAmountForTexelFloat(texel, 0.09F);
   }

   static float getAmountFloat(KnifeFinish.Texel texel) {
      float knifeFinishValue = 0.05F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.4F, texel.texelY * 0.35F, 2, texel.texelSeed + 71);
      float knifeFinishFactor = 0.8F + 0.2F * KnifeFinishKit.getAmount3(texel.rampAmount * 0.9F, texel.texelY * 0.22F, 3, texel.texelSeed + 83);
      return KnifeFinishKit.getAmountFloat(texel.edgePosition, 0.45F + knifeFinishValue, 0.2F) * KnifeFinishKit.getAmountForFloat(0.1F, 0.5F, texel.glowRecolorAmount) * knifeFinishFactor;
   }

   static float getAmount4(KnifeFinish.Texel texel) {
      float knifeFinishFactor = 1.0F - KnifeFinishKit.getAmountForFloat(0.04F, 0.42F, texel.edgeDistance);
      return knifeFinishFactor * knifeFinishFactor * KnifeFinishKit.getAmountForFloat(0.04F, 0.16F, texel.glowRecolorAmount) * getRigAmount(texel);
   }

   static int getRampAmountForTexel(KnifeFinish.Texel texel) {
      return GLOW_RAMP.getStopsRamp(0.1F + 0.9F * KnifeFinishKit.getMax(texel.glowRecolorAmount));
   }

   static float getRigAmount(KnifeFinish.Texel texel) {
      KnifeFinishBake.Geometry geometry = KnifeFinishBake.getGeometry(texel.knifeRig);
      float[] rigValues = geometry == null ? null : geometry.rigFloats[1];
      float rigFactor = rigValues == null ? 1.0F : KnifeFinishKit.getAmountForFloat(rigValues[1], rigValues[1] + 0.6F, texel.texelY);
      return rigFactor * KnifeFinishKit.getAmountForFloat(0.02F, 0.08F, texel.glowRecolorAmount);
   }
}
