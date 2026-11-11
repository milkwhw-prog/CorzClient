package com.corz.client.mixins;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.corz.client.categories.misc.FakeCoordinates;

@Mixin({Keyboard.class})
public abstract class MixinKeyboardFakeCoords {
   @ModifyArg(
      method = {"method_1468(Lnet/minecraft/class_11908;)Z", "method_1475(Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2487;)V", "method_1469(Lnet/minecraft/class_2960;Lnet/minecraft/class_243;Lnet/minecraft/class_2487;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_309;method_1455(Ljava/lang/String;)V"
      )
   )
   private String corz$fakeCopiedCoords(String corzfakeCopiedText) {
      return FakeCoordinates.formatInstanceForString(corzfakeCopiedText);
   }
}
