package com.corz.client.utils;

import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.HimEtaData;
import com.corz.client.utils.AggressiveDefensive;
import com.corz.client.utils.AbsorptionFoodUtils;
import com.corz.client.utils.PlaystyleTuning;

public final class CombatComboPlanner {
   public static final String UNPLACED = "unplaced";
   private final AggressiveDefensive aggressionSelector;
   private long lastAnchorPlacedTick = Long.MIN_VALUE;
   public static final String OWED = "owed";
   public static final String CAMP = "camp";
   private long lastCrystalLaneTick = Long.MIN_VALUE;
   public static final String BACKING = "Backing off";
   public static final String PLACED = "placed";
   public static final String ANTI_ANCHOR = "Anti-anchor";

   static String getEngageStatusText(DefendEngageType defendEngageType) {
      return switch (defendEngageType) {
         case IDLE -> "Searching";
         case CHASE -> "Chasing";
         case HEAL -> "Healing";
         case MEND -> "Mending";
         case ESCAPE -> "Escaping";
         case REFILL -> "Refilling";
         case LOOT -> "Looting";
         case BREACH -> "Breaching";
         case CAMP -> "Camping";
         default -> "Fighting";
      };
   }

   static boolean canUseCenterForLane(CenterVecData centerVecData, SurroundKitType surroundKitType, DefensiveAggressiveType defensiveAggressiveType) {
      if (centerVecData != null && centerVecData.isIdleState()) {
         return centerVecData.lane() == surroundKitType
            ? true
            : surroundKitType == SurroundKitType.ANCHOR
               && centerVecData.abort() == CenterVecData.Abort.NONE
               && defensiveAggressiveType != DefensiveAggressiveType.DEFENSIVE
               && defensiveAggressiveType != DefensiveAggressiveType.ESCAPE;
      } else {
         return false;
      }
   }

   static boolean canBlockGuard(SelfPopData selfPopData) {
      CaseMaxData options = selfPopData.getOptions(SurroundKitType.GUARD);
      return options.available() && "block".equals(options.why());
   }

   static boolean isTargetNearThreatCenter(SelfPopData selfPopData, VecBlock.Threat threat) {
      VecBlockingData target = selfPopData.target();
      return target != null && target.pos() != null && threat != null && threat.liveCenter() != null ? target.pos().distanceTo(threat.liveCenter()) <= 3.0 : false;
   }

   static double getBestProposalValue(SelfPopData selfPopData, CenterVecData centerVecData) {
      double proposalAnchor = Double.NEGATIVE_INFINITY;

      for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
         if (surroundKitType != SurroundKitType.ANCHOR || isAnchorLaneEnabled(selfPopData)) {
            CaseMaxData options = selfPopData.getOptions(surroundKitType);
            if (options.available() && Double.isFinite(options.value())) {
               proposalAnchor = Math.max(proposalAnchor, options.value());
            }
         }
      }

      if (centerVecData != null && centerVecData.lane() != null && centerVecData.lane() != SurroundKitType.NONE && Double.isFinite(centerVecData.proposalValue())) {
         proposalAnchor = Math.max(proposalAnchor, centerVecData.proposalValue());
      }

