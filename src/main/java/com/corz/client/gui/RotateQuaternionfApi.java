package com.corz.client.gui;

import net.minecraft.client.gui.DrawContext;

public interface RotateQuaternionfApi {
   boolean canQuaternionfRotate(double mouseX, double mouseY, int boxX, int boxY, int boxWidth, int boxHeight, int mouseButton);

   void updateQuaternionfRotate();

   void renderQuaternionfRotate(DrawContext context, int x, int y, int width, int height);

   void handleQuaternionfRotate(double mouseX, double mouseY);
}
