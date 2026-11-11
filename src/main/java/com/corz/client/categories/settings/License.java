package com.corz.client.categories.settings;

import com.corz.client.config.ConfigSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.CorzLicense;
import net.minecraft.client.MinecraftClient;
import ai.auth.LicenseRuntime;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class License extends Module {

   public License() {
      super("License", IconsPngType.SETTING);
      this.addSettings(new ConfigSetting[]{new ButtonBase("License status", "Open the license panel", "View License", () -> {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.setScreen(new CorzLicense(instance.currentScreen));
      }).getSupplier(LicenseRuntime::getLicenseOwnerLabel).getSupplierForSupplier(() -> -12922502)});
   }

   @Override
   public boolean isHiddenModule() {
      return true;
   }
}
