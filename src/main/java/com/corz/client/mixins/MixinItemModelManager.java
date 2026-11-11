package com.corz.client.mixins;

import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.corz.client.categories.visuals.SwordInspect;
import ai.auth.ModuleRegistry;

@Mixin({ItemModelManager.class})
public abstract class MixinItemModelManager {
   @ModifyVariable(
      method = {"method_65597(Lnet/minecraft/class_10444;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_1309;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private ItemStack corz$thirdPersonKnife(ItemStack modelThird, ItemRenderState itemRenderState, ItemStack itemStack, ItemDisplayContext itemDisplayContext, LivingEntity livingEntity) {
      SwordInspect si = (SwordInspect)ModuleRegistry.getModule(SwordInspect.class);
      return si == null ? modelThird : si.getModelThird(livingEntity, modelThird, itemDisplayContext);
   }
}
