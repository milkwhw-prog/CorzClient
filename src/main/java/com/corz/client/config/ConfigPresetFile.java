package com.corz.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class ConfigPresetFile {
   @SerializedName("createdAt")
   public String createdAt;
   @SerializedName("minecraftVersion")
   public String minecraftVersion;
   @SerializedName("name")
   public String name;
   @SerializedName("content")
   public JsonObject content;
   @SerializedName("updatedAt")
   public String updatedAt;
   @SerializedName("clientVersion")
   public String clientVersion;
   @SerializedName("description")
   public String description;
   @SerializedName("tags")
   public List<String> tagsContentItems;
   @SerializedName("author")
   public String author;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   @SerializedName("formatVersion")
   public int versionFormatValue = 1;

   public String toJsonString() {
      return GSON.toJson(this);
   }

   public ConfigPresetFile deepCopy() {
      return fromJsonObject(JsonParser.parseString(this.toJsonString()).getAsJsonObject());
   }

   public byte[] toJsonBytes() {
      return GSON.toJson(this).getBytes(StandardCharsets.UTF_8);
   }

   public static ConfigPresetFile fromJsonObject(JsonObject json) {
      ConfigPresetFile jsonVersion = new ConfigPresetFile();
      jsonVersion.versionFormatValue = json.has("formatVersion") ? json.get("formatVersion").getAsInt() : 1;
      jsonVersion.clientVersion = readStringOrNull(json, "clientVersion");
      jsonVersion.minecraftVersion = readStringOrNull(json, "minecraftVersion");
      jsonVersion.name = readStringOrNull(json, "name");
      jsonVersion.description = readStringOrNull(json, "description");
      jsonVersion.author = readStringOrNull(json, "author");
      jsonVersion.createdAt = readStringOrNull(json, "createdAt");
      jsonVersion.updatedAt = readStringOrNull(json, "updatedAt");
      if (json.has("tags") && json.get("tags").isJsonArray()) {
         jsonVersion.tagsContentItems = new ArrayList<>();

         for (JsonElement jsonElement : json.getAsJsonArray("tags")) {
            if (jsonElement.isJsonPrimitive()) {
               jsonVersion.tagsContentItems.add(jsonElement.getAsString());
            }
         }
      }

      if (json.has("content") && json.get("content").isJsonObject()) {
         jsonVersion.content = json.getAsJsonObject("content").deepCopy();
      }

      return jsonVersion;
   }

   private static String readStringOrNull(JsonObject json, String primitiveJsonText) {
      return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsString() : null;
   }

   public String computeShaString() {
      return sha256Hex(this.toJsonBytes());
   }

   public JsonObject getOrCreateSection(String jsonText) {
      if (!this.content.has(jsonText) || !this.content.get(jsonText).isJsonObject()) {
         this.content.add(jsonText, new JsonObject());
      }

      return this.content.getAsJsonObject(jsonText);
   }

   public static String sha256Hex(byte[] shaBytes) {
      try {
         MessageDigest instance = MessageDigest.getInstance("SHA-256");
         byte[] digest = instance.digest(shaBytes);
         StringBuilder stringBuilder = new StringBuilder(digest.length * 2);

         for (byte shaValue : digest) {
            stringBuilder.append(Character.forDigit(shaValue >> 4 & 15, 16)).append(Character.forDigit(shaValue & 15, 16));
         }

         return stringBuilder.toString();
      } catch (Exception e) {
         return "";
      }
   }

   public ConfigPresetFile() {
      this.content = new JsonObject();
   }

   public static ConfigPresetFile parseBytes(byte[] jsonVersionBytes, ConfigJsonValidator.Result[] results) {
      JsonObject[] jsonObjects = new JsonObject[1];
      ConfigJsonValidator.Result configJson = ConfigJsonValidator.validateConfigBytes(jsonVersionBytes, jsonObjects);
      if (results != null && results.length > 0) {
         results[0] = configJson;
      }

      return !configJson.isValid() ? null : fromJsonObject(jsonObjects[0]);
   }

   public JsonObject getOrCreateModules() {
      if (!this.content.has("modules") || !this.content.get("modules").isJsonObject()) {
         this.content.add("modules", new JsonObject());
      }

      return this.content.getAsJsonObject("modules");
   }
}
