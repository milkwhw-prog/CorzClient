package com.corz.client.utils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import net.minecraft.network.packet.s2c.play.LightData;
import com.corz.client.utils.SectionStatus;
import com.corz.client.utils.ChunkLightSnapshot;

public final class LightDataClassifier {
   private static final byte[] ZERO_LIGHT_TABLE = new byte[2048];
   private static final byte[] MAX_LIGHT_TABLE = new byte[2048];

   private LightDataClassifier() {
   }

   public static SectionStatus classifyLightNibbles(byte[] zeroedPresentBytes) {
      if (zeroedPresentBytes == null || zeroedPresentBytes.length != 2048) {
         return SectionStatus.UNKNOWN;
      } else if (Arrays.mismatch(zeroedPresentBytes, ZERO_LIGHT_TABLE) < 0) {
         return SectionStatus.ZEROED_PRESENT;
      } else {
         return Arrays.mismatch(zeroedPresentBytes, MAX_LIGHT_TABLE) < 0 ? SectionStatus.UNIFORM_MAX : SectionStatus.NORMAL;
      }
   }

   public static void applyLightData(LightData lightData, ChunkLightSnapshot chunkLightSnapshot, long skyNibblesValue) {
      applyLightMasks(lightData.getInitedSky(), lightData.getUninitedSky(), lightData.getSkyNibbles(), chunkLightSnapshot.blockLightStatuses, chunkLightSnapshot.blockNibblesTable, skyNibblesValue);
      applyLightMasks(lightData.getInitedBlock(), lightData.getUninitedBlock(), lightData.getBlockNibbles(), chunkLightSnapshot.skyLightStatuses, chunkLightSnapshot.skyNibblesTable, skyNibblesValue);
   }

   static {
      Arrays.fill(MAX_LIGHT_TABLE, (byte)-1);
   }

   private static void applyLightMasks(BitSet bitSet, BitSet bitSet2, List<byte[]> list, SectionStatus[] sectionStatus, long[] maskEmptyValues, long maskEmptyValue) {
      int index = 0;
      int maskEmptyLimit = sectionStatus.length;

      for (int i = 0; i < maskEmptyLimit; i++) {
         if (bitSet.get(i)) {
            byte[] maskEmptyBytes = index < list.size() ? (byte[])list.get(index) : null;
            index++;
            sectionStatus[i] = classifyLightNibbles(maskEmptyBytes);
            maskEmptyValues[i] = maskEmptyValue;
         } else if (bitSet2.get(i)) {
            sectionStatus[i] = SectionStatus.EMPTY_MASK;
            maskEmptyValues[i] = maskEmptyValue;
         }
      }
   }
}
