package com.corz.client.utils;

import java.util.Locale;

public final class CrystalBotCombatStats {
   private int rcStreakCount;
   private int currentRunLength;
   private int jmaxCount;
   private int timeFlagTickCount;
   private long lastJmaxTick;
   private int shnsCount;
   private int jmaxPeak;
   private int laCount;
   private double eatminBound = Double.NaN;
   private int eatminAttemptCount;
   private int latCount;
   private boolean runActive;
   private int epCount;
   private int rccCount;
   private int pnCount;
   private double shnsSampleSum;
   private int shnsAttemptCount;
   private int shnsHitCount;
   private int shnsSampleCount;
   private double learnSampleSum;
   private int apCount;
   private int learnSampleCount;
   private int aboCount;
   private int eatabEatminCount;
   private int longRunCount;
   private int learnEatabCount;
   private int abCount;
   private int eatminHitCount;
   private int timeFlagStreak;
   private int blCount;

   public void addShnsSample(float shnsEatbadBound) {
      if (Float.isFinite(shnsEatbadBound)) {
         this.shnsSampleSum = this.shnsSampleSum + Math.max(0.0F, shnsEatbadBound);
         this.shnsSampleCount++;
      }
   }

   public void incrementEatabEatminCount() {
      this.eatabEatminCount++;
   }

   public CrystalBotCombatStats() {
      this.lastJmaxTick = Long.MIN_VALUE;
   }

   private static String formatAverage(double eatabEatbadValue, int eatabEatbadDivisor) {
      return formatOneDecimal(eatabEatbadDivisor <= 0 ? 0.0 : eatabEatbadValue / eatabEatbadDivisor);
   }

   private void finishRun() {
      if (this.currentRunLength >= 2) {
         this.longRunCount++;
      }

      this.jmaxPeak = Math.max(this.jmaxPeak, this.currentRunLength);
      this.currentRunLength = 0;
      this.timeFlagTickCount = 0;
   }

   public void recordJmaxTick(boolean jmaxFlag, long time) {
      this.blCount++;
      if (jmaxFlag && this.runActive) {
         this.currentRunLength++;
         this.jmaxPeak = Math.max(this.jmaxPeak, this.currentRunLength);
      } else {
         this.lastJmaxTick = time;
         this.timeFlagStreak = 0;
      }
   }

   public void updateRunState(boolean eatabEatbadFlag) {
      if (!eatabEatbadFlag) {
         if (this.runActive) {
            this.finishRun();
         }

         this.runActive = false;
         if (this.lastJmaxTick != Long.MIN_VALUE && ++this.timeFlagStreak > 6) {
            this.lastJmaxTick = Long.MIN_VALUE;
         }
      } else {
         if (!this.runActive) {
            this.timeFlagTickCount = 0;
            boolean eatabEatbadFlag2 = this.lastJmaxTick != Long.MIN_VALUE && this.timeFlagStreak <= 6;
            this.currentRunLength = eatabEatbadFlag2 ? 1 : 0;
            this.lastJmaxTick = Long.MIN_VALUE;
            this.jmaxPeak = Math.max(this.jmaxPeak, this.currentRunLength);
         }

         this.runActive = true;
         this.timeFlagTickCount++;
      }
   }

   public void incrementAbCount() {
      this.abCount++;
   }

   public void recordJmax(boolean jmaxFlag, boolean aggroVecBound) {
      if (jmaxFlag) {
         this.apCount++;
      } else {
         this.jmaxCount++;
      }

      if (aggroVecBound) {
         this.pnCount++;
      }
   }

   public int getLongRunTotal() {
      return this.longRunCount + (this.runActive && this.currentRunLength >= 2 ? 1 : 0);
   }

   public int getLaCount() {
      return this.laCount;
   }

   public void incrementLearnEatabCount() {
      this.learnEatabCount++;
   }

   public int getRcStreakCount() {
      return this.rcStreakCount;
   }

   public int getShnsHitCount() {
      return this.shnsHitCount;
   }

   public int getEatminHitCount() {
      return this.eatminHitCount;
   }

   public void incrementRccCount() {
      this.rccCount++;
   }

