package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishMesh;

public final class KnifeFinishBake {
   private static final int[] compactTable = new int[]{1024, 1280, 1536, 1792, 2048};
   private static final Map<KnifeRig, KnifeFinishBake.Geometry> GEOMETRY_CACHE = new ConcurrentHashMap<>();
   private static final float[] knifeFinishTable = new float[256];
   static final int PAIR_STRIDE = 2;
   private static final float VALUE_0_02_F = 0.02F;

   private static boolean canFlags(KnifeFinishBake.Geometry geometry, KnifeFinish.Finish finish) {
      for (int i = 0; i < 8; i++) {
         if (geometry.geometryFlags[i] && finish.cloneCompact[i] != 2) {
            return true;
         }
      }

      return false;
   }

   private KnifeFinishBake() {
   }

   private static void handle(float[] knifeFinishValues, float[] floats, int knifeFinishValue, boolean knifeFinishFlag) {
      int knifeFinishLimit = knifeFinishValues.length;

      for (int i = 0; i < knifeFinishLimit; i++) {
         float knifeFinishBound = knifeFinishValues[i];
         int index = Math.max(0, i - knifeFinishValue);

         for (int j = Math.min(knifeFinishLimit - 1, i + knifeFinishValue); index <= j; index++) {
            knifeFinishBound = knifeFinishFlag ? Math.max(knifeFinishBound, knifeFinishValues[index]) : Math.min(knifeFinishBound, knifeFinishValues[index]);
         }

         floats[i] = knifeFinishBound;
      }
   }

   static void handle(boolean[] knifeFinishFlags, int knifeFinishLimit, int knifeFinishSize, int knifeFinishValue, float[] knifeFinishValues, float[] knifeFinishValues2) {
      int index = -1;

      for (int i = 0; i < knifeFinishSize; i++) {
         knifeFinishValues[i] = -1.0F;
         knifeFinishValues2[i] = -1.0F;

         for (int j = 0; j < knifeFinishLimit; j++) {
            if (knifeFinishFlags[i * knifeFinishLimit + j]) {
               if (knifeFinishValues[i] < 0.0F) {
                  knifeFinishValues[i] = j;
               }

               knifeFinishValues2[i] = j + 1;
            }
         }

         if (knifeFinishValues[i] >= 0.0F) {
            index = i;
         } else if (index >= 0) {
            knifeFinishValues[i] = knifeFinishValues[index];
            knifeFinishValues2[i] = knifeFinishValues2[index];
         }
      }

      if (index >= 0) {
         int index2 = -1;

         for (int k = knifeFinishSize - 1; k >= 0; k--) {
            if (knifeFinishValues[k] >= 0.0F) {
               if (index2 < 0) {
                  index2 = k;
               }
            } else if (index2 >= 0) {
               knifeFinishValues[k] = knifeFinishValues[index2];
               knifeFinishValues2[k] = knifeFinishValues2[index2];
            }
         }

         float[] floats = new float[knifeFinishSize];
         handle(knifeFinishValues, floats, knifeFinishValue, true);
         handle(floats, knifeFinishValues, knifeFinishValue, false);
         handle(knifeFinishValues, floats, knifeFinishValue, false);
         handle(floats, knifeFinishValues, knifeFinishValue, true);
         handle(knifeFinishValues2, floats, knifeFinishValue, false);
         handle(floats, knifeFinishValues2, knifeFinishValue, true);
         handle(knifeFinishValues2, floats, knifeFinishValue, true);
         handle(floats, knifeFinishValues2, knifeFinishValue, false);
         int max2 = Math.max(1, knifeFinishValue / 4);
         handle(knifeFinishValues, floats, max2);
         handle(knifeFinishValues2, floats, max2);
      }
   }

   static boolean canCompact(KnifeFinishBake.Geometry geometry, KnifeFinish.Finish finish, float compactValue) {
      if (finish.compact.length == 0) {
         return true;
      } else {
         int[][] classes = getClasses(geometry, finish.compact, compactValue, 0);
         int[][] ints = getValues(classes, 2048);
         return ints != null && getValue(Math.max(8, ints[classes.length][1])) <= 2048;
      }
   }

   private static void handle(float[] knifeFinishValues, float[] floats, int knifeFinishValue) {
      int knifeFinishLimit = knifeFinishValues.length;
      System.arraycopy(knifeFinishValues, 0, floats, 0, knifeFinishLimit);

      for (int i = 0; i < knifeFinishLimit; i++) {
         float knifeFinishTotal = 0.0F;
         int knifeFinishCounter = 0;
         int index = Math.max(0, i - knifeFinishValue);

         for (int j = Math.min(knifeFinishLimit - 1, i + knifeFinishValue); index <= j; index++) {
            knifeFinishTotal += floats[index];
            knifeFinishCounter++;
         }

         knifeFinishValues[i] = knifeFinishTotal / knifeFinishCounter;
      }
   }

   static KnifeFinish.Skin getAmountsAmount(KnifeFinishBake.Geometry geometry, KnifeFinish.Finish finish, KnifeFinishBake.Layout layout, int index) {
      KnifeFinishMesh knifeFinishMesh = geometry.knifeFinishMeshs[index];
      int knifeFinishLimit = knifeFinishMesh.getValue();
      float[] floats = new float[knifeFinishLimit * 8];
      int[] ints = new int[knifeFinishLimit * 4];
      int knifeFinishCounter = 0;

      for (int i = 0; i < knifeFinishLimit; i++) {
         if (finish.clone[geometry.meshInstances[index][i] & 0xFF] >= 0) {
            knifeFinishCounter++;
         }
      }

      int[] ints2 = new int[knifeFinishCounter];
      float[] floats2 = new float[knifeFinishCounter * 8];
      int knifeFinishCounter2 = 0;
      boolean childParentFlag = geometry.knifeRig.isChildParentFlag();

      for (int j = 0; j < knifeFinishLimit; j++) {
         int knifeFinishValue = geometry.meshInstances[index][j];
         int index2 = knifeFinishValue & 0xFF;
         boolean knifeFinishFlag = (knifeFinishValue & 256) != 0;
         byte knifeFinishValue2 = finish.cloneCompact[index2];
         int index3 = finish.cloneRemap[index2];
         int index4 = finish.clone[index2];
         float knifeFinishTotal = 0.0F;
         float knifeFinishTotal2 = 0.0F;
         if (childParentFlag) {
            for (int k = 0; k < 4; k++) {
               knifeFinishTotal += knifeFinishMesh.uvCoords[knifeFinishMesh.copyOf[j * 4 + k] * 2];
               knifeFinishTotal2 += knifeFinishMesh.uvCoords[knifeFinishMesh.copyOf[j * 4 + k] * 2 + 1];
            }

            knifeFinishTotal /= 4.0F;
            knifeFinishTotal2 /= 4.0F;
         }

         for (int m = 0; m < 4; m++) {
            int index5 = knifeFinishMesh.copyOf[j * 4 + m];
            int index6 = (j * 4 + m) * 2;
            float knifeFinishValue3 = knifeFinishMesh.vertexPositions[index5 * 3];
            float knifeFinishValue4 = knifeFinishMesh.vertexPositions[index5 * 3 + 1];
            if (knifeFinishValue2 == 2 && index3 >= 0) {
               floats[index6] = (layout.layoutInts[index3][0] + 8 + (knifeFinishValue3 - layout.compactFloats[index3][0]) * layout.atlasScale) / layout.compactValue;
               floats[index6 + 1] = (layout.layoutInts[index3][1] + 8 + (layout.compactFloats[index3][3] - knifeFinishValue4) * layout.atlasScale) / layout.atlasHeight;
               ints[j * 4 + m] = knifeFinishFlag ? -5987164 : -1;
            } else {
               int knifeFinishFactor = Math.max(1, layout.compactBlockHeight);
               float[] knifeFinishValues = getAmounts(childParentFlag, knifeFinishValue2, knifeFinishMesh.uvCoords[index5 * 2], knifeFinishMesh.uvCoords[index5 * 2 + 1], knifeFinishTotal, knifeFinishTotal2, knifeFinishFactor);
               floats[index6] = (layout.layoutValue + 8 + knifeFinishValues[0] * 256.0F * knifeFinishFactor) / layout.compactValue;
               floats[index6 + 1] = (layout.compactOffsetY + 8 + knifeFinishValues[1] * 256.0F * knifeFinishFactor) / layout.atlasHeight;
               int knifeFinishValue5 = knifeFinishValue2 == 1 ? finish.table[index2] : (childParentFlag ? -1 : knifeFinishMesh.table[index5]);
               ints[j * 4 + m] = knifeFinishFlag && knifeFinishValue2 == 1 ? getValue(knifeFinishValue5, -5987164) : knifeFinishValue5;
            }

            if (index4 >= 0) {
               int index7 = (knifeFinishCounter2 * 4 + m) * 2;
               floats2[index7] = (layout.compactInts[index4][0] + 8 + (knifeFinishValue3 - layout.layoutFloats[index4][0]) * layout.atlasScale) / layout.compactAtlasWidth;
               floats2[index7 + 1] = (layout.compactInts[index4][1] + 8 + (layout.layoutFloats[index4][3] - knifeFinishValue4) * layout.atlasScale) / layout.meshsFinishValue;
            }
         }

         if (index4 >= 0) {
            ints2[knifeFinishCounter2++] = j;
         }
      }

      return new KnifeFinish.Skin(floats, ints, ints2, floats2);
   }

