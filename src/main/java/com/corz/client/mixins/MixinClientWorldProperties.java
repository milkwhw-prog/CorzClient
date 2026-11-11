package com.corz.client.mixins;

import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.CustomWorld;

@Mixin({Properties.class})
public class MixinClientWorldProperties {
   @Inject(
      method = {"method_217()J"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void corz$timeOfDay(CallbackInfoReturnable<Long> callbackInfoReturnable) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null) {
         callbackInfoReturnable.setReturnValue(cw.getValueJ(callbackInfoReturnable.getReturnValueJ()));
      }
   }
}
