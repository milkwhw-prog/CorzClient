package com.corz.client.utils;

public record EyeStandsData(long feet, double eyeX, double eyeY, double eyeZ, boolean sneaking, long component, int reachableStands, Long surfaceCell, int tick) {
   public static final EyeStandsData INSTANCE = new EyeStandsData(0L, 0.0, 0.0, 0.0, false, 0L, 0, null, -1);

   public int getFeet() {
      return ProofSupportUtils.getValue(this.feet);
   }
}
