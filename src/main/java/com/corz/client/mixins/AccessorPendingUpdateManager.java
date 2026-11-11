package com.corz.client.mixins;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.network.PendingUpdateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PendingUpdateManager.class})
public interface AccessorPendingUpdateManager {
   @Accessor("blockPosToPendingUpdate")
   Long2ObjectOpenHashMap<?> corzPendingByPos();
}
