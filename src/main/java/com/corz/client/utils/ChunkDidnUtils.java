package com.corz.client.utils;

import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import ai.auth.Module;

public final class ChunkDidnUtils {
   public static boolean canBlockState(BlockPos pos, Block block) {
      return Module.mc.world.getBlockState(pos).getBlock() == block;
   }

   public static Stream<WorldChunk> getFilter() {
      int filterValue = Math.max(2, Module.mc.options.getClampedViewDistance()) + 3;
      int filterFactor = filterValue * 2 + 1;
      ChunkPos chunkPos = Module.mc.player.getChunkPos();
      ChunkPos chunkPos2 = new ChunkPos(chunkPos.x - filterValue, chunkPos.z - filterValue);
      ChunkPos chunkPos3 = new ChunkPos(chunkPos.x + filterValue, chunkPos.z + filterValue);
      return Stream.<ChunkPos>iterate(chunkPos2, min -> {
            int var3x = min.x;
            int var4x = min.z;
            if (++var3x > chunkPos3.x) {
               var3x = chunkPos2.x;
               var4x++;
            }

            if (var4x > chunkPos3.z) {
               throw new IllegalStateException("Stream limit didn't work.");
            } else {
               return new ChunkPos(var3x, var4x);
            }
         })
         .limit((long)filterFactor * filterFactor)
         .filter(chunkPos4 -> Module.mc.world.isChunkLoaded(chunkPos4.x, chunkPos4.z))
         .map(chunkPos5 -> Module.mc.world.getChunk(chunkPos5.x, chunkPos5.z))
         .filter(Objects::nonNull);
   }

   public static void handleHandInstance(BlockHitResult blockHitResult, boolean handFlag) {
      ActionResult interactBlock = Module.mc.interactionManager.interactBlock(Module.mc.player, Hand.MAIN_HAND, blockHitResult);
      if (interactBlock instanceof Success && handFlag) {
         Module.mc.player.swingHand(Hand.MAIN_HAND);
      }
   }
}
