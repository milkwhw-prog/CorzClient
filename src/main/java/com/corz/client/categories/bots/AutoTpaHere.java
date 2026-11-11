package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.config.StringBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.TickProgress;
import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import com.corz.client.utils.TpaFlow.Action;
import com.corz.client.utils.TpaFlow.Input;
import com.corz.client.utils.TpaFlow.Settings;
import com.corz.client.utils.TpaFlow.Spot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.world.World;
import com.corz.client.utils.TpaReplies;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.TpaStatusCard;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.TpaFlow;
import com.corz.client.utils.TpaPicker;

public class AutoTpaHere extends Module implements ScissorTgtHandler {
   private ClientConnection connection;
   private String lastTpaTarget;
   private Screen activeConfirmScreen;
   private final IntegerSetting playerCooldown;
   private final IntegerSetting waitAfterDeath;
   private volatile Snapshot menuSnapshot;
   private long confirmScreenSeenMs;
   private static final ConcurrentLinkedQueue<String> RECENT_MESSAGES = new ConcurrentLinkedQueue<>();
   public static final String YOU_LEFT_SERVER = "You left the server";
   private String lastSkipPlayersText;
   private final TpaFlow tpaFlow;
   private static volatile AutoTpaHere instance;
   private final IntegerSetting requestInterval;
   private Set<String> skippedNames;
   private final StringBase homeName = new StringBase("Home Name", "The home you set inside the trap with /sethome. Leave it empty to use plain /home.", "");
   private final TpaStatusCard tpaStatusCard;
   private final BooleanSetting ignoreFriends;
   private boolean confirmPressed;
   private long lastTpaSentMs;
   private final StringBase skipPlayers;
   private World lastWorld;
   private final BooleanSetting waitForAccept;
   private volatile boolean serverTeleported;

   private static boolean pressConfirmButton(Screen screen) {
      var arrayList = new ArrayList();
      collectChildren(screen, arrayList, 0);
      PressableWidget pressableWidget = null;

      for (Element element : (Iterable<net.minecraft.client.gui.Element>) (arrayList)) {
         if (element instanceof PressableWidget pressableWidget2 && pressableWidget2.active && pressableWidget2.visible) {
            String cancelConfirmText = TpaReplies.normalizeSmallCaps(pressableWidget2.getMessage().getString());
            if (cancelConfirmText.contains("confirm") && !cancelConfirmText.contains("cancel")) {
               if (pressableWidget != null && pressableWidget != pressableWidget2) {
                  return false;
               }

               pressableWidget = pressableWidget2;
            }
         }
      }

      return pressableWidget != null && pressableWidget.keyPressed(new KeyInput(257, 0, 0));
   }

   private static Collection<String> getOnlinePlayerNames(ClientPlayNetworkHandler clientPlayNetworkHandler) {
      var arrayList = new ArrayList();

      for (PlayerListEntry playerListEntry : clientPlayNetworkHandler.getPlayerList()) {
         if (playerListEntry != null && playerListEntry.getProfile() != null) {
            arrayList.add(playerListEntry.getProfile().name());
         }
      }

      return arrayList;
   }

   public static void recordOverlayMessage(String concurrentQueueText) {
      if (instance != null && concurrentQueueText != null && !concurrentQueueText.isBlank()) {
         while (RECENT_MESSAGES.size() >= 32) {
            RECENT_MESSAGES.poll();
         }

         RECENT_MESSAGES.offer(concurrentQueueText);
      }
   }

   public void renderStatusCard(DrawContext context) {
      if (context != null && mc.player != null) {
         this.tpaStatusCard.renderStatusCard(context, this.menuSnapshot, this.tpaFlow.getDiedOnceCount(), this.tpaFlow.getValue());
      }
   }

   @Override
   public Snapshot getStatusSnapshot() {
      return this.isEnabled() ? this.menuSnapshot : Snapshot.INSTANCE;
   }

