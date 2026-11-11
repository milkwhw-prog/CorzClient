package com.corz.client.world;

import java.util.List;

public final class VecPosUtilsBreak {
   public static final int UNIT_STEP = 1;

   public static int getValue(double vecPosLimit) {
      double vecPosValue = vecPosLimit > 0.0 ? vecPosLimit : 0.0;
      return Double.isInfinite(vecPosValue) ? 536870911 : (int)Math.ceil(vecPosValue / 50.0) + 1;
   }

   public static int[] getValues(VecPosUtilsBreak.Ctx ctx, List<VecPosUtilsBreak.Step> list) {
      return getPlaceBreak(ctx, list, null);
   }

   private VecPosUtilsBreak() {
   }

   static int[] getPlaceBreak(VecPosUtilsBreak.Ctx ctx, List<VecPosUtilsBreak.Step> list, int[] vecPosValues) {
      int vecPosSize = list == null ? 0 : list.size();
      int[] ints = new int[vecPosSize];
      if (vecPosSize == 0) {
         return ints;
      } else {
         int maxPlaceGap = Math.max(0, ctx.placeGap());
         int maxSpawnSeen = Math.max(0, ctx.spawnSeen());
         int vecPosValue = -1;
         int maxTicksSinceSlotChange = -Math.max(0, ctx.ticksSinceSlotChange());
         int maxTicksSinceRightClick = -Math.max(0, ctx.ticksSinceRightClick());
         int selectedItem = ctx.selectedItem();
         int minValue = Integer.MIN_VALUE;
         int minValue3 = Integer.MIN_VALUE;
         int minValue4 = Integer.MIN_VALUE;

         for (int i = 0; i < vecPosSize; i++) {
            VecPosUtilsBreak.Step selecttick = (VecPosUtilsBreak.Step)list.get(i);
            VecPosUtilsBreak.Op op = selecttick.op();
            boolean vecPosFlag = op == VecPosUtilsBreak.Op.PLACE_OBSIDIAN || op == VecPosUtilsBreak.Op.PLACE_CRYSTAL;
            int vecPosBound = vecPosValue + 1;
            int minValue5 = Integer.MIN_VALUE;
            if (op != VecPosUtilsBreak.Op.BREAK && selecttick.item() != selectedItem) {
               int max6 = Math.max(vecPosValue + 1, maxTicksSinceSlotChange + 2);
               vecPosBound = Math.max(vecPosBound, max6 + 1);
               selectedItem = selecttick.item();
               maxTicksSinceSlotChange = max6;
               minValue5 = max6;
            }

            int vecPosLimit = getValueForDouble(selecttick.turnDegrees());
            if (vecPosLimit > 0) {
               vecPosBound = Math.max(vecPosBound, vecPosValue + vecPosLimit + (vecPosFlag ? 1 : 0) + 1);
            }

            if (vecPosFlag) {
               vecPosBound = Math.max(vecPosBound, maxTicksSinceRightClick + maxPlaceGap);
            }

            if (op == VecPosUtilsBreak.Op.PLACE_CRYSTAL && minValue != Integer.MIN_VALUE) {
               vecPosBound = Math.max(vecPosBound, minValue + (ctx.trustObsidian() ? maxPlaceGap : Math.max(0, ctx.reattack())));
            }

            if (op == VecPosUtilsBreak.Op.BREAK && minValue3 != Integer.MIN_VALUE) {
               vecPosBound = Math.max(vecPosBound, minValue3 + maxSpawnSeen);
            }

            if (op == VecPosUtilsBreak.Op.PLACE_CRYSTAL && selecttick.sameBaseAsLastBreak() && minValue4 != Integer.MIN_VALUE) {
               vecPosBound = Math.max(vecPosBound, minValue4 + maxSpawnSeen);
            }

            ints[i] = vecPosBound;
            if (vecPosValues != null && i < vecPosValues.length) {
               vecPosValues[i] = minValue5;
            }

            vecPosValue = vecPosBound;
            if (vecPosFlag) {
               maxTicksSinceRightClick = vecPosBound;
            }

            if (op == VecPosUtilsBreak.Op.PLACE_OBSIDIAN) {
               minValue = vecPosBound;
            } else if (op == VecPosUtilsBreak.Op.PLACE_CRYSTAL) {
               minValue3 = vecPosBound;
            } else if (op == VecPosUtilsBreak.Op.BREAK) {
               minValue4 = vecPosBound;
            }
         }

         return ints;
      }
   }

   public static int getValue(int vecPosValue) {
      return 2 * vecPosValue;
   }

   public static int getValueForDouble(double vecPosValue) {
      double absDegrees = Math.abs(vecPosValue);
      if (absDegrees <= 6.0) {
         return 0;
      } else if (absDegrees <= 25.0) {
         return 1;
      } else if (absDegrees <= 45.0) {
         return 2;
      } else if (absDegrees <= 70.0) {
         return 3;
      } else if (absDegrees <= 100.0) {
         return 4;
      } else {
         return absDegrees <= 140.0 ? 5 : 6;
      }
   }

   public record Ctx(int selectedItem, int ticksSinceSlotChange, int ticksSinceRightClick, int placeGap, int spawnSeen, int reattack, boolean trustObsidian) {
   }

   public static enum Op {
      PLACE_OBSIDIAN,
      PLACE_CRYSTAL,
      BREAK,
      STRIKE;
   }

   public record Step(VecPosUtilsBreak.Op op, int item, double turnDegrees, boolean sameBaseAsLastBreak) {
   }
}
