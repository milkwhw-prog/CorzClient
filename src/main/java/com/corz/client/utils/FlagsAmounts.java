package com.corz.client.utils;
import com.corz.client.utils.AmountAmounts;

public final class FlagsAmounts {
   private final int[] table;
   private final long[] expiryTicks;
   private int activeSlotCount;
   private final double[] dirX;
   private final double[] originY;
   private final double[] originX;
   private int nextSlot;
   private final double[] dirY;
   private final boolean[] slotUsed;

   public int getFlags() {
      return this.slotUsed.length;
   }

   public void updateFlags() {
      for (int i = 0; i < this.slotUsed.length; i++) {
         this.slotUsed[i] = false;
      }

      this.nextSlot = 0;
      this.activeSlotCount = 0;
   }

   public void handleFlagsAmounts(long longValue, int limit, double d, double d2, double factor, double factor2) {
      if (limit > 0 && Double.isFinite(d) && Double.isFinite(d2)) {
         double distance = Math.sqrt(factor * factor + factor2 * factor2);
         if (Double.isFinite(distance) && !(distance < 0.03)) {
            int nextSlot2 = this.nextSlot;
            if (this.slotUsed[nextSlot2]) {
               this.activeSlotCount--;
            }

            this.expiryTicks[nextSlot2] = longValue + limit;
            this.table[nextSlot2] = limit;
            this.originX[nextSlot2] = d;
            this.originY[nextSlot2] = d2;
            this.dirX[nextSlot2] = factor / distance;
            this.dirY[nextSlot2] = factor2 / distance;
            this.slotUsed[nextSlot2] = true;
            this.activeSlotCount++;
            this.nextSlot = (this.nextSlot + 1) % this.slotUsed.length;
         }
      }
   }

   public int getValue() {
      return this.activeSlotCount;
   }

   public FlagsAmounts(int bound) {
      int maxCapacity = Math.max(1, bound);
      this.expiryTicks = new long[maxCapacity];
      this.table = new int[maxCapacity];
      this.originX = new double[maxCapacity];
      this.originY = new double[maxCapacity];
      this.dirX = new double[maxCapacity];
      this.dirY = new double[maxCapacity];
      this.slotUsed = new boolean[maxCapacity];
   }

   public void handleAmountsFlags(long longValue, double d, double d2, AmountAmounts amountAmounts) {
      boolean flag = Double.isFinite(d) && Double.isFinite(d2);

      for (int i = 0; i < this.slotUsed.length; i++) {
         if (this.slotUsed[i]) {
            if (this.expiryTicks[i] == longValue) {
               if (flag) {
                  this.slotUsed[i] = false;
                  this.activeSlotCount--;
                  if (amountAmounts != null) {
                     double factor = d - this.originX[i];
                     double factor2 = d2 - this.originY[i];
                     double d3 = factor * this.dirX[i] + factor2 * this.dirY[i];
                     double d4 = factor * -this.dirY[i] + factor2 * this.dirX[i];
                     amountAmounts.handleAmountsMax(this.table[i], d3, d4);
                  }
               }
            } else if (longValue - this.expiryTicks[i] > 8L) {
               this.slotUsed[i] = false;
               this.activeSlotCount--;
            }
         }
      }
   }
}
