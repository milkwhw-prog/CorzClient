package com.corz.client.utils;

public final class TrailBuffer {
   private double latestHeight;
   private final double[] heightTable;
   public int entityId = Integer.MIN_VALUE;
   private boolean lerpHeightActive;
   private final boolean[] trailBufferFlags;
   private double lerpLerpHeight;
   private int pointCount;
   private final double[] lerpTable;
   private final float[] trailBufferTable;
   private final double[] table;
   private int trailBufferValue;
   private float height;
   private double lerp2LerpHeight;
   public long lastUpdateFrame;
   private float heightAmount;
   private final double[] pointTimestamps;
   private final int pointCapacity;
   private double lerp3LerpHeight;
   private boolean heightActive;
   private final double[] pointXs;

   public double getAmount() {
      return this.latestHeight;
   }

   public boolean canFlags(int trailBufferValue) {
      return trailBufferValue == 0 || this.trailBufferFlags[this.getMax(trailBufferValue)];
   }

   private void handleLerpHeight(double amount, double lerp, double lerp2, double lerp3, float height2) {
      this.lerpHeightActive = true;
      this.latestHeight = amount;
      this.lerpLerpHeight = lerp;
      this.lerp2LerpHeight = lerp2;
      this.lerp3LerpHeight = lerp3;
      this.height = height2;
   }

   private int getMax(int trailBufferValue) {
      return (this.trailBufferValue + trailBufferValue) % this.pointCapacity;
   }

   public TrailBuffer() {
      this(384);
   }

   public TrailBuffer(int trailBufferBound) {
      this.pointCapacity = Math.max(8, trailBufferBound);
      this.table = new double[this.pointCapacity];
      this.pointXs = new double[this.pointCapacity];
      this.heightTable = new double[this.pointCapacity];
      this.lerpTable = new double[this.pointCapacity];
      this.pointTimestamps = new double[this.pointCapacity];
      this.trailBufferTable = new float[this.pointCapacity];
      this.trailBufferFlags = new boolean[this.pointCapacity];
   }

   public double getAmounts(int trailBufferValue) {
      return this.table[this.getMax(trailBufferValue)];
   }

   public float getAmountsForInt(int trailBufferValue) {
      return this.trailBufferTable[this.getMax(trailBufferValue)];
   }

   public double getLerp3LerpHeight() {
      return this.lerp3LerpHeight;
   }

   public static float getAmount(float trailBufferValue, float trailBufferValue2, double trailBufferLimit) {
      return !(trailBufferLimit > 0.0) ? trailBufferValue : trailBufferValue + (float)((trailBufferValue2 - trailBufferValue) * (1.0 - Math.exp(-trailBufferLimit / 0.12)));
   }

   public void handleAmountsHeight(double heightValue, double lerp, double heightValue3, double heightValue4, float height, double trailBufferBound) {
      if (!this.lerpHeightActive) {
         this.heightAmount = height;
         this.handleLerpHeight(heightValue, lerp, heightValue3, heightValue4, height);
         this.handleAmountsFlags(heightValue, lerp, heightValue3, heightValue4, height, 0.0, true);
         this.heightActive = false;
      } else {
         double heightValue6 = heightValue - this.latestHeight;
         double trailBufferLimit = getAmount(lerp, heightValue3, heightValue4, this.lerpLerpHeight, this.lerp2LerpHeight, this.lerp3LerpHeight);
         if (canFlag(trailBufferLimit, heightValue6)) {
            this.heightActive = true;
         }

         this.heightAmount = getAmount(this.heightAmount, height, heightValue6);
         float heightAmount2 = this.heightAmount;
         this.handleLerpHeight(heightValue, lerp, heightValue3, heightValue4, heightAmount2);
         if (!this.heightActive && this.pointCount != 0) {
            int index = this.getMax(this.pointCount - 1);
            double heightLimit = getAmount(lerp, heightValue3, heightValue4, this.pointXs[index], this.heightTable[index], this.lerpTable[index]);
            double heightLimit2 = heightValue - this.table[index];
            boolean heightFlag = heightLimit > 1.0E-4 && heightLimit2 >= 0.1;
            if (!(heightLimit < 0.05) || heightFlag) {
               if (!(heightLimit2 < getAmount(trailBufferBound, this.pointCapacity))) {
                  this.handleAmountsFlags(heightValue, lerp, heightValue3, heightValue4, heightAmount2, this.pointTimestamps[index] + heightLimit, false);
               }
            }
         } else {
            double heightValue8 = this.pointCount == 0 ? 0.0 : this.pointTimestamps[this.getMax(this.pointCount - 1)];
            this.handleAmountsFlags(heightValue, lerp, heightValue3, heightValue4, heightAmount2, heightValue8, true);
            this.heightActive = false;
         }
      }
   }

