package com.corz.client.world;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import com.corz.client.utils.DisabledMaskData;

public final class CompoundNbtUtilsOptional {
   private CompoundNbtUtilsOptional() {
   }

   public static ElseBuilder getPaletteCompound(Path path)  throws java.io.IOException {
      NbtCompound compressed = NbtIo.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
      NbtCompound nbtCompound = compressed.contains("Schematic") ? compressed.getCompoundOrEmpty("Schematic") : compressed;
      int short2 = getShort(nbtCompound, "Width");
      int short3 = getShort(nbtCompound, "Height");
      int short4 = getShort(nbtCompound, "Length");
      if (short2 > 0 && short3 > 0 && short4 > 0) {
         NbtCompound compoundOrEmpty = nbtCompound.getCompoundOrEmpty("Blocks");
         NbtCompound nbtCompound2 = compoundOrEmpty.contains("Palette") ? compoundOrEmpty.getCompoundOrEmpty("Palette") : nbtCompound.getCompoundOrEmpty("Palette");
         byte[] paletteCompoundBytes = compoundOrEmpty.contains("Data") ? compoundOrEmpty.getByteArray("Data").orElse(new byte[0]) : nbtCompound.getByteArray("BlockData").orElse(new byte[0]);
         ArrayList arrayList = new ArrayList();
         int paletteCompoundBound = -1;

         for (String paletteCompoundText : nbtCompound2.getKeys()) {
            paletteCompoundBound = Math.max(paletteCompoundBound, nbtCompound2.getInt(paletteCompoundText).orElse(0));
         }

         BlockState[] blockStates = new BlockState[paletteCompoundBound + 1];

         for (String getKeysItem : nbtCompound2.getKeys()) {
            int orElse = nbtCompound2.getInt(getKeysItem).orElse(0);
            if (orElse >= 0 && orElse < blockStates.length) {
               String moneyEnoughText = getKeysItem.contains("[") ? getKeysItem.substring(0, getKeysItem.indexOf(91)) : getKeysItem;
               Identifier tryParse = Identifier.tryParse(moneyEnoughText);
               Optional optional = tryParse == null ? Optional.empty() : Registries.BLOCK.getOptionalValue(tryParse);
               if (optional.isPresent()) {
                  blockStates[orElse] = UnparseableSuchUtils.getPropertyUnparseable((Block)optional.get(), getKeysItem, (prop, blk, reason, val) -> {});
               } else {
                  blockStates[orElse] = null;
                  if (!"minecraft:air".equals(moneyEnoughText) && !arrayList.contains(moneyEnoughText)) {
                     arrayList.add(moneyEnoughText);
                  }
               }
            }
         }

         int paletteCompoundSize = short2 * short3 * short4;
         int[] ints = new int[paletteCompoundSize];
         int paletteCompoundCounter = 0;
         int paletteCompoundCounter2 = 0;

         while (paletteCompoundCounter2 < paletteCompoundSize && paletteCompoundCounter < paletteCompoundBytes.length) {
            int paletteCompoundValue = 0;
            int paletteCompoundTotal = 0;

            while (true) {
               if (paletteCompoundCounter < paletteCompoundBytes.length) {
                  int paletteCompoundValue2 = paletteCompoundBytes[paletteCompoundCounter++];
                  paletteCompoundValue |= (paletteCompoundValue2 & 127) << paletteCompoundTotal;
                  if ((paletteCompoundValue2 & 128) != 0) {
                     paletteCompoundTotal += 7;
                     if (paletteCompoundTotal <= 35) {
                        continue;
                     }
                  }
               }

               ints[paletteCompoundCounter2++] = paletteCompoundValue;
               break;
            }
         }

         LinkedHashMap linkedHashMap = new LinkedHashMap();
         int paletteCompoundCounter3 = 0;

         for (int i = 0; i < paletteCompoundSize; i++) {
            int index = ints[i];
            BlockState blockState = index >= 0 && index < blockStates.length ? blockStates[index] : null;
            if (blockState != null && !blockState.isAir()) {
               linkedHashMap.merge(blockState.getBlock(), 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
               paletteCompoundCounter3++;
            }
         }

         HashMap hashMap = new HashMap();
         NbtList nbtList = compoundOrEmpty.contains("BlockEntities") ? compoundOrEmpty.getListOrEmpty("BlockEntities") : nbtCompound.getListOrEmpty("BlockEntities");

         for (int j = 0; j < nbtList.size(); j++) {
            NbtCompound orElse2 = nbtList.getCompound(j).orElse(null);
            if (orElse2 != null) {
               int[] orElse3 = orElse2.getIntArray("Pos").orElse(null);
               if (orElse3 != null && orElse3.length >= 3) {
                  NbtCompound nbtCompound3 = orElse2.contains("Data") ? orElse2.getCompoundOrEmpty("Data") : orElse2;
                  int[] disabledSlots = nbtCompound3.getIntArray("disabled_slots").orElse(null);
                  if (disabledSlots != null) {
                     int paletteCompoundLimit = orElse3[0];
                     int paletteCompoundLimit2 = orElse3[1];
                     int paletteCompoundLimit3 = orElse3[2];
                     if (paletteCompoundLimit >= 0 && paletteCompoundLimit2 >= 0 && paletteCompoundLimit3 >= 0 && paletteCompoundLimit < short2 && paletteCompoundLimit2 < short3 && paletteCompoundLimit3 < short4) {
                        hashMap.put((paletteCompoundLimit2 * short4 + paletteCompoundLimit3) * short2 + paletteCompoundLimit, DisabledMaskData.getValue(disabledSlots));
                     }
                  }
               }
            }
         }

         ArrayList arrayList2 = new ArrayList();
         arrayList2.add(new ElseBuilder.Region(0, 0, 0, short2, short3, short4, blockStates, ints, hashMap));
         String replaceFirst = path.getFileName().toString().replaceFirst("\\.(schem|schematic)$", "");
         return new ElseBuilder(replaceFirst, short2, short3, short4, arrayList2, linkedHashMap, paletteCompoundCounter3, arrayList);
      } else {
         throw new IllegalStateException("Invalid schem dimensions");
      }
   }

   private static int getShort(NbtCompound nbtCompound, String shortText) {
      return nbtCompound.getShort(shortText).map(Short::intValue).or(() -> nbtCompound.getInt(shortText)).orElse(0);
   }
}
