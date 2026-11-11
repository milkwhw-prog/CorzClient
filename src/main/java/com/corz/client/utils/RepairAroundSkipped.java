package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import com.corz.client.utils.ProofSupportUtils;

public final class RepairAroundSkipped {
   private int occurrenceCount;
   private final RepairAroundSkipped.Kind runRowType;
   private int skippedInvalidatedCount;
   private boolean noneFlag;
   private RepairAroundSkipped.YieldReason opportunitySuperiorType;
   private int invalidatedSkippedCount;
   private double totalRepairAmount;
   private String repairSuffixText;
   private int repairAroundCount;
   private final int repairAroundValue;
   private int repairAround;
   private double accumulatedAmount;
   private final List<Long> unmodifiableList;
   private final String[] skippedInvalidatedTexts;
   private final int baselineTick;
   private final List<RepairAroundSkipped.Step> unmodifiableStepList;
   private final RepairAroundSkipped.StepState[] suffixRepairSkippedInvalidatedTypes;
   private int updateCount;
   private final long creationTimeMs;
   private int indexRepairSuffix;
   private final long startTimeMs;
   private final Map<Long, Integer> skippedInvalidatedIntegerMap = new HashMap<>();

   public long getTime() {
      return this.creationTimeMs;
   }

   public RepairAroundSkipped.Step getUnmodifiable(long unmodifiableValue) {
      Integer index = this.skippedInvalidatedIntegerMap.get(unmodifiableValue);
      return index == null ? null : this.unmodifiableStepList.get(index);
   }

   public void update() {
      this.updateCount++;
   }

   public String getText() {
      return this.repairSuffixText;
   }

   public boolean isNoneFlag() {
      return this.noneFlag || this.getSuffixSkippedSuffixRepairSkippedInvalidatedTypes() == 0;
   }

   public List<Long> getUnmodifiableList() {
      return this.unmodifiableList;
   }

   public boolean canInvalidatedSkipped(long invalidatedSkippedValue, String invalidatedSkippedText, int repairAround) {
      Integer index = this.skippedInvalidatedIntegerMap.get(invalidatedSkippedValue);
      if (index != null && this.suffixRepairSkippedInvalidatedTypes[index] != RepairAroundSkipped.StepState.PLACED) {
         if (this.suffixRepairSkippedInvalidatedTypes[index] != RepairAroundSkipped.StepState.INVALIDATED) {
            this.skippedInvalidatedCount++;
         }

         if (this.suffixRepairSkippedInvalidatedTypes[index] == RepairAroundSkipped.StepState.SKIPPED) {
            this.invalidatedSkippedCount--;
         }

         this.suffixRepairSkippedInvalidatedTypes[index] = RepairAroundSkipped.StepState.INVALIDATED;
         this.skippedInvalidatedTexts[index] = invalidatedSkippedText == null ? "" : invalidatedSkippedText;
         if (this.indexRepairSuffix == index) {
            this.indexRepairSuffix = -1;
         }

         this.repairAround = repairAround;
         return true;
      } else {
         return false;
      }
   }

   public boolean isNoneFlag2() {
      return this.noneFlag;
   }

   public RepairAroundSkipped.YieldReason getOpportunitySuperiorType() {
      return this.opportunitySuperiorType;
   }

   public RepairAroundSkipped(
      long time, RepairAroundSkipped.Kind runRowType, long time2, List<RepairAroundSkipped.Step> list, List<Long> list2, int value, int value2
   ) {
      this.indexRepairSuffix = -1;
      this.repairSuffixText = "";
      this.creationTimeMs = time;
      this.runRowType = runRowType;
      this.startTimeMs = time2;
      ArrayList arrayList = new ArrayList(list == null ? List.of() : list);
      this.unmodifiableStepList = Collections.unmodifiableList(arrayList);
      this.unmodifiableList = Collections.unmodifiableList(new ArrayList<>(list2 == null ? List.of() : list2));
      this.repairAroundValue = value;
      this.baselineTick = value2;
      this.repairAround = value2;
      this.suffixRepairSkippedInvalidatedTypes = new RepairAroundSkipped.StepState[arrayList.size()];
      this.skippedInvalidatedTexts = new String[arrayList.size()];

      for (int i = 0; i < arrayList.size(); i++) {
         this.suffixRepairSkippedInvalidatedTypes[i] = RepairAroundSkipped.StepState.PENDING;
         this.skippedInvalidatedIntegerMap.putIfAbsent(((RepairAroundSkipped.Step)arrayList.get(i)).target(), i);
      }
   }

