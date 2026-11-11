package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;
import com.corz.client.utils.CardStatusUtilsFlag;

public final class RadioStationRegistry {
   private static int reloadCount;
   private static List<RadioStationRegistry.Station> loadedStations = List.of();
   public static final String RADIO_JSON = "radio.json";
   private static RadioStationRegistry.Source stationSource = RadioStationRegistry.Source.NONE;
   private static boolean stationsLoaded;
   private static final String BUNDLED_STATIONS_RESOURCE = "/assets/corzclient/radio/stations.json";
   private static boolean overrideFileInvalid;
   private static final String CONFIG_DIR_NAME = "corzclient";

   public static synchronized RadioStationRegistry.Source getStationSource() {
      return stationSource;
   }

   private static String readSanitizedString(JsonObject json, String primitiveJsonText, int end) {
      JsonElement jsonElement = json.get(primitiveJsonText);
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         String asString = jsonElement.getAsString();
         if (asString == null) {
            return "";
         } else {
            StringBuilder stringBuilder = new StringBuilder(Math.min(asString.length(), end));
            boolean primitiveJsonFlag = false;

            for (int i = 0; i < asString.length(); i++) {
               char charAt = asString.charAt(i);
               if (charAt >= ' ' && charAt != 127 && !Character.isWhitespace(charAt)) {
                  if (primitiveJsonFlag) {
                     stringBuilder.append(' ');
                  }

                  primitiveJsonFlag = false;
                  stringBuilder.append(charAt);
               } else {
                  primitiveJsonFlag = stringBuilder.length() > 0;
               }
            }

            String orDefault = CardStatusUtilsFlag.getOrDefault(stringBuilder.toString());
            if (orDefault == null) {
               return "";
            } else {
               String trim = orDefault.trim();
               return trim.length() > end ? trim.substring(0, end).trim() : trim;
            }
         }
      } else {
         return "";
      }
   }

   private static int readTrackCount(JsonObject json, String primitiveJsonText) {
      JsonElement name2 = json.get(primitiveJsonText);
      if (name2 != null && name2.isJsonPrimitive()) {
         try {
            JsonPrimitive asJsonPrimitive = name2.getAsJsonPrimitive();
            double primitiveJsonValue = asJsonPrimitive.isNumber() ? asJsonPrimitive.getAsDouble() : Double.parseDouble(asJsonPrimitive.getAsString().trim());
            return !Double.isNaN(primitiveJsonValue) && !(primitiveJsonValue < 1.0) ? (int)Math.min(99999.0, Math.floor(primitiveJsonValue)) : 0;
         } catch (Throwable e) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static String readBundledStationsJson() {
      try {
         String formatStationsRadio;
         try (InputStream resourceAsStream = RadioStationRegistry.class.getResourceAsStream("/assets/corzclient/radio/stations.json")) {
            formatStationsRadio = resourceAsStream == null ? null : new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
         }

         return formatStationsRadio;
      } catch (Throwable e) {
         return null;
      }
   }

   private static Path findOverrideFile() {
      try {
         FabricLoader instance = FabricLoader.getInstance();
         Path resolve = instance.getGameDir().resolve("corzclient").resolve("radio.json");
         if (Files.isRegularFile(resolve)) {
            return resolve;
         }

         Path resolve2 = instance.getConfigDir().resolve("corzclient").resolve("radio.json");
         if (Files.isRegularFile(resolve2)) {
            return resolve2;
         }
      } catch (Throwable e) {
      }

      return null;
   }

   public static synchronized int getReloadCount() {
      if (!stationsLoaded) {
         reloadStations();
      }

      return reloadCount;
   }

   static String sanitizeStationUrl(String asciistringText) {
      if (asciistringText != null && !asciistringText.isEmpty()) {
         try {
            URI uri = new URI(asciistringText);
            String scheme = uri.getScheme();
            if (scheme == null) {
               return "";
            } else {
               String lowerCase = scheme.toLowerCase(Locale.ROOT);
               if (lowerCase.equals("http") || lowerCase.equals("https")) {
                  String host = uri.getHost();
                  return host != null && !host.isEmpty() ? uri.toASCIIString() : "";
               } else if (!lowerCase.equals("spotify")) {
                  return "";
               } else {
                  String schemeSpecificPart = uri.getSchemeSpecificPart();
                  return schemeSpecificPart != null && schemeSpecificPart.matches("[A-Za-z0-9:]{1,128}") ? "spotify:" + schemeSpecificPart : "";
               }
            }
         } catch (Throwable e) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static synchronized void reloadStations() {
      stationsLoaded = true;
      reloadCount++;
      overrideFileInvalid = false;
      Path resolve = findOverrideFile();
      if (resolve != null) {
         List parseStations2 = parseStations(readFileIfSmall(resolve));
         if (parseStations2 != null) {
            loadedStations = parseStations2;
            stationSource = RadioStationRegistry.Source.OVERRIDE;
            return;
         }

         overrideFileInvalid = true;
      }

      List copyOf = parseStations(readBundledStationsJson());
      loadedStations = copyOf == null ? List.of() : copyOf;
      stationSource = copyOf == null ? RadioStationRegistry.Source.NONE : RadioStationRegistry.Source.BUNDLED;
   }

   static List<RadioStationRegistry.Station> parseStations(String jsonRadioText) {
      if (jsonRadioText == null) {
         return null;
      } else {
         try {
            if (!jsonRadioText.isEmpty() && jsonRadioText.charAt(0) == '\ufeff') {
               jsonRadioText = jsonRadioText.substring(1);
            }

            JsonElement jsonElement = JsonParser.parseString(jsonRadioText);
            JsonArray jsonArray = null;
            if (jsonElement.isJsonArray()) {
               jsonArray = jsonElement.getAsJsonArray();
            } else if (jsonElement.isJsonObject()) {
               JsonElement asjsonobject = jsonElement.getAsJsonObject().get("stations");
               if (asjsonobject != null && asjsonobject.isJsonArray()) {
                  jsonArray = asjsonobject.getAsJsonArray();
               }
            }

            if (jsonArray == null) {
               return null;
            } else {
               ArrayList arrayList = new ArrayList();

               for (JsonElement jsonItem : jsonArray) {
                  if (arrayList.size() >= 24) {
                     break;
                  }

                  RadioStationRegistry.Station tagTracks = parseStation(jsonItem);
                  if (tagTracks != null) {
                     arrayList.add(tagTracks);
                  }
               }

               return arrayList.isEmpty() && !jsonArray.isEmpty() ? null : List.copyOf(arrayList);
            }
         } catch (Throwable e) {
            return null;
         }
      }
   }

   public static synchronized List<RadioStationRegistry.Station> getStations() {
      if (!stationsLoaded) {
         reloadStations();
      }

      return loadedStations;
   }

   public static synchronized boolean isOverrideFileInvalid() {
      return overrideFileInvalid;
   }

   public static boolean openStationUrl(RadioStationRegistry.Station station) {
      if (station != null && station.hasUrl()) {
         try {
            URI uri = new URI(station.url());
            Util.getOperatingSystem().open(uri);
            return true;
         } catch (Throwable e) {
            return false;
         }
      } else {
         return false;
      }
   }

   private RadioStationRegistry() {
   }

   private static String readTrimmedStringWithinLimit(JsonObject json, String primitiveJsonText, int primitiveJsonValue) {
      JsonElement jsonElement = json.get(primitiveJsonText);
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         String asString = jsonElement.getAsString();
         if (asString == null) {
            return "";
         } else {
            String trim = asString.trim();
            return trim.length() > primitiveJsonValue ? "" : trim;
         }
      } else {
         return "";
      }
   }

   private static RadioStationRegistry.Station parseStation(JsonElement json) {
      if (json != null && json.isJsonObject()) {
         JsonObject asJsonObject = json.getAsJsonObject();
         String sanitizedString = readSanitizedString(asJsonObject, "staff", 24);
         if (sanitizedString.isEmpty()) {
            return null;
         } else {
            String readSanitizedString4 = readSanitizedString(asJsonObject, "mc", 16);
            if (!readSanitizedString4.matches("[A-Za-z0-9_]{1,16}")) {
               readSanitizedString4 = "";
            }

            String trimmedStringWithinLimit = readTrimmedStringWithinLimit(asJsonObject, "url", 512);
            String asciistring = sanitizeStationUrl(trimmedStringWithinLimit);
            String tagTracksText = sanitizedString.toLowerCase(Locale.ROOT) + "|" + asciistring;
            return new RadioStationRegistry.Station(
               sanitizedString,
               readSanitizedString4,
               readSanitizedString(asJsonObject, "role", 16),
               readSanitizedString(asJsonObject, "title", 64),
               asciistring,
               readTrackCount(asJsonObject, "tracks"),
               readSanitizedString(asJsonObject, "tag", 16),
               tagTracksText,
               asciistring.isEmpty() && !trimmedStringWithinLimit.isEmpty()
            );
         }
      } else {
         return null;
      }
   }

   private static String readFileIfSmall(Path path) {
      try {
         return Files.size(path) > 262144L ? null : Files.readString(path, StandardCharsets.UTF_8);
      } catch (Throwable e) {
         return null;
      }
   }

   public static enum Source {
      NONE,
      BUNDLED,
      OVERRIDE;
   }

   public record Station(String staff, String mc, String role, String title, String url, int tracks, String tag, String key, boolean badUrl) {
      public boolean hasUrl() {
         return !this.url.isEmpty();
      }
   }
}
