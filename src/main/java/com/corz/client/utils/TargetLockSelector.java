package com.corz.client.utils;

import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import java.util.List;
import com.corz.client.utils.TargetScoring;

public final class TargetLockSelector {
   private int outOfRangeTicks;
   private int currentTargetId = -1;
   private int challengerId = -1;
   private long latestObservedTick = Long.MIN_VALUE;
   private VecBlockingData currentTarget;
   private int challengerStreak;

   private void switchTarget(VecBlockingData next) {
      this.currentTarget = next;
      this.currentTargetId = next == null ? -1 : next.id();
      this.challengerId = -1;
      this.challengerStreak = 0;
      this.outOfRangeTicks = 0;
   }

   static int getSwitchStreakRequired(boolean blockingVecFlag) {
      return blockingVecFlag ? 20 : 6;
   }

   public VecBlockingData getCurrentTarget() {
      return this.currentTarget;
   }

   public VecBlockingData selectTarget(long distanceValue, List<VecBlockingData> list, AbsorptionPosData absorptionPosData, VecBlock.Threat threat, double groundY) {
      VecBlock.Threat threat3 = threat == null ? VecBlock.Threat.INSTANCE : threat;
      VecBlockingData next = null;
      if (this.currentTargetId >= 0 && list != null) {
         for (VecBlockingData vecBlockingData : list) {
            if (vecBlockingData != null && vecBlockingData.id() == this.currentTargetId) {
               next = vecBlockingData;
               break;
            }
         }
      }

      if (next != null && Double.isFinite(groundY) && next.distance() > groundY) {
         this.outOfRangeTicks++;
         if (this.outOfRangeTicks >= 40) {
            next = null;
         }
      } else if (next != null) {
         this.outOfRangeTicks = 0;
      }

      VecBlockingData vecBlockingData2 = null;
      double negativeInfinity = Double.NEGATIVE_INFINITY;
      double negativeInfinity4 = Double.NEGATIVE_INFINITY;
      if (list != null) {
         for (VecBlockingData vecBlockingData3 : list) {
            if (vecBlockingData3 != null && (next != null || !Double.isFinite(groundY) || !(vecBlockingData3.distance() > groundY) || vecBlockingData3.id() != this.currentTargetId)) {
               boolean distanceFlag = next != null && vecBlockingData3.id() == next.id();
               double durabilityArmor = TargetScoring.scoreTargetPriority(vecBlockingData3, absorptionPosData, getExpectedDamageFrom(vecBlockingData3, threat3), distanceFlag);
               if (distanceFlag) {
                  negativeInfinity4 = durabilityArmor;
               }

               if (durabilityArmor > negativeInfinity || durabilityArmor == negativeInfinity && vecBlockingData2 != null && vecBlockingData3.distance() < vecBlockingData2.distance()) {
                  negativeInfinity = durabilityArmor;
                  vecBlockingData2 = vecBlockingData3;
               }
            }
         }
      }

      if (next == null) {
         this.switchTarget(vecBlockingData2);
         return this.currentTarget;
      } else {
         boolean distance = isTargetSticky(next, distanceValue, this.latestObservedTick);
         if (vecBlockingData2 != null && vecBlockingData2.id() != next.id() && !(negativeInfinity <= negativeInfinity4 + getSwitchScoreMargin(distance))) {
            if (vecBlockingData2.id() == this.challengerId) {
               this.challengerStreak++;
            } else {
               this.challengerId = vecBlockingData2.id();
               this.challengerStreak = 1;
            }

            if (this.challengerStreak >= getSwitchStreakRequired(distance)) {
               this.switchTarget(vecBlockingData2);
               return this.currentTarget;
            } else {
               this.currentTarget = next;
               return this.currentTarget;
            }
         } else {
            this.challengerId = -1;
            this.challengerStreak = 0;
            this.currentTarget = next;
            return this.currentTarget;
         }
      }
   }

   public void reset() {
      this.currentTarget = null;
      this.currentTargetId = -1;
      this.challengerId = -1;
      this.challengerStreak = 0;
      this.outOfRangeTicks = 0;
      this.latestObservedTick = Long.MIN_VALUE;
   }

   static float getExpectedDamageFrom(VecBlockingData vecBlockingData, VecBlock.Threat threat) {
      double distance = vecBlockingData.distance();
      float worstMaceBound = 0.0F;
      if (distance <= 6.0) {
         worstMaceBound = threat.worstBaseDamage();
      } else if (distance < 10.0) {
         worstMaceBound = (float)(threat.worstBaseDamage() * (10.0 - distance) / 4.0);
      }

      float rocketsElytraBound = 0.0F;
      if (distance <= 3.5 && (vecBlockingData.main() == BowGappleType.SWORD || vecBlockingData.main() == BowGappleType.AXE || vecBlockingData.main() == BowGappleType.MACE)) {
         rocketsElytraBound = threat.meleeMax();
      }

      return Math.max(worstMaceBound, rocketsElytraBound);
   }

   public void updateLatestTick(long time) {
      if (time > this.latestObservedTick) {
         this.latestObservedTick = time;
      }
   }

   static boolean isTargetSticky(VecBlockingData vecBlockingData, long distanceValue, long distanceLimit) {
      return vecBlockingData == null ? false : distanceValue <= distanceLimit || vecBlockingData.distance() <= 6.0;
   }

   static double getSwitchScoreMargin(boolean blockingVecFlag) {
      return blockingVecFlag ? 8.0 : 2.0;
   }
}
