package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.donutsmp.DeepPlayerFinder;
import com.corz.client.utils.IconsPngType;

public class RtpBaseAlert extends Module {
   private long pauseDeadlineMs;
   private long nextRtpAtMs;
   private int lastChunkZ;
   private int rtpCount;
   private final EnumSetting flagActionSetting = new EnumSetting("Flag Action", "What to do when a flag is found.", "Disable", "Disable", "Pause");
   private final EnumSetting west;
   private boolean awaitingRtpArrival;
   private long rtpSentAtMs;
   private boolean justResumed;
   private boolean paused;
   private final BooleanSetting tpaHereSetting;
   private final IntegerSetting rtpDelaySetting = new IntegerSetting("Delay", "Seconds to wait after each /rtp before scanning.", 5, 1, 15);
   private boolean resumeCountdownActive;
   private final IntegerSetting minFlags = new IntegerSetting(
      "Min Flags", "Number of new flags required before reacting (counted from module enable).", 1, 1, 10
   );
   private long resumeAtMs;
   private final BooleanSetting antiDeath;
   private long nextTpaHereAtMs;
   private boolean sameRegionWarned;
   private final StringBase tpaHereNamesSetting;
   private boolean firstRtpPending;
   private boolean lastRtpUsedSecondaryRegion;
   private final ArrayDeque<String> pendingTpaNames;
   private boolean disconnecting;
   private boolean alertPending;
   private final IntegerSetting health;
   private int lastChunkX;
   private int baselineFlagCount;
   private final BooleanSetting stopOnPlayer;
   private final EnumSetting every10th;

