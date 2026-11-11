package com.corz.client.utils;

import com.corz.client.world.CenterVecData;

public final class ComboPhaseTracker {
   private int frozenTicks;
   private int episodeTicks;
   private int openerInvalidTicks;
   private int settledTicks;
   private int noKillSoonTicks;
   private CenterVecData.State currentPhase = CenterVecData.State.IDLE;
   private int phaseTicks;

   private ComboPhaseTracker.Out transitionTo(CenterVecData.State state, CenterVecData.State followJuggleType, String poppedOpenerText) {
      this.currentPhase = followJuggleType;
      this.phaseTicks = 0;
      this.openerInvalidTicks = 0;
      this.noKillSoonTicks = 0;
      this.settledTicks = 0;
      return new ComboPhaseTracker.Out(followJuggleType, state, false, false, poppedOpenerText);
   }

   public int getFrozenTicks() {
      return this.frozenTicks;
   }

   public ComboPhaseTracker.Out startEpisode(CenterVecData.State followJuggleType) {
      CenterVecData.State currentPhase2 = this.currentPhase;
      if (this.currentPhase == CenterVecData.State.IDLE && followJuggleType != null && followJuggleType != CenterVecData.State.IDLE) {
         this.currentPhase = followJuggleType;
         this.phaseTicks = 0;
         this.episodeTicks = 0;
         this.frozenTicks = 0;
         this.openerInvalidTicks = 0;
         this.noKillSoonTicks = 0;
         this.settledTicks = 0;
         return new ComboPhaseTracker.Out(this.currentPhase, currentPhase2, true, false, "start");
      } else {
         return new ComboPhaseTracker.Out(this.currentPhase, currentPhase2, false, false, null);
      }
   }

   private ComboPhaseTracker.Out stayInPhase(CenterVecData.State state, String juggleFollowText) {
      return new ComboPhaseTracker.Out(this.currentPhase, state, false, false, juggleFollowText);
   }

   public void reset() {
      this.currentPhase = CenterVecData.State.IDLE;
      this.phaseTicks = 0;
      this.episodeTicks = 0;
      this.frozenTicks = 0;
      this.openerInvalidTicks = 0;
      this.noKillSoonTicks = 0;
      this.settledTicks = 0;
   }

   public CenterVecData.State getCurrentPhase() {
      return this.currentPhase;
   }

   public int getSettledTicks() {
      return this.settledTicks;
   }

   private static boolean isTargetSettledInComboRange(ComboPhaseTracker.Inputs inputs) {
      return inputs.targetGrounded()
         && !inputs.targetAirborne()
         && inputs.targetDistance() <= inputs.comboRange()
         && !inputs.reopenReady()
         && !inputs.prestagePlanned()
         && !inputs.prestagedArmed()
         && !inputs.juggleStep()
         && !inputs.pearlRule();
   }

   public ComboPhaseTracker.Out advancePhase(ComboPhaseTracker.Inputs inputs) {
      CenterVecData.State followJuggleType = this.currentPhase;
      if (this.currentPhase == CenterVecData.State.IDLE) {
         return new ComboPhaseTracker.Out(this.currentPhase, followJuggleType, false, false, null);
      } else if (inputs == null) {
         return this.stayInPhase(followJuggleType, null);
      } else if (inputs.abort() != null && inputs.abort() != CenterVecData.Abort.NONE) {
         return this.finishEpisode(followJuggleType, inputs.abort().name());
      } else if (inputs.frozen()) {
         this.frozenTicks++;
         return this.frozenTicks >= 40 ? this.finishEpisode(followJuggleType, "frozen") : this.stayInPhase(followJuggleType, "frozen");
      } else {
         this.frozenTicks = 0;
         this.phaseTicks++;
         this.episodeTicks++;
         if (this.currentPhase != CenterVecData.State.FINISHER && inputs.targetPearlSeen()) {
            return this.transitionTo(followJuggleType, CenterVecData.State.RECOMBO, "pearl seen");
         } else if (inputs.killSoon() && canEnterFinisherFrom(this.currentPhase)) {
            return this.transitionTo(followJuggleType, CenterVecData.State.FINISHER, "kill soon");
         } else if (this.episodeTicks >= 600) {
            return this.finishEpisode(followJuggleType, "cap");
         } else {
            return switch (this.currentPhase) {
               case OPENER -> this.updateOpenerPhase(followJuggleType, inputs);
               case LAUNCH -> this.updateLaunchPhase(followJuggleType, inputs);
               case JUGGLE -> this.updateJugglePhase(followJuggleType, inputs);
               case FOLLOW -> this.updateFollowPhase(followJuggleType, inputs);
               case PEARL_FOLLOW -> this.updatePearlFollowPhase(followJuggleType, inputs);
               case ANCHOR_BELOW -> this.updateAnchorBelowPhase(followJuggleType, inputs);
               case RECOMBO -> this.updateRecomboPhase(followJuggleType, inputs);
               case FINISHER -> this.updateFinisherPhase(followJuggleType, inputs);
               default -> this.stayInPhase(followJuggleType, null);
            };
         }
      }
   }

