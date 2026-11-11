package com.corz.client.utils;

public final class ResourceHardUtils {
   public static boolean canResourceHard(ResourceHardUtils.BlockKind blockKind) {
      return blockKind == ResourceHardUtils.BlockKind.HARD_RESOURCE;
   }

   public static ResourceHardUtils.Verdict getDataForIntFlag(int resourceHardValue, int resourceHardLimit, boolean resourceHardFlag) {
      return resourceHardFlag
         ? new ResourceHardUtils.Verdict(resourceHardValue + resourceHardLimit, 0, false)
         : new ResourceHardUtils.Verdict(resourceHardValue, resourceHardLimit, resourceHardLimit > 0 && resourceHardValue == 0);
   }

   private ResourceHardUtils() {
   }

   public static enum BlockKind {
      HARD_RESOURCE,
      POLICY_SUPPRESSED,
      STRATEGY_FAILED;
   }

   public record Verdict(int expectedPlacements, int resourceBlocked, boolean blockedByTempDebt) {
   }
}
