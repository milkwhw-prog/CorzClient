package com.corz.client.inventory;

import com.corz.client.world.BlockStateType;

public final class ConstLitematicaUtils {
   public static int getValue(BlockStateType blockStateType) {
      return switch (blockStateType) {
         case MISSING -> 741585894;
         case EXTRA -> 1291799782;
         case WRONG_BLOCK -> 1291793203;
         case WRONG_STATE -> 1291816976;
         case CORRECT, UNKNOWN -> 0;
      };
   }

   private ConstLitematicaUtils() {
   }

   public static float getAmount(BlockStateType blockStateType) {
      int constLitematicaValue = getValue(blockStateType);
      return constLitematicaValue == 0 ? 0.0F : (constLitematicaValue >>> 24 & 0xFF) / 255.0F;
   }

   public static int getValueForBlockStateType(BlockStateType blockStateType) {
      return getValue(blockStateType) & 16777215;
   }
}
