package com.corz.client.utils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongPredicate;

public final class ChangedCapability {
   private int capabilityUpdateCount;
   private int capabilityChangedCount;
   private ChangedCapability.Reopen changedCapabilityType;
   private final EnumMap<ChangedCapability.Reopen, Integer> enumMap;
   private int changedCapabilityCount;
   private final HashMap<Integer, ChangedCapability.Capability> capAllowedCapabilityMap;
   private int columnChangedCount;
   private final LinkedHashMap<Integer, LinkedHashMap<Long, ChangedCapability.Proof>> columnChangedProofMap = new LinkedHashMap<>();
   private int capabilityChangedValue;
   private int entryFailuresRecordedCount;
   private int entryCapabilitySuppressionCount;
   private final HashSet<Integer> topologySurfaceIntegerSet;

   private void handle(int changedCapabilityValue, long immutablePosValue, ChangedCapability.Reopen reopen) {
      Map map = this.columnChangedProofMap.get(changedCapabilityValue);
      if (map != null) {
         map.remove(immutablePosValue);
      }

      this.handleCapabilityChanged(reopen);
   }

   public int getCapabilityChangedCount() {
      return this.capabilityChangedCount;
   }

   public ChangedCapability.Reopen getChangedCapabilityType() {
      return this.changedCapabilityType;
   }

   public void handle(int changedCapabilityValue, ChangedCapability.Capability capability) {
      this.capAllowedCapabilityMap.put(changedCapabilityValue, capability);
      this.entryCapabilitySuppressionCount++;
   }

   public int getValue(int changedCapabilityValue) {
      Map map = this.columnChangedProofMap.get(changedCapabilityValue);
      return map == null ? 0 : map.size();
   }

   public void markStrategyChanged(int changedCapabilityValue) {
      this.columnChangedProofMap.remove(changedCapabilityValue);
      this.capAllowedCapabilityMap.remove(changedCapabilityValue);
      this.topologySurfaceIntegerSet.remove(changedCapabilityValue);
   }

   public void handleTopologySurface(int topologySurfaceValue) {
      if (this.topologySurfaceIntegerSet.remove(topologySurfaceValue)) {
         this.handleCapabilityChanged(ChangedCapability.Reopen.SURFACE_TOPOLOGY_CHANGED);
      }
   }

