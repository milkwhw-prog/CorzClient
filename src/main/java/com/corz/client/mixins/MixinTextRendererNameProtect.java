package com.corz.client.mixins;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.corz.client.categories.misc.NameProtect;

@Mixin({TextRenderer.class})
public class MixinTextRendererNameProtect {
   @ModifyVariable(
      method = {"method_27525(Lnet/minecraft/class_5348;)I"},
      at = @At("HEAD"),
      argsOnly = true,
      require = 0
   )
   private StringVisitable corz_maskMeasuredName(StringVisitable stringVisitable) {
      return (StringVisitable)(stringVisitable instanceof Text t && NameProtect.isFlag() ? NameProtect.getDisplayName(t) : stringVisitable);
   }
}
