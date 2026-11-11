package com.corz.client.world;

import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.BowGappleType;

public final class BlockPosHelperGapple {
   private long lastObsidianGlowstoneTick;
   private final Map<Long, BlockPosHelperGapple.Tracked> glwChgTrackedMap = new HashMap<>();
   private int activeAnchorCount;
   private final Map<Long, Long> flooredPositionMap;
   private final Map<Long, Long> blockPosPositionMap = new HashMap<>();
   private final Map<Integer, BlockPosHelperGapple.Foe> bowGappleFoeMap;
   private long flooredImmutableValue;
   private World world;
   private List<HimEtaData> copyOf;
   private BlockPos floored;
   private HimEtaData posHimEtaData;
   private final Map<Integer, Long> bowGapplePositionMap;
   private int builderCount;
   private final ArrayDeque<BlockPosHelperGapple.BoomRec> arrayDeque;
   private long latestTick;
   private final StringBuilder stringBuilder;
   private final RayBudget timeLimiter;

   void handleBowGapple(long time, List<VecBlockingData> list) {
      this.latestTick = Math.max(this.latestTick, time);

      for (BlockPosHelperGapple.Foe foe : this.bowGappleFoeMap.values()) {
         foe.bowGappleActive = false;
      }

      if (list != null) {
         for (VecBlockingData vecBlockingData : list) {
            if (vecBlockingData != null) {
               BlockPosHelperGapple.Foe ifAbsent = this.bowGappleFoeMap.computeIfAbsent(vecBlockingData.id(), BlockPosHelperGapple.Foe::new);
               ifAbsent.bowGappleActive = true;
               ifAbsent.bowGappleValue = time;
               ifAbsent.vecVec3d = getAdd(vecBlockingData);
               ifAbsent.bowGappleBowGappleType = vecBlockingData.main() == null ? BowGappleType.EMPTY : vecBlockingData.main();
               ifAbsent.bowGappleType = vecBlockingData.off() == null ? BowGappleType.EMPTY : vecBlockingData.off();
               if (canGlowstoneAnchor(ifAbsent.bowGappleBowGappleType) || canGlowstoneAnchor(ifAbsent.bowGappleType)) {
                  ifAbsent.lastBowTick = time;
               }
            }
         }
      }

      this.bowGappleFoeMap.values().removeIf(foe2 -> !foe2.bowGappleActive && !canFlag(time, foe2.bowGappleValue, 600));
      this.bowGapplePositionMap.values().removeIf(tickx -> time - tickx > 12L);
   }

   private static Vec3d getAdd(VecBlockingData vecBlockingData) {
      if (vecBlockingData.eye() != null) {
         return vecBlockingData.eye();
      } else {
         Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
         return vec3d == null ? null : vec3d.add(0.0, 1.62, 0.0);
      }
   }

   private BlockPosHelperGapple.Eye getVecFlag(BlockPosHelperGapple.Tracked tracked) {
      if (tracked.foeEntityId != -1) {
         BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(tracked.foeEntityId);
         if (foe != null && foe.bowGappleActive && foe.vecVec3d != null) {
            return new BlockPosHelperGapple.Eye(foe.vecVec3d, canBowGapple(foe));
         }
      }

      Vec3d vec3d = null;
      boolean vecFlag = false;
      double vecLimit = Double.MAX_VALUE;

      for (BlockPosHelperGapple.Foe foe2 : this.bowGappleFoeMap.values()) {
         if (foe2.bowGappleActive && foe2.vecVec3d != null) {
            double vecValue = getAmount(foe2.vecVec3d, tracked.blockPos);
            if (vecValue < vecLimit) {
               vecLimit = vecValue;
               vec3d = foe2.vecVec3d;
               vecFlag = canBowGapple(foe2);
            }
         }
      }

      return new BlockPosHelperGapple.Eye(vec3d, vecFlag);
   }

   static float getAmount(float blockPosBound, int blockPosDivisor, int blockPosValue) {
      return blockPosDivisor < 3 ? blockPosBound : Math.max(blockPosBound, Math.min(0.95F, (float)blockPosValue / blockPosDivisor));
   }

   public void updateFlooredTime() {
      this.glwChgTrackedMap.clear();
      this.blockPosPositionMap.clear();
      this.flooredPositionMap.clear();
      this.bowGapplePositionMap.clear();
      this.bowGappleFoeMap.clear();
      this.arrayDeque.clear();
      this.stringBuilder.setLength(0);
      this.builderCount = 0;
      this.copyOf = List.of();
      this.posHimEtaData = HimEtaData.INSTANCE;
      this.latestTick = Long.MIN_VALUE;
      this.floored = null;
      this.flooredImmutableValue = Long.MIN_VALUE;
      this.lastObsidianGlowstoneTick = Long.MIN_VALUE;
      this.world = null;
      this.activeAnchorCount = 0;
   }

   public int getFlag() {
      int blockPosCounter = 0;

      for (BlockPosHelperGapple.Tracked tracked : this.glwChgTrackedMap.values()) {
         if (!tracked.glwChgActive) {
            blockPosCounter++;
         }
      }

      return blockPosCounter;
   }

   private static float getDistance(Vec3d vec, Vec3d vec2, int distanceValue) {
      float posBlockValue = (float)BoxLengthUtils.getAmount3(vec.distanceTo(vec2), 1.0, 5.0);
      return PosBlockUtilsBonus.getAmount(posBlockValue, distanceValue);
   }

