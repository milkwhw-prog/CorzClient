package com.corz.client.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class FingerprintUpdate {
   private int fingerprintHandleCount;
   private final Map<Long, FingerprintUpdate.Fail> fingerprintFailMap = new HashMap<>();
   private int fingerprintCount;
   private int fingerprintUpdateCount;
   public static final int FINGERPRINT_UPDATE_STEP = 1;
   private final Set<Long> mountsStrictPositionSet = new HashSet<>();
   private int secondaryFingerprintUpdateCount;

   public long getFingerprint(long fingerprintValue) {
      FingerprintUpdate.Fail fail = this.fingerprintFailMap.get(fingerprintValue);
      return fail == null ? 0L : fail.fingerprint();
   }

   public boolean hasFailForFingerprint(long containsValue) {
      return this.fingerprintFailMap.containsKey(containsValue);
   }

   public int getFingerprintHandleCount() {
      return this.mountsStrictPositionSet.size();
   }

   public void incrementFingerprintHandleCount() {
      this.mountsStrictPositionSet.add(Long.MIN_VALUE + ++this.fingerprintUpdateCount);
   }

   public int getFingerprintCount() {
      return this.fingerprintFailMap.size();
   }

   public void incrementFingerprintCount() {
      this.fingerprintFailMap.clear();
      this.mountsStrictPositionSet.clear();
   }

   public int getFingerprintUpdateCount() {
      return this.fingerprintHandleCount;
   }

   public void incrementFingerprintUpdateCount() {
      this.mountsStrictPositionSet.clear();
   }

   public void recordFingerprintFail(long fingerprintUpdateValue, long l, int fingerprintUpdateValue3) {
      this.recordFingerprintFailAlternate(fingerprintUpdateValue, l, fingerprintUpdateValue3);
      this.mountsStrictPositionSet.add(fingerprintUpdateValue);
   }

   public boolean isFingerprintFlagSet() {
      return false;
   }

   public int getSecondaryFingerprintUpdateCount() {
      return this.secondaryFingerprintUpdateCount;
   }

   public void incrementSecondaryUpdateCount() {
      this.mountsStrictPositionSet.removeIf(long2 -> long2 < -9223372036853775808L);
   }

   public int getFingerprintCountAlternate() {
      return this.fingerprintCount;
   }

   public static long packFingerprintValues(int fingerprintUpdateValue, int fingerprintUpdateValue2, int fingerprintUpdateValue3, int fingerprintUpdateValue4, int fingerprintUpdateValue5, int fingerprintUpdateValue6, int fingerprintUpdateValue7) {
      long fingerprintUpdateValue8 = 8435356283621752739L;
      long fingerprintUpdateValue9 = -3750763034362895579L;
      long[] longs = new long[]{fingerprintUpdateValue, fingerprintUpdateValue2, fingerprintUpdateValue3, fingerprintUpdateValue4, fingerprintUpdateValue5 >> 2, fingerprintUpdateValue6 >> 2, fingerprintUpdateValue7 >> 2};
      long[] fingerprintUpdateValues = longs;
      long fingerprintUpdateValue10 = fingerprintUpdateValue8 ^ ((long)longs.length << 32 ^ fingerprintUpdateValue8) & -4294967296L;

      for (long i = fingerprintUpdateValue10 ^ (0L ^ fingerprintUpdateValue10) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long fingerprintUpdateValue11 = fingerprintUpdateValues[(int)i];
         fingerprintUpdateValue9 = (fingerprintUpdateValue9 ^ fingerprintUpdateValue11) * 1099511628211L;
      }

      return fingerprintUpdateValue9;
   }

   public FingerprintUpdate.Strategy getMountsStrict() {
      int setSize = this.mountsStrictPositionSet.size();
      if (setSize <= 0) {
         return FingerprintUpdate.Strategy.ANY;
      } else if (setSize == 1) {
         return FingerprintUpdate.Strategy.STRICT_ROI;
      } else {
         return setSize == 2 ? FingerprintUpdate.Strategy.NO_MOUNTS : FingerprintUpdate.Strategy.LOCAL_MISSION;
      }
   }

   public int getFingerprintDelta(long fingerprintValue, long l) {
      FingerprintUpdate.Fail fail = this.fingerprintFailMap.get(fingerprintValue);
      if (fail != null && fail.fingerprint() != l) {
         this.fingerprintFailMap.remove(fingerprintValue);
         this.mountsStrictPositionSet.remove(fingerprintValue);
         this.fingerprintCount++;
         return 1;
      } else {
         return 0;
      }
   }

   public void recordFingerprintFailAlternate(long fingerprintUpdateValue, long l, int fingerprintUpdateValue3) {
      if (this.fingerprintFailMap.size() >= 1024 && !this.fingerprintFailMap.containsKey(fingerprintUpdateValue)) {
         Iterator iterator = this.fingerprintFailMap.keySet().iterator();
         iterator.next();
         iterator.remove();
      }

      this.fingerprintFailMap.put(fingerprintUpdateValue, new FingerprintUpdate.Fail(l, fingerprintUpdateValue3));
      this.fingerprintHandleCount++;
      this.secondaryFingerprintUpdateCount++;
   }

   public boolean canFingerprint(long fingerprintValue, long autoBuilderValue) {
      FingerprintUpdate.Fail fail = this.fingerprintFailMap.get(fingerprintValue);
      return fail != null && fail.fingerprint() == autoBuilderValue;
   }

   private record Fail(long fingerprint, int tick) {
   }

   public static enum Strategy {
      ANY,
      STRICT_ROI,
      NO_MOUNTS,
      LOCAL_MISSION;
   }
}
