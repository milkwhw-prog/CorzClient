package com.corz.client.mixins;

import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BiomeAccess.class})
public interface AccessorBiomeAccess {
   @Accessor("seed")
   long getSeed();
}
