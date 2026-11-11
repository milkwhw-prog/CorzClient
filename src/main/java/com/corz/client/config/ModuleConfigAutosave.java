package com.corz.client.config;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.DirExtractionUtils;

public class ModuleConfigAutosave {
   private static long lastSaveTimeMs = 0L;
   private static boolean savePending = false;

   public static void saveModuleConfig() {
      if (!DirExtractionUtils.isFlag()) {
         try {
            ModulesJsonStorage.saveModules(getConfigDir(), ModuleRegistry.getModules());
            lastSaveTimeMs = System.currentTimeMillis();
         } catch (Exception e) {
            System.err.println("[C" + e.getMessage());
         }
      }
   }

   private static Path getConfigDir() {
      try {
         return FabricLoader.getInstance().getConfigDir().resolve("corzclient");
      } catch (Throwable e) {
         return Path.of(System.getProperty("user.dir"), "config", "corzclient");
      }
   }

   public static void updateFlag() {
      saveModuleConfig();
      savePending = false;
   }

   public static void loadModuleConfig() {
      ModulesJsonStorage.loadModulesForEnvironment(getConfigDir(), ModuleRegistry.getModules(), ModuleConfigAutosave::updateFlag);
   }

   public static void updateFlag2() {
      savePending = true;
   }

   public static void saveIfPendingAndDue() {
      if (savePending && System.currentTimeMillis() - lastSaveTimeMs > 500L) {
         saveModuleConfig();
         savePending = false;
      }
   }
}
