package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.corz.client.utils.PayloadJson;
import com.corz.client.utils.DocJsonApi;

public final class JsonDoc {
   private JsonObject jsonObject;
   private volatile long versionTime;
   private long maxSequenceNumber;
   private volatile JsonDoc.State enteringConnectedType = JsonDoc.State.IDLE;
   private volatile long versionNetTime;
   private volatile String offlineText;
   private volatile String versionText;
   private volatile long docTimestamp;
   private volatile Thread thread;
   private volatile boolean equals;
   private volatile long jsonValue;
   private final ConcurrentLinkedQueue<PayloadJson> concurrentLinkedQueue;
   private volatile String offlineTextNetVersion;
   private volatile JsonObject jsonObjectJson;
   private JsonArray jsonArray;
   private volatile String offlineText4NetVersion;
   private volatile JsonObject jsonObjectNetVersion;
   private long versionJsonValue;
   private final Object versionObject;
   private volatile String unexpected = "";
   private final DocJsonApi versionJsonDocJsonApi;
   private long docJsonValue;
   private volatile boolean versionFlag;
   private volatile String leaveCorzText;
   private volatile int versionNetValue;
   private volatile String leaveOfflineText;
   private long longValue;
   private volatile boolean netCorzFlag;
   private volatile String offlineText2NetVersion;
   private final ConcurrentLinkedQueue<PayloadJson> doc;
   private volatile String versionJsonText;

   public long getVersionNetTime() {
      return Math.max(0L, this.versionNetTime);
   }

   public boolean isVersionFlag() {
      return this.versionFlag;
   }

   private void updateVersionJson() {
      ArrayList arrayList = new ArrayList();

      PayloadJson payloadJson;
      while (arrayList.size() < 64 && (payloadJson = this.concurrentLinkedQueue.poll()) != null) {
         arrayList.add(payloadJson);
      }

      JsonArray jsonArray = new JsonArray();

      for (PayloadJson payloadJson2 : (Iterable<com.corz.client.utils.PayloadJson>) (arrayList)) {
         jsonArray.add(payloadJson2.getSeqProperty());
      }

      boolean versionJsonFlag = this.leaveCorzText != null && this.leaveCorzText.equals(this.versionJsonText);
      JsonObject jsonObject = versionJsonFlag ? this.jsonObjectNetVersion : null;
      DocJsonApi.Result retainedMessages = this.versionJsonDocJsonApi
         .getRetainedMessages(this.leaveOfflineText, this.leaveCorzText, this.jsonObjectJson, jsonArray, this.maxSequenceNumber, jsonObject, this.jsonValue, this.docTimestamp);
      if (!retainedMessages.offlineFlag) {
         for (PayloadJson concurrentQueueText : (Iterable<com.corz.client.utils.PayloadJson>) (arrayList)) {
            this.concurrentLinkedQueue.offer(concurrentQueueText);
         }

         if (!"NOT_FOUND".equals(retainedMessages.closedOfflineText) && !"CLOSED".equals(retainedMessages.closedOfflineText)) {
            this.equals = true;
            this.unexpected = retainedMessages.closedOfflineText;
         } else {
            this.enteringConnectedType = JsonDoc.State.CLOSED;
         }
      } else {
         this.equals = false;
         this.unexpected = "";
         DocJsonApi.Snapshot offlineObject = (DocJsonApi.Snapshot)retainedMessages.offlineObject;
         if (offlineObject.versionJsonActive) {
            this.enteringConnectedType = JsonDoc.State.CLOSED;
         } else {
            this.versionJsonText = offlineObject.versionJsonText != null ? offlineObject.versionJsonText : this.versionJsonText;
            this.versionText = offlineObject.versionText != null ? offlineObject.versionText : this.versionText;
            this.versionTime = offlineObject.versionTime;
            boolean versionFlag = this.versionFlag;
            if (versionFlag) {
               this.versionFlag = false;
               long versionNetTime = offlineObject.jsonObject != null && offlineObject.jsonObject.has("version") ? offlineObject.jsonObject.get("version").getAsLong() : 0L;
               this.versionNetTime = versionNetTime;
               int versionNetValue = 0;
               if (offlineObject.jsonArray2VersionJson != null) {
                  for (JsonElement jsonElement : offlineObject.jsonArray2VersionJson) {
                     if (jsonElement.isJsonObject()) {
                        versionNetValue++;
                     }
                  }
               }

               this.versionNetValue = versionNetValue;
            }

            synchronized (this.versionObject) {
               if (offlineObject.jsonArray != null) {
                  this.jsonArray = offlineObject.jsonArray;
               }

               if (offlineObject.jsonObject != null && offlineObject.jsonObject.has("version")) {
                  long longValue = offlineObject.jsonObject.get("version").getAsLong();
                  if (longValue > this.longValue && longValue > this.versionNetTime) {
                     this.jsonObject = offlineObject.jsonObject;
                     this.longValue = longValue;
                  }
               }

               this.versionJsonValue = Math.max(this.versionJsonValue, offlineObject.snapshotTime);
            }

            if (versionFlag) {
               this.maxSequenceNumber = Math.max(this.maxSequenceNumber, offlineObject.snapshotTime);
            } else if (offlineObject.jsonArray2VersionJson != null) {
               long versionJsonBound = this.maxSequenceNumber;

               for (JsonElement jsonElement2 : offlineObject.jsonArray2VersionJson) {
                  if (jsonElement2.isJsonObject()) {
                     PayloadJson payloadSeq = PayloadJson.getPayloadSeq(jsonElement2.getAsJsonObject());
                     if (payloadSeq != null && (payloadSeq.seq < 0L || payloadSeq.seq > this.maxSequenceNumber)) {
                        if (this.leaveCorzText != null && this.leaveCorzText.equals(payloadSeq.from)) {
                           versionJsonBound = Math.max(versionJsonBound, payloadSeq.seq);
                        } else {
                           this.doc.offer(payloadSeq);
                           if (payloadSeq.seq > versionJsonBound) {
                              versionJsonBound = payloadSeq.seq;
                           }
                        }
                     }
                  }
               }

               this.maxSequenceNumber = Math.max(this.maxSequenceNumber, Math.max(versionJsonBound, offlineObject.snapshotTime));
            }
         }
      }
   }

