package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosHelperBlocked;
import com.corz.client.inventory.BlockPosHelperBlocked.Result;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BlockStateUtilsDirections.Hole;
import com.corz.client.world.ClientInstance;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;

public final class ObsidianSurroundPlacer {
   private int placedBlockCount;
   private final Map<Long, Long> blacklistExpiryByPos;
   private boolean placementInProgress;
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private String idle;
   private final ClientInstance clientInstance;
   private long surroundStartTime;
   private boolean currentPlacementActed;
   private final BlockPosHelperBlocked blockPlacer;
   private final Deque<BlockPos> pendingPositions = new ArrayDeque<>();
   private final HotbarSlotSwitcher inventorySelected;
   private ObsidianSurroundPlacer.Kind surroundBaseType;
   private boolean surroundActive;

   private BlockPos getNextPlaceablePos(ClientWorld world) {
      while (!this.pendingPositions.isEmpty()) {
         BlockPos peekFirst = this.pendingPositions.peekFirst();
         if (BlockPosUtilsRespawn.canReplaceableAir(peekFirst) && !hasEndCrystalAt(world, peekFirst) && !this.isPosBlacklisted(peekFirst)) {
            return peekFirst;
         }

         this.pendingPositions.pollFirst();
      }

      return null;
   }

   private static Vec3d getThreatFocusPoint(SelfPopData selfPopData, VecBlock.Threat threat) {
      if (threat.liveCenter() != null) {
         return threat.liveCenter();
      } else if (threat.worstBase() != null) {
         return BlockEntityUtils.getVec3d(threat.worstBase());
      } else {
         VecBlockingData target = selfPopData.target();
         if (target != null) {
            return target.serverPos() != null ? target.serverPos() : target.pos();
         } else {
            return null;
         }
      }
   }

   public ObsidianSurroundPlacer(ClientInstance clientInstance, HotbarSlotSwitcher inventorySelected) {
      this.blacklistExpiryByPos = new HashMap<>();
      this.idle = "idle";
      this.clientInstance = clientInstance;
      this.inventorySelected = inventorySelected;
      this.blockPlacer = new BlockPosHelperBlocked(clientInstance);
   }

