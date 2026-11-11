package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;
import com.corz.client.utils.CraftedExcavation;

final class BaseCore {
   static final int UNUSED_ONE = 1;

   static boolean isValidExcavationSet(List<CraftedExcavation> list) {
      for (CraftedExcavation craftedExcavation : list) {
         for (CraftedExcavation craftedExcavation2 : list) {
            if (craftedExcavation != craftedExcavation2
               && Math.max(Math.abs(craftedExcavation.posX - craftedExcavation2.posX), Math.max(Math.abs(craftedExcavation.posY - craftedExcavation2.posY), Math.abs(craftedExcavation.posZ - craftedExcavation2.posZ))) <= 2) {
               return true;
            }
         }
      }

      return false;
   }

   private BaseCore() {
   }

   static int clampCoreBound(int coreBound) {
      return 20 - Math.max(1, Math.min(8, coreBound));
   }

   static BaseCore.Result evaluateChestBed(
      List<CraftedExcavation> list, ToIntFunction<CraftedExcavation> toIntFunction, int chestBedValue, BaseCore.Guard guard, int chestBedValue2
   ) {
      int chestBedCounter = 0;
      int rocketsBuyBound = 0;
      int chestBedCounter3 = 0;
      int chestBedCounter4 = 0;
      int chestBedCounter5 = 0;
      int chestBedCounter6 = 0;
      int chestBedLimit = 0;
      int chestBedLimit2 = 0;
      int chestBedLimit3 = 0;
      int chestBedLimit4 = 0;
      int chestBedCounter7 = 0;
      int chestBedCounter8 = 0;
      int chestBedCounter9 = 0;
      int chestBedCounter10 = 0;
      int spotBound = 0;
      int chestBedCounter12 = 0;
      int chestBedCounter13 = 0;
      int chestBedCounter14 = 0;
      int chestBedCounter15 = 0;
      int chestBedCounter16 = 0;
      ArrayList arrayList = new ArrayList();

      for (CraftedExcavation craftedExcavation : list) {
         switch (craftedExcavation.craftedTypeId) {
            case 1:
               int applyAsInt = toIntFunction.applyAsInt(craftedExcavation);
               if (applyAsInt == 1) {
                  chestBedCounter++;
               } else if (applyAsInt == 2) {
                  rocketsBuyBound++;
               } else if (!craftedExcavation.hasFlag(64)) {
                  chestBedCounter3++;
               }
               break;
            case 2:
               if (!craftedExcavation.hasFlag(64)) {
                  chestBedCounter3++;
               }
               break;
            case 3:
               chestBedCounter5++;
               break;
            case 4:
               chestBedCounter6++;
               break;
            case 5:
               chestBedCounter4++;
               break;
            case 6:
               chestBedCounter10++;
               break;
            case 7:
               chestBedCounter13++;
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 47:
            case 48:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 107:
            default:
               break;
            case 9:
            case 15:
               chestBedCounter14++;
               break;
            case 23:
               spotBound++;
               break;
            case 24:
               chestBedCounter12++;
               break;
            case 25:
               chestBedLimit2++;
               break;
            case 26:
               chestBedLimit++;
               break;
            case 27:
               chestBedLimit3++;
               break;
            case 28:
               chestBedLimit4++;
               break;
            case 29:
            case 33:
            case 45:
            case 46:
               chestBedCounter15++;
               break;
            case 30:
               chestBedCounter7++;
               break;
            case 31:
               chestBedCounter8++;
               break;
            case 32:
               chestBedCounter9++;
               break;
            case 49:
               arrayList.add(craftedExcavation);
               break;
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 108:
               chestBedCounter16++;
         }
      }

      int max5 = Math.max(chestBedCounter, rocketsBuyBound);
      int chestBedValue3 = chestBedCounter3 + (chestBedCounter + rocketsBuyBound - 2 * Math.min(chestBedCounter, rocketsBuyBound));
      boolean chestBedFlag = guard == BaseCore.Guard.NONE;
      boolean chestBedFlag2 = guard != BaseCore.Guard.STRICT;
      boolean chestBedFlag3 = !arrayList.isEmpty() && (chestBedCounter13 > 0 || isValidExcavationSet(arrayList));
      int chestBedValue4 = chestBedCounter5 + chestBedCounter4 + chestBedLimit + chestBedLimit4 + chestBedCounter8 + chestBedCounter15 + chestBedCounter14;
      int chestBedLimit6 = (guard == BaseCore.Guard.STRICT ? 0 : Math.min(48, 6 * max5))
         + Math.min(36, 6 * chestBedCounter5)
         + (int)Math.min(24L, Math.round(1.5 * (chestBedCounter4 + (chestBedFlag ? chestBedValue3 : 0))));
      int chestBedTotal = (chestBedLimit4 > 0 ? 8 : 0) + (chestBedLimit > 0 ? 5 : 0) + (chestBedCounter8 > 0 ? 3 : 0) + Math.min(4, chestBedCounter14 + (chestBedFlag2 ? chestBedCounter13 : 0)) + (chestBedCounter15 > 0 ? 3 : 0);
      int chestBedLimit7 = (chestBedCounter7 + 1) / 2;
      if (chestBedFlag) {
         chestBedTotal += (chestBedLimit2 > 0 ? 3 : 0)
            + (chestBedLimit3 > 0 ? 3 : 0)
            + (chestBedLimit7 > 0 ? 3 : 0)
            + (chestBedCounter9 >= 6 ? 3 : 0)
            + Math.min(3, chestBedCounter10)
            + Math.min(1, spotBound)
            + Math.min(3, chestBedCounter12)
            + (chestBedCounter6 > 0 && chestBedValue2 != 2 ? 2 : 0);
      }

      int chestBedLimit8 = chestBedFlag3 && chestBedFlag2 ? Math.min(20, 8 + 4 * (arrayList.size() - 1)) : 0;
      int chestBedValue5 = chestBedCounter16 >= 8 && chestBedFlag ? 3 : 0;
      int chestBedLimit9 = (chestBedLimit2 > 0 ? 1 : 0) + (chestBedLimit3 > 0 ? 1 : 0) + (chestBedLimit7 > 0 ? 1 : 0) + (chestBedCounter8 > 0 ? 1 : 0) + (chestBedLimit4 > 0 ? 1 : 0);
      boolean chestBedFlag4 = chestBedLimit > 0 && chestBedLimit9 >= 2 && chestBedFlag;
      boolean chestBedFlag5 = chestBedLimit6 >= 9 || chestBedLimit4 > 0 || chestBedLimit8 > 0 || chestBedFlag4;
      if (chestBedValue2 != 0 && chestBedValue4 == 0 && max5 < 3) {
         chestBedFlag5 = false;
      }

      int chestBedLimit10 = chestBedLimit6 + chestBedTotal + chestBedLimit8 + chestBedValue5;
      int clampCoreBound6 = clampCoreBound(chestBedValue);
      if (chestBedFlag5 && chestBedLimit10 >= clampCoreBound6) {
         int max7 = Math.max(40, Math.min(100, 40 + 3 * (chestBedLimit10 - clampCoreBound6)));
         ArrayList arrayList2 = new ArrayList(4);
         if (max5 > 0 && guard != BaseCore.Guard.STRICT) {
            arrayList2.add(max5 == 1 ? "double chest" : max5 + " double chests");
         }

         if (chestBedCounter5 > 0) {
            arrayList2.add(chestBedCounter5 == 1 ? "shulker box" : chestBedCounter5 + " shulker boxes");
         }

         if (chestBedLimit4 > 0) {
            arrayList2.add("beacon");
         }

         if (chestBedLimit8 > 0) {
            arrayList2.add(arrayList.size() == 1 ? "spawner farm" : arrayList.size() + " spawners");
         }

         if (chestBedLimit > 0) {
            arrayList2.add("enchanting table");
         }

         if (chestBedFlag && chestBedValue3 + chestBedCounter4 > 0) {
            arrayList2.add(chestBedValue3 + chestBedCounter4 == 1 ? "chest" : chestBedValue3 + chestBedCounter4 + " chests");
         }

         if (chestBedFlag && chestBedLimit2 > 0) {
            arrayList2.add("anvil");
         }

         if (chestBedFlag && chestBedLimit3 > 0) {
            arrayList2.add("brewing stand");
         }

         if (chestBedFlag && chestBedLimit7 > 0) {
            arrayList2.add(chestBedLimit7 == 1 ? "bed" : chestBedLimit7 + " beds");
         }

         String join = String.join(", ", arrayList2.subList(0, Math.min(3, arrayList2.size())));
         if (!join.isEmpty()) {
            join = Character.toUpperCase(join.charAt(0)) + join.substring(1);
         }

         return new BaseCore.Result(max7, chestBedLimit10, join);
      } else {
         return BaseCore.Result.INSTANCE;
      }
   }

   static enum Guard {
      NONE,
      STRUCTURE,
      STRICT;
   }

   record Result(int score, int total, String describe) {
      static final BaseCore.Result INSTANCE = new BaseCore.Result(0, 0, "");
   }
}
