package com.corz.client.utils;

import java.util.HashMap;
import java.util.function.LongSupplier;
import java.util.function.LongUnaryOperator;
import com.corz.client.utils.CrouchedBodyUtils;
import com.corz.client.utils.EyePoseData;
import com.corz.client.utils.CellsFingerprint;

public final class ReachCertificate {
   public long reachCertificateExecutes;
   public long reachRepeatPrevented;
   public long reachCertificateSettled;
   public long reachProxyRejections;
   public long reachCertificates;
   public long reachParityFailures;
   public long achStaleCertificateStands;
   private final HashMap<Long, Boolean> invalidationsCertificateBool;
   private final HashMap<Long, ReachCertificate.Invalidation> invalidationsCertificateInva = new HashMap<>();
   public long hCertificateInvalidations;
   private long nextCertificateSerial;

   public boolean canRepeatPrevented(long repeatPreventedValue, long l2, CrouchedBodyUtils.EyeMode eyeMode, LongSupplier longSupplier, LongUnaryOperator longUnaryOperator) {
      if (this.getCellsFingerprint(getTime(repeatPreventedValue, l2, eyeMode), longSupplier, longUnaryOperator) == null) {
         return false;
      } else {
         this.reachRepeatPrevented++;
         return true;
      }
   }

   public void handleInvalidationsCertificate(
      long invalidationsCertificateValu, long l2, CrouchedBodyUtils.EyeMode eyeMode, long l, String invalidationsCertificateText, long invalidationsCertificateValu4, long[] ls
   ) {
      if (this.invalidationsCertificateInva.size() >= 512) {
         this.invalidationsCertificateInva.clear();
         this.invalidationsCertificateBool.clear();
      }

      long formatGroundAir = getTime(invalidationsCertificateValu, l2, eyeMode);
      this.invalidationsCertificateInva.put(formatGroundAir, new ReachCertificate.Invalidation(invalidationsCertificateValu, l2, eyeMode, l, invalidationsCertificateText, invalidationsCertificateValu4, ls));
      this.invalidationsCertificateBool.remove(formatGroundAir);
      this.hCertificateInvalidations++;
   }

   public ReachCertificate.Invalidation getDataForLongEyeMode(long reachCertificateValue, long l2, CrouchedBodyUtils.EyeMode eyeMode, LongSupplier longSupplier) {
      return this.getCellsFingerprint(getTime(reachCertificateValue, l2, eyeMode), longSupplier, null);
   }

   public String formatReachCertificate() {
      return "reachCertificates="
         + this.reachCertificates
         + " reachCertificateExecutes="
         + this.reachCertificateExecutes
         + " reachCertificateSettled="
         + this.reachCertificateSettled
         + " reachParityFailures="
         + this.reachParityFailures
         + " reachCertificateInvalidations="
         + this.hCertificateInvalidations
         + " reachRepeatPrevented="
         + this.reachRepeatPrevented
         + " reachInvalidLive="
         + this.invalidationsCertificateInva.size()
         + " reachProxyRejections="
         + this.reachProxyRejections
         + " reachStaleCertificateStands="
         + this.achStaleCertificateStands;
   }

   public void handleFailuresParity(EyePoseData eyePoseData, String failuresParityText) {
      this.reachParityFailures++;
      this.handle(eyePoseData.target(), eyePoseData.stand(), eyePoseData.eyeMode(), eyePoseData.geometryFingerprint(), failuresParityText, eyePoseData.id());
   }

   public static long getTime(long reachCertificateValue, long l2, CrouchedBodyUtils.EyeMode eyeMode) {
      return reachCertificateValue * -7046029254386353131L ^ Long.rotateLeft(l2 * -4417276706812531889L, 21) ^ (eyeMode.ordinal() + 1L) * 1609587929392839161L;
   }

   public void updateExecutesCertificate() {
      this.reachCertificateExecutes++;
   }

