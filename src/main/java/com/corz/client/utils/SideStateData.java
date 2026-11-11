package com.corz.client.utils;

import java.util.Locale;

public record SideStateData(
   long targetBlock,
   boolean desiredWaterlogged,
   int bucketSlot,
   long interactionBlock,
   int interactionFace,
   double hitX,
   double hitY,
   double hitZ,
   long expectedFluidTarget,
   boolean containerInteractionRisk,
   long geometryFingerprint
) {

   public static long getTime(long hitBlockFillable, boolean sideOrdinal, int hitBlock) {
      if (sideOrdinal) {
         return hitBlockFillable;
      } else {
         int sideValue = ProofSupportUtils.getValueInt(hitBlockFillable);
         int builderAutoValue = ProofSupportUtils.getValue(hitBlockFillable);
         int valueForLong = ProofSupportUtils.getValueForLong(hitBlockFillable);

         return switch (hitBlock) {
            case 0 -> ProofSupportUtils.getTime(sideValue, builderAutoValue - 1, valueForLong);
            case 1 -> ProofSupportUtils.getTime(sideValue, builderAutoValue + 1, valueForLong);
            case 2 -> ProofSupportUtils.getTime(sideValue, builderAutoValue, valueForLong - 1);
            case 3 -> ProofSupportUtils.getTime(sideValue, builderAutoValue, valueForLong + 1);
            case 4 -> ProofSupportUtils.getTime(sideValue - 1, builderAutoValue, valueForLong);
            default -> ProofSupportUtils.getTime(sideValue + 1, builderAutoValue, valueForLong);
         };
      }
   }

   public static SideStateData getSideStateData(
      long bucketSlot,
      boolean hz,
      int desired,
      long side,
      boolean fingerprint,
      int container,
      double hitBlock,
      double hx,
      double hy,
      boolean target,
      long hitBlockFillable
   ) {
      long sideValue = getTime(side, fingerprint, container);
      return side == bucketSlot && sideValue == bucketSlot
         ? new SideStateData(bucketSlot, hz, desired, side, container, hitBlock, hx, hy, sideValue, target, hitBlockFillable)
         : null;
   }

   public boolean canExpectedInteraction(long sideNow, boolean hitBlockNow, int fillableNow) {
      return sideNow == this.interactionBlock && getTime(sideNow, hitBlockNow, fillableNow) == this.expectedFluidTarget;
   }

   public static SideStateData.Action getDrainFill(boolean waterloggable, boolean isWaterlogged, boolean wantWaterlogged) {
      if (waterloggable && isWaterlogged != wantWaterlogged) {
         return isWaterlogged ? SideStateData.Action.FILL : SideStateData.Action.DRAIN;
      } else {
         return SideStateData.Action.NONE;
      }
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "target=%s desiredWaterlogged=%s bucketSlot=%d interactionBlock=%s interactionFace=%d hitVec=%.2f,%.2f,%.2f expectedFluidTarget=%s containerInteractionRisk=%s geometryFingerprint=%s",
         ProofSupportUtils.formatText(this.targetBlock),
         this.desiredWaterlogged,
         this.bucketSlot,
         ProofSupportUtils.formatText(this.interactionBlock),
         this.interactionFace,
         this.hitX,
         this.hitY,
         this.hitZ,
         ProofSupportUtils.formatText(this.expectedFluidTarget),
         this.containerInteractionRisk,
         Long.toUnsignedString(this.geometryFingerprint)
      );
   }

   public static enum Action {
      NONE,
      FILL,
      DRAIN;
   }
}
