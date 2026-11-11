package com.corz.client.mixins;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.utils.LightPacketCache;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.InventoryPlayer;
import ai.auth.ModuleRegistry;

@Mixin({ClientConnection.class})
public class MixinClientConnection {
   @Inject(
      method = {"method_10743(Lnet/minecraft/class_2596;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPacketSend(Packet<?> packet, CallbackInfo callbackInfo) {
      InventoryPlayer event = new InventoryPlayer(packet, (ClientConnection)((net.minecraft.network.ClientConnection) (Object) (this)));
      ModuleRegistry.dispatchPacketSend(event);
      ConnectionTimeUtils.Outbound.handlePacket(event);
      if (event.isFlag()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"method_52906(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corzOrderOutbound(Packet<?> packet, ChannelFutureListener channelFutureListener, boolean corzOrderFlag, CallbackInfo callbackInfo) {
      if (ConnectionTimeUtils.Outbound.canEventLoop((ClientConnection)((net.minecraft.network.ClientConnection) (Object) (this)), packet, channelFutureListener, corzOrderFlag)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"method_10770(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/class_2596;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPacketReceive(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
      LightPacketCache.onLightPacket(packet);
      AutoVanish event = new AutoVanish(packet);
      ModuleRegistry.dispatchPacketReceive(event);
      if (event.isFlag()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"method_10770(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/class_2596;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corzHoldInbound(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callbackInfo) {
      if (ConnectionTimeUtils.Inbound.canConnectionClient((ClientConnection)((net.minecraft.network.ClientConnection) (Object) (this)), packet)) {
         callbackInfo.cancel();
      }
   }
}
