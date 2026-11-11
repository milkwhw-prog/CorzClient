package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.LongUnaryOperator;
import com.corz.client.utils.DepsReasonData;
import com.corz.client.utils.ScopeAssetsData;

public final class ScopeCaused {
   private final Map<Long, ScopeCaused.Obligation> scopeCausalObligationMap = new LinkedHashMap<>();

   public int getScopeCausal(LongUnaryOperator longUnaryOperator, int scopeCausalValue) {
      int scopeCausal = 0;

      for (Entry entry : this.scopeCausalObligationMap.entrySet()) {
         ScopeCaused.Obligation obligation = (ScopeCaused.Obligation)entry.getValue();
         if (obligation.state() != ScopeCaused.State.RECOVERED && canScopeFingerprint(obligation, longUnaryOperator)) {
            entry.setValue(
               new ScopeCaused.Obligation(
                  obligation.target(),
                  obligation.causalConfirmedCell(),
                  obligation.lostDurablePoses(),
                  obligation.previousCapability(),
                  obligation.causalCells(),
                  DepsReasonData.packStateKey(obligation.causalCells(), longUnaryOperator),
                  obligation.scope() == null ? 0L : obligation.scope().getAssetCells(longUnaryOperator),
                  scopeCausalValue,
                  ScopeCaused.State.OPEN,
                  List.of(),
                  obligation.scope(),
                  -1
               )
            );
            scopeCausal++;
         }
      }

      return scopeCausal;
   }

   public void handleTarget(ScopeCaused.Obligation obligation) {
      this.scopeCausalObligationMap.put(obligation.target(), obligation);
   }

   public ScopeCaused.Obligation getOpen(
      long scopeCausedValue, long scopeCausedValue2, int lostProgramValue, long[] scopeCausedValues, LongUnaryOperator longUnaryOperator, ScopeAssetsData scopeAssetsData, int scopeCausedValue4
   ) {
      ScopeCaused.Obligation open = this.scopeCausalObligationMap.get(scopeCausedValue);
      if (open != null && open.isStateProgress()) {
         return open;
      } else {
         ScopeCaused.Obligation obligation = new ScopeCaused.Obligation(
            scopeCausedValue,
            scopeCausedValue2,
            lostProgramValue,
            lostProgramValue,
            scopeCausedValues,
            DepsReasonData.packStateKey(scopeCausedValues, longUnaryOperator),
            scopeAssetsData == null ? 0L : scopeAssetsData.getAssetCells(longUnaryOperator),
            scopeCausedValue4,
            ScopeCaused.State.OPEN,
            List.of(),
            scopeAssetsData,
            -1
         );
         this.scopeCausalObligationMap.put(scopeCausedValue, obligation);
         return obligation;
      }
   }

   public static String formatText(long scopeCausedValue) {
      return ProofSupportUtils.getValueInt(scopeCausedValue) + ", " + ProofSupportUtils.getValue(scopeCausedValue) + ", " + ProofSupportUtils.getValueForLong(scopeCausedValue);
   }

   public void update() {
      this.scopeCausalObligationMap.clear();
   }

   public int getValue() {
      return this.scopeCausalObligationMap.size();
   }

   public boolean canScope(long scopeValue) {
      for (ScopeCaused.Obligation obligation : this.scopeCausalObligationMap.values()) {
         if (obligation.isStateProgress() && obligation.scope() != null && obligation.scope().canWorkspaceCells(scopeValue)) {
            return false;
         }
      }

      return true;
   }

   public static boolean canScopeFingerprint(ScopeCaused.Obligation obligation, LongUnaryOperator longUnaryOperator) {
      if (obligation == null) {
         return false;
      } else {
         long scopeFingerprintValue = obligation.scope() == null ? 0L : obligation.scope().getAssetCells(longUnaryOperator);
         return DepsReasonData.packStateKey(obligation.causalCells(), longUnaryOperator) != obligation.geometryFingerprint() || scopeFingerprintValue != obligation.tempAssetFingerprint();
      }
   }

   public List<ScopeCaused.Obligation> getItems() {
      ArrayList arrayList = new ArrayList();

      for (ScopeCaused.Obligation obligation : this.scopeCausalObligationMap.values()) {
         if (obligation.isStateProgress()) {
            arrayList.add(obligation);
         }
      }

      return arrayList;
   }

   public ScopeCaused.Obligation getScopeCausedData(long scopeCausedValue) {
      return this.scopeCausalObligationMap.get(scopeCausedValue);
   }

