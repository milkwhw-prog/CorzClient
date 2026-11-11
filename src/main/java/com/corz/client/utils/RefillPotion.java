package com.corz.client.utils;

public class RefillPotion {
   private long timeMillis = System.currentTimeMillis();

   public boolean canFlag(double refillPotionValue) {
      return this.getTime() >= refillPotionValue;
   }

   public void updateTime() {
      this.timeMillis = System.currentTimeMillis();
   }

   public long getTime() {
      return System.currentTimeMillis() - this.timeMillis;
   }
}
