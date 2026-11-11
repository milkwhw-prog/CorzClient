package com.corz.client.utils;

import com.corz.client.config.OpponentProfileStore;
import com.corz.client.config.FightStatTotals;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.MaxMin;
import com.corz.client.utils.CrystalPop;
import com.corz.client.utils.HabitsRecordData;

public final class MinVec {
   private final FightStatTotals classPearlJson;
   private final AmountAmounts armsAmountAmounts = new AmountAmounts();
   private final String vecText;
   private final String uidText;
   private boolean armsActive;
   private final UUID uUID;
   private int habitScore;
   private final MaxMin maxMin = new MaxMin();
   private boolean uidActive;
   private final CrystalPop crystalPop;

   public MaxMin getMaxMin() {
      return this.maxMin;
   }

   public String getText() {
      return this.vecText;
   }

   public boolean isFlag() {
      return this.armsActive;
   }

   public String getUidText() {
      return this.uidText;
   }

   public void updateMinFlag() {
      if (this.habitScore < 1000000) {
         this.habitScore++;
      }

      this.uidActive = true;
   }

   public FightStatTotals getClassPearlJson() {
      return this.classPearlJson;
   }

   public MinVec(String vecText, UUID uUID, Random random) {
      this.classPearlJson = new FightStatTotals();
      this.uidText = vecText == null ? "" : vecText;
      this.uUID = uUID;
      this.vecText = OpponentProfileStore.toProfileKey(vecText);
      this.crystalPop = new CrystalPop(random);
   }

   public CrystalPop getCrystalPop() {
      return this.crystalPop;
   }

   public AmountAmounts getArmsAmountAmounts() {
      return this.armsAmountAmounts;
   }

   public void handleMinArms(HabitsRecordData habitsRecordData) {
      if (habitsRecordData != null) {
         this.armsAmountAmounts.handleAmountAmounts(habitsRecordData.cal());
         this.maxMin.handlePearlTime(habitsRecordData.habits());
         LinkedHashMap armsMap = new LinkedHashMap();
         if (habitsRecordData.arms() != null) {
            for (Entry outerEntry : habitsRecordData.arms().entrySet()) {
               if (outerEntry.getValue() != null) {
                  LinkedHashMap armsForKey = new LinkedHashMap();

                  for (Entry innerEntry : (Iterable<java.util.Map.Entry>) (((Map)outerEntry.getValue()).entrySet())) {
                     double[] armsValues = (double[])innerEntry.getValue();
                     if (armsValues != null && armsValues.length >= 2) {
                        armsForKey.put((String)innerEntry.getKey(), new CrystalPop.Arm(armsValues[0], armsValues[1]));
                     }
                  }

                  armsMap.put((String)outerEntry.getKey(), armsForKey);
               }
            }
         }

         this.crystalPop.handleMeanEnum(armsMap);
         this.classPearlJson.addTotals(FightStatTotals.fromStatsArray(habitsRecordData.stats()));
         this.habitScore = (int)Math.min(1000000L, (long)this.habitScore + FightStatTotals.clampCount(habitsRecordData.v()));
         this.armsActive = true;
      }
   }

   public boolean isUidActive() {
      return this.uidActive;
   }

   public HabitsRecordData getUidMin(long uidBound) {
      this.uidActive = false;
      LinkedHashMap armsMap = new LinkedHashMap();

      for (Entry outerEntry : this.crystalPop.getEnum().entrySet()) {
         LinkedHashMap armsForKey = new LinkedHashMap();

         for (Entry innerEntry : (Iterable<java.util.Map.Entry>) (((Map)outerEntry.getValue()).entrySet())) {
            armsForKey.put((String)innerEntry.getKey(), new double[]{((CrystalPop.Arm)innerEntry.getValue()).n(), ((CrystalPop.Arm)innerEntry.getValue()).mean()});
         }

         armsMap.put((String)outerEntry.getKey(), armsForKey);
      }

      return new HabitsRecordData(
         1,
         this.uidText.toLowerCase(Locale.ROOT),
         this.uUID == null ? "" : this.uUID.toString(),
         Math.max(0L, uidBound),
         this.habitScore,
         this.armsAmountAmounts.getAmountAmounts(),
         this.maxMin.getAmountJumpResetCounts(),
         armsMap,
         this.classPearlJson.toStatsArray()
      );
   }

   public void updateFlag() {
      this.uidActive = true;
   }

   public int getMin() {
      return this.habitScore;
   }

   public UUID getUUID() {
      return this.uUID;
   }
}
