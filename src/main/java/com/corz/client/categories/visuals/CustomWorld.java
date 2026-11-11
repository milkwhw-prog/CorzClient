package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.QueueCommand;
import net.minecraft.util.math.MathHelper;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class CustomWorld extends Module {
   private final EnumSetting enumHelper;
   private final DoubleSetting doubleHelper;
   private final BooleanSetting booleanBase;
   private float customWorldAmount;
   private final EnumSetting weather;
   private final ColorSetting colorBase;
   private final DoubleSetting customRain;
   private final ColorSetting worldTint;
   private final BooleanSetting skyCustom = new BooleanSetting("Sky Custom", "Replace the sky with a solid colour and hide stars / sun / moon", true);
   private final DoubleSetting fogStart;
   private final DoubleSetting customTime;
   private final BooleanSetting customLightColor;
   private final DoubleSetting fogEnd;
   private final ColorSetting fogColor;

   public int getColorBase() {
      return this.worldTint.getColor();
   }

   public float getRainySunny() {
      if (this.isWorldPlayer() && !this.weather.getCurrentOption().equals("None")) {
         String formatText = this.weather.getCurrentOption();

         return switch (formatText) {
            case "Sunny" -> 0.0F;
            case "Rainy" -> 1.0F;
            case "Thunder" -> 1.0F;
            case "Snowy" -> 0.9F;
            case "Custom" -> (float)this.customRain.getValue();
            default -> -1.0F;
         };
      } else {
         return -1.0F;
      }
   }

   public float getRainySunnyWeather() {
      if (this.isWorldPlayer() && !this.weather.getCurrentOption().equals("None")) {
         String formatText = this.weather.getCurrentOption();

         return switch (formatText) {
            case "Thunder" -> 1.0F;
            case "Custom" -> (float)this.doubleHelper.getValue();
            case "Sunny", "Rainy", "Snowy" -> 0.0F;
            default -> -1.0F;
         };
      } else {
         return -1.0F;
      }
   }

   public float getAmount() {
      return (float)this.fogStart.getValue();
   }

   public int getFogColor() {
      return this.fogColor.getColor();
   }

   public static CustomWorld getCustomWorld() {
      return (CustomWorld)ModuleRegistry.getModule(CustomWorld.class);
   }

   public boolean isBase() {
      return this.isWorldPlayer() && this.booleanBase.isEnabled();
   }

   public boolean isCustomLightColor() {
      return this.isWorldPlayer() && this.customLightColor.isEnabled();
   }

   public boolean isSkyCustom() {
      return this.isWorldPlayer() && this.skyCustom.isEnabled();
   }

   public CustomWorld() {
      super("CustomWorld", IconsPngType.VISUAL);
      this.colorBase = new ColorSetting("Sky Color", "Solid sky colour", -14811076);
      this.customLightColor = new BooleanSetting("Custom Light Color", "Flat-tint the world lightmap", true);
      this.worldTint = new ColorSetting("World Tint", "Lightmap tint colour", -3631361);
      this.enumHelper = new EnumSetting("Time", "Forced time of day", "None", "Day", "Midnight", "Night", "Custom", "None");
      this.customTime = new DoubleSetting("Custom Time", "Custom time in ticks (0-24000)", 1000.0, 0.0, 24000.0, 100.0);
      this.weather = new EnumSetting("Weather", "Forced weather", "None", "Sunny", "Rainy", "Thunder", "Snowy", "Custom", "None");
      this.customRain = new DoubleSetting("Custom Rain", "Custom rain gradient", 0.0, 0.0, 1.0, 0.1);
      this.doubleHelper = new DoubleSetting("Custom Thunder", "Custom thunder gradient", 0.0, 0.0, 1.0, 0.1);
      this.booleanBase = new BooleanSetting("Fog", "Enable custom fog colour + distance", true);
      this.fogColor = new ColorSetting("Fog Color", "Fog colour", -20561);
      this.fogEnd = new DoubleSetting("Fog End", "Far fog distance (blocks)", 64.0, 4.0, 512.0, 4.0);
      this.fogStart = new DoubleSetting("Fog Start", "Near fog distance (blocks)", 32.0, 0.0, 512.0, 4.0);
      this.customWorldAmount = 0.0F;
      this.addSettings(
         new ConfigSetting[]{
            this.skyCustom,
            this.colorBase,
            this.customLightColor,
            this.worldTint,
            this.enumHelper,
            this.customTime,
            this.weather,
            this.customRain,
            this.doubleHelper,
            this.booleanBase,
            this.fogColor,
            this.fogEnd,
            this.fogStart
         }
      );
   }

   private boolean isWorldPlayer() {
      return this.isEnabled() && mc.world != null && mc.player != null;
   }

   private long getMidnightNight() {
      String formatText = this.enumHelper.getCurrentOption();

      return switch (formatText) {
         case "Day" -> 1000L;
         case "Midnight" -> 18000L;
         case "Night" -> 12300L;
         default -> -1L;
      };
   }

   public long getValueJ(long valueJ) {
      if (!this.isWorldPlayer() || this.enumHelper.getCurrentOption().equals("None")) {
         return valueJ;
      } else {
         return this.enumHelper.getCurrentOption().equals("Custom") ? (long)this.customTime.getValue() : (long)this.customWorldAmount;
      }
   }

   public float getFogEnd() {
      return (float)this.fogEnd.getValue();
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      long midnightNight = this.getMidnightNight();
      if (midnightNight >= 0L) {
         float tickProgress = mc.getRenderTickCounter().getTickProgress(true);
         this.customWorldAmount = MathHelper.lerp(tickProgress, this.customWorldAmount, this.customWorldAmount + ((float)midnightNight - this.customWorldAmount) * 0.01F);
      }
   }
}
