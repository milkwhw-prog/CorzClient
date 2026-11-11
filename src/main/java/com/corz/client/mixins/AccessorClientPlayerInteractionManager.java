package com.corz.client.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientPlayerInteractionManager.class})
public interface AccessorClientPlayerInteractionManager {
   @Accessor("blockBreakingCooldown")
   void setBlockBreakingCooldown(int blockBreakingValue);

   @Accessor("blockBreakingCooldown")
   int getBlockBreakingCooldown();

   @Accessor("currentBreakingProgress")
   float corzBreakingProgress();

   @Invoker("syncSelectedSlot")
   void corzSyncSelectedSlot();
}
