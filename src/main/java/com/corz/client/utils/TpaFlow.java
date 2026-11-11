package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import com.corz.client.utils.TpaReplies;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.TpaPicker;

public final class TpaFlow {
   public static final Pattern compile = Pattern.compile("[A-Za-z0-9_-]{0,32}");
   private long pausedGoingTime;
   public static final String SERVER_TPAHERE = "This server has no /tpahere";
   public static final String RESPAWN = "Could not respawn";
   public static final String SERVER_SAID = "Server said no";
   private int respawnValue;
   private boolean inputTpahereActive;
   private long checkHomeBackoffMs;
   private long respawnStuckTime;
   private int homeGoingValue;
   private int spotTrapCount;
   private int homeCount;
   private TpaFlow.State flowState;
   public static final String HOME_CHECK_HOME = "Could not get home. Check Home Name";
   private String tpahereServerText;
   public static final String ONE_LEFT_ASK = "No one left to ask";
   private long askSentTimeMs;
   private int diedOnceCount;
   private long serverSaidValue;
   private TpaFlow.Input input;
   private long homeTrapTime;
   private final Map<String, TpaFlow.Ask> waitingCooldownMap;
   private String cooldownAskText;
   private long homeLettersTime;
   private int saidServerValue;
   private long homeGoingTime;
   private long homeGoingTimeHomeGoing;
   private String askingTpahereText;
   private long lettersNumbersTime;
   public static final String HOME_OPENED_MENU = "/home opened a menu. Set Home Name to your trap home";
   private String askingAskText;
   private long homeGoingTimeHomeCheck;
   public static final String AWAY_TRAP = "Away from the trap";
   public static final String SERVER_HOME = "This server has no /home";
   public static final String SERVER_COOLDOWN = "Server cooldown";
   private final Random random;
   private final Map<String, Long> askServerMap = new HashMap<>();
   public static final String CLOSE_MENU_KEEP = "Close the menu to keep going";
   public static final String HOME_LETTERS_NUMBERS = "Home Name can only use letters, numbers, - and _";
   private long pendingDeadlineMs;
   private boolean cooldownActive;
   private TpaFlow.Spot spot;
   private boolean homeTrapFlag;
   public static final String SEPARATOR = " \u00b7 ";
   private long homeValue;
   private boolean goingPausedFlag;
   private long respawnTime;
   private TpaFlow.Spot spotHomeGoing;
   private int cooldownCount;
   private long inputTpahereValue;
   public static final String STAND_YOUR_TRAP = "Stand in your trap, then switch it off and on.";
   private long cooldownPausedTime;
   private TpaFlow.Spot spotSpotTrap;
   private Set<String> inputTpahereStringSet;
   private final ArrayDeque<Long> arrayDeque;
   private String stuckText;

   public int getHomeCount() {
      return this.homeCount;
   }

   private ScissorTgtHandler.Snapshot getMax(ScissorTgtHandler.Tone tone, String tpaFlowText, String tpaFlowText2) {
      return new ScissorTgtHandler.Snapshot(tone, tpaFlowText, null, this.cooldownCount, this.homeCount, tpaFlowText2);
   }

   public static long getTime(long tpaFlowValue, long askingPausedValue) {
      return tpaFlowValue == 0L ? 0L : Math.max(0L, tpaFlowValue - askingPausedValue);
   }

   public TpaFlow(Random random) {
      this.waitingCooldownMap = new LinkedHashMap<>();
      this.arrayDeque = new ArrayDeque<>();
      this.flowState = TpaFlow.State.IDLE;
      this.serverSaidValue = 15000L;
      this.checkHomeBackoffMs = 5000L;
      this.random = random == null ? new Random() : random;
   }

   private void handleCooldownAsk(long cooldownAskValue, long cooldownAskValue2, String cooldownAskText) {
      this.pendingDeadlineMs = cooldownAskValue + cooldownAskValue2;
      this.cooldownPausedTime = this.pendingDeadlineMs;
      this.cooldownAskText = cooldownAskText;
   }

   private void handleAskAsking(TpaFlow.State state) {
      this.flowState = state;
      this.goingPausedFlag = false;
      this.askingAskText = null;
   }

