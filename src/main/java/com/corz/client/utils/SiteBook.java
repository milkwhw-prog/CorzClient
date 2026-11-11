package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.corz.client.utils.Site;
import com.corz.client.utils.Clue;
import com.corz.client.utils.Sense;

public final class SiteBook {
   private final Map<Long, Site> joinSiteMap = new HashMap<>();
   private final Set<String> clueStringSet;
   private final Map<String, Site> clueSiteMap = new HashMap<>();
   private final List<int[]> siteBookIntArrayList;
   private long nextSiteId;
   private final Map<Long, Set<String>> clueSetMap = new HashMap<>();

   public void handleTimeFlag(String siteBookText) {
      Site site = this.clueSiteMap.remove(siteBookText);
      if (site != null) {
         Site.Hit hit = site.clueHitMap.remove(siteBookText);
         if (hit != null) {
            this.handleTimeText(hit);
         }

         site.joinActive = true;
         if (site.clueHitMap.isEmpty()) {
            this.joinSiteMap.remove(site.joinTimeMs);
         }
      }
   }

   private void handle(double siteBookValue, double blockZ) {
      var arrayList = new ArrayList<>(this.joinSiteMap.values());
      arrayList.sort((pxx, site) -> {
         int var6x = Integer.compare(pxx.confidenceScore, site.confidenceScore);
         return var6x != 0 ? var6x : Double.compare(site.getAmount(siteBookValue, blockZ), pxx.getAmount(siteBookValue, blockZ));
      });

      for (int i = 0; i < arrayList.size() - 320; i++) {
         this.handleTextTime((Site)arrayList.get(i));
      }
   }

   public boolean canFlag(Clue clue, int siteBookValue, int blockY, int blockZ, String siteBookText, long farmLeashedValue) {
      return this.canTimeJoin(clue, siteBookValue, blockY, blockZ, siteBookText, clue.clueWeight, null, farmLeashedValue);
   }

   private static boolean canFlag(Site site, long siteBookValue) {
      for (Site.Hit hit : site.clueHitMap.values()) {
         if (hit.canClueTime(siteBookValue)) {
            return true;
         }
      }

      return false;
   }

   private void handleTimeText(Site.Hit hit) {
      var set = this.clueSetMap.get(hit.clueValue);
      if (set != null && set.remove(hit.clueText) && set.isEmpty()) {
         this.clueSetMap.remove(hit.clueValue);
      }
   }

