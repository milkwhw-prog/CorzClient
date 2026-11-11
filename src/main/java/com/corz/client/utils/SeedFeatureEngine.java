package com.corz.client.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.AbstractCountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightmapPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public final class SeedFeatureEngine {
   private Registry<PlacedFeature> placedFeatureRegistry;
   private Map<RegistryKey<PlacedFeature>, int[]> featureIndexMap;
   private DynamicRegistryManager dynamicRegistryManager;

   private boolean containsFeature(List<RegistryEntry<PlacedFeature>> list2, RegistryEntry<PlacedFeature> registryEntry) {
      Optional optional = registryEntry.getKey();
      if (optional.isEmpty()) {
         return list2.contains(registryEntry);
      } else {
         for (RegistryEntry registryEntry2 : list2) {
            if (((boolean) (Boolean) (registryEntry2.getKey().map(key -> key == optional.get()).orElse(false)))) {
               return true;
            }
         }

         return false;
      }
   }

   private Type normalizeHeightmapType(Type worldSurface) {
      if (worldSurface == null) {
         return Type.WORLD_SURFACE;
      } else {
         switch (worldSurface) {
            case WORLD_SURFACE_WG:
               return Type.WORLD_SURFACE;
            case OCEAN_FLOOR_WG:
               return Type.OCEAN_FLOOR;
            default:
               return worldSurface;
         }
      }
   }

   private List<PlacementModifier> getPlacementModifiers(PlacedFeature placedFeature) {
      try {
         for (Field field : placedFeature.getClass().getDeclaredFields()) {
            if (List.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               Object obj = field.get(placedFeature);
               if (obj instanceof List list && (list.isEmpty() || list.get(0) instanceof PlacementModifier)) {
                  return (List<PlacementModifier>)obj;
               }
            }
         }
      } catch (Throwable e) {
      }

      return null;
   }

   private void buildFeatureIndex(DynamicRegistryManager dynamicRegistryManager) {
      if (dynamicRegistryManager != this.dynamicRegistryManager || this.featureIndexMap == null) {
         this.dynamicRegistryManager = dynamicRegistryManager;
         this.featureIndexMap = new HashMap<>();

         try {
            this.placedFeatureRegistry = dynamicRegistryManager.getOrThrow(RegistryKeys.PLACED_FEATURE);
            Registry orThrow = dynamicRegistryManager.getOrThrow(RegistryKeys.BIOME);
            ArrayList arrayList = new ArrayList();

            for (Biome biome : (Iterable<net.minecraft.world.biome.Biome>) (orThrow)) {
               List features = biome.getGenerationSettings().getFeatures();

               for (int i = 0; i < features.size(); i++) {
                  while (arrayList.size() <= i) {
                     arrayList.add(new ArrayList());
                  }

                  List list = (List)arrayList.get(i);
                  Object obj = features.get(i);

                  for (RegistryEntry registryEntry : this.toEntryList(obj)) {
                     if (!this.containsFeature(list, registryEntry)) {
                        list.add(registryEntry);
                     }
                  }
               }
            }

            for (int j = 0; j < arrayList.size(); j++) {
               List list2 = (List)arrayList.get(j);

               for (int m = 0; m < list2.size(); m++) {
                  int throwDynamicValue = j;
                  int throwDynamicValue2 = m;
                  ((RegistryEntry)list2.get(m)).getKey().ifPresent(obj2 -> this.featureIndexMap.putIfAbsent((RegistryKey<PlacedFeature>)obj2, new int[]{throwDynamicValue, throwDynamicValue2}));
               }
            }
         } catch (Throwable e) {
         }
      }
   }

   public static Long parseSeed(String hashCodeText) {
      if (hashCodeText == null) {
         return null;
      } else {
         String trim = hashCodeText.trim();
         if (trim.isEmpty()) {
            return null;
         } else {
            try {
               return Long.parseLong(trim);
            } catch (NumberFormatException e) {
               return (long)trim.hashCode();
            }
         }
      }
   }

   private Iterable<RegistryEntry<PlacedFeature>> toEntryList(Object obj) {
      if (obj instanceof Iterable iterable) {
         ArrayList arrayList = new ArrayList();

         for (Object iterableItem : iterable) {
            if (iterableItem instanceof RegistryEntry registryEntry) {
               arrayList.add(registryEntry);
            }
         }

         return arrayList;
      } else {
         return List.of();
      }
   }

   private int getCountFromModifier(PlacementModifier placementModifier, Random random) {
      try {
         for (Field field : placementModifier.getClass().getDeclaredFields()) {
            if (IntProvider.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               if (field.get(placementModifier) instanceof IntProvider intProvider) {
                  return Math.max(0, intProvider.get(random));
               }
            }
         }

         int int2 = this.getFirstIntField(placementModifier);
         if (int2 > 0) {
            return int2;
         }
      } catch (Throwable e) {
      }

      return 1;
   }

   private void applyPlacementModifier(PlacementModifier placementModifier, Random random, BlockPos pos2, WorldView worldView, List<BlockPos> list) {
      if (!(placementModifier instanceof AbstractCountPlacementModifier)) {
         if (placementModifier instanceof RarityFilterPlacementModifier) {
            int int2 = this.getFirstIntField(placementModifier);
            if (int2 > 0 && random.nextFloat() < 1.0F / int2) {
               list.add(pos2);
            }
         } else if (placementModifier instanceof SquarePlacementModifier) {
            int x = pos2.getX() + random.nextInt(16);
            int z = pos2.getZ() + random.nextInt(16);
            list.add(new BlockPos(x, pos2.getY(), z));
         } else if (placementModifier instanceof HeightmapPlacementModifier) {
            Type worldSurface = this.normalizeHeightmapType(this.getHeightmapType(placementModifier));
            Chunk chunk = worldView.getChunk(pos2.getX() >> 4, pos2.getZ() >> 4);
            int sampleHeightmap = chunk.sampleHeightmap(worldSurface, pos2.getX(), pos2.getZ());
            list.add(new BlockPos(pos2.getX(), sampleHeightmap, pos2.getZ()));
         } else if (placementModifier instanceof HeightRangePlacementModifier) {
            int bottomY = worldView.getBottomY();
            int maxHeight = Math.max(1, worldView.getHeight());
            list.add(new BlockPos(pos2.getX(), bottomY + random.nextInt(maxHeight), pos2.getZ()));
         } else {
            list.add(pos2);
         }
      } else {
         int heightmapSampleLimit = this.getCountFromModifier(placementModifier, random);

         for (int i = 0; i < heightmapSampleLimit; i++) {
            list.add(pos2);
         }
      }
   }

   private int getFirstIntField(PlacementModifier placementModifier) {
      try {
         for (Field field : placementModifier.getClass().getDeclaredFields()) {
            if (field.getType() == int.class) {
               field.setAccessible(true);
               return field.getInt(placementModifier);
            }
         }
      } catch (Throwable e) {
      }

      return 0;
   }

   public List<BlockPos> computeFeaturePositions(
      WorldView worldView, DynamicRegistryManager dynamicRegistryManager, long seedDecoratorValue, int seedDecoratorValue2, int seedDecoratorValue3, RegistryKey<PlacedFeature> registryKey
   ) {
      ArrayList arrayList = new ArrayList();

      try {
         this.buildFeatureIndex(dynamicRegistryManager);
         if (this.placedFeatureRegistry == null) {
            return arrayList;
         }

         int[] seedDecoratorValues = this.featureIndexMap.get(registryKey);
         if (seedDecoratorValues == null) {
            return arrayList;
         }

         PlacedFeature orthrow = this.placedFeatureRegistry.get(registryKey);
         if (orthrow == null) {
            return arrayList;
         }

         List list = this.getPlacementModifiers(orthrow);
         if (list == null) {
            return arrayList;
         }

         int x = seedDecoratorValue2 << 4;
         int z = seedDecoratorValue3 << 4;
         ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
         long setPopulationSeed = chunkRandom.setPopulationSeed(seedDecoratorValue, x, z);
         chunkRandom.setDecoratorSeed(setPopulationSeed, seedDecoratorValues[1], seedDecoratorValues[0]);
         ArrayList supplier = new ArrayList();
         supplier.add(new BlockPos(x, 0, z));

         for (PlacementModifier placementModifier : (Iterable<net.minecraft.world.gen.placementmodifier.PlacementModifier>) (list)) {
            ArrayList arrayList3 = new ArrayList();

            for (BlockPos blockPos : (Iterable<net.minecraft.util.math.BlockPos>) (supplier)) {
               this.applyPlacementModifier(placementModifier, chunkRandom, blockPos, worldView, arrayList3);
            }

            supplier = arrayList3;
            if (arrayList3.isEmpty()) {
               break;
            }
         }

         arrayList.addAll(supplier);
      } catch (Throwable e) {
      }

      return arrayList;
   }

   private Type getHeightmapType(PlacementModifier placementModifier) {
      try {
         for (Field field : placementModifier.getClass().getDeclaredFields()) {
            if (field.getType() == Type.class) {
               field.setAccessible(true);
               return (Type)field.get(placementModifier);
            }
         }
      } catch (Throwable e) {
      }

      return Type.WORLD_SURFACE;
   }
}
