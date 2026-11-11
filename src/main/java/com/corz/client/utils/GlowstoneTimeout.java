package com.corz.client.utils;

import java.util.ArrayList;

public final class GlowstoneTimeout {
   public static final int[][] blockPosInts = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
   private GlowstoneTimeout.State protectionDetonateType = GlowstoneTimeout.State.IDLE;
   private int glowstoneRefusedCount;
   private static final double[] glowstoneTimeoutTable = new double[]{0.5, 0.5, 0.5};
   public static final int UNUSED_ONE = 1;
   private int aimTimeoutCount;
   private int timeoutAimValue;
   private String timeoutAimText = "";
   private int aimAttemptCount;
   private boolean aimTimeoutFlag;
   private boolean glowstoneReplaceableFlag;
   private static final double[][] glowstoneTimeoutDoubles = new double[][]{
      {0.0, -1.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, -1.0}, {0.0, 0.0, 1.0}, {-1.0, 0.0, 0.0}, {1.0, 0.0, 0.0}
   };

   static double[][] getArray(double[] arrayValues) {
      double arrayValue = arrayValues[3] - arrayValues[0];
      double arrayValue2 = arrayValues[4] - arrayValues[1];
      double arrayValue3 = arrayValues[5] - arrayValues[2];
      if (!(arrayValue <= 0.0) && !(arrayValue2 <= 0.0) && !(arrayValue3 <= 0.0)) {
         double arrayDivisor = 1.0 / (arrayValue * 2.0 + 1.0);
         double arrayValue4 = 1.0 / (arrayValue2 * 2.0 + 1.0);
         double arrayDivisor2 = 1.0 / (arrayValue3 * 2.0 + 1.0);
         double arrayValue5 = (1.0 - Math.floor(1.0 / arrayDivisor) * arrayDivisor) / 2.0;
         double arrayValue6 = (1.0 - Math.floor(1.0 / arrayDivisor2) * arrayDivisor2) / 2.0;
         var arrayList = new ArrayList<double[]>();

         for (double arrayTotal = 0.0; arrayTotal <= 1.0; arrayTotal += arrayDivisor) {
            for (double arrayTotal2 = 0.0; arrayTotal2 <= 1.0; arrayTotal2 += arrayValue4) {
               for (double arrayTotal3 = 0.0; arrayTotal3 <= 1.0; arrayTotal3 += arrayDivisor2) {
                  arrayList.add(
                     new double[]{arrayValues[0] + (arrayValues[3] - arrayValues[0]) * arrayTotal + arrayValue5, arrayValues[1] + (arrayValues[4] - arrayValues[1]) * arrayTotal2, arrayValues[2] + (arrayValues[5] - arrayValues[2]) * arrayTotal3 + arrayValue6}
                  );
               }
            }
         }

         return arrayList.toArray(new double[0][]);
      } else {
         return new double[0][];
      }
   }

   public static boolean canAmounts(double[] glowstoneTimeoutValues, int index, double glowstoneTimeoutFactor) {
      if (index >= 0 && index < 6) {
         double[] glowstoneTimeoutValues2 = glowstoneTimeoutDoubles[index];
         double glowstoneTimeoutFactor2 = glowstoneTimeoutValues[0] - (0.5 + 0.5 * glowstoneTimeoutValues2[0]);
         double glowstoneTimeoutFactor3 = glowstoneTimeoutValues[1] - (0.5 + 0.5 * glowstoneTimeoutValues2[1]);
         double glowstoneTimeoutFactor4 = glowstoneTimeoutValues[2] - (0.5 + 0.5 * glowstoneTimeoutValues2[2]);
         double glowstoneTimeoutValue = glowstoneTimeoutFactor2 * glowstoneTimeoutValues2[0] + glowstoneTimeoutFactor3 * glowstoneTimeoutValues2[1] + glowstoneTimeoutFactor4 * glowstoneTimeoutValues2[2];
         return glowstoneTimeoutValue < 0.1 ? false : glowstoneTimeoutFactor2 * glowstoneTimeoutFactor2 + glowstoneTimeoutFactor3 * glowstoneTimeoutFactor3 + glowstoneTimeoutFactor4 * glowstoneTimeoutFactor4 <= glowstoneTimeoutFactor * glowstoneTimeoutFactor;
      } else {
         return false;
      }
   }

