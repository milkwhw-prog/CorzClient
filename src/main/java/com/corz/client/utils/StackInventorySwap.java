package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.WaitInventoryUtils;
import com.corz.client.utils.InventoryStack;
import com.corz.client.utils.InventorySlotSelector;

public final class StackInventorySwap {
   private final MinecraftClient instanceStack;
   public int stackUpdateCount;
   private boolean absentActive;
   private int swapWaitCount;
   private boolean swapWaitActive;
   private int damageableSelected;
   private final Object swapWaitObject = new Object();
   private WaitInventoryUtils.Fail timeoutUnsafeType;
   private Item targetItem;
   private int absentCount;
   private boolean stackInventoryActive;
   private Item stackItem;
   public int stackCount;
   private WaitInventoryUtils.Phase waitSwapType = WaitInventoryUtils.Phase.IDLE;
   private ItemStack copy;
   private final InventoryStack absentInventoryStack;
   public int stackInventoryCount;
   private ItemStack copyStack;
   private int inventoryStack;
   private int inventoryStackStack = -1;

   private int getDamageableSelected(Set<Integer> set, boolean damageableSelectedFlag, Item item, Item item2) {
      PlayerInventory inventory = this.instanceStack.player.getInventory();
      int selectedSlot = inventory.getSelectedSlot();
      ArrayList arrayList = new ArrayList(9);

      for (int i = 0; i <= 8; i++) {
         ItemStack stack = inventory.getStack(i);
         boolean varEmpty = stack.isEmpty();
         boolean damageableSelectedFlag2 = !varEmpty && (stack.isDamageable() || stack.getMaxCount() == 1);
         boolean damageableSelectedFlag3 = !varEmpty && item != null && stack.isOf(item);
         boolean damageableSelectedFlag4 = !varEmpty && item2 != null && stack.isOf(item2);
         boolean damageableSelectedFlag5 = damageableSelectedFlag && i == selectedSlot;
         boolean damageableSelectedFlag6 = set != null && set.contains(i);
         arrayList.add(new InventorySlotSelector.Slot(i, varEmpty, damageableSelectedFlag6, damageableSelectedFlag5, damageableSelectedFlag2, damageableSelectedFlag4, damageableSelectedFlag3, varEmpty ? 0 : stack.getCount(), 0, 0));
      }

      return InventorySlotSelector.chooseSlotIndex(arrayList);
   }

   public int getInventoryStackStack() {
      return this.inventoryStackStack;
   }

   private void updateScreenInstance() {
      if (this.swapWaitActive && this.instanceStack.currentScreen instanceof InventoryScreen && this.instanceStack.player != null) {
         this.instanceStack.player.closeHandledScreen();
      }
   }

   public StackInventorySwap.Begin getTransactionBusyType(Item item, Set<Integer> set, boolean transactionBusyFlag, Item requiredItem, Item item3) {
      return this.getInstanceAbsent(item, set, transactionBusyFlag, requiredItem, item3, false);
   }

   public StackInventorySwap.Begin getInstanceAbsent(
      Item item, Set<Integer> set, boolean absentFlag, Item requiredItem, Item item3, boolean flag
   ) {
      if (this.instanceStack.player == null) {
         return StackInventorySwap.Begin.ITEM_ABSENT;
      } else if (this.absentInventoryStack.isUnchanged(item, this.getCodeInstance())) {
         this.stackCount++;
         return StackInventorySwap.Begin.TRANSACTION_REJECTED;
      } else {
         this.targetItem = item;
         this.inventoryStackStack = this.getInventoryStack(item);
         if (this.inventoryStackStack < 0) {
            return StackInventorySwap.Begin.ITEM_ABSENT;
         } else {
            this.damageableSelected = this.getDamageableSelected(set, absentFlag, requiredItem, item3);
            if (this.damageableSelected < 0) {
               return StackInventorySwap.Begin.NO_SLOT;
            } else if (!InventoryStackUtilsHandle.canFlagForObject(this.swapWaitObject)) {
               return StackInventorySwap.Begin.BUSY;
            } else {
               this.swapWaitActive = flag;
               this.swapWaitCount = 4;
               if (flag && this.instanceStack.currentScreen == null) {
                  this.instanceStack.setScreen(new InventoryScreen(this.instanceStack.player));
               }

               this.waitSwapType = WaitInventoryUtils.Phase.WAIT_SAFE;
               this.absentCount = 0;
               this.stackInventoryActive = false;
               this.absentActive = false;
               this.timeoutUnsafeType = WaitInventoryUtils.Fail.NONE;
               return StackInventorySwap.Begin.STARTED;
            }
         }
      }
   }