   private boolean canHomeServer(TpaReplies.Reply reply, String homeServerText, long homeServerValue, TpaFlow.Settings settings) {
      boolean homeServerFlag = this.homeLettersTime != 0L && homeServerValue - this.homeLettersTime <= 5000L;
      boolean homeServerFlag2 = homeServerFlag || this.homeValue != 0L && homeServerValue <= this.homeValue;
      if (reply == TpaReplies.Reply.COOLDOWN && TpaReplies.isTeleportWarmupMessage(homeServerText)) {
         reply = TpaReplies.Reply.TELEPORTING;
      }

      switch (reply) {
         case HOME_MISSING:
            if (!homeServerFlag) {
               return false;
            }

            this.handleStuckCooldown(formatSethomeTrap(settings.homeName()));
            break;
         case UNKNOWN_COMMAND:
            if (!homeServerFlag || !TpaReplies.canTeleportCommand(homeServerText)) {
               return false;
            }

            this.handleStuckCooldown("This server has no /home");
            break;
         case TELEPORTING:
            if (!homeServerFlag2) {
               return false;
            }

            long compile = TpaReplies.parseCooldownMs(homeServerText);
            this.homeTrapTime = homeServerValue + (compile > 0L ? compile : 5000L) + 5000L;
            this.homeValue = this.homeTrapTime;
            break;
         case CANCELLED:
            if (!homeServerFlag2 || !TpaReplies.isCancelledWarmupMessage(homeServerText)) {
               return false;
            }

            this.handleHomeCheck(homeServerValue, 0L);
            break;
         case COOLDOWN:
         case CANNOT:
            if (!homeServerFlag || !TpaReplies.canTeleportCommand(homeServerText)) {
               return false;
            }

            long homeServerLimit = reply == TpaReplies.Reply.COOLDOWN ? TpaReplies.parseCooldownMs(homeServerText) : -1L;
            this.handleHomeCheck(homeServerValue, homeServerLimit > 0L ? homeServerLimit + 1000L : 0L);
            break;
         default:
            return false;
      }

      return true;
   }

   public static String formatHome(String homeText) {
      String homeText2 = homeText == null ? "" : homeText.trim();
      return homeText2.isEmpty() ? "home" : "home " + homeText2;
   }

   private ScissorTgtHandler.Snapshot getAskingPaused(long askingPausedValue, TpaFlow.Settings settings) {
      if (this.goingPausedFlag) {
         return this.getMax(ScissorTgtHandler.Tone.PAUSED, "Paused", "Close the menu to keep going");
      } else if (this.pausedGoingTime != 0L) {
         String askingPausedText = "/" + formatHome(settings == null ? "" : settings.homeName());
         return this.getMax(ScissorTgtHandler.Tone.PAUSED, "Paused", "Away from the trap \u00b7 " + askingPausedText + " in " + formatText(this.getTimeGoing(askingPausedValue) - askingPausedValue));
      } else if (this.cooldownPausedTime != 0L && askingPausedValue < this.cooldownPausedTime) {
         return this.getMax(ScissorTgtHandler.Tone.PAUSED, "Cooldown", this.cooldownAskText + " \u00b7 next in " + formatText(this.cooldownPausedTime - askingPausedValue));
      } else if (settings != null && settings.waitForAccept() && !this.waitingCooldownMap.isEmpty()) {
         TpaFlow.Ask waitingcooldownmap = this.waitingCooldownMap.get(TpaPicker.getLowerCase(this.askingTpahereText));
         if (waitingcooldownmap == null) {
            waitingcooldownmap = this.waitingCooldownMap.values().iterator().next();
         }

         return this.getMax(
            ScissorTgtHandler.Tone.ACTIVE, "Waiting", "Waiting for " + waitingcooldownmap.name() + " \u00b7 " + formatText(getTime(waitingcooldownmap.until(), askingPausedValue)) + " left"
         );
      } else {
         String formatText2 = formatText(Math.max(0L, this.getMax(askingPausedValue) - askingPausedValue));
         if (this.askingAskText != null) {
            return this.getMax(ScissorTgtHandler.Tone.ACTIVE, "Asking", this.askingAskText + " \u00b7 next in " + formatText2);
         } else {
            return this.askingTpahereText == null
               ? this.getMax(ScissorTgtHandler.Tone.ACTIVE, "Asking", "First ask in " + formatText2)
               : this.getMax(ScissorTgtHandler.Tone.ACTIVE, "Asking", "Asked " + this.askingTpahereText + " \u00b7 next in " + formatText2);
         }
      }
   }

   private void handleSpotTrap(TpaFlow.Spot spot, long spotTrapValue) {
      if (this.homeTrapFlag && spot.getAmount(this.spotSpotTrap) > 4.0) {
         this.spotSpotTrap = spot;
      }

      this.spotTrapCount++;
      this.updateHomeTime();
      this.handleRequestingGoing(spotTrapValue);
   }

