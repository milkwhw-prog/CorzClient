package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import com.corz.client.utils.Clue;
import com.corz.client.utils.Sense;

public final class Site {
   final int[] clueJoinTable;
   boolean clueJoinActive;
   boolean rememberedActive;
   double averageBlockY;
   static final int[] siteTable = new int[]{0, 0, 10, 10, 10};
   final Map<String, Site.Hit> clueHitMap = new HashMap<>();
   boolean siteActive;
   long lastCheckTimeMs;
   double clueJoinAmount;
   long joinValue;
   double joinAmount;
   boolean joinActive;
   int clueHitCount;
   List<String> copyOf;
   int clueJoinValue;
   int maxClueStrength;
   long siteValue;
   boolean hasOnlySecondaryHit;
   List<String> copyOfClueJoin;
   int confidenceScore;
   long earliestTimeMs;
   public final long joinTimeMs;

   boolean canClue(Clue clue) {
      for (Site.Hit hit : this.clueHitMap.values()) {
         if (hit.clue2ClueJoin == clue) {
            return true;
         }
      }

      return false;
   }

   public int getValue(Sense sense) {
      return this.clueJoinTable[sense.ordinal()];
   }

   public int getMax() {
      return this.maxClueStrength;
   }

   double getAmount(double siteValue, double blockZ) {
      double siteFactor = this.joinAmount - siteValue;
      double siteFactor2 = this.clueJoinAmount - blockZ;
      return siteFactor * siteFactor + siteFactor2 * siteFactor2;
   }

   public List<String> getCopyOf() {
      return this.copyOf;
   }

   public void updateFlag() {
      this.rememberedActive = true;
   }

   public int getValue() {
      return this.clueHitMap.size();
   }

   public boolean canTime(long time, long siteValue) {
      if (this.siteValue == 0L || time - this.lastCheckTimeMs > 2000L) {
         this.siteValue = time;
      }

      this.lastCheckTimeMs = time;
      return time - this.siteValue >= siteValue;
   }

   public double getClueJoinAmount() {
      return this.clueJoinAmount;
   }

   public boolean isRememberedActive() {
      return this.rememberedActive;
   }

   public double getJoinAmount() {
      return this.joinAmount;
   }

   public boolean canFlagMax(int siteValue) {
      if (!this.siteActive && !this.joinActive && this.maxClueStrength >= siteValue) {
         this.siteActive = true;
         return true;
      } else {
         return false;
      }
   }

   public int getClueJoinValue() {
      return this.clueJoinValue;
   }

