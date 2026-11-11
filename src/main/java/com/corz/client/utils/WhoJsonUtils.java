package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.corz.client.utils.HostJson;
import com.corz.client.utils.JsonWho;
import com.corz.client.utils.JsonMats;
import com.corz.client.utils.TaskStatus;
import com.corz.client.utils.JsonProperty;

public final class WhoJsonUtils {

   private static Map<String, Set<Long>> getWho(HostJson hostJson) {
      var hashMap = new HashMap<String, Set<Long>>();

      for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
         if (jsonWho.who != null && !jsonWho.isComplete()) {
            hashMap.computeIfAbsent(jsonWho.who, whoText -> new HashSet<>()).add(packPositionKey(jsonWho.whoValue, jsonWho.distHostValue, jsonWho.whoZ));
         }
      }

      return hashMap;
   }

   public static int getHostScore(HostJson hostJson) {
      return countUnassignedWho(hostJson, System.currentTimeMillis());
   }

   private static void recordWhoGeneration(JsonWho jsonWho, JsonMats jsonMats, long timeMillis) {
      boolean whoGenFlag = !jsonMats.whoText.equals(jsonWho.who);
      boolean whoGenFlag2 = !jsonMats.whoText.equals(jsonWho.whoGenText) && whoGenFlag;
      jsonWho.who = jsonMats.whoText;
      jsonWho.st = TaskStatus.ASSIGNED;
      jsonWho.am = timeMillis;
      jsonWho.timeMillis = timeMillis;
      if (whoGenFlag) {
         jsonWho.gen++;
      }

      if (whoGenFlag2) {
         jsonWho.reportedPlacedCount = 0;
      }
   }

   private static double computeMatsScore(JsonMats jsonMats, JsonWho jsonWho, boolean matsPropertyFlag) {
      if (matsPropertyFlag && jsonMats.mats != null && !jsonMats.mats.isEmpty() && !jsonWho.jsonProperty.isEmpty()) {
         String matsPropertyText = null;
         int matsPropertyLimit = 0;

         for (Entry entry : jsonWho.jsonProperty.getMap().entrySet()) {
            if ((Integer)entry.getValue() > matsPropertyLimit) {
               matsPropertyLimit = (Integer)entry.getValue();
               matsPropertyText = (String)entry.getKey();
            }
         }

         if (matsPropertyText != null && matsPropertyLimit > 0) {
            int matsPropertyLimit2 = jsonMats.mats.getValue(matsPropertyText);
            if (matsPropertyLimit2 >= matsPropertyLimit) {
               return 0.0;
            } else {
               double matsPropertyValue = (double)(matsPropertyLimit - matsPropertyLimit2) / matsPropertyLimit;
               return 200.0 * matsPropertyValue;
            }
         } else {
            return 0.0;
         }
      } else {
         return 0.0;
      }
   }

   private static int countWhoPlaced(HostJson hostJson, List<JsonMats> list, long whoPlacedValue, boolean whoPlacedFlag) {
      var placed = groupPlacedByColumn(hostJson);
      if (placed.isEmpty()) {
         return 0;
      } else {
         var hashMap = new HashMap();
         var hashMap2 = new HashMap();

         for (JsonMats jsonMats : list) {
            hashMap.put(jsonMats.whoText, new HashSet());
            hashMap2.put(jsonMats.whoText, 0);
         }

         for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
            if (jsonWho.isComplete() && jsonWho.who != null && hashMap.containsKey(jsonWho.who)) {
               ((Set)hashMap.get(jsonWho.who)).add(packPositionKey(jsonWho.whoValue, 0, jsonWho.whoZ));
            }
         }

         var arrayList = new ArrayList();
         int whoPlaced = 0;

         for (WhoJsonUtils.Column column : placed.values()) {
            var hashMap3 = new HashMap();

            for (JsonWho jsonWho2 : column.whoList) {
               if (jsonWho2.who != null && hashMap.containsKey(jsonWho2.who)) {
                  hashMap3.merge(jsonWho2.who, jsonWho2.placedValue, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               }
            }

            String whoPlacedText = null;
            int whoPlacedValue2 = 0;

            for (Entry entry : (Iterable<java.util.Map.Entry>) (hashMap3.entrySet())) {
               if ((Integer)entry.getValue() > whoPlacedValue2) {
                  whoPlacedValue2 = (Integer)entry.getValue();
                  whoPlacedText = (String)entry.getKey();
               }
            }

            if (whoPlacedText == null) {
               arrayList.add(column);
            } else {
               JsonMats jsonMats2 = null;

               for (JsonMats jsonMats3 : list) {
                  if (jsonMats3.whoText.equals(whoPlacedText)) {
                     jsonMats2 = jsonMats3;
                  }
               }

               for (JsonWho jsonWho3 : column.whoList) {
                  if (jsonWho3.isFlag()) {
                     recordWhoGeneration(jsonWho3, jsonMats2, whoPlacedValue);
                     whoPlaced++;
                  }
               }

               ((Set)hashMap.get(whoPlacedText)).add(column.slotTime);
               hashMap2.merge(whoPlacedText, column.placedTotal, (obj3, obj4) -> Integer.sum((Integer) obj3, (Integer) obj4));
            }
         }

         var arrayList2 = new ArrayList(list);

         while (!arrayList.isEmpty()) {
            arrayList2.sort(Comparator.<JsonMats>comparingInt(jsonMats4 -> (Integer)hashMap2.get(jsonMats4.whoText)).thenComparing(jsonMats5 -> jsonMats5.whoText));
            JsonMats jsonMats6 = null;
            WhoJsonUtils.Column c = null;
            JsonMats jsonMats7 = null;
            WhoJsonUtils.Column column3 = null;
            double whoPlacedLimit = Double.MAX_VALUE;

            for (JsonMats jsonMats8 : (Iterable<com.corz.client.utils.JsonMats>) (arrayList2)) {
               var set = (Set)hashMap.get(jsonMats8.whoText);
               boolean empty2 = !set.isEmpty();
               WhoJsonUtils.Column column4 = null;
               double maxValue = Double.MAX_VALUE;

               for (WhoJsonUtils.Column column5 : (Iterable<com.corz.client.utils.WhoJsonUtils.Column>) (arrayList)) {
                  if (!empty2 || isColumnInSet(column5, set)) {
                     double matsRole = computeMatsRoleScore(jsonMats8, column5, list, hashMap, whoPlacedFlag);
                     if (matsRole < maxValue) {
                        maxValue = matsRole;
                        column4 = column5;
                     }
                  }
               }

               if (column4 != null) {
                  if (maxValue < 1000.0) {
                     jsonMats6 = jsonMats8;
                     c = column4;
                     break;
                  }

                  if (maxValue < whoPlacedLimit) {
                     whoPlacedLimit = maxValue;
                     jsonMats7 = jsonMats8;
                     column3 = column4;
                  }
               }
            }

            if (jsonMats6 == null) {
               jsonMats6 = jsonMats7;
               c = column3;
            }

            if (jsonMats6 == null || c == null) {
               break;
            }

            for (JsonWho jsonWho4 : c.whoList) {
               if (jsonWho4.isFlag()) {
                  recordWhoGeneration(jsonWho4, jsonMats6, whoPlacedValue);
                  whoPlaced++;
               }
            }

            ((Set)hashMap.get(jsonMats6.whoText)).add(c.slotTime);
            hashMap2.merge(jsonMats6.whoText, c.placedTotal, (obj5, obj6) -> Integer.sum((Integer) obj5, (Integer) obj6));
            arrayList.remove(c);
         }

         return whoPlaced;
      }
   }

   private static double computeRoleScore(JsonMats jsonMats, JsonWho jsonWho, int roleValue, boolean roleFlag, Set<Long> set) {
      double roleValue2 = jsonMats.roleFlag ? jsonMats.asDouble : jsonWho.getAmountBoxMinX();
      double roleValue3 = jsonMats.roleFlag ? jsonMats.doubleValue : jsonWho.getAmountBoxMinY();
      double roleValue4 = jsonMats.roleFlag ? jsonMats.asDoubleRoleDoc : jsonWho.getAmount();
      double roleFactor = jsonWho.getAmountBoxMinX() - roleValue2;
      double roleFactor2 = jsonWho.getAmount() - roleValue4;
      double spotValue = jsonWho.getAmountBoxMinY() - roleValue3;
      double distance = Math.sqrt(roleFactor * roleFactor + roleFactor2 * roleFactor2);
      double abs6 = Math.abs(spotValue);
      double roleValue7 = distance + 2.0 * abs6;
      double roleValue8 = roleValue7 + roleValue * 40.0;
      double roleValue9 = roleValue8 + jsonWho.rt * 25.0;
      double roleFlag2 = roleValue9 + computeMatsScore(jsonMats, jsonWho, roleFlag);
      if (set != null && isWhoInSet(jsonWho, set)) {
         roleFlag2 -= 30.0;
      }

      return roleFlag2;
   }

   private static boolean isColumnInSet(WhoJsonUtils.Column column, Set<Long> set) {
      return set.contains(packPositionKey(column.columnX + 1, 0, column.columnZ))
         || set.contains(packPositionKey(column.columnX - 1, 0, column.columnZ))
         || set.contains(packPositionKey(column.columnX, 0, column.columnZ + 1))
         || set.contains(packPositionKey(column.columnX, 0, column.columnZ - 1));
   }

   public static String formatCoordinateKey(int whoJsonValue, int whoJsonValue2, int whoJsonValue3) {
      return "J" + whoJsonValue + "_" + whoJsonValue2 + "_" + whoJsonValue3;
   }

   private static boolean isWhoInSet(JsonWho jsonWho, Set<Long> set) {
      return set.isEmpty()
         ? false
         : set.contains(packPositionKey(jsonWho.whoValue + 1, jsonWho.distHostValue, jsonWho.whoZ))
            || set.contains(packPositionKey(jsonWho.whoValue - 1, jsonWho.distHostValue, jsonWho.whoZ))
            || set.contains(packPositionKey(jsonWho.whoValue, jsonWho.distHostValue + 1, jsonWho.whoZ))
            || set.contains(packPositionKey(jsonWho.whoValue, jsonWho.distHostValue - 1, jsonWho.whoZ))
            || set.contains(packPositionKey(jsonWho.whoValue, jsonWho.distHostValue, jsonWho.whoZ + 1))
            || set.contains(packPositionKey(jsonWho.whoValue, jsonWho.distHostValue, jsonWho.whoZ - 1));
   }

   private WhoJsonUtils() {
   }

   public static int countUnassignedWho(HostJson hostJson, long whoUnassignedValue) {
      int whoUnassigned = 0;

      for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
         if (jsonWho.who != null && !jsonWho.isComplete()) {
            JsonMats jsonMats = hostJson.getJsonMats(jsonWho.who);
            boolean whoUnassignedFlag = jsonMats == null
               || !jsonMats.multiBuilderFlag
               || !jsonMats.isProtocolMismatch()
               || !jsonMats.cb
               || !jsonMats.offlineStateFlag && (jsonMats.timeMillis == 0L || whoUnassignedValue - jsonMats.timeMillis >= 30000L);
            if (whoUnassignedFlag) {
               jsonWho.handleWhoGen(TaskStatus.UNASSIGNED);
               whoUnassigned++;
            }
         }
      }

      return whoUnassigned;
   }

   private static int computeHostTotal(HostJson hostJson, List<JsonMats> list, long whoJsonValue, boolean whoJsonFlag) {
      var hashMap = new HashMap();
      var who = getWho(hostJson);

      for (Entry entry : who.entrySet()) {
         hashMap.put((String)entry.getKey(), ((Set)entry.getValue()).size());
      }

      var arrayList = new ArrayList();

      for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
         if (jsonWho.isFlag()) {
            arrayList.add(jsonWho);
         }
      }

      int whoJsonCounter;
      for (whoJsonCounter = 0; !arrayList.isEmpty(); whoJsonCounter++) {
         JsonWho jsonWho2 = null;
         JsonMats jsonMats = null;
         double whoJsonLimit = Double.MAX_VALUE;

         for (JsonWho jsonWho3 : (Iterable<com.corz.client.utils.JsonWho>) (arrayList)) {
            for (JsonMats jsonMats2 : list) {
               double roleFlag = computeRoleScore(jsonMats2, jsonWho3, ((int) (Integer) (hashMap.getOrDefault(jsonMats2.whoText, 0))), whoJsonFlag, (Set<Long>)who.get(jsonMats2.whoText));
               if (roleFlag < whoJsonLimit) {
                  whoJsonLimit = roleFlag;
                  jsonWho2 = jsonWho3;
                  jsonMats = jsonMats2;
               }
            }
         }

         if (jsonWho2 == null) {
            break;
         }

         recordWhoGeneration(jsonWho2, jsonMats, whoJsonValue);
         hashMap.merge(jsonMats.whoText, 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
         who.computeIfAbsent(jsonMats.whoText, whoJsonText -> new HashSet<>()).add(packPositionKey(jsonWho2.whoValue, jsonWho2.distHostValue, jsonWho2.whoZ));
         arrayList.remove(jsonWho2);
      }

      return whoJsonCounter;
   }

   static int computeWhoTiming(HostJson hostJson, List<JsonMats> list, long whoValue, boolean whoFlag) {
      if (list.size() < 2) {
         return 0;
      } else {
         var placed = groupPlacedByColumn(hostJson);
         var hashSet = new HashSet();

         for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
            if (jsonWho.isComplete() && whoValue - jsonWho.timeMillis < 45000L) {
               hashSet.add(packPositionKey(jsonWho.whoValue, 0, jsonWho.whoZ));
            }
         }

         var hashMap = new HashMap();
         var hashMap2 = new HashMap();
         var hashMap3 = new HashMap();
         var hashMap4 = new HashMap();

         for (JsonMats jsonMats : list) {
            hashMap.put(jsonMats.whoText, new HashSet());
            hashMap2.put(jsonMats.whoText, new ArrayList());
            hashMap3.put(jsonMats.whoText, new HashSet());
            hashMap4.put(jsonMats.whoText, 0);
         }

         for (WhoJsonUtils.Column column : placed.values()) {
            String whoText = null;

            for (JsonWho jsonWho2 : column.whoList) {
               if (jsonWho2.who != null && hashMap.containsKey(jsonWho2.who)) {
                  whoText = jsonWho2.who;
                  break;
               }
            }

            if (whoText != null) {
               ((Set)hashMap.get(whoText)).add(column.slotTime);
               hashMap4.merge(whoText, column.whoList.size(), (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               if (column.isAtCapacity()) {
                  ((List)hashMap2.get(whoText)).add(column);
               } else {
                  ((Set)hashMap3.get(whoText)).add(column.slotTime);
               }
            }
         }

         var hashSet2 = new HashSet();

         for (JsonMats jsonMats2 : list) {
            if ((Integer)hashMap4.get(jsonMats2.whoText) == 0) {
               hashSet2.add(jsonMats2.whoText);
            }
         }

         int whoTime = 0;

         while (whoTime < 8) {
            JsonMats jsonMats3 = null;
            JsonMats jsonMats4 = null;

            for (JsonMats jsonMats5 : list) {
               if (jsonMats3 == null || (Integer)hashMap4.get(jsonMats5.whoText) < (Integer)hashMap4.get(jsonMats3.whoText)) {
                  jsonMats3 = jsonMats5;
               }

               if (jsonMats4 == null || sumColumnTotals((List<WhoJsonUtils.Column>)hashMap2.get(jsonMats5.whoText)) > sumColumnTotals((List<WhoJsonUtils.Column>)hashMap2.get(jsonMats4.whoText))
                  )
                {
                  jsonMats4 = jsonMats5;
               }
            }

            if (jsonMats3 == null || jsonMats4 == null || jsonMats3.whoText.equals(jsonMats4.whoText)) {
               break;
            }

            int deltaValue = sumColumnTotals((List<WhoJsonUtils.Column>)hashMap2.get(jsonMats4.whoText)) - sumColumnTotals((List<WhoJsonUtils.Column>)hashMap2.get(jsonMats3.whoText));
            boolean whoFlag2 = (Integer)hashMap4.get(jsonMats3.whoText) == 0;
            if (!whoFlag2 && deltaValue < 4 || ((List)hashMap2.get(jsonMats4.whoText)).isEmpty()) {
               break;
            }

            WhoJsonUtils.Column fullInventory = null;
            double whoLimit = Double.MAX_VALUE;

            for (WhoJsonUtils.Column column3 : (Iterable<com.corz.client.utils.WhoJsonUtils.Column>) ((List)hashMap2.get(jsonMats4.whoText))) {
               if (!isColumnInSet(column3, (Set<Long>)hashMap3.get(jsonMats4.whoText)) && (hashSet2.contains(jsonMats3.whoText) || !hashSet.contains(column3.slotTime))) {
                  double matsRole = computeMatsRoleScore(jsonMats3, column3, list, hashMap, whoFlag);
                  if (!(matsRole >= 1000.0)) {
                     double whoValue2 = matsRole - (isColumnInSet(column3, (Set<Long>)hashMap.get(jsonMats3.whoText)) ? 30.0 : 0.0);
                     if (whoValue2 < whoLimit) {
                        whoLimit = whoValue2;
                        fullInventory = column3;
                     }
                  }
               }
            }

            if (fullInventory == null) {
               break;
            }

            for (JsonWho jsonWho3 : fullInventory.whoList) {
               jsonWho3.whoGenText = jsonMats4.whoText;
               recordWhoGeneration(jsonWho3, jsonMats3, whoValue);
            }

            ((List)hashMap2.get(jsonMats4.whoText)).remove(fullInventory);
            ((List)hashMap2.get(jsonMats3.whoText)).add(fullInventory);
            ((Set)hashMap.get(jsonMats4.whoText)).remove(fullInventory.slotTime);
            ((Set)hashMap.get(jsonMats3.whoText)).add(fullInventory.slotTime);
            hashMap4.merge(jsonMats4.whoText, -fullInventory.whoList.size(), (obj3, obj4) -> Integer.sum((Integer) obj3, (Integer) obj4));
            hashMap4.merge(jsonMats3.whoText, fullInventory.whoList.size(), (obj5, obj6) -> Integer.sum((Integer) obj5, (Integer) obj6));
            whoTime += fullInventory.whoList.size();
         }

         return whoTime;
      }
   }

   private static int computeBandedTotal(HostJson hostJson, List<JsonMats> list, long whoJsonValue, WhoJsonUtils.Banding banding) {
      var arrayList = new ArrayList();

      for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
         if (jsonWho.isFlag()) {
            arrayList.add(jsonWho);
         }
      }

      if (arrayList.isEmpty()) {
         return 0;
      } else {
         arrayList.sort(
            banding == WhoJsonUtils.Banding.X
               ? Comparator.<JsonWho>comparingInt(jsonWho2 -> jsonWho2.whoValue).thenComparingInt(jsonWho3 -> jsonWho3.whoZ).thenComparing(jsonWho4 -> jsonWho4.id)
               : Comparator.<JsonWho>comparingInt(jsonWho5 -> jsonWho5.distHostValue).thenComparingInt(jsonWho6 -> jsonWho6.whoValue).thenComparing(jsonWho7 -> jsonWho7.id)
         );
         int nowmsSize = list.size();
         int varSize = arrayList.size();
         int whoJsonCounter = 0;

         for (int i = 0; i < varSize; i++) {
            int index = Math.min(nowmsSize - 1, (int)((long)i * nowmsSize / varSize));
            JsonMats nowm = (JsonMats)list.get(index);
            recordWhoGeneration((JsonWho)arrayList.get(i), nowm, whoJsonValue);
            whoJsonCounter++;
         }

         return whoJsonCounter;
      }
   }

   public static long getWhoTimestamp(JsonWho jsonWho, long whoJsonValue) {
      long maxTimeMillis = Math.max(jsonWho.am, jsonWho.timeMillis);
      return maxTimeMillis <= 0L ? Long.MAX_VALUE : whoJsonValue - maxTimeMillis;
   }

   private static int sumColumnTotals(List<WhoJsonUtils.Column> list) {
      int whoJsonTotal = 0;

      for (WhoJsonUtils.Column column : list) {
         whoJsonTotal += column.whoList.size();
      }

      return whoJsonTotal;
   }

   public static long packPositionKey(int whoJsonValue, int whoJsonValue2, int var11x) {
      return (long)(whoJsonValue & 2097151) << 42 | (long)(whoJsonValue2 & 2097151) << 21 | var11x & 2097151;
   }

   public static boolean canGenerateWho(JsonWho jsonWho, String who, long timeMillis, long whoGenLimit) {
      if (jsonWho != null && who != null && !jsonWho.isComplete() && !who.equals(jsonWho.who)) {
         if (jsonWho.who != null && getWhoTimestamp(jsonWho, timeMillis) < whoGenLimit) {
            return false;
         } else {
            jsonWho.whoGenText = jsonWho.who;
            jsonWho.who = who;
            jsonWho.st = TaskStatus.ASSIGNED;
            jsonWho.am = timeMillis;
            jsonWho.timeMillis = timeMillis;
            jsonWho.rt++;
            jsonWho.gen++;
            jsonWho.reportedPlacedCount = 0;
            return true;
         }
      } else {
         return false;
      }
   }

   public static int getHostDistance(HostJson hostJson, long distValue, boolean distFlag) {
      var offlineMulti = hostJson.getOfflineMulti();
      if (offlineMulti.isEmpty()) {
         return 0;
      } else {
         offlineMulti.sort(Comparator.comparing(jsonMats -> jsonMats.whoText));

         return switch (hostJson.dist) {
            case REGION -> computeBandedTotal(hostJson, offlineMulti, distValue, WhoJsonUtils.Banding.X);
            case LAYER -> computeBandedTotal(hostJson, offlineMulti, distValue, WhoJsonUtils.Banding.Y);
            case NEAREST -> computeHostTotal(hostJson, offlineMulti, distValue, distFlag);
            case DYNAMIC -> countWhoPlaced(hostJson, offlineMulti, distValue, distFlag) + computeWhoTiming(hostJson, offlineMulti, distValue, distFlag);
         };
      }
   }

   public static int countUnassignedWhoStrict(HostJson hostJson, long whoUnassignedValue) {
      if (hostJson.getOfflineMulti().size() < 2) {
         return 0;
      } else {
         var hashMap = new HashMap<String, List<JsonWho>>();

         for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
            if (jsonWho.who != null && !jsonWho.isComplete()) {
               hashMap.computeIfAbsent(jsonWho.who, whoUnassignedText -> new ArrayList<>()).add(jsonWho);
            }
         }

         var hashMap2 = new HashMap();

         for (Entry entry : hashMap.entrySet()) {
            long whoUnassignedBound = 0L;

            for (JsonWho jsonWho2 : (Iterable<com.corz.client.utils.JsonWho>) ((List)entry.getValue())) {
               whoUnassignedBound = Math.max(whoUnassignedBound, Math.max(jsonWho2.am, jsonWho2.timeMillis));
            }

            hashMap2.put((String)entry.getKey(), whoUnassignedBound);
         }

         int whoUnassignedCounter = 0;

         for (JsonMats jsonMats : hostJson.getOfflineMulti()) {
            Long long2 = (Long)hashMap2.get(jsonMats.whoText);
            if (long2 == null || long2 > 0L && whoUnassignedValue - long2 < 45000L) {
               whoUnassignedCounter++;
            }
         }

         int whoUnassigned = 0;

         for (Entry entryItem : hashMap.entrySet()) {
            long whoUnassignedLimit = (Long)hashMap2.get(entryItem.getKey());
            if (whoUnassignedLimit > 0L && whoUnassignedValue - whoUnassignedLimit >= 45000L && whoUnassignedCounter >= 1) {
               for (JsonWho jsonWho3 : (Iterable<com.corz.client.utils.JsonWho>) ((List)entryItem.getValue())) {
                  if (jsonWho3.reportedPlacedCount <= 0 && jsonWho3.rt < 2) {
                     jsonWho3.rt++;
                     jsonWho3.handleWhoGen(TaskStatus.UNASSIGNED);
                     whoUnassigned++;
                  }
               }
            }
         }

         return whoUnassigned;
      }
   }

   public static int combineCoordinates(int whoJsonValue, int whoJsonValue2, int landingSpotFactor) {
      return Math.floorDiv(whoJsonValue - whoJsonValue2, landingSpotFactor);
   }

   public static int combineBounds(int whoJsonBound, int aggroPreemptBound, int rocketsBuyBound) {
      int maxHeight = Math.max(whoJsonBound, Math.max(aggroPreemptBound, rocketsBuyBound));
      int round = Math.round(maxHeight / 8.0F);
      return Math.max(2, Math.min(16, round));
   }

   public static List<JsonWho> collectWhoInBounds(int whoJsonValue, int whoJsonValue2, int whoJsonValue3, int landingSpotFactor, WhoJsonUtils.CellStream cellStream) {
      var hashMap = new HashMap();
      cellStream.forEach((whoJsonValue5, originYx, originXx, mat) -> {
         int var9x = combineCoordinates(whoJsonValue5, whoJsonValue, landingSpotFactor);
         int combineCoordinates6 = combineCoordinates(originYx, whoJsonValue2, landingSpotFactor);
         int var11x = combineCoordinates(originXx, whoJsonValue3, landingSpotFactor);
         ((com.corz.client.utils.WhoJsonUtils.Acc) hashMap.computeIfAbsent(packPositionKey(var9x, combineCoordinates6, var11x), obj -> new WhoJsonUtils.Acc(var9x, combineCoordinates6, var11x))).addSample(whoJsonValue5, originYx, originXx, mat);
      });
      var arrayList = new ArrayList(hashMap.size());

      for (WhoJsonUtils.Acc acc : (Iterable<com.corz.client.utils.WhoJsonUtils.Acc>) (hashMap.values())) {
         JsonProperty jsonProperty = new JsonProperty();

         for (Entry entry : acc.textCounts.entrySet()) {
            jsonProperty.handle((String)entry.getKey(), (Integer)entry.getValue());
         }

         arrayList.add(
            new JsonWho(
               formatCoordinateKey(acc.accX, acc.accZ, acc.anchorValue),
               acc.accX,
               acc.accZ,
               acc.anchorValue,
               acc.minSample,
               acc.minAccY,
               acc.minBoundValue,
               acc.maxSample,
               acc.maxAccY,
               acc.maxBoundValue,
               acc.sampleCount,
               jsonProperty,
               acc.minAccY
            )
         );
      }

      arrayList.sort(Comparator.<JsonWho>comparingInt(jsonWho -> jsonWho.whoX).thenComparingInt(jsonWho2 -> jsonWho2.whoValue).thenComparingInt(jsonWho3 -> jsonWho3.whoZ).thenComparing(jsonWho4 -> jsonWho4.id));
      return arrayList;
   }

   private static double computeMatsRoleScore(JsonMats jsonMats, WhoJsonUtils.Column column, List<JsonMats> list, Map<String, Set<Long>> map, boolean matsRoleFlag) {
      double matsRole = 0.0;
      if (jsonMats.roleFlag) {
         double matsRoleFactor = column.computeSecondaryAmount() - jsonMats.asDouble;
         double matsRoleFactor2 = column.computePrimaryAmount() - jsonMats.asDoubleRoleDoc;
         matsRole += Math.sqrt(matsRoleFactor * matsRoleFactor + matsRoleFactor2 * matsRoleFactor2);
      }

      for (JsonWho jsonWho : column.whoList) {
         if (jsonWho.rt > 0 && jsonMats.whoText.equals(jsonWho.whoGenText)) {
            matsRole += 1000.0;
            break;
         }
      }

      if (matsRoleFlag && jsonMats.mats != null && !jsonMats.mats.isEmpty()) {
         var next = column.getMostCommonEntry();
         if (next != null && (Integer)next.getValue() > 0) {
            int matsRoleValue = jsonMats.mats.getValue((String)next.getKey());
            if (matsRoleValue <= 0) {
               boolean matsRoleFlag2 = false;

               for (JsonMats jsonMats2 : list) {
                  if (!jsonMats2.whoText.equals(jsonMats.whoText) && jsonMats2.mats != null && jsonMats2.mats.getValue((String)next.getKey()) > 0) {
                     matsRoleFlag2 = true;
                  }
               }

               matsRole += matsRoleFlag2 ? 5000.0 : 0.0;
            } else if (matsRoleValue < (Integer)next.getValue()) {
               matsRole += 200.0 * (next.getValue() - matsRoleValue) / ((Integer)next.getValue()).intValue();
            }
         }
      }

      return matsRole;
   }

   private static Map<Long, WhoJsonUtils.Column> groupPlacedByColumn(HostJson hostJson) {
      var linkedHashMap = new LinkedHashMap();

      for (JsonWho jsonWho : hostJson.getAllJsonWho()) {
         if (!jsonWho.isComplete()) {
            WhoJsonUtils.Column ifAbsent = ((com.corz.client.utils.WhoJsonUtils.Column) (linkedHashMap.computeIfAbsent(packPositionKey(jsonWho.whoValue, 0, jsonWho.whoZ), obj -> new WhoJsonUtils.Column(jsonWho.whoValue, jsonWho.whoZ))));
            ifAbsent.whoList.add(jsonWho);
            ifAbsent.placedTotal = ifAbsent.placedTotal + jsonWho.placedValue;
         }
      }

      return linkedHashMap;
   }

   private static final class Acc {
      final Map<String, Integer> textCounts;
      int maxSample;
      int minSample = Integer.MAX_VALUE;
      int maxBoundValue;
      int sampleCount;
      final int anchorValue;
      final int accZ;
      int minBoundValue;
      int minAccY = Integer.MAX_VALUE;
      final int accX;
      int maxAccY;

      Acc(int value, int value2, int value3) {
         this.minBoundValue = Integer.MAX_VALUE;
         this.maxSample = Integer.MIN_VALUE;
         this.maxAccY = Integer.MIN_VALUE;
         this.maxBoundValue = Integer.MIN_VALUE;
         this.textCounts = new HashMap<>();
         this.accX = value;
         this.accZ = value2;
         this.anchorValue = value3;
      }

      void addSample(int accBound, int originYx, int originXx, String accText) {
         this.minSample = Math.min(this.minSample, accBound);
         this.minAccY = Math.min(this.minAccY, originYx);
         this.minBoundValue = Math.min(this.minBoundValue, originXx);
         this.maxSample = Math.max(this.maxSample, accBound);
         this.maxAccY = Math.max(this.maxAccY, originYx);
         this.maxBoundValue = Math.max(this.maxBoundValue, originXx);
         this.sampleCount++;
         if (accText != null) {
            this.textCounts.merge(accText, 1, Integer::sum);
         }
      }
   }

   private static enum Banding {
      X,
      Y;
   }

   public interface CellConsumer {
      void accept(int acceptValue, int acceptValue2, int acceptValue3, String acceptText);
   }

   public interface CellStream {
      void forEach(WhoJsonUtils.CellConsumer cellConsumer);
   }

   private static final class Column {
      final long slotTime;
      int placedTotal;
      final int columnX;
      final List<JsonWho> whoList = new ArrayList<>();
      final int columnZ;

      Entry<String, Integer> getMostCommonEntry() {
         var hashMap = new HashMap();

         for (JsonWho jsonWho : this.whoList) {
            for (Entry entry : jsonWho.jsonProperty.getMap().entrySet()) {
               hashMap.merge((String)entry.getKey(), (Integer)entry.getValue(), (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
            }
         }

         Entry next = null;

         for (Entry entry2 : (Iterable<java.util.Map.Entry>) (hashMap.entrySet())) {
            if (next == null || (Integer)entry2.getValue() > (Integer)next.getValue()) {
               next = entry2;
            }
         }

         return next;
      }

      double computePrimaryAmount() {
         double columnTotal = 0.0;

         for (JsonWho jsonWho : this.whoList) {
            columnTotal += jsonWho.getAmount();
         }

         return columnTotal / this.whoList.size();
      }

      double computeSecondaryAmount() {
         double columnTotal = 0.0;

         for (JsonWho jsonWho : this.whoList) {
            columnTotal += jsonWho.getAmountBoxMinX();
         }

         return columnTotal / this.whoList.size();
      }

      Column(int value, int var11x) {
         this.columnX = value;
         this.columnZ = var11x;
         this.slotTime = WhoJsonUtils.packPositionKey(value, 0, var11x);
      }

      boolean isAtCapacity() {
         for (JsonWho jsonWho : this.whoList) {
            if (jsonWho.reportedPlacedCount > 0) {
               return false;
            }
         }

         return true;
      }
   }
}
