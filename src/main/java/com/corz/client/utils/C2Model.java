package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.utils.C2Curve;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Viewmodel;

public final class C2Model {
   public final C2Model.Bone[] meshModelBones;
   public static final String ASSETS_CORZCLIENT_VISUALS = "/assets/corzclient/c2visuals/butterfly/";
   public final Matrix4f mulTranslationMatrix4f;
   public final int c2ModelValue;
   public final Map<String, C2Model.Clip> unmodifiableMap;
   private static final String[] downRotationTexts = new String[]{"north", "east", "south", "west", "up", "down"};
   private static final int[][] rotationInflateInts = new int[][]{{1, 0, 2, 3}, {5, 1, 3, 7}, {4, 5, 7, 6}, {0, 4, 6, 2}, {5, 4, 0, 1}, {3, 2, 6, 7}};
   private static final float[][] rotationInflateFloats = new float[][]{
      {0.0F, 0.0F, -1.0F}, {1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 1.0F}, {-1.0F, 0.0F, 0.0F}, {0.0F, -1.0F, 0.0F}, {0.0F, 1.0F, 0.0F}
   };
   private static final String ASSETS_CORZCLIENT = "/assets/corzclient/";
   public final Vector3f add;

   public static boolean canFlag(String c2ModelText) {
      try {
         boolean c2ModelFlag;
         try (InputStream resourceAsStream = C2Model.class.getResourceAsStream("/assets/corzclient/" + c2ModelText)) {
            c2ModelFlag = resourceAsStream != null;
         }

         return c2ModelFlag;
      } catch (IOException e) {
         return false;
      }
   }

   public int getBonesMesh(String bonesMeshText) {
      for (int i = 0; i < this.meshModelBones.length; i++) {
         if (this.meshModelBones[i].name.equals(bonesMeshText)) {
            return i;
         }
      }

      return -1;
   }

