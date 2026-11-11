package com.corz.client.utils;

public final class CellScoring {

   private CellScoring() {
   }

   public static double getDistanceBandPenalty(double constHazLimit, double constHazLimit2, double constHazValue) {
      if (Double.isNaN(constHazLimit)) {
         return Double.POSITIVE_INFINITY;
      } else if (constHazLimit < constHazLimit2) {
         return (constHazLimit2 - constHazLimit) * 3.0;
      } else {
         return constHazLimit > constHazValue ? (constHazLimit - constHazValue) * 2.0 : 0.0;
      }
   }

   public static double scoreDefensiveCell(
      double defHazFactor,
      double defHazFactor2,
      double defHazValue,
      int defHazBound,
      double constHazLimit,
      double defHazValue3,
      double constHazValue,
      int defHazValue5,
      double defHazFactor3,
      double defHazFactor4,
      boolean defHazFlag,
      SelfHazData selfHazData,
      boolean defHazFlag2
   ) {
      if (selfHazData == null) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double vecHashValue = selfHazData.off() * defHazFactor
            - selfHazData.def() * defHazFactor2
            - selfHazData.move() * (defHazValue + 6.0 * Math.max(0, defHazBound))
            - selfHazData.space() * getDistanceBandPenalty(constHazLimit, defHazValue3, constHazValue)
            + selfHazData.elev() * getHeightScore(defHazValue5)
            - selfHazData.haz() * defHazFactor3
            + (defHazFlag2 ? 3.0 * selfHazData.def() : 3.0) * defHazFactor4
            + (defHazFlag ? 0.4 : 0.0);
         return Double.isNaN(vecHashValue) ? Double.NEGATIVE_INFINITY : vecHashValue;
      }
   }

   public static double getHeightScore(int constHazLimit) {
      if (constHazLimit == 1) {
         return -4.0;
      } else if (constHazLimit >= 2) {
         return -5.0;
      } else if (constHazLimit == -1) {
         return 3.0;
      } else if (constHazLimit == -2) {
         return 1.5;
      } else {
         return constHazLimit <= -3 ? -0.8 : 0.0;
      }
   }
}
