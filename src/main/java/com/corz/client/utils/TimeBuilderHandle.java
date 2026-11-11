package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class TimeBuilderHandle {
   private long handleSequence;
   private final Map<Long, Long> builderHandlePositionMap = new HashMap<>();

   public int getValue() {
      return this.builderHandlePositionMap.size();
   }

   public boolean canTime(long builderHandleValue, long builderHandleValue2) {
      Long long2 = this.builderHandlePositionMap.get(builderHandleValue);
      if (long2 == null) {
         return false;
      } else if (long2 == builderHandleValue2) {
         this.handleSequence++;
         return true;
      } else {
         this.builderHandlePositionMap.remove(builderHandleValue);
         return false;
      }
   }

   public void update() {
      this.builderHandlePositionMap.clear();
   }

   public static long getTime(long builderHandleValue, int builderHandleValue2, long builderHandleValue3, long builderHandleValue4, int builderHandleValue5, boolean builderHandleFlag, long builderHandleValue6, int builderHandleValue7, long builderHandleValue8) {
      long builderHandleValue9 = -2823304044075448682L;
      long builderHandleValue10 = -3750763034362895579L;
      long[] longs = new long[]{builderHandleValue, builderHandleValue2, builderHandleValue3, builderHandleValue4, builderHandleValue5, builderHandleFlag ? 1L : 0L, builderHandleValue6, builderHandleValue7, builderHandleValue8};
      long[] builderHandleValues = longs;
      long builderHandleValue11 = builderHandleValue9 ^ ((long)longs.length << 32 ^ builderHandleValue9) & -4294967296L;

      for (long i = builderHandleValue11 ^ (0L ^ builderHandleValue11) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long builderHandleValue12 = builderHandleValues[(int)i];
         builderHandleValue10 = (builderHandleValue10 ^ builderHandleValue12) * 1099511628211L;
      }

      return builderHandleValue10;
   }

   public void handle(long builderHandleValue, long botCrystalValue) {
      this.builderHandlePositionMap.put(builderHandleValue, botCrystalValue);
   }

   public long getTime() {
      return this.handleSequence;
   }
}
