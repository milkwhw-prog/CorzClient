package com.corz.client.utils;

import java.util.LinkedHashSet;

public final class PackBuilderUtils {
   private PackBuilderUtils() {
   }

   public static long[] getPack(int packValue, int packValue2, int packValue3, int packValue4, int packValue5, int packValue6, int packValue7, int packValue8, int packValue9, PackBuilderUtils.Packer packer) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
               linkedHashSet.add(packer.pack(packValue + i, packValue2 + j, packValue3 + k));
            }
         }
      }

      linkedHashSet.add(packer.pack(packValue4, packValue5, packValue6));
      linkedHashSet.add(packer.pack(packValue7, packValue8 - 1, packValue9));
      linkedHashSet.add(packer.pack(packValue7, packValue8, packValue9));
      linkedHashSet.add(packer.pack(packValue7, packValue8 + 1, packValue9));
      long[] longs = new long[linkedHashSet.size()];
      int packCounter = 0;

      for (long packValue10 : (Iterable<Long>) (linkedHashSet)) {
         longs[packCounter++] = packValue10;
      }

      return longs;
   }

   public static long getTime(long packBuilderValue, long packBuilderValue2, int supportFace, long packBuilderValue4, boolean packBuilderFlag) {
      long packBuilderValue5 = 7323168022670960397L;
      long packBuilderValue6 = -3750763034362895579L;
      long[] longs = new long[]{packBuilderValue, packBuilderValue2, supportFace, packBuilderValue4, packBuilderFlag ? 1L : 0L, 32319L};
      long packBuilderValue7 = packBuilderValue5 ^ ((long)longs.length << 32 ^ packBuilderValue5) & -4294967296L;

      for (long i = packBuilderValue7 ^ (0L ^ packBuilderValue7) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long packBuilderValue8 = longs[(int)i];
         packBuilderValue6 = (packBuilderValue6 ^ packBuilderValue8) * 1099511628211L;
      }

      return packBuilderValue6;
   }

   @FunctionalInterface
   public interface Packer {
      long pack(int packValue, int packValue2, int packValue3);
   }
}
