package com.corz.client.config;

public final class FightStatTotals {
   public int ownPopTotal;
   public int opponentPopTotal;
   public int eatabActionTotal;
   public int killCount;
   public int juggleStrikeTotal;
   public int deathCount;

   private static int addClampedCounts(int pearlJsonValue, int pearlJsonBound) {
      return (int)Math.min(1000000L, (long)clampCount(pearlJsonValue) + clampCount(pearlJsonBound));
   }

   private static int getClampedAt(int[] pearlJsonValues, int index) {
      return index < pearlJsonValues.length ? clampCount(pearlJsonValues[index]) : 0;
   }

   public static FightStatTotals fromStatsArray(int[] classValues) {
      FightStatTotals pearlJson = new FightStatTotals();
      if (classValues == null) {
         return pearlJson;
      } else {
         pearlJson.opponentPopTotal = getClampedAt(classValues, 0);
         pearlJson.ownPopTotal = getClampedAt(classValues, 1);
         pearlJson.killCount = getClampedAt(classValues, 2);
         pearlJson.deathCount = getClampedAt(classValues, 3);
         pearlJson.juggleStrikeTotal = getClampedAt(classValues, 4);
         pearlJson.eatabActionTotal = getClampedAt(classValues, 5);
         return pearlJson;
      }
   }

   public static int clampCount(int pearlJsonBound) {
      return Math.max(0, Math.min(1000000, pearlJsonBound));
   }

   public void addTotals(FightStatTotals pearlJson) {
      if (pearlJson != null) {
         this.opponentPopTotal = addClampedCounts(this.opponentPopTotal, pearlJson.opponentPopTotal);
         this.ownPopTotal = addClampedCounts(this.ownPopTotal, pearlJson.ownPopTotal);
         this.killCount = addClampedCounts(this.killCount, pearlJson.killCount);
         this.deathCount = addClampedCounts(this.deathCount, pearlJson.deathCount);
         this.juggleStrikeTotal = addClampedCounts(this.juggleStrikeTotal, pearlJson.juggleStrikeTotal);
         this.eatabActionTotal = addClampedCounts(this.eatabActionTotal, pearlJson.eatabActionTotal);
      }
   }

   public int[] toStatsArray() {
      return new int[]{clampCount(this.opponentPopTotal), clampCount(this.ownPopTotal), clampCount(this.killCount), clampCount(this.deathCount), clampCount(this.juggleStrikeTotal), clampCount(this.eatabActionTotal)};
   }
}
