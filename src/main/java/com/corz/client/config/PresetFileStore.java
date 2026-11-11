package com.corz.client.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class PresetFileStore {
   private static Path path2 = null;
   private static final String PRESET_SEAL = "preset-seal.key";
   private static final String CORZ_OWN_PRESET = "corz-own-preset-v1";
   private static volatile Path path = null;
   private static volatile Path invoke;
   private static final String SOURCES_JSON = "sources.json";
   private static volatile byte[] values;
   private static volatile Path path3;
   private static volatile Path resolve;
   private static final String OWN_SEAL = "ownSeal";
   public static final String CORZ_JSON = ".corz.json";
   private static final String ACTIVE_TXT = "active.txt";
   private static final String BACKUP_CORZ_JSON = ".backup.corz.json";

   public static String duplicatePreset(String configCreatedText)  throws java.io.IOException {
      ConfigPresetFile jsonVersion = readPresetByName(configCreatedText);
      if (jsonVersion == null) {
         return null;
      } else {
         ConfigPresetFile parseJson = jsonVersion.deepCopy();
         parseJson.name = (jsonVersion.name == null ? "Config" : jsonVersion.name) + " (copy)";
         parseJson.createdAt = getUtcTimestamp();
         return saveAsUnsealedPreset(parseJson);
      }
   }

   private static void clearOwnSeal(String sealOwnText)  throws java.io.IOException {
      JsonObject jsonSources = readSourcesJson();
      if (jsonSources.has(sealOwnText) && jsonSources.get(sealOwnText).isJsonObject() && jsonSources.getAsJsonObject(sealOwnText).has("ownSeal")) {
         jsonSources.getAsJsonObject(sealOwnText).remove("ownSeal");
         writeSourcesJson(jsonSources);
      }
   }

   private static JsonObject readSourcesJson() {
      try {
         Path resolve = getPresetsDirectory().resolve("sources.json");
         if (!Files.exists(resolve)) {
            return new JsonObject();
         } else {
            JsonElement jsonElement = JsonParser.parseString(Files.readString(resolve));
            return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject();
         }
      } catch (Exception e) {
         return new JsonObject();
      }
   }

   private static byte[] readHexKeyFile(Path path) {
      try {
         if (!Files.isRegularFile(path)) {
            return null;
         } else {
            String trim = Files.readString(path, StandardCharsets.US_ASCII).trim();
            return trim.length() == 64 ? HexFormat.of().parseHex(trim) : null;
         }
      } catch (Exception e) {
         return null;
      }
   }

   public static String readActivePresetName() {
      try {
         Path resolve = getPresetsDirectory().resolve("active.txt");
         if (!Files.exists(resolve)) {
            return null;
         } else {
            String trim = Files.readString(resolve).trim();
            return trim.isEmpty() ? null : trim;
         }
      } catch (IOException e) {
         return null;
      }
   }

   public static boolean hasValidOwnSeal(String jsonSealText) {
      try {
         Path resolve = getPresetPath(jsonSealText);
         return Files.isRegularFile(resolve) && verifyOwnSeal(jsonSealText, Files.readAllBytes(resolve));
      } catch (IOException e) {
         return false;
      }
   }

   private PresetFileStore() {
   }

   private static byte[] getOrCreateSealKey() {
      Path path = getSealKeyPath();
      byte[] pathAscii = values;
      if (pathAscii != null && path.equals(path3)) {
         return pathAscii;
      } else {
         synchronized (PresetFileStore.class) {
            byte[] hexKeyFile = readHexKeyFile(path);
            if (hexKeyFile == null) {
               byte[] bytes = new byte[32];
               new SecureRandom().nextBytes(bytes);

               try {
                  writeFileAtomically(path, HexFormat.of().formatHex(bytes).getBytes(StandardCharsets.US_ASCII));
               } catch (Exception e) {
                  return null;
               }

               hexKeyFile = readHexKeyFile(path);
            }

            if (hexKeyFile != null) {
               values = hexKeyFile;
               path3 = path;
            }

            return hexKeyFile;
         }
      }
   }

   public static void saveSourceMeta(String communitySealText, PresetFileStore.SourceMeta sourceMeta)  throws java.io.IOException {
      JsonObject jsonSources = readSourcesJson();
      JsonObject jsonObject = jsonSources.has(communitySealText) && jsonSources.get(communitySealText).isJsonObject() ? jsonSources.getAsJsonObject(communitySealText) : null;
      JsonObject jsonObject2 = new JsonObject();
      jsonObject2.addProperty("source", sourceMeta.source.name());
      if (sourceMeta.communityId != null) {
         jsonObject2.addProperty("communityId", sourceMeta.communityId);
      }

      if (sourceMeta.communityVersionId != null) {
         jsonObject2.addProperty("communityVersionId", sourceMeta.communityVersionId);
      }

      if (sourceMeta.communitySha256 != null) {
         jsonObject2.addProperty("communitySha256", sourceMeta.communitySha256);
      }

      if (sourceMeta.downloadedAt != null) {
         jsonObject2.addProperty("downloadedAt", sourceMeta.downloadedAt);
      }

      if (sourceMeta.source == PresetFileStore.Source.LOCAL && jsonObject != null && jsonObject.has("ownSeal")) {
         jsonObject2.add("ownSeal", jsonObject.get("ownSeal"));
      }

      jsonSources.add(communitySealText, jsonObject2);
      writeSourcesJson(jsonSources);
   }

   private static boolean verifyOwnSeal(String sealSourceText, byte[] sealSourceBytes) {
      JsonObject jsonSources = readSourcesJson();
      if (jsonSources.has(sealSourceText) && jsonSources.get(sealSourceText).isJsonObject()) {
         JsonObject asJsonObject = jsonSources.getAsJsonObject(sealSourceText);
         if (!PresetFileStore.Source.LOCAL.name().equals(readStringOrNull(asJsonObject, "source"))) {
            return false;
         } else {
            String stringOrNull = readStringOrNull(asJsonObject, "ownSeal");
            if (stringOrNull == null) {
               return false;
            } else {
               String ownSeal = computeOwnSeal(sealSourceText, sealSourceBytes);
               return ownSeal != null && MessageDigest.isEqual(ownSeal.getBytes(StandardCharsets.UTF_8), stringOrNull.getBytes(StandardCharsets.UTF_8));
            }
         }
      } else {
         return false;
      }
   }

   public static byte[] readBackupJson()  throws java.io.IOException {
      Path resolve = getPresetsDirectory().resolve(".backup.corz.json");
      return Files.exists(resolve) ? Files.readAllBytes(resolve) : null;
   }

   public static void writeActivePresetName(String txtActiveText)  throws java.io.IOException {
      writeFileAtomically(getPresetsDirectory().resolve("active.txt"), txtActiveText.getBytes(StandardCharsets.UTF_8));
   }

   public static Path getPresetPath(String jsonSealText) {
      return getPresetsDirectory().resolve(jsonSealText + ".corz.json");
   }

   private static void writeOwnSeal(String sealOwnText, byte[] sealOwnBytes, boolean sealOwnFlag)  throws java.io.IOException {
      JsonObject jsonSources = readSourcesJson();
      JsonObject jsonObject = !sealOwnFlag && jsonSources.has(sealOwnText) && jsonSources.get(sealOwnText).isJsonObject() ? jsonSources.getAsJsonObject(sealOwnText) : new JsonObject();
      jsonObject.addProperty("source", PresetFileStore.Source.LOCAL.name());
      String ownSeal = computeOwnSeal(sealOwnText, sealOwnBytes);
      if (ownSeal != null) {
         jsonObject.addProperty("ownSeal", ownSeal);
      } else {
         jsonObject.remove("ownSeal");
      }

      jsonSources.add(sealOwnText, jsonObject);
      writeSourcesJson(jsonSources);
   }

   private static String computeOwnSeal(String presetOwnText, byte[] presetOwnBytes) {
      return computeHmacHex("corz-own-preset-v1", presetOwnText, presetOwnBytes);
   }

   private static void writePresetAndSeal(String jsonSealText, ConfigPresetFile jsonVersion, boolean jsonSealFlag)  throws java.io.IOException {
      touchTimestamps(jsonVersion);
      byte[] bytes = jsonVersion.toJsonBytes();
      clearOwnSeal(jsonSealText);
      writeFileAtomically(getPresetPath(jsonSealText), bytes);
      writeOwnSeal(jsonSealText, bytes, jsonSealFlag);
   }

   static Path getSealKeyDirectory() {
      return getSealKeyPath().toAbsolutePath().getParent();
   }

   public static List<PresetFileStore.Entry> listPresets() {
      ArrayList arrayList = new ArrayList();
      Path resolve = getPresetsDirectory();

      try (Stream stream = Files.list(resolve)) {
         stream.filter(obj -> ((java.nio.file.Path) obj).getFileName().toString().endsWith(".corz.json"))
            .filter(obj2 -> !((java.nio.file.Path) obj2).getFileName().toString().equals(".backup.corz.json"))
            .forEach(out -> {
               try {
                  byte[] var2x = Files.readAllBytes(((java.nio.file.Path) (out)));
                  ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(var2x, null);
                  if (jsonVersion != null) {
                     arrayList.add(new PresetFileStore.Entry(((java.nio.file.Path) (out)), jsonVersion));
                  }
               } catch (Exception e2) {
               }
            });
      } catch (IOException e3) {
      }

      arrayList.sort(Comparator.<PresetFileStore.Entry, String>comparing(entry -> entry.jsonVersion.updatedAt == null ? "" : entry.jsonVersion.updatedAt).reversed());
      return arrayList;
   }

   public static PresetFileStore.Preset loadPreset(String classText)  throws java.io.IOException {
      Path resolve = getPresetPath(classText);
      if (!Files.exists(resolve)) {
         return null;
      } else {
         byte[] allBytes = Files.readAllBytes(resolve);
         ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(allBytes, null);
         return jsonVersion == null ? null : new PresetFileStore.Preset(classText, jsonVersion, verifyOwnSeal(classText, allBytes));
      }
   }

   private static String makeUniquePresetName(String jsonSealText) {
      String sanitizePresetName2 = sanitizePresetName(jsonSealText);
      String formatText = sanitizePresetName2;
      int jsonSealCounter = 2;

      while (Files.exists(getPresetPath(formatText))) {
         formatText = sanitizePresetName2 + "-" + jsonSealCounter++;
      }

      return formatText;
   }

   private static void writeSourcesJson(JsonObject json)  throws java.io.IOException {
      writeFileAtomically(getPresetsDirectory().resolve("sources.json"), new GsonBuilder().setPrettyPrinting().create().toJson(json).getBytes(StandardCharsets.UTF_8));
   }

   public static boolean presetExists(String existsText) {
      return Files.exists(getPresetPath(existsText));
   }

   private static Path getSealKeyPath() {
      Path path3 = path;
      if (path3 != null) {
         return path3;
      } else if (path2 != null) {
         return path2.resolve("preset-seal.key");
      } else {
         Path homeConfig = getUserConfigRoot();
         return (homeConfig != null ? homeConfig : getFabricConfigDir()).resolve("corzclient").resolve("preset-seal.key");
      }
   }

   public static PresetFileStore.SourceMeta readSourceMeta(String communityDownloadedText) {
      PresetFileStore.SourceMeta sourceMeta = new PresetFileStore.SourceMeta();
      JsonObject jsonSources = readSourcesJson();
      if (jsonSources.has(communityDownloadedText) && jsonSources.get(communityDownloadedText).isJsonObject()) {
         JsonObject asJsonObject = jsonSources.getAsJsonObject(communityDownloadedText);

         try {
            sourceMeta.source = PresetFileStore.Source.valueOf(asJsonObject.get("source").getAsString());
         } catch (Exception e) {
         }

         sourceMeta.communityId = readStringOrNull(asJsonObject, "communityId");
         sourceMeta.communityVersionId = readStringOrNull(asJsonObject, "communityVersionId");
         sourceMeta.communitySha256 = readStringOrNull(asJsonObject, "communitySha256");
         sourceMeta.downloadedAt = readStringOrNull(asJsonObject, "downloadedAt");
      }

      return sourceMeta;
   }

   static String saveAsSealedLocalPreset(ConfigPresetFile jsonVersion)  throws java.io.IOException {
      String jsonSealText = makeUniquePresetName(jsonVersion.name);
      writePresetAndSeal(jsonSealText, jsonVersion, true);
      return jsonSealText;
   }

   public static String savePresetForCommunityId(ConfigPresetFile jsonVersion, String jsonSealText)  throws java.io.IOException {
      String presetNameByCommunityId = findPresetNameByCommunityId(jsonSealText);
      return writePresetFile(presetNameByCommunityId != null && !hasValidOwnSeal(presetNameByCommunityId) ? presetNameByCommunityId : makeUniquePresetName(jsonVersion.name), jsonVersion);
   }

   public static String findPresetNameByCommunityId(String jsonSealText) {
      if (jsonSealText != null && !jsonSealText.isBlank()) {
         for (Map.Entry entry : readSourcesJson().entrySet()) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
               String stringOrNull = readStringOrNull(((JsonElement)entry.getValue()).getAsJsonObject(), "communityId");
               if (jsonSealText.equals(stringOrNull) && presetExists((String)entry.getKey())) {
                  return (String)entry.getKey();
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static void overwritePreset(String jsonSealText, ConfigPresetFile jsonVersion)  throws java.io.IOException {
      writePresetFile(jsonSealText, jsonVersion);
   }

   public static void renameSourceEntry(String jsonSealText, String sealOwnText)  throws java.io.IOException {
      JsonObject jsonSources = readSourcesJson();
      if (jsonSources.has(jsonSealText)) {
         JsonElement jsonElement = jsonSources.remove(jsonSealText);
         jsonSources.add(sealOwnText, jsonElement);
         writeSourcesJson(jsonSources);
      }
   }

   private static String writePresetFile(String formatText, ConfigPresetFile jsonVersion)  throws java.io.IOException {
      touchTimestamps(jsonVersion);
      clearOwnSeal(formatText);
      writeFileAtomically(getPresetPath(formatText), jsonVersion.toJsonBytes());
      return formatText;
   }

   public static ConfigPresetFile readPresetByName(String jsonVersionText)  throws java.io.IOException {
      Path resolve = getPresetPath(jsonVersionText);
      return !Files.exists(resolve) ? null : ConfigPresetFile.parseBytes(Files.readAllBytes(resolve), null);
   }

   public static Path getPresetsDirectory() {
      Path resolve2 = path2 != null ? path2 : getFabricConfigDir().resolve("corzclient").resolve("configs");
      if (!resolve2.equals(resolve)) {
         try {
            Files.createDirectories(resolve2);
            resolve = resolve2;
         } catch (IOException e) {
         }
      }

      return resolve2;
   }

   public static ConfigPresetFile parsePresetBytes(byte[] jsonVersionBytes, ConfigJsonValidator.Result[] results, String[] texts)  throws java.io.IOException {
      ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(jsonVersionBytes, results);
      if (jsonVersion == null) {
         return null;
      } else {
         String formatText = saveAsUnsealedPreset(jsonVersion);
         if (texts != null && texts.length > 0) {
            texts[0] = formatText;
         }

         return jsonVersion;
      }
   }

   public static boolean deletePreset(String ifExistsText)  throws java.io.IOException {
      boolean deleteIfExists = Files.deleteIfExists(getPresetPath(ifExistsText));

      try {
         JsonObject jsonSources = readSourcesJson();
         if (jsonSources.remove(ifExistsText) != null) {
            writeSourcesJson(jsonSources);
         }
      } catch (Exception e) {
      }

      return deleteIfExists;
   }

   private static String getUtcTimestamp() {
      return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS).toString();
   }

   public static String saveAsUnsealedPreset(ConfigPresetFile jsonVersion)  throws java.io.IOException {
      return writePresetFile(makeUniquePresetName(jsonVersion.name), jsonVersion);
   }

   public static void setPresetsDirectoryOverride(Path path) {
      path2 = path;
   }

   static String computeHmacHex(String hmacShaText, String realPath, byte[] hmacShaBytes) {
      byte[] pathAscii = getOrCreateSealKey();
      if (pathAscii == null) {
         return null;
      } else {
         try {
            Mac instance = Mac.getInstance("HmacSHA256");
            instance.init(new SecretKeySpec(pathAscii, "HmacSHA256"));
            instance.update((hmacShaText + "\n" + realPath + "\n").getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(instance.doFinal(hmacShaBytes));
         } catch (Exception e) {
            return null;
         }
      }
   }

   public static ConfigPresetFile getJsonVersion(byte[] jsonVersionBytes, ConfigJsonValidator.Result[] results)  throws java.io.IOException {
      return parsePresetBytes(jsonVersionBytes, results, null);
   }

   private static Path getUserConfigRoot() {
      try {
         String lowerCase = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
         if (lowerCase.contains("win")) {
            String getenv = System.getenv("APPDATA");
            if (getenv != null && !getenv.isBlank()) {
               return Path.of(getenv);
            }
         }

         String property = System.getProperty("user.home");
         if (property == null || property.isBlank()) {
            return null;
         } else if (lowerCase.contains("mac")) {
            return Path.of(property, "Library", "Application Support");
         } else {
            String directory = System.getenv("XDG_CONFIG_HOME");
            return directory != null && !directory.isBlank() ? Path.of(directory) : Path.of(property, ".config");
         }
      } catch (Exception e) {
         return null;
      }
   }

   private static Path getFabricConfigDir() {
      Path invoke2 = invoke;
      if (invoke2 != null) {
         return invoke2;
      } else {
         try {
            Class forName = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object invoke3 = forName.getMethod("getInstance").invoke(null);
            Path invoke4 = (Path)forName.getMethod("getConfigDir").invoke(invoke3);
            invoke = invoke4;
            return invoke4;
         } catch (Throwable e) {
            return Path.of(System.getProperty("user.dir"), "config");
         }
      }
   }

   private static void touchTimestamps(ConfigPresetFile jsonVersion) {
      jsonVersion.updatedAt = getUtcTimestamp();
      if (jsonVersion.createdAt == null) {
         jsonVersion.createdAt = jsonVersion.updatedAt;
      }
   }

   private static String sanitizePresetName(String configText) {
      String substring = ConfigValueSanitizer.toSlug(configText == null ? "" : configText);
      return substring.isEmpty() ? "config" : substring;
   }

   static void rewritePresetKeepingSource(String jsonSealText, ConfigPresetFile jsonVersion)  throws java.io.IOException {
      writePresetAndSeal(jsonSealText, jsonVersion, false);
   }

   public static void writeBackupJson(byte[] backupJsonBytes)  throws java.io.IOException {
      writeFileAtomically(getPresetsDirectory().resolve(".backup.corz.json"), backupJsonBytes);
   }

   static void writeFileAtomically(Path path, byte[] existingReplaceBytes)  throws java.io.IOException {
      Files.createDirectories(path.getParent());
      Path resolveSibling = path.resolveSibling(path.getFileName().toString() + ".tmp");
      Files.write(resolveSibling, existingReplaceBytes);

      try {
         Files.move(resolveSibling, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException e) {
         Files.move(resolveSibling, path, StandardCopyOption.REPLACE_EXISTING);
      }
   }

   public static String renamePreset(String jsonSealText, String name)  throws java.io.IOException {
      Path resolve = getPresetPath(jsonSealText);
      if (!Files.exists(resolve)) {
         return null;
      } else {
         byte[] allBytes = Files.readAllBytes(resolve);
         ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(allBytes, null);
         if (jsonVersion == null) {
            return null;
         } else {
            boolean sealSource = verifyOwnSeal(jsonSealText, allBytes);
            jsonVersion.name = name;
            String sealOwnText = makeUniquePresetName(name);
            byte[] bytes = jsonVersion.toJsonBytes();
            clearOwnSeal(sealOwnText);
            writeFileAtomically(getPresetPath(sealOwnText), bytes);
            if (!sealOwnText.equals(jsonSealText)) {
               Files.deleteIfExists(resolve);
               renameSourceEntry(jsonSealText, sealOwnText);
            }

            if (sealSource) {
               writeOwnSeal(sealOwnText, bytes, false);
            } else {
               clearOwnSeal(sealOwnText);
            }

            return sealOwnText;
         }
      }
   }

   private static String readStringOrNull(JsonObject json, String primitiveJsonText) {
      return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsString() : null;
   }

   static void setSealKeyPathOverride(Path path2) {
      path = path2;
      values = null;
      path3 = null;
   }

   public static final class Entry {
      public final String fileName;
      public final ConfigPresetFile jsonVersion;
      public final Path path;

      Entry(Path path2, ConfigPresetFile jsonVersion) {
         this.path = path2;
         this.jsonVersion = jsonVersion;
         String entryText = path2.getFileName().toString();
         this.fileName = entryText.endsWith(".corz.json") ? entryText.substring(0, entryText.length() - ".corz.json".length()) : entryText;
      }
   }

   public static final class Preset {
      public final boolean ownSealValid;
      public final ConfigPresetFile jsonVersion;
      public final String presetName;

      private Preset(String text, ConfigPresetFile jsonVersion, boolean applyConfigFlag) {
         this.presetName = text;
         this.jsonVersion = jsonVersion;
         this.ownSealValid = applyConfigFlag;
      }
   }

   public static enum Source {
      LOCAL,
      IMPORTED,
      COMMUNITY;
   }

   public static final class SourceMeta {
      public String communityId;
      public String communitySha256;
      public String downloadedAt;
      public PresetFileStore.Source source = PresetFileStore.Source.LOCAL;
      public String communityVersionId;
   }
}
