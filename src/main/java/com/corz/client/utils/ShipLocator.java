package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public final class ShipLocator {
   private int[] chunkScanOrder;
   private final Map<Long, ShipLocator.Ship> shipsByPos = new LinkedHashMap<>();
   private int lastScanLimit;
   private int scanCursor;

   public ShipLocator() {
      this.chunkScanOrder = new int[0];
      this.lastScanLimit = -1;
   }

   public void update() {
      this.shipsByPos.clear();
      this.scanCursor = 0;
   }

   public void markLooted(MinecraftClient client, ShipLocator.Ship ship) {
      if (client.world != null) {
         List entitiesByClass = client.world.getEntitiesByClass(ItemFrameEntity.class, new Box(ship.blockPos).expand(1.0), itemFrameEntity -> true);
         ItemFrameEntity itemFrameEntity2 = null;

         for (ItemFrameEntity itemFrameEntity3 : (Iterable<net.minecraft.entity.decoration.ItemFrameEntity>) (entitiesByClass)) {
            if (itemFrameEntity2 == null || itemFrameEntity3.getBlockPos().getSquaredDistance(ship.blockPos) < itemFrameEntity2.getBlockPos().getSquaredDistance(ship.blockPos)) {
               itemFrameEntity2 = itemFrameEntity3;
            }
         }

         if (itemFrameEntity2 == null) {
            ship.lootedCount++;
         } else {
            ship.lootedCount = 0;
            ship.loot = itemFrameEntity2.getHeldItemStack().isOf(Items.ELYTRA) ? ShipLocator.Loot.ELYTRA : ShipLocator.Loot.EMPTY;
         }
      }
   }

   public Collection<ShipLocator.Ship> getShips() {
      return this.shipsByPos.values();
   }

   private static boolean hasBlockBehind(MinecraftClient client, BlockPos pos, Direction direction) {
      int oppositeOffsetCounter = 0;
      BlockPos offset = pos.offset(direction.getOpposite());

      for (BlockPos blockPos : new BlockPos[]{offset, offset.down(), offset.offset(direction.getOpposite()), pos.down().offset(direction.getOpposite(), 2)}) {
         if (isPurpurOrEndBlock(client.world.getBlockState(blockPos))) {
            oppositeOffsetCounter++;
         }
      }

      return oppositeOffsetCounter >= 2;
   }

   public static boolean isPurpurOrEndBlock(BlockState state) {
      return state.isOf(Blocks.PURPUR_BLOCK)
         || state.isOf(Blocks.PURPUR_PILLAR)
         || state.isOf(Blocks.PURPUR_STAIRS)
         || state.isOf(Blocks.PURPUR_SLAB)
         || state.isOf(Blocks.END_STONE_BRICKS)
         || state.isOf(Blocks.MAGENTA_STAINED_GLASS)
         || state.isOf(Blocks.OBSIDIAN)
         || state.isOf(Blocks.END_ROD);
   }

   private void trimShipLimit(int max) {
      this.lastScanLimit = max;
      ArrayList arrayList = new ArrayList();

      for (int i = -max; i <= max; i++) {
         for (int j = -max; j <= max; j++) {
            if (i * i + j * j <= max * max + max) {
               arrayList.add(new int[]{i, j});
            }
         }
      }

      arrayList.sort((obj, obj2) -> Integer.compare(((int[]) obj)[0] * ((int[]) obj)[0] + ((int[]) obj)[1] * ((int[]) obj)[1], ((int[]) obj2)[0] * ((int[]) obj2)[0] + ((int[]) obj2)[1] * ((int[]) obj2)[1]));
      this.chunkScanOrder = new int[arrayList.size() * 2];

      for (int k = 0; k < arrayList.size(); k++) {
         this.chunkScanOrder[2 * k] = ((int[])arrayList.get(k))[0];
         this.chunkScanOrder[2 * k + 1] = ((int[])arrayList.get(k))[1];
      }

      this.scanCursor = 0;
   }

   public ItemFrameEntity findItemFrame(MinecraftClient client, ShipLocator.Ship ship) {
      if (client.world == null) {
         return null;
      } else {
         List entitiesByClass = client.world.getEntitiesByClass(ItemFrameEntity.class, new Box(ship.blockPos).expand(1.0), itemFrameEntity -> itemFrameEntity.getHeldItemStack().isOf(Items.ELYTRA));
         return entitiesByClass.isEmpty() ? null : (ItemFrameEntity)entitiesByClass.get(0);
      }
   }

   public void scanChunkBlocks(MinecraftClient client, int chunkBlockValue, int chunkBlockLimit) {
      if (client.world != null && client.player != null) {
         if (chunkBlockValue != this.lastScanLimit) {
            this.trimShipLimit(chunkBlockValue);
         }

         int chunkBlockValue2 = client.player.getBlockX() >> 4;
         int chunkBlockValue3 = client.player.getBlockZ() >> 4;
         int chunkBlockLimit2 = this.chunkScanOrder.length / 2;

         for (int i = 0; i < chunkBlockLimit && chunkBlockLimit2 > 0; i++) {
            int chunkBlockValue4 = this.scanCursor++ % chunkBlockLimit2;
            WorldChunk worldChunk = client.world.getChunkManager().getWorldChunk(chunkBlockValue2 + this.chunkScanOrder[2 * chunkBlockValue4], chunkBlockValue3 + this.chunkScanOrder[2 * chunkBlockValue4 + 1]);
            if (worldChunk != null) {
               for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                  if (blockEntity instanceof SkullBlockEntity) {
                     BlockState cachedState = blockEntity.getCachedState();
                     if (cachedState.isOf(Blocks.DRAGON_WALL_HEAD)) {
                        BlockPos pos = blockEntity.getPos();
                        if (!this.shipsByPos.containsKey(pos.asLong())) {
                           Direction direction = cachedState.get(WallSkullBlock.FACING);
                           if (hasBlockBehind(client, pos, direction)) {
                              this.shipsByPos.put(pos.asLong(), new ShipLocator.Ship(pos, direction));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static enum Loot {
      UNKNOWN,
      ELYTRA,
      EMPTY,
      GONE;
   }

   public static final class Ship {
      public int unreachableCount;
      public final Direction opposite;
      public ShipLocator.Loot loot = ShipLocator.Loot.UNKNOWN;
      public final BlockPos blockPos;
      public final BlockPos immutable;
      public final Direction facing;
      public boolean unreachable;
      public final Direction rightDirection;
      public int lootedCount;

      public Vec3d getCenter() {
         return Vec3d.ofCenter(this.getRelativePos(6, 8, 14));
      }

      Ship(BlockPos pos, Direction blockState) {
         this.immutable = pos.toImmutable();
         this.facing = blockState;
         this.opposite = blockState.getOpposite();
         this.rightDirection = blockState.rotateYClockwise();
         this.blockPos = this.getRelativePos(6, 5, 7);
      }

      public Vec3d getApproachPoint() {
         Vec3d center = Vec3d.ofCenter(this.blockPos);
         return center.subtract(this.opposite.getOffsetX() * 0.46875, 0.0, this.opposite.getOffsetZ() * 0.46875);
      }

      public int getY() {
         return this.immutable.getY();
      }

      public BlockPos getRelativePos(int offsetYclockwiseValue, int offsetYclockwiseValue2, int offsetYclockwiseValue3) {
         return this.immutable.offset(this.rightDirection, offsetYclockwiseValue - 6).offset(Direction.UP, offsetYclockwiseValue2 - 8).offset(this.opposite, offsetYclockwiseValue3);
      }
   }
}
