package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.FloorBuildPlanner;
import com.corz.client.utils.SearchCandidate;
import com.corz.client.utils.ChangedCapability;
import com.corz.client.utils.MomentsExitedUtils;

public final class DeckPlanning {
   private long planningTimeNanos;
   private int eActionsWhileEntryPending;
   private int entryPlanningYields;
   private int searchBudgetLimit;
   private SearchCandidate.Step planningApiStep;
   private final ChangedCapability changedCapability;
   private final DeckPlanning.Runtime candidateSearchRuntime;
   private final SearchCandidate searchCandidate;
   private int entryActivationAfterProof;
   private int entryPlanningTicks;
   private int entryActivationBeforeProof;
   private int candidateSearchValue;
   private boolean candidateActive;
   private int planningApiValue = 12;
   private int candidateActivationsPrevented;
   private long deckPlanningValue;
   private int activationPlanningValue;

   public boolean canPlanDeck(int deckPlanningValue, long columnReaderValue, ChangedCapability.Context context) {
      return this.changedCapability.getValidProofFor(deckPlanningValue, columnReaderValue, context) == null;
   }

   public void updateActivationPlanning() {
      this.changedCapability.updateCapabilityChanged();
      this.searchCandidate.updateProofsSearch();
      this.candidateActive = false;
      this.activationPlanningValue = Integer.MIN_VALUE;
      this.planningApiStep = null;
      this.entryPlanningTicks = this.entryPlanningYields = this.eActionsWhileEntryPending = this.candidateActivationsPrevented = this.entryActivationAfterProof = this.entryActivationBeforeProof = 0;
      this.planningTimeNanos = 0L;
   }

   public long getDeckPlanningValue() {
      return this.planningTimeNanos;
   }

   public void recordCandidateCapability(int candidateSearchValue, ChangedCapability.Capability capability) {
      if (this.searchCandidate.canCandidateNone(candidateSearchValue) && !this.searchCandidate.isCommittedDiscoveredType()) {
         this.searchCandidate.handleCanceledSearch("no bootstrap capability: " + capability.formatAllowedCap());
      }

      this.changedCapability.handle(candidateSearchValue, capability);
   }

   public int getEntryActivationAfterProof() {
      return this.entryActivationAfterProof;
   }

   public SearchCandidate getSearchCandidate() {
      return this.searchCandidate;
   }

   public void recordPlanningTimes(int value, int value2, long time, int value3) {
      this.planningApiValue = value;
      this.searchBudgetLimit = value2;
      this.deckPlanningValue = time;
      this.candidateSearchValue = value3;
   }

   public DeckPlanning(ChangedCapability changedCapability, SearchCandidate searchCandidate, DeckPlanning.Runtime api) {
      this.searchBudgetLimit = 2;
      this.candidateSearchValue = 64;
      this.deckPlanningValue = 2000000L;
      this.activationPlanningValue = Integer.MIN_VALUE;
      this.changedCapability = changedCapability;
      this.searchCandidate = searchCandidate;
      this.candidateSearchRuntime = api;
   }

   public void handleRefusedWalk(int refusedWalkValue, long formatGroundAir, long proofsCommittedValue, ChangedCapability.Column column, int refusedWalkValue4) {
      this.changedCapability.handle(refusedWalkValue, formatGroundAir, ChangedCapability.FailureClass.WALK_ON_REFUSED, formatGroundAir, proofsCommittedValue, column, refusedWalkValue4);
   }

   public ChangedCapability getChangedCapability() {
      return this.changedCapability;
   }

   public int getEntryActivationBeforeProof() {
      return this.entryActivationBeforeProof;
   }

   public SearchCandidate.Step getPlanningApiStep() {
      return this.planningApiStep;
   }

   public DeckPlanning.Outcome resolveSearchOutcome(MomentsExitedUtils.Decision decision, long candidateValue, int candidateValue2) {
      if (this.candidateActive) {
         this.candidateActive = false;
         return DeckPlanning.Outcome.RECLAIM_STARTED;
      } else if (decision.isRejectDeck()) {
         return DeckPlanning.Outcome.ACTIVATE;
      } else if (decision.reject() == MomentsExitedUtils.Reject.ENTRY_PROOF_SEARCH && decision.deck() != null) {
         this.getPlanningApi(candidateValue, candidateValue2);
         return this.searchCandidate.isCommittedDiscovered() ? DeckPlanning.Outcome.ACTIVATE : DeckPlanning.Outcome.SEARCHING;
      } else {
         if ((this.searchCandidate.isDiscoveredCommitted() || this.searchCandidate.isCommittedDiscovered())
            && (decision.deck() == null || decision.deckY() != this.searchCandidate.getCandidateNoneValue())) {
            this.searchCandidate.handleCanceledSearch("candidate withdrawn: " + decision.reject());
         }

         return DeckPlanning.Outcome.NONE;
      }
   }