   static boolean canFlag(double[] glowstoneTimeoutValues, double[] glowstoneTimeoutValues2, int glowstoneTimeoutValue, int glowstoneTimeoutValue2, int glowstoneTimeoutValue3) {
      double glowstoneTimeoutBound = 0.0;
      double hypot = 1.0;
      int[] ints = new int[]{glowstoneTimeoutValue, glowstoneTimeoutValue2, glowstoneTimeoutValue3};

      for (int i = 0; i < 3; i++) {
         double glowstoneTimeoutLimit = glowstoneTimeoutValues[i];
         double glowstoneTimeoutDivisor = glowstoneTimeoutValues2[i] - glowstoneTimeoutValues[i];
         double glowstoneTimeoutLimit2 = ints[i];
         double glowstoneTimeoutValue4 = ints[i] + 1.0;
         if (Math.abs(glowstoneTimeoutDivisor) < 1.0E-12) {
            if (glowstoneTimeoutLimit <= glowstoneTimeoutLimit2 || glowstoneTimeoutLimit >= glowstoneTimeoutValue4) {
               return false;
            }
         } else {
            double apiPutBound = (glowstoneTimeoutLimit2 - glowstoneTimeoutLimit) / glowstoneTimeoutDivisor;
            double goalBound = (glowstoneTimeoutValue4 - glowstoneTimeoutLimit) / glowstoneTimeoutDivisor;
            if (apiPutBound > goalBound) {
               double apiPutBound5 = apiPutBound;
               apiPutBound = goalBound;
               goalBound = apiPutBound5;
            }

            glowstoneTimeoutBound = Math.max(glowstoneTimeoutBound, apiPutBound);
            hypot = Math.min(hypot, goalBound);
            if (glowstoneTimeoutBound >= hypot) {
               return false;
            }
         }
      }

      return hypot - glowstoneTimeoutBound > 1.0E-7;
   }

   private GlowstoneTimeout.Action getTimeoutAim() {
      boolean timeoutAimFlag = this.glowstoneReplaceableFlag && !this.aimTimeoutFlag;
      this.aimTimeoutFlag = this.aimTimeoutFlag || timeoutAimFlag;
      this.protectionDetonateType = GlowstoneTimeout.State.DONE;
      this.timeoutAimValue = 0;
      this.aimAttemptCount++;
      return timeoutAimFlag ? GlowstoneTimeout.Action.RESTORE_SLOT : GlowstoneTimeout.Action.NONE;
   }

