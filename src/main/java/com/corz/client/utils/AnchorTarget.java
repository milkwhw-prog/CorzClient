package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import com.corz.client.utils.AnchorSafety;
import com.corz.client.utils.StandFinder;

public record AnchorTarget(int x, int y, int z, int charges, int depth, boolean exposed, int digs, double distance, AnchorSafety.Verdict verdict) {
   public static final Comparator<AnchorTarget> comparingInt = Comparator.comparingDouble(AnchorTarget::getSortScore)
      .thenComparingDouble(AnchorTarget::distance)
      .thenComparingInt(anchorTarget -> -anchorTarget.y())
      .thenComparingInt(AnchorTarget::x)
      .thenComparingInt(AnchorTarget::z);

   public boolean isVerdictSafe() {
      return this.verdict != null && this.verdict.safe();
   }

   public static int computeDepth(int anchorY, int feetY) {
      return Math.max(0, feetY - anchorY);
   }

   public static int computeDigs(boolean exposed, int depth, int standDigs) {
      return exposed ? 0 : depth + standDigs;
   }

   public double getSortScore() {
      return this.distance + 3.0 * this.depth + this.digs;
   }

   public static AnchorTarget create(
      int sy, int allowDig, int sx, int stands, double y, double charges, double feetY, int z, StandFinder.Result sz, AnchorSafety.Verdict verdict, boolean x
   ) {
      int digsExposedValue = computeDepth(allowDig, z);
      boolean digsExposedFlag = sz != null && !sz.exposed().isEmpty();
      StandFinder.Stand stand = sz == null ? null : sz.getBestCandidate(x);
      int digs2 = computeDigs(digsExposedFlag, stand == null ? 0 : stand.digs(), digsExposedValue);
      double digsExposedFactor = sy + 0.5 - y;
      double digsExposedFactor2 = allowDig + 0.5 - charges;
      double digsExposedFactor3 = sx + 0.5 - feetY;
      return new AnchorTarget(sy, allowDig, sx, stands, digsExposedValue, digsExposedFlag, digs2, Math.sqrt(digsExposedFactor * digsExposedFactor + digsExposedFactor2 * digsExposedFactor2 + digsExposedFactor3 * digsExposedFactor3), verdict);
   }

   public static List<AnchorTarget> filterSafeSorted(Collection<AnchorTarget> in) {
      ArrayList arrayList = new ArrayList();
      if (in == null) {
         return arrayList;
      } else {
         for (AnchorTarget anchorTarget : in) {
            if (anchorTarget != null && anchorTarget.isVerdictSafe()) {
               arrayList.add(anchorTarget);
            }
         }

         arrayList.sort(comparingInt);
         return arrayList;
      }
   }
}
