package com.corz.client.utils;

import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.PosBlockUtilsBonus;

public final class TargetScoring {

   public static double getBaseCrystalDamage(VecBlockingData vecBlockingData) {
      PosBlockUtilsBonus.Armor armor = vecBlockingData.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : vecBlockingData.armor();
      float armorDifficultyValue = (float)BoxLengthUtils.getAmount3(1.0, 1.0, 6.0);
      float toughnessEpfLimit = PosBlockUtilsBonus.getAmount(armorDifficultyValue, armor.difficulty());
      double toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, armor);
      if (vecBlockingData.isHoleSides()) {
         toughnessEpf *= 0.45;
      }

      float calibration = vecBlockingData.calibration();
      return toughnessEpf * (Float.isFinite(calibration) && calibration > 0.0F ? calibration : 1.0F);
   }

   public static double scoreTargetPriority(VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, float durabilityArmorLimit, boolean durabilityArmorFlag) {
      if (vecBlockingData == null) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double armorDifficulty = getBaseCrystalDamage(vecBlockingData);
         double durabilityArmorTotal = 4.0 * clampWithFloor(armorDifficulty / Math.max(1.0, (double)vecBlockingData.getHealthTrusted()), 0.0, 1.5);
         if (vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand()) {
            durabilityArmorTotal += 3.0;
         }

         if (vecBlockingData.armorMinDurability() < 0.5) {
            durabilityArmorTotal += 1.5 * (1.0 - vecBlockingData.armorMinDurability());
         }

         float durabilityArmorValue = absorptionPosData == null ? 20.0F : absorptionPosData.getAbsorptionHealth();
         if (Float.isFinite(durabilityArmorLimit) && durabilityArmorLimit > 0.0F) {
            durabilityArmorTotal += 2.0 * durabilityArmorLimit / Math.max(1.0, (double)durabilityArmorValue);
         }

         if (vecBlockingData.eating()) {
            durabilityArmorTotal++;
         }

         if (vecBlockingData.isHoleSides()) {
            durabilityArmorTotal += -1.0;
         }

         if (!vecBlockingData.visible()) {
            durabilityArmorTotal += -2.0;
         }

         if (vecBlockingData.immune()) {
            durabilityArmorTotal += -3.0;
         }

         double durabilityArmor = durabilityArmorTotal - 0.35 * Math.max(0.0, capDistance(vecBlockingData.distance(), durabilityArmorFlag) - 4.0);
         if (durabilityArmorFlag) {
            durabilityArmor += 2.5;
         }

         return durabilityArmor;
      }
   }

   private TargetScoring() {
   }

   public static double capDistance(double constArmorLimit, boolean constArmorFlag) {
      return constArmorFlag && constArmorLimit > 10.0 ? 10.0 : constArmorLimit;
   }

   private static double clampWithFloor(double constArmorBound, double constArmorLimit, double goalBound) {
      return constArmorBound < constArmorLimit ? constArmorLimit : Math.min(constArmorBound, goalBound);
   }
}
