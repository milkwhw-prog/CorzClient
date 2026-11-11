package com.corz.client.utils;

import java.util.Arrays;
import java.util.function.LongPredicate;

public final class BuilderAutoUtilsEneg {

   public static BuilderAutoUtilsEneg.Anchor findAnchorAround(double builderAutoValue, double builderAutoValue2, int builderAutoValue3, LongPredicate longPredicate) {
      double builderAutoValue4 = 0.3;
      return findAnchorInBounds(builderAutoValue - builderAutoValue4, builderAutoValue + builderAutoValue4, builderAutoValue2 - builderAutoValue4, builderAutoValue2 + builderAutoValue4, builderAutoValue3, longPredicate);
   }

   public static BuilderAutoUtilsEneg.Anchor findAnchorInBounds(double builderAutoBound, double builderAutoBound2, double builderAutoBound3, double builderAutoBound4, int builderAutoValue, LongPredicate longPredicate) {
      double hashRegistryValue = (builderAutoBound + builderAutoBound2) * 0.5;
      double builderAutoValue3 = (builderAutoBound3 + builderAutoBound4) * 0.5;
      int proofSupportValue = (int)Math.floor(hashRegistryValue);
      int floor5 = (int)Math.floor(builderAutoValue3);
      int floorMaxX = (int)Math.floor(builderAutoBound);
      int builderAutoLimit = (int)Math.floor(builderAutoBound2 - 1.0E-7);
      int floorMinZ = (int)Math.floor(builderAutoBound3);
      int floor2 = (int)Math.floor(builderAutoBound4 - 1.0E-7);
      long time6 = ProofSupportUtils.getTime(proofSupportValue, builderAutoValue, floor5);
      long packPosKey7 = 0L;
      double builderAutoValue8 = -1.0;
      double maxValue = Double.MAX_VALUE;
      long[] longs = new long[(builderAutoLimit - floorMaxX + 1) * (floor2 - floorMinZ + 1)];
      int builderAutoCounter = 0;

      for (int i = floorMaxX; i <= builderAutoLimit; i++) {
         for (int j = floorMinZ; j <= floor2; j++) {
            long packPosKey = ProofSupportUtils.getTime(i, builderAutoValue, j);
            if (longPredicate.test(packPosKey)) {
               longs[builderAutoCounter++] = packPosKey;
               double apiPutBound = Math.min(builderAutoBound2, (double)(i + 1)) - Math.max(builderAutoBound, (double)i);
               double builderAutoBound6 = Math.min(builderAutoBound4, (double)(j + 1)) - Math.max(builderAutoBound3, (double)j);
               double builderAutoLimit4 = Math.max(0.0, apiPutBound) * Math.max(0.0, builderAutoBound6);
               double hypot = Math.hypot(i + 0.5 - hashRegistryValue, j + 0.5 - builderAutoValue3);
               if (builderAutoLimit4 > builderAutoValue8 + 1.0E-9 || Math.abs(builderAutoLimit4 - builderAutoValue8) <= 1.0E-9 && hypot < maxValue) {
                  packPosKey7 = packPosKey;
                  builderAutoValue8 = builderAutoLimit4;
                  maxValue = hypot;
               }
            }
         }
      }

      long[] copyOf = Arrays.copyOf(longs, builderAutoCounter);
      if (builderAutoCounter == 0) {
         return new BuilderAutoUtilsEneg.Anchor(0L, copyOf);
      } else {
         for (long copyOfItem : copyOf) {
            if (copyOfItem == time6) {
               return new BuilderAutoUtilsEneg.Anchor(time6, copyOf);
            }
         }

         return new BuilderAutoUtilsEneg.Anchor(packPosKey7, copyOf);
      }
   }

   private BuilderAutoUtilsEneg() {
   }

   public record Anchor(long primary, long[] supporting) {
      public boolean hasPrimary() {
         return this.primary != 0L;
      }

      public int getSupportingCount() {
         return this.supporting.length;
      }
   }
}
