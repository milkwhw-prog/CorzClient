package com.corz.client.mixins;

import com.corz.client.config.ThemeMusicPlayer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SoundLoader.class})
public class MixinSoundLoaderThemeMusic {
   @Inject(
      method = {"method_19744(Lnet/minecraft/class_2960;Z)Ljava/util/concurrent/CompletableFuture;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void corz$seekThemeSong(Identifier id2, boolean corzseekThemeFlag, CallbackInfoReturnable<CompletableFuture<AudioStream>> callbackInfoReturnable) {
      CompletableFuture<AudioStream> seeking = ThemeMusicPlayer.hookVoiceStreamLoad(id2, (CompletableFuture<AudioStream>)callbackInfoReturnable.getReturnValue());
      if (seeking != null) {
         callbackInfoReturnable.setReturnValue(seeking);
      }
   }
}
