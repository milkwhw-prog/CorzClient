package com.corz.client.utils;

public final class DepPurpose {
   public final long purposeId;
   public final int placedTick;
   private boolean placed;
   private boolean depSat;
   public final long cellKey;
   public final long owner;
   private boolean removed;
   public final DepPurpose.Purpose purpose;

   public void updateDepSat() {
      this.depSat = true;
   }

   public boolean isRemoved() {
      return this.removed;
   }

   public boolean isRemovalConfirmed() {
      return this.removed;
   }

   public DepPurpose(long depPurposeValue, DepPurpose.Purpose purpose2, long owner, long temp, int value) {
      this.purposeId = depPurposeValue;
      this.purpose = purpose2;
      this.owner = owner;
      this.cellKey = temp;
      this.placedTick = value;
   }

   public void updateRemoved() {
      this.removed = true;
   }

   public boolean isPlaced() {
      return this.placed;
   }

   @Override
   public String toString() {
      return "SupportMission{id="
         + this.purposeId
         + " purpose="
         + this.purpose
         + " owner="
         + this.owner
         + " temp="
         + this.cellKey
         + " placed="
         + this.placed
         + " depSat="
         + this.depSat
         + " removed="
         + this.removed
         + "}";
   }

   public boolean isDepSat() {
      return this.depSat;
   }

   public void updatePlaced() {
      this.placed = true;
   }

   public boolean canDepSat(int depSatValue, int depSatValue2) {
      return this.placed && this.depSat && !this.removed && depSatValue - this.placedTick >= depSatValue2;
   }

   public static enum Purpose {
      FACING,
      ATTACHMENT,
      CONNECTION,
      WORK_PLATFORM;
   }
}
