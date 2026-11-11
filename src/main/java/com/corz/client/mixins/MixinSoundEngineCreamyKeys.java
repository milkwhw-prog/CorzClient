package com.corz.client.mixins;

import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.misc.CreamyKeys;

@Mixin({SoundEngine.class})
public class MixinSoundEngineCreamyKeys {
   @Inject(
      method = {"method_19664()V"},
      at = {@At("HEAD")}
   )
   private void corz$creamyKeysRelease(CallbackInfo callbackInfo) {
      CreamyKeys.updateClickPlayer();
   }
}
