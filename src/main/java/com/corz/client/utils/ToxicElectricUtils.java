package com.corz.client.utils;

import com.corz.client.render.HalloweenMatrixUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.SnowVertexUtils;
import java.util.Arrays;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.HalloweenChristmasType;

public final class ToxicElectricUtils {
   public static final ToxicElectricUtils.Palette HALLOWEEN_PALETTE = new ToxicElectricUtils.Palette(
      HalloweenMatrixUtils.BATS_COBWEBS_TEXT, new int[]{-35560, -6599169, -8585393, -15594980}
   );
   public static final ToxicElectricUtils.Palette SPECIAL_EVENT_PALETTE = new ToxicElectricUtils.Palette(
      SnowVertexUtils.JANUARY_SNOW_TEXT, new int[]{-2083766, -14473, -13722010, -14938100}
   );
   private static final ToxicElectricUtils.Palette[] BASE_PALETTES = new ToxicElectricUtils.Palette[]{
      new ToxicElectricUtils.Palette("Cyberpunk", new int[]{-16718337, -53609, -8704001, -16052186}),
      new ToxicElectricUtils.Palette("Ultraviolet", new int[]{-5232897, -2096897, -9830156, -15466450}),
      new ToxicElectricUtils.Palette("Toxic", new int[]{-12976364, -3342592, -16711773, -16113907}),
      new ToxicElectricUtils.Palette("Inferno", new int[]{-38144, -57826, -15360, -14743552}),
      new ToxicElectricUtils.Palette("Glacier", new int[]{-16725761, -11999745, -8675585, -16246734}),
      new ToxicElectricUtils.Palette("Bloodmoon", new int[]{-65485, -45715, -5242848, -15073272}),
      new ToxicElectricUtils.Palette("Aurora", new int[]{-16711738, -12916481, -9806593, -16507100}),
      new ToxicElectricUtils.Palette("Bubblegum", new int[]{-45096, -28704, -4960257, -13956289}),
      new ToxicElectricUtils.Palette("Limelight", new int[]{-4784384, -1507492, -16711813, -15458560}),
      new ToxicElectricUtils.Palette("Sunset", new int[]{-65426, -38144, -5230849, -15073246}),
      new ToxicElectricUtils.Palette("Electric", new int[]{-13800449, -16727297, -10716161, -16379346}),
      new ToxicElectricUtils.Palette("Nebula", new int[]{-6467841, -3703297, -10872632, -15335373}),
      new ToxicElectricUtils.Palette("Goldrush", new int[]{-15360, -8090, -25088, -14411520}),
      new ToxicElectricUtils.Palette("Emerald", new int[]{-16718454, -13893712, -16734101, -16505830}),
      new ToxicElectricUtils.Palette("Plasma", new int[]{-53556, -9806593, -16719617, -15335366}),
      new ToxicElectricUtils.Palette("Crimson", new int[]{-51104, -34212, -3145728, -15073274})
   };
   private static final ToxicElectricUtils.Palette[] HALLOWEEN_PALETTES = getCopyOf(HALLOWEEN_PALETTE);
   private static final ToxicElectricUtils.Palette[] copyOf = getCopyOf(SPECIAL_EVENT_PALETTE);
   public static final int motionTexturesValue = BASE_PALETTES.length + 1;

   public static int getInstance() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton == null) {
         return -1;
      } else {
         int toxicElectricValue = singleton.main.getColor() & 16777215;
         ToxicElectricUtils.Palette[] crimson = getCrimson();

         for (int i = 0; i < crimson.length; i++) {
            if ((crimson[i].getColors() & 16777215) == toxicElectricValue) {
               return i;
            }
         }

         return -1;
      }
   }

   private static ToxicElectricUtils.Palette[] getCopyOf(ToxicElectricUtils.Palette palette) {
      ToxicElectricUtils.Palette[] copyOf = Arrays.copyOf(BASE_PALETTES, BASE_PALETTES.length + 1);
      copyOf[BASE_PALETTES.length] = palette;
      return copyOf;
   }

   public static ToxicElectricUtils.Palette[] getCrimson() {
      return switch (HalloweenChristmasType.getTimeInstance()) {
         case HALLOWEEN -> HALLOWEEN_PALETTES;
         case CHRISTMAS -> copyOf;
         case NONE -> BASE_PALETTES;
      };
   }

   private static double getAmount(int toxicElectricValue) {
      int toxicElectricBound = toxicElectricValue >> 16 & 0xFF;
      int aggroPreemptBound = toxicElectricValue >> 8 & 0xFF;
      int rocketsBuyBound = toxicElectricValue & 0xFF;
      return Math.max(toxicElectricBound, Math.max(aggroPreemptBound, rocketsBuyBound)) / 255.0;
   }

   public static int[] getColors() {
      ToxicElectricUtils.Palette[] crimson = getCrimson();
      int instance = getInstance();
      return instance >= 0 && instance < crimson.length ? crimson[instance].colors() : null;
   }

   private ToxicElectricUtils() {
   }

   private static double getAmountForInt(int toxicElectricValue) {
      int toxicElectricBound = toxicElectricValue >> 16 & 0xFF;
      int aggroPreemptBound = toxicElectricValue >> 8 & 0xFF;
      int rocketsBuyBound = toxicElectricValue & 0xFF;
      int toxicElectricDivisor = Math.max(toxicElectricBound, Math.max(aggroPreemptBound, rocketsBuyBound));
      int min2 = Math.min(toxicElectricBound, Math.min(aggroPreemptBound, rocketsBuyBound));
      return toxicElectricDivisor == 0 ? 0.0 : (double)(toxicElectricDivisor - min2) / toxicElectricDivisor;
   }

   public static void handleBaseInstance(ToxicElectricUtils.Palette palette) {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton != null && palette != null) {
         MatrixEspUtils.updateFlag();
         singleton.main.setColor(0xFF000000 | palette.getColors() & 16777215);
         singleton.ambientBackground.handle(true);
         MatrixEspUtils.updateFlagTime();
      }
   }

   public record Palette(String name, int[] colors) {
      public int getColors() {
         return this.colors[this.getColorsInt()];
      }

      public int getColorsInt() {
         int colors = 0;
         double colorsValue = -1.0;

         for (int i = 0; i < this.colors.length; i++) {
            double colorsLimit = ToxicElectricUtils.getAmountForInt(this.colors[i]) * 0.68 + ToxicElectricUtils.getAmount(this.colors[i]) * 0.32;
            if (colorsLimit > colorsValue) {
               colorsValue = colorsLimit;
               colors = i;
            }
         }

         return colors;
      }
   }
}
