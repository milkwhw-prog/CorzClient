package com.corz.client.mixins;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerInteractEntityC2SPacket.class})
public interface AccessorPlayerInteractEntityC2SPacket {
   @Accessor("entityId")
   int getEntityId();
}
