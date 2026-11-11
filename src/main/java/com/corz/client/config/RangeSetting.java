package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.corz.client.utils.MinmaxEnumType;

public class RangeSetting extends ConfigSetting {
   private final double rangeMin;
   private final double defaultMin;
   private final double defaultMax;
   private final double step;
   private final double rangeMax;
   private double max;
   private double currentMin;

   public double getMin() {
      return this.currentMin;
   }

   public double getStep() {
      return this.step;
   }

   public double getRangeMin() {
      return this.rangeMin;
   }

   public double getAmount3() {
      return this.defaultMax;
   }

   public double getAmount2() {
      return this.defaultMin;
   }

   @Override
   public JsonElement getJsonValue() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("min", this.currentMin);
      jsonObject.addProperty("max", this.max);
      return jsonObject;
   }

   public void handleMaxAmount(double minmaxBound) {
      this.max = Math.max(this.currentMin, Math.min(this.rangeMax, minmaxBound));
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         try {
            JsonObject asJsonObject = json.getAsJsonObject();
            double handledPrimitiveLimit = asJsonObject.has("min") && asJsonObject.get("min").isJsonPrimitive() ? asJsonObject.get("min").getAsDouble() : this.currentMin;
            double minmaxBound = asJsonObject.has("max") && asJsonObject.get("max").isJsonPrimitive() ? asJsonObject.get("max").getAsDouble() : this.max;
            if (!Double.isNaN(handledPrimitiveLimit) && !Double.isNaN(minmaxBound)) {
               if (handledPrimitiveLimit > minmaxBound) {
                  double handledPrimitiveValue = handledPrimitiveLimit;
                  handledPrimitiveLimit = minmaxBound;
                  minmaxBound = handledPrimitiveValue;
               }

               boolean handledPrimitiveFlag = handledPrimitiveLimit < this.rangeMin || minmaxBound > this.rangeMax;
               this.handleMaxAmount2(handledPrimitiveLimit);
               this.handleMaxAmount(minmaxBound);
               this.handleMaxAmount2(handledPrimitiveLimit);
               return handledPrimitiveFlag ? ConfigSetting.Applied.CLAMPED : ConfigSetting.Applied.APPLIED;
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

   public double getMax() {
      return this.max;
   }

   public RangeSetting(String minmaxText, String description, double minmaxBound, double goalBound, double amount, double hypot, double amount3) {
      super(minmaxText, description);
      this.rangeMin = amount;
      this.rangeMax = hypot;
      this.step = amount3;
      this.currentMin = Math.max(amount, Math.min(hypot, minmaxBound));
      this.max = Math.max(amount, Math.min(hypot, goalBound));
      if (this.currentMin > this.max) {
         double max = this.currentMin;
         this.currentMin = this.max;
         this.max = max;
      }

      this.defaultMin = this.currentMin;
      this.defaultMax = this.max;
   }

   public double getRangeMax() {
      return this.rangeMax;
   }

   public void handleMaxAmount2(double minmaxBound) {
      this.currentMin = Math.max(this.rangeMin, Math.min(this.max, minmaxBound));
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.MINMAX;
   }
}
