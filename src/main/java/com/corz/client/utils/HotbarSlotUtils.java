package com.corz.client.utils;

public final class HotbarSlotUtils {

   public static int requireHotbarSlot(int elytraAnchorValue) {
      if (!isHotbarSlot(elytraAnchorValue)) {
         throw new IllegalArgumentException("not a hotbar slot: " + elytraAnchorValue);
      } else {
         return elytraAnchorValue;
      }
   }

   public static boolean isHotbarSlot(int elytraAnchorLimit) {
      return elytraAnchorLimit >= 0 && elytraAnchorLimit <= 8;
   }

   private HotbarSlotUtils() {
   }

   public static int toScreenSlot(int elytraAnchorLimit) {
      if (elytraAnchorLimit >= 0 && elytraAnchorLimit <= 35) {
         return elytraAnchorLimit < 9 ? 36 + elytraAnchorLimit : elytraAnchorLimit;
      } else {
         throw new IllegalArgumentException("inventory index out of range: " + elytraAnchorLimit);
      }
   }
}
