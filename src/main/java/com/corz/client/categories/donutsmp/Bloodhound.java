package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.TickProgress;
import com.corz.client.mixins.AccessorClientPlayNetworkHandler;
import com.corz.client.render.QueueCommand;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.corz.client.utils.BlockSensor;
import com.corz.client.utils.BloodhoundCard;
import com.corz.client.utils.BloodhoundRender;
import com.corz.client.utils.EntitySensor;
import com.corz.client.utils.HuntDriver;
import com.corz.client.utils.PacketSensor;
import com.corz.client.utils.Places;
import com.corz.client.utils.Sense;
import com.corz.client.utils.Site;
import com.corz.client.utils.SiteBook;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.PingSoundPlayer;
import com.corz.client.utils.RtpRegions;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.SwordBot;

public final class Bloodhound extends Module {
   private Object lastWorld;
   private int lastHuntModeIndex;
   private final EntitySensor entitySensor;
   private SiteBook currentSiteBook;
   private final HuntDriver huntDriver;
   private Object lastConnection;
   private final BooleanSetting stopWhenFoundSetting;
   private final PacketSensor packetSensor;
   private final KeybindSetting flyToBestKeybind;
   private int baseCount;
   private String currentServerKey;
   private final EnumSetting rtpRegionSetting;
   private int bestBaseConfidence;
   private final BlockSensor blockSensor;
   private final BooleanSetting showStatusCard;
   private final BloodhoundRender bloodhoundRender;
   private volatile List<Site> topSites;
   private final BooleanSetting showTracers;
   private String statusCardText;
   private final BooleanSetting showPins;
   private boolean awaitingFirstServerKey;
   private long lastSaveRequestMillis;
   private Tone statusTone;
   private final IntegerSetting sensitivitySetting = new IntegerSetting("Sensitivity", "Higher also shows small stashes. Lower shows only big, sure bases.", 6, 1, 8);
   private int tickCount;
   private String huntStatusText;
   private final EnumSetting findModeSetting = new EnumSetting(
      "Find",
      "Underground finds only bases hidden below deepslate level. Everywhere also shows higher and surface bases.",
      "Underground",
      "Underground",
      "Everywhere"
   );
   public static volatile Bloodhound singleton;
   private final ButtonBase clearFindsButton;
   static final String CHAT_PREFIX = "\u00a77[\u00a7cBloodhound\u00a77]\u00a7r ";
   static final String IDLE_STATUS_TEXT = "Sniffing for bases";
   private final BloodhoundCard bloodhoundCard;
   static final String JSON_BASES_KEY = "bases4";
   private final Map<String, SiteBook> siteBooksByServer;
   private final EnumSetting alertModeSetting;
   private long statusSignature;
   private final EnumSetting huntModeSetting;
   private String lastDirectionText;
   private boolean savePending;

   private void announceNewBases(long absentPlayerValue) {
      if (this.alertModeSetting.getCurrentIndex() != 2) {
         int sureBaseThreshold = this.getSureBaseThreshold();
         boolean absentPlayerFlag = false;

         for (Site site : this.currentSiteBook.getFlag(mc.player.getX(), mc.player.getZ(), sureBaseThreshold, 320)) {
            if (site.getMax() >= sureBaseThreshold && !site.isRememberedActive() && site.canTime(absentPlayerValue, 1500L)) {
               site.updateFlag();
               if (!this.currentSiteBook.canFlag(site.getJoinAmount(), site.getClueJoinAmount())) {
                  this.currentSiteBook.handle((int)Math.round(site.getJoinAmount()), (int)Math.round(site.getClueJoinAmount()));
                  ModuleConfigAutosave.updateFlag2();
                  double absentPlayerFactor = site.getJoinAmount() - mc.player.getX();
                  double x = site.getClueJoinAmount() - mc.player.getZ();
                  String absentPlayerText = site.getCopyOfClueJoin().isEmpty() ? "" : " \u00b7 " + String.join(", ", site.getCopyOfClueJoin());
                  this.sendChatMessage(
                     "Base \u00a7a"
                        + site.getConfidenceScore()
                        + "%\u00a7r \u00b7 "
                        + Places.getFormat(Math.sqrt(absentPlayerFactor * absentPlayerFactor + x * x))
                        + " "
                        + Places.formatTexts(absentPlayerFactor, x)
                        + " \u00b7 \u00a7f"
                        + Places.formatText(site.getJoinAmount(), site.getAmount(), site.getClueJoinAmount())
                        + "\u00a7r"
                        + absentPlayerText
                  );
                  if (this.alertModeSetting.getCurrentIndex() == 0 && !absentPlayerFlag) {
                     PingSoundPlayer.playPingSound();
                     absentPlayerFlag = true;
                  }
               }
            }
         }
      } else {
         for (Site getFlagItem : this.currentSiteBook.getFlag(mc.player.getX(), mc.player.getZ(), this.getSureBaseThreshold(), 320)) {
            getFlagItem.updateFlag();
         }
      }
   }

