package com.corz.client.utils;
import com.corz.client.utils.ShardAxeType;

public record ElseMaxData(
   ShardAxeType kind, int page, int slot, String name, long price, long maxCharge, long secondsLeft, double durability, String fingerprint
) {
   public ElseMaxData(ShardAxeType kind, int page, int slot, String name, long price, long maxCharge, long secondsLeft, double durability, String fingerprint) {
      maxCharge = Math.max(price, maxCharge);
      this.kind = kind;
      this.page = page;
      this.slot = slot;
      this.name = name;
      this.price = price;
      this.maxCharge = maxCharge;
      this.secondsLeft = secondsLeft;
      this.durability = durability;
      this.fingerprint = fingerprint;
   }

   public double getSecondsPrice() {
      return this.price * 3600.0 / Math.max(1L, this.secondsLeft);
   }
}
