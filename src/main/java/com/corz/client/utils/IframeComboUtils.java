package com.corz.client.utils;

public final class IframeComboUtils {
   public static final String OK_RESULT = "ok";
   public static final String NONE = "none";
   public static final String HELD = "held";
   public static final String COMBO = "combo";
   public static final int UNUSED_ONE = 1;

   public static boolean canFlag(double iframeComboLimit) {
      return iframeComboLimit > 3.3;
   }

   public static int getValue(boolean iframeComboFlag, int iframeComboValue) {
      return iframeComboFlag && iframeComboValue <= 0 ? 1 : 0;
   }

   public static int[] getValues(int iframeComboValue, double angleFromRotation, double angleDifference) {
      int iframeComboValue4 = iframeComboValue + 1 + getValue(angleDifference);
      return new int[]{iframeComboValue4 + 1 + getValue(angleFromRotation), iframeComboValue4};
   }

   private IframeComboUtils() {
   }

   public static int getValue(double iframeComboValue) {
      if (Double.isNaN(iframeComboValue)) {
         return 0;
      } else {
         double absTurnDeg = Math.abs(iframeComboValue);
         if (absTurnDeg <= 20.0) {
            return 0;
         } else {
            return Double.isInfinite(absTurnDeg) ? (int)Math.ceil(9.0) - 1 : (int)Math.ceil(absTurnDeg / 20.0) - 1;
         }
      }
   }

   public static boolean canFlagForDouble(double iframeComboValue) {
      return Double.isFinite(iframeComboValue) && Math.abs(iframeComboValue) > 30.0;
   }

   public static double getAmount(double iframeComboValue, double groundY) {
      return Double.isFinite(iframeComboValue) && Double.isFinite(groundY) ? Math.hypot(Math.max(0.0, groundY - 1.0), iframeComboValue - 1.62) : Double.POSITIVE_INFINITY;
   }

   public static boolean canFlag(double iframeComboValue, int iframeComboLimit) {
      return Double.isFinite(iframeComboValue) && iframeComboValue <= 3.3 && iframeComboLimit >= 0 && iframeComboLimit <= 8;
   }

   public static int getValueForDouble(double iframeComboValue) {
      return Double.isFinite(iframeComboValue) && Math.abs(iframeComboValue) <= 30.0 ? 0 : 1;
   }

   public static boolean canFlag(double iframeComboValue, double iframeComboValue2, boolean iframeComboFlag) {
      return canFlag(iframeComboValue, iframeComboValue2, iframeComboFlag, false);
   }

   public static boolean canFlag(int iframeComboValue, float routedPartialValue, double iframeComboValue3, boolean iframeComboFlag) {
      return !iframeComboFlag && canFlag(iframeComboValue, routedPartialValue, iframeComboValue3);
   }

   public static boolean canFlag(float iframeComboLimit, float routedPartialValue) {
      return Float.isFinite(iframeComboLimit) && Float.isFinite(routedPartialValue) && routedPartialValue > 0.0F && iframeComboLimit >= routedPartialValue;
   }

   public static boolean canFlag(boolean iframeComboFlag, double iframeComboValue) {
      return !iframeComboFlag && Double.isFinite(iframeComboValue) && iframeComboValue <= 12.0;
   }

   public static boolean canFlag(double iframeComboValue, double distanceTo) {
      return Double.isFinite(iframeComboValue) && Double.isFinite(distanceTo) && distanceTo <= iframeComboValue - 1.5;
   }

   public static boolean canFlag(double iframeComboValue, double iframeComboValue2, int iframeComboValue3, double iframeComboValue4, double iframeComboValue5, int iframeComboValue6) {
      return Math.abs(iframeComboValue3 - iframeComboValue6) <= 1 && Math.hypot(iframeComboValue - iframeComboValue4, iframeComboValue2 - iframeComboValue5) <= 3.0;
   }

   public static boolean canFlag(boolean iframeComboFlag, boolean anchorGlowstone, float iframeComboLimit) {
      return !iframeComboFlag && !anchorGlowstone && !(iframeComboLimit >= 4.0F);
   }

