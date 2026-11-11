package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.OutboundPacketKind;

public final class AuditOwn {
   private boolean rotationChanged;
   private boolean interactedThisTick;
   private final int[] ownViolationCounts;
   private boolean flapOwn;
   private int interactCountThisTick;
   private float rotationBaselineYaw;
   private int flapRingIndex;
   private int movePacketCount;
   private boolean serverCorrectionPending;
   private final int[] flapHistory;
   private boolean useRotationOwn;
   private int slotChangeCount;
   private int flapCountInWindow;
   private boolean rotationInteractPending;
   private final int[] violationCounts = new int[AuditOwn.Rule.values().length];
   private boolean rotationPending;
   private boolean rotationBaselineSet;
   private int lastSlot;
   private float rotationBaselinePitch;
   private boolean serverCorrectionSeen;

   public void onRotationPacket(float rotationValue, float rotationValue2) {
      boolean flag = this.rotationBaselineSet && (Math.abs(rotationValue - this.rotationBaselineYaw) > 1.0E-4F || Math.abs(rotationValue2 - this.rotationBaselinePitch) > 1.0E-4F);
      if (this.rotationInteractPending) {
         this.rotationInteractPending = false;
         if (flag) {
            this.recordViolation(AuditOwn.Rule.USE_ROTATION, this.useRotationOwn);
         }
      } else {
         if (this.rotationPending && this.rotationChanged) {
            this.recordViolation(AuditOwn.Rule.USE_ROTATION, false);
         }

         this.rotationPending = true;
         this.rotationChanged = flag;
      }
   }

   public void endTick() {
      if (this.rotationPending) {
         if (this.rotationChanged) {
            this.recordViolation(AuditOwn.Rule.USE_ROTATION, false);
         }

         this.rotationPending = false;
      }

      this.resetTickState();
      this.flapRingIndex = (this.flapRingIndex + 1) % 20;
      this.flapCountInWindow = this.flapCountInWindow - this.flapHistory[this.flapRingIndex];
      this.flapHistory[this.flapRingIndex] = 0;
   }

   public int getOwnViolationTotal() {
      int auditOwnTotal = 0;

      for (int auditOwnValue : this.ownViolationCounts) {
         auditOwnTotal += auditOwnValue;
      }

      return auditOwnTotal;
   }

   private void resetTickState() {
      this.interactCountThisTick = 0;
      this.interactedThisTick = false;
      this.slotChangeCount = 0;
      this.movePacketCount = 0;
      this.serverCorrectionPending = false;
      this.serverCorrectionSeen = false;
      this.rotationPending = false;
      this.rotationChanged = false;
      this.rotationInteractPending = false;
      this.useRotationOwn = false;
   }

   public void checkReach(double reachLimit, double reachValue, boolean reachFlag) {
      if (Double.isNaN(reachLimit) || reachLimit > reachValue) {
         this.recordViolation(AuditOwn.Rule.REACH, reachFlag);
      }
   }

   public void onMovePacket(boolean auditOwnFlag, float amount, float planValuesItem, float auditOwnValue, boolean auditOwnFlag2) {
      if (this.serverCorrectionPending) {
         this.serverCorrectionPending = false;
      } else {
         this.movePacketCount++;
         if (this.movePacketCount > 1) {
            this.recordViolation(AuditOwn.Rule.DOUBLE_MOVE, false);
         }
      }

      if (auditOwnFlag) {
         if (Math.abs(planValuesItem) > 90.0F) {
            this.recordViolation(AuditOwn.Rule.PITCH_RANGE, auditOwnFlag2);
         }

         if (this.rotationBaselineSet) {
            float auditOwnValue2 = amount - this.rotationBaselineYaw;
            float auditOwnValue3 = planValuesItem - this.rotationBaselinePitch;
            if (Math.abs(auditOwnValue2) >= 180.0F && !this.serverCorrectionSeen) {
               this.recordViolation(AuditOwn.Rule.WRAP, auditOwnFlag2);
            }

            boolean auditOwnFlag3 = Math.abs(planValuesItem) >= 89.9999F || Math.abs(this.rotationBaselinePitch) >= 89.9999F;
            if (auditOwnFlag2 && !this.serverCorrectionSeen && !auditOwnFlag3 && (!isMultipleOfStep(auditOwnValue2, auditOwnValue) || !isMultipleOfStep(auditOwnValue3, auditOwnValue))) {
               this.recordViolation(AuditOwn.Rule.GCD, true);
            }
         }

         this.rotationBaselineSet = true;
         this.rotationBaselineYaw = amount;
         this.rotationBaselinePitch = planValuesItem;
      }
   }

   public void resetAll() {
      Arrays.fill(this.violationCounts, 0);
      Arrays.fill(this.ownViolationCounts, 0);
      Arrays.fill(this.flapHistory, 0);
      this.flapRingIndex = 0;
      this.flapCountInWindow = 0;
      this.resetTickState();
      this.lastSlot = -1;
      this.rotationBaselineSet = false;
      this.rotationBaselineYaw = 0.0F;
      this.rotationBaselinePitch = 0.0F;
      this.flapOwn = false;
   }

   public int getViolationCount(AuditOwn.Rule rule) {
      return rule == null ? 0 : this.violationCounts[rule.ordinal()];
   }

