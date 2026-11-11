package com.corz.client.utils;

import java.util.Locale;
import java.util.function.LongUnaryOperator;

public record DepsReasonData(
   DepsReasonData.Kind kind,
   long semanticKey,
   long target,
   long stand,
   int transitionKind,
   long support,
   long accessFamily,
   long dependencyFingerprint,
   long[] dependencyCells,
   String reason
) {
   public String getFormat() {
      return String.format(Locale.ROOT, "%s key=%d deps=%d reason=%s", this.kind, this.semanticKey, this.dependencyCells.length, this.reason);
   }

   public static long packTransitionKey(long from, long to, int transitionKind) {
      return from * 1099511628211L ^ Long.rotateLeft(to, 31) ^ transitionKind * 2870177450012600261L;
   }

   public boolean canDependencyCells(LongUnaryOperator stateOf) {
      return this.dependencyCells.length == 0 || packStateKey(this.dependencyCells, stateOf) == this.dependencyFingerprint;
   }

   public static long packTargetStandKey(long target, long stand) {
      return 2585217853L ^ target * -7046029254386353131L ^ Long.rotateLeft(stand * -4417276706812531889L, 29);
   }

   public long[] getCellsCopy() {
      return (long[])this.dependencyCells.clone();
   }

   public DepsReasonData(
      DepsReasonData.Kind kind,
      long semanticKey,
      long target,
      long stand,
      int transitionKind,
      long support,
      long accessFamily,
      long dependencyFingerprint,
      long[] dependencyCells,
      String reason
   ) {
      dependencyCells = dependencyCells == null ? new long[0] : (long[])dependencyCells.clone();
      reason = reason != null && !reason.isBlank() ? reason : kind.name();
      this.kind = kind;
      this.semanticKey = semanticKey;
      this.target = target;
      this.stand = stand;
      this.transitionKind = transitionKind;
      this.support = support;
      this.accessFamily = accessFamily;
      this.dependencyFingerprint = dependencyFingerprint;
      this.dependencyCells = dependencyCells;
      this.reason = reason;
   }

   public static long packStateKey(long[] stateOf, LongUnaryOperator cells) {
      long depsReasonValue = -743237448146434599L;
      long depsReasonValue2 = -3750763034362895579L;
      long[] stateOf2 = stateOf;
      long depsReasonValue3 = depsReasonValue ^ ((long)stateOf.length << 32 ^ depsReasonValue) & -4294967296L;

      for (long i = depsReasonValue3 ^ (0L ^ depsReasonValue3) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
         long depsReasonValue4 = stateOf2[(int)i];
         depsReasonValue2 = (depsReasonValue2 ^ depsReasonValue4 * -7046029254386353131L + cells.applyAsLong(depsReasonValue4)) * 1099511628211L;
      }

      return depsReasonValue2;
   }

   public static enum Kind {
      EXECUTOR_POSE_FORBIDDEN,
      ROUTE_EDGE_FORBIDDEN,
      ACCESS_FAMILY_BLOCKED,
      INTERACTION_CERTIFICATE_FORBIDDEN,
      EGRESS_TRANSITION_FORBIDDEN,
      MISSION_STAND_UNREACHABLE;
   }

   public static enum Use {
      DURING_SEARCH,
      AT_FINAL_BOUNDARY;
   }
}
