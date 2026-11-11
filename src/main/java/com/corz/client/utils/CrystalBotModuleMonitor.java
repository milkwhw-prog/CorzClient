package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.gui.DisabledEnabled;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.categories.combat.SafeAnchor;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.BotModuleConflicts;

public final class CrystalBotModuleMonitor {
   public static final String AND_MORE_SUFFIX = " and more";
   private String blockingModulesText;
   private final Module ownerModule;
   private String conflictingModuleName;
   private boolean yieldingModuleActive;
   private final Map<String, Long> lastNoticeMsByKey = new HashMap<>();
   private long lastScanTime = Long.MIN_VALUE;

   public void showThrottledNotice(String botCrystalText, String botCrystalText2, String substringText, long botCrystalValue) {
      if (botCrystalText != null && substringText != null) {
         Long crystalbotmap = this.lastNoticeMsByKey.get(botCrystalText);
         if (crystalbotmap == null || botCrystalValue < crystalbotmap || botCrystalValue - crystalbotmap >= 60000L) {
            this.lastNoticeMsByKey.put(botCrystalText, botCrystalValue);
            DisabledEnabled.getInstance().handleDisabledEnabled(botCrystalText2 == null ? "Crystal PvP Bot" : botCrystalText2, substringText, false);
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
               player.sendMessage(Text.literal("[C").formatted(Formatting.RED).append(Text.literal(substringText).formatted(Formatting.WHITE)), false);
            }
         }
      }
   }

   public void reset() {
      this.lastNoticeMsByKey.clear();
      this.lastScanTime = Long.MIN_VALUE;
      this.conflictingModuleName = null;
      this.blockingModulesText = null;
      this.yieldingModuleActive = false;
   }

   public String getBlockingModulesText() {
      this.scanActiveModules();
      return this.blockingModulesText;
   }

   private void scanActiveModules() {
      long time = ClientThreadUtils.getTime();
      if (time != this.lastScanTime) {
         this.lastScanTime = time;
         String text = null;
         StringBuilder stringBuilder = null;
         int autoPearlCounter = 0;
         boolean flag = false;

         for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
            if (iilliliilIIiItem != null && iilliliilIIiItem != this.ownerModule && iilliliilIIiItem.isEnabled()) {
               String autoPearlText = iilliliilIIiItem.getName();
               if (text == null && (AutoYourUtils.canUpdatePvp(iilliliilIIiItem) || BotModuleConflicts.REFUSING_MODULES.contains(autoPearlText))) {
                  text = iilliliilIIiItem.getCategory();
               }

               if (BotModuleConflicts.BLOCKING_MODULES.contains(autoPearlText)) {
                  if (autoPearlCounter < 2) {
                     if (stringBuilder == null) {
                        stringBuilder = new StringBuilder(iilliliilIIiItem.getCategory());
                     } else {
                        stringBuilder.append(", ").append(iilliliilIIiItem.getCategory());
                     }
                  } else if (autoPearlCounter == 2) {
                     stringBuilder.append(" and more");
                  }

                  autoPearlCounter++;
               }

               if (BotModuleConflicts.YIELDING_MODULES.contains(autoPearlText)) {
                  flag = true;
               }

               if (iilliliilIIiItem instanceof AnchorMacro anchorMacro && anchorMacro.isMacroInteracting()) {
                  flag = true;
               }

               if (iilliliilIIiItem instanceof SafeAnchor safeAnchor && safeAnchor.isShieldOrComboRunning()) {
                  flag = true;
               }
            }
         }

         this.conflictingModuleName = text;
         this.blockingModulesText = stringBuilder == null ? null : stringBuilder.toString();
         this.yieldingModuleActive = flag;
      }
   }

   public CrystalBotModuleMonitor(Module toggle) {
      this.ownerModule = toggle;
   }

   public boolean isYieldingModuleActive() {
      this.scanActiveModules();
      return this.yieldingModuleActive;
   }

   public String getConflictingModuleName() {
      this.scanActiveModules();
      return this.conflictingModuleName;
   }
}
