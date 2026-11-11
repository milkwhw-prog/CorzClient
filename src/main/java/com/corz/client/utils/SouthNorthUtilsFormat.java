package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.corz.client.utils.PistonStickyUtils;

public final class SouthNorthUtilsFormat {
   public static final List<String> ATTACHED_EAST_ITEMS = List.of("down", "up", "north", "south", "west", "east");

   public static int getValue(int southNorthLimit, boolean southNorthFlag) {
      if (southNorthLimit >= 0 && !PistonStickyUtils.canFlag(southNorthLimit)) {
         return southNorthFlag ? PistonStickyUtils.getValue(southNorthLimit) : southNorthLimit;
      } else {
         return -1;
      }
   }

   public static boolean canFlag(boolean southNorthFlag, boolean southNorthFlag2) {
      return southNorthFlag && !southNorthFlag2;
   }

   private static float getAmount(float southNorthValue) {
      float southNorthTotal = southNorthValue % 360.0F;
      if (southNorthTotal >= 180.0F) {
         southNorthTotal -= 360.0F;
      }

      if (southNorthTotal < -180.0F) {
         southNorthTotal += 360.0F;
      }

      return southNorthTotal;
   }

   public static boolean canAttachedEast(Map<String, String> map, Map<String, String> map2) {
      for (String attachedEastText : ATTACHED_EAST_ITEMS) {
         if (!Boolean.parseBoolean(map.getOrDefault(attachedEastText, "false")) && Boolean.parseBoolean(map2.getOrDefault(attachedEastText, "false"))) {
            return true;
         }
      }

      return false;
   }

   public static boolean canFlag(int southNorthValue, String southNorthText) {
      if (southNorthText == null) {
         return true;
      } else {
         boolean southNorthFlag = southNorthValue == 5 || southNorthValue == 4;
         boolean southNorthFlag2 = southNorthValue == 2 || southNorthValue == 3;
         return "x".equals(southNorthText) ? southNorthFlag : ("z".equals(southNorthText) ? southNorthFlag2 : false);
      }
   }

   public static boolean canSingle(String singleText, String moneyEnoughText, boolean singleFlag) {
      return !singleFlag && !"single".equals(singleText) && "single".equals(moneyEnoughText);
   }

   private SouthNorthUtilsFormat() {
   }

   public static boolean canSingle(String singleText, String leftRightText, String singleText3, String moneyEnoughText) {
      if (singleText == null || leftRightText == null || singleText3 == null || moneyEnoughText == null) {
         return false;
      } else {
         return !"single".equals(leftRightText) && !"single".equals(moneyEnoughText)
            ? formatSouthNorth(singleText).equals(singleText3) && formatLeftRight(leftRightText).equals(moneyEnoughText)
            : false;
      }
   }

   public static int getValue(SouthNorthUtilsFormat.SignMount signMount, int southNorthValue) {
      return switch (signMount) {
         case STANDING -> 0;
         case WALL -> PistonStickyUtils.getValue(southNorthValue);
         case HANGING -> 1;
      };
   }

   private static String formatLeftRight(String leftRightText) {
      return switch (leftRightText) {
         case "left" -> "right";
         case "right" -> "left";
         default -> "";
      };
   }

   public static int getFloorMod(float floorModValue) {
      return Math.floorMod((int)Math.floor((floorModValue + 180.0F) / 22.5F + 0.5F), 16);
   }

   public static float getAmount(int southNorthValue) {
      int floorMod = Math.floorMod(southNorthValue, 16);
      return getAmount(floorMod * 22.5F - 180.0F);
   }

   private static String formatSouthNorth(String southNorthText) {
      return switch (southNorthText) {
         case "north" -> "south";
         case "south" -> "north";
         case "east" -> "west";
         case "west" -> "east";
         default -> "";
      };
   }

