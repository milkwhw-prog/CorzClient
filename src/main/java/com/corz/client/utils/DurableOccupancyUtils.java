package com.corz.client.utils;

public final class DurableOccupancyUtils {
   public static DurableOccupancyUtils.Durability getDurableOccupancy(
      boolean durableOccupancyFlag, boolean durableOccupancyFlag2, boolean durableOccupancyFlag3, boolean durableOccupancyFlag4, boolean durableOccupancyFlag5
   ) {
      if (!durableOccupancyFlag) {
         return DurableOccupancyUtils.Durability.INVALID;
      } else if (durableOccupancyFlag5) {
         return DurableOccupancyUtils.Durability.DURABLE;
      } else if (durableOccupancyFlag2 || durableOccupancyFlag3) {
         return DurableOccupancyUtils.Durability.EPHEMERAL_SCHEMATIC_OCCUPANCY;
      } else {
         return durableOccupancyFlag4 ? DurableOccupancyUtils.Durability.TEMP_DEPENDENT : DurableOccupancyUtils.Durability.DURABLE;
      }
   }

   private DurableOccupancyUtils() {
   }

   public static boolean canDurable(DurableOccupancyUtils.Durability durability) {
      return durability == DurableOccupancyUtils.Durability.DURABLE;
   }

   public static boolean isWithinDurableLimit(int durableOccupancyLimit, int durableOccupancyValue) {
      return durableOccupancyLimit > 0 && durableOccupancyValue == 0;
   }

   public static enum Durability {
      DURABLE,
      EPHEMERAL_SCHEMATIC_OCCUPANCY,
      TEMP_DEPENDENT,
      INVALID;
   }
}
