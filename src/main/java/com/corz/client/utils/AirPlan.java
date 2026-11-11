package com.corz.client.utils;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.Cells;

public final class AirPlan {
   private static final int[] NEIGHBOR_DX = new int[]{1, -1, 0, 0};
   private static final int[] NEIGHBOR_DZ = new int[]{0, 0, 1, -1};

   public static int scaleAirByOxygenBonus(int airPlanLimit, double airPlanBound) {
      if (airPlanLimit >= 1000000) {
         return 1000000;
      } else {
         double airPlanFactor = 1.0 + Math.max(0.0, airPlanBound);
         return (int)Math.min(999999L, Math.round(airPlanLimit * airPlanFactor));
      }
   }

   public static boolean isBreathableSpot(Cells cells, int airPlanValue, int y, int z) {
      if (!isHazardFreePassable(cells, airPlanValue, y, z)) {
         return false;
      } else {
         int hashPut = cells.getCellFlags(airPlanValue, y + 1, z);
         return Cells.isFreeOfSolidFlags(hashPut) && !Cells.hasAnyFlag(hashPut, 14);
      }
   }

   public static boolean isAirAboveThreshold(int airPlanLimit, int airPlanValue, boolean airPlanFlag) {
      return !airPlanFlag && airPlanLimit >= getLowAirThreshold(airPlanValue);
   }

   private AirPlan() {
   }

   public static int getAirNeededForPathCost(int airPlanBound) {
      return 60 + (int)Math.ceil(1.4 * Math.max(0, airPlanBound));
   }

   public static boolean isWaterCell(Cells cells, int airPlanValue, int unpackLongY, int unpackLongZ) {
      int hashPut = cells.getCellFlags(airPlanValue, unpackLongY, unpackLongZ);
      return Cells.hasAnyFlag(hashPut, 4) && Cells.isFreeOfSolidFlags(hashPut);
   }

   private static void relaxNode(
      PriorityQueue<long[]> priorityQueue, Long2IntOpenHashMap long2IntOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap2, List<long[]> list, long airPlanValue, int unpackLongX, int unpackLongY, int unpackLongZ, int airPlanValue5
   ) {
      long long2 = BlockPos.asLong(unpackLongX, unpackLongY, unpackLongZ);
      if (airPlanValue5 < long2IntOpenHashMap.get(long2)) {
         long2IntOpenHashMap.put(long2, airPlanValue5);
         long2IntOpenHashMap2.put(long2, list.size());
         list.add(new long[]{long2, airPlanValue});
         priorityQueue.add(new long[]{long2, airPlanValue5});
      }
   }

   public static int getSwimUpCost(Cells cells, int airPlanValue, int airPlanValue2, int unpackLongZ) {
      for (int i = 0; i <= 24; i++) {
         if (!isHazardFreePassable(cells, airPlanValue, airPlanValue2 + i, unpackLongZ) || !isHazardFreePassable(cells, airPlanValue, airPlanValue2 + 1 + i, unpackLongZ)) {
            return -1;
         }

         if (isBreathableSpot(cells, airPlanValue, airPlanValue2 + i, unpackLongZ)) {
            return i * 8;
         }
      }

      return -1;
   }

   public static boolean isHazardFreePassable(Cells cells, int airPlanValue, int unpackLongY, int unpackLongZ) {
      int hashPut = cells.getCellFlags(airPlanValue, unpackLongY, unpackLongZ);
      return Cells.isFreeOfSolidFlags(hashPut) && !Cells.hasAnyFlag(hashPut, 26);
   }

