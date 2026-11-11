package com.corz.client.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.DimensionPosData;
import com.corz.client.utils.ChestTrappedType;

public final class JsonDim {
   private volatile List<DimensionPosData> entriesSnapshot;
   private final Map<String, DimensionPosData> entryMap = new LinkedHashMap<>();

   public synchronized void loadFromJson(JsonElement json) {
      this.entryMap.clear();
      if (json != null && json.isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray()) {
            try {
               JsonObject asJsonObject = jsonElement.getAsJsonObject();
               long asLong = asJsonObject.get("pos").getAsLong();
               String asString = asJsonObject.get("dim").getAsString();
               ChestTrappedType valueOf = ChestTrappedType.valueOf(asJsonObject.get("type").getAsString());
               this.entryMap.put(formatEntryKey(asString, asLong), new DimensionPosData(asLong, asString, valueOf));
            } catch (Exception e) {
            }
         }
      }

      this.refreshSnapshot();
   }

   public List<DimensionPosData> getEntriesSnapshot() {
      return this.entriesSnapshot;
   }

   public JsonDim() {
      this.entriesSnapshot = List.of();
   }

   public synchronized List<DimensionPosData> removeStaleEntriesInLoadedChunks(ClientWorld clientWorld) {
      if (clientWorld == null) {
         return List.of();
      } else {
         String registryLoadedText = clientWorld.getRegistryKey().getValue().toString();
         ArrayList registryLoaded = null;
         Iterator iterator = this.entryMap.entrySet().iterator();

         while (iterator.hasNext()) {
            DimensionPosData dimensionPosData = (DimensionPosData)((Entry)iterator.next()).getValue();
            if (dimensionPosData.dimension().equals(registryLoadedText)) {
               BlockPos long2 = dimensionPosData.getLong();
               if (clientWorld.isChunkLoaded(long2.getX() >> 4, long2.getZ() >> 4) && ChestTrappedType.getChestTrapped(clientWorld.getBlockState(long2).getBlock()) == null) {
                  iterator.remove();
                  if (registryLoaded == null) {
                     registryLoaded = new ArrayList();
                  }

                  registryLoaded.add(dimensionPosData);
               }
            }
         }

         if (registryLoaded == null) {
            return List.of();
         } else {
            this.refreshSnapshot();
            return registryLoaded;
         }
      }
   }

   public synchronized boolean addEntry(BlockPos pos2, String jsonDimText, ChestTrappedType chestTrappedType) {
      String obsidianIdleValue = formatEntryKey(jsonDimText, pos2.asLong());
      boolean jsonDimFlag = !this.entryMap.containsKey(obsidianIdleValue);
      this.entryMap.put(obsidianIdleValue, DimensionPosData.of(pos2, jsonDimText, chestTrappedType));
      this.refreshSnapshot();
      return jsonDimFlag;
   }

   public synchronized JsonElement serializeEntries() {
      JsonArray jsonArray = new JsonArray();

      for (DimensionPosData dimensionPosData : this.entryMap.values()) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("pos", dimensionPosData.pos());
         jsonObject.addProperty("dim", dimensionPosData.dimension());
         jsonObject.addProperty("type", dimensionPosData.type().name());
         jsonArray.add(jsonObject);
      }

      return jsonArray;
   }

   public synchronized void clearEntries() {
      this.entryMap.clear();
      this.refreshSnapshot();
   }

   private void refreshSnapshot() {
      this.entriesSnapshot = List.copyOf(this.entryMap.values());
   }

   private static String formatEntryKey(String jsonDimText, long jsonDimValue) {
      return jsonDimText + "@" + jsonDimValue;
   }

   public synchronized boolean removeEntry(BlockPos pos2, String jsonDimText) {
      if (this.entryMap.remove(formatEntryKey(jsonDimText, pos2.asLong())) != null) {
         this.refreshSnapshot();
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean containsEntry(BlockPos pos2, String jsonDimText) {
      return this.entryMap.containsKey(formatEntryKey(jsonDimText, pos2.asLong()));
   }

   public synchronized boolean toggleEntry(BlockPos pos2, String jsonDimText, ChestTrappedType chestTrappedType) {
      String immutablePosValue = formatEntryKey(jsonDimText, pos2.asLong());
      if (this.entryMap.remove(immutablePosValue) != null) {
         this.refreshSnapshot();
         return false;
      } else {
         this.entryMap.put(immutablePosValue, DimensionPosData.of(pos2, jsonDimText, chestTrappedType));
         this.refreshSnapshot();
         return true;
      }
   }

   public synchronized int getEntryCount() {
      return this.entryMap.size();
   }
}
