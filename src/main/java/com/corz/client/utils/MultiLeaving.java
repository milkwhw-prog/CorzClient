package com.corz.client.utils;

import java.util.Collections;
import java.util.Set;
import com.corz.client.utils.DocJsonApi;
import com.corz.client.utils.HostMulti;
import com.corz.client.utils.CdigestChashData;
import com.corz.client.utils.DimJsonData;
import com.corz.client.utils.JsonRetained;

public final class MultiLeaving {
   private boolean multiBuilderFlag;
   private final DocJsonApi hostMultiDocJsonApi;
   private HostMulti hostMulti;
   private String multiBuilderText = "";
   private CdigestChashData cdigestChashData;

   public boolean isHostMulti() {
      return this.hostMulti != null;
   }

   public MultiLeaving(DocJsonApi api) {
      this.hostMultiDocJsonApi = api;
   }

   public boolean canHostMulti(HostMulti.Context context, boolean hostMultiFlag) {
      return this.hostMulti != null && this.hostMulti.canIntentHost(context, hostMultiFlag);
   }

   public boolean isHostMultiFlag() {
      return this.hostMulti != null && this.hostMulti.isConnecting();
   }

   public boolean canHostMulti(int hostMultiValue, int whoJsonValue, int z5) {
      return this.hostMulti == null || this.hostMulti.canAdoptedMulti(hostMultiValue, whoJsonValue, z5);
   }

   public void handleMultiHost(HostMulti.Context context) {
      if (this.hostMulti != null) {
         if (this.canHostMulti(context)) {
            context.onMultiSessionMessage("\u00a7e[MultiBuilder]\u00a7r you loaded a different schematic - leaving the team");
            this.updateMultiHost();
         } else {
            this.handleHostMulti(context);
            this.hostMulti.handleMultiDoc(context);
            if (this.hostMulti.isConnectingAwaiting()) {
               this.multiBuilderText = this.hostMulti.getError();
               this.multiBuilderFlag = true;
               this.cdigestChashData = this.hostMulti.getCdigestChashData();
               this.hostMulti = null;
            }
         }
      }
   }

   private void handleHostMulti(HostMulti.Context context) {
      if (this.hostMulti != null && this.hostMulti.isAdopted()) {
         DimJsonData builderAuto = context.getPlacementSnapshot();
         if (builderAuto != null && builderAuto.isDimensionPlacement()) {
            if (this.hostMulti.getAdopted() == null || !this.hostMulti.getAdopted().canPlacementDimension(builderAuto)) {
               this.hostMulti.updateMovedDoc();
            }
         }
      }
   }

   public boolean canHostMulti(int hostMultiValue, int whoJsonValue, int z5, long whoGenLimit) {
      return this.hostMulti != null && this.hostMulti.canTookJob(hostMultiValue, whoJsonValue, z5, whoGenLimit);
   }

   public boolean isHostMulti3() {
      return this.hostMulti != null && this.hostMulti.isMultiBuilder();
   }

   public void handleHostMultiForContext(HostMulti.Context context) {
      this.updateMultiHost();
      this.updateChashCdigest();
      this.hostMulti = new HostMulti(context, true, this.hostMultiDocJsonApi);
      this.hostMulti.updateJoiningSession();
   }

   public void handleHostMulti(String hostMultiText) {
      if (this.hostMulti != null) {
         this.hostMulti.handleBlockedWhy(hostMultiText);
      }
   }

   public HostMulti getHostMulti() {
      return this.hostMulti;
   }

   public boolean isHostMulti4() {
      return this.hostMulti != null && this.hostMulti.isAdoptedApi();
   }

   public boolean canChashCdigest(HostMulti.Context context) {
      if (!this.multiBuilderFlag) {
         return false;
      } else {
         if (this.cdigestChashData != null && context != null && context.hasSchematicLoaded()) {
            CdigestChashData builderElse = context.getSchematicDigest();
            if (builderElse != null && !this.cdigestChashData.canHash(builderElse)) {
               this.updateChashCdigest();
               return false;
            }
         }

         return true;
      }
   }

   public String formatHostMulti(int hostMultiValue, int whoJsonValue, int z5) {
      return this.hostMulti == null ? null : this.hostMulti.formatPoolWho(hostMultiValue, whoJsonValue, z5);
   }

   public CdigestChashData getHostMultiCdigestChashData() {
      return this.hostMulti == null ? null : this.hostMulti.getCdigestChashData();
   }

   public String getMultiBuilderText() {
      return this.multiBuilderText;
   }

   public void handleHostMulti(int hostMultiValue, int whoJsonValue, int z5) {
      if (this.hostMulti != null) {
         this.hostMulti.handleMultiBuilder(hostMultiValue, whoJsonValue, z5);
      }
   }

   public void updateMultiHost() {
      if (this.hostMulti != null) {
         this.multiBuilderText = this.hostMulti.getError();

         try {
            this.hostMulti.updateConnectingDoc();
         } catch (Exception e) {
         }

         this.hostMulti = null;
      }
   }

   public boolean isHostMulti5() {
      return this.hostMulti != null && this.hostMulti.isAdopted();
   }

   public MultiLeaving() {
      this(new JsonRetained());
   }

   public void updateChashCdigest() {
      this.multiBuilderFlag = false;
      this.cdigestChashData = null;
   }

   private boolean canHostMulti(HostMulti.Context context) {
      if (!context.hasSchematicLoaded()) {
         return true;
      } else {
         CdigestChashData cdigestChashData = this.hostMulti.getCdigestChashData();
         return cdigestChashData != null && !cdigestChashData.canHash(context.getSchematicDigest());
      }
   }

   public Set<Long> getHostMultiSet() {
      return this.hostMulti == null ? Collections.emptySet() : this.hostMulti.getEmptySet();
   }

   public boolean isHostMulti6() {
      return this.hostMulti != null && this.hostMulti.isConnectingError();
   }

   public boolean isHostMulti7() {
      return this.hostMulti != null && this.hostMulti.isMeFullyComplete();
   }

   public boolean isHostMulti8() {
      return this.hostMulti != null && this.hostMulti.isConnectingAdopted();
   }

   public void handleHostMulti3(HostMulti.Context context) {
      this.updateMultiHost();
      this.updateChashCdigest();
      this.hostMulti = new HostMulti(context, false, this.hostMultiDocJsonApi);
      this.hostMulti.updateJoiningSession();
   }
}
