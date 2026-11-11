package com.corz.client.utils;

import java.util.List;
import java.util.function.Predicate;
import com.corz.client.utils.RtpRegions;
import com.corz.client.utils.RtpClock;
import com.corz.client.utils.TpaReplies;
import com.corz.client.utils.HomeReplies;
import com.corz.client.utils.RtpReplies;

public final class HomeHop {
   private long phaseStartTimeMs;
   public static final String MSG_NO_RTP_COMMAND = "This server has no /rtp - you're still at home.";
   private long menuOpenedAtMs;
   private boolean teleportInProgress;
   public static final String RTP_OPENED_MENU = "/rtp opened a menu it couldn't use - still at home.";
   public static final String MSG_RTP_NO_TELEPORT = "/rtp did not teleport you - you're still at home.";
   public static final String MSG_HOME_NAME_INVALID = "Home Name can only use letters, numbers, - and _.";
   public static final String MSG_NO_FREE_HOME_SLOT = "No free home slot - stopped before /rtp.";
   public static final String MSG_SETHOME_NO_ANSWER = "/sethome did not answer - stopped before /rtp.";
   public static final String MSG_HOME_DID_NOT_RETURN = "/home did not bring you back. Try /home yourself.";
   private final RtpRegions rtpRegions;
   public static final String MSG_REPLACE_HOME_FAILED = "Couldn't replace the old home - stopped before /rtp.";
   private long lastCommandAtMs;
   private final RtpClock rtpClock = new RtpClock();
   private TpaFlow.Spot landingSpot;
   private final String homeName;
   private int goHomeAttempts;
   public static final String SETHOME_OPENED_MENU = "/sethome opened a menu - stopped before /rtp.";
   private long phaseDeadlineMs;
   private TpaFlow.Spot lastSeenSpot;
   private long startedAtMs;
   public static final String MSG_BACK_HOME = "Back home.";
   private String east;
   public static final String MSG_HOME_CANCELLED = "/home got cancelled - hold still and try /home.";
   private final String requestedRegion;
   private long teleportExpectedUntilMs;
   public static final String MSG_RTP_REFUSED_IN_COMBAT = "In combat - /rtp refused, you're still at home.";
   public static final String HOME_OPENED_MENU = "/home opened a menu. Use /home to go back yourself.";
   private long finishedAtMs;
   private long nextActionAtMs;
   private TpaFlow.Spot homeSpot;
   public static final String MSG_NO_HOME_COMMAND = "This server has no /home.";
   private long landingSettledSinceMs;
   public static final String MSG_RTP_CANCELLED = "/rtp cancelled - hold still during the countdown.";
   private boolean triedDeleteHome;
   private int chatLimitRetries;
   public static final String MSG_NO_SETHOME_COMMAND = "This server has no /sethome.";
   private long rtpArrivedAtMs;
   private HomeHop.Phase phase = HomeHop.Phase.SET_HOME;
   public static final String MSG_CANNOT_SET_HOME = "Can't set a home here - stopped before /rtp.";
   public static final String MSG_PLAYER_DIED = "You died - Silent Home stopped.";
   private boolean regionPicked;
   public static final String MSG_TIMED_OUT = "Took too long and stopped - use /home to go back.";

   private boolean isNearHomeSpot(TpaFlow.Spot spot) {
      return this.homeSpot != null && spot != null && this.homeSpot.canDim(spot) && this.homeSpot.getAmount(spot) <= 6.0;
   }

   private HomeHop.Action advanceSetHomeWait(HomeHop.Input input, long rtpStoppedTime) {
      if (input.menu()) {
         return this.failMenu("/sethome opened a menu - stopped before /rtp.", rtpStoppedTime);
      } else if (rtpStoppedTime < this.phaseDeadlineMs) {
         return HomeHop.Action.INSTANCE;
      } else if (this.phase == HomeHop.Phase.WAIT_CLEAR) {
         this.phase = HomeHop.Phase.SET_HOME;
         this.nextActionAtMs = rtpStoppedTime;
         return HomeHop.Action.INSTANCE;
      } else {
         return this.failWith("/sethome did not answer - stopped before /rtp.", rtpStoppedTime);
      }
   }

