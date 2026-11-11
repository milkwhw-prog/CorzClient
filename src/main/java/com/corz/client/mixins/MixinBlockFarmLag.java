package com.corz.client.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({Block.class})
public class MixinBlockFarmLag {
   @Inject(
      method = {"method_9607(Lnet/minecraft/class_2680;Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void corz$noFarmLagUncull(BlockState currentState, BlockState neighborState, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (NoFarmLag.canRedstoneTorch(neighborState)) {
         callbackInfoReturnable.setReturnValue(true);
      }
   }
}