   void handleClueJoin(long clueJoinValue) {
      this.clueHitMap.values().removeIf(nowx -> nowx.canClueTime(clueJoinValue));
      var enumMap = new EnumMap<>(Clue.class);
      var enumMap2 = new EnumMap<>(Clue.class);
      var hashMap = new HashMap<Clue, Integer>();
      Site.Hit hit = null;
      Site.Hit hit2 = null;
      double clueJoinTotal = 0.0;
      double clueJoinTotal2 = 0.0;
      double clueJoinTotal3 = 0.0;
      double clueJoinDivisor = 0.0;

      for (Site.Hit hit3 : this.clueHitMap.values()) {
         if (hit3.clue2ClueJoin == Clue.REMEMBERED) {
            if (hit2 == null || hit3.hitStrength > hit2.hitStrength) {
               hit2 = hit3;
            }
         } else {
            if (hit3.clue2ClueJoin.clueHostActive) {
               if (hit == null || hit3.hitStrength > hit.hitStrength) {
                  hit = hit3;
               }
            } else {
               enumMap.merge(hit3.clue2ClueJoin, hit3.hitStrength, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               if (hit3.joinClueJoin != null && hit3.hitStrength > hashMap.getOrDefault(hit3.clue2ClueJoin, -1)) {
                  hashMap.put(hit3.clue2ClueJoin, hit3.hitStrength);
                  enumMap2.put(hit3.clue2ClueJoin, hit3.joinClueJoin);
               }
            }

            double clueJoinFactor = Math.max(1, hit3.hitStrength);
            clueJoinTotal += hit3.blockX * clueJoinFactor;
            clueJoinTotal2 += hit3.blockY * clueJoinFactor;
            clueJoinTotal3 += hit3.blockZ * clueJoinFactor;
            clueJoinDivisor += clueJoinFactor;
         }
      }

      Site.Hit hit4 = hit != null ? hit : hit2;
      if (hit4 != null) {
         this.joinAmount = hit4.blockX;
         this.averageBlockY = hit4.blockY;
         this.clueJoinAmount = hit4.blockZ;
      } else if (clueJoinDivisor > 0.0) {
         this.joinAmount = clueJoinTotal / clueJoinDivisor;
         this.averageBlockY = clueJoinTotal2 / clueJoinDivisor;
         this.clueJoinAmount = clueJoinTotal3 / clueJoinDivisor;
      }

      int clueJoinBound = hit == null ? 0 : Math.min(100, hit.hitStrength);
      int value = hit2 == null ? 0 : hit2.hitStrength;
      Arrays.fill(this.clueJoinTable, 0);
      var arrayList = new ArrayList();

      for (Entry entry : enumMap.entrySet()) {
         Clue clue = (Clue)entry.getKey();
         int min2 = Math.min(clue.clueJoinValue, (Integer)entry.getValue());
         this.clueJoinTable[clue.sense.ordinal()] += min2;
         arrayList.add(new int[]{clue.ordinal(), min2});
      }

      int spotBound = 0;
      int clueJoinCounter = 0;

      for (int i = 0; i < this.clueJoinTable.length; i++) {
         this.clueJoinTable[i] = Math.min(siteTable[i], this.clueJoinTable[i]);
         spotBound += this.clueJoinTable[i];
         if (this.clueJoinTable[i] > 0) {
            clueJoinCounter++;
         }
      }

      this.clueJoinTable[Sense.STRUCTURE.ordinal()] = clueJoinBound;
      int max = Math.max(clueJoinBound, value);
      this.maxClueStrength = max;
      this.confidenceScore = max > 0 ? Math.min(100, max + Math.min(10, spotBound)) : 0;
      this.clueJoinValue = value;
      this.hasOnlySecondaryHit = hit == null && hit2 != null;
      this.clueJoinActive = hit != null && hit.clue2ClueJoin == Clue.BLOCKS;
      this.clueHitCount = clueJoinCounter + (clueJoinBound > 0 ? 1 : 0);
      arrayList.sort((obj3, obj4) -> Integer.compare(((int[]) obj4)[1], ((int[]) obj3)[1]));
      var arrayList2 = new ArrayList(4);
      var arrayList3 = new ArrayList(3);
      String clueJoinText = hit != null ? (hit.joinClueJoin != null && !hit.joinClueJoin.isBlank() ? hit.joinClueJoin : hit.clue2ClueJoin.clueJoinText) : null;
      if (clueJoinText != null) {
         arrayList2.add(clueJoinText);
         arrayList3.add(clueJoinText);
      } else if (hit2 != null && hit2.joinClueJoin != null && !hit2.joinClueJoin.isBlank()) {
         for (String totalPriceData : hit2.joinClueJoin.split(Pattern.quote("|"))) {
            if (!totalPriceData.isBlank() && arrayList2.size() < 2) {
               arrayList2.add(totalPriceData);
               arrayList3.add(totalPriceData);
            }
         }
      }

      if (arrayList2.isEmpty() && hit2 != null) {
         arrayList2.add(Clue.REMEMBERED.clueJoinText);
      }

      for (int[] clueJoinValues : (Iterable<int[]>) (arrayList)) {
         if (arrayList2.size() >= 3 || clueJoinValues[1] <= 0) {
            break;
         }

         Clue clue2 = Clue.values()[clueJoinValues[0]];
         if (hit == null || clue2.sense != hit.clue2ClueJoin.sense) {
            String clueJoinText3 = (String)enumMap2.get(clue2);
            arrayList2.add(clueJoinText3 != null && !clueJoinText3.isBlank() ? clueJoinText3 : clue2.clueJoinText);
         }
      }

      this.copyOfClueJoin = List.copyOf(arrayList2);
      this.copyOf = List.copyOf(arrayList3);
      this.joinActive = false;
   }

   public Iterable<Site.Hit> getValues() {
      return this.clueHitMap.values();
   }

   public double getAmount() {
      return this.averageBlockY;
   }

   public int getConfidenceScore() {
      return this.confidenceScore;
   }

   Site(long time, double amount, double amount2, double amount3, long min) {
      this.clueJoinTable = new int[Sense.values().length];
      this.copyOfClueJoin = List.of();
      this.copyOf = List.of();
      this.joinActive = true;
      this.joinTimeMs = time;
      this.joinAmount = amount;
      this.averageBlockY = amount2;
      this.clueJoinAmount = amount3;
      this.earliestTimeMs = min;
      this.joinValue = min;
   }

   public long getMin() {
      return this.earliestTimeMs;
   }

   public List<String> getCopyOfClueJoin() {
      return this.copyOfClueJoin;
   }

   public int getClueHitCount() {
      return this.clueHitCount;
   }

   public static final class Hit {
      long hitTimeMs;
      public final String clueText;
      public final int blockY;
      public final int blockZ;
      public final int blockX;
      int hitStrength;
      String joinClueJoin;
      public final Clue clue2ClueJoin;
      public final long clueValue;
      final long hitValue;

      Hit(String text, Clue clue2, int blockX, int blockY, int blockZ, long time, int max, String join, long time2) {
         this.clueText = text;
         this.clue2ClueJoin = clue2;
         this.blockX = blockX;
         this.blockY = blockY;
         this.blockZ = blockZ;
         this.clueValue = time;
         this.hitStrength = max;
         this.joinClueJoin = join;
         this.hitValue = time2;
         this.hitTimeMs = time2;
      }

      public String getJoin() {
         return this.joinClueJoin;
      }

      public int getMax() {
         return this.hitStrength;
      }

      boolean canClueTime(long clueValue) {
         return this.clue2ClueJoin.isTime() && clueValue - this.hitTimeMs > this.clue2ClueJoin.cooldownMs;
      }
   }
}
