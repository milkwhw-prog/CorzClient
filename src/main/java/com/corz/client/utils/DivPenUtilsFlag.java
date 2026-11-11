package com.corz.client.utils;

public final class DivPenUtilsFlag {
   public final boolean penDivActive;
   public final boolean divPenActive;
   public final boolean countUniqueChunks;
   public final int penDivLimitCap;
   public final int penDivValue;
   public final int divPenValue;
   public final boolean countQuarterTotal;
   public final boolean usePenalizedResult;
   public final boolean requireCraftedPresence;
   public final boolean threadActive;
   public final boolean concurrentActive;
   public final boolean allowAncientCity;
   public final boolean requireQuarterDensity;
   public final boolean checkIgnoredPenalty;
   public final int minCraftedCount;
   public final boolean countCraftedExcavations;
   public final int minCounterThreshold;

   public static DivPenUtilsFlag getUtilsDivPenUtilsFlag() {
      return new DivPenUtilsFlag(70, 3, 10, 6, true, true, true, true, true, true, true, true, true, true, false);
   }

   public DivPenUtilsFlag(
      int value,
      int value2,
      int value3,
      int value4,
      int value5,
      boolean flag,
      boolean flag2,
      boolean flag3,
      boolean flag4,
      boolean flag5,
      boolean flag6,
      boolean flag7,
      boolean flag8,
      boolean flag9,
      boolean flag10,
      boolean flag11,
      boolean flag12
   ) {
      this.minCraftedCount = value;
      this.minCounterThreshold = value2;
      this.penDivValue = value3;
      this.penDivLimitCap = value4;
      this.divPenValue = value5;
      this.penDivActive = flag;
      this.divPenActive = flag2;
      this.countUniqueChunks = flag3;
      this.countCraftedExcavations = flag4;
      this.requireQuarterDensity = flag5;
      this.concurrentActive = flag6;
      this.countQuarterTotal = flag7;
      this.threadActive = flag8;
      this.requireCraftedPresence = flag9;
      this.usePenalizedResult = flag10;
      this.allowAncientCity = flag11;
      this.checkIgnoredPenalty = flag12;
   }

   public DivPenUtilsFlag(
      int divPenValue,
      int divPenValue2,
      int divPenValue3,
      int divPenValue4,
      boolean divPenFlag,
      boolean divPenFlag2,
      boolean divPenFlag3,
      boolean divPenFlag4,
      boolean divPenFlag5,
      boolean divPenFlag6,
      boolean divPenFlag7,
      boolean divPenFlag8,
      boolean divPenFlag9,
      boolean divPenFlag10,
      boolean divPenFlag11
   ) {
      this(
         divPenValue,
         divPenValue2,
         divPenValue3,
         0,
         divPenValue4,
         divPenFlag,
         divPenFlag2,
         divPenFlag3,
         divPenFlag4,
         divPenFlag5,
         divPenFlag6,
         divPenFlag7,
         divPenFlag8,
         true,
         divPenFlag9,
         divPenFlag10,
         divPenFlag11
      );
   }

   public DivPenUtilsFlag(
      int divPenValue,
      int divPenValue2,
      int divPenValue3,
      int divPenValue4,
      boolean divPenFlag,
      boolean divPenFlag2,
      boolean divPenFlag3,
      boolean divPenFlag4,
      boolean divPenFlag5,
      boolean divPenFlag6,
      boolean divPenFlag7,
      boolean divPenFlag8,
      boolean divPenFlag9
   ) {
      this(
         divPenValue,
         divPenValue2,
         divPenValue3,
         divPenValue4,
         divPenFlag,
         divPenFlag2,
         divPenFlag3,
         divPenFlag4,
         divPenFlag5,
         divPenFlag6,
         true,
         true,
         divPenFlag7,
         divPenFlag8,
         divPenFlag9
      );
   }

   public DivPenUtilsFlag(
      int divPenValue,
      int divPenValue2,
      int divPenValue3,
      int divPenValue4,
      boolean divPenFlag,
      boolean divPenFlag2,
      boolean divPenFlag3,
      boolean divPenFlag4,
      boolean divPenFlag5,
      boolean divPenFlag6,
      boolean divPenFlag7
   ) {
      this(
         divPenValue,
         divPenValue2,
         divPenValue3,
         divPenValue4,
         divPenFlag,
         divPenFlag2,
         divPenFlag3,
         divPenFlag4,
         true,
         true,
         divPenFlag5,
         divPenFlag6,
         divPenFlag7
      );
   }
}