   public TpaFlow.Spot getSpotSpotTrap() {
      return this.spotSpotTrap;
   }

   public void updateIdleState() {
      this.updateTimeHome();
      this.flowState = TpaFlow.State.IDLE;
   }

   private void handleServerSaid(TpaReplies.Reply reply, String serverSaidText, long serverSaidValue) {
      boolean serverSaidFlag = this.askingTpahereText != null && this.askSentTimeMs != 0L && serverSaidValue - this.askSentTimeMs <= 5000L;
      String lowerCase = TpaPicker.getLowerCase(this.askingTpahereText);
      switch (reply) {
         case UNKNOWN_COMMAND:
            if (serverSaidFlag && this.canAskingTpahere(serverSaidText, false)) {
               this.tpahereServerText = "This server has no /tpahere";
            }
         case TELEPORTING:
         case CANCELLED:
         default:
            break;
         case COOLDOWN:
            if (serverSaidFlag && this.canAskingTpahere(serverSaidText, false)) {
               this.handleMaxCooldown(lowerCase);
               this.askServerMap.remove(lowerCase);
               long compile = TpaReplies.parseCooldownMs(serverSaidText);
               this.handleCooldownAsk(serverSaidValue, Math.max(compile > 0L ? compile + 1000L : 0L, this.serverSaidValue), "Server cooldown");
               this.serverSaidValue = Math.min(this.serverSaidValue * 2L, 120000L);
            }
            break;
         case CANNOT:
            if (serverSaidFlag && this.canAskingTpahere(serverSaidText, false)) {
               this.handleMaxCooldown(lowerCase);
               this.askServerMap.remove(lowerCase);
               this.saidServerValue++;
               boolean serverSaidFlag2 = this.saidServerValue >= 3;
               if (serverSaidFlag2) {
                  this.saidServerValue = 0;
               }

               this.handleCooldownAsk(serverSaidValue, serverSaidFlag2 ? 300000L : 30000L, "Server said no");
            }
            break;
         case SENT:
            if (serverSaidFlag) {
               this.saidServerValue = 0;
               this.serverSaidValue = 15000L;
            }
            break;
         case NOT_FOUND:
         case PENDING:
            if (serverSaidFlag && this.canAskingTpahere(serverSaidText, reply == TpaReplies.Reply.NOT_FOUND)) {
               this.handleMaxCooldown(lowerCase);
               this.pendingDeadlineMs = serverSaidValue + 2000L;
            }
            break;
         case NOT_ACCEPTING:
            if (serverSaidFlag && this.canAskingTpahere(serverSaidText, false)) {
               this.handleMaxCooldown(lowerCase);
               this.askServerMap.put(lowerCase, serverSaidValue + 3600000L);
               this.pendingDeadlineMs = serverSaidValue + 2000L;
            }
            break;
         case ACCEPTED:
            for (Entry entry : this.waitingCooldownMap.entrySet()) {
               if (TpaReplies.containsWholeWord(serverSaidText, (String)entry.getKey())) {
                  entry.setValue(new TpaFlow.Ask(((TpaFlow.Ask)entry.getValue()).name(), serverSaidValue + 60000L));
               }
            }
            break;
         case DENIED:
         case EXPIRED:
            this.waitingCooldownMap.keySet().removeIf(serverSaidText2 -> TpaReplies.containsWholeWord(serverSaidText, serverSaidText2));
      }
   }

   private ScissorTgtHandler.Snapshot getGoingHome(long goingHomeValue, TpaFlow.Settings settings) {
      if (this.goingPausedFlag) {
         return this.getMax(ScissorTgtHandler.Tone.PAUSED, "Paused", "Close the menu to keep going");
      } else {
         String goingHomeText = "/" + formatHome(settings == null ? "" : settings.homeName());
         return this.homeGoingTime != 0L
            ? this.getMax(ScissorTgtHandler.Tone.ACTIVE, "Going home", "Sent " + goingHomeText + " \u00b7 try " + this.homeGoingValue + " of " + 4)
            : this.getMax(
               ScissorTgtHandler.Tone.ACTIVE,
               "Going home",
               goingHomeText + " in " + formatText(getTime(this.homeGoingTimeHomeCheck, goingHomeValue)) + " \u00b7 try " + (this.homeGoingValue + 1) + " of " + 4
            );
      }
   }

