package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.EnumSetting;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
public class TimeChanger extends Module {
   private final EnumSetting enumHelper = new EnumSetting("Time", "Client-side time of day", "Day", "Day", "Noon", "Sunset", "Night", "Midnight");

   @Override
   public void onReset() {
      if (mc.world != null) {
         mc.world.getLevelProperties().setTimeOfDay(this.getNoonMidnight());
      }
   }

   private long getNoonMidnight() {
      String formatText = this.enumHelper.getCurrentOption();

      return switch (formatText) {
         case "Noon" -> 6000L;
         case "Sunset" -> 12000L;
         case "Night" -> 14000L;
         case "Midnight" -> 18000L;
         default -> 1000L;
      };
   }

   public TimeChanger() {
      super("Time Changer", IconsPngType.MISC);
      this.addSettings(new ConfigSetting[]{this.enumHelper});
   }
}
