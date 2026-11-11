package com.corz.client.utils;

import com.corz.client.gui.ConsumerButton;
import com.corz.client.gui.MinButton;
import java.util.function.IntConsumer;

public final class ScannedSkipped {
   public float skippedScannedAmount;
   public float hoverX;
   public boolean skippedLimActive;
   public float skippedLimAmount;
   public float sideWalkAmount;
   public MinButton scannedSkippedMinButton;
   public float sideBuildingAmount;
   public float scannedSkippedAmount;
   public float hoverY;
   public ConsumerButton scannedSkippedConsumerButton;

   public float getMin(String scannedSkippedText, float scannedSkippedValue, float scannedSkippedValue2) {
      return this.scannedSkippedMinButton.getAmount(scannedSkippedText, scannedSkippedValue, scannedSkippedValue2);
   }

   private void handleScannedSkipped(float scannedSkippedBound, float pilotStatusBound, float scannedSkippedValue, float materialFactor, int scannedSkippedValue3, String scannedSkippedText, Runnable runnable, IntConsumer intConsumer) {
      if (this.skippedLimActive && this.scannedSkippedConsumerButton != null) {
         float maxSideBuildingAmount = Math.max(scannedSkippedBound, this.sideBuildingAmount);
         float maxSkippedScannedAmount = Math.max(pilotStatusBound, this.skippedScannedAmount);
         float min4 = Math.min(scannedSkippedBound + scannedSkippedValue, this.sideBuildingAmount + this.sideWalkAmount);
         float min5 = Math.min(pilotStatusBound + materialFactor, this.skippedScannedAmount + this.skippedLimAmount);
         if (min4 - maxSideBuildingAmount > 0.5F && min5 - maxSkippedScannedAmount > 0.5F) {
            this.scannedSkippedConsumerButton.handleMaxConsumer(maxSideBuildingAmount, maxSkippedScannedAmount, min4 - maxSideBuildingAmount, min5 - maxSkippedScannedAmount, scannedSkippedValue3, scannedSkippedText, runnable, intConsumer, 0);
         }
      }
   }

   public boolean canScannedSkipped(double scannedSkippedLimit, double scannedSkippedLimit2) {
      return scannedSkippedLimit >= this.sideBuildingAmount
         && scannedSkippedLimit <= this.sideBuildingAmount + this.sideWalkAmount
         && scannedSkippedLimit2 >= this.skippedScannedAmount
         && scannedSkippedLimit2 <= this.skippedScannedAmount + this.skippedLimAmount;
   }

   public void handle(String scannedSkippedText, float scannedSkippedValue, float matrixStackValue, float materialFactor, float scannedSkippedValue4, IntConsumer intConsumer) {
      this.handleScannedSkipped(scannedSkippedValue, matrixStackValue, materialFactor, scannedSkippedValue4, 1, scannedSkippedText, null, intConsumer);
   }

   public void handle(String scannedSkippedText, float scannedSkippedValue, float matrixStackValue, float scannedSkippedValue3, float materialFactor, Runnable runnable) {
      this.handleScannedSkipped(scannedSkippedValue, matrixStackValue, scannedSkippedValue3, materialFactor, 0, scannedSkippedText, runnable, null);
   }

   public float getMin(String scannedSkippedText, float scannedSkippedValue) {
      return this.scannedSkippedMinButton.getAmount(scannedSkippedText, scannedSkippedValue);
   }

   public float getMin(String scannedSkippedText, float scannedSkippedValue, float sideBuilding, float scannedSkippedValue3, float materialFactor) {
      return this.scannedSkippedMinButton.getAmount(scannedSkippedText, this.canAmountFlag(scannedSkippedValue, sideBuilding, scannedSkippedValue3, materialFactor));
   }

   public boolean canAmountFlag(float scannedSkippedValue, float sideBuilding, float scannedSkippedValue3, float materialFactor) {
      return this.skippedLimActive
         && this.canScannedSkipped(this.hoverX, this.scannedSkippedAmount)
         && this.hoverX >= scannedSkippedValue
         && this.hoverX <= scannedSkippedValue + scannedSkippedValue3
         && this.scannedSkippedAmount >= sideBuilding
         && this.scannedSkippedAmount <= sideBuilding + materialFactor;
   }
}