   public List<Site> getTopSites() {
      return this.topSites;
   }

   @Override
   protected void onEnable() {
      this.blockSensor.updateHashClear();
      this.packetSensor.updateAtomicConcurrent();
      this.bloodhoundCard.updateSmoothTime();
      this.tickCount = 0;
      this.currentServerKey = null;
      this.lastHuntModeIndex = 0;
      this.awaitingFirstServerKey = mc != null && mc.world != null;
      this.lastConnection = null;
      this.entitySensor.update();
      this.statusCardText = "Sniffing for bases";
      this.huntStatusText = "";
      this.lastDirectionText = null;
      this.statusTone = Tone.IDLE;
      this.baseCount = 0;
      this.bestBaseConfidence = 0;
      this.statusSignature = Long.MIN_VALUE;
   }

   public int getSureBaseThreshold() {
      return 55;
   }

   public void renderStatusCard(DrawContext context) {
      if (context != null && mc.player != null && !mc.options.hudHidden && this.showStatusCard.isEnabled()) {
         this.bloodhoundCard
            .renderScaledWidth(
               context, this.statusTone, this.statusCardText, this.baseCount, this.bestBaseConfidence, this.blockSensor.getSensorValue(), null
            );
      }
   }

   private void syncHuntMode(long huntStoppedValue) {
      int currentIndex = this.huntModeSetting.getCurrentIndex();
      if (currentIndex != this.lastHuntModeIndex) {
         this.lastHuntModeIndex = currentIndex;
         if (currentIndex == 0) {
            this.huntDriver.handleModeSite("Hunt stopped.");
         } else {
            this.huntDriver.handleElytraRtp(HuntDriver.Mode.values()[currentIndex], mc, this.getScanChunkRadius(), huntStoppedValue);
         }
      }

      this.huntDriver.handleHealthHunt(mc, huntStoppedValue);
      if (this.lastHuntModeIndex != 0 && this.huntDriver.getMode() == HuntDriver.Mode.OFF) {
         this.lastHuntModeIndex = 0;
         this.huntModeSetting.handle("Off");
      }
   }