   public int getChangedTotal() {
      int changedCapabilityTotal = 0;

      for (Map map : this.columnChangedProofMap.values()) {
         changedCapabilityTotal += map.size();
      }

      return changedCapabilityTotal;
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "entryFailuresRecorded=%d entryActivationsSuppressed=%d entryFailureMemoryHits=%d entryProofRevalidations=%d live=%d entryZeroCandidateSuppressions=%d entryCapabilitySuppressions=%d entryReachabilityReopens=%d entryReopenByReason=%s",
         this.entryFailuresRecordedCount,
         this.capabilityUpdateCount,
         this.capabilityChangedCount,
         this.columnChangedCount,
         this.getChangedTotal(),
         this.changedCapabilityCount,
         this.entryCapabilitySuppressionCount,
         this.getOrDefault(ChangedCapability.Reopen.ENTRY_BASE_REACHABLE),
         this.getAppend()
      );
   }

   public ChangedCapability() {
      this.capAllowedCapabilityMap = new HashMap<>();
      this.topologySurfaceIntegerSet = new HashSet<>();
      this.enumMap = new EnumMap<>(ChangedCapability.Reopen.class);
   }

   public ChangedCapability.Proof getProofFor(int changedCapabilityValue, long botCrystalText) {
      Map map = this.columnChangedProofMap.get(changedCapabilityValue);
      return map == null ? null : (ChangedCapability.Proof)map.get(botCrystalText);
   }

   public int getUpdateCount() {
      return this.entryFailuresRecordedCount;
   }

   public int getEntryFailureCount() {
      return this.changedCapabilityCount;
   }

   public ChangedCapability.Proof getValidProofFor(int changedCapabilityValue, long columnReaderValue, ChangedCapability.Context context) {
      ChangedCapability.Proof proof = this.getProofFor(changedCapabilityValue, columnReaderValue);
      if (proof == null) {
         return null;
      } else {
         ChangedCapability.Reopen columnChanged = this.getColumnChanged(changedCapabilityValue, columnReaderValue, proof, context, true);
         if (columnChanged == null) {
            this.capabilityChangedCount++;
            return this.getProofFor(changedCapabilityValue, columnReaderValue);
         } else {
            this.handle(changedCapabilityValue, columnReaderValue, columnChanged);
            return null;
         }
      }
   }

   public int getSuppressionCount() {
      return this.columnChangedCount;
   }

   public int getColumnChangeCount() {
      return this.capabilityUpdateCount;
   }

   public void handleInstance(
      int changedCapabilityValue, long formatGroundAir, ChangedCapability.FailureClass failureClass, long changedCapabilityValue3, long proofsCommittedValue, ChangedCapability.Column column, int changedCapabilityValue5, int changedCapabilityBound
   ) {
      LinkedHashMap linkedHashMap = this.columnChangedProofMap.get(changedCapabilityValue);
      if (linkedHashMap == null) {
         linkedHashMap = new LinkedHashMap();
         this.columnChangedProofMap.put(changedCapabilityValue, linkedHashMap);

         while (this.columnChangedProofMap.size() > 8) {
            Iterator iterator = this.columnChangedProofMap.keySet().iterator();
            iterator.next();
            iterator.remove();
         }
      }

      if (linkedHashMap.put(
            formatGroundAir, new ChangedCapability.Proof(failureClass, changedCapabilityValue3, proofsCommittedValue, column == null ? ChangedCapability.Column.INSTANCE : column, changedCapabilityValue5, Math.max(0, changedCapabilityBound))
         )
         == null) {
         this.entryFailuresRecordedCount++;
      }

      while (linkedHashMap.size() > 4096) {
         Iterator iterator2 = linkedHashMap.keySet().iterator();
         iterator2.next();
         iterator2.remove();
      }
   }

   public int getOrDefault(ChangedCapability.Reopen reopen) {
      return this.enumMap.getOrDefault(reopen, 0);
   }

   public void handleCapabilityChanged(ChangedCapability.Reopen changedCapabilityType) {
      this.enumMap.merge(changedCapabilityType, 1, Integer::sum);
      this.changedCapabilityType = changedCapabilityType;
   }

   public void resetState() {
      this.capabilityUpdateCount++;
   }

   public ChangedCapability.Capability getCapAllowedData(int capAllowedValue) {
      return this.capAllowedCapabilityMap.get(capAllowedValue);
   }

   private boolean canSectionColumn(ChangedCapability.Proof proof, ChangedCapability.Context context) {
      if (proof.why() == ChangedCapability.FailureClass.COLUMN_INTERSECTS_SCHEMATIC) {
         return false;
      } else {
         ChangedCapability.Column column = proof.column();

         for (int i = 0; i < column.sectionKeys().length; i++) {
            if (context.clock().contentHash(column.sectionKeys()[i]) != column.sectionHashes()[i]) {
               return true;
            }
         }

         return false;
      }
   }

   public void removeStrategy(int changedCapabilityValue) {
      this.topologySurfaceIntegerSet.add(changedCapabilityValue);
      this.changedCapabilityCount++;
   }

   public boolean isContains(int containsValue) {
      return this.topologySurfaceIntegerSet.contains(containsValue);
   }

   public void handleStrategyChanged(int strategyChangedValue, ChangedCapability.Capability capability) {
      ChangedCapability.Capability capability2 = this.capAllowedCapabilityMap.remove(strategyChangedValue);
      if (capability2 != null) {
         this.handleCapabilityChanged(
            capability2.strategyAllowed() != capability.strategyAllowed()
               ? ChangedCapability.Reopen.STRATEGY_CHANGED
               : ChangedCapability.Reopen.MATERIAL_CAPABILITY_CHANGED
         );
      }
   }

   private ChangedCapability.Reopen getColumnChanged(int columnChangedValue, long columnReaderValue, ChangedCapability.Proof proof, ChangedCapability.Context context, boolean columnChangedFlag) {
      switch (proof.why()) {
         case COLUMN_INTERSECTS_SCHEMATIC:
            return null;
         case NO_USABLE_GROUND:
         case BASE_ABOVE_DECK:
         case COLUMN_UNSAFE:
         default:
            break;
         case BASE_NOT_REACHABLE:
            if (context.component().test(proof.base())) {
               return ChangedCapability.Reopen.ENTRY_BASE_REACHABLE;
            }
            break;
         case ROUTE_REFUSED:
         case WALK_ON_REFUSED:
            if (context.componentExact()
               ? !context.component().test(proof.originFeet())
               : Math.abs(ProofSupportUtils.getValue(proof.originFeet()) - context.feetY()) >= 2) {
               return ChangedCapability.Reopen.PLAYER_COMPONENT_CHANGED;
            }
      }

      if (proof.validUntil() > 0 && context.tick() >= proof.validUntil()) {
         return ChangedCapability.Reopen.ACCESS_ASSET_CHANGED;
      } else if (!this.canSectionColumn(proof, context)) {
         return null;
      } else if (!columnChangedFlag) {
         return ChangedCapability.Reopen.OTHER;
      } else {
         ChangedCapability.Column read = context.columnNow().read(columnReaderValue, proof.base());
         this.columnChangedCount++;
         if (read == null) {
            return ChangedCapability.Reopen.OTHER;
         } else if (read.foreignHash() != proof.column().foreignHash()) {
            return ChangedCapability.Reopen.ENTRY_GEOMETRY_CHANGED;
         } else if (read.ownHash() != proof.column().ownHash()) {
            return ChangedCapability.Reopen.ACCESS_ASSET_CHANGED;
         } else {
            long[] longs = new long[proof.column().sectionKeys().length];

            for (int i = 0; i < longs.length; i++) {
               longs[i] = context.clock().contentHash(proof.column().sectionKeys()[i]);
            }

            this.columnChangedProofMap
               .get(columnChangedValue)
               .put(columnReaderValue, new ChangedCapability.Proof(proof.why(), proof.base(), proof.originFeet(), proof.column().getHashForeign(longs), proof.tick(), proof.validUntil()));
            return null;
         }
      }
   }

   public void handle(int changedCapabilityValue, long changedCapabilityValue2, ChangedCapability.FailureClass failureClass, long formatGroundAir, long proofsCommittedValue, ChangedCapability.Column column, int changedCapabilityValue5) {
      this.handleInstance(changedCapabilityValue, changedCapabilityValue2, failureClass, formatGroundAir, proofsCommittedValue, column, changedCapabilityValue5, 0);
   }

   public String getAppend() {
      StringBuilder stringBuilder = new StringBuilder("{");

      for (ChangedCapability.Reopen reopen : ChangedCapability.Reopen.values()) {
         if (stringBuilder.length() > 1) {
            stringBuilder.append(", ");
         }

         stringBuilder.append(reopen).append('=').append(this.enumMap.getOrDefault(reopen, 0));
      }

      return stringBuilder.append('}').toString();
   }

   public static long getTime(long changedCapabilityValue) {
      changedCapabilityValue ^= changedCapabilityValue >>> 33;
      changedCapabilityValue *= -49064778989728563L;
      changedCapabilityValue ^= changedCapabilityValue >>> 33;
      changedCapabilityValue *= -4265267296055464877L;
      return changedCapabilityValue ^ changedCapabilityValue >>> 33;
   }

   public int getEnum() {
      int enumTotal = 0;

      for (int enumValue : this.enumMap.values()) {
         enumTotal += enumValue;
      }

      return enumTotal;
   }

   public void updateCapabilityChanged() {
      this.columnChangedProofMap.clear();
      this.capAllowedCapabilityMap.clear();
      this.topologySurfaceIntegerSet.clear();
      this.enumMap.clear();
      this.entryFailuresRecordedCount = this.capabilityChangedCount = this.columnChangedCount = this.capabilityUpdateCount = this.changedCapabilityCount = this.entryCapabilitySuppressionCount = this.capabilityChangedValue = 0;
      this.changedCapabilityType = null;
   }

   public List<Long> getItems(int changedCapabilityValue, List<Long> list, ChangedCapability.Context context, int changedCapabilityValue2) {
      ArrayList arrayList = new ArrayList();
      Map map = this.columnChangedProofMap.get(changedCapabilityValue);
      if (map != null && !list.isEmpty()) {
         int deckySize = list.size();
         int changedCapabilityValue3 = this.capabilityChangedValue % deckySize;
         int changedCapabilityCounter = 0;

         for (int i = 0; i < deckySize; i++) {
            int value = (changedCapabilityValue3 + i) % deckySize;
            long decky = (Long)list.get(value);
            ChangedCapability.Proof proof = (ChangedCapability.Proof)map.get(decky);
            if (proof != null) {
               if (this.canSectionColumn(proof, context)) {
                  if (changedCapabilityCounter >= changedCapabilityValue2) {
                     this.capabilityChangedValue = value;
                     return arrayList;
                  }

                  changedCapabilityCounter++;
               }

               ChangedCapability.Reopen columnChanged = this.getColumnChanged(changedCapabilityValue, decky, proof, context, true);
               if (columnChanged != null) {
                  this.handle(changedCapabilityValue, decky, columnChanged);
                  arrayList.add(decky);
               }
            }
         }

         this.capabilityChangedValue = 0;
         return arrayList;
      } else {
         return arrayList;
      }
   }

   public record Capability(boolean strategyAllowed, boolean tempMaterial, boolean tempCapBlocked) {
      public boolean isAllowedCap() {
         return this.strategyAllowed && this.tempMaterial && !this.tempCapBlocked;
      }

      public String formatAllowedCap() {
         return "strategyAllowed=" + this.strategyAllowed + " tempMaterial=" + this.tempMaterial + " tempCapBlocked=" + this.tempCapBlocked;
      }
   }

   public record Column(long foreignHash, long ownHash, long[] sectionKeys, long[] sectionHashes) {
      public static final ChangedCapability.Column INSTANCE = new ChangedCapability.Column(0L, 0L, new long[0], new long[0]);

      private static int getValue(long[] a, long v) {
         for (int i = 0; i < a.length; i++) {
            if (a[i] == v) {
               return i;
            }
         }

         return -1;
      }

      public Column(long foreignHash, long ownHash, long[] sectionKeys, long[] sectionHashes) {
         sectionKeys = sectionKeys == null ? new long[0] : sectionKeys;
         sectionHashes = sectionHashes != null && sectionHashes.length == sectionKeys.length ? sectionHashes : new long[sectionKeys.length];
         this.foreignHash = foreignHash;
         this.ownHash = ownHash;
         this.sectionKeys = sectionKeys;
         this.sectionHashes = sectionHashes;
      }

      public ChangedCapability.Column getSectionHash(long[] keys, ChangedCapability.SectionClock clock) {
         if (keys != null && keys.length != 0) {
            LinkedHashSet linkedHashSet = new LinkedHashSet();

            for (long sectionHashValue : this.sectionKeys) {
               linkedHashSet.add(sectionHashValue);
            }

            for (long blockPos : keys) {
               linkedHashSet.add(blockPos);
            }

            long[] longs = new long[linkedHashSet.size()];
            long[] longs2 = new long[linkedHashSet.size()];
            int index = 0;

            for (long contentHashValue : (Iterable<Long>) (linkedHashSet)) {
               longs[index] = contentHashValue;
               int index2 = getValue(this.sectionKeys, contentHashValue);
               longs2[index] = index2 >= 0 ? this.sectionHashes[index2] : clock.contentHash(contentHashValue);
               index++;
            }

            return new ChangedCapability.Column(this.foreignHash, this.ownHash, longs, longs2);
         } else {
            return this;
         }
      }

      public ChangedCapability.Column getHashForeign(long[] hashes) {
         return new ChangedCapability.Column(this.foreignHash, this.ownHash, this.sectionKeys, hashes);
      }
   }

   @FunctionalInterface
   public interface ColumnReader {
      ChangedCapability.Column read(long columnReaderValue, long columnReaderValue2);
   }

   public record Context(
      LongPredicate component, boolean componentExact, int feetY, int tick, ChangedCapability.ColumnReader columnNow, ChangedCapability.SectionClock clock
   ) {
   }

   public static enum FailureClass {
      COLUMN_INTERSECTS_SCHEMATIC,
      NO_USABLE_GROUND,
      BASE_ABOVE_DECK,
      COLUMN_UNSAFE,
      BASE_NOT_REACHABLE,
      ROUTE_REFUSED,
      WALK_ON_REFUSED;
   }

   public record Proof(ChangedCapability.FailureClass why, long base, long originFeet, ChangedCapability.Column column, int tick, int validUntil) {
      public Proof(ChangedCapability.FailureClass column, long why, long base, ChangedCapability.Column originFeet, int tick) {
         this(column, why, base, originFeet, tick, 0);
      }
   }

   public static enum Reopen {
      ENTRY_BASE_REACHABLE,
      ENTRY_GEOMETRY_CHANGED,
      ACCESS_ASSET_CHANGED,
      STRATEGY_CHANGED,
      MATERIAL_CAPABILITY_CHANGED,
      PLAYER_COMPONENT_CHANGED,
      SURFACE_TOPOLOGY_CHANGED,
      OTHER;
   }

   @FunctionalInterface
   public interface SectionClock {
      long contentHash(long contentHashValue);
   }
}
