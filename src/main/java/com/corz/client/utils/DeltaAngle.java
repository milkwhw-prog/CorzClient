package com.corz.client.utils;

public class DeltaAngle {
   private double angleOffsetDegrees;
   private boolean deltaAngleActive;
   private boolean angleInitialized;
   private final float tickDelta;
   private double deltaAngleAmount;
   private float lerpAmount;
   private double previousAngleDegrees;
   private float angleDegrees;

   public final void handleAmountFlag(double amount) {
      this.previousAngleDegrees = amount;
      this.angleInitialized = true;
   }

   public final void handleAmountFlagForDouble(double amount) {
      this.deltaAngleAmount = amount;
      this.angleInitialized = true;
   }

   public final void setLerp(float lerp) {
      this.lerpAmount = lerp;
   }

   public final float getTickDelta() {
      return this.tickDelta;
   }

   public final double getAmount() {
      return this.angleOffsetDegrees;
   }

   public DeltaAngle(double amount, double amount2, double amount3, float angleDegrees, float lerp, float tickDelta2) {
      this.angleOffsetDegrees = amount;
      this.previousAngleDegrees = amount2;
      this.deltaAngleAmount = amount3;
      this.angleDegrees = angleDegrees;
      this.lerpAmount = lerp;
      this.tickDelta = tickDelta2;
      this.deltaAngleActive = false;
   }

   public final float getAngleDegrees() {
      return this.angleDegrees;
   }

   public final double getPreviousAngleDegrees() {
      return this.previousAngleDegrees;
   }

   public final boolean isFlag() {
      return this.deltaAngleActive;
   }

   public final void handleAmountFlagAngleOffsetDegrees(double amount) {
      this.angleOffsetDegrees = amount;
      this.angleInitialized = true;
   }

   public final void setAngleDegrees(float angleDegrees) {
      this.angleDegrees = angleDegrees;
   }

   public final double getDeltaAngleAmount() {
      return this.deltaAngleAmount;
   }

   public final void updateFlag() {
      this.deltaAngleActive = true;
   }

   public final boolean isAngleInitialized() {
      return this.angleInitialized;
   }

   public final float getLerp() {
      return this.lerpAmount;
   }
}