   public String formatSummary(String shnsEatbadText, String eatabEatbadText, String shnsEatbadText3) {
      StringBuilder stringBuilder = new StringBuilder(256);
      stringBuilder.append("combo=").append(isNullOrBlank(shnsEatbadText) ? "IDLE" : shnsEatbadText.trim());
      stringBuilder.append(" arm=").append(isNullOrBlank(eatabEatbadText) ? "-" : eatabEatbadText.trim());
      stringBuilder.append(" air=").append(this.getCurrentRunTicks());
      stringBuilder.append(" la=").append(this.laCount);
      stringBuilder.append(" lat=").append(this.latCount);
      stringBuilder.append(" jc=").append(this.getLongRunTotal());
      stringBuilder.append(" jmax=").append(this.jmaxPeak);
      stringBuilder.append(" bl=").append(this.blCount);
      stringBuilder.append(" pf=").append(this.jmaxCount);
      stringBuilder.append(" ap=").append(this.apCount);
      stringBuilder.append(" pn=").append(this.pnCount);
      stringBuilder.append(" rc=").append(this.rcStreakCount);
      stringBuilder.append(" rcc=").append(this.rccCount);
      stringBuilder.append(" ab=").append(this.abCount);
      stringBuilder.append(" fin=").append(this.shnsCount);
      stringBuilder.append(" ep=").append(this.epCount);
      stringBuilder.append(" abo=").append(this.aboCount);
      stringBuilder.append(" sh=").append(this.shnsAttemptCount);
      stringBuilder.append(" shns=").append(this.shnsHitCount);
      stringBuilder.append(" sdl=").append(formatAverage(this.shnsSampleSum, this.shnsSampleCount));
      stringBuilder.append(" eat=").append(this.eatminAttemptCount);
      stringBuilder.append(" eatbad=").append(this.eatminHitCount);
      stringBuilder.append(" eatmin=").append(Double.isNaN(this.eatminBound) ? "-" : formatOneDecimal(this.eatminBound));
      stringBuilder.append(" eatab=").append(this.eatabEatminCount);
      stringBuilder.append(" abs=").append(formatAverage(this.learnSampleSum, this.learnSampleCount));
      stringBuilder.append(" pt=").append(this.learnEatabCount);
      stringBuilder.append(" learn=").append(isNullOrBlank(shnsEatbadText3) ? "-" : shnsEatbadText3.trim());
      return stringBuilder.toString();
   }

   public void incrementLaCount() {
      this.laCount++;
   }

   public int getTotalJmaxCount() {
      return this.jmaxCount + this.apCount;
   }

   private static boolean isNullOrBlank(String eatabEatbadText) {
      return eatabEatbadText == null || eatabEatbadText.isBlank();
   }

   public int getCurrentRunTicks() {
      return this.runActive ? this.timeFlagTickCount : 0;
   }

   public void incrementShnsCount() {
      this.shnsCount++;
   }

   public void recordShnsAttempt(boolean shnsEatbadFlag) {
      this.shnsAttemptCount++;
      if (shnsEatbadFlag) {
         this.shnsHitCount++;
      }
   }

   public void incrementLatCount() {
      this.latCount++;
   }

   public void recordEatminResult(boolean eatminEatbadFlag, double eatminEatbadBound) {
      this.eatminAttemptCount++;
      if (eatminEatbadFlag) {
         this.eatminHitCount++;
      }

      this.eatminBound = Double.isNaN(eatminEatbadBound) ? 0.0 : Math.min(999.9, Math.max(0.0, eatminEatbadBound));
   }

   public int getBlCount() {
      return this.blCount;
   }

   public void addLearnSample(float learnEatabBound) {
      if (Float.isFinite(learnEatabBound)) {
         this.learnSampleSum = this.learnSampleSum + Math.max(0.0F, learnEatabBound);
         this.learnSampleCount++;
      }
   }

   private static String formatOneDecimal(double eatabEatbadValue) {
      return String.format(Locale.ROOT, "%.1f", eatabEatbadValue);
   }

   public void resetAll() {
      this.laCount = 0;
      this.latCount = 0;
      this.longRunCount = 0;
      this.jmaxPeak = 0;
      this.blCount = 0;
      this.jmaxCount = 0;
      this.apCount = 0;
      this.pnCount = 0;
      this.rcStreakCount = 0;
      this.rccCount = 0;
      this.abCount = 0;
      this.shnsCount = 0;
      this.epCount = 0;
      this.aboCount = 0;
      this.shnsAttemptCount = 0;
      this.shnsHitCount = 0;
      this.shnsSampleSum = 0.0;
      this.shnsSampleCount = 0;
      this.eatminAttemptCount = 0;
      this.eatminHitCount = 0;
      this.eatminBound = Double.NaN;
      this.eatabEatminCount = 0;
      this.learnSampleSum = 0.0;
      this.learnSampleCount = 0;
      this.learnEatabCount = 0;
      this.timeFlagTickCount = 0;
      this.currentRunLength = 0;
      this.runActive = false;
      this.lastJmaxTick = Long.MIN_VALUE;
      this.timeFlagStreak = 0;
   }

   public void recordShns(boolean shnsFlag) {
      this.epCount++;
      if (shnsFlag) {
         this.aboCount++;
      }
   }

   public void incrementRcStreakCount() {
      this.rcStreakCount++;
   }
}
