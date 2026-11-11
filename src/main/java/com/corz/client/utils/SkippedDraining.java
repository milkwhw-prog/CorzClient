package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.IntPredicate;
import com.corz.client.utils.LaneRowUtils;

public final class SkippedDraining {
   private SkippedDraining.Phase drainingAbortedType = SkippedDraining.Phase.ENTER;
   private final int[] skippedDrainingTable;
   private final int[] unplacedDrainingTable;
   public int sideRunCount;
   public int sideRunsHandled;
   private int sideRunValue = -1000;
   public static final int SKIPPED_CODE_PRIMARY = 1;
   private String unplacedSkippedText = "";
   private int drainingAbortedValue;
   public int skippedDrainingCount;
   private int skippedDrainingValue;
   private final byte[] cellStates;
   public int unplacedDrainingCount;
   public int unplacedSkippedCount;
   private final LaneRowUtils.Run unplacedSkippedRun;
   private final int[] unplacedDrainingSeenCounts;
   private final SkippedDraining.MemberState[] unplacedSkippedTypes;
   public int unplacedFirstSeenCount;

   public LaneRowUtils.Run getUnplacedSkippedRun() {
      return this.unplacedSkippedRun;
   }

   public void handleUnplacedDraining(int value, IntPredicate intPredicate) {
      for (int i = 0; i < this.unplacedSkippedTypes.length; i++) {
         if (this.unplacedSkippedTypes[i] == SkippedDraining.MemberState.PENDING) {
            if (intPredicate.test(i)) {
               this.unplacedSkippedTypes[i] = SkippedDraining.MemberState.CONFIRMED;
               this.unplacedFirstSeenCount++;
               this.drainingAbortedValue = value;
            } else if (value - this.unplacedDrainingTable[i] > 30) {
               if (this.unplacedDrainingSeenCounts[i]++ < 1) {
                  this.unplacedSkippedTypes[i] = SkippedDraining.MemberState.UNPLACED;
                  this.unplacedDrainingTable[i] = 0;
                  this.unplacedDrainingCount++;
               } else {
                  this.unplacedSkippedTypes[i] = SkippedDraining.MemberState.SKIPPED;
                  this.unplacedSkippedCount++;
               }
            }
         }
      }

      boolean unplacedDrainingFlag = false;
      boolean unplacedDrainingFlag2 = false;

      for (SkippedDraining.MemberState memberState : this.unplacedSkippedTypes) {
         if (memberState == SkippedDraining.MemberState.UNPLACED) {
            unplacedDrainingFlag = true;
         }

         if (memberState == SkippedDraining.MemberState.PENDING) {
            unplacedDrainingFlag2 = true;
         }
      }

      if (this.drainingAbortedType == SkippedDraining.Phase.RUNNING && !unplacedDrainingFlag) {
         this.drainingAbortedType = unplacedDrainingFlag2 ? SkippedDraining.Phase.DRAINING : SkippedDraining.Phase.COMPLETE;
      }

      if (this.drainingAbortedType == SkippedDraining.Phase.DRAINING && !unplacedDrainingFlag2) {
         this.drainingAbortedType = SkippedDraining.Phase.COMPLETE;
      }
   }

   public int getUnplacedSkipped() {
      for (int i = 0; i < this.unplacedSkippedTypes.length; i++) {
         if (this.unplacedSkippedTypes[i] == SkippedDraining.MemberState.UNPLACED) {
            return i;
         }
      }

      return -1;
   }

   public int getValue(int index) {
      return index >= 0 && index < this.unplacedDrainingTable.length && this.unplacedDrainingTable[index] > 0 ? 1 : 0;
   }

   public double getUnplacedSkippedUnplacedSkippedTypes() {
      for (int i = 0; i < this.unplacedSkippedTypes.length; i++) {
         if (this.unplacedSkippedTypes[i] == SkippedDraining.MemberState.UNPLACED) {
            return this.getMembersLane(i);
         }
      }

      long laneEnd = this.unplacedSkippedRun.laneEnd();
      return (this.unplacedSkippedRun.axis() == 0 ? ProofSupportUtils.getValueInt(laneEnd) : ProofSupportUtils.getValueForLong(laneEnd)) + 0.5;
   }

