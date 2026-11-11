package com.corz.client.mixins;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.utils.WaterCorzUtils;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRendererWater {
   @Inject(
      method = {"method_3295(IIIZ)V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void corz$waterSectionDirty(int corzwaterSectionValue, int corzwaterSectionValue2, int corzwaterSectionValue3, boolean corzwaterSectionFlag, CallbackInfo callbackInfo) {
      WaterCorzUtils.handleChunkHash(corzwaterSectionValue, corzwaterSectionValue2, corzwaterSectionValue3);
   }
}
