package com.corz.client.utils;

import java.util.HashMap;
import java.util.Map;
import com.corz.client.utils.DeltaHeadroomUtils;

public final class MovementCorrections {
   public long movementEdgeCorrections;
   public long movementEdgesLearned;
   private final Map<MovementCorrections.Key, MovementCorrections.Record> learnedEdgeRecordMap = new HashMap<>();

   public boolean canLearnedEdge(MovementCorrections.Key botCrystalText, long learnedEdgeValue) {
      MovementCorrections.Record record2 = this.learnedEdgeRecordMap.get(botCrystalText);
      return record2 != null && record2.learnedEdgeFlag && record2.recordTick == learnedEdgeValue;
   }

   public String formatMovementCorrections() {
      return "movementEdgeCorrections="
         + this.movementEdgeCorrections
         + " movementEdgesLearned="
         + this.movementEdgesLearned
         + " movementEdgeRecords="
         + this.learnedEdgeRecordMap.size();
   }

   public int getValue() {
      return this.learnedEdgeRecordMap.size();
   }

   public MovementCorrections.Verdict getLearnedEdge(MovementCorrections.Key botCrystalText, long time, double amount, int value) {
      this.movementEdgeCorrections++;
      MovementCorrections.Record botCrystalValue = this.learnedEdgeRecordMap.get(botCrystalText);
      if (botCrystalValue == null) {
         if (this.learnedEdgeRecordMap.size() >= 256) {
            this.learnedEdgeRecordMap.clear();
         }

         botCrystalValue = new MovementCorrections.Record();
         this.learnedEdgeRecordMap.put(botCrystalText, botCrystalValue);
      }

      if (botCrystalValue.recordTick != time) {
         botCrystalValue.learnedEdgeCount = 0;
         botCrystalValue.peakAmount = 0.0;
         botCrystalValue.learnedEdgeFlag = false;
         botCrystalValue.recordTick = time;
      }

      botCrystalValue.learnedEdgeCount++;
      botCrystalValue.lastAmount = amount;
      botCrystalValue.peakAmount = Math.max(botCrystalValue.peakAmount, amount);
      botCrystalValue.sampleValue = value;
      if (botCrystalValue.learnedEdgeFlag) {
         return MovementCorrections.Verdict.ALREADY_LEARNED;
      } else if (botCrystalValue.learnedEdgeCount < 2 && !(amount >= 0.6)) {
         return MovementCorrections.Verdict.OBSERVED;
      } else {
         botCrystalValue.learnedEdgeFlag = true;
         this.movementEdgesLearned++;
         return MovementCorrections.Verdict.LEARNED;
      }
   }

   public void update() {
      this.learnedEdgeRecordMap.clear();
   }

   public MovementCorrections.Record getMaxForKey(MovementCorrections.Key botCrystalText) {
      return this.learnedEdgeRecordMap.get(botCrystalText);
   }

   public record Key(long from, long to, DeltaHeadroomUtils.Kind transition) {
   }

   public static enum Recommit {
      FREE,
      SETTLED_AND_CENTRED,
      ALTERNATE_EDGE;
   }

   public static final class Record {
      public int sampleValue;
      public int learnedEdgeCount;
      public long recordTick;
      public boolean learnedEdgeFlag;
      public double lastAmount;
      public double peakAmount;
   }

   public static enum Verdict {
      OBSERVED,
      LEARNED,
      ALREADY_LEARNED;
   }
}
