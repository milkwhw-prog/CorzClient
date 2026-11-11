package com.corz.client.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.AmountConstTime;

public final class EmptyLvl {
   private long emptyTime = Long.MIN_VALUE;
   public static final double[] emptyRelTable = new double[]{2.0, 2.4, 2.8, 3.2, 1.5, 1.1};
   private static final int[] mutableHazardTable = new int[]{0, 0, -1, 1};
   private static final int[] reachHazardValues = new int[]{-1, 1, 0, 0};
   private String empty;
   private int emptyRelCount;
   public static final String NONE = "none";
   public static final String SEEK = "seek";
   private boolean emptyFlag;
   private int emptyValue;
   private EmptyLvl.Pick emptyRel;
   public static final String OK_TEXT = "ok";
   public static final String LVL = "lvl";
   private long emptyTime2Empty;
   private int emptyCount;
   private static final int SINGLE_STEP = 1;
   private int emptyTickCount;
   private double emptyAmount = Double.NaN;
   private static final String[] emptyThreatTexts = new String[]{"reach", "empty", "hazard", "shaft", "los", "threat"};

   public EmptyLvl() {
      this.emptyTime2Empty = Long.MIN_VALUE;
      this.empty = "empty";
   }

   public static double getAmount(EmptyLvl.Cells cells, int emptyLvlValue, int constDeflectValue, int x3, double emptyLvlValue4, double groundY, double emptyLvlValue6) {
      if (cells != null && canFlagForDouble(emptyLvlValue4, groundY, emptyLvlValue6)) {
         double emptyLvlValue7 = emptyLvlValue + 0.5;
         double emptyLvlValue8 = x3 + 0.5;
         double emptyLvlTotal = 0.0;

         for (int i = -1; i <= 1; i++) {
            int emptyLvlValue9 = constDeflectValue + i;
            double emptyLvlValue10 = emptyLvlValue9 + 0.5;

            for (int j = -1; j <= 1; j++) {
               for (int k = -1; k <= 1; k++) {
                  if (j != 0 || k != 0) {
                     double emptyLvlValue11 = emptyLvlValue7 + j;
                     double emptyLvlValue12 = emptyLvlValue8 + k;
                     if (!(getAmountForDouble(j) + getAmountForDouble(emptyLvlValue10 - constDeflectValue) + getAmountForDouble(k) > 4.0)
                        && !(getAmountForDouble(emptyLvlValue11 - emptyLvlValue4) + getAmountForDouble(emptyLvlValue10 - groundY) + getAmountForDouble(emptyLvlValue12 - emptyLvlValue6) > 20.25)
                        && (!(Math.abs(emptyLvlValue11 - emptyLvlValue4) < 0.8) || !(Math.abs(emptyLvlValue12 - emptyLvlValue6) < 0.8) || !(emptyLvlValue9 + 1.0 > groundY) || !(emptyLvlValue9 < groundY + 1.8))
                        && cells.canMutableState(emptyLvlValue + j, emptyLvlValue9, x3 + k)) {
                        emptyLvlTotal += i > 0 ? 0.5 : 1.0;
                     }
                  }
               }
            }
         }

         return emptyLvlTotal;
      } else {
         return 0.0;
      }
   }

   public String getEmpty() {
      return this.empty;
   }

   public int getEmptyCount() {
      return this.emptyCount;
   }

   public static String formatLvlNone(double lvlNoneValue, EmptyLvl.Pick pick, boolean lvlNoneFlag, String lvlNoneText) {
      StringBuilder stringBuilder = new StringBuilder(24);
      stringBuilder.append(getAppend(lvlNoneValue)).append(':');
      if (pick == null) {
         stringBuilder.append("-:-:").append("none").append(':').append(lvlNoneText == null ? "empty" : lvlNoneText);
         return stringBuilder.toString();
      } else {
         stringBuilder.append(getAppend(pick.rel())).append(':').append(getAppend(pick.dist())).append(':');
         stringBuilder.append(lvlNoneFlag ? pick.state() : "lvl");
         return stringBuilder.toString();
      }
   }

