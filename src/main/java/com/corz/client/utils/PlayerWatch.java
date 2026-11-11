package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PlayerWatch {
   private final Map<String, PlayerWatch.Track> trackByName = new HashMap<>();
   private long lastThreatSeenTick;
   private PlayerWatch.Threat lastThreat;
   private final Set<String> currentNames = new HashSet<>();
   private List<PlayerWatch.Threat> currentThreats = List.of();

   public void reset() {
      this.trackByName.clear();
      this.currentThreats = List.of();
      this.lastThreat = null;
      this.lastThreatSeenTick = Long.MIN_VALUE;
   }

   public List<PlayerWatch.Threat> getThreats() {
      return this.currentThreats;
   }

   public PlayerWatch() {
      this.lastThreatSeenTick = Long.MIN_VALUE;
   }

   public boolean hasThreats() {
      return !this.currentThreats.isEmpty();
   }

   public int getCurrentNameCount() {
      return this.currentNames.size();
   }

   public PlayerWatch.Threat getNearestThreat() {
      return this.currentThreats.isEmpty() ? null : this.currentThreats.get(0);
   }

   public void clearCurrentNames() {
      this.currentNames.clear();
   }

   public PlayerWatch.Threat getRecentThreat(long threatValue) {
      return this.lastThreat != null && threatValue - this.lastThreatSeenTick <= 200L ? this.lastThreat : null;
   }

   public List<PlayerWatch.Threat> updateThreats(long time, double seenThreatValue, double seenThreatValue2, double seenThreatValue3, List<PlayerWatch.Seen> list, double seenThreatFactor, boolean seenThreatFlag) {
      ArrayList arrayList = new ArrayList(2);
      double seenThreatValue4 = seenThreatFactor * seenThreatFactor;
      HashSet hashSet = new HashSet();
      if (list != null) {
         for (PlayerWatch.Seen seen2 : list) {
            if (seen2 != null && seen2.name() != null && !seen2.spectator() && (!seenThreatFlag || !seen2.friend())) {
               double seenThreatFactor2 = seen2.x() - seenThreatValue;
               double seenThreatFactor3 = seen2.y() - seenThreatValue2;
               double seenThreatFactor4 = seen2.z() - seenThreatValue3;
               double seenThreatLimit = seenThreatFactor2 * seenThreatFactor2 + seenThreatFactor3 * seenThreatFactor3 + seenThreatFactor4 * seenThreatFactor4;
               if (!(seenThreatLimit > seenThreatValue4)) {
                  double sqrt = Math.sqrt(seenThreatLimit);
                  String lowerCase = seen2.name().toLowerCase(Locale.ROOT);
                  hashSet.add(lowerCase);
                  PlayerWatch.Track track = this.trackByName.get(lowerCase);
                  long seenThreatValue5 = track != null && time - track.lastSeen() <= 200L ? track.firstSeen() : time;
                  double seenThreatValue6 = track != null && time > track.lastSeen() ? (track.lastDist() - sqrt) / (time - track.lastSeen()) : 0.0;
                  this.trackByName.put(lowerCase, new PlayerWatch.Track(seenThreatValue5, time, sqrt, seen2.x(), seen2.y(), seen2.z()));
                  this.currentNames.add(lowerCase);
                  arrayList.add(new PlayerWatch.Threat(seen2.name(), seen2.x(), seen2.y(), seen2.z(), sqrt, seenThreatValue6, seenThreatValue5));
               }
            }
         }
      }

      this.trackByName.keySet().removeIf(tickx -> !hashSet.contains(tickx) && time - this.trackByName.get(tickx).lastSeen() > 200L);
      arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.utils.PlayerWatch.Threat) obj).dist(), ((com.corz.client.utils.PlayerWatch.Threat) obj2).dist()));
      this.currentThreats = List.copyOf(arrayList);
      if (!this.currentThreats.isEmpty()) {
         this.lastThreat = this.currentThreats.get(0);
         this.lastThreatSeenTick = time;
      }

      return this.currentThreats;
   }

   public record Seen(String name, double x, double y, double z, boolean friend, boolean spectator) {
   }

   public record Threat(String name, double x, double y, double z, double dist, double closing, long firstSeenTick) {
   }

   private record Track(long firstSeen, long lastSeen, double lastDist, double x, double y, double z) {
   }
}
