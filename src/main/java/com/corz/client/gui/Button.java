package com.corz.client.gui;

public class Button {
   private boolean buttonActive;
   private final int button;
   private final int buttonIndex;

   public int getButton() {
      return this.button;
   }

   public void updateFlag() {
      this.buttonActive = true;
   }

   public int getValue() {
      return this.buttonIndex;
   }

   public boolean isFlag() {
      return this.buttonActive;
   }

   public Button(int buttonCode, int buttonIndexValue) {
      this.button = buttonCode;
      this.buttonIndex = buttonIndexValue;
   }
}
