package com.corz.client.utils;

import java.util.List;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.AStarPathfinder;

public final class PathFollower {
   private int currentNodeIndex = -1;
   private List<AStarPathfinder.Node> currentPath;

   private static int findStartIndex(List<AStarPathfinder.Node> list, Vec3d vec) {
      int pathSize = list.size();
      if (pathSize < 2) {
         return pathSize;
      } else {
         int gapFeetValue = 1;
         double maxValue = Double.MAX_VALUE;

         for (int i = 1; i < pathSize && i <= 6; i++) {
            double feet = getSegmentCost(list, i, vec);
            if (feet < maxValue - 1.0E-9) {
               maxValue = feet;
               gapFeetValue = i;
            }
         }

         return gapFeetValue;
      }
   }

   static double distanceToSegment(double gapFeetValue, double gapFeetValue2, double gapFeetValue3, double gapFeetValue4, double gapFeetValue5, double gapFeetValue6) {
      double gapFeetFactor = gapFeetValue5 - gapFeetValue3;
      double gapFeetFactor2 = gapFeetValue6 - gapFeetValue4;
      double gapFeetDivisor = gapFeetFactor * gapFeetFactor + gapFeetFactor2 * gapFeetFactor2;
      double gapFeetFactor3 = gapFeetDivisor < 1.0E-12 ? 0.0 : Math.max(0.0, Math.min(1.0, ((gapFeetValue - gapFeetValue3) * gapFeetFactor + (gapFeetValue2 - gapFeetValue4) * gapFeetFactor2) / gapFeetDivisor));
      return Math.hypot(gapFeetValue - (gapFeetValue3 + gapFeetFactor * gapFeetFactor3), gapFeetValue2 - (gapFeetValue4 + gapFeetFactor2 * gapFeetFactor3));
   }

   public static boolean isTraceable(AStarPathfinder.World world, Vec3d vec, Vec3d vec2, boolean gapFeetFlag, double gapFeetValue, int gapFeetValue2) {
      return !Double.isNaN(traceGroundHeight(world, vec, vec2, gapFeetFlag, gapFeetValue, gapFeetValue2));
   }

   public static double getGroundY(AStarPathfinder.World world, double gapFeetValue, double hashRegistryValue, double groundY, int gapFeetBound) {
      return AStarPathfinder.findGroundY(world, (int)Math.floor(gapFeetValue), (int)Math.floor(hashRegistryValue), groundY, 1.0, Math.max(0, gapFeetBound));
   }

   public void reset() {
      this.currentPath = null;
      this.currentNodeIndex = -1;
   }

   public static double traceGroundHeight(AStarPathfinder.World world, Vec3d vec, Vec3d vec2, boolean gapFeetFlag, double gapFeetBound, int gapFeetValue) {
      if (world != null && vec != null && vec2 != null && isFinitePosition(vec) && isFinitePosition(vec2)) {
         double deltaX = vec2.x - vec.x;
         double deltaZ = vec2.z - vec.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         if (hypot < 1.0E-6) {
            double groundY = getGroundY(world, vec.x, vec.z, vec.y, gapFeetValue);
            return Double.isFinite(groundY) ? groundY : vec.y;
         } else {
            double minAllowJump = Math.min(hypot, gapFeetBound);
            int gapFeetLimit = Math.max(1, (int)Math.ceil(minAllowJump / 0.4));
            double y = vec.y;
            double x = vec.x;
            double z = vec.z;
            long packPosKey = AStarPathfinder.packPosKey((int)Math.floor(vec.x), AStarPathfinder.toBlockY(vec.y), (int)Math.floor(vec.z));

            for (int i = 1; i <= gapFeetLimit; i++) {
               double gapFeetFactor = minAllowJump * i / gapFeetLimit / hypot;
               double gapFeetValue4 = vec.x + deltaX * gapFeetFactor;
               double gapFeetValue5 = vec.z + deltaZ * gapFeetFactor;
               double groundY6 = getGroundY(world, gapFeetValue4, gapFeetValue5, y, gapFeetValue);
               if (!Double.isFinite(groundY6)) {
                  return Double.NaN;
               }

               double deltaVar = groundY6 - y;
               if (deltaVar > 1.001) {
                  return Double.NaN;
               }

               if (deltaVar > 0.601) {
                  if (!gapFeetFlag) {
                     return Double.NaN;
                  }

                  if (world.hasCollision((int)Math.floor(x), AStarPathfinder.toBlockY(y) + 2, (int)Math.floor(z))) {
                     return Double.NaN;
                  }
               }

               int hashRegistryValue = (int)Math.floor(gapFeetValue4);
               int blockY = AStarPathfinder.toBlockY(groundY6);
               int z2 = (int)Math.floor(gapFeetValue5);
               long packPosKey10 = AStarPathfinder.packPosKey(hashRegistryValue, blockY, z2);
               if (packPosKey10 != packPosKey) {
                  packPosKey = packPosKey10;
                  if ((world.getBlockFlags(hashRegistryValue, blockY, z2) & 1309) != 0) {
                     return Double.NaN;
                  }
               }

               y = groundY6;
               x = gapFeetValue4;
               z = gapFeetValue5;
            }

            return y;
         }
      } else {
         return Double.NaN;
      }
   }

