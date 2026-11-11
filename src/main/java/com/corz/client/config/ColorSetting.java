package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class ColorSetting extends ConfigSetting {
   private int argbColor;
   private final int defaultColor;

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.argbColor);
   }

   public void setColor(int intValue) {
      this.argbColor = intValue;
      ModuleConfigAutosave.updateFlag2();
   }

   public void update() {
      this.argbColor = this.defaultColor;
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            this.setColor(json.getAsInt());
            return ConfigSetting.Applied.APPLIED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public void handle(String colorText) {
      try {
         String colorText2 = colorText.startsWith("#") ? colorText.substring(1) : colorText;
         long long2 = Long.parseLong(colorText2, 16);
         if (colorText2.length() == 6) {
            long2 |= 4278190080L;
         }

         this.argbColor = (int)long2;
         ModuleConfigAutosave.updateFlag2();
      } catch (NumberFormatException e) {
      }
   }

   public int getDefaultColor() {
      return this.defaultColor;
   }

   public int getColor() {
      return this.argbColor;
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.COLOR;
   }

   public ColorSetting(String colorText, String colorText2, int intValue) {
      super(colorText, colorText2);
      this.argbColor = intValue;
      this.defaultColor = intValue;
   }
}
