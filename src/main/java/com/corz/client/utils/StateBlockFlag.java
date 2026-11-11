package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.corz.client.utils.ExcavationCluster;

public final class StateBlockFlag {
   private int blockDetectCount;
   public static final int UNIT_STEP = 1;
   private final Map<Long, Map<Long, StateBlockFlag.Entry>> entriesByChunk = new HashMap<>();
   private boolean manuallyTriggered;
   private int entryCount = 0;
   private int blockSeenCount;
   private int blockConfirmCount = 0;

   public int getSeenCount() {
      return this.blockSeenCount;
   }

   public int getDetectCount() {
      return this.blockDetectCount;
   }

   public List<StateBlockFlag.Entry> getAllEntries() {
      ArrayList arrayList = new ArrayList();

      for (Map map : this.entriesByChunk.values()) {
         arrayList.addAll(map.values());
      }

      return arrayList;
   }

   public int purgeFlaggedEntriesNear(int blockValue, int blockValue2, int blockValue3, int blockLimit, long timeMillis, long blockValue5) {
      int timeFlag = 0;
      int blockValue6 = (blockLimit >> 4) + 1;
      int blockValue7 = blockValue >> 4;
      int blockValue8 = blockValue3 >> 4;

      for (int i = blockValue7 - blockValue6; i <= blockValue7 + blockValue6; i++) {
         for (int j = blockValue8 - blockValue6; j <= blockValue8 + blockValue6; j++) {
            long packChunkKey = ExcavationCluster.packChunkKey(i, j);
            Map map = this.entriesByChunk.get(packChunkKey);
            if (map != null) {
               Iterator iterator = map.values().iterator();

               while (iterator.hasNext()) {
                  StateBlockFlag.Entry entry = (StateBlockFlag.Entry)iterator.next();
                  if (entry.flag && timeMillis - entry.timeMillis >= blockValue5) {
                     int blockBound = Math.abs(entry.value - blockValue);
                     int aggroPreemptBound = Math.abs(entry.value2 - blockValue2);
                     int rocketsBuyBound = Math.abs(entry.value3 - blockValue3);
                     if (Math.max(blockBound, Math.max(aggroPreemptBound, rocketsBuyBound)) <= blockLimit) {
                        iterator.remove();
                        this.entryCount--;
                        timeFlag++;
                     }
                  }
               }

               if (map.isEmpty()) {
                  this.entriesByChunk.remove(packChunkKey);
               }
            }
         }
      }

      return timeFlag;
   }

   public void refreshExistingEntry(int blockValue, int blockValue2, int blockValue3, Object blockState, long timeMillis) {
      StateBlockFlag.Entry timeState = this.getEntryAt(blockValue, blockValue2, blockValue3);
      if (timeState != null) {
         if (blockState != null) {
            timeState.blockState = blockState;
         }

         timeState.timeMillis = timeMillis;
         timeState.flag = false;
      }
   }