   public static boolean canFlag(double iframeComboLimit, double iframeComboValue, boolean iframeComboFlag, boolean iframeComboFlag2, double iframeComboValue2) {
      if (!Double.isNaN(iframeComboLimit) && !Double.isNaN(iframeComboValue)) {
         double iframeComboValue3 = !iframeComboFlag && !iframeComboFlag2 ? 0.45 : 1.05;
         return iframeComboLimit >= -1.05 - iframeComboValue2 && iframeComboLimit <= iframeComboValue3 + iframeComboValue2 && iframeComboValue <= 2.3 + iframeComboValue2;
      } else {
         return false;
      }
   }

   public static boolean canFlag(boolean iframeComboFlag, double iframeComboLimit, boolean iframeComboFlag2, double iframeComboBound) {
      if (iframeComboFlag && !iframeComboFlag2) {
         return true;
      } else {
         return Double.isFinite(iframeComboLimit) && iframeComboLimit > 0.0 ? !Double.isFinite(iframeComboBound) || iframeComboLimit >= 2.0 * Math.max(0.25, iframeComboBound) : false;
      }
   }

   public static double getMax(double iframeComboBound) {
      return Double.isFinite(iframeComboBound) && !(iframeComboBound <= 0.0) ? Math.max(0.25, Math.min(3.0, iframeComboBound)) : 0.25;
   }

   public static int getValue(long iframeComboBound) {
      long iframeComboValue = Math.max(0L, iframeComboBound) / 10L;
      return (iframeComboValue & 1L) == 0L ? 1 : -1;
   }

   public static int getValue(boolean iframeComboFlag) {
      return iframeComboFlag ? 2 : 4;
   }

   public static boolean canFlag2(double iframeComboValue, double groundY) {
      if (Double.isFinite(iframeComboValue) && Double.isFinite(groundY) && !(iframeComboValue < -2.0)) {
         return iframeComboValue <= 1.5 && getAmountForDouble(iframeComboValue, groundY) <= 3.0 ? true : iframeComboValue <= 3.5 && getAmount(iframeComboValue, groundY) <= 4.25;
      } else {
         return false;
      }
   }

   public static boolean canFlag(double iframeComboLimit, boolean iframeComboFlag, double iframeComboValue, boolean iframeComboFlag2, long iframeComboValue2, boolean iframeComboFlag3) {
      return !iframeComboFlag3 ? canFlag(iframeComboLimit, iframeComboFlag, iframeComboValue, iframeComboFlag2, iframeComboValue2) : iframeComboFlag && !iframeComboFlag2 && Double.isFinite(iframeComboLimit) && iframeComboLimit > -0.5 && iframeComboValue <= 6.0;
   }

   public static boolean isEquals(String equalsText) {
      return "ok".equals(equalsText);
   }

   public static boolean canFlag(int iframeComboLimit, int iframeComboBound) {
      return iframeComboLimit > Math.max(1, iframeComboBound);
   }

   public static int getValue(int iframeComboValue, boolean iframeComboFlag) {
      return Math.max(0, iframeComboValue - 1 + (iframeComboFlag ? 1 : 0));
   }

   public static boolean canFlag3(double iframeComboValue) {
      return iframeComboValue <= 3.4;
   }

   public static boolean canFlag(boolean iframeComboFlag, double iframeComboLimit, double iframeComboBound, boolean iframeComboFlag2) {
      if (iframeComboFlag2) {
         return false;
      } else if (iframeComboFlag) {
         return true;
      } else {
         double iframeComboValue = Double.isFinite(iframeComboBound) ? Math.max(0.25, iframeComboBound) : 0.25;
         return Double.isFinite(iframeComboLimit) && iframeComboLimit >= iframeComboValue;
      }
   }

   public static double getAmount(int iframeComboBound) {
      return 0.17 * Math.max(0, iframeComboBound);
   }

   public static boolean canFlag(int iframeComboLimit, float iframeComboValue, int iframeComboValue2) {
      return canFlag(iframeComboLimit, iframeComboValue) && iframeComboLimit > getMax(iframeComboValue2);
   }

   public static double getAmount(boolean iframeComboFlag, int iframeComboLimit) {
      return iframeComboFlag && iframeComboLimit >= 1 ? 1.0 : 0.5;
   }

   public static boolean canFlagForDoubleFlag(double iframeComboLimit, double iframeComboValue, boolean iframeComboFlag) {
      return !iframeComboFlag && !Double.isNaN(iframeComboLimit) && !Double.isNaN(iframeComboValue) ? iframeComboLimit >= -2.6 && iframeComboLimit <= 0.45 && iframeComboValue <= 2.3 : false;
   }

