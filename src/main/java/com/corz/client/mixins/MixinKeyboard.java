package com.corz.client.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.SwordBot;

@Mixin({Keyboard.class})
public class MixinKeyboard {
   @Inject(
      method = {"method_1466(JILnet/minecraft/class_11908;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onKey(long windowHandle, int scanCode, KeyInput keyInput, CallbackInfo callbackInfo) {
      SwordBot event = new SwordBot(keyInput.key(), scanCode);
      ModuleRegistry.dispatchSwordBot(event);
      if (event.isFlag()) {
         callbackInfo.cancel();
      }
   }
}
