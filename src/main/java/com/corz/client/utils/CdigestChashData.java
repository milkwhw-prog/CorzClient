package com.corz.client.utils;

import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public record CdigestChashData(String schematicName, int width, int height, int length, int contentCells, long contentDigest, String hash) {
   public String formatSchematicOlder(CdigestChashData other) {
      if (other == null) {
         return "they have no schematic loaded";
      } else if (this.hash.equals(other.hash)) {
         return null;
      } else if (!this.schematicName.equals(other.schematicName)) {
         return "a different schematic (" + other.schematicName + " vs your " + this.schematicName + ")";
      } else {
         return this.width == other.width && this.height == other.height && this.length == other.length
            ? "the same name but different contents - one of you has an older copy of the file"
            : "the same name at a different size ("
               + other.width
               + "x"
               + other.height
               + "x"
               + other.length
               + " vs your "
               + this.width
               + "x"
               + this.height
               + "x"
               + this.length
               + ")";
      }
   }

   public JsonObject getPropertyCcells() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("schematic", this.schematicName);
      jsonObject.addProperty("w", this.width);
      jsonObject.addProperty("h", this.height);
      jsonObject.addProperty("l", this.length);
      jsonObject.addProperty("ccells", this.contentCells);
      jsonObject.addProperty("cdigest", Long.toHexString(this.contentDigest));
      jsonObject.addProperty("chash", this.hash);
      return jsonObject;
   }

   public static CdigestChashData getCdigestChash(JsonObject o) {
      if (o != null && o.has("chash")) {
         try {
            long cdigestChashValue = 0L;
            if (o.has("cdigest")) {
               try {
                  cdigestChashValue = Long.parseUnsignedLong(o.get("cdigest").getAsString(), 16);
               } catch (Exception e) {
               }
            }

            return new CdigestChashData(
               o.has("schematic") ? o.get("schematic").getAsString() : "",
               getValue(o, "w"),
               getValue(o, "h"),
               getValue(o, "l"),
               getValue(o, "ccells"),
               cdigestChashValue,
               o.get("chash").getAsString()
            );
         } catch (Exception e2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean canHash(CdigestChashData o) {
      return o != null && this.hash.equals(o.hash);
   }

   public static CdigestChashData getCdigestChashData(String width, int contentCells, int contentDigest, int height, int length, long schematicName) {
      String cdigestChashText = width == null ? "" : width;
      String seed = cdigestChashText + '|' + contentCells + 'x' + contentDigest + 'x' + height + '|' + length + '|' + Long.toHexString(schematicName);
      return new CdigestChashData(cdigestChashText, contentCells, contentDigest, height, length, schematicName, getHexString(seed));
   }

   private static int getValue(JsonObject k, String o) {
      return k.has(o) ? k.get(o).getAsInt() : 0;
   }

   private static String getHexString(String seed) {
      try {
         MessageDigest instance = MessageDigest.getInstance("SHA-256");
         byte[] digest = instance.digest(seed.getBytes(StandardCharsets.UTF_8));
         StringBuilder stringBuilder = new StringBuilder(16);

         for (int i = 0; i < 8; i++) {
            stringBuilder.append(Character.forDigit(digest[i] >> 4 & 15, 16)).append(Character.forDigit(digest[i] & 15, 16));
         }

         return stringBuilder.toString();
      } catch (Exception e) {
         return Integer.toHexString(seed.hashCode());
      }
   }
}