   private boolean shouldJump(
      AStarPathfinder.Node node,
      AStarPathfinder.Node node2,
      Vec3d vec,
      Vec3d vec2,
      boolean gapFeetFlag,
      boolean gapFeetFlag2,
      boolean gapFeetFlag3,
      AStarPathfinder.World world
   ) {
      if (gapFeetFlag3 && node2.feetY() > node.feetY() + 0.001) {
         return true;
      } else if (gapFeetFlag && !hasCollisionAboveHead(world, vec)) {
         AStarPathfinder.Move move = node2.move();
         if (move == AStarPathfinder.Move.JUMP_UP) {
            return gapFeetFlag2 || getDistanceToEntryEdge(vec, node, node2) <= 0.45000000100000004;
         } else if (move != AStarPathfinder.Move.GAP1 && move != AStarPathfinder.Move.GAP2) {
            return false;
         } else {
            int signum = Integer.signum(node2.x() - node.x());
            int signum2 = Integer.signum(node2.z() - node.z());
            double gapFeetLimit = vec2 == null ? 0.0 : vec2.x * signum + vec2.z * signum2;
            double distanceToExitEdge = getDistanceToExitEdge(vec, node, signum, signum2);
            return distanceToExitEdge > 0.250000001 ? false : gapFeetLimit >= 0.199999999 || distanceToExitEdge <= 0.08 && gapFeetLimit > -0.001;
         }
      } else {
         return false;
      }
   }

   static double getDistanceToExitEdge(Vec3d vec, AStarPathfinder.Node node, int gapFeetLimit, int gapFeetLimit2) {
      if (gapFeetLimit > 0) {
         return node.x() + 1.0 - vec.x;
      } else if (gapFeetLimit < 0) {
         return vec.x - node.x();
      } else if (gapFeetLimit2 > 0) {
         return node.z() + 1.0 - vec.z;
      } else {
         return gapFeetLimit2 < 0 ? vec.z - node.z() : Double.POSITIVE_INFINITY;
      }
   }

   private static boolean hasReachedNode(AStarPathfinder.Node node, Vec3d vec, boolean feetDropFlag) {
      double hypot = Math.hypot(node.centerX() - vec.x, node.centerZ() - vec.z);
      return hypot <= 0.35 && vec.y >= node.feetY() - 0.6
         ? true
         : node.move() == AStarPathfinder.Move.DROP && feetDropFlag && vec.y <= node.feetY() + 0.1 && hypot <= 1.0;
   }

   public List<AStarPathfinder.Node> getPath() {
      return this.currentPath;
   }

