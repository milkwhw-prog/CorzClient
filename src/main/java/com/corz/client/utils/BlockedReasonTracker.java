package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BlockedReasonTracker {
   private int firstSeenTick;
   private int updateCount;
   private String currentReason;
   private int sameTextRepeatCount;
   public static final String MISSING_MATERIAL = "MISSING_MATERIAL";
   public static final String WAITING_SUPPORT = "WAITING_SUPPORT";
   public static final String NO_ACCESS_ROUTE = "NO_ACCESS_ROUTE";
   public static final String DIRT_LOOP_BAN = "DIRT_LOOP_BAN";
   private final ArrayList<Long> sampledPositionKeys = new ArrayList<>();
   public static final String POSE_BLOCKED = "POSE_BLOCKED";
   public static final String ROI_REFUSED = "ROI_REFUSED";
   public static final String NO_STAND = "NO_STAND";
   public static final String TEMP_DEBT_NO_RECYCLE = "TEMP_DEBT_NO_RECYCLE";
   public static final String ISOLATED_AFTER_ACCESS = "ISOLATED_AFTER_ACCESS";
   public static final String OTHER_LABEL = "OTHER";

   public void recordReason(String text, int value, long accessIsolatedValue) {
      if (text.equals(this.currentReason)) {
         this.sameTextRepeatCount++;
      } else {
         this.currentReason = text;
         this.sameTextRepeatCount = 1;
         this.firstSeenTick = value;
         this.sampledPositionKeys.clear();
      }

      if (accessIsolatedValue != 0L && this.sampledPositionKeys.size() < 16 && !this.sampledPositionKeys.contains(accessIsolatedValue)) {
         this.sampledPositionKeys.add(accessIsolatedValue);
      }
   }

   public String getStableReason(int accessIsolatedValue, int accessIsolatedValue2) {
      if (this.currentReason != null && this.sameTextRepeatCount >= 6) {
         return accessIsolatedValue - accessIsolatedValue2 < 200 ? null : this.currentReason;
      } else {
         return null;
      }
   }

   public int getUpdateCount() {
      return this.updateCount;
   }

   public void clearReason() {
      this.currentReason = null;
      this.sameTextRepeatCount = 0;
      this.sampledPositionKeys.clear();
   }

   public void markProgress() {
      this.updateCount++;
      this.sameTextRepeatCount = 0;
      this.sampledPositionKeys.clear();
   }

   public List<Long> getSampledPositions() {
      return List.copyOf(this.sampledPositionKeys);
   }

   public int getFirstSeenTick() {
      return this.firstSeenTick;
   }

   public void handle(String accessIsolatedText, int accessIsolatedValue) {
      this.recordReason(accessIsolatedText, accessIsolatedValue, 0L);
   }

   public int getRepeatCount() {
      return this.sameTextRepeatCount;
   }

   public static String classifyReason(String accessIsolatedText) {
      String accessIsolatedText2 = accessIsolatedText == null ? "" : accessIsolatedText.toLowerCase(Locale.ROOT);
      if (accessIsolatedText2.contains("temp cap") || accessIsolatedText2.contains("nothing recyclable") || accessIsolatedText2.contains("temp debt")) {
         return "TEMP_DEBT_NO_RECYCLE";
      } else if (accessIsolatedText2.contains("dirt-loop")) {
         return "DIRT_LOOP_BAN";
      } else if (accessIsolatedText2.contains("relocation roi")) {
         return "ROI_REFUSED";
      } else if (accessIsolatedText2.contains("isolated after access") || accessIsolatedText2.contains("marooned")) {
         return "ISOLATED_AFTER_ACCESS";
      } else if (accessIsolatedText2.contains("no path") || accessIsolatedText2.contains("column") || accessIsolatedText2.contains("access")) {
         return "NO_ACCESS_ROUTE";
      } else if (accessIsolatedText2.contains("aim") || accessIsolatedText2.contains("pose") || accessIsolatedText2.contains("facing")) {
         return "POSE_BLOCKED";
      } else if (accessIsolatedText2.contains("stand")) {
         return "NO_STAND";
      } else if (accessIsolatedText2.contains("support") || accessIsolatedText2.contains("containment")) {
         return "WAITING_SUPPORT";
      } else {
         return accessIsolatedText2.contains("material") ? "MISSING_MATERIAL" : "OTHER";
      }
   }

   public String getCurrentReason() {
      return this.currentReason;
   }
}
