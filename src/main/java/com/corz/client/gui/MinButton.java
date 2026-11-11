package com.corz.client.gui;

import java.util.HashMap;
import java.util.Map;
import com.corz.client.utils.AmountPreviewUtils;

public final class MinButton {
   private final Map<String, float[]> buttonFloatArrayMap = new HashMap<>();
   private final Map<String, long[]> buttonLongArrayMap = new HashMap<>();
   private float frameDeltaSeconds;

   public float getMin() {
      return this.frameDeltaSeconds;
   }

   public void handleMin(float buttonBound) {
      this.frameDeltaSeconds = buttonBound < 0.0F ? 0.0F : Math.min(0.05F, buttonBound);
   }

   public void update() {
      this.buttonFloatArrayMap.clear();
      this.buttonLongArrayMap.clear();
   }

   public void handle(String buttonText, float buttonValue) {
      float[] buttonValues = this.buttonFloatArrayMap.get(buttonText);
      if (buttonValues == null) {
         this.buttonFloatArrayMap.put(buttonText, new float[]{buttonValue});
      } else {
         buttonValues[0] = buttonValue;
      }
   }

   public float getMin(String buttonText, float buttonValue, float thumbsLimit, float buttonValue3) {
      float[] buttonValues = this.buttonFloatArrayMap.get(buttonText);
      if (buttonValues == null) {
         buttonValues = new float[]{buttonValue};
         this.buttonFloatArrayMap.put(buttonText, buttonValues);
      }

      buttonValues[0] = AmountPreviewUtils.getAmount(buttonValues[0], thumbsLimit, buttonValue3, this.frameDeltaSeconds, 0.002F);
      return buttonValues[0];
   }

   public float getAmount(String buttonText) {
      float[] buttonValues = this.buttonFloatArrayMap.get(buttonText);
      return buttonValues == null ? 0.0F : buttonValues[0];
   }

   public void handle(String buttonText) {
      long[] buttonValues = this.buttonLongArrayMap.get(buttonText);
      if (buttonValues == null) {
         this.buttonLongArrayMap.put(buttonText, new long[]{System.nanoTime()});
      } else {
         buttonValues[0] = System.nanoTime();
      }
   }

   public float getAmount(String buttonText, float buttonFactor) {
      long[] buttonValues = this.buttonLongArrayMap.get(buttonText);
      if (buttonValues != null && buttonValues[0] != 0L) {
         float buttonLimit = (float)(System.nanoTime() - buttonValues[0]) / 1000000.0F / 180.0F;
         if (buttonLimit >= 1.0F) {
            buttonValues[0] = 0L;
            return 1.0F;
         } else {
            return 1.0F - buttonFactor * AmountPreviewUtils.getAmount(buttonLimit);
         }
      } else {
         return 1.0F;
      }
   }

   public float getAmount(String buttonText, boolean buttonFlag) {
      return this.getMin(buttonText, 0.0F, buttonFlag ? 1.0F : 0.0F, buttonFlag ? 16.0F : 11.0F);
   }

   public float getAmount(String buttonText, float buttonValue, float buttonValue2) {
      return this.getMin(buttonText, buttonValue, buttonValue, buttonValue2);
   }
}
