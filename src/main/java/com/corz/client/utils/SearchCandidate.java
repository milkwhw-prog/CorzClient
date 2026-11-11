package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.LongSupplier;
import com.corz.client.utils.FloorBuildPlanner;
import com.corz.client.utils.ChangedCapability;

public final class SearchCandidate {
   private final ArrayList<SearchCandidate.Valid> noneFinishedItems;
   private final ArrayList<SearchCandidate.Candidate> candidateProofItems;
   private int entryProofs;
   private SearchCandidate.State committedDiscoveredType = SearchCandidate.State.IDLE;
   private int reopenMemoryValue;
   private int proofsProofCount;
   private long longValue;
   private int elapsedProofsValue;
   private int entrySearchCanceled;
   private int entrySearchCommitted;
   private int yieldedProductiveValue;
   private int proofsTotalValue;
   private int discoveredValue;
   private int entrySearchResumes;
   private int discoveredCandidateCount;
   private FloorBuildPlanner.Deck remaining;
   private int entrySearchYields;
   private final HashMap<Long, Long> finishedCandidateMap;
   private int entrySearchCompleted;
   private String none;
   private final HashSet<Long> finishedNoneSet;
   private ChangedCapability.Reopen changedCapabilityType;
   private int proofsSearchCount;
   private int candidateNoneValue = Integer.MIN_VALUE;
   private SearchCandidate.Valid next;
   private int entrySearches;
   private boolean memoryYieldedFlag;
   private long discoveredTick;
   private int proofsValidValue;

   public String getNone() {
      return this.none;
   }

   public void updateProofsSearch() {
      this.updateFinishedCandidate();
      this.candidateNoneValue = Integer.MIN_VALUE;
      this.remaining = null;
      this.changedCapabilityType = null;
      this.none = "none";
      this.yieldedProductiveValue = this.proofsValidValue = this.proofsSearchCount = this.elapsedProofsValue = this.discoveredValue = this.proofsTotalValue = this.reopenMemoryValue = 0;
      this.entrySearches = this.entrySearchCompleted = this.entrySearchCanceled = this.entrySearchYields = this.entrySearchCommitted = this.entrySearchResumes = this.entryProofs = 0;
      this.memoryYieldedFlag = false;
   }

   public int getProofsValidValue() {
      return this.proofsValidValue;
   }

   public SearchCandidate.Valid getNext() {
      return this.next;
   }

   public int getProofsProofCount() {
      return this.proofsProofCount;
   }

   public int getYieldedProductiveValue() {
      return this.yieldedProductiveValue;
   }

   public int getEntrySearchCompleted() {
      return this.entrySearchCompleted;
   }

   public SearchCandidate.State getCommittedDiscoveredType() {
      return this.committedDiscoveredType;
   }

   public int getEntrySearches() {
      return this.entrySearches;
   }

   public int getEntrySearchCommitted() {
      return this.entrySearchCommitted;
   }

   public boolean isDiscoveredCommitted() {
      return this.committedDiscoveredType == SearchCandidate.State.CANDIDATE_DISCOVERED || this.committedDiscoveredType == SearchCandidate.State.PROOF_SEARCH;
   }

   public int getEntrySearchYields() {
      return this.entrySearchYields;
   }

   public void handleCanceledSearch(String canceledSearchText) {
      if (this.isDiscoveredCommitted() || this.isCommittedDiscovered()) {
         this.entrySearchCanceled++;
      }

      this.committedDiscoveredType = SearchCandidate.State.CANCELED;
      this.next = null;
      this.noneFinishedItems.clear();
      this.none = "canceled: " + canceledSearchText;
   }

   public int getElapsedProofsValue() {
      return this.elapsedProofsValue;
   }

   public int getProofsSearchCount() {
      return this.proofsSearchCount;
   }

   public boolean isCommittedDiscoveredType() {
      return this.committedDiscoveredType == SearchCandidate.State.EXHAUSTED;
   }

   public void updateActivatedNone() {
      this.none = "activated";
   }

   public List<Long> getFinishedNone() {
      return new ArrayList<>(this.finishedNoneSet);
   }

   public int getEntrySearchCanceled() {
      return this.entrySearchCanceled;
   }