   private void sendTpaCommand(String tpahereChatText) {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler != null && tpahereChatText != null && !tpahereChatText.isBlank()) {
         networkHandler.sendChatCommand(tpahereChatText);
         String trim = tpahereChatText.trim();
         if (trim.regionMatches(true, 0, "tpahere ", 0, 8)) {
            this.lastTpaTarget = trim.substring(8).trim();
            this.lastTpaSentMs = System.currentTimeMillis();
         }
      }
   }

   private static void collectChildren(ParentElement parentElement, List<Element> list, int childrenValue) {
      if (childrenValue <= 8) {
         for (Element element : parentElement.children()) {
            list.add(element);
            if (element instanceof ParentElement parentElement2) {
               collectChildren(parentElement2, list, childrenValue + 1);
            }
         }
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (tickProgress != null && mc.currentScreen == null && !mc.options.hudHidden && mc.player != null) {
         this.tpaStatusCard.renderStatusCard(tickProgress.drawContext, this.menuSnapshot, this.tpaFlow.getDiedOnceCount(), this.tpaFlow.getValue());
      }
   }

   @Override
   protected void onReset() {
      ClientPlayerEntity player = mc.player;
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (player != null && networkHandler != null && mc.world != null) {
         ClientConnection connection = networkHandler.getConnection();
         if (this.connection == null) {
            this.connection = connection;
         } else if (this.connection != connection) {
            this.stopWithMessage("You left the server");
            return;
         }

         long currentMillis = System.currentTimeMillis();
         boolean iIiFlag = mc.world != this.lastWorld;
         this.lastWorld = mc.world;
         boolean leftServerFlag = this.serverTeleported;
         this.serverTeleported = false;
         Screen currentScreen = mc.currentScreen;
         boolean iIiFlag2 = player.isDead() || player.getHealth() <= 0.0F || currentScreen instanceof DeathScreen;
         boolean iIiFlag3 = currentScreen != null && !(currentScreen instanceof DeathScreen);
         boolean substringScreen = this.updateConfirmDialog(currentScreen, currentMillis);
         boolean iIiFlag4 = !substringScreen && currentScreen instanceof DialogScreen
            || currentScreen instanceof HandledScreen handledScreen && !(currentScreen instanceof CreativeInventoryScreen) && handledScreen.getScreenHandler() != player.playerScreenHandler;
         Spot spot = new Spot(mc.world.getRegistryKey().getValue().toString(), player.getX(), player.getY(), player.getZ());
         Settings integerBase = this.buildFlowSettings();
         Input input = new Input(
            currentMillis,
            iIiFlag2,
            iIiFlag3,
            iIiFlag4,
            spot,
            leftServerFlag,
            iIiFlag,
            mc.getSession().getUsername(),
            () -> getOnlinePlayerNames(networkHandler),
            this.getNearbyPlayerNames(player),
            drainRecentMessages()
         );

         Action action;
         try {
            action = this.tpaFlow.getInputTpahere(input, integerBase);
         } catch (RuntimeException e) {
            this.stopWithMessage("Something went wrong, turned off");
            return;
         }

         switch (action.kind()) {
            case COMMAND:
               this.sendTpaCommand(action.text());
               break;
            case RESPAWN:
               player.requestRespawn();
               break;
            case STOP:
               this.stopWithMessage(action.text());
               return;
         }

         this.menuSnapshot = this.tpaFlow.getTrapYour(currentMillis, integerBase);
      }
   }

   private void stopWithMessage(String autoTpaText) {
      AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

      try {
         this.setEnabled(false);
      } finally {
         try {
            disabledEnabled.close();
         } catch (Exception e) {
         }
      }

      DisabledEnabled.getInstance().handleDisabledEnabled(this.getCategory(), autoTpaText, false);
   }

   private static String getScreenText(Screen screen) {
      StringBuilder stringBuilder = new StringBuilder(screen.getTitle().getString());
      var arrayList = new ArrayList();
      collectChildren(screen, arrayList, 0);

      for (Element element : (Iterable<net.minecraft.client.gui.Element>) (arrayList)) {
         if (element instanceof ClickableWidget clickableWidget && !(clickableWidget instanceof PressableWidget)) {
            stringBuilder.append(' ').append(clickableWidget.getMessage().getString());
         }
      }

      return stringBuilder.toString();
   }

   private void resetConfirmState() {
      this.lastTpaTarget = null;
      this.lastTpaSentMs = 0L;
      this.activeConfirmScreen = null;
      this.confirmPressed = false;
   }

   public static boolean isRunning() {
      return instance != null;
   }

   @Override
   protected void onEnable() {
      RECENT_MESSAGES.clear();
      this.connection = null;
      this.serverTeleported = false;
      this.lastWorld = mc.world;
      this.tpaStatusCard.resetAnimationState();
      this.resetConfirmState();
      this.tpaFlow.handleCheckHome(mc.world != null && mc.player != null);
      instance = this;
      this.menuSnapshot = this.tpaFlow.getTrapYour(System.currentTimeMillis(), this.buildFlowSettings());
   }

   private Settings buildFlowSettings() {
      return new Settings(
         this.homeName.getCurrentText(),
         this.requestInterval.getValue(),
         this.playerCooldown.getValue(),
         this.waitAfterDeath.getValue(),
         this.waitForAccept.isEnabled(),
         this.buildSkipPredicate()
      );
   }

   public AutoTpaHere() {
      super("Auto TPA Here", IconsPngType.BOTS);
      this.requestInterval = new IntegerSetting("Request Interval", "Seconds between two /tpahere requests.", 10, 3, 60, 1);
      this.playerCooldown = new IntegerSetting("Player Cooldown", "Minutes before the same player is asked again.", 10, 1, 60, 1);
      this.waitAfterDeath = new IntegerSetting("Wait After Death", "Seconds to wait after you die in the trap before going back with /home.", 15, 5, 60, 1);
      this.waitForAccept = new BooleanSetting(
         "Wait For Accept", "Waits until the last player accepts, says no or the request runs out before asking the next one.", false
      );
      this.ignoreFriends = new BooleanSetting("Ignore Friends", "Never sends a request to players on your friends list.", true);
      this.skipPlayers = new StringBase("Skip Players", "Names that never get a request, separated by commas.", "");
      this.tpaFlow = new TpaFlow(new Random());
      this.tpaStatusCard = new TpaStatusCard();
      this.skippedNames = Set.of();
      this.menuSnapshot = Snapshot.INSTANCE;
      this.addSettings(
         new ConfigSetting[]{
            this.homeName, this.requestInterval, this.playerCooldown, this.waitAfterDeath, this.waitForAccept, this.ignoreFriends, this.skipPlayers
         }
      );
   }

   private Predicate<String> buildSkipPredicate() {
      String text = this.skipPlayers.getCurrentText();
      if (text == null) {
         text = "";
      }

      if (!text.equals(this.lastSkipPlayersText)) {
         var hashSet = new HashSet();

         for (String autoTpaText : text.split("[,\\s]+")) {
            String trim = autoTpaText.trim();
            if (!trim.isEmpty()) {
               hashSet.add(TpaPicker.getLowerCase(trim));
            }
         }

         this.lastSkipPlayersText = text;
         this.skippedNames = Set.copyOf(hashSet);
      }

      var copyOf = this.skippedNames;
      boolean booleanValue = this.ignoreFriends.isEnabled();
      return lowerCaseText -> copyOf.contains(TpaPicker.getLowerCase(lowerCaseText)) || booleanValue && FriendNameStore.containsFriendIgnoreCase(lowerCaseText);
   }

   private boolean updateConfirmDialog(Screen currentScreen, long timeMillis) {
      if (!(currentScreen instanceof DialogScreen)) {
         return false;
      } else if (currentScreen != this.activeConfirmScreen) {
         if (this.lastTpaTarget != null && timeMillis - this.lastTpaSentMs <= 8000L && TpaReplies.isTeleportToYouMessage(getScreenText(currentScreen), this.lastTpaTarget)) {
            this.activeConfirmScreen = currentScreen;
            this.confirmScreenSeenMs = timeMillis;
            this.confirmPressed = false;
            return true;
         } else {
            return false;
         }
      } else {
         if (!this.confirmPressed && timeMillis - this.confirmScreenSeenMs >= 350L && pressConfirmButton(currentScreen)) {
            this.confirmPressed = true;
            this.lastTpaTarget = null;
         }

         return true;
      }
   }

   @Override
   protected void onDisable() {
      instance = null;
      RECENT_MESSAGES.clear();
      this.tpaFlow.updateIdleState();
      this.connection = null;
      this.lastWorld = null;
      this.resetConfirmState();
      this.menuSnapshot = Snapshot.INSTANCE;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish.packet instanceof PlayerPositionLookS2CPacket) {
         this.serverTeleported = true;
      } else if (autoVanish.packet instanceof GameMessageS2CPacket gameMessageS2CPacket && gameMessageS2CPacket.overlay() && gameMessageS2CPacket.content() != null) {
         recordOverlayMessage(gameMessageS2CPacket.content().getString());
      }
   }

   private static List<String> drainRecentMessages() {
      if (RECENT_MESSAGES.isEmpty()) {
         return List.of();
      } else {
         var arrayList = new ArrayList(RECENT_MESSAGES.size());

         String concurrentQueueText;
         while ((concurrentQueueText = RECENT_MESSAGES.poll()) != null) {
            arrayList.add(concurrentQueueText);
         }

         return arrayList;
      }
   }

   private Collection<String> getNearbyPlayerNames(ClientPlayerEntity player) {
      var arrayList = new ArrayList(2);
      double profilePlayersValue = 36.0;

      for (AbstractClientPlayerEntity abstractClientPlayerEntity : mc.world.getPlayers()) {
         if (abstractClientPlayerEntity != player && !(abstractClientPlayerEntity.squaredDistanceTo(player) > profilePlayersValue)) {
            arrayList.add(abstractClientPlayerEntity.getGameProfile().name());
         }
      }

      return arrayList;
   }
}
