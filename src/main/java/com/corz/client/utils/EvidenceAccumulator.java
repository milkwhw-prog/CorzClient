package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EvidenceAccumulator {
   private final Map<String, EvidenceAccumulator.CaseFile> caseFiles = new HashMap<>();

   public void clear() {
      this.caseFiles.clear();
   }

   public EvidenceAccumulator.Decision addEvidence(UUID uuid, String nearbyText, EvidenceAccumulator.Clue clue, String nearbyText2, long time) {
      String text = nearbyText != null && !nearbyText.isBlank() ? nearbyText : "Nearby";
      String longYValue = uuid == null ? "area:" + text : "player:" + uuid;
      EvidenceAccumulator.CaseFile ifAbsent = this.caseFiles.computeIfAbsent(longYValue, idx -> new EvidenceAccumulator.CaseFile(uuid, text));
      ifAbsent.subject = text;
      ifAbsent.itemsByClue.put(clue, new EvidenceAccumulator.Item(clue.evidenceWeight, nearbyText2, time + 15000L));
      int nearbyLimit = pruneAndSumWeights(ifAbsent, time);
      boolean nearbyFlag = nearbyLimit >= 70 && time - ifAbsent.lastAlertTimeMs >= 12000L;
      if (nearbyFlag) {
         ifAbsent.lastAlertTimeMs = time;
      }

      return new EvidenceAccumulator.Decision(ifAbsent.playerId, ifAbsent.subject, nearbyText2, nearbyLimit, nearbyFlag, time + 20000L);
   }

   private static int pruneAndSumWeights(EvidenceAccumulator.CaseFile caseFile, long evidenceAccumulatorLimit) {
      caseFile.itemsByClue.entrySet().removeIf(entry -> entry.getValue().expiresAt <= evidenceAccumulatorLimit);
      return caseFile.itemsByClue.values().stream().mapToInt(EvidenceAccumulator.Item::weight).sum();
   }

   public List<EvidenceAccumulator.Decision> getActiveDecisions(long nearbyExpiresValue) {
      ArrayList arrayList = new ArrayList();
      this.caseFiles.entrySet().removeIf(entry -> pruneAndSumWeights(entry.getValue(), nearbyExpiresValue) == 0);

      for (EvidenceAccumulator.CaseFile caseFile : this.caseFiles.values()) {
         EvidenceAccumulator.Item orElse = caseFile.itemsByClue
            .values()
            .stream()
            .filter(item -> item.expiresAt > nearbyExpiresValue)
            .max((item2, item3) -> Integer.compare(item2.weight, item3.weight))
            .orElse(null);
         if (orElse != null) {
            arrayList.add(new EvidenceAccumulator.Decision(caseFile.playerId, caseFile.subject, orElse.detail, pruneAndSumWeights(caseFile, nearbyExpiresValue), false, orElse.expiresAt));
         }
      }

      arrayList.sort((obj, obj2) -> Integer.compare(((com.corz.client.utils.EvidenceAccumulator.Decision) obj2).score, ((com.corz.client.utils.EvidenceAccumulator.Decision) obj).score));
      return arrayList;
   }

   public void removePlayerCase(UUID uuid) {
      if (uuid != null) {
         this.caseFiles.remove("player:" + uuid);
      }
   }

   private static final class CaseFile {
      final Map<EvidenceAccumulator.Clue, EvidenceAccumulator.Item> itemsByClue = new HashMap<>();
      final UUID playerId;
      String subject;
      long lastAlertTimeMs;

      CaseFile(UUID uUID, String text) {
         this.playerId = uUID;
         this.subject = text;
      }
   }

   public static enum Clue {
      ROSTER_LOSS(45),
      TARGETABLE_OFF_ROSTER(75),
      SPECTATOR_SWITCH(90),
      INVISIBLE_BODY(80),
      ENTITY_SOUND(35),
      WORLD_SOUND(25),
      PARTICLE_TRACE(25);
      private final int evidenceWeight;

      private Clue(int value) {
         this.evidenceWeight = value;
      }
   }

   public record Decision(UUID id, String subject, String detail, int score, boolean alert, long expiresAt) {
   }

   private record Item(int weight, String detail, long expiresAt) {
   }
}
