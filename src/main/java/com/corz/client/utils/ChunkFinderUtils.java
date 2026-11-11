package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.CraftedExcavation;

public final class ChunkFinderUtils {

   public static int getMin(int chunkFinderBound) {
      return chunkFinderBound < -64 ? -64 : Math.min(chunkFinderBound, -30);
   }

   private ChunkFinderUtils() {
   }

   public static List<CraftedExcavation> getItems(List<CraftedExcavation> list) {
      ArrayList arrayList = new ArrayList(list.size());

      for (CraftedExcavation craftedExcavation : list) {
         if (canFlag(craftedExcavation.posY)) {
            arrayList.add(craftedExcavation);
         }
      }

      return arrayList;
   }

   public static int getMax(int chunkFinderBound) {
      return Math.max(chunkFinderBound, -64);
   }

   public static boolean canFlag(int chunkFinderLimit) {
      return chunkFinderLimit >= -64 && chunkFinderLimit <= -30;
   }

   public static boolean canFlag(int chunkFinderValue, int chunkFinderBound) {
      return getMinForInt(chunkFinderBound) >= getMax(chunkFinderValue);
   }

   public static boolean canFlagForInt(int chunkFinderValue) {
      return chunkFinderValue <= -30 && chunkFinderValue + 15 >= -64;
   }

   public static int getMinForInt(int chunkFinderBound) {
      return Math.min(chunkFinderBound, -30);
   }
}
