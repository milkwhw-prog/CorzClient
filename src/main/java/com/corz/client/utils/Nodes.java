package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Nodes {
   private final List<Nodes.Step> copyOf;
   private final double totalAmount;
   private final Nodes.Kind runSideType;
   private final List<Long> copyOfString;
   private final int entryNodes;

   public boolean isEmpty() {
      return this.copyOf.isEmpty();
   }

   public Nodes(Nodes.Kind runSideType, List<Nodes.Step> list, List<Long> arrayList, int entryNodes2, double amount) {
      this.runSideType = runSideType;
      this.copyOf = List.copyOf(list);
      this.copyOfString = List.copyOf(arrayList);
      this.entryNodes = entryNodes2;
      this.totalAmount = amount;
   }

   public List<Long> getTarget() {
      ArrayList arrayList = new ArrayList(this.copyOf.size());

      for (Nodes.Step step : this.copyOf) {
         arrayList.add(step.target());
      }

      return arrayList;
   }

   @Override
   public String toString() {
      return this.runSideType
         + "{confirms="
         + this.getValue()
         + " stands="
         + this.getStand().size()
         + " entryNodes="
         + this.entryNodes
         + " temp="
         + this.copyOfString.size()
         + " secs="
         + String.format("%.1f", this.totalAmount)
         + " rate="
         + String.format("%.2f", this.getTotalAmount())
         + "}";
   }

   public double getAmount() {
      return this.totalAmount;
   }

   public Set<Long> getStand() {
      LinkedHashSet linkedHashSet = new LinkedHashSet();

      for (Nodes.Step step : this.copyOf) {
         linkedHashSet.add(step.stand());
      }

      return linkedHashSet;
   }

   public int getValue() {
      int nodesCounter = 0;

      for (Nodes.Step step : this.copyOf) {
         if (!step.isPlaceKind()) {
            nodesCounter++;
         }
      }

      return nodesCounter;
   }

   public List<Nodes.Step> getCopyOf() {
      return this.copyOf;
   }

   public int getEntryNodes() {
      return this.entryNodes;
   }

   public double getTotalAmount() {
      return this.totalAmount <= 0.0 ? 0.0 : this.getValue() / this.totalAmount;
   }

   public int getValueCopyOf() {
      int nodesCounter = 0;

      for (Nodes.Step step : this.copyOf) {
         if (step.isPlaceKind()) {
            nodesCounter++;
         }
      }

      return nodesCounter;
   }

   public List<Long> getCopyOfString() {
      return this.copyOfString;
   }

   public Nodes.Kind getRunSideType() {
      return this.runSideType;
   }

   public static enum Kind {
      STATIC_STAND_BATCH,
      LOCAL_COMPONENT_RUN,
      SIDE_ROW_RUN,
      SELF_CHAINING_SIDE_RUN,
      ACCESS_CLUSTER_RUN;
   }

   public record Step(long stand, long target, Nodes.StepKind kind) {
      public boolean isPlaceKind() {
         return this.kind == Nodes.StepKind.TEMP_PLACE;
      }

      public Step(long stand, long target) {
         this(stand, target, Nodes.StepKind.SCHEMATIC_PLACE);
      }
   }

   public static enum StepKind {
      SCHEMATIC_PLACE,
      TEMP_PLACE;
   }
}
