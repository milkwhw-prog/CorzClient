package com.corz.client.categories.settings;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.gui.ScreenSoundsUtils;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class ClientSounds extends Module {
   private final DoubleSetting doubleHelper;
   private final BooleanSetting booleanBase;
   private final DoubleSetting csCardOpen;
   private final BooleanSetting categoryClickSound;
   private final BooleanSetting moduleCardSound;
   private final EnumSetting enumHelper = new EnumSetting(
      "Sound Pack", "Which enable/disable sound set to use", "Default", "Default", "Pack 1", "Pack 2", "Pack 3", "Pack TH", "Pack 4"
   );
   private final BooleanSetting hoverSound;
   private final EnumSetting hoverStyle;

   public static void updateCardOpen() {
      if (ModuleRegistry.getModule(ClientSounds.class) instanceof ClientSounds clientSounds && clientSounds.moduleCardSound.isEnabled()) {
         handleSoundPlay("cs_card_open", (float)clientSounds.csCardOpen.getValue());
      }
   }

   public ClientSounds() {
      super("Client Sounds", IconsPngType.SETTING);
      this.csCardOpen = new DoubleSetting("Volume", "Sound volume", 1.7, 0.1, 2.0, 0.1);
      this.booleanBase = new BooleanSetting("Module Sound", "Play on every module toggle", true);
      this.hoverSound = new BooleanSetting("Hover Sound", "Play a soft tick when hovering the menu", true);
      this.hoverStyle = new EnumSetting("Hover Style", "Which hover tick to play", "Soft Tick", "Soft Tick", "Scroll");
      this.doubleHelper = new DoubleSetting("Hover Volume", "Hover tick volume (kept subtle)", 0.45, 0.05, 1.0, 0.05);
      this.categoryClickSound = new BooleanSetting("Category Click Sound", "Play a click when switching category", true);
      this.moduleCardSound = new BooleanSetting("Module Card Sound", "Play a sound when a module card opens", true);
      this.addSettings(
         new ConfigSetting[]{
            this.enumHelper, this.csCardOpen, this.booleanBase, this.hoverSound, this.hoverStyle, this.doubleHelper, this.categoryClickSound, this.moduleCardSound
         }
      );
   }

   private static SoundEvent getSoundEvent(String soundEventText) {
      return SoundEvent.of(Identifier.of("corzclient", soundEventText));
   }

   public static void updateScrollHover() {
      if (ModuleRegistry.getModule(ClientSounds.class) instanceof ClientSounds clientSounds && clientSounds.hoverSound.isEnabled()) {
         if (mc.getSoundManager() != null) {
            SoundEvent soundEvent;
            float scrollHoverValue;
            if ("Scroll".equals(clientSounds.hoverStyle.getCurrentOption())) {
               soundEvent = getSoundEvent("cs_hover_scroll");
               scrollHoverValue = 1.0F;
            } else {
               soundEvent = getSoundEvent("cs_default_on");
               scrollHoverValue = 1.25F;
            }

            mc.getSoundManager().play(PositionedSoundInstance.ui(soundEvent, scrollHoverValue, (float)clientSounds.doubleHelper.getValue()));
         }
      }
   }

   private SoundEvent[] getPackDefault() {
      String formatText = this.enumHelper.getCurrentOption();

      return switch (formatText) {
         case "Pack 1" -> new SoundEvent[]{getSoundEvent("cs_pack1_on"), getSoundEvent("cs_pack1_off")};
         case "Pack 2" -> new SoundEvent[]{getSoundEvent("cs_pack2_on"), getSoundEvent("cs_pack2_off")};
         case "Pack 3" -> new SoundEvent[]{getSoundEvent("cs_pack3_on"), getSoundEvent("cs_pack3_off")};
         case "Pack TH" -> new SoundEvent[]{getSoundEvent("cs_th_on"), getSoundEvent("cs_th_off")};
         case "Pack 4" -> new SoundEvent[]{getSoundEvent("cs_pack4_on"), getSoundEvent("cs_pack4_off")};
         default -> new SoundEvent[]{getSoundEvent("cs_default_on"), getSoundEvent("cs_default_off")};
      };
   }

   public static void handlePlaySound(Module Module, boolean playSoundFlag) {
      if (ModuleRegistry.getModule(ClientSounds.class) instanceof ClientSounds clientSounds && clientSounds.booleanBase.isEnabled()) {
         if (ScreenSoundsUtils.isGuiClickApiOpen()) {
            if (mc.player != null) {
               SoundEvent[] packDefault = clientSounds.getPackDefault();
               mc.player.playSound(playSoundFlag ? packDefault[0] : packDefault[1], (float)clientSounds.csCardOpen.getValue(), 1.0F);
            }
         }
      }
   }

   public static void updateCategoryClick() {
      if (ModuleRegistry.getModule(ClientSounds.class) instanceof ClientSounds clientSounds && clientSounds.categoryClickSound.isEnabled()) {
         handleSoundPlay("cs_category_click", (float)clientSounds.csCardOpen.getValue());
      }
   }

   private static void handleSoundPlay(String soundPlayText, float soundPlayValue) {
      if (mc.getSoundManager() != null) {
         mc.getSoundManager().play(PositionedSoundInstance.ui(getSoundEvent(soundPlayText), 1.0F, soundPlayValue));
      }
   }
}
