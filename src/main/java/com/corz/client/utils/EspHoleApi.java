package com.corz.client.utils;

public interface EspHoleApi {

   int getBottom();

   int getHeightmapSample(int heightmapSampleValue, int heightmapSampleValue2);

   static int getValue(int espHoleValue, int espHoleValue2, int espHoleValue3) {
      return espHoleValue2 << 8 | espHoleValue3 << 4 | espHoleValue;
   }

   static long getTime(int espHoleValue, int espHoleValue2) {
      return espHoleValue & 4294967295L | (espHoleValue2 & 4294967295L) << 32;
   }

   int getValue();

   static int getLongKey(long longValue) {
      return (int)longValue;
   }

   static int getValue(long espHoleValue) {
      return (int)(espHoleValue >>> 32);
   }

   byte[] getSectionHash(int sectionHashValue, int k, int sectionHashValue3);

   static long getTime(int espHoleValue, int buriedOpenLimit, int espHoleValue3) {
      return (long)(espHoleValue & 67108863) << 38 | (long)(espHoleValue3 & 67108863) << 12 | buriedOpenLimit & 4095;
   }
}