   private static String formatPrimitiveJson(JsonObject json, String primitiveJsonText)  throws java.io.IOException {
      JsonElement jsonElement = json.get(primitiveJsonText);
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsString();
      } else {
         throw new IOException("Knife display is missing " + primitiveJsonText);
      }
   }

   static JsonObject getJsonParse(String jsonParseText, int jsonParseValue)  throws java.io.IOException {
      try {
         JsonObject jsonParse;
         try (InputStream resourceAsStream = C2Model.class.getResourceAsStream(jsonParseText)) {
            if (resourceAsStream == null) {
               throw new IOException("Missing C2 Visuals resource: " + jsonParseText);
            }

            byte[] nBytes = resourceAsStream.readNBytes(jsonParseValue + 1);
            if (nBytes.length > jsonParseValue) {
               throw new IOException("Resource too large: " + jsonParseText);
            }

            JsonElement jsonElement = JsonParser.parseString(new String(nBytes, StandardCharsets.UTF_8));
            if (!jsonElement.isJsonObject()) {
               throw new IOException("Not a JSON object: " + jsonParseText);
            }

            jsonParse = jsonElement.getAsJsonObject();
         }

         return jsonParse;
      } catch (RuntimeException e) {
         throw new IOException("Unreadable C2 Visuals resource " + jsonParseText + ": " + e.getMessage(), e);
      }
   }

   public Matrix4f[] getBonesMesh() {
      Matrix4f[] matrix4fs = new Matrix4f[this.meshModelBones.length];
      Arrays.setAll(matrix4fs, bonesMeshValue -> new Matrix4f());
      return matrix4fs;
   }

   private static float[] getAmounts(JsonObject json, String c2ModelText, float c2ModelValue) {
      return json.has(c2ModelText) ? C2Curve.getVectorXyz(json.get(c2ModelText)) : new float[]{c2ModelValue, c2ModelValue, c2ModelValue};
   }

   private C2Model(C2Model.Bone[] meshModelBones, Map<String, C2Model.Clip> map, Matrix4f mul, Vector3f add) {
      this.meshModelBones = meshModelBones;
      this.unmodifiableMap = Collections.unmodifiableMap(map);
      this.mulTranslationMatrix4f = mul;
      this.add = add;
      this.c2ModelValue = Arrays.stream(meshModelBones).mapToInt(bone -> bone.vertices.length / 8).sum();
   }

   public static C2Model.Display getAnimationJson(C2Knife c2Knife)  throws java.io.IOException {
      if (c2Knife != null && !c2Knife.isClassics()) {
         JsonObject jsonParse = getJsonParse(c2Knife.formatClassicsText(), 65536);

         try {
            return new C2Model.Display(
               c2Knife.formatVisualsPack(formatPrimitiveJson(jsonParse, "model"), "geo_models", ".json"),
               c2Knife.formatVisualsPack(formatPrimitiveJson(jsonParse, "animation"), "animations", ".animation.json"),
               c2Knife.formatVisualsPack(formatPrimitiveJson(jsonParse, "texture"), "textures", ".png")
            );
         } catch (IllegalArgumentException e) {
            throw new IOException(e.getMessage(), e);
         }
      } else {
         throw new IOException("Not a pack knife");
      }
   }

   C2Model getMulTranslation(Vector3f x) {
      return x != null && x.isFinite() && x.lengthSquared() != 0.0F
         ? new C2Model(this.meshModelBones, this.unmodifiableMap, new Matrix4f().translation(x).mul(this.mulTranslationMatrix4f), new Vector3f(this.add).add(x))
         : this;
   }

   public static C2Model getCubesParent(JsonObject json, JsonObject json2) {
      JsonObject asJsonObject = json.getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
      JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("description");
      float float2 = C2Curve.getFloat(asJsonObject2.get("texture_width"));
      float float3 = C2Curve.getFloat(asJsonObject2.get("texture_height"));
      if (!(float2 <= 0.0F) && !(float3 <= 0.0F) && !(float2 > 8192.0F) && !(float3 > 8192.0F)) {
         JsonArray asJsonArray = asJsonObject.getAsJsonArray("bones");
         if (asJsonArray.size() > 256) {
            throw new IllegalArgumentException("Too many bones");
         } else {
            LinkedHashMap linkedHashMap = new LinkedHashMap();

            for (JsonElement jsonElement : asJsonArray) {
               JsonObject finish = jsonElement.getAsJsonObject();
               if (linkedHashMap.put(finish.get("name").getAsString(), finish) != null) {
                  throw new IllegalArgumentException("Duplicate bone");
               }
            }

            LinkedHashMap linkedHashMap2 = new LinkedHashMap();
            ArrayList arrayList = new ArrayList();

            while (!linkedHashMap.isEmpty()) {
               int varSize = linkedHashMap.size();
               Iterator iterator = linkedHashMap.entrySet().iterator();

               while (iterator.hasNext()) {
                  Entry entry = (Entry)iterator.next();
                  JsonObject jsonObject = (JsonObject)entry.getValue();
                  if (!jsonObject.has("parent") || linkedHashMap2.containsKey(jsonObject.get("parent").getAsString())) {
                     linkedHashMap2.put((String)entry.getKey(), arrayList.size());
                     arrayList.add(jsonObject);
                     iterator.remove();
                  }
               }

               if (varSize == linkedHashMap.size()) {
                  throw new IllegalArgumentException("Missing or cyclic bone parent");
               }
            }

            C2Model.Bone[] bones = new C2Model.Bone[arrayList.size()];
            int cubesParentCounter = 0;

            for (int i = 0; i < bones.length; i++) {
               JsonObject jsonObject2 = (JsonObject)arrayList.get(i);
               String asString = jsonObject2.get("name").getAsString();
               int cubesParentValue = jsonObject2.has("parent") ? (Integer)linkedHashMap2.get(jsonObject2.get("parent").getAsString()) : -1;
               float[] cubesParentValues = getAmounts(jsonObject2, "pivot", 0.0F);
               float[] amounts2 = getAmounts(jsonObject2, "rotation", 0.0F);
               float[] cubesParentValues3 = cubesParentValue < 0 ? new float[]{0.0F, 24.0F, 0.0F} : getAmounts((JsonObject)arrayList.get(cubesParentValue), "pivot", 0.0F);
               float[] floats = new float[]{(cubesParentValues[0] - cubesParentValues3[0]) / 16.0F, (cubesParentValues3[1] - cubesParentValues[1]) / 16.0F, (cubesParentValues[2] - cubesParentValues3[2]) / 16.0F};
               int cubesParentValue2 = asString.equals("righthand_pos") ? 1 : (asString.equals("lefthand_pos") ? -1 : 0);
               ArrayList arrayList2 = new ArrayList();
               if (cubesParentValue2 == 0 && jsonObject2.has("cubes")) {
                  for (JsonElement getAsJsonItem : jsonObject2.getAsJsonArray("cubes")) {
                     if (++cubesParentCounter > 4096) {
                        throw new IllegalArgumentException("Too many cubes");
                     }

                     handleRotationInflate(getAsJsonItem.getAsJsonObject(), cubesParentValues, float2, float3, arrayList2);
                  }
               }

               float[] floats2 = new float[arrayList2.size()];

               for (int j = 0; j < floats2.length; j++) {
                  floats2[j] = (Float)arrayList2.get(j);
               }

               float[] cubesParentValues4 = null;
               if (cubesParentValue2 != 0 && jsonObject2.has("cubes") && !jsonObject2.getAsJsonArray("cubes").isEmpty()) {
                  JsonObject asJsonObject4 = jsonObject2.getAsJsonArray("cubes").get(0).getAsJsonObject();
                  float[] amounts5 = getAmounts(asJsonObject4, "origin", 0.0F);
                  float[] amounts6 = getAmounts(asJsonObject4, "size", 0.0F);
                  cubesParentValues4 = new float[]{
                     (amounts5[0] + amounts6[0] / 2.0F - cubesParentValues[0]) / 16.0F, (cubesParentValues[1] - amounts5[1]) / 16.0F, (amounts5[2] + amounts6[2] / 2.0F - cubesParentValues[2]) / 16.0F
                  };
               }

               bones[i] = new C2Model.Bone(asString, cubesParentValue, floats, amounts2, floats2, cubesParentValue2, cubesParentValues4);
            }

            LinkedHashMap linkedHashMap3 = new LinkedHashMap();
            JsonObject asJsonObject5 = json2.getAsJsonObject("animations");
            if (asJsonObject5.size() > 128) {
               throw new IllegalArgumentException("Too many clips");
            } else {
               for (Entry entryItem : asJsonObject5.entrySet()) {
                  String cubesParentText = (String)entryItem.getKey();
                  if (cubesParentText.startsWith("static_idle") || cubesParentText.startsWith("draw") || cubesParentText.startsWith("inspect") || cubesParentText.startsWith("melee_")) {
                     JsonObject asJsonObject6 = ((JsonElement)entryItem.getValue()).getAsJsonObject();
                     float cubesParentBound = asJsonObject6.has("animation_length") ? C2Curve.getFloat(asJsonObject6.get("animation_length")) : 0.0F;
                     C2Curve[][] c2Curves = new C2Curve[bones.length][3];
                     JsonObject jsonObject3 = asJsonObject6.has("bones") && asJsonObject6.get("bones").isJsonObject() ? asJsonObject6.getAsJsonObject("bones") : new JsonObject();

                     for (Entry entry3 : jsonObject3.entrySet()) {
                        Integer index = (Integer)linkedHashMap2.get(entry3.getKey());
                        if (index != null && !((String)entry3.getKey()).equals("camera")) {
                           JsonObject asJsonObject7 = ((JsonElement)entry3.getValue()).getAsJsonObject();
                           String[] strings = new String[]{"position", "rotation", "scale"};

                           for (int k = 0; k < 3; k++) {
                              if (asJsonObject7.has(strings[k])) {
                                 c2Curves[index][k] = C2Curve.getAnimationInvalid(asJsonObject7.get(strings[k]));
                                 cubesParentBound = Math.max(cubesParentBound, c2Curves[index][k].getArray());
                              }
                           }
                        }
                     }

                     if (cubesParentBound > 300.0F || cubesParentBound < 0.0F) {
                        throw new IllegalArgumentException("Invalid clip duration");
                     }

                     linkedHashMap3.put(cubesParentText, new C2Model.Clip(cubesParentText, Math.max(0.05F, cubesParentBound), c2Curves, getCopyOf(asJsonObject6)));
                  }
               }

               if (linkedHashMap3.containsKey("static_idle") && linkedHashMap3.containsKey("inspect") && linkedHashMap3.containsKey("melee_1")) {
                  Matrix4f[] matrix4fs = new Matrix4f[bones.length];

                  for (int m = 0; m < bones.length; m++) {
                     C2Model.Bone bone = bones[m];
                     matrix4fs[m] = bone.parent < 0 ? new Matrix4f() : new Matrix4f(matrix4fs[bone.parent]);
                     matrix4fs[m].translate(bone.pivot[0], bone.pivot[1], bone.pivot[2]);
                     handleZyxRotate(matrix4fs[m], bone.rotation);
                  }

                  Integer integer = (Integer)linkedHashMap2.get("idle_view");
                  if (integer == null) {
                     throw new IllegalArgumentException("Missing idle_view");
                  } else {
                     Matrix4f mul = new Matrix4f().rotateZ((float) Math.PI).mul(new Matrix4f(matrix4fs[integer]).invert());
                     return new C2Model(bones, linkedHashMap3, mul);
                  }
               } else {
                  throw new IllegalArgumentException("Missing clips");
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid UV dimensions");
      }
   }

   public boolean canParentBones(int parentBonesValue, int bonesMeshValue) {
      int parentBonesValue2 = parentBonesValue;

      while (parentBonesValue2 >= 0) {
         if (parentBonesValue2 == bonesMeshValue) {
            return true;
         }

         parentBonesValue2 = this.meshModelBones[parentBonesValue2].parent;
      }

      return false;
   }

   static JsonObject getJsonObject(String jsonText)  throws java.io.IOException {
      return getJsonParse("/assets/corzclient/c2visuals/butterfly/" + jsonText, 4194304);
   }

   private static float getAmount(float c2ModelFactor) {
      return c2ModelFactor * (float) (Math.PI / 180.0);
   }

   public float[] getBonesMeshMeshModelBones() {
      float[] floats = new float[this.meshModelBones.length * 9];

      for (int i = 0; i < this.meshModelBones.length; i++) {
         Arrays.fill(floats, i * 9 + 6, i * 9 + 9, 1.0F);
      }

      return floats;
   }

   private static void handleZyxRotate(Matrix4f matrix, float[] zyxRotateValues) {
      matrix.rotateZYX(getAmount(zyxRotateValues[2]), getAmount(zyxRotateValues[1]), getAmount(zyxRotateValues[0]));
   }

   public void handlePivotParent(float[] pivotParentValues, Matrix4f[] matrix4fs) {
      for (int i = 0; i < this.meshModelBones.length; i++) {
         C2Model.Bone bone = this.meshModelBones[i];
         int index = i * 9;
         Matrix4f matrix4f = matrix4fs[i];
         if (bone.parent < 0) {
            matrix4f.identity();
         } else {
            matrix4f.set(matrix4fs[bone.parent]);
         }

         matrix4f.translate(bone.pivot[0] + pivotParentValues[index] / 16.0F, bone.pivot[1] - pivotParentValues[index + 1] / 16.0F, bone.pivot[2] + pivotParentValues[index + 2] / 16.0F);
         handleZyxRotate(matrix4f, bone.rotation);
         matrix4f.rotateZYX(getAmount(pivotParentValues[index + 5]), getAmount(pivotParentValues[index + 4]), getAmount(pivotParentValues[index + 3]));
         matrix4f.scale(pivotParentValues[index + 6], pivotParentValues[index + 7], pivotParentValues[index + 8]);
      }
   }

   private static void handleRotationInflate(JsonObject json, float[] rotationInflateValues, float rotationInflateDivisor, float rotationInflateDivisor2, List<Float> list) {
      float[] amounts2 = getAmounts(json, "origin", 0.0F);
      float[] amounts3 = getAmounts(json, "size", 0.0F);
      float[] rotationInflateValues4 = json.has("pivot") ? getAmounts(json, "pivot", 0.0F) : rotationInflateValues;
      float rotationInflateValue = json.has("inflate") ? C2Curve.getFloat(json.get("inflate")) : 0.0F;
      Matrix4f translate = new Matrix4f().translate((rotationInflateValues4[0] - rotationInflateValues[0]) / 16.0F, (rotationInflateValues[1] - rotationInflateValues4[1]) / 16.0F, (rotationInflateValues4[2] - rotationInflateValues[2]) / 16.0F);
      handleZyxRotate(translate, getAmounts(json, "rotation", 0.0F));
      Matrix3f matrix3f = new Matrix3f(translate);
      JsonElement out2 = json.get("uv");
      if (out2 != null) {
         float[][] floats = null;
         if (out2.isJsonArray()) {
            float float2 = C2Curve.getFloat(out2.getAsJsonArray().get(0));
            float float3 = C2Curve.getFloat(out2.getAsJsonArray().get(1));
            float rotationInflateValue2 = amounts3[0];
            float rotationInflateValue3 = amounts3[1];
            float rotationInflateValue4 = amounts3[2];
            floats = new float[][]{
               {float2 + rotationInflateValue4, float3 + rotationInflateValue4, rotationInflateValue2, rotationInflateValue3},
               {float2 + rotationInflateValue4 + rotationInflateValue2, float3 + rotationInflateValue4, rotationInflateValue4, rotationInflateValue3},
               {float2 + 2.0F * rotationInflateValue4 + rotationInflateValue2, float3 + rotationInflateValue4, rotationInflateValue2, rotationInflateValue3},
               {float2, float3 + rotationInflateValue4, rotationInflateValue4, rotationInflateValue3},
               {float2 + rotationInflateValue4, float3, rotationInflateValue2, rotationInflateValue4},
               {float2 + rotationInflateValue4 + rotationInflateValue2, float3 + rotationInflateValue4, rotationInflateValue2, -rotationInflateValue4}
            };
         }

         for (int i = 0; i < 6; i++) {
            int floorMod5 = 0;
            float[] rotationInflateValues5;
            if (floats != null) {
               rotationInflateValues5 = floats[i];
            } else {
               JsonObject asJsonObject = out2.getAsJsonObject().getAsJsonObject(downRotationTexts[i]);
               if (asJsonObject == null) {
                  continue;
               }

               JsonArray asJsonArray = asJsonObject.getAsJsonArray("uv");
               JsonArray asJsonArray2 = asJsonObject.getAsJsonArray("uv_size");
               if (asJsonObject.has("uv_rotation")) {
                  float float4 = C2Curve.getFloat(asJsonObject.get("uv_rotation"));
                  if (float4 % 90.0F != 0.0F) {
                     throw new IllegalArgumentException("Invalid face UV rotation");
                  }

                  floorMod5 = Math.floorMod(Math.round(float4 / 90.0F), 4);
               }

               rotationInflateValues5 = new float[]{
                  C2Curve.getFloat(asJsonArray.get(0)), C2Curve.getFloat(asJsonArray.get(1)), C2Curve.getFloat(asJsonArray2.get(0)), C2Curve.getFloat(asJsonArray2.get(1))
               };
            }

            Vector3f normalize = new Vector3f(rotationInflateFloats[i]).mul(matrix3f).normalize();

            for (int j = 0; j < 4; j++) {
               int rotationInflateValue6 = rotationInflateInts[i][j];
               float rotationInflateValue7 = amounts2[0] - rotationInflateValues4[0] + ((rotationInflateValue6 & 1) == 0 ? -rotationInflateValue : amounts3[0] + rotationInflateValue);
               float rotationInflateValue8 = rotationInflateValues4[1] - amounts2[1] - amounts3[1] + ((rotationInflateValue6 & 2) == 0 ? -rotationInflateValue : amounts3[1] + rotationInflateValue);
               float rotationInflateValue9 = amounts2[2] - rotationInflateValues4[2] + ((rotationInflateValue6 & 4) == 0 ? -rotationInflateValue : amounts3[2] + rotationInflateValue);
               Vector3f mulPosition = new Vector3f(rotationInflateValue7 / 16.0F, rotationInflateValue8 / 16.0F, rotationInflateValue9 / 16.0F).mulPosition(translate);
               int floorMod = Math.floorMod(j - floorMod5, 4);
               float rotationInflateValue10 = (rotationInflateValues5[0] + (floorMod != 0 && floorMod != 3 ? 0.0F : rotationInflateValues5[2])) / rotationInflateDivisor;
               float rotationInflateValue11 = (rotationInflateValues5[1] + (floorMod >= 2 ? rotationInflateValues5[3] : 0.0F)) / rotationInflateDivisor2;

               for (float groupParseData : new float[]{mulPosition.x, mulPosition.y, mulPosition.z, rotationInflateValue10, rotationInflateValue11, normalize.x, normalize.y, normalize.z}) {
                  list.add(groupParseData);
               }
            }
         }
      }
   }

   private C2Model(C2Model.Bone[] bones2, Map<String, C2Model.Clip> map, Matrix4f matrix) {
      this(bones2, map, matrix, new Vector3f());
   }

   private static List<C2Model.Cue> getCopyOf(JsonObject json) {
      if (json.has("sound_effects") && json.get("sound_effects").isJsonObject()) {
         ArrayList arrayList = new ArrayList();

         for (Entry entry : json.getAsJsonObject("sound_effects").entrySet()) {
            float c2ModelLimit;
            try {
               c2ModelLimit = C2Curve.getFloat(new JsonPrimitive((String)entry.getKey()));
            } catch (RuntimeException e) {
               continue;
            }

            if (!(c2ModelLimit < 0.0F) && !(c2ModelLimit > 300.0F)) {
               JsonArray jsonArray = new JsonArray();
               if (((JsonElement)entry.getValue()).isJsonArray()) {
                  jsonArray = ((JsonElement)entry.getValue()).getAsJsonArray();
               } else {
                  jsonArray.add((JsonElement)entry.getValue());
               }

               for (JsonElement jsonElement : jsonArray) {
                  if (arrayList.size() >= 128) {
                     break;
                  }

                  if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("effect")) {
                     JsonElement asjsonobject = jsonElement.getAsJsonObject().get("effect");
                     if (asjsonobject.isJsonPrimitive()) {
                        String asString = asjsonobject.getAsString();
                        if (asString.matches("[a-z0-9_]+:[a-z0-9_./-]+") && !asString.contains("..")) {
                           arrayList.add(new C2Model.Cue(c2ModelLimit, asString));
                        }
                     }
                  }
               }
            }
         }

         arrayList.sort(Comparator.comparingDouble(C2Model.Cue::time));
         return List.copyOf(arrayList);
      } else {
         return List.of();
      }
   }

   public static C2Model getKnifeAnimation(C2Knife c2Knife)  throws java.io.IOException {
      if (c2Knife == null) {
         throw new IOException("No knife");
      } else if (c2Knife.isClassics()) {
         return getJsonAnimations();
      } else {
         C2Model.Display animationJson = getAnimationJson(c2Knife);
         C2Model cubesParent = getCubesParent(
            getJsonParse("/assets/corzclient/" + animationJson.model(), 4194304), getJsonParse("/assets/corzclient/" + animationJson.animation(), 4194304)
         );
         return cubesParent.getMulTranslation(C2Viewmodel.getClassicsTransform(c2Knife, cubesParent));
      }
   }

   public static C2Model getJsonAnimations()  throws java.io.IOException {
      return getCubesParent(getJsonObject("model.json"), getJsonObject("animations.json"));
   }

   public record Bone(String name, int parent, float[] pivot, float[] rotation, float[] vertices, int arm, float[] grip) {
      public Bone(String vertices, int pivot, float[] name, float[] arm, float[] parent, int rotation) {
         this(vertices, pivot, name, arm, parent, rotation, null);
      }
   }

   public record Clip(String name, float length, C2Curve[][] tracks, List<C2Model.Cue> cues) {
      public void handleTracks(float pose, float[] time) {
         for (int i = 0; i < this.tracks.length; i++) {
            for (int j = 0; j < 3; j++) {
               if (this.tracks[i][j] != null) {
                  this.tracks[i][j].handleArrayPre(pose, time, i * 9 + j * 3);
               }
            }
         }
      }

      public Clip(String tracks, float name, C2Curve[][] length) {
         this(tracks, name, length, List.of());
      }
   }

   public record Cue(float time, String effect) {
   }

   public record Display(String model, String animation, String texture) {
   }
}
