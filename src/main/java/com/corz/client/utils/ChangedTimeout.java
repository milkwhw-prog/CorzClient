package com.corz.client.utils;
import com.corz.client.utils.SupportSnapshot;

public final class ChangedTimeout {
   private final int startTick;
   private ChangedTimeout.Action alignConfirmationType;
   private final SupportSnapshot supportSnapshot;
   private int lastTick = -1;
   private boolean supportSnapshotActive;
   private ChangedTimeout.Failure timeoutChangedType = ChangedTimeout.Failure.NONE;

   public ChangedTimeout.Action getAborted() {
      return this.getConfirmationAlign(ChangedTimeout.Failure.ABORTED);
   }

   private ChangedTimeout.Action getConfirmationAlign(ChangedTimeout.Failure timeoutChangedType) {
      this.timeoutChangedType = timeoutChangedType;
      return this.alignConfirmationType = ChangedTimeout.Action.FAILED;
   }

   public ChangedTimeout(SupportSnapshot supportSnapshot, int value) {
      this.supportSnapshot = supportSnapshot;
      this.startTick = value;
   }

   public ChangedTimeout.Action getAborted(ChangedTimeout.Failure failure) {
      return this.getConfirmationAlign(failure == null ? ChangedTimeout.Failure.ABORTED : failure);
   }

   public boolean isFlag() {
      return this.supportSnapshotActive;
   }

   public ChangedTimeout.Failure getTimeoutChangedType() {
      return this.timeoutChangedType;
   }

   public ChangedTimeout.Action getSupportSnapshot(ChangedTimeout.Observation observation) {
      if (this.alignConfirmationType != null) {
         return this.alignConfirmationType;
      } else if (observation.tick() - this.startTick > this.supportSnapshot.getSupportSnapshotValue()) {
         return this.getConfirmationAlign(ChangedTimeout.Failure.TIMEOUT);
      } else if (this.supportSnapshotActive) {
         if (!observation.supportPreserved()) {
            return this.getConfirmationAlign(observation.dirtPresent() ? ChangedTimeout.Failure.SUPPORT_CHANGED : ChangedTimeout.Failure.ACCIDENTAL_INTERACTION);
         } else if (observation.dirtPresent()) {
            return this.alignConfirmationType = ChangedTimeout.Action.CONFIRMED;
         } else if (!observation.targetReplaceable()) {
            return this.getConfirmationAlign(ChangedTimeout.Failure.WRONG_RESULT);
         } else {
            return observation.tick() - this.lastTick > this.supportSnapshot.getMinSupportLimit()
               ? this.getConfirmationAlign(ChangedTimeout.Failure.CONFIRM_TIMEOUT)
               : ChangedTimeout.Action.WAIT_CONFIRMATION;
         }
      } else if (!observation.supportPreserved()) {
         return this.getConfirmationAlign(ChangedTimeout.Failure.SUPPORT_CHANGED);
      } else if (!observation.targetReplaceable()) {
         return this.getConfirmationAlign(ChangedTimeout.Failure.WRONG_RESULT);
      } else if (!this.supportSnapshot.isInteractionRequiredFlag() || observation.sneaking() && observation.sneakTicks() >= this.supportSnapshot.getMax()) {
         if (!observation.exactRay()) {
            return ChangedTimeout.Action.ALIGN;
         } else {
            this.supportSnapshotActive = true;
            this.lastTick = observation.tick();
            return ChangedTimeout.Action.CLICK_ONCE;
         }
      } else {
         return ChangedTimeout.Action.HOLD_SNEAK;
      }
   }

   public SupportSnapshot getSupportSnapshot() {
      return this.supportSnapshot;
   }

   public static enum Action {
      HOLD_SNEAK,
      ALIGN,
      CLICK_ONCE,
      WAIT_CONFIRMATION,
      CONFIRMED,
      FAILED;
   }

   public static enum Failure {
      NONE,
      TIMEOUT,
      CONFIRM_TIMEOUT,
      SUPPORT_CHANGED,
      ACCIDENTAL_INTERACTION,
      WRONG_RESULT,
      STAND_CHANGED,
      NO_LEGAL_AIM,
      ABORTED;
   }

   public record Observation(
      int tick, boolean sneaking, int sneakTicks, boolean exactRay, boolean dirtPresent, boolean targetReplaceable, boolean supportPreserved
   ) {
   }
}