   private TpaFlow.Action getHomeGoing(TpaFlow.Input input, TpaFlow.Settings settings, long homeGoingTime) {
      TpaFlow.Spot spot = input.at();
      if (!input.dead() && spot != null) {
         this.goingPausedFlag = input.screen();
         if (this.homeGoingTime == 0L) {
            if (!this.goingPausedFlag && canFlag(this.homeGoingTimeHomeCheck, homeGoingTime) && (this.lettersNumbersTime == 0L || homeGoingTime - this.lettersNumbersTime >= 1500L)) {
               if (!canCompile(settings.homeName())) {
                  this.handleStuckCooldown("Home Name can only use letters, numbers, - and _");
                  return TpaFlow.Action.INSTANCE2;
               } else {
                  this.homeGoingValue++;
                  this.updateHomeTime();
                  this.homeGoingTime = homeGoingTime;
                  this.homeLettersTime = homeGoingTime;
                  this.homeTrapTime = homeGoingTime + 15000L;
                  this.spot = spot;
                  return this.getLettersNumbers(formatHome(settings.homeName()), homeGoingTime);
               }
            } else {
               return TpaFlow.Action.INSTANCE2;
            }
         } else if (input.menu()) {
            this.handleStuckCooldown("/home opened a menu. Set Home Name to your trap home");
            return TpaFlow.Action.INSTANCE2;
         } else {
            if (input.teleported()) {
               this.homeTrapFlag = true;
            }

            if (!this.canSpotTrap(spot)) {
               this.spotHomeGoing = null;
               this.homeGoingTimeHomeGoing = 0L;
               if (canFlag(this.homeTrapTime, homeGoingTime)) {
                  this.handleHomeCheck(homeGoingTime, 0L);
               }

               return TpaFlow.Action.INSTANCE2;
            } else {
               if (this.spotHomeGoing == null || spot.getAmount(this.spotHomeGoing) > 1.0) {
                  this.spotHomeGoing = spot;
                  this.homeGoingTimeHomeGoing = homeGoingTime;
               } else if (homeGoingTime - this.homeGoingTimeHomeGoing >= 1500L) {
                  this.handleSpotTrap(spot, homeGoingTime);
               }

               return TpaFlow.Action.INSTANCE2;
            }
         }
      } else {
         return TpaFlow.Action.INSTANCE2;
      }
   }

   public int getCooldownWaiting() {
      return this.waitingCooldownMap.size();
   }

   public static boolean canFlag(long tpaFlowValue, long tpaFlowLimit) {
      return tpaFlowValue != 0L && tpaFlowLimit >= tpaFlowValue;
   }

   public void handleCheckHome(boolean homeFlag) {
      this.updateTimeHome();
      this.flowState = homeFlag ? TpaFlow.State.CHECK_HOME : TpaFlow.State.IDLE;
   }

   private long getMax(long tpaFlowValue) {
      while (!this.arrayDeque.isEmpty() && tpaFlowValue - this.arrayDeque.peekFirst() >= 60000L) {
         this.arrayDeque.pollFirst();
      }

      long tpaFlowBound = this.pendingDeadlineMs == 0L ? tpaFlowValue : this.pendingDeadlineMs;
      if (this.arrayDeque.size() >= 10) {
         tpaFlowBound = Math.max(tpaFlowBound, this.arrayDeque.peekFirst() + 60000L);
      }

      if (this.lettersNumbersTime != 0L) {
         tpaFlowBound = Math.max(tpaFlowBound, this.lettersNumbersTime + 1500L);
      }

      return tpaFlowBound;
   }

   private void handleStuckCooldown(String stuckText) {
      this.handleAskAsking(TpaFlow.State.STUCK);
      this.stuckText = stuckText;
      this.waitingCooldownMap.clear();
      this.updateHomeTime();
   }

   private void handleRespawnOnce(long respawnOnceValue) {
      this.diedOnceCount++;
      this.waitingCooldownMap.clear();
      this.spotHomeGoing = null;
      this.updateHomeTime();
      this.handleAskAsking(TpaFlow.State.RESPAWN);
      this.respawnTime = respawnOnceValue + 1000L;
      this.respawnValue = 0;
   }