   public int getPhaseTicks() {
      return this.phaseTicks;
   }

   private ComboPhaseTracker.Out updatePearlFollowPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      return !inputs.pearlThrown() && !inputs.pearlFailed() && this.phaseTicks < 12
         ? this.stayInPhase(state, null)
         : this.transitionTo(state, CenterVecData.State.FOLLOW, inputs.pearlThrown() ? "thrown" : "no throw");
   }

   public int getEpisodeTicks() {
      return this.episodeTicks;
   }

   public ComboPhaseTracker.Out endEpisode(String juggleFollowText) {
      CenterVecData.State followJuggleType = this.currentPhase;
      return this.currentPhase == CenterVecData.State.IDLE
         ? new ComboPhaseTracker.Out(this.currentPhase, followJuggleType, false, false, null)
         : this.finishEpisode(followJuggleType, juggleFollowText == null ? "end" : juggleFollowText);
   }

   private ComboPhaseTracker.Out updateFollowPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.targetAirborne() && inputs.landIn() >= 8 && inputs.juggleStep()) {
         return this.transitionTo(state, CenterVecData.State.JUGGLE, "airborne");
      } else if (inputs.prestagedArmed() && inputs.targetArrived()) {
         return this.transitionTo(state, CenterVecData.State.LAUNCH, "prestaged");
      } else if (inputs.reopenReady()) {
         return this.transitionTo(state, CenterVecData.State.OPENER, "reopen");
      } else if (inputs.pearlRule()) {
         return this.transitionTo(state, CenterVecData.State.PEARL_FOLLOW, "pearl");
      } else if (inputs.pursuitAllowed() && this.phaseTicks < inputs.followTimeout()) {
         this.settledTicks = isTargetSettledInComboRange(inputs) ? this.settledTicks + 1 : 0;
         return this.settledTicks >= 20 ? this.finishEpisode(state, "settled") : this.stayInPhase(state, null);
      } else {
         return this.finishEpisode(state, "lost");
      }
   }

   private ComboPhaseTracker.Out updateFinisherPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.killed()) {
         return this.finishEpisode(state, "kill");
      } else {
         this.noKillSoonTicks = inputs.killSoon() ? 0 : this.noKillSoonTicks + 1;
         return !inputs.popped() && this.noKillSoonTicks < 3
            ? this.stayInPhase(state, null)
            : this.transitionTo(state, inputs.targetAirborne() ? CenterVecData.State.JUGGLE : CenterVecData.State.FOLLOW, inputs.popped() ? "popped" : "no kill");
      }
   }

   private ComboPhaseTracker.Out updateRecomboPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.targetTeleported() && inputs.prestagedArmed()) {
         return this.transitionTo(state, CenterVecData.State.LAUNCH, "prestage hit");
      } else if (inputs.targetTeleported() && inputs.reopenReady()) {
         return this.transitionTo(state, CenterVecData.State.OPENER, "reopen");
      } else if (inputs.targetTeleported()) {
         return this.transitionTo(state, CenterVecData.State.FOLLOW, "landed");
      } else if (inputs.counterPearlChosen()) {
         return this.transitionTo(state, CenterVecData.State.PEARL_FOLLOW, "counter pearl");
      } else {
         return this.phaseTicks >= 60 ? this.transitionTo(state, CenterVecData.State.FOLLOW, "recombo timeout") : this.stayInPhase(state, null);
      }
   }

   private static boolean canEnterFinisherFrom(CenterVecData.State state) {
      return state == CenterVecData.State.LAUNCH
         || state == CenterVecData.State.JUGGLE
         || state == CenterVecData.State.FOLLOW
         || state == CenterVecData.State.RECOMBO
         || state == CenterVecData.State.ANCHOR_BELOW;
   }

   private ComboPhaseTracker.Out finishEpisode(CenterVecData.State state, String frozenSeenText) {
      this.currentPhase = CenterVecData.State.IDLE;
      this.phaseTicks = 0;
      this.episodeTicks = 0;
      this.frozenTicks = 0;
      this.openerInvalidTicks = 0;
      this.noKillSoonTicks = 0;
      this.settledTicks = 0;
      return new ComboPhaseTracker.Out(CenterVecData.State.IDLE, state, false, true, frozenSeenText);
   }

   private ComboPhaseTracker.Out updateOpenerPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.strikeSent()) {
         return this.transitionTo(state, CenterVecData.State.LAUNCH, "strike sent");
      } else if (inputs.targetDistance() > inputs.comboRange() + 2.0 && inputs.pursuitAllowed()) {
         return this.transitionTo(state, CenterVecData.State.FOLLOW, "target left");
      } else {
         this.openerInvalidTicks = inputs.openerValid() ? 0 : this.openerInvalidTicks + 1;
         return this.phaseTicks < 80 && this.openerInvalidTicks < 10 ? this.stayInPhase(state, null) : this.finishEpisode(state, "opener");
      }
   }

   private ComboPhaseTracker.Out updateJugglePhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.juggleStep()) {
         return this.stayInPhase(state, null);
      } else {
         return inputs.pearlRule()
            ? this.transitionTo(state, CenterVecData.State.PEARL_FOLLOW, "pearl ahead")
            : this.transitionTo(state, CenterVecData.State.FOLLOW, "juggle over");
      }
   }

   private ComboPhaseTracker.Out updateLaunchPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.liftConfirmed()) {
         return this.transitionTo(state, CenterVecData.State.JUGGLE, "lift");
      } else {
         return !inputs.noLiftAfterBlast() && (!inputs.ourBlastSeen() || !inputs.targetGrounded()) && (this.phaseTicks < 14 || inputs.ourBlastSeen())
            ? this.stayInPhase(state, null)
            : this.transitionTo(state, CenterVecData.State.FOLLOW, "no lift");
      }
   }

   private ComboPhaseTracker.Out updateAnchorBelowPhase(CenterVecData.State state, ComboPhaseTracker.Inputs inputs) {
      if (inputs.anchorDetonated() && inputs.liftConfirmed()) {
         return this.transitionTo(state, CenterVecData.State.JUGGLE, "anchor lift");
      } else {
         return !inputs.anchorFailed() && !inputs.belowLevelLost() && this.phaseTicks < 160
            ? this.stayInPhase(state, null)
            : this.transitionTo(state, CenterVecData.State.FOLLOW, "anchor over");
      }
   }

   public record Inputs(
      CenterVecData.Abort abort,
      boolean frozen,
      boolean strikeSent,
      boolean ourBlastSeen,
      boolean liftConfirmed,
      boolean noLiftAfterBlast,
      boolean targetAirborne,
      boolean targetGrounded,
      int landIn,
      double targetDistance,
      boolean juggleStep,
      boolean killSoon,
      boolean popped,
      boolean killed,
      boolean reopenReady,
      boolean prestagedArmed,
      boolean targetArrived,
      boolean pearlRule,
      boolean pearlThrown,
      boolean pearlFailed,
      boolean targetPearlSeen,
      boolean targetTeleported,
      boolean counterPearlChosen,
      boolean anchorDetonated,
      boolean anchorFailed,
      boolean belowLevelLost,
      boolean openerValid,
      boolean pursuitAllowed,
      double comboRange,
      int followTimeout,
      boolean prestagePlanned
   ) {
   }

   public record Out(CenterVecData.State state, CenterVecData.State previous, boolean episodeStarted, boolean episodeEnded, String reason) {
   }
}