   private void recordViolation(AuditOwn.Rule rule, boolean auditOwnFlag) {
      this.violationCounts[rule.ordinal()]++;
      if (auditOwnFlag) {
         this.ownViolationCounts[rule.ordinal()]++;
      }
   }

   public AuditOwn() {
      this.ownViolationCounts = new int[AuditOwn.Rule.values().length];
      this.flapHistory = new int[20];
      this.lastSlot = -1;
   }

   public void onKeyFlap(boolean flag) {
      this.flapOwn = flag;
      this.flapHistory[this.flapRingIndex]++;
      this.flapCountInWindow++;
      if (this.flapCountInWindow > 16) {
         this.recordViolation(AuditOwn.Rule.KEY_FLAP, flag);
      }
   }

   public static boolean isMultipleOfStep(float auditOwnValue, float auditOwnLimit) {
      if (!(auditOwnLimit > 0.0F) || !Float.isFinite(auditOwnLimit)) {
         return true;
      } else if (!Float.isFinite(auditOwnValue)) {
         return false;
      } else {
         double auditOwnValue2 = (double)auditOwnValue / auditOwnLimit;
         return Math.abs(auditOwnValue2 - Math.rint(auditOwnValue2)) <= 0.02F;
      }
   }

   public int getViolationTotal() {
      int auditOwnTotal = 0;

      for (int auditOwnValue : this.violationCounts) {
         auditOwnTotal += auditOwnValue;
      }

      return auditOwnTotal;
   }

   public void resetLastSlot() {
      this.lastSlot = -1;
   }

   public void onInteractAction(OutboundPacketKind sprintInteractType, boolean flag, boolean interactFlag) {
      if (sprintInteractType != null && sprintInteractType.isInteractionPacket()) {
         if (this.interactCountThisTick > 0) {
            this.recordViolation(AuditOwn.Rule.DOUBLE_ACTION, flag);
         }

         if (sprintInteractType == OutboundPacketKind.ATTACK && this.interactedThisTick) {
            this.recordViolation(AuditOwn.Rule.ATTACK_AFTER_USE, flag);
         }

         if (interactFlag) {
            this.recordViolation(AuditOwn.Rule.ACTION_IN_SCREEN, flag);
         }

         this.interactCountThisTick++;
         if (sprintInteractType == OutboundPacketKind.INTERACT_BLOCK || sprintInteractType == OutboundPacketKind.INTERACT_ENTITY || sprintInteractType == OutboundPacketKind.USE_ITEM) {
            this.interactedThisTick = true;
         }

         if (sprintInteractType == OutboundPacketKind.USE_ITEM) {
            if (this.rotationPending) {
               if (this.rotationChanged) {
                  this.recordViolation(AuditOwn.Rule.USE_ROTATION, flag);
               }

               this.rotationPending = false;
            } else {
               this.rotationInteractPending = true;
               this.useRotationOwn = flag;
            }
         }
      }
   }

   public void checkSprintWithoutForward(boolean forwardSprintFlag) {
      if (!forwardSprintFlag) {
         this.recordViolation(AuditOwn.Rule.SPRINT_WITHOUT_FORWARD, this.flapOwn);
      }
   }

   public String formatSummary() {
      StringBuilder stringBuilder = new StringBuilder(160);
      stringBuilder.append("audit=").append(this.getViolationTotal()).append(" own=").append(this.getOwnViolationTotal());

      for (AuditOwn.Rule rule : AuditOwn.Rule.values()) {
         stringBuilder.append(' ').append(rule.shortLabel).append('=').append(this.violationCounts[rule.ordinal()]);
      }

      return stringBuilder.toString();
   }

   public void onServerCorrection() {
      this.serverCorrectionPending = true;
      this.serverCorrectionSeen = true;
   }

   public void recordSlotChange(int value, boolean slotDuplicateFlag) {
      this.slotChangeCount++;
      if (this.slotChangeCount > 1) {
         this.recordViolation(AuditOwn.Rule.DOUBLE_SLOT, slotDuplicateFlag);
      }

      if (this.interactCountThisTick > 0) {
         this.recordViolation(AuditOwn.Rule.SLOT_AFTER_ACTION, slotDuplicateFlag);
      }

      if (value == this.lastSlot) {
         this.recordViolation(AuditOwn.Rule.DUPLICATE_SLOT, slotDuplicateFlag);
      }

      this.lastSlot = value;
   }

   public static enum Rule {
      DOUBLE_ACTION("d_act"),
      ATTACK_AFTER_USE("atk_use"),
      DOUBLE_MOVE("mv2"),
      DOUBLE_SLOT("slot2"),
      SLOT_AFTER_ACTION("slot_act"),
      DUPLICATE_SLOT("dup_slot"),
      GCD("gcd"),
      WRAP("wrap"),
      PITCH_RANGE("pitch"),
      USE_ROTATION("use_rot"),
      KEY_FLAP("flap"),
      ACTION_IN_SCREEN("screen"),
      SPRINT_WITHOUT_FORWARD("spr"),
      REACH("reach");
      private final String shortLabel;

      private Rule(String text) {
         this.shortLabel = text;
      }
   }
}
