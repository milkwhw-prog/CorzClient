package com.corz.client.utils;

public final class MaxBuyer {
   private int penaltyTicks;
   private int lastBuyerValue;
   private int buyerValue;

   public boolean canMax(int buyerValue) {
      this.handleMax(buyerValue);
      return this.penaltyTicks + 20 <= 120;
   }

   public boolean canMaxForInt(int buyerLimit) {
      this.handleMax(buyerLimit);
      return buyerLimit >= this.buyerValue && this.penaltyTicks + 20 <= 120;
   }

   private void handleMax(int value) {
      if (value > this.lastBuyerValue) {
         this.penaltyTicks = Math.max(0, this.penaltyTicks - (value - this.lastBuyerValue));
         this.lastBuyerValue = value;
      }
   }

   public int getMax(int buyerValue) {
      this.handleMax(buyerValue);
      return this.penaltyTicks;
   }

   public int getMaxForInt(int buyerValue) {
      this.handleMax(buyerValue);
      int buyerBound = Math.max(0, this.buyerValue - buyerValue);
      int rocketsBuyBound = this.penaltyTicks + 20 <= 120 ? 0 : this.penaltyTicks + 20 - 120;
      return Math.max(buyerBound, rocketsBuyBound);
   }

   public void handleMax(int buyerValue, int buyerBound) {
      this.handleMax(buyerValue);
      this.penaltyTicks += 20;
      this.buyerValue = buyerValue + Math.max(0, buyerBound);
   }

   public void handleMaxForInt(int buyerValue) {
      this.handleMax(buyerValue);
      this.penaltyTicks += 20;
   }
}
