package com.corz.client.utils;

public final class Time {
   private int eventCount;
   private long accumulatedTime;

   public void mixHashSample(int blockX, int blockY, int blockZ, int salt) {
      long coordMix = blockX * 73856093L ^ blockY * 19349663L ^ blockZ * 83492791L;
      long saltedMix = coordMix * -7046029254386353131L + (salt & 4294967295L);
      long shifted1 = saltedMix ^ saltedMix >>> 29;
      long multiplied = shifted1 * -4658895280553007687L;
      long finalMix = multiplied ^ multiplied >>> 32;
      this.accumulatedTime += finalMix;
      this.eventCount++;
   }

   public long getHashDigest() {
      return this.accumulatedTime * -7046029254386353131L ^ this.eventCount * 1099511628211L;
   }

   public int getEventCount() {
      return this.eventCount;
   }
}
