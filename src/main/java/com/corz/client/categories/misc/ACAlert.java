package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.gui.AcBanner;
import com.corz.client.gui.AcBanner.Card;
import com.corz.client.utils.AcPulse.Change;
import com.corz.client.utils.AcPulse.State;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import com.corz.client.categories.donutsmp.StaffList;
import com.corz.client.utils.AcPulse;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.CorzLines;
import com.corz.client.utils.DonutRegions;
import com.corz.client.utils.IconsPngType;

public class ACAlert extends Module {
   private boolean anticheatBackFlag;
   private final BooleanSetting booleanBase;
   static final String ANTICHEAT_BACK = "Anticheat Back On";
   private final BooleanSetting chatToo;
   private final BooleanSetting backOnAlert = new BooleanSetting("Back On Alert", "Also pop a toast when the anticheat comes back", true);
   static final String ANTICHEAT = "Anticheat Off";
   private final AcPulse acPulse;
   private final Map<String, State> chatSoundMap;
   static final String ANTICHEAT_ON_LABEL = "Anticheat On";

   public ACAlert() {
      super("ACAlert", IconsPngType.MISC);
      this.booleanBase = new BooleanSetting("Sound", "Play a soft sound with each toast", true);
      this.chatToo = new BooleanSetting("Chat Too", "Also post each message in chat", false);
      this.acPulse = new AcPulse();
      this.chatSoundMap = new HashMap<>();
      this.addSettings(new ConfigSetting[]{this.backOnAlert, this.booleanBase, this.chatToo});
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && !mc.isInSingleplayer()) {
         Change atomicInteger = this.acPulse.getAtomicInteger();
         if (atomicInteger != Change.NONE) {
            String formatSmpDonut = this.formatSmpDonut();
            State put = this.chatSoundMap.put(formatSmpDonut, this.acPulse.getState());
            boolean anticheatBackFlag = this.anticheatBackFlag;
            this.anticheatBackFlag = false;
            if (atomicInteger == Change.WENT_OFF) {
               if (put != State.OFF) {
                  this.handlePlayMessage(true, "Anticheat Off", formatSmpDonut, CorzLines.formatWatchingAct());
               }
            } else if (put == State.OFF) {
               if (this.backOnAlert.isEnabled()) {
                  this.handlePlayMessage(false, "Anticheat Back On", formatSmpDonut, CorzLines.formatWatchingLegit());
               }
            } else if (anticheatBackFlag) {
               this.handlePlayMessage(false, "Anticheat On", formatSmpDonut, CorzLines.formatBusinessWatching());
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.acPulse.updateAtomic();
      this.chatSoundMap.clear();
      this.anticheatBackFlag = true;
   }

   @Override
   protected void onDisable() {
      AcBanner.updatePollDeque();
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish.packet instanceof BundlePacket bundlePacket) {
         for (Packet packet : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
            this.handlePulse(packet);
         }
      } else {
         this.handlePulse(autoVanish.packet);
      }
   }

   private String formatSmpDonut() {
      ClientWorld world = mc.world;
      boolean donutsmpAddress = StaffList.isDonutsmpAddress();
      String formatSmpDonut = World.NETHER.equals(world.getRegistryKey()) ? "Nether" : (World.END.equals(world.getRegistryKey()) ? "The End" : null);
      if (donutsmpAddress) {
         if (formatSmpDonut != null) {
            return formatSmpDonut;
         } else {
            String formatAsiaTexts = DonutRegions.formatAsiaTexts(mc.player.getX(), mc.player.getZ());
            return formatAsiaTexts == null ? "DonutSMP" : formatAsiaTexts;
         }
      } else {
         ServerInfo currentServerEntry = mc.getCurrentServerEntry();
         String smpDonutText = currentServerEntry == null ? "this server" : (currentServerEntry.address != null && !currentServerEntry.address.isBlank() ? currentServerEntry.address.trim() : currentServerEntry.name);
         return formatSmpDonut == null ? smpDonutText : smpDonutText + " " + formatSmpDonut;
      }
   }

   private void handlePlayMessage(boolean playMessageFlag, String playMessageText, String playMessageText2, String playMessageText3) {
      AcBanner.handleDequeArray(new Card(playMessageText, playMessageText2, playMessageText3, playMessageFlag));
      if (this.booleanBase.isEnabled()) {
         mc.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_TOAST_IN, playMessageFlag ? 1.15F : 0.8F, 1.0F));
      }

      if (this.chatToo.isEnabled() && mc.player != null) {
         mc.player.sendMessage(Text.literal((playMessageFlag ? "\u00a7a" : "\u00a7c") + playMessageText + " \u00a77" + playMessageText2 + " \u00a7f" + playMessageText3), false);
      }
   }

   private void handlePulse(Packet<?> packet) {
      if (packet instanceof CommonPingS2CPacket) {
         this.acPulse.updateAtomicInteger();
      } else if (packet instanceof WorldTimeUpdateS2CPacket) {
         this.acPulse.updateAtomicIntegerAtomicIntegerAtomicBoolean();
      } else if (packet instanceof GameJoinS2CPacket || packet instanceof PlayerRespawnS2CPacket) {
         this.acPulse.updateAtomic();
      }
   }
}
