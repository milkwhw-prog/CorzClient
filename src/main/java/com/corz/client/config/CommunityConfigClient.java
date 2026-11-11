package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import ai.auth.LicenseRuntime;

public final class CommunityConfigClient {
   private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(6000L)).build();

   private static CommunityConfigClient.ShareCode parseShareCode(JsonObject json) {
      Integer integer = json.has("maxUses") && json.get("maxUses").isJsonPrimitive() ? json.get("maxUses").getAsInt() : null;
      int usesRevokedValue = json.has("useCount") ? json.get("useCount").getAsInt() : 0;
      boolean usesRevokedFlag = json.has("revoked") && json.get("revoked").getAsBoolean();
      return new CommunityConfigClient.ShareCode(json.get("code").getAsString(), integer, usesRevokedValue, usesRevokedFlag);
   }

   public static void fetchShareCodes(String configJsonText, Consumer<CommunityConfigClient.Result<List<CommunityConfigClient.ShareCode>>> consumer) {
      runOnWorkerThread(() -> {
         if (LicenseRuntime.getAuthToken() == null) {
            return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in first.");
         } else {
            CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", "/configs/" + urlEncode(configJsonText) + "/share-codes", null, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else if (applicationJson.status >= 400) {
               return CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson));
            } else {
               var arrayList = new ArrayList();
               if (applicationJson.json.has("shareCodes") && applicationJson.json.get("shareCodes").isJsonArray()) {
                  for (JsonElement jsonElement : applicationJson.json.getAsJsonArray("shareCodes")) {
                     if (jsonElement.isJsonObject()) {
                        arrayList.add(parseShareCode(jsonElement.getAsJsonObject()));
                     }
                  }
               }

               return CommunityConfigClient.Result.okResult(arrayList);
            }
         }
      }, consumer);
   }

   private static boolean isServerConfigured() {
      return LicenseRuntime.apiBase != null && !LicenseRuntime.apiBase.isBlank();
   }

   public static void reportConfig(String configJsonText, String configJsonText2, String configJsonText3, Consumer<CommunityConfigClient.Result<Boolean>> consumer) {
      runOnWorkerThread(
         () -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("reason", configJsonText2);
            if (configJsonText3 != null) {
               jsonObject.addProperty("details", configJsonText3);
            }

            CommunityConfigClient.HttpResponse applicationJson = sendRequest("POST", "/configs/" + urlEncode(configJsonText) + "/report", jsonObject, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else {
               return applicationJson.status >= 400
                  ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                  : CommunityConfigClient.Result.okResult(Boolean.TRUE);
            }
         },
         consumer
      );
   }

   private static String getErrorMessage(CommunityConfigClient.HttpResponse httpResponse) {
      if (httpResponse.json.has("message")) {
         return httpResponse.json.get("message").getAsString();
      } else {
         return httpResponse.json.has("errors") && httpResponse.json.get("errors").isJsonArray() && httpResponse.json.getAsJsonArray("errors").size() > 0
            ? httpResponse.json.getAsJsonArray("errors").get(0).getAsString()
            : "Request failed (" + httpResponse.status + ").";
      }
   }

   private static String getErrorCode(CommunityConfigClient.HttpResponse httpResponse) {
      return httpResponse.json.has("error") ? httpResponse.json.get("error").getAsString() : "ERROR";
   }

   public static void deleteConfig(String configJsonText, Consumer<CommunityConfigClient.Result<Boolean>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to delete configs.");
            } else {
               CommunityConfigClient.HttpResponse applicationJson = sendRequest("DELETE", "/configs/" + urlEncode(configJsonText), null, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else if (applicationJson.status == 401) {
                  return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to delete configs.");
               } else if (applicationJson.status == 403) {
                  return CommunityConfigClient.Result.errorResult("FORBIDDEN", "You don't own this config.");
               } else if (applicationJson.status == 404) {
                  return CommunityConfigClient.Result.errorResult("NOT_FOUND", "That config no longer exists.");
               } else {
                  return applicationJson.status >= 400
                     ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                     : CommunityConfigClient.Result.okResult(Boolean.TRUE);
               }
            }
         },
         consumer
      );
   }

   public static void setConfigVisibility(String configJsonText, String configJsonText2, Consumer<CommunityConfigClient.Result<SharedConfigEntry>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in first.");
            } else {
               JsonObject jsonObject = new JsonObject();
               jsonObject.addProperty("visibility", configJsonText2);
               CommunityConfigClient.HttpResponse applicationJson = sendRequest("PATCH", "/configs/" + urlEncode(configJsonText), jsonObject, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else if (applicationJson.status == 403) {
                  return CommunityConfigClient.Result.errorResult("FORBIDDEN", "You don't own this config.");
               } else {
                  return applicationJson.status >= 400
                     ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                     : CommunityConfigClient.Result.okResult(SharedConfigEntry.fromJson(applicationJson.json.getAsJsonObject("config")));
               }
            }
         },
         consumer
      );
   }

   public static void searchConfigs(Map<String, String> map, Consumer<CommunityConfigClient.Result<ConfigsPage>> consumer) {
      runOnWorkerThread(
         () -> {
            CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", "/configs" + buildQueryString(map), null, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else {
               return applicationJson.status >= 400
                  ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                  : CommunityConfigClient.Result.okResult(ConfigsPage.fromJson(applicationJson.json));
            }
         },
         consumer
      );
   }

   private static CommunityConfigClient.HttpResponse sendHttpClientRequest(String applicationJsonText, String applicationJsonText2, JsonObject json, boolean applicationJsonFlag) {
      try {
         Builder header = HttpRequest.newBuilder(URI.create(LicenseRuntime.apiBase + applicationJsonText2))
            .timeout(Duration.ofMillis(8000L))
            .header("Accept", "application/json");
         if (applicationJsonFlag) {
            String ililllililil = LicenseRuntime.getAuthToken();
            if (ililllililil != null) {
               header.header("Authorization", "Bearer " + ililllililil);
            }
         }

         if (json != null) {
            header.header("Content-Type", "application/json");
            header.method(applicationJsonText, BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8));
         } else {
            header.method(applicationJsonText, BodyPublishers.noBody());
         }

         var send = HTTP_CLIENT.send(header.build(), BodyHandlers.ofString(StandardCharsets.UTF_8));

         JsonObject jsonObject;
         try {
            jsonObject = JsonParser.parseString((String)send.body()).getAsJsonObject();
         } catch (Exception e) {
            jsonObject = new JsonObject();
         }

         return new CommunityConfigClient.HttpResponse(send.statusCode(), jsonObject);
      } catch (Exception e2) {
         return null;
      }
   }

   public static void fetchConfigVersions(String configJsonText, Consumer<CommunityConfigClient.Result<List<JsonObject>>> consumer) {
      runOnWorkerThread(() -> {
         CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", "/configs/" + urlEncode(configJsonText) + "/versions", null, true);
         if (applicationJson == null) {
            return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
         } else if (applicationJson.status >= 400) {
            return CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson));
         } else {
            var arrayList = new ArrayList();
            JsonArray asJsonArray = applicationJson.json.getAsJsonArray("versions");
            if (asJsonArray != null) {
               for (JsonElement jsonElement : asJsonArray) {
                  if (jsonElement.isJsonObject()) {
                     arrayList.add(jsonElement.getAsJsonObject());
                  }
               }
            }

            return CommunityConfigClient.Result.okResult(arrayList);
         }
      }, consumer);
   }

   private static String readStreamAsUtfForInputStream(InputStream inputStream) {
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

   public static void setConfigLiked(String configJsonText, boolean configJsonFlag, Consumer<CommunityConfigClient.Result<Long>> consumer) {
      runOnWorkerThread(() -> {
         if (LicenseRuntime.getAuthToken() == null) {
            return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to like configs.");
         } else {
            CommunityConfigClient.HttpResponse applicationJson = sendRequest(configJsonFlag ? "POST" : "DELETE", "/configs/" + urlEncode(configJsonText) + "/like", null, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else if (applicationJson.status == 401) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to like configs.");
            } else if (applicationJson.status >= 400) {
               return CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson));
            } else {
               long configJsonValue = applicationJson.json.has("likeCount") ? applicationJson.json.get("likeCount").getAsLong() : 0L;
               return CommunityConfigClient.Result.okResult(configJsonValue);
            }
         }
      }, consumer);
   }

   public static void createShareCode(String configJsonText, Integer integer, Consumer<CommunityConfigClient.Result<CommunityConfigClient.ShareCode>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to share configs.");
            } else {
               JsonObject jsonObject = new JsonObject();
               if (integer != null) {
                  jsonObject.addProperty("maxUses", integer);
               }

               CommunityConfigClient.HttpResponse applicationJson = sendRequest("POST", "/configs/" + urlEncode(configJsonText) + "/share-codes", jsonObject, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else if (applicationJson.status == 403) {
                  return CommunityConfigClient.Result.errorResult("FORBIDDEN", "You don't own this config.");
               } else {
                  return applicationJson.status >= 400
                     ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                     : CommunityConfigClient.Result.okResult(parseShareCode(applicationJson.json.getAsJsonObject("shareCode")));
               }
            }
         },
         consumer
      );
   }

   public static void uploadConfigVersion(String configJsonText, ConfigPresetFile jsonVersion, String configJsonText2, Consumer<CommunityConfigClient.Result<SharedConfigEntry>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in first.");
            } else {
               ConfigPresetFile applyUploadSafetyRules2 = applyUploadSafetyRules(jsonVersion);
               if (applyUploadSafetyRules2 == null) {
                  return CommunityConfigClient.Result.errorResult("POLICY", "Config safety rules are missing, so nothing was uploaded.");
               } else {
                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("config", new String(applyUploadSafetyRules2.toJsonBytes(), StandardCharsets.UTF_8));
                  if (configJsonText2 != null) {
                     jsonObject.addProperty("changelog", configJsonText2);
                  }

                  CommunityConfigClient.HttpResponse applicationJson = sendRequest("POST", "/configs/" + urlEncode(configJsonText) + "/version", jsonObject, true);
                  if (applicationJson == null) {
                     return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
                  } else if (applicationJson.status == 403) {
                     return CommunityConfigClient.Result.errorResult("FORBIDDEN", "You don't own this config.");
                  } else {
                     return applicationJson.status >= 400
                        ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                        : CommunityConfigClient.Result.okResult(SharedConfigEntry.fromJson(applicationJson.json.getAsJsonObject("config")));
                  }
               }
            }
         },
         consumer
      );
   }

   public static void downloadConfig(String configJsonText, String finish, Consumer<CommunityConfigClient.Result<CommunityConfigClient.Download>> consumer) {
      runOnWorkerThread(
         () -> {
            String configJsonText3 = "/configs/" + urlEncode(configJsonText) + "/download";
            var linkedHashMap = new LinkedHashMap();
            if (finish != null) {
               linkedHashMap.put("versionId", finish);
            }

            linkedHashMap.put("clientVersion", LicenseRuntime.getClientVersion());
            CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", configJsonText3 + buildQueryString(linkedHashMap), null, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else if (applicationJson.status == 404) {
               return CommunityConfigClient.Result.errorResult("NOT_FOUND", "That config no longer exists.");
            } else if (applicationJson.status >= 400) {
               return CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson));
            } else {
               JsonObject asJsonObject = applicationJson.json.getAsJsonObject("meta");
               String configJsonText4 = applicationJson.json.has("config") && applicationJson.json.get("config").isJsonPrimitive() ? applicationJson.json.get("config").getAsString() : null;
               if (configJsonText4 != null && asJsonObject != null) {
                  byte[] bytes = configJsonText4.getBytes(StandardCharsets.UTF_8);
                  String fastSmoothText = asJsonObject.has("sha256") ? asJsonObject.get("sha256").getAsString() : "";
                  String formatSha = ConfigPresetFile.sha256Hex(bytes);
                  return !formatSha.equalsIgnoreCase(fastSmoothText)
                     ? CommunityConfigClient.Result.errorResult("SHA_MISMATCH", "Downloaded config failed its integrity check.")
                     : CommunityConfigClient.Result.okResult(new CommunityConfigClient.Download(bytes, asJsonObject));
               } else {
                  return CommunityConfigClient.Result.errorResult("BAD_RESPONSE", "Malformed download.");
               }
            }
         },
         consumer
      );
   }

   private CommunityConfigClient() {
   }

   private static CommunityConfigClient.HttpResponse sendRequest(String requestMethod, String applicationJsonText, JsonObject json, boolean applicationJsonFlag) {
      if (!isServerConfigured()) {
         return null;
      } else if ("PATCH".equals(requestMethod)) {
         return sendHttpClientRequest(requestMethod, applicationJsonText, json, applicationJsonFlag);
      } else {
         HttpURLConnection httpURLConnection = null;

         Object obj;
         try {
            httpURLConnection = (HttpURLConnection)URI.create(LicenseRuntime.apiBase + applicationJsonText).toURL().openConnection();
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setConnectTimeout(6000);
            httpURLConnection.setReadTimeout(8000);
            if (applicationJsonFlag) {
               String ililllililil = LicenseRuntime.getAuthToken();
               if (ililllililil != null) {
                  httpURLConnection.setRequestProperty("Authorization", "Bearer " + ililllililil);
               }
            }

            if (json != null) {
               httpURLConnection.setRequestProperty("Content-Type", "application/json");
               httpURLConnection.setDoOutput(true);

               try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                  outputStream.write(json.toString().getBytes(StandardCharsets.UTF_8));
               }
            }

            int responseCode = httpURLConnection.getResponseCode();
            String streamAsUtfForInputStream = readStreamAsUtfForInputStream(responseCode < 400 ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream());

            JsonObject jsonObject;
            try {
               jsonObject = JsonParser.parseString(streamAsUtfForInputStream).getAsJsonObject();
            } catch (Exception e) {
               jsonObject = new JsonObject();
            }

            return new CommunityConfigClient.HttpResponse(responseCode, jsonObject);
         } catch (Exception e2) {
            obj = null;
         } finally {
            if (httpURLConnection != null) {
               httpURLConnection.disconnect();
            }
         }

         return (CommunityConfigClient.HttpResponse)obj;
      }
   }

   public static void fetchConfig(String configJsonText, Consumer<CommunityConfigClient.Result<SharedConfigEntry>> consumer) {
      runOnWorkerThread(
         () -> {
            CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", "/configs/" + urlEncode(configJsonText), null, true);
            if (applicationJson == null) {
               return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
            } else if (applicationJson.status == 404) {
               return CommunityConfigClient.Result.errorResult("NOT_FOUND", "That config no longer exists.");
            } else {
               return applicationJson.status >= 400
                  ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                  : CommunityConfigClient.Result.okResult(SharedConfigEntry.fromJson(applicationJson.json.getAsJsonObject("config")));
            }
         },
         consumer
      );
   }

   public static void revokeShareCode(String configJsonText, String encodeText, Consumer<CommunityConfigClient.Result<Boolean>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in first.");
            } else {
               CommunityConfigClient.HttpResponse applicationJson = sendRequest("DELETE", "/configs/" + urlEncode(configJsonText) + "/share-codes/" + urlEncode(encodeText), null, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else {
                  return applicationJson.status >= 400
                     ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                     : CommunityConfigClient.Result.okResult(applicationJson.json.has("ok") && applicationJson.json.get("ok").getAsBoolean());
               }
            }
         },
         consumer
      );
   }

   public static void publishConfig(ConfigPresetFile jsonVersion, List<String> list, String configJsonText, String configJsonText2, Consumer<CommunityConfigClient.Result<SharedConfigEntry>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "A valid license is required to publish.");
            } else {
               ConfigPresetFile applyUploadSafetyRules2 = applyUploadSafetyRules(jsonVersion);
               if (applyUploadSafetyRules2 == null) {
                  return CommunityConfigClient.Result.errorResult("POLICY", "Config safety rules are missing, so nothing was uploaded.");
               } else {
                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("config", new String(applyUploadSafetyRules2.toJsonBytes(), StandardCharsets.UTF_8));
                  jsonObject.addProperty("authorDisplayName", getUsername());
                  if (configJsonText != null) {
                     jsonObject.addProperty("visibility", configJsonText);
                  }

                  if (configJsonText2 != null) {
                     jsonObject.addProperty("changelog", configJsonText2);
                  }

                  if (list != null) {
                     JsonArray jsonArray = new JsonArray();

                     for (String configJsonText3 : list) {
                        jsonArray.add(configJsonText3);
                     }

                     jsonObject.add("tags", jsonArray);
                  }

                  CommunityConfigClient.HttpResponse applicationJson = sendRequest("POST", "/configs", jsonObject, true);
                  if (applicationJson == null) {
                     return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
                  } else if (applicationJson.status == 401) {
                     return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "A valid license is required to publish.");
                  } else if (applicationJson.status == 409) {
                     return CommunityConfigClient.Result.errorResult("DUPLICATE", "You've already published an identical config.");
                  } else {
                     return applicationJson.status >= 400
                        ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                        : CommunityConfigClient.Result.okResult(SharedConfigEntry.fromJson(applicationJson.json.getAsJsonObject("config")));
                  }
               }
            }
         },
         consumer
      );
   }

   private static String getUsername() {
      try {
         String username = MinecraftClient.getInstance().getSession().getUsername();
         return username != null && !username.isBlank() ? username : "Anonymous";
      } catch (Throwable e) {
         return "Anonymous";
      }
   }

   private static <T> void runOnWorkerThread(Callable<CommunityConfigClient.Result<T>> callable, Consumer<CommunityConfigClient.Result<T>> consumer) {
      Thread thread = new Thread(() -> {
         CommunityConfigClient.Result var2x;
         try {
            var2x = (CommunityConfigClient.Result)callable.call();
         } catch (Exception e) {
            var2x = CommunityConfigClient.Result.errorResult("ERROR", "Unexpected error.");
         }

         try {
            consumer.accept(var2x);
         } catch (Exception e2) {
         }
      }, "corz-community");
      thread.setDaemon(true);
      thread.start();
   }

   private static String buildQueryString(Map<String, String> map) {
      if (map != null && !map.isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder("?");
         boolean configJsonFlag = true;

         for (Entry entry : map.entrySet()) {
            if (entry.getValue() != null && !((String)entry.getValue()).isEmpty()) {
               if (!configJsonFlag) {
                  stringBuilder.append('&');
               }

               stringBuilder.append(urlEncode((String)entry.getKey())).append('=').append(urlEncode((String)entry.getValue()));
               configJsonFlag = false;
            }
         }

         return stringBuilder.length() == 1 ? "" : stringBuilder.toString();
      } else {
         return "";
      }
   }

   public static ConfigPresetFile applyUploadSafetyRules(ConfigPresetFile jsonVersion) {
      return jsonVersion != null && ConfigSharePolicy.getInstance().isPolicyLoaded() ? ModuleConfigSerializer.getJsonVersion(jsonVersion) : null;
   }

   private static String urlEncode(String encodeText) {
      return URLEncoder.encode(encodeText == null ? "" : encodeText, StandardCharsets.UTF_8);
   }

   public static void fetchMyConfigs(Consumer<CommunityConfigClient.Result<ConfigsPage>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to view your configs.");
            } else {
               CommunityConfigClient.HttpResponse applicationJson = sendRequest("GET", "/configs/mine", null, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else if (applicationJson.status == 401) {
                  return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to view your configs.");
               } else {
                  return applicationJson.status >= 400
                     ? CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson))
                     : CommunityConfigClient.Result.okResult(ConfigsPage.fromJson(applicationJson.json));
               }
            }
         },
         consumer
      );
   }

   public static void redeemShareCode(String configJsonText, Consumer<CommunityConfigClient.Result<CommunityConfigClient.Download>> consumer) {
      runOnWorkerThread(
         () -> {
            if (LicenseRuntime.getAuthToken() == null) {
               return CommunityConfigClient.Result.errorResult("UNAUTHORIZED", "Sign in to redeem a code.");
            } else {
               JsonObject jsonObject = new JsonObject();
               jsonObject.addProperty("code", configJsonText);
               CommunityConfigClient.HttpResponse applicationJson = sendRequest("POST", "/configs/redeem", jsonObject, true);
               if (applicationJson == null) {
                  return CommunityConfigClient.Result.errorResult("OFFLINE", "Couldn't reach the config server.");
               } else if (applicationJson.status >= 400) {
                  return CommunityConfigClient.Result.errorResult(getErrorCode(applicationJson), getErrorMessage(applicationJson));
               } else {
                  JsonObject asJsonObject = applicationJson.json.getAsJsonObject("meta");
                  String configJsonText2 = applicationJson.json.has("config") && applicationJson.json.get("config").isJsonPrimitive() ? applicationJson.json.get("config").getAsString() : null;
                  if (configJsonText2 != null && asJsonObject != null) {
                     byte[] bytes = configJsonText2.getBytes(StandardCharsets.UTF_8);
                     String fastSmoothText = asJsonObject.has("sha256") ? asJsonObject.get("sha256").getAsString() : "";
                     String formatSha = ConfigPresetFile.sha256Hex(bytes);
                     return !formatSha.equalsIgnoreCase(fastSmoothText)
                        ? CommunityConfigClient.Result.errorResult("SHA_MISMATCH", "Shared config failed its integrity check.")
                        : CommunityConfigClient.Result.okResult(new CommunityConfigClient.Download(bytes, asJsonObject));
                  } else {
                     return CommunityConfigClient.Result.errorResult("BAD_RESPONSE", "Malformed redeem.");
                  }
               }
            }
         },
         consumer
      );
   }

   public static final class Download {
      public final byte[] bytes;
      public final JsonObject meta;

      public Download(byte[] bytes2, JsonObject jsonObject) {
         this.bytes = bytes2;
         this.meta = jsonObject;
      }
   }

   private record HttpResponse(int status, JsonObject json) {
   }

   public static final class Result<T> {
      public final String errorMessage;
      public final T value;
      public final boolean ok;
      public final String errorCode;

      static <T> CommunityConfigClient.Result<T> okResult(T t) {
         return new CommunityConfigClient.Result<>(true, (T)t, null, null);
      }

      private Result(boolean publishShareFlag, T t, String publishDuplicateText, String couldnShareText) {
         this.ok = publishShareFlag;
         this.value = (T)t;
         this.errorCode = publishDuplicateText;
         this.errorMessage = couldnShareText;
      }

      static <T> CommunityConfigClient.Result<T> errorResult(String configJsonText, String configJsonText2) {
         return new CommunityConfigClient.Result<>(false, null, configJsonText, configJsonText2);
      }
   }

   public static final class ShareCode {
      public final int usesCount;
      public final boolean revoked;
      public final Integer maxUses;
      public final String code;

      public String getUsageLabel() {
         return this.usesCount + "/" + (this.maxUses == null ? "*" : String.valueOf(this.maxUses));
      }

      public ShareCode(String string, Integer number, int value, boolean revokedCopiedFlag) {
         this.code = string;
         this.maxUses = number;
         this.usesCount = value;
         this.revoked = revokedCopiedFlag;
      }
   }
}