   public boolean canFlag(long reachCertificateValue, long l2, LongSupplier longSupplier) {
      return this.canRepeatPrevented(reachCertificateValue, l2, longSupplier, null);
   }

   public void updateCertificateSettled() {
      this.reachCertificateSettled++;
   }

   private ReachCertificate.Invalidation getCellsFingerprint(long cellsFingerprintValue, LongSupplier longSupplier, LongUnaryOperator longUnaryOperator) {
      ReachCertificate.Invalidation cellsFingerprint = this.invalidationsCertificateInva.get(cellsFingerprintValue);
      if (cellsFingerprint == null) {
         return null;
      } else {
         long cellsFingerprintValue2 = cellsFingerprint.cells() != null && longUnaryOperator != null ? CellsFingerprint.getTime(cellsFingerprint.cells(), longUnaryOperator) : longSupplier.getAsLong();
         if (cellsFingerprint.fingerprint() != cellsFingerprintValue2) {
            this.invalidationsCertificateInva.remove(cellsFingerprintValue);
            this.invalidationsCertificateBool.remove(cellsFingerprintValue);
            return null;
         } else {
            return cellsFingerprint;
         }
      }
   }

   public ReachCertificate.Invalidation getDataForLongEyeModeLongSupplier(
      long reachCertificateValue, long l2, CrouchedBodyUtils.EyeMode eyeMode, LongSupplier longSupplier, LongUnaryOperator longUnaryOperator
   ) {
      return this.getCellsFingerprint(getTime(reachCertificateValue, l2, eyeMode), longSupplier, longUnaryOperator);
   }

   public long getCertificatesReach() {
      this.reachCertificates++;
      return this.nextCertificateSerial++;
   }

   public boolean canFlag(long reachCertificateValue, long l2, CrouchedBodyUtils.EyeMode eyeMode) {
      return this.invalidationsCertificateBool.putIfAbsent(getTime(reachCertificateValue, l2, eyeMode), Boolean.TRUE) == null;
   }

   public int getValue() {
      return this.invalidationsCertificateInva.size();
   }

   public void update() {
      this.invalidationsCertificateInva.clear();
      this.invalidationsCertificateBool.clear();
   }

   public ReachCertificate() {
      this.invalidationsCertificateBool = new HashMap<>();
      this.nextCertificateSerial = 1L;
   }

   public boolean canFlag(long reachCertificateValue, long l2, CrouchedBodyUtils.EyeMode eyeMode, LongSupplier longSupplier) {
      return this.canRepeatPrevented(reachCertificateValue, l2, eyeMode, longSupplier, null);
   }

   public boolean canRepeatPrevented(long repeatPreventedValue, long l2, LongSupplier longSupplier, LongUnaryOperator longUnaryOperator) {
      boolean repeatPrevented = this.getCellsFingerprint(getTime(repeatPreventedValue, l2, CrouchedBodyUtils.EyeMode.STANDING), longSupplier, longUnaryOperator) != null
         && this.getCellsFingerprint(getTime(repeatPreventedValue, l2, CrouchedBodyUtils.EyeMode.CROUCHED), longSupplier, longUnaryOperator) != null;
      if (repeatPrevented) {
         this.reachRepeatPrevented++;
      }

      return repeatPrevented;
   }

   public void handle(long reachCertificateValue, long reachCertificateValue2, CrouchedBodyUtils.EyeMode eyeMode, long l, String reachCertificateText, long l2) {
      this.handleInvalidationsCertificate(reachCertificateValue, reachCertificateValue2, eyeMode, l, reachCertificateText, l2, null);
   }

   public record Invalidation(long target, long stand, CrouchedBodyUtils.EyeMode eyeMode, long fingerprint, String reason, long certificateId, long[] cells) {
      public Invalidation(long reason, long stand, CrouchedBodyUtils.EyeMode eyeMode, long fingerprint, String target, long certificateId) {
         this(reason, stand, eyeMode, fingerprint, target, certificateId, null);
      }
   }
}
