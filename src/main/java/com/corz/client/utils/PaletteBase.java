package com.corz.client.utils;

import com.corz.client.config.ConfigSetting;
import com.corz.client.utils.MinmaxEnumType;

public class PaletteBase extends ConfigSetting {
   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.PALETTE;
   }

   public PaletteBase(String settingName, String description) {
      super(settingName, description);
   }
}
