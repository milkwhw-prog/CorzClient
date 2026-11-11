package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import com.corz.client.utils.FacingSingleUtils;

public final class SolidBlockedUtils {
   private SolidBlockedUtils() {
   }

   public static List<long[]> getItems(List<long[]> list, int solidBlockedValue, SolidBlockedUtils.Solid solid, SolidBlockedUtils.Blocked blocked) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      ArrayList arrayList = new ArrayList();

      for (long[] solidBlockedValues : list) {
         List solidBlocked = getSolidBlocked((int)solidBlockedValues[0], (int)solidBlockedValues[1], (int)solidBlockedValues[2], solidBlockedValue, solid, blocked);
         if (solidBlocked != null) {
            for (long[] totalPriceData : (Iterable<long[]>) (solidBlocked)) {
               long packCellPosition = FacingSingleUtils.packCellPosition((int)totalPriceData[0], (int)totalPriceData[1], (int)totalPriceData[2]);
               if (linkedHashSet.add(packCellPosition)) {
                  arrayList.add(totalPriceData);
               }
            }
         }
      }

      arrayList.sort((obj, obj2) -> Long.compare(((long[]) obj)[1], ((long[]) obj2)[1]));
      return arrayList;
   }

   public static List<long[]> getSolidBlocked(int solidBlockedValue, int solidBlockedLimit, int solidBlockedValue2, int supportIslandValue, SolidBlockedUtils.Solid solid, SolidBlockedUtils.Blocked blocked) {
      if (solid.isSolid(solidBlockedValue, solidBlockedLimit, solidBlockedValue2)) {
         return List.of();
      } else {
         for (int i = 1; i <= supportIslandValue; i++) {
            int solidBlockedValue3 = solidBlockedLimit - i;
            if (solid.isSolid(solidBlockedValue, solidBlockedValue3, solidBlockedValue2)) {
               ArrayList arrayList = new ArrayList();

               for (int j = solidBlockedValue3 + 1; j <= solidBlockedLimit; j++) {
                  if (blocked != null && blocked.isBlocked(solidBlockedValue, j, solidBlockedValue2)) {
                     return null;
                  }

                  arrayList.add(new long[]{solidBlockedValue, j, solidBlockedValue2});
               }

               return arrayList;
            }
         }

         return null;
      }
   }

   public static long[] getTimes(int solidBlockedValue, int solidBlockedLimit, int solidBlockedValue3, int searchRadius, SolidBlockedUtils.Solid solid, SolidBlockedUtils.Blocked blocked) {
      List solidBlocked = getSolidBlocked(solidBlockedValue, solidBlockedLimit, solidBlockedValue3, searchRadius, solid, blocked);
      return solidBlocked != null && !solidBlocked.isEmpty() ? (long[])solidBlocked.get(0) : null;
   }

   public interface Blocked {
      boolean isBlocked(int blockedValue, int j, int blockedValue3);
   }

   public interface Solid {
      boolean isSolid(int solidValue, int solidBlockedLimit, int solidValue3);
   }
}
