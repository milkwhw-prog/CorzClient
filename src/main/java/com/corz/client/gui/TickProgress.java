package com.corz.client.gui;

import net.minecraft.client.gui.DrawContext;

public class TickProgress {
   public final DrawContext drawContext;
   public final float tickProgress;

   public TickProgress(DrawContext drawContext2, float tickProgress) {
      this.drawContext = drawContext2;
      this.tickProgress = tickProgress;
   }
}
