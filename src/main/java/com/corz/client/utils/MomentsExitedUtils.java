package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import com.corz.client.utils.FloorBuildPlanner;
import com.corz.client.utils.EffectiveLowestData;

public final class MomentsExitedUtils {
   public static MomentsExitedUtils.Decision getDataForProbeEffectiveLowestData(
      FloorBuildPlanner.Probe probe,
      EffectiveLowestData effectiveLowestData,
      int momentsExitedValue,
      int j,
      int remainingLimit,
      int momentsExitedValue4,
      MomentsExitedUtils.Stats stats,
      boolean momentsExitedFlag,
      boolean momentsExitedFlag2,
      boolean momentsExitedFlag3,
      boolean momentsExitedFlag4
   ) {
      return getDataDecision(probe, effectiveLowestData, momentsExitedValue, j, remainingLimit, momentsExitedValue4, stats, momentsExitedFlag, momentsExitedFlag2, momentsExitedFlag3, (Predicate<FloorBuildPlanner.Deck>) deck -> momentsExitedFlag4);
   }

   public static MomentsExitedUtils.Decision getDataForProbeEffectiveLowestDataInt(
      FloorBuildPlanner.Probe probe,
      EffectiveLowestData effectiveLowestData,
      int momentsExitedValue,
      int j,
      int remainingLimit,
      int momentsExitedValue4,
      MomentsExitedUtils.Stats stats,
      boolean momentsExitedFlag,
      boolean momentsExitedFlag2,
      boolean momentsExitedFlag3,
      Function<FloorBuildPlanner.Deck, MomentsExitedUtils.Reject> function
   ) {
      MomentsExitedUtils.Decision remainingMoments = getRemainingMoments(probe, effectiveLowestData, momentsExitedValue, j, remainingLimit, momentsExitedValue4, stats, momentsExitedFlag, momentsExitedFlag2, momentsExitedFlag3, function);
      if (stats != null) {
         stats.handleProbesActivations(remainingMoments);
      }

      return remainingMoments;
   }

   private static MomentsExitedUtils.Decision getRemainingMoments(
      FloorBuildPlanner.Probe probe,
      EffectiveLowestData effectiveLowestData,
      int remainingMomentsValue,
      int j,
      int remainingLimit,
      int remainingMomentsValue4,
      MomentsExitedUtils.Stats stats,
      boolean remainingMomentsFlag,
      boolean remainingMomentsFlag2,
      boolean remainingMomentsFlag3,
      Function<FloorBuildPlanner.Deck, MomentsExitedUtils.Reject> function
   ) {
      if (!remainingMomentsFlag) {
         return new MomentsExitedUtils.Decision(MomentsExitedUtils.Reject.SETTING_DISABLED, Integer.MIN_VALUE, null, null, "Deck Mode off");
      } else if (remainingMomentsFlag2) {
         return new MomentsExitedUtils.Decision(
            MomentsExitedUtils.Reject.ALREADY_OWNED_BY_OTHER_MISSION, Integer.MIN_VALUE, null, null, "completion mission owns scheduling"
         );
      } else {
         var list = effectiveLowestData == null ? List.of() : effectiveLowestData.getEffectiveLowest();
         if (list.isEmpty()) {
            return new MomentsExitedUtils.Decision(MomentsExitedUtils.Reject.NO_ELIGIBLE_LAYER, Integer.MIN_VALUE, null, null, "horizon unresolved");
         } else {
            FloorBuildPlanner.ProbeResult probeResult = null;
            int minValue = Integer.MIN_VALUE;
            int remainingMomentsLimit6 = -1;
            FloorBuildPlanner.ProbeResult chooseRemaining2 = null;
            int minValue7 = Integer.MIN_VALUE;

            for (int remainingValue : (Iterable<Integer>) (list)) {
               FloorBuildPlanner.ProbeResult chooseRemaining = FloorBuildPlanner.scanLayerForBestDeck(probe, remainingValue, remainingMomentsValue, j, remainingLimit, remainingMomentsValue4);
               if (chooseRemaining.deck != null && chooseRemaining.deck.hasEnoughEdgeFrontiers()) {
                  if (stats != null) {
                     stats.candidates++;
                  }

                  int remainingMomentsLimit = chooseRemaining.deck.getFrontierCount() * 4 + Math.min(chooseRemaining.deck.connectedExtraCount, 400);
                  if (remainingMomentsLimit > remainingMomentsLimit6) {
                     remainingMomentsLimit6 = remainingMomentsLimit;
                     probeResult = chooseRemaining;
                     minValue = remainingValue;
                  }
               }

               if (chooseRemaining2 == null || getRemainingFloor(chooseRemaining) > getRemainingFloor(chooseRemaining2)) {
                  chooseRemaining2 = chooseRemaining;
                  minValue7 = remainingValue;
               }
            }

            if (probeResult == null) {
               MomentsExitedUtils.Reject reject = chooseRemaining2.builtFloorCells == 0
                  ? MomentsExitedUtils.Reject.NO_BUILT_SEED
                  : (
                     chooseRemaining2.headroomFloorCells == 0
                        ? MomentsExitedUtils.Reject.NO_HEADROOM_NOW
                        : (
                           chooseRemaining2.deck == null
                              ? MomentsExitedUtils.Reject.NO_BUILT_SEED
                              : (
                                 chooseRemaining2.deck.getFrontierCount() == 0
                                    ? MomentsExitedUtils.Reject.NO_CONNECTED_FRONTIER
                                    : MomentsExitedUtils.Reject.TOO_LITTLE_REMAINING
                              )
                        )
                  );
               return new MomentsExitedUtils.Decision(reject, minValue7, null, chooseRemaining2, "layer " + minValue7 + ": " + chooseRemaining2.formatBuiltFloor());
            } else if (remainingMomentsFlag3) {
               return new MomentsExitedUtils.Decision(MomentsExitedUtils.Reject.RECENT_EXIT, minValue, null, probeResult, "exited moments ago");
            } else {
               if (!probeResult.deck.containsCell(ProofSupportUtils.getTime(remainingMomentsValue, effectiveLowestData.playerFeetY(), j))) {
                  MomentsExitedUtils.Reject apply = (MomentsExitedUtils.Reject)function.apply(probeResult.deck);
                  if (apply == null) {
                     apply = MomentsExitedUtils.Reject.NONE;
                  }

                  if (apply == MomentsExitedUtils.Reject.ENTRY_PROOF_SEARCH) {
                     return new MomentsExitedUtils.Decision(apply, minValue, probeResult.deck, probeResult, "entry candidate discovered: proof search");
                  }

                  if (apply != MomentsExitedUtils.Reject.NONE) {
                     return new MomentsExitedUtils.Decision(
                        apply,
                        minValue,
                        null,
                        probeResult,
                        apply == MomentsExitedUtils.Reject.ENTRY_FAILED_CONTEXT
                           ? "entry failed in this context"
                           : apply.name().toLowerCase(Locale.ROOT).replace('_', ' ')
                     );
                  }
               }

               return new MomentsExitedUtils.Decision(
                  MomentsExitedUtils.Reject.NONE, minValue, probeResult.deck, probeResult, "layer " + minValue + ": " + probeResult.formatBuiltFloor()
               );
            }
         }
      }
   }

