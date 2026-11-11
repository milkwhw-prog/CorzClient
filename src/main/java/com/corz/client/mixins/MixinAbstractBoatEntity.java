package com.corz.client.mixins;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.corz.client.categories.misc.BoatFly;
import ai.auth.ModuleRegistry;

@Mixin({AbstractBoatEntity.class})
public class MixinAbstractBoatEntity {
   @Redirect(
      method = {"method_5773()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_10255;method_5784(Lnet/minecraft/class_1313;Lnet/minecraft/class_243;)V"
      )
   )
   private void corzBoatFlyMove(AbstractBoatEntity abstractBoatEntity, MovementType movementType, Vec3d vec) {
      BoatFly boatFly = (BoatFly)ModuleRegistry.getModule(BoatFly.class);
      if (boatFly == null || !boatFly.isEnabled() || !boatFly.canPressedOptions(abstractBoatEntity, movementType)) {
         abstractBoatEntity.move(movementType, vec);
      }
   }
}
