package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class StringBase extends ConfigSetting {
   private final String defaultText;
   private String currentText;

   public String getCurrentText() {
      return this.currentText;
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.currentText);
   }

   public String getText() {
      return this.defaultText;
   }

   public StringBase(String settingName, String settingDescription, String text) {
      super(settingName, settingDescription);
      this.defaultText = text;
      this.currentText = text;
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            String asString = json.getAsString();
            boolean handledPrimitiveFlag = asString.length() > 2048;
            this.handleText(handledPrimitiveFlag ? asString.substring(0, 2048) : asString);
            return handledPrimitiveFlag ? ConfigSetting.Applied.CLAMPED : ConfigSetting.Applied.APPLIED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.STRING;
   }

   public void handleText(String newText) {
      this.currentText = newText != null ? newText : "";
   }
}
