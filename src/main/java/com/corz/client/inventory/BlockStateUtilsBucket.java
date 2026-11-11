package com.corz.client.inventory;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public final class BlockStateUtilsBucket {
   public static Item getAir(BlockState blockState) {
      if (blockState == null) {
         return Items.AIR;
      } else {
         BlockStateUtilsBucket.FluidMaterial bucketUnsupported = getBucketUnsupported(
            blockState.getBlock() == Blocks.LAVA, blockState.getBlock() == Blocks.WATER, blockState.getFluidState().isStill()
         );

         return switch (bucketUnsupported) {
            case NONE -> blockState.getBlock() == Blocks.TWISTING_VINES_PLANT
               ? Items.TWISTING_VINES
               : (
                  blockState.getBlock() == Blocks.WEEPING_VINES_PLANT
                     ? Items.WEEPING_VINES
                     : (blockState.getBlock() == Blocks.BAMBOO_SAPLING ? Items.BAMBOO : blockState.getBlock().asItem())
               );
            case LAVA_BUCKET -> Items.LAVA_BUCKET;
            case WATER_BUCKET -> Items.WATER_BUCKET;
            case UNSUPPORTED -> Items.AIR;
         };
      }
   }

   private BlockStateUtilsBucket() {
   }

   static BlockStateUtilsBucket.FluidMaterial getBucketUnsupported(boolean bucketUnsupportedFlag, boolean bucketUnsupportedFlag2, boolean bucketUnsupportedFlag3) {
      if (!bucketUnsupportedFlag && !bucketUnsupportedFlag2) {
         return BlockStateUtilsBucket.FluidMaterial.NONE;
      } else if (!bucketUnsupportedFlag3) {
         return BlockStateUtilsBucket.FluidMaterial.UNSUPPORTED;
      } else {
         return bucketUnsupportedFlag ? BlockStateUtilsBucket.FluidMaterial.LAVA_BUCKET : BlockStateUtilsBucket.FluidMaterial.WATER_BUCKET;
      }
   }

   static enum FluidMaterial {
      NONE,
      LAVA_BUCKET,
      WATER_BUCKET,
      UNSUPPORTED;
   }
}
