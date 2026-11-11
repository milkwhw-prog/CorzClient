package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.StringBase;
import com.corz.client.utils.HomeHop.Action;
import com.corz.client.utils.HomeHop.Input;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.utils.TpaFlow.Spot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.corz.client.categories.bots.AutoTpaHere;
import com.corz.client.utils.HomeHop;
import com.corz.client.utils.AutoVanish;
import com.corz.client.categories.settings.Notifications;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.RtpRegions;
import com.corz.client.categories.bots.AnchorHunter;
import com.corz.client.categories.bots.RtpBaseAlert;
import com.corz.client.utils.TpaReplies;
import com.corz.client.utils.HomeReplies;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.TpaFlow;

public class SilentHome extends Module {
   private final EnumSetting regionSetting;
   private ClientConnection connection;
   private final ConcurrentLinkedQueue<String> pendingMessages;
   private HomeHop homeHop;
   private final RtpRegions rtpRegions;
   public static final List<Class<? extends Module>> SILENT_HOME_MODULES = List.of(AnchorHunter.class, AutoTpaHere.class, RtpBaseAlert.class);
   private final BooleanSetting hideMessagesSetting;
   private volatile long suppressUntilMs;
   private final StringBase corz = new StringBase(
      "Home Name", "The home it sets where you stand and brings you back to. It replaces a home with this name, so don't use your base's home name.", "corz"
   );
   private int tickCount;
   private final AtomicInteger pendingMessageCount;
   private volatile Set<String> onlinePlayerNames;
   private long stopAtMs;

