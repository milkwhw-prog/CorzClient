package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public final class DequeConstraint {
   private final Deque<DequeConstraint.Entry> deque = new ArrayDeque<>();

   public boolean isEquivalentConstraint() {
      DequeConstraint.Entry peekFirst = this.deque.peekFirst();
      return peekFirst != null && peekFirst.equivalentRepeatCount() > 1 && !peekFirst.constraintPublished();
   }

   public void handleDequeFailure(DequeConstraint.Entry entry) {
      if (entry != null) {
         DequeConstraint.Entry peekFirst = this.deque.peekFirst();
         if (peekFirst != null && peekFirst.canFingerprintGeometry(entry)) {
            this.deque.removeFirst();
            this.deque
               .addFirst(
                  new DequeConstraint.Entry(
                     entry.failureTick(),
                     entry.missionId(),
                     entry.missionRevision(),
                     entry.target(),
                     entry.stand(),
                     entry.actionType(),
                     entry.failureClass(),
                     entry.failureReason(),
                     entry.routeStart(),
                     entry.routeGoal(),
                     entry.geometryFingerprint(),
                     entry.tempAssetFingerprint(),
                     entry.componentKey(),
                     entry.worldRevision(),
                     peekFirst.equivalentRepeatCount() + 1,
                     entry.constraintPublished(),
                     entry.constraintKind(),
                     entry.constraintKey(),
                     entry.nextDecisionAfterFailure()
                  )
               );
         } else {
            this.deque.addFirst(entry);

            while (this.deque.size() > 16) {
               this.deque.removeLast();
            }
         }
      }
   }

   public List<DequeConstraint.Entry> getCopyOf() {
      return List.copyOf(this.deque);
   }

   public DequeConstraint.Entry getPeekFirst() {
      return this.deque.peekFirst();
   }

   public void handleDeque(long dequeValue, long dequeValue2, String dequeText) {
      this.deque.removeIf(entry -> entry.target() == dequeValue && entry.stand() == dequeValue2 && entry.actionType().equals(dequeText));
   }

   public int getEquivalentRepeat() {
      DequeConstraint.Entry peekFirst = this.deque.peekFirst();
      return peekFirst == null ? 0 : peekFirst.equivalentRepeatCount();
   }

   public void updateDeque() {
      this.deque.clear();
   }

   public int getDequeSize() {
      return this.deque.size();
   }

   public record Entry(
      int failureTick,
      long missionId,
      long missionRevision,
      long target,
      long stand,
      String actionType,
      String failureClass,
      String failureReason,
      long routeStart,
      long routeGoal,
      long geometryFingerprint,
      long tempAssetFingerprint,
      long componentKey,
      long worldRevision,
      int equivalentRepeatCount,
      boolean constraintPublished,
      String constraintKind,
      long constraintKey,
      String nextDecisionAfterFailure
   ) {
      public boolean canFingerprintGeometry(DequeConstraint.Entry o) {
         return o != null
            && this.target == o.target
            && this.stand == o.stand
            && this.actionType.equals(o.actionType)
            && this.geometryFingerprint == o.geometryFingerprint;
      }

      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "tick=%d target=%d stand=%d action=%s class=%s repeats=%d constraint=%s reason=%s",
            this.failureTick,
            this.target,
            this.stand,
            this.actionType,
            this.failureClass,
            this.equivalentRepeatCount,
            this.constraintPublished ? this.constraintKind : "NONE",
            this.failureReason
         );
      }
   }
}
