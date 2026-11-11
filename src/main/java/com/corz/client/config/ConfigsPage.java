package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public final class ConfigsPage {
   public int pageSize;
   public int page;
   public int totalPages;
   public int total;
   public String sortKey;
   public final List<SharedConfigEntry> configs = new ArrayList<>();

   public static ConfigsPage fromJson(JsonObject json) {
      ConfigsPage configsJsonUtils = new ConfigsPage();
      if (json == null) {
         return configsJsonUtils;
      } else {
         configsJsonUtils.sortKey = json.has("sort") ? json.get("sort").getAsString() : null;
         configsJsonUtils.page = getIntOrDefault(json, "page", 1);
         configsJsonUtils.pageSize = getIntOrDefault(json, "pageSize", 20);
         configsJsonUtils.total = getIntOrDefault(json, "total", 0);
         configsJsonUtils.totalPages = getIntOrDefault(json, "totalPages", 1);
         JsonArray jsonArray = json.has("configs") && json.get("configs").isJsonArray() ? json.getAsJsonArray("configs") : null;
         if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
               if (jsonElement.isJsonObject()) {
                  SharedConfigEntry versionTags = SharedConfigEntry.fromJson(jsonElement.getAsJsonObject());
                  if (versionTags != null) {
                     configsJsonUtils.configs.add(versionTags);
                  }
               }
            }
         }

         return configsJsonUtils;
      }
   }

   private static int getIntOrDefault(JsonObject json, String primitiveJsonText, int primitiveJson) {
      try {
         return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsInt() : primitiveJson;
      } catch (Exception e) {
         return primitiveJson;
      }
   }
}
