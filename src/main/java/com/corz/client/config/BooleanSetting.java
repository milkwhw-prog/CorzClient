package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class BooleanSetting extends ConfigSetting {
   private final boolean defaultEnabled;
   private boolean enabled;

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.BOOLEAN;
   }

   public void toggle() {
      this.enabled = !this.enabled;
      ModuleConfigAutosave.updateFlag2();
   }

   public boolean isFlag3() {
      return this.defaultEnabled;
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.enabled);
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            this.handle(json.getAsBoolean());
            return ConfigSetting.Applied.APPLIED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void handle(boolean booleanValue) {
      if (this.enabled != booleanValue) {
         this.enabled = booleanValue;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public BooleanSetting(String booleanText, String booleanText2, boolean flag) {
      super(booleanText, booleanText2);
      this.defaultEnabled = flag;
      this.enabled = flag;
   }
}
