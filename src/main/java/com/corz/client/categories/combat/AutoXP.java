package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.inventory.SelectedInventoryUtils;
import ai.auth.Module.BindMode;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import ai.auth.Module;
import com.corz.client.utils.MillisCooldownTimer;
import com.corz.client.utils.IconsPngType;

public class AutoXP extends Module {
   private final IntegerSetting throwDelaySetting;
   private final BooleanSetting restoreSlotOnDisable;
   private final EnumSetting bindModeSetting = new EnumSetting(
      "Bind Mode",
      "On Hold: active only while the keybind is held (release turns it off). Toggle: press once to turn on, press again to turn off.",
      "On Hold",
      "On Hold",
      "Toggle"
   );
   private final MillisCooldownTimer throwTimer;
   private final BooleanSetting disableWhenEmpty;
   private int previousSlot;

   @Override
   public BindMode getBindMode() {
      return "On Hold".equals(this.bindModeSetting.getCurrentOption()) ? BindMode.HOLD : BindMode.TOGGLE;
   }

   @Override
   public void onDisable() {
      if (this.restoreSlotOnDisable.isEnabled() && this.previousSlot != -1 && mc.player != null) {
         mc.player.getInventory().setSelectedSlot(this.previousSlot);
      }

      this.previousSlot = -1;
   }

   @Override
   public void onEnable() {
      this.previousSlot = -1;
      this.throwTimer.restart();
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null) {
         if (mc.currentScreen == null) {
            int selectedSlot = SelectedInventoryUtils.getValue(Items.EXPERIENCE_BOTTLE);
            if (selectedSlot == -1) {
               if (this.disableWhenEmpty.isEnabled()) {
                  this.setEnabled(false);
               }
            } else {
               if (this.previousSlot == -1) {
                  this.previousSlot = mc.player.getInventory().getSelectedSlot();
               }

               if (mc.player.getInventory().getSelectedSlot() != selectedSlot) {
                  mc.player.getInventory().setSelectedSlot(selectedSlot);
               }

               if (this.throwTimer.hasElapsed(this.throwDelaySetting.getValue() * 50L)) {
                  this.throwTimer.restart();
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  mc.player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   public AutoXP() {
      super("Auto XP", IconsPngType.COMBAT);
      this.throwDelaySetting = new IntegerSetting("Throw Delay", "Ticks between thrown bottles (0 = every tick)", 1, 0, 10);
      this.disableWhenEmpty = new BooleanSetting("Disable When Empty", "Turn off automatically when no XP bottles remain in the hotbar", true);
      this.restoreSlotOnDisable = new BooleanSetting("Restore Slot", "Return to the previously held slot when it turns off", true);
      this.throwTimer = new MillisCooldownTimer();
      this.previousSlot = -1;
      this.addSettings(new ConfigSetting[]{this.bindModeSetting, this.throwDelaySetting, this.disableWhenEmpty, this.restoreSlotOnDisable});
   }
}
