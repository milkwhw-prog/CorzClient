package com.corz.client.utils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.corz.client.utils.ChangedInside;

public final class FailureFingerprint {
   private final LinkedHashMap<FailureFingerprint.Context, Integer> failureFingerprintIntegerMap = new LinkedHashMap<>();
   private FailureFingerprint.Context fingerprintFailureData;

   public int getFailureEntryCount() {
      return this.failureFingerprintIntegerMap.size();
   }

   public Map<FailureFingerprint.Context, Integer> getFailureCountMap() {
      return this.failureFingerprintIntegerMap;
   }

   public FailureFingerprint.Context getFingerprintFailureData() {
      return this.fingerprintFailureData;
   }

   public static FailureFingerprint.Variable getStrategyAsset(FailureFingerprint.Context context, FailureFingerprint.Context context2) {
      if (context == null) {
         return FailureFingerprint.Variable.FIRST;
      } else if (context.targetRegion() != context2.targetRegion()) {
         return FailureFingerprint.Variable.TARGET_REGION;
      } else if (context.entrySide() != context2.entrySide()) {
         return FailureFingerprint.Variable.ENTRY_SIDE;
      } else if (context.accessAssetSet() != context2.accessAssetSet()) {
         return FailureFingerprint.Variable.ACCESS_ASSET;
      } else if (context.strategy() != context2.strategy()) {
         return FailureFingerprint.Variable.STRATEGY;
      } else if (context.playerComponent() != context2.playerComponent()) {
         return FailureFingerprint.Variable.COMPONENT;
      } else {
         return context.failureFingerprint() != context2.failureFingerprint() ? FailureFingerprint.Variable.GEOMETRY : FailureFingerprint.Variable.NONE;
      }
   }

   public void updateFailureFingerprint() {
      this.failureFingerprintIntegerMap.clear();
      this.fingerprintFailureData = null;
   }

   public FailureFingerprint.Proof getStrategyAsset(List<FailureFingerprint.Option> list, boolean strategyAssetFlag) {
      HashSet hashSet = new HashSet();
      HashSet hashSet2 = new HashSet();
      HashSet hashSet3 = new HashSet();
      HashSet hashSet4 = new HashSet();
      HashSet hashSet5 = new HashSet();

      for (FailureFingerprint.Context context : this.failureFingerprintIntegerMap.keySet()) {
         hashSet.add(context.targetRegion());
         hashSet4.add(context.entrySide());
         hashSet5.add(context.strategy());
         hashSet2.add(context.accessAssetSet());
         hashSet3.add(context.playerComponent());
      }

      return new FailureFingerprint.Proof(
         this.failureFingerprintIntegerMap.size(),
         hashSet.size(),
         hashSet4.size(),
         hashSet5.size(),
         hashSet2.size(),
         hashSet3.size(),
         list.size(),
         this.getCtxFailure(list) != null,
         strategyAssetFlag
      );
   }

   public FailureFingerprint.Choice getCtxFailure(List<FailureFingerprint.Option> list) {
      for (FailureFingerprint.Option option : list) {
         if (!this.failureFingerprintIntegerMap.containsKey(option.ctx())) {
            FailureFingerprint.Variable strategyAsset = getStrategyAsset(this.fingerprintFailureData, option.ctx());
            if (strategyAsset != FailureFingerprint.Variable.NONE) {
               return new FailureFingerprint.Choice(option, strategyAsset);
            }
         }
      }

      return null;
   }

   public void recordFailureCount(FailureFingerprint.Context fingerprintFailureData, int failureFingerprintValue) {
      this.failureFingerprintIntegerMap.put(fingerprintFailureData, failureFingerprintValue);
      this.fingerprintFailureData = fingerprintFailureData;
   }

   public boolean hasContext(FailureFingerprint.Context context) {
      return this.failureFingerprintIntegerMap.containsKey(context);
   }

   public record Choice(FailureFingerprint.Option option, FailureFingerprint.Variable changed) {
   }

   public record Context(long targetRegion, long playerComponent, long accessAssetSet, int entrySide, int strategy, long failureFingerprint) {
      public String formatContext() {
         return String.format(
            Locale.ROOT,
            "{targetRegion=%d playerComponent=%d accessAssetSet=%s entrySide=%s strategy=%d failureFingerprint=%s}",
            this.targetRegion,
            this.playerComponent,
            Long.toUnsignedString(this.accessAssetSet, 16),
            ChangedInside.formatInsideBelow(this.entrySide),
            this.strategy,
            Long.toUnsignedString(this.failureFingerprint, 16)
         );
      }
   }

   public record Option(long target, FailureFingerprint.Context ctx) {
   }

   public record Proof(
      int contextsTried,
      int regionsTried,
      int sidesTried,
      int strategiesTried,
      int assetSetsTried,
      int componentsTried,
      int optionsNow,
      boolean untriedOptionExists,
      boolean variableRemains
   ) {
      public boolean isVariableRemains() {
         return !this.untriedOptionExists && !this.variableRemains;
      }

      public String formatTriedSets() {
         return "contextsTried="
            + this.contextsTried
            + " regions="
            + this.regionsTried
            + " entrySides="
            + this.sidesTried
            + " strategies="
            + this.strategiesTried
            + " assetSets="
            + this.assetSetsTried
            + " components="
            + this.componentsTried
            + " optionsNow="
            + this.optionsNow
            + " untriedOption="
            + this.untriedOptionExists
            + " variableRemains="
            + this.variableRemains;
      }
   }

   public static enum Variable {
      FIRST,
      TARGET_REGION,
      ENTRY_SIDE,
      ACCESS_ASSET,
      STRATEGY,
      COMPONENT,
      GEOMETRY,
      NONE;
   }
}
