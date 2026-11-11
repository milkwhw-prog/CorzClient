package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.inventory.SelectedInventoryUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import com.corz.client.utils.WrapDegreesUtils;
import ai.auth.Module;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.IconsPngType;

public class PearlCatchAssist extends Module {
   private final BooleanSetting booleanBase = new BooleanSetting(
      "Auto Aim Up",
      "Aim up automatically and hold that EXACT rotation for both throws, so the wind charge always catches the pearl. Off = throw along your live aim (you keep it lined up yourself).",
      true
   );
   private int selectedSlot;
   private float clamp;
   private final DoubleSetting doubleHelper = new DoubleSetting(
      "Launch Pitch",
      "How steeply to aim up when Auto Aim Up is on (\u221290 = straight up, \u221245 = up and out). Steeper gains more height; shallower travels farther.",
      -80.0,
      -90.0,
      0.0,
      1.0
   );
   private boolean hotbarPearlActive;
   private float yaw;
   private float pitch;
   private float wrapDegrees;
   private int holdTicksRemaining;
   private float pearlCatchAmount;
   private boolean pearlHotbarFlag;
   private final IntegerSetting integerHelper = new IntegerSetting(
      "Charge Delay", "Ticks to wait after the pearl before the wind charge, so the charge catches the pearl (20 ticks = 1s)", 2, 0, 20
   );
   private float iIiAmount;
   private boolean iIiActive;
   private final BooleanSetting swapBack = new BooleanSetting(
      "Swap Back", "Return to your previously held item once the launch has fired. Off = stay on the wind charge.", true
   );

   @Override
   protected void onCameraAngle(DeltaAngle deltaAngle) {
      if (this.hotbarPearlActive && mc.player != null) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerpAngleDegrees(tickDelta, this.yaw, this.wrapDegrees));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.pitch, this.clamp));
         deltaAngle.updateFlag();
      }
   }

   private void handleHandPlayer(int selectedSlot) {
      mc.player.getInventory().setSelectedSlot(selectedSlot);
      mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
      mc.player.swingHand(Hand.MAIN_HAND);
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
            boolean iIiFlag = true;
            if (this.booleanBase.isEnabled()) {
               if (!this.iIiActive) {
                  this.iIiAmount = mc.player.getYaw();
                  this.pearlCatchAmount = (float)this.doubleHelper.getValue();
                  this.iIiActive = true;
               }

               iIiFlag = this.isYawWrap();
            }

            if (!this.pearlHotbarFlag) {
               int iIiValue = SelectedInventoryUtils.getValue(Items.ENDER_PEARL);
               int iIiValue2 = SelectedInventoryUtils.getValue(Items.WIND_CHARGE);
               if (iIiValue == -1 || iIiValue2 == -1) {
                  DisabledEnabled.getInstance()
                     .handleDisabledEnabled("Pearl Catch Assist", iIiValue == -1 ? "No ender pearl in hotbar" : "No wind charge in hotbar", false);
                  this.setEnabled(false);
                  return;
               }

               if (mc.player.getItemCooldownManager().isCoolingDown(mc.player.getInventory().getStack(iIiValue))) {
                  this.setEnabled(false);
                  return;
               }

               if (this.booleanBase.isEnabled() && !iIiFlag) {
                  return;
               }

               this.selectedSlot = mc.player.getInventory().getSelectedSlot();
               this.handleHandPlayer(iIiValue);
               this.pearlHotbarFlag = true;
               this.holdTicksRemaining = this.integerHelper.getValue();
               if (this.holdTicksRemaining > 0) {
                  return;
               }
            } else if (this.holdTicksRemaining > 0) {
               this.holdTicksRemaining--;
               if (this.holdTicksRemaining > 0) {
                  return;
               }
            }

            int iIiValue3 = SelectedInventoryUtils.getValue(Items.WIND_CHARGE);
            if (iIiValue3 != -1) {
               this.handleHandPlayer(iIiValue3);
            }

            this.setEnabled(false);
         }
      }
   }

   private void updateHotbarPearl() {
      this.pearlHotbarFlag = false;
      this.holdTicksRemaining = 0;
      this.selectedSlot = -1;
      this.iIiActive = false;
      this.hotbarPearlActive = false;
   }

   private boolean isYawWrap() {
      this.yaw = mc.player.getYaw();
      this.pitch = mc.player.getPitch();
      this.hotbarPearlActive = true;
      float wrapDegrees = MathHelper.wrapDegrees(WrapDegreesUtils.stepAngleProportional(this.yaw, this.iIiAmount, 0.5F, 60.0F, 8.0F));
      float clamp = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.pitch, this.pearlCatchAmount, 0.5F, 60.0F, 8.0F), -90.0F, 90.0F);
      mc.player.setYaw(wrapDegrees);
      mc.player.setPitch(clamp);
      this.wrapDegrees = wrapDegrees;
      this.clamp = clamp;
      return Math.abs(MathHelper.wrapDegrees(this.iIiAmount - wrapDegrees)) <= 0.8F && Math.abs(this.pearlCatchAmount - clamp) <= 0.8F;
   }

   @Override
   protected void onEnable() {
      this.updateHotbarPearl();
   }

   public PearlCatchAssist() {
      super("Pearl Catch Assist", IconsPngType.MOVEMENT);
      this.selectedSlot = -1;
      this.addSettings(new ConfigSetting[]{this.booleanBase, this.doubleHelper, this.integerHelper, this.swapBack});
   }

   @Override
   protected void onDisable() {
      if (this.selectedSlot != -1 && this.swapBack.isEnabled() && mc.player != null) {
         mc.player.getInventory().setSelectedSlot(this.selectedSlot);
      }

      this.updateHotbarPearl();
   }
}
