package com.corz.client.utils;

public record TotalPriceData(int slot, int count, long totalPrice) {
   public long getTotalPrice() {
      return this.count <= 0 ? Long.MAX_VALUE : (this.totalPrice + this.count - 1L) / this.count;
   }
}
