package com.corz.client.utils;

public final class RebuildFms {
   public int fmsRebuildValue;
   public int fmsRebuildCount;
   public int accumulatedRebuildCount;
   public int rebuildFmsValue;
   public int bufferFmsCount;
   public int rebuildFmsCount;
   public long fmsRebuildDeadline;
   public int totalIntArrayLength;
   public int totalChunkArrayRebuilt;
   public int schemCount;
   public int fmsRebuildValueFmsRebuild;
   public int totalBufferClosed;
   public int samplerghostitemsSize;
   public int rebuildCallCount;

   public void update() {
      this.totalChunkArrayRebuilt = 0;
      this.rebuildFmsValue = 0;
      this.accumulatedRebuildCount = 0;
   }

   public String formatFmsRebuild() {
      return "sections "
         + this.samplerghostitemsSize
         + "/"
         + this.fmsRebuildValueFmsRebuild
         + "  dirty "
         + this.fmsRebuildValue
         + "  rebuilt "
         + this.fmsRebuildCount
         + "  draws "
         + this.bufferFmsCount
         + "  cull f"
         + this.schemCount
         + "/d"
         + this.rebuildFmsCount
         + String.format("  rebuild %.2fms", this.fmsRebuildDeadline / 1000000.0);
   }

   public void handleFmsRebuild(int fmsRebuildValue) {
      this.fmsRebuildValueFmsRebuild = fmsRebuildValue;
      this.samplerghostitemsSize = 0;
      this.fmsRebuildValue = 0;
      this.schemCount = 0;
      this.rebuildFmsCount = 0;
      this.fmsRebuildCount = 0;
      this.rebuildCallCount = 0;
      this.bufferFmsCount = 0;
      this.totalIntArrayLength = 0;
      this.totalBufferClosed = 0;
      this.fmsRebuildDeadline = 0L;
   }
}
