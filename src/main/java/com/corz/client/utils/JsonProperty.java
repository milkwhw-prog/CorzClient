package com.corz.client.utils;

import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class JsonProperty {
   private final Map<String, Integer> jsonPropertyIntegerMap;

   public static JsonProperty getJsonProperty(JsonObject json) {
      JsonProperty jsonProperty = new JsonProperty();
      if (json != null) {
         for (String jsonPropertyText : json.keySet()) {
            try {
               jsonProperty.jsonPropertyIntegerMap.put(jsonPropertyText, json.get(jsonPropertyText).getAsInt());
            } catch (Exception e) {
            }
         }
      }

      return jsonProperty;
   }

   public JsonObject getProperty() {
      JsonObject jsonObject = new JsonObject();

      for (Entry entry : this.jsonPropertyIntegerMap.entrySet()) {
         jsonObject.addProperty((String)entry.getKey(), (Number)entry.getValue());
      }

      return jsonObject;
   }

   private JsonProperty(Map<String, Integer> map) {
      this.jsonPropertyIntegerMap = map;
   }

   public void handle(String jsonPropertyText, int jsonPropertyLimit) {
      if (jsonPropertyText != null && jsonPropertyLimit > 0) {
         this.jsonPropertyIntegerMap.merge(jsonPropertyText, jsonPropertyLimit, Integer::sum);
      }
   }

   public boolean canFlag(String jsonPropertyText, int jsonPropertyValue) {
      return this.getValue(jsonPropertyText) >= jsonPropertyValue;
   }

   public Map<String, Integer> getMap() {
      return this.jsonPropertyIntegerMap;
   }

   public JsonProperty getJsonProperty() {
      return new JsonProperty(new LinkedHashMap<>(this.jsonPropertyIntegerMap));
   }

   public boolean isEmpty() {
      return this.jsonPropertyIntegerMap.isEmpty();
   }

   public int getValue(String jsonPropertyText) {
      Integer integer = this.jsonPropertyIntegerMap.get(jsonPropertyText);
      return integer == null ? 0 : integer;
   }

   public JsonProperty() {
      this.jsonPropertyIntegerMap = new LinkedHashMap<>();
   }
}
