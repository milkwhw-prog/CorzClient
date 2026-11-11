package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.corz.client.utils.KnifeInspectClip;

public final class KnifeSoundCues {
   public static final String DEPLOY = "deploy";
   public static final String LAND = "keys_land";
   public static final Map<String, KnifeSoundCues.Vanilla> MINECRAFT_BLOCK_MAP = Map.of(
      "deploy",
      new KnifeSoundCues.Vanilla("minecraft:item.flintandsteel.use", 1.5F, 0.8F),
      "deploy_butterfly",
      new KnifeSoundCues.Vanilla("minecraft:block.chain.place", 1.6F, 0.4F),
      "swish",
      new KnifeSoundCues.Vanilla("minecraft:item.brush.brushing.generic", 1.9F, 4.5F),
      "keys_land",
      new KnifeSoundCues.Vanilla("minecraft:item.flintandsteel.use", 1.8F, 0.75F),
      "tick",
      new KnifeSoundCues.Vanilla("minecraft:block.note_block.hat", 1.8F, 0.45F),
      "lock",
      new KnifeSoundCues.Vanilla("minecraft:block.note_block.hat", 1.3F, 0.65F),
      "keys",
      new KnifeSoundCues.Vanilla("minecraft:block.chain.place", 1.9F, 0.3F),
      "ring",
      new KnifeSoundCues.Vanilla("minecraft:block.amethyst_block.hit", 1.6F, 0.55F)
   );
   public static final String SWISH = "swish";
   public static final String TICK = "tick";
   private static final Map<KnifeSoundCues.CueKey, List<KnifeSoundCues.Cue>> BLOCK_MINECRAFT_MAP = new ConcurrentHashMap<>();
   public static final String DEPLOY_BUTTERFLY = "deploy_butterfly";
   public static final String KEYS_LABEL = "keys";
   static final int SINGLE_STEP = 1;
   public static final String RING = "ring";
   public static final String LOCK = "lock";

   static KnifeSoundCues.Cue getTick(float knifeSoundValue, double knifeSoundBound) {
      return new KnifeSoundCues.Cue(
         knifeSoundValue, "tick", (float)(0.3 + 0.12 * getAmount((knifeSoundBound - 200.0) / 1300.0, 0.0, 1.0)), (float)(1.0 + 0.05 * getAmount(knifeSoundBound / 2000.0, 0.0, 1.0))
      );
   }

   static void handle(List<KnifeSoundCues.Cue> list, float[] knifeSoundValues, float[] knifeSoundValues2, double knifeSoundDivisor) {
      int knifeSoundDivisor2 = knifeSoundValues.length - 1;
      int knifeSoundValue = Math.max(1, (int)Math.round(0.06 / knifeSoundDivisor));
      boolean knifeSoundFlag = false;

      for (int i = 1; i <= knifeSoundDivisor2; i++) {
         double knifeSoundLimit = getMin(knifeSoundValues[i] + knifeSoundValues2[i]);
         if (knifeSoundLimit > 30.0) {
            knifeSoundFlag = true;
         }

         if (knifeSoundFlag && knifeSoundLimit < 4.0) {
            knifeSoundFlag = false;
            double knifeSoundBound = 0.0;

            for (int j = Math.max(1, i - knifeSoundValue); j <= i; j++) {
               knifeSoundBound = Math.max(knifeSoundBound, Math.abs(knifeSoundValues[j] + knifeSoundValues2[j] - (knifeSoundValues[j - 1] + knifeSoundValues2[j - 1])) / knifeSoundDivisor);
            }

            list.add(knifeSoundBound > 200.0 ? getLand((float)i / knifeSoundDivisor2, knifeSoundBound) : getTick((float)i / knifeSoundDivisor2, knifeSoundBound));
         }
      }

      handle(list, knifeSoundValues, knifeSoundDivisor);
      handle(list, knifeSoundValues2, knifeSoundDivisor);
   }

   static List<KnifeSoundCues.Cue> getTick(float[] knifeSoundValues, double knifeSoundDivisor) {
      ArrayList arrayList = new ArrayList();
      int knifeSoundLimit = knifeSoundValues.length - 1;
      int max2 = Math.max(1, (int)Math.round(0.09 / knifeSoundDivisor));
      int knifeSoundLimit3 = -1;
      double knifeSoundBound = 0.0;

      for (int i = 1; i <= knifeSoundLimit; i++) {
         double apiPutBound = getAmount(knifeSoundValues, i, knifeSoundDivisor);
         if (apiPutBound > 500.0) {
            knifeSoundLimit3 = i;
            knifeSoundBound = Math.max(knifeSoundBound, apiPutBound);
         } else if (apiPutBound < 80.0 && knifeSoundLimit3 >= 0) {
            if (i - knifeSoundLimit3 <= max2) {
               arrayList.add(new KnifeSoundCues.Cue((float)i / knifeSoundLimit, "tick", (float)(0.28 + 0.06 * getAmount(knifeSoundBound / 2000.0, 0.0, 1.0)), 0.92F));
            }

            knifeSoundLimit3 = -1;
            knifeSoundBound = 0.0;
         }
      }

      return arrayList;
   }