   public void handleAmountsFlags(double trailBufferValue, double trailsValue) {
      double trailBufferLimit = trailBufferValue - trailsValue;

      while (this.pointCount >= 2) {
         int index = this.getMax(0);
         int max2 = this.getMax(1);
         boolean trailBufferFlag = this.table[max2] <= trailBufferLimit;
         boolean trailBufferFlag2 = this.trailBufferFlags[max2] && this.table[index] <= trailBufferLimit;
         if (!trailBufferFlag && !trailBufferFlag2) {
            break;
         }

         this.updateFlagsMax();
      }
   }

   public int getMax() {
      return this.pointCapacity;
   }

   public double getAmountsHeightTable(int trailBufferValue) {
      return this.heightTable[this.getMax(trailBufferValue)];
   }

   public static boolean canFlag(double trailBufferLimit, double trailBufferBound) {
      double trailBufferValue = Math.min(Math.max(trailBufferBound, 0.0), 0.25);
      return trailBufferLimit > Math.max(1.0, 80.0 * trailBufferValue);
   }

   public boolean canLerpAmounts(double lerpValue, double trailsValue) {
      if (!this.lerpHeightActive) {
         return true;
      } else {
         double lerpValue3 = lerpValue - trailsValue;
         if (this.latestHeight > lerpValue3) {
            if (this.pointCount == 0) {
               return true;
            } else {
               int index = this.getMax(this.pointCount - 1);
               return this.table[index] > lerpValue3
                  ? false
                  : getAmount(this.lerpLerpHeight, this.lerp2LerpHeight, this.lerp3LerpHeight, this.pointXs[index], this.heightTable[index], this.lerpTable[index]) < 1.0E-6;
            }
         } else {
            return true;
         }
      }
   }

   public float getHeight() {
      return this.height;
   }

   public double getLerp() {
      return this.lerpLerpHeight;
   }

   public double getAmountsPointTimestamps(int trailBufferValue) {
      return this.pointTimestamps[this.getMax(trailBufferValue)];
   }

   public int getValue() {
      int trailBufferCounter = 0;

      for (int i = 0; i < this.pointCount; i++) {
         if (this.canFlags(i)) {
            trailBufferCounter++;
         }
      }

      return trailBufferCounter;
   }

   public int getPointCount() {
      return this.pointCount;
   }

   public static double getAmount(double trailBufferBound, int trailBufferValue) {
      return Math.max(0.0, trailBufferBound) / Math.max(1, trailBufferValue - 16);
   }

   public void updateFlag() {
      this.trailBufferValue = 0;
      this.pointCount = 0;
      this.heightActive = false;
      this.lerpHeightActive = false;
   }

   public boolean isFlag() {
      return this.lerpHeightActive;
   }

   public void updateFlagHeightActive() {
      this.heightActive = true;
   }

   private void updateFlagsMax() {
      this.trailBufferValue = (this.trailBufferValue + 1) % this.pointCapacity;
      this.pointCount--;
      if (this.pointCount > 0) {
         this.trailBufferFlags[this.trailBufferValue] = true;
      }
   }

   public double getAmountsPointXs(int trailBufferValue) {
      return this.pointXs[this.getMax(trailBufferValue)];
   }

   static double getAmount(double trailBufferValue, double trailBufferValue2, double trailBufferValue3, double trailBufferValue4, double trailBufferValue5, double trailBufferValue6) {
      double trailBufferFactor = trailBufferValue - trailBufferValue4;
      double trailBufferFactor2 = trailBufferValue2 - trailBufferValue5;
      double trailBufferFactor3 = trailBufferValue3 - trailBufferValue6;
      return Math.sqrt(trailBufferFactor * trailBufferFactor + trailBufferFactor2 * trailBufferFactor2 + trailBufferFactor3 * trailBufferFactor3);
   }

   public double getLerp2LerpHeight() {
      return this.lerp2LerpHeight;
   }

   public double getAmountsLerpTable(int trailBufferValue) {
      return this.lerpTable[this.getMax(trailBufferValue)];
   }

   private void handleAmountsFlags(double trailBufferValue, double lerp, double trailBufferValue3, double trailBufferValue4, float trailBufferValue5, double trailBufferValue6, boolean trailBufferFlag) {
      if (this.pointCount == this.pointCapacity) {
         this.updateFlagsMax();
      }

      int index = this.getMax(this.pointCount);
      this.table[index] = trailBufferValue;
      this.pointXs[index] = lerp;
      this.heightTable[index] = trailBufferValue3;
      this.lerpTable[index] = trailBufferValue4;
      this.trailBufferTable[index] = trailBufferValue5;
      this.pointTimestamps[index] = trailBufferValue6;
      this.trailBufferFlags[index] = trailBufferFlag || this.pointCount == 0;
      this.pointCount++;
   }
}
