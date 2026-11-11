package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

public final class HashOpenDefault {
   private final Long2LongOpenHashMap cellXorHashMap;
   private long hashTickCounter;
   private final Long2IntOpenHashMap openCellCountMap = new Long2IntOpenHashMap();
   private final Long2IntOpenHashMap positionHitCountMap;

   public void updateClearHash() {
      this.openCellCountMap.clear();
      this.cellXorHashMap.clear();
      this.positionHitCountMap.clear();
      this.hashTickCounter = 0L;
   }

   public void markCellOpen(int hashOpenValue, int hashOpenValue2, int hashOpenValue3) {
      this.openCellCountMap.addTo(packCellKey(hashOpenValue, hashOpenValue2, hashOpenValue3), 1);
   }

   public long getHashTickCounter() {
      return this.hashTickCounter;
   }

   public void updateCellHash(int hashPutValue, int hashPutValue2, int hashPutValue3, long hashPutValue4, long hashPutValue5) {
      this.markCellOpen(hashPutValue, hashPutValue2, hashPutValue3);
      if (hashPutValue4 != hashPutValue5) {
         long packCellKey6 = packCellKey(hashPutValue, hashPutValue2, hashPutValue3);
         long hashPutValue7 = mixCellKeyWithValue(hashPutValue, hashPutValue2, hashPutValue3, hashPutValue4) ^ mixCellKeyWithValue(hashPutValue, hashPutValue2, hashPutValue3, hashPutValue5);
         this.cellXorHashMap.put(packCellKey6, this.cellXorHashMap.get(packCellKey6) ^ hashPutValue7);
      }
   }

   public long getCellXorHash(long orDefaultValue) {
      return this.cellXorHashMap.getOrDefault(orDefaultValue, 0L);
   }

   public long hashHitCountsInBox(int defaultHashValue, int defaultHashValue2, int defaultHashValue3, int defaultHashValue4, int defaultHashValue5, int defaultHashValue6) {
      long defaultHashValue7 = -1166716722322080200L;
      long defaultHashValue8 = 1099019634829716296L;
      long defaultHash = -3750763034362895579L;
      long defaultHashValue9 = defaultHashValue8 ^ ((long)(defaultHashValue >> 4) << 32 ^ defaultHashValue8) & -4294967296L;

      while ((int)(defaultHashValue9 >>> 32) <= defaultHashValue4 >> 4) {
         long defaultHashValue10;
         for (defaultHashValue10 = defaultHashValue9 ^ (defaultHashValue2 >> 4 ^ defaultHashValue9) & 4294967295L; (int)defaultHashValue10 <= defaultHashValue5 >> 4; defaultHashValue10 ^= (defaultHashValue10 ^ defaultHashValue10 + 1L) & 4294967295L) {
            defaultHashValue7 ^= ((long)(defaultHashValue3 >> 4) << 32 ^ defaultHashValue7) & -4294967296L;

            while ((int)(defaultHashValue7 >>> 32) <= defaultHashValue6 >> 4) {
               long defaultHashValue11 = ((int)(defaultHashValue10 >>> 32) & 16777215L) << 40 | ((int)defaultHashValue10 & 65535L) << 24 | (int)(defaultHashValue7 >>> 32) & 16777215L;
               long defaultHashValue12 = defaultHashValue7 ^ (this.positionHitCountMap.getOrDefault(defaultHashValue11, 0) ^ defaultHashValue7) & 4294967295L;
               defaultHash = (defaultHash ^ defaultHashValue11 * 31L + (int)defaultHashValue12) * 1099511628211L;
               defaultHashValue7 = defaultHashValue12 + 4294967296L;
            }
         }

         defaultHashValue9 = defaultHashValue10 + 4294967296L;
      }

      return defaultHash;
   }

   public long hashOpenCellsInRadius(int defaultHashValue, int defaultHashValue2, int defaultHashValue3, int defaultHashValue4) {
      long defaultHashValue5 = -2854560640851504516L;
      long defaultHashValue6 = 3483824508039619707L;
      long defaultHash = -3750763034362895579L;
      long defaultHashValue7 = defaultHashValue5 ^ ((long)(defaultHashValue - defaultHashValue4 >> 4) << 32 ^ defaultHashValue5) & -4294967296L;

      while ((int)(defaultHashValue7 >>> 32) <= defaultHashValue + defaultHashValue4 >> 4) {
         long defaultHashValue8;
         for (defaultHashValue8 = defaultHashValue7 ^ (defaultHashValue2 - defaultHashValue4 >> 4 ^ defaultHashValue7) & 4294967295L; (int)defaultHashValue8 <= defaultHashValue2 + defaultHashValue4 >> 4; defaultHashValue8 ^= (defaultHashValue8 ^ defaultHashValue8 + 1L) & 4294967295L) {
            defaultHashValue6 ^= ((long)(defaultHashValue3 - defaultHashValue4 >> 4) << 32 ^ defaultHashValue6) & -4294967296L;

            while ((int)(defaultHashValue6 >>> 32) <= defaultHashValue3 + defaultHashValue4 >> 4) {
               long defaultHashValue9 = ((int)(defaultHashValue8 >>> 32) & 16777215L) << 40 | ((int)defaultHashValue8 & 65535L) << 24 | (int)(defaultHashValue6 >>> 32) & 16777215L;
               long defaultHashValue10 = defaultHashValue6 ^ (this.openCellCountMap.getOrDefault(defaultHashValue9, 0) ^ defaultHashValue6) & 4294967295L;
               defaultHash = (defaultHash ^ defaultHashValue9 * 31L + (int)defaultHashValue10) * 1099511628211L;
               defaultHashValue6 = defaultHashValue10 + 4294967296L;
            }
         }

         defaultHashValue7 = defaultHashValue8 + 4294967296L;
      }

      return defaultHash;
   }

