package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.corz.client.utils.DisplayEpoch;
import com.corz.client.utils.NearestRegionType;
import com.corz.client.utils.JsonWho;
import com.corz.client.utils.JsonMats;
import com.corz.client.utils.DifferentDigest;
import com.corz.client.utils.DimJsonData;
import com.corz.client.utils.WhoJsonUtils;
import com.corz.client.utils.TaskStatus;

public final class HostJson {
   public boolean ancHost;
   public final String whoDistText;
   public DisplayEpoch sep;
   public NearestRegionType dist = NearestRegionType.DYNAMIC;
   public final int placementExtent;
   private final Map<String, JsonWho> distHostMap;
   private final Map<String, JsonMats> offlineJsonMatsMap;
   public String ancBy;
   private final Map<Long, JsonWho> distHostJsonWhoMap;
   public final int jsonWhoValue;
   public final int hostJsonValue;
   public final DifferentDigest differentDigest;
   public String host;
   public DimJsonData anc;
   public final int jsonWhoZ;

   public List<JsonMats> getOfflineMulti() {
      ArrayList arrayList = new ArrayList();

      for (JsonMats jsonMats : this.offlineJsonMatsMap.values()) {
         if (jsonMats.offlineStateFlag && jsonMats.multiBuilderFlag && jsonMats.isProtocolMismatch() && jsonMats.cb) {
            arrayList.add(jsonMats);
         }
      }

      return arrayList;
   }

   public JsonMats getJsonMats(String jsonMatsText) {
      return this.offlineJsonMatsMap.get(jsonMatsText);
   }

   public List<JsonWho> getWhoDist() {
      ArrayList arrayList = new ArrayList();

      for (JsonWho jsonWho : this.distHostMap.values()) {
         if (this.whoDistText.equals(jsonWho.who)) {
            arrayList.add(jsonWho);
         }
      }

      return arrayList;
   }

   public List<JsonMats> getOfflineState() {
      ArrayList arrayList = new ArrayList();

      for (JsonMats jsonMats : this.offlineJsonMatsMap.values()) {
         if (jsonMats.offlineStateFlag) {
            arrayList.add(jsonMats);
         }
      }

      return arrayList;
   }

   public JsonWho getDistHost(String distHostText) {
      return this.distHostMap.get(distHostText);
   }

   public boolean canWhoText(int whoValue, int whoJsonValue, int z5) {
      JsonWho jsonWho = this.getJsonWho(whoValue, whoJsonValue, z5);
      return jsonWho != null && this.whoDistText.equals(jsonWho.who);
   }

   public void handleDistHost(List<JsonWho> list) {
      this.distHostMap.clear();
      this.distHostJsonWhoMap.clear();

      for (JsonWho jsonWho : list) {
         this.distHostMap.put(jsonWho.id, jsonWho);
         this.distHostJsonWhoMap.put(WhoJsonUtils.packPositionKey(jsonWho.whoValue, jsonWho.distHostValue, jsonWho.whoZ), jsonWho);
      }
   }

   public void addJsonMats(JsonMats jsonMats) {
      if (jsonMats != null && !jsonMats.whoText.isEmpty()) {
         this.offlineJsonMatsMap.put(jsonMats.whoText, jsonMats);
      }
   }

   public int getRemainingCellCount() {
      return Math.max(0, this.getDigestCellCount() - this.getPlacedDist());
   }

