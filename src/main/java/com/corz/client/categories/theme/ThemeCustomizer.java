package com.corz.client.categories.theme;

import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.render.HalloweenMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.SnowVertexUtils;
import ai.auth.Module;
import com.corz.client.utils.HalloweenChristmasType;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PaletteBase;
public class ThemeCustomizer extends Module {
   public final DoubleSetting opacity;
   public final BooleanSetting booleanBase;
   public final DoubleSetting radius;
   public final DoubleSetting doubleHelper;
   public static boolean equals = true;
   public final BooleanSetting musicSongBooleanBase;
   public final ColorSetting main;
   public static boolean modernFlag = false;
   public final DoubleSetting christmasGifts;
   public final BooleanSetting ambientBackground;
   public static boolean booleanValue = true;
   public final PaletteBase paletteBase = new PaletteBase("Palettes", "Curated theme presets \u2014 click one to theme the whole client");
   public final BooleanSetting themeCustomizerBooleanBase;
   public final ColorSetting secondary;
   public static ThemeCustomizer singleton;
   public final BooleanSetting frostedBlur;
   public final ColorSetting primary;
   public final EnumSetting enumHelper;
   public final DoubleSetting intensity;
   public final BooleanSetting ambientAnimation;
   public final DoubleSetting seasonToastYear;
   public final BooleanSetting seeThroughGui;
   public final EnumSetting toggleStyle;
   public final DoubleSetting speed;
   public final EnumSetting ambientColorMode;

   @Override
   public void onConfigLoaded() {
      this.updateEnum();
   }

   public ThemeCustomizer() {
      super("Theme Customizer", IconsPngType.THEME);
      this.main = new ColorSetting("Main Color", "Accent color for sliders, icons, and highlights", -35560);
      this.toggleStyle = new EnumSetting("Toggle Style", "Switch design used for on/off toggles", "Modern", "Modern", "Classic");
      this.seeThroughGui = new BooleanSetting(
         "See-Through GUI", "Glass ClickGUI \u2014 the window and all module boxes turn translucent so the game shows through", false
      );
      this.frostedBlur = new BooleanSetting("Frosted Blur", "Blur the game behind the See-Through window for a frosted-glass look (needs See-Through on)", true);
      this.ambientBackground = new BooleanSetting("Ambient Background", "Soft themed glow behind the ClickGUI window", true);
      this.intensity = new DoubleSetting("Ambient Intensity", "Brightness of the glow blooms", 0.67, 0.0, 1.0, 0.05);
      this.opacity = new DoubleSetting("Ambient Opacity", "Overall opacity of the ambient layer", 0.6, 0.0, 1.0, 0.05);
      this.radius = new DoubleSetting("Ambient Radius", "Size of the glow blooms", 1.34, 0.5, 2.0, 0.05);
      this.ambientColorMode = new EnumSetting("Ambient Color Mode", "Theme accent, one custom colour, or two", "Theme", "Theme", "Custom", "Two Color");
      this.primary = new ColorSetting("Ambient Primary Color", "First glow colour (Custom / Two Color modes)", -16765185);
      this.secondary = new ColorSetting("Ambient Secondary Color", "Second glow colour (Two Color mode)", -6595841);
      this.ambientAnimation = new BooleanSetting("Ambient Animation", "Very slow drift / pulse of the glow", true);
      this.speed = new DoubleSetting("Ambient Animation Speed", "Drift speed (slow by design)", 2.0, 0.1, 2.0, 0.1);
      this.musicSongBooleanBase = new BooleanSetting(
         HalloweenMatrixUtils.BATS_COBWEBS_TEXT, "Flying bats and cobwebs in the ClickGUI (ends automatically on 2 November)", true
      );
      this.enumHelper = new EnumSetting("Theme Season", "Which seasonal theme to show (Auto follows the date)", "Auto", "Auto", "Halloween", "Christmas");
      this.themeCustomizerBooleanBase = new BooleanSetting(
         SnowVertexUtils.JANUARY_SNOW_TEXT, "Snowfall, fairy lights and frost in the ClickGUI (ends automatically on 2 January)", true
      );
      this.booleanBase = new BooleanSetting("Theme Music", "Plays the seasonal theme song softly while the ClickGUI is open", true);
      this.doubleHelper = new DoubleSetting(
         "Theme Music Volume", "Loudness of the theme song (Minecraft's Master and UI sliders apply too)", 0.4, 0.0, 1.0, 0.05
      );
      this.seasonToastYear = new DoubleSetting("Season Toast Year", "Year the seasonal theme was last announced", 0.0, 0.0, 9999.0, 1.0);
      this.christmasGifts = new DoubleSetting("Christmas Gifts", "Presents Santa has left under the tree this season", 0.0, 0.0, 999999.0, 1.0);
      singleton = this;
      this.addSetting(this.paletteBase);
      this.addSetting(this.main);
      this.addSetting(this.toggleStyle);
      this.addSetting(this.seeThroughGui);
      this.addSetting(this.frostedBlur);
      this.addSetting(this.ambientBackground);
      this.addSetting(this.ambientColorMode);
      this.addSetting(this.primary);
      this.addSetting(this.secondary);
      this.addSetting(this.intensity);
      this.addSetting(this.opacity);
      this.addSetting(this.radius);
      this.addSetting(this.ambientAnimation);
      this.addSetting(this.speed);
      this.addSetting(this.enumHelper);
      this.addSetting(this.musicSongBooleanBase);
      this.addSetting(this.themeCustomizerBooleanBase);
      this.addSetting(this.booleanBase);
      this.addSetting(this.doubleHelper);
      this.seasonToastYear.hideFromList();
      this.addSetting(this.seasonToastYear);
      this.christmasGifts.hideFromList();
      this.addSetting(this.christmasGifts);
      this.setEnabled(true);
      equals = this.toggleStyle.getCurrentOption().equals("Modern");
      modernFlag = this.seeThroughGui.isEnabled();
      booleanValue = this.frostedBlur.isEnabled();
      MatrixEspUtils.updateFlagTime();
   }

   public void updateEnum() {
      String formatText = this.enumHelper.getCurrentOption();
      String formatHalloweenChristmas = HalloweenChristmasType.formatHalloweenChristmas(formatText, HalloweenChristmasType.isTimeFlag());
      if (!formatHalloweenChristmas.equals(formatText)) {
         this.enumHelper.handle(this.enumHelper.getOptions().indexOf(formatHalloweenChristmas));
      }
   }

   public int getValue() {
      return this.main.getColor();
   }

   @Override
   public void onDisable() {
      this.toggle();
   }

   @Override
   protected void onReset() {
      this.updateEnum();
      equals = this.toggleStyle.getCurrentOption().equals("Modern");
      modernFlag = this.seeThroughGui.isEnabled();
      booleanValue = this.frostedBlur.isEnabled();
      MatrixEspUtils.updateFlagTime();
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }
}
