package com.corz.client.utils;
import com.corz.client.utils.SubmitRadians;
import com.corz.client.utils.Cos;
import com.corz.client.utils.AmountsAmountUpdate;

public final class RadiansAge {
   public static final float radians = (float)Math.toRadians(30.0);
   private int age;
   private float smoothedAmount;
   private float ageAmount;
   public final SubmitRadians cosSubmitRadians;
   private int handleStepCount;
   private final Cos cosCos;
   private double x;
   private float bodyYaw;
   private double y;
   public final AmountsAmountUpdate minAmountsUpdate = new AmountsAmountUpdate();
   private float cosAmount;
   private boolean cosActive;
   private double z;

   public void handleAmountAge(int age2, double ageValue, double groundY, double ageValue3, float bodyYaw, float routedPartialValue, boolean ageFlag) {
      if (Double.isFinite(ageValue) && Double.isFinite(groundY) && Double.isFinite(ageValue3) && Float.isFinite(bodyYaw) && Float.isFinite(routedPartialValue)) {
         if (ageFlag) {
            this.minAmountsUpdate.updateAmountsMin();
            this.cosActive = false;
         } else if (!this.cosActive) {
            this.handleAmountCos(age2, ageValue, groundY, ageValue3, bodyYaw, routedPartialValue);
         } else {
            int ageLimit = age2 - this.age;
            if (ageLimit != 0) {
               if (ageLimit >= 0 && ageLimit <= 5) {
                  double ageFactor = ageValue - this.x;
                  double ageFactor2 = groundY - this.y;
                  double ageFactor3 = ageValue3 - this.z;
                  double ageFactor4 = 8.0 * ageLimit;
                  if (ageFactor * ageFactor + ageFactor2 * ageFactor2 + ageFactor3 * ageFactor3 > ageFactor4 * ageFactor4) {
                     this.handleAmountCos(age2, ageValue, groundY, ageValue3, bodyYaw, routedPartialValue);
                  } else {
                     float ageFactor5 = 1.0F / ageLimit;
                     float angle = bodyYaw * (float) (Math.PI / 180.0);
                     float sin = (float)(-Math.sin(angle));
                     float cos = (float)Math.cos(angle);
                     float cos2 = (float)(-Math.cos(angle));
                     float sin2 = (float)(-Math.sin(angle));
                     float ageValue5 = getAmount((float)(ageFactor * sin + ageFactor3 * cos) * ageFactor5, 4.0F);
                     float ageValue6 = getAmount((float)(ageFactor * cos2 + ageFactor3 * sin2) * ageFactor5, 4.0F);
                     float ageValue7 = getAmount((float)ageFactor2 * ageFactor5, 4.0F);
                     float ageValue8 = getAmount(getAmount(bodyYaw - this.bodyYaw) * ageFactor5 * (float) (Math.PI / 180.0), radians);
                     float cosAmount3 = this.cosAmount;
                     float ageAmount2 = this.ageAmount;
                     float smoothedAmount9 = this.smoothedAmount;
                     this.cosAmount = this.cosAmount + (ageValue5 - this.cosAmount) * 0.6F;
                     this.ageAmount = this.ageAmount + (ageValue6 - this.ageAmount) * 0.6F;
                     this.smoothedAmount = this.smoothedAmount + (ageValue7 - this.smoothedAmount) * 0.6F;
                     float ageFactor6 = 9.375F;
                     float ageValue10 = getAmount(-(this.cosAmount - cosAmount3) * ageFactor6, 1.6F);
                     float ageValue11 = getAmount(-(this.ageAmount - ageAmount2) * ageFactor6, 1.6F);
                     float ageValue12 = getAmount(-(this.smoothedAmount - smoothedAmount9) * ageFactor6, 1.6F);
                     this.handleCosAmount(routedPartialValue, -this.cosAmount, -this.ageAmount, -this.smoothedAmount, ageValue10, ageValue11, ageValue12, ageValue8);

                     for (int i = 0; i < ageLimit; i++) {
                        this.minAmountsUpdate.handleAmountMin(this.cosCos, this.cosSubmitRadians);
                     }

                     this.age = age2;
                     this.x = ageValue;
                     this.y = groundY;
                     this.z = ageValue3;
                     this.bodyYaw = bodyYaw;
                  }
               } else {
                  this.handleAmountCos(age2, ageValue, groundY, ageValue3, bodyYaw, routedPartialValue);
               }
            }
         }
      }
   }

