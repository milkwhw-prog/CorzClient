package com.corz.client.utils;

import java.util.Arrays;

public final class ConstCopperUtils {
   public static final byte apiValue = getValue(1, 0);
   public static final byte DEFAULT_TYPE_CODE = getValue(2, 0);
   public static final int UNIT_STEP = 1;
   public static final byte railValue = getValue(3, 0);
   public static final byte[] buriedOpenTable = getValues(ConstCopperUtils.DEFAULT_TYPE_CODE);
   public static final byte[] sectionHashTable = getValues(ConstCopperUtils.apiValue);

   public static boolean canFlag(int constCopperValue) {
      return (constCopperValue & 3) == 1;
   }

   public static byte getValue(int constCopperValue, int constCopperValue2) {
      return (byte)(constCopperValue & 3 | constCopperValue2 << 2);
   }

   private ConstCopperUtils() {
   }

   private static byte[] getValues(byte constCopperValue) {
      byte[] bytes = new byte[4096];
      Arrays.fill(bytes, constCopperValue);
      return bytes;
   }

   public static int getValue(int constCopperValue) {
      return constCopperValue & 3;
   }

   public static int getValueForInt(int constCopperValue) {
      return (constCopperValue & 0xFF) >>> 2;
   }
}
