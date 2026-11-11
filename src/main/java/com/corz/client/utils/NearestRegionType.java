package com.corz.client.utils;

public enum NearestRegionType {
   DYNAMIC("Dynamic"),
   REGION("Region"),
   LAYER("Layer"),
   NEAREST("Nearest");

   public final String reconnectingSessionText;

   private NearestRegionType(String reconnectingSessionText) {
      this.reconnectingSessionText = reconnectingSessionText;
   }

   public static NearestRegionType getReconnectingDynamic(String reconnectingDynamicText) {
      if (reconnectingDynamicText != null) {
         for (NearestRegionType reconnectingDynamic : values()) {
            if (reconnectingDynamic.reconnectingSessionText.equalsIgnoreCase(reconnectingDynamicText)) {
               return reconnectingDynamic;
            }
         }
      }

      return DYNAMIC;
   }
}
