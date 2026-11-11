package com.corz.client.utils;

import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData;

public final class EngagementGateRules {
   private static final EngagementGateRules.Gate GATE_OK = new EngagementGateRules.Gate(true, "ok");

   public static boolean canEngageTarget(
      CarefulBalancedType carefulBalancedType, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData, boolean carefulDistanceFlag, double carefulDistanceLimit, double counterVecValue
   ) {
      if (absorptionPosData != null && isTargetAlive(vecBlockingData) && !vecBlockingData.immune()) {
         if (!(vecBlockingData.distance() <= counterVecValue)) {
            return false;
         } else {
            return absorptionPosData.totems() >= 1 && !carefulDistanceFlag ? carefulBalancedType != CarefulBalancedType.CAREFUL || vecBlockingData.distance() <= carefulDistanceLimit : false;
         }
      } else {
         return false;
      }
   }

   private static boolean canFollowRecombo(CenterVecData.State state) {
      return state == CenterVecData.State.FOLLOW || state == CenterVecData.State.PEARL_FOLLOW || state == CenterVecData.State.RECOMBO;
   }

   static boolean isTargetAlive(VecBlockingData vecBlockingData) {
      if (vecBlockingData == null) {
         return false;
      } else {
         return vecBlockingData.entity() == null ? true : vecBlockingData.entity().isAlive() && !vecBlockingData.entity().isRemoved();
      }
   }

   public static CenterVecData.Abort getAbortReason(EngagementGateRules.Inputs inputs) {
      if (inputs != null && inputs.state() != null && inputs.state() != CenterVecData.State.IDLE) {
         int terminator = getStyleIndex(inputs.style());
         if (inputs.defensiveOrEscape()) {
            return CenterVecData.Abort.POSTURE;
         } else if (inputs.ticksSinceOwnPop() > 10 || inputs.state() == CenterVecData.State.FINISHER && inputs.killInTicks() <= 3) {
            if (inputs.selfEhp() > ConstVec.episodeHealTable[terminator] && (!(inputs.selfEhp() <= 12.0F) || inputs.totemConfirmed())) {
               if (inputs.lethalThreat()) {
                  return CenterVecData.Abort.LETHAL_THREAT;
               } else if (inputs.healNow()) {
                  return CenterVecData.Abort.HEAL;
               } else if (!inputs.targetValid()) {
                  return CenterVecData.Abort.TARGET;
               } else if (inputs.totems() > 0 && (inputs.crystals() > 0 || canFollowRecombo(inputs.state()))) {
                  return inputs.episodeTicks() >= 600 ? CenterVecData.Abort.EPISODE_CAP : CenterVecData.Abort.NONE;
               } else {
                  return CenterVecData.Abort.RESOURCES;
               }
            } else {
               return CenterVecData.Abort.LOW_HP;
            }
         } else {
            return CenterVecData.Abort.OWN_POP;
         }
      } else {
         return CenterVecData.Abort.NONE;
      }
   }

   private EngagementGateRules() {
   }

   private static int getStyleIndex(CarefulBalancedType carefulBalancedType) {
      return (carefulBalancedType == null ? CarefulBalancedType.TERMINATOR : carefulBalancedType).ordinal();
   }

   public static EngagementGateRules.Gate checkEngageGate(
      CarefulBalancedType carefulBalancedType,
      AbsorptionPosData absorptionPosData,
      VecBlock.Threat threat,
      VecBlockingData vecBlockingData,
      boolean totemsCrystalsFlag,
      boolean totemsCrystalsFlag2,
      boolean totemsCrystalsFlag3,
      WeakFullUtils.Lift lift
   ) {
      if (absorptionPosData == null) {
         return new EngagementGateRules.Gate(false, "self");
      } else {
         int terminator = getStyleIndex(carefulBalancedType);
         VecBlock.Threat threat3 = threat == null ? VecBlock.Threat.INSTANCE : threat;
         if (!absorptionPosData.totemConfirmed()) {
            return new EngagementGateRules.Gate(false, "totem");
         } else if (!(absorptionPosData.getAbsorptionHealth() >= ConstVec.totemTotemsAmounts[terminator])) {
            return new EngagementGateRules.Gate(false, "ehp");
         } else if (absorptionPosData.totems() < ConstVec.totemsCrystalsValues[terminator]) {
            return new EngagementGateRules.Gate(false, "totems");
         } else if (absorptionPosData.crystals() < 8) {
            return new EngagementGateRules.Gate(false, "crystals");
         } else if (!(threat3.maxIncoming() <= 0.6 * absorptionPosData.getAbsorptionHealth())) {
            return new EngagementGateRules.Gate(false, "threat");
         } else if (absorptionPosData.usingItem()) {
            return new EngagementGateRules.Gate(false, "using");
         } else if (absorptionPosData.foodLevel() <= 6) {
            return new EngagementGateRules.Gate(false, "food");
         } else if (totemsCrystalsFlag2) {
            return new EngagementGateRules.Gate(false, "liquid");
         } else if (!isTargetAlive(vecBlockingData)) {
            return new EngagementGateRules.Gate(false, "target");
         } else if (vecBlockingData.immune()) {
            return new EngagementGateRules.Gate(false, "immune");
         } else if (totemsCrystalsFlag) {
            return new EngagementGateRules.Gate(false, "friend");
         } else {
            return !totemsCrystalsFlag3 || lift != null && lift != WeakFullUtils.Lift.NONE ? GATE_OK : new EngagementGateRules.Gate(false, "lift");
         }
      }
   }

   public record Gate(boolean ok, String why) {
   }

   public record Inputs(
      CenterVecData.State state,
      boolean defensiveOrEscape,
      int ticksSinceOwnPop,
      int killInTicks,
      float selfEhp,
      boolean totemConfirmed,
      int totems,
      int crystals,
      boolean lethalThreat,
      boolean healNow,
      boolean targetValid,
      int episodeTicks,
      CarefulBalancedType style
   ) {
   }
}
