package com.corz.client.mixins;

import net.minecraft.client.gui.hud.debug.BiomeDebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.misc.FakeCoordinates;

@Mixin({BiomeDebugHudEntry.class})
public abstract class MixinBiomeDebugHudEntryFakeCoords {
   @Inject(
      method = {"method_72751(Lnet/minecraft/class_11630;Lnet/minecraft/class_1937;Lnet/minecraft/class_2818;Lnet/minecraft/class_2818;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$hideBiome(DebugHudLines debugHudLines, World world, WorldChunk worldChunk, WorldChunk worldChunk2, CallbackInfo callbackInfo) {
      if (FakeCoordinates.isBaseInstance()) {
         callbackInfo.cancel();
      }
   }
}