   private void advanceNodeIndex(List<AStarPathfinder.Node> list, Vec3d vec, boolean gapFeetFlag) {
      int ongroundSize = list.size();

      while (this.currentNodeIndex < ongroundSize) {
         if (hasReachedNode((AStarPathfinder.Node)list.get(this.currentNodeIndex), vec, gapFeetFlag)) {
            this.currentNodeIndex++;
         } else {
            double feet = getSegmentCost(list, this.currentNodeIndex, vec);
            int value = this.currentNodeIndex;
            double gapFeetValue = feet - 0.3;

            for (int i = this.currentNodeIndex + 1; i < ongroundSize && i < this.currentNodeIndex + 4; i++) {
               double segmentCost = getSegmentCost(list, i, vec);
               if (segmentCost < gapFeetValue - 1.0E-9) {
                  gapFeetValue = segmentCost;
                  value = i;
               }
            }

            if (value == this.currentNodeIndex) {
               return;
            }

            this.currentNodeIndex = value;
         }
      }
   }

   private static boolean isFinitePosition(Vec3d vec) {
      return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
   }

   static double getDistanceToEntryEdge(Vec3d vec, AStarPathfinder.Node node, AStarPathfinder.Node node2) {
      int signum = Integer.signum(node2.x() - node.x());
      int signum2 = Integer.signum(node2.z() - node.z());
      if (signum > 0) {
         return node2.x() - vec.x;
      } else if (signum < 0) {
         return vec.x - (node2.x() + 1.0);
      } else if (signum2 > 0) {
         return node2.z() - vec.z;
      } else {
         return signum2 < 0 ? vec.z - (node2.z() + 1.0) : Double.POSITIVE_INFINITY;
      }
   }

   public static boolean canWalkStraightTo(AStarPathfinder.World world, Vec3d vec, Vec3d vec2, boolean gapFeetFlag, double gapFeetValue, int gapFeetValue2) {
      double vecHashValue = traceGroundHeight(world, vec, vec2, gapFeetFlag, gapFeetValue, gapFeetValue2);
      if (Double.isNaN(vecHashValue)) {
         return false;
      } else {
         return Math.hypot(vec2.x - vec.x, vec2.z - vec.z) > gapFeetValue ? true : Math.abs(vecHashValue - vec2.y) <= 0.601;
      }
   }

   private static boolean isPreciseMove(AStarPathfinder.Move move) {
      return move == AStarPathfinder.Move.DROP || move == AStarPathfinder.Move.GAP1 || move == AStarPathfinder.Move.GAP2 || move == AStarPathfinder.Move.JUMP_UP;
   }

   public int getCurrentNodeIndex() {
      return this.currentNodeIndex;
   }

   private static double getSegmentCost(List<AStarPathfinder.Node> list, int index, Vec3d vec) {
      AStarPathfinder.Node node = (AStarPathfinder.Node)list.get(index - 1);
      AStarPathfinder.Node aStarPathfinderNode = (AStarPathfinder.Node)list.get(index);
      return distanceToSegment(vec.x, vec.z, node.centerX(), node.centerZ(), aStarPathfinderNode.centerX(), aStarPathfinderNode.centerZ())
         + 0.5 * Math.min(Math.abs(vec.y - node.feetY()), Math.abs(vec.y - aStarPathfinderNode.feetY()));
   }

   private int getLookaheadIndex(List<AStarPathfinder.Node> list, Vec3d vec, AStarPathfinder.World world, int feetBound) {
      int pathSize = list.size();
      if (isPreciseMove(((AStarPathfinder.Node)list.get(this.currentNodeIndex)).move())) {
         return this.currentNodeIndex;
      } else {
         int feetValue = this.currentNodeIndex;
         int index = this.currentNodeIndex + 1;

         while (index < pathSize && index < this.currentNodeIndex + 4 && !isPreciseMove(((AStarPathfinder.Node)list.get(index)).move())) {
            feetValue = index++;
         }

         int gapFeetValue = Math.min(Math.max(0, feetBound), 1);

         for (int i = feetValue; i > this.currentNodeIndex; i--) {
            AStarPathfinder.Node aStarPathfinderNode = (AStarPathfinder.Node)list.get(i);
            if (canWalkStraightTo(world, vec, new Vec3d(aStarPathfinderNode.centerX(), aStarPathfinderNode.feetY(), aStarPathfinderNode.centerZ()), true, 16.0, gapFeetValue)) {
               return i;
            }
         }

         return this.currentNodeIndex;
      }
   }

