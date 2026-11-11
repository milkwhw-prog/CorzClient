package com.corz.client.config;

import com.corz.client.config.PresetFileStore.Entry;
import com.corz.client.config.PresetFileStore.Source;
import com.corz.client.config.PresetFileStore.SourceMeta;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import ai.auth.LicenseRuntime;

public final class ConfigSyncUtils {
   private static final String PRIVATE = "PRIVATE";

   public static boolean isSignedIn() {
      return LicenseRuntime.getAuthToken() != null;
   }

   private static Set<String> getLocalConfigHashes() {
      var hashSet = new HashSet();

      for (Entry entry : PresetFileStore.listPresets()) {
         try {
            hashSet.add(entry.jsonVersion.computeShaString());
         } catch (Exception e) {
         }

         try {
            ConfigPresetFile jsonVersion = CommunityConfigClient.applyUploadSafetyRules(entry.jsonVersion);
            if (jsonVersion != null) {
               hashSet.add(jsonVersion.computeShaString());
            }
         } catch (Exception e2) {
         }
      }

      return hashSet;
   }

   public static void downloadMissingServerConfigs(Consumer<Integer> consumer) {
      if (!isSignedIn()) {
         if (consumer != null) {
            consumer.accept(0);
         }
      } else {
         CommunityConfigClient.fetchMyConfigs(onDonex -> {
            if (onDonex.ok && onDonex.value != null) {
               var versionJson = getLocalConfigHashes();
               int[] ints = new int[]{0};
               int[] ints2 = new int[]{0};
               boolean[] booleans = new boolean[]{false};

               for (SharedConfigEntry versionJsonUtils : ((ConfigsPage)onDonex.value).configs) {
                  if (versionJsonUtils.sha256 == null || !versionJson.contains(versionJsonUtils.sha256)) {
                     String configSyncText = PresetFileStore.findPresetNameByCommunityId(versionJsonUtils.id);
                     if (configSyncText == null || !PresetFileStore.hasValidOwnSeal(configSyncText)) {
                        ints[0]++;
                        CommunityConfigClient.downloadConfig(versionJsonUtils.id, null, listed -> {
                           if (listed.ok) {
                              try {
                                 ConfigPresetFile jsonVersion = ConfigPresetFile.parseBytes(listed.value.bytes, null);
                                 if (jsonVersion != null) {
                                    String var7x = PresetFileStore.savePresetForCommunityId(jsonVersion, versionJsonUtils.id);
                                    linkLocalConfigToServerId(var7x, versionJsonUtils.id);
                                    synchronized (ints2) {
                                       ints2[0]++;
                                    }
                                 }
                              } catch (IOException e) {
                              }
                           }

                           synchronized (ints) {
                              ints[0]--;
                              if (booleans[0] && ints[0] == 0 && consumer != null) {
                                 consumer.accept(ints2[0]);
                              }
                           }
                        });
                     }
                  }
               }

               synchronized (ints) {
                  booleans[0] = true;
                  if (ints[0] == 0 && consumer != null) {
                     consumer.accept(ints2[0]);
                  }
               }
            } else {
               if (consumer != null) {
                  consumer.accept(-1);
               }
            }
         });
      }
   }

   private ConfigSyncUtils() {
   }

   private static void linkLocalConfigToServerId(String sourceCommunityText, String communityId) {
      try {
         SourceMeta communityDownloaded = PresetFileStore.readSourceMeta(sourceCommunityText);
         if (communityDownloaded.source == null) {
            communityDownloaded.source = Source.LOCAL;
         }

         communityDownloaded.communityId = communityId;
         PresetFileStore.saveSourceMeta(sourceCommunityText, communityDownloaded);
      } catch (IOException e) {
      }
   }

   public static void deleteServerCopyOfLocalConfig(String communityText) {
      SourceMeta communityDownloaded = PresetFileStore.readSourceMeta(communityText);
      if (communityDownloaded.communityId != null && !communityDownloaded.communityId.isBlank() && isSignedIn()) {
         CommunityConfigClient.deleteConfig(communityDownloaded.communityId, result -> {});
      }
   }

   public static void uploadPrivateConfig(String configPrivateText, Consumer<ConfigSyncUtils.Result> consumer) {
      if (!isSignedIn()) {
         consumer.accept(ConfigSyncUtils.Result.errorResult("Sign in to share configs."));
      } else {
         SourceMeta communityDownloaded = PresetFileStore.readSourceMeta(configPrivateText);
         if (communityDownloaded.communityId != null && !communityDownloaded.communityId.isBlank()) {
            consumer.accept(ConfigSyncUtils.Result.okResult(communityDownloaded.communityId));
         } else {
            ConfigPresetFile jsonVersion;
            try {
               jsonVersion = PresetFileStore.readPresetByName(configPrivateText);
            } catch (IOException e) {
               consumer.accept(ConfigSyncUtils.Result.errorResult("Couldn't read the config."));
               return;
            }

            if (jsonVersion == null) {
               consumer.accept(ConfigSyncUtils.Result.errorResult("Config not found."));
            } else {
               ConfigPresetFile applyUploadSafetyRules = CommunityConfigClient.applyUploadSafetyRules(jsonVersion);
               if (applyUploadSafetyRules == null) {
                  consumer.accept(ConfigSyncUtils.Result.errorResult("Config safety rules are missing."));
               } else {
                  String formatText = applyUploadSafetyRules.computeShaString();
                  CommunityConfigClient.publishConfig(jsonVersion, jsonVersion.tagsContentItems, "PRIVATE", "Personal sync", cbx -> {
                     if (cbx.ok) {
                        linkLocalConfigToServerId(configPrivateText, ((SharedConfigEntry)cbx.value).id);
                        consumer.accept(ConfigSyncUtils.Result.okResult(((SharedConfigEntry)cbx.value).id));
                     } else if ("DUPLICATE".equals(cbx.errorCode)) {
                        CommunityConfigClient.fetchMyConfigs(mineRes -> {
                           if (mineRes.ok) {
                              String configIdBySha = findConfigIdBySha(mineRes.value, formatText);
                              if (configIdBySha != null) {
                                 linkLocalConfigToServerId(configPrivateText, configIdBySha);
                                 consumer.accept(ConfigSyncUtils.Result.okResult(configIdBySha));
                                 return;
                              }
                           }

                           consumer.accept(ConfigSyncUtils.Result.errorResult("Couldn't sync this config to the server."));
                        });
                     } else {
                        consumer.accept(ConfigSyncUtils.Result.errorResult(cbx.errorMessage != null ? cbx.errorMessage : "Couldn't sync this config."));
                     }
                  });
               }
            }
         }
      }
   }

   private static String findConfigIdBySha(ConfigsPage configsJsonUtils, String resultsShaText) {
      if (configsJsonUtils != null && resultsShaText != null) {
         for (SharedConfigEntry versionJsonUtils : configsJsonUtils.configs) {
            if (resultsShaText.equalsIgnoreCase(versionJsonUtils.sha256)) {
               return versionJsonUtils.id;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static final class Result {
      public final String errorMessage;
      public final String configId;
      public final boolean ok;

      static ConfigSyncUtils.Result errorResult(String configSyncText) {
         return new ConfigSyncUtils.Result(false, null, configSyncText);
      }

      private Result(boolean publishCouldnFlag, String couldnPrepareText, String couldnPublishText) {
         this.ok = publishCouldnFlag;
         this.configId = couldnPrepareText;
         this.errorMessage = couldnPublishText;
      }

      static ConfigSyncUtils.Result okResult(String configSyncText) {
         return new ConfigSyncUtils.Result(true, configSyncText, null);
      }
   }
}