   public TpaFlow.Action getInputTpahere(TpaFlow.Input input, TpaFlow.Settings settings) {
      if (input != null && settings != null && this.flowState != TpaFlow.State.IDLE) {
         long time = input.now();
         this.input = input;
         this.inputTpahereStringSet = null;

         TpaFlow.Action inputTpahere;
         try {
            if (input.worldChanged()) {
               this.inputTpahereValue = time;
            }

            if (input.dead() && !this.inputTpahereActive) {
               this.inputTpahereActive = true;
               if (this.spotSpotTrap != null && this.flowState != TpaFlow.State.RESPAWN && this.flowState != TpaFlow.State.STUCK) {
                  this.handleRespawnOnce(time);
               }
            } else if (!input.dead()) {
               this.inputTpahereActive = false;
            }

            this.tpahereServerText = null;
            if (this.flowState != TpaFlow.State.STUCK && input.lines() != null) {
               for (String inputTpahereText : input.lines()) {
                  this.handleHomeGoing(inputTpahereText, time, settings);
               }
            }

            if (this.tpahereServerText == null) {
               return switch (this.flowState) {
                  case CHECK_HOME -> this.getLettersNumbers(input, settings, time);
                  case REQUESTING -> this.getAskGoing(input, settings, time);
                  case RESPAWN -> this.getRespawnInstance(input, settings, time);
                  case WAIT_AFTER_DEATH -> this.getInstanceStuck(input, time);
                  case GO_HOME -> this.getHomeGoing(input, settings, time);
                  default -> TpaFlow.Action.INSTANCE2;
               };
            }

            inputTpahere = new TpaFlow.Action(TpaFlow.Action.Kind.STOP, this.tpahereServerText);
         } finally {
            this.input = null;
            this.inputTpahereStringSet = null;
         }

         return inputTpahere;
      } else {
         return TpaFlow.Action.INSTANCE2;
      }
   }

   private TpaFlow.Action getRespawnInstance(TpaFlow.Input input, TpaFlow.Settings settings, long respawnValue) {
      if (!input.dead()) {
         this.handleAskAsking(TpaFlow.State.WAIT_AFTER_DEATH);
         this.respawnStuckTime = respawnValue + settings.waitAfterDeathS() * 1000L;
         return TpaFlow.Action.INSTANCE2;
      } else if (!canFlag(this.respawnTime, respawnValue)) {
         return TpaFlow.Action.INSTANCE2;
      } else if (this.respawnValue >= 10) {
         this.handleStuckCooldown("Could not respawn");
         return TpaFlow.Action.INSTANCE2;
      } else {
         this.respawnValue++;
         this.respawnTime = respawnValue + 1000L;
         return TpaFlow.Action.INSTANCE;
      }
   }

   public String getStuckText() {
      return this.stuckText;
   }

   private void handleRequestingGoing(long requestingGoingValue) {
      this.handleAskAsking(TpaFlow.State.REQUESTING);
      this.pausedGoingTime = 0L;
      this.pendingDeadlineMs = Math.max(this.pendingDeadlineMs, requestingGoingValue + 1000L);
   }

   public ScissorTgtHandler.Snapshot getTrapYour(long trapYourValue, TpaFlow.Settings settings) {
      return switch (this.flowState) {
         case IDLE -> this.getMax(ScissorTgtHandler.Tone.PAUSED, "Ready", "Stand in your trap, then switch it off and on.");
         case CHECK_HOME -> this.getMax(ScissorTgtHandler.Tone.ACTIVE, "Starting", "Saving your trap spot");
         case REQUESTING -> this.getAskingPaused(trapYourValue, settings);
         case RESPAWN -> this.getMax(ScissorTgtHandler.Tone.PAUSED, "Respawning", this.formatOnceDied() + " \u00b7 respawning");
         case WAIT_AFTER_DEATH -> this.getMax(
            ScissorTgtHandler.Tone.PAUSED,
            "Waiting",
            this.formatOnceDied() + " \u00b7 " + "/" + formatHome(settings == null ? "" : settings.homeName()) + " in " + formatText(getTime(this.respawnStuckTime, trapYourValue))
         );
         case GO_HOME -> this.getGoingHome(trapYourValue, settings);
         case STUCK -> this.getMax(ScissorTgtHandler.Tone.DANGER, "Stuck", this.stuckText);
      };
   }

   public static String formatText(long tpaFlowBound) {
      long tpaFlowLimit = (Math.max(0L, tpaFlowBound) + 999L) / 1000L;
      return tpaFlowLimit >= 120L ? (tpaFlowLimit + 59L) / 60L + "m" : tpaFlowLimit + "s";
   }

