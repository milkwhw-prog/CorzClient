package com.corz.client.config;

import com.google.gson.JsonElement;
import com.corz.client.utils.MinmaxEnumType;

public abstract class ConfigSetting {
   private boolean excludedFromSave;
   private boolean hiddenFromList;
   private final String settingName;
   private final String description;
   private String groupName = null;

   public String getSettingName() {
      return this.settingName;
   }

   public JsonElement getJsonValue() {
      return null;
   }

   public abstract MinmaxEnumType getSettingType();

   public String getGroupName() {
      return this.groupName;
   }

   public ConfigSetting hideFromList() {
      this.hiddenFromList = true;
      return this;
   }

   public String getDescription() {
      return this.description;
   }

   public ConfigSetting withGroupName(String text) {
      this.groupName = text;
      return this;
   }

   public boolean isHiddenFromList() {
      return this.hiddenFromList;
   }

   public ConfigSetting(String text, String text2) {
      this.hiddenFromList = false;
      this.excludedFromSave = false;
      this.settingName = text;
      this.description = text2;
   }

   public boolean isExcludedFromSave() {
      return this.excludedFromSave;
   }

   public ConfigSetting excludeFromSaveAndHide() {
      this.hiddenFromList = true;
      this.excludedFromSave = true;
      return this;
   }

   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      return ConfigSetting.Applied.NOT_HANDLED;
   }

   public static enum Applied {
      NOT_HANDLED,
      APPLIED,
      CLAMPED,
      REJECTED;
   }
}
