package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProofExecutor {
   public static final int PROOF_EXECUTOR_STEP = 1;
   private final Map<Long, List<ProofExecutor.Certificate>> arrivalExecutorMap = new LinkedHashMap<>();
   private int executorProofLive;
   private int executorProofHits;
   private int executorProofRecords;
   private int executorProofReopened;
   private static final String EXECUTOR_PROOF_INVALID = "executor proof invalid at arrival";

   public static boolean canFlag(long proofExecutorValue, long proofSupportValue) {
      return ProofSupportUtils.getValue(proofExecutorValue) == ProofSupportUtils.getValue(proofSupportValue)
         && Math.abs(ProofSupportUtils.getValueInt(proofExecutorValue) - ProofSupportUtils.getValueInt(proofSupportValue)) <= 1
         && Math.abs(ProofSupportUtils.getValueForLong(proofExecutorValue) - ProofSupportUtils.getValueForLong(proofSupportValue)) <= 1;
   }

   private static boolean canFlag(int proofExecutorValue, int proofExecutorValue2) {
      return proofExecutorValue == -1 || proofExecutorValue == proofExecutorValue2;
   }

   public void updateExecutorProof() {
      this.arrivalExecutorMap.clear();
      this.executorProofLive = 0;
      this.executorProofRecords = 0;
      this.executorProofHits = 0;
      this.executorProofReopened = 0;
   }

   public int getExecutorProof(long executorProofValue, long l) {
      List arrivalexecutormap = this.arrivalExecutorMap.get(executorProofValue);
      if (arrivalexecutormap == null) {
         return 0;
      } else {
         int executorProof = 0;
         Iterator iterator = arrivalexecutormap.iterator();

         while (iterator.hasNext()) {
            if (((ProofExecutor.Certificate)iterator.next()).dependencyFingerprint() != l) {
               iterator.remove();
               executorProof++;
            }
         }

         if (arrivalexecutormap.isEmpty()) {
            this.arrivalExecutorMap.remove(executorProofValue);
         }

         this.executorProofLive -= executorProof;
         this.executorProofReopened += executorProof;
         return executorProof;
      }
   }

   public int getExecutorProofHits() {
      return this.executorProofHits;
   }

   public void handleExecutorLive(long executorLiveValue) {
      List arrivalexecutormap = this.arrivalExecutorMap.remove(executorLiveValue);
      if (arrivalexecutormap != null) {
         this.executorProofLive = this.executorProofLive - arrivalexecutormap.size();
      }
   }

   public int getExecutorProofLive() {
      return this.executorProofLive;
   }

   public String formatExecutorHits(long executorHitsValue, long proofSupportValue, int executorHitsValue3, int executorHitsValue4, long l) {
      List arrivalexecutormap = this.arrivalExecutorMap.get(executorHitsValue);
      if (arrivalexecutormap == null) {
         return null;
      } else {
         for (ProofExecutor.Certificate certificate : (Iterable<com.corz.client.utils.ProofExecutor.Certificate>) (arrivalexecutormap)) {
            if (certificate.dependencyFingerprint() == l
               && canFlag(certificate.stand(), proofSupportValue)
               && canFlagForInt(certificate.poseClass(), executorHitsValue3)
               && canFlag(certificate.supportFace(), executorHitsValue4)) {
               this.executorProofHits++;
               return certificate.reason();
            }
         }

         return null;
      }
   }

   public boolean canExecutorArrival(long executorArrivalValue, long executorArrivalValue2, int executorArrivalValue3, int executorArrivalValue4, String executorArrivalText, long executorArrivalValue5, int executorArrivalValue6) {
      String executorArrivalText2 = executorArrivalText != null && !executorArrivalText.isBlank() ? executorArrivalText : "executor proof invalid at arrival";
      ProofExecutor.Certificate certificate = new ProofExecutor.Certificate(executorArrivalValue, executorArrivalValue2, executorArrivalValue3, executorArrivalValue4, executorArrivalText2, executorArrivalValue5, executorArrivalValue6);
      if (this.arrivalExecutorMap.size() >= 2048 && !this.arrivalExecutorMap.containsKey(executorArrivalValue)) {
         Iterator iterator = this.arrivalExecutorMap.keySet().iterator();
         iterator.next();
         iterator.remove();
      }

      List ifAbsent = this.arrivalExecutorMap.computeIfAbsent(executorArrivalValue, long2 -> new ArrayList<>());

      for (int i = 0; i < ifAbsent.size(); i++) {
         ProofExecutor.Certificate proofExecutorCertificate = (ProofExecutor.Certificate)ifAbsent.get(i);
         if (proofExecutorCertificate.stand() == executorArrivalValue2 && proofExecutorCertificate.poseClass() == executorArrivalValue3 && proofExecutorCertificate.supportFace() == executorArrivalValue4) {
            ifAbsent.set(i, certificate);
            return false;
         }
      }

      ifAbsent.add(certificate);
      this.executorProofLive++;
      this.executorProofRecords++;
      return true;
   }

   public String formatExecutorProof() {
      return "executorProofRecords="
         + this.executorProofRecords
         + " executorProofHits="
         + this.executorProofHits
         + " executorProofLive="
         + this.executorProofLive
         + " executorProofReopened="
         + this.executorProofReopened;
   }

   public int getExecutorProofRecords() {
      return this.executorProofRecords;
   }

   private static boolean canFlagForInt(int proofExecutorValue, int proofExecutorValue2) {
      return proofExecutorValue == 0 || proofExecutorValue2 == 0 || proofExecutorValue == proofExecutorValue2;
   }

   public int getExecutorProofReopened() {
      return this.executorProofReopened;
   }

   public record Certificate(long target, long stand, int poseClass, int supportFace, String reason, long dependencyFingerprint, int tick) {
   }
}