   public static List<String> getAttachedEast(Map<String, String> map, Map<String, String> map2) {
      var arrayList = new ArrayList();

      for (String attachedEastText : ATTACHED_EAST_ITEMS) {
         if (Boolean.parseBoolean(map.getOrDefault(attachedEastText, "false")) && !Boolean.parseBoolean(map2.getOrDefault(attachedEastText, "false"))) {
            arrayList.add(attachedEastText);
         }
      }

      return arrayList;
   }

   public static String formatAscendingSouth(String ascendingSouthText) {
      if (ascendingSouthText == null) {
         return null;
      } else {
         return switch (ascendingSouthText) {
            case "north_south", "ascending_north", "ascending_south" -> "z";
            case "east_west", "ascending_east", "ascending_west" -> "x";
            default -> null;
         };
      }
   }

   public static boolean canFlag(int southNorthValue, int southNorthBound) {
      return southNorthValue < Math.max(0, southNorthBound);
   }

   public static boolean canFlag(String southNorthText, String moneyEnoughText, boolean southNorthFlag) {
      if (southNorthText == null || moneyEnoughText == null) {
         return false;
      } else if (southNorthText.equals(moneyEnoughText)) {
         return true;
      } else if (!southNorthFlag && canSouthNorth(moneyEnoughText)) {
         String formatAscendingSouth2 = formatAscendingSouth(southNorthText);
         return formatAscendingSouth2 == null || formatAscendingSouth2.equals(formatAscendingSouth(moneyEnoughText));
      } else {
         return false;
      }
   }

   private static boolean canSouthNorth(String southNorthText) {
      return "north_south".equals(southNorthText) || "east_west".equals(southNorthText);
   }

   public static boolean canFlag(boolean southNorthFlag, boolean southNorthFlag2, boolean southNorthFlag3, boolean southNorthFlag4, boolean southNorthFlag5) {
      return !southNorthFlag && !southNorthFlag2 && southNorthFlag3 && southNorthFlag4 && southNorthFlag5;
   }

   public static final class RailOrder {
      private static final int[] curvesTable = new int[]{0, 0, -1, 1};
      private static final int[] shapeCurvesTable = new int[]{-1, 1, 0, 0};
      private static final int ORDER_CODE_ASCENDING = 1;
      private static final int[][] shapeInts = new int[][]{{0, 1}, {2, 3}, {3, 1}, {2, 1}, {2, 0}, {3, 0}};

      private static int getShape(SouthNorthUtilsFormat.RailOrder.Rail rail, boolean shapeFlag) {
         return rail.shape() != 0 && rail.shape() != 1 ? (shapeFlag ? 1 : 0) : rail.shape();
      }

      private static List<SouthNorthUtilsFormat.RailOrder.Rail> getItems(
         List<SouthNorthUtilsFormat.RailOrder.Rail> list, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> map, List<SouthNorthUtilsFormat.RailOrder.Rail> list2
      ) {
         var hashMap = new HashMap();
         var arrayList = new ArrayList();
         var arrayList2 = new ArrayList(list2);
         var arrayList3 = new ArrayList(list2.size());

         while (!arrayList2.isEmpty()) {
            int railOrderValue = -1;

            for (int i = 0; i < arrayList2.size(); i++) {
               if (canShapeCurves(hashMap, map, (SouthNorthUtilsFormat.RailOrder.Rail)arrayList2.get(i), arrayList)) {
                  railOrderValue = i;
                  break;
               }
            }

            if (railOrderValue < 0) {
               return null;
            }

            arrayList3.add((SouthNorthUtilsFormat.RailOrder.Rail)arrayList2.remove(railOrderValue));
         }

         return canShape(hashMap, list) ? arrayList3 : null;
      }

      private RailOrder() {
      }

