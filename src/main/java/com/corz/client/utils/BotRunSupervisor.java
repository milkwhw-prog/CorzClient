package com.corz.client.utils;

import java.util.ArrayDeque;

public final class BotRunSupervisor {
   private long lastSetbackCheckMs;
   private long lastPacketsHeldTick;
   private boolean armingPending;
   private final ArrayDeque<Long> setbackTimesMs;
   private long respawnGraceUntilMs;
   private boolean packetsHeld;
   private Object sessionId;
   private Object worldId;
   private BotRunSupervisor.Verdict stoppedVerdict;
   private long expectedPearlTeleportMs = -1L;
   private long setbackIgnoreUntilMs;
   private long lastScreenOpenTick;
   private long lastSetbackMs;
   private long lastPearlTeleportMs = -1L;
   private boolean menuOpen;
   private Object playerId;
   private final ArrayDeque<Long> errorTimesMs = new ArrayDeque<>();
   private boolean lowTpsActive;

   private void recordSetbackTime(long max) {
      if (max >= 0L && max >= this.lastSetbackMs) {
         boolean networkFlag = max > this.lastSetbackMs;
         this.lastSetbackMs = max;
         if (max != this.lastPearlTeleportMs && this.matchesExpectedPearlTeleport(max)) {
            this.markPearlTeleportHandled(max);
         } else if (networkFlag && max - this.lastSetbackCheckMs >= 3000L) {
            this.setbackTimesMs.addLast(max);
         }
      }
   }