   public void handleUnplacedSkipped(int index, String text) {
      if (this.unplacedSkippedTypes[index] == SkippedDraining.MemberState.UNPLACED || this.unplacedSkippedTypes[index] == SkippedDraining.MemberState.PENDING) {
         this.unplacedSkippedTypes[index] = SkippedDraining.MemberState.SKIPPED;
         this.unplacedSkippedCount++;
         this.unplacedSkippedText = text;
      }
   }

   public void handle(int index) {
      if (this.cellStates[index] == 1) {
         this.cellStates[index] = 2;
      }
   }

   public List<Long> getUnplacedSkippedList() {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < this.unplacedSkippedTypes.length; i++) {
         if (this.unplacedSkippedTypes[i] != SkippedDraining.MemberState.CONFIRMED) {
            arrayList.add(this.unplacedSkippedRun.members().get(i).target());
         }
      }

      return arrayList;
   }

   public void handleDrainingAborted(int value) {
      this.drainingAbortedType = SkippedDraining.Phase.RUNNING;
      this.drainingAbortedValue = value;
   }

   public SkippedDraining.Phase getDrainingAbortedType() {
      return this.drainingAbortedType;
   }

   public int getUnplacedSkippedInt() {
      int unplacedSkipped = 0;

      for (SkippedDraining.MemberState memberState : this.unplacedSkippedTypes) {
         if (memberState == SkippedDraining.MemberState.UNPLACED) {
            unplacedSkipped++;
         }
      }

      return unplacedSkipped;
   }

   private double getMembersLane(int index) {
      long laneCell = this.unplacedSkippedRun.members().get(index).laneCell();
      return (this.unplacedSkippedRun.axis() == 0 ? ProofSupportUtils.getValueInt(laneCell) : ProofSupportUtils.getValueForLong(laneCell)) + 0.5;
   }

   public String getText() {
      return this.unplacedSkippedText;
   }

   public void handleSideRun(int index, int value) {
      this.unplacedSkippedTypes[index] = SkippedDraining.MemberState.PENDING;
      this.unplacedDrainingTable[index] = value;
      this.sideRunValue = value;
      this.sideRunCount++;
      if (this.unplacedSkippedRun.members().get(index).directional()) {
         this.sideRunsHandled++;
      }

      this.drainingAbortedValue = value;
   }

   public int getUnplacedSkipped5() {
      int unplacedSkipped = 0;

      for (SkippedDraining.MemberState memberState : this.unplacedSkippedTypes) {
         if (memberState == SkippedDraining.MemberState.UNPLACED || memberState == SkippedDraining.MemberState.PENDING) {
            unplacedSkipped++;
         }
      }

      return unplacedSkipped;
   }

   public int getUnplacedSkipped(int index) {
      return index >= 0 && index < this.unplacedSkippedTypes.length && this.unplacedSkippedTypes[index] == SkippedDraining.MemberState.CONFIRMED ? 1 : 0;
   }

   public double getAxis(double axisValue, double axisValue2) {
      return this.unplacedSkippedRun.axis() == 0 ? axisValue : axisValue2;
   }

   public SkippedDraining(LaneRowUtils.Run data) {
      this.unplacedSkippedRun = data;
      this.unplacedSkippedTypes = new SkippedDraining.MemberState[data.getMembers()];
      this.unplacedDrainingTable = new int[data.getMembers()];
      this.unplacedDrainingSeenCounts = new int[data.getMembers()];
      this.skippedDrainingTable = new int[data.getMembers()];
      this.cellStates = new byte[data.getMembers()];
      Arrays.fill(this.unplacedSkippedTypes, SkippedDraining.MemberState.UNPLACED);
      Arrays.fill(this.skippedDrainingTable, -1000);
   }

   public void handleAbortedDraining(String text) {
      this.drainingAbortedType = SkippedDraining.Phase.ABORTED;
      this.unplacedSkippedText = text;
   }

   public int getValueForInt(int index) {
      return this.skippedDrainingTable[index];
   }

   public double getDirection(int directionValue, double axisValue, double directionValue3) {
      return (this.getMembersLane(directionValue) - this.getAxis(axisValue, directionValue3)) * this.unplacedSkippedRun.direction();
   }

   public void handle(int index, int skippedDrainingValue) {
      if (this.cellStates[index] != 1) {
         this.cellStates[index] = 1;
         this.skippedDrainingCount++;
      }

      this.skippedDrainingTable[index] = skippedDrainingValue;
   }

   public boolean canDrainingAborted(int drainingAbortedValue, int drainingAbortedValue2) {
      return this.drainingAbortedType == SkippedDraining.Phase.RUNNING && drainingAbortedValue - this.drainingAbortedValue > drainingAbortedValue2;
   }

   public boolean canFlag(int skippedDrainingValue) {
      return skippedDrainingValue - this.sideRunValue >= 4;
   }

   public void setValueForInt(int value) {
      this.drainingAbortedValue = value;
   }

   public int getValueUnplacedDrainingTable(int index) {
      return this.unplacedDrainingTable[index];
   }

   public boolean canLaneCell(int index) {
      LaneRowUtils.Member member = this.unplacedSkippedRun.members().get(index);
      int laneCellValue = this.unplacedSkippedRun.axis() == 0
         ? ProofSupportUtils.getValueForLong(member.target()) - ProofSupportUtils.getValueForLong(member.laneCell())
         : ProofSupportUtils.getValueInt(member.target()) - ProofSupportUtils.getValueInt(member.laneCell());
      return Math.abs(laneCellValue) >= 2;
   }

   public boolean canFlagForInt(int index) {
      return this.cellStates[index] == 1;
   }

   public int getUnplacedSkipped(double unplacedSkippedValue, double unplacedSkippedValue2) {
      double axis = this.getAxis(unplacedSkippedValue, unplacedSkippedValue2);

      for (int i = 0; i < this.unplacedSkippedTypes.length; i++) {
         if (this.unplacedSkippedTypes[i] == SkippedDraining.MemberState.UNPLACED) {
            double unplacedSkippedLimit = (this.getMembersLane(i) - axis) * this.unplacedSkippedRun.direction();
            boolean laneCell = this.canLaneCell(i);
            double unplacedSkippedLimit2 = laneCell ? 1.5 : 2.2;
            double unplacedSkippedValue3 = laneCell ? 1.3 : 1.6;
            if (unplacedSkippedLimit >= -unplacedSkippedValue3 && unplacedSkippedLimit <= unplacedSkippedLimit2) {
               return i;
            }

            if (unplacedSkippedLimit > unplacedSkippedLimit2) {
               return -1;
            }
         }
      }

      return -1;
   }

   public String getFormat(int skippedDrainingValue) {
      return String.format(
         Locale.ROOT,
         "kind=%s phase=%s members=%d placements=%d clicks=%d directionalClicks=%d sneakPulses=%d failures=%d skips=%d remaining=%d axis=%d dir=%d note=%s",
         this.unplacedSkippedRun.kind(),
         this.drainingAbortedType,
         this.unplacedSkippedRun.getMembers(),
         this.unplacedFirstSeenCount,
         this.sideRunCount,
         this.sideRunsHandled,
         this.skippedDrainingCount,
         this.unplacedDrainingCount,
         this.unplacedSkippedCount,
         this.getUnplacedSkipped5(),
         this.unplacedSkippedRun.axis(),
         this.unplacedSkippedRun.direction(),
         this.unplacedSkippedText
      );
   }

   public SkippedDraining.MemberState getUnplacedSkippedForInt(int index) {
      return this.unplacedSkippedTypes[index];
   }

   public static enum MemberState {
      UNPLACED,
      PENDING,
      CONFIRMED,
      FAILED,
      SKIPPED;
   }

   public static enum Phase {
      ENTER,
      RUNNING,
      DRAINING,
      COMPLETE,
      ABORTED;
   }
}
