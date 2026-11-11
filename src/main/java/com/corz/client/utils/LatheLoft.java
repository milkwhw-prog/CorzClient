package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.corz.client.utils.LoftLatheFlag;
import com.corz.client.utils.ShinBrr;
import com.corz.client.utils.ForeEar;
import com.corz.client.utils.AmountLoft;
import com.corz.client.utils.CorzclientWarm;
import com.corz.client.utils.ForeShinUtils;

public final class LatheLoft {
   final LoftLatheFlag latheLoftLoftLatheFlag;
   private final ShinBrr latheLoftShinBrr;
   private ForeEar foreEar;
   private ForeEar classLatheLoft;
   final float latheLoftAmount;
   final List<ForeEar> classForeEarList = new ArrayList<>();
   private final float[] rootAmounts;
   private final ArrayDeque<AmountLoft> arrayDeque;
   private final AmountLoft latheLoftAmountLoft = new AmountLoft();

   CorzclientWarm getMax(String latheLoftText, int[] latheLoftValues) {
      this.latheLoftLoftLatheFlag.updateFlag();
      return new CorzclientWarm(latheLoftText, this.latheLoftLoftLatheFlag, latheLoftValues, this.classForeEarList, this.foreEar);
   }

   private void updateRootAmounts() {
      this.latheLoftAmountLoft.handleAmountForFloatArrayInt(this.rootAmounts, 0);
   }

   LatheLoft(int latheLoftValue, int latheLoftValue2, float latheLoftAmount, boolean latheLoftFlag) {
      this.arrayDeque = new ArrayDeque<>();
      this.rootAmounts = new float[9];
      this.latheLoftAmount = latheLoftAmount;
      this.latheLoftLoftLatheFlag = new LoftLatheFlag(latheLoftValue, latheLoftValue2, latheLoftFlag);
      this.latheLoftShinBrr = new ShinBrr(this.latheLoftLoftLatheFlag.loftLatheRegion, -1, true);
      this.classLatheLoft = this.getClassForStringForeEar("root", null, 0.0F, 0.0F, 0.0F);
   }

   public ShinBrr getClassForRegion(LoftLatheFlag.Region region) {
      return new ShinBrr(region, -1, false);
   }

   public static float[] getPointsSpline(int pointsSplineBound, float... pointsSplineValues) {
      int pointsSplineValue = pointsSplineValues.length / 3;
      if (pointsSplineValue < 2) {
         throw new IllegalArgumentException("spline needs 2 points");
      } else {
         int pointsSplineFactor = pointsSplineValue - 1;
         int maxXyz = Math.max(1, pointsSplineBound);
         float[] floats = new float[3 * (pointsSplineFactor * maxXyz + 1)];
         int index = 0;

         for (int i = 0; i < pointsSplineFactor; i++) {
            int max2 = Math.max(0, i - 1);
            int pointsSplineValue3 = i;
            int pointsSplineValue4 = i + 1;
            int min5 = Math.min(pointsSplineValue - 1, i + 2);

            for (int j = 0; j < maxXyz; j++) {
               float pointsSplineFactor2 = (float)j / maxXyz;
               float pointsSplineFactor3 = pointsSplineFactor2 * pointsSplineFactor2;
               float pointsSplineFactor4 = pointsSplineFactor3 * pointsSplineFactor2;

               for (int k = 0; k < 3; k++) {
                  float pointsSplineValue6 = pointsSplineValues[3 * max2 + k];
                  float pointsSplineValue7 = pointsSplineValues[3 * pointsSplineValue3 + k];
                  float pointsSplineValue8 = pointsSplineValues[3 * pointsSplineValue4 + k];
                  float pointsSplineValue9 = pointsSplineValues[3 * min5 + k];
                  floats[index + k] = 0.5F
                     * (
                        2.0F * pointsSplineValue7
                           + (-pointsSplineValue6 + pointsSplineValue8) * pointsSplineFactor2
                           + (2.0F * pointsSplineValue6 - 5.0F * pointsSplineValue7 + 4.0F * pointsSplineValue8 - pointsSplineValue9) * pointsSplineFactor3
                           + (-pointsSplineValue6 + 3.0F * pointsSplineValue7 - 3.0F * pointsSplineValue8 + pointsSplineValue9) * pointsSplineFactor4
                     );
               }

               index += 3;
            }
         }

         floats[index] = pointsSplineValues[3 * (pointsSplineValue - 1)];
         floats[index + 1] = pointsSplineValues[3 * (pointsSplineValue - 1) + 1];
         floats[index + 2] = pointsSplineValues[3 * (pointsSplineValue - 1) + 2];
         return floats;
      }
   }

   public LatheLoft getLatheLoft(ForeEar foreEar) {
      this.foreEar = foreEar;
      return this;
   }

   public LatheLoft getLatheLoft(
      float latheLoftBound, float wrapDegreesBound, float latheLoftBound3, float hashHuntBound, float hypot, float latheLoftBound6, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, ShinBrr shinBrr4, ShinBrr shinBrr5, ShinBrr shinBrr6
   ) {
      this.updateRootAmounts();
      float minBack = Math.min(latheLoftBound, hashHuntBound);
      float maxBack = Math.max(latheLoftBound, hashHuntBound);
      float minTop = Math.min(wrapDegreesBound, hypot);
      float maxTop = Math.max(wrapDegreesBound, hypot);
      float latheLoftValue = Math.min(latheLoftBound3, latheLoftBound6);
      float max2 = Math.max(latheLoftBound3, latheLoftBound6);
      float[] floats = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F};
      if (shinBrr != null) {
         this.handle(new float[]{maxBack, maxTop, latheLoftValue, minBack, maxTop, latheLoftValue, minBack, minTop, latheLoftValue, maxBack, minTop, latheLoftValue}, floats, 0.0F, 0.0F, -1.0F, shinBrr);
      }

      if (shinBrr2 != null) {
         this.handle(new float[]{minBack, maxTop, max2, maxBack, maxTop, max2, maxBack, minTop, max2, minBack, minTop, max2}, floats, 0.0F, 0.0F, 1.0F, shinBrr2);
      }

      if (shinBrr3 != null) {
         this.handle(new float[]{maxBack, maxTop, max2, maxBack, maxTop, latheLoftValue, maxBack, minTop, latheLoftValue, maxBack, minTop, max2}, floats, 1.0F, 0.0F, 0.0F, shinBrr3);
      }

      if (shinBrr4 != null) {
         this.handle(new float[]{minBack, maxTop, latheLoftValue, minBack, maxTop, max2, minBack, minTop, max2, minBack, minTop, latheLoftValue}, floats, -1.0F, 0.0F, 0.0F, shinBrr4);
      }

      if (shinBrr5 != null) {
         this.handle(new float[]{minBack, maxTop, latheLoftValue, maxBack, maxTop, latheLoftValue, maxBack, maxTop, max2, minBack, maxTop, max2}, floats, 0.0F, 1.0F, 0.0F, shinBrr5);
      }

      if (shinBrr6 != null) {
         this.handle(new float[]{maxBack, minTop, latheLoftValue, minBack, minTop, latheLoftValue, minBack, minTop, max2, maxBack, minTop, max2}, floats, 0.0F, -1.0F, 0.0F, shinBrr6);
      }

