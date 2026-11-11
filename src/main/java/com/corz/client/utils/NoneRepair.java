package com.corz.client.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongSupplier;

public final class NoneRepair {
   public long repairMemoryHits;
   public long repairReopens;
   public long uplicateDispatchPrevented;
   private long noneCount;
   public long repairTickets;
   private final Map<Long, NoneRepair.Ticket> proofBudgetTicketMap = new HashMap<>();
   public long repairCompleted;
   public long repairBlocked;

   public NoneRepair.Ticket getNoneForLong(long noneValue) {
      return this.proofBudgetTicketMap.get(noneValue);
   }

   public NoneRepair.Decision getProofBudget(
      long proofBudgetValue, String proofBudgetText, String proofBudgetText2, NoneRepair.Kind kind, String noneProofText, long changedDependencyValue, LongSupplier longSupplier, long dependencyValue
   ) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(proofBudgetValue);
      if (ticket == null) {
         if (this.proofBudgetTicketMap.size() >= 256) {
            this.updateCompletedDispatched();
         }

         ticket = new NoneRepair.Ticket(this.noneCount++, proofBudgetValue, kind, proofBudgetText, proofBudgetText2);
         this.proofBudgetTicketMap.put(proofBudgetValue, ticket);
         this.repairTickets++;
      }