   private void handleHomeGoing(long homeGoingTime) {
      this.handleAskAsking(TpaFlow.State.GO_HOME);
      this.homeGoingValue = 0;
      this.checkHomeBackoffMs = 5000L;
      this.updateHomeTime();
      this.homeTrapTime = 0L;
      this.homeGoingTimeHomeCheck = homeGoingTime;
      this.spotHomeGoing = null;
      this.homeGoingTimeHomeGoing = 0L;
      this.pausedGoingTime = 0L;
   }

   private boolean canSpotTrap(TpaFlow.Spot spot) {
      double spotTrapValue = spot.getAmount(this.spotSpotTrap);
      return spotTrapValue <= 4.0 ? true : spotTrapValue <= 16.0 && (this.homeTrapFlag || !spot.canDim(this.spot) || spot.getAmount(this.spot) > 16.0);
   }

   public int getValue() {
      return this.spotTrapCount;
   }

   private long getTimeGoing(long goingValue) {
      boolean goingFlag = this.inputTpahereValue != 0L && goingValue - this.inputTpahereValue < 10000L;
      return this.pausedGoingTime + (goingFlag ? 10000L : 5000L);
   }

   private void handleHomeCheck(long homeCheckValue, long homeCheckBound) {
      this.updateHomeTime();
      if (this.homeGoingValue >= 4) {
         this.handleStuckCooldown("Could not get home. Check Home Name");
      } else {
         this.homeGoingTimeHomeCheck = homeCheckValue + Math.max(this.checkHomeBackoffMs, homeCheckBound);
         this.checkHomeBackoffMs = Math.min(this.checkHomeBackoffMs * 2L, 60000L);
      }
   }

   private void updateTimeHome() {
      this.askServerMap.clear();
      this.waitingCooldownMap.clear();
      this.arrayDeque.clear();
      this.spotSpotTrap = this.spot = this.spotHomeGoing = null;
      this.askingTpahereText = this.stuckText = this.askingAskText = this.cooldownAskText = this.tpahereServerText = null;
      this.lettersNumbersTime = this.askSentTimeMs = this.homeLettersTime = this.pendingDeadlineMs = this.cooldownPausedTime = this.respawnStuckTime = this.respawnTime = 0L;
      this.homeGoingTime = this.homeTrapTime = this.homeGoingTimeHomeCheck = this.homeGoingTimeHomeGoing = this.pausedGoingTime = this.inputTpahereValue = this.homeValue = 0L;
      this.serverSaidValue = 15000L;
      this.checkHomeBackoffMs = 5000L;
      this.saidServerValue = this.respawnValue = this.homeGoingValue = this.cooldownCount = this.homeCount = this.diedOnceCount = this.spotTrapCount = 0;
      this.inputTpahereActive = this.goingPausedFlag = this.cooldownActive = this.homeTrapFlag = false;
      this.input = null;
      this.inputTpahereStringSet = null;
   }

   private String formatOnceDied() {
      return this.diedOnceCount == 1 ? "Died once" : "Died " + this.diedOnceCount + " times";
   }

   private void handleCooldownWaiting(Set<String> set, long cooldownWaitingValue) {
      if (!set.isEmpty() && !this.waitingCooldownMap.isEmpty()) {
         for (String cooldownWaitingText : set) {
            TpaFlow.Ask waitingcooldownmap = this.waitingCooldownMap.remove(cooldownWaitingText);
            if (waitingcooldownmap != null && waitingcooldownmap.until() >= cooldownWaitingValue) {
               this.homeCount++;
               this.serverSaidValue = 15000L;
               this.saidServerValue = 0;
            }
         }
      }
   }

   private TpaFlow.Action getInstanceStuck(TpaFlow.Input input, long stuckValue) {
      if (!input.dead() && canFlag(this.respawnStuckTime, stuckValue)) {
         if (input.at() != null && input.at().getAmount(this.spotSpotTrap) <= 4.0) {
            this.handleRequestingGoing(stuckValue);
         } else {
            this.handleHomeGoing(stuckValue);
         }

         return TpaFlow.Action.INSTANCE2;
      } else {
         return TpaFlow.Action.INSTANCE2;
      }
   }

   private boolean canAskingTpahere(String askingTpahereText, boolean askingTpahereFlag) {
      return TpaReplies.isTeleportCommandForPlayer(askingTpahereText, this.askingTpahereText, askingTpahereFlag);
   }

   public static boolean canCompile(String compileText) {
      return compileText != null && compile.matcher(compileText.trim()).matches();
   }

