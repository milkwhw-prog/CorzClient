package com.corz.client.utils;

public final class EntityHeightSamples {
   public static final double[] HEIGHT_SAMPLE_OFFSETS = new double[]{0.0, 0.391304, 0.782609, 1.173913, 1.565217};

   public static int countSamplesReaching(double vecPosValue, double vecHashValue) {
      if (!Double.isNaN(vecPosValue) && !Double.isNaN(vecHashValue)) {
         int vecPosCounter = 0;

         for (double heightSampleOffsetsItem : HEIGHT_SAMPLE_OFFSETS) {
            if (vecPosValue + heightSampleOffsetsItem >= vecHashValue - 1.0E-6) {
               vecPosCounter++;
            }
         }

         return vecPosCounter;
      } else {
         return HEIGHT_SAMPLE_OFFSETS.length;
      }
   }

   public static boolean isEntirelyBelow(double vecPosValue, double vecHashValue) {
      return countSamplesReaching(vecPosValue, vecHashValue) == 0;
   }

   public static int countSamplesAbove(double vecPosValue, double livingEntityValue) {
      int vecPosCounter = 0;

      for (double heightSampleOffsetsItem : HEIGHT_SAMPLE_OFFSETS) {
         if (vecPosValue + heightSampleOffsetsItem > livingEntityValue + 1.0E-6) {
            vecPosCounter++;
         }
      }

      return vecPosCounter;
   }

   public static double getReachingFraction(double vecPosValue, double vecHashValue) {
      return (double)countSamplesReaching(vecPosValue, vecHashValue) / HEIGHT_SAMPLE_OFFSETS.length;
   }

   private EntityHeightSamples() {
   }
}
