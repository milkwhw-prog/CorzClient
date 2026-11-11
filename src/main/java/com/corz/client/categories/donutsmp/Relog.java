package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.RelogPlan.Verdict;
import com.corz.client.gui.DisabledEnabled;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import com.corz.client.utils.RelogPlan;
import com.corz.client.categories.settings.Notifications;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class Relog extends Module {
   private static long lastRelogStartMillis;
   private ClientConnection connection;
   private long relogStartMillis;
   private Relog.Phase phase;
   private int attemptGeneration;
   private final IntegerSetting waitSeconds = new IntegerSetting(
      "Wait", "Seconds to wait before joining again. 0 joins right away; raise it if the server says you joined too fast.", 0, 0, 10
   );
   private ServerInfo serverInfo;

   public Relog() {
      super("Relog Method", IconsPngType.DONUTSMP);
      this.phase = Relog.Phase.IDLE;
      this.addSettings(new ConfigSetting[]{this.waitSeconds});
   }

   @Override
   protected void onDisable() {
      this.attemptGeneration++;
      this.phase = Relog.Phase.IDLE;
      this.serverInfo = null;
      this.connection = null;
   }

   private void finishRelog(String phaseIdleText, boolean phaseIdleFlag) {
      this.phase = Relog.Phase.IDLE;
      if (this.isEnabled()) {
         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            this.setEnabled(false);
         } catch (Exception e) {
            this.phase = Relog.Phase.IDLE;
         }
      }

      if (phaseIdleText != null) {
         this.sendRelogMessage(phaseIdleText, phaseIdleFlag);
      }
   }

   private void sendRelogMessage(String relogMessageText, boolean relogMessageFlag) {
      if (!Notifications.isInstanceBase()) {
         if (mc.player != null) {
            mc.player.sendMessage(Text.literal("\u00a77[Relog]\u00a7r " + relogMessageText), false);
         }
      } else {
         DisabledEnabled.getInstance().handleDisabledEnabled("Relog Method", relogMessageText, relogMessageFlag);
      }
   }

   @Override
   protected void onReset() {
      if (this.phase == Relog.Phase.JOINING && mc.player != null && mc.getNetworkHandler() != null) {
         if (mc.getNetworkHandler().getConnection() != this.connection) {
            ServerInfo currentServerEntry = mc.getCurrentServerEntry();
            long elapsedMillis = System.currentTimeMillis() - this.relogStartMillis;
            String previousServerAddress = this.serverInfo == null ? null : this.serverInfo.address;
            if (RelogPlan.isRejoinedSameServer(previousServerAddress, currentServerEntry == null ? null : currentServerEntry.address, elapsedMillis)) {
               this.finishRelog("Relogged into " + getServerDisplayName(this.serverInfo) + ".", true);
            } else {
               this.finishRelog(null, false);
            }
         }
      }
   }

   private void beginDisconnect(int screenNetworkValue) {
      if (screenNetworkValue == this.attemptGeneration && this.phase == Relog.Phase.LEAVING && this.isEnabled() && this.serverInfo != null) {
         if (mc.world != null
            && mc.getNetworkHandler() != null
            && mc.getNetworkHandler().getConnection() == this.connection) {
            this.phase = Relog.Phase.JOINING;
            mc.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
            MultiplayerScreen screen = mc.currentScreen instanceof MultiplayerScreen multiplayerScreen ? multiplayerScreen : new MultiplayerScreen(new TitleScreen());
            long waitSecondsToMillis = RelogPlan.waitSecondsToMillis(this.waitSeconds.getValue());
            if (waitSecondsToMillis <= 0L) {
               this.reconnectToServer(screenNetworkValue, screen, null);
            } else {
               if (mc.currentScreen != screen) {
                  mc.setScreen(screen);
               }

               Screen currentScreen = mc.currentScreen;
               CompletableFuture.delayedExecutor(waitSecondsToMillis, TimeUnit.MILLISECONDS)
                  .execute(() -> mc.execute(() -> this.reconnectToServer(screenNetworkValue, screen, currentScreen)));
            }
         } else {
            this.finishRelog(null, false);
         }
      }
   }

   private void reconnectToServer(int serverConnectValue, Screen screen, Screen previousScreen) {
      if (serverConnectValue == this.attemptGeneration && this.phase == Relog.Phase.JOINING && this.isEnabled() && this.serverInfo != null) {
         if (mc.world == null && (previousScreen == null || mc.currentScreen == previousScreen)) {
            ConnectScreen.connect(screen, mc, ServerAddress.parse(this.serverInfo.address), this.serverInfo, false, null);
         } else {
            this.finishRelog(null, false);
         }
      }
   }

   private static String getServerDisplayName(ServerInfo serverInfo) {
      if (serverInfo == null) {
         return "the server";
      } else {
         return serverInfo.name != null && !serverInfo.name.isBlank() ? serverInfo.name : serverInfo.address;
      }
   }

   @Override
   protected void onEnable() {
      if (mc != null) {
         int relogValue = ++this.attemptGeneration;
         this.phase = Relog.Phase.IDLE;
         this.serverInfo = null;
         this.connection = null;
         long currentTimeMillis = System.currentTimeMillis();
         boolean relogFlag = mc.player != null && mc.world != null && mc.getNetworkHandler() != null;
         ServerInfo serverInfo = relogFlag ? mc.getCurrentServerEntry() : null;
         Verdict realmSingleplayer = RelogPlan.checkRelogAllowed(
            relogFlag, mc.isInSingleplayer(), serverInfo == null ? null : serverInfo.address, serverInfo != null && serverInfo.isRealm(), currentTimeMillis, lastRelogStartMillis
         );
         if (realmSingleplayer != Verdict.GO) {
            String formatHappenedRelog = RelogPlan.getVerdictMessage(realmSingleplayer);
            mc.send(() -> {
               if (relogValue == this.attemptGeneration) {
                  this.finishRelog(formatHappenedRelog, false);
               }
            });
         } else {
            lastRelogStartMillis = currentTimeMillis;
            this.serverInfo = serverInfo;
            this.connection = mc.getNetworkHandler().getConnection();
            this.relogStartMillis = currentTimeMillis;
            this.phase = Relog.Phase.LEAVING;
            mc.send(() -> this.beginDisconnect(relogValue));
         }
      }
   }

   private static enum Phase {
      IDLE,
      LEAVING,
      JOINING;
   }
}
