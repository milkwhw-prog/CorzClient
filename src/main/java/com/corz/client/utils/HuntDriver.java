package com.corz.client.utils;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.config.StringBase;
import com.corz.client.utils.RtpReplies.Reply;
import com.corz.client.gui.DisabledEnabled;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.utils.RtpClock;
import com.corz.client.utils.RtpRegions;
import com.corz.client.utils.RtpReplies;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.Site;
import com.corz.client.utils.Places;
import com.corz.client.utils.SiteBook;

public final class HuntDriver {
   private static final int[][] stoppedLandedValues = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
   private double z;
   private final List<double[]> eastItems;
   private double eastNetworkAmount;
   private long eastHuntTime;
   private long nextRtpAnswerTimeMs;
   private double rtpStartZ;
   private HuntDriver.Phase phase;
   private ClientWorld world;
   private final RtpClock rtpClock = new RtpClock();
   private HuntDriver.Mode mode;
   private int stoppedPilotValue;
   private final HuntDriver.Host host;
   private final ConcurrentLinkedQueue<String> concurrentLinkedQueue;
   private double x;
   private int sniffSpotCount;
   private Site siteRtpAnswer;
   private long spiralSpacingBlocks;
   private long answerTimesTime;
   private double rtpStartX;
   private int turningLegsCount;
   private String east;
   private final RtpRegions rtpRegions = new RtpRegions();
   private long answerTimesTimeRtpAnswer;
   private String turningLandText;
   private double modeSiteAmount;
   private int answerTimesValue;

   private boolean canEast(Site site) {
      double eastLimit = 576.0;

      for (double[] eastValues : this.eastItems) {
         double eastFactor = eastValues[0] - site.getJoinAmount();
         double eastFactor2 = eastValues[1] - site.getClueJoinAmount();
         if (eastFactor * eastFactor + eastFactor2 * eastFactor2 <= eastLimit) {
            return true;
         }
      }

      return false;
   }

   private static AutoElytraPilot getAutoElytraPilot() {
      return ModuleRegistry.getModule(AutoElytraPilot.class) instanceof AutoElytraPilot autoElytraPilot ? autoElytraPilot : null;
   }

   private void handleElytraSky(MinecraftClient client, Site site) {
      String formatText = Places.formatText(site.getJoinAmount(), site.getAmount(), site.getClueJoinAmount());
      if (this.mode == HuntDriver.Mode.ELYTRA) {
         boolean destinationPlayer = canDestinationPlayer(client, site.getJoinAmount(), site.getClueJoinAmount());
         this.handleSiteTurning(
            "Found a base ("
               + site.getConfidenceScore()
               + "%) at "
               + formatText
               + (destinationPlayer ? ". Flying you there." : ". The Elytra Pilot could not take off (it needs an elytra, rockets and open sky), so the hunt stopped here.")
         );
      } else {
         this.handleSiteTurning("Found a base (" + site.getConfidenceScore() + "%) at " + formatText + ". Hunt stopped here.");
      }
   }

   private static Set<String> getNetworkHandler(MinecraftClient client) {
      var hashSet = new HashSet();
      if (client.getNetworkHandler() == null) {
         return hashSet;
      } else {
         for (PlayerListEntry playerListEntry : client.getNetworkHandler().getPlayerList()) {
            hashSet.add(playerListEntry.getProfile().name().toLowerCase(Locale.ROOT));
         }

         return hashSet;
      }
   }

   public void handleConcurrentQueue(String concurrentQueueText) {
      if (this.mode == HuntDriver.Mode.RTP && concurrentQueueText != null && this.concurrentLinkedQueue.size() < 64) {
         this.concurrentLinkedQueue.offer(concurrentQueueText);
      }
   }

   private boolean canStoppedPilot(MinecraftClient client) {
      int[] stoppedPilotValues = stoppedLandedValues[this.stoppedPilotValue % 4];
      long stoppedPilotValue = this.spiralSpacingBlocks * (this.stoppedPilotValue / 2 + 1);
      return canDestinationPlayer(client, this.rtpStartX + stoppedPilotValues[0] * (stoppedPilotValue + 6000.0), this.rtpStartZ + stoppedPilotValues[1] * (stoppedPilotValue + 6000.0));
   }

