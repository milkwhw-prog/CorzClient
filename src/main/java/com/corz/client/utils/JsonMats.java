package com.corz.client.utils;

import com.google.gson.JsonObject;
import com.corz.client.utils.RefillingOfflineType;
import com.corz.client.utils.ProtoCommitData;
import com.corz.client.utils.JsonProperty;
import com.corz.client.utils.CoordinatorRunnerType;

public final class JsonMats {
   public boolean multiBuilderFlag = true;
   private static final int[] jsonMatsTable = new int[]{-11549705, -18611, -8271996, -1739917, -4560696, -11677471, -10929, -1023342, -6190977, -7297874};
   public boolean cb = true;
   public final int displayColor;
   public String seenUuidText;
   public int done;
   public String uuidOfflineText;
   public long timeMillis;
   public final String whoText;
   public double doubleValue;
   public RefillingOfflineType act;
   public ProtoCommitData.Verdict mismatchProtocolType;
   public boolean offlineStateFlag;
   public double asDouble;
   public long onlineSeenTime;
   public JsonProperty mats;
   public CoordinatorRunnerType role = CoordinatorRunnerType.BUILDER;
   public boolean roleFlag;
   public double asDoubleRoleDoc;
   public String dim;
   public int rem;

   public JsonObject getPropertyDim() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("role", this.role.name());
      jsonObject.addProperty("act", this.act.name());
      if (this.roleFlag) {
         jsonObject.addProperty("x", this.asDouble);
         jsonObject.addProperty("y", this.doubleValue);
         jsonObject.addProperty("z", this.asDoubleRoleDoc);
      }

      if (this.dim != null && !this.dim.isEmpty()) {
         jsonObject.addProperty("dim", this.dim);
      }

      jsonObject.addProperty("rem", this.rem);
      jsonObject.addProperty("done", this.done);
      jsonObject.addProperty("cb", this.cb);
      if (!this.mats.isEmpty()) {
         jsonObject.add("mats", this.mats.getProperty());
      }

      return jsonObject;
   }

   public JsonMats(String jsonMatsText) {
      this.act = RefillingOfflineType.IDLE;
      this.dim = "";
      this.mats = new JsonProperty();
      this.whoText = jsonMatsText == null ? "" : jsonMatsText;
      this.displayColor = jsonMatsTable[Math.floorMod(this.whoText.hashCode(), jsonMatsTable.length)];
   }

   public String formatOfflineUuid() {
      return this.uuidOfflineText != null && !this.uuidOfflineText.isBlank()
         ? this.uuidOfflineText
         : this.whoText.substring(0, Math.min(6, this.whoText.length()));
   }

   public double getAmount(double jsonMatsValue, double jsonMatsValue2, double jsonMatsValue3) {
      double jsonMatsFactor = this.asDouble - jsonMatsValue;
      double jsonMatsFactor2 = this.doubleValue - jsonMatsValue2;
      double jsonMatsFactor3 = this.asDoubleRoleDoc - jsonMatsValue3;
      return jsonMatsFactor * jsonMatsFactor + jsonMatsFactor2 * jsonMatsFactor2 + jsonMatsFactor3 * jsonMatsFactor3;
   }

   public void handleMatsRem(JsonObject json) {
      if (json != null) {
         this.role = CoordinatorRunnerType.getBuilder(json.has("role") ? json.get("role").getAsString() : null);
         this.act = RefillingOfflineType.getIdle(json.has("act") ? json.get("act").getAsString() : null);
         if (json.has("x") && json.has("y") && json.has("z")) {
            try {
               this.asDouble = json.get("x").getAsDouble();
               this.doubleValue = json.get("y").getAsDouble();
               this.asDoubleRoleDoc = json.get("z").getAsDouble();
               this.roleFlag = true;
            } catch (Exception e) {
            }
         }

         this.dim = json.has("dim") ? json.get("dim").getAsString() : "";
         this.rem = json.has("rem") ? json.get("rem").getAsInt() : 0;
         this.done = json.has("done") ? json.get("done").getAsInt() : 0;
         this.cb = !json.has("cb") || json.get("cb").getAsBoolean();
         if (json.has("mats") && json.get("mats").isJsonObject()) {
            this.mats = JsonProperty.getJsonProperty(json.getAsJsonObject("mats"));
         }
      }
   }

   public boolean isProtocolMismatch() {
      return this.mismatchProtocolType == null || this.mismatchProtocolType == ProtoCommitData.Verdict.OK;
   }

   public static JsonMats getSeenOnline(JsonObject json) {
      if (json != null && json.has("id")) {
         JsonMats jsonMats = new JsonMats(json.get("id").getAsString());
         jsonMats.uuidOfflineText = json.has("name") ? json.get("name").getAsString() : jsonMats.whoText;
         jsonMats.seenUuidText = json.has("uuid") ? json.get("uuid").getAsString() : "";
         jsonMats.onlineSeenTime = json.has("lastSeen") ? json.get("lastSeen").getAsLong() : 0L;
         jsonMats.offlineStateFlag = !json.has("online") || json.get("online").getAsBoolean();
         if (json.has("state") && json.get("state").isJsonObject()) {
            jsonMats.handleMatsRem(json.getAsJsonObject("state"));
         }

         return jsonMats;
      } else {
         return null;
      }
   }
}