   public RepairAroundSkipped.StepState getSuffixSkipped(long suffixSkippedValue) {
      Integer index = this.skippedInvalidatedIntegerMap.get(suffixSkippedValue);
      return index == null ? null : this.suffixRepairSkippedInvalidatedTypes[index];
   }

   public int getRepairAround() {
      return this.repairAround;
   }

   public long getStartTimeMs() {
      return this.startTimeMs;
   }

   public void handleAmount(double repairAroundLimit) {
      if (repairAroundLimit > 0.0) {
         this.totalRepairAmount += repairAroundLimit;
      }
   }

   public int getRepairAround(long repairAroundValue, int repairAroundLimit, Predicate<RepairAroundSkipped.Step> predicate) {
      int repairAround = 0;

      for (int i = 0; i < this.suffixRepairSkippedInvalidatedTypes.length; i++) {
         if (this.suffixRepairSkippedInvalidatedTypes[i] == RepairAroundSkipped.StepState.PENDING) {
            RepairAroundSkipped.Step unmodifiablelist = this.unmodifiableStepList.get(i);
            if ((
                  canFlag(repairAroundValue, unmodifiablelist.target(), repairAroundLimit)
                     || canFlag(repairAroundValue, unmodifiablelist.stand(), repairAroundLimit)
                     || canFlag(repairAroundValue, unmodifiablelist.support(), repairAroundLimit)
               )
               && !predicate.test(unmodifiablelist)) {
               this.canInvalidatedSkipped(unmodifiablelist.target(), "repair_around", this.repairAround);
               repairAround++;
            }
         }
      }

      return repairAround;
   }

   public RepairAroundSkipped.Step getSuffixSkipped() {
      if (this.indexRepairSuffix >= 0) {
         return this.unmodifiableStepList.get(this.indexRepairSuffix);
      } else {
         for (int i = 0; i < this.suffixRepairSkippedInvalidatedTypes.length; i++) {
            if (this.suffixRepairSkippedInvalidatedTypes[i] == RepairAroundSkipped.StepState.PENDING) {
               return this.unmodifiableStepList.get(i);
            }
         }

         return null;
      }
   }

   public int getRepairSuffix(Predicate<RepairAroundSkipped.Step> predicate) {
      int repairSuffix = 0;

      for (int i = 0; i < this.suffixRepairSkippedInvalidatedTypes.length; i++) {
         if (this.suffixRepairSkippedInvalidatedTypes[i] == RepairAroundSkipped.StepState.PENDING && !predicate.test(this.unmodifiableStepList.get(i))) {
            this.canInvalidatedSkipped(this.unmodifiableStepList.get(i).target(), "repair_suffix", this.repairAround);
            repairSuffix++;
         }
      }

      return repairSuffix;
   }

   public int getInvalidatedSkippedCount() {
      return this.invalidatedSkippedCount;
   }

   public int getValue() {
      return this.occurrenceCount;
   }

   public int getUpdateCount() {
      return this.updateCount;
   }

   public List<RepairAroundSkipped.Step> getUnmodifiableStepList() {
      return this.unmodifiableStepList;
   }

