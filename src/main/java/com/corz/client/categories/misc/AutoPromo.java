package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.StringBase;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoPromo extends Module {
   private final Map<UUID, String> deathSendMap;
   private final Map<UUID, Long> iIiPositionMap;
   private final StringBase stringBase = new StringBase("Message", "Message to send after kill.", "dominated by CorzClient | corz.gg");
   private final BooleanSetting booleanBase = new BooleanSetting("Wait for Death", "Only send after target's death is confirmed.", true);

   public AutoPromo() {
      super("Auto Promo", IconsPngType.MISC);
      this.iIiPositionMap = new HashMap<>();
      this.deathSendMap = new HashMap<>();
      this.addSettings(new ConfigSetting[]{this.stringBase, this.booleanBase});
   }

   public void handleUuidDeath(PlayerEntity player) {
      if (this.isEnabled()) {
         this.iIiPositionMap.put(player.getUuid(), System.currentTimeMillis());
         this.deathSendMap.put(player.getUuid(), player.getName().getString());
      }
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null) {
         long currentMillis = System.currentTimeMillis();
         this.iIiPositionMap.entrySet().removeIf(entry -> currentMillis - entry.getValue() > 10000L);
         if (this.booleanBase.isEnabled()) {
            for (UUID uuid : new HashSet<>(this.iIiPositionMap.keySet())) {
               boolean anyMatch = mc.world.getPlayers().stream().anyMatch(abstractClientPlayerEntity -> abstractClientPlayerEntity.getUuid().equals(uuid) && abstractClientPlayerEntity.isAlive());
               if (!anyMatch) {
                  String remove = this.getRemove(uuid);
                  this.iIiPositionMap.remove(uuid);
                  if (remove != null) {
                     this.handleChatCommand(remove);
                  }
               }
            }
         }
      }
   }

   private String getRemove(UUID uuid) {
      return this.deathSendMap.remove(uuid);
   }

   public void handleBase(String autoPromoText) {
      if (this.isEnabled()) {
         if (!this.booleanBase.isEnabled()) {
            this.handleChatCommand(autoPromoText);
         }
      }
   }

   @Override
   public void onDisable() {
      this.iIiPositionMap.clear();
   }

   @Override
   public void onEnable() {
      this.iIiPositionMap.clear();
   }

   private void handleChatCommand(String chatCommandText) {
      if (mc.player != null) {
         String currentText = this.stringBase.getCurrentText();
         mc.player.networkHandler.sendChatCommand("msg " + chatCommandText + " " + currentText);
      }
   }
}
