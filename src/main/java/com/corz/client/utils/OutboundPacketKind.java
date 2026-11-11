package com.corz.client.utils;

public enum OutboundPacketKind {
   ATTACK,
   INTERACT_ENTITY,
   INTERACT_BLOCK,
   USE_ITEM,
   DIG,
   RELEASE_USE,
   DROP,
   SWAP_OFFHAND,
   SLOT,
   MOVE,
   MOVE_LOOK,
   INPUT,
   SPRINT_START,
   SPRINT_STOP,
   CLICK_SLOT,
   CLOSE_SCREEN,
   TELEPORT_CONFIRM,
   TICK_END,
   SWING,
   OTHER;

   public boolean isInteractionPacket() {
      return this.ordinal() <= DROP.ordinal();
   }
}
