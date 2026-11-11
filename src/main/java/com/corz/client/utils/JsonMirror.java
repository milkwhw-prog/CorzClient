package com.corz.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class JsonMirror {
   public static final int JSON_MIRROR_STEP = 1;
   private  int mirror;
   private final LinkedHashMap<Long, JsonMirror.Entry> mirrorRotationMap;
   private static final Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
   private  String schematic = "";
   private  int rotation;

   public List<JsonMirror.Entry> getMirrorRotation(Set<Long> set) {
      var arrayList = new ArrayList();

      for (Map.Entry entry : this.mirrorRotationMap.entrySet()) {
         if (((JsonMirror.Entry)entry.getValue()).flag && !set.contains(entry.getKey())) {
            arrayList.add((JsonMirror.Entry)entry.getValue());
         }
      }

      return arrayList;
   }

   public static JsonMirror getMirrorFormat(String mirrorFormatText) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(mirrorFormatText).getAsJsonObject();
         if (asJsonObject.has("format") && asJsonObject.get("format").getAsInt() == 1) {
            JsonMirror jsonMirror = new JsonMirror(
               asJsonObject.has("schematic") ? asJsonObject.get("schematic").getAsString() : "",
               asJsonObject.has("rotation") ? asJsonObject.get("rotation").getAsInt() : 0,
               asJsonObject.has("mirror") ? asJsonObject.get("mirror").getAsInt() : 0
            );
            if (asJsonObject.has("entries")) {
               for (JsonElement jsonElement : asJsonObject.getAsJsonArray("entries")) {
                  JsonMirror.Entry jsonMirrorEntry = (JsonMirror.Entry)gsonBuilder.fromJson(jsonElement, JsonMirror.Entry.class);
                  if (jsonMirrorEntry != null && jsonMirrorEntry.text != null) {
                     jsonMirror.handleMirrorRotation(jsonMirrorEntry);
                  }
               }
            }

            return jsonMirror;
         } else {
            return null;
         }
      } catch (Exception e) {
         return null;
      }
   }

   public List<JsonMirror.Entry> getMirrorRotation() {
      return new ArrayList<>(this.mirrorRotationMap.values());
   }

   public static float getAmount(float jsonMirrorValue) {
      float jsonMirrorTotal = jsonMirrorValue % 360.0F;
      if (jsonMirrorTotal >= 180.0F) {
         jsonMirrorTotal -= 360.0F;
      }

      if (jsonMirrorTotal < -180.0F) {
         jsonMirrorTotal += 360.0F;
      }

      return jsonMirrorTotal;
   }

   public boolean canMirrorRotation(int mirrorRotationValue, int mirrorRotationValue2, int mirrorRotationValue3) {
      return this.mirrorRotationMap.remove(getTime(mirrorRotationValue, mirrorRotationValue2, mirrorRotationValue3)) != null;
   }

   public JsonMirror() {
      this.mirrorRotationMap = new LinkedHashMap<>();
   }

   public static long getTime(int jsonMirrorValue, int jsonMirrorValue2, int jsonMirrorValue3) {
      return (jsonMirrorValue & 2097151L) << 42 | (jsonMirrorValue2 & 2097151L) << 21 | jsonMirrorValue3 & 2097151L;
   }

   public String getJson() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("format", 1);
      jsonObject.addProperty("schematic", this.schematic);
      jsonObject.addProperty("rotation", this.rotation);
      jsonObject.addProperty("mirror", this.mirror);
      JsonArray jsonArray = new JsonArray();

      for (JsonMirror.Entry entry : this.mirrorRotationMap.values()) {
         jsonArray.add(gsonBuilder.toJsonTree(entry));
      }

      jsonObject.add("entries", jsonArray);
      return gsonBuilder.toJson(jsonObject);
   }

   public List<JsonMirror.Entry> getMirrorRotationMirrorRotationMap() {
      var arrayList = new ArrayList();

      for (JsonMirror.Entry entry : this.mirrorRotationMap.values()) {
         if (entry.flag) {
            arrayList.add(entry);
         }
      }

      return arrayList;
   }

   private static int getValue(int jsonMirrorValue) {
      return jsonMirrorValue << 11 >> 11;
   }

   public void handleMirrorRotation(JsonMirror.Entry entry) {
      if (entry != null) {
         long mirrorRotationValue = getTime(entry.value3, entry.value2, entry.value);
         this.mirrorRotationMap.remove(mirrorRotationValue);
         this.mirrorRotationMap.put(mirrorRotationValue, entry);
      }
   }

   public String getSchematic() {
      return this.schematic;
   }

   public static int getValue(long jsonMirrorValue) {
      return getValue((int)(jsonMirrorValue >> 21 & 2097151L));
   }

   public int getMirrorRotationMap() {
      return this.mirrorRotationMap.size();
   }

   public void updateMirrorRotation() {
      this.mirrorRotationMap.clear();
   }

   public static double getAmount(Integer integer, int jsonMirrorValue, double jsonMirrorBound) {
      return integer != null ? integer.intValue() : jsonMirrorValue + Math.max(0.0, jsonMirrorBound);
   }

   public static int getValueForLong(long jsonMirrorValue) {
      return getValue((int)(jsonMirrorValue & 2097151L));
   }

   public boolean canMirrorRotationForInt(int mirrorRotationValue, int mirrorRotationValue2, int mirrorRotationValue3) {
      return this.mirrorRotationMap.containsKey(getTime(mirrorRotationValue, mirrorRotationValue2, mirrorRotationValue3));
   }

   public int getRotation() {
      return this.rotation;
   }

   public static int getValueInt(long jsonMirrorValue) {
      return getValue((int)(jsonMirrorValue >> 42 & 2097151L));
   }

   public JsonMirror(String jsonMirrorText, int rotation2, int mirror2) {
      this.mirrorRotationMap = new LinkedHashMap<>();
      this.schematic = jsonMirrorText == null ? "" : jsonMirrorText;
      this.rotation = rotation2;
      this.mirror = mirror2;
   }

   public int getMirrorRotation(int mirrorRotationValue, int mirrorRotationValue2, int mirrorRotationValue3) {
      long time4 = getTime(mirrorRotationValue, mirrorRotationValue2, mirrorRotationValue3);
      int mirrorRotation = 0;

      for (Long long2 : this.mirrorRotationMap.keySet()) {
         if (long2 == time4) {
            return mirrorRotation;
         }

         mirrorRotation++;
      }

      return -1;
   }

   public JsonMirror.Entry getMirrorRotationForInt(int mirrorRotationValue, int mirrorRotationValue2, int mirrorRotationValue3) {
      return this.mirrorRotationMap.get(getTime(mirrorRotationValue, mirrorRotationValue2, mirrorRotationValue3));
   }

   public boolean isEmpty() {
      return this.mirrorRotationMap.isEmpty();
   }

   public boolean canMirrorRotation(String mirrorRotationText, int mirrorRotationValue, int mirrorRotationValue2) {
      return this.schematic.equals(mirrorRotationText == null ? "" : mirrorRotationText) && this.rotation == mirrorRotationValue && this.mirror == mirrorRotationValue2;
   }

   public int getMirror() {
      return this.mirror;
   }

   public static final class Entry {
      @SerializedName("pitch")
      public  float amount;
      @SerializedName("state")
      public  String text;
      @SerializedName("yaw")
      public  float amount2;
      @SerializedName("dz")
      public  int value;
      @SerializedName("dy")
      public  int value2;
      @SerializedName("dx")
      public  int value3;
      @SerializedName("hy")
      public  float amount3;
      @SerializedName("fz")
      public  int value4;
      @SerializedName("schematic")
      public  boolean flag;
      @SerializedName("hx")
      public  float amount4;
      @SerializedName("hz")
      public  float amount5;
      @SerializedName("face")
      public  String text2;
      @SerializedName("fy")
      public  int value5;
      @SerializedName("fx")
      public  int value6;
      @SerializedName("sneak")
      public  boolean flag2;

      public Entry(
         int value,
         int value2,
         int value3,
         String text,
         int value4,
         int value5,
         int value6,
         float amount,
         float amount2,
         String text2,
         float amount3,
         float amount4,
         float amount5,
         boolean flag,
         boolean flag2
      ) {
         this.value3 = value;
         this.value2 = value2;
         this.value = value3;
         this.text = text;
         this.value6 = value4;
         this.value5 = value5;
         this.value4 = value6;
         this.amount2 = amount;
         this.amount = amount2;
         this.text2 = text2;
         this.amount4 = amount3;
         this.amount3 = amount4;
         this.amount5 = amount5;
         this.flag2 = flag;
         this.flag = flag2;
      }

      public Entry() {
      }
   }
}
