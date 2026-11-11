package com.corz.client.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.combat.AutoTrap;
import com.corz.client.categories.misc.NameProtect;
import ai.auth.Module;
import ai.auth.ModuleRegistry;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer<T extends Entity> {
   @Inject(
      method = {"method_62358(Lnet/minecraft/class_1297;)Lnet/minecraft/class_238;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onGetBoundingBox(T t, CallbackInfoReturnable<Box> callbackInfoReturnable) {
      if (t != Module.mc.player) {
         if (t instanceof PlayerEntity) {
            AutoTrap autoTrap = (AutoTrap)ModuleRegistry.getModule(AutoTrap.class);
            if (autoTrap != null && autoTrap.isEnabled() && autoTrap.getHitboxExpand() > 0.0) {
               Box box = (Box)callbackInfoReturnable.getReturnValue();
               if (box != null) {
                  callbackInfoReturnable.setReturnValue(box.expand(autoTrap.getHitboxExpand()));
               }
            }
         }
      }
   }

   @Inject(
      method = {"method_62354(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V"},
      at = {@At("TAIL")}
   )
   private void corz_maskNameLabel(Entity entity, EntityRenderState entityRenderState, float corzMaskValue, CallbackInfo callbackInfo) {
      if (entityRenderState.displayName != null) {
         if (entity == Module.mc.player || NameProtect.isFlag()) {
            entityRenderState.displayName = NameProtect.getDisplayName(entityRenderState.displayName);
         }
      }
   }
}
