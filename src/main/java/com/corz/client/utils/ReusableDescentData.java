package com.corz.client.utils;

import java.util.Locale;

public record ReusableDescentData(long expectedFinalComponent, int usefulTargetsAfterConfirm, boolean reusableSurface, boolean safeDescent, long returnAnchor) {
   public static final ReusableDescentData INSTANCE = new ReusableDescentData(0L, 0, false, false, 0L);

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "expectedFinalComponent=%d usefulTargetsAfterConfirm=%d reusableSurface=%s safeDescent=%s returnAnchor=%d",
         this.expectedFinalComponent,
         this.usefulTargetsAfterConfirm,
         this.reusableSurface,
         this.safeDescent,
         this.returnAnchor
      );
   }

   public static ReusableDescentData getReusableDescentData(long base, long baseComponent, boolean ownColumn) {
      return new ReusableDescentData(base, 0, false, ownColumn, baseComponent);
   }

   public static ReusableDescentData getReusableDescentData(long base, int ownColumn, long unlocks, boolean surfaceComponent) {
      return new ReusableDescentData(base, Math.max(0, ownColumn), true, surfaceComponent, unlocks);
   }
}
