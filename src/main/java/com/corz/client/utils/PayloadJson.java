package com.corz.client.utils;

import com.google.gson.JsonObject;
import com.corz.client.utils.JobSessionType;

public final class PayloadJson {
   public final String from;
   public final long ts;
   public final JobSessionType type;
   public final long seq;
   public final JsonObject payload;

   public static PayloadJson getPayloadJson(JobSessionType jobSessionType, String payloadJsonText, JsonObject json) {
      return new PayloadJson(jobSessionType, payloadJsonText, -1L, System.currentTimeMillis(), json);
   }

   public String formatPayload(String payloadText) {
      return this.payload.has(payloadText) ? this.payload.get(payloadText).getAsString() : null;
   }

   public static PayloadJson getPayloadSeq(JsonObject json) {
      if (json == null) {
         return null;
      } else {
         JobSessionType jobSessionType = JobSessionType.parseByName(json.has("type") ? json.get("type").getAsString() : null);
         if (jobSessionType == null) {
            return null;
         } else {
            String payloadSeqText = json.has("from") ? json.get("from").getAsString() : "";
            long payloadSeqValue = json.has("seq") ? json.get("seq").getAsLong() : -1L;
            long payloadSeqValue2 = json.has("ts") ? json.get("ts").getAsLong() : System.currentTimeMillis();
            JsonObject jsonObject = json.has("payload") && json.get("payload").isJsonObject() ? json.getAsJsonObject("payload") : new JsonObject();
            return new PayloadJson(jobSessionType, payloadSeqText, payloadSeqValue, payloadSeqValue2, jsonObject);
         }
      }
   }

   public String formatText(String payloadJsonText, String payloadJsonText2) {
      String formatPayload = this.formatPayload(payloadJsonText);
      return formatPayload == null ? payloadJsonText2 : formatPayload;
   }

   public int getPayload(String payloadText, int payload) {
      try {
         return this.payload.has(payloadText) ? this.payload.get(payloadText).getAsInt() : payload;
      } catch (Exception e) {
         return payload;
      }
   }

   public JsonObject getSeqProperty() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("type", this.type.name());
      jsonObject.addProperty("from", this.from);
      if (this.seq >= 0L) {
         jsonObject.addProperty("seq", this.seq);
      }

      jsonObject.addProperty("ts", this.ts);
      jsonObject.add("payload", this.payload);
      return jsonObject;
   }

   public PayloadJson(JobSessionType type2, String from, long seq2, long payloadJsonValue, JsonObject json) {
      this.type = type2;
      this.from = from;
      this.seq = seq2;
      this.ts = payloadJsonValue;
      this.payload = json != null ? json : new JsonObject();
   }

   public long getPayload(String payloadText, long payload) {
      try {
         return this.payload.has(payloadText) ? this.payload.get(payloadText).getAsLong() : payload;
      } catch (Exception e) {
         return payload;
      }
   }
}
