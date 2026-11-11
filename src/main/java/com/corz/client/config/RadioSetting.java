package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.List;
import com.corz.client.utils.MinmaxEnumType;

public class RadioSetting extends ConfigSetting {
   private String selectedStationKey = "";

   public void setSelectedStationKey(String radioText) {
      String string = radioText == null ? "" : radioText;
      if (!string.equals(this.selectedStationKey)) {
         this.selectedStationKey = string;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public List<RadioStationRegistry.Station> getStations() {
      return RadioStationRegistry.getStations();
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.selectedStationKey);
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.RADIO;
   }

   public RadioSetting(String radioText, String description) {
      super(radioText, description);
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            String string = json.getAsString();
            if (string != null && string.length() <= 640) {
               this.selectedStationKey = string;
               return ConfigSetting.Applied.APPLIED;
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

   public boolean isSelected(RadioStationRegistry.Station station) {
      return station != null && station.hasUrl() && station.key().equals(this.selectedStationKey);
   }

   public String getSelectedStationKey() {
      return this.selectedStationKey;
   }
}