   public static boolean canFlagForDoubleInt(double iframeComboLimit, int readyIn) {
      if (!(iframeComboLimit > 3.4)) {
         return false;
      } else {
         return readyIn > 0 ? iframeComboLimit <= 6.0 : iframeComboLimit <= 7.5;
      }
   }

   public static boolean canFlag(int iframeComboLimit, long iframeComboLimit2) {
      return iframeComboLimit > 0 && iframeComboLimit2 >= 0L && iframeComboLimit2 <= 3L;
   }

   public static boolean canFlag4(double iframeComboValue) {
      return Double.isFinite(iframeComboValue) && iframeComboValue <= 3.3;
   }

   public static double[] getAmounts(double iframeComboValue, double iframeComboValue2, double iframeComboValue3, double iframeComboValue4) {
      return new double[]{iframeComboValue - iframeComboValue3 * 2.7, iframeComboValue2 - iframeComboValue4 * 2.7};
   }

   public static double getAmountForInt(int iframeComboValue) {
      if (iframeComboValue <= 0) {
         return 0.0;
      } else {
         return switch ((iframeComboValue - 1) % 4) {
            case 0 -> 0.0;
            case 1 -> 90.0;
            case 2 -> -90.0;
            default -> 180.0;
         };
      }
   }

   public static boolean canFlag(double iframeComboLimit, boolean iframeComboFlag, double iframeComboValue, double lvlValue) {
      return iframeComboFlag && Double.isFinite(iframeComboLimit) && iframeComboValue <= 6.0 ? iframeComboLimit >= 0.75 || Double.isFinite(lvlValue) && lvlValue <= 0.08 : false;
   }

   public static boolean canFlag3(double iframeComboValue, double iframeComboLimit) {
      if (!(iframeComboValue <= 0.5)) {
         return false;
      } else {
         return !(iframeComboLimit <= 4.0) ? false : canFlag6(iframeComboValue) || iframeComboLimit >= 2.0;
      }
   }

   public static boolean canFlag5(double iframeComboLimit) {
      return Double.isFinite(iframeComboLimit) && iframeComboLimit >= -0.6 && iframeComboLimit <= 0.45;
   }

   public static boolean canFlag(boolean iframeComboFlag, boolean iframeComboFlag2, double iframeComboValue) {
      return iframeComboFlag && iframeComboFlag2 && iframeComboValue <= 6.0;
   }

   public static boolean canFlag(int iframeComboLimit, boolean iframeComboFlag) {
      return iframeComboFlag && iframeComboLimit >= 3;
   }

   public static double getAmountForDouble(double iframeComboValue, double groundY) {
      if (Double.isFinite(iframeComboValue) && Double.isFinite(groundY)) {
         double iframeComboValue3 = 1.62;
         double deltaZ = Math.max(0.0, Math.max(iframeComboValue - iframeComboValue3, iframeComboValue3 - (iframeComboValue + 1.8)));
         return Math.hypot(Math.max(0.0, groundY - 0.3), deltaZ);
      } else {
         return Double.POSITIVE_INFINITY;
      }
   }

   public static int getValue(int iframeComboLimit, int iframeComboLimit2, int iframeComboBound) {
      if (iframeComboLimit >= 0 && iframeComboLimit < iframeComboLimit2) {
         return iframeComboLimit >= Math.max(2, iframeComboBound) ? iframeComboLimit : -1;
      } else {
         return -1;
      }
   }

   public static int getValue(long iframeComboLimit, int iframeComboBound, long iframeComboLimit2, double groundY, long iframeComboLimit3) {
      if (iframeComboLimit == Long.MIN_VALUE || iframeComboLimit3 < iframeComboLimit) {
         return -1;
      } else if (iframeComboLimit2 != Long.MIN_VALUE && iframeComboLimit2 > iframeComboLimit) {
         return -1;
      } else {
         long iframeComboValue = iframeComboLimit + Math.max(1, iframeComboBound);
         double coverageValue = Double.isFinite(groundY) ? Math.max(0.0, groundY) : 0.0;
         return iframeComboLimit3 > iframeComboValue + (long)Math.ceil(coverageValue) + 2L ? -1 : (int)Math.max(1L, Math.min(16L, iframeComboValue - iframeComboLimit3));
      }
   }

