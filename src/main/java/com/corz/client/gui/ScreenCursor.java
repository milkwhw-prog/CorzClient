package com.corz.client.gui;

import com.corz.client.inventory.DequeEmpty;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import com.corz.client.utils.ExtUtils;

public final class ScreenCursor {
   private boolean screenActive;
   private int inventoryScreenTicks;
   private final Object screenCursorObject;
   private final DequeEmpty screenDequeEmpty = new DequeEmpty();
   private long tickCounter;
   private int screenValue;
   private boolean screenCursorActive;
   private int screenCount;
   private long selectedValue;
   private int screenCursorCount;
   private int settleTicksLeft;
   private Object world;
   private Screen screen;
   private int screenPlayerCount = -1;
   private boolean updateFlagged;
   private long screenCursorValue = -4611686018427387904L;
   private int selectedSlot = -1;
   private boolean switchPending;
   private int previousSlot = -1;
   private int switchTickCount;

   public void handleScreenPlayer(MinecraftClient client) {
      if (client.player != null && client.currentScreen == null) {
         InventoryScreen screen = new InventoryScreen(client.player);
         client.setScreen(screen);
         if (client.currentScreen == screen) {
            this.screen = screen;
            this.screenPlayerCount = -1;
            this.inventoryScreenTicks = 0;
         }
      }
   }

   public void updateTime() {
      this.screenCursorValue = this.tickCounter;
   }

   public boolean isFlag() {
      boolean screenCursorFlag = this.screenActive;
      this.screenActive = false;
      return screenCursorFlag;
   }

   public void handleScreenCursor(MinecraftClient client) {
      Screen screen = this.screen;
      this.screen = null;
      this.screenPlayerCount = -1;
      if (screen != null && client.player != null && client.currentScreen == screen) {
         if (client.player.currentScreenHandler.getCursorStack().isEmpty()) {
            client.player.closeHandledScreen();
         }
      }
   }

   public void handleFlag(Runnable runnable, boolean screenCursorFlag, int screenCursorValue) {
      this.switchPending = true;
      this.screenCursorCount = 0;
      this.screenCursorActive = false;
      this.updateFlagged = false;
      this.screenValue = CursorScreenUtils.getValue(screenCursorValue, ThreadLocalRandom.current().nextDouble());
      ExtUtils.handleExt(() -> {
         this.screenCursorActive = true;
         runnable.run();
      }, screenCursorFlag);
   }

   public void updateFlagTime() {
      this.settleTicksLeft = 0;
      this.switchPending = false;
      this.screenCursorCount = 0;
      this.screenCursorActive = false;
      this.updateFlagged = false;
      this.screenValue = 0;
      this.screen = null;
      this.screenActive = false;
      this.screenPlayerCount = -1;
      this.inventoryScreenTicks = 0;
      this.screenCursorValue = -4611686018427387904L;
      this.switchTickCount = 0;
      this.world = null;
      this.screenCount = 0;
      this.selectedSlot = -1;
      this.previousSlot = -1;
      this.selectedValue = -4611686018427387904L;
      this.screenDequeEmpty.updateDequeArray();
      InventoryStackUtilsHandle.handle(this.screenCursorObject);
   }

   public int getSwitchTickCount() {
      return this.switchTickCount;
   }

   public boolean canScreenCursor(MinecraftClient client) {
      if (this.screen == null) {
         return true;
      } else if (client.player == null || client.currentScreen != this.screen) {
         this.screen = null;
         this.screenPlayerCount = -1;
         return true;
      } else if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
         return false;
      } else if (this.screenPlayerCount < 0) {
         this.screenPlayerCount = CursorScreenUtils.getValue(ThreadLocalRandom.current().nextDouble());
         return false;
      } else if (this.screenPlayerCount-- > 0) {
         return false;
      } else {
         this.handleScreenCursor(client);
         return true;
      }
   }

   public long getTime() {
      return this.tickCounter;
   }

   public void updateFlag() {
      this.updateFlagged = true;
   }

   public ScreenCursor(Object object) {
      this.selectedValue = -4611686018427387904L;
      this.screenCursorObject = object;
   }

   public boolean isInventoryScreenTicks() {
      return this.inventoryScreenTicks > 6;
   }

   public boolean canFlag(int screenCursorValue, ItemStack stack) {
      return this.screenDequeEmpty.canEmpty(screenCursorValue, stack);
   }

   public boolean isScreen() {
      return this.screen != null;
   }

   public void update() {
      this.screenPlayerCount = -1;
   }

   public DequeEmpty.Verdict getStopTime(int screenCursorValue, ItemStack stack) {
      DequeEmpty.Verdict dequeArray = this.screenDequeEmpty.getDequeArray(screenCursorValue, stack, this.tickCounter);
      if (dequeArray == DequeEmpty.Verdict.STOP) {
         this.screenCount = 200;
      }

      return dequeArray;
   }

   public boolean isTime() {
      return this.tickCounter - this.screenCursorValue < 20L;
   }

   public boolean canTimeScreen(MinecraftClient client) {
      if (client.world != this.world) {
         this.updateFlagTime();
         this.world = client.world;
      }

      this.tickCounter++;
      this.switchTickCount++;
      ClientPlayerEntity player = client.player;
      if (player.hurtTime > 0) {
         this.screenCursorValue = this.tickCounter;
      }

      int selectedSlot = player.getInventory().getSelectedSlot();
      if (selectedSlot != this.selectedSlot) {
         this.previousSlot = this.selectedSlot;
         this.selectedSlot = selectedSlot;
         this.selectedValue = this.tickCounter;
      }

      this.screenDequeEmpty.handleTime(this.tickCounter);
      this.inventoryScreenTicks = client.currentScreen instanceof InventoryScreen ? this.inventoryScreenTicks + 1 : 0;
      if (this.screen != null && client.currentScreen != this.screen) {
         this.screen = null;
         this.screenPlayerCount = -1;
         this.screenActive = true;
      }

      if (this.switchPending) {
         if (this.screenCursorActive) {
            this.switchPending = false;
            if (this.updateFlagged) {
               this.switchTickCount = 0;
            }

            this.settleTicksLeft = this.updateFlagged ? Math.max(0, this.screenValue - 1) : 2;
         } else {
            if (ExtUtils.isAction() && ++this.screenCursorCount <= 18) {
               return false;
            }

            this.switchPending = false;
            this.settleTicksLeft = 2;
         }
      }

      if (this.screenCount > 0) {
         this.screenCount--;
         return false;
      } else if (this.settleTicksLeft > 0) {
         this.settleTicksLeft--;
         return false;
      } else {
         return true;
      }
   }

   public void handleTime(int screenCursorValue, ItemStack stack) {
      this.screenDequeEmpty.handleEmptyTime(screenCursorValue, stack, this.tickCounter);
   }

   public boolean canTimeSelected(int selectedValue) {
      return selectedValue < 0 ? false : selectedValue == this.selectedSlot || selectedValue == this.previousSlot && this.tickCounter - this.selectedValue < 40L;
   }
}
