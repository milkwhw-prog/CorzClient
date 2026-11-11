package com.corz.client.utils;

import net.minecraft.util.math.BlockPos;

public interface BlockPosApi {
   float getBlastResistance(BlockPos pos);

   default boolean canStandAt(BlockPos pos) {
      return pos != null
         && this.hasSolidTopFace(pos.down())
         && this.isPassable(pos)
         && this.isPassable(pos.up())
         && !this.hasFluid(pos)
         && !this.hasFluid(pos.up());
   }

   boolean hasFluid(BlockPos pos);

   boolean isPassable(BlockPos pos);

   float getHardnessWorld(BlockPos pos);

   boolean isToolRequired(BlockPos pos);

   int getToolNeeds(BlockPos pos);

   boolean isReplaceable(BlockPos pos);

   boolean isBedrockOrObsidian(BlockPos pos);

   boolean hasSolidTopFace(BlockPos pos);

   default boolean isFreeToPlaceAt(BlockPos pos) {
      return this.isPassable(pos) && this.isReplaceable(pos) && !this.hasFluid(pos);
   }

   default boolean isSolidSupportBlock(BlockPos pos) {
      return !this.isPassable(pos) && this.hasSolidTopFace(pos);
   }
}
