package com.corz.client.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.corz.client.categories.misc.FakeCoordinates;

@Mixin({ClientPlayerEntity.class})
public abstract class MixinClientPlayerEntityFakeCoords {
   @ModifyVariable(
      method = {"method_7353(Lnet/minecraft/class_2561;Z)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Text corz$fakeCoordsInClientChat(Text text) {
      return FakeCoordinates.getLiteral(text);
   }
}
