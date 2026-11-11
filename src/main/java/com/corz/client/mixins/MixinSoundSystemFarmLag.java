package com.corz.client.mixins;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.SoundSystem.PlayResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.NoFarmLag;

@Mixin({SoundSystem.class})
public class MixinSoundSystemFarmLag {
   @Inject(
      method = {"method_4854(Lnet/minecraft/class_1113;)Lnet/minecraft/class_1140$class_11518;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$noFarmLagMuteSpam(SoundInstance soundInstance, CallbackInfoReturnable<PlayResult> callbackInfoReturnable) {
      NoFarmLag module = NoFarmLag.getNoFarmLag();
      if (module != null && module.canNoteBlock(soundInstance)) {
         callbackInfoReturnable.setReturnValue(PlayResult.NOT_STARTED);
      }
   }
}
