package com.corz.client.utils;

import java.util.Locale;

public final class ExhaustedLostUtils {
   public static boolean canExhausted(ExhaustedLostUtils.Yield yield) {
      return yield == ExhaustedLostUtils.Yield.EXHAUSTED;
   }

   private ExhaustedLostUtils() {
   }

   public record End(
      int id,
      String kind,
      int plannedMembers,
      int attempted,
      int confirmed,
      int tempPlaced,
      int tempRemoved,
      double travel,
      int jumpTransitions,
      int verticalTransitions,
      int durationTicks,
      ExhaustedLostUtils.Yield yieldReason
   ) {
      public String formatEndSummary() {
         return String.format(
            Locale.ROOT,
            "id=%d kind=%s plannedMembers=%d attempted=%d confirmed=%d tempPlaced=%d tempRemoved=%d travel=%.1f jumpTransitions=%d verticalTransitions=%d durationTicks=%d yieldReason=%s",
            this.id,
            this.kind,
            this.plannedMembers,
            this.attempted,
            this.confirmed,
            this.tempPlaced,
            this.tempRemoved,
            this.travel,
            this.jumpTransitions,
            this.verticalTransitions,
            this.durationTicks,
            this.yieldReason
         );
      }

      public double getDurationTicks() {
         return this.durationTicks <= 0 ? 0.0 : this.confirmed * 20.0 / this.durationTicks;
      }
   }

   public static final class Tracker {
      private int programsEndedCount;
      private int appendCount;
      private int programsOpenValue = -1;
      private String programsEndedText = "";
      private int programsEndedBaseline;

      public boolean canProgramsEnded(int programsOpenValue, String text, int value) {
         boolean programsEnded = this.programsOpenValue < 0;
         this.appendCount++;
         this.programsOpenValue = programsOpenValue;
         this.programsEndedText = text;
         this.programsEndedBaseline = value;
         return programsEnded;
      }

      public String getAppend() {
         return "programsSelected="
            + this.appendCount
            + " programsEnded="
            + this.programsEndedCount
            + " programsOpen="
            + (this.programsOpenValue >= 0 ? 1 : 0);
      }

      public boolean isProgramsEnded() {
         return this.appendCount == this.programsEndedCount && this.programsOpenValue < 0;
      }

      public boolean isProgramsOpen() {
         return this.programsOpenValue >= 0;
      }

      public int getProgramsEndedBaselineDelta() {
         return this.appendCount;
      }

      public ExhaustedLostUtils.End getProgramsEnded(
         int programsEndedValue,
         int programsEndedValue2,
         int programsEndedValue3,
         int programsEndedValue4,
         int programsEndedValue5,
         double programsEndedValue6,
         int programsEndedValue7,
         int programsEndedValue8,
         int programsEndedValue9,
         ExhaustedLostUtils.Yield yield
      ) {
         if (this.programsOpenValue < 0) {
            return null;
         } else {
            ExhaustedLostUtils.End end = new ExhaustedLostUtils.End(
               this.programsOpenValue,
               this.programsEndedText,
               programsEndedValue,
               programsEndedValue2,
               programsEndedValue3,
               programsEndedValue4,
               programsEndedValue5,
               programsEndedValue6,
               programsEndedValue7,
               programsEndedValue8,
               Math.max(0, programsEndedValue9 - this.programsEndedBaseline),
               yield
            );
            this.programsOpenValue = -1;
            this.programsEndedText = "";
            this.programsEndedCount++;
            return end;
         }
      }

      public int getProgramsOpenValue() {
         return this.programsOpenValue;
      }

      public String getProgramsEndedText() {
         return this.programsEndedText;
      }

      public void updateProgramsEnded() {
         this.appendCount = 0;
         this.programsEndedCount = 0;
         this.programsOpenValue = -1;
         this.programsEndedText = "";
      }

      public int getProgramsEndedCount() {
         return this.programsEndedCount;
      }
   }

   public static enum Yield {
      EXHAUSTED,
      POSE_INVALIDATED,
      MATERIAL_LOST,
      DEPENDENCY_CHANGED,
      ACCESS_LOST,
      ACCESS_FAILED,
      TEMP_FAILED,
      SUPERSEDED,
      ABORTED;
   }
}
