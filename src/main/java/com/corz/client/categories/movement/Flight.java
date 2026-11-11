package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.PlayerInput;
import ai.auth.Module;
import com.corz.client.utils.HackBoatUtils;
import com.corz.client.utils.IconsPngType;

public class Flight extends Module {
   private float flySpeed;
   private final DoubleSetting doubleHelper;
   private final DoubleSetting speedIIi;
   private boolean speedVelocityFlag;
   private final EnumSetting enumHelper = new EnumSetting(
      "Mode",
      "Velocity flies by overwriting your motion (works in survival, anticheat-visible); Creative flips your creative-flight ability (only where the server allows it)",
      "Velocity",
      "Velocity",
      "Creative"
   );

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      if (player != null) {
         PlayerAbilities abilities = player.getAbilities();
         if (this.isEquals()) {
            if (!this.speedVelocityFlag) {
               this.flySpeed = abilities.getFlySpeed();
               this.speedVelocityFlag = true;
            }

            abilities.allowFlying = true;
            abilities.flying = true;
            abilities.setFlySpeed((float)(0.05F * this.speedIIi.getValue()));
         } else {
            this.handleSpeedFly(abilities);
         }

         player.fallDistance = 0.0;
      }
   }

   public Flight() {
      super("Flight", IconsPngType.MOVEMENT);
      this.speedIIi = new DoubleSetting(
         "Speed", "Horizontal fly speed (blocks/tick in Velocity mode; scales creative fly speed in Creative mode)", 0.8, 0.1, 5.0, 0.05
      );
      this.doubleHelper = new DoubleSetting("Vertical Speed", "Up/down speed for Space and Shift (Velocity mode only)", 0.5, 0.1, 5.0, 0.05);
      this.speedVelocityFlag = false;
      this.flySpeed = 0.05F;
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.speedIIi, this.doubleHelper});
   }

   @Override
   protected void onDisable() {
      if (mc.player != null) {
         this.handleSpeedFly(mc.player.getAbilities());
      }
   }

   private void handleSpeedFly(PlayerAbilities playerAbilities) {
      if (this.speedVelocityFlag) {
         playerAbilities.setFlySpeed(this.flySpeed);
         if (!playerAbilities.creativeMode) {
            playerAbilities.allowFlying = false;
            playerAbilities.flying = false;
         }

         this.speedVelocityFlag = false;
      }
   }

   private boolean isEquals() {
      return "Creative".equals(this.enumHelper.getCurrentOption());
   }

   public boolean canPressedInput(ClientPlayerEntity player) {
      if (this.isEquals()) {
         return false;
      } else if (player.hasVehicle()) {
         return false;
      } else {
         PlayerInput playerInput = player.input.playerInput;
         double pressedInputValue = (playerInput.forward() ? 1.0 : 0.0) - (playerInput.backward() ? 1.0 : 0.0);
         double hackBoatFactor = (playerInput.left() ? 1.0 : 0.0) - (playerInput.right() ? 1.0 : 0.0);
         double[] pressedInputValues = HackBoatUtils.getAmounts(hackBoatFactor, pressedInputValue, this.speedIIi.getValue(), player.getYaw());
         double y = 0.0;
         boolean pressed = mc.options.jumpKey.isPressed();
         boolean pressed2 = mc.options.sneakKey.isPressed();
         if (pressed != pressed2) {
            y = pressed ? this.doubleHelper.getValue() : -this.doubleHelper.getValue();
         }

         player.setVelocity(pressedInputValues[0], y, pressedInputValues[1]);
         return true;
      }
   }
}