   public SearchCandidate.Step getProofsCommitted(
      SearchCandidate.Budget budget, ChangedCapability changedCapability, ChangedCapability.Context context, SearchCandidate.Prover prover, long proofsCommittedValue, int proofsCommittedValue2, LongSupplier longSupplier
   ) {
      if (!this.isDiscoveredCommitted()) {
         return new SearchCandidate.Step(this.committedDiscoveredType, 0, 0, 0, false, null);
      } else {
         String none = null;
         if (this.committedDiscoveredType == SearchCandidate.State.CANDIDATE_DISCOVERED) {
            this.committedDiscoveredType = SearchCandidate.State.PROOF_SEARCH;
            none = "proof search started";
         }

         int proofsTotalValue = 0;
         int reopenMemoryValue = 0;
         int proofsCommittedCounter = 0;
         boolean memoryYieldedFlag = false;

         while (this.proofsProofCount < this.candidateProofItems.size()) {
            if (proofsTotalValue >= budget.proofs() || longSupplier.getAsLong() > budget.deadlineNanos()) {
               memoryYieldedFlag = true;
               break;
            }

            SearchCandidate.Candidate candidateproofitem = this.candidateProofItems.get(this.proofsProofCount);
            if (changedCapability.getValidProofFor(this.candidateNoneValue, candidateproofitem.top(), context) != null) {
               reopenMemoryValue++;
               this.proofsProofCount++;
            } else {
               SearchCandidate.Verdict prove = prover.prove(candidateproofitem, proofsCommittedCounter < budget.astar());
               if (prove == null) {
                  prove = SearchCandidate.Verdict.INSTANCE;
               }

               if (prove.deferred()) {
                  memoryYieldedFlag = true;
                  break;
               }

               proofsTotalValue++;
               if (prove.usedAstar()) {
                  proofsCommittedCounter++;
               }

               if (prove.isDeferredWhy()) {
                  this.elapsedProofsValue++;
                  SearchCandidate.Valid next = new SearchCandidate.Valid(candidateproofitem.deckFeet(), candidateproofitem.top(), prove.base(), prove.climb(), prove.cost(), proofsCommittedValue);
                  this.noneFinishedItems.add(next);
                  if (this.next == null || prove.cost() < this.next.cost()) {
                     this.next = next;
                  }
               } else {
                  this.proofsSearchCount++;
                  changedCapability.handleInstance(this.candidateNoneValue, candidateproofitem.top(), prove.why(), prove.base(), proofsCommittedValue, prove.column(), proofsCommittedValue2, prove.validUntil());
               }

               this.proofsProofCount++;
            }
         }

         this.proofsValidValue += proofsTotalValue;
         this.entryProofs += proofsTotalValue;
         this.discoveredValue += reopenMemoryValue;
         this.yieldedProductiveValue++;
         this.proofsTotalValue = proofsTotalValue;
         this.reopenMemoryValue = reopenMemoryValue;
         this.memoryYieldedFlag = memoryYieldedFlag;
         if (this.next != null) {
            this.committedDiscoveredType = SearchCandidate.State.COMMITTED;
            this.entrySearchCommitted++;
            none = "committed";
         } else if (this.proofsProofCount >= this.candidateProofItems.size()) {
            this.committedDiscoveredType = SearchCandidate.State.EXHAUSTED;
            this.entrySearchCompleted++;
            none = "exhausted";
         } else if (memoryYieldedFlag) {
            this.entrySearchYields++;
         }

         if (none != null) {
            this.none = none;
         }

         return new SearchCandidate.Step(this.committedDiscoveredType, proofsTotalValue, reopenMemoryValue, proofsCommittedCounter, memoryYieldedFlag, none);
      }
   }

   private int getFinishedCandidate(List<long[]> list, long finishedCandidateValue) {
      int valueInt = ProofSupportUtils.getValueInt(finishedCandidateValue);
      int valueForLong = ProofSupportUtils.getValueForLong(finishedCandidateValue);
      ArrayList arrayList = new ArrayList();

      for (long[] finishedCandidateValues : list) {
         if (this.finishedNoneSet.add(finishedCandidateValues[1])) {
            arrayList.add(new SearchCandidate.Candidate(finishedCandidateValues[0], finishedCandidateValues[1]));
            this.finishedCandidateMap.put(finishedCandidateValues[1], finishedCandidateValues[0]);
         }
      }

      arrayList.sort(
         Comparator.<SearchCandidate.Candidate>comparingInt(
               candidate -> Math.max(Math.abs(ProofSupportUtils.getValueInt(candidate.top()) - valueInt), Math.abs(ProofSupportUtils.getValueForLong(candidate.top()) - valueForLong))
            )
            .thenComparingInt(candidate2 -> ProofSupportUtils.getValueInt(candidate2.top()))
            .thenComparingInt(candidate3 -> ProofSupportUtils.getValueForLong(candidate3.top()))
      );
      this.candidateProofItems.addAll(arrayList);
      return arrayList.size();
   }

