package com.corz.client.utils;

public final class AmountPreviewUtils {

   public static float getAmount(float previewValue, boolean previewFlag) {
      return previewFlag ? 1.0F - getAmount8(previewValue / 170.0F) : getAmount6(previewValue / 220.0F);
   }

   public static float getAmount(float previewValue, float thumbsLimit, float previewValue3, float previewFactor, float previewLimit) {
      if (previewFactor <= 0.0F) {
         return previewValue;
      } else if (previewValue3 <= 0.0F) {
         return thumbsLimit;
      } else {
         float previewValue4 = previewValue + (thumbsLimit - previewValue) * (1.0F - (float)Math.exp(-previewValue3 * previewFactor));
         return Math.abs(thumbsLimit - previewValue4) < previewLimit ? thumbsLimit : previewValue4;
      }
   }

   public static float getAmount(float previewValue, float thumbsLimit, float previewValue3, float previewFactor) {
      return getAmount(previewValue, thumbsLimit, previewValue3, previewFactor, 0.001F);
   }

   public static float getAmountForFloatFlag(float previewValue, boolean previewFlag) {
      return previewFlag ? 1.0F - 0.035000026F * getAmount6(previewValue / 150.0F) : 0.94F + 0.060000002F * getAmount((previewValue - 24.0F) / 250.0F, 1.55F);
   }

   public static float getAmount(float previewLimit) {
      if (previewLimit <= 0.0F || previewLimit >= 1.0F) {
         return 0.0F;
      } else {
         return previewLimit < 0.3F ? getAmount6(previewLimit / 0.3F) : 1.0F - getAmount((previewLimit - 0.3F) / 0.7F, 2.0F);
      }
   }

   public static float getAmount(float previewValue, float previewFactor) {
      float previewFactor2 = getMin(previewValue) - 1.0F;
      return 1.0F + (previewFactor + 1.0F) * previewFactor2 * previewFactor2 * previewFactor2 + previewFactor * previewFactor2 * previewFactor2;
   }

   public static float getAmountFloat(float previewValue, boolean previewFlag) {
      return previewFlag ? 4.0F * getAmount6(previewValue / 150.0F) : 8.0F * (1.0F - getAmount6((previewValue - 24.0F) / 250.0F));
   }

   public static float getAmount(float previewValue, int previewValue2, int previewBound, boolean previewFlag) {
      return previewFlag ? getAmount(previewValue, previewValue2, true) : getAmount(previewValue - Math.min(Math.max(previewBound, 0), 10) * 16.0F, previewValue2, false);
   }

   public static float getAmountForFloat(float previewValue) {
      return 0.975F + 0.024999976F * getAmount(previewValue / 260.0F, 1.2F);
   }

   public static float getAmount3(float previewValue) {
      return 1.0F - getAmount6(previewValue / 154.0F);
   }

   public static float getAmount4(float previewValue, boolean previewFlag) {
      return previewFlag ? 1.0F - getAmount6(previewValue / 150.0F) : 0.5F * getAmount6(previewValue / 60.0F) + 0.5F * getAmount6((previewValue - 30.0F) / 150.0F);
   }

   public static float getAmount4(float previewValue) {
      return 3.0F * (1.0F - getMin(previewValue));
   }

   public static float getAmount5(float previewValue) {
      return getAmount6((previewValue - 33.0F) / 187.0F);
   }

   private AmountPreviewUtils() {
   }

   public static float getAmount6(float previewValue) {
      float previewFactor = 1.0F - getMin(previewValue);
      return 1.0F - previewFactor * previewFactor * previewFactor;
   }

   public static float getAmount7(float previewValue) {
      return 1.0F + 0.014999986F * getAmount6(previewValue / 154.0F);
   }

   public static float getAmount8(float previewFactor) {
      previewFactor = getMin(previewFactor);
      if (previewFactor < 0.5F) {
         return 4.0F * previewFactor * previewFactor * previewFactor;
      } else {
         float previewFactor2 = 1.0F - previewFactor;
         return 1.0F - 4.0F * previewFactor2 * previewFactor2 * previewFactor2;
      }
   }

   public static float getAmount9(float previewFactor) {
      previewFactor = getMin(previewFactor);
      return previewFactor * previewFactor;
   }

   public static float getAmount(int previewValue) {
      return 70.0F + Math.max(0, previewValue - 1) * 26.0F + 160.0F + 150.0F;
   }

   public static float getAmount(float previewValue, int previewBound, boolean previewFlag) {
      return previewFlag ? 1.0F - getAmount6(previewValue / 110.0F) : getAmount6((previewValue - 70.0F - Math.max(0, previewBound) * 26.0F) / 150.0F);
   }

   public static float getAmount10(float previewValue) {
      return getAmount6(previewValue / 182.0F);
   }

   public static float getMin(float previewLimit) {
      return previewLimit < 0.0F ? 0.0F : (previewLimit > 1.0F ? 1.0F : previewLimit);
   }

   public static float getAmount11(float previewValue) {
      return 0.97F + 0.029999971F * getAmount6(previewValue / 220.0F);
   }

   public static boolean canFlag(float previewLimit) {
      return previewLimit >= 170.0F;
   }
}