   public boolean canFlag(Item obj) {
      return this.absentInventoryStack.isUnchanged(obj, this.getCodeInstance());
   }

   private int getInventoryStack() {
      if (this.instanceStack.player != null && this.targetItem != null) {
         int inventoryStack = 0;

         for (int i = 0; i < 36; i++) {
            if (this.instanceStack.player.getInventory().getStack(i).isOf(this.targetItem)) {
               inventoryStack += this.instanceStack.player.getInventory().getStack(i).getCount();
            }
         }

         return inventoryStack;
      } else {
         return 0;
      }
   }

   public WaitInventoryUtils.Fail getTimeoutUnsafeType() {
      return this.timeoutUnsafeType;
   }

   public long getCodeInstance() {
      if (this.instanceStack.player == null) {
         return 0L;
      } else {
         long codeInstance = 1L;

         for (int i = 0; i < 36; i++) {
            ItemStack stack = this.instanceStack.player.getInventory().getStack(i);
            long codeValue = 31L * codeInstance + ItemStack.hashCode(stack);
            codeInstance = 31L * codeValue + stack.getCount();
         }

         return codeInstance;
      }
   }

   public void update() {
      this.updateSwapWait();
      this.absentInventoryStack.reset();
      this.stackInventoryCount = this.stackCount = this.stackUpdateCount = 0;
      this.stackItem = null;
   }

   public Item getItem() {
      return this.targetItem;
   }

   private void updateStackInstance() {
      if (!this.instanceStack.player.getInventory().getStack(this.inventoryStackStack).isOf(this.targetItem)) {
         int value = this.getInventoryStack(this.targetItem);
         if (value < 0) {
            this.waitSwapType = WaitInventoryUtils.Phase.FAILED;
            this.timeoutUnsafeType = WaitInventoryUtils.Fail.SYNC_TIMEOUT;
            return;
         }

         this.inventoryStackStack = value;
      }

      if (!this.absentInventoryStack.markIfChanged(this.targetItem, this.getCodeInstance())) {
         this.stackCount++;
         this.waitSwapType = WaitInventoryUtils.Phase.FAILED;
         this.timeoutUnsafeType = WaitInventoryUtils.Fail.SYNC_TIMEOUT;
      } else {
         this.copy = this.instanceStack.player.getInventory().getStack(this.inventoryStackStack).copy();
         this.copyStack = this.instanceStack.player.getInventory().getStack(this.damageableSelected).copy();
         this.inventoryStack = this.getInventoryStack();
         if (this.stackItem == this.targetItem) {
            this.stackUpdateCount++;
         }

         this.stackItem = null;
         this.stackInventoryCount++;
         this.absentActive = true;
         if (this.instanceStack.player.getAbilities().creativeMode) {
            PlayerInventory inventory = this.instanceStack.player.getInventory();
            inventory.setStack(this.inventoryStackStack, this.copyStack.copy());
            inventory.setStack(this.damageableSelected, this.copy.copy());
            this.instanceStack.interactionManager.clickCreativeStack(this.copyStack.copy(), HotbarSlotUtils.toScreenSlot(this.inventoryStackStack));
            this.instanceStack.interactionManager.clickCreativeStack(this.copy.copy(), HotbarSlotUtils.toScreenSlot(this.damageableSelected));
         } else {
            this.instanceStack
               .interactionManager
               .clickSlot(
                  this.instanceStack.player.playerScreenHandler.syncId,
                  HotbarSlotUtils.toScreenSlot(this.inventoryStackStack),
                  HotbarSlotUtils.requireHotbarSlot(this.damageableSelected),
                  SlotActionType.SWAP,
                  this.instanceStack.player
               );
         }
      }
   }

