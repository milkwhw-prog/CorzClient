package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public final class TextMutableUtils {
   private static final String[] SUFFIX_LOWER = new String[]{"qt", "q", "t", "b", "m", "k"};
   private static final String[] SUFFIX_DISPLAY = new String[]{"QT", "Q", "T", "B", "M", "k"};
   private static final int[] SUFFIX_EXPONENTS = new int[]{18, 15, 12, 9, 6, 3};
   private static final Pattern NUMBER_WITH_SUFFIX_PATTERN = Pattern.compile("\\d[\\d.,]*(?:\\s?(?:[qQ][tT]|[kKmMbBtTqQ]))?");

   public static int[] findFirstDigitRange(String mutableText) {
      for (int i = 0; i < mutableText.length(); i++) {
         if (Character.isDigit(mutableText.charAt(i))) {
            return new int[]{i, mutableText.length()};
         }
      }

      return null;
   }

   public static String formatAbbreviatedNumber(double mutableValue) {
      double mutableLimit = Math.abs(mutableValue);

      for (int i = 0; i < SUFFIX_EXPONENTS.length; i++) {
         double pow = Math.pow(10.0, SUFFIX_EXPONENTS[i]);
         if (mutableLimit >= pow) {
            double roundScaled = Math.round(mutableValue / pow * 10.0) / 10.0;
            return formatWholeOrDecimal(roundScaled) + SUFFIX_DISPLAY[i];
         }
      }

      return Long.toString(Math.round(mutableValue));
   }

   private TextMutableUtils() {
   }

   private static Text replaceNumberInText(Text literalAppend, String literalAppendText, boolean literalAppendFlag) {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      literalAppend.visit((style, str) -> {
         arrayList.add(style);
         arrayList2.add(str);
         return Optional.empty();
      }, Style.EMPTY);
      StringBuilder stringBuilder = new StringBuilder();

      for (String builderText : (Iterable<java.lang.String>) (arrayList2)) {
         stringBuilder.append(builderText);
      }

      int[] literalAppendValues = literalAppendFlag ? findFirstDigitRange(stringBuilder.toString()) : findLastNumberRange(stringBuilder.toString());
      if (literalAppendValues == null) {
         return literalAppend;
      } else {
         int literalAppendBound = literalAppendValues[0];
         int rocketsBuyBound = literalAppendValues[1];
         MutableText empty = Text.empty();
         int aggroPreemptBound = 0;
         boolean literalAppendFlag2 = false;

         for (int i = 0; i < arrayList2.size(); i++) {
            String literalAppendText3 = (String)arrayList2.get(i);
            Style style2 = (Style)arrayList.get(i);
            int literalAppendLimit = aggroPreemptBound + literalAppendText3.length();
            int min2 = Math.min(literalAppendLimit, literalAppendBound);
            if (min2 > aggroPreemptBound) {
               empty.append(Text.literal(literalAppendText3.substring(0, min2 - aggroPreemptBound)).setStyle(style2));
            }

            if (!literalAppendFlag2 && literalAppendBound >= aggroPreemptBound && literalAppendBound < literalAppendLimit) {
               empty.append(Text.literal(literalAppendText).setStyle(style2));
               literalAppendFlag2 = true;
            }

            int literalAppendValue = Math.max(aggroPreemptBound, rocketsBuyBound);
            if (literalAppendLimit > literalAppendValue) {
               empty.append(Text.literal(literalAppendText3.substring(literalAppendValue - aggroPreemptBound)).setStyle(style2));
            }

            aggroPreemptBound = literalAppendLimit;
         }

         if (!literalAppendFlag2) {
            empty.append(Text.literal(literalAppendText));
         }

         return empty;
      }
   }

   private static String formatWholeOrDecimal(double mutableValue) {
      return mutableValue == Math.floor(mutableValue) && !Double.isInfinite(mutableValue) ? Long.toString((long)mutableValue) : String.valueOf(mutableValue);
   }

   public static String formatFlooredLong(double mutableValue) {
      return Long.toString((long)Math.floor(mutableValue));
   }

   public static Text replaceFromFirstDigit(Text text, String mutableText) {
      return replaceNumberInText(text, mutableText, true);
   }

   public static double parseAbbreviatedNumber(String doubleText) {
      if (doubleText == null) {
         return Double.NaN;
      } else {
         String replaceAll = doubleText.trim().toLowerCase(Locale.ROOT).replaceAll("[,\\s_]", "");

         while (!replaceAll.isEmpty() && (replaceAll.charAt(0) == '$' || replaceAll.charAt(0) == 8364 || replaceAll.charAt(0) == 163)) {
            replaceAll = replaceAll.substring(1);
         }

         if (replaceAll.isEmpty()) {
            return Double.NaN;
         } else {
            for (int i = 0; i < SUFFIX_LOWER.length; i++) {
               if (replaceAll.endsWith(SUFFIX_LOWER[i]) && replaceAll.length() > SUFFIX_LOWER[i].length()) {
                  String substring = replaceAll.substring(0, replaceAll.length() - SUFFIX_LOWER[i].length());

                  try {
                     return Double.parseDouble(substring) * Math.pow(10.0, SUFFIX_EXPONENTS[i]);
                  } catch (NumberFormatException e) {
                     return Double.NaN;
                  }
               }
            }

            try {
               return Double.parseDouble(replaceAll);
            } catch (NumberFormatException e2) {
               return Double.NaN;
            }
         }
      }
   }

   public static int[] findLastNumberRange(String compileText) {
      Matcher matcher = NUMBER_WITH_SUFFIX_PATTERN.matcher(compileText);
      int compileValue = -1;

      int compileValue2;
      for (compileValue2 = -1; matcher.find(); compileValue2 = matcher.end()) {
         compileValue = matcher.start();
      }

      return compileValue < 0 ? null : new int[]{compileValue, compileValue2};
   }

   public static Text replaceLastNumber(Text text, String mutableText) {
      return replaceNumberInText(text, mutableText, false);
   }
}
