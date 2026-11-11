package com.corz.client.utils;

public final class RotationSampleHistory {
   private final float[] sampleYaws;
   private final long[] sampleTimestamps = new long[64];
   private final float[] samplePitches;
   private final int[] sampleTicks = new int[64];
   private int sampleCount;
   private int writeIndex;

   public void clear() {
      this.sampleCount = 0;
      this.writeIndex = 0;
   }

   public int getSampleCount() {
      return this.sampleCount;
   }

   public float[] getLatestRotation() {
      if (this.sampleCount == 0) {
         return null;
      } else {
         int floorMod = Math.floorMod(this.writeIndex - 1, 64);
         return new float[]{this.sampleYaws[floorMod], this.samplePitches[floorMod]};
      }
   }

   static float normalizeDegrees(float total) {
      total %= 360.0F;
      if (total >= 180.0F) {
         total -= 360.0F;
      }

      if (total < -180.0F) {
         total += 360.0F;
      }

      return total;
   }

   public void addSample(long longValue, int n, float f, float f2) {
      this.sampleTimestamps[this.writeIndex] = longValue;
      this.sampleTicks[this.writeIndex] = n;
      this.sampleYaws[this.writeIndex] = f;
      this.samplePitches[this.writeIndex] = f2;
      this.writeIndex = (this.writeIndex + 1) % 64;
      if (this.sampleCount < 64) {
         this.sampleCount++;
      }
   }

   public long[] findStableSince(float f, float f2, float limit) {
      return this.findOldestMatchingSample((windowx, yawx) -> Math.abs(normalizeDegrees(windowx - f)) <= limit && Math.abs(yawx - f2) <= limit);
   }

   public RotationSampleHistory() {
      this.sampleYaws = new float[64];
      this.samplePitches = new float[64];
   }

   public long[] findOldestMatchingSample(RotationSampleHistory.LookTest lookTest) {
      int index = -1;

      for (int i = 0; i < this.sampleCount; i++) {
         int floorMod = Math.floorMod(this.writeIndex - 1 - i, 64);
         if (!lookTest.matches(this.sampleYaws[floorMod], this.samplePitches[floorMod])) {
            break;
         }

         index = floorMod;
      }

      return index < 0 ? null : new long[]{this.sampleTimestamps[index], this.sampleTicks[index]};
   }

   public interface LookTest {
      boolean matches(float lookTestValue, float lookTestValue2);
   }
}