   public GlowstoneTimeout.Action getGlowstoneTimeout(GlowstoneTimeout.Signals signals) {
      if (signals.glowstoneDetonateActive) {
         if (canProtectionConfirm(this.protectionDetonateType) && !signals.detonateCharges) {
            return this.getTimeoutAim("ANCHOR_INVALID");
         } else {
            switch (this.protectionDetonateType) {
               case IDLE:
                  if (!signals.detonateCharges) {
                     return GlowstoneTimeout.Action.NONE;
                  } else if (signals.invalidAnchorFlag) {
                     if (!signals.signalsActive) {
                        return this.getTimeoutAim();
                     }

                     this.glowstoneReplaceableFlag = true;
                     this.handleProtectionDetonate(GlowstoneTimeout.State.DETONATE);
                     return GlowstoneTimeout.Action.SELECT_DETONATE_SLOT;
                  } else if (!signals.glowstoneReplaceableFlag) {
                     return this.getTimeoutAim("NO_GLOWSTONE");
                  } else if (!signals.replaceableGlowstoneFlag) {
                     return this.getTimeoutAim("TARGET_NOT_REPLACEABLE");
                  } else {
                     if (!signals.replaceableFaceFlag) {
                        return this.getTimeoutAim("NO_SUPPORT_FACE");
                     }

                     this.glowstoneReplaceableFlag = true;
                     this.handleProtectionDetonate(GlowstoneTimeout.State.PLACE_PROTECTION);
                     return GlowstoneTimeout.Action.SELECT_GLOWSTONE_SLOT;
                  }
               case PLACE_PROTECTION:
                  if (signals.glowstoneTimeoutActive) {
                     if (!signals.signalsActive) {
                        return this.getTimeoutAim();
                     }

                     this.handleProtectionDetonate(GlowstoneTimeout.State.DETONATE);
                     return GlowstoneTimeout.Action.SELECT_DETONATE_SLOT;
                  } else if (!signals.glowstoneReplaceableFlag) {
                     return this.getTimeoutAim("NO_GLOWSTONE");
                  } else if (!signals.replaceableGlowstoneFlag) {
                     return this.getTimeoutAim("TARGET_NOT_REPLACEABLE");
                  } else if (!signals.replaceableFaceFlag) {
                     return this.getTimeoutAim("NO_SUPPORT_FACE");
                  } else {
                     if (!signals.faceSupportFlag) {
                        if (++this.timeoutAimValue > 12) {
                           return this.getTimeoutAim("AIM_TIMEOUT");
                        }

                        return GlowstoneTimeout.Action.SELECT_GLOWSTONE_SLOT;
                     }

                     this.aimTimeoutCount++;
                     this.handleProtectionDetonate(GlowstoneTimeout.State.WAIT_CONFIRM);
                     return GlowstoneTimeout.Action.PLACE_GLOWSTONE;
                  }
               case WAIT_CONFIRM:
                  if (signals.glowstoneTimeoutActive) {
                     if (!signals.signalsActive) {
                        return this.getTimeoutAim();
                     }

                     this.handleProtectionDetonate(GlowstoneTimeout.State.DETONATE);
                     return GlowstoneTimeout.Action.SELECT_DETONATE_SLOT;
                  } else if (!signals.glowstoneRefusedFlag && ++this.timeoutAimValue < signals.timeoutLimit) {
                     return GlowstoneTimeout.Action.NONE;
                  } else {
                     if (this.glowstoneRefusedCount < 1 && signals.glowstoneReplaceableFlag && signals.replaceableGlowstoneFlag && signals.replaceableFaceFlag) {
                        this.glowstoneRefusedCount++;
                        this.handleProtectionDetonate(GlowstoneTimeout.State.PLACE_PROTECTION);
                        return GlowstoneTimeout.Action.NONE;
                     }

                     return this.getTimeoutAim(signals.glowstoneRefusedFlag ? "GLOWSTONE_REFUSED" : "CONFIRMATION_TIMEOUT");
                  }
               case DETONATE:
                  if (!signals.timeoutConfirmationFlag) {
                     if (!signals.detonateCharges) {
                        return this.getTimeoutAim("ANCHOR_INVALID");
                     }

                     if (++this.timeoutAimValue > 12) {
                        return this.getTimeoutAim("AIM_TIMEOUT");
                     }

                     return GlowstoneTimeout.Action.SELECT_DETONATE_SLOT;
                  }

                  this.handleProtectionDetonate(GlowstoneTimeout.State.RESTORE);
                  return GlowstoneTimeout.Action.DETONATE;
               case RESTORE:
                  this.handleProtectionDetonate(GlowstoneTimeout.State.DONE);
                  if (this.glowstoneReplaceableFlag && !this.aimTimeoutFlag) {
                     this.aimTimeoutFlag = true;
                     return GlowstoneTimeout.Action.RESTORE_SLOT;
                  }

                  return GlowstoneTimeout.Action.NONE;
               case DONE:
                  if (this.glowstoneReplaceableFlag && !this.aimTimeoutFlag) {
                     this.aimTimeoutFlag = true;
                     return GlowstoneTimeout.Action.RESTORE_SLOT;
                  }

                  return GlowstoneTimeout.Action.NONE;
               case FAILED:
               default:
                  return GlowstoneTimeout.Action.NONE;
            }
         }
      } else {
         boolean glowstoneTimeoutFlag = this.glowstoneReplaceableFlag && !this.aimTimeoutFlag && this.protectionDetonateType != GlowstoneTimeout.State.IDLE;
         this.updateTimeoutAim();
         return glowstoneTimeoutFlag ? GlowstoneTimeout.Action.RESTORE_SLOT : GlowstoneTimeout.Action.NONE;
      }
   }

   public int getAimTimeoutCount() {
      return this.aimTimeoutCount;
   }

   public static int getValue(boolean[] glowstoneTimeoutFlags) {
      int glowstoneTimeoutLimit = Math.min(9, glowstoneTimeoutFlags.length);

      for (int i = 0; i < glowstoneTimeoutLimit; i++) {
         if (glowstoneTimeoutFlags[i]) {
            return i;
         }
      }

      return -1;
   }