   public void handleRepairSuffix(long repairSuffixValue, int repairAround) {
      Integer value = this.skippedInvalidatedIntegerMap.get(repairSuffixValue);
      if (value != null) {
         if (this.suffixRepairSkippedInvalidatedTypes[value] == RepairAroundSkipped.StepState.PENDING
            || this.suffixRepairSkippedInvalidatedTypes[value] == RepairAroundSkipped.StepState.ACTIVE) {
            if (this.indexRepairSuffix >= 0 && this.indexRepairSuffix != value && this.suffixRepairSkippedInvalidatedTypes[this.indexRepairSuffix] == RepairAroundSkipped.StepState.ACTIVE) {
               this.suffixRepairSkippedInvalidatedTypes[this.indexRepairSuffix] = RepairAroundSkipped.StepState.PENDING;
            }

            this.suffixRepairSkippedInvalidatedTypes[value] = RepairAroundSkipped.StepState.ACTIVE;
            this.indexRepairSuffix = value;
            this.repairAround = repairAround;
         }
      }
   }

   public boolean isContainsKey(long containsValue) {
      return this.skippedInvalidatedIntegerMap.containsKey(containsValue);
   }

   public static boolean canFlag(long repairAroundValue, long proofSupportValue, int repairAroundLimit) {
      return Math.abs(ProofSupportUtils.getValueInt(repairAroundValue) - ProofSupportUtils.getValueInt(proofSupportValue)) <= repairAroundLimit
         && Math.abs(ProofSupportUtils.getValue(repairAroundValue) - ProofSupportUtils.getValue(proofSupportValue)) <= repairAroundLimit
         && Math.abs(ProofSupportUtils.getValueForLong(repairAroundValue) - ProofSupportUtils.getValueForLong(proofSupportValue)) <= repairAroundLimit;
   }

   public int getBaselineTick() {
      return this.baselineTick;
   }

   public boolean canSkippedInvalidated(long skippedInvalidatedValue, int repairAround) {
      Integer index = this.skippedInvalidatedIntegerMap.get(skippedInvalidatedValue);
      if (index == null) {
         return false;
      } else {
         if (this.suffixRepairSkippedInvalidatedTypes[index] != RepairAroundSkipped.StepState.PLACED) {
            this.repairAroundCount++;
         }

         if (this.suffixRepairSkippedInvalidatedTypes[index] == RepairAroundSkipped.StepState.INVALIDATED) {
            this.skippedInvalidatedCount--;
         }

         if (this.suffixRepairSkippedInvalidatedTypes[index] == RepairAroundSkipped.StepState.SKIPPED) {
            this.invalidatedSkippedCount--;
         }

         this.suffixRepairSkippedInvalidatedTypes[index] = RepairAroundSkipped.StepState.PLACED;
         this.skippedInvalidatedTexts[index] = null;
         if (this.indexRepairSuffix == index) {
            this.indexRepairSuffix = -1;
         }

         this.repairAround = repairAround;
         return true;
      }
   }

   public void updateOccurrenceCount() {
      this.occurrenceCount++;
   }

   public double getAccumulatedAmount() {
      return this.accumulatedAmount;
   }

   public RepairAroundSkipped.Kind getRunRowType() {
      return this.runRowType;
   }

   public int getUnmodifiable() {
      return this.unmodifiableStepList.size();
   }

   public void handleRepairSuffix(RepairAroundSkipped.YieldReason opportunitySuperiorType, String repairSuffixText, int repairAround) {
      if (!this.noneFlag) {
         this.noneFlag = true;
         this.opportunitySuperiorType = opportunitySuperiorType;
         this.repairSuffixText = repairSuffixText == null ? "" : repairSuffixText;
         if (this.indexRepairSuffix >= 0 && this.suffixRepairSkippedInvalidatedTypes[this.indexRepairSuffix] == RepairAroundSkipped.StepState.ACTIVE) {
            this.suffixRepairSkippedInvalidatedTypes[this.indexRepairSuffix] = RepairAroundSkipped.StepState.PENDING;
         }

         this.indexRepairSuffix = -1;
         this.repairAround = repairAround;
      }
   }

