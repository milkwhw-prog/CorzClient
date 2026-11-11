package com.corz.client.utils;

import com.google.gson.JsonObject;
import java.util.Locale;

public record ExactJobData(
   String jobId, long leaseGeneration, int cellCount, int exactCount, int missingCount, int wrongCount, int unobservedCount, long worldRevision
) {
   public boolean isCell() {
      return this.cellCount == 0;
   }

   public boolean isCellUnobserved() {
      return this.cellCount >= 0 && this.missingCount == 0 && this.wrongCount == 0 && this.unobservedCount == 0 && this.exactCount == this.cellCount;
   }

   public int getMaxCellCount() {
      return Math.max(0, this.exactCount);
   }

   public static ExactJobData getUnseenRev(JsonObject o) {
      if (o != null && o.has("job") && o.has("cells") && o.has("exact")) {
         try {
            return new ExactJobData(
               o.get("job").getAsString(),
               o.has("gen") ? o.get("gen").getAsLong() : -1L,
               o.get("cells").getAsInt(),
               o.get("exact").getAsInt(),
               o.has("missing") ? o.get("missing").getAsInt() : 0,
               o.has("wrong") ? o.get("wrong").getAsInt() : 0,
               o.has("unseen") ? o.get("unseen").getAsInt() : 0,
               o.has("rev") ? o.get("rev").getAsLong() : 0L
            );
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public void writeUnseenPropertiesToJson(JsonObject o) {
      o.addProperty("job", this.jobId);
      o.addProperty("gen", this.leaseGeneration);
      o.addProperty("cells", this.cellCount);
      o.addProperty("exact", this.exactCount);
      o.addProperty("missing", this.missingCount);
      o.addProperty("wrong", this.wrongCount);
      o.addProperty("unseen", this.unobservedCount);
      o.addProperty("rev", this.worldRevision);
   }

   public String formatJobSummary() {
      return String.format(
         Locale.ROOT,
         "job=%s generation=%d cells=%d exact=%d missing=%d wrong=%d unobserved=%d worldRevision=%d result=%s",
         this.jobId,
         this.leaseGeneration,
         this.cellCount,
         this.exactCount,
         this.missingCount,
         this.wrongCount,
         this.unobservedCount,
         this.worldRevision,
         this.isCellUnobserved() ? "COMPLETE" : "REFUSED"
      );
   }

   public boolean isUnobserved() {
      return this.unobservedCount == 0;
   }

   public boolean canJobGeneration(String expectedJobId, int expectedGeneration, long expectedCellCount) {
      return this.jobId != null
         && this.jobId.equals(expectedJobId)
         && this.cellCount == expectedGeneration
         && this.leaseGeneration == expectedCellCount
         && this.isCellUnobserved();
   }

   public ExactJobData(String jobId, long cellCount, int leaseGeneration, int wrongCount, int exactCount, int worldRevision, long missingCount) {
      this(jobId, cellCount, leaseGeneration, wrongCount, exactCount, worldRevision, 0, missingCount);
   }
}
