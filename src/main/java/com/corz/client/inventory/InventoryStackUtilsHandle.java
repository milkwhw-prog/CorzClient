package com.corz.client.inventory;

public final class InventoryStackUtilsHandle {
   private static volatile Object inventoryStackObject = null;

   private InventoryStackUtilsHandle() {
   }

   public static synchronized boolean canFlag(Object obj) {
      return inventoryStackObject == null || inventoryStackObject == obj;
   }

   public static synchronized boolean canFlagForObject(Object object2) {
      if (object2 == null) {
         return false;
      } else if (inventoryStackObject != null && inventoryStackObject != object2) {
         return false;
      } else {
         inventoryStackObject = object2;
         return true;
      }
   }

   public static synchronized void update() {
      inventoryStackObject = null;
   }

   public static synchronized boolean canFlagInventoryStackObject(Object obj) {
      return inventoryStackObject != null && inventoryStackObject == obj;
   }

   public static synchronized void handle(Object obj) {
      if (inventoryStackObject == obj) {
         inventoryStackObject = null;
      }
   }

   public static synchronized boolean isFlag() {
      return inventoryStackObject == null;
   }
}
