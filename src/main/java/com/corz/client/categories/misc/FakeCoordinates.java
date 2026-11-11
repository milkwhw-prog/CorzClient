package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class FakeCoordinates extends Module {
   static final Pattern compile = Pattern.compile(
      "(?:(?<=\u00a7[0-9a-fk-orA-FK-OR])|(?<![\\p{L}\\p{N}\\p{Sc}_.\\-]))(-?\\d{1,8}(?:\\.\\d+)?)(,\\s+|\\s*/\\s*|\\s+)(-?\\d{1,8}(?:\\.\\d+)?)\\2(-?\\d{1,8}(?:\\.\\d+)?)(?![\\p{L}\\p{N}\\p{Sc}_%]|\\.\\d)"
   );
   static final Pattern fakeCoordinatesPattern = Pattern.compile(
      "(?:(?<=\u00a7[0-9a-fk-orA-FK-OR])|(?<![\\p{L}\\p{N}_]))([XxZz])(?:\\s*[:=]|(?<=[XZ])(?=\\s|\u00a7[0-9a-fk-orA-FK-OR]))(?:\\s|\u00a7[0-9a-fk-orA-FK-OR])*(-?\\d{1,8}(?:\\.\\d+)?)(?![\\p{L}\\p{N}\\p{Sc}_%]|\\.\\d)"
   );
   private int offsetZ;
   private static final String NUMBER_END_LOOKAHEAD = "(?![\\p{L}\\p{N}\\p{Sc}_%]|\\.\\d)";
   private static final String NUMBER_START_LOOKBEHIND = "(?:(?<=\u00a7[0-9a-fk-orA-FK-OR])|(?<![\\p{L}\\p{N}\\p{Sc}_.\\-]))";
   private static final String NUMBER_PATTERN = "(-?\\d{1,8}(?:\\.\\d+)?)";
   static final Pattern pattern = Pattern.compile(
      "(?:(?<=\u00a7[0-9a-fk-orA-FK-OR])|(?<![\\p{L}\\p{N}\\p{Sc}_.,:\\-]))(?<!\\d,\\s{1,3})(-?\\d{1,8}),\\s+(-?\\d{1,8})(?![\\p{L}\\p{N}\\p{Sc}_%:]|\\.\\d|,\\s*-?\\d)"
   );
   private double z;
   private ClientPlayNetworkHandler networkHandler;
   private int networkValue;
   private double x;
   private static FakeCoordinates singleton;
   private final BooleanSetting booleanBase = new BooleanSetting("Hide Biome", "Also hides the biome line on the F3 screen", true);
   private static final String COLOR_CODE_PATTERN = "\u00a7[0-9a-fk-orA-FK-OR]";
   private static final SecureRandom SECURE_RANDOM = new SecureRandom();

   static String formatText(String formatText, int fakeCoordinatesValue, int fakeCoordinatesValue2) {
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      handleCompile(formatText, fakeCoordinatesValue, fakeCoordinatesValue2, arrayList, arrayList2);
      if (arrayList.isEmpty()) {
         return formatText;
      } else {
         StringBuilder stringBuilder = new StringBuilder(formatText.length() + 8);
         int fakeCoordinatesValue3 = 0;

         for (int i = 0; i < arrayList.size(); i++) {
            int[] fakeCoordinatesValues = (int[])arrayList.get(i);
            stringBuilder.append(formatText, fakeCoordinatesValue3, fakeCoordinatesValues[0]).append((String)arrayList2.get(i));
            fakeCoordinatesValue3 = fakeCoordinatesValues[1];
         }

         return stringBuilder.append(formatText, fakeCoordinatesValue3, formatText.length()).toString();
      }
   }

   static String formatText(String formatText, int fakeCoordinatesValue) {
      try {
         return new BigDecimal(formatText).add(BigDecimal.valueOf((long)fakeCoordinatesValue)).toPlainString();
      } catch (NumberFormatException e) {
         return formatText;
      }
   }

   public static boolean isBaseInstance() {
      return isInstance() && singleton.booleanBase.isEnabled();
   }

   public static BlockPos getAdd(BlockPos pos) {
      return pos != null && isInstance() ? pos.add(singleton.networkValue, 0, singleton.offsetZ) : pos;
   }

   public static String formatInstance(String fakeCoordinatesText) {
      return fakeCoordinatesText != null && !fakeCoordinatesText.isEmpty() && isInstance() ? formatText(fakeCoordinatesText, singleton.networkValue, singleton.offsetZ) : fakeCoordinatesText;
   }

   private static Style getHoverEvent(Style hoverEvent, int hoverEventValue, int fakeCoordinatesValue) {
      if (hoverEvent.getHoverEvent() instanceof ShowText showText && showText.value() != null) {
         Text visitEmpty = getVisitEmpty(showText.value(), hoverEventValue, fakeCoordinatesValue);
         return visitEmpty == showText.value() ? hoverEvent : hoverEvent.withHoverEvent(new ShowText(visitEmpty));
      } else {
         return hoverEvent;
      }
   }

   @Override
   protected void onReset() {
      this.isNetworkHandler();
   }

   public static double getZ(double fakeCoordinatesValue) {
      return isInstance() ? fakeCoordinatesValue + singleton.offsetZ : fakeCoordinatesValue;
   }

   public static String formatInstanceForString(String fakeCoordinatesText) {
      return fakeCoordinatesText != null && !fakeCoordinatesText.isEmpty() && isInstance() ? formatSummonSetblock(fakeCoordinatesText, singleton.networkValue, singleton.offsetZ) : fakeCoordinatesText;
   }

   static String formatSummonSetblock(String summonSetblockText, int summonSetblockValue, int fakeCoordinatesValue) {
      String[] split = summonSetblockText.split(" ", -1);
      byte index = -1;
      if (split.length >= 9 && split[0].equals("/execute") && split[1].equals("in") && split[3].equals("run") && split[4].equals("tp") && split[5].equals("@s")) {
         index = 6;
      } else if (split.length >= 4 && split[0].equals("/setblock")) {
         index = 1;
      } else if (split.length >= 5 && split[0].equals("/summon")) {
         index = 2;
      }

      if (index < 0) {
         return formatText(summonSetblockText, summonSetblockValue, fakeCoordinatesValue);
      } else {
         split[index] = formatText(split[index], summonSetblockValue);
         split[index + 2] = formatText(split[index + 2], fakeCoordinatesValue);
         return String.join(" ", split);
      }
   }

   private static boolean canFlag(List<int[]> list, int fakeCoordinatesValue, int fakeCoordinatesLimit) {
      for (int[] fakeCoordinatesValues : list) {
         if (fakeCoordinatesValue < fakeCoordinatesValues[1] && fakeCoordinatesValues[0] < fakeCoordinatesLimit) {
            return true;
         }
      }

      return false;
   }

   static int[] getValues(double fakeCoordinatesValue, double fakeCoordinatesValue2, Random random) {
      int fakeCoordinatesValue3 = 0;
      int j = 0;

      for (int i = 0; i < 32; i++) {
         fakeCoordinatesValue3 = getValue(random.nextInt(48001) - 24000 - fakeCoordinatesValue);
         j = getValue(random.nextInt(48001) - 24000 - fakeCoordinatesValue2);
         if (Math.abs(fakeCoordinatesValue3) >= 1024 && Math.abs(j) >= 1024) {
            break;
         }
      }

      if (Math.abs(fakeCoordinatesValue3) < 1024) {
         fakeCoordinatesValue3 = fakeCoordinatesValue3 < 0 ? fakeCoordinatesValue3 - 1024 : fakeCoordinatesValue3 + 1024;
      }

      if (Math.abs(j) < 1024) {
         j = j < 0 ? j - 1024 : j + 1024;
      }

      return new int[]{fakeCoordinatesValue3, j};
   }

   public static boolean isInstance() {
      FakeCoordinates singleton2 = singleton;
      return singleton2 != null && singleton2.isEnabled() && singleton2.isNetworkHandler();
   }

   public static Text getLiteral(Text text) {
      return text != null && isInstance() ? getVisitEmpty(text, singleton.networkValue, singleton.offsetZ) : text;
   }

   public static int getInstance(int fakeCoordinatesValue) {
      return isInstance() ? fakeCoordinatesValue + singleton.networkValue : fakeCoordinatesValue;
   }

   static void handleCompile(String compileText, int compileValue, int fakeCoordinatesValue, List<int[]> list, List<String> list2) {
      if (compileText != null && !compileText.isEmpty()) {
         var arrayList = new ArrayList();
         var arrayList2 = new ArrayList();
         Matcher matcher = compile.matcher(compileText);
         int end3 = 0;

         while (end3 < compileText.length() && matcher.find(end3)) {
            if (!canFlag(matcher.group(3))) {
               end3 = matcher.end(1);
            } else {
               arrayList.add(new int[]{matcher.start(1), matcher.end(1)});
               arrayList2.add(formatText(matcher.group(1), compileValue));
               arrayList.add(new int[]{matcher.start(4), matcher.end(4)});
               arrayList2.add(formatText(matcher.group(4), fakeCoordinatesValue));
               end3 = matcher.end();
            }
         }

         Matcher matcher2 = fakeCoordinatesPattern.matcher(compileText);

         while (matcher2.find()) {
            int start = matcher2.start(2);
            int end = matcher2.end(2);
            if (!canFlag(arrayList, start, end)) {
               char lowerCase = Character.toLowerCase(matcher2.group(1).charAt(0));
               arrayList.add(new int[]{start, end});
               arrayList2.add(formatText(matcher2.group(2), lowerCase == 'x' ? compileValue : fakeCoordinatesValue));
            }
         }

         Matcher matcher3 = pattern.matcher(compileText);

         while (matcher3.find()) {
            if (!canFlag(arrayList, matcher3.start(1), matcher3.end(2))) {
               arrayList.add(new int[]{matcher3.start(1), matcher3.end(1)});
               arrayList2.add(formatText(matcher3.group(1), compileValue));
               arrayList.add(new int[]{matcher3.start(2), matcher3.end(2)});
               arrayList2.add(formatText(matcher3.group(2), fakeCoordinatesValue));
            }
         }

         Integer[] integers = new Integer[arrayList.size()];

         for (int i = 0; i < integers.length; i++) {
            integers[i] = i;
         }

         Arrays.sort(integers, (found, integer) -> Integer.compare(((int[])arrayList.get(found))[0], ((int[])arrayList.get(integer))[0]));

         for (Integer integersItem : integers) {
            list.add((int[])arrayList.get(integersItem));
            list2.add((String)arrayList2.get(integersItem));
         }
      }
   }

   static int getValue(double fakeCoordinatesValue) {
      return (int)Math.floor(fakeCoordinatesValue / 16.0) * 16;
   }

   public FakeCoordinates() {
      super("Fake Coordinates", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.booleanBase});
      singleton = this;
   }

   public static double getX(double fakeCoordinatesValue) {
      return isInstance() ? fakeCoordinatesValue + singleton.networkValue : fakeCoordinatesValue;
   }

   static Text getVisitEmpty(Text visitEmpty, int visitEmptyValue, int fakeCoordinatesValue) {
      String visitEmptyText = visitEmpty.getString();
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      handleCompile(visitEmptyText, visitEmptyValue, fakeCoordinatesValue, arrayList, arrayList2);
      var arrayList3 = new ArrayList();
      var arrayList4 = new ArrayList();
      boolean[] booleans = new boolean[]{false};
      visitEmpty.visit((hovers, parts) -> {
         arrayList3.add(hovers);
         arrayList4.add(parts);
         if (hovers.getHoverEvent() instanceof ShowText) {
            booleans[0] = true;
         }

         return Optional.empty();
      }, Style.EMPTY);
      if (arrayList.isEmpty() && !booleans[0]) {
         return visitEmpty;
      } else {
         MutableText empty = Text.empty();
         int visitEmptyLimit3 = 0;
         int visitEmptyCounter = 0;
         boolean visitEmptyFlag = false;

         for (int i = 0; i < arrayList4.size(); i++) {
            String visitEmptyText2 = (String)arrayList4.get(i);
            int visitEmptyLimit = visitEmptyLimit3 + visitEmptyText2.length();
            StringBuilder stringBuilder = null;

            int visitEmptyValue4;
            for (visitEmptyValue4 = 0; visitEmptyCounter < arrayList.size() && ((int[])arrayList.get(visitEmptyCounter))[0] < visitEmptyLimit; visitEmptyCounter++) {
               int[] visitEmptyValues = (int[])arrayList.get(visitEmptyCounter);
               if (visitEmptyValues[0] >= visitEmptyLimit3 && visitEmptyValues[1] <= visitEmptyLimit) {
                  if (stringBuilder == null) {
                     stringBuilder = new StringBuilder(visitEmptyText2.length() + 8);
                  }

                  stringBuilder.append(visitEmptyText2, visitEmptyValue4, visitEmptyValues[0] - visitEmptyLimit3).append((String)arrayList2.get(visitEmptyCounter));
                  visitEmptyValue4 = visitEmptyValues[1] - visitEmptyLimit3;
               }
            }

            if (stringBuilder != null) {
               stringBuilder.append(visitEmptyText2, visitEmptyValue4, visitEmptyText2.length());
               visitEmptyText2 = stringBuilder.toString();
               visitEmptyFlag = true;
            }

            Style style = (Style)arrayList3.get(i);
            Style style2 = booleans[0] ? getHoverEvent(style, visitEmptyValue, fakeCoordinatesValue) : style;
            if (style2 != style) {
               visitEmptyFlag = true;
            }

            empty.append(Text.literal(visitEmptyText2).setStyle(style2));
            visitEmptyLimit3 = visitEmptyLimit;
         }

         return (Text)(visitEmptyFlag ? empty : visitEmpty);
      }
   }

   private boolean isNetworkHandler() {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler == null) {
         return false;
      } else {
         ClientPlayerEntity player = mc.player;
         if (player == null) {
            return networkHandler == this.networkHandler;
         } else {
            double x = player.getX();
            double z = player.getZ();
            if (networkHandler != this.networkHandler || canFlag(this.x, this.z, x, z)) {
               this.networkHandler = networkHandler;
               int[] networkValues = getValues(x, z, SECURE_RANDOM);
               this.networkValue = networkValues[0];
               this.offsetZ = networkValues[1];
            }

            this.x = x;
            this.z = z;
            return true;
         }
      }
   }

   static boolean canFlag(double fakeCoordinatesValue, double fakeCoordinatesValue2, double fakeCoordinatesValue3, double fakeCoordinatesValue4) {
      double fakeCoordinatesFactor = fakeCoordinatesValue3 - fakeCoordinatesValue;
      double fakeCoordinatesFactor2 = fakeCoordinatesValue4 - fakeCoordinatesValue2;
      return fakeCoordinatesFactor * fakeCoordinatesFactor + fakeCoordinatesFactor2 * fakeCoordinatesFactor2 > 65536.0;
   }

   public static int getInstanceForInt(int fakeCoordinatesValue) {
      return isInstance() ? fakeCoordinatesValue + singleton.offsetZ : fakeCoordinatesValue;
   }

   private static boolean canFlag(String fakeCoordinatesText) {
      try {
         double double2 = Double.parseDouble(fakeCoordinatesText);
         return double2 >= -64.0 && double2 <= 320.0;
      } catch (NumberFormatException e) {
         return false;
      }
   }
}
