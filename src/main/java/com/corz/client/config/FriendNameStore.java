package com.corz.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;

public final class FriendNameStore {
   private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("corzclient");
   private static final Path FRIENDS_FILE = CONFIG_DIR.resolve("friends.json");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final LinkedHashSet<String> FRIEND_NAMES = new LinkedHashSet<>();

   public static void clearFriends() {
      FRIEND_NAMES.clear();
      saveFriends();
   }

   public static boolean removeFriend(String jsonGsonText) {
      if (jsonGsonText == null) {
         return false;
      } else {
         String lowerCase = jsonGsonText.toLowerCase(Locale.ROOT);
         boolean removeIf = FRIEND_NAMES.removeIf(jsonGsonText2 -> jsonGsonText2.toLowerCase(Locale.ROOT).equals(lowerCase));
         if (removeIf) {
            saveFriends();
         }

         return removeIf;
      }
   }

   public static boolean addFriend(String linkedHashText) {
      if (linkedHashText == null) {
         return false;
      } else {
         linkedHashText = linkedHashText.trim();
         if (!linkedHashText.isEmpty() && !containsFriendIgnoreCase(linkedHashText)) {
            FRIEND_NAMES.add(linkedHashText);
            saveFriends();
            return true;
         } else {
            return false;
         }
      }
   }

   public static List<String> getFriendNames() {
      return new ArrayList<>(FRIEND_NAMES);
   }

   private FriendNameStore() {
   }

   public static void saveFriends() {
      try {
         Files.createDirectories(CONFIG_DIR);
         JsonArray jsonArray = new JsonArray();

         for (String gsonText : FRIEND_NAMES) {
            jsonArray.add(gsonText);
         }

         try (BufferedWriter bufferedWriter = Files.newBufferedWriter(FRIENDS_FILE)) {
            GSON.toJson(jsonArray, bufferedWriter);
         }
      } catch (Exception e) {
         System.err.println("[C" + e.getMessage());
      }
   }

   public static boolean isEmpty() {
      return FRIEND_NAMES.isEmpty();
   }

   public static boolean isFriend(String jsonGsonText) {
      return containsFriendIgnoreCase(jsonGsonText);
   }

   public static boolean canFlag(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         String jsonGsonText = entity.getName().getString();
         return containsFriendIgnoreCase(jsonGsonText);
      }
   }

   public static void loadFriends() {
      if (Files.exists(FRIENDS_FILE)) {
         try {
            label72: {
               try (BufferedReader bufferedReader = Files.newBufferedReader(FRIENDS_FILE)) {
                  JsonArray json = (JsonArray)GSON.fromJson(bufferedReader, JsonArray.class);
                  if (json != null) {
                     FRIEND_NAMES.clear();

                     for (JsonElement jsonElement : json) {
                        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
                           String trim = jsonElement.getAsString().trim();
                           if (!trim.isEmpty()) {
                              FRIEND_NAMES.add(trim);
                           }
                        }
                     }
                     break label72;
                  }
               }

               return;
            }
         } catch (Exception e) {
            System.err.println("[C" + e.getMessage());
         }
      }
   }

   public static boolean containsFriendIgnoreCase(String rootLinkedText) {
      if (rootLinkedText != null && !rootLinkedText.isEmpty()) {
         String lowerCase = rootLinkedText.toLowerCase(Locale.ROOT);

         for (String friendNamesItem : FRIEND_NAMES) {
            if (friendNamesItem.toLowerCase(Locale.ROOT).equals(lowerCase)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static void toggleFriend(String jsonGsonText) {
      if (containsFriendIgnoreCase(jsonGsonText)) {
         removeFriend(jsonGsonText);
      } else {
         addFriend(jsonGsonText);
      }
   }

   public static int getFriendCount() {
      return FRIEND_NAMES.size();
   }
}
