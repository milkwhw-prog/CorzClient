package com.corz.client.utils;

import com.corz.client.world.PosBlockUtilsBonus;

public final class VecShieldUtilsToughness {

   public static int getMax(float vecShieldValue) {
      return (int)Math.max(1.0F, vecShieldValue / 4.0F);
   }

   public static float getAmount(float vecShieldFactor, float sharpness, float vecShieldFactor3, boolean vecShieldFlag) {
      float vecShieldValue = vecShieldFactor * (0.2F + vecShieldFactor3 * vecShieldFactor3 * 0.8F);
      if (vecShieldFlag) {
         vecShieldValue *= 1.5F;
      }

      return vecShieldValue + sharpness * vecShieldFactor3;
   }

   private VecShieldUtilsToughness() {
   }

   public static float getToughnessAmp(float toughnessAmpValue, float sharpness, float toughnessAmpValue3, boolean toughnessAmpFlag, PosBlockUtilsBonus.Armor armor, float toughnessAmpValue4) {
      PosBlockUtilsBonus.Armor armor2 = armor == null ? PosBlockUtilsBonus.Armor.INSTANCE : armor;
      return PosBlockUtilsBonus.getToughnessEpf(
         getAmount(toughnessAmpValue, sharpness, toughnessAmpValue3, toughnessAmpFlag), new PosBlockUtilsBonus.Armor(armor2.armor(), armor2.toughness(), armor2.resistanceAmp(), toughnessAmpValue4, -1)
      );
   }
}
