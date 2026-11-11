package com.corz.client.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.combat.Reach;
import com.corz.client.categories.misc.Fastbreak;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerEntity {
   @Inject(
      method = {"method_7351(Lnet/minecraft/class_2680;)F"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void corzFastbreak(BlockState state, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if (((Object) this) == Module.mc.player) {
         Fastbreak m = (Fastbreak)ModuleRegistry.getModule(Fastbreak.class);
         if (m != null && m.isEnabled() && m.isPaceToggle()) {
            callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValueF() * m.getAmount());
         }
      }
   }

   @Inject(
      method = {"method_55755()D"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void corzReach(CallbackInfoReturnable<Double> callbackInfoReturnable) {
      if (((Object) this) == Module.mc.player) {
         Reach m = (Reach)ModuleRegistry.getModule(Reach.class);
         if (m != null && m.isEnabled()) {
            double range = m.getActiveReachDistance();
            if (range > callbackInfoReturnable.getReturnValueD()) {
               callbackInfoReturnable.setReturnValue(range);
            }
         }
      }
   }
}