   private void handleSiteTurning(String siteTurningText) {
      this.mode = HuntDriver.Mode.OFF;
      this.phase = HuntDriver.Phase.DONE;
      this.turningLandText = "";
      this.siteRtpAnswer = null;
      this.host.sendChatMessage(siteTurningText);
   }

   public boolean isPhaseDone() {
      return this.mode != HuntDriver.Mode.OFF && this.phase != HuntDriver.Phase.DONE && this.phase != HuntDriver.Phase.IDLE;
   }

   private void handleStoppedTurning(MinecraftClient client, ClientPlayerEntity player) {
      AutoElytraPilot autoElytraPilot = getAutoElytraPilot();
      if (autoElytraPilot != null && autoElytraPilot.isEnabled()) {
         int[] stoppedTurningValues = stoppedLandedValues[this.stoppedPilotValue % 4];
         long stoppedTurningFactor = this.spiralSpacingBlocks * (this.stoppedPilotValue / 2 + 1);
         double stoppedTurningValue = (player.getX() - this.rtpStartX) * stoppedTurningValues[0] + (player.getZ() - this.rtpStartZ) * stoppedTurningValues[1];
         this.turningLandText = "Flying leg " + (this.stoppedPilotValue + 1) + " \u00b7 " + Places.getFormat(Math.max(0.0, stoppedTurningFactor - stoppedTurningValue)) + " to turn";
         if (!(stoppedTurningValue < stoppedTurningFactor)) {
            if (this.turningLegsCount + 1 >= 40) {
               double angle = Math.toRadians(player.getYaw());
               canDestinationPlayer(client, player.getX() - Math.sin(angle) * 200.0, player.getZ() + Math.cos(angle) * 200.0);
               this.handleSiteTurning("Hunt finished 40 legs without a sure base. Landing.");
            } else {
               double stoppedTurningValue2 = this.rtpStartX + stoppedTurningValues[0] * stoppedTurningFactor;
               double stoppedTurningValue3 = this.rtpStartZ + stoppedTurningValues[1] * stoppedTurningFactor;
               int stoppedPilotValue = this.stoppedPilotValue + 1;
               int[] stoppedTurningValues2 = stoppedLandedValues[stoppedPilotValue % 4];
               long stoppedTurningValue4 = this.spiralSpacingBlocks * (stoppedPilotValue / 2 + 1);
               if (!autoElytraPilot.changeDestination((int)Math.round(stoppedTurningValue2 + stoppedTurningValues2[0] * (stoppedTurningValue4 + 6000.0)), (int)Math.round(stoppedTurningValue3 + stoppedTurningValues2[1] * (stoppedTurningValue4 + 6000.0)))) {
                  this.turningLandText = "Turning soon";
               } else {
                  this.rtpStartX = stoppedTurningValue2;
                  this.rtpStartZ = stoppedTurningValue3;
                  this.stoppedPilotValue = stoppedPilotValue;
                  this.turningLegsCount++;
               }
            }
         }
      } else {
         this.mode = HuntDriver.Mode.OFF;
         this.handleSiteTurning("Hunt stopped: the Elytra Pilot landed or stopped.");
      }
   }

   public HuntDriver(HuntDriver.Host host2) {
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.eastItems = new ArrayList<>();
      this.mode = HuntDriver.Mode.OFF;
      this.phase = HuntDriver.Phase.IDLE;
      this.turningLandText = "";
      this.east = "east";
      this.host = host2;
   }

   private Site getNext(ClientPlayerEntity player) {
      Site next = null;
      int bloodhound = this.host.getSureBaseThreshold();
      double huntDriverFactor = this.mode == HuntDriver.Mode.ELYTRA ? 640.0 : 320.0;

      for (Site site : this.host.getSiteBook().getValues()) {
         if (!site.joinActive
            && site.getMax() >= bloodhound
            && !(site.getAmount(player.getX(), player.getZ()) > huntDriverFactor * huntDriverFactor)
            && !this.canEast(site)
            && (next == null || site.getConfidenceScore() > next.getConfidenceScore())) {
            next = site;
         }
      }

      return next;
   }

