package com.corz.client.utils;
import com.corz.client.utils.OutboundPacketKind;

public final class TickActionLedger {
   private long tickCounter;
   private int slotChangeCount;
   private boolean sprint;
   private int interactCount;
   private float lastSentYaw;
   private int selectedSlot = -1;
   private boolean interactDuringHandswap;
   private boolean sneak;
   private boolean tickEndSeen;
   private boolean forcedInteractRecorded;
   private boolean hasSentLook;
   private float lastSentPitch;
   private long lastClientTime;
   private long lastHandswapInteractTick = -1L;
   private boolean unmanagedSlotChange;
   private OutboundPacketKind firstInteractType;
   private boolean interactOutsideHandswap;
   private boolean containerScreenActive;
   private boolean flaggedPacketSeen;
   private boolean hasClientTime;

   public long getTickCounter() {
      return this.tickCounter;
   }

   public boolean hasSlotChangeThisTick() {
      return this.slotChangeCount > 0;
   }

   public void markContainerClicked() {
      this.containerScreenActive = true;
   }

   public boolean isSprint() {
      return this.sprint;
   }

   public void setSelectedSlot(int selectedSlot) {
      this.selectedSlot = selectedSlot;
   }

   public int getInteractCount() {
      return this.interactCount;
   }

   private void resetTickState() {
      this.interactCount = 0;
      this.slotChangeCount = 0;
      this.unmanagedSlotChange = false;
      this.interactDuringHandswap = false;
      this.interactOutsideHandswap = false;
      this.forcedInteractRecorded = false;
      this.flaggedPacketSeen = false;
      this.firstInteractType = null;
   }

   public boolean hasSentLookPacket() {
      return this.hasSentLook;
   }

   public float getLastSentYaw() {
      return this.lastSentYaw;
   }

   public boolean hasInteractDuringHandswap() {
      return this.interactDuringHandswap;
   }

   public void onSlotSelected(int selectedSlot, boolean selectedSlotFlag) {
      this.slotChangeCount++;
      this.selectedSlot = selectedSlot;
      if (!selectedSlotFlag) {
         this.unmanagedSlotChange = true;
      }
   }

   public boolean isContainerScreenActive() {
      return this.containerScreenActive;
   }

   public float getLastSentPitch() {
      return this.lastSentPitch;
   }

   public long getLastHandswapInteractTick() {
      return this.lastHandswapInteractTick;
   }

   public void onPlayerInput(boolean sprintSneakFlag, boolean sprintSneakFlag2, boolean sprintSneakFlag3, boolean sprintSneakFlag4, boolean sprintSneakFlag5, boolean sneak2, boolean sprint2) {
      this.sneak = sneak2;
      this.sprint = sprint2;
   }

   public OutboundPacketKind getFirstInteractType() {
      return this.firstInteractType;
   }

   public void markContainerClosed() {
      this.containerScreenActive = false;
   }

   public void recordForcedInteract(OutboundPacketKind sprintInteractType) {
      if (sprintInteractType != null && sprintInteractType.isInteractionPacket()) {
         this.addInteract(sprintInteractType, true);
         this.forcedInteractRecorded = true;
      }
   }

   public void onTickEnd() {
      this.advanceTick();
      this.tickEndSeen = true;
   }

   private void advanceTick() {
      this.tickCounter++;
      this.resetTickState();
   }

   public void recordSentLook(float amount, float amount2) {
      this.hasSentLook = true;
      this.lastSentYaw = amount;
      this.lastSentPitch = amount2;
   }

   public boolean isSneak() {
      return this.sneak;
   }

   public boolean hasFlaggedPacket() {
      return this.flaggedPacketSeen;
   }

   public void onClientTime(long time) {
      if (!this.hasClientTime || time != this.lastClientTime) {
         if (!this.tickEndSeen) {
            this.advanceTick();
         }

         this.tickEndSeen = false;
         this.hasClientTime = true;
         this.lastClientTime = time;
      }
   }

   public void reset() {
      this.resetTickState();
      this.hasClientTime = false;
      this.lastClientTime = 0L;
      this.tickEndSeen = false;
      this.selectedSlot = -1;
      this.hasSentLook = false;
      this.lastSentYaw = 0.0F;
      this.lastSentPitch = 0.0F;
      this.sneak = false;
      this.sprint = false;
      this.lastHandswapInteractTick = -1L;
      this.containerScreenActive = false;
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public boolean hasInteractThisTick() {
      return this.interactCount > 0;
   }

   public void recordInteractPacket(OutboundPacketKind sprintInteractType, boolean sprintSneakFlag, boolean sprintSneakFlag2) {
      if (sprintSneakFlag2) {
         this.flaggedPacketSeen = true;
      }

      if (sprintInteractType != null && sprintInteractType.isInteractionPacket()) {
         this.addInteract(sprintInteractType, sprintSneakFlag);
      }
   }

   private void addInteract(OutboundPacketKind sprintInteractType, boolean interactSprintFlag) {
      this.interactCount++;
      if (this.firstInteractType == null) {
         this.firstInteractType = sprintInteractType;
      }

      if (interactSprintFlag) {
         this.interactDuringHandswap = true;
         this.lastHandswapInteractTick = this.tickCounter;
      } else {
         this.interactOutsideHandswap = true;
      }
   }

   public boolean hasInteractOutsideHandswap() {
      return this.interactOutsideHandswap;
   }

   public boolean hasForcedInteract() {
      return this.forcedInteractRecorded;
   }

   public boolean hasNoInteractThisTick() {
      return this.interactCount == 0;
   }

   public boolean hasUnmanagedSlotChange() {
      return this.unmanagedSlotChange;
   }

   public boolean hasNoInteractOrSlotChange() {
      return this.interactCount == 0 && this.slotChangeCount == 0;
   }
}
