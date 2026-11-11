package com.corz.client.gui;

public class ZoomHud {
   private boolean zoomHudActive;
   private final double zoomFactor;

   public boolean isFlag() {
      return this.zoomHudActive;
   }

   public ZoomHud(double zoomFactorValue) {
      this.zoomFactor = zoomFactorValue;
   }

   public void updateFlag() {
      this.zoomHudActive = true;
   }

   public double getAmount() {
      return this.zoomFactor;
   }
}
