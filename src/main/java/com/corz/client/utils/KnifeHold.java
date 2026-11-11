package com.corz.client.utils;

import java.util.function.IntToDoubleFunction;

public final class KnifeHold {
   public static final double[] knifeHoldTable = new double[]{0.4, -0.23, -0.92};
   public static final double[] table = new double[]{-0.33, 0.7, -0.63};
   public static final double[] HOLD_POSITION = new double[]{0.8, 0.56, 0.21};
   public static final double[] HAND_OFFSET = new double[]{-0.0625, 0.56875, 0.0375};
   public static final double[] BLADE_END_OFFSET = new double[]{0.0625, 0.53125, -0.0125};
   public static final double[] BLADE_START_OFFSET = new double[]{0.0625, 0.625, -0.125};
   public static final double[] HOLD_PIVOT = new double[]{8.0, 2.0, 8.0};
   static final double[] SWING_PARAMS = new double[]{16.0, 30.0, -10.0, -0.06, -29.0, 0.55, -0.06};
   private static final double[] FLIP_Z_MATRIX = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
   private static final double[] FLIP_X_MATRIX = new double[]{-1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};

   public static double[] getAmounts(double knifeHoldValue) {
      double scaleScaled = knifeHoldValue / 0.48;
      double[] bladeStartOffset = BLADE_START_OFFSET;
      return new double[]{bladeStartOffset[0] + scaleScaled * (BLADE_END_OFFSET[0] - bladeStartOffset[0]), bladeStartOffset[1] + scaleScaled * (BLADE_END_OFFSET[1] - bladeStartOffset[1]), bladeStartOffset[2] + scaleScaled * (BLADE_END_OFFSET[2] - bladeStartOffset[2])};
   }

   public static double[] getAmounts(int knifeHoldValue, double knifeHoldValue2, IntToDoubleFunction intToDoubleFunction, boolean knifeHoldFlag) {
      return getAmounts(knifeHoldValue, knifeHoldValue2, intToDoubleFunction, knifeHoldFlag, null);
   }

   private KnifeHold() {
   }

   public static double[] getAmountsForDouble(double degrees) {
      double angle = Math.toRadians(degrees);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return new double[]{1.0, 0.0, 0.0, 0.0, 0.0, cos, -sin, 0.0, 0.0, sin, cos, 0.0, 0.0, 0.0, 0.0, 1.0};
   }

   public static double[] getAmounts(int knifeHoldValue, IntToDoubleFunction intToDoubleFunction) {
      return getAmountsForDoubleArray(getAmounts2(getAmounts(knifeHoldValue)), getAmountsForDoubleArray(getAmountsForIntIntToDoubleFunction(knifeHoldValue, intToDoubleFunction), getAmounts2(getAmountsForInt(knifeHoldValue))));
   }

   static double[] getAmounts(IntToDoubleFunction intToDoubleFunction) {
      return getAmounts(
         getAmounts(intToDoubleFunction.applyAsDouble(8), intToDoubleFunction.applyAsDouble(9), -intToDoubleFunction.applyAsDouble(10)),
         getAmountsDoubleArray(intToDoubleFunction.applyAsDouble(11)),
         getAmountsForDouble(intToDoubleFunction.applyAsDouble(13)),
         getAmounts5(-intToDoubleFunction.applyAsDouble(12))
      );
   }

   static double[] getAmounts(double[] knifeHoldValues, double[] knifeHoldValues2) {
      return new double[]{knifeHoldValues[1] * knifeHoldValues2[2] - knifeHoldValues[2] * knifeHoldValues2[1], knifeHoldValues[2] * knifeHoldValues2[0] - knifeHoldValues[0] * knifeHoldValues2[2], knifeHoldValues[0] * knifeHoldValues2[1] - knifeHoldValues[1] * knifeHoldValues2[0]};
   }

