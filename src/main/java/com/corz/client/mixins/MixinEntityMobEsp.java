package com.corz.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.corz.client.categories.visuals.MobESP;

@Mixin({Entity.class})
public abstract class MixinEntityMobEsp {
   @ModifyReturnValue(
      method = {"method_5851()Z"},
      at = {@At("RETURN")}
   )
   private boolean corz$mobEspGlow(boolean originalGlowing) {
      return originalGlowing || MobESP.getCornersOutline((Entity)((net.minecraft.entity.Entity) (Object) (this))) != 0;
   }

   @ModifyReturnValue(
      method = {"method_22861()I"},
      at = {@At("RETURN")}
   )
   private int corz$mobEspColor(int originalColor) {
      int color = MobESP.getCornersOutline((Entity)((net.minecraft.entity.Entity) (Object) (this)));
      return color != 0 ? color : originalColor;
   }
}
