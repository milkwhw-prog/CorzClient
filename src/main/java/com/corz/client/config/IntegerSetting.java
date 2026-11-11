package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class IntegerSetting extends ConfigSetting {
   private int textShowThreshold = Integer.MIN_VALUE;
   private final int minValue;
   private int currentValue;
   private String hintText = null;
   private final int defaultValue;
   private final int maxLimit;
   private final int stepSize;

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.INTEGER;
   }

   public IntegerSetting(String integerText, String description, int integerValue, int aggroPreemptBound, int intersectsCenterBound) {
      this(integerText, description, integerValue, aggroPreemptBound, intersectsCenterBound, 1);
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.currentValue);
   }

   public int getValue3() {
      return this.defaultValue;
   }

   public void stepUp() {
      this.handleMax(this.currentValue + this.stepSize);
   }

   public int getMinValue() {
      return this.minValue;
   }

   public IntegerSetting withHint(int value, String text2) {
      this.textShowThreshold = value;
      this.hintText = text2;
      return this;
   }

   public void stepDown() {
      this.handleMax(this.currentValue - this.stepSize);
   }

   public int getMaxValue() {
      return this.maxLimit;
   }

   public int getStepSize() {
      return this.stepSize;
   }

   public IntegerSetting(String integerText, String description, int value, int aggroPreemptBound, int intersectsCenterBound, int value4) {
      super(integerText, description);
      this.minValue = aggroPreemptBound;
      this.maxLimit = intersectsCenterBound;
      this.stepSize = value4;
      this.defaultValue = value;
      this.currentValue = Math.max(aggroPreemptBound, Math.min(intersectsCenterBound, value));
   }

   public String getHintLabel() {
      return this.hintText != null && this.currentValue < this.textShowThreshold ? this.hintText : null;
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            int roundAsDouble = (int)Math.round(json.getAsDouble());
            this.handleMax(roundAsDouble);
            return this.currentValue == roundAsDouble ? ConfigSetting.Applied.APPLIED : ConfigSetting.Applied.CLAMPED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public int getValue() {
      return this.currentValue;
   }

   public void handleMax(int integerBound) {
      int max = Math.max(this.minValue, Math.min(this.maxLimit, integerBound));
      if (this.currentValue != max) {
         this.currentValue = max;
         ModuleConfigAutosave.updateFlag2();
      }
   }
}