   public void handleCommittedSearch(long committedSearchValue, String committedSearchText) {
      if (this.committedDiscoveredType != SearchCandidate.State.CANCELED && this.committedDiscoveredType != SearchCandidate.State.IDLE) {
         this.noneFinishedItems.removeIf(failedTopx -> failedTopx.top() == committedSearchValue);
         this.next = null;

         for (SearchCandidate.Valid next : this.noneFinishedItems) {
            if (this.next == null || next.cost() < this.next.cost()) {
               this.next = next;
            }
         }

         if (this.next != null) {
            this.committedDiscoveredType = SearchCandidate.State.COMMITTED;
            this.entrySearchCommitted++;
            this.entrySearchResumes++;
            this.none = "resumed: " + committedSearchText + " \u2192 next proven entry";
         } else {
            this.committedDiscoveredType = this.proofsProofCount < this.candidateProofItems.size()
               ? SearchCandidate.State.PROOF_SEARCH
               : SearchCandidate.State.EXHAUSTED;
            if (this.committedDiscoveredType == SearchCandidate.State.EXHAUSTED) {
               this.entrySearchCompleted++;
            }

            this.entrySearchResumes++;
            this.none = "resumed: " + committedSearchText;
         }
      }
   }

   public FloorBuildPlanner.Deck getRemaining() {
      return this.remaining;
   }

   public void updateFinishedCandidate() {
      this.committedDiscoveredType = SearchCandidate.State.IDLE;
      this.next = null;
      this.noneFinishedItems.clear();
      this.candidateProofItems.clear();
      this.finishedCandidateMap.clear();
      this.finishedNoneSet.clear();
      this.proofsProofCount = 0;
      this.none = "finished";
   }

   public String formatSearchSearches() {
      return "entrySearches="
         + this.entrySearches
         + " entrySearchCompleted="
         + this.entrySearchCompleted
         + " entrySearchCanceled="
         + this.entrySearchCanceled
         + " entrySearchYields="
         + this.entrySearchYields
         + " entrySearchResumes="
         + this.entrySearchResumes
         + " entrySearchCommitted="
         + this.entrySearchCommitted
         + " entryProofs="
         + this.entryProofs;
   }

   public void handleCandidateCandidates(FloorBuildPlanner.Deck remaining) {
      if (remaining != null && remaining.layerY == this.candidateNoneValue) {
         this.remaining = remaining;
      }
   }

   public String getAppend() {
      return "candidateDeck="
         + (this.candidateNoneValue == Integer.MIN_VALUE ? "none" : String.valueOf(this.candidateNoneValue))
         + " searchState="
         + this.committedDiscoveredType
         + " candidateCount="
         + this.candidateProofItems.size()
         + " proofIndex="
         + this.proofsProofCount
         + " proofsThisTick="
         + this.proofsTotalValue
         + " proofsTotal="
         + this.proofsValidValue
         + " validEntriesFound="
         + this.elapsedProofsValue
         + " searchElapsedTicks="
         + this.yieldedProductiveValue
         + " yieldedForProductiveWork="
         + this.memoryYieldedFlag
         + " failureMemoryHit="
         + this.reopenMemoryValue
         + " reopenReason="
         + (this.changedCapabilityType == null ? "none" : this.changedCapabilityType);
   }

   public void handleCommittedDiscovered(Collection<Long> collection, ChangedCapability.Reopen changedCapabilityType) {
      if (!collection.isEmpty() && this.candidateNoneValue != Integer.MIN_VALUE) {
         for (long committedDiscoveredValue : collection) {
            Long finishedcandidatemap = this.finishedCandidateMap.get(committedDiscoveredValue);
            if (finishedcandidatemap != null) {
               this.candidateProofItems.add(new SearchCandidate.Candidate(finishedcandidatemap, committedDiscoveredValue));
            }
         }

         if (this.committedDiscoveredType == SearchCandidate.State.EXHAUSTED
            || this.committedDiscoveredType == SearchCandidate.State.COMMITTED
            || this.committedDiscoveredType == SearchCandidate.State.CANCELED) {
            this.committedDiscoveredType = SearchCandidate.State.PROOF_SEARCH;
            this.entrySearchResumes++;
         }

         this.changedCapabilityType = changedCapabilityType;
         this.none = "resumed: " + changedCapabilityType;
      }
   }

   public int getCandidateProof() {
      return this.candidateProofItems.size();
   }

   public ChangedCapability.Reopen getChangedCapabilityType() {
      return this.changedCapabilityType;
   }

   public int getEntrySearchResumes() {
      return this.entrySearchResumes;
   }

   public void handle(String candidateText) {
      this.handleCommittedSearch(Long.MIN_VALUE, candidateText);
   }

   public boolean canCandidateNone(int candidateNoneValue) {
      return this.candidateNoneValue == candidateNoneValue && (this.isDiscoveredCommitted() || this.isCommittedDiscovered() || this.isCommittedDiscoveredType());
   }

