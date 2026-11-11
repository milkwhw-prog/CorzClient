package com.corz.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.loader.api.FabricLoader;
import ai.auth.Module;

public final class ModulesJsonStorage {
   public static final String MODULES_JSON = "modules.json";
   private static final String MAC_CONTEXT = "corz-live-state-v1";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Map<String, String> LEGACY_MODULE_NAMES = Map.of("Auto Crystal", "Crystal Aura", "Custom Sky", "ShaderFog");
   private static final String SNAPSHOT_DIR_NAME = "live-state";

   static ModulesJsonStorage.Loaded loadModules(Path path, List<Module> list, Runnable runnable, boolean nothingModulesFlag) {
      Path resolve = path.resolve("modules.json");
      if (!Files.exists(resolve)) {
         return ModulesJsonStorage.Loaded.NOTHING;
      } else {
         byte[] nothingModulesBytes;
         try {
            nothingModulesBytes = Files.readAllBytes(resolve);
         } catch (IOException e) {
            System.err.println("[C" + e.getMessage());
            return ModulesJsonStorage.Loaded.NOTHING;
         }

         JsonObject json = parseJsonObject(nothingModulesBytes);
         if (!nothingModulesFlag) {
            if (json != null) {
               applyModulesJson(json, list);
            }

            return ModulesJsonStorage.Loaded.AS_IS;
         } else {
            byte[] macState = readTrustedSnapshot(path);
            if (macState == null) {
               if (json == null) {
                  return ModulesJsonStorage.Loaded.NOTHING;
               } else {
                  applyModulesJson(json, list);
                  writeSnapshot(path, nothingModulesBytes);
                  return ModulesJsonStorage.Loaded.FIRST_RUN;
               }
            } else if (Arrays.equals(macState, nothingModulesBytes)) {
               if (json != null) {
                  applyModulesJson(json, list);
               }

               return ModulesJsonStorage.Loaded.OWN;
            } else {
               JsonObject parseJsonObject2 = parseJsonObject(macState);
               if (parseJsonObject2 != null) {
                  applyModulesJson(parseJsonObject2, list);
               }

               if (json == null) {
                  runnable.run();
                  return ModulesJsonStorage.Loaded.CHANGED_OUTSIDE;
               } else {
                  ModuleConfigSerializer.Result settingsExtra = ModuleConfigSerializer.getSettingsExtra(
                     ModuleConfigSerializer.buildVersionFromModulesJson(json, list), ConfigImportOptions.createAllSections(), list, runnable
                  );
                  if (settingsExtra.errorMessage != null) {
                     System.err.println("[C" + settingsExtra.errorMessage);
                  } else if (settingsExtra.getSummaryMessage() != null) {
                  }

                  return ModulesJsonStorage.Loaded.CHANGED_OUTSIDE;
               }
            }
         }
      }
   }

