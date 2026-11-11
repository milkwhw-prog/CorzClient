package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import com.corz.client.utils.CrouchedBodyUtils;
import com.corz.client.utils.DroppedPlaced;

public final class EdgeAdjustStats {
   public int edgeAdjustStartedCount;
   public int provenMicroAdjustCount;
   public int runMembersDroppedCount;
   public int currentRunRemaining;
   public int parityFailureCount;
   public int edgeAdjustTicksTotal;
   public int invalidReachCount;
   public int anchorRescueCount;
   public int edgeAdjustCompletedCount;
   public int requiresSurfaceMoveCount;
   public int surfaceAnchorCount;
   public int runsStartedCount;
   public int preflightBanHitCount;
   public long surfaceAnchorPrimaryKey;
   public int edgeAdjustRefinedCount;
   public int edgeAdjustRejectedPreflightCount;
   public int invalidAdjustCount;
   public int edgeAdjustMaxTicks;
   public int poseCandidateCount;
   public final Map<String, Integer> runAbortsByReason = new LinkedHashMap<>();
   public int invalidNoFaceCount;
   public int edgeAdjustRejectedRuntimeCount;
   public int preflightBansTotal;
   public int invalidSafetyCount;
   public int currentRunLength;
   public int runMembersCount;
   public int invalidGateCount;
   public int eyeSwitchCount;
   public int provenExecutableNowCount;

   public void resetAll() {
      this.poseCandidateCount = this.provenExecutableNowCount = this.provenMicroAdjustCount = this.requiresSurfaceMoveCount = 0;
      this.invalidNoFaceCount = this.invalidReachCount = this.invalidAdjustCount = this.invalidSafetyCount = this.invalidGateCount = 0;
      this.edgeAdjustStartedCount = this.edgeAdjustCompletedCount = this.edgeAdjustRejectedPreflightCount = this.edgeAdjustRejectedRuntimeCount = this.edgeAdjustRefinedCount = this.edgeAdjustTicksTotal = this.edgeAdjustMaxTicks = 0;
      this.parityFailureCount = this.eyeSwitchCount = 0;
      this.surfaceAnchorPrimaryKey = 0L;
      this.surfaceAnchorCount = this.anchorRescueCount = 0;
      this.currentRunLength = this.currentRunRemaining = this.runsStartedCount = this.runMembersCount = this.runMembersDroppedCount = 0;
      this.runAbortsByReason.clear();
      this.preflightBansTotal = this.preflightBanHitCount = 0;
   }

   public void recordRunAbort(DroppedPlaced.Abort abort) {
      this.runAbortsByReason.merge(abort.name(), 1, Integer::sum);
   }

   public void recordCurrentRun(DroppedPlaced droppedPlaced) {
      this.currentRunLength = droppedPlaced == null ? 0 : droppedPlaced.getLength();
      this.currentRunRemaining = droppedPlaced == null ? 0 : droppedPlaced.getRemaining();
   }

   public void recordEdgeAdjustRefined(int adjustEdgeBound) {
      this.edgeAdjustTicksTotal += adjustEdgeBound;
      this.edgeAdjustMaxTicks = Math.max(this.edgeAdjustMaxTicks, adjustEdgeBound);
      this.edgeAdjustRefinedCount++;
   }

   public void recordEdgeAdjustResult(int adjustEdgeBound, boolean adjustEdgeFlag) {
      this.edgeAdjustTicksTotal += adjustEdgeBound;
      this.edgeAdjustMaxTicks = Math.max(this.edgeAdjustMaxTicks, adjustEdgeBound);
      if (adjustEdgeFlag) {
         this.edgeAdjustCompletedCount++;
      } else {
         this.edgeAdjustRejectedRuntimeCount++;
      }
   }

   public void recordEdgeAdjustStarted() {
      this.edgeAdjustStartedCount++;
   }

   public String formatSummary() {
      return "poseCandidates="
         + this.poseCandidateCount
         + " provenExecutableNow="
         + this.provenExecutableNowCount
         + " provenMicroAdjust="
         + this.provenMicroAdjustCount
         + " requiresSurfaceMove="
         + this.requiresSurfaceMoveCount
         + " invalidNoFace="
         + this.invalidNoFaceCount
         + " invalidReach="
         + this.invalidReachCount
         + " invalidCollision="
         + this.invalidAdjustCount
         + " invalidSafety="
         + this.invalidSafetyCount
         + " invalidGate="
         + this.invalidGateCount
         + " edgeAdjustStarted="
         + this.edgeAdjustStartedCount
         + " edgeAdjustCompleted="
         + this.edgeAdjustCompletedCount
         + " edgeAdjustRejectedPreflight="
         + this.edgeAdjustRejectedPreflightCount
         + " edgeAdjustRejectedRuntime="
         + this.edgeAdjustRejectedRuntimeCount
         + " edgeAdjustRefined="
         + this.edgeAdjustRefinedCount
         + " edgeAdjustTicksTotal="
         + this.edgeAdjustTicksTotal
         + " edgeAdjustMaxTicks="
         + this.edgeAdjustMaxTicks
         + " plannerExecutorParityFailures="
         + this.parityFailureCount
         + " certificateEyeSwitches="
         + this.eyeSwitchCount
         + " surfaceAnchorPrimary="
         + (
            this.surfaceAnchorPrimaryKey == 0L
               ? "none"
               : ProofSupportUtils.getValueInt(this.surfaceAnchorPrimaryKey)
                  + ","
                  + ProofSupportUtils.getValue(this.surfaceAnchorPrimaryKey)
                  + ","
                  + ProofSupportUtils.getValueForLong(this.surfaceAnchorPrimaryKey)
         )
         + " surfaceAnchorCount="
         + this.surfaceAnchorCount
         + " anchorRescues="
         + this.anchorRescueCount
         + " currentRunLength="
         + this.currentRunLength
         + " currentRunRemaining="
         + this.currentRunRemaining
         + " runsStarted="
         + this.runsStartedCount
         + " runPlacements="
         + this.runMembersCount
         + " runMembersDropped="
         + this.runMembersDroppedCount
         + " runAbortsByReason="
         + this.runAbortsByReason
         + " preflightBans="
         + this.preflightBansTotal
         + " preflightBanHits="
         + this.preflightBanHitCount;
   }

   public void recordCertificate(CrouchedBodyUtils.Certificate certificate) {
      this.poseCandidateCount++;
      switch (certificate.status()) {
         case EXECUTABLE_CURRENT_POSE:
            this.provenExecutableNowCount++;
            break;
         case EXECUTABLE_WITH_PROVEN_MICRO_ADJUST:
            this.provenMicroAdjustCount++;
            break;
         case REQUIRES_SURFACE_MOVE:
            this.requiresSurfaceMoveCount++;
            break;
         case INVALID_POSE:
            switch (certificate.reason()) {
               case NO_FACE:
                  this.invalidNoFaceCount++;
                  break;
               case OUT_OF_REACH:
                  this.invalidReachCount++;
                  break;
               case COLLISION:
               case BODY_OVERLAPS_TARGET:
                  this.invalidAdjustCount++;
                  break;
               case SAFETY:
               case NOT_STANDABLE:
                  this.invalidSafetyCount++;
                  break;
               default:
                  this.invalidGateCount++;
            }
      }
   }
}