   @Override
   protected void onDisable() {
      this.nextRtpAtMs = -1L;
      this.pendingTpaNames.clear();
      this.nextTpaHereAtMs = -1L;
      this.disconnecting = false;
      this.paused = false;
      this.pauseDeadlineMs = -1L;
      this.resumeCountdownActive = false;
      this.resumeAtMs = -1L;
      this.justResumed = false;
      this.alertPending = false;
      this.awaitingRtpArrival = false;
      this.firstRtpPending = false;
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         if (this.firstRtpPending) {
            this.firstRtpPending = false;
            this.sendRtpCommand(this.west.getCurrentOption());
            this.nextRtpAtMs = System.currentTimeMillis() + this.rtpDelaySetting.getValue() * 1000L;
         } else {
            if (this.antiDeath.isEnabled() && !this.disconnecting && mc.player.getHealth() <= this.health.getValue()) {
               double x = mc.player.getX();
               double y = mc.player.getY();
               double z = mc.player.getZ();

               for (Entity entity : mc.world.getEntities()) {
                  if (entity.getType().getSpawnGroup() == SpawnGroup.MONSTER) {
                     double iIiFactor = entity.getX() - x;
                     double iIiFactor2 = entity.getY() - y;
                     double iIiFactor3 = entity.getZ() - z;
                     if (iIiFactor * iIiFactor + iIiFactor2 * iIiFactor2 + iIiFactor3 * iIiFactor3 <= 64.0) {
                        this.disconnectWithReason("Low health with hostile mobs nearby");
                        break;
                     }
                  }
               }
            }

            if (this.stopOnPlayer.isEnabled() && !this.paused && !this.justResumed) {
               for (Entity getEntitiesItem : mc.world.getEntities()) {
                  if (getEntitiesItem instanceof PlayerEntity && getEntitiesItem != mc.player) {
                     String iIiText = getEntitiesItem.getName().getString();
                     boolean iIiFlag = false;

                     for (String getTpaHereNamesItem : this.getTpaHereNames()) {
                        if (getTpaHereNamesItem.equalsIgnoreCase(iIiText)) {
                           iIiFlag = true;
                           break;
                        }
                     }

                     if (!iIiFlag) {
                        this.sendInfoMessage("Player detected nearby - disabling RTP Base Alert");
                        this.setEnabled(false);
                        return;
                     }
                  }
               }
            }

            if (this.paused) {
               if (!this.pendingTpaNames.isEmpty() && System.currentTimeMillis() >= this.nextTpaHereAtMs) {
                  this.sendChatCommand("tpahere " + this.pendingTpaNames.poll());
                  this.nextTpaHereAtMs = System.currentTimeMillis() + 1000L;
               }

               if (!this.resumeCountdownActive) {
                  for (Entity entity3 : mc.world.getEntities()) {
                     if (entity3 instanceof PlayerEntity && entity3 != mc.player) {
                        String fastSmoothText = entity3.getName().getString();

                        for (String iIiText4 : this.getTpaHereNames()) {
                           if (iIiText4.equalsIgnoreCase(fastSmoothText)) {
                              this.resumeCountdownActive = true;
                              this.resumeAtMs = System.currentTimeMillis() + 5000L;
                              this.sendInfoMessage("Detected " + fastSmoothText + " - resuming rtp in 5 seconds");
                              break;
                           }
                        }

                        if (this.resumeCountdownActive) {
                           break;
                        }
                     }
                  }
               }

               if (this.resumeCountdownActive && System.currentTimeMillis() >= this.resumeAtMs) {
                  this.resumeAlert();
               } else if (System.currentTimeMillis() >= this.pauseDeadlineMs) {
                  this.sendInfoMessage("No /tpahere accepted within 10 minutes - resuming rtp");
                  this.resumeAlert();
               }
            } else if (this.awaitingRtpArrival) {
               long currentMillis = System.currentTimeMillis();
               int iIiValue = mc.player.getBlockX() >> 4;
               int iIiValue2 = mc.player.getBlockZ() >> 4;
               if (iIiValue != this.lastChunkX || iIiValue2 != this.lastChunkZ || currentMillis - this.rtpSentAtMs >= 30000L) {
                  this.awaitingRtpArrival = false;
                  this.nextRtpAtMs = currentMillis + this.rtpDelaySetting.getValue() * 1000L;
               }
            } else if (this.nextRtpAtMs < 0L) {
               if (!this.pendingTpaNames.isEmpty()) {
                  if (System.currentTimeMillis() >= this.nextTpaHereAtMs) {
                     this.sendChatCommand("tpahere " + this.pendingTpaNames.poll());
                     this.nextTpaHereAtMs = System.currentTimeMillis() + 1000L;
                  }
               } else if (this.nextTpaHereAtMs >= 0L && !this.paused) {
                  this.nextTpaHereAtMs = -1L;
                  this.setEnabled(false);
               }
            } else if (System.currentTimeMillis() >= this.nextRtpAtMs) {
               this.nextRtpAtMs = -1L;
               if (this.alertPending) {
                  this.alertPending = false;
               } else {
                  this.justResumed = false;
               }

               if (this.getFlagCount() - this.baselineFlagCount >= this.minFlags.getValue()) {
                  if (this.flagActionSetting.getCurrentOption().equals("Pause")) {
                     this.sendInfoMessage("Flag found - pausing RTP Base Alert for up to 10 minutes");
                     this.paused = true;
                     this.pauseDeadlineMs = System.currentTimeMillis() + 600000L;
                     if (this.tpaHereSetting.isEnabled() && !this.getTpaHereNames().isEmpty()) {
                        this.pendingTpaNames.addAll(this.getTpaHereNames());
                        this.nextTpaHereAtMs = System.currentTimeMillis();
                     }
                  } else {
                     this.sendInfoMessage("Flag found - disabling RTP Base Alert");
                     if (this.tpaHereSetting.isEnabled() && !this.getTpaHereNames().isEmpty()) {
                        this.pendingTpaNames.addAll(this.getTpaHereNames());
                        this.nextTpaHereAtMs = System.currentTimeMillis();
                     } else {
                        this.setEnabled(false);
                     }
                  }
               } else {
                  if (this.every10th.getCurrentOption().equals(this.west.getCurrentOption()) && !this.sameRegionWarned) {
                     this.sameRegionWarned = true;
                     this.sendWarningMessage("every-10th region equals the main region - using " + this.west.getCurrentOption() + " for every teleport");
                  }

                  this.rtpCount++;
                  boolean stopPlayerFlag = !this.every10th.getCurrentOption().equals(this.west.getCurrentOption()) && this.rtpCount % 10 == 0 && !this.lastRtpUsedSecondaryRegion;
                  String blockResumingText = stopPlayerFlag ? this.every10th.getCurrentOption() : this.west.getCurrentOption();
                  this.lastRtpUsedSecondaryRegion = stopPlayerFlag;
                  this.sendRtpCommand(blockResumingText);
                  this.nextRtpAtMs = System.currentTimeMillis() + this.rtpDelaySetting.getValue() * 1000L;
               }
            }
         }
      }
   }

   private int getFlagCount() {
      return ModuleRegistry.getModule(DeepPlayerFinder.class) instanceof DeepPlayerFinder deepPlayerFinder ? deepPlayerFinder.getFlaggedChunkCount() : 0;
   }

   private void enableDeepPlayerFinder() {
      Module iliIiiliiiii = ModuleRegistry.getModule(DeepPlayerFinder.class);
      if (iliIiiliiiii != null && !iliIiiliiiii.isEnabled()) {
         iliIiiliiiii.setEnabled(true);
      }
   }

   @Override
   protected void onEnable() {
      this.enableDeepPlayerFinder();
      this.baselineFlagCount = this.getFlagCount();
      this.rtpCount = 0;
      this.lastRtpUsedSecondaryRegion = false;
      this.sameRegionWarned = false;
      this.paused = false;
      this.pauseDeadlineMs = -1L;
      this.resumeCountdownActive = false;
      this.resumeAtMs = -1L;
      this.justResumed = false;
      this.alertPending = false;
      this.awaitingRtpArrival = false;
      this.disconnecting = false;
      this.nextRtpAtMs = -1L;
      this.pendingTpaNames.clear();
      this.nextTpaHereAtMs = -1L;
      this.firstRtpPending = true;
   }

   private void sendInfoMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal("\u00a7b[RTP Base Alert]\u00a7r " + messageLiteralText), false);
      }
   }

   private void sendRtpCommand(String blockResumingText) {
      this.sendChatCommand("rtp " + blockResumingText);
      this.awaitingRtpArrival = true;
      this.rtpSentAtMs = System.currentTimeMillis();
      this.lastChunkX = mc.player.getBlockX() >> 4;
      this.lastChunkZ = mc.player.getBlockZ() >> 4;
   }

   public RtpBaseAlert() {
      super("RTP Base Alert", IconsPngType.DONUTSMP);
      this.west = new EnumSetting("Region", "Region to /rtp to most of the time.", "east", "asia", "east", "eu central", "eu west", "oceania", "west");
      this.every10th = new EnumSetting(
         "Every 10th",
         "Region to /rtp to every 10th teleport. Ignored if it equals the main region.",
         "asia",
         "asia",
         "east",
         "eu central",
         "eu west",
         "nether",
         "oceania",
         "west"
      );
      this.tpaHereSetting = new BooleanSetting("TPAHere", "Send /tpahere to the configured players when a flag is found.", false);
      this.tpaHereNamesSetting = new StringBase("TPAHere Names", "Comma-separated player names to /tpahere, in order.", "");
      this.antiDeath = new BooleanSetting("Anti Death", "Disconnect when health is critically low and hostile mobs are nearby.", true);
      this.health = new IntegerSetting("Health", "Disconnect threshold for anti-death (HP).", 5, 1, 20);
      this.stopOnPlayer = new BooleanSetting("Stop On Player", "Stop and disable when another (non-trusted) player is detected nearby.", true);
      this.rtpCount = 0;
      this.lastRtpUsedSecondaryRegion = false;
      this.sameRegionWarned = false;
      this.baselineFlagCount = 0;
      this.disconnecting = false;
      this.nextRtpAtMs = -1L;
      this.pendingTpaNames = new ArrayDeque<>();
      this.nextTpaHereAtMs = -1L;
      this.paused = false;
      this.pauseDeadlineMs = -1L;
      this.resumeCountdownActive = false;
      this.resumeAtMs = -1L;
      this.justResumed = false;
      this.alertPending = false;
      this.awaitingRtpArrival = false;
      this.rtpSentAtMs = 0L;
      this.lastChunkX = 0;
      this.lastChunkZ = 0;
      this.firstRtpPending = false;
      this.addSettings(
         new ConfigSetting[]{
            this.flagActionSetting,
            this.rtpDelaySetting,
            this.minFlags,
            this.west,
            this.every10th,
            this.tpaHereSetting,
            this.tpaHereNamesSetting,
            this.antiDeath,
            this.health,
            this.stopOnPlayer
         }
      );
   }

   private List<String> getTpaHereNames() {
      var arrayList = new ArrayList();
      String rtpAlertText = this.tpaHereNamesSetting.getCurrentText();
      if (rtpAlertText != null) {
         for (String rtpAlertText2 : rtpAlertText.split(",")) {
            String trim = rtpAlertText2.trim();
            if (!trim.isEmpty()) {
               arrayList.add(trim);
            }
         }
      }

      return arrayList;
   }

   private void sendChatCommand(String networkText) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendChatCommand(networkText);
      }
   }

   private void disconnectWithReason(String networkText) {
      this.disconnecting = true;
      this.sendInfoMessage("Disconnecting - " + networkText);
      this.setEnabled(false);
      if (mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().getConnection().disconnect(Text.literal("[RTP Base Alert] " + networkText));
      }
   }

   private void resumeAlert() {
      this.paused = false;
      this.pauseDeadlineMs = -1L;
      this.resumeCountdownActive = false;
      this.resumeAtMs = -1L;
      this.pendingTpaNames.clear();
      this.nextTpaHereAtMs = -1L;
      this.justResumed = true;
      this.alertPending = true;
      this.baselineFlagCount = this.getFlagCount();
      this.sendInfoMessage("Resuming RTP Base Alert");
      this.nextRtpAtMs = System.currentTimeMillis() + this.rtpDelaySetting.getValue() * 1000L;
   }

   private void sendWarningMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal("\u00a7e[RTP Base Alert]\u00a7r " + messageLiteralText), false);
      }
   }
}
