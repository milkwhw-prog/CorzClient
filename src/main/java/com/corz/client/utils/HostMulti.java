package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.DimJsonData;
import com.corz.client.utils.JsonDoc;
import com.corz.client.utils.HostJson;
import com.corz.client.utils.DisplayEpoch;
import com.corz.client.utils.JsonWho;
import com.corz.client.utils.MultiHost;
import com.corz.client.utils.DifferentDigest;
import com.corz.client.utils.CdigestChashData;
import com.corz.client.utils.ProtoCommitData;
import com.corz.client.utils.PlacementClaimResolver;
import com.corz.client.utils.JsonMats;
import com.corz.client.utils.PayloadJson;
import com.corz.client.utils.JobSessionType;
import com.corz.client.utils.RotMirData;
import com.corz.client.utils.WhoJsonUtils;
import com.corz.client.utils.CoordinatorRunnerType;
import com.corz.client.utils.ExactJobData;
import com.corz.client.utils.JobMultiUtils;
import com.corz.client.utils.TaskStatus;
import com.corz.client.utils.DocJsonApi;
import com.corz.client.utils.ShaBuildUtils;
import com.corz.client.utils.OverworldNetherUtils;
import com.corz.client.utils.NearestRegionType;
import com.corz.client.utils.JsonProperty;
import com.corz.client.utils.RefillingOfflineType;

public final class HostMulti {
   private final boolean hostIntent;
   private boolean multiTeamFlag;
   private HostMulti.Phase connectingAwaitingType;
   private boolean multiBuilderFlag;
   private int completionsCellCount;
   private boolean hostAncActive;
   private long nextSequenceNumber;
   private DimJsonData adopted;
   private final Map<String, Long> multiBuilderMap;
   private final JsonDoc jsonDoc;
   private final Set<String> apiIncompatibleStringSet;
   private long timeMillis;
   private final Set<String> multiBuilderSet;
   private int completionsRefusedCount;
   private HostJson me;
   private int coordinatorOldCount;
   public static final String BLOCKED = "blocked";
   private long timeMillisMultiDoc;
   private long multiDocValue;
   private long timeMillisIntentAdopted;
   private volatile Set<Long> emptySet;
   private int placementExtent;
   private DisplayEpoch displayEpoch;
   private String teamApiText;
   private String error;
   private List<JsonWho> connectingItems = List.of();
   private DimJsonData dimJsonData;
   private String teamPlacementText;
   private boolean multiBuilderFlagMultiDoc;
   private final HostMulti.Context teamApiContext;
   private String multiBuilderText;
   private String teamMultiText;
   private int multiBuilderValue;
   private final MultiHost docOfflineMultiHost;
   private DifferentDigest differentDigest;
   private String ancHostText;
   private int docMultiValue;
   private String jsonIteratorText;
   private int completionsStaleCount;
   private int completionsIncompatibleCount;
   private int oldCoordinatorCount;
   private final Map<String, String> apiIncompatibleStringMap;
   private int apiIncompatibleCount;
   private final CdigestChashData cdigestChashData;
   private final String joiningSessionText;
   private final Map<String, ProtoCommitData.Verdict> offlineApiVerdictMap;
   private final Map<Long, Long> multiDocPositionMap;
   private boolean ancHostFlag;
   private boolean hostIntentHostAnc;
   private PlacementClaimResolver.Claim data2TeamMatching;
   private final Map<String, PlacementClaimResolver.Claim> intentHostMap;
   private boolean intentFlag;
   private final String hostMultiText;
   private long timeMillisMultiBuilder;

   private void handleOfflineApi(JsonArray jsonArray, String host) {
      if (this.me != null) {
         Set emptySet = this.teamApiContext.getTeammateNames();
         String leaveCorzText = this.jsonDoc.getLeaveCorzText();
         HashSet hashSet = new HashSet();
         if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
               if (jsonElement.isJsonObject()) {
                  JsonMats seenOnline = JsonMats.getSeenOnline(jsonElement.getAsJsonObject());
                  if (seenOnline != null) {
                     seenOnline.multiBuilderFlag = emptySet == null
                        || emptySet.isEmpty()
                        || seenOnline.whoText.equals(leaveCorzText)
                        || seenOnline.uuidOfflineText != null && emptySet.contains(seenOnline.uuidOfflineText.toLowerCase(Locale.ROOT));
                     if (seenOnline.whoText.equals(leaveCorzText)) {
                        seenOnline.cb = this.teamApiContext.isBuilderActive();
                     }

                     hashSet.add(seenOnline.whoText);
                     JsonMats jsonMats = this.me.getJsonMats(seenOnline.whoText);
                     if (!seenOnline.offlineStateFlag) {
                        seenOnline.timeMillis = jsonMats != null && jsonMats.timeMillis > 0L ? jsonMats.timeMillis : System.currentTimeMillis();
                     }

                     seenOnline.mismatchProtocolType = this.offlineApiVerdictMap.get(seenOnline.whoText);
                     this.me.addJsonMats(seenOnline);
                  }
               }
            }

            if (!hashSet.isEmpty()) {
               this.multiTeamFlag = true;
            }
         }

         for (JsonMats getAllJsonMatsItem : this.me.getAllJsonMats()) {
            if (!hashSet.contains(getAllJsonMatsItem.whoText) && getAllJsonMatsItem.offlineStateFlag) {
               getAllJsonMatsItem.offlineStateFlag = false;
               getAllJsonMatsItem.timeMillis = System.currentTimeMillis();
            }
         }

         if (!hashSet.isEmpty()) {
            this.intentHostMap.keySet().removeIf(offlineApiText -> !hashSet.contains(offlineApiText));
         }

         if (host != null) {
            this.me.host = host;
         }

