package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import com.corz.client.utils.HackBoatUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class BoatFly extends Module {
   private AbstractBoatEntity abstractBoatEntity;
   private final BooleanSetting booleanBase;
   private final DoubleSetting doubleHelper;
   private final DoubleSetting horizontalSpeed = new DoubleSetting(
      "Horizontal Speed", "Forward/back fly speed in blocks per tick (vanilla boat cruise is ~0.4)", 0.6, 0.1, 5.0, 0.05
   );

   private void updateBoatAbstract() {
      if (this.abstractBoatEntity != null) {
         this.abstractBoatEntity.noClip = false;
         this.abstractBoatEntity = null;
      }
   }

   public BoatFly() {
      super("Boatfly", IconsPngType.MISC);
      this.doubleHelper = new DoubleSetting("Vertical Speed", "Up/down speed for Jump and Sneak, in blocks per tick", 0.5, 0.1, 5.0, 0.05);
      this.booleanBase = new BooleanSetting(
         "NoClip",
         "Pass the boat through blocks so you can fly down through terrain to bedrock. A server that re-simulates movement (e.g. GrimAC) will reject this.",
         true
      );
      this.addSettings(new ConfigSetting[]{this.horizontalSpeed, this.doubleHelper, this.booleanBase});
   }

   @Override
   protected void onReset() {
      if (this.abstractBoatEntity != null && (mc.player == null || mc.player.getVehicle() != this.abstractBoatEntity)) {
         this.updateBoatAbstract();
      }
   }

   public boolean canPressedOptions(AbstractBoatEntity abstractBoatEntity, MovementType movementType) {
      ClientPlayerEntity player = mc.player;
      if (player != null && player.getVehicle() == abstractBoatEntity) {
         double pressedOptionsValue = (mc.options.forwardKey.isPressed() ? 1.0 : 0.0) - (mc.options.backKey.isPressed() ? 1.0 : 0.0);
         double[] pressedOptionsValues = HackBoatUtils.getAmounts(0.0, pressedOptionsValue, this.horizontalSpeed.getValue(), abstractBoatEntity.getYaw());
         double y = 0.0;
         boolean pressed = mc.options.jumpKey.isPressed();
         boolean pressed2 = mc.options.sneakKey.isPressed();
         if (pressed != pressed2) {
            y = pressed ? this.doubleHelper.getValue() : -this.doubleHelper.getValue();
         }

         abstractBoatEntity.setVelocity(pressedOptionsValues[0], y, pressedOptionsValues[1]);
         abstractBoatEntity.noClip = this.booleanBase.isEnabled();
         this.abstractBoatEntity = abstractBoatEntity;
         abstractBoatEntity.fallDistance = 0.0;
         player.fallDistance = 0.0;
         abstractBoatEntity.move(movementType, abstractBoatEntity.getVelocity());
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void onDisable() {
      this.updateBoatAbstract();
   }
}
