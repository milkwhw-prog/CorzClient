package com.corz.client.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/class_1796$class_1797"}
)
public interface AccessorItemCooldownEntry {
   @Accessor("comp_3083")
   int corzStartTick();

   @Accessor("comp_3084")
   int corzEndTick();
}
