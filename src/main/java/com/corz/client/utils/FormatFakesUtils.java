package com.corz.client.utils;
import com.corz.client.utils.ElseMaxData;
import com.corz.client.utils.BuyerAppendUtils;
import com.corz.client.utils.DaysHoursUtils;

public final class FormatFakesUtils {
   public static String formatSecondsPrice(ElseMaxData elseMaxData) {
      return elseMaxData == null ? "" : formatText(elseMaxData.price(), elseMaxData.secondsLeft());
   }

   private FormatFakesUtils() {
   }

   public static String formatText(long fakesValue, long fakesBound) {
      long round = Math.round(fakesValue * 3600.0 / Math.max(1L, fakesBound));
      String formatTimesChs = BuyerAppendUtils.formatTimesChs(round, round < 1000000L ? 0 : 2);
      return (formatTimesChs.startsWith("$") ? formatTimesChs.substring(1) : formatTimesChs) + "/h";
   }

   public static long getSecondsLeft(ElseMaxData elseMaxData, long secondsLeftValue, long timeMillis) {
      if (elseMaxData == null) {
         return 0L;
      } else {
         long secondsLeftValue3 = Math.max(0L, timeMillis - secondsLeftValue) / 1000L;
         return Math.max(0L, elseMaxData.secondsLeft() - secondsLeftValue3);
      }
   }

   public static String formatTextForLong(long fakesValue, long currentMillis) {
      if (fakesValue <= 0L) {
         return "never";
      } else {
         long fakesValue3 = Math.max(0L, currentMillis - fakesValue) / 1000L;
         return fakesValue3 < 60L ? "just now" : DaysHoursUtils.formatText(fakesValue3 - fakesValue3 % 60L) + " ago";
      }
   }

   public static String formatPrice(ElseMaxData elseMaxData, long priceValue, long currentMillis) {
      return elseMaxData == null ? "" : formatText(elseMaxData.price(), getSecondsLeft(elseMaxData, priceValue, currentMillis));
   }

   public static float getMax(long fakesValue) {
      return (float)Math.max(0.0, Math.min(1.0, fakesValue / 86400.0));
   }
}
