package com.corz.client.utils;

public final class CardStatusUtilsActive {
   private static volatile CardStatusUtilsActive.Snapshot cardStatusSnapshot = new CardStatusUtilsActive.Snapshot(false, false, 0, 0, Integer.MIN_VALUE, "", 0);

   public static void handle(boolean cardStatusFlag, boolean cardStatusFlag2, int cardStatusBound, int intersectsCenterBound, int cardStatusValue, String cardStatusText, int rocketsBuyBound) {
      int maxActive = Math.max(0, cardStatusBound);
      cardStatusSnapshot = new CardStatusUtilsActive.Snapshot(
         cardStatusFlag, cardStatusFlag2, maxActive, Math.max(0, Math.min(intersectsCenterBound, maxActive)), cardStatusValue, cardStatusText == null ? "" : cardStatusText, Math.max(0, rocketsBuyBound)
      );
   }

   public static int getActiveLayer() {
      return cardStatusSnapshot.activeLayer();
   }

   private CardStatusUtilsActive() {
   }

   public static String formatSection() {
      return cardStatusSnapshot.section();
   }

   public static void update() {
      cardStatusSnapshot = new CardStatusUtilsActive.Snapshot(false, false, 0, 0, Integer.MIN_VALUE, "", 0);
   }

   public static void handle(boolean cardStatusFlag, boolean cardStatusFlag2, int cardStatusValue, int intersectsCenterBound) {
      handle(cardStatusFlag, cardStatusFlag2, cardStatusValue, intersectsCenterBound, Integer.MIN_VALUE, "", 0);
   }

   public static int getPlaced() {
      return cardStatusSnapshot.placed();
   }

   public static int getTotal() {
      return cardStatusSnapshot.total();
   }

   public static float getAmount() {
      return cardStatusSnapshot.getTotalPlaced();
   }

   public static boolean isLoaded() {
      return cardStatusSnapshot.loaded();
   }

   public static int getBlocked() {
      return cardStatusSnapshot.blocked();
   }

   public static boolean isActive() {
      return cardStatusSnapshot.active();
   }

   public static CardStatusUtilsActive.Snapshot getCardStatusSnapshot() {
      return cardStatusSnapshot;
   }

   public static int getValue() {
      return Math.round(getAmount() * 100.0F);
   }

   public record Snapshot(boolean loaded, boolean active, int total, int placed, int activeLayer, String section, int blocked) {
      public int getValue() {
         return Math.round(this.getTotalPlaced() * 100.0F);
      }

      public boolean isLoadedTotal() {
         return this.loaded && this.active && this.total > 0;
      }

      public float getTotalPlaced() {
         return this.total <= 0 ? 0.0F : (float)this.placed / this.total;
      }
   }
}
