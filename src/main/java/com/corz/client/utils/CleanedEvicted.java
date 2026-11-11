package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CleanedEvicted {
   private long nextSequenceId;
   private int evicted;
   private int cleanedWhileNeeded;
   private int cleanedCells;
   private final LinkedHashMap<Long, CleanedEvicted.Entry> reclaimableCleanedEntryMap = new LinkedHashMap<>();
   private final HashMap<Long, Long> cleanedNeededPositionMap = new HashMap<>();

   public List<CleanedEvicted.Network> getItems() {
      ArrayList arrayList = new ArrayList(this.reclaimableCleanedEntryMap.size());

      for (CleanedEvicted.Entry entry : this.reclaimableCleanedEntryMap.values()) {
         arrayList.add(entry.getNetwork());
      }

      return arrayList;
   }

   public int getCleanedWhileNeeded() {
      return this.cleanedWhileNeeded;
   }

   public void handleCleanedNeeded(long cleanedNeededValue) {
      Long long2 = this.cleanedNeededPositionMap.remove(cleanedNeededValue);
      if (long2 != null) {
         this.cleanedCells++;
         CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(long2);
         if (entry != null) {
            if (entry.isReclaimableCleaned()) {
               this.cleanedWhileNeeded++;
            }

            entry.linkedHashSet2.remove(cleanedNeededValue);
            if (entry.linkedHashSet2.isEmpty()) {
               entry.cleanedReclaimableType = CleanedEvicted.State.CLEANED;
            }
         }
      }
   }

   public CleanedEvicted() {
      this.nextSequenceId = 1L;
   }

   public int getReclaimableCleaned(CleanedEvicted.State state) {
      int reclaimableCleaned = 0;

      for (CleanedEvicted.Entry entry : this.reclaimableCleanedEntryMap.values()) {
         if (entry.cleanedReclaimableType == state) {
            reclaimableCleaned++;
         }
      }

      return reclaimableCleaned;
   }

   public long getOrDefault(long orDefaultValue) {
      return this.cleanedNeededPositionMap.getOrDefault(orDefaultValue, 0L);
   }

   public void handleReclaimableCleaned(long reclaimableCleanedValue, long cleanedEvictedValue) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(reclaimableCleanedValue);
      if (entry != null && entry.cleanedReclaimableType == CleanedEvicted.State.RETURN_PATH) {
         if (!entry.linkedHashSet2.contains(cleanedEvictedValue) && !entry.linkedHashSet2.contains(offsetPositionKeyDown(cleanedEvictedValue))) {
            entry.cleanedReclaimableType = CleanedEvicted.State.RECLAIMABLE;
         }
      }
   }

   public void handleCleanedReclaimable(long cleanedReclaimableValue, long time) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(cleanedReclaimableValue);
      if (entry != null && entry.cleanedReclaimableType != CleanedEvicted.State.CLEANED) {
         entry.cleanedReclaimableType = CleanedEvicted.State.ACTIVE;
         entry.time = time;
      }
   }

   public long[] getReclaimableCleanedArray(CleanedEvicted.State state) {
      int reclaimableCleanedCounter = 0;

      for (CleanedEvicted.Entry entry : this.reclaimableCleanedEntryMap.values()) {
         if (entry.cleanedReclaimableType == state) {
            reclaimableCleanedCounter++;
         }
      }

      long[] longs = new long[reclaimableCleanedCounter];
      int reclaimableCleanedCounter2 = 0;

      for (CleanedEvicted.Entry entry2 : this.reclaimableCleanedEntryMap.values()) {
         if (entry2.cleanedReclaimableType == state) {
            longs[reclaimableCleanedCounter2++] = entry2.time2;
         }
      }

      return longs;
   }

   public void updateCleanedNeeded() {
      this.reclaimableCleanedEntryMap.clear();
      this.cleanedNeededPositionMap.clear();
      this.nextSequenceId = 1L;
      this.cleanedWhileNeeded = 0;
      this.cleanedCells = 0;
      this.evicted = 0;
   }

   public String formatCleanedNeeded() {
      return "networks="
         + this.reclaimableCleanedEntryMap.size()
         + " active="
         + this.getReclaimableCleaned(CleanedEvicted.State.ACTIVE)
         + " return="
         + this.getReclaimableCleaned(CleanedEvicted.State.RETURN_PATH)
         + " reclaimable="
         + this.getReclaimableCleaned(CleanedEvicted.State.RECLAIMABLE)
         + " cleaned="
         + this.getReclaimableCleaned(CleanedEvicted.State.CLEANED)
         + " retained="
         + this.getCleanedReclaimable()
         + " reclaimCells="
         + this.getReclaimablePositions().size()
         + " cleanedCells="
         + this.cleanedCells
         + " cleanedWhileNeeded="
         + this.cleanedWhileNeeded
         + " evicted="
         + this.evicted;
   }

   public int getEvicted() {
      return this.evicted;
   }

   public void markEntryReturnPath(long reclaimableCleanedValue) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(reclaimableCleanedValue);
      if (entry != null && entry.cleanedReclaimableType == CleanedEvicted.State.ACTIVE) {
         entry.cleanedReclaimableType = CleanedEvicted.State.RETURN_PATH;
      }
   }

   public CleanedEvicted.Network getNetworkForEntry(long unmodifiableValue) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(unmodifiableValue);
      return entry == null ? null : entry.getNetwork();
   }

   public int getEntryCount() {
      return this.reclaimableCleanedEntryMap.size();
   }

   public Set<Long> getUnassignedPositions(Collection<Long> collection) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      if (collection == null) {
         return linkedHashSet;
      } else {
         for (Long long2 : collection) {
            if (long2 != null && !this.cleanedNeededPositionMap.containsKey(long2)) {
               linkedHashSet.add(long2);
            }
         }

         return linkedHashSet;
      }
   }

   public Set<Long> getReclaimablePositions() {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (CleanedEvicted.Entry entry : this.reclaimableCleanedEntryMap.values()) {
         if (entry.cleanedReclaimableType == CleanedEvicted.State.RECLAIMABLE) {
            linkedHashSet.addAll(entry.linkedHashSet2);
         }
      }

      return Collections.unmodifiableSet(linkedHashSet);
   }

   public int getCleanedCells() {
      return this.cleanedCells;
   }

   public void handleCleanedLinked(long cleanedLinkedValue, long formatGroundAir) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(cleanedLinkedValue);
      if (entry != null && entry.cleanedReclaimableType != CleanedEvicted.State.CLEANED) {
         Long put = this.cleanedNeededPositionMap.put(formatGroundAir, cleanedLinkedValue);
         if (put != null && put != cleanedLinkedValue) {
            CleanedEvicted.Entry entry2 = this.reclaimableCleanedEntryMap.get(put);
            if (entry2 != null) {
               entry2.linkedHashSet2.remove(formatGroundAir);
            }
         }

         entry.linkedHashSet2.add(formatGroundAir);
      }
   }

   private static long offsetPositionKeyDown(long cleanedEvictedValue) {
      return ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(cleanedEvictedValue), ProofSupportUtils.getValue(cleanedEvictedValue) - 1, ProofSupportUtils.getValueForLong(cleanedEvictedValue));
   }

   public long createEntry(long cleanedEvictedValue, int cleanedEvictedValue2) {
      while (this.reclaimableCleanedEntryMap.size() >= 256) {
         this.updateEvictedCleaned();
      }

      long formatGroundAir = this.nextSequenceId++;
      this.reclaimableCleanedEntryMap.put(formatGroundAir, new CleanedEvicted.Entry(formatGroundAir, cleanedEvictedValue, cleanedEvictedValue2));
      return formatGroundAir;
   }

   public int getCleanedReclaimable() {
      int cleanedReclaimable = 0;

      for (CleanedEvicted.Entry entry : this.reclaimableCleanedEntryMap.values()) {
         if (entry.cleanedReclaimableType != CleanedEvicted.State.CLEANED) {
            cleanedReclaimable += entry.linkedHashSet2.size();
         }
      }

      return cleanedReclaimable;
   }

   private void updateEvictedCleaned() {
      CleanedEvicted.Entry entry = null;

      for (CleanedEvicted.Entry entry2 : this.reclaimableCleanedEntryMap.values()) {
         if (entry2.cleanedReclaimableType == CleanedEvicted.State.CLEANED) {
            entry = entry2;
            break;
         }
      }

      if (entry == null) {
         entry = this.reclaimableCleanedEntryMap.values().iterator().next();
      }

      this.reclaimableCleanedEntryMap.remove(entry.time2);

      for (Long long2 : entry.linkedHashSet2) {
         this.cleanedNeededPositionMap.remove(long2, entry.time2);
      }

      this.evicted++;
   }

   public void handleLinkedHash(long linkedHashValue, long blockPos) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(linkedHashValue);
      if (entry != null) {
         entry.linkedHashSet.add(blockPos);
      }
   }

   public boolean canFlagEntry(long cleanedEvictedValue) {
      Long long2 = this.cleanedNeededPositionMap.get(cleanedEvictedValue);
      if (long2 == null) {
         return false;
      } else {
         CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(long2);
         return entry != null && entry.isReclaimableCleaned();
      }
   }

   public CleanedEvicted.State getReclaimableCleaned(long reclaimableCleanedValue) {
      CleanedEvicted.Entry entry = this.reclaimableCleanedEntryMap.get(reclaimableCleanedValue);
      return entry == null ? null : entry.cleanedReclaimableType;
   }

   private static final class Entry {
      final int value;
      final LinkedHashSet<Long> linkedHashSet;
      CleanedEvicted.State cleanedReclaimableType = CleanedEvicted.State.ACTIVE;
      long time;
      final LinkedHashSet<Long> linkedHashSet2 = new LinkedHashSet<>();
      final long time2;

      Entry(long time, long time2, int value) {
         this.linkedHashSet = new LinkedHashSet<>();
         this.time2 = time;
         this.time = time2;
         this.value = value;
      }

      CleanedEvicted.Network getNetwork() {
         return new CleanedEvicted.Network(
            this.time2,
            this.time,
            this.cleanedReclaimableType,
            this.value,
            Collections.unmodifiableSet(new LinkedHashSet<>(this.linkedHashSet2)),
            Collections.unmodifiableSet(new LinkedHashSet<>(this.linkedHashSet))
         );
      }

      boolean isReclaimableCleaned() {
         return this.cleanedReclaimableType == CleanedEvicted.State.ACTIVE || this.cleanedReclaimableType == CleanedEvicted.State.RETURN_PATH;
      }
   }

   public record Network(long id, long ownerKey, CleanedEvicted.State state, int openedTick, Set<Long> cells, Set<Long> servedTargets) {
   }

   public static enum State {
      ACTIVE,
      RETURN_PATH,
      RECLAIMABLE,
      CLEANED;
   }
}
