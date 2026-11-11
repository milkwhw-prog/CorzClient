package com.corz.client.utils;

public final class HotbarSlotRules {
   public static final int MIN_ELAPSED_TICKS = 1;

   public static boolean isSlotUnavailable(int selectedShieldLimit, int selectedShieldValue, boolean[] selectedShieldFlags, boolean[] selectedShieldFlags2, boolean selectedShieldFlag) {
      if (selectedShieldLimit < 0 || selectedShieldLimit >= 9) {
         return true;
      } else if (isFlagSet(selectedShieldFlags2, selectedShieldLimit)) {
         return true;
      } else {
         return selectedShieldFlag ? false : selectedShieldLimit == selectedShieldValue || isFlagSet(selectedShieldFlags, selectedShieldLimit);
      }
   }

   private static boolean isFlagSet(boolean[] selectedShieldFlags, int index) {
      return selectedShieldFlags != null && index >= 0 && index < selectedShieldFlags.length && selectedShieldFlags[index];
   }

   public static boolean canSwitchSlotAt(long selectedShieldLimit, long selectedShieldValue, boolean selectedShieldFlag) {
      if (selectedShieldValue != Long.MIN_VALUE && selectedShieldLimit >= selectedShieldValue) {
         long selectedShieldLimit2 = selectedShieldLimit - selectedShieldValue;
         return selectedShieldLimit2 >= 1L || selectedShieldFlag && selectedShieldLimit2 >= 1L;
      } else {
         return true;
      }
   }

   private HotbarSlotRules() {
   }

   public static int findAvailableSlot(boolean[] selectedShieldFlags, int selectedShieldLimit, int selectedShieldValue, boolean[] selectedShieldFlags2, boolean[] selectedShieldFlags3, boolean selectedShieldFlag) {
      if (selectedShieldFlags == null) {
         return -1;
      } else if (selectedShieldLimit >= 0
         && selectedShieldLimit < 9
         && isFlagSet(selectedShieldFlags, selectedShieldLimit)
         && !isSlotUnavailable(selectedShieldLimit, selectedShieldValue, selectedShieldFlags2, selectedShieldFlags3, selectedShieldFlag)) {
         return selectedShieldLimit;
      } else {
         int min2 = Math.min(9, selectedShieldFlags.length);

         for (int i = 0; i < min2; i++) {
            if (selectedShieldFlags[i] && !isSlotUnavailable(i, selectedShieldValue, selectedShieldFlags2, selectedShieldFlags3, selectedShieldFlag)) {
               return i;
            }
         }

         return -1;
      }
   }
}
