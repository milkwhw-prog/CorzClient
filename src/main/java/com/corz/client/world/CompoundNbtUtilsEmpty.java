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

public final class CompoundNbtUtilsEmpty {
   private static int getValue(long[] compoundNbtValues, int compoundNbtLimit, long compoundNbtFactor) {
      if (compoundNbtLimit > 0 && compoundNbtValues.length != 0) {
         long compoundNbtValue = compoundNbtFactor * compoundNbtLimit;
         int index2 = (int)(compoundNbtValue >> 6);
         int index3 = (int)((compoundNbtFactor + 1L) * compoundNbtLimit - 1L >> 6);
         int compoundNbtValue2 = (int)(compoundNbtValue & 63L);
         long compoundNbtValue3 = compoundNbtLimit >= 64 ? -1L : (1L << compoundNbtLimit) - 1L;
         if (index2 < 0 || index2 >= compoundNbtValues.length) {
            return 0;
         } else if (index2 == index3) {
            return (int)(compoundNbtValues[index2] >>> compoundNbtValue2 & compoundNbtValue3);
         } else if (index3 >= compoundNbtValues.length) {
            return (int)(compoundNbtValues[index2] >>> compoundNbtValue2 & compoundNbtValue3);
         } else {
            int compoundNbtValue4 = 64 - compoundNbtValue2;
            return (int)((compoundNbtValues[index2] >>> compoundNbtValue2 | compoundNbtValues[index3] << compoundNbtValue4) & compoundNbtValue3);
         }
      } else {
         return 0;
      }
   }

