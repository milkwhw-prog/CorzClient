package com.corz.client.utils;

import java.util.Random;
import com.corz.client.utils.AngleMath;
import com.corz.client.utils.RotationDelta;

public final class SmoothAimController {
   private long elapsedNanos;
   private double lastAppliedPitch;
   private double prevPitchDelta;
   private double prevYawDelta;
   private double smoothedEyeVelocityY;
   private boolean hasPrevEyeSample;
   private double trackedTargetX;
   private double viewPitch;
   private double prevPitchSign;
   private double smoothedNoise;
   private boolean noiseSeeded;
   private double pitchErrorIntegral;
   private double smoothedPitchTargetVelocity;
   private boolean hasGroundEyeSample;
   private double prevEyeY;
   private double yawErrorIntegral;
   private double smoothedCloseness;
   private double yawFractionCarry;
   private double yawReversalCharge;
   private boolean hasPrevFrame;
   private double springVelocity;
   private double springOffset;
   private double smoothedTargetVelY;
   private double viewYaw;
   private double smoothedYawVelocity;
   private int accumulatedPitchSteps;
   private double pitchReversalCharge;
   private final Random random;
   private double smoothedPitchVelocity;
   private double prevYawSign;
   private double recentSnapLevel;
   private long nextJitterTimeMs;
   private double smoothedTargetZ;
   private double lastAppliedYaw;
   private double smoothedTargetY;
   private double jitterTarget;
   private double pitchFractionCarry;
   private boolean pitchAimEnabled;
   private double jumpAimOffset;
   private boolean trackingInitialized;
   private double smoothedYawTargetVelocity;
   private double lastGroundEyeY;

