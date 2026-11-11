package com.corz.client.utils;
import com.corz.client.utils.DensityBreachType;

public record SwapPreferenceData(
   boolean maces, boolean smashOnly, double minFall, DensityBreachType preference, boolean stunSlam, boolean axes, boolean swords, boolean swapBack
) {

   public SwapPreferenceData(
      boolean maces, boolean smashOnly, double minFall, DensityBreachType preference, boolean stunSlam, boolean axes, boolean swords, boolean swapBack
   ) {
      if (preference == null) {
         preference = DensityBreachType.ANY;
      }

      if (!(minFall >= 1.5)) {
         minFall = 1.5;
      }

      this.maces = maces;
      this.smashOnly = smashOnly;
      this.minFall = minFall;
      this.preference = preference;
      this.stunSlam = stunSlam;
      this.axes = axes;
      this.swords = swords;
      this.swapBack = swapBack;
   }

   public static SwapPreferenceData getDataForFlag(boolean swapBack) {
      return new SwapPreferenceData(false, true, 1.5, DensityBreachType.ANY, false, true, false, swapBack);
   }
}
