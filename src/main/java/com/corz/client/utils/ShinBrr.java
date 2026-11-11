package com.corz.client.utils;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ForeShinUtils;

public final class ShinBrr {
   private final boolean shinBrrActive;
   final int tintColor;
   final LoftLatheFlag.Region loftRegion;

   float getFlag(float shinBrrValue) {
      return this.loftRegion.getMax(this.shinBrrActive ? 0.5F : shinBrrValue);
   }

   ShinBrr(LoftLatheFlag.Region region, int color, boolean active) {
      this.loftRegion = region;
      this.tintColor = color;
      this.shinBrrActive = active;
   }

   public ShinBrr getFlagForFloat(float shinBrrValue) {
      return new ShinBrr(this.loftRegion, ForeShinUtils.getValueForIntFloat(this.tintColor, shinBrrValue), this.shinBrrActive);
   }

   float getFlagLoftRegion(float shinBrrValue) {
      return this.loftRegion.getMaxForFloat(this.shinBrrActive ? 0.5F : shinBrrValue);
   }

   public ShinBrr getFlag(int shinBrrValue) {
      return new ShinBrr(this.loftRegion, ForeShinUtils.getValueForInt(shinBrrValue), this.shinBrrActive);
   }
}
