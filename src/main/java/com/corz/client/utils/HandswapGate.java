package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.inventory.SelectedInventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.BotRunSupervisor;
import com.corz.client.utils.TickActionLedger;

public final class HandswapGate {
   private String blockReason;
   private int pendingHandswapTicks;
   private final TrackFlick trackFlick;
   private long pauseTime;
   private boolean teleportLookFlag;
   private static final String NO_REASON_TEXT = "-";
   private BotRunSupervisor.Level runLevel;
   private boolean blockOnLook;
   private long level;
   private final HandswapGate.SwitchHandle releaseHandle = this::decrementPendingTicks;
   private final TickActionLedger player;

   public String getBlockReason() {
      return this.blockReason;
   }

   private String computeSwitchBlocker() {
      if (this.runLevel != BotRunSupervisor.Level.RUN) {
         return "level";
      } else if (this.level >= this.pauseTime) {
         return "paused";
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null || instance.interactionManager == null) {
            return "player";
         } else if (!this.player.hasNoInteractThisTick()) {
            return "ledger";
         } else if (ClientThreadUtils.isEntityActive()) {
            return "attacked";
         } else if (AutoInventoryTotem.isHandSwapSlotActive()) {
            return "handswap";
         } else if (player.isUsingItem()) {
            return "using";
         } else if (instance.currentScreen != null) {
            return "screen";
         } else if (instance.getOverlay() != null) {
            return "overlay";
         } else if (instance.interactionManager.isBreakingBlock()) {
            return "breaking";
         } else if (ConnectionTimeUtils.Outbound.isFlag()) {
            return "held";
         } else if ((!ExtUtils.isAction() || ExtUtils.getActionValue() < 3) && !ExtUtils.isActionFlagged()) {
            if (player.getInventory().getSelectedSlot() != this.player.getSelectedSlot()) {
               return "slot";
            } else {
               return this.player.hasUnmanagedSlotChange() ? "slotchange" : null;
            }
         } else {
            return "invclick";
         }
      }
   }

   public void setTeleportLookFlag(boolean teleportLookFlag) {
      this.teleportLookFlag = teleportLookFlag;
   }

   public boolean isSlotSelected(int playerSelectedValue) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      return player == null ? false : player.getInventory().getSelectedSlot() == playerSelectedValue && this.player.getSelectedSlot() == playerSelectedValue && !this.player.hasUnmanagedSlotChange();
   }

   public void extendLevelTicks(int levelLimit) {
      if (levelLimit > 0) {
         long level = ClientThreadUtils.getTime() + levelLimit;
         if (level > this.level) {
            this.level = level;
         }
      }
   }

   public boolean isRotationSynced() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      return player != null && this.player.hasSentLookPacket()
         ? Math.abs(player.getYaw() - this.player.getLastSentYaw()) < 1.0E-4F
            && Math.abs(player.getPitch() - this.player.getLastSentPitch()) < 1.0E-4F
            && ConnectionTimeUtils.getTimeTimeMillis() - ConnectionTimeUtils.getTime() > 100L
         : false;
   }

   private String computeRestoreBlocker() {
      if (this.runLevel != BotRunSupervisor.Level.RUN) {
         return "level";
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null) {
            return "player";
         } else if (!this.player.hasNoInteractOrSlotChange()) {
            return "ledger";
         } else if (AutoInventoryTotem.isHandSwapSlotActive()) {
            return "handswap";
         } else if (player.isUsingItem()) {
            return "using";
         } else if (instance.currentScreen != null) {
            return "screen";
         } else if (instance.getOverlay() != null) {
            return "overlay";
         } else {
            return SelectedInventoryUtils.isSelectedSlot() ? "restore" : null;
         }
      }
   }

   public boolean isSwitchAllowedWithTeleport() {
      String formatBreakingSlotchange = this.computeSwitchBlocker();
      if (formatBreakingSlotchange == null && !this.trackFlick.didRotationMatchCommit()) {
         formatBreakingSlotchange = "rotation";
      }

      if (formatBreakingSlotchange == null && this.blockOnLook) {
         formatBreakingSlotchange = "look";
      }

      if (formatBreakingSlotchange == null && this.teleportLookFlag) {
         formatBreakingSlotchange = "teleport";
      }

      return this.recordBlocker(formatBreakingSlotchange);
   }

   public void reset(BotRunSupervisor.Level level2, boolean lookRotationFlag) {
      this.runLevel = level2 == null ? BotRunSupervisor.Level.PAUSED : level2;
      this.blockOnLook = lookRotationFlag;
      this.teleportLookFlag = false;
      this.pauseTime = ClientThreadUtils.getTime();
      this.pendingHandswapTicks = 0;
      this.blockReason = "-";
   }

   public HandswapGate(TickActionLedger player, TrackFlick trackFlick) {
      this.runLevel = BotRunSupervisor.Level.PAUSED;
      this.level = Long.MIN_VALUE;
      this.blockReason = "-";
      this.player = player;
      this.trackFlick = trackFlick;
   }

   public static boolean isWithinTimingWindow(int ledgerHandswapValue, long ledgerHandswapBound, int rocketsBuyBound) {
      long ledgerHandswapLimit = ledgerHandswapValue - Math.max(0L, ledgerHandswapBound);
      return ledgerHandswapLimit <= Math.max(0, rocketsBuyBound) + 1L && ledgerHandswapLimit >= -10L;
   }

   public boolean isSwitchBlockerClear() {
      return this.recordBlocker(this.computeSwitchBlocker());
   }

   public boolean isRestoreBlockerClear() {
      return this.recordBlocker(this.computeRestoreBlocker());
   }

   private void decrementPendingTicks() {
      if (this.pendingHandswapTicks > 0) {
         this.pendingHandswapTicks--;
      }
   }

   public boolean isHandswapPending() {
      return this.pendingHandswapTicks > 0;
   }

   private boolean recordBlocker(String ledgerHandswapText) {
      this.blockReason = ledgerHandswapText == null ? "-" : ledgerHandswapText;
      return ledgerHandswapText == null;
   }

   public boolean isSwitchAllowed() {
      String formatBreakingSlotchange = this.computeSwitchBlocker();
      if (formatBreakingSlotchange == null && !this.trackFlick.didRotationMatchCommit()) {
         formatBreakingSlotchange = "rotation";
      }

      if (formatBreakingSlotchange == null && this.blockOnLook) {
         formatBreakingSlotchange = "look";
      }

      return this.recordBlocker(formatBreakingSlotchange);
   }

   public HandswapGate.SwitchHandle acquireHandle() {
      this.pendingHandswapTicks++;
      return this.releaseHandle;
   }

   public interface SwitchHandle extends AutoCloseable {
      @Override
      void close();
   }
}