   private HomeHop.Action parseGoHomeReply(String homeTryText, long homeTryValue) {
      TpaReplies.Reply textsRequest = TpaReplies.classifyReply(homeTryText);
      boolean homeTryFlag = homeTryValue - this.phaseStartTimeMs <= 5000L;
      if (textsRequest == TpaReplies.Reply.COOLDOWN && TpaReplies.isTeleportWarmupMessage(homeTryText)) {
         textsRequest = TpaReplies.Reply.TELEPORTING;
      }

      switch (textsRequest) {
         case HOME_MISSING:
            if (homeTryFlag) {
               return this.failWith(formatHomeMissingMessage(this.homeName), homeTryValue);
            }
            break;
         case UNKNOWN_COMMAND:
            if (homeTryFlag && TpaReplies.canTeleportCommand(homeTryText)) {
               return this.failWith("This server has no /home.", homeTryValue);
            }
            break;
         case TELEPORTING:
            long compile = TpaReplies.parseCooldownMs(homeTryText);
            this.teleportInProgress = true;
            this.phaseDeadlineMs = Math.max(this.phaseDeadlineMs, homeTryValue + (compile > 0L ? compile : 5000L) + 5000L);
            break;
         case CANCELLED:
            if (TpaReplies.isCancelledWarmupMessage(homeTryText)) {
               return this.scheduleGoHome(homeTryValue, 0L, "/home got cancelled - hold still and try /home.");
            }
            break;
         case COOLDOWN:
         case CANNOT:
            if (homeTryFlag && TpaReplies.canTeleportCommand(homeTryText)) {
               long homeTryLimit = textsRequest == TpaReplies.Reply.COOLDOWN ? TpaReplies.parseCooldownMs(homeTryText) : -1L;
               if (homeTryLimit > 30000L) {
                  return this.failWith(formatHomeCooldownMessage(homeTryLimit), homeTryValue);
               }

               return this.scheduleGoHome(
                  homeTryValue,
                  homeTryLimit > 0L ? homeTryLimit + 1000L : 5000L,
                  textsRequest == TpaReplies.Reply.COOLDOWN ? formatHomeCooldownMessage(homeTryLimit) : "/home did not bring you back. Try /home yourself."
               );
            }
      }

      return null;
   }

   public static String formatHomeCooldownMessage(long homeHopLimit) {
      return "/home is on cooldown" + (homeHopLimit > 0L ? " for " + TpaFlow.formatText(homeHopLimit) : "") + " - use /home later.";
   }

   private HomeHop.Action sendSetHome(HomeHop.Input input, long lettersNumbersValue) {
      if (!isValidHomeName(this.homeName)) {
         return this.failWith("Home Name can only use letters, numbers, - and _.", lettersNumbersValue);
      } else if (!this.isCommandReady(lettersNumbersValue)) {
         return HomeHop.Action.INSTANCE;
      } else {
         this.homeSpot = input.at();
         this.enterPhase(HomeHop.Phase.WAIT_SET, lettersNumbersValue, 5000L);
         return this.sendCommand("sethome " + this.homeName, lettersNumbersValue);
      }
   }

   private HomeHop.Action sendRtp(long eastRtpValue) {
      if (!this.isCommandReady(eastRtpValue)) {
         return HomeHop.Action.INSTANCE;
      } else {
         String formatModFloor = this.rtpRegions.pickRegion(this.requestedRegion);
         this.east = RtpRegions.isKnownRegion(formatModFloor) ? formatModFloor : "east";
         this.rtpClock.onRequestSent(eastRtpValue);
         this.teleportInProgress = false;
         this.regionPicked = false;
         this.menuOpenedAtMs = 0L;
         this.enterPhase(HomeHop.Phase.WAIT_RTP, eastRtpValue, 60000L);
         return this.sendCommand("rtp " + this.east, eastRtpValue);
      }
   }

