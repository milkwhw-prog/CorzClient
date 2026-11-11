package com.corz.client.categories.settings;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
public class Notifications extends Module {
   private static Notifications singleton;
   private final BooleanSetting booleanBase;
   private final BooleanSetting toastNotifications = new BooleanSetting("Toast Notifications", "Show pop-up notifications", false);

   public static boolean isInstanceBase() {
      return singleton == null || singleton.toastNotifications.isEnabled();
   }

   public static boolean isInstanceBaseSingleton() {
      return singleton == null || singleton.booleanBase.isEnabled();
   }

   public Notifications() {
      super("Notifications", IconsPngType.SETTING);
      this.booleanBase = new BooleanSetting("Module Toggle Toasts", "Notify when a module is switched on or off", true);
      this.addSettings(new ConfigSetting[]{this.toastNotifications, this.booleanBase});
      singleton = this;
   }
}
