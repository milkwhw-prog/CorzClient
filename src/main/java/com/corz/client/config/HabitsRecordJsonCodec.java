package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.CrystalPop;
import com.corz.client.utils.HabitsRecordData;
import com.corz.client.utils.MaxMin;

public final class HabitsRecordJsonCodec {
   public static final int FORMAT_VERSION = 1;

   private static String joinContextArmKey(String jsonPearlText, String jsonPearlText2) {
      return jsonPearlText + '\n' + jsonPearlText2;
   }

   private static boolean isWithinByteLimit(String utfText) {
      return utfText != null && utfText.getBytes(StandardCharsets.UTF_8).length <= 16384;
   }

   private static MaxMin.State parsePearlHabits(JsonObject json) {
      MaxMin maxMin = new MaxMin();
      if (json == null) {
         return maxMin.getAmountJumpResetCounts();
      } else {
         maxMin.handlePearlTime(
            new MaxMin.State(
               readIntArray(asJsonArrayOrNull(json.get("pearlLaunched")), 2),
               readIntArray(asJsonArrayOrNull(json.get("pearlLow")), 2),
               readIntArray(asJsonArrayOrNull(json.get("pearlDir")), 4),
               readDoubleOrDefault(json.get("holeTime"), 0.0),
               readDoubleOrDefault(json.get("retotem"), 8.0),
               readDoubleOrDefault(json.get("boom"), 12.0),
               readIntArray(asJsonArrayOrNull(json.get("jumpReset")), 2)
            )
         );
         return maxMin.getAmountJumpResetCounts();
      }
   }

   private static Set<String> collectFirstArmKeys(List<HabitsRecordJsonCodec.ArmRef> list, int contextArmLimit) {
      HashSet hashSet = new HashSet();
      Iterator iterator = list.iterator();

      for (int i = 0; i < contextArmLimit && iterator.hasNext(); i++) {
         HabitsRecordJsonCodec.ArmRef armRef = (HabitsRecordJsonCodec.ArmRef)iterator.next();
         hashSet.add(joinContextArmKey(armRef.context(), armRef.arm()));
      }

      return hashSet;
   }

   private static String sanitizePlayerName(String lowerCaseText) {
      if (lowerCaseText == null) {
         return "";
      } else {
         String lowerCase = lowerCaseText.toLowerCase(Locale.ROOT);
         if (lowerCase.length() > 16) {
            return "";
         } else {
            for (int i = 0; i < lowerCase.length(); i++) {
               char charAt = lowerCase.charAt(i);
               if ((charAt < 'a' || charAt > 'z') && (charAt < '0' || charAt > '9') && charAt != '_') {
                  return "";
               }
            }

            return lowerCase;
         }
      }
   }

   private static double clampArmValueForKey(double jsonPearlValue, CrystalPop.Family family, String jsonPearlText) {
      return AmountAmounts.getAmount(jsonPearlValue, -1.0, 1.0, CrystalPop.getPopCrystal(family, jsonPearlText));
   }

   private static double[] readDoubleArray(JsonArray jsonArray, int jsonPearlSize, double jsonPearlValue) {
      double[] doubles = new double[jsonPearlSize];

      for (int i = 0; i < jsonPearlSize; i++) {
         doubles[i] = jsonArray != null && i < jsonArray.size() ? readDoubleOrDefault(jsonArray.get(i), jsonPearlValue) : jsonPearlValue;
      }

      return doubles;
   }

   private static JsonArray asJsonArrayOrNull(JsonElement json) {
      return json != null && json.isJsonArray() ? json.getAsJsonArray() : null;
   }

   private static String sanitizeUuid(String stringText) {
      if (stringText != null && !stringText.isEmpty() && stringText.length() <= 36) {
         try {
            return UUID.fromString(stringText).toString();
         } catch (IllegalArgumentException e) {
            return "";
         }
      } else {
         return "";
      }
   }

