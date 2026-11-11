package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import com.corz.client.categories.donutsmp.StaffList;

public final class StaffPresenceTracker {
   private Set<UUID> previousPlayerIds;
   private final Map<UUID, StaffPresenceTracker.SeenPlayer> seenPlayers = new HashMap<>();

   public Set<String> getOnlinePlayerNames(MinecraftClient client) {
      HashSet hashSet = new HashSet();
      if (client.getNetworkHandler() == null) {
         return hashSet;
      } else {
         for (PlayerListEntry playerListEntry : client.getNetworkHandler().getPlayerList()) {
            if (playerListEntry.getProfile() != null && playerListEntry.getProfile().name() != null) {
               hashSet.add(playerListEntry.getProfile().name().toLowerCase(Locale.ROOT));
            }
         }

         return hashSet;
      }
   }

   public void reset() {
      this.seenPlayers.clear();
      this.previousPlayerIds = Set.of();
   }

   public List<StaffPresenceTracker.Departure> pollStaffDepartures(MinecraftClient client, long profileNetworkValue) {
      if (client.player != null && client.getNetworkHandler() != null) {
         HashSet hashSet = new HashSet();

         for (PlayerListEntry playerListEntry : client.getNetworkHandler().getListedPlayerListEntries()) {
            if (playerListEntry != null && playerListEntry.getProfile() != null && playerListEntry.getProfile().id() != null) {
               UUID id = playerListEntry.getProfile().id();
               if (!id.equals(client.player.getUuid())) {
                  hashSet.add(id);
                  String name = playerListEntry.getProfile().name();
                  StaffPresenceTracker.SeenPlayer seenPlayer = this.seenPlayers.get(id);
                  boolean profileNetworkFlag = isStaffName(name) || StaffList.isStaffPlayer(id, name) || seenPlayer != null && seenPlayer.staff;
                  this.seenPlayers
                     .put(
                        id, seenPlayer == null ? new StaffPresenceTracker.SeenPlayer(name, profileNetworkValue, profileNetworkFlag) : new StaffPresenceTracker.SeenPlayer(name, seenPlayer.firstSeenMs, profileNetworkFlag)
                     );
               }
            }
         }

         ArrayList arrayList = new ArrayList();

         for (UUID uuid : this.previousPlayerIds) {
            if (!hashSet.contains(uuid)) {
               StaffPresenceTracker.SeenPlayer seenPlayer2 = this.seenPlayers.get(uuid);
               if (seenPlayer2 != null && seenPlayer2.staff && profileNetworkValue - seenPlayer2.firstSeenMs >= 2000L) {
                  arrayList.add(new StaffPresenceTracker.Departure(uuid, seenPlayer2.name));
               }
            }
         }

         this.previousPlayerIds = Set.copyOf(hashSet);
         if (this.seenPlayers.size() > 1024) {
            this.seenPlayers.keySet().retainAll(hashSet);
         }

         return arrayList;
      } else {
         return List.of();
      }
   }

   public boolean isStaff(UUID uuid) {
      StaffPresenceTracker.SeenPlayer seenPlayer = this.seenPlayers.get(uuid);
      return seenPlayer != null && seenPlayer.staff;
   }

   public StaffPresenceTracker() {
      this.previousPlayerIds = Set.of();
   }

   public String getKnownName(UUID uuid) {
      StaffPresenceTracker.SeenPlayer seenPlayer = this.seenPlayers.get(uuid);
      return seenPlayer == null ? null : seenPlayer.name;
   }

   public UUID findIdByName(String rosterTimelineText) {
      if (rosterTimelineText == null) {
         return null;
      } else {
         for (Entry entry : this.seenPlayers.entrySet()) {
            if (((StaffPresenceTracker.SeenPlayer)entry.getValue()).name.equalsIgnoreCase(rosterTimelineText)) {
               return (UUID)entry.getKey();
            }
         }

         return null;
      }
   }

   public static boolean isStaffName(String daveCryptoText) {
      if (daveCryptoText == null) {
         return false;
      } else {
         for (String daveCryptoText2 : StaffList.cryptoDaveTexts) {
            if (daveCryptoText2 != null && daveCryptoText2.equalsIgnoreCase(daveCryptoText.trim())) {
               return true;
            }
         }

         return false;
      }
   }

   public record Departure(UUID id, String name) {
   }

   private record SeenPlayer(String name, long firstSeenMs, boolean staff) {
   }
}
