package com.corz.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import com.corz.client.utils.KnifeRig;

public final class KnifeFinishMesh {
   public final float[] knifeFinishTable;
   private static final Map<String, KnifeFinishMesh> MESH_CACHE = new ConcurrentHashMap<>();
   public final int[] table;
   public final float[] uvCoords;
   public final int[] triangleIndices;
   private static final Map<String, int[]> RANGE_CACHE = new ConcurrentHashMap<>();
   public final int[] copyOf;
   public final float[] vertexPositions;
   public static final String ASSETS_CORZCLIENT_TEXTURES = "/assets/corzclient/textures/knife/knife.png";

   public static int[] getOfRange(String rangeText) {
      int[] ofRange = RANGE_CACHE.get(rangeText);
      if (ofRange != null) {
         return ofRange;
      } else {
         int[] rangeValues;
         try {
            label91: {
               Object obj;
               try (InputStream resourceAsStream = KnifeFinishMesh.class.getResourceAsStream(rangeText)) {
                  if (resourceAsStream == null) {
                     return null;
                  }

                  int[] iendRns = getIendRns(resourceAsStream.readAllBytes());
                  if (iendRns != null && iendRns[0] == 256 && iendRns[1] == 256) {
                     rangeValues = Arrays.copyOfRange(iendRns, 2, iendRns.length);
                     break label91;
                  }

                  obj = null;
               }

               return (int[])obj;
            }
         } catch (RuntimeException | IOException e) {
            return null;
         }

         int[] putIfAbsent = RANGE_CACHE.putIfAbsent(rangeText, rangeValues);
         return putIfAbsent != null ? putIfAbsent : rangeValues;
      }
   }

   public static KnifeFinishMesh getMeshKnife(byte[] meshKnifeBytes) {
      ByteBuffer order = ByteBuffer.wrap(meshKnifeBytes).order(ByteOrder.LITTLE_ENDIAN);
      if (meshKnifeBytes.length >= 12 && order.getInt() == 826691147) {
         int int2 = order.getInt();
         int int3 = order.getInt();
         if (int2 > 0 && int3 > 0 && int3 % 3 == 0 && int2 * 36L + int3 * 4L + 12L == meshKnifeBytes.length) {
            float[] floats = new float[int2 * 3];
            float[] floats2 = new float[int2 * 3];
            float[] floats3 = new float[int2 * 2];
            int[] ints = new int[int2];
            int[] ints2 = new int[int3];

            for (int i = 0; i < int2; i++) {
               floats[i * 3] = order.getFloat();
               floats[i * 3 + 1] = order.getFloat();
               floats[i * 3 + 2] = order.getFloat();
               floats2[i * 3] = order.getFloat();
               floats2[i * 3 + 1] = order.getFloat();
               floats2[i * 3 + 2] = order.getFloat();
               floats3[i * 2] = order.getFloat();
               floats3[i * 2 + 1] = order.getFloat();
               ints[i] = order.getInt();
            }

            for (int j = 0; j < int3; j++) {
               int int4 = order.getInt();
               if (int4 < 0 || int4 >= int2) {
                  throw new IllegalArgumentException("knife mesh index out of range");
               }

               ints2[j] = int4;
            }

            return new KnifeFinishMesh(floats, floats2, floats3, ints, ints2);
         } else {
            throw new IllegalArgumentException("knife mesh size mismatch");
         }
      } else {
         throw new IllegalArgumentException("not a knife mesh");
      }
   }

   private KnifeFinishMesh(float[] amounts, float[] amounts2, float[] amounts3, int[] values, int[] values2) {
      this.vertexPositions = amounts;
      this.knifeFinishTable = amounts2;
      this.uvCoords = amounts3;
      this.table = values;
      this.triangleIndices = values2;
      this.copyOf = this.getCopyOf();
   }

   public static int[] getKnifeAssets(KnifeRig knifeRig) {
      return getOfRange(knifeRig == null ? "/assets/corzclient/textures/knife/knife.png" : knifeRig.getChildParentText());
   }

   private static long getTime(int knifeFinishValue, int knifeFinishLimit) {
      return knifeFinishValue < knifeFinishLimit ? (long)knifeFinishValue << 32 | knifeFinishLimit & 4294967295L : (long)knifeFinishLimit << 32 | knifeFinishValue & 4294967295L;
   }

   public static KnifeFinishMesh[] getModel(KnifeRig knifeRig) {
      KnifeFinishMesh[] knifeFinishMeshes = new KnifeFinishMesh[knifeRig.getParentParts()];

      for (int i = 0; i < knifeFinishMeshes.length; i++) {
         knifeFinishMeshes[i] = getKnifeFinishMesh(knifeRig.getParentParts(i).model());
         if (knifeFinishMeshes[i] == null) {
            return null;
         }
      }

      return knifeFinishMeshes;
   }