   public long hashHitCountsInAsymmetricBox(int defaultHashValue, int defaultHashValue2, int defaultHashValue3, int defaultHashValue4, int defaultHashValue5, int defaultHashValue6, int defaultHashValue7) {
      long defaultHashValue8 = -7087669591613706553L;
      long defaultHashValue9 = -4707556752938117198L;
      long defaultHash = -3750763034362895579L;
      long defaultHashValue10 = defaultHashValue8 ^ ((long)(defaultHashValue - defaultHashValue4 >> 4) << 32 ^ defaultHashValue8) & -4294967296L;

      while ((int)(defaultHashValue10 >>> 32) <= defaultHashValue + defaultHashValue4 >> 4) {
         long defaultHashValue11;
         for (defaultHashValue11 = defaultHashValue10 ^ (defaultHashValue2 - defaultHashValue6 >> 4 ^ defaultHashValue10) & 4294967295L; (int)defaultHashValue11 <= defaultHashValue2 + defaultHashValue5 >> 4; defaultHashValue11 ^= (defaultHashValue11 ^ defaultHashValue11 + 1L) & 4294967295L) {
            defaultHashValue9 ^= ((long)(defaultHashValue3 - defaultHashValue7 >> 4) << 32 ^ defaultHashValue9) & -4294967296L;

            while ((int)(defaultHashValue9 >>> 32) <= defaultHashValue3 + defaultHashValue7 >> 4) {
               long defaultHashValue12 = ((int)(defaultHashValue11 >>> 32) & 16777215L) << 40 | ((int)defaultHashValue11 & 65535L) << 24 | (int)(defaultHashValue9 >>> 32) & 16777215L;
               long defaultHashValue13 = defaultHashValue9 ^ (this.positionHitCountMap.getOrDefault(defaultHashValue12, 0) ^ defaultHashValue9) & 4294967295L;
               defaultHash = (defaultHash ^ defaultHashValue12 * 31L + (int)defaultHashValue13) * 1099511628211L;
               defaultHashValue9 = defaultHashValue13 + 4294967296L;
            }
         }

         defaultHashValue10 = defaultHashValue11 + 4294967296L;
      }

      return defaultHash;
   }

   public void recordPositionHit(int hashValue, int hashValue2, int hashValue3) {
      this.positionHitCountMap.addTo(packCellKey(hashValue, hashValue2, hashValue3), 1);
      this.hashTickCounter++;
   }

   public static long packCellKey(int hashOpenValue, int hashOpenValue2, int hashOpenValue3) {
      return (hashOpenValue >> 4 & 16777215L) << 40 | (hashOpenValue2 >> 4 & 65535L) << 24 | hashOpenValue3 >> 4 & 16777215L;
   }

   public HashOpenDefault() {
      this.cellXorHashMap = new Long2LongOpenHashMap();
      this.positionHitCountMap = new Long2IntOpenHashMap();
   }

   public int getOpenCellCount() {
      return this.openCellCountMap.size();
   }

   private static long mixCellKeyWithValue(int hashOpenValue, int hashOpenValue2, int hashOpenValue3, long hashOpenValue4) {
      long hashOpenValue5 = hashOpenValue * -7046029254386353131L ^ hashOpenValue2 * -4417276706812531889L ^ hashOpenValue3 * 1609587929392839161L ^ hashOpenValue4 * 2870177450012600261L;
      long hashOpenValue6 = hashOpenValue5 ^ hashOpenValue5 >>> 29;
      long hashOpenValue7 = hashOpenValue6 * -4658895280553007687L;
      return hashOpenValue7 ^ hashOpenValue7 >>> 32;
   }

   public int getPositionHitCount(long orDefaultValue) {
      return this.positionHitCountMap.getOrDefault(orDefaultValue, 0);
   }
}
