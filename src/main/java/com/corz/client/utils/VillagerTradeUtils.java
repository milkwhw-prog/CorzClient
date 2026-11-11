package com.corz.client.utils;

import com.corz.client.config.VillagerTradeRule;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.TrackedVillager;

public final class VillagerTradeUtils {

   public static VillagerTradeUtils.Due findNextDueVillager(
      Collection<TrackedVillager> collection, List<VillagerTradeRule> list, long expensiveValue, long expensiveValue2, Vec3d vec, Predicate<TrackedVillager> predicate
   ) {
      if (collection != null && list != null && !list.isEmpty()) {
         VillagerTradeUtils.Due timeExpensive = null;
         double hintedVillagerLimit = 0.0;

         for (TrackedVillager villagerExperience : collection) {
            if (villagerExperience != null && !villagerExperience.isOnCooldown(expensiveValue) && (predicate == null || predicate.test(villagerExperience))) {
               VillagerTradeRule enchantJsonProfession = null;
               TrackedVillager.Drain drain = TrackedVillager.Drain.NONE;

               for (VillagerTradeRule enchantJsonProfession2 : list) {
                  TrackedVillager.Drain noneExpensive = villagerExperience.getBlockReason(enchantJsonProfession2);
                  if (noneExpensive != TrackedVillager.Drain.NONE) {
                     enchantJsonProfession = enchantJsonProfession2;
                     drain = noneExpensive;
                     break;
                  }
               }

               if (enchantJsonProfession != null) {
                  long expensiveLimit = expensiveValue - villagerExperience.lastTradeScreenTick;
                  boolean expensiveFlag = drain == TrackedVillager.Drain.SOLD_OUT && villagerExperience.soldOutTick > villagerExperience.lastTradeScreenTick && expensiveLimit >= getRestockBackoffTicks(villagerExperience.failStreak);
                  long expensiveValue4 = drain == TrackedVillager.Drain.TOO_EXPENSIVE ? expensiveValue2 * 4L : expensiveValue2;
                  if (expensiveFlag || expensiveLimit >= expensiveValue4) {
                     double expensiveValue5 = vec == null ? 0.0 : villagerExperience.entityPos.squaredDistanceTo(vec);
                     if (timeExpensive == null || isBetterDueCandidate(expensiveFlag, villagerExperience.lastTradeScreenTick, expensiveValue5, timeExpensive, hintedVillagerLimit)) {
                        timeExpensive = new VillagerTradeUtils.Due(villagerExperience, enchantJsonProfession, expensiveFlag);
                        hintedVillagerLimit = expensiveValue5;
                     }
                  }
               }
            }
         }

         return timeExpensive;
      } else {
         return null;
      }
   }

   private static boolean isBetterDueCandidate(boolean hintedVillager, long hintedVillagerValue, double hintedVillagerValue2, VillagerTradeUtils.Due due, double hintedVillagerLimit) {
      if (hintedVillager != due.hinted()) {
         return hintedVillager;
      } else {
         long lastTradeScreenTick = due.villager().lastTradeScreenTick;
         return hintedVillagerValue != lastTradeScreenTick ? hintedVillagerValue < lastTradeScreenTick : hintedVillagerValue2 < hintedVillagerLimit;
      }
   }

   private VillagerTradeUtils() {
   }

   public static long getRestockBackoffTicks(int villagerTradeBound) {
      int villagerTradeValue = Math.max(0, Math.min(villagerTradeBound, 4));
      return Math.min(2400L, 100L << 3 * villagerTradeValue);
   }

   public record Due(TrackedVillager villager, VillagerTradeRule target, boolean hinted) {
   }
}
