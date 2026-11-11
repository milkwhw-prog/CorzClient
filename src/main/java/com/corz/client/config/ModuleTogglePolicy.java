package com.corz.client.config;

import com.google.gson.JsonElement;
import com.corz.client.utils.MinmaxEnumType;

public final class ModuleTogglePolicy {
   public static boolean canConfigSetEnabled(ModuleTogglePolicy.ModuleView moduleView, boolean jsonExtraFlag) {
      if (moduleView == null) {
         return false;
      } else {
         return !jsonExtraFlag ? true : !moduleView.isConfigPinned();
      }
   }

   private ModuleTogglePolicy() {
   }

   public static boolean canFlag(ConfigSharePolicy jsonReview, ModuleTogglePolicy.ModuleView moduleView, boolean jsonExtraFlag) {
      if (moduleView != null && jsonReview != null && jsonReview.isPolicyLoaded()) {
         ConfigSharePolicy.Role riskyPortable = jsonReview.getEffectiveRole(moduleView.getModuleKey(), moduleView.isConfigPinned(), moduleView.isAutoPvpModule());
         return jsonReview.isRoleAllowed(riskyPortable, jsonExtraFlag) && canConfigSetEnabled(moduleView, jsonExtraFlag);
      } else {
         return false;
      }
   }

   public interface ModuleView {
      boolean isConfigPinned();

      default boolean supportsExtraData() {
         return false;
      }

      default int getBindKey() {
         return -1;
      }

      default JsonElement getSettingValue(String jsonElementText) {
         return null;
      }

      default boolean isLocalOnlySetting(String moduleViewText) {
         return false;
      }

      boolean isEnabled();

      default boolean isAutoPvpModule() {
         return false;
      }

      String getModuleKey();

      default String getDisplayName() {
         return this.getModuleKey();
      }

      default MinmaxEnumType getSettingType(String minmaxEnumText) {
         return null;
      }
   }
}
