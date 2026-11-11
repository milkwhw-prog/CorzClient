package com.corz.client.utils;
import com.corz.client.utils.ExactJobData;

public final class JobMultiUtils {
   public static JobMultiUtils.Ruling decideCensusRuling(String censusCompleteText, int censusCompleteValue, long expectedCellCount, ExactJobData exactJobData, ExactJobData autoBuilder) {
      if (exactJobData != null && exactJobData.isUnobserved()) {
         if (exactJobData.isCellUnobserved()) {
            return new JobMultiUtils.Ruling(JobMultiUtils.Verdict.COMPLETE_ON_COORDINATOR_CENSUS, clampPlacedCount(exactJobData.getMaxCellCount(), censusCompleteValue));
         } else {
            return exactJobData.missingCount() == 0 && autoBuilder != null && autoBuilder.canJobGeneration(censusCompleteText, censusCompleteValue, expectedCellCount)
               ? new JobMultiUtils.Ruling(JobMultiUtils.Verdict.COMPLETE_ON_OWNER_CENSUS, clampPlacedCount(autoBuilder.getMaxCellCount(), censusCompleteValue))
               : new JobMultiUtils.Ruling(JobMultiUtils.Verdict.REFUTED_BY_COORDINATOR, clampPlacedCount(exactJobData.getMaxCellCount(), censusCompleteValue));
         }
      } else {
         return autoBuilder != null && autoBuilder.canJobGeneration(censusCompleteText, censusCompleteValue, expectedCellCount)
            ? new JobMultiUtils.Ruling(JobMultiUtils.Verdict.COMPLETE_ON_OWNER_CENSUS, clampPlacedCount(autoBuilder.getMaxCellCount(), censusCompleteValue))
            : new JobMultiUtils.Ruling(JobMultiUtils.Verdict.REFUSED_NO_PROOF, exactJobData == null ? 0 : clampPlacedCount(exactJobData.getMaxCellCount(), censusCompleteValue));
      }
   }

   private static int clampPlacedCount(int jobMultiBound, int censusCompleteValue) {
      return Math.max(0, Math.min(jobMultiBound, censusCompleteValue));
   }

   private JobMultiUtils() {
   }

   public record Ruling(JobMultiUtils.Verdict verdict, int placedToRecord) {
      public boolean isCensusComplete() {
         return this.verdict.isCensusComplete();
      }
   }

   public static enum Verdict {
      COMPLETE_ON_COORDINATOR_CENSUS,
      COMPLETE_ON_OWNER_CENSUS,
      REFUTED_BY_COORDINATOR,
      REFUSED_NO_PROOF;

      public boolean isCensusComplete() {
         return this == COMPLETE_ON_COORDINATOR_CENSUS || this == COMPLETE_ON_OWNER_CENSUS;
      }
   }
}