   private boolean isContains(String containsText) {
      if (this.inputTpahereStringSet == null) {
         TpaFlow.Input input = this.input;
         this.inputTpahereStringSet = getSet((Collection<String>)(input != null && input.online() != null ? input.online().get() : null));
      }

      return this.inputTpahereStringSet.contains(containsText);
   }

   private TpaFlow.Action getAskGoing(TpaFlow.Input input, TpaFlow.Settings settings, long pausedGoingTime) {
      TpaFlow.Spot at = input.at();
      if (!input.dead() && at != null) {
         Set set = getSet(input.near());
         this.handleCooldownWaiting(set, pausedGoingTime);
         this.waitingCooldownMap.values().removeIf(nowx -> nowx.until() <= pausedGoingTime);
         if (!at.canDim(this.spotSpotTrap)) {
            this.handleHomeGoing(pausedGoingTime);
            return TpaFlow.Action.INSTANCE2;
         } else {
            this.goingPausedFlag = input.screen();
            if (at.getAmount(this.spotSpotTrap) > 16.0) {
               if (this.pausedGoingTime == 0L) {
                  this.pausedGoingTime = pausedGoingTime;
               }

               if (pausedGoingTime >= this.getTimeGoing(pausedGoingTime)) {
                  this.handleHomeGoing(pausedGoingTime);
               }

               return TpaFlow.Action.INSTANCE2;
            } else {
               this.pausedGoingTime = 0L;
               if (this.goingPausedFlag) {
                  this.pendingDeadlineMs = Math.max(this.pendingDeadlineMs, pausedGoingTime + 1000L);
                  return TpaFlow.Action.INSTANCE2;
               } else if (pausedGoingTime >= this.getMax(pausedGoingTime) && (!settings.waitForAccept() || this.waitingCooldownMap.isEmpty())) {
                  Predicate skip = settings.skip();
                  Predicate predicate = obj -> set.contains(TpaPicker.getLowerCase(((java.lang.String) (obj)))) || skip != null && skip.test(obj);
                  Collection collection = input.online() == null ? null : input.online().get();
                  String askingTpahereText = TpaPicker.formatText(collection, input.self(), predicate, this.askServerMap, pausedGoingTime, this.random);
                  if (askingTpahereText == null) {
                     this.askingAskText = "No one left to ask";
                     this.pendingDeadlineMs = pausedGoingTime + 5000L;
                     return TpaFlow.Action.INSTANCE2;
                  } else {
                     this.askingAskText = null;
                     this.cooldownAskText = null;
                     this.cooldownPausedTime = 0L;
                     String lowerCase = TpaPicker.getLowerCase(askingTpahereText);
                     this.askServerMap.put(lowerCase, pausedGoingTime + settings.cooldownMin() * 60000L);
                     TpaPicker.handle(this.askServerMap, pausedGoingTime, 512);
                     this.waitingCooldownMap.put(lowerCase, new TpaFlow.Ask(askingTpahereText, pausedGoingTime + 60000L));
                     this.askingTpahereText = askingTpahereText;
                     this.askSentTimeMs = pausedGoingTime;
                     this.cooldownActive = true;
                     this.cooldownCount++;
                     this.arrayDeque.addLast(pausedGoingTime);
                     this.pendingDeadlineMs = pausedGoingTime + settings.intervalS() * 1000L;
                     return this.getLettersNumbers("tpahere " + askingTpahereText, pausedGoingTime);
                  }
               } else {
                  return TpaFlow.Action.INSTANCE2;
               }
            }
         }
      } else {
         return TpaFlow.Action.INSTANCE2;
      }
   }

   private void handleHomeGoing(String homeGoingText, long homeGoingValue, TpaFlow.Settings settings) {
      TpaReplies.Reply textsRequest = TpaReplies.classifyReply(homeGoingText);
      if (textsRequest != TpaReplies.Reply.NONE && !TpaReplies.isPlayerChatMessage(homeGoingText, this::isContains)) {
         if (this.flowState != TpaFlow.State.GO_HOME || this.homeGoingTime == 0L || !this.canHomeServer(textsRequest, homeGoingText, homeGoingValue, settings)) {
            this.handleServerSaid(textsRequest, homeGoingText, homeGoingValue);
         }
      }
   }

   public int getDiedOnceCount() {
      return this.diedOnceCount;
   }

   private static Set<String> getSet(Collection<String> collection) {
      HashSet hashSet = new HashSet();
      if (collection != null) {
         for (String tpaFlowText : collection) {
            if (tpaFlowText != null) {
               hashSet.add(TpaPicker.getLowerCase(tpaFlowText));
            }
         }
      }

      return hashSet;
   }

