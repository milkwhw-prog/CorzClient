package com.corz.client.utils;

public final class JumpCircleRules {
   public static final String JUMP = "Jump";
   public static final String BOTH = "Both";
   public static final String RING = "Ring";
   public static final String BLOCK_WAVE = "Block Wave";
   public static final String MODE_BOTH = "Both";
   public static final String LAND = "Land";

   private JumpCircleRules() {
   }

   public static boolean canFlag(boolean jumpCircleFlag, boolean onGround, int jumpCircleLimit) {
      return !jumpCircleFlag && onGround && jumpCircleLimit >= 4;
   }

   public static boolean canWaveBlock(String waveBlockText) {
      return !"Block Wave".equals(waveBlockText);
   }

   public static boolean canFlag(double jumpCircleValue, double vecHashValue, double jumpCircleValue3, double jumpCircleValue4, double jumpCircleValue5, double jumpCircleValue6) {
      if (!Double.isNaN(jumpCircleValue) && !Double.isNaN(vecHashValue) && !Double.isNaN(jumpCircleValue3)) {
         double jumpCircleFactor = jumpCircleValue - jumpCircleValue4;
         double spotValue = vecHashValue - jumpCircleValue5;
         double jumpCircleFactor2 = jumpCircleValue3 - jumpCircleValue6;
         return jumpCircleFactor * jumpCircleFactor + jumpCircleFactor2 * jumpCircleFactor2 <= 16.0 && Math.abs(spotValue) <= 4.0;
      } else {
         return false;
      }
   }

   public static boolean canFlag(boolean jumpCircleFlag, boolean onGround, double jumpCircleLimit) {
      return jumpCircleFlag && !onGround && jumpCircleLimit > 0.1;
   }

   public static double getAmount(double jumpCircleValue) {
      if (!(jumpCircleValue < 0.0)) {
         return 0.35;
      } else {
         double jumpCircleLimit = -jumpCircleValue / 0.67;
         return jumpCircleLimit < 0.35 ? 0.35 : (jumpCircleLimit > 1.0 ? 1.0 : jumpCircleLimit);
      }
   }

   public static boolean canLandBoth(String landBothText, boolean landBothFlag, boolean landBothFlag2) {
      boolean landBothFlag3 = !"Land".equals(landBothText);
      boolean landBothFlag4 = "Land".equals(landBothText) || "Both".equals(landBothText);
      return landBothFlag && landBothFlag3 || landBothFlag2 && landBothFlag4;
   }

   public static boolean canBothWave(String bothWaveText) {
      return "Block Wave".equals(bothWaveText) || "Both".equals(bothWaveText);
   }
}
