package com.corz.client.utils;

public final class FlagUpdate {
   private int consecutiveFlagCount;
   public int updateCount;
   private boolean updateActive;
   private boolean lastUpdateActive;
   public int flagRiseCount;
   public int flagChangeCount;

   public void updateFlag() {
      this.updateActive = false;
      this.consecutiveFlagCount = 0;
      this.lastUpdateActive = false;
   }

   public boolean canFlagUpdate(boolean updateFlag, boolean updateFlag2) {
      boolean flag = updateFlag || updateFlag2;
      if (flag != this.lastUpdateActive) {
         this.lastUpdateActive = flag;
         if (this.updateActive && !flag) {
            this.flagChangeCount++;
         }
      }

      if (flag) {
         if (!this.updateActive) {
            this.updateActive = true;
            this.flagRiseCount++;
         }

         this.consecutiveFlagCount = 0;
         return true;
      } else if (!this.updateActive) {
         return false;
      } else if (++this.consecutiveFlagCount >= 4) {
         this.updateActive = false;
         this.consecutiveFlagCount = 0;
         this.updateCount++;
         return false;
      } else {
         return true;
      }
   }

   public void resetFlagCounters() {
      this.flagRiseCount = this.updateCount = this.flagChangeCount = 0;
   }

   public boolean isUpdateFlagSet() {
      return this.updateActive;
   }
}