   static float getAmount(float radiansAgeValue) {
      float radiansAgeTotal = radiansAgeValue % 360.0F;
      if (radiansAgeTotal >= 180.0F) {
         radiansAgeTotal -= 360.0F;
      }

      if (radiansAgeTotal < -180.0F) {
         radiansAgeTotal += 360.0F;
      }

      return radiansAgeTotal;
   }

   private void handleAmountCos(int age2, double cosValue, double cosValue2, double groundY, float bodyYaw, float angle) {
      this.cosActive = true;
      this.age = age2;
      this.x = cosValue;
      this.y = cosValue2;
      this.z = groundY;
      this.bodyYaw = bodyYaw;
      this.cosAmount = this.ageAmount = this.smoothedAmount = 0.0F;
      this.minAmountsUpdate.handleAmountsMin(this.cosCos.getAmountCos(angle), this.cosSubmitRadians);
      this.handleStepCount++;
   }

   public RadiansAge() {
      this.cosSubmitRadians = new SubmitRadians();
      this.cosCos = new Cos();
      SubmitRadians.handle(this.cosSubmitRadians);
   }

   public void handle(float radiansAgeValue, float matrixSubmitValue) {
      this.handleRadiansCos(radiansAgeValue, matrixSubmitValue, 0.0F);
   }

   private void handleCosAmount(float angle, float cosFactor, float amount, float cosFactor2, float cosFactor3, float amount2, float cosFactor4, float amount3) {
      float cos = (float)Math.cos(angle);
      float sin = (float)Math.sin(angle);
      this.cosCos.quatX = -cosFactor2 * cos - cosFactor * sin;
      this.cosCos.quatY = -cosFactor * cos + cosFactor2 * sin;
      this.cosCos.quatZ = amount;
      this.cosCos.quatW = cos;
      this.cosCos.negSinAngle = -sin;
      this.cosCos.tangentY = -cosFactor4 * cos - cosFactor3 * sin;
      this.cosCos.tangentX = -cosFactor3 * cos + cosFactor4 * sin;
      this.cosCos.tangentZ = amount2;
      this.cosCos.cosAmount = amount3;
   }

   public boolean isFlag() {
      return this.cosActive;
   }

   public void handleRadiansCos(float radiansCosValue, float matrixSubmitValue, float radiansAgeLimit) {
      this.handleCosAmount(
         Float.isFinite(radiansCosValue) ? radiansCosValue : 0.0F,
         -getAmount(radiansAgeLimit, 1.0F),
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         0.0F,
         getAmount(Float.isFinite(matrixSubmitValue) ? matrixSubmitValue : 0.0F, radians)
      );
      this.minAmountsUpdate.handleAmountMin(this.cosCos, this.cosSubmitRadians);
   }

   private static float getAmount(float radiansAgeLimit, float radiansAgeValue) {
      if (!Float.isFinite(radiansAgeLimit)) {
         return 0.0F;
      } else {
         return radiansAgeLimit < -radiansAgeValue ? -radiansAgeValue : (radiansAgeLimit > radiansAgeValue ? radiansAgeValue : radiansAgeLimit);
      }
   }

   public void updateCosMin() {
      this.minAmountsUpdate.handleAmountsMin(this.cosCos.getAmountCos(0.0F), this.cosSubmitRadians);
      this.cosActive = false;
      this.handleStepCount++;
   }

   public int getValue() {
      return this.handleStepCount;
   }
}
