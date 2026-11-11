package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface DocJsonApi {
   DocJsonApi.Result<DocJsonApi.Membership> getPropertyMismatch(String propertyMismatchText, String propertyMismatchText2, String propertyMismatchText3, String propertyMismatchText4);

   DocJsonApi.Result<DocJsonApi.Snapshot> getRetainedMessages(
      String retainedMessagesText, String retainedMessagesText2, JsonObject json, JsonArray jsonArray, long retainedMessagesValue, JsonObject jsonObject, long retainedMessagesValue2, long retainedMessagesValue3
   );

   void handleMemberPost(String memberPostText, String leaveCorzText);

   public static final class Membership {
      public final String offlineText;
      public final String membershipText;
      public final String offlineVersionJsonText;
      public final String offlineVersionText;

      public Membership(String offlineText, String text, String text2, String text3) {
         this.offlineText = offlineText;
         this.membershipText = text;
         this.offlineVersionJsonText = text2;
         this.offlineVersionText = text3;
      }
   }

   public static final class Result<T> {
      public final T offlineObject;
      public final String closedOfflineText;
      public final boolean offlineFlag;

      public static <T> DocJsonApi.Result<T> failure(String docJsonText) {
         return new DocJsonApi.Result<>(false, null, docJsonText);
      }

      private Result(boolean offlineFlag, T t, String closedOfflineText) {
         this.offlineFlag = offlineFlag;
         this.offlineObject = (T)t;
         this.closedOfflineText = closedOfflineText;
      }

      public static <T> DocJsonApi.Result<T> success(T t) {
         return new DocJsonApi.Result<>(true, (T)t, null);
      }
   }

   public static final class Snapshot {
      public final JsonArray jsonArray;
      public final boolean versionJsonActive;
      public final long versionTime;
      public final String versionText;
      public final JsonObject jsonObject;
      public final long snapshotTime;
      public final JsonArray jsonArray2VersionJson;
      public final String versionJsonText;

      public Snapshot(
         String text, String versionText, boolean flag, long versionTime, JsonArray jsonArray, JsonObject jsonObject, JsonArray jsonArray2, long time
      ) {
         this.versionJsonText = text;
         this.versionText = versionText;
         this.versionJsonActive = flag;
         this.versionTime = versionTime;
         this.jsonArray = jsonArray;
         this.jsonObject = jsonObject;
         this.jsonArray2VersionJson = jsonArray2;
         this.snapshotTime = time;
      }
   }
}
