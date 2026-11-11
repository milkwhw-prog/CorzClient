package com.corz.client.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.misc.AutoPromo;
import com.corz.client.categories.movement.KeepSprint;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerEntityAttack {
   @Inject(
      method = {"method_7324(Lnet/minecraft/class_1297;)V"},
      at = {@At("RETURN")}
   )
   private void onAttackReturn(Entity entity, CallbackInfo callbackInfo) {
      if (((Object) this) == Module.mc.player) {
         KeepSprint keepSprint = (KeepSprint)ModuleRegistry.getModule(KeepSprint.class);
         if (keepSprint != null && keepSprint.isEnabled()) {
            Module.mc.player.setSprinting(true);
         }

         if (entity instanceof PlayerEntity victim) {
            AutoPromo autoPromo = (AutoPromo)ModuleRegistry.getModule(AutoPromo.class);
            if (autoPromo != null && autoPromo.isEnabled()) {
               autoPromo.handleUuidDeath(victim);
            }
         }
      }
   }
}