   public static GlowstoneTimeout.CoverPlan getAmounts(double[] glowstoneTimeoutValues, boolean[] glowstoneTimeoutFlags, boolean[] glowstoneTimeoutFlags2) {
      int glowstoneTimeoutSize = blockPosInts.length;
      int[] ints = new int[glowstoneTimeoutSize];
      int glowstoneTimeoutCounter = 0;
      int glowstoneTimeoutCounter2 = 0;
      boolean[] booleans = new boolean[glowstoneTimeoutSize];

      for (double[] glowstoneTimeoutValues2 : getArray(glowstoneTimeoutValues)) {
         boolean glowstoneTimeoutFlag = false;
         boolean glowstoneTimeoutFlag2 = false;

         for (int i = 0; i < glowstoneTimeoutSize; i++) {
            booleans[i] = canFlag(glowstoneTimeoutValues2, glowstoneTimeoutTable, blockPosInts[i][0], 0, blockPosInts[i][1]);
            glowstoneTimeoutFlag |= booleans[i];
            glowstoneTimeoutFlag2 |= booleans[i] && glowstoneTimeoutFlags[i];
         }

         if (glowstoneTimeoutFlag) {
            glowstoneTimeoutCounter++;
            if (glowstoneTimeoutFlag2) {
               glowstoneTimeoutCounter2++;
            } else {
               for (int j = 0; j < glowstoneTimeoutSize; j++) {
                  if (booleans[j] && glowstoneTimeoutFlags2[j] && !glowstoneTimeoutFlags[j]) {
                     ints[j]++;
                  }
               }
            }
         }
      }

      int index = -1;

      for (int k = 0; k < glowstoneTimeoutSize; k++) {
         if (ints[k] > 0 && (index == -1 || ints[k] > ints[index])) {
            index = k;
         }
      }

      return new GlowstoneTimeout.CoverPlan(index, index == -1 ? 0 : ints[index], glowstoneTimeoutCounter, glowstoneTimeoutCounter2);
   }

   public static int getAmounts(double[] glowstoneTimeoutValues, double[] glowstoneTimeoutValues2, double glowstoneTimeoutValue) {
      int glowstoneTimeoutValue2 = -1;
      double glowstoneTimeoutLimit3 = -2.0;

      for (int i = 0; i < 6; i++) {
         if (canAmounts(glowstoneTimeoutValues, i, glowstoneTimeoutValue)) {
            double glowstoneTimeoutLimit = getAmount(glowstoneTimeoutValues, glowstoneTimeoutValues2, glowstoneTimeoutDoubles[i]);
            if (glowstoneTimeoutLimit > glowstoneTimeoutLimit3) {
               glowstoneTimeoutLimit3 = glowstoneTimeoutLimit;
               glowstoneTimeoutValue2 = i;
            }
         }
      }

      return glowstoneTimeoutValue2;
   }

   public GlowstoneTimeout.State getProtectionDetonateType() {
      return this.protectionDetonateType;
   }

   public static int getValue(int glowstoneTimeoutValue) {
      switch (glowstoneTimeoutValue) {
         case 0:
            return 1;
         case 1:
            return 0;
         case 2:
            return 3;
         case 3:
            return 2;
         case 4:
            return 5;
         case 5:
            return 4;
         default:
            return glowstoneTimeoutValue;
      }
   }

   public String getText() {
      return this.timeoutAimText;
   }

   public int getGlowstoneRefusedCount() {
      return this.glowstoneRefusedCount;
   }

   private static double getAmount(double[] glowstoneTimeoutValues, double[] glowstoneTimeoutValues2, double[] glowstoneTimeoutValues3) {
      double glowstoneTimeoutFactor = 0.5 + 0.5 * glowstoneTimeoutValues3[0] - glowstoneTimeoutValues[0];
      double glowstoneTimeoutFactor2 = 0.5 + 0.5 * glowstoneTimeoutValues3[1] - glowstoneTimeoutValues[1];
      double glowstoneTimeoutFactor3 = 0.5 + 0.5 * glowstoneTimeoutValues3[2] - glowstoneTimeoutValues[2];
      double distance = Math.sqrt(glowstoneTimeoutFactor * glowstoneTimeoutFactor + glowstoneTimeoutFactor2 * glowstoneTimeoutFactor2 + glowstoneTimeoutFactor3 * glowstoneTimeoutFactor3);
      double sqrt2 = Math.sqrt(glowstoneTimeoutValues2[0] * glowstoneTimeoutValues2[0] + glowstoneTimeoutValues2[1] * glowstoneTimeoutValues2[1] + glowstoneTimeoutValues2[2] * glowstoneTimeoutValues2[2]);
      return !(distance < 1.0E-9) && !(sqrt2 < 1.0E-9) ? (glowstoneTimeoutFactor * glowstoneTimeoutValues2[0] + glowstoneTimeoutFactor2 * glowstoneTimeoutValues2[1] + glowstoneTimeoutFactor3 * glowstoneTimeoutValues2[2]) / (distance * sqrt2) : -1.0;
   }

