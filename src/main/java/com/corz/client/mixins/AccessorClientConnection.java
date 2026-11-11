package com.corz.client.mixins;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientConnection.class})
public interface AccessorClientConnection {
   @Accessor("channel")
   Channel corzChannel();

   @Invoker("handlePacket")
   static void corzHandlePacket(Packet<?> packet, PacketListener packetListener) {
      throw new AssertionError();
   }
}
