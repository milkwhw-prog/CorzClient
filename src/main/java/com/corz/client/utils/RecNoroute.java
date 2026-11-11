package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import com.corz.client.utils.DepsReasonData;

public final class RecNoroute {
   private int totalDependencyCount;
   private final int[] recBoundaryValues;
   private final Map<DepsReasonData.Kind, LinkedHashMap<Long, DepsReasonData>> ifAbsentDepsReasonDataMap = new LinkedHashMap<>();
   private final int[] recBoundaryTable;
   private int recBoundaryCount;
   private final int[] semanticDependencyTable = new int[DepsReasonData.Kind.values().length];

   public int getValue(LongUnaryOperator longUnaryOperator) {
      int recNorouteTotal = 0;

      for (DepsReasonData.Kind kind : DepsReasonData.Kind.values()) {
         recNorouteTotal += this.getValue(kind, longUnaryOperator);
      }

      return recNorouteTotal;
   }

   public int getTarget(DepsReasonData.Kind kind, LongPredicate longPredicate) {
      LinkedHashMap ifAbsent = this.getIfAbsent(kind);
      ArrayList arrayList = new ArrayList();

      for (Entry entry : (Iterable<java.util.Map.Entry>) (ifAbsent.entrySet())) {
         if (longPredicate.test(((DepsReasonData)entry.getValue()).target())) {
            arrayList.add((Long)entry.getKey());
         }
      }

      for (long targetValue : (Iterable<Long>) (arrayList)) {
         ifAbsent.remove(targetValue);
      }

      this.totalDependencyCount = this.totalDependencyCount + arrayList.size();
      return arrayList.size();
   }

   private LinkedHashMap<Long, DepsReasonData> getIfAbsent(DepsReasonData.Kind kind) {
      return this.ifAbsentDepsReasonDataMap.computeIfAbsent(kind, kind2 -> new LinkedHashMap<>());
   }

   public int getValue(DepsReasonData.Kind kind) {
      int ifabsentSize = this.getIfAbsent(kind).size();
      this.getIfAbsent(kind).clear();
      this.totalDependencyCount += ifabsentSize;
      return ifabsentSize;
   }

   public List<Long> getItems(DepsReasonData.Kind kind) {
      return new ArrayList<>(this.getIfAbsent(kind).keySet());
   }

   public boolean canFlag(DepsReasonData.Kind kind, long recNorouteValue, LongUnaryOperator longUnaryOperator, DepsReasonData.Use use) {
      return this.getRecBoundary(kind, recNorouteValue, longUnaryOperator, use) != null;
   }

   private static String formatNoroutePose(DepsReasonData.Kind kind) {
      return switch (kind) {
         case EXECUTOR_POSE_FORBIDDEN -> "pose";
         case ROUTE_EDGE_FORBIDDEN -> "edge";
         case ACCESS_FAMILY_BLOCKED -> "access";
         case INTERACTION_CERTIFICATE_FORBIDDEN -> "click";
         case EGRESS_TRANSITION_FORBIDDEN -> "egress";
         case MISSION_STAND_UNREACHABLE -> "noroute";
      };
   }

