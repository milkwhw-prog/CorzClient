package com.corz.client.utils;

public final class HuntEhpTables {
   public static final int CONST_HUNT_UNIT = 1;
   public static final double[] EHP_BY_STYLE = new double[]{6.0, 12.0, 25.0};
   public static final float[] FIRE_IMMUNE_CAP_BY_STYLE = new float[]{4.0F, 2.5F, 1.0F};
   public static final float[] DAMAGE_MARGIN_BY_LEVEL = new float[]{3.0F, 2.0F, 1.0F};

   public static int clampStyleIndex(int constHuntBound) {
      return constHuntBound < 0 ? 0 : Math.min(2, constHuntBound);
   }

   private HuntEhpTables() {
   }
}
