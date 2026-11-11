package com.corz.client.utils;

import com.corz.client.inventory.ConstLitematicaUtils;
import com.corz.client.render.OpacityColorData;
import com.corz.client.world.BlockStateType;

public enum LitematicaInteriorType {
   BALANCED("Balanced", 0.5F, 0.12F, 0.22F, 0.2F, 0.07F, 2.0F, false, 0.0F),
   BUILDING("Building", 0.5F, 0.12F, 0.24F, 0.22F, 0.07F, 2.0F, false, 0.0F),
   STRUCTURE("Structure", 0.28F, 0.1F, 0.18F, 0.16F, 0.05F, 1.5F, false, 0.9F),
   INTERIOR("Interior", 0.45F, 0.12F, 0.24F, 0.22F, 0.05F, 4.0F, false, 0.9F),
   LITEMATICA(
      "Litematica",
      0.5F,
      ConstLitematicaUtils.getAmount(BlockStateType.MISSING),
      ConstLitematicaUtils.getAmount(BlockStateType.WRONG_BLOCK),
      ConstLitematicaUtils.getAmount(BlockStateType.WRONG_STATE),
      0.2F,
      2.0F,
      false,
      1.0F
   ),
   CUSTOM("Custom", -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, false, 0.0F);

   public final float baseFraction;
   public final float maximumAmount;
   public final float missingAmount;
   public final boolean missingActive;
   public final String presetOriginText;
   public final float litematicaInteriorAmount;
   public final float missingBaseAmount;
   public final float missingPeakAmount;
   public final float missingFloorAmount;

   public OpacityColorData.Style getLitematicaCorz() {
      return this == LITEMATICA ? OpacityColorData.Style.LITEMATICA : OpacityColorData.Style.CORZ;
   }

   public boolean isCustom() {
      return this != CUSTOM;
   }

   private LitematicaInteriorType(
      String presetOriginText,
      float maximumAmount,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      float amount5,
      boolean flag2,
      float amount6
   ) {
      this.presetOriginText = presetOriginText;
      this.maximumAmount = maximumAmount;
      this.litematicaInteriorAmount = amount;
      this.baseFraction = amount2;
      this.missingFloorAmount = amount3;
      this.missingBaseAmount = amount4;
      this.missingAmount = amount5;
      this.missingActive = flag2;
      this.missingPeakAmount = amount6;
   }

   public static String[] getPresetOrigin() {
      LitematicaInteriorType[] litematicaInteriorTypes = values();
      String[] strings = new String[litematicaInteriorTypes.length];

      for (int i = 0; i < litematicaInteriorTypes.length; i++) {
         strings[i] = litematicaInteriorTypes[i].presetOriginText;
      }

      return strings;
   }

   public static LitematicaInteriorType getPresetBuilding(String presetBuildingText) {
      for (LitematicaInteriorType presetBuilding : values()) {
         if (presetBuilding.presetOriginText.equals(presetBuildingText)) {
            return presetBuilding;
         }
      }

      return BUILDING;
   }
}
