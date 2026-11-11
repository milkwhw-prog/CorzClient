package com.corz.client.mixins;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.corz.client.categories.misc.WeatherChanger;
import com.corz.client.categories.visuals.CustomWorld;

@Mixin({World.class})
public class MixinWorldGradient {
   @Inject(
      method = {"method_8430(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$rainGradient(float corzrainGradientValue, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      WeatherChanger wc = WeatherChanger.getWeatherChanger();
      if (wc != null) {
         float wg = wc.getClearEnum();
         if (wg != -1.0F) {
            callbackInfoReturnable.setReturnValue(wg);
            return;
         }
      }

      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null) {
         float g = cw.getRainySunny();
         if (g != -1.0F) {
            callbackInfoReturnable.setReturnValue(g);
         }
      }
   }

   @Inject(
      method = {"method_8478(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void corz$thunderGradient(float corzthunderGradientValue, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      WeatherChanger wc = WeatherChanger.getWeatherChanger();
      if (wc != null) {
         float wg = wc.getThunderEnum();
         if (wg != -1.0F) {
            callbackInfoReturnable.setReturnValue(wg);
            return;
         }
      }

      CustomWorld cw = CustomWorld.getCustomWorld();
      if (cw != null) {
         float g = cw.getRainySunnyWeather();
         if (g != -1.0F) {
            callbackInfoReturnable.setReturnValue(g);
         }
      }
   }
}
