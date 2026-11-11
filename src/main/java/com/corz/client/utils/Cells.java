package com.corz.client.utils;

public interface Cells {
   static boolean isNextToLiquid(Cells cells, int cellsValue, int i, int unpackLongZ) {
      return hasAnyFlag(cells.getCellFlags(cellsValue, i + 1, unpackLongZ), 6)
         ? true
         : hasAnyFlag(cells.getCellFlags(cellsValue + 1, i, unpackLongZ), 6)
            || hasAnyFlag(cells.getCellFlags(cellsValue - 1, i, unpackLongZ), 6)
            || hasAnyFlag(cells.getCellFlags(cellsValue, i, unpackLongZ + 1), 6)
            || hasAnyFlag(cells.getCellFlags(cellsValue, i, unpackLongZ - 1), 6);
   }

   static boolean isSafeSolidBlock(Cells cells, int cellsValue, int i, int unpackLongZ) {
      int hashPut = cells.getCellFlags(cellsValue, i, unpackLongZ);
      return hasAnyFlag(hashPut, 1) && !hasAnyFlag(hashPut, 506) && !isNextToLiquid(cells, cellsValue, i, unpackLongZ);
   }

   int getCellFlags(int hashPutValue, int i, int unpackLongZ);

   static boolean isNextToLava(Cells cells, int cellsValue, int cellsValue2, int cellsValue3) {
      int[] ints = new int[]{1, -1, 0, 0};
      int[] ints2 = new int[]{0, 0, 1, -1};

      for (int i = 0; i < 4; i++) {
         int hashPutValue = cellsValue + ints[i];
         int unpackLongZ = cellsValue3 + ints2[i];
         if (hasAnyFlag(cells.getCellFlags(hashPutValue, cellsValue2, unpackLongZ), 2) || hasAnyFlag(cells.getCellFlags(hashPutValue, cellsValue2 + 1, unpackLongZ), 2)) {
            return true;
         }

         int hashPut = cells.getCellFlags(hashPutValue, cellsValue2 - 1, unpackLongZ);
         if (hasAnyFlag(hashPut, 2) && !hasAnyFlag(hashPut, 1)) {
            return true;
         }
      }

      return false;
   }

   static boolean hasAnyFlag(int cellsValue, int cellsValue2) {
      return (cellsValue & cellsValue2) != 0;
   }

   static boolean isSolidWithoutHazard(int cellsValue) {
      return (cellsValue & 1) != 0 && (cellsValue & 282) == 0;
   }

   static boolean isFreeOfSolidFlags(int cellsValue) {
      return (cellsValue & 257) == 0;
   }
}