   static byte[] readTrustedSnapshot(Path path) {
      try {
         String realPath = getCanonicalPathString(path);
         Path resolve = getResolve(realPath);
         if (!Files.isRegularFile(resolve)) {
            return null;
         } else {
            JsonObject asJsonObject = JsonParser.parseString(Files.readString(resolve, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!realPath.equals(getStringField(asJsonObject, "dir"))) {
               return null;
            } else {
               String stringField = getStringField(asJsonObject, "state");
               String stringField3 = getStringField(asJsonObject, "mac");
               if (stringField != null && stringField3 != null) {
                  byte[] allBytes = gunzip(Base64.getDecoder().decode(stringField));
                  String formatHmacSha = PresetFileStore.computeHmacHex("corz-live-state-v1", realPath, allBytes);
                  if (formatHmacSha == null) {
                     return null;
                  } else {
                     return MessageDigest.isEqual(formatHmacSha.getBytes(StandardCharsets.UTF_8), stringField3.getBytes(StandardCharsets.UTF_8)) ? allBytes : null;
                  }
               } else {
                  return null;
               }
            }
         }
      } catch (Exception e) {
         return null;
      }
   }

   private static byte[] gunzip(byte[] bytesBytes)  throws java.io.IOException {
      byte[] allBytes;
      try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytesBytes))) {
         allBytes = gzipInputStream.readAllBytes();
      }

      return allBytes;
   }

   static JsonElement findModuleJson(JsonObject json, Module Module) {
      JsonElement module2 = json.get(Module.getCategory());
      if (module2 != null) {
         return module2;
      } else {
         String crystal_fog_map = LEGACY_MODULE_NAMES.get(Module.getCategory());
         return crystal_fog_map == null ? null : json.get(crystal_fog_map);
      }
   }

   public static ModulesJsonStorage.Loaded loadModulesForEnvironment(Path path, List<Module> list, Runnable runnable) {
      return loadModules(path, list, runnable, !isDevelopmentEnvironment());
   }

   private static byte[] gzip(byte[] byteArrayBytes)  throws java.io.IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
         gzipOutputStream.write(byteArrayBytes);
      }

      return byteArrayOutputStream.toByteArray();
   }

   static Path getSnapshotPath(Path path) {
      return getResolve(getCanonicalPathString(path));
   }

   static synchronized void writeModulesJson(Path path, List<Module> list, boolean jsonModulesFlag)  throws java.io.IOException {
      Files.createDirectories(path);
      byte[] bytes = GSON.toJson(serializeModules(list)).getBytes(StandardCharsets.UTF_8);
      Files.write(path.resolve("modules.json"), bytes);
      if (jsonModulesFlag) {
         writeSnapshot(path, bytes);
      }
   }

   private static JsonObject serializeModules(List<Module> list) {
      JsonObject jsonObject = new JsonObject();

      for (Module ilIllIiIll2 : list) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("enabled", ilIllIiIll2.isEnabled());
         jsonObject2.addProperty("bindKey", ilIllIiIll2.getBindKey());
         JsonObject jsonObject3 = new JsonObject();

         for (ConfigSetting autoEspBase : ilIllIiIll2.getSettings()) {
            JsonElement maxProperty = autoEspBase.getJsonValue();
            jsonObject3.add(autoEspBase.getSettingName(), (JsonElement)(maxProperty != null ? maxProperty : JsonNull.INSTANCE));
         }

         jsonObject2.add("settings", jsonObject3);
         JsonElement jsonElement = ilIllIiIll2.saveExtraData();
         if (jsonElement != null) {
            jsonObject2.add("extra", jsonElement);
         }

         jsonObject.add(ilIllIiIll2.getCategory(), jsonObject2);
      }

      return jsonObject;
   }

   private static String getStringField(JsonObject json, String primitiveJsonText) {
      return json.has(primitiveJsonText) && json.get(primitiveJsonText).isJsonPrimitive() ? json.get(primitiveJsonText).getAsString() : null;
   }

   private static boolean isDevelopmentEnvironment() {
      try {
         return FabricLoader.getInstance().isDevelopmentEnvironment();
      } catch (Throwable e) {
         return false;
      }
   }

   private static void applySettingSafely(ConfigSetting autoEspBase, JsonElement json) {
      if (!autoEspBase.isExcludedFromSave()) {
         try {
            autoEspBase.applyJsonValue(json);
         } catch (Exception e) {
            System.err.println("[C" + autoEspBase.getSettingName() + ": " + e.getMessage());
         }
      }
   }

   public static void saveModules(Path path, List<Module> list)  throws java.io.IOException {
      writeModulesJson(path, list, !isDevelopmentEnvironment());
   }

   private static JsonObject parseJsonObject(byte[] jsonBytes) {
      try {
         return (JsonObject)GSON.fromJson(new String(jsonBytes, StandardCharsets.UTF_8), JsonObject.class);
      } catch (Exception e) {
         System.err.println("[C" + e.getMessage());
         return null;
      }
   }

   private static Path getResolve(String jsonLiveText) {
      String substring = ConfigPresetFile.sha256Hex(jsonLiveText.getBytes(StandardCharsets.UTF_8)).substring(0, 32);
      return PresetFileStore.getSealKeyDirectory().resolve("live-state").resolve(substring + ".json");
   }

   private static void writeSnapshot(Path path, byte[] propertyMacBytes) {
      try {
         String realPath = getCanonicalPathString(path);
         String formatHmacSha = PresetFileStore.computeHmacHex("corz-live-state-v1", realPath, propertyMacBytes);
         if (formatHmacSha == null) {
            return;
         }

         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("dir", realPath);
         jsonObject.addProperty("mac", formatHmacSha);
         jsonObject.addProperty("state", Base64.getEncoder().encodeToString(gzip(propertyMacBytes)));
         PresetFileStore.writeFileAtomically(getResolve(realPath), jsonObject.toString().getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
         System.err.println("[C" + e.getMessage());
      }
   }

   private static void applyModulesJson(JsonObject json, List<Module> list) {
      try {
         for (Module Module : list) {
            JsonElement fogCrystal = findModuleJson(json, Module);
            if (fogCrystal != null) {
               JsonObject asJsonObject = fogCrystal.getAsJsonObject();
               if (asJsonObject.has("bindKey")) {
                  Module.setBindKey(asJsonObject.get("bindKey").getAsInt());
               }

               if (asJsonObject.has("settings")) {
                  JsonObject asJsonObject2 = asJsonObject.getAsJsonObject("settings");

                  for (ConfigSetting autoEspBase : Module.getSettings()) {
                     if (asJsonObject2.has(autoEspBase.getSettingName())) {
                        applySettingSafely(autoEspBase, asJsonObject2.get(autoEspBase.getSettingName()));
                     }
                  }
               }

               if (asJsonObject.has("extra")) {
                  Module.loadExtraData(asJsonObject.get("extra"));
               }

               Module.onConfigLoaded();
               if (asJsonObject.has("enabled")) {
                  boolean asBoolean = asJsonObject.get("enabled").getAsBoolean();
                  if (asBoolean != Module.isEnabled()) {
                     Module.setEnabled(asBoolean);
                  }
               }
            }
         }
      } catch (Exception e) {
         System.err.println("[C" + e.getMessage());
      }
   }

   private static String getCanonicalPathString(Path path) {
      Path normalize = path.toAbsolutePath().normalize();

      try {
         normalize = normalize.toRealPath();
      } catch (IOException e) {
      }

      return normalize.toString();
   }

   private ModulesJsonStorage() {
   }

   public static enum Loaded {
      NOTHING,
      AS_IS,
      FIRST_RUN,
      OWN,
      CHANGED_OUTSIDE;
   }
}
