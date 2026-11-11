package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class CornerCamUtils {
   private static int frameCounter;
   private static final Map<String, Float> CORNER_CAM_VALUES = new HashMap<>();

   public static void update() {
      CORNER_CAM_VALUES.clear();
      frameCounter++;
   }

   public static int getValue() {
      return frameCounter;
   }

   public static void handle(String cornerCamText, float cornerCamValue) {
      CORNER_CAM_VALUES.put(cornerCamText, cornerCamValue);
      CornerCamUtils.frameCounter++;
   }

   public static float getAmount(String cornerCamText, float cornerCamValue) {
      if (CORNER_CAM_VALUES.isEmpty()) {
         return cornerCamValue;
      } else {
         Float float2 = CORNER_CAM_VALUES.get(cornerCamText);
         return float2 == null ? cornerCamValue : float2;
      }
   }

   private CornerCamUtils() {
   }
}
