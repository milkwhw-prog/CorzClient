package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class WeatherChanger extends Module {
   private final EnumSetting enumHelper = new EnumSetting("Weather", "Which weather to force on the client.", "Clear", "Clear", "Rain", "Thunder");

   public static WeatherChanger getWeatherChanger() {
      return (WeatherChanger)ModuleRegistry.getModule(WeatherChanger.class);
   }

   public WeatherChanger() {
      super("WeatherChanger", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.enumHelper});
   }

   public float getClearEnum() {
      if (!this.isEnabled()) {
         return -1.0F;
      } else {
         return "Clear".equals(this.enumHelper.getCurrentOption()) ? 0.0F : 1.0F;
      }
   }

   public float getThunderEnum() {
      if (!this.isEnabled()) {
         return -1.0F;
      } else {
         return "Thunder".equals(this.enumHelper.getCurrentOption()) ? 1.0F : 0.0F;
      }
   }
}