   private void handleAncTime(BlockPos pos, int ancLimit, long time, long time2, boolean ancFlag, long time3) {
      BlockPosHelperGapple.Tracked tracked = new BlockPosHelperGapple.Tracked(pos, ancLimit, time2);
      tracked.xancTimeAnc = time2;
      if (ancLimit > 0 && ancFlag) {
         tracked.xancTimeGlwChg = time;
      }

      this.glwChgTrackedMap.put(pos.asLong(), tracked);
      if (time3 != Long.MIN_VALUE) {
         tracked.xancActive = true;
         tracked.ancValue = time3;
      } else {
         tracked.foeEntityId = getSwingCenter(pos, time, this.getVecFlag());
         this.activeAnchorCount++;
         if (tracked.foeEntityId != -1) {
            BlockPosHelperGapple.Foe ifAbsent = this.bowGappleFoeMap.computeIfAbsent(tracked.foeEntityId, BlockPosHelperGapple.Foe::new);
            ifAbsent.ancValue = time;
            if (ancFlag) {
               ifAbsent.ancCount++;
               tracked.ancActive = true;
            }
         }

         this.handleBuilder("+anc@", pos, "");
      }
   }

   public boolean canMaxTime(int blockPosValue) {
      BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(blockPosValue);
      return foe == null ? false : canFlag(this.latestTick, foe.lastBowTick, 300) || canFlag(this.latestTick, foe.ancValue, 300);
   }

   private void handleXancAmount(BlockPosHelperGapple.Tracked tracked) {
      if (tracked.ancActive && !tracked.xancActive && tracked.foeEntityId != -1) {
         BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(tracked.foeEntityId);
         if (foe != null) {
            long xancLimit = tracked.xancTime - tracked.xancValue;
            if (xancLimit >= 1L && xancLimit <= 8L) {
               foe.lastXancLimit = getAmount(foe.lastXancLimit, xancLimit);
               foe.xancHitCount++;
            }
         }
      }
   }

   private void handleObsidianGlowstone(BlockPos pos2, BlockState blockState, long time) {
      if (pos2 != null && blockState != null) {
         this.lastObsidianGlowstoneTick = time;
         int obsidianGlowstoneValue = -1;
         BlockPosHelperGapple.Kind kind;
         if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            kind = BlockPosHelperGapple.Kind.ANCHOR;
            obsidianGlowstoneValue = blockState.get(RespawnAnchorBlock.CHARGES);
         } else if (blockState.isOf(Blocks.GLOWSTONE)) {
            kind = BlockPosHelperGapple.Kind.GLOWSTONE;
         } else if (blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.CRYING_OBSIDIAN)) {
            kind = BlockPosHelperGapple.Kind.OBSIDIAN;
         } else if (blockState.isAir()) {
            kind = BlockPosHelperGapple.Kind.AIR;
         } else {
            kind = BlockPosHelperGapple.Kind.OTHER;
         }

