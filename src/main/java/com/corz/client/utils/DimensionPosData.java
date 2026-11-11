package com.corz.client.utils;

import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.ChestTrappedType;

public record DimensionPosData(long pos, String dimension, ChestTrappedType type) {
   public BlockPos getLong() {
      return BlockPos.fromLong(this.pos);
   }

   public static DimensionPosData of(BlockPos pos, String dimension, ChestTrappedType type) {
      return new DimensionPosData(pos.asLong(), dimension, type);
   }
}
