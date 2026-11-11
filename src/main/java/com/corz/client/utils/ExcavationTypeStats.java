package com.corz.client.utils;
import com.corz.client.utils.CraftedExcavation;

public final class ExcavationTypeStats {
   public int minWorldLoadedValue;
   private final int[] typeCounts = new int[148];
   private int groupSampleCount;
   public int peakValue;
   public int entitiesWorldValue;
   public int cityValue;
   public int maxWorldLoadedValue;
   private final int[] groupCounts = new int[27];
   private final boolean[] seenTypeFlags = new boolean[148];
   public int biomeCraftedValue;
   public int entitiesWorldTier;

   public static boolean isCountedTypeId(int handleValue) {
      switch (handleValue) {
         case 114:
         case 115:
         case 118:
         case 121:
         case 122:
         case 125:
         case 126:
         case 129:
         case 130:
         case 131:
         case 137:
         case 145:
            return true;
         case 116:
         case 117:
         case 119:
         case 120:
         case 123:
         case 124:
         case 127:
         case 128:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         default:
            return false;
      }
   }

   public boolean hasAnyTrackedType() {
      if (this.typeCounts[34] > 0) {
         return true;
      } else {
         for (int i = 113; i < 148; i++) {
            if (this.typeCounts[i] > 0) {
               return true;
            }
         }

         return false;
      }
   }

   public int getMostCommonGroup() {
      int handleValue = 0;
      int handleValue2 = 0;

      for (int i = 0; i < this.groupCounts.length; i++) {
         if (this.groupCounts[i] > handleValue2) {
            handleValue2 = this.groupCounts[i];
            handleValue = i;
         }
      }

      return handleValue;
   }

   public double getGroupsFraction(int... handleValues) {
      if (this.groupSampleCount == 0) {
         return 0.0;
      } else {
         int handleTotal = 0;

         for (int handleValue : handleValues) {
            handleTotal += this.getGroupCount(handleValue);
         }

         return (double)handleTotal / this.groupSampleCount;
      }
   }

   public static ExcavationTypeStats fromExcavations(Iterable<CraftedExcavation> iterable, int value) {
      ExcavationTypeStats flagsHandle = new ExcavationTypeStats();
      flagsHandle.biomeCraftedValue = value;

      for (CraftedExcavation craftedExcavation : iterable) {
         flagsHandle.incrementTypeCount(craftedExcavation.craftedTypeId);
         flagsHandle.incrementGroupCount(craftedExcavation.craftedGroupId);
         flagsHandle.recordWorldLoadedValue(craftedExcavation.posY);
      }

      return flagsHandle;
   }

   public double getGroupFraction(int handleValue) {
      return this.groupSampleCount == 0 ? 0.0 : (double)this.getGroupCount(handleValue) / this.groupSampleCount;
   }

   public void markTypeSeen(int index) {
      if (index > 0 && index < 148) {
         this.seenTypeFlags[index] = true;
      }
   }

   public void addCountedTypes(ExcavationTypeStats flagsHandle) {
      for (int i = 1; i < 148; i++) {
         if (isCountedTypeId(i)) {
            this.typeCounts[i] = this.typeCounts[i] + flagsHandle.typeCounts[i];
         }
      }
   }

   public int getGroupCount(int index) {
      return index >= 0 && index < 27 ? this.groupCounts[index] : 0;
   }

   public boolean isTypeCountsEmpty() {
      for (int handleValue : this.typeCounts) {
         if (handleValue != 0) {
            return false;
         }
      }

      return true;
   }

   public int getGroupSampleCount() {
      return this.groupSampleCount;
   }

   public boolean wasTypeSeen(int index) {
      return index > 0 && index < 148 && this.seenTypeFlags[index];
   }

   public int getTypeCount(int index) {
      return index > 0 && index < 148 ? this.typeCounts[index] : 0;
   }

   public ExcavationTypeStats() {
      this.groupSampleCount = 0;
      this.biomeCraftedValue = 0;
      this.minWorldLoadedValue = Integer.MAX_VALUE;
      this.maxWorldLoadedValue = Integer.MIN_VALUE;
      this.entitiesWorldTier = 0;
      this.entitiesWorldValue = 0;
      this.cityValue = 0;
      this.peakValue = 0;
   }

   public void recordWorldLoadedValue(int value) {
      if (value < this.minWorldLoadedValue) {
         this.minWorldLoadedValue = value;
      }

      if (value > this.maxWorldLoadedValue) {
         this.maxWorldLoadedValue = value;
      }
   }

   public int getTypeCountSum(int... handleValues) {
      int handleTotal = 0;

      for (int handleValue : handleValues) {
         handleTotal += this.getTypeCount(handleValue);
      }

      return handleTotal;
   }

   public void incrementTypeCount(int index) {
      if (index > 0 && index < 148) {
         this.typeCounts[index]++;
      }
   }

   public void mergeFrom(ExcavationTypeStats flagsHandle) {
      for (int i = 0; i < this.typeCounts.length; i++) {
         this.typeCounts[i] = this.typeCounts[i] + flagsHandle.typeCounts[i];
      }

      for (int j = 0; j < this.groupCounts.length; j++) {
         this.groupCounts[j] = this.groupCounts[j] + flagsHandle.groupCounts[j];
      }

      for (int k = 0; k < this.seenTypeFlags.length; k++) {
         this.seenTypeFlags[k] = this.seenTypeFlags[k] | flagsHandle.seenTypeFlags[k];
      }

      this.groupSampleCount = this.groupSampleCount + flagsHandle.groupSampleCount;
      this.biomeCraftedValue = flagsHandle.biomeCraftedValue;
      if (flagsHandle.minWorldLoadedValue < this.minWorldLoadedValue) {
         this.minWorldLoadedValue = flagsHandle.minWorldLoadedValue;
      }

      if (flagsHandle.maxWorldLoadedValue > this.maxWorldLoadedValue) {
         this.maxWorldLoadedValue = flagsHandle.maxWorldLoadedValue;
      }
   }

   public void addTypeCount(int index, int handleValue) {
      if (index > 0 && index < 148) {
         this.typeCounts[index] = this.typeCounts[index] + handleValue;
      }
   }

   public boolean hasHighWorldLoadedValue() {
      return this.maxWorldLoadedValue >= 40;
   }

   public void incrementGroupCount(int index) {
      if (index >= 0 && index < 27) {
         this.groupCounts[index]++;
         this.groupSampleCount++;
      }
   }
}