   private static boolean canProtectionConfirm(GlowstoneTimeout.State state) {
      return state == GlowstoneTimeout.State.PLACE_PROTECTION || state == GlowstoneTimeout.State.WAIT_CONFIRM;
   }

   public int getValue() {
      return this.aimAttemptCount;
   }

   private GlowstoneTimeout.Action getTimeoutAim(String text) {
      this.timeoutAimText = text;
      boolean timeoutAimFlag = this.glowstoneReplaceableFlag && !this.aimTimeoutFlag;
      this.aimTimeoutFlag = this.aimTimeoutFlag || timeoutAimFlag;
      this.protectionDetonateType = GlowstoneTimeout.State.FAILED;
      this.timeoutAimValue = 0;
      this.aimAttemptCount++;
      return timeoutAimFlag ? GlowstoneTimeout.Action.RESTORE_SLOT : GlowstoneTimeout.Action.NONE;
   }

   public void updateTimeoutAim() {
      this.protectionDetonateType = GlowstoneTimeout.State.IDLE;
      this.timeoutAimValue = 0;
      this.glowstoneRefusedCount = 0;
      this.aimTimeoutCount = 0;
      this.aimAttemptCount = 0;
      this.glowstoneReplaceableFlag = false;
      this.aimTimeoutFlag = false;
      this.timeoutAimText = "";
   }

   public static int getAmounts(boolean[] glowstoneTimeoutFlags, int glowstoneTimeoutValue, double[] glowstoneTimeoutValues, double[] glowstoneTimeoutValues2, double glowstoneTimeoutFactor) {
      int glowstoneTimeoutValue3 = -1;
      double glowstoneTimeoutLimit4 = -2.0;

      for (int i = 0; i < 6; i++) {
         if (i != glowstoneTimeoutValue && i < glowstoneTimeoutFlags.length && glowstoneTimeoutFlags[i]) {
            double[] glowstoneTimeoutValues3 = glowstoneTimeoutDoubles[i];
            double[] doubles = new double[]{glowstoneTimeoutValues[0] - glowstoneTimeoutValues3[0], glowstoneTimeoutValues[1] - glowstoneTimeoutValues3[1], glowstoneTimeoutValues[2] - glowstoneTimeoutValues3[2]};
            if (canAmounts(doubles, getValue(i), glowstoneTimeoutFactor)) {
               double glowstoneTimeoutLimit = getAmount(glowstoneTimeoutValues, glowstoneTimeoutValues2, glowstoneTimeoutValues3);
               if (glowstoneTimeoutLimit > glowstoneTimeoutLimit4) {
                  glowstoneTimeoutLimit4 = glowstoneTimeoutLimit;
                  glowstoneTimeoutValue3 = i;
               }
            }
         }
      }

      return glowstoneTimeoutValue3;
   }

   private void handleProtectionDetonate(GlowstoneTimeout.State protectionDetonateType) {
      this.protectionDetonateType = protectionDetonateType;
      this.timeoutAimValue = 0;
      this.aimAttemptCount++;
   }

   public static enum Action {
      NONE,
      SELECT_GLOWSTONE_SLOT,
      PLACE_GLOWSTONE,
      SELECT_DETONATE_SLOT,
      DETONATE,
      RESTORE_SLOT;
   }

   public record CoverPlan(int index, int gain, int shieldable, int blocked) {
      public boolean isShieldableBlocked() {
         return this.shieldable > 0 && this.blocked == this.shieldable;
      }

      public boolean isFlag() {
         return this.index >= 0;
      }
   }

   public static final class Signals {
      public boolean timeoutConfirmationFlag;
      public boolean replaceableGlowstoneFlag;
      public boolean replaceableFaceFlag;
      public boolean glowstoneDetonateActive;
      public boolean glowstoneReplaceableFlag;
      public boolean glowstoneTimeoutActive;
      public boolean invalidAnchorFlag;
      public boolean glowstoneRefusedFlag;
      public boolean signalsActive;
      public int timeoutLimit;
      public boolean faceSupportFlag = true;
      public boolean detonateCharges;

      public Signals() {
         this.timeoutConfirmationFlag = true;
         this.timeoutLimit = 10;
      }
   }

   public static enum State {
      IDLE,
      PLACE_PROTECTION,
      WAIT_CONFIRM,
      DETONATE,
      RESTORE,
      DONE,
      FAILED;
   }
}