   private HomeHop.Action retryPhaseOrFail(HomeHop.Phase phase, long rtpPhaseValue, String rtpPhaseText) {
      if (this.chatLimitRetries >= 2) {
         return this.failWith(rtpPhaseText, rtpPhaseValue);
      } else {
         this.chatLimitRetries++;
         this.phase = phase;
         this.nextActionAtMs = rtpPhaseValue + 2500L;
         return HomeHop.Action.INSTANCE;
      }
   }

   private boolean isCommandReady(long rtpLimit) {
      return rtpLimit >= this.nextActionAtMs && (this.lastCommandAtMs == 0L || rtpLimit - this.lastCommandAtMs >= 1500L);
   }

   public static String formatHomeMissingMessage(String homeHopText) {
      return "No home called " + homeHopText + " - the server did not keep it.";
   }

   public static boolean isValidHomeName(String homeHopText) {
      return homeHopText != null && !homeHopText.isBlank() && TpaFlow.canCompile(homeHopText);
   }

   public String getChosenRegion() {
      return this.east;
   }

   private HomeHop.Action finishAtHome(long homeBackTime) {
      this.phase = HomeHop.Phase.DONE;
      this.finishedAtMs = homeBackTime;
      return new HomeHop.Action(HomeHop.Action.Kind.DONE, "Back home.");
   }

   private boolean isWithinReplyWindow(long homeHopValue) {
      return homeHopValue - this.phaseStartTimeMs <= 3000L;
   }

   private HomeHop.Action failWith(String homePhaseText, long homeBackTime) {
      this.phase = HomeHop.Phase.DONE;
      this.finishedAtMs = homeBackTime;
      return new HomeHop.Action(HomeHop.Action.Kind.FAIL, homePhaseText);
   }

