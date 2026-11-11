package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import com.corz.client.utils.RepairAroundSkipped;
import com.corz.client.utils.DegStandUtils;

public final class RepairAroundConst {
   private RepairAroundSkipped repairAround;
   private final List<RepairAroundSkipped.Step> aroundRepairStepList = new ArrayList<>();
   private final int maxStepHistory;

   public boolean isEmpty() {
      return this.aroundRepairStepList.isEmpty();
   }

   public RepairAroundSkipped.Step getAroundRepairStepList() {
      return this.aroundRepairStepList.isEmpty() ? null : this.aroundRepairStepList.get(0);
   }

   public RepairAroundSkipped getRepairAround() {
      return this.repairAround;
   }

   public RepairAroundConst(int repairAroundBound) {
      this.maxStepHistory = Math.max(16, Math.min(64, repairAroundBound));
   }

   public int getAroundRepair(long aroundRepairValue, int repairAroundLimit, Predicate<RepairAroundSkipped.Step> predicate) {
      if (this.repairAround == null) {
         return 0;
      } else {
         int repairAround = this.repairAround.getRepairAround(aroundRepairValue, repairAroundLimit, predicate);
         this.updateAroundRepair();
         return repairAround;
      }
   }

   private boolean canMax(double repairAroundValue, double repairAroundValue2, float degStandValue, long repairAroundValue4) {
      double standDeg = DegStandUtils.getStandDeg(getDegHeading(this.aroundRepairStepList.get(0)), repairAroundValue, repairAroundValue2, 0L, false, degStandValue, repairAroundValue4, 4.0);
      int repairAroundLimit = Math.min(this.maxStepHistory, this.aroundRepairStepList.size());

      for (int i = 1; i < repairAroundLimit; i++) {
         double standDeg2 = DegStandUtils.getStandDeg(getDegHeading(this.aroundRepairStepList.get(i)), repairAroundValue, repairAroundValue2, 0L, false, degStandValue, repairAroundValue4, 4.0);
         if (standDeg2 < standDeg - 1.0E-6) {
            return false;
         }
      }

      return true;
   }

   private void handle(double repairAroundValue, double repairAroundValue2, float degStandValue, long repairAroundValue4) {
      List standDeg = DegStandUtils.getStandDeg(this.aroundRepairStepList, RepairAroundConst::getDegHeading, repairAroundValue, repairAroundValue2, degStandValue, repairAroundValue4, 4.0);
      this.aroundRepairStepList.clear();
      this.aroundRepairStepList.addAll(standDeg);
   }

   private void updateAroundRepair() {
      if (this.repairAround == null) {
         this.aroundRepairStepList.clear();
      } else {
         this.aroundRepairStepList.removeIf(step -> {
            RepairAroundSkipped.StepState suffixSkipped = this.repairAround.getSuffixSkipped(step.target());
            return suffixSkipped != RepairAroundSkipped.StepState.PENDING && suffixSkipped != RepairAroundSkipped.StepState.ACTIVE;
         });
      }
   }

   public void handleAroundRepair(RepairAroundSkipped repairAround, double aroundRepairValue, double aroundRepairValue2, float degStandValue, long aroundRepairValue4) {
      this.repairAround = repairAround;
      this.aroundRepairStepList.clear();
      if (repairAround != null) {
         for (RepairAroundSkipped.Step step : repairAround.getUnmodifiableStepList()) {
            RepairAroundSkipped.StepState suffixSkipped = repairAround.getSuffixSkipped(step.target());
            if (suffixSkipped == RepairAroundSkipped.StepState.PENDING || suffixSkipped == RepairAroundSkipped.StepState.ACTIVE) {
               this.aroundRepairStepList.add(step);
            }
         }

         this.handle(aroundRepairValue, aroundRepairValue2, degStandValue, aroundRepairValue4);
      }
   }

   public List<RepairAroundSkipped.Step> getUnmodifiableList() {
      return Collections.unmodifiableList(new ArrayList<>(this.aroundRepairStepList.subList(0, Math.min(this.maxStepHistory, this.aroundRepairStepList.size()))));
   }

   private static DegStandUtils.Item getDegHeading(RepairAroundSkipped.Step step) {
      return new DegStandUtils.Item(step.target(), step.stand(), step.headingDeg(), step.material(), step.order());
   }

   public int getAroundRepair(Predicate<RepairAroundSkipped.Step> predicate) {
      if (this.repairAround == null) {
         return 0;
      } else {
         int repairSuffix = this.repairAround.getRepairSuffix(predicate);
         this.updateAroundRepair();
         return repairSuffix;
      }
   }

   public int getAroundRepair(long aroundRepairValue, double repairAroundValue, double aroundRepairValue3, float degStandValue, long aroundRepairValue5) {
      if (this.repairAround == null) {
         return 0;
      } else {
         this.repairAround.canSkippedInvalidated(aroundRepairValue, this.repairAround.getRepairAround());
         this.updateAroundRepair();
         if (!this.aroundRepairStepList.isEmpty() && !this.canMax(repairAroundValue, aroundRepairValue3, degStandValue, aroundRepairValue5)) {
            this.handle(repairAroundValue, aroundRepairValue3, degStandValue, aroundRepairValue5);
         }

         return this.repairAround.getSuffixSkippedSuffixRepairSkippedInvalidatedTypes();
      }
   }

   public void updateAroundRepairRepairAround() {
      this.repairAround = null;
      this.aroundRepairStepList.clear();
   }

   public int getMax() {
      return this.maxStepHistory;
   }
}
