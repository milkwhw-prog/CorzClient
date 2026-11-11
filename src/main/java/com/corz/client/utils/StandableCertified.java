package com.corz.client.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

public final class StandableCertified {
   private final LinkedHashMap<Long, StandableCertified.Verdict> constructibleDisconnectedMap = new LinkedHashMap<Long, StandableCertified.Verdict>(
      16, 0.75F, false
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, StandableCertified.Verdict> entry) {
         return this.size() > 256;
      }
   };
   private StandableCertified.Verdict disconnectedConstructibleVer;
   private final int[] disconnectedConstructibleTab = new int[StandableCertified.StandClass.values().length];

   public int getDisconnectedConstructible() {
      return this.constructibleDisconnectedMap.size();
   }

   public int getValue(StandableCertified.StandClass standClass) {
      return standClass == null ? 0 : this.disconnectedConstructibleTab[standClass.ordinal()];
   }

   public static StandableCertified.Verdict getStand(String standText) {
      return new StandableCertified.Verdict(StandableCertified.StandClass.NO_STAND, Long.MIN_VALUE, standText);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "remembered=%d reachable=%d constructible=%d disconnected=%d future=%d none=%d last=%s",
         this.constructibleDisconnectedMap.size(),
         this.getValue(StandableCertified.StandClass.REACHABLE_STAND),
         this.getValue(StandableCertified.StandClass.CONSTRUCTIBLE_STAND),
         this.getValue(StandableCertified.StandClass.DISCONNECTED_STAND),
         this.getValue(StandableCertified.StandClass.FUTURE_STAND),
         this.getValue(StandableCertified.StandClass.NO_STAND),
         this.disconnectedConstructibleVer == null ? "-" : formatNoneStand(this.disconnectedConstructibleVer)
      );
   }

   public StandableCertified.Verdict getDisconnectedConstructibleVer() {
      return this.disconnectedConstructibleVer;
   }

   public static StandableCertified.Verdict getStandableCertified(
      long standableCertifiedValue, boolean standableCertifiedFlag, boolean standableCertifiedFlag2, boolean standableCertifiedFlag3, boolean standableCertifiedFlag4, boolean standableCertifiedFlag5
   ) {
      if (standableCertifiedValue == Long.MIN_VALUE) {
         return getStand("no stand");
      } else if (standableCertifiedFlag && standableCertifiedFlag3) {
         return new StandableCertified.Verdict(StandableCertified.StandClass.REACHABLE_STAND, standableCertifiedValue, "in walk component");
      } else if (standableCertifiedFlag && standableCertifiedFlag4) {
         return new StandableCertified.Verdict(StandableCertified.StandClass.REACHABLE_STAND, standableCertifiedValue, "route certified");
      } else if (standableCertifiedFlag2) {
         return new StandableCertified.Verdict(StandableCertified.StandClass.FUTURE_STAND, standableCertifiedValue, "unbuilt schematic cell");
      } else if (standableCertifiedFlag) {
         return standableCertifiedFlag5
            ? new StandableCertified.Verdict(StandableCertified.StandClass.CONSTRUCTIBLE_STAND, standableCertifiedValue, "standable, constructive plan certified")
            : new StandableCertified.Verdict(StandableCertified.StandClass.DISCONNECTED_STAND, standableCertifiedValue, "standable but disconnected");
      } else {
         return standableCertifiedFlag5
            ? new StandableCertified.Verdict(
               StandableCertified.StandClass.CONSTRUCTIBLE_STAND, standableCertifiedValue, "not standable now, constructive plan certified"
            )
            : getStand("not standable now, no constructive plan");
      }
   }

   public void updateDisconnectedConstructible() {
      this.constructibleDisconnectedMap.clear();
      Arrays.fill(this.disconnectedConstructibleTab, 0);
      this.disconnectedConstructibleVer = null;
   }

   public StandableCertified.Verdict getDisconnectedConstructible(long disconnectedConstructibleVal) {
      return this.constructibleDisconnectedMap.get(disconnectedConstructibleVal);
   }

   public static String formatNoneStand(StandableCertified.Verdict verdict) {
      if (verdict == null) {
         return "NO_STAND NONE (no verdict)";
      } else {
         String noneStandText = verdict.isStand()
            ? ProofSupportUtils.getValueInt(verdict.stand()) + "," + ProofSupportUtils.getValue(verdict.stand()) + "," + ProofSupportUtils.getValueForLong(verdict.stand())
            : "NONE";
         return verdict.cls() + " " + noneStandText + " (" + verdict.why() + ")";
      }
   }

   public StandableCertified.Verdict getDisconnectedConstructible(StandableCertified.Verdict data) {
      if (data == null) {
         return null;
      } else {
         this.disconnectedConstructibleVer = data;
         this.disconnectedConstructibleTab[data.cls().ordinal()]++;
         if (data.isStand()) {
            this.constructibleDisconnectedMap.put(data.stand(), data);
         }

         return data;
      }
   }

   public static enum StandClass {
      REACHABLE_STAND,
      CONSTRUCTIBLE_STAND,
      DISCONNECTED_STAND,
      FUTURE_STAND,
      NO_STAND;
   }

   public record Verdict(StandableCertified.StandClass cls, long stand, String why) {
      public boolean isClsFuture() {
         return this.cls == StandableCertified.StandClass.FUTURE_STAND;
      }

      public boolean isStand() {
         return this.stand != Long.MIN_VALUE;
      }

      public boolean isClsReachable() {
         return this.cls == StandableCertified.StandClass.REACHABLE_STAND;
      }

      public boolean isClsStand() {
         return this.cls == StandableCertified.StandClass.CONSTRUCTIBLE_STAND || this.cls == StandableCertified.StandClass.DISCONNECTED_STAND;
      }

      public Verdict(StandableCertified.StandClass cls, long stand, String why) {
         cls = cls == null ? StandableCertified.StandClass.NO_STAND : cls;
         why = why == null ? "" : why;
         this.cls = cls;
         this.stand = stand;
         this.why = why;
      }
   }
}