   @Override
   protected void onDisable() {
      this.huntDriver.handleModeSite(this.huntDriver.getMode() == HuntDriver.Mode.OFF ? null : "Hunt stopped.");
      this.lastHuntModeIndex = 0;
      if (this.huntModeSetting.getCurrentIndex() != 0) {
         this.huntModeSetting.handle("Off");
      }

      this.packetSensor.updateAtomicConcurrent();
      this.topSites = List.of();
      if (this.savePending) {
         this.savePending = false;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public int getMinDisplayConfidence() {
      return 40;
   }

   private void clearAllFinds() {
      this.currentSiteBook.update();
      ModuleConfigAutosave.updateFlag2();
      this.blockSensor.updateHashClear();
      this.topSites = List.of();
      this.sendChatMessage("Forgot every find in this world.");
   }

   private void updateSavePending(long timeMillis) {
      for (Site site : this.currentSiteBook.getValues()) {
         if (site.canFlagMax(40)) {
            this.savePending = true;
         }
      }

      if (this.savePending && timeMillis - this.lastSaveRequestMillis >= 30000L) {
         this.savePending = false;
         this.lastSaveRequestMillis = timeMillis;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public static void onBlockStateChanged(BlockPos pos2, BlockState state, BlockState blockState) {
      Bloodhound singleton2 = singleton;
      if (singleton2 != null && singleton2.isEnabled()) {
         singleton2.blockSensor.onBlockChanged(pos2, state, blockState);
      }
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      this.packetSensor.handleEventIds(autoVanish.packet);
      if (autoVanish.packet instanceof GameMessageS2CPacket gameMessageS2CPacket && gameMessageS2CPacket.content() != null) {
         this.huntDriver.handleConcurrentQueue(gameMessageS2CPacket.content().getString());
      } else if (autoVanish.packet instanceof OverlayMessageS2CPacket overlayMessageS2CPacket && overlayMessageS2CPacket.text() != null) {
         this.huntDriver.handleConcurrentQueue(overlayMessageS2CPacket.text().getString());
      }
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      if (swordBot.getValue() == 1 && mc.player != null && mc.currentScreen == null) {
         if (this.flyToBestKeybind.isBoundTo(swordBot.getKey())) {
            var forgotSniffingItems = this.topSites;
            if (forgotSniffingItems.isEmpty()) {
               this.sendChatMessage("No base to fly to yet.");
            } else {
               if (this.huntDriver.getMode() != HuntDriver.Mode.OFF) {
                  this.huntDriver.handleModeSite(null);
                  this.lastHuntModeIndex = 0;
                  this.huntModeSetting.handle("Off");
                  this.sendChatMessage("Hunt stopped.");
               }

               Site site = (Site)forgotSniffingItems.get(0);
               if (HuntDriver.canDestinationPlayer(mc, site.getJoinAmount(), site.getClueJoinAmount())) {
                  this.sendChatMessage(
                     "Flying to the best base ("
                        + site.getConfidenceScore()
                        + "%) at "
                        + Places.formatText(site.getJoinAmount(), site.getAmount(), site.getClueJoinAmount())
                        + "."
                  );
               } else {
                  this.sendChatMessage("The Elytra Pilot could not start. You need an elytra and rockets.");
               }
            }
         }
      }
   }

   private String getLocalServerKey() {
      try {
         if (mc.getServer() != null) {
            return "local:" + mc.getServer().getSaveProperties().getLevelName();
         }
      } catch (Throwable e) {
      }

      return "local";
   }

   @Override
   public void loadExtraData(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         if (asJsonObject.has("bases4") && asJsonObject.get("bases4").isJsonObject()) {
            long currentMillis = System.currentTimeMillis();
            var arrayList = new ArrayList();

            for (Entry entry : asJsonObject.getAsJsonObject("bases4").entrySet()) {
               if (((JsonElement)entry.getValue()).isJsonArray()) {
                  arrayList.add(entry);
                  if (arrayList.size() >= 24) {
                     break;
                  }
               }
            }

            Collections.reverse(arrayList);

            for (Entry entry2 : (Iterable<java.util.Map.Entry>) (arrayList)) {
               SiteBook ifAbsent = this.siteBooksByServer.computeIfAbsent((String)entry2.getKey(), iIiText -> new SiteBook());
               int iIiCounter = 0;

               for (JsonElement jsonElement : ((JsonElement)entry2.getValue()).getAsJsonArray()) {
                  if (iIiCounter++ >= 96) {
                     break;
                  }

                  try {
                     JsonArray asJsonArray = jsonElement.getAsJsonArray();
                     String iIiText2 = asJsonArray.size() > 4 ? asJsonArray.get(4).getAsString() : "";
                     ifAbsent.handleMinRemembered(
                        asJsonArray.get(0).getAsInt(),
                        asJsonArray.get(1).getAsInt(),
                        asJsonArray.get(2).getAsInt(),
                        asJsonArray.get(3).getAsInt(),
                        iIiText2.isBlank() ? List.of() : List.of(iIiText2.split(Pattern.quote("|"))),
                        asJsonArray.size() > 5 ? asJsonArray.get(5).getAsLong() : currentMillis,
                        currentMillis
                     );
                  } catch (Throwable e) {
                  }
               }

               ifAbsent.handleTimeFlag(currentMillis, 0.0, 0.0);
            }
         }
      }
   }

   @Override
   public JsonElement saveExtraData() {
      JsonObject jsonObject = new JsonObject();
      int iIiCounter = 0;
      var arrayList = new ArrayList<>(this.siteBooksByServer.entrySet());
      Collections.reverse(arrayList);
      arrayList.sort((entry, entry2) -> Boolean.compare(!((String)entry.getKey()).equals(this.currentServerKey), !((String)entry2.getKey()).equals(this.currentServerKey)));

      for (Entry arrayItem : arrayList) {
         JsonArray jsonArray = new JsonArray();

         for (Site site : ((SiteBook)arrayItem.getValue()).getItems(40, 96)) {
            int iIiLimit = Math.min(site.getConfidenceScore(), Math.max(site.getClueJoinValue(), site.getValue(Sense.STRUCTURE)));
            if (iIiLimit >= 40) {
               JsonArray jsonArray2 = new JsonArray();
               jsonArray2.add((int)Math.round(site.getJoinAmount()));
               jsonArray2.add((int)Math.round(site.getAmount()));
               jsonArray2.add((int)Math.round(site.getClueJoinAmount()));
               jsonArray2.add(iIiLimit);
               jsonArray2.add(String.join("|", site.getCopyOf()));
               jsonArray2.add(site.getMin());
               jsonArray.add(jsonArray2);
            }
         }

         if (!jsonArray.isEmpty()) {
            if (++iIiCounter > 24) {
               break;
            }

            jsonObject.add((String)arrayItem.getKey(), jsonArray);
         }
      }

      if (jsonObject.isEmpty()) {
         return null;
      } else {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.add("bases4", jsonObject);
         return jsonObject2;
      }
   }

   public Bloodhound() {
      super("Bloodhound", IconsPngType.DONUTSMP);
      this.showPins = new BooleanSetting("Show Pins", "Glowing beams and labels on every base it finds.", true);
      this.showTracers = new BooleanSetting("Show Tracers", "Lines from your crosshair to the 3 best bases.", true);
      this.showStatusCard = new BooleanSetting("Status Card", "A small card at the top of the screen with the best find.", true);
      this.alertModeSetting = new EnumSetting("Alerts", "Tell you in chat (and with a ping) when it finds a new base.", "Chat + Sound", "Chat + Sound", "Chat", "Off");
      this.huntModeSetting = new EnumSetting(
         "Hunt", "Let it search by itself. RTP jumps around with /rtp, Elytra flies a growing spiral with the Elytra Pilot.", "Off", "Off", "RTP", "Elytra"
      );
      this.rtpRegionSetting = new EnumSetting(
         "RTP Region", "Which /rtp region the RTP hunt uses. Random switches between them.", "Random", RtpRegions.RANDOM_REGION_ITEMS.toArray(new String[0])
      );
      this.stopWhenFoundSetting = new BooleanSetting("Stop When Found", "Stop hunting when it finds a sure base. The elytra hunt then flies you to it.", true);
      this.flyToBestKeybind = new KeybindSetting("Fly To Best", "Press to fly to the best base with the Elytra Pilot.");
      this.clearFindsButton = new ButtonBase("Clear Finds", "Forget every base found so far in this world.", "Clear", this::clearAllFinds);
      this.blockSensor = new BlockSensor();
      this.entitySensor = new EntitySensor();
      this.packetSensor = new PacketSensor();
      this.bloodhoundRender = new BloodhoundRender();
      this.bloodhoundCard = new BloodhoundCard();
      this.siteBooksByServer = new LinkedHashMap<>(16, 0.75F, true);
      this.currentSiteBook = new SiteBook();
      this.topSites = List.of();
      this.statusCardText = "Sniffing for bases";
      this.huntStatusText = "";
      this.statusTone = Tone.IDLE;
      this.statusSignature = Long.MIN_VALUE;
      this.huntDriver = new HuntDriver(new HuntDriver.Host() {
         @Override
         public boolean shouldStopWhenFound() {
            return Bloodhound.this.stopWhenFoundSetting.isEnabled();
         }

         @Override
         public SiteBook getSiteBook() {
            return Bloodhound.this.currentSiteBook;
         }

         @Override
         public void sendChatMessage(String bloodhoundText) {
            Bloodhound.this.sendChatMessage(bloodhoundText);
         }

         @Override
         public String getRtpRegionName() {
            return Bloodhound.this.rtpRegionSetting.getCurrentOption();
         }

         @Override
         public boolean isBlockSensorSettled(long sensorBloodhoundValue) {
            return Bloodhound.this.blockSensor.isScanDue(sensorBloodhoundValue);
         }

         @Override
         public int getSureBaseThreshold() {
            return Bloodhound.this.getSureBaseThreshold();
         }

         @Override
         public int getBlockSensorCount() {
            return Bloodhound.this.blockSensor.getMarkerThreadCount();
         }
      });
      this.addSettings(
         new ConfigSetting[]{
            this.sensitivitySetting,
            this.findModeSetting,
            this.showPins,
            this.showTracers,
            this.showStatusCard,
            this.alertModeSetting,
            this.huntModeSetting,
            this.rtpRegionSetting,
            this.stopWhenFoundSetting,
            this.flyToBestKeybind,
            this.clearFindsButton
         }
      );
      singleton = this;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.showPins.isEnabled() || this.showTracers.isEnabled()) {
         this.bloodhoundRender.handleCameraClamp(this.topSites, this.showPins.isEnabled(), this.showTracers.isEnabled());
      }
   }

   boolean isUndergroundOnly() {
      return this.findModeSetting.getCurrentIndex() == 0;
   }

   private int getScanChunkRadius() {
      int clampedViewDistance = mc.options.getClampedViewDistance();

      try {
         if (mc.getNetworkHandler() instanceof AccessorClientPlayNetworkHandler accessorClientPlayNetworkHan && accessorClientPlayNetworkHan.getChunkLoadDistance() > 0) {
            clampedViewDistance = accessorClientPlayNetworkHan.getChunkLoadDistance();
         }
      } catch (Throwable e) {
      }

      return Math.max(2, Math.min(16, clampedViewDistance));
   }

   public SiteBook getCurrentSiteBook() {
      return this.currentSiteBook;
   }

   private void sendChatMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal("\u00a77[\u00a7cBloodhound\u00a77]\u00a7r " + messageLiteralText), false);
      }
   }

   @Override
   protected void onRender2D(TickProgress tickProgress) {
      if (mc.player != null && !mc.options.hudHidden) {
         if (this.showPins.isEnabled()) {
            this.bloodhoundRender.renderScaledWindow(tickProgress.drawContext, this.topSites);
         }

         if (mc.currentScreen == null) {
            this.renderStatusCard(tickProgress.drawContext);
         }
      }
   }

   private String getServerWorldKey() {
      ServerInfo currentServerEntry = mc.getCurrentServerEntry();
      String registryServerText = currentServerEntry != null ? currentServerEntry.address : this.getLocalServerKey();
      return registryServerText + "|" + mc.world.getRegistryKey().getValue();
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         ClientConnection connection = mc.getNetworkHandler() == null ? null : mc.getNetworkHandler().getConnection();
         boolean iIiFlag = this.lastConnection != null && connection != this.lastConnection;
         this.lastConnection = connection;
         boolean iIiFlag2 = this.lastWorld != null && mc.world != this.lastWorld;
         this.lastWorld = mc.world;
         String sniffingBasesText = this.getServerWorldKey();
         if (!sniffingBasesText.equals(this.currentServerKey) || iIiFlag) {
            boolean noSniffingbasestext = this.currentServerKey == null;
            this.currentServerKey = sniffingBasesText;
            if (!noSniffingbasestext || !this.awaitingFirstServerKey || iIiFlag) {
               this.huntDriver.handleModeSite(null);
               this.lastHuntModeIndex = 0;
               if (this.huntModeSetting.getCurrentIndex() != 0) {
                  this.huntModeSetting.handle("Off");
               }
            }

            this.awaitingFirstServerKey = false;
            this.currentSiteBook = this.siteBooksByServer.computeIfAbsent(sniffingBasesText, iIiText -> new SiteBook());
            this.blockSensor.updateHashClear();
            this.packetSensor.updateAtomicConcurrent();
            this.entitySensor.update();
            this.topSites = List.of();
         } else if (iIiFlag2) {
            this.blockSensor.updateHashClear();
            this.packetSensor.updateAtomicConcurrent();
            this.entitySensor.update();
         }

         long currentMillis = System.currentTimeMillis();
         int iIiValue = mc.player.getBlockX() >> 4;
         int iIiValue2 = mc.player.getBlockZ() >> 4;
         this.blockSensor
            .configureScan(mc.world, iIiValue, iIiValue2, this.getScanChunkRadius(), this.currentSiteBook, this.sensitivitySetting.getValue(), this.isUndergroundOnly(), currentMillis);
         this.packetSensor.handleProfilePlayers(mc.world, mc.player, this.currentSiteBook, currentMillis);
         this.tickCount++;
         this.syncHuntMode(currentMillis);
         if (this.tickCount % 20 == 0) {
            this.entitySensor
               .handleFarmLeashed(
                  mc.world, mc.player, this.currentSiteBook, mc.world.getRegistryKey() == World.END, this.isUndergroundOnly(), currentMillis
               );
         }

         if (this.tickCount % 10 == 0) {
            this.currentSiteBook.handleTimeFlag(currentMillis, mc.player.getX(), mc.player.getZ());
            this.topSites = this.currentSiteBook
               .getFlagAmount(
                  mc.player.getX(),
                  mc.player.getZ(),
                  this.getMinDisplayConfidence(),
                  8,
                  Math.max(1024.0, this.getScanChunkRadius() * 32.0),
                  2,
                  this.isUndergroundOnly() && mc.world.getRegistryKey() == World.OVERWORLD ? 0 : Integer.MAX_VALUE
               );
            this.announceNewBases(currentMillis);
            this.updateSavePending(currentMillis);
         }

         if (this.showStatusCard.isEnabled()) {
            this.updateStatusCardText();
         }
      }
   }

   private void updateStatusCardText() {
      var forgotSniffingItems = this.topSites;
      Site site = forgotSniffingItems.isEmpty() ? null : (Site)forgotSniffingItems.get(0);
      this.baseCount = forgotSniffingItems.size();
      this.bestBaseConfidence = site == null ? 0 : site.getConfidenceScore();
      double sniffingBasesFactor = site == null ? 0.0 : site.getJoinAmount() - mc.player.getX();
      double x = site == null ? 0.0 : site.getClueJoinAmount() - mc.player.getZ();
      double distance = Math.sqrt(sniffingBasesFactor * sniffingBasesFactor + x * x);
      boolean sniffingBasesFlag = site != null && site.getMax() >= this.getSureBaseThreshold();
      String sniffingBasesText = this.huntDriver.getTurningLandText();
      if (!this.huntDriver.isPhaseDone() || sniffingBasesText.isEmpty() || sniffingBasesFlag && distance <= 320.0) {
         this.huntStatusText = "";
         if (site == null) {
            this.statusCardText = "Sniffing for bases";
            this.statusSignature = Long.MIN_VALUE;
            this.statusTone = Tone.IDLE;
         } else {
            String formatTexts = Places.formatTexts(sniffingBasesFactor, x);
            long sniffingBasesTime = (long)site.getConfidenceScore() << 32 | (distance < 1000.0 ? Math.round(distance) : 1000000L + Math.round(distance / 100.0));
            if (sniffingBasesTime != this.statusSignature || !formatTexts.equals(this.lastDirectionText)) {
               this.statusSignature = sniffingBasesTime;
               this.lastDirectionText = formatTexts;
               this.statusCardText = "Base " + site.getConfidenceScore() + "% \u00b7 " + Places.getFormat(distance) + " " + formatTexts;
            }

            this.statusTone = sniffingBasesFlag ? Tone.ACTIVE : Tone.PAUSED;
         }
      } else {
         if (!sniffingBasesText.equals(this.huntStatusText)) {
            this.huntStatusText = sniffingBasesText;
            this.statusCardText = "Hunting \u00b7 " + sniffingBasesText;
         }

         this.statusSignature = Long.MIN_VALUE;
         this.statusTone = Tone.ACTIVE;
      }
   }
}
