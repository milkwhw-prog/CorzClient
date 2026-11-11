package com.corz.client.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientPlayNetworkHandler.class})
public interface AccessorClientPlayNetworkHandler {
   @Accessor("chunkLoadDistance")
   int getChunkLoadDistance();
}