   public static ElseBuilder getCompoundEmpty(Path path)  throws java.io.IOException {
      NbtCompound compressed = NbtIo.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
      NbtCompound compoundOrEmpty = compressed.getCompoundOrEmpty("Metadata");
      String replaceFirst = path.getFileName().toString().replaceFirst("\\.litematic$", "");
      String orElse = compoundOrEmpty.getString("Name").filter(compoundEmptyText -> !compoundEmptyText.isBlank()).orElse(replaceFirst);
      NbtCompound compoundOrEmpty2 = compoundOrEmpty.getCompoundOrEmpty("EnclosingSize");
      int absOrElse = Math.abs(compoundOrEmpty2.getInt("x").orElse(0));
      int abs2 = Math.abs(compoundOrEmpty2.getInt("y").orElse(0));
      int abs3 = Math.abs(compoundOrEmpty2.getInt("z").orElse(0));
      ArrayList arrayList = new ArrayList();
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      ArrayList arrayList2 = new ArrayList();
      int compoundEmptyCounter = 0;
      NbtCompound compoundOrEmpty3 = compressed.getCompoundOrEmpty("Regions");

      for (String getKeysItem : compoundOrEmpty3.getKeys()) {
         NbtCompound compoundOrEmpty4 = compoundOrEmpty3.getCompoundOrEmpty(getKeysItem);
         NbtCompound compoundOrEmpty5 = compoundOrEmpty4.getCompoundOrEmpty("Size");
         int orElse2 = compoundOrEmpty5.getInt("x").orElse(0);
         int orElse3 = compoundOrEmpty5.getInt("y").orElse(0);
         int orElse4 = compoundOrEmpty5.getInt("z").orElse(0);
         int compoundEmptyFactor = Math.abs(orElse2);
         int compoundEmptyLimit = Math.abs(orElse3);
         int compoundEmptyFactor2 = Math.abs(orElse4);
         if (compoundEmptyFactor != 0 && compoundEmptyLimit != 0 && compoundEmptyFactor2 != 0) {
            NbtCompound compoundOrEmpty6 = compoundOrEmpty4.getCompoundOrEmpty("Position");
            int orElse5 = compoundOrEmpty6.getInt("x").orElse(0);
            int orElse6 = compoundOrEmpty6.getInt("y").orElse(0);
            int orElse7 = compoundOrEmpty6.getInt("z").orElse(0);
            NbtList listOrEmpty = compoundOrEmpty4.getListOrEmpty("BlockStatePalette");
            BlockState[] blockStates = new BlockState[listOrEmpty.size()];

            for (int i = 0; i < listOrEmpty.size(); i++) {
               NbtCompound orElse8 = listOrEmpty.getCompound(i).orElse(new NbtCompound());
               String moneyEnoughText = orElse8.getString("Name").orElse("minecraft:air");
               Identifier tryParse = Identifier.tryParse(moneyEnoughText);
               Optional optional = tryParse == null ? Optional.empty() : Registries.BLOCK.getOptionalValue(tryParse);
               if (optional.isPresent()) {
                  NbtCompound nbtCompound = orElse8.getCompoundOrEmpty("Properties");
                  blockStates[i] = UnparseableSuchUtils.getPropertyUnparseable((Block)optional.get(), nbtCompound, (reason, val, blk, prop) -> {});
               } else {
                  blockStates[i] = null;
                  if (!"minecraft:air".equals(moneyEnoughText) && !arrayList2.contains(moneyEnoughText)) {
                     arrayList2.add(moneyEnoughText);
                  }
               }
            }

            long[] compoundNbtValues = compoundOrEmpty4.getLongArray("BlockStates").orElse(new long[0]);
            int compoundEmptyValue = Math.max(2, 32 - Integer.numberOfLeadingZeros(Math.max(1, blockStates.length - 1)));
            int compoundEmptySize = compoundEmptyFactor * compoundEmptyLimit * compoundEmptyFactor2;
            int[] ints = new int[compoundEmptySize];

            for (int j = 0; j < compoundEmptySize; j++) {
               ints[j] = getValue(compoundNbtValues, compoundEmptyValue, j);
            }

            HashMap hashMap = new HashMap();
            NbtList listOrEmpty2 = compoundOrEmpty4.getListOrEmpty("TileEntities");

            for (int k = 0; k < listOrEmpty2.size(); k++) {
               NbtCompound orElse11 = listOrEmpty2.getCompound(k).orElse(null);
               if (orElse11 != null) {
                  int orElse12 = orElse11.getInt("x").orElse(-1);
                  int orElse13 = orElse11.getInt("y").orElse(-1);
                  int orElse14 = orElse11.getInt("z").orElse(-1);
                  if (orElse12 >= 0 && orElse13 >= 0 && orElse14 >= 0 && orElse12 < compoundEmptyFactor && orElse13 < compoundEmptyLimit && orElse14 < compoundEmptyFactor2) {
                     int[] disabledSlots = orElse11.getIntArray("disabled_slots").orElse(null);
                     if (disabledSlots != null) {
                        hashMap.put((orElse13 * compoundEmptyFactor2 + orElse14) * compoundEmptyFactor + orElse12, DisabledMaskData.getValue(disabledSlots));
                     }
                  }
               }
            }

            ElseBuilder.Region region = new ElseBuilder.Region(orElse5, orElse6, orElse7, orElse2, orElse3, orElse4, blockStates, ints, hashMap);
            arrayList.add(region);

            for (int m = 0; m < compoundEmptyLimit; m++) {
               for (int p = 0; p < compoundEmptyFactor2; p++) {
                  for (int q = 0; q < compoundEmptyFactor; q++) {
                     BlockState statesBlock = region.getStatesBlock(q, m, p);
                     if (statesBlock != null && !statesBlock.isAir()) {
                        linkedHashMap.merge(statesBlock.getBlock(), 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
                        compoundEmptyCounter++;
                     }
                  }
               }
            }
         }
      }

      return new ElseBuilder(orElse, absOrElse, abs2, abs3, arrayList, linkedHashMap, compoundEmptyCounter, arrayList2);
   }

   private CompoundNbtUtilsEmpty() {
   }
}
