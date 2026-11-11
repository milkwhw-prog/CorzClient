package com.corz.client.categories.donutsmp;

import com.corz.client.utils.EvidenceAccumulator.Decision;
import com.corz.client.utils.StaffPresenceTracker.Departure;
import com.corz.client.gui.DisabledEnabled;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import com.corz.client.utils.EvidenceAccumulator;
import com.corz.client.utils.StaffPresenceTracker;
import com.corz.client.categories.misc.NameProtect;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public final class AntiVanish extends Module {
   private volatile List<String> commandTargetableNames;
   private Object lastSeenWorld;
   private final StaffPresenceTracker rosterTimeline = new StaffPresenceTracker();
   private int nextCompletionRequestId;
   private final Map<String, Long> lastReportTimes;
   private final ConcurrentLinkedQueue<AntiVanish.Input> pendingInputs;
   private final EvidenceAccumulator evidenceAccumulator = new EvidenceAccumulator();
   private final Set<Integer> pendingCompletionIds;
   private final Deque<AntiVanish.ChatLine> recentChatLines;
   private int scanTickCounter;

   private static long getTimeMillis() {
      return System.currentTimeMillis();
   }

   private boolean hasRecentLeaveMessageFor(String anyMatchText) {
      if (anyMatchText == null) {
         return false;
      } else {
         String lowerCase = anyMatchText.toLowerCase(Locale.ROOT);
         long timeMillis = getTimeMillis();
         return this.recentChatLines
            .stream()
            .anyMatch(
               line -> timeMillis - line.timeMs < 8000L
                  && line.text.toLowerCase(Locale.ROOT).contains(lowerCase)
                  && (
                     line.text.toLowerCase(Locale.ROOT).contains("left")
                        || line.text.toLowerCase(Locale.ROOT).contains("quit")
                        || line.text.toLowerCase(Locale.ROOT).contains("disconnect")
                  )
            );
      }
   }

   private void checkInvisibleStaffMetadata(int invisibleUuidValue) {
      if (mc.world.getEntityById(invisibleUuidValue) instanceof PlayerEntity playerEntity
         && playerEntity != mc.player
         && playerEntity.isInvisible()
         && playerEntity.squaredDistanceTo(mc.player) <= 4096.0
         && this.rosterTimeline.isStaff(playerEntity.getUuid())) {
         this.reportVanishEvidence(
            playerEntity.getUuid(),
            this.rosterTimeline.getKnownName(playerEntity.getUuid()),
            com.corz.client.utils.EvidenceAccumulator.Clue.INVISIBLE_BODY,
            "Invisible state confirmed by metadata"
         );
      }
   }

   private boolean isExplainedByNearbyEntity(Vec3d vec) {
      if (vec != null && !(vec.squaredDistanceTo(mc.player.getEntityPos()) > 4096.0)) {
         for (Entity entity : mc.world.getEntities()) {
            if (entity != mc.player) {
               if (entity instanceof PlayerEntity playerEntity && !playerEntity.isInvisible() && entity.getEntityPos().squaredDistanceTo(vec) <= 16.0) {
                  return true;
               }

               if (entity instanceof ProjectileEntity && entity.getEntityPos().squaredDistanceTo(vec) <= 16.0) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private void reportVanishEvidence(UUID uuid, String vanishAntiText, com.corz.client.utils.EvidenceAccumulator.Clue clue, String vanishAntiText2) {
      Decision nearbyText = this.evidenceAccumulator.addEvidence(uuid, vanishAntiText, clue, vanishAntiText2, getTimeMillis());
      if (nearbyText.alert()) {
         String substring = NameProtect.getSubstring(nearbyText.subject());
         String substringText = substring + " - " + nearbyText.detail() + " (confidence " + nearbyText.score() + ")";
         mc.player.sendMessage(Text.literal("\u00a78[\u00a7cAntiVanish\u00a78] \u00a7f" + substringText), false);
         DisabledEnabled.getInstance().handleDisabledEnabled("AntiVanish", substringText, false);
         mc.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, 1.0F, 1.0F));
      }
   }

   private static boolean isInteractableBlockSound(String leverFenceText) {
      String lowerCase = stripNamespaceToLowerCase(leverFenceText);
      return (
            lowerCase.contains("chest")
               || lowerCase.contains("barrel")
               || lowerCase.contains("shulker_box")
               || lowerCase.contains("door")
               || lowerCase.contains("fence_gate")
               || lowerCase.contains("button")
               || lowerCase.contains("lever")
         )
         && (lowerCase.contains("open") || lowerCase.contains("close") || lowerCase.contains("click") || lowerCase.contains("locked"));
   }

   private UUID getLeftPlayerUuid(Text text2) {
      if (text2 != null
         && text2.getContent() instanceof TranslatableTextContent translatableTextContent
         && "multiplayer.player.left".equals(translatableTextContent.getKey())
         && translatableTextContent.getArgs().length != 0) {
         Object obj = translatableTextContent.getArgs()[0];
         String multiplayerLeftText = obj instanceof Text text3 ? text3.getString() : String.valueOf(obj);
         return this.rosterTimeline.findIdByName(multiplayerLeftText);
      } else {
         return null;
      }
   }

   private void checkUnexplainedSound(Vec3d vec, String unexplainedSoundText) {
      if (isInteractableBlockSound(unexplainedSoundText) && !this.isExplainedByNearbyEntity(vec)) {
         String formatEntityPos = this.formatRelativePosition(vec);
         this.reportThrottledEvidence(
            "sound:" + formatEntityPos + stripNamespaceToLowerCase(unexplainedSoundText), null, formatEntityPos, com.corz.client.utils.EvidenceAccumulator.Clue.WORLD_SOUND, "Unexplained interaction sound", 4000L
         );
      }
   }

   private static String stripNamespaceToLowerCase(String lowerCaseText) {
      if (lowerCaseText == null) {
         return "unknown";
      } else {
         int indexOf = lowerCaseText.indexOf(58);
         return (indexOf < 0 ? lowerCaseText : lowerCaseText.substring(indexOf + 1)).toLowerCase(Locale.ROOT);
      }
   }

   private static String getSoundIdString(SoundEvent soundEvent) {
      return soundEvent != null && soundEvent.id() != null ? soundEvent.id().toString() : "";
   }

   private void scanInvisibleStaffNearby() {
      for (PlayerEntity playerEntity : mc.world.getPlayers()) {
         if (playerEntity != mc.player
            && playerEntity.isInvisible()
            && !(playerEntity.squaredDistanceTo(mc.player) > 4096.0)
            && this.rosterTimeline.isStaff(playerEntity.getUuid())) {
            this.reportVanishEvidence(
               playerEntity.getUuid(), this.rosterTimeline.getKnownName(playerEntity.getUuid()), com.corz.client.utils.EvidenceAccumulator.Clue.INVISIBLE_BODY, "Invisible staff body nearby"
            );
         }
      }
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null && mc.getNetworkHandler() != null) {
         if (this.lastSeenWorld != null && this.lastSeenWorld != mc.world) {
            this.resetState();
         }

         this.lastSeenWorld = mc.world;
         this.scanTickCounter++;
         this.drainPendingInputs();
         if (this.scanTickCounter % 5 == 0) {
            this.checkRosterDepartures();
            this.scanInvisibleStaffNearby();
         }

         if (this.scanTickCounter % 120 == 0) {
            this.sendNameCompletionRequest();
         }

         this.checkCommandTargetableRoster();
         this.expireOldEntries();
      } else {
         this.resetState();
      }
   }

   private void addChatLine(String dequeText) {
      this.recentChatLines.addLast(new AntiVanish.ChatLine(dequeText, getTimeMillis()));

      while (this.recentChatLines.size() > 48) {
         this.recentChatLines.removeFirst();
      }
   }

   private boolean hasHeatSourceNearby(Vec3d vec, String smokeSmokerText) {
      if (!stripNamespaceToLowerCase(smokeSmokerText).contains("smoke")) {
         return false;
      } else {
         BlockPos floored = BlockPos.ofFloored(vec);

         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -2; k <= 2; k++) {
                  Identifier id = Registries.BLOCK.getId(mc.world.getBlockState(floored.add(i, j, k)).getBlock());
                  String lowerCase = stripNamespaceToLowerCase(id == null ? "" : id.toString());
                  if (lowerCase.contains("fire") || lowerCase.contains("torch") || lowerCase.contains("furnace") || lowerCase.contains("smoker") || lowerCase.contains("candle")) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private void checkSpectatorSwitch(UUID uuid, String spectatorSwitchedText) {
      if (uuid != null && this.rosterTimeline.isStaff(uuid) && GameMode.SPECTATOR.getId().equals(spectatorSwitchedText)) {
         this.reportVanishEvidence(uuid, this.rosterTimeline.getKnownName(uuid), com.corz.client.utils.EvidenceAccumulator.Clue.SPECTATOR_SWITCH, "Staff switched to spectator");
      }
   }

   public List<AntiVanish.HudEntry> getHudEntries() {
      return this.evidenceAccumulator
         .getActiveDecisions(getTimeMillis())
         .stream()
         .limit(4L)
         .map(decision -> new AntiVanish.HudEntry(decision.subject(), decision.detail()))
         .toList();
   }

   private void checkInvisibleStaffSound(int invisibleUuidValue, String invisibleUuidText) {
      if (isInteractableBlockSound(invisibleUuidText)) {
         if (mc.world.getEntityById(invisibleUuidValue) instanceof PlayerEntity playerEntity
            && playerEntity != mc.player
            && playerEntity.isInvisible()
            && this.rosterTimeline.isStaff(playerEntity.getUuid())) {
            this.reportVanishEvidence(
               playerEntity.getUuid(),
               this.rosterTimeline.getKnownName(playerEntity.getUuid()),
               com.corz.client.utils.EvidenceAccumulator.Clue.ENTITY_SOUND,
               "Interaction sound from invisible staff"
            );
         }
      }
   }

   private void drainPendingInputs() {
      for (int i = 0; i < 384; i++) {
         AntiVanish.Input concurrentlinkedqueue = this.pendingInputs.poll();
         if (concurrentlinkedqueue == null) {
            break;
         }

         switch (concurrentlinkedqueue.kind) {
            case GAME_MODE:
               this.checkSpectatorSwitch(concurrentlinkedqueue.playerId, concurrentlinkedqueue.detail);
               break;
            case ENTITY_METADATA:
               this.checkInvisibleStaffMetadata(concurrentlinkedqueue.entityId);
               break;
            case ENTITY_SOUND:
               this.checkInvisibleStaffSound(concurrentlinkedqueue.entityId, concurrentlinkedqueue.detail);
               break;
            case WORLD_SOUND:
               this.checkUnexplainedSound(concurrentlinkedqueue.getPosition(), concurrentlinkedqueue.detail);
               break;
            case PARTICLE:
               this.checkUnexplainedParticles(concurrentlinkedqueue.getPosition(), concurrentlinkedqueue.detail);
               break;
            case CHAT:
               this.addChatLine(concurrentlinkedqueue.detail);
               break;
            case CONFIRMED_LEAVE:
               this.evidenceAccumulator.removePlayerCase(concurrentlinkedqueue.playerId);
         }
      }

      while (this.pendingInputs.size() > 2048) {
         this.pendingInputs.poll();
      }
   }

   private void checkUnexplainedParticles(Vec3d vec, String unexplainedParticlesText) {
      if (isPlayerLikeParticle(unexplainedParticlesText) && !this.isExplainedByNearbyEntity(vec) && !this.hasHeatSourceNearby(vec, unexplainedParticlesText)) {
         String formatEntityPos = this.formatRelativePosition(vec);
         this.reportThrottledEvidence(
            "particle:" + formatEntityPos + stripNamespaceToLowerCase(unexplainedParticlesText), null, formatEntityPos, com.corz.client.utils.EvidenceAccumulator.Clue.PARTICLE_TRACE, "Unexplained player-like particles", 4000L
         );
      }
   }

   @Override
   protected void onEnable() {
      this.resetState();
      this.checkRosterDepartures();
   }

   private void checkCommandTargetableRoster() {
      var list = this.commandTargetableNames;
      if (list != null) {
         this.commandTargetableNames = null;
         var profileNetwork = this.rosterTimeline.getOnlinePlayerNames(mc);
         List list2 = list.stream()
            .filter(StaffPresenceTracker::isStaffName)
            .filter(name -> !profileNetwork.contains(name.toLowerCase(Locale.ROOT)))
            .filter(name -> !name.equalsIgnoreCase(mc.player.getName().getString()))
            .filter(name -> !this.hasRecentLeaveMessageFor(name))
            .toList();
         if (list2.size() <= 3) {
            for (String targetableRosterText : (Iterable<java.lang.String>) (list2)) {
               this.reportVanishEvidence(
                  this.rosterTimeline.findIdByName(targetableRosterText), targetableRosterText, com.corz.client.utils.EvidenceAccumulator.Clue.TARGETABLE_OFF_ROSTER, "Staff is command-targetable but not listed"
               );
            }
         }
      }
   }

   private String formatRelativePosition(Vec3d vec) {
      Vec3d entityPos = mc.player.getEntityPos();
      double deltaX = vec.x - entityPos.x;
      double deltaY = vec.y - entityPos.y;
      double deltaZ = vec.z - entityPos.z;
      String entityPosText = deltaZ < -1.0 ? "N" : (deltaZ > 1.0 ? "S" : "");
      String entityPosText2 = deltaX < -1.0 ? "W" : (deltaX > 1.0 ? "E" : "");
      return Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)) + "m " + entityPosText + entityPosText2;
   }

   private void expireOldEntries() {
      long timeMillis = getTimeMillis();

      while (!this.recentChatLines.isEmpty() && timeMillis - this.recentChatLines.peekFirst().timeMs > 8000L) {
         this.recentChatLines.removeFirst();
      }

      this.lastReportTimes.entrySet().removeIf(timestamp -> timeMillis - timestamp.getValue() > 60000L);
      this.evidenceAccumulator.getActiveDecisions(timeMillis);
   }

   private void checkRosterDepartures() {
      long timeMillis = getTimeMillis();

      for (Departure departure : this.rosterTimeline.pollStaffDepartures(mc, timeMillis)) {
         if (!this.hasRecentLeaveMessageFor(departure.name())) {
            this.reportVanishEvidence(departure.id(), departure.name(), com.corz.client.utils.EvidenceAccumulator.Clue.ROSTER_LOSS, "Roster presence ended");
         } else {
            this.evidenceAccumulator.removePlayerCase(departure.id());
         }
      }
   }

   public AntiVanish() {
      super("AntiVanish", IconsPngType.DONUTSMP);
      this.pendingInputs = new ConcurrentLinkedQueue<>();
      this.pendingCompletionIds = ConcurrentHashMap.newKeySet();
      this.lastReportTimes = new HashMap<>();
      this.recentChatLines = new ArrayDeque<>();
      this.nextCompletionRequestId = 41000;
   }

   private void sendNameCompletionRequest() {
      int msgMinecraftValue = this.nextCompletionRequestId++;
      if (this.nextCompletionRequestId > 49000) {
         this.nextCompletionRequestId = 41000;
      }

      this.pendingCompletionIds.add(msgMinecraftValue);

      while (this.pendingCompletionIds.size() > 6) {
         this.pendingCompletionIds.remove(this.pendingCompletionIds.iterator().next());
      }

      try {
         mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(msgMinecraftValue, "minecraft:msg "));
      } catch (Throwable e) {
         this.pendingCompletionIds.remove(msgMinecraftValue);
      }
   }

   private void reportThrottledEvidence(String vanishText, UUID uuid, String vanishAntiText, com.corz.client.utils.EvidenceAccumulator.Clue clue, String vanishText3, long vanishValue) {
      long timeMillis = getTimeMillis();
      if (timeMillis - this.lastReportTimes.getOrDefault(vanishText, 0L) >= vanishValue) {
         this.lastReportTimes.put(vanishText, timeMillis);
         this.reportVanishEvidence(uuid, vanishAntiText, clue, vanishText3);
      }
   }

   private static boolean isPlayerLikeParticle(String indicatorSmokeText) {
      String lowerCase = stripNamespaceToLowerCase(indicatorSmokeText);
      return lowerCase.contains("crit") || lowerCase.contains("enchanted_hit") || lowerCase.contains("damage_indicator") || lowerCase.contains("smoke");
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      var packet = autoVanish.packet;
      if (packet instanceof CommandSuggestionsS2CPacket commandSuggestionsS2CPacket && this.pendingCompletionIds.remove(commandSuggestionsS2CPacket.id())) {
         var items = new ArrayList();

         for (Suggestion suggestion : commandSuggestionsS2CPacket.getSuggestions().getList()) {
            String iiIText = suggestion.getText();
            if (iiIText != null && !iiIText.isBlank()) {
               items.add(iiIText.trim());
            }
         }

         this.commandTargetableNames = items;
         autoVanish.updateFlag();
      } else if (packet instanceof PlayerListS2CPacket playerListS2CPacket && playerListS2CPacket.getActions().contains(Action.UPDATE_GAME_MODE)) {
         playerListS2CPacket.getEntries()
            .stream()
            .filter(entry -> entry.gameMode() != null)
            .forEach(
               entry -> this.pendingInputs.offer(AntiVanish.Input.forPlayer(AntiVanish.Kind.GAME_MODE, entry.profileId(), entry.gameMode().getId()))
            );
      } else if (packet instanceof EntityTrackerUpdateS2CPacket entityTrackerUpdateS2CPacket) {
         this.pendingInputs.offer(AntiVanish.Input.forEntity(AntiVanish.Kind.ENTITY_METADATA, entityTrackerUpdateS2CPacket.id(), ""));
      } else if (packet instanceof PlaySoundFromEntityS2CPacket playSoundFromEntityS2CPacket) {
         this.pendingInputs.offer(AntiVanish.Input.forEntity(AntiVanish.Kind.ENTITY_SOUND, playSoundFromEntityS2CPacket.getEntityId(), getSoundIdString(playSoundFromEntityS2CPacket.getSound().value())));
      } else if (packet instanceof PlaySoundS2CPacket playSoundS2CPacket) {
         this.pendingInputs
            .offer(AntiVanish.Input.atPosition(AntiVanish.Kind.WORLD_SOUND, playSoundS2CPacket.getX(), playSoundS2CPacket.getY(), playSoundS2CPacket.getZ(), getSoundIdString(playSoundS2CPacket.getSound().value())));
      } else if (packet instanceof ParticleS2CPacket particleS2CPacket) {
         Identifier id = Registries.PARTICLE_TYPE.getId(particleS2CPacket.getParameters().getType());
         this.pendingInputs
            .offer(AntiVanish.Input.atPosition(AntiVanish.Kind.PARTICLE, particleS2CPacket.getX(), particleS2CPacket.getY(), particleS2CPacket.getZ(), id == null ? "" : id.toString()));
      } else if (packet instanceof GameMessageS2CPacket gameMessageS2CPacket) {
         String detail = gameMessageS2CPacket.content() == null ? "" : gameMessageS2CPacket.content().getString();
         if (!detail.isBlank()) {
            this.pendingInputs.offer(AntiVanish.Input.ofText(AntiVanish.Kind.CHAT, detail));
         }

         UUID multiplayerLeft = this.getLeftPlayerUuid(gameMessageS2CPacket.content());
         if (multiplayerLeft != null) {
            this.pendingInputs.offer(AntiVanish.Input.forPlayer(AntiVanish.Kind.CONFIRMED_LEAVE, multiplayerLeft, ""));
         }
      }
   }

   private void resetState() {
      this.rosterTimeline.reset();
      this.evidenceAccumulator.clear();
      this.pendingInputs.clear();
      this.pendingCompletionIds.clear();
      this.lastReportTimes.clear();
      this.recentChatLines.clear();
      this.commandTargetableNames = null;
      this.lastSeenWorld = null;
      this.scanTickCounter = 0;
      this.nextCompletionRequestId = 41000;
   }

   private record ChatLine(String text, long timeMs) {
   }

   public record HudEntry(String name, String reason) {
   }

   private record Input(AntiVanish.Kind kind, UUID playerId, int entityId, double x, double y, double z, String detail) {
      static AntiVanish.Input forEntity(AntiVanish.Kind id, int kind, String detail) {
         return new AntiVanish.Input(id, null, kind, 0.0, 0.0, 0.0, detail);
      }

      static AntiVanish.Input forPlayer(AntiVanish.Kind detail, UUID id, String kind) {
         return new AntiVanish.Input(detail, id, -1, 0.0, 0.0, 0.0, kind);
      }

      static AntiVanish.Input atPosition(AntiVanish.Kind detail, double y, double kind, double x, String z) {
         return new AntiVanish.Input(detail, null, -1, y, kind, x, z);
      }

      static AntiVanish.Input ofText(AntiVanish.Kind kind, String detail) {
         return new AntiVanish.Input(kind, null, -1, 0.0, 0.0, 0.0, detail);
      }

      Vec3d getPosition() {
         return new Vec3d(this.x, this.y, this.z);
      }
   }

   private static enum Kind {
      GAME_MODE,
      ENTITY_METADATA,
      ENTITY_SOUND,
      WORLD_SOUND,
      PARTICLE,
      CHAT,
      CONFIRMED_LEAVE;
   }
}