   public static boolean canFlag(int iframeComboValue, boolean iframeComboFlag, float iframeComboLimit, double iframeComboValue2) {
      return iframeComboValue <= 0 && iframeComboFlag && Float.isFinite(iframeComboLimit) && iframeComboLimit >= 0.75F && iframeComboValue2 <= 3.0;
   }

   public static boolean canFlag(int iframeComboLimit, float routedPartialValue, double iframeComboValue) {
      return iframeComboLimit >= 0
         && iframeComboLimit <= 8
         && Float.isFinite(routedPartialValue)
         && !(routedPartialValue > 0.0F)
         && !Double.isNaN(iframeComboValue)
         && iframeComboValue <= 4.5;
   }

   public static int getValueForLong(long iframeComboValue) {
      return iframeComboValue < 30L ? 0 : 1 + (int)Math.min(1000L, (iframeComboValue - 30L) / 30L);
   }

   public static boolean canFlag(double iframeComboValue, boolean iframeComboFlag, boolean iframeComboFlag2) {
      return iframeComboValue <= 4.5 && (iframeComboFlag || iframeComboFlag2);
   }

   public static boolean canFlagForFloat(float iframeComboValue, float routedPartialValue) {
      return Float.isFinite(iframeComboValue) && Float.isFinite(routedPartialValue) ? iframeComboValue - routedPartialValue >= 4.0F : false;
   }

   public static double getAmount(double iframeComboLimit) {
      return !(iframeComboLimit > 3.0) ? 0.0 : (iframeComboLimit - 3.0) / 0.28;
   }

   public static long getTime(long iframeComboLimit, long iframeComboValue) {
      return iframeComboValue != Long.MIN_VALUE && iframeComboLimit >= iframeComboValue ? iframeComboLimit - iframeComboValue : -1L;
   }

   public static boolean canFlag(long iframeComboLimit, int iframeComboValue, int iframeComboLimit2) {
      return iframeComboLimit >= 0L && iframeComboLimit <= iframeComboValue + 2 && iframeComboLimit2 >= iframeComboLimit;
   }

   public static boolean canFlagForInt(int iframeComboLimit, int iframeComboValue) {
      return iframeComboLimit > 0 && iframeComboLimit <= getMax(iframeComboValue);
   }

   public static boolean canFlag(long iframeComboLimit, boolean iframeComboFlag) {
      return iframeComboLimit >= 0L && iframeComboLimit > (iframeComboFlag ? 24 : 100);
   }

   public static boolean canFlag(boolean iframeComboFlag, boolean iframeComboFlag2, boolean iframeComboFlag3) {
      return iframeComboFlag && iframeComboFlag2 && iframeComboFlag3;
   }

   public static int[] getValues(boolean iframeComboFlag, int iframeComboBound, double[] iframeComboValues) {
      int maxTurnDegs = Math.max(1, iframeComboBound);
      int iframeComboCounter = 0;
      int iframeComboTotal = 0;
      int iframeComboValue = -1;
      if (iframeComboFlag) {
         iframeComboTotal += 1 + getValue(getAmount(iframeComboValues, iframeComboCounter++));
         iframeComboValue = iframeComboTotal;
      }

      int iframeComboValue2 = iframeComboTotal + 1 + getValue(getAmount(iframeComboValues, iframeComboCounter++));
      int max3 = Math.max(iframeComboValue2 + 1 + getValue(getAmount(iframeComboValues, iframeComboCounter)), iframeComboValue2 + maxTurnDegs);
      return new int[]{iframeComboValue, iframeComboValue2, max3};
   }

   public static boolean canFlag(boolean iframeComboFlag, String iframeComboText) {
      return iframeComboFlag && iframeComboText == null;
   }

   public static int getValue(int iframeComboValue) {
      return getValues(iframeComboValue, 0.0, 0.0)[0];
   }

   public static boolean canFlag(double iframeComboLimit, double iframeComboValue, boolean iframeComboFlag, boolean iframeComboFlag2) {
      return !Double.isNaN(iframeComboLimit) && !Double.isNaN(iframeComboValue) ? iframeComboLimit >= -1.05 && iframeComboLimit <= (!iframeComboFlag && !iframeComboFlag2 ? 0.45 : 1.05) && iframeComboValue <= 2.3 : false;
   }

