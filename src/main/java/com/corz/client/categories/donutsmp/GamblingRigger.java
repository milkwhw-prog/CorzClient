package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class GamblingRigger extends Module {
   private int syncId;
   private final int[] destinationSlots;
   private final ItemStack[] savedGridStacks;
   private int delayTicks;
   private int keptSlotIndex;
   private final BooleanSetting keepPickedSlot;
   private GamblingRigger.Phase phase;
   private final IntegerSetting clickDelay = new IntegerSetting(
      "Click Delay", "Ticks between each moved stack. 0 = instant; raise it only if a server flags rapid container clicks.", 0, 0, 10
   );
   private final Deque<int[]> pendingMoves;
   private final BooleanSetting chatFeedback;
   private static final int RIGGER_MIN_SLOTS = 1;

   public void startRestore() {
      if (this.isExtractedPhase()) {
         ClientPlayerEntity player = mc.player;
         if (player != null && player.currentScreenHandler instanceof Generic3x3ContainerScreenHandler generic3x3ContainerScreenHan && generic3x3ContainerScreenHan.syncId == this.syncId) {
            this.pendingMoves.clear();
            this.delayTicks = 0;
            boolean[] booleans = new boolean[45];
            int chatSyncCounter = 0;

            for (int i = 0; i < 9; i++) {
               if (i != this.keptSlotIndex) {
                  ItemStack itemStack = this.savedGridStacks[i];
                  if (itemStack != null && !itemStack.isEmpty()) {
                     int equalSlot = this.findRestoreSlot(generic3x3ContainerScreenHan, itemStack, i, booleans);
                     if (equalSlot < 0) {
                        chatSyncCounter++;
                     } else {
                        booleans[equalSlot] = true;
                        this.pendingMoves.add(new int[]{0, equalSlot, i});
                     }
                  }
               }
            }

            this.phase = GamblingRigger.Phase.RESTORING;
            if (chatSyncCounter > 0) {
               this.sendChatFeedback(
                  "\u00a7eGamblingRigger \u00a77\u00bb restoring, but \u00a7c" + chatSyncCounter + " \u00a77stack(s) were not found in your inventory"
               );
            }
         } else {
            this.abortAndReset(true);
         }
      }
   }

   private void resetState() {
      this.phase = GamblingRigger.Phase.IDLE;
      this.keptSlotIndex = -1;
      this.syncId = -1;
      this.pendingMoves.clear();
      this.delayTicks = 0;

      for (int i = 0; i < 9; i++) {
         this.savedGridStacks[i] = null;
         this.destinationSlots[i] = -1;
      }
   }

   private void clickSlot(PlayerEntity player, int clickSlotValue, int clickSlotValue2, SlotActionType slotActionType) {
      mc.interactionManager.clickSlot(this.syncId, clickSlotValue, clickSlotValue2, slotActionType, player);
   }

   public void startExtraction(int riggerChatValue) {
      if (this.isPhaseIdle() && riggerChatValue >= 0 && riggerChatValue < 9) {
         ClientPlayerEntity player = mc.player;
         if (player != null && player.currentScreenHandler instanceof Generic3x3ContainerScreenHandler generic3x3ContainerScreenHan) {
            this.syncId = generic3x3ContainerScreenHan.syncId;
            this.keptSlotIndex = riggerChatValue;
            this.pendingMoves.clear();
            this.delayTicks = 0;
            boolean[] booleans = new boolean[45];
            int riggerChatCounter = 0;

            for (int i = 0; i < 9; i++) {
               this.savedGridStacks[i] = generic3x3ContainerScreenHan.getSlot(i).getStack().copy();
               this.destinationSlots[i] = -1;
            }

            for (int j = 0; j < 9; j++) {
               if ((!this.keepPickedSlot.isEnabled() || j != this.keptSlotIndex) && !this.savedGridStacks[j].isEmpty()) {
                  int slotStack = this.findFreeInventorySlot(generic3x3ContainerScreenHan, booleans);
                  if (slotStack >= 0) {
                     booleans[slotStack] = true;
                     this.destinationSlots[j] = slotStack;
                     this.pendingMoves.add(new int[]{0, j, slotStack});
                  } else {
                     riggerChatCounter++;
                     this.pendingMoves.add(new int[]{1, j, 0});
                  }
               }
            }

            if (this.pendingMoves.isEmpty()) {
               this.phase = GamblingRigger.Phase.EXTRACTED;
               this.sendChatFeedback("\u00a7dGamblingRigger \u00a77\u00bb grid empty \u2014 nothing to take, click \u00a7fRestore \u00a77when done");
            } else {
               this.phase = GamblingRigger.Phase.EXTRACTING;
               if (riggerChatCounter > 0) {
                  this.sendChatFeedback(
                     "\u00a7eGamblingRigger \u00a77\u00bb your inventory is nearly full \u2014 "
                        + riggerChatCounter
                        + " stack(s) fall back to shift-click and may not restore exactly"
                  );
               }
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.abortAndReset(false);
   }

   public boolean isExtractedPhase() {
      return this.phase == GamblingRigger.Phase.EXTRACTED && this.isDispenserScreenOpen();
   }

   @Override
   protected void onReset() {
      if (this.pendingMoves.isEmpty()) {
         this.finishPhaseIfDone();
      } else {
         ClientPlayerEntity player = mc.player;
         if (player != null
            && mc.interactionManager != null
            && player.currentScreenHandler instanceof Generic3x3ContainerScreenHandler
            && player.currentScreenHandler.syncId == this.syncId) {
            boolean iIiFlag = this.clickDelay.getValue() <= 0;

            while (!this.pendingMoves.isEmpty()) {
               if (this.delayTicks > 0) {
                  this.delayTicks--;
                  return;
               }

               this.performMove(player, this.pendingMoves.poll());
               this.delayTicks = this.clickDelay.getValue();
               if (!iIiFlag) {
                  return;
               }
            }

            this.finishPhaseIfDone();
         } else {
            this.abortAndReset(true);
         }
      }
   }

   public boolean isPhaseIdle() {
      return this.phase == GamblingRigger.Phase.IDLE && this.isDispenserScreenOpen();
   }

   public void resetWithNotice() {
      boolean riggerGamblingFlag = this.phase != GamblingRigger.Phase.IDLE || this.keptSlotIndex >= 0;
      this.resetState();
      if (riggerGamblingFlag) {
         this.sendChatFeedback("\u00a77GamblingRigger \u00bb reset");
      }
   }

   private int findRestoreSlot(ScreenHandler screenHandler, ItemStack stack, int index, boolean[] equalSlotFlags) {
      int index2 = this.destinationSlots[index];
      if (index2 >= 9 && index2 < 45 && !equalSlotFlags[index2] && ItemStack.areEqual(screenHandler.getSlot(index2).getStack(), stack)) {
         return index2;
      } else {
         for (int i = 9; i < 45; i++) {
            if (!equalSlotFlags[i] && ItemStack.areEqual(screenHandler.getSlot(i).getStack(), stack)) {
               return i;
            }
         }

         for (int k = 9; k < 45; k++) {
            ItemStack stack2 = screenHandler.getSlot(k).getStack();
            if (!equalSlotFlags[k] && !stack2.isEmpty() && ItemStack.areItemsAndComponentsEqual(stack2, stack)) {
               return k;
            }
         }

         return -1;
      }
   }

   private boolean isDispenserScreenOpen() {
      return mc.player != null && mc.player.currentScreenHandler instanceof Generic3x3ContainerScreenHandler;
   }

   private void sendChatFeedback(String messageLiteralText) {
      if (this.chatFeedback.isEnabled() && mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   private void performMove(PlayerEntity player, int[] pickupCursorValues) {
      ScreenHandler currentScreenHandler = player.currentScreenHandler;
      if (pickupCursorValues[0] == 1) {
         this.clickSlot(player, pickupCursorValues[1], 0, SlotActionType.QUICK_MOVE);
      } else {
         this.clickSlot(player, pickupCursorValues[1], 0, SlotActionType.PICKUP);
         if (!currentScreenHandler.getCursorStack().isEmpty()) {
            this.clickSlot(player, pickupCursorValues[2], 0, SlotActionType.PICKUP);
         }

         if (!currentScreenHandler.getCursorStack().isEmpty()) {
            this.clickSlot(player, pickupCursorValues[1], 0, SlotActionType.PICKUP);
         }
      }
   }

   public int getKeptSlotIndex() {
      return this.keptSlotIndex;
   }

   public int getPendingMoveCount() {
      return this.pendingMoves.size();
   }

   private void abortAndReset(boolean gamblingRiggerFlag) {
      boolean gamblingRiggerFlag2 = this.phase != GamblingRigger.Phase.IDLE;
      this.resetState();
      if (gamblingRiggerFlag && gamblingRiggerFlag2) {
         this.sendChatFeedback("\u00a7cGamblingRigger \u00a77\u00bb aborted (the container closed or changed)");
      }
   }

   private int findFreeInventorySlot(ScreenHandler screenHandler, boolean[] slotStackFlags) {
      for (int i = 9; i < 45; i++) {
         if (!slotStackFlags[i] && screenHandler.getSlot(i).getStack().isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   private void finishPhaseIfDone() {
      if (this.pendingMoves.isEmpty()) {
         if (this.phase == GamblingRigger.Phase.EXTRACTING) {
            this.phase = GamblingRigger.Phase.EXTRACTED;
            this.sendChatFeedback(
               "\u00a7aGamblingRigger \u00a77\u00bb pulled the grid (kept \u00a7f#"
                  + (this.keptSlotIndex + 1)
                  + "\u00a77) \u2014 click \u00a7fRestore \u00a77when ready"
            );
         } else if (this.phase == GamblingRigger.Phase.RESTORING) {
            this.sendChatFeedback(
               "\u00a7aGamblingRigger \u00a77\u00bb layout restored \u2014 slot \u00a7f#" + (this.keptSlotIndex + 1) + " \u00a77left open"
            );
            this.resetState();
         }
      }
   }

   public GamblingRigger.Phase getPhase() {
      return this.phase;
   }

   public GamblingRigger() {
      super("GamblingRigger", IconsPngType.DONUTSMP);
      this.keepPickedSlot = new BooleanSetting(
         "Keep Slot Stays", "Leave the picked slot untouched in the container (someone else refills it). Off: pull it out too.", true
      );
      this.chatFeedback = new BooleanSetting("Chat Feedback", "Print what the rigger is doing to chat.", true);
      this.phase = GamblingRigger.Phase.IDLE;
      this.keptSlotIndex = -1;
      this.syncId = -1;
      this.savedGridStacks = new ItemStack[9];
      this.destinationSlots = new int[9];
      this.pendingMoves = new ArrayDeque<>();
      this.delayTicks = 0;
      this.addSettings(new ConfigSetting[]{this.clickDelay, this.keepPickedSlot, this.chatFeedback});
   }

   public static enum Phase {
      IDLE,
      EXTRACTING,
      EXTRACTED,
      RESTORING;
   }
}