   public boolean canSkippedInvalidated(long skippedInvalidatedValue, String skippedInvalidatedText) {
      Integer index = this.skippedInvalidatedIntegerMap.get(skippedInvalidatedValue);
      if (index != null && this.suffixRepairSkippedInvalidatedTypes[index] != RepairAroundSkipped.StepState.PLACED) {
         if (this.suffixRepairSkippedInvalidatedTypes[index] != RepairAroundSkipped.StepState.SKIPPED) {
            this.invalidatedSkippedCount++;
         }

         if (this.suffixRepairSkippedInvalidatedTypes[index] == RepairAroundSkipped.StepState.INVALIDATED) {
            this.skippedInvalidatedCount--;
         }

         this.suffixRepairSkippedInvalidatedTypes[index] = RepairAroundSkipped.StepState.SKIPPED;
         this.skippedInvalidatedTexts[index] = skippedInvalidatedText == null ? "" : skippedInvalidatedText;
         if (this.indexRepairSuffix == index) {
            this.indexRepairSuffix = -1;
         }

         return true;
      } else {
         return false;
      }
   }

   public int getSkippedInvalidatedCount() {
      return this.skippedInvalidatedCount;
   }

   public int getRepairAroundCount() {
      return this.repairAroundCount;
   }

   public String getFormat(int repairAroundValue) {
      return String.format(
         Locale.ROOT,
         "id=%d kind=%s region=%d members=%d expectedPlacements=%d actualPlacements=%d entryCost=%d travelCost=%.1f rotationCost=%.0f sneakTicks=%d internalMoves=%d invalidated=%d duration=%ds yieldReason=%s",
         this.creationTimeMs,
         this.runRowType,
         this.startTimeMs,
         this.skippedInvalidatedIntegerMap.size(),
         this.unmodifiableStepList.size(),
         this.repairAroundCount,
         this.repairAroundValue,
         this.accumulatedAmount,
         this.totalRepairAmount,
         this.updateCount,
         this.occurrenceCount,
         this.skippedInvalidatedCount,
         Math.max(0, repairAroundValue - this.baselineTick) / 20,
         this.noneFlag && this.opportunitySuperiorType != null ? this.opportunitySuperiorType.name() : "NONE"
      );
   }

   public void handleAmountForDouble(double repairAroundLimit) {
      if (repairAroundLimit > 0.0) {
         this.accumulatedAmount += repairAroundLimit;
      }
   }

   public String formatTexts(long repairAroundValue) {
      Integer index = this.skippedInvalidatedIntegerMap.get(repairAroundValue);
      return index == null ? null : this.skippedInvalidatedTexts[index];
   }

   public int getSuffixSkippedSuffixRepairSkippedInvalidatedTypes() {
      int suffixSkipped = 0;

      for (RepairAroundSkipped.StepState stepState : this.suffixRepairSkippedInvalidatedTypes) {
         if (stepState == RepairAroundSkipped.StepState.PENDING || stepState == RepairAroundSkipped.StepState.ACTIVE) {
            suffixSkipped++;
         }
      }

      return suffixSkipped;
   }

   public double getAmount() {
      return this.totalRepairAmount;
   }

   public static enum Kind {
      SIDE_ROW_RUN,
      DIRECTIONAL_ROW_RUN,
      CONTAINER_ROW_RUN,
      DECK_STRIP_RUN,
      STATIC_STAND_BATCH,
      MULTIFACE_RUN,
      ACCESS_CLUSTER_RUN,
      REGION_DRAIN;
   }

   public record Step(long target, long stand, long support, int supportFace, int lookDir, long material, boolean sneakPulse, float headingDeg, int order) {
   }

   public static enum StepState {
      PENDING,
      ACTIVE,
      PLACED,
      INVALIDATED,
      SKIPPED;
   }

   public static enum YieldReason {
      FRONTIER_EXHAUSTED,
      GEOMETRY_CHANGED,
      MATERIAL_GONE,
      EXECUTOR_DISPROVED,
      SERVER_REJECTED,
      SUPERIOR_OPPORTUNITY,
      ABORTED,
      SESSION_RESET;
   }
}
