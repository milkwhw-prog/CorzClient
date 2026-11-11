package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.IntegerSetting;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class KeyPearl extends Module {
   public final IntegerSetting switchBackSlotSetting = new IntegerSetting("Switch Back Slot", "Hotbar slot to select after throwing", 1, 1, 9);
   private int pearlStage;
   private int cooldownWaitTicks;

   private int findPearlSlot() {
      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getStack(i).isOf(Items.ENDER_PEARL)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   protected void onEnable() {
      this.pearlStage = 0;
      this.cooldownWaitTicks = 0;
   }

   public KeyPearl() {
      super("Key Pearl", IconsPngType.COMBAT);
      this.addSettings(new ConfigSetting[]{this.switchBackSlotSetting});
   }

   @Override
   protected void onReset() {
      if (mc.player == null || mc.interactionManager == null) {
         this.toggle();
      } else if (mc.currentScreen == null) {
         PlayerInventory inventory = mc.player.getInventory();
         switch (this.pearlStage) {
            case 0:
               int inventoryEnder = this.findPearlSlot();
               if (inventoryEnder == -1) {
                  this.toggle();
                  return;
               }

               if (inventory.getSelectedSlot() != inventoryEnder) {
                  inventory.setSelectedSlot(inventoryEnder);
               }

               this.pearlStage = 1;
               break;
            case 1:
               ItemStack mainHandStack = mc.player.getMainHandStack();
               if (!mainHandStack.isOf(Items.ENDER_PEARL)) {
                  this.pearlStage = 2;
                  return;
               }

               if (mc.player.getItemCooldownManager().isCoolingDown(mainHandStack)) {
                  if (++this.cooldownWaitTicks > 10) {
                     this.pearlStage = 2;
                  }

                  return;
               }

               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               this.pearlStage = 2;
               break;
            default:
               int selectedSlot = this.switchBackSlotSetting.getValue() - 1;
               if (inventory.getSelectedSlot() != selectedSlot) {
                  inventory.setSelectedSlot(selectedSlot);
               }

               this.toggle();
         }
      }
   }
}
