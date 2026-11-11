package com.corz.client.world;

import java.util.List;

public final class PosVecUtils {
   public static final PosVecUtils.Terrain NO_TERRAIN = (posVecValue, posVecValue2, posVecValue3) -> false;

   private static PosVecUtils.Sample getSigmaGround(
      PosVecUtils.Sample sample, PosVecUtils.Terrain terrain, double sigmaGroundValue, double groundY, boolean sigmaGroundFlag, double sigmaGroundValue3, boolean sigmaGroundFlag2
   ) {
      PosVecUtils.Terrain terrain2 = terrain == null ? NO_TERRAIN : terrain;
      if (canFlag(sample.x(), sample.y(), sample.z())
         && canFlag(sample.vx(), sample.vy(), sample.vz())
         && canFlag(sigmaGroundValue, groundY, 0.0)
         && !(Math.abs(sample.x()) > 1.0E8)
         && !(Math.abs(sample.y()) > 1.0E8)
         && !(Math.abs(sample.z()) > 1.0E8)) {
         boolean onGround = sample.onGround();
         double sigmaGroundValue4 = onGround ? 0.546 : 0.91;
         double vx = sample.vx();
         double vy = sample.vy();
         double vz = sample.vz();
         if (!onGround) {
            vx += sigmaGroundValue;
            vz += groundY;
         }

         double x = sample.x();
         double y = sample.y();
         double z = sample.z();
         boolean sigmaGroundFlag3 = false;
         int valueForDouble = getValueForDouble(x - 0.3 + 1.0E-7);
         int valueForDouble6 = getValueForDouble(x + 0.3 - 1.0E-7);
         int valueForDouble7 = getValueForDouble(z - 0.3 + 1.0E-7);
         int valueForDouble8 = getValueForDouble(z + 0.3 - 1.0E-7);
         if (vy < 0.0) {
            double posVecValue = y + vy;
            int valueForDouble10 = getValueForDouble(y + 1.0E-7);
            boolean sigmaGroundFlag4 = onGround && y - valueForDouble10 > 1.0E-7 && canSolid(terrain2, valueForDouble, valueForDouble6, valueForDouble10, valueForDouble10, valueForDouble7, valueForDouble8);
            if (!sigmaGroundFlag4) {
               int sigmaGroundValue11 = valueForDouble10 - 1;
               int max12 = Math.max(getValueForDouble(posVecValue), sigmaGroundValue11 - 64);

               for (int i = sigmaGroundValue11; i >= max12; i--) {
                  if (canSolid(terrain2, valueForDouble, valueForDouble6, i, i, valueForDouble7, valueForDouble8)) {
                     y = i + 1.0;
                     sigmaGroundFlag4 = true;
                     break;
                  }
               }
            }

            if (sigmaGroundFlag4) {
               vy = 0.0;
               sigmaGroundFlag3 = true;
            } else {
               y = posVecValue;
            }
         } else if (vy > 0.0) {
            double sigmaGroundValue13 = y + vy;
            int sigmaGroundValue14 = getValue(y + 1.8 - 1.0E-7);
            int sigmaGroundLimit = Math.min(getValue(sigmaGroundValue13 + 1.8) - 1, sigmaGroundValue14 + 64);
            boolean sigmaGroundFlag5 = false;

            for (int j = sigmaGroundValue14; j <= sigmaGroundLimit; j++) {
               if (canSolid(terrain2, valueForDouble, valueForDouble6, j, j, valueForDouble7, valueForDouble8)) {
                  y = j - 1.8;
                  sigmaGroundFlag5 = true;
                  break;
               }
            }

            if (sigmaGroundFlag5) {
               vy = 0.0;
            } else {
               y = sigmaGroundValue13;
            }
         }

         int sigmaGroundBound = getValueForDouble(y + 1.0E-7);
         if (onGround || sigmaGroundFlag3) {
            sigmaGroundBound = Math.max(sigmaGroundBound, getValueForDouble((onGround ? sample.y() : y) + 0.6));
         }

         int valueForDouble15 = getValueForDouble(y + 1.8 - 1.0E-7);
         if (vx != 0.0) {
            double sigmaGroundValue16 = x + vx;
            boolean sigmaGroundFlag6;
            if (vx > 0.0) {
               int sigmaGroundValue17 = getValue(x + 0.3 - 1.0E-7);
               int min2 = Math.min(getValue(sigmaGroundValue16 + 0.3) - 1, sigmaGroundValue17 + 64);
               sigmaGroundFlag6 = sigmaGroundValue17 <= min2 && canSolid(terrain2, sigmaGroundValue17, min2, sigmaGroundBound, valueForDouble15, valueForDouble7, valueForDouble8);
            } else {
               int solidLimit = getValueForDouble(x - 0.3 + 1.0E-7) - 1;
               int solidValue = Math.max(getValueForDouble(sigmaGroundValue16 - 0.3), solidLimit - 64);
               sigmaGroundFlag6 = solidValue <= solidLimit && canSolid(terrain2, solidValue, solidLimit, sigmaGroundBound, valueForDouble15, valueForDouble7, valueForDouble8);
            }

            if (sigmaGroundFlag6) {
               vx = 0.0;
            } else {
               x = sigmaGroundValue16;
            }
         }

         int valueForDouble19 = getValueForDouble(x - 0.3 + 1.0E-7);
         int valueForDouble20 = getValueForDouble(x + 0.3 - 1.0E-7);
         if (vz != 0.0) {
            double sigmaGroundValue21 = z + vz;
            boolean sigmaGroundFlag7;
            if (vz > 0.0) {
               int sigmaGroundValue22 = getValue(z + 0.3 - 1.0E-7);
               int min4 = Math.min(getValue(sigmaGroundValue21 + 0.3) - 1, sigmaGroundValue22 + 64);
               sigmaGroundFlag7 = sigmaGroundValue22 <= min4 && canSolid(terrain2, valueForDouble19, valueForDouble20, sigmaGroundBound, valueForDouble15, sigmaGroundValue22, min4);
            } else {
               int sigmaGroundLimit5 = getValueForDouble(z - 0.3 + 1.0E-7) - 1;
               int max23 = Math.max(getValueForDouble(sigmaGroundValue21 - 0.3), sigmaGroundLimit5 - 64);
               sigmaGroundFlag7 = max23 <= sigmaGroundLimit5 && canSolid(terrain2, valueForDouble19, valueForDouble20, sigmaGroundBound, valueForDouble15, max23, sigmaGroundLimit5);
            }

            if (sigmaGroundFlag7) {
               vz = 0.0;
            } else {
               z = sigmaGroundValue21;
            }
         }

         double sigmaGroundValue24 = (vy - 0.08) * 0.98;
         if (!sigmaGroundFlag || !onGround || sigmaGroundFlag2) {
            vx *= sigmaGroundValue4;
            vz *= sigmaGroundValue4;
         }

         if (Math.abs(vx) < 0.003) {
            vx = 0.0;
         }

         if (Math.abs(vz) < 0.003) {
            vz = 0.0;
         }

         return new PosVecUtils.Sample(x, y, z, vx, sigmaGroundValue24, vz, sigmaGroundFlag3, sample.sigma() + sigmaGroundValue3);
      } else {
         return new PosVecUtils.Sample(
            sample.x(), sample.y(), sample.z(), sample.vx(), sample.vy(), sample.vz(), sample.onGround(), sample.sigma() + sigmaGroundValue3
         );
      }
   }

