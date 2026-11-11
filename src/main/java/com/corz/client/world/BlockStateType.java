package com.corz.client.world;

import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import com.corz.client.utils.BottleLevelUtils;

public enum BlockStateType {
   CORRECT,
   MISSING,
   WRONG_BLOCK,
   WRONG_STATE,
   EXTRA,
   UNKNOWN;

   public static BlockStateType getCorrectAir(BlockState state, BlockState blockState, boolean correctAirFlag) {
      if (!correctAirFlag) {
         return UNKNOWN;
      } else if (state == null) {
         return UNKNOWN;
      } else if (state == blockState) {
         return CORRECT;
      } else {
         boolean air = blockState.isAir();
         if (state.isAir()) {
            return air ? CORRECT : EXTRA;
         } else if (air) {
            return MISSING;
         } else {
            return state.getBlock() != blockState.getBlock() ? WRONG_BLOCK : WRONG_STATE;
         }
      }
   }

   private static boolean canProperties(BlockState state, BlockState blockState) {
      for (Property property : state.getProperties()) {
         if (!BottleLevelUtils.isContains(property.getName())) {
            if (!blockState.contains(property)) {
               return true;
            }

            if (!Objects.equals(state.get(property), blockState.get(property))) {
               return true;
            }
         }
      }

      return false;
   }

   public static BlockStateType getCorrectReplaceable(BlockState state, BlockState blockState, boolean correctReplaceableFlag) {
      if (!correctReplaceableFlag) {
         return UNKNOWN;
      } else if (state == null) {
         return UNKNOWN;
      } else if (state == blockState) {
         return CORRECT;
      } else {
         boolean air = state.isAir();
         boolean air2 = blockState.isAir();
         if (air) {
            return !air2 && !blockState.isReplaceable() ? EXTRA : CORRECT;
         } else if (!air2 && !blockState.isReplaceable()) {
            if (state.getBlock() != blockState.getBlock()) {
               return WRONG_BLOCK;
            } else {
               return canProperties(state, blockState) ? WRONG_STATE : CORRECT;
            }
         } else {
            return MISSING;
         }
      }
   }
}
