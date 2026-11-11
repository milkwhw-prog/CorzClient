package com.corz.client.utils;

public record RefBareData(boolean on, boolean deep, boolean behind, boolean ref, boolean bare) {
   public static final RefBareData INSTANCE = new RefBareData(false, false);

   public float getAmount(float base) {
      return this.on ? Math.min(base, 0.5F) : base;
   }

   public boolean canRef(float full, boolean dealt, float popsTarget, float cost, boolean deny) {
      if (!this.ref || dealt) {
         return true;
      } else if (deny && this.canBareRef(popsTarget, cost)) {
         return true;
      } else {
         float bareRef = this.getBareRef(popsTarget, cost);
         return Float.isFinite(full) && Float.isFinite(bareRef) && full >= bareRef;
      }
   }

   public static RefBareData getDataForFlagInt(boolean terminator, boolean totems, int totemConfirmed, float ehp) {
      return getDataForFlagIntFloat(terminator, totems, totemConfirmed, ehp, false);
   }

   public boolean canBareBehind(boolean kills, boolean pops) {
      return this.ref && this.deep && !this.bare && (kills || pops && !this.behind);
   }

   public static double getAmount(double up, double dist) {
      return Double.isFinite(up) && Double.isFinite(dist) && dist > 1.0E-6 ? Math.max(0.0, Math.min(1.0, up / dist)) : 0.0;
   }

   public boolean canFlag(float knock, float selfEhp, boolean cost, float full, float popsTarget, float dealt) {
      return this.canRef(knock, selfEhp, cost, Float.NaN, full, popsTarget, dealt);
   }

   public RefBareData(boolean on, boolean behind, boolean deep) {
      this(on, behind, deep, false, false);
   }

   public static boolean canFlag(float targetEhp) {
      return Float.isFinite(targetEhp) && targetEhp > 0.0F && targetEhp <= 6.0F;
   }

   public boolean canRef(float knock, float full, boolean targetEhp, float dealt, float popsTarget, float selfEhp, float cost) {
      if (!this.canFlag(knock, full, targetEhp)) {
         return false;
      } else if (!Float.isFinite(popsTarget) || !Float.isFinite(selfEhp) || !Float.isFinite(cost)) {
         return false;
      } else if (this.ref) {
         return this.canBare(Math.max(popsTarget, selfEhp), cost, false);
      } else {
         return canFlag(Math.max(popsTarget, selfEhp), cost)
            ? this.canDeep(knock, targetEhp, dealt)
            : this.canDeepRef(popsTarget, selfEhp, cost) && canFlag(knock, full, targetEhp, selfEhp);
      }
   }

   public float getAmount(float lift, float selfKnock, double knock) {
      if (!this.on) {
         return 0.0F;
      } else {
         float refBareValue = Float.isFinite(lift) ? Math.max(0.0F, Math.min(1.0F, lift)) : 0.0F;
         return Float.isFinite(selfKnock) && Double.isFinite(knock)
            ? refBareValue - Math.max(0.0F, Math.min(1.0F, selfKnock)) * (float)Math.max(0.0, Math.min(1.0, knock))
            : refBareValue;
      }
   }

   public float getAmountForFloat(float knock) {
      return this.on && Float.isFinite(knock) ? 3.0F * Math.max(-1.0F, Math.min(1.0F, knock)) : 0.0F;
   }

   public float getBareRef(float cost, float full) {
      return this.ref && !this.bare && Float.isFinite(full) ? Math.min(cost, Math.max(0.0F, full)) : cost;
   }

   public static boolean canFlag(float full, float ehp) {
      return full > 0.0F && Float.isFinite(ehp) && full >= ehp - 1.0F;
   }

   public static RefBareData getInstance(boolean ehp, boolean terminator, int behind, float popPending, boolean totems, boolean totemConfirmed) {
      return ehp && terminator && !totems && behind >= 1 && popPending > 0.0F && Float.isFinite(popPending)
         ? new RefBareData(true, behind >= 2, totemConfirmed)
         : INSTANCE;
   }

   public double getAmount(double value) {
      return this.on && Double.isFinite(value) ? Math.max(value, 0.25) : value;
   }

   public boolean canBareRef(float full, float cost) {
      return this.ref && !this.bare && Float.isFinite(full) && full - this.getBareRef(full, cost) >= 1.0F;
   }

   public boolean canFlag(boolean kills) {
      return this.canBareBehind(kills, false);
   }

   public RefBareData(boolean on, boolean deep) {
      this(on, deep, false);
   }

   public String formatDeepBare() {
      return this.on ? (this.ref ? (this.deep ? "R" : (this.bare ? "b" : "r")) : (this.deep ? "2" : "1")) : "0";
   }

   public boolean canDeepRef(float cost, float full, float selfEhp) {
      if (!this.on || !Float.isFinite(cost) || !Float.isFinite(full) || !Float.isFinite(selfEhp)) {
         return false;
      } else if (this.ref) {
         return this.canBare(Math.max(cost, full), selfEhp, false);
      } else if (canFlag(Math.max(cost, full), selfEhp)) {
         return false;
      } else {
         return this.deep ? true : Math.max(cost, full) <= selfEhp - 4.0F;
      }
   }

   public static RefBareData getInstance(boolean totems, int behind, float ehp, boolean totemReady) {
      return ehp > 0.0F && Float.isFinite(ehp) ? new RefBareData(true, totems && behind >= 2, totemReady, true, !totems) : INSTANCE;
   }

   public static RefBareData getDataForFlagIntFloat(boolean ehp, boolean terminator, int popPending, float totemConfirmed, boolean totems) {
      return getInstance(ehp, terminator, popPending, totemConfirmed, totems, false);
   }

   public boolean canDeep(float targetEhp, boolean popsTarget, float dealt) {
      return this.on && this.deep && popsTarget ? !Float.isFinite(dealt) || Float.isFinite(targetEhp) && targetEhp >= dealt + 1.0F : false;
   }

   public float getAmount() {
      return this.on ? 3.0F : 0.0F;
   }

   public boolean canFlag(float popsTarget, float knock, boolean dealt) {
      return !this.on ? false : dealt || Float.isFinite(popsTarget) && popsTarget >= 1.0F || Float.isFinite(knock) && knock >= 0.25F;
   }

   public boolean canBare(float selfEhp, float full, boolean lethalOk) {
      if (!Float.isFinite(selfEhp) || !Float.isFinite(full)) {
         return false;
      } else {
         return selfEhp > 0.0F && !(selfEhp < full - (this.bare ? 2.0F : 1.0F)) ? !this.bare && lethalOk : true;
      }
   }

   public static boolean canFlag(float cost, float knock, boolean dealt, float popsTarget) {
      if (!Float.isFinite(popsTarget)) {
         return false;
      } else {
         float maxPopsTarget = Math.max(0.0F, popsTarget);
         if (dealt) {
            return true;
         } else {
            return Float.isFinite(cost) && cost >= 1.0F ? maxPopsTarget <= 2.0F || maxPopsTarget <= 0.7F * cost : Float.isFinite(knock) && knock >= 0.25F && maxPopsTarget <= 1.0F;
         }
      }
   }
}
