package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.corz.client.utils.Spawner;
import com.corz.client.utils.SpawnerTimeUtils;
import com.corz.client.utils.MinecraftSpiderUtils;

public final class RememberedSpawner {
   private final LinkedHashMap<Long, Spawner> rememberedSpawnerMap;
   private boolean rememberedActive;
   private int updateRevision;
   private final LinkedHashMap<String, LinkedHashMap<Long, Spawner>> rememberedSpawnerSpawnerMap;
   private final Map<Long, Set<Long>> rememberedSetMap;
   private long spawnerSequenceNumber;
   private String rememberedText = "";

   public void update() {
      this.rememberedSpawnerMap.clear();
      this.rememberedSetMap.clear();
      this.rememberedSpawnerSpawnerMap.clear();
      this.updateRevision++;
   }

   public int getValue() {
      int rememberedSpawnerCounter = 0;

      for (Spawner spawner : this.rememberedSpawnerMap.values()) {
         if (spawner.isLoadedRemembered()) {
            rememberedSpawnerCounter++;
         }
      }

      return rememberedSpawnerCounter;
   }

   public boolean canFlag(int rememberedSpawnerValue, int y, int z) {
      return this.canFlagForLong(SpawnerTimeUtils.getTime(rememberedSpawnerValue, y, z));
   }

   public int getUpdateRevision() {
      return this.updateRevision;
   }

   public Map<String, List<Spawner>> getText() {
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      for (Entry entry : this.rememberedSpawnerSpawnerMap.entrySet()) {
         linkedHashMap.put((String)entry.getKey(), new ArrayList(((LinkedHashMap)entry.getValue()).values()));
      }

      if (!this.rememberedText.isEmpty() && !this.rememberedSpawnerMap.isEmpty()) {
         linkedHashMap.put(this.rememberedText, new ArrayList<>(this.rememberedSpawnerMap.values()));
      }

      return linkedHashMap;
   }

   public boolean canFlag(int rememberedSpawnerValue, int y, int z, boolean rememberedSpawnerFlag, String rememberedSpawnerText) {
      return rememberedSpawnerFlag ? this.canLoadedTime(rememberedSpawnerValue, y, z, rememberedSpawnerText) : this.canFlag(rememberedSpawnerValue, y, z);
   }

   private void handleTime(Spawner spawner) {
      this.rememberedSetMap.computeIfAbsent(spawner.regionKey, long2 -> new HashSet<>()).add(spawner.spawnerPositionKey);
   }

   public void updateRememberedSpawnerSpawnerMap() {
      boolean rememberedSpawnerFlag = !this.rememberedSpawnerSpawnerMap.isEmpty();
      this.rememberedSpawnerSpawnerMap.clear();
      Iterator iterator = this.rememberedSpawnerMap.values().iterator();

      while (iterator.hasNext()) {
         Spawner spawner = (Spawner)iterator.next();
         if (!spawner.isLoadedRemembered()) {
            iterator.remove();
            this.handleTimeForSpawner(spawner);
            rememberedSpawnerFlag = true;
         }
      }

      if (rememberedSpawnerFlag) {
         this.updateRevision++;
      }
   }

   public void handleFlag(boolean flag) {
      if (this.rememberedActive != flag) {
         this.rememberedActive = flag;
         this.updateRevision++;
      }
   }

   public boolean canFlagForInt(int rememberedSpawnerValue, int y, int z) {
      return this.rememberedSpawnerMap.containsKey(SpawnerTimeUtils.getTime(rememberedSpawnerValue, y, z));
   }

   public String getRememberedText() {
      return this.rememberedText;
   }

   private boolean canFlag(long rememberedSpawnerValue) {
      Spawner spawner = this.rememberedSpawnerMap.remove(rememberedSpawnerValue);
      if (spawner == null) {
         return false;
      } else {
         this.handleTimeForSpawner(spawner);
         return true;
      }
   }

   private boolean canFlagForLong(long rememberedSpawnerValue) {
      boolean rememberedSpawnerFlag = this.canFlag(rememberedSpawnerValue);
      if (rememberedSpawnerFlag) {
         this.updateRevision++;
      }

      return rememberedSpawnerFlag;
   }

   public void handleRememberedFlag(long rememberedValue) {
      Set set = this.rememberedSetMap.get(rememberedValue);
      if (set != null && !set.isEmpty()) {
         boolean rememberedFlag = false;

         for (Long long2 : (Iterable<java.lang.Long>) (new ArrayList(set))) {
            Spawner spawner = this.rememberedSpawnerMap.get(long2);
            if (spawner != null && spawner.isLoadedRemembered()) {
               if (this.rememberedActive) {
                  spawner.setRememberedLoadedType(Spawner.State.REMEMBERED);
               } else {
                  this.canFlag(long2);
               }

               rememberedFlag = true;
            }
         }

         if (rememberedFlag) {
            this.updateRememberedSpawnerMap();
            this.updateRevision++;
         }
      }
   }

