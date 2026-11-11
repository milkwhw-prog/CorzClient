package com.corz.client.utils;

public final class SingleRightUtils {
   public static final int[] singleRightTable = new int[]{0, 1, 0, -1};
   public static final int[] table = new int[]{-1, 0, 1, 0};
   public static final int SINGLE_CODE_PRIMARY = 1;

   public static boolean canSingle(SingleRightUtils.Type type, boolean singleFlag) {
      return type == SingleRightUtils.Type.SINGLE && canFlag(singleFlag);
   }

   public static int getValue(int singleRightValue, SingleRightUtils.Type type) {
      return getValueForIntType(singleRightValue, type);
   }

   @Deprecated
   public static boolean isFlag() {
      return false;
   }

   public static int getValue(int singleRightValue) {
      return singleRightValue + 1 & 3;
   }

   public static int getValueForInt(int singleRightValue) {
      return singleRightValue + 3 & 3;
   }

   private SingleRightUtils() {
   }

   public static int getValueForIntType(int singleRightValue, SingleRightUtils.Type type2) {
      return switch (type2) {
         case SINGLE -> -1;
         case LEFT -> getValue(singleRightValue);
         case RIGHT -> getValueForInt(singleRightValue);
      };
   }

   public static int getValueInt(int singleRightValue) {
      return singleRightValue != 1 && singleRightValue != 3 ? 1 : 0;
   }

   public static int[] getValues(int singleRightValue, SingleRightUtils.Type type2) {
      int index = getValueForIntType(singleRightValue, type2);
      return index < 0 ? null : new int[]{singleRightTable[index], table[index]};
   }

   public static boolean canFlag(int singleRightValue, int singleRightValue2) {
      return getValueInt(singleRightValue) != getValueInt(singleRightValue2);
   }

   public static boolean canFlag(boolean singleRightFlag) {
      return singleRightFlag;
   }

   public static int getValue4(int singleRightValue) {
      return singleRightValue + 2 & 3;
   }

   public static enum Type {
      SINGLE,
      LEFT,
      RIGHT;
   }
}
