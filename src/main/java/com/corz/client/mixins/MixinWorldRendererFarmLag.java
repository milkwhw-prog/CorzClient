package com.corz.client.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({WorldRenderer.class})
public class MixinWorldRendererFarmLag {
   @Inject(
      method = {"method_8570(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2680;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$noFarmLagSkipRebuild(BlockView blockView, BlockPos pos2, BlockState state, BlockState blockState, int corznoFarmValue, CallbackInfo callbackInfo) {
      if (state != blockState && state.getBlock() == blockState.getBlock() && NoFarmLag.canRedstoneTorch(blockState)) {
         callbackInfo.cancel();
      }
   }
}