   private static EmptyLvl.Pick getDataForPickDouble(EmptyLvl.Pick pick, double emptyLvlValue, double emptyLvlValue2, double emptyLvlValue3, double emptyLvlValue4, boolean emptyLvlFlag) {
      double hypot = Math.hypot(pick.getAmount2() - emptyLvlValue2, pick.getAmount() - emptyLvlValue4);
      double emptyLvlValue5 = getAmount(hypot, pick.y() - emptyLvlValue3);
      return new EmptyLvl.Pick(pick.x(), pick.y(), pick.z(), pick.y() - emptyLvlValue, pick.value(), hypot, emptyLvlValue5, formatSeek(pick.x(), pick.y(), pick.z(), emptyLvlValue2, emptyLvlValue3, emptyLvlValue4), emptyLvlFlag);
   }

   public static double getAmount(double emptyLvlValue) {
      return !Double.isFinite(emptyLvlValue) ? 0.0 : Math.max(0.0, emptyLvlValue - 2.7) * 3.6;
   }

   private static EmptyLvl.Cand getDataForListInt(List<EmptyLvl.Cand> list, int emptyLvlValue, int emptyLvlValue2, int emptyLvlValue3) {
      for (EmptyLvl.Cand cand : list) {
         if (cand.x() == emptyLvlValue && cand.y() == emptyLvlValue2 && cand.z() == emptyLvlValue3) {
            return cand;
         }
      }

      return null;
   }

   public EmptyLvl.Pick getEmptyTime(
      long emptyTime,
      EmptyLvl.Cells cells,
      double emptyValue,
      double groundY,
      double emptyValue3,
      double emptyAmount,
      double emptyLvlValue,
      double emptyValue5,
      double emptyValue6,
      AmountConstTime.Spot spot,
      EmptyLvl.Threat threat,
      boolean emptyFlag,
      boolean lvlEmptyFlag
   ) {
      if (cells != null && canFlagForDouble(emptyValue, groundY, emptyValue3) && canFlagForDouble(emptyLvlValue, emptyValue5, emptyValue6) && Double.isFinite(emptyAmount)) {
         int x = getX(emptyLvlValue);
         int x2 = getX(emptyValue5 + 0.001);
         int x3 = getX(emptyValue6);
         long time2 = getTime(x, x2, x3);
         boolean emptyFlag3 = emptyFlag
            || this.emptyTime == Long.MIN_VALUE
            || emptyTime < this.emptyTime
            || emptyTime - this.emptyTime >= 4L
            || Double.isNaN(this.emptyAmount)
            || Math.abs(emptyAmount - this.emptyAmount) >= 0.9
            || time2 != this.emptyTime2Empty
            || lvlEmptyFlag != this.emptyFlag
            || this.emptyRel != null && !canFlag(cells, this.emptyRel, emptyValue, groundY, emptyValue3, emptyAmount, lvlEmptyFlag);
         if (emptyFlag3) {
            this.emptyTime = emptyTime;
            this.emptyAmount = emptyAmount;
            this.emptyTime2Empty = time2;
            this.emptyFlag = lvlEmptyFlag;
            this.emptyTickCount++;
            this.emptyRel = this.getEmptyRel(cells, emptyValue, groundY, emptyValue3, emptyAmount, emptyLvlValue, emptyValue5, emptyValue6, x, x2, x3, spot, threat);
            return this.emptyRel;
         } else {
            if (this.emptyRel != null) {
               this.emptyRel = getDataForPickDouble(this.emptyRel, emptyAmount, emptyLvlValue, emptyValue5, emptyValue6, canFlag(emptyValue5, emptyAmount) && (cells.canWorld(x, x2, x3) || this.emptyRel.high()));
            }

            return this.emptyRel;
         }
      } else {
         this.handleEmptyTime("empty");
         return null;
      }
   }

