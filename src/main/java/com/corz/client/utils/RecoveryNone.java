package com.corz.client.utils;

import java.util.EnumSet;
import java.util.Set;

public final class RecoveryNone {
   private boolean recoveryActive;
   private RecoveryNone.Owner jumpRecoveryType;
   private boolean noneActive;
   private int noneTickCount;
   private final Set<RecoveryNone.Veto> noneOf = EnumSet.noneOf(RecoveryNone.Veto.class);
   private boolean recoveryNoneActive;
   private boolean cachedNoneActive;

   public static boolean canFlag(boolean recoveryNoneFlag, boolean recoveryNoneFlag2, boolean recoveryNoneFlag3) {
      return recoveryNoneFlag || recoveryNoneFlag2 || recoveryNoneFlag3;
   }

   public void updateFlagRecovery() {
      this.jumpRecoveryType = null;
      this.recoveryActive = false;
      this.recoveryNoneActive = false;
      this.noneOf.clear();
      this.noneActive = false;
      this.cachedNoneActive = false;
      this.noneTickCount = 0;
   }

   public boolean isContains(RecoveryNone.Veto veto) {
      return this.noneOf.contains(veto);
   }

   public RecoveryNone.Owner getJumpRecoveryType() {
      return this.jumpRecoveryType;
   }

   public boolean isFlagNone() {
      if (!this.noneActive) {
         this.noneActive = true;
         this.cachedNoneActive = this.recoveryActive && (this.recoveryNoneActive || this.noneOf.isEmpty());
      }

      return this.cachedNoneActive;
   }

   public boolean isFlag() {
      return this.recoveryActive;
   }

   public void handleNoneFlag(RecoveryNone.Veto veto) {
      if (this.noneActive) {
         this.noneTickCount++;
      } else {
         this.noneOf.add(veto);
      }
   }

   public void handleFlagRecovery(RecoveryNone.Owner jumpRecoveryType, boolean flag) {
      if (this.noneActive) {
         this.noneTickCount++;
      } else {
         if (!this.recoveryActive || flag && !this.recoveryNoneActive) {
            this.jumpRecoveryType = jumpRecoveryType;
            this.recoveryNoneActive = flag;
         }

         this.recoveryActive = true;
      }
   }

   public boolean isCachedNoneActive() {
      return this.cachedNoneActive;
   }

   public boolean isFlagNoneNoneActive() {
      return this.noneActive && this.recoveryActive && !this.recoveryNoneActive && !this.noneOf.isEmpty();
   }

   public static RecoveryNone.Owner getScaffoldGap(boolean scaffoldGapFlag, boolean scaffoldGapFlag2, boolean scaffoldGapFlag3) {
      if (scaffoldGapFlag) {
         return RecoveryNone.Owner.GAP_JUMP;
      } else if (scaffoldGapFlag2) {
         return RecoveryNone.Owner.SCAFFOLD;
      } else {
         return scaffoldGapFlag3 ? RecoveryNone.Owner.RECOVERY : RecoveryNone.Owner.ROUTE;
      }
   }

   public boolean isRecoveryNoneActive() {
      return this.recoveryNoneActive;
   }

   public int getValue() {
      return this.noneTickCount;
   }

   public boolean isNoneActive() {
      return this.noneActive;
   }

   public boolean isAboveHead() {
      boolean flagNone = this.isFlagNone();
      return flagNone && !this.isContains(RecoveryNone.Veto.LAVA_ABOVE_HEAD);
   }

   public static enum Owner {
      ROUTE,
      RECOVERY,
      GAP_JUMP,
      PILLAR,
      SCAFFOLD,
      JUMP_PLACE,
      ACCESS;
   }

   public static enum Veto {
      JUMP_LOOP_GUARD,
      LAVA_ABOVE_HEAD;
   }
}
