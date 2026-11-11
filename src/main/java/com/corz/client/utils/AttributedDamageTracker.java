package com.corz.client.utils;

public final class AttributedDamageTracker {
   private final float[] damageAmounts;
   private final long[] damageTicks;
   private int eventWriteIndex;
   private int damageSampleCount;
   private long windowEndTick;
   private float totalAttributedDamage;
   private int damageWriteIndex;
   private int eventSampleCount;
   private final long[] eventTicks = new long[64];

   public float getTotalAttributedDamage() {
      return this.totalAttributedDamage;
   }

   public void extendWindow(long longValue) {
      this.windowEndTick = Math.max(this.windowEndTick, longValue + 2L);
   }

   public boolean isWithinWindow(long longValue) {
      return this.windowEndTick != Long.MIN_VALUE && longValue <= this.windowEndTick;
   }

   public void handleMax(long longValue, int bound) {
      this.windowEndTick = Math.max(this.windowEndTick, longValue + Math.max(0, bound) + 2L);
   }

   public float getRecentDamageSum(long limit) {
      float timesAmounts = 0.0F;

      for (int i = 0; i < this.damageSampleCount; i++) {
         int floorMod = Math.floorMod(this.damageWriteIndex - 1 - i, 64);
         long limit2 = this.damageTicks[floorMod];
         if (limit2 <= limit && limit2 > limit - 40L) {
            timesAmounts += this.damageAmounts[floorMod];
         }
      }

      return timesAmounts;
   }

   private boolean hasRecentEvent(long limit) {
      for (int i = 0; i < this.eventSampleCount; i++) {
         long longValue = this.eventTicks[Math.floorMod(this.eventWriteIndex - 1 - i, 64)];
         if (longValue <= limit && limit - longValue <= 2L) {
            return true;
         }
      }

      return false;
   }

   public void reset() {
      this.eventWriteIndex = 0;
      this.eventSampleCount = 0;
      this.damageWriteIndex = 0;
      this.damageSampleCount = 0;
      this.totalAttributedDamage = 0.0F;
      this.windowEndTick = Long.MIN_VALUE;
   }

   public AttributedDamageTracker() {
      this.damageTicks = new long[64];
      this.damageAmounts = new float[64];
      this.windowEndTick = Long.MIN_VALUE;
   }

   public void recordDamage(long longValue, float limit) {
      if (limit > 0.0F && Float.isFinite(limit)) {
         if (this.hasRecentEvent(longValue)) {
            this.damageTicks[this.damageWriteIndex] = longValue;
            this.damageAmounts[this.damageWriteIndex] = limit;
            this.damageWriteIndex = (this.damageWriteIndex + 1) % 64;
            if (this.damageSampleCount < 64) {
               this.damageSampleCount++;
            }

            this.totalAttributedDamage += limit;
         }
      }
   }

   public void recordEvent(long longValue) {
      this.eventTicks[this.eventWriteIndex] = longValue;
      this.eventWriteIndex = (this.eventWriteIndex + 1) % 64;
      if (this.eventSampleCount < 64) {
         this.eventSampleCount++;
      }
   }
}
