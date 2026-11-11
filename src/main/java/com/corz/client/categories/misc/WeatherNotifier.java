package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.DisabledEnabled;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class WeatherNotifier extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting soundNotificationBar;
   private final BooleanSetting rainIIi = new BooleanSetting("Rain", "Announce when rain starts/stops", true);
   private Boolean raining;
   private final EnumSetting enumHelper;
   private Boolean thundering;

   @Override
   protected void onReset() {
      ClientWorld world = mc.world;
      if (world != null && mc.player != null) {
         boolean raining = world.isRaining();
         boolean thundering = world.isThundering();
         if (this.raining != null && this.thundering != null) {
            if (thundering != this.thundering) {
               this.thundering = thundering;
               if (this.booleanBase.isEnabled()) {
                  if (thundering) {
                     this.handleNotificationBar("Thunderstorm", "A storm rolls in", true);
                  } else {
                     this.handleNotificationBar("Storm cleared", "The thunder has passed", false);
                  }
               }
            }

            if (raining != this.raining) {
               this.raining = raining;
               if (this.rainIIi.isEnabled() && !thundering) {
                  if (raining) {
                     this.handleNotificationBar("Rain", "Rain starts to fall", true);
                  } else {
                     this.handleNotificationBar("Skies cleared", "The rain has stopped", false);
                  }
               }
            }
         } else {
            this.raining = raining;
            this.thundering = thundering;
         }
      }
   }

   @Override
   protected void onEnable() {
      this.raining = null;
      this.thundering = null;
   }

   public WeatherNotifier() {
      super("WeatherNotifier", IconsPngType.MISC);
      this.booleanBase = new BooleanSetting("Thunder", "Announce when a thunderstorm starts/stops", true);
      this.soundNotificationBar = new BooleanSetting("Sound", "Play a ping on each change", true);
      this.enumHelper = new EnumSetting("Output", "How the change is shown", "Notification", "Notification", "Chat", "Action Bar");
      this.addSettings(new ConfigSetting[]{this.rainIIi, this.booleanBase, this.soundNotificationBar, this.enumHelper});
   }

   private void handleNotificationBar(String notificationBarText, String substringText, boolean notificationBarFlag) {
      String formatText = this.enumHelper.getCurrentOption();
      if ("Notification".equals(formatText)) {
         DisabledEnabled.getInstance().handleDisabledEnabled(notificationBarText, substringText, notificationBarFlag);
      } else {
         boolean equals = "Action Bar".equals(formatText);
         MutableText literal = Text.literal("\u00a7d[Corz] \u00a7f" + notificationBarText + " \u2014 " + substringText);
         if (mc.player != null) {
            mc.player.sendMessage(literal, equals);
         }
      }

      if (this.soundNotificationBar.isEnabled() && mc.player != null) {
         mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6F, notificationBarFlag ? 1.2F : 0.8F);
      }
   }
}