   public DeckPlanning.Outcome evaluateCandidateSearch(long candidateSearchValue, int candidateSearchValue2, boolean candidateSearchFlag, boolean candidateSearchFlag2) {
      if (!this.searchCandidate.isDiscoveredCommitted() && !this.searchCandidate.isCommittedDiscovered()) {
         return DeckPlanning.Outcome.NONE;
      } else if (candidateSearchFlag) {
         this.searchCandidate.handleCanceledSearch("another mission owns scheduling");
         return DeckPlanning.Outcome.NONE;
      } else if (!candidateSearchFlag2) {
         this.searchCandidate.handleCanceledSearch("Deck Mode off");
         return DeckPlanning.Outcome.NONE;
      } else if (!this.candidateSearchRuntime.canEffectiveLowest(this.searchCandidate.getCandidateNoneValue())) {
         this.searchCandidate.handleCanceledSearch("layer left the work horizon");
         return DeckPlanning.Outcome.NONE;
      } else {
         ChangedCapability.Capability autoBuilder = this.candidateSearchRuntime.getElevatedCapability();
         if (!autoBuilder.isAllowedCap()) {
            this.recordCandidateCapability(this.searchCandidate.getCandidateNoneValue(), autoBuilder);
            return DeckPlanning.Outcome.NONE;
         } else {
            this.getPlanningApi(candidateSearchValue, candidateSearchValue2);
            return this.searchCandidate.isCommittedDiscovered() ? DeckPlanning.Outcome.ACTIVATE : DeckPlanning.Outcome.SEARCHING;
         }
      }
   }

   public void setCandidateSearchValue(int candidateSearchValue) {
      this.changedCapability.markStrategyChanged(candidateSearchValue);
      this.searchCandidate.updateFinishedCandidate();
   }

   public SearchCandidate.Valid validateCandidateSearch(int candidateSearchValue, boolean candidateSearchFlag, boolean candidateSearchFlag2) {
      SearchCandidate.Valid candidateSearch = !candidateSearchFlag && !candidateSearchFlag2 && this.searchCandidate.isCommittedDiscovered() && this.searchCandidate.getCandidateNoneValue() == candidateSearchValue
         ? this.searchCandidate.getNext()
         : null;
      if (candidateSearch != null) {
         this.entryActivationAfterProof++;
         this.searchCandidate.updateActivatedNone();
      } else {
         if (!candidateSearchFlag && !candidateSearchFlag2) {
            this.entryActivationBeforeProof++;
         }

         if (this.searchCandidate.canCandidateNone(candidateSearchValue)) {
            this.searchCandidate.handleCanceledSearch(candidateSearchFlag ? "on deck" : "walk-on entry");
         }
      }

      return candidateSearch;
   }

   public String formatCandidateSearch() {
      return this.changedCapability.getFormat() + " " + this.searchCandidate.formatSearchSearches() + " " + this.formatActivationPlanning();
   }

   public int getEActionsWhileEntryPending() {
      return this.eActionsWhileEntryPending;
   }

   public String formatActivationPlanning() {
      return "entryPlanningTicks="
         + this.entryPlanningTicks
         + " entryPlanningMs="
         + this.planningTimeNanos / 1000000L
         + " entryPlanningYields="
         + this.entryPlanningYields
         + " productiveActionsWhileEntryPending="
         + this.eActionsWhileEntryPending
         + " zeroCandidateActivationsPrevented="
         + this.candidateActivationsPrevented
         + " entryActivationAfterProof="
         + this.entryActivationAfterProof
         + " entryActivationBeforeProof="
         + this.entryActivationBeforeProof;
   }

   public SearchCandidate.Step getPlanningApi(long planningApiValue, int value) {
      if (this.activationPlanningValue == value) {
         return this.planningApiStep;
      } else {
         this.activationPlanningValue = value;
         if (!this.searchCandidate.isDiscoveredCommitted()) {
            return this.planningApiStep = new SearchCandidate.Step(this.searchCandidate.getCommittedDiscoveredType(), 0, 0, 0, false, null);
         } else {
            long nanoTime = this.candidateSearchRuntime.getNanoTime();
            this.entryPlanningTicks++;
            SearchCandidate.Step data = this.searchCandidate
               .getProofsCommitted(
                  new SearchCandidate.Budget(this.planningApiValue, this.searchBudgetLimit, nanoTime + this.deckPlanningValue),
                  this.changedCapability,
                  this.candidateSearchRuntime.getCapabilityContext(),
                  this.candidateSearchRuntime::getCandidateVerdict,
                  planningApiValue,
                  value,
                  this.candidateSearchRuntime::getNanoTime
               );
            this.planningTimeNanos = this.planningTimeNanos + (this.candidateSearchRuntime.getNanoTime() - nanoTime);
            if (data.yielded()) {
               this.entryPlanningYields++;
            }

            return this.planningApiStep = data;
         }
      }
   }

   public void noteCandidateSearchText(String candidateSearchText) {
      if (this.searchCandidate.isDiscoveredCommitted() || this.searchCandidate.isCommittedDiscovered()) {
         this.searchCandidate.handleCanceledSearch("mission exit: " + candidateSearchText);
      }
   }

