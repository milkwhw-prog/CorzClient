package com.corz.client.mixins;

import com.corz.client.entity.ClientThreadUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public class MixinClientPlayerInteractionManagerCombat {
   @Inject(
      method = {"method_2918(Lnet/minecraft/class_1657;Lnet/minecraft/class_1297;)V"},
      at = {@At("HEAD")}
   )
   private void corzOnAttackEntity(PlayerEntity attacker, Entity entity, CallbackInfo callbackInfo) {
      ClientThreadUtils.handleFlagEntity(entity);
   }
}
