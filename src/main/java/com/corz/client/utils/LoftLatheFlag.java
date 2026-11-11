package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import com.corz.client.utils.BonecaAmbalabu;

public final class LoftLatheFlag {
   public final int[] loftLatheTable;
   private final List<LoftLatheFlag.Region> loftLatheRegionList;
   public final int atlasHeight;
   private int loftLatheValue;
   final LoftLatheFlag.Region loftLatheRegion;
   public final boolean loftLatheActive;
   private int rowMaxHeight;
   public final int atlasWidth;
   private int loftLatheCount = 2;

   void updateFlag() {
      if (!this.loftLatheActive) {
         for (LoftLatheFlag.Region region : this.loftLatheRegionList) {
            this.handle(region);
         }
      }
   }

   public LoftLatheFlag(int loftLatheValue, int loftLatheValue2) {
      this(loftLatheValue, loftLatheValue2, false);
   }

   private void handle(LoftLatheFlag.Region region) {
      for (int i = region.regionY - 2; i < region.regionY + region.shinFootValue + 2; i++) {
         if (i >= 0 && i < this.atlasHeight) {
            int loftLatheFactor = Math.max(region.regionY, Math.min(region.regionY + region.shinFootValue - 1, i));

            for (int j = region.regionValue - 2; j < region.regionValue + region.regionWidth + 2; j++) {
               if (j >= 0 && j < this.atlasWidth && (j < region.regionValue || j >= region.regionValue + region.regionWidth || i < region.regionY || i >= region.regionY + region.shinFootValue)) {
                  int loftLatheValue = Math.max(region.regionValue, Math.min(region.regionValue + region.regionWidth - 1, j));
                  this.loftLatheTable[i * this.atlasWidth + j] = this.loftLatheTable[loftLatheFactor * this.atlasWidth + loftLatheValue];
               }
            }
         }
      }
   }

   LoftLatheFlag(int value, int value2, boolean flag) {
      this.loftLatheValue = 2;
      this.loftLatheRegionList = new ArrayList<>();
      this.atlasWidth = value;
      this.atlasHeight = value2;
      this.loftLatheActive = flag;
      this.loftLatheTable = flag ? new int[0] : new int[value * value2];
      this.loftLatheRegion = this.getMax(4, 4);
      this.loftLatheRegion.getClassBonecaAmbalabu().getBonecaAmbalabu(-1);
   }

   public int getMax() {
      return this.loftLatheValue + this.rowMaxHeight + 2;
   }

   public LoftLatheFlag.Region getMax(int loftLatheLimit, int round) {
      if (loftLatheLimit >= 1 && round >= 1) {
         if (loftLatheLimit + 4 <= this.atlasWidth && round + 4 <= this.atlasHeight) {
            if (this.loftLatheCount + loftLatheLimit + 2 > this.atlasWidth) {
               this.loftLatheCount = 2;
               this.loftLatheValue = this.loftLatheValue + this.rowMaxHeight + 4;
               this.rowMaxHeight = 0;
            }

            if (this.loftLatheValue + round + 2 > this.atlasHeight) {
               throw new IllegalStateException("atlas " + this.atlasWidth + "x" + this.atlasHeight + " full: raise atlasSize()");
            } else {
               LoftLatheFlag.Region region = new LoftLatheFlag.Region(this, this.loftLatheCount, this.loftLatheValue, loftLatheLimit, round);
               this.loftLatheRegionList.add(region);
               this.loftLatheCount += loftLatheLimit + 4;
               this.rowMaxHeight = Math.max(this.rowMaxHeight, round);
               return region;
            }
         } else {
            throw new IllegalStateException("region " + loftLatheLimit + "x" + round + " larger than atlas " + this.atlasWidth + "x" + this.atlasHeight);
         }
      } else {
         throw new IllegalArgumentException("region " + loftLatheLimit + "x" + round);
      }
   }

   public static final class Region {
      public final int regionWidth;
      public final int shinFootValue;
      public final int regionValue;
      public final int regionY;
      public final LoftLatheFlag regionLoftLatheFlag;

      public float getMax(float regionLimit) {
         regionLimit = regionLimit < 0.0F ? 0.0F : (regionLimit > 1.0F ? 1.0F : regionLimit);
         return (this.regionY + 0.5F + regionLimit * (this.shinFootValue - 1)) / this.regionLoftLatheFlag.atlasHeight;
      }

      public BonecaAmbalabu getClassBonecaAmbalabu() {
         return new BonecaAmbalabu(this);
      }

      public float getMaxForFloat(float regionLimit) {
         regionLimit = regionLimit < 0.0F ? 0.0F : (regionLimit > 1.0F ? 1.0F : regionLimit);
         return (this.regionValue + 0.5F + regionLimit * (this.regionWidth - 1)) / this.regionLoftLatheFlag.atlasWidth;
      }

      Region(LoftLatheFlag max, int value, int value2, int value3, int value4) {
         this.regionLoftLatheFlag = max;
         this.regionValue = value;
         this.regionY = value2;
         this.regionWidth = value3;
         this.shinFootValue = value4;
      }
   }
}
