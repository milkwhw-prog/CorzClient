package com.corz.client.utils;

public final class Cos {
   public float negSinAngle;
   public float cosAmount;
   public float tangentX;
   public float quatX;
   public float quatW = 1.0F;
   public float tangentY;
   public float quatY;
   public float tangentZ;
   public float quatZ;

   boolean isAmountCos() {
      return Float.isFinite(this.quatX)
         && Float.isFinite(this.quatY)
         && Float.isFinite(this.quatZ)
         && Float.isFinite(this.quatW)
         && Float.isFinite(this.negSinAngle)
         && Float.isFinite(this.tangentY)
         && Float.isFinite(this.tangentX)
         && Float.isFinite(this.tangentZ)
         && Float.isFinite(this.cosAmount);
   }

   public float getAmount() {
      return (float)Math.sqrt(this.quatX * this.quatX + this.quatY * this.quatY + this.quatZ * this.quatZ);
   }

   public Cos getAmountCos(float angle) {
      this.quatX = this.quatY = this.quatZ = 0.0F;
      this.tangentY = this.tangentX = this.tangentZ = 0.0F;
      this.cosAmount = 0.0F;
      this.quatW = (float)Math.cos(angle);
      this.negSinAngle = (float)(-Math.sin(angle));
      return this;
   }
}
