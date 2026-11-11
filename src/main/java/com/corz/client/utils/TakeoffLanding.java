package com.corz.client.utils;

import java.util.Locale;
import com.corz.client.utils.GapSupportData;

public final class TakeoffLanding {
   private long landedCellTime;
   private int airborneTakeoffCount;
   private int takeoffLandingValue;
   private double currentHypot;
   private boolean occupiedLandingFlag;
   private boolean primingEstimateFlag;
   private final int approachSignumX;
   private int firstSampleTick;
   private double hypotTakeoffLanding;
   private final double landingTargetZ;
   private final GapSupportData settling;
   private final double takeoffLandingAmount;
   private boolean landedNodeFlag;
   private TakeoffLanding.Failure certifiedStalledType;
   private final int approachSignumZ;
   private double gainingSlipperyAmount;
   private double takeoffBrakingAmount;
   private double bestApproachAmount;
   private int takeoffSpeedCount;
   private int airborneAllowsCount;
   private double lastApproachAmount;
   private TakeoffLanding.Phase landedTakeoffType = TakeoffLanding.Phase.APPROACH;
   private double previousSpeedAmount;
   private boolean landedSettlingActive;

   public boolean isLandedNodeFlag() {
      return this.landedNodeFlag;
   }

   private TakeoffLanding.Command getLandedSettling(TakeoffLanding.Sample sample, boolean landedSettlingFlag) {
      this.landedTakeoffType = TakeoffLanding.Phase.LANDED;
      if (!sample.onGround()) {
         return this.getSettlingRequired(true, false, false, landedSettlingFlag, "settling");
      } else if (this.settling.isLandedOnTarget(sample.x(), sample.y(), sample.z(), true)) {
         this.landedTakeoffType = TakeoffLanding.Phase.COMPLETE;
         return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, true, "landed in the cell");
      } else if (this.landedCellTime != 0L && ProofSupportUtils.getTime(sample.feetX(), sample.feetY(), sample.feetZ()) == this.landedCellTime) {
         this.landedTakeoffType = TakeoffLanding.Phase.COMPLETE;
         this.landedNodeFlag = true;
         this.landedSettlingActive = true;
         return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, true, "landed on the next route node");
      } else if (sample.feetY() == this.settling.getFromY() && this.getSignumAmount(sample.x(), sample.z()) > 1.0) {
         return this.getLandedTakeoff(TakeoffLanding.Failure.NO_TAKEOFF, "came down on the takeoff side");
      } else if (this.settling.isInsideLandingArea(sample.x(), sample.z())
         && sample.y() < this.settling.landingY()
         && this.settling.landingY() - sample.y() <= 0.6) {
         this.landedTakeoffType = TakeoffLanding.Phase.WALK;
         this.takeoffLandingValue = sample.tick();
         return this.getSettlingRequired(true, false, false, true, "landed on the low step of the landing cell: walking up");
      } else if (this.settling.isAtLandingHeight(sample.y())) {
         this.landedSettlingActive = this.getSignumAmount(sample.x(), sample.z()) <= 0.0;
         this.landedTakeoffType = TakeoffLanding.Phase.WALK;
         this.takeoffLandingValue = sample.tick();
         return this.getSettlingRequired(
            true, false, false, true, "landed " + (this.getSignumAmount(sample.x(), sample.z()) > 0.0 ? "short" : "long") + ": walking onto the cell"
         );
      } else {
         return this.getLandedTakeoff(
            TakeoffLanding.Failure.LANDING_HEIGHT_MISMATCH,
            String.format(Locale.ROOT, "landed at y=%.3f, certificate expects %.3f", sample.y(), this.settling.landingY())
         );
      }
   }

   public TakeoffLanding.Phase getLandedTakeoffType() {
      return this.landedTakeoffType;
   }

   public TakeoffLanding getGainingSlippery(double gainingSlipperyAmount) {
      this.gainingSlipperyAmount = gainingSlipperyAmount;
      return this;
   }

   public double getHypotTakeoffLanding() {
      return this.hypotTakeoffLanding;
   }

   public boolean isLandedTakeoff() {
      return this.landedTakeoffType == TakeoffLanding.Phase.FAILED;
   }

   private TakeoffLanding.Command getLandedTakeoff(TakeoffLanding.Failure certifiedStalledType, String landedTakeoffText) {
      this.landedTakeoffType = TakeoffLanding.Phase.FAILED;
      this.certifiedStalledType = certifiedStalledType;
      this.occupiedLandingFlag = false;
      return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, false, landedTakeoffText);
   }

   public double getHypot() {
      return this.currentHypot;
   }

   public boolean isLandedNodeReached() {
      return this.landedSettlingActive;
   }

   public boolean isOccupiedLandingFlag() {
      return this.occupiedLandingFlag;
   }

   public TakeoffLanding.Command tickTakeoffLanding(TakeoffLanding.Sample sample) {
      if (this.firstSampleTick == Integer.MIN_VALUE) {
         this.firstSampleTick = sample.tick();
         this.takeoffLandingValue = sample.tick();
      }

      double hypot = Math.hypot(this.takeoffLandingAmount - sample.x(), this.landingTargetZ - sample.z());
      double amount = Math.abs(sample.y() - this.settling.landingY());
      double signumAmount = this.getSignumAmount(sample.x(), sample.z());
      this.takeoffBrakingAmount = Double.isNaN(this.previousSpeedAmount) ? 0.0 : this.previousSpeedAmount - signumAmount;
      this.previousSpeedAmount = signumAmount;
      this.hypotTakeoffLanding = hypot;
      this.lastApproachAmount = amount;
      boolean takeoffLandingFlag = false;
      if (hypot < this.currentHypot - 0.06) {
         this.currentHypot = hypot;
         takeoffLandingFlag = this.landedTakeoffType == TakeoffLanding.Phase.APPROACH || this.landedTakeoffType == TakeoffLanding.Phase.WALK;
      }

      if (amount < this.bestApproachAmount - 0.06) {
         this.bestApproachAmount = amount;
         takeoffLandingFlag = true;
      }

      if (sample.onGround()
         && this.settling.isLandedOnTarget(sample.x(), sample.y(), sample.z(), true)
         && this.landedTakeoffType != TakeoffLanding.Phase.COMPLETE
         && this.landedTakeoffType != TakeoffLanding.Phase.FAILED) {
         this.landedTakeoffType = TakeoffLanding.Phase.COMPLETE;
         this.occupiedLandingFlag = true;
         return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, true, "landing pose occupied");
      } else {
         switch (this.landedTakeoffType) {
            case APPROACH:
               boolean takeoffLandingFlag2 = sample.feetY() == this.settling.getFromY();
               if (!this.primingEstimateFlag) {
                  this.primingEstimateFlag = true;
                  return this.getSettlingRequired(false, false, false, takeoffLandingFlag, "approach: priming the speed estimate");
               } else {
                  if (sample.onGround() && takeoffLandingFlag2 && this.takeoffSpeedCount == 0) {
                     if (sample.sprinting()) {
                        return this.getSettlingRequired(false, false, false, takeoffLandingFlag, "sprint still active: braking");
                     }

                     if (signumAmount <= this.settling.takeoffFar()) {
                        if (signumAmount >= this.settling.takeoffNear() && this.takeoffBrakingAmount < this.gainingSlipperyAmount) {
                           return this.getSettlingRequired(true, false, false, takeoffLandingFlag, "slippery takeoff: gaining speed first");
                        }

                        if (!(signumAmount < this.settling.takeoffNear()) && !(this.takeoffBrakingAmount <= this.settling.maxTakeoffSpeed())) {
                           return this.getSettlingRequired(false, false, false, takeoffLandingFlag, "braking for the takeoff");
                        }

                        this.takeoffSpeedCount++;
                        this.landedTakeoffType = TakeoffLanding.Phase.TAKEOFF;
                        this.takeoffLandingValue = sample.tick();
                        boolean takeoffLandingFlag3 = true;
                        return this.getSettlingRequired(true, true, true, takeoffLandingFlag3, signumAmount < this.settling.takeoffNear() ? "late takeoff from the edge" : "takeoff");
                     }

                     if (signumAmount <= this.settling.brakeFrom() && this.takeoffBrakingAmount > this.settling.maxTakeoffSpeed()) {
                        return this.getSettlingRequired(false, false, false, takeoffLandingFlag, "braking for the takeoff");
                     }
                  }

                  if (sample.onGround() && sample.feetY() != this.settling.getFromY() && this.settling.isAtLandingHeight(sample.y())) {
                     this.landedTakeoffType = TakeoffLanding.Phase.WALK;
                     this.takeoffLandingValue = sample.tick();
                     boolean takeoffLandingFlag4 = true;
                     return this.getSettlingRequired(true, false, false, takeoffLandingFlag4, "walk: at landing level");
                  } else if (sample.tick() - this.takeoffLandingValue > 60) {
                     return this.getLandedTakeoff(TakeoffLanding.Failure.STALLED, "approach never reached the takeoff zone");
                  } else {
                     if (!sample.onGround()) {
                        return this.getSettlingRequired(false, false, false, takeoffLandingFlag, "approach: airborne - waiting to land");
                     }

                     return this.getSettlingRequired(true, false, true, takeoffLandingFlag, "approach");
                  }
               }
            case TAKEOFF:
               if (!sample.onGround()) {
                  this.landedTakeoffType = TakeoffLanding.Phase.AIRBORNE;
                  this.takeoffLandingValue = sample.tick();
                  this.airborneTakeoffCount = 0;
                  return this.getSettlingRequired(true, false, true, true, "airborne");
               } else {
                  if (sample.tick() - this.takeoffLandingValue > 4) {
                     return this.getLandedTakeoff(TakeoffLanding.Failure.NO_TAKEOFF, "jump pressed but the body never left the ground");
                  }

                  return this.getSettlingRequired(true, true, true, takeoffLandingFlag, "takeoff pending");
               }
            case AIRBORNE:
               this.airborneTakeoffCount++;
               if (sample.onGround()) {
                  this.airborneAllowsCount++;
                  return this.getLandedSettling(sample, takeoffLandingFlag);
               } else {
                  if (this.airborneTakeoffCount > this.settling.maxAirborneTicks()) {
                     return this.getLandedTakeoff(TakeoffLanding.Failure.AIRBORNE_TIMEOUT, "airborne longer than the certificate allows");
                  }

                  return this.getSettlingRequired(true, false, true, takeoffLandingFlag, "airborne");
               }
            case LANDED:
               return this.getLandedSettling(sample, takeoffLandingFlag);
            case WALK:
               if (!sample.onGround() || !(sample.y() > this.settling.landingY() + 0.02) && !(sample.y() < this.settling.landingY() - 0.6 - 0.02)) {
                  return sample.tick() - this.takeoffLandingValue > this.settling.completionWindowTicks()
                     ? this.getLandedTakeoff(TakeoffLanding.Failure.STALLED, "walk onto the landing cell exceeded the window")
                     : this.getSettlingRequired(true, false, false, takeoffLandingFlag, "walk to landing cell");
               } else {
                  return this.getLandedTakeoff(TakeoffLanding.Failure.LANDING_HEIGHT_MISMATCH, "left the landing level while walking onto the cell");
               }
            case COMPLETE:
               return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, false, "complete");
            default:
               return new TakeoffLanding.Command(false, false, false, this.landedTakeoffType, false, this.certifiedStalledType.name());
         }
      }
   }

   public int getClampedValue(int takeoffLandingValue) {
      return this.firstSampleTick == Integer.MIN_VALUE ? 0 : takeoffLandingValue - this.firstSampleTick;
   }

   private TakeoffLanding.Command getSettlingRequired(boolean settlingRequiredFlag, boolean settlingRequiredFlag2, boolean settlingRequiredFlag3, boolean occupiedLandingFlag, String settlingRequiredText) {
      this.occupiedLandingFlag = occupiedLandingFlag;
      return new TakeoffLanding.Command(
         settlingRequiredFlag, settlingRequiredFlag2 && this.settling.jumpRequired(), settlingRequiredFlag3 && this.settling.sprintRequired(), this.landedTakeoffType, occupiedLandingFlag, settlingRequiredText
      );
   }

   public TakeoffLanding(GapSupportData settling) {
      this.certifiedStalledType = TakeoffLanding.Failure.NONE;
      this.firstSampleTick = Integer.MIN_VALUE;
      this.takeoffLandingValue = Integer.MIN_VALUE;
      this.currentHypot = Double.POSITIVE_INFINITY;
      this.bestApproachAmount = Double.POSITIVE_INFINITY;
      this.previousSpeedAmount = Double.NaN;
      this.gainingSlipperyAmount = Double.NEGATIVE_INFINITY;
      this.settling = settling;
      this.approachSignumX = Integer.signum(settling.getToX() - settling.getFromX());
      this.approachSignumZ = Integer.signum(settling.getToZ() - settling.getFromZ());
      this.takeoffLandingAmount = settling.getToX() + 0.5;
      this.landingTargetZ = settling.getToZ() + 0.5;
      if (!settling.isCertified()) {
         this.landedTakeoffType = TakeoffLanding.Phase.FAILED;
         this.certifiedStalledType = TakeoffLanding.Failure.NOT_CERTIFIED;
      }
   }

   public double getSignumAmount(double signumValue, double signumValue2) {
      return (this.takeoffLandingAmount - signumValue) * this.approachSignumX + (this.landingTargetZ - signumValue2) * this.approachSignumZ;
   }

   public TakeoffLanding.Failure getCertifiedStalledType() {
      return this.certifiedStalledType;
   }

   public boolean isLandedTakeoffStarted() {
      return this.landedTakeoffType == TakeoffLanding.Phase.COMPLETE || this.landedTakeoffType == TakeoffLanding.Phase.FAILED;
   }

   public double getBestApproachAmount() {
      return this.bestApproachAmount;
   }

   public boolean isLandedTakeoffDone() {
      return this.landedTakeoffType == TakeoffLanding.Phase.COMPLETE;
   }

   public String getFormat(int takeoffLandingValue) {
      return String.format(
         Locale.ROOT,
         "ticksOnNode=%d horizontalError=%.2f verticalError=%.2f expectedLandingY=%.3f trajectoryPhase=%s attempts=%d groundContacts=%d meaningfulProgress=%s failureReason=%s",
         this.getClampedValue(takeoffLandingValue),
         this.hypotTakeoffLanding,
         this.lastApproachAmount,
         this.settling.landingY(),
         this.landedTakeoffType,
         this.takeoffSpeedCount,
         this.airborneAllowsCount,
         this.occupiedLandingFlag,
         this.certifiedStalledType
      );
   }

   public double getLastApproachAmount() {
      return this.lastApproachAmount;
   }

   public int getAirborneAllowsCount() {
      return this.airborneAllowsCount;
   }

   public GapSupportData getSettling() {
      return this.settling;
   }

   public TakeoffLanding getLandedCell(long landedCellTime) {
      this.landedCellTime = landedCellTime;
      return this;
   }

   public int getTakeoffSpeedCount() {
      return this.takeoffSpeedCount;
   }

   public record Command(boolean forward, boolean jump, boolean sprint, TakeoffLanding.Phase phase, boolean progress, String note) {
   }

   public static enum Failure {
      NONE,
      NO_TAKEOFF,
      AIRBORNE_TIMEOUT,
      LANDING_HEIGHT_MISMATCH,
      LANDED_UNREACHABLE,
      STALLED,
      NOT_CERTIFIED;
   }

   public static enum Phase {
      APPROACH,
      TAKEOFF,
      AIRBORNE,
      LANDED,
      WALK,
      COMPLETE,
      FAILED;
   }

   public record Sample(int tick, double x, double y, double z, int feetX, int feetY, int feetZ, boolean onGround, boolean collision, boolean sprinting) {
      public Sample(int feetY, double x, double feetZ, double tick, int feetX, int collision, int z, boolean y, boolean onGround) {
         this(feetY, x, feetZ, tick, feetX, collision, z, y, onGround, false);
      }
   }
}
