package com.corz.client.config;

import com.corz.client.config.ModuleTogglePolicy.ModuleView;
import com.corz.client.config.ConfigSharePolicy.Role;
import com.corz.client.config.ConfigSharePolicy.Scope;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.corz.client.utils.AutoRiggerUtils;
import com.corz.client.utils.MinmaxEnumType;

public final class ConfigImportPlan {
   public int acceptedSettingCount;
   public final List<String> blockedToggleModules;
   public final List<String> keptLocalSettings;
   public final List<String> notes;
   public boolean hasHudSection;
   public int acceptedBindKeyCount;
   public final List<String> skippedModules;
   public final List<String> notTurnedOnModules;
   public final Map<String, JsonObject> extra;
   public int keptLocalCount;
   public boolean hasThemeSection;
   public final List<String> modulesToDisable;
   public boolean safetyRulesMissing;
   public final List<String> modulesToEnable = new ArrayList<>();
   public static final List<String> SECTION_NAMES = List.of("modules", "hud", "theme");

   private static boolean isBooleanJson(JsonElement json) {
      return json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean();
   }

   private void planTrustedModule(String extraSettingsText, JsonObject json, ModuleView moduleView, ConfigImportOptions jsonExtraEnabled) {
      long extraSettingsValue = -3805137965865233724L;
      JsonObject jsonObject = new JsonObject();
      if (jsonExtraEnabled.includeEnabledState && isBooleanJson(json.get("enabled"))) {
         long extraSettingsValue2 = extraSettingsValue ^ ((long)((long) ((json.get("enabled").getAsBoolean()) ? 1 : 0)) << 32 ^ extraSettingsValue) & -4294967296L;
         if ((int)(extraSettingsValue2 >>> 32) != (moduleView.isEnabled() ? 1 : 0)) {
            if (ModuleTogglePolicy.canConfigSetEnabled(moduleView, (boolean)((((int)(extraSettingsValue2 >>> 32))) != 0))) {
               ((int)(extraSettingsValue2 >>> 32) != 0 ? this.modulesToEnable : this.modulesToDisable).add(extraSettingsText);
            } else {
               this.blockedToggleModules.add(extraSettingsText);
               this.notTurnedOnModules.add(extraSettingsText);
               this.notes.add("Not turned on: " + moduleView.getDisplayName() + " is never switched on by a config.");
            }
         }
      }

      if (jsonExtraEnabled.includeSettings && hasJsonObject(json, "settings")) {
         JsonObject jsonObject2 = new JsonObject();

         for (Entry entry : json.getAsJsonObject("settings").entrySet()) {
            if (moduleView.getSettingType((String)entry.getKey()) != null && !moduleView.isLocalOnlySetting((String)entry.getKey())) {
               jsonObject2.add((String)entry.getKey(), (JsonElement)entry.getValue());
            }
         }

         if (jsonObject2.size() > 0) {
            jsonObject.add("settings", jsonObject2);
         }

         this.acceptedSettingCount = this.acceptedSettingCount + jsonObject2.size();
      }

      if (jsonExtraEnabled.includeBindKeys && isIntegerJson(json.get("bindKey")) && ConfigValueSanitizer.isValidKeyCode(json.get("bindKey").getAsInt())) {
         jsonObject.addProperty("bindKey", json.get("bindKey").getAsInt());
         this.acceptedBindKeyCount++;
      }

      if (jsonExtraEnabled.includeSettings && json.has("extra") && !json.get("extra").isJsonNull() && moduleView.supportsExtraData()) {
         jsonObject.add("extra", json.get("extra"));
      }

      this.extra.put(extraSettingsText, jsonObject);
   }

   public static ConfigImportPlan planImport(JsonObject json, ConfigImportOptions jsonExtraEnabled, Collection<ModuleView> collection) {
      return getThemeModules(json, jsonExtraEnabled, collection, ConfigSharePolicy.getInstance());
   }

   public ConfigImportPlan() {
      this.modulesToDisable = new ArrayList<>();
      this.blockedToggleModules = new ArrayList<>();
      this.notTurnedOnModules = new ArrayList<>();
      this.keptLocalSettings = new ArrayList<>();
      this.skippedModules = new ArrayList<>();
      this.notes = new ArrayList<>();
      this.acceptedSettingCount = 0;
      this.acceptedBindKeyCount = 0;
      this.keptLocalCount = 0;
      this.hasHudSection = false;
      this.hasThemeSection = false;
      this.safetyRulesMissing = false;
      this.extra = new LinkedHashMap<>();
   }

