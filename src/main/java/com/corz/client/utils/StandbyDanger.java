package com.corz.client.utils;
import com.corz.client.utils.ScissorTgtHandler;

public final class StandbyDanger {
   private float smoothedDangerFactor;
   private float standbyAmount;
   private boolean standbyActive;
   private StandbyDanger.Mode standbyDangerType;
   private float standbyDangerAmount;
   private int dangerColorArgb = -12959668;
   private float blendedDanger;

   public void updateFlag() {
      this.standbyActive = true;
   }

   public StandbyDanger.Mode getStandbyDanger() {
      return this.standbyDangerType == null ? StandbyDanger.Mode.OFF : this.standbyDangerType;
   }

   public void handleStandbyDanger(StandbyDanger.Mode mode, float standbyDangerValue) {
      this.standbyDangerType = mode == null ? StandbyDanger.Mode.OFF : mode;
      this.standbyAmount = 1.0F;
      this.standbyActive = false;
      this.blendedDanger = this.standbyDangerAmount = getAmount(this.standbyDangerType, standbyDangerValue);
      this.smoothedDangerFactor = this.standbyDangerType.isFightDanger() ? 1.0F : 0.0F;
      this.dangerColorArgb = getValue(-12959668, -54742, this.blendedDanger);
   }

   public float getAmount() {
      return this.smoothedDangerFactor;
   }

   private static float getAmount(float standbyDangerFactor, float standbyDangerFactor2) {
      return (float)Math.sin(standbyDangerFactor * standbyDangerFactor2 * (Math.PI * 2));
   }

   public void updateAmountFlag() {
      if (this.standbyActive) {
         this.standbyActive = false;
         this.standbyDangerAmount = this.blendedDanger;
         this.standbyAmount = 0.0F;
      }
   }

   public int getValue() {
      return this.dangerColorArgb;
   }

   public static int getValue(int standbyDangerValue, int standbyDangerValue2, float standbyDangerFactor) {
      standbyDangerFactor = standbyDangerFactor < 0.0F ? 0.0F : (standbyDangerFactor > 1.0F ? 1.0F : standbyDangerFactor);
      int standbyDangerValue3 = standbyDangerValue >> 16 & 0xFF;
      int standbyDangerValue4 = standbyDangerValue >> 8 & 0xFF;
      int standbyDangerValue5 = standbyDangerValue & 0xFF;
      int standbyDangerValue6 = standbyDangerValue2 >> 16 & 0xFF;
      int standbyDangerValue7 = standbyDangerValue2 >> 8 & 0xFF;
      int standbyDangerValue8 = standbyDangerValue2 & 0xFF;
      return 0xFF000000 | Math.round(standbyDangerValue3 + (standbyDangerValue6 - standbyDangerValue3) * standbyDangerFactor) << 16 | Math.round(standbyDangerValue4 + (standbyDangerValue7 - standbyDangerValue4) * standbyDangerFactor) << 8 | Math.round(standbyDangerValue5 + (standbyDangerValue8 - standbyDangerValue5) * standbyDangerFactor);
   }

   public StandbyDanger() {
      this.standbyAmount = 1.0F;
   }

   public boolean isFlag() {
      return this.standbyActive;
   }

   public void updateAmountStandby() {
      this.standbyDangerType = null;
      this.standbyAmount = 1.0F;
      this.standbyActive = false;
      this.standbyDangerAmount = this.blendedDanger = this.smoothedDangerFactor = 0.0F;
      this.dangerColorArgb = -12959668;
   }

   public int getAmountBlendedDanger() {
      int standbyDangerValue = Math.round(Math.max(0.0F, Math.min(1.0F, this.blendedDanger)) * 255.0F);
      return standbyDangerValue << 24 | 16722474;
   }

   public static float getAmount(StandbyDanger.Mode mode, float standbyDangerValue) {
      return switch (mode) {
         case OFF -> 0.0F;
         case STANDBY -> 0.8F;
         case PAUSED -> 0.84F + 0.08F * getAmount(standbyDangerValue, 0.35F);
         case HUNT -> 0.9F;
         case FIGHT -> 0.55F + 0.45F * (0.5F + 0.5F * getAmount(standbyDangerValue, 1.2F));
         case DANGER -> 0.35F + 0.65F * (0.5F + 0.5F * getAmount(standbyDangerValue, 2.4F));
      };
   }

   public float getBlendedDanger() {
      return this.blendedDanger;
   }

   public int getAmountStandby(StandbyDanger.Mode standbyDangerType, float standbyValue, float standbyFactor) {
      if (standbyDangerType == null) {
         standbyDangerType = StandbyDanger.Mode.OFF;
      }

      if (this.standbyDangerType == null) {
         this.standbyDangerType = standbyDangerType;
         this.standbyAmount = 1.0F;
      } else if (standbyDangerType != this.standbyDangerType) {
         this.standbyDangerAmount = this.blendedDanger;
         this.standbyDangerType = standbyDangerType;
         this.standbyAmount = 0.0F;
      }

      float standbyFactor2 = standbyFactor <= 0.0F ? 0.0F : 1.0F - (float)Math.exp(-12.0F * standbyFactor);
      this.standbyAmount = this.standbyAmount + (1.0F - this.standbyAmount) * standbyFactor2;
      this.smoothedDangerFactor = this.smoothedDangerFactor + ((standbyDangerType.isFightDanger() ? 1.0F : 0.0F) - this.smoothedDangerFactor) * standbyFactor2;
      if (this.standbyActive) {
         this.standbyAmount = 1.0F;
         this.blendedDanger = this.standbyDangerAmount = 0.0F;
      } else {
         this.blendedDanger = this.standbyDangerAmount + (getAmount(standbyDangerType, standbyValue) - this.standbyDangerAmount) * this.standbyAmount;
      }

      this.dangerColorArgb = getValue(-12959668, -54742, this.blendedDanger);
      return this.dangerColorArgb;
   }

   public static StandbyDanger.Mode getStandbyFight(boolean standbyFightFlag, ScissorTgtHandler.Snapshot snapshot) {
      if (!standbyFightFlag) {
         return StandbyDanger.Mode.OFF;
      } else if (snapshot == null) {
         return StandbyDanger.Mode.STANDBY;
      } else {
         return switch (snapshot.tone()) {
            case PAUSED -> StandbyDanger.Mode.PAUSED;
            case DANGER -> StandbyDanger.Mode.DANGER;
            case ACTIVE -> snapshot.target() != null ? StandbyDanger.Mode.FIGHT : StandbyDanger.Mode.HUNT;
            default -> StandbyDanger.Mode.STANDBY;
         };
      }
   }

   public static enum Mode {
      OFF,
      STANDBY,
      PAUSED,
      HUNT,
      FIGHT,
      DANGER;

      public boolean isFightDanger() {
         return this == FIGHT || this == DANGER;
      }
   }
}
