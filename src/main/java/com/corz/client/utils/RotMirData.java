package com.corz.client.utils;

import com.google.gson.JsonObject;

public record RotMirData(int originX, int originY, int originZ, int rotationIndex, int mirrorIndex) {
   public String formatOriginMirror() {
      return this.originX + "," + this.originY + "," + this.originZ + " rot=" + this.rotationIndex + " mirror=" + this.mirrorIndex;
   }

   public JsonObject getPropertyRot() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("ox", this.originX);
      jsonObject.addProperty("oy", this.originY);
      jsonObject.addProperty("oz", this.originZ);
      jsonObject.addProperty("rot", this.rotationIndex);
      jsonObject.addProperty("mir", this.mirrorIndex);
      return jsonObject;
   }

   public static boolean canFlag(int i) {
      return i >= 0 && i <= 3;
   }

   public static RotMirData getRotMir(JsonObject o) {
      if (o != null && o.has("ox") && o.has("oy") && o.has("oz")) {
         try {
            int rotMirValue = o.has("rot") ? o.get("rot").getAsInt() : 0;
            int i = o.has("mir") ? o.get("mir").getAsInt() : 0;
            return canFlag(rotMirValue) && canFlagForInt(i) ? new RotMirData(o.get("ox").getAsInt(), o.get("oy").getAsInt(), o.get("oz").getAsInt(), rotMirValue, i) : null;
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean canFlagForInt(int i) {
      return i >= 0 && i <= 2;
   }
}
