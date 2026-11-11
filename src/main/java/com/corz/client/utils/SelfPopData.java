package com.corz.client.utils;

import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.HimEtaData;
import java.util.Map;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.SurroundKitType;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.DefensiveAggressiveType;
import com.corz.client.utils.RefBareData;
import com.corz.client.utils.CarefulBalancedType;

public record SelfPopData(
   long tick,
   AbsorptionPosData self,
   VecBlockingData target,
   int enemiesWithin8,
   VecBlock.Threat threat,
   int targetReadyIn,
   int selfClosedFor,
   boolean nether,
   PearlsPlaystyleData cfg,
   Map<SurroundKitType, CaseMaxData> options,
   SurroundKitType committed,
   SurroundKitType lastLane,
   int ticksSinceOwnPop,
   int stallWindows,
   boolean lootNearby,
   double nearestEnemy,
   boolean pearlIncoming,
   DefensiveAggressiveType lastPosture,
   CenterVecData combo,
   float selfSpent,
   HimEtaData anchor,
   CenterVecData breach,
   boolean popPending
) {
   public RefBareData getSelfPop() {
      CarefulBalancedType carefulBalancedType = this.cfg != null && this.cfg.playstyle() != null ? this.cfg.playstyle() : CarefulBalancedType.TERMINATOR;
      if (this.self == null) {
         return RefBareData.INSTANCE;
      } else {
         return carefulBalancedType == CarefulBalancedType.TERMINATOR
            ? RefBareData.getInstance(
               this.self.totemConfirmed() && this.self.totems() >= 1 && !this.popPending && this.ticksSinceOwnPop > 2,
               this.self.totems(),
               this.self.getAbsorptionHealth(),
               this.isSelfTarget()
            )
            : RefBareData.getInstance(
               carefulBalancedType == CarefulBalancedType.TERMINATOR,
               this.self.totemConfirmed(),
               this.self.totems(),
               this.self.getAbsorptionHealth(),
               this.popPending || this.ticksSinceOwnPop <= 5,
               this.isSelfTarget()
            );
      }
   }

   public SelfPopData getSelfPop(CenterVecData c) {
      return new SelfPopData(
         this.tick,
         this.self,
         this.target,
         this.enemiesWithin8,
         this.threat,
         this.targetReadyIn,
         this.selfClosedFor,
         this.nether,
         this.cfg,
         this.options,
         this.committed,
         this.lastLane,
         this.ticksSinceOwnPop,
         this.stallWindows,
         this.lootNearby,
         this.nearestEnemy,
         this.pearlIncoming,
         this.lastPosture,
         c == null ? CenterVecData.INSTANCE : c,
         this.selfSpent,
         this.anchor,
         this.breach,
         this.popPending
      );
   }

   public static boolean canPops(AbsorptionPosData t, VecBlockingData self) {
      return t != null && self != null && t.pops() > self.pops();
   }

   public SelfPopData(
      long threat,
      AbsorptionPosData lastLane,
      VecBlockingData tick,
      int lastPosture,
      VecBlock.Threat cfg,
      int lootNearby,
      int nearestEnemy,
      boolean committed,
      PearlsPlaystyleData target,
      Map<SurroundKitType, CaseMaxData> pearlIncoming,
      SurroundKitType ticksSinceOwnPop,
      SurroundKitType self,
      int combo,
      int options,
      boolean selfClosedFor,
      double stallWindows,
      boolean targetReadyIn,
      DefensiveAggressiveType nether,
      CenterVecData enemiesWithin8
   ) {
      this(
         threat,
         lastLane,
         tick,
         lastPosture,
         cfg,
         lootNearby,
         nearestEnemy,
         committed,
         target,
         pearlIncoming,
         ticksSinceOwnPop,
         self,
         combo,
         options,
         selfClosedFor,
         stallWindows,
         targetReadyIn,
         nether,
         enemiesWithin8,
         0.0F
      );
   }

   public SelfPopData getSelfPopForCenterVecData(CenterVecData b) {
      return new SelfPopData(
         this.tick,
         this.self,
         this.target,
         this.enemiesWithin8,
         this.threat,
         this.targetReadyIn,
         this.selfClosedFor,
         this.nether,
         this.cfg,
         this.options,
         this.committed,
         this.lastLane,
         this.ticksSinceOwnPop,
         this.stallWindows,
         this.lootNearby,
         this.nearestEnemy,
         this.pearlIncoming,
         this.lastPosture,
         this.combo,
         this.selfSpent,
         this.anchor,
         b == null ? CenterVecData.INSTANCE : b,
         this.popPending
      );
   }

   public SelfPopData(
      long pearlIncoming,
      AbsorptionPosData options,
      VecBlockingData cfg,
      int nether,
      VecBlock.Threat self,
      int lastPosture,
      int selfClosedFor,
      boolean targetReadyIn,
      PearlsPlaystyleData lootNearby,
      Map<SurroundKitType, CaseMaxData> target,
      SurroundKitType tick,
      SurroundKitType committed,
      int enemiesWithin8,
      int stallWindows,
      boolean combo,
      double nearestEnemy,
      boolean ticksSinceOwnPop,
      DefensiveAggressiveType lastLane,
      CenterVecData threat,
      float selfSpent
   ) {
      this(
         pearlIncoming,
         options,
         cfg,
         nether,
         self,
         lastPosture,
         selfClosedFor,
         targetReadyIn,
         lootNearby,
         target,
         tick,
         committed,
         enemiesWithin8,
         stallWindows,
         combo,
         nearestEnemy,
         ticksSinceOwnPop,
         lastLane,
         threat,
         selfSpent,
         HimEtaData.INSTANCE,
         CenterVecData.INSTANCE
      );
   }

   public boolean isSelfTarget() {
      return canPops(this.self, this.target);
   }

   public SelfPopData(
      long lootNearby,
      AbsorptionPosData threat,
      VecBlockingData tick,
      int nether,
      VecBlock.Threat breach,
      int nearestEnemy,
      int lastPosture,
      boolean anchor,
      PearlsPlaystyleData self,
      Map<SurroundKitType, CaseMaxData> targetReadyIn,
      SurroundKitType target,
      SurroundKitType combo,
      int selfClosedFor,
      int stallWindows,
      boolean pearlIncoming,
      double options,
      boolean enemiesWithin8,
      DefensiveAggressiveType cfg,
      CenterVecData selfSpent,
      float lastLane,
      HimEtaData committed,
      CenterVecData ticksSinceOwnPop
   ) {
      this(
         lootNearby,
         threat,
         tick,
         nether,
         breach,
         nearestEnemy,
         lastPosture,
         anchor,
         self,
         targetReadyIn,
         target,
         combo,
         selfClosedFor,
         stallWindows,
         pearlIncoming,
         options,
         enemiesWithin8,
         cfg,
         selfSpent,
         lastLane,
         committed,
         ticksSinceOwnPop,
         false
      );
   }

   public CaseMaxData getOptions(SurroundKitType l) {
      CaseMaxData caseMaxData = this.options == null ? null : this.options.get(l);
      return caseMaxData != null ? caseMaxData : CaseMaxData.getCaseMaxData(l, "n/a");
   }

   public SelfPopData(
      long tick,
      AbsorptionPosData self,
      VecBlockingData target,
      int enemiesWithin8,
      VecBlock.Threat threat,
      int targetReadyIn,
      int selfClosedFor,
      boolean nether,
      PearlsPlaystyleData cfg,
      Map<SurroundKitType, CaseMaxData> options,
      SurroundKitType committed,
      SurroundKitType lastLane,
      int ticksSinceOwnPop,
      int stallWindows,
      boolean lootNearby,
      double nearestEnemy,
      boolean pearlIncoming,
      DefensiveAggressiveType lastPosture,
      CenterVecData combo,
      float selfSpent,
      HimEtaData anchor,
      CenterVecData breach,
      boolean popPending
   ) {
      anchor = anchor == null ? HimEtaData.INSTANCE : anchor;
      breach = breach == null ? CenterVecData.INSTANCE : breach;
      this.tick = tick;
      this.self = self;
      this.target = target;
      this.enemiesWithin8 = enemiesWithin8;
      this.threat = threat;
      this.targetReadyIn = targetReadyIn;
      this.selfClosedFor = selfClosedFor;
      this.nether = nether;
      this.cfg = cfg;
      this.options = options;
      this.committed = committed;
      this.lastLane = lastLane;
      this.ticksSinceOwnPop = ticksSinceOwnPop;
      this.stallWindows = stallWindows;
      this.lootNearby = lootNearby;
      this.nearestEnemy = nearestEnemy;
      this.pearlIncoming = pearlIncoming;
      this.lastPosture = lastPosture;
      this.combo = combo;
      this.selfSpent = selfSpent;
      this.anchor = anchor;
      this.breach = breach;
      this.popPending = popPending;
   }
}
