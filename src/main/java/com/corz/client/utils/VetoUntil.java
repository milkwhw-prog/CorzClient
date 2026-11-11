package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;

public final class VetoUntil {
   private int vetoPassCount;
   private final Map<Long, VetoUntil.Entry> vetoUntilEntryMap = new HashMap<>();
   private int vetoUntilCount;
   private int vetoCheckCount;

   public int getVetoPassCount() {
      return this.vetoUntilEntryMap.size();
   }

   public int getVetoUntilCount() {
      return this.vetoPassCount;
   }

   public static long getTime(long vetoUntilValue, long nextNodeKey, int vetoUntilValue3) {
      long fromScaled = vetoUntilValue * -7046029254386353131L;
      long vetoUntilValue4 = fromScaled ^ nextNodeKey + -4417276706812531889L + (fromScaled << 6) + (fromScaled >>> 2);
      return vetoUntilValue4 * 31L + vetoUntilValue3;
   }

   public void update() {
      this.vetoUntilEntryMap.clear();
   }

   public void resetVetoCounters() {
      this.vetoUntilEntryMap.clear();
      this.vetoPassCount = this.vetoCheckCount = this.vetoUntilCount = 0;
   }

   public boolean canVetoUntil(long vetoUntilValue, long nextNodeKey, int vetoUntilValue3, int vetoUntilValue4) {
      this.vetoUntilCount++;
      long botCrystalText = getTime(vetoUntilValue, nextNodeKey, vetoUntilValue3);
      VetoUntil.Entry entry = this.vetoUntilEntryMap.get(botCrystalText);
      if (entry != null && vetoUntilValue4 - entry.firstTick() <= 120) {
         int refusalsOffset = entry.refusals() + 1;
         if (refusalsOffset < 3) {
            this.vetoUntilEntryMap.put(botCrystalText, new VetoUntil.Entry(refusalsOffset, entry.firstTick(), entry.vetoUntil()));
            return false;
         } else {
            boolean vetoUntilFlag = vetoUntilValue4 < entry.vetoUntil();
            this.vetoUntilEntryMap.put(botCrystalText, new VetoUntil.Entry(refusalsOffset, entry.firstTick(), vetoUntilValue4 + 120));
            if (vetoUntilFlag) {
               return false;
            } else {
               this.vetoPassCount++;
               return true;
            }
         }
      } else {
         this.vetoUntilEntryMap.put(botCrystalText, new VetoUntil.Entry(1, vetoUntilValue4, 0));
         return false;
      }
   }

   public boolean isVetoActive(long vetoUntilValue, long nextNodeKey, int vetoUntilValue3, int vetoUntilValue4) {
      VetoUntil.Entry entry = this.vetoUntilEntryMap.get(getTime(vetoUntilValue, nextNodeKey, vetoUntilValue3));
      boolean vetoUntil = entry != null && vetoUntilValue4 < entry.vetoUntil();
      if (vetoUntil) {
         this.vetoCheckCount++;
      }

      return vetoUntil;
   }

   public int getVetoCheckCount() {
      return this.vetoUntilCount;
   }

   public int getTrackedEntryCount() {
      return this.vetoCheckCount;
   }

   private record Entry(int refusals, int firstTick, int vetoUntil) {
   }
}
