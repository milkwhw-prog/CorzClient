package com.corz.client.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.bots.AutoBuilder;

@Mixin({World.class})
public class MixinWorldSetBlockState {
   @Inject(
      method = {"method_30092(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;II)Z"},
      at = {@At("HEAD")}
   )
   private void corzContentHash(BlockPos pos, BlockState newState, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      World self = (World)((net.minecraft.world.World) (Object) (this));
      if (self.isClient()) {
         BlockState old = self.getBlockState(pos);
         if (old != newState) {
            AutoBuilder.onBlockStateChanged(pos, old, newState);
         }
      }
   }
}
