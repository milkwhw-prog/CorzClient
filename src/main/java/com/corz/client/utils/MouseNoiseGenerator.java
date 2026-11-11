package com.corz.client.utils;

import java.util.Random;
import com.corz.client.utils.RotationDelta;

public final class MouseNoiseGenerator {
   private double pitchCarry;
   private int yawJitterSum;
   private int jitterTickCount;
   private double pitchSlack;
   private int sampleTickCount;
   private double yawCarry;
   private double pitchNoise;
   private int settleTickCount;
   private int pitchJitterDirection;
   private double prevYawDeltaSample;
   private double yawNoise;
   private final Random random;
   private double yawSlack;
   private double yawNoiseDrift;
   private double pitchNoiseDrift;
   private int appliedPitchJitter;
   private boolean prevPitchUp;
   private int yawJitterDirection;
   private boolean prevYawNegative;
   private int pendingPitchJitter;

   public RotationDelta takeMouseDelta() {
      this.yawCarry = this.yawCarry + this.yawJitterSum;
      int randomValue = RotationDelta.clampDeltaToLimit(this.appliedPitchJitter, this.pendingPitchJitter, 10);
      this.appliedPitchJitter += randomValue;
      this.pitchCarry += randomValue;
      this.yawJitterSum = 0;
      this.pendingPitchJitter = 0;
      int yawCarry3 = (int)this.yawCarry;
      int pitchCarry2 = (int)this.pitchCarry;
      this.yawCarry -= yawCarry3;
      this.pitchCarry -= pitchCarry2;
      return yawCarry3 == 0 && pitchCarry2 == 0 ? RotationDelta.INSTANCE : new RotationDelta(yawCarry3, pitchCarry2);
   }

   public MouseNoiseGenerator(Random random) {
      this.random = random;
   }

   private void addYawNoise(double randomFactor, double randomValue, double amount) {
      if (randomFactor == 0.0) {
         this.yawCarry = 0.0;
      } else {
         double adj2 = amount;
         int absSpeedSetting = (int)Math.abs(randomValue);
         if (absSpeedSetting <= 10) {
            this.yawSlack = amount;
         }

         if (this.yawSlack > 0.0) {
            adj2 = amount - this.yawSlack / 3.0;
            this.yawSlack -= absSpeedSetting / 200.0;
         }

         this.yawCarry += adj2 * randomFactor * 5.0;
      }
   }

   private void tickJitter() {
      this.jitterTickCount++;
      if (this.jitterTickCount >= 250 + this.random.nextInt(50)) {
         this.jitterTickCount = -100 + this.random.nextInt(50);
         this.yawJitterDirection = this.random.nextInt(3) - 1;
         this.pitchJitterDirection = this.random.nextInt(3) - 1;
      }

      int yawJitterDirection7 = this.yawJitterDirection;
      int pitchJitterDirection5 = this.pitchJitterDirection;
      if (this.random.nextInt(10) < 2) {
         yawJitterDirection7 = 0;
      }

      if (this.random.nextInt(10) < 2) {
         pitchJitterDirection5 = 0;
      }

      if (this.jitterTickCount < 0) {
         yawJitterDirection7 = 0;
         pitchJitterDirection5 = 0;
      }

      if (this.random.nextInt(20) == 1) {
         this.yawJitterSum += yawJitterDirection7;
         this.pendingPitchJitter += pitchJitterDirection5;
      }

      if (this.yawCarry > 0.0 && this.yawJitterSum < 0 || this.yawCarry < 0.0 && this.yawJitterSum > 0) {
         this.yawJitterSum = 0;
      }
   }

   public void resetCarry() {
      this.yawCarry = 0.0;
      this.pitchCarry = 0.0;
      this.yawJitterDirection = 0;
      this.pitchJitterDirection = 0;
      this.yawJitterSum = 0;
      this.pendingPitchJitter = 0;
   }

   private void tickNoiseSettle() {
      if (++this.settleTickCount > 10) {
         this.pitchNoise = this.pitchNoiseDrift;
         this.yawNoise = this.yawNoiseDrift;
         this.yawNoiseDrift = 0.0;
         this.pitchNoiseDrift = 0.0;
         this.settleTickCount = 0;
      }
   }