   public StackInventorySwap.Status getSwapWait(
      boolean swapWaitFlag, boolean falling, boolean swapWaitFlag3, boolean swapWaitFlag4, boolean swapWaitFlag5, boolean swapWaitFlag6
   ) {
      if (this.waitSwapType == WaitInventoryUtils.Phase.DONE) {
         if (this.swapWaitActive && this.swapWaitCount > 0) {
            this.swapWaitCount--;
            return StackInventorySwap.Status.RUNNING;
         } else {
            this.updateScreenInstance();
            InventoryStackUtilsHandle.handle(this.swapWaitObject);
            return StackInventorySwap.Status.DONE;
         }
      } else if (!this.isWaitSwapType()) {
         return StackInventorySwap.Status.FAILED;
      } else if (this.instanceStack.player != null && this.instanceStack.interactionManager != null) {
         boolean swapWaitFlag7 = this.instanceStack.currentScreen != null && !this.isScreenInstance()
            || this.instanceStack.player.currentScreenHandler != this.instanceStack.player.playerScreenHandler;
         boolean canFlag8 = WaitInventoryUtils.canFlag(swapWaitFlag, falling, swapWaitFlag3, swapWaitFlag4, swapWaitFlag5, swapWaitFlag6, swapWaitFlag7);
         boolean swapWaitFlag9 = this.absentActive
            && this.damageableSelected >= 0
            && this.absentInventoryStack
               .updateStableStreak(
                  ItemStack.areEqual(this.instanceStack.player.getInventory().getStack(this.damageableSelected), this.copy),
                  ItemStack.areEqual(this.instanceStack.player.getInventory().getStack(this.inventoryStackStack), this.copyStack),
                  this.isFlag()
               );
         boolean swapWait = this.isSwapWait();
         WaitInventoryUtils.Out waitSafe = WaitInventoryUtils.getWaitSafe(this.waitSwapType, new WaitInventoryUtils.In(canFlag8, swapWaitFlag9, swapWait));
         if (waitSafe.phase() != this.waitSwapType) {
            this.waitSwapType = waitSafe.phase();
            this.absentCount = 0;
            this.stackInventoryActive = false;
            if (this.waitSwapType == WaitInventoryUtils.Phase.FAILED) {
               this.timeoutUnsafeType = waitSafe.fail();
            }
         }

         if (!this.stackInventoryActive && this.waitSwapType == WaitInventoryUtils.Phase.SWAP) {
            this.updateStackInstance();
            this.stackInventoryActive = true;
         }

         this.absentCount++;
         if (this.waitSwapType == WaitInventoryUtils.Phase.DONE) {
            if (this.swapWaitActive && this.swapWaitCount > 0) {
               this.swapWaitCount--;
               return StackInventorySwap.Status.RUNNING;
            } else {
               this.updateScreenInstance();
               InventoryStackUtilsHandle.handle(this.swapWaitObject);
               return StackInventorySwap.Status.DONE;
            }
         } else {
            return this.waitSwapType == WaitInventoryUtils.Phase.FAILED ? this.getSwapWait() : StackInventorySwap.Status.RUNNING;
         }
      } else {
         return this.getSwapWait();
      }
   }

   public int getValueDamageableSelected() {
      return this.damageableSelected;
   }

   private boolean isSwapWait() {
      return switch (this.waitSwapType) {
         case WAIT_SAFE -> this.absentCount > 60;
         case WAIT_SYNC -> this.absentCount > 20;
         default -> false;
      };
   }

   public void updateSwapWait() {
      this.updateScreenInstance();
      InventoryStackUtilsHandle.handle(this.swapWaitObject);
      this.waitSwapType = WaitInventoryUtils.Phase.IDLE;
      this.targetItem = null;
      this.inventoryStackStack = this.damageableSelected = -1;
      this.stackInventoryActive = false;
      this.swapWaitActive = false;
   }

   public boolean isScreenInstance() {
      return this.swapWaitActive && this.instanceStack.currentScreen instanceof InventoryScreen;
   }

   private int getInventoryStack(Item item2) {
      PlayerInventory inventory = this.instanceStack.player.getInventory();

      for (int i = 9; i <= 35; i++) {
         if (inventory.getStack(i).isOf(item2)) {
            return i;
         }
      }

      return -1;
   }

   public boolean isFlag() {
      return this.getInventoryStack() == this.inventoryStack;
   }

   public boolean isWaitSwapType() {
      return this.waitSwapType != WaitInventoryUtils.Phase.IDLE
         && this.waitSwapType != WaitInventoryUtils.Phase.DONE
         && this.waitSwapType != WaitInventoryUtils.Phase.FAILED;
   }

   private StackInventorySwap.Status getSwapWait() {
      this.stackItem = this.targetItem;
      this.updateScreenInstance();
      InventoryStackUtilsHandle.handle(this.swapWaitObject);
      if (this.waitSwapType != WaitInventoryUtils.Phase.FAILED) {
         this.waitSwapType = WaitInventoryUtils.Phase.FAILED;
      }

      return StackInventorySwap.Status.FAILED;
   }

   public StackInventorySwap(MinecraftClient instance) {
      this.damageableSelected = -1;
      this.absentInventoryStack = new InventoryStack();
      this.copy = ItemStack.EMPTY;
      this.copyStack = ItemStack.EMPTY;
      this.timeoutUnsafeType = WaitInventoryUtils.Fail.NONE;
      this.instanceStack = instance;
   }

   public static enum Begin {
      STARTED,
      ITEM_ABSENT,
      NO_SLOT,
      BUSY,
      TRANSACTION_REJECTED;
   }

   public static enum Status {
      RUNNING,
      DONE,
      FAILED;
   }
}
