package com.corz.client.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import com.corz.client.utils.AmountAmounts;

public final class CrystalPop {
   public static final List<String> OPENER_POP_ANCHOR_ITEMS = List.of("KB_HIT_CRYSTAL", "KB_POP_BUILD", "PUNCH_POP", "CRYSTAL_LAUNCH", "ANCHOR_BELOW");
   public static final List<String> ANCHOR_GROUND_ITEMS = List.of("AIR", "ANCHOR", "GROUND");
   private final EnumMap<CrystalPop.Family, LinkedHashMap<String, LinkedHashMap<String, double[]>>> enumMap = new EnumMap<>(CrystalPop.Family.class);
   public static final List<String> POP_ANCHOR_ITEMS = List.of("2.3", "2.6", "2.9");
   private final Random random;

   public void handleMeanEnum(Map<String, Map<String, CrystalPop.Arm>> map) {
      this.updateEnum();
      if (map != null) {
         for (Entry entry : map.entrySet()) {
            String meanEnumText = (String)entry.getKey();
            if (meanEnumText != null && entry.getValue() != null) {
               int indexOf = meanEnumText.indexOf(124);
               if (indexOf >= 0) {
                  CrystalPop.Family finisherOpenerType = getFinisherOpenerType(meanEnumText.substring(0, indexOf));
                  String substring = meanEnumText.substring(indexOf + 1);
                  if (finisherOpenerType != null && canFlag(substring)) {
                     List anchorPop = getAnchorPop(finisherOpenerType);
                     LinkedHashMap linkedHashMap = new LinkedHashMap();

                     for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((Map)entry.getValue()).entrySet())) {
                        String up = (String)entry2.getKey();
                        CrystalPop.Arm arm = (CrystalPop.Arm)entry2.getValue();
                        if (up != null && arm != null && anchorPop.contains(up) && linkedHashMap.size() < 8) {
                           double meanEnumValue = Double.isNaN(arm.n()) ? 0.0 : AmountAmounts.getAmount(arm.n(), 0.0, 1000000.0);
                           double meanEnumValue2 = AmountAmounts.getAmount(arm.mean(), -1.0, 1.0, getPopCrystal(finisherOpenerType, up));
                           linkedHashMap.put(up, new double[]{meanEnumValue, meanEnumValue2});
                        }
                     }

                     if (!linkedHashMap.isEmpty()) {
                        LinkedHashMap enummap = this.enumMap.get(finisherOpenerType);
                        enummap.remove(substring);
                        enummap.put(substring, linkedHashMap);
                        handleForLinkedHashMap(enummap);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean canFlag(char crystalPopLimit) {
      return crystalPopLimit >= 'a' && crystalPopLimit <= 'z' || crystalPopLimit >= 'A' && crystalPopLimit <= 'Z' || crystalPopLimit >= '0' && crystalPopLimit <= '9' || crystalPopLimit == '_' || crystalPopLimit == '.' || crystalPopLimit == '|' || crystalPopLimit == '-';
   }

   private static double getAmount(Map<String, Double> map, String crystalPopText) {
      Double double2 = map == null ? null : (Double)map.get(crystalPopText);
      if (double2 == null) {
         return 0.0;
      } else if (Double.isNaN(double2)) {
         return Double.NEGATIVE_INFINITY;
      } else {
         return double2 == Double.POSITIVE_INFINITY ? Double.MAX_VALUE : double2;
      }
   }

   public void updateEnum() {
      for (LinkedHashMap linkedHashMap : this.enumMap.values()) {
         linkedHashMap.clear();
      }
   }

   private static double getAmount(Map<String, double[]> map) {
      if (map == null) {
         return 0.0;
      } else {
         double crystalPopTotal = 0.0;

         for (double[] crystalPopValues : map.values()) {
            crystalPopTotal += crystalPopValues[0];
         }

         return crystalPopTotal;
      }
   }

   public static List<String> getAnchorPop(CrystalPop.Family family) {
      if (family == null) {
         return List.of();
      } else {
         return switch (family) {
            case OPENER -> OPENER_POP_ANCHOR_ITEMS;
            case FINISHER -> ANCHOR_GROUND_ITEMS;
            case SPACING -> POP_ANCHOR_ITEMS;
         };
      }
   }

   public CrystalPop(Random random) {
      this.random = random == null ? new Random() : random;

      for (CrystalPop.Family family : CrystalPop.Family.values()) {
         this.enumMap.put(family, new LinkedHashMap<>(32, 0.75F, true));
      }
   }

   public static String formatText(String crystalPopText) {
      if (crystalPopText == null) {
         return "";
      } else {
         int crystalPopLimit = Math.min(crystalPopText.length(), 48);
         StringBuilder stringBuilder = new StringBuilder(crystalPopLimit);

         for (int i = 0; i < crystalPopLimit; i++) {
            char charAt = crystalPopText.charAt(i);
            stringBuilder.append(canFlag(charAt) ? charAt : '_');
         }

         return stringBuilder.toString();
      }
   }

   public Map<String, Map<String, CrystalPop.Arm>> getEnum() {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (CrystalPop.Family family : CrystalPop.Family.values()) {
         for (Entry entry : this.enumMap.get(family).entrySet()) {
            LinkedHashMap finish = new LinkedHashMap();

            for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((LinkedHashMap)entry.getValue()).entrySet())) {
               finish.put((String)entry2.getKey(), new CrystalPop.Arm(((double[])entry2.getValue())[0], ((double[])entry2.getValue())[1]));
            }

            linkedHashMap.put(family.name() + "|" + (String)entry.getKey(), finish);
         }
      }

      return linkedHashMap;
   }

   public static boolean canFlag(String crystalPopText) {
      if (crystalPopText != null && crystalPopText.length() <= 48) {
         for (int i = 0; i < crystalPopText.length(); i++) {
            if (!canFlag(crystalPopText.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static CrystalPop.Family getFinisherOpenerType(String finisherOpenerText) {
      if (finisherOpenerText == null) {
         return null;
      } else {
         for (CrystalPop.Family finisherOpenerType : CrystalPop.Family.values()) {
            if (finisherOpenerType.name().equals(finisherOpenerText)) {
               return finisherOpenerType;
            }
         }

         return null;
      }
   }

   private static void handle(LinkedHashMap<String, double[]> linkedHashMap) {
      String crystalPopText = null;
      double crystalPopLimit = Double.POSITIVE_INFINITY;

      for (Entry entry : linkedHashMap.entrySet()) {
         if (((double[])entry.getValue())[0] < crystalPopLimit) {
            crystalPopLimit = ((double[])entry.getValue())[0];
            crystalPopText = (String)entry.getKey();
         }
      }

      if (crystalPopText == null) {
         linkedHashMap.clear();
      } else {
         linkedHashMap.remove(crystalPopText);
      }
   }

   private static void handleForLinkedHashMap(LinkedHashMap<String, LinkedHashMap<String, double[]>> linkedHashMap) {
      Iterator iterator = linkedHashMap.keySet().iterator();

      while (linkedHashMap.size() > 16 && iterator.hasNext()) {
         iterator.next();
         iterator.remove();
      }
   }

   public static double getPopCrystal(CrystalPop.Family family, String crystalText) {
      if (family == CrystalPop.Family.OPENER && crystalText != null) {
         return switch (crystalText) {
            case "KB_HIT_CRYSTAL" -> 0.3;
            case "ANCHOR_BELOW", "PUNCH_POP" -> 0.25;
            case "CRYSTAL_LAUNCH" -> 0.2;
            case "KB_POP_BUILD" -> 0.15;
            default -> 0.0;
         };
      } else {
         return family == CrystalPop.Family.SPACING && "2.6".equals(crystalText) ? 0.1 : 0.0;
      }
   }

   public CrystalPop.Arm getEnum(CrystalPop.Family family, String enumText, String crystalText) {
      if (family == null) {
         return new CrystalPop.Arm(0.0, 0.0);
      } else {
         LinkedHashMap get = this.enumMap.get(family).get(formatText(enumText));
         double[] enumValues = get == null ? null : (double[])get.get(crystalText);
         return enumValues == null ? new CrystalPop.Arm(0.0, getPopCrystal(family, crystalText)) : new CrystalPop.Arm(enumValues[0], enumValues[1]);
      }
   }

   private LinkedHashMap<String, double[]> getEnum(CrystalPop.Family family, String enumText) {
      LinkedHashMap enummap = this.enumMap.get(family);
      LinkedHashMap linkedHashMap = (LinkedHashMap)enummap.get(enumText);
      if (linkedHashMap == null) {
         linkedHashMap = new LinkedHashMap();
         enummap.put(enumText, linkedHashMap);
         handleForLinkedHashMap(enummap);
      }

      return linkedHashMap;
   }

   public void handle(CrystalPop.Family family, String crystalPopText, String crystalText, double crystalPopValue) {
      if (family != null && crystalText != null && getAnchorPop(family).contains(crystalText)) {
         double crystalPopValue2 = Double.isNaN(crystalPopValue) ? 0.0 : AmountAmounts.getAmount(crystalPopValue, -1.0, 1.0);
         LinkedHashMap linkedHashMap = this.getEnum(family, formatText(crystalPopText));

         for (double[] crystalPopValues : (Iterable<double[]>) (linkedHashMap.values())) {
            crystalPopValues[0] *= 0.97;
         }

         double[] finish = (double[])linkedHashMap.get(crystalText);
         if (finish == null) {
            while (linkedHashMap.size() >= 8) {
               handle(linkedHashMap);
            }

            finish = new double[]{0.0, getPopCrystal(family, crystalText)};
            linkedHashMap.put(crystalText, finish);
         }

         finish[0] = Math.min(1000000.0, finish[0] + 1.0);
         finish[1] = AmountAmounts.getAmount(finish[1] + (crystalPopValue2 - finish[1]) / (finish[0] + 2.0), -1.0, 1.0);
      }
   }

   public String formatRandomEnum(CrystalPop.Family family, String randomEnumText, List<String> list, Map<String, Double> map, boolean randomEnumFlag) {
      if (family != null && list != null && !list.isEmpty()) {
         List anchorPop = getAnchorPop(family);
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();

         for (String crystalPopText : list) {
            if (crystalPopText != null && anchorPop.contains(crystalPopText) && !arrayList.contains(crystalPopText)) {
               arrayList.add(crystalPopText);
               arrayList2.add(getAmount(map, crystalPopText));
            }
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            double randomEnumBound = Double.NEGATIVE_INFINITY;

            for (double apiPutBound : (Iterable<Double>) (arrayList2)) {
               randomEnumBound = Math.max(randomEnumBound, apiPutBound);
            }

            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();

            for (int i = 0; i < arrayList.size(); i++) {
               double randomEnumLimit = (Double)arrayList2.get(i);
               boolean randomEnumFlag2 = randomEnumBound > 0.0 ? randomEnumLimit >= 0.85 * randomEnumBound : randomEnumLimit >= randomEnumBound - 0.5;
               if (randomEnumFlag2) {
                  arrayList3.add((String)arrayList.get(i));
                  arrayList4.add(randomEnumLimit);
               }
            }

            if (arrayList3.size() == 1) {
               return (String)arrayList3.get(0);
            } else {
               LinkedHashMap get = this.enumMap.get(family).get(formatText(randomEnumText));
               double randomEnumValue = getAmount(get);
               if (randomEnumFlag) {
                  double max2 = Math.max(0.05, 3.0 / (20.0 + randomEnumValue));
                  if (this.random.nextDouble() < max2) {
                     return (String)arrayList3.get(this.random.nextInt(arrayList3.size()));
                  }
               }

               double log = Math.log(1.0 + randomEnumValue);
               String formatRandomEnum = null;
               double negativeInfinity = Double.NEGATIVE_INFINITY;
               double negativeInfinity3 = Double.NEGATIVE_INFINITY;

               for (int j = 0; j < arrayList3.size(); j++) {
                  String lowerCase = (String)arrayList3.get(j);
                  double[] randomEnumValues = get == null ? null : (double[])get.get(lowerCase);
                  double randomEnumValue4 = randomEnumValues == null ? 0.0 : randomEnumValues[0];
                  double limit = randomEnumValues == null ? getPopCrystal(family, lowerCase) : randomEnumValues[1];
                  double randomEnumTotal = AmountAmounts.getAmount(limit, -0.8, 1.0);
                  if (randomEnumFlag) {
                     randomEnumTotal += 0.4 * Math.sqrt(log / (randomEnumValue4 + 2.0));
                  }

                  double randomEnumLimit3 = (Double)arrayList4.get(j);
                  if (formatRandomEnum == null || randomEnumTotal > negativeInfinity || randomEnumTotal == negativeInfinity && randomEnumLimit3 > negativeInfinity3) {
                     formatRandomEnum = lowerCase;
                     negativeInfinity = randomEnumTotal;
                     negativeInfinity3 = randomEnumLimit3;
                  }
               }

               return formatRandomEnum;
            }
         }
      } else {
         return null;
      }
   }

   public record Arm(double n, double mean) {
   }

   public static enum Family {
      OPENER,
      FINISHER,
      SPACING;
   }
}