   public HuntDriver.Mode getMode() {
      return this.mode;
   }

   private static boolean canSpectatorPlayers(MinecraftClient client, ClientPlayerEntity player) {
      for (PlayerEntity playerEntity : client.world.getPlayers()) {
         if (playerEntity != player && !playerEntity.isSpectator() && !FriendNameStore.canFlag(playerEntity) && playerEntity.squaredDistanceTo(player) <= 576.0) {
            return true;
         }
      }

      return false;
   }

   public String getTurningLandText() {
      return this.turningLandText;
   }

   private void handleEastNetwork(MinecraftClient client, ClientPlayerEntity player, long eastHuntTime) {
      if (client.getNetworkHandler() != null) {
         String east = this.rtpRegions.pickRegion(this.host.getRtpRegionName());
         if (!RtpRegions.isKnownRegion(east)) {
            east = "east";
         }

         this.east = east;
         client.getNetworkHandler().sendChatCommand("rtp " + east);
         this.rtpClock.onRequestSent(eastHuntTime);
         this.eastHuntTime = eastHuntTime;
         this.modeSiteAmount = player.getX();
         this.eastNetworkAmount = player.getZ();
         this.world = client.world;
         this.nextRtpAnswerTimeMs = eastHuntTime + 30000L;
         this.phase = HuntDriver.Phase.WAIT_TP;
         this.turningLandText = "Teleporting (/rtp " + east + ")";
      }
   }

   private void handleRtpClock(MinecraftClient client, long rtpClockValue) {
      Set set = null;

      String rtpClockText;
      while ((rtpClockText = this.concurrentLinkedQueue.poll()) != null) {
         if (set == null) {
            set = getNetworkHandler(client);
         }

         var set2 = set;
         Reply textsCombat = RtpReplies.classifyReply(
            rtpClockText, this.eastHuntTime > 0L ? rtpClockValue - this.eastHuntTime : -1L, names -> set2.contains(names.toLowerCase(Locale.ROOT))
         );
         switch (textsCombat.kind()) {
            case SEARCHING:
               this.rtpClock.extendRequestDeadline(rtpClockValue);
               break;
            case COOLDOWN:
               if (this.rtpClock.isRequestPending()) {
                  this.rtpClock.onCooldownReply(rtpClockValue, textsCombat.waitMs());
                  if (this.phase == HuntDriver.Phase.WAIT_TP) {
                     this.phase = HuntDriver.Phase.SEND;
                  }
               }
               break;
            case COMBAT:
               if (this.rtpClock.isRequestPending()) {
                  this.rtpClock.postponeLong(rtpClockValue);
                  if (this.phase == HuntDriver.Phase.WAIT_TP) {
                     this.phase = HuntDriver.Phase.SEND;
                  }
               }
               break;
            case CHAT_LIMIT:
               if (this.rtpClock.isRequestPending() && rtpClockValue - this.eastHuntTime < 2000L) {
                  this.rtpClock.postponeShort(rtpClockValue);
                  if (this.phase == HuntDriver.Phase.WAIT_TP) {
                     this.phase = HuntDriver.Phase.SEND;
                  }
               }
               break;
            case UNKNOWN_COMMAND:
               if (this.rtpClock.isRequestPending()) {
                  this.handleModeSite("Hunt stopped: this server has no /rtp.");
                  return;
               }
               break;
            case CANCELLED:
               if (this.rtpClock.isRequestPending()) {
                  this.rtpClock.postponeMedium(rtpClockValue);
                  if (this.phase == HuntDriver.Phase.WAIT_TP) {
                     this.phase = HuntDriver.Phase.SEND;
                  }
               }
         }
      }
   }

   private void handleEast(Site site) {
      if (!this.canEast(site)) {
         this.eastItems.add(new double[]{site.getJoinAmount(), site.getClueJoinAmount()});
      }
   }

