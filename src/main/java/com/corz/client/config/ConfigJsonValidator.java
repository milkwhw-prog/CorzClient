package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import com.corz.client.utils.AutoRiggerUtils;
import com.corz.client.utils.TokenVersionUtils;

public final class ConfigJsonValidator {
   private static boolean isStringJson(JsonElement json) {
      return json != null && json.isJsonPrimitive() && ((JsonPrimitive)json).isString();
   }

   private static boolean isBooleanJson(JsonElement json) {
      return json != null && json.isJsonPrimitive() && ((JsonPrimitive)json).isBoolean();
   }

   private static int validateAndMeasureDepth(JsonElement json, int jsonExceeds, ConfigJsonValidator.Result result) {
      long jsonExceedsValue = -4925703570568547278L;
      if (jsonExceeds > 13) {
         return jsonExceeds;
      } else {
         long jsonExceedsValue2 = jsonExceedsValue ^ ((long)jsonExceeds << 32 ^ jsonExceedsValue) & -4294967296L;
         if (json.isJsonObject()) {
            for (Entry entry : json.getAsJsonObject().entrySet()) {
               if (TokenVersionUtils.canCardnumberCreditcard((String)entry.getKey())) {
                  result.addError("Config contains a forbidden field: " + (String)entry.getKey());
               }

               jsonExceedsValue2 ^= ((long)Math.max((int)(jsonExceedsValue2 >>> 32), validateAndMeasureDepth((JsonElement)entry.getValue(), jsonExceeds + 1, result)) << 32 ^ jsonExceedsValue2) & -4294967296L;
            }
         } else if (json.isJsonArray()) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
               jsonExceedsValue2 ^= ((long)Math.max((int)(jsonExceedsValue2 >>> 32), validateAndMeasureDepth(jsonElement, jsonExceeds + 1, result)) << 32 ^ jsonExceedsValue2) & -4294967296L;
            }
         } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() && json.getAsString().length() > 2048) {
            result.addError("A string value exceeds 2048 characters.");
         }

         return (int)(jsonExceedsValue2 >>> 32);
      }
   }

   public static ConfigJsonValidator.Result validateConfigBytes(byte[] configJsonBytes, JsonObject[] jsonObjects) {
      ConfigJsonValidator.Result configJson = new ConfigJsonValidator.Result();
      if (configJsonBytes != null && configJsonBytes.length != 0) {
         if (configJsonBytes.length > 1048576) {
            configJson.addError("Config is too large (" + configJsonBytes.length + " bytes; limit " + 1048576 + ").");
            return configJson;
         } else {
            String configJsonText = new String(configJsonBytes, StandardCharsets.UTF_8);
            if (!Arrays.equals(configJsonText.getBytes(StandardCharsets.UTF_8), configJsonBytes)) {
               configJson.addError("Config is not valid UTF-8.");
               return configJson;
            } else {
               JsonObject jsonObject;
               try {
                  JsonElement jsonElement = JsonParser.parseString(configJsonText);
                  if (!jsonElement.isJsonObject()) {
                     configJson.addError("Config root must be a JSON object.");
                     return configJson;
                  }

                  jsonObject = jsonElement.getAsJsonObject();
               } catch (Exception e) {
                  configJson.addError("Config is not valid JSON.");
                  return configJson;
               }

               validateConfigRoot(jsonObject, configJson);
               if (jsonObjects != null && jsonObjects.length > 0) {
                  jsonObjects[0] = jsonObject;
               }

               return configJson;
            }
         }
      } else {
         configJson.addError("Config is empty.");
         return configJson;
      }
   }

   public static ConfigJsonValidator.Result getClassForByteArray(byte[] classBytes) {
      return validateConfigBytes(classBytes, null);
   }

   private ConfigJsonValidator() {
   }

   private static boolean isIntegerJson(JsonElement json) {
      return json != null
         && json.isJsonPrimitive()
         && json.getAsJsonPrimitive().isNumber()
         && json.getAsDouble() == Math.floor(json.getAsDouble())
         && !Double.isInfinite(json.getAsDouble());
   }

   private static void validateConfigRoot(JsonObject json, ConfigJsonValidator.Result result) {
      long tagsContentValue = -195232249557855442L;
      long tagsContentValue2 = tagsContentValue ^ ((long)validateAndMeasureDepth(json, 1, result) << 32 ^ tagsContentValue) & -4294967296L;
      if ((int)(tagsContentValue2 >>> 32) > 12) {
         result.addError("Config nesting is too deep (" + (int)(tagsContentValue2 >>> 32) + "; limit " + 12 + ").");
      }

      if (json.has("formatVersion") && isIntegerJson(json.get("formatVersion"))) {
         long tagsContentValue3 = tagsContentValue2 ^ (json.get("formatVersion").getAsInt() ^ tagsContentValue2) & 4294967295L;
         if ((int)tagsContentValue3 < 1) {
            result.addError("Config formatVersion " + (int)tagsContentValue3 + " is too old.");
         } else if ((int)tagsContentValue3 > 1) {
            result.addError("Config formatVersion " + (int)tagsContentValue3 + " is newer than this client supports.");
         }
      } else {
         result.addError("Missing or non-integer formatVersion.");
      }

      for (Entry entry : json.entrySet()) {
         String tagsContentText = (String)entry.getKey();
         if (TokenVersionUtils.canCardnumberCreditcard(tagsContentText)) {
            result.addError("Config contains a forbidden field: " + tagsContentText);
         } else if (!TokenVersionUtils.CONTENT_VERSION_SET.contains(tagsContentText)) {
            result.addError("Config has an unknown top-level field: " + tagsContentText);
         }
      }

      String stringField = getStringField(json, "name");
      if (stringField != null && !stringField.isBlank()) {
         if (stringField.length() < 3 || stringField.length() > 64) {
            result.addError("Config name must be 3\u201364 characters.");
         }

         if (TokenVersionUtils.canFlag(stringField)) {
            result.addError("Config name must not look like a file path.");
         }
      } else {
         result.addError("Config name is required.");
      }

      String stringField3 = getStringField(json, "description");
      if (stringField3 != null && stringField3.length() > 1000) {
         result.addError("Config description exceeds 1000 characters.");
      }

      if (json.has("tags")) {
         if (!json.get("tags").isJsonArray()) {
            result.addError("tags must be an array.");
         } else {
            JsonArray asJsonArray = json.getAsJsonArray("tags");
            if (asJsonArray.size() > 8) {
               result.addError("Too many tags (max 8).");
            }

            for (JsonElement jsonElement : asJsonArray) {
               if (!isStringJson(jsonElement)) {
                  result.addError("Every tag must be a string.");
               }
            }
         }
      }

      if (json.has("content")) {
         if (!json.get("content").isJsonObject()) {
            result.addError("content must be an object.");
         } else {
            JsonObject asJsonObject = json.getAsJsonObject("content");

            for (String keyItem : asJsonObject.keySet()) {
               if (!TokenVersionUtils.CONTENT_MODULES_SET.contains(keyItem)) {
                  result.addError("content has an unknown section: " + keyItem);
               }
            }

            if (asJsonObject.has("modules")) {
               if (!asJsonObject.get("modules").isJsonObject()) {
                  result.addError("content.modules must be an object.");
               } else {
                  validateModules(asJsonObject.getAsJsonObject("modules"), result);
               }
            }
         }
      }
   }

   private static void validateModules(JsonObject json, ConfigJsonValidator.Result result) {
      long settingsBindValue = 1659213275396727339L;
      if (json.size() > 512) {
         result.addError("Too many modules (" + json.size() + "; limit " + 512 + ").");
      }

      for (Entry entry : json.entrySet()) {
         String settingsBindText = (String)entry.getKey();
         if (AutoRiggerUtils.isContains(settingsBindText)) {
            result.addWarning("Skipped unsupported module: " + settingsBindText);
         } else if (!((JsonElement)entry.getValue()).isJsonObject()) {
            result.addError("Module '" + settingsBindText + "' must be an object.");
         } else {
            JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
            if (asJsonObject.has("enabled") && !isBooleanJson(asJsonObject.get("enabled"))) {
               result.addError("Module '" + settingsBindText + "' has a non-boolean enabled.");
            }

            if (asJsonObject.has("bindKey")) {
               if (!isIntegerJson(asJsonObject.get("bindKey"))) {
                  result.addError("Module '" + settingsBindText + "' has a non-integer bindKey.");
               } else if (!ConfigValueSanitizer.isValidKeyCode(asJsonObject.get("bindKey").getAsInt())) {
                  result.addWarning("Module '" + settingsBindText + "' had an out-of-range keybind; it will be treated as unbound.");
               }
            }

            if (asJsonObject.has("settings")) {
               if (!asJsonObject.get("settings").isJsonObject()) {
                  result.addError("Module '" + settingsBindText + "' settings must be an object.");
               } else {
                  settingsBindValue ^= ((long)asJsonObject.getAsJsonObject("settings").size() << 32 ^ settingsBindValue) & -4294967296L;
                  if ((int)(settingsBindValue >>> 32) > 256) {
                     result.addError("Module '" + settingsBindText + "' has too many settings (" + (int)(settingsBindValue >>> 32) + ").");
                  }
               }
            }
         }
      }
   }

   public static JsonObject parseJsonObject(byte[] jsonParseBytes) {
      try {
         JsonElement jsonElement = JsonParser.parseString(new String(jsonParseBytes, StandardCharsets.UTF_8));
         return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
      } catch (Exception e) {
         return null;
      }
   }

   private static String getStringField(JsonObject json, String jsonConfigText) {
      return json.has(jsonConfigText) && isStringJson(json.get(jsonConfigText)) ? json.get(jsonConfigText).getAsString() : null;
   }

   public static ConfigJsonValidator.Result validateConfigObject(JsonObject json) {
      ConfigJsonValidator.Result config = new ConfigJsonValidator.Result();
      if (json == null) {
         config.addError("Config is null.");
         return config;
      } else {
         validateConfigRoot(json, config);
         return config;
      }
   }

   public static final class Result {
      public final List<String> errors = new ArrayList<>();
      public final List<String> warnings = new ArrayList<>();

      public String getFirstError() {
         return this.errors.isEmpty() ? null : this.errors.get(0);
      }

      public void addError(String jsonConfigText) {
         this.errors.add(jsonConfigText);
      }

      public void addWarning(String jsonConfigText) {
         this.warnings.add(jsonConfigText);
      }

      public boolean isValid() {
         return this.errors.isEmpty();
      }
   }
}
