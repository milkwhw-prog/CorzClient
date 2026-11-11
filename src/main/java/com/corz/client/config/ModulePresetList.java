package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import com.corz.client.utils.DisabledMaskData;

public final class ModulePresetList {
   private String presets;
   private final List<ModulePresetList.Preset> presetList = new ArrayList<>();
   private boolean autoApply;

   public synchronized String getSummaryString() {
      return String.format(Locale.ROOT, "presets=%d selected=%s autoApply=%s", this.presetList.size(), this.presets, this.autoApply);
   }

   public synchronized int getPresetCount() {
      return this.presetList.size();
   }

   public synchronized boolean renamePreset(String presetsSelectedText, String replaceText) {
      String presets = sanitizePresetName(replaceText);
      if (presets.isEmpty()) {
         return false;
      } else {
         ModulePresetList.Preset next = this.findPresetByName(presetsSelectedText);
         if (next == null) {
            return false;
         } else if (!presets.equalsIgnoreCase(presetsSelectedText) && this.findPresetByName(presets) != null) {
            return false;
         } else {
            this.presetList.set(this.presetList.indexOf(next), new ModulePresetList.Preset(presets, next.mask()));
            if (presetsSelectedText.equalsIgnoreCase(this.presets)) {
               this.presets = presets;
            }

            return true;
         }
      }
   }

   public synchronized boolean selectPreset(String presetsText) {
      if (presetsText == null) {
         this.presets = null;
         return true;
      } else {
         ModulePresetList.Preset next = this.findPresetByName(presetsText);
         if (next == null) {
            return false;
         } else {
            this.presets = next.name();
            return true;
         }
      }
   }

   public synchronized List<ModulePresetList.Preset> getPresetList() {
      return Collections.unmodifiableList(new ArrayList<>(this.presetList));
   }

   public synchronized boolean savePreset(String presetsSelectedText, int presetsSelectedValue) {
      String presets = sanitizePresetName(presetsSelectedText);
      if (presets.isEmpty()) {
         return false;
      } else {
         int presetsSelectedValue2 = presetsSelectedValue & 511;

         for (int i = 0; i < this.presetList.size(); i++) {
            if (this.presetList.get(i).name().equalsIgnoreCase(presets)) {
               this.presetList.set(i, new ModulePresetList.Preset(presets, presetsSelectedValue2));
               return true;
            }
         }

         if (this.presetList.size() >= 64) {
            return false;
         } else {
            this.presetList.add(new ModulePresetList.Preset(presets, presetsSelectedValue2));
            if (this.presets == null) {
               this.presets = presets;
            }

            return true;
         }
      }
   }

   public synchronized ModulePresetList.Preset getSelectedPreset() {
      return this.findPresetByName(this.presets);
   }

   public static String sanitizePresetName(String replaceText) {
      if (replaceText == null) {
         return "";
      } else {
         String replaceAll = replaceText.strip().replaceAll("[\\r\\n\\t]", " ");
         return replaceAll.length() > 24 ? replaceAll.substring(0, 24) : replaceAll;
      }
   }

   public synchronized ModulePresetList.Preset findPresetByName(String presetsJsonText) {
      if (presetsJsonText == null) {
         return null;
      } else {
         for (ModulePresetList.Preset next : this.presetList) {
            if (next.name().equalsIgnoreCase(presetsJsonText)) {
               return next;
            }
         }

         return null;
      }
   }

   public synchronized void loadFromJson(JsonElement json) {
      this.presetList.clear();
      this.presets = null;
      this.autoApply = false;
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         if (asJsonObject.has("presets") && asJsonObject.get("presets").isJsonArray()) {
            for (JsonElement jsonElement : asJsonObject.getAsJsonArray("presets")) {
               if (jsonElement.isJsonObject()) {
                  JsonObject asJsonObject2 = jsonElement.getAsJsonObject();
                  String presetsSelectedText = asJsonObject2.has("name") ? sanitizePresetName(asJsonObject2.get("name").getAsString()) : "";
                  int presetsSelectedValue = asJsonObject2.has("mask") ? asJsonObject2.get("mask").getAsInt() & 511 : 0;
                  if (!presetsSelectedText.isEmpty() && this.findPresetByName(presetsSelectedText) == null && this.presetList.size() < 64) {
                     this.presetList.add(new ModulePresetList.Preset(presetsSelectedText, presetsSelectedValue));
                  }
               }
            }
         }

         if (asJsonObject.has("selected") && this.findPresetByName(asJsonObject.get("selected").getAsString()) != null) {
            this.presets = this.findPresetByName(asJsonObject.get("selected").getAsString()).name();
         }

         if (asJsonObject.has("autoApply")) {
            this.autoApply = asJsonObject.get("autoApply").getAsBoolean();
         }
      }
   }

   public synchronized Integer getAutoApplyMask() {
      if (!this.autoApply) {
         return null;
      } else {
         ModulePresetList.Preset presets = this.getSelectedPreset();
         return presets == null ? null : presets.mask();
      }
   }

   public synchronized boolean isAutoApply() {
      return this.autoApply;
   }

   public synchronized String getSelectedPresetName() {
      return this.presets;
   }

   public synchronized JsonElement toJson() {
      JsonObject jsonObject = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (ModulePresetList.Preset preset : this.presetList) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("name", preset.name());
         jsonObject2.addProperty("mask", preset.mask());
         jsonArray.add(jsonObject2);
      }

      jsonObject.add("presets", jsonArray);
      if (this.presets != null) {
         jsonObject.addProperty("selected", this.presets);
      }

      jsonObject.addProperty("autoApply", this.autoApply);
      return jsonObject;
   }

   public synchronized boolean deletePreset(String presetsSelectedText) {
      ModulePresetList.Preset next = this.findPresetByName(presetsSelectedText);
      if (next == null) {
         return false;
      } else {
         this.presetList.remove(next);
         if (next.name().equalsIgnoreCase(this.presets)) {
            this.presets = this.presetList.isEmpty() ? null : this.presetList.get(0).name();
         }

         return true;
      }
   }

   public synchronized boolean movePreset(String presetsSelectedText, int presetsSelectedValue) {
      ModulePresetList.Preset next = this.findPresetByName(presetsSelectedText);
      if (next == null) {
         return false;
      } else {
         int indexOf = this.presetList.indexOf(next);
         int presetsSelectedLimit = indexOf + Integer.signum(presetsSelectedValue);
         if (presetsSelectedLimit >= 0 && presetsSelectedLimit < this.presetList.size()) {
            Collections.swap(this.presetList, indexOf, presetsSelectedLimit);
            return true;
         } else {
            return false;
         }
      }
   }

   public synchronized void setAutoApply(boolean autoApply) {
      this.autoApply = autoApply;
   }

   public record Preset(String name, int mask) {
      public String formatNameAndMask() {
         return this.name + " " + DisabledMaskData.formatText(this.mask);
      }
   }
}