   public static List<KnifeSoundCues.Cue> getMinecraftBlock(String minecraftBlockText, KnifeInspectClip knifeInspectClip, double minecraftBlockLimit) {
      if (knifeInspectClip != null && minecraftBlockLimit > 0.0) {
         KnifeSoundCues.CueKey cueKey = new KnifeSoundCues.CueKey(minecraftBlockText, knifeInspectClip, Double.doubleToLongBits(minecraftBlockLimit));
         List block_minecraft_map = BLOCK_MINECRAFT_MAP.get(cueKey);
         if (block_minecraft_map != null) {
            return block_minecraft_map;
         } else {
            List copyOf = List.copyOf(getStilettoButterfly(minecraftBlockText, knifeInspectClip, minecraftBlockLimit));
            if (BLOCK_MINECRAFT_MAP.size() > 64) {
               BLOCK_MINECRAFT_MAP.clear();
            }

            List putIfAbsent = BLOCK_MINECRAFT_MAP.putIfAbsent(cueKey, copyOf);
            return putIfAbsent != null ? putIfAbsent : copyOf;
         }
      } else {
         return new ArrayList<>();
      }
   }

   public static float getAmount(double knifeSoundValue) {
      double knifeSoundValue2 = getAmount(knifeSoundValue, 0.0, 1.0);
      return knifeSoundValue2 <= 0.8 ? (float)(0.3 * knifeSoundValue2 / 0.8) : (float)(0.3 + 0.7 * (knifeSoundValue2 - 0.8) / 0.19999999999999996);
   }

   static double getAmount(double knifeSoundLimit, double knifeSoundLimit2, double knifeSoundValue) {
      return knifeSoundLimit < knifeSoundLimit2 ? knifeSoundLimit2 : (knifeSoundLimit > knifeSoundValue ? knifeSoundValue : knifeSoundLimit);
   }

   static List<KnifeSoundCues.Cue> getLandVolume(List<KnifeSoundCues.Cue> list, double landVolumeFactor) {
      ArrayList arrayList = new ArrayList(list.size());
      double landVolumeValue = Double.NEGATIVE_INFINITY;
      int landVolumeValue2 = 0;

      for (KnifeSoundCues.Cue cue : list) {
         if (!"keys_land".equals(cue.sound())) {
            arrayList.add(cue);
         } else {
            double landVolumeValue3 = cue.p() * landVolumeFactor;
            landVolumeValue2 = landVolumeValue3 - landVolumeValue < 0.3 ? landVolumeValue2 + 1 : 0;
            landVolumeValue = landVolumeValue3;
            double max4 = Math.max(Math.min(0.45, (double)cue.volume()), cue.volume() * Math.pow(0.85, landVolumeValue2 / 2));
            arrayList.add(new KnifeSoundCues.Cue(cue.p(), cue.sound(), (float)max4, cue.pitch()));
         }
      }

      return arrayList;
   }

   static void handle(List<KnifeSoundCues.Cue> list, float[] knifeSoundValues, double knifeSoundDivisor) {
      int knifeSoundLimit = knifeSoundValues.length - 1;
      int knifeSoundValue = Math.max(1, (int)Math.round(0.04 / knifeSoundDivisor));

      for (double knifeSoundValue2 : new double[]{0.0, 180.0}) {
         boolean knifeSoundFlag = false;

         for (int i = 1; i <= knifeSoundLimit; i++) {
            double abs2 = Math.abs(knifeSoundValues[i] - knifeSoundValue2);
            if (abs2 > 25.0) {
               knifeSoundFlag = true;
            }

            if (knifeSoundFlag && abs2 < 3.0) {
               knifeSoundFlag = false;
               double knifeSoundBound = 0.0;

               for (int j = Math.max(1, i - knifeSoundValue); j <= i; j++) {
                  knifeSoundBound = Math.max(knifeSoundBound, getAmount(knifeSoundValues, j, knifeSoundDivisor));
               }

               if (knifeSoundBound >= 250.0) {
                  list.add(getTick((float)i / knifeSoundLimit, knifeSoundBound));
               }
            }
         }
      }
   }

