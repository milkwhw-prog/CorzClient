package com.corz.client.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.CpxVillagerData;

public final class PopsTaken {
   private ScissorTgtHandler.Snapshot menuTurnData;
   public static final String CAME = "CAME ";
   public static final String SEPARATOR = " \u00b7 ";
   public static final String SENT = "SENT ";
   private boolean cpxActive;
   public static final String TGT = "TGT ";
   public static final String ONLINE = "ONLINE";
   public static final String SOON_LABEL = "SOON";
   private String cpxText;
   private String popsTakenText;
   public static final String ELLIPSIS = "...";
   public static final String OFFLINE = "OFFLINE";
   public static final String TAKEN = "TAKEN ";
   public static final String POPS = "POPS ";
   private String villagerCode = "";
   private boolean popsTakenActive;
   private static final Map<String, List<String>> POPS_TAKEN_MAP = Map.of("AutoTpaHere", List.of("SENT ", "CAME "));
   private String onlineOfflineText = "";
   private CpxVillagerData cpxVillagerData;
   private static final List<String> TAKEN_POPS_ITEMS = List.of("POPS ", "TAKEN ");

   public static float getAmount(float popsTakenValue, float widthOffset, float popsTakenValue3) {
      return Math.max(0.0F, popsTakenValue - widthOffset - 6.0F - popsTakenValue3);
   }

   public static List<String> getOrDefault(CpxVillagerData cpxVillagerData) {
      return cpxVillagerData == null ? TAKEN_POPS_ITEMS : POPS_TAKEN_MAP.getOrDefault(cpxVillagerData.id(), TAKEN_POPS_ITEMS);
   }

   public static float getAmount(float popsTakenValue, float widthOffset) {
      return Math.max(0.0F, popsTakenValue - widthOffset - 6.0F - 24.0F);
   }

   public void updateCpxMenu() {
      this.cpxActive = false;
      this.cpxVillagerData = null;
      this.menuTurnData = null;
   }

   public static String formatPopsTaken(CpxVillagerData cpxVillagerData, boolean popsTakenFlag, ScissorTgtHandler.Snapshot snapshot) {
      if (canPlanned(cpxVillagerData, popsTakenFlag, snapshot) && snapshot.isPopsTaken()) {
         List orDefault = getOrDefault(cpxVillagerData);
         String popsTakenText = (String)orDefault.get(0) + snapshot.pops();
         return snapshot.popsTaken() > 0 ? popsTakenText + " \u00b7 " + (String)orDefault.get(1) + snapshot.popsTaken() : popsTakenText;
      } else {
         return "";
      }
   }

   public static String formatTarget(CpxVillagerData cpxVillagerData, boolean targetFlag, ScissorTgtHandler.Snapshot snapshot) {
      return canPlanned(cpxVillagerData, targetFlag, snapshot) && snapshot.isPopsTaken() && snapshot.target() != null ? "TGT " + snapshot.target() : "";
   }

   public boolean isText() {
      return !this.cpxText.isEmpty() || !this.popsTakenText.isEmpty();
   }

   public String getPopsTakenText() {
      return this.popsTakenText;
   }

   public static String formatOnlineOffline(CpxVillagerData cpxVillagerData, boolean onlineOfflineFlag, ScissorTgtHandler.Snapshot snapshot) {
      if (cpxVillagerData == null) {
         return "";
      } else if (cpxVillagerData.planned()) {
         return "SOON";
      } else if (!onlineOfflineFlag) {
         return "OFFLINE";
      } else {
         return snapshot != null ? snapshot.state().toUpperCase(Locale.ROOT) : "ONLINE";
      }
   }

   public String getVillagerCode() {
      return this.villagerCode;
   }

   public PopsTaken() {
      this.cpxText = "";
      this.popsTakenText = "";
   }

   public String getOnlineOfflineText() {
      return this.onlineOfflineText;
   }

   public static String formatWidth(String formatWidth, float widthLimit, PopsTaken.Measure measure) {
      if (formatWidth != null && !formatWidth.isEmpty()) {
         if (measure.width(formatWidth) <= widthLimit) {
            return formatWidth;
         } else {
            String widthText = formatWidth;

            while (widthText.length() > 1 && measure.width(widthText + "...") > widthLimit) {
               widthText = widthText.substring(0, widthText.length() - 1);
            }

            return widthText.stripTrailing() + "...";
         }
      } else {
         return "";
      }
   }

   public String getText() {
      return this.cpxText;
   }

   public boolean canCpxText(CpxVillagerData cpxVillagerData, boolean flag, ScissorTgtHandler.Snapshot menuTurnData) {
      if (this.cpxActive && cpxVillagerData == this.cpxVillagerData && flag == this.popsTakenActive && Objects.equals(menuTurnData, this.menuTurnData)) {
         return false;
      } else {
         this.cpxVillagerData = cpxVillagerData;
         this.popsTakenActive = flag;
         this.menuTurnData = menuTurnData;
         this.cpxActive = true;
         this.villagerCode = cpxVillagerData == null ? "" : cpxVillagerData.code();
         this.onlineOfflineText = formatOnlineOffline(cpxVillagerData, flag, menuTurnData);
         this.cpxText = formatTarget(cpxVillagerData, flag, menuTurnData);
         this.popsTakenText = formatPopsTaken(cpxVillagerData, flag, menuTurnData);
         return true;
      }
   }

   private static boolean canPlanned(CpxVillagerData cpxVillagerData, boolean plannedFlag, ScissorTgtHandler.Snapshot snapshot) {
      return cpxVillagerData != null && !cpxVillagerData.planned() && plannedFlag && snapshot != null;
   }

   public interface Measure {
      float width(String widthText);
   }
}
