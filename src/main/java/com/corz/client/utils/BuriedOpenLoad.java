package com.corz.client.utils;

import java.util.Arrays;

final class BuriedOpenLoad {
   private int loadCounter;
   private int[] buriedOpenTable;
   private int buriedOpenValue;
   private int loadLimit;
   private int[] table;

   private void handle(int buriedOpenSize) {
      this.table = new int[buriedOpenSize];
      this.buriedOpenTable = new int[buriedOpenSize];
      Arrays.fill(this.table, -1);
      this.buriedOpenValue = buriedOpenSize - 1;
      this.loadLimit = buriedOpenSize >> 1;
      this.loadCounter = 0;
   }

   void handle(int buriedOpenValue, int buriedOpenValue2) {
      int index = getValueForInt(buriedOpenValue) & this.buriedOpenValue;

      while (true) {
         int buriedOpenValue3 = this.table[index];
         if (buriedOpenValue3 == -1) {
            this.table[index] = buriedOpenValue;
            this.buriedOpenTable[index] = buriedOpenValue2;
            if (++this.loadCounter > this.loadLimit) {
               this.update();
            }

            return;
         }

         if (buriedOpenValue3 == buriedOpenValue) {
            this.buriedOpenTable[index] = buriedOpenValue2;
            return;
         }

         index = index + 1 & this.buriedOpenValue;
      }
   }

   private void update() {
      int[] table2 = this.table;
      int[] buriedOpenValues = this.buriedOpenTable;
      this.handle(table2.length << 1);

      for (int i = 0; i < table2.length; i++) {
         if (table2[i] != -1) {
            this.handle(table2[i], buriedOpenValues[i]);
         }
      }
   }

   int getValue(int buriedOpenValue) {
      int index = getValueForInt(buriedOpenValue) & this.buriedOpenValue;

      while (true) {
         int buriedOpenValue2 = this.table[index];
         if (buriedOpenValue2 == -1) {
            return -1;
         }

         if (buriedOpenValue2 == buriedOpenValue) {
            return this.buriedOpenTable[index];
         }

         index = index + 1 & this.buriedOpenValue;
      }
   }

   private static int getValueForInt(int buriedOpenValue) {
      int buriedOpenValue2 = buriedOpenValue * -1640531535;
      return buriedOpenValue2 ^ buriedOpenValue2 >>> 15;
   }

   BuriedOpenLoad(int buriedOpenLimit) {
      int buriedOpenValue = 16;

      while (buriedOpenValue * 0.5 < buriedOpenLimit) {
         buriedOpenValue <<= 1;
      }

      this.handle(buriedOpenValue);
   }

   void updateLoadCounter() {
      if (this.loadCounter != 0) {
         Arrays.fill(this.table, -1);
         this.loadCounter = 0;
      }
   }
}
