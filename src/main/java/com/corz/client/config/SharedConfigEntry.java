package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public final class SharedConfigEntry {
   public long uniqueDownloadCount;
   public int formatVersion;
   public boolean owner;
   public String name;
   public long updatedAt;
   public String currentVersionId;
   public String id;
   public String sha256;
   public long likeCount;
   public String minecraftVersion;
   public boolean featured;
   public boolean verified;
   public boolean liked;
   public long createdAt;
   public final List<String> tags = new ArrayList<>();
   public String corzVersion;
   public String author;
   public String description;
   public long downloadCount;

   private static String getJsonString(JsonObject json, String primitiveJsonText) {
      return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsString() : null;
   }

   private static int getJsonInt(JsonObject json, String primitiveJsonText, int primitiveJson) {
      try {
         return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsInt() : primitiveJson;
      } catch (Exception e) {
         return primitiveJson;
      }
   }

   private static long getJsonLong(JsonObject json, String primitiveJsonText, long primitiveJson) {
      try {
         return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsLong() : primitiveJson;
      } catch (Exception e) {
         return primitiveJson;
      }
   }

   private static boolean getJsonBoolean(JsonObject json, String primitiveJsonText, boolean primitiveJson) {
      try {
         return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsBoolean() : primitiveJson;
      } catch (Exception e) {
         return primitiveJson;
      }
   }

   public static SharedConfigEntry fromJson(JsonObject json) {
      if (json == null) {
         return null;
      } else {
         SharedConfigEntry versionJsonUtils = new SharedConfigEntry();
         versionJsonUtils.id = getJsonString(json, "id");
         versionJsonUtils.name = getJsonString(json, "name");
         versionJsonUtils.description = getJsonString(json, "description");
         versionJsonUtils.author = getJsonString(json, "author");
         if (json.has("tags") && json.get("tags").isJsonArray()) {
            for (JsonElement jsonElement : json.getAsJsonArray("tags")) {
               if (jsonElement.isJsonPrimitive()) {
                  versionJsonUtils.tags.add(jsonElement.getAsString());
               }
            }
         }

         versionJsonUtils.minecraftVersion = getJsonString(json, "minecraftVersion");
         versionJsonUtils.corzVersion = getJsonString(json, "corzVersion");
         versionJsonUtils.formatVersion = getJsonInt(json, "formatVersion", 1);
         versionJsonUtils.sha256 = getJsonString(json, "sha256");
         versionJsonUtils.currentVersionId = getJsonString(json, "currentVersionId");
         versionJsonUtils.verified = getJsonBoolean(json, "verified", false);
         versionJsonUtils.featured = getJsonBoolean(json, "featured", false);
         versionJsonUtils.downloadCount = getJsonLong(json, "downloadCount", 0L);
         versionJsonUtils.uniqueDownloadCount = getJsonLong(json, "uniqueDownloadCount", 0L);
         versionJsonUtils.likeCount = getJsonLong(json, "likeCount", 0L);
         versionJsonUtils.updatedAt = getJsonLong(json, "updatedAt", 0L);
         versionJsonUtils.createdAt = getJsonLong(json, "createdAt", 0L);
         versionJsonUtils.liked = getJsonBoolean(json, "liked", false);
         versionJsonUtils.owner = getJsonBoolean(json, "owner", false);
         return versionJsonUtils;
      }
   }
}