   private void sendSuppressedCommand(String chatCommandText) {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler != null && chatCommandText != null && !chatCommandText.isBlank()) {
         this.suppressUntilMs = System.currentTimeMillis() + 2000L;
         networkHandler.sendChatCommand(chatCommandText);
      }
   }

   private List<String> drainPendingMessages() {
      var arrayList = new ArrayList();

      String atomicConcurrentText;
      while ((atomicConcurrentText = this.pendingMessages.poll()) != null) {
         this.pendingMessageCount.decrementAndGet();
         arrayList.add(atomicConcurrentText);
      }

      return arrayList;
   }

   private String getConflictingModuleName() {
      for (Class silentHomeModulesItem : SILENT_HOME_MODULES) {
         Module iliIiiliiiii = ModuleRegistry.getModule(silentHomeModulesItem);
         if (iliIiiliiiii != null && iliIiiliiiii != this && iliIiiliiiii.isEnabled()) {
            return iliIiiliiiii.getCategory();
         }
      }

      return null;
   }

   private Set<String> getOnlinePlayerNames() {
      var hashSet = new HashSet();
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler == null) {
         return hashSet;
      } else {
         for (PlayerListEntry playerListEntry : networkHandler.getPlayerList()) {
            if (playerListEntry != null && playerListEntry.getProfile() != null && playerListEntry.getProfile().name() != null) {
               hashSet.add(playerListEntry.getProfile().name().toLowerCase(Locale.ROOT));
            }
         }

         return hashSet;
      }
   }

   private void notifyStatus(String silentHomeText, boolean silentHomeFlag) {
      if (silentHomeText != null) {
         if (!Notifications.isInstanceBase()) {
            if (mc.player != null) {
               mc.player.sendMessage(Text.literal("\u00a77[Silent Home]\u00a7r " + silentHomeText), false);
            }
         } else {
            DisabledEnabled.getInstance().handleDisabledEnabled("Silent Home", silentHomeText, silentHomeFlag);
         }
      }
   }

   private void closeContainerScreen() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.currentScreen instanceof HandledScreen && player.currentScreenHandler != player.playerScreenHandler) {
         player.closeHandledScreen();
      }
   }

   private void queueTextFromPacket(Packet<?> packet) {
      if (packet instanceof GameMessageS2CPacket gameMessageS2CPacket) {
         if (gameMessageS2CPacket.content() != null) {
            this.queueMessage(gameMessageS2CPacket.content().getString());
         }
      } else if (packet instanceof OverlayMessageS2CPacket overlayMessageS2CPacket) {
         if (overlayMessageS2CPacket.text() != null) {
            this.queueMessage(overlayMessageS2CPacket.text().getString());
         }
      } else if (packet instanceof TitleS2CPacket titleS2CPacket) {
         if (titleS2CPacket.text() != null) {
            this.queueMessage(titleS2CPacket.text().getString());
         }
      } else if (packet instanceof SubtitleS2CPacket subtitleS2CPacket && subtitleS2CPacket.text() != null) {
         this.queueMessage(subtitleS2CPacket.text().getString());
      }
   }

   private void stopHomeHop(String homeHopText, boolean homeHopFlag) {
      this.homeHop = null;
      this.stopAtMs = 0L;
      if (this.isEnabled()) {
         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            this.setEnabled(false);
         } catch (Exception e) {
            this.homeHop = null;
         }
      }

      if (homeHopText != null) {
         this.notifyStatus(homeHopText, homeHopFlag);
      }
   }

   public SilentHome() {
      super("Silent Home", IconsPngType.DONUTSMP);
      this.regionSetting = new EnumSetting(
         "Region", "Which /rtp region to use. Random picks a different one each time.", "Random", RtpRegions.RANDOM_REGION_ITEMS.toArray(new String[0])
      );
      this.hideMessagesSetting = new BooleanSetting("Hide Messages", "Hide the chat lines that /sethome, /rtp and /home print while it runs.", true);
      this.pendingMessages = new ConcurrentLinkedQueue<>();
      this.pendingMessageCount = new AtomicInteger();
      this.rtpRegions = new RtpRegions();
      this.onlinePlayerNames = Set.of();
      this.addSettings(new ConfigSetting[]{this.corz, this.regionSetting, this.hideMessagesSetting});
   }

   @Override
   protected void onDisable() {
      this.homeHop = null;
      this.connection = null;
      this.suppressUntilMs = 0L;
      this.pendingMessages.clear();
      this.pendingMessageCount.set(0);
   }

   private void queueMessage(String atomicIntegerText) {
      if (atomicIntegerText != null && !atomicIntegerText.isBlank() && this.pendingMessageCount.get() < 256) {
         this.pendingMessages.offer(atomicIntegerText);
         this.pendingMessageCount.incrementAndGet();
      }
   }

   @Override
   protected void onEnable() {
      this.pendingMessages.clear();
      this.pendingMessageCount.set(0);
      this.homeHop = null;
      this.connection = null;
      this.stopAtMs = 0L;
      this.tickCount = 0;
      if (mc != null) {
         if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
            String formatText = this.getConflictingModuleName();
            if (formatText != null) {
               mc.send(() -> this.stopHomeHop("Turn off " + formatText + " first.", false));
            } else {
               String silentHomeText = this.corz.getCurrentText() == null ? "" : this.corz.getCurrentText().trim();
               if (!HomeHop.isValidHomeName(silentHomeText)) {
                  mc.send(() -> this.stopHomeHop("Home Name can only use letters, numbers, - and _.", false));
               } else {
                  this.homeHop = new HomeHop(silentHomeText, this.regionSetting.getCurrentOption(), this.rtpRegions);
                  this.connection = mc.getNetworkHandler().getConnection();
                  this.onlinePlayerNames = this.getOnlinePlayerNames();
                  this.suppressUntilMs = System.currentTimeMillis() + 2000L;
               }
            }
         } else {
            mc.send(() -> this.stopHomeHop(null, false));
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
         long currentMillis = System.currentTimeMillis();
         if (this.homeHop == null) {
            if (this.stopAtMs > 0L && currentMillis >= this.stopAtMs) {
               this.stopHomeHop(null, false);
            }
         } else if (mc.getNetworkHandler().getConnection() != this.connection) {
            this.stopHomeHop("You left the server - Silent Home stopped.", false);
         } else {
            this.tickCount++;
            if (this.tickCount % 20 == 0) {
               this.onlinePlayerNames = this.getOnlinePlayerNames();
            }

            ClientPlayerEntity player = mc.player;
            var atomicConcurrent = this.drainPendingMessages();
            Spot spot = new Spot(mc.world.getRegistryKey().getValue().toString(), player.getX(), player.getY(), player.getZ());
            boolean iIiFlag = mc.currentScreen instanceof HandledScreen && player.currentScreenHandler != player.playerScreenHandler;
            boolean iIiFlag2 = !player.isAlive() || mc.currentScreen instanceof DeathScreen;
            var homeHideSet = this.onlinePlayerNames;
            Action homeStopped = this.homeHop
               .advance(
                  new Input(currentMillis, spot, player.isOnGround() || player.isTouchingWater(), iIiFlag, iIiFlag2, atomicConcurrent, names -> homeHideSet.contains(names.toLowerCase(Locale.ROOT)))
               );
            if (this.homeHop.isActive(currentMillis)) {
               this.suppressUntilMs = currentMillis + 2000L;
            }

            switch (homeStopped.kind()) {
               case COMMAND:
                  this.sendSuppressedCommand(homeStopped.text());
                  break;
               case PICK_REGION:
                  this.clickSlotMatchingName(homeStopped.text());
                  break;
               case FAIL_MENU:
                  this.closeContainerScreen();
                  this.stopHomeHop(homeStopped.text(), false);
                  break;
               case FAIL:
                  this.stopHomeHop(homeStopped.text(), false);
                  break;
               case DONE:
                  this.homeHop = null;
                  this.stopAtMs = currentMillis + 2000L;
                  this.notifyStatus(homeStopped.text(), true);
            }
         }
      }
   }

   private void clickSlotMatchingName(String rowsSlotText) {
      ClientPlayerEntity player = mc.player;
      if (player != null && rowsSlotText != null && player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandle) {
         String lowerCase = rowsSlotText.toLowerCase(Locale.ROOT);
         String replace = lowerCase.replace(" ", "");
         int rowsScaled = genericContainerScreenHandle.getRows() * 9;

         for (int i = 0; i < rowsScaled && i < genericContainerScreenHandle.slots.size(); i++) {
            ItemStack stack = genericContainerScreenHandle.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               String normalizeSmallCaps = TpaReplies.normalizeSmallCaps(stack.getName().getString());
               if (normalizeSmallCaps.contains(lowerCase) || normalizeSmallCaps.replace(" ", "").replace("-", "").contains(replace)) {
                  int syncId = genericContainerScreenHandle.syncId;
                  int rowsSlotValue = i;
                  ExtUtils.handleExt(
                     () -> {
                        if (mc.player != null
                           && mc.interactionManager != null
                           && mc.player.currentScreenHandler.syncId == syncId) {
                           mc.interactionManager.clickSlot(syncId, rowsSlotValue, 0, SlotActionType.PICKUP, mc.player);
                        }
                     },
                     false
                  );
                  return;
               }
            }
         }
      }
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish != null && autoVanish.packet != null) {
         if (!(autoVanish.packet instanceof BundlePacket bundlePacket)) {
            if (autoVanish.packet instanceof GameMessageS2CPacket gameMessageS2CPacket && gameMessageS2CPacket.content() != null) {
               String iiIText = gameMessageS2CPacket.content().getString();
               this.queueMessage(iiIText);
               if (!gameMessageS2CPacket.overlay() && this.hideMessagesSetting.isEnabled() && System.currentTimeMillis() <= this.suppressUntilMs) {
                  var homeHideSet = this.onlinePlayerNames;
                  if (HomeReplies.isHomeRelatedMessage(iiIText, iiIText2 -> homeHideSet.contains(iiIText2.toLowerCase(Locale.ROOT)))) {
                     autoVanish.updateFlag();
                  }
               }
            } else {
               this.queueTextFromPacket(autoVanish.packet);
            }
         } else {
            for (Packet packet : (Iterable<net.minecraft.network.packet.Packet>) (bundlePacket.getPackets())) {
               this.queueTextFromPacket(packet);
            }
         }
      }
   }
}