   public static JsonObject sanitizeConfigContent(JsonObject json, Collection<ModuleView> collection, ConfigSharePolicy jsonReview) {
      long modulesJsonValue = 7908900982126409565L;
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("modules", new JsonObject());
      if (json != null && jsonReview != null && jsonReview.isPolicyLoaded()) {
         var map = indexModulesByKey(collection);

         for (String modulesJsonText : SECTION_NAMES) {
            if (hasJsonObject(json, modulesJsonText)) {
               modulesJsonValue ^= ((long)((long) (("modules".equals(modulesJsonText)) ? 1 : 0)) << 32 ^ modulesJsonValue) & -4294967296L;
               JsonObject jsonObject2 = new JsonObject();

               for (Entry entry : json.getAsJsonObject(modulesJsonText).entrySet()) {
                  if (!AutoRiggerUtils.isContains((String)entry.getKey()) && ((JsonElement)entry.getValue()).isJsonObject()) {
                     String lowerCase = AutoRiggerUtils.getLowerCase((String)entry.getKey());
                     if ((int)(modulesJsonValue >>> 32) != 0 ? !jsonReview.isInAnySection(lowerCase) : jsonReview.isInSection(modulesJsonText, lowerCase)) {
                        ModuleView moduleView = (ModuleView)map.get(lowerCase);
                        if (moduleView != null && !jsonObject2.has(lowerCase)) {
                           jsonObject2.add(lowerCase, sanitizeModuleJson(lowerCase, ((JsonElement)entry.getValue()).getAsJsonObject(), moduleView, jsonReview));
                        }
                     }
                  }
               }

               if ((int)(modulesJsonValue >>> 32) != 0 || jsonObject2.size() > 0) {
                  jsonObject.add(modulesJsonText, jsonObject2);
               }
            }
         }

         return jsonObject;
      } else {
         return jsonObject;
      }
   }

   private void planSection(String rulesAppliedText, JsonObject json, ConfigImportOptions jsonExtraEnabled, Map<String, ModuleView> map, ConfigSharePolicy jsonReview) {
      long rulesAppliedValue = -6779144580289146766L;
      long rulesAppliedValue2 = rulesAppliedValue ^ ((long)((long) (("modules".equals(rulesAppliedText)) ? 1 : 0)) << 32 ^ rulesAppliedValue) & -4294967296L;

      for (Entry entry : json.entrySet()) {
         String containsText = (String)entry.getKey();
         if (AutoRiggerUtils.isContains(containsText)) {
            this.skippedModules.add(containsText);
            this.notes.add("Skipped unsupported module: " + containsText);
         } else {
            String lowerCase = AutoRiggerUtils.getLowerCase(containsText);
            if ((int)(rulesAppliedValue2 >>> 32) != 0 ? !jsonReview.isInAnySection(lowerCase) : jsonReview.isInSection(rulesAppliedText, lowerCase)) {
               ModuleView moduleView = (ModuleView)map.get(lowerCase);
               if (moduleView == null) {
                  this.skippedModules.add(containsText);
                  this.notes.add("Skipped unknown module: " + containsText);
               } else if (((JsonElement)entry.getValue()).isJsonObject() && !this.extra.containsKey(lowerCase)) {
                  this.planModule(lowerCase, ((JsonElement)entry.getValue()).getAsJsonObject(), moduleView, jsonExtraEnabled, jsonReview);
               }
            } else {
               this.skippedModules.add(containsText);
               this.notes.add("Skipped " + containsText + " in the " + rulesAppliedText + " section.");
            }
         }
      }
   }

   public boolean hasNothingToApply() {
      return this.modulesToEnable.isEmpty()
         && this.modulesToDisable.isEmpty()
         && this.acceptedSettingCount == 0
         && this.acceptedBindKeyCount == 0
         && !this.hasHudSection
         && !this.hasThemeSection;
   }

