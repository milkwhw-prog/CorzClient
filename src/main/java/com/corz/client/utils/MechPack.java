package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MechPack {
   private final String soundup;
   public final boolean keyDefineType;
   private final String sound;
   private final Map<Integer, MechPack.Clip> keyboardDefineMap = new HashMap<>();
   private final boolean definesVersionFlag;
   private final MechPack.Clip clipDefinesDefine;
   public final String name;
   private final Map<Integer, MechPack.Clip> keyboardSingleMap = new HashMap<>();
   private static final Pattern compile = Pattern.compile("\\{(\\d+)-(\\d+)}");

   public Set<Integer> getKeyboardDefine() {
      LinkedHashSet linkedHashSet = new LinkedHashSet<>(this.keyboardDefineMap.keySet());
      linkedHashSet.addAll(this.keyboardSingleMap.keySet());
      return linkedHashSet;
   }

   public MechPack.Clip getDefinesDefine(int definesDefineValue) {
      MechPack.Clip keyboarddefinemap = this.keyboardDefineMap.get(definesDefineValue);
      if (keyboarddefinemap != null) {
         return keyboarddefinemap;
      } else {
         return this.definesVersionFlag && definesDefineValue != -1 ? new MechPack.Clip(formatCompile(this.sound, definesDefineValue), -1, -1) : this.clipDefinesDefine;
      }
   }

   public MechPack.Clip getSoundupDefine(int soundupDefineValue) {
      MechPack.Clip keyboardsinglemap = this.keyboardSingleMap.get(soundupDefineValue);
      if (keyboardsinglemap != null) {
         return keyboardsinglemap;
      } else {
         return !this.keyDefineType && this.soundup != null ? new MechPack.Clip(formatCompile(this.soundup, soundupDefineValue), -1, -1) : null;
      }
   }

   public static MechPack getParseJson(String jsonText) {
      return new MechPack(JsonParser.parseString(jsonText).getAsJsonObject());
   }

   private MechPack.Clip getJsonArray(JsonElement json) {
      if (json == null || json.isJsonNull()) {
         return null;
      } else if (this.keyDefineType) {
         if (json.isJsonArray() && this.sound != null) {
            JsonArray asJsonArray = json.getAsJsonArray();
            if (asJsonArray.size() < 2) {
               return null;
            } else {
               int roundAsDouble = (int)Math.round(asJsonArray.get(0).getAsDouble());
               int round2 = (int)Math.round(asJsonArray.get(1).getAsDouble());
               return roundAsDouble >= 0 && round2 > 0 ? new MechPack.Clip(this.sound, roundAsDouble, round2) : null;
            }
         } else {
            return null;
         }
      } else if (!json.isJsonPrimitive()) {
         return null;
      } else {
         String asString = json.getAsString();
         return asString.isEmpty() ? null : new MechPack.Clip(asString, -1, -1);
      }
   }

   private MechPack(JsonObject json) {
      this.name = formatPrimitiveJson(json, "name", "Keyboard");
      this.keyDefineType = "single".equals(formatPrimitiveJson(json, "key_define_type", "multi"));
      this.sound = formatPrimitiveJson(json, "sound", null);
      this.soundup = formatPrimitiveJson(json, "soundup", null);
      this.definesVersionFlag = !this.keyDefineType && json.has("version") && json.get("version").getAsInt() >= 2 && this.sound != null;
      JsonObject jsonObject = json.has("defines") && json.get("defines").isJsonObject() ? json.getAsJsonObject("defines") : new JsonObject();

      for (Entry entry : jsonObject.entrySet()) {
         String mechPackText = (String)entry.getKey();
         boolean endsWith = mechPackText.endsWith("-up");

         int mechPackValue;
         try {
            mechPackValue = Integer.parseInt(endsWith ? mechPackText.substring(0, mechPackText.length() - 3) : mechPackText);
         } catch (NumberFormatException e) {
            continue;
         }

         MechPack.Clip jsonArray = this.getJsonArray((JsonElement)entry.getValue());
         if (jsonArray != null) {
            (endsWith ? this.keyboardSingleMap : this.keyboardDefineMap).put(mechPackValue, jsonArray);
         }
      }

      MechPack.Clip clip = this.keyboardDefineMap.get(30);
      if (clip == null && this.definesVersionFlag) {
         clip = new MechPack.Clip(formatCompile(this.sound, 30), -1, -1);
      }

      if (clip == null && !this.keyboardDefineMap.isEmpty()) {
         clip = this.keyboardDefineMap.get(this.keyboardDefineMap.keySet().stream().min(Integer::compare).orElseThrow());
      }

      this.clipDefinesDefine = clip;
   }

   private static String formatPrimitiveJson(JsonObject json, String primitiveJsonText, String primitiveJsonText2) {
      JsonElement def2 = json.get(primitiveJsonText);
      return def2 != null && def2.isJsonPrimitive() ? def2.getAsString() : primitiveJsonText2;
   }

   static String formatCompile(String formatCompile, int compileValue) {
      Matcher matcher = compile.matcher(formatCompile);
      if (!matcher.find()) {
         return formatCompile;
      } else {
         int int2 = Integer.parseInt(matcher.group(1));
         int int3 = Integer.parseInt(matcher.group(2));
         if (int3 < int2) {
            int compileValue2 = int2;
            int2 = int3;
            int3 = compileValue2;
         }

         int compileValue3 = int2 + Math.floorMod(compileValue == -1 ? 0 : compileValue, int3 - int2 + 1);
         return formatCompile.substring(0, matcher.start()) + compileValue3 + formatCompile.substring(matcher.end());
      }
   }

   public boolean isSoundupDefine() {
      return !this.keyboardSingleMap.isEmpty() || !this.keyDefineType && this.soundup != null;
   }

   public record Clip(String file, int startMs, int lengthMs) {
      public boolean isStart() {
         return this.startMs >= 0;
      }
   }
}
