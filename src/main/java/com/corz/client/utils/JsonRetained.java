package com.corz.client.utils;

import com.corz.client.utils.DocJsonApi.Membership;
import com.corz.client.utils.DocJsonApi.Result;
import com.corz.client.utils.DocJsonApi.Snapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.corz.client.utils.DocJsonApi;
import ai.auth.LicenseRuntime;

public final class JsonRetained implements DocJsonApi {
   private static final String BUILD_SESSIONS = "/build/sessions";

   @Override
   public void handleMemberPost(String memberPostText, String memberPostText2) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("memberId", memberPostText2);

      try {
         this.getApplicationJson("POST", "/build/sessions/" + getEncode(memberPostText) + "/leave", jsonObject);
      } catch (Exception e) {
      }
   }

   private static String formatUtf(InputStream inputStream) {
      if (inputStream == null) {
         return "";
      } else {
         try {
            InputStream inputStream2 = inputStream;

            String formatUtf;
            try {
               formatUtf = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Throwable e) {
               if (inputStream != null) {
                  try {
                     inputStream2.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return formatUtf;
         } catch (Exception e3) {
            return "";
         }
      }
   }

   @Override
   public Result<Membership> getPropertyMismatch(String propertyMismatchText, String propertyMismatchText2, String propertyMismatchText3, String propertyMismatchText4) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("buildHash", propertyMismatchText2);
      jsonObject.addProperty("name", propertyMismatchText3);
      if (propertyMismatchText4 != null) {
         jsonObject.addProperty("uuid", propertyMismatchText4);
      }

      JsonRetained.Http applicationJson = this.getApplicationJson("POST", "/build/sessions/" + getEncode(propertyMismatchText) + "/enter", jsonObject);
      if (applicationJson == null) {
         return Result.failure("OFFLINE");
      } else if (applicationJson.status == 409) {
         return Result.failure("MISMATCH");
      } else {
         return applicationJson.status >= 400 ? Result.failure(formatErrorJson(applicationJson)) : Result.success(getMemberSession(applicationJson.json));
      }
   }

   private static Membership getMemberSession(JsonObject json) {
      return new Membership(
         json.has("sessionId") ? json.get("sessionId").getAsString() : null,
         json.has("memberId") ? json.get("memberId").getAsString() : null,
         json.has("hostId") ? json.get("hostId").getAsString() : null,
         json.has("buildHash") ? json.get("buildHash").getAsString() : null
      );
   }

   @Override
   public Result<Snapshot> getRetainedMessages(
      String retainedMessagesText,
      String retainedMessagesText2,
      JsonObject json,
      JsonArray jsonArray,
      long retainedMessagesValue,
      JsonObject json2,
      long retainedMessagesValue2,
      long retainedMessagesValue3
   ) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("memberId", retainedMessagesText2);
      if (json != null) {
         jsonObject.add("state", json);
      }

      if (jsonArray != null && !jsonArray.isEmpty()) {
         jsonObject.add("outbox", jsonArray);
      }

      jsonObject.addProperty("sinceSeq", retainedMessagesValue);
      jsonObject.addProperty("knownRetainedVersion", retainedMessagesValue3);
      if (json2 != null) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("version", retainedMessagesValue2);
         jsonObject2.add("doc", json2);
         jsonObject.add("retained", jsonObject2);
      }

      JsonRetained.Http applicationJson = this.getApplicationJson("POST", "/build/sessions/" + getEncode(retainedMessagesText) + "/poll", jsonObject);
      if (applicationJson == null) {
         return Result.failure("OFFLINE");
      } else if (applicationJson.status == 404) {
         return Result.failure("NOT_FOUND");
      } else if (applicationJson.status >= 400) {
         return Result.failure(formatErrorJson(applicationJson));
      } else {
         JsonObject json3 = applicationJson.json;
         JsonObject asJsonObject3 = null;
         if (json3.has("retained") && json3.get("retained").isJsonObject()) {
            asJsonObject3 = json3.getAsJsonObject("retained");
         }

         Snapshot snapshot = new Snapshot(
            json3.has("hostId") ? json3.get("hostId").getAsString() : null,
            json3.has("buildHash") ? json3.get("buildHash").getAsString() : null,
            json3.has("closed") && json3.get("closed").getAsBoolean(),
            json3.has("now") ? json3.get("now").getAsLong() : System.currentTimeMillis(),
            json3.has("members") && json3.get("members").isJsonArray() ? json3.getAsJsonArray("members") : new JsonArray(),
            asJsonObject3,
            json3.has("messages") && json3.get("messages").isJsonArray() ? json3.getAsJsonArray("messages") : new JsonArray(),
            json3.has("maxSeq") ? json3.get("maxSeq").getAsLong() : retainedMessagesValue
         );
         return Result.success(snapshot);
      }
   }

   private static String formatErrorJson(JsonRetained.Http http) {
      return http.json != null && http.json.has("error") ? http.json.get("error").getAsString() : "ERROR";
   }

   private boolean isApplicationJson() {
      return LicenseRuntime.apiBase != null && !LicenseRuntime.apiBase.isBlank();
   }

   private static String getEncode(String encodeText) {
      return URLEncoder.encode(encodeText == null ? "" : encodeText, StandardCharsets.UTF_8);
   }

   private JsonRetained.Http getApplicationJson(String requestMethod, String applicationJsonText, JsonObject json) {
      if (!this.isApplicationJson()) {
         return null;
      } else {
         HttpURLConnection httpURLConnection = null;

         Object obj;
         try {
            httpURLConnection = (HttpURLConnection)URI.create(LicenseRuntime.apiBase + applicationJsonText).toURL().openConnection();
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setConnectTimeout(6000);
            httpURLConnection.setReadTimeout(8000);
            String ililllililil = LicenseRuntime.getAuthToken();
            if (ililllililil != null) {
               httpURLConnection.setRequestProperty("Authorization", "Bearer " + ililllililil);
            }

            if (json != null) {
               httpURLConnection.setRequestProperty("Content-Type", "application/json");
               httpURLConnection.setDoOutput(true);

               try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                  outputStream.write(json.toString().getBytes(StandardCharsets.UTF_8));
               }
            }

            int responseCode = httpURLConnection.getResponseCode();
            String formatUtf2 = formatUtf(responseCode < 400 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream());

            JsonObject jsonObject;
            try {
               jsonObject = JsonParser.parseString(formatUtf2).getAsJsonObject();
            } catch (Exception e) {
               jsonObject = new JsonObject();
            }

            return new JsonRetained.Http(responseCode, jsonObject);
         } catch (Exception e2) {
            obj = null;
         } finally {
            if (httpURLConnection != null) {
               httpURLConnection.disconnect();
            }
         }

         return (JsonRetained.Http)obj;
      }
   }

   private record Http(int status, JsonObject json) {
   }
}
