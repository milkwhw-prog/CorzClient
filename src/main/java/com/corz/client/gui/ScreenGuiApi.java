package com.corz.client.gui;

public interface ScreenGuiApi {

   static int getValue(int screenGuiValue, int screenGuiValue2) {
      int screenGuiDivisor = (int)Math.round(Math.min(screenGuiValue / 960.0, screenGuiValue2 / 540.0));

      while (screenGuiDivisor > 2 && (screenGuiValue / screenGuiDivisor < 680 || screenGuiValue2 / screenGuiDivisor < 384)) {
         screenGuiDivisor--;
      }

      return Math.max(2, screenGuiDivisor);
   }
}