   public BotRunSupervisor.Verdict evaluateProbe(BotRunSupervisor.Probe probe) {
      if (this.stoppedVerdict != null) {
         return this.stoppedVerdict;
      } else {
         long time = probe.nowMs();
         if (this.sessionId != null && probe.sessionId() == this.sessionId) {
            pruneOldEntries(this.errorTimesMs, time);
            if (this.errorTimesMs.size() >= 3) {
               return this.stopWithReason(BotRunSupervisor.Reason.ERROR_LOOP, false);
            } else {
               this.trackMenuHeldPacketsAndTps(probe);
               if (this.armingPending) {
                  this.armingPending = false;
                  this.worldId = probe.worldId();
                  this.playerId = probe.playerId();
                  this.lastSetbackMs = probe.lastSetbackMs();
                  return buildPausedVerdict(BotRunSupervisor.Reason.ARMING, "", true);
               } else {
                  long lastSetbackMs = probe.lastSetbackMs();
                  if (probe.worldId() == this.worldId && probe.playerId() == this.playerId) {
                     this.recordSetbackTime(lastSetbackMs);
                  } else {
                     this.lastSetbackMs = Math.max(this.lastSetbackMs, lastSetbackMs);
                  }

                  pruneOldEntries(this.setbackTimesMs, time);
                  if (this.setbackTimesMs.size() >= 6) {
                     return this.stopWithReason(BotRunSupervisor.Reason.SETBACK_LOOP, false);
                  } else if (probe.worldId() != this.worldId) {
                     this.worldId = probe.worldId();
                     this.playerId = probe.playerId();
                     this.setbackIgnoreUntilMs = time + 2000L;
                     this.lastSetbackCheckMs = time;
                     return buildPausedVerdict(BotRunSupervisor.Reason.LOADING, "", true);
                  } else if (time < this.setbackIgnoreUntilMs) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.LOADING, "", false);
                  } else if (!probe.alive()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.DEAD, "", false);
                  } else if (probe.playerId() != this.playerId) {
                     this.playerId = probe.playerId();
                     this.respawnGraceUntilMs = time + 1500L;
                     this.lastSetbackCheckMs = time;
                     return buildPausedVerdict(BotRunSupervisor.Reason.RESPAWN, "", true);
                  } else if (time < this.respawnGraceUntilMs) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.RESPAWN, "", false);
                  } else if (probe.otherBot() != null) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.OTHER_BOT, probe.otherBot(), false);
                  } else if (probe.conflict() != null) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.CONFLICT, probe.conflict(), false);
                  } else if (this.menuOpen) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.MENU, "", false);
                  } else if (probe.freecam()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.FREECAM, "", false);
                  } else if (probe.userOverride()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.USER, "", false);
                  } else if (probe.otherMacro()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.OTHER_MACRO, "", false);
                  } else if (probe.creative()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.CREATIVE, "", false);
                  } else if (probe.spectator()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.SPECTATOR, "", false);
                  } else if (probe.riding()) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.RIDING, "", false);
                  } else if (this.packetsHeld) {
                     return buildPausedVerdict(BotRunSupervisor.Reason.NET_HELD, "", false);
                  } else if (lastSetbackMs >= 0L && lastSetbackMs != this.lastPearlTeleportMs && time - lastSetbackMs < 350L) {
                     return new BotRunSupervisor.Verdict(BotRunSupervisor.Level.ACTIONS_PAUSED, BotRunSupervisor.Reason.SETBACK, "", false, false, false);
                  } else if (probe.timeStale()) {
                     return buildActionsPausedVerdict(BotRunSupervisor.Reason.SERVER_LAG);
                  } else if (this.lowTpsActive) {
                     return buildActionsPausedVerdict(BotRunSupervisor.Reason.LOW_TPS);
                  } else {
                     return probe.foreignTurnStreak() >= 3
                        ? buildActionsPausedVerdict(BotRunSupervisor.Reason.FOREIGN_ROTATION)
                        : new BotRunSupervisor.Verdict(BotRunSupervisor.Level.RUN, BotRunSupervisor.Reason.NONE, "", false, true, false);
                  }
               }
            }
         } else {
            return this.stopWithReason(BotRunSupervisor.Reason.NEW_SESSION, this.sessionId == null);
         }
      }
   }

   public void setExpectedPearlTeleportMs(long time) {
      this.expectedPearlTeleportMs = time;
   }

   private static BotRunSupervisor.Verdict buildActionsPausedVerdict(BotRunSupervisor.Reason reason) {
      return new BotRunSupervisor.Verdict(BotRunSupervisor.Level.ACTIONS_PAUSED, reason, "", false, true, false);
   }

   private void trackMenuHeldPacketsAndTps(BotRunSupervisor.Probe probe) {
      if (probe.screenOpen()) {
         this.menuOpen = true;
         this.lastScreenOpenTick = probe.tick();
      } else if (this.menuOpen && !isWithinTwoTicks(probe.tick(), this.lastScreenOpenTick)) {
         this.menuOpen = false;
      }

      if (probe.outboundHeld() || probe.inboundHeld()) {
         this.packetsHeld = true;
         this.lastPacketsHeldTick = probe.tick();
      } else if (this.packetsHeld && !isWithinTwoTicks(probe.tick(), this.lastPacketsHeldTick)) {
         this.packetsHeld = false;
      }

      double tps = probe.tps();
      if (!Double.isNaN(tps)) {
         if (this.lowTpsActive) {
            if (tps >= 12.0) {
               this.lowTpsActive = false;
            }
         } else if (tps < 10.0) {
            this.lowTpsActive = true;
         }
      }
   }

   private void markPearlTeleportHandled(long time) {
      this.lastPearlTeleportMs = time;
      this.expectedPearlTeleportMs = -1L;
      this.setbackTimesMs.removeLastOccurrence(time);
   }

   private static void pruneOldEntries(ArrayDeque<Long> arrayDeque, long networkValue) {
      while (!arrayDeque.isEmpty() && networkValue - arrayDeque.peekFirst() >= 10000L) {
         arrayDeque.removeFirst();
      }
   }

   public int getRecentSetbackCount() {
      return this.setbackTimesMs.size();
   }

   public void beginSession(Object networkHandler, long time) {
      this.sessionId = networkHandler;
      this.armingPending = networkHandler != null;
      this.lastSetbackCheckMs = time;
      this.worldId = null;
      this.playerId = null;
      this.setbackIgnoreUntilMs = 0L;
      this.respawnGraceUntilMs = 0L;
      this.lastSetbackMs = -1L;
      this.expectedPearlTeleportMs = -1L;
      this.lastPearlTeleportMs = -1L;
      this.menuOpen = false;
      this.packetsHeld = false;
      this.lowTpsActive = false;
      this.stoppedVerdict = null;
      this.errorTimesMs.clear();
      this.setbackTimesMs.clear();
   }

   public boolean isSessionRunning() {
      return this.sessionId != null && this.stoppedVerdict == null;
   }

   public BotRunSupervisor() {
      this.setbackTimesMs = new ArrayDeque<>();
   }

   public boolean hasRepeatedErrors() {
      if (this.errorTimesMs.isEmpty()) {
         return false;
      } else {
         long peekLast = this.errorTimesMs.peekLast();
         int dequeArrayCounter = 0;

         for (long dequeArrayValue : this.errorTimesMs) {
            if (peekLast - dequeArrayValue < 10000L) {
               dequeArrayCounter++;
            }
         }

         return dequeArrayCounter >= 3;
      }
   }

   private boolean matchesExpectedPearlTeleport(long networkLimit) {
      if (this.expectedPearlTeleportMs >= 0L && networkLimit >= 0L) {
         long networkLimit2 = networkLimit - this.expectedPearlTeleportMs;
         return networkLimit2 >= -1000L && networkLimit2 <= 250L;
      } else {
         return false;
      }
   }

   public void recordError(long dequeArrayValue) {
      this.errorTimesMs.addLast(dequeArrayValue);

      while (this.errorTimesMs.size() > 12) {
         this.errorTimesMs.removeFirst();
      }
   }

   private static boolean isWithinTwoTicks(long networkLimit, long networkValue) {
      return networkLimit >= networkValue && networkLimit - networkValue <= 2L;
   }

   private BotRunSupervisor.Verdict stopWithReason(BotRunSupervisor.Reason reason, boolean networkFlag) {
      this.stoppedVerdict = new BotRunSupervisor.Verdict(BotRunSupervisor.Level.STOP, reason, "", true, false, networkFlag);
      this.armingPending = false;
      return this.stoppedVerdict;
   }

   private static BotRunSupervisor.Verdict buildPausedVerdict(BotRunSupervisor.Reason reason, String pausedText, boolean pausedFlag) {
      return new BotRunSupervisor.Verdict(BotRunSupervisor.Level.PAUSED, reason, pausedText, pausedFlag, false, false);
   }

   public static enum Level {
      RUN,
      ACTIONS_PAUSED,
      PAUSED,
      STOP;
   }

   public record Probe(
      long nowMs,
      long tick,
      Object sessionId,
      Object worldId,
      Object playerId,
      boolean alive,
      boolean screenOpen,
      boolean freecam,
      long lastSetbackMs,
      boolean outboundHeld,
      boolean inboundHeld,
      boolean timeStale,
      double tps,
      boolean userOverride,
      boolean otherMacro,
      String otherBot,
      String conflict,
      boolean creative,
      boolean spectator,
      boolean riding,
      int foreignTurnStreak
   ) {
   }

   public static enum Reason {
      NONE("Fighting"),
      IDLE("Looking for a target"),
      ARMING("Starting"),
      MENU("Paused - menu open"),
      USER("Paused - you have control"),
      FREECAM("Paused - Freecam is on"),
      DEAD("Waiting to respawn"),
      RESPAWN("Respawned - getting ready"),
      LOADING("Loading the world"),
      SETBACK("Server corrected movement"),
      NET_HELD("Paused - packets are being held"),
      SERVER_LAG("Waiting - server lag"),
      LOW_TPS("Server is slow - not attacking"),
      OTHER_MACRO("Paused - another macro is acting"),
      OTHER_BOT("Turn off %s to use this bot"),
      CONFLICT("Turn off %s to let the bot fight"),
      CREATIVE("Switch to survival to fight"),
      SPECTATOR("Spectating - bot idle"),
      RIDING("Get off the mount to fight"),
      FOREIGN_ROTATION("Another module is turning you"),
      NEW_SESSION("Turned off - new server session"),
      SETBACK_LOOP("Server keeps pulling you back - stopped"),
      ERROR_LOOP("Stopped after an error");

      public final String statusMessage;

      private Reason(String teleportingModuleText) {
         this.statusMessage = teleportingModuleText;
      }
   }

   public record Verdict(BotRunSupervisor.Level level, BotRunSupervisor.Reason reason, String detail, boolean resetState, boolean moveAllowed, boolean quiet) {
   }
}
