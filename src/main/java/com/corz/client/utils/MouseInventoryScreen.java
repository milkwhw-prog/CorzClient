package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;

public final class MouseInventoryScreen extends InventoryScreen {
   private final Object inputLockOwner;

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      return this.isInputBlocked() || super.keyPressed(keyInput);
   }

   @Override
   public boolean mouseReleased(Click click) {
      return this.isInputBlocked() || super.mouseReleased(click);
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      return this.isInputBlocked() || super.mouseClicked(click, mouseClickedFlag);
   }

   @Override
   public boolean mouseScrolled(double mouseScrolledValue, double mouseScrolledValue2, double mouseScrolledValue3, double mouseScrolledValue4) {
      return this.isInputBlocked() || super.mouseScrolled(mouseScrolledValue, mouseScrolledValue2, mouseScrolledValue3, mouseScrolledValue4);
   }

   public MouseInventoryScreen(PlayerEntity playerEntity, Object object) {
      super(playerEntity);
      this.inputLockOwner = object;
   }

   @Override
   public boolean charTyped(CharInput charInput) {
      return this.isInputBlocked() || super.charTyped(charInput);
   }

   private boolean isInputBlocked() {
      return InventoryStackUtilsHandle.canFlagInventoryStackObject(this.inputLockOwner);
   }

   @Override
   public boolean mouseDragged(Click click, double mouseDraggedValue, double mouseDraggedValue2) {
      return this.isInputBlocked() || super.mouseDragged(click, mouseDraggedValue, mouseDraggedValue2);
   }
}
