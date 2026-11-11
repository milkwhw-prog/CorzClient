package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.corz.client.utils.MinmaxEnumType;

public class KeybindSetting extends ConfigSetting {
   private final int defaultKeyCode;
   private boolean globalHotkey;
   private int keyCode;

   public boolean isBound() {
      return this.keyCode >= 0;
   }

   public KeybindSetting(String keybindText, String description) {
      this(keybindText, description, -1);
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            int asInt = json.getAsInt();
            int handledPrimitiveValue = ConfigValueSanitizer.sanitizeKeyCode(asInt);
            this.setValue2(handledPrimitiveValue);
            return handledPrimitiveValue == asInt ? ConfigSetting.Applied.APPLIED : ConfigSetting.Applied.CLAMPED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public KeybindSetting markGlobalHotkey() {
      this.globalHotkey = true;
      return this;
   }

   public void setValue2(int value) {
      this.keyCode = value;
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.keyCode);
   }

   public boolean isBoundTo(int keybindValue) {
      return this.isBound() && this.keyCode == keybindValue;
   }

   public boolean isGlobalHotkey() {
      return this.globalHotkey;
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.KEYBIND;
   }

   public int getValue() {
      return this.defaultKeyCode;
   }

   public KeybindSetting(String keybindText, String description, int value) {
      super(keybindText, description);
      this.keyCode = value;
      this.defaultKeyCode = value;
   }
}
