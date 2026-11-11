package com.corz.client.config;

import com.corz.client.config.ModuleTogglePolicy.ModuleView;
import com.corz.client.config.ConfigSharePolicy.Role;
import com.corz.client.config.ConfigSharePolicy.Scope;
import com.corz.client.config.PresetFileStore.Preset;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.categories.visuals.ShowHUD;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.AutoRiggerUtils;
import com.corz.client.utils.AutoYourUtils;
import com.corz.client.utils.MinmaxEnumType;
import com.corz.client.utils.TokenVersionUtils;

public final class ModuleConfigSerializer {
   private static String getModuleKey(Module Module) {
      return AutoRiggerUtils.formatText(Module.getName());
   }

   private static String getClientVersion() {
      try {
         return LicenseRuntime.getClientVersion();
      } catch (Throwable e) {
         return "2.0.1";
      }
   }

   public static boolean updateSavedConfig(String jsonExtraText)  throws java.io.IOException {
      return canVersionJson(jsonExtraText, ModuleRegistry.getModules());
   }

   static boolean canVersionJson(String versionJsonText, List<Module> list)  throws java.io.IOException {
      Preset loadPreset = PresetFileStore.loadPreset(versionJsonText);
      if (loadPreset == null) {
         return false;
      } else {
         ConfigPresetFile versionAuthor = getVersionAuthor(loadPreset.jsonVersion.name, loadPreset.jsonVersion.description, loadPreset.jsonVersion.author, list);
         if (loadPreset.ownSealValid && versionAuthor.content.equals(loadPreset.jsonVersion.content)) {
            return true;
         } else {
            versionAuthor.createdAt = loadPreset.jsonVersion.createdAt;
            PresetFileStore.rewritePresetKeepingSource(versionJsonText, versionAuthor);
            return true;
         }
      }
   }

   public static ModuleConfigSerializer.Result applyConfig(ConfigPresetFile jsonVersion, ConfigImportOptions jsonExtraEnabled) {
      return getSettingsExtra(jsonVersion, jsonExtraEnabled, ModuleRegistry.getModules(), ModuleConfigAutosave::updateFlag);
   }

   public static ConfigPresetFile exportConfig(String jsonVersionText, String jsonVersionText2, String jsonVersionText3) {
      return getVersionAuthor(jsonVersionText, jsonVersionText2, jsonVersionText3, ModuleRegistry.getModules());
   }

   private static void addModuleJson(JsonObject json, Module Module, JsonObject jsonObject) {
      if (Module instanceof ShowHUD) {
         getOrCreateSection(json, "hud").add("show_hud", jsonObject);
      } else if (Module instanceof ThemeCustomizer) {
         getOrCreateSection(json, "theme").add("theme_customizer", jsonObject);
      } else {
         getOrCreateSection(json, "modules").add(getModuleKey(Module), jsonObject);
      }
   }

   private static Role getModuleRole(ConfigSharePolicy jsonReview, String actorPinnedText, Module Module) {
      return jsonReview.getEffectiveRole(actorPinnedText, Module.isHidden(), AutoYourUtils.isContains(Module));
   }