   private static Map<String, double[]> sanitizeArmEntries(String jsonPearlText, Map<String, double[]> map) {
      if (jsonPearlText != null && map != null) {
         int indexOf = jsonPearlText.indexOf(124);
         if (indexOf < 0) {
            return null;
         } else {
            CrystalPop.Family finisherOpenerType = CrystalPop.getFinisherOpenerType(jsonPearlText.substring(0, indexOf));
            if (finisherOpenerType != null && CrystalPop.canFlag(jsonPearlText.substring(indexOf + 1))) {
               List anchorPop = CrystalPop.getAnchorPop(finisherOpenerType);
               LinkedHashMap linkedHashMap = new LinkedHashMap();

               for (Entry entry : map.entrySet()) {
                  String up = (String)entry.getKey();
                  double[] jsonPearlValues = (double[])entry.getValue();
                  if (up != null && jsonPearlValues != null && jsonPearlValues.length >= 2 && anchorPop.contains(up) && !linkedHashMap.containsKey(up)) {
                     if (linkedHashMap.size() >= 8) {
                        break;
                     }

                     linkedHashMap.put(up, new double[]{clampNonNegativeAmount(jsonPearlValues[0]), clampArmValueForKey(jsonPearlValues[1], finisherOpenerType, up)});
                  }
               }

               return linkedHashMap.isEmpty() ? null : linkedHashMap;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static String serializeHabitsRecord(HabitsRecordData habitsRecordData, Set<String> set) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("v", 1);
      jsonObject.addProperty("name", habitsRecordData.uuid());
      jsonObject.addProperty("uuid", habitsRecordData.name());
      jsonObject.addProperty("updated", habitsRecordData.updated());
      jsonObject.addProperty("fights", habitsRecordData.v());
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.add("along", toJsonArray(habitsRecordData.cal().lateral()));
      jsonObject2.add("lateral", toJsonArray(habitsRecordData.cal().along()));
      jsonObject2.addProperty("speed", habitsRecordData.cal().air());
      jsonObject2.addProperty("melee", habitsRecordData.cal().expl());
      jsonObject2.addProperty("expl", habitsRecordData.cal().melee());
      jsonObject2.addProperty("air", habitsRecordData.cal().speed());
      jsonObject2.addProperty("n", habitsRecordData.cal().n());
      jsonObject.add("cal", jsonObject2);
      JsonObject jsonObject3 = new JsonObject();
      jsonObject3.add("pearlLaunched", getJsonArray(habitsRecordData.habits().jumpReset()));
      jsonObject3.add("pearlLow", getJsonArray(habitsRecordData.habits().pearlDir()));
      jsonObject3.add("pearlDir", getJsonArray(habitsRecordData.habits().pearlLow()));
      jsonObject3.addProperty("holeTime", habitsRecordData.habits().retotem());
      jsonObject3.addProperty("retotem", habitsRecordData.habits().boom());
      jsonObject3.addProperty("boom", habitsRecordData.habits().holeTime());
      jsonObject3.add("jumpReset", getJsonArray(habitsRecordData.habits().pearlLaunched()));
      jsonObject.add("habits", jsonObject3);
      JsonObject jsonObject4 = new JsonObject();

      for (Entry entry : habitsRecordData.arms().entrySet()) {
         JsonObject jsonObject5 = new JsonObject();

         for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((Map)entry.getValue()).entrySet())) {
            if (!set.contains(joinContextArmKey((String)entry.getKey(), (String)entry2.getKey()))) {
               JsonArray jsonArray = new JsonArray();
               jsonArray.add(new JsonPrimitive(((double[])entry2.getValue())[0]));
               jsonArray.add(new JsonPrimitive(((double[])entry2.getValue())[1]));
               jsonObject5.add((String)entry2.getKey(), jsonArray);
            }
         }

         if (jsonObject5.size() > 0) {
            jsonObject4.add((String)entry.getKey(), jsonObject5);
         }
      }

      jsonObject.add("arms", jsonObject4);
      jsonObject.add("stats", getJsonArray(habitsRecordData.stats()));
      return jsonObject.toString();
   }

   private static Map<String, Map<String, double[]>> parseArmsMap(JsonObject json) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      if (json == null) {
         return linkedHashMap;
      } else {
         LinkedHashMap linkedHashMap2 = new LinkedHashMap();

         for (Entry entry : json.entrySet()) {
            JsonObject jsonObjectOrNull = asJsonObjectOrNull((JsonElement)entry.getValue());
            if (jsonObjectOrNull != null) {
               LinkedHashMap linkedHashMap3 = new LinkedHashMap();

               for (Entry entryItem : jsonObjectOrNull.entrySet()) {
                  JsonArray jsonArray = asJsonArrayOrNull((JsonElement)entryItem.getValue());
                  if (jsonArray != null && jsonArray.size() >= 2) {
                     linkedHashMap3.put((String)entryItem.getKey(), new double[]{readDoubleOrDefault(jsonArray.get(0), 0.0), readDoubleOrDefault(jsonArray.get(1), Double.NaN)});
                  }
               }

               Map map = sanitizeArmEntries((String)entry.getKey(), linkedHashMap3);
               if (map != null) {
                  CrystalPop.Family finisherOpenerType = CrystalPop.getFinisherOpenerType(((String)entry.getKey()).substring(0, ((String)entry.getKey()).indexOf(124)));
                  List ifAbsent = ((java.util.List) (linkedHashMap2.computeIfAbsent(finisherOpenerType, obj -> new ArrayList())));
                  if (linkedHashMap.remove(entry.getKey()) != null) {
                     ifAbsent.remove(entry.getKey());
                  }

                  linkedHashMap.put((String)entry.getKey(), map);
                  ifAbsent.add((String)entry.getKey());

                  while (ifAbsent.size() > 16) {
                     linkedHashMap.remove(ifAbsent.remove(0));
                  }
               }
            }
         }

         return linkedHashMap;
      }
   }

