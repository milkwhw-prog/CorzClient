package com.corz.client.mixins;

import com.corz.client.config.ThemeMusicPlayer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class MixinGameRendererThemeMusic {
   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At("HEAD")}
   )
   private void corz$themeMusicFrame(RenderTickCounter renderTickCounter, boolean corzthemeMusicFlag, CallbackInfo callbackInfo) {
      ThemeMusicPlayer.tickMusicSafely();
   }
}