   public void handleConcurrentQueue(PayloadJson payloadJson) {
      if (payloadJson != null) {
         this.concurrentLinkedQueue.offer(payloadJson);
      }
   }

   public JsonDoc(DocJsonApi api) {
      this.jsonObjectJson = new JsonObject();
      this.versionObject = new Object();
      this.jsonArray = new JsonArray();
      this.longValue = -1L;
      this.docJsonValue = -1L;
      this.versionNetTime = -1L;
      this.doc = new ConcurrentLinkedQueue<>();
      this.concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
      this.versionJsonDocJsonApi = api;
   }

   public void updateLeaveCorz() {
      this.netCorzFlag = false;
      Thread thread = this.thread;
      if (thread != null) {
         thread.interrupt();
      }

      this.thread = null;
      String leaveOfflineText = this.leaveOfflineText;
      String leaveCorzText = this.leaveCorzText;
      if (leaveOfflineText != null && leaveCorzText != null) {
         Thread thread2 = new Thread(() -> {
            try {
               this.versionJsonDocJsonApi.handleMemberPost(leaveOfflineText, leaveCorzText);
            } catch (Exception var4x) {
            }
         }, "corz-build-leave");
         thread2.setDaemon(true);
         thread2.start();
      }

      this.enteringConnectedType = JsonDoc.State.IDLE;
   }

   public String getVersionJsonText() {
      return this.versionJsonText;
   }

   public void handleDocJson(JsonDoc.Consumer consumer) {
      JsonObject jsonObject = null;
      long docJsonValue = -1L;
      JsonArray jsonArray;
      synchronized (this.versionObject) {
         jsonArray = this.jsonArray;
         if (this.jsonObject != null && this.longValue > this.docJsonValue) {
            jsonObject = this.jsonObject;
            docJsonValue = this.longValue;
            this.docJsonValue = this.longValue;
            this.docTimestamp = this.longValue;
         }
      }

      if (jsonArray != null) {
         consumer.handleHostMulti(jsonArray, this.versionJsonText, this.versionTime);
      }

      if (jsonObject != null && jsonObject.has("doc") && jsonObject.get("doc").isJsonObject()) {
         consumer.handleHostMulti(docJsonValue, jsonObject.getAsJsonObject("doc"));
      }

      PayloadJson payloadJson;
      while ((payloadJson = this.doc.poll()) != null) {
         consumer.handleHostMulti(payloadJson);
      }
   }

