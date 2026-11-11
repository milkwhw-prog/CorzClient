package com.corz.client.utils;

import com.corz.client.config.ModuleConfigAutosave;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class PngIcons {
   private String pngIconsText;
   private final Map<String, Boolean> orDefaultBooleanMap = new HashMap<>();
   private static final PngIcons INSTANCE = new PngIcons();
   private boolean booleanValue;
   private IconsPngType iconsPngType = IconsPngType.COMBAT;

   private PngIcons() {
   }

   public String getText() {
      return this.pngIconsText;
   }

   public boolean isOrDefault(String orDefaultText) {
      return this.orDefaultBooleanMap.getOrDefault(orDefaultText, false);
   }

   public List<IconsPngType> getMiscDonutsmp() {
      var arrayList = new ArrayList();
      arrayList.add(IconsPngType.COMBAT);
      arrayList.add(IconsPngType.MOVEMENT);
      arrayList.add(IconsPngType.DONUTSMP);
      arrayList.add(IconsPngType.VISUAL);
      arrayList.add(IconsPngType.MISC);
      arrayList.add(IconsPngType.NETWORK);
      arrayList.add(IconsPngType.BOTS);
      arrayList.add(IconsPngType.SETTING);
      arrayList.add(IconsPngType.CONFIGS);
      arrayList.add(IconsPngType.THEME);
      arrayList.add(IconsPngType.SOCIALS);
      return arrayList;
   }

   public void handle(boolean booleanValue) {
      if (this.booleanValue != booleanValue) {
         this.booleanValue = booleanValue;
         ModuleConfigAutosave.updateFlag2();
      }
   }

   public List<Module> getItems(IconsPngType iconsPngType) {
      return ModuleRegistry.getModulesByCategory(iconsPngType);
   }

   public boolean isBooleanValue() {
      return this.booleanValue;
   }

   public IconsPngType getIconsPngType() {
      return this.iconsPngType;
   }

   public void setText(String text) {
      this.pngIconsText = text;
   }

   public static PngIcons getInstance() {
      return INSTANCE;
   }

   public void setIconsPngType(IconsPngType iconsPngType) {
      this.iconsPngType = iconsPngType;
   }

   public void handle(String pngIconsText, boolean pngIconsFlag) {
      this.orDefaultBooleanMap.put(pngIconsText, pngIconsFlag);
   }

   public List<Module> getItems() {
      return ModuleRegistry.getModules();
   }
}
