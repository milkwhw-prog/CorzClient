package com.corz.client.mixins;

import com.corz.client.render.CorzRoundedUtils;
import net.minecraft.client.texture.GlTextureView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GlTextureView.class})
public class MixinGlTextureView {
   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   private void corz$flushBeforeClose(CallbackInfo callbackInfo) {
      CorzRoundedUtils.updateRenderThread();
   }
}
