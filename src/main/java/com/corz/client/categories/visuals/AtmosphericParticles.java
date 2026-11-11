package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.RenderAtmoUtils.Settings;
import com.corz.client.utils.RenderAtmoUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AtmosphericParticles extends Module implements Settings {
   public static final String SNOW = "Snow";
   public static final String ROSE_PETALS = "Rose Petals";
   private final EnumSetting enumHelper = new EnumSetting("Preset", "What drifts down through the open sky around you.", "Snow", "Snow", "Rose Petals");
   private final IntegerSetting integerHelper = new IntegerSetting("Amount", "How much falls around you, in percent. Higher is denser.", 50, 10, 100, 5);
   private final IntegerSetting windInteger = new IntegerSetting("Wind", "How strongly the breeze carries it, in percent. 0 is a calm day.", 35, 0, 100, 5);

   public AtmosphericParticles() {
      super("Atmospheric Particles", IconsPngType.VISUAL);
      this.addSettings(new ConfigSetting[]{this.enumHelper, this.integerHelper, this.windInteger});
      RenderAtmoUtils.handleInstallRegister(this);
   }

   @Override
   public int getRosePetals() {
      return "Rose Petals".equals(this.enumHelper.getCurrentOption()) ? 1 : 0;
   }

   @Override
   public boolean isFlag() {
      return this.isEnabled();
   }

   @Override
   public float getInteger() {
      return this.windInteger.getValue() / 100.0F;
   }

   @Override
   public float getIntegerHelper() {
      return this.integerHelper.getValue() / 100.0F;
   }
}
