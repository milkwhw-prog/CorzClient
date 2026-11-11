package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.EspHoleApi;

public final class LinesQuads {
   public final int linesQuadsValue;
   public final long chunkKey;
   public final int scoreValue;
   public final int cellsTunnelValue;
   public final int linesQuadsValue3LinesQuads;
   public final long[] copyOf;
   public final int blockX;
   public final long hashOpenValue;
   public final int scoreCellsValue;
   public final float scoreCellsAmount;
   public final float[] linesQuadsTable;
   public final int blockZ;
   public final int linesQuadsValue4LinesQuads;
   public final float[] table;
   public final int blockY;
   public final long[] timesLinesQuads;
   public final int scoreLinesValue;
   public final int linesQuadsValue2LinesQuads;
   public final int shaftTunnelValue;
   public static final int UNIT_SIZE = 1;

   public boolean canFlag(long linesQuadsValue) {
      for (long linesQuadsValue2 : this.copyOf) {
         if (linesQuadsValue2 == linesQuadsValue) {
            return true;
         }
      }

      return false;
   }

   LinesQuads(
      long time,
      long time2,
      int shaftTunnelValue,
      int scoreValue,
      int scoreLinesValue,
      int linesQuadsValue,
      int linesQuadsValue2,
      int linesQuadsValue3,
      int linesQuadsValue4,
      int scoreCellsValue,
      int cellsTunnelValue,
      float scoreCellsAmount,
      int value,
      int value2,
      int value3,
      float[] amounts,
      float[] amounts2,
      long[] copyOf,
      long[] times
   ) {
      this.hashOpenValue = time;
      this.chunkKey = time2;
      this.shaftTunnelValue = shaftTunnelValue;
      this.scoreValue = scoreValue;
      this.scoreLinesValue = scoreLinesValue;
      this.linesQuadsValue = linesQuadsValue;
      this.linesQuadsValue2LinesQuads = linesQuadsValue2;
      this.linesQuadsValue3LinesQuads = linesQuadsValue3;
      this.linesQuadsValue4LinesQuads = linesQuadsValue4;
      this.scoreCellsValue = scoreCellsValue;
      this.cellsTunnelValue = cellsTunnelValue;
      this.scoreCellsAmount = scoreCellsAmount;
      this.blockX = value;
      this.blockY = value2;
      this.blockZ = value3;
      this.table = amounts;
      this.linesQuadsTable = amounts2;
      this.copyOf = copyOf;
      this.timesLinesQuads = times;
   }

   public int getAmounts() {
      return this.table.length / 6;
   }

   public boolean canLinesQuads(int linesQuadsLimit, int linesQuadsLimit2, int linesQuadsLimit3, int linesQuadsValue) {
      return linesQuadsLimit >= this.scoreValue - linesQuadsValue
         && linesQuadsLimit <= this.linesQuadsValue2LinesQuads + linesQuadsValue
         && linesQuadsLimit2 >= this.scoreLinesValue - linesQuadsValue
         && linesQuadsLimit2 <= this.linesQuadsValue3LinesQuads + linesQuadsValue
         && linesQuadsLimit3 >= this.linesQuadsValue - linesQuadsValue
         && linesQuadsLimit3 <= this.linesQuadsValue4LinesQuads + linesQuadsValue;
   }

   public boolean canLinesQuads(LinesQuads linesQuads) {
      if (linesQuads.scoreValue <= this.linesQuadsValue2LinesQuads
         && linesQuads.linesQuadsValue2LinesQuads >= this.scoreValue
         && linesQuads.scoreLinesValue <= this.linesQuadsValue3LinesQuads
         && linesQuads.linesQuadsValue3LinesQuads >= this.scoreLinesValue
         && linesQuads.linesQuadsValue <= this.linesQuadsValue4LinesQuads
         && linesQuads.linesQuadsValue4LinesQuads >= this.linesQuadsValue) {
         long[] times = this.timesLinesQuads;
         long[] timesLinesQuads2 = linesQuads.timesLinesQuads;
         int index = 0;
         int index2 = 0;

         while (index < times.length && index2 < timesLinesQuads2.length) {
            if (times[index] == timesLinesQuads2[index2]) {
               return true;
            }

            if (times[index] < timesLinesQuads2[index2]) {
               index++;
            } else {
               index2++;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean canLinesQuads(int linesQuadsLimit, int buriedOpenLimit, int linesQuadsLimit3) {
      return linesQuadsLimit >= this.scoreValue
            && linesQuadsLimit <= this.linesQuadsValue2LinesQuads
            && buriedOpenLimit >= this.scoreLinesValue
            && buriedOpenLimit <= this.linesQuadsValue3LinesQuads
            && linesQuadsLimit3 >= this.linesQuadsValue
            && linesQuadsLimit3 <= this.linesQuadsValue4LinesQuads
         ? Arrays.binarySearch(this.timesLinesQuads, EspHoleApi.getTime(linesQuadsLimit, buriedOpenLimit, linesQuadsLimit3)) >= 0
         : false;
   }

   public int getLinesQuadsTable() {
      return this.linesQuadsTable.length / 12;
   }
}