   public void handleElytraRtp(HuntDriver.Mode mode, MinecraftClient client, int elytraRtpValue, long huntStoppedValue) {
      boolean elytraRtpFlag = this.mode == HuntDriver.Mode.ELYTRA;
      this.handleModeSite(null);
      if (elytraRtpFlag && mode != HuntDriver.Mode.ELYTRA) {
         handlePlayerGliding(client);
      }

      if (mode != HuntDriver.Mode.OFF && client.player != null && client.world != null) {
         this.mode = mode;
         this.sniffSpotCount = 0;
         this.turningLegsCount = 0;
         this.siteRtpAnswer = null;
         this.eastItems.clear();

         for (Site site : this.host.getSiteBook().getValues()) {
            this.handleEast(site);
         }

         this.rtpClock.reset();
         this.rtpRegions.reset();
         this.concurrentLinkedQueue.clear();
         if (mode == HuntDriver.Mode.RTP) {
            if (client.world.getRegistryKey() != World.OVERWORLD) {
               this.handleSiteTurning("Hunt with /rtp works in the overworld.");
               return;
            }

            this.phase = HuntDriver.Phase.SEND;
            this.host.sendChatMessage("Hunting with /rtp. It moves on by itself and stops when it finds a sure base.");
         } else {
            if (getAutoElytraPilot() == null) {
               this.handleSiteTurning("The Elytra Pilot is missing from this build.");
               return;
            }

            this.spiralSpacingBlocks = Math.max(160L, 2L * elytraRtpValue * 16L - 32L);
            this.rtpStartX = client.player.getX();
            this.rtpStartZ = client.player.getZ();
            this.stoppedPilotValue = 0;
            this.phase = HuntDriver.Phase.LEG;
            this.host.sendChatMessage("Hunting by elytra in a growing spiral. Spacing " + this.spiralSpacingBlocks + " blocks.");
            if (!this.canStoppedPilot(client)) {
               this.handleSiteTurning("The Elytra Pilot could not take off. You need an elytra, rockets and open sky.");
            }
         }
      }
   }

   public static boolean canDestinationPlayer(MinecraftClient client, double destinationPlayerValue, double pilotRenderLimit) {
      AutoElytraPilot autoElytraPilot = getAutoElytraPilot();
      if (autoElytraPilot != null && client.player != null) {
         int destinationAdjustingValue = (int)Math.round(destinationPlayerValue);
         int round4 = (int)Math.round(pilotRenderLimit);
         if (autoElytraPilot.isEnabled() && autoElytraPilot.changeDestination(destinationAdjustingValue, round4)) {
            autoElytraPilot.setSuppressArrivalLogOff(true);
            return true;
         } else {
            StringBase stringBase = null;

            for (ConfigSetting autoEspBase : autoElytraPilot.getSettings()) {
               if (autoEspBase instanceof StringBase stringBase2 && "Destination".equals(autoEspBase.getSettingName())) {
                  stringBase = stringBase2;
               }
            }

            if (stringBase == null) {
               return false;
            } else {
               StringBase stringBase3 = stringBase;
               handle(() -> {
                  if (autoElytraPilot.isEnabled()) {
                     autoElytraPilot.setEnabled(false);
                  }

                  String destinationPlayerText = stringBase3.getCurrentText();
                  stringBase3.handleText(destinationAdjustingValue + " " + round4);

                  try {
                     autoElytraPilot.setEnabled(true);
                     autoElytraPilot.setSuppressArrivalLogOff(true);
                  } finally {
                     stringBase3.handleText(destinationPlayerText);
                  }
               });
               return autoElytraPilot.isEnabled() && !autoElytraPilot.hasStartError();
            }
         }
      } else {
         return false;
      }
   }

   private static String formatText(long huntDriverValue) {
      return (huntDriverValue + 999L) / 1000L + "s";
   }

