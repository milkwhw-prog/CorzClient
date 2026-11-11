package com.corz.client.utils;

import java.util.ArrayList;
import com.corz.client.utils.ProofSupportUtils;

public record DisabledMaskData(long target, int disabledMask, String schematicBlockState) {

   public static int getValue(int[] disabledSlots) {
      int disabledMaskValue = 0;
      if (disabledSlots != null) {
         for (int disabledMaskLimit : disabledSlots) {
            if (disabledMaskLimit >= 0 && disabledMaskLimit < 9) {
               disabledMaskValue |= 1 << disabledMaskLimit;
            }
         }
      }

      return disabledMaskValue;
   }

   public static String formatText(int mask) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < 9; i++) {
         if (i > 0 && i % 3 == 0) {
            stringBuilder.append('/');
         }

         stringBuilder.append((char)(canFlag(mask, i) ? '#' : '.'));
      }

      return stringBuilder.toString();
   }

   public String formatDisabledMask() {
      return "target=" + ProofSupportUtils.formatText(this.target) + " desiredMask=" + formatText(this.disabledMask) + " state=" + this.schematicBlockState;
   }

   public static int getValue(boolean[] disabled) {
      int disabledMaskValue = 0;
      if (disabled != null) {
         for (int i = 0; i < Math.min(9, disabled.length); i++) {
            if (disabled[i]) {
               disabledMaskValue |= 1 << i;
            }
         }
      }

      return disabledMaskValue;
   }

   public static int[] getValues(int current, int desired) {
      return getArray((current ^ desired) & 511);
   }

   public static int[] getArray(int mask) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < 9; i++) {
         if ((mask & 1 << i) != 0) {
            arrayList.add(i);
         }
      }

      return arrayList.stream().mapToInt(obj -> ((Integer) obj).intValue()).toArray();
   }

   public static boolean canFlag(int mask, int slot) {
      return (mask & 1 << slot) != 0;
   }
}