   private static boolean isSafeJsonValue(JsonElement json, int jsonPrimitiveLimit) {
      if (json == null || json.isJsonNull()) {
         return true;
      } else if (jsonPrimitiveLimit > 12) {
         return false;
      } else if (json.isJsonPrimitive()) {
         return !json.getAsJsonPrimitive().isString() || json.getAsString().length() <= 2048;
      } else if (json.isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray()) {
            if (!isSafeJsonValue(jsonElement, jsonPrimitiveLimit + 1)) {
               return false;
            }
         }

         return true;
      } else {
         for (Entry entry : json.getAsJsonObject().entrySet()) {
            if (TokenVersionUtils.canCardnumberCreditcard((String)entry.getKey()) || !isSafeJsonValue((JsonElement)entry.getValue(), jsonPrimitiveLimit + 1)) {
               return false;
            }
         }

         return true;
      }
   }

   public static List<ModuleView> toModuleViews(List<Module> list) {
      var arrayList = new ArrayList();

      for (final Module Module : list) {
         final String moduleKey = getModuleKey(Module);
         arrayList.add(new ModuleView() {
            @Override
            public boolean isLocalOnlySetting(String jsonExtraText) {
               ConfigSetting base = ModuleConfigSerializer.findSetting(Module, jsonExtraText);
               return base != null && base.isExcludedFromSave();
            }

            @Override
            public boolean isConfigPinned() {
               return Module.isHidden();
            }

            @Override
            public String getModuleKey() {
               return moduleKey;
            }

            @Override
            public boolean supportsExtraData() {
               return Module.hasExtraData();
            }

            @Override
            public boolean isAutoPvpModule() {
               return AutoYourUtils.isContains(Module);
            }

            @Override
            public JsonElement getSettingValue(String jsonElementText) {
               ConfigSetting base2 = ModuleConfigSerializer.findSetting(Module, jsonElementText);

               try {
                  return base2 == null ? null : base2.getJsonValue();
               } catch (Exception var4x) {
                  return null;
               }
            }

            @Override
            public int getBindKey() {
               return Module.getBindKey();
            }

            @Override
            public MinmaxEnumType getSettingType(String minmaxEnumText) {
               ConfigSetting base3 = ModuleConfigSerializer.findSetting(Module, minmaxEnumText);
               return base3 == null ? null : base3.getSettingType();
            }

            @Override
            public boolean isEnabled() {
               return Module.isEnabled();
            }

            @Override
            public String getDisplayName() {
               return Module.getCategory();
            }
         });
      }

      return arrayList;
   }

   public static ConfigPresetFile getVersionAuthor(String jsonVersionText, String description2, String author2, List<Module> list) {
      ConfigPresetFile jsonVersion = new ConfigPresetFile();
      jsonVersion.name = jsonVersionText;
      jsonVersion.description = description2;
      jsonVersion.author = author2;
      jsonVersion.clientVersion = getClientVersion();
      jsonVersion.minecraftVersion = "1.21.11";
      jsonVersion.content = stripUnsafeValues(serializeModules(list));
      return jsonVersion;
   }

   private static Module findModuleByKey(List<Module> list, String jsonExtraText) {
      for (Module toggle : list) {
         if (getModuleKey(toggle).equals(jsonExtraText)) {
            return toggle;
         }
      }

      return null;
   }

   private static int applyModuleSettings(Module Module, String notesAppliedText, JsonObject json, ConfigSharePolicy jsonReview, boolean notesAppliedFlag, ModuleConfigSerializer.Result result) {
      int notesApplied = 0;

      for (ConfigSetting autoEspBase : Module.getSettings()) {
         if (!autoEspBase.isExcludedFromSave()) {
            String settingName = autoEspBase.getSettingName();
            if (json.has(settingName) && (notesAppliedFlag || jsonReview.classifyScope(notesAppliedText, settingName, autoEspBase.getSettingType()) != Scope.LOCAL)) {
               try {
                  switch (autoEspBase.applyJsonValue(json.get(settingName))) {
                     case APPLIED:
                        notesApplied++;
                        break;
                     case CLAMPED:
                        notesApplied++;
                        result.clampedSettingCount++;
                        break;
                     case REJECTED:
                        result.notes.add("Invalid value for " + Module.getCategory() + "/" + settingName + "; kept current.");
                     case NOT_HANDLED:
                  }
               } catch (Exception e) {
                  result.notes.add("Skipped invalid value for " + Module.getCategory() + "/" + settingName);
               }
            }
         }
      }

      return notesApplied;
   }

   private static JsonObject getOrCreateSection(JsonObject json, String jsonText) {
      if (!json.has(jsonText)) {
         json.add(jsonText, new JsonObject());
      }

      return json.getAsJsonObject(jsonText);
   }

   public static ModuleConfigSerializer.Result applyPreset(Preset preset, List<Module> list, Runnable runnable) {
      if (preset == null) {
         ModuleConfigSerializer.Result applyConfig = new ModuleConfigSerializer.Result();
         applyConfig.errorMessage = "No config to apply.";
         return applyConfig;
      } else {
         return getSettingsExtra(preset.jsonVersion, preset.ownSealValid ? ConfigImportOptions.createTrusted() : ConfigImportOptions.createAllSections(), list, runnable);
      }
   }

   public static String exportConfigJson(String jsonExtraText, String jsonExtraText2, String jsonExtraText3)  throws java.io.IOException {
      return formatText(jsonExtraText, jsonExtraText2, jsonExtraText3, ModuleRegistry.getModules());
   }

   private static JsonObject serializeModule(Module Module) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("enabled", Module.isEnabled());
      jsonObject.addProperty("bindKey", Module.getBindKey());
      JsonObject jsonObject2 = new JsonObject();

      for (ConfigSetting autoEspBase : Module.getSettings()) {
         if (!autoEspBase.isExcludedFromSave()) {
            JsonElement maxProperty = autoEspBase.getJsonValue();
            if (maxProperty != null) {
               jsonObject2.add(autoEspBase.getSettingName(), maxProperty);
            }
         }
      }

      jsonObject.add("settings", jsonObject2);
      if (Module.hasExtraData()) {
         JsonElement jsonElement = Module.saveExtraData();
         if (jsonElement != null) {
            jsonObject.add("extra", jsonElement);
         }
      }

      return jsonObject;
   }

   public static ConfigPresetFile sanitizeConfig(ConfigPresetFile jsonVersion, List<Module> list) {
      if (jsonVersion == null) {
         return null;
      } else {
         ConfigPresetFile parseJson = jsonVersion.deepCopy();
         parseJson.content = ConfigImportPlan.sanitizeConfigContent(jsonVersion.content, toModuleViews(list), ConfigSharePolicy.getInstance());
         return parseJson;
      }
   }

   private static boolean canEnableModule(ConfigSharePolicy jsonReview, String jsonExtraText, Module Module, boolean jsonExtraFlag) {
      return jsonExtraFlag ? !Module.isHidden() : jsonReview.isRoleAllowed(getModuleRole(jsonReview, jsonExtraText, Module), true);
   }

   public static ModuleConfigSerializer.Result getSettingsExtra(ConfigPresetFile jsonVersion, ConfigImportOptions jsonExtraEnabled, List<Module> list, Runnable runnable) {
      ModuleConfigSerializer.Result settingsExtra = new ModuleConfigSerializer.Result();
      if (jsonVersion == null) {
         settingsExtra.errorMessage = "No config to apply.";
         return settingsExtra;
      } else {
         if (jsonExtraEnabled == null) {
            jsonExtraEnabled = ConfigImportOptions.createModulesOnly();
         }

         ConfigSharePolicy instance = ConfigSharePolicy.getInstance();
         if (!instance.isPolicyLoaded()) {
            settingsExtra.errorMessage = "Config safety rules are missing, so nothing was applied.";
            return settingsExtra;
         } else {
            boolean flag = jsonExtraEnabled.isTrustedSource();
            JsonObject json = serializeModules(list);
            ConfigImportPlan themeModules = ConfigImportPlan.getThemeModules(jsonVersion.content, jsonExtraEnabled, toModuleViews(list), instance);
            settingsExtra.notes.addAll(themeModules.notes);

            try {
               for (Entry entry : themeModules.extra.entrySet()) {
                  String settingsExtraText = (String)entry.getKey();
                  Module toggle = findModuleByKey(list, settingsExtraText);
                  if (toggle != null) {
                     JsonObject jsonObject = (JsonObject)entry.getValue();
                     if (jsonObject.has("settings") && jsonObject.get("settings").isJsonObject()) {
                        settingsExtra.appliedSettingCount = settingsExtra.appliedSettingCount + applyModuleSettings(toggle, settingsExtraText, jsonObject.getAsJsonObject("settings"), instance, flag, settingsExtra);
                     }

                     if (jsonObject.has("bindKey") && canSetBindKey(instance, settingsExtraText, toggle, jsonObject.get("bindKey").getAsInt(), flag)) {
                        toggle.setBindKey(jsonObject.get("bindKey").getAsInt());
                        settingsExtra.appliedBindKeyCount++;
                     }

                     if (jsonObject.has("extra") && toggle.hasExtraData() && (flag || instance.isExtraAllowed(settingsExtraText))) {
                        toggle.loadExtraData(jsonObject.get("extra"));
                     }

                     toggle.onConfigLoaded();
                  }
               }

               for (String jsonExtraText : themeModules.modulesToDisable) {
                  Module moduleByKey = findModuleByKey(list, jsonExtraText);
                  if (moduleByKey != null) {
                     moduleByKey.setEnabled(false);
                  }
               }

               for (String settingsExtraText3 : themeModules.modulesToEnable) {
                  Module Module = findModuleByKey(list, settingsExtraText3);
                  if (Module != null && canEnableModule(instance, settingsExtraText3, Module, flag)) {
                     Module.setEnabled(true);
                  }
               }

               runnable.run();
               settingsExtra.trustedSource = flag;
               settingsExtra.keptLocalCount = themeModules.keptLocalCount;

               for (String settingsExtraText4 : themeModules.notTurnedOnModules) {
                  Module moduleByKey4 = findModuleByKey(list, settingsExtraText4);
                  settingsExtra.notEnabledModuleNames.add(moduleByKey4 != null ? moduleByKey4.getCategory() : settingsExtraText4);
               }

               settingsExtra.applied = true;
               return settingsExtra;
            } catch (Exception e) {
               try {
                  rollbackModules(json, list);
                  runnable.run();
                  settingsExtra.rolledBack = true;
               } catch (Exception e2) {
               }

               settingsExtra.errorMessage = "Apply failed: " + e.getMessage();
               return settingsExtra;
            }
         }
      }
   }

   public static List<ModuleView> getItems() {
      return toModuleViews(ModuleRegistry.getModules());
   }

   private static JsonObject serializeModules(List<Module> list) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("modules", new JsonObject());

      for (Module Module : list) {
         addModuleJson(jsonObject, Module, serializeModule(Module));
      }

      return jsonObject;
   }

   static ConfigPresetFile buildVersionFromModulesJson(JsonObject json, List<Module> list) {
      ConfigPresetFile jsonVersion = new ConfigPresetFile();
      jsonVersion.name = "modules.json";
      JsonObject content = new JsonObject();
      content.add("modules", new JsonObject());

      for (Module Module : list) {
         JsonElement fogCrystal = ModulesJsonStorage.findModuleJson(json, Module);
         if (fogCrystal != null && fogCrystal.isJsonObject()) {
            addModuleJson(content, Module, fogCrystal.getAsJsonObject().deepCopy());
         }
      }

      jsonVersion.content = content;
      return jsonVersion;
   }

   private ModuleConfigSerializer() {
   }

   private static void rollbackModules(JsonObject json, List<Module> list) {
      for (String extraSettingsText : ConfigImportPlan.SECTION_NAMES) {
         if (json.has(extraSettingsText)) {
            for (Entry entry : json.getAsJsonObject(extraSettingsText).entrySet()) {
               Module toggle = findModuleByKey(list, (String)entry.getKey());
               if (toggle != null) {
                  JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();

                  for (ConfigSetting autoEspBase : toggle.getSettings()) {
                     if (!autoEspBase.isExcludedFromSave() && asJsonObject.getAsJsonObject("settings").has(autoEspBase.getSettingName())) {
                        try {
                           autoEspBase.applyJsonValue(asJsonObject.getAsJsonObject("settings").get(autoEspBase.getSettingName()));
                        } catch (Exception e) {
                        }
                     }
                  }

                  toggle.setBindKey(asJsonObject.get("bindKey").getAsInt());
                  if (asJsonObject.has("extra") && toggle.hasExtraData()) {
                     toggle.loadExtraData(asJsonObject.get("extra"));
                  }

                  boolean asBoolean = asJsonObject.get("enabled").getAsBoolean();
                  if (asBoolean != toggle.isEnabled()) {
                     toggle.setEnabled(asBoolean);
                  }
               }
            }
         }
      }
   }

   static String formatText(String jsonExtraText, String jsonExtraText2, String jsonExtraText3, List<Module> list)  throws java.io.IOException {
      return PresetFileStore.saveAsSealedLocalPreset(getVersionAuthor(jsonExtraText, jsonExtraText2, jsonExtraText3, list));
   }

   private static JsonObject stripUnsafeValues(JsonObject extraJson) {
      for (String extraJsonText : ConfigImportPlan.SECTION_NAMES) {
         if (extraJson.has(extraJsonText)) {
            JsonObject asJsonObject = extraJson.getAsJsonObject(extraJsonText);

            for (String copyOfItem : List.copyOf(asJsonObject.keySet())) {
               if (TokenVersionUtils.canCardnumberCreditcard(copyOfItem)) {
                  asJsonObject.remove(copyOfItem);
               } else {
                  JsonObject asJsonObject2 = asJsonObject.getAsJsonObject(copyOfItem);
                  JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("settings");

                  for (String cardnumberCreditcardText : List.copyOf(asJsonObject3.keySet())) {
                     if (TokenVersionUtils.canCardnumberCreditcard(cardnumberCreditcardText) || !isSafeJsonValue(asJsonObject3.get(cardnumberCreditcardText), 6)) {
                        asJsonObject3.remove(cardnumberCreditcardText);
                     }
                  }

                  if (asJsonObject2.has("extra") && !isSafeJsonValue(asJsonObject2.get("extra"), 5)) {
                     asJsonObject2.remove("extra");
                  }
               }
            }
         }
      }

      return extraJson;
   }

   private static boolean canSetBindKey(ConfigSharePolicy jsonReview, String jsonExtraText, Module Module, int jsonExtraValue, boolean jsonExtraFlag) {
      return jsonExtraFlag ? ConfigValueSanitizer.isValidKeyCode(jsonExtraValue) : jsonReview.canSharePortableKeybind(getModuleRole(jsonReview, jsonExtraText, Module), jsonExtraValue);
   }

   public static ModuleConfigSerializer.Result getSettingSetup(Preset preset) {
      return applyPreset(preset, ModuleRegistry.getModules(), ModuleConfigAutosave::updateFlag);
   }

   public static ConfigPresetFile getJsonVersion(ConfigPresetFile jsonVersion) {
      return sanitizeConfig(jsonVersion, ModuleRegistry.getModules());
   }

   private static ConfigSetting findSetting(Module Module, String jsonExtraText) {
      for (ConfigSetting base : Module.getSettings()) {
         if (base.getSettingName().equals(jsonExtraText)) {
            return base;
         }
      }

      return null;
   }

   public static final class Result {
      public String errorMessage;
      public final List<String> notes;
      public boolean applied = false;
      public boolean rolledBack = false;
      public boolean trustedSource;
      public int appliedSettingCount = 0;
      public final List<String> notEnabledModuleNames;
      public int clampedSettingCount;
      public int keptLocalCount;
      public int appliedBindKeyCount = 0;

      public Result() {
         this.clampedSettingCount = 0;
         this.keptLocalCount = 0;
         this.trustedSource = false;
         this.notEnabledModuleNames = new ArrayList<>();
         this.notes = new ArrayList<>();
         this.errorMessage = null;
      }

      public String getKeptLocalNote() {
         return this.keptLocalCount <= 0
            ? null
            : this.keptLocalCount + (this.keptLocalCount == 1 ? " setting" : " settings") + " kept from your own setup for safety.";
      }

      public String getSummaryMessage() {
         String append = this.getKeptLocalNote();
         String formatText = this.getNotEnabledNote();
         if (append == null) {
            return formatText;
         } else {
            return formatText == null ? append : append + " " + formatText;
         }
      }

      public String getNotEnabledNote() {
         return this.notEnabledModuleNames.isEmpty() ? null : "Not turned on for safety: " + String.join(", ", this.notEnabledModuleNames) + ". Turn them on yourself if you want them.";
      }
   }
}
