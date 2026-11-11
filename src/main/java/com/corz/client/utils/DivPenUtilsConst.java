package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.corz.client.utils.CraftedExcavation;
import com.corz.client.utils.ExcavationCraftedType;
import com.corz.client.utils.ExcavationCluster;

public final class DivPenUtilsConst {

   public static int getCraftedExcavation(List<CraftedExcavation> list) {
      ArrayList arrayList = new ArrayList();

      for (CraftedExcavation craftedExcavation : list) {
         if (craftedExcavation.excavationCraftedType == ExcavationCraftedType.CRAFTED) {
            arrayList.add(craftedExcavation);
         }
      }

      return getValueForList(arrayList) + getMin(arrayList) + getValue(list);
   }

   public static int getMin(List<CraftedExcavation> list) {
      if (list.size() < 3) {
         return 0;
      } else {
         HashSet hashSet = new HashSet();

         for (CraftedExcavation craftedExcavation : list) {
            hashSet.add(getTime(craftedExcavation.posX, craftedExcavation.posY, craftedExcavation.posZ));
         }

         int divPenValue = 0;

         for (CraftedExcavation craftedExcavation2 : list) {
            int divPenValue2 = divPenValue + getValue(hashSet, craftedExcavation2.posX, craftedExcavation2.posY, craftedExcavation2.posZ, 1, 0, 0);
            int divPenValue3 = divPenValue2 + getValue(hashSet, craftedExcavation2.posX, craftedExcavation2.posY, craftedExcavation2.posZ, 0, 1, 0);
            divPenValue = divPenValue3 + getValue(hashSet, craftedExcavation2.posX, craftedExcavation2.posY, craftedExcavation2.posZ, 0, 0, 1);
         }

         return Math.min(divPenValue * 3, 12);
      }
   }

   public static int getValue(ExcavationCluster maxCrafted) {
      return getCraftedExcavation(maxCrafted.getCells());
   }

   public static int getValue(List<CraftedExcavation> list) {
      int[] ints = new int[6];

      for (CraftedExcavation craftedExcavation : list) {
         if (craftedExcavation.faceIndex >= 0 && craftedExcavation.faceIndex < 6) {
            ints[craftedExcavation.faceIndex]++;
         }
      }

      int divPenLimit = 0;

      for (int intsItem : ints) {
         if (intsItem > divPenLimit) {
            divPenLimit = intsItem;
         }
      }

      return divPenLimit >= 3 ? Math.min(divPenLimit, 8) : 0;
   }

   public static int getValueForList(List<CraftedExcavation> list) {
      if (list.size() < 6) {
         return 0;
      } else {
         HashMap hashMap = new HashMap();
         int divPenLimit = 0;

         for (CraftedExcavation craftedExcavation : list) {
            int merge = ((int) (Integer) (hashMap.merge(craftedExcavation.posY, 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2))));
            if (merge > divPenLimit) {
               divPenLimit = merge;
            }
         }

         return divPenLimit >= 6 ? Math.min(divPenLimit, 12) : 0;
      }
   }

   private DivPenUtilsConst() {
   }

   private static long getTime(int divPenValue, int divPenValue2, int divPenValue3) {
      return divPenValue & 67108863L | (divPenValue3 & 67108863L) << 26 | (long)(divPenValue2 & 4095) << 52;
   }

   private static int getValue(Set<Long> hashSet, int divPenValue, int divPenValue2, int divPenValue3, int divPenFactor, int divPenFactor2, int divPenFactor3) {
      if (hashSet.contains(getTime(divPenValue - divPenFactor, divPenValue2 - divPenFactor2, divPenValue3 - divPenFactor3))) {
         return 0;
      } else {
         int divPenFactor4 = 1;

         while (hashSet.contains(getTime(divPenValue + divPenFactor * divPenFactor4, divPenValue2 + divPenFactor2 * divPenFactor4, divPenValue3 + divPenFactor3 * divPenFactor4))) {
            divPenFactor4++;
         }

         return divPenFactor4 >= 3 ? 1 : 0;
      }
   }
}