   static double[] getAmountsForIntToDoubleFunction(IntToDoubleFunction intToDoubleFunction) {
      return getAmounts(
         getAmounts(HAND_OFFSET[0], HAND_OFFSET[1], HAND_OFFSET[2]),
         getAmounts5(getAsDouble(intToDoubleFunction.applyAsDouble(7))),
         getAmountsForDouble(intToDoubleFunction.applyAsDouble(6)),
         getAmounts(-HAND_OFFSET[0], -HAND_OFFSET[1], -HAND_OFFSET[2])
      );
   }

   public static double[] getAmountsForIntIntToDoubleFunction(int knifeHoldValue, IntToDoubleFunction intToDoubleFunction) {
      double[] knifeHoldValues = getAmountsForDoubleArray(getAmountsKnifeHoldTable(intToDoubleFunction), getAmountsForDoubleArray(getAmountsTable(), getAmountsForIntToDoubleFunction(intToDoubleFunction)));
      return knifeHoldValue < 0 ? getClone(knifeHoldValues) : knifeHoldValues;
   }

   static double[] getAmountsKnifeHoldTable(IntToDoubleFunction intToDoubleFunction) {
      return getAmounts(
         getAmounts(knifeHoldTable[0] + intToDoubleFunction.applyAsDouble(0), knifeHoldTable[1] + intToDoubleFunction.applyAsDouble(1), knifeHoldTable[2] + intToDoubleFunction.applyAsDouble(2)),
         getAmounts5(intToDoubleFunction.applyAsDouble(4)),
         getAmountsDoubleArray(intToDoubleFunction.applyAsDouble(5)),
         getAmountsForDouble(intToDoubleFunction.applyAsDouble(3)),
         getAmounts(-knifeHoldTable[0], -knifeHoldTable[1], -knifeHoldTable[2])
      );
   }

   static double[] getAmountsSWING_PARAMS(double knifeHoldFactor) {
      double sin = Math.sin(Math.sqrt(knifeHoldFactor) * Math.PI);
      double sin2 = Math.sin(knifeHoldFactor * Math.PI);
      double knifeHoldValue = SWING_PARAMS[0] * sin;
      double knifeHoldValue2 = SWING_PARAMS[1] * sin;
      double degrees = SWING_PARAMS[2] * sin;
      double[] knifeHoldValues = getAmounts(table);
      double[] doubles = new double[]{knifeHoldValues[0] * SWING_PARAMS[5], knifeHoldValues[1] * SWING_PARAMS[5], knifeHoldValues[2] * SWING_PARAMS[5]};
      double[] amounts3 = getAmounts3(getAmounts(getAmounts5(knifeHoldValue), getAmountsDoubleArray(knifeHoldValue2), getAmountsForDouble(degrees)), doubles);
      double[] doubles2 = new double[17];
      doubles2[0] = amounts3[0] - doubles[0];
      doubles2[1] = amounts3[1] - doubles[1] + SWING_PARAMS[6] * sin;
      doubles2[2] = amounts3[2] - doubles[2] + SWING_PARAMS[3] * sin2;
      doubles2[4] = knifeHoldValue;
      doubles2[5] = knifeHoldValue2;
      doubles2[3] = degrees;
      doubles2[11] = SWING_PARAMS[4] * sin;
      return doubles2;
   }

   public static double[] getAmountsDoubleArray(double degrees) {
      double angle = Math.toRadians(degrees);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return new double[]{cos, -sin, 0.0, 0.0, sin, cos, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
   }

   public static double[] getAmounts5(double degrees) {
      double angle = Math.toRadians(degrees);
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return new double[]{cos, 0.0, sin, 0.0, 0.0, 1.0, 0.0, 0.0, -sin, 0.0, cos, 0.0, 0.0, 0.0, 0.0, 1.0};
   }

   static double getAmount(double[] knifeHoldValues, double[] knifeHoldValues2) {
      return knifeHoldValues[0] * knifeHoldValues2[0] + knifeHoldValues[1] * knifeHoldValues2[1] + knifeHoldValues[2] * knifeHoldValues2[2];
   }

   public static double[] getAmountsForDoubleArray(double[] knifeHoldValues, double[] doublesItem) {
      double[] doubles = new double[16];

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            double knifeHoldTotal = 0.0;

            for (int k = 0; k < 4; k++) {
               knifeHoldTotal += knifeHoldValues[i * 4 + k] * doublesItem[k * 4 + j];
            }

            doubles[i * 4 + j] = knifeHoldTotal;
         }
      }

