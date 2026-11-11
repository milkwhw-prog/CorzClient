package com.corz.client.utils;
import com.corz.client.utils.ForeShinUtils;
import com.corz.client.utils.LoftLatheFlag;

public final class BonecaAmbalabu {
   private final int regionOffsetY;
   private final int[] bonecaAmbalabuTable;
   private final int bonecaAmbalabuValue;
   public final int bonecaValue;
   public final int ambalabuBonecaValue;
   private final int regionOffsetX;
   private final boolean bonecaAmbalabuActive;

   public BonecaAmbalabu getClassForFloatInt(float classFactor, float classFactor2, float classFactor3, float amountForFloat, float classFactor5, float classFactor6, float classValue, int valueForIntFloat) {
      byte classLimit = 24;
      float[] floats = new float[classLimit + 1];
      float[] floats2 = new float[classLimit + 1];

      for (int i = 0; i <= classLimit; i++) {
         float classFactor7 = (float)i / classLimit;
         float classFactor8 = 1.0F - classFactor7;
         floats[i] = classFactor8 * classFactor8 * classFactor + 2.0F * classFactor8 * classFactor7 * classFactor3 + classFactor7 * classFactor7 * classFactor5;
         floats2[i] = classFactor8 * classFactor8 * classFactor2 + 2.0F * classFactor8 * classFactor7 * amountForFloat + classFactor7 * classFactor7 * classFactor6;
      }

      return this.getClassForFloatArrayFloat(floats, floats2, classValue, valueForIntFloat);
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuValue, int valueForInt) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         for (int i = 0; i < this.bonecaValue; i++) {
            int value2 = ForeShinUtils.getValue2(bonecaAmbalabuValue, valueForInt, (i + 0.5F) / this.bonecaValue);

            for (int j = 0; j < this.ambalabuBonecaValue; j++) {
               this.bonecaAmbalabuTable[(this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + j] = value2;
            }
         }

         return this;
      }
   }

   public BonecaAmbalabu getClassBonecaAmbalabu(float classBound, float i, float classBound3, float classBound4, int classValue) {
      float bonecaAmbalabuBound = Math.min(classBound, classBound3);
      float classLimit = Math.max(classBound, classBound3);
      float wrapDegreesBound = Math.min(i, classBound4);
      float hashHuntBound = Math.max(i, classBound4);
      return this.getBonecaAmbalabu(bonecaAmbalabuBound, wrapDegreesBound, classLimit, hashHuntBound, classValue, (classLimit3, classLimit4) -> classLimit3 >= bonecaAmbalabuBound && classLimit3 <= classLimit && classLimit4 >= wrapDegreesBound && classLimit4 <= hashHuntBound);
   }

   public int getBonecaAmbalabuForInt(int bonecaAmbalabuBound, int i) {
      if (this.bonecaAmbalabuActive) {
         return 0;
      } else {
         bonecaAmbalabuBound = Math.max(0, Math.min(this.ambalabuBonecaValue - 1, bonecaAmbalabuBound));
         i = Math.max(0, Math.min(this.bonecaValue - 1, i));
         return this.bonecaAmbalabuTable[(this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + bonecaAmbalabuBound];
      }
   }

   public BonecaAmbalabu getBonecaAmbalabu(float bonecaAmbalabuBound, float wrapDegreesBound, float classLimit, float hashHuntBound, int bonecaAmbalabuValue, BonecaAmbalabu.Inside inside) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         int max2 = Math.max(0, (int)Math.floor(Math.min(bonecaAmbalabuBound, classLimit)));
         int max3 = Math.max(0, (int)Math.floor(Math.min(wrapDegreesBound, hashHuntBound)));
         int bonecaAmbalabuLimit = Math.min(this.ambalabuBonecaValue - 1, (int)Math.ceil(Math.max(bonecaAmbalabuBound, classLimit)));
         int min2 = Math.min(this.bonecaValue - 1, (int)Math.ceil(Math.max(wrapDegreesBound, hashHuntBound)));
         float bonecaAmbalabuFactor = 0.25F;

         for (int i = max3; i <= min2; i++) {
            for (int j = max2; j <= bonecaAmbalabuLimit; j++) {
               int bonecaAmbalabuCounter = 0;

               for (int k = 0; k < 4; k++) {
                  for (int m = 0; m < 4; m++) {
                     if (inside.at(j + (m + 0.5F) * bonecaAmbalabuFactor, i + (k + 0.5F) * bonecaAmbalabuFactor)) {
                        bonecaAmbalabuCounter++;
                     }
                  }
               }

               if (bonecaAmbalabuCounter > 0) {
                  this.handleBonecaAmbalabu(j, i, bonecaAmbalabuValue, bonecaAmbalabuCounter / 16.0F);
               }
            }
         }

         return this;
      }
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuFactor2, float bonecaAmbalabuFactor3, int bonecaAmbalabuValue2) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         int maxRound = Math.max(1, Math.round(bonecaAmbalabuFactor));

         for (int i = 0; i < this.bonecaValue; i++) {
            for (int j = 0; j < this.ambalabuBonecaValue; j++) {
               float bonecaAmbalabuValue3 = (float)j / this.ambalabuBonecaValue * bonecaAmbalabuFactor;
               float bonecaAmbalabuValue4 = (getAmount(bonecaAmbalabuValue3 * 0.5F, i / 24.0F, Math.max(1, maxRound / 2), bonecaAmbalabuValue2) - 0.5F) * 2.0F * bonecaAmbalabuFactor2;
               float base = 0.5F + 0.5F * (float)Math.sin((float) (Math.PI * 2) * (bonecaAmbalabuValue3 + bonecaAmbalabuValue4));
               float base2 = 0.5F + 0.5F * (float)Math.sin((float) (Math.PI * 2) * (bonecaAmbalabuValue3 * 3.1F + bonecaAmbalabuValue4 * 1.7F + 0.37F));
               float bonecaAmbalabuLimit = (float)Math.pow(base, 10.0) * bonecaAmbalabuFactor3 + (float)Math.pow(base2, 14.0) * bonecaAmbalabuFactor3 * 0.45F;
               if (bonecaAmbalabuLimit > 0.004F) {
                  int index = (this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + j;
                  this.bonecaAmbalabuTable[index] = ForeShinUtils.getValue(this.bonecaAmbalabuTable[index], bonecaAmbalabuValue, Math.min(1.0F, bonecaAmbalabuLimit));
               }
            }
         }

         return this;
      }
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuValue, float bonecaAmbalabuDivisor, float bonecaAmbalabuLimit, float degrees) {
      float angle = (float)Math.toRadians(degrees);
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      return this.getBonecaAmbalabu(0.0F, 0.0F, this.ambalabuBonecaValue, this.bonecaValue, bonecaAmbalabuValue, (bonecaAmbalabuFactor, bonecaAmbalabuFactor2) -> {
         float var6x = bonecaAmbalabuFactor * cos + bonecaAmbalabuFactor2 * sin;
         float var7x = var6x - (float)Math.floor(var6x / bonecaAmbalabuDivisor) * bonecaAmbalabuDivisor;
         return var7x < bonecaAmbalabuLimit;
      });
   }

   private static float getAmount(float bonecaAmbalabuValue, float ysx, float classBound, float amountForFloat, float bonecaAmbalabuValue5, float hashHuntBound) {
      float deltaY = bonecaAmbalabuValue5 - classBound;
      float bonecaAmbalabuFactor = hashHuntBound - amountForFloat;
      float bonecaAmbalabuLimit = deltaY * deltaY + bonecaAmbalabuFactor * bonecaAmbalabuFactor;
      float bonecaAmbalabuLimit2 = bonecaAmbalabuLimit > 0.0F ? ((bonecaAmbalabuValue - classBound) * deltaY + (ysx - amountForFloat) * bonecaAmbalabuFactor) / bonecaAmbalabuLimit : 0.0F;
      float bonecaAmbalabuFactor2 = bonecaAmbalabuLimit2 < 0.0F ? 0.0F : (bonecaAmbalabuLimit2 > 1.0F ? 1.0F : bonecaAmbalabuLimit2);
      float bonecaAmbalabuFactor3 = classBound + bonecaAmbalabuFactor2 * deltaY - bonecaAmbalabuValue;
      float bonecaAmbalabuFactor4 = amountForFloat + bonecaAmbalabuFactor2 * bonecaAmbalabuFactor - ysx;
      return bonecaAmbalabuFactor3 * bonecaAmbalabuFactor3 + bonecaAmbalabuFactor4 * bonecaAmbalabuFactor4;
   }

   private void handleBonecaAmbalabu(int bonecaAmbalabuLimit, int i, int bonecaAmbalabuValue, float foreShinFactor) {
      if (bonecaAmbalabuLimit >= 0 && i >= 0 && bonecaAmbalabuLimit < this.ambalabuBonecaValue && i < this.bonecaValue) {
         int index = (this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + bonecaAmbalabuLimit;
         this.bonecaAmbalabuTable[index] = ForeShinUtils.getValue(this.bonecaAmbalabuTable[index], bonecaAmbalabuValue, foreShinFactor);
      }
   }

   public BonecaAmbalabu getClassForFloatArrayFloat(float[] classValues, float[] classValues2, float classValue, int valueForIntFloat) {
      float classFactor = classValue * 0.5F;
      float classBound = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;
      float maxValue4 = -Float.MAX_VALUE;

      for (int i = 0; i < classValues.length; i++) {
         classBound = Math.min(classBound, classValues[i]);
         pilotStatusBound = Math.max(pilotStatusBound, classValues[i]);
         maxValue = Math.min(maxValue, classValues2[i]);
         maxValue4 = Math.max(maxValue4, classValues2[i]);
      }

      return this.getBonecaAmbalabu(classBound - classFactor, maxValue - classFactor, pilotStatusBound + classFactor, maxValue4 + classFactor, valueForIntFloat, (xsx, ysx) -> {
         for (int var5x = 0; var5x + 1 < classValues.length; var5x++) {
            if (getAmount(xsx, ysx, classValues[var5x], classValues2[var5x], classValues[var5x + 1], classValues2[var5x + 1]) <= classFactor * classFactor) {
               return true;
            }
         }

         return false;
      });
   }

   public BonecaAmbalabu getBonecaAmbalabuBonecaAmbalabuActive(int bonecaAmbalabuValue, int valueForInt) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         for (int i = 0; i < this.ambalabuBonecaValue; i++) {
            int value2 = ForeShinUtils.getValue2(bonecaAmbalabuValue, valueForInt, (i + 0.5F) / this.ambalabuBonecaValue);

            for (int j = 0; j < this.bonecaValue; j++) {
               this.bonecaAmbalabuTable[(this.regionOffsetY + j) * this.bonecaAmbalabuValue + this.regionOffsetX + i] = value2;
            }
         }

         return this;
      }
   }

   static float getAmount(float bonecaAmbalabuValue, float hashRegistryValue, int maxRound, int bonecaAmbalabuValue4) {
      float bonecaAmbalabuTotal = 0.0F;
      float bonecaAmbalabuFactor = 0.5F;
      float bonecaAmbalabuTotal2 = 0.0F;

      for (int i = 0; i < 3; i++) {
         bonecaAmbalabuTotal += getAmountForFloatInt(bonecaAmbalabuValue, hashRegistryValue, maxRound, bonecaAmbalabuValue4 + i * 101) * bonecaAmbalabuFactor;
         bonecaAmbalabuTotal2 += bonecaAmbalabuFactor;
         bonecaAmbalabuFactor *= 0.5F;
         bonecaAmbalabuValue *= 2.0F;
         hashRegistryValue *= 2.0F;
         maxRound *= 2;
      }

      return bonecaAmbalabuTotal / bonecaAmbalabuTotal2;
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuBound, int bonecaAmbalabuValue2) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         int maxRound = Math.max(1, Math.round(this.ambalabuBonecaValue / Math.max(1.0F, bonecaAmbalabuBound)));

         for (int i = 0; i < this.bonecaValue; i++) {
            for (int j = 0; j < this.ambalabuBonecaValue; j++) {
               float bonecaAmbalabuValue3 = getAmount((float)(j * maxRound) / this.ambalabuBonecaValue, i / Math.max(1.0F, bonecaAmbalabuBound), maxRound, bonecaAmbalabuValue2);
               float foreShinFactor = Math.max(0.0F, Math.min(1.0F, (bonecaAmbalabuValue3 - 0.5F) * 2.0F * bonecaAmbalabuFactor + bonecaAmbalabuFactor * 0.5F));
               int index = (this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + j;
               this.bonecaAmbalabuTable[index] = ForeShinUtils.getValue(this.bonecaAmbalabuTable[index], bonecaAmbalabuValue, foreShinFactor);
            }
         }

         return this;
      }
   }

   BonecaAmbalabu(LoftLatheFlag.Region region) {
      this.bonecaAmbalabuTable = region.regionLoftLatheFlag.loftLatheTable;
      this.bonecaAmbalabuActive = region.regionLoftLatheFlag.loftLatheActive;
      this.bonecaAmbalabuValue = region.regionLoftLatheFlag.atlasWidth;
      this.regionOffsetX = region.regionValue;
      this.regionOffsetY = region.regionY;
      this.ambalabuBonecaValue = region.regionWidth;
      this.bonecaValue = region.shinFootValue;
   }

   public BonecaAmbalabu getBonecaAmbalabuForIntFloat(int bonecaAmbalabuValue, float bonecaAmbalabuFactor, float bonecaAmbalabuFactor2, int m) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         int maxRound = Math.max(1, Math.round(bonecaAmbalabuFactor * this.ambalabuBonecaValue * this.bonecaValue / 100.0F));

         for (int i = 0; i < maxRound; i++) {
            float classValue = getAmount(i, 1, m) * this.ambalabuBonecaValue;
            float amountForFloat = getAmount(i, 2, m) * this.bonecaValue;
            float classDivisor = bonecaAmbalabuFactor2 * (0.6F + 0.8F * getAmount(i, 3, m));
            this.getClass022(classValue, amountForFloat, classDivisor, classDivisor, bonecaAmbalabuValue);
         }

         return this;
      }
   }

   public BonecaAmbalabu getClass02(float classBound, float amountForFloat, float hashHuntBound, float classBound4, float classValue, int classValue2) {
      float classFactor = classValue * 0.5F;
      return this.getBonecaAmbalabu(
         Math.min(classBound, hashHuntBound) - classFactor,
         Math.min(amountForFloat, classBound4) - classFactor,
         Math.max(classBound, hashHuntBound) + classFactor,
         Math.max(amountForFloat, classBound4) + classFactor,
         classValue2,
         (classValue3, ysx) -> getAmount(classValue3, ysx, classBound, amountForFloat, hashHuntBound, classBound4) <= classFactor * classFactor
      );
   }

   public BonecaAmbalabu getClass02(float classValue, float bonecaAmbalabuFactor, float classFactor, float classFactor2, int m, int classValue4) {
      float[] floats = new float[5];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = getAmount(i, 7, m) * (float) (Math.PI * 2);
      }

      return this.getBonecaAmbalabu(
         classValue - classFactor * (1.0F + classFactor2),
         bonecaAmbalabuFactor - classFactor * (1.0F + classFactor2),
         classValue + classFactor * (1.0F + classFactor2),
         bonecaAmbalabuFactor + classFactor * (1.0F + classFactor2),
         classValue4,
         (wobblex, classValue5) -> {
            float var7x = wobblex - classValue;
            float var8x = classValue5 - bonecaAmbalabuFactor;
            float angle = (float)Math.atan2(var8x, var7x);
            float classFactor3 = 1.0F
               + classFactor2
                  * (
                     0.5F * (float)Math.sin(2.0F * angle + floats[0])
                        + 0.3F * (float)Math.sin(3.0F * angle + floats[1])
                        + 0.2F * (float)Math.sin(5.0F * angle + floats[2])
                  );
            return var7x * var7x + var8x * var8x <= classFactor * classFactor * classFactor3 * classFactor3;
         }
      );
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuValue) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         for (int i = 0; i < this.bonecaValue; i++) {
            for (int j = 0; j < this.ambalabuBonecaValue; j++) {
               this.bonecaAmbalabuTable[(this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + j] = bonecaAmbalabuValue;
            }
         }

         return this;
      }
   }

   public BonecaAmbalabu getBonecaAmbalabu(int bonecaAmbalabuLimit, int i, int bonecaAmbalabuValue) {
      if (!this.bonecaAmbalabuActive && bonecaAmbalabuLimit >= 0 && i >= 0 && bonecaAmbalabuLimit < this.ambalabuBonecaValue && i < this.bonecaValue) {
         this.bonecaAmbalabuTable[(this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + bonecaAmbalabuLimit] = bonecaAmbalabuValue;
      }

      return this;
   }

   static float getAmount(int bonecaAmbalabuValue, int bonecaAmbalabuValue2, int m) {
      int bonecaAmbalabuValue4 = bonecaAmbalabuValue * 374761393 + bonecaAmbalabuValue2 * 668265263 + m * 1442695041;
      int bonecaAmbalabuValue5 = (bonecaAmbalabuValue4 ^ bonecaAmbalabuValue4 >>> 13) * 1274126177;
      int bonecaAmbalabuValue6 = bonecaAmbalabuValue5 ^ bonecaAmbalabuValue5 >>> 16;
      return (bonecaAmbalabuValue6 & 16777215) / 1.6777215E7F;
   }

   public BonecaAmbalabu getClass022(float classValue, float amountForFloat, float classDivisor, float classDivisor2, int classValue3) {
      return !(classDivisor <= 0.0F) && !(classDivisor2 <= 0.0F) ? this.getBonecaAmbalabu(classValue - classDivisor, amountForFloat - classDivisor2, classValue + classDivisor, amountForFloat + classDivisor2, classValue3, (ryx, rxx) -> {
         float classFactor = (ryx - classValue) / classDivisor;
         float classFactor2 = (rxx - amountForFloat) / classDivisor2;
         return classFactor * classFactor + classFactor2 * classFactor2 <= 1.0F;
      }) : this;
   }

   public BonecaAmbalabu getBonecaAmbalabu(float bonecaAmbalabuFactor, float bonecaAmbalabuBound, int bonecaAmbalabuValue) {
      if (this.bonecaAmbalabuActive) {
         return this;
      } else {
         int maxRound = Math.max(1, Math.round(this.ambalabuBonecaValue / Math.max(1.0F, bonecaAmbalabuBound)));

         for (int i = 0; i < this.bonecaValue; i++) {
            for (int j = 0; j < this.ambalabuBonecaValue; j++) {
               float bonecaAmbalabuValue2 = getAmount((float)(j * maxRound) / this.ambalabuBonecaValue, i / Math.max(1.0F, bonecaAmbalabuBound), maxRound, bonecaAmbalabuValue);
               float foreShinFactor = 1.0F + bonecaAmbalabuFactor * (2.0F * bonecaAmbalabuValue2 - 1.0F);
               int index = (this.regionOffsetY + i) * this.bonecaAmbalabuValue + this.regionOffsetX + j;
               this.bonecaAmbalabuTable[index] = ForeShinUtils.getValue(this.bonecaAmbalabuTable[index], foreShinFactor);
            }
         }

         return this;
      }
   }

   private static float getAmountForFloatInt(float bonecaAmbalabuValue, float hashRegistryValue, int rowsSlotsLimit, int m) {
      int i = (int)Math.floor(bonecaAmbalabuValue);
      int floor6 = (int)Math.floor(hashRegistryValue);
      float bonecaAmbalabuFactor = bonecaAmbalabuValue - i;
      float bonecaAmbalabuFactor2 = hashRegistryValue - floor6;
      float bonecaAmbalabuFactor3 = bonecaAmbalabuFactor * bonecaAmbalabuFactor * (3.0F - 2.0F * bonecaAmbalabuFactor);
      float bonecaAmbalabuFactor4 = bonecaAmbalabuFactor2 * bonecaAmbalabuFactor2 * (3.0F - 2.0F * bonecaAmbalabuFactor2);
      int floorMod = Math.floorMod(i, rowsSlotsLimit);
      int floorMod2 = Math.floorMod(i + 1, rowsSlotsLimit);
      float bonecaAmbalabuValue7 = getAmount(floorMod, floor6, m);
      float bonecaAmbalabuValue8 = getAmount(floorMod2, floor6, m);
      float bonecaAmbalabuValue9 = getAmount(floorMod, floor6 + 1, m);
      float bonecaAmbalabuValue10 = getAmount(floorMod2, floor6 + 1, m);
      return bonecaAmbalabuValue7 + (bonecaAmbalabuValue8 - bonecaAmbalabuValue7) * bonecaAmbalabuFactor3 + (bonecaAmbalabuValue9 + (bonecaAmbalabuValue10 - bonecaAmbalabuValue9) * bonecaAmbalabuFactor3 - (bonecaAmbalabuValue7 + (bonecaAmbalabuValue8 - bonecaAmbalabuValue7) * bonecaAmbalabuFactor3)) * bonecaAmbalabuFactor4;
   }

   public BonecaAmbalabu getClass022(float classValue, float amountForFloat, float classDivisor, float classDivisor2, float classValue3, int classValue4) {
      float classDivisor3 = classDivisor - classValue3;
      float classDivisor4 = classDivisor2 - classValue3;
      return this.getBonecaAmbalabu(classValue - classDivisor, amountForFloat - classDivisor2, classValue + classDivisor, amountForFloat + classDivisor2, classValue4, (classValue5, iry) -> {
         float var8x = (classValue5 - classValue) / classDivisor;
         float classFactor = (iry - amountForFloat) / classDivisor2;
         if (var8x * var8x + classFactor * classFactor > 1.0F) {
            return false;
         } else if (!(classDivisor3 <= 0.0F) && !(classDivisor4 <= 0.0F)) {
            float classFactor2 = (classValue5 - classValue) / classDivisor3;
            float classFactor3 = (iry - amountForFloat) / classDivisor4;
            return classFactor2 * classFactor2 + classFactor3 * classFactor3 > 1.0F;
         } else {
            return true;
         }
      });
   }

   public BonecaAmbalabu getClassForIntFloatArray(int classValue, float... classValues) {
      float classBound = Float.MAX_VALUE;
      float maxValue = Float.MAX_VALUE;
      float maxValue3 = -Float.MAX_VALUE;
      float pilotStatusBound = -Float.MAX_VALUE;

      for (int i = 0; i + 1 < classValues.length; i += 2) {
         classBound = Math.min(classBound, classValues[i]);
         maxValue3 = Math.max(maxValue3, classValues[i]);
         maxValue = Math.min(maxValue, classValues[i + 1]);
         pilotStatusBound = Math.max(pilotStatusBound, classValues[i + 1]);
      }

      int classLimit = classValues.length / 2;
      return this.getBonecaAmbalabu(classBound, maxValue, maxValue3, pilotStatusBound, classValue, (classValue2, classValue3) -> {
         boolean var4x = false;
         int var5x = 0;

         for (int var6x = classLimit - 1; var5x < classLimit; var6x = var5x++) {
            float var7x = classValues[2 * var5x];
            float var8x = classValues[2 * var5x + 1];
            float classValue4 = classValues[2 * var6x];
            float classLimit2 = classValues[2 * var6x + 1];
            if (var8x > classValue3 != classLimit2 > classValue3 && classValue2 < (classValue4 - var7x) * (classValue3 - var8x) / (classLimit2 - var8x) + var7x) {
               var4x = !var4x;
            }
         }

         return var4x;
      });
   }

   public interface Inside {
      boolean at(float insideValue, float insideValue2);
   }
}
