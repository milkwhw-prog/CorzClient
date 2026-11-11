package com.corz.client.utils;

import java.util.Arrays;

public final class TimesBuilder {
   private int tableSlotIndex;
   private final long[] builderTable = new long[8];
   private final int[] table = new int[8];
   private int builderCount;
   private int builderCheckCount;
   private int builderValue = Integer.MIN_VALUE;

   public boolean canTimes(int builderValue, int floorDivValue, int builderValue3, int value) {
      if (value != this.builderValue) {
         this.builderValue = value;
         Arrays.fill(this.builderTable, Long.MIN_VALUE);
         Arrays.fill(this.table, 0);
         this.tableSlotIndex = 0;
      }

      long time4 = getTime(builderValue, floorDivValue, builderValue3);

      for (int i = 0; i < 8; i++) {
         if (this.builderTable[i] == time4) {
            if (this.table[i] >= 2) {
               this.builderCheckCount++;
               return false;
            }

            this.table[i]++;
            this.builderCount++;
            return true;
         }
      }

      this.builderTable[this.tableSlotIndex] = time4;
      this.table[this.tableSlotIndex] = 1;
      this.tableSlotIndex = (this.tableSlotIndex + 1) % 8;
      this.builderCount++;
      return true;
   }

   public int getTimes(int builderValue, int floorDivValue, int builderValue3) {
      long time4 = getTime(builderValue, floorDivValue, builderValue3);

      for (int i = 0; i < 8; i++) {
         if (this.builderTable[i] == time4) {
            return this.table[i];
         }
      }

      return 0;
   }

   public int getCheckCount() {
      return this.builderCheckCount;
   }

   public void updateTimes() {
      Arrays.fill(this.builderTable, Long.MIN_VALUE);
      Arrays.fill(this.table, 0);
      this.tableSlotIndex = 0;
      this.builderValue = Integer.MIN_VALUE;
      this.builderCheckCount = this.builderCount = 0;
   }

   public int getHitCount() {
      return this.builderCount;
   }

   private static long getTime(int builderValue, int floorDivValue, int builderValue3) {
      return Math.floorDiv(builderValue, 2) * 73856093L ^ Math.floorDiv(floorDivValue, 2) * 19349663L ^ Math.floorDiv(builderValue3, 2) * 83492791L;
   }

   public TimesBuilder() {
      this.updateTimes();
   }
}
