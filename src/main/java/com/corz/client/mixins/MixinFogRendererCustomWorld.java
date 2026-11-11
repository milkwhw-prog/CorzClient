package com.corz.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.nio.ByteBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.visuals.CustomWorld;

@Mixin({FogRenderer.class})
public class MixinFogRendererCustomWorld {
   @Unique
   private static Vector4f corz$color(int corzcolorValue) {
      return new Vector4f((corzcolorValue >> 16 & 0xFF) / 255.0F, (corzcolorValue >> 8 & 0xFF) / 255.0F, (corzcolorValue & 0xFF) / 255.0F, (corzcolorValue >>> 24 & 0xFF) / 255.0F);
   }

   @Inject(
      method = {"method_62185(Lnet/minecraft/class_4184;FLnet/minecraft/class_638;IF)Lorg/joml/Vector4f;"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void corz$fogColor(Camera camera, float corzfogColorValue, ClientWorld clientWorld, int corzfogColorValue2, float corzfogColorValue3, CallbackInfoReturnable<Vector4f> callbackInfoReturnable) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         callbackInfoReturnable.setReturnValue(corz$color(cw.getFogColor()));
      }
   }

   @WrapOperation(
      method = {"method_3211(Lnet/minecraft/class_4184;ILnet/minecraft/class_9779;FLnet/minecraft/class_638;)Lorg/joml/Vector4f;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_758;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
      )},
      require = 0
   )
   private void corz$worldFog(
      FogRenderer fogRenderer,
      ByteBuffer byteBuffer,
      int corzworldFogValue,
      Vector4f vector4f,
      float corzworldFogValue2,
      float corzworldFogValue3,
      float corzworldFogValue4,
      float corzworldFogValue5,
      float corzworldFogValue6,
      float corzworldFogValue7,
      Operation<Void> operation
   ) {
      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null && cw.isBase()) {
         float end = cw.getFogEnd();
         float start = Math.min(cw.getAmount(), end);
         operation.call(new Object[]{fogRenderer, byteBuffer, corzworldFogValue, corz$color(cw.getFogColor()), start, end, start, end, end, end});
      } else {
         operation.call(
            new Object[]{fogRenderer, byteBuffer, corzworldFogValue, vector4f, corzworldFogValue2, corzworldFogValue3, corzworldFogValue4, corzworldFogValue5, corzworldFogValue6, corzworldFogValue7}
         );
      }
   }
}