      return this;
   }

   public LatheLoft getLoftPath(float[] loftPathValues, float[] crownForeValues, float[] loftPathValues3, int loftPathFactor, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3, float loftPathValue, float loftPathValue2, float loftPathValue3) {
      int index = loftPathValues.length / 3 - 1;
      if (index >= 1 && crownForeValues.length == index + 1 && loftPathValues3.length == index + 1) {
         int maxRound = Math.max(3, Math.round(loftPathFactor * this.latheLoftAmount));
         float[] floats = new float[3 * (index + 1)];
         float[] floats2 = new float[3 * (index + 1)];
         float[] floats3 = new float[3 * (index + 1)];

         for (int i = 0; i <= index; i++) {
            int max4 = Math.max(0, i - 1);
            int min5 = Math.min(index, i + 1);
            float loftPathFactor2 = loftPathValues[3 * min5] - loftPathValues[3 * max4];
            float loftPathFactor3 = loftPathValues[3 * min5 + 1] - loftPathValues[3 * max4 + 1];
            float loftPathFactor4 = loftPathValues[3 * min5 + 2] - loftPathValues[3 * max4 + 2];
            float distance = (float)Math.sqrt(loftPathFactor2 * loftPathFactor2 + loftPathFactor3 * loftPathFactor3 + loftPathFactor4 * loftPathFactor4);
            if (distance < 1.0E-6F) {
               loftPathFactor2 = 0.0F;
               loftPathFactor3 = 1.0F;
               loftPathFactor4 = 0.0F;
               distance = 1.0F;
            }

            floats[3 * i] = loftPathFactor2 / distance;
            floats[3 * i + 1] = loftPathFactor3 / distance;
            floats[3 * i + 2] = loftPathFactor4 / distance;
         }

         float startCap2 = loftPathValue;
         float loftPathFactor5 = loftPathValue2;
         float path2 = loftPathValue3;

         for (int j = 0; j <= index; j++) {
            float loftPathFactor6 = floats[3 * j];
            float planValuesItem = floats[3 * j + 1];
            float loftPathFactor8 = floats[3 * j + 2];
            float loftPathFactor9 = startCap2 * loftPathFactor6 + loftPathFactor5 * planValuesItem + path2 * loftPathFactor8;
            float loftPathFactor10 = startCap2 - loftPathFactor9 * loftPathFactor6;
            float loftPathFactor11 = loftPathFactor5 - loftPathFactor9 * planValuesItem;
            float loftPathFactor12 = path2 - loftPathFactor9 * loftPathFactor8;
            float sqrt2 = (float)Math.sqrt(loftPathFactor10 * loftPathFactor10 + loftPathFactor11 * loftPathFactor11 + loftPathFactor12 * loftPathFactor12);
            if (sqrt2 < 1.0E-4F) {
               float[] loftPathValues4 = Math.abs(planValuesItem) < 0.9F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{0.0F, 0.0F, -1.0F};
               float loftPathFactor13 = loftPathValues4[0] * loftPathFactor6 + loftPathValues4[1] * planValuesItem + loftPathValues4[2] * loftPathFactor8;
               loftPathFactor10 = loftPathValues4[0] - loftPathFactor13 * loftPathFactor6;
               loftPathFactor11 = loftPathValues4[1] - loftPathFactor13 * planValuesItem;
               loftPathFactor12 = loftPathValues4[2] - loftPathFactor13 * loftPathFactor8;
               sqrt2 = (float)Math.sqrt(loftPathFactor10 * loftPathFactor10 + loftPathFactor11 * loftPathFactor11 + loftPathFactor12 * loftPathFactor12);
            }

            startCap2 = loftPathFactor10 / sqrt2;
            loftPathFactor5 = loftPathFactor11 / sqrt2;
            path2 = loftPathFactor12 / sqrt2;
            floats2[3 * j] = startCap2;
            floats2[3 * j + 1] = loftPathFactor5;
            floats2[3 * j + 2] = path2;
            floats3[3 * j] = planValuesItem * path2 - loftPathFactor8 * loftPathFactor5;
            floats3[3 * j + 1] = loftPathFactor8 * startCap2 - loftPathFactor6 * path2;
            floats3[3 * j + 2] = loftPathFactor6 * loftPathFactor5 - planValuesItem * startCap2;
         }

         float[] floats4 = new float[index + 1];

         for (int k = 1; k <= index; k++) {
            float loftPathFactor14 = loftPathValues[3 * k] - loftPathValues[3 * k - 3];
            float loftPathFactor15 = loftPathValues[3 * k + 1] - loftPathValues[3 * k - 2];
            float loftPathFactor16 = loftPathValues[3 * k + 2] - loftPathValues[3 * k - 1];
            floats4[k] = floats4[k - 1] + (float)Math.sqrt(loftPathFactor14 * loftPathFactor14 + loftPathFactor15 * loftPathFactor15 + loftPathFactor16 * loftPathFactor16);
         }

         float loftPathDivisor = floats4[index] > 0.0F ? floats4[index] : 1.0F;
         float[] floats5 = new float[maxRound + 1];
         float[] floats6 = new float[index + 1];

         for (int p = 0; p <= index; p++) {
            floats6[p] = floats4[p] / loftPathDivisor;
         }

         float[] latheLoftValues = new float[3 * (maxRound + 1) * (index + 1)];

         for (int q = 0; q <= maxRound; q++) {
            floats5[q] = (float)q / maxRound;
            float angle = (float) (Math.PI * 2) * (floats5[q] - 0.5F);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);

            for (int r = 0; r <= index; r++) {
               int loftPathValue6 = 3 * (r * (maxRound + 1) + q);

               for (int s = 0; s < 3; s++) {
                  latheLoftValues[loftPathValue6 + s] = loftPathValues[3 * r + s]
                     + floats2[3 * r + s] * loftPathValues3[r] * cos
                     + floats3[3 * r + s] * crownForeValues[r] * sin;
               }
            }
         }

         this.handle(latheLoftValues, maxRound, index, floats5, floats6, true, false, shinBrr);
         if (shinBrr2 != null) {
            this.handle(loftPathValues, floats2, floats3, floats, crownForeValues, loftPathValues3, 0, maxRound, -1.0F, shinBrr2);
         }

         if (shinBrr3 != null) {
            this.handle(loftPathValues, floats2, floats3, floats, crownForeValues, loftPathValues3, index, maxRound, 1.0F, shinBrr3);
         }

         return this;
      } else {
         throw new IllegalArgumentException("loft path");
      }
   }

   private void handle(float[] latheLoftValues, float[] floats, float latheLoftValue, float latheLoftValue2, float latheLoftValue3, ShinBrr shinBrr) {
      for (int i = 0; i < 4; i++) {
         this.handleRootAmounts(latheLoftValues[i * 3], latheLoftValues[i * 3 + 1], latheLoftValues[i * 3 + 2], shinBrr.getFlagLoftRegion(floats[i * 2]), shinBrr.getFlag(floats[i * 2 + 1]), latheLoftValue, latheLoftValue2, latheLoftValue3, shinBrr.tintColor);
      }
   }

   private void handleRootAmounts(float rootValue, float rootValue2, float minSide, float flagLoftRegion, float rootValue5, float rootFactor, float rootFactor2, float rootFactor3, int tintColor) {
      float loftLatheValue = this.latheLoftAmountLoft.getAmountMatrixM00(rootValue, rootValue2, minSide);
      float amountMatrixM10 = this.latheLoftAmountLoft.getAmountMatrixM10(rootValue, rootValue2, minSide);
      float rootValue9 = this.latheLoftAmountLoft.getAmount(rootValue, rootValue2, minSide);
      float loftLatheFactor = this.rootAmounts[0] * rootFactor + this.rootAmounts[1] * rootFactor2 + this.rootAmounts[2] * rootFactor3;
      float rootValue11 = this.rootAmounts[3] * rootFactor + this.rootAmounts[4] * rootFactor2 + this.rootAmounts[5] * rootFactor3;
      float rootValue12 = this.rootAmounts[6] * rootFactor + this.rootAmounts[7] * rootFactor2 + this.rootAmounts[8] * rootFactor3;
      this.classLatheLoft.rootTransform.handle(loftLatheValue, amountMatrixM10, rootValue9, flagLoftRegion, rootValue5, loftLatheFactor, rootValue11, rootValue12, tintColor);
   }

   private static boolean canFlag(float latheLoftValue, float latheLoftValue2, float[] latheLoftValues, int latheLoftValue3, int integer, int latheLoftValue5) {
      float latheLoftLimit = getAmount(latheLoftValue, latheLoftValue2, latheLoftValues[2 * latheLoftValue3], latheLoftValues[2 * latheLoftValue3 + 1], latheLoftValues[2 * integer], latheLoftValues[2 * integer + 1]);
      float latheLoftLimit2 = getAmount(latheLoftValue, latheLoftValue2, latheLoftValues[2 * integer], latheLoftValues[2 * integer + 1], latheLoftValues[2 * latheLoftValue5], latheLoftValues[2 * latheLoftValue5 + 1]);
      float latheLoftLimit3 = getAmount(latheLoftValue, latheLoftValue2, latheLoftValues[2 * latheLoftValue5], latheLoftValues[2 * latheLoftValue5 + 1], latheLoftValues[2 * latheLoftValue3], latheLoftValues[2 * latheLoftValue3 + 1]);
      boolean latheLoftFlag = latheLoftLimit < 0.0F || latheLoftLimit2 < 0.0F || latheLoftLimit3 < 0.0F;
      boolean latheLoftFlag2 = latheLoftLimit > 0.0F || latheLoftLimit2 > 0.0F || latheLoftLimit3 > 0.0F;
      return !latheLoftFlag || !latheLoftFlag2;
   }

   CorzclientWarm getClassForString(String classText) {
      return this.getMax(classText, null);
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float latheLoftValue2, float armTrunkValue) {
      this.latheLoftAmountLoft.getAmountTranslateX(latheLoftValue, latheLoftValue2, armTrunkValue);
      return this;
   }

   private void handle(float[] latheLoftValues, int latheLoftLimit, int latheLoftBound, float[] latheLoftValues2, float[] latheLoftValues3, boolean latheLoftFlag, boolean latheLoftFlag2, ShinBrr shinBrr) {
      int latheLoftFactor = latheLoftLimit + 1;
      int latheLoftDivisor = latheLoftFactor * (latheLoftBound + 1);
      float[] floats = new float[3 * latheLoftDivisor];
      float latheLoftTotal = 0.0F;
      float latheLoftTotal2 = 0.0F;
      float latheLoftTotal3 = 0.0F;

      for (int i = 0; i < latheLoftDivisor; i++) {
         latheLoftTotal += latheLoftValues[3 * i];
         latheLoftTotal2 += latheLoftValues[3 * i + 1];
         latheLoftTotal3 += latheLoftValues[3 * i + 2];
      }

      float latheLoftValue = latheLoftTotal / latheLoftDivisor;
      float latheLoftValue2 = latheLoftTotal2 / latheLoftDivisor;
      float latheLoftValue3 = latheLoftTotal3 / latheLoftDivisor;
      float latheLoftTotal4 = 0.0F;

      for (int j = 0; j <= latheLoftBound; j++) {
         for (int k = 0; k <= latheLoftLimit; k++) {
            float[] amounts4 = getAmounts(latheLoftValues, k, j, latheLoftLimit, latheLoftBound, latheLoftFlag);
            if (amounts4[0] * amounts4[0] + amounts4[1] * amounts4[1] + amounts4[2] * amounts4[2] < 1.0E-10F) {
               int latheLoftValue4 = j < latheLoftBound ? j + 1 : j - 1;
               if (j > 0 && j < latheLoftBound) {
                  latheLoftValue4 = j + 1;
               }

               amounts4 = getAmounts(latheLoftValues, k, latheLoftValue4, latheLoftLimit, latheLoftBound, latheLoftFlag);
            }

            float[] amountsForFloatArrayInt = getAmountsForFloatArrayInt(latheLoftValues, k, j, latheLoftLimit, latheLoftBound, latheLoftFlag2);
            float latheLoftFactor2 = amounts4[1] * amountsForFloatArrayInt[2] - amounts4[2] * amountsForFloatArrayInt[1];
            float latheLoftFactor3 = amounts4[2] * amountsForFloatArrayInt[0] - amounts4[0] * amountsForFloatArrayInt[2];
            float latheLoftFactor4 = amounts4[0] * amountsForFloatArrayInt[1] - amounts4[1] * amountsForFloatArrayInt[0];
            int index = 3 * (j * latheLoftFactor + k);
            floats[index] = latheLoftFactor2;
            floats[index + 1] = latheLoftFactor3;
            floats[index + 2] = latheLoftFactor4;
            float latheLoftLimit3 = latheLoftFactor2 * (latheLoftValues[index] - latheLoftValue) + latheLoftFactor3 * (latheLoftValues[index + 1] - latheLoftValue2) + latheLoftFactor4 * (latheLoftValues[index + 2] - latheLoftValue3);
            latheLoftTotal4 += latheLoftLimit3 > 0.0F ? 1.0F : (latheLoftLimit3 < 0.0F ? -1.0F : 0.0F);
         }
      }

      float latheLoftFactor5 = latheLoftTotal4 < 0.0F ? -1.0F : 1.0F;
      this.updateRootAmounts();
      int[] ints = new int[4];

      for (int q = 0; q < latheLoftBound; q++) {
         for (int r = 0; r < latheLoftLimit; r++) {
            ints[0] = q * latheLoftFactor + r;
            ints[1] = q * latheLoftFactor + r + 1;
            ints[2] = (q + 1) * latheLoftFactor + r + 1;
            ints[3] = (q + 1) * latheLoftFactor + r;

            for (int s = 0; s < 4; s++) {
               int latheLoftValue5 = ints[s];
               int index2 = 3 * latheLoftValue5;
               int index3 = latheLoftValue5 % latheLoftFactor;
               int index4 = latheLoftValue5 / latheLoftFactor;
               this.handleRootAmounts(
                  latheLoftValues[index2],
                  latheLoftValues[index2 + 1],
                  latheLoftValues[index2 + 2],
                  shinBrr.getFlagLoftRegion(latheLoftValues2[index3]),
                  shinBrr.getFlag(latheLoftValues3[index4]),
                  floats[index2] * latheLoftFactor5,
                  floats[index2 + 1] * latheLoftFactor5,
                  floats[index2 + 2] * latheLoftFactor5,
                  shinBrr.tintColor
               );
            }
         }
      }
   }

   public LatheLoft getLatheLoftForFloat(float latheLoftValue, float latheLoftValue2, float latheLoftValue3) {
      this.latheLoftAmountLoft.getAmountAmountLoft(latheLoftValue, latheLoftValue2, latheLoftValue3);
      return this;
   }

   private static float[] getAmounts(float[] latheLoftValues, int latheLoftValue, int latheLoftFactor, int latheLoftBound, int latheLoftValue2, boolean latheLoftFlag) {
      int latheLoftFactor2 = latheLoftBound + 1;
      int latheLoftValue3;
      int latheLoftValue4;
      if (latheLoftFlag) {
         int latheLoftValue5 = latheLoftValue % latheLoftBound;
         latheLoftValue3 = (latheLoftValue5 - 1 + latheLoftBound) % latheLoftBound;
         latheLoftValue4 = (latheLoftValue5 + 1) % latheLoftBound;
      } else {
         latheLoftValue3 = Math.max(0, latheLoftValue - 1);
         latheLoftValue4 = Math.min(latheLoftBound, latheLoftValue + 1);
      }

      int index = 3 * (latheLoftFactor * latheLoftFactor2 + latheLoftValue3);
      int index2 = 3 * (latheLoftFactor * latheLoftFactor2 + latheLoftValue4);
      return new float[]{latheLoftValues[index2] - latheLoftValues[index], latheLoftValues[index2 + 1] - latheLoftValues[index + 1], latheLoftValues[index2 + 2] - latheLoftValues[index + 2]};
   }

   public LatheLoft getLatheLoft(float degrees) {
      this.latheLoftAmountLoft.getAmount3((float)Math.toRadians(degrees));
      return this;
   }

   private static float[] getAmountsForFloatArrayInt(float[] latheLoftValues, int latheLoftValue, int j, int latheLoftLimit, int latheLoftBound, boolean latheLoftFlag) {
      int latheLoftFactor = latheLoftLimit + 1;
      int latheLoftFactor2;
      int latheLoftFactor3;
      if (latheLoftFlag) {
         int latheLoftValue4 = j % latheLoftBound;
         latheLoftFactor2 = (latheLoftValue4 - 1 + latheLoftBound) % latheLoftBound;
         latheLoftFactor3 = (latheLoftValue4 + 1) % latheLoftBound;
      } else {
         latheLoftFactor2 = Math.max(0, j - 1);
         latheLoftFactor3 = Math.min(latheLoftBound, j + 1);
      }

      int index = 3 * (latheLoftFactor2 * latheLoftFactor + latheLoftValue);
      int index2 = 3 * (latheLoftFactor3 * latheLoftFactor + latheLoftValue);
      return new float[]{latheLoftValues[index2] - latheLoftValues[index], latheLoftValues[index2 + 1] - latheLoftValues[index + 1], latheLoftValues[index2 + 2] - latheLoftValues[index + 2]};
   }

   public LatheLoft getLatheLoftLatheLoftAmountLoft(float degrees) {
      this.latheLoftAmountLoft.getAmountForFloat((float)Math.toRadians(degrees));
      return this;
   }

   public LatheLoft getLatheLoft3(float latheLoftValue) {
      this.latheLoftAmountLoft.getAmountAmountLoft(latheLoftValue, latheLoftValue, latheLoftValue);
      return this;
   }

   public LatheLoft getLatheLoftForForeEar(ForeEar foreEar) {
      this.classLatheLoft = foreEar;
      return this;
   }

   public LatheLoft getLatheLoft4(float degrees) {
      this.latheLoftAmountLoft.getAmount((float)Math.toRadians(degrees));
      return this;
   }

   public ForeEar getClassForStringForeEar(String classText, ForeEar foreEar, float classValue, float shinFootValue, float classValue3) {
      if (!this.classForeEarList.isEmpty() && foreEar == null) {
         foreEar = this.classForeEarList.get(0);
      }

      ForeEar groupParseData = new ForeEar(this.classForeEarList.size(), classText, foreEar, classValue, shinFootValue, classValue3);
      this.classForeEarList.add(groupParseData);
      return groupParseData;
   }

   public LatheLoft getLatheLoft(float[] latheLoftValues, float[] tailKatanaValues, int latheLoftValue, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      return this.getLatheProfile(latheLoftValues, tailKatanaValues, latheLoftValue, 1.0F, 1.0F, shinBrr, shinBrr2, shinBrr3);
   }

   private static float getAmount(float latheLoftValue, float latheLoftValue2, float latheLoftValue3, float latheLoftValue4, float latheLoftValue5, float latheLoftValue6) {
      return (latheLoftValue - latheLoftValue5) * (latheLoftValue4 - latheLoftValue6) - (latheLoftValue3 - latheLoftValue5) * (latheLoftValue2 - latheLoftValue6);
   }

   private static int[] getValues(float[] latheLoftValues, boolean latheLoftFlag) {
      int latheLoftFactor = latheLoftValues.length / 2;
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < latheLoftFactor; i++) {
         arrayList.add(i);
      }

      int[] ints = new int[3 * (latheLoftFactor - 2)];
      int latheLoftCounter = 0;
      int latheLoftCounter2 = 0;

      label67:
      while (arrayList.size() > 3 && latheLoftCounter2++ < latheLoftFactor * latheLoftFactor) {
         boolean latheLoftFlag2 = false;

         for (int j = 0; j < arrayList.size(); j++) {
            int latheLoftValue = (Integer)arrayList.get((j + arrayList.size() - 1) % arrayList.size());
            int integer = (Integer)arrayList.get(j);
            int latheLoftValue3 = (Integer)arrayList.get((j + 1) % arrayList.size());
            float latheLoftLimit = (latheLoftValues[2 * integer] - latheLoftValues[2 * latheLoftValue]) * (latheLoftValues[2 * latheLoftValue3 + 1] - latheLoftValues[2 * latheLoftValue + 1])
               - (latheLoftValues[2 * integer + 1] - latheLoftValues[2 * latheLoftValue + 1]) * (latheLoftValues[2 * latheLoftValue3] - latheLoftValues[2 * latheLoftValue]);
            if (latheLoftFlag ? !(latheLoftLimit <= 0.0F) : !(latheLoftLimit >= 0.0F)) {
               boolean latheLoftFlag3 = true;

               for (int latheLoftValue4 : (Iterable<Integer>) (arrayList)) {
                  if (latheLoftValue4 != latheLoftValue && latheLoftValue4 != integer && latheLoftValue4 != latheLoftValue3 && canFlag(latheLoftValues[2 * latheLoftValue4], latheLoftValues[2 * latheLoftValue4 + 1], latheLoftValues, latheLoftValue, integer, latheLoftValue3)) {
                     latheLoftFlag3 = false;
                     break;
                  }
               }

               if (latheLoftFlag3) {
                  ints[latheLoftCounter++] = latheLoftValue;
                  ints[latheLoftCounter++] = integer;
                  ints[latheLoftCounter++] = latheLoftValue3;
                  arrayList.remove(j);
                  latheLoftFlag2 = true;
                  if (!latheLoftFlag2) {
                     int latheLoftValue5 = (Integer)arrayList.get(0);
                     int latheLoftValue6 = (Integer)arrayList.get(1);
                     int latheLoftValue7 = (Integer)arrayList.get(2);
                     ints[latheLoftCounter++] = latheLoftValue5;
                     ints[latheLoftCounter++] = latheLoftValue6;
                     ints[latheLoftCounter++] = latheLoftValue7;
                     arrayList.remove(1);
                  }
                  continue label67;
               }
            }
         }
         break;
      }

      if (arrayList.size() == 3) {
         ints[latheLoftCounter++] = (Integer)arrayList.get(0);
         ints[latheLoftCounter++] = (Integer)arrayList.get(1);
         ints[latheLoftCounter++] = (Integer)arrayList.get(2);
      }

      return Arrays.copyOf(ints, latheLoftCounter);
   }

   public LatheLoft getDequeArray() {
      this.latheLoftAmountLoft.getAmount(this.arrayDeque.pop());
      return this;
   }

   public LatheLoft getLoftLathe(float loftLatheValue, float loftLatheValue2, float loftLatheValue3, float loftLatheValue4, float loftLatheFactor, int loftLatheFactor2, int loftLatheFactor3, ShinBrr shinBrr) {
      int maxRound = Math.max(3, Math.round(loftLatheFactor2 * this.latheLoftAmount));
      int latheLoftBound = Math.max(3, Math.round(loftLatheFactor3 * this.latheLoftAmount));
      float[] floats = new float[3 * (maxRound + 1) * (latheLoftBound + 1)];
      float[] floats2 = new float[maxRound + 1];
      float[] floats3 = new float[latheLoftBound + 1];

      for (int i = 0; i <= latheLoftBound; i++) {
         floats3[i] = (float)i / latheLoftBound;
      }

      for (int j = 0; j <= maxRound; j++) {
         floats2[j] = (float)j / maxRound;
         float angle = (float) (Math.PI * 2) * (floats2[j] - 0.5F);
         float sin = (float)Math.sin(angle);
         float cos = (float)Math.cos(angle);

         for (int k = 0; k <= latheLoftBound; k++) {
            float angle2 = (float) (Math.PI * 2) * floats3[k];
            float loftLatheFactor4 = loftLatheValue4 + loftLatheFactor * (float)Math.cos(angle2);
            int index = 3 * (k * (maxRound + 1) + j);
            floats[index] = loftLatheValue - loftLatheFactor4 * sin;
            floats[index + 1] = loftLatheValue2 + loftLatheFactor * (float)Math.sin(angle2);
            floats[index + 2] = loftLatheValue3 - loftLatheFactor4 * cos;
         }
      }

      this.handle(floats, maxRound, latheLoftBound, floats2, floats3, true, true, shinBrr);
      return this;
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float wrapDegreesBound, float latheLoftValue3, float latheLoftValue4, float hypot, float hashHuntBound, ShinBrr shinBrr) {
      return this.getLatheLoft(latheLoftValue, wrapDegreesBound, latheLoftValue3, latheLoftValue4, hypot, hashHuntBound, shinBrr, shinBrr, shinBrr, shinBrr, shinBrr, shinBrr);
   }

   public ShinBrr getClassShinBrr(LoftLatheFlag.Region region) {
      return new ShinBrr(region, -1, true);
   }

   public LatheLoft getLatheLoft(float[] latheLoftValues, float[] crownForeValues, int latheLoftValue, ShinBrr shinBrr, boolean latheLoftFlag) {
      return this.getLoftPath(latheLoftValues, crownForeValues, crownForeValues, latheLoftValue, shinBrr, latheLoftFlag ? shinBrr : null, latheLoftFlag ? shinBrr : null, 0.0F, 0.0F, -1.0F);
   }

   public LatheLoft getLoftLathe(float loftLatheValue, float eyeValue, float armTrunkValue, float loftLatheFactor, float loftLatheFactor2, float loftLatheFactor3, int loftLatheFactor4, float loftLatheBound, float hashHuntBound, ShinBrr shinBrr) {
      int maxRound = Math.max(4, Math.round(loftLatheFactor4 * this.latheLoftAmount));
      int latheLoftBound = Math.max(2, Math.round(maxRound * 0.5F * Math.abs(hashHuntBound - loftLatheBound)));
      float minSeg = Math.min(loftLatheBound, hashHuntBound);
      float maxSeg = Math.max(loftLatheBound, hashHuntBound);
      float[] floats = new float[latheLoftBound + 1];
      float[] floats2 = new float[latheLoftBound + 1];

      for (int i = 0; i <= latheLoftBound; i++) {
         float loftLatheFactor5 = maxSeg + (minSeg - maxSeg) * i / latheLoftBound;
         float angle = (float) Math.PI * loftLatheFactor5;
         floats[i] = (float)Math.sin(angle);
         floats2[i] = eyeValue + loftLatheFactor2 * (float)Math.cos(angle);
      }

      this.getDequeArrayArrayDeque();
      this.latheLoftAmountLoft.getAmountTranslateX(loftLatheValue, 0.0F, armTrunkValue);
      float[] floats3 = new float[maxRound + 1];
      float[] floats4 = new float[latheLoftBound + 1];
      float[] latheLoftValues = new float[3 * (maxRound + 1) * (latheLoftBound + 1)];

      for (int j = 0; j <= latheLoftBound; j++) {
         floats4[j] = 1.0F - (float)j / latheLoftBound;
      }

      for (int k = 0; k <= maxRound; k++) {
         floats3[k] = (float)k / maxRound;
         float angle2 = (float) (Math.PI * 2) * (floats3[k] - 0.5F);
         float sin = (float)Math.sin(angle2);
         float cos = (float)Math.cos(angle2);

         for (int p = 0; p <= latheLoftBound; p++) {
            int index = 3 * (p * (maxRound + 1) + k);
            latheLoftValues[index] = -floats[p] * sin * loftLatheFactor;
            latheLoftValues[index + 1] = floats2[p];
            latheLoftValues[index + 2] = -floats[p] * cos * loftLatheFactor3;
         }
      }

      this.handle(latheLoftValues, maxRound, latheLoftBound, floats3, floats4, true, false, shinBrr);
      this.getDequeArray();
      return this;
   }

   LatheLoft(int latheLoftValue, int latheLoftValue2, float latheLoftValue3) {
      this(latheLoftValue, latheLoftValue2, latheLoftValue3, false);
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float eyeValue, float dinForeValue, float loftLatheFactor, int latheLoftValue5, ShinBrr shinBrr) {
      return this.getLatheLoft(latheLoftValue, eyeValue, dinForeValue, loftLatheFactor, loftLatheFactor, loftLatheFactor, latheLoftValue5, shinBrr);
   }

   public LoftLatheFlag.Region getMax(int latheLoftValue, int round) {
      return this.latheLoftLoftLatheFlag.getMax(latheLoftValue, round);
   }

   public LatheLoft getPolygonExtrude(float[] polygonExtrudeValues, float polygonExtrudeBound, float hashHuntBound, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      int polygonExtrudeLimit = polygonExtrudeValues.length / 2;
      if (polygonExtrudeLimit < 3) {
         throw new IllegalArgumentException("extrude polygon");
      } else {
         float polygonExtrudeTotal = 0.0F;
         float maxValue = Float.MAX_VALUE;
         float wrapDegreesBound = Float.MAX_VALUE;
         float maxValue5 = -Float.MAX_VALUE;
         float pilotStatusBound = -Float.MAX_VALUE;
         int polygonExtrudeCounter = 0;

         for (int i = polygonExtrudeLimit - 1; polygonExtrudeCounter < polygonExtrudeLimit; i = polygonExtrudeCounter++) {
            polygonExtrudeTotal += polygonExtrudeValues[2 * i] * polygonExtrudeValues[2 * polygonExtrudeCounter + 1] - polygonExtrudeValues[2 * polygonExtrudeCounter] * polygonExtrudeValues[2 * i + 1];
            maxValue = Math.min(maxValue, polygonExtrudeValues[2 * polygonExtrudeCounter]);
            maxValue5 = Math.max(maxValue5, polygonExtrudeValues[2 * polygonExtrudeCounter]);
            wrapDegreesBound = Math.min(wrapDegreesBound, polygonExtrudeValues[2 * polygonExtrudeCounter + 1]);
            pilotStatusBound = Math.max(pilotStatusBound, polygonExtrudeValues[2 * polygonExtrudeCounter + 1]);
         }

         boolean polygonExtrudeFlag = polygonExtrudeTotal > 0.0F;
         float polygonExtrudeDivisor = Math.max(1.0E-4F, maxValue5 - maxValue);
         float max2 = Math.max(1.0E-4F, pilotStatusBound - wrapDegreesBound);
         int[] values2 = getValues(polygonExtrudeValues, polygonExtrudeFlag);
         this.updateRootAmounts();
         float minSide = Math.min(polygonExtrudeBound, hashHuntBound);
         float maxSide = Math.max(polygonExtrudeBound, hashHuntBound);

         for (int j = 0; j < values2.length; j += 3) {
            if (shinBrr != null) {
               for (int k = 0; k < 4; k++) {
                  int polygonExtrudeValue = values2[j + Math.min(k, 2)];
                  float polygonExtrudeValue2 = polygonExtrudeValues[2 * polygonExtrudeValue];
                  float polygonExtrudeValue3 = polygonExtrudeValues[2 * polygonExtrudeValue + 1];
                  this.handleRootAmounts(
                     polygonExtrudeValue2, polygonExtrudeValue3, minSide, shinBrr.getFlagLoftRegion((maxValue5 - polygonExtrudeValue2) / polygonExtrudeDivisor), shinBrr.getFlag((pilotStatusBound - polygonExtrudeValue3) / max2), 0.0F, 0.0F, -1.0F, shinBrr.tintColor
                  );
               }
            }

            if (shinBrr2 != null && maxSide > minSide) {
               for (int m = 0; m < 4; m++) {
                  int polygonExtrudeValue4 = values2[j + Math.min(m, 2)];
                  float polygonExtrudeValue5 = polygonExtrudeValues[2 * polygonExtrudeValue4];
                  float polygonExtrudeValue6 = polygonExtrudeValues[2 * polygonExtrudeValue4 + 1];
                  this.handleRootAmounts(
                     polygonExtrudeValue5, polygonExtrudeValue6, maxSide, shinBrr2.getFlagLoftRegion((polygonExtrudeValue5 - maxValue) / polygonExtrudeDivisor), shinBrr2.getFlag((pilotStatusBound - polygonExtrudeValue6) / max2), 0.0F, 0.0F, 1.0F, shinBrr2.tintColor
                  );
               }
            }
         }

         if (shinBrr3 != null && maxSide > minSide) {
            float polygonExtrudeTotal2 = 0.0F;

            for (int p = 0; p < polygonExtrudeLimit; p++) {
               int polygonExtrudeValue7 = (p + 1) % polygonExtrudeLimit;
               polygonExtrudeTotal2 += (float)Math.hypot(polygonExtrudeValues[2 * polygonExtrudeValue7] - polygonExtrudeValues[2 * p], polygonExtrudeValues[2 * polygonExtrudeValue7 + 1] - polygonExtrudeValues[2 * p + 1]);
            }

            float polygonExtrudeTotal3 = 0.0F;

            for (int q = 0; q < polygonExtrudeLimit; q++) {
               int polygonExtrudeValue8 = (q + 1) % polygonExtrudeLimit;
               float polygonExtrudeValue9 = polygonExtrudeValues[2 * q];
               float polygonExtrudeValue10 = polygonExtrudeValues[2 * q + 1];
               float rootValue = polygonExtrudeValues[2 * polygonExtrudeValue8];
               float polygonExtrudeValue12 = polygonExtrudeValues[2 * polygonExtrudeValue8 + 1];
               float deltaVar = rootValue - polygonExtrudeValue9;
               float deltaZ = polygonExtrudeValue12 - polygonExtrudeValue10;
               float hypot = (float)Math.hypot(deltaVar, deltaZ);
               if (!(hypot < 1.0E-6F)) {
                  float rootFactor = polygonExtrudeFlag ? deltaZ : -deltaZ;
                  float polygonExtrudeValue14 = polygonExtrudeFlag ? -deltaVar : deltaVar;
                  float polygonExtrudeValue15 = polygonExtrudeTotal3 / polygonExtrudeTotal2;
                  float shinBrrValue = (polygonExtrudeTotal3 + hypot) / polygonExtrudeTotal2;
                  polygonExtrudeTotal3 += hypot;
                  this.handleRootAmounts(polygonExtrudeValue9, polygonExtrudeValue10, minSide, shinBrr3.getFlagLoftRegion(polygonExtrudeValue15), shinBrr3.getFlag(0.0F), rootFactor, polygonExtrudeValue14, 0.0F, shinBrr3.tintColor);
                  this.handleRootAmounts(rootValue, polygonExtrudeValue12, minSide, shinBrr3.getFlagLoftRegion(shinBrrValue), shinBrr3.getFlag(0.0F), rootFactor, polygonExtrudeValue14, 0.0F, shinBrr3.tintColor);
                  this.handleRootAmounts(rootValue, polygonExtrudeValue12, maxSide, shinBrr3.getFlagLoftRegion(shinBrrValue), shinBrr3.getFlag(1.0F), rootFactor, polygonExtrudeValue14, 0.0F, shinBrr3.tintColor);
                  this.handleRootAmounts(polygonExtrudeValue9, polygonExtrudeValue10, maxSide, shinBrr3.getFlagLoftRegion(polygonExtrudeValue15), shinBrr3.getFlag(1.0F), rootFactor, polygonExtrudeValue14, 0.0F, shinBrr3.tintColor);
               }
            }
         }

         return this;
      }
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float latheLoftValue2, float minSide, float rootValue, float latheLoftValue5, float latheLoftValue6, float latheLoftValue7, float latheLoftValue8, float latheLoftValue9, ShinBrr shinBrr) {
      this.updateRootAmounts();
      float latheLoftFactor = rootValue - latheLoftValue;
      float latheLoftFactor2 = latheLoftValue5 - latheLoftValue2;
      float latheLoftFactor3 = latheLoftValue6 - minSide;
      float latheLoftFactor4 = latheLoftValue7 - latheLoftValue;
      float latheLoftFactor5 = latheLoftValue8 - latheLoftValue2;
      float latheLoftFactor6 = latheLoftValue9 - minSide;
      float rootFactor = latheLoftFactor2 * latheLoftFactor6 - latheLoftFactor3 * latheLoftFactor5;
      float latheLoftValue11 = latheLoftFactor3 * latheLoftFactor4 - latheLoftFactor * latheLoftFactor6;
      float latheLoftValue12 = latheLoftFactor * latheLoftFactor5 - latheLoftFactor2 * latheLoftFactor4;
      this.handleRootAmounts(latheLoftValue, latheLoftValue2, minSide, shinBrr.getFlagLoftRegion(0.0F), shinBrr.getFlag(0.0F), rootFactor, latheLoftValue11, latheLoftValue12, shinBrr.tintColor);
      this.handleRootAmounts(rootValue, latheLoftValue5, latheLoftValue6, shinBrr.getFlagLoftRegion(1.0F), shinBrr.getFlag(0.0F), rootFactor, latheLoftValue11, latheLoftValue12, shinBrr.tintColor);
      this.handleRootAmounts(latheLoftValue7, latheLoftValue8, latheLoftValue9, shinBrr.getFlagLoftRegion(1.0F), shinBrr.getFlag(1.0F), rootFactor, latheLoftValue11, latheLoftValue12, shinBrr.tintColor);
      this.handleRootAmounts(latheLoftValue7, latheLoftValue8, latheLoftValue9, shinBrr.getFlagLoftRegion(1.0F), shinBrr.getFlag(1.0F), rootFactor, latheLoftValue11, latheLoftValue12, shinBrr.tintColor);
      return this;
   }

   public static float getAmount(float latheLoftValue, float latheLoftDivisor) {
      double asin = Math.asin(Math.max(-1.0F, Math.min(1.0F, latheLoftValue / latheLoftDivisor)));
      return (float)(0.5 - asin / (Math.PI * 2));
   }

   public LatheLoft getLatheProfile(float[] latheProfileValues, float[] tailKatanaValues, int latheProfileFactor, float latheProfileFactor2, float latheProfileFactor3, ShinBrr shinBrr, ShinBrr shinBrr2, ShinBrr shinBrr3) {
      int index = latheProfileValues.length - 1;
      if (index >= 1 && tailKatanaValues.length == latheProfileValues.length) {
         int maxRound = Math.max(3, Math.round(latheProfileFactor * this.latheLoftAmount));
         float[] floats = new float[3 * (maxRound + 1) * (index + 1)];
         float[] floats2 = new float[maxRound + 1];
         float[] floats3 = new float[index + 1];
         float[] floats4 = new float[index + 1];
         float latheProfileFactor4 = 0.5F * (latheProfileFactor2 + latheProfileFactor3);

         for (int i = 1; i <= index; i++) {
            float latheProfileFactor5 = (latheProfileValues[i] - latheProfileValues[i - 1]) * latheProfileFactor4;
            float latheProfileFactor6 = tailKatanaValues[i] - tailKatanaValues[i - 1];
            floats4[i] = floats4[i - 1] + (float)Math.sqrt(latheProfileFactor5 * latheProfileFactor5 + latheProfileFactor6 * latheProfileFactor6);
         }

         float latheProfileDivisor = floats4[index] > 0.0F ? floats4[index] : 1.0F;

         for (int j = 0; j <= index; j++) {
            floats3[j] = 1.0F - floats4[j] / latheProfileDivisor;
         }

         for (int k = 0; k <= maxRound; k++) {
            floats2[k] = (float)k / maxRound;
            float angle = (float) (Math.PI * 2) * (floats2[k] - 0.5F);
            float sin = (float)Math.sin(angle);
            float cos = (float)Math.cos(angle);

            for (int m = 0; m <= index; m++) {
               int index2 = 3 * (m * (maxRound + 1) + k);
               floats[index2] = -latheProfileValues[m] * sin * latheProfileFactor2;
               floats[index2 + 1] = tailKatanaValues[m];
               floats[index2 + 2] = -latheProfileValues[m] * cos * latheProfileFactor3;
            }
         }

         this.handle(floats, maxRound, index, floats2, floats3, true, false, shinBrr);
         if (shinBrr2 != null && latheProfileValues[0] > 1.0E-4F) {
            this.handle(0.0F, tailKatanaValues[0], 0.0F, latheProfileValues[0] * latheProfileFactor2, latheProfileValues[0] * latheProfileFactor3, maxRound, false, shinBrr2);
         }

         if (shinBrr3 != null && latheProfileValues[index] > 1.0E-4F) {
            this.handle(0.0F, tailKatanaValues[index], 0.0F, latheProfileValues[index] * latheProfileFactor2, latheProfileValues[index] * latheProfileFactor3, maxRound, true, shinBrr3);
         }

         return this;
      } else {
         throw new IllegalArgumentException("lathe profile");
      }
   }

   public static float getAmountForFloat(float latheLoftValue, float latheLoftDivisor) {
      return (float)(-Math.toDegrees(Math.asin(Math.max(-1.0F, Math.min(1.0F, latheLoftValue / latheLoftDivisor)))));
   }

   public LatheLoft getLoftLathe(float loftLatheBound, float loftLatheBound2, float loftLatheBound3, float hashHuntBound, float loftLatheBound5, float loftLatheBound6, float wrapDegreesBound, int loftLatheDivisor, ShinBrr[] shinBrrs, ShinBrr shinBrr) {
      float loftLatheValue = Math.min(loftLatheBound, hashHuntBound);
      float max2 = Math.max(loftLatheBound, hashHuntBound);
      float minSeg = Math.min(loftLatheBound2, loftLatheBound5);
      float maxSeg = Math.max(loftLatheBound2, loftLatheBound5);
      float minRadius = Math.min(loftLatheBound3, loftLatheBound6);
      float maxRadius = Math.max(loftLatheBound3, loftLatheBound6);
      float loftLatheFactor = Math.max(0.0F, Math.min(wrapDegreesBound, 0.5F * Math.min(max2 - loftLatheValue, Math.min(maxSeg - minSeg, maxRadius - minRadius)) - 0.001F));
      loftLatheDivisor = Math.max(1, Math.round(loftLatheDivisor * this.latheLoftAmount));
      float rootValue = loftLatheValue + loftLatheFactor;
      float deltaVar = max2 - loftLatheFactor;
      float loftLatheValue4 = minSeg + loftLatheFactor;
      float deltaVar2 = maxSeg - loftLatheFactor;
      float loftLatheValue5 = minRadius + loftLatheFactor;
      float minSide = maxRadius - loftLatheFactor;
      this.updateRootAmounts();
      float[] floats = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F};
      if (shinBrrs[0] != null) {
         this.handle(new float[]{deltaVar, deltaVar2, minRadius, rootValue, deltaVar2, minRadius, rootValue, loftLatheValue4, minRadius, deltaVar, loftLatheValue4, minRadius}, floats, 0.0F, 0.0F, -1.0F, shinBrrs[0]);
      }

      if (shinBrrs[1] != null) {
         this.handle(new float[]{rootValue, deltaVar2, maxRadius, deltaVar, deltaVar2, maxRadius, deltaVar, loftLatheValue4, maxRadius, rootValue, loftLatheValue4, maxRadius}, floats, 0.0F, 0.0F, 1.0F, shinBrrs[1]);
      }

      if (shinBrrs[2] != null) {
         this.handle(new float[]{max2, deltaVar2, minSide, max2, deltaVar2, loftLatheValue5, max2, loftLatheValue4, loftLatheValue5, max2, loftLatheValue4, minSide}, floats, 1.0F, 0.0F, 0.0F, shinBrrs[2]);
      }

      if (shinBrrs[3] != null) {
         this.handle(new float[]{loftLatheValue, deltaVar2, loftLatheValue5, loftLatheValue, deltaVar2, minSide, loftLatheValue, loftLatheValue4, minSide, loftLatheValue, loftLatheValue4, loftLatheValue5}, floats, -1.0F, 0.0F, 0.0F, shinBrrs[3]);
      }

      if (shinBrrs[4] != null) {
         this.handle(new float[]{rootValue, maxSeg, loftLatheValue5, deltaVar, maxSeg, loftLatheValue5, deltaVar, maxSeg, minSide, rootValue, maxSeg, minSide}, floats, 0.0F, 1.0F, 0.0F, shinBrrs[4]);
      }

      if (shinBrrs[5] != null) {
         this.handle(new float[]{deltaVar, minSeg, loftLatheValue5, rootValue, minSeg, loftLatheValue5, rootValue, minSeg, minSide, deltaVar, minSeg, minSide}, floats, 0.0F, -1.0F, 0.0F, shinBrrs[5]);
      }

      if (!(loftLatheFactor <= 0.0F) && shinBrr != null) {
         float flagLoftRegion = shinBrr.getFlagLoftRegion(0.5F);
         float loftLatheValue7 = shinBrr.getFlag(0.5F);
         int tintColor = shinBrr.tintColor;

         for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
               float loftLatheValue9 = i < 0 ? loftLatheValue4 : deltaVar2;
               float loftLatheValue10 = j < 0 ? loftLatheValue5 : minSide;

               for (int k = 0; k < loftLatheDivisor; k++) {
                  float angle = (float) (Math.PI / 2) * k / loftLatheDivisor;
                  float angle2 = (float) (Math.PI / 2) * (k + 1) / loftLatheDivisor;
                  float loftLatheFactor2 = i * (float)Math.cos(angle);
                  float loftLatheFactor3 = j * (float)Math.sin(angle);
                  float loftLatheFactor4 = i * (float)Math.cos(angle2);
                  float loftLatheFactor5 = j * (float)Math.sin(angle2);
                  this.handleRootAmounts(rootValue, loftLatheValue9 + loftLatheFactor * loftLatheFactor2, loftLatheValue10 + loftLatheFactor * loftLatheFactor3, flagLoftRegion, loftLatheValue7, 0.0F, loftLatheFactor2, loftLatheFactor3, tintColor);
                  this.handleRootAmounts(deltaVar, loftLatheValue9 + loftLatheFactor * loftLatheFactor2, loftLatheValue10 + loftLatheFactor * loftLatheFactor3, flagLoftRegion, loftLatheValue7, 0.0F, loftLatheFactor2, loftLatheFactor3, tintColor);
                  this.handleRootAmounts(deltaVar, loftLatheValue9 + loftLatheFactor * loftLatheFactor4, loftLatheValue10 + loftLatheFactor * loftLatheFactor5, flagLoftRegion, loftLatheValue7, 0.0F, loftLatheFactor4, loftLatheFactor5, tintColor);
                  this.handleRootAmounts(rootValue, loftLatheValue9 + loftLatheFactor * loftLatheFactor4, loftLatheValue10 + loftLatheFactor * loftLatheFactor5, flagLoftRegion, loftLatheValue7, 0.0F, loftLatheFactor4, loftLatheFactor5, tintColor);
               }
            }
         }

         for (int m = -1; m <= 1; m += 2) {
            for (int p = -1; p <= 1; p += 2) {
               float loftLatheValue11 = m < 0 ? rootValue : deltaVar;
               float loftLatheValue12 = p < 0 ? loftLatheValue5 : minSide;

               for (int q = 0; q < loftLatheDivisor; q++) {
                  float angle3 = (float) (Math.PI / 2) * q / loftLatheDivisor;
                  float angle4 = (float) (Math.PI / 2) * (q + 1) / loftLatheDivisor;
                  float loftLatheFactor6 = m * (float)Math.cos(angle3);
                  float loftLatheFactor7 = p * (float)Math.sin(angle3);
                  float loftLatheFactor8 = m * (float)Math.cos(angle4);
                  float loftLatheFactor9 = p * (float)Math.sin(angle4);
                  this.handleRootAmounts(loftLatheValue11 + loftLatheFactor * loftLatheFactor6, loftLatheValue4, loftLatheValue12 + loftLatheFactor * loftLatheFactor7, flagLoftRegion, loftLatheValue7, loftLatheFactor6, 0.0F, loftLatheFactor7, tintColor);
                  this.handleRootAmounts(loftLatheValue11 + loftLatheFactor * loftLatheFactor6, deltaVar2, loftLatheValue12 + loftLatheFactor * loftLatheFactor7, flagLoftRegion, loftLatheValue7, loftLatheFactor6, 0.0F, loftLatheFactor7, tintColor);
                  this.handleRootAmounts(loftLatheValue11 + loftLatheFactor * loftLatheFactor8, deltaVar2, loftLatheValue12 + loftLatheFactor * loftLatheFactor9, flagLoftRegion, loftLatheValue7, loftLatheFactor8, 0.0F, loftLatheFactor9, tintColor);
                  this.handleRootAmounts(loftLatheValue11 + loftLatheFactor * loftLatheFactor8, loftLatheValue4, loftLatheValue12 + loftLatheFactor * loftLatheFactor9, flagLoftRegion, loftLatheValue7, loftLatheFactor8, 0.0F, loftLatheFactor9, tintColor);
               }
            }
         }

         for (int r = -1; r <= 1; r += 2) {
            for (int s = -1; s <= 1; s += 2) {
               float loftLatheValue13 = r < 0 ? rootValue : deltaVar;
               float loftLatheValue14 = s < 0 ? loftLatheValue4 : deltaVar2;

               for (int t = 0; t < loftLatheDivisor; t++) {
                  float angle5 = (float) (Math.PI / 2) * t / loftLatheDivisor;
                  float angle6 = (float) (Math.PI / 2) * (t + 1) / loftLatheDivisor;
                  float loftLatheFactor10 = r * (float)Math.cos(angle5);
                  float loftLatheFactor11 = s * (float)Math.sin(angle5);
                  float rootFactor = r * (float)Math.cos(angle6);
                  float loftLatheFactor13 = s * (float)Math.sin(angle6);
                  this.handleRootAmounts(loftLatheValue13 + loftLatheFactor * loftLatheFactor10, loftLatheValue14 + loftLatheFactor * loftLatheFactor11, loftLatheValue5, flagLoftRegion, loftLatheValue7, loftLatheFactor10, loftLatheFactor11, 0.0F, tintColor);
                  this.handleRootAmounts(loftLatheValue13 + loftLatheFactor * loftLatheFactor10, loftLatheValue14 + loftLatheFactor * loftLatheFactor11, minSide, flagLoftRegion, loftLatheValue7, loftLatheFactor10, loftLatheFactor11, 0.0F, tintColor);
                  this.handleRootAmounts(loftLatheValue13 + loftLatheFactor * rootFactor, loftLatheValue14 + loftLatheFactor * loftLatheFactor13, minSide, flagLoftRegion, loftLatheValue7, rootFactor, loftLatheFactor13, 0.0F, tintColor);
                  this.handleRootAmounts(loftLatheValue13 + loftLatheFactor * rootFactor, loftLatheValue14 + loftLatheFactor * loftLatheFactor13, loftLatheValue5, flagLoftRegion, loftLatheValue7, rootFactor, loftLatheFactor13, 0.0F, tintColor);
               }
            }
         }

         for (int loftLatheTotal = -1; loftLatheTotal <= 1; loftLatheTotal += 2) {
            for (int loftLatheTotal2 = -1; loftLatheTotal2 <= 1; loftLatheTotal2 += 2) {
               for (int loftLatheTotal3 = -1; loftLatheTotal3 <= 1; loftLatheTotal3 += 2) {
                  float loftLatheValue15 = loftLatheTotal < 0 ? rootValue : deltaVar;
                  float loftLatheValue16 = loftLatheTotal2 < 0 ? loftLatheValue4 : deltaVar2;
                  float loftLatheValue17 = loftLatheTotal3 < 0 ? loftLatheValue5 : minSide;

                  for (int loftLatheCounter = 0; loftLatheCounter < loftLatheDivisor; loftLatheCounter++) {
                     for (int loftLatheCounter2 = 0; loftLatheCounter2 < loftLatheDivisor; loftLatheCounter2++) {
                        float[] floats2 = new float[]{
                           (float) (Math.PI / 2) * loftLatheCounter / loftLatheDivisor,
                           (float) (Math.PI / 2) * (loftLatheCounter + 1) / loftLatheDivisor,
                           (float) (Math.PI / 2) * (loftLatheCounter + 1) / loftLatheDivisor,
                           (float) (Math.PI / 2) * loftLatheCounter / loftLatheDivisor
                        };
                        float[] floats3 = new float[]{
                           (float) (Math.PI / 2) * loftLatheCounter2 / loftLatheDivisor,
                           (float) (Math.PI / 2) * loftLatheCounter2 / loftLatheDivisor,
                           (float) (Math.PI / 2) * (loftLatheCounter2 + 1) / loftLatheDivisor,
                           (float) (Math.PI / 2) * (loftLatheCounter2 + 1) / loftLatheDivisor
                        };

                        for (int index = 0; index < 4; index++) {
                           float sin = (float)Math.sin(floats3[index]);
                           float loftLatheFactor14 = loftLatheTotal * sin * (float)Math.cos(floats2[index]);
                           float loftLatheFactor15 = loftLatheTotal2 * (float)Math.cos(floats3[index]);
                           float loftLatheFactor16 = loftLatheTotal3 * sin * (float)Math.sin(floats2[index]);
                           this.handleRootAmounts(loftLatheValue15 + loftLatheFactor * loftLatheFactor14, loftLatheValue16 + loftLatheFactor * loftLatheFactor15, loftLatheValue17 + loftLatheFactor * loftLatheFactor16, flagLoftRegion, loftLatheValue7, loftLatheFactor14, loftLatheFactor15, loftLatheFactor16, tintColor);
                        }
                     }
                  }
               }
            }
         }

         return this;
      } else {
         return this;
      }
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float wrapDegreesBound, float latheLoftValue3, float latheLoftValue4, float latheLoftValue5, float hashHuntBound, float latheLoftValue7, int loftLatheDivisor, ShinBrr shinBrr) {
      return this.getLoftLathe(latheLoftValue, wrapDegreesBound, latheLoftValue3, latheLoftValue4, latheLoftValue5, hashHuntBound, latheLoftValue7, loftLatheDivisor, new ShinBrr[]{shinBrr, shinBrr, shinBrr, shinBrr, shinBrr, shinBrr}, shinBrr);
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float eyeValue, float dinForeValue, float loftLatheFactor, float latheLoftValue5, float latheLoftValue6, int latheLoftValue7, ShinBrr shinBrr) {
      return this.getLoftLathe(latheLoftValue, eyeValue, dinForeValue, loftLatheFactor, latheLoftValue5, latheLoftValue6, latheLoftValue7, 0.0F, 1.0F, shinBrr);
   }

   public static int getValue(float[] latheLoftValues) {
      return latheLoftValues.length / 3;
   }

   private void handle(float latheLoftValue, float latheLoftValue2, float minSide, float latheLoftFactor, float latheLoftFactor2, int latheLoftDivisor, boolean latheLoftFlag, ShinBrr shinBrr) {
      this.updateRootAmounts();
      float latheLoftValue4 = latheLoftFlag ? 1.0F : -1.0F;

      for (int i = 0; i < latheLoftDivisor; i++) {
         float angle = (float) (Math.PI * 2) * i / latheLoftDivisor;
         float angle2 = (float) (Math.PI * 2) * (i + 1) / latheLoftDivisor;
         float sin = (float)Math.sin(angle);
         float cos = (float)Math.cos(angle);
         float sin2 = (float)Math.sin(angle2);
         float cos2 = (float)Math.cos(angle2);
         this.handleRootAmounts(latheLoftValue, latheLoftValue2, minSide, shinBrr.getFlagLoftRegion(0.5F), shinBrr.getFlag(0.5F), 0.0F, latheLoftValue4, 0.0F, shinBrr.tintColor);
         this.handleRootAmounts(
            latheLoftValue + sin * latheLoftFactor, latheLoftValue2, minSide + cos * latheLoftFactor2, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin), shinBrr.getFlag(0.5F + 0.5F * cos), 0.0F, latheLoftValue4, 0.0F, shinBrr.tintColor
         );
         this.handleRootAmounts(
            latheLoftValue + sin2 * latheLoftFactor, latheLoftValue2, minSide + cos2 * latheLoftFactor2, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin2), shinBrr.getFlag(0.5F + 0.5F * cos2), 0.0F, latheLoftValue4, 0.0F, shinBrr.tintColor
         );
         this.handleRootAmounts(
            latheLoftValue + sin2 * latheLoftFactor, latheLoftValue2, minSide + cos2 * latheLoftFactor2, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin2), shinBrr.getFlag(0.5F + 0.5F * cos2), 0.0F, latheLoftValue4, 0.0F, shinBrr.tintColor
         );
      }
   }

   public static float getAmount(float latheLoftValue) {
      float hashRegistryValue = 0.5F - latheLoftValue / 360.0F;
      return hashRegistryValue - (float)Math.floor(hashRegistryValue);
   }

   public ShinBrr getMax(int latheLoftValue) {
      return new ShinBrr(this.latheLoftLoftLatheFlag.loftLatheRegion, ForeShinUtils.getValueForInt(latheLoftValue), true);
   }

   public ForeEar getClassLatheLoft() {
      return this.classLatheLoft;
   }

   public LatheLoft getLoftLathe(float loftLatheValue, float loftLatheValue2, float minSide, float loftLatheFactor, float loftLatheFactor2, int loftLatheFactor3, ShinBrr shinBrr) {
      this.updateRootAmounts();
      int maxRound = Math.max(3, Math.round(loftLatheFactor3 * this.latheLoftAmount));

      for (int i = 0; i < maxRound; i++) {
         float angle = (float) (Math.PI * 2) * i / maxRound;
         float angle2 = (float) (Math.PI * 2) * (i + 1) / maxRound;
         float cos = (float)Math.cos(angle);
         float sin = (float)Math.sin(angle);
         float cos2 = (float)Math.cos(angle2);
         float sin2 = (float)Math.sin(angle2);
         this.handleRootAmounts(loftLatheValue, loftLatheValue2, minSide, shinBrr.getFlagLoftRegion(0.5F), shinBrr.getFlag(0.5F), 0.0F, 0.0F, -1.0F, shinBrr.tintColor);
         this.handleRootAmounts(
            loftLatheValue + cos * loftLatheFactor, loftLatheValue2 + sin * loftLatheFactor2, minSide, shinBrr.getFlagLoftRegion(0.5F - 0.5F * cos), shinBrr.getFlag(0.5F - 0.5F * sin), 0.0F, 0.0F, -1.0F, shinBrr.tintColor
         );
         this.handleRootAmounts(
            loftLatheValue + cos2 * loftLatheFactor, loftLatheValue2 + sin2 * loftLatheFactor2, minSide, shinBrr.getFlagLoftRegion(0.5F - 0.5F * cos2), shinBrr.getFlag(0.5F - 0.5F * sin2), 0.0F, 0.0F, -1.0F, shinBrr.tintColor
         );
         this.handleRootAmounts(
            loftLatheValue + cos2 * loftLatheFactor, loftLatheValue2 + sin2 * loftLatheFactor2, minSide, shinBrr.getFlagLoftRegion(0.5F - 0.5F * cos2), shinBrr.getFlag(0.5F - 0.5F * sin2), 0.0F, 0.0F, -1.0F, shinBrr.tintColor
         );
      }

      return this;
   }

   public LatheLoft getDequeArrayArrayDeque() {
      this.arrayDeque.push(new AmountLoft().getAmount(this.latheLoftAmountLoft));
      return this;
   }

   public ShinBrr getClassForRegionInt(LoftLatheFlag.Region region, int classValue) {
      return new ShinBrr(region, ForeShinUtils.getValueForInt(classValue), false);
   }

   private void handle(float[] latheLoftValues, float[] latheLoftValues2, float[] latheLoftValues3, float[] floats, float[] latheLoftValues5, float[] crownForeValues, int index, int latheLoftDivisor, float latheLoftFactor, ShinBrr shinBrr) {
      if (!(latheLoftValues5[index] < 1.0E-4F) || !(crownForeValues[index] < 1.0E-4F)) {
         this.updateRootAmounts();
         float latheLoftValue = latheLoftValues[3 * index];
         float latheLoftValue2 = latheLoftValues[3 * index + 1];
         float latheLoftValue3 = latheLoftValues[3 * index + 2];
         float rootFactor = floats[3 * index] * latheLoftFactor;
         float latheLoftValue5 = floats[3 * index + 1] * latheLoftFactor;
         float latheLoftValue6 = floats[3 * index + 2] * latheLoftFactor;

         for (int i = 0; i < latheLoftDivisor; i++) {
            float angle = (float) (Math.PI * 2) * i / latheLoftDivisor;
            float angle2 = (float) (Math.PI * 2) * (i + 1) / latheLoftDivisor;
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float cos2 = (float)Math.cos(angle2);
            float sin2 = (float)Math.sin(angle2);
            float latheLoftValue7 = latheLoftValue + latheLoftValues2[3 * index] * crownForeValues[index] * cos + latheLoftValues3[3 * index] * latheLoftValues5[index] * sin;
            float latheLoftValue8 = latheLoftValue2 + latheLoftValues2[3 * index + 1] * crownForeValues[index] * cos + latheLoftValues3[3 * index + 1] * latheLoftValues5[index] * sin;
            float latheLoftValue9 = latheLoftValue3 + latheLoftValues2[3 * index + 2] * crownForeValues[index] * cos + latheLoftValues3[3 * index + 2] * latheLoftValues5[index] * sin;
            float rootValue = latheLoftValue + latheLoftValues2[3 * index] * crownForeValues[index] * cos2 + latheLoftValues3[3 * index] * latheLoftValues5[index] * sin2;
            float latheLoftValue11 = latheLoftValue2 + latheLoftValues2[3 * index + 1] * crownForeValues[index] * cos2 + latheLoftValues3[3 * index + 1] * latheLoftValues5[index] * sin2;
            float minSide = latheLoftValue3 + latheLoftValues2[3 * index + 2] * crownForeValues[index] * cos2 + latheLoftValues3[3 * index + 2] * latheLoftValues5[index] * sin2;
            this.handleRootAmounts(latheLoftValue, latheLoftValue2, latheLoftValue3, shinBrr.getFlagLoftRegion(0.5F), shinBrr.getFlag(0.5F), rootFactor, latheLoftValue5, latheLoftValue6, shinBrr.tintColor);
            this.handleRootAmounts(latheLoftValue7, latheLoftValue8, latheLoftValue9, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin), shinBrr.getFlag(0.5F - 0.5F * cos), rootFactor, latheLoftValue5, latheLoftValue6, shinBrr.tintColor);
            this.handleRootAmounts(rootValue, latheLoftValue11, minSide, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin2), shinBrr.getFlag(0.5F - 0.5F * cos2), rootFactor, latheLoftValue5, latheLoftValue6, shinBrr.tintColor);
            this.handleRootAmounts(rootValue, latheLoftValue11, minSide, shinBrr.getFlagLoftRegion(0.5F + 0.5F * sin2), shinBrr.getFlag(0.5F - 0.5F * cos2), rootFactor, latheLoftValue5, latheLoftValue6, shinBrr.tintColor);
         }
      }
   }

   public ForeEar getClassForeEarList() {
      return this.classForeEarList.get(0);
   }

   public LatheLoft getLatheLoft(float latheLoftValue, float latheLoftValue2, float latheLoftValue3, float latheLoftValue4, int latheLoftValue5, ShinBrr shinBrr, ShinBrr shinBrr2) {
      return this.getLatheLoft(new float[]{latheLoftValue, latheLoftValue2}, new float[]{latheLoftValue3, latheLoftValue4}, latheLoftValue5, shinBrr, shinBrr2, shinBrr2);
   }

   public static float[] getAmounts(int latheLoftSize, float... latheLoftValues) {
      float[] floats = new float[latheLoftSize];
      if (latheLoftValues.length != 1 && latheLoftSize != 1) {
         for (int i = 0; i < latheLoftSize; i++) {
            float latheLoftValue = (float)i / (latheLoftSize - 1) * (latheLoftValues.length - 1);
            int index = Math.min(latheLoftValues.length - 2, (int)Math.floor(latheLoftValue));
            float deltaVar = latheLoftValue - index;
            float latheLoftFactor = deltaVar * deltaVar * (3.0F - 2.0F * deltaVar);
            floats[i] = latheLoftValues[index] + (latheLoftValues[index + 1] - latheLoftValues[index]) * latheLoftFactor;
         }

         return floats;
      } else {
         Arrays.fill(floats, latheLoftValues[0]);
         return floats;
      }
   }
}
