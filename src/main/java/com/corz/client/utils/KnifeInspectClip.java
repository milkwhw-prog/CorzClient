package com.corz.client.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.corz.client.utils.KnifeClipM9;
import com.corz.client.utils.KnifeClipStiletto;
import com.corz.client.utils.KnifeClipButterfly;

public final class KnifeInspectClip {
   private final KnifeInspectClip.Track[] tracks = new KnifeInspectClip.Track[17];
   static final int[] knifeInspectTable = new int[]{3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16};
   float clipDurationMs = 0.0F;
   public static final int SINGLE_STEP = 1;
   static final int[] table = new int[]{0, 1, 2, 8, 9, 10};
   private static final Map<KnifeInspectClip.Key, KnifeInspectClip> CLIP_CACHE = new ConcurrentHashMap<>();

   static float[] getAmounts(List<Float> list) {
      float[] floats = new float[list.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = (Float)list.get(i);
      }

      return floats;
   }

   private static KnifeInspectClip getBayonetStiletto(String bayonetStilettoText, float bayonetStilettoValue, float wrapDegreesBound, int intersectsCenterBound) {
      KnifeInspectClip knifeInspectClip = new KnifeInspectClip();
      switch (bayonetStilettoText) {
         case "M9 Bayonet":
            KnifeClipM9.handle(knifeInspectClip, bayonetStilettoValue, wrapDegreesBound, intersectsCenterBound);
            break;
         case "Stiletto":
            KnifeClipStiletto.handleAmount(knifeInspectClip, bayonetStilettoValue, wrapDegreesBound, intersectsCenterBound);
            break;
         case "Butterfly":
            KnifeClipButterfly.handleAmount(knifeInspectClip, bayonetStilettoValue, wrapDegreesBound, intersectsCenterBound);
            break;
         default:
            return null;
      }

      return knifeInspectClip;
   }

   public static float getAmount(String knifeInspectText, int knifeInspectValue) {
      if (knifeInspectValue <= 1) {
         return 1.0F;
      } else {
         KnifeInspectClip knifeInspectClip = getKnifeInspectClip(knifeInspectText, 0.0F, 0.0F, 1);
         KnifeInspectClip knifeInspectClip2 = getKnifeInspectClip(knifeInspectText, 0.0F, 0.0F, knifeInspectValue);
         return knifeInspectClip != null && knifeInspectClip2 != null && knifeInspectClip.clipDurationMs > 0.0F && knifeInspectClip2.clipDurationMs > 0.0F ? knifeInspectClip2.clipDurationMs / knifeInspectClip.clipDurationMs : 1.0F;
      }
   }

   KnifeInspectClip getTracks(int index, float... tracksValues) {
      this.tracks[index] = new KnifeInspectClip.Track(tracksValues);
      return this;
   }

   KnifeInspectClip.Track getTracks(int index) {
      return this.tracks[index];
   }

   public static KnifeInspectClip getKnifeInspectClip(String knifeInspectText, float knifeInspectBound, float rocketsElytraBound, int spins) {
      int maxSpins = Math.max(1, spins);
      float maxAntic = Math.max(0.0F, knifeInspectBound);
      float maxOver = Math.max(0.0F, rocketsElytraBound);
      KnifeInspectClip.Key key = new KnifeInspectClip.Key(knifeInspectText, Float.floatToIntBits(maxAntic), Float.floatToIntBits(maxOver), maxSpins);
      KnifeInspectClip knifeInspectClip = CLIP_CACHE.get(key);
      if (knifeInspectClip != null) {
         return knifeInspectClip;
      } else {
         KnifeInspectClip bayonetStiletto = getBayonetStiletto(knifeInspectText, maxAntic, maxOver, maxSpins);
         if (bayonetStiletto == null) {
            return null;
         } else {
            if (CLIP_CACHE.size() > 64) {
               CLIP_CACHE.clear();
            }

            KnifeInspectClip putIfAbsent = CLIP_CACHE.putIfAbsent(key, bayonetStiletto);
            return putIfAbsent != null ? putIfAbsent : bayonetStiletto;
         }
      }
   }

   public float getTracks(int index, float tracksValue) {
      KnifeInspectClip.Track track = this.tracks[index];
      return track == null ? 0.0F : track.getIncreaseTimes(tracksValue);
   }

   public static boolean canBayonetStiletto(String bayonetStilettoText) {
      return "M9 Bayonet".equals(bayonetStilettoText) || "Stiletto".equals(bayonetStilettoText) || "Butterfly".equals(bayonetStilettoText);
   }