   private void handleJobsDist(JsonObject json) {
      if (json.has("host")) {
         this.host = json.get("host").getAsString();
      }

      if (json.has("dist")) {
         try {
            this.dist = NearestRegionType.valueOf(json.get("dist").getAsString());
         } catch (Exception e) {
         }
      }

      for (JsonWho jsonWho : this.distHostMap.values()) {
         jsonWho.who = null;
         jsonWho.st = TaskStatus.UNASSIGNED;
         jsonWho.rt = 0;
         jsonWho.reportedPlacedCount = 0;
         jsonWho.am = 0L;
         jsonWho.timeMillis = 0L;
         jsonWho.gen = 0L;
      }

      if (json.has("jobs") && json.get("jobs").isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray("jobs")) {
            if (jsonElement.isJsonObject()) {
               JsonObject asJsonObject = jsonElement.getAsJsonObject();
               if (asJsonObject.has("id")) {
                  JsonWho disthostmap = this.distHostMap.get(asJsonObject.get("id").getAsString());
                  if (disthostmap != null) {
                     disthostmap.handleWhoGen(asJsonObject);
                  }
               }
            }
         }
      }
   }

   public Collection<JsonMats> getAllJsonMats() {
      return this.offlineJsonMatsMap.values();
   }

   public JsonObject getAncHost() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("host", this.host == null ? "" : this.host);
      jsonObject.addProperty("dist", this.dist.name());
      jsonObject.addProperty("sid", this.sep.equalsText);
      jsonObject.addProperty("sep", this.sep.ancHostTime);
      if (this.anc != null && this.anc.isDimensionPlacement()) {
         jsonObject.add("anc", this.anc.getDimProperty());
         jsonObject.addProperty("ancBy", this.ancBy == null ? "" : this.ancBy);
         jsonObject.addProperty("ancHost", this.ancHost);
      }

      JsonArray jsonArray = new JsonArray();

      for (JsonWho jsonWho : this.distHostMap.values()) {
         if (jsonWho.who != null || jsonWho.st != TaskStatus.UNASSIGNED || jsonWho.reportedPlacedCount > 0) {
            jsonArray.add(jsonWho.getPropertyWho());
         }
      }

      jsonObject.add("jobs", jsonArray);
      return jsonObject;
   }

   public boolean hasDistHosts() {
      return !this.distHostMap.isEmpty();
   }

   public HostJson(String text, DifferentDigest differentDigest, int value, int value2, int value3, int value4) {
      this.sep = DisplayEpoch.getDisplayEpoch();
      this.ancBy = "";
      this.offlineJsonMatsMap = new LinkedHashMap<>();
      this.distHostMap = new LinkedHashMap<>();
      this.distHostJsonWhoMap = new HashMap<>();
      this.whoDistText = text;
      this.differentDigest = differentDigest;
      this.jsonWhoZ = value;
      this.hostJsonValue = value2;
      this.jsonWhoValue = value3;
      this.placementExtent = value4;
   }

   public boolean isTextHost() {
      return this.whoDistText != null && this.whoDistText.equals(this.host);
   }

   public int getDigestCellCount() {
      return this.differentDigest == null ? 0 : this.differentDigest.cells;
   }

   public int getCompleteDistHostCount() {
      int distHost = 0;

      for (JsonWho jsonWho : this.distHostMap.values()) {
         if (jsonWho.isComplete()) {
            distHost++;
         }
      }

      return distHost;
   }

   public void clearOfflineMats() {
      this.offlineJsonMatsMap.clear();
   }

   public HostJson.DocVerdict applySessionStamp(JsonObject json, boolean sepSidFlag) {
      if (json == null) {
         return HostJson.DocVerdict.UNSTAMPED;
      } else {
         String sepSidText = json.has("sid") ? json.get("sid").getAsString() : "";
         long sepSidLimit = json.has("sep") ? json.get("sep").getAsLong() : 0L;
         HostJson.DocVerdict sepSid = HostJson.DocVerdict.APPLIED;
         if (sepSidText != null && !sepSidText.isEmpty()) {
            if (!this.sep.isEpochUuid()) {
               this.sep = new DisplayEpoch(this.sep.uuidEpochText, sepSidText, sepSidLimit);
               sepSid = HostJson.DocVerdict.ADOPTED_SESSION;
            } else if (!this.sep.equalsText.equals(sepSidText)) {
               if (!sepSidFlag) {
                  return HostJson.DocVerdict.UNSTAMPED;
               }

               this.sep = new DisplayEpoch(this.sep.uuidEpochText, sepSidText, sepSidLimit);
               sepSid = HostJson.DocVerdict.RE_ADOPTED_SESSION;
            } else {
               if (sepSidLimit < this.sep.ancHostTime) {
                  return HostJson.DocVerdict.STALE_EPOCH;
               }

               if (sepSidLimit > this.sep.ancHostTime) {
                  this.sep = new DisplayEpoch(this.sep.uuidEpochText, sepSidText, sepSidLimit);
               }
            }

            this.handleJobsDist(json);
            return sepSid;
         } else {
            return HostJson.DocVerdict.UNSTAMPED;
         }
      }
   }

   public Collection<JsonWho> getAllJsonWho() {
      return this.distHostMap.values();
   }

   public int getOfflineStateCount() {
      int offlineState = 0;

      for (JsonMats jsonMats : this.offlineJsonMatsMap.values()) {
         if (jsonMats.offlineStateFlag) {
            offlineState++;
         }
      }

      return offlineState;
   }

   public int getPlacedDist() {
      int placedDist = 0;

      for (JsonWho jsonWho : this.distHostMap.values()) {
         placedDist += jsonWho.isComplete() ? jsonWho.placedValue : Math.min(jsonWho.reportedPlacedCount, jsonWho.placedValue);
      }

      return placedDist;
   }

   public boolean areAllDistHostsComplete() {
      if (this.distHostMap.isEmpty()) {
         return false;
      } else {
         for (JsonWho jsonWho : this.distHostMap.values()) {
            if (!jsonWho.isComplete()) {
               return false;
            }
         }

         return true;
      }
   }

   public JsonWho getJsonWho(int jsonWhoValue, int whoJsonValue, int z5) {
      int combineCoordinates = WhoJsonUtils.combineCoordinates(jsonWhoValue, this.jsonWhoZ, this.placementExtent);
      int combineCoordinates5 = WhoJsonUtils.combineCoordinates(whoJsonValue, this.hostJsonValue, this.placementExtent);
      int var11x = WhoJsonUtils.combineCoordinates(z5, this.jsonWhoValue, this.placementExtent);
      return this.distHostJsonWhoMap.get(WhoJsonUtils.packPositionKey(combineCoordinates, combineCoordinates5, var11x));
   }

   public int getOfflineTime() {
      long currentMillis = System.currentTimeMillis();
      int offlineTime = 0;

      for (JsonMats jsonMats : this.offlineJsonMatsMap.values()) {
         if (jsonMats.offlineStateFlag || jsonMats.timeMillis > 0L && currentMillis - jsonMats.timeMillis < 30000L) {
            offlineTime++;
         }
      }

      return offlineTime;
   }

   public float getPlacedFraction() {
      int digestDifferent = this.getDigestCellCount();
      return digestDifferent <= 0 ? 0.0F : Math.min(1.0F, (float)this.getPlacedDist() / digestDifferent);
   }

   public int getOwnWhoDistCount() {
      int whoDist = 0;

      for (JsonWho jsonWho : this.distHostMap.values()) {
         if (this.whoDistText.equals(jsonWho.who)) {
            whoDist++;
         }
      }

      return whoDist;
   }

   public static enum DocVerdict {
      APPLIED,
      ADOPTED_SESSION,
      RE_ADOPTED_SESSION,
      STALE_EPOCH,
      UNSTAMPED;
   }
}