   private boolean isClueHost() {
      for (Site site : this.host.getSiteBook().getValues()) {
         if (site.joinActive && !(site.getAmount(this.x, this.z) > 102400.0)) {
            for (Site.Hit hit : site.getValues()) {
               if (hit.clue2ClueJoin.clueHostActive) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static void handle(Runnable runnable) {
      AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled();

      try {
         runnable.run();
      } finally {
         try {
            disabledEnabled.close();
         } catch (Exception e) {
         }
      }
   }

   private static void handlePlayerGliding(MinecraftClient client) {
      AutoElytraPilot autoElytraPilot = getAutoElytraPilot();
      if (autoElytraPilot != null && autoElytraPilot.isEnabled() && client.player != null) {
         if (!client.player.isGliding()) {
            handle(() -> autoElytraPilot.setEnabled(false));
         } else {
            autoElytraPilot.setSuppressArrivalLogOff(true);
            autoElytraPilot.retargetToCurrentPosition();
            if (!autoElytraPilot.isInLandingPhase()) {
               double angle = Math.toRadians(client.player.getYaw());
               int playerGlidingValue = (int)Math.round(client.player.getX() - Math.sin(angle) * 200.0);
               int pilotRenderLimit = (int)Math.round(client.player.getZ() + Math.cos(angle) * 200.0);
               if (!autoElytraPilot.changeDestination(playerGlidingValue, pilotRenderLimit)) {
                  canDestinationPlayer(client, playerGlidingValue, pilotRenderLimit);
               }
            }
         }
      }
   }

   public void handleModeSite(String modeSiteText) {
      boolean modeSiteFlag = this.mode != HuntDriver.Mode.OFF;
      HuntDriver.Mode mode = this.mode;
      this.mode = HuntDriver.Mode.OFF;
      this.phase = HuntDriver.Phase.IDLE;
      this.turningLandText = "";
      this.siteRtpAnswer = null;
      this.concurrentLinkedQueue.clear();
      this.world = null;
      this.nextRtpAnswerTimeMs = 0L;
      this.modeSiteAmount = 0.0;
      this.eastNetworkAmount = 0.0;
      if (mode == HuntDriver.Mode.ELYTRA && modeSiteText != null) {
         handlePlayerGliding(MinecraftClient.getInstance());
      }

      if (modeSiteFlag && modeSiteText != null) {
         this.host.sendChatMessage(modeSiteText);
      }
   }

   private void handleRtpAnswer(MinecraftClient client, ClientPlayerEntity player, long answerTimesTime) {
      this.handleRtpClock(client, answerTimesTime);
      if (this.mode != HuntDriver.Mode.OFF && this.phase != HuntDriver.Phase.DONE) {
         if (this.phase != HuntDriver.Phase.SNIFF && this.world != null && answerTimesTime <= this.nextRtpAnswerTimeMs) {
            boolean rtpAnswerFlag = client.world != this.world;
            double deltaX = player.getX() - this.modeSiteAmount;
            double deltaZ = player.getZ() - this.eastNetworkAmount;
            if (rtpAnswerFlag || deltaX * deltaX + deltaZ * deltaZ > 4096.0) {
               this.nextRtpAnswerTimeMs = 0L;
               if (this.siteRtpAnswer != null) {
                  Site site = this.siteRtpAnswer;
                  this.handleSiteTurning(
                     "Found a base ("
                        + site.getConfidenceScore()
                        + "%) at "
                        + Places.formatText(site.getJoinAmount(), site.getAmount(), site.getClueJoinAmount())
                        + " just before /rtp went through. Go back with the coordinates."
                  );
                  return;
               }

               this.rtpClock.onRtpCompleted(answerTimesTime);
               this.rtpRegions.clearRegionCount(this.east);
               this.answerTimesTime = answerTimesTime;
               this.x = player.getX();
               this.z = player.getZ();
               this.answerTimesValue = this.host.getBlockSensorCount();
               this.answerTimesTimeRtpAnswer = 0L;
               this.sniffSpotCount++;
               this.phase = HuntDriver.Phase.SNIFF;
            }
         }

         if (this.siteRtpAnswer != null && this.phase != HuntDriver.Phase.WAIT_TP) {
            Site siteRtpAnswer2 = this.siteRtpAnswer;
            this.siteRtpAnswer = null;
            this.handleElytraSky(client, siteRtpAnswer2);
         } else {
            switch (this.phase) {
               case SEND:
                  AutoElytraPilot autoElytraPilot = getAutoElytraPilot();
                  if (autoElytraPilot != null && autoElytraPilot.isEnabled()) {
                     this.turningLandText = "Waiting for the Elytra Pilot to land";
                  } else if (this.rtpClock.canSendAt(answerTimesTime)) {
                     this.handleEastNetwork(client, player, answerTimesTime);
                  } else {
                     this.turningLandText = "Next /rtp in " + formatText(this.rtpClock.getWaitRemainingMs(answerTimesTime));
                  }
                  break;
               case WAIT_TP:
                  if (this.rtpClock.hasRequestTimedOut(answerTimesTime)) {
                     this.rtpRegions.incrementRegionCount(this.east);
                     if (this.rtpClock.recordFailureAndCheckLimit(answerTimesTime)) {
                        this.handleModeSite("Hunt stopped: /rtp did not answer 5 times.");
                        return;
                     }

                     this.phase = HuntDriver.Phase.SEND;
                  } else {
                     this.turningLandText = "Teleporting (/rtp " + this.east + ")";
                  }
                  break;
               case SNIFF:
                  long rtpAnswerLimit = answerTimesTime - this.answerTimesTime;
                  int rtpAnswerLimit2 = this.host.getBlockSensorCount() - this.answerTimesValue;
                  this.turningLandText = "Sniffing spot " + this.sniffSpotCount + " \u00b7 " + rtpAnswerLimit2 + " chunks";
                  boolean spectatorPlayers = canSpectatorPlayers(client, player);
                  if (this.answerTimesTimeRtpAnswer == 0L && rtpAnswerLimit >= 10000L && rtpAnswerLimit2 >= 80) {
                     this.answerTimesTimeRtpAnswer = answerTimesTime;
                  }

                  boolean rtpAnswerFlag2 = this.answerTimesTimeRtpAnswer != 0L && this.host.isBlockSensorSettled(this.answerTimesTimeRtpAnswer) && !this.isClueHost();
                  if (spectatorPlayers || rtpAnswerLimit >= 25000L || rtpAnswerFlag2) {
                     for (Site getValuesItem : this.host.getSiteBook().getValues()) {
                        double rtpAnswerFactor = getValuesItem.getJoinAmount() - this.x;
                        double rtpAnswerFactor2 = getValuesItem.getClueJoinAmount() - this.z;
                        if (rtpAnswerFactor * rtpAnswerFactor + rtpAnswerFactor2 * rtpAnswerFactor2 <= 102400.0) {
                           this.handleEast(getValuesItem);
                        }
                     }

                     if (spectatorPlayers) {
                        this.host.sendChatMessage("A player is close, moving on.");
                     }

                     this.phase = HuntDriver.Phase.SEND;
                  }
            }
         }
      }
   }

   public void handleHealthHunt(MinecraftClient client, long healthHuntValue) {
      if (this.mode != HuntDriver.Mode.OFF && this.phase != HuntDriver.Phase.DONE && client.player != null && client.world != null) {
         ClientPlayerEntity player = client.player;
         if (player.getHealth() > 0.0F && player.getHealth() < 8.0F) {
            this.handleModeSite("Hunt stopped: low health.");
         } else {
            Site site = this.getNext(player);
            if (site != null && this.host.shouldStopWhenFound()) {
               if (this.mode != HuntDriver.Mode.RTP || this.phase != HuntDriver.Phase.WAIT_TP || !this.rtpClock.isRequestPending()) {
                  this.handleElytraSky(client, site);
                  return;
               }

               this.siteRtpAnswer = site;
            }

            if (this.mode == HuntDriver.Mode.RTP) {
               this.handleRtpAnswer(client, player, healthHuntValue);
            } else {
               this.handleStoppedTurning(client, player);
            }
         }
      }
   }

   public interface Host {
      int getBlockSensorCount();

      boolean isBlockSensorSettled(long sensorBloodhoundValue);

      String getRtpRegionName();

      void sendChatMessage(String bloodhoundText);

      SiteBook getSiteBook();

      int getSureBaseThreshold();

      boolean shouldStopWhenFound();
   }

   public static enum Mode {
      OFF,
      RTP,
      ELYTRA;
   }

   static enum Phase {
      IDLE,
      SEND,
      WAIT_TP,
      SNIFF,
      LEG,
      DONE;
   }
}
