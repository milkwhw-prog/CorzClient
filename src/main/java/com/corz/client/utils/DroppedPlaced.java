package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.List;

public final class DroppedPlaced {
   private final int listSize;
   private final ArrayDeque<DroppedPlaced.Member> remaining = new ArrayDeque<>();
   private final long stand;
   private int dropped;
   private int placed;
   private final long droppedTime;

   public String formatRemainingMaterial() {
      DroppedPlaced.Member remaining = this.remaining.peek();
      return remaining == null ? null : remaining.material();
   }

   public boolean isEmpty() {
      return this.remaining.isEmpty();
   }

   public int getDropped() {
      return this.dropped;
   }

   public DroppedPlaced.Member getPeek() {
      return this.remaining.peek();
   }

   public boolean canStand(long standValue) {
      return standValue == this.stand;
   }

   public boolean isRemainingTier() {
      if (this.remaining.isEmpty()) {
         return false;
      } else {
         for (DroppedPlaced.Member member : this.remaining) {
            if (member.tier() == 0 || !member.sneak()) {
               return false;
            }
         }

         return true;
      }
   }

   public long getStand() {
      return this.stand;
   }

   public void handlePlacedRemaining(long placedRemainingValue) {
      this.placed++;
      this.remaining.removeIf(member -> member.cell() == placedRemainingValue);
   }

   public void handleRemaining(DroppedPlaced.Member member) {
      if (member != null) {
         this.remaining.addFirst(member);
      }
   }

   public int getLength() {
      return this.listSize;
   }

   public long getTime() {
      return this.droppedTime;
   }

   public DroppedPlaced.Member getPoll() {
      return this.remaining.poll();
   }

   public DroppedPlaced(long stand2, long time, List<DroppedPlaced.Member> list) {
      this.stand = stand2;
      this.droppedTime = time;
      this.listSize = list.size();
      this.remaining.addAll(list);
   }

   public int getRemaining() {
      return this.remaining.size();
   }

   @Override
   public String toString() {
      return "DeckRun{stand="
         + ProofSupportUtils.getValueInt(this.stand)
         + ","
         + ProofSupportUtils.getValue(this.stand)
         + ","
         + ProofSupportUtils.getValueForLong(this.stand)
         + " length="
         + this.listSize
         + " remaining="
         + this.remaining.size()
         + " placed="
         + this.placed
         + " dropped="
         + this.dropped
         + "}";
   }

   public int getPlaced() {
      return this.placed;
   }

   public boolean isRemoveIf(long droppedPlacedValue) {
      boolean removeIf = this.remaining.removeIf(cellx -> cellx.cell() == droppedPlacedValue);
      if (removeIf) {
         this.dropped++;
      }

      return removeIf;
   }

   public static enum Abort {
      EXHAUSTED,
      STAND_CHANGED,
      MATERIAL_CHANGED,
      GEOMETRY_CHANGED,
      MOVE_REQUIRED,
      SAFETY,
      INVALIDATED,
      RESET;
   }

   public record Member(long cell, int tier, boolean sneak, String material, String face, float yaw, float pitch) {
   }
}
