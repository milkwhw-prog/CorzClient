package com.corz.client.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.Identifier;

public final class PlayerSkinResolver {
   private static final Map<String, SkinTextures> SKIN_CACHE = new LinkedHashMap<>();
   private static final Map<String, SkinTextures> FETCHED_HEAD_SKINS = new ConcurrentHashMap<>();
   private static final Set<String> PENDING_FETCHES = ConcurrentHashMap.newKeySet();
   private static final Set<String> FAILED_LOOKUPS = ConcurrentHashMap.newKeySet();
   private static final ExecutorService SKIN_FETCH_POOL = Executors.newFixedThreadPool(2, runnable -> {
      Thread thread = new Thread(runnable, "corz-skin-fetch");
      thread.setDaemon(true);
      return thread;
   });

   public static boolean isPlayerListed(String jsonMetadataText) {
      return jsonMetadataText != null && !jsonMetadataText.isEmpty() && getListEntry(jsonMetadataText) != null;
   }

   private static String[] fetchSkinUrlAndModel(String metadataJsonText) {
      try {
         String httpGetString2 = httpGetString("https://sessionserver.mojang.com/session/minecraft/profile/" + metadataJsonText);
         if (httpGetString2 == null) {
            return null;
         } else {
            JsonObject asJsonObject = JsonParser.parseString(httpGetString2).getAsJsonObject();
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("properties");
            if (asJsonArray != null && !asJsonArray.isEmpty()) {
               String asString = asJsonArray.get(0).getAsJsonObject().get("value").getAsString();
               String metadataJsonText2 = new String(Base64.getDecoder().decode(asString), StandardCharsets.UTF_8);
               JsonObject asJsonObject2 = JsonParser.parseString(metadataJsonText2).getAsJsonObject().getAsJsonObject("textures");
               if (asJsonObject2 == null) {
                  return null;
               } else {
                  JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("SKIN");
                  if (asJsonObject3 != null && asJsonObject3.has("url")) {
                     String asString2 = asJsonObject3.get("url").getAsString();
                     String asString3 = "";
                     if (asJsonObject3.has("metadata") && asJsonObject3.getAsJsonObject("metadata").has("model")) {
                        asString3 = asJsonObject3.getAsJsonObject("metadata").get("model").getAsString();
                     }

                     return new String[]{asString2, asString3};
                  } else {
                     return null;
                  }
               }
            } else {
               return null;
            }
         }
      } catch (Throwable e) {
         return null;
      }
   }

   private static byte[] downloadBytes(String bytesText) {
      try {
         HttpURLConnection openConnection = (HttpURLConnection)URI.create(bytesText).toURL().openConnection();
         openConnection.setConnectTimeout(5000);
         openConnection.setReadTimeout(5000);
         openConnection.setRequestProperty("User-Agent", "CorzClient");
         if (openConnection.getResponseCode() != 200) {
            return null;
         } else {
            byte[] allBytes;
            try (InputStream inputStream = openConnection.getInputStream()) {
               allBytes = inputStream.readAllBytes();
            }

            return allBytes;
         }
      } catch (Throwable e) {
         return null;
      }
   }

   private static String httpGetString(String agentUserText)  throws java.io.IOException, java.net.MalformedURLException {
      HttpURLConnection openConnection = (HttpURLConnection)URI.create(agentUserText).toURL().openConnection();
      openConnection.setConnectTimeout(5000);
      openConnection.setReadTimeout(5000);
      openConnection.setRequestProperty("User-Agent", "CorzClient");
      if (openConnection.getResponseCode() != 200) {
         return null;
      } else {
         String formatAgentUser;
         try (InputStream inputStream = openConnection.getInputStream()) {
            formatAgentUser = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
         }

         return formatAgentUser;
      }
   }