   public void updateAimFrame(SmoothAimController.Frame frame, double signumValue, double signumValue2, boolean flag, boolean signumFlag, double signumDivisor) {
      double exponent = this.hasPrevFrame ? 0.008333333333333333 : 0.016666666666666666;
      this.elapsedNanos += 1000000L;
      this.pitchAimEnabled = flag;
      this.updateJumpAimOffset(frame, exponent);
      double min3 = Math.min(0.85, this.jumpAimOffset);
      double signumValue4 = 0.1 + min3;
      double signumFactor = AngleMath.clamp(min3 / 0.55, 0.0, 1.0);
      double boxMinYOffset = frame.boxMinY() + 0.01;
      double max5 = Math.max(boxMinYOffset, frame.boxMaxY() - 0.01);
      double signumValue6 = frame.boxMinY() + 0.65 * (frame.boxMaxY() - frame.boxMinY());
      double clamp = AngleMath.clamp(frame.eyeY() - signumValue4, boxMinYOffset, max5);
      double amount = frame.targetX();
      double clamp2 = AngleMath.clamp(clamp + (signumValue6 - clamp) * signumFactor, boxMinYOffset, max5);
      double targetZ = frame.targetZ();
      double deltaX = frame.targetVelX() - frame.selfVelX();
      double signumValue9 = frame.targetVelY() - frame.selfVelY();
      double deltaZ = frame.targetVelZ() - frame.selfVelZ();
      double amount4 = frame.targetVelY() + signumValue9 * 0.1;
      if (!this.trackingInitialized) {
         this.trackedTargetX = amount;
         this.smoothedTargetY = clamp2;
         this.smoothedTargetZ = targetZ;
         this.smoothedTargetVelY = amount4;
         this.trackingInitialized = true;
      }

      double hypot = Math.hypot(frame.targetVelX(), frame.targetVelZ());
      double hypot2 = Math.hypot(deltaX, deltaZ);
      double distance = frame.distance();
      double clamp11 = AngleMath.clamp(0.28 + Math.min(0.35, hypot2 * 0.5), 0.08, 0.75);
      double clamp12 = AngleMath.clamp(0.18 + Math.min(0.42, (hypot + 0.25 * hypot2) * 0.72), 0.12, 0.68);
      double i = AngleMath.clamp(exponent * 60.0, 0.45, 2.4);
      double signumFactor2 = 1.0 - Math.pow(1.0 - clamp11, i);
      double signumFactor3 = 1.0 - Math.pow(1.0 - clamp12, i);
      this.trackedTargetX = this.trackedTargetX + (amount - this.trackedTargetX) * signumFactor2;
      this.smoothedTargetY = this.smoothedTargetY + (clamp2 - this.smoothedTargetY) * signumFactor2;
      this.smoothedTargetZ = this.smoothedTargetZ + (targetZ - this.smoothedTargetZ) * signumFactor2;
      this.smoothedTargetVelY = this.smoothedTargetVelY + (amount4 - this.smoothedTargetVelY) * signumFactor3;
      if (Math.hypot(amount - this.trackedTargetX, targetZ - this.smoothedTargetZ) > 0.3 + 1.3 * hypot + 0.4 * hypot2) {
         this.trackedTargetX = amount;
         this.smoothedTargetY = clamp2;
         this.smoothedTargetZ = targetZ;
         this.smoothedTargetVelY = amount4;
      }

      double clamp4 = AngleMath.clamp(0.35 + 0.045 * distance + 0.95 * hypot + Math.min(0.18, 0.12 * hypot2), 0.1, 1.05);
      double signumBound = clamp4 * (0.3 + 0.7 * AngleMath.clamp((distance - 0.8) / 2.5, 0.0, 1.0));
      double max2 = Math.max(0.05, signumBound);
      double signumValue13 = this.smoothedTargetY - frame.eyeY();
      double signumValue14 = this.smoothedTargetVelY * Math.min(max2, 0.7 + 0.04 * distance) * (0.28 + 0.52 * AngleMath.clamp(Math.abs(signumValue13) / 0.7, 0.0, 1.0));
      double signumValue15 = 0.16 + 0.055 * distance;
      double clamp16 = AngleMath.clamp(signumValue14, -signumValue15, signumValue15);
      double x = this.trackedTargetX - frame.playerX();
      double y = this.smoothedTargetY + clamp16 - frame.eyeY();
      double y2 = this.smoothedTargetZ - frame.playerZ();
      double amount5 = Math.toDegrees(Math.atan2(y2, x)) - 90.0;
      double degrees6 = -Math.toDegrees(Math.atan2(y, Math.max(Math.hypot(x, y2), 1.0E-4)));
      if (this.hasPrevFrame) {
         double hashRegistryBound = Math.abs(AngleMath.wrapDegrees(amount5 - this.lastAppliedYaw));
         double apiPutBound = Math.abs(AngleMath.wrapDegrees(degrees6 - this.lastAppliedPitch));
         double clamp5 = AngleMath.clamp((Math.max(hashRegistryBound, apiPutBound) - 12.0) / 50.0, 0.0, 1.0);
         this.recentSnapLevel = Math.max(this.recentSnapLevel, clamp5);
         if (clamp5 > 0.3) {
            this.smoothedCloseness *= 0.3;
            this.trackedTargetX = amount;
            this.smoothedTargetY = clamp2;
            this.smoothedTargetZ = targetZ;
         }

         double signumValue17 = (120.0 + 700.0 * hypot2)
            * (1.0 + Math.min(2.0, hashRegistryBound / 60.0))
            * (1.0 + 1.5 * AngleMath.smoothStep(10.0, 35.0, Math.abs(AngleMath.wrapDegrees(amount5 - frame.viewYaw()))));
         double signumValue18 = (95.0 + 550.0 * Math.abs(this.smoothedTargetVelY)) * (1.0 + Math.min(2.0, apiPutBound / 60.0));
         double signumFactor5 = AngleMath.clamp(signumValue17, 90.0, 1080.0);
         double clamp6 = AngleMath.clamp(signumValue18, 70.0, 500.0);
         amount5 = this.lastAppliedYaw + AngleMath.clamp(AngleMath.wrapDegrees(amount5 - this.lastAppliedYaw), -signumFactor5 * exponent, signumFactor5 * exponent);
         degrees6 = this.lastAppliedPitch + AngleMath.clamp(AngleMath.wrapDegrees(degrees6 - this.lastAppliedPitch), -clamp6 * exponent, clamp6 * exponent);
      }

      this.recentSnapLevel = this.recentSnapLevel * Math.pow(0.02, exponent);
      if (this.recentSnapLevel < 0.01) {
         this.recentSnapLevel = 0.0;
      }

      double signumFactor7 = 1.0 + 3.0 * this.recentSnapLevel;
      double wrapDegrees = AngleMath.wrapDegrees(amount5 - frame.viewYaw());
      double spotValue = flag ? AngleMath.wrapDegrees(degrees6 - frame.viewPitch()) : 0.0;
      double sqrt2 = Math.sqrt(wrapDegrees * wrapDegrees + spotValue * spotValue);
      double signumLimit = 1.0 - AngleMath.smoothStep(1.5, 8.0, sqrt2);
      if (this.hasPrevFrame) {
         signumLimit = AngleMath.clamp(
            signumLimit + AngleMath.clamp(-(Math.abs(wrapDegrees) - Math.abs(this.prevYawDelta)) / exponent / 20.0, -0.3, 0.3), 0.0, 1.0
         );
      }

      double signumFactor8 = signumLimit > this.smoothedCloseness ? AngleMath.clamp(exponent * 3.0, 0.01, 0.25) : AngleMath.clamp(exponent * 20.0, 0.05, 0.8);
      this.smoothedCloseness = this.smoothedCloseness + (signumLimit - this.smoothedCloseness) * signumFactor8;
      double smoothedCloseness12 = this.smoothedCloseness;
      double signumFactor9 = 0.0;
      double signumFactor10 = 0.0;
      double signumFactor11 = 0.0;
      double signumFactor12 = 0.0;
      if (this.hasPrevFrame) {
         double signumValue19 = AngleMath.wrapDegrees(amount5 - this.lastAppliedYaw) / exponent;
         double signumValue20 = AngleMath.wrapDegrees(degrees6 - this.lastAppliedPitch) / exponent;
         signumFactor11 = AngleMath.wrapDegrees(frame.viewYaw() - this.viewYaw) / exponent;
         signumFactor12 = AngleMath.wrapDegrees(frame.viewPitch() - this.viewPitch) / exponent;
         double clamp13 = AngleMath.clamp(exponent * 12.0, 0.05, 0.45);
         this.smoothedYawTargetVelocity = this.smoothedYawTargetVelocity + (signumValue19 - this.smoothedYawTargetVelocity) * clamp13;
         this.smoothedPitchTargetVelocity = this.smoothedPitchTargetVelocity + (signumValue20 - this.smoothedPitchTargetVelocity) * clamp13;
         signumFactor9 = this.smoothedYawTargetVelocity;
         signumFactor10 = this.smoothedPitchTargetVelocity;
      }

      double strafeIncreaseScaled = signumValue * 0.75;
      double aimVerticallyScaled = signumValue2 * 0.75;
      if (Math.signum(wrapDegrees) != Math.signum(this.prevYawDelta) && Math.abs(wrapDegrees) > 0.1 && Math.abs(this.prevYawDelta) > 0.1) {
         this.yawErrorIntegral *= 0.3;
      }

      if (Math.signum(spotValue) != Math.signum(this.prevPitchDelta) && Math.abs(spotValue) > 0.1 && Math.abs(this.prevPitchDelta) > 0.1) {
         this.pitchErrorIntegral *= 0.3;
      }

      this.yawErrorIntegral += wrapDegrees * exponent * smoothedCloseness12 * smoothedCloseness12;
      this.pitchErrorIntegral += spotValue * exponent * smoothedCloseness12 * smoothedCloseness12;
      double signumFactor14 = 1.0 - (1.0 - smoothedCloseness12) * AngleMath.clamp(exponent * 5.0, 0.0, 0.5);
      this.yawErrorIntegral = AngleMath.clamp(this.yawErrorIntegral * signumFactor14, -15.0, 15.0);
      this.pitchErrorIntegral = AngleMath.clamp(this.pitchErrorIntegral * signumFactor14, -10.0, 10.0);
      this.smoothedYawVelocity = 0.85 * this.smoothedYawVelocity + 0.15 * (this.hasPrevFrame ? (wrapDegrees - this.prevYawDelta) / exponent : 0.0);
      this.smoothedPitchVelocity = 0.85 * this.smoothedPitchVelocity + 0.15 * (this.hasPrevFrame ? (spotValue - this.prevPitchDelta) / exponent : 0.0);
      if (!flag) {
         this.pitchErrorIntegral = 0.0;
         this.smoothedPitchVelocity = 0.0;
      }

      double signum = Math.signum(wrapDegrees);
      double signum2 = Math.signum(spotValue);
      this.yawReversalCharge = signum != this.prevYawSign && Math.abs(wrapDegrees) > 0.5 ? Math.min(this.yawReversalCharge + 1.0, 8.0) : Math.max(0.0, this.yawReversalCharge - 3.0 * exponent);
      this.pitchReversalCharge = signum2 != this.prevPitchSign && Math.abs(spotValue) > 0.5 ? Math.min(this.pitchReversalCharge + 1.0, 8.0) : Math.max(0.0, this.pitchReversalCharge - 3.0 * exponent);
      this.prevYawSign = signum;
      this.prevPitchSign = signum2;
      double clamp21 = AngleMath.clamp(this.yawReversalCharge / 5.0, 0.0, 1.0);
      double clamp22 = AngleMath.clamp(this.pitchReversalCharge / 5.0, 0.0, 1.0);
      double min23 = Math.min(strafeIncreaseScaled, 10.0);
      double min24 = Math.min(aimVerticallyScaled, 10.0);
      double square25 = square((min23 - 1.0) / 9.0);
      double square26 = square((min24 - 1.0) / 9.0);
      double signumFactor15 = 0.15 + 0.85 * AngleMath.clamp(square25, 0.0, 1.0);
      double signumFactor16 = 0.15 + 0.85 * AngleMath.clamp(square26, 0.0, 1.0);
      double signumFactor17 = AngleMath.lerp(smoothedCloseness12, 8.0, 2.5 + 0.15 * min23) * signumFactor15 * (1.0 - 0.6 * clamp21);
      double signumFactor18 = AngleMath.lerp(smoothedCloseness12, 0.15, 0.8 + 0.04 * min23) * signumFactor15;
      double signumFactor19 = AngleMath.lerp(smoothedCloseness12, 0.08, 0.25) * signumFactor15 * (1.0 + 2.0 * clamp21);
      double signumFactor20 = (0.85 + 0.015 * min23) * signumFactor15;
      double lerp = AngleMath.lerp(smoothedCloseness12, 0.1, 0.3);
      double signumValue27 = signumFactor17 * wrapDegrees + signumFactor18 * this.yawErrorIntegral + signumFactor19 * this.smoothedYawVelocity + signumFactor20 * signumFactor9 - lerp * signumFactor11;
      double signumValue28 = 0.0;
      if (flag) {
         double signumFactor22 = AngleMath.lerp(smoothedCloseness12, 7.0, 2.2 + 0.13 * min24) * signumFactor16 * (1.0 - 0.6 * clamp22);
         double signumFactor23 = AngleMath.lerp(smoothedCloseness12, 0.12, 0.65 + 0.035 * min24) * signumFactor16;
         double signumFactor24 = AngleMath.lerp(smoothedCloseness12, 0.06, 0.2) * signumFactor16 * (1.0 + 2.0 * clamp22);
         double signumFactor25 = (0.82 + 0.013 * min24) * signumFactor16;
         double lerp26 = AngleMath.lerp(smoothedCloseness12, 0.08, 0.25);
         signumValue28 = signumFactor22 * spotValue + signumFactor23 * this.pitchErrorIntegral + signumFactor24 * this.smoothedPitchVelocity + signumFactor25 * signumFactor10 - lerp26 * signumFactor12;
      }

      double signumValue29 = signumValue28 + this.computePitchJitter(frame.viewPitch(), exponent, smoothedCloseness12);
      if (signumFlag && Math.abs(frame.strafe()) > 0.01) {
         boolean signumFlag2 = wrapDegrees > 0.0;
         if (signumFlag2 ? frame.strafe() > 0.0 : frame.strafe() < 0.0) {
            signumValue27 *= 1.15;
         }
      }

      double smoothStep = AngleMath.smoothStep(8.0, 40.0, Math.abs(wrapDegrees));
      double signumFactor27 = 1.0 + 2.5 * smoothStep;
      double min31 = Math.min(
         400.0 * signumFactor7 * signumFactor27, Math.max((22.0 + 15.0 * min23) * signumFactor15 * signumFactor7 * signumFactor27, (0.4 * Math.abs(signumFactor9) + 18.0) * (0.35 + 0.65 * signumFactor15) * signumFactor27)
      );
      double min32 = Math.min(300.0 * signumFactor7, Math.max((18.0 + 13.0 * min24) * signumFactor16 * signumFactor7, (0.38 * Math.abs(signumFactor10) + 14.0) * (0.35 + 0.65 * signumFactor16)));
      double clamp28 = AngleMath.clamp(signumValue27, -min31, min31);
      double clamp29 = AngleMath.clamp(signumValue29, -min32, min32);
      double smoothStep30 = AngleMath.smoothStep(0.5, 3.0, distance);
      double lerp33 = AngleMath.lerp(smoothStep, 0.15, 0.65);
      double signumValue34 = clamp28 * AngleMath.lerp(smoothedCloseness12, lerp33 + (1.0 - lerp33) * smoothStep30, 0.4 + 0.6 * smoothStep30);
      double signumTotal = clamp29 * AngleMath.lerp(smoothedCloseness12, 0.2 + 0.8 * smoothStep30, 0.45 + 0.55 * smoothStep30);
      double seconds = this.elapsedNanos / 1.0E9;
      double signumFactor31 = 0.4 * Math.sin(62.83 * seconds)
         + 0.25 * Math.sin(47.12 * seconds)
         + 0.15 * Math.sin(78.54 * seconds)
         + 0.8 * Math.sin(12.57 * seconds)
         + 0.5 * Math.sin(7.85 * seconds)
         + 0.3 * Math.sin(1.26 * seconds);
      double signumFactor32 = 0.35 * Math.sin(56.55 * seconds + 1.3)
         + 0.2 * Math.sin(43.98 * seconds + 0.7)
         + 0.12 * Math.sin(72.26 * seconds + 2.1)
         + 0.6 * Math.sin(10.47 * seconds + 0.9)
         + 0.4 * Math.sin(5.65 * seconds + 1.8)
         + 0.2 * Math.sin(0.94 * seconds + 0.5);
      double signumFactor33 = 0.15 + 0.85 * smoothedCloseness12;
      double signumFactor34 = 0.5 + 0.5 * (1.0 - square25);
      double groundY = signumValue34 + signumFactor31 * signumFactor33 * signumFactor34 * 1.5;
      if (flag) {
         signumTotal += signumFactor32 * signumFactor33 * signumFactor34;
      }

      if (signumDivisor > 1.0E-5 && Double.isFinite(groundY) && Double.isFinite(signumTotal)) {
         this.yawFractionCarry += groundY * exponent / signumDivisor;
         this.pitchFractionCarry += signumTotal * exponent / signumDivisor;
      }

      this.hasPrevFrame = true;
      this.prevYawDelta = wrapDegrees;
      this.prevPitchDelta = spotValue;
      this.lastAppliedYaw = amount5;
      this.lastAppliedPitch = degrees6;
      this.viewYaw = frame.viewYaw();
      this.viewPitch = frame.viewPitch();
   }

