package com.corz.client.mixins;

import com.corz.client.entity.ClientThreadUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class MixinClientPlayNetworkHandlerCombat {
   @Inject(
      method = {"method_11132(Lnet/minecraft/class_2743;)V"},
      at = {@At("TAIL")}
   )
   private void corzSelfVelocity(EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacke, CallbackInfo callbackInfo) {
      ClientThreadUtils.handleInstanceThread(entityVelocityUpdateS2CPacke.getEntityId());
   }

   @Inject(
      method = {"method_11124(Lnet/minecraft/class_2664;)V"},
      at = {@At("TAIL")}
   )
   private void corzSelfExplosion(ExplosionS2CPacket explosionS2CPacket, CallbackInfo callbackInfo) {
      explosionS2CPacket.playerKnockback().ifPresent(ClientThreadUtils::handleThreadInstance);
   }
}
