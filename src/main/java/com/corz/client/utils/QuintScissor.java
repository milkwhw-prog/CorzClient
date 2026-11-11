package com.corz.client.utils;
import com.corz.client.utils.QuintBounceType;

public class QuintScissor implements Cloneable {
   private double targetAmount;
   private boolean cloneActive;
   private long nanoTimeClone;
   private double quintAmount;
   private double cloneAmount;
   private final QuintBounceType quintBounceType;
   private int durationMs;

   public long getTime() {
      return (System.nanoTime() - this.nanoTimeClone) / 1000000L;
   }

   public int getValue() {
      return this.durationMs;
   }

   public void updateAmountTime() {
      this.nanoTimeClone = System.nanoTime();
      this.cloneActive = true;
      this.cloneAmount = this.quintAmount;
   }

   public double getAmount() {
      this.updateAmountQuint();
      return this.quintAmount;
   }

   public void handleTime(long quintScissorValue) {
      this.nanoTimeClone = System.nanoTime() - (System.currentTimeMillis() - quintScissorValue) * 1000000L;
   }

   public QuintScissor(QuintBounceType quintBounceType, int quintScissorValue) {
      this(quintBounceType, quintScissorValue, 0.0);
   }

   public void handleAmountFlag(double amount) {
      this.cloneAmount = amount;
      this.targetAmount = amount;
      this.quintAmount = amount;
      this.cloneActive = false;
   }

   public double getTargetAmount() {
      return this.targetAmount;
   }

   public void setValue(int value) {
      this.durationMs = value;
   }

   public QuintScissor clone() {
      QuintScissor quintScissor = new QuintScissor(this.quintBounceType, this.durationMs);
      quintScissor.nanoTimeClone = this.nanoTimeClone;
      quintScissor.cloneAmount = this.cloneAmount;
      quintScissor.targetAmount = this.targetAmount;
      quintScissor.quintAmount = this.quintAmount;
      quintScissor.cloneActive = this.cloneActive;
      return quintScissor;
   }

   public void handleAmountTime(double amount) {
      this.cloneAmount = this.quintAmount;
      this.targetAmount = amount;
      this.nanoTimeClone = System.nanoTime();
      this.cloneActive = true;
   }

   public QuintScissor(QuintBounceType quintBounceType, int value, double amount) {
      this.quintBounceType = quintBounceType;
      this.durationMs = value;
      this.cloneAmount = amount;
      this.targetAmount = amount;
      this.quintAmount = amount;
      this.nanoTimeClone = System.nanoTime();
      this.cloneActive = false;
   }

   private void updateAmountQuint() {
      if (this.cloneActive) {
         double millis = (System.nanoTime() - this.nanoTimeClone) / 1000000.0;
         if (millis >= this.durationMs) {
            this.quintAmount = this.targetAmount;
            this.cloneActive = false;
         } else {
            double unaryOperator = this.quintBounceType.getUnaryOperator(millis / this.durationMs);
            this.quintAmount = this.cloneAmount + (this.targetAmount - this.cloneAmount) * unaryOperator;
         }
      }
   }
}
