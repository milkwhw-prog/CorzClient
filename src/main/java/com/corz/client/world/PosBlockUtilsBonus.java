package com.corz.client.world;
import com.corz.client.utils.BonusLevelData;

public final class PosBlockUtilsBonus {

   private PosBlockUtilsBonus() {
   }

   public static float getAmount(float posBlockValue, int boxLengthValue) {
      return boxLengthValue < 0 ? posBlockValue : BoxLengthUtils.getAmount(posBlockValue, boxLengthValue);
   }

   public static boolean canBreakDamage(float damageLimit, boolean damageFlag, double damageLimit2, BonusLevelData bonusLevelData) {
      return damageLimit >= bonusLevelData.breakMinDamage() || damageFlag || damageLimit2 > 0.0;
   }

   public static float getReadyInstance(float readyValue, PosBlockUtilsBonus.Armor armor, PosBlockUtilsBonus.Window window, int landTicks) {
      PosBlockUtilsBonus.Window window2 = window == null ? PosBlockUtilsBonus.Window.INSTANCE : window;
      float toughnessEpf = getToughnessEpf(readyValue, armor);
      return window2.canReady(landTicks) ? toughnessEpf : Math.max(getScaledInstance(readyValue, armor, window2, landTicks), toughnessEpf * getPow(window2.readyIn() - landTicks));
   }

   public static double getDenyEnemy(boolean denyEnemyFlag, float denyEnemyValue, float scaledInstance, BonusLevelData bonusLevelData) {
      return !denyEnemyFlag && bonusLevelData.denyEnemyCrystals() && !(denyEnemyValue < bonusLevelData.denyThreat()) ? 1.0 + Math.max(0.0, (double)(denyEnemyValue - scaledInstance)) : 0.0;
   }

   public static float getToughnessEpf(float toughnessEpfLimit, PosBlockUtilsBonus.Armor armor) {
      if (!(toughnessEpfLimit > 0.0F)) {
         return 0.0F;
      } else {
         PosBlockUtilsBonus.Armor armor2 = armor == null ? PosBlockUtilsBonus.Armor.INSTANCE : armor;
         float toughnessEpfValue = BoxLengthUtils.getAmountForFloat(toughnessEpfLimit, armor2.armor(), armor2.toughness());
         float amountForFloatInt = BoxLengthUtils.getAmountForFloatInt(toughnessEpfValue, armor2.resistanceAmp());
         return amountForFloatInt <= 0.0F ? 0.0F : Math.max(0.0F, BoxLengthUtils.getAmountFloat(amountForFloatInt, armor2.epf()));
      }
   }

   public static boolean canFlag(float posBlockLimit, float posBlockValue) {
      return posBlockLimit >= posBlockValue;
   }

   public static float getPow(int powBound) {
      return (float)Math.pow(0.96F, Math.max(0, powBound));
   }

   public static boolean canFlag(int posBlockValue, float posBlockLimit, float toughnessEpf, boolean posBlockFlag, boolean posBlockFlag2, int posBlockLimit2, int posBlockValue3) {
      return posBlockValue <= 0 || posBlockFlag2 || posBlockFlag || posBlockLimit2 >= posBlockValue3 || posBlockLimit >= 0.85F * toughnessEpf * getPow(posBlockValue);
   }

   public static double getBonusWeight(float bonusWeightFactor, boolean bonusWeightFlag, boolean kills, float scaledInstance, double posBlockValue, boolean selfFlag, BonusLevelData bonusLevelData) {
      return bonusLevelData.offWeight() * bonusWeightFactor
         + (bonusWeightFlag ? bonusLevelData.popBonus() : 0.0)
         + (kills ? bonusLevelData.killBonus() : 0.0)
         - bonusLevelData.selfWeight() * scaledInstance
         - bonusLevelData.turnCost() * posBlockValue
         - (selfFlag ? bonusLevelData.obsidianPenalty() : 0.0);
   }

