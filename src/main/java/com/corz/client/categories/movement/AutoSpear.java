package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoSpear extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting jumpIIi;
   private int selectedSlot;
   private int spearPhase;
   private final IntegerSetting integerHelper = new IntegerSetting("Spear Slot", "Hotbar slot (1-9) that holds the spear", 1, 1, 9);

   private void updatePiercingHand() {
      ItemStack mainHandStack = mc.player.getMainHandStack();
      PiercingWeaponComponent piercingWeaponComponent = mainHandStack.get(DataComponentTypes.PIERCING_WEAPON);
      if (piercingWeaponComponent != null) {
         mc.interactionManager.attackWithPiercingWeapon(piercingWeaponComponent);
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   @Override
   protected void onEnable() {
      this.spearPhase = 0;
      this.selectedSlot = -1;
   }

   @Override
   public void onReset() {
      if (mc.player != null
         && mc.interactionManager != null
         && mc.world != null
         && mc.player.networkHandler != null) {
         if (mc.currentScreen != null) {
            this.setEnabled(false);
         } else {
            switch (this.spearPhase) {
               case 0:
                  int iIiValue = this.integerHelper.getValue() - 1;
                  this.selectedSlot = mc.player.getInventory().getSelectedSlot();
                  if (this.jumpIIi.isEnabled() && mc.player.isOnGround()) {
                     mc.options.jumpKey.setPressed(true);
                  }

                  if (this.selectedSlot != iIiValue) {
                     this.handleSelectedSlot(iIiValue);
                  }

                  this.updatePiercingHand();
                  this.spearPhase = 1;
                  return;
               default:
                  mc.options.jumpKey.setPressed(false);
                  if (this.booleanBase.isEnabled() && this.selectedSlot != -1) {
                     this.handleSelectedSlot(this.selectedSlot);
                  }

                  this.selectedSlot = -1;
                  this.setEnabled(false);
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      if (mc.options != null) {
         mc.options.jumpKey.setPressed(false);
      }

      if (this.booleanBase.isEnabled() && this.selectedSlot != -1 && mc.player != null && mc.player.networkHandler != null) {
         this.handleSelectedSlot(this.selectedSlot);
      }

      this.selectedSlot = -1;
   }

   public AutoSpear() {
      super("Auto Spear", IconsPngType.MOVEMENT);
      this.jumpIIi = new BooleanSetting("Jump", "Jump as the lunge fires", true);
      this.booleanBase = new BooleanSetting(
         "Swap Back", "Return to your previous item after the lunge (one clean swap each way \u2014 GrimAC-safe). Off = stay on the spear.", true
      );
      this.selectedSlot = -1;
      this.addSettings(new ConfigSetting[]{this.integerHelper, this.jumpIIi, this.booleanBase});
   }

   private void handleSelectedSlot(int selectedSlot) {
      mc.player.getInventory().setSelectedSlot(selectedSlot);
   }
}
