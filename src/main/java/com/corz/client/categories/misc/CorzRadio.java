package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.RadioSetting;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
public class CorzRadio extends Module {
   private final RadioSetting radioBase = new RadioSetting("Stations", "Staff playlists. Pick a station to open it.");

   @Override
   public void syncSettingHandlers() {
      this.radioBase.setSelectedStationKey("");
      super.syncSettingHandlers();
   }

   public CorzRadio() {
      super("Corz Radio", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.radioBase});
   }
}
