package com.corz.client.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;
import com.corz.client.utils.DeltaHeadroomUtils;

public final class TimeFlat {
   private final Map<TimeFlat.Edge, Long> containsPositionMap = new HashMap<>();
   private long flatSequence;

   public int getValue(ToLongFunction<TimeFlat.Edge> toLongFunction) {
      int flatCounter = 0;
      Iterator iterator = this.containsPositionMap.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if ((Long)entry.getValue() != toLongFunction.applyAsLong((TimeFlat.Edge)entry.getKey())) {
            iterator.remove();
            flatCounter++;
         }
      }

      return flatCounter;
   }

   public boolean canTime(TimeFlat.Edge edge, long flatValue) {
      Long long2 = this.containsPositionMap.get(edge);
      if (long2 == null) {
         return false;
      } else if (long2 != flatValue) {
         this.containsPositionMap.remove(edge);
         return false;
      } else {
         this.flatSequence++;
         return true;
      }
   }

   public int getValue() {
      return this.containsPositionMap.size();
   }

   public int getValue(LongPredicate longPredicate) {
      int mapSize = this.containsPositionMap.size();
      this.containsPositionMap.keySet().removeIf(fromx -> longPredicate.test(fromx.from()));
      return mapSize - this.containsPositionMap.size();
   }

   public static DeltaHeadroomUtils.Kind getWalkFlat(DeltaHeadroomUtils.Kind kind, int walkFlatValue, int walkFlatLimit, int j) {
      int abs3 = Math.abs(walkFlatValue);
      int absKind = Math.abs(j);
      if (abs3 + absKind != 2 || abs3 != 2 && absKind != 2) {
         if (walkFlatLimit > 0) {
            return DeltaHeadroomUtils.Kind.STEP_UP;
         } else if (walkFlatLimit < 0) {
            return DeltaHeadroomUtils.Kind.DROP;
         } else {
            return abs3 == 1 && absKind == 1 ? DeltaHeadroomUtils.Kind.WALK_DIAGONAL : DeltaHeadroomUtils.Kind.WALK_FLAT;
         }
      } else {
         return DeltaHeadroomUtils.Kind.GAP_JUMP;
      }
   }

   public boolean isContainsKey(TimeFlat.Edge edge) {
      return this.containsPositionMap.containsKey(edge);
   }

   public static long getTime(long flatValue, long proofSupportValue, LongUnaryOperator longUnaryOperator) {
      int flatBound = ProofSupportUtils.getValueInt(flatValue);
      int intersectsCenterBound = ProofSupportUtils.getValue(flatValue);
      int valueForLong = ProofSupportUtils.getValueForLong(flatValue);
      int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
      int spotBound = ProofSupportUtils.getValue(proofSupportValue);
      int valueForLong6 = ProofSupportUtils.getValueForLong(proofSupportValue);
      long flatValue3 = -3750763034362895579L;

      for (int i = Math.min(flatBound, valueInt) - 1; i <= Math.max(flatBound, valueInt) + 1; i++) {
         for (int j = Math.min(intersectsCenterBound, spotBound) - 2; j <= Math.max(intersectsCenterBound, spotBound) + 3; j++) {
            for (int k = Math.min(valueForLong, valueForLong6) - 1; k <= Math.max(valueForLong, valueForLong6) + 1; k++) {
               flatValue3 = (flatValue3 ^ longUnaryOperator.applyAsLong(ProofSupportUtils.getTime(i, j, k))) * 1099511628211L;
            }
         }
      }

      return flatValue3;
   }

   public void handle(TimeFlat.Edge edge, long flatValue) {
      this.containsPositionMap.put(edge, flatValue);
   }

   public long getTime() {
      return this.flatSequence;
   }

   public void updateTime() {
      this.containsPositionMap.clear();
      this.flatSequence = 0L;
   }

   public record Edge(long from, long to, DeltaHeadroomUtils.Kind transition, TimeFlat.Mode mode) {
   }

   public static enum Mode {
      SURFACE_WALK,
      WORLD_WALK;
   }
}