   public List<Spawner> getFlag() {
      ArrayList arrayList = new ArrayList(this.rememberedSpawnerMap.size());

      for (Spawner spawner : this.rememberedSpawnerMap.values()) {
         if (spawner.isLoadedRemembered() || this.rememberedActive) {
            arrayList.add(spawner);
         }
      }

      return arrayList;
   }

   public RememberedSpawner() {
      this.rememberedSpawnerMap = new LinkedHashMap<>();
      this.rememberedSetMap = new HashMap<>();
      this.rememberedSpawnerSpawnerMap = new LinkedHashMap<>();
      this.rememberedActive = true;
      this.updateRevision = 1;
   }

   public Spawner getSpawner(long spawnerValue) {
      return this.rememberedSpawnerMap.get(spawnerValue);
   }

   public int getValueRememberedSpawnerMap() {
      int rememberedSpawnerCounter = 0;

      for (Spawner spawner : this.rememberedSpawnerMap.values()) {
         if (!spawner.isLoadedRemembered()) {
            rememberedSpawnerCounter++;
         }
      }

      return rememberedSpawnerCounter;
   }

   private void update3() {
      while (this.rememberedSpawnerSpawnerMap.size() > 32) {
         String iterator = this.rememberedSpawnerSpawnerMap.keySet().iterator().next();
         this.rememberedSpawnerSpawnerMap.remove(iterator);
      }
   }

   public void handleTextRemembered(String rememberedText) {
      String text = rememberedText == null ? "" : rememberedText;
      if (!text.equals(this.rememberedText)) {
         LinkedHashMap linkedHashMap = new LinkedHashMap();

         for (Spawner spawner : this.rememberedSpawnerMap.values()) {
            if (spawner.isLoadedRemembered()) {
               if (!this.rememberedActive) {
                  continue;
               }

               spawner.setRememberedLoadedType(Spawner.State.REMEMBERED);
            }

            linkedHashMap.put(spawner.spawnerPositionKey, spawner);
         }

         if (!this.rememberedText.isEmpty() && !linkedHashMap.isEmpty()) {
            this.rememberedSpawnerSpawnerMap.remove(this.rememberedText);
            this.rememberedSpawnerSpawnerMap.put(this.rememberedText, linkedHashMap);
            this.update3();
         }

         this.rememberedSpawnerMap.clear();
         this.rememberedSetMap.clear();
         this.rememberedText = text;
         LinkedHashMap linkedHashMap2 = this.rememberedSpawnerSpawnerMap.remove(text);
         if (linkedHashMap2 != null) {
            for (Spawner finish : (Iterable<com.corz.client.utils.Spawner>) (linkedHashMap2.values())) {
               finish.setRememberedLoadedType(Spawner.State.REMEMBERED);
               this.rememberedSpawnerMap.put(finish.spawnerPositionKey, finish);
               this.handleTime(finish);
            }
         }

         this.updateRememberedSpawnerMap();
         this.updateRevision++;
      }
   }

   public boolean canFlag(long rememberedSpawnerValue, String rememberedSpawnerText) {
      String lowerCase = MinecraftSpiderUtils.getLowerCase(rememberedSpawnerText);
      if (lowerCase == null) {
         return false;
      } else {
         Spawner spawner = this.rememberedSpawnerMap.get(rememberedSpawnerValue);
         if (spawner != null && !lowerCase.equals(spawner.getSpawnerText())) {
            spawner.setSpawnerText(lowerCase);
            this.updateRevision++;
            return true;
         } else {
            return false;
         }
      }
   }

   private void handleTimeForSpawner(Spawner spawner) {
      Set set = this.rememberedSetMap.get(spawner.regionKey);
      if (set != null) {
         set.remove(spawner.spawnerPositionKey);
         if (set.isEmpty()) {
            this.rememberedSetMap.remove(spawner.regionKey);
         }
      }
   }

   private void updateRememberedSpawnerMap() {
      int rememberedSpawnerLimit = this.getValueRememberedSpawnerMap();
      if (rememberedSpawnerLimit > 4096) {
         int rememberedSpawnerCounter = rememberedSpawnerLimit - 4096;
         Iterator iterator = this.rememberedSpawnerMap.values().iterator();

         while (iterator.hasNext() && rememberedSpawnerCounter > 0) {
            Spawner spawner = (Spawner)iterator.next();
            if (!spawner.isLoadedRemembered()) {
               iterator.remove();
               this.handleTimeForSpawner(spawner);
               rememberedSpawnerCounter--;
            }
         }
      }
   }

