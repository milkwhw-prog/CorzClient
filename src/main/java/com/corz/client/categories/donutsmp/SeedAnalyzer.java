package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.mixins.AccessorBiomeAccess;
import net.minecraft.text.Text;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class SeedAnalyzer extends Module {
   private final BooleanSetting showHex;
   private final BooleanSetting copyToClipboard = new BooleanSetting(
      "Copy To Clipboard", "Copy the seed to the clipboard so it can be pasted straight into a seed map.", true
   );

   private void sendChatMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   public SeedAnalyzer() {
      super("SeedAnalyzer", IconsPngType.DONUTSMP);
      this.showHex = new BooleanSetting("Show Hex", "Also print the seed in hexadecimal.", true);
      this.addSettings(new ConfigSetting[]{this.copyToClipboard, this.showHex});
   }

   @Override
   protected void onEnable() {
      if (mc.player != null && mc.world != null) {
         long seed = ((AccessorBiomeAccess)mc.world.getBiomeAccess()).getSeed();
         boolean inSingleplayer = mc.isInSingleplayer();
         String seedAnalyzerText = inSingleplayer ? "world seed" : "server biome seed (hashed \u2014 not the true world seed)";
         this.sendChatMessage("\u00a7b[SeedAnalyzer]\u00a7r " + seedAnalyzerText);
         this.sendChatMessage("  \u00a77decimal \u00a7f" + seed);
         if (this.showHex.isEnabled()) {
            this.sendChatMessage("  \u00a77hex \u00a7f0x" + Long.toHexString(seed));
         }

         if (this.copyToClipboard.isEnabled()) {
            mc.keyboard.setClipboard(Long.toString(seed));
            this.sendChatMessage("  \u00a77copied to clipboard \u2014 paste it into a seed map");
            DisabledEnabled.getInstance().handleDisabledEnabled("SeedAnalyzer", "Seed copied \u00b7 " + seed, true);
         } else {
            DisabledEnabled.getInstance().handleDisabledEnabled("SeedAnalyzer", "Seed \u00b7 " + seed, true);
         }

         this.setEnabled(false);
      } else {
         DisabledEnabled.getInstance().handleDisabledEnabled("SeedAnalyzer", "Join a world first", false);
         this.setEnabled(false);
      }
   }
}
