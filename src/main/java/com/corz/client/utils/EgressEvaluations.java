package com.corz.client.utils;

import java.util.HashMap;

public final class EgressEvaluations {
   private int egressAlignDeferrals;
   private int egressEvaluations;
   private int booleanEvaluationCount;
   private int egressForcedOutside;
   private final HashMap<Long, Boolean> egressEvaluationsBooleanMap = new HashMap<>();
   private int egressRouteRejected;
   private int egressSafe;
   private int egressDeferred;

   public static long mixEgressKey(long egressEvaluationsValue, long egressEvaluationsValue2, long l) {
      long egressEvaluationsValue4 = egressEvaluationsValue * -7046029254386353131L ^ egressEvaluationsValue2;
      return (egressEvaluationsValue4 ^ l) * 1099511628211L;
   }

   public void incrementEvaluations() {
      this.egressEvaluationsBooleanMap.clear();
   }

   public int getEgressSafe() {
      return this.egressSafe;
   }

   public void updateRejectedEgress() {
      this.egressRouteRejected++;
   }

   public int getEgressAlignDeferrals() {
      return this.egressAlignDeferrals;
   }

   public void updateDeferralsAlign() {
      this.egressAlignDeferrals++;
   }

   public int getEgressDeferred() {
      return this.egressDeferred;
   }

   public int getEgressRouteRejected() {
      return this.egressRouteRejected;
   }

   public int getEgressEvaluations() {
      return this.egressEvaluations;
   }

   public int getEgressForcedOutside() {
      return this.egressForcedOutside;
   }

   public void handleEgressEvaluations(long egressEvaluationsValue, long egressEvaluationsValue2, long l, boolean egressEvaluationsFlag) {
      if (this.egressEvaluationsBooleanMap.size() > 4096) {
         this.egressEvaluationsBooleanMap.clear();
      }

      this.egressEvaluationsBooleanMap.put(mixEgressKey(egressEvaluationsValue, egressEvaluationsValue2, l), egressEvaluationsFlag);
      this.egressEvaluations++;
      if (egressEvaluationsFlag) {
         this.egressDeferred++;
      } else {
         this.egressSafe++;
      }
   }

   public int getCachedBooleanCount() {
      return this.booleanEvaluationCount;
   }

   public String formatEgressDeferrals() {
      return "egressEvaluations="
         + this.egressEvaluations
         + " egressSafe="
         + this.egressSafe
         + " egressDeferred="
         + this.egressDeferred
         + " egressForcedOutside="
         + this.egressForcedOutside
         + " egressRouteRejected="
         + this.egressRouteRejected
         + " egressAlignDeferrals="
         + this.egressAlignDeferrals;
   }

   public void updateOutsideForced() {
      this.egressForcedOutside++;
   }

   public Boolean getBoolean(long booleanValue, long booleanValue2, long l) {
      Boolean boolean2 = this.egressEvaluationsBooleanMap.get(mixEgressKey(booleanValue, booleanValue2, l));
      if (boolean2 != null) {
         this.booleanEvaluationCount++;
      }

      return boolean2;
   }
}
