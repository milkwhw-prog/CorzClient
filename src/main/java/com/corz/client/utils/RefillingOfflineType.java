package com.corz.client.utils;

public enum RefillingOfflineType {
   IDLE("Idle"),
   BUILDING("Building"),
   MOVING("Moving"),
   REFILLING("Refilling"),
   PAUSED("Paused"),
   FAILED("Failed"),
   OFFLINE("Offline");

   public final String offlineText;

   public static RefillingOfflineType getIdle(String idleText) {
      if (idleText != null) {
         for (RefillingOfflineType idle : values()) {
            if (idle.name().equalsIgnoreCase(idleText)) {
               return idle;
            }
         }
      }

      return IDLE;
   }

   private RefillingOfflineType(String offlineText) {
      this.offlineText = offlineText;
   }
}
