package com.corz.client.utils;

public final class KnifeCueClock {
   private int cueIndex;
   private float currentProgress;
   private float knifeCueAmount;
   private static final float[] knifeCueTable = new float[0];
   private float[] table = knifeCueTable;
   private long lastTrackStamp;
   private boolean knifeCueActive;

   public boolean isFlag() {
      return this.knifeCueActive;
   }

   public float getKnifeCueAmount() {
      return this.knifeCueAmount;
   }

   public void handle(KnifeCueClock.Sink sink) {
      this.handleAmountAmounts(0L, null, 0.0F, 0.0F, true, sink);
   }

   public int getValue() {
      return this.cueIndex;
   }

   public long getTime() {
      return this.lastTrackStamp;
   }

   public KnifeCueClock() {
      this.knifeCueAmount = Float.NEGATIVE_INFINITY;
   }

   public void handleAmountAmounts(long cueClockValue, float[] knifeCueValues, float amount, float knifeCueAmount2, boolean knifeCueFlag, KnifeCueClock.Sink sink) {
      if (cueClockValue != this.lastTrackStamp) {
         long knifeCueValue = this.lastTrackStamp;
         boolean knifeCueFlag2 = knifeCueValue != 0L && this.knifeCueActive && this.knifeCueAmount < this.currentProgress - 0.02F;
         this.lastTrackStamp = cueClockValue;
         this.table = cueClockValue != 0L && knifeCueValues != null ? knifeCueValues : knifeCueTable;
         this.currentProgress = amount;
         this.cueIndex = 0;
         this.knifeCueAmount = Float.NEGATIVE_INFINITY;
         this.knifeCueActive = false;
         if (knifeCueFlag2 && sink != null) {
            sink.handle(knifeCueValue);
         }
      }

      if (cueClockValue != 0L) {
         if (!(knifeCueAmount2 >= this.knifeCueAmount)) {
            knifeCueAmount2 = this.knifeCueAmount;
         }

         this.knifeCueAmount = knifeCueAmount2;

         while (this.cueIndex < this.table.length && this.table[this.cueIndex] <= knifeCueAmount2) {
            int index = this.cueIndex++;
            if (!knifeCueFlag && !(knifeCueAmount2 - this.table[index] > 0.1F) && sink != null) {
               this.knifeCueActive = true;
               sink.handleInspectSword(index);
            }
         }
      }
   }

   public interface Sink {
      void handleInspectSword(int inspectSwordValue);

      void handle(long sinkValue);
   }
}
