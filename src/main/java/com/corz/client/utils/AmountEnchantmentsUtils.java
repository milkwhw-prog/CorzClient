package com.corz.client.utils;

public final class AmountEnchantmentsUtils {

   public static double getAmount(int enchantmentsLimit, int enchantmentsValue, int enchantmentsValue2, int enchantmentsValue3, int enchantmentsValue4, int enchantmentsValue5, int enchantmentsValue6, double damageDamageable) {
      return 2.0
         + (enchantmentsLimit > 0 ? 0.5 * enchantmentsLimit + 0.5 : 0.0)
         + 0.1 * enchantmentsValue
         + 0.1 * enchantmentsValue2
         + enchantmentsValue3
         + 0.1 * enchantmentsValue4
         + 0.1 * enchantmentsValue5
         + 0.01 * enchantmentsValue6
         + 0.001 * damageDamageable;
   }

   public static double getAmount(int enchantmentsLimit) {
      return enchantmentsLimit > 0 ? 0.5 * enchantmentsLimit + 0.5 : 0.0;
   }

   public static double getAmount(double enchantmentsValue, int enchantmentsLimit, double enchantmentsValue3, int enchantmentsValue4, double damageDamageable) {
      return enchantmentsValue + getAmount(enchantmentsLimit) + enchantmentsValue3 + 0.01 * enchantmentsValue4 + 0.001 * damageDamageable;
   }

   private AmountEnchantmentsUtils() {
   }

   public static double getAmount(int enchantmentsValue, double enchantmentsValue2) {
      return 0.01 * enchantmentsValue + 0.001 * enchantmentsValue2;
   }

   public static double getAmount(int enchantmentsValue, float enchantmentsValue2) {
      return enchantmentsValue + enchantmentsValue2;
   }

   public static double getAmount(int enchantmentsValue, float miningSpeedMultiplier, int enchantmentsLimit, int enchantmentsValue4, double enchantmentsValue5, int enchantmentsValue6, double damageDamageable) {
      return getAmount(enchantmentsValue, miningSpeedMultiplier + getAmountForInt(enchantmentsLimit), enchantmentsValue4) + enchantmentsValue5 + 0.01 * enchantmentsValue6 + 0.001 * damageDamageable;
   }

   public static double getMin(int enchantmentsValue) {
      return enchantmentsValue <= 0 ? 1.0 : Math.min(1.0, enchantmentsValue / 131.0);
   }

   public static double getAmount(int enchantmentsValue, double enchantmentsFactor, int enchantmentsValue2) {
      return enchantmentsValue * 100.0 + enchantmentsFactor * getMin(enchantmentsValue2);
   }

   public static double getAmount(
      double enchantmentsValue, double enchantmentsValue2, double enchantmentsValue3, int enchantmentsValue4, int enchantmentsValue5, int enchantmentsValue6, double protectionArmorValue
   ) {
      return getAmount(enchantmentsValue, enchantmentsValue2, enchantmentsValue3) + enchantmentsValue4 + 0.1 * enchantmentsValue5 + 0.01 * enchantmentsValue6 + 0.001 * protectionArmorValue;
   }

   public static double getAmount(int enchantmentsValue, int enchantmentsDivisor) {
      if (enchantmentsDivisor <= 0) {
         return 1.0;
      } else {
         double enchantmentsBound = (double)(enchantmentsDivisor - enchantmentsValue) / enchantmentsDivisor;
         return Math.max(0.0, Math.min(1.0, enchantmentsBound));
      }
   }

   public static double getAmountForInt(int enchantmentsLimit) {
      return enchantmentsLimit > 0 ? (double)enchantmentsLimit * enchantmentsLimit + 1.0 : 0.0;
   }

   public static double getAmount(double enchantmentsValue, double enchantmentsValue2, double enchantmentsValue3) {
      return enchantmentsValue + enchantmentsValue2 + enchantmentsValue3;
   }

   public static double getAmount(int enchantmentsValue, int enchantmentsValue2, int enchantmentsValue3, int enchantmentsValue4, int enchantmentsValue5, int enchantmentsValue6, double damageDamageable) {
      return 2.0 + 0.4 * enchantmentsValue + 0.6 * enchantmentsValue2 + 0.3 * enchantmentsValue3 + 0.1 * enchantmentsValue4 + 0.1 * enchantmentsValue5 + 0.01 * enchantmentsValue6 + 0.001 * damageDamageable;
   }
}