   private static double readDoubleOrDefault(JsonElement json, double primitiveJson) {
      if (json != null && json.isJsonPrimitive()) {
         JsonPrimitive asJsonPrimitive = json.getAsJsonPrimitive();
         if (!asJsonPrimitive.isNumber()) {
            return primitiveJson;
         } else {
            try {
               return asJsonPrimitive.getAsDouble();
            } catch (RuntimeException e2) {
               return primitiveJson;
            }
         }
      } else {
         return primitiveJson;
      }
   }

   private static double clampNonNegativeAmount(double jsonPearlValue) {
      return Double.isNaN(jsonPearlValue) ? 0.0 : AmountAmounts.getAmount(jsonPearlValue, 0.0, 1000000.0);
   }

   private static int[] readIntArray(JsonArray jsonArray, int jsonPearlSize) {
      int[] ints = new int[jsonPearlSize];

      for (int i = 0; i < jsonPearlSize; i++) {
         ints[i] = jsonArray != null && i < jsonArray.size() ? toClampedCount(readDoubleOrDefault(jsonArray.get(i), 0.0)) : 0;
      }

      return ints;
   }

   public static HabitsRecordData parseHabitsRecord(String habitsFightsText) {
      if (habitsFightsText != null && habitsFightsText.length() <= 16384) {
         JsonObject jsonObject;
         try {
            JsonElement jsonElement = JsonParser.parseString(habitsFightsText);
            if (jsonElement == null || !jsonElement.isJsonObject()) {
               return null;
            }

            jsonObject = jsonElement.getAsJsonObject();
         } catch (StackOverflowError | RuntimeException e) {
            return null;
         }

         try {
            if (readDoubleOrDefault(jsonObject.get("v"), Double.NaN) != 1.0) {
               return null;
            } else {
               String lowerCase = sanitizePlayerName(asStringOrNull(jsonObject.get("name")));
               String sanitizeUuid2 = sanitizeUuid(asStringOrNull(jsonObject.get("uuid")));
               long habitsFightsValue = (long)AmountAmounts.getAmount(readDoubleOrDefault(jsonObject.get("updated"), 0.0), 0.0, 2.53402300799999E14, 0.0);
               int clampedCount = toClampedCount(readDoubleOrDefault(jsonObject.get("fights"), 0.0));
               AmountAmounts.State alongExpl = parseCalibration(asJsonObjectOrNull(jsonObject.get("cal")));
               MaxMin.State pearlRetotem = parsePearlHabits(asJsonObjectOrNull(jsonObject.get("habits")));
               Map map = parseArmsMap(asJsonObjectOrNull(jsonObject.get("arms")));
               int[] habitsFightsValues = FightStatTotals.fromStatsArray(readIntArray(asJsonArrayOrNull(jsonObject.get("stats")), 6)).toStatsArray();
               return new HabitsRecordData(1, lowerCase, sanitizeUuid2, habitsFightsValue, clampedCount, alongExpl, pearlRetotem, map, habitsFightsValues);
            }
         } catch (RuntimeException e2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String asStringOrNull(JsonElement json) {
      return json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() ? json.getAsString() : null;
   }

   private static int toClampedCount(double jsonPearlValue) {
      return Double.isNaN(jsonPearlValue) ? 0 : (int)Math.round(AmountAmounts.getAmount(jsonPearlValue, 0.0, 1000000.0));
   }

   private HabitsRecordJsonCodec() {
   }

   private static JsonArray toJsonArray(double[] jsonArrayValues) {
      JsonArray jsonArray = new JsonArray();

      for (double jsonArrayValue : jsonArrayValues) {
         jsonArray.add(new JsonPrimitive(jsonArrayValue));
      }

      return jsonArray;
   }

   static HabitsRecordData sanitizeHabitsRecord(HabitsRecordData habitsRecordData) {
      AmountAmounts amountAmounts = new AmountAmounts();
      amountAmounts.handleAmountAmounts(habitsRecordData.cal());
      MaxMin maxMin = new MaxMin();
      maxMin.handlePearlTime(habitsRecordData.habits());
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      if (habitsRecordData.arms() != null) {
         for (Entry entry : habitsRecordData.arms().entrySet()) {
            Map map = sanitizeArmEntries((String)entry.getKey(), (Map<String, double[]>)entry.getValue());
            if (map != null) {
               linkedHashMap.put((String)entry.getKey(), map);
            }
         }
      }

      long armsHabitsValue = Math.max(0L, Math.min(253402300799999L, habitsRecordData.updated()));
      return new HabitsRecordData(
         1,
         sanitizePlayerName(habitsRecordData.uuid()),
         sanitizeUuid(habitsRecordData.name()),
         armsHabitsValue,
         FightStatTotals.clampCount(habitsRecordData.v()),
         amountAmounts.getAmountAmounts(),
         maxMin.getAmountJumpResetCounts(),
         linkedHashMap,
         FightStatTotals.fromStatsArray(habitsRecordData.stats()).toStatsArray()
      );
   }

   public static String serializeHabitsRecordWithinLimit(HabitsRecordData habitsRecordData) {
      if (habitsRecordData == null) {
         return null;
      } else {
         HabitsRecordData armsHabits = sanitizeHabitsRecord(habitsRecordData);
         String utfText = serializeHabitsRecord(armsHabits, Set.of());
         if (isWithinByteLimit(utfText)) {
            return utfText;
         } else {
            ArrayList arrayList = new ArrayList();
            int armsCounter = 0;

            for (Entry entry : armsHabits.arms().entrySet()) {
               for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((Map)entry.getValue()).entrySet())) {
                  arrayList.add(new HabitsRecordJsonCodec.ArmRef((String)entry.getKey(), (String)entry2.getKey(), ((double[])entry2.getValue())[0], armsCounter++));
               }
            }

            arrayList.sort(Comparator.comparingDouble(HabitsRecordJsonCodec.ArmRef::n).thenComparingInt(HabitsRecordJsonCodec.ArmRef::order));
            int armsValue = 1;
            int varSize = arrayList.size();
            String serializeHabitsRecord3 = serializeHabitsRecord(armsHabits, collectFirstArmKeys(arrayList, varSize));

            while (armsValue < varSize) {
               int contextArmLimit = armsValue + varSize >>> 1;
               String serializeHabitsRecord4 = serializeHabitsRecord(armsHabits, collectFirstArmKeys(arrayList, contextArmLimit));
               if (isWithinByteLimit(serializeHabitsRecord4)) {
                  varSize = contextArmLimit;
                  serializeHabitsRecord3 = serializeHabitsRecord4;
               } else {
                  armsValue = contextArmLimit + 1;
               }
            }

            if (!isWithinByteLimit(serializeHabitsRecord3)) {
               serializeHabitsRecord3 = serializeHabitsRecord(armsHabits, collectFirstArmKeys(arrayList, armsValue));
            }

            return isWithinByteLimit(serializeHabitsRecord3) ? serializeHabitsRecord3 : null;
         }
      }
   }

   private static JsonArray getJsonArray(int[] jsonArrayValues) {
      JsonArray jsonArray = new JsonArray();

      for (int jsonArrayValue : jsonArrayValues) {
         jsonArray.add(new JsonPrimitive(jsonArrayValue));
      }

      return jsonArray;
   }

   private static AmountAmounts.State parseCalibration(JsonObject json) {
      AmountAmounts amountAmounts = new AmountAmounts();
      if (json == null) {
         return amountAmounts.getAmountAmounts();
      } else {
         double[] alongExplValues = readDoubleArray(asJsonArrayOrNull(json.get("along")), 3, 0.0);
         double[] doubleArray = readDoubleArray(asJsonArrayOrNull(json.get("lateral")), 3, 0.0);
         amountAmounts.handleAmountAmounts(
            new AmountAmounts.State(
               alongExplValues,
               doubleArray,
               readDoubleOrDefault(json.get("speed"), 1.0),
               readDoubleOrDefault(json.get("melee"), 1.0),
               readDoubleOrDefault(json.get("expl"), 1.0),
               readDoubleOrDefault(json.get("air"), 0.0),
               toClampedCount(readDoubleOrDefault(json.get("n"), 0.0))
            )
         );
         return amountAmounts.getAmountAmounts();
      }
   }

   private static JsonObject asJsonObjectOrNull(JsonElement json) {
      return json != null && json.isJsonObject() ? json.getAsJsonObject() : null;
   }

   private record ArmRef(String context, String arm, double n, int order) {
   }
}