   public static List<BlockPos> findPathToAirPocket(Cells cells, int x, int y, int z, int unpackPutLimit) {
      if (isBreathableSpot(cells, x, y, z)) {
         return List.of(new BlockPos(x, y, z));
      } else {
         Long2IntOpenHashMap long2IntOpenHashMap = new Long2IntOpenHashMap();
         Long2IntOpenHashMap long2IntOpenHashMap2 = new Long2IntOpenHashMap();
         long2IntOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
         ArrayList arrayList = new ArrayList();
         PriorityQueue priorityQueue = new PriorityQueue<>((obj, obj2) -> Long.compare(((long[]) obj)[1], ((long[]) obj2)[1]));
         long long2 = BlockPos.asLong(x, y, z);
         long2IntOpenHashMap.put(long2, 0);
         long2IntOpenHashMap2.put(long2, -1);
         arrayList.add(new long[]{long2});
         priorityQueue.add(new long[]{long2, 0L});
         int unpackPutCounter = 0;

         while (!priorityQueue.isEmpty() && unpackPutCounter < unpackPutLimit) {
            long[] unpackPutValues = (long[])priorityQueue.poll();
            long unpackPutValue = unpackPutValues[0];
            int unpackPutValue2 = long2IntOpenHashMap.get(unpackPutValue);
            if (unpackPutValues[1] == unpackPutValue2) {
               unpackPutCounter++;
               int unpackLongX = BlockPos.unpackLongX(unpackPutValue);
               int unpackLongY = BlockPos.unpackLongY(unpackPutValue);
               int unpackLongZ = BlockPos.unpackLongZ(unpackPutValue);
               if (isBreathableSpot(cells, unpackLongX, unpackLongY, unpackLongZ)) {
                  return buildPathFromParents(unpackPutValue, long2IntOpenHashMap2, arrayList);
               }

               if (Math.abs(unpackLongX - x) <= 10 && Math.abs(unpackLongZ - z) <= 10) {
                  if (isWaterCell(cells, unpackLongX, unpackLongY, unpackLongZ) && isHazardFreePassable(cells, unpackLongX, unpackLongY + 1, unpackLongZ) && isHazardFreePassable(cells, unpackLongX, unpackLongY + 2, unpackLongZ)) {
                     relaxNode(priorityQueue, long2IntOpenHashMap, long2IntOpenHashMap2, arrayList, unpackPutValue, unpackLongX, unpackLongY + 1, unpackLongZ, unpackPutValue2 + 8);
                  }

                  for (int i = 0; i < 4; i++) {
                     int airPlanValue = unpackLongX + NEIGHBOR_DX[i];
                     int unpackPutValue4 = unpackLongZ + NEIGHBOR_DZ[i];
                     if (isHazardFreePassable(cells, airPlanValue, unpackLongY, unpackPutValue4)
                        && isHazardFreePassable(cells, airPlanValue, unpackLongY + 1, unpackPutValue4)
                        && (isWaterCell(cells, airPlanValue, unpackLongY, unpackPutValue4) || Cells.hasAnyFlag(cells.getCellFlags(airPlanValue, unpackLongY - 1, unpackPutValue4), 1))) {
                        relaxNode(priorityQueue, long2IntOpenHashMap, long2IntOpenHashMap2, arrayList, unpackPutValue, airPlanValue, unpackLongY, unpackPutValue4, unpackPutValue2 + 13);
                     }
                  }

                  if (isWaterCell(cells, unpackLongX, unpackLongY - 1, unpackLongZ)) {
                     relaxNode(priorityQueue, long2IntOpenHashMap, long2IntOpenHashMap2, arrayList, unpackPutValue, unpackLongX, unpackLongY - 1, unpackLongZ, unpackPutValue2 + 9);
                  }
               }
            }
         }

         return null;
      }
   }

   public static AirPlan.AirAction decideAirAction(int panicSurfaceValue, boolean panicSurfaceFlag, boolean panicSurfaceFlag2, int airPlanBound, boolean panicSurfaceFlag3) {
      if (panicSurfaceFlag2 || !panicSurfaceFlag) {
         return AirPlan.AirAction.OK;
      } else if (panicSurfaceFlag3 || panicSurfaceValue <= 30) {
         return AirPlan.AirAction.PANIC;
      } else if (airPlanBound < 0) {
         return panicSurfaceValue < 240 ? AirPlan.AirAction.PANIC : AirPlan.AirAction.SURFACE;
      } else {
         return panicSurfaceValue <= getAirNeededForPathCost(airPlanBound) ? AirPlan.AirAction.SURFACE : AirPlan.AirAction.OK;
      }
   }

   public static int getLowAirThreshold(int airPlanLimit) {
      return airPlanLimit >= 1000000 ? 1000000 : Math.max(60, airPlanLimit - 20);
   }

   public static int getPathMoveCost(List<BlockPos> list) {
      if (list == null) {
         return -1;
      } else {
         int airPlanTotal = 0;

         for (int i = 1; i < list.size(); i++) {
            BlockPos blockPos = (BlockPos)list.get(i - 1);
            BlockPos route3 = (BlockPos)list.get(i);
            if (route3.getY() > blockPos.getY()) {
               airPlanTotal += 8;
            } else if (route3.getY() < blockPos.getY()) {
               airPlanTotal += 9;
            } else {
               airPlanTotal += 13;
            }
         }

         return airPlanTotal;
      }
   }

   private static List<BlockPos> buildPathFromParents(long airPlanValue, Long2IntOpenHashMap long2IntOpenHashMap, List<long[]> list) {
      ArrayList arrayList = new ArrayList();
      long airPlanValue2 = airPlanValue;

      for (int i = 0; i < 4096; i++) {
         arrayList.add(BlockPos.fromLong(airPlanValue2));
         int from2 = long2IntOpenHashMap.get(airPlanValue2);
         if (from2 < 0) {
            break;
         }

         airPlanValue2 = ((long[])list.get(from2))[1];
      }

      Collections.reverse(arrayList);
      return arrayList;
   }

   public static enum AirAction {
      OK,
      SURFACE,
      PANIC;
   }
}