   private void handleMinAmount(long siteBookValue) {
      if (this.joinSiteMap.size() >= 2) {
         var arrayList = new ArrayList<>(this.joinSiteMap.values());
         double siteBookValue2 = 576.0;

         for (int i = 0; i < arrayList.size(); i++) {
            Site site = (Site)arrayList.get(i);
            if (this.joinSiteMap.containsKey(site.joinTimeMs)) {
               for (int j = i + 1; j < arrayList.size(); j++) {
                  Site site2 = (Site)arrayList.get(j);
                  if (this.joinSiteMap.containsKey(site2.joinTimeMs)) {
                     double deltaAmount = site.joinAmount - site2.joinAmount;
                     double deltaAmount2 = site.clueJoinAmount - site2.clueJoinAmount;
                     if (!(deltaAmount * deltaAmount + deltaAmount2 * deltaAmount2 > siteBookValue2)) {
                        Site botCrystalValue = site.earliestTimeMs <= site2.earliestTimeMs ? site : site2;
                        Site site4 = botCrystalValue == site ? site2 : site;

                        for (Site.Hit hit : site4.clueHitMap.values()) {
                           botCrystalValue.clueHitMap.put(hit.clueText, hit);
                           this.clueSiteMap.put(hit.clueText, botCrystalValue);
                        }

                        site4.clueHitMap.clear();
                        botCrystalValue.rememberedActive = botCrystalValue.rememberedActive | site4.rememberedActive;
                        botCrystalValue.earliestTimeMs = Math.min(botCrystalValue.earliestTimeMs, site4.earliestTimeMs);
                        this.joinSiteMap.remove(site4.joinTimeMs);
                        botCrystalValue.handleClueJoin(siteBookValue);
                        if (site4 == site) {
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public SiteBook() {
      this.clueStringSet = new HashSet<>();
      this.nextSiteId = 1L;
      this.siteBookIntArrayList = new ArrayList<>();
   }

   public void handle(int siteBookValue, int blockZ) {
      if (this.siteBookIntArrayList.size() < 320 && !this.canFlag(siteBookValue, blockZ)) {
         this.siteBookIntArrayList.add(new int[]{siteBookValue, blockZ});
      }
   }

   public void handleMinRemembered(int rememberedValue, int blockY, int blockZ, int rememberedBound, List<String> list, long unreachableBound, long currentMillis) {
      String rememberedText = "mem:" + rememberedValue + "," + blockY + "," + blockZ;
      this.canTimeJoin(Clue.REMEMBERED, rememberedValue, blockY, blockZ, rememberedText, Math.max(0, Math.min(100, rememberedBound)), String.join("|", list), currentMillis);
      Site site = this.clueSiteMap.get(rememberedText);
      if (site != null) {
         site.rememberedActive = true;
         site.siteActive = true;
         site.earliestTimeMs = Math.min(site.earliestTimeMs, unreachableBound);
      }

      this.handle(rememberedValue, blockZ);
   }

   public boolean canTimeJoin(Clue clue, int joinValue, int blockY, int blockZ, String joinText, int max, String join, long time) {
      Site site = this.clueSiteMap.get(joinText);
      if (site != null) {
         Site.Hit hit = site.clueHitMap.get(joinText);
         if (hit != null && (hit.blockX != joinValue || hit.blockY != blockY || hit.blockZ != blockZ)) {
            double joinFactor = site.joinAmount - joinValue;
            double joinFactor2 = site.clueJoinAmount - blockZ;
            if (!(joinFactor * joinFactor + joinFactor2 * joinFactor2 > 576.0)) {
               long time4 = getTime(joinValue >> 4, blockZ >> 4);
               site.clueHitMap.put(joinText, new Site.Hit(joinText, clue, joinValue, blockY, blockZ, time4, max, join, time));
               site.joinValue = time;
               site.joinActive = true;
               return false;
            }

            this.handleTimeFlag(joinText);
            hit = null;
            site = null;
         }

         if (hit != null) {
            hit.hitTimeMs = time;
            if (hit.hitStrength != max || !Objects.equals(hit.joinClueJoin, join)) {
               hit.hitStrength = max;
               hit.joinClueJoin = join;
               site.joinActive = true;
            }

            site.joinValue = time;
            return false;
         }

         if (site != null) {
            this.clueSiteMap.remove(joinText);
         }
      }

      Site botCrystalValue = this.getSite(joinValue, blockZ, 24);
      if (botCrystalValue == null) {
         botCrystalValue = new Site(this.nextSiteId++, joinValue, blockY, blockZ, time);
         this.joinSiteMap.put(botCrystalValue.joinTimeMs, botCrystalValue);
      }

      long time5 = getTime(joinValue >> 4, blockZ >> 4);
      botCrystalValue.clueHitMap.put(joinText, new Site.Hit(joinText, clue, joinValue, blockY, blockZ, time5, max, join, time));
      botCrystalValue.joinValue = time;
      botCrystalValue.joinActive = true;
      this.clueSiteMap.put(joinText, botCrystalValue);
      return true;
   }

   public void handleClueText(List<SiteBook.Pending> list, Set<Long> set, long clueValue) {
      var hashSet = new HashSet();

      for (SiteBook.Pending pending : list) {
         hashSet.add(pending.pendingText);
      }

      var iterator = this.clueStringSet.iterator();

      while (iterator.hasNext()) {
         String clueText = (String)iterator.next();
         if (!hashSet.contains(clueText)) {
            Site site = this.clueSiteMap.get(clueText);
            Site.Hit hit = site == null ? null : site.clueHitMap.get(clueText);
            if (hit == null || set.contains(hit.clueValue)) {
               this.handleTimeFlag(clueText);
               iterator.remove();
            }
         }
      }

      for (SiteBook.Pending pending2 : list) {
         this.canTimeJoin(pending2.clue2Clue, pending2.pendingStrength, pending2.clueValue, pending2.pendingValue, pending2.pendingText, pending2.pendingClueStrength, pending2.clueText, clueValue);
         this.clueStringSet.add(pending2.pendingText);
         Site site2 = this.clueSiteMap.get(pending2.pendingText);
         if (site2 != null) {
            for (String siteBookText : new ArrayList<>(site2.clueHitMap.keySet())) {
               if (siteBookText.startsWith("mem:")) {
                  this.handleTimeFlag(siteBookText);
               }
            }
         }
      }

      for (Site site3 : new ArrayList<>(this.joinSiteMap.values())) {
         boolean clueFlag = false;
         var arrayList = new ArrayList();

         for (Site.Hit hit2 : site3.clueHitMap.values()) {
            if (hit2.clue2ClueJoin == Clue.REMEMBERED) {
               if (set.contains(hit2.clueValue)) {
                  arrayList.add(hit2.clueText);
               }
            } else if (hit2.clue2ClueJoin.sense == Sense.STRUCTURE || hit2.clue2ClueJoin.sense == Sense.LIGHT) {
               clueFlag = true;
            }
         }

         if (!clueFlag) {
            for (String clueText3 : (Iterable<java.lang.String>) (arrayList)) {
               this.handleTimeFlag(clueText3);
            }
         }
      }
   }

   public List<Site> getItems(int siteBookValue, int blockSessionValue) {
      var arrayList = new ArrayList<Site>();

      for (Site site : this.joinSiteMap.values()) {
         if (site.confidenceScore >= siteBookValue) {
            arrayList.add(site);
         }
      }

      arrayList.sort((site2, site3) -> Integer.compare(site3.confidenceScore, site2.confidenceScore));
      return arrayList.size() > blockSessionValue ? new ArrayList<>(arrayList.subList(0, blockSessionValue)) : arrayList;
   }

   public List<Site> getItems(double siteBookValue, double blockZ, int siteBookValue3, int siteBookLimit, double siteBookFactor, int siteBookBound) {
      return this.getFlagAmount(siteBookValue, blockZ, siteBookValue3, siteBookLimit, siteBookFactor, siteBookBound, Integer.MAX_VALUE);
   }

   public List<Site> getFlag(double siteBookValue, double blockZ, int sureBaseThreshold, int blockSessionValue) {
      var arrayList = new ArrayList();

      for (Site site : this.joinSiteMap.values()) {
         if (!site.joinActive && site.confidenceScore >= sureBaseThreshold) {
            arrayList.add(site);
         }
      }

      arrayList.sort((obj, pxx) -> {
         int compare = Integer.compare(((com.corz.client.utils.Site) pxx).confidenceScore, ((com.corz.client.utils.Site) obj).confidenceScore);
         return compare != 0 ? compare : Double.compare(((com.corz.client.utils.Site) obj).getAmount(siteBookValue, blockZ), ((com.corz.client.utils.Site) pxx).getAmount(siteBookValue, blockZ));
      });
      return arrayList.size() > blockSessionValue ? new ArrayList<>(arrayList.subList(0, blockSessionValue)) : arrayList;
   }

   public Site getSite(double siteValue, double blockZ, int sureBaseThreshold) {
      var list = this.getFlag(siteValue, blockZ, sureBaseThreshold, 1);
      return list.isEmpty() ? null : (Site)list.get(0);
   }

   private void handleTextTime(Site site) {
      this.joinSiteMap.remove(site.joinTimeMs);

      for (Site.Hit hit : site.clueHitMap.values()) {
         this.clueSiteMap.remove(hit.clueText);
         this.clueStringSet.remove(hit.clueText);
         this.handleTimeText(hit);
      }
   }

   public void handleTextClue(int clueValue, int j, List<SiteBook.Pending> list, long clueValue3) {
      long immutablePosValue = getTime(clueValue, j);
      var set = this.clueSetMap.remove(immutablePosValue);
      var hashSet = new HashSet();

      for (SiteBook.Pending pending : list) {
         hashSet.add(pending.pendingText);
      }

      if (set != null) {
         for (String clueText : set) {
            if (!hashSet.contains(clueText)) {
               this.handleTimeFlag(clueText);
            }
         }
      }

      if (!list.isEmpty()) {
         var botCrystalValue = new HashSet();

         for (SiteBook.Pending pending2 : list) {
            this.canTimeJoin(pending2.clue2Clue, pending2.pendingStrength, pending2.clueValue, pending2.pendingValue, pending2.pendingText, pending2.pendingClueStrength, pending2.clueText, clueValue3);
            botCrystalValue.add(pending2.pendingText);
         }

         this.clueSetMap.put(immutablePosValue, botCrystalValue);
      }
   }

   private void handle(Site site, long siteBookValue) {
      this.clueSiteMap.values().removeIf(site2 -> site2 == site);

      for (String siteBookText : site.clueHitMap.keySet()) {
         this.clueSiteMap.put(siteBookText, site);
      }
   }

   public void update() {
      this.siteBookIntArrayList.clear();
      this.joinSiteMap.clear();
      this.clueSiteMap.clear();
      this.clueSetMap.clear();
      this.clueStringSet.clear();
   }

   public boolean canFlag(double siteBookValue, double blockZ) {
      double siteBookLimit = 576.0;

      for (int[] siteBookValues : this.siteBookIntArrayList) {
         double siteBookFactor = siteBookValues[0] - siteBookValue;
         double siteBookFactor2 = siteBookValues[1] - blockZ;
         if (siteBookFactor * siteBookFactor + siteBookFactor2 * siteBookFactor2 <= siteBookLimit) {
            return true;
         }
      }

      return false;
   }

   public static long getTime(int siteBookValue, int j) {
      return (long)j << 32 | siteBookValue & 4294967295L;
   }

   public void handleTimeFlag(long siteBookValue, double siteBookValue2, double blockZ) {
      var arrayList = new ArrayList();

      for (Site site : this.joinSiteMap.values()) {
         int mapSize = site.clueHitMap.size();
         if (site.joinActive || canFlag(site, siteBookValue)) {
            site.handleClueJoin(siteBookValue);
         }

         if (site.clueHitMap.size() != mapSize) {
            this.handle(site, siteBookValue);
         }

         if (site.clueHitMap.isEmpty()) {
            arrayList.add(site);
         }
      }

      for (Site site2 : (Iterable<com.corz.client.utils.Site>) (arrayList)) {
         this.joinSiteMap.remove(site2.joinTimeMs);
      }

      this.handleMinAmount(siteBookValue);
      if (this.joinSiteMap.size() > 320) {
         this.handle(siteBookValue2, blockZ);
      }

      double siteBookValue4 = 1.6777216E7;
      var arrayList2 = new ArrayList();

      for (Site totalPriceData : this.joinSiteMap.values()) {
         if (totalPriceData.getAmount(siteBookValue2, blockZ) > siteBookValue4 && totalPriceData.confidenceScore < 40) {
            arrayList2.add(totalPriceData);
         }
      }

      for (Site site4 : (Iterable<com.corz.client.utils.Site>) (arrayList2)) {
         this.handleTextTime(site4);
      }
   }

   private Site getSite(int siteValue, int blockZ, int siteFactor) {
      Site site = null;
      double siteLimit = (double)siteFactor * siteFactor;

      for (Site site2 : this.joinSiteMap.values()) {
         double siteValue3 = site2.getAmount(siteValue, blockZ);
         if (siteValue3 <= siteLimit) {
            siteLimit = siteValue3;
            site = site2;
         }
      }

      return site;
   }

   public Collection<Site> getValues() {
      return this.joinSiteMap.values();
   }

   public List<Site> getFlagAmount(double siteBookValue, double blockZ, int siteBookValue3, int siteBookLimit, double siteBookFactor, int siteBookBound, int siteBookValue4) {
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      double siteBookLimit2 = siteBookFactor * siteBookFactor;

      for (Site site : this.joinSiteMap.values()) {
         if (!site.joinActive && site.confidenceScore >= siteBookValue3 && (site.clueJoinActive || !(site.averageBlockY > siteBookValue4))) {
            (site.getAmount(siteBookValue, blockZ) <= siteBookLimit2 ? arrayList : arrayList2).add(site);
         }
      }

      Comparator comparator = (pzx, obj) -> {
         int compare = Integer.compare(((com.corz.client.utils.Site) obj).confidenceScore, ((com.corz.client.utils.Site) pzx).confidenceScore);
         return compare != 0 ? compare : Double.compare(((com.corz.client.utils.Site) pzx).getAmount(siteBookValue, blockZ), ((com.corz.client.utils.Site) obj).getAmount(siteBookValue, blockZ));
      };
      arrayList.sort(comparator);
      arrayList2.sort(comparator);
      int blockSessionValue = Math.min(arrayList.size(), Math.max(0, siteBookLimit - Math.min(siteBookBound, arrayList2.size())));
      var arrayList3 = new ArrayList(arrayList.subList(0, blockSessionValue));

      for (int i = 0; i < arrayList2.size() && arrayList3.size() < siteBookLimit; i++) {
         arrayList3.add((Site)arrayList2.get(i));
      }

      return arrayList3;
   }

   public int getValue() {
      return this.joinSiteMap.size();
   }

   public static final class Pending {
      public final int pendingStrength;
      public final int clueValue;
      public final int pendingValue;
      public final String clueText;
      public final int pendingClueStrength;
      public final Clue clue2Clue;
      public final String pendingText;

      public Pending(Clue clue, int pendingValue, int pendingValue2, int pendingValue3, String pendingText) {
         this(clue, pendingValue, pendingValue2, pendingValue3, pendingText, clue.clueWeight, null);
      }

      public Pending(Clue clue, int value, int value2, int value3, String text, int value4, String text2) {
         this.clue2Clue = clue;
         this.pendingStrength = value;
         this.clueValue = value2;
         this.pendingValue = value3;
         this.pendingText = text;
         this.pendingClueStrength = value4;
         this.clueText = text2;
      }
   }
}
