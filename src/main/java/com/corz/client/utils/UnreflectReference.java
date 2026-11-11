package com.corz.client.utils;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

public final class UnreflectReference {
   private static volatile boolean unreflectIndicesActive;
   private static final MethodHandle unreflect;
   private final Reference2IntOpenHashMap<BlockState> reference2IntOpenHashMap;
   public BlockState[] copyOf;
   public int paletteSize;
   private static final MethodHandle unreflectReferenceMethodHand;
   public final int[] unreflectReferenceTable = new int[4096];
   private static final MethodHandle METHOD_HANDLE;

   private void handleReferenceHash(ChunkSection chunkSection) {
      this.reference2IntOpenHashMap.clear();
      int size = 0;
      BlockState blockState = null;
      int index = 0;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               BlockState blockState2 = chunkSection.getBlockState(k, i, j);
               if (blockState2 != blockState) {
                  blockState = blockState2;
                  index = this.reference2IntOpenHashMap.getInt(blockState2);
                  if (index < 0) {
                     index = size++;
                     this.reference2IntOpenHashMap.put(blockState2, index);
                     if (this.copyOf.length < size) {
                        this.copyOf = Arrays.copyOf(this.copyOf, this.copyOf.length * 2);
                     }

                     this.copyOf[index] = blockState2;
                  }
               }

               this.unreflectReferenceTable[getValue(k, i, j)] = index;
            }
         }
      }

      this.paletteSize = size;
   }

   private boolean canUnreflectIndices(ChunkSection chunkSection) {
      try {
         Object invoke = (Object)METHOD_HANDLE.invoke((PalettedContainer)chunkSection.getBlockStateContainer());
         PaletteStorage paletteStorage = (PaletteStorage)unreflectReferenceMethodHand.invoke((Object)invoke);
         Palette palette = (Palette)unreflect.invoke((Object)invoke);
         int size = palette.getSize();
         if (size > 0 && size <= 1024 && paletteStorage.getSize() == 4096) {
            if (this.copyOf.length < size) {
               this.copyOf = new BlockState[Math.max(size, this.copyOf.length * 2)];
            }

            for (int i = 0; i < size; i++) {
               this.copyOf[i] = (BlockState)palette.get(i);
            }

            this.paletteSize = size;
            paletteStorage.writePaletteIndices(this.unreflectReferenceTable);
            if (this.canStateBlock(chunkSection, 0, 0, 0) && this.canStateBlock(chunkSection, 5, 9, 13) && this.canStateBlock(chunkSection, 15, 15, 15)) {
               return true;
            } else {
               unreflectIndicesActive = false;
               return false;
            }
         } else {
            return false;
         }
      } catch (Throwable e) {
         return false;
      }
   }

   private boolean canStateBlock(ChunkSection chunkSection, int blockValue, int k, int p) {
      int index = this.unreflectReferenceTable[getValue(blockValue, k, p)];
      return index >= 0 && index < this.paletteSize && this.copyOf[index] == chunkSection.getBlockState(blockValue, k, p);
   }

   public static boolean isFlag() {
      return unreflectIndicesActive;
   }

   public UnreflectReference() {
      this.copyOf = new BlockState[64];
      this.reference2IntOpenHashMap = new Reference2IntOpenHashMap();
      this.reference2IntOpenHashMap.defaultReturnValue(-1);
   }

   public static int getValue(int unreflectReferenceValue, int k, int p) {
      return k << 8 | p << 4 | unreflectReferenceValue;
   }

   public void handleFlag(ChunkSection chunkSection) {
      if (!unreflectIndicesActive || !this.canUnreflectIndices(chunkSection)) {
         this.handleReferenceHash(chunkSection);
      }
   }

   static {
      MethodHandle METHOD_HANDLE2 = null;
      MethodHandle unreflect3 = null;
      MethodHandle unreflect4 = null;

      try {
         Lookup lookup = MethodHandles.lookup();

         for (Field field : PalettedContainer.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getType().isRecord()) {
               MethodHandle methodHandle = null;
               MethodHandle methodHandle2 = null;

               for (RecordComponent recordComponent : field.getType().getRecordComponents()) {
                  if (PaletteStorage.class.isAssignableFrom(recordComponent.getType())) {
                     recordComponent.getAccessor().setAccessible(true);
                     methodHandle = lookup.unreflect(recordComponent.getAccessor());
                  } else if (Palette.class.isAssignableFrom(recordComponent.getType())) {
                     recordComponent.getAccessor().setAccessible(true);
                     methodHandle2 = lookup.unreflect(recordComponent.getAccessor());
                  }
               }

               if (methodHandle != null && methodHandle2 != null) {
                  field.setAccessible(true);
                  METHOD_HANDLE2 = lookup.unreflectGetter(field);
                  unreflect3 = methodHandle;
                  unreflect4 = methodHandle2;
                  break;
               }
            }
         }
      } catch (Throwable e) {
         METHOD_HANDLE2 = null;
      }

      METHOD_HANDLE = METHOD_HANDLE2;
      unreflectReferenceMethodHand = unreflect3;
      unreflect = unreflect4;
      unreflectIndicesActive = METHOD_HANDLE2 != null && unreflect3 != null && unreflect4 != null;
   }
}