   public static boolean canFlag(double iframeComboLimit, boolean iframeComboFlag, double iframeComboValue, boolean iframeComboFlag2, long iframeComboLimit2) {
      return iframeComboFlag && !iframeComboFlag2 && Double.isFinite(iframeComboLimit) && iframeComboValue <= 6.0 ? iframeComboLimit > -0.5 && iframeComboLimit2 >= 0L && iframeComboLimit2 <= 40L : false;
   }

   public static double getAmount(double iframeComboValue, double scaledInstance, boolean iframeComboFlag, double iframeComboLimit, double iframeComboValue4, double eyePitch) {
      double iframeComboValue6 = Double.isFinite(eyePitch) ? Math.abs(eyePitch) : 180.0;
      return iframeComboValue - 1.0 * scaledInstance - (iframeComboFlag ? 0.5 : 0.0) + getAmountDouble(iframeComboLimit, iframeComboValue4) - 0.05 * iframeComboValue6;
   }

   public static boolean canComboHeld(boolean comboHeldFlag, String comboHeldText, double comboHeldLimit) {
      return comboHeldFlag && !"held".equals(comboHeldText) && !"none".equals(comboHeldText) ? isEquals(comboHeldText) || "combo".equals(comboHeldText) || Double.isFinite(comboHeldLimit) && comboHeldLimit > 0.0 : false;
   }

   public static boolean canImmuneStall(String immuneStallText) {
      if (immuneStallText == null) {
         return false;
      } else {
         return switch (immuneStallText) {
            case "reach", "pending", "stall", "using", "immune" -> true;
            default -> false;
         };
      }
   }

   public static double[][] getAmounts(double iframeComboValue, double iframeComboBound, double iframeComboValue2, double iframeComboValue3, double iframeComboValue4, double iframeComboValue5, double iframeComboFactor, double iframeComboFactor2) {
      double iframeComboValue6 = (iframeComboValue + iframeComboValue3) * 0.5;
      double iframeComboValue7 = (iframeComboValue2 + iframeComboValue5) * 0.5;
      double iframeComboValue8 = (iframeComboBound + iframeComboValue4) * 0.5;
      double min9 = Math.min(iframeComboValue4 - 0.1, iframeComboBound + 0.45);
      double maxMinX = Math.max(iframeComboBound, iframeComboValue4 - 0.25);
      double distance = Math.sqrt(iframeComboFactor * iframeComboFactor + iframeComboFactor2 * iframeComboFactor2);
      double iframeComboValue10 = 1.0;
      double iframeComboValue11 = 0.0;
      if (distance > 1.0E-6 && Double.isFinite(distance)) {
         iframeComboValue10 = -iframeComboFactor2 / distance;
         iframeComboValue11 = iframeComboFactor / distance;
      }

      double iframeComboValue12 = iframeComboValue10 * 0.15;
      double iframeComboValue13 = iframeComboValue11 * 0.15;
      return new double[][]{
         {iframeComboValue6, iframeComboValue8, iframeComboValue7},
         {iframeComboValue6, min9, iframeComboValue7},
         {iframeComboValue6, maxMinX, iframeComboValue7},
         {iframeComboValue6 + iframeComboValue12, iframeComboValue8, iframeComboValue7 + iframeComboValue13},
         {iframeComboValue6 - iframeComboValue12, iframeComboValue8, iframeComboValue7 - iframeComboValue13},
         {iframeComboValue6 + iframeComboValue12, min9, iframeComboValue7 + iframeComboValue13},
         {iframeComboValue6 - iframeComboValue12, min9, iframeComboValue7 - iframeComboValue13}
      };
   }

   public static int getValue(boolean iframeComboFlag, double iframeComboValue, boolean iframeComboFlag2, double iframeComboLimit) {
      boolean iframeComboFlag3 = iframeComboFlag && Double.isFinite(iframeComboValue);
      boolean iframeComboFlag4 = iframeComboFlag2 && Double.isFinite(iframeComboLimit) && iframeComboLimit > 0.0;
      if (!iframeComboFlag4 || iframeComboFlag3 && !(iframeComboLimit > iframeComboValue)) {
         return iframeComboFlag3 ? 1 : 0;
      } else {
         return 2;
      }
   }