   public void handleJsonTime(JsonObject jsonObject, long time) {
      this.jsonObjectNetVersion = jsonObject;
      this.jsonValue = time;
   }

   public JsonDoc.State getEnteringConnectedType() {
      return this.enteringConnectedType;
   }

   private void updateUnexpectedEntering() {
      while (this.netCorzFlag) {
         try {
            switch (this.enteringConnectedType) {
               case ENTERING:
                  this.updateOfflineText();
                  break;
               case CONNECTED:
                  this.updateVersionJson();
                  break;
               default:
                  return;
            }
         } catch (Exception e) {
            this.unexpected = "unexpected";
         }

         try {
            Thread.sleep(this.equals ? 3000L : 900L);
         } catch (InterruptedException e2) {
            return;
         }
      }
   }

   public String getVersionText() {
      return this.versionText;
   }

   private void updateOfflineText() {
      DocJsonApi.Result propertyMismatch = this.versionJsonDocJsonApi.getPropertyMismatch(this.offlineTextNetVersion, this.offlineText2NetVersion, this.offlineText, this.offlineText4NetVersion);
      if (!propertyMismatch.offlineFlag) {
         this.equals = "OFFLINE".equals(propertyMismatch.closedOfflineText);
         this.unexpected = propertyMismatch.closedOfflineText;
         if (!this.equals) {
            this.enteringConnectedType = JsonDoc.State.ERROR;
         }
      } else {
         DocJsonApi.Membership offlineObject = (DocJsonApi.Membership)propertyMismatch.offlineObject;
         this.leaveOfflineText = offlineObject.offlineText;
         this.leaveCorzText = offlineObject.membershipText;
         this.versionJsonText = offlineObject.offlineVersionJsonText;
         this.versionText = offlineObject.offlineVersionText;
         this.equals = false;
         this.unexpected = "";
         this.enteringConnectedType = JsonDoc.State.CONNECTED;
      }
   }

   public String getLeaveCorzText() {
      return this.leaveCorzText;
   }

   public String getUnexpected() {
      return this.unexpected;
   }

   public void handleJson(JsonObject jsonObject) {
      if (jsonObject != null) {
         this.jsonObjectJson = jsonObject;
      }
   }

   public void handleNetVersion(String offlineText, String offlineText2, String offlineText3, String offlineText4) {
      this.updateLeaveCorz();
      this.offlineTextNetVersion = offlineText;
      this.offlineText2NetVersion = offlineText2;
      this.offlineText = offlineText3;
      this.offlineText4NetVersion = offlineText4;
      this.leaveOfflineText = null;
      this.leaveCorzText = null;
      this.versionJsonText = null;
      this.versionText = null;
      this.unexpected = "";
      this.equals = false;
      this.jsonObjectNetVersion = null;
      this.jsonValue = 0L;
      this.docTimestamp = 0L;
      synchronized (this.versionObject) {
         this.jsonArray = new JsonArray();
         this.jsonObject = null;
         this.longValue = -1L;
         this.docJsonValue = -1L;
         this.maxSequenceNumber = 0L;
         this.versionJsonValue = 0L;
         this.versionFlag = true;
         this.versionNetValue = 0;
         this.versionNetTime = -1L;
         this.doc.clear();
         this.concurrentLinkedQueue.clear();
      }

      this.enteringConnectedType = JsonDoc.State.ENTERING;
      this.netCorzFlag = true;
      Thread thread = new Thread(this::updateUnexpectedEntering, "corz-build-net");
      thread.setDaemon(true);
      this.thread = thread;
      thread.start();
   }

   public int getVersionNetValue() {
      return this.versionNetValue;
   }

   public boolean isEquals() {
      return this.equals;
   }

   public interface Consumer {
      void handleHostMulti(JsonArray jsonArray, String hostMultiText, long hostMultiValue);

      void handleHostMulti(PayloadJson payloadJson);

      void handleHostMulti(long hostMultiValue, JsonObject json);
   }

   public static enum State {
      IDLE,
      ENTERING,
      CONNECTED,
      CLOSED,
      ERROR;
   }
}
