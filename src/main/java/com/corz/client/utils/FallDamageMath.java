package com.corz.client.utils;

public final class FallDamageMath {

   public static boolean isDamageWithinBudget(float hashHuntValue, float hashHuntValue2) {
      return hashHuntValue <= hashHuntValue2 + 1.0E-4F;
   }

   private FallDamageMath() {
   }

   public static float computeFallDamage(double hashHuntLimit, float hashHuntValue, int hashHuntValue2, boolean hashHuntFlag) {
      if (!hashHuntFlag && hashHuntLimit > 3.0) {
         float hashHuntFactor = (float)Math.ceil(hashHuntLimit - 3.0 - 1.0E-6);
         return applyDamageReduction(hashHuntFactor, hashHuntValue, hashHuntValue2);
      } else {
         return 0.0F;
      }
   }

   public static float computePearlDamage(float hashHuntValue, int hashHuntLimit) {
      return applyDamageReduction(5.0F, hashHuntValue, hashHuntLimit);
   }

   static float applyDamageReduction(float hashHuntFactor, float hashHuntBound, int hashHuntLimit) {
      float hashHuntValue = Float.isFinite(hashHuntBound) ? Math.max(0.0F, Math.min(20.0F, hashHuntBound)) : 0.0F;
      float hashHuntValue2 = hashHuntFactor * (1.0F - hashHuntValue * 0.04F);
      if (hashHuntLimit >= 0) {
         hashHuntValue2 *= Math.max(0.0F, 1.0F - 0.2F * (hashHuntLimit + 1));
      }

      return hashHuntValue2;
   }

   public static int getMaxSafeFallDistance(float hashHuntValue, int hashHuntValue2, float hashHuntValue3, int hashHuntLimit) {
      int hashHuntCounter = 3;

      while (hashHuntCounter < hashHuntLimit && isDamageWithinBudget(computeFallDamage(hashHuntCounter + 1, hashHuntValue, hashHuntValue2, false), hashHuntValue3)) {
         hashHuntCounter++;
      }

      return hashHuntCounter;
   }
}
