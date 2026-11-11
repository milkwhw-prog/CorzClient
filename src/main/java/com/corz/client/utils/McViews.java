package com.corz.client.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.World;
import com.corz.client.utils.StandFinder;
import com.corz.client.utils.HunterBody;
import com.corz.client.utils.Cells;
import com.corz.client.utils.FleePlanner;

public final class McViews {
   public static StandFinder.Sight getSight(HunterBody hunterBody) {
      return (x, y, z, x2, bodyx, z2) -> hunterBody.findVisibleFacePoint(new Vec3d(x, y, z), new BlockPos(x2, bodyx, z2), 4.5) != null;
   }

   public static boolean isSafeStandSpot(Cells cells, int mcViewsValue, int topY, int unpackLongZ) {
      int hashPut = cells.getCellFlags(mcViewsValue, topY - 1, unpackLongZ);
      int cellFlags = cells.getCellFlags(mcViewsValue, topY, unpackLongZ);
      int cellsValue = cells.getCellFlags(mcViewsValue, topY + 1, unpackLongZ);
      if (Cells.isSolidWithoutHazard(hashPut) && Cells.isFreeOfSolidFlags(cellFlags) && Cells.isFreeOfSolidFlags(cellsValue)) {
         return Cells.hasAnyFlag(cellFlags | cellsValue, 286) ? false : !Cells.isNextToLava(cells, mcViewsValue, topY, unpackLongZ) && !StandFinder.hasDeepDropNeighbor(cells, mcViewsValue, topY, unpackLongZ);
      } else {
         return false;
      }
   }

   public static BlockPos getSurfaceStandPos(Cells cells, World world, int x2, int z2) {
      if (world != null && world.getChunkManager().getWorldChunk(x2 >> 4, z2 >> 4) != null) {
         int topY = world.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, x2, z2);
         return isSafeStandSpot(cells, x2, topY, z2) ? new BlockPos(x2, topY, z2) : null;
      } else {
         return null;
      }
   }

   public static FleePlanner.Ground getGround(Cells cells) {
      return (groundValue, unpackLongZ, groundValue3) -> getGroundLevel(cells, groundValue, unpackLongZ, groundValue3);
   }

   private McViews() {
   }

   public static int getGroundLevel(Cells cells, int mcViewsValue, int unpackLongZ, int mcViewsValue3) {
      for (int i = mcViewsValue3 + 1; i >= mcViewsValue3 - 3 - 1; i--) {
         int hashPut = cells.getCellFlags(mcViewsValue, i, unpackLongZ);
         int cellFlags = cells.getCellFlags(mcViewsValue, i + 1, unpackLongZ);
         int cellsValue = cells.getCellFlags(mcViewsValue, i - 1, unpackLongZ);
         if (Cells.hasAnyFlag(hashPut | cellFlags | cellsValue, 256)) {
            return Integer.MIN_VALUE;
         }

         if (Cells.isFreeOfSolidFlags(hashPut) && Cells.isFreeOfSolidFlags(cellFlags)) {
            if (Cells.hasAnyFlag(hashPut | cellFlags, 10)) {
               return -2147483647;
            }

            if (!Cells.hasAnyFlag(hashPut, 4)) {
               if (Cells.hasAnyFlag(cellsValue, 1)) {
                  if (Cells.hasAnyFlag(cellsValue, 26)) {
                     return -2147483647;
                  }

                  return i;
               }

               if (Cells.hasAnyFlag(cellsValue, 2)) {
                  return -2147483647;
               }
            } else {
               int mcViewsCounter = 0;

               while (mcViewsCounter < 3 && Cells.hasAnyFlag(cells.getCellFlags(mcViewsValue, i - mcViewsCounter, unpackLongZ), 4)) {
                  mcViewsCounter++;
               }

               if (mcViewsCounter > 1) {
                  return -2147483646;
               }

               int cellFlags4 = cells.getCellFlags(mcViewsValue, i - 1, unpackLongZ);
               if (Cells.hasAnyFlag(cellFlags4, 10)) {
                  return -2147483647;
               }

               if (Cells.hasAnyFlag(cellFlags4, 1)) {
                  return i;
               }
            }
         }
      }

      return Integer.MIN_VALUE;
   }
}