   public static boolean canFlag(long iframeComboLimit) {
      return iframeComboLimit >= 0L && iframeComboLimit <= 10L;
   }

   public static double getAmountDouble(double iframeComboLimit, double iframeComboValue) {
      if (!Double.isNaN(iframeComboLimit) && !Double.isNaN(iframeComboValue)) {
         double iframeComboTotal = iframeComboLimit < -0.6 ? -1.5 : (iframeComboLimit > 0.45 ? -0.5 : 0.0);
         if (iframeComboValue <= 1.6 && iframeComboLimit >= -0.6) {
            iframeComboTotal += 2.0;
         }

         return iframeComboTotal;
      } else {
         return 0.0;
      }
   }

   public static boolean canFlag(double iframeComboLimit, double iframeComboValue, boolean iframeComboFlag, double iframeComboValue2) {
      return !iframeComboFlag && !Double.isNaN(iframeComboLimit) && !Double.isNaN(iframeComboValue) ? iframeComboLimit >= -2.6 - iframeComboValue2 && iframeComboLimit <= 0.45 + iframeComboValue2 && iframeComboValue <= 2.3 + iframeComboValue2 : false;
   }

   public static int getMax(int iframeComboValue) {
      return Math.max(3, iframeComboValue + 3);
   }

   public static double[] getAmounts(double iframeComboValue, double iframeComboValue2, double iframeComboValue3, double iframeComboValue4, double routedSearchingValue, double iframeComboBound) {
      double angle = Math.toRadians(Double.isFinite(routedSearchingValue) ? routedSearchingValue : 0.0);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      double ux2 = -iframeComboValue3;
      double deg2 = -iframeComboValue4;
      double iframeComboFactor = Double.isFinite(iframeComboBound) ? Math.max(0.0, iframeComboBound) : 2.7;
      return new double[]{iframeComboValue + (ux2 * cos - deg2 * sin) * iframeComboFactor, iframeComboValue2 + (ux2 * sin + deg2 * cos) * iframeComboFactor};
   }

   public static boolean canFlag(long iframeComboLimit, double iframeComboValue) {
      return iframeComboLimit >= 0L && iframeComboLimit <= 12L && iframeComboValue <= 1.0;
   }

   public static boolean canFlag(double iframeComboLimit, double iframeComboValue, double iframeComboLimit2) {
      return iframeComboLimit > 5.0 && iframeComboValue < 0.15 && iframeComboLimit2 > 4.5;
   }

   public static boolean canFlag6(double iframeComboValue) {
      return Double.isFinite(iframeComboValue) && iframeComboValue <= 0.5;
   }

   public static boolean canFlag(float iframeComboLimit, float iframeComboValue, double iframeComboBound, boolean iframeComboFlag) {
      if (!Float.isFinite(iframeComboLimit)) {
         return false;
      } else if (!(iframeComboLimit >= 4.0F) && !canFlag(iframeComboLimit, iframeComboValue)) {
         double iframeComboValue2 = Double.isFinite(iframeComboBound) ? Math.max(0.0, iframeComboBound) : 4.0;
         return iframeComboFlag && iframeComboLimit >= 2.5F && iframeComboLimit > iframeComboValue2;
      } else {
         return true;
      }
   }

   public static boolean canFlag(int iframeComboLimit, float iframeComboValue) {
      return iframeComboLimit > 0 && Float.isFinite(iframeComboValue) && iframeComboValue < 20.0F;
   }

   public static int getValueInt(double iframeComboValue) {
      return 3 + getValue(iframeComboValue);
   }

   public static boolean canIframe(String iframeText, int iframeLimit, double iframeValue, int slotSword) {
      return "iframe".equals(iframeText) && Double.isFinite(iframeValue) ? 2 * (getValue(iframeValue) + 1) <= iframeLimit && slotSword > iframeLimit : false;
   }

   public static float getAmount(int iframeComboLimit, boolean iframeComboFlag) {
      return iframeComboLimit >= 3 && !iframeComboFlag ? 0.75F : 0.9F;
   }

   public static int getMaxForInt(int iframeComboValue) {
      return Math.max(6, iframeComboValue + 1);
   }