   public boolean canLoadedTime(int loadedValue, int y, int z, String loadedText) {
      long time4 = SpawnerTimeUtils.getTime(loadedValue, y, z);
      Spawner spawner = this.rememberedSpawnerMap.get(time4);
      boolean loadedTime = false;
      if (spawner == null) {
         Spawner finish = new Spawner(loadedValue, y, z, MinecraftSpiderUtils.getLowerCase(loadedText), Spawner.State.LOADED, ++this.spawnerSequenceNumber);
         this.rememberedSpawnerMap.put(time4, finish);
         this.handleTime(finish);
         loadedTime = true;
      } else {
         if (!spawner.isLoadedRemembered()) {
            spawner.setRememberedLoadedType(Spawner.State.LOADED);
            loadedTime = true;
         }

         String lowerCase = MinecraftSpiderUtils.getLowerCase(loadedText);
         if (lowerCase != null && !lowerCase.equals(spawner.getSpawnerText())) {
            spawner.setSpawnerText(lowerCase);
            loadedTime = true;
         }
      }

      if (loadedTime) {
         this.updateRevision++;
      }

      return loadedTime;
   }

   public void handle(long rememberedSpawnerValue, Map<Long, String> map) {
      Set set = this.rememberedSetMap.get(rememberedSpawnerValue);
      if (set != null && !set.isEmpty()) {
         for (Long long2 : (Iterable<java.lang.Long>) (new ArrayList(set))) {
            if (!map.containsKey(long2)) {
               this.canFlagForLong(long2);
            }
         }
      }

      for (Entry entry : map.entrySet()) {
         long spawnerValue = (Long)entry.getKey();
         this.canLoadedTime(SpawnerTimeUtils.getValue4(spawnerValue), SpawnerTimeUtils.getValue5(spawnerValue), SpawnerTimeUtils.getValueInt(spawnerValue), (String)entry.getValue());
      }
   }

   public int getRememberedSpawnerSpawnerMap() {
      return this.rememberedSpawnerSpawnerMap.size();
   }

   public void handleRememberedTime(String rememberedText, List<int[]> list, List<String> list2) {
      if (rememberedText != null && !rememberedText.isEmpty() && list != null) {
         if (rememberedText.equals(this.rememberedText)) {
            for (int i = 0; i < list.size(); i++) {
               int[] importscope = (int[])list.get(i);
               long rememberedValue = SpawnerTimeUtils.getTime(importscope[0], importscope[1], importscope[2]);
               if (!this.rememberedSpawnerMap.containsKey(rememberedValue)) {
                  String lowerCaseText = list2 != null && i < list2.size() ? (String)list2.get(i) : null;
                  Spawner spawner = new Spawner(importscope[0], importscope[1], importscope[2], MinecraftSpiderUtils.getLowerCase(lowerCaseText), Spawner.State.REMEMBERED, ++this.spawnerSequenceNumber);
                  this.rememberedSpawnerMap.put(rememberedValue, spawner);
                  this.handleTime(spawner);
               }
            }

            this.updateRememberedSpawnerMap();
         } else {
            LinkedHashMap ifAbsent = this.rememberedSpawnerSpawnerMap.computeIfAbsent(rememberedText, rememberedText3 -> new LinkedHashMap<>());

            for (int j = 0; j < list.size(); j++) {
               int[] importscope2 = (int[])list.get(j);
               long formatGroundAir = SpawnerTimeUtils.getTime(importscope2[0], importscope2[1], importscope2[2]);
               if (!ifAbsent.containsKey(formatGroundAir)) {
                  if (ifAbsent.size() >= 4096) {
                     break;
                  }

                  String rememberedText4 = list2 != null && j < list2.size() ? (String)list2.get(j) : null;
                  ifAbsent.put(formatGroundAir, new Spawner(importscope2[0], importscope2[1], importscope2[2], MinecraftSpiderUtils.getLowerCase(rememberedText4), Spawner.State.REMEMBERED, ++this.spawnerSequenceNumber));
               }
            }

            if (ifAbsent.isEmpty()) {
               this.rememberedSpawnerSpawnerMap.remove(rememberedText);
            }

            this.update3();
         }

         this.updateRevision++;
      }
   }

   public List<Spawner> getItems(int rememberedSpawnerValue) {
      ArrayList arrayList = new ArrayList();

      for (Spawner spawner : this.rememberedSpawnerMap.values()) {
         if (spawner.isLoadedRemembered() && !spawner.isSpawnerText()) {
            arrayList.add(spawner);
            if (arrayList.size() >= rememberedSpawnerValue) {
               break;
            }
         }
      }

      return arrayList;
   }

   public void updateRememberedFlag() {
      boolean rememberedFlag = false;
      Iterator iterator = this.rememberedSpawnerMap.values().iterator();

      while (iterator.hasNext()) {
         Spawner spawner = (Spawner)iterator.next();
         if (spawner.isLoadedRemembered()) {
            if (this.rememberedActive) {
               spawner.setRememberedLoadedType(Spawner.State.REMEMBERED);
            } else {
               iterator.remove();
               this.handleTimeForSpawner(spawner);
            }

            rememberedFlag = true;
         }
      }

      if (rememberedFlag) {
         this.updateRememberedSpawnerMap();
         this.updateRevision++;
      }
   }

   public boolean isFlag() {
      return this.rememberedActive;
   }
}