   public int purgeDistantAndStale(long blockValue, int blockValue2, int blockValue3, int blockValue4) {
      int blockTotal = 0;
      Iterator iterator = this.entriesByChunk.entrySet().iterator();

      while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry)iterator.next();
         long blockValue5 = (Long)entry.getKey();
         int blockValue6 = (int)(blockValue5 & 4294967295L);
         int blockValue7 = (int)(blockValue5 >> 32);
         boolean blockFlag = Math.abs(blockValue6 - blockValue2) > blockValue4 || Math.abs(blockValue7 - blockValue3) > blockValue4;
         if (blockFlag) {
            blockTotal += ((Map)entry.getValue()).size();
            this.entryCount = this.entryCount - ((Map)entry.getValue()).size();
            iterator.remove();
         } else {
            Iterator iterator2 = ((Map)entry.getValue()).values().iterator();

            while (iterator2.hasNext()) {
               StateBlockFlag.Entry stateBlockFlagEntry = (StateBlockFlag.Entry)iterator2.next();
               if (blockValue - stateBlockFlagEntry.timeMillis > 1800000L) {
                  iterator2.remove();
                  this.entryCount--;
                  blockTotal++;
               }
            }

            if (((Map)entry.getValue()).isEmpty()) {
               iterator.remove();
            }
         }
      }

      return blockTotal;
   }

   public boolean removeEntryAt(int blockValue, int blockValue2, int blockValue3) {
      long packChunkKey = ExcavationCluster.packChunkKey(blockValue >> 4, blockValue3 >> 4);
      Map map = this.entriesByChunk.get(packChunkKey);
      if (map == null) {
         return false;
      } else {
         boolean hasRemove = map.remove(packBlockPos(blockValue, blockValue2, blockValue3)) != null;
         if (hasRemove) {
            this.entryCount--;
         }

         if (map.isEmpty()) {
            this.entriesByChunk.remove(packChunkKey);
         }

         return hasRemove;
      }
   }

   public Map<Long, StateBlockFlag.Entry> getChunkEntries(long unmodifiableValue) {
      Map map = this.entriesByChunk.get(unmodifiableValue);
      return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
   }

   public void markTriggered() {
      this.manuallyTriggered = true;
   }

   public void reset() {
      this.entriesByChunk.clear();
      this.entryCount = 0;
      this.blockConfirmCount = this.blockDetectCount = this.blockSeenCount = 0;
      this.manuallyTriggered = false;
   }

   public static long packBlockPos(int blockValue, int blockValue2, int blockValue3) {
      return blockValue & 67108863 | (long)(blockValue3 & 67108863) << 26 | (long)(blockValue2 & 4095) << 52;
   }

   public int getConfirmCount() {
      return this.blockConfirmCount;
   }

   public StateBlockFlag() {
      this.blockDetectCount = 0;
      this.blockSeenCount = 0;
      this.manuallyTriggered = false;
   }

   public StateBlockFlag.Entry getEntryAt(int blockValue, int blockValue2, int blockValue3) {
      Map map = this.entriesByChunk.get(ExcavationCluster.packChunkKey(blockValue >> 4, blockValue3 >> 4));
      return map == null ? null : (StateBlockFlag.Entry)map.get(packBlockPos(blockValue, blockValue2, blockValue3));
   }

   public boolean shouldAlert() {
      return this.manuallyTriggered || this.blockDetectCount >= 1 || this.blockConfirmCount >= 3;
   }

   public int getEntryCount() {
      return this.entryCount;
   }

   private void putEntry(int blockValue, int blockValue2, int blockValue3, Object blockState, long timeMillis, boolean flag) {
      long packChunkKey = ExcavationCluster.packChunkKey(blockValue >> 4, blockValue3 >> 4);
      Map ifAbsent = this.entriesByChunk.computeIfAbsent(packChunkKey, long2 -> new HashMap<>());
      long botCrystalText = packBlockPos(blockValue, blockValue2, blockValue3);
      StateBlockFlag.Entry entry = (StateBlockFlag.Entry)ifAbsent.get(botCrystalText);
      if (entry == null) {
         if (ifAbsent.size() >= 512 || this.entryCount >= 20000) {
            return;
         }

         ifAbsent.put(botCrystalText, new StateBlockFlag.Entry(blockValue, blockValue2, blockValue3, blockState, timeMillis, flag));
         this.entryCount++;
      } else {
         if (blockState != null) {
            entry.blockState = blockState;
         }

         entry.timeMillis = timeMillis;
         entry.flag = flag;
      }
   }

   public boolean onBlockTransition(int blockValue, int blockValue2, int blockValue3, int liquidAir, int classifyBlockState, Object obj, Object blockState, long timeMillis) {
      if (liquidAir == 2 && classifyBlockState == 3) {
         this.putEntry(blockValue, blockValue2, blockValue3, blockState, timeMillis, false);
         this.blockConfirmCount++;
         return true;
      } else if (liquidAir == 3 && classifyBlockState == 2) {
         this.putEntry(blockValue, blockValue2, blockValue3, obj, timeMillis, true);
         this.blockDetectCount++;
         return true;
      } else if (liquidAir == 3) {
         if (this.removeEntryAt(blockValue, blockValue2, blockValue3)) {
            this.blockSeenCount++;
         }

         return false;
      } else {
         if (classifyBlockState == 3) {
            StateBlockFlag.Entry timeState = this.getEntryAt(blockValue, blockValue2, blockValue3);
            if (timeState != null) {
               timeState.blockState = blockState;
               timeState.timeMillis = timeMillis;
               timeState.flag = false;
            }
         }

         return false;
      }
   }

   public static final class Entry {
      public Object blockState;
      public final int value;
      public boolean flag;
      public long timeMillis;
      public final int value2;
      public final int value3;

      Entry(int value, int value2, int value3, Object blockState, long timeMillis, boolean flag) {
         this.value = value;
         this.value2 = value2;
         this.value3 = value3;
         this.blockState = blockState;
         this.timeMillis = timeMillis;
         this.flag = flag;
      }
   }
}
