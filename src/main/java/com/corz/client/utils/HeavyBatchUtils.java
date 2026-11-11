package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HeavyBatchUtils {

   public static boolean canRunSide(HeavyBatchUtils.Kind kind) {
      return kind == HeavyBatchUtils.Kind.GROUND_SIDE_RUN
         || kind == HeavyBatchUtils.Kind.WALKING_SIDE_RUN
         || kind == HeavyBatchUtils.Kind.LOWER_LEVEL_SIDE_RUN
         || kind == HeavyBatchUtils.Kind.DIRECTIONAL_ROW_RUN
         || kind == HeavyBatchUtils.Kind.CONTAINER_ROW_RUN;
   }

   public static HeavyBatchUtils.Candidate buildContinuousRunCandidate(
      HeavyBatchUtils.Kind kind,
      long rootValue,
      int rootBound,
      int rootValue2,
      double pilotRenderLimit,
      boolean rootFlag,
      int rocketsBuyBound,
      int rootBound3
   ) {
      int maxSlippery = Math.max(1, rootBound);
      int rootValue4 = 2 + (int)Math.round((6.0 * Math.max(0, rocketsBuyBound) + 8.0 * Math.max(0, rootBound3)) / maxSlippery);
      return new HeavyBatchUtils.Candidate(
         kind,
         rootValue,
         rootValue2,
         rootBound,
         rootBound,
         1.0,
         rootValue4,
         0,
         0,
         0,
         rootFlag ? 4 : 0,
         (int)Math.round(pilotRenderLimit),
         rootFlag ? 0.1 : 0.05,
         10,
         kind.name().toLowerCase(Locale.ROOT) + "_continuous"
      );
   }

   public static HeavyBatchUtils.Candidate buildAccessHeavyCandidate(
      long heavyAccessValue, int heavyAccessValue2, int heavyAccessBound, double apiPutBound, double pilotRenderLimit, double heavyAccessBound3
   ) {
      return new HeavyBatchUtils.Candidate(
         HeavyBatchUtils.Kind.ACCESS_PLATFORM_RUN,
         heavyAccessValue,
         Math.max(0, heavyAccessBound),
         heavyAccessValue2,
         Math.max(1.0, apiPutBound),
         2.0,
         3,
         0,
         4,
         0,
         0,
         (int)Math.round(pilotRenderLimit),
         Math.max(0.0, heavyAccessBound3),
         0,
         "access_heavy"
      );
   }

   private HeavyBatchUtils() {
   }

   public static HeavyBatchUtils.Candidate buildSimpleRunCandidate(
      HeavyBatchUtils.Kind kind, long heavyBatchValue, int rootBound, int heavyBatchValue3, double pilotRenderLimit, boolean heavyBatchFlag
   ) {
      return buildContinuousRunCandidate(kind, heavyBatchValue, rootBound, heavyBatchValue3, pilotRenderLimit, heavyBatchFlag, 0, 0);
   }

   public static HeavyBatchUtils.Candidate buildStaticStandBatchCandidate(long batchStaticValue, int batchStaticValue2, int batchStaticValue3, double pilotRenderLimit) {
      return new HeavyBatchUtils.Candidate(
         HeavyBatchUtils.Kind.STATIC_STAND_BATCH,
         batchStaticValue,
         batchStaticValue3,
         batchStaticValue2,
         batchStaticValue2,
         batchStaticValue2,
         4,
         0,
         (int)Math.round(pilotRenderLimit),
         0,
         0,
         3,
         0.05,
         0,
         "static_stand_batch"
      );
   }

   public static HeavyBatchUtils.Decision chooseBestCandidate(List<HeavyBatchUtils.Candidate> list) {
      ArrayList arrayList = new ArrayList();

      for (HeavyBatchUtils.Candidate candidate : list) {
         if (candidate != null && candidate.expectedPlacements() > 0) {
            arrayList.add(candidate);
         }
      }

      if (arrayList.isEmpty()) {
         return new HeavyBatchUtils.Decision(null, arrayList, 0.0, "no_candidate");
      } else {
         HeavyBatchUtils.Candidate candidate2 = null;
         double candidatePlacementsValue = -1.0;

         for (HeavyBatchUtils.Candidate candidate3 : (Iterable<com.corz.client.utils.HeavyBatchUtils.Candidate>) (arrayList)) {
            if (!canRunSide(candidate3.kind()) || candidate3.expectedPlacements() >= 3) {
               double placementsExpected = candidate3.getPlacementsPerSecond();
               if (placementsExpected > candidatePlacementsValue) {
                  candidatePlacementsValue = placementsExpected;
                  candidate2 = candidate3;
               }
            }
         }

         if (candidate2 == null) {
            candidate2 = (HeavyBatchUtils.Candidate)arrayList.get(0);
            double placementsPerSecond = candidate2.getPlacementsPerSecond();

            for (HeavyBatchUtils.Candidate candidate4 : (Iterable<com.corz.client.utils.HeavyBatchUtils.Candidate>) (arrayList)) {
               if (candidate4.getPlacementsPerSecond() > placementsPerSecond) {
                  candidate2 = candidate4;
                  placementsPerSecond = candidate4.getPlacementsPerSecond();
               }
            }
         }

         HeavyBatchUtils.Candidate candidate5 = null;

         for (HeavyBatchUtils.Candidate candidate6 : (Iterable<com.corz.client.utils.HeavyBatchUtils.Candidate>) (arrayList)) {
            if (!canRunSide(candidate6.kind()) && (candidate5 == null || candidate6.getPlacementsPerSecond() > candidate5.getPlacementsPerSecond())) {
               candidate5 = candidate6;
            }
         }

         if (canRunSide(candidate2.kind()) && candidate5 != null && candidate2.getPlacementsPerSecond() < candidate5.getPlacementsPerSecond() * 1.15) {
            return new HeavyBatchUtils.Decision(candidate5, arrayList, 0.0, "walking_run_within_margin_of_" + candidate5.kind());
         } else {
            double candidatePlacementsBound = 0.0;

            for (HeavyBatchUtils.Candidate candidate7 : (Iterable<com.corz.client.utils.HeavyBatchUtils.Candidate>) (arrayList)) {
               if (candidate7 != candidate2) {
                  candidatePlacementsBound = Math.max(candidatePlacementsBound, candidate7.getPlacementsPerSecond());
               }
            }

            double candidatePlacementsValue2 = candidatePlacementsBound <= 0.0 ? 0.0 : Math.max(0.0, candidate2.expectedPlacements() / candidatePlacementsBound * 20.0 - candidate2.getPredictedTicks());
            return new HeavyBatchUtils.Decision(candidate2, arrayList, candidatePlacementsValue2, candidate2.reason());
         }
      }
   }

   public static HeavyBatchUtils.Candidate buildDeckEdgeRunCandidate(
      long deckRunValue,
      int deckRunValue2,
      int deckRunValue3,
      double d,
      double deckRunBound,
      double pilotRenderLimit,
      double deckRunValue6,
      int deckRunValue7
   ) {
      return new HeavyBatchUtils.Candidate(
         HeavyBatchUtils.Kind.DECK_RUN,
         deckRunValue,
         deckRunValue3,
         deckRunValue2,
         4.0,
         Math.max(0.5, deckRunBound),
         deckRunValue7,
         0,
         (int)Math.round(d),
         (int)Math.round(pilotRenderLimit),
         0,
         (int)Math.round(deckRunValue6),
         0.05,
         20,
         "deck_edge_run"
      );
   }

   public record Candidate(
      HeavyBatchUtils.Kind kind,
      long clusterKey,
      int entryTravelTicks,
      int expectedPlacements,
      double placementsPerStand,
      double placementsPerMove,
      int rotationCostTicks,
      int inventorySwitchCostTicks,
      int precisePoseCostTicks,
      int crouchCostTicks,
      int surfacePhysicsCostTicks,
      int serverConfirmationCostTicks,
      double expectedFailureRisk,
      int exitCostTicks,
      String reason
   ) {
      public double getPlacementsPerSecond() {
         double placementsTicks = this.getPredictedTicks();
         return !(placementsTicks <= 0.0) && this.expectedPlacements > 0 ? this.expectedPlacements / (placementsTicks / 20.0) : 0.0;
      }

      public double getPredictedTicks() {
         double placementsTicksFactor = this.precisePoseCostTicks
            + this.crouchCostTicks
            + this.surfacePhysicsCostTicks
            + this.serverConfirmationCostTicks
            + this.rotationCostTicks;
         double placementsTicksValue = this.placementsPerMove <= 0.0 ? this.expectedPlacements : Math.ceil(this.expectedPlacements / this.placementsPerMove);
         double placementsTicksValue2 = this.placementsPerStand <= 0.0 ? this.expectedPlacements : Math.ceil(this.expectedPlacements / this.placementsPerStand);
         double placementsTicksFactor2 = this.entryTravelTicks + this.exitCostTicks + this.expectedPlacements * placementsTicksFactor + this.inventorySwitchCostTicks + placementsTicksValue * 6.0 + placementsTicksValue2 * 30.0;
         return placementsTicksFactor2 * (1.0 + Math.max(0.0, this.expectedFailureRisk));
      }
   }

   public record Decision(HeavyBatchUtils.Candidate selected, List<HeavyBatchUtils.Candidate> candidates, double ticksSaved, String reason) {
      public String describeDecision() {
         StringBuilder stringBuilder = new StringBuilder();

         for (HeavyBatchUtils.Candidate candidate : this.candidates) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(',');
            }

            stringBuilder.append(candidate.kind())
               .append('(')
               .append(candidate.expectedPlacements())
               .append('/')
               .append(String.format(Locale.ROOT, "%.0ft/%.2fbps", candidate.getPredictedTicks(), candidate.getPlacementsPerSecond()))
               .append(')');
         }

         return "candidateStrategies="
            + stringBuilder
            + " selected="
            + (this.selected == null ? "NONE" : this.selected.kind())
            + " expectedPlacements="
            + (this.selected == null ? 0 : this.selected.expectedPlacements())
            + String.format(
               Locale.ROOT,
               " predictedTicks=%.0f expectedBps=%.2f ticksSaved=%.0f",
               this.selected == null ? 0.0 : this.selected.getPredictedTicks(),
               this.selected == null ? 0.0 : this.selected.getPlacementsPerSecond(),
               this.ticksSaved
            )
            + " reason="
            + this.reason;
      }
   }

   public static enum Kind {
      GROUND_SIDE_RUN,
      WALKING_SIDE_RUN,
      LOWER_LEVEL_SIDE_RUN,
      DIRECTIONAL_ROW_RUN,
      CONTAINER_ROW_RUN,
      DECK_RUN,
      STATIC_STAND_BATCH,
      FROM_ABOVE_RUN,
      ACCESS_PLATFORM_RUN;
   }
}