      private static int getValue(boolean railOrderFlag, boolean railOrderFlag2, boolean railOrderFlag3, boolean railOrderFlag4, boolean railOrderFlag5, int railOrderValue) {
         byte railOrderValue2 = -1;
         if (railOrderFlag2 || railOrderFlag3) {
            railOrderValue2 = 0;
         }

         if (railOrderFlag4 || railOrderFlag5) {
            railOrderValue2 = 1;
         }

         if (railOrderFlag) {
            if (railOrderFlag3 && railOrderFlag5 && !railOrderFlag2 && !railOrderFlag4) {
               railOrderValue2 = 2;
            }

            if (railOrderFlag3 && railOrderFlag4 && !railOrderFlag2 && !railOrderFlag5) {
               railOrderValue2 = 3;
            }

            if (railOrderFlag2 && railOrderFlag4 && !railOrderFlag3 && !railOrderFlag5) {
               railOrderValue2 = 4;
            }

            if (railOrderFlag2 && railOrderFlag5 && !railOrderFlag3 && !railOrderFlag4) {
               railOrderValue2 = 5;
            }
         }

         return railOrderValue2 < 0 ? railOrderValue : railOrderValue2;
      }

      private static long getTime(int railOrderValue, int railOrderValue2) {
         return (long)railOrderValue << 32 | railOrderValue2 & 4294967295L;
      }

      public static Map<Long, long[]> getMap(List<SouthNorthUtilsFormat.RailOrder.Rail> list) {
         var hashMap = new HashMap();
         var hashSet = new HashSet();
         var treeMap = new TreeMap<Integer, List<SouthNorthUtilsFormat.RailOrder.Rail>>();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
            hashSet.add(getTime(rail.x(), rail.y(), rail.z()));
            treeMap.computeIfAbsent(rail.y(), integer -> new ArrayList<>()).add(rail);
         }

         for (List<SouthNorthUtilsFormat.RailOrder.Rail> list2 : treeMap.values()) {
            handle(list2, hashSet, hashMap);
         }

         return hashMap;
      }

      static Map<Long, Integer> getCurves(List<SouthNorthUtilsFormat.RailOrder.Rail> list, boolean curvesFlag) {
         var hashMap = new HashMap();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
            hashMap.put(getTime(rail.x(), rail.z()), rail);
         }

