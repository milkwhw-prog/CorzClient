package com.corz.client.mixins;

import com.corz.client.render.BlockStateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.donutsmp.Bloodhound;
import com.corz.client.categories.donutsmp.ChunkFinder;
import com.corz.client.categories.donutsmp.ChunkFinderV2;
import com.corz.client.categories.visuals.HoleESP;
import com.corz.client.categories.visuals.SpawnerFinder;

@Mixin({ClientWorld.class})
public class MixinClientWorldBlockUpdate {
   @Inject(
      method = {"method_41928(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;I)V"},
      at = {@At("HEAD")}
   )
   private void corzChunkFinderBlockUpdate(BlockPos pos2, BlockState blockState, int corzChunkValue, CallbackInfo callbackInfo) {
      ClientWorld self = (ClientWorld)((net.minecraft.client.world.ClientWorld) (Object) (this));
      BlockState oldState = self.getBlockState(pos2);
      ChunkFinder.onBlockStateChanged(self, pos2, oldState, blockState);
      ChunkFinderV2.onBlockStateChanged(pos2, oldState, blockState);
      Bloodhound.onBlockStateChanged(pos2, oldState, blockState);
      HoleESP.handleScanRender(pos2, oldState, blockState);
      SpawnerFinder.handleInstance(self, pos2, oldState, blockState);
      AutoBuilder.recordBlockStateChange(pos2, oldState, blockState);
      if (oldState != blockState) {
         BlockStateHelper.handleHashOpenForBlockPos(pos2);
      }
   }

   @Inject(
      method = {"method_41926(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_243;)V"},
      at = {@At("HEAD")}
   )
   private void corzServerBlockAck(BlockPos pos2, BlockState blockState, Vec3d vec, CallbackInfo callbackInfo) {
      AutoBuilder.recordTickedBlockState(pos2, blockState);
   }
}
