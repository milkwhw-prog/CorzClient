package com.corz.client.mixins;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.utils.C2PackAudio;

@Mixin({SoundLoader.class})
public class MixinSoundLoaderKnifePack {
   @Shadow
   @Final
   private Map<Identifier, CompletableFuture<StaticSound>> loadedSounds;

   @Inject(
      method = {"method_19743(Lnet/minecraft/class_2960;)Ljava/util/concurrent/CompletableFuture;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$knifePackSound(Identifier id2, CallbackInfoReturnable<CompletableFuture<StaticSound>> callbackInfoReturnable) {
      if (C2PackAudio.canSoundsPack(id2)) {
         callbackInfoReturnable.setReturnValue(this.loadedSounds.computeIfAbsent(id2, C2PackAudio::getSupplyAsync));
      }
   }

   @Inject(
      method = {"method_19738()V"},
      at = {@At("HEAD")}
   )
   private void corz$knifePackReleased(CallbackInfo callbackInfo) {
      C2PackAudio.updateMuchPack();
   }
}