   public static enum Attempt {
      ALTERNATE_DURABLE_STAND,
      BOUNDED_RECENTER,
      RELOCATION,
      TEMP_REUSE,
      COMPLETION_ESCAPE;
   }

   public record Obligation(
      long target,
      long causalConfirmedCell,
      int lostDurablePoses,
      int previousCapability,
      long[] causalCells,
      long geometryFingerprint,
      long tempAssetFingerprint,
      int createdTick,
      ScopeCaused.State state,
      List<ScopeCaused.Attempt> tried,
      ScopeAssetsData scope,
      int firstActionTick
   ) {
      public Optional<ScopeCaused.Attempt> getEmpty() {
         for (ScopeCaused.Attempt attempt : ScopeCaused.Attempt.values()) {
            if (!this.tried.contains(attempt)) {
               return Optional.of(attempt);
            }
         }

         return Optional.empty();
      }

      public ScopeCaused.Obligation getCausalFingerprint(ScopeCaused.Attempt a) {
         ArrayList arrayList = new ArrayList<>(this.tried);
         if (!arrayList.contains(a)) {
            arrayList.add(a);
         }

         return new ScopeCaused.Obligation(
            this.target,
            this.causalConfirmedCell,
            this.lostDurablePoses,
            this.previousCapability,
            this.causalCells,
            this.geometryFingerprint,
            this.tempAssetFingerprint,
            this.createdTick,
            ScopeCaused.State.IN_PROGRESS,
            arrayList,
            this.scope,
            this.firstActionTick
         );
      }

      public int getTickAction() {
         return this.firstActionTick < 0 ? -1 : this.firstActionTick - this.createdTick;
      }

      public boolean isStateProgress() {
         return this.state == ScopeCaused.State.OPEN || this.state == ScopeCaused.State.IN_PROGRESS;
      }

      public Obligation(
         long target,
         long causalConfirmedCell,
         int lostDurablePoses,
         int previousCapability,
         long[] causalCells,
         long geometryFingerprint,
         long tempAssetFingerprint,
         int createdTick,
         ScopeCaused.State state,
         List<ScopeCaused.Attempt> tried,
         ScopeAssetsData scope,
         int firstActionTick
      ) {
         causalCells = causalCells == null ? new long[0] : (long[])causalCells.clone();
         tried = tried == null ? List.of() : List.copyOf(tried);
         this.target = target;
         this.causalConfirmedCell = causalConfirmedCell;
         this.lostDurablePoses = lostDurablePoses;
         this.previousCapability = previousCapability;
         this.causalCells = causalCells;
         this.geometryFingerprint = geometryFingerprint;
         this.tempAssetFingerprint = tempAssetFingerprint;
         this.createdTick = createdTick;
         this.state = state;
         this.tried = tried;
         this.scope = scope;
         this.firstActionTick = firstActionTick;
      }

      public ScopeCaused.Obligation getCausalFingerprint(ScopeCaused.State s) {
         return new ScopeCaused.Obligation(
            this.target,
            this.causalConfirmedCell,
            this.lostDurablePoses,
            this.previousCapability,
            this.causalCells,
            this.geometryFingerprint,
            this.tempAssetFingerprint,
            this.createdTick,
            s,
            this.tried,
            this.scope,
            this.firstActionTick
         );
      }

      public ScopeCaused.Obligation getCausalFingerprint(int tick) {
         return this.firstActionTick >= 0
            ? this
            : new ScopeCaused.Obligation(
               this.target,
               this.causalConfirmedCell,
               this.lostDurablePoses,
               this.previousCapability,
               this.causalCells,
               this.geometryFingerprint,
               this.tempAssetFingerprint,
               this.createdTick,
               this.state,
               this.tried,
               this.scope,
               tick
            );
      }

      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "target=%s causedBy=%s lost=%d->0 state=%s tried=%s createdTick=%d %s ticksToFirstAction=%d",
            ScopeCaused.formatText(this.target),
            ScopeCaused.formatText(this.causalConfirmedCell),
            this.previousCapability,
            this.state,
            this.tried,
            this.createdTick,
            this.scope == null ? "scopeCells=0 scopeAssets=0" : this.scope.getFormat(),
            this.getTickAction()
         );
      }
   }

   public static enum State {
      OPEN,
      IN_PROGRESS,
      RECOVERED,
      PROVEN_IMPOSSIBLE;
   }
}
