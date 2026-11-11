package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;
import com.corz.client.utils.MinmaxEnumType;

public class EnumSetting extends ConfigSetting {
   private final List<String> options;
   private int currentIndex;
   private final int defaultIndex;

   public String getDefaultOption() {
      return this.options.get(this.defaultIndex);
   }

   public List<String> getOptions() {
      return this.options;
   }

   public void selectNext() {
      this.currentIndex = (this.currentIndex + 1) % this.options.size();
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.ENUM;
   }

   public int getValue() {
      return this.defaultIndex;
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            String asString = json.getAsString();
            if (!this.options.contains(asString)) {
               return ConfigSetting.Applied.REJECTED;
            } else {
               this.handle(asString);
               return ConfigSetting.Applied.APPLIED;
            }
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public void handle(String enumText) {
      int indexOf = this.options.indexOf(enumText);
      if (indexOf != -1) {
         this.currentIndex = indexOf;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public void handle(int indexOf) {
      if (indexOf >= 0 && indexOf < this.options.size()) {
         this.currentIndex = indexOf;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public void selectPrevious() {
      this.currentIndex = (this.currentIndex - 1 + this.options.size()) % this.options.size();
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.getCurrentOption());
   }

   public int getCurrentIndex() {
      return this.currentIndex;
   }

   public String getCurrentOption() {
      return this.options.get(this.currentIndex);
   }

   public EnumSetting(String enumText, String description, String enumText3, String... texts) {
      super(enumText, description);
      this.options = Arrays.asList(texts);
      this.currentIndex = this.options.indexOf(enumText3);
      if (this.currentIndex == -1) {
         this.currentIndex = 0;
      }

      this.defaultIndex = this.currentIndex;
   }
}