   private static boolean hasCollisionAboveHead(AStarPathfinder.World world, Vec3d vec) {
      return world == null ? false : world.hasCollision((int)Math.floor(vec.x), AStarPathfinder.toBlockY(vec.y) + 2, (int)Math.floor(vec.z));
   }

   public PathFollower.Cmd computeCommand(
      List<AStarPathfinder.Node> items, Vec3d vec, Vec3d vec2, boolean gapFlag, boolean gapFlag2, boolean gapFlag3, AStarPathfinder.World world, int gapValue
   ) {
      if (items != null && !items.isEmpty() && vec != null) {
         int collidedSize = items.size();
         if (items != this.currentPath || this.currentNodeIndex < 0) {
            this.currentPath = items;
            this.currentNodeIndex = findStartIndex(items, vec);
         }

         this.advanceNodeIndex(items, vec, gapFlag);
         if (this.currentNodeIndex >= collidedSize) {
            return new PathFollower.Cmd(false, 0.0, 0.0, false, false, true, false, collidedSize, 0.0, null);
         } else {
            AStarPathfinder.Node aStarPathfinderNode = (AStarPathfinder.Node)items.get(this.currentNodeIndex - 1);
            AStarPathfinder.Node node = (AStarPathfinder.Node)items.get(this.currentNodeIndex);
            double gapLimit = distanceToSegment(vec.x, vec.z, aStarPathfinderNode.centerX(), aStarPathfinderNode.centerZ(), node.centerX(), node.centerZ());
            boolean gapFlag4 = gapLimit > 1.5 || vec.y < node.feetY() - 2.5;
            double hypot = Math.hypot(node.centerX() - vec.x, node.centerZ() - vec.z);

            for (int i = this.currentNodeIndex; i + 1 < collidedSize; i++) {
               AStarPathfinder.Node collided4 = (AStarPathfinder.Node)items.get(i);
               AStarPathfinder.Node collided5 = (AStarPathfinder.Node)items.get(i + 1);
               hypot += Math.hypot(collided5.centerX() - collided4.centerX(), collided5.centerZ() - collided4.centerZ());
            }

            int moveFeet = this.getLookaheadIndex(items, vec, world, gapValue);
            AStarPathfinder.Node collided6 = (AStarPathfinder.Node)items.get(moveFeet);
            double deltaX = collided6.centerX() - vec.x;
            double deltaZ = collided6.centerZ() - vec.z;
            double hypot2 = Math.hypot(deltaX, deltaZ);
            boolean gapFlag5 = hypot2 > 0.001;
            double gapValue4 = gapFlag5 ? deltaX / hypot2 : 0.0;
            double gapValue5 = gapFlag5 ? deltaZ / hypot2 : 0.0;
            boolean gapFeet = this.shouldJump(aStarPathfinderNode, node, vec, vec2, gapFlag, gapFlag2, gapFlag3, world);
            boolean gapFlag6 = gapFlag5 && (hypot >= 2.0 || node.move() == AStarPathfinder.Move.GAP2);
            if (gapFlag6 && node.move() == AStarPathfinder.Move.DROP && Math.hypot(node.centerX() - vec.x, node.centerZ() - vec.z) < 1.5) {
               gapFlag6 = false;
            }

            return new PathFollower.Cmd(gapFlag5, gapValue4, gapValue5, gapFeet, gapFlag6, false, gapFlag4, this.currentNodeIndex, hypot, node.move());
         }
      } else {
         return new PathFollower.Cmd(false, 0.0, 0.0, false, false, true, false, 0, 0.0, null);
      }
   }

   public record Cmd(
      boolean move, double dx, double dz, boolean jump, boolean sprint, boolean done, boolean offPath, int index, double remaining, AStarPathfinder.Move next
   ) {
   }
}
