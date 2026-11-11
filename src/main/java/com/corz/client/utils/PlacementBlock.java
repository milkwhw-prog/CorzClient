package com.corz.client.utils;

import java.util.Locale;

public final class PlacementBlock {
   public int placementAckRaces;
   public int placementRollbacks;
   public int placementPredicted;
   public int placementAcks;
   private PlacementBlock.Ack acksPlacementAck;
   public int placementConfirmed;
   public int placementUnconfirmed;

   public PlacementBlock.Ack getAcksPlacementAck() {
      return this.acksPlacementAck;
   }

   public static int getMax(int placementBlockBound) {
      return Math.max(2, Math.min(10, (int)Math.ceil(2.0 * Math.max(0, placementBlockBound) / 50.0) + 1));
   }

   public void update() {
      this.acksPlacementAck = null;
   }

   public PlacementBlock.Ack getAcksPlacement(
      long acksPlacementValue, long acksPlacementValue2, String acksPlacementText, int acksPlacementValue3, double acksPlacementValue4, double acksPlacementValue5, double acksPlacementValue6, boolean acksPlacementFlag, int acksPlacementValue7, int acksPlacementValue8, int acksPlacementValue9
   ) {
      this.acksPlacementAck = new PlacementBlock.Ack(acksPlacementValue, acksPlacementValue2, acksPlacementText, acksPlacementValue3, acksPlacementValue4, acksPlacementValue5, acksPlacementValue6, acksPlacementFlag, acksPlacementValue7, acksPlacementValue8, acksPlacementValue9);
      this.placementAcks++;
      return this.acksPlacementAck;
   }

   public PlacementBlock.Kind getPlacementRemoved(int removedBlockValue, boolean placementRemovedFlag, boolean placementRemovedFlag2, int blockRemovedValue, int placementRemovedValue, int placementRemovedBound) {
      PlacementBlock.Ack acksPlacementAck123 = this.acksPlacementAck;
      if (acksPlacementAck123 == null) {
         return PlacementBlock.Kind.PENDING;
      } else if (acksPlacementAck123.placementServerType == PlacementBlock.Kind.SERVER_CONFIRMED_PLACEMENT
         || acksPlacementAck123.placementServerType == PlacementBlock.Kind.SERVER_ROLLBACK
         || acksPlacementAck123.placementServerType == PlacementBlock.Kind.NO_CONFIRMATION) {
         return acksPlacementAck123.placementServerType;
      } else if (placementRemovedFlag) {
         if (acksPlacementAck123.absentRaceValue > 0) {
            this.placementAckRaces++;
            acksPlacementAck123.absentRaceValue = 0;
            acksPlacementAck123.blockRemovedText = "absent for a moment, then held (ack race)";
         }

         if (!acksPlacementAck123.isRemovedBlock()) {
            acksPlacementAck123.removedBlockValue = removedBlockValue;
            if (acksPlacementAck123.placementServerType == PlacementBlock.Kind.PENDING) {
               acksPlacementAck123.placementServerType = PlacementBlock.Kind.CLIENT_PREDICTED_PLACEMENT;
               this.placementPredicted++;
            }
         }

         if (placementRemovedFlag2) {
            acksPlacementAck123.placementServerType = PlacementBlock.Kind.SERVER_CONFIRMED_PLACEMENT;
            acksPlacementAck123.removedBlockValuePlacementRe = removedBlockValue;
            acksPlacementAck123.blockRemovedValuePlacementRe = blockRemovedValue;
            acksPlacementAck123.blockRemovedText = "server block update holds the block";
            this.placementConfirmed++;
         }

         return acksPlacementAck123.placementServerType;
      } else if (acksPlacementAck123.isRemovedBlock()) {
         if (++acksPlacementAck123.absentRaceValue < Math.max(1, placementRemovedBound)) {
            acksPlacementAck123.placementServerType = PlacementBlock.Kind.CLIENT_PREDICTED_PLACEMENT;
            return acksPlacementAck123.placementServerType;
         } else {
            acksPlacementAck123.placementServerType = PlacementBlock.Kind.SERVER_ROLLBACK;
            acksPlacementAck123.removedBlockValuePlacementRe = removedBlockValue;
            acksPlacementAck123.removedReturnedValue = removedBlockValue - acksPlacementAck123.removedBlockValue;
            acksPlacementAck123.blockRemovedValuePlacementRe = blockRemovedValue;
            acksPlacementAck123.blockRemovedText = blockRemovedValue >= acksPlacementAck123.blockRemovedValue2PlacementR
               ? "block removed and the item returned (server rejected the placement)"
               : "block removed";
            this.placementRollbacks++;
            return acksPlacementAck123.placementServerType;
         }
      } else {
         if (removedBlockValue - acksPlacementAck123.blockRemovedValue > placementRemovedValue) {
            acksPlacementAck123.placementServerType = PlacementBlock.Kind.NO_CONFIRMATION;
            acksPlacementAck123.removedBlockValuePlacementRe = removedBlockValue;
            acksPlacementAck123.blockRemovedValuePlacementRe = blockRemovedValue;
            acksPlacementAck123.blockRemovedText = blockRemovedValue < acksPlacementAck123.blockRemovedValue2PlacementR ? "item consumed but no block appeared" : "no block, item untouched";
            this.placementUnconfirmed++;
         }

         return acksPlacementAck123.placementServerType;
      }
   }

