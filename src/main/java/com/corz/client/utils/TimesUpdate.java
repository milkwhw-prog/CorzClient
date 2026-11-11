package com.corz.client.utils;

import java.util.Arrays;

final class TimesUpdate {
   private int loadCounter;
   private long[] updateTable;
   private int updateValue;
   private int loadThreshold;

   private void updateTimes() {
      long[] times = this.updateTable;
      this.handleTimes(times.length << 1);

      for (long updateValue : times) {
         if (updateValue != Long.MIN_VALUE) {
            this.canTimes(updateValue);
         }
      }
   }

   int getValue() {
      return this.loadCounter;
   }

   TimesUpdate(int updateLimit) {
      int updateValue = 16;

      while (updateValue * 0.6 < updateLimit) {
         updateValue <<= 1;
      }

      this.handleTimes(updateValue);
   }

   void updateTimesLoadCounter() {
      if (this.loadCounter != 0) {
         Arrays.fill(this.updateTable, Long.MIN_VALUE);
         this.loadCounter = 0;
      }
   }

   private static int getValue(long updateValue) {
      long updateValue2 = updateValue * -7046029254386353131L;
      return (int)(updateValue2 ^ updateValue2 >>> 29 ^ updateValue2 >>> 47);
   }

   boolean canTimes(long updateValue) {
      int index = getValue(updateValue) & this.updateValue;

      while (true) {
         long updateValue2 = this.updateTable[index];
         if (updateValue2 == Long.MIN_VALUE) {
            this.updateTable[index] = updateValue;
            if (++this.loadCounter > this.loadThreshold) {
               this.updateTimes();
            }

            return true;
         }

         if (updateValue2 == updateValue) {
            return false;
         }

         index = index + 1 & this.updateValue;
      }
   }

   boolean canTimesForLong(long updateValue) {
      int index = getValue(updateValue) & this.updateValue;

      while (true) {
         long updateValue2 = this.updateTable[index];
         if (updateValue2 == Long.MIN_VALUE) {
            return false;
         }

         if (updateValue2 == updateValue) {
            return true;
         }

         index = index + 1 & this.updateValue;
      }
   }

   private void handleTimes(int updateSize) {
      this.updateTable = new long[updateSize];
      Arrays.fill(this.updateTable, Long.MIN_VALUE);
      this.updateValue = updateSize - 1;
      this.loadThreshold = (int)(updateSize * 0.6);
      this.loadCounter = 0;
   }
}
