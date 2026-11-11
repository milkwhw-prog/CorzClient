package com.corz.client.utils;

import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosHelperBlocked;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.ClientInstance;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.HotbarKitRestocker;
import com.corz.client.utils.CampPlanner;
import com.corz.client.utils.CachedBlockWorldView;
import com.corz.client.utils.PocketAnalyzer;
import com.corz.client.utils.PickaxeToolbox;
import com.corz.client.utils.CrystalTargetPlanner;
import com.corz.client.utils.DigxDug;
import com.corz.client.utils.BlockBreakTracker;

public final class CampBreaching {
   private long followDeadlineTick;
   public static final String CAMP = "camp";
   private final HotbarKitRestocker stackInventory;
   private long lastBreachTick;
   public static final String PUNISH = "punish";
   private CampPlanner.Camp campPlan;
   private final HandswapGate ledgerHandswap;
   private int pickaxeWaitTicks;
   private int unreachableTicks;
   private int digStallTicks;
   public static final String CAMPING = "Camping";
   private final Map<Long, Integer> campPlaceCounts;
   private final RoutedBreachingApi routedBreachingApi;
   private String breachStatusText;
   public static final String FOLLOW = "follow";
   private final ClientInstance clientInstance;
   private final Map<Long, Long> failedCellExpiry;
   private boolean far;
   private final CachedBlockWorldView blockStateCache = new CachedBlockWorldView();
   private PocketAnalyzer.Result holeAnalysis;
   private PocketAnalyzer.Result punishHoleAnalysis;
   private PickaxeToolbox miningTools;
   private BlockPos openedBlockPos;
   private CrystalTargetPlanner.Plan followPlan;
   private boolean campCounted;
   public static final String CAMP_WALK = "camp:walk";
   private int lastTargetId;
   private final DigxDug digxDug;
   private long currentTick;
   private final StringBuilder eventLog;
   public static final String BREACH = "BREACH";
   private long punishStartTick;
   private final BlockBreakTracker digTracker = new BlockBreakTracker();
   private int campCount;
   private final HotbarSlotSwitcher inventorySelected;
   public static final String CAMP_WATCH = "camp:watch";
   private int lowDamageTicks;
   private CenterVecData breachDecision;
   private BlockPos lastDigPos;
   private CrystalTargetPlanner.Plan noplan;
   private CampBreaching.BreachPhase phase;
   public static final String CAMP_PLACE = "camp:place";
   private boolean watching;
   private int followCount;
   public static final String MINE = "mine";
   private String statusText;
   private final ArrayDeque<long[]> positionHistory = new ArrayDeque<>();
   private final BlockPosHelperBlocked obsidianPlacer;
   public static final String BREACHING = "Breaching";
   private boolean needsCampRecalc;
   private int punishCount;
   private CampPlanner.Side currentSide;

   static String getThreatLabel(float campBreachingValue, boolean campBreachingFlag, HimEtaData himEtaData, VecBlock.Threat threat) {
      return getThreatLabelExtended(campBreachingValue, campBreachingFlag, false, himEtaData, threat);
   }

