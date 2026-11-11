package com.corz.client.utils;

import com.corz.client.config.FriendNameStore;
import com.corz.client.config.FightStatTotals;
import com.corz.client.entity.WorldCollisionHelper;
import com.corz.client.entity.ComboingIntersects;
import com.corz.client.entity.DeflectZone;
import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.entity.VecMax;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.LivingEntityHelperVisible;
import com.corz.client.inventory.VecPos;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.TickStatusData;
import com.corz.client.world.VecPosUtilsAmount;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.SprintResetTracker;
import com.corz.client.utils.EntityHeightSamples;
import com.corz.client.utils.VelocityCpacket;
import com.corz.client.utils.AnchorStandPlanner;
import com.corz.client.utils.CrystalBotCombatStats;
import com.corz.client.utils.ComboPhaseTracker;
import com.corz.client.utils.CrystalStepPlanner;
import com.corz.client.utils.FinishingPressUtils;
import com.corz.client.utils.EpisodeScoring;
import com.corz.client.utils.BreakTimingClassifier;
import com.corz.client.utils.PlaystyleTuning;
import com.corz.client.utils.AbsorptionFoodUtils;
import com.corz.client.utils.EngagementGateRules;

public final class ComboPlanner {
   private boolean pearlThrown;
   private boolean lastPearlWasAggro;
   private long comboCooldownUntilTick;
   private boolean sampleGround;
   private ComboPlanner.SubPhase subPhase;
   private final Map<Long, Long> blockedPosExpiryTicks;
   private CenterVecData.State stateBeforePearlFollow;
   private final MaxMin defaultMaxMin;
   private long lastActionTick;
   private final AnchorStandPlanner anchorPlanner;
   private ComboPlanner.Proposal pendingProposal;
   private String targetContextKey;
   private long targetPearlLandedTick;
   private boolean finisherReached;
   private boolean aggroActive;
   private boolean proposalActive;
   private int selfPlayerId;
   private final AmountAmounts defaultKnockbackCalibration;
   private boolean offerStillValid;
   private double currentSpacing;
   private final LivingEntityHelperVisible targetTracker;
   private boolean targetPearlThrown;
   private String spacingChoice;
   private final Random random;
   public static final double[] PRESTAGE_RING_RADII = new double[]{0.8, 1.15, 1.5};
   private long strikeReadyTick;
   private final DeflectZone pearlShotPlanner;
   private final CrystalBotCombatStats eatabEatbad;
   private AnchorStandPlanner.Plan anchorPlan;
   private Vec3d selfPosAtStrike;
   private long launchBreakTick;
   private int targetEntityId;
   private long lastPrestageSearchTick;
   private final LinkedHashMap<Integer, ComboPlanner.OwnPearl> ownPearlsById;
   private int selfDamageEventCount;
   private float currentEhpDropStreak;
   private VecBlockingData currentTarget;
   private int targetLatencyMs;
   private static final SurroundKitType[] CANDIDATE_KITS = new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE};
   private double totalEhpDrop;
   private int selectedSlot;
   private ComboPlanner.TargetPearl targetPearl;
   private long lastStepSearchTick;
   private int targetPopCount;
   private String lastCounterReason;
   private int nearbyEntityCount;
   private long ownPearlThrowTick;
   private final VecPos offerFinder;
   private boolean strikeLanded;
   private final VecMax pearlTracker;
   private long stepPlanTick;
   private long ehpStreakDeadlineTick;
   private float selfDamageLimit;
   private final ComboingIntersects hitLoopCombo;
   private ComboPlanner.MeleeCheck pendingMeleeKnockbackCheck;
   private String proposalArm;
   private long slotChangeTick;
   private long lastOfferSearchTick;
   private final ComboPhaseTracker episodeStateMachine;
   private long crystalBoomTick;
   private VecPos.Offer activeOffer;
   private long lastPearlThrowTick;
   private ComboPlanner.Finish bestFinish;
   private boolean anchorDetonated;
   private long selfDropWindowEndTick;
   private long lastSeenBoomTick;
   private RoutedBreachingApi breachingApi;
   private long currentTick;
   private long lastCrystalBreakTick;
   private final RayBudget calibrationBudget;
   private long lastHelperTick;
   private final List<ComboPlanner.Break> recentCrystalBreaks;
   private float selfDropWindowTotal;
   private double loopTicks;
   private boolean usingLaunchCrystal;
   private ComboPlanner.Prestage prestagePlan;
   private boolean episodeActive;
   private final VelocityCpacket velocityCpacket = new VelocityCpacket();
   private final CrystalStepPlanner stepPlanner;
   private int juggleCount;
   private long lastSprintStrikeTick;
   private long lastCounterShotTick;
   private boolean pearlFailed;
   private long lastFinishEvalTick;
   private boolean anchorFailed;
   private MinVec opponentProfile;
   private long lastLatencyCheckTick;
   private final WorldCollisionHelper worldView;
   private BlockPos launchBase;
   private long recomboStartTick;
   private final CrystalPop crystalPop;
   private int targetFarTicks;
   private CrystalStepPlanner.Step currentStep;
   private long episodeStartTick;
   private long lastAnchorSearchTick;
   private final Map<Long, Integer> crystalFailCounts;
   private String finisherKind;
   private int crystalFailStreak;
   private CenterVecData.Abort pendingAbort;
   private long lastStrikeTick;
   private VecPos.Offer candidateOffer;
   private boolean eatabActive;
   private int comboYieldStreak;
   private long targetPopTick;
   private int eatabFlagBaseline;
   private CenterVecData.PearlShot counter;
   private String cachedSpacingChoice;
   private final SprintResetTracker sprintTimer = new SprintResetTracker();
   private long anchorDetonationTick;
   private double targetDamageTotal;
   private CenterVecData aggro;
   private List<VecPos.Offer> offers;

   private String chooseSpacingOption(String spacingPopText, boolean spacingPopFlag) {
      List list = CrystalPop.POP_ANCHOR_ITEMS;
      HashMap hashMap = new HashMap();

      for (String spacingPopText2 : (Iterable<java.lang.String>) (list)) {
         hashMap.put(spacingPopText2, 1.0);
      }

      String formatMinPop = this.pickLearnedOption(CrystalPop.Family.SPACING, spacingPopText, list, hashMap, spacingPopFlag);
      return formatMinPop == null ? "2.6" : formatMinPop;
   }

   private void onCrystalBroken(BlockPos pos, long time) {
      this.lastCrystalBreakTick = time;
      this.recentCrystalBreaks.add(new ComboPlanner.Break(BlockEntityUtils.getVec3d(pos), time));
      if (this.recentCrystalBreaks.size() > 16) {
         this.recentCrystalBreaks.remove(0);
      }

      CenterVecData.State followJuggleType = this.episodeStateMachine.getCurrentPhase();
      PosVecUtils.Sample sample = this.targetTracker.getDataGroundMax() == null ? null : this.targetTracker.getDataGroundMax().sample();
      if (sample != null && this.targetEntityId >= 0) {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         double livingEntityValue = pos.getY() + 1.0;
         double vecPosFactor = (double)EntityHeightSamples.countSamplesAbove(sample.y(), livingEntityValue) / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
         double rExpl = this.targetTracker.getDataGroundMax().rExpl();
         double[] livingEntityValues = VecPosUtilsAmount.getAmounts(sample.x(), sample.y(), sample.z(), 1.62, vec3d.x, vec3d.y, vec3d.z, 6.0, vecPosFactor, rExpl);
         double livingEntityFactor = this.getActiveKnockbackCalibration().getMeleeScale();
         if (Math.abs(livingEntityValues[1]) > 1.0E-6 || Math.abs(livingEntityValues[0]) > 1.0E-6) {
            this.targetTracker.handleTime(time, livingEntityValues[0] * livingEntityFactor, livingEntityValues[1] * livingEntityFactor, livingEntityValues[2] * livingEntityFactor, false);
            this.targetTracker.handle(time, livingEntityValues[1], sample.vy());
         }
      }

      if (followJuggleType == CenterVecData.State.OPENER && this.subPhase == ComboPlanner.SubPhase.LAUNCH_NOW && this.activeOffer != null && pos.equals(this.activeOffer.base())) {
         this.strikeLanded = true;
         this.launchBreakTick = time;
      } else if (followJuggleType == CenterVecData.State.LAUNCH && pos.equals(this.launchBase)) {
         this.launchBreakTick = time;
      }

      this.lastStepSearchTick = Long.MIN_VALUE;
   }

   public String formatDebugState() {
      StringBuilder stringBuilder = new StringBuilder(160);
      stringBuilder.append("cs=").append(this.episodeStateMachine.getCurrentPhase().name());
      if (this.subPhase != ComboPlanner.SubPhase.NONE) {
         stringBuilder.append('/').append(this.subPhase.name());
      }

      stringBuilder.append(" arm=").append(this.proposalArm == null ? "-" : this.proposalArm);
      CenterVecData.Proposal proposal = this.pendingProposal == null ? CenterVecData.Proposal.NONE : this.pendingProposal.kind();
      stringBuilder.append(" prop=").append(proposal.name()).append(':').append(String.format(Locale.ROOT, "%.1f", this.pendingProposal == null ? 0.0 : this.pendingProposal.value()));
      CrystalStepPlanner.Step directionVecData = this.currentStep;
      stringBuilder.append(" step=")
         .append(directionVecData == null ? "-" : directionVecData.base().toShortString())
         .append(" blast=")
         .append(directionVecData == null ? "-" : String.valueOf(directionVecData.breakTick() - this.currentTick))
         .append(" kind=")
         .append(directionVecData == null ? "-" : directionVecData.kind().name());
      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      stringBuilder.append(" lift=").append(seed == null ? "-" : seed.lift().name());
      stringBuilder.append(" rT=").append(this.targetTracker.getInterpolatorPingTargetData().getMax());
      stringBuilder.append(" y=").append(this.comboYieldStreak);
      stringBuilder.append(" cd=").append(this.comboCooldownUntilTick == Long.MIN_VALUE ? 0L : Math.max(0L, this.comboCooldownUntilTick - this.currentTick));
      stringBuilder.append(" pshot=").append(this.counter == null ? "-" : this.counter.why() + ":" + this.counter.flightTicks());
      stringBuilder.append(" fin=").append(this.bestFinish == null ? "-" : this.bestFinish.kind().name() + ":" + this.bestFinish.fire());
      stringBuilder.append(" ab=").append(this.pendingAbort.name());
      stringBuilder.append(' ').append(this.hitLoopCombo.getFormat());
      return stringBuilder.toString();
   }

   public void applyFeedback(ComboPlanner.Feedback feedback) {
      if (feedback != null) {
         long aggroTime = feedback.tick();
         TickStatusData tickStatusData = feedback.crystal() == null ? TickStatusData.INSTANCE : feedback.crystal();
         boolean aggroPearlFlag = this.aggro.state() == CenterVecData.State.HIT_LOOP;
         if (aggroPearlFlag) {
            this.hitLoopCombo
               .handleTimeKit(
                  aggroTime,
                  feedback.struck(),
                  feedback.strikeWasSprint(),
                  tickStatusData,
                  feedback.pearlThrown(),
                  feedback.pearlFailed(),
                  feedback.anchorDetonated(),
                  feedback.anchorFailed(),
                  feedback.targetPop(),
                  feedback.acted()
               );
         }

         boolean aggroPearlFlag2 = this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE;
         if (aggroPearlFlag2) {
            this.targetDamageTotal = this.targetDamageTotal + Math.max(0.0F, feedback.targetDamageDealt());
            this.totalEhpDrop = this.totalEhpDrop + Math.max(0.0F, feedback.selfEhpDrop());
            if (feedback.targetPop()) {
               this.targetPopCount++;
            }

            if (feedback.ownPop()) {
               this.selfDamageEventCount++;
            }
         }

         if (this.selfDropWindowEndTick != Long.MIN_VALUE) {
            this.selfDropWindowTotal = this.selfDropWindowTotal + Math.max(0.0F, feedback.selfEhpDrop());
            if (aggroTime >= this.selfDropWindowEndTick) {
               this.eatabEatbad.addShnsSample(this.selfDropWindowTotal);
               this.selfDropWindowEndTick = Long.MIN_VALUE;
               this.selfDropWindowTotal = 0.0F;
            }
         }

         if (this.ehpStreakDeadlineTick != Long.MIN_VALUE) {
            this.currentEhpDropStreak = this.currentEhpDropStreak + Math.max(0.0F, feedback.selfEhpDrop());
            if (aggroTime >= this.ehpStreakDeadlineTick) {
               this.eatabEatbad.addLearnSample(this.currentEhpDropStreak);
               this.ehpStreakDeadlineTick = Long.MIN_VALUE;
               this.currentEhpDropStreak = 0.0F;
            }
         }

         if (feedback.struck() && !aggroPearlFlag) {
            this.onStrikeLanded(feedback, aggroTime);
         }

         if (feedback.pearlThrown()) {
            this.pearlThrown = true;
            this.lastPearlThrowTick = aggroTime;
            this.ownPearlThrowTick = aggroTime;
            this.lastPearlWasAggro = this.aggro.pearl() != null && "aggro".equals(this.aggro.pearl().why());
         }

         if (feedback.pearlFailed()) {
            this.pearlFailed = true;
         }

         if (feedback.anchorDetonated()) {
            this.anchorDetonated = true;
            this.anchorDetonationTick = aggroTime;
            this.lastAnchorSearchTick = Long.MIN_VALUE;
            if (this.episodeStateMachine.getCurrentPhase() == CenterVecData.State.ANCHOR_BELOW) {
               this.eatabEatbad.incrementAbCount();
               this.ehpStreakDeadlineTick = aggroTime + 2L;
               this.currentEhpDropStreak = Math.max(0.0F, feedback.selfEhpDrop());
            }

            this.markEatabIfLaunched(aggroTime);
         }

         if (feedback.anchorFailed()) {
            this.anchorFailed = true;
            if (this.episodeStateMachine.getCurrentPhase() == CenterVecData.State.ANCHOR_BELOW && this.anchorPlan != null && this.anchorPlan.anchor() != null) {
               this.blockedPosExpiryTicks.put(this.anchorPlan.anchor().asLong(), aggroTime + 60L);
            }
         }

         if (tickStatusData.base() != null && tickStatusData.lastBreakTick() == aggroTime) {
            this.onCrystalBroken(tickStatusData.base(), aggroTime);
         }

         if (tickStatusData.base() != null && tickStatusData.lastPlaceTick() == aggroTime && this.isOnPearlPath(tickStatusData.base())) {
            this.eatabEatbad.incrementLearnEatabCount();
         }

         if (aggroPearlFlag2 && this.aggro.crystal() != null) {
            String directiveStatus = tickStatusData.directiveStatus();
            if (directiveStatus != null && isCrystalFailureStatus(directiveStatus)) {
               this.crystalFailStreak++;
               if (this.crystalFailStreak >= 3) {
                  BlockPos base = this.aggro.crystal().base();
                  if (base != null) {
                     int merge = this.crystalFailCounts.merge(base.asLong(), 1, Integer::sum);
                     if (merge >= 2) {
                        this.blockedPosExpiryTicks.put(base.asLong(), aggroTime + 60L);
                        this.crystalFailCounts.remove(base.asLong());
                     }
                  }

                  this.currentStep = null;
                  this.lastStepSearchTick = Long.MIN_VALUE;
                  this.prestagePlan = null;
                  this.lastPrestageSearchTick = Long.MIN_VALUE;
                  this.crystalFailStreak = 0;
               }
            } else if ("ok".equals(directiveStatus)) {
               this.crystalFailStreak = 0;
            }
         }
      }
   }

   public CenterVecData getLastDecision() {
      return this.aggro;
   }

   private static double getSpacingForChoice(String popText) {
      if (popText == null) {
         return ConstVec.popTable[1];
      } else {
         for (int i = 0; i < CrystalPop.POP_ANCHOR_ITEMS.size(); i++) {
            if (CrystalPop.POP_ANCHOR_ITEMS.get(i).equals(popText)) {
               return ConstVec.popTable[i];
            }
         }

         return ConstVec.popTable[1];
      }
   }

   private CenterVecData planIdleDecision(
      long chasingBelowValue,
      SelfPopData selfPopData,
      VecBlockingData vecBlockingData,
      AbsorptionPosData absorptionPosData,
      ClientPlayerEntity clientPlayerEntity,
      PearlsPlaystyleData pearlsPlaystyleData,
      CarefulBalancedType carefulBalancedType,
      SelfHazData selfHazData,
      double chasingBelowLimit,
      boolean chasingBelowFlag,
      boolean carefulDistanceFlag,
      boolean belowAnchorFlag,
      boolean chasingBelowFlag4,
      boolean chasingBelowFlag5,
      int counterVecLimit,
      TickStatusData tickStatusData,
      Predicate<Vec3d> predicate
   ) {
      this.subPhase = ComboPlanner.SubPhase.NONE;
      int ordinal = carefulBalancedType.ordinal();
      Vec3d blockingVec = this.getTargetPos();
      if (chasingBelowLimit > ConstVec.frozenAmounts[ordinal] && chasingBelowFlag) {
         CenterVecData.PearlShot pearlShot = pearlsPlaystyleData.aggroPearls() ? this.counter : null;
         CenterVecData.State state = pearlShot != null ? CenterVecData.State.PEARL_FOLLOW : CenterVecData.State.FOLLOW;
         this.pendingProposal = new ComboPlanner.Proposal(CenterVecData.Proposal.PURSUE, state, 0.0, null, null, null, null, this.currentSpacing);
         Vec3d subtractLength = this.getApproachPoint(blockingVec, absorptionPosData.pos());
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, subtractLength, 0.8, true, false, Long.MIN_VALUE);
         SurroundKitType surroundKitType = pearlShot != null ? SurroundKitType.PEARL : SurroundKitType.CRYSTAL;
         return this.buildDecision(
            CenterVecData.State.IDLE,
            CenterVecData.Proposal.PURSUE,
            0.0,
            surroundKitType,
            null,
            null,
            pearlShot,
            null,
            stand,
            blockingVec.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Chasing",
            null
         );
      } else if (this.comboCooldownUntilTick != Long.MIN_VALUE && chasingBelowValue < this.comboCooldownUntilTick) {
         this.pendingProposal = null;
         return CenterVecData.INSTANCE;
      } else if (pearlsPlaystyleData.combos() && !carefulDistanceFlag && (belowAnchorFlag || chasingBelowFlag4)) {
         CrystalStepPlanner.Step directionVecData = this.currentStep;
         if (chasingBelowFlag5 && directionVecData != null && directionVecData.value() >= 4.0 && directionVecData.breakTick() - chasingBelowValue <= Math.max(0, counterVecLimit - 1)) {
            this.pendingProposal = new ComboPlanner.Proposal(
               CenterVecData.Proposal.OPEN, CenterVecData.State.JUGGLE, directionVecData.value(), null, null, null, null, this.currentSpacing
            );
            return this.buildProposalDecision(this.planJuggleDecision(chasingBelowValue, selfPopData, vecBlockingData, absorptionPosData), CenterVecData.Proposal.OPEN, directionVecData.value(), null);
         } else {
            LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
            WeakFullUtils.Lift lift = seed == null ? WeakFullUtils.Lift.NONE : seed.lift();
            if (this.pendingProposal == null
               || this.pendingProposal.kind() == CenterVecData.Proposal.PURSUE
               || this.pendingProposal.state() == CenterVecData.State.JUGGLE
               || this.lastOfferSearchTick == chasingBelowValue
               || this.lastAnchorSearchTick == chasingBelowValue) {
               this.pendingProposal = this.chooseProposal(chasingBelowValue, selfPopData, vecBlockingData, absorptionPosData, pearlsPlaystyleData, carefulBalancedType, belowAnchorFlag, chasingBelowFlag4, lift, predicate, selfHazData);
            }

            if (this.pendingProposal == null) {
               return CenterVecData.INSTANCE;
            } else if (this.pendingProposal.kind() == CenterVecData.Proposal.OPEN && this.pendingProposal.offer() != null) {
               VecPos.Offer offer = this.pendingProposal.offer();
               ComboPlanner.SubPhase launchCrystal = this.getOfferEntrySubPhase(offer);
               CenterVecData openingOpener = this.planOpenerDecision(launchCrystal, offer, chasingBelowValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity, tickStatusData);
               return this.buildProposalDecision(openingOpener, CenterVecData.Proposal.OPEN, this.pendingProposal.value(), this.pendingProposal.arm());
            } else if (this.pendingProposal.kind() == CenterVecData.Proposal.ANCHOR && this.pendingProposal.anchor() != null) {
               CenterVecData anchoringBelow = this.buildAnchorDecision(this.pendingProposal.anchor(), chasingBelowValue, selfPopData, vecBlockingData, absorptionPosData, tickStatusData);
               return this.buildProposalDecision(anchoringBelow, CenterVecData.Proposal.ANCHOR, this.pendingProposal.value(), "ANCHOR_BELOW");
            } else {
               return CenterVecData.INSTANCE;
            }
         }
      } else {
         this.pendingProposal = null;
         return CenterVecData.INSTANCE;
      }
   }

   private boolean isRespawnAnchorAt(BlockPos pos) {
      World world = this.worldView.getWorld();
      return world != null && pos != null && world.getBlockState(pos).isOf(Blocks.RESPAWN_ANCHOR);
   }

   private CenterVecData.PearlShot planAggroPearlShot(
      long aggroPreemptValue,
      SelfPopData selfPopData,
      VecBlockingData vecBlockingData,
      AbsorptionPosData absorptionPosData,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      PearlsPlaystyleData pearlsPlaystyleData,
      CarefulBalancedType carefulBalancedType,
      double distanceTo,
      int livingSubtractBound,
      CenterVecData.State state
   ) {
      if (pearlsPlaystyleData.aggroPearls() && this.canThrowAggroPearl(absorptionPosData, pearlsPlaystyleData, aggroPreemptValue, carefulBalancedType)) {
         int ordinal = carefulBalancedType.ordinal();
         Vec3d pos = absorptionPosData.pos();
         Vec3d blockingVec = this.getTargetPos();
         double aggroPreemptLimit = WorldCollisionHelper.horizontalDistance(pos, blockingVec);
         if (aggroPreemptLimit > ConstVec.frozenAmounts[ordinal] && this.isPearlDistanceAllowed(carefulBalancedType, absorptionPosData, vecBlockingData, aggroPreemptLimit)) {
            int aggroPreemptBound = DeflectAmountsUtils.getAmounts(aggroPreemptLimit - 2.6);
            double hashRegistryValue = (aggroPreemptLimit - 3.0) / 0.26;
            if (hashRegistryValue > aggroPreemptBound + ConstVec.aggroValues[ordinal]) {
               Vec3d vec3d = LivingEntityHelperVisible.getVec3d(pos.subtract(blockingVec));
               PosVecUtils.Path sampleTick = this.targetTracker.getSampleTick(Math.max(aggroPreemptBound, 1) + 1, this.worldView, this.getActiveKnockbackCalibration(), vec3d, null);
               Vec3d vec3d2 = sampleTick == null ? blockingVec : getSamplePos(sampleTick.getDataForInt(aggroPreemptBound));
               int deflectZoneValue = (int)Math.floor(hashRegistryValue) - ConstVec.aggroValues[ordinal];
               CenterVecData.PearlShot instance = this.pearlShotPlanner.getInstance(vec3d2, pos, deflectZoneValue, aggroPreemptValue, this.worldView, vecBlock, list, absorptionPosData, 1.5, "aggro");
               if (instance != null) {
                  return instance;
               }
            }
         }

         if ((state == CenterVecData.State.JUGGLE || state == CenterVecData.State.FOLLOW)
            && carefulBalancedType != CarefulBalancedType.CAREFUL
            && this.targetTracker.isSampleGround()) {
            boolean aggroPreemptFlag = this.currentStep == null || this.currentStep.breakTick() - aggroPreemptValue > 6L;
            Vec3d livingSubtract = this.getPredictedTargetPos(livingSubtractBound, blockingVec);
            if (aggroPreemptFlag && pos.distanceTo(livingSubtract) - 3.0 > 0.26 * (livingSubtractBound + 6)) {
               return this.pearlShotPlanner.getInstance(livingSubtract, pos, livingSubtractBound + 4, aggroPreemptValue, this.worldView, vecBlock, list, absorptionPosData, 1.5, "preempt");
            }
         }

         return null;
      } else {
         return null;
      }
   }

   static boolean hasPearlArrived(long finishingVecValue, long finishingVecValue2, int finishingVecBound, long cheapHigh) {
      return finishingVecValue2 == Long.MIN_VALUE ? true : finishingVecValue + Math.max(0L, cheapHigh) >= finishingVecValue2 + Math.max(0, finishingVecBound);
   }

   private boolean canThrowAggroPearl(AbsorptionPosData absorptionPosData, PearlsPlaystyleData pearlsPlaystyleData, long aggroPearlsValue, CarefulBalancedType carefulBalancedType) {
      int aggroPearlsLimit = pearlsPlaystyleData.escapePearls() && absorptionPosData.totems() <= (carefulBalancedType == CarefulBalancedType.TERMINATOR ? 0 : 2) ? 1 : 0;
      if (absorptionPosData.pearls() <= aggroPearlsLimit) {
         return false;
      } else if (!(absorptionPosData.getAbsorptionHealth() > 6.0) && !absorptionPosData.totemConfirmed()) {
         return false;
      } else {
         return !absorptionPosData.pearlReady() ? false : this.lastPearlThrowTick == Long.MIN_VALUE || aggroPearlsValue - this.lastPearlThrowTick >= 20L;
      }
   }

   public void resetAll() {
      this.episodeStateMachine.reset();
      this.targetTracker.updateTimeFull();
      this.targetTracker.handleHandler(this.worldView);
      this.worldView.resetCache();
      this.pearlTracker.updateTime();
      this.velocityCpacket.updateAtomicConcurrent();
      this.velocityCpacket.setValue(Integer.MIN_VALUE);
      this.sprintTimer.reset();
      this.blockedPosExpiryTicks.clear();
      this.crystalFailCounts.clear();
      this.ownPearlsById.clear();
      this.recentCrystalBreaks.clear();
      this.currentTarget = null;
      this.targetEntityId = -1;
      this.opponentProfile = null;
      this.lastLatencyCheckTick = Long.MIN_VALUE;
      this.targetLatencyMs = 100;
      this.aggroActive = false;
      this.comboYieldStreak = 0;
      this.comboCooldownUntilTick = Long.MIN_VALUE;
      this.targetFarTicks = 0;
      this.aggro = CenterVecData.INSTANCE;
      this.pendingAbort = CenterVecData.Abort.NONE;
      this.lastPearlThrowTick = Long.MIN_VALUE;
      this.ownPearlThrowTick = Long.MIN_VALUE;
      this.slotChangeTick = Long.MIN_VALUE;
      this.selectedSlot = -1;
      this.lastSprintStrikeTick = Long.MIN_VALUE;
      this.hitLoopCombo.updateTimeIframe();
      this.clearEpisodeState();
      this.clearPlanningState();
      this.clearFeedbackFlags();
   }

   private CenterVecData buildDecision(
      CenterVecData.State state2,
      CenterVecData.Proposal proposal,
      double pshotOverrideValue,
      SurroundKitType surroundKitType,
      CenterVecData.CrystalStep crystalStep,
      CenterVecData.Strike strike,
      CenterVecData.PearlShot pearlShot,
      CenterVecData.AnchorStep anchorStep,
      CenterVecData.Stand stand,
      Vec3d vec,
      int pshotOverrideValue2,
      String pshotOverrideText,
      String pshotOverrideText2
   ) {
      return new CenterVecData(
         state2,
         proposal,
         pshotOverrideValue,
         this.pendingAbort,
         surroundKitType,
         crystalStep,
         strike,
         pearlShot,
         anchorStep,
         stand,
         vec,
         pshotOverrideValue2,
         this.comboYieldStreak,
         pshotOverrideText,
         pshotOverrideText2
      );
   }

   public SprintResetTracker getSprintTimer() {
      return this.sprintTimer;
   }

   private boolean shouldReplanSteps(long livingValue, TickStatusData tickStatusData) {
      if (this.lastStepSearchTick == Long.MIN_VALUE || livingValue < this.lastStepSearchTick || livingValue - this.lastStepSearchTick >= 2L) {
         return true;
      } else if (this.crystalBoomTick != this.lastSeenBoomTick) {
         return true;
      } else if (this.targetTracker.getFullValue() != this.lastHelperTick) {
         return true;
      } else if (this.targetTracker.isSampleGround() != this.sampleGround) {
         return true;
      } else if (tickStatusData.lastActionTick() != this.lastActionTick) {
         return true;
      } else {
         Vec3d blockingVec = this.getTargetPos();
         return blockingVec != null && this.worldView.getCrystalsInRadius(blockingVec, 3.0).size() != this.nearbyEntityCount;
      }
   }

   private boolean isTargetAbovePrestage(ComboPlanner.Prestage prestage, Vec3d vec) {
      if (prestage != null && vec != null && this.targetTracker.isFlag() && !this.targetTracker.isSampleGround()) {
         double livingEntityValue = WorldCollisionHelper.getAmount(vec.x, vec.z, prestage.base().getX() + 0.5, prestage.base().getZ() + 0.5);
         return livingEntityValue <= 1.6 && Math.abs(vec.y - prestage.top()) <= 1.6;
      } else {
         return false;
      }
   }

   private boolean isThreatLethal(SelfPopData selfPopData) {
      return selfPopData.threat() != null && selfPopData.threat().lethal();
   }

   private boolean hasTargetLandedSince(long livingValue) {
      return this.targetTracker.canTime(livingValue) ? true : livingValue != Long.MIN_VALUE && this.targetPearlLandedTick != Long.MIN_VALUE && this.targetPearlLandedTick >= livingValue;
   }

   private int getTicksUntilAttackReady(ClientPlayerEntity clientPlayerEntity, int attackProgressLimit, double attackProgressValue) {
      if (clientPlayerEntity == null) {
         return VecPosUtilsAmount.getValue(attackProgressValue) + 2;
      } else if (attackProgressLimit >= 0 && clientPlayerEntity.getInventory().getSelectedSlot() == attackProgressLimit) {
         float attackCooldownProgress = clientPlayerEntity.getAttackCooldownProgress(0.5F);
         if (attackCooldownProgress >= 0.95F) {
            return 0;
         } else {
            float attackProgressFactor = clientPlayerEntity.getAttackCooldownProgressPerTick();
            return Math.max(0, (int)Math.ceil((0.95F - attackCooldownProgress) * attackProgressFactor));
         }
      } else {
         return VecPosUtilsAmount.getValue(attackProgressValue) + 2;
      }
   }

   public void onVelocityPacket(long cpacketVelocityValue) {
      this.velocityCpacket.getAtomicConcurrent(tickx -> this.targetTracker.handleTimeVelocity(tickx, cpacketVelocityValue), 64);
   }

   private boolean isTargetAttackable(VecBlockingData vecBlockingData) {
      if (vecBlockingData != null && !vecBlockingData.immune()) {
         LivingEntity entity = vecBlockingData.entity();
         if (entity == null) {
            return true;
         } else if (entity.isAlive() && !entity.isRemoved()) {
            if (entity instanceof PlayerEntity playerEntity) {
               if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                  return false;
               }

               if (FriendNameStore.canFlag(playerEntity) || EntityPlayerUtilsLiving.canScoreboardTeam(playerEntity)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private String getProposalArmIfActive() {
      return this.episodeActive ? this.proposalArm : null;
   }

   public VelocityCpacket getVelocityCpacket() {
      return this.velocityCpacket;
   }

   public String getHitLoopSummary() {
      return this.hitLoopCombo.getFormat();
   }

   private void refreshTargetLatency(MinecraftClient client, VecBlockingData vecBlockingData, long time) {
      if (this.lastLatencyCheckTick == Long.MIN_VALUE || time - this.lastLatencyCheckTick >= 100L || time < this.lastLatencyCheckTick) {
         this.lastLatencyCheckTick = time;
         ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
         if (networkHandler != null && vecBlockingData.entity() != null) {
            PlayerListEntry playerListEntry = networkHandler.getPlayerListEntry(vecBlockingData.entity().getUuid());
            if (playerListEntry != null) {
               int latency = playerListEntry.getLatency();
               this.targetLatencyMs = latency > 0 ? latency : 100;
            }
         }
      }
   }

   public void setBreachingApi(RoutedBreachingApi api) {
      this.breachingApi = api;
   }

   private void resolvePendingKnockbackCheck(long predictedLivingLimit) {
      ComboPlanner.MeleeCheck data141 = this.pendingMeleeKnockbackCheck;
      if (data141 != null && predictedLivingLimit >= data141.at()) {
         this.pendingMeleeKnockbackCheck = null;
         Vec3d tickForLong = this.targetTracker.getTickForLong(data141.kickTick());
         Vec3d vec3d = this.targetTracker.getVec3dGroundHistList();
         if (tickForLong != null && vec3d != null && !this.targetTracker.canTime(data141.kickTick())) {
            double predictedLivingValue = WorldCollisionHelper.horizontalDistance(tickForLong, vec3d);
            this.getActiveKnockbackCalibration().handleAmount(data141.predicted(), predictedLivingValue);
            boolean predictedLivingFlag = data141.predicted() > 0.2 && predictedLivingValue < 0.7 * data141.predicted() || vec3d.y - tickForLong.y > data141.predictedRise() + 0.15;
            this.getActiveMaxMin().handle(predictedLivingFlag);
         }
      }
   }

   private MaxMin getActiveMaxMin() {
      return this.opponentProfile != null ? this.opponentProfile.getMaxMin() : this.defaultMaxMin;
   }

   static boolean isStepPlanCurrent(long finishingVecValue, long finishingVecValue2, long finishingVecLimit) {
      return finishingVecValue != Long.MIN_VALUE && finishingVecLimit >= finishingVecValue && finishingVecLimit - finishingVecValue <= 2L ? finishingVecValue2 + 1L >= finishingVecLimit : false;
   }

   private static boolean doesPathPassNearCell(BlockPos pos, List<Vec3d> list) {
      if (list != null && !list.isEmpty()) {
         Box expand = BlockEntityUtils.getBox(pos).expand(0.3);

         for (int i = 0; i + 1 < list.size(); i++) {
            Vec3d base2 = (Vec3d)list.get(i);
            Vec3d base3 = (Vec3d)list.get(i + 1);
            if (expand.contains(base2) || expand.contains(base3) || expand.raycast(base2, base3).isPresent()) {
               return true;
            }
         }

         return expand.contains((Vec3d)list.get(list.size() - 1));
      } else {
         return false;
      }
   }

   private CenterVecData planOpenerDecision(
      ComboPlanner.SubPhase sub, VecPos.Offer offer, long openingOpenerValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, ClientPlayerEntity clientPlayerEntity, TickStatusData tickStatusData
   ) {
      Vec3d blockingVec = this.getTargetPos();
      Vec3d lengthSquared = this.getOfferDirection(blockingVec, offer);
      double spacing = offer.spacing();
      Vec3d vec3d = new Vec3d(blockingVec.x - lengthSquared.x * spacing, blockingVec.y, blockingVec.z - lengthSquared.z * spacing);
      Vec3d vec3d2 = new Vec3d(blockingVec.x - lengthSquared.x * (spacing + 0.6), blockingVec.y, blockingVec.z - lengthSquared.z * (spacing + 0.6));
      Vec3d add = blockingVec.add(0.0, 1.2, 0.0);
      if (sub == ComboPlanner.SubPhase.LAUNCH_NOW) {
         CenterVecData.CrystalStep livingEntity = this.buildCrystalStep(offer.base(), offer.obsidianCell(), this.getPlacementFace(offer.base(), absorptionPosData.eye()), false, true);
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.HOLD, absorptionPosData.pos(), 0.1, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.OPENER,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            livingEntity,
            null,
            null,
            null,
            stand,
            BlockEntityUtils.getVec3d(offer.base()).add(0.0, 1.0, 0.0),
            Integer.MAX_VALUE,
            "Opening",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.CrystalStep crystalStep = offer.arm() == VecPos.Arm.KB_HIT_CRYSTAL
            ? this.buildCrystalStep(offer.base(), offer.obsidianCell(), this.getPlacementFace(offer.base(), vec3d.add(0.0, 1.62, 0.0)), true, false)
            : null;
         if (sub == ComboPlanner.SubPhase.STAGE) {
            CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.LINE_UP, vec3d, 0.35, false, false, Long.MIN_VALUE);
            return this.buildDecision(
               CenterVecData.State.OPENER,
               CenterVecData.Proposal.NONE,
               0.0,
               SurroundKitType.CRYSTAL,
               crystalStep,
               null,
               null,
               null,
               stand2,
               WorldCollisionHelper.getTopCenter(offer.base()),
               Integer.MAX_VALUE,
               "Opening",
               this.getProposalArmIfActive()
            );
         } else {
            float openingOpenerValue2 = VecShieldUtilsAmounts.getAmount(lengthSquared.x, lengthSquared.z);
            double vecShieldValue = Math.max(0.5, WorldCollisionHelper.horizontalDistance(absorptionPosData.pos(), blockingVec));
            float openingOpenerValue4 = sub == ComboPlanner.SubPhase.STRIKE ? (float)VecShieldUtilsAmounts.getMin(vecShieldValue) : -1.0F;
            CenterVecData.Strike strike = new CenterVecData.Strike(offer.weaponSlot(), offer.sprintHit(), openingOpenerValue2, openingOpenerValue4, true);
            long openingOpenerValue5 = !offer.sprintHit() || this.strikeReadyTick == Long.MIN_VALUE || sub != ComboPlanner.SubPhase.CHARGE && sub != ComboPlanner.SubPhase.STRIKE
               ? Long.MIN_VALUE
               : FinishingPressUtils.getPressLeadTime(this.strikeReadyTick);
            Vec3d vec3d3 = offer.sprintHit() ? vec3d2 : vec3d;
            CenterVecData.Stand stand3 = new CenterVecData.Stand(CenterVecData.StandKind.LINE_UP, vec3d3, 0.35, false, false, openingOpenerValue5);
            return this.buildDecision(
               CenterVecData.State.OPENER,
               CenterVecData.Proposal.NONE,
               0.0,
               SurroundKitType.MELEE,
               crystalStep,
               strike,
               null,
               null,
               stand3,
               add,
               Integer.MAX_VALUE,
               "Opening",
               this.getProposalArmIfActive()
            );
         }
      }
   }

   private boolean isTargetOffAnchorLayer(Vec3d vec, Vec3d vec2, double livingVecLimit) {
      AnchorStandPlanner.Plan vecPosData = this.anchorPlan;
      if (vecPosData == null) {
         return true;
      } else if (livingVecLimit > 6.0) {
         return true;
      } else if (!this.targetTracker.isFlag()) {
         return false;
      } else {
         double livingVecValue = vecPosData.stand().y + 1.0;
         return Math.abs(vec.y - livingVecValue) > 0.6 && Math.abs(vec.y - (livingVecValue + 1.0)) > 0.6;
      }
   }

   private void clearPlanningState() {
      this.clearStepPlan();
      this.offers = List.of();
      this.lastOfferSearchTick = Long.MIN_VALUE;
      this.candidateOffer = null;
      this.anchorPlan = null;
      this.lastAnchorSearchTick = Long.MIN_VALUE;
      this.counter = null;
      this.lastCounterShotTick = Long.MIN_VALUE;
      this.bestFinish = null;
      this.lastFinishEvalTick = Long.MIN_VALUE;
      this.prestagePlan = null;
      this.lastPrestageSearchTick = Long.MIN_VALUE;
      this.pendingProposal = null;
      this.pendingMeleeKnockbackCheck = null;
      this.cachedSpacingChoice = null;
   }

   private ComboPlanner.SubPhase computeOpenerSubPhase(long strikePrestageLimit, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, ClientPlayerEntity clientPlayerEntity) {
      if (this.activeOffer == null) {
         return ComboPlanner.SubPhase.NONE;
      } else if (this.activeOffer.arm() == VecPos.Arm.CRYSTAL_LAUNCH) {
         this.subPhase = ComboPlanner.SubPhase.LAUNCH_NOW;
         return this.subPhase;
      } else if (this.activeOffer.arm() == VecPos.Arm.KB_HIT_CRYSTAL && this.worldView.findCrystalAt(this.activeOffer.base()) == null && this.strikeReadyTick == Long.MIN_VALUE) {
         this.subPhase = ComboPlanner.SubPhase.STAGE;
         return this.subPhase;
      } else {
         Vec3d blockingVec = this.getTargetPos();
         Vec3d lengthSquared = this.getOfferDirection(blockingVec, this.activeOffer);
         double spacing = this.activeOffer.spacing();
         Vec3d vec3d = new Vec3d(blockingVec.x - lengthSquared.x * spacing, blockingVec.y, blockingVec.z - lengthSquared.z * spacing);
         Vec3d vec3d2 = new Vec3d(blockingVec.x - lengthSquared.x * (spacing + 0.6), blockingVec.y, blockingVec.z - lengthSquared.z * (spacing + 0.6));
         Vec3d vec3d3 = this.activeOffer.sprintHit() ? vec3d2 : vec3d;
         boolean strikePrestageFlag = WorldCollisionHelper.horizontalDistance(absorptionPosData.pos(), vec3d3) <= 0.35;
         if (this.strikeReadyTick != Long.MIN_VALUE && strikePrestageLimit > this.strikeReadyTick + 6L) {
            this.strikeReadyTick = Long.MIN_VALUE;
            this.subPhase = ComboPlanner.SubPhase.LINE_UP;
         }

         if (this.strikeReadyTick == Long.MIN_VALUE) {
            if (!strikePrestageFlag) {
               this.subPhase = ComboPlanner.SubPhase.LINE_UP;
               return this.subPhase;
            } else if (!this.activeOffer.sprintHit()) {
               this.subPhase = ComboPlanner.SubPhase.STRIKE;
               return this.subPhase;
            } else {
               int attackProgress = this.getTicksUntilAttackReady(clientPlayerEntity, this.activeOffer.weaponSlot(), this.activeOffer.hit() == VecPos.Hit.PUNCH_SPRINT ? 4.0 : 1.6);
               int strikePrestageValue = Math.max(4, Math.max(attackProgress, Math.max(0, selfPopData.targetReadyIn())));
               this.strikeReadyTick = strikePrestageLimit + strikePrestageValue;
               this.subPhase = ComboPlanner.SubPhase.CHARGE;
               return this.subPhase;
            }
         } else {
            this.subPhase = strikePrestageLimit >= FinishingPressUtils.getPressLeadTime(this.strikeReadyTick) ? ComboPlanner.SubPhase.STRIKE : ComboPlanner.SubPhase.CHARGE;
            return this.subPhase;
         }
      }
   }

   private CenterVecData buildAnchorDecision(
      AnchorStandPlanner.Plan plan, long anchoringBelowValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, TickStatusData tickStatusData
   ) {
      Vec3d center = Vec3d.ofCenter(plan.anchor());
      if (!plan.arrived()) {
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.BELOW_LAYER, plan.stand(), 0.25, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.ANCHOR_BELOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            null,
            null,
            null,
            null,
            stand,
            center,
            Integer.MAX_VALUE,
            "Anchoring",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.AnchorStep anchorStep = new CenterVecData.AnchorStep(plan.anchor(), selfPopData.targetReadyIn() <= 0);
         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.BELOW_LAYER, plan.stand(), 0.25, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.ANCHOR_BELOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.ANCHOR,
            null,
            null,
            null,
            anchorStep,
            stand2,
            center,
            Integer.MAX_VALUE,
            "Anchoring",
            this.getProposalArmIfActive()
         );
      }
   }

   private void onStateTransition(
      CenterVecData.State state, CenterVecData.State state2, long time, PearlsPlaystyleData pearlsPlaystyleData, VecBlockingData vecBlockingData, TickStatusData tickStatusData
   ) {
      this.episodeStartTick = time;
      switch (state2) {
         case OPENER:
            this.clearStepPlan();
            if (this.candidateOffer != null) {
               this.activeOffer = this.candidateOffer;
               this.proposalActive = true;
            }

            this.strikeReadyTick = Long.MIN_VALUE;
            this.offerStillValid = true;
            this.subPhase = this.activeOffer == null ? ComboPlanner.SubPhase.NONE : this.getOfferEntrySubPhase(this.activeOffer);
            break;
         case LAUNCH:
            this.eatabEatbad.incrementLatCount();
            this.launchBreakTick = Long.MIN_VALUE;
            if (state == CenterVecData.State.OPENER && this.activeOffer != null) {
               this.launchBase = this.activeOffer.base();
               this.usingLaunchCrystal = this.activeOffer.arm() == VecPos.Arm.CRYSTAL_LAUNCH;
               if (this.usingLaunchCrystal) {
                  this.launchBreakTick = this.lastCrystalBreakTick;
               }
            } else if (this.prestagePlan != null) {
               this.launchBase = this.prestagePlan.base();
               this.usingLaunchCrystal = true;
            }

            this.subPhase = ComboPlanner.SubPhase.NONE;
            break;
         case JUGGLE:
            if (state == CenterVecData.State.LAUNCH || state == CenterVecData.State.ANCHOR_BELOW) {
               this.eatabEatbad.incrementLaCount();
               this.juggleCount++;
               this.getActiveMaxMin().handleMaxMin(time);
            }

            this.lastStepSearchTick = Long.MIN_VALUE;
            this.subPhase = ComboPlanner.SubPhase.NONE;
            break;
         case FOLLOW:
            this.lastStepSearchTick = Long.MIN_VALUE;
            this.prestagePlan = null;
            this.lastPrestageSearchTick = Long.MIN_VALUE;
            this.subPhase = ComboPlanner.SubPhase.NONE;
            break;
         case PEARL_FOLLOW:
            this.clearStepPlan();
            this.stateBeforePearlFollow = state == CenterVecData.State.JUGGLE ? CenterVecData.State.JUGGLE : CenterVecData.State.FOLLOW;
            this.subPhase = ComboPlanner.SubPhase.NONE;
            break;
         case ANCHOR_BELOW:
            this.clearStepPlan();
            this.subPhase = ComboPlanner.SubPhase.ANCHOR;
            break;
         case RECOMBO:
            this.eatabEatbad.incrementRcStreakCount();
            this.recomboStartTick = time;
            this.eatabActive = false;
            this.prestagePlan = null;
            this.lastPrestageSearchTick = Long.MIN_VALUE;
            this.clearStepPlan();
            this.bestFinish = null;
            this.lastFinishEvalTick = Long.MIN_VALUE;
            this.counter = null;
            this.lastCounterShotTick = Long.MIN_VALUE;
            this.subPhase = ComboPlanner.SubPhase.RUN;
            break;
         case FINISHER:
            this.eatabEatbad.incrementShnsCount();
            this.finisherReached = true;
            this.finisherKind = this.bestFinish == null ? null : this.getFinishSurfaceLabel(this.bestFinish);
            this.subPhase = ComboPlanner.SubPhase.NONE;
            break;
         default:
            this.subPhase = ComboPlanner.SubPhase.NONE;
      }
   }

   private boolean isStepPlanningState(CenterVecData.State state) {
      return state == CenterVecData.State.JUGGLE
         || state == CenterVecData.State.FOLLOW
         || state == CenterVecData.State.LAUNCH
         || state == CenterVecData.State.FINISHER
         || state == CenterVecData.State.ANCHOR_BELOW && this.subPhase == ComboPlanner.SubPhase.CRYSTAL_BELOW;
   }

   public void endEpisode(String fightEndText, boolean fightEndFlag, boolean lost) {
      if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
         this.episodeStateMachine.endEpisode(fightEndText == null ? "fight end" : fightEndText);
         this.recordEpisodeOutcome(this.currentTick, fightEndText == null ? "fight end" : fightEndText, false, fightEndFlag, lost, CenterVecData.Abort.NONE);
      } else if (this.episodeActive) {
         this.recordEpisodeOutcome(this.currentTick, fightEndText == null ? "fight end" : fightEndText, false, fightEndFlag, lost, CenterVecData.Abort.NONE);
      }

      this.hitLoopCombo.updateTimeIframe();
      this.hitLoopCombo.updateIframeComboing();
      this.clearPlanningState();
      this.clearFeedbackFlags();
      this.pendingProposal = null;
      this.aggro = CenterVecData.INSTANCE;
   }

   private Vec3d getTargetPos() {
      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      if (seed != null) {
         return getSamplePos(seed.sample());
      } else if (this.currentTarget != null && this.currentTarget.serverPos() != null) {
         return this.currentTarget.serverPos();
      } else {
         return this.currentTarget != null && this.currentTarget.pos() != null ? this.currentTarget.pos() : new Vec3d(0.0, 0.0, 0.0);
      }
   }

   public String getActiveProposalArmText() {
      return this.episodeActive ? this.proposalArm : null;
   }

   private void recordEpisodeOutcome(long finishingVecValue, String finishingVecText, boolean finishingVecFlag, boolean won, boolean lost, CenterVecData.Abort abort) {
      if (!this.episodeActive) {
         this.clearEpisodeState();
      } else {
         this.eatabEatbad.recordShns(finishingVecFlag);
         int max2 = Math.max(0, this.eatabEatbad.getLongRunTotal() - this.eatabFlagBaseline);
         if (this.opponentProfile != null) {
            if (this.proposalActive) {
               double outcomeScore = EpisodeScoring.computeOutcomeScore(this.targetDamageTotal, this.totalEhpDrop, this.targetPopCount, this.selfDamageEventCount, won, lost);
               CrystalPop crystalPop = this.opponentProfile.getCrystalPop();
               if (this.proposalArm != null) {
                  crystalPop.handle(CrystalPop.Family.OPENER, this.targetContextKey, this.proposalArm, outcomeScore);
               }

               if (this.spacingChoice != null) {
                  crystalPop.handle(CrystalPop.Family.SPACING, this.targetContextKey, this.spacingChoice, outcomeScore);
               }

               if (this.finisherReached && this.finisherKind != null) {
                  crystalPop.handle(CrystalPop.Family.FINISHER, this.targetContextKey, this.finisherKind, outcomeScore);
               }
            }

            FightStatTotals classPearlJson = this.opponentProfile.getClassPearlJson();
            classPearlJson.juggleStrikeTotal = classPearlJson.juggleStrikeTotal + this.juggleCount;
            this.opponentProfile.getClassPearlJson().eatabActionTotal += max2;
            this.opponentProfile.updateFlag();
         }

         if (abort == CenterVecData.Abort.POSTURE
            || abort == CenterVecData.Abort.OWN_POP
            || abort == CenterVecData.Abort.LOW_HP
            || abort == CenterVecData.Abort.LETHAL_THREAT
            || abort == CenterVecData.Abort.HEAL) {
            this.comboCooldownUntilTick = finishingVecValue + 40L;
         }

         this.clearEpisodeState();
      }
   }

   public void onEntityPacketEvent(ServerPacketEventQueue.Event event, long time, int aggroVecValue) {
      if (event != null) {
         this.selfPlayerId = aggroVecValue;
         if (event instanceof ServerPacketEventQueue.Spawn spawn) {
            if (spawn.type() == EntityType.ENDER_PEARL) {
               this.pearlTracker.handle(spawn.entityId(), spawn.data(), time);
               if (spawn.data() == this.targetEntityId && this.targetEntityId >= 0) {
                  boolean aggroVecFlag = this.currentTarget != null && this.currentTarget.getHealthTrusted() <= 10.0F;
                  this.targetPearl = new ComboPlanner.TargetPearl(spawn.entityId(), new Vec3d(spawn.x(), spawn.y(), spawn.z()), time, aggroVecFlag);
                  this.targetPearlLandedTick = Long.MIN_VALUE;
                  this.targetPearlThrown = true;
               } else if (spawn.data() == aggroVecValue) {
                  boolean aggroVecFlag2 = this.ownPearlThrowTick != Long.MIN_VALUE && time - this.ownPearlThrowTick <= 10L && this.lastPearlWasAggro;
                  if (this.ownPearlThrowTick != Long.MIN_VALUE && time - this.ownPearlThrowTick <= 10L) {
                     this.ownPearlsById.put(spawn.entityId(), new ComboPlanner.OwnPearl(aggroVecFlag2, time));
                     this.ownPearlThrowTick = Long.MIN_VALUE;
                  }
               }
            }
         } else if (event instanceof ServerPacketEventQueue.Destroy destroy) {
            int[] ids = destroy.ids();
            if (ids != null) {
               for (int vecValue : ids) {
                  ComboPlanner.OwnPearl ownPearl = this.ownPearlsById.remove(vecValue);
                  if (ownPearl != null) {
                     VecMax.Forecast vecMax = this.pearlTracker.getVecMaxVecEntryMap(vecValue);
                     Vec3d blockingVec = this.getTargetPos();
                     boolean aggroVecBound = vecMax != null && blockingVec != null && vecMax.landing().distanceTo(blockingVec) <= 4.0;
                     this.eatabEatbad.recordJmax(ownPearl.aggro(), aggroVecBound);
                  }

                  if (this.targetPearl != null && this.targetPearl.id() == vecValue) {
                     VecMax.Forecast vecMaxVecEntryMap = this.pearlTracker.getVecMaxVecEntryMap(vecValue);
                     long cheapHigh = 4L + (long)Math.ceil(this.targetTracker.getInterpolatorPingTargetData().getPingMs());
                     if (hasPearlArrived(time, vecMaxVecEntryMap == null ? Long.MIN_VALUE : vecMaxVecEntryMap.seenTick(), vecMaxVecEntryMap == null ? 0 : vecMaxVecEntryMap.eta(), cheapHigh)) {
                        this.targetPearlLandedTick = time;
                     }

                     if (vecMaxVecEntryMap != null) {
                        Vec3d entityPos = this.getSelfPos();
                        double aggroVecValue4 = entityPos == null ? 0.0 : WorldCollisionHelper.horizontalDistance(this.targetPearl.from(), entityPos);
                        double aggroVecValue5 = entityPos == null ? 0.0 : WorldCollisionHelper.horizontalDistance(vecMaxVecEntryMap.landing(), entityPos);
                        int limit = MaxMin.getValue(vecMaxVecEntryMap.landing().y - this.targetPearl.from().y, aggroVecValue5 - aggroVecValue4);
                        this.getActiveMaxMin().handleTimeMax(this.targetPearl.tick(), this.targetPearl.low(), limit);
                     }
                  }
               }
            }

            this.pearlTracker.handleVecMax(ids, time);
         } else if (!(event instanceof ServerPacketEventQueue.Boom boom)) {
            if (event instanceof ServerPacketEventQueue.Pop pop && pop.entityId() == this.targetEntityId && this.targetEntityId >= 0) {
               this.targetPopTick = time;
               this.getActiveMaxMin().handleMax(time);
            }
         } else if (boom.center() != null) {
            long aggroVecValue7 = (long)Math.ceil(this.loopTicks) + 2L;
            Iterator iterator = this.recentCrystalBreaks.iterator();

            while (iterator.hasNext()) {
               ComboPlanner.Break comboPlannerBreak = (ComboPlanner.Break)iterator.next();
               if (time - comboPlannerBreak.tick() > aggroVecValue7) {
                  iterator.remove();
               } else if (comboPlannerBreak.centre().distanceTo(boom.center()) <= 0.5) {
                  iterator.remove();
                  this.crystalBoomTick = time;
                  if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
                     this.eatabEatbad.recordJmaxTick(this.targetTracker.isSampleGround(), time);
                     if (this.episodeStateMachine.getCurrentPhase() == CenterVecData.State.LAUNCH || this.launchBreakTick != Long.MIN_VALUE) {
                        this.selfDropWindowEndTick = time + 3L;
                        this.selfDropWindowTotal = 0.0F;
                     }

                     this.markEatabIfLaunched(time);
                  }
                  break;
               }
            }
         }
      }
   }

   private String getFinishSurfaceLabel(ComboPlanner.Finish finish) {
      return switch (finish.kind()) {
         case ANCHOR -> "ANCHOR";
         case MELEE -> "GROUND";
         default -> finish.airborne() ? "AIR" : "GROUND";
      };
   }

   private ComboPlanner.SubPhase chooseRecomboSubPhase(
      long counterTime,
      SelfPopData selfPopData,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      PearlsPlaystyleData pearlsPlaystyleData,
      CarefulBalancedType carefulBalancedType,
      VecMax.Forecast forecast,
      Vec3d vec
   ) {
      if (forecast == null) {
         return ComboPlanner.SubPhase.RUN;
      } else if (pearlsPlaystyleData.combos() && this.prestagePlan != null) {
         return ComboPlanner.SubPhase.PRESTAGE;
      } else {
         boolean counterRunFlag = carefulBalancedType != CarefulBalancedType.CAREFUL || vecBlockingData.getHealthTrusted() <= 9.0F;
         if (pearlsPlaystyleData.aggroPearls() && counterRunFlag && vec.distanceTo(forecast.landing()) - 3.0 > 0.26 * (forecast.eta() + 6) && this.canThrowAggroPearl(absorptionPosData, pearlsPlaystyleData, counterTime, carefulBalancedType)) {
            if (this.canRecomputeCounterShot(counterTime)) {
               this.counter = this.pearlShotPlanner.getInstance(forecast.landing(), vec, forecast.eta() + 4, counterTime, this.worldView, vecBlock, list, absorptionPosData, 1.5, "counter");
               this.lastCounterShotTick = counterTime;
            }

            if (this.counter != null && "counter".equals(this.counter.why())) {
               return ComboPlanner.SubPhase.COUNTER;
            }
         }

         return ComboPlanner.SubPhase.RUN;
      }
   }

   private boolean isAnchorCellAdjacentToTarget(BlockPos pos) {
      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      if (seed != null && seed.grounded()) {
         PosVecUtils.Sample sample = seed.sample();
         int flooredLivingValue = (int)Math.floor(sample.y() + 1.0E-6);
         return areCellsAdjacent(pos, BlockPos.ofFloored(sample.x(), flooredLivingValue + 0.01, sample.z()));
      } else {
         return true;
      }
   }

   private void refreshActiveOffer() {
      if (this.activeOffer != null) {
         if (this.strikeReadyTick != Long.MIN_VALUE) {
            this.offerStillValid = true;
         } else {
            VecPos.Offer next = null;

            for (VecPos.Offer offer : this.offers) {
               if (offer.arm() == this.activeOffer.arm() && offer.base().equals(this.activeOffer.base()) && (next == null || offer.value() > next.value())) {
                  next = offer;
               }
            }

            this.offerStillValid = next != null;
            if (next != null) {
               this.activeOffer = next;
            }
         }
      }
   }

   private void clearFeedbackFlags() {
      this.strikeLanded = false;
      this.pearlThrown = false;
      this.pearlFailed = false;
      this.anchorDetonated = false;
      this.anchorFailed = false;
      this.targetPearlThrown = false;
      this.targetPearl = null;
      this.targetPearlLandedTick = Long.MIN_VALUE;
      this.crystalBoomTick = Long.MIN_VALUE;
      this.targetPopTick = Long.MIN_VALUE;
      this.anchorDetonationTick = Long.MIN_VALUE;
      this.crystalFailStreak = 0;
   }

   private SurroundKitType pickBestKit(SelfPopData selfPopData) {
      SurroundKitType cfgKit = SurroundKitType.CRYSTAL;
      double cfgKitValue = Double.NEGATIVE_INFINITY;
      boolean cfgKitFlag = selfPopData.cfg() != null && selfPopData.cfg().anchors() && !selfPopData.nether();

      for (SurroundKitType surroundKitType : CANDIDATE_KITS) {
         if (surroundKitType != SurroundKitType.ANCHOR || cfgKitFlag) {
            CaseMaxData options = selfPopData.getOptions(surroundKitType);
            if (options != null && options.available()) {
               double cfgKitLimit = options.value() + (surroundKitType == selfPopData.lastLane() ? 1.0 : 0.0);
               if (cfgKitLimit > cfgKitValue) {
                  cfgKitValue = cfgKitLimit;
                  cfgKit = surroundKitType;
               }
            }
         }
      }

      return cfgKit;
   }

   private CenterVecData.CrystalStep buildCrystalStep(BlockPos pos, BlockPos pos2, Direction direction, boolean livingEntityFlag, boolean chasingCounterFlag) {
      if (pos == null) {
         return null;
      } else {
         BlockPos blockPos = pos2 != null && !this.worldView.canPlaceOnBedrockObsidian(pos2) ? pos2 : null;
         PosVecUtils.Sample sample = this.targetTracker.getDataGroundMax() == null ? null : this.targetTracker.getDataGroundMax().sample();
         Vec3d vec3d = sample == null ? null : new Vec3d(sample.x(), sample.y(), sample.z());
         Box box = sample == null ? null : WorldCollisionHelper.getBoxFromSample(sample, 0.0);
         return new CenterVecData.CrystalStep(pos, blockPos, direction, livingEntityFlag, chasingCounterFlag, true, vec3d, box);
      }
   }

   private boolean selectCandidateOffer(ClientPlayerEntity clientPlayerEntity, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData, SelfPopData selfPopData, Vec3d vec, Vec3d vec2) {
      this.candidateOffer = null;
      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      if (seed != null && seed.grounded() && seed.groundedUpdates() >= 1) {
         if (seed.lift() == WeakFullUtils.Lift.NONE) {
            return false;
         } else {
            int index = selfPopData.cfg().playstyle() == null ? 0 : selfPopData.cfg().playstyle().ordinal();
            double crystalPlaystyleLimit = WorldCollisionHelper.horizontalDistance(vec, vec2);
            if (crystalPlaystyleLimit > ConstVec.frozenAmounts[index] + 1.5) {
               return false;
            } else {
               boolean crystalPlaystyleFlag = crystalPlaystyleLimit <= 3.6 && selfPopData.targetReadyIn() <= 2;
               VecPos.Offer offer = null;
               VecPos.Offer offer2 = null;

               for (VecPos.Offer offer3 : this.offers) {
                  if (!(offer3.value() < 4.0)) {
                     boolean crystalPlaystyleFlag2 = offer3.arm() == VecPos.Arm.KB_HIT_CRYSTAL && this.worldView.findCrystalAt(offer3.base()) == null;
                     boolean crystalPlaystyleFlag3 = offer3.arm() == VecPos.Arm.CRYSTAL_LAUNCH
                        || this.getTicksUntilAttackReady(clientPlayerEntity, offer3.weaponSlot(), offer3.hit() == VecPos.Hit.PUNCH_SPRINT ? 4.0 : 1.6) <= 2;
                     if (!crystalPlaystyleFlag2 && crystalPlaystyleFlag3 && crystalPlaystyleFlag) {
                        if (offer == null || offer3.value() > offer.value()) {
                           offer = offer3;
                        }
                     } else if (offer2 == null || offer3.value() > offer2.value()) {
                        offer2 = offer3;
                     }
                  }
               }

               this.candidateOffer = offer != null ? offer : offer2;
               return this.candidateOffer != null;
            }
         }
      } else {
         return false;
      }
   }

   private Predicate<Vec3d> combineWithBlockedPositions(Predicate<Vec3d> predicate) {
      Predicate predicate2 = predicate == null ? obj -> false : predicate;
      return vec -> predicate2.test(vec) || this.isPositionBlocked(BlockPos.ofFloored(vec.x, vec.y - 0.5, vec.z));
   }

   public ComboPlanner(RayBudget max, Random random) {
      this.pearlTracker = new VecMax();
      this.eatabEatbad = new CrystalBotCombatStats();
      this.worldView = new WorldCollisionHelper();
      this.targetTracker = new LivingEntityHelperVisible();
      this.pearlShotPlanner = new DeflectZone();
      this.anchorPlanner = new AnchorStandPlanner();
      this.episodeStateMachine = new ComboPhaseTracker();
      this.hitLoopCombo = new ComboingIntersects();
      this.defaultKnockbackCalibration = new AmountAmounts();
      this.defaultMaxMin = new MaxMin();
      this.blockedPosExpiryTicks = new HashMap<>();
      this.crystalFailCounts = new HashMap<>();
      this.ownPearlsById = new LinkedHashMap<>();
      this.recentCrystalBreaks = new ArrayList<>();
      this.targetEntityId = -1;
      this.targetLatencyMs = 100;
      this.lastLatencyCheckTick = Long.MIN_VALUE;
      this.currentTick = Long.MIN_VALUE;
      this.selfPlayerId = Integer.MIN_VALUE;
      this.loopTicks = 3.0;
      this.selfDamageLimit = 6.0F;
      this.comboCooldownUntilTick = Long.MIN_VALUE;
      this.aggro = CenterVecData.INSTANCE;
      this.pendingAbort = CenterVecData.Abort.NONE;
      this.subPhase = ComboPlanner.SubPhase.NONE;
      this.episodeStartTick = Long.MIN_VALUE;
      this.currentSpacing = ConstVec.popTable[1];
      this.strikeReadyTick = Long.MIN_VALUE;
      this.lastStrikeTick = Long.MIN_VALUE;
      this.lastSprintStrikeTick = Long.MIN_VALUE;
      this.launchBreakTick = Long.MIN_VALUE;
      this.lastCrystalBreakTick = Long.MIN_VALUE;
      this.offerStillValid = true;
      this.lastStepSearchTick = Long.MIN_VALUE;
      this.stepPlanTick = Long.MIN_VALUE;
      this.lastSeenBoomTick = Long.MIN_VALUE;
      this.lastHelperTick = Long.MIN_VALUE;
      this.lastActionTick = Long.MIN_VALUE;
      this.nearbyEntityCount = -1;
      this.offers = List.of();
      this.lastOfferSearchTick = Long.MIN_VALUE;
      this.lastAnchorSearchTick = Long.MIN_VALUE;
      this.lastCounterShotTick = Long.MIN_VALUE;
      this.lastFinishEvalTick = Long.MIN_VALUE;
      this.lastPrestageSearchTick = Long.MIN_VALUE;
      this.anchorDetonationTick = Long.MIN_VALUE;
      this.crystalBoomTick = Long.MIN_VALUE;
      this.targetPopTick = Long.MIN_VALUE;
      this.targetPearlLandedTick = Long.MIN_VALUE;
      this.stateBeforePearlFollow = CenterVecData.State.FOLLOW;
      this.recomboStartTick = Long.MIN_VALUE;
      this.lastPearlThrowTick = Long.MIN_VALUE;
      this.ownPearlThrowTick = Long.MIN_VALUE;
      this.slotChangeTick = Long.MIN_VALUE;
      this.selectedSlot = -1;
      this.selfDropWindowEndTick = Long.MIN_VALUE;
      this.ehpStreakDeadlineTick = Long.MIN_VALUE;
      this.targetContextKey = "";
      this.calibrationBudget = max;
      this.random = random == null ? new Random() : random;
      this.offerFinder = new VecPos(max);
      this.stepPlanner = new CrystalStepPlanner(max);
      this.targetTracker.handleHandler(this.worldView);
      this.crystalPop = new CrystalPop(this.random);
   }

   private CenterVecData planJuggleDecision(long jugglingJuggleValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData) {
      CrystalStepPlanner.Step directionVecData = this.currentStep;
      Vec3d blockingVec = this.getTargetPos();
      if (directionVecData == null) {
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.UNDER_TARGET, absorptionPosData.pos(), 0.4, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.JUGGLE,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            null,
            null,
            null,
            null,
            stand,
            blockingVec.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Juggling",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.CrystalStep noneBreak = this.buildStepCrystalStep(jugglingJuggleValue, directionVecData, false);
         boolean jugglingJuggleFlag = WorldCollisionHelper.horizontalDistance(absorptionPosData.pos(), directionVecData.stand()) > 2.0;
         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.UNDER_TARGET, directionVecData.stand(), 0.4, jugglingJuggleFlag, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.JUGGLE,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            noneBreak,
            null,
            null,
            null,
            stand2,
            BlockEntityUtils.getVec3d(directionVecData.base()).add(0.0, 1.0, 0.0),
            Integer.MAX_VALUE,
            "Juggling",
            this.getProposalArmIfActive()
         );
      }
   }

   private CenterVecData buildProposalDecision(CenterVecData centerVecData, CenterVecData.Proposal proposal, double pointYieldsValue, String pointYieldsText) {
      return new CenterVecData(
         CenterVecData.State.IDLE,
         proposal,
         pointYieldsValue,
         centerVecData.abort(),
         centerVecData.lane(),
         centerVecData.crystal(),
         centerVecData.strike(),
         centerVecData.pearl(),
         centerVecData.anchor(),
         centerVecData.stand(),
         centerVecData.aimPoint(),
         centerVecData.killInTicks(),
         centerVecData.yields(),
         centerVecData.status(),
         pointYieldsText
      );
   }

   private CenterVecData finalizeDecision(CenterVecData aggro) {
      if (aggro == CenterVecData.INSTANCE && this.pendingAbort != CenterVecData.Abort.NONE) {
         aggro = new CenterVecData(
            CenterVecData.State.IDLE,
            CenterVecData.Proposal.NONE,
            0.0,
            this.pendingAbort,
            SurroundKitType.NONE,
            null,
            null,
            null,
            null,
            null,
            null,
            Integer.MAX_VALUE,
            this.comboYieldStreak,
            null,
            null
         );
      }

      this.aggro = aggro;
      return aggro;
   }

   private CenterVecData.CrystalStep buildStepCrystalStep(long noneBreakLimit, CrystalStepPlanner.Step step, boolean noneBreakFlag) {
      BreakTimingClassifier.Kind kind = step.breakTick() <= noneBreakLimit ? step.kind() : BreakTimingClassifier.Kind.NONE;
      boolean chasingCounterFlag = !noneBreakFlag && kind != BreakTimingClassifier.Kind.NONE;
      BlockPos blockPos = null;

      for (BlockPos obsidianItem : step.obsidian()) {
         if (!this.worldView.canPlaceOnBedrockObsidian(obsidianItem)) {
            blockPos = obsidianItem;
            break;
         }
      }

      return this.buildCrystalStep(step.base(), blockPos, step.face(), !chasingCounterFlag, chasingCounterFlag);
   }

   private String pickLearnedOption(CrystalPop.Family family, String popText, List<String> list, Map<String, Double> map, boolean popFlag) {
      if (list != null && !list.isEmpty()) {
         CrystalPop crystalPop = this.opponentProfile != null ? this.opponentProfile.getCrystalPop() : this.crystalPop;
         return crystalPop.formatRandomEnum(family, popText, list, map, popFlag && this.opponentProfile != null);
      } else {
         return null;
      }
   }

   public CenterVecData.State getCurrentState() {
      return this.episodeStateMachine.getCurrentPhase();
   }

   private void recordStepSearch(long time, TickStatusData tickStatusData) {
      this.lastStepSearchTick = time;
      this.stepPlanTick = time;
      this.lastSeenBoomTick = this.crystalBoomTick;
      this.lastHelperTick = this.targetTracker.getFullValue();
      this.sampleGround = this.targetTracker.isSampleGround();
      this.lastActionTick = tickStatusData.lastActionTick();
      Vec3d blockingVec = this.getTargetPos();
      this.nearbyEntityCount = blockingVec == null ? -1 : this.worldView.getCrystalsInRadius(blockingVec, 3.0).size();
   }

   private CenterVecData planAnchorBelowDecision(long anchoringBelowValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, TickStatusData tickStatusData) {
      AnchorStandPlanner.Plan vecPosData = this.anchorPlan;
      Vec3d blockingVec = this.getTargetPos();
      if (this.subPhase == ComboPlanner.SubPhase.CRYSTAL_BELOW) {
         Vec3d vec3d = vecPosData != null ? vecPosData.stand() : absorptionPosData.pos();
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.BELOW_LAYER, vec3d, 0.25, false, true, Long.MIN_VALUE);
         CenterVecData.CrystalStep crystalStep = this.currentStep == null ? null : this.buildStepCrystalStep(anchoringBelowValue, this.currentStep, false);
         Vec3d vec3d2 = this.currentStep == null ? blockingVec.add(0.0, 1.2, 0.0) : BlockEntityUtils.getVec3d(this.currentStep.base()).add(0.0, 1.0, 0.0);
         return this.buildDecision(
            CenterVecData.State.ANCHOR_BELOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            crystalStep,
            null,
            null,
            null,
            stand,
            vec3d2,
            Integer.MAX_VALUE,
            "Anchoring",
            this.getProposalArmIfActive()
         );
      } else if (vecPosData == null) {
         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.BELOW_LAYER, absorptionPosData.pos(), 0.25, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.ANCHOR_BELOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            null,
            null,
            null,
            null,
            stand2,
            blockingVec.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Anchoring",
            this.getProposalArmIfActive()
         );
      } else {
         this.subPhase = ComboPlanner.SubPhase.ANCHOR;
         return this.buildAnchorDecision(vecPosData, anchoringBelowValue, selfPopData, vecBlockingData, absorptionPosData, tickStatusData);
      }
   }

   private void expireStalePearlRecords(long aggroEatabValue) {
      Iterator iterator = this.ownPearlsById.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (aggroEatabValue - ((ComboPlanner.OwnPearl)entry.getValue()).tick() > 100L) {
            this.eatabEatbad.recordJmax(((ComboPlanner.OwnPearl)entry.getValue()).aggro(), false);
            iterator.remove();
         }
      }

      if (this.ownPearlThrowTick != Long.MIN_VALUE && aggroEatabValue - this.ownPearlThrowTick > 10L) {
         this.eatabEatbad.recordJmax(this.lastPearlWasAggro, false);
         this.ownPearlThrowTick = Long.MIN_VALUE;
      }
   }

   private Vec3d getSelfPos() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      return player == null ? null : player.getEntityPos();
   }

   private Vec3d getPredictedTargetPos(int livingSubtractBound, Vec3d livingSubtract) {
      if (!this.targetTracker.isSampleGround()) {
         return livingSubtract;
      } else {
         Vec3d entityPos = this.getSelfPos();
         Vec3d vec3d = entityPos == null ? new Vec3d(0.0, 0.0, 0.0) : LivingEntityHelperVisible.getVec3d(entityPos.subtract(livingSubtract));
         PosVecUtils.Path sampleTick = this.targetTracker.getSampleTick(Math.max(1, Math.min(livingSubtractBound, 60)), this.worldView, this.getActiveKnockbackCalibration(), vec3d, null);
         return sampleTick == null ? livingSubtract : getSamplePos(sampleTick.getDataForInt(Math.max(0, Math.min(livingSubtractBound, sampleTick.getValue() - 1))));
      }
   }

   private CenterVecData planPearlFollowDecision(long pearlingChasingValue, AbsorptionPosData absorptionPosData, Vec3d vec, Vec3d vec2, int livingSubtractBound) {
      CenterVecData.PearlShot pearlShot = this.counter != null && this.counter.validUntil() >= pearlingChasingValue ? this.counter : null;
      if (pearlShot == null) {
         Vec3d livingSubtract = this.getPredictedTargetPos(livingSubtractBound, vec);
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, this.getApproachPoint(livingSubtract, vec2), 0.8, true, false, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.PEARL_FOLLOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.PEARL,
            null,
            null,
            null,
            null,
            stand,
            livingSubtract.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Chasing",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.HOLD, absorptionPosData.pos(), 0.1, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.PEARL_FOLLOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.PEARL,
            null,
            null,
            pearlShot,
            null,
            stand2,
            pearlShot.landing(),
            Integer.MAX_VALUE,
            "Pearling",
            this.getProposalArmIfActive()
         );
      }
   }

   private static boolean isCrystalFailureStatus(String ignoredGoneText) {
      return switch (ignoredGoneText) {
         case "ok", "idle", "pending", "gone", "ignored" -> false;
         default -> true;
      };
   }

   private void clearTickFeedbackFlags() {
      this.strikeLanded = false;
      this.pearlThrown = false;
      this.pearlFailed = false;
      if (this.episodeStateMachine.getCurrentPhase() == CenterVecData.State.ANCHOR_BELOW && this.hasAnchorDetonatedThisEpisode()) {
         this.subPhase = ComboPlanner.SubPhase.CRYSTAL_BELOW;
      }

      this.anchorDetonated = false;
      this.anchorFailed = false;
      this.targetPearlThrown = false;
   }

   private void endEpisodeWithReason(long liftPearlValue, String liftPearlText, boolean liftPearlFlag, boolean won, boolean lost) {
      if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
         this.episodeStateMachine.endEpisode(liftPearlText);
      }

      this.recordEpisodeOutcome(liftPearlValue, liftPearlText, liftPearlFlag, won, lost, CenterVecData.Abort.NONE);
   }

   private boolean hasAnchorDetonatedThisEpisode() {
      return this.anchorDetonationTick != Long.MIN_VALUE && this.episodeStartTick != Long.MIN_VALUE && this.anchorDetonationTick >= this.episodeStartTick;
   }

   private static boolean isProfileForTarget(MinVec minVec, VecBlockingData vecBlockingData) {
      if (minVec != null && vecBlockingData != null && vecBlockingData.name() != null && minVec.getUidText() != null) {
         return !minVec.getUidText().equalsIgnoreCase(vecBlockingData.name()) ? false : vecBlockingData.entity() == null || minVec.getUUID() == null || minVec.getUUID().equals(vecBlockingData.entity().getUuid());
      } else {
         return false;
      }
   }

   private CenterVecData planRecomboDecision(long chasingCounterValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, Vec3d vec, Vec3d vec2) {
      VecMax.Forecast forecast = this.targetPearl == null
         ? this.pearlTracker.getVecMaxForInt(this.targetEntityId)
         : this.pearlTracker.getVecMaxVecEntryMap(this.targetPearl.id());
      Vec3d vec3d = forecast != null ? forecast.landing() : vec;
      if (this.subPhase == ComboPlanner.SubPhase.PRESTAGE && this.prestagePlan != null) {
         ComboPlanner.Prestage prestage = this.prestagePlan;
         boolean chasingCounterFlag = this.hasTargetLandedSince(this.recomboStartTick) && this.worldView.findCrystalAt(prestage.base()) != null;
         CenterVecData.CrystalStep livingEntity = this.buildCrystalStep(prestage.base(), prestage.build() ? prestage.base() : null, prestage.face(), !chasingCounterFlag, chasingCounterFlag);
         CenterVecData.Stand stand = new CenterVecData.Stand(
            CenterVecData.StandKind.FOLLOW, this.getLandingApproachPoint(prestage, vec2), 0.8, true, false, Long.MIN_VALUE
         );
         return this.buildDecision(
            CenterVecData.State.RECOMBO,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            livingEntity,
            null,
            null,
            null,
            stand,
            BlockEntityUtils.getVec3d(prestage.base()).add(0.0, 1.0, 0.0),
            Integer.MAX_VALUE,
            "Chasing",
            this.getProposalArmIfActive()
         );
      } else if (this.subPhase == ComboPlanner.SubPhase.COUNTER && this.counter != null && this.counter.validUntil() >= chasingCounterValue) {
         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.HOLD, absorptionPosData.pos(), 0.1, false, true, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.RECOMBO,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.PEARL,
            null,
            null,
            this.counter,
            null,
            stand2,
            this.counter.landing(),
            Integer.MAX_VALUE,
            "Pearling",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.Stand stand3 = new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, this.getApproachPoint(vec3d, vec2), 0.8, true, false, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.RECOMBO,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            null,
            null,
            null,
            null,
            stand3,
            vec3d.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Chasing",
            this.getProposalArmIfActive()
         );
      }
   }

   private void onStrikeLanded(ComboPlanner.Feedback feedback, long time) {
      this.strikeLanded = true;
      this.lastStrikeTick = time;
      Vec3d vec3d = this.getSelfPos();
      if (vec3d != null) {
         this.selfPosAtStrike = vec3d;
      }

      this.markEatabIfLaunched(time);
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (feedback.strikeWasSprint()) {
         long entityLivingLimit = this.sprintTimer.getSprintStartTime();
         boolean entityLivingFlag = entityLivingLimit == Long.MIN_VALUE || entityLivingLimit > time || this.lastSprintStrikeTick != Long.MIN_VALUE && entityLivingLimit <= this.lastSprintStrikeTick;
         this.eatabEatbad.recordShnsAttempt(entityLivingFlag);
         this.lastSprintStrikeTick = time;
      }

      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      if (player != null && seed != null) {
         PosVecUtils.Sample sample = seed.sample();
         int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(player.getMainHandStack(), Enchantments.KNOCKBACK);
         double entityLivingValue = VecPosUtilsAmount.getAmount(0.0, enchantmentsDefault, feedback.strikeWasSprint());
         Vec3d entityPos = player.getEntityPos();
         double[] freshBlocked = VecPosUtilsAmount.getFreshBlocked(
            new double[]{sample.vx(), sample.onGround() ? -0.0784 : sample.vy(), sample.vz()},
            sample.onGround(),
            seed.rMelee(),
            entityPos.x - sample.x(),
            entityPos.z - sample.z(),
            player.getYaw(),
            entityLivingValue,
            VecPosUtilsAmount.HitKind.FRESH
         );
         double entityLivingFactor = this.getActiveKnockbackCalibration().getExplosionScale();
         this.targetTracker.handleTime(time, freshBlocked[0] * entityLivingFactor, freshBlocked[1], freshBlocked[2] * entityLivingFactor, true);
         if (sample.onGround() && seed.grounded()) {
            double entityLivingFactor2 = 1.0 + 0.546 * (1.0 - Math.pow(0.91, 3.0)) / 0.08999999999999997;
            double entityLivingValue2 = Math.hypot(freshBlocked[0], freshBlocked[2]) * entityLivingFactor2;
            double amountDouble = PosVecUtils.getAmountDouble(freshBlocked[1], 4);
            long entityLivingValue4 = time + this.targetTracker.getInterpolatorPingTargetData().getMax() + (long)Math.ceil(this.targetTracker.getInterpolatorPingTargetData().getPingMs());
            this.pendingMeleeKnockbackCheck = new ComboPlanner.MeleeCheck(entityLivingValue4 + 4L, entityLivingValue4, entityLivingValue2, amountDouble);
         }
      }
   }

   private ComboPlanner.Finish findBestFinish(long entityCalibrationValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, ClientPlayerEntity clientPlayerEntity, TickStatusData tickStatusData, int counterVecLimit) {
      ArrayList arrayList = new ArrayList(5);
      float healthTrusted = vecBlockingData.getHealthTrusted();
      int maxTargetReadyIn = Math.max(0, selfPopData.targetReadyIn());
      PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(maxTargetReadyIn, vecBlockingData.lastScaledTaken());
      PosVecUtils.Path sampleTick = this.targetTracker
         .getSampleTick(
            12, this.worldView, this.getActiveKnockbackCalibration(), LivingEntityHelperVisible.getVec3d(selfPopData.self().pos().subtract(this.getTargetPos())), null
         );
      if (sampleTick != null && vecBlockingData.entity() != null) {
         boolean entityCalibrationFlag = vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand();
         if (tickStatusData.holding() && tickStatusData.base() != null && this.worldView.findCrystalAt(tickStatusData.base()) != null) {
            BlockPos base = tickStatusData.base();
            Vec3d vec3d = BlockEntityUtils.getVec3d(base);
            PosVecUtils.Sample sample = sampleTick.getDataForInt(maxTargetReadyIn);
            float readyValue = BlockEntityUtils.getAmount(vecBlockingData.entity(), getSamplePos(sample).distanceTo(vec3d), 6.0F)
               * (float)((double)EntityHeightSamples.countSamplesAbove(sample.y(), base.getY() + 1.0) / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length);
            if (PosBlockUtilsBonus.getReadyInstance(readyValue, vecBlockingData.armor(), window, maxTargetReadyIn) * vecBlockingData.calibration() >= healthTrusted
               && this.calibrationBudget != null
               && this.calibrationBudget.tryReserveRays(45, 2)) {
               float creativeSpectator = BlockEntityUtils.getCreativeSpectator(
                  vecBlockingData.entity(), vec3d, getSamplePos(sample), WorldCollisionHelper.getBoxFromSample(sample, 0.0), null, null, 6.0F
               );
               float entityCalibrationLimit = PosBlockUtilsBonus.getReadyInstance(creativeSpectator, vecBlockingData.armor(), window, maxTargetReadyIn) * vecBlockingData.calibration();
               if (entityCalibrationLimit >= healthTrusted) {
                  arrayList.add(new ComboPlanner.Finish(ComboPlanner.FinishKind.HELD, maxTargetReadyIn, entityCalibrationLimit, entityCalibrationFlag, 0.0F, base, !sample.onGround()));
               }
            }
         }

         AnchorStandPlanner.Plan vecPosData = this.anchorPlan;
         if (vecPosData != null && BlockPosUtilsRespawn.isRegistryWorld() && BlockPosUtilsRespawn.getChargesRespawn(vecPosData.anchor()) > 0) {
            PosVecUtils.Sample dataForInt = sampleTick.getDataForInt(maxTargetReadyIn);
            if (this.calibrationBudget != null && this.calibrationBudget.tryReserveRays(45, 2)) {
               float boxBounding = EntityCenterUtils.getBoxBounding(
                  vecBlockingData.entity(), new BlockEntityUtils.Predicted(getSamplePos(dataForInt), WorldCollisionHelper.getBoxFromSample(dataForInt, 0.0)), vecPosData.anchor(), null
               );
               float entityCalibrationLimit2 = PosBlockUtilsBonus.getReadyInstance(boxBounding, vecBlockingData.armor(), window, maxTargetReadyIn) * vecBlockingData.calibration();
               if (entityCalibrationLimit2 >= healthTrusted) {
                  arrayList.add(new ComboPlanner.Finish(ComboPlanner.FinishKind.ANCHOR, maxTargetReadyIn, entityCalibrationLimit2, entityCalibrationFlag, vecPosData.selfBound(), vecPosData.anchor(), !dataForInt.onGround()));
               }
            }
         }

         CrystalStepPlanner.Step directionVecData = this.currentStep;
         if (directionVecData != null && directionVecData.pops() && isStepPlanCurrent(this.stepPlanTick, directionVecData.breakTick(), entityCalibrationValue)) {
            int max4 = (int)Math.max(0L, directionVecData.breakTick() - entityCalibrationValue);
            arrayList.add(
               new ComboPlanner.Finish(
                  directionVecData.obsidian().isEmpty() ? ComboPlanner.FinishKind.BASE : ComboPlanner.FinishKind.BUILD,
                  max4,
                  healthTrusted,
                  directionVecData.kills(),
                  directionVecData.selfDamage(),
                  directionVecData.base(),
                  this.targetTracker.isSampleGround()
               )
            );
         }

         double minMax = BlockEntityUtils.getMinMax(clientPlayerEntity.getEyePos(), vecBlockingData.entity().getBoundingBox());
         if (minMax < 3.0 && maxTargetReadyIn <= 0) {
            VecPos.Loadout stackKnockback = VecPos.getStackKnockback(clientPlayerEntity, false);
            float toughnessAmpValue = stackKnockback.swordSlot() >= 0 ? VecPos.getAxeSword(stackKnockback.sword()) : VecPos.getAxeSword(clientPlayerEntity.getMainHandStack());
            float entityCalibrationLimit3 = VecShieldUtilsToughness.getToughnessAmp(toughnessAmpValue, 0.0F, 1.0F, false, vecBlockingData.armor(), vecBlockingData.meleeEpf()) * vecBlockingData.calibration();
            if (entityCalibrationLimit3 >= healthTrusted) {
               int attackProgressLimit = stackKnockback.swordSlot() >= 0 ? stackKnockback.swordSlot() : clientPlayerEntity.getInventory().getSelectedSlot();
               int attackProgress = this.getTicksUntilAttackReady(clientPlayerEntity, attackProgressLimit, stackKnockback.swordSlot() >= 0 ? 1.6 : 4.0);
               arrayList.add(new ComboPlanner.Finish(ComboPlanner.FinishKind.MELEE, attackProgress, entityCalibrationLimit3, entityCalibrationFlag, 0.0F, null, !vecBlockingData.onGround()));
            }
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            arrayList.sort((obj, obj2) -> ((com.corz.client.utils.ComboPlanner.Finish) obj).fire() != ((com.corz.client.utils.ComboPlanner.Finish) obj2).fire() ? Integer.compare(((com.corz.client.utils.ComboPlanner.Finish) obj).fire(), ((com.corz.client.utils.ComboPlanner.Finish) obj2).fire()) : Float.compare(((com.corz.client.utils.ComboPlanner.Finish) obj).selfBound(), ((com.corz.client.utils.ComboPlanner.Finish) obj2).selfBound()));
            ComboPlanner.Finish entityCalibration = (ComboPlanner.Finish)arrayList.get(0);
            ArrayList arrayList2 = new ArrayList();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();

            for (ComboPlanner.Finish finish : (Iterable<com.corz.client.utils.ComboPlanner.Finish>) (arrayList)) {
               if (finish.fire() <= entityCalibration.fire() + 1) {
                  String formatGroundAir = this.getFinishSurfaceLabel(finish);
                  if (!hashMap2.containsKey(formatGroundAir)) {
                     hashMap2.put(formatGroundAir, finish);
                     arrayList2.add(formatGroundAir);
                     hashMap.put(formatGroundAir, (double)finish.damage() - finish.selfBound());
                  }
               }
            }

            if (arrayList2.size() <= 1) {
               return entityCalibration;
            } else {
               LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
               String formatObsHole = this.buildTargetContextKey(vecBlockingData);
               String formatMinPop = this.pickLearnedOption(CrystalPop.Family.FINISHER, formatObsHole, arrayList2, hashMap, false);
               ComboPlanner.Finish finish2 = formatMinPop == null ? null : (ComboPlanner.Finish)hashMap2.get(formatMinPop);
               return finish2 != null && seed != null ? finish2 : entityCalibration;
            }
         }
      } else {
         return null;
      }
   }

   public VecMax getPearlTracker() {
      return this.pearlTracker;
   }

   private static Vec3d getSamplePos(PosVecUtils.Sample sample) {
      return new Vec3d(sample.x(), sample.y(), sample.z());
   }

   private boolean isFriendlyPlayerNearTarget(VecBlockingData vecBlockingData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.world != null && vecBlockingData != null && vecBlockingData.entity() != null) {
         Vec3d entityPos = vecBlockingData.entity().getEntityPos();

         for (PlayerEntity playerEntity : instance.world.getPlayers()) {
            if (playerEntity != instance.player
               && playerEntity != vecBlockingData.entity()
               && !(playerEntity.getEntityPos().distanceTo(entityPos) > 7.0)
               && (FriendNameStore.canFlag(playerEntity) || EntityPlayerUtilsLiving.canScoreboardTeam(playerEntity))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public String getHitLoopText() {
      return this.aggro.state() == CenterVecData.State.HIT_LOOP ? this.hitLoopCombo.getLoopText() : "-";
   }

   private Vec3d getOfferDirection(Vec3d vec, VecPos.Offer offer) {
      Vec3d vec3d = LivingEntityHelperVisible.getVec3d(new Vec3d(offer.base().getX() + 0.5 - vec.x, 0.0, offer.base().getZ() + 0.5 - vec.z));
      if (vec3d.lengthSquared() < 0.5) {
         float degrees = offer.launchYaw();
         double angle = Math.toRadians(degrees);
         vec3d = new Vec3d(-Math.sin(angle), 0.0, Math.cos(angle));
      }

      return vec3d;
   }

   private void clearStepPlan() {
      this.currentStep = null;
      this.lastStepSearchTick = Long.MIN_VALUE;
      this.stepPlanTick = Long.MIN_VALUE;
   }

   private void expireBlockedPositions(long finishingVecLimit) {
      this.blockedPosExpiryTicks.entrySet().removeIf(entry -> finishingVecLimit >= entry.getValue());
   }

   private AmountAmounts getActiveKnockbackCalibration() {
      return this.opponentProfile != null ? this.opponentProfile.getArmsAmountAmounts() : this.defaultKnockbackCalibration;
   }

   private Vec3d getApproachPoint(Vec3d vec, Vec3d vec2) {
      Vec3d vec3d = LivingEntityHelperVisible.getVec3d(vec2.subtract(vec));
      return vec3d.lengthSquared() < 0.5 ? vec2 : new Vec3d(vec.x + vec3d.x * 2.6, vec.y, vec.z + vec3d.z * 2.6);
   }

   private ComboPlanner.SubPhase getOfferEntrySubPhase(VecPos.Offer offer) {
      if (offer.arm() == VecPos.Arm.CRYSTAL_LAUNCH) {
         return ComboPlanner.SubPhase.LAUNCH_NOW;
      } else {
         return offer.arm() == VecPos.Arm.KB_HIT_CRYSTAL && this.worldView.findCrystalAt(offer.base()) == null ? ComboPlanner.SubPhase.STAGE : ComboPlanner.SubPhase.LINE_UP;
      }
   }

   public void updateTarget(VecBlockingData vecBlockingData, MinVec min) {
      if (vecBlockingData == null) {
         if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
            this.endEpisodeWithReason(this.currentTick, "retarget", false, false, false);
         }

         this.currentTarget = null;
         this.targetEntityId = -1;
         this.opponentProfile = min;
         this.targetTracker.updateTimeFull();
         this.targetTracker.handleHandler(this.worldView);
         this.velocityCpacket.setValue(Integer.MIN_VALUE);
         this.hitLoopCombo.updateTimeIframe();
         this.clearPlanningState();
         this.clearFeedbackFlags();
      } else if (vecBlockingData.id() == this.targetEntityId) {
         this.currentTarget = vecBlockingData;
         if (min != null) {
            this.opponentProfile = min;
         }
      } else {
         if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
            this.endEpisodeWithReason(this.currentTick, "retarget", false, false, false);
         }

         this.currentTarget = vecBlockingData;
         this.targetEntityId = vecBlockingData.id();
         this.opponentProfile = min;
         this.targetTracker.handleHandler(this.targetEntityId);
         this.targetTracker.handleHandler(this.worldView);
         this.velocityCpacket.updateAtomicConcurrent();
         this.velocityCpacket.setValue(this.targetEntityId);
         this.lastLatencyCheckTick = Long.MIN_VALUE;
         this.targetFarTicks = 0;
         this.hitLoopCombo.updateTimeIframe();
         this.clearPlanningState();
         this.clearFeedbackFlags();
      }
   }

   private Vec3d getLandingApproachPoint(ComboPlanner.Prestage prestage, Vec3d vec) {
      Vec3d vec3d = LivingEntityHelperVisible.getVec3d(prestage.landing().subtract(vec));
      return new Vec3d(prestage.landing().x - vec3d.x * 2.6, prestage.landing().y, prestage.landing().z - vec3d.z * 2.6);
   }

   private ComboPlanner.Proposal chooseProposal(
      long time,
      SelfPopData selfPopData,
      VecBlockingData vecBlockingData,
      AbsorptionPosData absorptionPosData,
      PearlsPlaystyleData pearlsPlaystyleData,
      CarefulBalancedType carefulBalancedType,
      boolean belowAnchorFlag,
      boolean belowAnchorFlag2,
      WeakFullUtils.Lift lift,
      Predicate<Vec3d> predicate,
      SelfHazData selfHazData
   ) {
      boolean spacingPopFlag = pearlsPlaystyleData.learnOpponents() && carefulBalancedType != CarefulBalancedType.CAREFUL && absorptionPosData.getAbsorptionHealth() >= 16.0;
      String formatObsHole = this.buildTargetContextKey(vecBlockingData);
      if (this.cachedSpacingChoice == null) {
         this.cachedSpacingChoice = this.chooseSpacingOption(formatObsHole, spacingPopFlag);
      }

      String popText = this.cachedSpacingChoice;
      double belowAmount = getSpacingForChoice(popText);
      if (Math.abs(belowAmount - this.currentSpacing) > 1.0E-6) {
         this.currentSpacing = belowAmount;
         if (belowAnchorFlag) {
            this.offers = this.offerFinder
               .getOffer(
                  time,
                  this.worldView,
                  this.targetTracker,
                  absorptionPosData,
                  vecBlockingData,
                  this.opponentProfile,
                  pearlsPlaystyleData,
                  selfHazData,
                  this.currentSpacing,
                  selfPopData.targetReadyIn(),
                  this.selfDamageLimit,
                  predicate
               );
            this.lastOfferSearchTick = time;
         }
      }

      HashMap hashMap = new HashMap();
      HashMap hashMap2 = new HashMap();
      ArrayList arrayList = new ArrayList();
      if (belowAnchorFlag && lift != WeakFullUtils.Lift.NONE) {
         for (VecPos.Offer offer : this.offers) {
            String name = offer.arm().name();
            VecPos.Offer vecPosOffer = (VecPos.Offer)hashMap2.get(name);
            if (vecPosOffer == null || offer.value() > vecPosOffer.value()) {
               hashMap2.put(name, offer);
            }
         }

         for (Entry entry : (Iterable<java.util.Map.Entry>) (hashMap2.entrySet())) {
            if (!(((VecPos.Offer)entry.getValue()).value() < 4.0)) {
               hashMap.put((String)entry.getKey(), ((VecPos.Offer)entry.getValue()).value());
               arrayList.add((String)entry.getKey());
            }
         }
      }

      AnchorStandPlanner.Plan plan = belowAnchorFlag2 && !selfPopData.nether() ? this.anchorPlan : null;
      if (plan != null) {
         double belowAnchorLimit = plan.value() + (this.getActiveMaxMin().getPearlAmount() > 0.4 ? 1.0 : 0.0);
         if (belowAnchorLimit >= 4.0) {
            hashMap.put("ANCHOR_BELOW", belowAnchorLimit);
            arrayList.add("ANCHOR_BELOW");
         }
      }

      if (arrayList.isEmpty()) {
         return null;
      } else {
         arrayList.sort((obj, obj2) -> Double.compare((Double)hashMap.get(obj2), (Double)hashMap.get(obj)));
         String formatMinPop = this.pickLearnedOption(CrystalPop.Family.OPENER, formatObsHole, arrayList, hashMap, spacingPopFlag);
         if (formatMinPop == null) {
            return null;
         } else if ("ANCHOR_BELOW".equals(formatMinPop)) {
            return new ComboPlanner.Proposal(
               CenterVecData.Proposal.ANCHOR, CenterVecData.State.ANCHOR_BELOW, (Double)hashMap.get(formatMinPop), null, plan, formatMinPop, popText, this.currentSpacing
            );
         } else {
            VecPos.Offer offer3 = (VecPos.Offer)hashMap2.get(formatMinPop);
            return offer3 == null
               ? null
               : new ComboPlanner.Proposal(CenterVecData.Proposal.OPEN, CenterVecData.State.OPENER, offer3.value(), offer3, null, formatMinPop, popText, this.currentSpacing);
         }
      }
   }

   static boolean areCellsAdjacent(BlockPos pos, BlockPos pos2) {
      if (pos != null && pos2 != null) {
         int finishingVecBound = Math.abs(pos.getX() - pos2.getX());
         int deltaY = pos.getY() - pos2.getY();
         int rocketsBuyBound = Math.abs(pos.getZ() - pos2.getZ());
         if (deltaY == 0) {
            return Math.max(finishingVecBound, rocketsBuyBound) == 1;
         } else {
            return deltaY == 1 ? finishingVecBound + rocketsBuyBound == 1 : false;
         }
      } else {
         return false;
      }
   }

   private ComboPlanner.Prestage findPrestagePlan(
      long intersectsPosValue, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData, CenterVecData.State followJuggleType, int livingSubtractBound, Predicate<Vec3d> predicate
   ) {
      List list = null;
      Vec3d vec3d;
      int intersectsPosBound;
      if (followJuggleType == CenterVecData.State.RECOMBO) {
         VecMax.Forecast forecast = this.targetPearl == null
            ? this.pearlTracker.getVecMaxForInt(this.targetEntityId)
            : this.pearlTracker.getVecMaxVecEntryMap(this.targetPearl.id());
         if (forecast == null) {
            return null;
         }

         vec3d = forecast.landing();
         intersectsPosBound = forecast.eta();
         list = forecast.tail();
      } else {
         vec3d = this.getPredictedTargetPos(livingSubtractBound, this.getTargetPos());
         intersectsPosBound = this.targetTracker.isSampleGround() ? livingSubtractBound : 0;
      }

      Vec3d pos = absorptionPosData.pos();
      Vec3d vec3d2 = LivingEntityHelperVisible.getVec3d(vec3d.subtract(pos));
      int floor3 = (int)Math.floor(vec3d.y + 1.0E-6);
      Box box = WorldCollisionHelper.getPlayerBoxAt(vec3d.x, vec3d.y, vec3d.z, 0.2);
      Box box2 = absorptionPosData.box();
      boolean intersectsPosFlag = false;
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         intersectsPosFlag = VecPos.getStackKnockback(player, true).obsidianSlot() >= 0;
      }

      ComboPlanner.Prestage intersectsPos = null;
      double negativeInfinity = Double.NEGATIVE_INFINITY;

      for (double intersectsPosFactor : PRESTAGE_RING_RADII) {
         for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2) * i / 8.0;
            double hashRegistryValue = vec3d.x + Math.cos(angle) * intersectsPosFactor;
            double intersectsPosValue6 = vec3d.z + Math.sin(angle) * intersectsPosFactor;
            double intersectsPosValue7 = Math.cos(angle) * vec3d2.x + Math.sin(angle) * vec3d2.z;

            for (int j = 0; j < 2; j++) {
               boolean intersectsPosFlag2 = j == 1;
               BlockPos blockPos = new BlockPos((int)Math.floor(hashRegistryValue), intersectsPosFlag2 ? floor3 : floor3 - 1, (int)Math.floor(intersectsPosValue6));
               if (!this.isPositionBlocked(blockPos)) {
                  boolean intersectsPosFlag3 = this.worldView.canPlaceOnBedrockObsidian(blockPos) && this.worldView.findCrystalAt(blockPos) != null;
                  if (!intersectsPosFlag2
                     ? this.worldView.canPlaceOnBedrockObsidian(blockPos) && (intersectsPosFlag3 || this.worldView.isAirAt(blockPos.up()))
                     : intersectsPosFlag && this.worldView.isReplaceableWithSupportBelow(blockPos)) {
                     Box box3 = BlockEntityUtils.getBox3(blockPos);
                     if ((intersectsPosFlag3 || !box3.intersects(box) && (box2 == null || !box3.intersects(box2)))
                        && (!intersectsPosFlag2 || !BlockEntityUtils.getBoxForBlockPos(blockPos).intersects(box))) {
                        Vec3d vec3d3 = BlockEntityUtils.getVec3d(blockPos);
                        if (!predicate.test(vec3d3) && !doesPathPassNearCell(blockPos, list) && !this.isOnPearlPath(blockPos)) {
                           double max8 = Math.max(0.0, WorldCollisionHelper.getAmount(pos.x, pos.z, vec3d3.x, vec3d3.z) - 3.5);
                           if (!(max8 / 0.26 > Math.max(intersectsPosBound, 1))) {
                              float toughnessEpfLimit = BlockEntityUtils.getAmount(player, Math.max(3.0, pos.distanceTo(vec3d3) - 0.26 * Math.max(intersectsPosBound, 0)), 6.0F)
                                 * (float)EntityHeightSamples.getReachingFraction(Math.min(pos.y, blockPos.getY() + 1.0), blockPos.getY() + 1.0);
                              if (!(PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, absorptionPosData.armor()) > this.selfDamageLimit)) {
                                 double intersectsPosLimit = 2.0 * intersectsPosValue7 - Math.abs(intersectsPosFactor - 1.1) - (intersectsPosFlag2 ? 0.5 : 0.0) + (intersectsPosFlag3 ? 1.0 : 0.0);
                                 if (intersectsPosLimit > negativeInfinity) {
                                    negativeInfinity = intersectsPosLimit;
                                    Vec3d vec3d4 = new Vec3d(vec3d.x - vec3d2.x * 2.6, vec3d.y, vec3d.z - vec3d2.z * 2.6);
                                    intersectsPos = new ComboPlanner.Prestage(
                                       blockPos,
                                       intersectsPosFlag2,
                                       vec3d,
                                       blockPos.getY() + 1,
                                       this.getPlacementFace(blockPos, vec3d4.add(0.0, 1.62, 0.0)),
                                       intersectsPosValue + Math.max(intersectsPosBound, 1) + 20L
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return intersectsPos;
   }

   private void beginEpisode(long time, ComboPlanner.Proposal proposal) {
      this.episodeActive = true;
      this.episodeStartTick = time;
      this.proposalActive = proposal.kind() == CenterVecData.Proposal.OPEN || proposal.kind() == CenterVecData.Proposal.ANCHOR;
      this.finisherReached = false;
      this.proposalArm = proposal.arm();
      this.spacingChoice = proposal.kind() == CenterVecData.Proposal.OPEN ? proposal.spacingArm() : null;
      this.finisherKind = null;
      this.targetContextKey = this.currentTarget == null ? "" : this.buildTargetContextKey(this.currentTarget);
      this.targetDamageTotal = 0.0;
      this.totalEhpDrop = 0.0;
      this.targetPopCount = 0;
      this.selfDamageEventCount = 0;
      this.juggleCount = 0;
      this.eatabFlagBaseline = this.eatabEatbad.getLongRunTotal();
      this.cachedSpacingChoice = null;
      if (proposal.state() != CenterVecData.State.JUGGLE) {
         this.clearStepPlan();
      }

      this.bestFinish = null;
      this.lastFinishEvalTick = Long.MIN_VALUE;
      this.stateBeforePearlFollow = CenterVecData.State.FOLLOW;
      this.activeOffer = proposal.offer();
      if (this.activeOffer != null) {
         this.currentSpacing = this.activeOffer.spacing();
      }

      this.anchorPlan = proposal.anchor() != null ? proposal.anchor() : this.anchorPlan;
      this.strikeReadyTick = Long.MIN_VALUE;
      this.launchBase = null;
      this.usingLaunchCrystal = false;
      this.launchBreakTick = Long.MIN_VALUE;
      this.offerStillValid = true;
      this.subPhase = proposal.state() == CenterVecData.State.OPENER && this.activeOffer != null
         ? this.getOfferEntrySubPhase(this.activeOffer)
         : (proposal.state() == CenterVecData.State.ANCHOR_BELOW ? ComboPlanner.SubPhase.ANCHOR : ComboPlanner.SubPhase.NONE);
      if (proposal.kind() == CenterVecData.Proposal.OPEN && proposal.offer() != null && proposal.offer().arm() == VecPos.Arm.CRYSTAL_LAUNCH) {
         this.subPhase = ComboPlanner.SubPhase.LAUNCH_NOW;
      }

      this.pendingProposal = null;
   }

   private boolean canRecomputeCounterShot(long counterValue) {
      return this.lastCounterShotTick == Long.MIN_VALUE || counterValue - this.lastCounterShotTick >= 4L || counterValue < this.lastCounterShotTick;
   }

   private CenterVecData planLaunchDecision(long launchingLaunchLimit, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, TickStatusData tickStatusData) {
      BlockPos blockPos = this.launchBase;
      Vec3d blockingVec = this.getTargetPos();
      if (blockPos == null) {
         return this.planJuggleDecision(launchingLaunchLimit, selfPopData, vecBlockingData, absorptionPosData);
      } else {
         Vec3d vec3d = LivingEntityHelperVisible.getVec3d(new Vec3d(blockPos.getX() + 0.5 - blockingVec.x, 0.0, blockPos.getZ() + 0.5 - blockingVec.z));
         double launchingLaunchFactor = this.activeOffer != null ? this.activeOffer.spacing() : this.currentSpacing;
         Vec3d vec3d2 = this.selfPosAtStrike != null && this.lastStrikeTick != Long.MIN_VALUE && this.lastStrikeTick >= this.episodeStartTick - 1L ? this.selfPosAtStrike : null;
         Vec3d vec3d3;
         if (this.usingLaunchCrystal) {
            vec3d3 = absorptionPosData.pos();
         } else if (vec3d2 != null) {
            Vec3d vec3d4 = LivingEntityHelperVisible.getVec3d(new Vec3d(blockPos.getX() + 0.5 - vec3d2.x, 0.0, blockPos.getZ() + 0.5 - vec3d2.z));
            vec3d3 = new Vec3d(vec3d2.x + vec3d4.x * 0.6, vec3d2.y, vec3d2.z + vec3d4.z * 0.6);
         } else {
            vec3d3 = new Vec3d(blockingVec.x - vec3d.x * launchingLaunchFactor, absorptionPosData.pos().y, blockingVec.z - vec3d.z * launchingLaunchFactor);
         }

         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.LINE_UP, vec3d3, 0.35, false, true, Long.MIN_VALUE);
         Vec3d add = BlockEntityUtils.getVec3d(blockPos).add(0.0, 1.0, 0.0);
         if (this.launchBreakTick != Long.MIN_VALUE) {
            if (this.currentStep != null) {
               CenterVecData.CrystalStep noneBreak = this.buildStepCrystalStep(launchingLaunchLimit, this.currentStep, true);
               return this.buildDecision(
                  CenterVecData.State.LAUNCH,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.CRYSTAL,
                  noneBreak,
                  null,
                  null,
                  null,
                  stand,
                  add,
                  Integer.MAX_VALUE,
                  "Launching",
                  this.getProposalArmIfActive()
               );
            } else {
               CenterVecData.CrystalStep livingEntity = this.buildCrystalStep(blockPos, null, this.getPlacementFace(blockPos, absorptionPosData.eye()), true, false);
               return this.buildDecision(
                  CenterVecData.State.LAUNCH,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.CRYSTAL,
                  livingEntity,
                  null,
                  null,
                  null,
                  stand,
                  add,
                  Integer.MAX_VALUE,
                  "Launching",
                  this.getProposalArmIfActive()
               );
            }
         } else {
            BlockPos pos2 = null;
            boolean launchingLaunchFlag;
            boolean livingEntityFlag;
            if (!this.usingLaunchCrystal && this.activeOffer != null && this.activeOffer.arm() == VecPos.Arm.KB_HIT_CRYSTAL) {
               long launchingLaunchValue = this.lastStrikeTick == Long.MIN_VALUE ? launchingLaunchLimit : this.lastStrikeTick + this.activeOffer.breakDelta();
               launchingLaunchFlag = launchingLaunchLimit >= launchingLaunchValue;
               livingEntityFlag = !launchingLaunchFlag;
            } else {
               launchingLaunchFlag = true;
               livingEntityFlag = false;
               if (this.activeOffer != null && this.activeOffer.arm() == VecPos.Arm.KB_POP_BUILD && !this.usingLaunchCrystal) {
                  pos2 = this.activeOffer.obsidianCell();
               }
            }

            CenterVecData.CrystalStep crystalStep = this.buildCrystalStep(blockPos, pos2, this.getPlacementFace(blockPos, absorptionPosData.eye()), livingEntityFlag, launchingLaunchFlag);
            return this.buildDecision(
               CenterVecData.State.LAUNCH,
               CenterVecData.Proposal.NONE,
               0.0,
               SurroundKitType.CRYSTAL,
               crystalStep,
               null,
               null,
               null,
               stand,
               add,
               Integer.MAX_VALUE,
               "Launching",
               this.getProposalArmIfActive()
            );
         }
      }
   }

   static boolean isRetreatOrDefendMode(DefendEngageType defendEngageType, SurroundKitType surroundKitType) {
      return defendEngageType != DefendEngageType.ESCAPE && defendEngageType != DefendEngageType.RECOVER && defendEngageType != DefendEngageType.HEAL
         ? defendEngageType == DefendEngageType.DEFEND && surroundKitType != SurroundKitType.GUARD
         : true;
   }

   private boolean wasTargetLaunchedByBoom(CenterVecData.State followJuggleType, long livingLimit, int livingValue) {
      long ceil2 = (long)Math.ceil(this.targetTracker.getInterpolatorPingTargetData().getPingMs() / 2.0);
      if (followJuggleType == CenterVecData.State.LAUNCH) {
         if (this.crystalBoomTick != Long.MIN_VALUE && this.crystalBoomTick >= this.episodeStartTick) {
            if (this.targetTracker.canTick(this.crystalBoomTick - 1L, 0.35)) {
               return true;
            } else {
               long cooldownPlayerBound = this.crystalBoomTick;
               if (this.lastStrikeTick != Long.MIN_VALUE) {
                  cooldownPlayerBound = Math.max(cooldownPlayerBound, this.lastStrikeTick + livingValue + 6L + ceil2);
               }

               return livingLimit > cooldownPlayerBound && this.targetTracker.getTick(cooldownPlayerBound) >= 0.3;
            }
         } else {
            return false;
         }
      } else if (followJuggleType == CenterVecData.State.ANCHOR_BELOW) {
         return this.anchorDetonationTick == Long.MIN_VALUE
            ? false
            : this.targetTracker.canTick(this.anchorDetonationTick - 1L, 0.35) || this.targetTracker.getTick(this.anchorDetonationTick) >= 0.3;
      } else {
         return false;
      }
   }

   private String buildTargetContextKey(VecBlockingData vecBlockingData) {
      LivingEntityHelperVisible.Seed seed = this.targetTracker.getDataGroundMax();
      String obsHoleText = seed == null ? WeakFullUtils.Lift.FULL.name() : seed.lift().name();
      String obsHoleText2 = vecBlockingData != null && vecBlockingData.isHoleSides() ? "hole" : "open";
      String obsHoleText3 = "OTHER";
      World world = this.worldView.getWorld();
      Vec3d blockingVec = this.getTargetPos();
      if (world != null && blockingVec != null) {
         BlockPos floored = BlockPos.ofFloored(blockingVec.x, blockingVec.y - 0.5, blockingVec.z);
         if (world.getBlockState(floored).isOf(Blocks.OBSIDIAN) || world.getBlockState(floored).isOf(Blocks.BEDROCK)) {
            obsHoleText3 = "OBS";
         }
      }

      return obsHoleText + "|" + obsHoleText2 + "|" + obsHoleText3;
   }

   private CenterVecData planFollowDecision(
      long followingFollowValue,
      SelfPopData selfPopData,
      VecBlockingData vecBlockingData,
      AbsorptionPosData absorptionPosData,
      ClientPlayerEntity clientPlayerEntity,
      PearlsPlaystyleData pearlsPlaystyleData,
      TickStatusData tickStatusData,
      int followingFollowLimit,
      Vec3d vec,
      Vec3d vec2
   ) {
      Vec3d livingSubtract = this.getPredictedTargetPos(followingFollowLimit, vec);
      Vec3d subtractLength = this.getApproachPoint(livingSubtract, vec2);
      VecPos.Loadout stackKnockback = VecPos.getStackKnockback(clientPlayerEntity, pearlsPlaystyleData.buildObsidian());
      boolean sampleGround = this.targetTracker.isSampleGround();
      if (pearlsPlaystyleData.combos() && sampleGround && followingFollowLimit >= 12 && this.currentStep == null && stackKnockback.swordSlot() >= 0) {
         Vec3d vec3d = LivingEntityHelperVisible.getVec3d(livingSubtract.subtract(subtractLength));
         float followingFollowValue2 = VecShieldUtilsAmounts.getAmount(vec3d.x, vec3d.z);
         float min3 = (float)VecShieldUtilsAmounts.getMin(2.6);
         CenterVecData.Strike strike = new CenterVecData.Strike(stackKnockback.swordSlot(), true, followingFollowValue2, min3, true);
         long pressLeadTime = FinishingPressUtils.getPressLeadTime(followingFollowValue + followingFollowLimit + 1L);
         CenterVecData.Stand stand = new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, subtractLength, 0.8, true, false, pressLeadTime);
         return this.buildDecision(
            CenterVecData.State.FOLLOW,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.MELEE,
            null,
            strike,
            null,
            null,
            stand,
            livingSubtract.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Following",
            this.getProposalArmIfActive()
         );
      } else {
         CenterVecData.CrystalStep crystalStep = null;
         SurroundKitType surroundKitType = SurroundKitType.CRYSTAL;
         ComboPlanner.Prestage prestage = this.prestagePlan;
         if (prestage != null) {
            boolean followingFollowFlag = this.isTargetAbovePrestage(prestage, vec) && this.worldView.findCrystalAt(prestage.base()) != null;
            crystalStep = this.buildCrystalStep(prestage.base(), prestage.build() ? prestage.base() : null, prestage.face(), !followingFollowFlag, followingFollowFlag);
            subtractLength = this.getLandingApproachPoint(prestage, vec2);
         } else if (!sampleGround && WorldCollisionHelper.horizontalDistance(vec2, vec) <= 4.5) {
            surroundKitType = this.pickBestKit(selfPopData);
         }

         CenterVecData.Stand stand2 = new CenterVecData.Stand(CenterVecData.StandKind.FOLLOW, subtractLength, 0.8, true, false, Long.MIN_VALUE);
         return this.buildDecision(
            CenterVecData.State.FOLLOW,
            CenterVecData.Proposal.NONE,
            0.0,
            surroundKitType,
            crystalStep,
            null,
            null,
            null,
            stand2,
            livingSubtract.add(0.0, 1.2, 0.0),
            Integer.MAX_VALUE,
            "Following",
            this.getProposalArmIfActive()
         );
      }
   }

   private void trackSelectedSlot(ClientPlayerEntity clientPlayerEntity, long time) {
      int selectedSlot = clientPlayerEntity.getInventory().getSelectedSlot();
      if (selectedSlot != this.selectedSlot) {
         this.selectedSlot = selectedSlot;
         this.slotChangeTick = time;
      }
   }

   private Direction getPlacementFace(BlockPos pos, Vec3d vec) {
      if (pos == null) {
         return Direction.UP;
      } else if (vec != null && !(vec.y > pos.getY() + 1.05)) {
         double southNorthLimit = vec.x - (pos.getX() + 0.5);
         double spotValue = vec.z - (pos.getZ() + 0.5);
         if (Math.abs(southNorthLimit) >= Math.abs(spotValue)) {
            return southNorthLimit >= 0.0 ? Direction.EAST : Direction.WEST;
         } else {
            return spotValue >= 0.0 ? Direction.SOUTH : Direction.NORTH;
         }
      } else {
         return Direction.UP;
      }
   }

   private void markEatabIfLaunched(long eatabValue) {
      if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE && !this.eatabActive) {
         long eatabLimit = Math.max(this.targetTracker.getVisibleValue(), this.targetPearlLandedTick);
         if (this.recomboStartTick != Long.MIN_VALUE && eatabLimit != Long.MIN_VALUE && eatabLimit >= this.recomboStartTick && eatabValue - eatabLimit <= 60L) {
            this.eatabActive = true;
            this.eatabEatbad.incrementRccCount();
         }
      }
   }

   static boolean isComboPhaseState(CenterVecData.State state) {
      return state == CenterVecData.State.OPENER
         || state == CenterVecData.State.LAUNCH
         || state == CenterVecData.State.JUGGLE
         || state == CenterVecData.State.ANCHOR_BELOW
         || state == CenterVecData.State.FINISHER;
   }

   public CenterVecData computeDecision(SelfPopData selfPopData, List<VecBlockingData> list, VecBlock vecBlock, TickStatusData tickStatusData, long fightRetargetTime, Predicate<Vec3d> predicate) {
      this.currentTick = fightRetargetTime;
      this.pendingAbort = CenterVecData.Abort.NONE;
      if (selfPopData != null && selfPopData.cfg() != null && selfPopData.self() != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null) {
            this.selfPlayerId = player.getId();
            PearlsPlaystyleData cfg = selfPopData.cfg();
            AbsorptionPosData self = selfPopData.self();
            this.loopTicks = Math.max(1.0, self.loopMs() / 50.0);
            this.trackSelectedSlot(player, fightRetargetTime);
            TickStatusData tickStatusData2 = tickStatusData == null ? TickStatusData.INSTANCE : tickStatusData;
            VecBlockingData vecBlockingData = selfPopData.target();
            this.worldView.updateCacheForWorld(fightRetargetTime, instance.world);
            this.expireBlockedPositions(fightRetargetTime);
            this.pearlTracker.handleVecMax(fightRetargetTime, instance.world);
            this.expireStalePearlRecords(fightRetargetTime);
            if (vecBlockingData == null) {
               if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
                  this.endEpisodeWithReason(fightRetargetTime, "no target", false, false, false);
               }

               this.eatabEatbad.updateRunState(false);
               this.pendingProposal = null;
               return this.finalizeDecision(CenterVecData.INSTANCE);
            } else {
               if (vecBlockingData.id() != this.targetEntityId) {
                  this.updateTarget(vecBlockingData, this.opponentProfile != null && isProfileForTarget(this.opponentProfile, vecBlockingData) ? this.opponentProfile : null);
               }

               this.currentTarget = vecBlockingData;
               this.refreshTargetLatency(instance, vecBlockingData, fightRetargetTime);
               AmountAmounts minMax = this.getActiveKnockbackCalibration();
               this.targetTracker.handleInterpolatorTime(fightRetargetTime, vecBlockingData.entity(), this.targetLatencyMs, self.pingMs(), minMax);
               this.eatabEatbad.updateRunState(this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE && this.targetTracker.isSampleGround());
               this.resolvePendingKnockbackCheck(fightRetargetTime);
               if (!cfg.combos() && !cfg.aggroPearls()) {
                  if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
                     this.endEpisodeWithReason(fightRetargetTime, "off", false, false, false);
                  }

                  this.episodeStateMachine.reset();
                  this.clearFeedbackFlags();
                  this.pendingProposal = null;
                  return this.finalizeDecision(CenterVecData.INSTANCE);
               } else {
                  if (!cfg.combos() && isComboPhaseState(this.episodeStateMachine.getCurrentPhase())) {
                     this.endEpisodeWithReason(fightRetargetTime, "off", false, false, false);
                  }

                  if (this.targetTracker.getDataGroundMax() == null) {
                     return this.finalizeDecision(CenterVecData.INSTANCE);
                  } else {
                     CarefulBalancedType carefulBalancedType = cfg.playstyle() == null ? CarefulBalancedType.TERMINATOR : cfg.playstyle();
                     boolean counterVecFlag = selfPopData.lastPosture() == DefensiveAggressiveType.DEFENSIVE || selfPopData.lastPosture() == DefensiveAggressiveType.ESCAPE;
                     if (cfg.combos() && carefulBalancedType != CarefulBalancedType.CAREFUL && !counterVecFlag) {
                        if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
                           this.endEpisodeWithReason(fightRetargetTime, "hitloop", false, false, false);
                        }

                        this.pendingProposal = null;
                        this.clearTickFeedbackFlags();
                        VecPos.Loadout stackKnockback = VecPos.getStackKnockback(player, cfg.buildObsidian());
                        return this.finalizeDecision(
                           this.hitLoopCombo
                              .getMoveAir(
                                 new ComboingIntersects.Ctx(
                                    fightRetargetTime,
                                    selfPopData,
                                    vecBlockingData,
                                    self,
                                    this.targetTracker,
                                    this.worldView,
                                    minMax,
                                    this.pearlShotPlanner,
                                    this.pearlTracker,
                                    vecBlock,
                                    list,
                                    tickStatusData2,
                                    this.combineWithBlockedPositions(predicate),
                                    this.sprintTimer,
                                    this.selfDamageLimit,
                                    stackKnockback,
                                    this.breachingApi
                                 )
                              )
                        );
                     } else {
                        int ordinal = carefulBalancedType.ordinal();
                        SelfHazData selfHaz = SelfHazData.getSelfHaz(selfPopData.lastPosture(), carefulBalancedType);
                        double counterVecValue = cfg.fightRange() + PlaystyleTuning.COUNTER_RANGE_BONUS_BY_STYLE[ordinal];
                        boolean carefulDistanceFlag = selfPopData.lastPosture() == DefensiveAggressiveType.DEFENSIVE || selfPopData.lastPosture() == DefensiveAggressiveType.ESCAPE;
                        boolean counterVecFlag3 = cfg.eatGapples() && AbsorptionFoodUtils.needsHealingOrFood(self, carefulBalancedType) && AbsorptionFoodUtils.isSafeToEatByThreatAndCombo(selfPopData);
                        Vec3d blockingVec = this.getTargetPos();
                        Vec3d pos = self.pos();
                        double distanceTo = blockingVec.distanceTo(pos);
                        Vec3d vec3d = LivingEntityHelperVisible.getVec3d(pos.subtract(blockingVec));
                        int counterVecLimit = this.targetTracker.getValue(this.worldView, minMax, vec3d);
                        int livingValue = this.targetTracker.getInterpolatorPingTargetData().getMax();
                        this.targetFarTicks = vecBlockingData.distance() > counterVecValue ? this.targetFarTicks + 1 : 0;
                        boolean counterVecFlag4 = vecBlockingData.entity() != null && (vecBlockingData.entity().isDead() || vecBlockingData.entity().getHealth() <= 0.0F);
                        boolean counterVecFlag5 = counterVecFlag4 || this.isTargetAttackable(vecBlockingData) && this.targetFarTicks < 40;
                        if (counterVecFlag4
                           && this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE
                           && this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.FINISHER) {
                           this.endEpisodeWithReason(fightRetargetTime, "kill", false, true, false);
                        }

                        CenterVecData.State followJuggleType = this.episodeStateMachine.getCurrentPhase();
                        boolean counterVecFlag6 = followJuggleType != CenterVecData.State.IDLE;
                        boolean carefulDistance = EngagementGateRules.canEngageTarget(carefulBalancedType, self, vecBlockingData, carefulDistanceFlag, cfg.fightRange(), counterVecValue);
                        Predicate predicate2 = this.combineWithBlockedPositions(predicate);
                        this.stepPlanner.setExcludedBasePredicate(this::isPositionBlocked);
                        this.stepPlanner.setAvoidedPaths(this.getPearlTails());
                        this.stepPlanner.setMaxCandidateLimit(this.slotChangeTick == Long.MIN_VALUE ? 100 : (int)Math.min(100L, fightRetargetTime - this.slotChangeTick));
                        this.pearlShotPlanner.setVec3d(blockingVec);
                        boolean counterVecFlag7 = this.comboCooldownUntilTick != Long.MIN_VALUE && fightRetargetTime < this.comboCooldownUntilTick;
                        boolean belowAnchorFlag = false;
                        boolean counterVecFlag9 = false;
                        if (followJuggleType == CenterVecData.State.IDLE && !counterVecFlag7 && cfg.combos() && !carefulDistanceFlag) {
                           WeakFullUtils.Lift lift = this.targetTracker.getDataGroundMax().lift();
                           boolean entityPos = this.isFriendlyPlayerNearTarget(vecBlockingData);
                           boolean counterVecFlag10 = player.isTouchingWater() || player.isInLava() || player.getBlockStateAtPos().isOf(Blocks.COBWEB);
                           belowAnchorFlag = EngagementGateRules.checkEngageGate(carefulBalancedType, self, selfPopData.threat(), vecBlockingData, entityPos, counterVecFlag10, true, lift).ok()
                              && distanceTo <= ConstVec.frozenAmounts[ordinal] + 1.5;
                           counterVecFlag9 = EngagementGateRules.checkEngageGate(carefulBalancedType, self, selfPopData.threat(), vecBlockingData, entityPos, counterVecFlag10, false, lift).ok() && !selfPopData.nether();
                        }

                        boolean counterVecFlag11 = followJuggleType == CenterVecData.State.IDLE && belowAnchorFlag && this.targetTracker.isSampleGround() && counterVecLimit >= 8;
                        if (cfg.combos() && (this.isStepPlanningState(followJuggleType) || counterVecFlag11) && this.shouldReplanSteps(fightRetargetTime, tickStatusData2)) {
                           boolean idxFeetFlag = followJuggleType == CenterVecData.State.ANCHOR_BELOW;
                           CrystalStepPlanner.Step directionVecData = this.currentStep;
                           this.currentStep = this.stepPlanner
                              .planBestStepAllowGrounded(
                                 fightRetargetTime,
                                 this.worldView,
                                 this.targetTracker,
                                 self,
                                 vecBlockingData,
                                 this.opponentProfile,
                                 cfg,
                                 selfHaz,
                                 vecBlock,
                                 list,
                                 selfPopData.targetReadyIn(),
                                 this.getTicksSinceCrystalBreak(fightRetargetTime),
                                 this.selfDamageLimit,
                                 tickStatusData2,
                                 predicate2,
                                 directionVecData,
                                 idxFeetFlag
                              );
                           this.recordStepSearch(fightRetargetTime, tickStatusData2);
                        }

                        if (!cfg.combos()) {
                           this.bestFinish = null;
                           this.lastFinishEvalTick = Long.MIN_VALUE;
                        } else if (counterVecFlag6 && (this.lastFinishEvalTick == Long.MIN_VALUE || fightRetargetTime - this.lastFinishEvalTick >= 2L || fightRetargetTime < this.lastFinishEvalTick)) {
                           this.bestFinish = this.findBestFinish(fightRetargetTime, selfPopData, vecBlockingData, player, tickStatusData2, counterVecLimit);
                           this.lastFinishEvalTick = fightRetargetTime;
                        }

                        boolean counterVecFlag13 = this.bestFinish != null && this.bestFinish.fire() <= 6;
                        int counterVecValue3 = this.bestFinish == null ? Integer.MAX_VALUE : this.bestFinish.fire();
                        CenterVecData.Abort postureLethalType = EngagementGateRules.getAbortReason(
                           new EngagementGateRules.Inputs(
                              followJuggleType,
                              carefulDistanceFlag,
                              selfPopData.ticksSinceOwnPop(),
                              counterVecValue3,
                              self.getAbsorptionHealth(),
                              self.totemConfirmed(),
                              self.totems(),
                              self.crystals(),
                              this.isThreatLethal(selfPopData),
                              counterVecFlag3,
                              counterVecFlag5,
                              this.episodeStateMachine.getEpisodeTicks(),
                              carefulBalancedType
                           )
                        );
                        if (!this.canRecomputeCounterShot(fightRetargetTime)
                           || followJuggleType != CenterVecData.State.IDLE && followJuggleType != CenterVecData.State.FOLLOW && followJuggleType != CenterVecData.State.JUGGLE) {
                           if (followJuggleType == CenterVecData.State.PEARL_FOLLOW
                              && this.canRecomputeCounterShot(fightRetargetTime)
                              && (this.counter == null || this.counter.validUntil() <= fightRetargetTime + 1L)) {
                              String counterVecText = this.counter != null ? this.counter.why() : this.lastCounterReason;
                              CenterVecData.PearlShot counter = null;
                              VecMax.Forecast vecMax = this.pearlTracker.getVecMaxForInt(this.targetEntityId);
                              if ("counter".equals(counterVecText) && vecMax != null && this.canThrowAggroPearl(self, cfg, fightRetargetTime, carefulBalancedType)) {
                                 counter = this.pearlShotPlanner
                                    .getInstance(vecMax.landing(), pos, vecMax.eta() + 4, fightRetargetTime, this.worldView, vecBlock, list, self, 1.5, "counter");
                              } else if (!"counter".equals(counterVecText)) {
                                 counter = this.planAggroPearlShot(fightRetargetTime, selfPopData, vecBlockingData, self, vecBlock, list, cfg, carefulBalancedType, distanceTo, counterVecLimit, this.stateBeforePearlFollow);
                              }

                              if (counter != null) {
                                 this.counter = counter;
                              }

                              this.lastCounterShotTick = fightRetargetTime;
                           }
                        } else {
                           this.counter = this.planAggroPearlShot(fightRetargetTime, selfPopData, vecBlockingData, self, vecBlock, list, cfg, carefulBalancedType, distanceTo, counterVecLimit, followJuggleType);
                           this.lastCounterShotTick = fightRetargetTime;
                        }

                        if (this.counter != null) {
                           this.lastCounterReason = this.counter.why();
                        }

                        if (this.counter != null && this.counter.validUntil() < fightRetargetTime) {
                           this.counter = null;
                        }

                        boolean counterVecFlag14 = cfg.combos()
                           && (
                              followJuggleType == CenterVecData.State.IDLE && belowAnchorFlag
                                 || followJuggleType == CenterVecData.State.FOLLOW
                                 || followJuggleType == CenterVecData.State.OPENER
                                 || followJuggleType == CenterVecData.State.RECOMBO
                           );
                        if (counterVecFlag14 && (this.lastOfferSearchTick == Long.MIN_VALUE || fightRetargetTime - this.lastOfferSearchTick >= 4L || fightRetargetTime < this.lastOfferSearchTick)) {
                           double slotSwordFactor = followJuggleType == CenterVecData.State.OPENER && this.activeOffer != null ? this.activeOffer.spacing() : this.currentSpacing;
                           this.offers = this.offerFinder
                              .getOffer(
                                 fightRetargetTime,
                                 this.worldView,
                                 this.targetTracker,
                                 self,
                                 vecBlockingData,
                                 this.opponentProfile,
                                 cfg,
                                 selfHaz,
                                 slotSwordFactor,
                                 selfPopData.targetReadyIn(),
                                 this.selfDamageLimit,
                                 predicate2
                              );
                           this.lastOfferSearchTick = fightRetargetTime;
                           if (followJuggleType == CenterVecData.State.OPENER) {
                              this.refreshActiveOffer();
                           }
                        }

                        if (cfg.anchors() && (followJuggleType == CenterVecData.State.IDLE && counterVecFlag9 || followJuggleType == CenterVecData.State.ANCHOR_BELOW)) {
                           boolean counterVecFlag15 = false;
                           if (followJuggleType == CenterVecData.State.ANCHOR_BELOW && this.anchorPlan != null) {
                              this.anchorPlan = this.anchorPlanner.revalidatePlan(this.anchorPlan, this.worldView, self, this.targetTracker);
                              counterVecFlag15 = this.anchorPlan == null;
                           }

                           boolean counterVecFlag16 = followJuggleType == CenterVecData.State.ANCHOR_BELOW
                                 && this.subPhase == ComboPlanner.SubPhase.ANCHOR
                                 && this.anchorPlan != null
                                 && !this.isRespawnAnchorAt(this.anchorPlan.anchor())
                                 && !this.isAnchorCellAdjacentToTarget(this.anchorPlan.anchor())
                              || counterVecFlag15;
                           boolean counterVecFlag17 = this.lastAnchorSearchTick == Long.MIN_VALUE || fightRetargetTime - this.lastAnchorSearchTick >= 8L || fightRetargetTime < this.lastAnchorSearchTick;
                           if ((counterVecFlag17 || counterVecFlag16) && (followJuggleType == CenterVecData.State.IDLE || this.anchorPlan == null || !this.anchorPlan.arrived() || counterVecFlag16)) {
                              AnchorStandPlanner.Plan vecPosData = this.anchorPlanner
                                 .planAnchorStand(fightRetargetTime, this.worldView, this.targetTracker, self, vecBlockingData, cfg, selfPopData.targetReadyIn(), predicate2);
                              if (followJuggleType == CenterVecData.State.IDLE
                                 || this.anchorPlan == null
                                 || counterVecFlag16
                                 || vecPosData != null && vecPosData.anchor().equals(this.anchorPlan.anchor())) {
                                 this.anchorPlan = vecPosData;
                              }

                              this.lastAnchorSearchTick = fightRetargetTime;
                           }
                        }

                        if ((followJuggleType == CenterVecData.State.FOLLOW || followJuggleType == CenterVecData.State.RECOMBO)
                           && cfg.combos()
                           && (this.lastPrestageSearchTick == Long.MIN_VALUE || fightRetargetTime - this.lastPrestageSearchTick >= 4L || fightRetargetTime < this.lastPrestageSearchTick)) {
                           this.prestagePlan = this.findPrestagePlan(fightRetargetTime, self, vecBlockingData, followJuggleType, counterVecLimit, predicate2);
                           this.lastPrestageSearchTick = fightRetargetTime;
                        }

                        boolean counterVecFlag18 = followJuggleType == CenterVecData.State.RECOMBO;
                        VecMax.Forecast forecast = this.targetPearl == null
                           ? this.pearlTracker.getVecMaxForInt(this.targetEntityId)
                           : this.pearlTracker.getVecMaxVecEntryMap(this.targetPearl.id());
                        if (counterVecFlag18 && forecast == null) {
                           forecast = this.pearlTracker.getVecMaxForInt(this.targetEntityId);
                        }

                        boolean counterVecFlag19 = this.recomboStartTick != Long.MIN_VALUE && this.hasTargetLandedSince(this.recomboStartTick);
                        boolean counterVecFlag20 = false;
                        if (counterVecFlag18 && !counterVecFlag19) {
                           this.subPhase = this.chooseRecomboSubPhase(fightRetargetTime, selfPopData, self, vecBlockingData, vecBlock, list, cfg, carefulBalancedType, forecast, pos);
                           counterVecFlag20 = this.subPhase == ComboPlanner.SubPhase.COUNTER && this.counter != null;
                        }

                        boolean counterVecFlag21 = (followJuggleType == CenterVecData.State.FOLLOW || counterVecFlag18)
                           && cfg.combos()
                           && this.selectCandidateOffer(player, self, vecBlockingData, selfPopData, pos, blockingVec);
                        boolean counterVecFlag22 = cfg.combos()
                           && this.prestagePlan != null
                           && this.worldView.findCrystalAt(this.prestagePlan.base()) != null;
                        boolean counterVecFlag23 = this.prestagePlan != null && this.isTargetAbovePrestage(this.prestagePlan, blockingVec);
                        boolean counterVecFlag24 = this.crystalBoomTick != Long.MIN_VALUE && this.crystalBoomTick >= this.episodeStartTick;
                        boolean timeLiving = this.wasTargetLaunchedByBoom(followJuggleType, fightRetargetTime, livingValue);
                        boolean counterVecFlag25 = counterVecFlag24 && fightRetargetTime - this.crystalBoomTick > livingValue + 4 + (long)Math.ceil(this.targetTracker.getInterpolatorPingTargetData().getPingMs()) && !timeLiving;
                        boolean sampleGround = this.targetTracker.isSampleGround();
                        boolean counterVecFlag26 = this.targetTracker.isFlag() && !sampleGround;
                        boolean counterVecFlag27 = cfg.combos()
                           && this.currentStep != null
                           && this.currentStep.value() >= 2.0
                           && (!sampleGround || this.currentStep.breakTick() - fightRetargetTime <= Math.max(0, counterVecLimit - 1));
                        boolean counterVecFlag28 = cfg.aggroPearls() && this.counter != null && (followJuggleType != CenterVecData.State.JUGGLE || !counterVecFlag27);
                        boolean counterVecFlag29 = followJuggleType == CenterVecData.State.ANCHOR_BELOW && this.isTargetOffAnchorLayer(blockingVec, pos, distanceTo);
                        boolean counterVecFlag30 = this.targetPopTick != Long.MIN_VALUE && this.targetPopTick >= this.episodeStartTick && followJuggleType == CenterVecData.State.FINISHER;
                        ComboPhaseTracker.Inputs inputs = new ComboPhaseTracker.Inputs(
                           postureLethalType,
                           this.aggroActive,
                           this.strikeLanded,
                           counterVecFlag24,
                           timeLiving,
                           counterVecFlag25,
                           sampleGround,
                           counterVecFlag26,
                           counterVecLimit,
                           distanceTo,
                           counterVecFlag27,
                           counterVecFlag13,
                           counterVecFlag30,
                           counterVecFlag4,
                           counterVecFlag21,
                           counterVecFlag22,
                           counterVecFlag23,
                           counterVecFlag28,
                           this.pearlThrown,
                           this.pearlFailed,
                           this.targetPearlThrown,
                           counterVecFlag19,
                           counterVecFlag20,
                           this.hasAnchorDetonatedThisEpisode(),
                           this.anchorFailed,
                           counterVecFlag29,
                           this.offerStillValid,
                           carefulDistance,
                           ConstVec.frozenAmounts[ordinal],
                           ConstVec.frozenValues[ordinal],
                           cfg.combos() && this.prestagePlan != null
                        );
                        ComboPhaseTracker.Out frozenSeen = this.episodeStateMachine.advancePhase(inputs);
                        if (frozenSeen.episodeEnded()) {
                           boolean finishingVecFlag = postureLethalType != CenterVecData.Abort.NONE || "frozen".equals(frozenSeen.reason());
                           if (postureLethalType != CenterVecData.Abort.NONE) {
                              this.pendingAbort = postureLethalType;
                           }

                           this.recordEpisodeOutcome(fightRetargetTime, frozenSeen.reason(), finishingVecFlag, "kill".equals(frozenSeen.reason()), false, postureLethalType);
                        } else if (frozenSeen.state() != frozenSeen.previous()) {
                           this.onStateTransition(frozenSeen.previous(), frozenSeen.state(), fightRetargetTime, cfg, vecBlockingData, tickStatusData2);
                        }

                        this.clearTickFeedbackFlags();
                        CenterVecData.State currentPhase = this.episodeStateMachine.getCurrentPhase();
                        CenterVecData centerVecData;
                        if (currentPhase == CenterVecData.State.IDLE) {
                           centerVecData = this.planIdleDecision(
                              fightRetargetTime, selfPopData, vecBlockingData, self, player, cfg, carefulBalancedType, selfHaz, distanceTo, carefulDistance, carefulDistanceFlag, belowAnchorFlag, counterVecFlag9, counterVecFlag11, counterVecLimit, tickStatusData2, predicate2
                           );
                        } else {
                           this.pendingProposal = null;
                           centerVecData = this.planDecisionForState(currentPhase, fightRetargetTime, selfPopData, vecBlockingData, self, player, cfg, tickStatusData2, counterVecLimit, blockingVec, pos, counterVecValue3);
                        }

                        return this.finalizeDecision(centerVecData);
                     }
                  }
               }
            }
         } else {
            return this.finalizeDecision(CenterVecData.INSTANCE);
         }
      } else {
         return this.finalizeDecision(CenterVecData.INSTANCE);
      }
   }

   public CenterVecData.Stand getHitLoopApproachStand(long comboingIntersectsValue) {
      return this.hitLoopCombo.getChasingApproach(comboingIntersectsValue);
   }

   private int getTicksSinceCrystalBreak(long finishingVecValue) {
      return this.lastCrystalBreakTick == Long.MIN_VALUE ? 100 : (int)Math.max(0L, Math.min(100L, finishingVecValue - this.lastCrystalBreakTick));
   }

   public CrystalBotCombatStats getEatabEatbad() {
      return this.eatabEatbad;
   }

   public void setAggroActive(boolean flag) {
      this.aggroActive = flag;
   }

   private boolean isPositionBlocked(BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         Long long2 = this.blockedPosExpiryTicks.get(pos.asLong());
         return long2 != null && this.currentTick != Long.MIN_VALUE && this.currentTick < long2;
      }
   }

   private CenterVecData planDecisionForState(
      CenterVecData.State currentPhase,
      long finishingVecValue,
      SelfPopData selfPopData,
      VecBlockingData vecBlockingData,
      AbsorptionPosData absorptionPosData,
      ClientPlayerEntity clientPlayerEntity,
      PearlsPlaystyleData pearlsPlaystyleData,
      TickStatusData tickStatusData,
      int followingFollowLimit,
      Vec3d vec,
      Vec3d vec2,
      int finishingVecValue3
   ) {
      return switch (currentPhase) {
         case OPENER -> {
            ComboPlanner.SubPhase strikePrestage = this.computeOpenerSubPhase(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity);
            yield this.activeOffer == null
               ? this.planFollowDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity, pearlsPlaystyleData, tickStatusData, followingFollowLimit, vec, vec2)
               : this.planOpenerDecision(strikePrestage, this.activeOffer, finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity, tickStatusData);
         }
         case LAUNCH -> this.planLaunchDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, tickStatusData);
         case JUGGLE -> this.planJuggleDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData);
         case FOLLOW -> this.planFollowDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity, pearlsPlaystyleData, tickStatusData, followingFollowLimit, vec, vec2);
         case PEARL_FOLLOW -> this.planPearlFollowDecision(finishingVecValue, absorptionPosData, vec, vec2, followingFollowLimit);
         case ANCHOR_BELOW -> this.planAnchorBelowDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, tickStatusData);
         case RECOMBO -> this.planRecomboDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, vec, vec2);
         case FINISHER -> this.planFinisherDecision(finishingVecValue, selfPopData, vecBlockingData, absorptionPosData, clientPlayerEntity, tickStatusData, finishingVecValue3);
         default -> CenterVecData.INSTANCE;
      };
   }

   private List<List<Vec3d>> getPearlTails() {
      ArrayList arrayList = new ArrayList();

      for (VecMax.Forecast forecast : this.pearlTracker.getVecMax(this.selfPlayerId)) {
         arrayList.add(forecast.tail());
      }

      return arrayList;
   }

   public void applyTradeOverride(ComboTradeData comboTradeData, long overridePshotValue) {
      if (comboTradeData != null) {
         if (Float.isFinite(comboTradeData.maxSelfDamage())) {
            this.selfDamageLimit = Math.max(0.0F, comboTradeData.maxSelfDamage());
         }

         boolean overridePshotFlag = comboTradeData.combo() != null && comboTradeData.combo() == this.aggro && this.aggro != CenterVecData.INSTANCE;
         if (this.aggro.state() == CenterVecData.State.HIT_LOOP && (comboTradeData.lane() != this.aggro.lane() || comboTradeData.mode() != DefendEngageType.COMBO && !overridePshotFlag)) {
            this.hitLoopCombo.handleMaxTime(overridePshotValue);
         }

         if (comboTradeData.comboYield()) {
            if (this.aggro.state() == CenterVecData.State.HIT_LOOP) {
               this.hitLoopCombo.handleMaxTime(overridePshotValue);
            }

            this.comboYieldStreak++;
         } else {
            boolean overridePshotFlag2 = comboTradeData.mode() == DefendEngageType.COMBO || overridePshotFlag;
            if (overridePshotFlag2
               && this.episodeStateMachine.getCurrentPhase() == CenterVecData.State.IDLE
               && this.pendingProposal != null
               && this.pendingProposal.kind() != CenterVecData.Proposal.NONE) {
               ComboPlanner.Proposal proposal = this.pendingProposal;
               ComboPhaseTracker.Out juggleFollow = this.episodeStateMachine.startEpisode(proposal.state());
               if (juggleFollow.episodeStarted()) {
                  this.beginEpisode(overridePshotValue, proposal);
               }

               this.comboYieldStreak = 0;
            } else if (overridePshotFlag2) {
               this.comboYieldStreak = 0;
            } else {
               if (this.episodeStateMachine.getCurrentPhase() != CenterVecData.State.IDLE) {
                  String overridePshotText = comboTradeData.mode() == null ? "override" : comboTradeData.mode().name();
                  this.episodeStateMachine.endEpisode(overridePshotText);
                  this.recordEpisodeOutcome(overridePshotValue, overridePshotText, true, false, false, CenterVecData.Abort.NONE);
                  if (isRetreatOrDefendMode(comboTradeData.mode(), comboTradeData.lane())) {
                     this.comboCooldownUntilTick = overridePshotValue + 40L;
                  }
               }
            }
         }
      }
   }

   private boolean isPearlDistanceAllowed(CarefulBalancedType carefulBalancedType, AbsorptionPosData absorptionPosData, VecBlockingData vecBlockingData, double awaitingTotemsLimit) {
      return switch (carefulBalancedType) {
         case TERMINATOR -> awaitingTotemsLimit >= 5.0;
         case BALANCED -> awaitingTotemsLimit >= 14.0 && (absorptionPosData.getAbsorptionHealth() >= 20.0F && absorptionPosData.totems() >= 3 || vecBlockingData.awaitingTotem() || vecBlockingData.getHealthTrusted() <= 9.0F);
         case CAREFUL -> false;
      };
   }

   private CenterVecData planFinisherDecision(
      long finishingFinisherValue, SelfPopData selfPopData, VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, ClientPlayerEntity clientPlayerEntity, TickStatusData tickStatusData, int finishingFinisherValue2
   ) {
      ComboPlanner.Finish maxPosData = this.bestFinish;
      Vec3d blockingVec = this.getTargetPos();
      CenterVecData.Stand stand = this.aggro.stand() != null
         ? this.aggro.stand()
         : new CenterVecData.Stand(CenterVecData.StandKind.HOLD, absorptionPosData.pos(), 0.1, false, true, Long.MIN_VALUE);
      if (maxPosData == null) {
         return this.buildDecision(
            CenterVecData.State.FINISHER,
            CenterVecData.Proposal.NONE,
            0.0,
            SurroundKitType.CRYSTAL,
            null,
            null,
            null,
            null,
            stand,
            blockingVec.add(0.0, 1.2, 0.0),
            finishingFinisherValue2,
            "Finishing",
            this.getProposalArmIfActive()
         );
      } else {
         switch (maxPosData.kind()) {
            case HELD:
               CenterVecData.CrystalStep livingEntity = this.buildCrystalStep(
                  maxPosData.cell(), null, this.getPlacementFace(maxPosData.cell(), absorptionPosData.eye()), maxPosData.fire() > 0, maxPosData.fire() <= 0
               );
               return this.buildDecision(
                  CenterVecData.State.FINISHER,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.CRYSTAL,
                  livingEntity,
                  null,
                  null,
                  null,
                  stand,
                  BlockEntityUtils.getVec3d(maxPosData.cell()).add(0.0, 1.0, 0.0),
                  finishingFinisherValue2,
                  "Finishing",
                  this.getProposalArmIfActive()
               );
            case ANCHOR:
               CenterVecData.AnchorStep anchorStep = new CenterVecData.AnchorStep(maxPosData.cell(), maxPosData.fire() <= 0);
               return this.buildDecision(
                  CenterVecData.State.FINISHER,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.ANCHOR,
                  null,
                  null,
                  null,
                  anchorStep,
                  stand,
                  Vec3d.ofCenter(maxPosData.cell()),
                  finishingFinisherValue2,
                  "Finishing",
                  this.getProposalArmIfActive()
               );
            case BASE:
            case BUILD:
            default:
               CrystalStepPlanner.Step directionVecData = this.currentStep;
               CenterVecData.CrystalStep crystalStep = directionVecData != null && directionVecData.base().equals(maxPosData.cell())
                  ? this.buildStepCrystalStep(finishingFinisherValue, directionVecData, false)
                  : this.buildCrystalStep(
                     maxPosData.cell(),
                     maxPosData.kind() == ComboPlanner.FinishKind.BUILD ? maxPosData.cell() : null,
                     this.getPlacementFace(maxPosData.cell(), absorptionPosData.eye()),
                     maxPosData.fire() > 0,
                     maxPosData.fire() <= 0
                  );
               CenterVecData.Stand stand2 = directionVecData != null && directionVecData.base().equals(maxPosData.cell())
                  ? new CenterVecData.Stand(CenterVecData.StandKind.UNDER_TARGET, directionVecData.stand(), 0.4, false, true, Long.MIN_VALUE)
                  : stand;
               return this.buildDecision(
                  CenterVecData.State.FINISHER,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.CRYSTAL,
                  crystalStep,
                  null,
                  null,
                  null,
                  stand2,
                  BlockEntityUtils.getVec3d(maxPosData.cell()).add(0.0, 1.0, 0.0),
                  finishingFinisherValue2,
                  "Finishing",
                  this.getProposalArmIfActive()
               );
            case MELEE:
               VecPos.Loadout stackKnockback = VecPos.getStackKnockback(clientPlayerEntity, false);
               int finishingFinisherValue3 = stackKnockback.swordSlot() >= 0 ? stackKnockback.swordSlot() : clientPlayerEntity.getInventory().getSelectedSlot();
               Vec3d vec3d = LivingEntityHelperVisible.getVec3d(blockingVec.subtract(absorptionPosData.pos()));
               float finishingFinisherValue4 = VecShieldUtilsAmounts.getAmount(vec3d.x, vec3d.z);
               double degreesLimit = Math.max(0.5, WorldCollisionHelper.horizontalDistance(absorptionPosData.pos(), blockingVec));
               CenterVecData.Strike strike = new CenterVecData.Strike(finishingFinisherValue3, this.sprintTimer.isSprinting(), finishingFinisherValue4, (float)VecShieldUtilsAmounts.getDegrees(degreesLimit), false);
               return this.buildDecision(
                  CenterVecData.State.FINISHER,
                  CenterVecData.Proposal.NONE,
                  0.0,
                  SurroundKitType.MELEE,
                  null,
                  strike,
                  null,
                  null,
                  stand,
                  blockingVec.add(0.0, 1.2, 0.0),
                  finishingFinisherValue2,
                  "Finishing",
                  this.getProposalArmIfActive()
               );
         }
      }
   }

   private boolean isOnPearlPath(BlockPos pos) {
      for (VecMax.Forecast forecast : this.pearlTracker.getVecMax(this.selfPlayerId)) {
         if (doesPathPassNearCell(pos, forecast.tail())) {
            return true;
         }
      }

      return false;
   }

   private void clearEpisodeState() {
      this.episodeActive = false;
      this.proposalActive = false;
      this.finisherReached = false;
      this.proposalArm = null;
      this.spacingChoice = null;
      this.finisherKind = null;
      this.targetDamageTotal = 0.0;
      this.totalEhpDrop = 0.0;
      this.targetPopCount = 0;
      this.selfDamageEventCount = 0;
      this.juggleCount = 0;
      this.activeOffer = null;
      this.candidateOffer = null;
      this.subPhase = ComboPlanner.SubPhase.NONE;
      this.strikeReadyTick = Long.MIN_VALUE;
      this.launchBase = null;
      this.usingLaunchCrystal = false;
      this.launchBreakTick = Long.MIN_VALUE;
      this.offerStillValid = true;
      this.episodeStartTick = Long.MIN_VALUE;
      this.recomboStartTick = Long.MIN_VALUE;
      this.eatabActive = false;
      this.selfDropWindowEndTick = Long.MIN_VALUE;
      this.selfDropWindowTotal = 0.0F;
      this.clearStepPlan();
      this.bestFinish = null;
      this.lastFinishEvalTick = Long.MIN_VALUE;
      this.stateBeforePearlFollow = CenterVecData.State.FOLLOW;
   }

   private record Break(Vec3d centre, long tick) {
   }

   public record Feedback(
      long tick,
      boolean struck,
      boolean strikeWasSprint,
      TickStatusData crystal,
      boolean pearlThrown,
      boolean pearlFailed,
      boolean anchorDetonated,
      boolean anchorFailed,
      float selfEhpDrop,
      float targetDamageDealt,
      boolean ownPop,
      boolean targetPop,
      boolean acted
   ) {
      public Feedback(
         long anchorFailed,
         boolean struck,
         boolean selfEhpDrop,
         TickStatusData pearlThrown,
         boolean anchorDetonated,
         boolean strikeWasSprint,
         boolean crystal,
         boolean tick,
         float pearlFailed,
         float targetPop,
         boolean targetDamageDealt,
         boolean ownPop
      ) {
         this(
            anchorFailed,
            struck,
            selfEhpDrop,
            pearlThrown,
            anchorDetonated,
            strikeWasSprint,
            crystal,
            tick,
            pearlFailed,
            targetPop,
            targetDamageDealt,
            ownPop,
            false
         );
      }
   }

   private static enum FinishKind {
      HELD,
      ANCHOR,
      BASE,
      BUILD,
      MELEE;
   }

   private record Finish(ComboPlanner.FinishKind kind, int fire, float damage, boolean kill, float selfBound, BlockPos cell, boolean airborne) {
   }

   private record MeleeCheck(long at, long kickTick, double predicted, double predictedRise) {
   }

   private record OwnPearl(boolean aggro, long tick) {
   }

   private record Prestage(BlockPos base, boolean build, Vec3d landing, int top, Direction face, long until) {
   }

   private record Proposal(
      CenterVecData.Proposal kind,
      CenterVecData.State state,
      double value,
      VecPos.Offer offer,
      AnchorStandPlanner.Plan anchor,
      String arm,
      String spacingArm,
      double spacing
   ) {
   }

   private static enum SubPhase {
      NONE,
      STAGE,
      LINE_UP,
      CHARGE,
      STRIKE,
      LAUNCH_NOW,
      ANCHOR,
      CRYSTAL_BELOW,
      PRESTAGE,
      COUNTER,
      RUN;
   }

   private record TargetPearl(int id, Vec3d from, long tick, boolean low) {
   }
}
