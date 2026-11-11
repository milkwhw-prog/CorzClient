package com.corz.client.render;

import com.corz.client.inventory.ConstLitematicaUtils;
import com.corz.client.world.BlockStateType;

public record OpacityColorData(
   OpacityColorData.Mode mode,
   OpacityColorData.InteriorClarity interiorClarity,
   OpacityColorData.LayerMode layerMode,
   float ghostOpacity,
   float missingOpacity,
   float wrongBlockOpacity,
   float wrongStateOpacity,
   float occludedOpacity,
   float interiorFadeRadius,
   boolean renderCorrect,
   boolean renderMissing,
   boolean renderIncorrect,
   boolean missingThroughWalls,
   boolean outlines,
   float outlineWidth,
   float outlineOpacity,
   int renderDistance,
   int rebuildBudget,
   int playerY,
   boolean showBox,
   boolean showOrigin,
   boolean textured,
   int correctColor,
   int missingColor,
   int wrongBlockColor,
   int wrongStateColor,
   int extraColor,
   OpacityColorData.Style style,
   boolean litematicaTranslucent
) {
   static float getAmount(float hideInterior, float interiorFade, float x) {
      if (interiorFade <= hideInterior) {
         return x < hideInterior ? 0.0F : 1.0F;
      } else {
         float opacityColorFactor = Math.max(0.0F, Math.min(1.0F, (x - hideInterior) / (interiorFade - hideInterior)));
         return opacityColorFactor * opacityColorFactor * (3.0F - 2.0F * opacityColorFactor);
      }
   }

   public boolean isLitematicaStyle() {
      return this.style == OpacityColorData.Style.LITEMATICA;
   }

   public float getHideCamera() {
      OpacityColorData.InteriorClarity clarityInterior = this.getClarityInterior();
      if (clarityInterior == OpacityColorData.InteriorClarity.OFF) {
         return 1.0F;
      } else {
         return clarityInterior == OpacityColorData.InteriorClarity.HIDE_CAMERA_CELL ? 0.0F : 0.01F;
      }
   }

   public long getOpacityClarity() {
      long ordinal = this.mode.ordinal();
      long opacityClarityValue = ordinal * 31L + this.style.ordinal();
      long opacityClarityValue2 = opacityClarityValue * 31L + this.layerMode.ordinal();
      long opacityClarityValue3 = opacityClarityValue2 * 31L + (this.renderCorrect ? 1 : 0);
      long opacityClarityValue4 = opacityClarityValue3 * 31L + (this.renderMissing ? 1 : 0);
      long opacityClarityValue5 = opacityClarityValue4 * 31L + (this.renderIncorrect ? 1 : 0);
      long opacityClarityValue6 = opacityClarityValue5 * 31L + (this.outlines ? 1 : 0);
      long opacityClarityValue7 = opacityClarityValue6 * 31L + (this.textured ? 1 : 0);
      long opacityClarityValue8 = opacityClarityValue7 * 31L + Math.round(this.missingOpacity * 50.0F);
      long opacityClarityValue9 = opacityClarityValue8 * 31L + Math.round(this.wrongBlockOpacity * 50.0F);
      long opacityClarityValue10 = opacityClarityValue9 * 31L + Math.round(this.wrongStateOpacity * 50.0F);
      long opacityClarityValue11 = opacityClarityValue10 * 31L + Math.round(this.outlineWidth * 4.0F);
      long opacityClarityValue12 = opacityClarityValue11 * 31L + Math.round(this.outlineOpacity * 50.0F);
      long opacityClarity = opacityClarityValue12 * 31L + (this.interiorClarity == OpacityColorData.InteriorClarity.CROSS_SECTION ? 1 : 0);
      if (this.layerMode != OpacityColorData.LayerMode.ALL || this.interiorClarity == OpacityColorData.InteriorClarity.CROSS_SECTION) {
         opacityClarity = opacityClarity * 31L + this.playerY;
      }

      return opacityClarity;
   }

   public float getAmount(float densityScale, float dist) {
      if (this.getClarityInterior() == OpacityColorData.InteriorClarity.OFF) {
         return 1.0F;
      } else {
         float hideInterior = this.getHideInterior(dist);
         float interiorFade = this.getInteriorFade(dist);
         float opacityColorFactor = getAmount(hideInterior, interiorFade, densityScale);
         return this.getHideCamera() + (1.0F - this.getHideCamera()) * opacityColorFactor;
      }
   }

   public float getInteriorFade(float densityScale) {
      return this.getClarityInterior() == OpacityColorData.InteriorClarity.OFF ? 0.0F : Math.max(0.5F, this.interiorFadeRadius) * densityScale;
   }

   public float getOpacityOutline() {
      return this.isLitematicaStyle() ? 1.0F : this.outlineOpacity;
   }

   private OpacityColorData.InteriorClarity getClarityInterior() {
      return this.isLitematicaStyle() ? OpacityColorData.InteriorClarity.OFF : this.interiorClarity;
   }

   public float getWallsThrough() {
      if (!this.isLitematicaStyle()) {
         return this.outlineWidth;
      } else {
         return this.missingThroughWalls ? 3.0F : 1.0F;
      }
   }

   public float getCorrectMissing(BlockStateType cls, boolean ghostDrawn) {
      if (this.isLitematicaStyle()) {
         return ConstLitematicaUtils.getAmount(cls);
      } else if (cls == BlockStateType.MISSING && ghostDrawn) {
         return 0.0F;
      } else {
         return cls == BlockStateType.CORRECT ? 0.0F : this.getOpacityWrong(cls);
      }
   }

   private int getClarityCross() {
      int clarityCross = this.layerMode.getValue();
      if (this.interiorClarity == OpacityColorData.InteriorClarity.CROSS_SECTION && clarityCross < 0) {
         clarityCross = 1;
      }

      return clarityCross;
   }

   public boolean canModeMissing(BlockStateType cls) {
      if (!this.textured || this.mode == OpacityColorData.Mode.OUTLINES_ONLY || this.mode == OpacityColorData.Mode.BOX_ONLY) {
         return false;
      } else if (this.isLitematicaStyle()) {
         return cls == BlockStateType.MISSING && this.renderMissing && this.mode != OpacityColorData.Mode.WRONG_ONLY;
      } else {
         return switch (cls) {
            case MISSING -> this.renderMissing && this.mode != OpacityColorData.Mode.WRONG_ONLY;
            case WRONG_BLOCK, WRONG_STATE -> this.renderIncorrect && this.mode != OpacityColorData.Mode.MISSING_ONLY;
            case CORRECT -> this.renderCorrect && this.mode == OpacityColorData.Mode.FULL;
            case EXTRA, UNKNOWN -> false;
         };
      }
   }

   public boolean canPlayer(int sectionMinY) {
      int clarityCross = this.getClarityCross();
      return clarityCross < 0 ? true : sectionMinY <= this.playerY + clarityCross && sectionMinY + 15 >= this.playerY - clarityCross;
   }

   public boolean canModeRender(BlockStateType cls) {
      if (this.mode == OpacityColorData.Mode.BOX_ONLY || this.mode == OpacityColorData.Mode.GHOST_ONLY) {
         return false;
      } else if (this.isLitematicaStyle() && cls == BlockStateType.CORRECT) {
         return false;
      } else {
         return switch (cls) {
            case MISSING -> this.renderMissing && this.mode != OpacityColorData.Mode.WRONG_ONLY;
            case WRONG_BLOCK, WRONG_STATE, EXTRA -> this.renderIncorrect && this.mode != OpacityColorData.Mode.MISSING_ONLY;
            case CORRECT -> this.renderCorrect && this.mode == OpacityColorData.Mode.FULL;
            case UNKNOWN -> false;
         };
      }
   }

   public float getHideInterior(float densityScale) {
      OpacityColorData.InteriorClarity clarityInterior = this.getClarityInterior();
      if (clarityInterior == OpacityColorData.InteriorClarity.OFF) {
         return 0.0F;
      } else {
         float interiorFactor = Math.max(0.5F, this.interiorFadeRadius) * densityScale;
         return interiorFactor * (clarityInterior == OpacityColorData.InteriorClarity.HIDE_CAMERA_CELL ? 0.55F : 0.3F);
      }
   }

   public float getAmount(BlockStateType cls) {
      return this.getCorrectMissing(cls, this.canModeMissing(cls));
   }

   public float getOpacityWrong(BlockStateType cls) {
      return switch (cls) {
         case MISSING -> this.missingOpacity;
         case WRONG_BLOCK -> this.wrongBlockOpacity;
         case WRONG_STATE -> this.wrongStateOpacity;
         case CORRECT -> 0.1F;
         case EXTRA -> this.wrongBlockOpacity;
         case UNKNOWN -> 0.0F;
      };
   }

   public boolean canPlayerForInt(int y) {
      int clarityCross = this.getClarityCross();
      return clarityCross < 0 ? true : Math.abs(y - this.playerY) <= clarityCross;
   }

   public static enum InteriorClarity {
      OFF,
      FADE_NEAR_CAMERA,
      HIDE_CAMERA_CELL,
      CROSS_SECTION;
   }

   public static enum LayerMode {
      ALL,
      CURRENT,
      PLUS_ONE,
      PLUS_TWO;

      public int getValue() {
         return switch (this) {
            case ALL -> -1;
            case CURRENT -> 0;
            case PLUS_ONE -> 1;
            case PLUS_TWO -> 2;
         };
      }
   }

   public static enum Mode {
      FULL,
      MISSING_ONLY,
      MISSING_WRONG,
      WRONG_ONLY,
      GHOST_ONLY,
      OUTLINES_ONLY,
      BOX_ONLY;
   }

   public static enum Style {
      CORZ,
      LITEMATICA;
   }
}
