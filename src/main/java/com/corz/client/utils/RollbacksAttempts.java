package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RollbacksAttempts {
   public static final int ROLLBACKS_ATTEMPTS_STEP = 1;
   private int rollbacks;
   private int attempts;
   private final Map<RollbacksAttempts.Certificate, RollbacksAttempts.Entry> rollbacksAttemptsEntryMap = new LinkedHashMap<>();
   private int refused;

   public boolean canRefusedFingerprint(RollbacksAttempts.Certificate certificate, long refusedFingerprintValue) {
      if (certificate == null) {
         return false;
      } else {
         RollbacksAttempts.Entry entry = this.rollbacksAttemptsEntryMap.get(certificate);
         if (entry == null) {
            return false;
         } else if (entry.geometryFingerprint() != refusedFingerprintValue) {
            this.rollbacksAttemptsEntryMap.remove(certificate);
            return false;
         } else {
            this.refused++;
            return true;
         }
      }
   }

   public int getAttempts() {
      return this.attempts;
   }

   public static int getValue(double rollbacksAttemptsLimit, double d2) {
      return rollbacksAttemptsLimit >= 0.3 && rollbacksAttemptsLimit <= 0.7 && d2 >= 0.3 && d2 <= 0.7 ? 0 : 1;
   }

   public int getValue() {
      return this.rollbacksAttemptsEntryMap.size();
   }

   public void handleRollbacksAttempts(RollbacksAttempts.Certificate certificate, long rollbacksAttemptsValue, int rollbacksAttemptsValue2) {
      if (certificate != null) {
         this.attempts++;
         if (this.rollbacksAttemptsEntryMap.put(certificate, new RollbacksAttempts.Entry(rollbacksAttemptsValue, rollbacksAttemptsValue2)) == null) {
            this.rollbacks++;
         }
      }
   }

   public void updateRollbacksAttempts() {
      this.rollbacksAttemptsEntryMap.clear();
      this.rollbacks = 0;
      this.refused = 0;
      this.attempts = 0;
   }

   public int getRollbacks() {
      return this.rollbacks;
   }

   public int getRefused() {
      return this.refused;
   }

   public String formatRollbacksAttempts() {
      return "rollbacks=" + this.rollbacks + " attempts=" + this.attempts + " refused=" + this.refused + " live=" + this.rollbacksAttemptsEntryMap.size();
   }

   public record Certificate(long target, long support, int face, boolean crouched, boolean sneak, int hitClass) {
   }

   private record Entry(long geometryFingerprint, int tick) {
   }
}