   private static double getAmount(double[] iframeComboValues, int index) {
      return iframeComboValues != null && index >= 0 && index < iframeComboValues.length ? iframeComboValues[index] : 0.0;
   }

   public static int getValueForFlagDouble(boolean iframeComboFlag, double iframeComboLimit, boolean iframeComboFlag2, double iframeComboLimit2) {
      boolean iframeComboFlag3 = iframeComboFlag && iframeComboLimit >= 3.0;
      boolean iframeComboFlag4 = iframeComboFlag2 && iframeComboLimit2 >= 3.0;
      if (!iframeComboFlag3 || iframeComboFlag4 && !(iframeComboLimit >= iframeComboLimit2)) {
         return iframeComboFlag4 ? 2 : 0;
      } else {
         return 1;
      }
   }

   public static boolean canFlag(int iframeComboValue, int iframeComboBound, double iframeComboLimit) {
      return iframeComboValue + Math.max(0, iframeComboBound) + 1 < getAmount(iframeComboLimit);
   }

   public static boolean canFlagForIntFloat(int iframeComboValue, float routedPartialValue) {
      return canFlag(iframeComboValue, routedPartialValue, 0.0);
   }

   public static String formatIframeLate(String iframeLateText, int iframeLateLimit, int iframeLateValue) {
      if (!"iframe".equals(iframeLateText)) {
         return "why";
      } else {
         return iframeLateValue <= iframeLateLimit ? "sword" : "late";
      }
   }

   public static int getValue4(double iframeComboValue) {
      return 1 + getValue(iframeComboValue);
   }

   public static double getAmount(double iframeComboFactor, double iframeComboFactor2, double iframeComboFactor3, double iframeComboFactor4) {
      return iframeComboFactor * iframeComboFactor3 + iframeComboFactor2 * iframeComboFactor4;
   }

   public static boolean canFlag(boolean iframeComboFlag, int iframeComboValue) {
      return iframeComboFlag || iframeComboValue == 2;
   }

   public static int getValue(int iframeComboBound, boolean iframeComboFlag, boolean iframeComboFlag2) {
      if (iframeComboFlag) {
         return 0;
      } else {
         return iframeComboFlag2 ? Math.min(2147483646, iframeComboBound) + 1 : iframeComboBound;
      }
   }

   public static int getValue(boolean iframeComboFlag, float iframeComboLimit, double iframeComboLimit2, int iframeComboValue) {
      if (iframeComboFlag && Float.isFinite(iframeComboLimit) && iframeComboLimit2 > 0.0) {
         return iframeComboLimit > 0.9 ? 0 : (int)Math.floor((0.9 - iframeComboLimit) * iframeComboLimit2) + 1;
      } else {
         return iframeComboValue;
      }
   }

   public static boolean canFlag(double iframeComboValue, int iframeComboLimit, boolean iframeComboFlag, int iframeComboValue2) {
      return iframeComboLimit > 0 && iframeComboFlag && iframeComboValue2 < 3 && Double.isFinite(iframeComboValue) && iframeComboValue <= 2.0;
   }

   public static double[] getAmounts(double iframeComboValue, double iframeComboValue2, double iframeComboValue3, double iframeComboValue4, double followLowValue) {
      return getAmounts(iframeComboValue, iframeComboValue2, iframeComboValue3, iframeComboValue4, followLowValue, 2.7);
   }

   public static int getValue(boolean iframeComboFlag, double iframeComboLimit, boolean iframeComboFlag2, double iframeComboLimit2, double iframeComboBound) {
      double iframeComboValue = Double.isFinite(iframeComboBound) ? Math.max(0.25, iframeComboBound) : 3.0;
      boolean iframeComboFlag3 = iframeComboFlag && iframeComboLimit >= iframeComboValue;
      boolean iframeComboFlag4 = iframeComboFlag2 && iframeComboLimit2 >= 3.0;
      if (!iframeComboFlag3 || iframeComboFlag4 && !(iframeComboLimit >= iframeComboLimit2)) {
         return iframeComboFlag4 ? 2 : 0;
      } else {
         return 1;
      }
   }

   public static double getAmount(String iframeComboText, double iframeComboBound) {
      if (!isEquals(iframeComboText)) {
         return iframeComboBound;
      } else {
         return Double.isFinite(iframeComboBound) ? Math.max(iframeComboBound, 0.25) : 0.25;
      }
   }
}