   private KnifeInspectClip() {
   }

   private record Key(String style, int a, int o, int n) {
   }

   static final class Track {
      final float[] pairsNeedAmounts;
      final float[] increaseTimesAmounts;
      final float[] increaseTable;

      Track(float... knifeInspectValues) {
         if (knifeInspectValues.length >= 4 && (knifeInspectValues.length & 1) == 0) {
            int knifeInspectSize = knifeInspectValues.length / 2;
            this.increaseTimesAmounts = new float[knifeInspectSize];
            this.increaseTable = new float[knifeInspectSize];
            this.pairsNeedAmounts = new float[knifeInspectSize];

            for (int i = 0; i < knifeInspectSize; i++) {
               this.increaseTimesAmounts[i] = knifeInspectValues[2 * i];
               this.increaseTable[i] = knifeInspectValues[2 * i + 1];
               if (i > 0 && this.increaseTimesAmounts[i] <= this.increaseTimesAmounts[i - 1]) {
                  throw new IllegalArgumentException("key times must increase");
               }
            }

            float[] floats = new float[knifeInspectSize - 1];

            for (int j = 0; j < knifeInspectSize - 1; j++) {
               floats[j] = (this.increaseTable[j + 1] - this.increaseTable[j])
                  / (this.increaseTimesAmounts[j + 1] - this.increaseTimesAmounts[j]);
            }

            for (int k = 1; k < knifeInspectSize - 1; k++) {
               this.pairsNeedAmounts[k] = floats[k - 1] * floats[k] <= 0.0F ? 0.0F : (floats[k - 1] + floats[k]) * 0.5F;
            }

            for (int m = 0; m < knifeInspectSize - 1; m++) {
               if (floats[m] == 0.0F) {
                  this.pairsNeedAmounts[m] = 0.0F;
                  this.pairsNeedAmounts[m + 1] = 0.0F;
               } else {
                  float knifeInspectFactor = this.pairsNeedAmounts[m] / floats[m];
                  float knifeInspectFactor2 = this.pairsNeedAmounts[m + 1] / floats[m];
                  float knifeInspectLimit = knifeInspectFactor * knifeInspectFactor + knifeInspectFactor2 * knifeInspectFactor2;
                  if (knifeInspectLimit > 9.0F) {
                     float knifeInspectFactor3 = 3.0F / (float)Math.sqrt(knifeInspectLimit);
                     this.pairsNeedAmounts[m] = knifeInspectFactor3 * knifeInspectFactor * floats[m];
                     this.pairsNeedAmounts[m + 1] = knifeInspectFactor3 * knifeInspectFactor2 * floats[m];
                  }
               }
            }
         } else {
            throw new IllegalArgumentException("need (t,v) pairs");
         }
      }

      float getIncreaseTimes(float increaseLimit) {
         int increaseValue = this.increaseTimesAmounts.length;
         if (increaseLimit <= this.increaseTimesAmounts[0]) {
            return this.increaseTable[0];
         } else if (increaseLimit >= this.increaseTimesAmounts[increaseValue - 1]) {
            return this.increaseTable[increaseValue - 1];
         } else {
            int index = 0;

            while (increaseLimit > this.increaseTimesAmounts[index + 1]) {
               index++;
            }

            float increaseFactor = this.increaseTimesAmounts[index + 1] - this.increaseTimesAmounts[index];
            float increaseFactor2 = (increaseLimit - this.increaseTimesAmounts[index]) / increaseFactor;
            float increaseFactor3 = increaseFactor2 * increaseFactor2;
            float increaseFactor4 = increaseFactor3 * increaseFactor2;
            return (2.0F * increaseFactor4 - 3.0F * increaseFactor3 + 1.0F) * this.increaseTable[index]
               + (increaseFactor4 - 2.0F * increaseFactor3 + increaseFactor2) * increaseFactor * this.pairsNeedAmounts[index]
               + (-2.0F * increaseFactor4 + 3.0F * increaseFactor3) * this.increaseTable[index + 1]
               + (increaseFactor4 - increaseFactor3) * increaseFactor * this.pairsNeedAmounts[index + 1];
         }
      }

      float getIncreaseTimes() {
         return this.increaseTable[0];
      }

      float getIncreaseTable() {
         return this.increaseTable[this.increaseTable.length - 1];
      }
   }
}
