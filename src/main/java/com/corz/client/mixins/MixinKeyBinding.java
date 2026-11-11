package com.corz.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.movement.Freecam;

@Mixin({KeyBinding.class})
public class MixinKeyBinding {
   @Inject(
      method = {"method_1434()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void freecam_blockInputs(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (Freecam.isInstanceFlag()) {
         if (!Freecam.isFlag()) {
            KeyBinding self = (KeyBinding)((net.minecraft.client.option.KeyBinding) (Object) (this));
            if (this.isMovementKey(self)) {
               callbackInfoReturnable.setReturnValue(false);
            }
         }
      }
   }

   private boolean isMovementKey(KeyBinding keyBinding) {
      GameOptions o = MinecraftClient.getInstance().options;
      return keyBinding == o.forwardKey || keyBinding == o.backKey || keyBinding == o.leftKey || keyBinding == o.rightKey || keyBinding == o.jumpKey || keyBinding == o.sneakKey || keyBinding == o.sprintKey;
   }
}
