package com.corz.client.utils;

public final class SpawnerTimeUtils {
   public static long getTime(int spawnerValue, int rememberedSpawnerValue) {
      return getTimeForInt(spawnerValue >> 4, rememberedSpawnerValue >> 4);
   }

   public static int getValue(long spawnerValue) {
      return (int)(spawnerValue >> 32);
   }

   public static long getTime(long spawnerValue, int spawnerValue2, int spawnerValue3) {
      long spawnerFactor = getValueForLong(spawnerValue) - spawnerValue2;
      long spawnerFactor2 = getValue(spawnerValue) - spawnerValue3;
      return spawnerFactor * spawnerFactor + spawnerFactor2 * spawnerFactor2;
   }

   public static int getValueForLong(long spawnerValue) {
      return (int)spawnerValue;
   }

   public static int getValueInt(long spawnerValue) {
      return (int)(spawnerValue << 26 >> 38);
   }

   public static int getValue4(long spawnerValue) {
      return (int)(spawnerValue >> 38);
   }

   private SpawnerTimeUtils() {
   }

   public static long getTime(int spawnerValue, int y, int z) {
      return (long)(spawnerValue & 67108863) << 38 | (long)(z & 67108863) << 12 | y & 4095;
   }

   public static int getValue5(long spawnerValue) {
      return (int)(spawnerValue << 52 >> 52);
   }

   public static long getTimeForInt(int spawnerValue, int spawnerValue2) {
      return spawnerValue & 4294967295L | (long)spawnerValue2 << 32;
   }
}