   static void handleTickLock(List<KnifeSoundCues.Cue> list, float[] lockValues, double lockDivisor) {
      boolean lockFlag = false;
      boolean lockFlag2 = false;
      boolean lockFlag3 = lockValues[0] >= 170.0F;
      boolean lockFlag4 = lockValues[0] < 1.0F;
      int lockLimit = lockValues.length - 1;
      int lockFactor = Math.max(1, (int)Math.round(0.03 / lockDivisor));

      for (int i = 1; i <= lockLimit; i++) {
         float lockLimit2 = lockValues[i];
         float lockValue = (float)i / lockLimit;
         if (lockFlag4 && lockLimit2 > 3.0F) {
            list.add(new KnifeSoundCues.Cue(lockValue, "tick", 0.36F, 1.04F));
            lockFlag4 = false;
         }

         if (lockLimit2 < 1.0F) {
            lockFlag4 = true;
         }

         if (lockLimit2 > 25.0F) {
            lockFlag = true;
         }

         if (lockLimit2 < 140.0F) {
            lockFlag2 = true;
         }

         if (lockFlag && lockLimit2 < 3.0F) {
            double lockValue2 = Math.abs(lockValues[i] - lockValues[Math.max(0, i - lockFactor)]) / (lockFactor * lockDivisor);
            list.add(new KnifeSoundCues.Cue(lockValue, "lock", (float)(0.45 + 0.07 * getAmount(lockValue2 / 2500.0, 0.0, 1.0)), 1.0F));
            lockFlag = false;
         }

         if (lockFlag2 && lockLimit2 > 168.0F) {
            list.add(new KnifeSoundCues.Cue(lockValue, "tick", 0.4F, 0.94F));
            lockFlag2 = false;
            lockFlag3 = true;
         }

         if (lockFlag3 && lockLimit2 < 166.0F) {
            list.add(new KnifeSoundCues.Cue(lockValue, "tick", 0.36F, 1.04F));
            lockFlag3 = false;
         }
      }
   }

   static double getAmount(float[] knifeSoundValues, int index, double knifeSoundDivisor) {
      return Math.abs(knifeSoundValues[index] - knifeSoundValues[index - 1]) / knifeSoundDivisor;
   }

   static List<KnifeSoundCues.Cue> getItems(List<KnifeSoundCues.Cue> list, List<KnifeSoundCues.Cue> list2, double knifeSoundFactor) {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList(list);
      arrayList2.sort(Comparator.<KnifeSoundCues.Cue>comparingDouble(cue -> -cue.volume()).thenComparingDouble(KnifeSoundCues.Cue::p));

      for (KnifeSoundCues.Cue totalPriceData : (Iterable<com.corz.client.utils.KnifeSoundCues.Cue>) (arrayList2)) {
         if (arrayList.size() >= 1) {
            break;
         }

         boolean knifeSoundFlag = true;

         for (KnifeSoundCues.Cue cue3 : list2) {
            if (Math.abs(totalPriceData.p() - cue3.p()) * knifeSoundFactor < 0.18) {
               knifeSoundFlag = false;
            }
         }

         if (knifeSoundFlag) {
            arrayList.add(totalPriceData);
         }
      }

      return arrayList;
   }

   private KnifeSoundCues() {
   }

   static List<KnifeSoundCues.Cue> getItems(List<KnifeSoundCues.Cue> list, double knifeSoundDivisor, int knifeSoundValue) {
      ArrayList arrayList = new ArrayList(list);
      arrayList.sort(Comparator.<KnifeSoundCues.Cue>comparingDouble(cue -> -cue.volume()).thenComparingDouble(KnifeSoundCues.Cue::p));
      ArrayList arrayList2 = new ArrayList();
      double knifeSoundValue2 = 1.0 - 0.06 / knifeSoundDivisor;

      for (KnifeSoundCues.Cue totalPriceData : (Iterable<com.corz.client.utils.KnifeSoundCues.Cue>) (arrayList)) {
         if (arrayList2.size() >= knifeSoundValue) {
            break;
         }

         if (!(totalPriceData.p() < 0.0F) && !(totalPriceData.p() > knifeSoundValue2)) {
            boolean knifeSoundFlag = true;

            for (KnifeSoundCues.Cue cue3 : (Iterable<com.corz.client.utils.KnifeSoundCues.Cue>) (arrayList2)) {
               if (Math.abs(totalPriceData.p() - cue3.p()) * knifeSoundDivisor < 0.12) {
                  knifeSoundFlag = false;
               }
            }

            if (knifeSoundFlag) {
               arrayList2.add(totalPriceData);
            }
         }
      }

      arrayList2.sort(Comparator.comparingDouble(KnifeSoundCues.Cue::p));
      return arrayList2;
   }