         if (this.me.dist != this.teamApiContext.getNearestRegionType() && this.me.isTextHost()) {
            this.me.dist = this.teamApiContext.getNearestRegionType();
         }
      }
   }

   public void updateJoiningSession() {
      this.jsonDoc.handleNetVersion(this.hostMultiText, this.cdigestChashData.hash(), this.teamApiContext.getUsername(), this.teamApiContext.getUuid());
      this.teamPlacementText = this.hostIntent ? "Creating session..." : "Joining session...";
      this.teamApiContext
         .onMultiSessionMessage(
            "\u00a7b[MultiBuilder]\u00a7r "
               + (this.hostIntent ? "hosting" : "joining")
               + " team "
               + (this.joiningSessionText.isEmpty() ? "(no code)" : this.joiningSessionText)
               + " for "
               + this.cdigestChashData.schematicName()
         );
   }

   public boolean isDocJson() {
      return this.jsonDoc.isEquals();
   }

   public MultiHost.Result getDocOffline() {
      if (this.me == null) {
         return MultiHost.Result.INSTANCE;
      } else {
         long currentMillis = System.currentTimeMillis();
         ArrayList arrayList = new ArrayList();

         for (JsonMats jsonMats : this.me.getAllJsonMats()) {
            boolean docOfflineFlag = jsonMats.offlineStateFlag || jsonMats.timeMillis > 0L && currentMillis - jsonMats.timeMillis < 30000L;
            arrayList.add(new MultiHost.Member(jsonMats.whoText, jsonMats.formatOfflineUuid(), docOfflineFlag));
         }

         return this.docOfflineMultiHost.getInstancePresent(arrayList, this.jsonDoc.getLeaveCorzText());
      }
   }

   private void handleOldCoordinator(JsonObject json) {
      this.handleAncHost(json);
      if (this.dimJsonData != null && this.adopted != null && !this.adopted.canPlacementDimension(this.dimJsonData)) {
         this.handleTeamApi(this.dimJsonData);
         if (this.adopted == null || !this.adopted.canPlacementDimension(this.dimJsonData)) {
            return;
         }
      }

      String oldCoordinatorText = json != null && json.has("host") ? json.get("host").getAsString() : "";
      boolean oldCoordinatorFlag = !oldCoordinatorText.isEmpty() && oldCoordinatorText.equals(this.jsonDoc.getVersionJsonText()) && !oldCoordinatorText.equals(this.jsonDoc.getLeaveCorzText());
      DisplayEpoch sep = this.me.sep;
      HostJson.DocVerdict sepSid = this.me.applySessionStamp(json, oldCoordinatorFlag);
      if (!this.me.sep.equals(sep)) {
         this.updateIntentMulti();
      }

      switch (sepSid) {
         case ADOPTED_SESSION:
            this.teamApiContext.onMultiSessionMessage("\u00a7b[MultiSession]\u00a7r joined " + this.me.sep.getFormat());
            break;
         case RE_ADOPTED_SESSION:
            this.teamApiContext.onMultiSessionMessage("\u00a7b[MultiSession]\u00a7r coordinator re-identified - now " + this.me.sep.getFormat());
            break;
         case UNSTAMPED:
            this.oldCoordinatorCount++;
            if (this.coordinatorOldCount++ == 3) {
               this.teamApiContext
                  .onMultiSessionMessage(
                     "\u00a7c[MultiBuilder]\u00a7r the coordinator is running an older MultiBuilder build - its session has no identity, so its assignments cannot be trusted."
                  );
               this.teamApiContext.onMultiSessionMessage("\u00a77Both PCs must run the SAME jar (" + this.teamApiContext.getBuildProtoCommit().plannerGeneration() + ").");
               this.teamPlacementText = "Coordinator build too old";
            }
            break;
         case STALE_EPOCH:
            this.oldCoordinatorCount++;
      }
   }

   public void handleMultiDoc(HostMulti.Context context) {
      JsonDoc.State enteringConnectedType = this.jsonDoc.getEnteringConnectedType();
      if (enteringConnectedType == JsonDoc.State.CONNECTED && this.connectingAwaitingType == HostMulti.Phase.CONNECTING) {
         this.updateTeamMulti();
      }

      if (enteringConnectedType == JsonDoc.State.CLOSED) {
         this.handleConnectingAwaiting("session closed");
      } else if (enteringConnectedType == JsonDoc.State.ERROR) {
         this.handleErrorConnecting(this.jsonDoc.getUnexpected());
      } else if (this.connectingAwaitingType == HostMulti.Phase.CONNECTING) {
         this.teamPlacementText = this.jsonDoc.isEquals() ? "Team server unreachable - retrying" : this.teamPlacementText;
      } else {
         long timeMillis = System.currentTimeMillis();
         if (this.me == null) {
            this.jsonDoc.handleDocJson(new JsonDoc.Consumer() {
               @Override
               public void handleHostMulti(long hostMultiValue, JsonObject json) {
                  HostMulti.this.handleAncHost(json);
               }

               @Override
               public void handleHostMulti(JsonArray jsonArray, String hostMultiText, long hostMultiValue2) {
                  HostMulti.this.handleJsonIterator(jsonArray, hostMultiText);
               }

               @Override
               public void handleHostMulti(PayloadJson payloadJson) {
                  if (payloadJson.type == JobSessionType.BUILD_INFO) {
                     HostMulti.this.handleApiIncompatible(payloadJson);
                  }
               }
            });
            this.handleIntentAdopted(timeMillis);
            this.updateTeamMatching();
            this.handleRoleDoc(context);
            if (this.me == null) {
               return;
            }
         }

         String host = this.jsonDoc.getVersionJsonText();
         if (host != null && !host.isEmpty()) {
            this.me.host = host;
         }

         boolean multiBuilderFlag = this.me.isTextHost();
         if (multiBuilderFlag != this.multiBuilderFlagMultiDoc) {
            this.teamApiContext
               .onMultiSessionMessage(multiBuilderFlag ? "\u00a7b[MultiBuilder]\u00a7r you are now the coordinator" : "\u00a7b[MultiBuilder]\u00a7r coordinator changed");
            if (multiBuilderFlag) {
               this.me.sep = this.me.sep.isEpochUuid()
                  ? this.me.sep.getEpochUuid()
                  : DisplayEpoch.getDisplayEpoch(this.differentDigest.getUpperCase(), this.jsonDoc.getLeaveCorzText(), timeMillis);
               this.teamApiContext.onMultiSessionMessage("\u00a7b[MultiSession]\u00a7r " + this.me.sep.getFormat() + " (coordinator change)");
            }

            this.multiBuilderFlagMultiDoc = multiBuilderFlag;
         }

         if (multiBuilderFlag) {
            this.updateSepDisplay();
         }

         this.jsonDoc.handleDocJson(new JsonDoc.Consumer() {
            @Override
            public void handleHostMulti(PayloadJson payloadJson) {
               HostMulti.this.handleJobGen(payloadJson);
            }

            @Override
            public void handleHostMulti(long hostMultiValue3, JsonObject json) {
               if (!HostMulti.this.me.isTextHost()) {
                  HostMulti.this.handleOldCoordinator(json);
               }
            }

            @Override
            public void handleHostMulti(JsonArray jsonArray, String host, long hostMultiValue4) {
               HostMulti.this.handleOfflineApi(jsonArray, host);
            }
         });
         boolean textHost = this.me.isTextHost();
         this.handleIntentAdopted(timeMillis);
         if (timeMillis - this.timeMillisMultiBuilder >= 500L) {
            this.timeMillisMultiBuilder = timeMillis;
            this.handleDocMulti(timeMillis);
         }

         if (textHost && this.multiTeamFlag && !this.jsonDoc.isEquals() && timeMillis - this.multiDocValue >= 400L) {
            this.multiDocValue = timeMillis;
            this.handleDocApi(timeMillis);
         }

         if (timeMillis - this.timeMillis >= 700L) {
            this.timeMillis = timeMillis;
            this.handleRoleDoc(context);
         }

         this.handleDimEmpty(context);
         this.multiDocPositionMap.entrySet().removeIf(now -> now.getValue() < timeMillis);
         if (timeMillis - this.timeMillisMultiDoc >= 30000L) {
            this.timeMillisMultiDoc = timeMillis;
            this.teamApiContext
               .onMultiSessionMessage(
                  "\u00a78[MultiSession] "
                     + this.getAppend()
                     + " jobs="
                     + this.me.getCompleteDistHostCount()
                     + "/"
                     + this.me.getAllJsonWho().size()
                     + " cells="
                     + this.me.getPlacedDist()
                     + "/"
                     + this.me.getDigestCellCount()
               );
         }

         this.teamPlacementText = textHost ? "Coordinating" : "Building";
      }
   }

   public boolean isConnectingAwaiting() {
      return this.connectingAwaitingType == HostMulti.Phase.CLOSED || this.connectingAwaitingType == HostMulti.Phase.ERROR;
   }

   private void handleCdigestChash(DimJsonData anc) {
      String leaveCorzText = this.jsonDoc.getLeaveCorzText();
      RotMirData placement = anc.placement();
      this.differentDigest = new DifferentDigest(
         this.cdigestChashData.schematicName(),
         this.cdigestChashData.width(),
         this.cdigestChashData.height(),
         this.cdigestChashData.length(),
         this.teamApiContext.getTargetBlockCount(),
         placement.originX(),
         placement.originY(),
         placement.originZ(),
         placement.rotationIndex(),
         placement.mirrorIndex(),
         this.cdigestChashData.contentDigest()
      );
      this.placementExtent = WhoJsonUtils.combineBounds(this.cdigestChashData.width(), this.cdigestChashData.height(), this.cdigestChashData.length());
      this.connectingItems = WhoJsonUtils.collectWhoInBounds(placement.originX(), placement.originY(), placement.originZ(), this.placementExtent, cellConsumer -> this.teamApiContext.forEachTargetCell(cellConsumer));
      DisplayEpoch displayEpoch = this.me != null ? this.me.sep : null;
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      String host = this.jsonDoc.getVersionJsonText();
      if (this.me != null) {
         for (JsonMats jsonMats : this.me.getAllJsonMats()) {
            linkedHashMap.put(jsonMats.whoText, jsonMats);
         }

         host = this.me.host;
      }

      this.me = new HostJson(leaveCorzText, this.differentDigest, placement.originX(), placement.originY(), placement.originZ(), this.placementExtent);
      this.me.host = host;
      this.me.dist = this.teamApiContext.getNearestRegionType();

      for (JsonMats jsonMats2 : (Iterable<com.corz.client.utils.JsonMats>) (linkedHashMap.values())) {
         this.me.addJsonMats(jsonMats2);
      }

      this.me.sep = displayEpoch != null && displayEpoch.isEpochUuid()
         ? displayEpoch
         : (
            this.me.isTextHost()
               ? DisplayEpoch.getDisplayEpoch(this.differentDigest.getUpperCase(), leaveCorzText, System.currentTimeMillis())
               : new DisplayEpoch(this.differentDigest.getUpperCase(), "", 0L)
         );
      this.displayEpoch = this.me.sep;
      ArrayList arrayList = new ArrayList(this.connectingItems.size());

      for (JsonWho jsonWho : this.connectingItems) {
         arrayList.add(
            new JsonWho(
               jsonWho.id,
               jsonWho.whoValue,
               jsonWho.distHostValue,
               jsonWho.whoZ,
               jsonWho.boxMinX,
               jsonWho.boxMinY,
               jsonWho.boxMinZ,
               jsonWho.cdigestChashValue,
               jsonWho.autoBuilderValue,
               jsonWho.jsonWhoValue,
               jsonWho.placedValue,
               jsonWho.jsonProperty,
               jsonWho.whoX
            )
         );
      }

      this.me.handleDistHost(arrayList);
      this.me.anc = anc;
      this.me.ancBy = this.ancHostText;
      this.me.ancHost = this.hostIntentHostAnc;
      this.adopted = anc;
      this.teamApiText = null;
      this.connectingAwaitingType = HostMulti.Phase.ACTIVE;
      this.multiBuilderFlagMultiDoc = this.me.isTextHost();
      this.docOfflineMultiHost.updateFlag();
      this.multiBuilderMap.clear();
      this.multiDocPositionMap.clear();
      this.multiBuilderSet.clear();
      this.multiBuilderValue = -1;
      this.teamApiContext
         .onMultiSessionMessage(
            "\u00a7a[MultiBuilder]\u00a7r team "
               + (this.joiningSessionText.isEmpty() ? this.differentDigest.getUpperCase() : this.joiningSessionText)
               + " ready - "
               + arrayList.size()
               + " jobs, "
               + this.teamApiContext.getTargetBlockCount()
               + " blocks at "
               + placement.formatOriginMirror()
               + (this.me.isTextHost() ? ", you are coordinator" : ", coordinator=" + shortenId(this.me.host))
         );
   }

   public String getJoiningSessionText() {
      return this.joiningSessionText;
   }

   private void handleApiIncompatible(PayloadJson payloadJson) {
      if (payloadJson.from != null && !payloadJson.from.equals(this.jsonDoc.getLeaveCorzText())) {
         ProtoCommitData protoBuild = ProtoCommitData.getProtoBuild(payloadJson.payload);
         ProtoCommitData.Verdict mismatchDigest = this.teamApiContext.getBuildProtoCommit().getMismatchDigest(protoBuild);
         this.offlineApiVerdictMap.put(payloadJson.from, mismatchDigest);
         if (mismatchDigest != ProtoCommitData.Verdict.OK) {
            if (this.apiIncompatibleStringSet.add(payloadJson.from)) {
               this.apiIncompatibleCount++;
               this.teamApiContext
                  .onMultiSessionMessage(
                     "\u00a7c[MultiBuilder]\u00a7r "
                        + this.apiIncompatibleStringMap.getOrDefault(payloadJson.from, shortenId(payloadJson.from))
                        + " is running a different build - they will not be given work."
                  );
               this.teamApiContext.onMultiSessionMessage("\u00a77" + this.teamApiContext.getBuildProtoCommit().formatDigestVersion(protoBuild, mismatchDigest).replace("\n", "\n\u00a77"));
            }
         } else {
            this.handleIntentHost(payloadJson);
         }
      }
   }

   public boolean isMultiBuilder() {
      return this.me != null && this.me.getOwnWhoDistCount() > 0 && this.multiBuilderValue != 0;
   }

   private void handleRoleDoc(HostMulti.Context context) {
      JsonMats jsonMats = new JsonMats(this.jsonDoc.getLeaveCorzText());
      jsonMats.uuidOfflineText = context.getUsername();
      jsonMats.seenUuidText = context.getUuid();
      jsonMats.act = context.getBuilderStatusType();
      jsonMats.roleFlag = true;
      jsonMats.asDouble = context.getX();
      jsonMats.doubleValue = context.getY();
      jsonMats.asDoubleRoleDoc = context.getZ();
      jsonMats.dim = context.getSessionKey();
      jsonMats.rem = this.multiBuilderValue;
      jsonMats.done = this.docMultiValue;
      jsonMats.role = this.me != null && this.me.isTextHost() ? CoordinatorRunnerType.COORDINATOR : CoordinatorRunnerType.BUILDER;
      jsonMats.cb = context.isBuilderActive();
      if (context.isMaterialSharingEnabled()) {
         jsonMats.mats = context.getInventoryMaterialCounts();
      }

      this.jsonDoc.handleJson(jsonMats.getPropertyDim());
   }

   public void updateConnectingDoc() {
      try {
         this.jsonDoc.updateLeaveCorz();
      } catch (Exception e) {
      }

      this.connectingAwaitingType = HostMulti.Phase.CLOSED;
   }

   public HostJson getMe() {
      return this.me;
   }

   public int getOldCoordinatorCount() {
      return this.oldCoordinatorCount;
   }

   private boolean canSepOld(PayloadJson payloadJson) {
      if (payloadJson.type == JobSessionType.BUILD_INFO) {
         return true;
      } else if (!this.me.sep.isEpochUuid()) {
         this.oldCoordinatorCount++;
         return false;
      } else {
         DisplayEpoch.Reject epochAnc = this.me.sep.getEpochAnc(payloadJson.formatPayload("sid"), payloadJson.getPayload("sep", -1L));
         if (epochAnc == DisplayEpoch.Reject.ACCEPTED) {
            return true;
         } else {
            this.oldCoordinatorCount++;
            return false;
         }
      }
   }

   private void handleCompletionsIncompatible(JsonWho jsonWho, ExactJobData exactJobData, ExactJobData autoBuilder) {
      JobMultiUtils.Ruling censusComplete = JobMultiUtils.decideCensusRuling(jsonWho.id, jsonWho.placedValue, jsonWho.gen, exactJobData, autoBuilder);
      jsonWho.reportedPlacedCount = exactJobData != null && exactJobData.isUnobserved() ? censusComplete.placedToRecord() : Math.max(jsonWho.reportedPlacedCount, censusComplete.placedToRecord());
      if (censusComplete.isCensusComplete()) {
         jsonWho.st = TaskStatus.COMPLETE;
         if (censusComplete.verdict() == JobMultiUtils.Verdict.COMPLETE_ON_OWNER_CENSUS) {
            this.completionsIncompatibleCount++;
         }
      } else {
         this.completionsRefusedCount++;
         if (jsonWho.isComplete()) {
            jsonWho.st = TaskStatus.ASSIGNED;
            jsonWho.timeMillis = System.currentTimeMillis();
            this.teamApiContext.onMultiSessionMessage("\u00a7e[MultiBuilder]\u00a7r job " + jsonWho.id + " reopened - its owner reports it is no longer complete");
         }
      }
   }

   public void handleBlockedWhy(String blockedWhyText) {
      if (this.me != null) {
         boolean blockedWhyFlag = blockedWhyText != null && blockedWhyText.startsWith("blocked");

         for (JsonWho jsonWho : this.me.getWhoDist()) {
            if (!jsonWho.isComplete()) {
               if (this.me.isTextHost()) {
                  jsonWho.handleWhoGen(TaskStatus.BLOCKED);
                  if (blockedWhyFlag) {
                     jsonWho.rt++;
                  }
               } else {
                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("why", blockedWhyText == null ? "" : blockedWhyText);
                  this.handleGenJob(JobSessionType.JOB_RELEASE, jsonWho, jsonObject);
               }
            }
         }
      }
   }

   public int getApiIncompatibleCount() {
      return this.apiIncompatibleCount;
   }

   public String getHostMultiText() {
      return this.hostMultiText;
   }

   private void updateTeamMulti() {
      String multiBuilderText = this.jsonDoc.getLeaveCorzText();
      String versionText = this.jsonDoc.getVersionText();
      if (versionText != null && !versionText.isEmpty() && !versionText.equals(this.cdigestChashData.hash())) {
         this.teamMultiText = "they have a different copy of " + this.cdigestChashData.schematicName();
         this.teamApiContext.onMultiSessionMessage("\u00a7c[MultiBuilder]\u00a7r that team code is building a different schematic. Not joining.");
         this.handleErrorConnecting("different schematic");
      } else {
         this.multiBuilderText = multiBuilderText;
         this.multiBuilderFlag = true;
         this.multiTeamFlag = false;
         this.connectingAwaitingType = HostMulti.Phase.AWAITING_ANCHOR;
         this.teamPlacementText = "Matching the team's placement...";
         DimJsonData builderAuto = this.teamApiContext.getPlacementSnapshot();
         if (builderAuto != null && builderAuto.isDimensionPlacement()) {
            this.intentHostMap.put(multiBuilderText, new PlacementClaimResolver.Claim(multiBuilderText, builderAuto, this.hostIntent));
         }
      }
   }

   public boolean isMultiBuilderFlag() {
      return this.multiBuilderFlag;
   }

   private boolean hasUnknownOfflineMember() {
      if (this.me == null) {
         return false;
      } else {
         String leaveCorzText = this.jsonDoc.getLeaveCorzText();

         for (JsonMats jsonMats : this.me.getOfflineState()) {
            if (!jsonMats.whoText.equals(leaveCorzText) && !this.offlineApiVerdictMap.containsKey(jsonMats.whoText)) {
               return true;
            }
         }

         return false;
      }
   }

   private void handleDimEmpty(HostMulti.Context context) {
      if (this.me == null) {
         this.emptySet = Collections.emptySet();
      } else {
         String formatAutoBuilder = context.getSessionKey();
         String leaveCorzText = this.jsonDoc.getLeaveCorzText();
         HashSet emptySet = new HashSet();

         for (JsonMats jsonMats : this.me.getOfflineState()) {
            if (!jsonMats.whoText.equals(leaveCorzText) && jsonMats.roleFlag && (jsonMats.dim == null || jsonMats.dim.isEmpty() || formatAutoBuilder == null || jsonMats.dim.equals(formatAutoBuilder))) {
               int floorDoubleValue = (int)Math.floor(jsonMats.asDouble);
               int whoJsonValue = (int)Math.floor(jsonMats.doubleValue);
               int z5 = (int)Math.floor(jsonMats.asDoubleRoleDoc);
               emptySet.add(packBlockPos(floorDoubleValue, whoJsonValue, z5));
               emptySet.add(packBlockPos(floorDoubleValue, whoJsonValue + 1, z5));
            }
         }

         this.emptySet = emptySet;
      }
   }

   public String getError() {
      return this.error;
   }

   public boolean isMeTextHost() {
      return this.me != null && this.me.isTextHost();
   }

   private static long packBlockPos(int hostMultiValue, int whoJsonValue, int z5) {
      return BlockPos.asLong(hostMultiValue, whoJsonValue, z5);
   }

   public int getCompletionsStaleCount() {
      return this.completionsStaleCount;
   }

   private PayloadJson getSepSid(PayloadJson sepSid) {
      if (this.me != null) {
         sepSid.payload.addProperty("sid", this.me.sep.equalsText);
         sepSid.payload.addProperty("sep", this.me.sep.ancHostTime);
      }

      return sepSid;
   }

   public String formatPoolWho(int poolWhoValue, int whoJsonValue, int z5) {
      if (this.me == null) {
         return null;
      } else {
         JsonWho jsonWho = this.me.getJsonWho(poolWhoValue, whoJsonValue, z5);
         if (jsonWho == null) {
            return null;
         } else {
            return jsonWho.who == null ? "pool" : this.getDisplayName(jsonWho.who);
         }
      }
   }

   public String getAppend() {
      return "session="
         + (this.me == null ? "none" : this.me.sep.getFormat())
         + " staleSessionRejected="
         + this.oldCoordinatorCount
         + " backlogDiscardedOnEntry="
         + this.jsonDoc.getVersionNetValue()
         + " invalidSenderCompletions="
         + this.completionsStaleCount
         + " staleLeaseMessages="
         + this.completionsCellCount
         + " falseCompletionsRefused="
         + this.completionsRefusedCount
         + " completionsOnOwnerProof="
         + this.completionsIncompatibleCount
         + " incompatiblePeersRefused="
         + this.apiIncompatibleCount;
   }

   private void handleIntentHost(PayloadJson payloadJson) {
      if (payloadJson.payload.has("anchor") && payloadJson.payload.get("anchor").isJsonObject()) {
         DimJsonData dim = DimJsonData.getDim(payloadJson.payload.getAsJsonObject("anchor"));
         if (dim != null) {
            boolean intentHostFlag = payloadJson.payload.has("hostIntent") && payloadJson.payload.get("hostIntent").getAsBoolean();
            this.intentHostMap.put(payloadJson.from, new PlacementClaimResolver.Claim(payloadJson.from, dim, intentHostFlag));
         }
      }
   }

   public int getCompletionsCellCount() {
      return this.completionsCellCount;
   }

   private void handleAncHost(JsonObject json) {
      if (json != null && json.has("anc") && json.get("anc").isJsonObject()) {
         DimJsonData dimJsonData = DimJsonData.getDim(json.getAsJsonObject("anc"));
         if (dimJsonData != null) {
            boolean ancHostFlag = this.dimJsonData == null || !this.dimJsonData.canPlacementDimension(dimJsonData);
            this.dimJsonData = dimJsonData;
            this.ancHostText = json.has("ancBy") ? json.get("ancBy").getAsString() : "";
            this.hostIntentHostAnc = json.has("ancHost") && json.get("ancHost").getAsBoolean();
            if (ancHostFlag) {
               this.ancHostFlag = false;
            }
         }
      }
   }

   public HostMulti(HostMulti.Context api, boolean hostIntent, DocJsonApi docJsonApi) {
      this.connectingAwaitingType = HostMulti.Phase.CONNECTING;
      this.teamPlacementText = "Connecting...";
      this.error = "";
      this.multiDocPositionMap = new HashMap<>();
      this.emptySet = Collections.emptySet();
      this.multiBuilderSet = new HashSet<>();
      this.ancHostText = "";
      this.intentHostMap = new HashMap<>();
      this.multiBuilderText = "";
      this.docOfflineMultiHost = new MultiHost();
      this.jsonIteratorText = "";
      this.apiIncompatibleStringMap = new HashMap<>();
      this.displayEpoch = DisplayEpoch.getDisplayEpoch();
      this.offlineApiVerdictMap = new HashMap<>();
      this.apiIncompatibleStringSet = new HashSet<>();
      this.multiBuilderValue = -1;
      this.multiBuilderMap = new HashMap<>();
      this.teamApiContext = api;
      this.hostIntent = hostIntent;
      this.jsonDoc = new JsonDoc(docJsonApi);
      this.cdigestChashData = api.getSchematicDigest();
      this.joiningSessionText = ShaBuildUtils.formatText(api.getTeamCodeText());
      this.hostMultiText = ShaBuildUtils.formatShaBuild(api.getTeamCodeText(), this.cdigestChashData);
   }

   private void updateHostAnc() {
      if (this.me != null && this.me.getPlacedDist() > 0) {
         this.hostAncActive = true;
      }

      if (!this.hostAncActive) {
         this.updateDimAnc();
         PlacementClaimResolver.Claim data = PlacementClaimResolver.pickPreferredClaim(this.intentHostMap.values());
         if (PlacementClaimResolver.isPreferredClaim(this.data2TeamMatching, data)) {
            PlacementClaimResolver.Claim data2TeamMatching2 = this.data2TeamMatching;
            DimJsonData dimJsonData = this.dimJsonData;
            String ancHostText = this.ancHostText;
            boolean ancHostFlag = this.hostIntentHostAnc;
            this.data2TeamMatching = data;
            this.dimJsonData = data.anchor();
            this.ancHostText = data.memberId();
            this.hostIntentHostAnc = data.hostIntent();
            this.handleTeamApi(this.dimJsonData);
            if (this.adopted == null || !this.adopted.canPlacementDimension(this.dimJsonData)) {
               this.data2TeamMatching = data2TeamMatching2;
               this.dimJsonData = dimJsonData;
               this.ancHostText = ancHostText;
               this.hostIntentHostAnc = ancHostFlag;
            }
         }
      }
   }

   public DifferentDigest getDifferentDigest() {
      return this.differentDigest;
   }

   public boolean canIntentHost(HostMulti.Context context, boolean intentHostFlag) {
      return context == null
         ? false
         : intentHostFlag == this.hostIntent && this.hostMultiText.equals(ShaBuildUtils.formatShaBuild(context.getTeamCodeText(), context.getSchematicDigest()));
   }

   public void updateMovedDoc() {
      if (this.adopted != null) {
         if (this.jsonDoc.getLeaveCorzText() != null && this.jsonDoc.getLeaveCorzText().equals(this.ancHostText)) {
            DimJsonData dimJsonData = this.teamApiContext.getPlacementSnapshot();
            if (dimJsonData != null && dimJsonData.isDimensionPlacement()) {
               this.data2TeamMatching = new PlacementClaimResolver.Claim(this.jsonDoc.getLeaveCorzText(), dimJsonData, this.hostIntent);
               this.dimJsonData = dimJsonData;
               this.handleSepDoc(dimJsonData, "you moved the build");
            }
         } else {
            this.ancHostFlag = true;
         }
      }
   }

   private void updateSepDisplay() {
      if (!this.me.sep.equals(this.displayEpoch)) {
         this.displayEpoch = this.me.sep;
         this.updateIntentMulti();
      }
   }

   public boolean canTookJob(int tookJobValue, int whoJsonValue, int z5, long whoGenLimit) {
      if (this.me == null) {
         return false;
      } else {
         JsonWho jsonWho = this.me.getJsonWho(tookJobValue, whoJsonValue, z5);
         if (jsonWho == null || jsonWho.isComplete() || this.jsonDoc.getLeaveCorzText().equals(jsonWho.who)) {
            return false;
         } else if (!this.me.isTextHost() && !this.me.sep.isEpochUuid()) {
            return false;
         } else if (this.me.isTextHost()) {
            String who = jsonWho.who;
            if (!WhoJsonUtils.canGenerateWho(jsonWho, this.jsonDoc.getLeaveCorzText(), System.currentTimeMillis(), whoGenLimit)) {
               return false;
            } else {
               this.teamApiContext
                  .onMultiSessionMessage(
                     "\u00a7b[MultiBuilder]\u00a7r took over job "
                        + jsonWho.id
                        + (who == null ? " from the pool" : " from " + this.getDisplayName(who) + " (idle on it)")
                        + " - it holds a block my cells wait on"
                  );
               return true;
            }
         } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("stale", whoGenLimit);
            this.handleGenJob(JobSessionType.JOB_CLAIM, jsonWho, jsonObject);
            return true;
         }
      }
   }

   private void handleGenJob(JobSessionType jobSessionType, JsonWho jsonWho, JsonObject json) {
      JsonObject jsonObject = json == null ? new JsonObject() : json;
      jsonObject.addProperty("job", jsonWho.id);
      jsonObject.addProperty("gen", jsonWho.gen);
      this.jsonDoc.handleConcurrentQueue(this.getSepSid(PayloadJson.getPayloadJson(jobSessionType, this.jsonDoc.getLeaveCorzText(), jsonObject)));
   }

   private void handleDocMulti(long timeMillis) {
      if (this.me != null) {
         int multiBuilderValue = 0;
         int value = 0;

         for (JsonWho jsonWho : this.me.getWhoDist()) {
            ExactJobData autoBuilder = this.teamApiContext.computeRegionProgress(jsonWho, jsonWho.gen);
            int docMultiValue = autoBuilder.missingCount() + autoBuilder.wrongCount() + autoBuilder.unobservedCount();
            int max = Math.min(autoBuilder.getMaxCellCount(), jsonWho.placedValue);
            if (max > jsonWho.reportedPlacedCount) {
               jsonWho.timeMillis = timeMillis;
               if (!this.me.isTextHost()) {
                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("placed", max);
                  this.handleGenJob(JobSessionType.PROGRESS_UPDATE, jsonWho, jsonObject);
               }
            }

            jsonWho.reportedPlacedCount = autoBuilder.isUnobserved() ? max : Math.max(jsonWho.reportedPlacedCount, max);
            value += jsonWho.reportedPlacedCount;
            multiBuilderValue += docMultiValue;
            if (autoBuilder.isCellUnobserved()) {
               Long multibuildermap = this.multiBuilderMap.get(jsonWho.id);
               if (multibuildermap == null || multibuildermap != jsonWho.gen) {
                  this.multiBuilderMap.put(jsonWho.id, jsonWho.gen);
                  if (this.me.isTextHost()) {
                     this.handleCompletionsIncompatible(jsonWho, autoBuilder, autoBuilder);
                  } else {
                     JsonObject o = new JsonObject();
                     autoBuilder.writeUnseenPropertiesToJson(o);
                     this.jsonDoc
                        .handleConcurrentQueue(this.getSepSid(PayloadJson.getPayloadJson(JobSessionType.JOB_COMPLETE, this.jsonDoc.getLeaveCorzText(), o)));
                  }
               }
            } else {
               this.multiBuilderMap.remove(jsonWho.id);
               if (jsonWho.isComplete() && autoBuilder.isUnobserved()) {
                  if (this.me.isTextHost()) {
                     jsonWho.st = TaskStatus.ASSIGNED;
                     jsonWho.who = this.jsonDoc.getLeaveCorzText();
                     jsonWho.reportedPlacedCount = max;
                     jsonWho.timeMillis = timeMillis;
                  } else {
                     JsonObject jsonObject3 = new JsonObject();
                     autoBuilder.writeUnseenPropertiesToJson(jsonObject3);
                     this.jsonDoc
                        .handleConcurrentQueue(this.getSepSid(PayloadJson.getPayloadJson(JobSessionType.JOB_COMPLETE, this.jsonDoc.getLeaveCorzText(), jsonObject3)));
                  }
               }
            }
         }

         this.multiBuilderSet.clear();
         this.multiBuilderValue = multiBuilderValue;
         this.docMultiValue = value;
      }
   }

   private void updateDimAnc() {
      if (this.data2TeamMatching == null && this.dimJsonData != null) {
         this.data2TeamMatching = new PlacementClaimResolver.Claim(this.ancHostText, this.dimJsonData, this.hostIntentHostAnc);
      }
   }

   public boolean isErrorConnecting() {
      return this.connectingAwaitingType == HostMulti.Phase.ERROR && !this.error.isEmpty();
   }

   public boolean isConnecting() {
      return this.connectingAwaitingType == HostMulti.Phase.CONNECTING;
   }

   public String getAncHostText() {
      return this.ancHostText;
   }

   private void handleGenCompletions(PayloadJson payloadJson) {
      String formatPayload = payloadJson.formatPayload("job");
      if (formatPayload != null) {
         JsonWho distHost = this.me.getDistHost(formatPayload);
         if (distHost != null) {
            if (!payloadJson.from.equals(distHost.who)) {
               this.completionsStaleCount++;
            } else {
               long payload = payloadJson.getPayload("gen", -1L);
               if (payload != distHost.gen) {
                  this.completionsCellCount++;
               } else {
                  this.handleCompletionsIncompatible(distHost, this.teamApiContext.computeRegionProgress(distHost, distHost.gen), ExactJobData.getUnseenRev(payloadJson.payload));
               }
            }
         }
      }
   }

   private void handleSepDoc(DimJsonData dimJsonData, String sepDocText) {
      this.handleBlockedWhy(sepDocText);
      this.handleCdigestChash(dimJsonData);
      if (this.me.isTextHost()) {
         this.me.sep = this.me.sep.getEpochUuid();
         this.displayEpoch = this.me.sep;
         this.nextSequenceNumber = Math.max(this.nextSequenceNumber + 1L, this.jsonDoc.getVersionNetTime() + 1L);
         this.jsonDoc.handleJsonTime(this.me.getAncHost(), this.nextSequenceNumber);
      }

      this.updateIntentMulti();
   }

   public void handleMultiBuilder(int multiBuilderValue, int whoJsonValue, int z5) {
      if (this.me != null) {
         JsonWho jsonWho = this.me.getJsonWho(multiBuilderValue, whoJsonValue, z5);
         if (jsonWho != null) {
            this.multiBuilderSet.add(jsonWho.id);
            this.timeMillisMultiBuilder = 0L;
         }
      }
   }

   public boolean isAdopted() {
      return this.me != null && this.adopted != null;
   }

   private static String shortenId(String substringText) {
      return substringText == null ? "?" : substringText.substring(0, Math.min(6, substringText.length()));
   }

   private void handleJobGen(PayloadJson payloadJson) {
      if (this.me != null && payloadJson != null) {
         if (this.canSepOld(payloadJson)) {
            switch (payloadJson.type) {
               case BUILD_INFO:
                  this.handleIncompatibleApi(payloadJson);
                  break;
               case JOB_COMPLETE:
                  if (this.me.isTextHost()) {
                     this.handleGenCompletions(payloadJson);
                  }
                  break;
               case JOB_FAILED:
               case JOB_RELEASE:
                  if (this.me.isTextHost()) {
                     JsonWho distHost = this.me.getDistHost(payloadJson.formatPayload("job"));
                     if (distHost != null && distHost.canWhoGen(payloadJson.from, payloadJson.getPayload("gen", -1L))) {
                        distHost.handleWhoGen(payloadJson.type == JobSessionType.JOB_FAILED ? TaskStatus.FAILED : TaskStatus.UNASSIGNED);
                        if (payloadJson.formatText("why", "").startsWith("blocked")) {
                           distHost.rt++;
                        }
                     } else if (distHost != null) {
                        this.completionsCellCount++;
                     }
                  }
                  break;
               case BLOCK_RESERVED:
                  long payload = payloadJson.getPayload("cell", Long.MIN_VALUE);
                  if (payload != Long.MIN_VALUE && !payloadJson.from.equals(this.jsonDoc.getLeaveCorzText())) {
                     this.multiDocPositionMap.put(payload, System.currentTimeMillis() + 4000L);
                  }
                  break;
               case PROGRESS_UPDATE:
                  if (this.me.isTextHost()) {
                     JsonWho distHost2 = this.me.getDistHost(payloadJson.formatPayload("job"));
                     int intersectsCenterBound = payloadJson.getPayload("placed", -1);
                     if (distHost2 != null && intersectsCenterBound >= 0 && distHost2.canWhoGen(payloadJson.from, payloadJson.getPayload("gen", -1L))) {
                        if (intersectsCenterBound > distHost2.reportedPlacedCount) {
                           distHost2.timeMillis = System.currentTimeMillis();
                        }

                        distHost2.reportedPlacedCount = Math.max(distHost2.reportedPlacedCount, Math.min(intersectsCenterBound, distHost2.placedValue));
                     }
                  }
                  break;
               case JOB_CLAIM:
                  if (this.me.isTextHost()) {
                     JsonWho jsonWho = this.me.getDistHost(payloadJson.formatPayload("job"));
                     JsonMats jsonMats = this.me.getJsonMats(payloadJson.from);
                     long maxPayload = Math.max(10000L, payloadJson.getPayload("stale", 30000L));
                     if (jsonWho != null && jsonMats != null && jsonMats.multiBuilderFlag) {
                        String who = jsonWho.who;
                        if (WhoJsonUtils.canGenerateWho(jsonWho, payloadJson.from, System.currentTimeMillis(), maxPayload)) {
                           this.teamApiContext
                              .onMultiSessionMessage(
                                 "\u00a7b[MultiBuilder]\u00a7r job "
                                    + jsonWho.id
                                    + " handed to "
                                    + this.getDisplayName(payloadJson.from)
                                    + " - their cells wait on it"
                                    + (who == null ? " (it sat in the pool)" : " (" + this.getDisplayName(who) + " was idle on it)")
                              );
                        }
                     }
                  }
               case BLOCK_COMPLETE:
               case BLOCK_INVALID:
               case MATERIAL_STATE:
               case PLAYER_STATE:
               case HEARTBEAT:
               case JOB_ASSIGN:
               case SESSION_CREATE:
               case SESSION_JOIN:
               case SESSION_LEAVE:
               case HOST_CHANGED:
            }
         }
      }
   }

   public String getTeamMultiText() {
      return this.teamMultiText;
   }

   private void handleTeamApi(DimJsonData dimJsonData) {
      OverworldNetherUtils.Decision seatAdopt = OverworldNetherUtils.getSeatAdopt(
         this.adopted,
         this.teamApiContext.getPlacementSnapshot(),
         dimJsonData,
         this.teamApiContext.getSessionKey(),
         this.ancHostFlag,
         this.jsonDoc.getLeaveCorzText().equals(this.ancHostText)
      );
      switch (seatAdopt.action()) {
         case REFUSE_WRONG_DIMENSION:
            if (this.teamApiText == null) {
               this.teamApiContext.onMultiSessionMessage("\u00a7e[MultiBuilder]\u00a7r " + seatAdopt.userMessage());
            }

            this.teamApiText = OverworldNetherUtils.formatMinecraftOverworld(dimJsonData.dimension());
            this.teamPlacementText = seatAdopt.userMessage();
            break;
         case SUPPRESSED_BY_MANUAL_OVERRIDE:
            this.teamPlacementText = seatAdopt.userMessage();
            break;
         case ALREADY_MATCHES:
            if (this.me == null) {
               this.handleCdigestChash(dimJsonData);
            }
            break;
         case SEAT_AS_IS:
         case ADOPT:
            this.teamApiText = null;
            boolean teamApiFlag = this.teamApiContext.getPlacementSnapshot() == null || !this.teamApiContext.getPlacementSnapshot().canPlacementDimension(dimJsonData);
            if (teamApiFlag && !this.teamApiContext.canAcceptTeamPlacement(dimJsonData)) {
               this.teamPlacementText = "Could not move your schematic to the team's placement";
               return;
            }

            if (!seatAdopt.userMessage().isEmpty()) {
               this.teamApiContext.onMultiSessionMessage("\u00a7b[MultiBuilder]\u00a7r " + seatAdopt.userMessage());
            }

            if (this.me == null) {
               this.handleCdigestChash(dimJsonData);
            } else {
               this.handleSepDoc(dimJsonData, "the team moved the build");
            }
      }
   }

   private void updateTeamMatching() {
      boolean teamMatchingFlag = !this.jsonIteratorText.isEmpty() && this.jsonIteratorText.equals(this.jsonDoc.getLeaveCorzText());
      if (teamMatchingFlag) {
         this.updateDimAnc();
         PlacementClaimResolver.Claim data = PlacementClaimResolver.pickPreferredClaim(this.intentHostMap.values());
         if (data == null && this.data2TeamMatching == null) {
            return;
         }

         if (PlacementClaimResolver.isPreferredClaim(this.data2TeamMatching, data)) {
            this.data2TeamMatching = data;
         }

         if (this.data2TeamMatching == null) {
            return;
         }

         this.dimJsonData = this.data2TeamMatching.anchor();
         this.ancHostText = this.data2TeamMatching.memberId();
         this.hostIntentHostAnc = this.data2TeamMatching.hostIntent();
      }

      if (this.dimJsonData == null) {
         this.teamPlacementText = "Matching the team's placement...";
      } else {
         this.handleTeamApi(this.dimJsonData);
      }
   }

   private void handleIntentAdopted(long timeMillis) {
      if (!this.jsonDoc.isVersionFlag()) {
         boolean intentAdoptedFlag = !this.intentFlag
            || this.dimJsonData == null
            || this.adopted != null && !this.adopted.canPlacementDimension(this.dimJsonData)
            || this.hasUnknownOfflineMember();
         if (intentAdoptedFlag) {
            if (!this.intentFlag || timeMillis - this.timeMillisIntentAdopted >= 5000L) {
               this.intentFlag = true;
               this.timeMillisIntentAdopted = timeMillis;
               JsonObject propertyCommit = this.teamApiContext.getBuildProtoCommit().getPropertyCommit();
               DimJsonData builderAuto = this.teamApiContext.getPlacementSnapshot();
               if (builderAuto != null && builderAuto.isDimensionPlacement()) {
                  propertyCommit.add("anchor", builderAuto.getDimProperty());
                  propertyCommit.addProperty("hostIntent", this.hostIntent);
                  propertyCommit.addProperty("adopted", this.adopted == null ? "" : this.adopted.getHexString());
                  this.intentHostMap
                     .put(this.jsonDoc.getLeaveCorzText(), new PlacementClaimResolver.Claim(this.jsonDoc.getLeaveCorzText(), builderAuto, this.hostIntent));
               }

               this.jsonDoc.handleConcurrentQueue(this.getSepSid(PayloadJson.getPayloadJson(JobSessionType.BUILD_INFO, this.jsonDoc.getLeaveCorzText(), propertyCommit)));
            }
         }
      }
   }

   public boolean isConnectingAdopted() {
      return this.connectingAwaitingType == HostMulti.Phase.ACTIVE && this.me != null && this.adopted != null;
   }

   private void handleIncompatibleApi(PayloadJson payloadJson) {
      if (payloadJson.from != null && !payloadJson.from.equals(this.jsonDoc.getLeaveCorzText())) {
         ProtoCommitData protoBuild = ProtoCommitData.getProtoBuild(payloadJson.payload);
         ProtoCommitData dirtyGen = this.teamApiContext.getBuildProtoCommit();
         ProtoCommitData.Verdict mismatchProtocolType = dirtyGen.getMismatchDigest(protoBuild);
         JsonMats jsonMats = this.me.getJsonMats(payloadJson.from);
         if (jsonMats != null) {
            jsonMats.mismatchProtocolType = mismatchProtocolType;
         }

         this.offlineApiVerdictMap.put(payloadJson.from, mismatchProtocolType);
         if (mismatchProtocolType == ProtoCommitData.Verdict.OK) {
            this.handleIntentHost(payloadJson);
         } else {
            if (this.apiIncompatibleStringSet.add(payloadJson.from)) {
               this.apiIncompatibleCount++;
               this.teamApiContext
                  .onMultiSessionMessage(
                     "\u00a7c[MultiBuilder]\u00a7r " + this.getDisplayName(payloadJson.from) + " is running a different build - they will not be given work."
                  );
               this.teamApiContext.onMultiSessionMessage("\u00a77" + dirtyGen.formatDigestVersion(protoBuild, mismatchProtocolType).replace("\n", "\n\u00a77"));
            }
         }
      }
   }

   public boolean isHostIntentHostAnc() {
      return this.hostIntentHostAnc;
   }

   public boolean isConnectingError() {
      return this.connectingAwaitingType == HostMulti.Phase.ERROR
         || this.connectingAwaitingType == HostMulti.Phase.CLOSED
         || this.jsonDoc.getEnteringConnectedType() == JsonDoc.State.ERROR
         || this.jsonDoc.getEnteringConnectedType() == JsonDoc.State.CLOSED;
   }

   private void handleErrorConnecting(String errorConnectingText) {
      if (this.connectingAwaitingType != HostMulti.Phase.ERROR) {
         this.connectingAwaitingType = HostMulti.Phase.ERROR;
         this.error = errorConnectingText == null ? "error" : errorConnectingText;
         this.teamPlacementText = "Error: " + this.error;
         this.teamApiContext.onMultiSessionMessage("\u00a7c[MultiBuilder]\u00a7r session error: " + this.error);
      }

      try {
         this.jsonDoc.updateLeaveCorz();
      } catch (Exception e) {
      }
   }

   private void handleDocApi(long docApiValue) {
      this.updateHostAnc();
      int whoUnassigned = WhoJsonUtils.countUnassignedWho(this.me, docApiValue);
      int countUnassignedWhoStrict = WhoJsonUtils.countUnassignedWhoStrict(this.me, docApiValue);
      if (countUnassignedWhoStrict > 0) {
         this.teamApiContext.onMultiSessionMessage("\u00a7b[MultiBuilder]\u00a7r rotated " + countUnassignedWhoStrict + " stalled job(s) back to the pool");
      }

      int dist = WhoJsonUtils.getHostDistance(this.me, docApiValue, this.teamApiContext.isMaterialSharingEnabled());
      this.nextSequenceNumber = Math.max(this.nextSequenceNumber + 1L, this.jsonDoc.getVersionNetTime() + 1L);
      this.jsonDoc.handleJsonTime(this.me.getAncHost(), this.nextSequenceNumber);
   }

   private void updateIntentMulti() {
      this.multiBuilderMap.clear();
      this.intentFlag = false;
   }

   public boolean isMeFullyComplete() {
      return this.me != null && this.me.areAllDistHostsComplete();
   }

   public boolean canAdoptedMulti(int adoptedMultiValue, int whoJsonValue, int z5) {
      if (this.me == null || this.adopted == null) {
         return false;
      } else if (!this.me.hasDistHosts()) {
         return true;
      } else {
         Long long2 = packBlockPos(adoptedMultiValue, whoJsonValue, z5);
         Long multibuildermap = this.multiDocPositionMap.get(long2);
         return multibuildermap != null && multibuildermap > System.currentTimeMillis() ? false : this.me.canWhoText(adoptedMultiValue, whoJsonValue, z5);
      }
   }

   public String formatDocJson() {
      return this.jsonDoc.getLeaveCorzText();
   }

   public String getTeamPlacementText() {
      return this.teamPlacementText;
   }

   private void handleJsonIterator(JsonArray jsonArray, String text) {
      if (text != null && !text.isEmpty()) {
         this.jsonIteratorText = text;
      }

      if (jsonArray != null) {
         HashSet hashSet = new HashSet();

         for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) {
               JsonMats seenOnline = JsonMats.getSeenOnline(jsonElement.getAsJsonObject());
               if (seenOnline != null) {
                  hashSet.add(seenOnline.whoText);
                  this.apiIncompatibleStringMap.put(seenOnline.whoText, seenOnline.formatOfflineUuid());
               }
            }
         }

         if (!hashSet.isEmpty()) {
            this.intentHostMap.keySet().removeIf(jsonIteratorText -> !hashSet.contains(jsonIteratorText));
            this.multiTeamFlag = true;
         }
      }
   }

   public Set<Long> getEmptySet() {
      return this.emptySet;
   }

   private String getDisplayName(String substringText) {
      if (substringText == null) {
         return "?";
      } else if (substringText.equals(this.jsonDoc.getLeaveCorzText())) {
         return "me";
      } else {
         JsonMats jsonMats = this.me != null ? this.me.getJsonMats(substringText) : null;
         return jsonMats != null && jsonMats.uuidOfflineText != null && !jsonMats.uuidOfflineText.isEmpty()
            ? jsonMats.uuidOfflineText
            : substringText.substring(0, Math.min(6, substringText.length()));
      }
   }

   private void handleConnectingAwaiting(String error) {
      if (this.connectingAwaitingType != HostMulti.Phase.CLOSED) {
         this.connectingAwaitingType = HostMulti.Phase.CLOSED;
         this.error = error;
         this.teamPlacementText = "Closed: " + error;
         this.teamApiContext.onMultiSessionMessage("\u00a7e[MultiBuilder]\u00a7r session ended (" + error + ")");
      }
   }

   public String getTeamApiText() {
      return this.teamApiText;
   }

   public boolean isAdoptedApi() {
      if (this.me != null && this.adopted != null) {
         DimJsonData builderAuto = this.teamApiContext.getPlacementSnapshot();
         return builderAuto != null && builderAuto.isDimensionPlacement() && !builderAuto.canPlacementDimension(this.adopted);
      } else {
         return false;
      }
   }

   public DimJsonData getAdopted() {
      return this.adopted;
   }

   public int getCompletionsRefusedCount() {
      return this.completionsRefusedCount;
   }

   public CdigestChashData getCdigestChashData() {
      return this.cdigestChashData;
   }

   public interface Context {
      int getSchematicDimensionInt();

      String getTeamCodeText();

      ProtoCommitData getBuildProtoCommit();

      int getTargetBlockCount();

      int getOriginY();

      int getSchematicDimension2();

      String getSessionKey();

      boolean hasSchematicLoaded();

      int getSchematicDimension4();

      NearestRegionType getNearestRegionType();

      void forEachTargetCell(WhoJsonUtils.CellConsumer cellConsumer);

      double getX();

      String getSchematicName();

      Set<String> getTeammateNames();

      int getRotationIndex();

      boolean canAcceptTeamPlacement(DimJsonData dimJsonData);

      boolean isMaterialSharingEnabled();

      String getUsername();

      JsonProperty getInventoryMaterialCounts();

      DimJsonData getPlacementSnapshot();

      boolean isBuilderActive();

      RefillingOfflineType getBuilderStatusType();

      double getY();

      ExactJobData computeRegionProgress(JsonWho jsonWho, long autoBuilderValue);

      CdigestChashData getSchematicDigest();

      int getOriginX();

      String getUuid();

      void onMultiSessionMessage(String multiSessionText);

      double getZ();

      int getMirrorIndex();

      int getOriginZ();
   }

   private static enum Phase {
      CONNECTING,
      AWAITING_ANCHOR,
      ACTIVE,
      CLOSED,
      ERROR;
   }
}
