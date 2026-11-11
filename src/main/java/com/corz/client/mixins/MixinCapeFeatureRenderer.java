package com.corz.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer.CrumblingOverlayCommand;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.corz.client.utils.MatrixSubmitUtils;

@Mixin({CapeFeatureRenderer.class})
public abstract class MixinCapeFeatureRenderer {
   @Shadow
   private boolean hasCustomModelForLayer(ItemStack itemStack, LayerType layerType) {
      throw new AssertionError();
   }

   @WrapOperation(
      method = {"method_4177(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;FF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11659;method_73489(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_11683$class_11792;)V"
      )}
   )
   private void corz$submitCape3d(
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      Model<?> model,
      Object obj,
      MatrixStack matrixStack,
      RenderLayer renderLayer,
      int corzsubmitCape3dValue,
      int corzsubmitCape3dValue2,
      int corzsubmitCape3dValue3,
      CrumblingOverlayCommand crumblingOverlayCommand,
      Operation<Void> operation
   ) {
      if (!(
         obj instanceof PlayerEntityRenderState s
            && MatrixSubmitUtils.canFlag(s)
            && MatrixSubmitUtils.canSubmitCustom(
               orderedRenderCommandQueue, model, s, matrixStack, renderLayer, corzsubmitCape3dValue, corzsubmitCape3dValue2, corzsubmitCape3dValue3, this.hasCustomModelForLayer(s.equippedChestStack, LayerType.HUMANOID)
            )
      )) {
         operation.call(new Object[]{orderedRenderCommandQueue, model, obj, matrixStack, renderLayer, corzsubmitCape3dValue, corzsubmitCape3dValue2, corzsubmitCape3dValue3, crumblingOverlayCommand});
      }
   }
}