   public void resetAll() {
      this.resetCarry();
      this.yawNoise = 0.0;
      this.yawNoiseDrift = 0.0;
      this.pitchNoiseDrift = 0.0;
      this.pitchNoise = 0.0;
      this.yawSlack = 0.0;
      this.pitchSlack = 0.0;
      this.jitterTickCount = 0;
      this.prevYawNegative = false;
      this.prevPitchUp = false;
      this.prevYawDeltaSample = 0.0;
      this.settleTickCount = 0;
      this.sampleTickCount = 0;
      this.appliedPitchJitter = 0;
   }

   public void updateNoise(
      double randomValue,
      double randomValue2,
      double randomLimit,
      double distanceTo,
      double x,
      double randomValue4,
      double randomValue5,
      boolean randomFlag,
      boolean enabled
   ) {
      this.tickNoiseSettle();
      this.tickJitter();
      double min = Math.min(180.0, Math.abs(randomValue));
      boolean flag = randomValue < 0.0;
      boolean flag2 = randomLimit > 0.0;
      double absAimVertically = Math.abs(randomLimit);
      double spotValue = absAimVertically - 10.0;
      double randomTotal = 1.0 + this.random.nextDouble() * 2.0 + min / 50.0;
      double randomValue7 = 1.0 + this.random.nextDouble() * 2.0 + Math.abs(spotValue) / 50.0;
      if (Math.abs(min - this.prevYawDeltaSample) > 6.0) {
         randomTotal += min / 35.0;
      }

      double randomValue8 = randomTotal + Math.max(0.0, (9.0 - distanceTo) / 2.5 - 2.0);
      boolean randomFlag3 = flag ? x < 0.0 : x > 0.0;
      if (enabled && randomFlag3) {
         randomValue8 *= 1.6;
      }

      if (distanceTo < 0.5) {
         randomValue8 /= 5.0;
      }

      double randomValue9 = randomValue8 / 90.0 * (flag ? -1.0 : 1.0);
      double randomValue10 = randomValue7 / 90.0 * (flag2 ? 1.0 : -1.0);
      if (min < 5.0) {
         randomValue9 = 0.0;
         this.yawNoise *= 0.7;
         if (flag ? x > 0.0 : x < 0.0) {
            this.yawNoise *= 0.5;
         }
      }

      if (flag != this.prevYawNegative) {
         this.yawNoise = -this.yawNoise;
         this.yawNoiseDrift = -this.yawNoiseDrift;
         this.yawCarry = 0.0;
      }

      if (flag2 != this.prevPitchUp) {
         this.pitchNoise = -this.pitchNoise;
         this.pitchNoiseDrift = -this.pitchNoiseDrift;
         this.pitchCarry = 0.0;
      }

      if (spotValue < 5.0) {
         randomValue10 = 0.0;
         this.pitchNoise *= 0.7;
      }

      this.yawNoiseDrift += randomValue9;
      this.pitchNoiseDrift += randomValue10;
      if (Math.abs(this.yawNoise) > 10.0) {
         this.yawNoiseDrift = 0.0;
         this.yawNoise = 0.0;
      } else {
         double randomFactor = this.yawNoise * 0.15;
         if (min <= 9.0) {
            randomFactor /= 10.0 - min;
         }

         if (!Double.isFinite(randomFactor)) {
            this.yawNoise = 0.0;
            this.yawNoiseDrift = 0.0;
            this.yawCarry = 0.0;
         } else {
            this.addYawNoise(randomFactor, randomValue2, randomValue4);
            if (randomFlag) {
               double groundY = this.pitchNoise * 0.15;
               if (!Double.isFinite(groundY)) {
                  this.pitchNoiseDrift = 0.0;
                  this.pitchNoise = 0.0;
                  this.pitchCarry = 0.0;
                  return;
               }

               this.addPitchNoise(groundY, absAimVertically, randomValue5);
            }

            this.prevPitchUp = flag2;
            this.prevYawNegative = flag;
            if (++this.sampleTickCount > 10) {
               this.prevYawDeltaSample = min;
               this.sampleTickCount = 0;
            }
         }
      }
   }

   private void addPitchNoise(double randomFactor, double randomValue, double amount) {
      if (randomFactor == 0.0) {
         this.pitchCarry = 0.0;
      } else {
         double adj2 = amount;
         int randomValue2 = (int)randomValue;
         if (randomValue2 <= 10) {
            this.pitchSlack = amount;
         }

         if (this.pitchSlack > 0.0) {
            adj2 = amount - this.pitchSlack / 3.0;
            this.pitchSlack -= randomValue2 / 200.0;
         }

         this.pitchCarry += adj2 * randomFactor * 5.0;
      }
   }
}