   private static boolean hasSolidNeighbor(ClientWorld world, BlockPos pos) {
      for (Direction direction : Direction.values()) {
         if (direction != Direction.UP) {
            BlockPos offset = pos.offset(direction);
            if (!world.getBlockState(offset).getCollisionShape(world, offset).isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   public String getStatusText() {
      return this.idle;
   }

   public void tickSurround() {
      if (this.surroundActive) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         ClientWorld world = instance.world;
         if (player != null && world != null) {
            if (ClientThreadUtils.getTime() - this.surroundStartTime > 15L) {
               this.idle = "timeout";
               if (this.placementInProgress && this.currentPlacementActed) {
                  this.blacklistPos(this.pendingPositions.peekFirst());
               }

               this.resetSurround();
            } else {
               int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.OBSIDIAN), false);
               if (selectedSlot < 0) {
                  this.idle = "no obsidian";
                  this.resetSurround();
               } else {
                  for (int i = 0; i < 6; i++) {
                     if (!this.placementInProgress && FloorMathUtilsBlock.canEmpty(world)) {
                        this.idle = "wait ack";
                        return;
                     }

                     if (!this.placementInProgress) {
                        BlockPos peekFirst = this.getNextPlaceablePos(world);
                        if (peekFirst == null) {
                           this.idle = "done";
                           this.finishSurround();
                           return;
                        }

                        int ackSettleValue = 4 + Math.round(EntityDamageUtils.getCachedHurtTimeLimit() / 50.0F);
                        if (!this.blockPlacer.canImmutableFlag(peekFirst, selectedSlot, Blocks.OBSIDIAN.getDefaultState(), null, ackSettleValue)) {
                           this.pendingPositions.pollFirst();
                           continue;
                        }

                        this.placementInProgress = true;
                        this.currentPlacementActed = false;
                     }

                     Result blockedHit = this.blockPlacer.getBlockedHit();
                     if (blockedHit == Result.DONE) {
                        this.placedBlockCount++;
                        this.pendingPositions.pollFirst();
                        this.placementInProgress = false;
                        this.idle = "settle";
                        return;
                     }

                     if (blockedHit != Result.FAILED) {
                        if (blockedHit == Result.ACTED) {
                           this.currentPlacementActed = true;
                        }

                        this.idle = blockedHit.name().toLowerCase(Locale.ROOT);
                        return;
                     }

                     this.idle = this.blockPlacer.getText() == null ? "failed" : this.blockPlacer.getText();
                     this.blacklistPos(this.pendingPositions.pollFirst());
                     this.placementInProgress = false;
                     this.blockPlacer.updateTextImmutableText();
                  }
               }
            }
         } else {
            this.resetSurround();
         }
      }
   }

   private static boolean hasEnclosedHoleNearby(ClientWorld world, BlockPos pos) {
      for (Hole hole : BlockStateUtilsDirections.getDownSides(world, pos, 3, 4)) {
         if (!hole.feet().equals(pos) && hole.openSides() == 0) {
            return true;
         }
      }

      return false;
   }

   private void finishSurround() {
      this.blockPlacer.updateTextImmutableText();
      this.pendingPositions.clear();
      this.surroundActive = false;
      this.placementInProgress = false;
   }

   private void blacklistPos(BlockPos pos) {
      if (pos != null) {
         this.blacklistExpiryByPos.put(pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
      }
   }

   private boolean isPosBlacklisted(BlockPos pos) {
      return pos != null && containsUnexpiredKey(this.blacklistExpiryByPos, pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis());
   }

   public int getPlacedBlockCount() {
      return this.placedBlockCount;
   }

   private static boolean hasEndCrystalAt(ClientWorld world, BlockPos pos) {
      return !world.getEntitiesByClass(EndCrystalEntity.class, BlockEntityUtils.getBoxForBlockPos(pos), endCrystalEntity -> endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved()).isEmpty();
   }

   private static boolean isWithinInteractionRange(ClientPlayerEntity player, BlockPos pos) {
      return Vec3d.ofCenter(pos).distanceTo(player.getEyePos()) <= BlockPosUtilsRespawn.getRangeInteraction() + 0.5;
   }

   public boolean isSurroundActive() {
      return this.surroundActive;
   }

   static boolean containsUnexpiredKey(Map<Long, Long> map, long obsidianIdleValue, long obsidianIdleLimit) {
      if (map.isEmpty()) {
         return false;
      } else {
         var iterator = map.entrySet().iterator();

         while (iterator.hasNext()) {
            if (obsidianIdleLimit >= (Long)((Entry)iterator.next()).getValue()) {
               iterator.remove();
            }
         }

         return map.containsKey(obsidianIdleValue);
      }
   }

   static boolean isNonCarefulPlaystyle(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return false;
      } else {
         CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
         return carefulBalancedType != CarefulBalancedType.CAREFUL;
      }
   }

   private ObsidianSurroundPlacer.Plan computePlacementPlan(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      if (player != null && world != null && selfPopData != null && selfPopData.self() != null) {
         int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.OBSIDIAN), false);
         if (selectedSlot < 0) {
            this.idle = "no obsidian";
            return null;
         } else if (!player.canModifyBlocks()) {
            this.idle = "can't build";
            return null;
         } else {
            VecBlock.Threat threat2 = selfPopData.threat() == null ? VecBlock.Threat.INSTANCE : selfPopData.threat();
            if (selfPopData.lastPosture() == DefensiveAggressiveType.DEFENSIVE
               && threat2.worstBase() != null
               && threat2.worstBaseDamage() >= 8.0F
               && threat2.secondBaseDamage() <= 0.5F * threat2.worstBaseDamage()) {
               BlockPos up = threat2.worstBase().up();
               if (!this.isPosBlacklisted(up)
                  && BlockPosUtilsRespawn.canReplaceableAir(up)
                  && !hasEndCrystalAt(world, up)
                  && world.canPlace(Blocks.OBSIDIAN.getDefaultState(), up, ShapeContext.absent())
                  && isWithinInteractionRange(player, up)) {
                  return new ObsidianSurroundPlacer.Plan(ObsidianSurroundPlacer.Kind.BLOCK_BASE, List.of(up.toImmutable()));
               }
            }

            AbsorptionPosData self = selfPopData.self();
            Vec3d vec3d = self.velocity() == null ? Vec3d.ZERO : self.velocity();
            if (!(Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z) >= 0.05) && self.onGround()) {
               BlockPos floored = BlockPos.ofFloored(self.pos());
               if (hasEnclosedHoleNearby(world, floored)) {
                  this.idle = "hole nearby";
                  return null;
               } else {
                  var downEntities = this.getSurroundCellsToFill(world, player, floored);
                  if (downEntities.isEmpty()) {
                     this.idle = "closed";
                     return null;
                  } else {
                     var arrayList = new ArrayList<BlockPos>(downEntities.size());

                     for (BlockPos blockPos : downEntities) {
                        if (!this.isPosBlacklisted(blockPos)) {
                           arrayList.add(blockPos);
                        }
                     }

                     if (arrayList.isEmpty()) {
                        this.idle = "blacklisted";
                        return null;
                     } else if (self.obsidian() < arrayList.size()) {
                        this.idle = "few obsidian";
                        return null;
                     } else {
                        Vec3d worstLive = getThreatFocusPoint(selfPopData, threat2);
                        if (worstLive != null) {
                           arrayList.sort(Comparator.comparingDouble(threat -> Vec3d.ofCenter(threat).squaredDistanceTo(worstLive)));
                        }

                        return new ObsidianSurroundPlacer.Plan(ObsidianSurroundPlacer.Kind.SURROUND, arrayList);
                     }
                  }
               }
            } else {
               this.idle = "moving";
               return null;
            }
         }
      } else {
         this.idle = "no player";
         return null;
      }
   }

   private double getPlaystyleWeightedThreatDamage(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return 0.0;
      } else {
         VecBlock.Threat threat = selfPopData.threat() == null ? VecBlock.Threat.INSTANCE : selfPopData.threat();
         CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
         SelfHazData selfHaz = SelfHazData.getSelfHaz(selfPopData.lastPosture(), carefulBalancedType);
         return selfHaz.def() * threat.worstBaseDamage() * 0.6;
      }
   }

   public boolean tryStartSurround(SelfPopData selfPopData) {
      if (this.surroundActive) {
         return false;
      } else if (isNonCarefulPlaystyle(selfPopData)) {
         this.idle = "style";
         return false;
      } else {
         ObsidianSurroundPlacer.Plan obsidianIdle = this.computePlacementPlan(selfPopData);
         if (obsidianIdle == null) {
            return false;
         } else {
            this.pendingPositions.clear();
            this.pendingPositions.addAll(obsidianIdle.cells);
            this.surroundBaseType = obsidianIdle.kind;
            this.surroundActive = true;
            this.placementInProgress = false;
            this.surroundStartTime = ClientThreadUtils.getTime();
            this.placedBlockCount = 0;
            this.idle = "start";
            return true;
         }
      }
   }

   public void resetSurround() {
      this.blockPlacer.updateTextImmutableText();
      this.pendingPositions.clear();
      this.surroundActive = false;
      this.placementInProgress = false;
      this.surroundBaseType = null;
   }

   public ObsidianSurroundPlacer.Kind getSurroundBaseType() {
      return this.surroundBaseType;
   }

   private List<BlockPos> getSurroundCellsToFill(ClientWorld world, ClientPlayerEntity player, BlockPos pos) {
      var arrayList = new ArrayList(4);
      var stateAbsent = BlockStateUtilsDirections.getStateAbsent(world, pos);
      Box boundingBox = player.getBoundingBox();

      for (Direction direction : HORIZONTAL_DIRECTIONS) {
         BlockPos immutable = pos.offset(direction).toImmutable();
         BlockState blockState = world.getBlockState(immutable.down());
         boolean downEntitiesFlag = !blockState.getCollisionShape(world, immutable.down()).isEmpty() || hasSolidNeighbor(world, immutable);
         if (downEntitiesFlag) {
            if (stateAbsent.contains(immutable)) {
               arrayList.add(immutable);
            } else if (BlockPosUtilsRespawn.canReplaceableAir(immutable) && !hasEndCrystalAt(world, immutable)) {
               Box box = BlockEntityUtils.getBoxForBlockPos(immutable);
               if (box.intersects(boundingBox)) {
                  boolean downEntitiesFlag2 = false;
                  Iterator iterator = world.getOtherEntities(player, box, entity -> entity.isAlive() && !entity.isSpectator() && !(entity instanceof ItemEntity)).iterator();
                  if (iterator.hasNext()) {
                     Entity next2 = (Entity)iterator.next();
                     downEntitiesFlag2 = true;
                  }

                  if (!downEntitiesFlag2) {
                     arrayList.add(immutable);
                  }
               }
            }
         }
      }

      return arrayList;
   }

   public CaseMaxData getSurroundCase(SelfPopData selfPopData) {
      if (this.surroundActive) {
         return new CaseMaxData(SurroundKitType.SURROUND, true, this.getPlaystyleWeightedThreatDamage(selfPopData), 0, false, false, "busy");
      } else if (isNonCarefulPlaystyle(selfPopData)) {
         this.idle = "style";
         return CaseMaxData.getCaseMaxData(SurroundKitType.SURROUND, this.idle);
      } else {
         ObsidianSurroundPlacer.Plan obsidianIdle = this.computePlacementPlan(selfPopData);
         return obsidianIdle == null
            ? CaseMaxData.getCaseMaxData(SurroundKitType.SURROUND, this.idle)
            : new CaseMaxData(
               SurroundKitType.SURROUND, true, this.getPlaystyleWeightedThreatDamage(selfPopData), 2 * obsidianIdle.cells.size() + 1, false, false, obsidianIdle.kind.name().toLowerCase(Locale.ROOT)
            );
      }
   }

   public void resetAll() {
      this.resetSurround();
      this.blacklistExpiryByPos.clear();
      this.placedBlockCount = 0;
      this.idle = "idle";
   }

   public static enum Kind {
      SURROUND,
      BLOCK_BASE;
   }

   private record Plan(ObsidianSurroundPlacer.Kind kind, List<BlockPos> cells) {
   }
}
