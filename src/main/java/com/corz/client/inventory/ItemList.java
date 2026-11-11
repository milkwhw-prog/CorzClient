package com.corz.client.inventory;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.corz.client.utils.MinmaxEnumType;

public class ItemList extends ConfigSetting {
   private final List<Item> itemList;
   private final List<Item> containsItemList;

   public String formatText() {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < this.containsItemList.size(); i++) {
         stringBuilder.append(Registries.ITEM.getId(this.containsItemList.get(i)).toString());
         if (i < this.containsItemList.size() - 1) {
            stringBuilder.append(",");
         }
      }

      return stringBuilder.toString();
   }

   @Override
   public JsonElement getJsonValue() {
      return new JsonPrimitive(this.formatText());
   }

   public void handle(Item fullInventory) {
      if (this.containsItemList.remove(fullInventory)) {
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public boolean isContains(Item up) {
      return this.containsItemList.contains(up);
   }

   public void handleForItem(Item up) {
      if (!this.containsItemList.contains(up)) {
         this.containsItemList.add(up);
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public void handle(String text) {
      this.containsItemList.clear();
      if (text != null && !text.isBlank()) {
         for (String text2 : text.split(",")) {
            String trim = text2.trim();
            if (!trim.isEmpty()) {
               Item item = Registries.ITEM.get(Identifier.of(trim));
               if (item != null) {
                  this.containsItemList.add(item);
               }
            }
         }

         ModuleConfigAutosave.updateFlag2();
      }
   }

   public void update() {
      this.containsItemList.clear();
      this.containsItemList.addAll(this.itemList);
      ModuleConfigAutosave.updateFlag2();
   }

   @Override
   public ConfigSetting.Applied applyJsonValue(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            this.handle(json.getAsString());
            return ConfigSetting.Applied.APPLIED;
         } catch (Exception e) {
            return ConfigSetting.Applied.NOT_HANDLED;
         }
      } else {
         return ConfigSetting.Applied.NOT_HANDLED;
      }
   }

   public ItemList(String text, String description, List<Item> list) {
      super(text, description);
      this.itemList = new ArrayList<>(list);
      this.containsItemList = new ArrayList<>(list);
   }

   public List<Item> getContainsItemList() {
      return this.containsItemList;
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.ITEM_LIST;
   }

   public void handle3(Item up) {
      if (this.isContains(up)) {
         this.handle(up);
      } else {
         this.handleForItem(up);
      }
   }
}
