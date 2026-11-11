package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.ZoomHud;
import ai.auth.Module.BindMode;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Zoom extends Module {
   private final DoubleSetting doubleHelper = new DoubleSetting("Factor", "Zoom factor", 4.0, 2.0, 10.0, 0.5);
   private final EnumSetting enumHelper;
   private long lastNanoTime;
   private final BooleanSetting booleanBase;
   private final BooleanSetting smooth = new BooleanSetting("Smooth", "Smooth zoom in/out", true);
   private double holdToggleAmount;

   @Override
   protected void onZoomHud(ZoomHud zoomHud) {
      if (this.isEnabled() && this.booleanBase.isEnabled()) {
         double zoomDirection = zoomHud.getAmount() > 0.0 ? 0.5 : -0.5;
         double clampedFactor = Math.max(2.0, Math.min(10.0, this.doubleHelper.getValue() + zoomDirection));
         this.doubleHelper.handleMaxAmount(clampedFactor);
         zoomHud.updateFlag();
      }
   }

   public double getToggleHold() {
      long nanoTime = System.nanoTime();
      double holdBound = this.lastNanoTime == 0L ? 0.0 : (nanoTime - this.lastNanoTime) / 1.0E9;
      this.lastNanoTime = nanoTime;
      double holdToggleAmount = this.isEnabled() ? this.doubleHelper.getValue() : 1.0;
      if (!this.smooth.isEnabled()) {
         this.holdToggleAmount = holdToggleAmount;
         return this.holdToggleAmount;
      } else {
         double holdFactor = 1.0 - Math.exp(-14.0 * Math.max(0.0, holdBound));
         this.holdToggleAmount = this.holdToggleAmount + (holdToggleAmount - this.holdToggleAmount) * holdFactor;
         if (Math.abs(this.holdToggleAmount - holdToggleAmount) < 0.001) {
            this.holdToggleAmount = holdToggleAmount;
         }

         return this.holdToggleAmount;
      }
   }

   public Zoom() {
      super("Zoom", IconsPngType.MISC);
      this.booleanBase = new BooleanSetting("Scroll Zoom", "Scroll while zoomed to change the factor", true);
      this.enumHelper = new EnumSetting("Bind Mode", "How the keybind behaves: hold-to-zoom, or press to toggle.", "On Hold", "On Hold", "Toggle");
      this.holdToggleAmount = 1.0;
      this.lastNanoTime = 0L;
      this.addSettings(new ConfigSetting[]{this.doubleHelper, this.smooth, this.booleanBase, this.enumHelper});
   }

   @Override
   public BindMode getBindMode() {
      return "On Hold".equals(this.enumHelper.getCurrentOption()) ? BindMode.HOLD : BindMode.TOGGLE;
   }
}
