package com.corz.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

public final class OutsideUseful {
   public final BlockPos[] blockPoss;
   public final int outsideSolid;
   public final int outsideArea;
   public final int useful;
   public final int inside;
   public final Direction face;
   public final BlockPos blockPos;

   private OutsideUseful(BlockPos blockPos, Direction face2, BlockPos[] blockPoss, int inside, int outsideArea2, int outsideSolid2, int useful) {
      this.blockPos = blockPos;
      this.face = face2;
      this.blockPoss = blockPoss;
      this.inside = inside;
      this.outsideArea = outsideArea2;
      this.outsideSolid = outsideSolid2;
      this.useful = useful;
   }

   @Override
   public String toString() {
      return "center="
         + this.blockPos.getX()
         + ","
         + this.blockPos.getY()
         + ","
         + this.blockPos.getZ()
         + " face="
         + this.face
         + " footprint="
         + 9
         + " inside="
         + this.inside
         + " outsideArea="
         + this.outsideArea
         + " outsideSolid="
         + this.outsideSolid
         + " useful="
         + this.useful
         + " safe="
         + this.isOutsideUseful();
   }

   public boolean canAxisFace(Axis axis) {
      return this.face.getAxis() == axis;
   }

   public boolean isOutsideUseful() {
      return this.outsideSolid == 0 && this.useful >= 1;
   }

   public static OutsideUseful getOutsideUseful(DirectionOffsetUtilsUseful.CellWorld cellWorld, BlockPos pos, Direction direction) {
      BlockPos[] blockPos = new BlockPos[9];
      int[] ints = new int[4];
      int[] ints2 = new int[]{0};
      AxisPosUtils.handleAxis(pos, direction, (cells, y2, z) -> {
         blockPos[ints2[0]++] = new BlockPos(cells, y2, z);
         if (cellWorld.canFlagForInt(cells, y2, z)) {
            ints[0]++;
            if (cellWorld.canFlag(cells, y2, z)) {
               ints[3]++;
            }
         } else {
            ints[1]++;
            if (cellWorld.canFlag3(cells, y2, z)) {
               ints[2]++;
            }
         }
      });
      return new OutsideUseful(pos, direction, blockPos, ints[0], ints[1], ints[2], ints[3]);
   }
}