   static boolean isTargetStationary(ArrayDeque<long[]> arrayDeque, Vec3d vec) {
      if (arrayDeque == null || arrayDeque.size() < 10) {
         return false;
      } else if (vec != null && Math.hypot(vec.x, vec.z) < 0.03) {
         Iterator descendingIterator = arrayDeque.descendingIterator();
         long[] campBreachingValues = (long[])descendingIterator.next();

         for (int i = 1; i < 10; i++) {
            long[] next2 = (long[])descendingIterator.next();
            if (next2[1] != campBreachingValues[1] || next2[2] != campBreachingValues[2] || next2[3] != campBreachingValues[3]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void logDigStatus() {
      String formatDig = this.digxDug.consumeStatusMessage();
      if (formatDig != null) {
         BlockPos blockPos = this.digxDug.getTargetPos() != null ? this.digxDug.getTargetPos() : this.digxDug.getLastBrokenPos();
         if (formatDig.startsWith("digx")) {
            this.appendLog(formatDig);
         } else {
            this.appendLog(formatDig + "@" + (blockPos == null ? "-" : formatPos(blockPos)));
         }
      }
   }

   public String formatPunishStatus() {
      if (this.phase == CampBreaching.BreachPhase.IDLE && !this.holeAnalysis.isEnclosed()) {
         return "-";
      } else {
         CrystalTargetPlanner.Plan plan = this.noplan != null ? this.noplan : this.followPlan;
         boolean campPunishFlag = this.phase == CampBreaching.BreachPhase.CAMP || this.phase == CampBreaching.BreachPhase.PUNISH;
         String campPunishText = campPunishFlag ? CrystalTargetPlanner.PlanKind.CAMP.name() : (plan == null ? "-" : plan.entry().name());
         BlockPos blockPos = plan == null ? null : (plan.mined() != null ? plan.mined() : plan.followCell());
         String campPunishText2 = blockPos == null ? "-" : formatPos(blockPos);
         int campPunishValue = plan == null ? 0 : plan.mineTicks();
         int campPunishValue2 = plan == null ? -1 : plan.toolSlot();
         return this.holeAnalysis.verdict().name() + ":" + campPunishText + ":" + campPunishText2 + ":" + this.digxDug.getHoldTicks() + "/" + campPunishValue + ":" + campPunishValue2;
      }
   }

   private CenterVecData buildFollowUpDecision(SelfPopData selfPopData, long breachingFollowValue, ClientPlayerEntity player) {
      CrystalTargetPlanner.Plan centerPosData = this.followPlan;
      VecBlockingData target = selfPopData.target();
      if (centerPosData != null && target != null && target.entity() != null && breachingFollowValue <= this.followDeadlineTick) {
         BlockPos followCell = centerPosData.followCell();
         if (followCell == null) {
            return null;
         } else {
            Vec3d entityPos = player.getEntityPos();
            Vec3d serverPos = getTargetPos(target);
            Box boundingBox = target.entity().getBoundingBox();
            if (centerPosData.follow() != CrystalTargetPlanner.FollowUpKind.CRYSTAL) {
               if (centerPosData.follow() != CrystalTargetPlanner.FollowUpKind.ANCHOR) {
                  return null;
               } else if (!canAnchorsCfg(selfPopData)) {
                  return null;
               } else {
                  BlockState blockState = MinecraftClient.getInstance().world.getBlockState(followCell);
                  if (!blockState.isReplaceable() && !blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                     return null;
                  } else {
                     CenterVecData.AnchorStep anchorStep = new CenterVecData.AnchorStep(followCell, true, serverPos, boundingBox);
                     return createBreachDecision(SurroundKitType.ANCHOR, null, anchorStep, createHoldStand(entityPos), Vec3d.ofCenter(followCell), Math.max(centerPosData.damage(), 1.0), "Breaching");
                  }
               }
            } else if (!this.blockStateCache.isBedrockOrObsidian(followCell)) {
               return null;
            } else if (!this.blockStateCache.isFreeToPlaceAt(followCell.up()) && BlockEntityUtils.getNext(followCell) == null) {
               return null;
            } else {
               CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(followCell, null, Direction.UP, false, true, true, serverPos, boundingBox);
               Vec3d vec3d = new Vec3d(followCell.getX() + 0.5, followCell.getY() + 1.0, followCell.getZ() + 0.5);
               return createBreachDecision(SurroundKitType.CRYSTAL, crystalStep, null, createHoldStand(entityPos), vec3d, Math.max(centerPosData.damage(), 1.0), "Breaching");
            }
         }
      } else {
         return null;
      }
   }

   static double getMaxBreachDistance(int campBreachingLimit, PocketAnalyzer.Result result) {
      return campBreachingLimit >= 6 && result != null && result.isEnclosed() ? 12.0 : 8.0;
   }

   public void reset() {
      this.digxDug.resetBreakState();
      this.logDigStatus();
      this.obsidianPlacer.updateTextImmutableText();
      this.digTracker.reset();
      this.positionHistory.clear();
      this.failedCellExpiry.clear();
      this.campPlaceCounts.clear();
      this.holeAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.miningTools = PickaxeToolbox.EMPTY;
      this.noplan = null;
      this.followPlan = null;
      this.campPlan = null;
      this.lastDigPos = null;
      this.needsCampRecalc = false;
      this.currentSide = null;
      this.openedBlockPos = null;
      this.punishHoleAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.punishStartTick = Long.MIN_VALUE;
      this.followDeadlineTick = Long.MIN_VALUE;
      this.lastBreachTick = Long.MIN_VALUE;
      this.currentTick = Long.MIN_VALUE;
      this.far = false;
      this.lowDamageTicks = 0;
      this.unreachableTicks = 0;
      this.watching = false;
      this.campCounted = false;
      this.phase = CampBreaching.BreachPhase.IDLE;
      this.lastTargetId = -1;
      this.breachDecision = CenterVecData.INSTANCE;
      this.statusText = "-";
      this.breachStatusText = "-";
   }

   static Vec3d getImpactCenter(CrystalTargetPlanner.PlanKind planKind, BlockPos pos) {
      return planKind == CrystalTargetPlanner.PlanKind.CITY_CRYSTAL ? new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5) : Vec3d.ofCenter(pos);
   }

   static boolean hasLineOfSight(PocketAnalyzer.Result result) {
      return result != null && (result.isSealedVerdict() || result.los() != null);
   }

   private static CrystalTargetPlanner.DamageFn createDamageFn(SelfPopData selfPopData, ClientPlayerEntity player, VecBlockingData vecBlockingData) {
      final LivingEntity entity = vecBlockingData.entity();
      final Vec3d serverPos = getTargetPos(vecBlockingData);
      final Box boundingBox = entity.getBoundingBox();
      final PosBlockUtilsBonus.Armor armor = vecBlockingData.armor();
      final PosBlockUtilsBonus.Armor armor2 = selfPopData.self().armor();
      return new CrystalTargetPlanner.DamageFn() {
         @Override
         public float getSelfDamage(CrystalTargetPlanner.PlanKind planKind, BlockPos pos, BlockPos pos2, BlockPos pos3) {
            if (pos3 != null && pos != null) {
               Vec3d var5x = CampBreaching.getImpactCenter(planKind, pos3);
               Vec3d var6x = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
               Box var7x = new Box(pos.getX() + 0.2, pos.getY(), pos.getZ() + 0.2, pos.getX() + 0.8, pos.getY() + 1.8, pos.getZ() + 0.8);
               float creativeSpectator = BlockEntityUtils.getCreativeSpectator(player, var5x, var6x, var7x, null, pos2, CampBreaching.getExplosionPower(planKind));
               return PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, armor2);
            } else {
               return 0.0F;
            }
         }

         @Override
         public float getTargetDamage(CrystalTargetPlanner.PlanKind entry, BlockPos pos4, BlockPos pos5) {
            if (pos5 == null) {
               return CrystalTargetPlanner.getDefaultDamage(entry);
            } else {
               Vec3d var4x = CampBreaching.getImpactCenter(entry, pos5);
               float var5x = BlockEntityUtils.getCreativeSpectator(entity, var4x, serverPos, boundingBox, null, pos4, CampBreaching.getExplosionPower(entry));
               return PosBlockUtilsBonus.getToughnessEpf(var5x, armor);
            }
         }
      };
   }

   static int getLowestRecentY(ArrayDeque<long[]> arrayDeque, int campBreachingValue, int campBreachingValue2, int z) {
      int x2 = campBreachingValue2;
      Iterator descendingIterator = arrayDeque.descendingIterator();

      while (descendingIterator.hasNext()) {
         long[] campBreachingValues = (long[])descendingIterator.next();
         if (campBreachingValues[1] != campBreachingValue || campBreachingValues[3] != z) {
            break;
         }

         x2 = (int)Math.min((long)x2, campBreachingValues[2]);
      }

      return x2;
   }

   static boolean canBreachMode(ComboTradeData comboTradeData) {
      if (comboTradeData == null) {
         return true;
      } else {
         SurroundKitType lane = comboTradeData.lane();
         return lane == SurroundKitType.NONE
            || lane == SurroundKitType.PEARL
            || lane == SurroundKitType.BREACH
            || lane == SurroundKitType.XP
            || lane == SurroundKitType.EAT
            || lane == SurroundKitType.KIT
            || lane == SurroundKitType.GUARD
            || comboTradeData.mode() == DefendEngageType.BREACH
            || comboTradeData.mode() == DefendEngageType.CAMP;
      }
   }

   public CenterVecData getBreachDecision() {
      return this.breachDecision == null ? CenterVecData.INSTANCE : this.breachDecision;
   }

   private static boolean canAnchorsCfg(SelfPopData selfPopData) {
      if (selfPopData.nether() || !BlockPosUtilsRespawn.isRegistryWorld()) {
         return false;
      } else {
         return selfPopData.cfg() != null && !selfPopData.cfg().anchors() ? false : selfPopData.self().anchors() > 0 && selfPopData.self().glowstone() > 0;
      }
   }

   private void resetPlanState() {
      this.obsidianPlacer.updateTextImmutableText();
      this.digTracker.reset();
      this.positionHistory.clear();
      this.campPlaceCounts.clear();
      this.holeAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.noplan = null;
      this.followPlan = null;
      this.campPlan = null;
      this.lastDigPos = null;
      this.currentSide = null;
      this.openedBlockPos = null;
      this.lowDamageTicks = 0;
      this.campCounted = false;
      this.phase = CampBreaching.BreachPhase.IDLE;
   }

   public CampBreaching(ClientInstance clientInstance, HandswapGate ledgerHandswap, HotbarSlotSwitcher inventorySelected, HotbarKitRestocker stackInventory, RoutedBreachingApi api) {
      this.failedCellExpiry = new HashMap<>();
      this.campPlaceCounts = new HashMap<>();
      this.eventLog = new StringBuilder();
      this.phase = CampBreaching.BreachPhase.IDLE;
      this.lastTargetId = -1;
      this.holeAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.miningTools = PickaxeToolbox.EMPTY;
      this.punishHoleAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.punishStartTick = Long.MIN_VALUE;
      this.followDeadlineTick = Long.MIN_VALUE;
      this.lastBreachTick = Long.MIN_VALUE;
      this.currentTick = Long.MIN_VALUE;
      this.breachDecision = CenterVecData.INSTANCE;
      this.statusText = "-";
      this.breachStatusText = "-";
      this.clientInstance = clientInstance;
      this.ledgerHandswap = ledgerHandswap;
      this.inventorySelected = inventorySelected;
      this.stackInventory = stackInventory;
      this.routedBreachingApi = api;
      this.digxDug = DigxDug.create(clientInstance, ledgerHandswap);
      this.obsidianPlacer = new BlockPosHelperBlocked(clientInstance);
   }

   private BlockPos trackTargetFeetPos(VecBlockingData vecBlockingData, long dequeArrayValue) {
      Vec3d serverPos = getTargetPos(vecBlockingData);
      int x = (int)Math.floor(serverPos.x);
      int floor2 = (int)Math.floor(serverPos.y + 0.001);
      int z = (int)Math.floor(serverPos.z);
      this.positionHistory.addLast(new long[]{dequeArrayValue, x, floor2, z});

      while (!this.positionHistory.isEmpty() && (this.positionHistory.size() > 20 || dequeArrayValue - ((long[])this.positionHistory.peekFirst())[0] >= 20L)) {
         this.positionHistory.removeFirst();
      }

      return new BlockPos(x, getLowestRecentY(this.positionHistory, x, floor2, z), z);
   }

   public int getPunishCount() {
      return this.punishCount;
   }

   private CaseMaxData reportStatus(String text) {
      this.statusText = text;
      this.breachStatusText = text;
      return CaseMaxData.getCaseMaxData(SurroundKitType.BREACH, text);
   }

   public boolean isDigHeld() {
      return this.digxDug.isHoldingOrCancelling();
   }

   public void onPacketEvent(ServerPacketEventQueue.Event event, long time, int immutablePosValue) {
      if (event instanceof ServerPacketEventQueue.Dig dig) {
         if (dig.breakerId() == immutablePosValue) {
            return;
         }

         this.digTracker.updateDigProgress(dig.breakerId(), dig.pos(), dig.stage(), time);
         if (!ServerPacketEventQueue.isOutsideZeroToNine(dig.stage()) && this.digTracker.isTracked(dig.pos())) {
            this.appendLog("edig@" + formatPos(dig.pos()) + ":" + dig.stage());
         }
      } else if (event instanceof ServerPacketEventQueue.BlockChange blockChange) {
         this.blockStateCache.invalidateBlock(blockChange.pos());
         if (this.holeAnalysis.isEnclosed() && this.holeAnalysis.isNearPocket(blockChange.pos(), 3)) {
            this.far = true;
         }

         boolean immutablePosFlag = blockChange.state() != null && blockChange.state().isAir();
         if (this.digTracker.tryRecordOpened(blockChange.pos(), immutablePosFlag, time)) {
            this.appendLog("open@" + formatPos(blockChange.pos()));
            if (this.phase == CampBreaching.BreachPhase.CAMP) {
               this.openedBlockPos = blockChange.pos().toImmutable();
               this.punishStartTick = time;
               this.punishHoleAnalysis = this.holeAnalysis;
               this.phase = CampBreaching.BreachPhase.PUNISH;
            }
         }
      }
   }

   static String getThreatLabelExtended(float ehpCrystalValue, boolean ehpCrystalFlag, boolean ehpCrystalFlag2, HimEtaData himEtaData, VecBlock.Threat threat) {
      if (ehpCrystalValue < 8.0F && !ehpCrystalFlag && !ehpCrystalFlag2) {
         return "ehp";
      } else if (himEtaData != null && himEtaData.isCell() && himEtaData.eta() <= 3 && himEtaData.hitUs() >= 0.3F * ehpCrystalValue) {
         return "anchor";
      } else {
         return threat != null && threat.liveCrystalId() >= 0 && threat.etaTicks() <= 3 ? "crystal" : null;
      }
   }

   private void appendLog(String builderText) {
      if (this.eventLog.length() <= 512) {
         if (!this.eventLog.isEmpty()) {
            this.eventLog.append(' ');
         }

         this.eventLog.append(builderText);
      }
   }

   public PocketAnalyzer.Result getHoleAnalysis() {
      return this.holeAnalysis;
   }

   private CaseMaxData planPunish(SelfPopData selfPopData, long campingPunishValue, ClientPlayerEntity player) {
      long cityDownLimit = campingPunishValue - this.punishStartTick;
      VecBlockingData target = selfPopData.target();
      if (this.openedBlockPos != null && target != null && cityDownLimit <= 10L) {
         boolean campingPunishFlag = this.currentSide != null && BlockEntityUtils.getNext(this.currentSide.base()) != null;
         CampPlanner.PunishPlan cityDown = CampPlanner.getPunishPlan(this.blockStateCache, this.punishHoleAnalysis, this.openedBlockPos, cityDownLimit, this.currentSide, campingPunishFlag);
         Vec3d entityPos = player.getEntityPos();
         Vec3d serverPos = getTargetPos(target);
         Box boundingBox = target.entity().getBoundingBox();
         switch (cityDown.act()) {
            case BREAK_PRE:
            case CITY_CRYSTAL:
               CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(cityDown.cell(), null, Direction.UP, false, true, true, serverPos, boundingBox);
               Vec3d vec3d = new Vec3d(cityDown.cell().getX() + 0.5, cityDown.cell().getY() + 1.5, cityDown.cell().getZ() + 0.5);
               this.breachDecision = createBreachDecision(SurroundKitType.CRYSTAL, crystalStep, null, createHoldStand(entityPos), vec3d, 8.0, "Camping");
               break;
            case CITY_ANCHOR:
            case ROOF_ANCHOR:
               if (!canAnchorsCfg(selfPopData)) {
                  this.finishPunish();
                  return null;
               }

               CenterVecData.AnchorStep anchorStep = new CenterVecData.AnchorStep(cityDown.cell(), true, serverPos, boundingBox);
               this.breachDecision = createBreachDecision(SurroundKitType.ANCHOR, null, anchorStep, createHoldStand(entityPos), Vec3d.ofCenter(cityDown.cell()), 8.0, "Camping");
               break;
            default:
               this.finishPunish();
               return null;
         }

         if (cityDownLimit == 0L) {
            this.appendLog("pun:" + cityDown.act().name().toLowerCase(Locale.ROOT));
         }

         return this.getTextBreach(8.0, 0, "punish");
      } else {
         this.finishPunish();
         return null;
      }
   }

   private int findPickaxeSlot(ClientPlayerEntity player) {
      if (this.noplan.toolSlot() == -1) {
         return -1;
      } else {
         PlayerInventory inventory = player.getInventory();
         int toolSlot = this.noplan.toolSlot();
         if (toolSlot >= 0 && toolSlot < 9 && inventory.getStack(toolSlot).isIn(ItemTags.PICKAXES)) {
            return toolSlot;
         } else {
            PickaxeToolbox hasteMining = getMiningTools(player);
            PickaxeToolbox.Pick suitableMultiplier = hasteMining.findBestPick(this.blockStateCache, this.noplan.mined(), true, true);
            PickaxeToolbox.Pick bestPick = hasteMining.findBestPick(this.blockStateCache, this.noplan.mined(), false, true);
            if (suitableMultiplier.pickaxe() && suitableMultiplier.ticks() <= bestPick.ticks()) {
               return suitableMultiplier.slot();
            } else if (suitableMultiplier.pickaxe()) {
               return suitableMultiplier.slot();
            } else {
               if (this.stackInventory != null) {
                  this.stackInventory.tryScheduleSwapForKind(HotbarKitRestocker.Kind.PICKAXE);
               }

               return -2;
            }
         }
      }
   }

   private int getMineTicksFor(LivingEntity entity, BlockPos pos2) {
      if (entity != null && pos2 != null) {
         ItemStack mainHandStack = entity.getMainHandStack();
         BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();
         boolean suitableMultiplierFlag = !mainHandStack.isEmpty() && mainHandStack.isIn(ItemTags.PICKAXES);
         PickaxeToolbox.Tool tool = suitableMultiplierFlag
            ? new PickaxeToolbox.Tool(
               0,
               mainHandStack.getMiningSpeedMultiplier(defaultState),
               InventoryStackUtilsEmpty.getEnchantmentsDefault(mainHandStack, Enchantments.EFFICIENCY),
               true,
               mainHandStack.isSuitableFor(defaultState)
            )
            : PickaxeToolbox.NO_TOOL;
         return PickaxeToolbox.ofTools(tool).getBreakTicks(tool, this.blockStateCache, pos2, true);
      } else {
         return Integer.MAX_VALUE;
      }
   }

   static int getMissTicksRequired(boolean campBreachingFlag) {
      return campBreachingFlag ? 2 : 3;
   }

   private static String formatPos(BlockPos pos) {
      return pos.getX() + "," + pos.getY() + "," + pos.getZ();
   }

   static double getBestOptionValue(SelfPopData selfPopData) {
      return getMaxAvailableValue(selfPopData::getOptions, false);
   }

   public CaseMaxData planBreach(SelfPopData selfPopData, ComboTradeData comboTradeData) {
      this.breachDecision = CenterVecData.INSTANCE;
      this.watching = false;
      this.logDigStatus();
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && selfPopData != null && selfPopData.self() != null) {
         VecBlockingData target = selfPopData.target();
         if (target != null && target.entity() != null) {
            long time = selfPopData.tick();
            if (this.currentTick < time - 1L) {
               if (this.obsidianPlacer.isFlag()) {
                  this.obsidianPlacer.updateTextImmutableText();
               }

               if (this.digxDug.hasActiveTarget() && !this.digxDug.isHoldingOrCancelling()) {
                  this.digxDug.cancelBreakIfHolding();
               }
            }

            if (target.id() != this.lastTargetId) {
               if (!this.digxDug.isHoldingOrCancelling()) {
                  this.resetPlanState();
               }

               this.lastTargetId = target.id();
            }

            this.blockStateCache.bindWorld(instance.world);
            BlockPos dequeArray = this.trackTargetFeetPos(target, time);
            this.unreachableTicks = this.routedBreachingApi == null
                  || this.routedBreachingApi.getRouteMode() != AnchorRoutePlanner.Mode.UNREACHABLE && this.routedBreachingApi.getRouteMode() != AnchorRoutePlanner.Mode.PARTIAL
               ? 0
               : Math.min(this.unreachableTicks + 1, 10000);
            this.digTracker.pruneExpired(time);
            this.failedCellExpiry.values().removeIf(tick -> tick <= time);
            if (target.distance() > getMaxBreachDistance(this.unreachableTicks, this.holeAnalysis)
               && !this.digxDug.isHoldingOrCancelling()
               && this.phase != CampBreaching.BreachPhase.PUNISH) {
               this.lowDamageTicks = 0;
               this.resetIfNotDigging();
               return this.reportStatus("far");
            } else {
               boolean noplanBreachingFlag = this.far || this.holeAnalysis.feet() == null || !dequeArray.equals(this.holeAnalysis.feet()) || time - this.lastBreachTick >= 4L;
               boolean campCampingFlag = false;
               if (noplanBreachingFlag && target.distance() <= 12.0) {
                  campCampingFlag = true;
                  this.miningTools = getMiningTools(player);
                  this.holeAnalysis = PocketAnalyzer.analyzePocket(
                     this.blockStateCache, dequeArray, this.miningTools, this.unreachableTicks >= 6, player.getEyePos(), getTargetAimPoint(target)
                  );
                  this.lastBreachTick = time;
                  this.far = false;
                  this.digTracker.setTrackedBlocks(collectHoleBlocks(this.holeAnalysis));
               }

               if (this.phase == CampBreaching.BreachPhase.PUNISH) {
                  CaseMaxData campingPunish = this.planPunish(selfPopData, time, player);
                  if (campingPunish != null) {
                     return campingPunish;
                  }
               }

               if (this.phase == CampBreaching.BreachPhase.FOLLOW) {
                  CenterVecData centerVecData = this.buildFollowUpDecision(selfPopData, time, player);
                  if (centerVecData != null) {
                     this.breachDecision = centerVecData;
                     return this.getTextBreach(this.followPlan == null ? 8.0 : Math.max(this.followPlan.damage(), 1.0), 0, "follow");
                  }

                  this.phase = CampBreaching.BreachPhase.IDLE;
                  this.followPlan = null;
               }

               boolean noplanBreachingFlag3 = this.digxDug.isHoldingOrCancelling()
                  || this.obsidianPlacer.isFlag()
                  || this.phase == CampBreaching.BreachPhase.MINE && this.digxDug.hasActiveTarget();
               if (!this.holeAnalysis.isEnclosed()) {
                  this.lowDamageTicks = 0;
                  this.resetIfNotDigging();
                  return this.reportStatus("open");
               } else {
                  boolean breachMode = canBreachMode(comboTradeData);
                  double noplanBreachingLimit = Math.max(0.5 * getMinUsefulDamage(selfPopData, comboTradeData), 1.0);
                  if (getBestOptionValueForHole(selfPopData, this.holeAnalysis) < noplanBreachingLimit && breachMode) {
                     this.lowDamageTicks = Math.min(this.lowDamageTicks + 1, 10000);
                  } else if (!noplanBreachingFlag3) {
                     this.lowDamageTicks = 0;
                  }

                  if (!noplanBreachingFlag3 && this.lowDamageTicks < getMissTicksRequired(isTargetStationary(this.positionHistory, target.velocity()))) {
                     return this.reportStatus("wait");
                  } else {
                     String selfThreatLabel = getSelfThreatLabel(selfPopData, this.holeAnalysis.isSealedVerdict());
                     if (selfThreatLabel != null) {
                        return this.reportStatus(selfThreatLabel);
                     } else if (noplanBreachingFlag3 && this.hasNoBlockBeyondMined(this.noplan)) {
                        this.resetIfNotDigging();
                        this.noplan = null;
                        return this.reportStatus("moved");
                     } else {
                        if (!noplanBreachingFlag3 && (campCampingFlag || this.noplan == null || this.phase == CampBreaching.BreachPhase.IDLE)) {
                           this.noplan = CrystalTargetPlanner.getBestPlan(this.createCenterInput(selfPopData, player, target), pos -> this.failedCellExpiry.containsKey(pos.asLong()));
                           this.needsCampRecalc = true;
                           if (this.noplan == null) {
                              this.resetIfNotDigging();
                              return this.reportStatus("noplan");
                           }
                        }

                        if (this.noplan == null) {
                           return this.reportStatus("noplan");
                        } else if (this.noplan.isCamp()) {
                           return this.planCamp(selfPopData, time, player, target, campCampingFlag);
                        } else {
                           this.campCounted = false;
                           Vec3d entityPos = player.getEntityPos();
                           boolean standingOnCell = isStandingOnCell(entityPos, this.noplan.stand());
                           String noplanBreachingText = "mine:" + this.noplan.entry().name();
                           if (this.noplan.hasMineTarget()) {
                              if (noplanBreachingFlag3 || standingOnCell && player.isOnGround()) {
                                 this.phase = CampBreaching.BreachPhase.MINE;
                                 Vec3d vec3d = this.digxDug.getLos() != null ? this.digxDug.getLos() : this.noplan.aim();
                                 this.breachDecision = createBreachDecision(SurroundKitType.BREACH, null, null, createHoldStand(entityPos), vec3d, this.noplan.value(), "Breaching");
                              } else {
                                 this.phase = CampBreaching.BreachPhase.WALK;
                                 this.breachDecision = createBreachDecision(
                                    SurroundKitType.BREACH,
                                    null,
                                    null,
                                    createFollowStand(this.noplan.stand(), entityPos),
                                    this.noplan.aim(),
                                    this.noplan.value(),
                                    "Breaching"
                                 );
                              }

                              return this.getTextBreach(this.noplan.value(), this.noplan.eta(), noplanBreachingText);
                           } else if (this.noplan.build() != null && !this.blockStateCache.isSolidSupportBlock(this.noplan.build())) {
                              if (!standingOnCell && !this.obsidianPlacer.isFlag()) {
                                 this.phase = CampBreaching.BreachPhase.WALK;
                                 this.breachDecision = createBreachDecision(
                                    SurroundKitType.BREACH,
                                    null,
                                    null,
                                    createFollowStand(this.noplan.stand(), entityPos),
                                    Vec3d.ofCenter(this.noplan.followCell()),
                                    this.noplan.value(),
                                    "Breaching"
                                 );
                              } else {
                                 this.phase = CampBreaching.BreachPhase.BUILD;
                                 this.breachDecision = createBreachDecision(
                                    SurroundKitType.BREACH, null, null, createHoldStand(entityPos), Vec3d.ofCenter(this.noplan.build()), this.noplan.value(), "Breaching"
                                 );
                              }

                              return this.getTextBreach(this.noplan.value(), this.noplan.eta(), noplanBreachingText);
                           } else if (!standingOnCell && player.getEyePos().distanceTo(Vec3d.ofCenter(this.noplan.followCell())) > 4.4) {
                              this.phase = CampBreaching.BreachPhase.WALK;
                              this.breachDecision = createBreachDecision(
                                 SurroundKitType.BREACH,
                                 null,
                                 null,
                                 createFollowStand(this.noplan.stand(), entityPos),
                                 Vec3d.ofCenter(this.noplan.followCell()),
                                 this.noplan.value(),
                                 "Breaching"
                              );
                              return this.getTextBreach(this.noplan.value(), this.noplan.eta(), noplanBreachingText);
                           } else {
                              this.followPlan = this.noplan;
                              this.followDeadlineTick = time + 6L;
                              this.phase = CampBreaching.BreachPhase.FOLLOW;
                              CenterVecData followUpDecision2 = this.buildFollowUpDecision(selfPopData, time, player);
                              if (followUpDecision2 == null) {
                                 this.phase = CampBreaching.BreachPhase.IDLE;
                                 this.followPlan = null;
                                 this.resetIfNotDigging();
                                 return this.reportStatus("follow");
                              } else {
                                 this.followCount++;
                                 this.breachDecision = followUpDecision2;
                                 return this.getTextBreach(this.noplan.value(), this.noplan.eta(), "follow");
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.resetIfNotDigging();
            return this.reportStatus("no target");
         }
      } else {
         return this.reportStatus("no player");
      }
   }

   public String flushLog() {
      this.logDigStatus();
      String formatBuilder = this.eventLog.toString();
      this.eventLog.setLength(0);
      return formatBuilder;
   }

   private static CenterVecData createBreachDecision(
      SurroundKitType surroundKitType,
      CenterVecData.CrystalStep crystalStep,
      CenterVecData.AnchorStep anchorStep,
      CenterVecData.Stand stand,
      Vec3d vec,
      double breachNoneValue,
      String breachNoneText
   ) {
      double breachNoneValue2 = Double.isFinite(breachNoneValue) ? breachNoneValue : 0.0;
      return new CenterVecData(
         CenterVecData.State.HIT_LOOP,
         CenterVecData.Proposal.NONE,
         breachNoneValue2,
         CenterVecData.Abort.NONE,
         surroundKitType,
         crystalStep,
         null,
         null,
         anchorStep,
         stand,
         vec,
         Integer.MAX_VALUE,
         0,
         breachNoneText,
         "BREACH"
      );
   }

   static double getMaxAvailableValue(Function<SurroundKitType, CaseMaxData> function, boolean meleeCrystalFlag) {
      double meleeCrystal = Double.NEGATIVE_INFINITY;

      for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
         if (!meleeCrystalFlag || surroundKitType != SurroundKitType.MELEE) {
            CaseMaxData apply = (CaseMaxData)function.apply(surroundKitType);
            if (apply != null && apply.available() && Double.isFinite(apply.value())) {
               meleeCrystal = Math.max(meleeCrystal, apply.value());
            }
         }
      }

      return meleeCrystal;
   }

   public String getStatusText() {
      return this.statusText;
   }

   static boolean isAimingOrWaiting(DigxDug.Result result, boolean aimingWaitingFlag) {
      return !aimingWaitingFlag && (result == DigxDug.Result.AIMING || result == DigxDug.Result.WAITING);
   }

   private CaseMaxData planCamp(SelfPopData selfPopData, long campCampingValue, ClientPlayerEntity player, VecBlockingData vecBlockingData, boolean campCampingFlag) {
      if (!this.campCounted) {
         this.campCount++;
         this.campCounted = true;
      }

      this.phase = CampBreaching.BreachPhase.CAMP;
      Vec3d entityPos = player.getEntityPos();
      BlockBreakTracker.Dig next = this.digTracker.getLatestTrackedDig();
      boolean immutableFlag = selfPopData.self().obsidian() > 0;
      BlockPos blockPos = next == null ? null : next.pos();
      if (this.campPlan == null || campCampingFlag || this.needsCampRecalc || blockPos != null && !blockPos.equals(this.lastDigPos)) {
         this.campPlan = CampPlanner.findCampSpot(this.blockStateCache, this.holeAnalysis, entityPos, blockPos, immutableFlag);
         this.lastDigPos = blockPos;
         this.needsCampRecalc = false;
      }

      Vec3d serverPos = getTargetPos(vecBlockingData);
      Box boundingBox = vecBlockingData.entity().getBoundingBox();
      if (this.campPlan == null) {
         this.breachDecision = createBreachDecision(SurroundKitType.NONE, null, null, createHoldStand(entityPos), getTargetAimPoint(vecBlockingData), 1.0, "Camping");
         this.watching = next == null;
         return this.getTextBreach(1.0, 0, this.watching ? "camp" : "camp:watch");
      } else if (!isStandingOnCell(entityPos, this.campPlan.stand())) {
         this.breachDecision = createBreachDecision(
            SurroundKitType.NONE, null, null, createFollowStand(this.campPlan.stand(), entityPos), getTargetAimPoint(vecBlockingData), 1.0, "Camping"
         );
         return this.getTextBreach(1.0, 0, "camp:walk");
      } else {
         for (CampPlanner.Side next2 : this.campPlan.sides()) {
            if (BlockEntityUtils.getNext(next2.base()) == null) {
               int orDefault = this.campPlaceCounts.getOrDefault(next2.base().asLong(), 0);
               if (orDefault < 30) {
                  this.campPlaceCounts.put(next2.base().asLong(), orDefault + 1);
                  this.currentSide = next2;
                  CenterVecData.CrystalStep crystalStep = new CenterVecData.CrystalStep(
                     next2.base(),
                     next2.buildBase() ? next2.base() : null,
                     Direction.UP,
                     true,
                     false,
                     true,
                     serverPos,
                     boundingBox,
                     next2.buildBase() ? next2.base().down() : null,
                     next2.buildBase() ? Direction.UP : null
                  );
                  Vec3d vec3d = new Vec3d(next2.base().getX() + 0.5, next2.base().getY() + 1.0, next2.base().getZ() + 0.5);
                  this.breachDecision = createBreachDecision(SurroundKitType.CRYSTAL, crystalStep, null, createHoldStand(entityPos), vec3d, 1.0, "Camping");
                  return this.getTextBreach(1.0, 1, "camp:place");
               }
            }
         }

         if (this.currentSide == null && !this.campPlan.sides().isEmpty()) {
            this.currentSide = this.campPlan.sides().get(0);
         }

         int campCampingLimit = next == null ? Integer.MAX_VALUE : BlockBreakTracker.scaleTicksByTenths(this.getMineTicksFor(vecBlockingData.entity(), next.pos()), next.stage());
         this.watching = campCampingLimit > 15;
         this.breachDecision = createBreachDecision(SurroundKitType.NONE, null, null, createHoldStand(entityPos), getTargetAimPoint(vecBlockingData), 1.0, "Camping");
         return this.getTextBreach(1.0, 0, this.watching ? "camp" : "camp:watch");
      }
   }

   static double getMinUsefulDamage(SelfPopData selfPopData, ComboTradeData comboTradeData) {
      if (comboTradeData != null && comboTradeData.weights() != null) {
         return comboTradeData.weights().minUseful();
      } else {
         CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
         return SelfHazData.getSelfHaz(DefensiveAggressiveType.BALANCED, carefulBalancedType).minUseful();
      }
   }

   private static CenterVecData.Stand createFollowStand(BlockPos pos, Vec3d vec) {
      Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
      boolean followDistanceFlag = vec != null && vec.distanceTo(vec3d) > 3.0;
      return new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, vec3d, 0.3, followDistanceFlag, false, Long.MIN_VALUE, vec3d, PreferAnchorData.Kind.CELL);
   }

   static float getExplosionPower(CrystalTargetPlanner.PlanKind planKind) {
      return planKind == CrystalTargetPlanner.PlanKind.CITY_CRYSTAL ? 6.0F : 5.0F;
   }

   private DigxDug.Result runBuildStep(SelfPopData selfPopData, ClientPlayerEntity player) {
      if (this.noplan == null || this.noplan.build() == null) {
         return DigxDug.Result.IDLE;
      } else if (!this.obsidianPlacer.isFlag() && FloorMathUtilsBlock.canInstanceWorld(this.noplan.build())) {
         FloorMathUtilsBlock.handle(false);
         return DigxDug.Result.WAITING;
      } else {
         if (!this.obsidianPlacer.isFlag()) {
            int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.OBSIDIAN), false);
            if (selectedSlot < 0) {
               this.noplan = null;
               this.phase = CampBreaching.BreachPhase.IDLE;
               return DigxDug.Result.FAILED;
            }

            if (!this.obsidianPlacer.canImmutableFlag(this.noplan.build(), selectedSlot, Blocks.OBSIDIAN.getDefaultState(), null, 4)) {
               return DigxDug.Result.WAITING;
            }
         }

         BlockPosHelperBlocked.Result blockedHit = this.obsidianPlacer.getBlockedHit();

         return switch (blockedHit) {
            case ACTED -> DigxDug.Result.STARTED;
            case DONE -> {
               this.appendLog("bld@" + formatPos(this.noplan.build()));
               this.phase = CampBreaching.BreachPhase.IDLE;
               this.far = true;
               yield DigxDug.Result.DONE;
            }
            case FAILED -> {
               this.failedCellExpiry.put(this.noplan.build().asLong(), (selfPopData == null ? this.clientInstance.getTime() : selfPopData.tick()) + 60L);
               this.noplan = null;
               this.phase = CampBreaching.BreachPhase.IDLE;
               yield DigxDug.Result.FAILED;
            }
            case AIMING -> DigxDug.Result.AIMING;
            case WAITING -> DigxDug.Result.WAITING;
            case IDLE -> DigxDug.Result.IDLE;
         };
      }
   }

   public boolean isBreachActionActive() {
      return this.digxDug.isHoldingOrCancelling() || this.phase == CampBreaching.BreachPhase.MINE && this.digxDug.hasActiveTarget() || this.obsidianPlacer.isFlag();
   }

   private void finishPunish() {
      if (this.phase == CampBreaching.BreachPhase.PUNISH) {
         this.punishCount++;
      }

      this.phase = CampBreaching.BreachPhase.IDLE;
      this.openedBlockPos = null;
      this.punishHoleAnalysis = PocketAnalyzer.Result.INSTANCE;
      this.punishStartTick = Long.MIN_VALUE;
      this.currentSide = null;
      this.campCounted = false;
      this.far = true;
   }

   public void cancelMining() {
      this.digxDug.cancelBreakIfHolding();
      this.logDigStatus();
      if (this.phase == CampBreaching.BreachPhase.MINE) {
         this.phase = CampBreaching.BreachPhase.IDLE;
      }
   }

   private CrystalTargetPlanner.PlanInput createCenterInput(SelfPopData selfPopData, ClientPlayerEntity player, VecBlockingData vecBlockingData) {
      boolean selfPosFlag = selfPopData.self().crystals() > 0;
      boolean anchorsCfg = canAnchorsCfg(selfPopData);
      boolean selfPosFlag2 = selfPopData.self().obsidian() > 0;
      CrystalTargetPlanner.DamageFn armorBounding = createDamageFn(selfPopData, player, vecBlockingData);
      return new CrystalTargetPlanner.PlanInput(
         this.blockStateCache, this.holeAnalysis, this.miningTools, player.getEntityPos(), selfPosFlag, anchorsCfg, selfPosFlag2, selfPopData.self().getAbsorptionHealth(), armorBounding, null
      );
   }

   private static Vec3d getTargetAimPoint(VecBlockingData vecBlockingData) {
      Box boundingBox = vecBlockingData.entity().getBoundingBox();
      return new Vec3d((boundingBox.minX + boundingBox.maxX) * 0.5, boundingBox.minY + 0.4, (boundingBox.minZ + boundingBox.maxZ) * 0.5);
   }

   static List<BlockPos> collectHoleBlocks(PocketAnalyzer.Result result) {
      ArrayList arrayList = new ArrayList();
      if (result != null && result.feet() != null) {
         for (PocketAnalyzer.Wall wall : result.walls()) {
            arrayList.add(wall.pos());
         }

         if (result.roof() != null) {
            arrayList.add(result.roof().pos());
         }

         arrayList.add(result.feet().down());
         return arrayList;
      } else {
         return arrayList;
      }
   }

   private void requestPickaxeInventory() {
      if (this.noplan != null && this.noplan.toolSlot() >= 9 && this.stackInventory != null) {
         this.stackInventory.tryScheduleSwapForKind(HotbarKitRestocker.Kind.PICKAXE);
      }
   }

   private DigxDug.Result runMineStep(SelfPopData selfPopData, ClientPlayerEntity player) {
      if (this.noplan != null && this.noplan.hasMineTarget()) {
         long digxNoplanValue = selfPopData == null ? this.clientInstance.getTime() : selfPopData.tick();
         if (!this.digxDug.hasActiveTarget() && !this.digxDug.isHoldingOrCancelling()) {
            int noplanPickaxe = this.findPickaxeSlot(player);
            if (noplanPickaxe == -2) {
               if (++this.pickaxeWaitTicks > 40) {
                  if (this.noplan.mined() != null) {
                     this.failedCellExpiry.put(this.noplan.mined().asLong(), digxNoplanValue + 60L);
                  }

                  this.appendLog("digx:tool");
                  this.noplan = null;
                  this.phase = CampBreaching.BreachPhase.IDLE;
                  this.pickaxeWaitTicks = 0;
                  return DigxDug.Result.FAILED;
               }

               return DigxDug.Result.WAITING;
            }

            this.pickaxeWaitTicks = 0;
            this.digStallTicks = 0;
            this.digxDug.tryStartTarget(this.noplan.mined(), noplanPickaxe, this.noplan.mineTicks());
         }

         DigxDug.Result digxCancel = this.digxDug.advanceBreak();
         this.digStallTicks = isAimingOrWaiting(digxCancel, this.digxDug.isHoldingOrCancelling()) ? this.digStallTicks + 1 : 0;
         if (this.digStallTicks >= 40) {
            if (this.noplan.mined() != null) {
               this.failedCellExpiry.put(this.noplan.mined().asLong(), digxNoplanValue + 60L);
            }

            this.appendLog("digx:stall");
            this.digxDug.cancelBreakIfHolding();
            this.noplan = null;
            this.phase = CampBreaching.BreachPhase.IDLE;
            this.digStallTicks = 0;
            return DigxDug.Result.FAILED;
         } else {
            if (digxCancel == DigxDug.Result.DONE) {
               this.far = true;
               if (this.noplan.follow() != CrystalTargetPlanner.FollowUpKind.NONE) {
                  this.followPlan = this.noplan;
                  this.followDeadlineTick = digxNoplanValue + 1L + 6L;
                  this.phase = CampBreaching.BreachPhase.FOLLOW;
               } else {
                  this.phase = CampBreaching.BreachPhase.IDLE;
               }

               this.followCount++;
               this.noplan = null;
            } else if (digxCancel == DigxDug.Result.FAILED) {
               if (this.noplan.mined() != null) {
                  this.failedCellExpiry.put(this.noplan.mined().asLong(), digxNoplanValue + 60L);
               }

               this.noplan = null;
               this.phase = CampBreaching.BreachPhase.IDLE;
            }

            return digxCancel;
         }
      } else {
         return DigxDug.Result.IDLE;
      }
   }

   static String getSelfThreatLabel(SelfPopData selfPopData, boolean selfThreatFlag) {
      boolean selfThreatFlag2 = selfPopData.self().totemConfirmed() && selfPopData.self().totems() >= 2;
      return getThreatLabelExtended(selfPopData.self().getAbsorptionHealth(), selfThreatFlag, selfThreatFlag2, selfPopData.anchor(), selfPopData.threat());
   }

   public int getCampCount() {
      return this.campCount;
   }

   private boolean hasNoBlockBeyondMined(CrystalTargetPlanner.Plan plan) {
      return plan != null && plan.mined() != null && plan.entry() != CrystalTargetPlanner.PlanKind.LOS_MINE ? this.holeAnalysis.getWallAt(plan.mined()) == null : false;
   }

   private static Vec3d getTargetPos(VecBlockingData vecBlockingData) {
      return vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
   }

   public DigxDug.Result runDigStep(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      this.currentTick = selfPopData == null ? this.clientInstance.getTime() : selfPopData.tick();
      if (player != null && instance.world != null) {
         DigxDug.Result dugDigx = switch (this.phase) {
            case WALK -> {
               this.requestPickaxeInventory();
               yield DigxDug.Result.AIMING;
            }
            case MINE -> this.runMineStep(selfPopData, player);
            case BUILD -> this.runBuildStep(selfPopData, player);
            default -> this.digxDug.isHoldingOrCancelling() ? this.digxDug.advanceBreak() : DigxDug.Result.IDLE;
         };
         this.logDigStatus();
         return dugDigx;
      } else {
         return DigxDug.Result.IDLE;
      }
   }

   public int getFollowCount() {
      return this.followCount;
   }

   static PickaxeToolbox getMiningTools(ClientPlayerEntity player) {
      PlayerInventory inventory = player.getInventory();
      BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < 36; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.isIn(ItemTags.PICKAXES)) {
            arrayList.add(
               new PickaxeToolbox.Tool(
                  i,
                  stack.getMiningSpeedMultiplier(defaultState),
                  InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.EFFICIENCY),
                  true,
                  stack.isSuitableFor(defaultState)
               )
            );
         }
      }

      int hasteMiningValue = StatusEffectUtil.hasHaste(player) ? StatusEffectUtil.getHasteAmplifier(player) : -1;
      StatusEffectInstance statusEffect = player.getStatusEffect(StatusEffects.MINING_FATIGUE);
      boolean hasteMiningFlag = player.isSubmergedIn(FluidTags.WATER) && player.getAttributeValue(EntityAttributes.SUBMERGED_MINING_SPEED) < 0.99;
      return new PickaxeToolbox(arrayList, hasteMiningValue, statusEffect == null ? -1 : statusEffect.getAmplifier(), hasteMiningFlag);
   }

   static double getBestOptionValueForHole(SelfPopData selfPopData, PocketAnalyzer.Result result) {
      return getMaxAvailableValue(selfPopData::getOptions, hasLineOfSight(result));
   }

   public String getStatusKey() {
      String replaceText = this.breachStatusText;
      return replaceText == null ? "ok" : replaceText.replace(' ', '_');
   }

   private static CenterVecData.Stand createHoldStand(Vec3d vec) {
      return new CenterVecData.Stand(CenterVecData.StandKind.HOLD, vec, 0.3, false, true, Long.MIN_VALUE);
   }

   private CaseMaxData getTextBreach(double breachValue, int breachBound, String text) {
      this.statusText = text;
      this.breachStatusText = null;
      double breachValue2 = Double.isFinite(breachValue) ? breachValue : 0.0;
      return new CaseMaxData(SurroundKitType.BREACH, true, breachValue2, Math.max(0, breachBound), false, false, text);
   }

   private void resetIfNotDigging() {
      if (!this.digxDug.isHoldingOrCancelling() && !this.obsidianPlacer.isFlag()) {
         if (this.phase != CampBreaching.BreachPhase.PUNISH && this.phase != CampBreaching.BreachPhase.FOLLOW) {
            this.phase = CampBreaching.BreachPhase.IDLE;
            this.campCounted = false;
            if (this.digxDug.hasActiveTarget()) {
               this.digxDug.cancelBreakIfHolding();
            }
         }
      }
   }

   public boolean isCampWatch() {
      return this.phase == CampBreaching.BreachPhase.CAMP && this.watching;
   }

   private static boolean isStandingOnCell(Vec3d vec, BlockPos pos) {
      if (vec != null && pos != null) {
         double campBreachingFactor = vec.x - (pos.getX() + 0.5);
         double campBreachingFactor2 = vec.z - (pos.getZ() + 0.5);
         return campBreachingFactor * campBreachingFactor + campBreachingFactor2 * campBreachingFactor2 <= 0.2025 && Math.abs(vec.y - pos.getY()) <= 0.6;
      } else {
         return false;
      }
   }

   static enum BreachPhase {
      IDLE,
      WALK,
      MINE,
      BUILD,
      FOLLOW,
      CAMP,
      PUNISH;
   }
}
