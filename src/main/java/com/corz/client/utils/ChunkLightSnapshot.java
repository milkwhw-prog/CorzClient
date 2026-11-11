package com.corz.client.utils;

import java.util.Arrays;
import com.corz.client.utils.SectionStatus;

public final class ChunkLightSnapshot {
   public final long[] skyNibblesTable;
   public final int sectionCoord;
   public final int chunkX;
   public final int chunkZ;
   public final SectionStatus[] blockLightStatuses;
   public final int verticalSections;
   public final SectionStatus[] skyLightStatuses;
   public final long[] blockNibblesTable;

   public ChunkLightSnapshot(int esp, int value, int verticalSections, int sectionCoord) {
      this.chunkX = esp;
      this.chunkZ = value;
      this.verticalSections = verticalSections;
      this.sectionCoord = sectionCoord;
      int chunkZOffset = verticalSections + 2;
      this.blockLightStatuses = new SectionStatus[chunkZOffset];
      this.blockNibblesTable = new long[chunkZOffset];
      this.skyLightStatuses = new SectionStatus[chunkZOffset];
      this.skyNibblesTable = new long[chunkZOffset];
      Arrays.fill(this.blockLightStatuses, SectionStatus.UNKNOWN);
      Arrays.fill(this.skyLightStatuses, SectionStatus.UNKNOWN);
   }
}