   public int getResumedCandidates(List<long[]> list, long resumedCandidatesValue) {
      if (!this.canCandidateNone(this.candidateNoneValue)) {
         return 0;
      } else {
         int finishedCandidate = this.getFinishedCandidate(list, resumedCandidatesValue);
         if (finishedCandidate > 0 && this.committedDiscoveredType == SearchCandidate.State.EXHAUSTED) {
            this.committedDiscoveredType = SearchCandidate.State.PROOF_SEARCH;
            this.entrySearchResumes++;
            this.changedCapabilityType = ChangedCapability.Reopen.SURFACE_TOPOLOGY_CHANGED;
            this.none = "resumed: new candidates";
         }

         return finishedCandidate;
      }
   }

   public int getFinishedNoneCount() {
      return this.noneFinishedItems.size();
   }

   public int getCandidateNoneValue() {
      return this.candidateNoneValue;
   }

   public long getTime() {
      return this.discoveredTick;
   }

   public void handleDiscoveredCandidate(FloorBuildPlanner.Deck remaining, List<long[]> list, long longValue, int value, long time) {
      if (this.isDiscoveredCommitted()) {
         this.handleCanceledSearch("superseded by a new candidate");
      }

      this.committedDiscoveredType = SearchCandidate.State.CANDIDATE_DISCOVERED;
      this.candidateNoneValue = remaining.layerY;
      this.remaining = remaining;
      this.candidateProofItems.clear();
      this.finishedCandidateMap.clear();
      this.finishedNoneSet.clear();
      this.proofsProofCount = 0;
      this.next = null;
      this.noneFinishedItems.clear();
      this.longValue = longValue;
      this.discoveredCandidateCount = value;
      this.discoveredTick = time;
      this.yieldedProductiveValue = this.proofsValidValue = this.proofsSearchCount = this.elapsedProofsValue = this.discoveredValue = this.proofsTotalValue = this.reopenMemoryValue = 0;
      this.memoryYieldedFlag = false;
      this.changedCapabilityType = null;
      this.none = "discovered";
      this.entrySearches++;
      this.getFinishedCandidate(list, longValue);
   }

   public boolean isCommittedDiscovered() {
      return this.committedDiscoveredType == SearchCandidate.State.COMMITTED && this.next != null;
   }

   public int getEntryProofs() {
      return this.entryProofs;
   }

   public SearchCandidate() {
      this.candidateProofItems = new ArrayList<>();
      this.finishedCandidateMap = new HashMap<>();
      this.finishedNoneSet = new HashSet<>();
      this.noneFinishedItems = new ArrayList<>();
      this.none = "none";
   }

   public int getValue() {
      return this.discoveredCandidateCount;
   }

   public record Budget(int proofs, int astar, long deadlineNanos) {
   }

   public record Candidate(long deckFeet, long top) {
   }

   @FunctionalInterface
   public interface Prover {
      SearchCandidate.Verdict prove(SearchCandidate.Candidate candidate, boolean proveFlag);
   }

   public static enum State {
      IDLE,
      CANDIDATE_DISCOVERED,
      PROOF_SEARCH,
      COMMITTED,
      EXHAUSTED,
      CANCELED;
   }

   public record Step(SearchCandidate.State state, int proofs, int memoryHits, int astar, boolean yielded, String transition) {
      public boolean isTransition() {
         return this.transition != null;
      }
   }

   public record Valid(long deckFeet, long top, long base, int climb, int cost, long originFeet) {
   }

   public record Verdict(
      ChangedCapability.FailureClass why, long base, int climb, int cost, ChangedCapability.Column column, boolean usedAstar, boolean deferred, int validUntil
   ) {
      public static final SearchCandidate.Verdict INSTANCE = new SearchCandidate.Verdict(null, 0L, 0, 0, ChangedCapability.Column.INSTANCE, false, true, 0);

      public SearchCandidate.Verdict getAstarDeferred(int validUntil) {
         return new SearchCandidate.Verdict(this.why, this.base, this.climb, this.cost, this.column, this.usedAstar, this.deferred, validUntil);
      }

      public boolean isDeferredWhy() {
         return this.why == null && !this.deferred;
      }

      public static SearchCandidate.Verdict getInstance(ChangedCapability.FailureClass column, long base, ChangedCapability.Column why, boolean usedAstar) {
         return new SearchCandidate.Verdict(column, base, 0, 0, why == null ? ChangedCapability.Column.INSTANCE : why, usedAstar, false, 0);
      }

      public static SearchCandidate.Verdict getInstance(long base, int climb, int cost, boolean usedAstar) {
         return new SearchCandidate.Verdict(null, base, climb, cost, ChangedCapability.Column.INSTANCE, usedAstar, false, 0);
      }
   }
}