      return doubles;
   }

   public static double[] getAmounts6(double knifeHoldValue) {
      double[] knifeHoldValues = getAmounts();
      knifeHoldValues[0] = knifeHoldValue;
      knifeHoldValues[5] = knifeHoldValue;
      knifeHoldValues[10] = knifeHoldValue;
      return knifeHoldValues;
   }

   public static double[] getAmounts(double knifeHoldValue, double knifeHoldValue2, double knifeHoldValue3) {
      double[] knifeHoldValues = getAmounts();
      knifeHoldValues[3] = knifeHoldValue;
      knifeHoldValues[7] = knifeHoldValue2;
      knifeHoldValues[11] = knifeHoldValue3;
      return knifeHoldValues;
   }

   static double[] getAmounts(double[] knifeHoldValues) {
      double sqrt = Math.sqrt(getAmount(knifeHoldValues, knifeHoldValues));
      return new double[]{knifeHoldValues[0] / sqrt, knifeHoldValues[1] / sqrt, knifeHoldValues[2] / sqrt};
   }

   public static IntToDoubleFunction getIntToDoubleFunction(IntToDoubleFunction intToDoubleFunction, double intToLimit) {
      if (intToLimit > 0.0 && !(intToLimit >= 1.0)) {
         double[] intToValues = getAmountsSWING_PARAMS(intToLimit);
         return chx -> intToDoubleFunction.applyAsDouble(chx) + intToValues[chx];
      } else {
         return intToDoubleFunction;
      }
   }

   public static double[] getAmounts() {
      return new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
   }

   public static double[] getAmounts(int knifeHoldValue, double knifeHoldValue2, IntToDoubleFunction intToDoubleFunction, boolean knifeHoldFlag, double[] knifeHoldValues) {
      double[] amounts2 = getAmounts(knifeHoldValue2);
      double knifeHoldValue3 = knifeHoldValues == null ? 0.0 : getAmount(knifeHoldValues[0], 1.0);
      double knifeHoldValue4 = knifeHoldValues == null ? 0.0 : getAmount(knifeHoldValues[1], 1.0);
      double knifeHoldValue5 = knifeHoldValues == null ? 0.0 : getAmount(knifeHoldValues[2], 20.0);
      double[] amounts3 = getAmounts(
         getAmounts(amounts2[0], amounts2[1] - knifeHoldValue3 / 16.0, amounts2[2] - knifeHoldValue4 / 16.0),
         getAmounts5(-90.0),
         getAmountsDoubleArray(90.0),
         getAmountsDoubleArray(-34.0 - knifeHoldValue5),
         getAmounts6(knifeHoldValue2 / 16.0),
         getAmounts(0.0, 0.0, -1.4),
         getAmounts(intToDoubleFunction)
      );
      if (knifeHoldValue < 0) {
         amounts3 = getAmounts(FLIP_X_MATRIX, amounts3, FLIP_Z_MATRIX);
      }

      return getAmountsForDoubleArray(amounts3, getAmounts(-HOLD_PIVOT[0], -HOLD_PIVOT[1], -HOLD_PIVOT[2]));
   }

   public static double[] getAmounts(int knifeHoldValue, double knifeHoldValue2, IntToDoubleFunction intToDoubleFunction) {
      return getAmounts(knifeHoldValue, knifeHoldValue2, intToDoubleFunction, false);
   }

   static double getAmount(double knifeHoldBound, double hypot) {
      return knifeHoldBound != knifeHoldBound ? 0.0 : Math.max(-hypot, Math.min(hypot, knifeHoldBound));
   }

   public static double[] getAmounts2(double[] knifeHoldValues) {
      double[] doubles = new double[16];
      double knifeHoldFactor = knifeHoldValues[0];
      double knifeHoldFactor2 = knifeHoldValues[1];
      double knifeHoldFactor3 = knifeHoldValues[2];
      double knifeHoldFactor4 = knifeHoldValues[4];
      double knifeHoldFactor5 = knifeHoldValues[5];
      double knifeHoldFactor6 = knifeHoldValues[6];
      double knifeHoldFactor7 = knifeHoldValues[8];
      double knifeHoldFactor8 = knifeHoldValues[9];
      double knifeHoldFactor9 = knifeHoldValues[10];
      double knifeHoldDivisor = knifeHoldFactor * (knifeHoldFactor5 * knifeHoldFactor9 - knifeHoldFactor6 * knifeHoldFactor8) - knifeHoldFactor2 * (knifeHoldFactor4 * knifeHoldFactor9 - knifeHoldFactor6 * knifeHoldFactor7) + knifeHoldFactor3 * (knifeHoldFactor4 * knifeHoldFactor8 - knifeHoldFactor5 * knifeHoldFactor7);
      double knifeHoldFactor10 = 1.0 / knifeHoldDivisor;
      doubles[0] = (knifeHoldFactor5 * knifeHoldFactor9 - knifeHoldFactor6 * knifeHoldFactor8) * knifeHoldFactor10;
      doubles[1] = (knifeHoldFactor3 * knifeHoldFactor8 - knifeHoldFactor2 * knifeHoldFactor9) * knifeHoldFactor10;
      doubles[2] = (knifeHoldFactor2 * knifeHoldFactor6 - knifeHoldFactor3 * knifeHoldFactor5) * knifeHoldFactor10;
      doubles[4] = (knifeHoldFactor6 * knifeHoldFactor7 - knifeHoldFactor4 * knifeHoldFactor9) * knifeHoldFactor10;
      doubles[5] = (knifeHoldFactor * knifeHoldFactor9 - knifeHoldFactor3 * knifeHoldFactor7) * knifeHoldFactor10;
      doubles[6] = (knifeHoldFactor3 * knifeHoldFactor4 - knifeHoldFactor * knifeHoldFactor6) * knifeHoldFactor10;
      doubles[8] = (knifeHoldFactor4 * knifeHoldFactor8 - knifeHoldFactor5 * knifeHoldFactor7) * knifeHoldFactor10;
      doubles[9] = (knifeHoldFactor2 * knifeHoldFactor7 - knifeHoldFactor * knifeHoldFactor8) * knifeHoldFactor10;
      doubles[10] = (knifeHoldFactor * knifeHoldFactor5 - knifeHoldFactor2 * knifeHoldFactor4) * knifeHoldFactor10;
      doubles[3] = -(doubles[0] * knifeHoldValues[3] + doubles[1] * knifeHoldValues[7] + doubles[2] * knifeHoldValues[11]);
      doubles[7] = -(doubles[4] * knifeHoldValues[3] + doubles[5] * knifeHoldValues[7] + doubles[6] * knifeHoldValues[11]);
      doubles[11] = -(doubles[8] * knifeHoldValues[3] + doubles[9] * knifeHoldValues[7] + doubles[10] * knifeHoldValues[11]);
      doubles[15] = 1.0;
      return doubles;
   }

   public static double[] getAmounts(double[]... doubles) {
      double[] knifeHoldValues = getAmounts();

      for (double[] doublesItem : doubles) {
         knifeHoldValues = getAmountsForDoubleArray(knifeHoldValues, doublesItem);
      }

      return knifeHoldValues;
   }

   static double[] getAmountsTable() {
      double[] knifeHoldValues = getAmounts(table);
      double[] amounts2 = getAmounts(HOLD_POSITION);
      double knifeHoldFactor = getAmount(amounts2, knifeHoldValues);
      double[] knifeHoldValues3 = getAmounts(new double[]{-(amounts2[0] - knifeHoldFactor * knifeHoldValues[0]), -(amounts2[1] - knifeHoldFactor * knifeHoldValues[1]), -(amounts2[2] - knifeHoldFactor * knifeHoldValues[2])});
      double[] amounts4 = getAmounts(knifeHoldValues, knifeHoldValues3);
      double[] doubles = new double[]{amounts4[0], knifeHoldValues[0], knifeHoldValues3[0], 0.0, amounts4[1], knifeHoldValues[1], knifeHoldValues3[1], 0.0, amounts4[2], knifeHoldValues[2], knifeHoldValues3[2], 0.0, 0.0, 0.0, 0.0, 1.0};
      double[] amounts3 = getAmounts3(doubles, HAND_OFFSET);
      doubles[3] = knifeHoldTable[0] - amounts3[0];
      doubles[7] = knifeHoldTable[1] - amounts3[1];
      doubles[11] = knifeHoldTable[2] - amounts3[2];
      return doubles;
   }

   public static double[] getAmounts(int knifeHoldValue) {
      double knifeHoldFactor = knifeHoldValue < 0 ? -1.0 : 1.0;
      return getAmounts(
         getAmounts(knifeHoldFactor * 0.64000005, -0.6, -0.71999997),
         getAmounts5(knifeHoldFactor * 45.0),
         getAmounts(knifeHoldFactor * -1.0, 3.6, 3.5),
         getAmountsDoubleArray(knifeHoldFactor * 120.0),
         getAmountsForDouble(200.0),
         getAmounts5(knifeHoldFactor * -135.0),
         getAmounts(knifeHoldFactor * 5.6, 0.0, 0.0)
      );
   }

   public static double[] getAmountsForInt(int knifeHoldValue) {
      double knifeHoldFactor = knifeHoldValue < 0 ? -1.0 : 1.0;
      return getAmounts(getAmounts(knifeHoldFactor * -5.0 / 16.0, 0.125, 0.0), getAmountsDoubleArray(Math.toDegrees(knifeHoldFactor * 0.1)));
   }

   public static double getAsDouble(double doubleLimit) {
      double doubleValue = Math.abs(doubleLimit);
      double doubleValue2 = doubleLimit > 0.0 ? 35.0 : 50.0;
      return doubleValue <= 25.0 ? doubleLimit : Math.signum(doubleLimit) * (25.0 + (doubleValue2 - 25.0) * Math.tanh((doubleValue - 25.0) / (doubleValue2 - 25.0)));
   }

   public static double[] getAmounts3(double[] knifeHoldValues, double[] doubles) {
      return new double[]{
         knifeHoldValues[0] * doubles[0] + knifeHoldValues[1] * doubles[1] + knifeHoldValues[2] * doubles[2] + knifeHoldValues[3], knifeHoldValues[4] * doubles[0] + knifeHoldValues[5] * doubles[1] + knifeHoldValues[6] * doubles[2] + knifeHoldValues[7], knifeHoldValues[8] * doubles[0] + knifeHoldValues[9] * doubles[1] + knifeHoldValues[10] * doubles[2] + knifeHoldValues[11]
      };
   }

   public static double[] getClone(double[] cloneValues) {
      double[] clone = (double[])cloneValues.clone();

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            double cloneFactor = (i == 0 ? -1 : 1) * (j == 0 ? -1 : 1);
            clone[i * 4 + j] = cloneValues[i * 4 + j] * cloneFactor;
         }
      }

      return clone;
   }
}
