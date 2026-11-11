package com.corz.client.utils;

public final class ProofSupportUtils {
   public static final int[] blockPosTable = new int[]{0, 1};
   public static final int[] proofSupportTable = new int[]{-1, -2, 2, -3};
   public static final int[] coverageTable = new int[]{0, 1, -1, -2, 2, -3};

   public static int getValue(long proofSupportValue) {
      return (int)(proofSupportValue << 52 >> 52);
   }

   private ProofSupportUtils() {
   }

   private static int getX(double proofSupportValue) {
      return (int)Math.floor(proofSupportValue);
   }

   public static int getValueForLong(long proofSupportValue) {
      return (int)(proofSupportValue << 26 >> 38);
   }

   public static String formatText(long proofSupportValue) {
      return getValueInt(proofSupportValue) + ", " + getValue(proofSupportValue) + ", " + getValueForLong(proofSupportValue);
   }

   public static long getTime(int proofSupportValue, int builderAutoValue, int j) {
      return (proofSupportValue & 67108863L) << 38 | (j & 67108863L) << 12 | builderAutoValue & 4095L;
   }

   public static int getTop(double topValue, double proofSupportValue, double topValue3, ProofSupportUtils.CollisionTop collisionTop) {
      int x2 = getX(topValue);
      int x3 = getX(proofSupportValue);
      int x4 = getX(topValue3);
      double top = collisionTop.top(x2, x3, x4);
      return top > 0.26 && proofSupportValue - x3 >= top - 0.001 ? x3 + 1 : x3;
   }

   public static boolean canFlag(double proofSupportValue, double proofSupportValue2, double proofSupportValue3, int proofSupportValue4, int proofSupportValue5, int proofSupportValue6, ProofSupportUtils.CollisionTop collisionTop) {
      return getX(proofSupportValue) == proofSupportValue4 && getX(proofSupportValue3) == proofSupportValue6 && getTop(proofSupportValue, proofSupportValue2, proofSupportValue3, collisionTop) == proofSupportValue5;
   }

   public static boolean canFlag(int proofSupportValue, int proofSupportValue2) {
      return proofSupportValue == proofSupportValue2 || proofSupportValue == proofSupportValue2 + 1;
   }

   public static long getTime(double proofSupportValue, double proofSupportValue2, double proofSupportValue3, ProofSupportUtils.CollisionTop collisionTop) {
      int x2 = getX(proofSupportValue);
      int x3 = getX(proofSupportValue3);
      int top = getTop(proofSupportValue, proofSupportValue2, proofSupportValue3, collisionTop);
      return getTime(x2, top, x3);
   }

   public static int getValueInt(long proofSupportValue) {
      return (int)(proofSupportValue << 0 >> 38);
   }

   public static int getValue(int proofSupportValue, int proofSupportValue2) {
      return canFlag(proofSupportValue, proofSupportValue2) ? 0 : (proofSupportValue == proofSupportValue2 - 1 ? 1 : 2);
   }

   public interface CollisionTop {
      double top(int topValue, int x3, int x4);
   }
}
