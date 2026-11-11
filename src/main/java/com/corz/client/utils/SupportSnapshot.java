package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import com.corz.client.utils.MinecraftSculkUtils;

public final class SupportSnapshot {
   private final int maxSupportLimit;
   private final String supportFace;
   private final SupportSnapshot.Cell requiredStand;
   private final int supportSnapshotValue;
   private final long expectedTime;
   private final boolean interactionRequiredFlag;
   private final String expectedBlockId;
   private static final Set<String> DOWN_WEST_SET = Set.of(
      "power", "powered", "lit", "locked", "triggered", "enabled", "extended", "conditional", "inverted", "north", "south", "east", "west", "up", "down"
   );
   private final SupportSnapshot.Hit hit;
   private final int minSupportLimit;
   private final SupportSnapshot.SupportSnapshotInner supportSnapshot;
   private final String phase;
   private final long phaseSupportTime;
   private final SupportSnapshot.Cell targetCell;
   private final SupportSnapshot.Cell supportPos;
   private final MinecraftSculkUtils.Interaction interaction;

   public static long getCodeHash(String codeHashText, SupportSnapshot.Cell cell, SupportSnapshot.Cell supplier, String codeHashText2, SupportSnapshot.Cell cell3) {
      long codeHashValue = -3750763034362895579L;
      long time2 = getTime(codeHashValue, codeHashText.hashCode());
      long time3 = getTime(time2, cell.hashCode());
      long time4 = getTime(time3, supplier.hashCode());
      long time5 = getTime(time4, codeHashText2.hashCode());
      return getTime(time5, cell3.hashCode());
   }

   public boolean isInteractionRequiredFlag() {
      return this.interactionRequiredFlag;
   }

   public long getExpectedTime() {
      return this.expectedTime;
   }

   private static long getTime(long supportSnapshotValue, long supportSnapshotValue2) {
      return (supportSnapshotValue ^ supportSnapshotValue2) * 1099511628211L;
   }

   public int getSupportSnapshotValue() {
      return this.supportSnapshotValue;
   }

   public String getPhase() {
      return this.phase;
   }

   public SupportSnapshot.Cell getTargetCell() {
      return this.targetCell;
   }

   public int getMax() {
      return this.maxSupportLimit;
   }

   public SupportSnapshot.Cell getRequiredStand() {
      return this.requiredStand;
   }

   public static SupportSnapshot.SupportSnapshotInner getNonBlockData(String nonBlockText, Map<String, String> map) {
      return new SupportSnapshot.SupportSnapshotInner(nonBlockText, getCopyOf(map));
   }

   public static boolean canFlag(int supportSnapshotValue, int n69, int n42) {
      return supportSnapshotValue <= 0 && n69 <= 0 && n42 <= 0;
   }

   public boolean canSnapshotSupport(String snapshotSupportText, Map<String, String> map) {
      if (!this.supportSnapshot.blockId().equals(snapshotSupportText)) {
         return false;
      } else {
         Map copyOf = getCopyOf(map);
         return this.supportSnapshot.protectedState().equals(copyOf);
      }
   }

   public static boolean canFlag(int supportSnapshotValue, int n69) {
      return canFlag(supportSnapshotValue, n69, 0);
   }

   public int getMinSupportLimit() {
      return this.minSupportLimit;
   }

   public String getSupportFace() {
      return this.supportFace;
   }

   public SupportSnapshot.Cell getSupportPos() {
      return this.supportPos;
   }

   public String getExpectedBlockId() {
      return this.expectedBlockId;
   }

   public static boolean canFlag(SupportSnapshot.Cell cell, SupportSnapshot.Cell o, boolean supportSnapshotFlag) {
      if (cell == null || o == null) {
         return false;
      } else {
         return cell.equals(o)
            ? true
            : supportSnapshotFlag && cell.x() == o.x() && cell.z() == o.z() && Math.abs(cell.y() - o.y()) <= 1;
      }
   }

   public SupportSnapshot.Hit getHit() {
      return this.hit;
   }

   public SupportSnapshot.SupportSnapshotInner getSupportSnapshot() {
      return this.supportSnapshot;
   }

   public static Map<String, String> getCopyOf(Map<String, String> map) {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      if (map != null) {
         for (Entry entry : map.entrySet()) {
            if (!DOWN_WEST_SET.contains(entry.getKey())) {
               linkedHashMap.put((String)entry.getKey(), (String)entry.getValue());
            }
         }
      }

      return Map.copyOf(linkedHashMap);
   }

   public MinecraftSculkUtils.Interaction getInteraction() {
      return this.interaction;
   }

   public long getPhaseSupportTime() {
      return this.phaseSupportTime;
   }

   public SupportSnapshot(
      long phaseSupportTime,
      String supportSnapshotText,
      SupportSnapshot.Cell cell,
      SupportSnapshot.Cell cell2,
      String supplier,
      SupportSnapshot.Hit hit2,
      SupportSnapshot.SupportSnapshotInner supportSnapshotInner,
      MinecraftSculkUtils.Interaction interaction2,
      boolean interactionRequiredFlag,
      int supportSnapshotBound,
      SupportSnapshot.Cell cell3,
      String supportSnapshotText3,
      int rocketsBuyBound,
      int supportSnapshotBound3
   ) {
      this.phaseSupportTime = phaseSupportTime;
      this.phase = Objects.requireNonNull(supportSnapshotText, "phase");
      this.targetCell = Objects.requireNonNull(cell, "targetCell");
      this.supportPos = Objects.requireNonNull(cell2, "supportPos");
      this.supportFace = Objects.requireNonNull(supplier, "supportFace");
      this.hit = Objects.requireNonNull(hit2, "hit");
      this.supportSnapshot = Objects.requireNonNull(supportSnapshotInner, "supportSnapshot");
      this.interaction = Objects.requireNonNull(interaction2, "interaction");
      this.interactionRequiredFlag = interactionRequiredFlag;
      this.maxSupportLimit = Math.max(0, supportSnapshotBound);
      this.requiredStand = Objects.requireNonNull(cell3, "requiredStand");
      this.expectedBlockId = Objects.requireNonNull(supportSnapshotText3, "expectedBlockId");
      this.minSupportLimit = Math.max(1, rocketsBuyBound);
      this.supportSnapshotValue = Math.max(this.minSupportLimit, supportSnapshotBound3);
      this.expectedTime = getCodeHash(supportSnapshotText, cell, cell2, supplier, cell3);
   }

   public record Cell(int x, int y, int z) {
   }

   public record Hit(double x, double y, double z) {
   }

   public record SupportSnapshotInner(String blockId, Map<String, String> protectedState) {
      public SupportSnapshotInner(String blockId, Map<String, String> protectedState) {
         blockId = Objects.requireNonNull(blockId, "blockId");
         protectedState = Map.copyOf(protectedState == null ? Map.of() : protectedState);
         this.blockId = blockId;
         this.protectedState = protectedState;
      }
   }
}