   public PlacementBlock.Kind getPlacementServerType(int placementServerValue, boolean placementServerFlag, boolean placementServerFlag2, int blockRemovedValue, int placementRemovedValue) {
      return this.getPlacementRemoved(placementServerValue, placementServerFlag, placementServerFlag2, blockRemovedValue, placementRemovedValue, 1);
   }

   public String formatPlacementRaces() {
      return "placementAcks="
         + this.placementAcks
         + " placementPredicted="
         + this.placementPredicted
         + " placementConfirmed="
         + this.placementConfirmed
         + " placementRollbacks="
         + this.placementRollbacks
         + " placementUnconfirmed="
         + this.placementUnconfirmed
         + " placementAckRaces="
         + this.placementAckRaces;
   }

   public static final class Ack {
      public final int blockRemovedValue;
      public final int blockRemovedValue2PlacementR;
      public final boolean hitBlockActive;
      public final String hopperText;
      public int blockRemovedValuePlacementRe = -1;
      public final int directionIndex;
      public int removedBlockValue = -1;
      public int absentRaceValue;
      public int removedReturnedValue = -1;
      public int removedBlockValuePlacementRe = -1;
      public final long targetPosLong;
      public PlacementBlock.Kind placementServerType = PlacementBlock.Kind.PENDING;
      public final double hitOffset;
      public final int hopperBlockValue;
      public final double hitBlockAmount;
      public String blockRemovedText = "";
      public final long hitBlockValue;
      public final double ackAmount;

      public String getFormat() {
         return String.format(
            Locale.ROOT,
            "target=%s support=%s supportType=%s face=%d hitVec=%.2f,%.2f,%.2f sneak=%s ping=%d predicted=%s confirmed=%s rollback=%s ticksUntilRollback=%d inventoryBefore=%d inventoryAfter=%d reason=%s",
            ProofSupportUtils.formatText(this.targetPosLong),
            ProofSupportUtils.formatText(this.hitBlockValue),
            this.hopperText,
            this.directionIndex,
            this.hitOffset,
            this.hitBlockAmount,
            this.ackAmount,
            this.hitBlockActive,
            this.hopperBlockValue,
            this.isRemovedBlock(),
            this.placementServerType == PlacementBlock.Kind.SERVER_CONFIRMED_PLACEMENT,
            this.placementServerType == PlacementBlock.Kind.SERVER_ROLLBACK,
            this.removedReturnedValue,
            this.blockRemovedValue2PlacementR,
            this.blockRemovedValuePlacementRe,
            this.blockRemovedText
         );
      }

      public boolean isRemovedBlock() {
         return this.removedBlockValue >= 0;
      }

      Ack(long time, long time2, String hopperText, int value, double amount, double amount2, double amount3, boolean flag, int value2, int blockRemovedValue, int blockRemovedValue2) {
         this.targetPosLong = time;
         this.hitBlockValue = time2;
         this.hopperText = hopperText;
         this.directionIndex = value;
         this.hitOffset = amount;
         this.hitBlockAmount = amount2;
         this.ackAmount = amount3;
         this.hitBlockActive = flag;
         this.hopperBlockValue = value2;
         this.blockRemovedValue = blockRemovedValue;
         this.blockRemovedValue2PlacementR = blockRemovedValue2;
      }
   }

   public static enum Kind {
      PENDING,
      CLIENT_PREDICTED_PLACEMENT,
      SERVER_CONFIRMED_PLACEMENT,
      SERVER_ROLLBACK,
      NO_CONFIRMATION;
   }
}
