package com.corz.client.gui;

import net.minecraft.client.gui.DrawContext;

public class FillRenderUtils {
   public static void renderFill(DrawContext context, int startX, int startY, int endX, int endY, int unused, int color) {
      context.fill(startX, startY, endX, endY, color);
   }
}
