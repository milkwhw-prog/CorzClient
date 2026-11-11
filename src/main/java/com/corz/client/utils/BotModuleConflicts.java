package com.corz.client.utils;

import java.util.Set;

public final class BotModuleConflicts {
   public static final Set<String> BLOCKING_MODULES = Set.of(
      "Criticals",
      "KeepSprint",
      "CrystalOptimizer",
      "AutoTotem",
      "AutoPot",
      "AutoTrap",
      "AutoAggroPearl",
      "Blink",
      "FakeLag",
      "BackTrack",
      "KnockbackDelay",
      "SpeedHack",
      "Flight",
      "Scaffold",
      "Fastbreak",
      "Reach",
      "HitBoxes",
      "AutoJumpReset"
   );
   public static final Set<String> YIELDING_MODULES = Set.of("AutoXP", "KeyPearl", "PearlCatchAssist", "PerfectWindCharge", "AutoSpear");
   public static final Set<String> REFUSING_MODULES = Set.of("AutoMine");

   public static BotModuleConflicts.ModuleConflictKind classifyModule(String refuseAutoText, boolean refuseAutoFlag) {
      if (refuseAutoFlag) {
         return BotModuleConflicts.ModuleConflictKind.REFUSE;
      } else if (refuseAutoText == null) {
         return BotModuleConflicts.ModuleConflictKind.NONE;
      } else if (REFUSING_MODULES.contains(refuseAutoText)) {
         return BotModuleConflicts.ModuleConflictKind.REFUSE;
      } else if (BLOCKING_MODULES.contains(refuseAutoText)) {
         return BotModuleConflicts.ModuleConflictKind.BLOCK;
      } else {
         return YIELDING_MODULES.contains(refuseAutoText) ? BotModuleConflicts.ModuleConflictKind.YIELD : BotModuleConflicts.ModuleConflictKind.NONE;
      }
   }

   private BotModuleConflicts() {
   }

   public static enum ModuleConflictKind {
      NONE,
      BLOCK,
      YIELD,
      REFUSE;
   }
}
