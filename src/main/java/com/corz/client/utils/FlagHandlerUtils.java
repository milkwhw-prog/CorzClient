package com.corz.client.utils;
import com.corz.client.utils.WalkableRegionAnalyzer;

public final class FlagHandlerUtils {

   public static WalkableRegionAnalyzer.Terrain getTerrainForCells(WalkableRegionAnalyzer.Terrain terrain, FlagHandlerUtils.Cells cells) {
      return new WalkableRegionAnalyzer.Terrain() {
         @Override
         public boolean isHoneyBlock(int mutableValue, int mutableValue2, int z) {
            return terrain.isHoneyBlock(mutableValue, mutableValue2, z);
         }

         @Override
         public boolean solid(int solidValue, int candidateY, int candidateZ) {
            return cells.has(solidValue, candidateY, candidateZ) || terrain.solid(solidValue, candidateY, candidateZ);
         }

         @Override
         public boolean isThinCollision(int mutableValue, int mutableValue5, int mutableValue6) {
            return terrain.isThinCollision(mutableValue, mutableValue5, mutableValue6);
         }

         @Override
         public boolean canStandOn(int cellsValue, int y, int mutableValue9) {
            return !cells.has(cellsValue, y, mutableValue9) && terrain.canStandOn(cellsValue, y, mutableValue9);
         }
      };
   }

   public static boolean canFlagTerrainCell(WalkableRegionAnalyzer.Terrain terrain, FlagHandlerUtils.Cells cells, int n, int n2, int n3) {
      WalkableRegionAnalyzer.Terrain handler = getTerrainForCells(terrain, cells);
      byte limit = 40;
      return WalkableRegionAnalyzer.countReachableCells(terrain, n, n2, n3, limit) >= limit && WalkableRegionAnalyzer.countReachableCells(handler, n, n2, n3, limit) < limit
         || WalkableRegionAnalyzer.doesOverlayShrinkRegion(terrain, handler, n, n2, n3);
   }

   public static boolean canFlagWithCount(boolean flag, int n) {
      return flag && n == 0;
   }

   public static boolean canFlagWithLimit(boolean flag, int limit, int n) {
      return flag && limit > 0 && n < 3;
   }

   private FlagHandlerUtils() {
   }

   public interface Cells {
      boolean has(int cellsValue, int candidateY, int candidateZ);
   }
}
