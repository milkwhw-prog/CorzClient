package com.corz.client.utils;

public final class DirectionOffsetUtils2 {
   public static int getValue(int directionOffsetValue, int directionOffsetValue2, int directionOffsetValue3) {
      int[] directionOffsetValues = getValues(directionOffsetValue2, directionOffsetValue3);
      if (directionOffsetValues.length == 0) {
         return directionOffsetValue;
      } else {
         int directionOffsetValue4 = directionOffsetValues[0];

         for (int directionOffsetValuesItem : directionOffsetValues) {
            if (Math.abs(directionOffsetValuesItem - directionOffsetValue) < Math.abs(directionOffsetValue4 - directionOffsetValue)) {
               directionOffsetValue4 = directionOffsetValuesItem;
            }
         }

         return directionOffsetValue4;
      }
   }

   public static boolean canFlag(int directionOffsetLimit, int directionOffsetValue, int valueForAxis) {
      if (directionOffsetLimit >= directionOffsetValue && directionOffsetLimit <= valueForAxis) {
         int directionOffsetValue2 = valueForAxis - directionOffsetValue + 1;
         if (directionOffsetValue2 < 3) {
            return directionOffsetLimit == directionOffsetValue;
         } else {
            int directionOffsetValue3 = (directionOffsetValue2 + 2) / 3;
            int directionOffsetLimit3 = directionOffsetLimit - directionOffsetValue - 1;
            boolean directionOffsetFlag = directionOffsetLimit3 >= 0 && directionOffsetLimit3 % 3 == 0;
            switch (directionOffsetValue2 % 3) {
               case 0:
                  return directionOffsetFlag;
               case 2:
                  return directionOffsetLimit == valueForAxis - 1 || directionOffsetFlag && directionOffsetLimit <= directionOffsetValue + 1 + 3 * (directionOffsetValue3 - 2);
               default:
                  return directionOffsetValue3 == 2 ? directionOffsetLimit == directionOffsetValue + 1 || directionOffsetLimit == valueForAxis : directionOffsetLimit == valueForAxis - 1 || directionOffsetLimit == valueForAxis - 3 || directionOffsetFlag && directionOffsetLimit <= directionOffsetValue + 1 + 3 * (directionOffsetValue3 - 3);
            }
         }
      } else {
         return false;
      }
   }

   public static int[] getValues(int directionOffsetValue, int directionOffsetValue2) {
      int directionOffsetValue3 = directionOffsetValue2 - directionOffsetValue + 1;
      if (directionOffsetValue3 <= 0) {
         return new int[0];
      } else if (directionOffsetValue3 < 3) {
         return new int[]{directionOffsetValue};
      } else {
         int directionOffsetSize = (directionOffsetValue3 + 2) / 3;
         int[] ints = new int[directionOffsetSize];

         for (int i = 0; i < directionOffsetSize; i++) {
            ints[i] = directionOffsetValue + 1 + 3 * i;
         }

         switch (directionOffsetValue3 % 3) {
            case 1:
               if (directionOffsetSize == 2) {
                  ints[1] = directionOffsetValue2;
               } else {
                  ints[directionOffsetSize - 2] = directionOffsetValue2 - 3;
                  ints[directionOffsetSize - 1] = directionOffsetValue2 - 1;
               }
               break;
            case 2:
               ints[directionOffsetSize - 1] = directionOffsetValue2 - 1;
         }

         return ints;
      }
   }

   private DirectionOffsetUtils2() {
   }

   public static int getValue(int directionOffsetValue, int directionOffsetValue2) {
      int directionOffsetValue3 = directionOffsetValue2 - directionOffsetValue + 1;
      if (directionOffsetValue3 <= 0) {
         return 0;
      } else {
         return directionOffsetValue3 < 3 ? 1 : (directionOffsetValue3 + 2) / 3;
      }
   }
}
