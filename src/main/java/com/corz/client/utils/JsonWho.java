package com.corz.client.utils;

import com.google.gson.JsonObject;
import com.corz.client.utils.TaskStatus;
import com.corz.client.utils.JsonProperty;

public final class JsonWho {
   public final int whoX;
   public final int placedValue;
   public final int whoValue;
   public final int autoBuilderValue;
   public final String id;
   public final int cdigestChashValue;
   public int reportedPlacedCount;
   public long timeMillis;
   public long gen;
   public final int jsonWhoValue;
   public final int distHostValue;
   public TaskStatus st = TaskStatus.UNASSIGNED;
   public long am;
   public final int boxMinY;
   public int rt;
   public String who;
   public final int boxMinZ;
   public final JsonProperty jsonProperty;
   public final int boxMinX;
   public String whoGenText;
   public final int whoZ;

   public double getAmount() {
      return (this.boxMinZ + this.jsonWhoValue) / 2.0 + 0.5;
   }

   public boolean canWhoGen(String whoGenText, long whoGenValue) {
      return whoGenText != null && whoGenText.equals(this.who) && whoGenValue == this.gen && !this.isComplete();
   }

   public double getAmountBoxMinX() {
      return (this.boxMinX + this.cdigestChashValue) / 2.0 + 0.5;
   }

   public boolean isFlag() {
      return this.st.isUnavailable();
   }

   public void handleWhoGen(TaskStatus assignedUnassignedType) {
      if (this.who != null) {
         this.gen++;
         this.whoGenText = this.who;
      }

      this.who = null;
      this.st = assignedUnassignedType == null ? TaskStatus.UNASSIGNED : assignedUnassignedType;
      this.am = 0L;
   }

   public void handleWhoGen(JsonObject json) {
      if (json != null) {
         this.who = json.has("who") ? json.get("who").getAsString() : null;
         this.st = TaskStatus.parseOrUnassigned(json.has("st") ? json.get("st").getAsString() : null);
         this.rt = json.has("rt") ? json.get("rt").getAsInt() : 0;
         this.reportedPlacedCount = json.has("pl") ? json.get("pl").getAsInt() : 0;
         this.am = json.has("am") ? json.get("am").getAsLong() : 0L;
         this.timeMillis = json.has("lp") ? json.get("lp").getAsLong() : 0L;
         this.gen = json.has("lg") ? json.get("lg").getAsLong() : 0L;
      }
   }

   public double getAmountBoxMinY() {
      return (this.boxMinY + this.autoBuilderValue) / 2.0 + 0.5;
   }

   public JsonObject getPropertyWho() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", this.id);
      if (this.who != null) {
         jsonObject.addProperty("who", this.who);
      }

      jsonObject.addProperty("st", this.st.name());
      if (this.rt > 0) {
         jsonObject.addProperty("rt", this.rt);
      }

      if (this.reportedPlacedCount > 0) {
         jsonObject.addProperty("pl", this.reportedPlacedCount);
      }

      if (this.am > 0L) {
         jsonObject.addProperty("am", this.am);
      }

      if (this.timeMillis > 0L) {
         jsonObject.addProperty("lp", this.timeMillis);
      }

      if (this.gen > 0L) {
         jsonObject.addProperty("lg", this.gen);
      }

      return jsonObject;
   }

   public JsonWho(
      String jsonWhoText, int value, int value2, int value3, int value4, int value5, int value6, int value7, int value8, int value9, int placedValue, JsonProperty jsonProperty, int value10
   ) {
      this.id = jsonWhoText;
      this.whoValue = value;
      this.distHostValue = value2;
      this.whoZ = value3;
      this.boxMinX = value4;
      this.boxMinY = value5;
      this.boxMinZ = value6;
      this.cdigestChashValue = value7;
      this.autoBuilderValue = value8;
      this.jsonWhoValue = value9;
      this.placedValue = placedValue;
      this.jsonProperty = jsonProperty;
      this.whoX = value10;
   }

   public boolean isComplete() {
      return this.st == TaskStatus.COMPLETE;
   }
}
