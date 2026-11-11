package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.LongUnaryOperator;

public final class CellsFingerprint {
   private int cellReadIndex;
   private final Map<Long, Integer> fingerprintCellsIntegerMap;
   private final ArrayList<Long> fingerprintCellsPositionList;
   private final Map<Long, CellsFingerprint.Ban> fingerprintCellsBanMap = new HashMap<>();
   private final HashSet<Long> fingerprintCellsPositionSet;
   private final Map<Long, CellsFingerprint.Ban> containsBanMap = new HashMap<>();

   public void recordBan(long cellsFingerprintValue, long[] cellsFingerprintValues, LongUnaryOperator longUnaryOperator, String cellsFingerprintText) {
      if (this.fingerprintCellsPositionSet.add(cellsFingerprintValue)) {
         this.fingerprintCellsPositionList.add(cellsFingerprintValue);
      }

      this.containsBanMap.put(cellsFingerprintValue, new CellsFingerprint.Ban(cellsFingerprintValue, (long[])cellsFingerprintValues.clone(), getTime(cellsFingerprintValues, longUnaryOperator), cellsFingerprintText));
   }

   public void clearAll() {
      this.fingerprintCellsBanMap.clear();
      this.containsBanMap.clear();
      this.fingerprintCellsIntegerMap.clear();
      this.fingerprintCellsPositionList.clear();
      this.fingerprintCellsPositionSet.clear();
      this.cellReadIndex = 0;
   }

   public static long getTime(long[] cellsFingerprintValues, LongUnaryOperator longUnaryOperator) {
      long cellsFingerprintValue = -8585585621011228093L;
      long cellsFingerprintValue2 = -3750763034362895579L;
      long[] cellsFingerprintValues2 = cellsFingerprintValues;
      long cellsFingerprintValue3 = cellsFingerprintValue ^ ((long)cellsFingerprintValues.length << 32 ^ cellsFingerprintValue) & -4294967296L;

      for (long i = cellsFingerprintValue3 ^ (0L ^ cellsFingerprintValue3) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long cellsFingerprintValue4 = cellsFingerprintValues2[(int)i];
         long applyAsLong = longUnaryOperator.applyAsLong(cellsFingerprintValue4);
         cellsFingerprintValue2 = (cellsFingerprintValue2 ^ cellsFingerprintValue4 * -7046029254386353131L + applyAsLong) * 1099511628211L;
      }

      return cellsFingerprintValue2;
   }

   public int getBanCount() {
      return this.containsBanMap.size();
   }

   public int getFingerprintCells(LongUnaryOperator longUnaryOperator, int fingerprintCellsLimit) {
      if (fingerprintCellsLimit > 0 && !this.containsBanMap.isEmpty()) {
         if (this.fingerprintCellsPositionList.size() > this.containsBanMap.size() * 2 + 64) {
            this.fingerprintCellsPositionList.removeIf(key -> !this.containsBanMap.containsKey(key));
            this.fingerprintCellsPositionSet.clear();
            this.fingerprintCellsPositionSet.addAll(this.fingerprintCellsPositionList);
            this.cellReadIndex = 0;
         }

         int fingerprintCells = 0;
         int fingerprintCellsCounter = 0;
         int itemsSize = this.fingerprintCellsPositionList.size();

         while (fingerprintCells < fingerprintCellsLimit && fingerprintCellsCounter < itemsSize) {
            if (this.cellReadIndex >= this.fingerprintCellsPositionList.size()) {
               this.cellReadIndex = 0;
            }

            long fingerprintCellsValue = this.fingerprintCellsPositionList.get(this.cellReadIndex++);
            fingerprintCellsCounter++;
            CellsFingerprint.Ban ban = this.containsBanMap.get(fingerprintCellsValue);
            if (ban != null) {
               fingerprintCells++;
               if (getTime(ban.cells(), longUnaryOperator) != ban.fingerprint()) {
                  this.containsBanMap.remove(fingerprintCellsValue);
                  this.fingerprintCellsIntegerMap.remove(fingerprintCellsValue);
               }
            }
         }

         return fingerprintCells;
      } else {
         return 0;
      }
   }

   public boolean canFingerprintCells(long fingerprintCellsValue, LongUnaryOperator longUnaryOperator) {
      CellsFingerprint.Ban ban = this.containsBanMap.get(fingerprintCellsValue);
      if (ban == null) {
         return false;
      } else if (getTime(ban.cells(), longUnaryOperator) == ban.fingerprint()) {
         return true;
      } else {
         this.containsBanMap.remove(fingerprintCellsValue);
         this.fingerprintCellsIntegerMap.remove(fingerprintCellsValue);
         return false;
      }
   }

   public Set<Long> getSet(LongUnaryOperator longUnaryOperator) {
      HashSet hashSet = new HashSet();

      for (CellsFingerprint.Ban ban : new ArrayList<>(this.fingerprintCellsBanMap.values())) {
         if (this.isFingerprintBannedAlternate(ban.key(), longUnaryOperator)) {
            hashSet.add(ban.key());
         }
      }

      return hashSet;
   }

   public int getMerge(long cellsFingerprintValue) {
      return this.fingerprintCellsIntegerMap.merge(cellsFingerprintValue, 1, Integer::sum);
   }

   public void recordBanAlternate(long cellsFingerprintValue, long[] cellsFingerprintValues, LongUnaryOperator longUnaryOperator, String cellsFingerprintText) {
      this.fingerprintCellsBanMap.put(cellsFingerprintValue, new CellsFingerprint.Ban(cellsFingerprintValue, (long[])cellsFingerprintValues.clone(), getTime(cellsFingerprintValues, longUnaryOperator), cellsFingerprintText));
   }

   public int getOrDefault(long orDefaultValue) {
      return this.fingerprintCellsIntegerMap.getOrDefault(orDefaultValue, 0);
   }

   public boolean hasBan(long cellsFingerprintValue) {
      return this.containsBanMap.remove(cellsFingerprintValue) != null;
   }

   public boolean containsBanKey(long containsValue) {
      return this.containsBanMap.containsKey(containsValue);
   }

   public int getPositionCount() {
      return this.fingerprintCellsBanMap.size();
   }

   public boolean isFingerprintBannedAlternate(long fingerprintCellsValue, LongUnaryOperator longUnaryOperator) {
      CellsFingerprint.Ban ban = this.fingerprintCellsBanMap.get(fingerprintCellsValue);
      if (ban == null) {
         return false;
      } else if (getTime(ban.cells(), longUnaryOperator) == ban.fingerprint()) {
         return true;
      } else {
         this.fingerprintCellsBanMap.remove(fingerprintCellsValue);
         return false;
      }
   }

   public CellsFingerprint() {
      this.fingerprintCellsIntegerMap = new HashMap<>();
      this.fingerprintCellsPositionList = new ArrayList<>();
      this.fingerprintCellsPositionSet = new HashSet<>();
   }

   public record Ban(long key, long[] cells, long fingerprint, String reason) {
   }
}
