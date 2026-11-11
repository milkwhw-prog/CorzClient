package com.corz.client.config;

public final class ConfigImportOptions {
   public final boolean includeHud;
   private final boolean trustedSource;
   public final boolean includeSettings;
   public final boolean includeTheme;
   public final boolean includeModules;
   public final boolean includeBindKeys;
   public final boolean includeEnabledState;

   public ConfigImportOptions(boolean jsonExtraFlag, boolean enabledFlag, boolean extraSettingsFlag, boolean bindSettingsFlag, boolean jsonExtraFlag5, boolean themeModulesFlag) {
      this(jsonExtraFlag, enabledFlag, extraSettingsFlag, bindSettingsFlag, jsonExtraFlag5, themeModulesFlag, false);
   }

   static ConfigImportOptions createTrusted() {
      return new ConfigImportOptions(true, true, true, true, true, true, true);
   }

   private ConfigImportOptions(boolean modulesRulesFlag, boolean enabledFlag, boolean extraSettingsFlag, boolean bindSettingsFlag, boolean modulesRulesFlag2, boolean themeModulesFlag, boolean flag) {
      this.includeModules = modulesRulesFlag;
      this.includeEnabledState = enabledFlag;
      this.includeSettings = extraSettingsFlag;
      this.includeBindKeys = bindSettingsFlag;
      this.includeHud = modulesRulesFlag2;
      this.includeTheme = themeModulesFlag;
      this.trustedSource = flag;
   }

   public boolean isTrustedSource() {
      return this.trustedSource;
   }

   public static ConfigImportOptions createModulesOnly() {
      return new ConfigImportOptions(true, true, true, true, false, false);
   }

   public static ConfigImportOptions createAllSections() {
      return new ConfigImportOptions(true, true, true, true, true, true);
   }
}