         this.handleGlwChg(pos2, kind, obsidianGlowstoneValue, time);
      }
   }

   private static boolean canGlowstoneAnchor(BowGappleType bowGappleType) {
      return bowGappleType == BowGappleType.ANCHOR || bowGappleType == BowGappleType.GLOWSTONE;
   }

   private static String getFormat(float blockPosValue) {
      return !Float.isFinite(blockPosValue) ? "0" : String.format(Locale.ROOT, "%.1f", blockPosValue);
   }

   public int getValue() {
      return this.activeAnchorCount;
   }

   private void handleInstancePos(BlockPos pos) {
      ArrayList arrayList = new ArrayList(this.copyOf.size());

      for (HimEtaData himEtaData : this.copyOf) {
         if (!pos.equals(himEtaData.cell())) {
            arrayList.add(himEtaData);
         }
      }

      this.copyOf = List.copyOf(arrayList);
      this.posHimEtaData = this.copyOf.isEmpty() ? HimEtaData.INSTANCE : this.copyOf.get(0);
   }

   private void handleAmountPos(ClientPlayerEntity entity, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData, int posBound, int rocketsBuyBound, long time) {
      Vec3d vec3d = absorptionPosData.pos();
      Box box = absorptionPosData.box();
      PosBlockUtilsBonus.Armor armor = absorptionPosData.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : absorptionPosData.armor();
      PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(Math.max(0, rocketsBuyBound), absorptionPosData.lastScaledTaken());
      ArrayList arrayList = new ArrayList();

      for (BlockPosHelperGapple.Tracked tracked : this.glwChgTrackedMap.values()) {
         if (!tracked.glwChgActive && !canTimeFlag(tracked, time) && !(Vec3d.ofCenter(tracked.blockPos).distanceTo(vec3d) > 8.5)) {
            arrayList.add(tracked);
         }
      }

      if (arrayList.isEmpty()) {
         this.updateInstancePos();
      } else {
         arrayList.sort(Comparator.comparingDouble(feet -> Vec3d.ofCenter(((com.corz.client.world.BlockPosHelperGapple.Tracked) feet).blockPos).squaredDistanceTo(vec3d)));
         Vec3d vec3d2 = vecBlockingData == null ? null : (vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos());
         int value = vecBlockingData == null ? Integer.MIN_VALUE : vecBlockingData.id();
         PosBlockUtilsBonus.Armor armor2 = vecBlockingData != null && vecBlockingData.armor() != null ? vecBlockingData.armor() : PosBlockUtilsBonus.Armor.INSTANCE;
         PosBlockUtilsBonus.Window window2 = vecBlockingData == null
            ? PosBlockUtilsBonus.Window.INSTANCE
            : new PosBlockUtilsBonus.Window(Math.max(0, posBound), vecBlockingData.lastScaledTaken());
         ArrayList arrayList2 = new ArrayList(3);
         int posCounter = 0;

         for (BlockPosHelperGapple.Tracked tracked2 : (Iterable<com.corz.client.world.BlockPosHelperGapple.Tracked>) (arrayList)) {
            if (posCounter++ >= 3) {
               break;
            }

            Vec3d center = Vec3d.ofCenter(tracked2.blockPos);
            BlockPosHelperGapple.Eye vecFlag = this.getVecFlag(tracked2);
            boolean posFlag = tracked2.blockState >= 1;
            int blockPosBound = vecFlag.eye == null ? 99 : getValue(posFlag, getAmount(vecFlag.eye, tracked2.blockPos), vecFlag.glow);
            boolean posFlag2 = tracked2.foeEntityId != -1;
            BlockPosHelperGapple.Foe foe = posFlag2 ? this.bowGappleFoeMap.get(tracked2.foeEntityId) : null;
            boolean posFlag3 = posFlag2 && (foe == null || !foe.bowGappleActive || foe.vecVec3d == null || foe.vecVec3d.distanceTo(center) > 8.0);
            boolean posFlag4 = vecFlag.eye != null && getAmount(vecFlag.eye, tracked2.blockPos) <= 4.5;
            float posValue = getAmount(posFlag, posFlag4, vecFlag.glow, posFlag2, posFlag3);
            if (foe != null && foe.bowGappleActive) {
               blockPosBound = getValue(blockPosBound, posFlag, posFlag4, foe.xancHitCount, foe.lastXancLimit, time - tracked2.xancValue);
               if (!posFlag3) {
                  posValue = getAmount(posValue, foe.ancCount, foe.xancHitCount);
               }
            }

            if (this.canVecDistance(tracked2, vec3d, vec3d2, value, time)) {
               if (this.timeLimiter != null && !this.timeLimiter.tryReserveRays(45, 3)) {
                  tracked2.posActive = true;
               } else {
                  tracked2.creativeSpectator = BlockEntityUtils.getCreativeSpectator(entity, center, vec3d, box, null, tracked2.blockPos, 5.0F);
                  tracked2.lastTrackedPos = vec3d;
                  tracked2.lastPosTick = time;
                  tracked2.posActive = false;
                  if (vecBlockingData == null || vecBlockingData.entity() == null) {
                     tracked2.boxBounding = Float.NaN;
                     tracked2.entityReachLimit = Float.NaN;
                     tracked2.vec3d2Pos = null;
                     tracked2.posValue = value;
                  } else if (this.timeLimiter != null && !this.timeLimiter.tryReserveRays(45, 3)) {
                     tracked2.posActive = true;
                  } else {
                     int interpolatorLimit = Math.max(1, Math.min(8, blockPosBound));
                     BlockEntityUtils.Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(vecBlockingData.entity(), interpolatorLimit, false, true);
                     tracked2.boxBounding = EntityCenterUtils.getBoxBounding(vecBlockingData.entity(), interpolatorInstance, tracked2.blockPos, null);
                     tracked2.entityReachLimit = BlockEntityUtils.getAmount(vecBlockingData.entity(), interpolatorInstance.feet().distanceTo(center), 5.0F);
                     tracked2.vec3d2Pos = vec3d2;
                     tracked2.posValue = value;
                  }
               }
            }

            float toughnessEpfLimit = Float.isNaN(tracked2.creativeSpectator) ? getDistance(vec3d, center, armor.difficulty()) : tracked2.creativeSpectator;
            float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, armor);
            float readyInstance = PosBlockUtilsBonus.getReadyInstance(toughnessEpfLimit, armor, window, blockPosBound);
            boolean posFlag5 = vecBlockingData != null && tracked2.posValue == value && !Float.isNaN(tracked2.boxBounding);
            float posValue4 = posFlag5 ? PosBlockUtilsBonus.getReadyInstance(tracked2.boxBounding, armor2, window2, blockPosBound) : 0.0F;
            boolean posFlag6 = posFlag5 && tracked2.entityReachLimit > 0.0F && tracked2.boxBounding < 0.5F * tracked2.entityReachLimit;
            arrayList2.add(
               new HimEtaData(tracked2.blockPos, tracked2.blockState, toughnessEpfLimit, toughnessEpf, readyInstance, posValue4, blockPosBound, posValue, tracked2.foeEntityId, posFlag6, this.canBowGapple(tracked2.foeEntityId))
            );
         }

         arrayList2.sort(
            Comparator.<HimEtaData>comparingDouble(himEtaData -> himEtaData.getDetHit())
               .reversed()
               .thenComparing(Comparator.<HimEtaData>comparingDouble(himEtaData2 -> himEtaData2.hitUs()).reversed())
         );
         this.copyOf = List.copyOf(arrayList2);
         this.posHimEtaData = this.copyOf.isEmpty() ? HimEtaData.INSTANCE : this.copyOf.get(0);
      }
   }

   static float getAmount(boolean blockPosFlag, boolean blockPosFlag2, boolean blockPosFlag3, boolean blockPosFlag4, boolean blockPosFlag5) {
      if (blockPosFlag5) {
         return 0.1F;
      } else if (!blockPosFlag4) {
         return 0.5F;
      } else if (blockPosFlag) {
         return blockPosFlag2 ? 0.9F : 0.6F;
      } else {
         return blockPosFlag3 ? 0.8F : 0.6F;
      }
   }

   void handleXancDeque(long xancDequeLimit) {
      ArrayList arrayList = new ArrayList(this.arrayDeque.size());
      ArrayList arrayList2 = new ArrayList(this.arrayDeque.size());

      for (BlockPosHelperGapple.BoomRec boomRec : this.arrayDeque) {
         arrayList.add(boomRec.center());
         arrayList2.add(boomRec.tick());
      }

      Iterator iterator = this.glwChgTrackedMap.entrySet().iterator();

      while (iterator.hasNext()) {
         BlockPosHelperGapple.Tracked tracked = (BlockPosHelperGapple.Tracked)((Entry)iterator.next()).getValue();
         if (tracked.glwChgActive) {
            boolean xancDequeFlag = tracked.blockState >= 1 && canCenterDistance(tracked.blockPos, tracked.xancTime, arrayList, arrayList2);
            if (xancDequeFlag || xancDequeLimit > tracked.xancTime) {
               if (xancDequeFlag) {
                  this.handleXancAmount(tracked);
               }

               this.handleBuilder("xanc@", tracked.blockPos, xancDequeFlag ? ":d" : ":m");
               iterator.remove();
            }
         }
      }

      this.arrayDeque.removeIf(boomRec2 -> xancDequeLimit - boomRec2.tick() > 3L);
   }

   public boolean canFlag(BlockPos pos) {
      BlockPosHelperGapple.Tracked tracked = pos == null ? null : this.glwChgTrackedMap.get(pos.asLong());
      return tracked != null && !tracked.glwChgActive;
   }

   private void handleChgBlock(World world2, long xancTime) {
      Iterator iterator = this.glwChgTrackedMap.entrySet().iterator();

      while (iterator.hasNext()) {
         BlockPosHelperGapple.Tracked tracked = (BlockPosHelperGapple.Tracked)((Entry)iterator.next()).getValue();
         if (!tracked.glwChgActive) {
            if (this.floored != null && Vec3d.ofCenter(tracked.blockPos).distanceTo(Vec3d.ofCenter(this.floored)) > 12.0) {
               iterator.remove();
            } else {
               BlockState blockState = world2.getBlockState(tracked.blockPos);
               if (!blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                  tracked.glwChgActive = true;
                  tracked.xancTime = xancTime;

                  for (BlockPosHelperGapple.Tracked tracked2 : this.glwChgTrackedMap.values()) {
                     tracked2.posActive = true;
                  }
               } else {
                  int blockState2 = blockState.get(RespawnAnchorBlock.CHARGES);
                  if (blockState2 != tracked.blockState) {
                     if (blockState2 > tracked.blockState) {
                        tracked.xancTimeGlwChg = xancTime;
                     }

                     tracked.blockState = blockState2;
                     tracked.xancTimeAnc = xancTime;
                     tracked.posActive = true;
                     if (!canTimeFlag(tracked, xancTime)) {
                        this.handleBuilder("chg@", tracked.blockPos, ":" + blockState2);
                     }
                  }
               }
            }
         }
      }
   }

   List<BlockPos> getCopyOf(BlockPos pos) {
      BlockPosHelperGapple.Tracked tracked = pos == null ? null : this.glwChgTrackedMap.get(pos.asLong());
      return tracked == null ? List.of() : List.copyOf(tracked.glwChgBlockPosList);
   }

   int getValue(int blockPosValue) {
      BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(blockPosValue);
      return foe == null ? 0 : foe.ancCount;
   }

   private static boolean canTimeFlag(BlockPosHelperGapple.Tracked tracked, long blockPosValue) {
      return tracked.xancActive && canFlag(blockPosValue, tracked.ancValue, 2);
   }

   private boolean canTime(long blockPosValue) {
      return this.lastObsidianGlowstoneTick != Long.MIN_VALUE && blockPosValue - this.lastObsidianGlowstoneTick <= 200L;
   }

   private static boolean canBowGapple(BlockPosHelperGapple.Foe foe) {
      return foe.bowGappleBowGappleType == BowGappleType.GLOWSTONE || foe.bowGappleType == BowGappleType.GLOWSTONE;
   }

   public String formatHimFloored() {
      HimEtaData posHimEtaData2 = this.posHimEtaData;
      if (posHimEtaData2 != null && posHimEtaData2.isCell()) {
         StringBuilder stringBuilder = new StringBuilder(64);
         BlockPos blockPos = this.floored == null ? BlockPos.ORIGIN : this.floored;
         stringBuilder.append(posHimEtaData2.cell().getX() - blockPos.getX())
            .append(',')
            .append(posHimEtaData2.cell().getY() - blockPos.getY())
            .append(',')
            .append(posHimEtaData2.cell().getZ() - blockPos.getZ());
         stringBuilder.append(":c").append(posHimEtaData2.charges());
         stringBuilder.append(":u").append(getFormat(posHimEtaData2.hitUs()));
         stringBuilder.append(":f").append(getFormat(posHimEtaData2.fullUs()));
         stringBuilder.append(":h").append(getFormat(posHimEtaData2.dmgHim()));
         stringBuilder.append(":e").append(posHimEtaData2.eta());
         stringBuilder.append(":p").append(getFormat(posHimEtaData2.pDet()));
         stringBuilder.append(":s").append((char)(posHimEtaData2.shieldedFromHim() ? '1' : '0'));
         stringBuilder.append(":b").append((char)(posHimEtaData2.busy() ? '1' : '0'));
         return stringBuilder.toString();
      } else {
         return "-";
      }
   }

   public String formatBuilder() {
      String formatBuilder = this.stringBuilder.toString();
      this.stringBuilder.setLength(0);
      this.builderCount = 0;
      return formatBuilder;
   }

   public BlockPosHelperGapple(RayBudget max) {
      this.flooredPositionMap = new HashMap<>();
      this.bowGapplePositionMap = new HashMap<>();
      this.bowGappleFoeMap = new HashMap<>();
      this.arrayDeque = new ArrayDeque<>();
      this.stringBuilder = new StringBuilder();
      this.copyOf = List.of();
      this.posHimEtaData = HimEtaData.INSTANCE;
      this.latestTick = Long.MIN_VALUE;
      this.flooredImmutableValue = Long.MIN_VALUE;
      this.lastObsidianGlowstoneTick = Long.MIN_VALUE;
      this.timeLimiter = max;
   }

   public void handleMaxEntity(ServerPacketEventQueue.Event event, long entityBound, int entityValue) {
      if (event != null) {
         this.latestTick = Math.max(this.latestTick, entityBound);
         if (event instanceof ServerPacketEventQueue.BlockChange blockChange) {
            this.handleObsidianGlowstone(blockChange.pos(), blockChange.state(), entityBound);
         } else if (event instanceof ServerPacketEventQueue.Boom boom) {
            this.handleDequeArray(boom.center(), boom.radius(), entityBound);
         } else if (event instanceof ServerPacketEventQueue.Swing swing && swing.entityId() != entityValue) {
            this.bowGapplePositionMap.put(swing.entityId(), entityBound);
         }
      }
   }

   int getStateBlock(BlockPos pos) {
      BlockPosHelperGapple.Tracked tracked = pos == null ? null : this.glwChgTrackedMap.get(pos.asLong());
      return tracked != null && !tracked.glwChgActive ? tracked.blockState : -1;
   }

   int getValueForInt(int blockPosValue) {
      BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(blockPosValue);
      return foe == null ? 0 : foe.xancHitCount;
   }

   private void updateInstancePos() {
      this.copyOf = List.of();
      this.posHimEtaData = HimEtaData.INSTANCE;
   }

   private void handleBuilder(String builderText, BlockPos pos, String builderText2) {
      if (this.builderCount < 32) {
         if (!this.stringBuilder.isEmpty()) {
            this.stringBuilder.append(' ');
         }

         this.stringBuilder.append(builderText).append(pos.getX()).append(',').append(pos.getY()).append(',').append(pos.getZ()).append(builderText2);
         this.builderCount++;
      }
   }

   void handleGlwChg(BlockPos pos2, BlockPosHelperGapple.Kind kind, int glwChgBound, long time) {
      if (pos2 != null && kind != null) {
         this.latestTick = Math.max(this.latestTick, time);
         if (this.floored == null || !(Vec3d.ofCenter(pos2).distanceTo(Vec3d.ofCenter(this.floored)) > 12.0)) {
            BlockPos immutable = pos2.toImmutable();
            long long2 = immutable.asLong();

            for (BlockPosHelperGapple.Tracked tracked : this.glwChgTrackedMap.values()) {
               tracked.posActive = true;
            }

            BlockPosHelperGapple.Tracked tracked2 = this.glwChgTrackedMap.get(long2);
            if (kind == BlockPosHelperGapple.Kind.ANCHOR) {
               if (tracked2 != null && !tracked2.glwChgActive) {
                  int blockState = Math.max(0, glwChgBound);
                  if (blockState != tracked2.blockState) {
                     if (blockState > tracked2.blockState) {
                        tracked2.xancTimeGlwChg = time;
                     }

                     tracked2.blockState = blockState;
                     tracked2.xancTimeAnc = time;
                     if (!canTimeFlag(tracked2, time)) {
                        this.handleBuilder("chg@", immutable, ":" + blockState);
                     }
                  }
               } else {
                  this.handleAncTime(immutable, Math.max(0, glwChgBound), time, time, true, this.getTime(long2, time));
               }
            } else if (tracked2 != null && !tracked2.glwChgActive) {
               tracked2.glwChgActive = true;
               tracked2.xancTime = time;
            } else {
               if (kind == BlockPosHelperGapple.Kind.GLOWSTONE || kind == BlockPosHelperGapple.Kind.OBSIDIAN) {
                  if (canFlag(this.flooredPositionMap, long2, time)) {
                     return;
                  }

                  for (BlockPosHelperGapple.Tracked tracked3 : this.glwChgTrackedMap.values()) {
                     if (!tracked3.glwChgActive && !tracked3.xancActive && canFlag(tracked3.blockPos, immutable)) {
                        long glwChgValue = Math.max(tracked3.xancValue, Math.max(tracked3.xancTimeGlwChg, tracked3.xancTimeAnc));
                        if (time - glwChgValue <= 3L) {
                           if (!tracked3.glwChgBlockPosList.contains(immutable) && tracked3.glwChgBlockPosList.size() < 4) {
                              tracked3.glwChgBlockPosList.add(immutable);
                           }

                           this.handleBuilder(kind == BlockPosHelperGapple.Kind.GLOWSTONE ? "+glw@" : "+obs@", immutable, "");
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean canFlag(BlockPos pos, BlockPos pos2) {
      return !pos.equals(pos2) && Math.abs(pos.getX() - pos2.getX()) <= 1 && Math.abs(pos.getY() - pos2.getY()) <= 1 && Math.abs(pos.getZ() - pos2.getZ()) <= 1;
   }

   public void handleFlooredWorld(
      long max, AbsorptionPosData absorptionPosData, List<VecBlockingData> list, VecBlockingData vecBlockingData, int flooredWorldValue, int rocketsBuyBound
   ) {
      this.latestTick = Math.max(this.latestTick, max);
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      ClientPlayerEntity player = instance.player;
      if (world != null && player != null && absorptionPosData != null && absorptionPosData.pos() != null && absorptionPosData.box() != null) {
         if (world != this.world) {
            this.updateFlooredTime();
            this.world = world;
            this.latestTick = max;
         }

         this.floored = BlockPos.ofFloored(absorptionPosData.pos());
         this.handleBowGapple(max, list);
         this.handle(max);
         this.handleChgBlock(world, max);
         int flooredWorldValue3 = this.canTime(max) ? 10 : 2;
         if (this.flooredImmutableValue == Long.MIN_VALUE || max - this.flooredImmutableValue >= flooredWorldValue3 || max < this.flooredImmutableValue) {
            this.handleFlooredImmutable(world, max);
         }

         this.handleXancDeque(max);
         if (World.NETHER.equals(world.getRegistryKey())) {
            this.updateInstancePos();
         } else {
            this.handleAmountPos(player, absorptionPosData, vecBlockingData, flooredWorldValue, rocketsBuyBound, max);
         }
      } else {
         this.updateInstancePos();
      }
   }

   public HimEtaData getPosHimEtaData() {
      return this.posHimEtaData;
   }

   private void handleFlooredImmutable(World world2, long time) {
      long flooredImmutableValue2 = this.flooredImmutableValue;
      this.flooredImmutableValue = time;
      if (this.floored != null) {
         Mutable mutable = new Mutable();

         for (int i = -3; i <= 3; i++) {
            for (int j = -6; j <= 6; j++) {
               for (int k = -6; k <= 6; k++) {
                  mutable.set(this.floored.getX() + j, this.floored.getY() + i, this.floored.getZ() + k);
                  BlockState blockState = world2.getBlockState(mutable);
                  if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                     long long2 = mutable.asLong();
                     BlockPosHelperGapple.Tracked tracked = this.glwChgTrackedMap.get(long2);
                     if (tracked == null || tracked.glwChgActive) {
                        if (tracked != null) {
                           this.glwChgTrackedMap.remove(long2);
                        }

                        long flooredImmutableValue = flooredImmutableValue2 != Long.MIN_VALUE && flooredImmutableValue2 < time ? flooredImmutableValue2 + 1L : time - 4L - 1L;
                        this.handleAncTime(mutable.toImmutable(), blockState.get(RespawnAnchorBlock.CHARGES), time, flooredImmutableValue, false, this.getTime(long2, time));
                     }
                  }
               }
            }
         }
      }
   }

   static int getValue(boolean blockPosFlag, double blockPosValue, boolean blockPosFlag2) {
      if (!Double.isNaN(blockPosValue) && !Double.isInfinite(blockPosValue)) {
         long blockPosValue2 = blockPosValue <= 4.5 ? 0L : (long)Math.ceil((blockPosValue - 4.5) / 0.28);
         long blockPosBound = (blockPosFlag ? 1L : 2L + (blockPosFlag2 ? 0L : 1L)) + blockPosValue2;
         return (int)Math.min(99L, blockPosBound);
      } else {
         return 99;
      }
   }

   public void handleTimeFlag(BlockPos pos, long time) {
      if (pos != null) {
         long long2 = pos.asLong();
         this.blockPosPositionMap.put(long2, time + 200L);
         BlockPosHelperGapple.Tracked tracked = this.glwChgTrackedMap.get(long2);
         if (tracked != null && !tracked.glwChgActive) {
            if (!tracked.xancActive && time >= tracked.xancValue && time - tracked.xancValue <= 2L) {
               this.handleFlag(tracked);
               tracked.foeEntityId = -1;
               if (this.activeAnchorCount > 0) {
                  this.activeAnchorCount--;
               }
            }

            tracked.xancActive = true;
            tracked.ancValue = time;
         }

         this.handleInstancePos(pos);
      }
   }

   private void handle(long blockPosLimit) {
      this.blockPosPositionMap.values().removeIf(until -> until < blockPosLimit);
      this.flooredPositionMap.values().removeIf(until -> until < blockPosLimit);
   }

   boolean canFlagForBlockPos(BlockPos pos) {
      BlockPosHelperGapple.Tracked tracked = pos == null ? null : this.glwChgTrackedMap.get(pos.asLong());
      return tracked != null && !tracked.glwChgActive && tracked.xancActive;
   }

   private void handleFlag(BlockPosHelperGapple.Tracked tracked) {
      if (tracked.ancActive) {
         tracked.ancActive = false;
         BlockPosHelperGapple.Foe foe = tracked.foeEntityId == -1 ? null : this.bowGappleFoeMap.get(tracked.foeEntityId);
         if (foe != null && foe.ancCount > 0) {
            foe.ancCount--;
         }
      }
   }

   public void handleImmutable(BlockPos pos, long immutableValue) {
      if (pos != null) {
         BlockPos immutable = pos.toImmutable();
         this.flooredPositionMap.put(immutable.asLong(), immutableValue + 200L);

         for (BlockPosHelperGapple.Tracked tracked : this.glwChgTrackedMap.values()) {
            tracked.glwChgBlockPosList.remove(immutable);
         }
      }
   }

   public boolean canBowGapple(int bowGappleValue) {
      if (bowGappleValue == -1) {
         return false;
      } else {
         for (BlockPosHelperGapple.Tracked tracked : this.glwChgTrackedMap.values()) {
            if (!tracked.glwChgActive && tracked.foeEntityId == bowGappleValue && !canTimeFlag(tracked, this.latestTick) && (canFlag(this.latestTick, tracked.xancValue, 4) || canFlag(this.latestTick, tracked.xancTimeGlwChg, 4))) {
               return true;
            }
         }

         BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(bowGappleValue);
         if (foe != null && foe.bowGappleActive && foe.vecVec3d != null && (foe.bowGappleBowGappleType == BowGappleType.ANCHOR || foe.bowGappleBowGappleType == BowGappleType.GLOWSTONE)
            )
          {
            for (BlockPosHelperGapple.Tracked tracked2 : this.glwChgTrackedMap.values()) {
               if (!tracked2.glwChgActive && (tracked2.blockState == 0 || tracked2.foeEntityId == -1) && getAmount(foe.vecVec3d, tracked2.blockPos) <= 4.5) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }
   }

   private long getTime(long blockPosValue, long blockPosValue2) {
      Long long2 = this.blockPosPositionMap.get(blockPosValue);
      return long2 != null && long2 >= blockPosValue2 ? long2 - 200L : Long.MIN_VALUE;
   }

   static boolean canCenterDistance(BlockPos pos, long centerDistanceValue, List<Vec3d> list, List<Long> list2) {
      if (pos != null && list != null && list2 != null) {
         Vec3d center = Vec3d.ofCenter(pos);

         for (int i = 0; i < list.size() && i < list2.size(); i++) {
            Vec3d vec3d = (Vec3d)list.get(i);
            long boomtick = (Long)list2.get(i);
            if (vec3d != null && Math.abs(boomtick - centerDistanceValue) <= 1L && vec3d.distanceTo(center) <= 1.5) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public List<HimEtaData> getCopyOf() {
      return this.copyOf;
   }

   void handleDequeArray(Vec3d vec, float dequeArrayValue, long entityBound) {
      if (vec != null && canFlag(dequeArrayValue)) {
         this.arrayDeque.addLast(new BlockPosHelperGapple.BoomRec(vec, entityBound));

         while (this.arrayDeque.size() > 16) {
            this.arrayDeque.removeFirst();
         }
      }
   }

   static int getSwingCenter(BlockPos pos, long swingCenterLimit, List<BlockPosHelperGapple.Cand> list) {
      if (pos != null && list != null) {
         Vec3d center = Vec3d.ofCenter(pos);
         int swingCenterValue = -1;
         double maxValue = Double.MAX_VALUE;
         int swingCenterValue2 = -1;
         double maxValue3 = Double.MAX_VALUE;

         for (BlockPosHelperGapple.Cand cand : list) {
            if (cand != null && cand.eye() != null) {
               double distanceTo = cand.eye().distanceTo(center);
               if (cand.lastSwing() != Long.MIN_VALUE && swingCenterLimit >= cand.lastSwing() && swingCenterLimit - cand.lastSwing() <= 3L && distanceTo <= 5.5 && distanceTo < maxValue) {
                  maxValue = distanceTo;
                  swingCenterValue = cand.id();
               }

               if (distanceTo <= 6.0 && distanceTo < maxValue3) {
                  maxValue3 = distanceTo;
                  swingCenterValue2 = cand.id();
               }
            }
         }

         return swingCenterValue != -1 ? swingCenterValue : swingCenterValue2;
      } else {
         return -1;
      }
   }

   static double getAmount(double blockPosValue, long xancLimit) {
      return Double.isNaN(blockPosValue) ? xancLimit : blockPosValue + 0.5 * (xancLimit - blockPosValue);
   }

   public float getAmountFlag(BlockPos pos) {
      if (pos == null) {
         return Float.NaN;
      } else {
         BlockPosHelperGapple.Tracked tracked = this.glwChgTrackedMap.get(pos.asLong());
         return tracked != null && !tracked.glwChgActive ? tracked.boxBounding : Float.NaN;
      }
   }

   static boolean canFlag(float blockPosValue) {
      return Math.abs(blockPosValue - 5.0F) <= 0.05F;
   }

   private static boolean canFlag(Map<Long, Long> map, long blockPosValue, long blockPosValue2) {
      Long own2 = (Long)map.get(blockPosValue);
      return own2 != null && own2 >= blockPosValue2;
   }

   double getAmount(int blockPosValue) {
      BlockPosHelperGapple.Foe foe = this.bowGappleFoeMap.get(blockPosValue);
      return foe == null ? Double.NaN : foe.lastXancLimit;
   }

   private List<BlockPosHelperGapple.Cand> getVecFlag() {
      ArrayList arrayList = new ArrayList(this.bowGappleFoeMap.size());

      for (BlockPosHelperGapple.Foe foe : this.bowGappleFoeMap.values()) {
         if (foe.bowGappleActive && foe.vecVec3d != null) {
            Long long2 = this.bowGapplePositionMap.get(foe.vecValue);
            arrayList.add(new BlockPosHelperGapple.Cand(foe.vecValue, foe.vecVec3d, long2 == null ? Long.MIN_VALUE : long2));
         }
      }

      return arrayList;
   }

   int getValue(BlockPos pos) {
      BlockPosHelperGapple.Tracked tracked = pos == null ? null : this.glwChgTrackedMap.get(pos.asLong());
      return tracked == null ? -1 : tracked.foeEntityId;
   }

   static int getValue(int blockPosBound, boolean blockPosFlag, boolean blockPosFlag2, int blockPosLimit, double blockPosValue, long cheapHigh) {
      if (!blockPosFlag && blockPosFlag2 && blockPosLimit >= 2 && Double.isFinite(blockPosValue)) {
         int rocketsBuyBound = (int)Math.round(blockPosValue) - (int)Math.min(99L, Math.max(0L, cheapHigh));
         return Math.min(blockPosBound, Math.max(1, rocketsBuyBound));
      } else {
         return blockPosBound;
      }
   }

   private static boolean canFlag(long blockPosLimit, long blockPosValue, int blockPosLimit2) {
      return blockPosValue != Long.MIN_VALUE && blockPosLimit != Long.MIN_VALUE && blockPosLimit >= blockPosValue && blockPosLimit - blockPosValue <= blockPosLimit2;
   }

   private boolean canVecDistance(BlockPosHelperGapple.Tracked tracked, Vec3d vec, Vec3d vec2, int vecDistanceValue, long vecDistanceValue2) {
      if (tracked.posActive || Float.isNaN(tracked.creativeSpectator) || tracked.lastPosTick == Long.MIN_VALUE || vecDistanceValue2 - tracked.lastPosTick >= 4L || vecDistanceValue2 < tracked.lastPosTick) {
         return true;
      } else if (tracked.lastTrackedPos == null || tracked.lastTrackedPos.distanceTo(vec) > 0.3) {
         return true;
      } else {
         return tracked.posValue != vecDistanceValue ? true : vec2 != null && (tracked.vec3d2Pos == null || tracked.vec3d2Pos.distanceTo(vec2) > 0.3);
      }
   }

   static double getAmount(Vec3d vec, BlockPos pos) {
      if (vec != null && pos != null) {
         double blockPosFactor = Math.max(Math.max(pos.getX() - vec.x, 0.0), vec.x - (pos.getX() + 1.0));
         double max2 = Math.max(Math.max(pos.getY() - vec.y, 0.0), vec.y - (pos.getY() + 1.0));
         double max3 = Math.max(Math.max(pos.getZ() - vec.z, 0.0), vec.z - (pos.getZ() + 1.0));
         return Math.sqrt(blockPosFactor * blockPosFactor + max2 * max2 + max3 * max3);
      } else {
         return Double.POSITIVE_INFINITY;
      }
   }

   private record BoomRec(Vec3d center, long tick) {
   }

   record Cand(int id, Vec3d eye, long lastSwing) {
   }

   private record Eye(Vec3d eye, boolean glow) {
   }

   private static final class Foe {
      int xancHitCount;
      long lastBowTick;
      int ancCount;
      BowGappleType bowGappleType;
      boolean bowGappleActive;
      long bowGappleValue;
      Vec3d vecVec3d;
      long ancValue;
      BowGappleType bowGappleBowGappleType = BowGappleType.EMPTY;
      double lastXancLimit;
      final int vecValue;

      Foe(int value) {
         this.bowGappleType = BowGappleType.EMPTY;
         this.lastBowTick = Long.MIN_VALUE;
         this.ancValue = Long.MIN_VALUE;
         this.bowGappleValue = Long.MIN_VALUE;
         this.lastXancLimit = Double.NaN;
         this.vecValue = value;
      }
   }

   public static enum Kind {
      ANCHOR,
      GLOWSTONE,
      OBSIDIAN,
      AIR,
      OTHER;
   }

   private static final class Tracked {
      boolean ancActive;
      float entityReachLimit;
      final BlockPos blockPos;
      int foeEntityId = -1;
      long lastPosTick;
      float boxBounding;
      Vec3d vec3d2Pos;
      int blockState;
      final List<BlockPos> glwChgBlockPosList;
      long xancTimeGlwChg = Long.MIN_VALUE;
      Vec3d lastTrackedPos;
      int posValue;
      final long xancValue;
      long xancTimeAnc;
      boolean glwChgActive;
      long ancValue;
      long xancTime = Long.MIN_VALUE;
      boolean posActive;
      float creativeSpectator;
      boolean xancActive;

      Tracked(BlockPos blockPos, int blockState, long time) {
         this.ancValue = Long.MIN_VALUE;
         this.glwChgBlockPosList = new ArrayList<>(2);
         this.posActive = true;
         this.lastPosTick = Long.MIN_VALUE;
         this.posValue = Integer.MIN_VALUE;
         this.creativeSpectator = Float.NaN;
         this.boxBounding = Float.NaN;
         this.entityReachLimit = Float.NaN;
         this.blockPos = blockPos;
         this.blockState = blockState;
         this.xancValue = time;
         this.xancTimeAnc = time;
      }
   }
}
