package com.corz.client.utils;

public interface ScissorTgtHandler {
   ScissorTgtHandler.Snapshot getStatusSnapshot();

   public record Snapshot(ScissorTgtHandler.Tone tone, String state, String target, int pops, int popsTaken, String hint) {
      public static final ScissorTgtHandler.Snapshot INSTANCE = new ScissorTgtHandler.Snapshot(
         ScissorTgtHandler.Tone.IDLE, "Off", null, 0, 0, "Turn it on, then close the menu to start."
      );

      public Snapshot(ScissorTgtHandler.Tone tone, String state, String target, int pops, int popsTaken, String hint) {
         tone = tone == null ? ScissorTgtHandler.Tone.IDLE : tone;
         state = state != null && !state.isBlank() ? state : "Off";
         target = target != null && !target.isBlank() ? target : null;
         pops = Math.max(0, pops);
         popsTaken = Math.max(0, popsTaken);
         hint = hint != null && !hint.isBlank() ? hint : null;
         this.tone = tone;
         this.state = state;
         this.target = target;
         this.pops = pops;
         this.popsTaken = popsTaken;
         this.hint = hint;
      }

      public boolean isPopsTaken() {
         return this.target != null || this.pops > 0 || this.popsTaken > 0;
      }
   }

   public static enum Tone {
      IDLE,
      ACTIVE,
      PAUSED,
      DANGER;
   }
}
