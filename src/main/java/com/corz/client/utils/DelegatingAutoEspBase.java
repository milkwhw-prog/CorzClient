package com.corz.client.utils;

import com.corz.client.config.ConfigSetting;
import com.google.gson.JsonElement;

public final class DelegatingAutoEspBase extends ConfigSetting {
   private final ConfigSetting delegate;

   @Override
   public MinmaxEnumType getSettingType() {
      return this.delegate.getSettingType();
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      return json != null && !json.isJsonNull() ? this.delegate.applyJsonValue(json) : ConfigSetting.Applied.NOT_HANDLED;
   }

   public DelegatingAutoEspBase(String jsonElementText, ConfigSetting base) {
      super(jsonElementText, base.getDescription());
      this.delegate = base;
      this.hideFromList();
   }

   public ConfigSetting getDelegate() {
      return this.delegate;
   }

   @Override
   public JsonElement getJsonValue() {
      return null;
   }
}
