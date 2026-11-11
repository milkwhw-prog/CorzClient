package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.function.LongUnaryOperator;

public final class ChangedInside {
   private int strategyChangeCount;
   private int regionNoneCount;
   private final Map<Long, ChangedInside.Record> noneRegionMap = new LinkedHashMap<>();
   private final Map<String, Integer> regionNoneIntegerMap = new HashMap<>();
   public static final int CHANGED_INSIDE_STEP = 1;
   private String regionNoneText = "";
   private int changedStrategyCount;
   private static final String[] insideBelowTexts = new String[]{"EAST", "WEST", "SOUTH", "NORTH", "ABOVE", "BELOW", "INSIDE"};

   private boolean canRegionNone(long regionNoneValue, String text) {
      this.noneRegionMap.remove(regionNoneValue);
      this.regionNoneText = text;
      this.regionNoneCount++;
      this.regionNoneIntegerMap.merge(text, 1, Integer::sum);
      return false;
   }

   public ChangedInside.Record getRegionNone(long regionNoneValue) {
      return this.noneRegionMap.get(regionNoneValue);
   }

   public int getRegionNoneCount() {
      return this.regionNoneCount;
   }

   public int getChangedStrategyCount() {
      return this.changedStrategyCount;
   }

   public void handleChangedStrategy(
      long changedStrategyValue, ChangedInside.Context context, long[] changedStrategyValues, long[] ls2, int n3, String changedStrategyText, int changedStrategyValue3, long longValue
   ) {
      ChangedInside.Record noneregionmap = this.noneRegionMap.get(changedStrategyValue);
      if (noneregionmap != null && noneregionmap.assetSetChanged.equals(context)) {
         noneregionmap.strategyChanged |= 1 << n3;
         noneregionmap.longValue = longValue;
         this.strategyChangeCount++;
      } else {
         this.noneRegionMap
            .put(changedStrategyValue, new ChangedInside.Record(changedStrategyValue, context, (long[])changedStrategyValues.clone(), (long[])ls2.clone(), 1 << n3, changedStrategyText, changedStrategyValue3, longValue));
         this.changedStrategyCount++;
      }
   }

   public void handleRegionConfirm(long regionConfirmValue) {
      if (this.noneRegionMap.containsKey(regionConfirmValue)) {
         this.canRegionNone(regionConfirmValue, "confirm_in_region");
      }
   }

   public Collection<ChangedInside.Record> getValues() {
      return this.noneRegionMap.values();
   }

   public Map<String, Integer> getMap() {
      return this.regionNoneIntegerMap;
   }

   public String getJoin() {
      if (this.noneRegionMap.isEmpty()) {
         return "none";
      } else {
         ArrayList arrayList = new ArrayList();

         for (ChangedInside.Record record2 : this.noneRegionMap.values()) {
            arrayList.add(record2.formatInsideBelow());
         }

         return String.join("; ", arrayList);
      }
   }

   public int getRegionNone() {
      return this.noneRegionMap.size();
   }

   public static boolean canNeedsAccess(ChangedInside.CandidateClass candidateClass) {
      return candidateClass == ChangedInside.CandidateClass.NEEDS_ACCESS;
   }

   public String getText() {
      return this.regionNoneText;
   }

   public int getStrategyChangeCount() {
      return this.strategyChangeCount;
   }

   public void updateRegionNone() {
      this.noneRegionMap.clear();
   }

   public static String formatInsideBelow(int index) {
      return index >= 0 && index < insideBelowTexts.length ? insideBelowTexts[index] : "?";
   }

