package com.corz.client.utils;

import java.util.Arrays;

public final class FacesEdgesUtils {
   public final float[] faces;
   public final float[] edges;

   public static FacesEdgesUtils getFacesEdges(int facesEdgesValue, int[] facesEdgesValues, int[] facesEdgesValues2, int[] facesEdgesValues3, FacesEdgesUtils.Cells cells) {
      FacesEdgesUtils.Builder builder = new FacesEdgesUtils.Builder(facesEdgesValue, facesEdgesValues, facesEdgesValues2, facesEdgesValues3, cells);
      return new FacesEdgesUtils(builder.edges(), builder.faces());
   }

   private FacesEdgesUtils(float[] edges, float[] faces) {
      this.edges = edges;
      this.faces = faces;
   }

   private static final class Builder {
      final int cellCount;
      final int[] coordTable;
      final int[] builderTable;
      final FacesEdgesUtils.Cells handler;
      final int[] pointTable = new int[3];
      final int[] table;

      boolean has(int index, int builderValue, int builderValue2, int builderValue3) {
         this.pointTable[index] = builderValue;
         this.pointTable[(index + 1) % 3] = builderValue2;
         this.pointTable[(index + 2) % 3] = builderValue3;
         return this.handler.has(this.pointTable[0], this.pointTable[1], this.pointTable[2]);
      }

      Builder(int value, int[] values, int[] values2, int[] values3, FacesEdgesUtils.Cells handler) {
         this.cellCount = value;
         this.table = values;
         this.coordTable = values2;
         this.builderTable = values3;
         this.handler = handler;
      }

      void point(FacesEdgesUtils.FloatList floatList, int index, int pointValue, int pointValue2, int pointValue3) {
         this.pointTable[index] = pointValue;
         this.pointTable[(index + 1) % 3] = pointValue2;
         this.pointTable[(index + 2) % 3] = pointValue3;
         floatList.handle(this.pointTable[0]);
         floatList.handle(this.pointTable[1]);
         floatList.handle(this.pointTable[2]);
      }

      int coord(int index, int coordValue) {
         return coordValue == 0 ? this.table[index] : (coordValue == 1 ? this.coordTable[index] : this.builderTable[index]);
      }

      float[] edges() {
         FacesEdgesUtils.FloatList floatList = new FacesEdgesUtils.FloatList(this.cellCount * 6);
         FacesEdgesUtils.LongList longList = new FacesEdgesUtils.LongList(this.cellCount * 2);

         for (int i = 0; i < 3; i++) {
            int edgesValue = (i + 1) % 3;
            int coordValue = (i + 2) % 3;
            longList.updateElementCount();

            for (int j = 0; j < this.cellCount; j++) {
               int coord = this.coord(j, i);
               int coord2 = this.coord(j, edgesValue);
               int coord3 = this.coord(j, coordValue);

               for (int k = 0; k <= 1; k++) {
                  for (int m = 0; m <= 1; m++) {
                     int edgesValue3 = coord2 + k;
                     int edgesValue4 = coord3 + m;
                     boolean has = this.has(i, coord, edgesValue3 - 1, edgesValue4 - 1);
                     boolean has2 = this.has(i, coord, edgesValue3 - 1, edgesValue4);
                     boolean has3 = this.has(i, coord, edgesValue3, edgesValue4 - 1);
                     boolean has4 = this.has(i, coord, edgesValue3, edgesValue4);
                     int edgesValue5 = !has && !has2 ? edgesValue3 : edgesValue3 - 1;
                     int edgesValue6 = has ? edgesValue4 - 1 : (has2 ? edgesValue4 : (has3 ? edgesValue4 - 1 : edgesValue4));
                     if (edgesValue5 == coord2 && edgesValue6 == coord3) {
                        int edgesValue7 = (has ? 1 : 0) + (has2 ? 1 : 0) + (has3 ? 1 : 0) + (has4 ? 1 : 0);
                        boolean edgesFlag = edgesValue7 == 1 || edgesValue7 == 3 || edgesValue7 == 2 && has == has4;
                        if (edgesFlag) {
                           longList.handle((long)edgesValue3 << 42 | (long)edgesValue4 << 21 | coord);
                        }
                     }
                  }
               }
            }

            longList.update();
            int index = 0;

            while (index < longList.elementCount) {
               long edgesValue8 = longList.copyOf[index];
               long edgesValue9 = edgesValue8 >>> 21;
               int pointValue = (int)(edgesValue8 & 2097151L);
               int edgesCounter = pointValue;

               int index2;
               for (index2 = index + 1; index2 < longList.elementCount && longList.copyOf[index2] >>> 21 == edgesValue9 && (int)(longList.copyOf[index2] & 2097151L) == edgesCounter + 1; index2++) {
                  edgesCounter++;
               }

               int edgesValue11 = (int)(edgesValue8 >>> 42);
               int edgesValue12 = (int)(edgesValue8 >>> 21 & 2097151L);
               this.point(floatList, i, pointValue, edgesValue11, edgesValue12);
               this.point(floatList, i, edgesCounter + 1, edgesValue11, edgesValue12);
               index = index2;
            }
         }

         return floatList.getCopyOf();
      }