   public RotationDelta takePendingMouseDelta() {
      int yawFractionCarry13 = (int)this.yawFractionCarry;
      int limit = (int)this.pitchFractionCarry;
      this.yawFractionCarry -= yawFractionCarry13;
      this.pitchFractionCarry -= limit;
      if (!this.pitchAimEnabled) {
         limit = RotationDelta.clampDeltaToLimit(this.accumulatedPitchSteps, limit, 10);
         this.accumulatedPitchSteps += limit;
      }

      return yawFractionCarry13 == 0 && limit == 0 ? RotationDelta.INSTANCE : new RotationDelta(yawFractionCarry13, limit);
   }

   double getSmoothedCloseness() {
      return this.smoothedCloseness;
   }

   public SmoothAimController(Random random) {
      this.random = random;
   }

   private static double square(double signumFactor) {
      return signumFactor * signumFactor;
   }

   public void reset() {
      this.yawFractionCarry = 0.0;
      this.pitchFractionCarry = 0.0;
      this.yawErrorIntegral = 0.0;
      this.pitchErrorIntegral = 0.0;
      this.smoothedYawVelocity = 0.0;
      this.smoothedPitchVelocity = 0.0;
      this.prevYawDelta = 0.0;
      this.prevPitchDelta = 0.0;
      this.lastAppliedYaw = 0.0;
      this.lastAppliedPitch = 0.0;
      this.smoothedYawTargetVelocity = 0.0;
      this.smoothedPitchTargetVelocity = 0.0;
      this.viewYaw = 0.0;
      this.viewPitch = 0.0;
      this.hasPrevFrame = false;
      this.trackingInitialized = false;
      this.trackedTargetX = this.smoothedTargetY = this.smoothedTargetZ = 0.0;
      this.smoothedTargetVelY = 0.0;
      this.smoothedCloseness = 0.0;
      this.yawReversalCharge = 0.0;
      this.pitchReversalCharge = 0.0;
      this.prevYawSign = 0.0;
      this.prevPitchSign = 0.0;
      this.recentSnapLevel = 0.0;
      this.prevEyeY = 0.0;
      this.hasPrevEyeSample = false;
      this.lastGroundEyeY = 0.0;
      this.hasGroundEyeSample = false;
      this.smoothedEyeVelocityY = 0.0;
      this.jumpAimOffset = 0.0;
      this.noiseSeeded = false;
      this.nextJitterTimeMs = 0L;
      this.jitterTarget = 0.0;
      this.springOffset = 0.0;
      this.springVelocity = 0.0;
      this.smoothedNoise = 0.0;
      this.elapsedNanos = 0L;
      this.accumulatedPitchSteps = 0;
   }

