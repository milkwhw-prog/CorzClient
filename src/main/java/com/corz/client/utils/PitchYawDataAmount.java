package com.corz.client.utils;

import java.util.Locale;

public record PitchYawDataAmount(
   int teleportId,
   boolean absolute,
   int relativeFlags,
   double beforeX,
   double beforeY,
   double beforeZ,
   float beforeYaw,
   float beforePitch,
   double afterX,
   double afterY,
   double afterZ,
   float afterYaw,
   float afterPitch
) {

   public double getAmount() {
      return this.afterZ - this.beforeZ;
   }

   public double getAmountAfterY() {
      return this.afterY - this.beforeY;
   }

   public boolean isFlag() {
      return this.getAmountDouble() >= 0.05 || Math.abs(this.getYaw()) >= 0.5 || Math.abs(this.getPitch()) >= 0.5;
   }

   public double getAmountDouble() {
      return Math.sqrt(this.getAmountAfterX() * this.getAmountAfterX() + this.getAmountAfterY() * this.getAmountAfterY() + this.getAmount() * this.getAmount());
   }

   public double getAmountAfterX() {
      return this.afterX - this.beforeX;
   }

   static double getAmount(double deg) {
      deg %= 360.0;
      if (deg >= 180.0) {
         deg -= 360.0;
      }

      if (deg < -180.0) {
         deg += 360.0;
      }

      return deg;
   }

   public double getYaw() {
      return getAmount(this.afterYaw - this.beforeYaw);
   }

   public static PitchYawDataAmount.Reaction getObserveInvalidate(PitchYawDataAmount c, boolean preciseActionActive) {
      return c != null && c.isFlag() && preciseActionActive ? PitchYawDataAmount.Reaction.INVALIDATE_AND_REPLAN : PitchYawDataAmount.Reaction.OBSERVE;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "teleportId=%d absolute=%s flags=%d beforePos=%.3f,%.3f,%.3f afterPos=%.3f,%.3f,%.3f delta=%.3f,%.3f,%.3f distance=%.3f beforeYawPitch=%.1f,%.1f afterYawPitch=%.1f,%.1f meaningful=%s",
         this.teleportId,
         this.absolute,
         this.relativeFlags,
         this.beforeX,
         this.beforeY,
         this.beforeZ,
         this.afterX,
         this.afterY,
         this.afterZ,
         this.getAmountAfterX(),
         this.getAmountAfterY(),
         this.getAmount(),
         this.getAmountDouble(),
         this.beforeYaw,
         this.beforePitch,
         this.afterYaw,
         this.afterPitch,
         this.isFlag()
      );
   }

   public double getPitch() {
      return this.afterPitch - this.beforePitch;
   }

   public static enum Reaction {
      OBSERVE,
      INVALIDATE_AND_REPLAN;
   }
}