      switch (ticket.dispatchedCompletedType) {
         case DISPATCHED:
            if (!ticket.noneProofText.equals(noneProofText)) {
               this.uplicateDispatchPrevented++;
               ticket.proofBudgetCount++;
               return NoneRepair.Decision.DUPLICATE_DISPATCH_PREVENTED;
            } else {
               if (ticket.proofBudgetValue >= 3) {
                  this.handleNoneBlocked(ticket, "PROOF_BUDGET_EXHAUSTED", ticket.none, new NoneRepair.Dependency(changedDependencyValue, longSupplier.getAsLong(), (long[])null));
                  return NoneRepair.Decision.BLOCKED_PROOF_BUDGET;
               }

               ticket.proofBudgetValue++;
               return NoneRepair.Decision.DISPATCH;
            }
         case BLOCKED:
            String noneText = formatChangedDependency(ticket, changedDependencyValue, longSupplier, dependencyValue);
            if (noneText == null) {
               this.repairMemoryHits++;
               ticket.noneValue++;
               return NoneRepair.Decision.BLOCKED_MEMORY_HIT;
            }

            this.repairReopens++;
            ticket.ticketCount++;
            ticket.noneText = noneText;
            ticket.proofBudgetText = "";
            ticket.none = "none";
            ticket.dispatchedCompletedType = NoneRepair.State.DISPATCHED;
            ticket.noneProofText = noneProofText;
            ticket.proofBudgetValue = 1;
            ticket.noneValue = 0;
            ticket.proofBudgetCount = 0;
            return NoneRepair.Decision.REOPENED;
         case COMPLETED:
            NoneRepair.Ticket botCrystalValue = new NoneRepair.Ticket(this.noneCount++, proofBudgetValue, kind, proofBudgetText, proofBudgetText2);
            this.proofBudgetTicketMap.put(proofBudgetValue, botCrystalValue);
            this.repairTickets++;
            botCrystalValue.dispatchedCompletedType = NoneRepair.State.DISPATCHED;
            botCrystalValue.noneProofText = noneProofText;
            botCrystalValue.proofBudgetValue = 1;
            return NoneRepair.Decision.DISPATCH;
         default:
            if (ticket.proofBudgetValue >= 3) {
               this.handleNoneBlocked(ticket, "PROOF_BUDGET_EXHAUSTED", ticket.none, new NoneRepair.Dependency(changedDependencyValue, longSupplier.getAsLong(), (long[])null));
               return NoneRepair.Decision.BLOCKED_PROOF_BUDGET;
            } else {
               ticket.dispatchedCompletedType = NoneRepair.State.DISPATCHED;
               ticket.noneProofText = noneProofText;
               ticket.proofBudgetValue++;
               return NoneRepair.Decision.DISPATCH;
            }
      }
   }

   public void handleNone(long noneValue, long longValue, String noneText) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(noneValue);
      if (ticket != null) {
         ticket.longValue = longValue;
         ticket.none = noneText == null ? "none" : noneText;
      }
   }

   public void handle(long noneRepairValue) {
      this.proofBudgetTicketMap.remove(noneRepairValue);
   }

   private static String formatChangedDependency(NoneRepair.Ticket ticket, long changedDependencyValue, LongSupplier longSupplier, long dependencyValue) {
      NoneRepair.Dependency changedComponent = ticket.changedComponent;
      if (changedComponent == null) {
         return "no_dependency_recorded";
      } else if (!changedComponent.canFlag(dependencyValue)) {
         return "component_changed";
      } else {
         if (changedDependencyValue != changedComponent.componentChanged) {
            long asLong = longSupplier.getAsLong();
            if (asLong != changedComponent.longValue) {
               return "dependency_geometry_changed";
            }

            ticket.changedComponent = changedComponent.getChangedComponent(changedDependencyValue);
         }

         return null;
      }
   }

   public void update() {
      this.proofBudgetTicketMap.clear();
   }

   public int getDispatchedCompleted(String dispatchedCompletedText) {
      int dispatchedCompleted = 0;

      for (NoneRepair.Ticket ticket : this.proofBudgetTicketMap.values()) {
         if (ticket.dispatchedCompletedType == NoneRepair.State.DISPATCHED && ticket.noneProofText.equals(dispatchedCompletedText)) {
            ticket.dispatchedCompletedType = NoneRepair.State.OPEN;
            ticket.noneProofText = "";
            dispatchedCompleted++;
         }
      }

      return dispatchedCompleted;
   }

   private void handleNoneBlocked(NoneRepair.Ticket ticket, String noneBlockedText, String noneBlockedText2, NoneRepair.Dependency changedComponent) {
      ticket.dispatchedCompletedType = NoneRepair.State.BLOCKED;
      ticket.proofBudgetText = noneBlockedText == null ? "" : noneBlockedText;
      ticket.none = noneBlockedText2 == null ? "none" : noneBlockedText2;
      ticket.changedComponent = changedComponent;
      ticket.noneCount++;
      ticket.proofBudgetValue = 0;
      ticket.noneValue = 0;
      ticket.noneProofText = "";
      this.repairBlocked++;
   }

   public NoneRepair() {
      this.noneCount = 1L;
   }

   public void handleCompletedRepair(long completedRepairValue) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(completedRepairValue);
      if (ticket != null) {
         ticket.dispatchedCompletedType = NoneRepair.State.COMPLETED;
         ticket.noneProofText = "";
         this.repairCompleted++;
      }
   }

   public int getValue() {
      return this.proofBudgetTicketMap.size();
   }

   public String formatRepairUplicate() {
      return "repairTickets="
         + this.repairTickets
         + " repairCompleted="
         + this.repairCompleted
         + " repairBlocked="
         + this.repairBlocked
         + " repairMemoryHits="
         + this.repairMemoryHits
         + " repairReopens="
         + this.repairReopens
         + " repairDuplicateDispatchPrevented="
         + this.uplicateDispatchPrevented;
   }

   public void handleDispatchedCompleted(long dispatchedCompletedValue) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(dispatchedCompletedValue);
      if (ticket != null && ticket.dispatchedCompletedType == NoneRepair.State.DISPATCHED) {
         ticket.dispatchedCompletedType = NoneRepair.State.OPEN;
         ticket.noneProofText = "";
      }
   }

   public boolean canDispatchedCompleted(long dispatchedCompletedValue, long changedDependencyValue, LongSupplier longSupplier, long dependencyValue) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(dispatchedCompletedValue);
      return ticket != null
         && ticket.dispatchedCompletedType == NoneRepair.State.BLOCKED
         && formatChangedDependency(ticket, changedDependencyValue, longSupplier, dependencyValue) == null;
   }

   public void handle(long noneRepairValue, String noneRepairText, String noneRepairText2, NoneRepair.Dependency changedComponent) {
      NoneRepair.Ticket ticket = this.proofBudgetTicketMap.get(noneRepairValue);
      if (ticket != null) {
         this.handleNoneBlocked(ticket, noneRepairText, noneRepairText2, changedComponent);
      }
   }

   public static String formatNoneChanged(NoneRepair.Ticket ticket, boolean noneChangedFlag) {
      return String.format(
         Locale.ROOT,
         "repairKind=%s state=%s accessProof=%s blockedReason=%s dependencyFingerprint=%s memoryHit=%s reopenReason=%s ticket=%d dispatches=%d proofs=%d",
         ticket.breakReplaceType,
         ticket.dispatchedCompletedType,
         ticket.none,
         ticket.proofBudgetText.isEmpty() ? "none" : ticket.proofBudgetText,
         ticket.changedComponent == null
            ? "none"
            : Long.toUnsignedString(ticket.changedComponent.longValue)
               + (ticket.changedComponent.isFlag() ? "+component(" + ticket.changedComponent.getValue() + ")" : ""),
         noneChangedFlag,
         ticket.noneText.isEmpty() ? "none" : ticket.noneText,
         ticket.noneTime,
         ticket.proofBudgetValue,
         ticket.noneCount
      );
   }

   private void updateCompletedDispatched() {
      long completedDispatchedValue = 0L;
      boolean completedDispatchedFlag = false;

      for (Entry entry : this.proofBudgetTicketMap.entrySet()) {
         if (((NoneRepair.Ticket)entry.getValue()).dispatchedCompletedType == NoneRepair.State.COMPLETED
            || ((NoneRepair.Ticket)entry.getValue()).dispatchedCompletedType == NoneRepair.State.BLOCKED) {
            completedDispatchedValue = (Long)entry.getKey();
            completedDispatchedFlag = true;
            break;
         }
      }

      if (!completedDispatchedFlag) {
         completedDispatchedValue = this.proofBudgetTicketMap.keySet().iterator().next();
      }

      this.proofBudgetTicketMap.remove(completedDispatchedValue);
   }

   public static enum Decision {
      DISPATCH,
      DUPLICATE_DISPATCH_PREVENTED,
      BLOCKED_MEMORY_HIT,
      BLOCKED_PROOF_BUDGET,
      REOPENED;
   }

   public static final class Dependency {
      private final HashSet<Long> changedComponentPositionSet;
      public final long componentChanged;
      public final long longValue;

      public boolean isFlag() {
         return this.changedComponentPositionSet != null;
      }

      private Dependency(long componentChanged, long longValue, HashSet<Long> set) {
         this.componentChanged = componentChanged;
         this.longValue = longValue;
         this.changedComponentPositionSet = set;
      }

      NoneRepair.Dependency getChangedComponent(long changedComponentValue) {
         return new NoneRepair.Dependency(changedComponentValue, this.longValue, this.changedComponentPositionSet);
      }

      public int getValue() {
         return this.changedComponentPositionSet == null ? 0 : this.changedComponentPositionSet.size();
      }

      public Dependency(long componentChanged, long longValue, long[] dependencyValues) {
         this.componentChanged = componentChanged;
         this.longValue = longValue;
         if (dependencyValues != null && dependencyValues.length != 0) {
            this.changedComponentPositionSet = new HashSet<>(dependencyValues.length * 2);

            for (long dependencyValue : dependencyValues) {
               this.changedComponentPositionSet.add(dependencyValue);
            }
         } else {
            this.changedComponentPositionSet = null;
         }
      }

      public boolean canFlag(long dependencyValue) {
         return this.changedComponentPositionSet == null || this.changedComponentPositionSet.contains(dependencyValue);
      }
   }

   public static enum Kind {
      BREAK_REPLACE,
      MISSION_BREAK;
   }

   public static enum State {
      OPEN,
      DISPATCHED,
      BLOCKED,
      COMPLETED;
   }

   public static final class Ticket {
      public final long noneTime;
      public String noneProofText;
      public int noneValue;
      public final long createdTick;
      public String none;
      public int noneCount;
      public int proofBudgetValue;
      public final String ticketText;
      public final NoneRepair.Kind breakReplaceType;
      public String noneText;
      public int ticketCount;
      public NoneRepair.State dispatchedCompletedType = NoneRepair.State.OPEN;
      public NoneRepair.Dependency changedComponent;
      public int proofBudgetCount;
      public final String ticketReasonText;
      public long longValue;
      public String proofBudgetText;

      public boolean isDispatchedCompleted() {
         return this.dispatchedCompletedType == NoneRepair.State.BLOCKED;
      }

      Ticket(long noneTime, long time, NoneRepair.Kind breakReplaceType, String text, String text2) {
         this.noneProofText = "";
         this.none = "none";
         this.proofBudgetText = "";
         this.noneText = "";
         this.noneTime = noneTime;
         this.createdTick = time;
         this.breakReplaceType = breakReplaceType;
         this.ticketReasonText = text;
         this.ticketText = text2;
      }
   }
}
