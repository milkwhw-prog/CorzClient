package com.corz.client.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.CustomFlame;

@Mixin({AbstractBlock.class})
public class MixinAbstractBlockFireRender {
   @Inject(
      method = {"method_9604(Lnet/minecraft/class_2680;)Lnet/minecraft/class_2464;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz_hideFireForCustomFlame(BlockState blockState, CallbackInfoReturnable<BlockRenderType> callbackInfoReturnable) {
      if ((Object) this instanceof AbstractFireBlock && CustomFlame.isBaseRestyleWorldFire()) {
         callbackInfoReturnable.setReturnValue(BlockRenderType.INVISIBLE);
      }
   }
}