         var hashMap2 = new HashMap();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail2 : list) {
            handleCurves(hashMap2, hashMap, rail2.x(), rail2.z(), rail2.curves(), getShape(rail2, curvesFlag), null);
         }

         var hashMap3 = new HashMap();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail3 : list) {
            hashMap3.put(rail3.id(), (Integer)hashMap2.get(getTime(rail3.x(), rail3.z())));
         }

         return hashMap3;
      }

      private static boolean canShape(Map<Long, Integer> map, List<SouthNorthUtilsFormat.RailOrder.Rail> list) {
         for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
            Integer integer = (Integer)map.get(getTime(rail.x(), rail.z()));
            if (integer == null || integer != rail.shape()) {
               return false;
            }
         }

         return true;
      }

      private static void handle(List<SouthNorthUtilsFormat.RailOrder.Rail> list, Set<Long> set, Map<Long, long[]> map) {
         var linkedHashMap = new LinkedHashMap();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
            linkedHashMap.put(getTime(rail.x(), rail.z()), rail);
         }

         var hashSet = new HashSet();

         for (SouthNorthUtilsFormat.RailOrder.Rail rail2 : list) {
            if (hashSet.add(getTime(rail2.x(), rail2.z()))) {
               var arrayList = new ArrayList();
               var arrayList2 = new ArrayList();
               arrayList.add(rail2);
               arrayList2.add(rail2);

               while (!arrayList2.isEmpty()) {
                  SouthNorthUtilsFormat.RailOrder.Rail rail3 = (SouthNorthUtilsFormat.RailOrder.Rail)arrayList2.remove(arrayList2.size() - 1);

                  for (int i = 0; i < 4; i++) {
                     long railOrderValue = getTime(rail3.x() + curvesTable[i], rail3.z() + shapeCurvesTable[i]);
                     SouthNorthUtilsFormat.RailOrder.Rail totalPriceData = (SouthNorthUtilsFormat.RailOrder.Rail)linkedHashMap.get(railOrderValue);
                     if (totalPriceData != null && hashSet.add(railOrderValue)) {
                        arrayList.add(totalPriceData);
                        arrayList2.add(totalPriceData);
                     }
                  }
               }

               handleCurvesShape(arrayList, linkedHashMap, set, map);
            }
         }
      }

      private static boolean canFlag(Map<Long, Integer> map, int railOrderValue, int railOrderValue2, int index) {
         int railOrderValue3 = railOrderValue + curvesTable[index];
         int railOrderValue4 = railOrderValue2 + shapeCurvesTable[index];
         if (!map.containsKey(getTime(railOrderValue3, railOrderValue4))) {
            return false;
         } else {
            int railOrderValue5 = getValue(map, railOrderValue3, railOrderValue4);
            return (railOrderValue5 & 1 << (index ^ 1)) != 0 || Integer.bitCount(railOrderValue5) != 2;
         }
      }

      private static boolean canShapeCurves(
         Map<Long, Integer> map, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> hashMap, SouthNorthUtilsFormat.RailOrder.Rail rail, List<long[]> list
      ) {
         int[] shapeCurvesValues = rail.shape() != 0 && rail.shape() != 1 ? new int[]{0, 1} : new int[]{rail.shape()};
         int[] restartedClimbValues2 = null;

         for (int i = 0; i < shapeCurvesValues.length; i++) {
            handleCurves(map, hashMap, rail.x(), rail.z(), rail.curves(), shapeCurvesValues[i], list);
            boolean shape = canShape(map, hashMap, rail.x(), rail.z());

            for (int j = 0; j < 4; j++) {
               shape = shape && canShape(map, hashMap, rail.x() + curvesTable[j], rail.z() + shapeCurvesTable[j]);
            }

            int[] restartedClimbValues = getValues(map, rail);
            if (!shape || restartedClimbValues2 != null && !Arrays.equals(restartedClimbValues, restartedClimbValues2)) {
               handle(map, list);
               return false;
            }

            restartedClimbValues2 = restartedClimbValues;
            if (i < shapeCurvesValues.length - 1) {
               handle(map, list);
            }
         }

         list.clear();
         return true;
      }

      private static int getValue(Map<Long, Integer> map, int railOrderValue, int railOrderValue2) {
         Integer index = (Integer)map.get(getTime(railOrderValue, railOrderValue2));
         if (index == null) {
            return 0;
         } else {
            int railOrderValue3 = 0;

            for (int index2 : shapeInts[index]) {
               Integer integer = (Integer)map.get(getTime(railOrderValue + curvesTable[index2], railOrderValue2 + shapeCurvesTable[index2]));
               if (integer != null && (shapeInts[integer][0] == (index2 ^ 1) || shapeInts[integer][1] == (index2 ^ 1))) {
                  railOrderValue3 |= 1 << index2;
               }
            }

            return railOrderValue3;
         }
      }

      private static int[] getValues(Map<Long, Integer> map, SouthNorthUtilsFormat.RailOrder.Rail rail) {
         int[] ints = new int[5];
         ints[0] = getValue(map, rail.x(), rail.z()) == 0 ? -2 : (Integer)map.get(getTime(rail.x(), rail.z()));

         for (int i = 0; i < 4; i++) {
            Integer integer = (Integer)map.get(getTime(rail.x() + curvesTable[i], rail.z() + shapeCurvesTable[i]));
            ints[i + 1] = integer == null ? -1 : integer;
         }

         return ints;
      }

      private static boolean canShapeCurves(
         List<SouthNorthUtilsFormat.RailOrder.Rail> list, List<SouthNorthUtilsFormat.RailOrder.Rail> list2, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> map
      ) {
         boolean shapeCurvesFlag = false;

         for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
            if (rail.shape() != 0 && rail.shape() != 1) {
               shapeCurvesFlag = true;
            }
         }

         for (int i = 0; i < (shapeCurvesFlag ? 2 : 1); i++) {
            var hashMap = new HashMap();

            for (SouthNorthUtilsFormat.RailOrder.Rail rail2 : list2) {
               handleCurves(hashMap, map, rail2.x(), rail2.z(), rail2.curves(), getShape(rail2, i == 1), null);
            }

            if (!canShape(hashMap, list)) {
               return false;
            }
         }

         return true;
      }

      private static void handleCurvesShape(
         List<SouthNorthUtilsFormat.RailOrder.Rail> list, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> map, Set<Long> set, Map<Long, long[]> map2
      ) {
         if (list.size() >= 2 && list.size() <= 1024) {
            for (SouthNorthUtilsFormat.RailOrder.Rail rail : list) {
               if (rail.shape() < 0) {
                  return;
               }

               for (int i = 0; i < 4; i++) {
                  for (int j = -1; j <= 1; j += 2) {
                     if (set.contains(getTime(rail.x() + curvesTable[i], rail.y() + j, rail.z() + shapeCurvesTable[i]))) {
                        return;
                     }
                  }
               }
            }

            boolean curvesShapeFlag = false;

            for (SouthNorthUtilsFormat.RailOrder.Rail rail2 : list) {
               if (rail2.curves()) {
                  curvesShapeFlag = true;
                  break;
               }
            }

            if (curvesShapeFlag) {
               var arrayList = new ArrayList(list);
               var totalPriceData = new ArrayList(list);
               arrayList.sort((obj, obj2) -> ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj).x() != ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj2).x() ? Integer.compare(((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj).x(), ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj2).x()) : Integer.compare(((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj).z(), ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj2).z()));
               totalPriceData.sort((obj3, obj4) -> ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj3).z() != ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj4).z() ? Integer.compare(((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj3).z(), ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj4).z()) : Integer.compare(((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj3).x(), ((com.corz.client.utils.SouthNorthUtilsFormat.RailOrder.Rail) obj4).x()));
               var arrayList3 = new ArrayList(arrayList);
               var arrayList4 = new ArrayList(totalPriceData);
               Collections.reverse(arrayList3);
               Collections.reverse(arrayList4);
               var arrayList5 = new ArrayList();
               arrayList5.add(list);
               arrayList5.add(arrayList);
               arrayList5.add(totalPriceData);
               arrayList5.add(arrayList3);
               arrayList5.add(arrayList4);

               for (int k = 1; k <= 8; k++) {
                  arrayList5.add(getItems(list, k * 7919));
               }

               boolean curvesShapeFlag2 = false;

               for (List list2 : (Iterable<java.util.List>) (arrayList5)) {
                  if (!canShapeCurves(list, list2, map)) {
                     curvesShapeFlag2 = true;
                     break;
                  }
               }

               if (curvesShapeFlag2) {
                  List items3 = null;

                  for (List list4 : List.of(arrayList, totalPriceData, arrayList3, arrayList4)) {
                     items3 = getItems(list, map, list4);
                     if (items3 != null) {
                        break;
                     }
                  }

                  if (items3 != null) {
                     var hashMap = new HashMap();

                     for (int m = 0; m < items3.size(); m++) {
                        hashMap.put(
                           getTime(((SouthNorthUtilsFormat.RailOrder.Rail)items3.get(m)).x(), ((SouthNorthUtilsFormat.RailOrder.Rail)items3.get(m)).z()), m
                        );
                     }

                     for (int p = 0; p < items3.size(); p++) {
                        SouthNorthUtilsFormat.RailOrder.Rail rail3 = (SouthNorthUtilsFormat.RailOrder.Rail)items3.get(p);
                        var arrayList6 = new ArrayList();

                        for (int q = -3; q <= 3; q++) {
                           for (int r = -3; r <= 3; r++) {
                              if (Math.abs(q) + Math.abs(r) <= 3 && (q != 0 || r != 0)) {
                                 Integer index = (Integer)hashMap.get(getTime(rail3.x() + q, rail3.z() + r));
                                 if (index != null && index < p) {
                                    arrayList6.add(((SouthNorthUtilsFormat.RailOrder.Rail)items3.get(index)).id());
                                 }
                              }
                           }
                        }

                        long[] longs = new long[arrayList6.size()];

                        for (int s = 0; s < longs.length; s++) {
                           longs[s] = (Long)arrayList6.get(s);
                        }

                        map2.put(rail3.id(), longs);
                     }
                  }
               }
            }
         }
      }

      private static boolean canShape(Map<Long, Integer> map, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> hashMap, int shapeValue, int shapeValue2) {
         SouthNorthUtilsFormat.RailOrder.Rail rail = (SouthNorthUtilsFormat.RailOrder.Rail)hashMap.get(getTime(shapeValue, shapeValue2));
         Integer integer = (Integer)map.get(getTime(shapeValue, shapeValue2));
         if (rail != null && integer != null) {
            int shapeValue3 = getValue(map, shapeValue, shapeValue2);
            int shapeValue4 = 1 << shapeInts[rail.shape()][0] | 1 << shapeInts[rail.shape()][1];
            return (shapeValue3 & ~shapeValue4) != 0 ? false : Integer.bitCount(shapeValue3) < 2 || integer == rail.shape();
         } else {
            return true;
         }
      }

      private static long getTime(int railOrderValue, int railOrderValue2, int railOrderValue3) {
         return (long)(railOrderValue & 67108863) << 38 | (long)(railOrderValue3 & 67108863) << 12 | railOrderValue2 & 4095;
      }

      private static void handle(Map<Long, Integer> map, List<long[]> list) {
         for (int i = list.size() - 1; i >= 0; i--) {
            long[] log2 = (long[])list.get(i);
            if (log2[1] == Long.MIN_VALUE) {
               map.remove(log2[0]);
            } else {
               map.put(log2[0], (int)log2[1]);
            }
         }

         list.clear();
      }

      private static List<SouthNorthUtilsFormat.RailOrder.Rail> getItems(List<SouthNorthUtilsFormat.RailOrder.Rail> list, int railOrderValue) {
         var arrayList = new ArrayList(list);
         int comp2 = railOrderValue;

         for (int i = arrayList.size() - 1; i > 0; i--) {
            comp2 = comp2 * 1103515245 + 12345;
            int railOrderValue2 = (int)((comp2 & 4294967295L) % (i + 1));
            SouthNorthUtilsFormat.RailOrder.Rail rail = (SouthNorthUtilsFormat.RailOrder.Rail)arrayList.get(i);
            arrayList.set(i, (SouthNorthUtilsFormat.RailOrder.Rail)arrayList.get(railOrderValue2));
            arrayList.set(railOrderValue2, rail);
         }

         return arrayList;
      }

      private static void handleCurves(
         Map<Long, Integer> map, Map<Long, SouthNorthUtilsFormat.RailOrder.Rail> hashMap, int curvesValue, int curvesValue2, boolean curvesFlag, int botCrystalValue, List<long[]> list
      ) {
         long obsidianIdleValue = getTime(curvesValue, curvesValue2);
         if (list != null) {
            list.add(new long[]{obsidianIdleValue, map.containsKey(obsidianIdleValue) ? ((Integer)map.get(obsidianIdleValue)).intValue() : Long.MIN_VALUE});
         }

         map.put(obsidianIdleValue, botCrystalValue);
         boolean canFlag2 = canFlag(map, curvesValue, curvesValue2, 0);
         boolean canFlag3 = canFlag(map, curvesValue, curvesValue2, 1);
         boolean canFlag4 = canFlag(map, curvesValue, curvesValue2, 2);
         boolean canFlag5 = canFlag(map, curvesValue, curvesValue2, 3);
         int index = -1;
         boolean curvesFlag6 = canFlag2 || canFlag3;
         boolean curvesFlag7 = canFlag4 || canFlag5;
         if (curvesFlag6 && !curvesFlag7) {
            index = 0;
         }

         if (curvesFlag7 && !curvesFlag6) {
            index = 1;
         }

         boolean curvesFlag8 = canFlag3 && canFlag5;
         boolean curvesFlag9 = canFlag3 && canFlag4;
         boolean curvesFlag10 = canFlag2 && canFlag5;
         boolean curvesFlag11 = canFlag2 && canFlag4;
         if (curvesFlag) {
            if (curvesFlag8 && !canFlag2 && !canFlag4) {
               index = 2;
            }

            if (curvesFlag9 && !canFlag2 && !canFlag5) {
               index = 3;
            }

            if (curvesFlag11 && !canFlag3 && !canFlag5) {
               index = 4;
            }

            if (curvesFlag10 && !canFlag3 && !canFlag4) {
               index = 5;
            }
         }

         if (index < 0) {
            if (curvesFlag6 && curvesFlag7) {
               index = botCrystalValue;
            } else if (curvesFlag6) {
               index = 0;
            } else if (curvesFlag7) {
               index = 1;
            }

            if (curvesFlag) {
               if (curvesFlag11) {
                  index = 4;
               }

               if (curvesFlag10) {
                  index = 5;
               }

               if (curvesFlag9) {
                  index = 3;
               }

               if (curvesFlag8) {
                  index = 2;
               }
            }
         }

         if (index < 0) {
            index = botCrystalValue;
         }

         map.put(obsidianIdleValue, index);

         for (int index2 : shapeInts[index]) {
            long botCrystalText = getTime(curvesValue + curvesTable[index2], curvesValue2 + shapeCurvesTable[index2]);
            Integer integer = (Integer)map.get(botCrystalText);
            if (integer != null) {
               int curvesValue6 = getValue(map, curvesValue + curvesTable[index2], curvesValue2 + shapeCurvesTable[index2]);
               if ((curvesValue6 & 1 << (index2 ^ 1)) != 0 || Integer.bitCount(curvesValue6) != 2) {
                  int curvesValue7 = curvesValue6 | 1 << (index2 ^ 1);
                  SouthNorthUtilsFormat.RailOrder.Rail rail = (SouthNorthUtilsFormat.RailOrder.Rail)hashMap.get(botCrystalText);
                  int curvesValue8 = getValue(rail != null && rail.curves(), (curvesValue7 & 1) != 0, (curvesValue7 & 2) != 0, (curvesValue7 & 4) != 0, (curvesValue7 & 8) != 0, integer);
                  if (list != null) {
                     list.add(new long[]{botCrystalText, integer.intValue()});
                  }

                  map.put(botCrystalText, curvesValue8);
               }
            }
         }
      }

      public static int getSouthNorth(String southNorthText) {
         return switch (southNorthText) {
            case "north_south" -> 0;
            case "east_west" -> 1;
            case "south_east" -> 2;
            case "south_west" -> 3;
            case "north_west" -> 4;
            case "north_east" -> 5;
            default -> -1;
         };
      }

      public record Rail(long id, int x, int y, int z, boolean curves, int shape) {
      }
   }

   public static enum SignMount {
      STANDING,
      WALL,
      HANGING;
   }

   public static final class StableMismatch {
      private String stableMismatchText;
      private final int mismatchLimit;
      private int consecutiveMismatchCount;

      public void updateText() {
         this.stableMismatchText = null;
         this.consecutiveMismatchCount = 0;
      }

      public boolean canTextMax(String text, boolean stableMismatchFlag, boolean stableMismatchFlag2) {
         if (text != null && !text.isBlank() && !stableMismatchFlag2 && stableMismatchFlag) {
            if (!text.equals(this.stableMismatchText)) {
               this.stableMismatchText = text;
               this.consecutiveMismatchCount = 1;
            } else {
               this.consecutiveMismatchCount++;
            }

            return this.consecutiveMismatchCount >= this.mismatchLimit;
         } else {
            this.updateText();
            return false;
         }
      }

      public StableMismatch(int stableMismatchBound) {
         this.mismatchLimit = Math.max(2, stableMismatchBound);
      }

      public int getValue() {
         return this.consecutiveMismatchCount;
      }
   }
}