   public MomentsExitedUtils.Reject checkDeckCandidate(FloorBuildPlanner.Deck deck, long candidateSearchValue, int candidateSearchValue2, long candidateSearchValue3) {
      if (!deck.containsCell(candidateSearchValue) && this.candidateSearchRuntime.getDeckPosKey(deck) == 0L) {
         List apiAuto = this.candidateSearchRuntime.getDeckCandidateRoutes(deck);
         if (apiAuto.isEmpty()) {
            if (this.searchCandidate.canCandidateNone(deck.layerY)) {
               this.searchCandidate.handleCanceledSearch("no entry candidates");
            }

            this.changedCapability.removeStrategy(deck.layerY);
            this.candidateActivationsPrevented++;
            return MomentsExitedUtils.Reject.NO_ENTRY_CANDIDATES;
         } else {
            this.changedCapability.handleTopologySurface(deck.layerY);
            ChangedCapability.Capability autoBuilder = this.candidateSearchRuntime.getElevatedCapability();
            if (!autoBuilder.isAllowedCap()) {
               if (autoBuilder.tempCapBlocked() && autoBuilder.strategyAllowed() && autoBuilder.tempMaterial() && this.candidateSearchRuntime.isReclaimingOwnership()) {
                  this.candidateActive = true;
               }

               if (this.searchCandidate.canCandidateNone(deck.layerY) && !this.searchCandidate.isCommittedDiscoveredType()) {
                  this.searchCandidate.handleCanceledSearch("no bootstrap capability: " + autoBuilder.formatAllowedCap());
               }

               this.changedCapability.handle(deck.layerY, autoBuilder);
               return MomentsExitedUtils.Reject.ENTRY_CAPABILITY_BLOCKED;
            } else {
               this.changedCapability.handleStrategyChanged(deck.layerY, autoBuilder);
               if (!this.searchCandidate.canCandidateNone(deck.layerY)) {
                  this.searchCandidate.handleDiscoveredCandidate(deck, apiAuto, candidateSearchValue, candidateSearchValue2, candidateSearchValue3);
                  return MomentsExitedUtils.Reject.ENTRY_PROOF_SEARCH;
               } else {
                  this.searchCandidate.handleCandidateCandidates(deck);
                  boolean discoveredCommitted = this.searchCandidate.isCommittedDiscoveredType();
                  if (this.searchCandidate.getResumedCandidates(apiAuto, candidateSearchValue) > 0 && discoveredCommitted) {
                     this.changedCapability.handleCapabilityChanged(ChangedCapability.Reopen.SURFACE_TOPOLOGY_CHANGED);
                  }

                  if (this.searchCandidate.isCommittedDiscoveredType()) {
                     List list = this.changedCapability
                        .getItems(deck.layerY, this.searchCandidate.getFinishedNone(), this.candidateSearchRuntime.getCapabilityContext(), this.candidateSearchValue);
                     if (!list.isEmpty()) {
                        this.searchCandidate.handleCommittedDiscovered(list, this.changedCapability.getChangedCapabilityType());
                     }
                  }

                  if (this.searchCandidate.isDiscoveredCommitted()) {
                     return MomentsExitedUtils.Reject.ENTRY_PROOF_SEARCH;
                  } else if (this.searchCandidate.isCommittedDiscovered()) {
                     return MomentsExitedUtils.Reject.ENTRY_PROOF_SEARCH;
                  } else {
                     this.changedCapability.resetState();
                     return MomentsExitedUtils.Reject.ENTRY_FAILED_CONTEXT;
                  }
               }
            }
         }
      } else {
         return MomentsExitedUtils.Reject.NONE;
      }
   }

   public int getEntryPlanningTicks() {
      return this.entryPlanningTicks;
   }

   public int getCandidateActivationsPrevented() {
      return this.candidateActivationsPrevented;
   }

   public void handleWhyTop(int whyTopValue, SearchCandidate.Valid valid, SearchCandidate.Verdict verdict, long proofsCommittedValue, int whyTopValue3, String whyTopText) {
      if (valid != null && verdict != null && verdict.why() != null) {
         this.changedCapability.handleInstance(whyTopValue, valid.top(), verdict.why(), verdict.base(), proofsCommittedValue, verdict.column(), whyTopValue3, verdict.validUntil());
      }

      this.searchCandidate.handleCommittedSearch(valid == null ? Long.MIN_VALUE : valid.top(), whyTopText);
   }

   public void updateActionsPending() {
      if (this.searchCandidate.isDiscoveredCommitted()) {
         this.eActionsWhileEntryPending++;
      }
   }

   public int getEntryPlanningYields() {
      return this.entryPlanningYields;
   }

   public static enum Outcome {
      NONE,
      RECLAIM_STARTED,
      SEARCHING,
      ACTIVATE;
   }

   public interface Runtime {
      List<long[]> getDeckCandidateRoutes(FloorBuildPlanner.Deck deck);

      boolean isReclaimingOwnership();

      long getNanoTime();

      ChangedCapability.Context getCapabilityContext();

      SearchCandidate.Verdict getCandidateVerdict(SearchCandidate.Candidate candidate, boolean autoBuilderFlag);

      ChangedCapability.Capability getElevatedCapability();

      long getDeckPosKey(FloorBuildPlanner.Deck deck);

      boolean canEffectiveLowest(int effectiveLowestValue);
   }
}