   public int getCooldownCount() {
      return this.cooldownCount;
   }

   private void handleMaxCooldown(String cooldownText) {
      this.waitingCooldownMap.remove(cooldownText);
      if (this.cooldownActive) {
         this.cooldownCount = Math.max(0, this.cooldownCount - 1);
         this.cooldownActive = false;
      }
   }

   private void updateHomeTime() {
      this.homeGoingTime = 0L;
      this.homeValue = 0L;
      this.homeTrapFlag = false;
      this.spotHomeGoing = null;
      this.homeGoingTimeHomeGoing = 0L;
   }

   private TpaFlow.Action getLettersNumbers(TpaFlow.Input input, TpaFlow.Settings settings, long lettersNumbersValue) {
      if (input.dead() || input.at() == null) {
         return TpaFlow.Action.INSTANCE2;
      } else if (!canCompile(settings.homeName())) {
         this.handleStuckCooldown("Home Name can only use letters, numbers, - and _");
         return TpaFlow.Action.INSTANCE2;
      } else {
         this.spotSpotTrap = input.at();
         this.handleAskAsking(TpaFlow.State.REQUESTING);
         this.pendingDeadlineMs = lettersNumbersValue + 1000L;
         return TpaFlow.Action.INSTANCE2;
      }
   }

   public TpaFlow.State getState() {
      return this.flowState;
   }

   public String getAskingTpahereText() {
      return this.askingTpahereText;
   }

   private TpaFlow.Action getLettersNumbers(String lettersNumbersText, long lettersNumbersTime) {
      this.lettersNumbersTime = lettersNumbersTime;
      return new TpaFlow.Action(TpaFlow.Action.Kind.COMMAND, lettersNumbersText);
   }

   public static String formatSethomeTrap(String sethomeTrapText) {
      String sethomeTrapText2 = sethomeTrapText == null ? "" : sethomeTrapText.trim();
      return sethomeTrapText2.isEmpty() ? "No home set. Use /sethome in the trap" : "No home called " + sethomeTrapText2 + ". Use /sethome " + sethomeTrapText2 + " in the trap";
   }

   public record Action(TpaFlow.Action.Kind kind, String text) {
      public static final TpaFlow.Action INSTANCE2 = new TpaFlow.Action(TpaFlow.Action.Kind.NONE, null);
      public static final TpaFlow.Action INSTANCE = new TpaFlow.Action(TpaFlow.Action.Kind.RESPAWN, null);

      public static enum Kind {
         NONE,
         COMMAND,
         RESPAWN,
         STOP;
      }
   }

   private record Ask(String name, long until) {
   }

   public record Input(
      long now,
      boolean dead,
      boolean screen,
      boolean menu,
      TpaFlow.Spot at,
      boolean teleported,
      boolean worldChanged,
      String self,
      Supplier<? extends Collection<String>> online,
      Collection<String> near,
      List<String> lines
   ) {
   }

   public record Settings(String homeName, int intervalS, int cooldownMin, int waitAfterDeathS, boolean waitForAccept, Predicate<String> skip) {
      public Settings(String homeName, int intervalS, int cooldownMin, int waitAfterDeathS, boolean waitForAccept, Predicate<String> skip) {
         homeName = homeName == null ? "" : homeName.trim();
         intervalS = Math.max(3, intervalS);
         cooldownMin = Math.max(1, cooldownMin);
         waitAfterDeathS = Math.max(0, waitAfterDeathS);
         this.homeName = homeName;
         this.intervalS = intervalS;
         this.cooldownMin = cooldownMin;
         this.waitAfterDeathS = waitAfterDeathS;
         this.waitForAccept = waitForAccept;
         this.skip = skip;
      }
   }

   public record Spot(String dim, double x, double y, double z) {
      public double getAmount(TpaFlow.Spot o) {
         if (!this.canDim(o)) {
            return Double.POSITIVE_INFINITY;
         } else {
            double deltaX = this.x - o.x;
            double deltaY = this.y - o.y;
            double deltaZ = this.z - o.z;
            return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
         }
      }

      public boolean canDim(TpaFlow.Spot o) {
         return o != null && Objects.equals(this.dim, o.dim);
      }
   }

   public static enum State {
      IDLE,
      CHECK_HOME,
      REQUESTING,
      RESPAWN,
      WAIT_AFTER_DEATH,
      GO_HOME,
      STUCK;
   }
}
