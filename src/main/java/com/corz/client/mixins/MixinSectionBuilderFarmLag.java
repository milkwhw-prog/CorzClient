package com.corz.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.chunk.SectionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({SectionBuilder.class})
public class MixinSectionBuilderFarmLag {
   @WrapOperation(
      method = {"method_60904(Lnet/minecraft/class_4076;Lnet/minecraft/class_853;Lnet/minecraft/class_8251;Lnet/minecraft/class_750;)Lnet/minecraft/class_9810$class_9811;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_2680;method_26217()Lnet/minecraft/class_2464;"
      )}
   )
   private BlockRenderType corz$noFarmLagRedstone(BlockState blockState, Operation<BlockRenderType> operation) {
      return NoFarmLag.canRedstoneTorch(blockState) ? BlockRenderType.INVISIBLE : (BlockRenderType)operation.call(new Object[]{blockState});
   }
}
