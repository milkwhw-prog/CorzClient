package com.corz.client.mixins;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.MotionBlur;

@Mixin({GameRenderer.class})
public abstract class MixinGameRendererMotionBlur {
   @Inject(
      method = {"method_3188(Lnet/minecraft/class_9779;)V"},
      at = {@At("TAIL")}
   )
   private void corz$motionBlurWorldDrawn(RenderTickCounter renderTickCounter, CallbackInfo callbackInfo) {
      MotionBlur.updateFlag();
   }

   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_758;method_71108()V"
      )}
   )
   private void corz$motionBlurBeforeHud(RenderTickCounter renderTickCounter, boolean corzmotionBlurFlag, CallbackInfo callbackInfo) {
      MotionBlur.updateFlagInstance();
   }
}