   public boolean canChangedAsset(long changedAssetValue, ChangedInside.Context context, int changedAssetValue2, LongSupplier longSupplier, LongUnaryOperator longUnaryOperator) {
      ChangedInside.Record noneregionmap = this.noneRegionMap.get(changedAssetValue);
      if (noneregionmap == null) {
         return false;
      } else if (noneregionmap.assetSetChanged.componentKey() != context.componentKey() || noneregionmap.assetSetChanged.componentSize() != context.componentSize()) {
         return this.canRegionNone(changedAssetValue, "component_changed");
      } else if (noneregionmap.assetSetChanged.assetSetHash() != context.assetSetHash()) {
         return this.canRegionNone(changedAssetValue, "asset_set_changed");
      } else if (noneregionmap.assetSetChanged.strategy() != context.strategy()) {
         return this.canRegionNone(changedAssetValue, "strategy_changed");
      } else if ((noneregionmap.strategyChanged & 1 << changedAssetValue2) == 0) {
         return false;
      } else {
         long longValue = longSupplier.getAsLong();
         if (longValue != noneregionmap.longValue) {
            for (int i = 0; i < noneregionmap.clone.length; i++) {
               if (longUnaryOperator.applyAsLong(noneregionmap.clone[i]) != noneregionmap.clone2ChangedAsset[i]) {
                  return this.canRegionNone(changedAssetValue, "geometry_changed");
               }
            }

            noneregionmap.longValue = longValue;
         }

         return true;
      }
   }

   public static int getValue(int changedInsideValue, int changedInsideValue2, int changedInsideValue3, int changedInsideValue4, int changedInsideValue5, int changedInsideValue6) {
      int changedInsideLimit = changedInsideValue4 - changedInsideValue;
      int j = changedInsideValue5 - changedInsideValue2;
      int changedInsideLimit3 = changedInsideValue6 - changedInsideValue3;
      int abs4 = Math.abs(changedInsideLimit);
      int abs5 = Math.abs(j);
      int abs7 = Math.abs(changedInsideLimit3);
      if (abs4 <= 2 && abs5 <= 2 && abs7 <= 2) {
         return 6;
      } else if (abs5 > abs4 && abs5 > abs7) {
         return j > 0 ? 4 : 5;
      } else if (abs4 >= abs7) {
         return changedInsideLimit > 0 ? 0 : 1;
      } else {
         return changedInsideLimit3 > 0 ? 2 : 3;
      }
   }

   public static enum CandidateClass {
      READY_NOW,
      WALKABLE,
      NEEDS_ACCESS;
   }

   public record Context(long componentKey, int componentSize, long assetSetHash, int strategy) {
      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "component=%d size=%d assets=%s strategy=%d",
            this.componentKey,
            this.componentSize,
            Long.toUnsignedString(this.assetSetHash, 16),
            this.strategy
         );
      }
   }

   public static final class Record {
      public int strategyChanged;
      public final long[] clone;
      public final String reason;
      public long longValue;
      public final long[] clone2ChangedAsset;
      public final int recordedCount;
      public final ChangedInside.Context assetSetChanged;
      public final long region;

      Record(long region2, ChangedInside.Context assetSetChanged, long[] clone, long[] clone2, int strategyChanged, String reason2, int value, long longValue) {
         this.region = region2;
         this.assetSetChanged = assetSetChanged;
         this.clone = clone;
         this.clone2ChangedAsset = clone2;
         this.strategyChanged = strategyChanged;
         this.reason = reason2;
         this.recordedCount = value;
         this.longValue = longValue;
      }

      public String formatInsideBelow() {
         StringBuilder stringBuilder = new StringBuilder();

         for (int i = 0; i < ChangedInside.insideBelowTexts.length; i++) {
            if ((this.strategyChanged & 1 << i) != 0) {
               if (stringBuilder.length() > 0) {
                  stringBuilder.append('|');
               }

               stringBuilder.append(ChangedInside.insideBelowTexts[i]);
            }
         }

         return "region="
            + this.region
            + " targets="
            + this.clone.length
            + " sides="
            + stringBuilder
            + " reason="
            + this.reason
            + " ctx{"
            + this.assetSetChanged.getFormat()
            + "}";
      }
   }
}