   public int getValue() {
      return this.copyOf.length / 4;
   }

   private int[] getAmounts() {
      int knifeFinishSize = this.table.length;
      int[] ints = new int[knifeFinishSize];
      HashMap hashMap = new HashMap();

      for (int i = 0; i < knifeFinishSize; i++) {
         String knifeFinishText = Float.floatToIntBits(this.vertexPositions[i * 3])
            + ":"
            + Float.floatToIntBits(this.vertexPositions[i * 3 + 1])
            + ":"
            + Float.floatToIntBits(this.vertexPositions[i * 3 + 2])
            + ":"
            + Float.floatToIntBits(this.uvCoords[i * 2])
            + ":"
            + Float.floatToIntBits(this.uvCoords[i * 2 + 1])
            + ":"
            + this.table[i]
            + ":"
            + Math.round(this.knifeFinishTable[i * 3] * 2000.0F)
            + ":"
            + Math.round(this.knifeFinishTable[i * 3 + 1] * 2000.0F)
            + ":"
            + Math.round(this.knifeFinishTable[i * 3 + 2] * 2000.0F);
         Integer putIfAbsent = ((java.lang.Integer) (hashMap.putIfAbsent(knifeFinishText, i)));
         ints[i] = putIfAbsent == null ? i : putIfAbsent;
      }

      return ints;
   }

   public int getTable() {
      return this.table.length;
   }

   public static int[] getKnifeAssets() {
      return getOfRange("/assets/corzclient/textures/knife/knife.png");
   }

   public static KnifeFinishMesh getKnifeFinishMesh(String knifeFinishText) {
      KnifeFinishMesh knifeFinishMesh = MESH_CACHE.get(knifeFinishText);
      if (knifeFinishMesh != null) {
         return knifeFinishMesh;
      } else {
         KnifeFinishMesh i;
         try {
            label61: {
               Object obj;
               try (InputStream resourceAsStream = KnifeFinishMesh.class.getResourceAsStream("/assets/corzclient/knife/" + knifeFinishText + ".knife")) {
                  if (resourceAsStream != null) {
                     i = getMeshKnife(resourceAsStream.readAllBytes());
                     break label61;
                  }

                  obj = null;
               }

               return (KnifeFinishMesh)obj;
            }
         } catch (IOException | IllegalArgumentException e) {
            return null;
         }

         KnifeFinishMesh putIfAbsent = MESH_CACHE.putIfAbsent(knifeFinishText, i);
         return putIfAbsent != null ? putIfAbsent : i;
      }
   }

   public int getTriangleIndices() {
      return this.triangleIndices.length / 3;
   }