   private static boolean isIntegerJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         double asDouble = json.getAsDouble();
         return asDouble == Math.floor(asDouble) && !Double.isInfinite(asDouble) && Math.abs(asDouble) <= 2.147483647E9;
      } else {
         return false;
      }
   }

   private static Map<String, ModuleView> indexModulesByKey(Collection<ModuleView> collection) {
      var linkedHashMap = new LinkedHashMap();
      if (collection != null) {
         for (ModuleView moduleView : collection) {
            if (moduleView != null) {
               linkedHashMap.putIfAbsent(moduleView.getModuleKey(), moduleView);
            }
         }
      }

      return linkedHashMap;
   }

   private static boolean hasJsonObject(JsonObject json, String jsonText) {
      return json != null && json.has(jsonText) && json.get(jsonText).isJsonObject();
   }

   private void planModule(String extraSettingsText, JsonObject json, ModuleView moduleView, ConfigImportOptions jsonExtraEnabled, ConfigSharePolicy jsonReview) {
      long extraSettingsValue = -3620463955811738099L;
      if (jsonExtraEnabled.isTrustedSource()) {
         this.planTrustedModule(extraSettingsText, json, moduleView, jsonExtraEnabled);
      } else {
         Role riskyPortable = jsonReview.getEffectiveRole(extraSettingsText, moduleView.isConfigPinned(), moduleView.isAutoPvpModule());
         JsonObject jsonObject = new JsonObject();
         if (jsonExtraEnabled.includeEnabledState && isBooleanJson(json.get("enabled"))) {
            extraSettingsValue ^= ((long)((long) ((json.get("enabled").getAsBoolean()) ? 1 : 0)) << 32 ^ extraSettingsValue) & -4294967296L;
            if ((int)(extraSettingsValue >>> 32) != (moduleView.isEnabled() ? 1 : 0)) {
               if (ModuleTogglePolicy.canFlag(jsonReview, moduleView, (boolean)((((int)(extraSettingsValue >>> 32))) != 0))) {
                  ((int)(extraSettingsValue >>> 32) != 0 ? this.modulesToEnable : this.modulesToDisable).add(extraSettingsText);
               } else {
                  this.blockedToggleModules.add(extraSettingsText);
                  if ((int)(extraSettingsValue >>> 32) != 0) {
                     this.notTurnedOnModules.add(extraSettingsText);
                     this.notes.add("Not turned on for safety: " + moduleView.getDisplayName());
                  } else {
                     this.keptLocalCount++;
                     this.keptLocalSettings.add(extraSettingsText + "/enabled");
                     this.notes.add("Left " + moduleView.getDisplayName() + " on for safety.");
                  }
               }
            }
         }

         if (jsonExtraEnabled.includeSettings && hasJsonObject(json, "settings")) {
            JsonObject jsonObject2 = new JsonObject();

            for (Entry entry : json.getAsJsonObject("settings").entrySet()) {
               String minmaxEnumText = (String)entry.getKey();
               MinmaxEnumType minmaxEnumType = moduleView.getSettingType(minmaxEnumText);
               if (minmaxEnumType != null && !moduleView.isLocalOnlySetting(minmaxEnumText)) {
                  JsonElement jsonElement = (JsonElement)entry.getValue();
                  Scope localPortable = jsonReview.classifyScope(extraSettingsText, minmaxEnumText, minmaxEnumType);
                  if (localPortable == Scope.LOCAL) {
                     if (!jsonElement.equals(moduleView.getSettingValue(minmaxEnumText))) {
                        this.keptLocalCount++;
                        this.keptLocalSettings.add(extraSettingsText + "/" + minmaxEnumText);
                     }
                  } else if (!jsonReview.isValueShareable(localPortable, jsonElement)) {
                     this.keptLocalCount++;
                     this.keptLocalSettings.add(extraSettingsText + "/" + minmaxEnumText);
                     this.notes.add("Skipped an unsafe value for " + moduleView.getDisplayName() + " / " + minmaxEnumText + ".");
                  } else {
                     jsonObject2.add(minmaxEnumText, jsonElement);
                  }
               }
            }

            if (jsonObject2.size() > 0) {
               jsonObject.add("settings", jsonObject2);
            }

            this.acceptedSettingCount = this.acceptedSettingCount + jsonObject2.size();
         }

         if (jsonExtraEnabled.includeBindKeys && isIntegerJson(json.get("bindKey"))) {
            long extraSettingsValue2 = extraSettingsValue ^ ((long)json.get("bindKey").getAsInt() << 32 ^ extraSettingsValue) & -4294967296L;
            if (jsonReview.canSharePortableKeybind(riskyPortable, (int)(extraSettingsValue2 >>> 32))) {
               jsonObject.addProperty("bindKey", (int)(extraSettingsValue2 >>> 32));
               this.acceptedBindKeyCount++;
            } else if ((int)(extraSettingsValue2 >>> 32) != moduleView.getBindKey()) {
               this.keptLocalCount++;
               this.keptLocalSettings.add(extraSettingsText + "/bindKey");
               if (jsonReview.isPortableRole(riskyPortable) && ConfigValueSanitizer.isMouseButtonCode((int)(extraSettingsValue2 >>> 32))) {
                  this.notes.add("Kept your own key for " + moduleView.getDisplayName() + ": a config can't bind it to a mouse button.");
               }
            }
         }

         if (jsonExtraEnabled.includeSettings && json.has("extra") && !json.get("extra").isJsonNull()) {
            JsonElement jsonElement2 = json.get("extra");
            if (jsonReview.isExtraAllowed(extraSettingsText) && moduleView.supportsExtraData() && ConfigSharePolicy.detectRiskyJson(jsonElement2, 0) == null) {
               jsonObject.add("extra", jsonElement2);
            } else {
               this.keptLocalCount++;
               this.keptLocalSettings.add(extraSettingsText + "/extra");
            }
         }

         this.extra.put(extraSettingsText, jsonObject);
      }
   }

   private static JsonObject sanitizeModuleJson(String extraBindText, JsonObject json, ModuleView moduleView, ConfigSharePolicy jsonReview) {
      long extraBindValue = -2826934968387238434L;
      Role riskyPortable = jsonReview.getEffectiveRole(extraBindText, moduleView.isConfigPinned(), moduleView.isAutoPvpModule());
      JsonObject jsonObject = new JsonObject();
      if (isBooleanJson(json.get("enabled"))) {
         long extraBindValue2 = extraBindValue ^ ((long)((long) ((json.get("enabled").getAsBoolean()) ? 1 : 0)) << 32 ^ extraBindValue) & -4294967296L;
         if (jsonReview.isRoleAllowed(riskyPortable, (boolean)((((int)(extraBindValue2 >>> 32))) != 0))) {
            jsonObject.addProperty("enabled", Boolean.valueOf((boolean)((((int)(extraBindValue2 >>> 32))) != 0)));
         }
      }

      if (isIntegerJson(json.get("bindKey")) && jsonReview.canSharePortableKeybind(riskyPortable, json.get("bindKey").getAsInt())) {
         jsonObject.addProperty("bindKey", json.get("bindKey").getAsInt());
      }

      JsonObject jsonObject2 = new JsonObject();
      if (hasJsonObject(json, "settings")) {
         for (Entry entry : json.getAsJsonObject("settings").entrySet()) {
            MinmaxEnumType minmaxEnumType = moduleView.getSettingType((String)entry.getKey());
            if (minmaxEnumType != null && !moduleView.isLocalOnlySetting((String)entry.getKey())) {
               Scope localPortable = jsonReview.classifyScope(extraBindText, (String)entry.getKey(), minmaxEnumType);
               if (localPortable != Scope.LOCAL && jsonReview.isValueShareable(localPortable, (JsonElement)entry.getValue())) {
                  jsonObject2.add((String)entry.getKey(), (JsonElement)entry.getValue());
               }
            }
         }
      }

      jsonObject.add("settings", jsonObject2);
      if (json.has("extra")
         && !json.get("extra").isJsonNull()
         && jsonReview.isExtraAllowed(extraBindText)
         && moduleView.supportsExtraData()
         && ConfigSharePolicy.detectRiskyJson(json.get("extra"), 0) == null) {
         jsonObject.add("extra", json.get("extra"));
      }

      return jsonObject;
   }

   public static ConfigImportPlan getThemeModules(JsonObject json, ConfigImportOptions jsonExtraEnabled, Collection<ModuleView> collection, ConfigSharePolicy jsonReview) {
      ConfigImportPlan jsonExtraHandle = new ConfigImportPlan();
      if (json == null || jsonExtraEnabled == null) {
         return jsonExtraHandle;
      } else if (jsonReview != null && jsonReview.isPolicyLoaded()) {
         var map = indexModulesByKey(collection);
         if (jsonExtraEnabled.includeModules && hasJsonObject(json, "modules")) {
            jsonExtraHandle.planSection("modules", json.getAsJsonObject("modules"), jsonExtraEnabled, map, jsonReview);
         }

         if (jsonExtraEnabled.includeHud && hasJsonObject(json, "hud")) {
            jsonExtraHandle.hasHudSection = true;
            jsonExtraHandle.planSection("hud", json.getAsJsonObject("hud"), jsonExtraEnabled, map, jsonReview);
         }

         if (jsonExtraEnabled.includeTheme && hasJsonObject(json, "theme")) {
            jsonExtraHandle.hasThemeSection = true;
            jsonExtraHandle.planSection("theme", json.getAsJsonObject("theme"), jsonExtraEnabled, map, jsonReview);
         }

         return jsonExtraHandle;
      } else {
         jsonExtraHandle.safetyRulesMissing = true;
         jsonExtraHandle.notes.add("Config safety rules are missing, so nothing was applied.");
         return jsonExtraHandle;
      }
   }
}
