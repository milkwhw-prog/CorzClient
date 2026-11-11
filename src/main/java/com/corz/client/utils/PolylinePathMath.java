package com.corz.client.utils;

import java.util.Arrays;

public final class PolylinePathMath {

   public static double[] trimPathToPosition(double pilotValue, double pilotValue2, double pilotValue3, double[] pilotValues) {
      int pilotValue4 = pilotValues.length / 3;
      if (pilotValue4 < 2) {
         return prependPoint(pilotValue, pilotValue2, pilotValue3, pilotValues);
      } else {
         int pilotLimit = Math.min(pilotValue4 - 2, 3);
         int pilotValue5 = 0;
         double maxValue = Double.MAX_VALUE;
         double pilotValue7 = 0.0;

         for (int i = 0; i <= pilotLimit; i++) {
            int index = i * 3;
            int index2 = index + 3;
            double pilotFactor = pilotValues[index2] - pilotValues[index];
            double pilotFactor2 = pilotValues[index2 + 1] - pilotValues[index + 1];
            double pilotFactor3 = pilotValues[index2 + 2] - pilotValues[index + 2];
            double pilotLimit2 = pilotFactor * pilotFactor + pilotFactor2 * pilotFactor2 + pilotFactor3 * pilotFactor3;
            double pilotBound = pilotLimit2 > 1.0E-12 ? ((pilotValue - pilotValues[index]) * pilotFactor + (pilotValue2 - pilotValues[index + 1]) * pilotFactor2 + (pilotValue3 - pilotValues[index + 2]) * pilotFactor3) / pilotLimit2 : 0.0;
            double min4 = Math.min(1.0, Math.max(0.0, pilotBound));
            double pilotFactor5 = pilotValues[index] + pilotFactor * min4 - pilotValue;
            double pilotFactor6 = pilotValues[index + 1] + pilotFactor2 * min4 - pilotValue2;
            double pilotFactor7 = pilotValues[index + 2] + pilotFactor3 * min4 - pilotValue3;
            double pilotValue8 = pilotFactor5 * pilotFactor5 + pilotFactor6 * pilotFactor6 + pilotFactor7 * pilotFactor7;
            if (pilotValue8 <= maxValue + 1.0E-9) {
               maxValue = pilotValue8;
               pilotValue5 = i;
               pilotValue7 = min4;
            }
         }

         int pilotValue9 = pilotValue5 == 0 && pilotValue7 <= 0.0 ? 0 : pilotValue5 + 1;
         double[] doubles = new double[(pilotValue4 - pilotValue9 + 1) * 3];
         doubles[0] = pilotValue;
         doubles[1] = pilotValue2;
         doubles[2] = pilotValue3;
         System.arraycopy(pilotValues, pilotValue9 * 3, doubles, 3, (pilotValue4 - pilotValue9) * 3);
         return doubles;
      }
   }

   public static int resampleWithDistances(double[] pilotValues, double pilotDivisor, double[] pilotValues2, double[] pilotValues3) {
      int pilotLimit = pilotValues.length / 3;
      if (pilotLimit == 0) {
         return 0;
      } else {
         pilotValues2[0] = pilotValues[0];
         pilotValues2[1] = pilotValues[1];
         pilotValues2[2] = pilotValues[2];
         pilotValues3[0] = 0.0;
         if (pilotLimit == 1) {
            return 1;
         } else {
            double pilotTotal = 0.0;

            for (int i = 1; i < pilotLimit; i++) {
               pilotTotal += getSegmentLength(pilotValues, i - 1, i);
            }

            if (!(pilotTotal > 1.0E-9)) {
               pilotValues2[3] = pilotValues[(pilotLimit - 1) * 3];
               pilotValues2[4] = pilotValues[(pilotLimit - 1) * 3 + 1];
               pilotValues2[5] = pilotValues[(pilotLimit - 1) * 3 + 2];
               pilotValues3[1] = 0.0;
               return 2;
            } else {
               int max2 = Math.max(1, (int)Math.ceil(pilotTotal / pilotDivisor));
               double pilotFactor = pilotTotal / max2;
               int pilotCounter = 1;
               double pilotTotal2 = 0.0;
               double segmentLength = getSegmentLength(pilotValues, 0, 1);
               int index = 1;

               for (int j = 1; j < max2; j++) {
                  double pilotLimit3;
                  for (pilotLimit3 = j * pilotFactor; pilotCounter < pilotLimit - 1 && pilotLimit3 > pilotTotal2 + segmentLength; segmentLength = getSegmentLength(pilotValues, pilotCounter - 1, pilotCounter)) {
                     pilotTotal2 += segmentLength;
                     pilotCounter++;
                  }

                  double pilotFactor2 = segmentLength > 1.0E-12 ? Math.min(1.0, Math.max(0.0, (pilotLimit3 - pilotTotal2) / segmentLength)) : 1.0;

                  for (int k = 0; k < 3; k++) {
                     pilotValues2[index * 3 + k] = pilotValues[(pilotCounter - 1) * 3 + k] + (pilotValues[pilotCounter * 3 + k] - pilotValues[(pilotCounter - 1) * 3 + k]) * pilotFactor2;
                  }

                  pilotValues3[index] = pilotLimit3;
                  index++;
               }

               pilotValues2[index * 3] = pilotValues[(pilotLimit - 1) * 3];
               pilotValues2[index * 3 + 1] = pilotValues[(pilotLimit - 1) * 3 + 1];
               pilotValues2[index * 3 + 2] = pilotValues[(pilotLimit - 1) * 3 + 2];
               pilotValues3[index] = pilotTotal;
               return index + 1;
            }
         }
      }
   }

