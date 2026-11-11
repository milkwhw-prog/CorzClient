package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import com.corz.client.utils.HunterPather;
import com.corz.client.utils.McCells;
import com.corz.client.utils.Cells;

public final class HunterGrid implements HunterPather.Grid {
   private final McCells mcCells;
   private final LongOpenHashSet forbiddenCells = new LongOpenHashSet();
   private final Mutable mutable = new Mutable();
   private long noDigBlockKey = Long.MIN_VALUE;

   public void setNoDigBlock(BlockPos pos2) {
      this.noDigBlockKey = pos2 == null ? Long.MIN_VALUE : pos2.asLong();
   }

   @Override
   public float getDigCost(int mutableHardnessValue, int mutableHardnessValue2, int mutableHardnessValue3) {
      World world = this.mcCells.getWorld();
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (world != null && player != null) {
         this.mutable.set(mutableHardnessValue, mutableHardnessValue2, mutableHardnessValue3);
         BlockState blockState = world.getBlockState(this.mutable);
         float maxHardness = Math.max(0.0F, blockState.getHardness(world, this.mutable));
         float mutableHardnessBound = 1.0F;
         PlayerInventory inventory = player.getInventory();

         for (int i = 0; i < 9; i++) {
            mutableHardnessBound = Math.max(mutableHardnessBound, inventory.getStack(i).getMiningSpeedMultiplier(blockState));
         }

         return 1.0F + maxHardness * 30.0F / mutableHardnessBound / 5.0F;
      } else {
         return 1.0F;
      }
   }

   @Override
   public float getLavaPenalty(int cellsValue, int i, int cellsValue3) {
      return Cells.isNextToLava(this.mcCells, cellsValue, i, cellsValue3) ? 30.0F : 0.0F;
   }

   public HunterGrid(McCells mcCells) {
      this.mcCells = mcCells;
   }

   @Override
   public boolean isNextToLavaCell(int cellsValue, int i, int cellsValue3) {
      return Cells.isNextToLava(this.mcCells, cellsValue, i, cellsValue3);
   }

   public void markCellForbidden(BlockPos pos2) {
      if (pos2 != null) {
         this.forbiddenCells.add(pos2.asLong());
      }
   }

   @Override
   public boolean isForbiddenCell(int cellsHashValue, int mutableBottomLimit, int cellsHashValue3) {
      return this.forbiddenCells.contains(BlockPos.asLong(cellsHashValue, mutableBottomLimit, cellsHashValue3)) || Cells.hasAnyFlag(this.mcCells.getCellFlags(cellsHashValue, mutableBottomLimit, cellsHashValue3), 26);
   }

   public void clearForbiddenCells() {
      this.forbiddenCells.clear();
   }

   @Override
   public boolean isWaterCell(int cellsValue, int mutableBottomLimit, int cellsValue3) {
      return Cells.hasAnyFlag(this.mcCells.getCellFlags(cellsValue, mutableBottomLimit, cellsValue3), 4);
   }

   @Override
   public boolean canDigCell(int cellsHashValue, int i, int unpackLongZ) {
      long long2 = BlockPos.asLong(cellsHashValue, i, unpackLongZ);
      return long2 != this.noDigBlockKey && !this.forbiddenCells.contains(long2) ? Cells.isSafeSolidBlock(this.mcCells, cellsHashValue, i, unpackLongZ) : false;
   }

   public boolean isCellForbidden(BlockPos pos2) {
      return pos2 != null && this.forbiddenCells.contains(pos2.asLong());
   }

   @Override
   public boolean isSolidCell(int cellsValue, int mutableBottomLimit, int cellsValue3) {
      return Cells.hasAnyFlag(this.mcCells.getCellFlags(cellsValue, mutableBottomLimit, cellsValue3), 1);
   }
}