   static int[] getIendRns(byte[] iendRnsBytes) {
      if (iendRnsBytes.length >= 8 && (iendRnsBytes[0] & 255) == 137 && iendRnsBytes[1] == 80 && iendRnsBytes[2] == 78 && iendRnsBytes[3] == 71) {
         int index = 8;
         int iendRnsFactor = 0;
         int iendRnsLimit = 0;
         int iendRnsValue = 0;
         int iendRnsValue2 = -1;
         byte[] copyOfRange2 = null;
         byte[] copyOfRange3 = null;
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         while (index + 8 <= iendRnsBytes.length) {
            int iendRnsValue3 = (iendRnsBytes[index] & 255) << 24 | (iendRnsBytes[index + 1] & 255) << 16 | (iendRnsBytes[index + 2] & 255) << 8 | iendRnsBytes[index + 3] & 255;
            String iendRnsText = new String(iendRnsBytes, index + 4, 4, StandardCharsets.US_ASCII);
            int index2 = index + 8;
            if (iendRnsValue3 < 0 || index2 + iendRnsValue3 > iendRnsBytes.length) {
               return null;
            }

            switch (iendRnsText) {
               case "IHDR":
                  iendRnsFactor = (iendRnsBytes[index2] & 255) << 24 | (iendRnsBytes[index2 + 1] & 255) << 16 | (iendRnsBytes[index2 + 2] & 255) << 8 | iendRnsBytes[index2 + 3] & 255;
                  iendRnsLimit = (iendRnsBytes[index2 + 4] & 255) << 24 | (iendRnsBytes[index2 + 5] & 255) << 16 | (iendRnsBytes[index2 + 6] & 255) << 8 | iendRnsBytes[index2 + 7] & 255;
                  iendRnsValue = iendRnsBytes[index2 + 8] & 255;
                  iendRnsValue2 = iendRnsBytes[index2 + 9] & 255;
                  if (iendRnsBytes[index2 + 12] != 0) {
                     return null;
                  }
                  break;
               case "PLTE":
                  copyOfRange2 = Arrays.copyOfRange(iendRnsBytes, index2, index2 + iendRnsValue3);
                  break;
               case "tRNS":
                  copyOfRange3 = Arrays.copyOfRange(iendRnsBytes, index2, index2 + iendRnsValue3);
                  break;
               case "IDAT":
                  byteArrayOutputStream.write(iendRnsBytes, index2, iendRnsValue3);
            }

            if (iendRnsText.equals("IEND")) {
               break;
            }

            index = index2 + iendRnsValue3 + 4;
         }

         if (iendRnsValue == 8 && iendRnsFactor > 0 && iendRnsLimit > 0) {
            byte iendRnsFactor2 = switch (iendRnsValue2) {
               case 0 -> 1;
               default -> -1;
               case 2 -> 3;
               case 3 -> 1;
               case 4 -> 2;
               case 6 -> 4;
            };
            if (iendRnsFactor2 < 0) {
               return null;
            } else {
               int iendRnsSize = iendRnsFactor * iendRnsFactor2;
               byte[] bytes = new byte[(iendRnsSize + 1) * iendRnsLimit];
               Inflater inflater = new Inflater();

               try {
                  inflater.setInput(byteArrayOutputStream.toByteArray());
                  int iendRnsTotal = 0;

                  while (iendRnsTotal < bytes.length && !inflater.finished()) {
                     int inflate = inflater.inflate(bytes, iendRnsTotal, bytes.length - iendRnsTotal);
                     if (inflate == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
                        break;
                     }

                     iendRnsTotal += inflate;
                  }

                  if (iendRnsTotal < bytes.length) {
                     return null;
                  }
               } catch (DataFormatException e) {
                  return null;
               } finally {
                  inflater.end();
               }

               byte[] bytes2 = new byte[iendRnsSize];
               byte[] bytes3 = new byte[iendRnsSize];
               int[] ints = new int[2 + iendRnsFactor * iendRnsLimit];
               ints[0] = iendRnsFactor;
               ints[1] = iendRnsLimit;

               for (int i = 0; i < iendRnsLimit; i++) {
                  int iendRnsValue4 = bytes[i * (iendRnsSize + 1)] & 255;
                  int iendRnsValue5 = i * (iendRnsSize + 1) + 1;

                  for (int j = 0; j < iendRnsSize; j++) {
                     int iendRnsValue6 = bytes[iendRnsValue5 + j] & 255;
                     int iendRnsValue7 = j >= iendRnsFactor2 ? bytes2[j - iendRnsFactor2] & 255 : 0;
                     int iendRnsValue8 = bytes3[j] & 255;
                     int iendRnsValue9 = j >= iendRnsFactor2 ? bytes3[j - iendRnsFactor2] & 255 : 0;

                     int iendRnsValue10 = switch (iendRnsValue4) {
                        case 0 -> iendRnsValue6;
                        case 1 -> iendRnsValue6 + iendRnsValue7;
                        case 2 -> iendRnsValue6 + iendRnsValue8;
                        case 3 -> iendRnsValue6 + (iendRnsValue7 + iendRnsValue8 >> 1);
                        case 4 -> {
                           int iendRnsValue11 = iendRnsValue7 + iendRnsValue8 - iendRnsValue9;
                           int abs12 = Math.abs(iendRnsValue11 - iendRnsValue7);
                           int abs2 = Math.abs(iendRnsValue11 - iendRnsValue8);
                           int abs3 = Math.abs(iendRnsValue11 - iendRnsValue9);
                           yield iendRnsValue6 + (abs12 <= abs2 && abs12 <= abs3 ? iendRnsValue7 : (abs2 <= abs3 ? iendRnsValue8 : iendRnsValue9));
                        }
                        default -> -1;
                     };
                     if (iendRnsValue10 < 0) {
                        return null;
                     }

                     bytes2[j] = (byte)iendRnsValue10;
                  }

                  for (int k = 0; k < iendRnsFactor; k++) {
                     int iendRnsValue13 = 255;
                     int index3 = k * iendRnsFactor2;
                     int iendRnsValue14;
                     int iendRnsValue15;
                     int iendRnsValue16;
                     switch (iendRnsValue2) {
                        case 0:
                           iendRnsValue16 = iendRnsValue15 = iendRnsValue14 = bytes2[index3] & 255;
                           break;
                        case 1:
                        default:
                           iendRnsValue16 = bytes2[index3] & 255;
                           iendRnsValue15 = bytes2[index3 + 1] & 255;
                           iendRnsValue14 = bytes2[index3 + 2] & 255;
                           iendRnsValue13 = bytes2[index3 + 3] & 255;
                           break;
                        case 2:
                           iendRnsValue16 = bytes2[index3] & 255;
                           iendRnsValue15 = bytes2[index3 + 1] & 255;
                           iendRnsValue14 = bytes2[index3 + 2] & 255;
                           break;
                        case 3:
                           int index4 = bytes2[index3] & 255;
                           if (copyOfRange2 == null || index4 * 3 + 2 >= copyOfRange2.length) {
                              return null;
                           }

                           iendRnsValue16 = copyOfRange2[index4 * 3] & 255;
                           iendRnsValue15 = copyOfRange2[index4 * 3 + 1] & 255;
                           iendRnsValue14 = copyOfRange2[index4 * 3 + 2] & 255;
                           if (copyOfRange3 != null && index4 < copyOfRange3.length) {
                              iendRnsValue13 = copyOfRange3[index4] & 255;
                           }
                           break;
                        case 4:
                           iendRnsValue16 = iendRnsValue15 = iendRnsValue14 = bytes2[index3] & 255;
                           iendRnsValue13 = bytes2[index3 + 1] & 255;
                     }

                     ints[2 + i * iendRnsFactor + k] = iendRnsValue13 << 24 | iendRnsValue16 << 16 | iendRnsValue15 << 8 | iendRnsValue14;
                  }

                  byte[] iendRnsBytes4 = bytes3;
                  bytes3 = bytes2;
                  bytes2 = iendRnsBytes4;
               }

               return ints;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean canFlag(int[] knifeFinishValues, int knifeFinishValue) {
      int knifeFinishValue2 = knifeFinishValues[this.triangleIndices[knifeFinishValue * 3]];
      int knifeFinishValue3 = knifeFinishValues[this.triangleIndices[knifeFinishValue * 3 + 1]];
      int knifeFinishValue4 = knifeFinishValues[this.triangleIndices[knifeFinishValue * 3 + 2]];
      return knifeFinishValue2 == knifeFinishValue3 || knifeFinishValue3 == knifeFinishValue4 || knifeFinishValue2 == knifeFinishValue4;
   }

   private int[] getCopyOf() {
      int knifeFinishLimit = this.triangleIndices.length / 3;
      int[] knifeFinishValues = this.getAmounts();
      HashMap hashMap = new HashMap();

      for (int i = 0; i < knifeFinishLimit; i++) {
         for (int j = 0; j < 3; j++) {
            ((java.util.List) hashMap.computeIfAbsent(getTime(knifeFinishValues[this.triangleIndices[i * 3 + j]], knifeFinishValues[this.triangleIndices[i * 3 + (j + 1) % 3]]), obj -> new ArrayList<>(2)))
               .add(i);
         }
      }

      boolean[] booleans = new boolean[knifeFinishLimit];
      int[] ints = new int[knifeFinishLimit * 4];
      int knifeFinishCounter = 0;

      for (int m = 0; m < knifeFinishLimit; m++) {
         if (!booleans[m]) {
            booleans[m] = true;
            int index = -1;
            int knifeFinishValue = -1;

            for (int p = 0; p < 3 && index < 0; p++) {
               for (int index2 : (Iterable<Integer>) ((List)hashMap.get(getTime(knifeFinishValues[this.triangleIndices[m * 3 + p]], knifeFinishValues[this.triangleIndices[m * 3 + (p + 1) % 3]])))) {
                  if (!booleans[index2] && !this.canFlag(knifeFinishValues, index2)) {
                     index = index2;
                     knifeFinishValue = p;
                     break;
                  }
               }
            }

            if (index >= 0 && !this.canFlag(knifeFinishValues, m)) {
               booleans[index] = true;
               int index3 = this.triangleIndices[m * 3 + knifeFinishValue];
               int index4 = this.triangleIndices[m * 3 + (knifeFinishValue + 1) % 3];
               int knifeFinishValue2 = this.triangleIndices[m * 3 + (knifeFinishValue + 2) % 3];
               int knifeFinishValue3 = -1;

               for (int q = 0; q < 3; q++) {
                  int index5 = this.triangleIndices[index * 3 + q];
                  if (knifeFinishValues[index5] != knifeFinishValues[index4] && knifeFinishValues[index5] != knifeFinishValues[index3]) {
                     knifeFinishValue3 = index5;
                  }
               }

               ints[knifeFinishCounter++] = index4;
               ints[knifeFinishCounter++] = knifeFinishValue2;
               ints[knifeFinishCounter++] = index3;
               ints[knifeFinishCounter++] = knifeFinishValue3;
            } else {
               int knifeFinishValue4 = this.triangleIndices[m * 3];
               int knifeFinishValue5 = this.triangleIndices[m * 3 + 1];
               int knifeFinishValue6 = this.triangleIndices[m * 3 + 2];
               ints[knifeFinishCounter++] = knifeFinishValue4;
               ints[knifeFinishCounter++] = knifeFinishValue5;
               ints[knifeFinishCounter++] = knifeFinishValue6;
               ints[knifeFinishCounter++] = knifeFinishValue6;
            }
         }
      }

      return Arrays.copyOf(ints, knifeFinishCounter);
   }
}
