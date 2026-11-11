package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;

public final class TierBuilderUtils {
   public static List<TierBuilderUtils.Candidate> getTargetTier(
      List<TierBuilderUtils.Candidate> list, float targetTierValue, float pitch2, String targetTierText, String face2
   ) {
      ArrayList arrayList = new ArrayList(list);
      ArrayList arrayList2 = new ArrayList();
      long minValue = Long.MIN_VALUE;

      while (!arrayList.isEmpty()) {
         int targetTierBound = Integer.MAX_VALUE;

         for (TierBuilderUtils.Candidate candidate : (Iterable<com.corz.client.utils.TierBuilderUtils.Candidate>) (arrayList)) {
            targetTierBound = Math.min(targetTierBound, candidate.tier);
         }

         TierBuilderUtils.Candidate totalPriceData = null;
         double targetTierLimit = Double.MAX_VALUE;

         for (TierBuilderUtils.Candidate candidate3 : (Iterable<com.corz.client.utils.TierBuilderUtils.Candidate>) (arrayList)) {
            if (candidate3.tier == targetTierBound) {
               double targetTierValue4 = minValue == Long.MIN_VALUE
                  ? 0.0
                  : Math.abs(ProofSupportUtils.getValueInt(minValue) - ProofSupportUtils.getValueInt(candidate3.target))
                     + Math.abs(ProofSupportUtils.getValueForLong(minValue) - ProofSupportUtils.getValueForLong(candidate3.target));
               double targetTierValue5 = Math.abs(getAmount(targetTierValue, candidate3.yaw))
                  + 0.6 * Math.abs(pitch2 - candidate3.pitch)
                  + (targetTierText != null && !targetTierText.equals(candidate3.material) ? 240 : 0)
                  + (face2 != null && !face2.equals(candidate3.face) ? 30 : 0)
                  + (candidate3.hotbar ? 0 : 120)
                  + (candidate3.edge ? 4 : 0)
                  + targetTierValue4 * 4.0;
               if (targetTierValue5 < targetTierLimit || targetTierValue5 == targetTierLimit && (totalPriceData == null || candidate3.target < totalPriceData.target)) {
                  totalPriceData = candidate3;
                  targetTierLimit = targetTierValue5;
               }
            }
         }

         arrayList2.add(totalPriceData);
         arrayList.remove(totalPriceData);
         targetTierValue = totalPriceData.yaw;
         pitch2 = totalPriceData.pitch;
         targetTierText = totalPriceData.material;
         face2 = totalPriceData.face;
         minValue = totalPriceData.target;
      }

      return List.copyOf(arrayList2);
   }

   public static List<TierBuilderUtils.Run> getCopyOf(List<TierBuilderUtils.Candidate> list) {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      TierBuilderUtils.Candidate candidate = null;

      for (TierBuilderUtils.Candidate totalPriceData : list) {
         boolean tierBuilderFlag = candidate != null
            && Math.abs(ProofSupportUtils.getValueInt(totalPriceData.target) - ProofSupportUtils.getValueInt(candidate.target))
                  + Math.abs(ProofSupportUtils.getValueForLong(totalPriceData.target) - ProofSupportUtils.getValueForLong(candidate.target))
               == 1;
         if (candidate != null && (!tierBuilderFlag || totalPriceData.stand != candidate.stand || !totalPriceData.material.equals(candidate.material) || !totalPriceData.face.equals(candidate.face))) {
            arrayList.add(new TierBuilderUtils.Run(candidate.stand, candidate.material, candidate.face, List.copyOf(arrayList2)));
            arrayList2.clear();
         }

         arrayList2.add(totalPriceData);
         candidate = totalPriceData;
      }

      if (candidate != null) {
         arrayList.add(new TierBuilderUtils.Run(candidate.stand, candidate.material, candidate.face, List.copyOf(arrayList2)));
      }

      return List.copyOf(arrayList);
   }

   public static float getAmount(float tierBuilderValue, float tierBuilderValue2) {
      float tierBuilderLimit = (tierBuilderValue2 - tierBuilderValue) % 360.0F;
      return tierBuilderLimit >= 180.0F ? tierBuilderLimit - 360.0F : (tierBuilderLimit < -180.0F ? tierBuilderLimit + 360.0F : tierBuilderLimit);
   }

   public record Candidate(long target, long stand, String material, String face, float yaw, float pitch, boolean edge, boolean hotbar, int tier) {
      public Candidate(long target, long pitch, String edge, String hotbar, float material, float face, boolean stand, boolean yaw) {
         this(target, pitch, edge, hotbar, material, face, stand, yaw, stand ? 1 : 0);
      }
   }

   public record Run(long stand, String material, String face, List<TierBuilderUtils.Candidate> targets) {
   }
}