      float[] faces() {
         FacesEdgesUtils.LongList longList = new FacesEdgesUtils.LongList(this.cellCount * 3);

         for (int i = 0; i < this.cellCount; i++) {
            for (int j = 0; j < 3; j++) {
               int facesValue = (j + 1) % 3;
               int coordValue = (j + 2) % 3;
               int coord = this.coord(i, j);
               int coord2 = this.coord(i, facesValue);
               int coord3 = this.coord(i, coordValue);

               for (int k = 0; k <= 1; k++) {
                  int builderValue = k == 0 ? coord - 1 : coord + 1;
                  if (!this.has(j, builderValue, coord2, coord3)) {
                     long facesValue4 = j * 2L + k;
                     long facesValue5 = k == 0 ? coord : coord + 1;
                     longList.handle(facesValue4 << 58 | facesValue5 << 38 | (long)coord3 << 19 | coord2);
                  }
               }
            }
         }

         longList.update();
         FacesEdgesUtils.FloatList floatList = new FacesEdgesUtils.FloatList(longList.elementCount * 4);
         boolean[] booleans = new boolean[64];
         int index = 0;

         while (index < longList.elementCount) {
            long facesValue6 = longList.copyOf[index] >>> 38;
            int index2 = index;
            int facesLimit = Integer.MAX_VALUE;
            int minValue = Integer.MIN_VALUE;
            int maxValue = Integer.MAX_VALUE;

            int minValue8;
            for (minValue8 = Integer.MIN_VALUE; index2 < longList.elementCount && longList.copyOf[index2] >>> 38 == facesValue6; index2++) {
               int facesLimit3 = (int)(longList.copyOf[index2] & 524287L);
               int facesLimit4 = (int)(longList.copyOf[index2] >>> 19 & 524287L);
               if (facesLimit3 < facesLimit) {
                  facesLimit = facesLimit3;
               }

               if (facesLimit3 > minValue) {
                  minValue = facesLimit3;
               }

               if (facesLimit4 < maxValue) {
                  maxValue = facesLimit4;
               }

               if (facesLimit4 > minValue8) {
                  minValue8 = facesLimit4;
               }
            }

            int facesFactor = minValue - facesLimit + 1;
            int facesFactor2 = minValue8 - maxValue + 1;
            if (booleans.length < facesFactor * facesFactor2) {
               booleans = new boolean[Math.max(facesFactor * facesFactor2, booleans.length * 2)];
            }

            Arrays.fill(booleans, 0, facesFactor * facesFactor2, false);

            for (int m = index; m < index2; m++) {
               int facesValue9 = (int)(longList.copyOf[m] & 524287L) - facesLimit;
               int facesFactor3 = (int)(longList.copyOf[m] >>> 19 & 524287L) - maxValue;
               booleans[facesFactor3 * facesFactor + facesValue9] = true;
            }

            int facesValue10 = (int)(facesValue6 >>> 20) >> 1;
            int pointValue = (int)(facesValue6 & 1048575L);

            for (int p = 0; p < facesFactor2; p++) {
               for (int q = 0; q < facesFactor; q++) {
                  if (booleans[p * facesFactor + q]) {
                     int facesCounter = 1;

                     while (q + facesCounter < facesFactor && booleans[p * facesFactor + q + facesCounter]) {
                        facesCounter++;
                     }

                     int facesCounter2;
                     label122:
                     for (facesCounter2 = 1; p + facesCounter2 < facesFactor2; facesCounter2++) {
                        for (int r = 0; r < facesCounter; r++) {
                           if (!booleans[(p + facesCounter2) * facesFactor + q + r]) {
                              break label122;
                           }
                        }
                     }

                     for (int s = 0; s < facesCounter2; s++) {
                        Arrays.fill(booleans, (p + s) * facesFactor + q, (p + s) * facesFactor + q + facesCounter, false);
                     }

                     int facesValue12 = q + facesLimit;
                     int facesValue13 = p + maxValue;
                     int facesValue14 = facesValue12 + facesCounter;
                     int facesValue15 = facesValue13 + facesCounter2;
                     this.point(floatList, facesValue10, pointValue, facesValue12, facesValue13);
                     this.point(floatList, facesValue10, pointValue, facesValue14, facesValue13);
                     this.point(floatList, facesValue10, pointValue, facesValue14, facesValue15);
                     this.point(floatList, facesValue10, pointValue, facesValue12, facesValue15);
                  }
               }
            }

            index = index2;
         }

         return floatList.getCopyOf();
      }
   }

   @FunctionalInterface
   public interface Cells {
      boolean has(int cellsValue, int cellsValue2, int cellsValue3);
   }

   private static final class FloatList {
      int elementCount;
      float[] copyOf;

      FloatList(int floatBound) {
         this.copyOf = new float[Math.max(24, floatBound)];
      }

      float[] getCopyOf() {
         return Arrays.copyOf(this.copyOf, this.elementCount);
      }

      void handle(float floatValue) {
         if (this.elementCount == this.copyOf.length) {
            this.copyOf = Arrays.copyOf(this.copyOf, this.elementCount * 2);
         }

         this.copyOf[this.elementCount++] = floatValue;
      }
   }

   private static final class LongList {
      int elementCount;
      long[] copyOf;

      void handle(long longValue) {
         if (this.elementCount == this.copyOf.length) {
            this.copyOf = Arrays.copyOf(this.copyOf, this.elementCount * 2);
         }

         this.copyOf[this.elementCount++] = longValue;
      }

      LongList(int longBound) {
         this.copyOf = new long[Math.max(16, longBound)];
      }

      void update() {
         Arrays.sort(this.copyOf, 0, this.elementCount);
      }

      void updateElementCount() {
         this.elementCount = 0;
      }
   }
}
