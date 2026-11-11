package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Comparator;

public final class C2Curve {
   private final C2Curve.Key[] array;

   public void handleArrayPre(float arrayPreLimit, float[] arrayPreValues, int arrayPreValue) {
      int index = 0;
      int arrayPreLimit2 = this.array.length;

      while (index < arrayPreLimit2) {
         int index2 = index + arrayPreLimit2 >>> 1;
         if (this.array[index2].time <= arrayPreLimit) {
            index = index2 + 1;
         } else {
            arrayPreLimit2 = index2;
         }
      }

      if (index == 0) {
         System.arraycopy(this.array[0].pre, 0, arrayPreValues, arrayPreValue, 3);
      } else {
         C2Curve.Key key = this.array[index - 1];
         if (index == this.array.length) {
            System.arraycopy(key.post, 0, arrayPreValues, arrayPreValue, 3);
         } else {
            C2Curve.Key key2 = this.array[index];
            float arrayPreFactor = (arrayPreLimit - key.time) / (key2.time - key.time);

            for (int i = 0; i < 3; i++) {
               float arrayPreValue2 = key.post[i];
               float arrayPreValue3 = key2.pre[i];
               if (!key.spline && !key2.spline) {
                  arrayPreValues[arrayPreValue + i] = arrayPreValue2 + (arrayPreValue3 - arrayPreValue2) * arrayPreFactor;
               } else {
                  float arrayPreValue4 = this.array[Math.max(0, index - 2)].post[i];
                  float arrayPreValue5 = this.array[Math.min(this.array.length - 1, index + 1)].pre[i];
                  arrayPreValues[arrayPreValue + i] = 0.5F
                     * (
                        2.0F * arrayPreValue2
                           + (-arrayPreValue4 + arrayPreValue3) * arrayPreFactor
                           + (2.0F * arrayPreValue4 - 5.0F * arrayPreValue2 + 4.0F * arrayPreValue3 - arrayPreValue5) * arrayPreFactor * arrayPreFactor
                           + (-arrayPreValue4 + 3.0F * arrayPreValue2 - 3.0F * arrayPreValue3 + arrayPreValue5) * arrayPreFactor * arrayPreFactor * arrayPreFactor
                     );
               }
            }
         }
      }
   }

   public float[] getArrayTime() {
      float[] floats = new float[this.array.length];

      for (int i = 0; i < this.array.length; i++) {
         floats[i] = this.array[i].time;
      }

      return floats;
   }

   public static C2Curve getAnimationInvalid(JsonElement json) {
      if (!json.isJsonObject()) {
         return new C2Curve(new C2Curve.Key[]{new C2Curve.Key(0.0F, getVectorXyz(json), getVectorXyz(json), false)});
      } else {
         JsonObject asJsonObject = json.getAsJsonObject();
         if (!asJsonObject.has("post") && !asJsonObject.has("pre")) {
            if (asJsonObject.size() != 0 && asJsonObject.size() <= 4096) {
               ArrayList<C2Curve.Key> arrayList = new ArrayList<>();
               asJsonObject.entrySet().forEach(keys -> arrayList.add(getPostCatmullrom(getFloat(new JsonPrimitive((String)keys.getKey())), (JsonElement)keys.getValue())));
               arrayList.sort(Comparator.comparingDouble(C2Curve.Key::time));
               float animationInvalidLimit = -1.0F;

               for (C2Curve.Key key : (Iterable<com.corz.client.utils.C2Curve.Key>) (arrayList)) {
                  if (key.time < 0.0F || key.time > 300.0F || key.time <= animationInvalidLimit) {
                     throw new IllegalArgumentException("Invalid animation time");
                  }

                  animationInvalidLimit = key.time;
               }

               return new C2Curve(arrayList.toArray(C2Curve.Key[]::new));
            } else {
               throw new IllegalArgumentException("Invalid animation track size");
            }
         } else {
            return new C2Curve(new C2Curve.Key[]{getPostCatmullrom(0.0F, asJsonObject)});
         }
      }
   }

   private static C2Curve.Key getPostCatmullrom(float postCatmullromValue, JsonElement json) {
      if (!json.isJsonObject()) {
         float[] vectorXyz = getVectorXyz(json);
         return new C2Curve.Key(postCatmullromValue, vectorXyz, vectorXyz, false);
      } else {
         JsonObject asJsonObject = json.getAsJsonObject();
         JsonElement jsonElement = asJsonObject.has("pre") ? asJsonObject.get("pre") : asJsonObject.get("post");
         JsonElement jsonElement2 = asJsonObject.has("post") ? asJsonObject.get("post") : jsonElement;
         String postCatmullromText = asJsonObject.has("lerp_mode") ? asJsonObject.get("lerp_mode").getAsString() : "linear";
         if (!postCatmullromText.equals("linear") && !postCatmullromText.equals("catmullrom")) {
            throw new IllegalArgumentException("Unsupported interpolation: " + postCatmullromText);
         } else {
            return new C2Curve.Key(postCatmullromValue, getVectorXyz(jsonElement), getVectorXyz(jsonElement2), postCatmullromText.equals("catmullrom"));
         }
      }
   }

   static float getFloat(JsonElement json) {
      float asFloat = json.getAsFloat();
      if (Float.isFinite(asFloat) && !(Math.abs(asFloat) > 100000.0F)) {
         return asFloat;
      } else {
         throw new IllegalArgumentException("Non-finite or excessive coordinate");
      }
   }

   static float[] getVectorXyz(JsonElement json) {
      if (json == null) {
         throw new IllegalArgumentException("Missing vector");
      } else if (json.isJsonArray()) {
         JsonArray asJsonArray = json.getAsJsonArray();
         if (asJsonArray.size() != 3) {
            throw new IllegalArgumentException("Expected xyz vector");
         } else {
            return new float[]{getFloat(asJsonArray.get(0)), getFloat(asJsonArray.get(1)), getFloat(asJsonArray.get(2))};
         }
      } else {
         float float2 = getFloat(json);
         return new float[]{float2, float2, float2};
      }
   }

   public float getArray() {
      return this.array[this.array.length - 1].time;
   }

   private C2Curve(C2Curve.Key[] array) {
      this.array = array;
   }

   private record Key(float time, float[] pre, float[] post, boolean spline) {
   }
}
