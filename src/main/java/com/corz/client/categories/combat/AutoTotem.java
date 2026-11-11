package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoTotem extends Module {
   private final IntegerSetting delaySetting = new IntegerSetting("Delay", "Delay in ticks before moving totem to offhand", 0, 1, 5);
   private int remainingDelayTicks;

   @Override
   public void onEnable() {
      this.remainingDelayTicks = 0;
   }

   @Override
   public void onDisable() {
      this.remainingDelayTicks = 0;
   }

   private static int toContainerSlot(int autoTotemValue) {
      return autoTotemValue < 9 ? 36 + autoTotemValue : autoTotemValue;
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null) {
         if (mc.player.getInventory().getStack(40).isOf(Items.TOTEM_OF_UNDYING)) {
            this.remainingDelayTicks = this.delaySetting.getValue();
         } else if (this.remainingDelayTicks > 0) {
            this.remainingDelayTicks--;
         } else {
            int inventoryPlayer = this.findInventorySlot(Items.TOTEM_OF_UNDYING);
            if (inventoryPlayer != -1) {
               if (InventoryStackUtilsHandle.canFlagForObject(this)) {
                  try {
                     mc.interactionManager
                        .clickSlot(mc.player.currentScreenHandler.syncId, toContainerSlot(inventoryPlayer), 40, SlotActionType.SWAP, mc.player);
                  } finally {
                     InventoryStackUtilsHandle.handle(this);
                  }

                  this.remainingDelayTicks = this.delaySetting.getValue();
               }
            }
         }
      }
   }

   public AutoTotem() {
      super("Auto Totem", IconsPngType.COMBAT);
      this.addSettings(new ConfigSetting[]{this.delaySetting});
      this.remainingDelayTicks = 0;
   }

   private int findInventorySlot(Item item2) {
      if (mc.player == null) {
         return -1;
      } else {
         for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isOf(item2)) {
               return i;
            }
         }

         return -1;
      }
   }
}
