package com.corz.client.utils;

import java.util.List;

public final class EvidenceDug {
   public final String evidenceDugText;
   public final long foundTimeMs;
   public final double basePlayerAmount;
   public final int storage;
   public final int evidenceScore;
   public final double finderBaseAmount;
   public final long basePlayerTime;
   public final int dug;
   public final String detailText;
   public final int releaseRightValue;
   public final String playerIntegerText;
   public final double finderChunkAmount;
   public final int evidenceDugValue;
   public final int farmCount;
   public final int evidence;
   public final int highCount;
   public final int lights;
   public final List<Long> evidenceDugPositionList;
   public final boolean evidenceDugActive;
   public final int seenRememberedValue;
   public final int storageScore;
   public final int finderValue;
   public final int util;
   public final int redstoneScore;
   public final int utilityScore;
   public final int redstone;
   public final int crafted;
   public final int high;
   public final int farm;

   public String formatHighLikely() {
      if (this.high >= 75) {
         return "high";
      } else {
         return this.high >= 60 ? "likely" : "suspicious";
      }
   }

   public String formatEvidenceRemembered() {
      StringBuilder stringBuilder = new StringBuilder();
      handle(stringBuilder, "storage", this.storage);
      handle(stringBuilder, "util", this.util);
      handle(stringBuilder, "lights", this.lights);
      handle(stringBuilder, "redstone", this.redstone);
      handle(stringBuilder, "crafted", this.crafted);
      handle(stringBuilder, "farm", this.farm);
      handle(stringBuilder, "dug", this.dug);
      if (stringBuilder.length() == 0) {
         stringBuilder.append("evidence");
      }

      if (this.evidence > 0) {
         stringBuilder.append(" \u00b7 remembered ").append(this.evidence);
      }

      if (this.seenRememberedValue < 100) {
         stringBuilder.append(" \u00b7 seen ").append(this.seenRememberedValue).append('%');
      }

      return stringBuilder.toString();
   }

   public EvidenceDug(
      long evidenceDugValue,
      String evidenceDugText,
      List<Long> list,
      int evidenceDugValue2,
      int evidenceDugValue3,
      int evidenceDugValue4,
      int evidenceDugValue5,
      int evidenceDugValue6,
      int evidenceDugValue7,
      int evidenceDugValue8,
      int evidenceDugValue9,
      int evidenceDugValue10,
      int evidenceDugValue11,
      int evidenceDugValue12,
      int evidenceDugValue13,
      int evidenceDugValue14,
      int evidenceDugValue15,
      int evidenceDugValue16,
      int evidenceDugValue17,
      int evidenceDugValue18,
      int evidenceDugValue19,
      int evidenceDugValue20,
      String evidenceDugText2,
      String evidenceDugText3,
      long evidenceDugValue21
   ) {
      this(
         evidenceDugValue,
         evidenceDugText,
         list,
         evidenceDugValue2,
         evidenceDugValue3,
         evidenceDugValue4,
         evidenceDugValue5,
         evidenceDugValue6,
         evidenceDugValue7,
         evidenceDugValue8,
         evidenceDugValue9,
         evidenceDugValue10,
         evidenceDugValue11,
         evidenceDugValue12,
         evidenceDugValue13,
         evidenceDugValue14,
         evidenceDugValue15,
         evidenceDugValue16,
         evidenceDugValue17,
         evidenceDugValue18,
         evidenceDugValue19,
         evidenceDugValue20,
         false,
         evidenceDugText2,
         evidenceDugText3,
         evidenceDugValue21
      );
   }

   private static void handle(StringBuilder stringBuilder, String evidenceDugText, int evidenceDugLimit) {
      if (evidenceDugLimit > 0) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append(" \u00b7 ");
         }

         stringBuilder.append(evidenceDugText).append(' ').append(evidenceDugLimit);
      }
   }

   public boolean isEvidenceRemembered() {
      return this.seenRememberedValue < 100 || this.evidence > 0;
   }

   public EvidenceDug(
      long basePlayerTime,
      String text,
      List<Long> items,
      int value,
      int value2,
      int value3,
      int value4,
      int value5,
      int value6,
      int high,
      int value7,
      int storage2,
      int util,
      int lights,
      int redstone2,
      int crafted2,
      int farm2,
      int dug,
      int value8,
      int value9,
      int evidence,
      int seenRememberedValue,
      boolean flag,
      String text2,
      String text3,
      long time
   ) {
      this.basePlayerTime = basePlayerTime;
      this.playerIntegerText = text;
      this.evidenceDugPositionList = items;
      this.storageScore = value;
      this.releaseRightValue = value2;
      this.utilityScore = value3;
      this.evidenceDugValue = value4;
      this.finderValue = value5;
      this.redstoneScore = value6;
      this.basePlayerAmount = (value + value4 + 1) / 2.0;
      this.finderChunkAmount = (value2 + value5 + 1) / 2.0;
      this.finderBaseAmount = (value3 + value6 + 1) / 2.0;
      this.high = high;
      this.evidenceScore = value7;
      this.storage = storage2;
      this.util = util;
      this.lights = lights;
      this.redstone = redstone2;
      this.crafted = crafted2;
      this.farm = farm2;
      this.dug = dug;
      this.highCount = value8;
      this.farmCount = value9;
      this.evidence = evidence;
      this.seenRememberedValue = seenRememberedValue;
      this.evidenceDugActive = flag;
      this.detailText = text2;
      this.evidenceDugText = text3;
      this.foundTimeMs = time;
   }
}
