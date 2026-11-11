package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.world.ForcedFaceData;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BonusLevelData;

public record TargetSelfDataTarget(
   LivingEntity target,
   BonusLevelData tuning,
   float maxSelfDamage,
   boolean allowTotemTrade,
   boolean autoObsidian,
   int predictTicks,
   Vec3d spread,
   BlockEntityUtils.Predicted self,
   PosBlockUtilsBonus.Window targetWindow,
   PosBlockUtilsBonus.Window selfWindow,
   float targetEhp,
   boolean targetTotem,
   Predicate<Vec3d> veto,
   ForcedFaceData directive
) {
   public TargetSelfDataTarget getSelfTarget(ForcedFaceData d) {
      return new TargetSelfDataTarget(
         this.target,
         this.tuning,
         this.maxSelfDamage,
         this.allowTotemTrade,
         this.autoObsidian,
         this.predictTicks,
         this.spread,
         this.self,
         this.targetWindow,
         this.selfWindow,
         this.targetEhp,
         this.targetTotem,
         this.veto,
         d
      );
   }

   public TargetSelfDataTarget(
      LivingEntity predictTicks,
      BonusLevelData targetEhp,
      float target,
      boolean targetWindow,
      boolean selfWindow,
      int autoObsidian,
      Vec3d self,
      BlockEntityUtils.Predicted tuning,
      PosBlockUtilsBonus.Window targetTotem,
      PosBlockUtilsBonus.Window allowTotemTrade,
      float spread,
      boolean maxSelfDamage,
      Predicate<Vec3d> veto
   ) {
      this(
         predictTicks, targetEhp, target, targetWindow, selfWindow, autoObsidian, self, tuning, targetTotem, allowTotemTrade, spread, maxSelfDamage, veto, null
      );
   }
}
