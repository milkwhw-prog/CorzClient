package com.corz.client.utils;

public final class PlayerBoxOverlapCheck {

   private PlayerBoxOverlapCheck() {
   }

   public static boolean hasClearance(double playerY, int blockY) {
      return playerY >= blockY + 1.0;
   }

   public static boolean isPlayerBoxIntersectingBlock(double playerX, double playerY, double playerZ, int blockX, int blockY, int blockZ) {
      return playerX - 0.3 < blockX + 1 && playerX + 0.3 > blockX && playerY < blockY + 1 && playerY + 1.8 > blockY && playerZ - 0.3 < blockZ + 1 && playerZ + 0.3 > blockZ;
   }
}
