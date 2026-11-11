package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class DoubleSetting extends ConfigSetting {
   private final double maxValue;
   private final double stepSize;
   private double currentValue;
   private final double defaultValue;
   private final double minValue;

   public void stepDown() {
      this.handleMaxAmount(this.currentValue - this.stepSize);
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.currentValue);
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            double asDouble = json.getAsDouble();
            if (!Double.isNaN(asDouble) && !Double.isInfinite(asDouble)) {
               this.handleMaxAmount(asDouble);
               return this.currentValue == asDouble ? ConfigSetting.Applied.APPLIED : ConfigSetting.Applied.CLAMPED;
            } else {
               return ConfigSetting.Applied.REJECTED;
            }
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.DOUBLE;
   }

   public double getValue() {
      return this.currentValue;
   }

   public void handleMaxAmount(double doubleBound) {
      double max = Math.max(this.minValue, Math.min(this.maxValue, doubleBound));
      if (this.currentValue != max) {
         this.currentValue = max;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public DoubleSetting(String doubleText, String description, double doubleValue, double doubleValue2, double doubleValue3) {
      this(doubleText, description, doubleValue, doubleValue2, doubleValue3, 0.1);
   }

   public double getStepSize() {
      return this.stepSize;
   }

   public double getAmount3() {
      return this.defaultValue;
   }

   public void stepUp() {
      this.handleMaxAmount(this.currentValue + this.stepSize);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public DoubleSetting(String doubleText, String description, double amount, double amount2, double amount3, double amount4) {
      super(doubleText, description);
      this.minValue = amount2;
      this.maxValue = amount3;
      this.stepSize = amount4;
      this.defaultValue = amount;
      this.currentValue = amount;
   }
}