   public static boolean endsWithSequence(long[] pilotValues, int pilotLimit, long[] pilotValues2, int times) {
      if (times != 0 && times <= pilotLimit) {
         int pilotValue = pilotLimit - times;

         for (int i = 0; i < times; i++) {
            if (pilotValues[pilotValue + i] != pilotValues2[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static double[] smoothCornersChaikin(double[] cloneValues) {
      int cloneValue = cloneValues.length / 3;
      if (cloneValue < 3) {
         return (double[])cloneValues.clone();
      } else {
         double[] doubles = new double[2 * (cloneValue - 1) * 3];
         int cloneCounter = 0;

         for (int i = 0; i < 3; i++) {
            doubles[cloneCounter++] = cloneValues[i];
         }

         for (int j = 0; j < cloneValue - 1; j++) {
            int cloneValue2 = j * 3;
            int cloneValue3 = (j + 1) * 3;
            if (j > 0) {
               for (int k = 0; k < 3; k++) {
                  doubles[cloneCounter++] = cloneValues[cloneValue2 + k] * 0.75 + cloneValues[cloneValue3 + k] * 0.25;
               }
            }

            if (j < cloneValue - 2) {
               for (int m = 0; m < 3; m++) {
                  doubles[cloneCounter++] = cloneValues[cloneValue2 + m] * 0.25 + cloneValues[cloneValue3 + m] * 0.75;
               }
            }
         }

         for (int p = 0; p < 3; p++) {
            doubles[cloneCounter++] = cloneValues[(cloneValue - 1) * 3 + p];
         }

         return Arrays.copyOf(doubles, cloneCounter);
      }
   }

   private static double getSegmentLength(double[] pilotValues, int pilotValue, int i) {
      double pilotFactor = pilotValues[i * 3] - pilotValues[pilotValue * 3];
      double pilotFactor2 = pilotValues[i * 3 + 1] - pilotValues[pilotValue * 3 + 1];
      double pilotFactor3 = pilotValues[i * 3 + 2] - pilotValues[pilotValue * 3 + 2];
      return Math.sqrt(pilotFactor * pilotFactor + pilotFactor2 * pilotFactor2 + pilotFactor3 * pilotFactor3);
   }

   public static int findSegmentIndex(double[] pilotValues, int pilotLimit, double pilotLimit2) {
      if (pilotLimit > 1 && !(pilotLimit2 <= pilotValues[0])) {
         if (pilotLimit2 >= pilotValues[pilotLimit - 1]) {
            return pilotLimit - 2;
         } else {
            int pilotValue = 0;
            int index2 = pilotLimit - 1;

            while (index2 - pilotValue > 1) {
               int index = pilotValue + index2 >>> 1;
               if (pilotValues[index] <= pilotLimit2) {
                  pilotValue = index;
               } else {
                  index2 = index;
               }
            }

            return pilotValue;
         }
      } else {
         return 0;
      }
   }

   public static void samplePositionAtDistance(double[] pilotValues, double[] pilotValues2, int pilotValue, double pilotValue2, double[] pilotValues3) {
      if (pilotValue != 0) {
         if (pilotValue == 1) {
            pilotValues3[0] = pilotValues[0];
            pilotValues3[1] = pilotValues[1];
            pilotValues3[2] = pilotValues[2];
         } else {
            int index = findSegmentIndex(pilotValues2, pilotValue, pilotValue2);
            double pilotLimit = pilotValues2[index + 1] - pilotValues2[index];
            double pilotFactor = pilotLimit > 1.0E-12 ? Math.min(1.0, Math.max(0.0, (pilotValue2 - pilotValues2[index]) / pilotLimit)) : 0.0;

            for (int i = 0; i < 3; i++) {
               pilotValues3[i] = pilotValues[index * 3 + i] + (pilotValues[(index + 1) * 3 + i] - pilotValues[index * 3 + i]) * pilotFactor;
            }
         }
      }
   }

   public static void sampleDirectionAtDistance(double[] pilotValues, double[] pilotValues2, int pilotLimit, double pilotValue, double[] pilotValues3) {
      pilotValues3[0] = 1.0;
      pilotValues3[1] = 0.0;
      pilotValues3[2] = 0.0;
      if (pilotLimit >= 2) {
         int segmentIndex = findSegmentIndex(pilotValues2, pilotLimit, pilotValue);

         for (int i = 0; i < pilotLimit; i++) {
            int max3 = Math.max(0, Math.min(pilotLimit - 2, segmentIndex + (i % 2 == 0 ? i / 2 : -(i + 1) / 2)));
            double pilotFactor = pilotValues[(max3 + 1) * 3] - pilotValues[max3 * 3];
            double pilotFactor2 = pilotValues[(max3 + 1) * 3 + 1] - pilotValues[max3 * 3 + 1];
            double pilotFactor3 = pilotValues[(max3 + 1) * 3 + 2] - pilotValues[max3 * 3 + 2];
            double distance = Math.sqrt(pilotFactor * pilotFactor + pilotFactor2 * pilotFactor2 + pilotFactor3 * pilotFactor3);
            if (distance > 1.0E-9) {
               pilotValues3[0] = pilotFactor / distance;
               pilotValues3[1] = pilotFactor2 / distance;
               pilotValues3[2] = pilotFactor3 / distance;
               return;
            }
         }
      }
   }

   public static int estimateSampleCount(double[] pilotValues, double pilotDivisor) {
      int pilotLimit = pilotValues.length / 3;
      double pilotTotal = 0.0;

      for (int i = 1; i < pilotLimit; i++) {
         pilotTotal += getSegmentLength(pilotValues, i - 1, i);
      }

      return Math.max(2, (int)Math.ceil(pilotTotal / pilotDivisor) + 2);
   }

   public static double[] removeInvalidAndDuplicatePoints(double[] pilotValues) {
      int pilotLimit = pilotValues.length / 3;
      double[] doubles = new double[pilotValues.length];
      int pilotCounter = 0;

      for (int i = 0; i < pilotLimit; i++) {
         double pilotValue = pilotValues[i * 3];
         double pilotValue2 = pilotValues[i * 3 + 1];
         double pilotValue3 = pilotValues[i * 3 + 2];
         if (isFiniteValue(pilotValue) && isFiniteValue(pilotValue2) && isFiniteValue(pilotValue3)) {
            if (pilotCounter > 0) {
               double pilotFactor = pilotValue - doubles[pilotCounter - 3];
               double pilotFactor2 = pilotValue2 - doubles[pilotCounter - 2];
               double pilotFactor3 = pilotValue3 - doubles[pilotCounter - 1];
               if (pilotFactor * pilotFactor + pilotFactor2 * pilotFactor2 + pilotFactor3 * pilotFactor3 < 1.0E-8) {
                  continue;
               }
            }

            doubles[pilotCounter++] = pilotValue;
            doubles[pilotCounter++] = pilotValue2;
            doubles[pilotCounter++] = pilotValue3;
         }
      }

      return Arrays.copyOf(doubles, pilotCounter);
   }

   public static double getDistanceAlongPath(double[] pilotValues, double[] pilotValues2, int pilotLimit, double pilotValue, double pilotValue2, double pilotValue3) {
      if (pilotLimit <= 1) {
         return 0.0;
      } else {
         double maxValue = Double.MAX_VALUE;
         double pilotValue4 = 0.0;

         for (int i = 1; i < pilotLimit; i++) {
            int index = (i - 1) * 3;
            int index2 = i * 3;
            double pilotFactor = pilotValues[index2] - pilotValues[index];
            double pilotFactor2 = pilotValues[index2 + 1] - pilotValues[index + 1];
            double pilotFactor3 = pilotValues[index2 + 2] - pilotValues[index + 2];
            double pilotLimit3 = pilotFactor * pilotFactor + pilotFactor2 * pilotFactor2 + pilotFactor3 * pilotFactor3;
            double pilotBound = pilotLimit3 > 1.0E-12 ? ((pilotValue - pilotValues[index]) * pilotFactor + (pilotValue2 - pilotValues[index + 1]) * pilotFactor2 + (pilotValue3 - pilotValues[index + 2]) * pilotFactor3) / pilotLimit3 : 0.0;
            double min4 = Math.min(1.0, Math.max(0.0, pilotBound));
            double pilotFactor5 = pilotValues[index] + pilotFactor * min4 - pilotValue;
            double pilotFactor6 = pilotValues[index + 1] + pilotFactor2 * min4 - pilotValue2;
            double pilotFactor7 = pilotValues[index + 2] + pilotFactor3 * min4 - pilotValue3;
            double pilotValue5 = pilotFactor5 * pilotFactor5 + pilotFactor6 * pilotFactor6 + pilotFactor7 * pilotFactor7;
            if (pilotValue5 < maxValue) {
               maxValue = pilotValue5;
               pilotValue4 = pilotValues2[i - 1] + (pilotValues2[i] - pilotValues2[i - 1]) * min4;
            }
         }

         return pilotValue4;
      }
   }

   public static double[] blockPathToPoints(int[] pilotValues, int pilotLimit) {
      double[] doubles = new double[Math.max(1, pilotLimit * 2) * 3];
      int pilotCounter = 0;
      int pilotValue = 0;
      int pilotBound = 0;
      int pilotValue2 = 0;

      for (int i = 0; i < pilotLimit; i++) {
         int pilotValue3 = pilotValues[i * 3];
         int rocketsBuyBound = pilotValues[i * 3 + 1];
         int pilotValue4 = pilotValues[i * 3 + 2];
         if (i > 0) {
            if (pilotValue3 == pilotValue && rocketsBuyBound == pilotBound && pilotValue4 == pilotValue2) {
               continue;
            }

            if (rocketsBuyBound != pilotBound) {
               doubles[pilotCounter++] = (pilotValue + pilotValue3) * 0.5 + 0.5;
               doubles[pilotCounter++] = Math.max(pilotBound, rocketsBuyBound);
               doubles[pilotCounter++] = (pilotValue2 + pilotValue4) * 0.5 + 0.5;
            }
         }

         doubles[pilotCounter++] = pilotValue3 + 0.5;
         doubles[pilotCounter++] = rocketsBuyBound;
         doubles[pilotCounter++] = pilotValue4 + 0.5;
         pilotValue = pilotValue3;
         pilotBound = rocketsBuyBound;
         pilotValue2 = pilotValue4;
      }

      return Arrays.copyOf(doubles, pilotCounter);
   }

   private PolylinePathMath() {
   }

   public static double[] prependPoint(double pilotValue, double pilotValue2, double pilotValue3, double[] pilotValues) {
      double[] doubles = new double[pilotValues.length + 3];
      doubles[0] = pilotValue;
      doubles[1] = pilotValue2;
      doubles[2] = pilotValue3;
      System.arraycopy(pilotValues, 0, doubles, 3, pilotValues.length);
      return doubles;
   }

   private static boolean isFiniteValue(double pilotValue) {
      return !Double.isNaN(pilotValue) && !Double.isInfinite(pilotValue);
   }

   public static double[] smoothPath(double[] pilotValues) {
      double[] copyOf = removeInvalidAndDuplicatePoints(pilotValues);
      if (copyOf.length < 9) {
         return copyOf;
      } else {
         double[] cloneValues = resamplePath(copyOf, 1.0);

         for (int i = 0; i < 3; i++) {
            cloneValues = smoothCornersChaikin(cloneValues);
         }

         return cloneValues;
      }
   }

   public static double[] resamplePath(double[] pilotValues, double pilotDivisor) {
      int pilotLimit = pilotValues.length / 3;
      if (pilotLimit < 2) {
         return (double[])pilotValues.clone();
      } else {
         int pilotTotal = 1;

         for (int i = 1; i < pilotLimit; i++) {
            pilotTotal += Math.max(1, (int)Math.ceil(getSegmentLength(pilotValues, i - 1, i) / pilotDivisor));
         }

         double[] doubles = new double[pilotTotal * 3];
         int pilotCounter = 0;
         doubles[pilotCounter++] = pilotValues[0];
         doubles[pilotCounter++] = pilotValues[1];
         doubles[pilotCounter++] = pilotValues[2];

         for (int j = 1; j < pilotLimit; j++) {
            int max2 = Math.max(1, (int)Math.ceil(getSegmentLength(pilotValues, j - 1, j) / pilotDivisor));

            for (int k = 1; k <= max2; k++) {
               double pilotFactor = (double)k / max2;

               for (int m = 0; m < 3; m++) {
                  doubles[pilotCounter++] = pilotValues[(j - 1) * 3 + m] + (pilotValues[j * 3 + m] - pilotValues[(j - 1) * 3 + m]) * pilotFactor;
               }
            }
         }

         return doubles;
      }
   }
}
