package com.corz.client.mixins;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({EntityRenderManager.class})
public class MixinEntityRenderManagerFarmLag {
   @Inject(
      method = {"method_3950(Lnet/minecraft/class_1297;Lnet/minecraft/class_4604;DDD)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <E extends Entity> void corz$noFarmLagHide(E e, Frustum frustum, double corznoFarmValue, double corznoFarmValue2, double corznoFarmValue3, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      NoFarmLag module = NoFarmLag.getNoFarmLag();
      if (module != null && module.canBase(e)) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}