   private static List<KnifeSoundCues.Cue> getStilettoButterfly(String stilettoButterflyText, KnifeInspectClip knifeInspectClip, double stilettoButterflyLimit) {
      ArrayList arrayList = new ArrayList();
      if (knifeInspectClip != null && stilettoButterflyLimit > 0.0) {
         float[][] floats = new float[17][4001];

         for (int i = 0; i < 17; i++) {
            for (int j = 0; j <= 4000; j++) {
               floats[i][j] = knifeInspectClip.getTracks(i, j / 4000.0F);
            }
         }

         double styleScaled = stilettoButterflyLimit / 4000.0;
         if ("Butterfly".equals(stilettoButterflyText)) {
            int stilettoButterflyValue = Math.max(1, Math.min(10, (int)Math.floor(2.7 * stilettoButterflyLimit)));
            handle(arrayList, floats[15], floats[16], styleScaled);
            arrayList.sort(Comparator.comparingDouble(KnifeSoundCues.Cue::p));
            List list = getItems(getLandVolume(arrayList, stilettoButterflyLimit), stilettoButterflyLimit, stilettoButterflyValue);
            List items2 = getItems(floats[12], styleScaled);
            items2.addAll(getItems(floats[11], styleScaled));
            ArrayList arrayList2 = new ArrayList(list);
            arrayList2.addAll(getItems(items2, list, stilettoButterflyLimit));
            return getItems(arrayList2, stilettoButterflyLimit, stilettoButterflyValue);
         } else {
            List tick = getTick(floats[11], styleScaled);
            tick.addAll(getTick(floats[12], styleScaled));
            tick.sort(Comparator.<KnifeSoundCues.Cue>comparingDouble(cue -> -cue.volume()).thenComparingDouble(KnifeSoundCues.Cue::p));
            arrayList.addAll(tick.subList(0, Math.min(2, tick.size())));
            if ("Stiletto".equals(stilettoButterflyText)) {
               handleTickLock(arrayList, floats[14], styleScaled);
               return getItems(arrayList, stilettoButterflyLimit, 6);
            } else {
               return getItems(arrayList, stilettoButterflyLimit, 2);
            }
         }
      } else {
         return arrayList;
      }
   }

   static List<KnifeSoundCues.Cue> getItems(float[] knifeSoundValues, double knifeSoundFactor) {
      ArrayList arrayList = new ArrayList();
      int knifeSoundLimit = knifeSoundValues.length - 1;
      int knifeSoundCounter = 1;

      while (knifeSoundCounter <= knifeSoundLimit) {
         if (getAmount(knifeSoundValues, knifeSoundCounter, knifeSoundFactor) < 600.0) {
            knifeSoundCounter++;
         } else {
            int knifeSoundValue = knifeSoundCounter;
            double knifeSoundBound = 0.0;

            while (knifeSoundCounter <= knifeSoundLimit && getAmount(knifeSoundValues, knifeSoundCounter, knifeSoundFactor) >= 600.0) {
               knifeSoundBound = Math.max(knifeSoundBound, getAmount(knifeSoundValues, knifeSoundCounter++, knifeSoundFactor));
            }

            if ((knifeSoundCounter - knifeSoundValue) * knifeSoundFactor >= 0.12 && Math.abs(knifeSoundValues[knifeSoundCounter - 1] - knifeSoundValues[knifeSoundValue - 1]) >= 150.0) {
               arrayList.add(new KnifeSoundCues.Cue((knifeSoundValue + knifeSoundCounter - 1) * 0.5F / knifeSoundLimit, "keys", (float)(0.32 + 0.04 * getAmount(knifeSoundBound / 2000.0, 0.0, 1.0)), 1.0F));
            }
         }
      }

      return arrayList;
   }

   public static double getDeployButterfly(String deployButterflyText) {
      return !"keys".equals(deployButterflyText) && !"keys_land".equals(deployButterflyText) && !"deploy_butterfly".equals(deployButterflyText) ? 0.05 : 0.09;
   }

   static double getMin(double knifeSoundValue) {
      double knifeSoundTotal = knifeSoundValue % 360.0;
      if (knifeSoundTotal < 0.0) {
         knifeSoundTotal += 360.0;
      }

      return Math.min(knifeSoundTotal, 360.0 - knifeSoundTotal);
   }

   static KnifeSoundCues.Cue getLand(float landValue, double knifeSoundBound) {
      return new KnifeSoundCues.Cue(
         landValue, "keys_land", (float)(0.45 + 0.2 * getAmount((knifeSoundBound - 600.0) / 2400.0, 0.0, 1.0)), (float)(0.97 + 0.08 * getAmount(knifeSoundBound / 3000.0, 0.0, 1.0))
      );
   }

   public static double getAmount(int knifeSoundValue, double knifeSoundValue2) {
      return knifeSoundValue == 4 ? knifeSoundValue2 : 0.8;
   }

   public record Cue(float p, String sound, float volume, float pitch) {
   }

   private record CueKey(String style, KnifeInspectClip clip, long secs) {
   }

   public record Vanilla(String id, float pitch, float volume) {
   }
}