   public static double getAmount(double emptyLvlValue, double emptyLvlValue2, double emptyLvlValue3, boolean emptyLvlFlag, double emptyLvlValue4) {
      return getAmount(emptyLvlValue, emptyLvlValue2, emptyLvlValue3, emptyLvlFlag) - 0.5 * emptyLvlValue4;
   }

   private static String getAppend(double appendValue) {
      if (!Double.isFinite(appendValue)) {
         return "-";
      } else {
         long round = Math.round(appendValue * 10.0);
         StringBuilder stringBuilder = new StringBuilder(6);
         if (round < 0L) {
            stringBuilder.append('-');
            round = -round;
         }

         return stringBuilder.append(round / 10L).append('.').append(round % 10L).toString();
      }
   }

   public void handleEmptyTime(String emptyText) {
      this.emptyRel = null;
      this.empty = emptyText == null ? "empty" : emptyText;
      this.emptyTime = Long.MIN_VALUE;
      this.emptyAmount = Double.NaN;
      this.emptyTime2Empty = Long.MIN_VALUE;
      this.emptyFlag = false;
   }

   public static boolean canRelHigh(EmptyLvl.Pick pick, double relHighValue, boolean relHighFlag) {
      if (pick != null && Double.isFinite(relHighValue)) {
         int relHighBound = getValue(pick.rel(), relHighFlag);
         if (pick.high() && pick.rel() <= 0.5 && pick.rel() >= -3.5) {
            relHighBound = Math.max(30, relHighBound);
         }

         return pick.dist() <= (pick.high() ? 10.0 : 8.0) && pick.eta() <= getAmount(relHighValue) + relHighBound;
      } else {
         return false;
      }
   }

   public EmptyLvl.Pick getDataForLongCells(
      long emptyLvlValue,
      EmptyLvl.Cells cells,
      double emptyLvlValue2,
      double groundY,
      double emptyLvlValue4,
      double y,
      double emptyLvlValue6,
      double emptyLvlValue7,
      double emptyLvlValue8,
      AmountConstTime.Spot spot,
      EmptyLvl.Threat threat,
      boolean emptyLvlFlag
   ) {
      return this.getEmptyTime(emptyLvlValue, cells, emptyLvlValue2, groundY, emptyLvlValue4, y, emptyLvlValue6, emptyLvlValue7, emptyLvlValue8, spot, threat, emptyLvlFlag, false);
   }

