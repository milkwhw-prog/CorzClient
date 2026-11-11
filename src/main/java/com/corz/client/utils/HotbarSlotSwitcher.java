package com.corz.client.utils;

import com.corz.client.inventory.InventorySelectedUtils;
import com.corz.client.inventory.SelectedInventoryUtils;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.InventoryPlayerUtils;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.HotbarSlotRules;

public final class HotbarSlotSwitcher {
   private boolean requestedSlotForced;
   private long lastSelectionTime;
   private final TickActionLedger sprintSneak;
   private int requestedSlotIndex;
   private final HandswapGate ledgerHandswap;
   private boolean restoreRequested;
   private final HotbarSlotSwitcher.SlotMemory slotMemory = new HotbarSlotSwitcher.SlotMemory();

   public HotbarSlotSwitcher(TickActionLedger sprintSneak, HandswapGate ledgerHandswap) {
      this.requestedSlotIndex = -1;
      this.lastSelectionTime = Long.MIN_VALUE;
      this.sprintSneak = sprintSneak;
      this.ledgerHandswap = ledgerHandswap;
   }

   public boolean trySelectSlot(int ledgerHandswapLimit, long ledgerHandswapValue) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && ledgerHandswapLimit >= 0 && ledgerHandswapLimit < 9) {
         int selectedSlot = player.getInventory().getSelectedSlot();
         if (selectedSlot == ledgerHandswapLimit) {
            return this.ledgerHandswap.isSlotSelected(ledgerHandswapLimit);
         } else {
            this.slotMemory.invalidateIfStale(selectedSlot, isInventoryActionActive());
            if (!this.performSlotSwitch(ledgerHandswapLimit, ledgerHandswapValue, true, false)) {
               return false;
            } else {
               this.clearRequestedSlot();
               return this.ledgerHandswap.isSlotSelected(ledgerHandswapLimit);
            }
         }
      } else {
         return false;
      }
   }

   public void clearRequest() {
      this.requestedSlotIndex = -1;
      this.requestedSlotForced = false;
      this.lastSelectionTime = Long.MIN_VALUE;
      this.restoreRequested = false;
   }

   public long getLastSelectionTime() {
      return this.lastSelectionTime;
   }

   public void queueSlotRequest(int value, boolean flag) {
      if (value >= 0 && value < 9) {
         if (this.requestedSlotIndex < 0 || !this.requestedSlotForced || flag) {
            this.requestedSlotIndex = value;
            this.requestedSlotForced = flag;
         }
      }
   }

   public boolean requestSlot(int ledgerHandswapLimit, boolean ledgerHandswapFlag) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && ledgerHandswapLimit >= 0 && ledgerHandswapLimit < 9) {
         if (player.getInventory().getSelectedSlot() == ledgerHandswapLimit) {
            return this.ledgerHandswap.isSlotSelected(ledgerHandswapLimit);
         } else {
            this.queueSlotRequest(ledgerHandswapLimit, ledgerHandswapFlag);
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean performSlotSwitch(int selectedSlotLimit, long time, boolean selectedSlotFlag, boolean selectedSlotFlag2) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && selectedSlotLimit >= 0 && selectedSlotLimit < 9) {
         int selectedSlot = player.getInventory().getSelectedSlot();
         if (selectedSlotLimit == selectedSlot) {
            return false;
         } else if (AutoInventoryTotem.isSlotInPendingSwap(selectedSlotLimit)) {
            return false;
         } else if (!this.sprintSneak.hasInteractThisTick() && this.ledgerHandswap.isRestoreBlockerClear() && HotbarSlotRules.canSwitchSlotAt(time, this.lastSelectionTime, selectedSlotFlag)) {
            this.slotMemory.saveSlot(selectedSlot);

            try (HandswapGate.SwitchHandle handler = this.ledgerHandswap.acquireHandle()) {
               InventorySelectedUtils.handleSelectedSlot(selectedSlotLimit);
            }

            this.slotMemory.recordSwitchedSlot(selectedSlotLimit, selectedSlotFlag2);
            this.lastSelectionTime = time;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void resetRestoreRequest() {
      if (InventoryPlayerUtils.isLivingSelected()) {
         InventoryPlayerUtils.updateTimeSelected();
      }

      this.restoreRequested = false;
   }

   public void flushPendingSlotRequest(long selectedSlotValue) {
      boolean restoreRequested2 = this.restoreRequested;
      this.restoreRequested = false;
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         this.clearRequestedSlot();
      } else {
         int selectedSlot = player.getInventory().getSelectedSlot();
         this.slotMemory.invalidateIfStale(selectedSlot, isInventoryActionActive());
         if (this.requestedSlotIndex < 0 && restoreRequested2) {
            int value = this.slotMemory.getSavedSlotOrClear(selectedSlot);
            if (value >= 0) {
               this.requestedSlotIndex = value;
               this.requestedSlotForced = false;
            }
         }

         if (this.requestedSlotIndex >= 0) {
            int selectedSlotLimit = this.requestedSlotIndex;
            boolean selectedSlotFlag = this.requestedSlotForced;
            this.clearRequestedSlot();
            if (selectedSlotLimit != selectedSlot) {
               this.performSlotSwitch(selectedSlotLimit, selectedSlotValue, selectedSlotFlag, restoreRequested2);
            }
         }
      }
   }

   public void syncSelectedSlot() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         this.sprintSneak.setSelectedSlot(player.getInventory().getSelectedSlot());
      }
   }

   public int getRequestedSlotIndex() {
      return this.requestedSlotIndex;
   }

   public void resetAll() {
      this.clearRequest();
      this.slotMemory.clear();
   }

   private void clearRequestedSlot() {
      this.requestedSlotIndex = -1;
      this.requestedSlotForced = false;
   }

   private static boolean isInventoryActionActive() {
      return AutoInventoryTotem.isHandSwapSlotActive() || SelectedInventoryUtils.isSelectedSlot() || InventoryPlayerUtils.isLivingSelected();
   }

   static boolean shouldInvalidateMemory(boolean inventorySelectedFlag, int inventorySelectedLimit, int inventorySelectedValue, boolean selectedFlag) {
      return inventorySelectedFlag && inventorySelectedLimit >= 0 && !selectedFlag && inventorySelectedValue != inventorySelectedLimit;
   }

   public void rememberCurrentSlot() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         this.slotMemory.invalidateIfStale(player.getInventory().getSelectedSlot(), isInventoryActionActive());
      }

      if (this.slotMemory.isSlotSaved()) {
         this.restoreRequested = true;
      }
   }

   public int getRememberedSlot() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      return player == null ? -1 : this.slotMemory.getSavedSlotIfValid(player.getInventory().getSelectedSlot(), isInventoryActionActive());
   }

   public int findSlotMatching(Predicate<ItemStack> predicate, boolean selectedSlotFlag) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && predicate != null) {
         PlayerInventory inventory = player.getInventory();
         boolean[] booleans = new boolean[9];
         boolean[] booleans2 = new boolean[9];
         boolean[] selectedShieldFlags = new boolean[9];

         for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStack(i);
            booleans[i] = stack != null && predicate.test(stack);
            booleans2[i] = AutoInventoryTotem.wasTotemSlot(i);
            selectedShieldFlags[i] = AutoInventoryTotem.isSlotInPendingSwap(i);
         }

         return HotbarSlotRules.findAvailableSlot(booleans, inventory.getSelectedSlot(), AutoInventoryTotem.getReserveSlotIfEnabled(), booleans2, selectedShieldFlags, selectedSlotFlag);
      } else {
         return -1;
      }
   }

   static final class SlotMemory {
      private int savedSlot = -1;
      private int lastSwitchedToSlot = -1;
      private boolean slotSaved;

      int getSavedSlot() {
         return this.slotSaved ? this.savedSlot : -1;
      }

      void saveSlot(int selectedSlot) {
         if (!this.slotSaved && selectedSlot >= 0) {
            this.savedSlot = selectedSlot;
            this.slotSaved = true;
         }
      }

      void clear() {
         this.savedSlot = -1;
         this.slotSaved = false;
         this.lastSwitchedToSlot = -1;
      }

      int getSavedSlotIfValid(int selectedValue, boolean selectedFlag) {
         return this.slotSaved && !HotbarSlotSwitcher.shouldInvalidateMemory(this.slotSaved, this.lastSwitchedToSlot, selectedValue, selectedFlag) ? this.savedSlot : -1;
      }

      int getSavedSlotOrClear(int selectedSlotValue) {
         if (!this.slotSaved) {
            return -1;
         } else if (this.savedSlot >= 0 && this.savedSlot != selectedSlotValue) {
            return this.savedSlot;
         } else {
            this.clear();
            return -1;
         }
      }

      boolean isSlotSaved() {
         return this.slotSaved;
      }

      void invalidateIfStale(int userMemoryValue, boolean userMemoryFlag) {
         if (HotbarSlotSwitcher.shouldInvalidateMemory(this.slotSaved, this.lastSwitchedToSlot, userMemoryValue, userMemoryFlag)) {
            this.clear();
         }
      }

      void recordSwitchedSlot(int value, boolean selectedSlotFlag) {
         this.lastSwitchedToSlot = value;
         if (selectedSlotFlag && this.slotSaved && value == this.savedSlot) {
            this.clear();
         }
      }
   }
}
