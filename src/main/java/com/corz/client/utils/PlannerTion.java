package com.corz.client.utils;

public final class PlannerTion {
   private long totalTionNanos;
   private long tionValue;
   private long etionCanceledValue;
   private long plannerTionValue;
   private int etionPlannerCanceledCount;
   private int tionPlannerCompletedCount;

   public void handleEtionCanceled(PlannerTion.Ticket ticket, long etionCanceledValue) {
      if (ticket != null && !ticket.etionCanceledActive) {
         ticket.etionCanceledActive = true;
         this.etionCanceledValue = this.etionCanceledValue + Math.max(0L, etionCanceledValue - ticket.startNanos);
         this.etionPlannerCanceledCount++;
      }
   }

   public long getTime() {
      return this.plannerTionValue / 1000000L;
   }

   public void handleTime(PlannerTion.Ticket ticket, long nanoTime) {
      if (ticket != null && ticket.nanoTimeNano == 0L) {
         ticket.nanoTimeNano = nanoTime;
      }
   }

   public int getEtionPlannerCanceledCount() {
      return this.etionPlannerCanceledCount;
   }

   public String formatPlannerEtion() {
      return "completionPlannerCpuMs="
         + this.getTionValue()
         + " completionPlannerWallMs="
         + this.getTime()
         + " completionPlannerQueueMs="
         + this.getTotalTionNanos()
         + " completionPlannerCanceledMs="
         + this.getEtionCanceledValue()
         + " completionPlannerCompletedCount="
         + this.tionPlannerCompletedCount
         + " completionPlannerCanceledCount="
         + this.etionPlannerCanceledCount;
   }

   public PlannerTion.Ticket getTimeForLong(long plannerTionValue) {
      return new PlannerTion.Ticket(plannerTionValue);
   }

   public long getEtionCanceledValue() {
      return this.etionCanceledValue / 1000000L;
   }

   public long getTotalTionNanos() {
      return this.totalTionNanos / 1000000L;
   }

   public long getTionValue() {
      return this.tionValue / 1000000L;
   }

   public void handleTimeTion(PlannerTion.Ticket ticket, long tionValue) {
      if (ticket != null && !ticket.etionCanceledActive) {
         ticket.etionCanceledActive = true;
         long tionValue2 = ticket.nanoTimeNano == 0L ? tionValue : ticket.nanoTimeNano;
         long tionValue3 = ticket.nanoTimeTion == 0L ? tionValue : ticket.nanoTimeTion;
         this.totalTionNanos = this.totalTionNanos + Math.max(0L, tionValue2 - ticket.startNanos);
         this.tionValue = this.tionValue + Math.max(0L, tionValue3 - tionValue2);
         this.plannerTionValue = this.plannerTionValue + Math.max(0L, tionValue - ticket.startNanos);
         this.tionPlannerCompletedCount++;
      }
   }

   public void handleTimeForTicketLong(PlannerTion.Ticket ticket, long nanoTime) {
      if (ticket != null && ticket.nanoTimeTion == 0L) {
         ticket.nanoTimeTion = nanoTime;
      }
   }

   public int getTionPlannerCompletedCount() {
      return this.tionPlannerCompletedCount;
   }

   public static final class Ticket {
      volatile long nanoTimeTion;
      volatile long nanoTimeNano;
      final long startNanos;
      boolean etionCanceledActive;

      public long getTime() {
         return this.startNanos;
      }

      public long getNanoTimeNano() {
         return this.nanoTimeNano;
      }

      public long getNanoTime() {
         return this.nanoTimeTion;
      }

      Ticket(long time) {
         this.startNanos = time;
      }
   }
}