   private static void startSkinFetch(String poolThreadText) {
      String lowerCase = toLowerKey(poolThreadText);
      if (!FETCHED_HEAD_SKINS.containsKey(lowerCase) && !FAILED_LOOKUPS.contains(lowerCase) && PENDING_FETCHES.add(lowerCase)) {
         SKIN_FETCH_POOL.submit(() -> {
            try {
               String metadataJsonText = fetchUuidForName(poolThreadText);
               if (metadataJsonText == null) {
                  markLookupFailed(lowerCase);
                  return;
               }

               String[] metadataJson = fetchSkinUrlAndModel(metadataJsonText);
               if (metadataJson == null || metadataJson[0] == null) {
                  markLookupFailed(lowerCase);
                  return;
               }

               boolean equalsIgnoreCase = "slim".equalsIgnoreCase(metadataJson[1]);
               byte[] allBytes = downloadBytes(metadataJson[0]);
               if (allBytes == null) {
                  markLookupFailed(lowerCase);
                  return;
               }

               NativeImage nativeImage;
               try {
                  nativeImage = upgradeLegacySkin(NativeImage.read(new ByteArrayInputStream(allBytes)));
               } catch (Throwable e) {
                  markLookupFailed(lowerCase);
                  return;
               }

               MinecraftClient instance = MinecraftClient.getInstance();
               if (instance == null) {
                  markLookupFailed(lowerCase);
                  return;
               }

               instance.execute(() -> {
                  try {
                     Identifier var4x = Identifier.of("corzclient", "offline_skin/" + lowerCase.replaceAll("[^a-z0-9_.-]", "_"));
                     instance.getTextureManager().registerTexture(var4x, new NativeImageBackedTexture(() -> "corz_offline_skin_" + lowerCase, nativeImage));
                     FETCHED_HEAD_SKINS.put(lowerCase, SkinTextures.create(new PlayerSkinResolver.HeadAsset(var4x), null, null, equalsIgnoreCase ? PlayerSkinType.SLIM : PlayerSkinType.WIDE));
                  } catch (Throwable e2) {
                     FAILED_LOOKUPS.add(lowerCase);
                  } finally {
                     PENDING_FETCHES.remove(lowerCase);
                  }
               });
            } catch (Throwable e3) {
               markLookupFailed(lowerCase);
            }
         });
      }
   }

   public static void clearSkinCache() {
      SKIN_CACHE.clear();
   }

   private PlayerSkinResolver() {
   }

   public static SkinTextures getSkinTextures(String skinTexturesText) {
      if (skinTexturesText != null && !skinTexturesText.isEmpty()) {
         String lowerCase = toLowerKey(skinTexturesText);
         PlayerListEntry listEntry = getListEntry(skinTexturesText);
         if (listEntry != null) {
            try {
               SkinTextures skinTextures = listEntry.getSkinTextures();
               if (skinTextures != null) {
                  if (SKIN_CACHE.size() >= 128 && !SKIN_CACHE.containsKey(lowerCase)) {
                     var iterator = SKIN_CACHE.keySet().iterator();
                     if (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                     }
                  }

                  SKIN_CACHE.put(lowerCase, skinTextures);
                  return skinTextures;
               }
            } catch (Exception e) {
            }
         }

         SkinTextures skinTextures2 = SKIN_CACHE.get(lowerCase);
         if (skinTextures2 != null) {
            return skinTextures2;
         } else {
            SkinTextures skinTextures3 = FETCHED_HEAD_SKINS.get(lowerCase);
            if (skinTextures3 != null) {
               return skinTextures3;
            } else {
               startSkinFetch(skinTexturesText);
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static PlayerListEntry getListEntry(String entryText) {
      try {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance == null) {
            return null;
         } else {
            ClientPlayNetworkHandler networkHandler = instance.getNetworkHandler();
            return networkHandler == null ? null : networkHandler.getPlayerListEntry(entryText);
         }
      } catch (Exception e) {
         return null;
      }
   }

   private static void markLookupFailed(String jsonMetadataText) {
      FAILED_LOOKUPS.add(jsonMetadataText);
      PENDING_FETCHES.remove(jsonMetadataText);
   }

   private static NativeImage upgradeLegacySkin(NativeImage read) {
      if (read.getWidth() == 64 && read.getHeight() == 32) {
         NativeImage nativeImage = new NativeImage(64, 64, true);
         nativeImage.copyFrom(read);
         read.close();
         boolean jsonMetadataFlag = true;

         for (int i = 32; i < 64 && jsonMetadataFlag; i++) {
            for (int j = 0; j < 32; j++) {
               if (nativeImage.getColorArgb(i, j) >>> 24 < 128) {
                  jsonMetadataFlag = false;
                  break;
               }
            }
         }

         if (jsonMetadataFlag) {
            for (int k = 32; k < 64; k++) {
               for (int m = 0; m < 32; m++) {
                  nativeImage.setColorArgb(k, m, nativeImage.getColorArgb(k, m) & 16777215);
               }
            }
         }

         return nativeImage;
      } else {
         return read;
      }
   }

   private static String fetchUuidForName(String jsonText) {
      try {
         String httpGetString2 = httpGetString("https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(jsonText, StandardCharsets.UTF_8));
         if (httpGetString2 != null && !httpGetString2.isBlank()) {
            JsonObject asJsonObject = JsonParser.parseString(httpGetString2).getAsJsonObject();
            return asJsonObject.has("id") ? asJsonObject.get("id").getAsString() : null;
         } else {
            return null;
         }
      } catch (Throwable e) {
         return null;
      }
   }

   private static String toLowerKey(String lowerCaseText) {
      return lowerCaseText == null ? "" : lowerCaseText.toLowerCase(Locale.ROOT);
   }

   private record HeadAsset(Identifier tex) implements TextureAsset {
      @Override
      public Identifier texturePath() {
         return this.tex;
      }

      @Override
      public Identifier id() {
         return this.tex;
      }
   }
}
