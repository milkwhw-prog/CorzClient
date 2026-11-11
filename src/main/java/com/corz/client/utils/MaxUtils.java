package com.corz.client.utils;

public final class MaxUtils {
   public final int cellLimit;
   public final int maxBound;
   public final int maxBoundClaimed;

   public static MaxUtils getMaxUtils(int n) {
      return new MaxUtils(n, 1536, 128);
   }

   public MaxUtils(int rawMaxBound, int rawClaimedBound, int rawCellLimit) {
      this.maxBound = Math.max(4, rawMaxBound);
      this.maxBoundClaimed = Math.max(8, rawClaimedBound);
      this.cellLimit = Math.max(8, Math.min(250, rawCellLimit));
   }
}