   private HomeHop.Action parseChatLines(HomeHop.Input input, long onlinePhaseValue) {
      List lines = input.lines();
      if (lines != null && !lines.isEmpty()) {
         for (String onlinePhaseText : (Iterable<java.lang.String>) (lines)) {
            if (onlinePhaseText != null && !onlinePhaseText.isBlank() && !TpaReplies.isPlayerChatMessage(onlinePhaseText, input.online())) {
               HomeHop.Action onlinePhase = switch (this.phase) {
                  case WAIT_SET -> this.parseSetHomeReply(onlinePhaseText, onlinePhaseValue);
                  default -> null;
                  case WAIT_CLEAR -> this.parseDelHomeReply(onlinePhaseText, onlinePhaseValue);
                  case WAIT_RTP -> this.parseRtpReply(onlinePhaseText, onlinePhaseValue, input.online());
                  case WAIT_HOME -> this.parseGoHomeReply(onlinePhaseText, onlinePhaseValue);
               };
               if (onlinePhase != null) {
                  return onlinePhase;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private HomeHop.Action advanceWaitHome(HomeHop.Input input, long homeYourselfLimit) {
      if (this.isNearHomeSpot(input.at())) {
         return this.finishAtHome(homeYourselfLimit);
      } else if (input.menu()) {
         return this.failMenu("/home opened a menu. Use /home to go back yourself.", homeYourselfLimit);
      } else {
         return homeYourselfLimit >= this.phaseDeadlineMs ? this.scheduleGoHome(homeYourselfLimit, 0L, "/home did not bring you back. Try /home yourself.") : HomeHop.Action.INSTANCE;
      }
   }

   public TpaFlow.Spot getHomeSpot() {
      return this.homeSpot;
   }

   public HomeHop.Action advance(HomeHop.Input input) {
      if (this.phase != HomeHop.Phase.DONE && input != null) {
         long homeStoppedTime = input.now();
         if (this.startedAtMs == 0L) {
            this.startedAtMs = homeStoppedTime;
         }

         TpaFlow.Spot spot = input.at();
         boolean homeStoppedFlag = this.lastSeenSpot != null && spot != null && (!this.lastSeenSpot.canDim(spot) || this.lastSeenSpot.getAmount(spot) > 24.0);
         if (spot != null) {
            this.lastSeenSpot = spot;
         }

         if (input.dead()) {
            return this.failWith("You died - Silent Home stopped.", homeStoppedTime);
         } else if (homeStoppedTime - this.startedAtMs > 180000L) {
            return this.failWith("Took too long and stopped - use /home to go back.", homeStoppedTime);
         } else if (spot == null) {
            return HomeHop.Action.INSTANCE;
         } else {
            HomeHop.Action onlinePhase = this.parseChatLines(input, homeStoppedTime);
            if (onlinePhase != null) {
               return onlinePhase;
            } else {
               return switch (this.phase) {
                  case SET_HOME -> this.sendSetHome(input, homeStoppedTime);
                  case WAIT_SET, WAIT_CLEAR -> this.advanceSetHomeWait(input, homeStoppedTime);
                  case CLEAR_HOME -> this.sendDelHome(homeStoppedTime);
                  case RTP -> this.sendRtp(homeStoppedTime);
                  case WAIT_RTP -> this.advanceWaitRtp(input, homeStoppedTime, homeStoppedFlag);
                  case LANDING -> this.advanceLanding(input, homeStoppedTime);
                  case GO_HOME -> this.sendHome(homeStoppedTime);
                  case WAIT_HOME -> this.advanceWaitHome(input, homeStoppedTime);
                  case DONE -> HomeHop.Action.INSTANCE;
               };
            }
         }
      } else {
         return HomeHop.Action.INSTANCE;
      }
   }

   private HomeHop.Action failMenu(String homeMenuText, long homeBackTime) {
      this.phase = HomeHop.Phase.DONE;
      this.finishedAtMs = homeBackTime;
      return new HomeHop.Action(HomeHop.Action.Kind.FAIL_MENU, homeMenuText);
   }

   private HomeHop.Action sendCommand(String commandText, long time) {
      this.lastCommandAtMs = time;
      return new HomeHop.Action(HomeHop.Action.Kind.COMMAND, commandText);
   }

   private boolean hasMovedFarFromHome(TpaFlow.Spot spot) {
      return this.homeSpot != null && spot != null && (!this.homeSpot.canDim(spot) || this.homeSpot.getAmount(spot) > 24.0);
   }

   public boolean isActive(long homePhaseValue) {
      return this.phase != HomeHop.Phase.DONE || homePhaseValue - this.finishedAtMs < 2000L;
   }

   private void enterPhase(HomeHop.Phase phase, long time, long phaseValue) {
      this.phase = phase;
      this.phaseStartTimeMs = time;
      this.phaseDeadlineMs = time + phaseValue;
   }

   public HomeHop.Phase getPhase() {
      return this.phase;
   }

   private HomeHop.Action parseDelHomeReply(String homeOldText, long rtpStoppedTime) {
      if (HomeReplies.isHomeDeletedMessage(homeOldText)) {
         this.phase = HomeHop.Phase.SET_HOME;
         this.nextActionAtMs = rtpStoppedTime;
         return null;
      } else {
         HomeReplies.SetReply textsHome = HomeReplies.classifySetHomeReply(homeOldText);
         if (textsHome == HomeReplies.SetReply.CHAT_LIMIT && this.isWithinReplyWindow(rtpStoppedTime)) {
            return this.retryPhaseOrFail(HomeHop.Phase.CLEAR_HOME, rtpStoppedTime, "Couldn't replace the old home - stopped before /rtp.");
         } else {
            return textsHome == HomeReplies.SetReply.NO_COMMAND && this.isWithinReplyWindow(rtpStoppedTime)
               ? this.failWith("Couldn't replace the old home - stopped before /rtp.", rtpStoppedTime)
               : null;
         }
      }
   }

   private HomeHop.Action parseSetHomeReply(String rtpStoppedText, long rtpStoppedTime) {
      switch (HomeReplies.classifySetHomeReply(rtpStoppedText)) {
         case OK:
            this.phase = HomeHop.Phase.RTP;
            this.nextActionAtMs = rtpStoppedTime;
            break;
         case EXISTS:
            if (this.triedDeleteHome) {
               return this.failWith("Couldn't replace the old home - stopped before /rtp.", rtpStoppedTime);
            }

            this.phase = HomeHop.Phase.CLEAR_HOME;
            this.nextActionAtMs = rtpStoppedTime;
            break;
         case LIMIT:
            return this.failWith("No free home slot - stopped before /rtp.", rtpStoppedTime);
         case DENIED:
            return this.failWith("Can't set a home here - stopped before /rtp.", rtpStoppedTime);
         case NO_COMMAND:
            if (this.isWithinReplyWindow(rtpStoppedTime)) {
               return this.failWith("This server has no /sethome.", rtpStoppedTime);
            }
            break;
         case CHAT_LIMIT:
            if (this.isWithinReplyWindow(rtpStoppedTime)) {
               return this.retryPhaseOrFail(HomeHop.Phase.SET_HOME, rtpStoppedTime, "/sethome did not answer - stopped before /rtp.");
            }
      }

      return null;
   }

   public HomeHop(String homeHopText, String eastText, RtpRegions rtpRegions) {
      this.east = "";
      this.homeName = homeHopText == null ? "" : homeHopText.trim();
      this.requestedRegion = eastText;
      this.rtpRegions = rtpRegions == null ? new RtpRegions() : rtpRegions;
   }

   private HomeHop.Action sendHome(long homeTrimValue) {
      if (!this.isCommandReady(homeTrimValue)) {
         return HomeHop.Action.INSTANCE;
      } else {
         this.goHomeAttempts++;
         this.teleportInProgress = false;
         this.enterPhase(HomeHop.Phase.WAIT_HOME, homeTrimValue, 15000L);
         return this.sendCommand("home " + this.homeName, homeTrimValue);
      }
   }

   private HomeHop.Action parseRtpReply(String rtpYouText, long rtpYouValue, Predicate<String> predicate) {
      RtpReplies.Reply textsCombat = RtpReplies.classifyReply(rtpYouText, rtpYouValue - this.phaseStartTimeMs, predicate);
      switch (textsCombat.kind()) {
         case SEARCHING:
            this.rtpClock.extendRequestDeadline(rtpYouValue);
            break;
         case WARMUP:
            long rtpYouBound = RtpReplies.parseWaitMs(rtpYouText);
            this.teleportInProgress = true;
            this.teleportExpectedUntilMs = Math.max(this.teleportExpectedUntilMs, rtpYouValue + Math.max(rtpYouBound, 5000L) + 6000L);
            break;
         case SUCCESS:
            this.teleportInProgress = true;
            this.teleportExpectedUntilMs = Math.max(this.teleportExpectedUntilMs, rtpYouValue + 6000L);
            break;
         case COOLDOWN:
            this.rtpClock.onCooldownReply(rtpYouValue, textsCombat.waitMs());
            return this.failWith(formatRtpCooldownMessage(textsCombat.waitMs()), rtpYouValue);
         case COMBAT:
            return this.failWith("In combat - /rtp refused, you're still at home.", rtpYouValue);
         case CHAT_LIMIT:
            if (this.isWithinReplyWindow(rtpYouValue)) {
               this.rtpClock.postponeShort(rtpYouValue);
               return this.retryPhaseOrFail(HomeHop.Phase.RTP, rtpYouValue, "/rtp did not teleport you - you're still at home.");
            }
            break;
         case UNKNOWN_COMMAND:
            return this.failWith("This server has no /rtp - you're still at home.", rtpYouValue);
         case CANCELLED:
            return this.failWith("/rtp cancelled - hold still during the countdown.", rtpYouValue);
      }

      return null;
   }

   private HomeHop.Action scheduleGoHome(long rtpHomeValue, long rtpHomeBound, String rtpHomeText) {
      if (this.goHomeAttempts >= 3) {
         return this.failWith(rtpHomeText, rtpHomeValue);
      } else {
         this.phase = HomeHop.Phase.GO_HOME;
         this.nextActionAtMs = rtpHomeValue + Math.max(1500L, rtpHomeBound);
         return HomeHop.Action.INSTANCE;
      }
   }

   private HomeHop.Action sendDelHome(long trimRtpValue) {
      if (!this.isCommandReady(trimRtpValue)) {
         return HomeHop.Action.INSTANCE;
      } else {
         this.triedDeleteHome = true;
         this.enterPhase(HomeHop.Phase.WAIT_CLEAR, trimRtpValue, 5000L);
         return this.sendCommand("delhome " + this.homeName, trimRtpValue);
      }
   }

   private HomeHop.Action advanceWaitRtp(HomeHop.Input input, long time, boolean rtpYouFlag) {
      if (rtpYouFlag || this.hasMovedFarFromHome(input.at())) {
         this.rtpClock.onRtpCompleted(time);
         this.rtpRegions.clearRegionCount(this.east);
         this.phase = HomeHop.Phase.LANDING;
         this.rtpArrivedAtMs = time;
         this.landingSpot = null;
         return HomeHop.Action.INSTANCE;
      } else if (input.menu()) {
         if (this.menuOpenedAtMs == 0L) {
            this.menuOpenedAtMs = time;
         }

         if (!this.regionPicked) {
            this.regionPicked = true;
            return new HomeHop.Action(HomeHop.Action.Kind.PICK_REGION, this.east);
         } else {
            return time - this.menuOpenedAtMs > 3000L ? this.failMenu("/rtp opened a menu it couldn't use - still at home.", time) : HomeHop.Action.INSTANCE;
         }
      } else {
         this.menuOpenedAtMs = 0L;
         if (time >= this.phaseDeadlineMs) {
            return this.failWith("/rtp did not teleport you - you're still at home.", time);
         } else if (this.teleportInProgress) {
            return time > this.teleportExpectedUntilMs ? this.failWith("/rtp did not teleport you - you're still at home.", time) : HomeHop.Action.INSTANCE;
         } else {
            return this.rtpClock.hasRequestTimedOut(time) ? this.failWith("/rtp did not teleport you - you're still at home.", time) : HomeHop.Action.INSTANCE;
         }
      }
   }

   public static String formatRtpCooldownMessage(long homeHopLimit) {
      return "/rtp is on cooldown" + (homeHopLimit > 0L ? " for " + TpaFlow.formatText(homeHopLimit) : "") + " - still at home.";
   }

   private HomeHop.Action advanceLanding(HomeHop.Input input, long time) {
      TpaFlow.Spot spot = input.at();
      if (this.landingSpot == null || !this.landingSpot.canDim(spot) || this.landingSpot.getAmount(spot) > 0.6) {
         this.landingSpot = spot;
         this.landingSettledSinceMs = time;
      }

      boolean spotFlag = input.ground() && time - this.landingSettledSinceMs >= 1000L && time - this.rtpArrivedAtMs >= 1500L;
      if (spotFlag || time - this.rtpArrivedAtMs >= 10000L) {
         this.phase = HomeHop.Phase.GO_HOME;
         this.goHomeAttempts = 0;
         this.nextActionAtMs = time;
      }

      return HomeHop.Action.INSTANCE;
   }

   public record Action(HomeHop.Action.Kind kind, String text) {
      public static final HomeHop.Action INSTANCE = new HomeHop.Action(HomeHop.Action.Kind.NONE, null);

      public static enum Kind {
         NONE,
         COMMAND,
         PICK_REGION,
         DONE,
         FAIL,
         FAIL_MENU;
      }
   }

   public record Input(long now, TpaFlow.Spot at, boolean ground, boolean menu, boolean dead, List<String> lines, Predicate<String> online) {
   }

   public static enum Phase {
      SET_HOME,
      WAIT_SET,
      CLEAR_HOME,
      WAIT_CLEAR,
      RTP,
      WAIT_RTP,
      LANDING,
      GO_HOME,
      WAIT_HOME,
      DONE;
   }
}