   private void updateJumpAimOffset(SmoothAimController.Frame frame, double signumDivisor) {
      double amount = frame.eyeY();
      double signumValue = this.hasPrevFrame && this.hasPrevEyeSample ? (amount - this.prevEyeY) / signumDivisor : 0.0;
      this.smoothedEyeVelocityY = this.smoothedEyeVelocityY + (signumValue - this.smoothedEyeVelocityY) * 0.65;
      this.prevEyeY = amount;
      this.hasPrevEyeSample = true;
      if (frame.onGround()) {
         this.lastGroundEyeY = amount;
         this.hasGroundEyeSample = true;
         this.jumpAimOffset *= 0.35;
      } else {
         if (!this.hasGroundEyeSample) {
            this.lastGroundEyeY = amount;
            this.hasGroundEyeSample = true;
         }

         double max2 = Math.max(0.0, amount - this.lastGroundEyeY);
         this.jumpAimOffset = Math.max(this.jumpAimOffset * 0.92, Math.max(Math.min(0.7, Math.max(0.0, this.smoothedEyeVelocityY) * 0.34), Math.min(0.82, max2 * 0.58)));
      }
   }

   private double computePitchJitter(double signumLimit, double exponent, double signumValue) {
      double signumValue2 = 0.65 + 0.35 * signumValue;
      long signumLimit2 = this.elapsedNanos / 1000000L;
      if (!this.noiseSeeded || signumLimit2 >= this.nextJitterTimeMs) {
         double signumValue3 = signumLimit > 22.0 ? -0.18 : (signumLimit < -22.0 ? 0.18 : 0.0);
         this.jitterTarget = AngleMath.clamp((this.random.nextDouble() * 2.0 - 1.0) * AngleMath.lerp(signumValue2, 0.05, 0.15) + signumValue3, -0.5, 0.5);
         this.nextJitterTimeMs = signumLimit2 + 300L + this.random.nextInt(420);
         this.noiseSeeded = true;
      }

      this.springVelocity = this.springVelocity + (this.jitterTarget - this.springOffset) * AngleMath.lerp(signumValue2, 2.0, 5.0) * exponent;
      this.springVelocity = this.springVelocity * Math.pow(0.04, exponent);
      this.springOffset = this.springOffset + this.springVelocity * exponent;
      double amount = AngleMath.lerp(signumValue2, 0.45, 0.8);
      if (this.springOffset > amount) {
         this.springOffset = amount;
         this.springVelocity = Math.min(0.0, this.springVelocity);
      } else if (this.springOffset < -amount) {
         this.springOffset = -amount;
         this.springVelocity = Math.max(0.0, this.springVelocity);
      }

      this.smoothedNoise = this.smoothedNoise + (this.random.nextDouble() * 2.0 - 1.0 - this.smoothedNoise) * AngleMath.clamp(exponent * 5.0, 0.02, 0.18);
      double seconds = this.elapsedNanos / 1.0E9;
      double signumFactor = 0.35 * Math.sin(8.7 * seconds + 0.4) + 0.22 * Math.sin(13.1 * seconds + 2.2) + 0.12 * Math.sin(19.6 * seconds + 1.1) + 0.3 * this.smoothedNoise;
      double signumValue4 = 0.25 * this.springVelocity + this.springOffset * AngleMath.lerp(signumValue2, 1.4, 3.0) + signumFactor * AngleMath.lerp(signumValue2, 0.35, 0.9);
      double signumValue5 = AngleMath.lerp(signumValue2, 0.25, 0.55) * (1.0 - 0.85 * AngleMath.smoothStep(72.0, 88.0, Math.abs(signumLimit)));
      return AngleMath.clamp(signumValue4, -signumValue5, signumValue5);
   }

   public record Frame(
      double playerX,
      double eyeY,
      double playerZ,
      boolean onGround,
      double targetX,
      double targetZ,
      double boxMinY,
      double boxMaxY,
      double targetVelX,
      double targetVelY,
      double targetVelZ,
      double selfVelX,
      double selfVelY,
      double selfVelZ,
      double distance,
      double viewYaw,
      double viewPitch,
      double strafe
   ) {
   }
}
