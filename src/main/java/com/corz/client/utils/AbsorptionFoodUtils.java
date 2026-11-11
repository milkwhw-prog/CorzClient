package com.corz.client.utils;

import com.corz.client.entity.VecBlock;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData;

public final class AbsorptionFoodUtils {
   public static final float[] GAPPLE_HEALTH_THRESHOLDS = new float[]{12.0F, 14.0F, 16.0F};

   public static boolean isSafeToEatByThreat(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return false;
      } else {
         VecBlock.Threat threat = selfPopData.threat() == null ? VecBlock.Threat.INSTANCE : selfPopData.threat();
         return isSafeToEat(selfPopData.nearestEnemy(), selfPopData.pearlIncoming(), threat.maxIncoming(), threat.lethal());
      }
   }

   public static boolean isSafeToEat(double absorptionFoodLimit, boolean absorptionFoodFlag, float absorptionFoodValue, boolean absorptionFoodFlag2) {
      return absorptionFoodLimit >= 16.0 && !absorptionFoodFlag && absorptionFoodValue < 4.0F && !absorptionFoodFlag2;
   }

   public static boolean isHungryWithFoodAvailable(AbsorptionPosData absorptionPosData) {
      return absorptionPosData == null ? false : absorptionPosData.foodLevel() <= 14 && (absorptionPosData.food() > 0 || absorptionPosData.gapples() > 0);
   }

   public static boolean isSafeToEatExtended(double absorptionFoodLimit, boolean absorptionFoodFlag, float absorptionFoodValue, boolean absorptionFoodFlag2, int absorptionFoodLimit2, boolean absorptionFoodFlag3) {
      return absorptionFoodLimit >= 20.0 && !absorptionFoodFlag && absorptionFoodValue < 2.0F && !absorptionFoodFlag2 && absorptionFoodLimit2 >= 30 && !absorptionFoodFlag3;
   }

   static boolean isActiveComboState(CenterVecData.State state2) {
      if (state2 == null) {
         return false;
      } else {
         return switch (state2) {
            case OPENER, LAUNCH, JUGGLE, ANCHOR_BELOW, FINISHER -> true;
            default -> false;
         };
      }
   }

   public static boolean isFoodLevelCritical(AbsorptionPosData absorptionPosData) {
      return absorptionPosData != null && absorptionPosData.foodLevel() <= 6;
   }

   public static boolean isSafeToEatByThreatAndCombo(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return false;
      } else {
         VecBlock.Threat threat = selfPopData.threat() == null ? VecBlock.Threat.INSTANCE : selfPopData.threat();
         CenterVecData centerVecData = selfPopData.combo() == null ? CenterVecData.INSTANCE : selfPopData.combo();
         return isSafeToEatExtended(selfPopData.nearestEnemy(), selfPopData.pearlIncoming(), threat.maxIncoming(), threat.lethal(), threat.etaTicks(), isActiveComboState(centerVecData.state()));
      }
   }

   private AbsorptionFoodUtils() {
   }

   public static long computeNextCheckTick(boolean absorptionFoodFlag, boolean cooldownPlayerFlag, int absorptionFoodLimit, long absorptionFoodValue) {
      if (!cooldownPlayerFlag) {
         return absorptionFoodValue + 40L;
      } else {
         return absorptionFoodFlag && absorptionFoodLimit >= 30 ? absorptionFoodValue + 400L : absorptionFoodValue;
      }
   }

   public static boolean needsHealingOrFood(AbsorptionPosData absorptionPosData, CarefulBalancedType carefulBalancedType) {
      return shouldEatGappleForHealth(absorptionPosData, carefulBalancedType) || isHungryWithFoodAvailable(absorptionPosData);
   }

   public static boolean shouldEatGappleForHealth(AbsorptionPosData absorptionPosData, CarefulBalancedType carefulBalancedType) {
      if (absorptionPosData == null) {
         return false;
      } else {
         int ordinal = (carefulBalancedType == null ? CarefulBalancedType.TERMINATOR : carefulBalancedType).ordinal();
         return absorptionPosData.gapples() > 0 && absorptionPosData.absorption() < 4.0F && (absorptionPosData.health() <= GAPPLE_HEALTH_THRESHOLDS[ordinal] || absorptionPosData.getAbsorptionHealth() <= GAPPLE_HEALTH_THRESHOLDS[ordinal] + 2.0F);
      }
   }
}
