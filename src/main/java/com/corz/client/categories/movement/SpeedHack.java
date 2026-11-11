package com.corz.client.categories.movement;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import ai.auth.Module;
import com.corz.client.utils.HackBoatUtils;
import com.corz.client.utils.IconsPngType;

public class SpeedHack extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting sprintOnly;
   private final EnumSetting enumHelper = new EnumSetting("Mode", "Strafe glides at a fixed speed; Bhop hops as it moves", "Strafe", "Strafe", "Bhop");
   private final DoubleSetting doubleHelper = new DoubleSetting("Speed", "Movement speed multiplier over vanilla sprint", 1.4, 1.0, 5.0, 0.1);

   private boolean isEquals() {
      return "Bhop".equals(this.enumHelper.getCurrentOption());
   }

   public SpeedHack() {
      super("SpeedHack", IconsPngType.MOVEMENT);
      this.sprintOnly = new BooleanSetting("Sprint Only", "Only boost while you are sprinting", false);
      this.booleanBase = new BooleanSetting("Auto Jump", "Bhop mode: hop automatically while moving", true);
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.doubleHelper, this.sprintOnly, this.booleanBase});
   }

   public void handleVelocityInput(ClientPlayerEntity player) {
      if (!player.getAbilities().flying && !player.isGliding() && !player.hasVehicle()) {
         if (!player.isTouchingWater() && !player.isInLava() && !player.isClimbing()) {
            if (!this.sprintOnly.isEnabled() || player.isSprinting()) {
               PlayerInput playerInput = player.input.playerInput;
               double velocityInputValue = (playerInput.forward() ? 1.0 : 0.0) - (playerInput.backward() ? 1.0 : 0.0);
               double hackBoatFactor = (playerInput.left() ? 1.0 : 0.0) - (playerInput.right() ? 1.0 : 0.0);
               if (velocityInputValue != 0.0 || hackBoatFactor != 0.0) {
                  double hackBoatValue = 0.2873 * this.doubleHelper.getValue();
                  double[] velocityInputValues = HackBoatUtils.getAmounts(hackBoatFactor, velocityInputValue, hackBoatValue, player.getYaw());
                  double y = player.getVelocity().y;
                  if (this.isEquals() && this.booleanBase.isEnabled() && player.isOnGround()) {
                     y = 0.42;
                  }

                  player.setVelocity(velocityInputValues[0], y, velocityInputValues[1]);
               }
            }
         }
      }
   }
}
