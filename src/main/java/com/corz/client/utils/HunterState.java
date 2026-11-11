package com.corz.client.utils;

public enum HunterState {
   PREFLIGHT("Getting ready"),
   RTP_SEND("Sending /rtp"),
   RTP_WAIT("Waiting for the teleport"),
   HOLD_STILL("Standing still for the teleport"),
   COOLDOWN_IDLE("Waiting for /rtp"),
   SETTLE("Landing"),
   SCAN("Looking for anchors"),
   PICK("Choosing an anchor"),
   APPROACH("Going to an anchor"),
   MINE("Mining an anchor"),
   COLLECT("Picking up the anchor"),
   STASH("Putting anchors in the ender chest"),
   PROTECTED("Protected area - leaving"),
   FLEE("Player nearby - leaving"),
   PAUSED("Paused"),
   STOPPED("Stopped");
   public final String displayText;

   private HunterState(String divingBreathText) {
      this.displayText = divingBreathText;
   }

   public boolean isAnchorMiningPhase() {
      return this == MINE || this == APPROACH || this == COLLECT;
   }
}
