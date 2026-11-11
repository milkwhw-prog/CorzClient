package com.corz.client.categories.settings;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PngIcons;
public class GUISettings extends Module {
   private static final String SIDEBAR_COLLAPSED = "sidebarCollapsed";
   private static GUISettings singleton;
   private final BooleanSetting booleanBase = new BooleanSetting("3D GUI", "Open the menu as a floating panel in the world", false);

   public static boolean isInstanceBase() {
      return singleton != null && singleton.booleanBase.isEnabled();
   }

   @Override
   public void onDisable() {
      this.toggle();
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonElement asjsonobject = json.getAsJsonObject().get("sidebarCollapsed");
         if (asjsonobject != null && asjsonobject.isJsonPrimitive() && asjsonobject.getAsJsonPrimitive().isBoolean()) {
            PngIcons.getInstance().handle(asjsonobject.getAsBoolean());
         }
      }
   }

   public GUISettings() {
      super("GUI Settings", IconsPngType.SETTING);
      this.addSettings(new ConfigSetting[]{this.booleanBase});
      this.setBindKey(344);
      this.setEnabled(true);
      singleton = this;
   }

   @Override
   public void setBindKey(int ilIiValue) {
      super.setBindKey(ilIiValue < 0 ? 344 : ilIiValue);
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("sidebarCollapsed", PngIcons.getInstance().isBooleanValue());
      return jsonObject;
   }
}