   private EmptyLvl.Pick getEmptyRel(
      EmptyLvl.Cells cells,
      double emptyRelValue,
      double groundY,
      double emptyRelValue3,
      double emptyRelValue4,
      double emptyRelValue5,
      double emptyLvlValue,
      double emptyRelValue7,
      int emptyRelValue8,
      int constDeflectValue,
      int emptyRelValue10,
      AmountConstTime.Spot spot,
      EmptyLvl.Threat threat
   ) {
      EmptyLvl.Pick pick = this.emptyRel;
      this.emptyRelCount = 0;
      this.emptyCount = 0;
      this.emptyValue = 0;
      int[] ints = new int[]{-1};
      ArrayList arrayList = new ArrayList();
      HashSet hashSet = new HashSet();
      this.handleEmptyFlag(cells, emptyRelValue8, constDeflectValue, emptyRelValue10, emptyRelValue, groundY, emptyRelValue3, emptyRelValue4, emptyRelValue5, emptyLvlValue, emptyRelValue7, spot, pick, hashSet, arrayList, ints);
      if (pick != null) {
         this.handleEmptyFlag(cells, pick.x(), pick.y(), pick.z(), emptyRelValue, groundY, emptyRelValue3, emptyRelValue4, emptyRelValue5, emptyLvlValue, emptyRelValue7, spot, pick, hashSet, arrayList, ints);
      }

      double x = emptyRelValue5 - emptyRelValue;
      double y = emptyRelValue7 - emptyRelValue3;
      double emptyRelValue11;
      if (x * x + y * y > 1.0E-6) {
         emptyRelValue11 = Math.atan2(y, x);
      } else if (pick != null) {
         emptyRelValue11 = Math.atan2(pick.getAmount() - emptyRelValue3, pick.getAmount2() - emptyRelValue);
      } else {
         emptyRelValue11 = 0.0;
      }

      int floor12 = (int)Math.floor(emptyRelValue4 + (this.emptyFlag ? 0.0 : 0.5) + 1.0E-9);
      int emptyRelValue13 = (int)Math.floor(emptyRelValue4 + 1.0E-9) - 3;
      HashSet hashSet2 = new HashSet();
      boolean emptyRelFlag = canFlag(emptyLvlValue, emptyRelValue4) && (cells.canWorld(emptyRelValue8, constDeflectValue, emptyRelValue10) || pick != null && pick.high());
      int emptyRelValue14 = (emptyRelFlag ? 180 : 90) / 15;

      for (int i = 0; i <= 2 * emptyRelValue14; i++) {
         int degrees = i == 0 ? 0 : (i + 1) / 2 * 15 * ((i & 1) == 1 ? 1 : -1);
         if (degrees > -180) {
            double angle = emptyRelValue11 + Math.toRadians(degrees);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (double emptyRelFactor : emptyRelTable) {
               int x2 = getX(emptyRelValue + cos * emptyRelFactor);
               int x3 = getX(emptyRelValue3 + sin * emptyRelFactor);
               if (hashSet2.add(getTime(x2, 0, x3))) {
                  for (int j = floor12; j >= emptyRelValue13; j--) {
                     this.handleEmptyFlag(cells, x2, j, x3, emptyRelValue, groundY, emptyRelValue3, emptyRelValue4, emptyRelValue5, emptyLvlValue, emptyRelValue7, spot, pick, hashSet, arrayList, ints);
                  }
               }
            }
         }
      }

      if (arrayList.isEmpty()) {
         this.empty = ints[0] < 0 ? "empty" : emptyThreatTexts[Math.min(ints[0], 3)];
         return null;
      } else {
         arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.world.EmptyLvl.Cand) obj2).score(), ((com.corz.client.world.EmptyLvl.Cand) obj).score()));
         double emptyRelValue15 = groundY + (this.emptyFlag ? 1.3 : 0.45);
         EmptyLvl.Cand cand = null;
         HashSet hashSet3 = new HashSet();
         boolean emptyRelFlag2 = false;

         for (EmptyLvl.Cand blockPos : (Iterable<com.corz.client.world.EmptyLvl.Cand>) (arrayList)) {
            if (this.emptyRelCount >= 8 || emptyRelFlag2) {
               break;
            }

            this.emptyRelCount++;
            if (!cells.canAbsentRaycast(blockPos.x() + 0.5, blockPos.y() + 1.62, blockPos.z() + 0.5, emptyRelValue, emptyRelValue15, emptyRelValue3)) {
               ints[0] = Math.max(ints[0], 4);
               hashSet3.add(blockPos);
            } else if (this.emptyFlag && blockPos.rel() > -0.5 && !this.canEmpty(cells, blockPos.x(), blockPos.y(), blockPos.z(), emptyRelValue, emptyRelValue3, emptyRelValue4)) {
               ints[0] = Math.max(ints[0], 4);
               hashSet3.add(blockPos);
            } else {
               if (threat != null) {
                  this.emptyCount++;
                  if (!threat.ok(blockPos.x() + 0.5, blockPos.y(), blockPos.z() + 0.5)) {
                     ints[0] = Math.max(ints[0], 5);
                     hashSet3.add(blockPos);
                     if (this.emptyCount >= 2) {
                        emptyRelFlag2 = true;
                     }
                     continue;
                  }
               }

               cand = blockPos;
               break;
            }
         }

         if (cand == null) {
            this.empty = ints[0] < 0 ? "empty" : emptyThreatTexts[Math.max(4, Math.min(ints[0], 5))];
            return null;
         } else {
            EmptyLvl.Cand tickx = pick == null ? null : getDataForListInt(arrayList, pick.x(), pick.y(), pick.z());
            if (tickx != null
               && tickx != cand
               && !hashSet3.contains(tickx)
               && cand.score() - tickx.score() <= 0.5
               && this.emptyRelCount < 8
               && (threat == null || this.emptyCount < 2)) {
               this.emptyRelCount++;
               boolean absentRaycast = cells.canAbsentRaycast(tickx.x() + 0.5, tickx.y() + 1.62, tickx.z() + 0.5, emptyRelValue, emptyRelValue15, emptyRelValue3);
               if (absentRaycast && this.emptyFlag && tickx.rel() > -0.5) {
                  absentRaycast = this.canEmpty(cells, tickx.x(), tickx.y(), tickx.z(), emptyRelValue, emptyRelValue3, emptyRelValue4);
               }

               if (absentRaycast && threat != null) {
                  this.emptyCount++;
                  absentRaycast = threat.ok(tickx.x() + 0.5, tickx.y(), tickx.z() + 0.5);
               }

               if (absentRaycast) {
                  cand = tickx;
               }
            }

            this.empty = "-";
            return new EmptyLvl.Pick(
               cand.x(),
               cand.y(),
               cand.z(),
               cand.rel(),
               cand.score(),
               cand.dist(),
               cand.eta(),
               formatSeek(cand.x(), cand.y(), cand.z(), emptyRelValue5, emptyLvlValue, emptyRelValue7),
               emptyRelFlag
            );
         }
      }
   }

   public void updateEmpty() {
      this.handleEmptyTime("empty");
      this.emptyRelCount = 0;
      this.emptyCount = 0;
      this.emptyValue = 0;
   }

   public static double getAmount(double emptyLvlValue, double emptyLvlBound) {
      return emptyLvlValue * 3.6 + Math.max(0.0, -emptyLvlBound) * 0.8 + Math.max(0.0, emptyLvlBound) * 4.0;
   }

   public int getEmptyValue() {
      return this.emptyValue;
   }

   public int getValue() {
      return this.emptyTickCount;
   }

   public static EmptyLvl.Cells getApiForWorld(World world) {
      return world == null ? null : new EmptyLvl.Mc(world);
   }

   private static double getAmountForDouble(double emptyLvlFactor) {
      return emptyLvlFactor * emptyLvlFactor;
   }

   public static boolean canFlag(double emptyLvlValue, double emptyLvlValue2) {
      return Double.isFinite(emptyLvlValue) && Double.isFinite(emptyLvlValue2) && emptyLvlValue - emptyLvlValue2 > 0.5;
   }

   public static boolean canFlag(double emptyLvlValue, double emptyLvlLimit, double groundY) {
      if (Double.isNaN(emptyLvlValue)) {
         return false;
      } else {
         return emptyLvlValue < emptyLvlLimit ? true : Double.isFinite(groundY) && emptyLvlValue <= groundY;
      }
   }

   private static long getTime(int emptyLvlValue, int x2, int x3) {
      return (long)(emptyLvlValue & 67108863) << 38 | (long)(x3 & 67108863) << 12 | x2 & 4095;
   }

   public int getEmptyRelCount() {
      return this.emptyRelCount;
   }

   private static boolean canFlag(EmptyLvl.Cells cells, EmptyLvl.Pick pick, double emptyLvlValue, double groundY, double emptyLvlValue3, double emptyLvlValue4, boolean emptyLvlFlag) {
      if (!canFlag2(pick.getAmount2(), pick.y() + 1.62, pick.getAmount(), emptyLvlValue, groundY, emptyLvlValue3)) {
         return false;
      } else if (canFlag(pick.getAmount2(), pick.y(), pick.getAmount(), emptyLvlValue, groundY, emptyLvlValue3)) {
         return false;
      } else if (!emptyLvlFlag || !(pick.y() - emptyLvlValue4 > 1.0E-9) && !canFlag(pick.y() - emptyLvlValue4, pick.getAmount2(), pick.getAmount(), emptyLvlValue, emptyLvlValue3)) {
         return !cells.canWorld(pick.x(), pick.y(), pick.z()) ? false : (cells.getTimeWorld(pick.x(), pick.y(), pick.z()) & 1407) == 0;
      } else {
         return false;
      }
   }

   private boolean canEmpty(EmptyLvl.Cells cells, int emptyValue, int emptyValue2, int emptyValue3, double emptyRelValue, double emptyValue5, double emptyValue6) {
      if (this.emptyRelCount >= 8) {
         return false;
      } else {
         this.emptyRelCount++;
         return !cells.canAbsentRaycast(emptyValue + 0.5, emptyValue2 + 0.5, emptyValue3 + 0.5, emptyRelValue, Math.floor(emptyValue6) + 0.5, emptyValue5);
      }
   }

   private static boolean canFlag(double emptyLvlValue, double constDeflectValue, double levelOffset, double emptyLvlValue4, double groundY, double emptyLvlValue6) {
      return Math.abs(emptyLvlValue - emptyLvlValue4) < 0.6 && Math.abs(levelOffset - emptyLvlValue6) < 0.6 && constDeflectValue < groundY + 1.8 && constDeflectValue + 1.8 > groundY;
   }

   private static boolean canFlagForDouble(double emptyLvlValue, double groundY, double emptyLvlValue3) {
      return Double.isFinite(emptyLvlValue) && Double.isFinite(groundY) && Double.isFinite(emptyLvlValue3);
   }

   public static int getValue(double emptyLvlLimit, boolean emptyLvlFlag) {
      return emptyLvlFlag && emptyLvlLimit <= -0.5 && emptyLvlLimit >= -3.5 ? 14 : 6;
   }

   public static boolean canFlag2(double emptyLvlValue, double emptyLvlValue2, double posBlockValue, double emptyLvlValue4, double groundY, double emptyLvlValue6) {
      double emptyLvlFactor = Math.max(0.0, Math.max(emptyLvlValue4 - 0.3 - emptyLvlValue, emptyLvlValue - (emptyLvlValue4 + 0.3)));
      double max2 = Math.max(0.0, Math.max(groundY - emptyLvlValue2, emptyLvlValue2 - (groundY + 1.8)));
      double max3 = Math.max(0.0, Math.max(emptyLvlValue6 - 0.3 - posBlockValue, posBlockValue - (emptyLvlValue6 + 0.3)));
      return Math.sqrt(emptyLvlFactor * emptyLvlFactor + max2 * max2 + max3 * max3) <= 2.800000001;
   }

   public static boolean canFlag(EmptyLvl.Pick pick, double emptyLvlValue) {
      return canRelHigh(pick, emptyLvlValue, false);
   }

   private static int getX(double emptyLvlValue) {
      return (int)Math.floor(emptyLvlValue);
   }

   public EmptyLvl.Pick getDataEmptyRel() {
      return this.emptyRel;
   }

   public static double getAmount(double emptyLvlValue, double emptyLvlValue2, double emptyLvlValue3, boolean emptyLvlFlag) {
      return HeightDeltaMath.getHeightDeltaScore(emptyLvlValue, true) - 0.05 * emptyLvlValue2 - 4.0 * emptyLvlValue3 + (emptyLvlFlag ? 0.4 : 0.0);
   }

   private void handleEmptyFlag(
      EmptyLvl.Cells cells,
      int emptyValue,
      int constDeflectValue,
      int x3,
      double emptyRelValue,
      double groundY,
      double emptyValue6,
      double emptyValue7,
      double emptyValue8,
      double emptyValue9,
      double emptyValue10,
      AmountConstTime.Spot spot,
      EmptyLvl.Pick pick,
      Set<Long> set,
      List<EmptyLvl.Cand> list,
      int[] emptyValues
   ) {
      if (this.emptyValue < 72) {
         double emptyLimit = constDeflectValue - emptyValue7;
         if (!(emptyLimit < -3.5) && !(emptyLimit > (this.emptyFlag ? 1.0E-9 : 0.5))) {
            if (set.add(getTime(emptyValue, constDeflectValue, x3))) {
               double emptyLvlValue = emptyValue + 0.5;
               double levelOffset = x3 + 0.5;
               if (!canFlag2(emptyLvlValue, constDeflectValue + 1.62, levelOffset, emptyRelValue, groundY, emptyValue6) || canFlag(emptyLvlValue, constDeflectValue, levelOffset, emptyRelValue, groundY, emptyValue6)) {
                  emptyValues[0] = Math.max(emptyValues[0], 0);
               } else if (this.emptyFlag && canFlag(emptyLimit, emptyLvlValue, levelOffset, emptyRelValue, emptyValue6)) {
                  emptyValues[0] = Math.max(emptyValues[0], 3);
               } else if (!cells.canWorld(emptyValue, constDeflectValue, x3)) {
                  emptyValues[0] = Math.max(emptyValues[0], 1);
               } else if ((cells.getTimeWorld(emptyValue, constDeflectValue, x3) & 1407) != 0) {
                  emptyValues[0] = Math.max(emptyValues[0], 2);
               } else if (emptyLimit <= -0.5 && cells.getMutableHazard(emptyValue, constDeflectValue, x3) < 2) {
                  emptyValues[0] = Math.max(emptyValues[0], 3);
               } else {
                  this.emptyValue++;
                  double hypot = Math.hypot(emptyLvlValue - emptyValue8, levelOffset - emptyValue10);
                  double emptyValue12 = getAmount(hypot, constDeflectValue - emptyValue9);
                  double emptyValue13 = 0.0;
                  if (spot != null) {
                     double sqrt = Math.sqrt(
                        getAmountForDouble(emptyLvlValue - spot.getX()) + getAmountForDouble(constDeflectValue - spot.getY()) + getAmountForDouble(levelOffset - spot.getZ())
                     );
                     emptyValue13 = HeightDeltaMath.getDistanceWeightedReachFraction(constDeflectValue, sqrt, spot.getAmount());
                  }

                  boolean emptyFlag = pick != null && pick.canFlag(emptyValue, constDeflectValue, x3);
                  list.add(
                     new EmptyLvl.Cand(
                        emptyValue, constDeflectValue, x3, emptyLimit, getAmount(emptyLimit, emptyValue12, emptyValue13, emptyFlag, getAmount(cells, emptyValue, constDeflectValue, x3, emptyRelValue, groundY, emptyValue6)), hypot, emptyValue12
                     )
                  );
               }
            }
         }
      }
   }

   private static boolean canFlag(double emptyLvlLimit, double emptyLvlValue, double levelOffset, double emptyLvlValue3, double emptyLvlValue4) {
      return emptyLvlLimit > -0.5 && Math.hypot(emptyLvlValue - emptyLvlValue3, levelOffset - emptyLvlValue4) < 2.0;
   }

   private static String formatSeek(int seekValue, int seekValue2, int seekValue3, double seekValue4, double emptyLvlValue, double seekValue6) {
      double hypot = Math.hypot(seekValue + 0.5 - seekValue4, seekValue3 + 0.5 - seekValue6);
      return hypot <= 0.6 && Math.abs(emptyLvlValue - seekValue2) <= 0.6 ? "ok" : "seek";
   }

   private record Cand(int x, int y, int z, double rel, double score, double dist, double eta) {
   }

   public interface Cells {
      boolean canWorld(int worldValue, int x2, int x3);

      default boolean canMutableState(int mutableValue, int mutableValue2, int mutableValue3) {
         return false;
      }

      int getMutableHazard(int mutableHazardValue, int constDeflectValue, int x3);

      boolean canAbsentRaycast(double absentRaycastValue, double absentRaycastValue2, double absentRaycastValue3, double emptyRelValue, double absentRaycastValue5, double absentRaycastValue6);

      int getTimeWorld(int worldValue, int constDeflectValue, int x3);
   }

   private static final class Mc implements EmptyLvl.Cells {
      private int cachedBlockState;
      private final Mutable mutable = new Mutable();
      private long lastQueryTimeMs = Long.MIN_VALUE;
      private final World world;

      Mc(World world2) {
         this.world = world2;
      }

      @Override
      public boolean canMutableState(int mutableValue, int mutableValue2, int mutableValue3) {
         this.mutable.set(mutableValue, mutableValue2, mutableValue3);
         BlockState blockState = this.world.getBlockState(this.mutable);
         if ((blockState.isAir() || blockState.isReplaceable()) && blockState.getFluidState().isEmpty()) {
            this.mutable.set(mutableValue, mutableValue2 - 1, mutableValue3);
            return !this.world.getBlockState(this.mutable).isAir();
         } else {
            return false;
         }
      }

      @Override
      public boolean canAbsentRaycast(double x, double y, double z, double x2, double y2, double z2) {
         Vec3d vec3d = new Vec3d(x, y, z);
         Vec3d vec3d2 = new Vec3d(x2, y2, z2);
         return vec3d.squaredDistanceTo(vec3d2) < 1.0E-8
            ? true
            : this.world.raycast(new RaycastContext(vec3d, vec3d2, ShapeType.COLLIDER, FluidHandling.NONE, ShapeContext.absent())).getType() == Type.MISS;
      }

      @Override
      public boolean canWorld(int x2, int y2, int z2) {
         BlockPos blockPos = new BlockPos(x2, y2, z2);
         return StateBlockUtilsCenter.canDown(this.world, blockPos) && (this.getTimeWorld(x2, y2, z2) & 287) == 0;
      }

      @Override
      public int getMutableHazard(int mutableHazardValue, int mutableHazardValue2, int mutableHazardValue3) {
         int mutableHazard = 0;

         for (int i = 0; i < 4; i++) {
            this.mutable.set(mutableHazardValue + EmptyLvl.mutableHazardTable[i], mutableHazardValue2, mutableHazardValue3 + EmptyLvl.reachHazardValues[i]);
            if (StateBlockUtilsCenter.canCollisionShape(this.world, this.mutable)) {
               this.mutable.set(mutableHazardValue + EmptyLvl.mutableHazardTable[i], mutableHazardValue2 + 1, mutableHazardValue3 + EmptyLvl.reachHazardValues[i]);
               if (StateBlockUtilsCenter.canCollisionShape(this.world, this.mutable)) {
                  mutableHazard++;
               }
            }
         }

         return mutableHazard;
      }

      @Override
      public int getTimeWorld(int x2, int y2, int z2) {
         long time = EmptyLvl.getTime(x2, y2, z2);
         if (time != this.lastQueryTimeMs) {
            this.cachedBlockState = BlockHazardFlags.getExtendedHazardFlags(this.world, new BlockPos(x2, y2, z2));
            this.lastQueryTimeMs = time;
         }

         return this.cachedBlockState;
      }
   }

   public record Pick(int x, int y, int z, double rel, double value, double dist, double eta, String state, boolean high) {
      public boolean canFlag(int cx, int cy, int cz) {
         return this.x == cx && this.y == cy && this.z == cz;
      }

      public double getAmount() {
         return this.z + 0.5;
      }

      public Pick(int state, int z, int y, double x, double value, double eta, double dist, String rel) {
         this(state, z, y, x, value, eta, dist, rel, false);
      }

      public double getAmount2() {
         return this.x + 0.5;
      }
   }

   public interface Threat {
      boolean ok(double threatValue, double threatValue2, double threatValue3);
   }
}