   public static boolean canFlag(
      float posBlockBound, float posBlockLimit, float selfEhp, boolean posBlockFlag, boolean selfBusyFlag, boolean intersectsFlooredFlag, boolean terminatorPlaystyle, float posBlockValue, float selfSpent
   ) {
      if (Float.isFinite(posBlockLimit) && Float.isFinite(selfEhp) && Float.isFinite(posBlockValue)) {
         if (!selfBusyFlag || !(selfEhp < posBlockValue - 1.0F) && !intersectsFlooredFlag) {
            if (posBlockFlag && terminatorPlaystyle) {
               return true;
            } else if (selfEhp >= posBlockValue - 1.0F) {
               return false;
            } else if (posBlockLimit > 1.0F && posBlockLimit > posBlockValue - 4.0F) {
               return false;
            } else {
               float wrapDegreesBound = posBlockFlag ? 0.6F * Math.max(posBlockBound, 6.0F) : Math.max(1.0F, 0.35F * posBlockBound);
               if (Math.max(0.0F, selfSpent) + posBlockLimit > 6.0F) {
                  wrapDegreesBound = Math.min(wrapDegreesBound, 1.0F);
               }

               return posBlockLimit <= wrapDegreesBound;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static float getDifficultyInstance(float difficultyValue, PosBlockUtilsBonus.Armor armor, float boxLengthValue) {
      PosBlockUtilsBonus.Armor armor2 = armor == null ? PosBlockUtilsBonus.Armor.INSTANCE : armor;
      return (float)BoxLengthUtils.getAmount3(getAmountForFloatInt(difficultyValue, armor2.difficulty()), boxLengthValue);
   }

   public static double getBonusWeight(float bonusWeightFactor, boolean bonusWeightFlag, boolean kills, float scaledInstance, double bonusWeightValue, BonusLevelData bonusLevelData) {
      return bonusLevelData.offWeight() * bonusWeightFactor
         + (bonusWeightFlag ? bonusLevelData.popBonus() : 0.0)
         + (kills ? bonusLevelData.killBonus() : 0.0)
         - bonusLevelData.selfWeight() * scaledInstance
         + bonusWeightValue
         + bonusLevelData.breakTempo();
   }

   public static double getLevelPref(double levelPrefLimit, double levelPrefValue, BonusLevelData bonusLevelData) {
      if (!bonusLevelData.levelGate()) {
         return 0.0;
      } else {
         double levelPref = levelPrefLimit < bonusLevelData.levelLo() ? -bonusLevelData.lowPenalty() : (levelPrefLimit > 0.45 ? -0.5 : 0.0);
         if (levelPrefValue <= bonusLevelData.adjPref() && levelPrefLimit >= bonusLevelData.levelLo()) {
            levelPref += bonusLevelData.adjacentBonus();
         }

         return levelPref;
      }
   }

   public static float getAmountForFloatInt(float posBlockLimit, int posBlockValue) {
      if (posBlockLimit > 0.0F && Float.isFinite(posBlockLimit)) {
         return switch (posBlockValue) {
            case 1 -> posBlockLimit <= 2.0F ? posBlockLimit : 2.0F * (posBlockLimit - 1.0F);
            case 3 -> posBlockLimit * 2.0F / 3.0F;
            default -> posBlockLimit;
         };
      } else {
         return 0.0F;
      }
   }

   public static boolean canFlag(int posBlockValue, float posBlockLimit, float toughnessEpf, boolean posBlockFlag, boolean posBlockFlag2, int posBlockValue4) {
      return canFlag(posBlockValue, posBlockLimit, toughnessEpf, posBlockFlag, posBlockFlag2, posBlockValue4, 10);
   }

   public static float getAmount(float posBlockValue, PosBlockUtilsBonus.Armor armor, PosBlockUtilsBonus.Window window, int landTicks, boolean posBlockFlag) {
      return posBlockFlag ? getScaledInstance(posBlockValue, armor, window, landTicks) : getReadyInstance(posBlockValue, armor, window, landTicks);
   }

   public static boolean canLevelAdj(double levelAdjLimit, double levelAdjValue, BonusLevelData bonusLevelData) {
      return levelAdjLimit >= bonusLevelData.levelLowMin() && levelAdjLimit <= bonusLevelData.levelHi() && levelAdjValue <= bonusLevelData.adjMax();
   }

   public static float getScaledInstance(float scaledValue, PosBlockUtilsBonus.Armor armor, PosBlockUtilsBonus.Window window, int landTicks) {
      PosBlockUtilsBonus.Window window2 = window == null ? PosBlockUtilsBonus.Window.INSTANCE : window;
      return window2.canReady(landTicks) ? getToughnessEpf(scaledValue, armor) : getToughnessEpf(Math.max(0.0F, scaledValue - window2.lastScaled()), armor);
   }

   public record Armor(float armor, float toughness, int resistanceAmp, float epf, int difficulty) {
      public static final PosBlockUtilsBonus.Armor INSTANCE = new PosBlockUtilsBonus.Armor(0.0F, 0.0F, -1, 0.0F, -1);
   }

   public record Window(int readyIn, float lastScaled) {
      public static final PosBlockUtilsBonus.Window INSTANCE = new PosBlockUtilsBonus.Window(0, 0.0F);

      public boolean canReady(int landTicks) {
         return landTicks >= this.readyIn;
      }
   }
}
