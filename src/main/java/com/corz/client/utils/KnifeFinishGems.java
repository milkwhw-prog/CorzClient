package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.KnifeFinishKit;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishReference;

final class KnifeFinishGems {
   static final KnifeFinishKit.Ramp BASE_GEM_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.2F, 0.42F, 0.64F, 0.84F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(132640),
         KnifeFinishKit.getValue(463966),
         KnifeFinishKit.getValue(668334),
         KnifeFinishKit.getValue(1203446),
         KnifeFinishKit.getValue(3050751),
         KnifeFinishKit.getValue(8177407)
      }
   );
   static final KnifeFinishGems.Gem GEM = new KnifeFinishGems.Gem(
      KnifeFinishGems.BASE_GEM_RAMP,
      0.56F,
      0.82F,
      KnifeFinishKit.getValue(133690),
      0.42F,
      KnifeFinishKit.getValue(3979519),
      0.34F,
      -9.0F,
      KnifeFinishKit.getValue(3844351),
      0.3F,
      KnifeFinishKit.getValue(133168),
      KnifeFinishKit.getValue(11855615)
   );
   static final KnifeFinishKit.Ramp HIGHLIGHT_GEM_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.2F, 0.42F, 0.64F, 0.84F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(71688),
         KnifeFinishKit.getValue(211479),
         KnifeFinishKit.getValue(356393),
         KnifeFinishKit.getValue(895034),
         KnifeFinishKit.getValue(2282578),
         KnifeFinishKit.getValue(6747520)
      }
   );
   static final KnifeFinishGems.Gem SHEEN_GEM = new KnifeFinishGems.Gem(
      HIGHLIGHT_GEM_RAMP,
      0.56F,
      0.82F,
      KnifeFinishKit.getValue(74768),
      0.42F,
      KnifeFinishKit.getValue(3997540),
      0.24F,
      0.0F,
      KnifeFinishKit.getValue(7143262),
      0.28F,
      KnifeFinishKit.getValue(73740),
      KnifeFinishKit.getValue(10289034)
   );
   static final KnifeFinishKit.Ramp DEPTH_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.3F, 0.55F, 0.8F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(131334),
         KnifeFinishKit.getValue(525850),
         KnifeFinishKit.getValue(1313848),
         KnifeFinishKit.getValue(2234206),
         KnifeFinishKit.getValue(3418238)
      }
   );
   static final int knifeFinishValue = KnifeFinishKit.getValue(7615712);
   static final int DEEP_BLUE_COLOR = KnifeFinishKit.getValue(2907376);
   static final int DARK_TINT = KnifeFinishKit.getValue(65539);
   static final int LAVENDER_TINT = KnifeFinishKit.getValue(12100863);
   static final KnifeFinishKit.Ramp SHEEN_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.25F, 0.5F, 0.75F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(10120447),
         KnifeFinishKit.getValue(6192383),
         KnifeFinishKit.getValue(5220080),
         KnifeFinishKit.getValue(14183648),
         KnifeFinishKit.getValue(10120447)
      }
   );
   static final KnifeFinishKit.Ramp RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.22F, 0.45F, 0.68F, 0.86F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(328456),
         KnifeFinishKit.getValue(2754616),
         KnifeFinishKit.getValue(9049208),
         KnifeFinishKit.getValue(15216794),
         KnifeFinishKit.getValue(16743624),
         KnifeFinishKit.getValue(16765164)
      }
   );
   static final KnifeFinishKit.Ramp BLEND_RAMP = KnifeFinishKit.getRamp(
      new float[]{0.0F, 0.22F, 0.45F, 0.68F, 0.86F, 1.0F},
      new int[]{
         KnifeFinishKit.getValue(328456),
         KnifeFinishKit.getValue(1183304),
         KnifeFinishKit.getValue(3352240),
         KnifeFinishKit.getValue(6966527),
         KnifeFinishKit.getValue(11046143),
         KnifeFinishKit.getValue(14471167)
      }
   );
   static final int DEEP_PINK = KnifeFinishKit.getValue4(KnifeFinishKit.getValue(16739016), 0.55F);
   static final int PINK_WHITE = KnifeFinishKit.getValue(16774396);

   static float getAmount(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getAmountForFloat(-0.2F, 0.2F, KnifeFinishKit.getAmount3(texel.rampAmount * 0.3F, texel.texelY * 0.12F, 2, texel.texelSeed + 131));
   }

   static float getMax(KnifeFinish.Texel texel) {
      return Math.max(getAmount(texel, 3.6F, 0.12F, 0.04F, 301), getAmount(texel, 8.0F, 0.05F, 0.022F, 307));
   }

   static int getBrightHue(KnifeFinish.Texel texel, KnifeFinishGems.Gem gem) {
      float brightHueValue = getAmount6(texel);
      float amountFloat = getAmountFloat(texel);
      float brightHueFactor = 0.75F * brightHueValue + 0.35F * amountFloat;
      int stopsRamp = gem.ramp().getStopsRamp(KnifeFinishKit.getMax(gem.mid() + gem.amp() * brightHueFactor + 0.06F * (texel.glowRecolorAmount - 0.5F)));
      int valueInt = KnifeFinishKit.getValueInt(stopsRamp, gem.bright(), gem.brightK() * KnifeFinishKit.getAmountForFloat(0.16F, 0.5F, brightHueFactor));
      if (gem.brightHue() != 0.0F) {
         valueInt = KnifeFinishKit.getValue5(valueInt, gem.brightHue() * KnifeFinishKit.getAmountForFloat(0.16F, 0.42F, brightHueFactor));
      }

      float brightHueFactor2 = getAmount(texel);
      int brightHueValue4 = KnifeFinishKit.getValue(valueInt, gem.vein(), gem.veinK() * brightHueFactor2 * KnifeFinishKit.getAmountFloat(amountFloat, 0.04F, 0.022F + 0.03F * brightHueFactor2));
      int valueInt5 = KnifeFinishKit.getValueInt(brightHueValue4, gem.core(), gem.coreK() * getAmountForTexel(texel));
      return KnifeFinishKit.getValue(valueInt5, gem.rim(), 0.3F * getAmount5(texel));
   }

   static KnifeFinish.Finish getBuild() {
      return getGlowRecolor(
            KnifeFinish.getBuilder("Nebula")
               .paint(KnifeFinish.fadeSapphireValue, texel -> {
                  int amountRamp = getAmountRamp(texel);
                  if (texel.sheenValue == 2) {
                     amountRamp = KnifeFinishKit.getValue4(amountRamp, 0.1F);
                  }

                  int knifeFinishValue = KnifeFinishKit.getValue(texel, amountRamp, 0.9F, KnifeFinishKit.getValue(amountRamp, KnifeFinishKit.getValue(16743115), 0.7F));
                  return KnifeFinishKit.getValue(knifeFinishValue, PINK_WHITE, getMax(texel));
               })
               .glow(
                  KnifeFinish.fadeSapphireValue,
                  texel2 -> {
                     float max2 = getMax(texel2);
                     return KnifeFinishKit.getValue6(
                        KnifeFinishKit.getValue(DEEP_PINK, PINK_WHITE, KnifeFinishKit.getAmountForFloat(0.02F, 0.25F, max2)),
                        Math.max(0.18F * KnifeFinishKit.getAmountForTexel(texel2), 0.9F * max2)
                     );
                  }
               ),
            KnifeFinishKit.getValue(2368555),
            KnifeFinishKit.getValue(3026486),
            2236968,
            8290442,
            KnifeFinishKit.getValue(16727982),
            KnifeFinishKit.getValue(12729599),
            KnifeFinishKit.getValue(9067775)
         )
         .build();
   }

   static KnifeFinish.Builder getGlowRecolor(KnifeFinish.Builder builder, int glowRecolorValue, int glowRecolorValue2, int glowRecolorValue3, int glowRecolorValue4, int glowRecolorValue5, int glowRecolorValue6, int stopsRamp) {
      return builder.paint(KnifeFinish.getValue(3) | KnifeFinish.getValue(4), texel -> KnifeFinishKit.getValue(texel, glowRecolorValue, 0.4F, 0.8F))
         .paint(KnifeFinish.getValue(5), accentx -> KnifeFinishKit.getValue(accentx, glowRecolorValue2, 0.5F, 0.6F))
         .recolor(KnifeFinish.getValue(6), glowRecolorValue3)
         .recolor(KnifeFinish.getValue(7), glowRecolorValue4)
         .glow(
            KnifeFinish.getValue(5),
            glowBx -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(glowRecolorValue5, glowRecolorValue6, 0.6F * KnifeFinishKit.getMax(glowBx.glowRecolorAmount)), KnifeFinishReference.getAmountForTexel(glowBx)
            )
         )
         .glow(
            KnifeFinish.getValue(4),
            glowBx -> KnifeFinishKit.getValue6(
               KnifeFinishKit.getValue(glowRecolorValue6, stopsRamp, 0.5F * KnifeFinishKit.getAmountForTexelFloat(glowBx, 0.12F) + 0.3F * KnifeFinishKit.getMax(glowBx.glowRecolorAmount)),
               0.07F + 0.93F * KnifeFinishKit.getAmountForTexelFloat(glowBx, 0.16F)
            )
         )
         .glow(KnifeFinish.getValue(3), glowAx -> KnifeFinishKit.getValue6(glowRecolorValue5, KnifeFinishReference.getRigKnife(glowAx, false)));
   }

   static float getAmountForTexel(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getAmountFloat(texel.edgePosition, 0.45F, 0.2F) * KnifeFinishKit.getAmountForFloat(0.12F, 0.45F, texel.glowRecolorAmount);
   }

   private KnifeFinishGems() {
   }

   static float getAmountFloat(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getAmount(texel.rampAmount * 0.62F, texel.texelY * 0.26F, 1.5F, 3, texel.texelSeed + 71);
   }

   static List<KnifeFinish.Finish> getItems() {
      return List.of(getGEM(), getLAVENDER_TINT(), getSHEEN_GEM(), getBuild());
   }

   static float getAmount(KnifeFinish.Texel texel, float knifeFinishFactor, float knifeFinishValue, float knifeFinishFactor2, int knifeFinishValue2) {
      float knifeFinishValue3 = texel.rampAmount * knifeFinishFactor;
      float hashRegistryValue = texel.texelY * knifeFinishFactor;
      float knifeFinishBound = 0.0F;
      int floor5 = (int)Math.floor(knifeFinishValue3);
      int floor6 = (int)Math.floor(hashRegistryValue);

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            int knifeFinishValue7 = floor5 + j;
            int floorSeed = floor6 + i;
            int knifeFinishValue9 = KnifeFinishKit.getValue(knifeFinishValue7, floorSeed, texel.texelSeed + knifeFinishValue2);
            if (!((knifeFinishValue9 & 1023) >= knifeFinishValue * 1024.0F)) {
               float knifeFinishValue10 = knifeFinishValue7 + (knifeFinishValue9 >>> 10 & 1023) / 1024.0F;
               float knifeFinishValue11 = floorSeed + (knifeFinishValue9 >>> 20 & 1023) / 1024.0F;
               float knifeFinishFactor3 = (knifeFinishValue3 - knifeFinishValue10) / knifeFinishFactor;
               float knifeFinishFactor4 = (hashRegistryValue - knifeFinishValue11) / knifeFinishFactor;
               float distance = (float)Math.sqrt(knifeFinishFactor3 * knifeFinishFactor3 + knifeFinishFactor4 * knifeFinishFactor4);
               float knifeFinishLimit = KnifeFinishKit.getAmount(knifeFinishValue7, floorSeed, texel.texelSeed + knifeFinishValue2 + 1);
               float knifeFinishValue12 = knifeFinishFactor2 * (0.55F + 0.45F * knifeFinishLimit);
               if (!(distance > knifeFinishValue12 * 3.5F)) {
                  float pilotStatusBound = 1.0F - KnifeFinishKit.getAmountForFloat(knifeFinishValue12 * 0.3F, knifeFinishValue12 + 0.6F * texel.texelSize, distance);
                  float knifeFinishFactor5 = 1.0F - KnifeFinishKit.getAmountForFloat(0.0F, knifeFinishValue12 * 3.2F, distance);
                  float knifeFinishValue13 = knifeFinishLimit > 0.9F
                     ? (
                           KnifeFinishKit.getAmountFloat(knifeFinishFactor3, 0.0F, 0.5F * texel.texelSize + 0.004F)
                              + KnifeFinishKit.getAmountFloat(knifeFinishFactor4, 0.0F, 0.5F * texel.texelSize + 0.004F)
                        )
                        * (1.0F - KnifeFinishKit.getAmountForFloat(0.0F, knifeFinishValue12 * 3.5F, distance))
                     : 0.0F;
                  knifeFinishBound = Math.max(knifeFinishBound, (0.35F + 0.65F * knifeFinishLimit * knifeFinishLimit) * Math.max(pilotStatusBound, Math.max(0.4F * knifeFinishFactor5 * knifeFinishFactor5, 0.6F * knifeFinishValue13)));
               }
            }
         }
      }

      return knifeFinishBound;
   }

   static int getRamp(KnifeFinish.Texel texel, float rampValue) {
      float amount6 = getAmount6(texel);
      float amountFloat = getAmountFloat(texel);
      float rampValue4 = 0.75F * amount6 + 0.35F * amountFloat;
      int stopsRamp = DEPTH_RAMP.getStopsRamp(KnifeFinishKit.getMax(0.5F + 0.9F * rampValue4));
      int rampValue5 = KnifeFinishKit.getValue(knifeFinishValue, DEEP_BLUE_COLOR, KnifeFinishKit.getAmountForFloat(-0.1F, 0.45F, rampValue + 0.4F * amountFloat));
      int valueInt = KnifeFinishKit.getValueInt(
         stopsRamp, rampValue5, 0.62F * KnifeFinishKit.getAmountForFloat(-0.12F, 0.4F, rampValue4) + 0.12F * KnifeFinishKit.getAmountForFloat(-0.1F, 0.3F, amountFloat)
      );
      float rampValue7 = getAmount(texel);
      int rampValue8 = KnifeFinishKit.getValue(valueInt, DARK_TINT, 0.55F * rampValue7 * KnifeFinishKit.getAmountFloat(amountFloat, 0.04F, 0.022F + 0.03F * rampValue7));
      int valueInt9 = KnifeFinishKit.getValueInt(rampValue8, DEEP_BLUE_COLOR, 0.16F * getAmountForTexel(texel));
      return KnifeFinishKit.getValue(valueInt9, DARK_TINT, 0.3F * getAmount5(texel));
   }

   static int getAmountRamp(KnifeFinish.Texel texel) {
      float rampValue = KnifeFinishKit.getAmount(texel.rampAmount * 0.42F, texel.texelY * 0.2F, 1.5F, 3, texel.texelSeed + 5);
      float amount3 = KnifeFinishKit.getAmount3(texel.rampAmount * 0.22F, texel.texelY * 0.1F, 2, texel.texelSeed + 9);
      float rampValue3 = KnifeFinishKit.getAmount(texel.rampAmount * 0.9F + 0.6F * rampValue, texel.texelY * 0.4F, 3, texel.texelSeed + 13);
      float rampFactor = KnifeFinishKit.getAmountForFloat(-0.3F, 0.45F, rampValue)
         * (1.0F - 0.55F * KnifeFinishKit.getAmountForFloat(0.55F, 0.9F, rampValue3) * KnifeFinishKit.getAmountForFloat(0.7F, 0.2F, rampValue));
      int rampValue4 = KnifeFinishKit.getValue(RAMP.getStopsRamp(rampFactor), BLEND_RAMP.getStopsRamp(rampFactor), KnifeFinishKit.getAmountForFloat(-0.3F, 0.12F, amount3));
      int valueInt = KnifeFinishKit.getValueInt(rampValue4, KnifeFinishKit.getValue(16734904), 0.25F * getAmountForTexel(texel) * rampFactor);
      return KnifeFinishKit.getValue(valueInt, KnifeFinishKit.getValue(197126), 0.35F * getAmount5(texel));
   }

   static int getRampAmount(KnifeFinish.Texel texel, float rampValue) {
      return SHEEN_RAMP.getStopsRamp(KnifeFinishKit.getAmount(1.3F * KnifeFinishKit.getAmount3(texel) + rampValue + 0.2F * texel.glowRecolorAmount));
   }

   static KnifeFinish.Finish getGEM() {
      return getGlowRecolor(
            KnifeFinish.getBuilder("Sapphire")
               .paint(KnifeFinish.fadeSapphireValue, texel -> getSheen(texel, GEM, 0.3F))
               .glow(KnifeFinish.fadeSapphireValue, texel2 -> KnifeFinishKit.getValueForTexelIntFloat(texel2, KnifeFinishKit.getValue(4892927), 0.2F)),
            KnifeFinishKit.getValue(4606548),
            KnifeFinishKit.getValue(5264478),
            3685186,
            10133160,
            KnifeFinishKit.getValue(1331455),
            KnifeFinishKit.getValue(2002175),
            KnifeFinishKit.getValue(4577023)
         )
         .build();
   }

   static int getSheen(KnifeFinish.Texel texel, KnifeFinishGems.Gem gem, float sheenValue) {
      int brightHue = getBrightHue(texel, gem);
      if (texel.sheenValue == 2) {
         brightHue = KnifeFinishKit.getValueInt(brightHue, gem.sheen(), sheenValue);
      }

      return KnifeFinishKit.getValue(texel, brightHue, 0.95F, KnifeFinishKit.getValue(brightHue, gem.sheen(), 0.7F));
   }

   static KnifeFinish.Finish getSHEEN_GEM() {
      return getGlowRecolor(
            KnifeFinish.getBuilder("Emerald")
               .paint(KnifeFinish.fadeSapphireValue, texel -> getSheen(texel, SHEEN_GEM, 0.26F))
               .glow(KnifeFinish.fadeSapphireValue, texel2 -> KnifeFinishKit.getValueForTexelIntFloat(texel2, KnifeFinishKit.getValue(3997546), 0.2F)),
            KnifeFinishKit.getValue(4540749),
            KnifeFinishKit.getValue(5198679),
            3619390,
            10133156,
            KnifeFinishKit.getValue(1032778),
            KnifeFinishKit.getValue(2945130),
            KnifeFinishKit.getValue(9240500)
         )
         .build();
   }

   static KnifeFinish.Finish getLAVENDER_TINT() {
      return getGlowRecolor(
            KnifeFinish.getBuilder("Black Pearl").seed(1143176573).paint(KnifeFinish.fadeSapphireValue, texel -> {
               float knifeFinishValue = KnifeFinishKit.getAmount3(texel.rampAmount * 0.4F, texel.texelY * 0.16F, 2, texel.texelSeed + 57);
               int ramp = getRamp(texel, knifeFinishValue);
               if (texel.sheenValue == 2) {
                  ramp = KnifeFinishKit.getValueInt(ramp, LAVENDER_TINT, 0.2F);
               }

               int rampAmount = getRampAmount(texel, 0.3F * knifeFinishValue);
               int knifeFinishValue2 = KnifeFinishKit.getValue(texel, ramp, 0.95F, rampAmount);
               return KnifeFinishKit.getValueInt(knifeFinishValue2, rampAmount, 0.16F * KnifeFinishKit.getAmountForFloat(0.62F, 1.15F, KnifeFinishKit.getAmount(texel)));
            }).glow(KnifeFinish.fadeSapphireValue, texel2 -> KnifeFinishKit.getValueForTexelIntFloat(texel2, getRampAmount(texel2, 0.0F), 0.18F)),
            KnifeFinishKit.getValue(3158586),
            KnifeFinishKit.getValue(3816517),
            2763826,
            9080472,
            KnifeFinishKit.getValue(8011007),
            KnifeFinishKit.getValue(5922047),
            KnifeFinishKit.getValue(5220607)
         )
         .build();
   }

   static float getAmount5(KnifeFinish.Texel texel) {
      return texel.rigAmount > -1.0E9F ? 1.0F - KnifeFinishKit.getAmountForFloat(0.0F, 0.2F, texel.rigAmount) : 0.0F;
   }

   static float getAmount6(KnifeFinish.Texel texel) {
      return KnifeFinishKit.getAmount(texel.rampAmount * 0.33F, texel.texelY * 0.14F, 0.9F, 2, texel.texelSeed);
   }

   record Gem(
      KnifeFinishKit.Ramp ramp,
      float mid,
      float amp,
      int vein,
      float veinK,
      int bright,
      float brightK,
      float brightHue,
      int core,
      float coreK,
      int rim,
      int sheen
   ) {
   }
}
