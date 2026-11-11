package com.corz.client.mixins;

import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.PistonBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({PistonBlockEntityRenderer.class})
public class MixinPistonBlockEntityRendererFarmLag {
   @Inject(
      method = {"method_74380(Lnet/minecraft/class_2669;Lnet/minecraft/class_11968;FLnet/minecraft/class_243;Lnet/minecraft/class_11683$class_11792;)V"},
      at = {@At("TAIL")}
   )
   private void corz$noFarmLagSnapPiston(
      PistonBlockEntity pistonBlockEntity, PistonBlockEntityRenderState pistonBlockEntityRenderState, float corznoFarmValue, Vec3d vec, CrumblingOverlayCommand crumblingOverlayCommand, CallbackInfo callbackInfo
   ) {
      NoFarmLag module = NoFarmLag.getNoFarmLag();
      if (module != null && module.isPistons()) {
         pistonBlockEntityRenderState.offsetX = 0.0F;
         pistonBlockEntityRenderState.offsetY = 0.0F;
         pistonBlockEntityRenderState.offsetZ = 0.0F;
      }
   }
}
