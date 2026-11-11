package com.corz.client.config;

import com.corz.client.gui.DisabledEnabled;
import com.corz.client.world.CompoundNbtUtilsEmpty;
import com.corz.client.world.CompoundNbtUtilsOptional;
import com.corz.client.world.ElseBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

public final class SchematicLoader {
   private volatile String statusText;
   private volatile int loadGeneration;
   private volatile String loadedFileName;
   private static final Path SCHEMATICS_DIR = FabricLoader.getInstance().getGameDir().resolve("corzclient").resolve("schematics");
   private volatile boolean loading;
   private volatile ElseBuilder loadedSchematic;
   private final List<String> folderFileNames = new ArrayList<>();

   public String getLoadedFileName() {
      return this.loadedFileName;
   }

   public void loadSchematicAsync(String loadedSchematicText) {
      if (loadedSchematicText != null && !loadedSchematicText.isBlank()) {
         Path resolve = SCHEMATICS_DIR.resolve(loadedSchematicText);
         if (!Files.exists(resolve)) {
            this.statusText = "File missing: " + loadedSchematicText;
            DisabledEnabled.getInstance().handleDisabledEnabled("Auto Builder", "Schematic not found", false);
            this.refreshFolderList();
         } else {
            this.loading = true;
            this.statusText = "Loading " + loadedSchematicText + "...";
            String lowerCase = loadedSchematicText.toLowerCase();
            int schematicBuilderCount = this.loadGeneration;
            Thread thread = new Thread(
               () -> {
                  try {
                     ElseBuilder var5x = lowerCase.endsWith(".litematic")
                        ? CompoundNbtUtilsEmpty.getCompoundEmpty(resolve)
                        : CompoundNbtUtilsOptional.getPaletteCompound(resolve);
                     if (schematicBuilderCount == this.loadGeneration) {
                        this.loadedSchematic = var5x;
                        this.loadedFileName = loadedSchematicText;
                        this.statusText = var5x.orElse + " (" + var5x.builderAutoValue + " blocks)";
                        DisabledEnabled.getInstance()
                           .handleDisabledEnabled("Auto Builder", "Loaded " + var5x.orElse + " - " + var5x.builderAutoValue + " blocks", true);
                        if (!var5x.builderAutoItems.isEmpty()) {
                           DisabledEnabled.getInstance().handleDisabledEnabled("Auto Builder", var5x.builderAutoItems.size() + " unsupported block(s)", false);
                        }

                        return;
                     }

                     return;
                  } catch (Exception e) {
                     if (schematicBuilderCount == this.loadGeneration) {
                        this.statusText = "Failed to load " + loadedSchematicText;
                        DisabledEnabled.getInstance().handleDisabledEnabled("Auto Builder", "Failed to parse schematic", false);
                        return;
                     }
                  } finally {
                     this.loading = false;
                  }
               },
               "Corz-SchematicLoad"
            );
            thread.setDaemon(true);
            thread.start();
         }
      }
   }

   public SchematicLoader() {
      this.loadedFileName = "";
      this.statusText = "No schematic loaded";
      this.loading = false;
      ensureSchematicsDir();
      this.refreshFolderList();
   }

   public List<String> getFolderFileNames() {
      return this.folderFileNames;
   }

   public void loadNextSchematic() {
      this.loadSchematicByOffset(1);
   }

   public synchronized void refreshFolderList() {
      ensureSchematicsDir();
      this.folderFileNames.clear();

      try (Stream<java.nio.file.Path> stream = Files.list(SCHEMATICS_DIR)) {
         stream.filter(path -> {
            String var1x = ((java.nio.file.Path) path).getFileName().toString().toLowerCase();
            return var1x.endsWith(".litematic") || var1x.endsWith(".schem") || var1x.endsWith(".schematic");
         }).map(path2 -> ((java.nio.file.Path) path2).getFileName().toString()).sorted(Comparator.naturalOrder()).forEach(this.folderFileNames::add);
      } catch (IOException e) {
      }

      if (!this.folderFileNames.contains(this.loadedFileName)) {
         this.loadedFileName = "";
      }
   }

   public ElseBuilder getLoadedSchematic() {
      return this.loadedSchematic;
   }

   public String getStatusText() {
      return this.statusText;
   }

   private void loadSchematicByOffset(int folderSchematicsValue) {
      if (this.folderFileNames.isEmpty()) {
         this.statusText = "No schematics in folder";
      } else {
         int indexOf = this.folderFileNames.indexOf(this.loadedFileName);
         int index = (indexOf + folderSchematicsValue + this.folderFileNames.size() * 2) % this.folderFileNames.size();
         this.loadSchematicAsync(this.folderFileNames.get(index));
      }
   }

   private static void ensureSchematicsDir() {
      try {
         Files.createDirectories(SCHEMATICS_DIR);
      } catch (IOException e) {
      }
   }

   public void clearLoadedSchematic() {
      this.loadGeneration++;
      this.loadedSchematic = null;
      this.loadedFileName = "";
      this.statusText = "No schematic loaded";
   }

   public static Path getSchematicsDir() {
      return SCHEMATICS_DIR;
   }

   public void loadPreviousSchematic() {
      this.loadSchematicByOffset(-1);
   }

   public void openSchematicsFolder() {
      ensureSchematicsDir();

      try {
         Util.getOperatingSystem().open(SCHEMATICS_DIR.toUri());
      } catch (Exception e) {
         DisabledEnabled.getInstance().handleDisabledEnabled("Auto Builder", "Couldn't open folder", false);
      }
   }
}
