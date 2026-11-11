package com.corz.client.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import com.corz.client.utils.DepsReasonData;

public record ScopeAssetsData(long target, long[] workspaceCells, long[] assetCells) {
   public ScopeAssetsData(long target, long[] workspaceCells, long[] assetCells) {
      workspaceCells = workspaceCells == null ? new long[0] : (long[])workspaceCells.clone();
      assetCells = assetCells == null ? new long[0] : (long[])assetCells.clone();
      this.target = target;
      this.workspaceCells = workspaceCells;
      this.assetCells = assetCells;
   }

   private static long getTime(long packed) {
      return ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(packed), ProofSupportUtils.getValue(packed) - 1, ProofSupportUtils.getValueForLong(packed));
   }

   private static long getTimeForLong(long packed) {
      return ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(packed), ProofSupportUtils.getValue(packed) + 1, ProofSupportUtils.getValueForLong(packed));
   }

   public static ScopeAssetsData getScopeAssetsData(long target, Collection<Long> candidateStands, LongPredicate ownedTemp) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      linkedHashSet.add(target);
      if (candidateStands != null) {
         for (long scopeAssetsValue : candidateStands) {
            linkedHashSet.add(scopeAssetsValue);
            linkedHashSet.add(getTimeForLong(scopeAssetsValue));
         }
      }

      LinkedHashSet in = new LinkedHashSet();

      for (long packed : (Iterable<Long>) (linkedHashSet)) {
         in.add(getTime(packed));
         if (ownedTemp != null && ownedTemp.test(packed)) {
            in.add(packed);
         }
      }

      return new ScopeAssetsData(target, getTimes(linkedHashSet), getTimes(in));
   }

   private static long[] getTimes(Collection<Long> in) {
      long[] longs = new long[in.size()];
      int scopeAssetsCounter = 0;

      for (long scopeAssetsValue : in) {
         longs[scopeAssetsCounter++] = scopeAssetsValue;
      }

      return longs;
   }

   public boolean canWorkspaceCells(long cell) {
      for (long workspaceCellsValue : this.workspaceCells) {
         if (workspaceCellsValue == cell) {
            return true;
         }
      }

      return false;
   }

   public int getWorkspaceCells() {
      return this.workspaceCells.length;
   }

   public String getFormat() {
      return String.format(Locale.ROOT, "scopeCells=%d scopeAssets=%d", this.workspaceCells.length, this.assetCells.length);
   }

   public long getAssetCells(LongUnaryOperator stateOf) {
      return DepsReasonData.packStateKey(this.assetCells, stateOf);
   }
}
