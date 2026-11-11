package com.corz.client.utils;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

public final class DifferentDigest {
   public final int rot;
   public final int mir;
   public final int differentSchematicValue;
   public final int schematicDifferentValue;
   public final int propertySchematicValue;
   public final int ox;
   public final int oz;
   public final String hash;
   public final long digest;
   public final int oy;
   public final String schematic;
   public final int cells;

   public String getUpperCase() {
      return this.hash.substring(0, Math.min(4, this.hash.length())).toUpperCase(Locale.ROOT);
   }

   private static String formatText(JsonObject json, String differentDigestText) {
      return json.has(differentDigestText) ? json.get(differentDigestText).getAsString() : "";
   }

   public String formatDifferentSchematic(DifferentDigest differentDigest) {
      if (differentDigest == null) {
         return "no build loaded";
      } else if (this.hash.equals(differentDigest.hash)) {
         return null;
      } else if (!this.schematic.equals(differentDigest.schematic)) {
         return "different schematic (" + this.schematic + " vs " + differentDigest.schematic + ")";
      } else if (this.schematicDifferentValue != differentDigest.schematicDifferentValue
         || this.propertySchematicValue != differentDigest.propertySchematicValue
         || this.differentSchematicValue != differentDigest.differentSchematicValue) {
         return "different schematic size";
      } else if (this.ox != differentDigest.ox || this.oy != differentDigest.oy || this.oz != differentDigest.oz) {
         return "different origin";
      } else if (this.rot != differentDigest.rot) {
         return "different rotation";
      } else {
         return this.mir != differentDigest.mir ? "different mirror" : "different schematic contents";
      }
   }

   public JsonObject getPropertySchematic() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("schematic", this.schematic);
      jsonObject.addProperty("w", this.schematicDifferentValue);
      jsonObject.addProperty("h", this.propertySchematicValue);
      jsonObject.addProperty("l", this.differentSchematicValue);
      jsonObject.addProperty("cells", this.cells);
      jsonObject.addProperty("ox", this.ox);
      jsonObject.addProperty("oy", this.oy);
      jsonObject.addProperty("oz", this.oz);
      jsonObject.addProperty("rot", this.rot);
      jsonObject.addProperty("mir", this.mir);
      jsonObject.addProperty("digest", Long.toHexString(this.digest));
      jsonObject.addProperty("hash", this.hash);
      return jsonObject;
   }

   public static DifferentDigest getDigestRot(JsonObject json) {
      if (json == null) {
         return null;
      } else {
         try {
            long digestRotValue = 0L;
            if (json.has("digest")) {
               try {
                  digestRotValue = Long.parseUnsignedLong(json.get("digest").getAsString(), 16);
               } catch (Exception e) {
               }
            }

            return new DifferentDigest(
               formatText(json, "schematic"),
               readJsonInt(json, "w"),
               readJsonInt(json, "h"),
               readJsonInt(json, "l"),
               readJsonInt(json, "cells"),
               readJsonInt(json, "ox"),
               readJsonInt(json, "oy"),
               readJsonInt(json, "oz"),
               readJsonInt(json, "rot"),
               readJsonInt(json, "mir"),
               digestRotValue
            );
         } catch (Exception e2) {
            return null;
         }
      }
   }

   public DifferentDigest(
      String differentDigestText,
      int schematicDifferentValue,
      int differentSchematicValue,
      int differentSchematicValue2,
      int cells,
      int differentDigestValue,
      int differentDigestValue2,
      int differentDigestValue3,
      int rot,
      int mir,
      long digest
   ) {
      this.schematic = differentDigestText == null ? "" : differentDigestText;
      this.schematicDifferentValue = schematicDifferentValue;
      this.propertySchematicValue = differentSchematicValue;
      this.differentSchematicValue = differentSchematicValue2;
      this.cells = cells;
      this.ox = differentDigestValue;
      this.oy = differentDigestValue2;
      this.oz = differentDigestValue3;
      this.rot = rot;
      this.mir = mir;
      this.digest = digest;
      this.hash = this.getHexString();
   }

   private String getHexString() {
      String hexStringText = this.schematic
         + '|'
         + this.schematicDifferentValue
         + 'x'
         + this.propertySchematicValue
         + 'x'
         + this.differentSchematicValue
         + '|'
         + this.cells
         + '|'
         + this.ox
         + ','
         + this.oy
         + ','
         + this.oz
         + '|'
         + this.rot
         + '|'
         + this.mir
         + '|'
         + Long.toHexString(this.digest);

      try {
         MessageDigest instance = MessageDigest.getInstance("SHA-256");
         byte[] digest = instance.digest(hexStringText.getBytes(StandardCharsets.UTF_8));
         StringBuilder stringBuilder = new StringBuilder(16);

         for (int i = 0; i < 8; i++) {
            stringBuilder.append(Character.forDigit(digest[i] >> 4 & 15, 16)).append(Character.forDigit(digest[i] & 15, 16));
         }

         return stringBuilder.toString();
      } catch (Exception e) {
         return Integer.toHexString(hexStringText.hashCode());
      }
   }

   public boolean canHash(DifferentDigest differentDigest) {
      return differentDigest != null && this.hash.equals(differentDigest.hash);
   }

   private static int readJsonInt(JsonObject json, String differentDigestText) {
      return json.has(differentDigestText) ? json.get(differentDigestText).getAsInt() : 0;
   }
}