   public String getFormat() {
      StringBuilder stringBuilder = new StringBuilder();

      for (DepsReasonData.Kind kind : DepsReasonData.Kind.values()) {
         if (this.semanticDependencyTable[kind.ordinal()] != 0 || this.recBoundaryValues[kind.ordinal()] != 0 || this.recBoundaryTable[kind.ordinal()] != 0) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(' ');
            }

            stringBuilder.append(
               String.format(
                  Locale.ROOT,
                  "%s{rec=%d search=%d boundary=%d live=%d}",
                  formatNoroutePose(kind),
                  this.semanticDependencyTable[kind.ordinal()],
                  this.recBoundaryValues[kind.ordinal()],
                  this.recBoundaryTable[kind.ordinal()],
                  this.getValueForKind(kind)
               )
            );
         }
      }

      return String.format(
         Locale.ROOT,
         "constraintsRecorded=%d constraintsUsedDuringSearch=%d constraintsUsedAtFinalBoundary=%d constraintSearchShare=%.3f constraintReopens=%d constraintResproofs=%d %s",
         this.getRecBoundary(),
         this.getRecBoundaryRecBoundaryValues(),
         this.getRecBoundaryRecBoundaryTable(),
         this.getAmount(),
         this.totalDependencyCount,
         this.recBoundaryCount,
         stringBuilder
      );
   }

   public int getRecBoundary(DepsReasonData.Kind kind) {
      return this.recBoundaryTable[kind.ordinal()];
   }

   public void updateRecBoundary() {
      this.ifAbsentDepsReasonDataMap.clear();
      Arrays.fill(this.semanticDependencyTable, 0);
      Arrays.fill(this.recBoundaryValues, 0);
      Arrays.fill(this.recBoundaryTable, 0);
      this.totalDependencyCount = this.recBoundaryCount = 0;
   }

   public int getValueForKind(DepsReasonData.Kind kind) {
      return this.getIfAbsent(kind).size();
   }

   public RecNoroute() {
      this.recBoundaryValues = new int[DepsReasonData.Kind.values().length];
      this.recBoundaryTable = new int[DepsReasonData.Kind.values().length];
   }

   public int getRecBoundaryForKind(DepsReasonData.Kind kind) {
      return this.recBoundaryValues[kind.ordinal()];
   }

   public void handleRecBoundary(DepsReasonData.Kind kind, int recBoundaryLimit) {
      if (recBoundaryLimit > 0) {
         this.recBoundaryValues[kind.ordinal()] += recBoundaryLimit;
      }
   }

   public int getRecBoundaryCount() {
      return this.recBoundaryCount;
   }

   public DepsReasonData getRecBoundary(DepsReasonData.Kind kind, long recBoundaryValue, LongUnaryOperator longUnaryOperator, DepsReasonData.Use use) {
      DepsReasonData ifabsent = this.getIfAbsent(kind).get(recBoundaryValue);
      if (ifabsent != null && ifabsent.canDependencyCells(longUnaryOperator)) {
         if (use == DepsReasonData.Use.DURING_SEARCH) {
            this.recBoundaryValues[kind.ordinal()]++;
         } else {
            this.recBoundaryTable[kind.ordinal()]++;
         }

         return ifabsent;
      } else {
         return null;
      }
   }

   public double getAmount() {
      int recNorouteDivisor = this.getRecBoundaryRecBoundaryValues() + this.getRecBoundaryRecBoundaryTable();
      return recNorouteDivisor == 0 ? -1.0 : (double)this.getRecBoundaryRecBoundaryValues() / recNorouteDivisor;
   }

   public int getRecBoundarySemanticDependencyTable(DepsReasonData.Kind kind) {
      return this.semanticDependencyTable[kind.ordinal()];
   }

   public int getRecBoundary() {
      int recBoundary = 0;

      for (int recBoundaryValue : this.semanticDependencyTable) {
         recBoundary += recBoundaryValue;
      }

      return recBoundary;
   }

   public int getValue() {
      return this.totalDependencyCount;
   }

   public int getValue(DepsReasonData.Kind kind, LongUnaryOperator longUnaryOperator) {
      LinkedHashMap ifAbsent = this.getIfAbsent(kind);
      ArrayList arrayList = new ArrayList();

      for (Entry entry : (Iterable<java.util.Map.Entry>) (ifAbsent.entrySet())) {
         if (!((DepsReasonData)entry.getValue()).canDependencyCells(longUnaryOperator)) {
            arrayList.add((Long)entry.getKey());
         }
      }

      for (long recNorouteValue : (Iterable<Long>) (arrayList)) {
         ifAbsent.remove(recNorouteValue);
      }

      this.totalDependencyCount = this.totalDependencyCount + arrayList.size();
      return arrayList.size();
   }

   public boolean canSemanticDependency(DepsReasonData depsReasonData) {
      if (depsReasonData == null) {
         return false;
      } else {
         LinkedHashMap ifAbsent = this.getIfAbsent(depsReasonData.kind());
         DepsReasonData depsReasonData2 = (DepsReasonData)ifAbsent.get(depsReasonData.semanticKey());
         if (depsReasonData2 != null && depsReasonData2.dependencyFingerprint() == depsReasonData.dependencyFingerprint()) {
            this.recBoundaryCount++;
            return false;
         } else {
            if (depsReasonData2 == null && ifAbsent.size() >= 1024) {
               Iterator iterator = ifAbsent.keySet().iterator();
               iterator.next();
               iterator.remove();
            }

            ifAbsent.put(depsReasonData.semanticKey(), depsReasonData);
            this.semanticDependencyTable[depsReasonData.kind().ordinal()]++;
            return true;
         }
      }
   }

   public int getRecBoundaryRecBoundaryValues() {
      int recBoundary = 0;

      for (int recBoundaryValue : this.recBoundaryValues) {
         recBoundary += recBoundaryValue;
      }

      return recBoundary;
   }

   public int getRecBoundaryRecBoundaryTable() {
      int recBoundary = 0;

      for (int recBoundaryValue : this.recBoundaryTable) {
         recBoundary += recBoundaryValue;
      }

      return recBoundary;
   }
}