   private static boolean canFlag(double posVecValue, double groundY, double posVecValue3) {
      return Double.isFinite(posVecValue) && Double.isFinite(groundY) && Double.isFinite(posVecValue3);
   }

   private static int getValue(double posVecValue) {
      return (int)Math.ceil(posVecValue);
   }

   private static int getValueForDouble(double posVecValue) {
      return (int)Math.floor(posVecValue);
   }

   public static double getAmount(double posVecValue, int exponent) {
      return -3.92 + (posVecValue + 3.92) * Math.pow(0.98, exponent);
   }

   public static double getAmountForDoubleInt(double posVecValue, int exponent) {
      return exponent < 1 ? Double.NaN : (posVecValue + 3.92 * exponent) / (50.0 * (1.0 - Math.pow(0.98, exponent))) - 3.92;
   }

   public static PosVecUtils.Path getGround(
      PosVecUtils.Sample sample,
      int groundBound,
      PosVecUtils.Terrain terrain,
      double groundValue,
      double groundY,
      boolean groundFlag,
      double groundValue3,
      List<PosVecUtils.Kick> list
   ) {
      int maxStart = Math.max(0, groundBound);
      PosVecUtils.Sample[] samples = new PosVecUtils.Sample[maxStart + 1];
      PosVecUtils.Sample sample2 = sample;
      boolean groundFlag2 = false;

      for (int i = 0; i <= maxStart; i++) {
         boolean groundFlag3 = false;
         if (list != null) {
            for (PosVecUtils.Kick kick : list) {
               if (kick != null && kick.index() == i) {
                  sample2 = getSigmaGround(sample2, kick);
                  groundFlag3 = true;
               }
            }
         }

         samples[i] = sample2;
         if (!sample2.onGround()) {
            groundFlag2 = true;
         }

         if (i < maxStart) {
            sample2 = getSigmaGround(sample2, terrain, groundValue, groundY, groundFlag && !groundFlag2, groundValue3, groundFlag3);
         }
      }

      return new PosVecUtils.Path(samples);
   }

