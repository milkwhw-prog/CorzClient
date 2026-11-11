package com.corz.client.utils;

public final class ModelQuadUtils {
   public static final float[] modelQuadTable = new float[]{0.58F, 0.72F, 0.86F, 1.0F};
   private static final float[] table = new float[]{0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};

   public static float getAmounts(int index) {
      return index >= 0 && index < table.length ? table[index] : 1.0F;
   }

   public static int getValue(float modelQuadValue) {
      return modelQuadValue < 0.5F ? -1 : 1;
   }

   public static float getAmountsForInt(int modelQuadLimit) {
      return modelQuadTable[modelQuadLimit < 0 ? 0 : (modelQuadLimit > 3 ? 3 : modelQuadLimit)];
   }

   public static boolean canFlag(int modelQuadValue, int modelQuadValue2, int modelQuadValue3, int modelQuadValue4) {
      return modelQuadValue + modelQuadValue3 < modelQuadValue2 + modelQuadValue4;
   }

   private ModelQuadUtils() {
   }

   private static int getValue(int modelQuadFactor, float modelQuadFactor2) {
      int modelQuadLimit = Math.round(modelQuadFactor * modelQuadFactor2);
      return modelQuadLimit < 0 ? 0 : (modelQuadLimit > 255 ? 255 : modelQuadLimit);
   }

   public static int getValueForIntFloat(int modelQuadValue, float modelQuadValue2) {
      int modelQuadValue3 = getValue(modelQuadValue >> 16 & 0xFF, modelQuadValue2);
      int modelQuadValue4 = getValue(modelQuadValue >> 8 & 0xFF, modelQuadValue2);
      int modelQuadValue5 = getValue(modelQuadValue & 0xFF, modelQuadValue2);
      return modelQuadValue3 << 16 | modelQuadValue4 << 8 | modelQuadValue5;
   }

   public static int getValue(boolean modelQuadFlag, boolean canCubeFull, boolean modelQuadFlag3) {
      return modelQuadFlag && canCubeFull ? 0 : 3 - (modelQuadFlag ? 1 : 0) - (canCubeFull ? 1 : 0) - (modelQuadFlag3 ? 1 : 0);
   }
}
