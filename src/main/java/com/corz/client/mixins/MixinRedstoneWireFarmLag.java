package com.corz.client.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({RedstoneWireBlock.class})
public class MixinRedstoneWireFarmLag {
   @Inject(
      method = {"method_9496(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$noFarmLagDustParticles(BlockState blockState, World world, BlockPos pos2, Random random, CallbackInfo callbackInfo) {
      if (world.isClient() && NoFarmLag.canRedstoneTorch(blockState)) {
         callbackInfo.cancel();
      }
   }
}
