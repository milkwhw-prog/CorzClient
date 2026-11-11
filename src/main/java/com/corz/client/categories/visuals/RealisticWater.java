package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.WaterCorzUtils;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.visuals.CustomSky;
import com.corz.client.utils.IconsPngType;

public class RealisticWater extends Module {
   private long waterWavesTime;
   private static RealisticWater singleton;
   private float smoothedWaveAmount;
   private float wavesAmount;
   private float waterWavesAmount;
   private final IntegerSetting integerHelper = new IntegerSetting("Reflections", "How strongly the water mirrors the sky. Higher = shinier water.", 70, 0, 100);
   private final BooleanSetting booleanBase = new BooleanSetting(
      "Reflect Custom Sky", "While Custom Sky is on, the water mirrors the sky you picked. Off = the normal sky, sun and moon.", true
   );
   private float fadeProgress;
   private final IntegerSetting clarity;
   private long realisticWaterValue;
   private final IntegerSetting waves = new IntegerSetting("Waves", "Size of the moving waves. 0 = a calm mirror.", 55, 0, 100);
   private float realisticWaterAmount;

   public static RealisticWater getInstance() {
      RealisticWater singleton2 = singleton;
      return singleton2 != null ? singleton2 : (RealisticWater)ModuleRegistry.getModule(RealisticWater.class);
   }

   public void handleWavesAmount(long waterWavesTime) {
      float waterWavesAmount = this.integerHelper.getValue() / 100.0F;
      float amount = this.waves.getValue() / 100.0F;
      float amount2 = this.clarity.getValue() / 100.0F;
      CustomSky customSky = CustomSky.getCustomSky();
      float amount3 = this.booleanBase.isEnabled() && customSky != null ? customSky.getMaxTime() : 0.0F;
      if (!(this.waterWavesAmount < 0.0F) && this.waterWavesTime >= 0L) {
         double wavesValue = Math.max(0.0, Math.min((waterWavesTime - this.waterWavesTime) / 1.0E9, 0.25));
         this.waterWavesTime = waterWavesTime;
         float wavesFactor = (float)(1.0 - Math.exp(-wavesValue / 0.16));
         this.waterWavesAmount = this.waterWavesAmount + (waterWavesAmount - this.waterWavesAmount) * wavesFactor;
         this.realisticWaterAmount = this.realisticWaterAmount + (amount - this.realisticWaterAmount) * wavesFactor;
         this.smoothedWaveAmount = this.smoothedWaveAmount + (amount2 - this.smoothedWaveAmount) * wavesFactor;
         this.wavesAmount = this.wavesAmount + (amount3 - this.wavesAmount) * wavesFactor;
         if (Math.abs(amount3 - this.wavesAmount) < 0.002F) {
            this.wavesAmount = amount3;
         }
      } else {
         this.waterWavesAmount = waterWavesAmount;
         this.realisticWaterAmount = amount;
         this.smoothedWaveAmount = amount2;
         this.wavesAmount = amount3;
         this.waterWavesTime = waterWavesTime;
      }
   }

   @Override
   public void onDisable() {
      this.realisticWaterValue = System.nanoTime();
   }

   @Override
   public void onEnable() {
      this.realisticWaterValue = System.nanoTime();
   }

   public float getRealisticWaterAmount() {
      return this.realisticWaterAmount;
   }

   public float getMaxTime(long nanoTime) {
      if (this.realisticWaterValue < 0L) {
         this.realisticWaterValue = nanoTime;
      }

      long realisticWaterValue = Math.max(0L, Math.min(nanoTime - this.realisticWaterValue, 250000000L));
      this.realisticWaterValue = nanoTime;
      boolean realisticWaterFlag = this.isEnabled() && mc.world != null && mc.player != null && !WaterCorzUtils.isScanFlag();
      float realisticWaterValue2 = (float)realisticWaterValue * 2.2222222E-9F;
      this.fadeProgress = realisticWaterFlag ? Math.min(1.0F, this.fadeProgress + realisticWaterValue2) : Math.max(0.0F, this.fadeProgress - realisticWaterValue2);
      return this.fadeProgress * this.fadeProgress * (3.0F - 2.0F * this.fadeProgress);
   }

   public float getMax() {
      return Math.max(0.0F, this.waterWavesAmount);
   }

   public float getAmount() {
      return this.smoothedWaveAmount;
   }

   @Override
   protected void onReset() {
      WaterCorzUtils.handleScanChunk(mc);
   }

   public float getWavesAmount() {
      return this.wavesAmount;
   }

   public RealisticWater() {
      super("Realistic Water", IconsPngType.VISUAL);
      this.clarity = new IntegerSetting("Clarity", "How see-through the water stays. Lower = deeper, darker water.", 70, 0, 100);
      this.realisticWaterValue = -1L;
      this.waterWavesTime = -1L;
      this.waterWavesAmount = -1.0F;
      this.addSettings(new ConfigSetting[]{this.integerHelper, this.booleanBase, this.waves, this.clarity});
      singleton = this;
      WaterCorzUtils.updateRegisterChunk();
   }
}