   public static int getValueInt(double posVecLimit) {
      return !(posVecLimit > 0.0) ? 0 : (int)Math.ceil(Math.log(3.92 / (posVecLimit + 3.92)) / Math.log(0.98));
   }

   private static boolean canSolid(PosVecUtils.Terrain terrain, int solidValue, int solidLimit, int solidValue2, int solidLimit2, int solidValue3, int solidLimit3) {
      for (int i = solidValue2; i <= solidLimit2; i++) {
         for (int j = solidValue; j <= solidLimit; j++) {
            for (int k = solidValue3; k <= solidLimit3; k++) {
               if (terrain.solid(j, i, k)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private PosVecUtils() {
   }

   private static PosVecUtils.Sample getSigmaGround(PosVecUtils.Sample sample, PosVecUtils.Kick kick) {
      return kick.set()
         ? new PosVecUtils.Sample(sample.x(), sample.y(), sample.z(), kick.vx(), kick.vy(), kick.vz(), sample.onGround(), sample.sigma())
         : new PosVecUtils.Sample(sample.x(), sample.y(), sample.z(), sample.vx() + kick.vx(), sample.vy() + kick.vy(), sample.vz() + kick.vz(), sample.onGround(), sample.sigma());
   }

   public static double getAmountDouble(double posVecValue, int exponent) {
      return -3.92 * exponent + 50.0 * (posVecValue + 3.92) * (1.0 - Math.pow(0.98, exponent));
   }

   public static PosVecUtils.Sample getDataForSampleTerrain(
      PosVecUtils.Sample sample, PosVecUtils.Terrain terrain, double posVecValue, double groundY, boolean posVecFlag, double posVecValue3
   ) {
      return getSigmaGround(sample, terrain, posVecValue, groundY, posVecFlag, posVecValue3, false);
   }

   public record Kick(int index, double vx, double vy, double vz, boolean set) {
   }

   public static final class Path {
      private final int sampleIndex;
      private final int ground;
      private final PosVecUtils.Sample[] pathSamples;

      private static int getGround(PosVecUtils.Sample[] samples) {
         boolean groundFlag = !samples[0].onGround();

         for (int i = 1; i < samples.length; i++) {
            if (samples[i].onGround()) {
               if (groundFlag) {
                  return i;
               }
            } else {
               groundFlag = true;
            }
         }

         return samples.length;
      }

      public PosVecUtils.Sample getDataForInt(int pathBound) {
         int index2 = pathBound < 0 ? 0 : Math.min(pathBound, this.pathSamples.length - 1);
         return this.pathSamples[index2];
      }

      public int getValue() {
         return this.pathSamples.length;
      }

      public int getSampleIndex() {
         return this.sampleIndex;
      }

      public double getAmount() {
         return this.pathSamples[this.sampleIndex].y();
      }

      public int getValueGround() {
         return this.ground;
      }

      private Path(PosVecUtils.Sample[] data59s) {
         this.pathSamples = data59s;
         this.ground = getGround(data59s);
         int value = 0;

         for (int i = 1; i < data59s.length; i++) {
            if (data59s[i].y() > data59s[value].y()) {
               value = i;
            }
         }

         this.sampleIndex = value;
      }
   }

   public record Sample(double x, double y, double z, double vx, double vy, double vz, boolean onGround, double sigma) {
   }

   public interface Terrain {
      boolean solid(int solidValue, int i, int k);
   }
}