   private static int getRemainingFloor(FloorBuildPlanner.ProbeResult probeResult) {
      if (probeResult.deck != null && probeResult.deck.getFrontierCount() > 0) {
         return 4;
      } else if (probeResult.deck != null) {
         return 3;
      } else if (probeResult.headroomFloorCells > 0) {
         return 2;
      } else {
         return probeResult.builtFloorCells > 0 ? 1 : 0;
      }
   }

   private MomentsExitedUtils() {
   }

   public static MomentsExitedUtils.Decision getDataDecision(
      FloorBuildPlanner.Probe probe,
      EffectiveLowestData effectiveLowestData,
      int momentsExitedValue,
      int j,
      int remainingLimit,
      int momentsExitedValue4,
      MomentsExitedUtils.Stats stats,
      boolean momentsExitedFlag,
      boolean momentsExitedFlag2,
      boolean momentsExitedFlag3,
      Predicate<FloorBuildPlanner.Deck> predicate
   ) {
      return getDataForProbeEffectiveLowestDataInt(
         probe,
         effectiveLowestData,
         momentsExitedValue,
         j,
         remainingLimit,
         momentsExitedValue4,
         stats,
         momentsExitedFlag,
         momentsExitedFlag2,
         momentsExitedFlag3,
         (Function<FloorBuildPlanner.Deck, MomentsExitedUtils.Reject>) entryFailedx -> predicate.test(entryFailedx) ? MomentsExitedUtils.Reject.ENTRY_FAILED_CONTEXT : MomentsExitedUtils.Reject.NONE
      );
   }

   public record Decision(
      MomentsExitedUtils.Reject reject, int deckY, FloorBuildPlanner.Deck deck, FloorBuildPlanner.ProbeResult probe, String detail
   ) {
      public boolean isRejectDeck() {
         return this.reject == MomentsExitedUtils.Reject.NONE && this.deck != null;
      }
   }

   public static enum Reject {
      NONE,
      SETTING_DISABLED,
      NO_ELIGIBLE_LAYER,
      NO_BUILT_SEED,
      NO_HEADROOM_NOW,
      NO_CONNECTED_FRONTIER,
      TOO_LITTLE_REMAINING,
      LOW_VALUE,
      NO_ENTRY,
      ALREADY_OWNED_BY_OTHER_MISSION,
      RECENT_EXIT,
      ENTRY_FAILED_CONTEXT,
      NO_ENTRY_CANDIDATES,
      ENTRY_PROOF_SEARCH,
      ENTRY_CAPABILITY_BLOCKED;
   }

   public static final class Stats {
      public final Map<MomentsExitedUtils.Reject, Integer> rejectByReason = new LinkedHashMap<>();
      public int candidates;
      public int activations;
      public int probes;

      public void updateProbesActivations() {
         this.probes = this.candidates = this.activations = 0;
         this.rejectByReason.clear();
      }

      public void handleProbesActivations(MomentsExitedUtils.Decision decision) {
         this.probes++;
         if (decision.isRejectDeck()) {
            this.activations++;
         } else {
            this.rejectByReason.merge(decision.reject(), 1, Integer::sum);
         }
      }

      public String formatProbesActivations() {
         return "probes=" + this.probes + " candidates=" + this.candidates + " activations=" + this.activations + " rejectByReason=" + this.rejectByReason;
      }
   }
}