   private static boolean[] getFlags(boolean[] knifeFinishFlags, int knifeFinishFactor, int knifeFinishFactor2) {
      boolean[] booleans = new boolean[knifeFinishFactor * knifeFinishFactor2];
      int[] ints = new int[knifeFinishFactor * knifeFinishFactor2];
      int knifeFinishCounter = 0;

      for (int i = 0; i < knifeFinishFactor; i++) {
         int knifeFinishValue = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, i);
         knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishValue, (knifeFinishFactor2 - 1) * knifeFinishFactor + i);
      }

      for (int j = 0; j < knifeFinishFactor2; j++) {
         int knifeFinishValue2 = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, j * knifeFinishFactor);
         knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishValue2, j * knifeFinishFactor + knifeFinishFactor - 1);
      }

      while (knifeFinishCounter > 0) {
         int knifeFinishValue3 = ints[--knifeFinishCounter];
         int knifeFinishLimit = knifeFinishValue3 % knifeFinishFactor;
         int knifeFinishLimit2 = knifeFinishValue3 / knifeFinishFactor;
         if (knifeFinishLimit > 0) {
            knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, knifeFinishValue3 - 1);
         }

         if (knifeFinishLimit < knifeFinishFactor - 1) {
            knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, knifeFinishValue3 + 1);
         }

         if (knifeFinishLimit2 > 0) {
            knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, knifeFinishValue3 - knifeFinishFactor);
         }

         if (knifeFinishLimit2 < knifeFinishFactor2 - 1) {
            knifeFinishCounter = getValue(knifeFinishFlags, booleans, ints, knifeFinishCounter, knifeFinishValue3 + knifeFinishFactor);
         }
      }

      return booleans;
   }

   private static void handle(float[] knifeFinishValues, int knifeFinishLimit, float[] standCellKeys, int[] ints, float[] knifeFinishValues4) {
      int index = 0;
      ints[0] = 0;
      knifeFinishValues4[0] = -1.0E20F;
      knifeFinishValues4[1] = 1.0E20F;

      for (int i = 1; i < knifeFinishLimit; i++) {
         while (true) {
            int index2 = ints[index];
            float knifeFinishValue = (knifeFinishValues[i] + (float)i * i - (knifeFinishValues[index2] + (float)index2 * index2)) / (2.0F * (i - index2));
            if (!(knifeFinishValue <= knifeFinishValues4[index]) || index <= 0) {
               if (knifeFinishValue <= knifeFinishValues4[index]) {
                  ints[0] = i;
                  knifeFinishValues4[0] = -1.0E20F;
                  knifeFinishValues4[1] = 1.0E20F;
                  index = 0;
               } else {
                  ints[++index] = i;
                  knifeFinishValues4[index] = knifeFinishValue;
                  knifeFinishValues4[index + 1] = 1.0E20F;
               }
               break;
            }

            index--;
         }
      }

      int index3 = 0;

      for (int j = 0; j < knifeFinishLimit; j++) {
         while (knifeFinishValues4[index3 + 1] < j) {
            index3++;
         }

         float knifeFinishFactor = j - ints[index3];
         standCellKeys[j] = knifeFinishFactor * knifeFinishFactor + knifeFinishValues[ints[index3]];
      }
   }

   static KnifeFinishBake.Layout getLayout(KnifeRig knifeRig, KnifeFinish.Finish finish, int layoutLimit, int compactLimit) {
      KnifeFinishBake.Geometry geometry = getGeometry(knifeRig);
      if (geometry != null && finish != null && layoutLimit > 0 && compactLimit > 0) {
         boolean flags = canFlags(geometry, finish);

         for (int layoutValue : flags ? new int[]{2, 1} : new int[]{0}) {
            if (canCompact(geometry, finish, 64.0F, layoutValue, layoutLimit, compactLimit)) {
               return getCompactAmounts(geometry, finish, layoutLimit, compactLimit, 64.0F, layoutValue);
            }

            if (canCompact(geometry, finish, 1.0F, layoutValue, layoutLimit, compactLimit)) {
               float compactValue2 = 1.0F;
               float compactValue3 = 64.0F;

               for (int i = 0; i < 24; i++) {
                  float compactValue = (compactValue2 + compactValue3) / 2.0F;
                  if (canCompact(geometry, finish, compactValue, layoutValue, layoutLimit, compactLimit)) {
                     compactValue2 = compactValue;
                  } else {
                     compactValue3 = compactValue;
                  }
               }

               return getCompactAmounts(geometry, finish, layoutLimit, compactLimit, compactValue2, layoutValue);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   static float[] getAmounts(KnifeFinishBake.Geometry geometry, int knifeFinishValue, float knifeFinishFactor) {
      float[] knifeFinishValues = geometry.getAmounts(knifeFinishValue);
      float[] amounts2 = geometry.getAmounts(getValueForInt(knifeFinishValue));
      if (knifeFinishValues != null && amounts2 != null) {
         float knifeFinishValue2 = amounts2[0] + (float)Math.floor((knifeFinishValues[0] - amounts2[0]) * knifeFinishFactor) / knifeFinishFactor;
         float knifeFinishValue3 = amounts2[3] - (float)Math.floor((amounts2[3] - knifeFinishValues[3]) * knifeFinishFactor) / knifeFinishFactor;
         return new float[]{knifeFinishValue2, knifeFinishValues[1], knifeFinishValues[2], knifeFinishValue3};
      } else {
         return knifeFinishValues;
      }
   }

   static boolean canCompact(KnifeFinishBake.Geometry geometry, KnifeFinish.Finish finish, float compactValue, int classesLimit, int knifeFinishValue, int compactLimit) {
      int[][] classes = getClasses(geometry, finish.compactPages, compactValue, classesLimit);
      int[][] ints = getValues(classes, knifeFinishValue);
      return ints != null && ints[classes.length][1] <= compactLimit && canCompact(geometry, finish, compactValue);
   }

   private static void handle(float[] knifeFinishValues, float knifeFinishLimit, float knifeFinishLimit2) {
      if (knifeFinishLimit < knifeFinishValues[0]) {
         knifeFinishValues[0] = knifeFinishLimit;
      }

      if (knifeFinishLimit2 < knifeFinishValues[1]) {
         knifeFinishValues[1] = knifeFinishLimit2;
      }

      if (knifeFinishLimit > knifeFinishValues[2]) {
         knifeFinishValues[2] = knifeFinishLimit;
      }

      if (knifeFinishLimit2 > knifeFinishValues[3]) {
         knifeFinishValues[3] = knifeFinishLimit2;
      }
   }

   private static boolean canAmounts(
      boolean[] knifeFinishFlags, boolean[] knifeFinishFlags2, KnifeFinishBake.Field field, int knifeFinishFactor, int knifeFinishValue, float[] knifeFinishValues, float knifeFinishFactor2, float[] knifeFinishValues2, int index, int index2, int index3, byte knifeFinishValue2, byte knifeFinishValue3
   ) {
      float knifeFinishBound = (knifeFinishValues2[index] - knifeFinishValues[0]) * knifeFinishFactor2 + 8.0F;
      float knifeFinishBound2 = (knifeFinishValues[3] - knifeFinishValues2[index + 1]) * knifeFinishFactor2 + 8.0F;
      float knifeFinishBound3 = (knifeFinishValues2[index2] - knifeFinishValues[0]) * knifeFinishFactor2 + 8.0F;
      float wrapDegreesBound = (knifeFinishValues[3] - knifeFinishValues2[index2 + 1]) * knifeFinishFactor2 + 8.0F;
      float knifeFinishBound5 = (knifeFinishValues2[index3] - knifeFinishValues[0]) * knifeFinishFactor2 + 8.0F;
      float hashHuntBound = (knifeFinishValues[3] - knifeFinishValues2[index3 + 1]) * knifeFinishFactor2 + 8.0F;
      float knifeFinishDivisor = (knifeFinishBound3 - knifeFinishBound) * (hashHuntBound - knifeFinishBound2) - (wrapDegreesBound - knifeFinishBound2) * (knifeFinishBound5 - knifeFinishBound);
      if (Math.abs(knifeFinishDivisor) < 1.0E-9F) {
         return false;
      } else {
         int max4 = Math.max(0, (int)Math.floor(Math.min(knifeFinishBound, Math.min(knifeFinishBound3, knifeFinishBound5))));
         int knifeFinishLimit = Math.min(knifeFinishFactor - 1, (int)Math.ceil(Math.max(knifeFinishBound, Math.max(knifeFinishBound3, knifeFinishBound5))));
         int max5 = Math.max(0, (int)Math.floor(Math.min(knifeFinishBound2, Math.min(wrapDegreesBound, hashHuntBound))));
         int min2 = Math.min(knifeFinishValue - 1, (int)Math.ceil(Math.max(knifeFinishBound2, Math.max(wrapDegreesBound, hashHuntBound))));
         boolean knifeFinishFlag = false;

         for (int i = max5; i <= min2; i++) {
            for (int j = max4; j <= knifeFinishLimit; j++) {
               float knifeFinishValue6 = j + 0.5F;
               float knifeFinishValue7 = i + 0.5F;
               float knifeFinishFactor3 = ((knifeFinishBound3 - knifeFinishValue6) * (hashHuntBound - knifeFinishValue7) - (wrapDegreesBound - knifeFinishValue7) * (knifeFinishBound5 - knifeFinishValue6)) / knifeFinishDivisor;
               float knifeFinishFactor4 = ((knifeFinishBound5 - knifeFinishValue6) * (knifeFinishBound2 - knifeFinishValue7) - (hashHuntBound - knifeFinishValue7) * (knifeFinishBound - knifeFinishValue6)) / knifeFinishDivisor;
               float knifeFinishFactor5 = 1.0F - knifeFinishFactor3 - knifeFinishFactor4;
               if (!(knifeFinishFactor3 < -1.0E-4F) && !(knifeFinishFactor4 < -1.0E-4F) && !(knifeFinishFactor5 < -1.0E-4F)) {
                  int index4 = i * knifeFinishFactor + j;
                  knifeFinishFlags[index4] = true;
                  knifeFinishFlag = true;
                  if (knifeFinishFlags2 != null) {
                     knifeFinishFlags2[index4] = true;
                     field.sheenIds[index4] = knifeFinishValue2;
                     field.rigIds[index4] = knifeFinishValue3;
                     float knifeFinishFactor6 = knifeFinishFactor3 * knifeFinishValues2[index + 2] + knifeFinishFactor4 * knifeFinishValues2[index2 + 2] + knifeFinishFactor5 * knifeFinishValues2[index3 + 2];
                     float knifeFinishFactor7 = knifeFinishFactor3 * knifeFinishValues2[index + 3] + knifeFinishFactor4 * knifeFinishValues2[index2 + 3] + knifeFinishFactor5 * knifeFinishValues2[index3 + 3];
                     float knifeFinishFactor8 = knifeFinishFactor3 * knifeFinishValues2[index + 4] + knifeFinishFactor4 * knifeFinishValues2[index2 + 4] + knifeFinishFactor5 * knifeFinishValues2[index3 + 4];
                     float distance = (float)Math.sqrt(knifeFinishFactor6 * knifeFinishFactor6 + knifeFinishFactor7 * knifeFinishFactor7 + knifeFinishFactor8 * knifeFinishFactor8);
                     if (distance > 1.0E-6F) {
                        field.fieldVectors[index4 * 3] = knifeFinishFactor6 / distance;
                        field.fieldVectors[index4 * 3 + 1] = knifeFinishFactor7 / distance;
                        field.fieldVectors[index4 * 3 + 2] = knifeFinishFactor8 / distance;
                     }
                  }
               }
            }
         }

         return knifeFinishFlag;
      }
   }

   private static KnifeFinishBake.Layout getCompactAmounts(KnifeFinishBake.Geometry geometry, KnifeFinish.Finish finish, int value, int compactLimit, float amount, int classesLimit) {
      KnifeFinishBake.Layout layout = new KnifeFinishBake.Layout();
      layout.compactValue = value;
      layout.atlasHeight = compactLimit;
      layout.atlasScale = amount;
      layout.compactBlockHeight = classesLimit;
      int[][] classes = getClasses(geometry, finish.compactPages, amount, classesLimit);
      int[][] ints = getValues(classes, value);
      int index = finish.compactPages.length;
      layout.layoutInts = new int[index][];
      layout.compactFloats = new float[index][];
      layout.floats = new float[index][];

      for (int i = 0; i < index; i++) {
         layout.layoutInts[i] = new int[]{ints[i][0], ints[i][1], classes[i][0], classes[i][1]};
         layout.compactFloats[i] = getAmounts(geometry, finish.compactPages[i].classes(), amount);
         layout.floats[i] = geometry.getAmounts(getValueForInt(finish.compactPages[i].classes()));
      }

      if (classesLimit > 0) {
         layout.layoutValue = ints[index][0];
         layout.compactOffsetY = ints[index][1];
      }

      int[][] classes2 = getClasses(geometry, finish.compact, amount, 0);
      int[][] values2 = getValues(classes2, 2048);
      int index2 = finish.compact.length;
      layout.compactInts = new int[index2][];
      layout.layoutFloats = new float[index2][];
      layout.compactUvs = new float[index2][];

      for (int j = 0; j < index2; j++) {
         layout.compactInts[j] = new int[]{values2[j][0], values2[j][1], classes2[j][0], classes2[j][1]};
         layout.layoutFloats[j] = getAmounts(geometry, finish.compact[j].classes(), amount);
         layout.compactUvs[j] = geometry.getAmounts(getValueForInt(finish.compact[j].classes()));
      }

      layout.compactAtlasWidth = index2 == 0 ? 0 : getValue(Math.max(8, values2[index2][0]));
      layout.meshsFinishValue = index2 == 0 ? 0 : getValue(Math.max(8, values2[index2][1]));
      return layout;
   }

   private static int getValue(int knifeFinishValue) {
      return knifeFinishValue + 7 & -8;
   }

   static KnifeFinish.Baked getMeshsFinish(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      KnifeFinishBake.Geometry geometry = getGeometry(knifeRig);
      if (geometry != null && finish != null) {
         KnifeFinishBake.Layout compact = getCompact(knifeRig, finish);
         if (compact == null) {
            return null;
         } else {
            KnifeFinishBake.Fields fields = new KnifeFinishBake.Fields(geometry, compact.atlasScale);
            int[] amountAmounts = getAmountAmounts(knifeRig, finish, compact, false, fields);
            int[][] ints = getValues(amountAmounts, compact.compactValue, compact.atlasHeight, false);
            int[][] values2 = null;
            if (finish.isCompact() && compact.compactAtlasWidth > 0) {
               values2 = getValues(getAmountAmounts(knifeRig, finish, compact, true, fields), compact.compactAtlasWidth, compact.meshsFinishValue, true);
            }

            KnifeFinish.Skin[] skins = new KnifeFinish.Skin[geometry.knifeFinishMeshs.length];

            for (int i = 0; i < skins.length; i++) {
               skins[i] = getAmountsAmount(geometry, finish, compact, i);
            }

            return new KnifeFinish.Baked(knifeRig, finish, compact.compactValue, compact.atlasHeight, compact.compactAtlasWidth, compact.meshsFinishValue, ints, values2, skins);
         }
      } else {
         return null;
      }
   }

   static int[] getValues(float[] knifeFinishValues) {
      Integer[] integers = new Integer[knifeFinishValues.length];

      for (int i = 0; i < integers.length; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, (index, var8x) -> knifeFinishValues[index] != knifeFinishValues[var8x] ? Float.compare(knifeFinishValues[index], knifeFinishValues[var8x]) : Integer.compare(index, var8x));
      int[] ints = new int[integers.length];

      for (int j = 0; j < ints.length; j++) {
         ints[j] = integers[j];
      }

      return ints;
   }

   static int[][] getValues(int[][] ints, int knifeFinishValue) {
      Integer[] integers = new Integer[ints.length];

      for (int i = 0; i < integers.length; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, (itemsx, index) -> ints[index][1] != ints[itemsx][1] ? Integer.compare(ints[index][1], ints[itemsx][1]) : Integer.compare(itemsx, index));
      int[][] ints2 = new int[ints.length + 1][];
      int knifeFinishTotal = 0;
      int knifeFinishTotal2 = 0;
      int knifeFinishBound = 0;
      int aggroPreemptBound = 0;
      Integer[] integers2 = integers;
      int knifeFinishLimit = integers.length;

      for (int j = 0; j < knifeFinishLimit; j++) {
         int index2 = integers2[j];
         int knifeFinishLimit2 = ints[index2][0];
         int rocketsBuyBound = ints[index2][1];
         if (knifeFinishLimit2 > knifeFinishValue) {
            return null;
         }

         if (knifeFinishTotal + knifeFinishLimit2 > knifeFinishValue) {
            knifeFinishTotal2 += knifeFinishBound;
            knifeFinishTotal = 0;
            knifeFinishBound = 0;
         }

         ints2[index2] = new int[]{knifeFinishTotal, knifeFinishTotal2};
         knifeFinishTotal += knifeFinishLimit2;
         knifeFinishBound = Math.max(knifeFinishBound, rocketsBuyBound);
         aggroPreemptBound = Math.max(aggroPreemptBound, knifeFinishTotal);
      }

      ints2[ints.length] = new int[]{aggroPreemptBound, knifeFinishTotal2 + knifeFinishBound};
      return ints2;
   }

   static KnifeFinishBake.Field getAmountsKnife(KnifeFinishBake.Geometry geometry, int knifeValue, float[] knifeValues, int knifeFactor, int knifeFinishValue, float knifeFactor3) {
      KnifeFinishBake.Field field = new KnifeFinishBake.Field(knifeFactor, knifeFinishValue);
      Arrays.fill(field.table, -1.0E20F);
      Arrays.fill(field.rigEdgeDistances, 1.0E20F);
      Arrays.fill(field.edgeDistances, 1.0E20F);
      Arrays.fill(field.fieldTable, 1.0E20F);
      Arrays.fill(field.rowExtents, -1.0F);
      Arrays.fill(field.knifeTable, -1.0F);
      Arrays.fill(field.sheenIds, (byte)-1);
      Arrays.fill(field.rigIds, (byte)-1);
      if (knifeValues == null) {
         return field;
      } else {
         int valueForInt = getValueForInt(knifeValue);
         boolean childParentFlag = geometry.knifeRig.isChildParentFlag();
         boolean[] booleans = new boolean[knifeFactor * knifeFinishValue];
         boolean[] knifeFinishFlags = new boolean[knifeFactor * knifeFinishValue];
         boolean[] booleans3 = new boolean[knifeFactor * knifeFinishValue];
         boolean[] knifeFlags = childParentFlag ? new boolean[knifeFactor * knifeFinishValue] : null;
         float[] floats = new float[knifeFactor * knifeFinishValue];
         float[] outlineValues = new float[knifeFactor * knifeFinishValue];
         float[] copyOf2 = childParentFlag ? new float[384] : null;
         KnifeFinishBake.Outline outline = new KnifeFinishBake.Outline(knifeFactor, knifeFinishValue, childParentFlag);
         boolean knifeFlag = false;

         for (int i = 0; i < geometry.knifeFinishMeshs.length; i++) {
            Arrays.fill(booleans3, false);
            outline.update();
            float[] knifeValues3 = geometry.geometryFloats[i];
            int knifeLimit = geometry.geometryInts[i].length;
            int knifeCounter = 0;
            int[] knifeValues4 = childParentFlag ? getValues(geometry.knifeFloats[i]) : null;
            boolean knifeFlag2 = false;
            if (childParentFlag) {
               Arrays.fill(knifeFlags, false);
            }

            for (int j = 0; j < 2; j++) {
               for (int k = 0; k < knifeLimit; k++) {
                  int index = knifeValues4 == null ? k : knifeValues4[k];
                  if ((valueForInt & KnifeFinish.getValue(geometry.knifeInts[i][index])) != 0 && geometry.knifeFlags[i][index] == (j == 1)) {
                     boolean knifeFlag3 = childParentFlag && j == 1 && geometry.geometryInts[i][index] == 16 && geometry.knifeInts[i][index] == 4;
                     int ticks = index * 20;

                     for (int m = 0; m < 2; m++) {
                        int index2 = ticks + (m == 0 ? 5 : 10);
                        int index3 = ticks + (m == 0 ? 10 : 15);
                        if (canAmounts(
                           booleans3,
                           j == 1 ? knifeFinishFlags : null,
                           field,
                           knifeFactor,
                           knifeFinishValue,
                           knifeValues,
                           knifeFactor3,
                           knifeValues3,
                           ticks,
                           index2,
                           index3,
                           (byte)geometry.geometryInts[i][index],
                           (byte)geometry.knifeInts[i][index]
                        )) {
                           knifeFlag2 = true;
                           float outlineValue = (knifeValues3[ticks] - knifeValues[0]) * knifeFactor3 + 8.0F;
                           float knifeValue4 = (knifeValues[3] - knifeValues3[ticks + 1]) * knifeFactor3 + 8.0F;
                           float knifeValue5 = (knifeValues3[index2] - knifeValues[0]) * knifeFactor3 + 8.0F;
                           float knifeValue6 = (knifeValues[3] - knifeValues3[index2 + 1]) * knifeFactor3 + 8.0F;
                           float knifeValue7 = (knifeValues3[index3] - knifeValues[0]) * knifeFactor3 + 8.0F;
                           float knifeValue8 = (knifeValues[3] - knifeValues3[index3 + 1]) * knifeFactor3 + 8.0F;
                           outline.handle(outlineValue, knifeValue4, knifeValue5, knifeValue6, knifeValue7, knifeValue8);
                           if (knifeFlag3) {
                              canAmounts(knifeFlags, null, field, knifeFactor, knifeFinishValue, knifeValues, knifeFactor3, knifeValues3, ticks, index2, index3, (byte)0, (byte)0);
                              if (knifeCounter + 6 > copyOf2.length) {
                                 copyOf2 = Arrays.copyOf(copyOf2, copyOf2.length * 2);
                              }

                              copyOf2[knifeCounter++] = outlineValue;
                              copyOf2[knifeCounter++] = knifeValue4;
                              copyOf2[knifeCounter++] = knifeValue5;
                              copyOf2[knifeCounter++] = knifeValue6;
                              copyOf2[knifeCounter++] = knifeValue7;
                              copyOf2[knifeCounter++] = knifeValue8;
                           }
                        }
                     }
                  }
               }
            }

            if (knifeFlag2) {
               knifeFlag = true;
               boolean[] flags = getFlags(booleans3, knifeFactor, knifeFinishValue);
               boolean knifeFlag4 = false;

               for (int p = 0; p < booleans3.length && !knifeFlag4 && !childParentFlag; p++) {
                  knifeFlag4 = !booleans3[p] && !flags[p];
               }

               if (!outline.canFlag(booleans3, flags, floats, childParentFlag)) {
                  handle(childParentFlag ? getFlags(booleans3, flags) : booleans3, knifeFactor, knifeFinishValue, floats);
               }

               if (knifeFlag4 && !outline.canFlag(outlineValues)) {
                  handle(booleans3, flags, knifeFactor, knifeFinishValue, outlineValues);
               }

               boolean knifeFlag5 = knifeCounter > 0;
               if (knifeFlag5) {
                  outline.update();

                  for (int q = 0; q < knifeCounter; q += 6) {
                     outline.handle(copyOf2[q], copyOf2[q + 1], copyOf2[q + 2], copyOf2[q + 3], copyOf2[q + 4], copyOf2[q + 5]);
                  }

                  if (!outline.canFlag(knifeFlags, getFlags(knifeFlags, knifeFactor, knifeFinishValue), outlineValues, false)) {
                     handle(knifeFlags, knifeFactor, knifeFinishValue, outlineValues);
                  }
               }

               for (int r = 0; r < booleans3.length; r++) {
                  if (floats[r] > field.table[r]) {
                     field.table[r] = floats[r];
                     field.rigEdgeDistances[r] = knifeFlag5 ? Math.max(0.0F, -outlineValues[r]) : (knifeFlag4 ? (!booleans3[r] && !flags[r] ? 0.0F : outlineValues[r]) : 1.0E20F);
                  }

                  booleans[r] |= booleans3[r];
               }
            }
         }

         for (int s = 0; s < field.table.length; s++) {
            field.table[s] = field.table[s] <= -5.0E19F ? -1.0E20F : field.table[s] / knifeFactor3;
            field.rigEdgeDistances[s] = field.rigEdgeDistances[s] >= 5.0E19F ? 1.0E20F : field.rigEdgeDistances[s] / knifeFactor3;
         }

         handleAmounts(field, knifeFinishFlags, knifeFactor, knifeFinishValue, knifeFactor + knifeFinishValue);
         if (!knifeFlag) {
            Arrays.fill(field.edgePositions, 0.5F);
            return field;
         } else {
            handle(booleans, knifeFactor, knifeFinishValue, Math.max(1, Math.round(0.35F * knifeFactor3)), field.rowExtents, field.knifeTable);

            for (int t = 0; t < knifeFinishValue; t++) {
               float knifeValue9 = field.rowExtents[t];
               float knifeValue10 = field.knifeTable[t];
               float deltaVar = knifeValue10 - knifeValue9;

               for (int knifeCounter2 = 0; knifeCounter2 < knifeFactor; knifeCounter2++) {
                  int index4 = t * knifeFactor + knifeCounter2;
                  float knifeValue11 = knifeCounter2 + 0.5F;
                  field.edgePositions[index4] = deltaVar <= 0.001F ? 0.5F : Math.max(0.0F, Math.min(1.0F, (knifeValue11 - knifeValue9) / deltaVar));
                  field.fieldTable[index4] = (knifeValue11 - knifeValue9) / knifeFactor3;
                  field.edgeDistances[index4] = (knifeValue10 - knifeValue11) / knifeFactor3;
               }
            }

            return field;
         }
      }
   }

   static float[] getAmounts(boolean knifeFinishFlag, byte knifeFinishValue, float knifeFinishValue2, float knifeFinishValue3, float knifeFinishValue4, float knifeFinishTotal, int knifeFinishBound) {
      if (!knifeFinishFlag) {
         return new float[]{knifeFinishValue2, knifeFinishValue3};
      } else if (knifeFinishValue == 1) {
         return new float[]{0.85F, 0.85F};
      } else {
         float knifeFinishLimit = 0.5F / (256 * Math.max(1, knifeFinishBound));
         return new float[]{getAmount(knifeFinishValue2, knifeFinishValue4, knifeFinishLimit), getAmount(knifeFinishValue3, knifeFinishTotal, knifeFinishLimit)};
      }
   }

   private static void handle(boolean[] knifeFinishFlags, boolean[] knifeFinishFlags2, int knifeFinishValue, int knifeFinishLimit, float[] knifeFinishValues) {
      for (int i = 0; i < knifeFinishFlags.length; i++) {
         knifeFinishValues[i] = !knifeFinishFlags[i] && !knifeFinishFlags2[i] ? 0.0F : 1.0E20F;
      }

      handle(knifeFinishValues, knifeFinishValue, knifeFinishLimit);

      for (int j = 0; j < knifeFinishFlags.length; j++) {
         knifeFinishValues[j] = (float)Math.sqrt(knifeFinishValues[j]);
      }
   }

   static int getValueForInt(int knifeFinishValue) {
      int knifeFinishValue2 = 0;

      for (int i = 0; i < 8; i++) {
         if ((knifeFinishValue & KnifeFinish.getValue(i)) != 0) {
            knifeFinishValue2 |= KnifeFinish.getFittingSapphire(i);
         }
      }

      return knifeFinishValue2;
   }

   static int[] getAmount(KnifeRig knifeRig, KnifeFinish.Finish finish, KnifeFinishBake.Layout layout, boolean knifeFinishFlag) {
      KnifeFinishBake.Geometry geometry = getGeometry(knifeRig);
      return geometry == null ? null : getAmountAmounts(knifeRig, finish, layout, knifeFinishFlag, new KnifeFinishBake.Fields(geometry, layout.atlasScale));
   }

   static {
      for (int i = 0; i < 256; i++) {
         double knifeFinishValue = i / 255.0;
         knifeFinishTable[i] = (float)(knifeFinishValue <= 0.04045 ? knifeFinishValue / 12.92 : Math.pow((knifeFinishValue + 0.055) / 1.055, 2.4));
      }
   }

   static KnifeFinishBake.Geometry getGeometry(KnifeRig knifeRig) {
      KnifeFinishBake.Geometry geometry = GEOMETRY_CACHE.get(knifeRig);
      if (geometry != null) {
         return geometry;
      } else {
         KnifeFinishMesh[] model = KnifeFinishMesh.getModel(knifeRig);
         if (model == null) {
            return null;
         } else {
            KnifeFinishBake.Geometry i = new KnifeFinishBake.Geometry(knifeRig, model);
            KnifeFinishBake.Geometry putIfAbsent = GEOMETRY_CACHE.putIfAbsent(knifeRig, i);
            return putIfAbsent != null ? putIfAbsent : i;
         }
      }
   }

   static void handle(float[] knifeFinishValues, int knifeFinishFactor, int knifeFinishLimit) {
      int knifeFinishSize = Math.max(knifeFinishFactor, knifeFinishLimit);
      float[] floats = new float[knifeFinishSize];
      float[] standCellKeys = new float[knifeFinishSize];
      float[] floats3 = new float[knifeFinishSize + 1];
      int[] ints = new int[knifeFinishSize];

      for (int i = 0; i < knifeFinishFactor; i++) {
         for (int j = 0; j < knifeFinishLimit; j++) {
            floats[j] = knifeFinishValues[j * knifeFinishFactor + i];
         }

         handle(floats, knifeFinishLimit, standCellKeys, ints, floats3);

         for (int k = 0; k < knifeFinishLimit; k++) {
            knifeFinishValues[k * knifeFinishFactor + i] = standCellKeys[k];
         }
      }

      for (int m = 0; m < knifeFinishLimit; m++) {
         System.arraycopy(knifeFinishValues, m * knifeFinishFactor, floats, 0, knifeFinishFactor);
         handle(floats, knifeFinishFactor, standCellKeys, ints, floats3);
         System.arraycopy(standCellKeys, 0, knifeFinishValues, m * knifeFinishFactor, knifeFinishFactor);
      }
   }

   static float getAmounts(int knifeFinishValue) {
      return knifeFinishTable[knifeFinishValue & 0xFF];
   }

   static int[] getAmountAmounts(KnifeRig knifeRig, KnifeFinish.Finish finish, KnifeFinishBake.Layout layout, boolean knifeFinishFlag, KnifeFinishBake.Fields fields) {
      KnifeFinishBake.Geometry geometry = getGeometry(knifeRig);
      if (geometry == null) {
         return null;
      } else {
         int knifeFinishLimit = knifeFinishFlag ? layout.compactAtlasWidth : layout.compactValue;
         int knifeFinishLimit2 = knifeFinishFlag ? layout.meshsFinishValue : layout.atlasHeight;
         if (knifeFinishLimit > 0 && knifeFinishLimit2 > 0) {
            int[] ints = new int[knifeFinishLimit * knifeFinishLimit2];
            if (!knifeFinishFlag) {
               Arrays.fill(ints, -8355712);
            }

            KnifeFinish.Page[] pages = knifeFinishFlag ? finish.compact : finish.compactPages;
            int[][] ints2 = knifeFinishFlag ? layout.compactInts : layout.layoutInts;
            float[][] floats = knifeFinishFlag ? layout.layoutFloats : layout.compactFloats;
            float[][] floats2 = knifeFinishFlag ? layout.compactUvs : layout.floats;
            KnifeFinish.Texel texel = new KnifeFinish.Texel();
            texel.knifeRig = knifeRig;
            texel.texelSeed = finish.finishSeed;
            texel.texelSize = 1.0F / layout.atlasScale;
            float[] cloneTable = geometry.cloneTable;

            for (int i = 0; i < pages.length; i++) {
               float[] knifeFinishValues = floats[i];
               float[] geometryValues = floats2[i];
               if (knifeFinishValues != null) {
                  int knifeFinishValue = ints2[i][0];
                  int knifeFinishValue2 = ints2[i][1];
                  int knifeFinishLimit3 = ints2[i][2];
                  int knifeFinishLimit4 = ints2[i][3];
                  KnifeFinishBake.Field amountGeometry = fields.getAmountGeometry(getValueForInt(pages[i].classes()), geometryValues);
                  int round = Math.round((knifeFinishValues[0] - geometryValues[0]) * layout.atlasScale);
                  int round4 = Math.round((geometryValues[3] - knifeFinishValues[3]) * layout.atlasScale);
                  KnifeFinish.Painter painter = pages[i].painter();
                  float knifeFinishDivisor = Math.max(1.0E-4F, geometryValues[2] - geometryValues[0]);
                  float max2 = Math.max(1.0E-4F, geometryValues[3] - geometryValues[1]);
                  float max3 = Math.max(1.0E-4F, cloneTable[2] - cloneTable[0]);
                  float max4 = Math.max(1.0E-4F, cloneTable[3] - cloneTable[1]);

                  for (int j = 0; j < knifeFinishLimit4; j++) {
                     int knifeFinishValue5 = (knifeFinishValue2 + j) * knifeFinishLimit + knifeFinishValue;
                     if (knifeFinishValue2 + j >= knifeFinishLimit2) {
                        break;
                     }

                     int knifeFinishFactor = Math.max(0, Math.min(amountGeometry.geometrySize - 1, j + round4));

                     for (int k = 0; k < knifeFinishLimit3 && knifeFinishValue + k < knifeFinishLimit; k++) {
                        int index = knifeFinishFactor * amountGeometry.fieldValue + Math.max(0, Math.min(amountGeometry.fieldValue - 1, k + round));
                        texel.rampAmount = knifeFinishValues[0] + (k - 8 + 0.5F) / layout.atlasScale;
                        texel.texelY = knifeFinishValues[3] - (j - 8 + 0.5F) / layout.atlasScale;
                        texel.texelAmount = (texel.rampAmount - geometryValues[0]) / knifeFinishDivisor;
                        texel.glowRecolorAmount = (texel.texelY - geometryValues[1]) / max2;
                        texel.normalizedU = (texel.rampAmount - cloneTable[0]) / max3;
                        texel.normalizedV = (texel.texelY - cloneTable[1]) / max4;
                        texel.edgePosition = amountGeometry.edgePositions[index];
                        texel.edgeDistance = amountGeometry.edgeDistances[index];
                        texel.outlineDistance = amountGeometry.fieldTable[index];
                        texel.rigAmount = amountGeometry.table[index];
                        texel.rigEdgeDistance = amountGeometry.rigEdgeDistances[index];
                        texel.sheenValue = amountGeometry.sheenIds[index];
                        texel.rigKnifeValue = amountGeometry.rigIds[index];
                        float amount = amountGeometry.fieldVectors[index * 3 + 2];
                        if (amount == 0.0F && amountGeometry.fieldVectors[index * 3] == 0.0F && amountGeometry.fieldVectors[index * 3 + 1] == 0.0F) {
                           texel.fieldX = 0.0F;
                           texel.fieldY = 0.0F;
                           texel.rigKnifeAmount = 1.0F;
                        } else {
                           texel.fieldX = amountGeometry.fieldVectors[index * 3];
                           texel.fieldY = amountGeometry.fieldVectors[index * 3 + 1];
                           texel.rigKnifeAmount = amount;
                        }

                        int paint = painter.paint(texel);
                        ints[knifeFinishValue5 + k] = knifeFinishFlag ? paint : paint | 0xFF000000;
                     }
                  }
               }
            }

            if (!knifeFinishFlag && layout.layoutValue >= 0) {
               int[] knifeAssets = KnifeFinishMesh.getKnifeAssets(knifeRig);
               short knifeFinishFactor2 = 256;
               int compactBlockHeight = layout.compactBlockHeight;
               int knifeFinishLimit5 = knifeFinishFactor2 * compactBlockHeight + 16;

               for (int m = 0; m < knifeFinishLimit5 && layout.compactOffsetY + m < knifeFinishLimit2; m++) {
                  for (int p = 0; p < knifeFinishLimit5 && layout.layoutValue + p < knifeFinishLimit; p++) {
                     int max6 = Math.max(0, Math.min(knifeFinishFactor2 - 1, Math.floorDiv(p - 8, compactBlockHeight)));
                     int knifeFinishFactor3 = Math.max(0, Math.min(knifeFinishFactor2 - 1, Math.floorDiv(m - 8, compactBlockHeight)));
                     ints[(layout.compactOffsetY + m) * knifeFinishLimit + layout.layoutValue + p] = getValue(knifeAssets, knifeFinishFactor3 * knifeFinishFactor2 + max6);
                  }
               }
            }

            return ints;
         } else {
            return null;
         }
      }
   }

   private static void handle(float[] knifeFinishValues, int knifeFinishLimit, int[] knifeFinishValues2, int[] knifeFinishValues3, float[] knifeFinishValues4) {
      int index = 0;
      knifeFinishValues3[0] = 0;
      knifeFinishValues4[0] = -1.0E20F;
      knifeFinishValues4[1] = 1.0E20F;

      for (int i = 1; i < knifeFinishLimit; i++) {
         while (true) {
            int index2 = knifeFinishValues3[index];
            float knifeFinishValue = (knifeFinishValues[i] + (float)i * i - (knifeFinishValues[index2] + (float)index2 * index2)) / (2.0F * (i - index2));
            if (!(knifeFinishValue <= knifeFinishValues4[index]) || index <= 0) {
               if (knifeFinishValue <= knifeFinishValues4[index]) {
                  knifeFinishValues3[0] = i;
                  knifeFinishValues4[0] = -1.0E20F;
                  knifeFinishValues4[1] = 1.0E20F;
                  index = 0;
               } else {
                  knifeFinishValues3[++index] = i;
                  knifeFinishValues4[index] = knifeFinishValue;
                  knifeFinishValues4[index + 1] = 1.0E20F;
               }
               break;
            }

            index--;
         }
      }

      int index3 = 0;

      for (int j = 0; j < knifeFinishLimit; j++) {
         while (knifeFinishValues4[index3 + 1] < j) {
            index3++;
         }

         knifeFinishValues2[j] = knifeFinishValues3[index3];
      }
   }

   private static void handle(boolean[] knifeFinishFlags, int knifeFinishFactor, int knifeFinishLimit, float[] knifeFinishValues) {
      float[] floats = new float[knifeFinishFactor * knifeFinishLimit];
      float[] floats2 = new float[knifeFinishFactor * knifeFinishLimit];

      for (int i = 0; i < knifeFinishFlags.length; i++) {
         floats[i] = knifeFinishFlags[i] ? 1.0E20F : 0.0F;
         floats2[i] = knifeFinishFlags[i] ? 0.0F : 1.0E20F;
      }

      handle(floats, knifeFinishFactor, knifeFinishLimit);
      handle(floats2, knifeFinishFactor, knifeFinishLimit);

      for (int j = 0; j < knifeFinishFlags.length; j++) {
         knifeFinishValues[j] = knifeFinishFlags[j] ? (float)Math.sqrt(floats[j]) - 0.5F : 0.5F - (float)Math.sqrt(floats2[j]);
      }
   }

   private static int getValue(float knifeFinishFactor, float classesValue) {
      return (int)Math.ceil(knifeFinishFactor * classesValue) + 1 + 16;
   }

   private static boolean[] getFlags(boolean[] knifeFinishFlags, boolean[] knifeFinishFlags2) {
      boolean[] booleans = new boolean[knifeFinishFlags.length];

      for (int i = 0; i < knifeFinishFlags.length; i++) {
         booleans[i] = knifeFinishFlags[i] || !knifeFinishFlags2[i];
      }

      return booleans;
   }

   private static float getAmount(float knifeFinishValue, float knifeFinishTotal, float knifeFinishLimit) {
      float planValuesItem = knifeFinishTotal - knifeFinishValue;
      return Math.abs(planValuesItem) <= knifeFinishLimit ? knifeFinishTotal : knifeFinishValue + Math.copySign(knifeFinishLimit, planValuesItem);
   }

   private static void handleAmounts(KnifeFinishBake.Field field, boolean[] knifeFinishFlags, int knifeFinishFactor, int knifeFinishValue, int knifeFinishLimit) {
      int knifeFinishSize = knifeFinishFactor * knifeFinishValue;
      int[] ints = new int[knifeFinishSize];
      int[] veinBearValues = new int[knifeFinishSize];
      Arrays.fill(veinBearValues, -1);
      int knifeFinishCounter = 0;
      int knifeFinishCounter2 = 0;

      for (int i = 0; i < knifeFinishSize; i++) {
         if (knifeFinishFlags[i]) {
            veinBearValues[i] = 0;
            ints[knifeFinishCounter2++] = i;
         }
      }

      while (knifeFinishCounter < knifeFinishCounter2) {
         int index = ints[knifeFinishCounter++];
         if (veinBearValues[index] < knifeFinishLimit) {
            int knifeFinishLimit2 = index % knifeFinishFactor;
            int knifeFinishLimit3 = index / knifeFinishFactor;

            for (int j = 0; j < 4; j++) {
               int index2 = j == 0
                  ? (knifeFinishLimit2 > 0 ? index - 1 : -1)
                  : (j == 1 ? (knifeFinishLimit2 < knifeFinishFactor - 1 ? index + 1 : -1) : (j == 2 ? (knifeFinishLimit3 > 0 ? index - knifeFinishFactor : -1) : (knifeFinishLimit3 < knifeFinishValue - 1 ? index + knifeFinishFactor : -1)));
               if (index2 >= 0 && veinBearValues[index2] < 0) {
                  veinBearValues[index2] = veinBearValues[index] + 1;
                  field.fieldVectors[index2 * 3] = field.fieldVectors[index * 3];
                  field.fieldVectors[index2 * 3 + 1] = field.fieldVectors[index * 3 + 1];
                  field.fieldVectors[index2 * 3 + 2] = field.fieldVectors[index * 3 + 2];
                  field.sheenIds[index2] = field.sheenIds[index];
                  field.rigIds[index2] = field.rigIds[index];
                  ints[knifeFinishCounter2++] = index2;
               }
            }
         }
      }
   }

   static int getValue(int knifeFinishValue, int knifeFinishValue2) {
      int knifeFinishValue3 = (knifeFinishValue >> 16 & 0xFF) * (knifeFinishValue2 >> 16 & 0xFF) / 255;
      int knifeFinishValue4 = (knifeFinishValue >> 8 & 0xFF) * (knifeFinishValue2 >> 8 & 0xFF) / 255;
      int knifeFinishValue5 = (knifeFinishValue & 0xFF) * (knifeFinishValue2 & 0xFF) / 255;
      return knifeFinishValue & 0xFF000000 | knifeFinishValue3 << 16 | knifeFinishValue4 << 8 | knifeFinishValue5;
   }

   private static int getValue(boolean[] knifeFinishFlags, boolean[] booleans, int[] knifeFinishValues, int knifeFinishCounter, int index) {
      if (!knifeFinishFlags[index] && !booleans[index]) {
         booleans[index] = true;
         knifeFinishValues[knifeFinishCounter++] = index;
         return knifeFinishCounter;
      } else {
         return knifeFinishCounter;
      }
   }

   static int getValue(float base) {
      if (base <= 0.0F) {
         return 0;
      } else if (base >= 1.0F) {
         return 255;
      } else {
         double knifeFinishValue = base <= 0.0031308 ? base * 12.92 : 1.055 * Math.pow(base, 0.4166666666666667) - 0.055;
         return (int)Math.round(knifeFinishValue * 255.0);
      }
   }

   static int getValue(int[] knifeFinishValues, int index) {
      if (knifeFinishValues == null) {
         return -2171170;
      } else {
         int knifeFinishValue = knifeFinishValues[index];
         return knifeFinishValue >>> 24 == 0 ? -2171170 : knifeFinishValue | 0xFF000000;
      }
   }

   static int[][] getClasses(KnifeFinishBake.Geometry geometry, KnifeFinish.Page[] pages, float classesValue, int classesLimit) {
      ArrayList<int[]> arrayList = new ArrayList<>();

      for (KnifeFinish.Page page : pages) {
         float[] classesValues = getAmounts(geometry, page.classes(), classesValue);
         arrayList.add(classesValues == null ? new int[]{1, 1} : new int[]{getValue(classesValues[2] - classesValues[0], classesValue), getValue(classesValues[3] - classesValues[1], classesValue)});
      }

      if (classesLimit > 0) {
         arrayList.add(new int[]{256 * classesLimit + 16, 256 * classesLimit + 16});
      }

      return arrayList.toArray(new int[0][]);
   }

   static KnifeFinishBake.Layout getCompact(KnifeRig knifeRig, KnifeFinish.Finish finish) {
      KnifeFinishBake.Geometry geometry = getGeometry(knifeRig);
      if (geometry != null && finish != null) {
         int compactValue = canFlags(geometry, finish) ? 2 : 0;
         int[][] classes = getClasses(geometry, finish.compactPages, 64.0F, compactValue);
         int compactBound = -1;
         int rocketsBuyBound = -1;
         long compactLimit = Long.MAX_VALUE;

         for (int compactTableItem : compactTable) {
            int[][] ints = getValues(classes, compactTableItem);
            if (ints != null) {
               int aggroPreemptBound = getValue(Math.max(8, ints[classes.length][0]));
               int compactFactor = getValue(Math.max(8, ints[classes.length][1]));
               if (aggroPreemptBound <= 2048 && compactFactor <= 2048) {
                  long compactValue3 = (long)aggroPreemptBound * compactFactor;
                  if (compactValue3 < compactLimit || compactValue3 == compactLimit && Math.max(aggroPreemptBound, compactFactor) < Math.max(compactBound, rocketsBuyBound)) {
                     compactLimit = compactValue3;
                     compactBound = aggroPreemptBound;
                     rocketsBuyBound = compactFactor;
                  }
               }
            }
         }

         return compactBound < 0 ? getLayout(knifeRig, finish, 2048, 2048) : getLayout(knifeRig, finish, compactBound, rocketsBuyBound);
      } else {
         return null;
      }
   }

   static int[][] getValues(int[] knifeFinishValues, int knifeFinishValue, int knifeFinishValue2, boolean knifeFinishFlag) {
      int knifeFinishCounter = 1;

      while (knifeFinishCounter < 4 && knifeFinishValue >> knifeFinishCounter >= 8 && knifeFinishValue2 >> knifeFinishCounter >= 8 && knifeFinishValue % (1 << knifeFinishCounter) == 0 && knifeFinishValue2 % (1 << knifeFinishCounter) == 0) {
         knifeFinishCounter++;
      }

      int[][] ints = new int[knifeFinishCounter][];
      ints[0] = knifeFinishValues;
      int w2 = knifeFinishValue;
      int img2 = knifeFinishValue2;

      for (int i = 1; i < knifeFinishCounter; i++) {
         int knifeFinishFactor = w2 / 2;
         int knifeFinishFactor2 = img2 / 2;
         int[] knifeFinishValues2 = ints[i - 1];
         int[] ints2 = new int[knifeFinishFactor * knifeFinishFactor2];

         for (int j = 0; j < knifeFinishFactor2; j++) {
            for (int k = 0; k < knifeFinishFactor; k++) {
               float knifeFinishTotal = 0.0F;
               float knifeFinishTotal2 = 0.0F;
               float knifeFinishTotal3 = 0.0F;
               float knifeFinishTotal4 = 0.0F;
               float knifeFinishDivisor = 0.0F;

               for (int m = 0; m < 4; m++) {
                  int knifeFinishValue3 = knifeFinishValues2[(j * 2 + (m >> 1)) * w2 + k * 2 + (m & 1)];
                  float knifeFinishValue4 = (knifeFinishValue3 >>> 24 & 0xFF) / 255.0F;
                  float knifeFinishFactor3 = knifeFinishFlag ? knifeFinishValue4 : 1.0F;
                  knifeFinishTotal += getAmounts(knifeFinishValue3 >> 16) * knifeFinishFactor3;
                  knifeFinishTotal2 += getAmounts(knifeFinishValue3 >> 8) * knifeFinishFactor3;
                  knifeFinishTotal3 += getAmounts(knifeFinishValue3) * knifeFinishFactor3;
                  knifeFinishTotal4 += knifeFinishValue4;
                  knifeFinishDivisor += knifeFinishFactor3;
               }

               if (knifeFinishDivisor <= 1.0E-6F) {
                  for (int p = 0; p < 4; p++) {
                     int knifeFinishValue5 = knifeFinishValues2[(j * 2 + (p >> 1)) * w2 + k * 2 + (p & 1)];
                     knifeFinishTotal += getAmounts(knifeFinishValue5 >> 16);
                     knifeFinishTotal2 += getAmounts(knifeFinishValue5 >> 8);
                     knifeFinishTotal3 += getAmounts(knifeFinishValue5);
                  }

                  knifeFinishDivisor = 4.0F;
               }

               int round = Math.round(knifeFinishTotal4 / 4.0F * 255.0F);
               ints2[j * knifeFinishFactor + k] = round << 24 | getValue(knifeFinishTotal / knifeFinishDivisor) << 16 | getValue(knifeFinishTotal2 / knifeFinishDivisor) << 8 | getValue(knifeFinishTotal3 / knifeFinishDivisor);
            }
         }

         ints[i] = ints2;
         w2 = knifeFinishFactor;
         img2 = knifeFinishFactor2;
      }

      return ints;
   }

   static final class Field {
      final float[] knifeTable;
      final float[] fieldTable;
      final float[] table;
      final byte[] rigIds;
      final int geometrySize;
      final byte[] sheenIds;
      final float[] fieldVectors;
      final float[] edgeDistances;
      final float[] edgePositions;
      final float[] rowExtents;
      final int fieldValue;
      final float[] rigEdgeDistances;

      Field(int value, int value2) {
         this.fieldValue = value;
         this.geometrySize = value2;
         this.table = new float[value * value2];
         this.rigEdgeDistances = new float[value * value2];
         this.edgePositions = new float[value * value2];
         this.edgeDistances = new float[value * value2];
         this.fieldTable = new float[value * value2];
         this.rowExtents = new float[value2];
         this.knifeTable = new float[value2];
         this.fieldVectors = new float[value * value2 * 3];
         this.sheenIds = new byte[value * value2];
         this.rigIds = new byte[value * value2];
      }
   }

   static final class Fields {
      private final float pixelScale;
      private int[] copyOf = new int[4];
      private final KnifeFinishBake.Geometry geometry;
      private KnifeFinishBake.Field[] copyOfGeometry = new KnifeFinishBake.Field[4];
      private int entryCount;

      Fields(KnifeFinishBake.Geometry geometry, float amount) {
         this.geometry = geometry;
         this.pixelScale = amount;
      }

      KnifeFinishBake.Field getAmountGeometry(int geometryValue, float[] geometryValues) {
         for (int i = 0; i < this.entryCount; i++) {
            if (this.copyOf[i] == geometryValue) {
               return this.copyOfGeometry[i];
            }
         }

         KnifeFinishBake.Field amountsKnife = KnifeFinishBake.getAmountsKnife(
            this.geometry,
            geometryValue,
            geometryValues,
            KnifeFinishBake.getValue(geometryValues[2] - geometryValues[0], this.pixelScale),
            KnifeFinishBake.getValue(geometryValues[3] - geometryValues[1], this.pixelScale),
            this.pixelScale
         );
         if (this.entryCount == this.copyOf.length) {
            this.copyOf = Arrays.copyOf(this.copyOf, this.entryCount * 2);
            this.copyOfGeometry = Arrays.copyOf(this.copyOfGeometry, this.entryCount * 2);
         }

         this.copyOf[this.entryCount] = geometryValue;
         this.copyOfGeometry[this.entryCount++] = amountsKnife;
         return amountsKnife;
      }
   }

   static final class Geometry {
      final int[][] knifeInts;
      final int[][] geometryInts;
      final int[][] meshInstances;
      final float[][] knifeFloats;
      final boolean[][] knifeFlags;
      final KnifeRig knifeRig;
      final boolean[] geometryFlags;
      final float[][] rigFloats;
      final float[][] geometryFloats;
      final float[] cloneTable = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
      final KnifeFinishMesh[] knifeFinishMeshs;

      float[] getAmounts(int geometryValue) {
         float[] floats = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

         for (int i = 0; i < 8; i++) {
            if ((geometryValue & KnifeFinish.getValue(i)) != 0 && this.rigFloats[i] != null) {
               KnifeFinishBake.handle(floats, this.rigFloats[i][0], this.rigFloats[i][1]);
               KnifeFinishBake.handle(floats, this.rigFloats[i][2], this.rigFloats[i][3]);
            }
         }

         return floats[0] > floats[2] ? null : floats;
      }

      int getFlags(int geometryValue) {
         int flags = 0;

         for (int i = 0; i < 8; i++) {
            if ((geometryValue & KnifeFinish.getValue(i)) != 0 && this.geometryFlags[i]) {
               flags |= KnifeFinish.getValue(i);
            }
         }

         return flags;
      }

      Geometry(KnifeRig knifeRig, KnifeFinishMesh[] knifeFinishMeshs) {
         this.rigFloats = new float[8][];
         this.geometryFlags = new boolean[8];
         this.knifeRig = knifeRig;
         this.knifeFinishMeshs = knifeFinishMeshs;
         this.meshInstances = new int[knifeFinishMeshs.length][];
         this.geometryInts = new int[knifeFinishMeshs.length][];
         this.geometryFloats = new float[knifeFinishMeshs.length][];
         this.knifeInts = new int[knifeFinishMeshs.length][];
         this.knifeFlags = new boolean[knifeFinishMeshs.length][];
         this.knifeFloats = new float[knifeFinishMeshs.length][];

         for (int i = 0; i < knifeFinishMeshs.length; i++) {
            KnifeFinishMesh knifeFinishMesh = knifeFinishMeshs[i];
            int geometrySize = knifeFinishMesh.getValue();
            this.meshInstances[i] = new int[geometrySize];
            float[] floats = new float[geometrySize * 20];
            float[] floats2 = new float[geometrySize];
            int[] ints = new int[geometrySize];
            int[] ints2 = new int[geometrySize];
            boolean[] booleans = new boolean[geometrySize];
            int index = 0;

            for (int j = 0; j < geometrySize; j++) {
               float geometryTotal = 0.0F;
               float geometryTotal2 = 0.0F;
               float geometryTotal3 = 0.0F;
               float geometryTotal4 = 0.0F;
               float geometryTotal5 = 0.0F;
               float geometryTotal6 = 0.0F;

               for (int k = 0; k < 4; k++) {
                  int geometryValue = knifeFinishMesh.copyOf[j * 4 + k];
                  geometryTotal += knifeFinishMesh.vertexPositions[geometryValue * 3];
                  geometryTotal2 += knifeFinishMesh.vertexPositions[geometryValue * 3 + 1];
                  geometryTotal3 += knifeFinishMesh.vertexPositions[geometryValue * 3 + 2];
                  geometryTotal4 += knifeFinishMesh.knifeFinishTable[geometryValue * 3];
                  geometryTotal5 += knifeFinishMesh.knifeFinishTable[geometryValue * 3 + 1];
                  geometryTotal6 += knifeFinishMesh.knifeFinishTable[geometryValue * 3 + 2];
               }

               float distance = (float)Math.sqrt(geometryTotal4 * geometryTotal4 + geometryTotal5 * geometryTotal5 + geometryTotal6 * geometryTotal6);
               float geometryValue2;
               float geometryValue3;
               float knifeFinishFactor;
               if (distance > 1.0E-6F) {
                  geometryValue2 = geometryTotal4 / distance;
                  geometryValue3 = geometryTotal5 / distance;
                  knifeFinishFactor = geometryTotal6 / distance;
               } else {
                  geometryValue2 = 0.0F;
                  geometryValue3 = 0.0F;
                  knifeFinishFactor = 1.0F;
               }

               int index2 = knifeFinishMesh.copyOf[j * 4];
               int geometryValue5 = KnifeFinish.getValue(knifeFinishMesh.table[index2], knifeFinishMesh.uvCoords[index2 * 2], knifeFinishMesh.uvCoords[index2 * 2 + 1]);
               int instance = KnifeFinish.getInstance(knifeRig, i, geometryValue5, geometryTotal / 4.0F, geometryTotal2 / 4.0F, geometryTotal3 / 4.0F, geometryValue2, geometryValue3, knifeFinishFactor);
               this.meshInstances[i][j] = instance;
               int index3 = instance & 0xFF;
               this.geometryFlags[index3] = true;
               float[] geometryValues = this.rigFloats[index3];
               if (geometryValues == null) {
                  geometryValues = this.rigFloats[index3] = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
               }

               for (int m = 0; m < 4; m++) {
                  int geometryValue6 = knifeFinishMesh.copyOf[j * 4 + m];
                  float knifeFinishLimit = knifeFinishMesh.vertexPositions[geometryValue6 * 3];
                  float geometryValue8 = knifeFinishMesh.vertexPositions[geometryValue6 * 3 + 1];
                  KnifeFinishBake.handle(geometryValues, knifeFinishLimit, geometryValue8);
                  KnifeFinishBake.handle(this.cloneTable, knifeFinishLimit, geometryValue8);
                  int index4 = index * 20 + m * 5;
                  floats[index4] = knifeFinishLimit;
                  floats[index4 + 1] = geometryValue8;
                  floats[index4 + 2] = knifeFinishMesh.knifeFinishTable[geometryValue6 * 3];
                  floats[index4 + 3] = knifeFinishMesh.knifeFinishTable[geometryValue6 * 3 + 1];
                  floats[index4 + 4] = Math.abs(knifeFinishMesh.knifeFinishTable[geometryValue6 * 3 + 2]);
               }

               if (Math.abs(knifeFinishFactor) > 0.02F) {
                  ints[index] = geometryValue5;
                  ints2[index] = index3;
                  booleans[index] = Math.abs(knifeFinishFactor) > 0.3F;
                  floats2[index] = Math.abs(geometryTotal3 / 4.0F - 8.0F);
                  index++;
               }
            }

            this.geometryFloats[i] = Arrays.copyOf(floats, index * 20);
            this.geometryInts[i] = Arrays.copyOf(ints, index);
            this.knifeInts[i] = Arrays.copyOf(ints2, index);
            this.knifeFlags[i] = Arrays.copyOf(booleans, index);
            this.knifeFloats[i] = Arrays.copyOf(floats2, index);
         }
      }
   }

   static final class Layout {
      int atlasHeight;
      float atlasScale;
      int compactValue;
      int layoutValue;
      float[][] compactFloats;
      float[][] layoutFloats;
      float[][] floats;
      int[][] compactInts;
      int compactOffsetY;
      int[][] layoutInts;
      int meshsFinishValue;
      int compactBlockHeight = 0;
      float[][] compactUvs;
      int compactAtlasWidth;

      Layout() {
         this.layoutValue = -1;
         this.compactOffsetY = -1;
      }
   }

   static final class Outline {
      final int[] outlineTable;
      final boolean outlineActive;
      int outlineEntryCount;
      final int[] table;
      final int[] tileWriteCursors;
      final int[] parabolaIndices;
      private static final int PAIR_STRIDE = 2;
      int outlineCount;
      final int[] tileOffsets;
      int segmentCount;
      final int outlineValue;
      final int[] rowDistanceGrid;
      final byte[] sideMask;
      final int gridWidth;
      int[] copyOf;
      int[] entryKeys;
      float[] segmentData = new float[3584];
      boolean[] outlineFlags;
      private static final int SINGLE_STEP = 1;
      final int gridHeight;
      final int tilesAcross;
      int[] tileSegmentIndices;
      private static final float VALUE_0_02_F = 0.02F;
      final float[] parabolaBoundaries;
      final float[] parabolaHeights;
      float[] edgeEndpoints = new float[1024];

      private void handle(boolean[] outlineFlags, boolean[] outlineFlags2) {
         Arrays.fill(this.table, -1);
         Arrays.fill(this.sideMask, (byte)0);

         for (int i = 0; i < this.segmentCount; i++) {
            int outlineValue = i * 7;

            for (int j = 0; j < 3; j++) {
               int index = outlineValue + j * 2;
               int index2 = outlineValue + (j + 1) % 3 * 2;
               int index3 = outlineValue + (j + 2) % 3 * 2;
               float outlineValue2 = this.segmentData[index];
               float outlineValue3 = this.segmentData[index + 1];
               float outlineFactor = this.segmentData[index2] - outlineValue2;
               float outlineFactor2 = this.segmentData[index2 + 1] - outlineValue3;
               float distance = (float)Math.sqrt(outlineFactor * outlineFactor + outlineFactor2 * outlineFactor2);
               if (!(distance < 0.001F)) {
                  float outlineFactor3 = -outlineFactor2 / distance;
                  float outlineFactor4 = outlineFactor / distance;
                  if ((this.segmentData[index3] - outlineValue2) * outlineFactor3 + (this.segmentData[index3 + 1] - outlineValue3) * outlineFactor4 > 0.0F) {
                     outlineFactor3 = -outlineFactor3;
                     outlineFactor4 = -outlineFactor4;
                  }

                  int outlineDivisor = Math.max(1, (int)Math.ceil(distance / 0.5F));
                  int index4 = -1;

                  for (int k = 0; k < outlineDivisor; k++) {
                     float outlineFactor5 = (k + 0.5F) / outlineDivisor;
                     float outlineValue4 = outlineValue2 + outlineFactor * outlineFactor5;
                     float outlineValue5 = outlineValue3 + outlineFactor2 * outlineFactor5;
                     if (this.canFlag(outlineValue4 + outlineFactor3 * 0.02F, outlineValue5 + outlineFactor4 * 0.02F)) {
                        index4 = -1;
                     } else {
                        if (index4 < 0) {
                           float outlineFactor6 = (float)k / outlineDivisor;
                           index4 = this.getValue(
                              outlineValue2 + outlineFactor * outlineFactor6, outlineValue3 + outlineFactor2 * outlineFactor6, this.canFlag(outlineFlags, outlineFlags2, outlineValue4 + outlineFactor3 * 1.5F, outlineValue5 + outlineFactor4 * 1.5F)
                           );
                        }

                        float outlineFactor7 = (k + 1.0F) / outlineDivisor;
                        this.edgeEndpoints[index4 * 4 + 2] = outlineValue2 + outlineFactor * outlineFactor7;
                        this.edgeEndpoints[index4 * 4 + 3] = outlineValue3 + outlineFactor2 * outlineFactor7;
                        int max6 = Math.max(0, Math.min(this.gridWidth - 1, (int)outlineValue4));
                        int max8 = Math.max(0, Math.min(this.gridHeight - 1, (int)outlineValue5));
                        int index5 = max8 * this.gridWidth + max6;
                        if (this.table[index5] < 0 || this.entryKeys[this.table[index5]] != index4) {
                           if (this.outlineEntryCount == this.entryKeys.length) {
                              this.entryKeys = Arrays.copyOf(this.entryKeys, this.outlineEntryCount * 2);
                              this.copyOf = Arrays.copyOf(this.copyOf, this.outlineEntryCount * 2);
                           }

                           this.entryKeys[this.outlineEntryCount] = index4;
                           this.copyOf[this.outlineEntryCount] = this.table[index5];
                           this.table[index5] = this.outlineEntryCount++;
                           this.sideMask[index5] = (byte)(this.sideMask[index5] | (byte)(this.outlineFlags[index4] ? 2 : 1));
                        }
                     }
                  }
               }
            }
         }
      }

      Outline(int value, int rocketsBuyBound, boolean flag) {
         this.outlineFlags = new boolean[256];
         this.entryKeys = new int[2048];
         this.copyOf = new int[2048];
         this.tileSegmentIndices = new int[4096];
         this.gridWidth = value;
         this.gridHeight = rocketsBuyBound;
         this.outlineActive = flag;
         this.tilesAcross = (value + 16 - 1) / 16;
         this.outlineValue = (rocketsBuyBound + 16 - 1) / 16;
         this.table = new int[value * rocketsBuyBound];
         this.tileWriteCursors = new int[value * rocketsBuyBound];
         this.rowDistanceGrid = new int[value * rocketsBuyBound];
         this.sideMask = new byte[value * rocketsBuyBound];
         this.tileOffsets = new int[this.tilesAcross * this.outlineValue + 1];
         int maxCracks = Math.max(value, rocketsBuyBound);
         this.outlineTable = new int[maxCracks];
         this.parabolaIndices = new int[maxCracks];
         this.parabolaHeights = new float[maxCracks];
         this.parabolaBoundaries = new float[maxCracks + 1];
      }

      private static int getValue(float outlineValue, int outlineValue2) {
         int outlineLimit = (int)Math.floor(outlineValue / 16.0F);
         return outlineLimit < 0 ? 0 : (outlineLimit >= outlineValue2 ? outlineValue2 - 1 : outlineLimit);
      }

      private int getValue(float outlineValue, float outlineValue2, boolean outlineFlag) {
         if (this.outlineCount == this.outlineFlags.length) {
            this.edgeEndpoints = Arrays.copyOf(this.edgeEndpoints, this.outlineCount * 8);
            this.outlineFlags = Arrays.copyOf(this.outlineFlags, this.outlineCount * 2);
         }

         this.edgeEndpoints[this.outlineCount * 4] = outlineValue;
         this.edgeEndpoints[this.outlineCount * 4 + 1] = outlineValue2;
         this.edgeEndpoints[this.outlineCount * 4 + 2] = outlineValue;
         this.edgeEndpoints[this.outlineCount * 4 + 3] = outlineValue2;
         this.outlineFlags[this.outlineCount] = outlineFlag;
         return this.outlineCount++;
      }

      void update() {
         this.segmentCount = 0;
         this.outlineCount = 0;
         this.outlineEntryCount = 0;
      }

      private float getAmount(int outlineValue, int outlineValue2, int outlineValue3) {
         if (outlineValue2 < 0) {
            return 1.0E20F;
         } else {
            float outlineValue4 = outlineValue % this.gridWidth + 0.5F;
            float outlineValue5 = outlineValue / this.gridWidth + 0.5F;
            int outlineValue6 = outlineValue2 % this.gridWidth;
            int outlineValue7 = outlineValue2 / this.gridWidth;
            float outlineLimit = 1.0E20F;

            for (int j = 0; j < 5; j++) {
               int outlineLimit2 = outlineValue6 + (j == 1 ? 1 : (j == 2 ? -1 : 0));
               int outlineLimit3 = outlineValue7 + (j == 3 ? 1 : (j == 4 ? -1 : 0));
               if (outlineLimit2 >= 0 && outlineLimit3 >= 0 && outlineLimit2 < this.gridWidth && outlineLimit3 < this.gridHeight) {
                  for (int k = this.table[outlineLimit3 * this.gridWidth + outlineLimit2]; k >= 0; k = this.copyOf[k]) {
                     int index = this.entryKeys[k];
                     if ((outlineValue3 & (this.outlineFlags[index] ? 2 : 1)) != 0) {
                        int index2 = index * 4;
                        float outlineValue8 = this.edgeEndpoints[index2];
                        float outlineValue9 = this.edgeEndpoints[index2 + 1];
                        float outlineFactor = this.edgeEndpoints[index2 + 2] - outlineValue8;
                        float outlineFactor2 = this.edgeEndpoints[index2 + 3] - outlineValue9;
                        float outlineLimit4 = outlineFactor * outlineFactor + outlineFactor2 * outlineFactor2;
                        float outlineFactor3 = outlineLimit4 > 1.0E-12F ? Math.max(0.0F, Math.min(1.0F, ((outlineValue4 - outlineValue8) * outlineFactor + (outlineValue5 - outlineValue9) * outlineFactor2) / outlineLimit4)) : 0.0F;
                        float outlineFactor4 = outlineValue8 + outlineFactor * outlineFactor3 - outlineValue4;
                        float outlineFactor5 = outlineValue9 + outlineFactor2 * outlineFactor3 - outlineValue5;
                        float outlineValue10 = outlineFactor4 * outlineFactor4 + outlineFactor5 * outlineFactor5;
                        if (outlineValue10 < outlineLimit) {
                           outlineLimit = outlineValue10;
                        }
                     }
                  }
               }
            }

            return outlineLimit >= 5.0E19F ? 1.0E20F : (float)Math.sqrt(outlineLimit);
         }
      }

      boolean canFlag(boolean[] outlineFlags, boolean[] outlineFlags2, float[] outlineValues, boolean outlineFlag) {
         if (this.segmentCount == 0) {
            return false;
         } else {
            this.updateTileOffsets();
            this.handle(outlineFlags, outlineFlags2);
            if (this.outlineEntryCount != 0 && this.canAmounts(outlineFlag ? 1 : 3)) {
               for (int i = 0; i < this.gridWidth * this.gridHeight; i++) {
                  float outlineLimit = this.getAmount(i, this.tileWriteCursors[i], outlineFlag ? 1 : 3);
                  if (outlineLimit >= 5.0E19F) {
                     return false;
                  }

                  outlineValues[i] = !outlineFlags[i] && (!outlineFlag || outlineFlags2[i]) ? -outlineLimit : outlineLimit;
               }

               return true;
            } else {
               return false;
            }
         }
      }

      private boolean canAmounts(int outlineValue) {
         boolean outlineFlag = false;

         for (int i = 0; i < this.gridWidth; i++) {
            int outlineValue2 = -1;

            for (int j = 0; j < this.gridHeight; j++) {
               if ((this.sideMask[j * this.gridWidth + i] & outlineValue) != 0) {
                  outlineValue2 = j;
                  outlineFlag = true;
               }

               this.rowDistanceGrid[j * this.gridWidth + i] = outlineValue2;
            }

            int outlineLimit = -1;

            for (int k = this.gridHeight - 1; k >= 0; k--) {
               if ((this.sideMask[k * this.gridWidth + i] & outlineValue) != 0) {
                  outlineLimit = k;
               }

               int outlineValue3 = this.rowDistanceGrid[k * this.gridWidth + i];
               if (outlineLimit >= 0 && (outlineValue3 < 0 || outlineLimit - k < k - outlineValue3)) {
                  this.rowDistanceGrid[k * this.gridWidth + i] = outlineLimit;
               }
            }
         }

         if (!outlineFlag) {
            return false;
         } else {
            for (int m = 0; m < this.gridHeight; m++) {
               for (int p = 0; p < this.gridWidth; p++) {
                  int outlineValue4 = this.rowDistanceGrid[m * this.gridWidth + p];
                  this.parabolaHeights[p] = outlineValue4 < 0 ? 1.0E20F : (float)(outlineValue4 - m) * (outlineValue4 - m);
               }

               KnifeFinishBake.handle(this.parabolaHeights, this.gridWidth, this.outlineTable, this.parabolaIndices, this.parabolaBoundaries);

               for (int q = 0; q < this.gridWidth; q++) {
                  int outlineValue5 = this.outlineTable[q];
                  int outlineFactor = this.rowDistanceGrid[m * this.gridWidth + outlineValue5];
                  this.tileWriteCursors[m * this.gridWidth + q] = outlineFactor < 0 ? -1 : outlineFactor * this.gridWidth + outlineValue5;
               }
            }

            return true;
         }
      }

      void handle(float outlineValue, float outlineValue2, float outlineValue3, float outlineValue4, float outlineValue5, float outlineValue6) {
         float outlineDivisor = (outlineValue3 - outlineValue) * (outlineValue6 - outlineValue2) - (outlineValue4 - outlineValue2) * (outlineValue5 - outlineValue);
         if (!(Math.abs(outlineDivisor) < 1.0E-4F)) {
            if ((this.segmentCount + 1) * 7 > this.segmentData.length) {
               this.segmentData = Arrays.copyOf(this.segmentData, this.segmentData.length * 2);
            }

            int index = this.segmentCount++ * 7;
            this.segmentData[index] = outlineValue;
            this.segmentData[index + 1] = outlineValue2;
            this.segmentData[index + 2] = outlineValue3;
            this.segmentData[index + 3] = outlineValue4;
            this.segmentData[index + 4] = outlineValue5;
            this.segmentData[index + 5] = outlineValue6;
            this.segmentData[index + 6] = 1.0F / outlineDivisor;
         }
      }

      private void updateTileOffsets() {
         Arrays.fill(this.tileOffsets, 0);

         for (int i = 0; i < 2; i++) {
            for (int j = 0; j < this.segmentCount; j++) {
               int index = j * 7;
               int outlineValue = getValue(Math.min(this.segmentData[index], Math.min(this.segmentData[index + 2], this.segmentData[index + 4])), this.tilesAcross);
               int outlineLimit = getValue(Math.max(this.segmentData[index], Math.max(this.segmentData[index + 2], this.segmentData[index + 4])), this.tilesAcross);
               int outlineValue2 = getValue(Math.min(this.segmentData[index + 1], Math.min(this.segmentData[index + 3], this.segmentData[index + 5])), this.outlineValue);
               int outlineLimit2 = getValue(Math.max(this.segmentData[index + 1], Math.max(this.segmentData[index + 3], this.segmentData[index + 5])), this.outlineValue);

               for (int k = outlineValue2; k <= outlineLimit2; k++) {
                  for (int m = outlineValue; m <= outlineLimit; m++) {
                     int index2 = k * this.tilesAcross + m;
                     if (i == 0) {
                        this.tileOffsets[index2 + 1]++;
                     } else {
                        this.tileSegmentIndices[this.tileWriteCursors[index2]++] = j;
                     }
                  }
               }
            }

            if (i == 0) {
               for (int p = 0; p < this.tilesAcross * this.outlineValue; p++) {
                  this.tileOffsets[p + 1] = this.tileOffsets[p + 1] + this.tileOffsets[p];
               }

               if (this.tileSegmentIndices.length < this.tileOffsets[this.tilesAcross * this.outlineValue]) {
                  this.tileSegmentIndices = new int[this.tileOffsets[this.tilesAcross * this.outlineValue]];
               }

               System.arraycopy(this.tileOffsets, 0, this.tileWriteCursors, 0, this.tilesAcross * this.outlineValue);
            }
         }
      }

      private boolean canFlag(boolean[] outlineFlags, boolean[] outlineFlags2, float outlineLimit, float outlineLimit2) {
         if (!(outlineLimit < 0.0F) && !(outlineLimit2 < 0.0F) && !(outlineLimit >= this.gridWidth) && !(outlineLimit2 >= this.gridHeight)) {
            int index = (int)outlineLimit2 * this.gridWidth + (int)outlineLimit;
            return this.outlineActive ? !outlineFlags2[index] : !outlineFlags[index] && !outlineFlags2[index];
         } else {
            return false;
         }
      }

      boolean canFlag(float[] outlineValues) {
         if (this.outlineEntryCount != 0 && this.canAmounts(2)) {
            for (int i = 0; i < this.gridWidth * this.gridHeight; i++) {
               outlineValues[i] = this.getAmount(i, this.tileWriteCursors[i], 2);
            }

            return true;
         } else {
            return false;
         }
      }

      private boolean canFlag(float outlineLimit, float outlineLimit2) {
         if (!(outlineLimit < 0.0F) && !(outlineLimit2 < 0.0F) && !(outlineLimit >= this.gridWidth) && !(outlineLimit2 >= this.gridHeight)) {
            int index = (int)outlineLimit2 / 16 * this.tilesAcross + (int)outlineLimit / 16;
            int index2 = this.tileOffsets[index];

            for (int i = this.tileOffsets[index + 1]; index2 < i; index2++) {
               int index3 = this.tileSegmentIndices[index2] * 7;
               float outlineValue = this.segmentData[index3];
               float outlineValue2 = this.segmentData[index3 + 1];
               float outlineValue3 = this.segmentData[index3 + 2];
               float outlineValue4 = this.segmentData[index3 + 3];
               float outlineValue5 = this.segmentData[index3 + 4];
               float outlineValue6 = this.segmentData[index3 + 5];
               float outlineFactor = this.segmentData[index3 + 6];
               float outlineValue7 = ((outlineValue3 - outlineLimit) * (outlineValue6 - outlineLimit2) - (outlineValue4 - outlineLimit2) * (outlineValue5 - outlineLimit)) * outlineFactor;
               if (!(outlineValue7 < 0.0F)) {
                  float outlineValue8 = ((outlineValue5 - outlineLimit) * (outlineValue2 - outlineLimit2) - (outlineValue6 - outlineLimit2) * (outlineValue - outlineLimit)) * outlineFactor;
                  if (!(outlineValue8 < 0.0F) && !(outlineValue7 + outlineValue8 > 1.0F)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }
}