      return proposalAnchor;
   }

   private static SurroundKitType nullToNone(SurroundKitType surroundKitType) {
      return surroundKitType == null ? SurroundKitType.NONE : surroundKitType;
   }

   static boolean canCrystalOverrideUnplacedAnchorEagerly(SelfPopData selfPopData, CenterVecData centerVecData) {
      return canCrystalOverrideUnplacedAnchor(selfPopData, centerVecData, true);
   }

   private static DanceChaseType getDanceChaseForLane(SurroundKitType surroundKitType, DefensiveAggressiveType defensiveAggressiveType) {
      return switch (surroundKitType) {
         case EAT -> defensiveAggressiveType == DefensiveAggressiveType.DEFENSIVE ? DanceChaseType.ENTER_HOLE : DanceChaseType.STILL;
         case XP, KIT, SURROUND -> DanceChaseType.STILL;
         case BREACH -> DanceChaseType.HOLD;
         case ANCHOR, PEARL, MELEE -> defensiveAggressiveType == DefensiveAggressiveType.ESCAPE && surroundKitType == SurroundKitType.PEARL ? DanceChaseType.FLEE : DanceChaseType.HOLD;
         case CRYSTAL, GUARD -> DanceChaseType.DANCE;
         case NONE -> DanceChaseType.STILL;
      };
   }

   static boolean isTerminatorLoopWithTotems(SelfPopData selfPopData, CenterVecData centerVecData) {
      if (centerVecData != null && centerVecData.isLoopHit()) {
         CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
         AbsorptionPosData self = selfPopData.self();
         return carefulBalancedType == CarefulBalancedType.TERMINATOR && self != null && self.totemConfirmed() && self.totems() >= 2;
      } else {
         return false;
      }
   }

   private static ComboTradeData buildComboPlan(
      CombatComboPlanner.Ctx ctx, DefendEngageType defendEngageType, SurroundKitType surroundKitType, DanceChaseType danceChaseType, String antiText, CenterVecData centerVecData, boolean antiFlag
   ) {
      return new ComboTradeData(
         defendEngageType,
         ctx.aggressionPosture,
         ctx.selfHazWeights,
         surroundKitType,
         danceChaseType,
         ctx.dodge,
         ctx.minDamage,
         ctx.selfWeight,
         ctx.maxSelfDamage,
         ctx.allowTotemTrade,
         ctx.forceTimedType,
         ctx.anchorRelease,
         ctx.restockWindow,
         antiText,
         centerVecData,
         antiFlag,
         ctx.selfPop
      );
   }

   static AggressiveDefensive.Inputs buildAggressionInputs(SelfPopData selfPopData, CarefulBalancedType carefulBalancedType) {
      AbsorptionPosData self = selfPopData.self();
      VecBlockingData target = selfPopData.target();
      VecBlock.Threat threat = selfPopData.threat() != null ? selfPopData.threat() : VecBlock.Threat.INSTANCE;
      float threatTotemBound = 0.0F;

      for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
         CaseMaxData options = selfPopData.getOptions(surroundKitType);
         if (options.available() && Double.isFinite(options.value())) {
            threatTotemBound = Math.max(threatTotemBound, (float)options.value());
         }
      }

      return new AggressiveDefensive.Inputs(
         self.getAbsorptionHealth(),
         self.totems(),
         self.totemConfirmed(),
         threat.maxIncoming(),
         threat.lethal(),
         threatTotemBound,
         target == null ? 20.0F : target.getHealthTrusted(),
         target != null && target.awaitingTotem(),
         target != null && target.eating(),
         target == null ? 1.0F : target.armorMinDurability(),
         selfPopData.enemiesWithin8(),
         self.pearls() > 0,
         carefulBalancedType
      );
   }

   static boolean wasAnchorJustPlaced(SelfPopData selfPopData, SurroundKitType surroundKitType, long placedAnchorValue) {
      if (surroundKitType != SurroundKitType.ANCHOR
         || !"placed".equals(selfPopData.getOptions(SurroundKitType.ANCHOR).why())
         || selfPopData.lastLane() != SurroundKitType.ANCHOR) {
         return false;
      } else {
         return placedAnchorValue != Long.MIN_VALUE && selfPopData.tick() >= placedAnchorValue && selfPopData.tick() - placedAnchorValue <= 6L ? !selfPopData.getOptions(SurroundKitType.MELEE).kills() : false;
      }
   }

   static String getComboStatusText(CenterVecData centerVecData) {
      return centerVecData.status() != null && !centerVecData.status().isBlank() ? centerVecData.status() : getEngageStatusText(DefendEngageType.COMBO);
   }

   static boolean shouldUseGuardLane(SelfPopData selfPopData, CenterVecData centerVecData, CaseMaxData caseMaxData, boolean defuseSpotFlag) {
      if (caseMaxData == null || !caseMaxData.available()) {
         return false;
      } else if (defuseSpotFlag) {
         return true;
      } else if (selfPopData.getSelfPop().ref() && isCrystalOrAnchorPlanActive(selfPopData, centerVecData)) {
         return false;
      } else if ("face".equals(caseMaxData.why())) {
         return !hasActiveAttackPlan(selfPopData, centerVecData);
      } else if ("spot".equals(caseMaxData.why()) && isTerminatorLoopWithTotems(selfPopData, centerVecData)) {
         return false;
      } else {
         return isCrystalOrAnchorPlanActive(selfPopData, centerVecData) && !"defuse".equals(caseMaxData.why()) ? false : caseMaxData.value() >= getBestProposalValue(selfPopData, centerVecData);
      }
   }

   static DanceChaseType getDanceChaseForStand(CenterVecData centerVecData) {
      if (centerVecData != null && centerVecData.stand() != null) {
         return centerVecData.stand().kind() == CenterVecData.StandKind.FOLLOW ? DanceChaseType.CHASE : DanceChaseType.HOLD;
      } else {
         return DanceChaseType.DANCE;
      }
   }

   private static CaseMaxData pickBestLaneOption(SelfPopData selfPopData, boolean meleePrepFlag) {
      CaseMaxData meleePrep = null;
      double meleePrepValue = Double.NEGATIVE_INFINITY;

      for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
         if (surroundKitType != SurroundKitType.ANCHOR || isAnchorLaneEnabled(selfPopData)) {
            CaseMaxData options = selfPopData.getOptions(surroundKitType);
            if (options.available() && (!meleePrepFlag || surroundKitType != SurroundKitType.MELEE || selfPopData.targetReadyIn() <= 3 && options.prepTicks() <= 3)) {
               double meleePrepLimit = options.kills() ? 1000.0 - options.prepTicks() : options.value();
               if (meleePrepLimit > meleePrepValue) {
                  meleePrepValue = meleePrepLimit;
                  meleePrep = options;
               }
            }
         }
      }

      return meleePrep;
   }

   static boolean canCrystalOverrideUnplacedAnchor(SelfPopData selfPopData, CenterVecData centerVecData, boolean unplacedCrystalFlag) {
      CaseMaxData options = selfPopData.getOptions(SurroundKitType.ANCHOR);
      CaseMaxData options2 = selfPopData.getOptions(SurroundKitType.CRYSTAL);
      if (centerVecData.lane() == SurroundKitType.CRYSTAL && "unplaced".equals(options.why()) && options2.available()) {
         return options.prepTicks() > 9 && options2.value() > options.value()
            ? true
            : options2.kills() || unplacedCrystalFlag && centerVecData.crystal() != null && selfPopData.targetReadyIn() > 9 && options2.value() >= 1.5 * options.value() && options2.value() - options.value() >= 8.0;
      } else {
         return false;
      }
   }

   static boolean isAnchorFinisherThreat(SelfPopData selfPopData, CenterVecData centerVecData) {
      if (!canBlockGuard(selfPopData)) {
         return false;
      } else if (canAnchorKillSelf(selfPopData)) {
         return true;
      } else if (selfPopData.getSelfPop().ref() && isCrystalOrAnchorPlanActive(selfPopData, centerVecData)) {
         return false;
      } else if (centerVecData != null && centerVecData.state() == CenterVecData.State.FINISHER && centerVecData.killInTicks() <= 1) {
         return false;
      } else {
         for (SurroundKitType surroundKitType : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
            if (surroundKitType != SurroundKitType.ANCHOR || isAnchorLaneEnabled(selfPopData)) {
               CaseMaxData options = selfPopData.getOptions(surroundKitType);
               if (options.available() && (options.pops() || options.kills()) && options.prepTicks() <= 1) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   static boolean canCounterWithLane(SelfPopData selfPopData, CenterVecData centerVecData, SurroundKitType surroundKitType, DefensiveAggressiveType defensiveAggressiveType, boolean sentCounterFlag) {
      if (centerVecData != null && centerVecData.isLoopHit()) {
         boolean sentCounterFlag2 = selfPopData.targetReadyIn() <= 0 && centerVecData.strike() != null;
         if (surroundKitType == SurroundKitType.ANCHOR) {
            return centerVecData.strike() != null && isAnchorFollowUpReady(selfPopData) || canCrystalOverrideUnplacedAnchor(selfPopData, centerVecData, sentCounterFlag);
         } else if (surroundKitType == SurroundKitType.PEARL) {
            if (defensiveAggressiveType == DefensiveAggressiveType.ESCAPE) {
               return false;
            } else {
               return "counter".equals(selfPopData.getOptions(SurroundKitType.PEARL).why()) ? sentCounterFlag2 : centerVecData.lane() != SurroundKitType.PEARL || sentCounterFlag2;
            }
         } else if (surroundKitType != SurroundKitType.GUARD) {
            return false;
         } else if ("block".equals(selfPopData.getOptions(SurroundKitType.GUARD).why())) {
            return selfPopData.getOptions(SurroundKitType.MELEE).kills();
         } else {
            return centerVecData.abort() == CenterVecData.Abort.NONE && !isAnchorThreateningSelf(selfPopData)
               ? sentCounterFlag2 || "sent".equals(selfPopData.getOptions(SurroundKitType.GUARD).why()) && isCrystalOrAnchorPlanActive(selfPopData, centerVecData)
               : false;
         }
      } else {
         return false;
      }
   }

   private static ComboTradeData getKillComboIfAvailable(CombatComboPlanner.Ctx ctx, SelfPopData selfPopData, HimEtaData himEtaData, AbsorptionPosData absorptionPosData) {
      if (selfPopData.targetReadyIn() > 3 && !ctx.selfPop.ref()) {
         return null;
      } else if (himEtaData.fullUs() >= absorptionPosData.getAbsorptionHealth() - 0.5F && !absorptionPosData.totemConfirmed()) {
         return null;
      } else {
         CaseMaxData meleePrep = pickBestLaneOption(selfPopData, ctx.selfPop.ref());
         return meleePrep != null && meleePrep.kills() ? buildKillComboPlan(ctx, selfPopData, meleePrep) : null;
      }
   }

   static boolean isAnchorFollowUpReady(SelfPopData selfPopData) {
      String why = selfPopData.getOptions(SurroundKitType.ANCHOR).why();
      if ("unplaced".equals(why)) {
         return selfPopData.getOptions(SurroundKitType.ANCHOR).prepTicks() >= 2 || selfPopData.getOptions(SurroundKitType.MELEE).kills();
      } else if (!"placed".equals(why)) {
         return false;
      } else {
         AbsorptionPosData self = selfPopData.self();
         return self != null && self.swordCharge() >= 0.8F || selfPopData.getOptions(SurroundKitType.MELEE).kills();
      }
   }

   static boolean isAnchorThreateningSelf(SelfPopData selfPopData) {
      HimEtaData anchor = selfPopData.anchor();
      AbsorptionPosData self = selfPopData.self();
      return anchor != null && anchor.isCell() && self != null
         ? anchor.canChargesEta(self.getAbsorptionHealth()) || !self.totemConfirmed() && anchor.fullUs() >= self.getAbsorptionHealth() - 0.5F
         : false;
   }

   static boolean isCrystalOrAnchorPlanActive(SelfPopData selfPopData, CenterVecData centerVecData) {
      if (centerVecData == null || !centerVecData.isLoopHit() || centerVecData.abort() != CenterVecData.Abort.NONE) {
         return false;
      } else if (centerVecData.crystal() != null || centerVecData.anchor() != null) {
         return true;
      } else {
         return centerVecData.strike() != null && selfPopData.targetReadyIn() <= 0 ? true : centerVecData.lane() == SurroundKitType.CRYSTAL || centerVecData.lane() == SurroundKitType.ANCHOR;
      }
   }

   public DefensiveAggressiveType getDefensiveAggressiveType() {
      return this.aggressionSelector.getCurrentStance();
   }

   public CombatComboPlanner(AggressiveDefensive aggressiveDefensive) {
      this.aggressionSelector = aggressiveDefensive == null ? new AggressiveDefensive() : aggressiveDefensive;
   }

   static boolean canPearlStrike(SelfPopData selfPopData, CenterVecData centerVecData) {
      return centerVecData != null && centerVecData.isLoopHit() && centerVecData.lane() == SurroundKitType.PEARL && centerVecData.strike() != null && selfPopData.targetReadyIn() <= 0;
   }

   private static ComboTradeData buildBreachLanePlan(CombatComboPlanner.Ctx ctx, DefendEngageType defendEngageType, CenterVecData centerVecData) {
      updateForceTimedFromCenter(ctx, centerVecData);
      SurroundKitType surroundKitType = centerVecData.lane() != null && centerVecData.lane() != SurroundKitType.NONE ? centerVecData.lane() : SurroundKitType.BREACH;
      return buildComboPlan(ctx, defendEngageType, surroundKitType, getDanceChaseForStand(centerVecData), getEngageStatusText(defendEngageType), centerVecData, false);
   }

   static boolean isUnplacedAnchorOrSupportLane(SurroundKitType surroundKitType, String unplacedText) {
      if (surroundKitType == null) {
         return false;
      } else {
         return switch (surroundKitType) {
            case EAT, XP, KIT, SURROUND, BREACH -> true;
            case ANCHOR -> "unplaced".equals(unplacedText);
            default -> false;
         };
      }
   }

   public void resetState() {
      this.aggressionSelector.reset();
      this.lastAnchorPlacedTick = Long.MIN_VALUE;
      this.lastCrystalLaneTick = Long.MIN_VALUE;
   }

   static boolean isAnchorPlacementStatus(String unplacedPlacedText) {
      return "unplaced".equals(unplacedPlacedText) || "placed".equals(unplacedPlacedText);
   }

   private static ComboTradeData buildDefendComboPlan(CombatComboPlanner.Ctx ctx, SelfPopData selfPopData, CenterVecData centerVecData, VecBlock.Threat threat) {
      if (centerVecData.yields() < 3 && isLiveCrystalThreateningSelf(selfPopData, threat)) {
         return buildComboPlan(
            ctx, DefendEngageType.DEFEND, SurroundKitType.CRYSTAL, getDanceChaseForStand(centerVecData), getEngageStatusText(DefendEngageType.DEFEND), centerVecData, true
         );
      } else {
         updateForceTimedFromCenter(ctx, centerVecData);
         SurroundKitType surroundKitType = canPearlStrike(selfPopData, centerVecData) ? SurroundKitType.MELEE : nullToNone(centerVecData.lane());
         return buildComboPlan(ctx, DefendEngageType.COMBO, surroundKitType, getDanceChaseForStand(centerVecData), getComboStatusText(centerVecData), centerVecData, false);
      }
   }

   static boolean canAnchorKillSelf(SelfPopData selfPopData) {
      HimEtaData anchor = selfPopData.anchor();
      AbsorptionPosData self = selfPopData.self();
      return anchor != null && anchor.isCell() && self != null && anchor.hitUs() >= self.getAbsorptionHealth() - 0.5F;
   }

   static boolean isLaneNotBreachOrPearl(SurroundKitType surroundKitType) {
      return surroundKitType != SurroundKitType.BREACH && surroundKitType != SurroundKitType.PEARL;
   }

   static boolean hasTotemsWhileTargetEating(VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData) {
      return vecBlockingData != null && vecBlockingData.eating() && absorptionPosData != null && !AbsorptionFoodUtils.isFoodLevelCritical(absorptionPosData) ? absorptionPosData.totems() >= 1 && absorptionPosData.getAbsorptionHealth() > 8.0F : false;
   }

   static boolean isCampStatus(String campText) {
      return campText != null && (campText.equals("camp") || campText.startsWith("camp:"));
   }

   public static boolean canGuardAvailable(SelfPopData selfPopData) {
      HimEtaData anchor = selfPopData.anchor();
      AbsorptionPosData self = selfPopData.self();
      if (anchor != null && self != null) {
         boolean guardAvailableFlag = selfPopData.getOptions(SurroundKitType.GUARD).available() && (anchor.canChargesEta(self.getAbsorptionHealth()) || isAnchorThreateningSelf(selfPopData));
         return guardAvailableFlag || canBlockGuard(selfPopData) && canAnchorKillSelf(selfPopData);
      } else {
         return false;
      }
   }

   static boolean isLiveCrystalThreateningSelf(SelfPopData selfPopData, VecBlock.Threat threat) {
      return threat.liveCrystalId() >= 0
         && threat.etaTicks() <= 2
         && threat.liveMax() >= Math.max(6.0F, 0.3F * selfPopData.self().getAbsorptionHealth())
         && isCrystalDenyAvailable(selfPopData)
         && (selfPopData.selfClosedFor() >= 1 || isTargetNearThreatCenter(selfPopData, threat));
   }

   private static boolean isAnchorLaneEnabled(SelfPopData selfPopData) {
      return selfPopData.cfg() != null && selfPopData.cfg().anchors() && !selfPopData.nether();
   }

   static boolean canCounterWithLaneEagerly(SelfPopData selfPopData, CenterVecData centerVecData, SurroundKitType surroundKitType, DefensiveAggressiveType defensiveAggressiveType) {
      return canCounterWithLane(selfPopData, centerVecData, surroundKitType, defensiveAggressiveType, true);
   }

   public ComboTradeData planCombo(SelfPopData selfPopData) {
      CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
      DefensiveAggressiveType defensiveAggressive = this.aggressionSelector.decideStance(buildAggressionInputs(selfPopData, carefulBalancedType));
      SelfHazData selfHaz = SelfHazData.getSelfHaz(defensiveAggressive, carefulBalancedType);
      VecBlockingData target = selfPopData.target();
      AbsorptionPosData self = selfPopData.self();
      CenterVecData centerVecData = selfPopData.combo() != null ? selfPopData.combo() : CenterVecData.INSTANCE;
      boolean anchorAntiFlag = centerVecData.lane() == SurroundKitType.CRYSTAL && this.lastCrystalLaneTick == selfPopData.tick() - 1L;
      this.lastCrystalLaneTick = centerVecData.lane() == SurroundKitType.CRYSTAL ? selfPopData.tick() : Long.MIN_VALUE;
      CenterVecData centerVecData2 = selfPopData.breach() != null ? selfPopData.breach() : CenterVecData.INSTANCE;
      HimEtaData himEtaData = selfPopData.anchor() != null ? selfPopData.anchor() : HimEtaData.INSTANCE;
      VecBlock.Threat threat = selfPopData.threat() != null ? selfPopData.threat() : VecBlock.Threat.INSTANCE;
      PearlsPlaystyleData cfg = selfPopData.cfg();
      boolean anchorAntiFlag2 = target != null && target.isHoleSides() && (target.getHealthTrusted() <= 10.0F || target.armorMinDurability() <= 0.25F || selfPopData.stallWindows() >= 3);
      CombatComboPlanner.Ctx ctx = new CombatComboPlanner.Ctx(defensiveAggressive, selfHaz);
      ctx.selfPop = selfPopData.getSelfPop();
      ctx.minDamage = anchorAntiFlag2 ? 2.5F : selfHaz.minUseful();
      ctx.selfWeight = selfHaz.self();
      ctx.maxSelfDamage = UnplacedAntiUtils.getBareRef(self.health(), self.absorption(), self.totemConfirmed(), selfPopData.ticksSinceOwnPop(), selfHaz.maxSelfOpen(), ctx.selfPop);
      ctx.allowTotemTrade = carefulBalancedType == CarefulBalancedType.TERMINATOR
         ? self.totemConfirmed() && self.totems() >= 2
         : defensiveAggressive == DefensiveAggressiveType.AGGRESSIVE && self.totemConfirmed() && self.totems() >= 3;
      boolean anchorAntiFlag3 = selfPopData.enemiesWithin8() == 0 || self.hole() != null && self.hole().openSides() == 0 || threat.maxIncoming() < 4.0F;
      ctx.restockWindow = anchorAntiFlag3 && (carefulBalancedType == CarefulBalancedType.CAREFUL || target == null || target.distance() > 8.0);
      ctx.forceTimedType = ForceTimedType.TIMED;
      ctx.anchorRelease = selfPopData.targetReadyIn() <= 0 || ctx.selfPop.ref();
      ctx.dodge = false;
      boolean anchorAntiFlag4 = threat.etaTicks() <= 2 && threat.liveMax() >= Math.max(6.0F, 0.3F * self.getAbsorptionHealth()) && selfPopData.selfClosedFor() == 0;
      boolean anchorAntiFlag5 = himEtaData.canEtaHit(self.getAbsorptionHealth()) && selfPopData.selfClosedFor() == 0;
      boolean antiAnchorFlag = anchorAntiFlag4 || anchorAntiFlag5;
      boolean anchorAntiFlag6 = himEtaData.canChargesEta(self.getAbsorptionHealth()) && selfPopData.getOptions(SurroundKitType.GUARD).available();
      boolean defuseSpotFlag = anchorAntiFlag6 || isAnchorThreateningSelf(selfPopData);
      boolean anchorAntiFlag8 = target != null && isAnchorFinisherThreat(selfPopData, centerVecData);
      boolean anchorAntiFlag9 = anchorAntiFlag8 && canAnchorKillSelf(selfPopData);
      CaseMaxData options = selfPopData.getOptions(SurroundKitType.ANCHOR);
      SurroundKitType committed = selfPopData.committed();
      boolean anchorAntiFlag10 = committed != null && committed != SurroundKitType.NONE && selfPopData.getOptions(committed).available();
      if (target != null
         && !defuseSpotFlag
         && !anchorAntiFlag9
         && options.available()
         && "owed".equals(options.why())
         && !isCommittedLaneOverridingAnchor(defensiveAggressive, options.kills(), committed, anchorAntiFlag10, anchorAntiFlag10 && committed == SurroundKitType.EAT && AbsorptionFoodUtils.isSafeToEatByThreat(selfPopData))) {
         ctx.anchorRelease = true;
         return buildComboPlan(
            ctx,
            DefendEngageType.ANCHOR,
            SurroundKitType.ANCHOR,
            DanceChaseType.HOLD,
            getEngageStatusText(DefendEngageType.ANCHOR),
            centerVecData.isIdleState() ? centerVecData : null,
            centerVecData.isIdleState()
         );
      } else {
         SurroundKitType committed2 = selfPopData.committed();
         boolean anchorAntiFlag11 = committed2 != null && committed2 != SurroundKitType.NONE && selfPopData.getOptions(committed2).available();
         boolean anchorAntiFlag12 = anchorAntiFlag11 && canCounterWithLane(selfPopData, centerVecData, committed2, defensiveAggressive, anchorAntiFlag) && !wasAnchorJustPlaced(selfPopData, committed2, this.lastAnchorPlacedTick);
         if (anchorAntiFlag12 && committed2 == SurroundKitType.ANCHOR && "placed".equals(selfPopData.getOptions(committed2).why())) {
            this.lastAnchorPlacedTick = selfPopData.tick();
         }

         if (anchorAntiFlag11 && !anchorAntiFlag12) {
            boolean anchorAntiFlag13 = (committed2 == SurroundKitType.XP || committed2 == SurroundKitType.KIT || committed2 == SurroundKitType.SURROUND) && threat.lethal()
               || committed2 == SurroundKitType.EAT && !AbsorptionFoodUtils.isSafeToEatByThreat(selfPopData)
               || anchorAntiFlag6 && isPlacedOrUnplacedAnchorOrSupportLane(committed2, selfPopData.getOptions(committed2).why())
               || anchorAntiFlag8 && isUnplacedAnchorOrSupportLane(committed2, selfPopData.getOptions(committed2).why())
               || anchorAntiFlag5 && committed2 == SurroundKitType.ANCHOR && "unplaced".equals(selfPopData.getOptions(committed2).why());
            if (!anchorAntiFlag13) {
               if (committed2 == SurroundKitType.GUARD && canBlockGuard(selfPopData)) {
                  ctx.dodge = false;
                  return buildComboPlan(
                     ctx,
                     DefendEngageType.DEFEND,
                     SurroundKitType.GUARD,
                     DanceChaseType.STILL,
                     "Anti-anchor",
                     centerVecData.isIdleState() ? centerVecData : null,
                     centerVecData.isIdleState()
                  );
               }

               CenterVecData centerVecData3 = committed2 == SurroundKitType.BREACH ? centerVecData2 : centerVecData;
               CenterVecData centerVecData4 = canUseCenterForLane(centerVecData3, committed2, defensiveAggressive) ? centerVecData3 : null;
               DanceChaseType danceChaseType = centerVecData4 != null ? getDanceChaseForStand(centerVecData4) : getDanceChaseForLane(committed2, defensiveAggressive);
               DefendEngageType escapeIdle = getEngageTypeForLane(committed2, defensiveAggressive);
               String anchorAntiText = committed2 == SurroundKitType.GUARD
                  ? "Anti-anchor"
                  : (centerVecData4 != null && committed2 != SurroundKitType.BREACH ? getComboStatusText(centerVecData4) : getEngageStatusText(escapeIdle));
               ctx.dodge = antiAnchorFlag && (committed2 != SurroundKitType.ANCHOR || !"armed".equals(selfPopData.getOptions(committed2).why()));
               return buildComboPlan(ctx, escapeIdle, committed2, danceChaseType, anchorAntiText, centerVecData4, false);
            }
         }

         boolean anchorAntiFlag14 = cfg != null && cfg.eatGapples();
         boolean anchorAntiFlag15 = anchorAntiFlag14
            && AbsorptionFoodUtils.needsHealingOrFood(self, carefulBalancedType)
            && AbsorptionFoodUtils.isSafeToEatByThreatAndCombo(selfPopData)
            && selfPopData.getOptions(SurroundKitType.EAT).available();
         boolean anchorAntiFlag16 = anchorAntiFlag15 && (!hasTotemsWhileTargetEating(target, self) || !isNotCarefulOrDefensive(carefulBalancedType, defensiveAggressive));
         if (defensiveAggressive == DefensiveAggressiveType.ESCAPE) {
            if (anchorAntiFlag15) {
               return buildComboPlan(
                  ctx, DefendEngageType.HEAL, SurroundKitType.EAT, DanceChaseType.STILL, getEngageStatusText(DefendEngageType.HEAL), null, false
               );
            } else {
               SurroundKitType surroundKitType = cfg != null && cfg.escapePearls() && selfPopData.getOptions(SurroundKitType.PEARL).available()
                  ? SurroundKitType.PEARL
                  : (isCrystalDenyAvailable(selfPopData) ? SurroundKitType.CRYSTAL : SurroundKitType.NONE);
               return buildComboPlan(ctx, DefendEngageType.ESCAPE, surroundKitType, DanceChaseType.FLEE, getEngageStatusText(DefendEngageType.ESCAPE), null, false);
            }
         } else if (selfPopData.ticksSinceOwnPop() > 10
            || ctx.selfPop.on()
            || carefulBalancedType == CarefulBalancedType.TERMINATOR && self.totems() >= 1
            || centerVecData.state() == CenterVecData.State.FINISHER && centerVecData.killInTicks() <= 3) {
            if (anchorAntiFlag16) {
               return buildComboPlan(
                  ctx, DefendEngageType.HEAL, SurroundKitType.EAT, DanceChaseType.STILL, getEngageStatusText(DefendEngageType.HEAL), null, false
               );
            } else if (anchorAntiFlag14
               && AbsorptionFoodUtils.shouldEatGappleForHealth(self, carefulBalancedType)
               && defensiveAggressive == DefensiveAggressiveType.DEFENSIVE
               && carefulBalancedType != CarefulBalancedType.TERMINATOR
               && target != null
               && target.distance() < 24.0) {
               SurroundKitType surroundKitType2 = isCrystalDenyAvailable(selfPopData) ? SurroundKitType.CRYSTAL : SurroundKitType.NONE;
               DanceChaseType danceChaseType2 = target.distance() < 20.0 ? DanceChaseType.FLEE : DanceChaseType.STILL;
               return buildComboPlan(ctx, DefendEngageType.DEFEND, surroundKitType2, danceChaseType2, "Backing off", null, false);
            } else {
               ctx.dodge = antiAnchorFlag;
               CaseMaxData options2 = selfPopData.getOptions(SurroundKitType.GUARD);
               if (anchorAntiFlag8) {
                  ComboTradeData refKills = getKillComboIfAvailable(ctx, selfPopData, himEtaData, self);
                  if (refKills != null) {
                     return refKills;
                  } else {
                     ctx.dodge = false;
                     return buildComboPlan(
                        ctx,
                        DefendEngageType.DEFEND,
                        SurroundKitType.GUARD,
                        DanceChaseType.STILL,
                        "Anti-anchor",
                        centerVecData.isIdleState() ? centerVecData : null,
                        centerVecData.isIdleState()
                     );
                  }
               } else if (target != null && shouldUseGuardLane(selfPopData, centerVecData, options2, defuseSpotFlag)) {
                  ComboTradeData killComboIfAvailable = getKillComboIfAvailable(ctx, selfPopData, himEtaData, self);
                  return killComboIfAvailable != null
                     ? killComboIfAvailable
                     : buildComboPlan(ctx, DefendEngageType.DEFEND, SurroundKitType.GUARD, DanceChaseType.DANCE, "Anti-anchor", null, false);
               } else {
                  CaseMaxData options3 = selfPopData.getOptions(SurroundKitType.BREACH);
                  if (target != null && options3.available() && centerVecData2.isIdleState()) {
                     ComboTradeData killComboIfAvailable3 = getKillComboIfAvailable(ctx, selfPopData, himEtaData, self);
                     if (killComboIfAvailable3 != null) {
                        return killComboIfAvailable3;
                     } else if (isCampStatus(options3.why())) {
                        ctx.restockWindow = true;
                        if ("camp".equals(options3.why())) {
                           if (selfPopData.getOptions(SurroundKitType.XP).available()) {
                              return buildComboPlan(
                                 ctx,
                                 DefendEngageType.MEND,
                                 SurroundKitType.XP,
                                 DanceChaseType.STILL,
                                 getEngageStatusText(DefendEngageType.MEND),
                                 null,
                                 false
                              );
                           }

                           if (selfPopData.getOptions(SurroundKitType.KIT).available()) {
                              return buildComboPlan(
                                 ctx,
                                 DefendEngageType.REFILL,
                                 SurroundKitType.KIT,
                                 DanceChaseType.STILL,
                                 getEngageStatusText(DefendEngageType.REFILL),
                                 null,
                                 false
                              );
                           }
                        }

                        return buildBreachLanePlan(ctx, DefendEngageType.CAMP, centerVecData2);
                     } else {
                        return buildBreachLanePlan(ctx, DefendEngageType.BREACH, centerVecData2);
                     }
                  } else if (centerVecData.state() == CenterVecData.State.HIT_LOOP
                     && centerVecData.abort() == CenterVecData.Abort.NONE
                     && defensiveAggressive != DefensiveAggressiveType.DEFENSIVE
                     && defensiveAggressive != DefensiveAggressiveType.ESCAPE
                     && target != null) {
                     CaseMaxData caseMaxData = selfPopData.targetReadyIn() > 3 && !ctx.selfPop.ref() ? null : pickBestLaneOption(selfPopData, ctx.selfPop.ref());
                     return caseMaxData != null && caseMaxData.kills() ? buildKillComboPlan(ctx, selfPopData, caseMaxData) : buildDefendComboPlan(ctx, selfPopData, centerVecData, threat);
                  } else if (centerVecData.isIdleState()
                     && centerVecData.state() != CenterVecData.State.HIT_LOOP
                     && centerVecData.abort() == CenterVecData.Abort.NONE
                     && defensiveAggressive != DefensiveAggressiveType.DEFENSIVE) {
                     return buildDefendComboPlan(ctx, selfPopData, centerVecData, threat);
                  } else {
                     if (target != null && (target.awaitingTotem() || target.getHealthTrusted() <= 9.0F) && (selfPopData.targetReadyIn() <= 3 || ctx.selfPop.ref())) {
                        CaseMaxData meleePrep = pickBestLaneOption(selfPopData, ctx.selfPop.ref());
                        if (meleePrep != null) {
                           return buildKillComboPlan(ctx, selfPopData, meleePrep);
                        }
                     }

                     if (target == null) {
                        if (selfPopData.nearestEnemy() >= 12.0 && !selfPopData.pearlIncoming() && selfPopData.getOptions(SurroundKitType.XP).available()) {
                           return buildComboPlan(
                              ctx,
                              DefendEngageType.MEND,
                              SurroundKitType.XP,
                              DanceChaseType.STILL,
                              getEngageStatusText(DefendEngageType.MEND),
                              null,
                              false
                           );
                        } else if (ctx.restockWindow && selfPopData.getOptions(SurroundKitType.KIT).available()) {
                           return buildComboPlan(
                              ctx,
                              DefendEngageType.REFILL,
                              SurroundKitType.KIT,
                              DanceChaseType.STILL,
                              getEngageStatusText(DefendEngageType.REFILL),
                              null,
                              false
                           );
                        } else {
                           return cfg != null && cfg.protectLoot() && selfPopData.lootNearby()
                              ? buildComboPlan(
                                 ctx,
                                 DefendEngageType.LOOT,
                                 SurroundKitType.NONE,
                                 DanceChaseType.LOOT,
                                 getEngageStatusText(DefendEngageType.LOOT),
                                 null,
                                 false
                              )
                              : buildComboPlan(
                                 ctx,
                                 DefendEngageType.IDLE,
                                 SurroundKitType.NONE,
                                 DanceChaseType.STILL,
                                 getEngageStatusText(DefendEngageType.IDLE),
                                 null,
                                 false
                              );
                        }
                     } else if (centerVecData.proposal() == CenterVecData.Proposal.PURSUE) {
                        return buildComboPlan(ctx, DefendEngageType.COMBO, nullToNone(centerVecData.lane()), DanceChaseType.CHASE, getComboStatusText(centerVecData), centerVecData, false);
                     } else {
                        CaseMaxData caseMaxData2 = null;
                        double anchorAntiValue = Double.NEGATIVE_INFINITY;

                        for (SurroundKitType l : new SurroundKitType[]{SurroundKitType.CRYSTAL, SurroundKitType.ANCHOR, SurroundKitType.MELEE}) {
                           if (l != SurroundKitType.ANCHOR || isAnchorLaneEnabled(selfPopData)) {
                              CaseMaxData options4 = selfPopData.getOptions(l);
                              if (options4.available() && Double.isFinite(options4.value())) {
                                 double anchorAntiLimit = options4.value() + (l == selfPopData.lastLane() ? 1.0 : 0.0);
                                 if (anchorAntiLimit > anchorAntiValue) {
                                    anchorAntiValue = anchorAntiLimit;
                                    caseMaxData2 = options4;
                                 }
                              }
                           }
                        }

                        if ((centerVecData.proposal() == CenterVecData.Proposal.OPEN || centerVecData.proposal() == CenterVecData.Proposal.ANCHOR)
                           && centerVecData.proposalValue()
                              >= Math.max((double)selfHaz.minUseful(), PlaystyleTuning.ANCHOR_VALUE_SCALE_BY_STYLE[carefulBalancedType.ordinal()] * (caseMaxData2 == null ? 0.0 : caseMaxData2.value()))) {
                           return buildComboPlan(ctx, DefendEngageType.COMBO, nullToNone(centerVecData.lane()), getDanceChaseForStand(centerVecData), getComboStatusText(centerVecData), centerVecData, false);
                        } else if (target.distance() > 6.0) {
                           SurroundKitType surroundKitType4 = selfPopData.getOptions(SurroundKitType.CRYSTAL).available() ? SurroundKitType.CRYSTAL : SurroundKitType.NONE;
                           return buildComboPlan(
                              ctx, DefendEngageType.CHASE, surroundKitType4, DanceChaseType.CHASE, getEngageStatusText(DefendEngageType.CHASE), null, false
                           );
                        } else if (caseMaxData2 != null && caseMaxData2.value() >= ctx.minDamage * 0.5) {
                           DefendEngageType defendEngageType = caseMaxData2.lane() == SurroundKitType.CRYSTAL
                              ? (anchorAntiFlag2 ? DefendEngageType.FACE_PLACE : DefendEngageType.ENGAGE)
                              : getEngageTypeForLane(caseMaxData2.lane(), defensiveAggressive);
                           DanceChaseType danceChaseType3 = caseMaxData2.lane() != SurroundKitType.ANCHOR && caseMaxData2.lane() != SurroundKitType.MELEE
                              ? DanceChaseType.DANCE
                              : DanceChaseType.HOLD;
                           return buildComboPlan(ctx, defendEngageType, caseMaxData2.lane(), danceChaseType3, getEngageStatusText(defendEngageType), null, false);
                        } else if (carefulBalancedType == CarefulBalancedType.CAREFUL
                           && defensiveAggressive == DefensiveAggressiveType.DEFENSIVE
                           && selfPopData.getOptions(SurroundKitType.SURROUND).available()) {
                           return buildComboPlan(
                              ctx,
                              DefendEngageType.DEFEND,
                              SurroundKitType.SURROUND,
                              DanceChaseType.STILL,
                              getEngageStatusText(DefendEngageType.DEFEND),
                              null,
                              false
                           );
                        } else {
                           SurroundKitType surroundKitType5 = selfPopData.getOptions(SurroundKitType.CRYSTAL).available() ? SurroundKitType.CRYSTAL : SurroundKitType.NONE;
                           return buildComboPlan(
                              ctx, DefendEngageType.ENGAGE, surroundKitType5, DanceChaseType.DANCE, getEngageStatusText(DefendEngageType.ENGAGE), null, false
                           );
                        }
                     }
                  }
               }
            }
         } else {
            ctx.selfWeight = 2.0F;
            ctx.maxSelfDamage = Math.min(ctx.maxSelfDamage, 6.0F);
            SurroundKitType surroundKitType6 = selfPopData.getOptions(SurroundKitType.CRYSTAL).available() ? SurroundKitType.CRYSTAL : SurroundKitType.NONE;
            return buildComboPlan(ctx, DefendEngageType.RECOVER, surroundKitType6, DanceChaseType.COVER, getEngageStatusText(DefendEngageType.RECOVER), null, false);
         }
      }
   }

   static boolean isCommittedLaneOverridingAnchor(DefensiveAggressiveType defensiveAggressiveType, boolean eatEscapeFlag, SurroundKitType surroundKitType, boolean eatEscapeFlag2, boolean eatEscapeFlag3) {
      if (defensiveAggressiveType == DefensiveAggressiveType.ESCAPE && !eatEscapeFlag) {
         return true;
      } else {
         return !isLaneNotBreachOrPearl(surroundKitType) ? true : surroundKitType == SurroundKitType.EAT && eatEscapeFlag2 && eatEscapeFlag3;
      }
   }

   static DefendEngageType getEngageTypeForLane(SurroundKitType surroundKitType, DefensiveAggressiveType defensiveAggressiveType) {
      if (surroundKitType == null) {
         return DefendEngageType.IDLE;
      } else {
         return switch (surroundKitType) {
            case EAT -> DefendEngageType.HEAL;
            case XP -> DefendEngageType.MEND;
            case KIT -> DefendEngageType.REFILL;
            case SURROUND, GUARD -> DefendEngageType.DEFEND;
            case BREACH -> DefendEngageType.BREACH;
            case ANCHOR -> DefendEngageType.ANCHOR;
            case PEARL -> defensiveAggressiveType == DefensiveAggressiveType.ESCAPE ? DefendEngageType.ESCAPE : DefendEngageType.CHASE;
            case CRYSTAL -> DefendEngageType.ENGAGE;
            case MELEE -> DefendEngageType.MELEE;
            case NONE -> DefendEngageType.IDLE;
         };
      }
   }

   static boolean isNotCarefulOrDefensive(CarefulBalancedType carefulBalancedType, DefensiveAggressiveType defensiveAggressiveType) {
      return carefulBalancedType != CarefulBalancedType.CAREFUL && defensiveAggressiveType != DefensiveAggressiveType.DEFENSIVE && defensiveAggressiveType != DefensiveAggressiveType.ESCAPE;
   }

   private static void updateForceTimedFromCenter(CombatComboPlanner.Ctx ctx, CenterVecData centerVecData) {
      CenterVecData.CrystalStep crystal = centerVecData.crystal();
      ctx.forceTimedType = crystal == null
         ? ForceTimedType.TIMED
         : (crystal.breakNow() ? ForceTimedType.FORCE : (crystal.hold() ? ForceTimedType.HOLD : ForceTimedType.TIMED));
      if (centerVecData.anchor() != null) {
         ctx.anchorRelease = centerVecData.anchor().release();
      }
   }

   static boolean hasActiveAttackPlan(SelfPopData selfPopData, CenterVecData centerVecData) {
      return centerVecData != null
         && centerVecData.isLoopHit()
         && centerVecData.abort() == CenterVecData.Abort.NONE
         && (centerVecData.crystal() != null || centerVecData.anchor() != null || centerVecData.strike() != null && selfPopData.targetReadyIn() <= 0);
   }

   static boolean isPlacedOrUnplacedAnchorOrSupportLane(SurroundKitType surroundKitType, String unplacedText) {
      if (surroundKitType == null) {
         return false;
      } else {
         return switch (surroundKitType) {
            case EAT, XP, KIT, SURROUND, BREACH, PEARL -> true;
            case ANCHOR -> isAnchorPlacementStatus(unplacedText);
            default -> false;
         };
      }
   }

   private static ComboTradeData buildKillComboPlan(CombatComboPlanner.Ctx ctx, SelfPopData selfPopData, CaseMaxData caseMaxData) {
      ctx.forceTimedType = selfPopData.targetReadyIn() > 0 && !ctx.selfPop.ref() ? ForceTimedType.TIMED : ForceTimedType.FORCE;
      ctx.anchorRelease = selfPopData.targetReadyIn() <= 0 || ctx.selfPop.ref();
      DanceChaseType danceChaseType = caseMaxData.lane() != SurroundKitType.ANCHOR && caseMaxData.lane() != SurroundKitType.MELEE ? DanceChaseType.DANCE : DanceChaseType.HOLD;
      return buildComboPlan(ctx, DefendEngageType.KILL, caseMaxData.lane(), danceChaseType, getEngageStatusText(DefendEngageType.KILL), null, false);
   }

   private static boolean isCrystalDenyAvailable(SelfPopData selfPopData) {
      CaseMaxData options = selfPopData.getOptions(SurroundKitType.CRYSTAL);
      return options.available() && "deny".equals(options.why());
   }

   private static final class Ctx {
      RefBareData selfPop = RefBareData.INSTANCE;
      boolean dodge;
      final DefensiveAggressiveType aggressionPosture;
      boolean anchorRelease;
      float minDamage;
      final SelfHazData selfHazWeights;
      boolean restockWindow;
      float selfWeight;
      ForceTimedType forceTimedType;
      float maxSelfDamage;
      boolean allowTotemTrade;

      Ctx(DefensiveAggressiveType defensiveAggressiveType, SelfHazData data) {
         this.aggressionPosture = defensiveAggressiveType;
         this.selfHazWeights = data;
      }
   }
}
